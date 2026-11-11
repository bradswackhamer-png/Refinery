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

### 🛢️ 1. Petroleum Geology & Fractional Distillation
- **Real In-World Fluid Crude Oil**: Naturally occurring subterranean oil reservoirs and surface lakes rimmed with bituminous oil sand. Viscous black liquid following authentic fluid physics.
- **Deposit Capacities & Spacing**: Standard oil pits yield **600,000 mB to 1,000,000 mB**, with rare high-pressure **Super-Geysers** erupting with **2,000,000 mB**! Generates ~1 in 200 chunks with an enforced **20-chunk (320-block) minimum spacing** between wells to prevent clustered lakes.
- **Persistent Anti-Retrogen**: Oil deposits are strictly registered once generated. Even when drained completely dry, chunks will never re-generate oil over your quarries or drills.
- **3-Stage Fractional Distillation Column (3x9x3 Multiblock — Three 3x3x3 Stages)**: Divided into Stage 1 (Bottom: Liquid Tar & Bitumen), Stage 2 (Middle: Diesel), and Stage 3 (Top: Gasoline). Consumes **1,000 mB Crude Oil** per cycle with individually toggleable stages (e.g., Stage 1 only -> 500 mB Liquid Tar + 1 Bitumen; Stage 2 only -> 500 mB Diesel; Stage 3 only -> 500 mB Gasoline; S1+S2 -> 250 mB Tar + 250 mB Diesel + 1 Bitumen; S1+S3 -> 250 mB Tar + 250 mB Gas + 1 Bitumen; All 3 Stages -> 250 mB Tar + 250 mB Diesel + 250 mB Gasoline + 25% chance at 1 Bitumen).
- **Burner & Electric Refinery Furnaces (1x2x1 Heat Sources)**: Choose between the fast-burning **Burner Refinery Furnace** (4x burn speed) or high-power **Electric Refinery Furnace** (500 FE/t) placed adjacent to Stage 1 to supply the `>= 350°C` operating heat required by the Refinery Tower!
- **Oil Sand Cracking**: Direct high-pressure cracking of raw Bituminous Oil Sand yielding synthetic crude oil, clean sand, and petroleum tar.

### ⚡ 2. Thermodynamic Closed-Loop Steam Power (Rankine Cycle)
- **Small (3x3x3) & Large Heavy (5x5x5) Steam Boilers**: Boil water with solid coal/coke or liquid petroleum fuels into pressurized Steam. Features authentic boiler overheating mechanics and monolithic 3D models!
- **Steam Turbine Generator (3x3x3 Multiblock)**: Converts high-pressure steam into **1,000 FE/t** of clean electricity while simultaneously condensing the exhaust steam back into pure water. Features a custom high-fidelity monolithic 3D turbine-generator skid in vibrant industrial electric blue, featuring an AC alternator, speed-reduction gearbox, curved split casing with bolted horizontal split line, flanged steam inlet nozzle, and upright control console with analog dial gauges and emergency stop controls.
- **Closed-Loop Conservation**: Pipe the turbine's condensed water directly back into your boiler to build completely closed-loop, self-sustaining power plants!

### ?? 3. Heavy Metallurgy & Grinding
- **Industrial Blast Smelter (5x5x5 Multiblock)**: Massive foundry with a continuous 3x3 industrial glass viewing window. Smelts iron ingots / blocks and coal / coal blocks into structural **Steel Ingots**.
- **Industrial Metal Press (2x2x2 Multiblock)**: Mechanical stamping press that stamps ingots into heavy **Steel Plates**, rods, and components.
- **Industrial Grinder & Pulverizer (3x3x3 Multiblock)**: Dual-motor mineral crusher with twin counter-rotating spiked drums and animated block pulverization. Provides **2x Ore Doubling**, crushes stone into gravel and sand, and pulverizes oil sand into **3x Bitumen** with a 50% chance for **Tar**.
- **Industrial Multi-Smelter (5x4x3 Multiblock)**: 8-in-1 high-throughput smelting array with dual-fuel operation (Solid Fuel or 30 FE/t RF). Features a 6-block hollow combustion chamber with dynamic 3D floating cooking items and pulsing ember/induction animations.
- **Heavy Pit Quarry (3x3x3 Multiblock Excavator)**:
  - Heavy open-pit continuous surface mining crawler rig engineered for automated geological strip mining and deep pit excavation.
  - **3-Phase Automated Excavation Sequence**: Phase 0 clears a $43 \times 43 \times 3$ overburden terrace bench in front of the machine; Phase 1 lines the perimeter of the inner $41 \times 41$ pit with safety fencing (using custom player-supplied fences or auto-fabricated stone railings); Phase 2 excavates the inner $41 \times 41$ pit layer-by-layer all the way down to bedrock.
  - **3 Flush Conveyor Export Chutes**: Extended Rear `(0, 0, 3)`, Left `(-2, 0, 1)`, and Right `(2, 0, 1)` discharge arms with 5px bed height that connect seamlessly to mod conveyors for round-robin auto-ejection.
  - **Power-Scalable Speed**: Max speed 1 block/second requiring 20 FE/tick, smoothly running progressively slower as power drops below 20 FE/t and stopping when unpowered.
  - **Industrial Lighting**: Dual high-intensity halogen work floodlights (emitting in-world light level 15) and rotating amber hazard strobe.
  - **Telemetry GUI**: Interactive radius adjustment down to 3 ($7\times 7$), live remaining blocks breakdown dashboard tracking individual block counts with icons and tooltips, dedicated fence slot, and 9-slot buffer.
- **Electric Arc Melter (3x3x3 Multiblock)**: High-temperature metallurgical induction furnace that melts metal dusts and sand into glowing Molten Iron, Gold, Copper, Tin, Bronze, and Glass.
- **Continuous Casting Press (3x3x3 Multiblock)**: Automated hydraulic quenching press that casts molten liquid from Arc Melters into solid ingots and glass blocks.
- **Refractory Casting Channel**: Open 3D trough geometry for seamless in-world fluid transfer between melters and presses.
- **Industrial Fermenter (Agricultural Biofuel)**: Standalone bioreactor that converts crops (Wheat, Potatoes, etc.) into high-yield **Ethanol** fuel.

### ?? 4. Standalone Agricultural Automation
- **Auto Planter**: Smart cultivator with GUI On/Off switch and configurable work radius up to 9 blocks away in all directions (3x3 up to 19x19 / 361 tiles). Automatically prepares soil and plants seeds and tree saplings from internal storage, hoppers, or pipes.
- **Auto Harvester**: Omnidirectional farm harvester with GUI On/Off switch and configurable radius up to 9 blocks in all directions. Auto-harvests mature crops, fells trees/leaves, replants seeds, and ejects drops into chests or pipes behind or below.
- **Livestock Feeding Trough (`refinery:feeding_trough`)**: A 2-block-wide monolithic galvanized steel feed bunk engineered for automated livestock breeding and animal farming. Features a dynamic 3D golden straw mound that physically rises and falls with inventory stock, a 4.5m herd radar, automated breeding for any adult animal off cooldown, full dietary compatibility (wheat, seeds, vegetables, fruit, fungi, meat, fish), and seamless Logistics Automation pipe and hopper connectivity.

### ⛽ 5. Portable Industrial Fluid Pumps & 3D Suction Hose
- **Three Specialized Variants**:
  - **Coal Fluid Pump (`refinery:coal_fluid_pump`)**: Heavy Cast-Iron Dark Charcoal frame (`#374151`). Single solid-fuel slot burning coal, charcoal, or furnace fuels at 1 coal per 3 seconds (60 ticks) of active pumping.
  - **Diesel Fluid Pump (`refinery:diesel_fluid_pump`)**: Caterpillar Yellow-Orange frame (`#FA983A`). Internal 4,000 mB diesel reservoir consuming 10 mB per bucket pumped (1 bucket powers 100 buckets pumped). Refuelable via bucket slots or fluid pipes.
  - **Electric Fluid Pump (`refinery:electric_fluid_pump`)**: High-Voltage Electric Blue frame (`#2563EB`). 20,000 FE buffer consuming 400 FE per bucket pumped (20 FE/t). Connects to external RF/FE power grids.
- **In-World 3D Flexible Suction Hose**: Sneak-click with the Refinery Wrench to link, then click any block or fluid source in the world (up to 64 blocks away) to deploy a dynamic 3D spiral-ribbed suction hose with brass suction strainer.
- **Connected Fluid Source BFS Flood-Fill**: Intelligently discovers and pumps all contiguous, connected fluid source blocks one at a time from the intake point outward without touching unconnected pockets. Full water pool drainage now navigates both source and flowing water cleanly to drain pools dry.
- **Infinite Water Mode (`[ INFINITE ]` / `[ DRAIN ]`)**: Toggleable in the dedicated GUI or by **Shift-Right-Clicking the pump block in-world** with an empty hand or wrench! Infinite mode draws unlimited water without destroying source blocks; Drain mode clears fluid bodies completely dry.
- **Dedicated GUIs**: Clean, customized screen layouts displaying only the controls and gauges appropriate for that pump type (no useless RF gauges on coal pumps or coal slots on electric pumps). Intake coordinates are hidden for maximum UI clarity.
- **Logistics Craft Output**: Automatically discharges pumped fluids directly into adjacent Logistics Craft fluid pipes and tanks through its top outlet flange.

### 📦 6. Industrial Belt Conveyor System
- **Modular Low-Profile Belt Conveyors (`refinery:conveyor_belt`)**:
  - High-throughput item transport and entity conveyance modeled after extruded aluminum industrial conveyor lines.
  - **Monolithic 3D Model**: Extruded anodized aluminum bed frame with side T-slot channels, heavy cast mounting foot pads, and galvanized steel deck bed.
  - **Matte Black Industrial Belt**: High-density vulcanized rubber canvas belt with micro-grip friction ribs.
  - **Dynamic In-World Animation**:
    - Seamless UV texture scrolling animation smoothly moves the top belt forward and bottom return belt backward.
    - Precision brushed stainless steel cylindrical end rollers physically rotate around their axles at exact belt surface velocity.
    - Real-time physical item centering and smooth propulsion along the conveyor line.
    - Item-only conveyance without player or mob interference.
  - **Intelligent Seamless Snapping**:
    - Placing conveyors in a row automatically snaps them together into a continuous, seamless belt assembly.
    - Intermediate end terminals open up, seamlessly joining side rails, T-slots, and belt surfaces with zero gaps or visible seams.
    - Head and tail rollers and machined bearing plates automatically render only at the exposed line terminals.
  - **Automated Machine & Container Integration**:
    - Items reaching the conveyor head automatically inject into adjacent inventories, chests, hoppers, furnaces, and machines via the NeoForge item transfer system.
    - Hoppers, droppers, and pipes can directly deposit items onto the belt surface.
  - **Redstone & In-Place Rotation Controls**:
    - Redstone signals pause conveyor operation, halting belt scrolling and item transit.
    - Right-click with a Wrench rotates conveyor orientation clockwise; Sneak-Right-Click with a Wrench rotates counter-clockwise.
    - Sneak-Right-Click with an empty hand also rotates orientation clockwise.
- **Sloped Conveyor Belt (`refinery:sloped_conveyor_belt`)**:
  - Transports items smoothly up or down 1-block elevations with height-interpolating slope physics and centered tracking.
  - Automatically transitions items to adjacent conveyors, chests, and machine inventories at the upper and lower terminals.
  - Right-click with a wrench or sneak-empty-hand to rotate facing; Sneak-Right-Click with a wrench to toggle elevation incline between `UP` and `DOWN`.
- **Right-Angle Conveyor Belt (`refinery:right_angle_conveyor_belt`)**:
  - 90-degree corner conveyor for clean factory turn routing with zero item spillage.
  - Two-phase corner vectoring: guides incoming items into center, then turns velocity 90 degrees toward the exit.
  - 3D monolithic model with curved aluminum rails and 8-segment animated curved belt.
  - Right-click with a wrench or sneak-empty-hand to rotate facing; Sneak-Right-Click with a wrench to toggle turn direction between `RIGHT` and `LEFT`.

### 🛢️ 7. Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks
- **Industrial Bulk Liquid Storage**: High-capacity Glass-Fused-to-Steel (GFS) / Enamel bolted industrial liquid storage tanks based on modern municipal and petroleum tank engineering.
- **Six Standard Sizes & Scalable Capacities**:
  - `3x3x3`: **64,000 mB** (64 Buckets)
  - `3x3x6`: **256,000 mB** (256 Buckets)
  - `3x3x9`: **512,000 mB** (512 Buckets)
  - `5x5x5`: **1,000,000 mB** (1,000 Buckets)
  - `5x5x7`: **2,000,000 mB** (2,000 Buckets)
  - `5x5x9`: **4,000,000 mB** (4,000 Buckets)
