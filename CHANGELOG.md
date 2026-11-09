# Changelog — Industrial Refinery

All notable changes to the **Industrial Refinery** mod are documented in this file.

---

## [1.11.60] - 2026-11-08

### Changed
- **Boiler & Steam Turbine Thermodynamic Balance Rebalancing (`SmallBoilerControllerBlockEntity`, `LargeBoilerControllerBlockEntity`, `SteamTurbineControllerBlockEntity`)**:
  - **Small Boilers**: Rebalanced solid fuel steam production to **500 mB/s** (25 mB/t, consuming 12 mB/t water), matching Small Diesel Boilers.
  - **Large Boilers**: Rebalanced solid fuel steam production to **1,500 mB/s** (75 mB/t, consuming 37 mB/t water), matching Large Diesel Boilers.
  - **Steam Turbine Steam Requirement**: Tuned steam intake to **1,450 mB/s** (72.5 mB/t average) at peak generation (10,000 FE/t). 1 Large Boiler or 3 Small Boilers (1,500 mB/s) now provide a steady 50 mB/s surplus to keep the 32,000 mB steam buffer full. Recycles 725 mB/s water back into the closed loop.
- **Dynamic Turbine RPM Scaling & Centered Diagnostic Readout (`SteamTurbineScreen`, `SteamTurbineControllerBlockEntity`)**:
  - Turbine speed now dynamically scales with power generation up to **3,000 RPM at peak generation** (10,000 FE/t; 0 RPM when idle/standby).
  - Diagnostic monitor now displays centered text showing exact live RPM (`3000 RPM`) and power (`+10000 FE/t`).
  - Active turbine blade indicator animation speed and hum sound pitch dynamically increase with RPM.

### Fixed
- **Fluid Pipe Pipeline Flow Bottleneck & Directional Momentum (`IndustrialPipeBlockEntity`, `RefineryMod`)**:
  - Fixed an issue where pipes equalized fluid using naive static half-difference division (`(diff + 1) / 2`), halving throughput across every pipe block and throttling long pipelines down to ~24 mB/t.
  - Implemented directional flow momentum (`recordFluidReceived` and `lastReceivedFrom` tracking): pipes actively push fluid forward along the pipeline into downstream pipes and connected machine ports at the full material transfer rate (Iron: 500 mB/t, Steel: 1,500 mB/t) without distance attenuation or backward sloshing.
  - Steam turbine steam input port now immediately notifies the controller on fluid insertion to maintain seamless client-server state synchronization.
- **Steam Turbine Generator & Diesel Generator NeoForge Transaction Handling (SteamTurbineControllerBlockEntity, DieselGeneratorBlockEntity)**:
  - Replaced manual state assignments in xtract(...) with NeoForge's standard onEnergyChanged(int previousAmount) handler on SimpleEnergyHandler.
  - Resolved a severe ghost energy drain bug where external simulated transactions (such as AWG cables, fluxducts, or wire networks probing for grid demand) temporarily decremented energy storage and desynchronized the controller's internal nergy field without rolling back when the simulation aborted.
  - Prevents generators from constantly bleeding generated FE into the void when connected to fully-charged energy storage banks or satisfied grids.

---

## [1.11.58] - 2026-11-08

### Added
- **In-Game Field Manual Boiler Guide Pages (`GuideBookRegistry`)**:
  - Added dedicated, fully illustrated guide book pages with full multi-layer slice schematics, required block lists, and operational specifications for all four boiler multiblock types:
    - **Small Steam Boiler (3x3x3)**: 27-block compact solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 1 Firebox, 1 Pressure Pipe, 21 Steel Boiler Casings) producing up to 100 mB/t steam with 2 fuel slots.
    - **Large Heavy Steam Boiler (5x5x5)**: 125-block heavy-duty solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 9 Fireboxes, 27 Pressure Pipes, 85 Steel Boiler Casings) producing up to 300 mB/t steam with 8 fuel slots across 5 layer schematics.
    - **Small Diesel Boiler (3x3x3)**: 27-block liquid fuel boiler utilizing Diesel, Biodiesel, or Ethanol via fluid fuel port.
    - **Large Diesel Boiler (5x5x5)**: 125-block industrial liquid fuel boiler producing 300 mB/t steam with high-efficiency fluid burner combustion.

---

## [1.11.57] - 2026-11-08

### Changed
- Mod update

---

## [1.11.57] - 2026-11-08

### Changed
- Mod update

---

## [1.11.57] - 2026-11-08

### Fixed
- **Large Boiler & Diesel Boiler Multiblock Block Hiding (`LargeBoilerControllerBlock`, `LargeDieselBoilerControllerBlock`, `SmallDieselBoilerControllerBlock`, `SteelBoilerCasingBlock`)**:
  - Unified the `ASSEMBLED` blockstate boolean property across all boiler controllers and member blocks (`SmallBoilerControllerBlock.ASSEMBLED`).
  - Fixed an issue where the 124 member blocks (casings, pressure pipes, fireboxes, and ports) failed to hide their physical voxel models upon multiblock formation, obscuring the monolithic 5x5x5 3D boiler package model.
  - Expanded `SteelBoilerCasingBlock.findNearbyController` search radius from 3 to 5 blocks to guarantee all blocks across 5x5x5 multiblocks properly discover and trigger their controller.
- **Steam Turbine Generator Diagnostic Monitor & Operational State (`SteamTurbineControllerBlockEntity`, `SteamTurbineMenu`, `SteamTurbineScreen`)**:
  - Fixed active generation state detection in ContainerData: the turbine no longer incorrectly reports `STANDBY / 0 RPM / 0 FE/t` when consuming steam every tick down to 0.
  - Added live generated power sync (`menu.getFeGenerated()`), dynamically displaying actual power output (e.g. `+10,000 FE/t` or throttled load output) instead of static/idle text.
  - Corrected steam buffer tooltip typo from `100 mB/t` to `300 mB/t` at peak load.
  - Fixed rotor tooltip showing "ACTIVE" in dark gray during standby; now correctly shows `ACTIVE` (green) or `IDLE` (gray).
- **Steam Turbine Generator Energy Sync & Dynamic Load Throttling (`SteamTurbineControllerBlockEntity`, `TurbinePortBlockEntity`)**:
  - Fixed broken `energyStorage.extract(...)` logic that self-assigned `this.energy = this.energy;` instead of querying `getAmount()`, preventing external energy extraction from updating the block entity state.
  - Removed duplicate energy and water auto-push logic from the controller BE that collided with the dedicated energy and water output ports.
  - Capped auto-push rate to the rated `ENERGY_PER_TICK` (10,000 FE/t) and streamlined internal battery capacity to 100,000 FE so the turbine rapidly fills surplus storage and throttles steam consumption to match external cable/battery draw (e.g. 240 mB/t at 8,000 FE/t load, leaving 60 mB/t boiler surplus to accumulate in the steam buffer).

---

## [1.11.54] - 2026-11-08

### Changed
- Mod update

---

## [1.11.53] - 2026-11-08

### Changed
- Mod update

---

## [1.11.57] - 2026-11-08

### Fixed
- **Large Boiler & Diesel Boiler Multiblock Block Hiding (`LargeBoilerControllerBlock`, `LargeDieselBoilerControllerBlock`, `SmallDieselBoilerControllerBlock`, `SteelBoilerCasingBlock`)**:
  - Unified the `ASSEMBLED` blockstate boolean property across all boiler controllers and member blocks (`SmallBoilerControllerBlock.ASSEMBLED`).
  - Fixed an issue where the 124 member blocks (casings, pressure pipes, fireboxes, and ports) failed to hide their physical voxel models upon multiblock formation, obscuring the monolithic 5x5x5 3D boiler package model.
  - Expanded `SteelBoilerCasingBlock.findNearbyController` search radius from 3 to 5 blocks to guarantee all blocks across 5x5x5 multiblocks properly discover and trigger their controller.
- **Steam Turbine Generator Diagnostic Monitor & Operational State (`SteamTurbineControllerBlockEntity`, `SteamTurbineMenu`, `SteamTurbineScreen`)**:
  - Fixed active generation state detection in ContainerData: the turbine no longer incorrectly reports `STANDBY / 0 RPM / 0 FE/t` when consuming steam every tick down to 0.
  - Added live generated power sync (`menu.getFeGenerated()`), dynamically displaying actual power output (e.g. `+10,000 FE/t` or throttled load output) instead of static/idle text.
  - Corrected steam buffer tooltip typo from `100 mB/t` to `300 mB/t` at peak load.
  - Fixed rotor tooltip showing "ACTIVE" in dark gray during standby; now correctly shows `ACTIVE` (green) or `IDLE` (gray).
- **Steam Turbine Generator Energy Sync & Dynamic Load Throttling (`SteamTurbineControllerBlockEntity`, `TurbinePortBlockEntity`)**:
  - Fixed broken `energyStorage.extract(...)` logic that self-assigned `this.energy = this.energy;` instead of querying `getAmount()`, preventing external energy extraction from updating the block entity state.
  - Removed duplicate energy and water auto-push logic from the controller BE that collided with the dedicated energy and water output ports.
  - Capped auto-push rate to the rated `ENERGY_PER_TICK` (10,000 FE/t) and streamlined internal battery capacity to 100,000 FE so the turbine rapidly fills surplus storage and throttles steam consumption to match external cable/battery draw (e.g. 240 mB/t at 8,000 FE/t load, leaving 60 mB/t boiler surplus to accumulate in the steam buffer).

---

## [1.11.54] - 2026-11-08

### Added
- **Multi-Smelter Plank Pyrolysis (Planks to Charcoal) (`MultiSmelterControllerBlockEntity`, `MultiSmelterMenu`, `GuideBookRegistry`)**:
  - Added support for converting any wooden planks (`#minecraft:planks`) directly into Charcoal (`minecraft:charcoal`) inside the 5x4x3 Heavy Multi-Smelter.
  - Planks in any of the 8 parallel input slots are now smelted into Charcoal at full speed (5s / 100 ticks per batch) using electric induction or solid fuel.
  - Updated `canPlaceItem` and `canPlaceItemThroughFace` so hoppers, conveyors, and pipe logistics feeding the top face or input ports smoothly load wooden planks.
  - Enhanced GUI shift-click routing in `MultiSmelterMenu` to prioritize smeltable materials (including planks and logs) into available input slots before routing overflow to the fuel combustion slot.
  - Added Plank Pyrolysis to the in-game Field Manual Guide Book under the Heavy Multi-Smelter section.

---

## [1.11.51] - 2026-11-08

### Fixed
- **Iron Item Pipe & Cast Iron Fluid Pipe Recipe Collision Fix (`cast_iron_fluid_pipe.json`)**:
  - Resolved crafting recipe collision where Iron Item Pipes (`refinery:iron_item_pipe`) and Cast Iron Fluid Pipes (`refinery:cast_iron_fluid_pipe`) both required the identical recipe (`iron_ingot + glass + iron_ingot`).
  - Restored Iron Item Pipe as the exclusive owner of the standard horizontal `iron_ingot + glass + iron_ingot` recipe.
  - Changed Cast Iron Fluid Pipe to craft from `minecraft:iron_bars + minecraft:glass + minecraft:iron_bars` (`data/refinery/recipe/cast_iron_fluid_pipe.json`), perfectly matching the heavy cast lattice profile of iron bars.
  - Added an alternative smelting/alloy shaped recipe (`data/refinery/recipe/cast_iron_fluid_pipe_from_coal.json`) crafting from `iron_ingot + glass + iron_ingot` with Coal/Charcoal (`#minecraft:coals`) above the glass.

---

## [1.11.49] - 2026-11-08

### Fixed
- **Industrial Diesel Generator Single-Item Drop Fix (`DieselGeneratorBlock`)**:
  - Resolved an issue where destroying a placed Industrial Diesel Generator (`refinery:diesel_generator`) in survival dropped two generator items.
  - Overrode `getDrops` in `DieselGeneratorBlock` so only the master `LEFT` part returns `List.of(new ItemStack(this))` while the slave `RIGHT` part strictly returns an empty list `List.of()`.
  - Updated `playerWillDestroy` to safely clear and drop container items, silently remove the partner block with flag 35 and break particles, and pop strictly 1 generator item only when a survival player breaks the non-master `RIGHT` block.
  - Added `data/refinery/loot_tables/blocks/diesel_generator.json` and ensured both `loot_table` and `loot_tables` enforce `part = left` state conditions.

---

## [1.11.47] - 2026-11-08

### Changed
- **Autarchic Chipset Recipe Yield (4x Multiplier)**:
  - Increased the crafting output of the Autarchic Chipset recipe from 1 to 4 chipsets per craft (`data/refinery/recipe/autarchic_chipset.json`).
  - Standard crafting (1x Ender Pearl + 2x Redstone Dust + 2x Gold Nugget) now yields 4x Autarchic Chipsets, quadrupling production efficiency for gate upgrades and automated pulsar automation.

---

## [1.11.45] - 2026-11-08

### Changed
- **Per-Tick Machine Energy Requirements Across All Machines (`*ControllerBlockEntity`, `FermenterBlockEntity`)**:
  - Removed artificial whole-process and excessive buffer requirements (previously checking `(PROCESS_TIME - progress) * ENERGY_PER_TICK` or multi-thousand FE thresholds) across all machines.
  - Machines now fire off and operate as long as they have at least their nominal per-tick power (`ENERGY_PER_TICK`):
    - **Continuous Casting Press**: Starts immediately with $\ge$ 600 FE (instead of requiring 240,000 FE).
    - **Industrial Rock Crusher / Grinder**: Starts immediately with $\ge$ 2,000 FE (instead of requiring 120,000 FE).
    - **Industrial Metal Press**: Starts immediately with $\ge$ 500 FE (instead of requiring 20,000 FE).
    - **Industrial Multi-Smelter**: Starts immediately with $\ge$ 5,000 FE (instead of requiring 500,000 FE).
    - **Electric Arc Melter**: Starts immediately with $\ge$ 10,000 FE (instead of requiring 50,000+ FE).
    - **Industrial Fermenter**: Starts immediately with $\ge$ 15 FE (instead of requiring 900 FE).
- **Casting Channel Flow Speed Boost & Anti-Bounce-Back (`CastingChannelBlockEntity`)**:
  - Increased molten fluid transfer rate in open refractory casting channels to 50 mB every 2 ticks (25 mB/tick, a 4x increase from the previous 6.25 mB/tick rate).
  - Reduced initial viscous flow propagation delay from 25 ticks to 10 ticks (0.5s).
  - Completely eliminated the fluid bounce-back mechanic: fluid flowing down a channel will strictly proceed downstream and can never reverse direction back into the upstream feed.

---

## [1.11.44] - 2026-11-08

### Added
- **Continuous Casting Press Water Quenching Requirement (1:1 Ratio with Molten Metal) (`CastingPressControllerBlockEntity`, `CastingPressMenu`, `CastingPressScreen`, `RefineryMod`)**:
  - Added a dedicated **4,000 mB Quenching Water Tank** alongside the 4,000 mB Molten Metal Basin on the Continuous Casting Press.
  - Each casting and quenching cycle now requires a **1:1 fluid ratio**: **1,000 mB of Water** and **1,000 mB of Molten Metal** per ingot/block cast (consumed together at the high-pressure quenching point).
  - Registered `MultiFluidHandler` capability across the Casting Press controller, input port, casing, basin, and chamber blocks so both Water and Molten Metal can be piped in simultaneously or filled by right-clicking with Water Buckets / Molten Metal Buckets.
  - Updated `CastingPressScreen` to render dual side-by-side fluid gauges (`Molten Inflow Basin` and `Quenching Water Tank`) with detailed hover tooltips.

### Changed
- **Electric Arc Melter 50,000 FE Start Threshold (`ArcMelterControllerBlockEntity`)**:
  - Changed the Electric Arc Melter so that it begins melting a batch as soon as its internal energy buffer reaches **50,000 FE** (`MIN_START_ENERGY = 50_000`) instead of requiring the full 2,000,000 FE buffer upfront before starting.
