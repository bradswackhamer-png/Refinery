package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.FluidPumpBlockEntity;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class FluidPumpMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 2;
    public static final int DATA_COUNT = 16;

    private final Container container;
    private final ContainerData data;

    public FluidPumpMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public FluidPumpMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.FLUID_PUMP_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Slot 0: Fuel / Bucket Input (x: 80, y: 37)
        this.addSlot(new Slot(container, 0, 80, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(RefineryFluids.DIESEL_BUCKET.get()) ||
                        stack.is(RefineryFluids.GASOLINE_BUCKET.get()) ||
                        stack.is(RefineryFluids.ETHANOL_BUCKET.get()) ||
                        FluidPumpBlockEntity.isSolidFuel(stack);
            }
        });

        // Slot 1: Empty Bucket Output (x: 106, y: 37)
        this.addSlot(new Slot(container, 1, 106, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (9 slots at y: 142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                // Toggle Power Mode: RF -> SOLID_FUEL -> DIESEL
                int next = (getPowerMode().ordinal() + 1) % FluidPumpBlockEntity.PowerMode.values().length;
                this.data.set(8, next);
                if (this.container instanceof FluidPumpBlockEntity pumpBe) {
                    pumpBe.setPowerMode(FluidPumpBlockEntity.PowerMode.values()[next]);
                }
                return true;
            }
            case 1 -> {
                // Toggle Infinite Water: OFF <-> ON
                boolean curr = isInfiniteWater();
                this.data.set(9, curr ? 0 : 1);
                if (this.container instanceof FluidPumpBlockEntity pumpBe) {
                    pumpBe.setInfiniteWater(!curr);
                }
                return true;
            }
            case 2 -> {
                // Toggle Redstone Mode: IGNORED -> REQUIRE_HIGH -> REQUIRE_LOW
                int next = (getRedstoneMode().ordinal() + 1) % FluidPumpBlockEntity.RedstoneMode.values().length;
                this.data.set(10, next);
                if (this.container instanceof FluidPumpBlockEntity pumpBe) {
                    pumpBe.setRedstoneMode(FluidPumpBlockEntity.RedstoneMode.values()[next]);
                }
                return true;
            }
            case 3 -> {
                // Clear Target Pos
                if (this.container instanceof FluidPumpBlockEntity pumpBe) {
                    pumpBe.setTargetPos(null);
                }
                return true;
            }
        }
        return false;
    }

    public void cyclePowerModeClient() {
        int next = (getPowerMode().ordinal() + 1) % FluidPumpBlockEntity.PowerMode.values().length;
        this.data.set(8, next);
    }

    public void toggleInfiniteWaterClient() {
        this.data.set(9, isInfiniteWater() ? 0 : 1);
    }

    public void cycleRedstoneModeClient() {
        int next = (getRedstoneMode().ordinal() + 1) % FluidPumpBlockEntity.RedstoneMode.values().length;
        this.data.set(10, next);
    }

    public int getEnergy() {
        return (this.data.get(0) & 0xFFFF) | ((this.data.get(1) & 0xFFFF) << 16);
    }

    public int getFluidAmount() {
        return (this.data.get(2) & 0xFFFF) | ((this.data.get(3) & 0xFFFF) << 16);
    }

    public int getFluidTypeIndex() {
        return this.data.get(4);
    }

    public int getDieselAmount() {
        return this.data.get(5);
    }

    public int getBurnTime() {
        return this.data.get(6);
    }

    public int getMaxBurnTime() {
        return this.data.get(7);
    }

    public FluidPumpBlockEntity.PowerMode getPowerMode() {
        int idx = this.data.get(8);
        var modes = FluidPumpBlockEntity.PowerMode.values();
        return modes[Math.clamp(idx, 0, modes.length - 1)];
    }

    public boolean isInfiniteWater() {
        return this.data.get(9) != 0;
    }

    public FluidPumpBlockEntity.RedstoneMode getRedstoneMode() {
        int idx = this.data.get(10);
        var modes = FluidPumpBlockEntity.RedstoneMode.values();
        return modes[Math.clamp(idx, 0, modes.length - 1)];
    }

    public FluidPumpBlockEntity.PumpStatus getStatus() {
        int idx = this.data.get(11);
        var statuses = FluidPumpBlockEntity.PumpStatus.values();
        return statuses[Math.clamp(idx, 0, statuses.length - 1)];
    }

    public boolean hasTarget() {
        return this.data.get(15) != 0;
    }

    public @Nullable BlockPos getTargetPos() {
        if (!hasTarget()) return null;
        return new BlockPos(this.data.get(12), this.data.get(13), this.data.get(14));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(itemstack1, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
