package com.refinery.client;

import com.refinery.RefineryMod;
import com.refinery.client.gui.*;
import com.refinery.client.renderer.*;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@Mod(value = RefineryMod.MODID, dist = Dist.CLIENT)
public class RefineryClientMod {

    public RefineryClientMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerClientExtensions);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register((MenuType) RefineryMod.REFINERY_MENU.get(), RefineryScreen::new);
        event.register((MenuType) RefineryMod.AUTO_PLANTER_MENU.get(), AutoPlanterScreen::new);
        event.register((MenuType) RefineryMod.AUTO_HARVESTER_MENU.get(), AutoHarvesterScreen::new);
        event.register((MenuType) RefineryMod.BLAST_FURNACE_MENU.get(), BlastFurnaceScreen::new);
        event.register((MenuType) RefineryMod.SMALL_BOILER_MENU.get(), SmallBoilerScreen::new);
        event.register((MenuType) RefineryMod.LARGE_BOILER_MENU.get(), LargeBoilerScreen::new);
        event.register((MenuType) RefineryMod.STEAM_TURBINE_MENU.get(), SteamTurbineScreen::new);
        event.register((MenuType) RefineryMod.METAL_PRESS_MENU.get(), MetalPressScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RefineryMod.REFINERY_CONTROLLER_BE.get(), RefineryControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.BLAST_FURNACE_CONTROLLER_BE.get(), BlastFurnaceControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.SMALL_BOILER_CONTROLLER_BE.get(), SmallBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.LARGE_BOILER_CONTROLLER_BE.get(), LargeBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.STEAM_TURBINE_CONTROLLER_BE.get(), SteamTurbineControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.METAL_PRESS_CONTROLLER_BE.get(), MetalPressControllerRenderer::new);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.DIESEL_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.GASOLINE_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.STEAM_TYPE.get());
    }
}
