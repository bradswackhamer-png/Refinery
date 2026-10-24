package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.MetalPressControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MetalPressMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = MetalPressControllerBlockEntity.INVENTORY_SIZE; // 4
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final ContainerData data;

    public MetalPressMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public MetalPressMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.METAL_PRESS_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Input slots (x: 44, 62; y: 34)
        this.addSlot(new Slot(container, 0, 44, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !com.refinery.block.entity.MetalPressControllerBlockEntity.getPressResult(stack).isEmpty();
            }
        });
        this.addSlot(new Slot(container, 1, 62, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !com.refinery.block.entity.MetalPressControllerBlockEntity.getPressResult(stack).isEmpty();
            }
        });

        // Output slots (x: 116, 134; y: 34)
        this.addSlot(new Slot(container, 2, 116, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 134, 34) {
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

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getEnergy() {
        return (this.data.get(3) << 16) | (this.data.get(2) & 0xFFFF);
    }

    public boolean isAssembled() {
        return this.data.get(4) != 0;
    }

    public boolean isPressing() {
        return getProgress() > 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                // From press to player inventory
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player to input slots
                if (!com.refinery.block.entity.MetalPressControllerBlockEntity.getPressResult(slotStack).isEmpty()) {
                    if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
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