- **Continuous Casting Press Ground-Level Input Port (`CastingPressControllerBlockEntity`, `GuideBookRegistry`)**:
  - Swapped the `Casting Input Port` at `(0, 1, 2)` (Layer 1, rear-center) with the `Casting Press Casing` block below it at `(0, 0, 2)` (Layer 0, rear-center), allowing ground-level `Y=0` Open Casting Channels from the Electric Arc Melter to feed straight into the Casting Input Port without elevation changes.
- **Multiblock Hologram & Guide Book Name Parity with JEI (`*ControllerBlockEntity`, `StorageTankBlockEntity`, `BlueprintColors`, `GuideBookRegistry`)**:
  - Updated all 3D blueprint hologram labels across all 14 multiblock machines (Industrial Blast Furnace, Electric Arc Melter, Continuous Casting Press, 3-Stage Petroleum Refinery, Industrial Grinder, Industrial Metal Press, Heavy Multi-Smelter, Small & Large Steam Boilers, Small & Large Diesel Boilers, Steam Turbine Generator, Heavy Pit Quarry, and Multiblock Liquid Storage Tank) and the in-game Field Manual Guide Book so every block label matches its exact localized item name in JEI (`en_us.json`).

---

## [1.11.42] - 2026-11-07

### Added
- **In-Game Guide Book Energy & Power Specifications (`RefineryGuideScreen`, `GuideEntry`, `GuideBookRegistry`)**:
  - Added prominent energy and power specification badges (`⚡ Power / Energy: ...`) to the Industrial Field Manual for every machine, multiblock, and tool.
  - Detailed live rates across all devices: Electric Arc Melter (10,000 FE/t), Heavy Multi-Smelter (5,000 FE/t or solid fuel), Industrial Grinder (2,000 FE/t), Continuous Casting Press (600 FE/t), Hydraulic Metal Press (500 FE/t), Electric Refinery Furnace (500 FE/t), Steam Turbine Generator (+10,000 FE/t), Heavy Pit Quarry (240 FE/t default), Industrial Diesel Generator (+256 FE/t), Electric Fluid Pump (400 FE/cycle, ~40 FE/t), Industrial Fermenter (15 FE/t), Crude Oil Pump Jack (1–4 FE/t), Auto Harvester (1 FE/t + 20 FE/crop), Auto Planter (1 FE/t + 10 FE/action), and unpowered solid-fuel/liquid-fuel boilers and logistics (0 FE/t).
  - Added smooth mouse wheel scrolling (`contentScrollOffset`) and amber visual scrollbars to the Overview tab, Bill of Materials, and Recipes tabs in `RefineryGuideScreen` to ensure extensive machine documentation never truncates.
  - Added dedicated Guide Book entry for **Diesel Steam Boilers (Small & Large)** (`diesel_boilers`) with complete 3x3x3 multiblock bill of materials, layer diagrams, and diesel fuel consumption rates.
  - Added Oil Sand Feedstock Extraction recipe (200 mB Crude Oil + 1 Sand per heated block) to the Petroleum Refinery guide entry.

### Changed
- **Continuous Casting Press Cycle Duration (`CastingPressControllerBlockEntity`, `CastingPressModel`, `GuideBookRegistry`)**:
  - Increased casting stroke cycle time from 12.0s (240 ticks) to **20.0 seconds (400 ticks)** per process stroke.
  - Dynamically scaled 3D animated hydraulic stamping ram (approach stroke, high-pressure dwell hold, and smooth hydraulic retraction) and molten basin solidification across the full 400-tick duration.
  - Total energy consumed per stroke is 240,000 FE (600 FE/t over 400 ticks).
- **Electric Arc Melter Batch Duration (`ArcMelterControllerBlockEntity`, `ArcMelterModel`, `GuideBookRegistry`)**:
  - Increased ore melting cycle time from 6.0s (120 ticks) to **10.0 seconds (200 ticks)** per batch.
  - Dynamically synchronized the 3D electrode arc plasma animation and crucible molten bath filling to the new 200-tick duration.
  - Total energy consumed per batch is 2,000,000 FE (10,000 FE/t over 200 ticks), exactly matching the full 2,000,000 FE internal buffer.

---

## [1.11.40] - 2026-11-07

### Changed
- **Electric Arc Melter Power Consumption & Storage (`ArcMelterControllerBlockEntity`, `ArcMelterScreen`)**:
  - Rebalanced power consumption to **10,000 FE/t** (up from 40 FE/t) for high-voltage industrial smelting.
  - Expanded internal energy storage buffer from 50,000 FE to **2,000,000 FE**, with maximum receive rate increased to **100,000 FE/t** to reliably buffer and power full 120-tick melt cycles.
  - Updated `ArcMelterScreen` GUI energy bar scaling and tooltips to display live capacity and `10,000 FE/t` consumption rate.
- **Steam Turbine Generator Output & Steam Consumption Ratio (`SteamTurbineControllerBlockEntity`, `SteamTurbineScreen`, `TurbinePortBlockEntity`)**:
  - Rebalanced electrical generation to **10,000 FE/t** (up from 1,000 FE/t).
  - Scaled steam consumption rate to **300 mB/t** (up from 50 mB/t), requiring 3x the steam output of a Small Steam Boiler (3 × 100 mB/t).
  - Maintained closed-loop Rankine cycle condensation ratio (2 mB steam -> 1 mB water), returning **150 mB/t** of condensed water back to boilers.
  - Increased maximum energy export rate to **50,000 FE/t** and added active energy auto-push from both the controller and Energy Output Port into adjacent energy handlers and cables.
  - Updated `SteamTurbineScreen` tooltips to reflect `10,000 FE/t` generation and `50,000 FE/t` output.
- **Large Steam Boiler Steam & Water Production (`LargeBoilerControllerBlockEntity`, `LargeDieselBoilerControllerBlockEntity`, `LargeDieselBoilerScreen`)**:
  - Rebalanced Large Steam Boiler production to **300 mB/t** High Pressure Steam consuming **150 mB/t** Water, exactly matching the combined production of 3 Small Steam Boilers (3 × 100 mB/t steam, 3 × 50 mB/t water).
  - Rebalanced Large Diesel Boiler production to **750 mB steam / cycle** (consuming 300 mB water & 30 mB diesel per 10-tick cycle), exactly matching 3x the Small Diesel Boiler.
  - Updated `LargeDieselBoilerScreen` tooltips with the new consumption and production rates.
- **Industrial Multi-Smelter Electric Energy Consumption (`MultiSmelterControllerBlockEntity`, `MultiSmelterScreen`)**:
  - Rebalanced electric induction power consumption to **5,000 FE/t** (up from 30 FE/t) when running on RF/FE.
  - Expanded internal energy storage buffer from 50,000 FE to **1,000,000 FE**, with maximum receive rate increased to **50,000 FE/t**.
  - Updated `MultiSmelterScreen` GUI energy bar scaling, coil activation threshold, and tooltips to show `5,000 FE/t` electric smelting consumption.
- **Industrial Grinder Power Consumption & Storage (`IndustrialGrinderControllerBlockEntity`, `IndustrialGrinderScreen`)**:
  - Rebalanced power consumption to **2,000 FE/t** (up from 30 FE/t).
  - Expanded internal energy storage buffer from 50,000 FE to **500,000 FE**, with maximum receive rate increased to **50,000 FE/t**.
  - Updated `IndustrialGrinderScreen` GUI energy bar scaling and tooltips to show `2,000 FE/t` grinding consumption.
- **Industrial Metal Press Power Consumption & Storage (`MetalPressControllerBlockEntity`, `MetalPressScreen`)**:
  - Rebalanced power consumption to **500 FE/t** (up from 20 FE/t).
  - Expanded internal energy storage buffer from 50,000 FE to **100,000 FE**, with maximum receive rate increased to **20,000 FE/t**.
  - Updated `MetalPressScreen` GUI energy bar scaling and tooltips to show `500 FE/t` stamping consumption.
- **Continuous Casting Press Power Consumption & Storage (`CastingPressControllerBlockEntity`, `CastingPressScreen`)**:
  - Rebalanced power consumption to **600 FE/t** (up from 40 FE/t).
  - Expanded internal energy storage buffer from 50,000 FE to **500,000 FE**, with maximum receive rate increased to **20,000 FE/t**.
  - Updated `CastingPressScreen` GUI energy bar scaling and tooltips to show `600 FE/t` casting consumption.

---

## [1.11.39] - 2026-11-07

### Added
- **Fast 0.5-Second Pipe Gate Redstone Pulsing (`GateAction.REDSTONE_PULSE_HALF_SEC`)**:
  - Added an extra redstone pulse option to all Pipe Gates (`Basic`, `Iron`, `Gold`, `Diamond`, and autarchic variants): `Redstone Pulse (0.5s)` (GUI short label `Pls .5s`), emitting rapid periodic redstone pulses alternating every 10 ticks (0.5 seconds on, 0.5 seconds off / 1.0 Hz clock frequency).
  - Retained the existing 1.0-second pulse option (`Redstone Pulse (1s)` / `Pls 1s`) for standard 20-tick period clocking.
  - Updated both `PipeGateBlockEntity` and `PipeGateEntity` to process `REDSTONE_PULSE_HALF_SEC` every tick via `(gameTime / 10) % 2 == 0`.
  - Appended `REDSTONE_PULSE_HALF_SEC` to enum values after existing entries, fully preserving ordinal backwards compatibility for existing world saves and network container data synchronization.

### Changed
- **Pipe Gate Action Natural Cycle Order (`GateAction.CYCLE_ORDER`)**:
  - Grouped gate actions into a natural cycling sequence (`None` -> `Emit Redstone` -> `Redstone Pulse (1s)` -> `Redstone Pulse (0.5s)` -> `Autarchic Pulses` -> `Pipe Wires`) for intuitive forward/backward selection in `GateScreen`.
- **In-Game Field Manual & Documentation (`GuideBookRegistry`)**:
  - Updated the Field Manual Pipe Gates entry to detail both 1.0s and rapid 0.5s redstone pulsing options for high-frequency clocking and automation circuits.

---

## [1.11.38] - 2026-11-07

### Added
- **AWWA Safety-Yellow Fire Hydrant (`FireHydrantBlock`, `FireHydrantBlockEntity`, `FireHydrantModel`, `FireHydrantRenderer`, `FireHydrantRenderState`)**:
  - Added `refinery:fire_hydrant`, an industrial safety-yellow standpipe fire hydrant that connects to fluid pipes and fills buckets when right-clicked with an empty bucket in hand.
  - **High-Detail 3D Model**: Modeled with a black cast-iron bottom flange ring with 8 silver hex nuts/studs, yellow lower flange with silver bolt heads, ribbed yellow barrel with front embossed circular `A` emblem and `250 AWWA` markings, front 4.5" pumper/steamer nozzle cap with `B 41` pentagonal nut, left and right 2.5" side hose nozzles with wrench nuts and bottom chain-loop lugs, upper double bonnet flange with perimeter bolts, domed yellow bonnet cap, black thrust washer, and an animated top brass pentagonal operating stem nut.
  - **Pipe-Fed Liquid Buffering & Contiguous Pipeline Draw**: Accepts any bucketable liquid from connected Industrial Fluid Pipes into an 8,000 mB internal reservoir and pulls from contiguous connected fluid pipes on demand when filling a bucket.
  - **Bucket Filling & Player Interaction**: Right-clicking with an empty bucket (`minecraft:bucket`) consumes 1,000 mB from the hydrant to return the corresponding filled bucket with splash particles, bucket fill audio, and top stem nut rotation animation. Empty-hand right-click inspects tank status; shift-right-click flushes residual fluid; Refinery Wrench rotates facing.
  - Added crafting recipe (`data/refinery/recipe/fire_hydrant.json`), loot table, pickaxe mineable tag entry, and Field Manual Guide Book documentation.

---

## [1.11.37] - 2026-11-07

### Added
- **Configurable Heavy Pit Quarry Max Speed & Energy Rates (`RefineryConfig`, `refinery.toml`)**:
  - Added `quarryEnergyPerTick` (default `240` FE/t, range `1` to `100,000`) and `quarryMaxMiningSpeed` (default `1.5` blocks/s, range `0.05` to `50.0`) under the `[Heavy_Pit_Quarry]` section in `config/refinery.toml`.
  - Settings are configured strictly through the mod config file (not changeable in-game) to ensure server authority and modpack balance.
  - Dynamically synchronized config values to client GUI via container data slots (`QuarryMenu`, `QuarryControllerBlockEntity`), updating the energy gauge tooltip to show live rates (`240 FE/t (1.5 blocks/s)`).

### Changed
- **Quarry Operational Rates Tuning (`QuarryControllerBlockEntity`)**:
  - Increased base excavation speed from 1.0 block/s to **1.5 blocks/second** (~13.3 ticks per standard block).
  - Material speed multipliers scale accordingly: soft overburden (dirt, sand, gravel, tuff) at 2x (**3.0 blocks/s**); precious ores (iron, diamond) at 0.5x (**0.75 blocks/s**).
  - Raised maximum power draw from 20 FE/t to **240 FE/tick** at full speed, with linear proportional throttling when under-powered.

---

## [1.11.35] - 2026-11-06

### Fixed
- **Perimeter Safety Fence Preservation During Quarry Deep Mining (`QuarryControllerBlockEntity`)**:
  - Fixed an issue where the quarry top-down obstruction scanner would re-mine and destroy the perimeter safety fence immediately after placing it in Phase 1.
  - In Phase 2 (`PHASE_MINING`), top obstruction monitoring is now strictly restricted to the inner excavation volume ($Y \le -1$), completely ceasing monitoring of the upper bench ($Y \ge 0$).
  - Added explicit exclusion for fences (`FenceBlock`, `BlockTags.FENCES`) and walls (`WallBlock`, `BlockTags.WALLS`) in `findTopObstruction`, ensuring safety fences around the pit rim are permanently preserved.
- **Stone-to-Cobblestone Throw-Back Equivalence (`QuarryControllerBlockEntity`)**:
  - Marking `Stone` as a Throw-Back block in the Geological Manifest or filter slot now automatically treats mined `Cobblestone` drops as throwbacks, ejecting them out the rear conveyor chute into the open pit void instead of filling the factory buffer.
  - Symmetrically synchronized `Cobblestone` $\leftrightarrow$ `Stone` and `Deepslate` $\leftrightarrow$ `Cobbled Deepslate` pairs for both individual manifest toggles, the `[SPOIL]` bulk toggle button, and Slot 10 filter checks.
  - Guaranteed that any drop resulting from mining a marked throwback block is automatically routed to rear spoil disposal.

---

## [1.11.33] - 2026-11-06

### Added
- **In-Game Field Manual Guide Book Entries (`GuideBookRegistry`)**:
  - **Heavy Pit Quarry (`GuideCategory.MULTIBLOCKS`)**: Added interactive multiblock guide entry complete with 3-layer blueprint inspector (Layer 0, Layer 1, Layer 2), full material requirements checklist (1 Heavy Pit Quarry Controller + 26 Quarry Structural Casings), crafting recipes, and comprehensive operational handbook (180° inverted pit digging behind the rig, top-down clearing, rear spoil ejection, and side conveyor routing).
  - **Crude Oil Pump Jack (`GuideCategory.MACHINERY`)**: Added industrial beam pumping unit guide entry detailing the 2x3x3 footprint, downhole vertical drilling reach up to 50 blocks beneath the wellhead, power-scaled extraction rates (1–4 FE/t), and crafting recipe.
  - **Bitumen Storage Pile (`GuideCategory.LOGISTICS`)**: Added open-air asphalt stockpile guide entry detailing the 3x3 ground perimeter, 8-stage dynamic mound growth up to 16,384 chunks (256 stacks), vacuum item ingestion, and harvesting mechanics.
  - **Fluid Pumps & In-World Discharge (`GuideCategory.MACHINERY`)**: Added documentation for Coal, Diesel, and Electric fluid extraction pumps alongside the pressurized Fluid Discharge nozzle.
