package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReinforcedCasingBlock extends Block {
    public ReinforcedCasingBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f).sound(SoundType.NETHERITE_BLOCK));
    }
}
