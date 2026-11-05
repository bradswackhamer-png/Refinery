package com.refinery.block.entity;

public interface IRefineryFurnace {
    int AMBIENT_TEMP = 20;
    int MIN_OPERATING_TEMP = 350;
    int MAX_TEMP = 500;

    int getTemperature();
    boolean isHeating();
    boolean isElectric();
}