- **Refinery Wrench Blueprint Projection Support (`RefineryWrenchItem`)**:
  - Right-clicking the Heavy Pit Quarry Controller with a Refinery Wrench now toggles the 3D blueprint hologram wireframe in-world with auditory button click feedback, matching all other industrial multiblock machines.

### Changed
- **Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Composition (`QuarryControllerBlockEntity`, `RefineryMod`)**:
  - Simplified the Heavy Pit Quarry multiblock structure to strictly require **1 Heavy Pit Quarry Controller** and **26 Quarry Structural Casings**.
  - All 26 blueprint requirements and structure validation checks now uniformly require and display `Quarry Structural Casing`, eliminating confusing internal subcomponent labels ("Quarry Left Conveyor Port", "Quarry Turntable Base", etc.) that misled players into searching for non-existent blocks.
  - Completely removed `QuarryFrameBlock` (Quarry Lattice Frame) and its assets/recipes from the mod, streamlining crafting requirements so players only need controller and casing blocks.

---

## [1.11.32] - 2026-11-06

### Changed
- Mod update

---

## [1.11.32] - 2026-11-06

### Changed
- **Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Recipe (`QuarryControllerBlockEntity`, `RefineryMod`)**:
  - Simplified the Heavy Pit Quarry multiblock composition to strictly require **1 Heavy Pit Quarry Controller** and **26 Quarry Structural Casings**.
  - All 26 blueprint requirements and structure validation checks now uniformly require `Quarry Structural Casing`.
  - Removed `QuarryFrameBlock` (Quarry Lattice Frame) and its assets/recipes from the mod, streamlining crafting requirements so players only need controller and casing blocks.

---

## [1.11.30] - 2026-11-06

### Added
- **Rear High-Intensity Work Floodlights (`QuarryModel`, `QuarryControllerRenderer`)**:
  - Added dual rear-facing halogen work floodlights mounted to the overhead lattice gantry tower illuminating the pit behind the machine.
  - Rear floodlights emit full brightness (`FULL_BRIGHT`) when the excavation rig is powered and active, matching the front floodlights.
- **Safe Multiblock Player Displacement (`QuarryControllerBlockEntity`)**:
  - When the 3x3x3 multiblock is completed and assembled, any player whose bounding box is inside or standing on the multiblock volume is safely relocated to the ground in front of the operator station, completely preventing players from getting their head trapped in the casing or frame collision.

### Changed
- **Quarry Excavation Heights & Bedrock Progression (`QuarryControllerBlockEntity`)**:
  - **Outer Box Clearing Up 3 Blocks**: The outer $43 \times 43 \times 3$ clearing area now clears top-down from the same Y level as the machine up 3 blocks ($Y = +2$ down to $Y = 0$).
  - **Safety Perimeter Railing**: The automated safety fence is installed at $Y = 0$ along the bench rim surrounding the deep excavation pit.
  - **Inner Deep Pit**: The inner $41 \times 41$ pit starts below the machine level at $Y = -1$ and digs continuously layer by layer all the way down to bedrock (`level.getMinY()`).
- **Conveyor Chute Removal & Dedicated Built-in Spoil Discharge (`QuarryModel`, `QuarryControllerRenderer`, `QuarryControllerBlockEntity`)**:
  - Removed the front conveyor chute geometry, keeping the operator station clean and open.
  - Items configured as Throw-Back blocks automatically roll off the built-in rear conveyor chute (`getRelativePos(0, 0, 3, facing)`) and tumble directly into the excavation pit void without requiring external pipes or conveyors.
- **Buffer Retention & Side Conveyor/Pipe Export (`QuarryControllerBlockEntity`)**:
  - Kept items (ores, minerals, non-throwbacks) accumulate in the machine's 9-slot internal buffer.
  - Export only begins when a normal conveyor (or belt below) or pipe (`IndustrialPipeBlockEntity`, `BufferPipeBlockEntity`, or item capability container) is connected to the Left or Right side ports.
  - If the buffer is full and no side conveyor or pipe is connected, mining safely pauses until buffer space becomes available.
- **Strict 3-Block Multiblock Validation (`QuarryControllerBlockEntity`)**:
  - Confirmed and verified that all 27 blueprint requirements in `getRequirements()` strictly refer to the 3 real craftable blocks: `Heavy Pit Quarry Controller`, `Quarry Structural Casing`, and `Quarry Lattice Frame`, with no references to legacy turntable or conveyor port blocks.

---

## [1.11.28] - 2026-11-06

### Added
- **Heavy Pit Quarry Throw-Back Blocks & Pit Spoil Rejection System (`QuarryControllerBlockEntity`, `QuarryMenu`, `QuarryScreen`, `QuarryModel`, `QuarryControllerRenderer`)**:
  - **Rear Conveyor Pit Spoil Ejector**: Added a dedicated rear conveyor chute on the quarry rig (`getRelativePos(0, 0, 3, facing)`) pointing directly back out over the pit. When any block flagged as a "Throw Back" block is mined, it bypasses factory exports and is automatically ejected via the rear conveyor back into the pit void.
  - **Interactive Geological Manifest Toggle**: In the Quarry GUI Manifest Monitor, clicking on any detected block type toggles its Throw-Back status. Active throw-back blocks are highlighted with a vibrant orange border and a `[TB]` badge. Hover tooltips indicate `[THROW BACK TO PIT]` vs `[NORMAL EXPORT]`.
  - **Quick `[SPOIL]` Overburden Preset**: Added an instant `[SPOIL]` toggle button in the Manifest Monitor header that toggles all common overburden materials (Dirt, Sand, Gravel, Stone, Deepslate, Tuff, Cobblestone, Andesite, Diorite, Granite) with a single click.
  - **Dedicated Throw-Back Filter Slot**: Added a dedicated Throw-Back / Spoil filter slot (Slot 10) in the GUI at `(220, 85)` where players can insert any block to flag it for rear ejection into the pit.
  - **3D Animated Spoil Discharge**: Added the rear conveyor chute geometry and dynamic animated items to the monolithic 3D excavator model, showing spoil blocks tumbling off the rear chute into the pit.

### Changed
- **Side Conveyor Factory Integration**:
  - All non-throw-back products ("the rest of the products", including ores and minerals) are routed directly to connected normal conveyors on the left and right side conveyor ports (and front port).
  - Automatically detects connected conveyor belts and right-angle/sloped conveyors, checking clear belt intake space and feeding items onto the moving belts with forward momentum.
  - If no side conveyors are connected (or belts are temporarily congested), items safely accumulate in the machine's 9-slot internal buffer and auto-export the moment a conveyor is connected.

---

## [1.11.26] - 2026-11-06

### Changed
- **Heavy Pit Quarry 180° Direction Flip & Chute Alignment (`QuarryControllerBlockEntity`, `QuarryModel`, `QuarryControllerRenderer`)**:
  - **Excavation Area Inversion**: Flipped the work area $180^\circ$ so the heavy pit quarry excavates entirely **behind** the machine rather than in front. The operator console and controller face forward away from the pit towards player workstations.
  - **Conveyor Chute Repositioning**: Moved the middle conveyor discharge chute from the rear (which faced into the new pit) to the **front face** (`0, 0, -1`). Mined items ride forward along the front low-profile conveyor chute and to the left/right chutes, feeding seamlessly into conveyor networks away from the pit edge.
- **Dynamic Material-Specific Mining Speed**:
  - **Soft Materials (2x Speed)**: Mining **Dirt, Sand, Gravel, and Tuff** (including polished and brick variants) now executes at **2x speed** (10 ticks / 0.5s per block at 20 FE/t).
  - **Precious Hard Ores (1/2 Speed)**: Mining **Iron and Diamonds** (including deepslate, raw ore blocks, and block variants) now executes at **1/2 speed** (40 ticks / 2.0s per block at 20 FE/t).
  - **Standard Blocks (1x Base Speed)**: Stone, Deepslate, Copper, Gold, Redstone, Coal, etc., mine at normal base speed (20 ticks / 1.0s per block at 20 FE/t).
- **Top-Down Fallen Block & Obstruction Clearing**:
  - Implemented proactive top-down clearing for the entire excavated volume above the active drill head.
  - If gravel or sand collapses, water creates cobblestone, or blocks are placed in the previously excavated chamber, the quarry detects them and clears them from the **top down** before resuming downward progression.

---

## [1.11.24] - 2026-11-06

### Changed
- **Heavy Pit Quarry Industrial GUI Redesign & Geological Manifest Overhaul (`QuarryScreen`, `QuarryMenu`, `QuarryControllerBlockEntity`, `QuarryAssetGenerator`)**:
  - **Complete Visual Retheming**: Redesigned the Quarry GUI texture and layout (`256x196`) to match the exact aesthetic of all other refinery machines (dark industrial steel panel `#2B3036`, beveled metallic borders `#565F6C` / `#15181B`, corner bolts, recessed slot wells, and gold FE energy bar with live hover tooltip).
  - **Zero Text Overlapping & Rescaled Layout**: Cleanly separated all machine readouts, buttons, and slots into dedicated plates:
    - Left side: Machine title, operation phase status ("BENCH: 43x43x3", "SAFETY RAILING", "DEEP PIT (Y: -32)"), 50,000 FE energy gauge, Excavation Radius controls (`[-]` `21` `[+]` `[R]`), and dedicated Fence Supply slot.
    - Right side: Integrated CRT Geological Manifest Monitor with cyan header, total block counter, 8 paginated block slots, and arrow page controls (`< 1/4 >`).
    - Lower section: Clean 9-slot machine buffer row with standard inventory and hotbar centered below.
  - **Comprehensive Whole-Volume Pit Block Scanning**:
    - Updated `performInitialScan` to pre-load all chunk columns across the mining footprint so no blocks are skipped by unloaded chunks.
    - Removed arbitrary block limits so **all block types and their exact amounts** across the $43\times 43\times 3$ box and $41\times 41$ pit to bedrock are cataloged and browseable.
    - Added block entity position synchronization in `ContainerData` so the client GUI reliably receives the complete block list on open and updates counts in real time.
    - Interactive tooltips display localized block name, exact remaining count, and volume percentage. Mouse wheel scrolling is supported over the monitor.

---

## [1.11.23] - 2026-11-06

### Fixed
- **Heavy Pit Quarry 3D Monolithic Model Camera Anchor Fix (`QuarryControllerRenderer`, `QuarryModel`)**:
  - Fixed a critical rendering issue where the assembled 3D quarry excavator model was erroneously rendered using `poseStack.last()` instead of the deferred callback's captured `PoseStack.Pose`.
  - Because deferred `submitCustomGeometry` flushes later in the render loop, referencing `poseStack.last()` caused the entire 3×3×3 excavator model to bind to the player's camera/view matrix, causing the machine model to be stuck on the player's head and screen.
  - Updated `QuarryModel.render` and `QuarryControllerRenderer` to pass and consume the captured `PoseStack.Pose` directly, anchoring the 3D model properly in world coordinates at the multiblock controller position.

---

## [1.11.22] - 2026-11-06

### Changed
- **Heavy Pit Quarry Blueprint Hologram & Member Flexibility Overhaul (`QuarryControllerBlockEntity`, `QuarryControllerRenderer`, `BlueprintColors`)**:
  - Clarified and aligned all multiblock blueprint requirements, in-world floating nametag holograms, and missing-component chat messages to strictly match the 3 real craftable blocks: **Heavy Pit Quarry Controller**, **Quarry Structural Casing**, and **Quarry Lattice Frame**.
  - Removed confusing internal subcomponent label names (such as "Quarry Left Conveyor Port", "Quarry Turntable Base", "Quarry Operator Cabin", etc.) from requirement definitions so players are not misled into searching for non-existent blocks in JEI or the creative inventory.
  - Made multiblock verification flexible: all 26 member block positions now accept **either** `Quarry Structural Casing` or `Quarry Lattice Frame` interchangeably. Placing either block satisfies the structure position and clears that block from the active blueprint hologram.
  - Added dedicated sky-blue blueprint styling for `Quarry Lattice Frame` in `BlueprintColors`.

---

## [1.11.21] - 2026-11-06

### Added
- **Heavy Pit Quarry 3×3×3 Multiblock Machine (`QuarryControllerBlock`, `QuarryCasingBlock`, `QuarryFrameBlock`, `QuarryControllerBlockEntity`, `QuarryModel`, `QuarryControllerRenderer`, `QuarryScreen`, `QuarryMenu`)**:
  - Added the **Heavy Pit Quarry**: an authentic open-pit mining crawler rig engineered for automated geological strip mining, overburden clearing, and deep pit excavation.
  - **3-Phase Automated Excavation Sequence**:
    - **Phase 0 (Surface Overburden Clearing)**: Mines an expansive $43 \times 43 \times 3$ box directly in front of the machine ($y = 0, -1, -2$), excavating a wide 3-block-deep recessed terrace bench.
    - **Phase 1 (Automated Safety Perimeter Fencing)**: Once the $43 \times 43 \times 3$ clearing is completed, the quarry automatically lines the outside perimeter of the inner $41 \times 41$ pit with safety railings (using fences supplied in the dedicated GUI fence slot, or crafting durable safety fences automatically from quarried stone).
    - **Phase 2 (Deep Pit Excavation to Bedrock)**: Excavates the inner $41 \times 41$ pit layer-by-layer downward from $y = -3$ all the way to bedrock (`level.getMinY()`). Fluid pockets (water and lava) are safely cleared, ores and minerals are harvested with diamond-pickaxe loot context, and unbreakable bedrock is safely preserved.
  - **3 Low-Profile Conveyor Export Chutes**:
    - Features 3 extended industrial discharge arms—**Rear** `(0, 0, 3)`, **Left** `(-2, 0, 1)`, and **Right** `(2, 0, 1)`.
    - Conveyor beds are constructed at standard 5px height (`y: 0..5`), aligning seamlessly with our mod's `ConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `SlopedConveyorBeltBlock`.
    - Automatically distributes and ejects mined materials in a round-robin cycle directly onto adjacent conveyor belts, into adjacent item pipes, or into connected storage containers (Chests, Barrels, Hoppers), falling back to an internal 9-slot collection buffer if output lines are backed up.
  - **Energy-Proportional Mining Speed**:
    - **Max Speed**: 1 block per second (20 ticks per block mined) requiring strictly **20 FE/tick** (400 FE total per block).
    - **Progressive Power Throttling**: The quarry runs progressively slower as available RF/FE drops below 20 FE/tick (e.g. 10 FE/t runs at 50% speed, 5 FE/t at 25% speed), cleanly pausing with zero energy loss when no RF is applied.
  - **Integrated Industrial Lighting**:
    - Features dual high-intensity halogen work floodlights mounted on the upper lattice gantry aiming down into the excavation pit.
    - Emits world light level 15 when actively operating, brightly illuminating the entire mining site.
    - Model incorporates full-bright optic reflector lenses and an active rotating amber hazard warning strobe beacon.
  - **Interactive Industrial Quarry GUI**:
    - **Radius Adjustment**: Interactive `[-]` and `[+]` buttons allowing the clearing radius to be adjusted down from 21 ($43\times 43$) down to 3 ($7\times 7$), with live box and pit dimension readouts.
    - **Remaining Blocks Breakdown Dashboard**: Tracks and displays the total remaining block count and an interactive, paginated breakdown showing how many of each individual block type remains to be mined (with in-game block icons, formatted count tags, and localized hover tooltips).
    - **Dedicated Fence Supply Slot & 9-Slot Buffer**: Accepts custom player fences or iron bars for perimeter safety fencing, with 9 internal output buffer slots.
  - **Monolithic 3D Model & 256×256 Texture Atlas**:
    - Built according to the Monolithic 3D Architecture with zero texture bleeding (0.15px UV margin), zero z-fighting, and transformed surface normals.
    - Modeled after real-world open-pit surface mining crawler excavators: heavy steel crawler tracks, outrigger stabilizer plinths, central turntable deck, main electric winch drum with coiled steel wire cable, operator cabin with panoramic safety glass and dual control joysticks, rear counterweight ballast with 45-degree yellow/black hazard chevrons, and upper structural lattice gantry tower.

---

## [1.11.20] - 2026-11-06

