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

# 🏭 Industrial Refinery
**Heavy Industry • Fractional Distillation • Thermodynamic Closed-Loop Steam Power • Metallurgy • BuildCraft-Style Pipe Logic Gates**

*Built natively for Minecraft 26.3 on NeoForge.*

---

## 📖 Overview
**Industrial Refinery** is an expansive industrial engineering mod designed to bring realistic, rewarding progression back to Minecraft. From prospecting for subterranean crude oil reservoirs in oceans and deserts with kinematic pump jacks, to fractional distillation, closed-loop Rankine thermodynamic power cycles, high-temperature electric arc metallurgy and continuous ingot casting, agricultural mechanization, extruded aluminum conveyor logistics, and the legendary return of **BuildCraft-style programmable Pipe Logic Gates**!

Whether you play standalone or alongside logistics and tech mods, Industrial Refinery delivers high-performance, lag-free automation with zero hard dependencies.

---

## ⚙️ Key Features

### 🛢️ 1. Petroleum Geology, Downhole Extraction & Fractional Distillation
- **Real In-World Fluid Crude Oil**: Naturally occurring subterranean oil reservoirs and surface lakes rimmed with bituminous tar sand, red oil sand, and oil shale. Viscous black liquid featuring authentic fluid physics and animated flows.
- **Deposit Capacities & Spacing**: Standard oil pits yield **600,000 mB to 1,000,000 mB**, with rare high-pressure **Super-Geysers** erupting with **2,000,000 mB**! Generates ~1 in 200 chunks with an enforced **20-chunk (320-block) minimum spacing** between wells to prevent clustered lakes.
- **Persistent Anti-Retrogen**: Oil deposits are strictly registered once generated. Even when drained completely dry, chunks will never re-generate oil over your quarries or drills.
- **Crude Pump Jack (2x3x3 Multiblock Beam Pumping Unit)**:
  - Heavy industrial oil extraction unit with an authentic kinematic walking beam (±12° rocking), counterweighted rotating crank wheels (360°), Samson post A-frame, arc horsehead, and vertically reciprocating polished rod entering the wellhead.
  - Placed from a single inventory item with safe single-item drop recovery.
  - Automatically prospects downhole vertical columns beneath the wellhead (up to 50 blocks depth, 10-block horizontal radius) for crude oil sources.
  - Power-scalable extraction rates: 1 FE/t (100 mB/min), 2 FE/t (500 mB/min), and 4 FE/t (1,000 mB/min).
  - Front wellhead acts as an auto-export port directly pushing crude oil into connected pipes and tanks.
- **3-Stage Fractional Distillation Column (3x9x3 Multiblock — Three 3x3x3 Stages)**:
  - Divided into Stage 1 (Bottom: Liquid Tar & Bitumen), Stage 2 (Middle: Diesel), and Stage 3 (Top: Gasoline).
  - Consumes **1,000 mB Crude Oil** per cycle with individually toggleable stages via a dark gunmetal telemetry GUI.
  - Flexible distillation outputs: Stage 1 only -> 500 mB Liquid Tar + 1 Bitumen; Stage 2 only -> 500 mB Diesel; Stage 3 only -> 500 mB Gasoline; All 3 Stages -> 250 mB Tar + 250 mB Diesel + 250 mB Gasoline + 25% chance for 1 Bitumen.
  - Takeoff ports auto-push fluids into connected pipes and tanks; Byproduct Ports auto-eject solid Bitumen and Tar directly onto adjacent conveyor belts, pipes, or containers.
- **Burner & Electric Refinery Furnaces (1x2x1 Heat Sources)**:
  - Supply the required operating heat (>= 350°C) placed adjacent to Stage 1.
  - Choose between the **Burner Refinery Furnace** (4x rapid burn speed across 3 fuel slots) or **Electric Refinery Furnace** (500 FE/t, 500,000 FE buffer).
  - Single-item placement and drop safety with glowing serpentine crude-oil heating coils.
- **Bitumen Storage Pile (3x3 Multiblock Bulk Storage)**:
  - Heavy 18-part loose material bunker with real-time dynamic 3D visual heightfield mesh stages.
  - Physically mounds up from a dusting inside reinforced steel retaining walls to a towering 2-block asphalt peak as inventory fills (up to 3,456 items across 54 double-chest slots).
  - Hopper-style vacuum absorption automatically sucks in nearby dropped bitumen items with pebble impact sounds and particle splashes.
  - Full multi-sided item extraction and hopper/pipe connectivity.

### ⚡ 2. Thermodynamic Closed-Loop Steam Power (Rankine Cycle)
- **Small (3x3x3) & Large Heavy (5x5x5) Steam Boilers**:
  - Boil water with solid coal/coke or liquid petroleum fuels into pressurized Steam.
  - Monolithic Scotch marine boiler geometry with opening cleanout hatches, realistic overheating mechanics, and audible emergency raid siren alarms if water runs dry.
- **Steam Turbine Generator (3x3x3 Multiblock)**:
  - Converts high-pressure steam into **10,000 FE/t** of clean electricity (consuming 300 mB/t steam) while simultaneously condensing 150 mB/t exhaust steam back into pure water.
  - High-fidelity monolithic 3D turbine-generator skid in vibrant industrial electric blue, featuring an AC alternator, speed-reduction gearbox, curved split casing, dial gauges, and emergency stop controls.
  - Overhauled dark gunmetal GUI featuring digital telemetry (RPM readout, power rate, status LED), rotating blade activity monitor, and recessed fluid gauges.
- **Closed-Loop Conservation**: Pipe the turbine's condensed water directly back into your boiler to build completely closed-loop, self-sustaining power plants with zero water loss!
- **Industrial Diesel Generator (1x2x2 Compact Power Plant)**:
  - Standalone internal combustion power station generating **256 FE/t** directly from refined Diesel fuel.

### 🔨 3. Heavy Metallurgy, Continuous Casting & Mineral Processing
- **Industrial Blast Smelter (5x5x5 Multiblock)**:
  - Massive refractory foundry with a 3x3 transparent heat-resistant glass viewing window.
  - Smelts iron ingots/blocks and coal/coal blocks into structural **Steel Ingots**.
- **Industrial Metal Press (3x2x2 Multiblock)**:
  - Heavy hydraulic mechanical press with an animated ram, anvil impact sounds, and swappable Stamping Dies:
    - **Plate Die**: Cold-forms ingots (Steel, Iron, Copper, Gold, Tin, Bronze) into industrial Plates.
    - **Gear Die**: Cold-forms ingots or plates into industrial Gears at **4x material efficiency** over hand crafting.
    - **Corrugated Steel Die**: Cold-forms 1 Steel Ingot/Plate into **4x Corrugated Steel Panels** (or 1 Iron -> 2 Panels).
    - **Rail Die**: Cold-forms steel/iron/gold/copper into vanilla and powered rails.
- **Industrial Multi-Smelter (5x4x3 Multiblock)**:
  - Early-game 8-in-1 smelting array with dual-fuel operation (Solid Fuel or 5,000 FE/t RF).
  - 6-block hollow combustion chamber with dynamic 3D floating cooking items and pulsing ember or RF induction animations.
  - Crafted entirely from early-game materials — **no steel required**!
- **Industrial Grinder & Pulverizer (3x3x3 Multiblock)**:
  - Dual-motor mineral crusher with twin counter-rotating spiked drums and animated block pulverization.
  - Delivers **2x Ore Doubling**, crushes stone into gravel and sand, and pulverizes oil sand into **3x Bitumen** with a 50% chance for **Tar**.
- **Heavy Pit Quarry (3x3x3 Multiblock Excavator)**:
  - Heavy open-pit continuous surface mining crawler rig engineered for automated geological strip mining and deep pit excavation.
  - **3-Phase Automated Excavation Sequence**: Phase 0 clears a $43 \times 43 \times 3$ overburden terrace bench in front of the machine; Phase 1 lines the perimeter of the inner $41 \times 41$ pit with safety fencing (using custom player-supplied fences or auto-fabricated stone railings); Phase 2 excavates the inner $41 \times 41$ pit layer-by-layer all the way down to bedrock.
  - **3 Flush Conveyor Export Chutes**: Extended Rear `(0, 0, 3)`, Left `(-2, 0, 1)`, and Right `(2, 0, 1)` discharge arms with 5px bed height that connect seamlessly to mod conveyors for round-robin auto-ejection.
  - **Power-Scalable Speed**: Max speed 1 block/second requiring 20 FE/tick, smoothly running progressively slower as power drops below 20 FE/t and stopping when unpowered.
  - **Industrial Lighting**: Dual high-intensity halogen work floodlights (emitting in-world light level 15) and rotating amber hazard strobe.
  - **Telemetry GUI**: Interactive radius adjustment down to 3 ($7\times 7$), live remaining blocks breakdown dashboard tracking individual block counts with icons and tooltips, dedicated fence slot, and 9-slot buffer.
- **Electric Arc Melter (3x3x3 Multiblock)**:
  - Monolithic 3D Industrial Electric Arc Furnace (EAF) with cylindrical water-cooled ribbed shell, high-voltage transformer vault, copper busbars, charge hopper, and eccentric bottom-tapping launder spout.
  - 3 reciprocating graphite electrodes discharging dynamic 3D plasma arc bolts into a glowing molten metal bath (Molten Iron, Gold, Copper, Tin, Bronze, and Glass) with 2x ore doubling! Starts melting as soon as its energy buffer reaches **50,000 FE**.
- **Continuous Casting Press (3x3x3 Multiblock)**:
  - Monolithic 3D Heavy Hydraulic Continuous Casting & Quenching Press with 4 chrome tie-rod columns, overhead hydraulic cylinder, hazard-striped ram crosshead, copper crystallizer mold, ground-level `Casting Input Port` at `(0, 0, 2)` for direct `Y=0` channel connections, and motorized run-out roller table that stamps and water-quenches molten metal (1:1 ratio: 1,000 mB Water + 1,000 mB Molten Metal) into solid ingots and glass blocks.
- **Refractory Open Casting Channels**:
  - Open 3D refractory trough geometry for seamless gravity-fed molten metal transfer between Arc Melters, Casting Presses, and fluid pipes.
- **Corrugated Steel Panels**:
  - Heavy industrial structural cladding and roofing panels in unpainted galvanized steel and 16 dyed colors.
  - Seamless tiling with 3-axis pillar orientation and full **LabPBR 1.3 normal maps & Parallax Occlusion Mapping (POM)** for realistic 3D depth and metallic specular sheen in modern shaderpacks.
- **Industrial Fermenter (Agricultural Biofuel)**:
  - Standalone bioreactor that ferments crops (Wheat, Potatoes, Beetroots, Sugar Cane) into high-yield **Ethanol** fuel.

### 📦 4. Industrial Belt Conveyor Logistics System
- **Modular Low-Profile Belt Conveyors (`refinery:conveyor_belt`)**:
  - Extruded anodized aluminum bed frame with side T-slot channels, heavy cast mounting foot pads, and galvanized steel deck bed.
  - Dynamic UV scrolling matte black rubber belt and rotating brushed stainless steel end rollers matching belt velocity.
  - Physical item centering, smooth propulsion, and item-only conveyance with zero mob or player interference.
  - Intelligent seamless snapping: placing conveyors in a row automatically removes intermediate terminals, rendering end rollers only at open line terminals.
  - Direct machine injection: items reaching the conveyor head automatically inject into adjacent inventories, chests, hoppers, and machines.
- **Adaptive Corner & T-Intersection Junctions**:
  - Automatically adapts side frames when conveyors meet at 90 degrees or form T-junctions: retracts the receiving side rail so items roll smoothly onto the belt, while adding a protective rear corner guard rail at 90° bends to prevent item spillage.
- **Sloped Conveyor Belt (`refinery:sloped_conveyor_belt`)**:
  - Transports items smoothly up or down 1-block elevations with height-interpolating slope physics and seamless crest handoffs.
  - Dynamically extends continuous aluminum support legs with heavy bolted foot pads down to the ground (raycasts up to 32 blocks down).
- **Right-Angle Conveyor Belt (`refinery:right_angle_conveyor_belt`)**:
  - Dedicated 90-degree curved corner conveyor with two-phase vectoring to guide incoming items cleanly into 90° turns without dead zones.

### 🛢️ 5. Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks
- **Industrial Bulk Liquid Storage**: High-capacity Glass-Fused-to-Steel (GFS) / Enamel bolted industrial liquid storage tanks based on municipal and petroleum engineering.
- **Six Scalable Capacities**:
  - `3x3x3`: **64,000 mB** (64 Buckets)
  - `3x3x6`: **256,000 mB** (256 Buckets)
  - `3x3x9`: **512,000 mB** (512 Buckets)
  - `5x5x5`: **1,000,000 mB** (1,000 Buckets)
  - `5x5x7`: **2,000,000 mB** (2,000 Buckets)
  - `5x5x9`: **4,000,000 mB** (4,000 Buckets!)
