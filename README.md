# Industrial Refinery â€” Complete Mod Wiki & Engineering Guide
*A comprehensive guide to heavy industry, fractional distillation, steam power, metallurgy, and automation for Minecraft 26.3 NeoForge.*

---

## Table of Contents
1. [Core Progression & Materials](#1-core-progression--materials)
2. [Essential Tool: The Refinery Wrench](#2-essential-tool-the-refinery-wrench)
3. [Standalone Agricultural Automation](#3-standalone-agricultural-automation)
   - [Automated Planter](#automated-planter)
   - [Automated Harvester](#automated-harvester)
   - [Livestock Feeding Trough](#livestock-feeding-trough)
4. [Heavy Metallurgy](#4-heavy-metallurgy)
   - [Industrial Blast Smelter (5Ã—5Ã—5 Multiblock)](#industrial-blast-smelter-555)
   - [Industrial Metal Press (2Ã—2Ã—2 Multiblock)](#industrial-metal-press-222)
   - [Industrial Multi-Smelter (5Ã—4Ã—3 Heavy Smelter Array)](#industrial-multi-smelter-543)
5. [Thermodynamic Steam Power & Closed-Loop Energy](#5-thermodynamic-steam-power--closed-loop-energy)
   - [Small Steam Boiler (3Ã—3Ã—3 Multiblock)](#small-steam-boiler-333)
   - [Large Heavy Steam Boiler (5Ã—5Ã—5 Multiblock)](#large-heavy-steam-boiler-555)
   - [Steam Turbine Generator (3Ã—3Ã—3 Multiblock)](#steam-turbine-generator-333)
   - [The Closed-Loop Rankine Cycle](#the-closed-loop-rankine-cycle)
6. [Petroleum Refining & Chemical Byproducts](#6-petroleum-refining--chemical-byproducts)
   - [Petroleum Refinery (3Ã—3Ã—4 Multiblock)](#petroleum-refinery-334)
   - [Oil Sand Cracking & Extraction](#oil-sand-cracking--extraction)
   - [Fuels & Byproducts](#fuels--byproducts)
   - [Industrial Grinder & Pulverizer (3Ã—3Ã—3 Multiblock)](#industrial-grinder--pulverizer-3x3x3)
7. [Comprehensive Recipe Codex](#7-comprehensive-recipe-codex)
8. [Automation, Conveyors, Pipes, & Interfacing](#8-automation-conveyors-pipes--interfacing)
   - [Industrial Belt Conveyor](#industrial-belt-conveyor)
9. [Crude Oil World Generation & Retrogen](#9-crude-oil-world-generation--retrogen)
   - [In-World Fluid Crude Oil](#in-world-fluid-crude-oil)
   - [Deposit Structures & Capacities](#deposit-structures--capacities)
   - [Configuration (`config/refinery.toml`)](#configuration-configrefinerytoml)
   - [Retrogen Persistence (No Re-Gen Once Mined)](#retrogen-persistence-no-re-gen-once-mined)
   - [Administrative Commands](#administrative-commands)
10. [BuildCraft-Style Pipe Logic Gates & Wires](#10-buildcraft-style-pipe-logic-gates--wires)
   - [Overview & Pipe Attachment](#overview--pipe-attachment)
   - [Gate Tiers](#gate-tiers)
   - [Autarchic Pulsar (Automated Extraction)](#autarchic-pulsar-automated-extraction)
   - [Pipe Wire Networks (Red, Blue, Green, Yellow)](#pipe-wire-networks-red-blue-green-yellow)
   - [Trigger & Action Codex](#trigger--action-codex)
   - [Crafting & In-World Upgrades](#crafting--in-world-upgrades)
11. [Continuous Molten Metallurgy & Ingot Casting Suite](#11-continuous-molten-metallurgy--ingot-casting-suite)
   - [Molten Fluids & Thermochemistry](#molten-fluids--thermochemistry)
   - [Electric Arc Melter (3Ã—3Ã—3 Multiblock)](#electric-arc-melter-333-multiblock)
   - [Open Casting Channel (Refractory Trough)](#open-casting-channel-refractory-trough)
   - [Continuous Casting Press (3Ã—3Ã—3 Multiblock)](#continuous-casting-press-333-multiblock)
   - [Melting & Stamping Recipe Codex](#melting--stamping-recipe-codex)
12. [Biofuel Production & Industrial Fermentation](#12-biofuel-production--industrial-fermentation)
   - [Industrial Fermenter](#industrial-fermenter)
   - [Ethanol Biofuel & Crop Fermentation Codex](#ethanol-biofuel--crop-fermentation-codex)
13. [Engineering Changelog & Version History](#13-engineering-changelog--version-history)
   - [Version 1.4.1 (Progression Balancing & Steel Plate Exclusivity)](#version-141-progression-balancing--steel-plate-exclusivity)
   - [Version 1.4.0 (Biofuel & Oil Sand Cracking)](#version-140-biofuel--oil-sand-cracking)
   - [Version 1.3.0 (Continuous Molten Metallurgy)](#version-130-continuous-molten-metallurgy)
   - [Version 1.2.0 (Grinder Overhaul & Logic Gates)](#version-120-grinder-overhaul--logic-gates)
   - [Version 1.1.0 (Oil Geysers & Retrogen)](#version-110-oil-geysers--retrogen)
   - [Version 1.0.0 (Foundational Release)](#version-100-foundational-release)

---

## 1. Core Progression & Materials

The mod introduces a tiered industrial metallurgy and chemical progression:

```
[Raw Iron + Coal] ---> (5x5x5 Blast Smelter) ---> [Steel Ingot]
                                                       |
                                                       v
                                            (3x2x2 Metal Press)
                                                       |
                                                       v
                                            [Heavy Steel Plate]
                                                       |
       +-----------------------+-----------------------+-----------------------+
       |                       |                       |                       |
       v                       v                       v                       v
[3x3x3 Small Boiler]  [5x5x5 Large Boiler]   [3x3x3 Steam Turbine]   [3x3x4 Refinery]
```

### Key Materials
| Item | Source | Purpose |
| :--- | :--- | :--- |
| **Steel Ingot** (`refinery:steel_ingot`) | Industrial Blast Smelter | Primary alloy for heavy machine frames, Casings, and Controllers. |
| **Heavy Steel Plate** (`refinery:steel_plate`) | Industrial Metal Press (exclusive) | Reinforced plating stamped exclusively in the Metal Press; required for high-pressure boilers and turbines. |
| **Tin Ingot** (`refinery:tin_ingot`) | Smelting Tin Dust / Ores | Malleable industrial metal used for bronze alloying and plating. |
| **Tin Dust** (`refinery:tin_dust`) | Grinding Tin Ores or Raw Tin (x2) | Pulverized tin mineral used for smelting or alloy blending. |
| **Tin Plate** (`refinery:tin_plate`) | Metal Press (Tin Ingot) | Stamped tin plate used in circuitry and engineering. |
| **Bronze Ingot** (`refinery:bronze_ingot`) | Alloying 3 Copper + 1 Tin | Durable copper-tin alloy ingot for machinery and tools. |
| **Bronze Dust** (`refinery:bronze_dust`) | Grinding Bronze Ingot or blending dusts | Alloy dust that smelts directly into Bronze Ingots. |
| **Bronze Plate** (`refinery:bronze_plate`) | Metal Press (Bronze Ingot) | Heavy-duty stamped bronze structural plate. |
| **Coal Dust** (`refinery:coal_dust`) | Grinding Coal or Coal Blocks | Finely pulverized coal fuel for Blast Smelters and boilers. |
| **Iron Plate** (`refinery:iron_plate`) | Metal Press (Iron Ingot) | Standard industrial stamped iron plate. |
| **Copper Plate** (`refinery:copper_plate`) | Metal Press (Copper Ingot) | Highly conductive stamped plate for electrical piping and components. |
| **Gold Plate** (`refinery:gold_plate`) | Metal Press (Gold Ingot) | Corrosion-resistant stamped plate for advanced logic chips and gates. |
| **Petroleum Tar** (`refinery:tar`) | Refinery byproduct / Oil Sand grinding | Heavy hydrocarbon fuel (burns 1,600 ticks / 8 items) or crafts into torches/pipes. |
| **Sulfur Dust** (`refinery:sulfur_dust`) | Refinery byproduct | Chemical mineral used to craft Gunpowder without creepers. |
| **Diesel** (`refinery:diesel`) | Refinery fractional distillation | High-yield petroleum distillate. |
| **Gasoline** (`refinery:gasoline`) | Refinery fractional distillation | Volatile combustible gas distillate. |
| **High Pressure Steam** (`refinery:steam`) | Steam Boilers | Working fluid for power generation in Steam Turbines. |

---

## 2. Essential Tool: The Refinery Wrench

The **Refinery Wrench** (`refinery:refinery_wrench`) is the ultimate industrial diagnostic tool.

- **Block Reorientation**: Right-click machine blocks, ports, or casings to rotate their horizontal facing.
- **3D Holographic Blueprint**: Sneak-right-click (or right-click) any **Multiblock Controller** to cast a real-time **3D in-world hologram projection** for 30 seconds.
  - **Color-Coded Block Labels**: Each block type now displays bold, high-contrast text in a distinct signature color:
    - **Controllers**: Vivid Emerald Green (`#22C55E`)
    - **Crude Oil Input Port**: Petroleum Amber Gold (`#F59E0B`)
    - **Diesel Output Port**: Radiant Canary Yellow (`#FACC15`)
    - **Gasoline / Gas Port**: Electric Cyan (`#06B6D4`)
    - **Steam Ports**: Frost Sky Blue (`#38BDF8`)
    - **Water Ports**: Royal Ocean Blue (`#3B82F6`)
    - **Fuel Ports**: Flame Orange (`#FB923C`)
    - **Energy Ports**: Neon Lime (`#A3E635`)
    - **Byproduct Ports**: Vivid Fuchsia Pink (`#EC4899`)
    - **Foundry / Grinder Input Ports**: Mint Green (`#4ADE80`)
    - **Foundry / Grinder Output Ports**: Tangerine Orange (`#FB923C`)
    - **Combustion Chamber & Firebox**: Fiery Crimson Red (`#EF4444`)
    - **Heat Exchanger & Crushing Chamber**: Coral / Ruby (`#FB7185` / `#F43F5E`)
    - **Distillation Column**: Aquamarine Ice (`#67E8F9`)
    - **Foundry Glass Window**: Amethyst Purple (`#C084FC`)
    - **Boiler Pressure Pipes**: Copper Bronze (`#FDBA74`)
    - **Turbine Rotor Shaft**: Bright Electric Teal (`#2DD4BF`)
    - **Press Hydraulic Head**: Rich Violet (`#A855F7`)
    - **Press Anvil Bed**: Cobalt Indigo (`#818CF8`)
    - **Casings**: Clean metallic tones (Platinum Slate, Steel Blue, Industrial Brass, Titanium, Periwinkle, Hazard Yellow).
  - Both the 3D wireframe outline and floating text match their block type's signature color, making complex structures instantly readable from any angle.
- **Fluid Pump Suction Hose Linking**: Sneak-right-click the **Industrial Fluid Pump** with the wrench to select it, then right-click or sneak-click any block or fluid source in the world (up to 64 blocks away) to link the suction intake point. The mod immediately deploys a dynamic 3D flexible suction hose connecting the pump to that exact point! Double sneak-clicking the pump dismantles and picks it up.

---

## 3. Standalone Agricultural Automation

### Automated Planter
The **Automated Planter** (`refinery:auto_planter`) manages soil preparation and crop planting over a large farm area.

- **Crafting Recipe**: 4x Iron Ingot, 2x Copper Ingot, 1x Iron Hoe (top-center), 1x Piston (center), 1x Redstone Dust (bottom-center).
- **GUI Controls**:
  - **Power Switch**: Interactive GUI `ON`/`OFF` toggle button. Turning the machine OFF pauses all scanning and stops idle energy consumption.
  - **Configurable Work Radius**: Interactive `[-]` and `[+]` buttons to configure the work radius anywhere from **1 to 9 blocks** away in all directions (yielding square areas from 3x3 up to 19x19 / 361 tiles total, easily covering 100m+ farms). Default is 5 blocks (11x11 square / 121 tiles).
- **Scan Coverage**: 1 to 9 blocks in all horizontal directions (centered on the planter) up to **2 blocks above** the planter.
- **Tilling & Planting**:
  - **Crops**: Automatically detects dirt or grass blocks, tills them into farmland, and plants any valid seeds found in its 9-slot inventory.
  - **Trees & Saplings**: Accepts all tree saplings (Oak, Birch, Spruce, Dark Oak, Jungle, Acacia, Cherry, Pale Oak, Mangrove Propagules, and modded saplings). Plants them directly onto natural soil (`#minecraft:dirt` â€” Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling into farmland.
- **Energy**: Consumes 1-2 FE/t standby and 10 FE per planting action.
- **Automation**: Accepts seeds and saplings from hoppers, droppers, or item pipes through any face.

### Automated Harvester
The **Automated Harvester** (`refinery:auto_harvester`) scans and collects mature crops and trees across your farm.

- **Crafting Recipe**: 4x Iron Ingot, 2x Copper Ingot, 1x Shears (top-center), 1x Hopper (center), 1x Redstone Dust (bottom-center).
- **GUI Controls**:
  - **Power Switch**: Interactive GUI `ON`/`OFF` toggle button to immediately run or pause harvesting without breaking the machine.
  - **Configurable Work Radius**: Interactive `[-]` and `[+]` buttons to configure the scan radius from **1 to 9 blocks** away in all directions (3x3 up to 19x19 / 361 tiles total). Default is 5 blocks (11x11 square / 121 tiles).
- **Scan Coverage**: Configurable radius up to 9 blocks away in all directions around the harvester, spanning vertical offsets from 2 blocks below to 1 block above.
- **Harvest & Replant**:
  - **Crops**: Detects fully mature crops (Wheat, Carrots, Potatoes, Beetroots, Melon/Pumpkin, Sugar Cane, Cocoa, Sweet Berries, etc.), harvests the drops, and immediately replants the seed.
  - **Trees & Foliage**: Detects tree trunks (`#minecraft:logs`) and leaves (`#minecraft:leaves`). Intelligently fells the entire tree via 3D recursive flood-fill (trunk, branches, and canopy up to 32 blocks high and 8 blocks radius), gathering wood logs, saplings, apples, and sticks while clearing the ground for immediate replanting.
- **Buffer & Ejection**: Internal 9-slot buffer. **Automatically pushes harvested produce, wood logs, and saplings into chests or pipes** placed directly **behind** or **below** the harvester!
- **Energy**: Consumes 1-2 FE/t standby and 20 FE per harvest.

```
       [+5 Left] <--- 11 Blocks Wide ---> [+5 Right]
            . . . . . . . . . . .  ^
            . . . . . . . . . . .  |
            . . . . . . . . . . .  | 11 Blocks
            . . . . . . . . . . .  | Forward
            . . . . . . . . . . .  v
                   [ HARVESTER ]
                   [Output Chest] (Placed Behind or Below)
```

### Livestock Feeding Trough
The **Livestock Feeding Trough** (`refinery:feeding_trough`) is a 2-block-wide monolithic galvanized steel feed bunk engineered for automated livestock breeding and animal ranching.

- **Monolithic 3D Architecture**: Modeled after heavy-duty weathered galvanized sheet steel feed bunks, complete with realistic zinc spangle crystal flakes, formed trapezoidal sloped sidewalls, rolled top safety flanges, four heavy-gauge steel divider cross-struts, end bulkheads with cleanout slots, and corner skid feet.
- **Dynamic In-World Hay Mound**:
  - The trough interior features a dynamic, textured golden chopped straw/hay layer.
  - The feed mound smoothly and continuously rises from **Y = 1.0px** (empty metal floor) to **Y = 6.4px** (completely full bunk) proportional to the total food stocked in the inventory.
  - When completely empty, the golden hay hides, revealing the clean galvanized sheet metal floor.
- **Automated Livestock Breeding**:
  - **4.5-Block Radar Range**: Automatically scans a 4.5-block horizontal and 2-block vertical radius (10×5×10 volume) around the two trough blocks every second.
  - **Off-Cooldown Adults Only**: Feeds up to 2 eligible adult animals per second that are off breeding cooldown (`canFallInLove()`).
  - **Heart & Sound Effects**: Successfully fed animals emit love heart particles (`broadcastEntityEvent(18)`) and play authentic eating sound effects (`SoundEvents.GENERIC_EAT`).
  - Baby animals and animals currently in love or on breeding cooldown are never fed, completely preventing food waste.
- **Universal Animal & Diet Compatibility**:
  - Dynamically queries `animal.isFood(stack)` to support all vanilla and modded livestock species.
  - Accepts any valid breeding food: Wheat, Hay Bales, Seeds (Wheat, Pumpkin, Melon, Beetroot, Torchflower, Pitcher Pod), Vegetables (Carrots, Potatoes, Beetroots), Apples, Golden Apples/Carrots, Berries (Sweet, Glow), Bamboo, Seagrass, Kelp, Meats, Fish, Slimeballs, and Fungi.
- **Logistics & Multiblock Integration**:
  - **Unified Double-Block Inventory**: Placing the trough creates a 2-block structure (`FOOT` and `HEAD`). Both halves seamlessly forward all item interactions, hoppers, droppers, and Logistics Automation pipe connections to the master block entity.
  - **Interactive Quick-Deposit**: Right-clicking the trough while holding any valid animal feed instantly deposits food into the trough with sound feedback. Right-clicking with an empty hand opens the container GUI.
  - **Custom GUI**: Features a 9-slot feed grid, a dynamic golden hay level gauge, a live herd radar counter showing nearby animals, and status LED.
- **Crafting Recipe**:
  - 4x Steel Plate, 2x Iron Bars, 1x Hay Bale produces 1 Feeding Trough.

---

## 4. Heavy Metallurgy

### Industrial Blast Smelter (5Ã—5Ã—5)
A massive foundry furnace that transforms iron sources and carbon fuels into high-grade Steel Ingots.

- **Dimensions**: 5 blocks wide Ã— 5 blocks tall Ã— 5 blocks deep (125 blocks total).
- **Viewing Window**: The front wall features a 3Ã—3 transparent **Foundry Heat Glass** window where you can watch raw materials drop into the molten chamber and cook into glowing steel!
- **Universal Ingredients**:
  - **Iron Sources**: Raw Iron, Iron Ingot, Iron Ore, Deepslate Iron Ore, Raw Iron Block, or **Iron Dust** (`#c:dusts/iron`).
  - **Fuel Sources**: Coal, Charcoal, Coal Block, Petroleum Tar, Bitumen, or **Coal Dust / Charcoal Dust** (`#c:dusts/coal`, `#c:dusts/charcoal`).
- **Recipe**: 1 Iron Source + 1 Fuel Source -> 1 Steel Ingot (100 ticks).
- **Buffers**: 4 Iron input slots, 4 Fuel slots, 4 Steel output slots.
- **Automation**: 
  - **Foundry Input Port**: Inserts Iron and Fuel via pipes/hoppers.
  - **Foundry Output Port**: Automatically pulls out finished Steel Ingots.

#### Layer-by-Layer Assembly (Local coordinates relative to Controller)
- **Layer 0 (Foundation)**:
  - `(0, 0, 0)`: **Blast Smelter Controller** (Front center)
  - `(-1, 0, 0)`: **Foundry Input Port**
  - `(1, 0, 0)`: **Foundry Output Port**
  - Remaining 22 floor positions: **Foundry Refractory Casing**
- **Layers 1â€“3 (Smelting Chamber & Viewing Window)**:
  - Front face x in [-1, 1]: **Foundry Heat Glass** (3Ã—3 window)
  - Center 3Ã—3 interior (x in [-1, 1], z in [1, 3]): **Air (Hollow chamber)**
  - Outer side and back walls: **Foundry Refractory Casing**
- **Layer 4 (Roof)**:
  - 25 **Foundry Refractory Casing** covering the entire top.

---

### Industrial Metal Press (3Ã—2Ã—2)
A universal hydraulic mechanical press with a 3-block-wide conveyor bed and an overhead AIDA NC1-1500 stamping head for stamping metal ingots into structural plates.

- **Dimensions**: 3 wide Ã— 2 tall Ã— 2 deep (12 blocks total; also supports 3Ã—2Ã—1 layout).
- **Physical 3D Stamping Animation**: Features an in-world animated hydraulic piston ram with a power stroke, heavy anvil impact (`SoundEvents.ANVIL_LAND`), and spark ejection!
- **Supported Stamping Stamping Recipes** (40 ticks = 2.0s per plate):
  - **Steel Ingot** -> **Heavy Steel Plate**
  - **Iron Ingot** -> **Iron Plate**
  - **Copper Ingot** -> **Copper Plate**
  - **Gold Ingot** -> **Gold Plate**
  - **Tin Ingot** -> **Tin Plate**
  - **Bronze Ingot** -> **Bronze Plate**
- **Energy**: 50,000 FE buffer; consumes 20 FE/t (800 FE per plate).
- **Logistics**:
  - **Top & Sides**: Inserts Ingots into the 2-slot input queue.
  - **Bottom**: Extracts Finished Plates from the 2-slot output buffer.

#### Layer-by-Layer Assembly (3Ã—2Ã—2)
```
Top Layer (y=1):
  Back (z=1):  [Press Casing]      [Press Casing]          [Press Casing]
  Front (z=0): [Press Casing]      [Press Hydraulic Head]  [Press Casing]

Bottom Layer (y=0):
  Back (z=1):  [Press Anvil Bed]   [Press Anvil Bed]       [Press Anvil Bed]
  Front (z=0): [Press Anvil Bed]   [Press Controller]      [Press Anvil Bed]
```

---

### Industrial Multi-Smelter (5Ã—4Ã—3)
An early-to-midgame high-throughput industrial smelting array that unifies **8 independent furnaces into a single automated multiblock machine**. 

- **Dimensions**: 5 wide Ã— 4 high Ã— 3 deep (60 blocks total volume).
- **Hollow Core & 3D In-World Animation**:
  - The interior 3 wide Ã— 2 high Ã— 1 deep core (`X` in [-1, 1], `Y` in [1, 2], `Z` = 1) is a **6-block hollow air combustion chamber**.
  - The front face (`Z` = 0) features a 3Ã—2 array of **Smelter Chamber Glass** viewing windows so players can peer directly into the active furnace interior.
  - **Dynamic In-World 3D Visuals**:
    - **Solid Fuel Combustion**: When burning solid fuel, a glowing fiery red-orange charcoal ember bed and flame grate pulse on the chamber floor.
    - **Electric RF Induction**: When running on RF/FE, 3 glowing orange electric induction coils illuminate the chamber.
    - **Live Cooking Items**: All items actively being smelted float inside the chamber in an organized 4Ã—2 floating matrix, gently bobbing and spinning smoothly in the heat haze!
    - Emits server-side flame particles, smoke trails, and realistic furnace crackle audio.
- **8 Parallel Smelting Slots**:
  - Contains **8 Input Slots** (slots 0..7), **8 Output Slots** (slots 8..15), and **1 Solid Fuel Slot** (slot 16).
  - Automatically identifies and smelts any valid Minecraft or modded smelting recipe in parallel.
  - Smelting speed: **100 ticks (5.0 seconds)** per item (standard blast furnace speed).
- **Dual-Power System (Solid Fuel OR RF/FE)**:
  - **Burnt Material / Solid Fuel Mode**: Powers all 8 parallel smelting slots simultaneously from a single piece of fuel. Accepts Coal, Charcoal (1,600t), Coal Blocks (16,000t), Lava Buckets (20,000t), Blaze Rods (2,400t), Logs/Planks (300t), Sticks (100t), Petroleum Tar (1,600t), Bitumen (1,600t), or Ethanol Buckets (4,000t). 1 piece of Coal can smelt up to 16 items when all 8 slots are active!
  - **Electric / RF Mode**: Integrated 50,000 FE energy storage buffer. Consumes **30 FE/t** total while cooking items without burning any solid fuel.
- **Early-Game Accessible (NO Steel Required!)**:
  - Engineered specifically for early game automation prior to obtaining steel.
  - Crafted entirely from **Iron Ingots, Copper Ingots, Clay Bricks, Smooth Stone, Furnaces, Glass, Hoppers, and Droppers**. Absolutely no Steel Plates or Steel Ingots are needed!
- **Automation & Sided Interfacing**:
  - **Smelter Input Port**: Automatically routes items from hoppers/pipes into the 8 input slots.
  - **Smelter Output Port**: Automatically extracts completed items from the 8 output slots.
  - **Smelter Fuel/Energy Port**: Accepts solid fuel into the fuel slot and connects directly to external RF energy cables or power generators.
  - **Refinery Wrench Support**: Right-clicking the controller with a wrench projects a full 60-block 3D holographic blueprint with labeled block requirements.

#### Layer-by-Layer Multi-Smelter Schematic
Local coordinates relative to the controller facing North:
```
Layer 3 (Roof, Y=3):
  [Casing] [Casing] [Casing] [Casing] [Casing]
  [Casing] [Casing] [Casing] [Casing] [Casing]
  [Casing] [Casing] [Casing] [Casing] [Casing]

Layer 2 (Upper Smelting Chamber, Y=2):
  [Casing] [Window] [Window] [Window] [Casing]  <- Front
  [Casing] [ AIR  ] [ AIR  ] [ AIR  ] [Casing]  <- Hollow Core
  [Casing] [Casing] [Casing] [Casing] [Casing]  <- Back

Layer 1 (Lower Smelting Chamber, Y=1):
  [Casing] [Window] [Window] [Window] [Casing]  <- Front
  [Casing] [ AIR  ] [ AIR  ] [ AIR  ] [Casing]  <- Hollow Core
  [Casing] [Casing] [Casing] [Casing] [Casing]  <- Back

Layer 0 (Foundation & Ports, Y=0):
  [InPort] [Casing] [Controller] [Casing] [OutPort] <- Front
  [Casing] [Casing] [  Casing  ] [Casing] [Casing ] <- Middle
  [Casing] [Casing] [ FuelPort ] [Casing] [Casing ] <- Back
```
*(Note: Ports can be placed interchangeably in place of any Smelter Casing block for layout customization.)*

---

## 5. Thermodynamic Steam Power & Closed-Loop Energy

### Small Steam Boiler (3×3×3)
A compact packaged thermal generator for small-to-mid tier steam power generation.

- **Monolithic 3D Package Boiler Transformation**: Upon assembly, the entire 3×3×3 block structure transforms into a seamless monolithic industrial Scotch marine fire-tube package boiler model on structural steel I-beam skid runners:
  - **Horizontal Cylindrical Shell**: Solid steel cladding plates with riveted reinforcement straps (smooth exterior with no exposed cutaways).
  - **Hinged Front Boiler Door**: Massive bolted circular door with heavy black cast iron hinges, perimeter bolt circle, and central burner hub.
  - **Dynamic Sight-Glass Viewport**: Illuminates with an intense full-brightness fire glow when burning and reverts to dark soot-coated inspection glass on standby.
  - **Lower Cleanout Hatch**: Arched ash cleanout hatch with heavy latch handle.
  - **Overhead Steam Manifold & Piping**: Industrial steam header, red globe valve handwheels, safety relief valve, and dial pressure gauge.
  - **Chimney Smokestack**: Tall exhaust stack with reinforcement collar and top flange, venting steam and combustion smoke while active.
  - **Side Equipment**: Industrial electrical control cabinet (ICI Caldaie style) with status lamps on the right side; vertical water level column and dual feed-water booster pumps on the left.
- **Custom Industrial GUI**: Redesigned dark gunmetal steel GUI panel with dual fluid glass column gauges (Water Buffer on the left, High-Pressure Steam on the right), 2×2 fuel hearth slots, round redstone status indicator lamp, firebox flame viewport, real-time status header badge, and overheat hazard warning bar.
- **Capacity**: 16,000 mB Water, 16,000 mB Steam, 4 Fuel slots.
- **Performance**: Consumes 50 mB Water -> Produces 100 mB Steam per cycle.
- **Redstone Requirement**: Requires an active redstone signal to burn fuel.
- **Meltdown Safety Warning**: If the boiler is fueled and burning but **runs completely out of water**, an internal overheat timer begins ticking. If left uncooled without water for **1 full minute (1,200 ticks)**, the boiler undergoes a catastrophic steam explosion with a **7.0 blast power**, destroying the multiblock!

#### Small Boiler Layer Schematic
- **Layer 0 (Base)**:
  - Front: `(-1, 0, 0)` **Boiler Water Port**, `(0, 0, 0)` **Small Boiler Controller**, `(1, 0, 0)` **Boiler Fuel Port**
  - Center: `(0, 0, 1)` **Boiler Firebox Hearth** (flames ignite when active)
  - Rear: `(0, 0, 2)` **Boiler Steam Output Port**
  - Corners: **Steel Boiler Casing**
- **Layer 1 (Heat Exchanger)**:
  - Center `(0, 1, 1)`: **Boiler Pressure Pipe**
  - Surrounding 8 blocks: **Steel Boiler Casing**
- **Layer 2 (Roof)**:
  - 9 **Steel Boiler Casing**

---

### Large Heavy Steam Boiler (5×5×5)
An industrial-grade boiler engineered for massive steam output.

- **Monolithic 3D Scotch Marine Package Boiler**: Upon assembly, the entire 5×5×5 structure transforms into a massive monolithic twin-furnace industrial Scotch marine boiler model on structural steel I-beam skid runners:
  - **Horizontal Cylindrical Pressure Vessel**: Heavy riveted steel cladding with dual support saddles.
  - **Dual Front Furnace Doors**: Twin circular front doors with heavy black cast iron hinges, perimeter bolt circle, and central burner hubs.
  - **Twin Emissive Burner Viewports**: Dynamic fiery glow when active (`15728880` full brightness), reverting to dark soot-coated glass when idle.
  - **Twin Smokestacks**: Dual tall exhaust stacks venting steam and smoke during operation.
  - **Overhead Steam Manifold & Valve Piping**: Heavy steam piping with dual valve handwheels and central dial pressure gauge.
  - **Catwalk & Control Console**: Maintenance platform with safety railing, access ladder, side electrical cabinet, and twin vertical water level sight columns.
- **Custom Industrial GUI**: High-capacity dark steel GUI with dual water and steam buffer columns, 8-slot fuel buffer, combustion viewport, redstone lamp, and meltdown alert.
- **Capacity**: 64,000 mB Water, 64,000 mB Steam, 8 Fuel slots.
- **Performance**: 5× production rate (250 mB Water -> 500 mB Steam per cycle).
- **Redstone Requirement**: Requires an active redstone signal to burn.
- **Meltdown Condition**: Running dry for 60 seconds triggers a **massive 10.0 power crater explosion**!

#### Large Boiler Layer Schematic
- **Layer 0 (Foundation & Hearth)**:
  - Front: `(-1, 0, 0)` Water Port, `(0, 0, 0)` Large Controller, `(1, 0, 0)` Fuel Port
  - Rear: `(0, 0, 4)` Steam Port
  - Center 3×3 hearth (x in [-1, 1], z in [1, 3]): 9 **Boiler Firebox Hearth** blocks
  - Perimeter: **Steel Boiler Casing**
- **Layers 1–3 (Core Pressure Piping)**:
  - Center 3×3×3 core: 27 **Boiler Pressure Pipes** transferring heat
  - Outer walls: **Steel Boiler Casing**
- **Layer 4 (Roof)**:
  - 25 **Steel Boiler Casing** covering the entire top.

---

### Small & Large Diesel-Fired Steam Boilers (3×3×3 & 5×5×5)
Modern liquid-fueled steam boilers designed to burn refined **Diesel** fuel instead of solid combustibles.

- **Small Diesel Boiler (3×3×3)**:
  - Controller: `refinery:small_diesel_boiler_controller`
  - Replaces solid fuel with liquid **Diesel** combustion via precision fuel injectors and high-pressure fuel rail.
  - **Tanks**: 16,000 mB Diesel, 16,000 mB Water, 16,000 mB High-Pressure Steam.
  - **Consumption & Output**: Consumes 10 mB Diesel + 50 mB Water -> Produces 100 mB Steam per operational cycle.
  - **Custom 3-Tank Industrial GUI**: Features 3 recessed fluid columns (Diesel, Water, Steam) with bucket fill/drain slots, redstone signal indicator, burning flame viewport, and status badges.
- **Large Diesel Boiler (5×5×5)**:
  - Controller: `refinery:large_diesel_boiler_controller`
  - Heavy twin-furnace high-volume diesel steam plant.
  - **Tanks**: 64,000 mB Diesel, 64,000 mB Water, 64,000 mB High-Pressure Steam.
  - **Consumption & Output**: Consumes 50 mB Diesel + 250 mB Water -> Produces 500 mB Steam per operational cycle.
  - **Custom 3-Tank Industrial GUI**: High-capacity Diesel, Water, and Steam gauges with automated port interfacing.
- **Structure**: Uses the exact same multiblock casing, pipe, and port layouts as the standard boilers, swapping only the controller block to `Small Diesel Boiler Controller` or `Large Diesel Boiler Controller`. Water and Diesel can be supplied through ports or directly into the controller.

---

### Steam Turbine Generator (3Ã—3Ã—3)
A heavy Rankine-cycle turbine converting High Pressure Steam into Forge Energy and recyclable water.

- **Capacity**: 32,000 mB Steam, 16,000 mB Water, **1,000,000 FE battery**.
- **Thermodynamic Conversion**:
  - 1 mB High Pressure Steam -> 20 FE + 0.5 mB Condensed Water.
  - Consumes up to 50 mB Steam/t -> Generates **1,000 FE/t**.
  - Output extraction rate: Up to 10,000 FE/t via Energy Port or Controller.

#### Turbine Layer Schematic
- **Layer 0 (Base & Ports)**:
  - Front: `(-1, 0, 0)` **Turbine Steam Port**, `(0, 0, 0)` **Turbine Controller**, `(1, 0, 0)` **Turbine Energy Port**
  - Center `(0, 0, 1)`: **Turbine Rotor Shaft** (Bottom segment)
  - Rear `(0, 0, 2)`: **Turbine Water Port** (Condensed water drain)
  - Corners: **Turbine Casing**
- **Layers 1 & 2 (Rotor Housing)**:
  - Center `(0, y, 1)`: **Turbine Rotor Shaft** (spinning blades)
  - Surrounding 8 blocks on each layer: **Turbine Casing**

---

### The Closed-Loop Rankine Cycle
Connect the **Turbine Water Output Port** back into the **Boiler Water Input Port** using fluid pipes!
Since the turbine condenses steam back into water at a 2:1 ratio, a closed loop with a modest buffer top-up creates an endless, self-sustaining thermodynamic power plant.

---

## 6. Petroleum Refining & Chemical Byproducts

### Petroleum Refinery (3Ã—3Ã—4)
A multi-stage fractional distillation column that refines raw Crude Oil into commercial fuels and chemical feedstocks.

- **Dimensions**: 3 wide Ã— 4 tall Ã— 3 deep (36 blocks total).
- **Operation**: Consumes 200 mB Crude Oil + 40 FE/t over 100 ticks.
- **Outputs**:
  - **120 mB Diesel** (drained from Diesel Port)
  - **60 mB Gasoline** (drained from Gas Port)
  - **Petroleum Tar** (ejected to Byproduct Port)
  - **Sulfur Dust** (ejected to Byproduct Port)

#### Refinery Layer Schematic
- **Layer 0 (Base)**: Controller front center `(0, 0, 0)`, Crude Oil Port `(-1, 0, 0)`, Diesel Port `(1, 0, 0)`, Gas Port `(0, 0, 2)`, Byproduct Port `(1, 0, 1)`. Reinforced Casings fill the rest.
- **Layer 1 (Furnace)**: Combustion Chambers.
- **Layer 2 (Distillation)**: Distillation Columns.
- **Layer 3 (Condenser)**: Heat Exchangers.

---

### Oil Sand Cracking & Extraction

The Petroleum Refinery features a dedicated high-pressure **Feedstock Input Slot** directly above the burner slot in its interface:

- **Compatible Inputs**: Accepts `refinery:oil_sand` as well as any geological Oil Sand from mods such as the *Logistics* mod.
- **Automated Input**: Hoppers and item transport pipes can push Oil Sand directly into the **Crude Oil Port** or the top of the **Refinery Controller**.
- **Cracking Process**: Consumes 20 FE/t (or active burner flame) over 60 ticks (3 seconds) per block:
  - **+200 mB Crude Oil**: Injected directly into the internal Crude Oil tank!
  - **1 Sand (100% Guaranteed Yield)**: Ejected to the Byproduct output slots / Byproduct Port.
  - **Petroleum Tar (40% Chance)**: Ejected to the Byproduct output slots / Byproduct Port.
- **Full Automation**: Feeding Oil Sand into the Crude Oil Port generates Crude Oil on-site and refines it into Diesel and Gasoline in a single closed-loop industrial facility!

---

### Industrial Grinder & Pulverizer (3×3×3)
A heavy rock crushing and pulverizing plant that doubles raw ore yields and processes geological strata.

- **Monolithic 3D Rock Crusher Plant Transformation**: Upon assembly, the entire 3×3×3 structure transforms into a monolithic heavy rock crusher plant inspired by industrial quarry processing machinery:
  - **Structural Frame**: 4-post heavy structural steel framework with vibration-dampening foot pads and cross-bracing, with full non-occluding skylight propagation so the floor underneath the machine's legs remains visible and naturally lit.
  - **Crusher Box & Drive System**: Central eccentric jaw/cone crushing box fitted with side flywheels, counterweights, and belt-drive housings.
  - **Open-Top Feed Hopper with 3D Spinning Crusher Blades**: Deep open-top flared hopper bowl housing twin counter-rotating 3D toothed shredder rotors. When grinding, the 3D blades spin and pull the actual loaded input material down into the crushing throat.
  - **Top Hopper Direct Item Intake**: In addition to the GUI and ports, the top hopper acts as an open physical intake—items dropped into the top hopper bowl (from standalone conveyors, droppers, or players) or pushed via hoppers/pipes are automatically loaded into the grinder.
  - **Inclined Output Discharge Conveyor**: Angled right-side discharge conveyor with an empty chevron belt that dynamically displays the actual crushed output material climbing the belt as products are unloaded.
  - **Member Block Occlusion**: Surrounding member blocks automatically turn invisible without culling adjacent floor faces and forward wrench/hand interactions directly to the controller.
- **Custom Industrial GUI**: Dark gunmetal steel panel with 2-slot raw feed tray, 4-slot crushed output collection bay, central animated crushing chamber indicator, and real-time status header badge (`ONLINE`, `CRUSHING`, `IDLE`, `UNFORMED`).
- **Core Functionality**:
  - **Ore Doubling**: Pulverizes Raw Iron, Raw Gold, and Raw Copper (or ore blocks) into **2x Dust** (`refinery:iron_dust`, `gold_dust`, `copper_dust`). Smelting these dusts produces 2 ingots per raw ore!
  - **Stone Crushing**: Crushes Stone, Cobblestone, Deepslate, Andesite, Diorite, and Granite into **1x Gravel**.
  - **Sand Pulverizing**: Crushes Gravel into **1x Sand**.
  - **Ingot Grinding**: Converts Ingots into **1x Dust** for metallurgy and chemistry.
- **Specs**:
  - Energy Storage: 50,000 FE
  - Energy Consumption: 30 FE/t while grinding
  - Process Duration: 60 ticks (3.0 seconds) per batch
  - Internal Buffer: 4 Input slots + 4 Output slots

#### Grinder Layer Schematic (Local Coordinates)
- **Layer 0 (Foundation & Output)**:
  - `(0, 0, 0)`: **Industrial Grinder Controller** (Bottom Center Front)
  - `(1, 0, 0)`: **Grinder Discharge Port (Output)** (Bottom Front Right)
  - `(-1, 0, 0)`, `(-1, 0, 1)`, `(0, 0, 1)`, `(1, 0, 1)`, `(-1, 0, 2)`, `(0, 0, 2)`, `(1, 0, 2)`: **Grinder Structural Casing**
- **Layer 1 (Crushing Chamber & Spiked Drums)**:
  - `(0, 1, 0)`: **Grinder Chamber Window** (Front Center Viewing Grille)
  - `(0, 1, 1)`: **Grinder Crushing Chamber** (Hollow Air or Chamber Block containing the counter-rotating spiked drums)
  - `(-1, 1, 0)`, `(1, 1, 0)`, `(-1, 1, 1)`, `(1, 1, 1)`, `(-1, 1, 2)`, `(0, 1, 2)`, `(1, 1, 2)`: **Grinder Structural Casing**
- **Layer 2 (Feed Hopper & Input)**:
  - `(0, 2, 1)`: **Grinder Feed Port (Input)** (Top Center Hopper Chute)
  - All other 8 positions: **Grinder Structural Casing**

*(Note: Flexible port placement is supported—input ports and output ports can be placed anywhere along the outer casing walls to fit your automation layout).*

---

## 7. Comprehensive Recipe Codex

### Base Materials & Tools
| Result | Pattern / Ingredients |
| :--- | :--- |
| **Refinery Wrench** | Iron Ingot, Copper Ingot, Lever (Shaped) |
| **Heavy Steel Plate** | 1 Steel Ingot (Exclusively stamped in Industrial Metal Press) |
| **Bituminous Oil Sand** (x2) | 1 Sand + 1 Petroleum Tar (Shapeless) |
| **Gunpowder (from Sulfur)** | 1 Sulfur Dust + 1 Charcoal (Shapeless) |
| **Torches (from Tar)** | 1 Petroleum Tar + 1 Stick -> 4 Torches (Shapeless) |

### Standalone Automation Machines
| Machine | Crafting Ingredients |
| :--- | :--- |
| **Automated Planter** | 4 Iron Ingots, 2 Copper Ingots, 1 Iron Hoe (top-center), 1 Piston (center), 1 Redstone Dust (bottom-center) |
| **Automated Harvester** | 4 Iron Ingots, 2 Copper Ingots, 1 Shears (top-center), 1 Hopper (center), 1 Redstone Dust (bottom-center) |
| **Livestock Feeding Trough** | 4 Steel Plates, 2 Iron Bars, 1 Hay Bale |
| **Industrial Fermenter** | 4 Iron Ingots, 2 Copper Ingots, 1 Glass (top-center), 1 Cauldron (center), 1 Redstone Dust (bottom-center) |

### Multiblock Machine Blocks
| Machine Block | Crafting Ingredients |
| :--- | :--- |
| **Grinder Casing** (x4) | 4 Steel Plates + 4 Steel Ingots (Shaped ring) |
| **Grinder Controller** | 1 Grinder Casing + 4 Steel Plates + 2 Redstone Blocks + 1 Comparator + 1 Steel Ingot |
| **Grinder Chamber** | 4 Steel Plates + 2 Iron Bars + 2 Foundry Heat Glass |
| **Grinder Input Port** | 4 Steel Plates + 1 Hopper (Top funnel configuration) |
| **Grinder Output Port** | 4 Steel Plates + 1 Hopper (Bottom discharge configuration) |
| **Press Casing** (x4) | 4 Steel Ingots + 4 Iron Ingots around Smooth Stone |
| **Press Anvil Bed** | 3 Steel Ingots (top) + 1 Anvil (center) + 3 Iron Blocks (bottom) |
| **Press Hydraulic Head** | 4 Steel Ingots + 2 Pistons + 2 Redstone |
| **Metal Press Controller** | 4 Iron Ingots + 2 Steel Ingots + 2 Redstone Blocks + 1 Comparator |
| **Steel Boiler Casing** (x4) | 4 Steel Plates + 4 Steel Ingots around 1 Iron Block |
| **Boiler Firebox Hearth** | 4 Steel Plates + 4 Iron Bars + 1 Blast Furnace |
| **Boiler Pressure Pipe** (x4) | 4 Steel Plates in diamond ring |
| **Boiler Water Port** | 1 Steel Boiler Casing + 1 Water Bucket + 2 Steel Plates |
| **Boiler Steam Port** | 1 Steel Boiler Casing + 2 Boiler Pipes + 2 Steel Plates |
| **Boiler Fuel Port** | 1 Steel Boiler Casing + 2 Hoppers + 2 Steel Plates |
| **Small Boiler Controller** | 1 Steel Boiler Casing + 4 Steel Plates + 2 Steel Ingots + 2 Redstone |
| **Large Boiler Controller** | 1 Small Boiler Controller + 4 Steel Plates + 2 Steel Ingots + 2 Redstone Blocks |
| **Turbine Casing** (x4) | 4 Steel Plates + 4 Steel Ingots around Smooth Stone |
| **Turbine Rotor Shaft** (x2) | 4 Steel Plates around 1 Steel Ingot |
| **Turbine Steam Port** | 1 Turbine Casing + 2 Boiler Pipes + 2 Steel Plates |
| **Turbine Water Port** | 1 Turbine Casing + 2 Buckets + 2 Steel Plates |
| **Turbine Energy Port** | 1 Turbine Casing + 2 Redstone Blocks + 2 Steel Plates |
| **Steam Turbine Controller** | 1 Turbine Rotor + 4 Steel Plates + 2 Steel Ingots + 2 Redstone Blocks |

### Mineral Pulverizing, Alloying & Smelting
| Input | Machine / Process | Output |
| :--- | :--- | :--- |
| **Bituminous Oil Sand** (`refinery:oil_sand` or `logistics:core/oil_sand`, `oil_red_sand`, `oil_shale`) | Industrial Grinder | **3x Bitumen** (+ 50% chance for **1x Tar**) |
| **Bitumen** (Refinery or Logistics) | Furnace / Blast Furnace | **1x Petroleum Tar** |
| **Raw Iron / Iron Ore / Deepslate Iron Ore** | Industrial Grinder | **2x Iron Dust** (Ore Doubling!) |
| **Raw Gold / Gold Ore / Deepslate Gold Ore** | Industrial Grinder | **2x Gold Dust** (Ore Doubling!) |
| **Raw Copper / Copper Ore / Deepslate Copper Ore** | Industrial Grinder | **2x Copper Dust** (Ore Doubling!) |
| **Tin Ore / Deepslate Tin / Raw Tin** (`#c:ores/tin`, `#c:raw_materials/tin`) | Industrial Grinder | **2x Tin Dust** (Ore Doubling!) |
| **Coal Ore / Deepslate Coal Ore** | Industrial Grinder | **2x Coal Dust** |
| **Coal** (`Items.COAL` or `#c:coals`) | Industrial Grinder | **1x Coal Dust** |
| **Block of Coal** | Industrial Grinder | **9x Coal Dust** |
| **Charcoal** | Industrial Grinder | **1x Charcoal Dust** |
| **Diamond / Diamond Ore** | Industrial Grinder | **1x / 2x Diamond Dust** |
| **Emerald / Emerald Ore** | Industrial Grinder | **1x / 2x Emerald Dust** |
| **Lapis Lazuli / Lapis Ore** | Industrial Grinder | **1x / 4x Lapis Dust** |
| **Nether Quartz / Quartz Ore** | Industrial Grinder | **1x / 2x Quartz Dust** |
| **Amethyst Shard / Block** | Industrial Grinder | **1x / 4x Amethyst Dust** |
| **Obsidian** | Industrial Grinder | **1x Obsidian Dust** |
| **Ender Pearl** | Industrial Grinder | **1x Ender Pearl Dust** |
| **Caterium / Echonite / Lumenite Ores or Raw** | Industrial Grinder | **2x Respective Dust** |
| **Caterium / Echonite / Lumenite Ingots** | Industrial Grinder | **1x Respective Dust** |
| **Stone / Cobblestone / Deepslate / Andesite / Diorite / Granite** | Industrial Grinder | **1x Gravel** |
| **Gravel** | Industrial Grinder | **1x Sand** |
| **Iron / Gold / Copper Ingot** | Industrial Grinder | **1x Respective Dust** |
| **Tin Ingot** | Industrial Grinder | **1x Tin Dust** |
| **Bronze Ingot** | Industrial Grinder | **1x Bronze Dust** |
| **Iron / Gold / Copper / Tin / Bronze Dust** | Furnace / Blast Furnace | **1x Respective Ingot** |
| **3 Copper Ingots + 1 Tin Ingot** | Crafting Table (Shapeless) | **4x Bronze Ingot** |
| **3 Copper Dust + 1 Tin Dust** | Crafting Table (Shapeless) | **4x Bronze Dust** |
| **Any Ingot (Steel, Iron, Copper, Gold, Tin, Bronze)** | Industrial Metal Press | **1x Respective Plate** |

### Logistics Mod Cross-Compatibility & Tar Recipes
*(These recipes activate dynamically when the Logistics mod is detected with zero hard dependencies)*
- **Smart Stacking Byproducts**: When Logistics is installed, the Industrial Grinder and Refinery automatically output native `logistics:core/tar` and `logistics:core/bitumen` so they immediately stack with your existing pipes and storage. If standalone, it gracefully outputs `refinery:tar` and `refinery:bitumen`.
- **Crafting Logistics Fluid Pipes**: `refinery:tar` can be crafted directly with any transport pipe:
  - `refinery:tar` + `copper_transport_pipe` -> `copper_fluid_pipe`
  - `refinery:tar` + `stone_transport_pipe` -> `stone_fluid_pipe`
  - `refinery:tar` + `gold_transport_pipe` -> `gold_fluid_pipe`
  - `refinery:tar` + `item_passthrough_pipe` -> `bypass_fluid_pipe`
  - `refinery:tar` + `item_extractor_pipe` -> `fluid_extractor_pipe`
  - `refinery:tar` + `item_insertion_pipe` -> `insertion_fluid_pipe`
  - `refinery:tar` + `item_merger_pipe` -> `merger_fluid_pipe`
  - `refinery:tar` + `item_void_pipe` -> `void_fluid_pipe`
- **Polymer & Utility Crafting**:
  - `refinery:tar` + Coal Dust -> `synthetic_polymer`
  - `refinery:tar` + Piston -> `sticky_piston`
  - `refinery:tar` + 3 String -> 2 `lead`
- **Instant 1:1 Interchangeability**:
  - 1 `refinery:tar` <=> 1 `logistics:core/tar` (shapeless crafting)
  - 1 `refinery:bitumen` <=> 1 `logistics:core/bitumen` (shapeless crafting)

---

## 8. Automation, Conveyors, Pipes, & Interfacing

### Industrial Belt Conveyor (`refinery:conveyor_belt`)
A modular low-profile belt conveyor system for high-throughput factory automation and dedicated item transport:
- **Monolithic 3D Model**: High-detail extruded anodized aluminum bed frame with longitudinal side T-slot channels, recessed deck plate, heavy cast steel mounting feet, and sleek guide lips.
- **Matte Black Industrial Belt**: High-density vulcanized rubber canvas belt with micro-grip friction ribs.
- **Calibrated Forward Motion & Speed**:
  - Smooth 0.05 blocks/tick (~1 block/sec) item conveyance, calibrated to half speed for realistic factory pacing.
  - Seamless UV texture scrolling moves forward in exact synchronization with item travel, while the return belt travels in reverse.
  - Precision brushed stainless steel cylindrical end rollers physically rotate around their axles at matching velocity.
  - Real-time physical item centering keeps dropped items aligned along the conveyor's center axis.
  - **Item-Only Conveyance**: Specifically transports items (`ItemEntity`) without pushing players, animals, or monsters, allowing smooth foot traversal across the belt without interference.
- **Intelligent Seamless Snapping**:
  - Placing conveyors in a row automatically snaps them together into a continuous, seamless belt assembly.
  - Intermediate end terminals open up, seamlessly joining side rails, T-slots, and belt surfaces with zero gaps or visible seams.
  - Head and tail rollers and machined bearing plates automatically render only at the exposed line terminals.
- **Automated Machine & Container Integration**:
  - Items reaching the conveyor head automatically inject into adjacent inventories, chests, hoppers, furnaces, and machines via the NeoForge item transfer system.
  - Hoppers, droppers, and pipes can directly deposit items onto the belt surface.
- **Redstone & Wrench Automation**:
  - Redstone signals pause conveyor operation, halting belt scrolling and item transit.
  - Right-click or Sneak-Right-Click with the **Refinery Wrench** rotates the conveyor orientation with instant neighbor re-connection.
- **Crafting Recipe**: 3 Petroleum Tar + 1 Redstone Dust + 5 Iron Ingots produce 4 Industrial Conveyor Belts.

All ports and controllers implement native Minecraft and NeoForge capability standards:

- **Fluid Automation (`Capabilities.Fluid.BLOCK`)**:
  - Connect fluid pipes directly to any Port.
  - **Water Ports**: Accept fluid insertion.
  - **Steam Ports**: Fluid extraction (pull out high pressure steam).
  - **Fuel / Byproduct Ports**: Standard item capability.
- **Energy Transfer (`Capabilities.Energy.BLOCK`)**:
  - Connect energy conduits or cables to the **Turbine Energy Port**, **Turbine Controller**, **Refinery Controller**, **Metal Press Controller**, **Industrial Grinder Controller**, **Auto Planter**, or **Auto Harvester**.
- **Item Automation (`Capabilities.Item.BLOCK` / `WorldlyContainer`)**:
  - **Industrial Grinder**: Connect pipes/hoppers to **Grinder Input Port** (Layer 2) to supply ores or stone, and **Grinder Output Port** (Layer 0) to extract dusts, gravel, and sand automatically.
  - **Blast Smelter**: Connect pipes/hoppers to **Foundry Input Port** (Iron & Coal) and **Foundry Output Port** (Steel Ingots).
  - **Metal Press**: Insert into top/sides (Ingots); extract from bottom face (Plates).
  - **Steam Boilers**: Insert burnable fuel into the **Boiler Fuel Port**.
  - **Harvester**: Automatically dumps into chests or conduits behind or underneath it.


---

## 9. Crude Oil World Generation & Retrogen

### In-World Fluid Crude Oil
Crude Oil is a heavy, dark liquid naturally present in subterranean geologic traps:
- **Liquid Blocks**: `refinery:crude_oil` (source) and `refinery:flowing_crude_oil`.
- **Block Off & Displace**: Fully follows standard water/lava placement rules â€” can be blocked off, dammed, or displaced by placing any solid block directly into it.
- **Viscous Movement Physics**: Thick, viscous fluid physics â€” entities sink at a steady, damped rate, can wade and swim through it with WASD, and can swim upwards to safety by holding Spacebar.
- **Handling**: Can be collected using standard Buckets to obtain **Crude Oil Buckets** (`refinery:crude_oil_bucket`), pumped via fluid extraction pipes, or manually right-clicked onto a Refinery **Crude Oil Input Port**.
- **Port Bucket Interaction**: Right-clicking the **Crude Oil Input Port** with a Crude Oil Bucket empties it into the refinery (+1,000 mB). Right-clicking the **Diesel Port** or **Gas Port** with an empty bucket collects refined fuel (-1,000 mB).

### Deposit Structures & Capacities
1. **Standard Oil Pit (600,000 mB â€“ 1,000,000 mB)**:
   - **Surface Crater**: A large natural dark petroleum lake (20-block diameter) with shoreline beaches of **Bituminous Oil Sand** (`refinery:oil_sand`).
   - **Feeder Conduit**: A vertical cylindrical conduit dropping through the earth.
   - **Subterranean Reservoir**: A massive subterranean cavern embedded in the deep stone crust, filled with hundreds of source blocks and encased in an oil sand stratum.
   - **Yield**: Provides 600 to 1,000 buckets (600k to 1,000k mB) of pumpable crude oil, plus dozens of mineable oil sand blocks.
2. **Rare Super-Geyser (2,000,000 mB)**:
   - A rare high-pressure geologic anomaly (default 20% of spawned oil pits).
   - Features an active towering geyser fountain erupting above the surface lake (28-block diameter), fed by an immense subterranean reservoir dome holding **2,000 crude oil source blocks** (2,000,000 mB).

### Configuration (`config/refinery.toml`)
All generation parameters are fully customizable:
- `enableOilRetrogen` (default: `true`): Enables automatic generation in existing explored chunks.
- `oilPitSpawnChance` (default: `0.005`): ~0.5% chance per chunk in allowed biomes (~1 in 200 chunks).
- `oceanSpawnChanceMultiplier` (default: `0.20`): Multiplier for ocean biomes (80% frequency reduction) to keep vast deep oceans balanced.
- `minChunkDistanceBetweenWells` (default: `40`): Minimum chunk distance between oil deposits (640 blocks) to space out wells and prevent clustering (range 1â€“512).
- `rareGeyserChance` (default: `0.20`): 20% chance for a spawned pit to be a 2,000k mB super-geyser.
- `minOilVolumeMb` / `maxOilVolumeMb` (default: `600000` / `1000000`): Standard pit capacity range.
- `geyserOilVolumeMb` (default: `2000000`): Super-geyser oil capacity.
- `allowedBiomes`: Configurable list of biome IDs or tags. By default includes `#minecraft:is_ocean`, `#minecraft:is_deep_ocean`, `#minecraft:is_desert`, `#minecraft:is_badlands`, all ocean types, deserts, and badlands/mesas. Compatible with modded biomes!

### Retrogen Persistence (No Re-Gen Once Mined)
- The retrogen manager uses a robust `RecordCodecBuilder` CompoundTag system to record all evaluated and generated chunk coordinates to `<world>/data/oil_retrogen.dat`.
- Once a chunk has had its oil generated (or evaluated), it is permanently registered. **It will never regenerate oil even after being mined, drilled, or pumped dry.**

### Administrative Commands
Commands can be issued via `/admin` or `/refinery`:
- `/admin spawn_oil_pit [true|false]` or `/admin spawn_oil [true|false]`: Spawns a standard pit (`false`) or super-geyser (`true`) at your position and registers the location.
- `/admin spawn_geyser`: Shortcut to spawn a 2,000,000 mB super-geyser.
- `/admin oil_stats`: Displays scanned chunks and total generated oil wells.
- `/admin reset_oil_retrogen`: Resets retrogen history for the world.
- *(Also mirrored under `/refinery spawn_oil_pit`, `/refinery spawn_geyser`, `/refinery stats`)*

---

## 10. BuildCraft-Style Pipe Logic Gates & Wires

Industrial Refinery brings back the classic, beloved **BuildCraft Pipe Logic Gate** automation system â€” fully re-engineered for Minecraft 26.3 NeoForge!

Gates are slim, directional logic controllers that can be attached to **any face of a pipe** (including Logistics mod Item & Fluid pipes, Refinery multiblock ports, vanilla containers, or any modded pipes/machines).

```
   [Chest / Tank] <--- (Pipe Arm) ---> [Pipe Block] === [Gate Plate] ---> (Redstone / Wire Signal)
                                             |
                                    [Autarchic Pulsar]
                               (Extracts items / fluids)
```

---

### Overview & Pipe Attachment
- **1x1x1 In-Space Snapping (Zero Neighbor Footprint)**: Right-clicking any face (North, South, East, West, Up, Down) of a pipe or container snaps the gate directly onto that collar **within the exact same 1x1x1 block space** as the pipe!
  - The adjacent block coordinate remains 100% completely open air, allowing players to walk through freely, lay parallel pipelines directly adjacent, or place nearby machines with zero obstruction.
  - The gate forms a sleek 3-pixel-thick metallic bezel plate adhering snugly to the pipe collar.
  - If the supporting pipe or block is broken, the attached gate automatically pops off and drops its item.
- **Programming GUI**: Right-click any placed gate with an empty hand to open its industrial programming console.
  - **Left-Click** a Trigger or Action to cycle forward.
  - **Right-Click** a Trigger or Action to cycle backward.
  - An animated **Status LED** glows green whenever the condition is actively satisfied in the world.
- **Dismantling & Upgrades**:
  - Right-click with a **Refinery Wrench** to instantly dismantle the gate without tools.
  - Right-click with an **Autarchic Chipset** to instantly upgrade any standard gate into an Autarchic Gate in place!
  - Right-click with a **Pipe Wire** to connect or toggle wires.
- **Redstone Output**: When an active pair fires the `Emit Redstone` action, the gate emits strong redstone power (signal 15) to adjacent redstone dust, repeaters, pistons, and doors.

---

### Gate Tiers

| Gate Tier | Logic Slots | Pipe Wires | Pulsar Capable? | Signature Bezel |
| :--- | :---: | :---: | :---: | :--- |
| **Basic Gate** | 1 Pair | None | With Autarchic Upgrade | Dark Industrial Slate & Riveted Steel |
| **Iron Gate** | 2 Pairs | Red, Blue | With Autarchic Upgrade | Polished Silver Iron Trim |
| **Gold Gate** | 4 Pairs | Red, Blue, Green, Yellow | With Autarchic Upgrade | Radiant Lustrous Gold |
| **Diamond Gate** | 8 Pairs | Red, Blue, Green, Yellow | With Autarchic Upgrade | Crystalline Brilliant Cyan Diamond |

Each gate also has a dedicated **Autarchic** variant:
- **Basic Autarchic Gate**
- **Iron Autarchic Gate**
- **Gold Autarchic Gate**
- **Diamond Autarchic Gate**

---

### Autarchic Pulsar (Automated Extraction)
One of the most celebrated features of classic BuildCraft:
- Gates equipped with an **Autarchic Chipset** unlock the `Autarchic Pulse (Item)` and `Autarchic Pulse (Fluid)` actions.
- When its condition is met, the gate pulses every 20 ticks (1 second) and **automatically pulls 1 item** from the connected chest/machine directly into the pipe, or **pumps 250 mB of fluid** into the fluid pipe!
- **No external engines, redstone clocks, or wooden pumps required**!
- Native seamless support for:
  - **Logistics Mod Item Pipes & Fluid Pipes** (inserts traveling item packets into pipe network).
  - **NeoForge Item & Fluid Transfer Handlers** (works on all chests, barrels, droppers, and modded machines).
  - **Refinery Multiblock Ports** (Crude Oil Port, Diesel Port, Boiler Ports, Byproduct Ports).

---

### Pipe Wire Networks (Red, Blue, Green, Yellow)
Gates can communicate across long distances without bulky redstone dust lines:
- **Placing Wires on Pipes**: Right-click any pipe segment directly while holding a **Red, Blue, Green, or Yellow Pipe Wire** to string that wire along the pipe!
  - Multiple wire colors can be attached to the exact same pipe simultaneously (visible as distinct colored conductors along the pipe corners).
  - Right-click with a **Refinery Wrench** to pop off and retrieve all attached wires.
  - If the pipe block is broken, all attached pipe wires automatically drop as items.
- **Action (`Emit Wire`)**: Broadcasts that wire's logic state along contiguous pipe networks up to 32 blocks.
- **Trigger (`Wire ON` / `Wire OFF`)**: Reads the active wire broadcast from any other gate or wire on that pipe network.
- **Wire HUD**: The gate GUI displays real-time glowing status dots for all supported pipe wire channels. When an active signal is pulsing, the wires on the pipe illuminate brightly!

---

### Machine & Mod Interoperability
- **Logistics Mod & Pipe Systems**: All multiblock ports (Refinery Diesel, Gas, Crude Oil, Boiler Water, Boiler Steam, Turbine Steam, Turbine Water) and Controllers feature full NeoForge Transfer API `ResourceHandler<FluidResource>` implementations with transaction support. Logistics fluid extraction pipes, pumps, wooden/redstone engines, and gates seamlessly drain and fill tanks.
- **Track & Trains Mod Compatibility**: Machines with hybrid item and fluid inventories (such as the Track & Trains Fluid Loader and Unloader) are fully supported. Gates automatically discover both fluid and item capabilities simultaneously on any face.
- **Accurate Liquid Detection**:
  - `Tank Empty`: Triggers when the tank has 0 mB.
  - `Has Fluid`: Triggers when the tank contains at least 1 mB.
  - `Space in Tank`: Accurately triggers whenever the tank has room for more fluid (including empty tanks).
  - `Tank Full`: Triggers when the fluid volume reaches maximum capacity.

---

### Trigger & Action Codex

#### Triggers (Conditions):
- `Redstone ON` / `Redstone OFF`: Evaluates adjacent redstone power.
- `Inventory Empty`: Connected container has 0 items.
- `Has Items`: Connected container has at least 1 item.
- `Space in Inventory`: Container has free space or incomplete item stacks.
- `Inventory Full`: Container has zero free slots or capacity.
- `Tank Empty`: Connected fluid tank has 0 mB.
- `Has Fluid`: Connected fluid tank contains fluid.
- `Space in Tank`: Connected fluid tank has room for more fluid.
- `Tank Full`: Connected fluid tank is 100% full.
- `Pipe Empty`: Attached pipe currently has no traveling items or fluids.
- `Pipe Flowing`: Attached pipe has items or fluids traversing it.
- `Machine Active` / `Machine Idle`: Evaluates whether connected furnace, boiler, or machine is burning or operating.
- `Red / Blue / Green / Yellow Wire ON` & `OFF`: Evaluates pipe network wire signals.

#### Actions (Outputs):
- `Emit Redstone`: Constant redstone signal (power 15).
- `Redstone Pulse`: Pulses redstone on and off every 1 second (ideal for clocks).
- `Autarchic Pulse (Item)`: Extracts 1 item from adjacent container into pipe.
- `Autarchic Pulse (Fluid)`: Pumps 250 mB from adjacent tank into fluid pipe.
- `Emit Red / Blue / Green / Yellow Wire`: Broadcasts logic signal to other gates along connected pipes.

---

### Crafting & In-World Upgrades

#### 1. In-World Upgrade with Autarchic Chipset
Hold an **Autarchic Chipset** and right-click any placed in-world Gate. It instantly upgrades into its Autarchic variant with a sound effect and cyan particle flare!

#### 2. Crafting Recipes:
- **Basic Gate**:
  - `Redstone Dust` (top/bottom) + `Iron Ingot` (left/right) + `Redstone Torch` (center) $\rightarrow$ 1x Basic Gate.
- **Iron Gate**:
  - `Basic Gate` (center) + 2x `Iron Ingot` + 2x `Redstone Dust` $\rightarrow$ 1x Iron Gate.
- **Gold Gate**:
  - `Iron Gate` (center) + 2x `Gold Ingot` + 2x `Redstone Dust` $\rightarrow$ 1x Gold Gate.
- **Diamond Gate**:
  - `Gold Gate` (center) + 2x `Diamond` + 2x `Redstone Dust` $\rightarrow$ 1x Diamond Gate.
- **Autarchic Chipset**:
  - `Ender Pearl` (center) + 2x `Redstone Dust` + 2x `Gold Nugget` $\rightarrow$ 1x Autarchic Chipset.
- **Autarchic Gates (Crafting Grid)**:
  - Any Gate + 1x `Autarchic Chipset` (shapeless) $\rightarrow$ Autarchic Gate variant.
- **Pipe Wires (4x per craft)**:
  - `String` + `Redstone Dust` + `Dye` (Red, Blue, Green, or Yellow) $\rightarrow$ 4x Pipe Wire.

---

## 10.5. Industrial Fluid Pumps & 3D In-World Suction Hose

The **Industrial Fluid Pump** system features high-performance portable engine-driven pumps inspired by heavy-duty commercial centrifugal water pumps (Superkiger WP40X). Available in three specialized variants with distinct roll-cage frame colorings and tailored interfaces:

- **Coal Fluid Pump (`refinery:coal_fluid_pump`)**: Heavy Cast-Iron Dark Charcoal frame (`#374151`). Burns coal, charcoal, and other solid fuels at 1 coal per 3 seconds (60 ticks) of active pumping.
- **Diesel Fluid Pump (`refinery:diesel_fluid_pump`)**: Caterpillar Yellow-Orange frame (`#FA983A`). Internal 4,000 mB diesel tank consuming 10 mB per bucket pumped (1 bucket powers 100 buckets pumped). Refueled via bucket slots or fluid pipes.
- **Electric Fluid Pump (`refinery:electric_fluid_pump`)**: High-Voltage Electric Blue frame (`#2563EB`). 20,000 FE internal buffer consuming 400 FE per bucket pumped (20 FE/t).

### Key Features & Operation
- **Monolithic 3D Model with Frame Tinting**:
  - Full tubular steel roll-cage color-coded by fuel type (Charcoal, Caterpillar Orange, Electric Blue) with rubber vibration damping pads.
  - Industrial combustion engine with a safety-yellow recoil pull-starter shroud and pull handle.
  - White enamel fuel tank with chrome gas cap and embossed logo badge.
  - Heavy cast aluminum centrifugal volute pump casing with circular front inspection cover.
  - Vertical discharge neck and top flange for fluid pipes.
  - Front suction intake collar for in-world hose attachment.
- **In-World 3D Suction Hose**:
  - Renders a 3D flexible, spiral-ribbed industrial suction hose extending directly from the pump's front suction port to the set in-world intake point up to 64 blocks away.
  - Follows a natural Cubic Bezier curve with realistic gravity catenary sag.
  - Complete with a perforated brass suction strainer basket submerged at the intake target.
  - Features an active pulsing suction wave animation along the hose while the machine is actively pumping!
- **Refinery Wrench Suction Point Linking**:
  - **Link Suction Intake**: Sneak-right-click the placed Fluid Pump with a **Refinery Wrench** to select it. Then, right-click or sneak-click **any block or fluid source** anywhere in the world (up to 64 blocks away) to link the suction intake point.
  - **Direct Fluid Clicking**: Aim directly at the surface of lakes, rivers, ocean trenches, or lava pools to submerge the intake hose right into the liquid.
  - **Dismantle**: Sneak-right-clicking the pump twice in succession with the wrench instantly dismantles and collects the pump.
- **Contiguous Source Flood-Fill (BFS)**:
  - Automatically discovers all contiguous, directly connected fluid source blocks of the target liquid.
  - Pumps blocks **one at a time from the pumping point outward**.
  - Traverses across both source and flowing water in Drain mode, pumping water bodies completely dry.
  - Unconnected fluid pockets are never touched, ensuring clean and predictable draining.
- **Infinite Water Mode (`[ INFINITE ]` / `[ DRAIN ]`)**:
  - Toggleable via GUI button or by **Shift-Right-Clicking the pump block in-world** with an empty hand or wrench!
  - **INFINITE (Infinite Water)**: When pumping water sources, generates 1,000 mB of water per cycle without destroying or removing source blocks. Perfect for continuous boiler feed-water and cooling!
  - **DRAIN (Drain Pool)**: Removes fluid source blocks each cycle, pumping caverns, lakes, and quarries completely dry. (Non-water fluids like Lava and Crude Oil always drain).
- **Dedicated GUIs**:
  - Tailored to each machine type with no unnecessary gauges or slots. Intake coordinates have been streamlined away for maximum clarity.
- **Redstone Automation**:
  - GUI button toggles between `RS: Ignored`, `RS: High` (only runs when powered), and `RS: Low` (runs when unpowered, pauses with redstone signal).
- **Logistics Craft & Fluid Pipe Integration**:
  - The top vertical discharge flange automatically pumps fluid directly into adjacent **Logistics Craft fluid pipes** and fluid tanks every tick.
  - Exposes standard NeoForge `Capabilities.Fluid.BLOCK` on all faces for external pipe extraction.
- **Crafting Recipe**:
  - 4x Iron Ingot, 1x Bucket (top center), 1x Piston (center), 1x Redstone Dust (bottom center), 2x Smooth Stone (bottom corners).

---

## 10.6. Industrial Fluid Discharge Block (`refinery:fluid_discharge`)

The **Industrial Fluid Discharge** block is an industrial fire-engine red foot valve and slotted strainer outlet engineered to accept piped fluids from Logistics Automation pipes and standard fluid capabilities, featuring two distinct operational modes:

### Key Features & Operation
- **Monolithic 3D Model**:
  - High-gloss safety/fire-engine red cylindrical housing with octagonal base collar, 4 vertical cast reinforcement ribs, dual-tier mounting flanges with side ear lugs, dark graphite sealing gasket, polished silver/chrome hexagonal bolt heads, and threaded steel studs with bottom hex nuts.
  - Domed slotted strainer cage with vertical vent slots revealing a dark interior valve chamber and bronze flapper plate, capped with a perforated top dome crown.
- **Dual Operational Modes**:
  - **`VOID / DUMP` Mode**: Instantly destroys / dumps any fluid received from pipe systems without backing up pipe networks or spilling into the world. Ideal for discarding surplus refinery byproducts, contaminated wastewater, or unwanted fluids.
  - **`FILL` Mode**: Discharges fluid directly into the world at the **exact same Y level** ($Y = Y_{discharge}$), converting contiguous air and replaceable blocks into genuine fluid source blocks (consuming 1,000 mB per block).
  - **Contiguous Flood-Fill (BFS) with Barrier Isolation**: Uses strict 4-way orthogonal Breadth-First Search on the horizontal plane. Fluid only expands through the discharge block itself or contiguous blocks of the same fluid. It will **never jump over walls or barriers** into neighboring pits!
- **Snap Interaction & Mode Toggling**:
  - Right-click with empty hand or with a **Refinery Wrench** to toggle instantly between `VOID` and `FILL` mode with sound and chat feedback.
  - Sneak-right-click with an empty hand to inspect live status: current mode, buffer fluid volume, active state, and redstone signal.
- **Redstone Automation**:
  - Receiving a redstone signal safely pauses discharging operations.
- **Crafting Recipe**:
  - 4x Iron Ingot, 2x Red Dye, 1x Iron Bars (top center), 1x Bucket (center), 1x Redstone Dust (bottom center).

---

## 11. Continuous Molten Metallurgy & Ingot Casting Suite

The Continuous Molten Metallurgy suite introduces an automated, visually stunning foundry pipeline:
```
[Mineral Dusts / Ores / Sand]
             |
             v
+-------------------------------+
|  Electric Arc Melter (3x3x3)  |  <--- 40 FE/t, Electrodes & Arc Flash Animation
+-------------------------------+
             | (1,000 mB Molten Liquid)
             v
+-------------------------------+
|   Open Casting Channel (3D)   |  <--- Visible Glowing Liquid Flow Trough
+-------------------------------+
             |
             v
+-------------------------------+
| Continuous Casting Press(3x3) |  <--- Hydraulic Ram Quench & Stamping Animation
+-------------------------------+
             |
             v
[Finished Ingots / Glass Blocks]
```

### Molten Fluids & Thermochemistry

Six high-temperature molten fluids can be held in buckets, pumped through fluid pipes, carried via open refractory casting channels, or stored in tanks:

| Fluid | Source Items | Molten Color | Stamping Result |
| :--- | :--- | :--- | :--- |
| **Molten Iron** (`refinery:molten_iron`) | Iron Dust (`refinery:iron_dust`), Raw Iron | Radiant Orange-Gold (`#E76F51`) | 1 Iron Ingot |
| **Molten Gold** (`refinery:molten_gold`) | Gold Dust (`refinery:gold_dust`), Raw Gold | Blazing Solar Yellow (`#F4A261`) | 1 Gold Ingot |
| **Molten Copper** (`refinery:molten_copper`) | Copper Dust (`refinery:copper_dust`), Raw Copper | Glowing Ember Rust (`#E63946`) | 1 Copper Ingot |
| **Molten Tin** (`refinery:molten_tin`) | Tin Dust (`refinery:tin_dust`), Raw Tin | Molten Silver-Cyan (`#A8DADC`) | 1 Tin Ingot |
| **Molten Bronze** (`refinery:molten_bronze`) | Bronze Dust (`refinery:bronze_dust`) | Rich Golden Amber (`#D4A373`) | 1 Bronze Ingot |
| **Molten Glass** (`refinery:molten_glass`) | Sand, Red Sand, Quartz | Luminescent White-Gold (`#FFF3B0`) | 1 Glass Block |

*Note: All molten fluids emit light (level 15) and cause fire damage to entities contacting in-world fluid blocks.*

---

### Electric Arc Melter (3Ã—3Ã—3 Multiblock)

The **Electric Arc Melter** superheats mineral dusts and raw ores into liquid molten baths using high-energy graphite electrodes.

#### Multiblock Structure & Blueprint:
- **Dimensions**: 3 blocks wide $\times$ 3 blocks tall $\times$ 3 blocks deep (27 blocks total).
- **Core Requirements**:
  - 1x **Electric Arc Melter Controller** (`refinery:arc_melter_controller`) â€” front face, center-bottom.
  - 22x **Arc Melter Casing** (`refinery:melter_casing`) â€” heavy heat-dissipating refractory outer framing.
  - 2x **Arc Melter Observation Chamber** (`refinery:melter_chamber`) â€” tinted safety glass viewports.
  - 1x **Arc Melter Input Port** (`refinery:melter_input_port`) â€” top or side funnel hopper. Accepts dusts & raw ores from hoppers and item pipes.
  - 1x **Arc Melter Output Port** (`refinery:melter_output_port`) â€” bottom or side molten spout. Automatically pushes molten liquid to adjacent Casting Channels, fluid pipes, or tanks!
- **Hologram Support**: Right-click the Controller with a **Refinery Wrench** to project the full color-coded 3D holographic blueprint.

#### Operation & Animated Renderer:
- **Power Consumption**: 40 FE/t while melting.
- **Process Speed**: 40 ticks (2 seconds) per item.
- **Fluid Yield**: 1 Dust / Raw Ore / Sand $\rightarrow$ 1,000 mB of Molten Fluid.
- **Internal Tank**: 8,000 mB capacity with auto-drain through the Output Port.
- **3D Animation**: While active, dual carbon electrodes descend inside the central chamber and discharge energetic electric arcs with dynamic sparks and particle bursts.

---

### Open Casting Channel (Refractory Trough)

The **Casting Channel** (`refinery:casting_channel`) is an open refractory ceramic trough that carries molten liquids between machines or directly into casting molds.

- **Seamless 3D Geometry**: Dynamically connects north, south, east, and west, extending side walls while retaining open channels for gravity flow.
- **Down Spouts**: Automatically extends a pouring spout beneath when placed above another channel, pipe, or the input port of a Continuous Casting Press.
- **Dynamic Fluid Render**: The molten fluid visibly glows and flows along the channel bed, reflecting the true color of the liquid inside!
- **Fluid Transfer**: Handles 250 mB/t transfer rate with full NeoForge Fluid Transfer capability integration.

---

### Continuous Casting Press (3Ã—3Ã—3 Multiblock)

The **Continuous Casting Press** receives molten liquids and uses a heavy overhead hydraulic press and water-quenching die to stamp them into solidified ingots and glass blocks.

#### Multiblock Structure & Blueprint:
- **Dimensions**: 3 blocks wide $\times$ 3 blocks tall $\times$ 3 blocks deep (27 blocks total).
- **Core Requirements**:
  - 1x **Continuous Casting Press Controller** (`refinery:casting_press_controller`) â€” front face, center-bottom.
  - 20x **Casting Press Casing** (`refinery:casting_casing`) â€” heavy hydraulic frame with safety hazard striping.
  - 2x **Casting Mold Basin** (`refinery:casting_basin`) â€” ceramic mold bed at the base where liquid pools.
  - 2x **Casting Safety Chamber** (`refinery:casting_chamber`) â€” wire-reinforced safety viewing windows.
  - 1x **Casting Press Input Port** (`refinery:casting_input_port`) â€” top or side intake. Connects directly to Casting Channels or fluid pipes.
  - 1x **Casting Press Output Port** (`refinery:casting_output_port`) â€” bottom or side exit conveyor chute. Automatically ejects finished ingots and blocks into hoppers, chests, or transport pipes!

#### Operation & Animated Renderer:
- **Power Consumption**: 40 FE/t while stamping.
- **Process Speed**: 40 ticks (2 seconds) per stroke.
- **Input Requirement**: 1,000 mB of Molten Fluid per ingot/block.
- **Hydraulic Stamping Animation**: When a 1,000 mB charge fills the mold bed, the heavy overhead stamping die descends under high pressure, accompanied by hydraulic piston sounds, steam hisses, and quenching effects!
- **Auto-Ejection**: Ejects finished ingots and blocks through the Output Port immediately upon solidification.

---

### Melting & Stamping Recipe Codex

| Raw Input Item | Melter Output (1,000 mB) | Press Output (Solid) | Energy Cost |
| :--- | :--- | :--- | :--- |
| **Iron Dust** / Raw Iron | Molten Iron | **Iron Ingot** (1x) | 1,600 FE total |
| **Gold Dust** / Raw Gold | Molten Gold | **Gold Ingot** (1x) | 1,600 FE total |
| **Copper Dust** / Raw Copper | Molten Copper | **Copper Ingot** (1x) | 1,600 FE total |
| **Tin Dust** / Raw Tin | Molten Tin | **Tin Ingot** (1x) | 1,600 FE total |
| **Bronze Dust** | Molten Bronze | **Bronze Ingot** (1x) | 1,600 FE total |
| **Sand** / Red Sand / Quartz | Molten Glass | **Glass Block** (1x) | 1,600 FE total |

---

## 12. Biofuel Production & Industrial Fermentation

The **Industrial Fermenter** bridges the agricultural progression (Automated Planters & Harvesters) with thermodynamic power generation:

```
[Wheat / Potatoes / Sugar Cane]
              |
              v
+-------------------------------+
|  Industrial Fermenter (1-Blk) |  <--- 15 FE/t, Bubbling Mash Chamber
+-------------------------------+
              | (200 mB Ethanol + 20% Seeds)
              v
[Ethanol Biofuel Tank / Buckets]
              |
              v
[Boilers / Combustion Chambers / Engines] (4,000 Burn Ticks)
```

### Industrial Fermenter

- **Block**: [`FermenterBlock`](file:///c:/Users/swack/Desktop/refinery/src/main/java/com/refinery/block/FermenterBlock.java) (`refinery:fermenter`)
- **Dimensions**: Single-block standalone industrial machine.
- **Power Buffer**: 20,000 FE capacity, consuming 15 FE/t while fermenting.
- **Internal Tank**: 8,000 mB **Ethanol** storage with automatic fluid push to adjacent fluid pipes, tanks, or boilers (250 mB/t).
- **Manual Bucket Port**: Includes Bucket In & Bucket Out slots for easy manual filling of Ethanol Buckets.
- **Wrench Support**: Right-click with a Refinery Wrench to rotate horizontal facing; Sneak + Right-Click to instantly dismantle.

---

### Ethanol Biofuel & Crop Fermentation Codex

**Ethanol** (`refinery:ethanol`) is a renewable liquid biofuel. When held in buckets (`refinery:ethanol_bucket`), it serves as high-efficiency burner fuel in Boilers and Combustion Chambers (burning for 4,000 ticks / 20 items per bucket).

| Feedstock Crop | Ethanol Yield | Byproduct Yield | Ferment Time | Energy Cost |
| :--- | :--- | :--- | :--- | :--- |
| **Wheat** (`minecraft:wheat`) | **200 mB** (5 Wheat = 1 Bucket) | **Wheat Seeds (20% bonus)** | 60 ticks (3s) | 900 FE |
| **Potato** (`minecraft:potato`) | **150 mB** | Poisonous Potato (10%) | 60 ticks (3s) | 900 FE |
| **Sugar Cane** (`minecraft:sugar_cane`) | **100 mB** | Sugar (15%) | 60 ticks (3s) | 900 FE |
| **Beetroot** (`minecraft:beetroot`) | **100 mB** | Beetroot Seeds (20%) | 60 ticks (3s) | 900 FE |
| **Apple** (`minecraft:apple`) | **150 mB** | None | 60 ticks (3s) | 900 FE |
| **Sweet Berries** (`minecraft:sweet_berries`) | **100 mB** | None | 60 ticks (3s) | 900 FE |

---

## 13. Engineering Changelog & Version History

### Version 1.8.0 (Livestock Feeding Trough & Automated Ranching)
- **Livestock Feeding Trough (`refinery:feeding_trough`)**:
  - Added a 2-block-wide monolithic multiblock feed bunk designed for automated livestock breeding and animal farming.
  - **Monolithic 3D Model**: High-detail formed weathered galvanized sheet steel feed bunk featuring realistic zinc spangle crystal flakes, rolled top safety flanges, four heavy-gauge steel divider cross-struts, end bulkheads with cleanout slots, and corner skid feet.
  - **Dynamic In-World Hay Mound**: High-resolution golden straw/hay texture filling the trough that rises smoothly and continuously from Y = 1.0px to Y = 6.4px based on inventory food stock.
  - **Automated Livestock Breeding**:
    - Scans a 4.5-block radius (10×5×10 bounding volume) around the 2-block trough every second.
    - Feeds up to 2 eligible adult animals per second that are off breeding cooldown (`canFallInLove()`), triggering love hearts (`broadcastEntityEvent(18)`) and realistic eating sound effects (`SoundEvents.GENERIC_EAT`).
    - Excludes baby animals and animals currently on breeding cooldown or already in love.
  - **Universal Animal & Diet Compatibility**: Dynamically checks `animal.isFood(stack)` to accept any valid food item for vanilla and modded livestock.
  - **Logistics & Multiblock Integration**: Unified double-block inventory accessible from any face by Logistics Automation item pipes, conveyors, hoppers, and droppers. Interactive right-click quick-deposit and custom GUI with live herd radar counter and golden hay fill gauge.
  - **Crafting Recipe**: 4 Steel Plates + 2 Iron Bars + 1 Hay Bale produce 1 Feeding Trough.

### Version 1.7.2 (Conveyor Speed Calibration, Scroll Sync & Item-Only Mode)
- **Calibrated 1/2 Item Transit Speed**:
  - Reduced item velocity from 0.10 to 0.05 blocks/tick (~1.0 block per second) for steady, authentic factory pacing.
- **Item-Only Conveyance**:
  - The conveyor now exclusively conveys items (`ItemEntity`). Players, animals, and monsters are no longer pushed or displaced when standing or walking over the belt.
- **Synchronized Scroll Direction**:
  - Fixed top belt UV texture scrolling to advance forward in the exact direction items travel.
  - Added reverse scrolling on the bottom return belt and calibrated rotating end roller velocity.

### Version 1.7.0 (Industrial Belt Conveyor System)
- **Industrial Belt Conveyor (`refinery:conveyor_belt`)**:
  - Added modular low-profile belt conveyor system for high-throughput factory automation, item transport, and entity conveyance.
  - **Monolithic 3D Model**: High-detail extruded anodized aluminum bed frame with longitudinal side T-slot channels, recessed deck plate, heavy cast mounting foot pads, and sleek guide lips.
  - **Matte Black Industrial Belt**: High-density vulcanized rubber canvas belt with micro-grip friction ribs.
  - **Dynamic In-World Animations**:
    - Seamless UV texture scrolling animation smoothly moves the top belt forward and bottom return belt backward.
    - Precision brushed stainless steel cylindrical end rollers physically rotate around their axles at exact belt surface velocity.
    - Real-time physical item centering and smooth propulsion along the conveyor line.
    - Player and mob conveyance with sneak/crouch stability.
  - **Intelligent Seamless Snapping**:
    - Placing conveyors in a row automatically snaps them together into a continuous, seamless belt assembly.
    - Intermediate end terminals open up, seamlessly joining side rails, T-slots, and belt surfaces with zero gaps or visible seams.
    - Head and tail rollers and machined bearing plates automatically render only at the exposed line terminals.
  - **Automated Machine & Container Integration**:
    - Items reaching the conveyor head automatically inject into adjacent inventories, chests, hoppers, furnaces, and machines via the NeoForge item transfer system.
    - Hoppers, droppers, and pipes can directly deposit items onto the belt surface.
  - **Redstone & Wrench Automation**:
    - Redstone signals pause conveyor operation, halting belt scrolling and item transit.
    - Right-click or Sneak-Right-Click with the **Refinery Wrench** rotates the conveyor orientation with instant neighbor re-connection.
  - **Crafting Recipe**: 3 Petroleum Tar + 1 Redstone Dust + 5 Iron Ingots produce 4 Industrial Conveyor Belts.

### Version 1.6.5 (Dedicated Coal/Diesel/RF Fluid Pumps & Drain Fixes)
- **Specialized Fluid Pump Split**:
  - Replaced the single multi-mode pump with 3 distinct machine blocks: **Coal Fluid Pump** (Dark Charcoal frame `#374151`), **Diesel Fluid Pump** (Caterpillar Orange frame `#FA983A`), and **Electric Fluid Pump** (Electric Blue frame `#2563EB`).
  - Added dedicated crafting recipes, loot tables, and 32x32 item textures for all three variants.
- **Dedicated GUIs**:
  - Each machine now has its own tailored interface displaying only relevant power slots and readouts (no unnecessary RF bars on coal pumps or coal slots on electric pumps).
  - Streamlined GUI layout by removing intake coordinates and unlink button.
- **In-World Shift-Right-Click Toggle**:
  - Shift-right-click any fluid pump in the world with an empty hand or wrench to instantly toggle between `[ INFINITE WATER ]` and `[ DRAIN POOL ]` with sound and chat feedback.
- **Contiguous Drain Fix for Water**:
  - Fixed fluid traversal in Drain mode to navigate across water (`is(FluidTags.WATER)`), ensuring full pools are completely drained dry without stalling.

### Version 1.6.4 (Fluid Pump Coal Fuel Tuning, Real Buttons & 26.3 Item Icons)
- **Coal Mode Burn Rate**:
  - Calibrated solid fuel combustion to burn exactly 1 Coal (or burnable item) per 3 seconds (60 ticks) while actively pumping, pausing consumption when idle, paused by redstone, or when fluid is dry.
- **Genuine Minecraft GUI Buttons**:
  - Transformed the Power Mode and Drain Mode toggles into authentic 3D beveled Minecraft buttons with instant client prediction and click sound feedback. Power mode switches smoothly between `[ RF ]`, `[ COAL ]`, and `[ DIESEL ]` anytime without requiring connected fuels.
- **Digital Power Readout (No Progress Bar)**:
  - Replaced the left progress bar with a clean digital readout panel displaying current power mode, stored FE, burn seconds, or diesel fuel volume directly without needing to mouse over.
- **Calibrated Fluid Tank Reservoir & Flow Animation**:
  - Upgraded the fluid output gauge into a calibrated glass reservoir with graduations (25%, 50%, 75%), volume readouts, and active fluid flow animations when pumping directly into pipes.
- **26.3 Item Model Definitions**:
  - Added `assets/refinery/items/fluid_pump.json` and `assets/refinery/items/fluid_discharge.json` required by modern Minecraft 26.3, resolving broken inventory icons for both blocks.

### Version 1.6.3 (Fluid Pump Stability, Transaction Safety & UI Polish)
- **NeoForge Transfer API Transaction Safety**:
  - Resolved `NullPointerException` crash during fluid push operations to Logistics Automation fluid pipes by wrapping fluid transfers in `Transaction.openRoot()` and committing upon positive insertion across `FluidPumpBlockEntity`, `ArcMelterControllerBlockEntity`, `CastingChannelBlockEntity`, and `FermenterBlockEntity`.
- **Fluid Pump Power & Setting Controls**:
  - Fixed power mode switching in the Fluid Pump GUI. Button hitboxes expanded with immediate client-side prediction and bidirectional server block entity synchronization.
- **UI & Icon Polish**:
  - Replaced unsupported Unicode characters with universal bracketed status text in the Fluid Pump GUI (`[PUMPING]`, `[IDLE]`, `[NO POWER]`, `[NO FUEL]`, `[NO DIESEL]`, `[TANK FULL]`, etc.).
  - Added dedicated high-resolution 2D item textures and models for the Industrial Fluid Pump and Industrial Fluid Discharge.

### Version 1.6.2 (Industrial Fluid Discharge & Void/Fill Outlet)
- **Industrial Fluid Discharge (`refinery:fluid_discharge`)**:
  - Added an industrial fire-engine red foot valve and discharge strainer outlet block.
  - **Monolithic 3D Model**: Cast red body, mounting collar with ribs, dual-tier bolt flanges with side ear lugs, graphite gasket, chrome hex bolts/studs, slotted dome cage, and perforated crown.
  - **Dual Toggled Operational Modes**:
    - **`VOID / DUMP`**: Fluid pumped into the block from Logistics Automation or standard pipes is immediately dumped and voided.
    - **`FILL`**: Discharges liquid into the surrounding area at the **same Y level**, contiguous-only BFS (no jumping walls/barriers into adjacent pits).
  - **Simple Mode Toggling**: Right-click with empty hand or Refinery Wrench toggles mode with sound and action bar/system feedback; sneak-click inspects status.
  - **Redstone Automation**: Redstone high signal pauses discharging.

### Version 1.6.0 (Industrial Fluid Pump & 3D Suction Hose)
- **Industrial Fluid Pump (`refinery:fluid_pump`)**:
  - Added a heavy-duty portable engine-driven fluid pump modeled after industrial gas/diesel water pumps (Superkiger WP40X).
  - High-fidelity **Monolithic 3D Model**: tubular black steel roll-cage with rubber damping pads, safety-yellow recoil pull-starter shroud, white enamel fuel tank with chrome cap, cast aluminum centrifugal volute pump casing, top vertical discharge pipe/flange, and front suction intake flange.
  - **In-World 3D Suction Hose**: Renders a dynamic, 3D spiral-ribbed flexible suction hose extending from the pump's suction flange to the in-world intake point, complete with metal camlock quick-connect couplings, brass perforated intake strainer basket, and an active pulsing suction wave animation when pumping.
  - **Refinery Wrench Suction Point Linking**: Sneak-right-clicking the pump with a Refinery Wrench selects the pump; right-clicking or sneak-clicking any block or fluid in the world (up to 64 blocks away) connects the pump intake and deploys the 3D suction hose. Double sneak-clicking the pump dismantles it.
  - **Contiguous Fluid Source Flood-Fill (BFS)**: Intelligently discovers and pumps all contiguous, connected fluid source blocks one at a time from the intake point outward. Never draws from unconnected pockets.
  - **Infinite Water Mode Toggle (`INF` / `DRY`)**: Toggleable in the GUI. When active on water sources, continuously pumps water without destroying source blocks for infinite water extraction. When inactive (or when pumping non-water fluids like Lava or Crude Oil), drains the fluid pool completely.
  - **Multi-Fuel Engine Architecture**:
    - **RF / FE Mode**: Consumes low electrical energy (400 FE per 1,000 mB bucket / 20 FE/t) into its 20,000 FE buffer.
    - **Solid Fuel (Burnables) Mode**: Consumes coal, charcoal, wood, blaze rods, or petroleum tar/bitumen in its fuel slot.
    - **Diesel Mode**: Uses 10 mB of Diesel, Gasoline, or Biofuel per bucket pumped from its internal 4,000 mB fuel tank; refueled by placing fuel buckets into the GUI or via fluid pipes.
  - **Redstone Control Mode**: Configurable via GUI between `Ignored`, `Require High Signal`, or `Require Low Signal`.
  - **Logistics Craft Pipe Auto-Output**: Automatically pumps fluid out of its 10,000 mB tank directly into adjacent Logistics Craft fluid pipes and tanks, prioritizing the top discharge flange.
  - **Interactive GUI**: Displays live machine status, digital intake coordinate readout, fluid volume gauge with dynamic fluid colors, RF energy gauge, fuel slot with bucket output, burn time / diesel gauges, and mode toggle buttons.

---

### Version 1.5.15 (Industrial Grinder Open-Top Blade Hopper, Dynamic Output Conveyor & Floor Occlusion Fix)
- **Open-Top Hopper Bowl with 3D Counter-Rotating Spiked Grinder Blades**:
  - Replaced the static rock mound inside the Industrial Grinder's top hopper with a hollow open-top steel bowl containing twin counter-rotating 3D spiked shredder rotors that spin when grinding and render the actual loaded input items between the blades.
  - Dropped items falling into the top hopper bowl (e.g., from standalone conveyors) are automatically absorbed into the grinder's input inventory.
- **Clean Output Conveyor & Removal of Left Side Belt**:
  - Removed the left-side feed belt and all pre-generated static rock/aggregate geometry.
  - Kept the right-side inclined discharge conveyor with a clean empty belt that dynamically renders the actual crushed output material climbing out of the machine.
- **Floor Occlusion Fix Under Assembled Multiblocks**:
  - Fixed transparent floor X-raying beneath assembled multiblocks by returning `Shapes.empty()` from `getOcclusionShape(BlockState)` when `ASSEMBLED=true`.

---

### Version 1.5.14 (Grinder & 5x5 Boiler Overhaul, Diesel Boilers, Lighter Diesel)
- **Multiblock Industrial Grinder Monolithic 3D Overhaul & Animated Conveyors**:
  - Overhauled the 3×3×3 Industrial Grinder (`refinery:industrial_grinder_controller`) into a monolithic 3D rock crushing plant based on real-world heavy industrial machinery.
  - Structural 4-post steel frame with heavy vibration dampening foot pads, cross-braces, and reinforced mounting flanges.
  - Central eccentric crushing box with side flywheels, counterweights, and belt-drive housings that animate dynamically during active operation.
  - Large top flared rock hopper funnel loaded with stone and ore chunks.
  - Horizontal feed conveyor bringing raw material into the crusher hopper, complete with animated raw items traveling along the belt.
  - Inclined discharge conveyor carrying crushed gravel and mineral dusts out from under the crusher box, complete with animated output items climbing the belt.
  - Full member block occlusion via `ASSEMBLED = true -> invisible.json` and interaction/wrench forwarding on `GrinderCasingBlock`, `GrinderChamberBlock`, and `GrinderPortBlock`.
- **Custom Industrial Grinder GUI**:
  - Overhauled `IndustrialGrinderScreen` and custom textured `textures/gui/industrial_grinder.png` matching the dark gunmetal steel industrial aesthetic of the Metal Press.
  - 2-slot raw input feed tray, 4-slot crushed output collection bay, central animated crushing chamber indicator, and real-time status badge (`ONLINE`, `CRUSHING`, `IDLE`, `UNFORMED`).
- **5x5 Large Steam Boiler Monolithic 3D Overhaul**:
  - Overhauled the 5×5×5 Large Steam Boiler (`refinery:large_boiler_controller`) into a monolithic 3D twin-furnace Scotch marine industrial packaged boiler (`LargeBoilerModel`).
  - Massive cylindrical boiler pressure vessel with dark steel structural skid runners and saddle cradles.
  - Dual circular front boiler furnace doors with twin glowing burner viewports (dynamic `15728880` full emissive flame glow when active).
  - Twin vertical exhaust smokestacks with top flanges and reinforcement collars.
  - Overhead heavy steam manifold piping with dual valve handwheels and central dial pressure gauge.
  - Right-side elevated maintenance catwalk platform with safety handrail and access ladder, side electrical control console, and twin water sight level columns.
  - Custom high-capacity industrial GUI (`LargeBoilerScreen` & `large_boiler.png`).
- **Small and Large Diesel-Fired Steam Boilers**:
  - Introduced the **Small Diesel Boiler** (3×3×3, `refinery:small_diesel_boiler_controller`) and **Large Diesel Boiler** (5×5×5, `refinery:large_diesel_boiler_controller`) that consume liquid **Diesel** instead of solid burnables to produce high-pressure steam.
  - Custom 3D models with modern liquid fuel injector burner hubs, fuel rails, and high-efficiency combustion chambers.
  - Custom 3-tank industrial GUIs (`SmallDieselBoilerScreen` & `LargeDieselBoilerScreen`) featuring recessed gauges for Diesel, Water, and Steam, fluid bucket fill/drain interaction slots, redstone indicators, and combustion flame viewports.
  - Fluid and item capabilities integrated with input/output ports and automation pipes.
  - Added shaped crafting recipes and loot tables for both diesel boiler controllers.
- **Lighter Diesel Fluid Texture Palette**:
  - Re-paletted diesel fluid textures (`diesel_still.png`, `diesel_flow.png`, `diesel_bucket.png`, `port_diesel.png`) to a refined golden straw tone (`#fdee87` / `#f5cf47`) for enhanced visual distinction from crude oil and gasoline.

---

### Version 1.5.13 (Small Steam Boiler Monolithic Scotch Marine Overhaul)
- **Monolithic 3D Small Steam Boiler Model Overhaul**:
  - Transformed the assembled 3×3×3 Small Steam Boiler into a monolithic 3D industrial packaged Scotch marine fire-tube boiler model.
  - Horizontal cylindrical drum with steel cladding plates, circular front door with hinges and central burner hub, emissive burning sight-glass viewport, arched ash door, overhead steam manifold header with valves, exhaust stack, ICI Caldaie style control cabinet, and feed-water booster pumps.
- **Custom Industrial Small Boiler GUI Overhaul & Realignment**:
  - Dark gunmetal steel panel with recessed water & steam buffer gauges, 2x2 fuel buffer, redstone lamp, combustion flame viewport, meltdown hazard bar, and real-time status badge.

---

### Version 1.5.11 (Planter & Harvester GUI Power Switch & Work Radius Control)
- **GUI ON/OFF Power Switch**:
  - Added an interactive power switch directly in the Automated Planter and Automated Harvester GUIs, with live LED indicators and status readouts.
  - Toggling OFF stops machine scanning and cuts idle energy drain.
- **Adjustable Work Radius (Up to 9 Blocks / 100m+ Area)**:
  - Added `-` and `+` radius control buttons to both machines, allowing operators to set work radius from 1 block to 9 blocks away in all directions (covering 3x3 up to 19x19 / 361 tiles total).
  - Harvester now scans in all directions around the block, matching the Planter's circular/square perimeter coverage while maintaining backward-compatible auto-ejection behind or below.
- **Persistent Settings & Network Sync**:
  - Machine enabled status and configured radius are stored in block entity NBT and synchronized via menu container data.

---

### Version 1.5.9 (3-Block-Wide Industrial Metal Press & Conveyor Bed)
- **3-Block-Wide (3Ã—2Ã—2) Industrial Metal Press with Conveyor Bed & AIDA NC1-1500 Stamping Head**:
  - Expanded the Metal Press multiblock structure to 3 blocks wide (`3Ã—2Ã—2`, with flexible placement supporting centered or side-aligned controllers as well as `3Ã—2Ã—1` depth).
  - Redesigned the assembled 3D model (`AidaPressModel`) and 256Ã—256 texture atlas (`aida_press.png`) combining an Immersive Engineering-style conveyor bed and 4-post center gantry with the AIDA NC1-1500 overhead stamping head.
  - Clean member block model suppression (`invisible.json`) and forwarded energy/item capabilities to the controller.

---

### Version 1.5.8 (Industrial Metal Press Monolithic AIDA NC1-1500 C-Frame Model & Texture)
- **Monolithic AIDA NC1-1500 C-Frame Model & Texture**:
  - High-definition seamless 3D model with classic off-white machine enamel, AIDA diamond branding, analog dial gauge, operator control pendant with mushroom E-stop, and animated hydraulic ram slide.

---

### Version 1.5.7 (Logistics Pipe Compatibility & Machine Detection Fix)
- **Accurate Machine Inventory Detection for Pipe Gates**:
  - Fixed an issue where pipe gates mistakenly evaluated connected transport pipes as target inventories instead of the attached machine. Connected pipes are now strictly excluded from inventory resolution, allowing gate conditions (`Has Items`, `Inventory Empty`, `Inventory Space`, `Inventory Full`) to accurately detect machine inventory contents.
- **Autarchic Pulsar Item Extraction on Logistics Extractor Pipes**:
  - Fixed a `MethodHandle` polymorphic signature exception in `LogisticsPipeCompat` that prevented pulsar gates on extractor pipes from injecting items into the pipe network.
  - Implemented reliable accessible reflection with dual fallback (`forceAddItem` with `TravelingItem` and `addItem` with `(Direction, ItemStack)`), allowing pulsar gates to reliably extract items from machines into logistics networks.
- **Harvester Native Pipe Auto-Export**:
  - The Automated Harvester now natively detects connected Logistics Automation item pipes positioned behind or below the block, auto-injecting harvested items directly into the pipe network.
- **WorldlyContainer Capability Wrappers**:
  - Migrated `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity` capability registrations to `WorldlyContainerWrapper(be, side)` with explicit `canPlaceItem` and `canTakeItem` handlers, ensuring sided insertion and extraction work with third-party logistics systems, hoppers, and pipes.
- **Expanded Machine Status Gate Triggers**:
  - `MACHINE_ACTIVE` and `MACHINE_IDLE` gate triggers now dynamically inspect block state properties (`working`, `active`, `lit`, `running`, `powered`) as well as direct machine working getters for Planters, Harvesters, Boilers, and Controllers.

---
### Version 1.5.6 (Automated Tree Farming & Sapling Planting Support)
- **Automated Planter Sapling & Tree Support**:
  - The **Automated Planter** (`refinery:auto_planter`) now accepts and plants all tree saplings (`#minecraft:saplings`, `SaplingBlock`, and modded saplings) alongside agricultural crops.
  - Saplings are planted directly on natural soil blocks (`#minecraft:dirt` â€” Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling them into farmland.
- **Automated Harvester Tree & Leaf Canopy Felling**:
  - The **Automated Harvester** (`refinery:auto_harvester`) now detects tree trunks and logs (`#minecraft:logs`) and leaf blocks (`#minecraft:leaves`).
  - When a tree is scanned, the harvester performs an intelligent 3D recursive flood-fill harvest: felling the entire trunk, diagonal branches, and the surrounding leaf canopy up to 32 blocks high and 8 blocks radius.
  - Collects all tree drops into the harvester's buffer: wood logs, saplings, apples, and sticks, auto-exporting them to chests or pipes connected behind or below.
  - Standalone leaf clusters are also cleared, creating a continuous, 100% automated tree farming loop alongside the Planter.

---

### Version 1.5.5 (Automated Agricultural Crafting Recipes)
- **Automated Planter & Harvester Recipes**:
  - Added shaped crafting recipes for the **Automated Planter** (`refinery:auto_planter`) and **Automated Harvester** (`refinery:auto_harvester`).
  - Planter: 4 Iron Ingots, 2 Copper Ingots, 1 Iron Hoe, 1 Piston, 1 Redstone Dust.
  - Harvester: 4 Iron Ingots, 2 Copper Ingots, 1 Shears, 1 Hopper, 1 Redstone Dust.

---

### Version 1.5.4 (Autarchic Pulsar Item Routing & Gate Redstone Emission Fix)
- **Autarchic Pulsar Extraction & Safe Injection**:
  - Replaced unsafe standard external injection calls with internal `forceAddItem` handling in Logistics Pipes compat. External injection was previously failing pipe acceptance rules, causing Logistics to drop extracted items on the ground on every pulse.
  - Implemented smart bidirectional endpoint resolution (`PipeAndTarget`): automatically identifies which adjacent block is the pipe and which is the inventory container, properly supporting gates mounted either onto pipes facing inventories or onto chests/machines facing pipes.
  - Extracted items are safely injected into the pipe's internal traveling queue, moving towards connected endpoints without dropping on the ground.
- **Pipe Gate Redstone Signal Emission**:
  - Fixed issue where pipe gates configured to emit redstone signals (`REDSTONE_SIGNAL` and `REDSTONE_PULSE`) did not power adjacent redstone dust, repeaters, comparators, or machines.
  - Because pipe gates are collar entities sharing the pipe's block space rather than standalone full blocks, introduced `BlockStateBaseMixin` injecting into `BlockBehaviour$BlockStateBase.getSignal()`, `getDirectSignal()`, and `isSignalSource()`.
  - Maintains a thread-safe, dimension-keyed tracking registry of active powered gate positions (`POWERED_GATE_POSITIONS`). Blocks hosting powered gates output redstone signal level 15 in all directions.
  - Added comprehensive neighbor updates (`updateNeighborsAt`) whenever gate redstone emission toggles on/off or when the gate entity is removed, guaranteeing instant response from adjacent redstone circuits.

---

### Version 1.5.3 (Persistent Chunk Burnt State & Multi-Tier Retrogen Defense)
- **Direct-to-Chunk MCA Burnt State**:
  - Implemented persistent NeoForge Data Attachment (`refinery:oil_chunk_burnt`) serialized directly into chunk `.mca` files. Chunks are permanently burnt upon their first load and evaluation, preventing any repeated rolls when players leave and return.
  - Dimension Guard: Strictly confines oil retrogen evaluation to `ServerLevel.OVERWORLD`.
  - Well Perimeter Burning: Spawning a well now immediately burns all 8 adjacent neighbor chunks.

---

### Version 1.5.2 (Ocean Generation Balancing & Chunk Cascade Fix)
- **Cascade Chunk-Loading & Neighbor Spawning Fix**:
  - Reserved well positions in `wellChunks` saved data **prior** to block placement, eliminating duplicate and touching oil wells caused by boundary chunk loads.
- **Config Max Spacing Range**:
  - Expanded `minChunkDistanceBetweenWells` maximum from `64` to `512` chunks, with default set to `40` chunks (640 blocks).
- **Ocean Biome Over-Generation Balancing**:
  - Introduced `oceanSpawnChanceMultiplier` (default `0.20`), reducing ocean generation density by 80% to balance vast contiguous oceans.
  - Enhanced sea-level detection (Y=62/63) so ocean wells anchor correctly to the seabed.

---

### Version 1.5.1 (Visual Overhaul & Worldgen Balancing)
- **Crude Oil Spacing & Generation Balancing**:
  - Decreased `oilPitSpawnChance` to `0.005` (1 in 200 chunks) and increased `minChunkDistanceBetweenWells` to `20` chunks (320 blocks minimum well spacing) to prevent clustered oil deposits.
- **Multi-Smelter Block Icons Fix**:
  - Added missing 26.3 item definition JSONs for all 6 Multi-Smelter components, fixing invisible/missing inventory item rendering.
- **Clearish White Ethanol**:
  - Converted still fluid, flowing fluid, and bucket textures to clear frosted white, updated map color to `MapColor.SNOW`, and updated client underwater fog.
- **Complete Visual Redo of Pipe Attachments**:
  - Redesigned all pipe wires (red, blue, green, yellow) into wire spools with metallic center spindles, coiled insulation, and stripped wire leads.
  - Redesigned all 8 pipe gates (Basic, Iron, Gold, Diamond â€” standard & autarchic) into 14x14 beveled industrial attachment plates with tier-specific bezels, corner mounting bolts, and glowing logic cores.
  - Redesigned Autarchic Chipset into an authentic ceramic dual-in-line IC chip with gold pins and glowing energy core.

---

### Version 1.5.0 (Industrial Multi-Smelter Array)
- **Industrial Multi-Smelter (5Ã—4Ã—3 Multiblock)**:
  - 8-in-1 parallel furnace array multiblock acting as an industrial super-smelter.
  - 6-block hollow air combustion chamber with front viewing windows and live 3D BER rendering.
  - Dual-power: runs on solid fuel (coal, wood, tar, bitumen, ethanol) OR electric RF (50,000 FE buffer, 30 FE/t).
  - Dynamic 3D in-world animations: glowing embers/fire grate or electric induction coils, plus 8 floating rotating cooking items.
  - 100% early-game accessible: crafts using Iron, Copper, Bricks, Smooth Stone, Furnaces, and Glass. Absolutely NO Steel required.
  - Full automation support via dedicated Input, Output, and Fuel/Energy ports, plus 3D wrench holographic blueprint projection.

---

### Version 1.4.1 (Progression Balancing & Steel Plate Exclusivity)
- **Steel Plate Exclusivity**:
  - Removed crafting table recipe for Heavy Steel Plate (`refinery:steel_plate`). Steel plates are now **exclusively** produced via the Industrial Metal Press.
  - Replaced steel plate ingredients in the **Metal Press Controller** and **Press Anvil Bed** crafting recipes with Iron Ingots and Iron Blocks to eliminate circular dependencies.
  - Verified linear metallurgy progression: Blast Smelter $\rightarrow$ Steel Ingots $\rightarrow$ Metal Press $\rightarrow$ Steel Plates $\rightarrow$ Boilers & Advanced Machinery.
  - Enabled automated hopper/pipe insertion of all valid ingots (Steel, Iron, Copper, Gold, Tin, Bronze) into the Metal Press.

---

### Version 1.4.0 (Biofuel & Oil Sand Cracking)
- **Refinery Oil Sand Cracking**:
  - Integrated high-pressure feedstock slot in the Petroleum Refinery interface.
  - Universal compatibility with `refinery:oil_sand` and external mod oil sands (e.g. *Logistics* mod).
  - Yields: **+200 mB Crude Oil**, **100% Sand byproduct**, and **40% Petroleum Tar**.
  - Direct automation via Crude Oil Port intake and Byproduct Port output.
- **Industrial Fermenter**:
  - Standalone single-block agricultural fermentation machine (`refinery:fermenter`).
  - Converts Wheat into **200 mB Ethanol** + **20% bonus Wheat Seeds**.
  - Also processes Potatoes, Sugar Cane, Beetroots, Apples, and Sweet Berries.
  - 8,000 mB internal storage with auto-push to fluid pipes and manual bucket filling.
- **Ethanol Biofuel**:
  - Registered `refinery:ethanol` fluid and bucket items.
  - 4,000 tick burn duration in Boilers and Combustion Chambers.

---

### Version 1.3.0 (Continuous Molten Metallurgy)
- **Molten Foundry Suite**:
  - Registered 6 molten fluids (Molten Iron, Gold, Copper, Tin, Bronze, Glass) with buckets, light emission, and in-world interaction.
- **Electric Arc Melter (3Ã—3Ã—3 Multiblock)**:
  - High-temperature induction melting with 3D animated descending carbon electrodes and dynamic electric arc discharges.
- **Open Refractory Casting Channel**:
  - Seamless 3D trough geometry with directional side extensions, down-spouts, and glowing flowing molten liquid rendering.
- **Continuous Casting Press (3Ã—3Ã—3 Multiblock)**:
  - Overhead hydraulic stamping ram with water-quenching steam clouds, mechanical piston sound effects, and ingot/block stamping.

---

### Version 1.2.0 (Grinder Overhaul & Logic Gates)
- **Industrial Grinder 3Ã—3Ã—3 Multiblock**:
  - Expanded into a full 3Ã—3Ã—3 multiblock with 3D physical counter-rotating spiked crushing drums.
- **In-Space Logic Gates**:
  - BuildCraft-style logic gates now snap directly to pipe faces, occupying the same block space.
  - Pipe wires rendered seamlessly along pipe paths.
- **Multi-Mod Material Integration**:
  - Added Tin, Bronze, and Coal dusts, ingots, and plates.

---

### Version 1.1.0 (Oil Geysers & Retrogen)
- **Crude Oil World Generation**:
  - Surface oil pits and subterranean pressurized oil geysers.
  - Retrogen persistence system preventing mined deposits from regenerating.
  - Administrative `/refinery` and `/admin` commands.

---

### Version 1.0.0 (Foundational Release)
- **Petroleum Distillation Refinery (3Ã—3Ã—4 Multiblock)**
- **Industrial Blast Smelter (5Ã—5Ã—5 Multiblock)** & **Industrial Metal Press (2Ã—2Ã—2 Multiblock)**
- **Thermodynamic Steam Power Suite**: Small Boiler (3Ã—3Ã—3), Large Boiler (5Ã—5Ã—5), Steam Turbine (3Ã—3Ã—3) with Rankine cycle.
- **Agricultural Automation**: Automated Planter and Automated Harvester.
- **Refinery Wrench**: 3D holographic blueprints and instant block dismantle.




