package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallBoilerControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LargeBoilerMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = LargeBoilerControllerBlockEntity.FUEL_SLOTS_COUNT; // 8
    public static final int DATA_COUNT = 7;

    private final Container container;
    private final ContainerData data;

    public LargeBoilerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public LargeBoilerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.LARGE_BOILER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;

        // 8 Fuel buffer slots (2 rows of 4 at center)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 4; c++) {
                int slotIndex = c + r * 4;
                this.addSlot(new Slot(container, slotIndex, 53 + c * 18, 26 + r * 18));
            }
        }

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

    public int getWaterAmount() {
        return this.data.get(0);
    }

    public int getSteamAmount() {
        return this.data.get(1);
    }

    public int getBurnTime() {
        return this.data.get(2);
    }

    public int getMaxBurnTime() {
        return this.data.get(3);
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

    public boolean isBurning() {
        return getBurnTime() > 0 && hasRedstone();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                // From boiler to player inventory
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player to boiler fuel slot
                if (SmallBoilerControllerBlockEntity.getFuelDuration(slotStack) > 0) {
                    if (!this.moveItemStackTo(slotStack, 0, CONTAINER_SIZE, false)) {
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
