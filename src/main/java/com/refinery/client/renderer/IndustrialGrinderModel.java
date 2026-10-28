package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity 3x3x3 Multiblock Model for the Assembled Industrial Grinder:
 * - 4-post heavy structural steel stand with rubber vibration-damping feet
 * - Central heavy crushing box with side flywheels, belt drive, and eccentric bearing hubs
 * - Top tapered hopper funnel loaded with rock / ore chunks
 * - Left horizontal feed conveyor bringing material into the crusher
 * - Right inclined discharge conveyor with chevron belt carrying crushed product away
 */
@OnlyIn(Dist.CLIENT)
public class IndustrialGrinderModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/industrial_grinder.png"
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

    // Atlas UV Regions (matching GrinderAssetGenerator 256x256 layout)
    private static final UVRect UV_CRUSH_PLATES     = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_SUPPORT_LEGS     = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_FLYWHEEL         = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_BEARING_HUBS     = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_HOPPER_FUNNEL    = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_ROCK_MOUND       = UVRect.of(64, 48, 64, 48);
    private static final UVRect UV_FEED_CONVEYOR    = UVRect.of(128, 48, 64, 32);
    private static final UVRect UV_DISCHARGE_BELT   = UVRect.of(192, 48, 64, 32);
    private static final UVRect UV_DRIVE_MOTOR      = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_JAW_PLATES       = UVRect.of(64, 96, 64, 48);
    private static final UVRect UV_CRUSHED_AGG      = UVRect.of(128, 96, 64, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid flywheel;
    private final Cuboid motorPulley;

    public IndustrialGrinderModel() {
        // =========================================================================
        // 1. 4-POST HEAVY STRUCTURAL STAND & VIBRATION PADS (Y: 0..18)
        // =========================================================================
        // 4 Vertical Steel Leg Columns
        staticCuboids.add(Cuboid.uniform(-13.0f, 0.0f, 2.0f, -7.0f, 18.0f, 8.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(23.0f, 0.0f, 2.0f, 29.0f, 18.0f, 8.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(-13.0f, 0.0f, 40.0f, -7.0f, 18.0f, 46.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(23.0f, 0.0f, 40.0f, 29.0f, 18.0f, 46.0f, UV_SUPPORT_LEGS));

        // 4 Rubber Vibration-Damping Foot Pads
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 1.0f, -6.0f, 3.0f, 9.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(22.0f, 0.0f, 1.0f, 30.0f, 3.0f, 9.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 39.0f, -6.0f, 3.0f, 47.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(22.0f, 0.0f, 39.0f, 30.0f, 3.0f, 47.0f, UV_SUPPORT_LEGS));

        // Perimeter Horizontal Cross-Beams
        staticCuboids.add(Cuboid.uniform(-7.0f, 14.0f, 3.0f, 23.0f, 18.0f, 7.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(-7.0f, 14.0f, 41.0f, 23.0f, 18.0f, 45.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(-12.0f, 14.0f, 8.0f, -8.0f, 18.0f, 40.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(24.0f, 14.0f, 8.0f, 28.0f, 18.0f, 40.0f, UV_SUPPORT_LEGS));

        // Diagonal Frame Braces
        staticCuboids.add(Cuboid.uniform(-11.0f, 4.0f, 8.0f, -9.0f, 14.0f, 20.0f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(25.0f, 4.0f, 8.0f, 27.0f, 14.0f, 20.0f, UV_SUPPORT_LEGS));

        // =========================================================================
        // 2. CENTRAL CRUSHING BOX / JAW CHAMBER (Y: 18..34)
        // =========================================================================
        // Main Crushing Chamber Body
        staticCuboids.add(Cuboid.uniform(-8.0f, 18.0f, 6.0f, 24.0f, 34.0f, 42.0f, UV_CRUSH_PLATES));
        // Bolted Side Flange Enclosures
        staticCuboids.add(Cuboid.uniform(-9.5f, 19.0f, 8.0f, 25.5f, 33.0f, 40.0f, UV_CRUSH_PLATES));
        // Front Inspection Port & Nameplate
        staticCuboids.add(Cuboid.uniform(2.0f, 23.0f, 4.5f, 14.0f, 29.0f, 6.0f, UV_CRUSH_PLATES));

        // Left Eccentric Bearing Hub & Grease Cap
        staticCuboids.add(Cuboid.uniform(-13.0f, 21.0f, 19.0f, -8.0f, 31.0f, 29.0f, UV_BEARING_HUBS));

        // Drive Flywheel on the right (rotates around Z-axis)
        this.flywheel = Cuboid.uniform(25.0f, 18.0f, 16.0f, 29.5f, 34.0f, 32.0f, UV_FLYWHEEL);

        // Electric Drive Motor on Right Side
        staticCuboids.add(Cuboid.uniform(24.0f, 8.0f, 20.0f, 31.0f, 16.0f, 30.0f, UV_DRIVE_MOTOR));
        // Motor Pulley Wheel
        this.motorPulley = Cuboid.uniform(29.0f, 9.5f, 22.5f, 31.5f, 14.5f, 27.5f, UV_FLYWHEEL);
        // Drive Belt Casing between Motor and Flywheel
        staticCuboids.add(Cuboid.uniform(26.5f, 13.0f, 22.0f, 28.5f, 22.0f, 28.0f, UV_FLYWHEEL));

        // =========================================================================
        // 3. TOP TAPERED HOPPER FUNNEL (Y: 34..52)
        // =========================================================================
        // Lower Tapered Funnel Chute
        staticCuboids.add(Cuboid.uniform(-6.0f, 34.0f, 8.0f, 22.0f, 38.0f, 40.0f, UV_HOPPER_FUNNEL));
        // Flared Upper Funnel Box
        staticCuboids.add(Cuboid.uniform(-10.0f, 38.0f, 4.0f, 26.0f, 46.0f, 44.0f, UV_HOPPER_FUNNEL));
        // Bolted Top Reinforcement Rim
        staticCuboids.add(Cuboid.uniform(-11.5f, 46.0f, 2.5f, 27.5f, 48.5f, 45.5f, UV_HOPPER_FUNNEL));

        // Jagged Rock & Ore Mound Piled Inside Hopper
        staticCuboids.add(Cuboid.uniform(-8.5f, 46.0f, 5.0f, 24.5f, 52.0f, 43.0f, UV_ROCK_MOUND));
        staticCuboids.add(Cuboid.uniform(-5.0f, 52.0f, 10.0f, 21.0f, 55.0f, 38.0f, UV_ROCK_MOUND));

        // =========================================================================
        // 4. INPUT FEED CONVEYOR (Left Side, X: -26..-6, Y: 26..35)
        // =========================================================================
        // Horizontal Feed Conveyor Trough Frame
        staticCuboids.add(Cuboid.uniform(-26.0f, 26.0f, 17.0f, -6.0f, 30.0f, 31.0f, UV_FEED_CONVEYOR));
        // Feed Conveyor Rubber Belt Bed
        staticCuboids.add(Cuboid.uniform(-26.0f, 29.5f, 18.0f, -6.0f, 31.0f, 30.0f, UV_FEED_CONVEYOR));
        // Front and Back Steel Guide Skirts
        staticCuboids.add(Cuboid.uniform(-26.0f, 30.5f, 17.0f, -6.0f, 35.0f, 18.5f, UV_FEED_CONVEYOR));
        staticCuboids.add(Cuboid.uniform(-26.0f, 30.5f, 29.5f, -6.0f, 35.0f, 31.0f, UV_FEED_CONVEYOR));
        // Cantilever Support Strut connecting to frame
        staticCuboids.add(Cuboid.uniform(-22.0f, 18.0f, 22.0f, -12.0f, 26.0f, 26.0f, UV_SUPPORT_LEGS));

        // Rock Chunks riding on the Feed Conveyor (Visual Feed In)
        staticCuboids.add(Cuboid.uniform(-24.0f, 31.0f, 19.5f, -10.0f, 34.0f, 28.5f, UV_ROCK_MOUND));

        // =========================================================================
        // 5. OUTPUT DISCHARGE CONVEYOR (Right Side, X: 16..38, Inclined)
        // =========================================================================
        // Under-Chamber Discharge Chute Catch Pan
        staticCuboids.add(Cuboid.uniform(14.0f, 12.0f, 17.0f, 24.0f, 16.0f, 31.0f, UV_DISCHARGE_BELT));
        // Inclined Rising Conveyor Truss (Angles up to discharge crushed product)
        staticCuboids.add(Cuboid.uniform(22.0f, 14.0f, 17.5f, 36.0f, 26.0f, 30.5f, UV_DISCHARGE_BELT));
        // Inclined Chevron Rubber Belt
        staticCuboids.add(Cuboid.uniform(22.0f, 16.5f, 18.5f, 36.0f, 27.5f, 29.5f, UV_DISCHARGE_BELT));
        // Conveyor End Roller Pulley
        staticCuboids.add(Cuboid.uniform(35.0f, 24.5f, 18.0f, 38.5f, 28.5f, 30.0f, UV_SUPPORT_LEGS));
        // Slanted Side Guard Rails
        staticCuboids.add(Cuboid.uniform(22.0f, 16.0f, 17.0f, 37.0f, 29.0f, 18.5f, UV_DISCHARGE_BELT));
        staticCuboids.add(Cuboid.uniform(22.0f, 16.0f, 29.5f, 37.0f, 29.0f, 31.0f, UV_DISCHARGE_BELT));
        // Diagonal Support Strut
        staticCuboids.add(Cuboid.uniform(24.0f, 10.0f, 23.0f, 32.0f, 18.0f, 25.0f, UV_SUPPORT_LEGS));

        // Crushed Aggregate riding up the Discharge Conveyor
        staticCuboids.add(Cuboid.uniform(24.0f, 17.5f, 20.0f, 35.0f, 28.0f, 28.0f, UV_CRUSHED_AGG));
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            boolean isActive,
            int light,
            int overlay
    ) {
        Matrix4f poseMatrix = pose.pose();

        // 1. Emit all static crusher plant cuboids
        for (Cuboid c : this.staticCuboids) {
            emitCuboid(pose, poseMatrix, consumer, c, light, overlay);
        }

        // 2. Emit Flywheel and Motor Pulley
        emitCuboid(pose, poseMatrix, consumer, this.flywheel, light, overlay);
        emitCuboid(pose, poseMatrix, consumer, this.motorPulley, light, overlay);
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

        // NORTH Face
        emitQuad(pose, matrix, consumer,
                x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0,
                c.north(), 0.0f, 0.0f, -1.0f, light, overlay);

        // SOUTH Face
        emitQuad(pose, matrix, consumer,
                x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, light, overlay);

        // WEST Face
        emitQuad(pose, matrix, consumer,
                x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, light, overlay);

        // EAST Face
        emitQuad(pose, matrix, consumer,
                x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, light, overlay);

        // UP Face
        emitQuad(pose, matrix, consumer,
                x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, light, overlay);

        // DOWN Face
        emitQuad(pose, matrix, consumer,
                x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1,
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
