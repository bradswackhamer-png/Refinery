package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.block.pipe.IndustrialPipeMaterial;
import com.refinery.block.pipe.PipeConnectionType;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.Map;

/**
 * High-Fidelity 3D Monolithic Cylindrical Model for Industrial Refinery Pipes.
 * Renders smooth 12-sided faceted round pipes:
 * - Straight pipe runs merge into a single uninterrupted, seamless cylinder (0.0 to 1.0)
 * - Elbows & Tees fuse seamlessly via a solid faceted spherical joint fitting with zero Z-fighting
 * - Industrial bolted connection flanges appear strictly at machine endpoints
 * - Standalone single pipes render as complete solid cylindrical segments
 */
@OnlyIn(Dist.CLIENT)
public class IndustrialPipeModel {
    public static final Identifier TEXTURE_STEEL = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/steel_pipe.png");
    public static final Identifier TEXTURE_COPPER = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/copper_pipe.png");
    public static final Identifier TEXTURE_WOOD = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/wood_pipe.png");
    public static final Identifier TEXTURE_IRON = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/iron_pipe.png");
    public static final Identifier TEXTURE_GOLD = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/gold_pipe.png");
    public static final Identifier TEXTURE_DIAMOND = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/diamond_pipe.png");
    public static final Identifier TEXTURE_EMERALD = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/emerald_pipe.png");
    public static final Identifier TEXTURE_BRASS = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/brass_pipe.png");
    public static final Identifier TEXTURE_CAST_IRON = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/cast_iron_pipe.png");
    public static final Identifier TEXTURE_VOID = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/void_pipe.png");
    public static final Identifier TEXTURE_QUARTZ = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/quartz_pipe.png");
    public static final Identifier TEXTURE_WINDOW = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/block/pipe/pipe_window.png");

    private static final int SEGMENTS = 12;
    private static final float PIPE_RADIUS = 3.2f / 16.0f; // 0.20 normalized
    private static final float HUB_RADIUS = 3.65f / 16.0f;  // 0.228 normalized - cleanly encloses entering arms
    private static final float FLANGE_RADIUS = 4.4f / 16.0f; // 0.275 normalized
    private static final float CX = 0.5f;
    private static final float CY = 0.5f;
    private static final float CZ = 0.5f;

    // Cylinder arms enter the fitting hub and stop cleanly before the center
    // Arms terminate at 0.5 ± 0.08f. At d = 0.08f, Hub R = sqrt(0.228^2 - 0.08^2) = 0.213 > 0.200 PIPE_RADIUS.
    // This guarantees arms are 100% inside the hub, never intersect each other, and never poke out.
    private static final float HUB_STOP = 0.08f;

    private static final float[] COS = new float[SEGMENTS + 1];
    private static final float[] SIN = new float[SEGMENTS + 1];

    static {
        for (int i = 0; i <= SEGMENTS; i++) {
            double angle = (2.0 * Math.PI * i) / SEGMENTS;
            COS[i] = (float) Math.cos(angle);
            SIN[i] = (float) Math.sin(angle);
        }
    }

    // Precomputed vertices for 1/8 pipe diameter clear sight-glass strips and metal wall quadrants
    // Strip width = 2 * PIPE_RADIUS * STRIP_S = 2 * 0.20 * 0.125 = 0.050 (exactly 1/8 of pipe diameter 0.40)
    private static final float STRIP_S = 0.125f;
    private static final float STRIP_C = (float) Math.sqrt(1.0 - STRIP_S * STRIP_S); // 0.99215674f
    private static final float DIAG_D = 0.70710678f; // cos(45 deg) = sin(45 deg)

    // 8 metal wall segments: 2 per quadrant
    private static final float[] METAL_X0 = {
         STRIP_C,  DIAG_D,
        -STRIP_S, -DIAG_D,
        -STRIP_C, -DIAG_D,
         STRIP_S,  DIAG_D
    };
    private static final float[] METAL_Y0 = {
         STRIP_S,  DIAG_D,
         STRIP_C,  DIAG_D,
        -STRIP_S, -DIAG_D,
        -STRIP_C, -DIAG_D
    };
    private static final float[] METAL_X1 = {
         DIAG_D,   STRIP_S,
        -DIAG_D,  -STRIP_C,
        -DIAG_D,  -STRIP_S,
         DIAG_D,   STRIP_C
    };
    private static final float[] METAL_Y1 = {
         DIAG_D,   STRIP_C,
         DIAG_D,   STRIP_S,
        -DIAG_D,  -STRIP_C,
        -DIAG_D,  -STRIP_S
    };

    // 4 clear sight-glass viewing strips (width = 0.050 = 1/8 of pipe diameter)
    // Strip 0: East (+X)
    // Strip 1: Top / South (+Y / +Z)
    // Strip 2: West (-X)
    // Strip 3: Bottom / North (-Y / -Z)
    private static final float[] STRIP_X0 = {
         STRIP_C,
         STRIP_S,
        -STRIP_C,
        -STRIP_S
    };
    private static final float[] STRIP_Y0 = {
        -STRIP_S,
         STRIP_C,
         STRIP_S,
        -STRIP_C
    };
    private static final float[] STRIP_X1 = {
         STRIP_C,
        -STRIP_S,
        -STRIP_C,
         STRIP_S
    };
    private static final float[] STRIP_Y1 = {
         STRIP_S,
         STRIP_C,
        -STRIP_S,
        -STRIP_C
    };

    public static Identifier getTextureForMaterial(IndustrialPipeMaterial material) {
        return switch (material) {
            case WOOD -> TEXTURE_WOOD;
            case COPPER -> TEXTURE_COPPER;
            case IRON -> TEXTURE_IRON;
            case GOLD -> TEXTURE_GOLD;
            case DIAMOND -> TEXTURE_DIAMOND;
            case EMERALD -> TEXTURE_EMERALD;
            case BRASS -> TEXTURE_BRASS;
            case CAST_IRON -> TEXTURE_CAST_IRON;
            case VOID -> TEXTURE_VOID;
            case QUARTZ -> TEXTURE_QUARTZ;
            default -> TEXTURE_STEEL;
        };
    }