### Fixed
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

## [1.11.19] - 2026-11-05

### Added
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

### Fixed
- **Bitumen Storage Pile Alignment & Drop Fixes (`BitumenPileRenderer`, `BitumenPileBlock`, `loot_table/blocks/bitumen_pile.json`)**:
  - Fixed 3D model rotation and origin offsets across all 4 horizontal facings (North, South, West, East) so the 3x3 footprint aligns with placed blocks.
  - Enforced single-item drop safety via Java `getDrops` override and `minecraft:block_state_property` loot table conditions, preventing duplicate item drops when broken.
- **Refinery Controller GUI Label Scaling (`RefineryScreen`)**:
  - Rescaled the amber "BITUMEN" monitor text label down by 20% (0.8x) using JOML `Matrix3x2fStack` transformations for GUI alignment.

---

## [1.11.18] - 2026-11-05

### Changed
- Mod update

---

## [1.11.18] - 2026-11-05

### Changed
- **Bitumen Storage Pile 3x3 Footprint Expansion (`BitumenPileBlock`, `BitumenPilePart`, `BitumenPileModel`, `BitumenPileRenderer`, `BitumenPileBlockEntity`)**:
  - Expanded the Bitumen Storage Pile footprint to an industrial **3x3 horizontal base** (18-part multiblock: 3x3x2) with extended retaining walls, corner pillars, and modular upright structural girders at 16px and 32px joints.
  - Generates continuous organic 24x24 heightfield mesh stages across the 48px x 48px ground area, mounding up smoothly to a towering peak under the 2-block height.
  - Expanded the hopper-style vacuum absorption bounding box to encompass the full 3x3x2.6 volume.
- **Refinery Controller GUI Monitor Label Rescaling (`RefineryScreen`)**:
  - Scaled down the amber "BITUMEN" monitor label by 20% (0.8x) using JOML `Matrix3x2fStack` (`pushMatrix`, `translate`, `scale`, `popMatrix`), nesting it cleanly inside the 35px byproduct panel monitor.

### Fixed
- **Bitumen Storage Pile Single-Item Drop Fix (`BitumenPileBlock`, `loot_table/blocks/bitumen_pile.json`)**:
  - Fixed an issue where destroying the Bitumen Pile in survival dropped multiple block items (one per sibling block).
  - Configured the loot table with `minecraft:block_state_property` constrained strictly to `part = bottom_front_left`.
  - Updated `playerWillDestroy` to remove all non-mined sibling blocks with silent flag 35 and route survival drops through a single destruction event, ensuring exactly 1 Bitumen Storage Pile item drops regardless of which block in the 3x3 is mined.

---

## [1.11.17] - 2026-11-05

### Added
- **Bitumen Storage Pile (`BitumenPileBlock`, `BitumenPilePart`, `BitumenPileBlockEntity`, `BitumenPileModel`, `BitumenPileRenderer`)**:
  - Implemented the 2x2x2 multiblock Bitumen Storage Pile with dynamic loose material heap physics and visual scaling:
    - **Dynamic Visual Growth**: Renders 9 continuous organic heightfield mesh stages (0 to 8) that dynamically mound up in real time from a floor dusting inside a reinforced steel retaining bunker to a towering 2-block-tall asphalt mountain as inventory fills (up to 3,456 items across 54 double-chest slots).
    - **Hopper-Style Vacuum Absorption**: Automatically sucks in bitumen items (`refinery:bitumen` and any tagged bitumen/asphalt items) dropped or falling onto or into the 2x2x2 pile bounds, accompanied by aggregate impact sounds and item particle splashes.
    - **Industrial Logistics Capabilities**: Exposes `WorldlyContainer` and NeoForge `ItemHandler` across all 8 multiblock blocks, allowing hoppers, pipes, conveyors, and direct player interactions to insert bitumen or open the double chest inventory interface.
    - **Photorealistic High-Resolution Textures**: Ultra-realistic 2048x2048 texture atlas with granular asphalt pebble aggregate, dark industrial riveted steel retaining plates, cast steel angle brackets, and compacted tar floor sheen, paired with a custom 32x32 inventory sprite.

---

## [1.11.16] - 2026-11-05

### Fixed
- **Multiblock Glass-Fused-to-Steel Storage Tank Flush Button (`StorageTankBlockEntity`, `StorageTankMenu`, `StorageTankScreen`)**:
  - Resolved an issue where clicking the "FLUSH" button in the multiblock storage tank GUI had no effect.
  - Linked the master `StorageTankBlockEntity` reference across both 4-argument and 5-argument `StorageTankMenu` constructors and `createMenu`, allowing server-side button click packets (`clickMenuButton(player, 0)`) to resolve the block entity.
  - Added `flushTank(@Nullable Player player)` on `StorageTankBlockEntity`: dumps all stored liquid, clears fluid locks, plays the emptying sound (`SoundEvents.BUCKET_EMPTY`), triggers a block update sync to all tracking clients, and sends a system notification displaying the exact volume of fluid flushed.
  - Updated `StorageTankScreen` hover, render, and click hitboxes to remain active whenever the tank holds liquid or has an active fluid type registered, and added client-side container data zeroing for immediate UI response.

---

## [1.11.15] - 2026-11-05

### Fixed
- **Blast Furnace GUI Bottom-Half Player Inventory Alignment (`BlastFurnaceMenu`, `BlastFurnaceScreen`)**:
  - Corrected player inventory slots in `BlastFurnaceMenu` from `y = 74` to `y = 84` and player hotbar from `y = 132` to `y = 142`, perfectly snapping the slot wells onto the dark gunmetal background texture.
  - Adjusted `inventoryLabelY` from `74` to `72` in `BlastFurnaceScreen`, resolving the text overlap where the "Inventory" header previously collided with the top row of items.
- **Metal Press RF Energy Meter Well Alignment (`MetalPressScreen`)**:
  - Corrected the horizontal rendering coordinate of the RF energy bar in `MetalPressScreen` from `xo + 18` to `xo + 14` (`width = 14`), perfectly nesting the yellow energy meter inside the recessed gauge well (`x: 13..28`).
  - Synchronized the energy meter tooltip hover detection bounds from `x: 18..32` to `x: 13..28`, eliminating the 4-pixel rightward shift and texture overflow.

---

## [1.11.13] - 2026-11-05

### Changed
- Mod update

---

## [1.11.13] - 2026-11-05

### Added
- **Conveyor Belt 90-Degree Corner & T-Intersection Adaptive Frame (`ConveyorBeltBlock`, `ConveyorBeltModel`, `ConveyorBeltRenderer`)**:
  - Automatically adapts standard horizontal conveyor belt side frames and rear end caps when conveyors meet perpendicularly at 90 degrees or form T-intersections:
    - **Side Frame Retraction**: When an incoming conveyor feeds into the side of a receiving conveyor (heading N/S into E/W or vice-versa), the side rail on that receiving side is automatically removed so items roll directly onto the belt without colliding with the frame.
    - **Rear Corner Guard Rail**: In 90-degree corner turns (where no conveyor feeds into the back), the removed frame piece is seamlessly relocated to the tail/back side of the receiving conveyor, creating a protective rear bumper that prevents turning items from slipping off the back.
    - **T-Intersection Support**: In T-junctions where a conveyor line continues from behind while a perpendicular conveyor merges into the side, the side rail is removed for the merge while the back remains completely open for uninterrupted passthrough.
    - **Zero Z-Fighting**: The back rail connects flush to the remaining side rail without overlapping faces, and mounting foot pads on the open incoming side are omitted for a completely flat junction.
    - **Backstop Item Retention (`ConveyorBeltBlockEntity`)**: Added physical backstop clamping at the rear rail to ensure turning items never glitch backwards during high-speed routing.

---

## [1.11.11] - 2026-11-05

### Fixed
- **Seamless Sloped-to-Flat Conveyor Transition & Collision Wall Elimination (`ConveyorBeltBlock`, `SlopedConveyorBeltBlockEntity`)**:
  - Resolved an issue where items reaching the top crest of a sloped conveyor belt would hit a vertical collision ledge, bounce into the air, or get permanently stuck at the transition to a horizontal flat conveyor.
  - Replaced restrictive directional voxel shapes (`ITEM_ENTRY_SHAPES`) in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` with `Shapes.empty()` for `ItemEntity`, eliminating the 5-pixel vertical block collision wall at conveyor seams while preserving full solid collision for players and mobs.
  - Implemented clean conveyor hand-off logic in `SlopedConveyorBeltBlockEntity`: once an item physically crosses the boundary into the next conveyor (`rawT >= 1.0`), ownership is relinquished immediately to prevent competing velocity and position conflicts.
  - Synchronized carrying surface heights across all conveyor types to `0.3125` (`5.0px`), leveled off vertical velocity by `rawT >= 0.82`, and added an exit momentum boost to ensure silky-smooth glide onto flat conveyors.

---

## [1.11.10] - 2026-11-05

### Added
- **Sloped Conveyor Belt Ground Support Legs & Foot Pads (`SlopedConveyorBeltModel` & `SlopedConveyorBeltRenderer`)**:
  - Dynamically extended support leg trestles downwards to reach the floor or solid ground when chaining sloped conveyors together in multi-block ramps or elevated inclines.
  - Automatically raycasts down up to 32 blocks beneath the elevated conveyor's support legs, detecting the exact top elevation of floor blocks, slabs, machine housings, or containers.
  - Renders continuous aluminum support leg columns from the inclined bed frame all the way down to the surface, complete with heavy bolted foot pads (`UV_FOOT_PAD`) resting on the floor.
  - Adds horizontal cross-bracing ties every 1-meter block interval down the entire height of the extended support legs.
  - Expanded `SlopedConveyorBeltRenderer.getRenderBoundingBox` downwards (`expandTowards(0.0, -32.0, 0.0)`) to ensure tall support legs are never frustum-culled.

---

## [1.11.9] - 2026-11-05

### Changed
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

## [1.11.8] - 2026-11-05

### Added
- **Refinery Tower Stage 1 Liquid Tar South Takeoff Pipe Assembly (`RefineryTowerModel`)**:
  - Added a third South-facing horizontal takeoff valve assembly at `Y = 22.0f` on the bottom section (Stage 1: Heavy Residue / Liquid Tar) of the 3×9×3 Petroleum Refinery Tower.
  - Symmetrically matches the rotated Stage 2 (`Y = 60.0f`, Diesel) and Stage 3 (`Y = 115.0f`, Gasoline) takeoff valve assemblies, projecting cleanly towards the South (+Z) face with a crimson indicator handwheel when Stage 1 is active (`UV_STAGE1_CRIMSON`), completing the 3-stage visual takeoff system.

### Fixed
- **Refinery Output Port Fluid Auto-Push to Connected Pipes & Tanks (`RefineryPortBlockEntity`)**:
  - Resolved an issue where distillation output liquids (Liquid Tar, Diesel, Gasoline) would not exit the Petroleum Refinery multiblock into connected fluid pipes or fluid storage containers.
  - Implemented active fluid pushing in `RefineryPortBlockEntity.serverTick`: every tick, fluid output ports (`TAR_OUT`, `DIESEL_OUT`, `GAS_OUT`, and `BYPRODUCT_OUT`) inspect all adjacent faces (`Direction.values()`) for connected NeoForge fluid handlers (`Capabilities.Fluid.BLOCK`, including `IndustrialPipeBlockEntity`, tanks, and fluid containers) and automatically push up to 1,000 mB/t of matching output fluid directly into the connected network using transaction-safe transfers.
  - Enhanced controller lookup with fallback via `ReinforcedCasingBlock.findNearbyController(...)` and automatic multiblock re-assembly synchronization (`syncAssembly()`), ensuring ports stay firmly connected to the tower even after block updates or chunk reloads.
- **Electric Arc Melter & Hydraulic Casting Press Texture Flickering & Z-Fighting Resolution (`ArcMelterModel` & `CastingPressModel`)**:
  - **Atlas UV Padding Inset**: Upgraded `UVRect.of` margin inset padding from `0.15px` to a robust `0.5px` (`(coord + 0.5f) / 256.0f`), completely eliminating texture bleeding, shimmering, and edge flickering across adjacent atlas tiles during camera movement and mipmapping.
  - **Ground Floor Z-Fighting Elimination**: Suppressed emitting downward-facing (`DOWN`) quads for foundation cuboids sitting on the ground (`y0 <= 0.01f`), preventing bottom runner faces from fighting with the top surface of floor blocks.
  - **Foundation Cross-Beam & Deck Plate Disjoint Planes**: Adjusted cross-beam heights in both `ArcMelterModel` (`y1 = 3.0f`) and `CastingPressModel` (`y1 = 3.5f`) so the heavy foundation deck plates sit directly on top of the beams without sharing identical coplanar top surfaces.
  - **Flange Ring & Platen Intersections**: Offset water-cooling girth flange rings in `ArcMelterModel` (`8.05f .. 9.95f` and `28.05f .. 29.95f`) and trimmed column locknuts in `CastingPressModel` (`39.5f .. 41.5f`) to prevent penetrating cuboids and edge collisions.

---

## [1.11.6] - 2026-11-05

### Added
- **Refinery Byproduct Port Automatic Conveyor & Pipe Ejection (`RefineryPortBlockEntity`)**:
  - Implemented automatic item ejection for the Byproduct Port (`refinery:byproduct_port`) on the 3-Stage Petroleum Refinery multiblock tower.
  - Automatically detects item conveyors (`refinery:conveyor_belt`, `sloped_conveyor_belt`, `right_angle_conveyor_belt`, and third-party conveyors via `isAnyConveyorBlock`) and item pipes (`IndustrialPipeBlockEntity`, hoppers, chests, barrels, crates, and NeoForge item capability handlers) attached **below** (`pos.below()`) or **beside** the Byproduct Port (`pos.relative(NORTH/SOUTH/EAST/WEST)` and 1-block below).
  - Every 4 ticks, the Byproduct Port scans for adjacent conveyance channels and smoothly ejects solid byproducts (Bitumen, Tar, and extracted sand) from the controller's internal byproduct slots (slots 2–5 and 1) directly onto the conveyor belt surface or into connected pipes/containers.
  - Integrated anti-clogging belt occupancy checks (`level.getEntitiesOfClass(ItemEntity.class, beltBox)`), preventing item stacking or clumping by waiting until previously ejected items advance along the belt.
  - Implemented `WorldlyContainer` directly on `RefineryPortBlockEntity` and updated item capability registration to allow multi-directional extraction through all horizontal and bottom faces of Byproduct Ports.

---

## [1.11.4] - 2026-11-05

### Changed
- **Refinery Tower Monolithic 3D Model Pipe Re-orientation & Vertical Header Removal**:
  - Removed the tall vertical grey collection header pipe that previously ran up the East (+X) side of the 3×9×3 Petroleum Refinery Tower.
  - Rotated the Stage 2 (Diesel, orange section at `Y = 60.0f`) and Stage 3 (Gasoline, green section at `Y = 115.0f`) horizontal takeoff valve assemblies by 90 degrees around the column shell. Both stubby takeoff pipes now project cleanly towards the **South (+Z)** face (`Z: 15.1f .. 23.5f`), directly aligning with the South-facing Diesel Output Port (`(0, 3, 2)`) and Gas Output Port (`(0, 6, 2)`).
  - Cleared the redundant lower East bracket assembly at Stage 1, leaving the East side of the tower completely unencumbered for furnace placement.

---

## [1.11.3] - 2026-11-05

### Fixed
- **Refinery Tower Stage Toggle Button Click & Interaction Resolution**:
  - Resolved an issue where clicking the Stage 1, Stage 2, or Stage 3 buttons (`S1`, `S2`, `S3`) in `RefineryScreen` failed to toggle stages.
  - Aligned stage button click listeners to execute immediate client prediction (`this.menu.clickMenuButton(...)`) and server packet synchronization (`handleInventoryButtonClick(...)`) with UI button click audio feedback.
  - Upgraded `RefineryControllerBlockEntity.stillValid(Player)` to `Container.stillValidBlockEntity(this, player, 16.0F)`, ensuring players interacting with member blocks across the large `3×9×3` multiblock tower do not trigger container reach invalidation when clicking stage toggles.

### Changed
- **Dark Gunmetal Industrial Aesthetic Overhaul for Refinery & Furnace GUIs**:
  - Completely restyled the GUI textures (`refinery.png`, `burner_refinery_furnace.png`, `electric_refinery_furnace.png`) using the dark gunmetal industrial steel palette (`#2B3036`), beveled frames, corner rivets, and recessed wells (`#1D2126` slots, `#0E1318` digital monitors) to match `MultiSmelterScreen`, `BlastFurnaceScreen`, and `IndustrialGrinderScreen`.
  - **Burner Refinery Furnace GUI Alignment (`BurnerRefineryFurnaceScreen` & `BurnerRefineryFurnaceMenu`)**:
    - Re-aligned the 3 fuel input slots horizontally to `(12, 20)`, `(30, 20)`, `(48, 20)` and the empty bucket return slot to `(48, 47)` with a downward fuel transfer arrow.
    - Added recessed wells for the 4× burn flame indicator `(11, 45, w: 34, h: 20)` and furnace thermometer `(67, 19, w: 12, h: 46)` with 350 °C operating threshold line.
    - Integrated a dark CRT/LCD Digital Telemetry Monitor box `(88, 19, w: 77, h: 33)` for temperature and flame status, plus a 3D beveled Redstone Mode toggle button `(88, 54, w: 77, h: 14)`, eliminating all overlapping text collisions.
  - **Electric Refinery Furnace GUI Alignment (`ElectricRefineryFurnaceScreen` & `ElectricRefineryFurnaceMenu`)**:
    - Added recessed wells for the RF Energy Buffer gauge `(12, 19, w: 18, h: 46)`, induction coil heating chamber `(34, 24, w: 28, h: 36)` with active coil glow, and thermometer `(66, 19, w: 12, h: 46)`.
    - Integrated the matching Digital Telemetry Monitor box `(88, 19, w: 77, h: 33)` and 3D beveled Redstone Mode button `(88, 54, w: 77, h: 14)`.
  - **3-Stage Petroleum Refinery GUI Overhaul (`RefineryScreen` & `RefineryMenu`)**:
    - Organized the interface into 3 dedicated riveted panel plates: Feed & Heat (left), Fractional Distillation & Product Tanks (center), and Bitumen Byproduct (right).
    - Positioned Crude Oil feed tank `(11, 19)`, Heat Input thermometer `(31, 19)`, oil sand cracking groove `(11, 53)`, and crude input/bucket return slots `(12, 59)` and `(30, 59)`.
    - Centered the 3 vertical stage toggle buttons `S3`, `S2`, `S1` at `(56, 19/37/55, w: 24, h: 16)` with 3D beveled styling, stage-specific active colors, and power LEDs.
    - Recessed sight-glass wells for Liquid Tar `(83, 19)`, Diesel `(98, 19)`, and Gasoline `(113, 19)` with meniscus highlights, distillation progress groove `(56, 73, w: 69, h: 5)`, and 2×2 Bitumen byproduct output slots `(132/150, 37/55)`.
    - Added a recessed LED status bezel in the top-right header `(161, 5, w: 8, h: 8)` with live green/red structure assembly status.

