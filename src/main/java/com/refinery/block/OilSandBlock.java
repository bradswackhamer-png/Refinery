package com.refinery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class OilSandBlock extends FallingBlock {
    public OilSandBlock(Properties properties) {
        super(properties.strength(0.8f, 1.0f)
                .sound(SoundType.SAND));
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0x2A2724; // Dark bituminous sand particle color
    }
}
