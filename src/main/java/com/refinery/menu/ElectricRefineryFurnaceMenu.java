package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.ElectricRefineryFurnaceBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ElectricRefineryFurnaceMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 5;

    private final @Nullable ElectricRefineryFurnaceBlockEntity blockEntity;
    private final ContainerData data;

    public ElectricRefineryFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainerData(DATA_COUNT));
    }

    public ElectricRefineryFurnaceMenu(int containerId, Inventory playerInventory, @Nullable ElectricRefineryFurnaceBlockEntity blockEntity, ContainerData data) {
        super(RefineryMod.ELECTRIC_REFINERY_FURNACE_MENU.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.blockEntity = blockEntity;
        this.data = data;

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
            if (this.blockEntity != null) {
                this.blockEntity.setRedstoneMode(nextMode);
            }
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getEnergyStored() {
        int low = this.data.get(0) & 0xFFFF;
        int high = this.data.get(1) & 0xFFFF;
        return (high << 16) | low;
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity == null || this.blockEntity.stillValid(player);
    }
}
