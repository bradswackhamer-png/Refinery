package com.refinery;

import com.mojang.logging.LogUtils;
import com.refinery.block.*;
import com.refinery.block.entity.*;
import com.refinery.fluid.RefineryFluids;
import com.refinery.item.*;
import com.refinery.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import org.slf4j.Logger;

@Mod(RefineryMod.MODID)
public class RefineryMod {
    public static final String MODID = "refinery";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registries
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);

    // === Blocks ===
    public static final DeferredBlock<RefineryControllerBlock> REFINERY_CONTROLLER = BLOCKS.registerBlock("refinery_controller",
            RefineryControllerBlock::new);
    public static final DeferredItem<BlockItem> REFINERY_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("refinery_controller", REFINERY_CONTROLLER);

    public static final DeferredBlock<ReinforcedCasingBlock> REINFORCED_CASING = BLOCKS.registerBlock("reinforced_casing",
            ReinforcedCasingBlock::new);
    public static final DeferredItem<BlockItem> REINFORCED_CASING_ITEM = ITEMS.registerSimpleBlockItem("reinforced_casing", REINFORCED_CASING);

    public static final DeferredBlock<CombustionChamberBlock> COMBUSTION_CHAMBER = BLOCKS.registerBlock("combustion_chamber",
            CombustionChamberBlock::new);
    public static final DeferredItem<BlockItem> COMBUSTION_CHAMBER_ITEM = ITEMS.registerSimpleBlockItem("combustion_chamber", COMBUSTION_CHAMBER);

    public static final DeferredBlock<DistillationColumnBlock> DISTILLATION_COLUMN = BLOCKS.registerBlock("distillation_column",
            DistillationColumnBlock::new);
    public static final DeferredItem<BlockItem> DISTILLATION_COLUMN_ITEM = ITEMS.registerSimpleBlockItem("distillation_column", DISTILLATION_COLUMN);

    public static final DeferredBlock<HeatExchangerBlock> HEAT_EXCHANGER = BLOCKS.registerBlock("heat_exchanger",
            HeatExchangerBlock::new);
    public static final DeferredItem<BlockItem> HEAT_EXCHANGER_ITEM = ITEMS.registerSimpleBlockItem("heat_exchanger", HEAT_EXCHANGER);

    // Ports
    public static final DeferredBlock<RefineryPortBlock> CRUDE_OIL_PORT = BLOCKS.registerBlock("crude_oil_port",
            p -> new RefineryPortBlock(RefineryPortBlock.PortType.CRUDE_OIL_IN, p));
    public static final DeferredItem<BlockItem> CRUDE_OIL_PORT_ITEM = ITEMS.registerSimpleBlockItem("crude_oil_port", CRUDE_OIL_PORT);

    public static final DeferredBlock<RefineryPortBlock> DIESEL_PORT = BLOCKS.registerBlock("diesel_port",
            p -> new RefineryPortBlock(RefineryPortBlock.PortType.DIESEL_OUT, p));
    public static final DeferredItem<BlockItem> DIESEL_PORT_ITEM = ITEMS.registerSimpleBlockItem("diesel_port", DIESEL_PORT);

    public static final DeferredBlock<RefineryPortBlock> GAS_PORT = BLOCKS.registerBlock("gas_port",
            p -> new RefineryPortBlock(RefineryPortBlock.PortType.GAS_OUT, p));
    public static final DeferredItem<BlockItem> GAS_PORT_ITEM = ITEMS.registerSimpleBlockItem("gas_port", GAS_PORT);

    public static final DeferredBlock<RefineryPortBlock> BYPRODUCT_PORT = BLOCKS.registerBlock("byproduct_port",
            p -> new RefineryPortBlock(RefineryPortBlock.PortType.BYPRODUCT_OUT, p));
    public static final DeferredItem<BlockItem> BYPRODUCT_PORT_ITEM = ITEMS.registerSimpleBlockItem("byproduct_port", BYPRODUCT_PORT);

    public static final DeferredBlock<AutoPlanterBlock> AUTO_PLANTER = BLOCKS.registerBlock("auto_planter",
            AutoPlanterBlock::new);
    public static final DeferredItem<BlockItem> AUTO_PLANTER_ITEM = ITEMS.registerSimpleBlockItem("auto_planter", AUTO_PLANTER);

    public static final DeferredBlock<AutoHarvesterBlock> AUTO_HARVESTER = BLOCKS.registerBlock("auto_harvester",
            AutoHarvesterBlock::new);
    public static final DeferredItem<BlockItem> AUTO_HARVESTER_ITEM = ITEMS.registerSimpleBlockItem("auto_harvester", AUTO_HARVESTER);

    // 5x5x5 Blast Smelter Blocks
    public static final DeferredBlock<BlastFurnaceControllerBlock> BLAST_FURNACE_CONTROLLER = BLOCKS.registerBlock("blast_furnace_controller",
            BlastFurnaceControllerBlock::new);
    public static final DeferredItem<BlockItem> BLAST_FURNACE_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("blast_furnace_controller", BLAST_FURNACE_CONTROLLER);

    public static final DeferredBlock<BlastFurnaceCasingBlock> BLAST_FURNACE_CASING = BLOCKS.registerBlock("blast_furnace_casing",
            BlastFurnaceCasingBlock::new);
    public static final DeferredItem<BlockItem> BLAST_FURNACE_CASING_ITEM = ITEMS.registerSimpleBlockItem("blast_furnace_casing", BLAST_FURNACE_CASING);

    public static final DeferredBlock<FoundryGlassBlock> FOUNDRY_GLASS = BLOCKS.registerBlock("foundry_glass",
            FoundryGlassBlock::new);
    public static final DeferredItem<BlockItem> FOUNDRY_GLASS_ITEM = ITEMS.registerSimpleBlockItem("foundry_glass", FOUNDRY_GLASS);

    public static final DeferredBlock<FoundryPortBlock> FOUNDRY_INPUT_PORT = BLOCKS.registerBlock("foundry_input_port",
            p -> new FoundryPortBlock(FoundryPortBlock.PortType.INPUT, p));
    public static final DeferredItem<BlockItem> FOUNDRY_INPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("foundry_input_port", FOUNDRY_INPUT_PORT);

    public static final DeferredBlock<FoundryPortBlock> FOUNDRY_OUTPUT_PORT = BLOCKS.registerBlock("foundry_output_port",
            p -> new FoundryPortBlock(FoundryPortBlock.PortType.OUTPUT, p));
    public static final DeferredItem<BlockItem> FOUNDRY_OUTPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("foundry_output_port", FOUNDRY_OUTPUT_PORT);

    // === Steam Boiler Common Blocks ===
    public static final DeferredBlock<SteelBoilerCasingBlock> STEEL_BOILER_CASING = BLOCKS.registerBlock("steel_boiler_casing",
            SteelBoilerCasingBlock::new);
    public static final DeferredItem<BlockItem> STEEL_BOILER_CASING_ITEM = ITEMS.registerSimpleBlockItem("steel_boiler_casing", STEEL_BOILER_CASING);

    public static final DeferredBlock<BoilerFireboxBlock> BOILER_FIREBOX = BLOCKS.registerBlock("boiler_firebox",
            BoilerFireboxBlock::new);
    public static final DeferredItem<BlockItem> BOILER_FIREBOX_ITEM = ITEMS.registerSimpleBlockItem("boiler_firebox", BOILER_FIREBOX);

    public static final DeferredBlock<BoilerPipeBlock> BOILER_PIPE = BLOCKS.registerBlock("boiler_pipe",
            BoilerPipeBlock::new);
    public static final DeferredItem<BlockItem> BOILER_PIPE_ITEM = ITEMS.registerSimpleBlockItem("boiler_pipe", BOILER_PIPE);

    public static final DeferredBlock<BoilerPortBlock> BOILER_WATER_PORT = BLOCKS.registerBlock("boiler_water_port",
            p -> new BoilerPortBlock(BoilerPortBlock.PortType.WATER_IN, p));
    public static final DeferredItem<BlockItem> BOILER_WATER_PORT_ITEM = ITEMS.registerSimpleBlockItem("boiler_water_port", BOILER_WATER_PORT);

    public static final DeferredBlock<BoilerPortBlock> BOILER_STEAM_PORT = BLOCKS.registerBlock("boiler_steam_port",
            p -> new BoilerPortBlock(BoilerPortBlock.PortType.STEAM_OUT, p));
    public static final DeferredItem<BlockItem> BOILER_STEAM_PORT_ITEM = ITEMS.registerSimpleBlockItem("boiler_steam_port", BOILER_STEAM_PORT);

    public static final DeferredBlock<BoilerPortBlock> BOILER_FUEL_PORT = BLOCKS.registerBlock("boiler_fuel_port",
            p -> new BoilerPortBlock(BoilerPortBlock.PortType.FUEL_IN, p));
    public static final DeferredItem<BlockItem> BOILER_FUEL_PORT_ITEM = ITEMS.registerSimpleBlockItem("boiler_fuel_port", BOILER_FUEL_PORT);

    // === Small Boiler (3x3x3) Controller ===
    public static final DeferredBlock<SmallBoilerControllerBlock> SMALL_BOILER_CONTROLLER = BLOCKS.registerBlock("small_boiler_controller",
            SmallBoilerControllerBlock::new);
    public static final DeferredItem<BlockItem> SMALL_BOILER_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("small_boiler_controller", SMALL_BOILER_CONTROLLER);

    // === Large Boiler (5x5x5) Controller ===
    public static final DeferredBlock<LargeBoilerControllerBlock> LARGE_BOILER_CONTROLLER = BLOCKS.registerBlock("large_boiler_controller",
            LargeBoilerControllerBlock::new);
    public static final DeferredItem<BlockItem> LARGE_BOILER_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("large_boiler_controller", LARGE_BOILER_CONTROLLER);

    // === Steam Turbine (3x3x3) Multiblock ===
    public static final DeferredBlock<TurbineCasingBlock> TURBINE_CASING = BLOCKS.registerBlock("turbine_casing",
            TurbineCasingBlock::new);
    public static final DeferredItem<BlockItem> TURBINE_CASING_ITEM = ITEMS.registerSimpleBlockItem("turbine_casing", TURBINE_CASING);

    public static final DeferredBlock<TurbineRotorBlock> TURBINE_ROTOR = BLOCKS.registerBlock("turbine_rotor",
            TurbineRotorBlock::new);
    public static final DeferredItem<BlockItem> TURBINE_ROTOR_ITEM = ITEMS.registerSimpleBlockItem("turbine_rotor", TURBINE_ROTOR);

    public static final DeferredBlock<TurbinePortBlock> TURBINE_STEAM_PORT = BLOCKS.registerBlock("turbine_steam_port",
            p -> new TurbinePortBlock(TurbinePortBlock.PortType.STEAM_IN, p));
    public static final DeferredItem<BlockItem> TURBINE_STEAM_PORT_ITEM = ITEMS.registerSimpleBlockItem("turbine_steam_port", TURBINE_STEAM_PORT);

    public static final DeferredBlock<TurbinePortBlock> TURBINE_WATER_PORT = BLOCKS.registerBlock("turbine_water_port",
            p -> new TurbinePortBlock(TurbinePortBlock.PortType.WATER_OUT, p));
    public static final DeferredItem<BlockItem> TURBINE_WATER_PORT_ITEM = ITEMS.registerSimpleBlockItem("turbine_water_port", TURBINE_WATER_PORT);

    public static final DeferredBlock<TurbinePortBlock> TURBINE_ENERGY_PORT = BLOCKS.registerBlock("turbine_energy_port",
            p -> new TurbinePortBlock(TurbinePortBlock.PortType.ENERGY_OUT, p));
    public static final DeferredItem<BlockItem> TURBINE_ENERGY_PORT_ITEM = ITEMS.registerSimpleBlockItem("turbine_energy_port", TURBINE_ENERGY_PORT);

    public static final DeferredBlock<SteamTurbineControllerBlock> STEAM_TURBINE_CONTROLLER = BLOCKS.registerBlock("steam_turbine_controller",
            SteamTurbineControllerBlock::new);
    public static final DeferredItem<BlockItem> STEAM_TURBINE_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("steam_turbine_controller", STEAM_TURBINE_CONTROLLER);

    // === Metal Press (2x2x2) Multiblock ===
    public static final DeferredBlock<PressCasingBlock> PRESS_CASING = BLOCKS.registerBlock("press_casing",
            PressCasingBlock::new);
    public static final DeferredItem<BlockItem> PRESS_CASING_ITEM = ITEMS.registerSimpleBlockItem("press_casing", PRESS_CASING);

    public static final DeferredBlock<PressBedBlock> PRESS_BED = BLOCKS.registerBlock("press_bed",
            PressBedBlock::new);
    public static final DeferredItem<BlockItem> PRESS_BED_ITEM = ITEMS.registerSimpleBlockItem("press_bed", PRESS_BED);

    public static final DeferredBlock<PressHeadBlock> PRESS_HEAD = BLOCKS.registerBlock("press_head",
            PressHeadBlock::new);
    public static final DeferredItem<BlockItem> PRESS_HEAD_ITEM = ITEMS.registerSimpleBlockItem("press_head", PRESS_HEAD);

    public static final DeferredBlock<MetalPressControllerBlock> METAL_PRESS_CONTROLLER = BLOCKS.registerBlock("metal_press_controller",
            MetalPressControllerBlock::new);
    public static final DeferredItem<BlockItem> METAL_PRESS_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("metal_press_controller", METAL_PRESS_CONTROLLER);

    // === Items ===
    public static final DeferredItem<SteelIngotItem> STEEL_INGOT = ITEMS.registerItem("steel_ingot",
            SteelIngotItem::new, Item.Properties::new);
    public static final DeferredItem<SteelPlateItem> STEEL_PLATE = ITEMS.registerItem("steel_plate",
            SteelPlateItem::new, Item.Properties::new);
    public static final DeferredItem<TarItem> TAR_ITEM = ITEMS.registerItem("tar",
            TarItem::new, Item.Properties::new);

    public static final DeferredItem<SulfurDustItem> SULFUR_DUST_ITEM = ITEMS.registerItem("sulfur_dust",
            SulfurDustItem::new, Item.Properties::new);

    public static final DeferredItem<RefineryWrenchItem> REFINERY_WRENCH = ITEMS.registerItem("refinery_wrench",
            RefineryWrenchItem::new, Item.Properties::new);

    // === Block Entity Types ===
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RefineryControllerBlockEntity>> REFINERY_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("refinery_controller",
                    () -> new BlockEntityType<>(RefineryControllerBlockEntity::new, REFINERY_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RefineryPortBlockEntity>> REFINERY_PORT_BE =
            BLOCK_ENTITY_TYPES.register("refinery_port",
                    () -> new BlockEntityType<>(RefineryPortBlockEntity::new,
                            CRUDE_OIL_PORT.get(), DIESEL_PORT.get(), GAS_PORT.get(), BYPRODUCT_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoPlanterBlockEntity>> AUTO_PLANTER_BE =
            BLOCK_ENTITY_TYPES.register("auto_planter",
                    () -> new BlockEntityType<>(AutoPlanterBlockEntity::new, AUTO_PLANTER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoHarvesterBlockEntity>> AUTO_HARVESTER_BE =
            BLOCK_ENTITY_TYPES.register("auto_harvester",
                    () -> new BlockEntityType<>(AutoHarvesterBlockEntity::new, AUTO_HARVESTER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlastFurnaceControllerBlockEntity>> BLAST_FURNACE_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("blast_furnace_controller",
                    () -> new BlockEntityType<>(BlastFurnaceControllerBlockEntity::new, BLAST_FURNACE_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FoundryPortBlockEntity>> FOUNDRY_PORT_BE =
            BLOCK_ENTITY_TYPES.register("foundry_port",
                    () -> new BlockEntityType<>(FoundryPortBlockEntity::new, FOUNDRY_INPUT_PORT.get(), FOUNDRY_OUTPUT_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmallBoilerControllerBlockEntity>> SMALL_BOILER_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("small_boiler_controller",
                    () -> new BlockEntityType<>(SmallBoilerControllerBlockEntity::new, SMALL_BOILER_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LargeBoilerControllerBlockEntity>> LARGE_BOILER_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("large_boiler_controller",
                    () -> new BlockEntityType<>(LargeBoilerControllerBlockEntity::new, LARGE_BOILER_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoilerPortBlockEntity>> BOILER_PORT_BE =
            BLOCK_ENTITY_TYPES.register("boiler_port",
                    () -> new BlockEntityType<>(BoilerPortBlockEntity::new,
                            BOILER_WATER_PORT.get(), BOILER_STEAM_PORT.get(), BOILER_FUEL_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SteamTurbineControllerBlockEntity>> STEAM_TURBINE_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("steam_turbine_controller",
                    () -> new BlockEntityType<>(SteamTurbineControllerBlockEntity::new, STEAM_TURBINE_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TurbinePortBlockEntity>> TURBINE_PORT_BE =
            BLOCK_ENTITY_TYPES.register("turbine_port",
                    () -> new BlockEntityType<>(TurbinePortBlockEntity::new,
                            TURBINE_STEAM_PORT.get(), TURBINE_WATER_PORT.get(), TURBINE_ENERGY_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalPressControllerBlockEntity>> METAL_PRESS_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("metal_press_controller",
                    () -> new BlockEntityType<>(MetalPressControllerBlockEntity::new, METAL_PRESS_CONTROLLER.get()));

    // === Menu ===
    public static final DeferredHolder<MenuType<?>, MenuType<RefineryMenu>> REFINERY_MENU =
            MENUS.register("refinery", () -> new MenuType<>(RefineryMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<AutoPlanterMenu>> AUTO_PLANTER_MENU =
            MENUS.register("auto_planter", () -> new MenuType<>(AutoPlanterMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<AutoHarvesterMenu>> AUTO_HARVESTER_MENU =
            MENUS.register("auto_harvester", () -> new MenuType<>(AutoHarvesterMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<BlastFurnaceMenu>> BLAST_FURNACE_MENU =
            MENUS.register("blast_furnace", () -> new MenuType<>(BlastFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<SmallBoilerMenu>> SMALL_BOILER_MENU =
            MENUS.register("small_boiler", () -> new MenuType<>(SmallBoilerMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<LargeBoilerMenu>> LARGE_BOILER_MENU =
            MENUS.register("large_boiler", () -> new MenuType<>(LargeBoilerMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<SteamTurbineMenu>> STEAM_TURBINE_MENU =
            MENUS.register("steam_turbine", () -> new MenuType<>(SteamTurbineMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MetalPressMenu>> METAL_PRESS_MENU =
            MENUS.register("metal_press", () -> new MenuType<>(MetalPressMenu::new, FeatureFlags.DEFAULT_FLAGS));

    // === Creative Tab ===
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REFINERY_TAB = CREATIVE_MODE_TABS.register("refinery_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.refinery"))
                    .icon(() -> REFINERY_CONTROLLER_ITEM.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(REFINERY_CONTROLLER_ITEM.get());
                        output.accept(REINFORCED_CASING_ITEM.get());
                        output.accept(COMBUSTION_CHAMBER_ITEM.get());
                        output.accept(DISTILLATION_COLUMN_ITEM.get());
                        output.accept(HEAT_EXCHANGER_ITEM.get());
                        output.accept(CRUDE_OIL_PORT_ITEM.get());
                        output.accept(DIESEL_PORT_ITEM.get());
                        output.accept(GAS_PORT_ITEM.get());
                        output.accept(BYPRODUCT_PORT_ITEM.get());
                        output.accept(AUTO_PLANTER_ITEM.get());
                        output.accept(AUTO_HARVESTER_ITEM.get());
                        output.accept(BLAST_FURNACE_CONTROLLER_ITEM.get());
                        output.accept(BLAST_FURNACE_CASING_ITEM.get());
                        output.accept(FOUNDRY_GLASS_ITEM.get());
                        output.accept(FOUNDRY_INPUT_PORT_ITEM.get());
                        output.accept(FOUNDRY_OUTPUT_PORT_ITEM.get());
                        output.accept(SMALL_BOILER_CONTROLLER_ITEM.get());
                        output.accept(LARGE_BOILER_CONTROLLER_ITEM.get());
                        output.accept(STEEL_BOILER_CASING_ITEM.get());
                        output.accept(BOILER_FIREBOX_ITEM.get());
                        output.accept(BOILER_PIPE_ITEM.get());
                        output.accept(BOILER_WATER_PORT_ITEM.get());
                        output.accept(BOILER_STEAM_PORT_ITEM.get());
                        output.accept(BOILER_FUEL_PORT_ITEM.get());
                        output.accept(STEAM_TURBINE_CONTROLLER_ITEM.get());
                        output.accept(TURBINE_CASING_ITEM.get());
                        output.accept(TURBINE_ROTOR_ITEM.get());
                        output.accept(TURBINE_STEAM_PORT_ITEM.get());
                        output.accept(TURBINE_WATER_PORT_ITEM.get());
                        output.accept(TURBINE_ENERGY_PORT_ITEM.get());
                        output.accept(METAL_PRESS_CONTROLLER_ITEM.get());
                        output.accept(PRESS_BED_ITEM.get());
                        output.accept(PRESS_HEAD_ITEM.get());
                        output.accept(PRESS_CASING_ITEM.get());
                        output.accept(STEEL_INGOT.get());
                        output.accept(STEEL_PLATE.get());
                        output.accept(REFINERY_WRENCH.get());
                        output.accept(TAR_ITEM.get());
                        output.accept(SULFUR_DUST_ITEM.get());
                        output.accept(RefineryFluids.DIESEL_BUCKET.get());
                        output.accept(RefineryFluids.GASOLINE_BUCKET.get());
                        output.accept(RefineryFluids.STEAM_BUCKET.get());
                    })
                    .build());

    public RefineryMod(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);

        RefineryFluids.init();

        modEventBus.addListener(this::registerCapabilities);

        LOGGER.info("Industrial Refinery Mod initialized for Minecraft 26.3 NeoForge 26.3.0.48!");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Energy capability on Controller
        event.registerBlockEntity(Capabilities.Energy.BLOCK, REFINERY_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        // Fluid capability on Ports
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, REFINERY_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return switch (be.getPortType()) {
                        case CRUDE_OIL_IN -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, RefineryControllerBlockEntity.TANK_CAPACITY) {
                            @Override
                            public int insert(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.fillCrudeOil(amount);
                            }
                        };
                        case DIESEL_OUT -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, RefineryControllerBlockEntity.TANK_CAPACITY) {
                            @Override
                            public int extract(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.drainDiesel(amount);
                            }
                        };
                        case GAS_OUT -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, RefineryControllerBlockEntity.TANK_CAPACITY) {
                            @Override
                            public int extract(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.drainGasoline(amount);
                            }
                        };
                        default -> null;
                    };
                });

        // Item capability on Byproduct Port
        event.registerBlockEntity(Capabilities.Item.BLOCK, REFINERY_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller != null && be.getPortType() == RefineryPortBlock.PortType.BYPRODUCT_OUT) {
                        return VanillaContainerWrapper.of(controller);
                    }
                    return null;
                });

        // Capabilities on Auto Planter (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, AUTO_PLANTER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, AUTO_PLANTER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        // Capabilities on Auto Harvester (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, AUTO_HARVESTER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, AUTO_HARVESTER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        // Capabilities on Blast Furnace Controller & Ports (Item Container for pipes/hoppers)
        event.registerBlockEntity(Capabilities.Item.BLOCK, BLAST_FURNACE_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Item.BLOCK, FOUNDRY_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return VanillaContainerWrapper.of(be);
                });

        // Capabilities on Boiler Controllers (Fuel Container)
        event.registerBlockEntity(Capabilities.Item.BLOCK, SMALL_BOILER_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Item.BLOCK, LARGE_BOILER_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        // Capabilities on Boiler Ports (Water In, Steam Out, Fuel In)
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, BOILER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return switch (be.getPortType()) {
                        case WATER_IN -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, SmallBoilerControllerBlockEntity.TANK_CAPACITY) {
                            @Override
                            public int insert(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.fillWater(amount);
                            }
                        };
                        case STEAM_OUT -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, SmallBoilerControllerBlockEntity.TANK_CAPACITY) {
                            @Override
                            public int extract(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.drainSteam(amount);
                            }
                        };
                        default -> null;
                    };
                });

        event.registerBlockEntity(Capabilities.Item.BLOCK, BOILER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller != null && be.getPortType() == BoilerPortBlock.PortType.FUEL_IN) {
                        return VanillaContainerWrapper.of(controller.getFuelContainer());
                    }
                    return null;
                });

        // Capabilities on Steam Turbine Controller & Ports
        event.registerBlockEntity(Capabilities.Energy.BLOCK, STEAM_TURBINE_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Energy.BLOCK, TURBINE_PORT_BE.get(),
                (be, side) -> be.getEnergyHandler());

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, TURBINE_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return switch (be.getPortType()) {
                        case STEAM_IN -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, SteamTurbineControllerBlockEntity.STEAM_CAPACITY) {
                            @Override
                            public int insert(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.fillSteam(amount);
                            }
                        };
                        case WATER_OUT -> new net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler(1, SteamTurbineControllerBlockEntity.WATER_CAPACITY) {
                            @Override
                            public int extract(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                                return controller.drainWater(amount);
                            }
                        };
                        default -> null;
                    };
                });

        // Capabilities on Metal Press Controller (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, METAL_PRESS_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, METAL_PRESS_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));
    }
}
