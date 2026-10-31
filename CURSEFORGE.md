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

### ⚡ 2. Thermodynamic Closed-Loop Steam Power (Rankine Cycle)
- **Small (3x3x3) & Large Heavy (5x5x5) Steam Boilers**: Boil water with solid coal/coke or liquid petroleum fuels into pressurized Steam. Features authentic boiler overheating mechanics and monolithic 3D models!
- **Steam Turbine Generator (3x3x3 Multiblock)**: Converts high-pressure steam into **1,000 FE/t** of clean electricity while simultaneously condensing the exhaust steam back into pure water. Features a custom high-fidelity monolithic 3D turbine-generator skid in vibrant industrial electric blue, featuring an AC alternator, speed-reduction gearbox, curved split casing with bolted horizontal split line, flanged steam inlet nozzle, and upright control console with analog dial gauges and emergency stop controls.
- **Closed-Loop Conservation**: Pipe the turbine's condensed water directly back into your boiler to build completely closed-loop, self-sustaining power plants!

### ?? 3. Heavy Metallurgy & Grinding
- **Industrial Blast Smelter (5x5x5 Multiblock)**: Massive foundry with a continuous 3x3 industrial glass viewing window. Smelts iron ingots / blocks and coal / coal blocks into structural **Steel Ingots**.
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
`refinery-1.8.10.jar`

### Display Name
`Industrial Refinery 1.8.10 (NeoForge 26.3)`

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
# Industrial Refinery v1.8.10

### What's New in v1.8.10:
- **Logistics Automation Ultra-Realistic 3D Monolithic Pipe Pack**:
  - Full high-definition visual upgrade pack for the Logistics Automation (`logistics`) pipe network.
  - **Photorealistic 64x64 PBR Textures**: Replaces legacy textures with brushed anisotropic metals, ambient occlusion depth, hex socket screws, and crystalline viewports preserving live item/fluid transit visibility.
  - **LabPBR 1.3 Shader Standard**: Normal displacement maps (`_n.png`) and specular/roughness maps (`_s.png`) provide real-time 3D relief, metal reflections, and illuminated glows with modern shader packs.
  - **3D Monolithic Block Models**: Chamfered 12-sided junction hubs, reinforced corner brackets, physical connection flanges with hex bolts, and mid-segment clamp sleeves.
  - **Standalone Downloads**: Kept unbaked from the mod jar as standalone downloads with proper forward-slash zip structures (`resourcepacks/Logistics-UltraRealistic-3D-Pipes.zip`, `MC1.21.1.zip`, and `MC1.20.1.zip`), eliminating pack incompatibility warnings.

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
