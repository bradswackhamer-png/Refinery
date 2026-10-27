package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class MetalPressControllerRenderState extends BlockEntityRenderState {
    public boolean isBlueprintActive;
    public boolean isAssembled;
    public boolean isActive;
    public Direction facing = Direction.NORTH;
    public BlockPos controllerPos = BlockPos.ZERO;
    public int structureOffsetX = 0;
    public int structureDepth = 2;
    public int progress = 0;
    public long gameTime = 0;
    public float partialTicks = 0.0f;

    public boolean hasActiveItem = false;
    public final ItemStackRenderState activeItemState = new ItemStackRenderState();

    public record MissingComponent(
            int relX, int relY, int relZ,
            int color,
            String name
    ) {}

    public final List<MissingComponent> missingComponents = new ArrayList<>();
}
