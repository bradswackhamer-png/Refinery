# Changelog — Industrial Refinery

All notable changes to the **Industrial Refinery** mod are documented in this file.

---

## [1.8.47] - 2026-11-01

### Fixed
- **Steam Boiler Infinite Water Draw & Phantom Fluid Voiding**:
  - Fixed a critical bug where Steam Boilers and Diesel Boilers continuously drained connected water tanks and pipes even when their internal water tanks were 100% full.
  - The boiler port capability handler (`BOILER_PORT_BE`) previously hardcoded a 64,000 mB capacity for all boilers regardless of boiler size. When attached to Small Boilers (16,000 mB capacity), the handler reported 48,000 mB of remaining space, accepting fluid transactions and falsely returning full acceptance to upstream pipes while the boiler controller clamped and discarded the excess water.
  - Port fluid handlers now dynamically query the controller's true capacity (`controller.getTankCapacity()`).
  - Hardened `SingleFluidHandler.insert(...)` and `extract(...)` to measure actual difference in stored fluid before and after mutation, guaranteeing handlers never return higher insertion values than what was actually accepted by the container.
  - Added real-time client block updates and synchronization (`syncToClient()`) upon fluid insertion and extraction through boiler ports.

---

## [1.8.46] - 2026-11-01

### Fixed
- **Storage Tank Valve Isolation & Side Connection Prevention**:
  - Removed fluid capability registration from `refinery:storage_tank_casing`. Liquid pipes running adjacent to storage tank perimeter walls will no longer create unwanted branch connections or pull fluid directly through casing blocks.
  - Enforced strict unidirectional valve roles: the **Fill Valve** (`refinery:storage_tank_fill_valve`) only accepts fluid input (`insert` only, `extract = 0`), and the **Drain Valve** (`refinery:storage_tank_drain_valve`) only outputs fluid (`extract` only, `insert = 0`).
  - Restricted the multiblock tank's auto-export scanning to only target blocks directly adjacent to the Drain Valve itself rather than scanning the entire outer casing perimeter and bottom floor.

---

## [1.8.45] - 2026-11-01

### Changed
- **Pipe Fluid Capacity Reduction (250â€“500 mB) & High-Speed Throughput**:
  - Reduced the internal fluid capacity across all industrial pipes from 1,000â€“8,000 mB down to **250â€“500 mB** per block (Wood/Cast Iron: 250 mB, Copper: 300 mB, Brass: 350 mB, Iron: 400 mB, Gold/Quartz: 400 mB, Diamond/Emerald: 500 mB, Steel: 500 mB).
  - High-throughput transfer rates scaling by material tier (Copper: 250 mB/t, Iron: 500 mB/t, Gold: 800 mB/t, Steel: 1,500 mB/t). Fluids travel rapidly downstream to boilers, tanks, and machines without hoarding dozens of buckets inside pipeline runs.
  - Clamped deserialized fluid amounts on chunk load to ensure existing pipes cleanly conform to new capacity limits.

---
## [1.8.44] - 2026-11-01

### Fixed
- **Diamond Sorting Pipe Filter Retention Across Server Restarts**:
  - Fixed an issue where items placed into the 54 direction filter slots of the Diamond Sorting Pipe (
efinery:diamond_item_pipe) disappeared after server restart or chunk reload.
  - In loadAdditional, calling clearContent() on SimpleContainer truncated its internal NonNullList to size 0, causing ContainerHelper.loadAllItems to discard every filter slot due to isValidInContainer size checks.
  - Added dedicated DiamondFilters NBT list serialization via ItemStackWithSlot.CODEC with backward-compatible fallback, pre-sizing to 54 slots on load without truncating the container.
  - Added preRemoveSideEffects to drop filter contents when the Diamond Pipe is dismantled, and real-time block update broadcasting on setChanged().

---
## [1.8.43] - 2026-11-01

### Fixed
- **Loading Terrain Deadlock with C2ME (TinRetrogenHandler)**:
  - Removed synchronous ChunkEvent.Load ore injection in TinRetrogenHandler.
  - In instances with concurrent chunk managers like C2ME, querying or modifying blockstates via serverLevel.getBlockState(...) inside chunk load events triggers recursive blocking chunk requests (ServerChunkCache.getChunkBlocking), deadlocking the terrain loading phase.
  - Subterranean Tin Ore generation is cleanly handled through Minecraft's native worldgen feature pipeline (worldgen/feature/ore_tin.json, worldgen/placed_feature/ore_tin.json, and 
eoforge/biome_modifier/add_tin_ore.json).

---
## [1.8.42] - 2026-11-01

### Fixed
- **Minecraft 26.3 World Loading Crash (Unbound values in registry ... [refinery:ore_tin])**:
  - In Minecraft 26.3 (1.21.4+), configured features are resolved via minecraft:worldgen/feature (loaded from data/<namespace>/worldgen/feature/), replacing legacy configured_feature.
  - Moved ore_tin.json from worldgen/configured_feature/ to worldgen/feature/ and inlined its target configuration to match 26.3's feature codec.
- **Language File Conflict Resolution (en_us.json)**:
  - Resolved merge conflict markers in ssets/refinery/lang/en_us.json that triggered a MalformedJsonException during Jade and client language loading.

---

## [1.8.41] - 2026-11-01

### Fixed
- **Minecraft Bootstrap Block Registration Crash (Block id not set)**:
  - In NeoForge 26.3.0.48+, blocks strictly require a ResourceKey assigned via Properties.setId(...) provided by NeoForge's 
egisterBlock(name, func) callback parameter p.
  - Updated TIN_ORE, DEEPSLATE_TIN_ORE, RAW_TIN_BLOCK, TIN_BLOCK, and BRONZE_BLOCK to chain onto p directly rather than creating detached copies with ofFullCopy(...).

---

## [1.8.40] - 2026-11-01

### Added
- **Industrial Diesel Generator (1x2x2 Compact Power Plant)**:
  - Added the **Industrial Diesel Generator** (`refinery:diesel_generator`): a heavy-duty containerized sound-attenuated diesel generator that produces FE/RF power from refined Diesel (and Biofuel/Ethanol).
  - **Compact 2-Block Footprint (Non-Multiblock)**:
    - Placed as a single item that intuitively forms a 2-block wide footprint ($1\times 2\times 2$ max boundary) with Left (Master Controller) and Right (Slave) sections.
    - No multiblock assembly, blueprint projections, or separate frame blocks required; simply place and power.
    - Automatic breaking synchronization: destroying either block cleanly drops 1 generator item and empties internal inventory.
  - **High-Fidelity 3D Monolithic Model & POM Shading**:
    - Modeled faithfully after heavy commercial generator skids (Triton / CAT / Cummins style).
    - Heavy black C-channel steel skid base with forklift tine pockets, yellow tie-down tags, and sub-base fuel tank.
    - Off-white / light cream powder-coated acoustic weather canopy (`#E4E2DA`) with beveled drip-edge roof and crane lifting eye bolt brackets.
    - Recessed digital operator control panel with glowing cyan LCD telemetry readout, red mushroom E-Stop button, and warning placards.
    - Bold black **TRITON** power logo, **Blue Tech ECO Products** green leaf badge, and **TIER 4 FINAL** eco rating ribbon.
    - Access doors with black industrial hinges, black recessed paddle handles, and stamped ventilation louvers.
    - Rear high-voltage terminal connection box with yellow 480V danger flash decal.
    - Exhaust muffler stack with dynamic rain-cap flapper that tilts open when the engine is firing.
  - **Power Generation & Fluid Telemetry**:
    - **Generation Rate**: Produces **256 FE/t** of continuous electrical energy.
    - **Capacity & Buffer**: Internal 8,000 mB (8 buckets) diesel fuel tank and 500,000 FE capacitor.
    - **Fuel Economy**: 1 mB of diesel yields 1,000 FE (1 bucket provides 4,000 ticks / 200s of continuous 256 FE/t power).
    - **Auto-Push Energy Output**: Automatically scans all adjacent faces across both blocks to push up to **1,024 FE/t** into connected cables, machines, or energy grids.
    - **Smart Idling**: Shuts off fuel consumption when the internal energy buffer is full, eliminating fuel waste when machines downstream are satisfied.
    - **Configurable Redstone Control Modes**:
      - `ALWAYS ON`: Runs whenever fuel and capacitor space are available.
      - `ACTIVE HIGH`: Requires a redstone signal to start generating.
      - `ACTIVE LOW`: Operates as an automated emergency backup generator when grid/redstone power drops.
    - Supports direct bucket interaction (right-click with diesel bucket to fill without opening GUI), plus fluid pipes connecting to any face.
  - **Interactive Telemetry GUI**:
    - Full container GUI with animated diesel fuel level, energy buffer bar, digital status monitor ("ONLINE", "GENERATING", "STANDBY", "NO FUEL"), and a one-click redstone control toggle button.

---

## [1.8.39] - 2026-11-01

### Added
- **Tin Ore, Deepslate Tin Ore & Raw Tin Generation**:
  - Re-introduced subterranean **Tin Ore** and **Deepslate Tin Ore** naturally generating across all Overworld biomes.
  - Matches vanilla Copper Ore rarity and distribution:
    - 16 veins per chunk.
    - Triangle distribution between $Y = -16$ and $Y = 112$ (peaking at $Y = 48$).
    - Vein size: 10 ore blocks.
    - Replaces stone with Tin Ore and deepslate with Deepslate Tin Ore.
  - Added retroactive ore generation (`TinRetrogenHandler`): automatically populates newly loaded chunks in pre-existing worlds so exploration in existing saves yields tin veins without needing world regeneration.
  - Added **Raw Tin** drop item with Fortune and Silk Touch support, plus **Block of Raw Tin** for compact 9x storage.
  - Added **Block of Tin** and **Block of Bronze** for full storage block compression/decompression.
  - Full Minecraft 26.3 item definition architecture (`assets/refinery/items/<id>.json`), blockstates, models, textures, and common tag registrations (`#c:ores/tin`, `#c:raw_materials/tin`, `#c:storage_blocks/*`).

### Changed
- **Bronze Forging Recipe**:
  - Updated shapeless alloy crafting recipe: **1 Copper Ingot + 1 Tin Ingot $\rightarrow$ 2 Bronze Ingots** (and **1 Copper Dust + 1 Tin Dust $\rightarrow$ 2 Bronze Dust**).
  - Compatible with common tag ingots and dusts (`#c:ingots/copper`, `#c:ingots/tin`, `#c:dusts/copper`, `#c:dusts/tin`).
- **Metallurgy & Processing Integration**:
  - **Electric Arc Melter**: Now melts `Raw Tin`, `Tin Ingot`, `Tin Ore`, `Deepslate Tin Ore`, and `Bronze Ingot` into `Molten Tin` and `Molten Bronze`.
  - **Casting Press**: Casts `Molten Tin` into `Tin Ingots` and `Molten Bronze` into `Bronze Ingots`.
  - **Metal Press**: Forges `Tin Ingot` $\rightarrow$ `Tin Plate` and `Bronze Ingot` $\rightarrow$ `Bronze Plate`.
  - **Industrial Grinder**: Crushes `Tin Ore`, `Deepslate Tin Ore`, and `Raw Tin` into 2x `Tin Dust`, and `Tin Ingot` / `Bronze Ingot` into their respective dusts.
  - Furnace and Blast Furnace smelting/blasting recipes for Raw Tin, Tin Ore, and Deepslate Tin Ore into Tin Ingots.

---

## [1.8.38] - 2026-11-01

### Fixed
- **Pipe State & 3D Elbow/Corner Connection Retention on Server Restart**:
  - Fixed a critical deserialization bug in `IndustrialPipeBlockEntity`: the `BlockEntityType` factory constructor `IndustrialPipeBlockEntity(BlockPos pos, BlockState state)` hardcoded `IndustrialPipeType.FLUID` and `IndustrialPipeMaterial.STEEL`.
  - When chunks were reloaded from disk on server restart or transmitted to the client, all item pipes (e.g., Copper, Iron, Steel, Wood, Gold, Diamond, Emerald) were mistakenly instantiated as fluid pipes.
  - As a result, subsequent connection updates evaluated `pipeType.handlesItems()` as `false`, causing pipes to sever their connections to item machines (including conveyor belts, hoppers, chests, ports, and chutes) and visually revert from 3D corner elbows to straight, dead-end capped runs.
  - Updated the 2-argument constructor and getters to dynamically resolve `pipeType` and `material` directly from `state.getBlock()`, ensuring pipes retain their exact type, material, fluid capacity, and item behavior across chunk loads and server restarts.
  - Guarded against block entity initialization races during world startup: if a neighbor block possesses a block entity (`targetState.hasBlockEntity()`) that has not finished instantiating (`level.getBlockEntity(targetPos) == null`), existing saved connections are safely preserved.
  - Added explicit `PipeType` and `Material` NBT tags in `saveAdditional()` for robust save-game persistence.

---

## [1.8.37] - 2026-11-01