    public void render(
            Matrix4f matrix,
            VertexConsumer consumer,
            Map<Direction, PipeConnectionType> connections,
            IndustrialPipeMaterial material,
            int light,
            int overlay
    ) {
        render(matrix, consumer, connections, material, null, light, overlay);
    }

    public void render(
            Matrix4f matrix,
            VertexConsumer consumer,
            Map<Direction, PipeConnectionType> connections,
            IndustrialPipeMaterial material,
            Direction mergeOutput,
            int light,
            int overlay
    ) {
        boolean north = connections.getOrDefault(Direction.NORTH, PipeConnectionType.NONE).isConnected();
        boolean south = connections.getOrDefault(Direction.SOUTH, PipeConnectionType.NONE).isConnected();
        boolean west  = connections.getOrDefault(Direction.WEST, PipeConnectionType.NONE).isConnected();
        boolean east  = connections.getOrDefault(Direction.EAST, PipeConnectionType.NONE).isConnected();
        boolean down  = connections.getOrDefault(Direction.DOWN, PipeConnectionType.NONE).isConnected();
        boolean up    = connections.getOrDefault(Direction.UP, PipeConnectionType.NONE).isConnected();

        int connectionCount = (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) +
                              (east ? 1 : 0) + (down ? 1 : 0) + (up ? 1 : 0);

        // 1. STANDALONE UNCONNECTED PIPE:
        // Clean solid capped cylinder with zero overlapping surfaces or flickering
        if (connectionCount == 0) {
            renderCylinderZ(matrix, consumer, 0.20f, 0.80f, light, overlay);
            renderDiskZ(matrix, consumer, 0.20f, -1, light, overlay);
            renderDiskZ(matrix, consumer, 0.80f, 1, light, overlay);
            return;
        }

        // 2. DEAD-END / TERMINAL SINGLE ARM (connectionCount == 1):
        // Extends through the block to 0.80 (or from 0.20) and terminates with a clean round cap,
        // matching standalone pipe bounds and ensuring any attached pipe gate sits fully supported on the pipe cylinder.
        if (connectionCount == 1) {
            if (north) {
                renderCylinderZ(matrix, consumer, 0.0f, 0.80f, light, overlay);
                renderDiskZ(matrix, consumer, 0.80f, 1, light, overlay);
                if (connections.get(Direction.NORTH) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.NORTH, light, overlay);
            } else if (south) {
                renderCylinderZ(matrix, consumer, 0.20f, 1.0f, light, overlay);
                renderDiskZ(matrix, consumer, 0.20f, -1, light, overlay);
                if (connections.get(Direction.SOUTH) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.SOUTH, light, overlay);
            } else if (west) {
                renderCylinderX(matrix, consumer, 0.0f, 0.80f, light, overlay);
                renderDiskX(matrix, consumer, 0.80f, 1, light, overlay);
                if (connections.get(Direction.WEST) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.WEST, light, overlay);
            } else if (east) {
                renderCylinderX(matrix, consumer, 0.20f, 1.0f, light, overlay);
                renderDiskX(matrix, consumer, 0.20f, -1, light, overlay);
                if (connections.get(Direction.EAST) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.EAST, light, overlay);
            } else if (down) {
                renderCylinderY(matrix, consumer, 0.0f, 0.80f, light, overlay);
                renderDiskY(matrix, consumer, 0.80f, 1, light, overlay);
                if (connections.get(Direction.DOWN) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.DOWN, light, overlay);
            } else if (up) {
                renderCylinderY(matrix, consumer, 0.20f, 1.0f, light, overlay);
                renderDiskY(matrix, consumer, 0.20f, -1, light, overlay);
                if (connections.get(Direction.UP) == PipeConnectionType.MACHINE) renderMachineFlange(matrix, consumer, Direction.UP, light, overlay);
            }
            if (material == IndustrialPipeMaterial.QUARTZ && mergeOutput != null) {
                renderMergeOutputIndicator(matrix, consumer, mergeOutput, light, overlay);
            }
            if (material == IndustrialPipeMaterial.DIAMOND) {
                renderDiamondSortingBands(matrix, consumer, connections, light, overlay);
            }
            return;
        }

        // 3. PURE STRAIGHT LINE CONTINUOUS RUN (0.0 to 1.0):
        // Single continuous cylinder with zero joint breaks across 50+ blocks
        if (connectionCount == 2) {
            if (north && south) {
                renderCylinderZ(matrix, consumer, 0.0f, 1.0f, light, overlay);
                checkMachineFlanges(matrix, consumer, connections, Direction.NORTH, Direction.SOUTH, light, overlay);
                if (material == IndustrialPipeMaterial.QUARTZ && mergeOutput != null) renderMergeOutputIndicator(matrix, consumer, mergeOutput, light, overlay);
                if (material == IndustrialPipeMaterial.DIAMOND) renderDiamondSortingBands(matrix, consumer, connections, light, overlay);
                return;
            } else if (west && east) {
                renderCylinderX(matrix, consumer, 0.0f, 1.0f, light, overlay);
                checkMachineFlanges(matrix, consumer, connections, Direction.WEST, Direction.EAST, light, overlay);
                if (material == IndustrialPipeMaterial.QUARTZ && mergeOutput != null) renderMergeOutputIndicator(matrix, consumer, mergeOutput, light, overlay);
                if (material == IndustrialPipeMaterial.DIAMOND) renderDiamondSortingBands(matrix, consumer, connections, light, overlay);
                return;
            } else if (down && up) {
                renderCylinderY(matrix, consumer, 0.0f, 1.0f, light, overlay);
                checkMachineFlanges(matrix, consumer, connections, Direction.DOWN, Direction.UP, light, overlay);
                if (material == IndustrialPipeMaterial.QUARTZ && mergeOutput != null) renderMergeOutputIndicator(matrix, consumer, mergeOutput, light, overlay);
                if (material == IndustrialPipeMaterial.DIAMOND) renderDiamondSortingBands(matrix, consumer, connections, light, overlay);
                return;
            }
        }

        // 4. CORNERS, TEES, CROSSES, AND MULTI-WAY JUNCTIONS:
        // Render each connected arm from block boundary entering the spherical fitting hub
        if (north) renderCylinderZ(matrix, consumer, 0.0f, 0.5f - HUB_STOP, light, overlay);
        if (south) renderCylinderZ(matrix, consumer, 0.5f + HUB_STOP, 1.0f, light, overlay);
        if (west)  renderCylinderX(matrix, consumer, 0.0f, 0.5f - HUB_STOP, light, overlay);
        if (east)  renderCylinderX(matrix, consumer, 0.5f + HUB_STOP, 1.0f, light, overlay);
        if (down)  renderCylinderY(matrix, consumer, 0.0f, 0.5f - HUB_STOP, light, overlay);
        if (up)    renderCylinderY(matrix, consumer, 0.5f + HUB_STOP, 1.0f, light, overlay);

        // Solid spherical fitting hub cleanly bridges all connected arms with zero Z-fighting
        renderCenterJunction(matrix, consumer, light, overlay);

        // 5. MACHINE CONNECTION FLANGES:
        // Bolted industrial flanges strictly at machine/container connection terminals
        for (Direction dir : Direction.values()) {
            if (connections.getOrDefault(dir, PipeConnectionType.NONE) == PipeConnectionType.MACHINE) {
                renderMachineFlange(matrix, consumer, dir, light, overlay);
            }
        }

        // 6. MERGE PIPE DIRECTIONAL OUTPUT INDICATOR:
        if (material == IndustrialPipeMaterial.QUARTZ && mergeOutput != null) {
            renderMergeOutputIndicator(matrix, consumer, mergeOutput, light, overlay);
        }

        // 7. DIAMOND PIPE COLOR-CODED SORTING BANDS AT CONNECTION ENDS:
        if (material == IndustrialPipeMaterial.DIAMOND) {
            renderDiamondSortingBands(matrix, consumer, connections, light, overlay);
        }
    }

