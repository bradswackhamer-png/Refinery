package com.refinery.fluid;

import com.refinery.RefineryMod;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class RefineryFluids {

    public static BlockBehaviour.Properties liquidProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .replaceable()
                .liquid()
                .noCollision()
                .strength(100.0f)
                .pushReaction(PushReaction.POPPED)
                .noLootTable()
                .sound(SoundType.EMPTY);
    }

    public static BlockBehaviour.Properties moltenLiquidProperties(MapColor mapColor, int light) {
        return liquidProperties(mapColor).lightLevel(s -> light);
    }

    // === DIESEL ===
    public static final DeferredHolder<FluidType, FluidType> DIESEL_TYPE = RefineryMod.FLUID_TYPES.register("diesel",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(850)
                    .viscosity(1200)
                    .temperature(320)
                    .fallDistanceModifier(0.5F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.diesel"),
                    0.70D, 0.85D, 0.40D, 0.020F));

    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_SOURCE = RefineryMod.FLUIDS.register("diesel",
            () -> new BaseFlowingFluid.Source(RefineryFluids.DIESEL_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_FLOWING = RefineryMod.FLUIDS.register("flowing_diesel",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.DIESEL_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> DIESEL_BLOCK = RefineryMod.BLOCKS.registerBlock("diesel",
            p -> new LiquidBlock(DIESEL_SOURCE.get(), p),
            p -> liquidProperties(MapColor.COLOR_YELLOW));

    public static final DeferredItem<BucketItem> DIESEL_BUCKET = RefineryMod.ITEMS.registerItem("diesel_bucket",
            p -> new BucketItem(DIESEL_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties DIESEL_PROPERTIES = new BaseFlowingFluid.Properties(
            DIESEL_TYPE, DIESEL_SOURCE, DIESEL_FLOWING)
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .block(DIESEL_BLOCK)
            .bucket(DIESEL_BUCKET);

    // === GASOLINE / PETROLEUM GAS ===
    public static final DeferredHolder<FluidType, FluidType> GASOLINE_TYPE = RefineryMod.FLUID_TYPES.register("gasoline",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(720)
                    .viscosity(600)
                    .temperature(295)
                    .fallDistanceModifier(0.5F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.gasoline"),
                    0.80D, 0.85D, 0.50D, 0.025F));

    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_SOURCE = RefineryMod.FLUIDS.register("gasoline",
            () -> new BaseFlowingFluid.Source(RefineryFluids.GASOLINE_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_FLOWING = RefineryMod.FLUIDS.register("flowing_gasoline",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.GASOLINE_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> GASOLINE_BLOCK = RefineryMod.BLOCKS.registerBlock("gasoline",
            p -> new LiquidBlock(GASOLINE_SOURCE.get(), p),
            p -> liquidProperties(MapColor.COLOR_LIGHT_GRAY));

    public static final DeferredItem<BucketItem> GASOLINE_BUCKET = RefineryMod.ITEMS.registerItem("gasoline_bucket",
            p -> new BucketItem(GASOLINE_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties GASOLINE_PROPERTIES = new BaseFlowingFluid.Properties(
            GASOLINE_TYPE, GASOLINE_SOURCE, GASOLINE_FLOWING)
            .slopeFindDistance(5)
            .levelDecreasePerBlock(1)
            .block(GASOLINE_BLOCK)
            .bucket(GASOLINE_BUCKET);

    // === HIGH PRESSURE STEAM ===
    public static final DeferredHolder<FluidType, FluidType> STEAM_TYPE = RefineryMod.FLUID_TYPES.register("steam",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(-500)
                    .viscosity(200)
                    .temperature(450)
                    .canSwim(false)
                    .canDrown(false)
                    .canPushEntity(false)
                    .descriptionId("fluid.refinery.steam"),
                    0.85D, 0.90D, -0.30D, 0.025F));

    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_SOURCE = RefineryMod.FLUIDS.register("steam",
            () -> new BaseFlowingFluid.Source(RefineryFluids.STEAM_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_FLOWING = RefineryMod.FLUIDS.register("flowing_steam",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.STEAM_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> STEAM_BLOCK = RefineryMod.BLOCKS.registerBlock("steam",
            p -> new LiquidBlock(STEAM_SOURCE.get(), p),
            p -> liquidProperties(MapColor.SNOW));

    public static final DeferredItem<BucketItem> STEAM_BUCKET = RefineryMod.ITEMS.registerItem("steam_bucket",
            p -> new BucketItem(STEAM_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties STEAM_PROPERTIES = new BaseFlowingFluid.Properties(
            STEAM_TYPE, STEAM_SOURCE, STEAM_FLOWING)
            .slopeFindDistance(6)
            .levelDecreasePerBlock(2)
            .block(STEAM_BLOCK)
            .bucket(STEAM_BUCKET);

    // === CRUDE OIL ===
    public static final DeferredHolder<FluidType, FluidType> CRUDE_OIL_TYPE = RefineryMod.FLUID_TYPES.register("crude_oil",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(1200)
                    .viscosity(3000)
                    .temperature(300)
                    .fallDistanceModifier(0.3F)
                    .motionScale(0.007D)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.crude_oil"),
                    0.55D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> CRUDE_OIL_SOURCE = RefineryMod.FLUIDS.register("crude_oil",
            () -> new BaseFlowingFluid.Source(RefineryFluids.CRUDE_OIL_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> CRUDE_OIL_FLOWING = RefineryMod.FLUIDS.register("flowing_crude_oil",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.CRUDE_OIL_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> CRUDE_OIL_BLOCK = RefineryMod.BLOCKS.registerBlock("crude_oil",
            p -> new LiquidBlock(CRUDE_OIL_SOURCE.get(), p),
            p -> liquidProperties(MapColor.COLOR_BLACK));

    public static final DeferredItem<BucketItem> CRUDE_OIL_BUCKET = RefineryMod.ITEMS.registerItem("crude_oil_bucket",
            p -> new BucketItem(CRUDE_OIL_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties CRUDE_OIL_PROPERTIES = new BaseFlowingFluid.Properties(
            CRUDE_OIL_TYPE, CRUDE_OIL_SOURCE, CRUDE_OIL_FLOWING)
            .slopeFindDistance(3)
            .levelDecreasePerBlock(2)
            .block(CRUDE_OIL_BLOCK)
            .bucket(CRUDE_OIL_BUCKET);

    // === MOLTEN IRON ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_IRON_TYPE = RefineryMod.FLUID_TYPES.register("molten_iron",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(7000)
                    .viscosity(3000)
                    .temperature(1800)
                    .lightLevel(15)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_iron"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_IRON_SOURCE = RefineryMod.FLUIDS.register("molten_iron",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_IRON_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_IRON_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_iron",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_IRON_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_IRON_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_iron",
            p -> new LiquidBlock(MOLTEN_IRON_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.FIRE, 15));

    public static final DeferredItem<BucketItem> MOLTEN_IRON_BUCKET = RefineryMod.ITEMS.registerItem("molten_iron_bucket",
            p -> new BucketItem(MOLTEN_IRON_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_IRON_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_IRON_TYPE, MOLTEN_IRON_SOURCE, MOLTEN_IRON_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_IRON_BLOCK)
            .bucket(MOLTEN_IRON_BUCKET);

    // === MOLTEN GOLD ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_GOLD_TYPE = RefineryMod.FLUID_TYPES.register("molten_gold",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(17000)
                    .viscosity(2500)
                    .temperature(1350)
                    .lightLevel(15)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_gold"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_GOLD_SOURCE = RefineryMod.FLUIDS.register("molten_gold",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_GOLD_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_GOLD_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_gold",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_GOLD_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_GOLD_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_gold",
            p -> new LiquidBlock(MOLTEN_GOLD_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.GOLD, 15));

    public static final DeferredItem<BucketItem> MOLTEN_GOLD_BUCKET = RefineryMod.ITEMS.registerItem("molten_gold_bucket",
            p -> new BucketItem(MOLTEN_GOLD_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_GOLD_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_GOLD_TYPE, MOLTEN_GOLD_SOURCE, MOLTEN_GOLD_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_GOLD_BLOCK)
            .bucket(MOLTEN_GOLD_BUCKET);

    // === MOLTEN COPPER ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_COPPER_TYPE = RefineryMod.FLUID_TYPES.register("molten_copper",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(8000)
                    .viscosity(2800)
                    .temperature(1350)
                    .lightLevel(15)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_copper"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_COPPER_SOURCE = RefineryMod.FLUIDS.register("molten_copper",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_COPPER_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_COPPER_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_copper",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_COPPER_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_COPPER_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_copper",
            p -> new LiquidBlock(MOLTEN_COPPER_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.COLOR_ORANGE, 15));

    public static final DeferredItem<BucketItem> MOLTEN_COPPER_BUCKET = RefineryMod.ITEMS.registerItem("molten_copper_bucket",
            p -> new BucketItem(MOLTEN_COPPER_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_COPPER_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_COPPER_TYPE, MOLTEN_COPPER_SOURCE, MOLTEN_COPPER_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_COPPER_BLOCK)
            .bucket(MOLTEN_COPPER_BUCKET);

    // === MOLTEN TIN ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_TIN_TYPE = RefineryMod.FLUID_TYPES.register("molten_tin",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(6500)
                    .viscosity(2000)
                    .temperature(800)
                    .lightLevel(13)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_tin"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_TIN_SOURCE = RefineryMod.FLUIDS.register("molten_tin",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_TIN_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_TIN_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_tin",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_TIN_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_TIN_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_tin",
            p -> new LiquidBlock(MOLTEN_TIN_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.METAL, 13));

    public static final DeferredItem<BucketItem> MOLTEN_TIN_BUCKET = RefineryMod.ITEMS.registerItem("molten_tin_bucket",
            p -> new BucketItem(MOLTEN_TIN_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_TIN_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_TIN_TYPE, MOLTEN_TIN_SOURCE, MOLTEN_TIN_FLOWING)
            .slopeFindDistance(3)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_TIN_BLOCK)
            .bucket(MOLTEN_TIN_BUCKET);

    // === MOLTEN BRONZE ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_BRONZE_TYPE = RefineryMod.FLUID_TYPES.register("molten_bronze",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(8000)
                    .viscosity(2700)
                    .temperature(1200)
                    .lightLevel(14)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_bronze"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_BRONZE_SOURCE = RefineryMod.FLUIDS.register("molten_bronze",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_BRONZE_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_BRONZE_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_bronze",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_BRONZE_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_BRONZE_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_bronze",
            p -> new LiquidBlock(MOLTEN_BRONZE_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.COLOR_BROWN, 14));

    public static final DeferredItem<BucketItem> MOLTEN_BRONZE_BUCKET = RefineryMod.ITEMS.registerItem("molten_bronze_bucket",
            p -> new BucketItem(MOLTEN_BRONZE_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_BRONZE_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_BRONZE_TYPE, MOLTEN_BRONZE_SOURCE, MOLTEN_BRONZE_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_BRONZE_BLOCK)
            .bucket(MOLTEN_BRONZE_BUCKET);

    // === MOLTEN GLASS ===
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_GLASS_TYPE = RefineryMod.FLUID_TYPES.register("molten_glass",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(2500)
                    .viscosity(6000)
                    .temperature(1700)
                    .lightLevel(15)
                    .fallDistanceModifier(0.3F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.molten_glass"),
                    0.50D, 0.80D, 0.25D, 0.015F));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_GLASS_SOURCE = RefineryMod.FLUIDS.register("molten_glass",
            () -> new BaseFlowingFluid.Source(RefineryFluids.MOLTEN_GLASS_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_GLASS_FLOWING = RefineryMod.FLUIDS.register("flowing_molten_glass",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.MOLTEN_GLASS_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> MOLTEN_GLASS_BLOCK = RefineryMod.BLOCKS.registerBlock("molten_glass",
            p -> new LiquidBlock(MOLTEN_GLASS_SOURCE.get(), p),
            p -> moltenLiquidProperties(MapColor.NONE, 15));

    public static final DeferredItem<BucketItem> MOLTEN_GLASS_BUCKET = RefineryMod.ITEMS.registerItem("molten_glass_bucket",
            p -> new BucketItem(MOLTEN_GLASS_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties MOLTEN_GLASS_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_GLASS_TYPE, MOLTEN_GLASS_SOURCE, MOLTEN_GLASS_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(MOLTEN_GLASS_BLOCK)
            .bucket(MOLTEN_GLASS_BUCKET);

    // === ETHANOL (BIOFUEL) ===
    public static final DeferredHolder<FluidType, FluidType> ETHANOL_TYPE = RefineryMod.FLUID_TYPES.register("ethanol",
            () -> new RefineryFluidType(FluidType.Properties.create()
                    .density(789)
                    .viscosity(800)
                    .temperature(300)
                    .fallDistanceModifier(0.5F)
                    .canSwim(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .descriptionId("fluid.refinery.ethanol"),
                    0.80D, 0.85D, 0.50D, 0.025F));

    public static final DeferredHolder<Fluid, FlowingFluid> ETHANOL_SOURCE = RefineryMod.FLUIDS.register("ethanol",
            () -> new BaseFlowingFluid.Source(RefineryFluids.ETHANOL_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> ETHANOL_FLOWING = RefineryMod.FLUIDS.register("flowing_ethanol",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.ETHANOL_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> ETHANOL_BLOCK = RefineryMod.BLOCKS.registerBlock("ethanol",
            p -> new LiquidBlock(ETHANOL_SOURCE.get(), p),
            p -> liquidProperties(MapColor.COLOR_LIGHT_GREEN));

    public static final DeferredItem<BucketItem> ETHANOL_BUCKET = RefineryMod.ITEMS.registerItem("ethanol_bucket",
            p -> new BucketItem(ETHANOL_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties ETHANOL_PROPERTIES = new BaseFlowingFluid.Properties(
            ETHANOL_TYPE, ETHANOL_SOURCE, ETHANOL_FLOWING)
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .block(ETHANOL_BLOCK)
            .bucket(ETHANOL_BUCKET);

    public static void init() {
    }
}
