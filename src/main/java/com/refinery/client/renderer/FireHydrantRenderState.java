package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

/**
 * Render state for the AWWA Yellow Fire Hydrant 3D BlockEntityRenderer.
 */
public class FireHydrantRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean active = false;
    public int valveAnimTicks = 0;
}
