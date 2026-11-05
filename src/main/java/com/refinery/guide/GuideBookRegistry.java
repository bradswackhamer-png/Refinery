package com.refinery.guide;

import com.refinery.RefineryMod;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class GuideBookRegistry {
    private static final List<GuideEntry> ENTRIES = new ArrayList<>();
    private static final Map<String, GuideEntry> BY_ID = new HashMap<>();
    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;

        // =========================================================================
        // 1. INDUSTRIAL BLAST SMELTER (5x5x5 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Blast Furnace Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.BLAST_FURNACE_CONTROLLER.get())));
            legend.put('I', new LayerSymbol('I', "Foundry Input Port", 0xFF3B82F6, () -> new ItemStack(RefineryMod.FOUNDRY_INPUT_PORT.get())));
            legend.put('O', new LayerSymbol('O', "Foundry Output Port", 0xFFF59E0B, () -> new ItemStack(RefineryMod.FOUNDRY_OUTPUT_PORT.get())));
            legend.put('G', new LayerSymbol('G', "Foundry Heat Glass", 0xFF38BDF8, () -> new ItemStack(RefineryMod.FOUNDRY_GLASS.get())));
            legend.put('X', new LayerSymbol('X', "Foundry Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.BLAST_FURNACE_CASING.get())));
            legend.put('.', new LayerSymbol('.', "Hollow Chamber (Air)", 0xFFEA580C, null));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BLAST_FURNACE_CONTROLLER.get()), "Blast Furnace Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_INPUT_PORT.get()), "Foundry Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_OUTPUT_PORT.get()), "Foundry Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_GLASS.get()), "Foundry Heat-Resistant Glass", 9),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BLAST_FURNACE_CASING.get()), "Foundry Casing", 86)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Base / Foundation)", new String[]{
                            "XXXXX",
                            "XXXXX",
                            "XXXXX",
                            "XXXXX",
                            "XICOX"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Viewing Window & Chamber)", new String[]{
                            "XXXXX",
                            "X...X",
                            "X...X",
                            "X...X",
                            "XGGGX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Middle Chamber)", new String[]{
                            "XXXXX",
                            "X...X",
                            "X...X",
                            "X...X",
                            "XGGGX"
                    }, legend),
                    new GuideLayer(3, "Layer 3 (Upper Chamber)", new String[]{
                            "XXXXX",
                            "X...X",
                            "X...X",
                            "X...X",
                            "XGGGX"
                    }, legend),
                    new GuideLayer(4, "Layer 4 (Roof & Exhaust Cap)", new String[]{
                            "XXXXX",
                            "XXXXX",
                            "XXXXX",
                            "XXXXX",
                            "XXXXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Molten Iron Smelting",
                            List.of(() -> new ItemStack(Items.RAW_IRON), () -> new ItemStack(Items.COAL)),
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_IRON_BUCKET.get())),
                            "Smelts Raw Iron into liquid Molten Iron for casting channels. Generates Slag byproduct."),
                    new GuideRecipe("High-Grade Steel Ingot",
                            List.of(() -> new ItemStack(Items.IRON_INGOT), () -> new ItemStack(RefineryMod.BITUMEN.get())),
                            List.of(() -> new ItemStack(RefineryMod.STEEL_INGOT.get())),
                            "Infuses refined iron with petroleum carbon to produce structural Steel Ingots.")
            );

            register(new GuideEntry(
                    "blast_furnace",
                    "Industrial Blast Smelter",
                    "5x5x5 Multiblock • Heavy Metallurgy",
                    GuideCategory.METALLURGY,
                    () -> new ItemStack(RefineryMod.BLAST_FURNACE_CONTROLLER.get()),
                    "The Industrial Blast Smelter is the primary high-temperature foundry for smelting raw ores into molten liquid metals and alloyed steel. It features a hollow 3x3x3 internal combustion chamber and a 3x3 heat-resistant glass viewing window.\n\nFueled by Coal, Coke, Charcoal, or Bitumen. Outputs molten metal directly into connected Casting Channels or fluid pipes.",
                    "Sneak-right-click the Controller with a Refinery Wrench to toggle an in-world 3D holographic projection of the multiblock!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 2. ELECTRIC ARC MELTER (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Arc Melter Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.ARC_MELTER_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Melter Molten Output Port", 0xFFF97316, () -> new ItemStack(RefineryMod.MELTER_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Melter Feed Input Port", 0xFF3B82F6, () -> new ItemStack(RefineryMod.MELTER_INPUT_PORT.get())));
            legend.put('Q', new LayerSymbol('Q', "Melter Quartz Viewport", 0xFF38BDF8, () -> new ItemStack(RefineryMod.MELTER_CHAMBER.get())));
            legend.put('V', new LayerSymbol('V', "Crucible Bath (or Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Melter Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.MELTER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.ARC_MELTER_CONTROLLER.get()), "Electric Arc Melter Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_OUTPUT_PORT.get()), "Melter Molten Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_INPUT_PORT.get()), "Melter Feed Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_CHAMBER.get()), "Melter Quartz Viewport / Chamber", 2),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_CASING.get()), "Melter Structural Casing", 22)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Spout)", new String[]{
                            "XXX",
                            "XXX",
                            "XCO"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Crucible & Viewport)", new String[]{
                            "XXX",
                            "XVX",
                            "XQX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Overhead Gantry & Feed)", new String[]{
                            "XXX",
                            "XIX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Raw Ore Liquefaction (2x Doubling)",
                            List.of(() -> new ItemStack(Items.RAW_IRON)),
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_IRON_BUCKET.get())),
                            "Liquefies Raw Iron into 200 mB Molten Iron (2x metal yield compared to basic smelting)."),
                    new GuideRecipe("Molten Bronze & Glass Synthesis",
                            List.of(() -> new ItemStack(RefineryMod.BRONZE_DUST.get())),
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_BRONZE_BUCKET.get())),
                            "Melts bronze or raw copper/tin into high-grade liquid casting alloys.")
            );

            register(new GuideEntry(
                    "arc_melter",
                    "Electric Arc Melter",
                    "3x3x3 Multiblock • High-Yield Metallurgy",
                    GuideCategory.METALLURGY,
                    () -> new ItemStack(RefineryMod.ARC_MELTER_CONTROLLER.get()),
                    "The Electric Arc Melter employs massive carbon electrodes to melt ores and scrap metals into molten fluid. It doubles the output of raw ores (yielding 200 mB per raw ore).\n\nRequires RF/FE electrical power. The bottom-front spout pours molten metal into open Casting Channels without pipes.",
                    "Position an open Casting Channel directly in front of the Molten Output Port to channel molten metal into a Continuous Casting Press!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 3. CONTINUOUS CASTING PRESS (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Casting Press Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Casting Discharge Port (Output)", 0xFF38BDF8, () -> new ItemStack(RefineryMod.CASTING_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Casting Fluid Inflow Port (Input)", 0xFFF97316, () -> new ItemStack(RefineryMod.CASTING_INPUT_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Refractory Casting Basin", 0xFFF59E0B, () -> new ItemStack(RefineryMod.CASTING_BASIN.get())));
            legend.put('G', new LayerSymbol('G', "Casting Safety Grille Window", 0xFF38BDF8, () -> new ItemStack(RefineryMod.CASTING_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Stamping Die Stroke (Air/Chamber)", 0xFFEA580C, null));
            legend.put('X', new LayerSymbol('X', "Casting Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.CASTING_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get()), "Casting Press Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_INPUT_PORT.get()), "Casting Fluid Inflow Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_OUTPUT_PORT.get()), "Casting Discharge Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_BASIN.get()), "Refractory Casting Basin", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_CHAMBER.get()), "Casting Safety Grille Window", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_CASING.get()), "Casting Structural Casing", 22)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Basin Bed)", new String[]{
                            "XXX",
                            "XBX",
                            "XCO"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Stamping Die & Inflow)", new String[]{
                            "XIX",
                            "X.X",
                            "XGX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Overhead Hydraulic Cylinder)", new String[]{
                            "XXX",
                            "XXX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Continuous Ingot Casting",
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_IRON_BUCKET.get())),
                            List.of(() -> new ItemStack(Items.IRON_INGOT)),
                            "Consumes 100 mB of molten metal per stroke to cast solidified ingots."),
                    new GuideRecipe("Industrial Plate Stamping",
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_COPPER_BUCKET.get())),
                            List.of(() -> new ItemStack(RefineryMod.COPPER_PLATE.get())),
                            "Casts molten metals directly into high-durability plates and gears.")
            );

            register(new GuideEntry(
                    "casting_press",
                    "Continuous Casting Press",
                    "3x3x3 Multiblock • Automated Metal Casting",
                    GuideCategory.METALLURGY,
                    () -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get()),
                    "The Continuous Casting Press automates the solidification of molten metal into finished goods. Equipped with an animated hydraulic stamping ram, it accepts molten feeds from casting channels, pipes, or buckets and presses them into solid ingots or plates.",
                    "Eject finished ingots directly onto an adjacent conveyor belt placed next to the Casting Discharge Port!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 4. PETROLEUM REFINERY (3x9x3 3-Stage Fractional Distillation Tower)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Refinery Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Combustion Chamber", 0xFFEA580C, () -> new ItemStack(RefineryMod.COMBUSTION_CHAMBER.get())));
            legend.put('I', new LayerSymbol('I', "Crude Oil Port (Input)", 0xFF18181B, () -> new ItemStack(RefineryMod.CRUDE_OIL_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Tar / Byproduct Port (Stage 1 Out)", 0xFF991B1B, () -> new ItemStack(RefineryMod.TAR_PORT.get())));
            legend.put('D', new LayerSymbol('D', "Diesel Port (Stage 2 Out)", 0xFFEAB308, () -> new ItemStack(RefineryMod.DIESEL_PORT.get())));
            legend.put('G', new LayerSymbol('G', "Gas Port (Stage 3 Out)", 0xFF38BDF8, () -> new ItemStack(RefineryMod.GAS_PORT.get())));
            legend.put('H', new LayerSymbol('H', "Heat Exchanger", 0xFF38BDF8, () -> new ItemStack(RefineryMod.HEAT_EXCHANGER.get())));
            legend.put('T', new LayerSymbol('T', "Distillation Column", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.DISTILLATION_COLUMN.get())));
            legend.put('X', new LayerSymbol('X', "Reinforced Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.REINFORCED_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get()), "Refinery Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.COMBUSTION_CHAMBER.get()), "Combustion Chamber", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CRUDE_OIL_PORT.get()), "Crude Oil Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TAR_PORT.get()), "Liquid Tar / Byproduct Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.DIESEL_PORT.get()), "Diesel Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GAS_PORT.get()), "Gas Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.DISTILLATION_COLUMN.get()), "Distillation Column", 8),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.HEAT_EXCHANGER.get()), "Heat Exchanger", 6),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.REINFORCED_CASING.get()), "Reinforced Casing", 61)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Stage 1 - Layer 0 (Base & Controller)", new String[]{
                            "XXX",
                            "IFB",
                            "XCX"
                    }, legend),
                    new GuideLayer(1, "Stage 1 - Layer 1 (350-500°C Tar Zone)", new String[]{
                            "XXX",
                            "HTH",
                            "XXX"
                    }, legend),
                    new GuideLayer(2, "Stage 1 - Layer 2 (Stage 1 Top)", new String[]{
                            "XXX",
                            "XTX",
                            "XXX"
                    }, legend),
                    new GuideLayer(3, "Stage 2 - Layer 3 (Diesel Takeoff)", new String[]{
                            "XDX",
                            "HTH",
                            "XXX"
                    }, legend),
                    new GuideLayer(4, "Stage 2 - Layer 4 (250-350°C Diesel Zone)", new String[]{
                            "XXX",
                            "XTX",
                            "XXX"
                    }, legend),
                    new GuideLayer(5, "Stage 2 - Layer 5 (Stage 2 Top)", new String[]{
                            "XXX",
                            "XTX",
                            "XXX"
                    }, legend),
                    new GuideLayer(6, "Stage 3 - Layer 6 (Gasoline Takeoff)", new String[]{
                            "XGX",
                            "HTH",
                            "XXX"
                    }, legend),
                    new GuideLayer(7, "Stage 3 - Layer 7 (40-110°C Petrol Zone)", new String[]{
                            "XXX",
                            "XTX",
                            "XXX"
                    }, legend),
                    new GuideLayer(8, "Stage 3 - Layer 8 (Condenser Dome)", new String[]{
                            "XXX",
                            "XTX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("All 3 Stages Enabled (1,000 mB Crude)",
                            List.of(() -> new ItemStack(RefineryFluids.CRUDE_OIL_BUCKET.get())),
                            List.of(() -> new ItemStack(RefineryFluids.LIQUID_TAR_BUCKET.get()), () -> new ItemStack(RefineryFluids.DIESEL_BUCKET.get()), () -> new ItemStack(RefineryFluids.GASOLINE_BUCKET.get()), () -> new ItemStack(RefineryMod.BITUMEN.get())),
                            "250 mB Liquid Tar + 250 mB Diesel + 250 mB Gasoline + 25% chance at 1 Bitumen."),
                    new GuideRecipe("Single Stage Enabled (1,000 mB Crude)",
                            List.of(() -> new ItemStack(RefineryFluids.CRUDE_OIL_BUCKET.get())),
                            List.of(() -> new ItemStack(RefineryFluids.LIQUID_TAR_BUCKET.get()), () -> new ItemStack(RefineryFluids.DIESEL_BUCKET.get()), () -> new ItemStack(RefineryFluids.GASOLINE_BUCKET.get())),
                            "Stage 1 only: 500 mB Liquid Tar + 1 Bitumen | Stage 2 only: 500 mB Diesel | Stage 3 only: 500 mB Gasoline."),
                    new GuideRecipe("Two Stages Enabled (1,000 mB Crude)",
                            List.of(() -> new ItemStack(RefineryFluids.CRUDE_OIL_BUCKET.get())),
                            List.of(() -> new ItemStack(RefineryFluids.LIQUID_TAR_BUCKET.get()), () -> new ItemStack(RefineryFluids.DIESEL_BUCKET.get()), () -> new ItemStack(RefineryMod.BITUMEN.get())),
                            "S1+S2: 250 mB Tar + 250 mB Diesel + 1 Bitumen | S1+S3: 250 mB Tar + 250 mB Gas + 1 Bitumen | S2+S3: 250 mB Diesel + 250 mB Gas.")
            );

            register(new GuideEntry(
                    "refinery",
                    "3-Stage Petroleum Refinery",
                    "3x9x3 Multiblock • Three 3x3x3 Stages",
                    GuideCategory.MULTIBLOCKS,
                    () -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get()),
                    "The 3-Stage Petroleum Refinery is a 3x9x3 fractional distillation column divided into three 3x3x3 vertical stages:\n• Bottom Third (Stage 1): Liquid Tar & Bitumen\n• Middle Third (Stage 2): Diesel Fuel\n• Top Third (Stage 3): Gasoline\n\nRequires an external Heat Source (>= 350°C) from either a Burner Refinery Furnace or Electric Refinery Furnace placed adjacent to Stage 1!",
                    "Toggle individual stages ON or OFF inside the Refinery GUI to customize your output ratios per 1,000 mB of Crude Oil!",
                    reqs,
                    layers,
                    recipes
            ));

            register(new GuideEntry(
                    "refinery_furnaces",
                    "Refinery Furnaces (Burner & Electric)",
                    "1x2x1 Heat Sources • 350°C–500°C",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.BURNER_REFINERY_FURNACE.get()),
                    "The 3-Stage Petroleum Refinery requires an adjacent Refinery Furnace at >= 350°C to heat crude oil vapors.\n\n• Burner Refinery Furnace: Burns solid fuels and fuel buckets at 4x rapid combustion speed.\n• Electric Refinery Furnace: Consumes 500 FE/t of high-voltage RF power to heat cleanly without ash.\n\nEach furnace is placed as a single item occupying 2 vertical blocks (1x2x1) and drops 1 item when broken.",
                    "Place your chosen Refinery Furnace directly touching the outside of Stage 1 (bottom 3x3x3 section) of the Refinery Tower!",
                    null,
                    null,
                    List.of()
            ));
        }

        // =========================================================================
        // 5. INDUSTRIAL GRINDER (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Grinder Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Grinder Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.GRINDER_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Grinder Input Port", 0xFFEA580C, () -> new ItemStack(RefineryMod.GRINDER_INPUT_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Grinder Chamber Window", 0xFFF59E0B, () -> new ItemStack(RefineryMod.GRINDER_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Crushing Chamber Interior (Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Grinder Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.GRINDER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get()), "Grinder Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_INPUT_PORT.get()), "Grinder Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_OUTPUT_PORT.get()), "Grinder Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_CHAMBER.get()), "Grinder Chamber Window", 2),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_CASING.get()), "Grinder Structural Casing", 22)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Discharge)", new String[]{
                            "XXX",
                            "XXX",
                            "XCO"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Crushing Chamber)", new String[]{
                            "XXX",
                            "X.X",
                            "XWX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Feed Chute & Hopper)", new String[]{
                            "XXX",
                            "XIX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Ore Pulverization (2x Mineral Dusts)",
                            List.of(() -> new ItemStack(Items.RAW_IRON)),
                            List.of(() -> new ItemStack(RefineryMod.IRON_DUST.get(), 2)),
                            "Crushes raw ores into doubled dusts with a 10% chance of bonus byproduct dusts/gems."),
                    new GuideRecipe("Grain Milling",
                            List.of(() -> new ItemStack(Items.WHEAT)),
                            List.of(() -> new ItemStack(Items.BREAD, 2)),
                            "High-efficiency industrial milling.")
            );

            register(new GuideEntry(
                    "industrial_grinder",
                    "Industrial Rock Crusher",
                    "3x3x3 Multiblock • Ore Pulverizer",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get()),
                    "The Industrial Grinder features massive counter-rotating steel rolls to crush ores, gravel, and stone into doubled mineral dusts and rare byproducts.\n\nAutomatically expels finished dusts onto adjacent conveyor belts!",
                    "Feed ores into the top feed port via hopper or conveyor, and capture output dusts with a flat conveyor on the ground!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 6. INDUSTRIAL METAL PRESS (3x2x2 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Metal Press Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.METAL_PRESS_CONTROLLER.get())));
            legend.put('B', new LayerSymbol('B', "Press Anvil Bed", 0xFFF59E0B, () -> new ItemStack(RefineryMod.PRESS_BED.get())));
            legend.put('H', new LayerSymbol('H', "Press Hydraulic Head", 0xFF3B82F6, () -> new ItemStack(RefineryMod.PRESS_HEAD.get())));
            legend.put('X', new LayerSymbol('X', "Press Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.PRESS_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.METAL_PRESS_CONTROLLER.get()), "Metal Press Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_BED.get()), "Press Anvil Bed", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_HEAD.get()), "Press Hydraulic Head", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_CASING.get()), "Press Structural Casing", 9)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Anvil Bed)", new String[]{
                            "XXX",
                            "XCB"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Hydraulic Head & Frame)", new String[]{
                            "XXX",
                            "XHX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Plate Die Stamping",
                            List.of(() -> new ItemStack(RefineryMod.PLATE_DIE.get()), () -> new ItemStack(RefineryMod.STEEL_INGOT.get())),
                            List.of(() -> new ItemStack(RefineryMod.STEEL_PLATE.get())),
                            "With a Plate Die installed, stamps 1 metal ingot into 1 industrial plate."),
                    new GuideRecipe("Gear Die Stamping",
                            List.of(() -> new ItemStack(RefineryMod.GEAR_DIE.get()), () -> new ItemStack(RefineryMod.STEEL_INGOT.get())),
                            List.of(() -> new ItemStack(RefineryMod.STEEL_GEAR.get())),
                            "With a Gear Die installed, cold-forms 1 metal ingot or plate directly into a high-strength gear."),
                    new GuideRecipe("Corrugated Steel Stamping",
                            List.of(() -> new ItemStack(RefineryMod.CORRUGATED_DIE.get()), () -> new ItemStack(RefineryMod.STEEL_INGOT.get())),
                            List.of(() -> new ItemStack(RefineryMod.CORRUGATED_STEEL_PANEL.get(), 4)),
                            "With a Corrugated Die installed, rolls 1 Steel Ingot into 4 Corrugated Steel Panels (or 1 Iron Ingot into 2 panels)."),
                    new GuideRecipe("Rail Die Stamping",
                            List.of(() -> new ItemStack(RefineryMod.RAIL_DIE.get()), () -> new ItemStack(RefineryMod.STEEL_INGOT.get())),
                            List.of(() -> new ItemStack(Items.RAIL, 4)),
                            "With a Rail Die installed, stamps 1 Steel Ingot into 4 Rails (or 1 Iron Ingot into 2 Rails; Gold Ingot into 2 Powered Rails).")
            );

            register(new GuideEntry(
                    "metal_press",
                    "Hydraulic Metal Press",
                    "3x2x2 Multiblock • Cold-Forming Press",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.METAL_PRESS_CONTROLLER.get()),
                    "The Hydraulic Metal Press is a cold-forming machine that compresses ingots and plates into industrial components.\n\nStamping strictly requires an installed Die (Plate Die, Gear Die, Corrugated Steel Die, or Rail Die) locked into the tooling slot below the ram.",
                    "Place conveyor belts directly aligned with the Anvil Bed for seamless automated pass-through stamping!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 7. INDUSTRIAL MULTI-SMELTER (5x4x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Multi-Smelter Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Smelter Fuel Port", 0xFFEF4444, () -> new ItemStack(RefineryMod.SMELTER_FUEL_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Smelter Input Port", 0xFF3B82F6, () -> new ItemStack(RefineryMod.SMELTER_INPUT_PORT.get())));
            legend.put('O', new LayerSymbol('O', "Smelter Output Port", 0xFFF59E0B, () -> new ItemStack(RefineryMod.SMELTER_OUTPUT_PORT.get())));
            legend.put('G', new LayerSymbol('G', "Smelter Chamber Glass", 0xFF38BDF8, () -> new ItemStack(RefineryMod.SMELTER_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Hollow Chamber (Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Smelter Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.SMELTER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get()), "Multi-Smelter Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_FUEL_PORT.get()), "Smelter Fuel Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_INPUT_PORT.get()), "Smelter Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_OUTPUT_PORT.get()), "Smelter Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_CHAMBER.get()), "Smelter Chamber Glass", 6),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_CASING.get()), "Smelter Casing", 44)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Ports)", new String[]{
                            "XXXXX",
                            "XXXXX",
                            "FICOU" // U is casing
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Lower Chamber & Windows)", new String[]{
                            "XXXXX",
                            "X...X",
                            "XGGGX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Upper Chamber & Windows)", new String[]{
                            "XXXXX",
                            "X...X",
                            "XGGGX"
                    }, legend),
                    new GuideLayer(3, "Layer 3 (Roof & Thermal Crown)", new String[]{
                            "XXXXX",
                            "XXXXX",
                            "XXXXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Parallel 8-Slot Smelting",
                            List.of(() -> new ItemStack(Items.RAW_COPPER, 8), () -> new ItemStack(Items.COAL)),
                            List.of(() -> new ItemStack(Items.COPPER_INGOT, 8)),
                            "Smelts 8 items simultaneously in parallel, consuming fuel evenly across all batches.")
            );

            register(new GuideEntry(
                    "multi_smelter",
                    "Heavy Multi-Smelter",
                    "5x4x3 Multiblock • 8-Batch Heavy Smelter",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get()),
                    "The Industrial Multi-Smelter is an 8-slot heavy batch furnace array. It accepts full stacks of items and smelts all 8 slots in parallel with remarkable thermal efficiency.",
                    "Connect an Input Port on the left and Output Port on the right to fully automate your ore smelting pipeline!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        // =========================================================================
        // 8. STEAM BOILERS & STEAM TURBINE (Thermodynamics & Power)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Boiler Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Boiler Fuel Port", 0xFFEF4444, () -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Boiler Water Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get())));
            legend.put('S', new LayerSymbol('S', "Boiler Steam Port", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Boiler Firebox", 0xFFEA580C, () -> new ItemStack(RefineryMod.BOILER_FIREBOX.get())));
            legend.put('P', new LayerSymbol('P', "Boiler Pipe", 0xFFF59E0B, () -> new ItemStack(RefineryMod.BOILER_PIPE.get())));
            legend.put('X', new LayerSymbol('X', "Steel Boiler Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.STEEL_BOILER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get()), "Small Boiler Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get()), "Boiler Fuel Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get()), "Boiler Water Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get()), "Boiler Steam Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FIREBOX.get()), "Boiler Firebox", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_PIPE.get()), "Boiler Pipe", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.STEEL_BOILER_CASING.get()), "Steel Boiler Casing", 21)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Firebox & Fuel)", new String[]{
                            "XFX",
                            "XBX",
                            "XCX"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Water In & Steam Out)", new String[]{
                            "XSX",
                            "WPX",
                            "XXX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Steam Dome)", new String[]{
                            "XXX",
                            "XXX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("High-Pressure Steam Generation",
                            List.of(() -> new ItemStack(Items.WATER_BUCKET), () -> new ItemStack(Items.COAL)),
                            List.of(() -> new ItemStack(RefineryFluids.STEAM_BUCKET.get())),
                            "Generates 1,000 mB of high-pressure steam from 1,000 mB water under sustained heat.")
            );

            register(new GuideEntry(
                    "boilers",
                    "Steam Boilers (Small & Large)",
                    "3x3x3 & 5x5x5 Multiblock • Steam Generation",
                    GuideCategory.POWER,
                    () -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get()),
                    "Steam Boilers burn solid fuels (or diesel in Diesel Boilers) to flash water into high-pressure steam.\n\nKeep water supplied continuously! If a boiler runs dry while hot, it enters a critical countdown with a loud siren alarm before explosive meltdown.",
                    "Always pipe turbine exhaust water back into the Boiler Water Port to maintain a safe, closed Rankine loop!",
                    reqs,
                    layers,
                    recipes
            ));
        }

        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Turbine Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.STEAM_TURBINE_CONTROLLER.get())));
            legend.put('E', new LayerSymbol('E', "Turbine Energy Output Port", 0xFFF59E0B, () -> new ItemStack(RefineryMod.TURBINE_ENERGY_PORT.get())));
            legend.put('S', new LayerSymbol('S', "Turbine Steam Input Port", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.TURBINE_STEAM_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Turbine Water Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.TURBINE_WATER_PORT.get())));
            legend.put('R', new LayerSymbol('R', "Turbine Rotor Shaft", 0xFFA855F7, () -> new ItemStack(RefineryMod.TURBINE_ROTOR.get())));
            legend.put('X', new LayerSymbol('X', "Turbine Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.TURBINE_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.STEAM_TURBINE_CONTROLLER.get()), "Steam Turbine Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_ENERGY_PORT.get()), "Turbine Energy Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_STEAM_PORT.get()), "Turbine Steam Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_WATER_PORT.get()), "Turbine Water Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_ROTOR.get()), "Turbine Rotor Shaft", 3),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_CASING.get()), "Turbine Casing", 20)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation & Energy)", new String[]{
                            "XWX",
                            "XRX",
                            "XCE"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Steam Inflow & Rotor)", new String[]{
                            "XSX",
                            "XRX",
                            "XXX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (Upper Casing & Rotor Cap)", new String[]{
                            "XXX",
                            "XRX",
                            "XXX"
                    }, legend)
            );

            register(new GuideEntry(
                    "steam_turbine",
                    "Steam Turbine Generator",
                    "3x3x3 Multiblock • Rankine Power Plant",
                    GuideCategory.POWER,
                    () -> new ItemStack(RefineryMod.STEAM_TURBINE_CONTROLLER.get()),
                    "The Steam Turbine Generator expands high-pressure steam across a 3-tier rotor shaft to produce up to 2,048 FE/t of electricity.\n\nIt features an internal surface condenser that recaptures 90% of steam as pure water.",
                    "Route water back to your boiler with an insulated fluid pipe to run continuous, closed-loop zero-loss power generation!",
                    reqs,
                    layers,
                    List.of()
            ));
        }

        // =========================================================================
        // 9. STANDALONE MACHINES: GENERATOR & FERMENTER
        // =========================================================================
        register(new GuideEntry(
                "diesel_generator",
                "Industrial Diesel Generator",
                "1x2x2 Compact Power Plant",
                GuideCategory.POWER,
                () -> new ItemStack(RefineryMod.DIESEL_GENERATOR.get()),
                "A compact 1x2x2 internal combustion generator that runs on Diesel or Ethanol Biofuel. Produces high-voltage RF/FE energy directly from its output terminals.\n\nIdeal for remote drilling sites or powering arc furnaces before steam infrastructure is built.",
                "Sneak-click with a wrench to align its electrical output facing toward your energy network.",
                null,
                null,
                List.of()
        ));

        register(new GuideEntry(
                "fermenter",
                "Industrial Fermenter",
                "Biochemical Ethanol Production",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.FERMENTER.get()),
                "The Industrial Fermenter is a biochemical processing vat that digests agricultural crops into Ethanol Biofuel.\n\nAccepts sugar cane, sweet berries, potatoes, apples, and wheat seeds.",
                "Pair with an Automated Harvester for infinite, 100% renewable green electricity!",
                null,
                null,
                List.of(
                        new GuideRecipe("Ethanol Biofuel Fermentation",
                                List.of(() -> new ItemStack(Items.SUGAR_CANE, 4)),
                                List.of(() -> new ItemStack(RefineryFluids.ETHANOL_BUCKET.get())),
                                "Ferments organic biomass into 250 mB of high-octane Ethanol liquid fuel.")
                )
        ));

        register(new GuideEntry(
                "agricultural",
                "Automated Harvester & Planter",
                "Autonomous Crop & Tree Farming",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.AUTO_HARVESTER.get()),
                "The Auto Planter and Auto Harvester automate agricultural cultivation in up to a 9x9 field.\n\nAuto Harvester cuts down mature crops and timber trees automatically, routing harvested goods to its internal storage.\n\nThe Feeding Trough keeps animals fed and bred autonomously.",
                "Attach an extraction pipe with an autarchic pulse gate to automatically draw wheat, seeds, and logs into your storage network!",
                null,
                null,
                List.of()
        ));

        // =========================================================================
        // 10. LOGISTICS: CONVEYORS & STORAGE TANKS
        // =========================================================================
        register(new GuideEntry(
                "conveyors",
                "Industrial Conveyor Belts",
                "Smooth In-World Item Transport",
                GuideCategory.LOGISTICS,
                () -> new ItemStack(RefineryMod.CONVEYOR_BELT.get()),
                "Conveyor belts provide continuous in-world item transport at 0.1 blocks/tick. Available in Flat, Sloped (Climbing/Descending), and Right-Angle (Corner) varieties.\n\nIncludes universal cross-mod compatibility to pass items seamlessly into other conveyor systems like Ole Saw Mill without bunching or getting stuck.",
                "Items dropped onto belts can never despawn and are picked up instantly by players or funneled into grinders!",
                null,
                null,
                List.of()
        ));

        register(new GuideEntry(
                "storage_tank",
                "Modular Liquid Storage Tanks",
                "Glass-Fused-to-Steel Multiblock Tanks",
                GuideCategory.LOGISTICS,
                () -> new ItemStack(RefineryMod.STORAGE_TANK_DRAIN_VALVE.get()),
                "Modular liquid storage tanks holding from 576,000 mB up to 4,000,000+ mB of any liquid.\n\nFeatures integrated ladder climbing, lower drain valve, upper fill valve, and top overflow vent.",
                "Build sizes: 3x3x4, 5x5x6, or 7x7x8 using Storage Tank Casings and Valves.",
                null,
                null,
                List.of()
        ));

        // =========================================================================
        // 11. PIPES, LOGIC GATES & WIRES
        // =========================================================================
        register(new GuideEntry(
                "pipes",
                "Industrial Pipes & Filtering",
                "Fluid, Item, & Logic Piping",
                GuideCategory.PIPES_GATES,
                () -> new ItemStack(RefineryMod.WOOD_ITEM_PIPE.get()),
                "Refinery provides specialized industrial pipes for fluids and items:\n\n• Wood: Filtered Extraction (right-click to open 9-slot filter GUI; pulses strictly with Autarchic Gates!)\n• Iron: Directional one-way valve\n• Gold: High-speed acceleration & priority insertion\n• Diamond: 9-slot directional sorting filter\n• Quartz: Merge pipe (5 inputs -> 1 selected output)\n• Emerald: Round-robin load balancer\n• Valve: Redstone/Gate actuated flow control (Normal: closes on redstone, Inverted: opens on redstone)\n• Obsidian: In-world item suction vacuum\n• Void: Incineration of unwanted byproducts\n• Buffer: 1-slot inline chest pipe",
                "Right-click pipes with a Refinery Wrench to toggle connections or change merger output directions!",
                null,
                null,
                List.of()
        ));

        register(new GuideEntry(
                "pipe_gates",
                "BuildCraft-Style Logic Gates & Wires",
                "Autarchic Pulsars & Conditional Automation",
                GuideCategory.PIPES_GATES,
                () -> new ItemStack(RefineryMod.DIAMOND_AUTARCHIC_GATE_ITEM.get()),
                "Pipe Gates mount directly on pipe faces or chests to execute logic actions based on machine status, inventory space, or network signals.\n\n• Tiers: Basic (1 slot), Iron (2 slots), Gold (4 slots), Diamond (8 slots)\n• Autarchic Gates: Pull 1, 5, or 10 items at a time without engines\n• Pipe Wires: Red, Blue, Green, Yellow wires broadcast conditional signals along the pipe network.",
                "Combine 'Space in Inventory' -> 'Autarchic Pulse (Item x10)' to rapidly restock machines only when they need supplies!",
                null,
                null,
                List.of()
        ));

        register(new GuideEntry(
                "refinery_wrench",
                "Refinery Wrench & Hologram Wand",
                "Essential Engineering Multitool",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.REFINERY_WRENCH.get()),
                "The Refinery Wrench is your indispensable tool for setting up industrial base infrastructure.\n\n• Multiblocks: Sneak-right-click any controller to toggle a luminous 3D holographic projection of the multiblock structure in the world!\n• Pipes: Right-click pipe faces to connect/disconnect branches or cycle Quartz merge outputs.\n• Machines: Right-click pumps or generators to rotate their facing.",
                "Always carry a Refinery Wrench when building complex multiblocks!",
                null,
                null,
                List.of()
        ));
    }

    private static void register(GuideEntry entry) {
        ENTRIES.add(entry);
        BY_ID.put(entry.getId(), entry);
    }

    public static List<GuideEntry> getAllEntries() {
        init();
        return Collections.unmodifiableList(ENTRIES);
    }

    public static List<GuideEntry> getEntriesByCategory(GuideCategory category) {
        init();
        if (category == GuideCategory.ALL) return getAllEntries();
        return ENTRIES.stream().filter(e -> e.getCategory() == category || (category == GuideCategory.MULTIBLOCKS && e.isMultiblock())).toList();
    }

    public static GuideEntry getEntry(String id) {
        init();
        return BY_ID.get(id);
    }
}
