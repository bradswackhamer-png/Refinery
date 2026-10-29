package com.refinery.block;

import net.minecraft.util.StringRepresentable;

public enum DischargeMode implements StringRepresentable {
    VOID("void", "Void / Dump"),
    FILL("fill", "Contiguous Fill");

    private final String name;
    private final String displayName;

    DischargeMode(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public DischargeMode cycle() {
        return this == VOID ? FILL : VOID;
    }
}
