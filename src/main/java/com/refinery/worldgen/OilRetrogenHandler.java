package com.refinery.worldgen;

import com.refinery.RefineryMod;
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

        // Only evaluate Overworld chunks for crude oil deposits
        if (serverLevel.dimension() != ServerLevel.OVERWORLD) {
            return;
        }

        LevelChunk chunk = event.getChunk();

        // 1. Check persistent chunk attachment (stored directly inside the chunk's .mca file)
        // If this chunk was already evaluated once in its lifetime, it is permanently BURNT and never re-evaluates.
        if (Boolean.TRUE.equals(chunk.getData(RefineryMod.OIL_CHUNK_BURNT))) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        long chunkKey = chunkPos.pack();

        // 2. Check global saved data cache
        OilRetrogenSavedData data = OilRetrogenSavedData.get(serverLevel);
        if (data.isChunkProcessed(chunkKey)) {
            chunk.setData(RefineryMod.OIL_CHUNK_BURNT, true);
            chunk.markUnsaved();
            return;
        }

        // Permanently burn the chunk in BOTH places before doing any generation checks
        chunk.setData(RefineryMod.OIL_CHUNK_BURNT, true);
        chunk.markUnsaved();
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

            // Also burn all 8 immediate neighbor chunks around this well
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    data.markChunkProcessed(ChunkPos.pack(chunkPos.x() + dx, chunkPos.z() + dz));
                }
            }

            boolean isGeyser = random.nextFloat() < RefineryConfig.CONFIG.rareGeyserChance.get();
            boolean generated = OilPitGenerator.generate(serverLevel, samplePos, isGeyser, random);
            if (!generated) {
                data.unmarkWell(chunkKey);
            }
        }
    }
}
