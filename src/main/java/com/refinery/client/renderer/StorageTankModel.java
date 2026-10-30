package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.block.entity.StorageTankSize;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * High-Fidelity Monolithic 3D Model for the Assembled Liquid Storage Tank:
 * Faithfully matches Glass-Fused-to-Steel (GFS) / Enamel Bolted Industrial Storage Tanks:
 * - Octagonal/cylindrical glossy dark enamel shell with bolted horizontal/vertical seams
 * - Concrete foundation ring wall / curb at the base
 * - Galvanized circumferential wind stiffener rings at multiple elevations
 * - White/galvanized vertical access ladder with safety cage hoops and intermediate rest platform
 * - Top roof perimeter safety handrails & guardrail stanchions
 * - Bottom center drain nozzle and top center fill nozzle with flanged connections
 * - Circular bolted manway door and red standpipe
 * - Dynamically scaled to all 6 supported sizes (3x3x3, 3x3x6, 3x3x9, 5x5x5, 5x5x7, 5x5x9)
 */
@OnlyIn(Dist.CLIENT)
public class StorageTankModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/storage_tank.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;

    public record UVRect(float u0, float v0, float u1, float v1) {
        public static UVRect of(int x, int y, int w, int h) {
            return new UVRect(
                    (x + 0.15f) / TEX_W,
                    (y + 0.15f) / TEX_H,
                    (x + w - 0.15f) / TEX_W,
                    (y + h - 0.15f) / TEX_H
            );
        }
    }

    public record Cuboid(
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            UVRect north, UVRect south,
            UVRect west, UVRect east,
            UVRect up, UVRect down
    ) {
        public static Cuboid uniform(float x0, float y0, float z0, float x1, float y1, float z1, UVRect uv) {
            return new Cuboid(x0, y0, z0, x1, y1, z1, uv, uv, uv, uv, uv, uv);
        }
    }

    // Atlas UV Regions (matching StorageTankAssetGenerator 256x256 layout)
    private static final UVRect UV_ENAMEL_WALL    = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_WIND_RING      = UVRect.of(96, 0, 48, 48);
    private static final UVRect UV_LADDER_STEEL   = UVRect.of(144, 0, 48, 48);
    private static final UVRect UV_CONCRETE       = UVRect.of(192, 0, 64, 48);
    private static final UVRect UV_ROOF_CONE      = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_DRAIN_NOZZLE   = UVRect.of(64, 48, 48, 48);
    private static final UVRect UV_FILL_NOZZLE    = UVRect.of(112, 48, 48, 48);
    private static final UVRect UV_MANWAY_DOOR    = UVRect.of(160, 48, 48, 48);
    private static final UVRect UV_HANDRAIL_STEEL = UVRect.of(208, 48, 48, 48);

    private final Map<StorageTankSize, List<Cuboid>> sizeGeometry = new EnumMap<>(StorageTankSize.class);

    public StorageTankModel() {
        for (StorageTankSize size : StorageTankSize.values()) {
            this.sizeGeometry.put(size, buildGeometryForSize(size));
        }
    }

    private List<Cuboid> buildGeometryForSize(StorageTankSize size) {
        List<Cuboid> list = new ArrayList<>();

        float cx = 8.0f;
        float cz = 8.0f;
        float r = (size.getRadius() == 1) ? 22.0f : 38.0f;
        float height = size.getHeight() * 16.0f;

        // 1. Concrete Foundation Ring Curb (Y: 0..3)
        float fR = r + 1.5f;
        list.add(Cuboid.uniform(cx - fR, 0.0f, cz - fR * 0.7f, cx + fR, 3.0f, cz + fR * 0.7f, UV_CONCRETE));
        list.add(Cuboid.uniform(cx - fR * 0.7f, 0.0f, cz - fR, cx + fR * 0.7f, 3.0f, cz + fR, UV_CONCRETE));
        list.add(Cuboid.uniform(cx - fR * 0.88f, 0.0f, cz - fR * 0.88f, cx + fR * 0.88f, 3.0f, cz + fR * 0.88f, UV_CONCRETE));

        // 2. Main Cylindrical GFS Enamel Shell (Octagonal Prism profile)
        float s0 = r;
        float s1 = r * 0.707f;
        float s2 = r * 0.924f;
        float s3 = r * 0.383f;

        // Cardinal & Diagonal Wall Panels
        list.add(Cuboid.uniform(cx - s1, 3.0f, cz - s0, cx + s1, height, cz + s0, UV_ENAMEL_WALL));
        list.add(Cuboid.uniform(cx - s0, 3.0f, cz - s1, cx + s0, height, cz + s1, UV_ENAMEL_WALL));
        list.add(Cuboid.uniform(cx - s2, 3.0f, cz - s3, cx + s2, height, cz + s3, UV_ENAMEL_WALL));
        list.add(Cuboid.uniform(cx - s3, 3.0f, cz - s2, cx + s3, height, cz + s2, UV_ENAMEL_WALL));

        // 3. Galvanized Wind Stiffener Rings (encircling the tank at intermediate heights)
        int numRings = Math.max(1, (size.getHeight() - 1) / 2);
        float ringStep = height / (numRings + 1);

        for (int i = 1; i <= numRings; i++) {
            float ry = i * ringStep;
            float wR = r + 0.6f;
            list.add(Cuboid.uniform(cx - wR, ry - 1.0f, cz - wR * 0.72f, cx + wR, ry + 1.0f, cz + wR * 0.72f, UV_WIND_RING));
            list.add(Cuboid.uniform(cx - wR * 0.72f, ry - 1.0f, cz - wR, cx + wR * 0.72f, ry + 1.0f, cz + wR, UV_WIND_RING));
        }

        // 4. Low-Profile Conical Roof (Y: height - 1.0f .. height + 3.0f)
        float rR = r + 0.5f;
        list.add(Cuboid.uniform(cx - rR, height - 1.0f, cz - rR * 0.7f, cx + rR, height + 1.5f, cz + rR * 0.7f, UV_ROOF_CONE));
        list.add(Cuboid.uniform(cx - rR * 0.7f, height - 1.0f, cz - rR, cx + rR * 0.7f, height + 1.5f, cz + rR, UV_ROOF_CONE));
        list.add(Cuboid.uniform(cx - r * 0.5f, height + 1.5f, cz - r * 0.5f, cx + r * 0.5f, height + 3.0f, cz + r * 0.5f, UV_ROOF_CONE));

        // 5. Top Roof Perimeter Safety Handrails & Guardrails (Galvanized Steel, Y: height + 0.5f .. height + 8.5f)
        float grR = r - 0.8f;
        float railH = height + 8.0f;
        float midH = height + 4.2f;
        float kickH = height + 0.8f;

        // Octagonal perimeter top handrail segments (leaving North center open for ladder walkthrough)
        // North segments (left & right of ladder gap)
        list.add(Cuboid.uniform(cx - s1, railH, cz - grR - 0.5f, cx - 4.0f, railH + 1.0f, cz - grR + 0.5f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + 4.0f, railH, cz - grR - 0.5f, cx + s1, railH + 1.0f, cz - grR + 0.5f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - s1, midH, cz - grR - 0.4f, cx - 4.0f, midH + 0.8f, cz - grR + 0.4f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + 4.0f, midH, cz - grR - 0.4f, cx + s1, midH + 0.8f, cz - grR + 0.4f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - s1, kickH, cz - grR - 0.5f, cx - 4.0f, kickH + 1.2f, cz - grR + 0.5f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + 4.0f, kickH, cz - grR - 0.5f, cx + s1, kickH + 1.2f, cz - grR + 0.5f, UV_HANDRAIL_STEEL));

        // South rail
        list.add(Cuboid.uniform(cx - s1, railH, cz + grR - 0.5f, cx + s1, railH + 1.0f, cz + grR + 0.5f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - s1, midH, cz + grR - 0.4f, cx + s1, midH + 0.8f, cz + grR + 0.4f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - s1, kickH, cz + grR - 0.5f, cx + s1, kickH + 1.2f, cz + grR + 0.5f, UV_HANDRAIL_STEEL));

        // West rail
        list.add(Cuboid.uniform(cx - grR - 0.5f, railH, cz - s1, cx - grR + 0.5f, railH + 1.0f, cz + s1, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - grR - 0.4f, midH, cz - s1, cx - grR + 0.4f, midH + 0.8f, cz + s1, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - grR - 0.5f, kickH, cz - s1, cx - grR + 0.5f, kickH + 1.2f, cz + s1, UV_HANDRAIL_STEEL));

        // East rail
        list.add(Cuboid.uniform(cx + grR - 0.5f, railH, cz - s1, cx + grR + 0.5f, railH + 1.0f, cz + s1, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + grR - 0.4f, midH, cz - s1, cx + grR + 0.4f, midH + 0.8f, cz + s1, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + grR - 0.5f, kickH, cz - s1, cx + grR + 0.5f, kickH + 1.2f, cz + s1, UV_HANDRAIL_STEEL));

        // Diagonal corner rails (NW, NE, SW, SE)
        list.add(Cuboid.uniform(cx - s2, railH, cz - s2, cx - s1 + 1.0f, railH + 1.0f, cz - s1 + 1.0f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + s1 - 1.0f, railH, cz - s2, cx + s2, railH + 1.0f, cz - s1 + 1.0f, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx - s2, railH, cz + s1 - 1.0f, cx - s1 + 1.0f, railH + 1.0f, cz + s2, UV_HANDRAIL_STEEL));
        list.add(Cuboid.uniform(cx + s1 - 1.0f, railH, cz + s1 - 1.0f, cx + s2, railH + 1.0f, cz + s2, UV_HANDRAIL_STEEL));

        // Vertical Stanchion Posts (galvanized steel posts at key vertices)
        float[][] stanchions = {
                {cx - 4.2f, cz - grR}, {cx + 4.2f, cz - grR},
                {cx - s1, cz - grR}, {cx + s1, cz - grR},
                {cx - s1, cz + grR}, {cx + s1, cz + grR},
                {cx - grR, cz - s1}, {cx - grR, cz + s1},
                {cx + grR, cz - s1}, {cx + grR, cz + s1},
                {cx - s2 + 0.5f, cz - s2 + 0.5f}, {cx + s2 - 0.5f, cz - s2 + 0.5f},
                {cx - s2 + 0.5f, cz + s2 - 0.5f}, {cx + s2 - 0.5f, cz + s2 - 0.5f}
        };
        for (float[] p : stanchions) {
            list.add(Cuboid.uniform(p[0] - 0.5f, height + 0.5f, p[1] - 0.5f, p[0] + 0.5f, railH + 1.0f, p[1] + 0.5f, UV_HANDRAIL_STEEL));
        }

        // 6. Clean Industrial Access Ladder (Mounted directly to the North face of the tank)
        float wallZ = cz - r; // North wall surface
        float ladderZ = wallZ - 1.2f; // Standoff distance from tank wall
        float ladderW = 6.4f; // Ladder width (from cx - 3.2f to cx + 3.2f)
        float railThick = 0.8f; // Steel stringer rail thickness

        // Left vertical side rail (stringer)
        list.add(Cuboid.uniform(cx - ladderW * 0.5f - railThick, 0.0f, ladderZ - railThick,
                                cx - ladderW * 0.5f, height + 4.5f, ladderZ, UV_LADDER_STEEL));
        // Right vertical side rail (stringer)
        list.add(Cuboid.uniform(cx + ladderW * 0.5f, 0.0f, ladderZ - railThick,
                                cx + ladderW * 0.5f + railThick, height + 4.5f, ladderZ, UV_LADDER_STEEL));

        // Standoff mounting brackets anchoring ladder rails to the tank wall
        for (float by = 4.0f; by <= height; by += 16.0f) {
            // Left bracket
            list.add(Cuboid.uniform(cx - ladderW * 0.5f - railThick, by, wallZ,
                                    cx - ladderW * 0.5f, by + 0.8f, ladderZ - railThick * 0.2f, UV_LADDER_STEEL));
            // Right bracket
            list.add(Cuboid.uniform(cx + ladderW * 0.5f, by, wallZ,
                                    cx + ladderW * 0.5f + railThick, by + 0.8f, ladderZ - railThick * 0.2f, UV_LADDER_STEEL));
        }

        // Horizontal climbing rungs (discrete, clean rungs spaced every 3.5 units from Y: 2.5f up to height)
        for (float ry = 3.0f; ry <= height; ry += 3.5f) {
            list.add(Cuboid.uniform(cx - ladderW * 0.5f, ry, ladderZ - railThick * 0.8f,
                                    cx + ladderW * 0.5f, ry + 0.6f, ladderZ - railThick * 0.2f, UV_LADDER_STEEL));
        }

        // Top Walkthrough Safety Grab Bars (curving inward onto the roof rim)
        list.add(Cuboid.uniform(cx - ladderW * 0.5f - railThick, height + 3.7f, ladderZ - railThick,
                                cx - ladderW * 0.5f, height + 4.5f, wallZ + 1.0f, UV_LADDER_STEEL));
        list.add(Cuboid.uniform(cx + ladderW * 0.5f, height + 3.7f, ladderZ - railThick,
                                cx + ladderW * 0.5f + railThick, height + 4.5f, wallZ + 1.0f, UV_LADDER_STEEL));

        // 7. Circular Bolted Manway Door (at base near South side)
        list.add(Cuboid.uniform(cx - 6.0f, 4.0f, cz + r - 0.5f, cx + 6.0f, 16.0f, cz + r + 2.5f, UV_MANWAY_DOOR));

        // 8. Bottom Center Flanged Drain Nozzle (Y: 0..4)
        list.add(Cuboid.uniform(cx - 5.0f, 0.0f, cz - 5.0f, cx + 5.0f, 4.0f, cz + 5.0f, UV_DRAIN_NOZZLE));

        // 9. Top Center Flanged Fill Nozzle (Y: height + 2.0f .. height + 6.0f)
        list.add(Cuboid.uniform(cx - 5.0f, height + 2.0f, cz - 5.0f, cx + 5.0f, height + 6.0f, cz + 5.0f, UV_FILL_NOZZLE));

        return list;
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            StorageTankSize size,
            int light,
            int overlay
    ) {
        Matrix4f poseMatrix = pose.pose();
        List<Cuboid> cuboids = this.sizeGeometry.get(size);
        if (cuboids == null) {
            cuboids = this.sizeGeometry.get(StorageTankSize.SIZE_3X3X3);
        }

        for (Cuboid c : cuboids) {
            emitCuboid(pose, poseMatrix, consumer, c, light, overlay);
        }
    }

    private static void emitCuboid(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            int light,
            int overlay
    ) {
        float x0 = c.x0() / 16.0f;
        float y0 = c.y0() / 16.0f;
        float z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f;
        float y1 = c.y1() / 16.0f;
        float z1 = c.z1() / 16.0f;

        // NORTH Face (z = z0, normal 0, 0, -1)
        emitQuad(pose, matrix, consumer,
                x1, y1, z0,
                x1, y0, z0,
                x0, y0, z0,
                x0, y1, z0,
                c.north(), 0.0f, 0.0f, -1.0f, light, overlay);

        // SOUTH Face (z = z1, normal 0, 0, +1)
        emitQuad(pose, matrix, consumer,
                x0, y1, z1,
                x0, y0, z1,
                x1, y0, z1,
                x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, light, overlay);

        // WEST Face (x = x0, normal -1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y0, z0,
                x0, y0, z1,
                x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, light, overlay);

        // EAST Face (x = x1, normal +1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x1, y1, z1,
                x1, y0, z1,
                x1, y0, z0,
                x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, light, overlay);

        // UP Face (y = y1, normal 0, +1, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y1, z1,
                x1, y1, z1,
                x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, light, overlay);

        // DOWN Face (y = y0, normal 0, -1, 0)
        emitQuad(pose, matrix, consumer,
                x0, y0, z1,
                x0, y0, z0,
                x1, y0, z0,
                x1, y0, z1,
                c.down(), 0.0f, -1.0f, 0.0f, light, overlay);
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            float xA, float yA, float zA,
            float xB, float yB, float zB,
            float xC, float yC, float zC,
            float xD, float yD, float zD,
            UVRect uv,
            float nx, float ny, float nz,
            int light,
            int overlay
    ) {
        consumer.addVertex(matrix, xA, yA, zA)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xB, yB, zB)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xC, yC, zC)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xD, yD, zD)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
