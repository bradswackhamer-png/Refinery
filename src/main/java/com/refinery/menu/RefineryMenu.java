package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.RefineryControllerBlockEntity;
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

        // Slot 0: Crude Oil Bucket / Oil Sand Input Slot (well at 11, 58)
        this.addSlot(new Slot(container, 0, 12, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get()) || RefineryControllerBlockEntity.isOilSand(stack);
            }
        });

        // Slot 1: Empty Bucket / Sand Return Slot (well at 29, 58)
        this.addSlot(new Slot(container, 1, 30, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Slots 2-5: Bitumen Output Slots (2x2 at 132/150, 37/55)
        this.addSlot(new Slot(container, 2, 132, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 3, 150, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 4, 132, 55) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 5, 150, 55) {
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id <= 2) {
            int stage = id + 1;
            if (this.container instanceof RefineryControllerBlockEntity be) {
                be.toggleStage(stage);
            } else {
                int flags = this.data.get(7);
                this.data.set(7, flags ^ (1 << id));
            }
            return true;
        }
        return false;
    }

    public int getCrudeOil() {
        return this.data.get(0);
    }

    public int getLiquidTar() {
        return this.data.get(1);
    }

    public int getDiesel() {
        return this.data.get(2);
    }

    public int getGasoline() {
        return this.data.get(3);
    }

    public int getProgress() {
        return this.data.get(4);
    }

    public int getCurrentHeat() {
        return this.data.get(5);
    }

    public int getFurnaceType() {
        return this.data.get(6);
    }

    public int getStageFlags() {
        return this.data.get(7);
    }

    public boolean isStage1Enabled() {
        return (this.data.get(7) & 1) != 0;
    }

    public boolean isStage2Enabled() {
        return (this.data.get(7) & 2) != 0;
    }

    public boolean isStage3Enabled() {
        return (this.data.get(7) & 4) != 0;
    }

    public boolean isAssembled() {
        return this.data.get(8) != 0;
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
                if (slotStack.is(RefineryFluids.CRUDE_OIL_BUCKET.get()) || RefineryControllerBlockEntity.isOilSand(slotStack)) {
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
