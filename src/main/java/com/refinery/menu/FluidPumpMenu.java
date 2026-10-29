package com.refinery.menu;

import com.refinery.RefineryMod;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

/**
 * Legacy menu for backwards compatibility with the original fluid_pump id.
 * Delegates directly to CoalFluidPumpMenu.
 */
public class FluidPumpMenu extends CoalFluidPumpMenu {
    public FluidPumpMenu(int containerId, Inventory playerInventory) {
        super(containerId, playerInventory);
    }

    public FluidPumpMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(containerId, playerInventory, container, data);
    }
}
