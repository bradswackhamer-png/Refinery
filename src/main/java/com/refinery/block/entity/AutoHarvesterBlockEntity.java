package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.AutoHarvesterBlock;
import com.refinery.menu.AutoHarvesterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoHarvesterBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 9;
    public static final int ENERGY_CAPACITY = 10_000;
    public static final int ENERGY_PER_TICK = 1;
    public static final int ENERGY_PER_HARVEST = 20;

    public static final int SIDE_RANGE = 5;      // 5 left to 5 right (width: 11)
    public static final int FORWARD_RANGE = 11;  // 1 to 11 blocks in front
    public static final int Y_LEVELS = 4;        // dy: -2, -1, 0, 1
    public static final int TOTAL_POSITIONS = (SIDE_RANGE * 2 + 1) * FORWARD_RANGE * Y_LEVELS; // 11 * 11 * 4 = 484

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int energy = 0;
    private int scanIndex = 0;
    private int actionCooldown = 0;
    private int exportCooldown = 0;
    private boolean working = false;

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 500, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            AutoHarvesterBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            AutoHarvesterBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AutoHarvesterBlockEntity.this.energy & 0xFFFF;
                case 1 -> (AutoHarvesterBlockEntity.this.energy >> 16) & 0xFFFF;
                case 2 -> AutoHarvesterBlockEntity.this.working ? 1 : 0;
                case 3 -> AutoHarvesterBlockEntity.this.getTotalItemCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AutoHarvesterBlockEntity.this.energy = (AutoHarvesterBlockEntity.this.energy & 0xFFFF0000) | (value & 0xFFFF);
                case 1 -> AutoHarvesterBlockEntity.this.energy = (AutoHarvesterBlockEntity.this.energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 2 -> AutoHarvesterBlockEntity.this.working = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AutoHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.AUTO_HARVESTER_BE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        Direction facing = state.getValue(AutoHarvesterBlock.FACING);

        // Auto-export buffered items to chest/pipe behind or below every 10 ticks
        if (--this.exportCooldown <= 0) {
            this.exportCooldown = 10;
            tryExportItems(serverLevel, pos, facing);
        }

        // Energy availability check
        if (this.energy < ENERGY_PER_TICK) {
            if (this.working) {
                setWorking(serverLevel, pos, false);
            }
            return;
        }

        // Idle power draw
        this.energy -= ENERGY_PER_TICK;
        this.energyStorage.set(this.energy);

        if (this.actionCooldown > 0) {
            this.actionCooldown--;
            return;
        }

        // Check if internal buffer has space for harvested drops
        if (!hasBufferSpace()) {
            if (this.working) {
                setWorking(serverLevel, pos, false);
            }
            return;
        }

        boolean harvestedSomething = false;
        int checks = 0;

        // Scan up to 16 positions per tick
        while (checks < 16) {
            checks++;
            this.scanIndex = (this.scanIndex + 1) % TOTAL_POSITIONS;

            // Coordinate math: 11 wide (-5 to +5), 11 forward (1 to 11), 4 vertical (-2 to 1)
            int col = this.scanIndex / Y_LEVELS;
            int dyIdx = this.scanIndex % Y_LEVELS;

            int sideOffset = (col % (SIDE_RANGE * 2 + 1)) - SIDE_RANGE; // -5 to +5
            int forwardDist = (col / (SIDE_RANGE * 2 + 1)) + 1;           // 1 to 11
            int dy = dyIdx - 2;                                          // -2, -1, 0, 1

            // Calculate actual world target block
            Direction right = facing.getClockWise();
            BlockPos targetPos = pos.relative(facing, forwardDist)
                    .relative(right, sideOffset)
                    .above(dy);

            BlockState targetState = level.getBlockState(targetPos);

            if (isMatureCrop(targetState, level, targetPos)) {
                // Collect drops
                List<ItemStack> drops = getCropHarvestDrops(serverLevel, targetPos, targetState);

                // Update world state (replant age 0 or break block)
                harvestBlockInWorld(serverLevel, targetPos, targetState);

                // Deposit drops into buffer
                for (ItemStack drop : drops) {
                    insertIntoBuffer(drop);
                }

                // Deduct energy
                if (this.energy >= ENERGY_PER_HARVEST) {
                    this.energy -= ENERGY_PER_HARVEST;
                    this.energyStorage.set(this.energy);
                }

                // Sounds & visual particles
                level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        targetPos.getX() + 0.5, targetPos.getY() + 0.3, targetPos.getZ() + 0.5,
                        4, 0.2, 0.1, 0.2, 0.05);

                harvestedSomething = true;
                break;
            }
        }

        if (harvestedSomething) {
            this.actionCooldown = 4; // ~5 harvests per second
            setWorking(serverLevel, pos, true);
            setChanged();
        } else if (this.working && this.actionCooldown == 0) {
            setWorking(serverLevel, pos, false);
        }
    }

    public static boolean isMatureCrop(BlockState state, Level level, BlockPos pos) {
        Block block = state.getBlock();

        // Standard vanilla crops (Wheat, Carrots, Potatoes, Beetroot, Torchflower, Pitcher)
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }

        // Melon & Pumpkin blocks attached to stems
        if (block == Blocks.PUMPKIN || block == Blocks.MELON) {
            return true;
        }

        // Cocoa beans (age 2)
        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }

        // Sweet berry bush (age 3)
        if (block instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) >= 3;
        }

        // Nether wart (age 3)
        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }

        // Sugar cane / Cactus (harvest only if block below is also cane/cactus so root is kept)
        if (block == Blocks.SUGAR_CANE || block == Blocks.CACTUS) {
            return level.getBlockState(pos.below()).is(block);
        }

        // Generic modded crop age check
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equalsIgnoreCase("age") && prop instanceof IntegerProperty intProp) {
                int max = intProp.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
                if (state.getValue(intProp) >= max && max > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<ItemStack> getCropHarvestDrops(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        // Sweet berry bush special drop
        if (block instanceof SweetBerryBushBlock) {
            int count = 2 + level.getRandom().nextInt(2);
            return List.of(new ItemStack(Items.SWEET_BERRIES, count));
        }

        // Standard drops from loot table
        List<ItemStack> rawDrops = Block.getDrops(state, level, pos, null);
        List<ItemStack> result = new ArrayList<>();

        if (block instanceof CropBlock crop) {
            // Keep 1 seed for replanting
            Item seedItem = getSeedForCrop(block);
            boolean seedSubtracted = false;

            for (ItemStack drop : rawDrops) {
                if (!seedSubtracted && seedItem != null && drop.is(seedItem)) {
                    if (drop.getCount() > 1) {
                        ItemStack copy = drop.copy();
                        copy.shrink(1);
                        result.add(copy);
                    }
                    seedSubtracted = true;
                } else {
                    result.add(drop.copy());
                }
            }
        } else {
            for (ItemStack drop : rawDrops) {
                result.add(drop.copy());
            }
        }

        return result;
    }

    public static @Nullable Item getSeedForCrop(Block block) {
        if (block == Blocks.WHEAT) return Items.WHEAT_SEEDS;
        if (block == Blocks.CARROTS) return Items.CARROT;
        if (block == Blocks.POTATOES) return Items.POTATO;
        if (block == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
        if (block == Blocks.TORCHFLOWER_CROP) return Items.TORCHFLOWER_SEEDS;
        if (block == Blocks.PITCHER_CROP) return Items.PITCHER_POD;
        Item item = block.asItem();
        return item != Items.AIR ? item : null;
    }

    private static void harvestBlockInWorld(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            // Right-click harvest replant to age 0
            level.setBlock(pos, crop.getStateForAge(0), 3);
        } else if (block instanceof SweetBerryBushBlock) {
            level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), 3);
        } else if (block instanceof CocoaBlock) {
            level.setBlock(pos, state.setValue(CocoaBlock.AGE, 0), 3);
        } else if (block instanceof NetherWartBlock) {
            level.setBlock(pos, state.setValue(NetherWartBlock.AGE, 0), 3);
        } else {
            // Melon, Pumpkin, Sugar cane, Cactus
            level.destroyBlock(pos, false);
        }
    }

    private void insertIntoBuffer(ItemStack stack) {
        if (stack.isEmpty()) return;

        // Pass 1: Try stacking into existing matching items
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack inSlot = this.items.get(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, stack)) {
                int space = inSlot.getMaxStackSize() - inSlot.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.getCount());
                    inSlot.grow(toAdd);
                    stack.shrink(toAdd);
                    if (stack.isEmpty()) return;
                }
            }
        }

        // Pass 2: Place into empty slots
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack inSlot = this.items.get(i);
            if (inSlot.isEmpty()) {
                this.items.set(i, stack.copy());
                stack.setCount(0);
                return;
            }
        }

        // If buffer full, spawn remaining drops in front of machine
        if (!stack.isEmpty() && this.level != null) {
            Block.popResource(this.level, this.worldPosition, stack);
        }
    }

    private boolean hasBufferSpace() {
        for (ItemStack stack : this.items) {
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void tryExportItems(ServerLevel level, BlockPos pos, Direction facing) {
        if (isEmpty()) return;

        // Auto-eject targets: directly behind or directly below
        BlockPos[] targets = new BlockPos[]{
                pos.relative(facing.getOpposite()),
                pos.below()
        };

        for (BlockPos targetPos : targets) {
            BlockEntity targetBE = level.getBlockEntity(targetPos);
            if (targetBE instanceof Container targetContainer) {
                for (int i = 0; i < INVENTORY_SIZE; i++) {
                    ItemStack sourceStack = this.items.get(i);
                    if (!sourceStack.isEmpty()) {
                        ItemStack remainder = addItemToContainer(targetContainer, sourceStack.copy());
                        if (remainder.getCount() != sourceStack.getCount()) {
                            this.items.set(i, remainder);
                            setChanged();
                            targetContainer.setChanged();
                            return; // Exported 1 stack per cycle
                        }
                    }
                }
            }
        }
    }

    public static ItemStack addItemToContainer(Container target, ItemStack stack) {
        ItemStack remaining = stack.copy();

        // Pass 1: Existing stacks
        for (int i = 0; i < target.getContainerSize(); i++) {
            if (target.canPlaceItem(i, remaining)) {
                ItemStack inSlot = target.getItem(i);
                if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, remaining)) {
                    int space = Math.min(target.getMaxStackSize(remaining), inSlot.getMaxStackSize()) - inSlot.getCount();
                    if (space > 0) {
                        int toAdd = Math.min(space, remaining.getCount());
                        inSlot.grow(toAdd);
                        remaining.shrink(toAdd);
                        target.setItem(i, inSlot);
                        if (remaining.isEmpty()) return ItemStack.EMPTY;
                    }
                }
            }
        }

        // Pass 2: Empty slots
        for (int i = 0; i < target.getContainerSize(); i++) {
            if (target.canPlaceItem(i, remaining)) {
                ItemStack inSlot = target.getItem(i);
                if (inSlot.isEmpty()) {
                    int toAdd = Math.min(target.getMaxStackSize(remaining), remaining.getCount());
                    ItemStack placed = remaining.split(toAdd);
                    target.setItem(i, placed);
                    if (remaining.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }

        return remaining;
    }

    private void setWorking(ServerLevel level, BlockPos pos, boolean isWorking) {
        this.working = isWorking;
        BlockState current = getBlockState();
        if (current.getBlock() instanceof AutoHarvesterBlock && current.getValue(AutoHarvesterBlock.WORKING) != isWorking) {
            level.setBlock(pos, current.setValue(AutoHarvesterBlock.WORKING, isWorking), 3);
        }
    }

    public int getTotalItemCount() {
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
        return true;
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
        return Component.translatable("container.refinery.auto_harvester");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AutoHarvesterMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
