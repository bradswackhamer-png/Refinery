package com.refinery.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RefineryControllerRenderState extends BlockEntityRenderState {
    public boolean isBlueprintActive = false;
    public boolean isAssembled = false;
    public boolean isActive = false;
    public boolean stage1Enabled = true;
    public boolean stage2Enabled = true;
    public boolean stage3Enabled = true;
    public Direction facing = Direction.NORTH;
    public BlockPos controllerPos = BlockPos.ZERO;
    public final List<MissingComponent> missingComponents = new ArrayList<>();

    public record MissingComponent(
            int relX,
            int relY,
            int relZ,
            int color,
            String name
    ) {}
}