    private void checkMachineFlanges(
            Matrix4f m, VertexConsumer c,
            Map<Direction, PipeConnectionType> connections,
            Direction dir1, Direction dir2,
            int light, int overlay
    ) {
        if (connections.get(dir1) == PipeConnectionType.MACHINE) renderMachineFlange(m, c, dir1, light, overlay);
        if (connections.get(dir2) == PipeConnectionType.MACHINE) renderMachineFlange(m, c, dir2, light, overlay);
    }

    /**
     * Renders the 8 curved metal quadrant walls of the cylinder along Z, leaving 4 clear sight-glass apertures.
     * Guaranteed single outward-facing quads with CCW winding to eliminate Z-fighting.
     */
    private void renderCylinderZ(Matrix4f m, VertexConsumer c, float z0, float z1, int light, int overlay) {
        for (int k = 0; k < 8; k++) {
            float x0 = CX + PIPE_RADIUS * METAL_X0[k];
            float y0 = CY + PIPE_RADIUS * METAL_Y0[k];
            float x1 = CX + PIPE_RADIUS * METAL_X1[k];
            float y1 = CY + PIPE_RADIUS * METAL_Y1[k];

            float u0 = (float) k / 8.0f;
            float u1 = (float) (k + 1) / 8.0f;
            float v0 = z0;
            float v1 = z1;

            float nx = 0.5f * (METAL_X0[k] + METAL_X1[k]);
            float ny = 0.5f * (METAL_Y0[k] + METAL_Y1[k]);

            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
        }
    }

    /**
     * Renders the 8 curved metal quadrant walls of the cylinder along X, leaving 4 clear sight-glass apertures.
     * Guaranteed single outward-facing quads with CCW winding to eliminate Z-fighting.
     */
    private void renderCylinderX(Matrix4f m, VertexConsumer c, float x0, float x1, int light, int overlay) {
        for (int k = 0; k < 8; k++) {
            float y0 = CY + PIPE_RADIUS * METAL_X0[k];
            float z0 = CZ + PIPE_RADIUS * METAL_Y0[k];
            float y1 = CY + PIPE_RADIUS * METAL_X1[k];
            float z1 = CZ + PIPE_RADIUS * METAL_Y1[k];

            float u0 = (float) k / 8.0f;
            float u1 = (float) (k + 1) / 8.0f;
            float v0 = x0;
            float v1 = x1;

            float ny = 0.5f * (METAL_X0[k] + METAL_X1[k]);
            float nz = 0.5f * (METAL_Y0[k] + METAL_Y1[k]);

            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
        }
    }

    /**
     * Renders the 8 curved metal quadrant walls of the cylinder along Y, leaving 4 clear sight-glass apertures.
     * Guaranteed single outward-facing quads with CCW winding to eliminate Z-fighting.
     */
    private void renderCylinderY(Matrix4f m, VertexConsumer c, float y0, float y1, int light, int overlay) {
        for (int k = 0; k < 8; k++) {
            float x0 = CX + PIPE_RADIUS * METAL_X0[k];
            float z0 = CZ + PIPE_RADIUS * METAL_Y0[k];
            float x1 = CX + PIPE_RADIUS * METAL_X1[k];
            float z1 = CZ + PIPE_RADIUS * METAL_Y1[k];

            float u0 = (float) k / 8.0f;
            float u1 = (float) (k + 1) / 8.0f;
            float v0 = y0;
            float v1 = y1;

            float nx = 0.5f * (METAL_X0[k] + METAL_X1[k]);
            float nz = 0.5f * (METAL_Y0[k] + METAL_Y1[k]);

            c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
        }
    }

