package com.refinery.menu;

import com.refinery.RefineryMod;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DiamondPipeMenu extends AbstractContainerMenu {
    public static final int FILTER_SLOTS_COUNT = 54;
    private final Container filterContainer;

    public DiamondPipeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(FILTER_SLOTS_COUNT));
    }

    public DiamondPipeMenu(int containerId, Inventory playerInventory, Container filterContainer) {
        super(RefineryMod.DIAMOND_PIPE_MENU.get(), containerId);
        checkContainerSize(filterContainer, FILTER_SLOTS_COUNT);
        this.filterContainer = filterContainer;

        // 6 Directions x 9 Slots:
        // Row 0: DOWN   (y = 18)
        // Row 1: UP     (y = 36)
        // Row 2: NORTH  (y = 54)
        // Row 3: SOUTH  (y = 72)
        // Row 4: WEST   (y = 90)
        // Row 5: EAST   (y = 108)
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9;
                this.addSlot(new FilterSlot(filterContainer, slotIndex, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player Inventory (3 rows at y = 140)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Player Hotbar (1 row at y = 198)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
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
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.filterContainer.stillValid(player);
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
