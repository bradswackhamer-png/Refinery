package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class MultiSmelterControllerRenderState extends BlockEntityRenderState {
    public boolean isBlueprintActive;
    public boolean isAssembled;
    public boolean isActive;
    public int burnTime = 0;
    public int energy = 0;
    public Direction facing = Direction.NORTH;
    public BlockPos controllerPos = BlockPos.ZERO;
    public long gameTime = 0;
    public float partialTicks = 0.0f;

    public final ItemStackRenderState[] itemStates = new ItemStackRenderState[8];
    public final boolean[] hasItem = new boolean[8];
    public final int[] cookProgress = new int[8];

    public MultiSmelterControllerRenderState() {
        for (int i = 0; i < 8; i++) {
            itemStates[i] = new ItemStackRenderState();
        }
    }

    public record MissingComponent(
            int relX, int relY, int relZ,
            int color,
            String name
    ) {}

    public final List<MissingComponent> missingComponents = new ArrayList<>();
}
