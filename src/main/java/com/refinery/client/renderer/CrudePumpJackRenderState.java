package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrudePumpJackRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float crankAngle = 0.0f;
    public float pumpingSpeed = 0.0f;
    public int crudeOil = 0;
    public int energy = 0;
}
