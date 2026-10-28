# CurseForge Upload Template & Metadata Guide: Industrial Refinery

This document contains everything needed to publish and update **Industrial Refinery** on CurseForge. You can copy-paste each section directly into the CurseForge Project Creation and File Upload forms.

---

## 1. CurseForge Project Submission Fields

### Project Name
`Industrial Refinery`

### Short Summary / Tagline (Max 255 characters)
> Heavy industry, petroleum distillation, thermodynamic steam power, closed-loop metallurgy, and classic BuildCraft-style pipe logic gates for modern Minecraft!

### Primary Category
- **Technology**

### Secondary Categories
- **Energy, Fluid, and Item Transport**
- **Processing**
- **Automation**
- **Ores and Resources**

---

## 2. Project Description (Markdown / Rich Text)
*(Copy everything below this line into the "Description" box on CurseForge)*

---

# ?? Industrial Refinery
**Heavy Industry • Fractional Distillation • Thermodynamic Closed-Loop Steam Power • Metallurgy • BuildCraft-Style Pipe Logic Gates**

*Built natively for Minecraft 26.3 on NeoForge.*

---

## ? Overview
**Industrial Refinery** is a complete industrial engineering mod designed to bring realistic, rewarding progression back to Minecraft. From prospecting for subterranean crude oil deposits in oceans and deserts to fractional distillation, closed-loop Rankine thermodynamic power cycles, high-temperature blast metallurgy, agricultural mechanization, and the legendary return of **BuildCraft-style programmable Pipe Logic Gates**!

Whether you play standalone or alongside logistics and tech mods, Industrial Refinery delivers high-performance, lag-free automation with zero hard dependencies.

---

## ?? Key Features

### ??? 1. Petroleum Geology & Fractional Distillation
- **Real In-World Fluid Crude Oil**: Naturally occurring subterranean oil reservoirs and surface lakes rimmed with bituminous oil sand. Viscous black liquid following authentic fluid physics.
- **Deposit Capacities & Spacing**: Standard oil pits yield **600,000 mB to 1,000,000 mB**, with rare high-pressure **Super-Geysers** erupting with **2,000,000 mB**! Generates ~1 in 200 chunks with an enforced **20-chunk (320-block) minimum spacing** between wells to prevent clustered lakes.
- **Persistent Anti-Retrogen**: Oil deposits are strictly registered once generated. Even when drained completely dry, chunks will never re-generate oil over your quarries or drills.
- **Fractional Distillation Column (3x3x4 Multiblock)**: Fractionates crude oil into high-energy **Diesel** (500 mB), **Gasoline** (300 mB), heavy **Bitumen** (burns 1,600t), and chemical **Sulfur Dust**. Also supports direct **Oil Sand Cracking** yielding crude oil, clean sand, and petroleum tar.

### ? 2. Thermodynamic Closed-Loop Steam Power (Rankine Cycle)
- **Small (3x3x3) & Large Heavy (5x5x5) Steam Boilers**: Boil water with solid coal/coke or liquid petroleum fuels into pressurized Steam. Features authentic boiler overheating mechanics!
- **Steam Turbine Generator (3x3x3 Multiblock)**: Converts high-pressure steam into **250 FE/t** of clean electricity while simultaneously condensing the exhaust steam back into pure water.
- **Closed-Loop Conservation**: Pipe the turbine's condensed water directly back into your boiler to build completely closed-loop, self-sustaining power plants!