- **Universal Fluid Handling**: Accepts and stores any fluid resource (Water, Lava, Crude Oil, Diesel, Gasoline, High-Pressure Steam, Ethanol, Molten Metals) with transactional rollback integrity.
- **Top-Center Intake & Bottom-Center Drainage**:
  - Fills from the top-center valve (`refinery:storage_tank_fill_valve`) via pipes, pumps, or buckets.
  - Drains from the bottom-center valve (`refinery:storage_tank_drain_valve`).
- **No Extraction Pipe Required (Direct Logistics & Pipe Auto-Push)**:
  - The drain valve actively pushes stored fluid downward into pipes run beneath the tank floor and horizontally into neighboring pipes.
  - Connecting regular liquid pipes immediately receives fluid flow without needing an extractor pipe or mechanical puller!
- **Dedicated Industrial GUI & Sight Glass Gauge**:
  - Brushed steel interface with input/output bucket interaction slots.
  - Full-height illuminated sight glass tube displaying fluid color, percentage, formatted fluid name, and exact mB volume.
  - Dedicated "Dump / Flush" button to quickly purge residual fluids when reconfiguring tank storage.
- **Monolithic 3D Render & Holographic Blueprints**:
  - Monolithic shell with high-gloss dark enamel bolted plates, reinforced concrete foundation curb, galvanized wind stiffener rings, caged safety climbing ladder, intermediate rest landing, and roof perimeter handrails.
  - Full holographic blueprint projection with the Refinery Wrench (`Shift-Right-Click` cycles target sizes; `Right-Click` activates the ghost guide).

### 🔌 8. BuildCraft-Style Pipe Logic Gates & Wires
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

### 🔧 9. 3D Holographic In-World Blueprints & The Refinery Wrench
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
`refinery-1.11.71.jar`

### Display Name
`Industrial Refinery 1.11.71 (NeoForge 26.3)`

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
# Industrial Refinery v1.11.71

### What's New in v1.11.71:
- **Monolithic 3D Multiblock Blast Furnace Model & In-World Rendering**:
  - Implemented high-fidelity in-world 3D model rendering for the assembled 5x5x5 Multiblock Blast Furnace:
    - **Structural Steel Panoramic Window Bezel & Truss**: Heavy industrial I-beam lintel, sill, and jamb girders boxing the 3x3 front viewing window, reinforced by 4 riveted corner gusset bracket plates and a center mullion cross-strut with a circular connection rosette hub.
    - **Foundry Ingot Rating Emblem**: Heavy embossed steel rating nameplate badge mounted above the front window lintel.
    - **Stepped Refractory Crucible Hearth Basin**: Multi-tier magnesia-carbon firebrick refractory hearth lining the bottom layer of the 3x3x3 interior chamber, complete with a tapping drainage launder notch.
    - **Dynamic Molten Pig Iron / Steel Bath**: Fully animated undulating incandescent molten metal bath glowing fiercely in fullbright (`15728880`) with wave height oscillation and floating dark metallurgical slag crust rafts.
    - **Superheated Hot Blast Tuyeres**: 6 water-cooled cast copper blowpipe nozzles projecting from the refractory walls into the hearth.
    - **Active Smelting Hot Blast Flame Jets**: Roaring high-temperature oxygen-enriched flame plasma streams blasting from the tuyere nozzles directly into the molten hearth during active smelting.
    - **Overhead Bustle Pipe Ring Manifold & Downcomers**: Suspended insulated toroidal dark steel hot blast distribution pipe with 4 vertical gooseneck downcomers feeding the tuyere cooling jackets.
    - **Conical Charge Throat & Distribution Bell Valve**: Suspended conical hopper throat and heavy cast distribution bell valve at the ceiling center.
- **Dedicated 256x256 Blast Furnace Entity Texture Atlas (`assets/refinery/textures/entity/blast_furnace.png`)**:
  - High-definition textures for structural steel girders, refractory hearth firebrick, incandescent molten bath, slag crust, copper tuyeres, bustle piping, and blast flame jets.
- **3D Block Models for Blast Furnace Components**:
  - `blast_furnace_controller`: Overhauled block model with a recessed operator instrument panel (set back 2 pixels), heavy protective overhead hood, side casing pillars, and lower foundation sill.
  - `foundry_input_port`: Overhauled block model with a recessed pneumatic feed intake hopper throat and circular mounting flange.
  - `foundry_output_port`: Overhauled block model featuring a protruding physical cast iron casting spout and tapping launder trough extending forward.
- **16x16 Industrial Block Textures Overhaul**:
  - `blast_furnace_casing.png`: High-definition dark gunmetal refractory casing with chamfered perimeter bevels, 4 heavy corner hex-head bolts, and an embossed center refractory ceramic heat-shield plate.
  - `blast_furnace_controller_off.png`: Recessed operator console featuring a brass-rimmed analog pyrometer dial, multi-tier status LED cluster, and horizontal telemetry CRT monitor.
  - `blast_furnace_controller_on.png`: Active operator console with the pyrometer dial pegged into the glowing 1600°C+ red zone, bright active green/yellow status LEDs, and glowing cyan telemetry diagnostics.
  - `foundry_port_input.png`: High-detail pneumatic hopper intake with circular flange bolts and blue directional feed chevrons.
  - `foundry_port_output.png`: Heavy refractory taphole opening with slag-resistant collar and glowing amber exit chevrons.
  - `foundry_glass.png`: Clear high-temperature quartz glass with embedded diamond steel wire-mesh reinforcement and crisp specular gleams.
```

---

```markdown
# Industrial Refinery v1.11.62

### What's New in v1.11.62:
- **Multi-Smelter 8-Slot Parallel Input Load Balancing**:
  - Piped, conveyed, and hopper-fed items now balance evenly across all 8 input slots instead of filling one slot stack full first. All 8 slots smelt in parallel!
- **Multi-Smelter Auto-Fuel Retention Reserve Option**:
  - Interactive GUI button allows setting a target fuel reserve (0..64) in the fuel slot; produced solid fuels (Charcoal) automatically replenish the furnace before excess is exported.

---

# Industrial Refinery v1.11.54

### What's New in v1.11.54:
- **Multi-Smelter Plank Pyrolysis (Planks to Charcoal)**:
  - Added support for converting any wooden planks (`#minecraft:planks`) directly into Charcoal (`minecraft:charcoal`) inside the 5x4x3 Heavy Multi-Smelter.
  - Multi-Smelter input slots now accept planks and smelt them in parallel at full speed (5 seconds per batch) using electric induction or solid fuel.
  - Shift-clicking in the Multi-Smelter GUI now prioritizes loading smeltable goods (planks, logs, ores) into input slots before overflowing to the combustion fuel slot.
  - Updated the in-game Field Manual Guide Book with the Plank Pyrolysis recipe details.

---

# Industrial Refinery v1.11.51

### What's New in v1.11.51:
- **Iron Item Pipe & Cast Iron Fluid Pipe Recipe Collision Fix (`cast_iron_fluid_pipe.json`)**:
  - Resolved recipe conflict where Iron Item Pipes and Cast Iron Fluid Pipes shared the same shaped recipe (`iron_ingot + glass + iron_ingot`).
  - Restored Iron Item Pipe as the exclusive owner of the classic horizontal `iron_ingot + glass + iron_ingot` recipe.
  - Cast Iron Fluid Pipe now crafts using `iron_bars + glass + iron_bars` (`data/refinery/recipe/cast_iron_fluid_pipe.json`).
  - Added an alternative metallurgical recipe using Iron Ingots and Coal/Charcoal (`data/refinery/recipe/cast_iron_fluid_pipe_from_coal.json`).

---

# Industrial Refinery v1.11.49

### What's New in v1.11.49:
- **Industrial Diesel Generator Single-Item Drop Fix (`DieselGeneratorBlock`)**:
  - Fixed an issue where destroying a placed Industrial Diesel Generator in survival mode dropped two generator items.
  - Overrode `getDrops` in Java so that only the master `LEFT` block drops the single generator item (`refinery:diesel_generator`) while the slave `RIGHT` block returns empty drops.
  - Updated `playerWillDestroy` to safely drop and clear container contents, remove the partner block silently with flag 35 and break particles, and pop strictly 1 generator item only when a survival player breaks the non-master `RIGHT` block.
  - Added `data/refinery/loot_tables/blocks/diesel_generator.json` and ensured both `loot_table` and `loot_tables` enforce `part = left` state conditions.

---

# Industrial Refinery v1.11.47

### What's New in v1.11.47:
- **Autarchic Chipset Recipe Yield (4x Multiplier)**:
  - Crafting an Autarchic Chipset now yields **4x Autarchic Chipsets** instead of 1 (crafting 1x Ender Pearl + 2x Redstone Dust + 2x Gold Nugget produces 4 chipsets).
  - Significantly reduces microchip production bottlenecks when mass-upgrading Pipe Gates to Autarchic variants for automated pulsating extraction.

---

# Industrial Refinery v1.11.42

### What's New in v1.11.42:
- **In-Game Guide Book Energy & Power Specifications**:
  - Integrated dedicated power badges (`⚡ Power / Energy: ...`) into the Industrial Field Manual for every machine, multiblock, and tool.
  - Documents exact FE/RF per tick rates, operating buffers, and fuel/thermal requirements:
    - **Electric Arc Melter**: 10,000 FE/t (2,000,000 FE buffer, 100,000 FE/t max in)
    - **Heavy Multi-Smelter**: 5,000 FE/t electric induction or solid fuel (1,000,000 FE buffer, 50,000 FE/t max in)
    - **Industrial Rock Crusher / Grinder**: 2,000 FE/t (500,000 FE buffer, 50,000 FE/t max in)
    - **Continuous Casting Press**: 600 FE/t (500,000 FE buffer, 20,000 FE/t max in)
    - **Hydraulic Metal Press**: 500 FE/t (100,000 FE buffer, 20,000 FE/t max in)
    - **Electric Refinery Furnace**: 500 FE/t when heating (500,000 FE buffer)
    - **Steam Turbine Generator**: Produces 10,000 FE/t (1,000,000 FE buffer, 50,000 FE/t max active export)
    - **Heavy Pit Quarry**: 240 FE/t default (1.5 blocks/sec mining speed, configurable in `refinery.toml`)
    - **Industrial Diesel Generator**: Produces 256 FE/t (500,000 FE buffer, 1,024 FE/t max export)
    - **Electric Fluid Pump**: 400 FE/cycle (10 ticks = 2,000 mB, ~40 FE/t continuous, 20,000 FE buffer)
    - **Industrial Fermenter**: 15 FE/t (20,000 FE buffer, 500 FE/t max in)
    - **Crude Oil Pump Jack**: 1 to 4 FE/t proportional scaling (10,000 FE buffer)
    - **Auto Planter & Harvester**: 1 FE/t idle standby + 10 FE/action or 20 FE/crop
    - **Boilers, Furnaces, & Logistics**: Fully documented unpowered / solid/liquid fuel operation (0 FE/t)
  - Added smooth mouse wheel scrolling (`contentScrollOffset`) and visual scrollbars to the Overview tab, Bill of Materials, and Recipes tabs in `RefineryGuideScreen`.
  - Added a dedicated Guide Book entry for **Diesel Steam Boilers (Small & Large)** (`diesel_boilers`) with 3x3x3 multiblock bill of materials, layer diagrams, and fuel consumption rates.
  - Added Oil Sand Feedstock Extraction recipe to the Petroleum Refinery guide entry.
- **Continuous Casting Press Cycle Duration**:
  - Increased casting stroke cycle time from 12.0s to **20.0 seconds (400 ticks)** per process stroke.
  - Dynamically scaled 3D animated hydraulic stamping ram (approach stroke, high-pressure dwell hold, and smooth hydraulic retraction) and molten basin solidification across the full 400-tick duration.
  - Consumes 240,000 FE total per stroke (600 FE/t over 400 ticks).
- **Electric Arc Melter Batch Duration**:
  - Increased ore melting cycle time from 6.0s to **10.0 seconds (200 ticks)** per batch.
  - Dynamically synchronized the 3D electrode arc plasma animation and crucible molten bath filling to the new 200-tick duration.
  - Consumes 2,000,000 FE total per batch (10,000 FE/t over 200 ticks), perfectly matching the full 2,000,000 FE internal buffer.

---

# Industrial Refinery v1.11.40

