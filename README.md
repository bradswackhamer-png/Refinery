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
   - [Crude Pump Jack (2×3×3 Multiblock)](#crude-pump-jack-233-multiblock)
   - [3-Stage Petroleum Refinery (3×9×3 Multiblock)](#3-stage-petroleum-refinery-393)
   - [Refinery Furnaces: Burner & Electric Heat Sources (1×2×1)](#refinery-furnaces-burner--electric-heat-sources-121)
   - [Oil Sand Cracking & Extraction](#oil-sand-cracking--extraction)
   - [Fuels & Byproducts](#fuels--byproducts)
   - [Industrial Grinder & Pulverizer (3×3×3 Multiblock)](#industrial-grinder--pulverizer-3x3x3)
   - [Heavy Pit Quarry Excavator (3×3×3 Multiblock)](#heavy-pit-quarry-excavator-3x3x3)
7. [Comprehensive Recipe Codex](#7-comprehensive-recipe-codex)
8. [Automation, Conveyors, Pipes, & Interfacing](#8-automation-conveyors-pipes--interfacing)
   - [Industrial Belt Conveyor](#industrial-belt-conveyor)
   - [Sloped Conveyor Belt](#sloped-conveyor-belt)
   - [Right-Angle Conveyor Belt](#right-angle-conveyor-belt)
   - [Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks](#glass-fused-to-steel-gfs-multiblock-liquid-storage-tanks)
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
   - [Version 1.11.60 (Thermodynamic Rebalance: 500 & 1,500 mB/s Boilers, 1,450 mB/s Turbine, 3,000 RPM Scaling & Fluid Pipe Flow Fix)](#version-11160-thermodynamic-rebalance-500--1500-mbs-boilers-1450-mbs-turbine-3000-rpm-scaling--fluid-pipe-flow-fix)
   - [Version 1.11.58 (In-Game Field Manual Boiler Guide Pages: Small & Large Steam and Diesel Boilers)](#version-11158-in-game-field-manual-boiler-guide-pages-small--large-steam-and-diesel-boilers)
   - [Version 1.11.57 (Large Boiler 3D Model Block Hiding, Steam Turbine Dynamic Load Throttling & Diagnostic Monitor Sync)](#version-11157-large-boiler-3d-model-block-hiding-steam-turbine-dynamic-load-throttling--diagnostic-monitor-sync)
   - [Version 1.11.54 (Multi-Smelter Plank Pyrolysis: Planks to Charcoal)](#version-11154-multi-smelter-plank-pyrolysis-planks-to-charcoal)
   - [Version 1.11.51 (Iron Item Pipe & Cast Iron Fluid Pipe Recipe Collision Fix)](#version-11151-iron-item-pipe--cast-iron-fluid-pipe-recipe-collision-fix)
   - [Version 1.11.49 (Industrial Diesel Generator Single-Item Drop Fix)](#version-11149-industrial-diesel-generator-single-item-drop-fix)
   - [Version 1.11.47 (Autarchic Chipset 4x Recipe Output)](#version-11147-autarchic-chipset-4x-recipe-output)
   - [Version 1.11.45 (Per-Tick Machine Energy Requirements, 4x Faster Casting Channel Flow, & Anti-Bounce-Back)](#version-11145-per-tick-machine-energy-requirements-4x-faster-casting-channel-flow--anti-bounce-back)
   - [Version 1.11.44 (Arc Melter 50k FE Start Threshold, Ground-Level Casting Input Port, 1:1 Water Quenching & JEI Hologram Name Parity)](#version-11144-arc-melter-50k-fe-start-threshold-ground-level-casting-input-port-11-water-quenching--jei-hologram-name-parity)
   - [Version 1.11.42 (In-Game Guide Book Power & Energy Badges, 20s Casting Press & 10s Arc Melter Durations)](#version-11142-in-game-guide-book-power--energy-badges-20s-casting-press--10s-arc-melter-durations)
   - [Version 1.11.40 (Industrial Power & Steam Rebalance: 10k Arc Melter, 10k Steam Turbine, 3x Large Boiler, 5k Multi-Smelter, 2k Grinder, 500 Press, 600 Caster)](#version-11140-industrial-power--steam-rebalance-10k-arc-melter-10k-steam-turbine-3x-large-boiler-5k-multi-smelter-2k-grinder-500-press-600-caster)
   - [Version 1.11.39 (Fast 0.5s Pipe Gate Redstone Pulsing & Natural Action Cycle Order)](#version-11139-fast-05s-pipe-gate-redstone-pulsing--natural-action-cycle-order)
   - [Version 1.11.38 (AWWA Safety-Yellow Fire Hydrant & Pipe-Fed Bucket Filling Station)](#version-11138-awwa-safety-yellow-fire-hydrant--pipe-fed-bucket-filling-station)
   - [Version 1.11.37 (Configurable Quarry Max Speed & Energy Rates, 240 FE/t & 1.5 blocks/s Defaults)](#version-11137-configurable-quarry-max-speed--energy-rates-240-fet--15-blockss-defaults)
   - [Version 1.11.35 (Perimeter Safety Fence Preservation, Stone-to-Cobblestone Throw-Back Equivalence)](#version-11135-perimeter-safety-fence-preservation-stone-to-cobblestone-throw-back-equivalence)
   - [Version 1.11.33 (In-Game Field Manual Guide Book Quarry & Machinery Additions, Wrench Hologram Projection)](#version-11133-in-game-field-manual-guide-book-quarry--machinery-additions-wrench-hologram-projection)
   - [Version 1.11.32 (Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Recipe)](#version-11132-heavy-pit-quarry-1-controller--26-casings-multiblock-recipe)
   - [Version 1.11.20 (Refinery Heaters Single-Item Drop, Steam Turbine GUI Overhaul & Header Alignment)](#version-11120-refinery-heaters-single-item-drop-steam-turbine-gui-overhaul--header-alignment)
   - [Version 1.11.19 (Crude Pump Jack 2x3x3 Multiblock, Bitumen Pile Alignment & Drop Fixes)](#version-11119-crude-pump-jack-2x3x3-multiblock-bitumen-pile-alignment--drop-fixes)
   - [Version 1.11.18 (Bitumen Storage Pile 3x3 Footprint, Single-Item Drop Fix, GUI Label Rescaling)](#version-11118-bitumen-storage-pile-3x3-footprint-single-item-drop-fix-gui-label-rescaling)
   - [Version 1.11.17 (Bitumen Storage Pile, Dynamic Visual Growth & Vacuum Absorption)](#version-11117-bitumen-storage-pile-dynamic-visual-growth--vacuum-absorption)
   - [Version 1.11.16 (Multiblock Glass-Fused-to-Steel Storage Tank Flush Button Fix)](#version-11116-multiblock-glass-fused-to-steel-storage-tank-flush-button-fix)
   - [Version 1.11.15 (Blast Furnace & Metal Press GUI Alignment Fixes)](#version-11115-blast-furnace--metal-press-gui-alignment-fixes)
   - [Version 1.11.13 (Conveyor Belt 90-Degree Corner & T-Intersection Adaptive Frame)](#version-11113-conveyor-belt-90-degree-corner--t-intersection-adaptive-frame)
   - [Version 1.11.11 (Seamless Sloped-to-Flat Conveyor Transition & Collision Fix)](#version-11111-seamless-sloped-to-flat-conveyor-transition--collision-fix)
   - [Version 1.11.10 (Sloped Conveyor Belt Ground Support Legs & Foot Pads)](#version-11110-sloped-conveyor-belt-ground-support-legs--foot-pads)
   - [Version 1.11.9 (Refinery Tower South Takeoff Pipes Realignment, Arc Melter Hazard Stripe Removal)](#version-1119-refinery-tower-south-takeoff-pipes-realignment-arc-melter-hazard-stripe-removal)
   - [Version 1.11.8 (Refinery Output Fluid Auto-Push, Stage 1 South Takeoff Pipe, Anti-Flicker Fixes)](#version-1118-refinery-output-fluid-auto-push-stage-1-south-takeoff-pipe-anti-flicker-fixes)
   - [Version 1.11.4 (Refinery Tower South Takeoff Pipes Re-orientation & Vertical Header Removal)](#version-1114-refinery-tower-south-takeoff-pipes-re-orientation--vertical-header-removal)
   - [Version 1.11.3 (Dark Gunmetal Industrial GUI Redesign & Stage Toggle Button Fix)](#version-1113-dark-gunmetal-industrial-gui-redesign--stage-toggle-button-fix)
   - [Version 1.11.2 (3-Stage 3×9×3 Petroleum Refinery Rebuild, Burner & Electric Refinery Furnaces, Liquid Tar)](#version-1112-3-stage-393-petroleum-refinery-rebuild-burner--electric-refinery-furnaces-liquid-tar)
   - [Version 1.10.25 (Texture Flickering Resolution, Control Panel UV Fix & 3D Monolithic Guide)](#version-11025-texture-flickering-resolution-control-panel-uv-fix--3d-monolithic-guide)
   - [Version 1.10.23 (Monolithic 3D Electric Arc Melter & Continuous Casting Press Overhaul)](#version-11023-monolithic-3d-electric-arc-melter--continuous-casting-press-overhaul)
   - [Version 1.10.21 (Plate Stamping Die Progression Fix)](#version-11021-plate-stamping-die-progression-fix)
   - [Version 1.10.19 (Steel Gear Tag & Valve Pipe Upgrade Recipe Fix)](#version-11019-steel-gear-tag--valve-pipe-upgrade-recipe-fix)
   - [Version 1.10.17 (Redstone Valve Pipe, Pipe Gate Flow Control, Spool Wire Icons)](#version-11017-redstone-valve-pipe-pipe-gate-flow-control-spool-wire-icons)
   - [Version 1.10.15 (Metal Press Tooling Dies, Machinery Gears, Corrugated Steel & Rail Stamping)](#version-11015-metal-press-tooling-dies-machinery-gears-corrugated-steel--rail-stamping)
   - [Version 1.10.14 (Slender 3D 8-Bit Pipes, Dedicated Facades Tab, Dynamic Block Textures)](#version-11014-slender-3d-8-bit-pipes-dedicated-facades-tab-dynamic-block-textures)
   - [Version 1.10.12 (Pipe Facade Network Codec Fix & Tin Ore Silk Touch Loot Table Fix)](#version-11012-pipe-facade-network-codec-fix--tin-ore-silk-touch-loot-table-fix)
   - [Version 1.10.11 (3D 8-Bit Pixel Art Pipe Item Icons & Fluid Markings)](#version-11011-3d-8-bit-pixel-art-pipe-item-icons--fluid-markings)
   - [Version 1.10.10 (BuildCraft-Style Pipe Facades & Structure Pipe)](#version-11010-buildcraft-style-pipe-facades--structure-pipe)
   - [Version 1.10.9 (Cross-Mod Conveyor Belt Transition & Ole Saw Mill Fix)](#version-1109-cross-mod-conveyor-belt-transition--ole-saw-mill-fix)
   - [Version 1.10.7 (LabPBR 1.3 & POM for Corrugated Steel Panels)](#version-1107-labpbr-13--pom-for-corrugated-steel-panels)
   - [Version 1.10.5 (Steam Bucket Creative Restriction & Item Pipe Multi-Block Traversal)](#version-1105-steam-bucket-creative-restriction--item-pipe-multi-block-traversal)
   - [Version 1.10.3 (Model & Blockstate Resolution, 26.3 Recipe Syntax Migration)](#version-1103-model--blockstate-resolution-263-recipe-syntax-migration)
   - [Version 1.10.2 (Corrugated Steel Panels & 16 Dyed Color Variants)](#version-1102-corrugated-steel-panels--16-dyed-color-variants)
   - [Version 1.10.1 (Bituminous Oil Sands & Shale, 3D Pipe End Caps, Item Ejection)](#version-1101-bituminous-oil-sands--shale-3d-pipe-end-caps-item-ejection)
   - [Version 1.9.9 (Multi-Smelter, Metal Press, & Blast Furnace Conveyor Auto-Export)](#version-199-multi-smelter-metal-press--blast-furnace-conveyor-auto-export)
   - [Version 1.9.8 (Multi-Smelter Dark Gunmetal GUI & Item Pipe In-Transit Visibility)](#version-198-multi-smelter-dark-gunmetal-gui--item-pipe-in-transit-visibility)
   - [Version 1.9.7 (Guide Book Click Fix)](#version-197-guide-book-click-fix)
   - [Version 1.9.6 (Informative Pipe Tooltips & Transfer Rates)](#version-196-informative-pipe-tooltips--transfer-rates)
   - [Version 1.9.5 (In-Game Industrial Field Manual / Guide Book)](#version-195-in-game-industrial-field-manual--guide-book)
   - [Version 1.9.4 (Wood Extraction Pipe Filter GUI & Pulse Gate Extraction)](#version-194-wood-extraction-pipe-filter-gui--pulse-gate-extraction)
   - [Version 1.9.3 (Cross-Mod Conveyor Belt Handoff & Trapped Item Resolution)](#version-193-cross-mod-conveyor-belt-handoff--trapped-item-resolution)
   - [Version 1.9.2 (Autarchic Pulse Item x5 / x10)](#version-192-autarchic-pulse-item-x5--x10)
   - [Version 1.9.0 (Hologram Blueprint Label Size Reduced by 60%)](#version-190-hologram-blueprint-label-size-reduced-by-60)
   - [Version 1.8.63 (Petroleum Refinery Diesel Output Port Relocation)](#version-1863-petroleum-refinery-diesel-output-port-relocation)
   - [Version 1.8.62 (Arc Melter & Casting Press Speeds, Viscous Lava Channel Flow, Grinder Auto-Export)](#version-1862-arc-melter--casting-press-speeds-viscous-lava-channel-flow-grinder-auto-export)
   - [Version 1.8.61 (Sloped-to-Flat Conveyor Crest Transition & Item Sticking Resolution)](#version-1861-sloped-to-flat-conveyor-crest-transition--item-sticking-resolution)
   - [Version 1.8.60 (Arc Melter 2x Output Doubling, Dark Steel Blast Furnace & Metal Press GUI, Clean Screen Headers)](#version-1860-arc-melter-2x-output-doubling-dark-steel-blast-furnace--metal-press-gui-clean-screen-headers)
   - [Version 1.8.59 (Arc Furnace, Casting Press, & Channel Flow Rates Adjusted)](#version-1859-arc-furnace-casting-press--channel-flow-rates-adjusted)
   - [Version 1.8.58 (Casting Press Input Port Molten Acceptance, Pipe Wire Termination, & Generator Drops)](#version-1858-casting-press-input-port-molten-acceptance-pipe-wire-termination--generator-drops)
   - [Version 1.8.57 (Unlimited Channel Flow Length, Animated Molten Metal, & Arc/Casting GUI Alignment)](#version-1857-unlimited-channel-flow-length-animated-molten-metal--arccasting-gui-alignment)
   - [Version 1.8.56 (Electric Arc Melter & Continuous Casting Press Blueprint Holograms)](#version-1856-electric-arc-melter--continuous-casting-press-blueprint-holograms)
   - [Version 1.8.55 (Buffer Pipe 1-Slot Chest Pipe)](#version-1855-buffer-pipe-1-slot-chest-pipe)
   - [Version 1.8.54 (Diesel Generator Placement Alignment & Drop Duplication Fix)](#version-1854-diesel-generator-placement-alignment--drop-duplication-fix)
   - [Version 1.8.53 (Pipe Wire End-Wire Direction & Spurious Gate Branches Fix)](#version-1853-pipe-wire-end-wire-direction--spurious-gate-branches-fix)
   - [Version 1.8.52 (Pipe Wire Signal Expiration & Active State Stuck Fix)](#version-1852-pipe-wire-signal-expiration--active-state-stuck-fix)
   - [Version 1.8.51 (Sloped-to-Flat Conveyor Transition Seam & Item Sticking Fix)](#version-1851-sloped-to-flat-conveyor-transition-seam--item-sticking-fix)
   - [Version 1.8.50 (Sloped & Corner Conveyor Belt Controls Unified)](#version-1850-sloped--corner-conveyor-belt-controls-unified)
   - [Version 1.8.49 (Creative Fluid Pump, 2x Pump Output, & 200-Block Fluid Discharge Range)](#version-1849-creative-fluid-pump-2x-pump-output--200-block-fluid-discharge-range)
   - [Version 1.8.48 (Boiler Meltdown Raid Siren Alarm Countdown)](#version-1848-boiler-meltdown-raid-siren-alarm-countdown)
   - [Version 1.8.47 (Boiler Infinite Water Draw & Phantom Fluid Voiding Fix)](#version-1847-boiler-infinite-water-draw--phantom-fluid-voiding-fix)
   - [Version 1.8.46 (Storage Tank Valve Isolation & Side Connection Prevention)](#version-1846-storage-tank-valve-isolation--side-connection-prevention)
   - [Version 1.8.45 (Pipe Fluid Capacity Reduction 250â€“500 mB & High-Speed Flow)](#version-1845-pipe-fluid-capacity-reduction-250500-mb--high-speed-flow)
   - [Version 1.8.44 (Diamond Sorting Pipe Filter Retention Across Restarts)](#version-1844-diamond-sorting-pipe-filter-retention-across-restarts)
   - [Version 1.8.43 (Terrain Loading Deadlock Fix with C2ME)](#version-1843-terrain-loading-deadlock-fix-with-c2me)
   - [Version 1.8.42 (Minecraft 26.3 Worldgen Feature Migration & en_us.json Fix)](#version-1842-minecraft-263-worldgen-feature-migration--en_usjson-fix)
   - [Version 1.8.41 (Minecraft Bootstrap Block Registration Fix)](#version-1841-minecraft-bootstrap-block-registration-fix)
   - [Version 1.8.40 (Industrial Diesel Generator 1x2x2 Compact Power Plant)](#version-1840-industrial-diesel-generator-1x2x2-compact-power-plant)
   - [Version 1.8.39 (Tin Ore Worldgen & Retrogen, Bronze 1:1 Forging Ratio)](#version-1839-tin-ore-worldgen--retrogen-bronze-11-forging-ratio)
   - [Version 1.8.38 (Pipe State & 3D Elbow Connection Retention on Server Restart)](#version-1838-pipe-state--3d-elbow-connection-retention-on-server-restart)
   - [Version 1.8.37 (Pipe Texture Markings Cleanup & 1/8 Clear Sight-Glass Strips)](#version-1837-pipe-texture-markings-cleanup--18-clear-sight-glass-strips)
   - [Version 1.8.36 (Pipe Connection Persistence Across Restarts & Snapping)](#version-1836-pipe-connection-persistence-across-restarts--snapping)
   - [Version 1.8.35 (Multiblock Storage Tank Auto-Export & Pipe Connectivity)](#version-1835-multiblock-storage-tank-auto-export--pipe-connectivity)
   - [Version 1.8.34 (Boiler Redstone Shutdown & Meltdown Prevention)](#version-1834-boiler-redstone-shutdown--meltdown-prevention)
   - [Version 1.8.33 (Continuous 3D Surface-Mounted Pipe Wires)](#version-1833-continuous-3d-surface-mounted-pipe-wires)
   - [Version 1.8.32 (Sloped Conveyor Belt Item Ascent & Collision Fix)](#version-1832-sloped-conveyor-belt-item-ascent--collision-fix)
   - [Version 1.8.31 (Industrial Grinder Hopper Bounding & Conveyor Export Validation)](#version-1831-industrial-grinder-hopper-bounding--conveyor-export-validation)
   - [Version 1.8.30 (Fluid Pump Back-Draining & Pipe Leak Resolution)](#version-1830-fluid-pump-back-draining--pipe-leak-resolution)
   - [Version 1.8.29 (Automated Planter Sapling Spacing & Gate Placement Snapping)](#version-1829-automated-planter-sapling-spacing--gate-placement-snapping)
   - [Version 1.8.28 (Pipe Sight-Glass Inspection Strips & Flow Visualization)](#version-1828-pipe-sight-glass-inspection-strips--flow-visualization)
   - [Version 1.8.27 (Pipe Gate 3D Saddle Orientation & Radial Face Snapping)](#version-1827-pipe-gate-3d-saddle-orientation--radial-face-snapping)
   - [Version 1.8.26 (Diamond Pipe 3D Color-Coded Connection Bands & Whitelist GUI Legend)](#version-1826-diamond-pipe-3d-color-coded-connection-bands--whitelist-gui-legend)
   - [Version 1.8.25 (Minecraft 26.3 GUI Mouse Button Fix & Text Alpha Visibility)](#version-1825-minecraft-263-gui-mouse-button-fix--text-alpha-visibility)
   - [Version 1.8.24 (Auto Harvester GUI Synchronization & Gate Saddle Alignment)](#version-1824-auto-harvester-gui-synchronization--gate-saddle-alignment)
   - [Version 1.8.24 (Complete In-Game Localization & Translation Key Fix)](#version-1824-complete-in-game-localization--translation-key-fix)
   - [Version 1.8.23 (Quartz Merge Pipe Suite & Selectable Output Routing)](#version-1823-quartz-merge-pipe-suite--selectable-output-routing)
   - [Version 1.8.22 (Void Pipe Shaped Crafting Parity & Gate Curvature Fix)](#version-1822-void-pipe-shaped-crafting-parity--gate-curvature-fix)
   - [Version 1.8.21 (Pipe Gate Orientation, Extended Dead-Ends, & Conveyor Occlusion Fix)](#version-1821-pipe-gate-orientation-extended-dead-ends--conveyor-occlusion-fix)
   - [Version 1.8.20 (Tiered Pipe Suite, Insertion, Sorting, Round-Robin, & Waterproofing)](#version-1820-tiered-pipe-suite-insertion-sorting-round-robin--waterproofing)
   - [Version 1.8.19 (Conveyor Occlusion, Gate Saddle Hugging, & Sloped Conveyor Alignment)](#version-1819-conveyor-occlusion-gate-saddle-hugging--sloped-conveyor-alignment)
   - [Version 1.8.18 (World Loading Fix & Brass Pipe Recipe Correction)](#version-1818-world-loading-fix--brass-pipe-recipe-correction)
   - [Version 1.8.17 (Auto Harvester Directional Front-Only Harvest Geometry)](#version-1817-auto-harvester-directional-front-only-harvest-geometry)
   - [Version 1.8.16 (Auto Planter & Harvester GUI Controls & Network Sync Fix)](#version-1816-auto-planter--harvester-gui-controls--network-sync-fix)
   - [Version 1.8.15 (Industrial Grinder Empty Inventory Rendering & Sync Fix)](#version-1815-industrial-grinder-empty-inventory-rendering--sync-fix)
   - [Version 1.8.14 (Pipe Blank Gates, 3D Curved Saddle Gates & Logistics Recipe Parity)](#version-1814-pipe-blank-gates-3d-curved-saddle-gates--logistics-recipe-parity)
   - [Version 1.8.13 (Native 3D Pipe Texture Flickering & Z-Fighting Resolution)](#version-1813-native-3d-pipe-texture-flickering--z-fighting-resolution)
   - [Version 1.8.12 (Native Industrial 3D Monolithic Pipe System)](#version-1812-native-industrial-3d-monolithic-pipe-system)
   - [Version 1.8.11 (True Round 3D Monolithic Pipe Geometry & Seamless Runs)](#version-1811-true-round-3d-monolithic-pipe-geometry--seamless-runs)
   - [Version 1.8.10 (Logistics Automation Ultra-Realistic 3D Monolithic Pipe Resource Pack)](#version-1810-logistics-automation-ultra-realistic-3d-monolithic-pipe-resource-pack)
   - [Version 1.8.9 (Conveyor In-Place Rotation, Sloped & Right-Angle Conveyors, Crusher Auto-Export)](#version-189-conveyor-in-place-rotation-sloped--right-angle-conveyors-crusher-auto-export)
   - [Version 1.8.8 (Steam Turbine & Boiler Fluid Auto-Transfer)](#version-188-steam-turbine--boiler-fluid-auto-transfer)
   - [Version 1.8.7 (Blast Furnace Input Restrictions & Fluid Discharge Drop Fix)](#version-187-blast-furnace-input-restrictions--fluid-discharge-drop-fix)
   - [Version 1.8.6 (Storage Tank Ladder & Visual Clean-up)](#version-186-storage-tank-ladder--visual-clean-up)
   - [Version 1.8.5 (Glass-Fused-to-Steel Multiblock Liquid Storage Tanks)](#version-185-glass-fused-to-steel-multiblock-liquid-storage-tanks)
   - [Version 1.8.4 (Steam Turbine Monolithic 3D Model & High-Resolution Textures)](#version-184-steam-turbine-monolithic-3d-model--high-resolution-textures)
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
[Iron Ingot / Block + Coal / Block] ---> (5x5x5 Blast Smelter) ---> [Steel Ingot]
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
| **Tin Ore** (`refinery:tin_ore`) | Subterranean Worldgen ($Y = -16$ to $112$) | Naturally occurring tin ore in stone formations. |
| **Deepslate Tin Ore** (`refinery:deepslate_tin_ore`) | Deep Subterranean Worldgen ($Y < 0$) | Deepslate tin ore deposits in deep strata. |
| **Raw Tin** (`refinery:raw_tin`) | Mining Tin Ores | Raw unrefined tin metal. Smelts into Tin Ingots or grinds into 2x Tin Dust. |
| **Block of Raw Tin** (`refinery:raw_tin_block`) | Crafting (9 Raw Tin) | Compact raw ore storage block. |
| **Block of Tin** (`refinery:tin_block`) | Crafting (9 Tin Ingots) | Solid metal storage block for refined tin. |
| **Block of Bronze** (`refinery:bronze_block`) | Crafting (9 Bronze Ingots) | Solid metal storage block for bronze alloy. |
| **Tin Ingot** (`refinery:tin_ingot`) | Smelting Tin Dust / Ores / Raw Tin | Malleable industrial metal used for bronze alloying and plating. |
| **Tin Dust** (`refinery:tin_dust`) | Grinding Tin Ores or Raw Tin (x2) | Pulverized tin mineral used for smelting or alloy blending. |
| **Tin Plate** (`refinery:tin_plate`) | Metal Press (Tin Ingot) | Stamped tin plate used in circuitry and engineering. |
| **Bronze Ingot** (`refinery:bronze_ingot`) | Alloying 1 Copper + 1 Tin $\rightarrow$ 2 Bronze | Durable copper-tin alloy ingot for machinery and tools. |
| **Bronze Dust** (`refinery:bronze_dust`) | Grinding Bronze Ingot or blending 1:1 dusts | Alloy dust that smelts directly into Bronze Ingots. |
| **Bronze Plate** (`refinery:bronze_plate`) | Metal Press (Bronze Ingot) | Heavy-duty stamped bronze structural plate. |
| **Coal Dust** (`refinery:coal_dust`) | Grinding Coal or Coal Blocks | Finely pulverized coal fuel for Blast Smelters and boilers. |
| **Iron Plate** (`refinery:iron_plate`) | Metal Press (Iron Ingot) | Standard industrial stamped iron plate. |
| **Copper Plate** (`refinery:copper_plate`) | Metal Press (Copper Ingot) | Highly conductive stamped plate for electrical piping and components. |
| **Gold Plate** (`refinery:gold_plate`) | Metal Press (Gold Ingot) | Corrosion-resistant stamped plate for advanced logic chips and gates. |
| **Petroleum Tar** (`refinery:tar`) | Refinery byproduct / Oil Sand grinding | Heavy hydrocarbon fuel (burns 1,600 ticks / 8 items) or crafts into torches/pipes. |
| **Sulfur Dust** (`refinery:sulfur_dust`) | Refinery byproduct | Chemical mineral used to craft Gunpowder without creepers. |
| **Diesel** (`refinery:diesel`) | Refinery fractional distillation | High-yield petroleum distillate. |
| **Industrial Diesel Generator** (`refinery:diesel_generator`) | Heavy Machinery Crafting | Compact 2-block power station generating 256 FE/t from refined Diesel. |
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
- **Strict Supported Ingredients**:
  - **Iron Sources**: Iron Ingot (`minecraft:iron_ingot`) or Block of Iron (`minecraft:iron_block`). (1 Iron Ingot = 1 Iron Unit; 1 Block of Iron = 9 Iron Units). Raw iron, ores, and dusts are not accepted.
  - **Fuel Sources**: Coal (`minecraft:coal`) or Block of Coal (`minecraft:coal_block`). (1 Coal = 1 Fuel Unit; 1 Block of Coal = 9 Fuel Units). Charcoal, dusts, petroleum tar, bitumen, and other burnables are not accepted.
- **Recipe**: 1 Iron Unit + 1 Fuel Unit -> 1 Steel Ingot (100 ticks). 1 Block of Iron + 1 Block of Coal yields 9 Steel Ingots.
- **Buffers**: 4 Iron input slots, 4 Fuel slots, 4 Steel output slots. Partial block units are tracked and preserved.
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

### Industrial Metal Press (3×2×2)
A universal hydraulic mechanical press with a 3-block-wide conveyor bed and an overhead AIDA NC1-1500 stamping head for cold-forming ingots and plates into industrial plates, gears, corrugated siding, and rails.

- **Tooling Die Requirement**: The press **strictly requires a Stamping Die** inserted into the dedicated tooling slot below the ram. Different dies configure the press for different product lines:
  - **Plate Die** (`refinery:plate_die` — crafted from 4 Steel Ingots):
    - Stamps 1 Ingot (Steel, Iron, Copper, Gold, Tin, Bronze) $\rightarrow$ **1x Respective Plate**.
  - **Gear Die** (`refinery:gear_die` — crafted from 4 Steel Plates):
    - Cold-forms 1 Ingot or Plate $\rightarrow$ **1x Respective Gear** (**Steel Gear**, **Iron Gear**, **Copper Gear**, **Bronze Gear**, **Gold Gear**, **Tin Gear**) with 4x material efficiency over hand crafting.
  - **Corrugated Steel Die** (`refinery:corrugated_die`):
    - **1 Steel Ingot (or Plate)** $\rightarrow$ **4x Corrugated Steel Panels** (`refinery:corrugated_steel_panel`).
    - **1 Iron Ingot (or Plate)** $\rightarrow$ **2x Corrugated Steel Panels**.
  - **Rail Die** (`refinery:rail_die`):
    - **1 Steel Ingot (or Plate)** $\rightarrow$ **4x Rails** (`minecraft:rail`).
    - **1 Iron Ingot (or Plate)** $\rightarrow$ **2x Rails**.
    - **1 Gold Ingot (or Plate)** $\rightarrow$ **2x Powered Rails** (`minecraft:powered_rail`).
    - **1 Copper Ingot (or Plate)** $\rightarrow$ **2x Activator Rails** (`minecraft:activator_rail`).
- **Dimensions**: 3 wide × 2 tall × 2 deep (12 blocks total; also supports 3×2×1 layout).
- **Physical 3D Stamping Animation**: Features an in-world animated hydraulic piston ram with a power stroke, heavy anvil impact (`SoundEvents.ANVIL_LAND`), and spark ejection!
- **Energy**: 100,000 FE buffer; consumes 500 FE/t (20,000 FE per cycle).
- **Logistics**:
  - **Slot 0 (Die)**: Tooling slot (stacks to 1); protected from hopper extraction.
  - **Top & Sides**: Inserts raw materials into the 2-slot input queue (Slots 1..2).
  - **Bottom / Auto-Export**: Extracts finished goods from the 2-slot output buffer (Slots 3..4). Automatically ejects onto connected conveyor belts, pipes, or chests.

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
  - **Electric / RF Mode**: Integrated 1,000,000 FE energy storage buffer. Consumes **5,000 FE/t** total while cooking items without burning any solid fuel.
- **Early-Game Accessible (NO Steel Required!)**:
  - Engineered specifically for early game automation prior to obtaining steel.
  - Crafted entirely from **Iron Ingots, Copper Ingots, Clay Bricks, Smooth Stone, Furnaces, Glass, Hoppers, and Droppers**. Absolutely no Steel Plates or Steel Ingots are needed!
- **Automation & Sided Interfacing**:
  - **Smelter Input Port**: Automatically routes items from hoppers/pipes into the 8 input slots.
  - **Smelter Output Port**: Automatically extracts completed items from the 8 output slots.
  - **Smelter Fuel/Energy Port**: Accepts solid fuel into the fuel slot and connects directly to external RF energy cables or power generators.
  - **Refinery Wrench Support**: Right-clicking the controller with a wrench projects a full 60-block 3D holographic blueprint with labeled block requirements.
  - **Dark Gunmetal Industrial Interface**: Redesigned heavy steel UI matching the Grinder and Metal Press with energy gauges, combustion flame & RF induction activity meters, and dedicated cook progress tracks under each slot.
  - **Auto-Export to Conveyors & Factory Logistics**: When a conveyor belt is placed in front of the controller, along the front face, or adjacent to the Output Port, finished products (charcoal, ingots, glass) are automatically ejected onto the conveyor with smooth single-item spacing.

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
- **Performance**: 3× Small Boiler production rate (150 mB Water -> 300 mB Steam per cycle, exactly matching 3 Small Steam Boilers).
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
  - **Consumption & Output**: Consumes 30 mB Diesel + 300 mB Water -> Produces 750 mB Steam per operational cycle (3× Small Diesel Boiler).
  - **Custom 3-Tank Industrial GUI**: High-capacity Diesel, Water, and Steam gauges with automated port interfacing.
- **Structure**: Uses the exact same multiblock casing, pipe, and port layouts as the standard boilers, swapping only the controller block to `Small Diesel Boiler Controller` or `Large Diesel Boiler Controller`. Water and Diesel can be supplied through ports or directly into the controller.

---

### Steam Turbine Generator (3×3×3)
A heavy Rankine-cycle turbine converting High Pressure Steam into Forge Energy and recyclable water.

- **Monolithic 3D Model**: High-fidelity industrial turbine-generator skid in vibrant industrial electric blue (`#12569F`), inspired by heavy industrial Elliott, Dresser-Rand, and Siemens steam turbine generator sets.
  - **Structural Steel Base Skid**: Heavy I-beam perimeter runners, transverse cross-beams, and foundation anchor footing pads with mounting shims.
  - **Electric AC Alternator / Generator**: Cylindrical stator housing with horizontal cooling vents, end bell air intake cowl with perforated black mesh, and top rectangular junction box with high-voltage hazard plate.
  - **Intermediate Gearbox**: Cast speed-reduction casing with red lubrication inspection tag ("Royal Purple" style), oil level sight glass, and flexible coupling guards.
  - **Split Turbine Casing**: Horizontally split casing shell with curved dome, prominent bolted split-line perimeter flange, circular bearing end covers, and crane lifting eye lugs.
  - **Steam Inlet Chest & Governor**: Front flanged steam inlet nozzle with circular bolt circle and dark steam bore, throttle trip valve with red handwheel, and top governor valve actuator assembly with linkage arms.
  - **Upright Control Console & Gauges**: Upper blue instrumentation box housing three analog dial gauges (Steam Pressure 0-600 PSI, Turbine RPM, and Condenser Vacuum) and digital LED readout, plus lower silver/grey electrical cabinet with 3D red emergency stop mushroom button, yellow bezel, rotary selector switches, status lamps, and wiring conduit.
  - **Dynamic Emissive Indicators**: Full-bright glowing green run lamp and active power output display when actively generating FE (`isActive = true`).
  - **Seamless Invisibility**: Constituent blocks become invisible with empty occlusion shapes when assembled, seamlessly rendering the monolithic model with interaction forwarding to the controller menu and wrench blueprint toggles.
- **Capacity**: 32,000 mB Steam, 16,000 mB Water, **1,000,000 FE battery**.
- **Thermodynamic Conversion**:
  - Consumes up to 300 mB Steam/t (requires 3× Small Boiler steam generation) -> Generates **10,000 FE/t** + 150 mB/t Condensed Water (closed Rankine cycle back to boilers).
  - Output extraction rate: Up to 50,000 FE/t via Energy Port or Controller with automated power push to adjacent handlers.

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

### Crude Pump Jack (2×3×3 Multiblock)
A heavy industrial beam pumping unit (oil pumpjack) designed for automated deep extraction of crude oil from subterranean reservoirs and surface oil lakes.
- **Dimensions & Footprint**: 2 blocks wide × 3 blocks long × 3 blocks high (18 blocks total).
- **Single-Item Placement**: The entire 2×3×3 multiblock is placed from a single inventory item (`refinery:crude_pump_jack`) facing away from the player. It automatically checks for unobstructed space across all 18 blocks before placing.
- **Single-Item Drop Guarantee**: Mining or destroying any block of the multiblock drops exactly **one** Crude Pump Jack item in survival (and none in creative), cleanly removing the remaining sibling blocks without duplicate drops.
- **Kinematic Monolithic 3D Animation**: Features a high-fidelity monolithic renderer with a 256×256 texture atlas:
  - Heavy structural steel skid and Samson post A-frame.
  - Rocking walking beam with center saddle bearing ($\pm 12^\circ$ pitch).
  - Arc-shaped horsehead and wireline hanger bridle.
  - Vertically reciprocating polished rod moving down into the wellhead stuffing box.
  - Dual counterweighted rotating crank wheels ($360^\circ$ rotation) driven by the prime mover motor.
  - Dynamic pitman connecting arms linking the cranks to the beam equalizer.
- **Downhole Prospecting & Extraction**:
  - Automatically scans a **10-block horizontal radius** and up to **50 blocks vertically downward** beneath the wellhead for crude oil source blocks (`refinery:crude_oil`).
  - Prioritizes the vertical borehole directly beneath the wellhead first before radially searching outward concentric rings.
- **Power Consumption & Extraction Speeds**:
  - Requires Forge Energy (FE) supplied to the rear motor / electrical control panel (10,000 FE internal buffer):
    - **1 FE/t**: Low speed extraction $\rightarrow$ **100 mB/min**.
    - **2 FE/t**: Medium speed extraction $\rightarrow$ **500 mB/min**.
    - **4 FE/t**: Maximum full speed extraction $\rightarrow$ **1,000 mB/min**.
- **Automated Pipe & Fluid Export**:
  - Holds up to 16,000 mB of crude oil internally.
  - Front wellhead connects to liquid pipes and automatically pushes crude oil into adjacent pipes, tanks, or the Refinery input port.
  - Right-clicking with an empty bucket extracts a crude oil bucket.
  - Right-clicking with an empty hand displays current crude oil volume, stored energy, and extraction speed in the action bar.

### 3-Stage Petroleum Refinery (3×9×3)
A towering 3-stage fractional distillation column divided into three **3×3×3** vertical stages (81 blocks total) that refines **1,000 mB Crude Oil** per batch into **Liquid Tar**, **Diesel**, **Gasoline**, and **Bitumen**.

- **Dimensions**: 3 wide × 9 tall × 3 deep (three 3×3×3 stages stacked vertically):
  - **Bottom Third — Stage 1 (`Y = 0..2`, 3×3×3)**: Heavy Residue / Liquid Tar & Bitumen (`350–500 °C`).
  - **Middle Third — Stage 2 (`Y = 3..5`, 3×3×3)**: Diesel Fuel fractionating zone (`180–350 °C`).
  - **Top Third — Stage 3 (`Y = 6..8`, 3×3×3)**: Gasoline / Petrol & overhead condenser dome (`40–110 °C`).
- **Heat Requirement**: Requires an external **Refinery Furnace** (`>= 350 °C`) placed adjacent to Stage 1 (`Y = 0..2`).
- **Configurable Stage Yields (per 1,000 mB Crude Oil)**:
  Players can independently enable or disable Stage 1, Stage 2, and Stage 3 in the Refinery GUI:
  - **Stage 1 Only**: `500 mB` Liquid Tar + `1` Bitumen (100%).
  - **Stage 2 Only (Stage 1 & 3 Disabled)**: `500 mB` Diesel.
  - **Stage 3 Only (Stage 1 & 2 Disabled)**: `500 mB` Gasoline.
  - **Stage 1 + Stage 2**: `250 mB` Liquid Tar + `250 mB` Diesel + `1` Bitumen (100%).
  - **Stage 1 + Stage 3**: `250 mB` Liquid Tar + `250 mB` Gasoline + `1` Bitumen (100%).
  - **Stage 2 + Stage 3 (Stage 1 Disabled)**: `250 mB` Diesel + `250 mB` Gasoline.
  - **All 3 Stages Enabled (Stage 1 + 2 + 3)**: `250 mB` Liquid Tar + `250 mB` Diesel + `250 mB` Gasoline + **25% chance at `1` Bitumen**.
- **Byproduct Port Active Ejection & Conveyor Automation**:
  - The Byproduct Port (`refinery:byproduct_port`) automatically detects adjacent item conveyors (`refinery:conveyor_belt`, `sloped_conveyor_belt`, `right_angle_conveyor_belt`) and item pipes (`IndustrialPipeBlockEntity`, hoppers, chests, or item handlers) placed **below** (`pos.below()`) or **beside** the port (`pos.relative(NORTH/SOUTH/EAST/WEST)` and 1-block below).
  - Every 4 ticks, the port actively ejects Bitumen (and solid byproducts) from the controller's byproduct slots directly onto the conveyor belt surface (or inserts into the pipe/container), complete with anti-clogging belt space checks and mechanical pickup audio feedback.
- **South-Facing 3-Stage Takeoff Valve Piping**: Each of the 3 stages features a dedicated horizontal takeoff valve assembly pointing South (+Z) towards the output ports: Stage 1 (`Y = 22.0f`, Liquid Tar), Stage 2 (`Y = 60.0f`, Diesel), and Stage 3 (`Y = 115.0f`, Gasoline) with color-coded stage status indicator handwheels.

### Refinery Furnaces: Burner & Electric Heat Sources (1×2×1)
To operate the 3-Stage Refinery Tower, place either furnace directly adjacent to the outside of Stage 1:
- **Burner Refinery Furnace (`refinery:burner_refinery_furnace`)**: Accepts burnable solid fuels and fuel buckets across 3 fuel slots with a **4× rapid burn rate** to heat the serpentine crude-oil coil up to `500 °C`.
- **Electric Refinery Furnace (`refinery:electric_refinery_furnace`)**: High-voltage RF heater (`500,000 FE` buffer, consuming `500 FE/t`) that heats the tower cleanly using Forge Energy.
- **2-Block Placement, Single-Item Drop**: Each furnace is placed from a single item and occupies a `1×2×1` vertical footprint (`LOWER` and `UPPER`), dropping exactly 1 item when destroyed from either block.

#### Refinery Layer Schematic
- **Stage 1 (`Y = 0..2`, 3×3×3)**:
  - `Y = 0`: Controller front center `(0, 0, 0)`, Crude Oil Port `(-1, 0, 1)`, Combustion Chamber `(0, 0, 1)`, Liquid Tar / Byproduct Port `(1, 0, 1)`, Reinforced Casings on remaining perimeter.
  - `Y = 1`: Heat Exchangers at `(-1, 1, 1)` and `(1, 1, 1)`, Distillation Column at `(0, 1, 1)`, Reinforced Casings around perimeter.
  - `Y = 2`: Distillation Column at `(0, 2, 1)`, Reinforced Casings around perimeter.
- **Stage 2 (`Y = 3..5`, 3×3×3)**:
  - `Y = 3`: Diesel Port at `(0, 3, 2)`, Heat Exchangers at `(-1, 3, 1)` and `(1, 3, 1)`, Distillation Column at `(0, 3, 1)`, Reinforced Casings around perimeter.
  - `Y = 4..5`: Distillation Column at `(0, y, 1)`, Reinforced Casings around perimeter.
- **Stage 3 (`Y = 6..8`, 3×3×3)**:
  - `Y = 6`: Gas Port at `(0, 6, 2)`, Heat Exchangers at `(-1, 6, 1)` and `(1, 6, 1)`, Distillation Column at `(0, 6, 1)`, Reinforced Casings around perimeter.
  - `Y = 7..8`: Distillation Column at `(0, y, 1)`, Reinforced Casings around perimeter.

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
  - **Inclined Output Discharge Conveyor & Automatic Export**: Angled right-side discharge conveyor with an empty chevron belt that dynamically displays the actual crushed output material climbing the belt as products are unloaded. **Direct Auto-Export**: The grinder automatically ejects finished crushed items directly onto any conveyor belt snapped to the output chute or adjacent positions (as well as adjacent chests and item capabilities) every 4 ticks without requiring hoppers or extract servos!
  - **Member Block Occlusion**: Surrounding member blocks automatically turn invisible without culling adjacent floor faces and forward wrench/hand interactions directly to the controller.
- **Custom Industrial GUI**: Dark gunmetal steel panel with 2-slot raw feed tray, 4-slot crushed output collection bay, central animated crushing chamber indicator, and real-time status header badge (`ONLINE`, `CRUSHING`, `IDLE`, `UNFORMED`).
- **Core Functionality**:
  - **Ore Doubling**: Pulverizes Raw Iron, Raw Gold, and Raw Copper (or ore blocks) into **2x Dust** (`refinery:iron_dust`, `gold_dust`, `copper_dust`). Smelting these dusts produces 2 ingots per raw ore!
  - **Stone Crushing**: Crushes Stone, Cobblestone, Deepslate, Andesite, Diorite, and Granite into **1x Gravel**.
  - **Sand Pulverizing**: Crushes Gravel into **1x Sand**.
  - **Ingot Grinding**: Converts Ingots into **1x Dust** for metallurgy and chemistry.
- **Specs**:
  - Energy Storage: 500,000 FE
  - Energy Consumption: 2,000 FE/t while grinding
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

### Heavy Pit Quarry Excavator (3×3×3)
A heavy industrial open-pit continuous surface mining crawler rig engineered for automated geological excavation, surface bench clearing, and deep pit mining.

- **Monolithic 3D Excavator Architecture**: Modeled after heavy open-pit surface mining crawler machines, featuring:
  - **Crawler Track Undercarriage & Plinths**: Dual heavy steel continuous crawler track assemblies with 4 corner hydraulic stabilizer outrigger pads.
  - **Slewing Turntable & Machinery Deck**: Center rotating ring with diamond treadplate deck, heavy electric winch drum wound with braided steel wire cable, drive motor, and hydraulic power pack.
  - **Reinforced Operator Cabin**: Front-left elevated climate-controlled operator cabin with panoramic tinted safety glass, twin joystick consoles, and roof sunshade awning.
  - **Structural Lattice A-Frame Gantry Tower**: 4 vertical lattice columns rising to Y=46 with crown sheaves, boom pulleys, and tension stays.
  - **High-Intensity Work Lighting**: Dual high-intensity halogen work floodlights mounted to the upper gantry aiming forward and downward into the pit, providing full-bright optic lenses and emitting in-world **light level 15** while operating.
  - **Warning Amber Hazard Strobe**: Active rotating amber strobe beacon mounted on the cabin roof.
  - **Counterweight Ballast**: Rear ballast box with 45-degree yellow and black safety hazard stripes.
- **3-Phase Automated Excavation Sequence**:
  - **Phase 0: Surface Overburden Clearing ($43\times 43\times 3$)**: Mines an expansive $43\times 43\times 3$ box directly in front of the machine ($y = 0, -1, -2$), excavating a wide 3-block-deep recessed terrace bench.
  - **Phase 1: Automated Safety Perimeter Fencing**: Once the $43\times 43\times 3$ clearing is completed, the quarry automatically lines the outside perimeter of the inner $41\times 41$ pit with safety railings (using fences supplied in the dedicated GUI fence slot, or crafting durable safety fences automatically from quarried stone).
  - **Phase 2: Deep Pit Excavation to Bedrock**: Excavates the inner $41\times 41$ pit layer-by-layer downward from $y = -3$ all the way to bedrock (`level.getMinY()`). Fluid pockets (water and lava) are safely cleared, ores and minerals are harvested with diamond-pickaxe loot context, and unbreakable bedrock is safely preserved.
- **3 Low-Profile Conveyor Export Chutes**:
  - Features 3 extended industrial discharge arms—**Rear** `(0, 0, 3)`, **Left** `(-2, 0, 1)`, and **Right** `(2, 0, 1)`.
  - Built at standard 5px conveyor bed height (`y: 0..5`), lining up flush with our mod's `ConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `SlopedConveyorBeltBlock`.
  - Automatically pushes mined aggregates and ores in a round-robin load-balanced cycle directly onto adjacent conveyor belts, into item pipes, or into connected storage containers (Chests, Barrels, Hoppers).
  - 9 internal buffer slots safely store output if downstream lines are backed up.
- **Energy Consumption & Proportional Speed Scaling**:
  - **Max Speed**: 1 block per second (20 ticks per block) requiring strictly **20 FE/tick** (400 FE total per block).
  - **Progressive Throttling**: The quarry automatically scales down speed proportionally as available energy drops below 20 FE/tick (10 FE/t = 50% speed, 5 FE/t = 25% speed), cleanly pausing when 0 FE is available.
  - Internal Energy Storage: 50,000 FE.
- **Interactive Control Terminal GUI**:
  - **Radius Controls**: `[-]` and `[+]` buttons allowing the clearing radius to be adjusted down from 21 ($43\times 43$) down to 3 ($7\times 7$), with live box and pit dimension readouts.
  - **Remaining Blocks Breakdown Dashboard**: Real-time monitor displaying total remaining blocks and a paginated, item-icon list showing how many of each individual block type remains to be mined (Stone, Deepslate, Coal Ore, Iron Ore, Copper Ore, Gold Ore, Diamond Ore, etc.).
  - **Dedicated Fence Supply Slot**: Slot 9 accepts custom player fence blocks or iron bars.

#### Multiblock Construction (Only 2 Blocks Required)
Only 2 craftable blocks are needed to build the 3×3×3 Heavy Pit Quarry:
1. **Heavy Pit Quarry Controller** (1x at `(0, 0, 0)`)
2. **Quarry Structural Casing** (26x)

> [!NOTE]
> The multiblock structure is simple: 1 controller at the front-center and 26 casings forming the remainder of the 3×3×3 cube. There are no separate conveyor port, turntable, or lattice frame blocks needed.

#### Layer Schematic (Local Coordinates)
- **Layer 0 (Foundation Skid & Operator Station)**:
  - `(0, 0, 0)`: **Heavy Pit Quarry Controller** (Bottom Center Front, operator console facing forward away from the excavation pit behind)
  - Remaining 8 positions: **Quarry Structural Casing**
- **Layer 1 (Machinery Deck)**:
  - All 9 positions: **Quarry Structural Casing**
- **Layer 2 (Gantry & Tower)**:
  - All 9 positions: **Quarry Structural Casing**

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

### Multiblock Liquid Storage Tanks
| Block | Recipe / Ingredients | Output |
| :--- | :--- | :--- |
| **Storage Tank Casing** | 4 Steel Plates + 4 Glass + 1 Iron Block | **4x Storage Tank Casing** |
| **Storage Tank Drain Valve** | 4 Steel Plates + 3 Iron Bars + 1 Storage Tank Casing + 1 Hopper | **1x Storage Tank Drain Valve** |
| **Storage Tank Fill Valve** | 4 Steel Plates + 3 Iron Bars + 1 Storage Tank Casing + 1 Hopper | **1x Storage Tank Fill Valve** |

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
  - **In-Place Rotation**: Right-click with the **Refinery Wrench** rotates the conveyor orientation clockwise; Sneak-Right-Click with the Wrench rotates counter-clockwise.
  - Sneak-Right-Click with an empty hand also rotates orientation clockwise.
  - Visual connection geometry instantly recalculates and joins with adjacent conveyors upon rotation.
- **Crafting Recipe**: 3 Petroleum Tar + 1 Redstone Dust + 5 Iron Ingots produce 4 Industrial Conveyor Belts.

### Sloped Conveyor Belt (`refinery:sloped_conveyor_belt`)
An inclined modular conveyor engineered to transport items vertically up or down elevations:
- **Inclined Motion & Physics**: Elevates or lowers items smoothly along a 1-block incline (1:1 vertical rise per horizontal block) with continuous velocity clamping and perpendicular alignment along the belt center.
- **Top & Bottom Terminal Handoff**: Automatically transitions items onto adjacent conveyors, chests, and machine inventories positioned at either the upper or lower terminal elevation.
- **Container & Capability Support**: Implements `Container` and NeoForge `Item` capabilities, allowing hoppers, auto-harvesters, droppers, and the Industrial Grinder to feed items directly onto the incline.
- **Monolithic 3D Incline Model**: Features inclined extruded aluminum side rails, galvanized steel slider deck, vertical structural stanchion legs, dual steel rollers, and animated scrolling vulcanized belt top.
- **Controls & Wrench Configuration**:
  - Right-click with a Wrench or Sneak-Right-Click with an empty hand to rotate facing clockwise.
  - Sneak-Right-Click with a Wrench to toggle elevation incline between `UP` (climbing) and `DOWN` (descending).
- **Crafting Recipe**: 1 Industrial Conveyor Belt + 1 Iron Ingot placed diagonally produces 1 Sloped Conveyor Belt.

### Right-Angle Conveyor Belt (`refinery:right_angle_conveyor_belt`)
A specialized 90-degree corner conveyor designed for seamless turn routing in factory layouts:
- **Two-Phase Corner Routing**: Smoothly accepts incoming items along the entry axis, guides them toward the turntable center, and vectors velocity 90 degrees outward toward the exit terminal with zero spills or dead stops.
- **Exit Handoff**: Injects items directly into adjacent downstream conveyors or machine inventories at the corner exit.
- **Monolithic 3D Corner Model**: Includes L-shaped outer aluminum guide rails, corner support bracket, entry and exit steel rollers, and an 8-segment curved animated conveyor belt surface that wraps around the 90-degree turn.
- **Controls & Wrench Configuration**:
  - Right-click with a Wrench or Sneak-Right-Click with an empty hand to rotate entry facing clockwise.
  - Sneak-Right-Click with a Wrench to toggle corner turn direction between `RIGHT` (clockwise) and `LEFT` (counter-clockwise).
- **Crafting Recipe**: 3 Industrial Conveyor Belts arranged in an "L" pattern produce 3 Right-Angle Conveyor Belts.

### Glass-Fused-to-Steel (GFS) Multiblock Liquid Storage Tanks
Engineered for bulk storage of any liquid resource, modeled after real-world Glass-Fused-to-Steel (GFS) enamel bolted industrial storage tanks.

- **Six Scalable Multiblock Geometries**:
  - `3x3x3`: **64,000 mB** (64 Buckets)
  - `3x3x6`: **256,000 mB** (256 Buckets)
  - `3x3x9`: **512,000 mB** (512 Buckets)
  - `5x5x5`: **1,000,000 mB** (1,000 Buckets)
  - `5x5x7`: **2,000,000 mB** (2,000 Buckets)
  - `5x5x9`: **4,000,000 mB** (4,000 Buckets)
- **Universal Liquid Compatibility**:
  - Stores any fluid (Water, Lava, Crude Oil, Diesel, Gasoline, High Pressure Steam, Ethanol, Molten Metals) with transactional rollback safety.
- **Top Fill Valve & Bottom Drain Valve**:
  - **Fill Valve (`refinery:storage_tank_fill_valve`)**: Top-center block. Accepts inputs from fluid pipes, pumps, or buckets and directs them into the central reservoir.
  - **Drain Valve (`refinery:storage_tank_drain_valve`)**: Bottom-center block. Multiblock master controller managing structure formation, fluid storage, and active drainage.
- **No Extract Pipe Needed (Logistics & Pipe Auto-Push)**:
  - The drain valve actively auto-pushes stored liquid downward into pipes laid beneath the tank floor, as well as horizontally into adjacent pipes.
  - Attaching regular liquid pipes directly receives fluid flow without requiring an extract pipe or external pump.
- **Dedicated Industrial GUI & Sight Glass Gauge**:
  - Custom interface with fill and empty bucket slots.
  - Vertical illuminated sight glass gauge displaying real-time liquid level percentage, fluid title, and formatted mB volume.
  - "Dump / Flush" button to purge residual fluids when converting the tank to a different liquid.
- **Monolithic 3D Render & Holographic Blueprint**:
  - High-gloss enamel bolted shell panels, concrete foundation curb, circumferential galvanized wind stiffener rings, caged safety climbing ladder, intermediate rest landing platform, and roof perimeter handrails.
  - Right-click the drain valve with the **Refinery Wrench** to activate the 3D building guide hologram. `Shift-Right-Click` cycles between the 6 supported tank sizes.

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
- `Redstone Pulse (1s)`: Pulses redstone on and off every 1 second (20 ticks, ideal for standard clocks).
- `Redstone Pulse (0.5s)`: Rapidly pulses redstone on and off every 0.5 seconds (10 ticks, ideal for high-frequency clocking and rapid circuits).
- `Autarchic Pulse (Item)`: Extracts 1 item from adjacent container into pipe.
- `Autarchic Pulse (Item x5)` / `Autarchic Pulse (Item x10)`: Same, but extracts 5 or 10 items per pulse.
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
- **Autarchic Chipset (4x per craft)**:
  - `Ender Pearl` (center) + 2x `Redstone Dust` + 2x `Gold Nugget` $\rightarrow$ 4x Autarchic Chipset.
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
|  Electric Arc Melter (3x3x3)  |  <--- 10,000 FE/t, Electrodes & Arc Flash Animation
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
  - 1x **Electric Arc Melter Controller** (`refinery:arc_melter_controller`) — front face, center-bottom.
  - 22x **Arc Melter Casing** (`refinery:melter_casing`) — heavy heat-dissipating refractory outer framing.
  - 2x **Arc Melter Heating Chamber** (`refinery:melter_chamber`) — tinted safety glass viewports.
  - 1x **Arc Melter Input Port** (`refinery:melter_input_port`) — top or side funnel hopper. Accepts dusts & raw ores from hoppers and item pipes.
  - 1x **Arc Melter Output Port** (`refinery:melter_output_port`) — bottom-front right molten spout. Automatically pushes molten liquid to adjacent Casting Channels, fluid pipes, or tanks!
- **Hologram Support**: Right-click the Controller with a **Refinery Wrench** to project the full color-coded 3D holographic blueprint with exact JEI item labels.

#### Operation & Monolithic 3D Animated Model (`ArcMelterModel`):
- **Power Consumption**: 10,000 FE/t while melting (2,000,000 FE internal buffer, starts melting once the buffer reaches **50,000 FE**).
- **Process Speed**: 200 ticks (10 seconds) per item.
- **Fluid Yield**: 1 Ore / Raw Ore $\rightarrow$ 2,000 mB (2x doubling); 1 Ingot / Dust / Sand $\rightarrow$ 1,000 mB of Molten Fluid.
- **Internal Tank**: 8,000 mB capacity with automatic molten fluid push into adjacent Open Casting Channels, fluid pipes, or tanks.
- **Monolithic 3D EAF Architecture (`arc_melter.png` 256×256 Atlas)**:
  - Heavy steel rocker trunnion foundation skid, cylindrical water-cooled ribbed furnace shell with vertical buckstay stiffeners, interior magnesia-carbon refractory hearth, front armored quartz slag/observation door at `(0, 1, 0)`, eccentric bottom-tapping (EBT) refractory launder spout at `(1, 0, 0)` aligning with Open Casting Channels, rear-left high-voltage EAF transformer vault with oil radiator fins and 3 copper busbar tubes, twin hydraulic electrode mast columns, domed water-cooled roof with delta refractory insert, top charge hopper at `(0, 2, 1)`, and front-left operator console.
- **Dynamic Animations**:
  - 3 reciprocating graphite carbon electrodes in a delta triad held by water-cooled copper clamp arms that oscillate vertically and discharge 3D jagged full-bright electric arc plasma bolts (`15728880` light) into the crucible bath.
  - Rising and falling full-bright molten bath surface and flowing launder tapping stream dynamically tinted to match the active molten fluid (`molten_iron`, `molten_gold`, `molten_copper`, `molten_tin`, `molten_bronze`, `molten_glass`).
  - Loaded input ore/dust/ingot visibly sinks into the top charge hopper (`(0, 2, 1)`), and items dropped into the top funnel from conveyors or droppers are automatically absorbed into the input slots.

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
  - 1x **Casting Press Controller** (`refinery:casting_press_controller`) — front face, center-bottom.
  - 22x **Casting Press Casing** (`refinery:casting_casing`) — heavy hydraulic frame with safety hazard striping.
  - 1x **Casting Mold Basin** (`refinery:casting_basin`) — ceramic mold bed at the base `(0, 0, 1)` where liquid pools.
  - 1x **Casting Safety Chamber** (`refinery:casting_chamber`) — wire-reinforced safety viewing window at `(0, 1, 0)`.
  - 1x **Casting Input Port** (`refinery:casting_input_port`) — ground-level rear-center intake at `(0, 0, 2)` (swapped to Layer 0 so ground-level `Y=0` Open Casting Channels and fluid pipes connect directly without elevation changes).
  - 1x **Casting Output Port** (`refinery:casting_output_port`) — bottom-front right exit conveyor chute at `(1, 0, 0)`. Automatically ejects finished ingots and blocks onto conveyors, hoppers, chests, or transport pipes!

#### Operation & Monolithic 3D Animated Model (`CastingPressModel`):
- **Power Consumption**: 600 FE/t while stamping (500,000 FE internal buffer).
- **Process Speed**: 400 ticks (20 seconds) per casting & quenching cycle.
- **Input Requirement (1:1 Fluid Ratio)**: **1,000 mB of Molten Fluid** AND **1,000 mB of Water** per ingot/block (4,000 mB Molten Basin capacity + 4,000 mB Quenching Water Tank capacity).
- **Monolithic 3D Hydraulic Caster Architecture (`casting_press.png` 256×256 Atlas)**:
  - Reinforced steel foundation bed, central water-cooled copper & refractory crystallizer mold basin at `(0, 0, 1)`, 4 precision-ground chrome tie-rod columns with castle locknuts, overhead cast-steel crown platen with crimson-amber main hydraulic cylinder and twin return boosters, front safety grille observation window at `(0, 1, 0)`, upper refractory tundish & sloped launder chute at `(0, 1, 2)` plus lower `Y=0` refractory channel receiver troughs at `(0, 0, 2)` and `(-1, 0, 1)`, right-side water-cooling manifold cabinet, motorized steel run-out roller table at `(1, 0, 0)`, and front-left PLC operator cabinet.
- **Dynamic Animations**:
  - Reciprocating hydraulic ram crosshead (with diagonal hazard stripes, bronze guide bushings sliding on the 4 chrome tie-rods, chrome piston rods, and water-cooled copper upper stamping die) that accelerates downward (`0..90` ticks), holds high-pressure quench compression (`90..150` ticks), and retracts smoothly (`150..240` ticks).
  - Dynamic molten pool to incandescent solidified billet transition inside the mold cavity and animated finished ingots/blocks traveling out along the right-hand motorized run-out roller table (`(1, 0, 0)`).
- **Auto-Ejection & Full Member Capability Forwarding**: Automatically exports finished ingots and glass blocks onto adjacent conveyors, item pipes, or containers at `(1, 0, 0)`, and accepts molten fluid from Open Casting Channels or fluid pipes connected to the Input Port or any assembled casing/basin block.

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

### Version 1.8.18 (World Loading Fix & Brass Pipe Recipe Correction)
- **World Load / Save Crash (`refinery:brass_ingot` Unknown Registry Key)**:
  - Fixed a critical datapack recipe parsing exception that prevented Minecraft from loading worlds/saves (`Registry Loading / Failed to load level data or datapacks`).
  - Resolved invalid `refinery:brass_ingot` item reference in `data/refinery/recipe/brass_item_pipe.json`. Brass Item Pipe is now craftable with `minecraft:gold_ingot` (matching Logistics Automation golden transport pipe crafting) as well as `refinery:bronze_ingot`.

### Version 1.8.17 (Auto Harvester Directional Front-Only Harvest Geometry)
- **Auto Harvester Directional Front-Only Harvest Geometry**:
  - Re-architected `AutoHarvesterBlockEntity` scanning coordinate mathematics to orient strictly in front of the machine's horizontal facing direction (`facing`).
  - Completely eliminated backward scanning: the harvester now scans forward distance `1 .. radius` directly in front of itself (`pos.relative(facing, forwardDist).relative(rightDir, lateralDist).above(dy)`), completely preventing the machine from ever harvesting crops, trees, or blocks behind it or directly underneath its housing.
  - The working range setting ($X$) directly controls the forward reach (1 to 9 blocks deep in front of the machine), centered laterally across $(2X + 1)$ blocks.
  - Updated `AutoHarvesterScreen` GUI label and tooltips to clearly communicate forward depth and front-only coverage (e.g. `11x5 in front`), removing confusing 360-degree radius descriptions.

### Version 1.8.16 (Auto Planter & Harvester GUI Controls & Network Sync Fix)
- **Auto Planter & Auto Harvester GUI Button Controls & State Sync**:
  - Resolved an issue where clicking the Power ON/OFF toggle and the working range radius `[-]` / `[+]` buttons in both the Auto Planter and Auto Harvester GUIs failed to respond or update the working parameters.
  - Fixed client/server menu button dispatch in `AutoPlanterMenu` and `AutoHarvesterMenu`: `clickMenuButton` now immediately updates `this.data` slots (slot 4 for enabled toggle, slot 5 for radius), ensuring client-side GUI state and server state remain synchronized whether the container is a client `SimpleContainer` or a server block entity.
  - Added immediate optimistic client-side UI feedback in `AutoPlanterScreen` and `AutoHarvesterScreen` mouse click handlers so button state, LED indicators, and radius dimensions update instantly upon clicking.
  - Implemented block entity network packet synchronization (`syncToClient()`, `getUpdatePacket()`, and `getUpdateTag()`) in `AutoPlanterBlockEntity` and `AutoHarvesterBlockEntity`, ensuring block entity state changes sync immediately across all connected clients.
  - Added inventory clearance (`this.items.clear()`) prior to loading items from NBT in `loadAdditional` to prevent ghost item retention.

### Version 1.8.15 (Industrial Grinder Empty Inventory Rendering & Sync Fix)
- **Industrial Grinder (Rock Crusher) Empty Inventory Item Rendering**:
  - Fixed an issue where the Industrial Grinder continued rendering 3D item models of the last crushed item between its spinning hopper blades even when the input inventory was completely empty.
  - Added explicit container clearing (`this.items.clear()`) in `IndustrialGrinderControllerBlockEntity.loadAdditional` prior to loading NBT items, preventing stale items from lingering on the client when slots are emptied on the server.
  - Overrode `setChanged()` to immediately synchronize block entity data packets to tracking clients whenever items are added or removed through player GUI interaction, hopper transfer, or automation piping.
  - Updated `IndustrialGrinderControllerRenderer` to explicitly clear active and output item render states when slots are empty or unpopulated.
  - Applied the same inventory load clearance fix to the Metal Press and Multi-Smelter controllers to eliminate ghost item rendering across all industrial multiblock machinery.

### Version 1.8.14 (Pipe Blank Gates, 3D Curved Saddle Gates & Logistics Recipe Parity)
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

### Version 1.8.13 (Native 3D Pipe Texture Flickering & Z-Fighting Resolution)
- **Eliminated All Texture Flickering and Z-Fighting on Native 3D Monolithic Pipes**:
  - Switched renderer pipeline to `RenderTypes.entitySolid`, providing true solid depth-buffer writing and eliminating alpha-test discard flickering.
  - Eliminated internal surface collision between cylinder arms and junction hub:
    - Arm geometry now terminates at `0.5 ± 0.08`, cleanly sealed inside the spherical hub (`R = 0.228`) without penetrating through the center or colliding with perpendicular arms.
    - Repaired vertical (Y-axis) cylinder vertex ordering, eliminating sheared/twisted non-planar quad artifacts.
    - Standalone (unconnected) pipes now render solely as solid capped cylinders without redundant center hub rendering.
    - Corrected polar quads on spherical fitting hubs to eliminate degenerate triangles and UV distortion.
    - Machine terminal flanges inset by `0.001` to prevent coplanar face collision with adjacent machine blocks.

### Version 1.8.12 (Native Industrial 3D Monolithic Pipe System)
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

### Version 1.8.11 (True Round 3D Monolithic Pipe Geometry & Seamless Runs)
- **True Round 3D Monolithic Pipe Geometry (`Logistics Automation`)**:
  - Replaced legacy cubic box meshes with true 12-sided faceted cylindrical pipe geometry (`PipeGeometry.java`) matching the monolithic 3D machine aesthetic.
  - **Seamless Straight Pipe Runs**: Pipe-to-pipe arm connections (`ARM`) and core junctions now share identical continuous cylindrical profiles (radius 3.2 units) with zero collar breaks, zero block-boundary seams, and zero dark holes. Consecutive pipes merge into a continuous, smooth industrial tube.
  - **Industrial Machine Connection Flanges**: Connections to external inventories, machines, tanks, and boilers (`ARM_EXTENDED`) feature authentic heavy bolted connection flanges (Fitting 11 Flange) with raised collar rims, machined hex bolts, and recessed gasket seals.
  - **Multi-Axial Junction Core (`CORE`)**: Central junction seamlessly connects straight runs, smooth 90° elbows, and tee branches without square corners or protruding box faces.
  - **Seamless Textures & LabPBR 1.3 Updates**: Longitudinal brushed metal texture wrapping around the cylindrical surface with physically accurate diffuse and specular cylindrical lighting. Intelligent logistics pipes feature flush illuminated status LEDs.
  - **Automated Baking & Mod Jar Deployment**: Fully integrated into `scripts/generate_pipe_texture_pack.ps1` with Java 25 compilation directly updating the mod jar and resource pack.

### Version 1.8.10 (Logistics Automation Ultra-Realistic 3D Monolithic Pipe Resource Pack)
- **Ultra-Realistic 3D Monolithic Pipe Visual Overhaul (`Logistics Automation`)**:
  - High-definition 64x64 procedural PBR texture pack and 3D geometric block model suite designed for Logistics Automation (`logistics` namespace pipes).
  - **Photorealistic PBR Materials**: Features brushed metal anisotropic grain, directional ambient occlusion chamfers, machined hex socket-head bolts, and crystalline tempered viewports preserving live item/liquid transit visibility.
  - **Full LabPBR 1.3 Shader Compliance**: Includes baked normal displacement maps (`_n.png`) and specular/roughness/metallic maps (`_s.png`) for real-time parallax depth and lighting reflections with modern shader packs (Iris, Oculus, Complementary, BSL, SEUS).
  - **Extruded 3D Monolithic Block Geometry**: Enhanced core manifold junction hubs with 12-sided corner stanchions, physical connection flanges with mounting bolt studs, and mid-segment clamp sleeves.
  - **Standalone Downloads & Compatibility**: Kept strictly as separate standalone resource pack downloads (not baked into the mod jar) with forward-slash zip entries: `resourcepacks/Logistics-UltraRealistic-3D-Pipes.zip` (Universal), `Logistics-UltraRealistic-3D-Pipes-MC1.21.1.zip` (MC 1.21.1), and `Logistics-UltraRealistic-3D-Pipes-MC1.20.1.zip` (MC 1.20.1).

### Version 1.8.9 (Conveyor In-Place Rotation, Sloped & Right-Angle Conveyors, Crusher Auto-Export)
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

### Version 1.8.8 (Steam Turbine & Boiler Fluid Auto-Transfer)
- **Active Fluid Transfer to Pipes (No Extraction Servos / Powered Pipes Needed)**:
  - Added active auto-push fluid transfers to adjacent regular liquid pipes and fluid handlers for all steam boilers and the steam turbine, matching the liquid storage tank drainage behavior.
  - **Steam Boilers (Small & Large, Solid-Fuel & Diesel)**:
    - Dedicated Steam Output Ports (`boiler_steam_port`) and Boiler Controllers now actively push produced Steam (up to 1,000 mB/t) directly into connected pipes or tanks.
  - **Steam Turbine Generator**:
    - Dedicated Turbine Water Output Ports (`turbine_water_port`) and Turbine Controllers now actively push condensed Water (up to 1,000 mB/t) directly into connected pipes or tanks.
  - Streamlines closed-loop Rankine thermodynamic power plants: run simple liquid pipes between boiler steam outputs and turbine steam inputs, and return water lines straight into boiler water inputs with zero power or servos required!

### Version 1.8.7 (Blast Furnace Input Restrictions & Fluid Discharge Drop Fix)
- **Industrial Fluid Discharge Survival Drop Fix**:
  - Added the missing block loot table definitions (`data/refinery/loot_table/blocks/fluid_discharge.json` and `loot_tables/blocks/fluid_discharge.json`) so breaking or mining the Industrial Fluid Discharge block in survival returns the item properly.
- **Storage Tank Item Icons & Held Models**:
  - Added missing item definitions for `storage_tank_casing`, `storage_tank_drain_valve`, and `storage_tank_fill_valve`, restoring crisp GUI inventory icons and 3D held models in player hands.
- **Blast Furnace Input Restrictions**:
  - Iron input slots and automation ports now strictly accept ONLY **Iron Ingot** (`minecraft:iron_ingot`) and **Block of Iron** (`minecraft:iron_block`). Raw iron, iron ores, and dusts are rejected.
  - Fuel input slots and automation ports now strictly accept ONLY **Coal** (`minecraft:coal`) and **Block of Coal** (`minecraft:coal_block`). Charcoal, coal dust, petroleum tar, bitumen, and other burnables are rejected.
  - Implemented unit conversion: 1 Block of Iron provides 9 iron units (yielding 9 steel ingots); 1 Block of Coal provides 9 fuel units (fueling 9 smelting operations).
  - In-progress iron units and fuel units are persistently stored in NBT and safely drop as items upon dismantling the controller.
  - Updated GUI validation and tooltips to clearly reflect the Iron Ingot/Block and Coal/Block requirements.

### Version 1.8.6 (Storage Tank Ladder & Visual Clean-up)
- **Sleek Industrial Access Ladder Redesign**:
  - Replaced the bulky, chunky caged ladder with a clean, authentic industrial steel access ladder mounted directly up the North face of the tank.
  - Features twin vertical stringer rails, standoff wall brackets connecting to the tank shell, distinct individual horizontal climbing rungs spaced every 3.5 units with hollow air between them (exposing the dark enamel tank panels behind), and smooth top walkthrough grab bars stepping onto the roof rim.
  - Assembled storage tank casing blocks in the North ladder column (`relX == 0, relZ == -radius`) now implement `isLadder = true`, allowing players to physically climb the ladder to the roof of the tank.
  - Updated all ladder stringers, rungs, and roof handrails to use a crisp, bright galvanized industrial steel texture swatch (`#D8DFE8`), eliminating messy textures and distorted UVs.
  - Overhauled top roof guardrails to follow the octagonal profile of the tank with an open walkthrough entry at the ladder head.
- **Removed Exterior Red Standpipe**:
  - Completely removed the vertical red pipe and flanged valves running down the side of the tank model.

### Version 1.8.5 (Glass-Fused-to-Steel Multiblock Liquid Storage Tanks)
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

### Version 1.8.4 (Steam Turbine Monolithic 3D Model & High-Resolution Textures)
- **Monolithic 3D Steam Turbine Model & High-Resolution Textures**:
  - Replaced individual multiblock block models with a custom, high-fidelity monolithic 3D industrial steam turbine model based on heavy industrial turbine-generator skids (Elliott, Dresser-Rand, Siemens).
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

### Version 1.8.2 (Fluid Pump Wrench Linking & Pipe Backflow Prevention)
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

### Version 1.11.60 (Thermodynamic Rebalance: 500 & 1,500 mB/s Boilers, 1,450 mB/s Turbine, 3,000 RPM Scaling & Fluid Pipe Flow Fix)
- **Boiler & Steam Turbine Thermodynamic Balance Rebalancing (`SmallBoilerControllerBlockEntity`, `LargeBoilerControllerBlockEntity`, `SteamTurbineControllerBlockEntity`)**:
  - **Small Boilers**: Rebalanced solid fuel steam production to **500 mB/s** (25 mB/t, consuming 12 mB/t water).
  - **Large Boilers**: Rebalanced solid fuel steam production to **1,500 mB/s** (75 mB/t, consuming 37 mB/t water).
  - **Steam Turbine Steam Requirement**: Tuned steam intake to **1,450 mB/s** (72.5 mB/t average) at peak generation (10,000 FE/t). 1 Large Boiler or 3 Small Boilers (1,500 mB/s) provide a steady 50 mB/s surplus to keep the 32,000 mB steam buffer saturated.
- **Dynamic Turbine RPM Scaling & Centered Diagnostic Readout (`SteamTurbineScreen`, `SteamTurbineControllerBlockEntity`)**:
  - Turbine speed now dynamically scales with power generation up to **3,000 RPM at peak generation** (10,000 FE/t; 0 RPM when idle).
  - Diagnostic monitor now displays centered text showing exact live RPM (`3000 RPM`) and generation (`+10000 FE/t`).
  - Active turbine blade indicator animation speed and hum sound pitch dynamically increase with RPM.
- **Fluid Pipe Pipeline Flow Bottleneck & Directional Momentum Fix (`IndustrialPipeBlockEntity`, `RefineryMod`)**:
  - Fixed an issue where pipes equalized fluid using naive half-difference division (`(diff + 1) / 2`), halving throughput across every pipe block and throttling long pipelines down to ~24 mB/t.
  - Implemented directional flow momentum: pipes actively push fluid forward along the pipeline into downstream pipes and connected machine ports at the full material transfer rate (Iron: 500 mB/t, Steel: 1,500 mB/t) without distance attenuation or backward sloshing.
  - Steam turbine steam input port now immediately notifies the controller on fluid insertion for real-time sync.
- **Steam Turbine & Diesel Generator NeoForge Transaction Safety**:
  - Replaced manual state assignments in xtract(...) with NeoForge's standard onEnergyChanged(int previousAmount) handler on SimpleEnergyHandler.
  - Resolved a severe ghost energy drain bug where external simulated transactions (such as AWG cables, fluxducts, or wire networks probing for grid demand) temporarily decremented energy storage and desynchronized the controller's internal nergy field without rolling back when the simulation aborted.
  - Prevents generators from constantly bleeding generated FE into the void when connected to fully-charged energy storage banks or satisfied grids.

---

### Version 1.11.58 (In-Game Field Manual Boiler Guide Pages: Small & Large Steam and Diesel Boilers)
- **In-Game Field Manual Boiler Guide Pages (`GuideBookRegistry`)**:
  - Added dedicated, fully illustrated guide book pages with full multi-layer slice schematics, required block lists, and operational specifications for all four boiler multiblock types:
    - **Small Steam Boiler (3x3x3)**: 27-block compact solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 1 Firebox, 1 Pressure Pipe, 21 Steel Boiler Casings) producing up to 100 mB/t steam with 2 fuel slots.
    - **Large Heavy Steam Boiler (5x5x5)**: 125-block heavy-duty solid fuel boiler (1 Controller, 1 Water Port, 1 Fuel Port, 1 Steam Port, 9 Fireboxes, 27 Pressure Pipes, 85 Steel Boiler Casings) producing up to 300 mB/t steam with 8 fuel slots across 5 layer schematics.
    - **Small Diesel Boiler (3x3x3)**: 27-block liquid fuel boiler utilizing Diesel, Biodiesel, or Ethanol via fluid fuel port.
    - **Large Diesel Boiler (5x5x5)**: 125-block industrial liquid fuel boiler producing 300 mB/t steam with high-efficiency fluid burner combustion.

---

### Version 1.11.57 (Large Boiler 3D Model Block Hiding, Steam Turbine Dynamic Load Throttling & Diagnostic Monitor Sync)
- **Large Boiler & Diesel Boiler Multiblock Block Hiding (`LargeBoilerControllerBlock`, `LargeDieselBoilerControllerBlock`, `SmallDieselBoilerControllerBlock`, `SteelBoilerCasingBlock`)**:
  - Unified the `ASSEMBLED` blockstate boolean property across all boiler controllers and member blocks using `SmallBoilerControllerBlock.ASSEMBLED`.
  - Fixed an issue where the 124 member blocks (casing, pressure piping, fireboxes, and ports) did not hide upon assembly, obscuring the 5x5x5 3D monolithic boiler model.
  - Expanded `SteelBoilerCasingBlock.findNearbyController` search radius from 3 to 5 blocks to guarantee full 5x5x5 structure coverage when clicked or broken.
- **Steam Turbine Generator Diagnostic Monitor & Operational State (`SteamTurbineControllerBlockEntity`, `SteamTurbineMenu`, `SteamTurbineScreen`)**:
  - Fixed active state detection in ContainerData: the turbine now correctly shows `ONLINE / 1,800 RPM` and live generated power (e.g. `+10,000 FE/t` or throttled load output) instead of `STANDBY / 0 RPM / 0 FE/t`.
  - Fixed steam buffer tooltip typo (`300 mB/t` at peak load, previously stated `100 mB/t`).
  - Fixed rotor expansion tooltip state (`ACTIVE` vs `IDLE`).
- **Steam Turbine Generator Energy Sync & Dynamic Load Throttling (`SteamTurbineControllerBlockEntity`, `TurbinePortBlockEntity`)**:
  - Fixed `energyStorage.extract(...)` self-assignment bug so external energy extraction correctly synchronizes with the controller's energy level.
  - Removed duplicate auto-push logic between the controller BE and dedicated output ports.
  - Capped energy push rate to 10,000 FE/t and streamlined internal buffer to 100,000 FE, enabling prompt load-following throttling (e.g. at an 8,000 FE/t cable load, the turbine consumes 240 mB/t steam, allowing surplus steam from a Large Boiler to fill the 32,000 mB steam buffer).

---

### Version 1.11.54 (Multi-Smelter Plank Pyrolysis: Planks to Charcoal)
- **Multi-Smelter Plank Pyrolysis (Planks to Charcoal) (`MultiSmelterControllerBlockEntity`, `MultiSmelterMenu`, `GuideBookRegistry`)**:
  - Added support for converting any wooden planks (`#minecraft:planks`) directly into Charcoal (`minecraft:charcoal`) inside the 5x4x3 Heavy Multi-Smelter.
  - Multi-Smelter input slots accept wooden planks and smelt them in parallel at full speed (5 seconds per batch) using electric induction or solid fuel.
  - Shift-clicking in the Multi-Smelter GUI now prioritizes loading smeltable goods (planks, logs, ores) into input slots before overflowing to the combustion fuel slot.
  - Updated the in-game Field Manual Guide Book with the Plank Pyrolysis recipe details.

---

### Version 1.11.51 (Iron Item Pipe & Cast Iron Fluid Pipe Recipe Collision Fix)
- **Iron Item Pipe & Cast Iron Fluid Pipe Recipe Collision Fix (`cast_iron_fluid_pipe.json`)**:
  - Resolved recipe collision where Iron Item Pipes and Cast Iron Fluid Pipes shared the same shaped recipe (`iron_ingot + glass + iron_ingot`).
  - Restored Iron Item Pipe as the exclusive owner of the classic horizontal `iron_ingot + glass + iron_ingot` recipe.
  - Cast Iron Fluid Pipe now crafts using `iron_bars + glass + iron_bars` (`data/refinery/recipe/cast_iron_fluid_pipe.json`).
  - Added an alternative metallurgical recipe using Iron Ingots and Coal/Charcoal (`data/refinery/recipe/cast_iron_fluid_pipe_from_coal.json`).

---

### Version 1.11.49 (Industrial Diesel Generator Single-Item Drop Fix)
- **Industrial Diesel Generator Single-Item Drop Fix (`DieselGeneratorBlock`)**:
  - Resolved an issue where destroying a placed Industrial Diesel Generator (`refinery:diesel_generator`) in survival dropped two generator items.
  - Overrode `getDrops` in `DieselGeneratorBlock` so only the master `LEFT` block drops the single generator item (`refinery:diesel_generator`) while the slave `RIGHT` block strictly returns an empty list `List.of()`.
  - Updated `playerWillDestroy` to safely clear and drop container items, silently remove the partner block with flag 35 and break particles, and pop strictly 1 generator item only when a survival player breaks the non-master `RIGHT` block.
  - Added `data/refinery/loot_tables/blocks/diesel_generator.json` and ensured both `loot_table` and `loot_tables` enforce `part = left` state conditions.

---

### Version 1.11.47 (Autarchic Chipset 4x Recipe Output)
- **Autarchic Chipset Recipe Yield (4x Multiplier)**:
  - Increased the crafting output of the Autarchic Chipset recipe from 1 to 4 chipsets per craft (`data/refinery/recipe/autarchic_chipset.json`).
  - Standard crafting (1x Ender Pearl + 2x Redstone Dust + 2x Gold Nugget) now produces 4x Autarchic Chipsets, drastically speeding up gate upgrading and automation setups.

---

### Version 1.11.45 (Per-Tick Machine Energy Requirements, 4x Faster Casting Channel Flow, & Anti-Bounce-Back)
- **Per-Tick Machine Energy Requirements Across All Machines (`*ControllerBlockEntity`, `FermenterBlockEntity`)**:
  - Removed artificial whole-process and excessive buffer requirements across all machines (previously checking `(PROCESS_TIME - progress) * ENERGY_PER_TICK` or multi-thousand FE start thresholds).
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

### Version 1.11.44 (Arc Melter 50k FE Start Threshold, Ground-Level Casting Input Port, 1:1 Water Quenching & JEI Hologram Name Parity)
- **Continuous Casting Press Water Quenching Requirement (1:1 Ratio with Molten Metal) (`CastingPressControllerBlockEntity`, `CastingPressMenu`, `CastingPressScreen`, `RefineryMod`)**:
  - Added a dedicated **4,000 mB Quenching Water Tank** alongside the 4,000 mB Molten Metal Basin on the Continuous Casting Press.
  - Each casting and quenching cycle now requires a **1:1 fluid ratio**: **1,000 mB of Water** and **1,000 mB of Molten Metal** per ingot/block cast (consumed together at the high-pressure quenching point).
  - Registered `MultiFluidHandler` capability across the Casting Press controller, input port, casing, basin, and chamber blocks so both Water and Molten Metal can be piped in simultaneously or filled by right-clicking with Water Buckets / Molten Metal Buckets.
  - Updated `CastingPressScreen` to render dual side-by-side fluid gauges (`Molten Inflow Basin` and `Quenching Water Tank`) with detailed hover tooltips.
- **Electric Arc Melter 50,000 FE Start Threshold (`ArcMelterControllerBlockEntity`)**:
  - Changed the Electric Arc Melter so that it begins melting a batch as soon as its internal energy buffer reaches **50,000 FE** (`MIN_START_ENERGY = 50_000`) instead of requiring the full 2,000,000 FE buffer upfront before starting.
- **Continuous Casting Press Ground-Level Input Port (`CastingPressControllerBlockEntity`, `GuideBookRegistry`)**:
  - Swapped the `Casting Input Port` at `(0, 1, 2)` (Layer 1, rear-center) with the `Casting Press Casing` block below it at `(0, 0, 2)` (Layer 0, rear-center), allowing ground-level `Y=0` Open Casting Channels from the Electric Arc Melter to feed straight into the Casting Input Port without elevation changes.
- **Multiblock Hologram & Guide Book Name Parity with JEI (`*ControllerBlockEntity`, `StorageTankBlockEntity`, `BlueprintColors`, `GuideBookRegistry`)**:
  - Updated all 3D blueprint hologram labels across all 14 multiblock machines (Industrial Blast Furnace, Electric Arc Melter, Continuous Casting Press, 3-Stage Petroleum Refinery, Industrial Grinder, Industrial Metal Press, Heavy Multi-Smelter, Small & Large Steam Boilers, Small & Large Diesel Boilers, Steam Turbine Generator, Heavy Pit Quarry, and Multiblock Liquid Storage Tank) and the in-game Field Manual Guide Book so every block label matches its exact localized item name in JEI (`en_us.json`).

---

### Version 1.11.42 (In-Game Guide Book Power & Energy Badges, 20s Casting Press & 10s Arc Melter Durations)
- **In-Game Guide Book Energy & Power Specifications (`RefineryGuideScreen`, `GuideEntry`, `GuideBookRegistry`)**:
  - Integrated prominent energy badges (`⚡ Power / Energy: ...`) into the Industrial Field Manual for every machine, multiblock, and tool.
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
- **Continuous Casting Press Cycle Duration (`CastingPressControllerBlockEntity`, `CastingPressModel`, `GuideBookRegistry`)**:
  - Increased casting stroke cycle time from 12.0s to **20.0 seconds (400 ticks)** per process stroke.
  - Dynamically scaled 3D animated hydraulic stamping ram (approach stroke, high-pressure dwell hold, and smooth hydraulic retraction) and molten basin solidification across the full 400-tick duration.
  - Consumes 240,000 FE total per stroke (600 FE/t over 400 ticks).
- **Electric Arc Melter Batch Duration (`ArcMelterControllerBlockEntity`, `ArcMelterModel`, `GuideBookRegistry`)**:
  - Increased ore melting cycle time from 6.0s to **10.0 seconds (200 ticks)** per batch.
  - Dynamically synchronized the 3D electrode arc plasma animation and crucible molten bath filling to the new 200-tick duration.
  - Consumes 2,000,000 FE total per batch (10,000 FE/t over 200 ticks), perfectly matching the full 2,000,000 FE internal buffer.

---

### Version 1.11.40 (Industrial Power & Steam Rebalance: 10k Arc Melter, 10k Steam Turbine, 3x Large Boiler, 5k Multi-Smelter, 2k Grinder, 500 Press, 600 Caster)
- **Electric Arc Melter Power Scaling (`ArcMelterControllerBlockEntity`, `ArcMelterScreen`)**:
  - Rebalanced power consumption to **10,000 FE/t** (up from 40 FE/t).
  - Expanded internal energy capacity to **2,000,000 FE** with **100,000 FE/t** maximum receive rate to buffer and power high-throughput melts.
- **Steam Turbine Generator High-Output Overhaul (`SteamTurbineControllerBlockEntity`, `SteamTurbineScreen`, `TurbinePortBlockEntity`)**:
  - Rebalanced power generation to **10,000 FE/t** (up from 1,000 FE/t).
  - Scaled steam consumption to **300 mB/t** (up from 50 mB/t), requiring 3x the output of a Small Steam Boiler (3 × 100 mB/t).
  - Recaptures **150 mB/t** condensed water in the closed Rankine cycle back to boilers.
  - Increased energy export throughput to **50,000 FE/t** with automated power pushing into adjacent cables and machines.
- **Large Boiler Output Synchronization (`LargeBoilerControllerBlockEntity`, `LargeDieselBoilerControllerBlockEntity`, `LargeDieselBoilerScreen`)**:
  - Synchronized Large Steam Boiler production to **300 mB/t** High Pressure Steam consuming **150 mB/t** Water, exactly matching 3 Small Steam Boilers.
  - Scaled Large Diesel Boiler to **750 mB steam / cycle** (consuming 300 mB water & 30 mB diesel per 10-tick cycle), exactly matching 3x the Small Diesel Boiler.
- **Industrial Multi-Smelter Electric Induction (`MultiSmelterControllerBlockEntity`, `MultiSmelterScreen`)**:
  - Rebalanced electric mode consumption to **5,000 FE/t** (up from 30 FE/t) with an expanded **1,000,000 FE** buffer.
- **Industrial Grinder Pulverizing Power (`IndustrialGrinderControllerBlockEntity`, `IndustrialGrinderScreen`)**:
  - Rebalanced crushing consumption to **2,000 FE/t** (up from 30 FE/t) with an expanded **500,000 FE** buffer.
- **Industrial Metal Press Hydraulic Forming (`MetalPressControllerBlockEntity`, `MetalPressScreen`)**:
  - Rebalanced stamping consumption to **500 FE/t** (up from 20 FE/t) with an expanded **100,000 FE** buffer.
- **Continuous Casting Press Solidification (`CastingPressControllerBlockEntity`, `CastingPressScreen`)**:
  - Rebalanced continuous casting consumption to **600 FE/t** (up from 40 FE/t) with an expanded **500,000 FE** buffer.

---

### Version 1.11.39 (Fast 0.5s Pipe Gate Redstone Pulsing & Natural Action Cycle Order)
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

### Version 1.11.38 (AWWA Safety-Yellow Fire Hydrant & Pipe-Fed Bucket Filling Station)
- **AWWA Safety-Yellow Fire Hydrant (`FireHydrantBlock`, `FireHydrantBlockEntity`, `FireHydrantModel`, `FireHydrantRenderer`, `FireHydrantRenderState`)**:
  - Added the **Fire Hydrant** (`refinery:fire_hydrant`), an industrial AWWA-style safety-yellow standpipe hydrant designed for convenient bucket filling along any fluid pipe network.
  - **Monolithic 3D Model**: High-detail 3D model featuring a black cast-iron bottom flange ring with 8 protruding silver hex nuts and studs, safety-yellow lower flange ring with silver top bolt heads, cylindrical ribbed yellow barrel with front embossed circular `A` and `250 AWWA` plaque, 3-way nozzle outlets (front 4.5" pumper/steamer cap with `B 41` pentagonal nut plus left and right 2.5" side hose nozzles with wrench nuts and bottom chain-loop lugs), upper double bonnet flange with perimeter bolts, domed yellow bonnet cap, black thrust washer, and a rotatable top **Brass Pentagonal Operating Stem Nut**.
  - **Pipe Connectivity & 8,000 mB Internal Buffer**:
    - Automatically connects to Industrial Fluid Pipes on any face (bottom, sides, or back) and actively buffers up to **8,000 mB** (8 buckets) of any bucketable liquid (Water, Lava, Crude Oil, Diesel, Gasoline, Liquid Tar, Ethanol, and Molten Metals).
    - Includes contiguous pipeline draw so right-clicking with a bucket immediately pulls available fluid from connected fluid pipes if at least 1,000 mB is present in the pipeline.
  - **Right-Click Bucket Filling & Valve Animation**:
    - Right-clicking the hydrant with an empty bucket (`minecraft:bucket`) fills it with **1,000 mB** of the supplied liquid, plays authentic bucket fill and valve clank sounds, emits liquid splash particles from the front pumper nozzle, and animates the top brass pentagonal operating nut.
    - Right-clicking with an empty hand displays the buffered fluid type and volume; sneak-right-clicking with an empty hand flushes the internal buffer; right-clicking with a Refinery Wrench rotates the hydrant facing.

---

### Version 1.11.37 (Configurable Quarry Max Speed & Energy Rates, 240 FE/t & 1.5 blocks/s Defaults)
- **Configurable Heavy Pit Quarry Options (`config/refinery.toml`)**:
  - Added user and server configuration options for the Heavy Pit Quarry:
    - `quarryEnergyPerTick`: Max operational power draw (default: `240` FE/tick).
    - `quarryMaxMiningSpeed`: Base excavation speed in blocks per second (default: `1.5` blocks/sec).
  - Configurable strictly through the mod config file (not changeable in-game) to preserve server authority and modpack balance.
  - Dynamically synchronizes configured server values to client terminal screen tooltips.
- **Excavation Speed & Energy Tuning (`QuarryControllerBlockEntity`)**:
  - Increased base excavation speed from 1.0 to **1.5 blocks/second** (~13.3 ticks per standard block).
  - Soft materials (dirt, sand, gravel, tuff) mine at 2x speed (**3.0 blocks/s**); hard ores (iron, diamond) mine at 0.5x speed (**0.75 blocks/s**).
  - Raised maximum power draw to **240 FE/tick** at full speed, with linear proportional throttling when under-powered.

---

### Version 1.11.35 (Perimeter Safety Fence Preservation, Stone-to-Cobblestone Throw-Back Equivalence)
- **Perimeter Safety Fence Preservation During Deep Mining (`QuarryControllerBlockEntity`)**:
  - Fixed an issue where the quarry top-down obstruction scanner would re-mine and destroy the perimeter safety fence immediately after placing it in Phase 1.
  - In Phase 2 (`PHASE_MINING`), top obstruction monitoring is now strictly restricted to the inner excavation volume ($Y \le -1$), completely ceasing monitoring of the upper bench ($Y \ge 0$).
  - Added explicit exclusion for fences (`FenceBlock`, `BlockTags.FENCES`) and walls (`WallBlock`, `BlockTags.WALLS`), ensuring safety fences around the pit rim are permanently preserved.
- **Stone-to-Cobblestone Throw-Back Equivalence (`QuarryControllerBlockEntity`)**:
  - Marking `Stone` as a Throw-Back block in the Geological Manifest or filter slot now automatically treats mined `Cobblestone` drops as throwbacks, ejecting them out the rear conveyor chute into the open pit void instead of filling the factory buffer.
  - Symmetrically synchronized `Cobblestone` $\leftrightarrow$ `Stone` and `Deepslate` $\leftrightarrow$ `Cobbled Deepslate` pairs for both individual manifest toggles, the `[SPOIL]` bulk toggle button, and Slot 10 filter checks.

---

### Version 1.11.33 (In-Game Field Manual Guide Book Quarry & Machinery Additions, Wrench Hologram Projection)
- **In-Game Field Manual Guide Book Updates (`GuideBookRegistry`)**:
  - **Heavy Pit Quarry (`GuideCategory.MULTIBLOCKS`)**: Added comprehensive guide book entry featuring interactive 3-layer blueprint slice viewer (Layers 0, 1, 2), complete material requirements checklist (1 Controller + 26 Casings), crafting recipes, and full operational handbook.
  - **Crude Oil Pump Jack (`GuideCategory.MACHINERY`)**: Added entry detailing 2x3x3 footprint, downhole 50-block reach, power-scaled pumping speed (1–4 FE/t), and crafting recipe.
  - **Bitumen Storage Pile (`GuideCategory.LOGISTICS`)**: Added entry detailing 3x3 open-air bulk storage footprint, 8-stage visual mound growth, and vacuum absorption.
  - **Fluid Pumps & In-World Discharge (`GuideCategory.MACHINERY`)**: Added documentation for Coal, Diesel, and Electric pumps and Fluid Discharge.
- **Refinery Wrench Hologram Wand Support**:
  - Right-clicking the Heavy Pit Quarry Controller with a Refinery Wrench now toggles the 3D blueprint hologram wireframe in-world with auditory button click feedback.

---

### Version 1.11.32 (Heavy Pit Quarry 1 Controller + 26 Casings Multiblock Recipe)
- **Simplified Multiblock Structure**:
  - The Heavy Pit Quarry multiblock now strictly requires **1 Heavy Pit Quarry Controller** and **26 Quarry Structural Casings**.
  - All 26 member block positions require `Quarry Structural Casing`.
  - Removed `Quarry Lattice Frame` blocks from the mod, streamlining resource gathering and crafting.

---

### Version 1.11.30 (Heavy Pit Quarry Digging Heights, Rear Floodlights, Spoil Ejection & Safe Displacement)
- **Quarry Excavation Heights & Bedrock Progression**:
  - The outer $43 \times 43 \times 3$ clearing area now clears top-down from the same Y level as the machine up 3 blocks ($Y = 2$ down to $Y = 0$).
  - Automated safety perimeter railing is installed at $Y = 0$ along the bench rim.
  - The inner $41 \times 41$ pit starts at $Y = -1$ below machine level and mines downward continuously to bedrock.
- **Rear High-Intensity Work Floodlights**:
  - Added dual rear-facing halogen work floodlights mounted to the lattice gantry tower.
  - Glows at full brightness (`FULL_BRIGHT`) when the machine is active, brilliantly illuminating the excavation pit.
- **Safe Multiblock Player Displacement**:
  - Completing the 3x3x3 multiblock automatically teleports any players inside or standing on the multiblock safely to the ground in front of the operator station, preventing suffocation or getting trapped inside blocks.
- **Dedicated Built-in Spoil Discharge**:
  - Removed the front conveyor chute to keep the operator station clear and accessible.
  - Items configured as Throw-Back blocks automatically roll off the built-in rear conveyor chute into the open pit void.
- **Kept Products Buffer & Side Conveyor/Pipe Export**:
  - Kept items wait in the 9-slot machine buffer until a normal conveyor (or belt below) or pipe is connected to the side ports (Left or Right), then actively export.

---

### Version 1.11.28 (Heavy Pit Quarry Throw-Back Blocks, Pit Spoil Ejection & Side Conveyor Routing)
- **Throw-Back Blocks & Pit Spoil Ejection**:
  - Implemented the ability to flag any mined block as a **"Throw Back"** block (overburden/waste rock such as dirt, sand, gravel, stone, deepslate, tuff, or cobblestone).
  - Throw-back blocks bypass factory outputs and are automatically ejected out the **rear conveyor chute** (`0, 0, 3`), tumbling back down into the open pit void.
  - Three convenient ways to configure Throw-Back blocks:
    1. **Click any block in the Geological Manifest Monitor** in the GUI to toggle Throw-Back on/off. Active blocks display an orange outline, `[TB]` badge, and tooltip.
    2. **Click `[SPOIL]`** in the monitor header to instantly toggle all common overburden blocks (dirt, sand, gravel, stone, deepslate, tuff, cobblestone, andesite, diorite, granite) in a single click.
    3. **Insert any block into the dedicated Throw-Back Filter Slot** (Slot 10) in the GUI at `(220, 85)`.
- **Side Conveyor Factory Integration**:
  - All non-throw-back products ("the rest of the products", including valuable ores and minerals) are routed directly to connected normal conveyors on the left and right side conveyor ports (and front port).
  - Detects connected conveyor belts (and below at floor level), places items on belts with forward velocity, and buffers items in the internal 9-slot buffer if no conveyors are connected yet.
- **3D Model & Chute Animations**:
  - Added the rear spoil discharge chute to the monolithic 3D excavator model, complete with traveling item animations tumbling off the rear chute into the pit.

---

### Version 1.11.26 (Heavy Pit Quarry 180° Inversion, Material Speed Tuning & Top-Down Clearing)
- **180° Excavation Flip**:
  - The Heavy Pit Quarry now digs **behind** the machine instead of in front.
  - The controller's operator console and GUI face forward towards the player, while the $43 \times 43 \times 3$ clearing box and $41 \times 41$ pit to bedrock extend behind the machine.
  - The center conveyor discharge chute is positioned at the front face (`0, 0, -1`), ejecting forward onto conveyor belts away from the pit edge alongside the left and right chutes.
- **Dynamic Material-Specific Speed Multipliers**:
  - Soft blocks (**Dirt, Sand, Gravel, and Tuff**) mine at **2x speed** (0.5s / 10 ticks per block at 20 FE/t).
  - Hard precious ores (**Iron and Diamonds**, including deepslate and raw ore blocks) mine at **1/2 speed** (2.0s / 40 ticks per block at 20 FE/t).
  - Standard blocks (Stone, Deepslate, Copper, Gold, etc.) mine at **1x base speed** (1.0s / 20 ticks per block at 20 FE/t).
- **Proactive Top-Down Clearing**:
  - The quarry monitors the excavated volume above its drill depth; any sand, gravel, fallen debris, or fluid stone that enters the cleared volume is systematically mined from the **top down** before resuming downward excavation.

---

### Version 1.11.24 (Heavy Pit Quarry GUI Redesign, Zero-Overlap Layout & Geological Manifest)
- **Heavy Pit Quarry Industrial GUI Redesign**:
  - Replaced the temporary GUI with a full-size $256 \times 196$ custom dark industrial steel interface matching the aesthetic of the mod.
  - Eliminated text overlapping by separating operations into dedicated plates: machine status, 50,000 FE energy gauge, radius controls, dedicated fence slot, 9-slot buffer row, and centered player inventory.
  - Pre-loads mining chunk columns to scan and catalog **every block type and its exact amount** across the entire volume without chunk boundary cutoffs.
  - Integrated CRT Manifest Monitor with paginated block slots, compact count badges, hovering tooltips, and page/scroll controls.

---

### Version 1.11.23 (Heavy Pit Quarry 3D Model Camera Transform Fix & Member Flexibility)
- **Heavy Pit Quarry 3D Monolithic Model Camera Anchor Fix**:
  - Fixed an issue where the assembled 3D quarry excavator model was rendered using `poseStack.last()` inside `submitCustomGeometry` rather than the captured lambda `PoseStack.Pose`.
  - In Minecraft 26.3's deferred render queue, referencing `poseStack.last()` caused the 3D model to bind to the camera/player view matrix (rendering directly on top of the player's head and screen).
  - Geometry is now correctly anchored to the in-world multiblock controller block.
- **Blueprint Hologram & Member Flexibility**:
  - Aligned all multiblock blueprint requirements, in-world floating nametag holograms, and missing-component chat messages to strictly match the 3 real craftable blocks: **Heavy Pit Quarry Controller**, **Quarry Structural Casing**, and **Quarry Lattice Frame**.
  - All 26 member block positions accept either `Quarry Structural Casing` or `Quarry Lattice Frame` interchangeably.

---

### Version 1.11.21 (Heavy Pit Quarry 3×3×3 Multiblock, 3 Conveyor Exports & Real-Time Remaining Block Breakdown)
- **Heavy Pit Quarry 3×3×3 Multiblock Excavation Rig (`QuarryControllerBlock`, `QuarryCasingBlock`, `QuarryFrameBlock`, `QuarryControllerBlockEntity`, `QuarryModel`, `QuarryControllerRenderer`, `QuarryScreen`, `QuarryMenu`)**:
  - Implemented the heavy industrial **Heavy Pit Quarry**: an authentic open-pit surface mining crawler machine engineered for automated strip mining, overburden clearing, and deep pit excavation.
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

---

### Version 1.11.20 (Refinery Heaters Single-Item Drop, Steam Turbine GUI Overhaul & Header Alignment)
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

### Version 1.11.19 (Crude Pump Jack 2x3x3 Multiblock, Bitumen Pile Alignment & Drop Fixes)
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

### Version 1.11.18 (Bitumen Storage Pile 3x3 Footprint, Single-Item Drop Fix, GUI Label Rescaling)
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

### Version 1.11.17 (Bitumen Storage Pile, Dynamic Visual Growth & Vacuum Absorption)
- **Bitumen Storage Pile (`BitumenPileBlock`, `BitumenPilePart`, `BitumenPileBlockEntity`, `BitumenPileModel`, `BitumenPileRenderer`)**:
  - Implemented the 2x2x2 multiblock Bitumen Storage Pile with dynamic loose material heap physics and visual scaling:
    - **Dynamic Visual Growth**: Renders 9 continuous organic heightfield mesh stages (0 to 8) that dynamically mound up in real time from a floor dusting inside a reinforced steel retaining bunker to a towering 2-block-tall asphalt mountain as inventory fills (up to 3,456 items across 54 double-chest slots).
    - **Hopper-Style Vacuum Absorption**: Automatically sucks in bitumen items (`refinery:bitumen` and any tagged bitumen/asphalt items) dropped or falling onto or into the 2x2x2 pile bounds, accompanied by aggregate impact sounds and item particle splashes.
    - **Industrial Logistics Capabilities**: Exposes `WorldlyContainer` and NeoForge `ItemHandler` across all 8 multiblock blocks, allowing hoppers, pipes, conveyors, and direct player interactions to insert bitumen or open the double chest inventory interface.
    - **Photorealistic High-Resolution Textures**: Ultra-realistic 2048x2048 texture atlas with granular asphalt pebble aggregate, dark industrial riveted steel retaining plates, cast steel angle brackets, and compacted tar floor sheen, paired with a custom 32x32 inventory sprite.

---

### Version 1.11.16 (Multiblock Glass-Fused-to-Steel Storage Tank Flush Button Fix)
- **Multiblock Glass-Fused-to-Steel Storage Tank Flush Button (`StorageTankBlockEntity`, `StorageTankMenu`, `StorageTankScreen`)**:
  - Resolved an issue where clicking the "FLUSH" button in the multiblock storage tank GUI had no effect.
  - Linked the master `StorageTankBlockEntity` reference across both 4-argument and 5-argument `StorageTankMenu` constructors and `createMenu`, allowing server-side button click packets (`clickMenuButton(player, 0)`) to resolve the block entity.
  - Added `flushTank(@Nullable Player player)` on `StorageTankBlockEntity`: dumps all stored liquid, clears fluid locks, plays the emptying sound (`SoundEvents.BUCKET_EMPTY`), triggers a block update sync to all tracking clients, and sends a system notification displaying the exact volume of fluid flushed.
  - Updated `StorageTankScreen` hover, render, and click hitboxes to remain active whenever the tank holds liquid or has an active fluid type registered, and added client-side container data zeroing for immediate UI response.

---

### Version 1.11.15 (Blast Furnace & Metal Press GUI Alignment Fixes)
- **Blast Furnace GUI Bottom-Half Player Inventory Alignment (`BlastFurnaceMenu`, `BlastFurnaceScreen`)**:
  - Corrected player inventory slots in `BlastFurnaceMenu` from `y = 74` to `y = 84` and player hotbar from `y = 132` to `y = 142`, perfectly snapping the slot wells onto the dark gunmetal background texture.
  - Adjusted `inventoryLabelY` from `74` to `72` in `BlastFurnaceScreen`, resolving the text overlap where the "Inventory" header previously collided with the top row of items.
- **Metal Press RF Energy Meter Well Alignment (`MetalPressScreen`)**:
  - Corrected the horizontal rendering coordinate of the RF energy bar in `MetalPressScreen` from `xo + 18` to `xo + 14` (`width = 14`), perfectly nesting the yellow energy meter inside the recessed gauge well (`x: 13..28`).
  - Synchronized the energy meter tooltip hover detection bounds from `x: 18..32` to `x: 13..28`, eliminating the 4-pixel rightward shift and texture overflow.

---

### Version 1.11.13 (Conveyor Belt 90-Degree Corner & T-Intersection Adaptive Frame)
- **Conveyor Belt 90-Degree Corner & T-Intersection Adaptive Frame (`ConveyorBeltBlock`, `ConveyorBeltModel`, `ConveyorBeltRenderer`)**:
  - Automatically adapts standard horizontal conveyor belt side frames and rear end caps when conveyors meet perpendicularly at 90 degrees or form T-intersections:
    - **Side Frame Retraction**: When an incoming conveyor feeds into the side of a receiving conveyor (heading N/S into E/W or vice-versa), the side rail on that receiving side is automatically removed so items roll directly onto the belt without colliding with the frame.
    - **Rear Corner Guard Rail**: In 90-degree corner turns (where no conveyor feeds into the back), the removed frame piece is seamlessly relocated to the tail/back side of the receiving conveyor, creating a protective rear bumper that prevents turning items from slipping off the back.
    - **T-Intersection Support**: In T-junctions where a conveyor line continues from behind while a perpendicular conveyor merges into the side, the side rail is removed for the merge while the back remains completely open for uninterrupted passthrough.
    - **Zero Z-Fighting**: The back rail connects flush to the remaining side rail without overlapping faces, and mounting foot pads on the open incoming side are omitted for a completely flat junction.
    - **Backstop Item Retention (`ConveyorBeltBlockEntity`)**: Added physical backstop clamping at the rear rail to ensure turning items never glitch backwards during high-speed routing.

---

### Version 1.11.11 (Seamless Sloped-to-Flat Conveyor Transition & Collision Fix)
- **Seamless Sloped-to-Flat Conveyor Transition & Collision Wall Elimination (`ConveyorBeltBlock`, `SlopedConveyorBeltBlockEntity`)**:
  - Resolved an issue where items reaching the top crest of a sloped conveyor belt would hit a vertical collision ledge, bounce into the air, or get permanently stuck at the transition to a horizontal flat conveyor.
  - Replaced restrictive directional voxel shapes (`ITEM_ENTRY_SHAPES`) in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` with `Shapes.empty()` for `ItemEntity`, eliminating the 5-pixel vertical block collision wall at conveyor seams while preserving full solid collision for players and mobs.
  - Implemented clean conveyor hand-off logic in `SlopedConveyorBeltBlockEntity`: once an item physically crosses the boundary into the next conveyor (`rawT >= 1.0`), ownership is relinquished immediately to prevent competing velocity and position conflicts.
  - Synchronized carrying surface heights across all conveyor types to `0.3125` (`5.0px`), leveled off vertical velocity by `rawT >= 0.82`, and added an exit momentum boost to ensure silky-smooth glide onto flat conveyors.

---

### Version 1.11.10 (Sloped Conveyor Belt Ground Support Legs & Foot Pads)
- **Sloped Conveyor Belt Ground Support Legs & Foot Pads (`SlopedConveyorBeltModel` & `SlopedConveyorBeltRenderer`)**:
  - Dynamically extended support leg trestles downwards to reach the floor or solid ground when chaining sloped conveyors together in multi-block ramps or elevated inclines.
  - Automatically raycasts down up to 32 blocks beneath the elevated conveyor's support legs, detecting the exact top elevation of floor blocks, slabs, machine housings, or containers.
  - Renders continuous aluminum support leg columns from the inclined bed frame all the way down to the surface, complete with heavy bolted foot pads (`UV_FOOT_PAD`) resting on the floor.
  - Adds horizontal cross-bracing ties every 1-meter block interval down the entire height of the extended support legs.
  - Expanded `SlopedConveyorBeltRenderer.getRenderBoundingBox` downwards (`expandTowards(0.0, -32.0, 0.0)`) to ensure tall support legs are never frustum-culled.

---

### Version 1.11.9 (Refinery Tower South Takeoff Pipes Realignment, Arc Melter Hazard Stripe Removal)
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

### Version 1.11.8 (Refinery Output Fluid Auto-Push, Stage 1 South Takeoff Pipe, Anti-Flicker Fixes)
- **Refinery Output Port Fluid Auto-Push to Adjacent Pipes & Tanks (`RefineryPortBlockEntity`)**:
  - Distillation output liquids (Liquid Tar, Diesel, Gasoline) now automatically push outward from output ports (`TAR_OUT`, `DIESEL_OUT`, `GAS_OUT`, and `BYPRODUCT_OUT`) into adjacent fluid pipes (`IndustrialPipeBlockEntity`), fluid storage tanks, and third-party fluid capability handlers at up to 1,000 mB/t.
  - Active multiblock controller discovery with fallback via `ReinforcedCasingBlock.findNearbyController(...)` and automated multiblock synchronization (`syncAssembly()`).
- **Refinery Tower Stage 1 Liquid Tar South Takeoff Pipe Assembly (`RefineryTowerModel`)**:
  - Added a third South-facing horizontal takeoff valve assembly at `Y = 22.0f` on the bottom section (Stage 1: Heavy Residue / Liquid Tar) of the 3×9×3 Petroleum Refinery Tower.
  - Symmetrically matches the rotated Stage 2 (`Y = 60.0f`, Diesel) and Stage 3 (`Y = 115.0f`, Gasoline) takeoff valve assemblies, projecting cleanly towards the South (+Z) face with a crimson indicator handwheel when Stage 1 is active (`UV_STAGE1_CRIMSON`), completing the 3-stage visual takeoff system.
- **Electric Arc Melter & Hydraulic Casting Press Anti-Flicker & Z-Fighting Resolution (`ArcMelterModel` & `CastingPressModel`)**:
  - Upgraded atlas margin padding in `UVRect.of` from 0.15px to 0.5px to eliminate edge bleeding across tiles during camera movement and mipmapping.
  - Suppressed downward ground quads (`y0 <= 0.01f`) to prevent z-fighting against floor blocks.
  - Adjusted base cross-beam heights below deck plates and offset flange rings/locknuts to prevent coplanar rendering conflicts.

---

### Version 1.11.4 (Refinery Tower South Takeoff Pipes Re-orientation & Vertical Header Removal)
- **Refinery Tower Monolithic 3D Model Pipe Re-orientation & Vertical Header Removal**:
  - Removed the tall vertical grey collection header pipe that previously ran up the East (+X) side of the 3×9×3 Petroleum Refinery Tower.
  - Rotated the Stage 2 (Diesel, orange section at `Y = 60.0f`) and Stage 3 (Gasoline, green section at `Y = 115.0f`) horizontal takeoff valve assemblies by 90 degrees around the column shell. Both stubby takeoff pipes now project cleanly towards the **South (+Z)** face (`Z: 15.1f .. 23.5f`), directly aligning with the South-facing Diesel Output Port (`(0, 3, 2)`) and Gas Output Port (`(0, 6, 2)`).
  - Cleared the redundant lower East bracket assembly at Stage 1, leaving the East side of the tower completely unencumbered for furnace placement.

---

### Version 1.11.3 (Dark Gunmetal Industrial GUI Redesign & Stage Toggle Button Fix)
- **Refinery Tower Stage Toggle Button Click & Interaction Fix**:
  - Fixed an issue where clicking the Stage 1, Stage 2, or Stage 3 buttons (`S1`, `S2`, `S3`) in `RefineryScreen` failed to toggle stages.
  - Linked stage button click listeners to execute immediate client prediction (`this.menu.clickMenuButton(...)`) and server packet synchronization (`handleInventoryButtonClick(...)`) with UI click audio feedback.
  - Upgraded `RefineryControllerBlockEntity.stillValid(Player)` to `Container.stillValidBlockEntity(this, player, 16.0F)`, ensuring players interacting with member blocks across the `3×9×3` multiblock structure do not trigger reach distance invalidation when clicking stage toggles.
- **Dark Gunmetal Industrial Aesthetic Overhaul for Refinery & Furnace GUIs**:
  - Restyled `refinery.png`, `burner_refinery_furnace.png`, and `electric_refinery_furnace.png` with heavy dark gunmetal steel panels (`#2B3036`), beveled frames, corner rivets, and recessed wells (`#1D2126` slots, `#0E1318` digital monitors) to match the machine aesthetic of the Multi-Smelter, Blast Furnace, and Grinder.
  - **Burner Refinery Furnace GUI Alignment (`BurnerRefineryFurnaceScreen` & `BurnerRefineryFurnaceMenu`)**:
    - Re-aligned the 3 fuel input slots horizontally to `(12, 20)`, `(30, 20)`, `(48, 20)` and bucket return slot to `(48, 47)` with downward fuel transfer arrow.
    - Added recessed wells for the 4× burn flame indicator `(11, 45, w: 34, h: 20)` and furnace thermometer `(67, 19, w: 12, h: 46)` with a 350 °C operating threshold line.
    - Integrated a dark CRT/LCD Digital Telemetry Monitor box `(88, 19, w: 77, h: 33)` and a 3D beveled Redstone Mode toggle button `(88, 54, w: 77, h: 14)`, completely eliminating overlapping text collisions.
  - **Electric Refinery Furnace GUI Alignment (`ElectricRefineryFurnaceScreen` & `ElectricRefineryFurnaceMenu`)**:
    - Added recessed wells for the RF Energy Buffer gauge `(12, 19, w: 18, h: 46)`, induction coil heating chamber `(34, 24, w: 28, h: 36)` with active coil glow, and thermometer `(66, 19, w: 12, h: 46)`.
    - Integrated matching Digital Telemetry Monitor box `(88, 19, w: 77, h: 33)` and 3D beveled Redstone Mode button `(88, 54, w: 77, h: 14)`.
  - **3-Stage Petroleum Refinery GUI Overhaul (`RefineryScreen` & `RefineryMenu`)**:
    - Organized the interface into 3 dedicated riveted panel plates: Feed & Heat (left), Fractional Distillation & Product Tanks (center), and Bitumen Byproduct (right).
    - Positioned Crude Oil feed tank `(11, 19)`, Heat Input thermometer `(31, 19)`, oil sand cracking groove `(11, 53)`, and crude input/bucket return slots `(12, 59)` and `(30, 59)`.
    - Centered the 3 vertical stage toggle buttons `S3`, `S2`, `S1` at `(56, 19/37/55, w: 24, h: 16)` with 3D beveled styling, stage-specific active colors, and power LEDs.
    - Recessed sight-glass wells for Liquid Tar `(83, 19)`, Diesel `(98, 19)`, and Gasoline `(113, 19)` with meniscus highlights, distillation progress groove `(56, 73, w: 69, h: 5)`, and 2×2 Bitumen byproduct output slots `(132/150, 37/55)`.
    - Added a recessed LED status bezel in the top-right header `(161, 5, w: 8, h: 8)` with live green/red structure assembly status.

---

### Version 1.11.2 (3-Stage 3×9×3 Petroleum Refinery Rebuild, Burner & Electric Refinery Furnaces, Liquid Tar)
- **3-Stage Petroleum Refinery Tower (`3×9×3` Multiblock — Three `3×3×3` Stages)**:
  - Rebuilt the Petroleum Refinery into a 9-block-tall (`3×9×3`, 81 blocks) fractional distillation column divided into three `3×3×3` stages: Stage 1 (Bottom: Liquid Tar & Bitumen), Stage 2 (Middle: Diesel), and Stage 3 (Top: Gasoline).
  - Added interactive Stage 1 / Stage 2 / Stage 3 toggle buttons in the Refinery GUI to customize distillation outputs per **1,000 mB Crude Oil**:
    - Stage 1 only: `500 mB` Liquid Tar + `1` Bitumen.
    - Stage 2 only: `500 mB` Diesel.
    - Stage 3 only: `500 mB` Gasoline.
    - Stage 1 + 2: `250 mB` Liquid Tar + `250 mB` Diesel + `1` Bitumen.
    - Stage 1 + 3: `250 mB` Liquid Tar + `250 mB` Gasoline + `1` Bitumen.
    - Stage 2 + 3: `250 mB` Diesel + `250 mB` Gasoline.
    - All 3 Stages: `250 mB` Liquid Tar + `250 mB` Diesel + `250 mB` Gasoline + **25% chance at `1` Bitumen**.
- **Two Refinery Furnaces (`Burner Refinery Furnace` & `Electric Refinery Furnace`)**:
  - Added two `1×2×1` vertical 2-block heat sources that supply heat (`>= 350 °C` up to `500 °C`) when placed adjacent to Stage 1 of the Refinery Tower:
    - **Burner Refinery Furnace**: Fast-burning (`4×` burn rate) solid/bucket fuel heater.
    - **Electric Refinery Furnace**: High-RF (`500 FE/t`, `500,000 FE` buffer) electric heater.
  - Placed as a single item occupying 2 vertical blocks and guaranteed to drop only 1 item when broken.
- **Liquid Tar Fluid, Bucket & Port (`refinery:liquid_tar`, `refinery:liquid_tar_bucket`, `refinery:tar_port`)**:
  - Added Liquid Tar fluid, bucket, and port block, plus shapeless crafting (`1 Liquid Tar Bucket -> 4 Petroleum Tar`).
- **Zero-Flicker Monolithic 3D Models (`RefineryTowerModel` & `RefineryFurnaceModel`)**:
  - Built using a 16-sided polygonal cylinder/frustum emitter (`emitCylinder` & `emitFlangeRing`) with strictly disjoint vertical segments and `0.5px` UV inset margins for zero texture flickering.

---

### Version 1.10.25 (Texture Flickering Resolution, Control Panel UV Fix & 3D Monolithic Guide)
- **Texture Flickering & Z-Fighting Resolution**:
  - Replaced ad-hoc quad generation with the standardized `Cuboid` + `UVRect` model architecture using a $0.15\text{px}$ inset padding margin across both `ArcMelterModel` and `CastingPressModel`.
  - Removed all coplanar surface overlap and penetrating box geometry on foundation skids, trunnions, and control consoles, completely eliminating visual flickering.
  - Linked active `PoseStack.Pose` into `consumer.setNormal(pose, nx, ny, nz)` to ensure mathematically correct lighting normals when rotated across all 4 cardinal facings.
- **Operator Control Console Texture Wrapping & Alignment**:
  - Re-aligned `UV_CONSOLE_FRONT` and `UV_PLC_FRONT` to the exact atlas coordinates at `(0, 96, 64, 48)` in `ArcCastingAssetGenerator`, rendering crisp OLED screens, ammeter dials, pushbuttons, and E-stops.
  - Formatted consoles as single non-overlapping cuboids with dedicated faceplates on the front face and clean metal enclosure panels on side/top faces.
- **Monolithic 3D Model & Texture Atlas Engineering Guide**:
  - Authored [`MONOLITHIC_3D_MODEL_GUIDE.md`](file:///c:/Users/swack/Desktop/refinery/MONOLITHIC_3D_MODEL_GUIDE.md) providing complete architectural instructions, zero-overlap rules, UV padding formulas, and copy-paste ready Java templates for creating monolithic 3D models and procedural texture atlases for other industrial buildings.

---

### Version 1.10.23 (Monolithic 3D Electric Arc Melter & Continuous Casting Press Overhaul)
- **Electric Arc Melter Monolithic 3D Model & 256×256 Texture Atlas (`ArcMelterModel` & `arc_melter.png`)**:
  - Redesigned the assembled 3×3×3 Electric Arc Melter (`refinery:arc_melter_controller`) into a monolithic 3D Industrial Electric Arc Furnace (EAF) featuring a rocker trunnion foundation skid, cylindrical water-cooled ribbed steel shell with vertical buckstay stiffeners, interior magnesia-carbon refractory hearth, front armored quartz slag/observation door at `(0, 1, 0)`, eccentric bottom-tapping (EBT) refractory launder spout at `(1, 0, 0)` aligning with Open Casting Channels, rear-left high-voltage EAF transformer vault with oil-cooling radiator fins and 3 copper busbar tubes, twin hydraulic gantry mast columns, domed water-cooled roof with delta refractory insert, top flared charge hopper at `(0, 2, 1)`, and front-left operator console.
  - **Dynamic Operational Animations**: 3 reciprocating graphite carbon electrodes in a delta triad with copper clamp arms, incandescent glowing tips, 3D jagged full-bright electric arc plasma bolts leaping into the bath, rising/falling molten metal bath and launder stream dynamically tinted to match the active molten fluid (`molten_iron`, `molten_gold`, `molten_copper`, `molten_tin`, `molten_bronze`, `molten_glass`), and input items sinking in the top charge hopper (with in-world dropped item absorption at `(0, 2, 1)`).
- **Continuous Casting Press Monolithic 3D Model & 256×256 Texture Atlas (`CastingPressModel` & `casting_press.png`)**:
  - Redesigned the assembled 3×3×3 Continuous Casting Press (`refinery:casting_press_controller`) into a monolithic 3D Heavy Hydraulic Continuous Casting & Quenching Press featuring a reinforced steel foundation bed, central water-cooled copper & refractory crystallizer mold basin at `(0, 0, 1)`, 4 precision-ground chrome tie-rod columns with castle locknuts, overhead cast-steel crown platen with crimson-amber main hydraulic cylinder and twin return boosters, front safety grille observation window at `(0, 1, 0)`, upper refractory tundish & sloped launder chute at `(0, 1, 2)`, lower `Y=0` refractory channel receiver troughs at `(0, 0, 2)` and `(-1, 0, 1)` with animated glowing molten inflow streams, right-side water-cooling manifold cabinet, motorized steel run-out roller table at `(1, 0, 0)`, and front-left PLC operator control cabinet.
  - **Dynamic Operational Animations**: Reciprocating hydraulic ram crosshead (with diagonal hazard stripes, bronze guide bushings sliding on the 4 chrome tie-rods, chrome piston rods, and water-cooled copper upper stamping die) that accelerates downward (`0..90` ticks), holds high-pressure quench compression (`90..150` ticks), and retracts smoothly (`150..240` ticks), plus dynamic molten-to-solidified billet transition and finished cast items traveling along the right-hand run-out roller table (`(1, 0, 0)`).
- **Seamless Member Invisibility, Channel Flow & Automation Connectivity**:
  - All 9 Arc Melter and Casting Press controller and member blocks hide their individual block models (`RenderShape.INVISIBLE`, `Shapes.empty()` occlusion) when `ASSEMBLED = true`, forwarding right-click GUI/wrench interactions to the controller and disassembling cleanly if broken.
  - Registered Energy, Item (`WorldlyContainerWrapper`), and Fluid capabilities across all assembled member blocks of both machines and added automatic output export (`tryExportOutput`) on the Casting Press to adjacent conveyors, item pipes, and containers.

---

### Version 1.10.21 (Plate Stamping Die Progression Fix)
- **Plate Stamping Die Recipe (`refinery:plate_die`)**:
  - Changed the Plate Stamping Die crafting recipe (`data/refinery/recipe/plate_die.json`) from Steel Plates (`#c:plates/steel`) to 4 Steel Ingots (`#c:ingots/steel`), eliminating the circular progression dependency where crafting the Plate Die required Steel Plates that could only be stamped with a Plate Die.

---

### Version 1.10.19 (Steel Gear Tag & Valve Pipe Upgrade Recipe Fix)
- **Steel Gear Recipe Tag Resolution (`refinery:steel_gear`)**:
  - Added the `#c:ingots/steel` common item tag (`data/c/tags/item/ingots/steel.json`) and registered it in `#c:ingots`, resolving the missing tag registry error during server/world datapack loading.
- **Valve Pipe Upgrade Recipe Syntax Migration (`refinery:valve_pipe_upgrade`)**:
  - Updated `data/refinery/recipe/valve_pipe_upgrade.json` to use Minecraft 26.3 flat string ingredients in its shapeless recipe definition.

---

### Version 1.10.17 (Redstone Valve Pipe, Pipe Gate Flow Control, Spool Wire Icons)
- **Industrial Valve Pipe (`refinery:valve_pipe`)**:
  - Added the Redstone Valve Pipe capable of opening and closing flow for both fluids and items using redstone signals or Pipe Gates.
  - Features Normal Mode (closes on redstone power) and Inverted Mode (opens on redstone power).
  - Modes can be toggled by right-clicking the valve pipe with the Refinery Wrench, displaying action bar feedback.
  - Fully compatible with BuildCraft-style Pipe Gates attached to the pipe or adjacent pipes.
  - Items pausing before a closed valve pipe remain held in the pipe until the valve is signaled to open.
  - Crafting recipe: 2 Steel Ingots + 1 Redstone Dust $\rightarrow$ 8 Valve Pipes (plus a 1-to-1 conversion recipe from steel pipes).
- **Classic Spool Pipe Wire Icons**:
  - Redesigned Red, Blue, Green, and Yellow pipe wire icons (`pipe_wire_red`, `pipe_wire_blue`, `pipe_wire_green`, `pipe_wire_yellow`) to match classic wooden thread/wire spools with wrapped coils and an unspooling wire tail.

---

### Version 1.10.15 (Metal Press Tooling Dies, Machinery Gears, Corrugated Steel & Rail Stamping)
- **Metal Press Tooling Dies**:
  - Added 4 dedicated tooling dies: Plate Die (`refinery:plate_die`), Gear Die (`refinery:gear_die`), Corrugated Steel Die (`refinery:corrugated_die`), and Rail Die (`refinery:rail_die`).
  - Stamping in the Hydraulic Metal Press strictly requires an installed Die inserted into the dedicated tooling slot.
  - Upgraded the Metal Press GUI with a beveled Die slot beneath the ram, an empty-slot guide tooltip, and ram hover tooltips showing the installed tooling.
- **Machinery Gears & Stamping**:
  - Registered 6 industrial gears: Steel Gear (`refinery:steel_gear`), Iron Gear (`refinery:iron_gear`), Copper Gear (`refinery:copper_gear`), Bronze Gear (`refinery:bronze_gear`), Gold Gear (`refinery:gold_gear`), and Tin Gear (`refinery:tin_gear`).
  - Added shaped crafting recipes (4 ingots in a cross) and common tags (`#c:gears` and `#c:gears/<metal>`).
  - Stamping with the Gear Die produces gears at 4x material efficiency (1 ingot or plate $\rightarrow$ 1 gear).
- **Corrugated Steel Block & Rail Stamping**:
  - Stamping with the Corrugated Steel Die yields 4 Corrugated Steel Panels per Steel Ingot/Plate (and 2 per Iron Ingot/Plate).
  - Stamping with the Rail Die yields 4 Rails per Steel Ingot/Plate, 2 Rails per Iron Ingot/Plate, 2 Powered Rails per Gold Ingot/Plate, and 2 Activator Rails per Copper Ingot/Plate.
- **In-Game Guide Book Documentation**:
  - Updated the Hydraulic Metal Press chapter with full die tooling instructions and all 4 stamping recipe types.

---

### Version 1.10.14 (Slender 3D 8-Bit Pipes, Dedicated Facades Tab, Dynamic Block Textures)
- **Dedicated Pipe Facades Creative Tab (`itemGroup.refinery.facades`)**:
  - Moved all dynamic Pipe Facades out of the main Industrial Refinery creative tab into their own dedicated creative tab (`refinery_facades_tab`), preventing clutter in the main mod item tab.
- **Dynamic Real-Block Texture Rendering for Pipe Facade Item Models (`PipeFacadeSpecialRenderer`)**:
  - Registered a Minecraft 26.3 `SpecialModelRenderer` for `pipe_facade` and `pipe_facade_hole` items via `RegisterSpecialModelRendererEvent`.
  - Automatically samples the target block's native `TextureAtlasSprite` from `TextureAtlas.LOCATION_BLOCKS` at runtime, rendering every facade item in creative menus, player inventories, and hand models with its true block texture (Stone, Oak, Corrugated Steel, Bronze, etc.) and 3D plate thickness rather than a generic flat gray sprite.
- **Slender Horizontal Rectangular 3D 8-Bit Pipe Icons**:
  - Re-proportioned all 23 pipe item icons to slender horizontal rectangles (16px collar height, 12px shaft height, 30px width; 2.5:1 aspect ratio), matching the slender profile of classic Mario pipes.
  - Retains all crisp 8-bit details: flanged collars, top specular highlight stripes, dark notch grooves, deep body mid-tones, 1x1 checkerboard dithering, bottom bounce highlights, and centered 5x7 fluid droplet indicators for liquid pipes.

---

### Version 1.10.12 (Pipe Facade Network Codec Fix & Tin Ore Silk Touch Loot Table Fix)
- **Pipe Facade CustomRecipe Network Packet Encoding (`neoforge:recipe_content`)**:
  - Fixed a disconnect crash (`EncoderException: Failed encoding custom payload neoforge:recipe_content: IllegalStateException: Can't encode 'com.refinery.recipe.PipeFacadeRecipe@...', expected '...'`) when joining a world by registering `PipeFacadeRecipe.INSTANCE` and `PipeFacadeSwapRecipe.INSTANCE` singletons with `equals` and `hashCode` implementations for `MapCodec.unit(...)` and `StreamCodec.unit(...)`.
- **Tin Ore & Deepslate Tin Ore Loot Table Validation**:
  - Updated `tin_ore.json` and `deepslate_tin_ore.json` loot tables to use the Minecraft 26.3 `"condition": "minecraft:tool/can_silk_touch"` syntax instead of `#minecraft:enchantments/silk_touch`, resolving `ReloadableServerRegistries` validation warnings and restoring Silk Touch drops.

---

### Version 1.10.11 (3D 8-Bit Pixel Art Pipe Item Icons & Fluid Markings)
- **Authentic 3D 8-Bit Pixel Art Pipe Item Icons**:
  - Replaced legacy blurred vector/anti-aliased pipe icons with crisp 32x32 3D 8-bit retro arcade pixel art pipe icons inspired by classic Mario pipes.
  - Features flanged collar rims on both ends with vertical rim highlights, specular top highlight stripes, grooved notch separators, deep cylindrical body shading, 1x1 checkerboard dither shading at the base, and bottom bounce specular reflection.
  - Tailored distinct material palettes across all 23 pipe types: Wood, Copper, Iron, Steel, Gold, Diamond, Emerald (reference green pipe), Brass, Cast Iron, Void, Quartz/Merge, Buffer, and Structure.
  - Added a distinct 8-bit fluid droplet emblem marking in the center of all liquid transport pipes (`wood_fluid_pipe`, `copper_fluid_pipe`, `iron_fluid_pipe`, `steel_fluid_pipe`, `gold_fluid_pipe`, `diamond_fluid_pipe`, `cast_iron_fluid_pipe`, `fluid_extractor_pipe`, and `merge_fluid_pipe`) for immediate visual distinction between item and liquid conduits in inventory screens.
  - Added dedicated status badges for utility pipes: golden extraction chevrons for extractor pipes, storage latches for buffer pipes, and swirling singularity vortexes for void pipes.

---

### Version 1.10.10 (BuildCraft-Style Pipe Facades & Structure Pipe)
- **BuildCraft-Style Pipe Facades (`refinery:pipe_facade` & `refinery:pipe_facade_hole`)**:
  - Added **Solid Pipe Facades** (`refinery:pipe_facade`) and **Pass-Through (Hollow) Pipe Facades** (`refinery:pipe_facade_hole`) that conceal pipes behind 2/16-inch (`0.125` block) structural wall panels on any of the 6 faces (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`) of any Industrial Pipe or Buffer Pipe.
  - **Live Registry Scanning & Dynamic Texture Wrapping (`PipeFacadeHelper` & `PipeFacadeRenderer`)**:
    - Automatically scans `BuiltInRegistries.BLOCK` at runtime to support any full-cube, opaque block from Vanilla, Industrial Refinery (including all 17 Corrugated Steel Panels, metal/ore storage blocks, Oil Shale, and industrial casings), and third-party mods without pre-generating static asset files per block.
    - Excludes non-solid or problematic blocks (glass, ice, leaves, grass blocks, slime/honey, TNT, redstone components, spawners, portals, liquids, falling blocks, and block entities).
    - Samples the disguised block's native `TextureAtlasSprite`s on `TextureAtlas.LOCATION_BLOCKS`, preserving **LabPBR 1.3 metallic specular reflections (`_s`)** and **Parallax Occlusion Mapping (`_n`)** in Complementary Unbound and Iris shaders.
  - **Crafting & Conversion Recipes (`PipeFacadeRecipe` & `PipeFacadeSwapRecipe`)**:
    - **Solid Pipe Facade (x8)**: 1 eligible full block in the center surrounded by 8 Copper Item Pipes (`refinery:copper_item_pipe`).
    - **Solid <-> Pass-Through Conversion**: Craft any Solid Pipe Facade 1-to-1 into a Pass-Through Facade (with a centered pipe opening) and back at any time.
  - **In-World Placement & Removal**:
    - Right-click any pipe with a facade to attach it to the clicked side; Shift + Right-Click with a **Refinery Wrench** or empty hand to pop the facade off.
- **Structure Pipe (`refinery:structure_pipe`)**:
  - Added an inexpensive structural pipe (`2 Cobblestone + 1 Gravel -> 8 Structure Pipes`) with a solid cobblestone-gray finish.
  - Transfers neither items nor fluids, serving as a dedicated structural conduit for bridging **Pipe Wires** (`Red`, `Blue`, `Green`, `Yellow`), mounting **Pipe Logic Gates**, and anchoring **Pipe Facades**.

---

### Version 1.10.9 (Cross-Mod Conveyor Belt Transition & Ole Saw Mill Fix)
- **Cross-Mod Conveyor Belt Transition & Item Sticking (Ole Saw Mill Compatibility)**:
  - Fixed item collision box height in `ConveyorBeltBlock` and `RightAngleConveyorBeltBlock` from 4.0 px (`0.25`) to 5.0 px (`0.3125`), matching the standard 5-pixel surface height used by adjacent conveyor mods such as Ole Saw Mill (`olesawmill:sawmill_conveyor`). This eliminates the 1-pixel collision lip that previously stopped items from crossing the block seam.
  - Implemented boundary handoff release in `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity`: once an item physically crosses the forward boundary into a connected conveyor belt block, our conveyor immediately yields ownership, eliminating conflicting dual-conveyor velocity overrides and seam jitter.
  - Added forward velocity boost (`1.3x`) when items approach the exit threshold of an attached conveyor belt, guaranteeing uninterrupted gliding across block boundaries.

---

### Version 1.10.7 (LabPBR 1.3 & POM for Corrugated Steel Panels)
- **LabPBR 1.3 & POM (Parallax Occlusion Mapping) Support for Corrugated Steel Panels**:
  - Added dedicated LabPBR 1.3 Normal Maps (`_n.png`) and Specular Maps (`_s.png`) for all 17 Corrugated Steel Panel variants (raw steel and 16 dyed colors) across both side panels and cross-section caps (102 textures total).
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

---

### Version 1.10.5 (Steam Bucket Creative Restriction & Item Pipe Multi-Block Traversal)
- **Steam Bucket Creative-Only Restriction**:
  - Survival players can no longer scoop up gaseous steam into open buckets. Right-clicking in-world steam with an empty bucket plays a vapor dissipation sound (`SoundEvents.FIRE_EXTINGUISH`) without filling the bucket.
  - The Steam Bucket (`refinery:steam_bucket`) remains accessible in Creative mode for world building and testing, featuring an explanatory tooltip: *"Creative Only: Steam cannot be contained in an open bucket in Survival."*
- **Continuous Item Pipe In-Transit Traversal**:
  - Replaced legacy `ItemStackWithSlot` serialization with dedicated NBT `childrenList` storage for `"TravelingItems"`, fixing packet encoding failures where direction and progress bitmasks exceeded the Minecraft `UNSIGNED_BYTE` 255-slot limitation.
  - Added immediate block entity update broadcasts (`sendBlockUpdated` with flag 3) when an item exits a pipe and enters an adjacent pipe block entity, ensuring uninterrupted visual traversal through every pipe piece across the entire transport network.
  - Increased in-transit item synchronization frequency to every 2 ticks for responsive client-side progress tracking and sub-tick partial tick animation.
  - Applied minimum ambient lighting (`0x00D000D0`) to traveling items inside transparent pipe casings, ensuring items remain distinctly visible even when passing through dark, enclosed, or unlit factory environments.

---

### Version 1.10.3 (Model & Blockstate Resolution, 26.3 Recipe Syntax Migration)
- **Recipe Syntax Migration (Minecraft 26.3 / 1.21.4+)**:
  - Migrated Corrugated Steel Panel recipes (shaped, shapeless re-dyeing, water-bucket washing, stonecutting) to Minecraft 26.3 string-based ingredient IDs (`"#c:corrugated_steel_panels"`, `"refinery:corrugated_steel_panel"`, etc.), resolving recipe registry deserialization warnings.
- **Model & Blockstate Resolution**:
  - Added missing item model for `oil_shale` (`assets/refinery/models/item/oil_shale.json`).
  - Added missing blockstate and block model for `buffer_pipe` (`assets/refinery/blockstates/buffer_pipe.json`, `models/block/buffer_pipe.json`).
  - Cleaned up `casting_input_port`, `casting_output_port`, `melter_input_port`, and `melter_output_port` blockstates by removing invalid `facing=up` and `facing=down` entries, aligning with `HorizontalDirectionalBlock`.

---

### Version 1.10.2 (Corrugated Steel Panels & 16 Dyed Color Variants)
- **Corrugated Steel Panels (Industrial Cladding & Siding)**:
  - Added the **Corrugated Steel Panel** (`refinery:corrugated_steel_panel`) along with **16 dyed color variants** (White, Orange, Magenta, Light Blue, Yellow, Lime, Pink, Gray, Light Gray, Cyan, Purple, Blue, Brown, Green, Red, Black).
  - Designed with 64x64 high-resolution textures capturing authentic R-panel / standing seam industrial sheet metal: specular crests, directional drop shadows in the troughs, fine micro-ribbing stiffeners, and galvanized metallic stippling.
  - **100% Seamless Wall & Roof Flow**: Every block features a 16-pixel rib pitch (4 major ribs per block) and continuous vertical flutes that align mathematically with zero edge borders or seam gaps across adjacent blocks in all dimensions.
  - **Rotated Pillar Orientation (`axis=x|y|z`)**: Place vertically for factory/warehouse exterior siding, or horizontally for industrial metal roofing and architectural cladding. End cross sections accurately depict the wavy corrugated metal plate profile.
  - **Crafting & Dyeing Integration**:
    - Crafted in a 2x2 grid with 4 Iron Ingots -> 4 Corrugated Steel Panels.
    - Stonecutter: 1 Iron Ingot -> 2 Corrugated Steel Panels.
    - Dyeing: 8 Corrugated Steel Panels surrounding any dye -> 8 Dyed Panels.
    - Shapeless 1-to-1 re-dyeing and water-bucket washing back to natural galvanized steel.

---

### Version 1.10.1 (Bituminous Oil Sands & Shale, 3D Pipe End Caps, Item Ejection)
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

---

### Version 1.9.9 (Multi-Smelter, Metal Press, & Blast Furnace Conveyor Auto-Export)
- **Multi-Smelter Conveyor & Logistics Auto-Export**:
  - The Industrial Multi-Smelter (`MultiSmelterControllerBlockEntity`) now auto-exports finished smelted items (such as charcoal from wood/logs, refined ingots, glass) from Output Slots 8..15 directly onto any conveyor belt placed in front of the machine (in front of the Controller or along the front face) or adjacent to any Smelter Output Port.
  - Automatically detects conveyor orientation to avoid feeding items into reversed conveyors heading back into the machine.
  - Enforces smooth single-item spacing along the conveyor surface so items travel down the belt in an orderly line without clumping.
  - Also auto-exports into adjacent item transport pipes, containers (chests, barrels, hoppers), and external item capabilities.
- **Metal Press & Blast Furnace Auto-Export Parity**:
  - Added auto-export support to the Industrial Metal Press (`MetalPressControllerBlockEntity`), automatically ejecting finished stamped items (plates, rods, gears) from slots 2..3 onto adjacent conveyors or containers at the bed discharge or in front.
  - Upgraded Blast Furnace (`BlastFurnaceControllerBlockEntity`) auto-export with comprehensive conveyor detection, proper forward orientation handling, and single-item spacing.

---

### Version 1.9.8 (Multi-Smelter Dark Gunmetal GUI & Item Pipe In-Transit Visibility)
- **Item Pipe In-Transit Visibility & 3D Glass Rendering**:
  - Implemented 360-degree clear cylindrical glass pipe bodies with heavy industrial structural collar rings for all item transport pipes (Wood, Stone, Iron, Gold, Diamond, Netherite, Emerald, Quartz, Obsidian, Void, and Buffer).
  - Traveling items are now fully visible moving through the pipes in real time.
  - Added smooth client-side partial tick interpolation for fluid, continuous item motion.
  - Added full NBT serialization/deserialization for traveling items in `IndustrialPipeBlockEntity` via `ItemStackWithSlot.CODEC`, keeping items preserved during chunk unloads and reloads.
  - Synchronized pipe traveling items to client every 4 ticks during transit.
- **Industrial Multi-Smelter GUI Overhaul & Dark Gunmetal Styling**:
  - Restyled `textures/gui/multi_smelter.png` in the dark gunmetal industrial steel theme (`#2B3036`), beveled borders, corner rivets, and recessed slot wells to seamlessly match the Grinder and Metal Press.
  - Re-aligned 8 input slots (4x2 grid) and 8 output slots (4x2 grid) with clean spacing, plus centered fuel slot at (89, 62).
  - Repositioned title and inventory labels to avoid overlaps (`titleLabelX=8, titleLabelY=6`, `inventoryLabelX=8, inventoryLabelY=74`).
  - Integrated energy gauge rendering with hover tooltips (RF stored / max capacity) at (10, 18).
  - Added animated combustion flame indicator at (70, 63) and induction heating indicator at (110, 63) with descriptive tooltips.
  - Moved parallel cook progress bars to dedicated 2px grooves under each input slot (`Y=36` and `Y=56`), eliminating all item slot overlapping.

---

### Version 1.9.7 (Guide Book Click Fix)
- Fixed the Industrial Field Manual ignoring all mouse clicks (sidebar, Table of Contents, Back, tabs, layer pills); left-click is button 1 in Minecraft 26.3.

---

### Version 1.9.6 (Informative Pipe Tooltips & Transfer Rates)
- **Comprehensive Pipe Tooltips (`PipeBlockItem`)**:
  - Added rich hover tooltips to all 22 industrial item and fluid pipes.
  - Displays functional role, exact fluid transfer rates (mB/t), internal buffer capacities, item speed multipliers, and operational tips (filter GUI, wrench rotation, merge selection, and pulse gate requirements).

---

### Version 1.9.5 (In-Game Industrial Field Manual / Guide Book)
- **In-Game Industrial Field Manual (`refinery:guide_book`)**:
  - Added craftable guide book (`minecraft:book` + `minecraft:iron_ingot`).
  - Right-clicking opens the interactive manual GUI (`RefineryGuideScreen`).
  - Features category sidebar filtering, scrollable Table of Contents cards, and direct machine links.
  - Multiblock pages feature a complete **Bill of Materials** showing exact block counts, an interactive **Layer Layout Viewer** with 2D top-down grid blueprints for each layer (`L0` to `L4`), orientation guides, cell hover coordinates, and recipe tables.

---

### Version 1.9.4 (Wood Extraction Pipe Filter GUI & Pulse Gate Extraction)
- **Wood Extraction Pipe Filter GUI**:
  - Right-clicking a Wood Item Pipe opens a 9-slot filter interface (3x3 grid) allowing players to define which items may be extracted.
  - When filter slots are empty, all items are eligible for extraction; when items are configured in the filter, only matching items are extracted.
  - Filter contents persist through saves and drop when the pipe is broken.
- **Autarchic Pulse Gate Gated Extraction**:
  - Wood Item Extraction Pipes now strictly extract items when pulsed by an Autarchic Pulse Gate, preventing unmetered passive extraction.
  - Autarchic pulsar actions (`PULSAR_PULL_ITEM`, `PULSAR_PULL_ITEM_5`, and `PULSAR_PULL_ITEM_10`) respect the Wood Pipe filter configuration before withdrawing items.

---

### Version 1.9.3 (Cross-Mod Conveyor Belt Handoff & Trapped Item Resolution)
- **Cross-Mod Conveyor Belt Handoff & Trapped Item Resolution**:
  - Implemented universal cross-mod conveyor detection (`ConveyorBeltBlock.isAnyConveyorBlock`), preventing conveyor belts from mistakenly identifying external conveyor blocks (such as Ole Saw Mill's conveyors) as static inventory endpoints.
  - Eliminated the infinite capability insertion/respawn cycle at conveyor transfer seams that caused items to bunch up, get trapped in an unpickable state with reset pickup delays, and stall at the handoff boundary.
  - Adjusted conveyor item surface height to $pos.getY() + 0.32$ (above 5px / 0.3125 collision height), ensuring items glide seamlessly across block transitions without catching on the leading edges of adjacent belts.
  - Connected front and back conveyor models to external matching conveyors seamlessly.

---

### Version 1.9.2 (Autarchic Pulse Item x5 / x10)
- **New Gate Actions**: Autarchic Pulse (Item x5) and Autarchic Pulse (Item x10) pull 5 or 10 items at a time from the adjacent container into the pipe (autarchic gates only).

---

### Version 1.9.0 (Hologram Blueprint Label Size Reduced by 60%)
- **Hologram Blueprint Label Text Size Significantly Reduced**:
  - Implemented centralized blueprint label rendering via `BlueprintColors.renderBlueprintLabel` across all 13 multiblock machine renderers.
  - Reduced text size by 60% (scaled to 40% of original dimensions), eliminating label overlap across adjacent blocks and making multiblock construction holograms vastly cleaner and easier to read.
  - Re-anchored billboard labels to the top center of each block frame with zero-offset baseline correction.

---
### Version 1.8.63 (Petroleum Refinery Diesel Output Port Relocation)
- **Diesel Output Port Relocated to Ground Layer**:
  - Relocated the Diesel Output Port from Layer 1 (`(0, 1, 2)`) to Layer 0 (`(0, 0, 2)`), moving it down to the ground tier directly beneath the Gas Output Port.
  - Placed a Reinforced Casing at Layer 1 (`(0, 1, 2)`) so the Diesel Port and Gas Port (Layer 2 `(0, 2, 2)`) are separated by a solid casing block rather than sitting adjacent, preventing fluid pipe congestion on the rear face.
  - Blueprint holographic projections now reflect the new ground-level port location.

---
### Version 1.8.62 (Arc Melter & Casting Press Speeds, Viscous Lava Channel Flow, Grinder Auto-Export)
- **Arc Melter Processing Speed**:
  - Doubled batch process time from 60 ticks (3.0s) to 120 ticks (6.0s).
  - Reduced automatic fluid output rate to 25 mB every 4 ticks, simulating a steady pouring stream.
- **Continuous Casting Press Processing Speed**:
  - Tripled cycle process time from 80 ticks (4.0s) to 240 ticks (12.0s) per press stroke (+200% duration).
  - Synchronized hydraulic die ram descent/ascent to 120 ticks each, with quench effects and ingot solidification at tick 120.
- **Viscous Lava-Like Casting Channel Flow**:
  - Molten metal accumulates in empty channels for 25 ticks (1.25s) before cascading downstream, mimicking thick molten lava.
  - Slowed downstream fluid transfer rate to 25 mB every 4 ticks.
  - Active upstream channels maintain a 150 mB visual pool so pouring channels stay continuously filled and glowing.
  - Reduced visual molten fluid surface scrolling velocity in the renderer by nearly 7x (0.0018).
- **Industrial Grinder Conveyor Auto-Export**:
  - Automatically exports finished crushed items onto any attached conveyor belt (flat, sloped, or right-angle) one at a time.
  - Removed downstream receiver restriction that prevented single or un-terminated conveyor attachments.
  - Safely buffers finished items internally in output slots 4..7 when no conveyor, pipe, or container is connected.

---
### Version 1.8.61 (Sloped-to-Flat Conveyor Crest Transition & Item Sticking Resolution)
- **Sloped-to-Flat Conveyor Crest Alignment**:
  - Re-profiled the sloped conveyor incline curve so it reaches the full crest height of the level conveyor ($pos.getY() + 1.3125$) by $t = 0.85$, allowing items to glide horizontally across the top terminal rather than slamming into the vertical face of the flat conveyor.
  - Extended active item position and height alignment across the hand-off seam ($0.0 \le rawT \le 1.15$), maintaining smooth continuity while transitioning into the next block.
- **Directional Recessed Entry Collision**:
  - Implemented directional recessed entry collision shapes (`ITEM_ENTRY_SHAPES`) on flat and right-angle conveyors (`ConveyorBeltBlock` & `RightAngleConveyorBeltBlock`), leaving a 3px clearance at the incoming face to completely eliminate physical collision walls for incoming items.
  - Expanded horizontal item collection bounds on level conveyors ($\pm0.15$ blocks) and lowered detection floor to $pos.getY() + 0.05$ with automatic upward alignment onto the belt surface ($0.3125$).
  - Eliminated downward gravity drag during level conveyor transit, setting vertical velocity to $0.0$ so items glide seamlessly across all block boundaries.

---

### Version 1.8.60 (Arc Melter 2x Output Doubling, Dark Steel Blast Furnace & Metal Press GUI, Clean Screen Headers)
- **2x Output Doubling via Arc Melter & Casting Press**:
  - Raw ores, ore blocks, and dusts smelted in the Electric Arc Melter yield 1,000 mB of molten metal.
  - The Hydraulic Casting Press now stamps 1,000 mB of molten fluid into **2 ingots** (Iron, Gold, Copper, Tin, Bronze) or **2 Glass blocks**, doubling the reward for processing through the foundry chain.
  - Ingot recycling is balanced: inserting ingots into the Arc Melter yields 500 mB per ingot (1,000 mB for 2 ingots), preventing infinite duplication loops.
- **Blast Furnace & Metal Press Dark Industrial GUIs**:
  - Replaced legacy vanilla light-gray GUI textures for both the Industrial Blast Furnace and Hydraulic Metal Press with heavy riveted dark steel panels (`#2E3339`), recessed dark slot wells (`#1D2126`), and beveled industrial bezels matching the rest of the machinery suite.
  - Realignment of `inventoryLabelY` to 74 for consistent spacing above player inventory slots.
- **Screen Title Alignment & Overlapping Status Text Elimination**:
  - Eliminated text collisions where "ONLINE", "OFFLINE", "UNFORMED", and pump subheaders were rendered at $(x \approx 98..120, y=6)$ directly colliding with machine titles (e.g., "Industrial Rock Crusher", "Hydraulic Casting Press", "Industrial Blast Furnace").
  - Removed header text collisions across all mod container screens (`IndustrialGrinderScreen`, `ArcMelterScreen`, `CastingPressScreen`, `BlastFurnaceScreen`, `MetalPressScreen`, `FermenterScreen`, `RefineryScreen`, `SmallBoilerScreen`, `LargeBoilerScreen`, `LargeDieselBoilerScreen`, `SmallDieselBoilerScreen`, `MultiSmelterScreen`, `SteamTurbineScreen`, `StorageTankScreen`, `FeedingTroughScreen`, and pump screens), leaving title rows completely uncorrupted.

---

### Version 1.8.59 (Arc Furnace, Casting Press, & Channel Flow Rates Adjusted)
- **Arc Melter Processing Speed**:
  - Increased `PROCESS_TIME` from 40 ticks (2.0s) to 60 ticks (3.0s) per smelting cycle (+50% process duration).
- **Casting Press Processing Speed**:
  - Increased `PROCESS_TIME` from 40 ticks (2.0s) to 80 ticks (4.0s) per press stroke (+100% process duration / 50% speed).
  - Adjusted hydraulic stamping die ram stroke in `CastingPressControllerRenderer` to span 40 ticks descent and 40 ticks ascent, with ingot solidification at tick 40.
- **Molten Channel Fluid Flow & Animation Speed**:
  - Decreased casting channel fluid transfer rate from 250 mB/t to 80 mB/t in `CastingChannelBlockEntity` and `ArcMelterControllerBlockEntity`.
  - Reduced visual molten fluid surface scrolling rate from 0.035 to 0.012 in `CastingChannelRenderer`, providing realistic viscous molten fluid movement.

---

### Version 1.8.58 (Casting Press Input Port Molten Acceptance, Pipe Wire Termination, & Generator Drops)
- **Continuous Casting Press Input Port Molten Fluid Acceptance**:
  - Upgraded `SingleFluidHandler` with dynamic fluid setters and predicate filtering, resolving empty-tank rejection so Casting Input Ports accept molten fluids into empty internal tanks.
  - Linked `CASTING_PORT_BE` and `CASTING_PRESS_CONTROLLER_BE` fluid capabilities to dynamic setters and casting recipe filters (`getCastingProduct`), enabling molten metal transfer from casting channels, fluid pipes, and direct bucket interactions.
  - Added instant multiblock assembly synchronization (`syncAssembly()`) upon capability querying, ensuring connected channels and pipes immediately flow upon structure completion.
  - Added direct right-click bucket dumping on `CastingPortBlock` to fill 1,000 mB of molten fluid into the input port and return an empty bucket.
- **Pipe Wire End Wire Termination & Routing Correction**:
  - Eliminated erroneous arm propagation in `PipeWireEntityRenderer` when a wire terminates on an end pipe (`wireCount <= 1`).
  - End wires now terminate cleanly at the pipe's center node without sprouting ghost perpendicular or 45°/90° arms into thin air or adjacent blocks.
- **Diesel Generator Placement Alignment & Drops**:
  - Confirmed 2-block monolithic model bounds and coordinate transformations across all four cardinal directions (NORTH, SOUTH, EAST, WEST), guaranteeing the enclosure aligns flush with placed world blocks.
  - Confirmed break behavior ensuring breaking either half of the 2x1 generator reliably drops exactly 1 item.

---

### Version 1.8.57 (Unlimited Channel Flow Length, Animated Molten Metal, & Arc/Casting GUI Alignment)
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

---

### Version 1.8.56 (Electric Arc Melter & Continuous Casting Press Blueprint Holograms)
- **Electric Arc Melter & Continuous Casting Press Blueprint Holograms**:
  - Added sneak-right-click (Shift + Right-Click) support to both `ArcMelterControllerBlock` and `CastingPressControllerBlock` to project real-time 3D in-world holographic build guides, matching all other multiblock machines in the mod.
  - Added empty-hand right-click projection when unassembled so players without a wrench can preview multiblock construction requirements and receive structure diagnostics in chat.
  - Registered `ArcMelterControllerBlock` and `CastingPressControllerBlock` (along with `MultiSmelterControllerBlock`, `SmallDieselBoilerControllerBlock`, and `LargeDieselBoilerControllerBlock`) directly in `RefineryWrenchItem.useOn` so right-clicking with a wrench cleanly triggers holographic blueprint projection instead of block rotation.
  - Implemented `shouldRenderOffScreen()` and expanded `getRenderBoundingBox()` in `ArcMelterControllerRenderer`, `CastingPressControllerRenderer`, `MultiSmelterControllerRenderer`, and `BlastFurnaceControllerRenderer` to eliminate frustum culling when players look away from the controller block to examine the hologram structure.
  - Added `getUpdateTag()` implementation to ensure client block entities properly sync state tags upon chunk loading.

---

### Version 1.8.55 (Buffer Pipe 1-Slot Chest Pipe)
- **Buffer Pipe (1-Slot Chest Pipe)**:
  - Added the **Buffer Pipe** (`refinery:buffer_pipe`): an industrial pipe that acts like a chest with 1 inventory slot (holding 1 full stack of items).
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

### Version 1.8.54 (Diesel Generator Placement Alignment & Drop Duplication Fix)
- **Diesel Generator Placement Alignment & Drop Duplication**:
  - Corrected East and West rotation angles in `DieselGeneratorRenderer`: inverted `EAST` from $90.0^\circ$ to $270.0^\circ$ and `WEST` from $270.0^\circ$ to $90.0^\circ$, fixing a spatial offset where the 2-block monolithic generator model rendered shifted by 2 blocks into adjacent air instead of matching its physical blocks and selection outlines.
  - Made the generator face towards the player upon placement using `context.getHorizontalDirection().getOpposite()`, matching standard Minecraft machine conventions.
  - Added a `part: left` block state property condition to `loot_table/blocks/diesel_generator.json` to prevent duplicate generator item drops when breaking either block.
  - Updated `DieselGeneratorBlock.playerWillDestroy`: in survival mode, breaking the `RIGHT` block now safely destroys the master `LEFT` block to drop the single generator item and inventory contents; in creative mode, breaking either half immediately removes its partner block cleanly without dropping items.

---

### Version 1.8.53 (Pipe Wire End-Wire Direction & Spurious Gate Branches Fix)
- **Pipe Wire End-Wire Direction & Spurious Gate Branches**:
  - Eliminated bogus wire arm connection generation in `PipeWireEntityRenderer` that previously read `gate.getDirection()` (which returned `EAST`), causing 0.5-block long wire conduits to shoot out sideways into empty air at every gate.
  - Corrected end-wire directional continuation: when a wire terminates on a pipe that continues straight or around a corner, the wire now automatically follows the physical pipe's direction across that block instead of making an abrupt 90°/45° turn into open space.
  - Dynamically configured radial quadrant offsets (`[cx, cy, cz]`) across all 3 spatial axes ($Y$, $Z$, and $X$); on vertical pipes, wires now hug the pipe perimeter diagonally across the $X$-$Z$ plane rather than collapsing along an assumed $Z$-axis.

---

### Version 1.8.52 (Pipe Wire Signal Expiration & Active State Stuck Fix)
- **Pipe Wire Active State Stuck Fix**:
  - Implemented dynamic pulse lease timers (`signalHoldTicks` and `wireHoldTicks`) across `PipeWireEntity` and `PipeGateEntity`. Wire signals automatically expire within 6 ticks ($0.3$s) when triggering conditions are lost.
  - Eliminated permanent one-way latching where `DATA_SIGNALS` and `wireStates` remained active indefinitely after gate actions stopped emitting.
  - Replaced accidental lever-toggle behavior in `PipeGateEntity.interact` when right-clicking gates with pipe wire items; now safely attaches the wire color to the pipe host without latching logic bits.
  - Constrained BFS wire propagation to only traverse adjacent pipe blocks that actually have that wire color installed (`PipeWireEntity.hasWire(color)`).
  - Dynamically synchronized gate screen GUI wire status indicators and `WIRE_COLOR_ON` / `WIRE_COLOR_OFF` condition triggers to live wire states each tick.

---

### Version 1.8.51 (Sloped-to-Flat Conveyor Transition Seam & Item Sticking Fix)
- **Sloped-to-Flat Conveyor Transition Seam Alignment**:
  - Aligned the bottom surface elevation of the sloped conveyor belt to `0.3125` ($5.0$ pixels), matching the flat conveyor belt bed with zero vertical ledge.
  - Set `ItemEntity` collision boxes on flat and corner conveyor belts to $4.0$px (`Block.box(0, 0, 0, 16, 4, 16)`), preventing items from colliding with the leading edge of flat conveyors when descending from slopes.
  - Dynamically tapered downward vertical velocity to $0.0$ near the bottom terminal ($t \ge 0.85$ on down slopes) and applied a forward hand-off velocity boost ($1.3\times$).
  - Guarded vertical `setPos` positioning strictly to $0.0 \le t \le 1.0$ to ensure the sloped conveyor cleanly hands off control as items enter adjacent blocks.
  - Expanded horizontal item collection bounds on flat and right-angle conveyors by $\pm0.08$ blocks to seamlessly capture incoming items at block boundaries.

---

### Version 1.8.50 (Sloped & Corner Conveyor Belt Controls Unified)
- **Unified Conveyor Belt Player Controls**:
  - **Normal Right-Click** (with wrench or empty hand): Rotates conveyor direction clockwise (`NORTH` $\rightarrow$ `EAST` $\rightarrow$ `SOUTH` $\rightarrow$ `WEST`).
  - **Sneak Right-Click** (Shift + Right-Click with wrench or empty hand):
    - **Sloped Conveyor Belt**: Toggles incline elevation between `UP` and `DOWN`, allowing conveyors to easily route items downhill instead of uphill.
    - **Right-Angle Conveyor Belt**: Toggles turn direction between `RIGHT` and `LEFT`.
  - Empty-hand normal right-click now rotates conveyors reliably without needing a wrench, and shift-right-click smoothly changes elevation or turn direction.

---

### Version 1.8.49 (Creative Fluid Pump, 2x Pump Output, & 200-Block Fluid Discharge Range)
- **Creative Fluid Pump (`refinery:fluid_pump`)**:
  - Transformed the industrial pump into a standalone **Creative Fluid Pump** for sandbox testing and infinite fluid supply.
  - Requires zero fuel, zero electricity, and no intake target; automatically fills with infinite water upon placement and auto-pushes up to 2,000 mB/t directly into connected pipes, tanks, and machines.
  - Fluid extraction never drains the internal buffer.
  - Removed survival recipe so it is exclusively obtainable through Creative Mode or `/give`.
  - Rendered with a custom Vibrant Magenta frame styling (`0xFFD946EF`) and dedicated creative GUI.
- **Fluid Discharge Valve 200-Block Fill Range (`refinery:fluid_discharge`)**:
  - Expanded contiguous horizontal fill radius from 48 blocks to **200 blocks** ($401 \times 401$ horizontal bounding area).
  - Raised BFS node exploration ceiling to **131,072 nodes** to traverse vast water bodies.
  - Added $O(1)$ frontier neighbor caching (`lastFillPos`) allowing the valve to place source blocks rapidly without re-scanning previously placed water.
  - Multi-placement support: places up to 4 source blocks (4,000 mB) per tick when supplied by high-throughput pumps with an enlarged 16,000 mB tank buffer.
  - Guarded against chunk thrashing via `level.isLoaded()`.
- **Doubled Pumping Throughput on Diesel & Electric (RF) Pumps**:
  - Doubled the output push rate of both Diesel Fluid Pumps and Electric Fluid Pumps from 500 mB/t to **1,000 mB/t**.
  - Halved cycle duration from 20 ticks to **10 ticks** (2x speed) and doubled pumped amount from 1,000 mB to **2,000 mB** per cycle.

---

### Version 1.8.48 (Boiler Meltdown Raid Siren Alarm Countdown)
- **Acoustic Evacuation Raid Siren Alarm**:
  - Added a very loud industrial raid siren alarm (`minecraft:event.raid.horn`) across all Small and Large Steam Boilers and Diesel Boilers during dry-running overheat countdowns (60s / 1,200 ticks).
  - High acoustic range (16.0fâ€“20.0f volume, audible up to 250â€“320+ blocks away) so factory operators receive unmistakable auditory warning when a boiler runs out of water while burning fuel.
  - Urgency escalation:
    - **0sâ€“30s**: Deep warning horn every 4 seconds (pitch 0.85).
    - **30sâ€“45s**: Urgent horn blast every 3 seconds (pitch 1.0).
    - **Final 15s**: Rapid, shrieking alarm blast every 2 seconds (pitch 1.2) accompanied by billowing black smoke and hissing until water is restored, redstone signal is removed, or the boiler detonates.

---

### Version 1.8.47 (Boiler Infinite Water Draw & Phantom Fluid Voiding Fix)
- **Resolved Infinite Water Drain**:
  - Fixed an issue where Steam Boilers and Diesel Boilers continuously drained connected water tanks and pipes even when full at 16,000 mB.
  - Corrected `BOILER_PORT_BE` capability registration which erroneously reported a static 64,000 mB capacity on Small Boilers (16,000 mB actual), causing the fluid transaction handler to report 48,000 mB available space and void incoming water while accepting it from upstream pipes.
  - Dynamically bound port capacity to `controller.getTankCapacity()` across all small (16,000 mB) and large (64,000 mB) boilers.
- **Fail-Safe Transaction Math**:
  - Updated `SingleFluidHandler` insertion and extraction logic to verify actual difference in internal fluid levels, preventing phantom liquid voiding or over-reporting accepted amounts.
  - Added real-time client block synchronization when fluids enter or leave boiler ports.

---

### Version 1.8.46 (Storage Tank Valve Isolation & Side Connection Prevention)
- **Casing Wall Fluid Isolation**:
  - Removed fluid capability registration and machine recognition from `refinery:storage_tank_casing`. Liquid pipes running adjacent to storage tank perimeter walls will no longer snap or connect horizontally into the casing.
- **Strict Valve Directionality**:
  - Multiblock liquid storage tanks now only accept liquid from the top **Fill Valve** (`refinery:storage_tank_fill_valve`) and only export liquid from the bottom **Drain Valve** (`refinery:storage_tank_drain_valve`).
  - Fill valve handlers strictly reject extraction (`extract = 0`), and drain valve handlers strictly reject insertion (`insert = 0`).
- **Targeted Drain Auto-Export**:
  - Auto-export push from the multiblock tank is restricted to direct neighbors of the Drain Valve, preventing active discharge into side casing walls.

---

### Version 1.8.45 (Pipe Fluid Capacity Reduction 250â€“500 mB & High-Speed Flow)
- **Pipe Fluid Capacity Reduction (250â€“500 mB)**:
  - Scaled down pipe internal fluid storage from 1,000â€“8,000 mB to compact **250â€“500 mB** per block, preventing pipelines from hoarding large reservoirs of liquid.
- **High-Throughput Material Transfer Rates**:
  - Transfer rates are decoupled from capacity and boosted significantly by material tier (Wood: 100 mB/t, Copper: 250 mB/t, Iron: 500 mB/t, Gold: 800 mB/t, Diamond: 1,000 mB/t, Steel: 1,500 mB/t).
  - Streamlined pipe-to-pipe fluid equalization, allowing liquids to surge swiftly through long runs directly into consumers.

---
### Version 1.8.44 (Diamond Sorting Pipe Filter Retention Across Restarts)
- **Diamond Sorting Pipe Filter Retention Fix**:
  - Resolved filter loss across restarts in Diamond Sorting Pipes (
efinery:diamond_item_pipe).
  - Fixed an internal container list truncation issue during loadAdditional where clearContent() reduced the NonNullList size to 0, rejecting all restored filter items.
  - Direction filters (6 directions $\times$ 9 slots) now save and restore faithfully under dedicated NBT list records with live network sync and safety drops on block break.

---
### Version 1.8.43 (Terrain Loading Deadlock Fix with C2ME)
- **Terrain Loading Deadlock Fix (TinRetrogenHandler)**:
  - Fixed an issue where worlds with C2ME (Concurrent Chunk Management Engine) got permanently stuck on the "Loading Terrain" screen.
  - Eliminated the custom ChunkEvent.Load listener that attempted synchronous block checks across chunk borders. Tin Ore generation is now handled cleanly through Minecraft's native worldgen feature pipeline.

---
### Version 1.8.42 (Minecraft 26.3 Worldgen Feature Migration & en_us.json Fix)
- **Minecraft 26.3 Worldgen Feature Migration (`worldgen/feature/`)**:
  - In Minecraft 26.3, configured features are resolved from `data/<namespace>/worldgen/feature/` under registry `minecraft:worldgen/feature`.
  - Migrated `ore_tin.json` from legacy `configured_feature/` to `worldgen/feature/` and inlined target configuration, resolving fatal `Unbound values in registry ResourceKey[minecraft:root / minecraft:worldgen/feature]: [refinery:ore_tin]` when loading world saves.
- **Language Localization Parsing**:
  - Resolved merge conflict in `en_us.json`, restoring compatibility with Jade and language loaders.

---

### Version 1.8.41 (Minecraft Bootstrap Block Registration Fix)
- **Minecraft Bootstrap Block Registration Fix (`Block id not set`)**:
  - Resolved fatal `NullPointerException: Block id not set` occurring during mod bootstrap registry dispatch.
  - Corrected block registration supplier lambdas for `tin_ore`, `deepslate_tin_ore`, `raw_tin_block`, `tin_block`, and `bronze_block` to chain onto the NeoForge-provided `Properties` instance rather than instantiating detached property objects.

---

### Version 1.8.40 (Industrial Diesel Generator 1x2x2 Compact Power Plant)
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

### Version 1.8.39 (Tin Ore Worldgen & Retrogen, Bronze 1:1 Forging Ratio)
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

### Version 1.8.38 (Pipe State & 3D Elbow Connection Retention on Server Restart)
- **Pipe State & 3D Elbow/Corner Connection Retention on Server Restart**:
  - Fixed a critical deserialization bug in `IndustrialPipeBlockEntity`: the `BlockEntityType` factory constructor `IndustrialPipeBlockEntity(BlockPos pos, BlockState state)` previously hardcoded `IndustrialPipeType.FLUID` and `IndustrialPipeMaterial.STEEL`.
  - When chunks reloaded from disk on server restart or transmitted over the network to the client, all item pipes (such as Copper, Iron, Steel, Wood, Gold, Diamond, Emerald) were mistakenly instantiated as fluid pipes.
  - When connection checks subsequently ran, `pipeType.handlesItems()` evaluated to `false`, causing pipes to sever their connections to item machines (conveyor belts, hoppers, chests, multiblock ports) and visually revert from 3D corner elbows to straight, dead-end capped runs.
  - Updated the 2-argument constructor and getters to dynamically resolve `pipeType` and `material` directly from `state.getBlock()`, ensuring pipes retain their exact type, material, fluid capacity, and item behavior across chunk loads and server restarts.
  - Guarded against block entity initialization races during world startup: if a neighbor block possesses a block entity (`targetState.hasBlockEntity()`) that has not finished instantiating (`level.getBlockEntity(targetPos) == null`), existing saved connections are safely preserved.
  - Added explicit `PipeType` and `Material` NBT tags in `saveAdditional()` for robust save-game persistence.

---

### Version 1.8.37 (Pipe Texture Markings Cleanup & 1/8 Clear Sight-Glass Strips)
- **Pipe Texture Markings Cleanup**:
  - Removed circular flange and bolt emblem markings that were baked into the pipe textures by `PipeAssetGenerator.java`.
  - Regenerated all 12 pipe textures with clean, uninterrupted cylindrical metal shading.
- **True 1/8-Diameter Clear Sight-Glass Inspection Strips**:
  - Engineered dedicated longitudinal apertures on all 4 cardinal faces of pipe cylinders of width exactly $\frac{1}{8}$ of the pipe diameter ($0.050$ blocks wide).
  - Metal walls now terminate cleanly at the strip borders, allowing an unobstructed line of sight straight into the hollow pipe interior without opaque depth buffer occlusion.
  - Across the apertures, translucent sight-glass quads with metallic bevel edge frames (`pipe_window.png`) render flush with the pipe surface with zero z-fighting and no protruding ridges.
  - Live traveling items and colored fluid levels are now 100% visible moving and flowing through the pipe from all viewing angles.

---

### Version 1.8.36 (Pipe Connection Persistence Across Restarts & Snapping)
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

### Version 1.8.35 (Multiblock Storage Tank Auto-Export & Pipe Connectivity)
- **Multiblock Storage Tank Auto-Export & Pipe Connectivity**:
  - Fixed an issue where multiblock liquid storage tanks failed to auto-export fluids to connected pipes even though fluid reception via the top fill valve functioned properly.
  - Resolved an internal back-drain loop on the drain valve (`STORAGE_TANK_DRAIN_VALVE_BE`): replaced the bidirectional handler with an extract-only handler (`getExportOnlyHandler()`) that rejects fluid insertion (`isValid = false`, `insert = 0`). Connected pipes receiving pushed fluid can no longer immediately pump the fluid backward into the tank, allowing fluid to freely flow downstream.
  - Registered `Capabilities.Fluid.BLOCK` on all multiblock tank casing blocks (`STORAGE_TANK_CASING`), delegating fluid extraction to the assembled tank controller.
  - Updated `IndustrialPipeBlockEntity` connection detection to recognize block-level capabilities on blocks without block entities (such as `StorageTankCasingBlock`), allowing fluid pipes to cleanly connect with visible 3D coupling arms to any casing face around the tank.
  - Expanded `StorageTankBlockEntity.pushFluidToNeighbors()` from checking solely beneath the bottom center drain valve to scanning all exterior adjacent block positions around the entire tank floor and perimeter walls, caching valid fluid handlers and pushing up to 1,000 mB/t to any connected pipe network.
  - Added instantaneous connection refresh on `StorageTankCasingBlock` and `StorageTankDrainValveBlock` neighbor changes so newly placed pipes immediately receive exported fluid without delay.

---

### Version 1.8.34 (Boiler Redstone Shutdown & Meltdown Prevention)
- **Boiler Redstone Combustion Control & Overheat Safety**:
  - Fixed a safety bug where solid fuel boilers (`SmallBoilerControllerBlockEntity`, `LargeBoilerControllerBlockEntity`) remained active after their redstone signal was turned off, leading to unchecked overheating and catastrophic explosions when water was depleted.
  - Active boiling and steam generation now strictly require an active redstone signal in addition to burning fuel. When redstone power is removed, active boiling ceases immediately, halting water consumption and steam production.
  - Boilers without redstone power or fuel automatically enter idle cooldown: firebox flames extinguish, active blockstates disable, and any accumulated overheat ticks steadily dissipate.
  - Diesel boilers (`SmallDieselBoilerControllerBlockEntity`, `LargeDieselBoilerControllerBlockEntity`) feature accelerated cooldown dissipation and complete firebox flame deactivation when unpowered.
  - Enhanced multiblock redstone connectivity (`hasRedstone()`): all boilers now check for redstone power across the controller block as well as all multiblock structure blocks and ports (water input, fuel input, steam output), enabling full remote control from anywhere on the structure.
  - Synchronized boiler GUI status, hearth flame viewports, and redstone indicator lamps with real-time machine power state.

---

### Version 1.8.33 (Continuous 3D Surface-Mounted Pipe Wires)
- **Continuous 3D Surface-Mounted Pipe Wires**:
  - Replaced the floating 2D wire item icons with continuous 3D surface-mounted wire conduits in `PipeWireEntityRenderer`.
  - When pipe wires (Red, Blue, Green, Yellow) are attached to adjacent pipes in a network, the wires now dynamically trace along the outer cylinder of the pipes and connect seamlessly across pipe block boundaries without gaps.
  - Dedicated 4-Quadrant Conduit Geometry: Each of the 4 wire colors occupies a dedicated diagonal quadrant along the pipe's outer cylinder, allowing all 4 wire lines (Red, Blue, Green, Yellow) to run parallel along the same pipe simultaneously without colliding with each other or occluding gate faces and sight glass strips.
  - 3D Wire Hub Collars & Corner Routing: Smoothly connects multi-way branch intersections, right-angle pipe corners, and attached gate terminal bases.
  - Signal Illumination: Active wire signals emit vibrant full-bright glowing neon light (`0x00F000F0`).

---

### Version 1.8.32 (Sloped Conveyor Belt Item Ascent & Collision Fix)
- **Sloped Conveyor Ascent & Friction Resolution**:
  - Replaced the rigid $16\times 16\times 16$ solid block collision shape in `SlopedConveyorBeltBlock` with directional 4-tier stepped collision geometry for players/mobs and `Shapes.empty()` for `ItemEntity`. Items now glide onto and up the slope without being blocked by vertical block walls.
  - Locked item vertical positioning directly to the inclined surface height (`targetY = yEntry + t * 1.0`), preventing gravity from pulling items downward beneath the conveyor.
  - Implemented 45-degree upward velocity matching the belt speed so client-side interpolation renders smooth 60fps item transit.
  - Updated `ConveyorBeltBlockEntity` and `RightAngleConveyorBeltBlockEntity` boundary checks to recognize `SlopedConveyorBeltBlock` (both directly adjacent and stepped below) so items are handed off smoothly into slopes instead of attempting container insertion.

---

### Version 1.8.31 (Industrial Grinder Hopper Bounding & Conveyor Export Validation)
- **Industrial Grinder Hopper Bounding Box Containment**:
  - Tightly bounded the top feed hopper collection box (`hopperBox`) in `IndustrialGrinderControllerBlockEntity` to strictly cover the open funnel mouth ($Y \in [2.20, 3.60]$ above base, horizontal radius $0.40$ blocks centered at $(0, 2, 1)$).
  - Eliminated the oversized bounding volume inflation that previously allowed the input hopper to reach downward into $Y=1..0$ and rightward into $X=1$, which sucked items right back off the output hopper, output chute, and discharge conveyor.
  - Cleaned up export targets to remove redundant controller block positions, ensuring auto-export exclusively feeds from the output port and right-hand discharge chute.
- **Output Conveyor Downstream Connection Validation**:
  - The Industrial Grinder now verifies downstream connectivity before discharging products onto an adjacent conveyor belt (`ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`).
  - Output items will only export onto an adjacent conveyor if that conveyor is actively connected to another conveyor, an item pipe (`IndustrialPipeBlock` / `IndustrialPipeBlockEntity`), or a receiving machine/container. If a conveyor line terminates in open air without a recipient, items remain safely stored inside the grinder's internal output slots.
  - Added belt vacancy detection: the grinder emits 1 item at a time onto the receiving conveyor belt only when the receiving belt section is clear, preventing items from stacking up in excessive entity clusters.

---

### Version 1.8.30 (Fluid Pump Back-Draining & Pipe Leak Resolution)
- **Fluid Pump Back-Drain & Extraction-Only Output**:
  - The pumped fluid output reservoir across all pump variants (`AbstractFluidPumpBlockEntity`, `ElectricFluidPumpBlockEntity`, `DieselFluidPumpBlockEntity`, `CoalFluidPumpBlockEntity`) is now strictly extract-only (`insert = 0`, `isValid = false`).
  - Pipes can freely pull or receive fluid pumped from the reservoir, but can never push fluid backward into the pump.
  - Fixes continuous fluid cycling between pipes and the pump that previously caused the Electric Fluid Pump (`refinery:electric_fluid_pump`) to burn 400 FE every second even when the pipe network was full.
  - Decremented `pumpCooldown` cleanly every tick regardless of current tank level, allowing the pump to sit quietly in `[TANK FULL]` with zero energy consumption when output pipes are backlogged.
  - Added `onEnergyChanged()` callback in `ElectricFluidPumpBlockEntity.energyStorage` so the internal buffer syncs immediately upon receiving energy from power cables and grid handlers, and increased `maxInsert` to 1,000 FE/t.
  - Resolved `ClassCastException` in `DieselFluidPumpBlockEntity.getFluidHandler` and added a dedicated `DieselFuelHandler` supporting pipe insertion of Diesel, Gasoline, and Ethanol fuels into the engine tank.

---

### Version 1.8.29 (Automated Planter Sapling Spacing & Gate Placement Snapping)
- **Automated Planter Sapling Spacing**:
  - The Automated Planter (`refinery:auto_planter`) now naturally spaces tree saplings so no sapling is placed directly adjacent to another sapling or tree trunk.
  - When planting saplings, the planter verifies horizontal Chebyshev adjacency ($\Delta x \in [-1, 1], \Delta z \in [-1, 1], \Delta y \in [-1, 3]$) to ensure no other `SaplingBlock`, `#minecraft:saplings`, or `#minecraft:logs` is directly neighboring the planting spot.
  - Standard farmland crops (wheat, carrots, potatoes, beetroots, melon/pumpkin stems) continue to be densely planted across all farmland blocks without restriction.
- **Pipe Gate Placement Snapping on Occupied Faces**:
  - Clicking on a pipe face that is already occupied by an attached gate automatically redirects to the closest open, unoccupied radial face facing the player instead of failing to place.

---

### Version 1.8.28 (Pipe Sight-Glass Inspection Strips & Flow Visualization)
- **Longitudinal Sight-Glass Inspection Strips**:
  - Connected pipe branches now feature clean, beveled sight-glass inspection strips along their length.
  - Items in transit travel directly down the pipe axis and are clearly visible through the glass window.
  - Active fluid pipes dynamically render a colored internal liquid core matching stored liquids (Water, Lava, Crude Oil, Diesel, Gasoline, Ethanol, Steam) based on current fill capacity.

---

### Version 1.8.27 (Pipe Gate 3D Saddle Orientation & Radial Face Snapping)
- **Pipe Gate 3D Saddle Orientation (`WEST`, `EAST`, `UP`, `DOWN`)**:
  - Corrected right-handed JOML rotations in `PipeGateEntityRenderer` (`WEST = 270°`, `EAST = 90°`, `UP = 270°`, `DOWN = 90°`) so the front gate plate (`+Z`) always faces outward and the concave saddle (`-Z`) always hugs the pipe cylinder inward across all 6 faces.
  - Unified local-X / local-Y pipe axis detection so gates on vertical pipes, horizontal pipes, and T-junctions always curve across the cylinder axis.
- **Smart Radial Face Snapping**:
  - Clicking a pipe's end-cap or connected arm with a Gate automatically snaps the gate onto the nearest open radial side of the cylinder facing the player, and existing gates placed on end-caps auto-correct on tick.

---

### Version 1.8.26 (Diamond Pipe 3D Color-Coded Connection Bands & Whitelist GUI Legend)
- **3D Color-Coded Connection Bands on Diamond Pipes**:
  - Every connected arm of a Diamond Pipe (`refinery:diamond_item_pipe` and `refinery:diamond_fluid_pipe`) now renders a 3D color-coded collar band at its connection end corresponding 1-to-1 with the whitelist filter chart:
    - **DOWN**: Green (`#22C55E`) | **UP**: Yellow (`#EAB308`) | **NORTH**: Blue (`#3B82F6`) | **SOUTH**: Red (`#EF4444`) | **WEST**: Purple (`#A855F7`) | **EAST**: White (`#F8FAFC`)
- **Enhanced Whitelist Chart GUI (`DiamondPipeScreen`)**:
  - Added color-coded row borders, subtle row slot tints, side direction badge tabs (`DOWN`, `UP`, `NORTH`, `SOUTH`, `WEST`, `EAST`) with matching color swatches, and hover tooltips.

---

### Version 1.8.25 (Minecraft 26.3 GUI Mouse Button Fix & Text Alpha Visibility)
- **Minecraft 26.3 GUI Mouse Click Handling (`InputConstants.MOUSE_BUTTON_LEFT = 1`)**:
  - Fixed unresponsive GUI buttons in `AutoHarvesterScreen`, `AutoPlanterScreen`, `CoalFluidPumpScreen`, `DieselFluidPumpScreen`, `ElectricFluidPumpScreen`, `StorageTankScreen`, and `GateScreen`.
  - Updated all custom screen `mouseClicked` handlers for Minecraft 26.3's 1-indexed mouse button constants (`InputConstants.MOUSE_BUTTON_LEFT = 1`, `InputConstants.MOUSE_BUTTON_RIGHT = 3`).
- **GUI Text Alpha Visibility & Texture Cleanup**:
  - Added full opaque alpha (`0xFF000000`) to all 24-bit RGB text colors across all custom machine screens and Gate trigger/action labels so status badges and labels render visibly in Minecraft 26.3.
  - Positioned Harvester and Planter live status labels cleanly beneath the coverage area readout and removed a stray dark box artifact behind the radius `[+]` button on `auto_harvester.png` and `auto_planter.png`.

---

### Version 1.8.24 (Auto Harvester GUI Synchronization & Gate Saddle Alignment)
- **Auto Harvester Client GUI Synchronization**:
  - Fully merged and stabilized PR #1 (`GUI-Fix`), ensuring instantaneous client-to-server updates when toggling power or clicking radius buttons in the Auto Harvester menu.
  - Eliminated duplicate packet/tag registration methods in `AutoHarvesterBlockEntity` to ensure clean build compilation.
- **Pipe Gate Rotation & Saddle Alignment**:
  - Corrected `PipeGateEntityRenderer` rotational mappings for `WEST` (90°), `EAST` (270°), `UP` (90°), and `DOWN` (270°) faces.
  - Curved gate clamp plates wrap flush and securely around 12-sided pipe cylinders without bending backwards into open air.
### Version 1.8.24 (Complete In-Game Localization & Translation Key Fix)
- **Complete In-Game Localization & Translation Key Resolution (n_us.json)**:
  - Fixed an issue where multiple items, blocks, and tooltips displayed raw registry/variable keys (such as item.refinery.coal_dust and 	ooltip.refinery.coal_dust.desc) in GUI containers and player inventories.
  - Added full user-facing item names and informative tooltip descriptions for all materials and metallurgy items:
    - **Dusts**: Coal Dust, Iron Dust, Gold Dust, Copper Dust, Tin Dust, Bronze Dust.
    - **Ingots & Plates**: Steel Ingot & Plate, Tin Ingot & Plate, Bronze Ingot & Plate, Iron Plate, Copper Plate, Gold Plate.
    - **Hydrocarbons & Upgrades**: Refined Bitumen, Autarchic Chipset, and Red/Blue/Green/Yellow Pipe Wires.
  - Added comprehensive localization for all advanced multiblock machinery and structures:
    - **Industrial Blast Furnace Suite**: Controller, Casing, Foundry Heat-Resistant Glass, Foundry Input & Output Ports.
    - **Steam Turbine Generator Suite**: Controller, Casing, Rotor Shaft, Steam Input, Water Output, and Energy Output Ports.
    - **Electric Arc Melter Suite**: Controller, Casing, Heating Chamber, Input & Output Ports.
    - **Continuous Casting Suite**: Controller, Casing, Mold Basin, Safety Chamber, Refractory Casting Channel, Input & Output Ports.
    - **Multi-Smelter & Fermenter**: Multi-Smelter Controller, Casing, Chamber, Ports, and Industrial Fermenter.
  - Added localized names for all fluid types, fluid blocks, and buckets (Crude Oil, Steam, Molten Iron/Gold/Copper/Tin/Bronze/Glass, Ethanol) and GUI container titles.
  - Cleaned up duplicate client network sync methods in AutoHarvesterBlockEntity.


---

### Version 1.8.23 (Quartz Merge Pipe Suite & Selectable Output Routing)
- **Quartz Merge Pipe (`refinery:merge_pipe`, `refinery:merge_item_pipe`, `refinery:merge_fluid_pipe`)**:
  - Added new Quartz-tiered Merger Pipe suite designed to funnel multiple separate transport lines into a single designated outlet.
  - **Selectable Output & Dedicated Inputs**:
    - Right-clicking any face (with an empty hand or wrench) designates that face as the active **OUTPUT**. All other connected branches automatically become unidirectional **INPUTS**.
    - Clicking the active output face cycles through all connected branches, accompanied by an action bar notification and audio feedback.
    - Defaults to the facing direction away from the player when placed.
    - **Visual Output Indicator**: Renders an illuminated golden collar band around the active output arm for instant in-world identification.
  - **Merging Mechanics for Items & Fluids**:
    - Any items arriving from input branches are routed strictly into the designated output direction without diversion or bounce-back.
    - Fluids entering the merge pipe are actively pumped downstream strictly into the designated output connection, preventing any fluid from flowing backward into input branches.
  - **Crafting Recipes & Waterproofing Conversion**:
    - Base recipe: `[Quartz, Glass, Quartz]` (`minecraft:quartz`, `minecraft:glass`, `minecraft:quartz`) in a crafting table yields **10x Merge Pipes** (`refinery:merge_pipe` / `refinery:merge_item_pipe`).
    - Waterproofing: Placing any Merge Item Pipe with **Tar** (`refinery:tar`) or a **Slime Ball** (`minecraft:slime_ball`) transforms it into a **Merge Fluid Pipe** (`refinery:merge_fluid_pipe`).

---

### Version 1.8.22 (Void Pipe Shaped Crafting Parity & Gate Curvature Fix)
- **Void Pipe Crafting & Instant Annihilation Logic**:
  - Added shaped crafting recipe: `[Lava Bucket, Glass, Lava Bucket]` (`minecraft:lava_bucket`, `minecraft:glass`, `minecraft:lava_bucket`) producing **10x Void Pipes** (`refinery:void_pipe`). Standard empty buckets remain in the crafting grid upon recipe completion.
  - Upgraded Void Pipe fluid handling with an un-capped instant voiding capability handler that absorbs 100% of inserted fluid volumes without buffer limits or backlog.
  - Enforced zero-delay item destruction: items inserted via containers/hoppers or arriving from traveling pipe routes are immediately consumed and cleared from memory.
- **Pipe Gate Rotation & Curvature Correction**:
  - Corrected `WEST` (270°) and `EAST` (90°) rotation in `PipeGateEntityRenderer`, guaranteeing that gate saddle clamp plates wrap tightly around the pipe cylinder rather than bulging inside-out.

---

### Version 1.8.21 (Pipe Gate Orientation, Extended Dead-Ends, & Conveyor Occlusion Fix)
- **Pipe Gate Saddle Hugging & Rotation Fix**:
  - Corrected `PipeGateEntityRenderer` rotational mappings for `EAST` (270°) and `WEST` (90°) faces. Gate saddle clamp plates now hug the pipe circumference seamlessly rather than flaring outwards/inverted into the air.
  - Extended single-arm dead-end pipe cylinders in `IndustrialPipeModel` to `0.80f` (and from `0.20f`), matching standalone pipe geometry and ensuring gates centered at `0.50f` are fully supported without hanging off the pipe end.
- **Conveyor Belt & Pipe Floor Occlusion Resolution**:
  - Overrode `getOcclusionShape(BlockState)` to return `Shapes.empty()` and `useShapeForLightOcclusion(BlockState)` to return `false` on `ConveyorBeltBlock`, `SlopedConveyorBeltBlock`, `RightAngleConveyorBeltBlock`, and `IndustrialPipeBlock`. Completely prevents chunk meshing from culling the top faces of floor blocks underneath conveyor belts and pipes.
- **Sloped Conveyor Connection Snapping & Roller Alignment**:
  - Fixed `ConveyorBeltBlock.updateShape` to properly evaluate `isBeltMatchingFront` and `isBeltMatchingBack` with `LevelReader`, keeping horizontal conveyors connected to sloped conveyors without retracting their belts or leaving un-animated gaps.
  - Lowered roller centers in `SlopedConveyorBeltModel` (`yEntry - 0.04f`, `yExit - 0.04f`) so rollers sit cleanly beneath the belt rather than protruding through the top carrying surface.

---

### Version 1.8.20 (Tiered Pipe Suite, Insertion, Sorting, Round-Robin, & Waterproofing)
- **Full Tiered Pipe Suite (Wood, Copper, Iron, Steel, Gold, Diamond, Emerald)**:
  - **Wood Pipes (`refinery:wood_item_pipe`, `refinery:wood_fluid_pipe`)**: Function as extraction pipes that actively pull items and fluids from connected chests, barrels, and external machines.
  - **Copper, Iron, and Steel Progression**: Iron and Steel pipes significantly boost transport speed (1.75x and 2.5x) and fluid bandwidth (1,000 mB/t and 2,000 mB/t).
  - **Gold Pipes (`refinery:gold_item_pipe`, `refinery:gold_fluid_pipe`)**: Function as **Insertion Pipes**: prioritizes adding items into connected machine/container inventories until completely full before routing items down the line.
  - **Diamond Pipes (`refinery:diamond_item_pipe`, `refinery:diamond_fluid_pipe`)**: Function as **Sorting Pipes**: interactive 54-slot GUI with 9 filter slots per face (Down, Up, North, South, West, East). Directs matching items to filtered directions, using unconfigured lines for overflow/default paths.
  - **Emerald Pipes (`refinery:emerald_item_pipe`)**: Functions as a **Round-Robin Item Pipe**: distributes items cyclically and equally across all connected outgoing branches.
- **Crafting & Waterproofing System**:
  - `[Material, Glass, Material]` in crafting tables produces **10x Item Pipes** across all materials.
  - Placing any Item Pipe into crafting with **Tar** or a **Slime Ball** converts it into its corresponding Fluid Pipe.
  - Mod machines are pre-configured to auto-work and auto-eject into any connected pipe or container without requiring extraction pipes.

---

### Version 1.8.19 (Conveyor Occlusion, Gate Saddle Hugging, & Sloped Conveyor Alignment)
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




