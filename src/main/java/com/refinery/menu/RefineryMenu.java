package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RefineryMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 6;
    public static final int DATA_COUNT = 11;

    private final Container container;
    private final ContainerData data;

    public RefineryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public RefineryMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.REFINERY_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // Slot 0: Burner Fuel Slot (Coal, Tar, Bitumen, Lava, Ethanol Bucket)
        this.addSlot(new Slot(container, 0, 44, 53));

        // Slot 1: Feedstock Input Slot (Oil Sand)
        this.addSlot(new Slot(container, 1, 44, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return RefineryControllerBlockEntity.isOilSand(stack);
            }
        });

        // Slots 2-5: Byproduct Output Slots (Sand, Tar, Sulfur)
        this.addSlot(new Slot(container, 2, 126, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 144, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 4, 126, 62) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 5, 144, 62) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 94 + row * 18));
            }
        }

        // Player Hotbar (1x9)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 152));
        }

        this.addDataSlots(data);
    }

    public int getEnergy() {
        int low = this.data.get(0) & 0xFFFF;
        int high = this.data.get(1) & 0xFFFF;
        return (high << 16) | low;
    }

    public int getBurnTime() {
        return this.data.get(2);
    }

    public int getMaxBurnTime() {
        return this.data.get(3);
    }

    public int getProgress() {
        return this.data.get(4);
    }

    public int getCrudeOil() {
        return this.data.get(5);
    }

    public int getDiesel() {
        return this.data.get(6);
    }

    public int getGasoline() {
        return this.data.get(7);
    }

    public boolean isAssembled() {
        return this.data.get(8) != 0;
    }

    public boolean isBurning() {
        return this.getBurnTime() > 0;
    }

    public int getOilSandProgress() {
        return this.data.get(9);
    }

    public int getMaxOilSandProgress() {
        return this.data.get(10);
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
                if (RefineryControllerBlockEntity.isOilSand(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (RefineryControllerBlockEntity.getFuelBurnDuration(slotStack) > 0) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
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
