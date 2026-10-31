package com.refinery.client.renderer;

import com.refinery.block.RightAngleConveyorBeltBlock;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RightAngleConveyorBeltRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public RightAngleConveyorBeltBlock.TurnDirection turn = RightAngleConveyorBeltBlock.TurnDirection.RIGHT;
    public boolean isRunning = true;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
}
