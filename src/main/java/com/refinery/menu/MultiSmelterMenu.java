package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.MultiSmelterControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MultiSmelterMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = MultiSmelterControllerBlockEntity.INVENTORY_SIZE; // 17
    public static final int DATA_COUNT = 7 + MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT; // 15

    private final Container container;
    private final ContainerData data;

    public MultiSmelterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public MultiSmelterMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.MULTI_SMELTER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // 8 Input slots (4x2 grid on the left: X: 26, 44, 62, 80; Y: 20, 38)
        // Row 1: slots 0..3
        for (int i = 0; i < 4; i++) {
            int slotIdx = i;
            this.addSlot(new Slot(container, slotIdx, 26 + i * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true;
                }
            });
        }
        // Row 2: slots 4..7
        for (int i = 0; i < 4; i++) {
            int slotIdx = 4 + i;
            this.addSlot(new Slot(container, slotIdx, 26 + i * 18, 38) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true;
                }
            });
        }

        // 8 Output slots (4x2 grid on the right: X: 100, 118, 136, 154; Y: 20, 38)
        // Row 1: slots 8..11
        for (int i = 0; i < 4; i++) {
            int slotIdx = 8 + i;
            this.addSlot(new Slot(container, slotIdx, 100 + i * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
        // Row 2: slots 12..15
        for (int i = 0; i < 4; i++) {
            int slotIdx = 12 + i;
            this.addSlot(new Slot(container, slotIdx, 100 + i * 18, 38) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 1 Fuel slot (slot 16, centered at bottom of smelter area: X: 89, Y: 60)
        this.addSlot(new Slot(container, 16, 89, 60) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0;
            }
        });

        // Player Inventory (3 rows x 9 columns)
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

    public int getMaxProcessTime() {
        return this.data.get(0);
    }

    public int getEnergy() {
        int low = this.data.get(1);
        int high = this.data.get(2);
        return (high << 16) | (low & 0xFFFF);
    }

    public int getBurnTime() {
        return this.data.get(3);
    }

    public int getMaxBurnTime() {
        return this.data.get(4);
    }

    public boolean isAssembled() {
        return this.data.get(5) != 0;
    }

    public int getActiveSlotsMask() {
        return this.data.get(6);
    }

    public int getCookProgress(int slot) {
        if (slot >= 0 && slot < MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT) {
            return this.data.get(7 + slot);
        }
        return 0;
    }

    public boolean isSlotCooking(int slot) {
        return (getActiveSlotsMask() & (1 << slot)) != 0;
    }

    public boolean isSmelting() {
        return getActiveSlotsMask() != 0 || getBurnTime() > 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < CONTAINER_SIZE) {
                // Moving from machine to player inventory
                if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to machine
                if (MultiSmelterControllerBlockEntity.getFuelBurnDuration(stackInSlot) > 0) {
                    if (!this.moveItemStackTo(stackInSlot, 16, 17, false)) {
                        if (!this.moveItemStackTo(stackInSlot, 0, 8, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (!this.moveItemStackTo(stackInSlot, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
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
