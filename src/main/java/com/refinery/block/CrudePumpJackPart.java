package com.refinery.block;

import net.minecraft.util.StringRepresentable;

public enum CrudePumpJackPart implements StringRepresentable {
    // Bottom layer (Y = 0)
    BOTTOM_FRONT_LEFT("bottom_front_left", 0, 0, 0),
    BOTTOM_FRONT_RIGHT("bottom_front_right", 1, 0, 0),
    BOTTOM_CENTER_LEFT("bottom_center_left", 0, 1, 0),
    BOTTOM_CENTER_RIGHT("bottom_center_right", 1, 1, 0),
    BOTTOM_BACK_LEFT("bottom_back_left", 0, 2, 0),
    BOTTOM_BACK_RIGHT("bottom_back_right", 1, 2, 0),

    // Middle layer (Y = 1)
    MID_FRONT_LEFT("mid_front_left", 0, 0, 1),
    MID_FRONT_RIGHT("mid_front_right", 1, 0, 1),
    MID_CENTER_LEFT("mid_center_left", 0, 1, 1),
    MID_CENTER_RIGHT("mid_center_right", 1, 1, 1),
    MID_BACK_LEFT("mid_back_left", 0, 2, 1),
    MID_BACK_RIGHT("mid_back_right", 1, 2, 1),

    // Top layer (Y = 2)
    TOP_FRONT_LEFT("top_front_left", 0, 0, 2),
    TOP_FRONT_RIGHT("top_front_right", 1, 0, 2),
    TOP_CENTER_LEFT("top_center_left", 0, 1, 2),
    TOP_CENTER_RIGHT("top_center_right", 1, 1, 2),
    TOP_BACK_LEFT("top_back_left", 0, 2, 2),
    TOP_BACK_RIGHT("top_back_right", 1, 2, 2);

    private final String name;
    private final int dx; // 0 = Left, 1 = Right (Width = 2)
    private final int dz; // 0 = Front (Wellhead), 1 = Center (Samson Post), 2 = Back (Motor/Crank) (Length = 3)
    private final int dy; // 0 = Bottom, 1 = Mid, 2 = Top (Height = 3)

    CrudePumpJackPart(String name, int dx, int dz, int dy) {
        this.name = name;
        this.dx = dx;
        this.dz = dz;
        this.dy = dy;
    }

    public boolean isMaster() {
        return this == BOTTOM_FRONT_LEFT;
    }

    public boolean isWellhead() {
        return this.dz == 0 && this.dy <= 1;
    }

    public boolean isMotorOrPanel() {
        return this.dz == 2 && this.dy <= 1;
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