### What's New in v1.11.39:
- **Fast 0.5-Second Pipe Gate Redstone Pulsing (`GateAction.REDSTONE_PULSE_HALF_SEC`)**:
  - Added an extra redstone pulse option to all Pipe Gates (`Basic`, `Iron`, `Gold`, `Diamond`, and autarchic variants): `Redstone Pulse (0.5s)` (GUI short label `Pls .5s`), emitting rapid periodic redstone pulses alternating every 10 ticks (0.5 seconds on, 0.5 seconds off / 1.0 Hz clock frequency).
  - Retained the existing 1.0-second pulse option (`Redstone Pulse (1s)` / `Pls 1s`) for standard 20-tick period clocking.
  - Updated both `PipeGateBlockEntity` and `PipeGateEntity` to process `REDSTONE_PULSE_HALF_SEC` every tick via `(gameTime / 10) % 2 == 0`.
  - Appended `REDSTONE_PULSE_HALF_SEC` to enum values after existing entries, fully preserving ordinal backwards compatibility for existing world saves and network container data synchronization.
- **Natural Gate Action Cycling Order (`GateAction.CYCLE_ORDER`)**:
  - Grouped gate actions into a natural cycling sequence (`None` -> `Emit Redstone` -> `Redstone Pulse (1s)` -> `Redstone Pulse (0.5s)` -> `Autarchic Pulses` -> `Pipe Wires`) for intuitive forward/backward selection in `GateScreen`.
- **Field Manual Updates (`GuideBookRegistry`)**:
  - Updated the in-game Field Manual Guide Book Pipe Gates section to document both 1.0s and rapid 0.5s redstone pulsing options for high-frequency clocking and automation circuits.

---

# Industrial Refinery v1.11.37

### What's New in v1.11.37:
- **Configurable Heavy Pit Quarry Max Speed & Energy Rates (`config/refinery.toml`)**:
  - Added user-configurable options for the Heavy Pit Quarry in `config/refinery.toml`:
    - `quarryEnergyPerTick`: Max operational power draw (default: `240` FE/tick).
    - `quarryMaxMiningSpeed`: Base excavation speed in blocks per second (default: `1.5` blocks/sec).
  - Configurable strictly via configuration file (not changeable in-game) to preserve server integrity and pack author tuning.
  - Dynamically synchronizes configured server values to client terminal screen tooltips.
- **Excavation Speed & Energy Draw Tuning**:
  - Increased base excavation speed from 1.0 to **1.5 blocks/second** (~13.3 ticks per standard block).
  - Soft materials (dirt, sand, gravel, tuff) mine at 2x speed (**3.0 blocks/s**); hard ores (iron, diamond) mine at 0.5x speed (**0.75 blocks/s**).
  - Adjusted maximum speed consumption to **240 FE/tick**.

---

# Industrial Refinery v1.11.35

### What's New in v1.11.35:
- **Perimeter Safety Fence Preservation During Quarry Deep Mining**:
  - Fixed an issue where the quarry top-down obstruction scanner would re-mine and destroy the perimeter safety fence immediately after placing it in Phase 1.
  - In Phase 2 (`PHASE_MINING`), top obstruction monitoring is now strictly restricted to the inner excavation volume ($Y \le -1$), completely ceasing monitoring of the upper bench ($Y \ge 0$).
  - Added explicit exclusion for fences (`FenceBlock`, `BlockTags.FENCES`) and walls (`WallBlock`, `BlockTags.WALLS`), ensuring safety fences around the pit rim are permanently preserved.
- **Stone-to-Cobblestone Throw-Back Equivalence**:
  - Marking `Stone` as a Throw-Back block in the Geological Manifest or filter slot now automatically treats mined `Cobblestone` drops as throwbacks, ejecting them out the rear conveyor chute into the open pit void instead of filling the factory buffer.
  - Symmetrically synchronized `Cobblestone` $\leftrightarrow$ `Stone` and `Deepslate` $\leftrightarrow$ `Cobbled Deepslate` pairs for both individual manifest toggles, the `[SPOIL]` bulk toggle button, and Slot 10 filter checks.

---

# Industrial Refinery v1.11.33

### What's New in v1.11.33:
- **In-Game Field Manual Guide Book Entries (`GuideBookRegistry`)**:
  - **Heavy Pit Quarry**: Added full multiblock entry under Multiblocks category with 3-layer blueprint inspector (Layer 0, 1, 2), complete materials checklist (1 Controller + 26 Casings), crafting recipes, and operational instructions.
  - **Crude Oil Pump Jack**: Added guide entry under Machinery detailing 2x3x3 footprint, downhole 50-block reach, power scaling (1–4 FE/t), and crafting recipe.
  - **Bitumen Storage Pile**: Added guide entry under Logistics detailing 3x3 footprint, 8-stage visual heap growth, vacuum item ingestion, and harvesting mechanics.
  - **Fluid Pumps & In-World Discharge**: Added guide entry under Machinery detailing Coal, Diesel, and Electric fluid extraction pumps alongside the pressurized Fluid Discharge nozzle.
- **Refinery Wrench Blueprint Projection Support**:
  - Right-clicking the Heavy Pit Quarry Controller with a Refinery Wrench toggles the 3D blueprint hologram wireframe in-world with auditory button click feedback.
- **Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Composition**:
  - Simplified the multiblock requirements: now strictly requires **1 Heavy Pit Quarry Controller** and **26 Quarry Structural Casings**.
  - All 26 blueprint positions and missing-component checks require `Quarry Structural Casing`.
  - Removed confusing subcomponent names ("Quarry Left Conveyor Port", "Quarry Turntable Base", etc.) from requirement lists.
  - Removed `Quarry Lattice Frame` blocks from the mod.

---

# Industrial Refinery v1.11.20

### What's New in v1.11.20:
- **Refinery Heaters / Furnaces Single-Item Drop Fix (`BurnerRefineryFurnaceBlock`, `ElectricRefineryFurnaceBlock`)**:
  - Fixed an issue where destroying the 2-block-tall Burner Refinery Furnace or Electric Refinery Furnace dropped multiple items (one for each block it occupies).
  - Overrode `getDrops` in Java so only the master `LOWER` part returns `List.of(new ItemStack(this))` while the `UPPER` part returns empty drops.
  - Updated `playerWillDestroy` to silently remove partner blocks with flag 35 and only trigger a single item pop if a player in survival mines the non-master upper half, guaranteeing strictly 1 furnace item is returned in survival and 0 in creative.