    /**
     * Renders a solid faceted fitting hub at (0.5, 0.5, 0.5).
     * Single-layer outward quads with zero duplicate coplanar faces and no polar singularities.
     */
    private void renderCenterJunction(Matrix4f m, VertexConsumer c, int light, int overlay) {
        float r = HUB_RADIUS;
        int latBands = 6;
        int lonBands = SEGMENTS;

        // Middle latitude bands (clean non-degenerate 4-vertex quads)
        for (int lat = 0; lat < latBands; lat++) {
            double theta0 = Math.PI * (double) lat / latBands;
            double theta1 = Math.PI * (double) (lat + 1) / latBands;

            float y0 = CY + r * (float) Math.cos(theta0);
            float y1 = CY + r * (float) Math.cos(theta1);
            float r0 = r * (float) Math.sin(theta0);
            float r1 = r * (float) Math.sin(theta1);

            float v0 = (float) lat / latBands;
            float v1 = (float) (lat + 1) / latBands;

            for (int lon = 0; lon < lonBands; lon++) {
                double phi0 = 2.0 * Math.PI * (double) lon / lonBands;
                double phi1 = 2.0 * Math.PI * (double) (lon + 1) / lonBands;

                float x00 = CX + r0 * (float) Math.cos(phi0);
                float z00 = CZ + r0 * (float) Math.sin(phi0);

                float x01 = CX + r0 * (float) Math.cos(phi1);
                float z01 = CZ + r0 * (float) Math.sin(phi1);

                float x10 = CX + r1 * (float) Math.cos(phi0);
                float z10 = CZ + r1 * (float) Math.sin(phi0);

                float x11 = CX + r1 * (float) Math.cos(phi1);
                float z11 = CZ + r1 * (float) Math.sin(phi1);

                float u0 = (float) lon / lonBands;
                float u1 = (float) (lon + 1) / lonBands;

                float nx = (float) (Math.sin((theta0 + theta1) * 0.5) * Math.cos((phi0 + phi1) * 0.5));
                float ny = (float) Math.cos((theta0 + theta1) * 0.5);
                float nz = (float) (Math.sin((theta0 + theta1) * 0.5) * Math.sin((phi0 + phi1) * 0.5));

                if (lat == 0) {
                    // Top pole: quad fan where apex shares center UV
                    c.addVertex(m, CX, CY + r, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv((u0 + u1) * 0.5f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x11, y1, z11).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x10, y1, z10).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, CX, CY + r, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv((u0 + u1) * 0.5f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                } else if (lat == latBands - 1) {
                    // Bottom pole: quad fan where base shares center UV
                    c.addVertex(m, CX, CY - r, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv((u0 + u1) * 0.5f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x00, y0, z00).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x01, y0, z01).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, CX, CY - r, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv((u0 + u1) * 0.5f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                } else {
                    // Standard body band
                    c.addVertex(m, x00, y0, z00).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x01, y0, z01).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x11, y1, z11).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                    c.addVertex(m, x10, y1, z10).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
                }
            }
        }
    }

