package com.refinery.client.gui;

import com.refinery.menu.CoalFluidPumpMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Legacy screen for backwards compatibility.
 */
@OnlyIn(Dist.CLIENT)
public class FluidPumpScreen extends CoalFluidPumpScreen {
    public FluidPumpScreen(CoalFluidPumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
