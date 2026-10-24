package com.refinery.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
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

    public final ItemStackRenderState redItem = new ItemStackRenderState();
    public final ItemStackRenderState blueItem = new ItemStackRenderState();
    public final ItemStackRenderState greenItem = new ItemStackRenderState();
    public final ItemStackRenderState yellowItem = new ItemStackRenderState();
}
