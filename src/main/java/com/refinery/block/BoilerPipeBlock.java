package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BoilerPipeBlock extends Block {
    public BoilerPipeBlock(Properties properties) {
        super(properties.strength(1.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                );
    }
}
