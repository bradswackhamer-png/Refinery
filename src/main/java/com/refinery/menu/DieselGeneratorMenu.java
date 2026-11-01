package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.DieselGeneratorBlockEntity;
import com.refinery.fluid.RefineryFluids;
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

public class DieselGeneratorMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 2;
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final ContainerData data;

    public DieselGeneratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public DieselGeneratorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.DIESEL_GENERATOR_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Slot 0: Fuel Bucket Input (x: 12, y: 24)
        this.addSlot(new Slot(container, 0, 12, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(RefineryFluids.DIESEL_BUCKET.get());
            }
        });

        // Slot 1: Empty Bucket Output (x: 12, y: 50)
        this.addSlot(new Slot(container, 1, 12, 50) {
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
        if (id == 0) {
            int currentMode = this.data.get(3);
            this.data.set(3, (currentMode + 1) % 3);
            if (this.container instanceof DieselGeneratorBlockEntity be) {
                be.setRedstoneMode((currentMode + 1) % 3);
            }
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getDieselAmount() {
        return this.data.get(0);
    }

    public int getEnergyStored() {
        return this.data.get(1);
    }

    public boolean isActive() {
        return this.data.get(2) != 0;
    }

    public int getRedstoneMode() {
        return this.data.get(3);
    }

    public int getGenerationRate() {
        return this.data.get(4);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(slotStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.is(RefineryFluids.DIESEL_BUCKET.get())) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
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
        return this.container.stillValid(player);
    }
}
