package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.ArcMelterControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

public class ArcMelterMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = ArcMelterControllerBlockEntity.INVENTORY_SIZE; // 4 input slots
    public static final int DATA_COUNT = 7;

    private final Container container;
    private final ContainerData data;

    public ArcMelterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public ArcMelterMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.ARC_MELTER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // 4 Input Slots in a 2x2 grid (x: 44, 62; y: 26, 44)
        this.addSlot(new Slot(container, 0, 44, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ArcMelterControllerBlockEntity.getMeltingResult(stack) != Fluids.EMPTY;
            }
        });
        this.addSlot(new Slot(container, 1, 62, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ArcMelterControllerBlockEntity.getMeltingResult(stack) != Fluids.EMPTY;
            }
        });
        this.addSlot(new Slot(container, 2, 44, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ArcMelterControllerBlockEntity.getMeltingResult(stack) != Fluids.EMPTY;
            }
        });
        this.addSlot(new Slot(container, 3, 62, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ArcMelterControllerBlockEntity.getMeltingResult(stack) != Fluids.EMPTY;
            }
        });

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

    public boolean isAssembled() {
        return this.data.get(4) == 1;
    }

    public boolean isMelting() {
        return this.data.get(0) > 0;
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

    public int getMoltenAmount() {
        return this.data.get(5);
    }

    public int getTankCapacity() {
        return this.data.get(6);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < CONTAINER_SIZE) {
                // Moving from machine to player
                if (!this.moveItemStackTo(itemstack1, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to machine
                if (ArcMelterControllerBlockEntity.getMeltingResult(itemstack1) != Fluids.EMPTY) {
                    if (!this.moveItemStackTo(itemstack1, 0, CONTAINER_SIZE, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < CONTAINER_SIZE + 27) {
                    if (!this.moveItemStackTo(itemstack1, CONTAINER_SIZE + 27, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, CONTAINER_SIZE, CONTAINER_SIZE + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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
