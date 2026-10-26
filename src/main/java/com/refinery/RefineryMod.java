package com.refinery;

import com.mojang.logging.LogUtils;
import com.refinery.block.*;
import com.refinery.block.entity.*;
import com.refinery.entity.PipeGateEntity;
import com.refinery.entity.PipeWireEntity;
import com.refinery.fluid.MultiFluidHandler;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import com.refinery.gate.GateTier;
import com.refinery.gate.PipeWireColor;
import com.refinery.item.*;
import com.refinery.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
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
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final java.util.function.Supplier<AttachmentType<Boolean>> OIL_CHUNK_BURNT = ATTACHMENT_TYPES.register(
            "oil_chunk_burnt",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL.fieldOf("burnt")).build()
    );

    public static final DeferredHolder<EntityType<?>, EntityType<PipeGateEntity>> PIPE_GATE_ENTITY =
            ENTITY_TYPES.register("pipe_gate",
                    id -> EntityType.Builder.<PipeGateEntity>of(PipeGateEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<PipeWireEntity>> PIPE_WIRE_ENTITY =
            ENTITY_TYPES.register("pipe_wire",
                    id -> EntityType.Builder.<PipeWireEntity>of(PipeWireEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

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

    // === Industrial Grinder (3x3x3) Multiblock ===
    public static final DeferredBlock<IndustrialGrinderControllerBlock> INDUSTRIAL_GRINDER_CONTROLLER = BLOCKS.registerBlock("industrial_grinder_controller",
            IndustrialGrinderControllerBlock::new);
    public static final DeferredItem<BlockItem> INDUSTRIAL_GRINDER_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("industrial_grinder_controller", INDUSTRIAL_GRINDER_CONTROLLER);

    public static final DeferredBlock<GrinderCasingBlock> GRINDER_CASING = BLOCKS.registerBlock("grinder_casing",
            GrinderCasingBlock::new);
    public static final DeferredItem<BlockItem> GRINDER_CASING_ITEM = ITEMS.registerSimpleBlockItem("grinder_casing", GRINDER_CASING);

    public static final DeferredBlock<GrinderChamberBlock> GRINDER_CHAMBER = BLOCKS.registerBlock("grinder_chamber",
            GrinderChamberBlock::new);
    public static final DeferredItem<BlockItem> GRINDER_CHAMBER_ITEM = ITEMS.registerSimpleBlockItem("grinder_chamber", GRINDER_CHAMBER);

    public static final DeferredBlock<GrinderPortBlock> GRINDER_INPUT_PORT = BLOCKS.registerBlock("grinder_input_port",
            p -> new GrinderPortBlock(GrinderPortBlock.PortType.INPUT, p));
    public static final DeferredItem<BlockItem> GRINDER_INPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("grinder_input_port", GRINDER_INPUT_PORT);

    public static final DeferredBlock<GrinderPortBlock> GRINDER_OUTPUT_PORT = BLOCKS.registerBlock("grinder_output_port",
            p -> new GrinderPortBlock(GrinderPortBlock.PortType.OUTPUT, p));
    public static final DeferredItem<BlockItem> GRINDER_OUTPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("grinder_output_port", GRINDER_OUTPUT_PORT);

    // === Electric Arc Melter Multiblock ===
    public static final DeferredBlock<ArcMelterControllerBlock> ARC_MELTER_CONTROLLER = BLOCKS.registerBlock("arc_melter_controller",
            ArcMelterControllerBlock::new);
    public static final DeferredItem<BlockItem> ARC_MELTER_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("arc_melter_controller", ARC_MELTER_CONTROLLER);

    public static final DeferredBlock<MelterCasingBlock> MELTER_CASING = BLOCKS.registerBlock("melter_casing",
            MelterCasingBlock::new);
    public static final DeferredItem<BlockItem> MELTER_CASING_ITEM = ITEMS.registerSimpleBlockItem("melter_casing", MELTER_CASING);

    public static final DeferredBlock<MelterChamberBlock> MELTER_CHAMBER = BLOCKS.registerBlock("melter_chamber",
            MelterChamberBlock::new);
    public static final DeferredItem<BlockItem> MELTER_CHAMBER_ITEM = ITEMS.registerSimpleBlockItem("melter_chamber", MELTER_CHAMBER);

    public static final DeferredBlock<MelterPortBlock> MELTER_INPUT_PORT = BLOCKS.registerBlock("melter_input_port",
            p -> new MelterPortBlock(MelterPortBlock.PortType.INPUT, p));
    public static final DeferredItem<BlockItem> MELTER_INPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("melter_input_port", MELTER_INPUT_PORT);

    public static final DeferredBlock<MelterPortBlock> MELTER_OUTPUT_PORT = BLOCKS.registerBlock("melter_output_port",
            p -> new MelterPortBlock(MelterPortBlock.PortType.OUTPUT, p));
    public static final DeferredItem<BlockItem> MELTER_OUTPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("melter_output_port", MELTER_OUTPUT_PORT);

    // === Casting Channel ===
    public static final DeferredBlock<CastingChannelBlock> CASTING_CHANNEL = BLOCKS.registerBlock("casting_channel",
            CastingChannelBlock::new);
    public static final DeferredItem<BlockItem> CASTING_CHANNEL_ITEM = ITEMS.registerSimpleBlockItem("casting_channel", CASTING_CHANNEL);

    // === Continuous Casting Press Multiblock ===
    public static final DeferredBlock<CastingPressControllerBlock> CASTING_PRESS_CONTROLLER = BLOCKS.registerBlock("casting_press_controller",
            CastingPressControllerBlock::new);
    public static final DeferredItem<BlockItem> CASTING_PRESS_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("casting_press_controller", CASTING_PRESS_CONTROLLER);

    public static final DeferredBlock<CastingCasingBlock> CASTING_CASING = BLOCKS.registerBlock("casting_casing",
            CastingCasingBlock::new);
    public static final DeferredItem<BlockItem> CASTING_CASING_ITEM = ITEMS.registerSimpleBlockItem("casting_casing", CASTING_CASING);

    public static final DeferredBlock<CastingBasinBlock> CASTING_BASIN = BLOCKS.registerBlock("casting_basin",
            CastingBasinBlock::new);
    public static final DeferredItem<BlockItem> CASTING_BASIN_ITEM = ITEMS.registerSimpleBlockItem("casting_basin", CASTING_BASIN);

    public static final DeferredBlock<CastingChamberBlock> CASTING_CHAMBER = BLOCKS.registerBlock("casting_chamber",
            CastingChamberBlock::new);
    public static final DeferredItem<BlockItem> CASTING_CHAMBER_ITEM = ITEMS.registerSimpleBlockItem("casting_chamber", CASTING_CHAMBER);

    public static final DeferredBlock<CastingPortBlock> CASTING_INPUT_PORT = BLOCKS.registerBlock("casting_input_port",
            p -> new CastingPortBlock(CastingPortBlock.PortType.INPUT, p));
    public static final DeferredItem<BlockItem> CASTING_INPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("casting_input_port", CASTING_INPUT_PORT);

    public static final DeferredBlock<CastingPortBlock> CASTING_OUTPUT_PORT = BLOCKS.registerBlock("casting_output_port",
            p -> new CastingPortBlock(CastingPortBlock.PortType.OUTPUT, p));
    public static final DeferredItem<BlockItem> CASTING_OUTPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("casting_output_port", CASTING_OUTPUT_PORT);

    public static final DeferredBlock<OilSandBlock> OIL_SAND = BLOCKS.registerBlock("oil_sand",
            OilSandBlock::new);
    public static final DeferredItem<BlockItem> OIL_SAND_ITEM = ITEMS.registerSimpleBlockItem("oil_sand", OIL_SAND);

    public static final DeferredBlock<FermenterBlock> FERMENTER = BLOCKS.registerBlock("fermenter",
            FermenterBlock::new);
    public static final DeferredItem<BlockItem> FERMENTER_ITEM = ITEMS.registerSimpleBlockItem("fermenter", FERMENTER);

    // === Industrial Multi-Smelter (5x4x3) Multiblock ===
    public static final DeferredBlock<MultiSmelterControllerBlock> MULTI_SMELTER_CONTROLLER = BLOCKS.registerBlock("multi_smelter_controller",
            MultiSmelterControllerBlock::new);
    public static final DeferredItem<BlockItem> MULTI_SMELTER_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("multi_smelter_controller", MULTI_SMELTER_CONTROLLER);

    public static final DeferredBlock<SmelterCasingBlock> SMELTER_CASING = BLOCKS.registerBlock("smelter_casing",
            SmelterCasingBlock::new);
    public static final DeferredItem<BlockItem> SMELTER_CASING_ITEM = ITEMS.registerSimpleBlockItem("smelter_casing", SMELTER_CASING);

    public static final DeferredBlock<SmelterChamberBlock> SMELTER_CHAMBER = BLOCKS.registerBlock("smelter_chamber",
            SmelterChamberBlock::new);
    public static final DeferredItem<BlockItem> SMELTER_CHAMBER_ITEM = ITEMS.registerSimpleBlockItem("smelter_chamber", SMELTER_CHAMBER);

    public static final DeferredBlock<SmelterPortBlock> SMELTER_INPUT_PORT = BLOCKS.registerBlock("smelter_input_port",
            p -> new SmelterPortBlock(SmelterPortBlock.PortType.INPUT, p));
    public static final DeferredItem<BlockItem> SMELTER_INPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("smelter_input_port", SMELTER_INPUT_PORT);

    public static final DeferredBlock<SmelterPortBlock> SMELTER_OUTPUT_PORT = BLOCKS.registerBlock("smelter_output_port",
            p -> new SmelterPortBlock(SmelterPortBlock.PortType.OUTPUT, p));
    public static final DeferredItem<BlockItem> SMELTER_OUTPUT_PORT_ITEM = ITEMS.registerSimpleBlockItem("smelter_output_port", SMELTER_OUTPUT_PORT);

    public static final DeferredBlock<SmelterPortBlock> SMELTER_FUEL_PORT = BLOCKS.registerBlock("smelter_fuel_port",
            p -> new SmelterPortBlock(SmelterPortBlock.PortType.ENERGY_FUEL, p));
    public static final DeferredItem<BlockItem> SMELTER_FUEL_PORT_ITEM = ITEMS.registerSimpleBlockItem("smelter_fuel_port", SMELTER_FUEL_PORT);

    // === Pipe Gates ===
    public static final DeferredBlock<PipeGateBlock> BASIC_GATE = BLOCKS.registerBlock("basic_gate",
            p -> new PipeGateBlock(GateTier.BASIC, false, p.strength(0.5f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> BASIC_GATE_ITEM = ITEMS.registerItem("basic_gate",
            p -> new PipeGateItem(BASIC_GATE.get(), GateTier.BASIC, false, p));

    public static final DeferredBlock<PipeGateBlock> IRON_GATE = BLOCKS.registerBlock("iron_gate",
            p -> new PipeGateBlock(GateTier.IRON, false, p.strength(0.8f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> IRON_GATE_ITEM = ITEMS.registerItem("iron_gate",
            p -> new PipeGateItem(IRON_GATE.get(), GateTier.IRON, false, p));

    public static final DeferredBlock<PipeGateBlock> GOLD_GATE = BLOCKS.registerBlock("gold_gate",
            p -> new PipeGateBlock(GateTier.GOLD, false, p.strength(0.8f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> GOLD_GATE_ITEM = ITEMS.registerItem("gold_gate",
            p -> new PipeGateItem(GOLD_GATE.get(), GateTier.GOLD, false, p));

    public static final DeferredBlock<PipeGateBlock> DIAMOND_GATE = BLOCKS.registerBlock("diamond_gate",
            p -> new PipeGateBlock(GateTier.DIAMOND, false, p.strength(1.0f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> DIAMOND_GATE_ITEM = ITEMS.registerItem("diamond_gate",
            p -> new PipeGateItem(DIAMOND_GATE.get(), GateTier.DIAMOND, false, p));

    public static final DeferredBlock<PipeGateBlock> BASIC_AUTARCHIC_GATE = BLOCKS.registerBlock("basic_autarchic_gate",
            p -> new PipeGateBlock(GateTier.BASIC, true, p.strength(0.5f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> BASIC_AUTARCHIC_GATE_ITEM = ITEMS.registerItem("basic_autarchic_gate",
            p -> new PipeGateItem(BASIC_AUTARCHIC_GATE.get(), GateTier.BASIC, true, p));

    public static final DeferredBlock<PipeGateBlock> IRON_AUTARCHIC_GATE = BLOCKS.registerBlock("iron_autarchic_gate",
            p -> new PipeGateBlock(GateTier.IRON, true, p.strength(0.8f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> IRON_AUTARCHIC_GATE_ITEM = ITEMS.registerItem("iron_autarchic_gate",
            p -> new PipeGateItem(IRON_AUTARCHIC_GATE.get(), GateTier.IRON, true, p));

    public static final DeferredBlock<PipeGateBlock> GOLD_AUTARCHIC_GATE = BLOCKS.registerBlock("gold_autarchic_gate",
            p -> new PipeGateBlock(GateTier.GOLD, true, p.strength(0.8f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> GOLD_AUTARCHIC_GATE_ITEM = ITEMS.registerItem("gold_autarchic_gate",
            p -> new PipeGateItem(GOLD_AUTARCHIC_GATE.get(), GateTier.GOLD, true, p));

    public static final DeferredBlock<PipeGateBlock> DIAMOND_AUTARCHIC_GATE = BLOCKS.registerBlock("diamond_autarchic_gate",
            p -> new PipeGateBlock(GateTier.DIAMOND, true, p.strength(1.0f).noOcclusion()));
    public static final DeferredItem<PipeGateItem> DIAMOND_AUTARCHIC_GATE_ITEM = ITEMS.registerItem("diamond_autarchic_gate",
            p -> new PipeGateItem(DIAMOND_AUTARCHIC_GATE.get(), GateTier.DIAMOND, true, p));

    // === Gate Upgrades & Wires ===
    public static final DeferredItem<Item> AUTARCHIC_CHIPSET = ITEMS.registerItem("autarchic_chipset",
            Item::new, Item.Properties::new);
    public static final DeferredItem<PipeWireItem> PIPE_WIRE_RED = ITEMS.registerItem("pipe_wire_red",
            props -> new PipeWireItem(PipeWireColor.RED, props), Item.Properties::new);
    public static final DeferredItem<PipeWireItem> PIPE_WIRE_BLUE = ITEMS.registerItem("pipe_wire_blue",
            props -> new PipeWireItem(PipeWireColor.BLUE, props), Item.Properties::new);
    public static final DeferredItem<PipeWireItem> PIPE_WIRE_GREEN = ITEMS.registerItem("pipe_wire_green",
            props -> new PipeWireItem(PipeWireColor.GREEN, props), Item.Properties::new);
    public static final DeferredItem<PipeWireItem> PIPE_WIRE_YELLOW = ITEMS.registerItem("pipe_wire_yellow",
            props -> new PipeWireItem(PipeWireColor.YELLOW, props), Item.Properties::new);

    // === Items ===
    public static final DeferredItem<IronDustItem> IRON_DUST = ITEMS.registerItem("iron_dust",
            IronDustItem::new, Item.Properties::new);
    public static final DeferredItem<GoldDustItem> GOLD_DUST = ITEMS.registerItem("gold_dust",
            GoldDustItem::new, Item.Properties::new);
    public static final DeferredItem<CopperDustItem> COPPER_DUST = ITEMS.registerItem("copper_dust",
            CopperDustItem::new, Item.Properties::new);
    public static final DeferredItem<BitumenItem> BITUMEN = ITEMS.registerItem("bitumen",
            BitumenItem::new, Item.Properties::new);
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

    // === New Modded Materials ===
    public static final DeferredItem<MaterialItem> TIN_DUST = ITEMS.registerItem("tin_dust",
            p -> new MaterialItem(p, "tooltip.refinery.tin_dust.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> TIN_INGOT = ITEMS.registerItem("tin_ingot",
            p -> new MaterialItem(p, "tooltip.refinery.tin_ingot.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> TIN_PLATE = ITEMS.registerItem("tin_plate",
            p -> new MaterialItem(p, "tooltip.refinery.tin_plate.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> BRONZE_DUST = ITEMS.registerItem("bronze_dust",
            p -> new MaterialItem(p, "tooltip.refinery.bronze_dust.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> BRONZE_INGOT = ITEMS.registerItem("bronze_ingot",
            p -> new MaterialItem(p, "tooltip.refinery.bronze_ingot.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> BRONZE_PLATE = ITEMS.registerItem("bronze_plate",
            p -> new MaterialItem(p, "tooltip.refinery.bronze_plate.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> COAL_DUST = ITEMS.registerItem("coal_dust",
            p -> new MaterialItem(p, "tooltip.refinery.coal_dust.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> IRON_PLATE = ITEMS.registerItem("iron_plate",
            p -> new MaterialItem(p, "tooltip.refinery.iron_plate.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> COPPER_PLATE = ITEMS.registerItem("copper_plate",
            p -> new MaterialItem(p, "tooltip.refinery.copper_plate.desc"), Item.Properties::new);
    public static final DeferredItem<MaterialItem> GOLD_PLATE = ITEMS.registerItem("gold_plate",
            p -> new MaterialItem(p, "tooltip.refinery.gold_plate.desc"), Item.Properties::new);

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialGrinderControllerBlockEntity>> INDUSTRIAL_GRINDER_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("industrial_grinder_controller",
                    () -> new BlockEntityType<>(IndustrialGrinderControllerBlockEntity::new, INDUSTRIAL_GRINDER_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrinderPortBlockEntity>> GRINDER_PORT_BE =
            BLOCK_ENTITY_TYPES.register("grinder_port",
                    () -> new BlockEntityType<>(GrinderPortBlockEntity::new,
                            GRINDER_INPUT_PORT.get(), GRINDER_OUTPUT_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeGateBlockEntity>> PIPE_GATE_BE =
            BLOCK_ENTITY_TYPES.register("pipe_gate",
                    () -> new BlockEntityType<>(PipeGateBlockEntity::new,
                            BASIC_GATE.get(), IRON_GATE.get(), GOLD_GATE.get(), DIAMOND_GATE.get(),
                            BASIC_AUTARCHIC_GATE.get(), IRON_AUTARCHIC_GATE.get(), GOLD_AUTARCHIC_GATE.get(), DIAMOND_AUTARCHIC_GATE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcMelterControllerBlockEntity>> ARC_MELTER_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("arc_melter_controller",
                    () -> new BlockEntityType<>(ArcMelterControllerBlockEntity::new, ARC_MELTER_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MelterPortBlockEntity>> MELTER_PORT_BE =
            BLOCK_ENTITY_TYPES.register("melter_port",
                    () -> new BlockEntityType<>(MelterPortBlockEntity::new,
                            MELTER_INPUT_PORT.get(), MELTER_OUTPUT_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingChannelBlockEntity>> CASTING_CHANNEL_BE =
            BLOCK_ENTITY_TYPES.register("casting_channel",
                    () -> new BlockEntityType<>(CastingChannelBlockEntity::new, CASTING_CHANNEL.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingPressControllerBlockEntity>> CASTING_PRESS_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("casting_press_controller",
                    () -> new BlockEntityType<>(CastingPressControllerBlockEntity::new, CASTING_PRESS_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingPortBlockEntity>> CASTING_PORT_BE =
            BLOCK_ENTITY_TYPES.register("casting_port",
                    () -> new BlockEntityType<>(CastingPortBlockEntity::new,
                            CASTING_INPUT_PORT.get(), CASTING_OUTPUT_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermenterBlockEntity>> FERMENTER_BE =
            BLOCK_ENTITY_TYPES.register("fermenter",
                    () -> new BlockEntityType<>(FermenterBlockEntity::new, FERMENTER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiSmelterControllerBlockEntity>> MULTI_SMELTER_CONTROLLER_BE =
            BLOCK_ENTITY_TYPES.register("multi_smelter_controller",
                    () -> new BlockEntityType<>(MultiSmelterControllerBlockEntity::new, MULTI_SMELTER_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelterPortBlockEntity>> SMELTER_PORT_BE =
            BLOCK_ENTITY_TYPES.register("smelter_port",
                    () -> new BlockEntityType<>(SmelterPortBlockEntity::new,
                            SMELTER_INPUT_PORT.get(), SMELTER_OUTPUT_PORT.get(), SMELTER_FUEL_PORT.get()));

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

    public static final DeferredHolder<MenuType<?>, MenuType<IndustrialGrinderMenu>> INDUSTRIAL_GRINDER_MENU =
            MENUS.register("industrial_grinder", () -> new MenuType<>(IndustrialGrinderMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<ArcMelterMenu>> ARC_MELTER_MENU =
            MENUS.register("arc_melter", () -> new MenuType<>(ArcMelterMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<CastingPressMenu>> CASTING_PRESS_MENU =
            MENUS.register("casting_press", () -> new MenuType<>(CastingPressMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<GateMenu>> GATE_MENU =
            MENUS.register("gate_menu", () -> new MenuType<>(GateMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<FermenterMenu>> FERMENTER_MENU =
            MENUS.register("fermenter", () -> new MenuType<>(FermenterMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MultiSmelterMenu>> MULTI_SMELTER_MENU =
            MENUS.register("multi_smelter", () -> new MenuType<>(MultiSmelterMenu::new, FeatureFlags.DEFAULT_FLAGS));

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
                        output.accept(INDUSTRIAL_GRINDER_CONTROLLER_ITEM.get());
                        output.accept(GRINDER_CASING_ITEM.get());
                        output.accept(GRINDER_CHAMBER_ITEM.get());
                        output.accept(GRINDER_INPUT_PORT_ITEM.get());
                        output.accept(GRINDER_OUTPUT_PORT_ITEM.get());
                        output.accept(ARC_MELTER_CONTROLLER_ITEM.get());
                        output.accept(MELTER_CASING_ITEM.get());
                        output.accept(MELTER_CHAMBER_ITEM.get());
                        output.accept(MELTER_INPUT_PORT_ITEM.get());
                        output.accept(MELTER_OUTPUT_PORT_ITEM.get());
                        output.accept(CASTING_CHANNEL_ITEM.get());
                        output.accept(CASTING_PRESS_CONTROLLER_ITEM.get());
                        output.accept(CASTING_CASING_ITEM.get());
                        output.accept(CASTING_BASIN_ITEM.get());
                        output.accept(CASTING_CHAMBER_ITEM.get());
                        output.accept(CASTING_INPUT_PORT_ITEM.get());
                        output.accept(CASTING_OUTPUT_PORT_ITEM.get());
                        output.accept(FERMENTER_ITEM.get());
                        output.accept(MULTI_SMELTER_CONTROLLER_ITEM.get());
                        output.accept(SMELTER_CASING_ITEM.get());
                        output.accept(SMELTER_CHAMBER_ITEM.get());
                        output.accept(SMELTER_INPUT_PORT_ITEM.get());
                        output.accept(SMELTER_OUTPUT_PORT_ITEM.get());
                        output.accept(SMELTER_FUEL_PORT_ITEM.get());
                        output.accept(OIL_SAND_ITEM.get());
                        output.accept(BASIC_GATE_ITEM.get());
                        output.accept(IRON_GATE_ITEM.get());
                        output.accept(GOLD_GATE_ITEM.get());
                        output.accept(DIAMOND_GATE_ITEM.get());
                        output.accept(BASIC_AUTARCHIC_GATE_ITEM.get());
                        output.accept(IRON_AUTARCHIC_GATE_ITEM.get());
                        output.accept(GOLD_AUTARCHIC_GATE_ITEM.get());
                        output.accept(DIAMOND_AUTARCHIC_GATE_ITEM.get());
                        output.accept(AUTARCHIC_CHIPSET.get());
                        output.accept(PIPE_WIRE_RED.get());
                        output.accept(PIPE_WIRE_BLUE.get());
                        output.accept(PIPE_WIRE_GREEN.get());
                        output.accept(PIPE_WIRE_YELLOW.get());
                        output.accept(BITUMEN.get());
                        output.accept(IRON_DUST.get());
                        output.accept(GOLD_DUST.get());
                        output.accept(COPPER_DUST.get());
                        output.accept(STEEL_INGOT.get());
                        output.accept(STEEL_PLATE.get());
                        output.accept(REFINERY_WRENCH.get());
                        output.accept(TAR_ITEM.get());
                        output.accept(SULFUR_DUST_ITEM.get());
                        output.accept(TIN_DUST.get());
                        output.accept(TIN_INGOT.get());
                        output.accept(TIN_PLATE.get());
                        output.accept(BRONZE_DUST.get());
                        output.accept(BRONZE_INGOT.get());
                        output.accept(BRONZE_PLATE.get());
                        output.accept(COAL_DUST.get());
                        output.accept(IRON_PLATE.get());
                        output.accept(COPPER_PLATE.get());
                        output.accept(GOLD_PLATE.get());
                        output.accept(RefineryFluids.CRUDE_OIL_BUCKET.get());
                        output.accept(RefineryFluids.DIESEL_BUCKET.get());
                        output.accept(RefineryFluids.GASOLINE_BUCKET.get());
                        output.accept(RefineryFluids.STEAM_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_IRON_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_GOLD_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_COPPER_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_TIN_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_BRONZE_BUCKET.get());
                        output.accept(RefineryFluids.MOLTEN_GLASS_BUCKET.get());
                        output.accept(RefineryFluids.ETHANOL_BUCKET.get());
                    })
                    .build());

    public RefineryMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register configuration specification
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.LOCAL, com.refinery.config.RefineryConfig.SPEC, "refinery.toml");

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        RefineryFluids.init();

        modEventBus.addListener(this::registerCapabilities);

        // Register game event bus listeners (Retrogen & Commands)
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(com.refinery.worldgen.OilRetrogenHandler::onChunkLoad);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(com.refinery.command.RefineryCommands::registerCommands);

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
                        case CRUDE_OIL_IN -> new SingleFluidHandler(
                                RefineryFluids.CRUDE_OIL_SOURCE::get,
                                controller::getCrudeOil,
                                controller::setCrudeOil,
                                RefineryControllerBlockEntity.TANK_CAPACITY,
                                true, false, controller::setChanged);
                        case DIESEL_OUT -> new SingleFluidHandler(
                                RefineryFluids.DIESEL_SOURCE::get,
                                controller::getDiesel,
                                controller::setDiesel,
                                RefineryControllerBlockEntity.TANK_CAPACITY,
                                false, true, controller::setChanged);
                        case GAS_OUT -> new SingleFluidHandler(
                                RefineryFluids.GASOLINE_SOURCE::get,
                                controller::getGasoline,
                                controller::setGasoline,
                                RefineryControllerBlockEntity.TANK_CAPACITY,
                                false, true, controller::setChanged);
                        default -> null;
                    };
                });

        // Fluid capability directly on Refinery Controller (exposes all 3 tanks!)
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, REFINERY_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new MultiFluidHandler(
                            new SingleFluidHandler(RefineryFluids.CRUDE_OIL_SOURCE::get, be::getCrudeOil, be::setCrudeOil, RefineryControllerBlockEntity.TANK_CAPACITY, true, false, be::setChanged),
                            new SingleFluidHandler(RefineryFluids.DIESEL_SOURCE::get, be::getDiesel, be::setDiesel, RefineryControllerBlockEntity.TANK_CAPACITY, false, true, be::setChanged),
                            new SingleFluidHandler(RefineryFluids.GASOLINE_SOURCE::get, be::getGasoline, be::setGasoline, RefineryControllerBlockEntity.TANK_CAPACITY, false, true, be::setChanged)
                    );
                });

        // Item capability on Ports (Crude Oil Port accepts Oil Sand, Byproduct Port outputs Sand/Tar/Sulfur)
        event.registerBlockEntity(Capabilities.Item.BLOCK, REFINERY_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller != null && (be.getPortType() == RefineryPortBlock.PortType.BYPRODUCT_OUT || be.getPortType() == RefineryPortBlock.PortType.CRUDE_OIL_IN)) {
                        return VanillaContainerWrapper.of(controller);
                    }
                    return null;
                });

        // Capabilities on Auto Planter (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, AUTO_PLANTER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, AUTO_PLANTER_BE.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));

        // Capabilities on Auto Harvester (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, AUTO_HARVESTER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, AUTO_HARVESTER_BE.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));

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
                        case WATER_IN -> new SingleFluidHandler(
                                () -> net.minecraft.world.level.material.Fluids.WATER,
                                controller::getWaterAmount,
                                controller::setWaterAmount,
                                SmallBoilerControllerBlockEntity.TANK_CAPACITY,
                                true, false, () -> {});
                        case STEAM_OUT -> new SingleFluidHandler(
                                RefineryFluids.STEAM_SOURCE::get,
                                controller::getSteamAmount,
                                controller::setSteamAmount,
                                SmallBoilerControllerBlockEntity.TANK_CAPACITY,
                                false, true, () -> {});
                        default -> null;
                    };
                });

        // Capabilities on Boiler Controllers (Expose Water & Steam to Gates / Pipes)
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, SMALL_BOILER_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new MultiFluidHandler(
                            new SingleFluidHandler(() -> net.minecraft.world.level.material.Fluids.WATER, be::getWaterAmount, be::setWaterAmount, SmallBoilerControllerBlockEntity.TANK_CAPACITY, true, false, be::setChanged),
                            new SingleFluidHandler(RefineryFluids.STEAM_SOURCE::get, be::getSteamAmount, be::setSteamAmount, SmallBoilerControllerBlockEntity.TANK_CAPACITY, false, true, be::setChanged)
                    );
                });
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, LARGE_BOILER_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new MultiFluidHandler(
                            new SingleFluidHandler(() -> net.minecraft.world.level.material.Fluids.WATER, be::getWaterAmount, be::setWaterAmount, LargeBoilerControllerBlockEntity.TANK_CAPACITY, true, false, be::setChanged),
                            new SingleFluidHandler(RefineryFluids.STEAM_SOURCE::get, be::getSteamAmount, be::setSteamAmount, LargeBoilerControllerBlockEntity.TANK_CAPACITY, false, true, be::setChanged)
                    );
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
                        case STEAM_IN -> new SingleFluidHandler(
                                RefineryFluids.STEAM_SOURCE::get,
                                controller::getSteamAmount,
                                controller::setSteamAmount,
                                SteamTurbineControllerBlockEntity.STEAM_CAPACITY,
                                true, false, () -> {});
                        case WATER_OUT -> new SingleFluidHandler(
                                () -> net.minecraft.world.level.material.Fluids.WATER,
                                controller::getWaterAmount,
                                controller::setWaterAmount,
                                SteamTurbineControllerBlockEntity.WATER_CAPACITY,
                                false, true, () -> {});
                        default -> null;
                    };
                });

        // Capabilities directly on Steam Turbine Controller
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, STEAM_TURBINE_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new MultiFluidHandler(
                            new SingleFluidHandler(RefineryFluids.STEAM_SOURCE::get, be::getSteamAmount, be::setSteamAmount, SteamTurbineControllerBlockEntity.STEAM_CAPACITY, true, false, be::setChanged),
                            new SingleFluidHandler(() -> net.minecraft.world.level.material.Fluids.WATER, be::getWaterAmount, be::setWaterAmount, SteamTurbineControllerBlockEntity.WATER_CAPACITY, false, true, be::setChanged)
                    );
                });

        // Capabilities on Metal Press Controller (Energy & Item Container)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, METAL_PRESS_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, METAL_PRESS_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        // Capabilities on Industrial Grinder Controller & Ports
        event.registerBlockEntity(Capabilities.Energy.BLOCK, INDUSTRIAL_GRINDER_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, INDUSTRIAL_GRINDER_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Item.BLOCK, GRINDER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return VanillaContainerWrapper.of(be);
                });

        // Capabilities on Electric Arc Melter
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ARC_MELTER_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, ARC_MELTER_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ARC_MELTER_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new SingleFluidHandler(
                            be::getMoltenFluid,
                            be::getMoltenAmount,
                            be::setMoltenAmount,
                            ArcMelterControllerBlockEntity.TANK_CAPACITY,
                            false, true, be::setChanged);
                });

        event.registerBlockEntity(Capabilities.Item.BLOCK, MELTER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null || be.getPortType() != MelterPortBlock.PortType.INPUT) return null;
                    return VanillaContainerWrapper.of(be);
                });

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, MELTER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null || be.getPortType() != MelterPortBlock.PortType.OUTPUT) return null;
                    return new SingleFluidHandler(
                            controller::getMoltenFluid,
                            controller::getMoltenAmount,
                            controller::setMoltenAmount,
                            ArcMelterControllerBlockEntity.TANK_CAPACITY,
                            false, true, controller::setChanged);
                });

        // Capabilities on Casting Channel
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, CASTING_CHANNEL_BE.get(),
                (be, side) -> be.fluidHandler);

        // Capabilities on Continuous Casting Press
        event.registerBlockEntity(Capabilities.Energy.BLOCK, CASTING_PRESS_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, CASTING_PRESS_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, CASTING_PRESS_CONTROLLER_BE.get(),
                (be, side) -> {
                    if (!be.isAssembled()) return null;
                    return new SingleFluidHandler(
                            be::getMoltenFluid,
                            be::getMoltenAmount,
                            be::setMoltenAmount,
                            CastingPressControllerBlockEntity.TANK_CAPACITY,
                            true, false, be::setChanged);
                });

        event.registerBlockEntity(Capabilities.Item.BLOCK, CASTING_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null || be.getPortType() != CastingPortBlock.PortType.OUTPUT) return null;
                    return VanillaContainerWrapper.of(be);
                });

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, CASTING_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null || be.getPortType() != CastingPortBlock.PortType.INPUT) return null;
                    return new SingleFluidHandler(
                            controller::getMoltenFluid,
                            controller::getMoltenAmount,
                            controller::setMoltenAmount,
                            CastingPressControllerBlockEntity.TANK_CAPACITY,
                            true, false, controller::setChanged);
                });

        // Capabilities on Industrial Fermenter (Energy, Fluid Ethanol, Items)
        event.registerBlockEntity(Capabilities.Energy.BLOCK, FERMENTER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK, FERMENTER_BE.get(),
                (be, side) -> be.fluidHandler);

        event.registerBlockEntity(Capabilities.Item.BLOCK, FERMENTER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        // Capabilities on Industrial Multi-Smelter Controller & Ports
        event.registerBlockEntity(Capabilities.Energy.BLOCK, MULTI_SMELTER_CONTROLLER_BE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.Item.BLOCK, MULTI_SMELTER_CONTROLLER_BE.get(),
                (be, side) -> VanillaContainerWrapper.of(be));

        event.registerBlockEntity(Capabilities.Item.BLOCK, SMELTER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null) return null;
                    return VanillaContainerWrapper.of(be);
                });

        event.registerBlockEntity(Capabilities.Energy.BLOCK, SMELTER_PORT_BE.get(),
                (be, side) -> {
                    var controller = be.getController();
                    if (controller == null || be.getPortType() != SmelterPortBlock.PortType.ENERGY_FUEL) return null;
                    return controller.energyStorage;
                });
    }
}
