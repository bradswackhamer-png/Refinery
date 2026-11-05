package com.refinery.guide;

public enum GuideCategory {
    ALL("All Contents", 0xFFE2E8F0),
    MULTIBLOCKS("Multiblock Plants", 0xFFF59E0B),
    METALLURGY("Foundry & Casting", 0xFFEF4444),
    MACHINERY("Processing Machines", 0xFF10B981),
    POWER("Steam & Power", 0xFF38BDF8),
    PIPES_GATES("Pipes & Logic Gates", 0xFFF97316),
    LOGISTICS("Logistics & Storage", 0xFFA855F7);

    private final String displayName;
    private final int color;

    GuideCategory(String displayName, int color) {
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
