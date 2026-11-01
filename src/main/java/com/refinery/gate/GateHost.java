package com.refinery.gate;

import net.minecraft.world.inventory.ContainerData;

public interface GateHost {
    GateTier getTier();
    boolean isAutarchic();
    void cycleTrigger(int slot, boolean forward);
    void cycleTriggerCategory(int slot, boolean forward);
    void cycleAction(int slot, boolean forward);
    ContainerData getDataAccess();
}
