package com.refinery.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class PressBedBlock extends Block {
    public PressBedBlock(Properties properties) {
        super(properties.strength(5.5f, 12.0f)
                .sound(SoundType.ANVIL)
                .requiresCorrectToolForDrops());
    }
}
