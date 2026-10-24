package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class GrinderChamberBlock extends Block {
    public GrinderChamberBlock(Properties properties) {
        super(properties.strength(5.5f, 10.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops());
    }
}
