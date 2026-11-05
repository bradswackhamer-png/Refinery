package com.refinery.menu;

import com.refinery.RefineryMod;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WoodPipeMenu extends AbstractContainerMenu {
    public static final int FILTER_SLOTS_COUNT = 9;
    private final Container filterContainer;

    public WoodPipeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(FILTER_SLOTS_COUNT));
    }

    public WoodPipeMenu(int containerId, Inventory playerInventory, Container filterContainer) {
        super(RefineryMod.WOOD_PIPE_MENU.get(), containerId);
        checkContainerSize(filterContainer, FILTER_SLOTS_COUNT);
        this.filterContainer = filterContainer;

        // 3x3 Filter Grid (centered in dispenser GUI: x = 62 + col * 18, y = 17 + row * 18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = col + row * 3;
                this.addSlot(new FilterSlot(filterContainer, slotIndex, 62 + col * 18, 17 + row * 18));
            }
        }

        // Player Inventory (3 rows x 9 columns at y = 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (1 row x 9 columns at y = 142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.filterContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex < FILTER_SLOTS_COUNT) {
                if (!this.moveItemStackTo(itemstack1, FILTER_SLOTS_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, FILTER_SLOTS_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    public static class FilterSlot extends Slot {
        public FilterSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return true;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
