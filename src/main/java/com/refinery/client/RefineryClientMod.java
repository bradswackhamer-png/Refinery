package com.refinery.client;

import com.refinery.RefineryMod;
import com.refinery.client.gui.*;
import com.refinery.client.renderer.*;
import com.refinery.client.renderer.entity.PipeGateEntityRenderer;
import com.refinery.client.renderer.entity.PipeWireEntityRenderer;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.joml.Vector4f;

import java.util.function.Supplier;

@Mod(value = RefineryMod.MODID, dist = Dist.CLIENT)
public class RefineryClientMod {

    public RefineryClientMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerClientExtensions);
        modEventBus.addListener(this::registerFluidModels);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register((MenuType) RefineryMod.REFINERY_MENU.get(), RefineryScreen::new);
        event.register((MenuType) RefineryMod.AUTO_PLANTER_MENU.get(), AutoPlanterScreen::new);
        event.register((MenuType) RefineryMod.AUTO_HARVESTER_MENU.get(), AutoHarvesterScreen::new);
        event.register((MenuType) RefineryMod.BLAST_FURNACE_MENU.get(), BlastFurnaceScreen::new);
        event.register((MenuType) RefineryMod.SMALL_BOILER_MENU.get(), SmallBoilerScreen::new);
        event.register((MenuType) RefineryMod.LARGE_BOILER_MENU.get(), LargeBoilerScreen::new);
        event.register((MenuType) RefineryMod.SMALL_DIESEL_BOILER_MENU.get(), SmallDieselBoilerScreen::new);
        event.register((MenuType) RefineryMod.LARGE_DIESEL_BOILER_MENU.get(), LargeDieselBoilerScreen::new);
        event.register((MenuType) RefineryMod.STEAM_TURBINE_MENU.get(), SteamTurbineScreen::new);
        event.register((MenuType) RefineryMod.METAL_PRESS_MENU.get(), MetalPressScreen::new);
        event.register((MenuType) RefineryMod.INDUSTRIAL_GRINDER_MENU.get(), IndustrialGrinderScreen::new);
        event.register((MenuType) RefineryMod.GATE_MENU.get(), GateScreen::new);
        event.register((MenuType) RefineryMod.ARC_MELTER_MENU.get(), ArcMelterScreen::new);
        event.register((MenuType) RefineryMod.CASTING_PRESS_MENU.get(), CastingPressScreen::new);
        event.register((MenuType) RefineryMod.FERMENTER_MENU.get(), FermenterScreen::new);
        event.register((MenuType) RefineryMod.MULTI_SMELTER_MENU.get(), MultiSmelterScreen::new);
        event.register((MenuType) RefineryMod.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
        event.register((MenuType) RefineryMod.COAL_FLUID_PUMP_MENU.get(), CoalFluidPumpScreen::new);
        event.register((MenuType) RefineryMod.DIESEL_FLUID_PUMP_MENU.get(), DieselFluidPumpScreen::new);
        event.register((MenuType) RefineryMod.ELECTRIC_FLUID_PUMP_MENU.get(), ElectricFluidPumpScreen::new);
        event.register((MenuType) RefineryMod.FEEDING_TROUGH_MENU.get(), FeedingTroughScreen::new);
        event.register((MenuType) RefineryMod.STORAGE_TANK_MENU.get(), StorageTankScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RefineryMod.REFINERY_CONTROLLER_BE.get(), RefineryControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.BLAST_FURNACE_CONTROLLER_BE.get(), BlastFurnaceControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.SMALL_BOILER_CONTROLLER_BE.get(), SmallBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.LARGE_BOILER_CONTROLLER_BE.get(), LargeBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER_BE.get(), SmallDieselBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.LARGE_DIESEL_BOILER_CONTROLLER_BE.get(), LargeDieselBoilerControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.STEAM_TURBINE_CONTROLLER_BE.get(), SteamTurbineControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.METAL_PRESS_CONTROLLER_BE.get(), MetalPressControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER_BE.get(), IndustrialGrinderControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.ARC_MELTER_CONTROLLER_BE.get(), ArcMelterControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.CASTING_CHANNEL_BE.get(), CastingChannelRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.CASTING_PRESS_CONTROLLER_BE.get(), CastingPressControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.MULTI_SMELTER_CONTROLLER_BE.get(), MultiSmelterControllerRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.FLUID_PUMP_BE.get(), FluidPumpRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.COAL_FLUID_PUMP_BE.get(), FluidPumpRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.DIESEL_FLUID_PUMP_BE.get(), FluidPumpRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.ELECTRIC_FLUID_PUMP_BE.get(), FluidPumpRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.FLUID_DISCHARGE_BE.get(), FluidDischargeRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.CONVEYOR_BELT_BE.get(), ConveyorBeltRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.FEEDING_TROUGH_BE.get(), FeedingTroughRenderer::new);
        event.registerBlockEntityRenderer(RefineryMod.STORAGE_TANK_DRAIN_VALVE_BE.get(), StorageTankRenderer::new);
        event.registerEntityRenderer(RefineryMod.PIPE_GATE_ENTITY.get(), PipeGateEntityRenderer::new);
        event.registerEntityRenderer(RefineryMod.PIPE_WIRE_ENTITY.get(), PipeWireEntityRenderer::new);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.DIESEL_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.GASOLINE_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.STEAM_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                fluidFogColor.set(0.02f, 0.02f, 0.02f, 1.0f);
            }
        }, RefineryFluids.CRUDE_OIL_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_IRON_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_GOLD_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_COPPER_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_TIN_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_BRONZE_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {}, RefineryFluids.MOLTEN_GLASS_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                fluidFogColor.set(0.92f, 0.94f, 0.96f, 0.85f);
            }
        }, RefineryFluids.ETHANOL_TYPE.get());
    }

    private void registerFluidModels(RegisterFluidModelsEvent event) {
        registerFluidModel(event, "crude_oil", RefineryFluids.CRUDE_OIL_SOURCE, RefineryFluids.CRUDE_OIL_FLOWING);
        registerFluidModel(event, "diesel", RefineryFluids.DIESEL_SOURCE, RefineryFluids.DIESEL_FLOWING);
        registerFluidModel(event, "gasoline", RefineryFluids.GASOLINE_SOURCE, RefineryFluids.GASOLINE_FLOWING);
        registerFluidModel(event, "steam", RefineryFluids.STEAM_SOURCE, RefineryFluids.STEAM_FLOWING);
        registerFluidModel(event, "molten_iron", RefineryFluids.MOLTEN_IRON_SOURCE, RefineryFluids.MOLTEN_IRON_FLOWING);
        registerFluidModel(event, "molten_gold", RefineryFluids.MOLTEN_GOLD_SOURCE, RefineryFluids.MOLTEN_GOLD_FLOWING);
        registerFluidModel(event, "molten_copper", RefineryFluids.MOLTEN_COPPER_SOURCE, RefineryFluids.MOLTEN_COPPER_FLOWING);
        registerFluidModel(event, "molten_tin", RefineryFluids.MOLTEN_TIN_SOURCE, RefineryFluids.MOLTEN_TIN_FLOWING);
        registerFluidModel(event, "molten_bronze", RefineryFluids.MOLTEN_BRONZE_SOURCE, RefineryFluids.MOLTEN_BRONZE_FLOWING);
        registerFluidModel(event, "molten_glass", RefineryFluids.MOLTEN_GLASS_SOURCE, RefineryFluids.MOLTEN_GLASS_FLOWING);
        registerFluidModel(event, "ethanol", RefineryFluids.ETHANOL_SOURCE, RefineryFluids.ETHANOL_FLOWING);
    }

    private void registerFluidModel(RegisterFluidModelsEvent event, String name,
            Supplier<? extends Fluid> still,
            Supplier<? extends Fluid> flowing) {
        event.register(new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath(RefineryMod.MODID, "block/" + name + "_still")),
                new Material(Identifier.fromNamespaceAndPath(RefineryMod.MODID, "block/" + name + "_flow")),
                null,
                null
        ), still, flowing);
    }
}
