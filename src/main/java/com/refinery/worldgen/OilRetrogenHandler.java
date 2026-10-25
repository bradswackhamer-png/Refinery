package com.refinery.worldgen;

import com.refinery.config.RefineryConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.ChunkEvent;

public class OilRetrogenHandler {

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!RefineryConfig.CONFIG.enableOilRetrogen.get()) {
            return;
        }

        LevelChunk chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();
        long chunkKey = chunkPos.pack();

        OilRetrogenSavedData data = OilRetrogenSavedData.get(serverLevel);
        if (data.isChunkProcessed(chunkKey)) {
            return; // Once a chunk is processed or mined, it never regens
        }

        // Permanently record that this chunk has been evaluated so it is never re-evaluated
        data.markChunkProcessed(chunkKey);

        // Check well spacing to guarantee wells are spaced out across chunks
        int minDistance = RefineryConfig.CONFIG.minChunkDistanceBetweenWells.get();
        if (data.isWellNearby(chunkPos.x(), chunkPos.z(), minDistance)) {
            return;
        }

        // Check biome at chunk center
        int centerX = chunkPos.getMinBlockX() + 8;
        int centerZ = chunkPos.getMinBlockZ() + 8;
        BlockPos samplePos = new BlockPos(centerX, 64, centerZ);
        Holder<Biome> biomeHolder = serverLevel.getBiome(samplePos);

        if (!RefineryConfig.CONFIG.isBiomeAllowed(biomeHolder)) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        double spawnChance = RefineryConfig.CONFIG.oilPitSpawnChance.get();
        if (RefineryConfig.CONFIG.isOceanBiome(biomeHolder)) {
            spawnChance *= RefineryConfig.CONFIG.oceanSpawnChanceMultiplier.get();
        }

        if (random.nextFloat() < spawnChance) {
            // Reserve well location in saved data BEFORE generate() runs.
            // OilPitGenerator sets blocks across chunk boundaries which synchronously loads neighbor chunks;
            // reserving the chunk first ensures any synchronously loaded neighbor sees isWellNearby() == true
            // and immediately aborts, preventing cascading touching wells.
            data.markWellGenerated(chunkKey);

            boolean isGeyser = random.nextFloat() < RefineryConfig.CONFIG.rareGeyserChance.get();
            boolean generated = OilPitGenerator.generate(serverLevel, samplePos, isGeyser, random);
            if (!generated) {
                data.unmarkWell(chunkKey);
            }
        }
    }
}
