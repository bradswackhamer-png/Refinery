package com.refinery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IBoilerController {
    boolean isAssembled();
    BlockPos getPosition();
    int fillWater(int amount);
    int drainSteam(int amount);
    int getWaterAmount();
    int getSteamAmount();
    void setWaterAmount(int amt);
    void setSteamAmount(int amt);
    Container getFuelContainer();
    boolean canInsertFuel(ItemStack stack);

    default boolean isDieselBoiler() {
        return false;
    }
    default int fillDiesel(int amount) {
        return 0;
    }
    default int getDieselAmount() {
        return 0;
    }
    default void setDieselAmount(int amt) {}
}
