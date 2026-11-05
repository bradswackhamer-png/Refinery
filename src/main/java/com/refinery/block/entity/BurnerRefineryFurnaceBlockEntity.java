package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BurnerRefineryFurnaceBlock;
import com.refinery.block.FurnacePart;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.BurnerRefineryFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class BurnerRefineryFurnaceBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, IRefineryFurnace {
    public static final int INVENTORY_SIZE = 4; // Slots 0, 1, 2 = Fuel Input; Slot 3 = Bucket Return
    public static final int BURN_SPEED = 4;     // Fast burn rate: 4x faster than a standard furnace

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int temperature = AMBIENT_TEMP;
    private int redstoneMode = 0; // 0 = ALWAYS_ON, 1 = ACTIVE_HIGH, 2 = ACTIVE_LOW
    private boolean active = false;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BurnerRefineryFurnaceBlockEntity.this.burnTime;
                case 1 -> BurnerRefineryFurnaceBlockEntity.this.maxBurnTime;
                case 2 -> BurnerRefineryFurnaceBlockEntity.this.temperature;
                case 3 -> BurnerRefineryFurnaceBlockEntity.this.active ? 1 : 0;
                case 4 -> BurnerRefineryFurnaceBlockEntity.this.redstoneMode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BurnerRefineryFurnaceBlockEntity.this.burnTime = value;
                case 1 -> BurnerRefineryFurnaceBlockEntity.this.maxBurnTime = value;
                case 2 -> BurnerRefineryFurnaceBlockEntity.this.temperature = value;
                case 3 -> BurnerRefineryFurnaceBlockEntity.this.active = (value != 0);
                case 4 -> BurnerRefineryFurnaceBlockEntity.this.redstoneMode = value;
            }
        }

        @Override
        public int getCount() {
            return BurnerRefineryFurnaceMenu.DATA_COUNT;
        }
    };

    public BurnerRefineryFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.BURNER_REFINERY_FURNACE_BE.get(), pos, state);
    }

    public BurnerRefineryFurnaceBlockEntity getMaster() {
        if (this.level == null) return this;
        BlockState state = getBlockState();
        if (state.hasProperty(BurnerRefineryFurnaceBlock.PART) && state.getValue(BurnerRefineryFurnaceBlock.PART) == FurnacePart.UPPER) {
            BlockPos masterPos = this.worldPosition.below();
            if (this.level.getBlockEntity(masterPos) instanceof BurnerRefineryFurnaceBlockEntity master) {
                return master;
            }
        }
        return this;
    }

    public boolean isMaster() {
        BlockState state = getBlockState();
        return !state.hasProperty(BurnerRefineryFurnaceBlock.PART) || state.getValue(BurnerRefineryFurnaceBlock.PART) == FurnacePart.LOWER;
    }

    @Override
    public int getTemperature() {
        return getMaster().temperature;
    }

    @Override
    public boolean isHeating() {
        return getMaster().active;
    }

    @Override
    public boolean isElectric() {
        return false;
    }

    public boolean isActive() {
        return getMaster().active;
    }

    public int getRedstoneMode() {
        return getMaster().redstoneMode;
    }

    public void setRedstoneMode(int mode) {
        BurnerRefineryFurnaceBlockEntity m = getMaster();
        m.redstoneMode = mode % 3;
        m.setChanged();
        m.syncToClient();
    }

    public NonNullList<ItemStack> getInventory() {
        return this.items;
    }

    public void syncToClient() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide() || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!isMaster()) return;

        BlockState state = getBlockState();
        BlockPos upperPos = this.worldPosition.above();

        boolean hasRedstone = serverLevel.hasNeighborSignal(this.worldPosition) || serverLevel.hasNeighborSignal(upperPos);
        boolean redstoneAllows = switch (this.redstoneMode) {
            case 1 -> hasRedstone;
            case 2 -> !hasRedstone;
            default -> true;
        };

        boolean wasActive = this.active;
        boolean dirty = false;

        if (this.burnTime > 0) {
            this.burnTime = Math.max(0, this.burnTime - BURN_SPEED);
            dirty = true;
        }

        if (this.burnTime <= 0 && redstoneAllows) {
            // Try to consume fuel from slots 0, 1, 2
            for (int i = 0; i < 3; i++) {
                ItemStack fuelStack = this.items.get(i);
                if (!fuelStack.isEmpty()) {
                    int duration = getFuelBurnDuration(fuelStack);
                    if (duration > 0 && canConsumeFuel(fuelStack)) {
                        this.burnTime = duration;
                        this.maxBurnTime = duration;
                        consumeFuelItem(i, fuelStack);
                        dirty = true;
                        break;
                    }
                }
            }
        }

        this.active = this.burnTime > 0;

        if (this.active) {
            if (this.temperature < MAX_TEMP) {
                this.temperature = Math.min(MAX_TEMP, this.temperature + 2);
                dirty = true;
            }

            // Crackle sound & smokestack particles
            if (serverLevel.getGameTime() % 18 == 0) {
                serverLevel.playSound(null, this.worldPosition, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.8f, 0.9f);
            }
            if (serverLevel.getGameTime() % 3 == 0) {
                double sx = this.worldPosition.getX() + 0.5;
                double sy = this.worldPosition.getY() + 2.65;
                double sz = this.worldPosition.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, sx, sy, sz, 1, 0.04, 0.10, 0.04, 0.015);
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.45, this.worldPosition.getZ() + 0.5,
                        1, 0.12, 0.08, 0.12, 0.005);
            }
        } else {
            if (this.temperature > AMBIENT_TEMP && serverLevel.getGameTime() % 2 == 0) {
                this.temperature = Math.max(AMBIENT_TEMP, this.temperature - 1);
                dirty = true;
            }
        }

        if (wasActive != this.active) {
            if (state.hasProperty(BurnerRefineryFurnaceBlock.ACTIVE)) {
                serverLevel.setBlock(this.worldPosition, state.setValue(BurnerRefineryFurnaceBlock.ACTIVE, this.active), 3);
            }
            BlockState upState = serverLevel.getBlockState(upperPos);
            if (upState.is(state.getBlock()) && upState.hasProperty(BurnerRefineryFurnaceBlock.ACTIVE)) {
                serverLevel.setBlock(upperPos, upState.setValue(BurnerRefineryFurnaceBlock.ACTIVE, this.active), 3);
            }
            dirty = true;
            syncToClient();
        } else if (dirty && serverLevel.getGameTime() % 10 == 0) {
            syncToClient();
        }

        if (dirty) {
            setChanged();
        }
    }

    private boolean canConsumeFuel(ItemStack fuelStack) {
        if (isBucketFuel(fuelStack)) {
            ItemStack bucketSlot = this.items.get(3);
            return bucketSlot.isEmpty() || (bucketSlot.is(Items.BUCKET) && bucketSlot.getCount() < bucketSlot.getMaxStackSize());
        }
        return true;
    }

    private void consumeFuelItem(int slot, ItemStack fuelStack) {
        boolean isBucket = isBucketFuel(fuelStack);
        fuelStack.shrink(1);
        if (fuelStack.isEmpty()) {
            this.items.set(slot, ItemStack.EMPTY);
        }
        if (isBucket) {
            ItemStack bucketSlot = this.items.get(3);
            if (bucketSlot.isEmpty()) {
                this.items.set(3, new ItemStack(Items.BUCKET));
            } else if (bucketSlot.is(Items.BUCKET)) {
                bucketSlot.grow(1);
            }
        }
    }

    private static boolean isBucketFuel(ItemStack stack) {
        return stack.is(Items.LAVA_BUCKET)
                || stack.is(RefineryFluids.ETHANOL_BUCKET.get())
                || stack.is(RefineryFluids.LIQUID_TAR_BUCKET.get())
                || stack.is(RefineryFluids.DIESEL_BUCKET.get())
                || stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get());
    }

    public static int getFuelBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        if (stack.is(RefineryFluids.DIESEL_BUCKET.get())) return 8000;
        if (stack.is(RefineryFluids.LIQUID_TAR_BUCKET.get())) return 6400;
        if (stack.is(RefineryFluids.ETHANOL_BUCKET.get())) return 4000;
        if (stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get())) return 3200;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(ItemTags.COALS)) return 1600;
        if (stack.is(RefineryMod.TAR_ITEM.get())) return 1600;
        if (stack.is(RefineryMod.BITUMEN.get())) return 1600;
        if (stack.is(RefineryMod.COAL_DUST.get())) return 1600;
        if (stack.is(ItemTags.LOGS)) return 300;
        if (stack.is(ItemTags.PLANKS)) return 300;
        if (stack.is(Items.STICK)) return 100;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && (id.getPath().contains("tar") || id.getPath().contains("bitumen") || id.getPath().contains("coal") || id.getPath().contains("coke"))) {
            return 1600;
        }
        if (stack.has(DataComponents.COOKING_FUEL)) return 1600;
        return 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
        output.putInt("temperature", this.temperature);
        output.putInt("redstoneMode", this.redstoneMode);
        output.putBoolean("active", this.active);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
        this.temperature = input.getIntOr("temperature", AMBIENT_TEMP);
        this.redstoneMode = input.getIntOr("redstoneMode", 0);
        this.active = input.getBooleanOr("active", false);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_INPUT = new int[]{0, 1, 2};
    private static final int[] SLOTS_OUTPUT = new int[]{3};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_OUTPUT : SLOTS_INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 3;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 0 && slot < 3 && getFuelBurnDuration(stack) > 0;
    }

    @Override
    public int getContainerSize() {
        return getMaster().items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : getMaster().items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return getMaster().items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        BurnerRefineryFurnaceBlockEntity m = getMaster();
        ItemStack res = ContainerHelper.removeItem(m.items, slot, amount);
        if (!res.isEmpty()) m.setChanged();
        return res;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getMaster().items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        BurnerRefineryFurnaceBlockEntity m = getMaster();
        m.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        m.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(getMaster(), player);
    }

    @Override
    public void clearContent() {
        getMaster().items.clear();
        getMaster().setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.refinery.burner_refinery_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        BurnerRefineryFurnaceBlockEntity m = getMaster();
        return new BurnerRefineryFurnaceMenu(containerId, playerInventory, m, m.dataAccess);
    }
}