- **Universal Fluid Handling**: Accepts and stores any fluid resource (Water, Lava, Crude Oil, Diesel, Gasoline, Steam, Ethanol, Molten Metals) with transactional rollback safety.
- **Top Intake & Bottom Direct Auto-Push Drainage**:
  - Fills through the top-center fill valve via pipes, pumps, or buckets.
  - Drains through the bottom-center drain valve, which actively pushes stored fluid downward and horizontally into connected pipes — **no extraction pipe or mechanical puller required**!
- **Sight Glass & Purge Controls**:
  - Full-height illuminated sight glass displaying fluid color, percentage, formatted fluid name, and exact mB volume.
  - Dedicated "Dump / Flush" button in the GUI to instantly purge residual fluids when reconfiguring tank storage.
- **Monolithic 3D Render & Holographic Blueprints**:
  - Glossy dark enamel bolted plates, concrete foundation curb, wind stiffener rings, caged safety ladder, and roof handrails.

### ⛽ 6. Portable Industrial Fluid Pumps & 3D Suction Hose
- **Three Specialized Variants**:
  - **Coal Fluid Pump**: Heavy Cast-Iron Dark Charcoal frame. Single solid-fuel slot burning coal, charcoal, or furnace fuels at 1 coal per 3 seconds of active pumping.
  - **Diesel Fluid Pump**: Caterpillar Yellow-Orange frame. 4,000 mB internal diesel reservoir consuming 10 mB per bucket pumped (1 bucket powers 100 buckets pumped).
  - **Electric Fluid Pump**: High-Voltage Electric Blue frame. 20,000 FE buffer consuming 400 FE per bucket pumped (20 FE/t).
- **In-World 3D Flexible Suction Hose**: Sneak-click with the Refinery Wrench to link, then click any block or fluid source in the world (up to 64 blocks away) to deploy a dynamic 3D spiral-ribbed suction hose with brass suction strainer.
- **BFS Flood-Fill Navigation**: Intelligently discovers and pumps all contiguous, connected fluid source blocks one at a time from the intake point outward.
- **Infinite Water Mode (`[ INFINITE ]` / `[ DRAIN ]`)**: Toggleable in the dedicated GUI or by **Shift-Right-Clicking the pump block in-world** with an empty hand or wrench! Infinite mode draws unlimited water without destroying source blocks; Drain mode clears fluid bodies completely dry.
- **AWWA Safety-Yellow Fire Hydrant (`refinery:fire_hydrant`)**: Pipe-fed standpipe hydrant with an 8,000 mB internal buffer and animated top brass pentagonal stem nut. When connected to a fluid pipe and supplied with liquid, players can simply right-click with an empty bucket in hand to fill it!

### 🔌 7. BuildCraft-Style Pipes & Pipe Logic Gates
The classic BuildCraft automation experience, re-engineered natively for modern Minecraft!
- **Native 3D Monolithic Pipes**:
  - Slender pixel-art profile with 360° clear glass bodies and heavy industrial collars.
  - Traveling items are fully visible in transit with smooth interpolation and ambient lighting.
  - Includes Item Pipes, Fluid Pipes, Redstone Valve Pipes, Buffer Pipes, Quartz Merge Pipes, Insertion, Sorting, Round-Robin, and Void Pipes.
- **BuildCraft-Style Pipe Facades & Structure Pipes**:
  - Conceal pipes seamlessly behind any full block texture (Vanilla, Refinery, or modded) with LabPBR metallic specular and normal map support.
  - Structure pipes provide zero-flow structural routing for wires and gates.
- **Slim Face Attachment**: Attaches flush onto **any face of a pipe**, machine, chest, or refinery port with non-solid hitboxes.
- **4 Gate Tiers**:
  - **Basic Gate**: 1 conditional trigger-action pair.
  - **Iron Gate**: 2 conditional pairs + Red & Blue wire channels.
  - **Gold Gate**: 4 conditional pairs + 4-wire network support.
  - **Diamond Gate**: 8 conditional pairs across an industrial dual-column dashboard.
- **Autarchic Pulsar (Automated Extraction)**: Upgrade any gate with an **Autarchic Chipset** (via crafting or in-world right-click) to pulse every 1 second, pulling 1 item from adjacent containers into the pipe or pumping 250 mB of fluid. **No redstone clocks or external engines required!**
- **4-Color Pipe Wires**: Broadcast Red, Blue, Green, and Yellow logic signals along continuous pipe networks up to 24 blocks.

### 🌾 8. Standalone Agricultural Automation
- **Automated Planter**: Smart cultivator with GUI On/Off switch and configurable work radius up to 9 blocks in all directions (3x3 up to 19x19 / 361 tiles). Automatically tills soil and plants seeds or tree saplings.
- **Automated Harvester**: Omnidirectional farm harvester with GUI On/Off switch and configurable radius up to 9 blocks. Auto-harvests mature crops, fells whole trees via 3D recursive flood-fill, replants seeds, and auto-ejects drops into chests or pipes behind or below.
- **Livestock Feeding Trough**: Monolithic galvanized steel feed bunk with dynamic 3D golden hay mound that rises and falls with inventory stock, 4.5m herd radar, automated breeding for off-cooldown adults, and full dietary compatibility with zero food waste.

### 🔧 9. 3D Holographic Blueprints & The Refinery Wrench
- **The Refinery Wrench**: Right-click any multiblock controller to project a life-sized, translucent **3D in-world building hologram** for 30 seconds.
- **Color-Coded Holograms**: Every block type renders in a signature high-contrast color (Emerald Controllers, Petroleum Amber Ports, Canary Diesel, Flame Orange Fuel, Fiery Red Fireboxes, Lime Energy, Crystal Purple Glass) so multiblock construction is effortless.
- **Instant Dismantling**: Shift + Right-Click any mod machine block with the wrench to instantly break and collect it into your inventory.
- **In-Game Field Manual / Guide Book**: Comprehensive in-game handbook covering multiblock layouts, recipes, progression flowcharts, and automation mechanics.

---

## 🔌 Mod Compatibility

Industrial Refinery is built with zero hard dependencies:
- **Logistics Mod**:
  - Full native pipe attachment & gate operation.
  - 1:1 interchange recipes between `refinery:tar` <-> `logistics:core/tar` and `refinery:bitumen` <-> `logistics:core/bitumen`.
  - 8 Logistics fluid pipes craftable using Refinery tar.
  - Industrial Grinder pulverizes all Logistics oil sands and outputs preferred Logistics items when present.
- **Universal Tech / Modpack Setup**:
  - Fully compatible with standard NeoForge Transfer API (`Capabilities.Item.BLOCK`, `Capabilities.Fluid.BLOCK`, `Capabilities.Energy.BLOCK`).

---

## 💻 Administrative Commands

- `/admin spawn_oil_pit [true|false]`: Spawns a natural oil pit (`false`) or super-geyser (`true`) at your coordinates.
- `/admin spawn_geyser`: Instantly spawns a 1,000,000 mB erupting super-geyser.
- `/admin oil_stats`: Displays scanned chunks and registered oil wells.
- `/admin reset_oil_retrogen`: Resets retrogen history for the current save world.
*(Also mirrored under `/refinery ...`)*

---

## ❓ Modpack Permissions & FAQ

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
`refinery-1.11.60.jar`

### Display Name
`Industrial Refinery 1.11.60 (NeoForge 26.3)`

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
# Industrial Refinery v1.11.60

### What's New in v1.11.60:
- **Boiler & Steam Turbine Thermodynamic Balance Rebalancing**:
  - **Small Boilers**: Rebalanced solid fuel steam production to **500 mB/s** (25 mB/t, consuming 12 mB/t water).
  - **Large Boilers**: Rebalanced solid fuel steam production to **1,500 mB/s** (75 mB/t, consuming 37 mB/t water).
  - **Steam Turbine Steam Requirement**: Tuned steam intake to **1,450 mB/s** (72.5 mB/t average) at peak generation (10,000 FE/t). 1 Large Boiler or 3 Small Boilers (1,500 mB/s) provide a steady 50 mB/s surplus to keep the 32,000 mB steam buffer saturated.
- **Dynamic Turbine RPM Scaling & Centered Diagnostic Readout**:
  - Turbine speed now dynamically scales with power generation up to **3,000 RPM at peak generation** (10,000 FE/t; 0 RPM when idle).
  - Diagnostic monitor now displays centered text showing exact live RPM (`3000 RPM`) and generation (`+10000 FE/t`).
  - Active turbine blade indicator animation speed and hum sound pitch dynamically increase with RPM.
- **Fluid Pipe Pipeline Flow Bottleneck & Directional Momentum Fix**:
  - Fixed an issue where pipes equalized fluid using naive half-difference division (`(diff + 1) / 2`), halving throughput across every pipe block and throttling long pipelines down to ~24 mB/t.
  - Implemented directional flow momentum: pipes actively push fluid forward along the pipeline into downstream pipes and connected machine ports at the full material transfer rate (Iron: 500 mB/t, Steel: 1,500 mB/t) without distance attenuation or backward sloshing.
  - Steam turbine steam input port now immediately notifies the controller on fluid insertion for real-time sync.

---

# Industrial Refinery v1.11.58

### What's New in v1.11.58:
- **In-Game Field Manual Boiler Guide Pages (`GuideBookRegistry`)**:
  - Added dedicated, fully illustrated guide book pages with full multi-layer slice schematics, required block lists, and operational specifications for all four boiler multiblock types:
    - **Small Steam Boiler (3x3x3)**: 27-block compact solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 1 Firebox, 1 Pressure Pipe, 21 Steel Boiler Casings) producing up to 100 mB/t steam with 2 fuel slots.
    - **Large Heavy Steam Boiler (5x5x5)**: 125-block heavy-duty solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 9 Fireboxes, 27 Pressure Pipes, 85 Steel Boiler Casings) producing up to 300 mB/t steam with 8 fuel slots across 5 layer schematics.
    - **Small Diesel Boiler (3x3x3)**: 27-block liquid fuel boiler utilizing Diesel, Biodiesel, or Ethanol via fluid fuel port.
    - **Large Diesel Boiler (5x5x5)**: 125-block industrial liquid fuel boiler producing 300 mB/t steam with high-efficiency fluid burner combustion.

---

# Industrial Refinery v1.11.57

### What's New in v1.11.57:
- **Large Boiler & Diesel Boiler Multiblock Block Hiding**:
  - Unified the `ASSEMBLED` blockstate property across all boiler controllers and member blocks.
  - Fixed an issue where the 124 member blocks failed to hide upon multiblock formation, obscuring the monolithic 5x5x5 3D boiler model.
  - Expanded casing controller search radius from 3 to 5 blocks to guarantee full 5x5x5 structure coverage when clicked or broken.
- **Steam Turbine Generator Diagnostic Monitor & Operational State**:
  - Fixed active state detection in ContainerData: the turbine now correctly shows `ONLINE / 1,800 RPM` and live generated power (e.g. `+10,000 FE/t` or throttled load output) instead of `STANDBY / 0 RPM / 0 FE/t`.
  - Fixed steam buffer tooltip typo (`300 mB/t` at peak load, previously stated `100 mB/t`).
  - Fixed rotor expansion tooltip state (`ACTIVE` vs `IDLE`).
- **Steam Turbine Generator Energy Sync & Dynamic Load Throttling**:
  - Fixed `energyStorage.extract(...)` self-assignment bug so external energy extraction correctly synchronizes with the controller's energy level.
  - Removed duplicate auto-push logic between the controller BE and dedicated output ports.
  - Capped energy push rate to 10,000 FE/t and streamlined internal buffer to 100,000 FE, enabling prompt load-following throttling (e.g. at an 8,000 FE/t cable load, the turbine consumes 240 mB/t steam, allowing surplus steam from a Large Boiler to fill the 32,000 mB steam buffer).

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

# Industrial Refinery v1.11.45

### What's New in v1.11.45:
- **Per-Tick Machine Energy Requirements Across All Machines**:
  - Removed whole-process and excessive buffer requirements across all machines (previously checking `(PROCESS_TIME - progress) * ENERGY_PER_TICK` or multi-thousand FE thresholds).
  - Machines now fire off and operate as long as they have at least their nominal per-tick power (`ENERGY_PER_TICK`):
    - **Continuous Casting Press**: Starts immediately with $\ge$ 600 FE (instead of requiring 240,000 FE).
    - **Industrial Rock Crusher / Grinder**: Starts immediately with $\ge$ 2,000 FE (instead of requiring 120,000 FE).
    - **Industrial Metal Press**: Starts immediately with $\ge$ 500 FE (instead of requiring 20,000 FE).
    - **Industrial Multi-Smelter**: Starts immediately with $\ge$ 5,000 FE (instead of requiring 500,000 FE).
    - **Electric Arc Melter**: Starts immediately with $\ge$ 10,000 FE (instead of requiring 50,000+ FE).
    - **Industrial Fermenter**: Starts immediately with $\ge$ 15 FE (instead of requiring 900 FE).
- **Casting Channel Flow Speed Boost & Anti-Bounce-Back**:
  - Increased molten fluid transfer rate in open refractory casting channels to 50 mB every 2 ticks (25 mB/tick, 4x faster).
  - Reduced initial viscous flow propagation delay from 25 ticks to 10 ticks (0.5s).
  - Completely eliminated fluid bounce-back: fluid flowing down a channel will strictly proceed downstream and can never reverse direction back into the upstream feed.

