package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CastingChannelRenderState extends BlockEntityRenderState {
    public int amount = 0;
    public Fluid fluid = Fluids.EMPTY;
    public boolean north = false;
    public boolean south = false;
    public boolean east = false;
    public boolean west = false;
    public boolean down = false;
}
