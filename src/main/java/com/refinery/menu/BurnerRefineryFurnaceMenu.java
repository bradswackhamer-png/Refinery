package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.BurnerRefineryFurnaceBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BurnerRefineryFurnaceMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 4;
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final ContainerData data;

    public BurnerRefineryFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public BurnerRefineryFurnaceMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.BURNER_REFINERY_FURNACE_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Slots 0, 1, 2: Solid / Bucket Fuel Input Slots (horizontal row at top-left panel)
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(container, i, 12 + i * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return BurnerRefineryFurnaceBlockEntity.getFuelBurnDuration(stack) > 0;
                }
            });
        }

        // Slot 3: Empty Bucket Return Slot (below Slot 2)
        this.addSlot(new Slot(container, 3, 48, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (1x9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            int nextMode = (this.data.get(4) + 1) % 3;
            this.data.set(4, nextMode);
            if (this.container instanceof BurnerRefineryFurnaceBlockEntity be) {
                be.setRedstoneMode(nextMode);
            }
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getBurnTime() {
        return this.data.get(0);
    }

    public int getMaxBurnTime() {
        return this.data.get(1);
    }

    public int getTemperature() {
        return this.data.get(2);
    }

    public boolean isActive() {
        return this.data.get(3) != 0;
    }

    public int getRedstoneMode() {
        return this.data.get(4);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (BurnerRefineryFurnaceBlockEntity.getFuelBurnDuration(slotStack) > 0) {
                    if (!this.moveItemStackTo(slotStack, 0, 3, false)) {
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