---

# Industrial Refinery v1.11.44

### What's New in v1.11.44:
- **Continuous Casting Press Water Quenching Requirement (1:1 Ratio with Molten Metal)**:
  - Added a dedicated **4,000 mB Quenching Water Tank** alongside the 4,000 mB Molten Metal Basin on the Continuous Casting Press.
  - Each casting and quenching cycle now requires a **1:1 fluid ratio**: **1,000 mB of Water** and **1,000 mB of Molten Metal** per ingot/block cast.
  - Water and Molten Metal can be piped simultaneously into any Casting Press block or filled by right-clicking with Water Buckets / Molten Metal Buckets.
  - Added side-by-side `Molten Inflow Basin` and `Quenching Water Tank` gauges to the Continuous Casting Press GUI with live tooltips.
- **Electric Arc Melter 50,000 FE Start Threshold**:
  - The Electric Arc Melter now begins melting a batch as soon as its internal energy buffer reaches **50,000 FE** instead of waiting for the full 2,000,000 FE buffer to fill first.
- **Continuous Casting Press Ground-Level Input Port**:
  - Swapped the `Casting Input Port` at `(0, 1, 2)` (Layer 1, rear-center) with the `Casting Press Casing` block below it at `(0, 0, 2)` (Layer 0, rear-center), allowing ground-level `Y=0` Open Casting Channels from the Electric Arc Melter to connect directly into the Casting Input Port without elevation changes.
- **Multiblock Hologram & Guide Book Name Parity with JEI**:
  - Updated all 3D blueprint hologram labels across all 14 multiblock machines and the in-game Field Manual Guide Book so every block label matches its exact localized item name in JEI (`en_us.json`).

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

### What's New in v1.11.40:
- **Industrial Power & Steam Rebalance**:
  - **Electric Arc Melter**: Rebalanced power consumption to **10,000 FE/t** with an expanded **2,000,000 FE** internal buffer (100,000 FE/t max receive) for heavy industrial electrometallurgy.
  - **Steam Turbine Generator**: Overhauled to produce **10,000 FE/t** Forge Energy while consuming **300 mB/t** steam (exactly 3x Small Steam Boiler steam production). Condenses and recirculates **150 mB/t** water into the Rankine cycle, with max output throughput raised to **50,000 FE/t** and active energy pushing to adjacent cables.
  - **Large Steam Boilers**: Synchronized Large Steam Boiler output to **300 mB/t** steam consuming **150 mB/t** water, exactly matching 3 Small Steam Boilers. Synchronized Large Diesel Boiler to **750 mB steam / cycle** (300 mB water & 30 mB diesel per 10-tick cycle), exactly matching 3x Small Diesel Boiler.
  - **Industrial Multi-Smelter**: Rebalanced electric induction power consumption to **5,000 FE/t** with an expanded **1,000,000 FE** buffer.
  - **Industrial Rock Crusher / Grinder**: Rebalanced power consumption to **2,000 FE/t** with an expanded **500,000 FE** buffer.
  - **Industrial Metal Press**: Rebalanced stamping power consumption to **500 FE/t** with an expanded **100,000 FE** buffer.
  - **Continuous Casting Press**: Rebalanced casting power consumption to **600 FE/t** with an expanded **500,000 FE** buffer.
- **GUI Energy Bar & Tooltip Calibration**:
  - Dynamically scaled energy storage meters, status indicators, and hover tooltips across all machine GUIs (`ArcMelterScreen`, `SteamTurbineScreen`, `LargeDieselBoilerScreen`, `MultiSmelterScreen`, `IndustrialGrinderScreen`, `MetalPressScreen`, `CastingPressScreen`) to accurately display the new capacities and operational draw rates.

---

# Industrial Refinery v1.11.39

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

# Industrial Refinery v1.11.38

### What's New in v1.11.38:
- **AWWA Safety-Yellow Fire Hydrant (`refinery:fire_hydrant`)**:
  - Added a 3D AWWA-style safety-yellow **Fire Hydrant** block that attaches directly to any Industrial Fluid Pipe line.
  - **3D Model & Animated Brass Operating Nut**: Features a black cast-iron base flange with protruding silver hex bolts and nuts, yellow lower and upper double flanges, cylindrical yellow standpipe barrel with embossed `A` and `250 AWWA` markings, large front pumper nozzle cap with `B 41` pentagonal nut, left and right 2.5" side hose nozzles with chain lugs, domed yellow bonnet, and a top brass pentagonal stem nut that rotates when dispensing liquid.
  - **Right-Click Bucket Filling**: Buffers up to **8,000 mB** of liquid fed from connected fluid pipes (and draws directly from contiguous connected fluid pipes on demand). Right-click with an empty bucket in hand to fill it with 1,000 mB of the supplied liquid (Water, Lava, Crude Oil, Diesel, Gasoline, Liquid Tar, Ethanol, or Molten Metals).
  - **Inspection & Flushing**: Right-click with an empty hand to inspect buffered fluid level; sneak-right-click with an empty hand to drain/flush the hydrant barrel; right-click with a Refinery Wrench to rotate.

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

# Industrial Refinery v1.11.32

### What's New in v1.11.32:
- **Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Composition**:
  - Simplified the multiblock requirements: now strictly requires **1 Heavy Pit Quarry Controller** and **26 Quarry Structural Casings**.
  - All 26 blueprint positions and missing-component checks require `Quarry Structural Casing`.
  - Removed `Quarry Lattice Frame` blocks from the mod.

### What's New in v1.11.30:
- **Rear High-Intensity Work Floodlights**:
  - Dual overhead halogen work floodlights mounted on the gantry facing the pit illuminate the excavation zone with full brightness (`FULL_BRIGHT`) when active.
- **Safe Multiblock Player Displacement**:
  - Completing the 3x3x3 multiblock automatically teleports any players inside or standing on the multiblock safely to the ground in front of the operator station, preventing suffocation or getting trapped inside blocks.
- **Quarry Excavation Heights & Bedrock Digging**:
  - The outer $43 \times 43 \times 3$ clearing clears from the same Y level as the machine up 3 blocks ($Y = 2$ down to $Y = 0$).
  - Safety railing perimeter fence is placed at $Y = 0$ along the bench rim.
  - The inner $41 \times 41$ pit starts at $Y = -1$ below machine level and mines downward continuously to bedrock.
- **Built-in Rear Spoil Chute & Clean Front Face**:
  - Removed the front conveyor chute to keep the front operator station unobstructed.
  - Items designated as Throw-Back blocks automatically roll off the built-in rear conveyor chute into the pit void.
- **Kept Products Buffer & Side Conveyor/Pipe Export**:
  - Kept items wait in the 9-slot buffer until a normal conveyor (or belt below) or pipe is connected to the side ports (Left or Right), then actively export.
- **Strict 3-Block Multiblock Validation**:
  - Blueprint requirements strictly check only `Heavy Pit Quarry Controller`, `Quarry Structural Casing`, and `Quarry Lattice Frame`.

### What's New in v1.11.28:
- **Heavy Pit Quarry Throw-Back Blocks & Pit Spoil Ejection**:
  - Implemented configurable "Throw-Back" blocks that automatically eject out the rear conveyor chute into the open pit void.
  - Three convenient ways to set throw-back blocks:
    1. Click any block in the GUI Geological Manifest Monitor (highlights in orange with `[TB]` badge).
    2. Click `[SPOIL]` in the monitor header to instantly toggle all common overburden (dirt, stone, deepslate, gravel, sand, tuff, cobblestone).
    3. Insert any block into the dedicated Throw-Back Filter Slot (Slot 10) in the GUI at `(220, 85)`.
- **Side Conveyor Factory Integration**:
  - All non-throw-back products ("the rest of the products", including ores and minerals) route directly to connected normal conveyors on the left and right side conveyor ports (and front port).
  - Pushes items onto connected conveyor belts with forward velocity, buffering items in the 9-slot machine buffer if no conveyors are connected yet.
- **3D Model Rear Chute & Item Animations**:
  - Added the rear spoil discharge chute to the 3D monolithic excavator model with animated spoil items tumbling off the chute into the pit.

### What's New in v1.11.26:
- **Heavy Pit Quarry 180° Inversion (Digs Behind Machine)**:
  - Flipped work area 180° so excavation occurs entirely behind the machine, allowing the operator console and front face to remain clear and facing forward toward player workstations.
  - Front conveyor chute relocated to `(0, 0, -1)` discharging forward away from the pit edge alongside the left and right chutes.
- **Dynamic Material-Specific Speed Multipliers**:
  - Soft blocks (**Dirt, Sand, Gravel, and Tuff**) mine at **2x speed** (0.5s / 10 ticks per block at 20 FE/t).
  - Hard precious ores (**Iron and Diamonds**, including deepslate and raw ore blocks) mine at **1/2 speed** (2.0s / 40 ticks per block at 20 FE/t).
  - Standard blocks (Stone, Deepslate, Copper, Gold, etc.) mine at **1x base speed** (1.0s / 20 ticks per block at 20 FE/t).
- **Proactive Top-Down Clearing**:
  - The quarry monitors the excavated volume above its drill depth; any sand, gravel, fallen debris, or fluid stone that enters the cleared volume is systematically mined from the **top down** before resuming downward excavation.

### What's New in v1.11.24:
- **Heavy Pit Quarry Industrial GUI Redesign & Complete Pit Block Manifest**:
  - Re-themed the Quarry GUI texture and layout to match the dark industrial steel aesthetic of the mod.
  - Rescaled all plates and readouts to permanently resolve text overlapping.
  - Enhanced scanning to preload chunks across the entire mining footprint and catalog **every block type and its exact count** in the $43\times 43\times 3$ clearing and $41\times 41$ deep pit down to bedrock.
  - Integrated CRT Manifest Monitor with paginated block slots, clean compact count badges, hovering tooltips (name, count, volume %), page controls, and mouse wheel scrolling.

### What's New in v1.11.23:
- **Heavy Pit Quarry 3D Monolithic Model Camera Anchor Fix**:
  - Fixed an issue where the assembled 3D quarry excavator model was rendered using `poseStack.last()` inside `submitCustomGeometry` rather than the captured lambda `PoseStack.Pose`.
  - In Minecraft 26.3's deferred render queue, this caused the 3D model to anchor to the camera / player view matrix (rendering directly on top of the player's head and screen).
  - Geometry is now correctly anchored to the in-world multiblock controller block.

### What's New in v1.11.22:
- **Heavy Pit Quarry Blueprint Hologram & Member Flexibility Fix**:
  - Aligned all multiblock blueprint requirements, in-world floating nametag holograms, and missing-component chat messages to strictly match the 3 real craftable blocks: **Heavy Pit Quarry Controller**, **Quarry Structural Casing**, and **Quarry Lattice Frame**.
  - Removed confusing internal subcomponent label names (such as "Quarry Left Conveyor Port", "Quarry Turntable Base", etc.) so players are not misled into searching for non-existent blocks.
  - Made multiblock verification flexible: all 26 member block positions now accept **either** `Quarry Structural Casing` or `Quarry Lattice Frame` interchangeably. Placing either block satisfies the structure position and clears that block from the active blueprint hologram.

