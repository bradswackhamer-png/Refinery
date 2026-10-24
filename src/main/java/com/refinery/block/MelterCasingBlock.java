package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class MelterCasingBlock extends Block {
    public MelterCasingBlock(Properties properties) {
        super(properties.strength(3.0f, 9.0f)
                .sound(SoundType.NETHERITE_BLOCK));
    }
}
