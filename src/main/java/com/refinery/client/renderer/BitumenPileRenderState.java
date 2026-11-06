package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BitumenPileRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int visibleStage = 0;
    public int totalCount = 0;
}
