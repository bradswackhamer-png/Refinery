package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BlastFurnaceCasingBlock extends Block {
    public BlastFurnaceCasingBlock(Properties properties) {
        super(properties.strength(4.5f, 9.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops());
    }
}