### What's New in v1.11.21:
- **Heavy Pit Quarry 3×3×3 Multiblock Excavation Rig (`QuarryControllerBlock`, `QuarryCasingBlock`, `QuarryFrameBlock`, `QuarryControllerBlockEntity`, `QuarryModel`, `QuarryControllerRenderer`, `QuarryScreen`, `QuarryMenu`)**:
  - Added the **Heavy Pit Quarry**: an authentic open-pit surface mining crawler machine engineered for automated strip mining, overburden clearing, and deep pit excavation.
  - **3-Phase Automated Excavation Sequence**:
    - **Phase 0 (Surface Overburden Clearing)**: Mines an expansive $43 \times 43 \times 3$ box directly in front of the machine ($y = 0, -1, -2$), excavating a wide 3-block-deep recessed terrace bench.
    - **Phase 1 (Automated Safety Perimeter Fencing)**: Once the $43 \times 43 \times 3$ clearing is completed, the quarry automatically lines the outside perimeter of the inner $41 \times 41$ pit with safety railings (using fences supplied in the dedicated GUI fence slot, or crafting durable safety fences automatically from quarried stone).
    - **Phase 2 (Deep Pit Excavation to Bedrock)**: Excavates the inner $41 \times 41$ pit layer-by-layer downward from $y = -3$ all the way to bedrock (`level.getMinY()`). Fluid pockets (water and lava) are safely cleared, ores and minerals are harvested with diamond-pickaxe loot context, and unbreakable bedrock is safely preserved.
  - **3 Low-Profile Conveyor Export Chutes**:
    - Features 3 extended industrial discharge arms—**Rear** `(0, 0, 3)`, **Left** `(-2, 0, 1)`, and **Right** `(2, 0, 1)`.
    - Conveyor beds are constructed at standard 5px height (`y: 0..5`), aligning flush with our mod's `ConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `SlopedConveyorBeltBlock`.
    - Automatically pushes mined aggregates and ores in a round-robin load-balanced cycle directly onto adjacent conveyor belts, into item pipes, or into connected storage containers (Chests, Barrels, Hoppers).
    - 9 internal buffer slots safely store output if downstream lines are backed up.
  - **Energy Consumption & Proportional Speed Scaling**:
    - **Max Speed**: 1 block per second (20 ticks per block) requiring strictly **20 FE/tick** (400 FE total per block).
    - **Progressive Throttling**: The quarry automatically scales down speed proportionally as available energy drops below 20 FE/tick (10 FE/t = 50% speed, 5 FE/t = 25% speed), cleanly pausing when 0 FE is available.
    - Internal Energy Storage: 50,000 FE.
  - **Integrated Industrial Lighting**:
    - Dual high-intensity halogen work floodlights mounted on the upper gantry aiming forward and downward into the pit.
    - Emits in-world **light level 15** while operating, illuminating the entire mining pit.
    - Model features full-bright optic lenses and an active rotating amber hazard warning strobe beacon.
  - **Interactive Control Terminal GUI**:
    - **Radius Controls**: Interactive `[-]` and `[+]` buttons allowing the clearing radius to be adjusted down from 21 ($43\times 43$) down to 3 ($7\times 7$), with live box and pit dimension readouts.
    - **Remaining Blocks Breakdown Dashboard**: Real-time monitor displaying total remaining blocks and a paginated, item-icon list showing how many of each individual block type remains to be mined (Stone, Deepslate, Coal Ore, Iron Ore, Copper Ore, Gold Ore, Diamond Ore, etc.).
    - **Dedicated Fence Supply Slot**: Slot 9 accepts custom player fence blocks or iron bars.
  - **Monolithic 3D Model & 256×256 Texture Atlas**:
    - Built according to the Monolithic 3D Architecture with zero texture bleeding, zero z-fighting, and transformed surface normals.
    - High-detail crawler track undercarriage, stabilizer jacks, turntable deck, winch drum with braided steel wire cable, operator cabin with panoramic tinted glass, rear ballast with yellow/black hazard chevrons, and upper structural lattice gantry tower.
```
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

### What's New in v1.10.25:
- **Texture Wrapping & Control Panel UV Alignment (`ArcMelterModel`, `CastingPressModel`, `ArcCastingAssetGenerator`)**:
  - Fixed severe texture wrapping distortion and empty/black regions on the Electric Arc Melter operator control console and Casting Press PLC console.
  - Aligned UV coordinate mappings in `ArcMelterModel` and `CastingPressModel` with the generator's atlas layout: North face maps `UV_CONSOLE_FRONT` / `UV_PLC_FRONT` (`(0, 96, 64, 48)`), South/East/West/Up/Down faces map dedicated metallic casing (`(0, 144, 64, 48)`), eliminating face-bleeding of button textures over narrow edges.
- **Zero-Flicker Monolithic Rendering & Z-Fighting Resolution**:
  - Implemented 0.15px margin inset padding (`((coord + 0.15f) / 256f)`) across all quad UV calculations to eliminate bilinear edge bleeding and texture shimmering during camera rotation.
  - Eliminated all coplanar and penetrating box volumes across foundation skids, trunnions, shell walls, and operator console cabinets.
  - Converted quad normal generation to use pose-matrix transformed normals (`consumer.setNormal(pose, nx, ny, nz)`), preventing normal fighting across directional facings.
  - Cached single model instances in `ArcMelterControllerRenderer` and `CastingPressControllerRenderer` constructors to avoid per-frame allocations.
- **Developer & Modder Monolithic 3D Model Guide (`MONOLITHIC_3D_MODEL_GUIDE.md`)**:
  - Added comprehensive engineering and implementation guide for building flicker-free 3D monolithic multiblocks with procedural 256×256 texture atlases in modern NeoForge (26.3+), complete with zero-Z-fighting rules, atlas UV budgeting, member concealment, and copy-paste ready starter templates.

---

# Industrial Refinery v1.10.23

### What's New in v1.10.23:
- **Electric Arc Melter Monolithic 3D Model & 256×256 Texture Atlas (`ArcMelterModel` & `arc_melter.png`)**:
  - Redesigned the assembled 3×3×3 Electric Arc Melter (`refinery:arc_melter_controller`) into a monolithic 3D Industrial Electric Arc Furnace (EAF) featuring a rocker trunnion foundation skid, cylindrical water-cooled ribbed steel shell with vertical buckstay stiffeners, interior magnesia-carbon refractory hearth, front armored quartz slag/observation door at `(0, 1, 0)` with emissive arc glare, eccentric bottom-tapping (EBT) refractory launder spout at `(1, 0, 0)` aligning seamlessly with Open Casting Channels, rear-left high-voltage EAF transformer vault with oil-cooling radiator fins and 3 copper busbar tubes, twin hydraulic gantry mast columns, domed water-cooled roof with delta refractory insert, top flared charge hopper at `(0, 2, 1)`, and front-left operator console.
  - **Dynamic Animations**: 3 reciprocating graphite carbon electrodes in a delta triad held by copper clamp arms, incandescent glowing electrode tips, 3D jagged full-bright electric arc plasma bolts leaping into the crucible bath, dynamically rising/falling molten metal bath surface and flowing launder stream tinted to match the active molten fluid (`molten_iron`, `molten_gold`, `molten_copper`, `molten_tin`, `molten_bronze`, `molten_glass`), and input items sinking in the top charge hopper (with in-world dropped item absorption at `(0, 2, 1)`).
- **Continuous Casting Press Monolithic 3D Model & 256×256 Texture Atlas (`CastingPressModel` & `casting_press.png`)**:
  - Redesigned the assembled 3×3×3 Continuous Casting Press (`refinery:casting_press_controller`) into a monolithic 3D Heavy Hydraulic Continuous Casting & Quenching Press featuring a reinforced steel foundation bed, central water-cooled copper & refractory crystallizer mold basin at `(0, 0, 1)`, 4 precision-ground chrome tie-rod columns with castle locknuts, overhead cast-steel crown platen with crimson-amber main hydraulic cylinder and twin return boosters, front safety grille observation window at `(0, 1, 0)`, upper refractory tundish & sloped launder chute at `(0, 1, 2)`, lower `Y=0` refractory channel receiver troughs at `(0, 0, 2)` and `(-1, 0, 1)` with animated glowing molten inflow streams, right-side water-cooling manifold cabinet, motorized steel run-out roller table at `(1, 0, 0)`, and front-left PLC operator control cabinet.
  - **Dynamic Animations**: Reciprocating hydraulic ram crosshead (with diagonal hazard stripes, bronze guide bushings sliding on the 4 chrome tie-rods, chrome piston rods, and water-cooled copper upper stamping die) that accelerates downward (`0..90` ticks), holds high-pressure quench compression (`90..150` ticks), and retracts smoothly (`150..240` ticks), plus dynamic molten-to-solidified billet transition and finished cast items traveling along the right-hand run-out roller table (`(1, 0, 0)`).
- **Seamless Member Invisibility, Channel Flow & Pipe/Conveyor Connectivity**:
  - All 9 Arc Melter and Casting Press controller and member blocks transition to `RenderShape.INVISIBLE` with `Shapes.empty()` occlusion when `ASSEMBLED = true`, forwarding right-click GUI/wrench interactions to the controller and disassembling cleanly if broken.
  - Registered Energy, Item (`WorldlyContainerWrapper`), and Fluid capabilities across all assembled member blocks of both machines so Open Casting Channels, fluid/item pipes, conveyors, hoppers, and energy cables connect seamlessly at both port locations and `Y=0` casing/basin faces.
  - Added automatic finished-item export (`tryExportOutput`) on `CastingPressControllerBlockEntity` to eject cast ingots and blocks onto adjacent conveyor belts, item pipes, and containers at `(1, 0, 0)`.

---

# Industrial Refinery v1.10.21

### What's New in v1.10.21:
- **Plate Stamping Die Progression Fix (`refinery:plate_die`)**:
  - Updated `data/refinery/recipe/plate_die.json` to use 4 Steel Ingots (`#c:ingots/steel`) instead of Steel Plates (`#c:plates/steel`), resolving the circular crafting dependency where making the Plate Die required plates that could only be stamped with a Plate Die.

---

# Industrial Refinery v1.10.19

### What's New in v1.10.19:
- **Steel Gear Tag & Valve Pipe Upgrade Recipe Fix**:
  - Added `#c:ingots/steel` (`data/c/tags/item/ingots/steel.json`) and included it in `#c:ingots` so `refinery:steel_gear` resolves its ingredient tag cleanly during datapack registry loading.
  - Migrated `refinery:valve_pipe_upgrade` (`data/refinery/recipe/valve_pipe_upgrade.json`) to Minecraft 26.3 flat string shapeless ingredients, fixing datapack startup errors.

---

# Industrial Refinery v1.10.17

### What's New in v1.10.17:
- **Redstone Valve Pipe (`refinery:valve_pipe`)**:
  - Added the Redstone Valve Pipe capable of opening and closing transit for both fluids and items based on redstone signals and attached Pipe Gates.
  - Supports Normal Mode (valve closes when redstone signal is applied) and Inverted Mode (valve opens when redstone signal is applied).
  - Mode toggled seamlessly in-world by right-clicking the valve pipe with the Refinery Wrench, displaying action bar feedback and playing mechanical switch sounds.
  - Directly compatible with BuildCraft-style Pipe Gates (e.g. Tank Full, Space in Inventory, Redstone Signal) mounted on the pipe or adjacent blocks.
  - When closed, fluid pushing and item dispatch into the valve pipe are blocked, holding traveling items in place until the valve re-opens.
  - Added shaped crafting recipe (Steel Ingot + Redstone) yielding 8 valve pipes, plus a shapeless upgrade recipe converting steel pipes with redstone.
- **Classic Spool Pipe Wire Icons**:
  - Redesigned Red, Blue, Green, and Yellow Pipe Wire icons (`pipe_wire_red`, `pipe_wire_blue`, `pipe_wire_green`, `pipe_wire_yellow`) with warm golden-amber wooden spool flanges, wrapped colored wire coils, and an unspooling wire tail extending downward.

---

# Industrial Refinery v1.10.15

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

# Industrial Refinery v1.10.14

### What's New in v1.10.14:
- **Dedicated Pipe Facades Creative Tab (`itemGroup.refinery.facades`)**:
  - Moved all dynamic Pipe Facades out of the main Industrial Refinery creative tab into their own dedicated creative tab (`refinery_facades_tab`), preventing clutter in the main mod item tab.
- **Dynamic Real-Block Texture Rendering for Pipe Facade Item Models (`PipeFacadeSpecialRenderer`)**:
  - Registered a Minecraft 26.3 `SpecialModelRenderer` for `pipe_facade` and `pipe_facade_hole` items via `RegisterSpecialModelRendererEvent`.
  - Automatically samples the target block's native `TextureAtlasSprite` from `TextureAtlas.LOCATION_BLOCKS` at runtime, rendering every facade item in creative menus, player inventories, and hand models with its true block texture (Stone, Oak, Corrugated Steel, Bronze, etc.) and 3D plate thickness rather than a generic flat gray sprite.
- **Slender Horizontal Rectangular 3D 8-Bit Pipe Icons**:
  - Re-proportioned all 23 pipe item icons to slender horizontal rectangles (16px collar height, 12px shaft height, 30px width; 2.5:1 aspect ratio), matching the slender profile of classic Mario pipes.
  - Retains all crisp 8-bit details: flanged collars, top specular highlight stripes, dark notch grooves, deep body mid-tones, 1x1 checkerboard dithering, bottom bounce highlights, and centered 5x7 fluid droplet indicators for liquid pipes.

---

# Industrial Refinery v1.10.12

### What's New in v1.10.12:
- **Pipe Facade CustomRecipe Network Packet Encoding Fix**:
  - Fixed a client disconnect (`EncoderException: Failed encoding custom payload neoforge:recipe_content`) when joining a world by registering `PipeFacadeRecipe.INSTANCE` and `PipeFacadeSwapRecipe.INSTANCE` singletons with `equals`/`hashCode` for `MapCodec.unit(...)` and `StreamCodec.unit(...)`.
- **Tin Ore & Deepslate Tin Ore Loot Table Validation Fix**:
  - Updated `tin_ore.json` and `deepslate_tin_ore.json` loot tables to use the Minecraft 26.3 `"condition": "minecraft:tool/can_silk_touch"` syntax, eliminating `Unreachable entry!` validation warnings and restoring Silk Touch drops.

---

# Industrial Refinery v1.10.11

