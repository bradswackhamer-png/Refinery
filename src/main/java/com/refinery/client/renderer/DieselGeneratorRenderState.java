package com.refinery.client.renderer;

import com.refinery.block.GeneratorPart;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DieselGeneratorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public GeneratorPart part = GeneratorPart.LEFT;
    public boolean isMainRenderer = false;
    public boolean active = false;
}
