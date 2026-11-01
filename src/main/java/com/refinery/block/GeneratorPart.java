package com.refinery.block;

import net.minecraft.util.StringRepresentable;

public enum GeneratorPart implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    GeneratorPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
