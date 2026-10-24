package com.refinery.gate;

public enum PipeWireColor {
    RED("Red Wire", 0xFFFF2222),
    BLUE("Blue Wire", 0xFF2266FF),
    GREEN("Green Wire", 0xFF22DD22),
    YELLOW("Yellow Wire", 0xFFFFDD22);

    private final String displayName;
    private final int color;

    PipeWireColor(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }
}
