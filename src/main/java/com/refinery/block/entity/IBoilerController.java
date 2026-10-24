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
}
