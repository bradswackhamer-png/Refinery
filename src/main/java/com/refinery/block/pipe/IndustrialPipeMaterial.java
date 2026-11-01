package com.refinery.block.pipe;

import net.minecraft.util.StringRepresentable;

public enum IndustrialPipeMaterial implements StringRepresentable {
    WOOD("wood", 0x8b5a2b, 0.75f, 250, 2, 100),
    COPPER("copper", 0xc3734b, 1.0f, 300, 4, 250),
    IRON("iron", 0xd8d8d8, 1.75f, 400, 8, 500),
    STEEL("steel", 0xa8b0ba, 2.5f, 500, 16, 1500),
    GOLD("gold", 0xf5c020, 1.5f, 400, 8, 800),
    DIAMOND("diamond", 0x33ebcb, 2.0f, 500, 16, 1000),
    EMERALD("emerald", 0x17dd62, 2.0f, 500, 16, 1000),
    BRASS("brass", 0xdcb43c, 1.5f, 350, 16, 350),
    CAST_IRON("cast_iron", 0x46484e, 0.8f, 250, 4, 150),
    VOID("void", 0x202026, 2.0f, 500, 64, 2000),
    QUARTZ("quartz", 0xf0ede9, 1.75f, 400, 8, 800);

    private final String name;
    private final int colorRgb;
    private final float speedMultiplier;
    private final int fluidCapacity;
    private final int itemTransferRate;
    private final int fluidTransferRate;

    IndustrialPipeMaterial(String name, int colorRgb, float speedMultiplier, int fluidCapacity, int itemTransferRate, int fluidTransferRate) {
        this.name = name;
        this.colorRgb = colorRgb;
        this.speedMultiplier = speedMultiplier;
        this.fluidCapacity = fluidCapacity;
        this.itemTransferRate = itemTransferRate;
        this.fluidTransferRate = fluidTransferRate;
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
        return fluidTransferRate;
    }

    public int getItemTransferRate() {
        return itemTransferRate;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
