# Changelog — Industrial Refinery

All notable changes to the **Industrial Refinery** mod are documented in this file.

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
