package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.ElectricFluidPumpBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ElectricFluidPumpMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 8;

    private final ContainerData data;

    public ElectricFluidPumpMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(DATA_COUNT));
    }

    public ElectricFluidPumpMenu(int containerId, Inventory playerInventory, ContainerData data) {
        super(RefineryMod.ELECTRIC_FLUID_PUMP_MENU.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;

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
                boolean curr = isInfiniteWater();
                this.data.set(5, curr ? 0 : 1);
                // When on server, if block entity at pos is available, set it directly or let data slot handle
                return true;
            }
            case 1 -> {
                int next = (getRedstoneMode().ordinal() + 1) % AbstractFluidPumpBlockEntity.RedstoneMode.values().length;
                this.data.set(6, next);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public int getEnergy() {
        return this.data.get(0) | (this.data.get(1) << 16);
    }

    public int getFluidAmount() {
        return this.data.get(2) | (this.data.get(3) << 16);
    }

    public int getFluidTypeIndex() {
        return this.data.get(4);
    }

    public boolean isInfiniteWater() {
        return this.data.get(5) != 0;
    }

    public AbstractFluidPumpBlockEntity.RedstoneMode getRedstoneMode() {
        int idx = this.data.get(6);
        var modes = AbstractFluidPumpBlockEntity.RedstoneMode.values();
        if (idx < 0 || idx >= modes.length) return AbstractFluidPumpBlockEntity.RedstoneMode.IGNORED;
        return modes[idx];
    }

    public AbstractFluidPumpBlockEntity.PumpStatus getPumpStatus() {
        int idx = this.data.get(7);
        var statuses = AbstractFluidPumpBlockEntity.PumpStatus.values();
        if (idx < 0 || idx >= statuses.length) return AbstractFluidPumpBlockEntity.PumpStatus.IDLE;
        return statuses[idx];
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