- **Steam Turbine GUI Dark Gunmetal Redesign & Header Alignment (`SteamTurbineScreen`, `steam_turbine.png`)**:
  - Overhauled the Steam Turbine GUI to match the dark industrial gunmetal theme (#2B3036 base, #353B43 riveted sub-panels, #14171A recessed wells) of the other machinery (Refinery, Blast Furnace, Metal Press, Boilers).
  - Resolved header text collision where sub-headers ("STEAM", "WATER", "POWER") were drawn directly on top of the container title ("Steam Turbine Generator").
  - Placed sub-headers at `y: 18` inside distinct sub-panel housings, giving the top title bar at `y: 6` full clearance with zero text overlap.
  - Added a live digital telemetry monitor (Status: Online / Standby / Unformed, RPM readout, power generation rate), dynamic rotating blade activity indicator, and high-pressure fluid/energy tank fill gauges with custom tooltips.
  - Added multiblock structure assembly LED status indicator in the top-right header with diagnostic assembly tooltip.
- **Blast Furnace GUI Header Alignment (`BlastFurnaceScreen`)**:
  - Removed redundant section labels ("IRON", "FUEL", "STEEL") from `extractLabels` that were overlapping the "Industrial Blast Furnace" title at `y: 6`, keeping the title bar clean and unobstructed.

---

# Industrial Refinery v1.11.19

### What's New in v1.11.19:
- **Crude Pump Jack Multiblock (`CrudePumpJackBlock`, `CrudePumpJackPart`, `CrudePumpJackBlockEntity`, `CrudePumpJackModel`, `CrudePumpJackRenderer`)**:
  - Added the industrial **Crude Pump Jack (Beam Pumping Unit)**: a 2-wide x 3-long x 3-high (18-part) heavy multiblock oil extraction machine.
  - **Single-Item Placement & Break Safety**: Places the entire 2x3x3 assembly from a single inventory item after verifying clear placement clearance. Guarantees strictly 1 Crude Pump Jack item is dropped when any block in the multiblock is broken in survival, with zero duplicate drops.
  - **Kinematic Monolithic 3D Model**: Monolithic animated renderer featuring a structural H-beam skid base, Samson post A-frame, center saddle bearing, rocking walking beam ($\pm 12^\circ$), arc horsehead, wireline bridle, vertically reciprocating polished rod entering the wellhead stuffing box, dual counterweighted rotating crank wheels ($360^\circ$), and dynamic pitman connecting arms.
  - **Downhole Crude Oil Extraction**:
    - Automatically prospects a 10-block horizontal radius and up to 50 blocks vertically downward beneath the front wellhead for crude oil source blocks (`refinery:crude_oil`).
    - Prioritizes downhole vertical columns beneath the wellhead first before radially searching outward concentric rings.
  - **Power-Scalable Extraction Rates**:
    - 1 FE/t $\rightarrow$ 100 mB/min
    - 2 FE/t $\rightarrow$ 500 mB/min
    - 4 FE/t $\rightarrow$ 1,000 mB/min (full operational speed)
  - **Automated Pipe & Power Interfacing**:
    - Front wellhead acts as a fluid export port, automatically pushing extracted crude oil into connected pipes and tanks.
    - Rear motor and electrical control panel accepts energy cables (10,000 FE internal buffer).
    - Supports right-click empty bucket filling to directly extract crude oil buckets.
    - Right-click inspection displays current crude storage, energy stored, and active pumping speed in the chat bar.
- **Bitumen Storage Pile Alignment & Drop Fixes (`BitumenPileRenderer`, `BitumenPileBlock`, `loot_table/blocks/bitumen_pile.json`)**:
  - Fixed 3D model rotation and origin offsets across all 4 horizontal facings (North, South, West, East) so the 3x3 footprint aligns with placed blocks.
  - Enforced single-item drop safety via Java `getDrops` override and `minecraft:block_state_property` loot table conditions, preventing duplicate item drops when broken.
- **Refinery Controller GUI Label Scaling (`RefineryScreen`)**:
  - Rescaled the amber "BITUMEN" monitor text label down by 20% (0.8x) using JOML `Matrix3x2fStack` transformations for GUI alignment.

---

# Industrial Refinery v1.11.18

### What's New in v1.11.18:
- **Bitumen Storage Pile 3x3 Footprint Expansion (`BitumenPileBlock`, `BitumenPilePart`, `BitumenPileModel`, `BitumenPileRenderer`, `BitumenPileBlockEntity`)**:
  - Expanded the Bitumen Storage Pile footprint to an industrial **3x3 horizontal base** (18-part multiblock: 3x3x2) with extended retaining walls, corner pillars, and modular upright structural girders at 16px and 32px joints.
  - Generates continuous organic 24x24 heightfield mesh stages across the 48px x 48px ground area, mounding up smoothly to a towering peak under the 2-block height.
  - Expanded the hopper-style vacuum absorption bounding box to encompass the full 3x3x2.6 volume.
- **Refinery Controller GUI Monitor Label Rescaling (`RefineryScreen`)**:
  - Scaled down the amber "BITUMEN" monitor label by 20% (0.8x) using JOML `Matrix3x2fStack` (`pushMatrix`, `translate`, `scale`, `popMatrix`), nesting it cleanly inside the 35px byproduct panel monitor.
- **Bitumen Storage Pile Single-Item Drop Fix (`BitumenPileBlock`, `loot_table/blocks/bitumen_pile.json`)**:
  - Fixed an issue where destroying the Bitumen Pile in survival dropped multiple block items (one per sibling block).
  - Configured the loot table with `minecraft:block_state_property` constrained strictly to `part = bottom_front_left`.
  - Updated `playerWillDestroy` to remove all non-mined sibling blocks with silent flag 35 and route survival drops through a single destruction event, ensuring exactly 1 Bitumen Storage Pile item drops regardless of which block in the 3x3 is mined.

---

# Industrial Refinery v1.11.17

### What's New in v1.11.17:
- **Bitumen Storage Pile (`BitumenPileBlock`, `BitumenPilePart`, `BitumenPileBlockEntity`, `BitumenPileModel`, `BitumenPileRenderer`)**:
  - Implemented the 2x2x2 multiblock Bitumen Storage Pile with dynamic loose material heap physics and visual scaling:
    - **Dynamic Visual Growth**: Renders 9 continuous organic heightfield mesh stages (0 to 8) that dynamically mound up in real time from a floor dusting inside a reinforced steel retaining bunker to a towering 2-block-tall asphalt mountain as inventory fills (up to 3,456 items across 54 double-chest slots).
    - **Hopper-Style Vacuum Absorption**: Automatically sucks in bitumen items (`refinery:bitumen` and any tagged bitumen/asphalt items) dropped or falling onto or into the 2x2x2 pile bounds, accompanied by aggregate impact sounds and item particle splashes.
    - **Industrial Logistics Capabilities**: Exposes `WorldlyContainer` and NeoForge `ItemHandler` across all 8 multiblock blocks, allowing hoppers, pipes, conveyors, and direct player interactions to insert bitumen or open the double chest inventory interface.
    - **Photorealistic High-Resolution Textures**: Ultra-realistic 2048x2048 texture atlas with granular asphalt pebble aggregate, dark industrial riveted steel retaining plates, cast steel angle brackets, and compacted tar floor sheen, paired with a custom 32x32 inventory sprite.

---

# Industrial Refinery v1.11.16

### What's New in v1.11.16:
- **Multiblock Glass-Fused-to-Steel Storage Tank Flush Button (`StorageTankBlockEntity`, `StorageTankMenu`, `StorageTankScreen`)**:
  - Resolved an issue where clicking the "FLUSH" button in the multiblock storage tank GUI had no effect.
  - Linked the master `StorageTankBlockEntity` reference across both 4-argument and 5-argument `StorageTankMenu` constructors and `createMenu`, allowing server-side button click packets (`clickMenuButton(player, 0)`) to resolve the block entity.
  - Added `flushTank(@Nullable Player player)` on `StorageTankBlockEntity`: dumps all stored liquid, clears fluid locks, plays the emptying sound (`SoundEvents.BUCKET_EMPTY`), triggers a block update sync to all tracking clients, and sends a system notification displaying the exact volume of fluid flushed.
  - Updated `StorageTankScreen` hover, render, and click hitboxes to remain active whenever the tank holds liquid or has an active fluid type registered, and added client-side container data zeroing for immediate UI response.

---

# Industrial Refinery v1.11.15

### What's New in v1.11.15:
- **Blast Furnace GUI Bottom-Half Player Inventory Alignment (`BlastFurnaceMenu`, `BlastFurnaceScreen`)**:
  - Corrected player inventory slots in `BlastFurnaceMenu` from `y = 74` to `y = 84` and player hotbar from `y = 132` to `y = 142`, perfectly snapping the slot wells onto the dark gunmetal background texture.
  - Adjusted `inventoryLabelY` from `74` to `72` in `BlastFurnaceScreen`, resolving the text overlap where the "Inventory" header previously collided with the top row of items.
- **Metal Press RF Energy Meter Well Alignment (`MetalPressScreen`)**:
  - Corrected the horizontal rendering coordinate of the RF energy bar in `MetalPressScreen` from `xo + 18` to `xo + 14` (`width = 14`), perfectly nesting the yellow energy meter inside the recessed gauge well (`x: 13..28`).
  - Synchronized the energy meter tooltip hover detection bounds from `x: 18..32` to `x: 13..28`, eliminating the 4-pixel rightward shift and texture overflow.

---

# Industrial Refinery v1.11.13

### What's New in v1.11.13:
- **Conveyor Belt 90-Degree Corner & T-Intersection Adaptive Frame (`ConveyorBeltBlock`, `ConveyorBeltModel`, `ConveyorBeltRenderer`)**:
  - Automatically adapts standard horizontal conveyor belt side frames and rear end caps when conveyors meet perpendicularly at 90 degrees or form T-intersections:
    - **Side Frame Retraction**: When an incoming conveyor feeds into the side of a receiving conveyor (heading N/S into E/W or vice-versa), the side rail on that receiving side is automatically removed so items roll directly onto the belt without colliding with the frame.
    - **Rear Corner Guard Rail**: In 90-degree corner turns (where no conveyor feeds into the back), the removed frame piece is seamlessly relocated to the tail/back side of the receiving conveyor, creating a protective rear bumper that prevents turning items from slipping off the back.
    - **T-Intersection Support**: In T-junctions where a conveyor line continues from behind while a perpendicular conveyor merges into the side, the side rail is removed for the merge while the back remains completely open for uninterrupted passthrough.
    - **Zero Z-Fighting**: The back rail connects flush to the remaining side rail without overlapping faces, and mounting foot pads on the open incoming side are omitted for a completely flat junction.
    - **Backstop Item Retention (`ConveyorBeltBlockEntity`)**: Added physical backstop clamping at the rear rail to ensure turning items never glitch backwards during high-speed routing.

---

# Industrial Refinery v1.11.11

### What's New in v1.11.11:
- **Seamless Sloped-to-Flat Conveyor Transition & Collision Wall Elimination (`ConveyorBeltBlock`, `SlopedConveyorBeltBlockEntity`)**:
  - Resolved an issue where items reaching the top crest of a sloped conveyor belt would hit a vertical collision ledge, bounce into the air, or get permanently stuck at the transition to a horizontal flat conveyor.
  - Replaced restrictive directional voxel shapes (`ITEM_ENTRY_SHAPES`) in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` with `Shapes.empty()` for `ItemEntity`, eliminating the 5-pixel vertical block collision wall at conveyor seams while preserving full solid collision for players and mobs.
  - Implemented clean conveyor hand-off logic in `SlopedConveyorBeltBlockEntity`: once an item physically crosses the boundary into the next conveyor (`rawT >= 1.0`), ownership is relinquished immediately to prevent competing velocity and position conflicts.
  - Synchronized carrying surface heights across all conveyor types to `0.3125` (`5.0px`), leveled off vertical velocity by `rawT >= 0.82`, and added an exit momentum boost to ensure silky-smooth glide onto flat conveyors.

---

# Industrial Refinery v1.11.10

### What's New in v1.11.10:
- **Sloped Conveyor Belt Ground Support Legs & Foot Pads (`SlopedConveyorBeltModel` & `SlopedConveyorBeltRenderer`)**:
  - Dynamically extended support leg trestles downwards to reach the floor or solid ground when chaining sloped conveyors together in multi-block ramps or elevated inclines.
  - Automatically raycasts down up to 32 blocks beneath the elevated conveyor's support legs, detecting the exact top elevation of floor blocks, slabs, machine housings, or containers.
  - Renders continuous aluminum support leg columns from the inclined bed frame all the way down to the surface, complete with heavy bolted foot pads (`UV_FOOT_PAD`) resting on the floor.
  - Adds horizontal cross-bracing ties every 1-meter block interval down the entire height of the extended support legs.
  - Expanded `SlopedConveyorBeltRenderer.getRenderBoundingBox` downwards (`expandTowards(0.0, -32.0, 0.0)`) to ensure tall support legs are never frustum-culled.

---

# Industrial Refinery v1.11.9

### What's New in v1.11.9:
- **Refinery Tower South Takeoff Pipes Height Realignment (`RefineryTowerModel`)**:
  - Realigned all three South-facing horizontal takeoff pipe and valve assemblies to match the exact block center elevations of connected pipes and output ports:
    - Stage 1 (Liquid Tar, `TAR_PORT` at Layer 0): lowered from `Y = 22.0f` to `Y = 8.0f` (`0.5 * 16.0f`), extending from the central drain sump cone (`Z = 4.0f`) out to the South face (`Z = 18.5f .. 23.5f`).
    - Stage 2 (Diesel, `DIESEL_PORT` at Layer 3): lowered from `Y = 60.0f` to `Y = 56.0f` (`3.5 * 16.0f`), aligning dead-center with the Diesel Output Port.
    - Stage 3 (Gasoline, `GAS_PORT` at Layer 6): lowered from `Y = 115.0f` to `Y = 104.0f` (`6.5 * 16.0f`), aligning dead-center with the Gasoline Output Port.
- **Electric Arc Melter Hazard Stripe Texture Removal (`ArcMelterModel`)**:
  - Removed yellow and black diagonal hazard stripe overlays from the Electric Arc Melter model, restoring authentic dark industrial metallurgy textures across all highlighted zones:
    - Overhead raw ore charge hopper top intake flange: replaced with heavy steel `UV_CHARGE_HOPPER`.
    - Foundation perimeter toe-kick strip: replaced with cast machine base `UV_TRUNNION_BASE`.
    - Front and East tapping spout flange collars: replaced with ribbed refractory furnace shell `UV_FURNACE_SHELL`.

---

# Industrial Refinery v1.11.8

### What's New in v1.11.8:
- **Refinery Output Port Fluid Auto-Push to Connected Pipes & Tanks (`RefineryPortBlockEntity`)**:
  - Resolved an issue where distillation output liquids (Liquid Tar, Diesel, Gasoline) would not exit the Petroleum Refinery multiblock into connected fluid pipes or fluid storage containers.
  - Implemented active fluid pushing in `RefineryPortBlockEntity.serverTick`: every tick, fluid output ports (`TAR_OUT`, `DIESEL_OUT`, `GAS_OUT`, and `BYPRODUCT_OUT`) inspect all adjacent faces (`Direction.values()`) for connected NeoForge fluid handlers (`Capabilities.Fluid.BLOCK`, including `IndustrialPipeBlockEntity`, tanks, and fluid containers) and automatically push up to 1,000 mB/t of matching output fluid directly into the connected network using transaction-safe transfers.
  - Enhanced controller lookup with fallback via `ReinforcedCasingBlock.findNearbyController(...)` and automatic multiblock re-assembly synchronization (`syncAssembly()`), ensuring ports stay firmly connected to the tower even after block updates or chunk reloads.
- **Refinery Tower Stage 1 Liquid Tar South Takeoff Pipe Assembly (`RefineryTowerModel`)**:
  - Added a third South-facing horizontal takeoff valve assembly at `Y = 22.0f` on the bottom section (Stage 1: Heavy Residue / Liquid Tar) of the 3×9×3 Petroleum Refinery Tower.
  - Symmetrically matches the rotated Stage 2 (`Y = 60.0f`, Diesel) and Stage 3 (`Y = 115.0f`, Gasoline) takeoff valve assemblies, projecting cleanly towards the South (+Z) face with a crimson indicator handwheel when Stage 1 is active (`UV_STAGE1_CRIMSON`), completing the 3-stage visual takeoff system.
- **Electric Arc Melter & Hydraulic Casting Press Texture Flickering & Z-Fighting Resolution (`ArcMelterModel` & `CastingPressModel`)**:
  - **Atlas UV Padding Inset**: Upgraded `UVRect.of` margin inset padding from `0.15px` to a robust `0.5px` (`(coord + 0.5f) / 256.0f`), completely eliminating texture bleeding, shimmering, and edge flickering across adjacent atlas tiles during camera movement and mipmapping.
  - **Ground Floor Z-Fighting Elimination**: Suppressed emitting downward-facing (`DOWN`) quads for foundation cuboids sitting on the ground (`y0 <= 0.01f`), preventing bottom runner faces from fighting with the top surface of floor blocks.
  - **Foundation Cross-Beam & Deck Plate Disjoint Planes**: Adjusted cross-beam heights in both `ArcMelterModel` (`y1 = 3.0f`) and `CastingPressModel` (`y1 = 3.5f`) so the heavy foundation deck plates sit directly on top of the beams without sharing identical coplanar top surfaces.
  - **Flange Ring & Platen Intersections**: Offset water-cooling girth flange rings in `ArcMelterModel` (`8.05f .. 9.95f` and `28.05f .. 29.95f`) and trimmed column locknuts in `CastingPressModel` (`39.5f .. 41.5f`) to prevent penetrating cuboids and edge collisions.

---

# Industrial Refinery v1.11.6

### What's New in v1.11.6:
- **Refinery Byproduct Port Automatic Conveyor & Pipe Ejection (`RefineryPortBlockEntity`)**:
  - Implemented automatic solid byproduct ejection for the Byproduct Port (`refinery:byproduct_port`) on the 3-Stage Petroleum Refinery multiblock tower.
  - Automatically detects item conveyors (`refinery:conveyor_belt`, `sloped_conveyor_belt`, `right_angle_conveyor_belt`, and third-party conveyors) and item pipes (`IndustrialPipeBlockEntity`, hoppers, chests, barrels, crates, and NeoForge item capability handlers) attached **below** (`pos.below()`) or **beside** the Byproduct Port (`pos.relative(NORTH/SOUTH/EAST/WEST)` and 1-block below).
  - Every 4 ticks, the port scans for adjacent conveyance channels and smoothly ejects solid byproducts (Bitumen, Tar, and extracted sand) from the controller's internal byproduct slots directly onto the conveyor belt surface or into connected pipes/containers.
  - Integrated anti-clogging belt occupancy checks, preventing item stacking or clumping by waiting until previously ejected items advance along the belt.
  - Implemented `WorldlyContainer` directly on `RefineryPortBlockEntity` and updated item capability registration to allow multi-directional extraction through all horizontal and bottom faces of Byproduct Ports.

---

# Industrial Refinery v1.11.4

### What's New in v1.11.4:
- **Refinery Tower South Takeoff Pipes Re-orientation & Vertical Header Removal**:
  - Removed the tall vertical grey collection header pipe on the East (+X) side of the 3×9×3 Petroleum Refinery Tower.
  - Rotated the Stage 2 (Diesel, orange section at `Y = 60.0f`) and Stage 3 (Gasoline, green section at `Y = 115.0f`) horizontal takeoff valve assemblies by 90 degrees around the column body. Both stubby takeoff pipes now project cleanly towards the **South (+Z)** face (`Z: 15.1f .. 23.5f`), directly aligning with the South-facing Diesel Output Port (`(0, 3, 2)`) and Gas Output Port (`(0, 6, 2)`).
  - Cleared the redundant lower East bracket assembly at Stage 1, leaving the East side of the tower completely unencumbered for furnace placement.
```
  - The Refinery Tower requires a heat source (>= 350°C) placed adjacent to Stage 1. Players can choose between:
    - **Burner Refinery Furnace (`refinery:burner_refinery_furnace`)**: Burns solid fuels and fuel buckets at 4x rapid burn speed across 3 fuel slots.
    - **Electric Refinery Furnace (`refinery:electric_refinery_furnace`)**: Consumes 500 FE/t from a 500,000 FE buffer to generate up to 500°C of clean electric heat.
  - Each furnace is placed from a single item as a 2-block-tall (1x2x1) unit with a glowing internal serpentine crude-oil heating coil and drops exactly 1 item when broken.
- **Liquid Tar Fluid, Bucket & Port**:
  - Added `refinery:liquid_tar`, `refinery:liquid_tar_bucket`, and `refinery:tar_port`, plus shapeless crafting of 1 Liquid Tar Bucket into 4 Petroleum Tar items.
- **Zero-Flicker Monolithic 3D Models (`RefineryTowerModel` & `RefineryFurnaceModel`)**:
  - Built with a 16-sided polygonal cylinder/frustum emitter (`emitCylinder` & `emitFlangeRing`), strictly non-overlapping vertical planes, and `0.5px` UV inset margins to eliminate Z-fighting and texture flickering.

---

# Industrial Refinery v1.10.25

### What's New in v1.10.15:
- **Metal Press Tooling Dies**:
  - Added 4 dedicated tooling dies: Plate Die (`refinery:plate_die`), Gear Die (`refinery:gear_die`), Corrugated Steel Die (`refinery:corrugated_die`), and Rail Die (`refinery:rail_die`).
  - Stamping in the Hydraulic Metal Press strictly requires an installed Die inserted into the dedicated tooling slot.
  - Upgraded the Metal Press GUI with a beveled Die slot beneath the ram, an empty-slot guide tooltip, and ram hover tooltips showing the installed tooling.
- **Machinery Gears & Stamping**:
  - Registered 6 industrial gears: Steel Gear (`refinery:steel_gear`), Iron Gear (`refinery:iron_gear`), Copper Gear (`refinery:copper_gear`), Bronze Gear (`refinery:bronze_gear`), Gold Gear (`refinery:gold_gear`), and Tin Gear (`refinery:tin_gear`).
  - Added shaped crafting recipes (4 ingots in a cross) and common tags (`#c:gears` and `#c:gears/<metal>`).
  - Stamping with the Gear Die produces gears at 4x material efficiency (1 ingot or plate -> 1 gear).
- **Corrugated Steel Block & Rail Stamping**:
  - Stamping with the Corrugated Steel Die yields 4 Corrugated Steel Panels per Steel Ingot/Plate (and 2 per Iron Ingot/Plate).
  - Stamping with the Rail Die yields 4 Rails per Steel Ingot/Plate, 2 Rails per Iron Ingot/Plate, 2 Powered Rails per Gold Ingot/Plate, and 2 Activator Rails per Copper Ingot/Plate.
- **In-Game Guide Book Documentation**:
  - Updated the Hydraulic Metal Press chapter with full die tooling instructions and all 4 stamping recipe types.

---

# Industrial Refinery v1.8.40

### What's New in v1.8.40:
- **Industrial Diesel Generator (1x2x2 Compact Power Plant)**:
  - Added the **Industrial Diesel Generator** (`refinery:diesel_generator`): a heavy-duty containerized sound-attenuated diesel power plant producing FE/RF from refined Diesel.
  - **Compact 2-Block Footprint (Non-Multiblock)**: Placed as a single block item that automatically forms a 2-block wide footprint ($1\times 2\times 2$ max boundary) with Left (Master) and Right (Slave) sections. Breaking either cleanly drops 1 generator item and drops internal inventory contents.
  - **3D Monolithic Model & POM Shading**: Modeled after heavy commercial generator skids (Triton / CAT style): heavy black C-channel steel skid with forklift tine pockets and yellow tie-down tags, off-white powder-coated acoustic weather canopy (`#E4E2DA`) with beveled drip-edge roof, digital operator control panel with glowing cyan LCD telemetry readout and red mushroom E-Stop button, bold black TRITON power logo, Blue Tech ECO Products badge, Tier 4 Final ribbon, access doors with black hinges, handles, and louvers, rear high-voltage connection box with 480V danger flash decal, and top exhaust muffler stack with dynamic animated rain-cap flapper.
  - **Power Generation & Fluid Telemetry**: Generates **256 FE/t** of continuous electrical energy. Internal 8,000 mB diesel tank and 500,000 FE buffer. Consumes 1 mB diesel every 4 ticks (1,000 FE/mB; 1 bucket provides 200s / 4,000 ticks of full power).
  - **Auto-Push Energy Output**: Automatically scans all adjacent faces across both blocks to push up to **1,024 FE/t** into connected cables, machines, or energy grids.
  - **Smart Idling**: Shuts off fuel consumption when the internal capacitor is full, preventing fuel waste.
  - **Configurable Redstone Modes**: Always On, Active High (redstone signal required), or Active Low (emergency backup generator mode).
  - **Interactive Telemetry GUI**: Telemetry display with animated diesel level, energy buffer bar, digital status readout, and a one-click redstone control toggle button.

---

# Industrial Refinery v1.8.39

### What's New in v1.8.39:
- **Tin Ore, Deepslate Tin Ore & Raw Tin Generation**:
  - Re-introduced subterranean **Tin Ore** and **Deepslate Tin Ore** naturally generating across all Overworld biomes.
  - Generates with copper rarity and distribution: 16 veins per chunk, triangle distribution between $Y = -16$ and $Y = 112$ (peak at $Y = 48$), vein size 10.
  - Added retroactive chunk generation (`TinRetrogenHandler`): automatically populates newly loaded chunks in pre-existing worlds with tin veins without requiring a new world.
  - Added **Raw Tin** drop item with Fortune and Silk Touch support, plus **Block of Raw Tin** (9x storage).
  - Added **Block of Tin** and **Block of Bronze** for full storage block compression/decompression.
  - Full Minecraft 26.3 item definition architecture (`assets/refinery/items/<id>.json`), blockstates, models, textures, and common tag registrations (`#c:ores/tin`, `#c:raw_materials/tin`, `#c:storage_blocks/*`).
- **Bronze 1:1 Forging Ratio**:
  - Updated shapeless alloy crafting recipe: **1 Copper Ingot + 1 Tin Ingot $\rightarrow$ 2 Bronze Ingots** (and **1 Copper Dust + 1 Tin Dust $\rightarrow$ 2 Bronze Dust**).
  - Compatible with common tag ingots and dusts (`#c:ingots/copper`, `#c:ingots/tin`, `#c:dusts/copper`, `#c:dusts/tin`).
- **Metallurgy & Processing Integration**:
  - **Electric Arc Melter**: Melts Raw Tin, Tin Ingot, Tin Ore, Deepslate Tin Ore, and Bronze Ingot into Molten Tin and Molten Bronze.
  - **Casting Press**: Casts Molten Tin into Tin Ingots and Molten Bronze into Bronze Ingots.
  - **Metal Press**: Forges Tin Ingot $\rightarrow$ Tin Plate and Bronze Ingot $\rightarrow$ Bronze Plate.
  - **Industrial Grinder**: Crushes Tin Ore, Deepslate Tin Ore, and Raw Tin into 2x Tin Dust.
  - Smelting and blasting recipes for Raw Tin, Tin Ore, and Deepslate Tin Ore into Tin Ingots.

---

# Industrial Refinery v1.8.38

### What's New in v1.8.38:
- **Pipe State & 3D Elbow Connection Retention on Server Restart**:
  - Fixed a critical deserialization bug in `IndustrialPipeBlockEntity`: the `BlockEntityType` factory constructor `IndustrialPipeBlockEntity(BlockPos pos, BlockState state)` previously hardcoded `IndustrialPipeType.FLUID` and `IndustrialPipeMaterial.STEEL`.
  - When chunks reloaded from disk on server restart or transmitted over the network to the client, all item pipes (such as Copper, Iron, Steel, Wood, Gold, Diamond, Emerald) were mistakenly instantiated as fluid pipes.
  - When connection checks subsequently ran, `pipeType.handlesItems()` evaluated to `false`, causing pipes to sever their connections to item machines (conveyor belts, hoppers, chests, multiblock ports) and visually revert from 3D corner elbows to straight, dead-end capped runs.
  - Updated the 2-argument constructor and getters to dynamically resolve `pipeType` and `material` directly from `state.getBlock()`, ensuring pipes retain their exact type, material, fluid capacity, and item behavior across chunk loads and server restarts.
  - Guarded against block entity initialization races during world startup: if a neighbor block possesses a block entity (`targetState.hasBlockEntity()`) that has not finished instantiating (`level.getBlockEntity(targetPos) == null`), existing saved connections are safely preserved.
  - Added explicit `PipeType` and `Material` NBT tags in `saveAdditional()` for robust save-game persistence.

---

# Industrial Refinery v1.8.37

### What's New in v1.8.37:
- **Pipe Texture Cleanup & Markings Removal**:
  - Removed circular flange and bolt emblem markings that were previously baked into the top-left region of the pipe texture generator (`PipeAssetGenerator.java`).
  - Regenerated all 12 pipe textures (`steel`, `iron`, `copper`, `wood`, `gold`, `diamond`, `emerald`, `brass`, `cast_iron`, `void`, `quartz`) to have clean, seamless, uninterrupted cylindrical metal shading.
- **True 1/8-Diameter Clear Sight-Glass Inspection Strips**:
  - Engineered dedicated longitudinal apertures on all 4 cardinal faces of pipe cylinders of width exactly $\frac{1}{8}$ of the pipe diameter ($0.050$ blocks wide).
  - Metal walls now terminate cleanly at the strip borders, allowing an unobstructed line of sight straight into the hollow pipe interior without opaque depth buffer occlusion.
  - Across the apertures, translucent sight-glass quads with metallic bevel edge frames (`pipe_window.png`) render flush with the pipe surface with zero z-fighting and no protruding ridges.
  - Live traveling items and colored fluid levels are now 100% visible moving and flowing through the pipe from all viewing angles.

---

# Industrial Refinery v1.8.36

### What's New in v1.8.36:
- **Pipe Connection Persistence Across Server Restarts**:
  - Fixed an issue where item pipes lost their connections to machines (chests, barrels, hoppers, furnaces, multiblock ports, and machines) upon restarting the server or reloading chunks.
  - Guarded `updateConnections()` against chunk loading races: if an adjacent block's chunk is not yet loaded (`!level.isLoaded(targetPos)`), the existing connection loaded from NBT is preserved rather than prematurely wiped to `NONE`.
  - Added multi-layered machine and container capability resolution (`isItemMachine` / `isFluidMachine`): pipes check directional side capabilities (`opposite`), unsided capabilities (`null`), `Container` / `WorldlyContainer` instances, and known multiblock member/port blocks. Multiblock machine ports undergoing structure assembly or delayed controller linkage no longer cause connected pipes to disconnect.
  - Added robust fallback wrappers (`VanillaContainerWrapper` and `WorldlyContainerWrapper`) during item dispatch insertion and active machine extraction in `tickItems()`, as well as `null`-side capability query in `getGoldInsertionDirection()`, guaranteeing seamless item transfer to all vanilla and modded containers.
- **Pipe Placement & Removal Neighbor Connection Snapping**:
  - Fixed an issue where breaking a pipe left adjacent pipes in a stale connection state, preventing newly placed adjacent pipes from connecting and forcing players to replace the whole pipe line.
  - Implemented `affectNeighborsAfterRemoval` on `IndustrialPipeBlock` to immediately notify and update connections on all adjacent pipes when a pipe is broken or deleted.
  - Implemented `setPlacedBy` and scheduled placement ticks (`level.scheduleTick(pos, this, 1)`) on `IndustrialPipeBlock` so newly placed pipes immediately update themselves and trigger connection updates on all neighboring pipes and machines.
  - Corrected the periodic pipe refresh schedule from `pos.asLong() & 19L` to `Math.floorMod(pos.asLong(), 20L)`, ensuring pipe connection checks are evenly and reliably evaluated across all 20 game ticks per second.

---

# Industrial Refinery v1.8.35

### What's New in v1.8.35:
- **Multiblock Storage Tank Auto-Export & Pipe Connectivity**:
  - Fixed an issue where multiblock liquid storage tanks failed to auto-export fluids to connected pipes even though fluid reception via the top fill valve functioned properly.
  - Resolved an internal back-drain loop on the drain valve (`STORAGE_TANK_DRAIN_VALVE_BE`): replaced the bidirectional handler with an extract-only handler (`getExportOnlyHandler()`) that rejects fluid insertion (`isValid = false`, `insert = 0`). Connected pipes receiving pushed fluid can no longer immediately pump the fluid backward into the tank, allowing fluid to freely flow downstream.
  - Registered `Capabilities.Fluid.BLOCK` on all multiblock tank casing blocks (`STORAGE_TANK_CASING`), delegating fluid extraction to the assembled tank controller.
  - Updated `IndustrialPipeBlockEntity` connection detection to recognize block-level capabilities on blocks without block entities (such as `StorageTankCasingBlock`), allowing fluid pipes to cleanly connect with visible 3D coupling arms to any casing face around the tank.
  - Expanded `StorageTankBlockEntity.pushFluidToNeighbors()` from checking solely beneath the bottom center drain valve to scanning all exterior adjacent block positions around the entire tank floor and perimeter walls, caching valid fluid handlers and pushing up to 1,000 mB/t to any connected pipe network.
  - Added instantaneous connection refresh on `StorageTankCasingBlock` and `StorageTankDrainValveBlock` neighbor changes so newly placed pipes immediately receive exported fluid without delay.

---

# Industrial Refinery v1.8.34

### What's New in v1.8.34:
- **Boiler Redstone Shutdown & Meltdown Prevention**:
  - Fixed a critical safety bug where solid fuel boilers (`SmallBoilerControllerBlockEntity`, `LargeBoilerControllerBlockEntity`) failed to shut down when their redstone signal was turned off, leading to unchecked overheating and catastrophic explosions when water was depleted.
  - Active boiling and steam generation now strictly require an active redstone signal in addition to burning fuel. When redstone power is removed, active boiling ceases immediately, halting water consumption and steam production.
  - Boilers without redstone power or fuel automatically enter idle cooldown: firebox flames extinguish, active blockstates disable, and any accumulated overheat ticks steadily dissipate.
  - Diesel boilers (`SmallDieselBoilerControllerBlockEntity`, `LargeDieselBoilerControllerBlockEntity`) feature accelerated cooldown dissipation and complete firebox flame deactivation when unpowered.
  - Enhanced multiblock redstone connectivity (`hasRedstone()`): all boilers now check for redstone power across the controller block as well as all multiblock structure blocks and ports (water input, fuel input, steam output), enabling full remote control from anywhere on the structure.
  - Synchronized boiler GUI status, hearth flame viewports, and redstone indicator lamps with real-time machine power state.

---

# Industrial Refinery v1.8.33

### What's New in v1.8.33:
- **Continuous 3D Surface-Mounted Pipe Wires**:
  - Replaced the floating 2D wire item icons with continuous 3D surface-mounted wire conduits in `PipeWireEntityRenderer`.
  - When pipe wires (Red, Blue, Green, Yellow) are attached to adjacent pipes in a network, the wires now dynamically trace along the outer cylinder of the pipes and connect seamlessly across pipe block boundaries without gaps.
  - Dedicated 4-Quadrant Conduit Geometry: Each of the 4 wire colors occupies a dedicated diagonal quadrant along the pipe's outer cylinder, allowing all 4 wire lines (Red, Blue, Green, Yellow) to run parallel along the same pipe simultaneously without colliding with each other or occluding gate faces and sight glass strips.
  - 3D Wire Hub Collars & Corner Routing: Smoothly connects multi-way branch intersections, right-angle pipe corners, and attached gate terminal bases.
  - Signal Illumination: Active wire signals emit vibrant full-bright glowing neon light (`0x00F000F0`).

# Industrial Refinery v1.8.32

### What's New in v1.8.32:
- **Sloped Conveyor Belt Item Ascent & Collision Resolution**:
  - Fixed an issue where items traveling on conveyor belts got permanently stuck at the bottom of sloped conveyors (`refinery:sloped_conveyor_belt`).
  - Replaced the rigid $16\times 16\times 16$ solid cube collision shape in `SlopedConveyorBeltBlock` with directional stepped collision geometry for players/mobs and `Shapes.empty()` for `ItemEntity`, allowing items to freely enter and glide up the slope without hitting vertical block collision walls.
  - Locked item vertical positioning directly to the inclined surface height (`targetY = yEntry + t * 1.0`), preventing gravity from pulling items downward beneath the conveyor.
  - Implemented 45-degree upward velocity matching the belt speed so client-side interpolation renders smooth 60fps item transit.
  - Updated `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity` boundary checks to recognize `SlopedConveyorBeltBlock` (both directly adjacent and stepped below) so items are handed off smoothly into slopes instead of attempting container insertion.

# Industrial Refinery v1.8.31

### What's New in v1.8.31:
- **Industrial Grinder Hopper Bounding Box Containment**:
  - Tightly bounded the top feed hopper collection box (`hopperBox`) in `IndustrialGrinderControllerBlockEntity` to strictly cover the open funnel mouth ($Y \in [2.20, 3.60]$ above base, horizontal radius $0.40$ blocks centered at $(0, 2, 1)$).
  - Eliminated the oversized bounding volume inflation that previously allowed the input hopper to reach downward into $Y=1..0$ and rightward into $X=1$, which sucked items right back off the output hopper, output chute, and discharge conveyor.
  - Cleaned up export targets to remove redundant controller block positions, ensuring auto-export exclusively feeds from the output port and right-hand discharge chute.
- **Output Conveyor Downstream Connection Validation**:
  - The Industrial Grinder now verifies downstream connectivity before discharging products onto an adjacent conveyor belt (`ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`).
  - Output items will only export onto an adjacent conveyor if that conveyor is actively connected to another conveyor, an item pipe (`IndustrialPipeBlock` / `IndustrialPipeBlockEntity`), or a receiving machine/container. If a conveyor line terminates in open air without a recipient, items remain safely stored inside the grinder's internal output slots.
  - Added belt vacancy detection: the grinder emits 1 item at a time onto the receiving conveyor belt only when the receiving belt section is clear, preventing items from stacking up in excessive entity clusters.

# Industrial Refinery v1.8.30

### What's New in v1.8.30:
- **Fluid Pump Back-Draining & Pipe Leak Resolution**:
  - The pumped fluid output reservoir across all pump variants (`Electric`, `Diesel`, `Coal`) is now strictly extract-only (`insert = 0`). Pipes can pull fluid out, but can never push fluid backward into the pump.
  - Eliminated the continuous push-and-drain loop with fluid pipes that previously caused the Electric Fluid Pump to burn 400 FE every second even when the pipe network was full.
  - Decremented `pumpCooldown` cleanly every tick regardless of buffer capacity, ensuring the pump idles in `[TANK FULL]` with zero energy consumption when the output network is backed up.
  - Overrode `onEnergyChanged()` in `ElectricFluidPumpBlockEntity.energyStorage` so the internal buffer syncs immediately upon receiving energy from power cables, and increased `maxInsert` to 1,000 FE/t.
  - Fixed a `ClassCastException` in `DieselFluidPumpBlockEntity.getFluidHandler` and added a dedicated `DieselFuelHandler` supporting pipe insertion of Diesel, Gasoline, and Ethanol fuels into the engine tank.

# Industrial Refinery v1.8.29

### What's New in v1.8.29:
- **Automated Planter Sapling Spacing**:
  - The Automated Planter now spaces out tree saplings so no sapling is placed directly adjacent (orthogonally or diagonally) to another sapling or tree trunk.
  - Crops (wheat, potatoes, carrots, beetroots, melon/pumpkin stems) continue dense planting across farmland.
- **Pipe Gate Placement Snapping on Occupied Faces**:
  - Clicking on a pipe face that is already occupied by a gate automatically snaps to the closest unoccupied radial side facing the player.

# Industrial Refinery v1.8.28

### What's New in v1.8.28:
- **Pipe Sight-Glass Inspection Strips & Flow Visualization**:
  - Integrated longitudinal sight-glass inspection strips along pipe lengths.
  - Live traveling items and fluid levels are clearly visible flowing through the pipes in real-time.

# Industrial Refinery v1.8.27

### What's New in v1.8.27:
- **Pipe Gate 3D Saddle Orientation & Smart Radial Face Snapping**:
  - Fixed `PipeGateEntityRenderer` rotations for `WEST` (`270°`), `EAST` (`90°`), `UP` (`270°`), and `DOWN` (`90°`) so the front gate face (`+Z`) always points outward and the concave saddle (`-Z`) always wraps inward around the pipe on all 6 faces.
  - Unified local-X / local-Y pipe axis mapping so gates on vertical pipes, horizontal pipes, and T-junctions always curve perpendicular to the cylinder axis.
  - Added smart radial face snapping (`PipeGateItem.resolveBestPipeFace`) and live tick auto-correction so clicking a pipe's flat end-cap or connected arm snaps the gate onto the nearest open radial side of the pipe cylinder.

# Industrial Refinery v1.8.26

### What's New in v1.8.26:
- **Diamond Pipe 3D Color-Coded Connection Bands & Whitelist GUI Legend**:
  - Added 3D color-coded collar bands around the ends of every connected arm on Diamond Pipes (`DOWN` = Green, `UP` = Yellow, `NORTH` = Blue, `SOUTH` = Red, `WEST` = Purple, `EAST` = White) that correspond directly with the 6-row whitelist sorting GUI.
  - Upgraded the Diamond Pipe whitelist GUI (`DiamondPipeScreen`) with bold color-coded row bands, slot row tinting, side direction tabs (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`), and hover tooltips.

# Industrial Refinery v1.8.25

### What's New in v1.8.25:
- **Minecraft 26.3 GUI Mouse Button Fix (`InputConstants.MOUSE_BUTTON_LEFT = 1`)**:
  - Fixed unresponsive GUI buttons across the Auto-Harvester, Auto-Planter, Fluid Pumps (Coal, Diesel, Electric), Storage Tank, and Pipe Logic Gates.
  - Updated `mouseClicked` handlers to check Minecraft 26.3's 1-indexed mouse button constants (`MOUSE_BUTTON_LEFT = 1`, `MOUSE_BUTTON_RIGHT = 3`).
- **GUI Status Text Alpha Visibility & Texture Cleanup**:
  - Added opaque alpha (`0xFF000000`) to all 24-bit RGB text colors across all machine screens and Gate trigger/action labels so status text renders visibly in Minecraft 26.3.
  - Cleaned stray background artifact behind the radius `[+]` button in `auto_harvester.png` and `auto_planter.png`.

# Industrial Refinery v1.8.24

### What's New in v1.8.24:
- **Auto Harvester Client GUI Synchronization**:
  - Fully merged and stabilized PR #1 (`GUI-Fix`), ensuring instantaneous client-to-server updates when toggling power or clicking radius buttons in the Auto Harvester menu.
  - Eliminated duplicate packet/tag registration methods in `AutoHarvesterBlockEntity` to ensure clean build compilation.
- **Pipe Gate Rotation & Saddle Alignment**:
  - Corrected `PipeGateEntityRenderer` rotational mappings for `WEST` (90°), `EAST` (270°), `UP` (90°), and `DOWN` (270°) faces.
  - Curved gate clamp plates wrap flush and securely around 12-sided pipe cylinders without bending backwards into open air.
- **Native Industrial 3D Monolithic Pipe Infrastructure (`refinery` namespace)**:
  - Built-in, fully autonomous native industrial pipe infrastructure directly integrated into Industrial Refinery, giving complete control over physical geometry, rendering, and logic without external mod dependencies.
  - **100% Round & Perfectly Seamless Geometry**:
    - 12-sided faceted cylindrical geometry spanning normalized radius 0.20 (3.2 units).
    - Straight runs across 10, 20, 50, or more blocks render as a single continuous, unbroken cylinder from coordinate 0.0 to 1.0 with mathematical boundary vertex alignment and zero visual joint lines or collar seams.
    - Central multi-directional junction hubs dynamically form smooth 90° elbows, tees, and 4-way/6-way crosses with matching radial curvature.
    - Bolted industrial flanges (Fitting 11) with 6 hex studs render strictly at machine, tank, pump, boiler, and container connection terminals.
  - **Comprehensive Material Suite**:
    - Industrial Steel Fluid Pipe & Item Pipe (1,000 mB / 8 items/t).
    - High-Capacity Copper Fluid Pipe (2,000 mB / fast fluid transfer).
    - Heavy Cast Iron Fluid Pipe (1,500 mB / resilient high-pressure fluid transfer).
    - High-Speed Brass Item Pipe (16 items/t / high-throughput transport).
    - Active Fluid Extractor Pipe & Active Item Extractor Pipe with automated directional machine extraction.
    - Industrial Void Pipe (infinite sink for surplus fluids and items).
  - **Live In-World 3D Item Transport Rendering**:
    - Floating 3D item packets travel in real time along the central pipe axis with dynamic orientation, smooth progress interpolation, and gentle rotation.
  - **Interactive Pipe Wrench Control**:
    - Right-click pipe faces with the Refinery Wrench to manually toggle side connections open or disconnected.
  - **Seamless Native Interoperability**:
    - Full bidirectional compatibility with Industrial Refinery Multiblock Liquid Storage Tanks, Boilers, Pumps, Multismelters, Metal Presses, Grinders, and Pipe Gates.
  - **Solid Monolithic Fitting Hub & Visual Refinements**:
    - Replaced legacy corner quad patches with a solid 12-sided spherical fitting hub, eliminating floating square artifacts at tees and elbows.
    - Fixed cylinder vertex winding and outward normal orientation, closing missing faces on 90° pipe elbows.
    - Single standalone pipe blocks render as complete solid cylindrical segments with closed end caps.

### Previous v1.8.11 Highlights:
- **True Round 3D Monolithic Pipe Geometry (`Logistics Automation`)**:
  - Replaced legacy cubic boxes with true 12-sided faceted cylindrical pipe geometry (`PipeGeometry.java`) matching the monolithic 3D machine aesthetic.
  - **Seamless Straight Runs**: Pipes connecting to each other now share continuous cylindrical profiles (radius 3.2 units) with zero collar breaks, zero block-boundary seams, and zero dark holes. Consecutive pipes merge into a smooth continuous industrial pipeline.
  - **Machine Connection Flanges**: Connections to external inventories, machines, tanks, and boilers (`ARM_EXTENDED`) feature authentic heavy bolted connection flanges (Fitting 11 Flange) with raised collar rims, machined hex bolts, and recessed gasket seals.
  - **Multi-Axial Junction Core (`CORE`)**: Central junction seamlessly connects straight runs, smooth 90° elbows, and tee branches without square corners or protruding box faces.
  - **Seamless Textures & LabPBR 1.3 Updates**: Longitudinal brushed metal texture wrapping around the cylindrical surface with physically accurate diffuse and specular cylindrical lighting. Intelligent logistics pipes feature flush illuminated status LEDs.
  - **Automated Baking & Mod Jar Deployment**: Fully integrated into `scripts/generate_pipe_texture_pack.ps1` with Java 25 compilation directly updating the mod jar and resource pack.

### Previous v1.8.10 Highlights:
- **Logistics Automation Ultra-Realistic 3D Monolithic Pipe Pack**:
  - Full high-definition visual upgrade pack for the Logistics Automation (`logistics`) pipe network.
  - **Photorealistic 64x64 PBR Textures**: Replaces legacy textures with brushed anisotropic metals, ambient occlusion depth, hex socket screws, and crystalline viewports preserving live item/fluid transit visibility.
  - **LabPBR 1.3 Shader Standard**: Normal displacement maps (`_n.png`) and specular/roughness maps (`_s.png`) provide real-time 3D relief, metal reflections, and illuminated glows with modern shader packs.

### Previous v1.8.9 Highlights:
- **In-Place Conveyor Belt Rotation**:
  - Rotate any standard conveyor belt in-place without breaking it: right-click with a Wrench rotates clockwise, sneak-right-click with a Wrench rotates counter-clockwise, and sneak-right-click with an empty hand rotates clockwise.
  - Connected mesh geometry dynamically updates instantly with neighboring conveyors upon rotation.
- **Sloped Conveyor Belt (`refinery:sloped_conveyor_belt`)**:
  - Added sloped conveyor belts for moving items up or down 1-block elevations.
  - Continuous height-interpolating physics elevates or lowers items smoothly along the incline while centering items.
  - Seamlessly transfers items onto adjacent conveyors, inventories, and machines at the top and bottom ends.
  - Implements `Container` and NeoForge `Item` capabilities for automatic loading by hoppers, harvesters, and the crusher.
  - Configure incline with a wrench: right-click rotates direction, sneak-right-click toggles between `UP` and `DOWN`.
- **Right-Angle Conveyor Belt (`refinery:right_angle_conveyor_belt`)**:
  - Added 90-degree corner conveyors to route items cleanly around factory turns without bottlenecks.
  - Features two-phase travel physics that guides items to the corner center and vectors velocity smoothly into the turn exit.
  - Monolithic 3D model with curved aluminum guide rails and an 8-segment curved animated conveyor belt surface.
  - Configure turn with a wrench: right-click rotates facing, sneak-right-click toggles between `RIGHT` and `LEFT`.
- **Industrial Grinder (Crusher) Direct Conveyor Auto-Export**:
  - The Industrial Grinder multiblock now automatically ejects processed items directly from its output slots onto any conveyor belt snapped to its right-side discharge chute or adjacent positions (as well as adjacent chests and item capabilities) every 4 ticks.

### Previous v1.8.8 Highlights:
- **Direct Fluid Auto-Transfer for Steam Boilers & Steam Turbines (No Extract Pipes Required)**:
  - Enabled active fluid auto-push directly into regular liquid pipes and adjacent fluid handlers for all steam boilers and the steam turbine, matching the seamless logistics behavior of the multiblock liquid storage tanks.
  - Dedicated Steam Output Ports (`boiler_steam_port`) and Turbine Water Output Ports (`turbine_water_port`) actively transfer fluids directly into adjacent pipes without requiring extraction servos or powered extraction pipes.
  - Facilitates 100% plug-and-play closed-loop Rankine power cycles.

### Previous v1.8.7 Highlights:
- **Industrial Fluid Discharge Survival Drop**:
  - Added missing block loot table definitions so the Industrial Fluid Discharge block drops itself properly when mined or broken in survival.
- **Storage Tank Item Icons & Held Models**:
  - Added missing item definitions for `storage_tank_casing`, `storage_tank_drain_valve`, and `storage_tank_fill_valve`, restoring crisp GUI inventory icons and 3D held models in player hands.
- **Blast Furnace Input Restrictions**:
  - Iron input slots and automation ports now strictly accept ONLY **Iron Ingot** (`minecraft:iron_ingot`) and **Block of Iron** (`minecraft:iron_block`). Raw iron, iron ores, and dusts are rejected.
  - Fuel input slots and automation ports now strictly accept ONLY **Coal** (`minecraft:coal`) and **Block of Coal** (`minecraft:coal_block`). Charcoal, coal dust, petroleum tar, bitumen, and other burnables are rejected.
  - Added unit conversion: 1 Block of Iron adds 9 iron units (yielding 9 steel ingots); 1 Block of Coal adds 9 fuel units (fueling 9 smelting operations).
  - In-progress iron units and fuel units are persistently stored in NBT and drop safely as blocks and ingots/coal when the controller is dismantled.
  - Updated GUI validation and tooltips to clearly reflect the Iron Ingot/Block and Coal/Block requirements.

### Previous v1.8.6 Highlights:
- **Sleek Industrial Access Ladder Redesign**:
  - Replaced the bulky, chunky caged ladder with a clean, authentic industrial steel access ladder mounted directly up the North face of the tank.
  - Features twin vertical stringer rails, standoff wall brackets connecting to the tank shell, distinct individual horizontal climbing rungs spaced every 3.5 units with hollow air between them (exposing the dark enamel tank panels behind), and smooth top walkthrough grab bars stepping onto the roof rim.
  - Assembled storage tank casing blocks in the North ladder column (`relX == 0, relZ == -radius`) now implement `isLadder = true`, allowing players to physically climb the ladder to the roof of the tank.
  - Updated all ladder stringers, rungs, and roof handrails to use a crisp, bright galvanized industrial steel texture swatch (`#D8DFE8`), eliminating messy textures and distorted UVs.
  - Overhauled top roof guardrails to follow the octagonal profile of the tank with an open walkthrough entry at the ladder head.
- **Removed Exterior Red Standpipe**:
  - Completely removed the vertical red pipe and flanged valves running down the side of the tank model.

### Previous v1.8.5 Highlights:
- **Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks**:
  - Implemented multiblock industrial liquid storage tanks based on modern Glass-Fused-to-Steel / Enamel bolted tank engineering (Hebei Wansheng / enameltanks.com reference design).
  - **Six Supported Multiblock Geometries & Capacities**:
    - `3x3x3`: **64,000 mB** (64 Buckets)
    - `3x3x6`: **256,000 mB** (256 Buckets)
    - `3x3x9`: **512,000 mB** (512 Buckets)
    - `5x5x5`: **1,000,000 mB** (1,000 Buckets)
    - `5x5x7`: **2,000,000 mB** (2,000 Buckets)
    - `5x5x9`: **4,000,000 mB** (4,000 Buckets)
  - **Universal Fluid Acceptance & Storage**:
    - Accepts and stores any fluid resource (Water, Lava, Crude Oil, Diesel, Gasoline, High-Pressure Steam, Ethanol, Molten Metals, etc.) with transactional rollback safety.
  - **Top-Center Intake & Bottom-Center Drainage**:
    - Top Center Fill Valve (`storage_tank_fill_valve`): accepts fluid inputs via pipes, pumps, or buckets and directs them to the tank interior.
    - Bottom Center Drain Valve (`storage_tank_drain_valve`): master multiblock controller managing structure assembly, fluid storage, container interactions, and active pipe drainage.
  - **Seamless Logistics Automation & Standard Pipe Support (No Extract Pipe Required)**:
    - The bottom center drain valve actively auto-pushes fluid downwards into pipes underneath the tank floor and horizontally into neighbor pipes.
    - Attaching a regular liquid pipe directly receives fluid without requiring an extraction pipe or mechanical puller.
  - **Custom Industrial Storage Tank GUI**:
    - Custom brushed-metal interface with two bucket transfer slots (fill and empty).
    - Real-time animated sight glass fluid gauge displaying fluid level percentage, formatted fluid name, and exact amount/capacity readout.
    - Quick "Dump / Flush" button to purge contents when re-tasking a tank for a different fluid.
  - **Monolithic 3D Render & Holographic Blueprint Projection**:
    - Monolithic rendering featuring gloss enamel bolted panels, concrete foundation ring curb, circumferential galvanized wind stiffener rings, white/galvanized caged safety ladder, intermediate rest landing platform, and top roof perimeter handrails.
    - Holographic blueprint ghost projection showing exact block placement requirements and ghost guides when cycling target sizes with the Refinery Wrench.
  - **Shaped Crafting Recipes & Loot Tables**:
    - Added crafting recipes and loot tables for `storage_tank_casing`, `storage_tank_drain_valve`, and `storage_tank_fill_valve`.

### Previous v1.8.4 Highlights:
- **Monolithic 3D Steam Turbine Model & High-Resolution Textures**:
  - Replaced the multiblock's individual block rendering with a high-fidelity monolithic 3D industrial steam turbine model based on heavy industrial turbine-generator skids (Elliott, Dresser-Rand, Siemens).
  - **Structural Base Skid**: Electric blue (`#12569F`) heavy steel I-beam skid base with longitudinal runners, cross-beams, and anchor footing pads with foundation bolts.
  - **Electric AC Generator / Alternator**: Cylindrical stator housing with horizontal cooling vents/ribs, end bell air intake cowl with perforated black mesh, and top rectangular terminal junction box with high-voltage hazard plate.
  - **Intermediate Gearbox**: Cast speed-reduction gear casing with red lubrication tag plate ("Royal Purple" style), oil level sight glass, and flexible coupling guards.
  - **Split Turbine Casing**: Heavy horizontally split cast turbine shell with curved dome, prominent bolted perimeter split flange, circular bearing inspection end-cap covers, and crane lifting eye lugs.
  - **Steam Inlet Chest & Governor**: Front flanged steam inlet nozzle with circular bolt circle and dark steam bore (aligned with the Steam Port), throttle trip valve with red handwheel, and top governor valve actuator assembly with linkage arms.
  - **Upright Control Console & Instrumentation**:
    - Upper blue instrumentation box housing three analog dial gauges (Steam Pressure 0-600 PSI, Turbine RPM, and Condenser Vacuum) and digital LED readout window.
    - Lower pale-grey electrical cabinet with 3D red emergency stop mushroom button, yellow bezel, rotary selector switches, status lamps, and wiring conduit.
  - **Dynamic Operational Indicators**:
    - Full-bright emissive green run indicator lamp and active power output display when the turbine is actively generating Forge Energy (`isActive = true`).
    - Idle standby indicator lamp and resting dials when offline.
  - **Seamless Multiblock Invisibility & Interaction Forwarding**:
    - Turbine Casing, Turbine Rotor Shaft, and Turbine Port blocks dynamically convert to invisible, occlusion-free states when assembled, ensuring seamless rendering without light clipping or internal block occlusion.
    - Right-clicking any constituent block in an assembled turbine forwards interactions to the controller menu or allows wrench toggling of blueprint holograms.

### Previous v1.8.2 Highlights:
- **Fluid Pump Wrench Linking Mode**:
  - Fixed an issue where sneak-right-clicking a Coal Fluid Pump (or any pump variant extending `AbstractFluidPumpBlock`) with the Refinery Wrench dismantled the pump instead of selecting it for 3D suction hose linking.
  - Added double-sneak-click confirmation on the same pump block to dismantle and retrieve it.
  - Shift-right-clicking any block or fluid source in the world properly anchors the intake point and deploys the animated 3D suction hose.
- **Pipe Back-Drain & Backflow Prevention**:
  - Implemented transactional `SnapshotJournal` fluid capability on all pump variants: pumps now safely accept returning fluid of the same type without leaking or stalling pipe parcels.
  - Disabled pump discharge downward (`Direction.DOWN`) into mounting bases and skid frames.
  - Added BFS tile-entity protection so contiguous source searching never targets or drains water from adjacent pipes, waterlogged pipes, tanks, or machines.
  - Protected waterlogged blocks during finite source drainage by unsetting the `WATERLOGGED` block state property rather than replacing blocks with air.
  - Changed default fluid pump operating mode to `[INFINITE WATER]` (`true`) so base water sources are preserved automatically upon placement.

### Previous v1.8.0 Highlights:
- **Livestock Feeding Trough (`refinery:feeding_trough`)**:
  - Added a 2-block-wide monolithic multiblock feed bunk designed for automated livestock breeding and animal farming.
  - **Monolithic 3D Model**: High-detail formed weathered galvanized sheet steel feed bunk featuring realistic zinc spangle crystal flakes, rolled top safety flanges, four heavy-gauge steel divider cross-struts, end bulkheads with cleanout slots, and corner skid feet.
  - **Dynamic In-World Hay Mound**: High-resolution golden straw/hay texture filling the trough that rises smoothly and continuously from Y = 1.0px to Y = 6.4px based on inventory food stock.
  - **Automated Livestock Breeding**:
    - Scans a 4.5-block radius (10×5×10 bounding volume) around the 2-block trough every second.
    - Feeds up to 2 eligible adult animals per second that are off breeding cooldown (`canFallInLove()`), triggering love hearts (`broadcastEntityEvent(18)`) and realistic eating sound effects (`SoundEvents.GENERIC_EAT`).
    - Excludes baby animals and animals currently on breeding cooldown or already in love.
  - **Universal Animal & Diet Compatibility**: Dynamically checks `animal.isFood(stack)` to accept any valid food item for vanilla and modded livestock (wheat, seeds, carrots, potatoes, beetroots, apples, berries, grasses, kelp, meat/fish, slimeballs, fungi, etc.).
  - **Logistics & Multiblock Integration**: Unified double-block inventory accessible from any face by Logistics Automation item pipes, conveyors, hoppers, and droppers. Interactive right-click quick-deposit and custom GUI with live herd radar counter and golden hay fill gauge.
  - **Crafting Recipe**: 4 Steel Plates + 2 Iron Bars + 1 Hay Bale produce 1 Feeding Trough.

### Previous v1.7.2 Highlights:
- **Calibrated 1/2 Conveyor Speed**:
  - Reduced item transit velocity by half to 0.05 blocks/tick (~1.0 block per second), providing smooth, authentic industrial conveyor pacing.
- **Item-Only Conveyance**:
  - The conveyor belt now strictly conveys items (`ItemEntity`). Players, animals, and monsters are no longer pushed or displaced when standing or walking over the belt.
- **Forward Belt Scroll Direction Fix**:
  - Fixed UV texture scrolling to advance forward in the exact direction items travel.
  - Added reverse return belt scrolling along the underside and synchronized rotating end roller velocity.

### Previous v1.7.0 Highlights:
- **Industrial Belt Conveyor System (`refinery:conveyor_belt`)**:
  - Added modular low-profile belt conveyor system for high-throughput factory automation, item transport, and seamless row snapping.
  - High-detail extruded anodized aluminum bed frame with side T-slot channels, heavy cast mounting foot pads, and galvanized steel deck bed.
  - Matte black high-density vulcanized rubber canvas belt with micro-grip friction ribs.
  - Intelligent snapping: conveyors placed in a row join seamlessly with zero gaps, hiding intermediate end caps and rollers.
  - Automated inventory injection at head terminal and insertion from hoppers/droppers/pipes.
  - Redstone halt support and Refinery Wrench rotation.
  - **Automated Machine & Container Integration**:
    - Items reaching the conveyor head automatically inject into adjacent inventories, chests, hoppers, furnaces, and machines via the NeoForge item transfer system.
    - Hoppers, droppers, and pipes can directly deposit items onto the belt surface.
  - **Redstone & Wrench Automation**:
    - Redstone signals pause conveyor operation, halting belt scrolling and item transit.
    - Right-click or Sneak-Right-Click with the **Refinery Wrench** rotates the conveyor orientation with instant neighbor re-connection.
  - **Crafting Recipe**: 3 Petroleum Tar + 1 Redstone Dust + 5 Iron Ingots produce 4 Industrial Conveyor Belts.

### Previous v1.6.5 Highlights:
- **Dedicated Fluid Pump Variants**: Split the fluid pump into 3 distinct specialized machine blocks and items: Coal Fluid Pump (`#374151`), Diesel Fluid Pump (`#FA983A`), and Electric Fluid Pump (`#2563EB`).
- **Dedicated GUIs**: Tailored interfaces displaying only relevant power controls and gauges.
- **In-World Shift-Right-Click Mode Toggle**: Toggle between `[ INFINITE WATER ]` and `[ DRAIN POOL ]` in-world.
- **Water Drain Traversal Fix**: Navigates across both source and flowing water to drain pools dry.

### Previous v1.6.3 Highlights:
- **Transfer API Transaction Safety & Crash Fix**:
  - Fixed `NullPointerException: Cannot invoke "net.neoforged.neoforge.transfer.transaction.TransactionContext.depth()" because "transaction" is null` that crashed the game when the Fluid Pump pumped fluid into Logistics Automation pipes.
  - Wrapped all capability fluid insertion logic inside `try (Transaction tx = Transaction.openRoot()) { ... tx.commit(); }` across `FluidPumpBlockEntity`, `ArcMelterControllerBlockEntity`, `CastingChannelBlockEntity`, and `FermenterBlockEntity`.
- **Screen Typography & Missing Glyphs Fix**:
  - Replaced unsupported Unicode emojis in `FluidPumpScreen` with crisp, legible bracketed status badges (`[PUMPING]`, `[IDLE]`, `[NO POWER]`, `[NO FUEL]`, `[NO DIESEL]`, `[TANK FULL]`, `[NO INTAKE]`, `[OUT OF RANGE]`, `[POOL DRAINED]`, `[PAUSED - REDSTONE]`), eliminating rectangular missing glyph boxes.

### Previous v1.6.2 Highlights:
- **Industrial Fluid Discharge (`refinery:fluid_discharge`)**:
  - Heavy-duty industrial foot valve & slotted discharge strainer outlet block.
  - **Monolithic 3D Model**: High-gloss fire-engine red cylindrical housing, mounting collar with 4 cast ribs, dual-tier bolt flanges with side ear lugs, graphite gasket, polished silver/chrome hexagonal bolt heads, threaded studs with hex nuts, slotted dome cage with vertical vent slots exposing dark interior valve throat and flapper plate, and perforated dome crown.
  - **Dual Toggled Operational Modes**:
    - **`VOID / DUMP`**: Fluid pumped into the block from Logistics Automation or standard pipes simply disappears (voided).
    - **`FILL`**: Discharges liquid into the surrounding area at the **same Y level**, filling contiguous air/replaceable blocks with fluid source blocks (1,000 mB per block).
    - **Strict Contiguous Flood-Fill (BFS)**: Fluid expands only through the discharge block or contiguous blocks of the same fluid. Never jumps across solid walls or boundaries into neighboring pits!
  - **Snap Interaction & Mode Toggling**:
    - Right-click with empty hand or Refinery Wrench toggles mode with sound and chat feedback.
    - Sneak-right-click inspects live status: mode, fluid volume, active state, and redstone signal.
  - **Redstone Control**: Redstone signal safely pauses discharging.

### Previous v1.6.0 Highlights:
- **Industrial Fluid Pump (`refinery:fluid_pump`)**:
  - Heavy-duty portable engine-driven pump modeled after classic industrial gas/diesel water pumps (Superkiger WP40X).
  - High-fidelity **Monolithic 3D Model**: tubular black steel roll cage, safety-yellow recoil pull-starter shroud, white enamel fuel tank with chrome cap, cast aluminum centrifugal volute pump casing, top vertical discharge pipe/flange, and front suction intake flange.
  - **In-World 3D Suction Hose**: Dynamic 3D flexible spiral-ribbed suction hose connecting the pump to the suction point in the world, complete with brass perforated suction strainer basket and active pulsing suction wave animation.
  - **Refinery Wrench Suction Point Linking**: Sneak-right-click the pump with a Refinery Wrench to link, then click any block or fluid source in the world (up to 64 blocks away) to set the suction intake. Double sneak-clicking dismantles the pump.
  - **Contiguous Fluid Source Flood-Fill (BFS)**: Intelligently discovers and pumps all contiguous, connected fluid source blocks one at a time from the intake point outward.
  - **Infinite Water Mode Toggle (`INF` / `DRY`)**: Toggleable in the GUI. Pumps infinite water without removing source blocks, or drains pools dry. Non-water fluids always drain.
  - **Multi-Fuel Engine Architecture**: RF/FE mode, solid fuel (coal/tar/wood) mode, and liquid diesel/gasoline mode.
  - **Logistics Craft Pipe Auto-Output**: Automatically pumps fluid out of its 10,000 mB tank directly into adjacent Logistics Craft fluid pipes.

### Previous v1.5.15 Highlights:
- **Open-Top Grinder Hopper with 3D Spinning Crusher Blades**:
  - Redesigned the Industrial Grinder's top hopper into an open-top steel bowl housing twin counter-rotating 3D spiked shredder rotors (`7` toothed blade discs per shaft) that spin while grinding and display the actual loaded input material being pulled into the blades.
  - Added direct top-hopper item intake: items dropped into the top hopper bowl (from standalone conveyors or droppers) are automatically absorbed into the machine's input buffer.
- **Dynamic Output Conveyor & Clean Belt Visuals**:
  - Removed the left-side belt and all pre-generated static rock mounds from the hopper and discharge belt.
  - The right-side inclined output conveyor now renders an empty belt when idle and dynamically displays the actual crushed output product climbing the conveyor when unloading.
- **Fixed Transparent Floor Under Assembled Multiblocks**:
  - Fixed an issue where floor blocks directly beneath the Industrial Grinder, Steam Boilers, and Metal Press were culled/transparent when assembled (`getOcclusionShape` now returns `Shapes.empty()` when assembled).

### Previous v1.5.14 Highlights:
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
