package com.refinery.client.renderer;

import com.refinery.block.pipe.PipeConnectionType;
import com.refinery.block.pipe.PipeFacadeHelper;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BufferPipeRenderState extends BlockEntityRenderState {
    public BlockPos pos = BlockPos.ZERO;
    public final Map<Direction, PipeConnectionType> connections = new EnumMap<>(Direction.class);
    public final Map<Direction, PipeFacadeHelper.FacadeData> facades = new EnumMap<>(Direction.class);
    public final ItemStackRenderState itemState = new ItemStackRenderState();
    public boolean hasItem = false;
    public long gameTime;
    public float partialTicks;
}