### ?? 3. Heavy Metallurgy & Grinding
- **Industrial Blast Smelter (5x5x5 Multiblock)**: Massive foundry with a continuous 3x3 industrial glass viewing window. Smelts raw iron and coal/coke into structural **Steel Ingots**.
- **Industrial Metal Press (2x2x2 Multiblock)**: Mechanical stamping press that stamps ingots into heavy **Steel Plates**, rods, and components.
- **Industrial Grinder & Pulverizer (3x3x3 Multiblock)**: Dual-motor mineral crusher with twin counter-rotating spiked drums and animated block pulverization. Provides **2x Ore Doubling**, crushes stone into gravel and sand, and pulverizes oil sand into **3x Bitumen** with a 50% chance for **Tar**.
- **Industrial Multi-Smelter (5x4x3 Multiblock)**: 8-in-1 high-throughput smelting array with dual-fuel operation (Solid Fuel or 30 FE/t RF). Features a 6-block hollow combustion chamber with dynamic 3D floating cooking items and pulsing ember/induction animations.
- **Electric Arc Melter (3x3x3 Multiblock)**: High-temperature metallurgical induction furnace that melts metal dusts and sand into glowing Molten Iron, Gold, Copper, Tin, Bronze, and Glass.
- **Continuous Casting Press (3x3x3 Multiblock)**: Automated hydraulic quenching press that casts molten liquid from Arc Melters into solid ingots and glass blocks.
- **Refractory Casting Channel**: Open 3D trough geometry for seamless in-world fluid transfer between melters and presses.
- **Industrial Fermenter (Agricultural Biofuel)**: Standalone bioreactor that converts crops (Wheat, Potatoes, etc.) into high-yield **Ethanol** fuel.

### ?? 4. Standalone Agricultural Automation
- **Auto Planter**: Smart cultivator with GUI On/Off switch and configurable work radius up to 9 blocks away in all directions (3x3 up to 19x19 / 361 tiles). Automatically prepares soil and plants seeds and tree saplings from internal storage, hoppers, or pipes.
- **Auto Harvester**: Omnidirectional farm harvester with GUI On/Off switch and configurable radius up to 9 blocks in all directions. Auto-harvests mature crops, fells trees/leaves, replants seeds, and ejects drops into chests or pipes behind or below.

### ?? 5. BuildCraft-Style Pipe Logic Gates & Wires
The classic BuildCraft automation system, re-engineered for modern Minecraft!
- **Slim Face Attachment**: Attaches flush onto **any face of a pipe**, machine, chest, or refinery port with non-solid hitboxes.
- **High-Definition Visuals**: Beautifully detailed 14x14 beveled industrial attachment plates with tier-specific metallic bezels, corner mounting bolts, and glowing logic cores; pipe wires render as industrial wire spools with metallic spindles and coiled insulation.
- **Native Cross-Mod Integration**: Directly interfaces with **Logistics Mod Item & Fluid Pipes** as well as standard NeoForge containers and machines.
- **4 Gate Tiers**:
  - **Basic Gate**: 1 conditional trigger-action pair.
  - **Iron Gate**: 2 conditional pairs + Red & Blue wire channels.
  - **Gold Gate**: 4 conditional pairs + 4-wire network support.
  - **Diamond Gate**: 8 conditional pairs across an industrial dual-column dashboard.
- **Autarchic Pulsar (Automated Extraction)**: Upgrade any gate with an **Autarchic Chipset** (via crafting or in-world right-click) to pulse every 1 second, pulling 1 item from adjacent containers into the pipe or pumping 250 mB of fluid. **No redstone clocks or external engines required!**
- **4-Color Pipe Wires**: Broadcast Red, Blue, Green, and Yellow logic signals along continuous pipe networks up to 24 blocks.
- **Visual Console**: Interactive GUI with live glowing green status LEDs for every active condition!

### ?? 6. 3D Holographic In-World Blueprints & The Refinery Wrench
- Right-click any multiblock controller with the **Refinery Wrench** to project a life-sized, translucent **3D building hologram**.
- **Color-Coded Blueprints**: Every block type renders in a signature high-contrast color (Emerald Controllers, Petroleum Amber Ports, Canary Diesel, Flame Orange Fuel, Fiery Red Fireboxes, Lime Energy, Crystal Purple Glass) so multiblock construction is effortless!
- **Instant Dismantling**: Shift + Right-Click any machine block with the wrench to instantly break and collect it into your inventory.

---

## ?? Mod Compatibility

