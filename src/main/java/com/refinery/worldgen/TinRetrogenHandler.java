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
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Only evaluate Overworld chunks for subterranean tin ore veins
        if (serverLevel.dimension() != ServerLevel.OVERWORLD) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        // Check if already processed
        if (Boolean.TRUE.equals(chunk.getData(RefineryMod.TIN_CHUNK_RETROGEN))) {
            return;
        }

        chunk.setData(RefineryMod.TIN_CHUNK_RETROGEN, true);
        chunk.markUnsaved();

        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        RandomSource random = RandomSource.create(serverLevel.getSeed() ^ (chunkPos.pack() * 341873128712L + 132897987541L));

        BlockState tinOre = RefineryMod.TIN_ORE.get().defaultBlockState();
        BlockState deepslateTinOre = RefineryMod.DEEPSLATE_TIN_ORE.get().defaultBlockState();

        // 16 veins per chunk, triangle distribution between -16 and 112 (same as vanilla copper)
        for (int v = 0; v < 16; v++) {
            int cx = minX + random.nextInt(16);
            int cz = minZ + random.nextInt(16);
            // Triangle distribution: -16 + (0..63) + (0..64) -> range [-16, 111], peak at Y=48
            int cy = -16 + random.nextInt(64) + random.nextInt(65);

            int veinSize = random.nextIntBetweenInclusive(8, 12);
            BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

            for (int step = 0; step < veinSize; step++) {
                int ox = cx + random.nextInt(3) - 1;
                int oy = cy + random.nextInt(3) - 1;
                int oz = cz + random.nextInt(3) - 1;

                mpos.set(ox, oy, oz);
                BlockState target = serverLevel.getBlockState(mpos);

                if (target.is(BlockTags.STONE_ORE_REPLACEABLES)) {
                    serverLevel.setBlock(mpos, tinOre, 2);
                } else if (target.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
                    serverLevel.setBlock(mpos, deepslateTinOre, 2);
                }
            }
        }
    }
}
