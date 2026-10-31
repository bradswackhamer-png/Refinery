package com.refinery.block.pipe;

import net.minecraft.util.StringRepresentable;

public enum IndustrialPipeType implements StringRepresentable {
    FLUID("fluid", true, false, false),
    ITEM("item", false, true, false),
    FLUID_EXTRACTOR("fluid_extractor", true, false, true),
    ITEM_EXTRACTOR("item_extractor", false, true, true),
    VOID("void", true, true, false);

    private final String name;
    private final boolean handlesFluid;
    private final boolean handlesItems;
    private final boolean isExtractor;

    IndustrialPipeType(String name, boolean handlesFluid, boolean handlesItems, boolean isExtractor) {
        this.name = name;
        this.handlesFluid = handlesFluid;
        this.handlesItems = handlesItems;
        this.isExtractor = isExtractor;
    }

    public boolean handlesFluid() {
        return handlesFluid;
    }

    public boolean handlesItems() {
        return handlesItems;
    }

    public boolean isExtractor() {
        return isExtractor;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