Industrial Refinery is built with zero hard dependencies:
- **Logistics Mod**:
  - Full native pipe attachment & gate operation.
  - 1:1 interchange recipes between `refinery:tar` $\leftrightarrow$ `logistics:core/tar` and `refinery:bitumen` $\leftrightarrow$ `logistics:core/bitumen`.
  - 8 Logistics fluid pipes craftable using Refinery tar.
  - Industrial Grinder pulverizes all Logistics oil sands and outputs preferred Logistics items when present.
- **Any Tech / Modpack Setup**:
  - Fully compatible with standard NeoForge Transfer API (`Capabilities.Item.BLOCK`, `Capabilities.Fluid.BLOCK`, `Capabilities.Energy.BLOCK`).

---

## ??? Administrative Commands

- `/admin spawn_oil_pit [true|false]`: Spawns a natural oil pit (`false`) or super-geyser (`true`) at your coordinates.
- `/admin spawn_geyser`: Instantly spawns a 1,000,000 mB erupting super-geyser.
- `/admin oil_stats`: Displays scanned chunks and registered oil wells.
- `/admin reset_oil_retrogen`: Resets retrogen history for the current save world.
*(Also mirrored under `/refinery ...`)*

---

## ?? Modpack Permissions & FAQ

- **Can I include this in my public or private modpack?**  
  **Yes!** You are completely free to include Industrial Refinery in any CurseForge, Modrinth, or custom modpack.
- **Is this safe to add to existing worlds?**  
  **Yes.** The world generation retrogen system automatically and safely places oil pits into newly loaded and explored chunks without corrupting existing structures, with spacing constraints to prevent clustering.
- **Where can I report bugs or suggest features?**  
  Please report issues and feedback on our GitHub Issue Tracker!

---

## 3. CurseForge File Upload Form Details

When uploading your `.jar` file to CurseForge, use these values:

### File
`refinery-1.5.14.jar`

### Display Name
`Industrial Refinery 1.5.13 (NeoForge 26.3)`

### Release Type
`Release` *(or Beta if preferred for initial testing)*

### Supported Minecraft Versions
- `26.3` (or corresponding release tag)

### Java Version
- `Java 21` / `Java 25`

### Mod Loaders
- `NeoForge`

