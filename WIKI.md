# Industrial Refinery — Complete Mod Wiki & Engineering Guide
*A comprehensive guide to heavy industry, fractional distillation, steam power, metallurgy, and automation for Minecraft 26.3 NeoForge.*

---

## Table of Contents
1. [Core Progression & Materials](#1-core-progression--materials)
2. [Essential Tool: The Refinery Wrench](#2-essential-tool-the-refinery-wrench)
3. [Standalone Agricultural Automation](#3-standalone-agricultural-automation)
   - [Automated Planter](#automated-planter)
   - [Automated Harvester](#automated-harvester)
4. [Heavy Metallurgy](#4-heavy-metallurgy)
   - [Industrial Blast Smelter (5×5×5 Multiblock)](#industrial-blast-smelter-555)
   - [Industrial Metal Press (2×2×2 Multiblock)](#industrial-metal-press-222)
   - [Industrial Multi-Smelter (5×4×3 Heavy Smelter Array)](#industrial-multi-smelter-543)
5. [Thermodynamic Steam Power & Closed-Loop Energy](#5-thermodynamic-steam-power--closed-loop-energy)
   - [Small Steam Boiler (3×3×3 Multiblock)](#small-steam-boiler-333)
   - [Large Heavy Steam Boiler (5×5×5 Multiblock)](#large-heavy-steam-boiler-555)
   - [Steam Turbine Generator (3×3×3 Multiblock)](#steam-turbine-generator-333)
   - [The Closed-Loop Rankine Cycle](#the-closed-loop-rankine-cycle)
6. [Petroleum Refining & Chemical Byproducts](#6-petroleum-refining--chemical-byproducts)
   - [Petroleum Refinery (3×3×4 Multiblock)](#petroleum-refinery-334)
   - [Oil Sand Cracking & Extraction](#oil-sand-cracking--extraction)
   - [Fuels & Byproducts](#fuels--byproducts)
   - [Industrial Grinder & Pulverizer (3×3×3 Multiblock)](#industrial-grinder--pulverizer-3x3x3)
7. [Comprehensive Recipe Codex](#7-comprehensive-recipe-codex)
8. [Automation, Pipes, & Interfacing](#8-automation-pipes--interfacing)
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
   - [Electric Arc Melter (3×3×3 Multiblock)](#electric-arc-melter-333-multiblock)
   - [Open Casting Channel (Refractory Trough)](#open-casting-channel-refractory-trough)
   - [Continuous Casting Press (3×3×3 Multiblock)](#continuous-casting-press-333-multiblock)
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
                                            (2x2x2 Metal Press)
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

---

## 3. Standalone Agricultural Automation

### Automated Planter
The **Automated Planter** (`refinery:auto_planter`) manages soil preparation and crop planting over a large farm area.

- **Crafting Recipe**: 4x Iron Ingot, 2x Copper Ingot, 1x Iron Hoe (top-center), 1x Piston (center), 1x Redstone Dust (bottom-center).
- **Scan Coverage**: 5 blocks in all horizontal directions (an **11×11 square**, 121 tiles total) up to **2 blocks above** the planter.
- **Tilling & Planting**:
  - **Crops**: Automatically detects dirt or grass blocks, tills them into farmland, and plants any valid seeds found in its 9-slot inventory.
  - **Trees & Saplings**: Accepts all tree saplings (Oak, Birch, Spruce, Dark Oak, Jungle, Acacia, Cherry, Pale Oak, Mangrove Propagules, and modded saplings). Plants them directly onto natural soil (`#minecraft:dirt` — Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling into farmland.
- **Energy**: Consumes 1-2 FE/t standby and 10 FE per planting action.
- **Automation**: Accepts seeds and saplings from hoppers, droppers, or item pipes through any face.

### Automated Harvester
The **Automated Harvester** (`refinery:auto_harvester`) scans mature crops directly in front of it.

- **Crafting Recipe**: 4x Iron Ingot, 2x Copper Ingot, 1x Shears (top-center), 1x Hopper (center), 1x Redstone Dust (bottom-center).
- **Scan Coverage**: An **11×11 field** directly in front of the machine (5 blocks to the left, 5 blocks to the right, and 11 blocks forward). Place this unit above ground centered with your farm.
- **Harvest & Replant**:
  - **Crops**: Detects fully mature crops (Wheat, Carrots, Potatoes, Beetroots, Melon/Pumpkin, Sugar Cane, Cocoa, Sweet Berries, etc.), harvests the drops, and immediately replants the seed.
  - **Trees & Foliage**: Detects tree trunks (`#minecraft:logs`) and leaves (`#minecraft:leaves`). Intelligently fells the entire tree via 3D recursive flood-fill (trunk, branches, and canopy up to 32 blocks high and 8 blocks radius), gathering wood logs, saplings, apples, and sticks while clearing the ground for immediate replanting.
- **Buffer & Ejection**: Internal 9-slot buffer. **Automatically pushes harvested produce, wood logs, and saplings into chests or pipes** placed directly **behind** or **below** the harvester!
- **Energy**: Consumes 2 FE/t standby and 20 FE per harvest.

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

---

## 4. Heavy Metallurgy

### Industrial Blast Smelter (5×5×5)
A massive foundry furnace that transforms iron sources and carbon fuels into high-grade Steel Ingots.

- **Dimensions**: 5 blocks wide × 5 blocks tall × 5 blocks deep (125 blocks total).
- **Viewing Window**: The front wall features a 3×3 transparent **Foundry Heat Glass** window where you can watch raw materials drop into the molten chamber and cook into glowing steel!
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
- **Layers 1–3 (Smelting Chamber & Viewing Window)**:
  - Front face x in [-1, 1]: **Foundry Heat Glass** (3×3 window)
  - Center 3×3 interior (x in [-1, 1], z in [1, 3]): **Air (Hollow chamber)**
  - Outer side and back walls: **Foundry Refractory Casing**
- **Layer 4 (Roof)**:
  - 25 **Foundry Refractory Casing** covering the entire top.

---

### Industrial Metal Press (2×2×2)
A universal hydraulic mechanical press for stamping various metal ingots into structural plates.

- **Dimensions**: 2 wide × 2 tall × 2 deep (8 blocks total).
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

#### Layer-by-Layer Assembly
```
Top Layer (y=1):
  [Press Casing]    [Press Hydraulic Head]
  [Press Casing]    [Press Casing]

Bottom Layer (y=0):
  [Press Controller] [Press Anvil Bed]
  [Press Casing]    [Press Casing]
```

---

### Industrial Multi-Smelter (5×4×3)
An early-to-midgame high-throughput industrial smelting array that unifies **8 independent furnaces into a single automated multiblock machine**. 

- **Dimensions**: 5 wide × 4 high × 3 deep (60 blocks total volume).
- **Hollow Core & 3D In-World Animation**:
  - The interior 3 wide × 2 high × 1 deep core (`X` in [-1, 1], `Y` in [1, 2], `Z` = 1) is a **6-block hollow air combustion chamber**.
  - The front face (`Z` = 0) features a 3×2 array of **Smelter Chamber Glass** viewing windows so players can peer directly into the active furnace interior.
  - **Dynamic In-World 3D Visuals**:
    - **Solid Fuel Combustion**: When burning solid fuel, a glowing fiery red-orange charcoal ember bed and flame grate pulse on the chamber floor.
    - **Electric RF Induction**: When running on RF/FE, 3 glowing orange electric induction coils illuminate the chamber.
    - **Live Cooking Items**: All items actively being smelted float inside the chamber in an organized 4×2 floating matrix, gently bobbing and spinning smoothly in the heat haze!
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
A compact thermal generator for small-to-mid tier power generation.

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

### Steam Turbine Generator (3×3×3)
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

### Petroleum Refinery (3×3×4)
A multi-stage fractional distillation column that refines raw Crude Oil into commercial fuels and chemical feedstocks.

- **Dimensions**: 3 wide × 4 tall × 3 deep (36 blocks total).
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
A heavy dual-roller crushing and pulverizing mill that doubles raw ore yields and processes geological strata.

- **Dimensions**: 3 wide × 3 tall × 3 deep (27 blocks total, matching the Refinery, Boilers, and Blast Smelter).
- **Core Functionality**:
  - **Ore Doubling**: Pulverizes Raw Iron, Raw Gold, and Raw Copper (or ore blocks) into **2x Dust** (`refinery:iron_dust`, `gold_dust`, `copper_dust`). Smelting these dusts produces 2 ingots per raw ore!
  - **Stone Crushing**: Crushes Stone, Cobblestone, Deepslate, Andesite, Diorite, and Granite into **1x Gravel**.
  - **Sand Pulverizing**: Crushes Gravel into **1x Sand**.
  - **Ingot Grinding**: Converts Ingots into **1x Dust** for metallurgy and chemistry.
- **Animated Mechanical Spiked Drums & Pulverizing Block**: Features twin physical cylindrical counter-rotating crusher drums with protruding intermeshing steel spikes visible through the reinforced front viewing window (`refinery:grinder_chamber`). When actively processing, the block/ore being pulverized vibrates violently in the center nip and is drawn downwards into the spinning teeth with rich crunching sound effects and pulverized dust particles.
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

## 8. Automation, Pipes, & Interfacing

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
- **Block Off & Displace**: Fully follows standard water/lava placement rules — can be blocked off, dammed, or displaced by placing any solid block directly into it.
- **Viscous Movement Physics**: Thick, viscous fluid physics — entities sink at a steady, damped rate, can wade and swim through it with WASD, and can swim upwards to safety by holding Spacebar.
- **Handling**: Can be collected using standard Buckets to obtain **Crude Oil Buckets** (`refinery:crude_oil_bucket`), pumped via fluid extraction pipes, or manually right-clicked onto a Refinery **Crude Oil Input Port**.
- **Port Bucket Interaction**: Right-clicking the **Crude Oil Input Port** with a Crude Oil Bucket empties it into the refinery (+1,000 mB). Right-clicking the **Diesel Port** or **Gas Port** with an empty bucket collects refined fuel (-1,000 mB).

### Deposit Structures & Capacities
1. **Standard Oil Pit (600,000 mB – 1,000,000 mB)**:
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
- `minChunkDistanceBetweenWells` (default: `40`): Minimum chunk distance between oil deposits (640 blocks) to space out wells and prevent clustering (range 1–512).
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

Industrial Refinery brings back the classic, beloved **BuildCraft Pipe Logic Gate** automation system — fully re-engineered for Minecraft 26.3 NeoForge!

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

### Electric Arc Melter (3×3×3 Multiblock)

The **Electric Arc Melter** superheats mineral dusts and raw ores into liquid molten baths using high-energy graphite electrodes.

#### Multiblock Structure & Blueprint:
- **Dimensions**: 3 blocks wide $\times$ 3 blocks tall $\times$ 3 blocks deep (27 blocks total).
- **Core Requirements**:
  - 1x **Electric Arc Melter Controller** (`refinery:arc_melter_controller`) — front face, center-bottom.
  - 22x **Arc Melter Casing** (`refinery:melter_casing`) — heavy heat-dissipating refractory outer framing.
  - 2x **Arc Melter Observation Chamber** (`refinery:melter_chamber`) — tinted safety glass viewports.
  - 1x **Arc Melter Input Port** (`refinery:melter_input_port`) — top or side funnel hopper. Accepts dusts & raw ores from hoppers and item pipes.
  - 1x **Arc Melter Output Port** (`refinery:melter_output_port`) — bottom or side molten spout. Automatically pushes molten liquid to adjacent Casting Channels, fluid pipes, or tanks!
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

### Continuous Casting Press (3×3×3 Multiblock)

The **Continuous Casting Press** receives molten liquids and uses a heavy overhead hydraulic press and water-quenching die to stamp them into solidified ingots and glass blocks.

#### Multiblock Structure & Blueprint:
- **Dimensions**: 3 blocks wide $\times$ 3 blocks tall $\times$ 3 blocks deep (27 blocks total).
- **Core Requirements**:
  - 1x **Continuous Casting Press Controller** (`refinery:casting_press_controller`) — front face, center-bottom.
  - 20x **Casting Press Casing** (`refinery:casting_casing`) — heavy hydraulic frame with safety hazard striping.
  - 2x **Casting Mold Basin** (`refinery:casting_basin`) — ceramic mold bed at the base where liquid pools.
  - 2x **Casting Safety Chamber** (`refinery:casting_chamber`) — wire-reinforced safety viewing windows.
  - 1x **Casting Press Input Port** (`refinery:casting_input_port`) — top or side intake. Connects directly to Casting Channels or fluid pipes.
  - 1x **Casting Press Output Port** (`refinery:casting_output_port`) — bottom or side exit conveyor chute. Automatically ejects finished ingots and blocks into hoppers, chests, or transport pipes!

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
  - Saplings are planted directly on natural soil blocks (`#minecraft:dirt` — Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling them into farmland.
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
  - Redesigned all 8 pipe gates (Basic, Iron, Gold, Diamond — standard & autarchic) into 14x14 beveled industrial attachment plates with tier-specific bezels, corner mounting bolts, and glowing logic cores.
  - Redesigned Autarchic Chipset into an authentic ceramic dual-in-line IC chip with gold pins and glowing energy core.

---

### Version 1.5.0 (Industrial Multi-Smelter Array)
- **Industrial Multi-Smelter (5×4×3 Multiblock)**:
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
- **Electric Arc Melter (3×3×3 Multiblock)**:
  - High-temperature induction melting with 3D animated descending carbon electrodes and dynamic electric arc discharges.
- **Open Refractory Casting Channel**:
  - Seamless 3D trough geometry with directional side extensions, down-spouts, and glowing flowing molten liquid rendering.
- **Continuous Casting Press (3×3×3 Multiblock)**:
  - Overhead hydraulic stamping ram with water-quenching steam clouds, mechanical piston sound effects, and ingot/block stamping.

---

### Version 1.2.0 (Grinder Overhaul & Logic Gates)
- **Industrial Grinder 3×3×3 Multiblock**:
  - Expanded into a full 3×3×3 multiblock with 3D physical counter-rotating spiked crushing drums.
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
- **Petroleum Distillation Refinery (3×3×4 Multiblock)**
- **Industrial Blast Smelter (5×5×5 Multiblock)** & **Industrial Metal Press (2×2×2 Multiblock)**
- **Thermodynamic Steam Power Suite**: Small Boiler (3×3×3), Large Boiler (5×5×5), Steam Turbine (3×3×3) with Rankine cycle.
- **Agricultural Automation**: Automated Planter and Automated Harvester.
- **Refinery Wrench**: 3D holographic blueprints and instant block dismantle.




