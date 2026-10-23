# Industrial Refinery Mod

A high-depth 3x3x3 multiblock petroleum refinery mod built for **Minecraft 26.3** and **NeoForge 26.3.0.48-beta**, designed to integrate seamlessly with the **Logistics Automation** mod.

---

## Features

- **3x3x3 Industrial Multiblock Architecture**:
  - Distinct block components with authentic model depth: recessed louvers, beveled frames, brass dials, valve hand-wheels, and cylindrical distillation columns with support struts.
  - Not just a large plain cube — every face features industrial detail and functional components.

- **Interactive 3D Holographic Blueprint Projector**:
  - Place down the **Refinery Controller** block first.
  - Right-click (or Sneak + Right-Click) the Controller with the **Refinery Wrench** or empty hand.
  - If incomplete, the Controller activates **Blueprint Projection Mode** for 30 seconds, projecting colored particle markers into the world showing where each component goes!
  - Sends diagnostic reports to chat showing missing components layer by layer.
  - Instantly forms upon completion with a clank sound, steam burst, and state updates.

- **Dual Power Engine**:
  - **FE / RF Compatible**: Connects to Logistics power cables and engines (stores up to 100,000 FE).
  - **Combustion Firebox**: Has an integrated burner chamber that can consume solid fuels (Coal, Charcoal, Coal Blocks, Lava Buckets, Tar) to generate heat internally!
  - When burning fuel, the Combustion Chamber glows with light level 13 and animated furnace fire.

- **Directional Fluid & Item Ports**:
  - **Crude Oil Input Port**: Connects directly to Logistics fluid pipes to pump Crude Oil into the internal 16,000 mB tank.
  - **Diesel Output Port**: Collects refined Diesel fuel (golden amber liquid).
  - **Gas Output Port**: Collects refined Petroleum Gas / Gasoline (cyan vaporous liquid).
  - **Byproduct Output Port**: Auto-extractable item port that ejects solid **Petroleum Tar** and **Sulfur Dust**.

- **Refining Distillation Cycle**:
  - Input: 200 mB Crude Oil + 40 FE/t (or active firebox burn)
  - Output: 120 mB Diesel + 60 mB Petroleum Gas
  - Byproducts:
    - 40% chance of **Petroleum Tar** (burnable fuel, torch crafting)
    - 30% chance of **Sulfur Dust** (gunpowder crafting, chemical reagent)
  - Distillation column emits realistic steam and condensation vapor during active refining.

---

## 3x3x3 Blueprint Layout

*(All positions relative to the Refinery Controller placed at Layer 0 front center)*

### Layer 0 (Bottom)
```
[ Casing ]        [ Controller ]        [ Casing ]
[ Crude Oil Port ][ Combustion Chamber ][ Byproduct Port ]
[ Casing ]        [ Casing ]            [ Casing ]
```

### Layer 1 (Middle)
```
[ Casing ]        [ Casing ]             [ Casing ]
[ Heat Exchanger ][ Distillation Column ][ Heat Exchanger ]
[ Casing ]        [ Diesel Output Port ] [ Casing ]
```

### Layer 2 (Top)
```
[ Casing ]        [ Casing ]             [ Casing ]
[ Casing ]        [ Distillation Column ][ Casing ]
[ Casing ]        [ Gas Output Port ]    [ Casing ]
```

---

## Block & Item Count Checklist

- 1x **Refinery Controller**
- 1x **Refinery Combustion Chamber**
- 2x **Distillation Column**
- 2x **Refinery Heat Exchanger**
- 1x **Crude Oil Input Port**
- 1x **Diesel Output Port**
- 1x **Gas Output Port**
- 1x **Byproduct Output Port**
- 17x **Reinforced Refinery Casing**
- 1x **Refinery Wrench**