### Changelog / Release Notes
```markdown
# Industrial Refinery v1.5.14

### What's New in v1.5.14:
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

### Previous v1.5.13 Highlights:
- **Monolithic 3D Small Steam Boiler Model Overhaul (Scotch Marine Package Boiler)**:
  - Transformed the assembled 3×3×3 Small Steam Boiler into a monolithic 3D industrial packaged Scotch marine fire-tube boiler model.
  - Circular front boiler door, glowing burner sight-glass viewport, arched ash door, overhead steam manifold header with valves, exhaust stack, ICI Caldaie style control cabinet, and feed-water booster pumps.
- **Custom Industrial Small Boiler GUI Overhaul & Realignment**:
  - Dark gunmetal steel panel with recessed water & steam buffer gauges, 2x2 fuel buffer, redstone lamp, combustion flame viewport, meltdown hazard bar, and real-time status badge.

### Previous v1.5.11 Highlights:
- **Planter & Harvester GUI On/Off Power Switch**:
  - Added an interactive power toggle switch directly inside the Automated Planter and Automated Harvester GUIs.
  - Toggling OFF immediately pauses all scanning routines, ceases idle energy consumption, and displays a clear `DISABLED` status.
  - State is preserved across world saves and synchronized via container network data.
- **Configurable Work Radius in All Directions (Up to 9 Blocks / 100m+ Area)**:
  - Both the Automated Planter and Automated Harvester now feature interactive `[-]` and `[+]` GUI buttons to configure the work radius anywhere from 1 to 9 blocks in all directions.
  - Spans farm coverage from compact 3x3 plots up to massive 19x19 fields (361 blocks / 100m+ area) with live radius readout and area display. Default radius is 5 blocks (11x11 square).
- **Omnidirectional Harvester Scanning**:
  - The Automated Harvester now scans in all horizontal directions around itself up to the configured radius, fully matching the Planter's circular/square perimeter coverage while maintaining backward-compatible auto-ejection to chests or pipes placed behind or below.

### Previous v1.5.9 Highlights:
- **3-Block-Wide (3x2x2) Industrial Metal Press with Conveyor Bed & AIDA NC1-1500 Stamping Head**:
  - Expanded the Metal Press multiblock structure to 3 blocks wide (3x2x2) with custom 3D model combining conveyor bed and AIDA overhead stamping head.
  - Dynamic vertical ram slide animation accelerating on downward stroke and retracting during stamping.

### Previous v1.5.8 Highlights:
- **Industrial Metal Press Monolithic AIDA NC1-1500 C-Frame Model & Texture**:
  - High-definition seamless 3D model with classic off-white machine enamel, AIDA diamond branding, dial gauge, and operator pendant.

### Previous v1.5.7 Highlights:
- **Logistics Pipe Compatibility & Machine Detection**:
  - **Accurate Machine Inventory Detection for Pipe Gates**: Fixed an issue where pipe gates attached to pipes mistakenly identified adjacent transport pipes as target inventories instead of the attached machine (Automated Harvester, Automated Planter, Boilers, Refinery Controller, etc.). Connected pipes are now strictly excluded from inventory evaluation, allowing gate conditions (`Has Items`, `Inventory Empty`, `Inventory Space`, `Inventory Full`) to accurately reflect machine contents.
  - **Autarchic Pulsar Extraction on Logistics Pipes**: Fixed a reflection call site bug in `LogisticsPipeCompat.injectItemIntoPipe` where `forceAddItem` was blocked by a polymorphic method handle cast exception. Replaced with robust reflection fallback invoking `forceAddItem(TravelingItem, Direction)` and `addItem(Direction, ItemStack)`, enabling autarchic pulsar gates on extractor pipes to reliably pulse items out of machines into the logistics network.
  - **Harvester Auto-Ejection to Logistics Pipes**: The Automated Harvester now natively detects connected Logistics Automation item pipes behind or below the block, auto-injecting harvested logs, saplings, apples, and crops directly into the pipe network in addition to chests and hoppers.
  - **WorldlyContainer Capability Wrappers**: Migrated `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity` capability registrations to `WorldlyContainerWrapper(be, side)` with explicit `canPlaceItem` and `canTakeItem` handlers, ensuring sided insertion and extraction work seamlessly with third-party logistics systems, hoppers, and pipes.
  - **Expanded Machine Status Gate Triggers**: Updated `MACHINE_ACTIVE` and `MACHINE_IDLE` gate triggers to dynamically inspect block state properties (`working`, `active`, `lit`, `running`, `powered`) and machine status getters, providing accurate operational detection for Harvesters, Planters, Boilers, and Controllers.

### ?? Previous v1.5.6 Highlights:
- **Automated Planter Sapling & Tree Support**:
  - The **Automated Planter** (`refinery:auto_planter`) now accepts and plants all tree saplings (`#minecraft:saplings`, `SaplingBlock`, and modded saplings) alongside agricultural crops.
  - Saplings are planted directly on natural soil blocks (`#minecraft:dirt` — Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling them into farmland.
- **Automated Harvester Tree & Leaf Canopy Felling**:
  - The **Automated Harvester** (`refinery:auto_harvester`) now detects tree trunks and logs (`#minecraft:logs`) and leaf blocks (`#minecraft:leaves`).
  - When a tree is scanned, the harvester performs an intelligent 3D recursive flood-fill harvest: felling the entire trunk, diagonal branches, and the surrounding leaf canopy up to 32 blocks high and 8 blocks radius.
  - Collects all tree drops into the harvester's buffer: wood logs, saplings, apples, and sticks, auto-exporting them to chests or pipes connected behind or below.
  - Standalone leaf clusters are also cleared, creating a continuous, 100% automated tree farming loop alongside the Planter.

### ?? Previous v1.5.5 Highlights:
- **Automated Agricultural Crafting Recipes**:
  - Added shaped crafting recipes for the **Automated Planter** (`refinery:auto_planter`) and **Automated Harvester** (`refinery:auto_harvester`).
  - **Planter**: 4 Iron Ingots, 2 Copper Ingots, 1 Iron Hoe (top), 1 Piston (center), 1 Redstone Dust (bottom).
  - **Harvester**: 4 Iron Ingots, 2 Copper Ingots, 1 Shears (top), 1 Hopper (center), 1 Redstone Dust (bottom).
  - Completes the standalone agricultural automation loop leading into the **Industrial Fermenter** for Ethanol biofuel production.

### ?? Previous v1.5.4 Highlights:
- **Autarchic Pulsar Extraction & Safe Injection**:
  - Fixed issue where autarchic pulsar gates spewed items onto the ground instead of into the connected pipe network.
  - Replaced standard external injection calls with internal `forceAddItem(TravelingItem, Direction)` handling in Logistics Pipes compat. External insertion was previously failing pipe acceptance rules, causing Logistics to invoke `dropItemInWorld` on every pulse.
  - Implemented smart bidirectional endpoint resolution (`PipeAndTarget`): automatically identifies which adjacent block is the pipe and which is the inventory container, properly supporting gates mounted either onto pipes facing inventories or onto chests/machines facing pipes.
  - Items are now smoothly injected into the pipe's internal traveling queue, moving into the pipe network towards connected endpoints. If insertion cannot be accepted, items remain safely inside the inventory without dropping or vanishing.
- **Pipe Gate Redstone Signal Emission**:
  - Fixed issue where pipe gates configured to emit redstone signals (`REDSTONE_SIGNAL` and `REDSTONE_PULSE`) did not power adjacent redstone dust, repeaters, comparators, or machines.
  - Introduced `BlockStateBaseMixin` injecting into `BlockBehaviour$BlockStateBase.getSignal()`, `getDirectSignal()`, and `isSignalSource()` with a thread-safe registry of active powered gate positions (`POWERED_GATE_POSITIONS`). Blocks hosting powered gates output redstone signal level 15 in all directions.
  - Added comprehensive neighbor updates (`updateNeighborsAt`) whenever gate redstone emission toggles on/off or when the gate entity is removed, guaranteeing instant response from adjacent redstone circuits.

### ?? Previous v1.5.3 Highlights:
- **Persistent Chunk "Burnt" State (Anti-Repeat Worldgen)**:
  - Fixed issue where chunks could re-evaluate oil generation upon leaving and returning if global saved data had not flushed to disk.
  - Implemented persistent NeoForge Data Attachment (`refinery:oil_chunk_burnt`) saved directly into each chunk's `.mca` region file. Once a chunk is evaluated once in its lifetime, it is permanently marked burnt on disk. Leaving and returning to a chunk will never re-roll or re-evaluate.
  - Added strict Overworld dimension restriction (`ServerLevel.OVERWORLD`).
  - Added perimeter burning: spawning an oil well now immediately burns all 8 neighbor chunks around it.
- **Ocean Generation Balancing & Spacing (v1.5.2)**:
  - Added `oceanSpawnChanceMultiplier` (default `0.20`), reducing oil spawn density in oceans by 80% to keep vast deep oceans balanced.
  - Expanded `minChunkDistanceBetweenWells` maximum range in config up to `512` chunks (default `40` chunks / 640 blocks).
```
- **Previous v1.5.1 Highlights**:
  - Industrial Multi-Smelter (5x4x3 Multiblock) 8-in-1 parallel furnace array with dual fuel/RF power.
  - Fixed Multi-Smelter item definitions and icons in 26.3.
  - Frosted clearish-white Ethanol fluid and bucket overhaul.
  - Complete HD redo of Pipe Wires, Pipe Gates, and Autarchic Chipset.
```
