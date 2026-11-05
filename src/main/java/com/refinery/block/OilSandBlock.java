package com.refinery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class OilSandBlock extends FallingBlock {
    private final int dustColor;

    public OilSandBlock(Properties properties) {
        this(properties, 0x2A2724); // Dark bituminous sand particle color
    }

    public OilSandBlock(Properties properties, int dustColor) {
        super(properties.strength(0.8f, 1.0f)
                .sound(SoundType.SAND));
        this.dustColor = dustColor;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return dustColor;
    }
}
