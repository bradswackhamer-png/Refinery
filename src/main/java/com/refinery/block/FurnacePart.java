package com.refinery.block;

import net.minecraft.util.StringRepresentable;

public enum FurnacePart implements StringRepresentable {
    LOWER("lower"),
    UPPER("upper");

    private final String name;

    FurnacePart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
