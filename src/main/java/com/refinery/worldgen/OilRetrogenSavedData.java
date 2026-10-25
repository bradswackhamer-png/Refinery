package com.refinery.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OilRetrogenSavedData extends SavedData {
    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "oil_retrogen");

    private final Set<Long> processedChunks = new HashSet<>();
    private final Set<Long> wellChunks = new HashSet<>();

    public static final Codec<OilRetrogenSavedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.listOf().optionalFieldOf("processed_chunks", List.of()).forGetter(data -> new ArrayList<>(data.processedChunks)),
                    Codec.LONG.listOf().optionalFieldOf("well_chunks", List.of()).forGetter(data -> new ArrayList<>(data.wellChunks))
            ).apply(instance, (processed, wells) -> {
                OilRetrogenSavedData data = new OilRetrogenSavedData();
                data.processedChunks.addAll(processed);
                data.wellChunks.addAll(wells);
                return data;
            })
    );

    public static final SavedDataType<OilRetrogenSavedData> TYPE = new SavedDataType<>(
            TYPE_ID,
            OilRetrogenSavedData::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    public OilRetrogenSavedData() {
    }

    public static OilRetrogenSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public synchronized boolean isChunkProcessed(long chunkCoord) {
        return processedChunks.contains(chunkCoord);
    }

    public synchronized void markChunkProcessed(long chunkCoord) {
        if (processedChunks.add(chunkCoord)) {
            setDirty();
        }
    }

    public synchronized boolean isWellNearby(int chunkX, int chunkZ, int minDistance) {
        int minDistSq = minDistance * minDistance;
        for (long wellKey : wellChunks) {
            int wx = ChunkPos.getX(wellKey);
            int wz = ChunkPos.getZ(wellKey);
            int dx = wx - chunkX;
            int dz = wz - chunkZ;
            if ((dx * dx + dz * dz) < minDistSq) {
                return true;
            }
        }
        return false;
    }

    public synchronized void markWellGenerated(long chunkCoord) {
        processedChunks.add(chunkCoord);
        if (wellChunks.add(chunkCoord)) {
            setDirty();
        }
    }

    public synchronized void unmarkWell(long chunkCoord) {
        if (wellChunks.remove(chunkCoord)) {
            setDirty();
        }
    }

    public synchronized void clear() {
        processedChunks.clear();
        wellChunks.clear();
        setDirty();
    }

    public synchronized int getProcessedChunkCount() {
        return processedChunks.size();
    }

    public synchronized int getWellChunkCount() {
        return wellChunks.size();
    }
}
