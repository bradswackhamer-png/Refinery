package com.refinery.block.pipe;

import net.minecraft.util.StringRepresentable;

public enum IndustrialPipeMaterial implements StringRepresentable {
    WOOD("wood", 0x8b5a2b, 0.75f, 1000, 2),
    COPPER("copper", 0xc3734b, 1.0f, 2000, 4),
    IRON("iron", 0xd8d8d8, 1.75f, 4000, 8),
    STEEL("steel", 0xa8b0ba, 2.5f, 8000, 16),
    GOLD("gold", 0xf5c020, 1.5f, 4000, 8),
    DIAMOND("diamond", 0x33ebcb, 2.0f, 6000, 16),
    EMERALD("emerald", 0x17dd62, 2.0f, 6000, 16),
    BRASS("brass", 0xdcb43c, 1.5f, 1000, 16),
    CAST_IRON("cast_iron", 0x46484e, 0.8f, 1500, 4),
    VOID("void", 0x202026, 2.0f, 10000, 64),
    QUARTZ("quartz", 0xf0ede9, 1.75f, 4000, 8);

    private final String name;
    private final int colorRgb;
    private final float speedMultiplier;
    private final int fluidCapacity;
    private final int itemTransferRate;

    IndustrialPipeMaterial(String name, int colorRgb, float speedMultiplier, int fluidCapacity, int itemTransferRate) {
        this.name = name;
        this.colorRgb = colorRgb;
        this.speedMultiplier = speedMultiplier;
        this.fluidCapacity = fluidCapacity;
        this.itemTransferRate = itemTransferRate;
    }

    public int getColorRgb() {
        return colorRgb;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getFluidCapacity() {
        return fluidCapacity;
    }

    public int getTransferRate() {
        return Math.max(100, fluidCapacity / 4);
    }

    public int getItemTransferRate() {
        return itemTransferRate;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