### What's New in v1.10.11:
- **Authentic 3D 8-Bit Pixel Art Pipe Item Icons**:
  - Replaced legacy blurred vector/anti-aliased pipe icons with crisp 32x32 3D 8-bit retro arcade pixel art pipe icons inspired by classic Mario pipes.
  - Features flanged collar rims on both ends with vertical rim highlights, specular top highlight stripes, grooved notch separators, deep cylindrical body shading, 1x1 checkerboard dither shading at the base, and bottom bounce specular reflection.
  - Tailored distinct material palettes across all 23 pipe types: Wood, Copper, Iron, Steel, Gold, Diamond, Emerald (reference green pipe), Brass, Cast Iron, Void, Quartz/Merge, Buffer, and Structure.
  - Added a distinct 8-bit fluid droplet emblem marking in the center of all liquid transport pipes (`wood_fluid_pipe`, `copper_fluid_pipe`, `iron_fluid_pipe`, `steel_fluid_pipe`, `gold_fluid_pipe`, `diamond_fluid_pipe`, `cast_iron_fluid_pipe`, `fluid_extractor_pipe`, and `merge_fluid_pipe`) for immediate visual distinction between item and liquid conduits in inventory screens.
  - Added dedicated status badges for utility pipes: golden extraction chevrons for extractor pipes, storage latches for buffer pipes, and swirling singularity vortexes for void pipes.

---

# Industrial Refinery v1.10.10

