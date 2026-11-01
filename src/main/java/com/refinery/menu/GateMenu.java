package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.PipeGateBlockEntity;
import com.refinery.gate.GateAction;
import com.refinery.gate.GateHost;
import com.refinery.gate.GateTier;
import com.refinery.gate.GateTrigger;
import com.refinery.gate.PipeWireColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GateMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = PipeGateBlockEntity.DATA_COUNT;

    private final GateHost gateHost;
    private final ContainerData data;

    public GateMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainerData(DATA_COUNT));
    }

    public GateMenu(int containerId, Inventory playerInventory, GateHost gateHost, ContainerData data) {
        super(RefineryMod.GATE_MENU.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.gateHost = gateHost;
        this.data = data;

        // Player Inventory (3 rows of 9 at y: 134)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 134 + row * 18));
            }
        }

        // Player Hotbar (9 slots at y: 192)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 192));
        }

        this.addDataSlots(data);
    }

    public GateTier getTier() {
        int ord = data.get(0);
        if (ord >= 0 && ord < GateTier.values().length) {
            return GateTier.values()[ord];
        }
        return GateTier.BASIC;
    }

    public boolean isAutarchic() {
        return data.get(1) != 0;
    }

    public GateTrigger getTrigger(int slot) {
        if (slot >= 0 && slot < 8) {
            int ord = data.get(2 + slot);
            if (ord >= 0 && ord < GateTrigger.values().length) {
                return GateTrigger.values()[ord];
            }
        }
        return GateTrigger.NONE;
    }

    public GateAction getAction(int slot) {
        if (slot >= 0 && slot < 8) {
            int ord = data.get(10 + slot);
            if (ord >= 0 && ord < GateAction.values().length) {
                return GateAction.values()[ord];
            }
        }
        return GateAction.NONE;
    }

    public boolean isConditionMet(int slot) {
        int bits = data.get(18);
        return (bits & (1 << slot)) != 0;
    }

    public boolean isWireActive(PipeWireColor color) {
        int bits = data.get(19);
        return (bits & (1 << color.ordinal())) != 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 100) {
            int slot = (id - 100) / 4;
            int actionType = (id - 100) % 4;
            if (gateHost != null && slot >= 0 && slot < gateHost.getTier().getSlots()) {
                if (actionType == 0) {
                    gateHost.cycleTriggerCategory(slot, true);
                    return true;
                } else if (actionType == 1) {
                    gateHost.cycleTriggerCategory(slot, false);
                    return true;
                }
            }
            return false;
        }

        int slot = id / 4;
        int actionType = id % 4;

        if (gateHost != null && slot >= 0 && slot < gateHost.getTier().getSlots()) {
            switch (actionType) {
                case 0 -> gateHost.cycleTrigger(slot, true);
                case 1 -> gateHost.cycleTrigger(slot, false);
                case 2 -> gateHost.cycleAction(slot, true);
                case 3 -> gateHost.cycleAction(slot, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
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
        return true;
    }
}
