package com.refinery.gate;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

public enum GateTrigger {
    // Ordinals 0 to 22 (Preserved for world save backward-compatibility)
    NONE("None", "No trigger selected", 0x888888, "None"),
    REDSTONE_ON("Redstone ON", "Gate receives redstone power > 0", 0xFF3333, "Red ON"),
    REDSTONE_OFF("Redstone OFF", "Gate has no redstone power", 0x773333, "Red OFF"),
    INV_EMPTY("Inventory Empty", "Connected container is completely empty (0%)", 0xAAAAAA, "Inv Empty"),
    INV_HAS_ITEMS("Has Items", "Connected container contains at least 1 item (> 0%)", 0x55FF55, "Has Items"),
    INV_SPACE("Space in Inventory", "Connected container has room for more items (< 100%)", 0x55AAFF, "Inv Space"),
    INV_FULL("Inventory Full", "Connected container has zero free slots or space (100%)", 0xFFAA00, "Inv Full"),
    TANK_EMPTY("Tank Empty", "Connected tank has 0 mB fluid (0%)", 0xAAAAAA, "Tank Empty"),
    TANK_HAS_FLUID("Has Fluid", "Connected tank contains fluid (> 0%)", 0x33AAFF, "Has Fluid"),
    TANK_SPACE("Space in Tank", "Connected tank has room for more fluid (< 100%)", 0x55FFFF, "Tank Space"),
    TANK_FULL("Tank Full", "Connected tank is at max capacity (100%)", 0xFFAA00, "Tank Full"),
    PIPE_EMPTY("Pipe Empty", "No items or fluids traveling in pipe", 0xAAAAAA, "Pipe Empty"),
    PIPE_FLOWING("Pipe Flowing", "Items or fluids currently traveling in pipe", 0x55FF55, "Flowing"),
    MACHINE_ACTIVE("Machine Active", "Connected machine is running/operating", 0xFF5533, "Active"),
    MACHINE_IDLE("Machine Idle", "Connected machine is idle/unpowered", 0x888888, "Idle"),
    WIRE_RED_ON("Red Wire ON", "Red pipe wire signal is active", 0xFF2222, "Red ON"),
    WIRE_RED_OFF("Red Wire OFF", "Red pipe wire signal is inactive", 0x771111, "Red OFF"),
    WIRE_BLUE_ON("Blue Wire ON", "Blue pipe wire signal is active", 0x2266FF, "Blu ON"),
    WIRE_BLUE_OFF("Blue Wire OFF", "Blue pipe wire signal is inactive", 0x113377, "Blu OFF"),
    WIRE_GREEN_ON("Green Wire ON", "Green pipe wire signal is active", 0x22DD22, "Grn ON"),
    WIRE_GREEN_OFF("Green Wire OFF", "Green pipe wire signal is inactive", 0x116611, "Grn OFF"),
    WIRE_YELLOW_ON("Yellow Wire ON", "Yellow pipe wire signal is active", 0xFFFF22, "Yel ON"),
    WIRE_YELLOW_OFF("Yellow Wire OFF", "Yellow pipe wire signal is inactive", 0x777711, "Yel OFF"),

    // Ordinals 23 to 40: Tank Threshold Triggers (Preset values 0, 25, 50, 75, 100)
    TANK_GE_0("Tank >= 0%", "Connected tank has at least 0% fluid (tank present)", 0x33AAFF, "Tank>=0%"),
    TANK_GE_25("Tank >= 25%", "Connected tank is at least 25% full", 0x44BBDD, "Tank>=25%"),
    TANK_GE_50("Tank >= 50%", "Connected tank is at least 50% full", 0x55CCEE, "Tank>=50%"),
    TANK_GE_75("Tank >= 75%", "Connected tank is at least 75% full", 0x66DDFF, "Tank>=75%"),
    TANK_GE_100("Tank >= 100%", "Connected tank is 100% full", 0xFFAA00, "Tank>=100%"),

