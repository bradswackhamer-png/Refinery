package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidPumpRenderState extends BlockEntityRenderState {
    public BlockPos pumpPos = BlockPos.ZERO;
    public Direction facing = Direction.NORTH;
    public boolean isActive = false;
    public boolean hasTarget = false;
    public BlockPos targetPos = null;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
    public int lightCoords = 0;
    public int frameColor = 0xFFFFFFFF;
}
