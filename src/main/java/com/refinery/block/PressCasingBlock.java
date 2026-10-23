package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class PressCasingBlock extends Block {
    public PressCasingBlock(Properties properties) {
        super(properties.strength(5.0f, 10.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops());
    }
}
