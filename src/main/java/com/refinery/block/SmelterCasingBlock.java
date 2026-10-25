package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class SmelterCasingBlock extends Block {
    public SmelterCasingBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK));
    }
}
