package com.refinery.block.pipe;

import net.minecraft.util.StringRepresentable;

public enum PipeConnectionType implements StringRepresentable {
    NONE("none"),
    PIPE("pipe"),
    MACHINE("machine");

    private final String name;

    PipeConnectionType(String name) {
        this.name = name;
    }

    public boolean isConnected() {
        return this != NONE;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
