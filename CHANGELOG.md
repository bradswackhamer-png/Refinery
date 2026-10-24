# Changelog — Industrial Refinery

All notable changes to the **Industrial Refinery** mod are documented in this file.

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
