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
5. [Thermodynamic Steam Power & Closed-Loop Energy](#5-thermodynamic-steam-power--closed-loop-energy)
   - [Small Steam Boiler (3×3×3 Multiblock)](#small-steam-boiler-333)
   - [Large Heavy Steam Boiler (5×5×5 Multiblock)](#large-heavy-steam-boiler-555)
   - [Steam Turbine Generator (3×3×3 Multiblock)](#steam-turbine-generator-333)
   - [The Closed-Loop Rankine Cycle](#the-closed-loop-rankine-cycle)
6. [Petroleum Refining & Chemical Byproducts](#6-petroleum-refining--chemical-byproducts)
   - [Petroleum Refinery (3×3×4 Multiblock)](#petroleum-refinery-334)
   - [Fuels & Byproducts](#fuels--byproducts)
   - [Industrial Grinder & Pulverizer (2×3×3 Multiblock)](#industrial-grinder--pulverizer-2x3x3)
7. [Comprehensive Recipe Codex](#7-comprehensive-recipe-codex)
8. [Automation, Pipes, & Interfacing](#8-automation-pipes--interfacing)
9. [Crude Oil World Generation & Retrogen](#9-crude-oil-world-generation--retrogen)
   - [In-World Fluid Crude Oil](#in-world-fluid-crude-oil)
   - [Deposit Structures & Capacities](#deposit-structures--capacities)
   - [Configuration (`config/refinery.toml`)](#configuration-configrefinerytoml)
   - [Retrogen Persistence (No Re-Gen Once Mined)](#retrogen-persistence-no-re-gen-once-mined)
   - [Administrative Commands](#administrative-commands)

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
| **Heavy Steel Plate** (`refinery:steel_plate`) | Industrial Metal Press (or crafting) | Reinforced plating required for high-pressure vessels, boilers, and presses. |
| **Petroleum Tar** (`refinery:tar`) | Refinery byproduct | Heavy hydrocarbon fuel (burns 1,600 ticks / 8 items) or crafts into torches. |
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

- **Scan Coverage**: 5 blocks in all horizontal directions (an **11×11 square**, 121 tiles total) up to **2 blocks above** the planter.
- **Tilling & Planting**: Automatically detects dirt or grass blocks, tills them into farmland, and plants any valid seeds found in its 9-slot inventory.
- **Energy**: Consumes 1-2 FE/t standby and 10 FE per planting action.
- **Automation**: Accepts seeds from hoppers, droppers, or item pipes through any face.

### Automated Harvester
The **Automated Harvester** (`refinery:auto_harvester`) scans mature crops directly in front of it.

- **Scan Coverage**: An **11×11 field** directly in front of the machine (5 blocks to the left, 5 blocks to the right, and 11 blocks forward). Place this unit above ground centered with your farm.
- **Harvest & Replant**: Detects fully mature crops (Wheat, Carrots, Potatoes, Beetroots, Melon/Pumpkin stems, etc.), harvests the drops, and immediately replants the seed.
- **Buffer & Ejection**: Internal 9-slot buffer. **Automatically pushes harvested produce into chests or pipes** placed directly **behind** or **below** the harvester!
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
A massive foundry furnace that transforms Raw Iron and Coal into high-grade Steel Ingots.

- **Dimensions**: 5 blocks wide × 5 blocks tall × 5 blocks deep (125 blocks total).
- **Viewing Window**: The front wall features a 3×3 transparent **Foundry Heat Glass** window where you can watch raw materials drop into the molten chamber and cook into glowing steel!
- **Recipe**: 1 Raw Iron + 1 Coal/Charcoal -> 1 Steel Ingot (100 ticks).
- **Buffers**: 4 Iron input slots, 4 Fuel slots, 4 Steel output slots.
- **Automation**: 
  - **Foundry Input Port**: Inserts Raw Iron and Coal via pipes/hoppers.
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
A compact hydraulic mechanical press for stamping ingots into structural plates.

- **Dimensions**: 2 wide × 2 tall × 2 deep (8 blocks total).
- **Physical 3D Stamping Animation**: Features an in-world animated hydraulic piston ram with a power stroke, heavy anvil impact (`SoundEvents.ANVIL_LAND`), and spark ejection!
- **Recipe**: 1 Steel Ingot -> 1 Heavy Steel Plate (40 ticks = 2.0s).
- **Energy**: 50,000 FE buffer; consumes 20 FE/t (800 FE per plate).
- **Logistics**:
  - **Top & Sides**: Inserts Steel Ingots into the 2-slot input queue.
  - **Bottom**: Extracts Heavy Steel Plates from the 2-slot output buffer.

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

### Industrial Grinder & Pulverizer (2×3×3)
A heavy dual-roller crushing and pulverizing mill that doubles raw ore yields and processes geological strata.

- **Dimensions**: 2 wide × 3 tall × 3 deep (18 blocks total).
- **Core Functionality**:
  - **Ore Doubling**: Pulverizes Raw Iron, Raw Gold, and Raw Copper (or ore blocks) into **2x Dust** (`refinery:iron_dust`, `gold_dust`, `copper_dust`). Smelting these dusts produces 2 ingots per raw ore!
  - **Stone Crushing**: Crushes Stone, Cobblestone, Deepslate, Andesite, Diorite, and Granite into **1x Gravel**.
  - **Sand Pulverizing**: Crushes Gravel into **1x Sand**.
  - **Ingot Grinding**: Converts Ingots into **1x Dust** for metallurgy and chemistry.
- **Animated Mechanical Crushing**: Features twin physical counter-rotating grinding drums with intermeshing fluted tungsten teeth visible through the front reinforced viewing window! The rollers spin smoothly in idle mode and accelerate rapidly with grinding sound effects and sparks during active cycles.
- **Specs**:
  - Energy Storage: 50,000 FE
  - Energy Consumption: 30 FE/t while grinding
  - Process Duration: 60 ticks (3.0 seconds) per batch
  - Internal Buffer: 4 Input slots + 4 Output slots

#### Grinder Layer Schematic (Local Coordinates)
- **Layer 0 (Foundation & Output)**:
  - `(0, 0, 0)`: **Industrial Grinder Controller** (Front Left)
  - `(1, 0, 0)`: **Grinder Discharge Port (Output)** (Front Right)
  - `(0, 0, 1)`, `(1, 0, 1)`, `(0, 0, 2)`, `(1, 0, 2)`: **Grinder Structural Casing**
- **Layer 1 (Crushing Chamber & Rollers)**:
  - `(0, 1, 0)`: **Grinder Structural Casing**
  - `(1, 1, 0)`: **Grinder Crushing Chamber** (Front Viewing Window into animated rollers)
  - `(0, 1, 1)`, `(1, 1, 1)`, `(0, 1, 2)`, `(1, 1, 2)`: **Grinder Structural Casing**
- **Layer 2 (Feed Chute & Input)**:
  - `(0, 2, 0)`: **Grinder Structural Casing**
  - `(1, 2, 0)`: **Grinder Feed Port (Input)** (Front Right Top)
  - `(0, 2, 1)`, `(1, 2, 1)`, `(0, 2, 2)`, `(1, 2, 2)`: **Grinder Structural Casing**

---

## 7. Comprehensive Recipe Codex

### Base Materials & Tools
| Result | Pattern / Ingredients |
| :--- | :--- |
| **Refinery Wrench** | Iron Ingot, Copper Ingot, Lever (Shaped) |
| **Heavy Steel Plate** | 1 Steel Ingot (Shapeless in crafting table or 1 Ingot in Metal Press) |
| **Bituminous Oil Sand** (x2) | 1 Sand + 1 Petroleum Tar (Shapeless) |
| **Gunpowder (from Sulfur)** | 1 Sulfur Dust + 1 Charcoal (Shapeless) |
| **Torches (from Tar)** | 1 Petroleum Tar + 1 Stick -> 4 Torches (Shapeless) |

### Multiblock Machine Blocks
| Machine Block | Crafting Ingredients |
| :--- | :--- |
| **Grinder Casing** (x4) | 4 Steel Plates + 4 Steel Ingots (Shaped ring) |
| **Grinder Controller** | 1 Grinder Casing + 4 Steel Plates + 2 Redstone Blocks + 1 Comparator + 1 Steel Ingot |
| **Grinder Chamber** | 4 Steel Plates + 2 Iron Bars + 2 Foundry Heat Glass |
| **Grinder Input Port** | 4 Steel Plates + 1 Hopper (Top funnel configuration) |
| **Grinder Output Port** | 4 Steel Plates + 1 Hopper (Bottom discharge configuration) |
| **Press Casing** (x4) | 4 Steel Ingots + 4 Iron Ingots around Smooth Stone |
| **Press Anvil Bed** | 3 Steel Ingots (top) + 1 Anvil (center) + 3 Steel Plates (bottom) |
| **Press Hydraulic Head** | 4 Steel Ingots + 2 Steel Plates + 1 Piston + 2 Redstone |
| **Metal Press Controller** | 4 Steel Ingots + 4 Steel Plates + 2 Redstone Blocks + 1 Comparator |
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

### Mineral Pulverizing & Smelting
| Input | Machine / Process | Output |
| :--- | :--- | :--- |
| **Bituminous Oil Sand** | Industrial Grinder | **3x Bitumen** (+ 50% chance for **1x Tar**) |
| **Bitumen** | Furnace / Blast Furnace | **1x Petroleum Tar** |
| **Raw Iron / Iron Ore / Deepslate Iron Ore** | Industrial Grinder | **2x Iron Dust** (Ore Doubling!) |
| **Raw Gold / Gold Ore / Deepslate Gold Ore** | Industrial Grinder | **2x Gold Dust** (Ore Doubling!) |
| **Raw Copper / Copper Ore / Deepslate Copper Ore** | Industrial Grinder | **2x Copper Dust** (Ore Doubling!) |
| **Stone / Cobblestone / Deepslate / Andesite / Diorite / Granite** | Industrial Grinder | **1x Gravel** |
| **Gravel** | Industrial Grinder | **1x Sand** |
| **Iron / Gold / Copper Ingot** | Industrial Grinder | **1x Respective Dust** |
| **Iron / Gold / Copper Dust** | Furnace / Blast Furnace | **1x Respective Ingot** |

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
- **Handling**: Can be collected using standard Buckets to obtain **Crude Oil Buckets** (`refinery:crude_oil_bucket`), pumped via fluid extraction pipes, or manually right-clicked onto a Refinery **Crude Oil Input Port**.
- **Port Bucket Interaction**: Right-clicking the **Crude Oil Input Port** with a Crude Oil Bucket empties it into the refinery (+1,000 mB). Right-clicking the **Diesel Port** or **Gas Port** with an empty bucket collects refined fuel (-1,000 mB).

### Deposit Structures & Capacities
1. **Standard Oil Pit (300,000 mB – 500,000 mB)**:
   - **Surface Crater**: A natural dark petroleum lake with shoreline beaches of **Bituminous Oil Sand** (`refinery:oil_sand`).
   - **Feeder Conduit**: A vertical cylindrical conduit dropping through the earth.
   - **Subterranean Reservoir**: A massive subterranean cavern embedded in the deep stone crust, filled with hundreds of source blocks and encased in an oil sand stratum.
   - **Yield**: Provides 300 to 500 buckets (300k to 500k mB) of pumpable crude oil, plus dozens of mineable oil sand blocks.
2. **Rare Super-Geyser (1,000,000 mB)**:
   - A rare high-pressure geologic anomaly (default 20% of spawned oil pits).
   - Features an active towering geyser fountain erupting above the surface lake, fed by an immense subterranean reservoir dome holding **1,000 crude oil source blocks** (1,000,000 mB).

### Configuration (`config/refinery.toml`)
All generation parameters are fully customizable:
- `enableOilRetrogen` (default: `true`): Enables automatic generation in existing explored chunks.
- `oilPitSpawnChance` (default: `0.04`): ~4% chance per chunk in allowed biomes.
- `minChunkDistanceBetweenWells` (default: `4`): Minimum chunk distance between oil deposits to space out wells and prevent clustering.
- `rareGeyserChance` (default: `0.20`): 20% chance for a spawned pit to be a 1,000k mB super-geyser.
- `minOilVolumeMb` / `maxOilVolumeMb` (default: `300000` / `500000`): Standard pit capacity range.
- `geyserOilVolumeMb` (default: `1000000`): Super-geyser oil capacity.
- `allowedBiomes`: Configurable list of biome IDs or tags. By default includes `#minecraft:is_ocean`, `#minecraft:is_deep_ocean`, `#minecraft:is_desert`, `#minecraft:is_badlands`, all ocean types, deserts, and badlands/mesas. Compatible with modded biomes!

### Retrogen Persistence (No Re-Gen Once Mined)
- The retrogen manager uses a robust `RecordCodecBuilder` CompoundTag system to record all evaluated and generated chunk coordinates to `<world>/data/oil_retrogen.dat`.
- Once a chunk has had its oil generated (or evaluated), it is permanently registered. **It will never regenerate oil even after being mined, drilled, or pumped dry.**

### Administrative Commands
Commands can be issued via `/admin` or `/refinery`:
- `/admin spawn_oil_pit [true|false]` or `/admin spawn_oil [true|false]`: Spawns a standard pit (`false`) or super-geyser (`true`) at your position and registers the location.
- `/admin spawn_geyser`: Shortcut to spawn a 1,000,000 mB super-geyser.
- `/admin oil_stats`: Displays scanned chunks and total generated oil wells.
- `/admin reset_oil_retrogen`: Resets retrogen history for the world.
- *(Also mirrored under `/refinery spawn_oil_pit`, `/refinery spawn_geyser`, `/refinery stats`)*
