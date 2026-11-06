package com.refinery.block;

import net.minecraft.util.StringRepresentable;

public enum BitumenPilePart implements StringRepresentable {
    // Bottom layer (Y = 0)
    BOTTOM_FRONT_LEFT("bottom_front_left", 0, 0, 0),
    BOTTOM_FRONT_CENTER("bottom_front_center", 1, 0, 0),
    BOTTOM_FRONT_RIGHT("bottom_front_right", 2, 0, 0),
    BOTTOM_CENTER_LEFT("bottom_center_left", 0, 1, 0),
    BOTTOM_CENTER_CENTER("bottom_center_center", 1, 1, 0),
    BOTTOM_CENTER_RIGHT("bottom_center_right", 2, 1, 0),
    BOTTOM_BACK_LEFT("bottom_back_left", 0, 2, 0),
    BOTTOM_BACK_CENTER("bottom_back_center", 1, 2, 0),
    BOTTOM_BACK_RIGHT("bottom_back_right", 2, 2, 0),

    // Top layer (Y = 1)
    TOP_FRONT_LEFT("top_front_left", 0, 0, 1),
    TOP_FRONT_CENTER("top_front_center", 1, 0, 1),
    TOP_FRONT_RIGHT("top_front_right", 2, 0, 1),
    TOP_CENTER_LEFT("top_center_left", 0, 1, 1),
    TOP_CENTER_CENTER("top_center_center", 1, 1, 1),
    TOP_CENTER_RIGHT("top_center_right", 2, 1, 1),
    TOP_BACK_LEFT("top_back_left", 0, 2, 1),
    TOP_BACK_CENTER("top_back_center", 1, 2, 1),
    TOP_BACK_RIGHT("top_back_right", 2, 2, 1);

    private final String name;
    private final int dx; // 0 = Left, 1 = Center, 2 = Right
    private final int dz; // 0 = Front, 1 = Center, 2 = Back
    private final int dy; // 0 = Bottom, 1 = Top

    BitumenPilePart(String name, int dx, int dz, int dy) {
        this.name = name;
        this.dx = dx;
        this.dz = dz;
        this.dy = dy;
    }

    public boolean isMaster() {
        return this == BOTTOM_FRONT_LEFT;
    }

    public boolean isBottom() {
        return this.dy == 0;
    }

    public int dx() {
        return this.dx;
    }

    public int dz() {
        return this.dz;
    }

    public int dy() {
        return this.dy;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
