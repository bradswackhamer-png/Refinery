package com.refinery.gate;

public enum GateTrigger {
    NONE("None", "No trigger selected", 0x888888),
    REDSTONE_ON("Redstone ON", "Gate receives redstone power > 0", 0xFF3333),
    REDSTONE_OFF("Redstone OFF", "Gate has no redstone power", 0x773333),
    INV_EMPTY("Inventory Empty", "Connected container is completely empty", 0xAAAAAA),
    INV_HAS_ITEMS("Has Items", "Connected container contains at least 1 item", 0x55FF55),
    INV_SPACE("Space in Inventory", "Connected container has room for more items", 0x55AAFF),
    INV_FULL("Inventory Full", "Connected container has zero free slots or space", 0xFFAA00),
    TANK_EMPTY("Tank Empty", "Connected tank has 0 mB fluid", 0xAAAAAA),
    TANK_HAS_FLUID("Has Fluid", "Connected tank has fluid", 0x33AAFF),
    TANK_SPACE("Space in Tank", "Connected tank has room for more fluid", 0x55FFFF),
    TANK_FULL("Tank Full", "Connected tank is at max capacity", 0xFFAA00),
    PIPE_EMPTY("Pipe Empty", "No items or fluids traveling in pipe", 0xAAAAAA),
    PIPE_FLOWING("Pipe Flowing", "Items or fluids currently traveling in pipe", 0x55FF55),
    MACHINE_ACTIVE("Machine Active", "Connected machine is running/operating", 0xFF5533),
    MACHINE_IDLE("Machine Idle", "Connected machine is idle/unpowered", 0x888888),
    WIRE_RED_ON("Red Wire ON", "Red pipe wire signal is active", 0xFF2222),
    WIRE_RED_OFF("Red Wire OFF", "Red pipe wire signal is inactive", 0x771111),
    WIRE_BLUE_ON("Blue Wire ON", "Blue pipe wire signal is active", 0x2266FF),
    WIRE_BLUE_OFF("Blue Wire OFF", "Blue pipe wire signal is inactive", 0x113377),
    WIRE_GREEN_ON("Green Wire ON", "Green pipe wire signal is active", 0x22DD22),
    WIRE_GREEN_OFF("Green Wire OFF", "Green pipe wire signal is inactive", 0x116611),
    WIRE_YELLOW_ON("Yellow Wire ON", "Yellow pipe wire signal is active", 0xFFFF22),
    WIRE_YELLOW_OFF("Yellow Wire OFF", "Yellow pipe wire signal is inactive", 0x777711);

    private final String label;
    private final String description;
    private final int displayColor;

    GateTrigger(String label, String description, int displayColor) {
        this.label = label;
        this.description = description;
        this.displayColor = 0xFF000000 | displayColor;
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

    public boolean isWireTrigger() {
        return this.name().startsWith("WIRE_");
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

    public GateTrigger next(GateTier tier) {
        GateTrigger[] values = values();
        int idx = this.ordinal();
        for (int i = 1; i <= values.length; i++) {
            GateTrigger next = values[(idx + i) % values.length];
            if (next.isValidForTier(tier)) {
                return next;
            }
        }
        return NONE;
    }

    public GateTrigger previous(GateTier tier) {
        GateTrigger[] values = values();
        int idx = this.ordinal();
        for (int i = 1; i <= values.length; i++) {
            GateTrigger prev = values[(idx - i + values.length) % values.length];
            if (prev.isValidForTier(tier)) {
                return prev;
            }
        }
        return NONE;
    }
}
