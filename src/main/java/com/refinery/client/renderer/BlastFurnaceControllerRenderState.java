package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class BlastFurnaceControllerRenderState extends BlockEntityRenderState {
    public boolean isBlueprintActive;
    public boolean isAssembled;
    public boolean isActive;
    public int progress;
    public long gameTime;
    public float partialTicks;
    public Direction facing = Direction.NORTH;
    public BlockPos controllerPos = BlockPos.ZERO;

    public record MissingComponent(
            int relX, int relY, int relZ,
            int color,
            String name
    ) {}

    public final List<MissingComponent> missingComponents = new ArrayList<>();
}