---

## [1.11.2] - 2026-11-05

### Added
- **3-Stage Petroleum Refinery Tower Rebuild (`3×9×3` Multiblock — Three `3×3×3` Stages)**:
  - Rebuilt the Petroleum Refinery into a towering **3×9×3** fractional distillation plant divided into three **3×3×3** vertical stages:
    - **Stage 1 (Bottom Third, `Y = 0..2`)**: Heavy residue / Liquid Tar & Bitumen stage (`350–500 °C`).
    - **Stage 2 (Middle Third, `Y = 3..5`)**: Diesel Oil fractionating stage (`180–350 °C`).
    - **Stage 3 (Top Third, `Y = 6..8`)**: Gasoline / Petrol & overhead condenser stage (`40–110 °C`).
  - Added individual **Stage 1 / Stage 2 / Stage 3 Enable/Disable Toggles** in the Refinery GUI (`RefineryScreen` & `RefineryMenu`) allowing players to dynamically customize distillation yields per **1,000 mB Crude Oil**:
    - **Stage 1 only**: `500 mB` Liquid Tar + `1` Bitumen (100%).
    - **Stage 2 only** (Stage 1 & 3 disabled): `500 mB` Diesel.
    - **Stage 3 only** (Stage 1 & 2 disabled): `500 mB` Gasoline.
    - **Stage 1 + Stage 2** (Stage 3 disabled): `250 mB` Liquid Tar + `250 mB` Diesel + `1` Bitumen (100%).
    - **Stage 1 + Stage 3** (Stage 2 disabled): `250 mB` Liquid Tar + `250 mB` Gasoline + `1` Bitumen (100%).
    - **Stage 2 + Stage 3** (Stage 1 disabled): `250 mB` Diesel + `250 mB` Gasoline.
    - **All 3 Stages (Stage 1 + 2 + 3 enabled)**: `250 mB` Liquid Tar + `250 mB` Diesel + `250 mB` Gasoline + **25% chance at `1` Bitumen**.
- **Two External Refinery Furnaces (Heat Sources — `1×2×1` Two-Block Vertical Units)**:
  - The 3-Stage Refinery Tower requires a heat source (`>= 350 °C`, up to `500 °C`) placed adjacent to Stage 1 (`Y = 0..2`). Players choose which furnace to build and place:
    - **Burner Refinery Furnace (`refinery:burner_refinery_furnace`)**: Burns solid fuels and fuel buckets at **4× fast burn speed** (`BURN_SPEED = 4`) across 3 fuel input slots with an empty bucket return slot.
    - **Electric Refinery Furnace (`refinery:electric_refinery_furnace`)**: High-power RF/FE heater consuming **500 FE/t** from a **500,000 FE** internal energy buffer.
  - Each furnace is placed from a **single item** as a 2-block-tall (`1×2×1`) structure (`FurnacePart.LOWER` & `FurnacePart.UPPER`) and strictly drops **only 1 item** when broken from either the top or bottom block.
- **Liquid Tar Fluid, Bucket & Liquid Tar Port**:
  - Registered **Liquid Tar** (`refinery:liquid_tar`, `refinery:flowing_liquid_tar`), **Liquid Tar Bucket** (`refinery:liquid_tar_bucket`), and **Liquid Tar Port** (`refinery:tar_port`), plus shapeless crafting of 1 Liquid Tar Bucket into 4 Petroleum Tar items and full Storage Tank support.
- **Zero-Flicker Monolithic 3D Models (`RefineryTowerModel` & `RefineryFurnaceModel`)**:
  - Created a 16-sided polygonal cylinder/frustum emitter (`emitCylinder` & `emitFlangeRing`) with strictly sequential, non-overlapping vertical segments and `0.5px` UV inset margins to completely eliminate coplanar Z-fighting and texture flickering on both the 3×9×3 Refinery Tower and 1×2×1 Refinery Furnaces.

---

## [1.10.25] - 2026-11-05

### Fixed
- **Texture Flickering & Z-Fighting Resolution (`ArcMelterModel` & `CastingPressModel`)**:
  - Re-architected both monolithic models to use the robust pre-computed `Cuboid` + `UVRect` system with $0.15\text{px}$ margin padding, eliminating edge-bleeding and polygon border shimmer.
  - Eliminated all coplanar surface overlap and penetrating box geometry on foundation skids, trunnions, and control consoles that previously caused severe Z-fighting and visual flickering.
  - Corrected face normal transformations by passing the active `PoseStack.Pose` into `consumer.setNormal(pose, nx, ny, nz)`, guaranteeing consistent diffuse shading and lighting as models rotate across cardinal directions.
- **Operator Control Console Texture Wrapping & Alignment**:
  - Corrected UV coordinates for `UV_CONSOLE_FRONT` and `UV_PLC_FRONT` to exactly match the $256\times 256$ atlas definitions at `(0, 96, 64, 48)`, properly rendering digital touchscreens, ammeter and hydraulic tonnage dials, pushbuttons, and yellow emergency stop collars.
  - Mapped operator consoles as single non-overlapping cuboids: the North face displays the instrument faceplate while the remaining 5 faces render seamless painted metal enclosure casing (`UV_CONSOLE_CASING` and `UV_PLC_CASING`), preventing texture stretching across thin edges.

### Added
- **Monolithic 3D Model & Procedural Texture Atlas Engineering Guide (`MONOLITHIC_3D_MODEL_GUIDE.md`)**:
  - Added a comprehensive architectural reference and blueprint guide for creating high-fidelity monolithic 3D models and procedural texture atlases for multiblock machines in Minecraft 26.3 NeoForge.
  - Includes full explanations of the 0.15px padding formula, zero-overlap rules, procedural Java2D painting techniques, multiblock member block concealment, and complete copy-paste ready Java code templates.

---

## [1.10.23] - 2026-11-05

### Added
- **Electric Arc Melter Monolithic 3D Model & 256×256 Texture Atlas (`ArcMelterModel` & `arc_melter.png`)**:
  - Redesigned the assembled 3×3×3 Electric Arc Melter (`refinery:arc_melter_controller`) into a monolithic 3D Industrial Electric Arc Furnace (EAF) modeled after real-world water-cooled steelmaking arc furnaces.
  - Features a heavy rocker trunnion foundation skid, cylindrical water-cooled ribbed steel shell with vertical buckstay stiffeners, interior magnesia-carbon refractory crucible hearth, front armored quartz slag/observation door at `(0, 1, 0)` with emissive arc glare, eccentric bottom-tapping (EBT) refractory launder spout at `(1, 0, 0)` (`Y: 4..9`) aligning seamlessly with Open Casting Channels, rear-left high-voltage step-down EAF transformer vault with oil-cooling radiator fins and 3 heavy copper busbar tubes, twin rear hydraulic gantry mast columns, domed water-cooled roof with refractory delta insert, top flared charge hopper at `(0, 2, 1)`, and front-left operator control console.
  - **Dynamic Operational Animations**:
    - 3 reciprocating graphite carbon electrodes arranged in a delta triad held by copper water-cooled clamp arms that oscillate vertically during active melting.
    - Incandescent glowing electrode tips and 3D jagged full-bright electric arc plasma bolts (`15728880` light) leaping from all 3 electrodes into the molten bath when active.
    - Dynamically rising and falling full-bright molten metal bath surface inside the crucible and flowing tapping stream along the launder spout, dynamically tinted to match the active molten fluid (`molten_iron`, `molten_gold`, `molten_copper`, `molten_tin`, `molten_bronze`, `molten_glass`).
    - Loaded input ore/dust/ingot rendered sinking into the top charge hopper (`(0, 2, 1)`), with physical item entity absorption for items dropped into the top funnel.
- **Continuous Casting Press Monolithic 3D Model & 256×256 Texture Atlas (`CastingPressModel` & `casting_press.png`)**:
  - Redesigned the assembled 3×3×3 Continuous Casting Press (`refinery:casting_press_controller`) into a monolithic 3D Heavy Hydraulic Continuous Casting & Quenching Press.
  - Features a reinforced steel foundation bed, central water-cooled copper & refractory crystallizer mold basin at `(0, 0, 1)`, 4 precision-ground chrome tie-rod columns with castle locknuts, overhead cast-steel crown platen topped by a high-tonnage crimson-amber main hydraulic cylinder and twin return boosters, front safety grille observation window at `(0, 1, 0)`, upper refractory tundish & sloped launder chute at `(0, 1, 2)`, lower `Y=0` (`Y: 3..9`) refractory channel receiver troughs at `(0, 0, 2)` and `(-1, 0, 1)` with animated glowing molten inflow streams, right-side water-cooling manifold cabinet, motorized steel run-out roller table extending through `(1, 0, 0)`, and front-left PLC operator control cabinet.
  - **Dynamic Operational Animations**:
    - Reciprocating hydraulic ram crosshead (with diagonal hazard stripes, bronze guide bushings sliding along the 4 chrome tie-rods, chrome piston rods, and water-cooled copper upper stamping die) that accelerates downward during ticks `0..90`, holds high-pressure quench compression during `90..150`, and retracts smoothly during `150..240`.
    - Dynamic molten pool to incandescent solidified billet transition inside the mold cavity and animated billet/item transport along the right-hand run-out roller table (`(1, 0, 0)`).

### Changed
- **Seamless Multiblock Assembly, Channel Flow & Pipe/Conveyor Connectivity**:
  - All 9 Arc Melter and Casting Press controller and member blocks (`arc_melter_controller`, `melter_casing`, `melter_chamber`, `melter_input_port`, `melter_output_port`, `casting_press_controller`, `casting_casing`, `casting_basin`, `casting_chamber`, `casting_input_port`, `casting_output_port`) now transition to `RenderShape.INVISIBLE` with `Shapes.empty()` occlusion when `ASSEMBLED = true`, forwarding right-click interactions to the controller GUI and disassembling cleanly if broken.
  - Registered Energy, Item (`WorldlyContainerWrapper`), and Fluid capabilities across all assembled member blocks of both machines so pipes, conveyors, hoppers, energy cables, and Open Casting Channels can connect to ports or any assembled casing/basin block (including level `Y=0` channel runs directly from the Arc Melter output port into the Casting Press).
  - Added automatic finished-item export (`tryExportOutput`) on `CastingPressControllerBlockEntity` to eject cast ingots and blocks onto adjacent conveyor belts, item pipes, and containers at `(1, 0, 0)`.

---

## [1.10.21] - 2026-11-04

### Fixed
- **Plate Stamping Die Crafting Progression (`refinery:plate_die`)**:
  - Changed `data/refinery/recipe/plate_die.json` to craft from 4 Steel Ingots (`#c:ingots/steel`) instead of Steel Plates (`#c:plates/steel`), resolving the circular progression dependency where making a Plate Die required plates that could only be stamped using a Plate Die.

---

## [1.10.19] - 2026-11-04

### Fixed
- **Steel Gear Recipe Tag (`refinery:steel_gear`)**:
  - Created `data/c/tags/item/ingots/steel.json` (`#c:ingots/steel`) and linked it in `data/c/tags/item/ingots.json`, fixing `Missing tag: 'c:ingots/steel' in 'minecraft:item'` during datapack registry loading.
- **Valve Pipe Upgrade Shapeless Recipe (`refinery:valve_pipe_upgrade`)**:
  - Updated `data/refinery/recipe/valve_pipe_upgrade.json` to use Minecraft 26.3 flat string ingredient syntax instead of legacy `{"item": ...}` objects, fixing `List is too short: 0, expected range [1-9]` on startup.

---

## [1.10.17] - 2026-11-04

### Added
- **Industrial Valve Pipe (`refinery:valve_pipe`)**:
  - Added the Redstone Valve Pipe capable of opening and closing transit for both fluids and items based on redstone signals and attached Pipe Gates.
  - Supports Normal Mode (valve closes when redstone signal is applied) and Inverted Mode (valve opens when redstone signal is applied).
  - Mode toggled seamlessly in-world by right-clicking the valve pipe with the Refinery Wrench, displaying action bar feedback and playing mechanical switch sounds.
  - Directly compatible with BuildCraft-style Pipe Gates (e.g. Tank Full, Space in Inventory, Redstone Signal) mounted on the pipe or adjacent blocks.
  - When closed, fluid pushing and item dispatch into the valve pipe are blocked, holding traveling items in place until the valve re-opens.
  - Added shaped crafting recipe (Steel Ingot + Redstone) yielding 8 valve pipes, plus a shapeless upgrade recipe converting steel pipes with redstone.
- **Classic Spool Pipe Wire Icons**:
  - Redesigned Red, Blue, Green, and Yellow Pipe Wire icons (`pipe_wire_red`, `pipe_wire_blue`, `pipe_wire_green`, `pipe_wire_yellow`) with warm golden-amber wooden spool flanges, wrapped colored wire coils, and an unspooling wire tail extending downward.

### Changed
- **Pipes Guide Book Entry**:
  - Updated the in-game Guide Book's industrial pipes entry with documentation for the Redstone Valve Pipe and its wrench interaction.