    TANK_GT_0("Tank > 0%", "Connected tank has more than 0% fluid", 0x33AAFF, "Tank>0%"),
    TANK_GT_25("Tank > 25%", "Connected tank has more than 25% fluid", 0x44BBDD, "Tank>25%"),
    TANK_GT_50("Tank > 50%", "Connected tank has more than 50% fluid", 0x55CCEE, "Tank>50%"),
    TANK_GT_75("Tank > 75%", "Connected tank has more than 75% fluid", 0x66DDFF, "Tank>75%"),

    TANK_LE_0("Tank <= 0%", "Connected tank has 0% fluid (empty)", 0xAAAAAA, "Tank<=0%"),
    TANK_LE_25("Tank <= 25%", "Connected tank is 25% full or less", 0x44BBDD, "Tank<=25%"),
    TANK_LE_50("Tank <= 50%", "Connected tank is 50% full or less", 0x55CCEE, "Tank<=50%"),
    TANK_LE_75("Tank <= 75%", "Connected tank is 75% full or less", 0x66DDFF, "Tank<=75%"),
    TANK_LE_100("Tank <= 100%", "Connected tank is 100% full or less (tank present)", 0xFFAA00, "Tank<=100%"),

    TANK_LT_25("Tank < 25%", "Connected tank has less than 25% fluid", 0x44BBDD, "Tank<25%"),
    TANK_LT_50("Tank < 50%", "Connected tank has less than 50% fluid", 0x55CCEE, "Tank<50%"),
    TANK_LT_75("Tank < 75%", "Connected tank has less than 75% fluid", 0x66DDFF, "Tank<75%"),
    TANK_LT_100("Tank < 100%", "Connected tank has less than 100% fluid (space available)", 0x55FFFF, "Tank<100%"),

    // Ordinals 41 to 58: Inventory Threshold Triggers (Preset values 0, 25, 50, 75, 100)
    INV_GE_0("Inv >= 0%", "Connected container has at least 0% items (container present)", 0x55FF55, "Inv>=0%"),
    INV_GE_25("Inv >= 25%", "Connected container is at least 25% full", 0x66FFAA, "Inv>=25%"),
    INV_GE_50("Inv >= 50%", "Connected container is at least 50% full", 0x77FFCC, "Inv>=50%"),
    INV_GE_75("Inv >= 75%", "Connected container is at least 75% full", 0x88FFEE, "Inv>=75%"),
    INV_GE_100("Inv >= 100%", "Connected container is 100% full", 0xFFAA00, "Inv>=100%"),

    INV_GT_0("Inv > 0%", "Connected container has more than 0% items", 0x55FF55, "Inv>0%"),
    INV_GT_25("Inv > 25%", "Connected container has more than 25% items", 0x66FFAA, "Inv>25%"),
    INV_GT_50("Inv > 50%", "Connected container has more than 50% items", 0x77FFCC, "Inv>50%"),
    INV_GT_75("Inv > 75%", "Connected container has more than 75% items", 0x88FFEE, "Inv>75%"),

    INV_LE_0("Inv <= 0%", "Connected container has 0% items (empty)", 0xAAAAAA, "Inv<=0%"),
    INV_LE_25("Inv <= 25%", "Connected container is 25% full or less", 0x66FFAA, "Inv<=25%"),
    INV_LE_50("Inv <= 50%", "Connected container is 50% full or less", 0x77FFCC, "Inv<=50%"),
    INV_LE_75("Inv <= 75%", "Connected container is 75% full or less", 0x88FFEE, "Inv<=75%"),
    INV_LE_100("Inv <= 100%", "Connected container is 100% full or less (container present)", 0xFFAA00, "Inv<=100%"),

    INV_LT_25("Inv < 25%", "Connected container has less than 25% items", 0x66FFAA, "Inv<25%"),
    INV_LT_50("Inv < 50%", "Connected container has less than 50% items", 0x77FFCC, "Inv<50%"),
    INV_LT_75("Inv < 75%", "Connected container has less than 75% items", 0x88FFEE, "Inv<75%"),
    INV_LT_100("Inv < 100%", "Connected container has less than 100% items (space available)", 0x55AAFF, "Inv<100%");

