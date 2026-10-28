package com.refinery.menu;

import com.refinery.RefineryMod;
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

public class LargeDieselBoilerMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 4;
    public static final int DATA_COUNT = 7;

    private final Container container;
    private final ContainerData data;

    public LargeDieselBoilerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public LargeDieselBoilerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.LARGE_DIESEL_BOILER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Slot 0, 1: Bucket/Canister Input at (73, 24) and (111, 24)
        this.addSlot(new Slot(container, 0, 73, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(RefineryFluids.DIESEL_BUCKET.get()) || stack.is(Items.WATER_BUCKET);
            }
        });
        this.addSlot(new Slot(container, 1, 111, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(RefineryFluids.DIESEL_BUCKET.get()) || stack.is(Items.WATER_BUCKET);
            }
        });

        // Slot 2, 3: Bucket/Canister Output at (73, 44) and (111, 44)
        this.addSlot(new Slot(container, 2, 73, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 111, 44) {
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

    public int getDieselAmount() {
        return this.data.get(0);
    }

    public int getWaterAmount() {
        return this.data.get(1);
    }

    public int getSteamAmount() {
        return this.data.get(2);
    }

    public boolean isBurning() {
        return this.data.get(3) != 0;
    }

    public int getOverheatTicks() {
        return this.data.get(4);
    }

    public boolean isAssembled() {
        return this.data.get(5) != 0;
    }

    public boolean hasRedstone() {
        return this.data.get(6) != 0;
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
            } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
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
