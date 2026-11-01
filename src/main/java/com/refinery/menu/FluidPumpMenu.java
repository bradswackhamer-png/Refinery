package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FluidPumpMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 8;
    private final ContainerData data;

    public FluidPumpMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(DATA_COUNT));
    }

    public FluidPumpMenu(int containerId, Inventory playerInventory, ContainerData data) {
        super(RefineryMod.FLUID_PUMP_MENU.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;

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
        switch (id) {
            case 0 -> {
                boolean curr = isInfiniteWater();
                this.data.set(5, curr ? 0 : 1);
                return true;
            }
            case 1 -> {
                int next = (getRedstoneMode().ordinal() + 1) % AbstractFluidPumpBlockEntity.RedstoneMode.values().length;
                this.data.set(6, next);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public int getFluidAmount() {
        int low = this.data.get(2) & 0xFFFF;
        int high = this.data.get(3) & 0xFFFF;
        return (high << 16) | low;
    }

    public int getFluidTypeIndex() {
        return this.data.get(4);
    }

    public boolean isInfiniteWater() {
        return this.data.get(5) != 0;
    }

    public AbstractFluidPumpBlockEntity.RedstoneMode getRedstoneMode() {
        int modeIdx = this.data.get(6);
        var modes = AbstractFluidPumpBlockEntity.RedstoneMode.values();
        if (modeIdx >= 0 && modeIdx < modes.length) {
            return modes[modeIdx];
        }
        return AbstractFluidPumpBlockEntity.RedstoneMode.IGNORED;
    }

    public AbstractFluidPumpBlockEntity.PumpStatus getPumpStatus() {
        int statusIdx = this.data.get(7);
        var statuses = AbstractFluidPumpBlockEntity.PumpStatus.values();
        if (statusIdx >= 0 && statusIdx < statuses.length) {
            return statuses[statusIdx];
        }
        return AbstractFluidPumpBlockEntity.PumpStatus.IDLE;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
