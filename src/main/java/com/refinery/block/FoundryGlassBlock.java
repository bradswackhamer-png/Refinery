package com.refinery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class FoundryGlassBlock extends HalfTransparentBlock {
    public FoundryGlassBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.GLASS)
                .noOcclusion());
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }
}
