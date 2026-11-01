package com.refinery.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeWireRenderState extends EntityRenderState {
    public boolean hasRed = false;
    public boolean hasBlue = false;
    public boolean hasGreen = false;
    public boolean hasYellow = false;

    public boolean signalRed = false;
    public boolean signalBlue = false;
    public boolean signalGreen = false;
    public boolean signalYellow = false;

    // Directional connectivity for each of the 4 colors (6 directions: D, U, N, S, W, E)
    public final boolean[][] connections = new boolean[4][6];
}
