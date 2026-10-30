package com.refinery.block.entity;

public enum StorageTankSize {
    SIZE_3X3X3("3x3x3 Tank", 1, 3, 64_000),
    SIZE_3X3X6("3x3x6 Tank", 1, 6, 256_000),
    SIZE_3X3X9("3x3x9 Tank", 1, 9, 512_000),
    SIZE_5X5X5("5x5x5 Tank", 2, 5, 1_000_000),
    SIZE_5X5X7("5x5x7 Tank", 2, 7, 2_000_000),
    SIZE_5X5X9("5x5x9 Tank", 2, 9, 4_000_000);

    private final String displayName;
    private final int radius; // 1 for 3x3, 2 for 5x5
    private final int height;
    private final int capacity;

    StorageTankSize(String displayName, int radius, int height, int capacity) {
        this.displayName = displayName;
        this.radius = radius;
        this.height = height;
        this.capacity = capacity;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getDiameter() {
        return this.radius * 2 + 1;
    }

    public int getHeight() {
        return this.height;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public StorageTankSize next() {
        StorageTankSize[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