    private final String label;
    private final String description;
    private final int displayColor;
    private final String shortLabel;

    GateTrigger(String label, String description, int displayColor, String shortLabel) {
        this.label = label;
        this.description = description;
        this.displayColor = 0xFF000000 | displayColor;
        this.shortLabel = shortLabel;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public boolean isWireTrigger() {
        return this.name().startsWith("WIRE_");
    }

    public boolean isTankTrigger() {
        return this.name().startsWith("TANK_");
    }

    public boolean isInventoryTrigger() {
        return this.name().startsWith("INV_");
    }

    public PipeWireColor getWireColor() {
        if (this == WIRE_RED_ON || this == WIRE_RED_OFF) return PipeWireColor.RED;
        if (this == WIRE_BLUE_ON || this == WIRE_BLUE_OFF) return PipeWireColor.BLUE;
        if (this == WIRE_GREEN_ON || this == WIRE_GREEN_OFF) return PipeWireColor.GREEN;
        if (this == WIRE_YELLOW_ON || this == WIRE_YELLOW_OFF) return PipeWireColor.YELLOW;
        return null;
    }

    public boolean isValidForTier(GateTier tier) {
        if (!isWireTrigger()) return true;
        PipeWireColor color = getWireColor();
        return color != null && tier.supportsWire(color);
    }

    // Natural grouped order for in-game cycling
    public static final GateTrigger[] CYCLE_ORDER = {
        NONE,
        REDSTONE_ON,
        REDSTONE_OFF,
        // Inventory
        INV_EMPTY,
        INV_HAS_ITEMS,
        INV_SPACE,
        INV_FULL,
        INV_GE_0,
        INV_GT_0,
        INV_GE_25,
        INV_GT_25,
        INV_GE_50,
        INV_GT_50,
        INV_GE_75,
        INV_GT_75,
        INV_GE_100,
        INV_LE_0,
        INV_LT_25,
        INV_LE_25,
        INV_LT_50,
        INV_LE_50,
        INV_LT_75,
        INV_LE_75,
        INV_LT_100,
        INV_LE_100,
        // Tank
        TANK_EMPTY,
        TANK_HAS_FLUID,
        TANK_SPACE,
        TANK_FULL,
        TANK_GE_0,
        TANK_GT_0,
        TANK_GE_25,
        TANK_GT_25,
        TANK_GE_50,
        TANK_GT_50,
        TANK_GE_75,
        TANK_GT_75,
        TANK_GE_100,
        TANK_LE_0,
        TANK_LT_25,
        TANK_LE_25,
        TANK_LT_50,
        TANK_LE_50,
        TANK_LT_75,
        TANK_LE_75,
        TANK_LT_100,
        TANK_LE_100,
        // Pipe
        PIPE_EMPTY,
        PIPE_FLOWING,
        // Machine
        MACHINE_ACTIVE,
        MACHINE_IDLE,
        // Wires
        WIRE_RED_ON,
        WIRE_RED_OFF,
        WIRE_BLUE_ON,
        WIRE_BLUE_OFF,
        WIRE_GREEN_ON,
        WIRE_GREEN_OFF,
        WIRE_YELLOW_ON,
        WIRE_YELLOW_OFF
    };

    public enum Category {
        NONE,
        REDSTONE,
        INVENTORY,
        TANK,
        PIPE,
        MACHINE,
        WIRE
    }

    public Category getCategory() {
        if (this == NONE) return Category.NONE;
        if (this == REDSTONE_ON || this == REDSTONE_OFF) return Category.REDSTONE;
        if (isInventoryTrigger()) return Category.INVENTORY;
        if (isTankTrigger()) return Category.TANK;
        if (this == PIPE_EMPTY || this == PIPE_FLOWING) return Category.PIPE;
        if (this == MACHINE_ACTIVE || this == MACHINE_IDLE) return Category.MACHINE;
        if (isWireTrigger()) return Category.WIRE;
        return Category.NONE;
    }

    public static GateTrigger getFirstInCategory(Category cat, GateTier tier) {
        return switch (cat) {
            case NONE -> NONE;
            case REDSTONE -> REDSTONE_ON;
            case INVENTORY -> INV_EMPTY;
            case TANK -> TANK_EMPTY;
            case PIPE -> PIPE_EMPTY;
            case MACHINE -> MACHINE_ACTIVE;
            case WIRE -> tier.getWireCount() > 0 ? WIRE_RED_ON : NONE;
        };
    }

    public GateTrigger nextCategory(GateTier tier) {
        Category[] cats = Category.values();
        int curCatIdx = getCategory().ordinal();
        for (int i = 1; i <= cats.length; i++) {
            Category nextCat = cats[(curCatIdx + i) % cats.length];
            if (nextCat == Category.WIRE && tier.getWireCount() == 0) continue;
            GateTrigger first = getFirstInCategory(nextCat, tier);
            if (first.isValidForTier(tier)) return first;
        }
        return NONE;
    }

    public GateTrigger previousCategory(GateTier tier) {
        Category[] cats = Category.values();
        int curCatIdx = getCategory().ordinal();
        for (int i = 1; i <= cats.length; i++) {
            Category prevCat = cats[(curCatIdx - i + cats.length) % cats.length];
            if (prevCat == Category.WIRE && tier.getWireCount() == 0) continue;
            GateTrigger first = getFirstInCategory(prevCat, tier);
            if (first.isValidForTier(tier)) return first;
        }
        return NONE;
    }

    public GateTrigger next(GateTier tier) {
        int idx = 0;
        for (int i = 0; i < CYCLE_ORDER.length; i++) {
            if (CYCLE_ORDER[i] == this) {
                idx = i;
                break;
            }
        }
        for (int i = 1; i <= CYCLE_ORDER.length; i++) {
            GateTrigger next = CYCLE_ORDER[(idx + i) % CYCLE_ORDER.length];
            if (next.isValidForTier(tier)) {
                return next;
            }
        }
        return NONE;
    }

    public GateTrigger previous(GateTier tier) {
        int idx = 0;
        for (int i = 0; i < CYCLE_ORDER.length; i++) {
            if (CYCLE_ORDER[i] == this) {
                idx = i;
                break;
            }
        }
        for (int i = 1; i <= CYCLE_ORDER.length; i++) {
            GateTrigger prev = CYCLE_ORDER[(idx - i + CYCLE_ORDER.length) % CYCLE_ORDER.length];
            if (prev.isValidForTier(tier)) {
                return prev;
            }
        }
        return NONE;
    }

    public static double getFluidFillPercentage(@Nullable ResourceHandler<FluidResource> fluidHandler) {
        if (fluidHandler == null || fluidHandler.size() == 0) return -1.0;
        long totalAmount = 0;
        long totalCapacity = 0;
        for (int t = 0; t < fluidHandler.size(); t++) {
            long amt = fluidHandler.getAmountAsLong(t);
            long cap = fluidHandler.getCapacityAsLong(t, fluidHandler.getResource(t));
            if (cap <= 0) {
                cap = fluidHandler.getCapacityAsLong(t, net.neoforged.neoforge.transfer.fluid.FluidResource.of(net.minecraft.world.level.material.Fluids.WATER));
            }
            totalAmount += amt;
            totalCapacity += Math.max(0, cap);
        }
        totalCapacity = Math.max(totalCapacity, totalAmount);
        if (totalCapacity <= 0) return -1.0;
        return ((double) totalAmount / (double) totalCapacity) * 100.0;
    }

    public static double getItemFillPercentage(@Nullable Container container,
                                              @Nullable ResourceHandler<ItemResource> itemHandler) {
        if (container != null) {
            int size = container.getContainerSize();
            if (size == 0) return -1.0;
            double sumFractions = 0.0;
            for (int s = 0; s < size; s++) {
                ItemStack st = container.getItem(s);
                if (!st.isEmpty()) {
                    int max = Math.max(1, st.getMaxStackSize());
                    sumFractions += (double) st.getCount() / (double) max;
                }
            }
            return (sumFractions / (double) size) * 100.0;
        }
        if (itemHandler != null) {
            int size = itemHandler.size();
            if (size == 0) return -1.0;
            double sumFractions = 0.0;
            for (int s = 0; s < size; s++) {
                long amt = itemHandler.getAmountAsLong(s);
                if (amt > 0) {
                    long cap = itemHandler.getCapacityAsLong(s, itemHandler.getResource(s));
                    if (cap <= 0) cap = 64;
                    sumFractions += (double) amt / (double) cap;
                }
            }
            return (sumFractions / (double) size) * 100.0;
        }
        return -1.0;
    }

    public boolean evaluateTank(double percent) {
        if (percent < 0.0) return false;
        return switch (this) {
            case TANK_EMPTY, TANK_LE_0 -> percent <= 0.0 + 1e-4;
            case TANK_HAS_FLUID, TANK_GT_0 -> percent > 0.0 + 1e-4;
            case TANK_SPACE, TANK_LT_100 -> percent < 100.0 - 1e-4;
            case TANK_FULL, TANK_GE_100 -> percent >= 100.0 - 1e-4;
            case TANK_GE_0 -> percent >= 0.0 - 1e-4;
            case TANK_GE_25 -> percent >= 25.0 - 1e-4;
            case TANK_GE_50 -> percent >= 50.0 - 1e-4;
            case TANK_GE_75 -> percent >= 75.0 - 1e-4;
            case TANK_GT_25 -> percent > 25.0 + 1e-4;
            case TANK_GT_50 -> percent > 50.0 + 1e-4;
            case TANK_GT_75 -> percent > 75.0 + 1e-4;
            case TANK_LE_25 -> percent <= 25.0 + 1e-4;
            case TANK_LE_50 -> percent <= 50.0 + 1e-4;
            case TANK_LE_75 -> percent <= 75.0 + 1e-4;
            case TANK_LE_100 -> percent <= 100.0 + 1e-4;
            case TANK_LT_25 -> percent < 25.0 - 1e-4;
            case TANK_LT_50 -> percent < 50.0 - 1e-4;
            case TANK_LT_75 -> percent < 75.0 - 1e-4;
            default -> false;
        };
    }

    public boolean evaluateInventory(double percent) {
        if (percent < 0.0) return false;
        return switch (this) {
            case INV_EMPTY, INV_LE_0 -> percent <= 0.0 + 1e-4;
            case INV_HAS_ITEMS, INV_GT_0 -> percent > 0.0 + 1e-4;
            case INV_SPACE, INV_LT_100 -> percent < 100.0 - 1e-4;
            case INV_FULL, INV_GE_100 -> percent >= 100.0 - 1e-4;
            case INV_GE_0 -> percent >= 0.0 - 1e-4;
            case INV_GE_25 -> percent >= 25.0 - 1e-4;
            case INV_GE_50 -> percent >= 50.0 - 1e-4;
            case INV_GE_75 -> percent >= 75.0 - 1e-4;
            case INV_GT_25 -> percent > 25.0 + 1e-4;
            case INV_GT_50 -> percent > 50.0 + 1e-4;
            case INV_GT_75 -> percent > 75.0 + 1e-4;
            case INV_LE_25 -> percent <= 25.0 + 1e-4;
            case INV_LE_50 -> percent <= 50.0 + 1e-4;
            case INV_LE_75 -> percent <= 75.0 + 1e-4;
            case INV_LE_100 -> percent <= 100.0 + 1e-4;
            case INV_LT_25 -> percent < 25.0 - 1e-4;
            case INV_LT_50 -> percent < 50.0 - 1e-4;
            case INV_LT_75 -> percent < 75.0 - 1e-4;
            default -> false;
        };
    }
}
