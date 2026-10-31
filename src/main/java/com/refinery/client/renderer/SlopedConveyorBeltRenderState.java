package com.refinery.client.renderer;

import com.refinery.block.SlopedConveyorBeltBlock;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlopedConveyorBeltRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public SlopedConveyorBeltBlock.SlopeDirection slope = SlopedConveyorBeltBlock.SlopeDirection.UP;
    public boolean isRunning = true;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
}
