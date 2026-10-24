package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.IndustrialGrinderControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class IndustrialGrinderMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = IndustrialGrinderControllerBlockEntity.INVENTORY_SIZE; // 8: 4 in, 4 out
    public static final int DATA_COUNT = 5;

    private final Container container;
    private final ContainerData data;

    public IndustrialGrinderMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public IndustrialGrinderMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.INDUSTRIAL_GRINDER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Input slots 0..3 (2x2 grid: x: 38, 56; y: 26, 44)
        this.addSlot(new Slot(container, 0, 38, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !IndustrialGrinderControllerBlockEntity.getGrindResult(stack).isEmpty();
            }
        });
        this.addSlot(new Slot(container, 1, 56, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !IndustrialGrinderControllerBlockEntity.getGrindResult(stack).isEmpty();
            }
        });
        this.addSlot(new Slot(container, 2, 38, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !IndustrialGrinderControllerBlockEntity.getGrindResult(stack).isEmpty();
            }
        });
        this.addSlot(new Slot(container, 3, 56, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !IndustrialGrinderControllerBlockEntity.getGrindResult(stack).isEmpty();
            }
        });

        // Output slots 4..7 (2x2 grid: x: 116, 134; y: 26, 44)
        this.addSlot(new Slot(container, 4, 116, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 5, 134, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 6, 116, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 7, 134, 44) {
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

    public boolean isGrinding() {
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
                // From grinder to player inventory
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player to input slots (0..3)
                if (!IndustrialGrinderControllerBlockEntity.getGrindResult(slotStack).isEmpty()) {
                    if (!this.moveItemStackTo(slotStack, 0, 4, false)) {
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
