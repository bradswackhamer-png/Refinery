package com.refinery.fluid;

import com.refinery.RefineryMod;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class RefineryFluids {

    // === DIESEL ===
    public static final DeferredHolder<FluidType, FluidType> DIESEL_TYPE = RefineryMod.FLUID_TYPES.register("diesel",
            () -> new FluidType(FluidType.Properties.create()
                    .density(850)
                    .viscosity(1200)
                    .temperature(320)
                    .descriptionId("fluid.refinery.diesel")));

    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_SOURCE = RefineryMod.FLUIDS.register("diesel",
            () -> new BaseFlowingFluid.Source(RefineryFluids.DIESEL_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> DIESEL_FLOWING = RefineryMod.FLUIDS.register("flowing_diesel",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.DIESEL_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> DIESEL_BLOCK = RefineryMod.BLOCKS.registerBlock("diesel",
            p -> new LiquidBlock(DIESEL_SOURCE.get(), p),
            p -> p.noCollision().strength(100.0f));

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
            () -> new FluidType(FluidType.Properties.create()
                    .density(720)
                    .viscosity(600)
                    .temperature(295)
                    .descriptionId("fluid.refinery.gasoline")));

    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_SOURCE = RefineryMod.FLUIDS.register("gasoline",
            () -> new BaseFlowingFluid.Source(RefineryFluids.GASOLINE_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> GASOLINE_FLOWING = RefineryMod.FLUIDS.register("flowing_gasoline",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.GASOLINE_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> GASOLINE_BLOCK = RefineryMod.BLOCKS.registerBlock("gasoline",
            p -> new LiquidBlock(GASOLINE_SOURCE.get(), p),
            p -> p.noCollision().strength(100.0f));

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
            () -> new FluidType(FluidType.Properties.create()
                    .density(-500)
                    .viscosity(200)
                    .temperature(450)
                    .descriptionId("fluid.refinery.steam")));

    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_SOURCE = RefineryMod.FLUIDS.register("steam",
            () -> new BaseFlowingFluid.Source(RefineryFluids.STEAM_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> STEAM_FLOWING = RefineryMod.FLUIDS.register("flowing_steam",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.STEAM_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> STEAM_BLOCK = RefineryMod.BLOCKS.registerBlock("steam",
            p -> new LiquidBlock(STEAM_SOURCE.get(), p),
            p -> p.noCollision().strength(100.0f));

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
            () -> new FluidType(FluidType.Properties.create()
                    .density(950)
                    .viscosity(3000)
                    .temperature(300)
                    .descriptionId("fluid.refinery.crude_oil")));

    public static final DeferredHolder<Fluid, FlowingFluid> CRUDE_OIL_SOURCE = RefineryMod.FLUIDS.register("crude_oil",
            () -> new BaseFlowingFluid.Source(RefineryFluids.CRUDE_OIL_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> CRUDE_OIL_FLOWING = RefineryMod.FLUIDS.register("flowing_crude_oil",
            () -> new BaseFlowingFluid.Flowing(RefineryFluids.CRUDE_OIL_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> CRUDE_OIL_BLOCK = RefineryMod.BLOCKS.registerBlock("crude_oil",
            p -> new LiquidBlock(CRUDE_OIL_SOURCE.get(), p),
            p -> p.noCollision().strength(100.0f));

    public static final DeferredItem<BucketItem> CRUDE_OIL_BUCKET = RefineryMod.ITEMS.registerItem("crude_oil_bucket",
            p -> new BucketItem(CRUDE_OIL_SOURCE.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final BaseFlowingFluid.Properties CRUDE_OIL_PROPERTIES = new BaseFlowingFluid.Properties(
            CRUDE_OIL_TYPE, CRUDE_OIL_SOURCE, CRUDE_OIL_FLOWING)
            .slopeFindDistance(3)
            .levelDecreasePerBlock(2)
            .block(CRUDE_OIL_BLOCK)
            .bucket(CRUDE_OIL_BUCKET);

    public static void init() {
    }
}