### What's New in v1.10.10:
- **BuildCraft-Style Pipe Facades (`refinery:pipe_facade` & `refinery:pipe_facade_hole`)**:
  - Added **Solid Pipe Facades** (`refinery:pipe_facade`) and **Pass-Through (Hollow) Pipe Facades** (`refinery:pipe_facade_hole`) that conceal pipes behind 2/16-inch (`0.125` block) structural wall panels on any of the 6 faces (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`) of any Industrial Pipe or Buffer Pipe.
  - **Live Registry Scanning & Dynamic Texture Wrapping**:
    - Automatically scans `BuiltInRegistries.BLOCK` at runtime to support any full-cube, opaque block from Vanilla, Industrial Refinery (including all 17 Corrugated Steel Panels, metal/ore storage blocks, Oil Shale, and industrial casings), and third-party mods without pre-generating static asset files per block.
    - Excludes non-solid or problematic blocks (glass, ice, leaves, grass blocks, slime/honey, TNT, redstone components, spawners, portals, liquids, falling blocks, and block entities).
    - Wraps the disguised block's native `TextureAtlasSprite`s directly onto the facade geometry on `TextureAtlas.LOCATION_BLOCKS`, preserving **LabPBR 1.3 metallic specular reflections (`_s`)** and **Parallax Occlusion Mapping (`_n`)** in Complementary Unbound and Iris shaders.
  - **Crafting & Conversion Recipes**:
    - **Solid Pipe Facade (x8)**: 1 eligible full block in the center surrounded by 8 Copper Item Pipes (`refinery:copper_item_pipe`).
    - **Solid <-> Pass-Through Conversion**: Craft any Solid Pipe Facade 1-to-1 into a Pass-Through Facade (with a centered pipe opening) and back at any time.
  - **In-World Placement & Removal**:
    - Right-click any pipe with a facade to attach it to the clicked side; Shift + Right-Click with a **Refinery Wrench** or empty hand to pop the facade off.
- **Structure Pipe (`refinery:structure_pipe`)**:
  - Added an inexpensive structural pipe (`2 Cobblestone + 1 Gravel -> 8 Structure Pipes`) with a solid cobblestone-gray finish.
  - Transfers neither items nor fluids, serving as a dedicated structural conduit for bridging **Pipe Wires** (`Red`, `Blue`, `Green`, `Yellow`), mounting **Pipe Logic Gates**, and anchoring **Pipe Facades**.

# Industrial Refinery v1.10.9

### What's New in v1.10.9:
- **Cross-Mod Conveyor Belt Transition & Item Sticking (Ole Saw Mill Compatibility)**:
  - Fixed item collision box height in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` from 4.0 px (`0.25`) to 5.0 px (`0.3125`), matching the standard 5-pixel surface height used by adjacent conveyor mods such as Ole Saw Mill (`olesawmill:sawmill_conveyor`). This eliminates the 1-pixel collision lip that previously stopped items from crossing the block seam.
  - Implemented boundary handoff release in `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity`: once an item physically crosses the forward boundary into a connected conveyor belt block, our conveyor immediately yields ownership, eliminating conflicting dual-conveyor velocity overrides and seam jitter.
  - Added forward velocity boost (`1.3x`) when items approach the exit threshold of an attached conveyor belt, guaranteeing uninterrupted gliding across block boundaries.

# Industrial Refinery v1.10.7

### What's New in v1.10.7:
- **LabPBR 1.3 & POM (Parallax Occlusion Mapping) Support for Corrugated Steel Panels**:
  - Full LabPBR 1.3 Normal Maps (`_n.png`) and Specular Maps (`_s.png`) added for all 17 Corrugated Steel Panel variants (raw steel and 16 dyed colors) across both side panels and cross-section caps (102 textures total).
  - **Parallax Occlusion Mapping (POM) Depth**: Encoded physical height profiles into the Alpha channel of `_n.png` (rib crests at 245, valleys at 36, stiffening micro-ribs at 90), rendering authentic 3D depth, sloped folds, and raymarched self-shadows in shaders like Complementary Unbound.
  - **DirectX Normal Vectors & Ambient Occlusion**: Analytically derived tangent-space normal vectors with DirectX Y-down orientation and linear contact ambient occlusion baked into the Blue channel (`B = 140-250`) for deep contact shadows in panel crevices.
  - **Metallic Specular Reflectance (LabPBR 1.3)**:
    - Raw Corrugated Steel Panel configured with Hardcoded Iron/Steel ID (`G = 230`) for physically accurate metallic Fresnel reflectance.
    - 16 Dyed Corrugated Steel Panels configured with Custom Metal mode (`G = 255`) for glossy, colored metallic specular highlights matching the panel's powder-coat color.
    - Smoothness calibrated to a crisp satin industrial metal finish (`R = 186-214`, roughness 0.16–0.27).
    - Porosity set to non-porous metal (`B = 0`), creating realistic rainwater slick reflections during rainfall.
- **Enhanced Galvanized & Powder-Coat Diffuse Textures**:
  - Implemented Voronoi crystallization zinc spangle lattices on the raw Corrugated Steel Panel diffuse map.
  - Refined specular crest highlights, shadow contrast in trough valleys, and cold-rolling mill micro-streaks across all variants.

# Industrial Refinery v1.10.5

### What's New in v1.10.5:
- **Steam Bucket Creative-Only Restriction**:
  - Gaseous steam can no longer be collected into an open bucket in Survival mode. Attempting to scoop steam plays a vapor dissipation sound (`SoundEvents.FIRE_EXTINGUISH`) without filling the bucket.
  - The Steam Bucket (`refinery:steam_bucket`) remains accessible in Creative mode for world building and testing, featuring an explanatory tooltip: *"Creative Only: Steam cannot be contained in an open bucket in Survival."*
- **Item Pipe Traveling Item Traversal & Continuous Visibility**:
  - Replaced legacy `ItemStackWithSlot` serialization with dedicated NBT `childrenList` storage for `"TravelingItems"`, fixing packet encoding failures where direction and progress bitmasks exceeded the Minecraft `UNSIGNED_BYTE` 255-slot limitation.
  - Added immediate block entity update broadcasts (`sendBlockUpdated` with flag 3) when an item exits a pipe and enters an adjacent pipe block entity, ensuring uninterrupted visual traversal through every pipe piece across the entire transport network.
  - Increased in-transit item synchronization frequency to every 2 ticks for responsive client-side progress tracking and sub-tick partial tick animation.
  - Applied minimum ambient lighting (`0x00D000D0`) to traveling items inside transparent pipe casings, ensuring items remain distinctly visible even when passing through dark, enclosed, or unlit factory environments.

# Industrial Refinery v1.10.3

### What's New in v1.10.3:
- **Recipe Syntax Migration (Minecraft 26.3 / 1.21.4+)**:
  - Migrated Corrugated Steel Panel recipes (shaped, shapeless re-dyeing, water-bucket washing, stonecutting) to Minecraft 26.3 string-based ingredient IDs (`"#c:corrugated_steel_panels"`, `"refinery:corrugated_steel_panel"`, etc.), resolving recipe registry deserialization warnings.
- **Model & Blockstate Resolution**:
  - Added missing item model for `oil_shale` (`assets/refinery/models/item/oil_shale.json`).
  - Added missing blockstate and block model for `buffer_pipe` (`assets/refinery/blockstates/buffer_pipe.json`, `models/block/buffer_pipe.json`).
  - Cleaned up `casting_input_port`, `casting_output_port`, `melter_input_port`, and `melter_output_port` blockstates by removing invalid `facing=up` and `facing=down` entries, aligning with `HorizontalDirectionalBlock`.

# Industrial Refinery v1.10.2

### What's New in v1.10.2:
- **Corrugated Steel Panels (Industrial Cladding & Siding)**:
  - Added the **Corrugated Steel Panel** (`refinery:corrugated_steel_panel`) and **16 dyed color variants** (White, Orange, Magenta, Light Blue, Yellow, Lime, Pink, Gray, Light Gray, Cyan, Purple, Blue, Brown, Green, Red, Black).
  - Modeled after industrial R-panel / standing seam corrugated steel sheet siding with 64x64 high-resolution textures, specular ridge crests, directional drop shadows in the troughs, fine stiffening micro-ribs, and metallic galvanized stippling.
  - **100% Seamless Wall & Roof Flow**: Rib spacing (16-pixel pitch, 4 major ribs per block) and micro-flutes align mathematically with zero boundary lines or edge seams, allowing walls and roofs of any height and width to tile into an uninterrupted continuous corrugated sheet metal surface.
  - **3-Axis Pillar Orientation**: Can be placed vertically (`axis=y`) for warehouse/factory siding, or horizontally (`axis=x`, `axis=z`) for metal roofing and horizontal industrial cladding. The cross-section end faces render the corrugated profile plate.
  - **Full Crafting & Dyeing Suite**:
    - Crafted with 4 Iron Ingots in a 2x2 grid -> 4 Corrugated Steel Panels.
    - Stonecutting support: 1 Iron Ingot -> 2 Corrugated Steel Panels.
    - 8 Corrugated Steel Panels around any dye -> 8 Dyed Corrugated Steel Panels.
    - Direct 1-to-1 re-dyeing with any dye.
    - Washing back to unpainted galvanized steel with a water bucket in a crafting grid.

# Industrial Refinery v1.10.1

### What's New in v1.10.1:
- **Bituminous Tar Sands & Animated Crude Oil Sync**:
  - Synchronized Crude Oil still and flowing fluid textures with animated 32x1024 and 16x512 high-resolution textures and `.mcmeta` animation timings.
  - Replaced standard Oil Sand with authentic bituminous tar sand texturing.
  - Added **Oil Red Sand** (`refinery:oil_red_sand`) with badlands red sand bituminous texture and custom reddish dust particles (`0x8C381E`).
  - Added **Oil Shale** (`refinery:oil_shale`) stone-tier bituminous ore block mineable with pickaxe.
  - Fully integrated into world generation: Oil Red Sand generates in badlands and red sand surface deposits, while Oil Shale lines subterranean deepslate and cavern oil reservoirs.
  - Both new blocks are fully supported in the Industrial Grinder and Petroleum Refinery distillation column for crude oil extraction.
- **Heavy-Duty 3D Industrial Pipe End Caps**:
  - Modeled 3D industrial pipe caps on all unconnected pipe termination ends and standalone pipes (`IndustrialPipeModel`).
  - Features an outer sleeve collar wrapping the pipe end, a circular front cap plate, and a raised central hex nut / bolt head boss for a heavy industrial appearance.
- **Pipe Item Ejection on Dead Ends & Full Containers**:
  - When traveling items in an item pipe reach a dead end (unconnected pipe terminal) or an attached container whose inventory is full or rejects the item, the items are cleanly ejected out onto the ground with realistic velocity and sound (`IndustrialPipeBlockEntity`).
  - Partial insertion leftovers are similarly ejected rather than looping or freezing inside the pipe network.

# Industrial Refinery v1.9.9

### What's New in v1.9.9:
- **Multi-Smelter Auto-Export to Conveyors & Automation**:
  - The Industrial Multi-Smelter now automatically detects conveyor belts placed in front of it (in front of the Controller or front face) or adjacent to its Output Port, and auto-exports finished smelted items (such as charcoal from wood/logs, ingots, glass).
  - Automatically verifies conveyor direction to ensure items are never sent into backward-running belts.
  - Features single-item spacing checks on conveyor belt surfaces, allowing smelted products to ride along factory lines in neatly spaced rows.
  - Also auto-exports into adjacent item pipes, chests, barrels, hoppers, and external item capabilities.
- **Metal Press & Blast Furnace Auto-Export Parity**:
  - The Metal Press now automatically exports finished stamped items (plates, rods, gears) onto conveyors or containers at its bed discharge or in front.
  - Upgraded Blast Furnace auto-export with full conveyor orientation verification and smooth item spacing.

# Industrial Refinery v1.9.8

### What's New in v1.9.8:
- **Item Pipe Traveling Item Visibility & Rendering**:
  - Implemented 360-degree clear cylindrical glass pipe bodies with heavy industrial structural collar rings for all item pipes (Wood, Stone, Iron, Gold, Diamond, Netherite, Emerald, Quartz, Obsidian, Void, and Buffer).
  - Traveling items are now fully visible inside pipes as they move between inventories.
  - Smooth partial-tick client interpolation animates items traveling between blocks seamlessly.
  - Fixed client network synchronization and added full NBT persistence for traveling items in transit.
- **Multi-Smelter GUI Alignment & Dark Gunmetal Styling**:
  - Completely restyled the Industrial Multi-Smelter GUI with dark gunmetal industrial steel styling matching the Grinder and Metal Press.
  - Aligned all 8 input and 8 output slots and the centered fuel slot with clean recessed slot wells.
  - Fixed label positioning and removed clashing text overlays.
  - Added energy gauge rendering with hover tooltip, animated combustion flame and induction heating indicators, and dedicated 2px cook progress bar grooves under each input slot.

# Industrial Refinery v1.9.7

### What's New in v1.9.7:
- **Guide Book Fix**: The Industrial Field Manual now responds to clicks (sidebar, Table of Contents, tabs, Back button, layer pills).

# Industrial Refinery v1.9.6

### What's New in v1.9.6:
- **Comprehensive Informative Pipe Tooltips**:
  - Implemented `PipeBlockItem` across all 22 industrial pipes.
  - Hovering over any pipe item now displays its functional role, fluid transfer rates (mB/t), buffer capacities, item travel speeds, and operational tips (filter GUI, wrench configuration, and pulse gate requirements).

# Industrial Refinery v1.9.5

### What's New in v1.9.5:
- **In-Game Industrial Field Manual / Guide Book**:
  - Added the craftable Industrial Field Manual (`refinery:guide_book`) with a comprehensive in-game GUI.
  - Interactive Table of Contents with category filtering across all mod machines, tools, pipes, and logistics.
  - Multiblock pages feature an exact Bill of Materials (counts and item icons) and an interactive Layer Layout Viewer with 2D layer-by-layer blueprints (`L0` to `L4`), orientation guides, cell tooltips, and recipes.

# Industrial Refinery v1.9.4

### What's New in v1.9.4:
- **Wood Extraction Pipe Filter GUI**:
  - Right-clicking a Wood Item Pipe opens a 9-slot filter inventory (3x3 grid) to configure which items are allowed to be extracted.
  - An empty filter extracts any item; when configured, only matching items are extracted.
- **Autarchic Pulse Gate Extraction Gating**:
  - Wood Item Extraction Pipes now only extract items when pulsed by an Autarchic Pulse Gate, preventing continuous unmetered passive draining.
  - Autarchic pulsar actions strictly honor the Wood Pipe filter configuration.

# Industrial Refinery v1.9.3

### What's New in v1.9.3:
- **Cross-Mod Conveyor Belt Handoff & Trapped Item Resolution**:
  - Implemented universal cross-mod conveyor detection, preventing conveyor belts from mistakenly identifying external conveyor blocks (such as Ole Saw Mill's conveyors) as static inventory endpoints.
  - Eliminated the infinite capability insertion/respawn cycle at conveyor transfer seams that caused items to bunch up and become unpickable.
  - Adjusted conveyor item surface height to $pos.getY() + 0.32$, allowing items to glide smoothly across block transitions without catching on adjacent conveyor edges.

# Industrial Refinery v1.9.2

### What's New in v1.9.2:
- **Autarchic Pulse (Item) x5 and x10 modes**: Autarchic gates can now pull 5 or 10 items at a time (instead of 1) per pulse.
# Industrial Refinery v1.9.0

### What's New in v1.9.0:
- **Multiblock Hologram Part Label Size Reduced by 60%**:
  - Centralized holographic 3D billboard label rendering via `BlueprintColors.renderBlueprintLabel` across all 13 multiblock machine renderers.
  - Floating nametags are now 60% smaller (scaled to 40% of their previous oversized dimensions), dramatically reducing visual clutter and preventing label overlap on dense multiblock matrices.
  - Precisely anchored to the top face of each projected outline box for crisp, clean readability.
# Industrial Refinery v1.8.63

### What's New in v1.8.63:
- **Petroleum Refinery Diesel Output Port Relocation**:
  - Moved the Diesel Output Port from Layer 1 (middle tier) down to Layer 0 (ground tier) at the rear of the multiblock (`(0, 0, 2)`).
  - Placed Reinforced Casing at Layer 1 (`(0, 1, 2)`) between the Diesel Port and the Gas Output Port at Layer 2 (`(0, 2, 2)`), preventing output pipes from overlapping and crowding the rear face.
  - Holographic blueprint projection now guides construction to the new ground-level port location.
# Industrial Refinery v1.8.62

### What's New in v1.8.62:
- **Arc Melter Processing Speed Slowed Another 100%**:
  - Doubled process time in `ArcMelterControllerBlockEntity` from 60 ticks (3.0s) to 120 ticks (6.0s) per batch.
  - Reduced automatic fluid output rate to 25 mB every 4 ticks, creating a steady, authentic stream pouring into casting channels.
- **Continuous Casting Press Processing Speed Slowed by 200%**:
  - Tripled process time in `CastingPressControllerBlockEntity` from 80 ticks (4.0s) to 240 ticks (12.0s) per press stroke (+200% duration).
  - Synchronized the 3D hydraulic ram stroke to span 120 ticks down and 120 ticks up, with quench sounds, anvil impact, steam clouds, and metallic ingot solidification occurring at tick 120.
- **Casting Channel Lava-Like Viscous Flow Physics**:
  - Implemented viscous flow propagation delay: molten metal holds and accumulates for 25 ticks (1.25s) before cascading downstream, mimicking thick molten lava.
  - Slowed downstream fluid transfer to 25 mB in rhythmic 4-tick intervals (6.25 mB/t).
  - While upstream actively feeds a channel line, each channel retains a minimum pool of 150 mB so the entire channel network remains visibly filled and glowing with thick molten metal during pouring, draining completely once upstream supply ceases.
  - Decreased molten fluid surface scroll velocity in `CastingChannelRenderer` from 0.012 to 0.0018 (nearly 7x slower), rendering thick, slow-creeping fluid dynamics.
- **Industrial Grinder Conveyor Auto-Export & Internal Buffering**:
  - When any conveyor belt (flat, sloped, or right-angle) is attached to the grinder (at an output port, adjacent to the right-hand discharge chute, or along the perimeter), finished crushed items are automatically fed onto the belt one at a time.
  - Eliminated the restrictive downstream-receiver check that previously rejected attached conveyors if they did not immediately terminate into a container on the next block.
  - When no conveyor, pipe, or container is attached, finished items remain safely stored inside the grinder's 4-slot internal output buffer.
# Industrial Refinery v1.8.61

### What's New in v1.8.61:
- **Sloped-to-Flat Conveyor Crest Transition & Item Sticking Resolution**:
  - Re-profiled the sloped conveyor incline curve so it reaches the full crest height of the level conveyor ($pos.getY() + 1.3125$) by $t = 0.85$, allowing items to glide horizontally across the top terminal rather than slamming into the vertical face of the flat conveyor.
  - Extended active item position and height alignment across the hand-off seam ($0.0 \le rawT \le 1.15$), maintaining smooth continuity while transitioning into the next block.
  - Implemented directional recessed entry collision shapes (`ITEM_ENTRY_SHAPES`) on flat and right-angle conveyors (`ConveyorBeltBlock` & `RightAngleConveyorBeltBlock`), leaving a 3px clearance at the incoming face to completely eliminate physical collision walls for incoming items.
  - Expanded horizontal item collection bounds on level conveyors ($\\pm0.15$ blocks) and lowered detection floor to $pos.getY() + 0.05$ with automatic upward alignment onto the belt surface ($0.3125$).
  - Eliminated downward gravity drag during level conveyor transit, setting vertical velocity to $0.0$ so items glide seamlessly across all block boundaries.

# Industrial Refinery v1.8.60

### What's New in v1.8.60:
- **2x Output Doubling via Arc Melter & Casting Press**:
  - Raw ores, ore blocks, and dusts smelted in the Electric Arc Melter yield 1,000 mB of molten metal.
  - The Hydraulic Casting Press now stamps 1,000 mB of molten fluid into **2 ingots** (Iron, Gold, Copper, Tin, Bronze) or **2 Glass blocks**, doubling the reward for processing through the foundry chain.
  - Ingot recycling is balanced: inserting ingots into the Arc Melter yields 500 mB per ingot (1,000 mB for 2 ingots), preventing infinite duplication loops.
- **Blast Furnace & Metal Press Dark Industrial GUIs**:
  - Replaced legacy vanilla light-gray GUI textures for both the Industrial Blast Furnace and Hydraulic Metal Press with heavy riveted dark steel panels (`#2E3339`), recessed dark slot wells (`#1D2126`), and beveled industrial bezels matching the rest of the machinery suite.
  - Realignment of `inventoryLabelY` to 74 for consistent spacing above player inventory slots.
- **Screen Title Alignment & Overlapping Status Text Elimination**:
  - Fixed an overlapping text collision where "ONLINE", "OFFLINE", "UNFORMED", and pump subheaders were rendered at $(x \approx 98..120, y=6)$ directly colliding with machine titles (e.g., "Industrial Rock Crusher", "Hydraulic Casting Press", "Industrial Blast Furnace").
  - Removed header text collisions across all mod container screens (`IndustrialGrinderScreen`, `ArcMelterScreen`, `CastingPressScreen`, `BlastFurnaceScreen`, `MetalPressScreen`, `FermenterScreen`, `RefineryScreen`, `SmallBoilerScreen`, `LargeBoilerScreen`, `LargeDieselBoilerScreen`, `SmallDieselBoilerScreen`, `MultiSmelterScreen`, `SteamTurbineScreen`, `StorageTankScreen`, `FeedingTroughScreen`, and pump screens), leaving title rows completely uncorrupted.

# Industrial Refinery v1.8.59

### What's New in v1.8.59:
- **Arc Melter Processing Speed Slowed by 50%**:
  - Increased process time from 40 ticks (2.0s) to 60 ticks (3.0s) per smelting cycle (+50% process duration).
- **Casting Press Processing Speed Slowed by 100%**:
  - Increased process time from 40 ticks (2.0s) to 80 ticks (4.0s) per press stroke (+100% process duration / 50% speed).
  - Synchronized hydraulic stamping die ram stroke in the renderer to span 40 ticks descent and 40 ticks ascent, with ingot solidification at tick 40.
- **Molten Channel Fluid Flow & Animation Speed Reduced**:
  - Decreased casting channel fluid transfer rate from 250 mB/t to 80 mB/t in CastingChannelBlockEntity and ArcMelterControllerBlockEntity.
  - Reduced visual molten fluid surface scrolling rate from 0.035 to 0.012 in CastingChannelRenderer for viscous molten metal appearance.

# Industrial Refinery v1.8.58

### What's New in v1.8.58:
- **Continuous Casting Press Input Port Molten Fluid Acceptance**:
  - Upgraded SingleFluidHandler with dynamic fluid setters and predicate filtering, resolving empty-tank rejection so Casting Input Ports accept molten fluids into empty internal tanks.
  - Linked CASTING_PORT_BE and CASTING_PRESS_CONTROLLER_BE fluid capabilities to dynamic setters and casting recipe filters (getCastingProduct), enabling molten metal transfer from casting channels, fluid pipes, and direct bucket interactions.
  - Added instant multiblock assembly synchronization (syncAssembly()) upon capability querying, ensuring connected channels and pipes immediately flow upon structure completion.
  - Added direct right-click bucket dumping on CastingPortBlock to fill 1,000 mB of molten fluid into the input port and return an empty bucket.
- **Pipe Wire End Wire Termination & Routing Correction**:
  - Eliminated erroneous arm propagation in PipeWireEntityRenderer when a wire terminates on an end pipe (wireCount <= 1).
  - End wires now terminate cleanly at the pipe's center node without sprouting ghost perpendicular or 45/90 arms into thin air or adjacent blocks.
- **Diesel Generator Placement Alignment & Drops**:
  - Confirmed 2-block monolithic model bounds and coordinate transformations across all four cardinal directions (NORTH, SOUTH, EAST, WEST), guaranteeing the enclosure aligns flush with placed world blocks.
  - Confirmed break behavior ensuring breaking either half of the 2x1 generator reliably drops exactly 1 item.

# Industrial Refinery v1.8.57

### What's New in v1.8.57:
- **Unlimited Casting Channel Flow Length**:
  - Eliminated the 3-channel flow limitation by implementing flow direction tracking (`lastInputSide` and `lastInputTick`). Channels now prioritize forward discharge away from the source neighbor and gravity discharge downwards, preventing fluid from bouncing back and forth between adjacent blocks.
  - Channels now inform receiving neighbor channels of the flow direction upon transfer, enabling continuous cascading molten fluid flow across arbitrarily long flumes with no length restrictions.
  - Increased channel transfer rate from 100 mB/t to 250 mB/t to match the Arc Melter output spout without pooling or backflow.
- **Animated Visible Molten Metal Fluid in Casting Channels**:
  - Replaced wireframe outline rendering in `CastingChannelRenderer` with real, solid, emissive 3D molten fluid geometry using `submitCustomGeometry` and `RenderTypes.entityTranslucent`.
  - Added real-time flowing fluid surface animation with texture scrolling across all molten fluids (Iron, Gold, Copper, Tin, Bronze, and Glass) at full bright light (15728880).
  - Added animated downward vertical pour stream geometry when channels connect downwards into molds, basins, or lower channel runs.
  - Implemented `getUpdateTag()` in `CastingChannelBlockEntity` to ensure client chunk loading properly synchronizes fluid type and volume.
- **Arc Melter & Continuous Casting Press GUI Re-alignment**:
  - Realigned all 4 input slots in `ArcMelterMenu` to `(38, 26), (56, 26), (38, 44), (56, 44)`, perfectly matching the texture slot boxes of `industrial_grinder.png`.
  - Realigned the molten metal bath gauge in `ArcMelterScreen` to the 36x36 area at `(116..152, 26..62)` with accurate level fill, fluid surface highlight, and tooltips.
  - Replaced the mismatched `metal_press.png` layout in `CastingPressScreen` with `industrial_grinder.png`, positioning the molten metal inflow basin at `(38..74, 26..62)` and the 4 stamped product output slots at `(116, 26), (134, 26), (116, 44), (134, 44)`.
  - Centered all progress bars, energy gauges, and section headers (`ORES / DUSTS`, `MOLTEN BATH`, `MOLTEN INFLOW`, `CAST PRODUCTS`).

# Industrial Refinery v1.8.56

### What's New in v1.8.56:
- **Electric Arc Melter & Continuous Casting Press Blueprint Holograms**:
  - Added sneak-right-click (Shift + Right-Click) support to both `ArcMelterControllerBlock` and `CastingPressControllerBlock` to project real-time 3D in-world holographic build guides, matching all other multiblock machines in the mod.
  - Added empty-hand right-click projection when unassembled so players without a wrench can preview multiblock construction requirements and receive structure diagnostics in chat.
  - Registered `ArcMelterControllerBlock` and `CastingPressControllerBlock` (along with `MultiSmelterControllerBlock`, `SmallDieselBoilerControllerBlock`, and `LargeDieselBoilerControllerBlock`) directly in `RefineryWrenchItem.useOn` so right-clicking with a wrench cleanly triggers holographic blueprint projection instead of block rotation.
  - Implemented `shouldRenderOffScreen()` and expanded `getRenderBoundingBox()` in `ArcMelterControllerRenderer`, `CastingPressControllerRenderer`, `MultiSmelterControllerRenderer`, and `BlastFurnaceControllerRenderer` to eliminate frustum culling when players look away from the controller block to examine the hologram structure.
  - Added `getUpdateTag()` implementation to ensure client block entities properly sync state tags upon chunk loading.

# Industrial Refinery v1.8.55

### What's New in v1.8.55:
- **Buffer Pipe (1-Slot Chest Pipe)**:
  - Added the **Buffer Pipe** (`refinery:buffer_pipe`): an industrial pipe that acts like a chest with 1 inventory slot (holding 1 item stack).
  - High-fidelity 3D cylindrical pipe model featuring sight glass inspection windows; buffered items visibly hover and rotate inside the pipe center in the world.
  - Dedicated container GUI with a single centered slot, shift-click quick move, and full player inventory access.
  - Full interoperability with NeoForge `Capabilities.Item.BLOCK` and vanilla `WorldlyContainer` / `Container`:
    - Transport item pipes seamlessly route items into the buffer.
    - Extractor pipes, wooden pipes, and Autarchic Pulsar gates pull items from the buffer.
    - Hoppers and automation insert into or extract from the buffer from any face.
  - Full Redstone Comparator support: produces analog output signal ($0$ to $15$) proportional to item stack fullness.
  - Wrench support to manually configure or disconnect individual side connections.
  - Drops buffered contents when broken in survival.
  - Shapeless crafting recipes from an Item Pipe (Steel or Iron) + a Chest.

# Industrial Refinery v1.8.54

### What's New in v1.8.54:
- **Diesel Generator Placement Alignment & Drop Duplication**:
  - Corrected East and West rotation angles in `DieselGeneratorRenderer`: inverted `EAST` from $90.0^\circ$ to $270.0^\circ$ and `WEST` from $270.0^\circ$ to $90.0^\circ$, fixing a spatial offset where the 2-block monolithic generator model rendered shifted by 2 blocks into adjacent air instead of matching its physical blocks and selection outlines.
  - Made the generator face towards the player upon placement using `context.getHorizontalDirection().getOpposite()`, matching standard Minecraft machine conventions.
  - Added a `part: left` block state property condition to `loot_table/blocks/diesel_generator.json` to prevent duplicate generator item drops when breaking either block.
  - Updated `DieselGeneratorBlock.playerWillDestroy`: in survival mode, breaking the `RIGHT` block now safely destroys the master `LEFT` block to drop the single generator item and inventory contents; in creative mode, breaking either half immediately removes its partner block cleanly without dropping items.

# Industrial Refinery v1.8.53

### What's New in v1.8.53:
- **Pipe Wire End-Wire Direction & Spurious Gate Branches**:
  - Eliminated bogus wire arm connection generation in `PipeWireEntityRenderer` that previously read `gate.getDirection()` (which returned `EAST`), causing 0.5-block long wire conduits to shoot out sideways into empty air at every gate.
  - Corrected end-wire directional continuation: when a wire terminates on a pipe that continues straight or around a corner, the wire now automatically follows the physical pipe's direction across that block instead of making an abrupt 90°/45° turn into open space.
  - Dynamically configured radial quadrant offsets (`[cx, cy, cz]`) across all 3 spatial axes ($Y$, $Z$, and $X$); on vertical pipes, wires now hug the pipe perimeter diagonally across the $X$-$Z$ plane rather than collapsing along an assumed $Z$-axis.

# Industrial Refinery v1.8.52

### What's New in v1.8.52:
- **Pipe Wire Active State Stuck Fix**:
  - Implemented dynamic pulse lease timers (`signalHoldTicks` and `wireHoldTicks`) across `PipeWireEntity` and `PipeGateEntity`. Wire signals automatically expire within 6 ticks ($0.3$s) when triggering conditions are lost.
  - Eliminated permanent one-way latching where `DATA_SIGNALS` and `wireStates` remained active indefinitely after gate actions stopped emitting.
  - Replaced accidental lever-toggle behavior in `PipeGateEntity.interact` when right-clicking gates with pipe wire items; now safely attaches the wire color to the pipe host without latching logic bits.
  - Constrained BFS wire propagation to only traverse adjacent pipe blocks that actually have that wire color installed (`PipeWireEntity.hasWire(color)`).
  - Dynamically synchronized gate screen GUI wire status indicators and `WIRE_COLOR_ON` / `WIRE_COLOR_OFF` condition triggers to live wire states each tick.

# Industrial Refinery v1.8.51

### What's New in v1.8.51:
- **Sloped-to-Flat Conveyor Transition Seam & Item Sticking Fix**:
  - Aligned the bottom surface elevation of the sloped conveyor belt to `0.3125` ($5.0$ pixels), matching the flat conveyor belt bed with zero vertical ledge.
  - Set `ItemEntity` collision boxes on flat and corner conveyor belts to $4.0$px (`Block.box(0, 0, 0, 16, 4, 16)`), preventing items from colliding with the leading edge of flat conveyors when descending from slopes.
  - Dynamically tapered downward vertical velocity to $0.0$ near the bottom terminal ($t \ge 0.85$ on down slopes) and applied a forward hand-off velocity boost ($1.3\times$).
  - Guarded vertical `setPos` positioning strictly to $0.0 \le t \le 1.0$ to ensure the sloped conveyor cleanly hands off control as items enter adjacent blocks.
  - Expanded horizontal item collection bounds on flat and right-angle conveyors by $\pm0.08$ blocks to seamlessly capture incoming items at block boundaries.

# Industrial Refinery v1.8.50

### What's New in v1.8.50:
- **Unified Sloped & Corner Conveyor Belt Controls**:
  - **Normal Right-Click** (with wrench or empty hand): Rotates conveyor direction clockwise (`NORTH` -> `EAST` -> `SOUTH` -> `WEST`).
  - **Sneak Right-Click** (Shift + Right-Click with wrench or empty hand):
    - **Sloped Conveyor Belt**: Toggles incline elevation between `UP` and `DOWN`, allowing conveyors to seamlessly carry items downhill instead of uphill.
    - **Right-Angle Conveyor Belt**: Toggles turn direction between `RIGHT` and `LEFT`.
  - Empty-hand normal right-click now rotates conveyors reliably without needing a wrench, and shift-right-click smoothly changes elevation or turn direction.

# Industrial Refinery v1.8.49

### What's New in v1.8.49:
- **Creative Fluid Pump (`refinery:fluid_pump`)**:
  - Transformed the industrial pump into a dedicated **Creative Fluid Pump**.
  - Requires zero fuel, zero electricity, and no intake target; immediately supplies infinite water (or any linked fluid) and auto-pushes up to 2,000 mB/t directly into connected pipes, tanks, and machines.
  - Fluid extraction never drains the internal buffer.
  - Removed survival recipe (`recipe/fluid_pump.json`) so it is obtainable exclusively via Creative Mode or commands.
  - Features a custom Vibrant Magenta frame styling (`0xFFD946EF`) and dedicated creative management GUI.
- **Fluid Discharge Valve 200-Block Fill Range (`refinery:fluid_discharge`)**:
  - Expanded contiguous horizontal fill radius from 48 blocks to **200 blocks** ($401 \times 401$ horizontal bounding area).
  - Raised BFS node exploration ceiling to **131,072 nodes** to traverse vast water bodies.
  - Added $O(1)$ frontier neighbor caching (`lastFillPos`) allowing the valve to place source blocks rapidly without re-scanning previously placed water.
  - Multi-placement support: places up to 4 source blocks (4,000 mB) per tick when supplied by high-throughput pumps with an enlarged 16,000 mB tank buffer.
  - Guarded against chunk thrashing via `level.isLoaded()`.
- **Doubled Pumping Throughput on Diesel & Electric (RF) Pumps**:
  - Doubled the output push rate of both Diesel Fluid Pumps and Electric (RF) Fluid Pumps from 500 mB/t to **1,000 mB/t**.
  - Doubled cycle pumping speed and volume: cycle duration reduced from 20 ticks to **10 ticks** (2x speed) and pumped amount increased from 1,000 mB to **2,000 mB** per cycle.

---

# Industrial Refinery v1.8.48

### What's New in v1.8.48:
- **Boiler Meltdown Raid Siren Alarm Countdown**:
  - Added a very loud industrial raid siren alarm (`minecraft:event.raid.horn`) across all Steam Boilers and Diesel Boilers (Small and Large) during dry-running overheat countdowns (60s / 1,200 ticks).
  - High acoustic range (16.0fâ€“20.0f volume, audible up to 250â€“320+ blocks away) for plant-wide emergency warning.
  - Urgency escalation:
    - **0sâ€“30s**: Deep warning horn every 4 seconds (pitch 0.85).
    - **30sâ€“45s**: Urgent horn blast every 3 seconds (pitch 1.0).
    - **Final 15s**: Rapid, shrieking alarm blast every 2 seconds (pitch 1.2) accompanied by billowing black smoke and hissing until water is restored, redstone is disengaged, or detonation occurs.

---

# Industrial Refinery v1.8.47

### What's New in v1.8.47:
- **Boiler Infinite Water Draw & Phantom Voiding Fix**:
  - Resolved an issue where Steam Boilers and Diesel Boilers continuously drew water from tanks and pipes even when full.
  - Dynamically bound boiler port capacity to the controller's actual tank capacity (`controller.getTankCapacity()`, 16,000 mB for small boilers, 64,000 mB for large boilers) instead of a hardcoded 64,000 mB limit.
  - Hardened `SingleFluidHandler` insertion and extraction math to measure internal fluid change, preventing handlers from reporting higher acceptance than what the machine actually stored.
  - Added immediate client block updates upon fluid transfers.

---

# Industrial Refinery v1.8.46

### What's New in v1.8.46:
- **Storage Tank Valve Isolation & Side Connection Prevention**:
  - Removed fluid capability registration and fluid machine detection from `refinery:storage_tank_casing`. Pipes routed along storage tank walls no longer connect or pull fluid through casing blocks.
  - Strict valve directionality: the **Fill Valve** (`refinery:storage_tank_fill_valve`) only accepts fluid input (`insert` only), and the **Drain Valve** (`refinery:storage_tank_drain_valve`) only outputs fluid (`extract` only).
  - Restricted auto-export scans to immediate neighbors of the Drain Valve, preventing active discharge through exterior casing walls.

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

# Industrial Refinery v1.8.23

### What's New in v1.8.23:
- **Quartz Merge Pipe (`refinery:merge_pipe`, `refinery:merge_item_pipe`, `refinery:merge_fluid_pipe`)**:
  - Selectable Output Direction: Right-clicking any face (empty hand or wrench) sets that face as the sole OUTPUT; all other connections automatically become unidirectional INPUTS.
  - Cycle through branches: Clicking the active output face cycles across connected branches with action bar status display and audio click feedback.
  - Directional Indicator: Renders an illuminated golden collar band on the selected output arm.
  - Item & Fluid Merging: All items and fluids from any input branch are routed strictly into the designated output.
  - Crafting: `[Quartz, Glass, Quartz]` -> 10x Merge Pipes. Waterproof with Tar or Slime Ball to get Merge Fluid Pipe.

# Industrial Refinery v1.8.22

### What's New in v1.8.22:
- **Void Pipe Shaped Crafting Parity & Instant Destruction**:
  - Added shaped crafting recipe: `[Lava Bucket, Glass, Lava Bucket]` producing **10x Void Pipes** (`refinery:void_pipe`). Empty buckets remain in the crafting grid upon crafting.
  - Zero-bottleneck fluid insertion capability handler accepting and destroying 100% of inserted fluid volumes without buffer limits or backlog.
  - Immediate item destruction on insertion from hoppers/containers and pipe traveling networks.
- **Pipe Gate Rotation & Saddle Hugging Fix**:
  - Corrected `WEST` (270°) and `EAST` (90°) rotation in `PipeGateEntityRenderer`, guaranteeing gate saddle clamp plates curve tightly around the pipe cylinder instead of curving backwards inside-out.

# Industrial Refinery v1.8.21

### What's New in v1.8.21:
- **Pipe Gate Saddle Hugging & Rotation Fix**:
  - Corrected `PipeGateEntityRenderer` rotational mappings for `EAST` (270°) and `WEST` (90°) faces. Gate saddle clamp plates now hug the pipe circumference snugly rather than flaring outwards/inverted into the air.
  - Extended dead-end single-arm pipe cylinders in `IndustrialPipeModel` to `0.80f` (and from `0.20f`), matching standalone pipe geometry and ensuring gates centered at `0.50f` are fully supported without hanging off the pipe end.
- **Conveyor Belt & Pipe Floor Occlusion Resolution**:
  - Overrode `getOcclusionShape(BlockState)` to return `Shapes.empty()` and `useShapeForLightOcclusion(BlockState)` to return `false` across `ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `IndustrialPipeBlock`. Chunk meshing no longer culls floor tiles underneath conveyor belts or pipes.
- **Sloped Conveyor Connection Snapping & Roller Alignment**:
  - Fixed `ConveyorBeltBlock.updateShape` to properly evaluate `isBeltMatchingFront` and `isBeltMatchingBack` with `LevelReader`, keeping horizontal conveyors connected to sloped conveyors without retracting their belts or leaving un-animated gaps.
  - Lowered roller centers in `SlopedConveyorBeltModel` (`yEntry - 0.04f`, `yExit - 0.04f`) so rollers sit cleanly beneath the belt rather than protruding through the top carrying surface.

# Industrial Refinery v1.8.20

### What's New in v1.8.20:
- **Comprehensive Pipe System (Wood, Copper, Iron, Steel, Gold, Diamond, Emerald)**:
  - 7 material tiers with dedicated models, textures, item definitions, and recipes.
  - Wood active extraction, Copper/Iron/Steel bandwidth scaling, Gold insertion prioritization, Diamond 54-slot directional filtering GUI, Emerald round-robin cyclic routing.
  - Shapeless Tar/Slime Ball fluid pipe conversion. Auto-working mod machines.

# Industrial Refinery v1.8.17

### What's New in v1.8.17:
- **Auto Harvester Directional Front-Only Harvest Geometry**:
  - Re-architected `AutoHarvesterBlockEntity` scanning coordinate mathematics to orient strictly in front of the machine's horizontal facing direction (`facing`).
  - Completely eliminated backward scanning: the harvester now scans forward distance `1 .. radius` directly in front of itself (`pos.relative(facing, forwardDist).relative(rightDir, lateralDist).above(dy)`), completely preventing the machine from ever harvesting crops, trees, or blocks behind it or directly underneath its housing.
  - The working range setting ($X$) directly controls the forward reach (1 to 9 blocks deep in front of the machine), centered laterally across $(2X + 1)$ blocks.
  - Updated `AutoHarvesterScreen` GUI label and tooltips to clearly communicate forward depth and front-only coverage (e.g. `11x5 in front`), removing confusing 360-degree radius descriptions.

# Industrial Refinery v1.8.16

### What's New in v1.8.16:
- **Auto Planter & Auto Harvester GUI Button Controls & State Sync**:
  - Resolved an issue where clicking the Power ON/OFF toggle and the working range radius `[-]` / `[+]` buttons in both the Auto Planter and Auto Harvester GUIs failed to respond or update the working parameters.
  - Fixed client/server menu button dispatch in `AutoPlanterMenu` and `AutoHarvesterMenu`: `clickMenuButton` now immediately updates `this.data` slots (slot 4 for enabled toggle, slot 5 for radius), ensuring client-side GUI state and server state remain synchronized whether the container is a client `SimpleContainer` or a server block entity.
  - Added immediate optimistic client-side UI feedback in `AutoPlanterScreen` and `AutoHarvesterScreen` mouse click handlers so button state, LED indicators, and radius dimensions update instantly upon clicking.
  - Implemented block entity network packet synchronization (`syncToClient()`, `getUpdatePacket()`, and `getUpdateTag()`) in `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity`, ensuring block entity state changes sync immediately across all connected clients.
  - Added inventory clearance (`this.items.clear()`) prior to loading items from NBT in `loadAdditional` to prevent ghost item retention.

# Industrial Refinery v1.8.15

### What's New in v1.8.15:
- **Industrial Grinder (Rock Crusher) Empty Inventory Item Rendering**:
  - Fixed an issue where the Industrial Grinder continued rendering 3D item models of the last crushed item between its spinning hopper blades even when the input inventory was completely empty.
  - Added explicit container clearing (`this.items.clear()`) in `IndustrialGrinderControllerBlockEntity.loadAdditional` prior to loading NBT items, preventing stale items from lingering on the client when slots are emptied on the server.
  - Overrode `setChanged()` to immediately synchronize block entity data packets to tracking clients whenever items are added or removed through player GUI interaction, hopper transfer, or automation piping.
  - Updated `IndustrialGrinderControllerRenderer` to explicitly clear active and output item render states when slots are empty or unpopulated.
  - Applied the same inventory load clearance fix to the Metal Press and Multi-Smelter controllers to eliminate ghost item rendering across all industrial multiblock machinery.

# Industrial Refinery v1.8.14

### What's New in v1.8.14:
- **Pipe Blank Gate (`refinery:blank_gate`)**:
  - Inexpensive, accessible gate tier (`GateTier.BLANK`) that acts as an industrial pipe blanking plate and flow stopper.
  - Attaches to any pipe face to immediately disconnect the pipe on that side, preventing adjacent pipes, tanks, or machines from joining together and stopping fluid/item transfer.
  - Neighboring pipes automatically recognize the blank and disconnect their opposing face in real time.
  - Right-clicking the blank gate provides on-screen status feedback ("Connection blocked on this side").
  - Dismantling with a wrench or mining it pops off the item and restores pipe connections.
  - Highly accessible crafting recipe: 1 Iron Ingot crafts 4 Pipe Blank Gates.
- **3D Curved Pipe Gate Saddle Geometry**:
  - All pipe gates now dynamically contour and follow the cylindrical curvature of round pipes when attached!
  - Replaced flat card rendering with `CurvedPipeGateModel`, which renders a custom 3D saddle clamp wrapping tightly around the pipe's 12-sided cylindrical profile ($R = 0.200$).
  - Dynamically calculates the pipe's orientation (curving across horizontal or vertical axes, or dome saddle across junction hubs) with sealed beveled rims and 4 corner hex mounting clamp bolts.
- **Logistics Automation Recipe Parity**:
  - Re-aligned all 8 pipe recipes to match Logistics Automation standard crafting:
    - Item/transport pipes crafted via `["MGM"]` (Material + Glass + Material) yielding 8 pipes per craft.
    - Fluid pipes craftable shapelessly from transport pipes with slime balls or petroleum tar/bitumen.
    - Extractor pipes crafted from Planks + Glass yielding 8 pipes.
    - Void pipes crafted from Obsidian + Glass + Ender Pearl yielding 8 pipes.

### Previous v1.8.13 Highlights:
- **Eliminated All Texture Flickering and Z-Fighting on Native 3D Pipes**:
  - Switched pipe render type from `RenderTypes.entityCutout` to `RenderTypes.entitySolid`, ensuring rock-solid depth buffer writing and preventing alpha-test jitter.
  - Resolved intersecting and coplanar surface Z-fighting between cylinder arms and junction fittings:
    - Cylinder arms now terminate cleanly at `0.5 ± 0.08` inside the fitting hub, preventing internal face collision and intersection while remaining fully enclosed within the hub radius (`0.228`).
    - Fixed warped and sheared non-planar quads on vertical (Y-axis) cylinders by correcting vertex pairing and counter-clockwise outward normal winding.
    - Removed redundant center junction rendering from standalone (unconnected) pipes, rendering them as clean, solid capped cylinders with zero overlapping surfaces.
    - Formatted non-degenerate latitude quads and polar fans on the junction fitting hub, eliminating zero-area quads and UV gradient artifacts.
    - Inset machine connection flanges by `0.001` to eliminate coplanar face collision with adjacent machine blocks.

### Previous v1.8.12 Highlights:
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

### Recent v1.8.20 Highlights:
- **Comprehensive Pipe System (Wood, Copper, Iron, Steel, Gold, Diamond, Emerald)**:
  - **Wood Pipes**: Act as extraction pipes, automatically extracting items and fluids from chests, containers, and external machines into the pipe network.
  - **Copper, Iron, and Steel Pipes**: Progression tier triplet where Iron and Steel dramatically increase item travel speed (1.75x and 2.5x) and fluid bandwidth (1,000 mB/t and 2,000 mB/t).
  - **Gold Pipes (Insertion)**: Automatically prioritize inserting items into adjacent inventories/machines until completely full before passing items down the line.
  - **Diamond Pipes (Sorting)**: Features an interactive 54-slot sorting GUI (9 filter slots for each of the 6 directions: Down, Up, North, South, West, East). Directs matching items to filtered routes, using unconfigured routes for overflow/defaults.
  - **Emerald Pipes (Round-Robin)**: Cycles items evenly across all connected outgoing pipe branches.
- **Crafting & Waterproofing**:
  - `[Material, Glass, Material]` in a crafting table crafts **10x Item Pipes** across all tiers.
  - Placing any Item Pipe into a crafting grid with **Tar** or a **Slime Ball** converts it into its corresponding Fluid Pipe.
  - All mod machines are natively configured to auto-work and auto-eject into any connected pipe or container without needing extraction pipes.

### Previous v1.8.19 Highlights:
- **Pipe Gate Inverted Curvature & Overhang**:
  - Corrected `PipeGateEntityRenderer` PoseStack orientation matrices for `EAST`, `WEST`, `UP`, and `DOWN` faces so curved gates hug round pipes tightly rather than curling backwards into the air.
  - Implemented exact multi-axis pipe contour detection: gates on flat cylinder end-caps render flat while gates on pipe sides curve smoothly around the pipe.
  - Extended single-arm terminal pipe cylinders in `IndustrialPipeModel` to `0.66f` (or from `0.34f`): gates centered at `0.5f` now sit fully supported on the pipe cylinder without hanging off into thin air.
  - Refined gate plate dimensions in `CurvedPipeGateModel` to `0.125f` half-width (`4x4` pixels), perfectly enclosing the gate within the pipe length.
- **Conveyor Belt Neighbor Occlusion & Transparent Floor Voids**:
  - Registered all conveyor blocks (`CONVEYOR_BELT`, `SLOPED_CONVEYOR_BELT`, `RIGHT_ANGLE_CONVEYOR_BELT`) with `.noOcclusion()` and overrode `useShapeForLightOcclusion(BlockState)`. Completely eliminates chunk mesher neighbor face culling that caused floor tiles underneath and beside conveyors to render as transparent void holes.
- **Sloped Conveyor Belt Animated Canvas Gap & Height Misalignment**:
  - Extended animated black tread canvas bounds in `SlopedConveyorBeltModel` to `z0 = 0.0f .. z1 = 1.0f`, eliminating the wide, un-animated brown slider-bed spot exposed at transitions.
  - Adjusted slope transition heights to `yLow = 0.2875f` (4.6px) and `yHigh = 1.2875f` (16 + 4.6px) to match horizontal conveyor belts seamlessly.
  - Updated `ConveyorBeltBlock.updateConnections`: horizontal conveyors now recognize adjacent `SlopedConveyorBeltBlock` (climbing and descending) and `RightAngleConveyorBeltBlock` neighbors, automatically retracting terminal end-caps to create a continuous conveyor run.

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
