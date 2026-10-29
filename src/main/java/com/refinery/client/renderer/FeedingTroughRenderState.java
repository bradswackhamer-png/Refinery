package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FeedingTroughRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public BedPart part = BedPart.FOOT;
    public float fillFraction = 0.0f;
    public boolean isMainRenderer = true;
}
