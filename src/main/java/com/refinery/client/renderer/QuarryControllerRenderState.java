package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class QuarryControllerRenderState extends BlockEntityRenderState {
    public boolean isAssembled = false;
    public boolean isActive = false;
    public boolean isBlueprintActive = false;
    public Direction facing = Direction.NORTH;
    public BlockPos controllerPos = BlockPos.ZERO;
    public int radius = 21;
    public int phase = 0;
    public long gameTime = 0;
    public float partialTicks = 0.0f;
    public int lightCoords = 0;

    public final ItemStackRenderState outputItemState = new ItemStackRenderState();
    public boolean hasOutputItem = false;

    public record MissingComponent(
            int relX, int relY, int relZ,
            int color,
            String name
    ) {}

    public final List<MissingComponent> missingComponents = new ArrayList<>();
}