---

## [1.10.15] - 2026-11-04

### Added
- **Industrial Stamping Dies for Metal Press**:
  - Introduced 4 dedicated stamping dies: Plate Die (`refinery:plate_die`), Gear Die (`refinery:gear_die`), Corrugated Steel Die (`refinery:corrugated_die`), and Rail Die (`refinery:rail_die`).
  - Added shaped crafting recipes for all 4 dies using steel plates and ingots.
  - Registered `#refinery:press_dies` item tag grouping all valid press tooling.
- **Machinery Gears & Stamping**:
  - Registered 6 industrial gears: Steel Gear (`refinery:steel_gear`), Iron Gear (`refinery:iron_gear`), Copper Gear (`refinery:copper_gear`), Bronze Gear (`refinery:bronze_gear`), Gold Gear (`refinery:gold_gear`), and Tin Gear (`refinery:tin_gear`).
  - Added hand-crafting recipes (4 ingots in a cross) and common tags (`#c:gears` and `#c:gears/<metal>`).
  - In the Metal Press, inserting a Gear Die allows cold-forming 1 metal ingot or plate directly into 1 gear with 4x material efficiency.
- **Corrugated Steel Block & Rail Stamping**:
  - With the Corrugated Steel Die installed, rolling 1 Steel Ingot (or plate) yields 4 Corrugated Steel Panels (`refinery:corrugated_steel_panel`), and 1 Iron Ingot/Plate yields 2 panels.
  - With the Rail Die installed, 1 Steel Ingot/Plate stamps into 4 Rails (`minecraft:rail`), 1 Iron Ingot/Plate stamps into 2 Rails, 1 Gold Ingot/Plate stamps into 2 Powered Rails, and 1 Copper Ingot/Plate stamps into 2 Activator Rails.
- **In-Game Guide Book Documentation**:
  - Updated the Hydraulic Metal Press entry with detailed documentation on die requirements, tooling slot mechanics, and all 4 stamping recipe types.

### Changed
- **Hydraulic Metal Press Die Requirement & GUI Upgrade**:
  - Expanded the Metal Press inventory to 5 slots: Slot 0 dedicated to Tooling Dies (max stack 1), Slots 1..2 for Inputs, and Slots 3..4 for Outputs.
  - The press strictly requires an installed Die to operate; stamping cycles now validate the die tooling before consuming energy or cycling the hydraulic ram.
  - Upgraded the GUI layout and texture (`metal_press.png`) with a beveled Die slot frame beneath the ram, an empty-slot tooltip guide, and ram hover tooltips showing the installed die and stamping status.
  - Shift-click smart routing directs dies to the die slot, pressable materials to inputs, and finished goods to the player inventory.
  - Automation (hoppers, conveyors, pipes) extracts exclusively from output slots (3..4) and inserts into material inputs (1..2), protecting installed dies from extraction.

---

## [1.10.14] - 2026-11-04

### Added
- **Dedicated Pipe Facades Creative Tab (`itemGroup.refinery.facades`)**:
  - Moved all dynamic Pipe Facades (both Solid Panels and Pass-Through Frames) out of the main Industrial Refinery creative tab into their own dedicated creative tab (`refinery_facades_tab`), preventing clutter in the main mod item tab.
- **Dynamic Real-Block Texture Rendering for Pipe Facade Item Models (`PipeFacadeSpecialRenderer`)**:
  - Registered a Minecraft 26.3 `SpecialModelRenderer` for `pipe_facade` and `pipe_facade_hole` items via `RegisterSpecialModelRendererEvent`.
  - Automatically samples the target block's native `TextureAtlasSprite` from `TextureAtlas.LOCATION_BLOCKS` at runtime, rendering every facade item in creative menus, player inventories, and hand models with its true block texture (Stone, Oak, Corrugated Steel, Bronze, etc.) and 3D plate thickness rather than a generic flat gray sprite.

### Changed
- **Slender Horizontal Rectangular 3D 8-Bit Pipe Icons**:
  - Re-proportioned all 23 pipe item icons to slender horizontal rectangles (16px collar height, 12px shaft height, 30px width; 2.5:1 aspect ratio), matching the slender profile of classic Mario pipes.
  - Retains all crisp 8-bit details: flanged collars, top specular highlight stripes, dark notch grooves, deep body mid-tones, 1x1 checkerboard dithering, bottom bounce highlights, and centered 5x7 fluid droplet indicators for liquid pipes.

---

## [1.10.12] - 2026-11-04

### Fixed
- **Pipe Facade CustomRecipe Network Packet Encoding (`neoforge:recipe_content`)**:
  - Fixed a disconnect crash (`EncoderException: Failed encoding custom payload neoforge:recipe_content: IllegalStateException: Can't encode 'com.refinery.recipe.PipeFacadeRecipe@...', expected '...'`) when joining a world by registering `PipeFacadeRecipe.INSTANCE` and `PipeFacadeSwapRecipe.INSTANCE` singletons with `equals` and `hashCode` implementations for `MapCodec.unit(...)` and `StreamCodec.unit(...)`.
- **Tin Ore & Deepslate Tin Ore Loot Table Validation (`Unreachable entry!`)**:
  - Updated `data/refinery/loot_table/blocks/tin_ore.json` and `deepslate_tin_ore.json` to use the Minecraft 26.3 `"condition": "minecraft:tool/can_silk_touch"` syntax instead of the non-existent `#minecraft:enchantments/silk_touch` tag, resolving `ReloadableServerRegistries` loot table validation warnings and restoring Silk Touch drops.

---

## [1.10.11] - 2026-11-04

### Changed
- **Authentic 3D 8-Bit Pixel Art Pipe Item Icons**:
  - Replaced legacy blurred vector/anti-aliased pipe icons with crisp 32x32 3D 8-bit retro arcade pixel art pipe icons inspired by classic Mario pipes.
  - Features flanged collar rims on both ends with vertical rim highlights, specular top highlight stripes, grooved notch separators, deep cylindrical body shading, 1x1 checkerboard dither shading at the base, and bottom bounce specular reflection.
  - Tailored distinct material palettes across all 23 pipe types: Wood, Copper, Iron, Steel, Gold, Diamond, Emerald (reference green pipe), Brass, Cast Iron, Void, Quartz/Merge, Buffer, and Structure.
  - Added a distinct 8-bit fluid droplet emblem marking in the center of all liquid transport pipes (`wood_fluid_pipe`, `copper_fluid_pipe`, `iron_fluid_pipe`, `steel_fluid_pipe`, `gold_fluid_pipe`, `diamond_fluid_pipe`, `cast_iron_fluid_pipe`, `fluid_extractor_pipe`, and `merge_fluid_pipe`) for immediate visual distinction between item and liquid conduits in inventory screens.
  - Added dedicated status badges for utility pipes: golden extraction chevrons for extractor pipes, storage latches for buffer pipes, and swirling singularity vortexes for void pipes.

---

## [1.10.10] - 2026-11-04

