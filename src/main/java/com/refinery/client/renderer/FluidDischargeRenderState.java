package com.refinery.client.renderer;

import com.refinery.block.DischargeMode;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidDischargeRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public DischargeMode mode = DischargeMode.VOID;
    public boolean isActive = false;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
}
