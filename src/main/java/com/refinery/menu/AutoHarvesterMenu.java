package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AutoHarvesterBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AutoHarvesterMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = AutoHarvesterBlockEntity.INVENTORY_SIZE;
    public static final int DATA_COUNT = 6;

    private final Container container;
    private final ContainerData data;

    public AutoHarvesterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), createDefaultContainerData());
    }

    private static ContainerData createDefaultContainerData() {
        SimpleContainerData defaultData = new SimpleContainerData(DATA_COUNT);
        defaultData.set(4, 1); // enabled = true
        defaultData.set(5, AutoHarvesterBlockEntity.DEFAULT_RADIUS);
        return defaultData;
    }

    public AutoHarvesterMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.AUTO_HARVESTER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // 3x3 Harvest Buffer Grid (centered at x: 62, y: 18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = col + row * 3;
                this.addSlot(new Slot(container, slotIndex, 62 + col * 18, 18 + row * 18));
            }
        }

        // Player Inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public boolean isWorking() {
        return this.data.get(2) != 0;
    }

    public int getItemCount() {
        return this.data.get(3);
    }

    public boolean isEnabled() {
        return this.data.get(4) != 0;
    }

    public int getRadius() {
        int r = this.data.get(5);
        return r > 0 ? r : AutoHarvesterBlockEntity.DEFAULT_RADIUS;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                boolean newEnabled = !isEnabled();
                this.data.set(4, newEnabled ? 1 : 0);
                if (this.container instanceof AutoHarvesterBlockEntity be) {
                    be.setEnabled(newEnabled);
                }
                return true;
            }
            case 1 -> {
                int newRadius = Math.max(AutoHarvesterBlockEntity.MIN_RADIUS, getRadius() - 1);
                this.data.set(5, newRadius);
                if (this.container instanceof AutoHarvesterBlockEntity be) {
                    be.setRadius(newRadius);
                }
                return true;
            }
            case 2 -> {
                int newRadius = Math.min(AutoHarvesterBlockEntity.MAX_RADIUS, getRadius() + 1);
                this.data.set(5, newRadius);
                if (this.container instanceof AutoHarvesterBlockEntity be) {
                    be.setRadius(newRadius);
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                // Moving from machine buffer to player inventory
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine buffer
                if (!this.moveItemStackTo(slotStack, 0, CONTAINER_SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
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
