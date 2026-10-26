package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.AutoPlanterBlock;
import com.refinery.menu.AutoPlanterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

public class AutoPlanterBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 9;
    public static final int ENERGY_CAPACITY = 10_000;
    public static final int ENERGY_PER_TICK = 1;
    public static final int ENERGY_PER_ACTION = 10;
    public static final int RADIUS = 5;
    public static final int DIAMETER = 11; // -5 to +5
    public static final int Y_LEVELS = 4; // dy: -1, 0, 1, 2
    public static final int TOTAL_POSITIONS = DIAMETER * DIAMETER * Y_LEVELS; // 484

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int energy = 0;
    private int scanIndex = 0;
    private int actionCooldown = 0;
    private boolean working = false;

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 500, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            AutoPlanterBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            AutoPlanterBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AutoPlanterBlockEntity.this.energy & 0xFFFF;
                case 1 -> (AutoPlanterBlockEntity.this.energy >> 16) & 0xFFFF;
                case 2 -> AutoPlanterBlockEntity.this.working ? 1 : 0;
                case 3 -> AutoPlanterBlockEntity.this.getTotalSeedsCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AutoPlanterBlockEntity.this.energy = (AutoPlanterBlockEntity.this.energy & 0xFFFF0000) | (value & 0xFFFF);
                case 1 -> AutoPlanterBlockEntity.this.energy = (AutoPlanterBlockEntity.this.energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 2 -> AutoPlanterBlockEntity.this.working = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AutoPlanterBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.AUTO_PLANTER_BE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Check power availability
        if (this.energy < ENERGY_PER_TICK) {
            if (this.working) {
                setWorking(serverLevel, pos, false);
            }
            return;
        }

        // Idle power consumption
        this.energy -= ENERGY_PER_TICK;
        this.energyStorage.set(this.energy);

        if (this.actionCooldown > 0) {
            this.actionCooldown--;
            return;
        }

        boolean performedAction = false;
        int checks = 0;

        // Scan up to 16 positions per tick
        while (checks < 16) {
            checks++;
            this.scanIndex = (this.scanIndex + 1) % TOTAL_POSITIONS;

            int col = this.scanIndex / Y_LEVELS;
            int dyIdx = this.scanIndex % Y_LEVELS;
            int dx = (col % DIAMETER) - RADIUS;
            int dz = (col / DIAMETER) - RADIUS;
            int dy = dyIdx - 1; // -1, 0, 1, 2

            BlockPos targetPos = pos.offset(dx, dy, dz);
            BlockPos abovePos = targetPos.above();

            BlockState targetState = level.getBlockState(targetPos);
            BlockState aboveState = level.getBlockState(abovePos);

            boolean canPlantAbove = aboveState.isAir() || aboveState.canBeReplaced()
                    || aboveState.is(Blocks.SHORT_GRASS) || aboveState.is(Blocks.FERN);

            if (canPlantAbove) {
                for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                    ItemStack stack = this.items.get(slot);
                    if (stack.isEmpty() || !isPlantable(stack)) continue;

                    if (isSapling(stack)) {
                        // Saplings plant on Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss, etc.
                        boolean isSoil = targetState.is(BlockTags.DIRT) || targetState.is(Blocks.FARMLAND);
                        if (!isSoil) continue;

                        BlockState saplingState = getPlantableCrop(stack, level, abovePos);
                        if (saplingState == null) continue;

                        // If farmland, revert back to normal dirt for tree sapling
                        if (targetState.is(Blocks.FARMLAND)) {
                            level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                        }

                        if (!aboveState.isAir()) {
                            level.destroyBlock(abovePos, false);
                        }

                        stack.shrink(1);
                        level.setBlock(abovePos, saplingState, 3);
                        level.playSound(null, abovePos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.8f, 1.0f);
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                abovePos.getX() + 0.5, abovePos.getY() + 0.2, abovePos.getZ() + 0.5,
                                3, 0.2, 0.1, 0.2, 0.05);

                        if (this.energy >= ENERGY_PER_ACTION) {
                            this.energy -= ENERGY_PER_ACTION;
                            this.energyStorage.set(this.energy);
                        }

                        performedAction = true;
                        setChanged();
                        break;
                    } else {
                        // Standard crop seed
                        boolean isDirt = targetState.is(Blocks.DIRT) || targetState.is(Blocks.GRASS_BLOCK)
                                || targetState.is(Blocks.DIRT_PATH) || targetState.is(Blocks.ROOTED_DIRT)
                                || (targetState.is(BlockTags.DIRT) && !targetState.is(Blocks.FARMLAND) && !targetState.is(Blocks.COARSE_DIRT));
                        boolean isFarmland = targetState.is(Blocks.FARMLAND);

                        if (!isDirt && !isFarmland) continue;

                        if (isDirt) {
                            if (!aboveState.isAir()) {
                                level.destroyBlock(abovePos, false);
                            }
                            level.setBlock(targetPos, Blocks.FARMLAND.defaultBlockState(), 3);
                            level.playSound(null, targetPos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 0.8f, 1.0f);
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5,
                                    3, 0.2, 0.1, 0.2, 0.05);
                        }

                        BlockState cropState = getPlantableCrop(stack, level, abovePos);
                        if (cropState != null) {
                            if (!aboveState.isAir()) {
                                level.destroyBlock(abovePos, false);
                            }
                            stack.shrink(1);
                            level.setBlock(abovePos, cropState, 3);
                            level.playSound(null, abovePos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.8f, 1.0f);
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    abovePos.getX() + 0.5, abovePos.getY() + 0.2, abovePos.getZ() + 0.5,
                                    3, 0.2, 0.1, 0.2, 0.05);

                            if (this.energy >= ENERGY_PER_ACTION) {
                                this.energy -= ENERGY_PER_ACTION;
                                this.energyStorage.set(this.energy);
                            }

                            performedAction = true;
                            setChanged();
                            break;
                        }
                    }
                }
                if (performedAction) break;
            }
        }

        if (performedAction) {
            this.actionCooldown = 4; // ~5 operations per second
            setWorking(serverLevel, pos, true);
            setChanged();
        } else if (this.working && this.actionCooldown == 0) {
            setWorking(serverLevel, pos, false);
        }
    }

    private void setWorking(ServerLevel level, BlockPos pos, boolean isWorking) {
        this.working = isWorking;
        BlockState current = getBlockState();
        if (current.getBlock() instanceof AutoPlanterBlock && current.getValue(AutoPlanterBlock.WORKING) != isWorking) {
            level.setBlock(pos, current.setValue(AutoPlanterBlock.WORKING, isWorking), 3);
        }
    }

    public static boolean isSapling(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(ItemTags.SAPLINGS)) return true;
        Item item = stack.getItem();
        if (item == Items.MANGROVE_PROPAGULE) return true;
        if (item instanceof BlockItem bi) {
            Block block = bi.getBlock();
            return block instanceof SaplingBlock || block.defaultBlockState().is(BlockTags.SAPLINGS);
        }
        return false;
    }

    public static boolean isPlantable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (isSapling(stack)) return true;
        Item item = stack.getItem();
        if (item == Items.WHEAT_SEEDS || item == Items.CARROT || item == Items.POTATO
                || item == Items.BEETROOT_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS
                || item == Items.TORCHFLOWER_SEEDS || item == Items.PITCHER_POD) {
            return true;
        }
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return block instanceof CropBlock || block instanceof StemBlock;
        }
        return false;
    }

    public static @Nullable BlockState getPlantableCrop(ItemStack stack, Level level, BlockPos plantPos) {
        if (stack.isEmpty()) return null;
        Item item = stack.getItem();
        if (item == Items.WHEAT_SEEDS) return Blocks.WHEAT.defaultBlockState();
        if (item == Items.CARROT) return Blocks.CARROTS.defaultBlockState();
        if (item == Items.POTATO) return Blocks.POTATOES.defaultBlockState();
        if (item == Items.BEETROOT_SEEDS) return Blocks.BEETROOTS.defaultBlockState();
        if (item == Items.PUMPKIN_SEEDS) return Blocks.PUMPKIN_STEM.defaultBlockState();
        if (item == Items.MELON_SEEDS) return Blocks.MELON_STEM.defaultBlockState();
        if (item == Items.TORCHFLOWER_SEEDS) return Blocks.TORCHFLOWER_CROP.defaultBlockState();
        if (item == Items.PITCHER_POD) return Blocks.PITCHER_CROP.defaultBlockState();

        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState defaultState = block.defaultBlockState();
            if (block instanceof CropBlock || block instanceof StemBlock || block instanceof SaplingBlock || defaultState.is(BlockTags.SAPLINGS)) {
                return defaultState;
            }
            if (defaultState.canSurvive(level, plantPos)) {
                return defaultState;
            }
        }
        return null;
    }

    public int getTotalSeedsCount() {
        int count = 0;
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) count += stack.getCount();
        }
        return count;
    }

    public int getEnergy() {
        return this.energy;
    }

    public boolean isWorking() {
        return this.working;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
        output.putInt("scanIndex", this.scanIndex);
        output.putBoolean("working", this.working);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.scanIndex = input.getIntOr("scanIndex", 0);
        this.working = input.getBooleanOr("working", false);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === WorldlyContainer & Container Implementation ===
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return isPlantable(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isPlantable(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // === MenuProvider Implementation ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.auto_planter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoPlanterMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
