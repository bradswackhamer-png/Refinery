package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class CastingBasinBlock extends Block {
    public CastingBasinBlock(Properties properties) {
        super(properties.strength(3.5f, 10.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
    }
}
