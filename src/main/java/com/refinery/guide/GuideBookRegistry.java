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
            legend.put('G', new LayerSymbol('G', "Foundry Heat-Resistant Glass", 0xFF38BDF8, () -> new ItemStack(RefineryMod.FOUNDRY_GLASS.get())));
            legend.put('X', new LayerSymbol('X', "Blast Furnace Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.BLAST_FURNACE_CASING.get())));
            legend.put('.', new LayerSymbol('.', "Hollow Chamber (Air)", 0xFFEA580C, null));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BLAST_FURNACE_CONTROLLER.get()), "Blast Furnace Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_INPUT_PORT.get()), "Foundry Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_OUTPUT_PORT.get()), "Foundry Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.FOUNDRY_GLASS.get()), "Foundry Heat-Resistant Glass", 9),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BLAST_FURNACE_CASING.get()), "Blast Furnace Casing", 86)
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
                    "The Industrial Blast Smelter is the primary high-temperature foundry for smelting raw ores into molten liquid metals and alloyed steel. It features a hollow 3x3x3 internal combustion chamber and a 3x3 heat-resistant glass viewing window.\n\n" +
                    "• Power Consumption: 0 FE/t (Powered strictly by solid fuels)\n" +
                    "• Supported Fuels: Coal, Charcoal, Coke, or Bitumen (1 fuel unit per operation, 9 per Coal Block)\n" +
                    "• Outputs: Molten metal (100 mB per raw ore) poured directly into connected Casting Channels or fluid pipes, plus Slag byproduct.",
                    "Sneak-right-click the Controller with a Refinery Wrench to toggle an in-world 3D holographic projection of the multiblock!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power: Unpowered (Consumes Solid Fuel: Coal, Charcoal, Bitumen; 0 FE/t)"
            ));
        }

        // =========================================================================
        // 2. ELECTRIC ARC MELTER (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Electric Arc Melter Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.ARC_MELTER_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Arc Melter Output Port", 0xFFF97316, () -> new ItemStack(RefineryMod.MELTER_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Arc Melter Input Port", 0xFF3B82F6, () -> new ItemStack(RefineryMod.MELTER_INPUT_PORT.get())));
            legend.put('Q', new LayerSymbol('Q', "Arc Melter Heating Chamber", 0xFF38BDF8, () -> new ItemStack(RefineryMod.MELTER_CHAMBER.get())));
            legend.put('V', new LayerSymbol('V', "Crucible Bath (or Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Arc Melter Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.MELTER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.ARC_MELTER_CONTROLLER.get()), "Electric Arc Melter Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_OUTPUT_PORT.get()), "Arc Melter Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_INPUT_PORT.get()), "Arc Melter Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_CHAMBER.get()), "Arc Melter Heating Chamber", 2),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MELTER_CASING.get()), "Arc Melter Casing", 22)
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
                            "Liquefies Raw Iron into 2,000 mB Molten Iron (2x metal yield compared to basic smelting)."),
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
                    "The Electric Arc Melter employs massive carbon electrodes to melt ores and scrap metals into molten fluid. It doubles the output of raw ores (yielding 2,000 mB per raw ore).\n\n" +
                    "• Power Consumption: 10,000 FE/t during active melting (2,000,000 FE buffer)\n" +
                    "• Cycle Time: 10.0 seconds (200 ticks) per batch\n" +
                    "• Energy Storage Buffer: 2,000,000 FE (Max input rate: 100,000 FE/t)\n" +
                    "• Molten Discharge: Bottom-front spout pours molten metal directly into open Casting Channels without pipes.\n" +
                    "• Top Hopper Funnel: Items dropped into the overhead gantry opening are suctioned directly into the crucible.",
                    "Position an open Casting Channel directly in front of the Arc Melter Output Port to channel molten metal into a Continuous Casting Press!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 10,000 FE/t (Buffer: 2,000,000 FE | Max In: 100,000 FE/t)"
            ));
        }

        // =========================================================================
        // 3. CONTINUOUS CASTING PRESS (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Casting Press Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Casting Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.CASTING_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Casting Input Port", 0xFFF97316, () -> new ItemStack(RefineryMod.CASTING_INPUT_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Casting Mold Basin", 0xFFF59E0B, () -> new ItemStack(RefineryMod.CASTING_BASIN.get())));
            legend.put('G', new LayerSymbol('G', "Casting Safety Chamber", 0xFF38BDF8, () -> new ItemStack(RefineryMod.CASTING_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Stamping Die Stroke (Air/Chamber)", 0xFFEA580C, null));
            legend.put('X', new LayerSymbol('X', "Casting Press Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.CASTING_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get()), "Casting Press Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_INPUT_PORT.get()), "Casting Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_OUTPUT_PORT.get()), "Casting Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_BASIN.get()), "Casting Mold Basin", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_CHAMBER.get()), "Casting Safety Chamber", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CASTING_CASING.get()), "Casting Press Casing", 22)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation, Inflow & Basin Bed)", new String[]{
                            "XIX",
                            "XBX",
                            "XCO"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Stamping Die & Safety Chamber)", new String[]{
                            "XXX",
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
                    new GuideRecipe("Continuous Ingot Casting (1:1 Water Quench)",
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_IRON_BUCKET.get()), () -> new ItemStack(Items.WATER_BUCKET)),
                            List.of(() -> new ItemStack(Items.IRON_INGOT)),
                            "Consumes 1,000 mB of molten metal and 1,000 mB of Water (1:1 ratio) per stroke to cast solidified ingots."),
                    new GuideRecipe("Glass Block Casting (1:1 Water Quench)",
                            List.of(() -> new ItemStack(RefineryFluids.MOLTEN_GLASS_BUCKET.get()), () -> new ItemStack(Items.WATER_BUCKET)),
                            List.of(() -> new ItemStack(Items.GLASS)),
                            "Quenches 1,000 mB of Molten Glass with 1,000 mB of Water into crystal-clear Glass blocks.")
            );

            register(new GuideEntry(
                    "casting_press",
                    "Continuous Casting Press",
                    "3x3x3 Multiblock • Automated Metal Casting",
                    GuideCategory.METALLURGY,
                    () -> new ItemStack(RefineryMod.CASTING_PRESS_CONTROLLER.get()),
                    "The Continuous Casting Press automates the solidification and water-quenching of molten metal into finished ingots. Equipped with an animated hydraulic stamping ram, its ground-level Casting Input Port at (0, 0, 2) accepts molten feeds directly from ground-level Casting Channels, pipes, or buckets.\n\n" +
                    "• Power Consumption: 600 FE/t while hydraulic stamping ram is operating (240,000 FE total per 400-tick / 20.0s stroke)\n" +
                    "• Cycle Time: 20.0 seconds (400 ticks) per stroke\n" +
                    "• Energy Storage Buffer: 500,000 FE (Max input rate: 20,000 FE/t)\n" +
                    "• Fluid Consumption (1:1 Ratio): Requires 1,000 mB of Molten Metal AND 1,000 mB of Water per casting stroke (4,000 mB molten capacity + 4,000 mB water capacity).",
                    "Supply cooling water via fluid pipes or buckets to any casing or input port, and run a ground-level Casting Channel straight into the rear Casting Input Port!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 600 FE/t (Buffer: 500,000 FE | Requires 1:1 Water & Molten Metal)"
            ));
        }

        // =========================================================================
        // 4. PETROLEUM REFINERY (3x9x3 3-Stage Fractional Distillation Tower)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Refinery Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Byproduct Output Port", 0xFFEA580C, () -> new ItemStack(RefineryMod.COMBUSTION_CHAMBER.get())));
            legend.put('I', new LayerSymbol('I', "Crude Oil Input Port", 0xFF18181B, () -> new ItemStack(RefineryMod.CRUDE_OIL_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Liquid Tar Port", 0xFF991B1B, () -> new ItemStack(RefineryMod.TAR_PORT.get())));
            legend.put('D', new LayerSymbol('D', "Diesel Output Port", 0xFFEAB308, () -> new ItemStack(RefineryMod.DIESEL_PORT.get())));
            legend.put('G', new LayerSymbol('G', "Gas Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.GAS_PORT.get())));
            legend.put('H', new LayerSymbol('H', "Refinery Heat Exchanger", 0xFF38BDF8, () -> new ItemStack(RefineryMod.HEAT_EXCHANGER.get())));
            legend.put('T', new LayerSymbol('T', "Distillation Column", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.DISTILLATION_COLUMN.get())));
            legend.put('X', new LayerSymbol('X', "Reinforced Refinery Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.REINFORCED_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get()), "Refinery Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.COMBUSTION_CHAMBER.get()), "Byproduct Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.CRUDE_OIL_PORT.get()), "Crude Oil Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TAR_PORT.get()), "Liquid Tar Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.DIESEL_PORT.get()), "Diesel Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GAS_PORT.get()), "Gas Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.DISTILLATION_COLUMN.get()), "Distillation Column", 8),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.HEAT_EXCHANGER.get()), "Refinery Heat Exchanger", 6),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.REINFORCED_CASING.get()), "Reinforced Refinery Casing", 61)
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
                            "S1+S2: 250 mB Tar + 250 mB Diesel + 1 Bitumen | S1+S3: 250 mB Tar + 250 mB Gas + 1 Bitumen | S2+S3: 250 mB Diesel + 250 mB Gas."),
                    new GuideRecipe("Oil Sand Feedstock Extraction",
                            List.of(() -> new ItemStack(RefineryMod.OIL_SAND.get())),
                            List.of(() -> new ItemStack(RefineryFluids.CRUDE_OIL_BUCKET.get()), () -> new ItemStack(Blocks.SAND)),
                            "When heated (>= 350°C), extracts 200 mB of Crude Oil and returns 1 Sand per Oil Sand block.")
            );

            register(new GuideEntry(
                    "refinery",
                    "3-Stage Petroleum Refinery",
                    "3x9x3 Multiblock • Three 3x3x3 Stages",
                    GuideCategory.MULTIBLOCKS,
                    () -> new ItemStack(RefineryMod.REFINERY_CONTROLLER.get()),
                    "The 3-Stage Petroleum Refinery is a 3x9x3 fractional distillation column divided into three 3x3x3 vertical stages:\n" +
                    "• Stage 1 (Bottom 3x3x3): Liquid Tar & Bitumen (350°C–500°C)\n" +
                    "• Stage 2 (Middle 3x3x3): Diesel Fuel (250°C–350°C)\n" +
                    "• Stage 3 (Top 3x3x3): Gasoline (40°C–110°C)\n\n" +
                    "• Power Consumption: 0 FE/t direct electrical draw (Powered by thermal heat)\n" +
                    "• Heat Requirement: Requires an external Heat Source (>= 350°C) from either an adjacent Burner Refinery Furnace or Electric Refinery Furnace touching Stage 1.\n" +
                    "• Feedstocks: Distills piped/bucketed Crude Oil, or extracts 200 mB Crude Oil + 1 Sand per block of natural Oil Sand!",
                    "Toggle individual stages ON or OFF inside the Refinery GUI to customize your output ratios per 1,000 mB of Crude Oil!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power: Unpowered (Requires Adjacent Furnace Heat >= 350°C; 0 FE/t)"
            ));

            register(new GuideEntry(
                    "refinery_furnaces",
                    "Refinery Furnaces (Burner & Electric)",
                    "1x2x1 Heat Sources • 350°C–500°C Operating Range",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.BURNER_REFINERY_FURNACE.get()),
                    "The 3-Stage Petroleum Refinery requires an adjacent Refinery Furnace at >= 350°C to heat crude oil vapors.\n\n" +
                    "• Electric Refinery Furnace: Consumes 500 FE/t of high-voltage RF power while heating to maintain 350°C–500°C. Internal buffer: 500,000 FE. Clean, soot-free, zero ash.\n" +
                    "• Burner Refinery Furnace: Burns solid fuels (Coal, Charcoal, Bitumen) and fuel buckets at 4x rapid combustion speed. 0 FE/t electrical draw.\n\n" +
                    "Each furnace occupies 2 vertical blocks (1x2x1) and is placed and broken as a single item.",
                    "Place your chosen Refinery Furnace directly touching the outside of Stage 1 (bottom 3x3x3 section) of the Refinery Tower!",
                    null,
                    null,
                    List.of(),
                    "⚡ Electric: 500 FE/t (Buffer: 500,000 FE) | Burner: Solid Fuels (0 FE/t)"
            ));
        }

        // =========================================================================
        // 5. INDUSTRIAL GRINDER (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Industrial Grinder Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get())));
            legend.put('O', new LayerSymbol('O', "Grinder Discharge Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.GRINDER_OUTPUT_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Grinder Feed Input Port", 0xFFEA580C, () -> new ItemStack(RefineryMod.GRINDER_INPUT_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Grinder Crushing Chamber", 0xFFF59E0B, () -> new ItemStack(RefineryMod.GRINDER_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Crushing Chamber Interior (Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Grinder Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.GRINDER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get()), "Industrial Grinder Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_INPUT_PORT.get()), "Grinder Feed Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_OUTPUT_PORT.get()), "Grinder Discharge Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.GRINDER_CHAMBER.get()), "Grinder Crushing Chamber", 2),
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
                    "The Industrial Rock Crusher features massive counter-rotating steel rolls to crush ores, gravel, and stone into doubled mineral dusts and rare byproducts.\n\n" +
                    "• Power Consumption: 2,000 FE/t during active crushing\n" +
                    "• Energy Storage Buffer: 500,000 FE (Max input rate: 50,000 FE/t)\n" +
                    "• Vacuum Intake: Dropped items falling into the top hopper funnel opening are suctioned directly into the crushing chamber.\n" +
                    "• Auto-Export: Finished dusts automatically roll out onto adjacent conveyor belts!",
                    "Feed ores into the top feed port via hopper or conveyor, and capture output dusts with a flat conveyor on the ground!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 2,000 FE/t (Buffer: 500,000 FE | Max In: 50,000 FE/t)"
            ));
        }

        // =========================================================================
        // 6. INDUSTRIAL METAL PRESS (3x2x2 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Industrial Metal Press Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.METAL_PRESS_CONTROLLER.get())));
            legend.put('B', new LayerSymbol('B', "Metal Press Anvil Bed", 0xFFF59E0B, () -> new ItemStack(RefineryMod.PRESS_BED.get())));
            legend.put('H', new LayerSymbol('H', "Metal Press Stamping Head", 0xFF3B82F6, () -> new ItemStack(RefineryMod.PRESS_HEAD.get())));
            legend.put('X', new LayerSymbol('X', "Metal Press Structural Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.PRESS_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.METAL_PRESS_CONTROLLER.get()), "Industrial Metal Press Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_BED.get()), "Metal Press Anvil Bed", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_HEAD.get()), "Metal Press Stamping Head", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.PRESS_CASING.get()), "Metal Press Structural Casing", 9)
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
                    "The Hydraulic Metal Press is a cold-forming machine that compresses ingots and plates into industrial components.\n\n" +
                    "• Power Consumption: 500 FE/t while hydraulic stamping ram is in motion\n" +
                    "• Energy Storage Buffer: 100,000 FE (Max input rate: 20,000 FE/t)\n" +
                    "• Tooling Dies: Requires an installed Die (Plate Die, Gear Die, Corrugated Steel Die, or Rail Die) locked into the tooling slot below the ram.",
                    "Place conveyor belts directly aligned with the Anvil Bed for seamless automated pass-through stamping!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 500 FE/t (Buffer: 100,000 FE | Max In: 20,000 FE/t)"
            ));
        }

        // =========================================================================
        // 7. INDUSTRIAL MULTI-SMELTER (5x4x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Multi-Smelter Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Multi-Smelter Fuel Port", 0xFFEF4444, () -> new ItemStack(RefineryMod.SMELTER_FUEL_PORT.get())));
            legend.put('I', new LayerSymbol('I', "Multi-Smelter Input Port", 0xFF3B82F6, () -> new ItemStack(RefineryMod.SMELTER_INPUT_PORT.get())));
            legend.put('O', new LayerSymbol('O', "Multi-Smelter Output Port", 0xFFF59E0B, () -> new ItemStack(RefineryMod.SMELTER_OUTPUT_PORT.get())));
            legend.put('G', new LayerSymbol('G', "Multi-Smelter Heating Chamber", 0xFF38BDF8, () -> new ItemStack(RefineryMod.SMELTER_CHAMBER.get())));
            legend.put('.', new LayerSymbol('.', "Hollow Chamber (Air)", 0xFFF59E0B, null));
            legend.put('X', new LayerSymbol('X', "Multi-Smelter Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.SMELTER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get()), "Multi-Smelter Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_FUEL_PORT.get()), "Multi-Smelter Fuel Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_INPUT_PORT.get()), "Multi-Smelter Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_OUTPUT_PORT.get()), "Multi-Smelter Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_CHAMBER.get()), "Multi-Smelter Heating Chamber", 6),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMELTER_CASING.get()), "Multi-Smelter Casing", 44)
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
                            "Smelts 8 items simultaneously in parallel, consuming fuel or RF evenly across all batches."),
                    new GuideRecipe("Plank Pyrolysis (Planks to Charcoal)",
                            List.of(() -> new ItemStack(Items.OAK_PLANKS, 8)),
                            List.of(() -> new ItemStack(Items.CHARCOAL, 8)),
                            "Converts wooden planks directly into Charcoal in parallel at high temperatures.")
            );

            register(new GuideEntry(
                    "multi_smelter",
                    "Heavy Multi-Smelter",
                    "5x4x3 Multiblock • 8-Batch Heavy Smelter",
                    GuideCategory.MACHINERY,
                    () -> new ItemStack(RefineryMod.MULTI_SMELTER_CONTROLLER.get()),
                    "The Industrial Multi-Smelter is an 8-slot heavy batch furnace array that smelts full stacks of items in parallel with remarkable thermal efficiency.\n\n" +
                    "• Dual Power Operation:\n" +
                    "  1. Electric Induction: Consumes 5,000 FE/t of RF/FE power via connected cables or the Fuel/Energy Port. Internal buffer: 1,000,000 FE (Max input: 50,000 FE/t).\n" +
                    "  2. Solid Fuel: Burns solid fuels (Coal, Charcoal, Bitumen) in its fuel slot (0 FE/t).\n" +
                    "• Parallel Throughput: Smelts up to 8 batches simultaneously (ores, metals, and converts wooden planks to Charcoal)!",
                    "Connect an Input Port on the left and Output Port on the right to fully automate your ore smelting pipeline!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 5,000 FE/t or Solid Fuel (Buffer: 1,000,000 FE | Max In: 50,000 FE/t)"
            ));
        }

        // =========================================================================
        // 8. STEAM BOILERS (Solid-Fuel: Small & Large)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Small Steam Boiler Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Boiler Fuel Input Port", 0xFFEF4444, () -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Boiler Water Input Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get())));
            legend.put('S', new LayerSymbol('S', "Boiler Steam Output Port", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Boiler Firebox Hearth", 0xFFEA580C, () -> new ItemStack(RefineryMod.BOILER_FIREBOX.get())));
            legend.put('P', new LayerSymbol('P', "Boiler Pressure Pipe", 0xFFF59E0B, () -> new ItemStack(RefineryMod.BOILER_PIPE.get())));
            legend.put('X', new LayerSymbol('X', "Steel Boiler Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.STEEL_BOILER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get()), "Small Steam Boiler Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get()), "Boiler Fuel Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get()), "Boiler Water Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get()), "Boiler Steam Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FIREBOX.get()), "Boiler Firebox Hearth", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_PIPE.get()), "Boiler Pressure Pipe", 1),
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
                    new GuideRecipe("Small Boiler Steam Generation",
                            List.of(() -> new ItemStack(Items.WATER_BUCKET), () -> new ItemStack(Items.COAL)),
                            List.of(() -> new ItemStack(RefineryFluids.STEAM_BUCKET.get())),
                            "Boils 50 mB/t Water into 100 mB/t High Pressure Steam under sustained combustion."),
                    new GuideRecipe("Large Boiler Steam Generation",
                            List.of(() -> new ItemStack(Items.WATER_BUCKET, 3), () -> new ItemStack(Items.COAL, 2)),
                            List.of(() -> new ItemStack(RefineryFluids.STEAM_BUCKET.get(), 3)),
                            "Boils 150 mB/t Water into 300 mB/t High Pressure Steam across 8 fuel slots (matches 3 Small Boilers).")
            );

            register(new GuideEntry(
                    "boilers",
                    "Solid-Fuel Steam Boilers (Small & Large)",
                    "3x3x3 & 5x5x5 Multiblock • Solid Fuel Steam Generation",
                    GuideCategory.POWER,
                    () -> new ItemStack(RefineryMod.SMALL_BOILER_CONTROLLER.get()),
                    "Steam Boilers burn solid fuels (Coal, Coke, Charcoal, Bitumen) to flash water into high-pressure steam for Steam Turbines.\n\n" +
                    "• Power Consumption: 0 FE/t (Thermal combustion powered)\n" +
                    "• Small Steam Boiler (3x3x3): 4 fuel slots. Boils 50 mB/t Water into 100 mB/t Steam. Reservoir: 16,000 mB.\n" +
                    "• Large Steam Boiler (5x5x5): 8 fuel slots. Boils 150 mB/t Water into 300 mB/t Steam (exactly 3x Small). Reservoir: 64,000 mB.\n" +
                    "• Meltdown Hazard: Keep water supplied continuously! If a boiler runs dry while hot, a siren alarm warns of impending catastrophic explosion.",
                    "Always pipe turbine exhaust water back into the Boiler Water Port to maintain a safe, closed Rankine loop!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power: Unpowered (Consumes Solid Fuel: Coal, Charcoal, Bitumen; 0 FE/t)"
            ));
        }

        // =========================================================================
        // 8A. DIESEL STEAM BOILERS (Small & Large)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Small Diesel Boiler Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER.get())));
            legend.put('F', new LayerSymbol('F', "Boiler Fuel Input Port", 0xFFEF4444, () -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Boiler Water Input Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get())));
            legend.put('S', new LayerSymbol('S', "Boiler Steam Output Port", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get())));
            legend.put('B', new LayerSymbol('B', "Boiler Firebox Hearth", 0xFFEA580C, () -> new ItemStack(RefineryMod.BOILER_FIREBOX.get())));
            legend.put('P', new LayerSymbol('P', "Boiler Pressure Pipe", 0xFFF59E0B, () -> new ItemStack(RefineryMod.BOILER_PIPE.get())));
            legend.put('X', new LayerSymbol('X', "Steel Boiler Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.STEEL_BOILER_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER.get()), "Small Diesel Boiler Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FUEL_PORT.get()), "Boiler Fuel Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_WATER_PORT.get()), "Boiler Water Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_STEAM_PORT.get()), "Boiler Steam Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_FIREBOX.get()), "Boiler Firebox Hearth", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.BOILER_PIPE.get()), "Boiler Pressure Pipe", 1),
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
                    new GuideRecipe("Small Diesel Boiler Cycle",
                            List.of(() -> new ItemStack(RefineryFluids.DIESEL_BUCKET.get()), () -> new ItemStack(Items.WATER_BUCKET)),
                            List.of(() -> new ItemStack(RefineryFluids.STEAM_BUCKET.get())),
                            "Consumes 10 mB Diesel & 100 mB Water per 10-tick cycle to produce 250 mB Steam / cycle (25 mB/t)."),
                    new GuideRecipe("Large Diesel Boiler Cycle",
                            List.of(() -> new ItemStack(RefineryFluids.DIESEL_BUCKET.get()), () -> new ItemStack(Items.WATER_BUCKET, 3)),
                            List.of(() -> new ItemStack(RefineryFluids.STEAM_BUCKET.get(), 3)),
                            "Consumes 30 mB Diesel & 300 mB Water per 10-tick cycle to produce 750 mB Steam / cycle (75 mB/t; 3x Small).")
            );

            register(new GuideEntry(
                    "diesel_boilers",
                    "Diesel Steam Boilers (Small & Large)",
                    "3x3x3 & 5x5x5 Multiblock • Liquid Fuel Steam Generation",
                    GuideCategory.POWER,
                    () -> new ItemStack(RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER.get()),
                    "Diesel Steam Boilers burn liquid hydrocarbons (Diesel or Ethanol Biofuel) to produce massive volumes of high-pressure steam with zero solid ash.\n\n" +
                    "• Power Consumption: 0 FE/t (Liquid combustion powered)\n" +
                    "• Small Diesel Boiler (3x3x3): Consumes 10 mB Diesel & 100 mB Water per 10-tick cycle to produce 250 mB Steam / cycle (25 mB/t). Reservoir: 16,000 mB.\n" +
                    "• Large Diesel Boiler (5x5x5): Consumes 30 mB Diesel & 300 mB Water per 10-tick cycle to produce 750 mB Steam / cycle (75 mB/t; 3x Small). Reservoir: 64,000 mB.\n" +
                    "• Siren Alarm: Warns of low water before dry-run thermal meltdown.",
                    "Supply fuel via Industrial Fluid Pipes connected to the Boiler Fuel Port from your Diesel storage tanks!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power: Unpowered (Consumes Diesel or Ethanol Fuel; 0 FE/t)"
            ));
        }

        // =========================================================================
        // 8B. STEAM TURBINE GENERATOR (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Steam Turbine Controller", 0xFF10B981, () -> new ItemStack(RefineryMod.STEAM_TURBINE_CONTROLLER.get())));
            legend.put('E', new LayerSymbol('E', "Turbine Energy Output Port", 0xFFF59E0B, () -> new ItemStack(RefineryMod.TURBINE_ENERGY_PORT.get())));
            legend.put('S', new LayerSymbol('S', "Turbine Steam Input Port", 0xFFCBD5E1, () -> new ItemStack(RefineryMod.TURBINE_STEAM_PORT.get())));
            legend.put('W', new LayerSymbol('W', "Turbine Water Output Port", 0xFF38BDF8, () -> new ItemStack(RefineryMod.TURBINE_WATER_PORT.get())));
            legend.put('R', new LayerSymbol('R', "Turbine Rotor Shaft", 0xFFA855F7, () -> new ItemStack(RefineryMod.TURBINE_ROTOR.get())));
            legend.put('X', new LayerSymbol('X', "Steam Turbine Casing", 0xFF64748B, () -> new ItemStack(RefineryMod.TURBINE_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.STEAM_TURBINE_CONTROLLER.get()), "Steam Turbine Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_ENERGY_PORT.get()), "Turbine Energy Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_STEAM_PORT.get()), "Turbine Steam Input Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_WATER_PORT.get()), "Turbine Water Output Port", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_ROTOR.get()), "Turbine Rotor Shaft", 3),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.TURBINE_CASING.get()), "Steam Turbine Casing", 20)
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
                    "The Steam Turbine Generator expands high-pressure steam across a 3-tier rotor shaft to produce massive electrical power.\n\n" +
                    "• Power Generation: Produces 10,000 FE/t of electricity at full steam consumption\n" +
                    "• Steam Consumption: Consumes 300 mB/t High-Pressure Steam at full load (matches 1 Large Steam Boiler or 3 Small Boilers)\n" +
                    "• Water Condensation: Internal surface condenser recaptures 150 mB/t recyclable Water (2:1 steam-to-water ratio)\n" +
                    "• Energy Export: Internal buffer holds 1,000,000 FE and actively pushes up to 50,000 FE/t to connected energy cables/machines.",
                    "Route water back to your boiler with an insulated fluid pipe to run continuous, closed-loop zero-loss power generation!",
                    reqs,
                    layers,
                    List.of(),
                    "⚡ Power Generation: 10,000 FE/t (Buffer: 1,000,000 FE | Max Out: 50,000 FE/t)"
            ));
        }

        // =========================================================================
        // 8C. HEAVY PIT QUARRY EXCAVATOR (3x3x3 Multiblock)
        // =========================================================================
        {
            Map<Character, LayerSymbol> legend = new HashMap<>();
            legend.put('C', new LayerSymbol('C', "Heavy Pit Quarry Controller", 0xFF38BDF8, () -> new ItemStack(RefineryMod.QUARRY_CONTROLLER.get())));
            legend.put('X', new LayerSymbol('X', "Quarry Structural Casing", 0xFFD9821E, () -> new ItemStack(RefineryMod.QUARRY_CASING.get())));

            List<GuideBlockRequirement> reqs = List.of(
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.QUARRY_CONTROLLER.get()), "Heavy Pit Quarry Controller", 1),
                    new GuideBlockRequirement(() -> new ItemStack(RefineryMod.QUARRY_CASING.get()), "Quarry Structural Casing", 26)
            );

            List<GuideLayer> layers = List.of(
                    new GuideLayer(0, "Layer 0 (Foundation Skid & Operator Station)", new String[]{
                            "XXX",
                            "XXX",
                            "XCX"
                    }, legend),
                    new GuideLayer(1, "Layer 1 (Machinery Deck & Winch)", new String[]{
                            "XXX",
                            "XXX",
                            "XXX"
                    }, legend),
                    new GuideLayer(2, "Layer 2 (A-Frame Gantry & Floodlights)", new String[]{
                            "XXX",
                            "XXX",
                            "XXX"
                    }, legend)
            );

            List<GuideRecipe> recipes = List.of(
                    new GuideRecipe("Quarry Structural Casing (x4)",
                            List.of(() -> new ItemStack(RefineryMod.STEEL_INGOT.get(), 4), () -> new ItemStack(RefineryMod.STEEL_PLATE.get(), 4)),
                            List.of(() -> new ItemStack(RefineryMod.QUARRY_CASING.get(), 4)),
                            "Crafted with 4 Steel Plates and 4 Steel Ingots in a crafting table."),
                    new GuideRecipe("Heavy Pit Quarry Controller",
                            List.of(() -> new ItemStack(Items.DIAMOND_PICKAXE, 2), () -> new ItemStack(RefineryMod.QUARRY_CASING.get()), () -> new ItemStack(RefineryMod.STEEL_PLATE.get(), 2)),
                            List.of(() -> new ItemStack(RefineryMod.QUARRY_CONTROLLER.get())),
                            "Crafted with Diamond Pickaxes, Comparator, Redstone Blocks, Quarry Casing, Steel Plates, and Gold Ingot.")
            );

            register(new GuideEntry(
                    "quarry",
                    "Heavy Pit Quarry Excavator",
                    "3x3x3 Multiblock • Automated Surface Mining",
                    GuideCategory.MULTIBLOCKS,
                    () -> new ItemStack(RefineryMod.QUARRY_CONTROLLER.get()),
                    "The Heavy Pit Quarry Excavator is an automated open-pit mining machine designed to excavate massive terraced pits down to bedrock.\n\n" +
                    "• Power Consumption: 240 FE/t default at max mining speed (configurable via quarryEnergyPerTick in refinery.toml)\n" +
                    "• Base Mining Speed: 1.5 blocks/second default (configurable via quarryMaxMiningSpeed in refinery.toml)\n" +
                    "• Proportional Speed Scaling: Throttles speed proportionally if supplied energy is below 240 FE/t\n" +
                    "• Energy Buffer: 50,000 FE (Max input: 1,000 FE/t)\n" +
                    "• Material Speed Multipliers: Soft blocks (Dirt, Sand, Gravel, Tuff) mine at 2x (3.0 blocks/s); Ores (Iron, Diamond) mine at 0.5x (0.75 blocks/s)\n" +
                    "• Phase 0 (Outer Bench): Clears a 43x43x3 perimeter box (Y=+2 down to Y=0)\n" +
                    "• Phase 1 (Safety Railing): Automatically erects safety fences around the 41x41 rim using fences supplied in GUI slot 9\n" +
                    "• Phase 2 (Deep Pit Excavation): Sinks a 41x41 shaft from Y=-1 down to bedrock\n" +
                    "• Rear Spoil Chute: Items designated as Throw-Back spoil roll off the rear chute into the pit void; Left/Right side ports export products.",
                    "• Click any block in the Geological Manifest Monitor or press [SPOIL] to toggle Throw-Back rejection!\n" +
                    "• Sneak-right-click the Controller with a Refinery Wrench to toggle an in-world 3D holographic blueprint!",
                    reqs,
                    layers,
                    recipes,
                    "⚡ Power Draw: 240 FE/t default (Buffer: 50,000 FE | Configurable in refinery.toml)"
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
                "A compact 1x2x2 internal combustion generator that runs on Diesel or Ethanol Biofuel. Produces high-voltage RF/FE energy directly from its output terminals.\n\n" +
                "• Energy Generation: Produces 256 FE/t directly into connected cables or machines\n" +
                "• Fuel Consumption: Burns 1 mB diesel every 4 ticks (1,000 FE per mB of diesel fuel)\n" +
                "• Energy Storage Buffer: 500,000 FE (Max export rate: 1,024 FE/t)\n" +
                "• Redstone Automation: Supports Always On, Active High, or Active Low (backup emergency generator mode).",
                "Set redstone mode to Active Low so the generator turns on automatically as a backup whenever your primary grid drops power!",
                null,
                null,
                List.of(),
                "⚡ Power Generation: 256 FE/t (Fuel: Diesel/Ethanol | Buffer: 500,000 FE)"
        ));

        register(new GuideEntry(
                "fermenter",
                "Industrial Fermenter",
                "Biochemical Ethanol Production",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.FERMENTER.get()),
                "The Industrial Fermenter is a biochemical processing vat that digests agricultural crops into Ethanol Biofuel.\n\n" +
                "• Power Consumption: 15 FE/t over 60 ticks / 3.0s per crop (900 FE total per batch)\n" +
                "• Energy Storage Buffer: 20,000 FE (Max input rate: 500 FE/t)\n" +
                "• Tank Capacity: 8,000 mB Ethanol\n" +
                "• Supported Crops: Sugar cane, sweet berries, potatoes, apples, and wheat seeds (produces 250 mB Ethanol per batch).",
                "Pair with an Automated Harvester and Diesel Generator for an infinite, 100% renewable green electricity loop!",
                null,
                null,
                List.of(
                        new GuideRecipe("Ethanol Biofuel Fermentation",
                                List.of(() -> new ItemStack(Items.SUGAR_CANE, 4)),
                                List.of(() -> new ItemStack(RefineryFluids.ETHANOL_BUCKET.get())),
                                "Ferments organic biomass into 250 mB of high-octane Ethanol liquid fuel.")
                ),
                "⚡ Power Draw: 15 FE/t (Buffer: 20,000 FE | Max In: 500 FE/t)"
        ));

        register(new GuideEntry(
                "agricultural",
                "Automated Harvester & Planter",
                "Autonomous Crop & Tree Farming",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.AUTO_HARVESTER.get()),
                "The Auto Planter and Auto Harvester automate agricultural cultivation in up to a 9x9 field.\n\n" +
                "• Auto Planter: Sows seeds and tills soil. Consumes 1 FE/t idle standby + 10 FE per planting/tilling action. Buffer: 10,000 FE (Max in: 500 FE/t).\n" +
                "• Auto Harvester: Cuts mature crops and timber trees. Consumes 1 FE/t idle standby + 20 FE per crop harvest. For timber trees: consumes 20 FE base + 5 FE/log + 2 FE/leaf (leaf decay: 20 FE + 2 FE/leaf). Buffer: 10,000 FE (Max in: 500 FE/t).\n" +
                "• Feeding Trough: Autonomous livestock feeder and breeder. Unpowered (0 FE/t).",
                "Attach an extraction pipe with an autarchic pulse gate to automatically draw wheat, seeds, and logs into your storage network!",
                null,
                null,
                List.of(),
                "⚡ Harvester: 1 FE/t idle + 20 FE/crop | Planter: 1 FE/t idle + 10 FE/action"
        ));

        register(new GuideEntry(
                "crude_pump_jack",
                "Crude Oil Pump Jack",
                "2x3x3 Machine • Downhole Well Extraction",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.CRUDE_PUMP_JACK_ITEM.get()),
                "The Crude Oil Pump Jack is an authentic walking-beam beam pump engineered for automated subterranean oil well extraction.\n\n" +
                "• Power Consumption & Speed Scaling:\n" +
                "  • 4 FE/t: Full speed (1,000 mB/min)\n" +
                "  • 2 FE/t: Half speed (500 mB/min)\n" +
                "  • 1 FE/t: Low speed (100 mB/min)\n" +
                "• Energy Storage Buffer: 10,000 FE (Max input rate: 64 FE/t)\n" +
                "• Downhole Reach: Probes up to 50 blocks downward and 10 blocks horizontally, detecting underground crude oil pockets and natural reservoirs.\n" +
                "• Internal Reservoir: Stores up to 16,000 mB of Crude Oil, auto-pushing to connected fluid pipes.\n" +
                "• In-World Footprint: Placed as a single block that automatically forms a full 2x3x3 structural steel walking beam assembly.",
                "• Place directly above or within 10 blocks of subterranean crude oil reservoirs detected during prospecting.\n" +
                "• Connect Industrial Fluid Pipes to the base to pump oil directly into your 3-Stage Petroleum Refinery!\n" +
                "• Breaks as a single block and cleanly drops 1 Crude Pump Jack item.",
                null,
                null,
                List.of(
                        new GuideRecipe("Crude Oil Pump Jack",
                                List.of(() -> new ItemStack(RefineryMod.ELECTRIC_FLUID_PUMP.get()), () -> new ItemStack(RefineryMod.STEEL_GEAR.get()), () -> new ItemStack(RefineryMod.STEEL_PLATE.get(), 4), () -> new ItemStack(RefineryMod.REINFORCED_CASING.get())),
                                List.of(() -> new ItemStack(RefineryMod.CRUDE_PUMP_JACK_ITEM.get())),
                                "Crafted with an Electric Fluid Pump, Steel Gear, 4 Steel Plates, and 1 Reinforced Structural Casing.")
                ),
                "⚡ Power Draw: 1 to 4 FE/t (Buffer: 10,000 FE | Max In: 64 FE/t)"
        ));

        register(new GuideEntry(
                "fluid_pumps",
                "Fluid Pumps & In-World Discharge",
                "Surface & Well Liquid Pumping",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.ELECTRIC_FLUID_PUMP.get()),
                "Refinery provides powerful pumps for surface liquids, underground pools, and in-world fluid placement:\n\n" +
                "• Electric Fluid Pump: Consumes 400 FE per pump cycle (operates every 10 ticks, pumping 2,000 mB = ~40 FE/t continuous). Buffer: 20,000 FE (Max in: 1,000 FE/t).\n" +
                "• Coal Fluid Pump: Solid fuel powered extraction pump (Coal, Charcoal, Bitumen; 0 FE/t).\n" +
                "• Diesel Fluid Pump: High-rate internal combustion pump fueled by Diesel or Ethanol (0 FE/t).\n" +
                "• Fluid Discharge: Discharges pressurized fluids directly into the world as source blocks or flowing streams (0 FE/t).\n" +
                "• Fire Hydrant: AWWA safety-yellow standpipe hydrant with an 8,000 mB reservoir. When attached to a fluid pipe and fed liquid, right-click with an Empty Bucket in hand to fill it (0 FE/t)!",
                "• Right-click pumps with a Refinery Wrench to rotate their intake and discharge ports!\n" +
                "• Right-click a Fluid Discharge with a wrench to toggle between Source Block and Flowing mode.\n" +
                "• Right-click a Fire Hydrant with an Empty Bucket to draw 1,000 mB of liquid; shift-right-click with an empty hand to flush.",
                null,
                null,
                List.of(),
                "⚡ Electric Pump: 400 FE/cycle (~40 FE/t) | Coal/Diesel: Fuel | Hydrant: 0 FE/t"
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
                "Conveyor belts provide continuous in-world item transport at 0.1 blocks/tick. Available in Flat, Sloped (Climbing/Descending), and Right-Angle (Corner) varieties.\n\n" +
                "• Power Consumption: 0 FE/t (Mechanical belt drive)\n" +
                "• Non-Despawn: Items dropped onto belts can never despawn and are picked up instantly by players or funneled into grinders\n" +
                "• Universal Compatibility: Passes items seamlessly into other conveyor systems like Ole Saw Mill without bunching or getting stuck.",
                "Items dropped onto belts can never despawn and are picked up instantly by players or funneled into grinders!",
                null,
                null,
                List.of(),
                "⚡ Power: Unpowered (0 FE/t)"
        ));

        register(new GuideEntry(
                "storage_tank",
                "Modular Liquid Storage Tanks",
                "Glass-Fused-to-Steel Multiblock Tanks",
                GuideCategory.LOGISTICS,
                () -> new ItemStack(RefineryMod.STORAGE_TANK_DRAIN_VALVE.get()),
                "Modular liquid storage tanks holding from 576,000 mB up to 4,000,000+ mB of any liquid.\n\n" +
                "• Power Consumption: 0 FE/t (Gravity/containment vessel)\n" +
                "• Sizes: 3x3x4, 5x5x6, or 7x7x8 built using Storage Tank Casings and Valves\n" +
                "• Features: Integrated ladder climbing, lower drain valve, upper fill valve, and top overflow vent.",
                "Build sizes: 3x3x4, 5x5x6, or 7x7x8 using Storage Tank Casings and Valves.",
                null,
                null,
                List.of(),
                "⚡ Power: Unpowered (0 FE/t)"
        ));

        register(new GuideEntry(
                "bitumen_pile",
                "Bitumen Storage Pile",
                "3x3 Footprint • Dynamic Visual Asphalt Stockpile",
                GuideCategory.LOGISTICS,
                () -> new ItemStack(RefineryMod.BITUMEN.get()),
                "The Bitumen Storage Pile is a large open-air industrial bulk storage dump for asphalt bitumen and petroleum tar.\n\n" +
                "• Power Consumption: 0 FE/t\n" +
                "• 3x3 Footprint: Placed as a single block establishing an authentic 3x3 ground perimeter.\n" +
                "• Dynamic Visual Growth: Gradually mounds and piles up visually through 8 distinct height stages as more bitumen is added (holding up to 256 stacks / 16,384 bitumen chunks).\n" +
                "• Vacuum Ingestion: Automatically suctions and vacuums up bitumen items dropped or tossed nearby.\n" +
                "• Logistics Feeds: Easily supplied from overhead conveyors, chutes, or hoppers.",
                "• Right-click with Bitumen to deposit; sneak-right-click with an empty hand to extract chunks.\n" +
                "• Hit the pile with a shovel or pickaxe to extract bitumen quickly.\n" +
                "• Breaking the pile drops the Bitumen Storage Pile item plus all stored bitumen contents!",
                null,
                null,
                List.of(
                        new GuideRecipe("Bitumen Storage Pile",
                                List.of(() -> new ItemStack(RefineryMod.BITUMEN.get(), 8)),
                                List.of(() -> new ItemStack(RefineryMod.BITUMEN_PILE.get())),
                                "Crafted with 8 Bitumen chunks around an empty center in a crafting table.")
                ),
                "⚡ Power: Unpowered (0 FE/t)"
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
                "Refinery provides specialized industrial pipes for fluids and items:\n\n" +
                "• Power Consumption: 0 FE/t\n" +
                "• Wood: Filtered Extraction (right-click to open 9-slot filter GUI; pulses strictly with Autarchic Gates!)\n" +
                "• Iron: Directional one-way valve\n" +
                "• Gold: High-speed acceleration & priority insertion\n" +
                "• Diamond: 9-slot directional sorting filter\n" +
                "• Quartz: Merge pipe (5 inputs -> 1 selected output)\n" +
                "• Emerald: Round-robin load balancer\n" +
                "• Valve: Redstone/Gate actuated flow control (Normal: closes on redstone, Inverted: opens on redstone)\n" +
                "• Obsidian: In-world item suction vacuum\n" +
                "• Void: Incineration of unwanted byproducts\n" +
                "• Buffer: 1-slot inline chest pipe",
                "Right-click pipes with a Refinery Wrench to toggle connections or change merger output directions!",
                null,
                null,
                List.of(),
                "⚡ Power: Unpowered (0 FE/t)"
        ));

        register(new GuideEntry(
                "pipe_gates",
                "BuildCraft-Style Logic Gates & Wires",
                "Autarchic Pulsars & Conditional Automation",
                GuideCategory.PIPES_GATES,
                () -> new ItemStack(RefineryMod.DIAMOND_AUTARCHIC_GATE_ITEM.get()),
                "Pipe Gates mount directly on pipe faces or chests to execute logic actions based on machine status, inventory space, or network signals.\n\n" +
                "• Power Consumption: 0 FE/t (Logic driven)\n" +
                "• Tiers: Blank, Basic (1 slot), Iron (2 slots), Gold (4 slots), Diamond (8 slots)\n" +
                "• Redstone Actions:\n" +
                "  • Constant power (Signal 15)\n" +
                "  • Periodic 1.0s pulse (20-tick period)\n" +
                "  • Rapid 0.5s pulse (10-tick period / 1.0 Hz clock frequency)\n" +
                "• Autarchic Gates: Pull 1, 5, or 10 items at a time without external engines\n" +
                "• Pipe Wires: Red, Blue, Green, Yellow wires broadcast conditional signals along the pipe network.",
                "Combine 'Space in Inventory' -> 'Autarchic Pulse (Item x10)' to rapidly restock machines only when they need supplies!",
                null,
                null,
                List.of(),
                "⚡ Power: Unpowered (Logic Driven; 0 FE/t)"
        ));

        register(new GuideEntry(
                "refinery_wrench",
                "Refinery Wrench & Hologram Wand",
                "Essential Engineering Multitool",
                GuideCategory.MACHINERY,
                () -> new ItemStack(RefineryMod.REFINERY_WRENCH.get()),
                "The Refinery Wrench is your indispensable tool for setting up industrial base infrastructure.\n\n" +
                "• Multiblocks: Sneak-right-click any controller to toggle a luminous 3D holographic projection of the multiblock structure in the world!\n" +
                "• Pipes: Right-click pipe faces to connect/disconnect branches or cycle Quartz merge outputs.\n" +
                "• Machines: Right-click pumps or generators to rotate their facing.",
                "Always carry a Refinery Wrench when building complex multiblocks!",
                null,
                null,
                List.of(),
                "⚡ Power: Unpowered Handheld Tool"
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