### Added
- **BuildCraft-Style Pipe Facades (`refinery:pipe_facade` & `refinery:pipe_facade_hole`)**:
  - Added **Solid Pipe Facades** (`refinery:pipe_facade`) and **Pass-Through (Hollow) Pipe Facades** (`refinery:pipe_facade_hole`) that conceal pipes behind 2/16-inch (`0.125` block) structural wall panels on any of the 6 faces (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`) of any Industrial Pipe or Buffer Pipe.
  - **Live Registry Scanning & Dynamic Texture Wrapping (`PipeFacadeHelper` & `PipeFacadeRenderer`)**:
    - Automatically scans `BuiltInRegistries.BLOCK` at runtime to support any full-cube, opaque block from Vanilla, Industrial Refinery (including all 17 Corrugated Steel Panels, metal/ore storage blocks, Oil Shale, and industrial casings), and third-party mods without pre-generating thousands of static asset files.
    - Automatically filters out problematic non-solid or special blocks (glass, ice, leaves, grass blocks, slime/honey, TNT, redstone components, spawners, portals, liquids, falling blocks, and block entities).
    - Wraps the target block's native `TextureAtlasSprite` directly onto the 3D facade geometry on `TextureAtlas.LOCATION_BLOCKS`, automatically preserving **LabPBR 1.3 specular reflections (`_s`)** and **Parallax Occlusion Mapping (`_n`)** in shaders like Complementary Unbound.
  - **Crafting & Conversion Recipes (`PipeFacadeRecipe` & `PipeFacadeSwapRecipe`)**:
    - **Solid Pipe Facade (x8)**: 1 eligible full block in the center surrounded by 8 Copper Item Pipes (`refinery:copper_item_pipe`).
    - **Solid <-> Pass-Through Conversion**: Place any Solid Pipe Facade alone in a crafting grid to convert it 1-to-1 into a Pass-Through Facade with a centered pipe opening (`4.8/16` to `11.2/16`), or convert a Pass-Through Facade back into a Solid Facade.
  - **In-World Attachment & Removal**:
    - Right-click any pipe with a Pipe Facade to attach it to the targeted face (or opposite face when clicking the center core).
    - Shift + Right-Click a facaded pipe face with a **Refinery Wrench** or empty hand to pop that side's facade off, or break the pipe to drop all attached facades.
- **Structure Pipe (`refinery:structure_pipe`)**:
  - Added a low-cost structural pipe (`2 Cobblestone + 1 Gravel -> 8 Structure Pipes`) with a solid cobblestone-gray finish and no glass window cutouts.
  - Does not transfer items or fluids (`handlesItems = false`, `handlesFluid = false`), preventing unwanted item or liquid routing while connecting seamlessly to all pipe networks to carry **Pipe Wires** (`Red`, `Blue`, `Green`, `Yellow`), mount **Pipe Logic Gates**, and hold **Pipe Facades**.

---

## [1.10.9] - 2026-11-04

### Fixed
- **Cross-Mod Conveyor Belt Transition & Item Sticking (Ole Saw Mill Compatibility)**:
  - Fixed item collision box height in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` from 4.0 px (`0.25`) to 5.0 px (`0.3125`), matching the standard 5-pixel surface height used by adjacent conveyor mods such as Ole Saw Mill (`olesawmill:sawmill_conveyor`). This eliminates the 1-pixel collision lip that previously stopped items from crossing the block seam.
  - Implemented boundary handoff release in `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity`: once an item physically crosses the forward boundary into a connected conveyor belt block, our conveyor immediately yields ownership, eliminating conflicting dual-conveyor velocity overrides and seam jitter.
  - Added forward velocity boost (`1.3x`) when items approach the exit threshold of an attached conveyor belt, guaranteeing uninterrupted gliding across block boundaries.

---

## [1.10.7] - 2026-11-04

### Changed
- Mod update

---

## [1.10.7] - 2026-11-04

### Added
- **LabPBR 1.3 & POM (Parallax Occlusion Mapping) Support for Corrugated Steel Panels**:
  - Generated full LabPBR 1.3 specification Normal Maps (`_n.png`) and Specular Maps (`_s.png`) for all 17 Corrugated Steel Panel variants (raw steel and 16 dyed colors), across both siding faces and cross-section cut caps (102 textures total).
  - **Parallax Occlusion Mapping (POM) Depth**: Encoded continuous physical height profiles into the Alpha channel of `_n.png` (crests at height 245, troughs at 36, stiffening micro-ribs at 90), rendering true geometric 3D relief, sloped folds, and raymarched self-shadows in shaders like Complementary Unbound.
  - **DirectX Normal Vectors & Ambient Occlusion**: Analytically derived tangent-space normal vectors with DirectX Y-down orientation and linear contact ambient occlusion baked into the Blue channel (`B = 140-250`) for deep contact shadows in panel crevices.
  - **Metallic Specular Reflectance (LabPBR 1.3)**:
    - Raw Corrugated Steel Panel configured with Hardcoded Iron/Steel ID (`G = 230`) for physically accurate metallic Fresnel reflectance.
    - 16 Dyed Corrugated Steel Panels configured with Custom Metal mode (`G = 255`) for glossy, colored metallic specular highlights matching the panel's powder-coat color.
    - Smoothness calibrated to a crisp satin industrial metal finish (`R = 186-214`, roughness 0.16–0.27).
    - Porosity set to non-porous metal (`B = 0`), creating realistic rainwater slick reflections during rainfall.
- **Enhanced Galvanized & Powder-Coat Diffuse Textures**:
  - Implemented Voronoi crystallization zinc spangle lattices on the raw Corrugated Steel Panel diffuse map.
  - Refined specular crest highlights, shadow contrast in trough valleys, and cold-rolling mill micro-streaks across all variants.

---

## [1.10.5] - 2026-11-04

### Changed
- Mod update

---

## [1.10.5] - 2026-11-04

### Changed
- **Steam Bucket Creative-Only Restriction**:
  - Steam can no longer be scooped up into an open bucket in Survival mode, matching realistic thermodynamics. Attempting to scoop steam with an empty bucket triggers a vapor dissipation sound (`SoundEvents.FIRE_EXTINGUISH`) without filling the bucket.
  - Creative mode players retain access to the Steam Bucket (`refinery:steam_bucket`) for testing, construction, and fluid placement.
  - Added descriptive tooltip: *"Creative Only: Steam cannot be contained in an open bucket in Survival."*

### Fixed
- **Item Pipe Traveling Item Traversal & Continuous Visibility**:
  - Replaced legacy `ItemStackWithSlot` serialization with dedicated NBT `childrenList` storage for `"TravelingItems"`, fixing packet encoding failures where direction and progress bitmasks exceeded the Minecraft `UNSIGNED_BYTE` 255-slot limitation.
  - Added immediate block entity update broadcasts (`sendBlockUpdated` with flag 3) when an item exits a pipe and enters an adjacent pipe block entity, ensuring uninterrupted visual traversal through every pipe piece across the entire transport network.
  - Increased in-transit item synchronization frequency from every 4 ticks to every 2 ticks for responsive client-side progress tracking and sub-tick partial tick animation.
  - Applied minimum ambient lighting (`0x00D000D0`) to traveling items inside transparent pipe casings, ensuring items remain distinctly visible even when passing through dark, enclosed, or unlit factory environments.

---

## [1.10.3] - 2026-11-04

### Fixed
- **Recipe Syntax Migration (Minecraft 26.3 / 1.21.4+)**:
  - Migrated Corrugated Steel Panel shaped, shapeless re-dyeing, water-bucket washing, and stonecutter recipes to Minecraft 26.3 string-based ingredient format (`"#c:corrugated_steel_panels"`, `"refinery:corrugated_steel_panel"`, etc.), fixing `IllegalStateException` during recipe registry deserialization.
- **Blockstate & Model Resolution**:
  - Added missing `assets/refinery/models/item/oil_shale.json` item model, resolving `Missing block model: refinery:item/oil_shale`.
  - Added missing `assets/refinery/blockstates/buffer_pipe.json` and block model, resolving `Missing model for variant: 'Block{refinery:buffer_pipe}'`.
  - Removed invalid `facing=up` and `facing=down` variants from `casting_input_port`, `casting_output_port`, `melter_input_port`, and `melter_output_port` blockstates (which are `HorizontalDirectionalBlock` and only have horizontal facings), resolving blockstate dispatcher loader warnings.

---

## [1.10.2] - 2026-11-04

### Added
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
  - Full item definitions, blockstates, loot tables, and `#c:corrugated_steel_panels` tag support.

---

## [1.10.1] - 2026-11-04

### Added
- **Bituminous Tar Sands & Crude Oil Texture Sync**:
  - Synchronized and updated Crude Oil still and flowing fluid textures (`crude_oil_still.png`, `crude_oil_flow.png`) with high-resolution animated textures and `.mcmeta` frame rate definitions.
  - Replaced standard Oil Sand with authentic bituminous tar sand texture.
  - Added **Oil Red Sand** (`refinery:oil_red_sand`) with badlands red sand bituminous texture and custom reddish dust particles (`0x8C381E`).
  - Added **Oil Shale** (`refinery:oil_shale`) stone-tier bituminous ore block mineable with pickaxe.
  - Added full loot tables, item definitions, models, and tag registrations (`c:tags/block/oil_sands`, `c:tags/item/oil_sands`, shovel/pickaxe mineable tags) for both new oil blocks.
  - Integrated `oil_red_sand` and `oil_shale` world generation into `OilPitGenerator`: placing Oil Red Sand in badlands biomes and surface red sand layers, and Oil Shale in subterranean deepslate/cavern layers.
  - Supported all oil sand and shale variants in the Industrial Grinder and Refinery Distillation Column.
- **Heavy-Duty 3D Industrial Pipe End Caps**:
  - Modeled 3D industrial pipe caps on all unconnected pipe termination ends and standalone pipes (`IndustrialPipeModel`).
  - Features an outer sleeve collar wrapping the pipe end, a circular front cap plate, and a raised central hex nut / bolt head boss for a heavy industrial appearance.
- **Pipe Item Ejection on Dead Ends & Full Containers**:
  - When traveling items in an item pipe reach a dead end (no connecting pipe or inventory) or an attached container whose inventory is full or rejects the item, the items are cleanly ejected out onto the ground with realistic velocity and sound (`IndustrialPipeBlockEntity`).
  - Partial insertion leftovers are similarly ejected rather than looping or freezing inside the pipe network.

---

## [1.9.9] - 2026-11-04

### Added
- **Multi-Smelter Auto-Export to Conveyors & Containers**:
  - Implemented auto-export from Output Slots 8..15 for the **Industrial Multi-Smelter** (`MultiSmelterControllerBlockEntity`).
  - Smelted products (such as charcoal from wood/logs, ingots, glass) are automatically ejected onto any conveyor belt detected in front of the machine (in front of the Controller or front face), adjacent to any Smelter Output Port, or around the perimeter.
  - Automatically verifies conveyor direction so items are never ejected into backwards-facing belts leading back into the machine.
  - Implemented item spacing checks on the conveyor belt surface with single-item ejection, ensuring items ride down the conveyor neatly separated.
  - Added support for direct auto-export into adjacent item transport pipes, containers (chests, barrels, hoppers), and external item capabilities.
- **Metal Press & Blast Furnace Conveyor Auto-Export Parity**:
  - Added auto-export support to the **Industrial Metal Press** (`MetalPressControllerBlockEntity`), automatically ejecting finished stamped items (plates, rods, gears) onto conveyors or containers placed at the discharge bed or in front of the machine.
  - Upgraded **Blast Furnace** (`BlastFurnaceControllerBlockEntity`) auto-export with comprehensive conveyor detection, proper forward orientation handling, and single-item spacing.

---

## [1.9.8] - 2026-11-04

### Added
- **Item Pipe Traveling Item Visibility & Rendering**:
  - Implemented 360-degree clear cylindrical glass pipe bodies with heavy industrial structural collar rings for all item transport pipes (Wood, Stone, Iron, Gold, Diamond, Netherite, Emerald, Quartz, Obsidian, Void, and Buffer).
  - Traveling items are now fully visible inside pipes as they move between inventories, matching classic logistics aesthetics.
  - Implemented client-side traveling item movement and smooth partial-tick interpolation so items animate smoothly without jitter or popping.
  - Added full NBT serialization and deserialization for traveling items in `IndustrialPipeBlockEntity` via `ItemStackWithSlot.CODEC`, ensuring in-transit items persist across chunk unloads and world reloads.
  - Added periodic server network synchronization (`sendBlockUpdated`) every 4 ticks while items are in transit.

### Fixed
- **Multi-Smelter GUI Alignment & Dark Gunmetal Industrial Styling**:
  - Completely restyled the Industrial Multi-Smelter GUI (`textures/gui/multi_smelter.png`) using the dark gunmetal industrial steel palette (`#2B3036`), beveled borders, corner rivets, and recessed slot wells to match the Industrial Grinder and Metal Press.
  - Re-aligned all 8 input slots (4x2 grid at X=27, 45, 63, 81; Y=19, 39) and 8 output slots (4x2 grid at X=101, 119, 137, 155; Y=19, 39) to perfectly center in their respective texture wells.
  - Centered the solid fuel slot at (X=89, Y=62) between the dual-power indicators.
  - Fixed title and inventory label positioning to prevent visual overlap with frame borders or slots (`titleLabelX=8, titleLabelY=6`, `inventoryLabelX=8, inventoryLabelY=74`).
  - Removed clashing "IN" and "OUT" text overlays.
  - Integrated energy gauge rendering with hover tooltips (RF stored / max RF) at (10, 18).
  - Added dedicated combustion flame indicator at (70, 63) and induction heating indicator at (110, 63) with descriptive hover tooltips.
  - Moved parallel cook progress bars to dedicated 2px grooves under each input slot (`Y=36` and `Y=56`), eliminating all slot overlapping.
  - Added informative hover tooltips for the Smelting Hearth and Smelted Product Storage header plates.

---

## [1.9.7] - 2026-11-03

### Fixed
- **Guide Book Clicks**: Fixed the Industrial Field Manual ignoring all mouse clicks (category sidebar, Table of Contents entries, Back button, sub-tabs, layer pills). Left-click is button 1 in Minecraft 26.3, but the screen was checking for 0.

---

## [1.9.6] - 2026-11-03

### Added
- **Comprehensive Informative Pipe Tooltips**:
  - Implemented `PipeBlockItem` across all 22 item and fluid pipe blocks.
  - Hovering over any pipe item now displays:
    - **Function / Type**: Clear classification (Filtered Extraction, Basic Transport, Directional One-Way Valve, Priority Insertion, Directional Sorting, Round-Robin Balancer, Merge Funnel, Inline Buffer, or Disposal Void).
    - **Transfer Rates & Speeds**: Exact numerical transfer rates (mB/t for fluid pipes) and velocity multipliers (blocks/t for item pipes), along with internal buffer capacities.
    - **Operational Guidance**: Practical tips on right-click filter GUI access, wrench rotation/output configuration, and pulse gate requirements.

### Fixed
- **Conveyor Inter-Mod Item Handoff Compatibility**:
  - Fixed an issue where items transferring from Industrial Refinery conveyors to external mod conveyors (such as Ole Saw Mill's conveyor) would bunch up and get stuck at the junction in an unpickupable state due to capability insertion loops.
  - Recognized all external conveyor blocks via `ConveyorBeltBlock.isAnyConveyorBlock` to ensure smooth physical item handoff across block boundaries without capability interference.

---

## [1.9.5] - 2026-11-03

### Added
- **In-Game Industrial Field Manual / Guide Book (`refinery:guide_book`)**:
  - Added an in-game guidebook craftable with `minecraft:book` + `minecraft:iron_ingot` (or wrench).
  - Right-clicking the book opens the interactive Industrial Field Manual GUI (`RefineryGuideScreen`).
  - **Interactive Table of Contents (TOC)**:
    - Sidebar categories (`All Topics`, `Multiblock Plants`, `Foundry & Casting`, `Processing Machines`, `Steam & Power`, `Pipes & Gates`, `Logistics & Storage`).
    - Scrollable topic cards with 3D item icons, titles, and category tags.
  - **Multiblock Bill of Materials & Layer Layout**:
    - Clicking any multiblock in the TOC (e.g. `Industrial Blast Furnace`) immediately opens its dedicated section.
    - Displays a **Bill of Materials** with exact block counts (e.g., Blast Furnace: 1 Controller, 1 Input Port, 1 Output Port, 9 Foundry Heat Glass, 86 Foundry Casings — 98 blocks total) and item hover tooltips.
    - Features an interactive **Layer Layout Viewer** with layer selection pills (`L0`, `L1`, `L2`, `L3`, `L4`), a 2D top-down grid diagram with color-coded block cells, coordinate hover tooltips, orientation markers, and block legend.
  - **Recipe & Operation Codex**:
    - Detailed overview of machine mechanics, operating parameters, and visual input/output recipes.
    - Pro-tips for automation, conveyor alignment, and 3D hologram blueprint projection using the Refinery Wrench.

---

## [1.9.4] - 2026-11-03

### Added
- **Wood Extraction Pipe Filter GUI**:
  - Right-clicking a Wood Item Pipe now opens a 9-slot filter interface (`WoodPipeMenu` and `WoodPipeScreen`) with standard dispenser-style 3x3 layout.
  - Players can insert sample items into the filter slots to specify which items the pipe is allowed to extract from connected inventories or machines.
  - An empty filter allows extraction of any item.
  - Filter inventory contents are saved with world chunk data and dropped upon pipe removal.

### Changed
- **Autarchic Pulse Gate Gated Extraction for Wood Item Pipes**:
  - Wood Item Extraction Pipes no longer passively extract items on tick; item extraction is now strictly triggered when an active Autarchic Pulse Gate on the pipe fires (`PULSAR_PULL_ITEM`, `PULSAR_PULL_ITEM_5`, or `PULSAR_PULL_ITEM_10`).
  - Autarchic pulsar pulls (`PipeGateEntity` and `PipeGateBlockEntity`) strictly check the Wood Pipe's filter configuration before pulling items from connected inventories or item handlers.
  - Items injected by pulse gates automatically route along connected pipe network paths via `chooseNextItemDirection`.

---

## [1.9.3] - 2026-11-03

### Fixed
- **Cross-Mod Conveyor Belt Handoff & Trapped Item Resolution**:
  - Implemented universal cross-mod conveyor detection (`ConveyorBeltBlock.isAnyConveyorBlock`), preventing conveyor belts from mistakenly identifying external conveyor blocks (such as Ole Saw Mill's conveyors) as static inventory endpoints.
  - Eliminated the infinite capability insertion/respawn cycle at conveyor transfer seams that caused items to bunch up, get trapped in an unpickable state with reset pickup delays, and stall at the handoff boundary.
  - Adjusted conveyor item surface height to $pos.getY() + 0.32$ (above 5px / 0.3125 collision height), ensuring items glide seamlessly across block transitions without catching on the leading edges of adjacent belts.
  - Connected front and back conveyor models to external matching conveyors seamlessly.

---

## [1.9.2] - 2026-11-03

### Added
- **Autarchic Pulse (Item x5) and (Item x10) gate actions**: pull 5 or 10 items at a time into the pipe instead of 1.

---
## [1.9.0] - 2026-11-03

### Changed
- **Multiblock Hologram Part Label Size Reduced by 60%**:
  - Implemented centralized blueprint label rendering via `BlueprintColors.renderBlueprintLabel` across all 13 multiblock machine renderers (`ArcMelter`, `BlastFurnace`, `CastingPress`, `IndustrialGrinder`, `LargeBoiler`, `LargeDieselBoiler`, `MetalPress`, `MultiSmelter`, `Refinery`, `SmallBoiler`, `SmallDieselBoiler`, `SteamTurbine`, and `StorageTank`).
  - Scaled floating 3D billboard text down to 40% of its previous size (`scale = 0.40f`, a 60% reduction), significantly improving legibility and preventing text clutter.
  - Positioned labels directly above each block's top center with zero-offset anchoring (`(0.5, 1.0, 0.5)` with `(0, -0.5, 0)` nametag baseline), preventing nametags from overlapping and obscuring neighboring blocks in dense multiblock structures.

---
## [1.8.63] - 2026-11-02

### Changed
- **Petroleum Refinery Diesel Output Port Relocation**:
  - Relocated the Diesel Output Port from Layer 1 (`(0, 1, 2)`) to Layer 0 (`(0, 0, 2)`), placing it on the bottom ground tier of the multiblock structure.
  - Placed a Reinforced Casing at Layer 1 (`(0, 1, 2)`), separating the Diesel Port from the Gas Output Port at Layer 2 (`(0, 2, 2)`) so liquid output connections do not crowd together on the back face.
  - Updated holographic blueprint projection guidelines to reflect the new port layout.

---
## [1.8.62] - 2026-11-02

### Changed
- **Arc Melter Processing Speed Slowed Another 100%**:
  - Doubled `PROCESS_TIME` in `ArcMelterControllerBlockEntity` from 60 ticks (3.0 s) to 120 ticks (6.0 s) per batch.
  - Reduced automatic molten fluid ejection rate from 80 mB/t to 25 mB every 4 ticks, simulating a steady, realistic stream pouring into connected casting channels.
- **Continuous Casting Press Processing Speed Slowed by 200%**:
  - Tripled `PROCESS_TIME` in `CastingPressControllerBlockEntity` from 80 ticks (4.0 s) to 240 ticks (12.0 s) per press stroke (+200% process duration).
  - Synchronized hydraulic stamping die ram stroke in `CastingPressControllerRenderer` to span 120 ticks down and 120 ticks up, with quench sounds, anvil impact, steam clouds, and metallic ingot solidification occurring at tick 120.
- **Casting Channel Lava-Like Viscous Flow Physics**:
  - Implemented viscous flow propagation in `CastingChannelBlockEntity`: when molten metal enters an empty channel, it now accumulates for 25 ticks (1.25 s) before cascading downstream, mimicking thick molten lava.
  - Slowed downstream fluid transfer to 25 mB in rhythmic 4-tick intervals (6.25 mB/t).
  - While upstream actively feeds a channel line, each channel retains a minimum pool of 150 mB so the entire channel network remains visibly filled and glowing with thick molten metal during pouring, draining completely once upstream supply ceases.
  - Decreased molten fluid surface scroll velocity in `CastingChannelRenderer` from 0.012 to 0.0018 (nearly 7x slower), rendering thick, slow-creeping fluid dynamics.

### Fixed
- **Industrial Grinder Conveyor Auto-Export & Internal Buffering**:
  - Fixed conveyor auto-exporting in `IndustrialGrinderControllerBlockEntity`: when any conveyor belt (flat, sloped, or right-angle) is attached to the grinder (at an output port, adjacent to the right-hand discharge chute, or along the perimeter), finished crushed items are automatically fed onto the belt one at a time.
  - Eliminated the restrictive downstream-receiver check that previously rejected attached conveyors if they did not immediately terminate into a container on the next block.
  - Added support for all conveyor types (`ConveyorBeltBlockEntity`, `RightAngleConveyorBeltBlockEntity`, and `SlopedConveyorBeltBlockEntity`) via standard `Container` item placement.
  - When no conveyor, pipe, or container is attached, finished items remain safely stored inside the grinder's 4-slot internal output buffer.

---
## [1.8.61] - 2026-11-02

### Fixed
- **Sloped-to-Flat Conveyor Crest Transition & Item Sticking Resolution**:
  - Re-profiled the sloped conveyor incline curve so it reaches the full crest height of the level conveyor ($pos.getY() + 1.3125$) by $t = 0.85$, allowing items to glide horizontally across the top terminal rather than slamming into the vertical face of the flat conveyor.
  - Extended active item position and height alignment across the hand-off seam ($0.0 \le rawT \le 1.15$), maintaining smooth continuity while transitioning into the next block.
  - Implemented directional recessed entry collision shapes (`ITEM_ENTRY_SHAPES`) on flat and right-angle conveyors (`ConveyorBeltBlock` & `RightAngleConveyorBeltBlock`), leaving a 3px clearance at the incoming face to completely eliminate physical collision walls for incoming items.
  - Expanded horizontal item collection bounds on level conveyors ($\pm0.15$ blocks) and lowered detection floor to $pos.getY() + 0.05$ with automatic upward alignment onto the belt surface ($0.3125$).
  - Eliminated downward gravity drag during level conveyor transit, setting vertical velocity to $0.0$ so items glide seamlessly across all block boundaries.

---

## [1.8.60] - 2026-11-02

### Added
- **2x Output Doubling via Arc Melter & Casting Press**:
  - Raw ores, ore blocks, and dusts smelted in the Electric Arc Melter yield $1,000\text{ mB}$ of molten metal.
  - The Hydraulic Casting Press now stamps $1,000\text{ mB}$ of molten fluid into **2 ingots** (e.g. Iron, Gold, Copper, Tin, Bronze) or **2 Glass blocks**, doubling the reward for running materials through the foundry chain.
  - Ingot recycling is balanced: inserting ingots into the Arc Melter yields $500\text{ mB}$ per ingot ($1,000\text{ mB}$ for 2 ingots), preventing infinite metal duplication loops.

### Changed
- **Blast Furnace and Metal Press GUI Dark Industrial Aesthetic**:
  - Replaced legacy vanilla light-gray GUI textures for both the Industrial Blast Furnace and Hydraulic Metal Press with heavy riveted dark steel panels (`#2E3339`), recessed dark slot wells (`#1D2126`), and beveled industrial bezels matching the rest of the machinery suite.
  - Realignment of `inventoryLabelY` to 74 for consistent spacing above player inventory slots.

### Fixed
- **Screen Title Alignment & Overlapping Status Text Elimination**:
  - Fixed an overlapping text collision where "ONLINE", "OFFLINE", "UNFORMED", and pump subheaders were rendered at $(x \approx 98..120, y=6)$ directly colliding with the machine screen titles (e.g., "Industrial Rock Crusher", "Hydraulic Casting Press", "Industrial Blast Furnace").
  - Removed header text collisions across all mod container screens (`IndustrialGrinderScreen`, `ArcMelterScreen`, `CastingPressScreen`, `BlastFurnaceScreen`, `MetalPressScreen`, `FermenterScreen`, `RefineryScreen`, `SmallBoilerScreen`, `LargeBoilerScreen`, `LargeDieselBoilerScreen`, `SmallDieselBoilerScreen`, `MultiSmelterScreen`, `SteamTurbineScreen`, `StorageTankScreen`, `FeedingTroughScreen`, and pump screens), leaving title rows completely uncorrupted.

---

## [1.8.59] - 2026-11-02

### Changed
- **Arc Melter Processing Speed Slowed by 50%**:
  - Increased `PROCESS_TIME` in `ArcMelterControllerBlockEntity` from 40 ticks ($2.0\text{ s}$) to 60 ticks ($3.0\text{ s}$) per smelting cycle (+50% process duration).
- **Casting Press Processing Speed Slowed by 100%**:
  - Increased `PROCESS_TIME` in `CastingPressControllerBlockEntity` from 40 ticks ($2.0\text{ s}$) to 80 ticks ($4.0\text{ s}$) per press stroke (+100% process duration / half speed).
  - Adjusted hydraulic stamping die ram stroke in `CastingPressControllerRenderer` to span 40 ticks descent and 40 ticks ascent, with ingot solidification at tick 40.
- **Molten Channel Fluid Flow & Animation Speed Reduced**:
  - Decreased casting channel fluid transfer rate from $250\text{ mB/t}$ to $80\text{ mB/t}$ in `CastingChannelBlockEntity` and `ArcMelterControllerBlockEntity`.
  - Reduced visual molten fluid surface scrolling rate from $0.035$ to $0.012$ in `CastingChannelRenderer`, providing realistic viscous molten fluid movement.

---

## [1.8.58] - 2026-11-02

### Fixed
- **Continuous Casting Press Input Port Molten Fluid Acceptance**:
  - Upgraded `SingleFluidHandler` to accept dynamic fluid setters and predicate filters, resolving an issue where empty internal tanks (`Fluids.EMPTY`) unconditionally rejected incoming fluids.
  - Configured `CASTING_PORT_BE` and `CASTING_PRESS_CONTROLLER_BE` fluid capabilities with dynamic setters and molten recipe validation (`getCastingProduct`), allowing Casting Input Ports to accept molten metals and glass from casting channels, fluid pipes, and direct bucket interactions.
  - Added automatic multiblock assembly validation (`syncAssembly()`) when ports are queried, guaranteeing instantaneous fluid capability availability without waiting for the controller's periodic tick.
  - Added direct right-click bucket dumping on `CastingPortBlock` to fill 1,000 mB of molten fluid into the input port and return an empty bucket.

- **Pipe Wire End Wire Termination & Phantom Arm Elimination**:
  - Removed erroneous arm propagation in `PipeWireEntityRenderer` when a wire run terminates on an end pipe (`wireCount <= 1`).
  - End wires now terminate cleanly at the pipe's center node without sprouting ghost perpendicular or 45°/90° arms into thin air or adjacent blocks.

- **Diesel Generator Model Alignment & Single-Item Drops**:
  - Confirmed 2-block monolithic model bounds and coordinate transformations across all four cardinal directions (NORTH, SOUTH, EAST, WEST), guaranteeing the enclosure aligns flush with placed world blocks.
  - Confirmed break behavior ensuring breaking either half of the 2x1 generator reliably drops exactly 1 item.

---

## [1.8.57] - 2026-11-02

### Fixed
- **Unlimited Casting Channel Flow Length & Bidirectional Sloshing Elimination**:
  - Eliminated the 3-channel flow limitation by implementing input direction tracking (`lastInputSide` and `lastInputTick`). Channels now prioritize forward discharge away from the source neighbor and gravity discharge downwards, preventing fluid from bouncing back and forth between adjacent blocks.
  - Channels now inform receiving neighbor channels of the flow direction upon transfer, enabling continuous cascading molten fluid flow across arbitrarily long flumes with no length restrictions.
  - Increased channel transfer rate from $100\text{ mB/t}$ to $250\text{ mB/t}$ to match the Arc Melter output spout without pooling or backflow.

- **Animated Visible Molten Metal Fluid in Casting Channels**:
  - Replaced wireframe outline rendering in `CastingChannelRenderer` with real, solid, emissive 3D molten fluid geometry using `submitCustomGeometry` and `RenderTypes.entityTranslucent`.
  - Added real-time flowing fluid surface animation with texture scrolling across all molten fluids (Iron, Gold, Copper, Tin, Bronze, and Glass) at full bright light (`15728880`).
  - Added animated downward vertical pour stream geometry when channels connect downwards into molds, basins, or lower channel runs.
  - Implemented `getUpdateTag()` in `CastingChannelBlockEntity` to ensure client chunk loading properly synchronizes fluid type and volume.

- **Arc Melter & Continuous Casting Press GUI Re-alignment**:
  - Realigned all 4 input slots in `ArcMelterMenu` to `(38, 26), (56, 26), (38, 44), (56, 44)`, perfectly matching the texture slot boxes of `industrial_grinder.png`.
  - Realigned the molten metal bath gauge in `ArcMelterScreen` to the $36 \times 36$ area at `(116..152, 26..62)` with accurate level fill, fluid surface highlight, and tooltips.
  - Replaced the mismatched `metal_press.png` layout in `CastingPressScreen` with `industrial_grinder.png`, positioning the molten metal inflow basin at `(38..74, 26..62)` and the 4 stamped product output slots at `(116, 26), (134, 26), (116, 44), (134, 44)`.
  - Centered all progress bars, energy gauges, and section headers (`ORES / DUSTS`, `MOLTEN BATH`, `MOLTEN INFLOW`, `CAST PRODUCTS`).

---

## [1.8.56] - 2026-11-02

### Fixed
- **Electric Arc Melter & Continuous Casting Press Blueprint Hologram Activation**:
  - Added sneak-right-click (Shift + Right-Click) support to both `ArcMelterControllerBlock` and `CastingPressControllerBlock` to project real-time 3D in-world holographic build guides, matching all other multiblock machines in the mod.
  - Added empty-hand right-click projection when unassembled so players without a wrench can preview multiblock construction requirements and receive structure diagnostics in chat.
  - Registered `ArcMelterControllerBlock` and `CastingPressControllerBlock` (along with `MultiSmelterControllerBlock`, `SmallDieselBoilerControllerBlock`, and `LargeDieselBoilerControllerBlock`) directly in `RefineryWrenchItem.useOn` so right-clicking with a wrench cleanly triggers holographic blueprint projection instead of block rotation.
  - Implemented `shouldRenderOffScreen()` and expanded `getRenderBoundingBox()` in `ArcMelterControllerRenderer`, `CastingPressControllerRenderer`, `MultiSmelterControllerRenderer`, and `BlastFurnaceControllerRenderer` to eliminate frustum culling when players look away from the controller block to examine the hologram structure.
  - Added `getUpdateTag()` implementation to ensure client block entities properly sync state tags upon chunk loading.

---

## [1.8.55] - 2026-11-02

### Added
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

---

## [1.8.54] - 2026-11-02

### Fixed
- **Diesel Generator Placement Alignment & Drop Duplication**:
  - Corrected East and West rotation angles in `DieselGeneratorRenderer`: inverted `EAST` from $90.0^\circ$ to $270.0^\circ$ and `WEST` from $270.0^\circ$ to $90.0^\circ$, fixing a spatial offset where the 2-block monolithic generator model rendered shifted by 2 blocks into adjacent air instead of matching its physical blocks and selection outlines.
  - Made the generator face towards the player upon placement using `context.getHorizontalDirection().getOpposite()`, matching standard Minecraft machine conventions.
  - Added a `part: left` block state property condition to `loot_table/blocks/diesel_generator.json` to prevent duplicate generator item drops when breaking either block.
  - Updated `DieselGeneratorBlock.playerWillDestroy`: in survival mode, breaking the `RIGHT` block now safely destroys the master `LEFT` block to drop the single generator item and inventory contents; in creative mode, breaking either half immediately removes its partner block cleanly without dropping items.

---

## [1.8.53] - 2026-11-02

### Fixed
- **Pipe Wire End-Wire Direction & Spurious Gate Branches**:
  - Eliminated bogus wire arm connection generation in `PipeWireEntityRenderer` that previously read `gate.getDirection()` (which returned `EAST`), causing 0.5-block long wire conduits to shoot out sideways into empty air at every gate.
  - Corrected end-wire directional continuation: when a wire terminates on a pipe that continues straight or around a corner, the wire now automatically follows the physical pipe's direction across that block instead of making an abrupt 90°/45° turn into open space.
  - Dynamically configured radial quadrant offsets (`[cx, cy, cz]`) across all 3 spatial axes ($Y$, $Z$, and $X$); on vertical pipes, wires now hug the pipe perimeter diagonally across the $X$-$Z$ plane rather than collapsing along an assumed $Z$-axis.

---

## [1.8.52] - 2026-11-01

### Fixed
- **Pipe Wires Stuck in Active ON State**:
  - Implemented a time-to-live lease/pulse hold mechanism on `PipeWireEntity` (`pulseSignal(color, ticks)`) and `PipeGateEntity` (`pulseWire(color, ticks)`) so wire signals automatically count down and expire when emitting gates cease broadcasting.
  - Resolved one-way latch where `setSignalActive(color, true)` and gate `wireStates[color.ordinal()] = true` were never reset to `false` when conditions stopped being met.
  - Replaced improper lever-toggle behavior in `PipeGateEntity.interact` when right-clicking gates with pipe wire items; right-clicking now properly attaches the wire color to the pipe host instead of manually latching the signal bit.
  - Constrained wire signal BFS network propagation along connected pipes to only traverse adjacent pipe blocks that actually have that wire color installed (`PipeWireEntity.hasWire(color)`).
  - Synchronized gate screen GUI wire status indicators and `WIRE_COLOR_ON` / `WIRE_COLOR_OFF` condition triggers dynamically each tick to live wire signal states.

---

## [1.8.51] - 2026-11-01

### Fixed
- **Sloped-to-Flat Conveyor Transition Seam & Item Sticking**:
  - Aligned the bottom surface height of the sloped conveyor belt to exactly `0.3125` ($5.0$ pixels), perfectly matching the flat conveyor belt's bed height.
  - Resolved physical collision ledge where items traveling downhill would catch against the $5.0$px front edge of the flat conveyor by configuring flat and corner conveyor collision for `ItemEntity` to $4.0$px (`Block.box(0, 0, 0, 16, 4, 16)`).
  - Tapered vertical velocity to $0.0$ near the bottom terminal ($t \ge 0.85$ on down slopes) and applied a forward hand-off momentum boost ($1.3\times$) so items seamlessly glide horizontally onto flat belts without driving into the floor.
  - Restricted direct vertical position snapping (`setPos`) to $0.0 \le t \le 1.0$ so the sloped belt never overrides item positions once they cross into the adjacent conveyor.
  - Expanded item query bounds across flat and corner conveyor block entities (`±0.08` blocks horizontally) to ensure instantaneous capture at conveyor seams.

---

## [1.8.50] - 2026-11-01

### Changed
- **Sloped & Corner Conveyor Belt Controls Unified**:
  - **Normal Right-Click** (with wrench or empty hand): Rotates conveyor direction clockwise (`NORTH` $\rightarrow$ `EAST` $\rightarrow$ `SOUTH` $\rightarrow$ `WEST`).
  - **Sneak Right-Click** (Shift + Right-Click with wrench or empty hand):
    - **Sloped Conveyor Belt**: Toggles incline elevation between `UP` and `DOWN`, allowing the conveyor belt to smoothly move items downhill instead of uphill.
    - **Right-Angle Conveyor Belt**: Toggles turn direction between `RIGHT` and `LEFT`.
  - Fixed empty-hand right-click behavior so players do not need to hold a wrench or sneak to rotate conveyors, while shift-right-click reliably toggles the slope/turn.

---

## [1.8.49] - 2026-11-01

### Added
- **Creative Fluid Pump (`refinery:fluid_pump`)**:
  - Transformed the industrial pump into a dedicated **Creative Fluid Pump**.
  - Requires zero fuel, zero electricity, and no intake target to operate.
  - Automatically generates unlimited fluid (defaults to infinite water upon placement) and auto-pushes up to 2,000 mB/t directly into connected pipes, tanks, and machines.
  - Extracting fluid from the creative pump never depletes its internal buffer.
  - Removed crafting recipe (`recipe/fluid_pump.json`) so it is obtainable exclusively via Creative Mode or commands.
  - Features a custom Vibrant Magenta frame styling (`0xFFD946EF`) and dedicated creative management GUI.

### Changed
- **Fluid Discharge Fill Range Expanded to 200 Blocks (`refinery:fluid_discharge`)**:
  - Increased contiguous fill search radius from 48 blocks to **200 blocks** ($401 \times 401$ horizontal bounding area).
  - Expanded search budget from 2,048 to **131,072 nodes** to effortlessly fill massive lakes, reservoirs, and canals.
  - Implemented high-efficiency frontier neighbor caching (`lastFillPos`) enabling $O(1)$ source block placement at the lake's perimeter without re-traversing already filled blocks every tick.
  - Guarded search traversal with `level.isLoaded()` to safely prevent lagging server ticks by loading distant unloaded chunks.
  - Increased internal buffer capacity from 4,000 mB to **16,000 mB** and enabled rapid multi-block placement (up to 4 source blocks / 4,000 mB per tick) for high-speed filling from creative or industrial pump feeds.
- **Doubled Pumping Throughput on Diesel & Electric (RF) Pumps**:
  - Doubled the output push rate of both Diesel Fluid Pumps and Electric (RF) Fluid Pumps from 500 mB/t to **1,000 mB/t** into connected pipes and tanks.
  - Doubled pumping speed and volume: cycle duration reduced from 20 ticks to **10 ticks** (2x speed) and pumped amount increased from 1,000 mB to **2,000 mB** per cycle.

---

## [1.8.48] - 2026-11-01

### Added
- **Boiler Meltdown Raid Siren Alarm Countdown**:
  - Implemented a very loud industrial raid siren alarm (`minecraft:event.raid.horn`) across all Steam Boilers and Diesel Boilers (Small and Large) during the 60-second dry-running overheat countdown prior to catastrophic explosion.
  - Broadcasts at high volume (16.0fâ€“20.0f, audible up to 250â€“320+ blocks away) so players anywhere in their factory or base receive an immediate emergency audible warning when a boiler runs out of water while actively burning fuel.
  - Escalating urgency cadence:
    - **Initial 0sâ€“30s**: Deep ominous horn blast every 4 seconds (pitch 0.85).
    - **Urgent 30sâ€“45s**: Faster horn blast every 3 seconds (pitch 1.0).
    - **Critical Final 15s**: Rapid, high-pitched shrieking siren blast every 2 seconds (pitch 1.2) accompanied by thick black smoke and hissing steam until water is replenished, redstone is disengaged, or catastrophic explosion occurs.

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
