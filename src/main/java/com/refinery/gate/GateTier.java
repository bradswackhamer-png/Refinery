package com.refinery.gate;

public enum GateTier {
    BASIC(1, 0, "Basic Gate", 0xFF8A8A8A),
    IRON(2, 2, "Iron Gate", 0xFFD8D8D8),
    GOLD(4, 4, "Gold Gate", 0xFFFFD700),
    DIAMOND(8, 4, "Diamond Gate", 0xFF4DEEEA);

    private final int slots;
    private final int wireCount;
    private final String displayName;
    private final int color;

    GateTier(int slots, int wireCount, String displayName, int color) {
        this.slots = slots;
        this.wireCount = wireCount;
        this.displayName = displayName;
        this.color = color;
    }

    public int getSlots() {
        return slots;
    }

    public int getWireCount() {
        return wireCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public boolean supportsWire(PipeWireColor color) {
        if (wireCount == 0) return false;
        if (wireCount == 2) return color == PipeWireColor.RED || color == PipeWireColor.BLUE;
        return true;
    }
}
