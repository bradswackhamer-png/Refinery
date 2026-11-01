package com.refinery.worldgen;

import com.refinery.RefineryMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.ChunkEvent;

public class TinRetrogenHandler {

    public static void onChunkLoad(ChunkEvent.Load event) {
        // Disabled: Vanilla/NeoForge worldgen feature pipeline handles ore generation.
        // ChunkEvent retrogen causes C2ME deadlock during terrain loading.
    }
}
