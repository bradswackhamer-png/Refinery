package com.refinery.client.renderer.entity;

import com.refinery.gate.GateTier;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeGateRenderState extends EntityRenderState {
    public Direction direction = Direction.NORTH;
    public GateTier tier = GateTier.BASIC;
    public boolean autarchic = false;
    public boolean active = false;
    public boolean powered = false;
    public final ItemStackRenderState item = new ItemStackRenderState();
}
