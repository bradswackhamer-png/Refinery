package com.refinery.block.entity;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface ITurbineController {
    boolean isAssembled();
    BlockPos getPosition();
    int fillSteam(int amount);
    int drainWater(int amount);
    EnergyHandler getEnergyHandler();
    int getSteamAmount();
    int getWaterAmount();
    void setSteamAmount(int amt);
    void setWaterAmount(int amt);
    int getEnergyStored();
    int getMaxEnergyStored();
}
