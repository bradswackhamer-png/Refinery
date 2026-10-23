package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BoilerPipeBlock extends Block {
    public BoilerPipeBlock(Properties properties) {
        super(properties.strength(4.0f, 8.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops());
    }
}