    /**
     * Circular disk cap for standalone pipe ends along Z.
     */
    private void renderDiskZ(Matrix4f m, VertexConsumer c, float z, int normalZ, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + PIPE_RADIUS * COS[i];
            float y0 = CY + PIPE_RADIUS * SIN[i];
            float x1 = CX + PIPE_RADIUS * COS[i + 1];
            float y1 = CY + PIPE_RADIUS * SIN[i + 1];

            if (normalZ > 0) {
                c.addVertex(m, CX, CY, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, x0, y0, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, x1, y1, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, CX, CY, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            } else {
                c.addVertex(m, CX, CY, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, x1, y1, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, x0, y0, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, CX, CY, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            }
        }
    }

    /**
     * Circular disk cap for pipe ends along X.
     */
    private void renderDiskX(Matrix4f m, VertexConsumer c, float x, int normalX, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float y0 = CY + PIPE_RADIUS * COS[i];
            float z0 = CZ + PIPE_RADIUS * SIN[i];
            float y1 = CY + PIPE_RADIUS * COS[i + 1];
            float z1 = CZ + PIPE_RADIUS * SIN[i + 1];

            if (normalX > 0) {
                c.addVertex(m, x, CY, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, CY, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            } else {
                c.addVertex(m, x, CY, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, x, CY, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            }
        }
    }

    /**
     * Circular disk cap for pipe ends along Y.
     */
    private void renderDiskY(Matrix4f m, VertexConsumer c, float y, int normalY, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + PIPE_RADIUS * COS[i];
            float z0 = CZ + PIPE_RADIUS * SIN[i];
            float x1 = CX + PIPE_RADIUS * COS[i + 1];
            float z1 = CZ + PIPE_RADIUS * SIN[i + 1];

            if (normalY > 0) {
                c.addVertex(m, CX, y, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, x0, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, x1, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, CX, y, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            } else {
                c.addVertex(m, CX, y, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, x1, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.6f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, x0, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.4f, 0.4f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, CX, y, CZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.5f, 0.5f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            }
        }
    }

    /**
     * Renders an industrial bolted connection flange with outer rim and collar face at machine interfaces.
     * Offset slightly (0.001f) from the block boundary to eliminate Z-fighting with adjacent machine blocks.
     */
    private void renderMachineFlange(Matrix4f m, VertexConsumer c, Direction dir, int light, int overlay) {
        float flangeDepth = 1.2f / 16.0f;
        float eps = 0.001f;

        switch (dir) {
            case NORTH -> renderFlangeZ(m, c, eps, flangeDepth, -1, light, overlay);
            case SOUTH -> renderFlangeZ(m, c, 1.0f - eps, 1.0f - flangeDepth, 1, light, overlay);
            case WEST  -> renderFlangeX(m, c, eps, flangeDepth, -1, light, overlay);
            case EAST  -> renderFlangeX(m, c, 1.0f - eps, 1.0f - flangeDepth, 1, light, overlay);
            case DOWN  -> renderFlangeY(m, c, eps, flangeDepth, -1, light, overlay);
            case UP    -> renderFlangeY(m, c, 1.0f - eps, 1.0f - flangeDepth, 1, light, overlay);
        }
    }

    private void renderFlangeZ(Matrix4f m, VertexConsumer c, float zFace, float zBack, int normalZ, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + FLANGE_RADIUS * COS[i];
            float y0 = CY + FLANGE_RADIUS * SIN[i];
            float x1 = CX + FLANGE_RADIUS * COS[i + 1];
            float y1 = CY + FLANGE_RADIUS * SIN[i + 1];

            // Outer flange collar rim
            c.addVertex(m, x0, y0, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(COS[i], SIN[i], 0.0f);
            c.addVertex(m, x1, y1, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(COS[i + 1], SIN[i + 1], 0.0f);
            c.addVertex(m, x1, y1, zBack).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(COS[i + 1], SIN[i + 1], 0.0f);
            c.addVertex(m, x0, y0, zBack).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(COS[i], SIN[i], 0.0f);

            // Annular face of flange
            float xi0 = CX + PIPE_RADIUS * COS[i];
            float yi0 = CY + PIPE_RADIUS * SIN[i];
            float xi1 = CX + PIPE_RADIUS * COS[i + 1];
            float yi1 = CY + PIPE_RADIUS * SIN[i + 1];

            if (normalZ > 0) {
                c.addVertex(m, x0, y0, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, x1, y1, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, xi1, yi1, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
                c.addVertex(m, xi0, yi0, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            } else {
                c.addVertex(m, x1, y1, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, x0, y0, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, xi0, yi0, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                c.addVertex(m, xi1, yi1, zFace).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            }
        }
    }

    private void renderFlangeX(Matrix4f m, VertexConsumer c, float xFace, float xBack, int normalX, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float y0 = CY + FLANGE_RADIUS * COS[i];
            float z0 = CZ + FLANGE_RADIUS * SIN[i];
            float y1 = CY + FLANGE_RADIUS * COS[i + 1];
            float z1 = CZ + FLANGE_RADIUS * SIN[i + 1];

            c.addVertex(m, xFace, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, COS[i], SIN[i]);
            c.addVertex(m, xFace, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, COS[i + 1], SIN[i + 1]);
            c.addVertex(m, xBack, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, COS[i + 1], SIN[i + 1]);
            c.addVertex(m, xBack, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, COS[i], SIN[i]);

            float yi0 = CY + PIPE_RADIUS * COS[i];
            float zi0 = CZ + PIPE_RADIUS * SIN[i];
            float yi1 = CY + PIPE_RADIUS * COS[i + 1];
            float zi1 = CZ + PIPE_RADIUS * SIN[i + 1];

            if (normalX > 0) {
                c.addVertex(m, xFace, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, yi1, zi1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, yi0, zi0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            } else {
                c.addVertex(m, xFace, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, yi0, zi0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
                c.addVertex(m, xFace, yi1, zi1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            }
        }
    }

    private void renderFlangeY(Matrix4f m, VertexConsumer c, float yFace, float yBack, int normalY, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + FLANGE_RADIUS * COS[i];
            float z0 = CZ + FLANGE_RADIUS * SIN[i];
            float x1 = CX + FLANGE_RADIUS * COS[i + 1];
            float z1 = CZ + FLANGE_RADIUS * SIN[i + 1];

            c.addVertex(m, x0, yFace, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(COS[i], 0.0f, SIN[i]);
            c.addVertex(m, x1, yFace, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(COS[i + 1], 0.0f, SIN[i + 1]);
            c.addVertex(m, x1, yBack, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(COS[i + 1], 0.0f, SIN[i + 1]);
            c.addVertex(m, x0, yBack, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(COS[i], 0.0f, SIN[i]);

            float xi0 = CX + PIPE_RADIUS * COS[i];
            float zi0 = CZ + PIPE_RADIUS * SIN[i];
            float xi1 = CX + PIPE_RADIUS * COS[i + 1];
            float zi1 = CZ + PIPE_RADIUS * SIN[i + 1];

            if (normalY > 0) {
                c.addVertex(m, x0, yFace, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, x1, yFace, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, xi1, yFace, zi1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                c.addVertex(m, xi0, yFace, zi0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            } else {
                c.addVertex(m, x1, yFace, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, x0, yFace, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, xi0, yFace, zi0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
                c.addVertex(m, xi1, yFace, zi1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            }
        }
    }

    public static int getDiamondDirectionColor(Direction dir) {
        return switch (dir) {
            case DOWN  -> 0xFF22C55E; // Green
            case UP    -> 0xFFEAB308; // Yellow
            case NORTH -> 0xFF3B82F6; // Blue
            case SOUTH -> 0xFFEF4444; // Red
            case WEST  -> 0xFFA855F7; // Purple
            case EAST  -> 0xFFF8FAFC; // White
        };
    }

    private void renderDiamondSortingBands(
            Matrix4f m,
            VertexConsumer c,
            Map<Direction, PipeConnectionType> connections,
            int light,
            int overlay
    ) {
        float r = PIPE_RADIUS + 0.016f;
        for (Direction dir : Direction.values()) {
            PipeConnectionType conn = connections.getOrDefault(dir, PipeConnectionType.NONE);
            if (!conn.isConnected()) continue;

            int argb = getDiamondDirectionColor(dir);
            float rCol = ((argb >> 16) & 0xFF) / 255.0f;
            float gCol = ((argb >> 8) & 0xFF) / 255.0f;
            float bCol = (argb & 0xFF) / 255.0f;

            // Place the color band right at the end of the connection (or flush behind the machine flange)
            boolean hasFlange = (conn == PipeConnectionType.MACHINE);
            float low0  = hasFlange ? 0.076f : 0.015f;
            float low1  = hasFlange ? 0.220f : 0.165f;
            float high0 = hasFlange ? 0.780f : 0.835f;
            float high1 = hasFlange ? 0.924f : 0.985f;

            switch (dir) {
                case NORTH -> renderCollarZ(m, c, r, low0, low1, rCol, gCol, bCol, light, overlay);
                case SOUTH -> renderCollarZ(m, c, r, high0, high1, rCol, gCol, bCol, light, overlay);
                case WEST  -> renderCollarX(m, c, r, low0, low1, rCol, gCol, bCol, light, overlay);
                case EAST  -> renderCollarX(m, c, r, high0, high1, rCol, gCol, bCol, light, overlay);
                case DOWN  -> renderCollarY(m, c, r, low0, low1, rCol, gCol, bCol, light, overlay);
                case UP    -> renderCollarY(m, c, r, high0, high1, rCol, gCol, bCol, light, overlay);
            }
        }
    }

    private void renderMergeOutputIndicator(Matrix4f m, VertexConsumer c, Direction outDir, int light, int overlay) {
        float r = PIPE_RADIUS + 0.015f;
        float rCol = 1.0f, gCol = 0.82f, bCol = 0.22f; // Vivid golden output indicator collar
        int indLight = 0x00F000F0; // Brightly visible indicator

        switch (outDir) {
            case NORTH -> renderCollarZ(m, c, r, 0.16f, 0.32f, rCol, gCol, bCol, indLight, overlay);
            case SOUTH -> renderCollarZ(m, c, r, 0.68f, 0.84f, rCol, gCol, bCol, indLight, overlay);
            case WEST  -> renderCollarX(m, c, r, 0.16f, 0.32f, rCol, gCol, bCol, indLight, overlay);
            case EAST  -> renderCollarX(m, c, r, 0.68f, 0.84f, rCol, gCol, bCol, indLight, overlay);
            case DOWN  -> renderCollarY(m, c, r, 0.16f, 0.32f, rCol, gCol, bCol, indLight, overlay);
            case UP    -> renderCollarY(m, c, r, 0.68f, 0.84f, rCol, gCol, bCol, indLight, overlay);
        }
    }

    private void renderCollarZ(Matrix4f m, VertexConsumer c, float r, float z0, float z1, float red, float green, float blue, int light, int overlay) {
        float uv = 0.01f; // Samples pure white (255,255,255) swatch at top-left of texture
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + r * COS[i];
            float y0 = CY + r * SIN[i];
            float x1 = CX + r * COS[i + 1];
            float y1 = CY + r * SIN[i + 1];

            float xi0 = CX + PIPE_RADIUS * COS[i];
            float yi0 = CY + PIPE_RADIUS * SIN[i];
            float xi1 = CX + PIPE_RADIUS * COS[i + 1];
            float yi1 = CY + PIPE_RADIUS * SIN[i + 1];

            float nx = 0.5f * (COS[i] + COS[i + 1]);
            float ny = 0.5f * (SIN[i] + SIN[i + 1]);

            // Outer cylindrical band
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x0, y0, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);

            // Front annular step (-Z)
            c.addVertex(m, x1, y1, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            c.addVertex(m, xi0, yi0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            c.addVertex(m, xi1, yi1, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);

            // Back annular step (+Z)
            c.addVertex(m, x0, y0, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, xi1, yi1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, xi0, yi0, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
        }
    }

    private void renderCollarX(Matrix4f m, VertexConsumer c, float r, float x0, float x1, float red, float green, float blue, int light, int overlay) {
        float uv = 0.01f;
        for (int i = 0; i < SEGMENTS; i++) {
            float y0 = CY + r * COS[i];
            float z0 = CZ + r * SIN[i];
            float y1 = CY + r * COS[i + 1];
            float z1 = CZ + r * SIN[i + 1];

            float yi0 = CY + PIPE_RADIUS * COS[i];
            float zi0 = CZ + PIPE_RADIUS * SIN[i];
            float yi1 = CY + PIPE_RADIUS * COS[i + 1];
            float zi1 = CZ + PIPE_RADIUS * SIN[i + 1];

            float ny = 0.5f * (COS[i] + COS[i + 1]);
            float nz = 0.5f * (SIN[i] + SIN[i + 1]);

            // Outer cylindrical band
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x0, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);

            // West annular step (-X)
            c.addVertex(m, x0, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            c.addVertex(m, x0, yi0, zi0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            c.addVertex(m, x0, yi1, zi1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);

            // East annular step (+X)
            c.addVertex(m, x1, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            c.addVertex(m, x1, yi1, zi1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            c.addVertex(m, x1, yi0, zi0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
        }
    }

    private void renderCollarY(Matrix4f m, VertexConsumer c, float r, float y0, float y1, float red, float green, float blue, int light, int overlay) {
        float uv = 0.01f;
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + r * COS[i];
            float z0 = CZ + r * SIN[i];
            float x1 = CX + r * COS[i + 1];
            float z1 = CZ + r * SIN[i + 1];

            float xi0 = CX + PIPE_RADIUS * COS[i];
            float zi0 = CZ + PIPE_RADIUS * SIN[i];
            float xi1 = CX + PIPE_RADIUS * COS[i + 1];
            float zi1 = CZ + PIPE_RADIUS * SIN[i + 1];

            float nx = 0.5f * (COS[i] + COS[i + 1]);
            float nz = 0.5f * (SIN[i] + SIN[i + 1]);

            // Outer cylindrical band
            c.addVertex(m, x1, y0, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y1, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);

            // Down annular step (-Y)
            c.addVertex(m, x1, y0, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            c.addVertex(m, xi0, y0, zi0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            c.addVertex(m, xi1, y0, zi1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);

            // Up annular step (+Y)
            c.addVertex(m, x0, y1, z0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            c.addVertex(m, xi1, y1, zi1).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            c.addVertex(m, xi0, y1, zi0).setColor(red, green, blue, 1.0f).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
        }
    }

    /**
     * Renders longitudinal sight-glass inspection strips along connected pipe arms.
     */
    public void renderSightGlassWindows(
            Matrix4f m,
            VertexConsumer c,
            Map<Direction, PipeConnectionType> connections,
            int light,
            int overlay
    ) {
        boolean north = connections.getOrDefault(Direction.NORTH, PipeConnectionType.NONE).isConnected();
        boolean south = connections.getOrDefault(Direction.SOUTH, PipeConnectionType.NONE).isConnected();
        boolean west  = connections.getOrDefault(Direction.WEST, PipeConnectionType.NONE).isConnected();
        boolean east  = connections.getOrDefault(Direction.EAST, PipeConnectionType.NONE).isConnected();
        boolean down  = connections.getOrDefault(Direction.DOWN, PipeConnectionType.NONE).isConnected();
        boolean up    = connections.getOrDefault(Direction.UP, PipeConnectionType.NONE).isConnected();

        int connectionCount = (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) +
                              (east ? 1 : 0) + (down ? 1 : 0) + (up ? 1 : 0);

        if (connectionCount == 0) {
            renderWindowZ(m, c, 0.22f, 0.78f, light, overlay);
            return;
        }

        if (connectionCount == 1) {
            if (north) renderWindowZ(m, c, 0.05f, 0.78f, light, overlay);
            else if (south) renderWindowZ(m, c, 0.22f, 0.95f, light, overlay);
            else if (west) renderWindowX(m, c, 0.05f, 0.78f, light, overlay);
            else if (east) renderWindowX(m, c, 0.22f, 0.95f, light, overlay);
            else if (down) renderWindowY(m, c, 0.05f, 0.78f, light, overlay);
            else if (up) renderWindowY(m, c, 0.22f, 0.95f, light, overlay);
            return;
        }

        if (connectionCount == 2) {
            if (north && south) {
                renderWindowZ(m, c, 0.05f, 0.95f, light, overlay);
                return;
            } else if (west && east) {
                renderWindowX(m, c, 0.05f, 0.95f, light, overlay);
                return;
            } else if (down && up) {
                renderWindowY(m, c, 0.05f, 0.95f, light, overlay);
                return;
            }
        }

        // Multi-branch fittings
        if (north) renderWindowZ(m, c, 0.05f, 0.5f - HUB_STOP, light, overlay);
        if (south) renderWindowZ(m, c, 0.5f + HUB_STOP, 0.95f, light, overlay);
        if (west)  renderWindowX(m, c, 0.05f, 0.5f - HUB_STOP, light, overlay);
        if (east)  renderWindowX(m, c, 0.5f + HUB_STOP, 0.95f, light, overlay);
        if (down)  renderWindowY(m, c, 0.05f, 0.5f - HUB_STOP, light, overlay);
        if (up)    renderWindowY(m, c, 0.5f + HUB_STOP, 0.95f, light, overlay);
    }

    private void renderWindowZ(Matrix4f m, VertexConsumer c, float z0, float z1, int light, int overlay) {
        for (int k = 0; k < 4; k++) {
            float x0 = CX + PIPE_RADIUS * STRIP_X0[k];
            float y0 = CY + PIPE_RADIUS * STRIP_Y0[k];
            float x1 = CX + PIPE_RADIUS * STRIP_X1[k];
            float y1 = CY + PIPE_RADIUS * STRIP_Y1[k];

            float nx = 0.5f * (STRIP_X0[k] + STRIP_X1[k]);
            float ny = 0.5f * (STRIP_Y0[k] + STRIP_Y1[k]);

            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, z0).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, z0).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, z1).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, z1).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
        }
    }

    private void renderWindowX(Matrix4f m, VertexConsumer c, float x0, float x1, int light, int overlay) {
        for (int k = 0; k < 4; k++) {
            float y0 = CY + PIPE_RADIUS * STRIP_X0[k];
            float z0 = CZ + PIPE_RADIUS * STRIP_Y0[k];
            float y1 = CY + PIPE_RADIUS * STRIP_X1[k];
            float z1 = CZ + PIPE_RADIUS * STRIP_Y1[k];

            float ny = 0.5f * (STRIP_X0[k] + STRIP_X1[k]);
            float nz = 0.5f * (STRIP_Y0[k] + STRIP_Y1[k]);

            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, x0).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, x0).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, x1).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, x1).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
        }
    }

    private void renderWindowY(Matrix4f m, VertexConsumer c, float y0, float y1, int light, int overlay) {
        for (int k = 0; k < 4; k++) {
            float x0 = CX + PIPE_RADIUS * STRIP_X0[k];
            float z0 = CZ + PIPE_RADIUS * STRIP_Y0[k];
            float x1 = CX + PIPE_RADIUS * STRIP_X1[k];
            float z1 = CZ + PIPE_RADIUS * STRIP_Y1[k];

            float nx = 0.5f * (STRIP_X0[k] + STRIP_X1[k]);
            float nz = 0.5f * (STRIP_Y0[k] + STRIP_Y1[k]);

            c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, y0).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, y0).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(0.0f, y1).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 0.75f).setUv(1.0f, y1).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
        }
    }

    /**
     * Renders stored fluid level core visible directly through the sight-glass windows.
     */
    public void renderFluidCore(
            Matrix4f m,
            VertexConsumer c,
            Map<Direction, PipeConnectionType> connections,
            int fluidColor,
            float fillRatio,
            int light,
            int overlay
    ) {
        float r = (PIPE_RADIUS - 0.005f) * Math.max(0.4f, fillRatio);
        float red = ((fluidColor >> 16) & 0xFF) / 255.0f;
        float green = ((fluidColor >> 8) & 0xFF) / 255.0f;
        float blue = (fluidColor & 0xFF) / 255.0f;
        float alpha = 0.90f;
        float uv = 0.01f;

        boolean north = connections.getOrDefault(Direction.NORTH, PipeConnectionType.NONE).isConnected();
        boolean south = connections.getOrDefault(Direction.SOUTH, PipeConnectionType.NONE).isConnected();
        boolean west  = connections.getOrDefault(Direction.WEST, PipeConnectionType.NONE).isConnected();
        boolean east  = connections.getOrDefault(Direction.EAST, PipeConnectionType.NONE).isConnected();
        boolean down  = connections.getOrDefault(Direction.DOWN, PipeConnectionType.NONE).isConnected();
        boolean up    = connections.getOrDefault(Direction.UP, PipeConnectionType.NONE).isConnected();

        int connectionCount = (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) +
                              (east ? 1 : 0) + (down ? 1 : 0) + (up ? 1 : 0);

        if (connectionCount == 0) {
            renderFluidCylinderZ(m, c, r, 0.22f, 0.78f, red, green, blue, alpha, uv, light, overlay);
            return;
        }

        if (connectionCount == 1) {
            if (north) renderFluidCylinderZ(m, c, r, 0.02f, 0.78f, red, green, blue, alpha, uv, light, overlay);
            else if (south) renderFluidCylinderZ(m, c, r, 0.22f, 0.98f, red, green, blue, alpha, uv, light, overlay);
            else if (west) renderFluidCylinderX(m, c, r, 0.02f, 0.78f, red, green, blue, alpha, uv, light, overlay);
            else if (east) renderFluidCylinderX(m, c, r, 0.22f, 0.98f, red, green, blue, alpha, uv, light, overlay);
            else if (down) renderFluidCylinderY(m, c, r, 0.02f, 0.78f, red, green, blue, alpha, uv, light, overlay);
            else if (up) renderFluidCylinderY(m, c, r, 0.22f, 0.98f, red, green, blue, alpha, uv, light, overlay);
            return;
        }

        if (connectionCount == 2) {
            if (north && south) {
                renderFluidCylinderZ(m, c, r, 0.02f, 0.98f, red, green, blue, alpha, uv, light, overlay);
                return;
            } else if (west && east) {
                renderFluidCylinderX(m, c, r, 0.02f, 0.98f, red, green, blue, alpha, uv, light, overlay);
                return;
            } else if (down && up) {
                renderFluidCylinderY(m, c, r, 0.02f, 0.98f, red, green, blue, alpha, uv, light, overlay);
                return;
            }
        }

        if (north) renderFluidCylinderZ(m, c, r, 0.02f, 0.5f, red, green, blue, alpha, uv, light, overlay);
        if (south) renderFluidCylinderZ(m, c, r, 0.5f, 0.98f, red, green, blue, alpha, uv, light, overlay);
        if (west)  renderFluidCylinderX(m, c, r, 0.02f, 0.5f, red, green, blue, alpha, uv, light, overlay);
        if (east)  renderFluidCylinderX(m, c, r, 0.5f, 0.98f, red, green, blue, alpha, uv, light, overlay);
        if (down)  renderFluidCylinderY(m, c, r, 0.02f, 0.5f, red, green, blue, alpha, uv, light, overlay);
        if (up)    renderFluidCylinderY(m, c, r, 0.5f, 0.98f, red, green, blue, alpha, uv, light, overlay);
    }

    private void renderFluidCylinderZ(Matrix4f m, VertexConsumer c, float r, float z0, float z1, float red, float green, float blue, float alpha, float uv, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + r * COS[i];
            float y0 = CY + r * SIN[i];
            float x1 = CX + r * COS[i + 1];
            float y1 = CY + r * SIN[i + 1];

            float nx = 0.5f * (COS[i] + COS[i + 1]);
            float ny = 0.5f * (SIN[i] + SIN[i + 1]);

            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
            c.addVertex(m, x0, y0, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, ny, 0.0f);
        }
    }

    private void renderFluidCylinderX(Matrix4f m, VertexConsumer c, float r, float x0, float x1, float red, float green, float blue, float alpha, float uv, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float y0 = CY + r * COS[i];
            float z0 = CZ + r * SIN[i];
            float y1 = CY + r * COS[i + 1];
            float z1 = CZ + r * SIN[i + 1];

            float ny = 0.5f * (COS[i] + COS[i + 1]);
            float nz = 0.5f * (SIN[i] + SIN[i + 1]);

            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x0, y1, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
            c.addVertex(m, x1, y0, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(0.0f, ny, nz);
        }
    }

    private void renderFluidCylinderY(Matrix4f m, VertexConsumer c, float r, float y0, float y1, float red, float green, float blue, float alpha, float uv, int light, int overlay) {
        for (int i = 0; i < SEGMENTS; i++) {
            float x0 = CX + r * COS[i];
            float z0 = CZ + r * SIN[i];
            float x1 = CX + r * COS[i + 1];
            float z1 = CZ + r * SIN[i + 1];

            float nx = 0.5f * (COS[i] + COS[i + 1]);
            float nz = 0.5f * (SIN[i] + SIN[i + 1]);

            c.addVertex(m, x1, y0, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x0, y1, z0).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
            c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(uv, uv).setOverlay(overlay).setLight(light).setNormal(nx, 0.0f, nz);
        }
    }
}
