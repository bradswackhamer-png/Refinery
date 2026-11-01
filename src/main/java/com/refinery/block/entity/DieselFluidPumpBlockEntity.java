package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.fluid.MultiFluidHandler;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import com.refinery.menu.DieselFluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class DieselFluidPumpBlockEntity extends AbstractFluidPumpBlockEntity implements WorldlyContainer {
    public static final int INVENTORY_SIZE = 2; // Slot 0: Bucket In, Slot 1: Bucket Out
    public static final int DIESEL_CAPACITY = 4_000; // 4 Buckets
    public static final int DIESEL_PER_BUCKET = 10; // 10 mB per 1000 mB pumped (1 bucket = 100 pumped)
    public static final int FRAME_COLOR = 0xFFFA983A; // Caterpillar Yellow-Orange

    private int dieselAmount = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> dieselAmount & 0xFFFF;
                case 1 -> (dieselAmount >> 16) & 0xFFFF;
                case 2 -> fluidAmount & 0xFFFF;
                case 3 -> (fluidAmount >> 16) & 0xFFFF;
                case 4 -> (currentFluid != net.minecraft.world.level.material.Fluids.EMPTY)
                        ? getFluidTypeIndex(currentFluid)
                        : getFluidTypeIndex(lastPumpedFluid);
                case 5 -> infiniteWater ? 1 : 0;
                case 6 -> redstoneMode.ordinal();
                case 7 -> status.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 5 -> {
                    infiniteWater = (value != 0);
                    setChanged();
                }
                case 6 -> {
                    redstoneMode = RedstoneMode.values()[Math.clamp(value, 0, RedstoneMode.values().length - 1)];
                    setChanged();
                }
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public DieselFluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.DIESEL_FLUID_PUMP_BE.get(), pos, blockState);
    }

    @Override
    public int getFrameColor() {
        return FRAME_COLOR;
    }

    @Override
    public int getPushRate() {
        return 1000; // 2x amount of water per tick (was 500 mB/t)
    }

    @Override
    public int getPumpCycleTicks() {
        return 10; // 2x faster cycle (was 20 ticks)
    }

    @Override
    public int getPumpAmount() {
        return 2000; // 2x pump amount per cycle (was 1000 mB)
    }

    @Override
    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    protected PumpStatus getNoPowerStatus() {
        return PumpStatus.NO_DIESEL;
    }

    @Override
    public boolean hasPowerToPump() {
        return this.dieselAmount >= DIESEL_PER_BUCKET;
    }

    @Override
    public void consumePower() {
        this.dieselAmount = Math.max(0, this.dieselAmount - DIESEL_PER_BUCKET);
        setChanged();
    }

    @Override
    public void tickFuel() {
        handleFuelBuckets();
    }

    private void handleFuelBuckets() {
        ItemStack in = this.items.get(0);
        if (in.isEmpty()) return;

        boolean isDiesel = in.is(RefineryFluids.DIESEL_BUCKET.get());
        boolean isGasoline = in.is(RefineryFluids.GASOLINE_BUCKET.get());
        boolean isEthanol = in.is(RefineryFluids.ETHANOL_BUCKET.get());

        if (isDiesel || isGasoline || isEthanol) {
            if (this.dieselAmount + 1000 <= DIESEL_CAPACITY) {
                ItemStack out = this.items.get(1);
                if (out.isEmpty() || (out.is(Items.BUCKET) && out.getCount() < out.getMaxStackSize())) {
                    this.dieselAmount += 1000;
                    in.shrink(1);
                    if (out.isEmpty()) {
                        this.items.set(1, new ItemStack(Items.BUCKET));
                    } else {
                        out.grow(1);
                    }
                    setChanged();
                }
            }
        }
    }

    public int getDieselAmount() {
        return this.dieselAmount;
    }

    public void setDieselAmount(int amt) {
        this.dieselAmount = amt;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    private final DieselFuelHandler fuelHandler = new DieselFuelHandler();

    private class DieselFuelHandler extends net.neoforged.neoforge.transfer.transaction.SnapshotJournal<Integer> implements ResourceHandler<FluidResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            if (index != 0 || dieselAmount <= 0) return FluidResource.EMPTY;
            return FluidResource.of(RefineryFluids.DIESEL_SOURCE.get());
        }

        @Override
        public long getAmountAsLong(int index) {
            return index == 0 ? dieselAmount : 0L;
        }

        @Override
        public int getAmountAsInt(int index) {
            return index == 0 ? dieselAmount : 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return DIESEL_CAPACITY;
        }

        @Override
        public int getCapacityAsInt(int index, FluidResource resource) {
            return DIESEL_CAPACITY;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            if (index != 0 || resource.isEmpty()) return false;
            net.minecraft.world.level.material.Fluid f = resource.getFluid();
            return f == RefineryFluids.DIESEL_SOURCE.get()
                    || f == RefineryFluids.GASOLINE_SOURCE.get()
                    || f == RefineryFluids.ETHANOL_SOURCE.get();
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            if (index != 0 || amount <= 0 || !isValid(0, resource)) return 0;
            int space = DIESEL_CAPACITY - dieselAmount;
            int toInsert = Math.min(space, amount);
            if (toInsert <= 0) return 0;

            updateSnapshots(transaction);
            dieselAmount += toInsert;
            setChanged();
            return toInsert;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            return 0; // Fuel tank is insert-only (consumes fuel, does not discharge fuel into pipes)
        }

        @Override
        protected Integer createSnapshot() {
            return dieselAmount;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            dieselAmount = snapshot;
            setChanged();
        }
    }

    @Override
    public @Nullable ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) {
            return null; // Skid / base mounting face has no fluid port
        }
        ResourceHandler<FluidResource> outputHandler = super.getFluidHandler(side);
        if (outputHandler == null) {
            return this.fuelHandler;
        }
        return new MultiFluidHandler(outputHandler, this.fuelHandler);
    }

    // === WorldlyContainer Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return new int[]{1};
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 0 && isLiquidFuelBucket(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.items) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack res = ContainerHelper.removeItem(this.items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
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
        return slot == 0 && isLiquidFuelBucket(stack);
    }

    public static boolean isLiquidFuelBucket(ItemStack stack) {
        return stack.is(RefineryFluids.DIESEL_BUCKET.get()) ||
                stack.is(RefineryFluids.GASOLINE_BUCKET.get()) ||
                stack.is(RefineryFluids.ETHANOL_BUCKET.get());
    }

    public static boolean isFuelBucket(ItemStack stack) {
        return isLiquidFuelBucket(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.diesel_fluid_pump");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DieselFluidPumpMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("diesel", this.dieselAmount);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dieselAmount = input.getIntOr("diesel", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }
}