### Changed
- **Pipe Texture Cleanup & Markings Removal**:
  - Removed circular flange and bolt emblem markings that were previously baked into the top-left region of the pipe texture generator (`PipeAssetGenerator.java`).
  - Regenerated all 12 pipe textures (`steel`, `iron`, `copper`, `wood`, `gold`, `diamond`, `emerald`, `brass`, `cast_iron`, `void`, `quartz`) to have clean, seamless, uninterrupted cylindrical metal shading.

### Added
- **True 1/8-Diameter Clear Sight-Glass Inspection Strips**:
  - Engineered dedicated longitudinal apertures on all 4 cardinal faces of pipe cylinders of width exactly $\frac{1}{8}$ of the pipe diameter ($0.050$ blocks wide).
  - Metal walls now terminate cleanly at the strip borders, allowing an unobstructed line of sight straight into the hollow pipe interior without opaque depth buffer occlusion.
  - Across the apertures, translucent sight-glass quads with metallic bevel edge frames (`pipe_window.png`) render flush with the pipe surface with zero z-fighting and no protruding ridges.
  - Live traveling items and colored fluid levels are now 100% visible moving and flowing through the pipe from all viewing angles.

---

## [1.8.36] - 2026-11-01

### Fixed
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

## [1.8.35] - 2026-11-01

### Fixed
- **Multiblock Storage Tank Auto-Export & Pipe Connectivity**:
  - Fixed an issue where multiblock liquid storage tanks failed to auto-export fluids to connected pipes even though fluid reception via the top fill valve functioned properly.
  - Resolved an internal back-drain loop on the drain valve (`STORAGE_TANK_DRAIN_VALVE_BE`): replaced the bidirectional handler with an extract-only handler (`getExportOnlyHandler()`) that rejects fluid insertion (`isValid = false`, `insert = 0`). Connected pipes receiving pushed fluid can no longer immediately pump the fluid backward into the tank, allowing fluid to freely flow downstream.
  - Registered `Capabilities.Fluid.BLOCK` on all multiblock tank casing blocks (`STORAGE_TANK_CASING`), delegating fluid extraction to the assembled tank controller.
  - Updated `IndustrialPipeBlockEntity` connection detection to recognize block-level capabilities on blocks without block entities (such as `StorageTankCasingBlock`), allowing fluid pipes to cleanly connect with visible 3D coupling arms to any casing face around the tank.
  - Expanded `StorageTankBlockEntity.pushFluidToNeighbors()` from checking solely beneath the bottom center drain valve to scanning all exterior adjacent block positions around the entire tank floor and perimeter walls, caching valid fluid handlers and pushing up to 1,000 mB/t to any connected pipe network.
  - Added instantaneous connection refresh on `StorageTankCasingBlock` and `StorageTankDrainValveBlock` neighbor changes so newly placed pipes immediately receive exported fluid without delay.

---

## [1.8.34] - 2026-11-01

### Fixed
- **Boiler Redstone Shutdown & Meltdown Prevention**:
  - Fixed a critical safety bug where solid fuel boilers (`SmallBoilerControllerBlockEntity`, `LargeBoilerControllerBlockEntity`) failed to shut down when their redstone signal was turned off, causing active boiling and overheating to continue until catastrophic explosion when water was exhausted.
  - Active boiling and steam generation in solid fuel boilers now strictly require both fuel combustion (`burnTime > 0`) and an active redstone signal (`redstonePowered`), pausing active combustion immediately when the redstone lever or signal is switched off.
  - While redstone is unpowered or fuel is depleted, boilers enter idle cooldown mode: `isActive` is set to false, firebox flames are extinguished, steam and water consumption halts, and any accumulated overheat ticks decrease steadily.
  - Updated `SmallDieselBoilerControllerBlockEntity` and `LargeDieselBoilerControllerBlockEntity` to reliably shut down when unpowered, accelerating cooldown dissipation when idle and turning off hearth firebox flame blocks.
  - Enhanced multiblock redstone signal detection (`hasRedstone()`): boilers now monitor redstone power across the controller block as well as all structure blocks and ports (water input, fuel input, steam output), allowing players to switch boilers on/off from anywhere on the multiblock.
  - Synchronized GUI status and hearth viewport rendering to accurately reflect redstone shutdown (displaying `ONLINE` instead of `BURNING` or `OVERHEATING` when unpowered).

---

## [1.8.33] - 2026-10-31

### Added
- **Continuous 3D Surface-Mounted Pipe Wires**:
  - Replaced the legacy floating 2D wire item icons with authentic continuous 3D surface-mounted wire conduits in `PipeWireEntityRenderer`.
  - When pipe wires (Red, Blue, Green, Yellow) are attached to adjacent pipes in a network, the wires now dynamically trace along the outer cylinder of the pipes and connect seamlessly across pipe block boundaries without gaps.
  - Dedicated 4-Quadrant Conduit Geometry: Each of the 4 wire colors occupies a dedicated diagonal quadrant along the pipe's outer cylinder, allowing all 4 wire lines (Red, Blue, Green, Yellow) to run parallel along the same pipe simultaneously without colliding with each other or occluding gate faces and sight glass strips.
  - 3D Wire Hub Collars & Corner Routing: Smoothly connects multi-way branch intersections, right-angle pipe corners, and attached gate terminal bases.
  - Signal Illumination: Active wire signals emit vibrant full-bright glowing neon light (`0x00F000F0`).

---

## [1.8.32] - 2026-10-31

### Fixed
- **Sloped Conveyor Belt Item Ascent & Collision Resolution**:
  - Fixed an issue where items traveling on conveyor belts got permanently stuck at the bottom of sloped conveyors (`refinery:sloped_conveyor_belt`).
  - Replaced the rigid $16\times 16\times 16$ solid cube collision shape in `SlopedConveyorBeltBlock` with directional stepped collision geometry for players/mobs and `Shapes.empty()` for `ItemEntity`, allowing items to freely enter and glide up the slope without hitting vertical block collision walls.
  - Locked item vertical positioning directly to the inclined surface height (`targetY = yEntry + t * 1.0`), preventing gravity from pulling items downward beneath the conveyor.
  - Implemented 45-degree upward velocity matching the belt speed so client-side interpolation renders smooth 60fps item transit.
  - Updated `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity` boundary checks to recognize `SlopedConveyorBeltBlock` (both directly adjacent and stepped below) so items are handed off smoothly into slopes instead of attempting container insertion.

---

## [1.8.31] - 2026-10-31

### Changed
- **Industrial Grinder / Crusher Output Conveyor Downstream Validation**:
  - The Industrial Grinder (`refinery:industrial_grinder_controller`) now verifies downstream connectivity before discharging products onto an adjacent conveyor belt (`ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`).
  - Output items will only export onto an adjacent conveyor if that conveyor is actively connected to another conveyor, an item pipe (`IndustrialPipeBlock` / `IndustrialPipeBlockEntity`), or a receiving machine/container. If a conveyor line terminates in open air or dead-ends without a downstream recipient, items remain safely stored inside the grinder's internal output slots.
  - Added belt vacancy detection: the grinder emits 1 item at a time onto the receiving conveyor belt only when the receiving belt section is clear, preventing items from stacking up in excessive entity clusters.

### Fixed
- **Industrial Grinder Input Hopper Collection Bounding Box**:
  - Tightly bounded the top feed hopper collection box (`hopperBox`) in `IndustrialGrinderControllerBlockEntity` to strictly cover the open funnel mouth ($Y \in [2.20, 3.60]$ above base, horizontal radius $0.40$ blocks centered at $(0, 2, 1)$).
  - Eliminated the oversized $1.25\times 0.85\times 1.25$ bounding volume inflation that previously allowed the input hopper to reach downward into $Y=1..0$ and rightward into $X=1$, which was sucking items right back off the output hopper, output chute, and discharge conveyor.
  - Cleaned up export targets to remove redundant controller block positions, ensuring auto-export exclusively feeds from the output port and right-hand discharge chute.

---

## [1.8.30] - 2026-10-31

### Fixed
- **Fluid Pump Back-Draining & Pipe Leak Resolution**:
  - Made the pumped fluid output reservoir on all fluid pump variants (`AbstractFluidPumpBlockEntity`, `ElectricFluidPumpBlockEntity`, `DieselFluidPumpBlockEntity`, `CoalFluidPumpBlockEntity`) strictly extract-only (`insert = 0`, `isValid = false`). Connected fluid pipes, machines, and tanks can now pull pumped fluid out or receive pushed fluid, but can never push fluid backward into the pump reservoir.
  - Eliminated the continuous push-and-drain loop between fluid pipes and the pump that previously caused the Electric Fluid Pump (`refinery:electric_fluid_pump`) to burn 400 FE every second even when the pipe network was full.
  - Decremented `pumpCooldown` cleanly every tick regardless of buffer capacity, ensuring the pump idles in `[TANK FULL]` with zero energy consumption when the output network is backed up.
  - Overrode `onEnergyChanged()` in `ElectricFluidPumpBlockEntity.energyStorage` so the internal buffer syncs immediately upon receiving energy from power cables and grid handlers, and increased `maxInsert` to 1,000 FE/t.
  - Fixed a `ClassCastException` in `DieselFluidPumpBlockEntity.getFluidHandler` where the output handler was cast to `SingleFluidHandler`, crashing or corrupting fluid connections. Introduced a dedicated `DieselFuelHandler` supporting pipe insertion of Diesel, Gasoline, and Biofuel/Ethanol into the engine fuel tank while keeping the pumped water output tank separate and extract-only.

---

## [1.8.29] - 2026-10-31

### Changed
- **Automated Planter Sapling Spacing**:
  - The Automated Planter (`refinery:auto_planter`) now naturally spaces tree saplings so no sapling is planted directly adjacent to another sapling or tree trunk.
  - When scanning and planting saplings, the planter verifies horizontal Chebyshev adjacency ($\Delta x \in [-1, 1], \Delta z \in [-1, 1], \Delta y \in [-1, 3]$) to ensure no other `SaplingBlock`, `#minecraft:saplings`, or `#minecraft:logs` is directly neighboring the planting spot.
  - Standard farmland crops (wheat, carrots, potatoes, beetroots, melon/pumpkin stems) continue to be densely planted across all farmland blocks without restriction.

### Fixed
- **Pipe Gate Placement Snapping on Occupied Faces**:
  - Improved `PipeGateItem.resolveBestPipeFace` so clicking on a pipe face that is already occupied by an attached gate automatically redirects to the closest open, unoccupied radial face facing the player instead of failing to place.

---

## [1.8.28] - 2026-10-31

### Added
- **Pipe Sight-Glass Inspection Strips & Flow Visualization**:
  - Added longitudinal sight-glass inspection strips along the lengths of all pipe arms.
  - Pipe contents and traveling items are visible moving directly through the pipe axis behind the glass strip.
  - Fluid pipes render an authentic fluid core colored to match the stored liquid (Crude Oil, Diesel, Gasoline, Water, Lava, Ethanol, Steam, etc.), dynamically showing current fill volume and fluid movement.

---

## [1.8.27] - 2026-10-31

### Fixed
- **Pipe Gate 3D Saddle Orientation, Radial Face Snapping & Curvature Alignment**:
  - Fixed `PipeGateEntityRenderer.submit` rotations for `WEST` (`270°`), `EAST` (`90°`), `UP` (`270°`), and `DOWN` (`90°`) so the front LED face (`+Z` in model space) always points outward and the concave saddle (`-Z` in model space) always wraps inward around the pipe on all 6 faces (fixing the inverted/inside-out grey back-grid appearance on `WEST`, `EAST`, `UP`, and `DOWN` faces).
  - Unified local-X / local-Y pipe axis mapping in `PipeGateEntityRenderer.extractRenderState` so gates on vertical pipes, horizontal pipes, and 3-way T-junctions always curve perpendicular to the cylinder axis.
  - Added smart radial face snapping in `PipeGateItem.resolveBestPipeFace` and automatic live correction in `PipeGateEntity.tick()`: clicking a pipe's flat end-cap or active connection arm automatically snaps the gate onto the nearest open radial side of the pipe cylinder facing the player.
  - Allowed `PipeGateItem` and `PipeWireItem` right-clicks to pass through on Quartz Merge Pipes and Diamond Sorting Pipes so gates and wires can be attached directly without opening the filter GUI or cycling the merge output.

---

## [1.8.26] - 2026-10-31

