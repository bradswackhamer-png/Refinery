package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.BlastFurnaceControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlastFurnaceMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = BlastFurnaceControllerBlockEntity.INVENTORY_SIZE;
    public static final int DATA_COUNT = 4;

    private final Container container;
    private final ContainerData data;

    public BlastFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public BlastFurnaceMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.BLAST_FURNACE_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // 1. Iron Input Queue (2x2 grid: x: 20, 38; y: 22, 40)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                int slotIndex = c + r * 2;
                this.addSlot(new Slot(container, slotIndex, 20 + c * 18, 22 + r * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return BlastFurnaceControllerBlockEntity.isIronSource(stack);
                    }
                });
            }
        }

        // 2. Coal / Fuel Input Queue (2x2 grid: x: 62, 80; y: 22, 40)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                int slotIndex = 4 + c + r * 2;
                this.addSlot(new Slot(container, slotIndex, 62 + c * 18, 22 + r * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return BlastFurnaceControllerBlockEntity.isFuelSource(stack);
                    }
                });
            }
        }

        // 3. Steel Ingot Output Buffer (2x2 grid: x: 126, 144; y: 22, 40)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                int slotIndex = 8 + c + r * 2;
                this.addSlot(new Slot(container, slotIndex, 126 + c * 18, 22 + r * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false; // Output only
                    }
                });
            }
        }

        // 4. Player Inventory (3 rows of 9 at y: 74)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 74 + row * 18));
            }
        }

        // 5. Player Hotbar (9 slots at y: 132)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 132));
        }

        this.addDataSlots(data);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public boolean isAssembled() {
        return this.data.get(2) != 0;
    }

    public boolean isSmelting() {
        return this.data.get(3) != 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                // From blast furnace to player inventory
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to blast furnace
                if (BlastFurnaceControllerBlockEntity.isIronSource(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (BlastFurnaceControllerBlockEntity.isFuelSource(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 4, 8, false)) {
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
