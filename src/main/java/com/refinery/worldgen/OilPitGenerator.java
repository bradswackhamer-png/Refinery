package com.refinery.worldgen;

import com.refinery.RefineryMod;
import com.refinery.config.RefineryConfig;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

public class OilPitGenerator {

    public static boolean generate(ServerLevel level, BlockPos origin, boolean isGeyser, RandomSource random) {
        int centerX = origin.getX();
        int centerZ = origin.getZ();

        // Check if ocean / water at sea level
        boolean isOcean = level.getFluidState(new BlockPos(centerX, 63, centerZ)).isSource();
        int surfaceY = isOcean
                ? level.getHeight(Heightmap.Types.OCEAN_FLOOR, centerX, centerZ)
                : level.getHeight(Heightmap.Types.WORLD_SURFACE, centerX, centerZ) - 1;

        if (surfaceY <= level.getMinY() + 25 || surfaceY >= level.getMaxY() - 20) {
            return false;
        }

        // Determine target oil source volume in blocks (1 block = 1,000 mB)
        int minBlocks = Math.max(100, RefineryConfig.CONFIG.minOilVolumeMb.get() / 1000);
        int maxBlocks = Math.max(minBlocks, RefineryConfig.CONFIG.maxOilVolumeMb.get() / 1000);
        int targetOilBlocks = isGeyser
                ? (RefineryConfig.CONFIG.geyserOilVolumeMb.get() / 1000)
                : random.nextIntBetweenInclusive(minBlocks, maxBlocks);

        BlockState oilBlock = RefineryFluids.CRUDE_OIL_BLOCK.get().defaultBlockState();
        BlockState oilSand = RefineryMod.OIL_SAND.get().defaultBlockState();

        int cavernY = Math.max(level.getMinY() + 15, surfaceY - (isGeyser ? 45 : 35));

        // Radii for cavern
        double rx = isGeyser ? 8.8 : 6.5;
        double ry = isGeyser ? 5.8 : 4.2;
        double rz = isGeyser ? 8.8 : 6.5;

        // Radii for surface pool
        int poolRadius = isGeyser ? 7 : 5;
        int poolDepth = isGeyser ? 4 : 3;

        // Collect positions to place oil and positions to place oil sand
        Set<BlockPos> oilPositions = new LinkedHashSet<>();
        Set<BlockPos> sandCasingPositions = new HashSet<>();

        // 1. Surface Pool
        for (int dx = -poolRadius - 1; dx <= poolRadius + 1; dx++) {
            for (int dz = -poolRadius - 1; dz <= poolRadius + 1; dz++) {
                double distSq = (dx * dx) + (dz * dz);
                if (distSq <= poolRadius * poolRadius) {
                    for (int dy = 0; dy >= -poolDepth; dy--) {
                        BlockPos pos = new BlockPos(centerX + dx, surfaceY + dy, centerZ + dz);
                        if (isReplaceable(level.getBlockState(pos))) {
                            oilPositions.add(pos);
                        }
                    }
                } else if (distSq <= (poolRadius + 1.5) * (poolRadius + 1.5)) {
                    // Pool border rim
                    for (int dy = 1; dy >= -poolDepth - 1; dy--) {
                        BlockPos pos = new BlockPos(centerX + dx, surfaceY + dy, centerZ + dz);
                        if (isReplaceable(level.getBlockState(pos))) {
                            sandCasingPositions.add(pos);
                        }
                    }
                }
            }
        }

        // 2. Vertical Conduit / Feeder Neck
        int conduitRadius = isGeyser ? 2 : 1;
        for (int y = surfaceY - poolDepth; y >= (int) (cavernY + ry); y--) {
            for (int dx = -conduitRadius; dx <= conduitRadius; dx++) {
                for (int dz = -conduitRadius; dz <= conduitRadius; dz++) {
                    if (dx * dx + dz * dz <= conduitRadius * conduitRadius + 0.5) {
                        BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                        if (isReplaceable(level.getBlockState(pos))) {
                            oilPositions.add(pos);
                        }
                    }
                }
            }
        }

        // 3. Subterranean Reservoir Cavern
        // Ordered from bottom of cavern up
        List<BlockPos> cavernCandidates = new ArrayList<>();
        int minY = (int) Math.floor(cavernY - ry);
        int maxY = (int) Math.ceil(cavernY + ry);
        int rMaxX = (int) Math.ceil(rx);
        int rMaxZ = (int) Math.ceil(rz);

        for (int y = minY; y <= maxY; y++) {
            double dy = (y - cavernY) / ry;
            for (int dx = -rMaxX; dx <= rMaxX; dx++) {
                double dxx = dx / rx;
                for (int dz = -rMaxZ; dz <= rMaxZ; dz++) {
                    double dzz = dz / rz;
                    double distNorm = (dxx * dxx) + (dy * dy) + (dzz * dzz);
                    if (distNorm <= 1.0) {
                        BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                        if (isReplaceable(level.getBlockState(pos))) {
                            cavernCandidates.add(pos);
                        }
                    }
                }
            }
        }

        // Add cavern positions until targetOilBlocks is satisfied
        for (BlockPos pos : cavernCandidates) {
            oilPositions.add(pos);
            if (oilPositions.size() >= targetOilBlocks) {
                break;
            }
        }

        // 4. Geyser Surface Spout (if geyser)
        if (isGeyser) {
            int spoutHeight = random.nextIntBetweenInclusive(6, 10);
            for (int h = 1; h <= spoutHeight; h++) {
                BlockPos pos = new BlockPos(centerX, surfaceY + h, centerZ);
                if (isReplaceable(level.getBlockState(pos))) {
                    oilPositions.add(pos);
                }
                // Oil sand spout base collar
                if (h <= 2) {
                    for (Direction d : Direction.Plane.HORIZONTAL) {
                        BlockPos collar = pos.relative(d);
                        if (isReplaceable(level.getBlockState(collar))) {
                            sandCasingPositions.add(collar);
                        }
                    }
                }
            }
        }

        // 5. Place Oil Sand casing around perimeter of oil blocks
        for (BlockPos oilPos : oilPositions) {
            for (Direction dir : Direction.values()) {
                BlockPos adj = oilPos.relative(dir);
                if (!oilPositions.contains(adj)) {
                    BlockState current = level.getBlockState(adj);
                    if (isReplaceable(current) && !current.isAir() && !current.is(Blocks.WATER)) {
                        sandCasingPositions.add(adj);
                    }
                }
            }
        }

        // 6. Write oil sand casing into world
        for (BlockPos pos : sandCasingPositions) {
            if (!oilPositions.contains(pos) && isReplaceable(level.getBlockState(pos))) {
                level.setBlock(pos, oilSand, 2);
            }
        }

        // 7. Write crude oil source blocks into world
        int placedCount = 0;
        for (BlockPos pos : oilPositions) {
            if (isReplaceable(level.getBlockState(pos))) {
                level.setBlock(pos, oilBlock, 2);
                placedCount++;
            }
        }

        RefineryMod.LOGGER.info("Generated Crude Oil Pit at [{}, {}, {}] with {} mB ({} source blocks) of oil. Geyser: {}",
                centerX, surfaceY, centerZ, placedCount * 1000, placedCount, isGeyser);

        return true;
    }

    private static boolean isReplaceable(BlockState state) {
        if (state.isAir()) return true;
        if (state.is(Blocks.BEDROCK)) return false;
        if (state.hasBlockEntity()) return false;
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.DIRT)
                || state.is(BlockTags.SAND)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.CLAY)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.RED_SANDSTONE)
                || state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.WATER)
                || state.is(Blocks.ICE)
                || state.is(RefineryMod.OIL_SAND.get())
                || state.is(RefineryFluids.CRUDE_OIL_BLOCK.get());
    }
}