### Added
- **Diamond Pipe 3D Color-Coded Connection Bands & Whitelist GUI Legend**:
  - Added 3D color-coded collar bands (`renderDiamondSortingBands` in `IndustrialPipeModel`) around the ends of every active connection on Diamond Pipes (`refinery:diamond_item_pipe` and `refinery:diamond_fluid_pipe`), corresponding directly with the 6 directional filter rows in the right-click whitelist GUI:
    - **DOWN**: Green (`#22C55E`)
    - **UP**: Yellow (`#EAB308`)
    - **NORTH**: Blue (`#3B82F6`)
    - **SOUTH**: Red (`#EF4444`)
    - **WEST**: Purple (`#A855F7`)
    - **EAST**: White (`#F8FAFC`)
  - Enhanced `DiamondPipeScreen` with bold color-coded row bands, slot row tinting, left-hand directional badge tabs (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`) with matching color swatches, and descriptive hover tooltips.

---

## [1.8.25] - 2026-10-31

### Fixed
- **Minecraft 26.3 GUI Mouse Click Handling (`InputConstants.MOUSE_BUTTON_LEFT = 1`)**:
  - Fixed unresponsive GUI buttons in `AutoHarvesterScreen`, `AutoPlanterScreen`, `CoalFluidPumpScreen`, `DieselFluidPumpScreen`, `ElectricFluidPumpScreen`, `StorageTankScreen`, and `GateScreen`.
  - In Minecraft 26.3, `InputConstants.MOUSE_BUTTON_LEFT` is `1` and `MOUSE_BUTTON_RIGHT` is `3` (1-indexed SDL3 mouse button constants instead of legacy 0-indexed GLFW `0` and `1`). Updated all custom screen `mouseClicked` handlers to check `InputConstants.MOUSE_BUTTON_LEFT` (`1`) for left-clicks and `InputConstants.MOUSE_BUTTON_RIGHT` (`3`) for right-clicks.
- **Minecraft 26.3 GUI Text Alpha Visibility & Harvester/Planter Texture Cleanup**:
  - Added full opaque alpha bits (`0xFF000000`) to all 24-bit hex text colors in `extractLabels` across all custom machine screens (`AutoHarvesterScreen`, `AutoPlanterScreen`, `CoalFluidPumpScreen`, `DieselFluidPumpScreen`, `ElectricFluidPumpScreen`, `StorageTankScreen`, `FeedingTroughScreen`, `ArcMelterScreen`, `BlastFurnaceScreen`, `CastingPressScreen`, `FermenterScreen`, `IndustrialGrinderScreen`, `LargeBoilerScreen`, `LargeDieselBoilerScreen`, `MetalPressScreen`, `MultiSmelterScreen`, `RefineryScreen`, `SmallBoilerScreen`, `SmallDieselBoilerScreen`, `SteamTurbineScreen`) and Gate trigger/action labels (`GateTrigger`, `GateAction`), restoring visibility in Minecraft 26.3's `GuiGraphicsExtractor.text` renderer.
  - Repositioned the Harvester and Planter live status label (`SCANNING` / `HARVESTING` / `PLANTING` / `DISABLED`) neatly below the coverage area indicator so it does not overlap the header title.
  - Cleaned a legacy dark square artifact from `auto_harvester.png` and `auto_planter.png` behind the radius display and `[+]` button.

---

## [1.8.24] - 2026-10-31

### Fixed
- **Auto Harvester Client GUI Synchronization & Clean Merge**:
  - Successfully verified and merged PR #1 (`GUI-Fix`), synchronizing client and server state when toggling harvester power or adjusting working radius (+/-) buttons.
  - Resolved merge duplication by removing redundant `getUpdateTag` / `getUpdatePacket` methods and duplicate imports in `AutoHarvesterBlockEntity.java`, restoring clean compilation.
- **Pipe Gate Rotation & Saddle Alignment**:
  - Corrected `PipeGateEntityRenderer` rotational mappings for `WEST` (90°), `EAST` (270°), `UP` (90°), and `DOWN` (270°) faces. Curved pipe gate saddles now wrap snugly around round pipe cylinders rather than curving backwards inside-out.

## [1.8.24] - 2026-11-01

### Fixed
- **Complete In-Game Localization & Translation Key Fix (`en_us.json`)**:
  - Resolved missing item names and raw translation keys (such as `item.refinery.coal_dust`, `tooltip.refinery.coal_dust.desc`) displayed in inventory tooltips and GUI containers.
  - Added user-facing item translations and hover tooltips for all metallurgy and material items:
    - **Dusts**: Coal Dust, Iron Dust, Gold Dust, Copper Dust, Tin Dust, Bronze Dust.
    - **Ingots & Plates**: Steel Ingot & Plate, Tin Ingot & Plate, Bronze Ingot & Plate, Iron Plate, Copper Plate, Gold Plate.
    - **Hydrocarbons & Upgrades**: Refined Bitumen, Autarchic Chipset, and Red/Blue/Green/Yellow Pipe Wires.
  - Added complete block and item localization for all advanced multiblock machinery and structures:
    - **Industrial Blast Furnace Suite**: Controller, Casing, Foundry Heat-Resistant Glass, Foundry Input & Output Ports.
    - **Steam Turbine Generator Suite**: Controller, Casing, Rotor Shaft, Steam Input, Water Output, and Energy Output Ports.
    - **Electric Arc Melter Suite**: Controller, Casing, Heating Chamber, Input & Output Ports.
    - **Continuous Casting Suite**: Controller, Casing, Mold Basin, Safety Chamber, Refractory Casting Channel, Input & Output Ports.
    - **Multi-Smelter & Fermenter**: Multi-Smelter Controller, Casing, Chamber, Ports, and Industrial Fermenter.
  - Added localized names for all fluid types, fluid blocks, and fluid buckets: Crude Oil, Steam, Molten Iron, Molten Gold, Molten Copper, Molten Tin, Molten Bronze, Molten Glass, and Biofuel (Ethanol).
  - Added proper GUI container title translations (`container.refinery.*` and `gui.refinery.gate`).
  - Resolved duplicate client sync method declarations in `AutoHarvesterBlockEntity`.

---

## [1.8.23] - 2026-10-31

### Added
- **Quartz Merge Pipe (`refinery:merge_pipe`, `refinery:merge_item_pipe`, `refinery:merge_fluid_pipe`)**:
  - Added new Quartz-tiered Merger Pipe suite designed to consolidate multiple incoming transport lines into a single designated outlet.
  - **Selectable Output & Dedicated Input Conversion**:
    - Right-clicking any face (with an empty hand or wrench) designates that face as the active **OUTPUT**. All other connected branches automatically convert into unidirectional **INPUTS**.
    - Clicking the active output face cycles through all connected branches, accompanied by an action bar notification and audio feedback.
    - Defaults to the facing direction away from the player when placed.
    - **Visual Output Indicator**: Renders an illuminated golden collar band on the active output arm for instant in-world visual identification.
  - **Merging Mechanics for Items & Fluids**:
    - Any items arriving from input branches are routed strictly into the designated output direction without diversion or bounce-back.
    - Fluids entering the merge pipe are actively pumped downstream strictly into the designated output connection, preventing any fluid from flowing backward into input branches.
  - **Crafting Recipes & Waterproofing Conversion**:
    - Base recipe: `[Quartz, Glass, Quartz]` (`minecraft:quartz`, `minecraft:glass`, `minecraft:quartz`) in a crafting table yields **10x Merge Pipes** (`refinery:merge_pipe` / `refinery:merge_item_pipe`).
    - Waterproofing: Placing any Merge Item Pipe with **Tar** (`refinery:tar`) or a **Slime Ball** (`minecraft:slime_ball`) transforms it into a **Merge Fluid Pipe** (`refinery:merge_fluid_pipe`).

---

## [1.8.22] - 2026-10-31

### Added
- **Void Pipe Shaped Crafting Parity & Instant Destruction**:
  - Added shaped crafting recipe: `[Lava Bucket, Glass, Lava Bucket]` (`minecraft:lava_bucket`, `minecraft:glass`, `minecraft:lava_bucket`) yielding **10x Void Pipes** (`refinery:void_pipe`). Empty buckets remain in the crafting grid upon crafting.
  - Overhauled Void Pipe (`refinery:void_pipe`) annihilation mechanics:
    - Zero-bottleneck fluid insertion capability accepting and voiding 100% of incoming fluid volumes without capacity caps or flow rate restrictions.
    - Unconditional item destruction on insertion (`canPlaceItem` accepts any item, `setItem` voids immediately, preventing item extraction).
    - In-flight network voiding: traveling items reaching a void pipe are instantly dispatched into oblivion, while adjacent fluid pipes actively extract and void stored fluids each tick.

### Fixed
- **Pipe Gate East/West Saddle Hugging & Rotation Alignment**:
  - Corrected `PipeGateEntityRenderer` rotational angle mapping for `WEST` (270°) and `EAST` (90°) faces. Saddle clamp plates now face properly outward and curve tightly around the pipe cylinder rather than bulging inside-out.

---

## [1.8.21] - 2026-10-31

### Fixed
- **Pipe Gate Curvature & Dead-End Support**:
  - Corrected `PipeGateEntityRenderer` rotational angle mapping for `EAST` (270°) and `WEST` (90°) faces. Saddle clamp plates now hug the pipe circumference snugly instead of curving backwards/inverted away from the pipe.
  - Extended dead-end single-arm pipe cylinders in `IndustrialPipeModel` to `0.80f` (and from `0.20f`), ensuring any attached pipe gate is fully supported on the pipe cylinder without hanging off into empty air.
- **Conveyor Belt Neighbor Occlusion & Transparent Floor Voids**:
  - Overrode `getOcclusionShape(BlockState)` to return `Shapes.empty()` and `useShapeForLightOcclusion(BlockState)` to return `false` across `ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `IndustrialPipeBlock`. Chunk meshing no longer culls floor tiles underneath conveyor belts or pipes.
- **Sloped Conveyor Connection Snapping & Roller Alignment**:
  - Fixed `ConveyorBeltBlock.updateShape` to properly evaluate `isBeltMatchingFront` and `isBeltMatchingBack` with `LevelReader`, keeping horizontal conveyors connected to sloped conveyors without retracting their belts or leaving un-animated gaps.
  - Lowered roller centers in `SlopedConveyorBeltModel` (`yEntry - 0.04f`, `yExit - 0.04f`) so rollers sit cleanly beneath the belt rather than protruding through the top carrying surface.

---

## [1.8.20] - 2026-10-31

### Added
- **Full Industrial Pipe Tier Suite (Wood, Copper, Iron, Steel, Gold, Diamond, Emerald)**:
  - Added complete material progression for 3D monolithic item and fluid transport pipes:
    - **Wood Pipe (`refinery:wood_item_pipe`, `refinery:wood_fluid_pipe`)**: Acts as an active extraction pipe that pulls items and fluids from connected inventories, chests, and tanks into the pipe network.
    - **Copper Pipe (`refinery:copper_item_pipe`, `refinery:copper_fluid_pipe`)**: Standard introductory tier with 1.0x transfer speed and 2,000 mB fluid capacity.
    - **Iron Pipe (`refinery:iron_item_pipe`, `refinery:iron_fluid_pipe`)**: Upgraded tier with 1.75x item speed, 4,000 mB fluid capacity, and 1,000 mB/t bandwidth.
    - **Steel Pipe (`refinery:steel_item_pipe`, `refinery:steel_fluid_pipe`)**: Heavy industrial tier with 2.5x item speed, 8,000 mB fluid capacity, and 2,000 mB/t bandwidth.
    - **Gold Pipe (`refinery:gold_item_pipe`, `refinery:gold_fluid_pipe`)**: Functions as an **Insertion Pipe**: actively checks adjacent inventories/machines and prioritizes inserting items until full before passing items downstream.
    - **Diamond Pipe (`refinery:diamond_item_pipe`, `refinery:diamond_fluid_pipe`)**: Functions as a **Sorting Pipe**: right-clicking opens a 54-slot GUI (9 filter slots for each of the 6 directions). Routes items to matching filtered directions, or to unconfigured/unfiltered directions as default/overflow routes.
    - **Emerald Pipe (`refinery:emerald_item_pipe`)**: Functions as a **Round-Robin Item Pipe**: cyclically and evenly distributes incoming items across all connected outgoing branches.
- **Crafting Recipes & Waterproofing Conversion**:
  - Base recipe: `[Material, Glass, Material]` in a crafting table yields **10x Item Pipes** for all tiers (Wood, Copper, Iron, Steel, Gold, Diamond, Emerald).
  - Waterproofing: Placing any Item Pipe into a crafting window with either **Tar** (`refinery:tar`) or a **Slime Ball** (`minecraft:slime_ball`) transforms it into its corresponding Fluid Pipe.
  - Normal pipes default to item pipes as standard.
- **Seamless Machine Auto-Work & Universal Auto-Ejection**:
  - Configured all mod machines (Auto Harvester, Planter, Fermenter, Boilers, Refinery, Grinder, Multi-Smelter, Metal Press, etc.) to automatically work and auto-eject into any connected pipe or container without requiring extraction pipes.
  - Expanded Auto Harvester auto-ejection to all candidate faces (behind, below, sides, top).

---

## [1.8.19] - 2026-10-31

### Fixed
- **Pipe Gate Inverted Curvature & Overhang**:
  - Corrected `PipeGateEntityRenderer` PoseStack orientation matrices for `EAST`, `WEST`, `UP`, and `DOWN` faces. Gates on lateral and vertical pipe faces were previously rotated 180° backwards, causing their saddle plate to bend away from the pipe into thin air.
  - Implemented exact multi-axis pipe contour detection in `PipeGateEntityRenderer.extractRenderState`: automatically detects cylinder axis (X, Y, or Z), applying straight geometry on flat circular pipe end-caps (`curveFactor = 0.0f`) and authentic cylindrical curvature across pipe circumferences (`curveFactor = 1.0f`).
  - Extended single-arm terminal pipe cylinders in `IndustrialPipeModel` to `0.66f` (or from `0.34f`): gates centered at `0.5f` now sit fully supported on the pipe cylinder without hanging off the dead-end cap.
  - Refined gate plate dimensions in `CurvedPipeGateModel` to `0.125f` half-width (`4x4` pixels), perfectly enclosing the gate within the pipe length.
- **Conveyor Belt Neighbor Occlusion & Transparent Floor Voids**:
  - Registered `CONVEYOR_BELT`, `SLOPED_CONVEYOR_BELT`, and `RIGHT_ANGLE_CONVEYOR_BELT` with `.noOcclusion()` in `RefineryMod.java`.
  - Overrode `useShapeForLightOcclusion(BlockState)` on all conveyor blocks: completely eliminated Minecraft's chunk mesher neighbor face culling that caused floor tiles underneath and beside conveyors to render as invisible/transparent void holes.
- **Sloped Conveyor Belt Animated Canvas Gap & Height Misalignment**:
  - Extended animated black tread canvas bounds in `SlopedConveyorBeltModel` to `z0 = 0.0f .. z1 = 1.0f`: eliminated the wide, un-animated brown slider-bed spot exposed at transitions.
  - Adjusted slope transition heights to `yLow = 0.2875f` (4.6px) and `yHigh = 1.2875f` (16 + 4.6px): transitions now line up to the exact sub-millimeter height of horizontal conveyor belts.
  - Synchronized `SlopedConveyorBeltBlockEntity` item trajectory physics to match the 4.6px conveyor belt carrying plane.
  - Updated `ConveyorBeltBlock.updateConnections`: horizontal conveyors now recognize adjacent `SlopedConveyorBeltBlock` (both climbing and descending) and `RightAngleConveyorBeltBlock` neighbors, automatically retracting terminal end-caps and rollers to form a seamless continuous conveyor run.

---

## [1.8.18] - 2026-10-31

### Fixed
- **World Load / Save Crash (`refinery:brass_ingot` Unknown Registry Key)**:
  - Fixed a critical datapack recipe parsing exception that prevented Minecraft from loading worlds/saves (`Registry Loading / Failed to load level data or datapacks`).
  - Resolved invalid `refinery:brass_ingot` item reference in `data/refinery/recipe/brass_item_pipe.json`. Brass Item Pipe is now craftable with `minecraft:gold_ingot` (matching Logistics Automation golden transport pipe crafting) as well as `refinery:bronze_ingot`.

---

## [1.8.17] - 2026-10-31

### Changed
- **Auto Harvester Directional Front-Only Harvest Geometry**:
  - Re-architected `AutoHarvesterBlockEntity` scanning coordinate mathematics to orient strictly in front of the machine's horizontal facing direction (`facing`).
  - Completely eliminated backward scanning: the harvester now scans forward distance `1 .. radius` directly in front of itself (`pos.relative(facing, forwardDist).relative(rightDir, lateralDist).above(dy)`), completely preventing the machine from ever harvesting crops, trees, or blocks behind it or directly underneath its housing.
  - The working range setting ($X$) directly controls the forward reach (1 to 9 blocks deep in front of the machine), centered laterally across $(2X + 1)$ blocks.
  - Updated `AutoHarvesterScreen` GUI label and tooltips to clearly communicate forward depth and front-only coverage (e.g. `11x5 in front`), removing confusing 360-degree radius descriptions.

---

## [1.8.16] - 2026-10-31

### Fixed
- **Auto Planter & Auto Harvester GUI Button Controls & State Sync**:
  - Resolved an issue where clicking the Power ON/OFF toggle and the working range radius `[-]` / `[+]` buttons in both the Auto Planter and Auto Harvester GUIs failed to respond or update the working parameters.
  - Fixed client/server menu button dispatch in `AutoPlanterMenu` and `AutoHarvesterMenu`: `clickMenuButton` now immediately updates `this.data` slots (slot 4 for enabled toggle, slot 5 for radius), ensuring client-side GUI state and server state remain synchronized whether the container is a client `SimpleContainer` or a server block entity.
  - Added immediate optimistic client-side UI feedback in `AutoPlanterScreen` and `AutoHarvesterScreen` mouse click handlers so button state, LED indicators, and radius dimensions update instantly upon clicking.
  - Implemented block entity network packet synchronization (`syncToClient()`, `getUpdatePacket()`, and `getUpdateTag()`) in `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity`, ensuring block entity state changes sync immediately across all connected clients.
  - Added inventory clearance (`this.items.clear()`) prior to loading items from NBT in `loadAdditional` to prevent ghost item retention.

---

## [1.8.15] - 2026-10-31

### Fixed
- **Industrial Grinder (Rock Crusher) Empty Inventory Item Rendering**:
  - Fixed an issue where the Industrial Grinder continued rendering 3D item models of the last crushed item between its spinning hopper blades even when the input inventory was completely empty.
  - Added explicit container clearing (`this.items.clear()`) in `IndustrialGrinderControllerBlockEntity.loadAdditional` prior to loading NBT items, preventing stale items from lingering on the client when slots are emptied on the server.
  - Overrode `setChanged()` to immediately synchronize block entity data packets to tracking clients whenever items are added or removed through player GUI interaction, hopper transfer, or automation piping.
  - Updated `IndustrialGrinderControllerRenderer` to explicitly clear active and output item render states when slots are empty or unpopulated.
  - Applied the same inventory load clearance fix to the Metal Press and Multi-Smelter controllers to eliminate ghost item rendering across all industrial multiblock machinery.

---

## [1.8.14] - 2026-10-31

### Added
- **Pipe Blank Gate (`refinery:blank_gate`)**:
  - Inexpensive, accessible gate tier (`GateTier.BLANK`) that functions as an industrial pipe blanking plate and flow stopper.
  - Attaches to any pipe face to immediately disconnect the pipe on that side, preventing adjacent pipes, tanks, or machines from joining together and stopping fluid/item transfer.
  - Neighboring pipes automatically recognize the blank and disconnect their opposing face in real time.
  - Right-clicking the blank gate provides on-screen status feedback ("Connection blocked on this side").
  - Dismantling with a wrench or mining it pops off the item and restores pipe connections.
  - Highly accessible crafting recipe: 1 Iron Ingot crafts 4 Pipe Blank Gates.
- **3D Curved Pipe Gate Saddle Geometry**:
  - All pipe gates now dynamically contour and follow the cylindrical curvature of round pipes when attached!
  - Replaced flat card rendering with `CurvedPipeGateModel`, which renders a custom 3D saddle clamp wrapping tightly around the pipe's 12-sided cylindrical profile ($R = 0.200$).
  - Dynamically calculates the pipe's orientation (curving across horizontal or vertical axes, or dome saddle across junction hubs) with sealed beveled rims and 4 corner hex mounting clamp bolts.

### Changed
- **Logistics Automation Recipe Parity**:
  - Re-aligned all 8 pipe recipes to match Logistics Automation standard crafting:
    - Item/transport pipes crafted via `["MGM"]` (Material + Glass + Material) yielding 8 pipes per craft.
    - Fluid pipes craftable shapelessly from transport pipes with slime balls or petroleum tar/bitumen.
    - Extractor pipes crafted from Planks + Glass yielding 8 pipes.
    - Void pipes crafted from Obsidian + Glass + Ender Pearl yielding 8 pipes.

---

## [1.8.13] - 2026-10-31

### Fixed
- **Eliminated All Texture Flickering and Z-Fighting on Native 3D Pipes**:
  - Switched pipe render type from `RenderTypes.entityCutout` to `RenderTypes.entitySolid`, ensuring full depth-buffer writing and preventing alpha-test jitter.
  - Resolved intersecting and coplanar surface Z-fighting between cylinder arms and junction fittings:
    - Cylinder arms now terminate cleanly at `0.5 ± 0.08` inside the fitting hub, preventing internal face collision and intersection while remaining fully enclosed within the hub radius (`0.228`).
    - Fixed warped and sheared non-planar quads on vertical (Y-axis) cylinders by correcting vertex pairing and counter-clockwise outward normal winding.
    - Removed redundant center junction rendering from standalone (unconnected) pipes, rendering them as clean, solid capped cylinders with zero overlapping surfaces.
    - Formatted non-degenerate latitude quads and polar fans on the junction fitting hub, eliminating zero-area quads and UV gradient artifacts.
    - Inset machine connection flanges by `0.001` to eliminate coplanar face collision with adjacent machine blocks.

---

## [1.8.12] - 2026-10-31

### Added
- **Native Industrial 3D Monolithic Pipe System (`refinery` namespace)**:
  - Built-in, fully autonomous native industrial pipe infrastructure directly integrated into Industrial Refinery, providing high-performance fluid and item logistics without external mod dependencies.
  - **100% Round & Perfectly Seamless Geometry**:
    - 12-sided faceted cylindrical geometry spanning normalized radius 0.20 (3.2 units).
    - Straight runs across 10, 20, 50, or more blocks render as a single continuous, unbroken cylinder from coordinate 0.0 to 1.0 with exact boundary vertex alignment and zero visual joint lines or collar seams.
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

### Fixed
- **Seamless Monolithic Fitting Hub & Double-Sided Rendering**:
  - Replaced legacy corner quad patches with a solid 12-sided faceted spherical fitting hub, eliminating the floating square tabs at tee and elbow joints.
  - Fixed cylinder vertex winding and outward normal orientation, closing the missing bottom and outer corner geometry on 90° pipe elbows.
  - Standalone single pipes now render as complete solid cylindrical pipe segments with closed circular end caps and central fitting hubs.
  - Corrected Minecraft 26.3 crafting recipe format across all 8 pipe recipes to prevent registry load errors.

---

## [1.8.11] - 2026-10-31

### Added
- **True Round 3D Monolithic Pipe Geometry (`Logistics Automation`)**:
  - Replaced legacy cubic boxes with true 12-sided faceted cylindrical pipe geometry (`PipeGeometry.java`) matching the monolithic 3D machine aesthetic.
  - Seamless straight pipe runs: pipe-to-pipe arm connections (`ARM`) and core junctions now share identical continuous cylindrical profiles (radius 3.2 units) with zero collar breaks, zero block-boundary seams, and zero black holes. Adjacent pipes merge into one smooth, unbroken pipeline.
  - Authentic industrial machine terminal flanges: connections to external inventories, machines, tanks, and boilers (`ARM_EXTENDED`) feature heavy bolted connection flanges (Fitting 11 Flange) with raised collar rims, machined hex bolts, and recessed gasket seals.
  - Monolithic multi-axial junction core (`CORE`): central junction seamlessly connects straight runs, smooth 90° elbows, and tee branches without square corners or protruding box faces.

### Changed
- **Ultra-Realistic Seamless Textures & LabPBR 1.3 Maps**:
  - Arm textures updated to 100% seamless longitudinal brushed metal along the cylinder ridge with physically accurate diffuse and specular cylindrical lighting.
  - Completely eliminated collar lines, edge grooves, and artificial joint stripes on straight pipe arm bodies.
  - Intelligent logistics pipe cores (Provider, Requester, Supplier, Crafting, Process, Satellite, Fluid, Chassis) feature crisp, illuminated high-tech LED indicator insets embedded flush within the brushed cast metal hub.
  - Updated LabPBR 1.3 normal maps (`_n.png`) and specular maps (`_s.png`) across all material variants: Galvanized/Stainless Steel, Copper (Clean, Exposed, Weathered, Oxidized), Brass/Gold, Cast Iron/Ductile Iron, and Void.
  - Mod jar auto-baking: updated `scripts/generate_pipe_texture_pack.ps1` to compile `PipeGeometry` with Java 25 directly into the logistics mod jar and deploy to active CurseForge 26.3 instances.

---

## [1.8.10] - 2026-10-30

### Added
- **Ultra-Realistic 3D Monolithic Pipe Resource Pack (`Logistics Automation`)**:
  - Comprehensive high-definition 3D Monolithic visual overhaul texture pack designed specifically for Logistics Automation (`logistics` namespace pipes).
  - **Ultra-Realistic 64x64 Photorealistic PBR Textures**:
    - Replaced legacy 8x8 textures with ultra-crisp 64x64 anisotropic brushed metal surfaces, directional ambient occlusion bevels, and CNC-machined hex bolts with recessed sockets.
    - Crystalline transparent inspection viewports with optical glare highlights and internal conduit rails, preserving full live in-world visibility of traveling item packets and fluid flows.
    - Full material suite coverage: Copper Transport & Fluid Pipes (Clean, Exposed, Weathered, and fully Oxidized verdigris), Stone Basalt & Ceramic Pipes, Titanium-Gold Accelerator & Electrified Pipes, Monolithic Void Singularities, and High-Tech Chassis Pipes (MkI through MkV).
  - **Full LabPBR 1.3 Shader Standard Support**:
    - Includes baked normal maps (`_n.png`) with true geometric relief, surface depth, bolt protrusions, and chamfered edges for real-time parallax in shader packs (Iris, Oculus, Complementary, BSL, SEUS).
    - Includes specular maps (`_s.png`) with accurate perceptual roughness, metallic F0 reflectance, and emissive channels for glowing power coils, LEDs, and laser optics.
  - **Enhanced 3D Monolithic Block Models**:
    - 3D physical block models featuring chamfered 12-sided junction hubs, reinforced corner stanchions, physical connection flanges with hex-head bolt studs, and mid-segment clamp sleeves.
  - **Standalone Downloads & Universal Compatibility**:
    - Distributed strictly as separate standalone resource pack downloads (kept unbaked from the mod jar to eliminate any mod loader validation conflicts):
      - `resourcepacks/Logistics-UltraRealistic-3D-Pipes.zip` (Universal for MC 1.21.4+ / NeoForge 26.3, formats 15-65).
      - `resourcepacks/Logistics-UltraRealistic-3D-Pipes-MC1.21.1.zip` (Native for MC 1.21 - 1.21.1, format 34).
      - `resourcepacks/Logistics-UltraRealistic-3D-Pipes-MC1.20.1.zip` (Native for MC 1.20 - 1.20.1, format 15).
    - Fixed archive entry path separators to strictly use standard forward slashes (`/`), resolving "broken or incompatible" warnings in Minecraft's pack repository.

---

## [1.8.9] - 2026-10-30

### Added
- **Sloped Conveyor Belt (`refinery:sloped_conveyor_belt`)**:
  - Low-profile extruded aluminum conveyor designed to smoothly transit items up or down elevations (1 block vertical rise per block run).
  - Features continuous height-interpolating slope physics that elevates or lowers items along the incline, clamping velocities and centering items along the belt bed.
  - Seamlessly transfers items to adjacent conveyors, storage chests, and machine inventories positioned at either the top or bottom terminal.
  - Implements `Container` and NeoForge `Item` capability for automated item loading by hoppers, harvesters, and crushers.
  - Monolithic 3D rendering with sloped guide rails, support stanchions, rollers, and animated scrolling belt.
  - Right-click with a wrench to rotate direction clockwise; Shift-right-click with a wrench to toggle elevation incline between `UP` and `DOWN`; Shift-right-click with an empty hand to rotate direction.
- **Right-Angle Conveyor Belt (`refinery:right_angle_conveyor_belt`)**:
  - High-efficiency 90-degree corner conveyor for turning logistics lines cleanly around corners without dead spots or item spillages.
  - Supports two-phase corner physics: incoming travel along entry facing toward center, followed by smooth 90-degree velocity vectoring into the exit direction.
  - Monolithic 3D model with extruded aluminum curved outer guide rails, corner bracket, cylindrical rollers, and an 8-segment curved animated scrolling belt top.
  - Right-click with a wrench or shift-empty-hand to rotate facing; Shift-right-click with a wrench to toggle corner turn direction between `RIGHT` and `LEFT`.

### Changed
- **Conveyor Belt In-Place Rotation**:
  - Players can now easily re-orient any standard Conveyor Belt in-place without having to break and replace it.
  - Right-click with a Wrench rotates clockwise; Shift-right-click with a Wrench rotates counter-clockwise.
  - Shift-right-click with an empty hand also rotates clockwise.
  - Belt connection geometry dynamically re-snaps to adjacent conveyors immediately upon rotation.
- **Industrial Grinder (Crusher) Direct Auto-Export**:
  - The Industrial Grinder multiblock controller now automatically extracts crushed ores and byproducts from its output slots and ejects them directly onto any conveyor belt snapped to its discharge chute or adjacent positions.
  - Also auto-pushes items into adjacent chests and inventory capabilities every 4 ticks without requiring external extract pipes or hoppers.

---

## [1.8.8] - 2026-10-30

### Changed
- **Direct Fluid Auto-Transfer for Steam Boilers & Steam Turbines (No Extract Pipes Required)**:
  - Enabled active fluid auto-push directly into regular liquid pipes and adjacent fluid handlers for all steam boilers and the steam turbine, matching the seamless logistics behavior of the multiblock liquid storage tanks.
  - **Steam Boilers (Small & Large, Solid-Fuel & Diesel)**:
    - Dedicated Steam Output Ports (`boiler_steam_port`) and Boiler Controllers now actively transfer generated Steam (up to 1,000 mB/t) directly into adjacent pipes without requiring extraction servos or powered extraction pipes.
  - **Steam Turbine Generator**:
    - Dedicated Turbine Water Output Ports (`turbine_water_port`) and Turbine Controllers now actively transfer condensed Water (up to 1,000 mB/t) directly into adjacent pipes without requiring extraction pipes.
  - Facilitates 100% plug-and-play closed-loop Rankine power cycles: standard liquid pipes now route steam from boiler ports directly into turbine steam inputs, and return condensed water seamlessly back into boiler water inputs.

---

## [1.8.7] - 2026-10-30

### Fixed
- **Industrial Fluid Discharge Survival Drop**: Added the missing block loot table definitions (`data/refinery/loot_table/blocks/fluid_discharge.json` and `loot_tables/blocks/fluid_discharge.json`) so the Industrial Fluid Discharge block drops itself properly when mined or broken in survival.
- **Storage Tank Item Icons & Held Models**: Added missing item definitions for the Glass-Fused-to-Steel storage tank components (`assets/refinery/items/storage_tank_casing.json`, `storage_tank_drain_valve.json`, and `storage_tank_fill_valve.json`), resolving missing GUI inventory icons and black/purple checkerboard held models.

### Changed
- **Blast Furnace Fuel & Iron Strict Restrictions**:
  - Restricted iron input sources strictly to **Iron Ingot** (`minecraft:iron_ingot`) and **Block of Iron** (`minecraft:iron_block`). Raw iron, ores, and dusts are no longer accepted.
  - Restricted fuel sources strictly to **Coal** (`minecraft:coal`) and **Block of Coal** (`minecraft:coal_block`). Charcoal, coal dust, petroleum tar, bitumen, and other burnables are no longer accepted.
  - Implemented unit conversion for blocks: 1 Block of Iron adds 9 iron units (yielding 9 steel ingots); 1 Block of Coal adds 9 fuel units (fueling 9 smelting operations).
  - Partial iron and fuel units are persistently stored in NBT and safely drop as blocks and ingots/coal when broken.
  - Updated GUI slot filters and port container insertion to strictly reject any other items from manual placement and automated piping.
  - Updated Blast Smelter GUI tooltips to reflect the Iron Ingot/Block and Coal/Block requirements.

---

## [1.8.6] - 2026-10-30

### Changed
- **Sleek Industrial Access Ladder Redesign**:
  - Replaced the bulky, chunky caged ladder with a clean, authentic industrial steel access ladder mounted directly up the North face of the tank.
  - Features twin vertical stringer rails, standoff wall brackets connecting to the tank shell, distinct individual horizontal climbing rungs spaced every 3.5 units with hollow air between them (exposing the dark enamel tank panels behind), and smooth top walkthrough grab bars stepping onto the roof rim.
  - Assembled storage tank casing blocks in the North ladder column (`relX == 0, relZ == -radius`) now implement `isLadder = true`, allowing players to physically climb the ladder to the roof of the tank.
  - Updated all ladder stringers, rungs, and roof handrails to use a crisp, bright galvanized industrial steel texture swatch (`#D8DFE8`), eliminating messy textures and distorted UVs.
  - Overhauled top roof guardrails to follow the octagonal profile of the tank with an open walkthrough entry at the ladder head.

### Removed
- **Exterior Red Standpipe**: Completely removed the vertical red pipe and flanged valves running down the side of the tank model.

---

## [1.8.5] - 2026-10-30

### Added
- **Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks**:
  - Implemented multiblock industrial liquid storage tanks based on modern Glass-Fused-to-Steel / Enamel bolted tank engineering (Hebei Wansheng / enameltanks.com reference design).
  - **Six Supported Multiblock Geometries & Capacities**:
    - `3x3x3` (Capacity: 64,000 mB / 64 Buckets)
    - `3x3x6` (Capacity: 256,000 mB / 256 Buckets)
    - `3x3x9` (Capacity: 512,000 mB / 512 Buckets)
    - `5x5x5` (Capacity: 1,000,000 mB / 1,000 Buckets)
    - `5x5x7` (Capacity: 2,000,000 mB / 2,000 Buckets)
    - `5x5x9` (Capacity: 4,000,000 mB / 4,000 Buckets)
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

---

## [1.8.4] - 2026-10-29

### Changed
- Mod update

---

## [1.8.4] - 2026-10-29

### Added
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

---

## [1.8.2] - 2026-10-29

### Changed
- Mod update

---

## [1.8.2] - 2026-10-29

### Fixed
- **Fluid Pump Suction Hose Linking via Refinery Wrench**: Fixed an issue where sneak-right-clicking a Coal Fluid Pump (or any pump variant extending `AbstractFluidPumpBlock`) with the Refinery Wrench dismantled the pump instead of selecting it for 3D suction hose linking. Added double-sneak-click confirmation for deliberate pump dismantling.
- **Fluid Pump Backflow & Pipe Draining**:
  - Prevented fluid back-draining from pipe networks into or out of fluid pumps: pumps now implement a robust transactional `SnapshotJournal` fluid handler accepting returning fluid of the same type without leaking or stalling pipe parcels.
  - Disabled pump discharge downward (`Direction.DOWN`) into mounting bases and skid frames.
  - Added BFS tile-entity protection so the pump's contiguous source scanner never targets or drains water from adjacent pipes, waterlogged pipes, tanks, or machines.
  - Protected waterlogged blocks during finite source drainage by unsetting the `WATERLOGGED` block state property rather than replacing blocks with air.
  - Changed default fluid pump operating mode to `[INFINITE WATER]` (`true`) so base water sources are preserved automatically upon placement.

---

## [1.8.0] - 2026-10-29

### Added
- **Livestock Feeding Trough (`refinery:feeding_trough`)**:
  - A 2-block wide/long monolithic multiblock feed bunk designed for automated livestock breeding and animal farming.
  - **Monolithic 3D Model**: High-detail formed weathered galvanized sheet steel feed bunk featuring realistic zinc spangle crystal flakes, rolled top safety flanges, four heavy-gauge steel divider cross-struts, end bulkheads with cleanout slots, and corner skid feet.
  - **Dynamic In-World Hay Mound**:
    - High-resolution golden straw/hay texture filling the interior of the trough.
    - Mound level rises dynamically and continuously from Y = 1.0px (empty metal floor) to Y = 6.4px (completely full bunk) based on inventory food quantity.
  - **Automated Livestock Breeding**:
    - Scans a 4.5-block radius (10x5x10 bounding area) around the 2-block trough every second.
    - Feeds up to 2 eligible adult animals per second that are off breeding cooldown (`canFallInLove()`), triggering love hearts (`broadcastEntityEvent(18)`) and realistic eating sound effects (`SoundEvents.GENERIC_EAT`).
    - Excludes baby animals and animals currently on breeding cooldown or already in love.
  - **Universal Animal & Diet Compatibility**:
    - Automatically checks `animal.isFood(stack)` to accept any valid food item for vanilla and modded livestock (wheat, seeds, carrots, potatoes, beetroots, apples, berries, grasses, kelp, meat/fish, slimeballs, fungi, etc.).
  - **Logistics & Multiblock Integration**:
    - Unified master inventory: both the Head and Foot blocks forward capabilities and item access to the master block entity.
    - Fully compatible with Logistics Automation item pipes, conveyors, hoppers, droppers, and automated item transfer systems from any side or block face.
    - Interactive quick-deposit: right-clicking with valid animal feed instantly deposits food into the trough with sound feedback; empty hand opens the container GUI.
  - **Dedicated Container GUI & Radar**:
    - Custom 9-slot feed grid with live golden hay fill gauge, active animal herd radar counter, and status indicator LED.
  - **Crafting Recipe**: 4 Steel Plates + 2 Iron Bars + 1 Hay Bale produce 1 Feeding Trough.

---

## [1.7.2] - 2026-10-29

### Changed
- **Conveyor Belt Speed Calibrated**: Reduced item transit speed by half to a steady, realistic factory conveyor velocity (from 0.10 to 0.05 blocks/tick; ~1.0 block per second).
- **Item-Only Conveyance**: The conveyor belt now strictly conveys dropped and transported items (`ItemEntity`). Players, animals, and monsters standing or walking across the belt are no longer pushed or displaced, allowing comfortable walking without interference.

### Fixed
- **Belt Texture Scroll Direction Synchronization**: Fixed the UV texture scroll direction on both the top and bottom belt surfaces. The top belt surface now glides forward in the exact direction of item movement, and the bottom return belt moves in reverse.
- **Synchronized Roller Rotation**: End rollers at exposed terminals rotate at half speed, perfectly matching the calibrated belt velocity and item transit speed.

---

## [1.7.0] - 2026-10-29

### Added
- **Industrial Belt Conveyor System (`refinery:conveyor_belt`)**:
  - Low-profile modular belt conveyor system for high-throughput factory automation, item transport, and entity conveyance.
  - **Monolithic 3D Model**: High-detail extruded anodized aluminum bed frame with side longitudinal T-slot channels, heavy cast mounting foot pads, and galvanized steel deck bed.
  - **Matte Black Textured Industrial Belt**: High-density vulcanized rubber canvas belt with fine transverse friction ribs and stitched seams.
  - **Dynamic In-World Animation**:
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

---

## [1.6.5] - 2026-10-29

### Added
- **Dedicated Fluid Pump Variants**: Split the fluid pump into 3 distinct specialized machine blocks and items, each with custom frame coloring on the monolithic 3D model, custom inventory slots, and unique crafting recipes:
  - **Coal Fluid Pump (`refinery:coal_fluid_pump`)**: Heavy Cast-Iron Dark Charcoal frame (`#374151`). Single solid-fuel slot burning coal, charcoal, or other furnace fuels at a calibrated rate of 1 coal per 3 seconds (60 ticks) of active pumping.
  - **Diesel Fluid Pump (`refinery:diesel_fluid_pump`)**: Industrial Caterpillar Yellow-Orange frame (`#FA983A`). Internal 4,000 mB diesel reservoir consuming 10 mB per bucket pumped (1 bucket of fuel yields 100 buckets pumped). Refuelable via fuel bucket slots or fluid pipes.
  - **Electric Fluid Pump (`refinery:electric_fluid_pump`)**: High-Voltage Electric Blue frame (`#2563EB`). 20,000 FE buffer consuming 400 FE per bucket pumped (20 FE/t). Connects to external RF/FE power grids.
- **Custom Screen Interfaces**: Each pump variant features a custom-tailored GUI displaying only its relevant power gauges and controls (burn flame & rate for Coal, diesel gauge & bucket transfer slots for Diesel, energy meter & digital readout for Electric).
- **Shift-Right-Click In-World Mode Toggle**: Shift-Right-Clicking any fluid pump with an empty hand or Refinery Wrench directly toggles between `[ INFINITE WATER ]` and `[ DRAIN POOL ]` with sound effects and on-screen notification.

### Changed
- **Fluid Pump GUI Streamlining**: Removed the coordinate readout and unlink button from the GUI, keeping the interface clean, compact, and focused on operational controls and internal fluid storage.
- **Backward Compatibility**: Existing `refinery:fluid_pump` blocks and items now map cleanly to the Coal Fluid Pump behavior and retain full world functionality.

### Fixed
- **Fluid Pool Drain Traversal**: Fixed water pool drainage traversal across water so BFS travels across both source and flowing water blocks (`is(FluidTags.WATER)`), allowing entire water bodies to be drained dry in Drain mode without prematurely stopping.

---

## [1.6.4] - 2026-10-29

### Changed
- **Fluid Pump Coal Burn Rate**: Fixed solid fuel combustion duration to burn exactly 1 Coal (or burnable item) per 3 seconds (60 ticks) while actively pumping, pausing consumption when idle, paused by redstone, or when fluid is dry.
- **Fluid Pump Power Mode & Drain Toggle Buttons**: Overhauled the mode toggles from flat progress-like bars into authentic 3D beveled Minecraft button widgets with client-side prediction, click sound effects, and instant state switching between `[ RF ]`, `[ COAL ]`, and `[ DIESEL ]` (does not require external sources connected to switch).
- **GUI Power Buffer Text Display**: Replaced the left progress bar with a clean digital readout panel displaying current power mode, stored FE, burn seconds, or diesel fuel volume directly without needing to mouse over.
- **Fluid Tank Reservoir Visuals**: Added a calibrated glass tube display with measurement tick marks (25%, 50%, 75%), active liquid flow animations when pumping directly into pipes, and persistent fluid type retention (`lastPumpedFluid`).

### Fixed
- **Missing Item Model Definitions for 26.3**: Created `assets/refinery/items/fluid_pump.json` and `assets/refinery/items/fluid_discharge.json` required by Minecraft 26.3, resolving broken missing-model icons for the Fluid Pump and Fluid Discharge.

---

## [1.6.3] - 2026-10-29

### Fixed
- **Transfer API Transaction NPE Crash**: Fixed `NullPointerException: Cannot invoke "net.neoforged.neoforge.transfer.transaction.TransactionContext.depth()" because "transaction" is null` when pushing fluids into external handlers or Logistics Automation pipes (`FluidPumpBlockEntity`, `ArcMelterControllerBlockEntity`, `CastingChannelBlockEntity`, `FermenterBlockEntity`). All fluid export operations now cleanly execute within `try (Transaction tx = Transaction.openRoot())` and commit upon successful insertion.
- **Fluid Pump Power Source & Setting Toggles**: Fixed an issue where clicking the power source button in `FluidPumpScreen` did not change modes. Expanded button hitboxes, added client-side prediction, and linked menu button clicks directly to block entity state updates with immediate network synchronization.
- **Fluid Pump Screen Typography & Missing Glyphs**: Replaced unsupported Unicode emojis (`⚡`, `♨`, `⛽`, `⚠`, `⏻`, `●`, `○`, `✓`) with crisp bracketed indicators (`[PUMPING]`, `[IDLE]`, `[NO POWER]`, `[NO FUEL]`, `[NO DIESEL]`, `[TANK FULL]`, `[NO INTAKE]`, `[OUT OF RANGE]`, `[POOL DRAINED]`, `[PAUSED - REDSTONE]`), completely eliminating missing-glyph rectangular boxes.
- **Fluid Pump & Discharge Item Icons**: Replaced 3D cube fallback model references with custom high-definition 2D item textures (`textures/item/fluid_pump.png` and `textures/item/fluid_discharge.png`) accurately depicting the Superkiger tubular roll-cage pump and red slotted foot valve discharge.

---

## [1.6.2] - 2026-10-29

### Added
- **Industrial Fluid Discharge (`refinery:fluid_discharge`)**:
  - Added a heavy-duty industrial foot valve & slotted discharge strainer block designed to interface seamlessly with Logistics Automation pipes and standard NeoForge fluid capabilities.
  - **Monolithic 3D Model**: High-gloss fire-engine red cylindrical housing with base mounting collar, 4 cast reinforcement ribs, dual-tier mounting flange rings with side ear bolt lugs, dark graphite sealing gasket, polished silver/chrome hexagonal bolt heads, high-tensile threaded studs with bottom hex nuts, slotted dome cage with vertical vent slots revealing dark interior valve throat & bronze flapper plate, and perforated dome crown.
  - **Dual Toggled Operational Modes**:
    - **`VOID / DUMP` Mode**: Instantly dumps and destroys all incoming pipe fluids (water, lava, crude oil, diesel, or any modded liquid) without backing up or clogging pipe networks. Emits periodic drain sound effects and visual feedback.
    - **`FILL` Mode**: Discharges incoming fluid directly into the surrounding world at the exact same Y level ($Y = Y_{discharge}$), converting contiguous air and replaceable blocks into genuine fluid source blocks (consuming 1,000 mB per source block).
    - **Contiguous Flood-Fill (BFS) with Barrier Isolation**: Uses strict 4-way orthogonal Breadth-First Search on the horizontal plane. Fluid only expands through contiguous blocks of the same fluid or the discharge block itself. It strictly respects solid walls and boundaries—if placed in a walled pit, fluid will never jump across barriers into neighboring pits!
  - **Mode Toggling**: Easily toggled between `VOID` and `FILL` mode by right-clicking with an empty hand or with a Refinery Wrench. Sneak-right-clicking displays a comprehensive readout of current mode, internal buffer fluid, active state, and redstone signal.
  - **Redstone Automation Pause**: Safely pauses discharging when receiving a redstone signal.
  - **Crafting Recipe**: Accessible recipe crafted from Iron Ingots, Red Dye, Iron Bars, Bucket, and Redstone.

---

## [1.6.0] - 2026-10-28

### Added
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

## [1.5.15] - 2026-10-28

### Added
- **Open-Top Hopper Bowl with 3D Counter-Rotating Spiked Grinder Blades**:
  - Redesigned the top feed hopper of the Industrial Grinder (`IndustrialGrinderModel`) into a deep open-top bowl housing twin counter-rotating 3D spiked shredder rotors (`7` intermeshing toothed blade discs per shaft).
  - The 3D grinder blades spin dynamically when the machine is actively crushing material and display the actual loaded input items being pulled between the teeth.
- **Direct Top Hopper Item Intake (Conveyor Drop & Automation Ready)**:
  - The top hopper of the assembled Industrial Grinder now acts as an active physical item intake: dropped items (such as material fed from standalone overhead conveyors or droppers) falling into the top hopper bowl are automatically collected into the grinder's input slots.
  - Forwarded item and energy capabilities across assembled grinder member blocks so hoppers, pipes, and external conveyors can interface directly with the top hopper and ports.

### Changed
- **Clean Dynamic Conveyor & Hopper Visuals**:
  - Removed the side feed conveyor on the left side of the Industrial Grinder (`IndustrialGrinderModel`).
  - Removed all static pre-generated rock and aggregate mounds from inside the top hopper and on the right-side inclined output conveyor.
  - The right-side inclined discharge conveyor now renders an empty cycling chevron belt when idle and dynamically displays the actual crushed output items climbing the conveyor when unloading.

### Fixed
- **Floor X-Ray / Transparent Ground Under Assembled Multiblocks**:
  - Fixed an issue where the floor blocks directly underneath assembled multiblocks (`IndustrialGrinderControllerBlock`, `GrinderCasingBlock`, `GrinderChamberBlock`, `GrinderPortBlock`, as well as the Boiler and Metal Press multiblocks) appeared transparent/invisible due to full-cube occlusion culling when `ASSEMBLED=true`.
  - Overrode `getOcclusionShape` to return `Shapes.empty()`, `getShadeBrightness` to `1.0f`, and `propagatesSkylightDown` to `true` when assembled so the ground beneath the machine's legs renders solidly with natural lighting.

---

## [1.5.14] - 2026-10-28

### Added
- **Multiblock Industrial Grinder Monolithic 3D Overhaul & Animated Conveyors**:
  - Overhauled the $3\times 3\times 3$ Industrial Grinder (`refinery:industrial_grinder_controller`) into a monolithic 3D rock crushing plant based on real-world heavy industrial machinery.
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
  - Overhauled the $5\times 5\times 5$ Large Steam Boiler (`refinery:large_boiler_controller`) into a monolithic 3D twin-furnace Scotch marine industrial packaged boiler (`LargeBoilerModel`).
  - Massive cylindrical boiler pressure vessel with dark steel structural skid runners and saddle cradles.
  - Dual circular front boiler furnace doors with twin glowing burner viewports (dynamic `15728880` full emissive flame glow when active).
  - Twin vertical exhaust smokestacks with top flanges and reinforcement collars.
  - Overhead heavy steam manifold piping with dual valve handwheels and central dial pressure gauge.
  - Right-side elevated maintenance catwalk platform with safety handrail and access ladder, side electrical control console, and twin water sight level columns.
  - Custom high-capacity industrial GUI (`LargeBoilerScreen` & `large_boiler.png`).
- **Small and Large Diesel-Fired Steam Boilers**:
  - Introduced the **Small Diesel Boiler** ($3\times 3\times 3$, `refinery:small_diesel_boiler_controller`) and **Large Diesel Boiler** ($5\times 5\times 5$, `refinery:large_diesel_boiler_controller`) that consume liquid **Diesel** instead of solid burnables to produce high-pressure steam.
  - Custom 3D models with modern liquid fuel injector burner hubs, fuel rails, and high-efficiency combustion chambers.
  - Custom 3-tank industrial GUIs (`SmallDieselBoilerScreen` & `LargeDieselBoilerScreen`) featuring recessed gauges for Diesel, Water, and Steam, fluid bucket fill/drain interaction slots, redstone indicators, and combustion flame viewports.
  - Fluid and item capabilities integrated with input/output ports and automation pipes.
  - Added shaped crafting recipes and loot tables for both diesel boiler controllers.

### Changed
- **Lighter Diesel Fluid Texture Palette**:
  - Re-paletted diesel fluid textures (`diesel_still.png`, `diesel_flow.png`, `diesel_bucket.png`, `port_diesel.png`) to a refined golden straw tone (`#fdee87` / `#f5cf47`) for enhanced visual distinction from crude oil and gasoline.

---

## [1.5.13] - 2026-10-28

### Changed
- Mod update

---

## [v1.5.13] - 2026-10-28

### Added
- **Monolithic 3D Small Steam Boiler Model Overhaul (Scotch Marine Package Boiler)**:
  - Transformed the assembled 3×3×3 Small Steam Boiler (`refinery:small_boiler_controller`) into a monolithic 3D industrial packaged Scotch marine fire-tube boiler model using the established `ASSEMBLED` blockstate and `invisible.json` occlusion pattern (exact technique used on the Metal Press).
  - Features a horizontal cylindrical boiler drum with solid steel cladding plates and riveted reinforcement straps (solid steel exterior with no exposed cutaways).
  - Massive circular front boiler door with heavy black cast iron hinges, perimeter bolt circle, and central burner hub.
  - Dynamic glowing burner sight-glass viewport: emits an intense fiery glow when combustion is active (`15728880` full brightness) and reverts to dark soot glass on standby.
  - Lower arched rectangular ash/cleanout access door with heavy latch.
  - Structural steel I-beam skid base with transverse cross-beams and two curved saddle cradles supporting the drum.
  - Overhead industrial steam manifold header piping, red globe valve handwheels, safety relief valve, and dial pressure gauge.
  - Tall vertical exhaust smokestack / chimney with reinforcement collar and top flange.
  - Side industrial electrical control cabinet (ICI Caldaie style) with status indicator lamps, caution badge, and conduit to skid.
  - Side vertical water level sight glass column and dual base feed-water booster pumps.
- **Member Block Occlusion & Interaction Delegation**:
  - Member blocks (`SteelBoilerCasingBlock`, `BoilerFireboxBlock`, `BoilerPipeBlock`, `BoilerPortBlock`) now track `ASSEMBLED`, feature `.noOcclusion()`, return `RenderShape.INVISIBLE` when assembled, and forward wrench/hand interactions and destruction to the controller.

### Changed
- **Custom Industrial Small Boiler GUI Overhaul & Realignment**:
  - Completely aligned and overhauled the Small Boiler GUI (`SmallBoilerScreen` & `small_boiler.png`) into a dark gunmetal steel industrial panel with rivets, matching the custom aesthetics of the Metal Press.
  - Recessed water buffer tank gauge on the left with blue fluid fill and graduation markings.
  - Recessed high-pressure steam buffer tank gauge on the right with aqua steam fill and graduation markings.
  - Central machined steel hearth with $2\times 2$ fuel buffer slot wells.
  - Round redstone signal indicator lamp (glowing green when powered, dark red when off).
  - Combustion flame viewport with dynamic flickering flame effect when burning.
  - Overheat / meltdown danger hazard bar with warning details and countdown timer to explosion.
  - Real-time status header badge (`ONLINE`, `BURNING`, `OVERHEATING!`, `UNFORMED`).

---

## [v1.5.11] - 2026-10-28

### Added
- **Automated Planter & Harvester GUI ON/OFF Power Switch**:
  - Added an interactive power toggle switch directly inside the Automated Planter and Automated Harvester GUIs.
  - Players can toggle machine operation ON or OFF at any time. When turned OFF, machines immediately pause scanning, cease idle energy consumption, and set status to `DISABLED`.
  - Power state is synchronized across client-server and persisted in block entity NBT.
- **Configurable Work Radius in All Directions (Up to 9 Blocks / 100m+ Area)**:
  - Added interactive radius adjustment buttons (`-` and `+`) to the Planter and Harvester GUIs, with live radius readout and area display.
  - Adjustable from 1 block up to 9 blocks away from the machine in all directions, offering coverage areas ranging from 3x3 (9 tiles) up to 19x19 (361 tiles / 100m+ area). Default radius is 5 blocks (11x11 square / 121 tiles).
  - Radius settings persist across world saves and synchronize via container data slots.

### Changed
- **Omnidirectional Harvester Coverage**:
  - The Automated Harvester now sweeps in all horizontal directions around itself up to the configured radius (matching the Planter's circular/square perimeter coverage), allowing flexible farm and tree orchard layouts.
  - Preserved backward-compatible auto-ejection to chests and logistics pipes positioned behind or below the harvester.

---

## [v1.5.9] - 2026-10-27

### Added, Fixed & Improved
- **3-Block-Wide (3×2×2) Industrial Metal Press with Conveyor Bed & AIDA NC1-1500 Stamping Head**:
  - Expanded the Metal Press multiblock structure to **3 blocks wide** (`3×2×2`, with flexible placement supporting centered or side-aligned controllers as well as `3×2×1` depth).
  - Redesigned the assembled 3D model (`AidaPressModel`) and 256×256 texture atlas (`aida_press.png`) to combine the **Immersive Engineering style 3-block-wide conveyor bed & 4-post center gantry** (dark gunmetal base with bottom vents, 4 brown/bronze vertical tensioner straps with silver rivets, steel end rollers, and a stitched terracotta-orange conveyor belt) with the **AIDA NC1-1500 overhead stamping head** (off-white industrial enamel crown, green AIDA 4-diamond emblem + text, NC1-1500 badge, circular analog tonnage/crank dial gauge, 3 side cooling louvers, amber hydraulic cylinder, side electrical cabinet, operator control pendant with red mushroom E-stop, and safety-yellow die bar).
  - Fixed member block invisibility when assembled by sharing the `MetalPressControllerBlock.ASSEMBLED` `BooleanProperty` instance across `PressBedBlock`, `PressHeadBlock`, and `PressCasingBlock`, adding `.noOcclusion()`, and overriding `getRenderShape` to return `RenderShape.INVISIBLE` when assembled (preventing X-ray floor culling and hiding all member cubes cleanly).
  - Fixed client-side custom model rendering by syncing `MetalPressControllerBlockEntity` state via `getUpdateTag`/`getUpdatePacket`, checking blockstate `ASSEMBLED` on the client, and rendering with custom outward-normal geometry (`submitCustomGeometry`).
  - Forwarded Energy and Item capabilities across all member blocks (`PressBedBlock`, `PressHeadBlock`, `PressCasingBlock`) to the assembled controller.

---

## [v1.5.8] - 2026-10-26

### Added & Improved
- **Industrial Metal Press Monolithic AIDA NC1-1500 C-Frame Model & Texture**:
  - Replaced the multiblock's cubic assembled appearance with a seamless, high-definition 3D model of the **AIDA NC1-1500 C-frame Stamping Press**.
  - Custom 128×128 pixel-art texture featuring classic off-white machine enamel, dark green AIDA branding with diamond logo, NC1-1500 designation, analog circular stroke dial gauge, side electrical cabinet with ventilation louvers, and lower operator pendant with mushroom emergency stop button and flexible conduit.
  - Member blocks (`PressBedBlock`, `PressHeadBlock`, `PressCasingBlock`, and `MetalPressControllerBlock`) now dynamically suppress their cubic block models when assembled (`invisible.json`) while maintaining collision, hardness, and drop behaviors.
  - Right-clicking any part of the assembled press forwards interactions directly to the controller and opens the stamping interface.
  - Integrated dynamic vertical ram slide animation: amber/bronze hydraulic cylinder and safety-yellow die bar that accelerates downward on the power stroke and smoothly retracts during stamping.

---

## [v1.5.7] - 2026-10-26

### Fixed & Improved
- **Logistics Pipe Compatibility & Machine Detection**:
  - **Accurate Machine Inventory Detection for Pipe Gates**: Fixed an issue where pipe gates attached to pipes mistakenly identified adjacent transport pipes as target inventories instead of the attached machine (Automated Harvester, Automated Planter, Boilers, Refinery Controller, etc.). Connected pipes are now strictly excluded from inventory evaluation, allowing gate conditions (`Has Items`, `Inventory Empty`, `Inventory Space`, `Inventory Full`) to accurately reflect machine contents.
  - **Autarchic Pulsar Extraction on Logistics Pipes**: Fixed a reflection call site bug in `LogisticsPipeCompat.injectItemIntoPipe` where `forceAddItem` was blocked by a polymorphic method handle cast exception. Replaced with robust reflection fallback invoking `forceAddItem(TravelingItem, Direction)` and `addItem(Direction, ItemStack)`, enabling autarchic pulsar gates on extractor pipes to reliably pulse items out of machines into the logistics network.
  - **Harvester Auto-Ejection to Logistics Pipes**: The Automated Harvester now natively detects connected Logistics Automation item pipes behind or below the block, auto-injecting harvested logs, saplings, apples, and crops directly into the pipe network in addition to chests and hoppers.
  - **WorldlyContainer Capability Wrappers**: Migrated `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity` capability registrations to `WorldlyContainerWrapper(be, side)` with explicit `canPlaceItem` and `canTakeItem` handlers, ensuring sided insertion and extraction work seamlessly with third-party logistics systems, hoppers, and pipes.
  - **Expanded Machine Status Gate Triggers**: Updated `MACHINE_ACTIVE` and `MACHINE_IDLE` gate triggers to dynamically inspect block state properties (`working`, `active`, `lit`, `running`, `powered`) and machine status getters, providing accurate operational detection for Harvesters, Planters, Boilers, and Controllers.

---
## [v1.5.6] - 2026-10-26

### Added & Improved
- **Automated Planter Sapling & Tree Support**:
  - The **Automated Planter** (`refinery:auto_planter`) now accepts and plants all tree saplings (`#minecraft:saplings`, `SaplingBlock`, and modded tree saplings) alongside standard agricultural crop seeds.
  - Saplings are planted directly on natural soil blocks (`#minecraft:dirt` — Dirt, Grass, Podzol, Coarse Dirt, Rooted Dirt, Mud, Mycelium, Moss) without tilling them into farmland.
  - Automatically handles slot placement, hopper/pipe input filtering, and shift-clicking for saplings.
- **Automated Harvester Tree & Leaf Canopy Felling**:
  - The **Automated Harvester** (`refinery:auto_harvester`) now detects tree trunks and logs (`#minecraft:logs`) and leaf blocks (`#minecraft:leaves`).
  - When a tree is scanned, the harvester performs an intelligent 3D recursive flood-fill harvest: felling the entire trunk, diagonal branches, and the surrounding leaf canopy up to 32 blocks high and 8 blocks radius.
  - Collects all tree drops into the harvester's buffer: wood logs, saplings, apples, and sticks, auto-exporting them to chests or pipes connected behind or below.
  - Also clears standalone leaf clusters, fully opening the ground for the Automated Planter to replant new saplings and creating a continuous, 100% automated tree farm loop.

---

## [v1.5.5] - 2026-10-26

### Added
- **Automated Planter & Automated Harvester Crafting Recipes**:
  - Added shaped crafting recipes for the **Automated Planter** (`refinery:auto_planter`) and **Automated Harvester** (`refinery:auto_harvester`).
  - **Automated Planter**: Crafted with 4 Iron Ingots, 2 Copper Ingots, 1 Iron Hoe (top-center), 1 Piston (center), and 1 Redstone Dust (bottom-center).
  - **Automated Harvester**: Crafted with 4 Iron Ingots, 2 Copper Ingots, 1 Shears (top-center), 1 Hopper (center), and 1 Redstone Dust (bottom-center).
  - Matches the established industrial agricultural design language alongside the **Industrial Fermenter** (`refinery:fermenter`).

---

## [v1.5.4] - 2026-10-26

### Fixed
- **Autarchic Pulsar Gate Item Spewing**:
  - Fixed issue where autarchic pulsar gates dumped extracted items onto the ground instead of into the connected pipe network.
  - Replaced unsafe standard external injection calls with internal `forceAddItem(TravelingItem, Direction)` handling in Logistics Pipes compat. External insertion was previously failing pipe acceptance rules, causing Logistics to invoke `dropItemInWorld` on every pulse.
  - Implemented smart bidirectional endpoint resolution (`PipeAndTarget`): automatically identifies which adjacent block is the pipe and which is the inventory container, properly supporting gates mounted either onto pipes facing inventories or onto chests/machines facing pipes.
  - Items are now smoothly injected into the pipe's internal traveling queue, moving into the pipe network towards connected endpoints. If insertion cannot be accepted, items remain safely inside the inventory without dropping or vanishing.
- **Pipe Gate Redstone Signal Emission**:
  - Fixed issue where pipe gates configured to emit redstone signals (`REDSTONE_SIGNAL` and `REDSTONE_PULSE`) did not power adjacent redstone dust, repeaters, comparators, or machines.
  - Because pipe gates are collar entities sharing the pipe's block space rather than standalone full blocks, introduced `BlockStateBaseMixin` injecting into `BlockBehaviour$BlockStateBase.getSignal()`, `getDirectSignal()`, and `isSignalSource()`.
  - Maintains a thread-safe, dimension-keyed tracking registry of active powered gate positions (`POWERED_GATE_POSITIONS`). Blocks hosting powered gates output redstone signal level 15 in all directions.
  - Added comprehensive neighbor updates (`updateNeighborsAt`) whenever gate redstone emission toggles on/off or when the gate entity is removed, guaranteeing instant response from adjacent redstone circuits.

---

## [v1.5.3] - 2026-10-26

### Fixed
- **Persistent Chunk "Burnt" State (Anti-Repeat Evaluation)**:
  - Fixed issue where leaving and re-entering chunks could cause chunks to re-evaluate oil generation if the global saved data hadn't flushed to disk.
  - Implemented NeoForge Data Attachment (`refinery:oil_chunk_burnt`) saved **directly inside each chunk's `.mca` region file**. Once a chunk loads and evaluates once in its entire lifetime, it is permanently marked burnt on disk. Leaving and returning to the chunk immediately exits without re-evaluating or re-rolling.
  - Overworld Dimension Guard: Strictly restricts retrogen evaluation to `ServerLevel.OVERWORLD`.
  - Radius Burning: Whenever an oil well generates, all 8 immediate neighbor chunks around the well are also burned immediately to prevent adjacent boundary generation.

---

## [v1.5.2] - 2026-10-25

### Fixed
- **Synchronous Chunk-Loading Cascade & Touching Oil Wells**:
  - Fixed race condition where placing blocks across chunk boundaries synchronously loaded neighboring chunks before the generating well was recorded in `wellChunks`. The well chunk is now reserved in saved data **before** world modification, ensuring all adjacent chunks loaded during generation immediately abort and never spawn touching or duplicate wells.
- **Config Max Distance Clamping**:
  - Expanded `minChunkDistanceBetweenWells` maximum range from `64` to `512` chunks (up to 8,192 blocks) in `RefineryConfig`, allowing large spacing values without being silently clamped. Default increased to `40` chunks (640 blocks).
- **Ocean Biome Over-Generation & Surface Detection**:
  - Added `oceanSpawnChanceMultiplier` (default: `0.20`, an 80% reduction in ocean biomes) to prevent contiguous deep oceans from generating clustered wells.
  - Fixed sea-level water detection in `OilPitGenerator` at Y=62 and Y=63 so ocean oil wells correctly anchor to the seabed rather than floating on the ocean surface.

---

## [v1.5.1] - 2026-10-25

### Changed & Improved
- **Crude Oil Spacing & Generation Balancing**:
  - Reduced default `oilPitSpawnChance` from `0.02` to `0.005` (~1 in 200 chunks instead of 1 in 50) for realistic world distribution.
  - Increased `minChunkDistanceBetweenWells` from `8` chunks to `20` chunks (minimum distance of **320 blocks** between oil wells/pits), eliminating clustered surface deposits on both initial world generation and chunk retrogen.
- **Ethanol Visual Palette Overhaul**:
  - Converted Ethanol (`refinery:ethanol`) fluid still, flowing, and bucket textures from chartreuse yellow-green to an authentic frosted clearish-white liquid palette.
  - Updated in-fluid client fog color to translucent frosted white `(0.92, 0.94, 0.96, 0.85)` and map color to `MapColor.SNOW`.
- **Complete Visual Redo of Pipe Attachments & Chipsets**:
  - **Pipe Wires** (Red, Blue, Green, Yellow): Completely redesigned from plain dots into detailed industrial wire spools featuring metallic center spindles, shaded coiled insulation, and stripped wire leads with copper/silver tips.
  - **Pipe Gates** (Basic, Iron, Gold, Diamond — Normal and Autarchic): Completely redesigned from flat solid blocks into 14x14 beveled industrial attachment plates with tier-specific bezels (slate steel, polished iron, gold, and diamond crystal), corner mounting bolts, recessed dark circuit bays, and central glowing logic cores with pulsing energetic traces on autarchic variants.
  - **Autarchic Chipset**: Redesigned into a dual-in-line IC microchip with gold pins, beveled dark ceramic packaging, alignment notch, gold circuit bus traces, and a central glowing ruby energy core with specular glint.

### Fixed
- **Industrial Multi-Smelter Item Icons**:
  - Fixed missing item definition models for all 6 multi-smelter block items (`multi_smelter_controller`, `smelter_casing`, `smelter_chamber`, `smelter_input_port`, `smelter_output_port`, `smelter_fuel_port`), restoring proper rendering in inventory GUI, hotbars, and in-hand models in Minecraft 26.3.

---

## [v1.5.0] - 2026-10-24

### Added
- **Industrial Multi-Smelter (5x4x3 Heavy Smelter Array)**:
  - New early-to-midgame furnace array multiblock acting as an 8-in-1 super smelter.
  - **Structure (5 Wide × 4 High × 3 Deep = 60 Blocks)**:
    - **Controller** (`refinery:multi_smelter_controller`): Placed at front bottom center (0, 0, 0).
    - **Smelter Structural Casings** (`refinery:smelter_casing`): Built from firebricks, smooth stone, and iron ingots.
    - **Smelter Chamber Glass** (`refinery:smelter_chamber`): Heat-resistant viewing windows across the front 3x2 section to showcase the interior.
    - **Input Port** (`refinery:smelter_input_port`), **Output Port** (`refinery:smelter_output_port`), and **Fuel/Energy Port** (`refinery:smelter_fuel_port`).
    - **Hollow Combustion & Smelting Interior**: The 3 wide × 2 high × 1 deep core (6 blocks) is hollow air, where live dynamic 3D smelting animations render.
  - **8 Parallel Smelting Slots**:
    - Automatically processes any vanilla or modded smelting recipe in up to 8 slots simultaneously (100 ticks / 5 seconds per item).
    - Dedicated 8 Input Slots, 8 Output Slots, and 1 Solid Fuel Slot.
  - **Dual-Power System**:
    - **Solid Fuel / Burnt Material Version**: Accepts Coal, Charcoal, Coal Blocks, Lava Buckets, Blaze Rods, Tar, Bitumen, Ethanol, Logs, and Planks. 1 coal powers all 8 parallel slots concurrently!
    - **Electric / RF Version**: Integrated 50,000 FE buffer. Consumes 30 FE/t when running without requiring coal.
  - **Dynamic In-World 3D Animations**:
    - Burning solid fuel renders a pulsing fiery ember bed and glowing firebox grate.
    - Electric RF operation renders 3 glowing orange induction heating coils.
    - All cooking items actively float, rotate, and shimmer in heat haze inside the hollow chamber in an organized 4x2 matrix.
    - Server-side crackle sounds, smoke, and flame particles emit from the chamber.
  - **Early-Game Accessible (NO Steel Required)**:
    - Entire machine crafts using Iron Ingots, Copper Ingots, Bricks, Furnaces, Smooth Stone, Glass, Hoppers, and Droppers.
    - Can be constructed immediately upon discovering basic copper and iron.
  - **Full Automation & Pipe Support**:
    - Sided WorldlyContainer and NeoForge Item/Energy capabilities allow hoppers and item pipes to feed inputs, extract outputs, and inject fuel/energy.
    - Holographic blueprint projection toggleable via Refinery Wrench for effortless structure construction.

---

## [v1.4.1] - 2026-10-24

### Changed & Balanced
- **Metallurgical Progression Gating**:
  - Removed crafting table shapeless recipe for **Heavy Steel Plate** (`refinery:steel_plate`). Steel plates can now **only** be stamped from Steel Ingots inside the **Industrial Metal Press**.
  - Adjusted **Metal Press Controller** crafting recipe to use Iron Ingots, Steel Ingots, Redstone Blocks, and a Comparator (removed steel plate requirement so the press can be constructed without circular dependencies).
  - Adjusted **Press Anvil Bed** crafting recipe to use Steel Ingots, an Anvil, and Iron Blocks (removed steel plate requirement).
  - Verified that neither the **Blast Furnace** nor the **Metal Press** require Steel Plates, enforcing the linear progression: Blast Smelter $\rightarrow$ Steel Ingots $\rightarrow$ Metal Press $\rightarrow$ Steel Plates $\rightarrow$ Boilers & Advanced Machines.
  - Enhanced Metal Press automation: hoppers and automation pipes can now insert all valid ingots (Steel, Iron, Copper, Gold, Tin, Bronze) directly into the press.

---

## [v1.4.0] - 2026-10-24

### Added
- **Refinery Oil Sand Cracking**:
  - Added dedicated Feedstock Input slot in the Petroleum Refinery GUI directly above the burner slot.
  - Full compatibility with `refinery:oil_sand` and external mod oil sands (including the *Logistics* mod).
  - Processes 1 Oil Sand into:
    - **+200 mB Crude Oil** directly injected into the internal Crude Oil tank.
    - **1 Sand (100% Guaranteed Yield)** ejected to Byproduct output slots.
    - **Petroleum Tar (40% Yield Chance)** ejected to Byproduct output slots.
  - Automated hopper and item pipe insertion directly into the Crude Oil Port or Controller top.
- **Industrial Fermenter (Agricultural Biofuel Machine)**:
  - Added the single-block standalone `refinery:fermenter` machine.
  - Ferments agricultural crops into **Ethanol** biofuel with an 8,000 mB internal storage tank.
  - Supports automatic fluid extraction to pipes/tanks (250 mB/t) and dedicated manual Bucket In/Out slots.
  - Primary recipe: **1 Wheat** $\rightarrow$ **200 mB Ethanol** (5 Wheat = 1 Bucket) + **20% bonus Wheat Seeds**.
  - Also ferments Potatoes (150 mB + 10% Poisonous Potato), Sugar Cane (100 mB + 15% Sugar), Beetroots (100 mB + 20% Seeds), Apples (150 mB), and Sweet Berries (100 mB).
- **Ethanol Fluid & Biofuel Combustion**:
  - Added `refinery:ethanol` fluid, flowing fluid, fluid block, and `refinery:ethanol_bucket`.
  - Ethanol Buckets burn for **4,000 ticks** (200 seconds / 20 items) in Boilers and Combustion Chambers.

---

## [v1.3.0] - 2026-10-24

### Added
- **Continuous Molten Metallurgy Suite**:
  - Registered 6 high-temperature molten fluids with fluid blocks, light emission (level 15), client extensions, and bucket items:
    - **Molten Iron** (`refinery:molten_iron`)
    - **Molten Gold** (`refinery:molten_gold`)
    - **Molten Copper** (`refinery:molten_copper`)
    - **Molten Tin** (`refinery:molten_tin`)
    - **Molten Bronze** (`refinery:molten_bronze`)
    - **Molten Glass** (`refinery:molten_glass`)
- **Electric Arc Melter (3×3×3 Multiblock)**:
  - High-temperature arc furnace that superheats mineral dusts, raw ores, sand, and quartz into 1,000 mB molten liquid batches.
  - 3D Animated carbon electrodes that descend and discharge electric arcs with dynamic sparks and particle bursts.
  - 8,000 mB internal fluid reservoir with automatic push through its Output Port.
- **Open Refractory Casting Channel**:
  - Ceramic trough (`refinery:casting_channel`) that dynamically connects in all horizontal directions and forms vertical down-spouts.
  - Visible flowing glowing liquid renderer displaying the true color of the transported molten metal.
- **Continuous Casting Press (3×3×3 Multiblock)**:
  - Heavy industrial press that receives 1,000 mB of molten fluid and solidifies/stamps it into finished ingots and glass blocks.
  - 3D Animated overhead hydraulic ram that descends under high pressure with quenching steam clouds and mechanical piston sounds.

---

## [v1.2.0] - 2026-10-24

### Changed & Improved
- **Industrial Grinder 3×3×3 Expansion**:
  - Rebuilt the Industrial Grinder from a compact 2×3×3 frame into a full **3×3×3 multiblock**.
  - Added twin physical cylindrical crusher drums with protruding intermeshing steel spikes.
  - Dynamic 3D in-world animation: drums counter-rotate while the item/ore vibrates violently in the crushing nip before pulverizing into dust.
- **BuildCraft-Style Logic Gates & In-Space Snapping**:
  - Pipe Gates now attach directly to the exterior faces of pipes, sharing the same 1×1×1 block space without consuming an extra block.
  - Pipe Wires (Red, Blue, Green, Yellow) render seamlessly along pipe networks.
  - Fixed fluid detection across multiblock machines and train liquid loaders/unloaders.
- **Expanded Metal & Mineral Dust Progression**:
  - Added Tin Dust, Tin Ingot, Tin Plate, Bronze Dust, Bronze Ingot, Bronze Plate, and Coal Dust for broad compatibility across tech mods.

---

## [v1.1.0] - 2026-10-23

### Added
- **BuildCraft Pipe Logic Gates**:
  - Basic, Iron, Gold, and Diamond Gates with conditional triggers and actions.
  - Autarchic Chipset and Autarchic Gates with automated pulsation extraction.
- **Crude Oil World Generation & Retrogen**:
  - Surface oil pits and massive subterranean pressurized oil geysers.
  - Persistent chunk retrogen system ensuring depleted wells never regenerate.
  - Administrative commands (`/refinery spawn_oil_pit`, `/refinery spawn_geyser`, `/refinery stats`).

---

## [v1.0.0] - 2026-10-22

### Initial Release
- **Petroleum Distillation Refinery (3×3×4 Multiblock)**: Fractional distillation of Crude Oil into Diesel, Gasoline, Tar, and Sulfur.
- **Industrial Blast Smelter (5×5×5 Multiblock)**: Carbon steel production.
- **Industrial Metal Press (2×2×2 Multiblock)**: Heavy plate stamping.
- **Thermodynamic Steam Power Suite**:
  - Small Steam Boiler (3×3×3) and Large Heavy Steam Boiler (5×5×5).
  - Steam Turbine Generator (3×3×3) with Rankine cycle water condensation.
- **Agricultural Automation**: Automated Planter and Automated Harvester.
- **Refinery Wrench**: 3D holographic blueprint projections and instant block dismantle.
