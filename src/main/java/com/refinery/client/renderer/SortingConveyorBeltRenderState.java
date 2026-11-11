package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SortingConveyorBeltRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean connectedFront = false;
    public boolean connectedBack = false;
    public boolean connectedLeft = false;
    public boolean connectedRight = false;
    public boolean isRunning = true;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
    public long lastDivertedTick = 0;
    public Direction lastDivertedDir = null;
}
