package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RefineryFurnaceRenderState extends BlockEntityRenderState {
    public boolean shouldRender = false;
    public boolean isElectric = false;
    public boolean isActive = false;
    public int temperature = 20;
    public Direction facing = Direction.NORTH;
}
