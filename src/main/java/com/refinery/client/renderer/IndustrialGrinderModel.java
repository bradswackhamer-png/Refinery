package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity 3x3x3 Multiblock Model for the Assembled Industrial Grinder:
 * - 4-post heavy structural steel stand with rubber vibration-damping feet
 * - Central heavy crushing box with side flywheels, belt drive, and eccentric bearing hubs
 * - Open-top tapered hopper bowl with twin counter-rotating 3D spiked grinding rotors & blades inside
 * - Right inclined discharge conveyor with empty cycling chevron belt (crushed product rendered dynamically)
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

    // Atlas UV Regions (matching 256x256 layout)
    private static final UVRect UV_CRUSH_PLATES     = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_SUPPORT_LEGS     = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_FLYWHEEL         = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_BEARING_HUBS     = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_HOPPER_FUNNEL    = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_DISCHARGE_BELT   = UVRect.of(192, 48, 64, 32);
    private static final UVRect UV_DRIVE_MOTOR      = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_JAW_PLATES       = UVRect.of(64, 96, 64, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final List<Cuboid> rotorParts = new ArrayList<>();
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

        // Drive Flywheel on the right
        this.flywheel = Cuboid.uniform(25.0f, 18.0f, 16.0f, 29.5f, 34.0f, 32.0f, UV_FLYWHEEL);

        // Electric Drive Motor on Right Side
        staticCuboids.add(Cuboid.uniform(24.0f, 8.0f, 20.0f, 31.0f, 16.0f, 30.0f, UV_DRIVE_MOTOR));
        // Motor Pulley Wheel
        this.motorPulley = Cuboid.uniform(29.0f, 9.5f, 22.5f, 31.5f, 14.5f, 27.5f, UV_FLYWHEEL);
        // Drive Belt Casing between Motor and Flywheel
        staticCuboids.add(Cuboid.uniform(26.5f, 13.0f, 22.0f, 28.5f, 22.0f, 28.0f, UV_FLYWHEEL));

        // =========================================================================
        // 3. OPEN-TOP TAPERED HOPPER BOWL (Y: 34..48.5)
        // =========================================================================
        // Deep Inner Throat Floor (beneath the twin grinding rotors)
        staticCuboids.add(Cuboid.uniform(-5.0f, 34.0f, 9.0f, 21.0f, 36.5f, 39.0f, UV_JAW_PLATES));

        // Lower Tapered Funnel Chute Walls (Hollow Bowl Lower Tier, Y: 34..38)
        staticCuboids.add(Cuboid.uniform(-6.0f, 34.0f, 8.0f, 22.0f, 38.0f, 11.0f, UV_HOPPER_FUNNEL)); // North
        staticCuboids.add(Cuboid.uniform(-6.0f, 34.0f, 37.0f, 22.0f, 38.0f, 40.0f, UV_HOPPER_FUNNEL)); // South
        staticCuboids.add(Cuboid.uniform(-6.0f, 34.0f, 11.0f, -3.0f, 38.0f, 37.0f, UV_HOPPER_FUNNEL)); // West
        staticCuboids.add(Cuboid.uniform(19.0f, 34.0f, 11.0f, 22.0f, 38.0f, 37.0f, UV_HOPPER_FUNNEL)); // East

        // Flared Upper Funnel Bowl Walls (Hollow Bowl Upper Tier, Y: 38..46)
        staticCuboids.add(Cuboid.uniform(-10.0f, 38.0f, 4.0f, 26.0f, 46.0f, 7.5f, UV_HOPPER_FUNNEL));   // North wall
        staticCuboids.add(Cuboid.uniform(-10.0f, 38.0f, 40.5f, 26.0f, 46.0f, 44.0f, UV_HOPPER_FUNNEL)); // South wall
        staticCuboids.add(Cuboid.uniform(-10.0f, 38.0f, 7.5f, -6.5f, 46.0f, 40.5f, UV_HOPPER_FUNNEL));  // West wall
        staticCuboids.add(Cuboid.uniform(22.5f, 38.0f, 7.5f, 26.0f, 46.0f, 40.5f, UV_HOPPER_FUNNEL));   // East wall

        // Inner Sloped Liner Plates / Breaker Bars inside Hopper Bowl
        staticCuboids.add(Cuboid.uniform(-6.5f, 38.0f, 7.5f, 22.5f, 41.5f, 9.5f, UV_JAW_PLATES));     // North inner liner
        staticCuboids.add(Cuboid.uniform(-6.5f, 38.0f, 38.5f, 22.5f, 41.5f, 40.5f, UV_JAW_PLATES));   // South inner liner
        staticCuboids.add(Cuboid.uniform(-6.5f, 38.0f, 9.5f, -4.5f, 41.5f, 38.5f, UV_JAW_PLATES));    // West inner liner
        staticCuboids.add(Cuboid.uniform(20.5f, 38.0f, 9.5f, 22.5f, 41.5f, 38.5f, UV_JAW_PLATES));    // East inner liner

        // Bolted Top Reinforcement Rim (Open Hollow Perimeter Frame, Y: 46..48.5)
        staticCuboids.add(Cuboid.uniform(-11.5f, 46.0f, 2.5f, 27.5f, 48.5f, 7.5f, UV_HOPPER_FUNNEL));   // North rim
        staticCuboids.add(Cuboid.uniform(-11.5f, 46.0f, 40.5f, 27.5f, 48.5f, 45.5f, UV_HOPPER_FUNNEL)); // South rim
        staticCuboids.add(Cuboid.uniform(-11.5f, 46.0f, 7.5f, -6.5f, 48.5f, 40.5f, UV_HOPPER_FUNNEL));  // West rim
        staticCuboids.add(Cuboid.uniform(22.5f, 46.0f, 7.5f, 27.5f, 48.5f, 40.5f, UV_HOPPER_FUNNEL));   // East rim

        // Rotor End Bearing Mounts inside North & South Bowl Walls
        staticCuboids.add(Cuboid.uniform(-0.5f, 39.0f, 7.2f, 4.5f, 44.0f, 9.8f, UV_BEARING_HUBS));
        staticCuboids.add(Cuboid.uniform(11.5f, 39.0f, 7.2f, 16.5f, 44.0f, 9.8f, UV_BEARING_HUBS));
        staticCuboids.add(Cuboid.uniform(-0.5f, 39.0f, 38.2f, 4.5f, 44.0f, 40.8f, UV_BEARING_HUBS));
        staticCuboids.add(Cuboid.uniform(11.5f, 39.0f, 38.2f, 16.5f, 44.0f, 40.8f, UV_BEARING_HUBS));

        // =========================================================================
        // 4. 3D SPINNING CRUSHER BLADE ROTOR GEOMETRY (Local to Rotor Axis)
        // =========================================================================
        // Central Heavy Hexagonal Steel Axle Shaft (spans Z = 8.5..39.5)
        rotorParts.add(Cuboid.uniform(-2.0f, -2.0f, 8.5f, 2.0f, 2.0f, 39.5f, UV_SUPPORT_LEGS));
        // Heavy Cylindrical Drum Core
        rotorParts.add(Cuboid.uniform(-3.0f, -1.5f, 9.5f, 3.0f, 1.5f, 38.5f, UV_JAW_PLATES));
        rotorParts.add(Cuboid.uniform(-1.5f, -3.0f, 9.5f, 1.5f, 3.0f, 38.5f, UV_JAW_PLATES));

        // 7 Intermeshing Toothed Shredder / Crusher Blade Discs along the shaft
        float[] bladeZPositions = {11.0f, 15.0f, 19.0f, 23.0f, 27.0f, 31.0f, 35.0f};
        for (int i = 0; i < bladeZPositions.length; i++) {
            float bz = bladeZPositions[i];
            // Solid circular collar disc
            rotorParts.add(Cuboid.uniform(-3.8f, -3.8f, bz, 3.8f, 3.8f, bz + 2.0f, UV_FLYWHEEL));
            if (i % 2 == 0) {
                // Vertical & Horizontal Shredder Teeth (extend to 5.6px radius)
                rotorParts.add(Cuboid.uniform(-5.6f, -1.3f, bz + 0.2f, 5.6f, 1.3f, bz + 1.8f, UV_JAW_PLATES));
                rotorParts.add(Cuboid.uniform(-1.3f, -5.6f, bz + 0.2f, 1.3f, 5.6f, bz + 1.8f, UV_JAW_PLATES));
            } else {
                // Staggered Offset Shredder Teeth (creates a helical biting pattern)
                rotorParts.add(Cuboid.uniform(-5.2f, -2.6f, bz + 0.2f, 5.2f, 0.4f, bz + 1.8f, UV_JAW_PLATES));
                rotorParts.add(Cuboid.uniform(-5.2f, -0.4f, bz + 0.2f, 5.2f, 2.6f, bz + 1.8f, UV_JAW_PLATES));
                rotorParts.add(Cuboid.uniform(-2.6f, -5.2f, bz + 0.2f, 0.4f, 5.2f, bz + 1.8f, UV_JAW_PLATES));
                rotorParts.add(Cuboid.uniform(-0.4f, -5.2f, bz + 0.2f, 2.6f, 5.2f, bz + 1.8f, UV_JAW_PLATES));
            }
        }

        // =========================================================================
        // 5. OUTPUT DISCHARGE CONVEYOR (Right Side, X: 14..38.5, Clean Empty Belt)
        // =========================================================================
        // Under-Chamber Discharge Chute Catch Pan
        staticCuboids.add(Cuboid.uniform(14.0f, 12.0f, 17.0f, 24.0f, 16.0f, 31.0f, UV_DISCHARGE_BELT));
        // Inclined Rising Conveyor Truss Frame
        staticCuboids.add(Cuboid.uniform(22.0f, 14.0f, 17.5f, 36.0f, 26.0f, 30.5f, UV_SUPPORT_LEGS));
        // Clean Empty Inclined Chevron Rubber Belt (No pre-generated aggregate mound!)
        staticCuboids.add(Cuboid.uniform(22.0f, 16.5f, 18.5f, 36.0f, 27.5f, 29.5f, UV_DISCHARGE_BELT));
        // Conveyor End Roller Pulley
        staticCuboids.add(Cuboid.uniform(35.0f, 24.5f, 18.0f, 38.5f, 28.5f, 30.0f, UV_SUPPORT_LEGS));
        // Slanted Side Guard Rails
        staticCuboids.add(Cuboid.uniform(22.0f, 16.0f, 17.0f, 37.0f, 29.0f, 18.5f, UV_SUPPORT_LEGS));
        staticCuboids.add(Cuboid.uniform(22.0f, 16.0f, 29.5f, 37.0f, 29.0f, 31.0f, UV_SUPPORT_LEGS));
        // Diagonal Support Strut
        staticCuboids.add(Cuboid.uniform(24.0f, 10.0f, 23.0f, 32.0f, 18.0f, 25.0f, UV_SUPPORT_LEGS));
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            boolean isActive,
            float bladeAngleDegrees,
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

        // 3. Emit Twin Counter-Rotating 3D Spiked Grinder Blade Rotors inside the Top Hopper Bowl
        float rad = (float) Math.toRadians(bladeAngleDegrees);

        // Left Rotor (Centered at X = 2.0, Y = 41.5, turns clockwise / inward toward center)
        Matrix4f leftRotorMatrix = new Matrix4f(poseMatrix)
                .translate(2.0f / 16.0f, 41.5f / 16.0f, 0.0f)
                .rotateZ(-rad);
        for (Cuboid part : this.rotorParts) {
            emitCuboid(pose, leftRotorMatrix, consumer, part, light, overlay);
        }

        // Right Rotor (Centered at X = 14.0, Y = 41.5, counter-rotates +45 deg offset so teeth intermesh)
        Matrix4f rightRotorMatrix = new Matrix4f(poseMatrix)
                .translate(14.0f / 16.0f, 41.5f / 16.0f, 0.0f)
                .rotateZ(rad + (float) Math.toRadians(45.0f));
        for (Cuboid part : this.rotorParts) {
            emitCuboid(pose, rightRotorMatrix, consumer, part, light, overlay);
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
