package com.refinery.client.renderer;

import com.refinery.block.pipe.IndustrialPipeMaterial;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.block.pipe.PipeConnectionType;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class IndustrialPipeRenderState extends BlockEntityRenderState {
    public BlockPos pos = BlockPos.ZERO;
    public IndustrialPipeType pipeType = IndustrialPipeType.FLUID;
    public IndustrialPipeMaterial material = IndustrialPipeMaterial.STEEL;
    public Direction mergeOutput = Direction.NORTH;
    public final Map<Direction, PipeConnectionType> connections = new EnumMap<>(Direction.class);

    public Fluid storedFluid = Fluids.EMPTY;
    public float fluidFillRatio = 0.0f;

    public static class RenderTravelingItem {
        public final ItemStackRenderState itemState = new ItemStackRenderState();
        public Direction fromDir = Direction.NORTH;
        public Direction toDir = Direction.SOUTH;
        public float progress = 0.0f;
    }

    public final List<RenderTravelingItem> travelingItems = new ArrayList<>();
    public long gameTime = 0L;
    public float partialTicks = 0.0f;

    public IndustrialPipeRenderState() {
        for (Direction dir : Direction.values()) {
            connections.put(dir, PipeConnectionType.NONE);
        }
    }
}
