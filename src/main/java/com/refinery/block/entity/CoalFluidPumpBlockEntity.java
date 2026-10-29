package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.menu.CoalFluidPumpMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class CoalFluidPumpBlockEntity extends AbstractFluidPumpBlockEntity implements WorldlyContainer {
    public static final int INVENTORY_SIZE = 1; // Slot 0: Coal / Solid fuel
    public static final int SOLID_FUEL_BURN_TICKS = 60; // 1 coal burns for 3 seconds (60 ticks)
    public static final int FRAME_COLOR = 0xFF374151; // Heavy Cast-Iron Dark Charcoal

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
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

    public CoalFluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        this(RefineryMod.COAL_FLUID_PUMP_BE.get(), pos, blockState);
    }

    protected CoalFluidPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public int getFrameColor() {
        return FRAME_COLOR;
    }

    @Override
    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    protected PumpStatus getNoPowerStatus() {
        return PumpStatus.NO_FUEL;
    }

    @Override
    public boolean hasPowerToPump() {
        return this.burnTime > 0 || canBurnSolidFuel();
    }

    public boolean canBurnSolidFuel() {
        ItemStack fuel = this.items.get(0);
        return !fuel.isEmpty() && isSolidFuel(fuel);
    }

    public static boolean isSolidFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL) || stack.is(Items.COAL_BLOCK) || stack.is(Items.BLAZE_ROD)) return true;
        if (stack.is(RefineryMod.TAR_ITEM.get()) || stack.is(RefineryMod.BITUMEN.get()) || stack.is(RefineryMod.COAL_DUST.get())) return true;
        return MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0;
    }

    public static int getSolidFuelDuration(ItemStack stack) {
        if (stack.is(Items.COAL_BLOCK)) return SOLID_FUEL_BURN_TICKS * 9;
        return SOLID_FUEL_BURN_TICKS;
    }

    @Override
    public void consumePower() {
        if (this.burnTime <= 0) {
            ItemStack fuel = this.items.get(0);
            if (!fuel.isEmpty() && isSolidFuel(fuel)) {
                int dur = getSolidFuelDuration(fuel);
                this.burnTime = dur;
                this.maxBurnTime = dur;
                fuel.shrink(1);
                setChanged();
            }
        }
    }

    @Override
    public void tickFuel() {
        // Decrement burn time ONLY while pump is actively pumping or in pump cycle
        if (this.burnTime > 0) {
            if (this.status == PumpStatus.PUMPING || this.pumpCooldown > 0) {
                this.burnTime--;
            }
        }
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    // === WorldlyContainer Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 0 && isSolidFuel(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
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
        return slot == 0 && isSolidFuel(stack);
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
        return Component.translatable("container.refinery.coal_fluid_pump");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CoalFluidPumpMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }
}
