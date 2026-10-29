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
 * Monolithic 3D Portable Engine-Driven Fluid Pump Model:
 * Reference: Superkiger WP40X Gas/Diesel Engine Water Pump
 * Features:
 * - Full Tubular Black Steel Roll-Cage Frame (runners, 4 uprights, top rails, crossbars, rubber feet)
 * - Industrial Combustion Engine (Left):
 *   - Safety Yellow recoil starter housing & circular fan shroud with pull-starter handle
 *   - White enamel fuel tank with chrome filler cap and Superkiger logo
 *   - Silver cast-aluminum engine block / crankcase with cooling fins & red oil plug
 *   - Heavy black air cleaner box & exhaust heat shield
 * - Centrifugal Impeller Pump Body (Right):
 *   - Cast aluminum volute spiral casing with front circular inspection plate & safety decals
 *   - Top vertical discharge pipe & outlet flange (outputs to Logistics Craft fluid pipes)
 *   - Front suction intake flange & collar (connects to 3D in-world suction hose)
 *   - Bottom skid mounting brackets bolted to tubular frame
 */
@OnlyIn(Dist.CLIENT)
public class FluidPumpModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/fluid_pump.png"
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
    public static final UVRect UV_ROLL_CAGE       = UVRect.of(0, 0, 96, 48);
    public static final UVRect UV_RUBBER_FEET     = UVRect.of(96, 0, 48, 48);
    public static final UVRect UV_CRANKCASE       = UVRect.of(144, 0, 64, 48);
    public static final UVRect UV_OIL_PLUG        = UVRect.of(208, 0, 48, 48);
    public static final UVRect UV_RECOIL_STARTER  = UVRect.of(0, 48, 64, 64);
    public static final UVRect UV_STARTER_HANDLE  = UVRect.of(64, 48, 32, 32);
    public static final UVRect UV_FUEL_TANK       = UVRect.of(96, 48, 64, 64);
    public static final UVRect UV_TANK_CAP        = UVRect.of(160, 48, 32, 32);
    public static final UVRect UV_AIR_FILTER      = UVRect.of(192, 48, 64, 64);
    public static final UVRect UV_PUMP_VOLUTE     = UVRect.of(0, 112, 64, 64);
    public static final UVRect UV_DISCHARGE_PIPE  = UVRect.of(64, 112, 48, 48);
    public static final UVRect UV_SUCTION_FLANGE  = UVRect.of(112, 112, 48, 48);
    public static final UVRect UV_HOSE_RIBBED     = UVRect.of(160, 112, 64, 64);
    public static final UVRect UV_HOSE_STRAINER   = UVRect.of(224, 112, 32, 32);
    public static final UVRect UV_HOSE_COUPLING   = UVRect.of(224, 144, 32, 32);
    public static final UVRect UV_BASE_SKID       = UVRect.of(0, 176, 96, 48);
    public static final UVRect UV_FLYWHEEL        = UVRect.of(96, 176, 48, 48);

    private final List<Cuboid> frameCuboids = new ArrayList<>();
    private final List<Cuboid> engineCuboids = new ArrayList<>();
    private final List<Cuboid> pumpCuboids = new ArrayList<>();

    public FluidPumpModel() {
        // =========================================================================
        // 1. TUBULAR ROLL-CAGE FRAME (All coordinates in 0..16 pixel space / 16.0f)
        // =========================================================================
        // Rubber feet pads (4 corners under frame)
        frameCuboids.add(Cuboid.uniform(1.0f, 0.0f, 1.0f, 3.5f, 0.75f, 3.5f, UV_RUBBER_FEET));
        frameCuboids.add(Cuboid.uniform(12.5f, 0.0f, 1.0f, 15.0f, 0.75f, 3.5f, UV_RUBBER_FEET));
        frameCuboids.add(Cuboid.uniform(1.0f, 0.0f, 12.5f, 3.5f, 0.75f, 15.0f, UV_RUBBER_FEET));
        frameCuboids.add(Cuboid.uniform(12.5f, 0.0f, 12.5f, 15.0f, 0.75f, 15.0f, UV_RUBBER_FEET));

        // Bottom Skid Rails
        frameCuboids.add(Cuboid.uniform(1.25f, 0.75f, 1.0f, 2.75f, 2.0f, 15.0f, UV_ROLL_CAGE)); // Left rail
        frameCuboids.add(Cuboid.uniform(13.25f, 0.75f, 1.0f, 14.75f, 2.0f, 15.0f, UV_ROLL_CAGE)); // Right rail
        frameCuboids.add(Cuboid.uniform(2.75f, 0.75f, 1.0f, 13.25f, 2.0f, 2.5f, UV_ROLL_CAGE)); // Front crossbar
        frameCuboids.add(Cuboid.uniform(2.75f, 0.75f, 13.5f, 13.25f, 2.0f, 15.0f, UV_ROLL_CAGE)); // Back crossbar

        // 4 Vertical Tubular Corner Posts
        frameCuboids.add(Cuboid.uniform(1.25f, 2.0f, 1.0f, 2.75f, 14.0f, 2.5f, UV_ROLL_CAGE)); // Front-Left
        frameCuboids.add(Cuboid.uniform(13.25f, 2.0f, 1.0f, 14.75f, 14.0f, 2.5f, UV_ROLL_CAGE)); // Front-Right
        frameCuboids.add(Cuboid.uniform(1.25f, 2.0f, 13.5f, 2.75f, 14.0f, 15.0f, UV_ROLL_CAGE)); // Back-Left
        frameCuboids.add(Cuboid.uniform(13.25f, 2.0f, 13.5f, 14.75f, 14.0f, 15.0f, UV_ROLL_CAGE)); // Back-Right

        // Top Tubular Guard Rails
        frameCuboids.add(Cuboid.uniform(1.25f, 14.0f, 1.0f, 14.75f, 15.25f, 2.5f, UV_ROLL_CAGE)); // Front top
        frameCuboids.add(Cuboid.uniform(1.25f, 14.0f, 13.5f, 14.75f, 15.25f, 15.0f, UV_ROLL_CAGE)); // Back top
        frameCuboids.add(Cuboid.uniform(1.25f, 14.0f, 2.5f, 2.75f, 15.25f, 13.5f, UV_ROLL_CAGE)); // Left top
        frameCuboids.add(Cuboid.uniform(13.25f, 14.0f, 2.5f, 14.75f, 15.25f, 13.5f, UV_ROLL_CAGE)); // Right top

        // Base Equipment Bed / Skid Mounting Plate
        frameCuboids.add(Cuboid.uniform(2.5f, 1.5f, 2.5f, 13.5f, 2.5f, 13.5f, UV_BASE_SKID));

        // =========================================================================
        // 2. COMBUSTION ENGINE ASSEMBLY (Left Side, x: 2.5 .. 8.5)
        // =========================================================================
        // Engine Base / Silver Crankcase
        engineCuboids.add(Cuboid.uniform(3.0f, 2.5f, 4.0f, 8.0f, 7.5f, 12.0f, UV_CRANKCASE));

        // Yellow Recoil Starter Shroud (Front Face)
        engineCuboids.add(Cuboid.uniform(2.8f, 3.2f, 2.2f, 6.8f, 7.2f, 4.0f, UV_RECOIL_STARTER));
        // Recoil Pull Handle (Black T-Grip on left)
        engineCuboids.add(Cuboid.uniform(1.6f, 4.6f, 2.5f, 2.8f, 5.8f, 3.7f, UV_STARTER_HANDLE));

        // Red Oil Fill Cap / Dipstick
        engineCuboids.add(Cuboid.uniform(6.8f, 3.2f, 2.8f, 7.8f, 4.2f, 3.8f, UV_OIL_PLUG));

        // White Fuel Tank (Top Left, x: 2.8 .. 8.2, y: 8.5 .. 12.8, z: 2.8 .. 11.5)
        engineCuboids.add(Cuboid.uniform(2.8f, 8.5f, 2.8f, 8.2f, 12.8f, 11.5f, UV_FUEL_TANK));
        // Chrome Fuel Cap
        engineCuboids.add(Cuboid.uniform(4.5f, 12.8f, 6.0f, 6.5f, 13.6f, 8.0f, UV_TANK_CAP));

        // Black Air Filter Box & Muffler Heat Shield (Behind tank, z: 10.5 .. 13.5)
        engineCuboids.add(Cuboid.uniform(2.8f, 7.0f, 11.0f, 5.5f, 11.5f, 13.8f, UV_AIR_FILTER)); // Air cleaner
        engineCuboids.add(Cuboid.uniform(5.5f, 6.5f, 11.5f, 8.2f, 10.5f, 13.8f, UV_AIR_FILTER)); // Muffler box

        // Flywheel coupling between engine and pump
        engineCuboids.add(Cuboid.uniform(7.8f, 3.5f, 5.5f, 8.5f, 6.8f, 10.5f, UV_FLYWHEEL));

        // =========================================================================
        // 3. CENTRIFUGAL PUMP HOUSING (Right Side, x: 8.0 .. 13.5)
        // =========================================================================
        // Heavy Cast Aluminum Pump Volute Casing
        pumpCuboids.add(Cuboid.uniform(8.2f, 2.5f, 4.2f, 13.2f, 8.5f, 12.2f, UV_PUMP_VOLUTE));
        // Bolted Front Volute Cover
        pumpCuboids.add(Cuboid.uniform(8.8f, 3.0f, 2.4f, 12.8f, 8.0f, 4.2f, UV_PUMP_VOLUTE));

        // Suction Intake Port / Flange (Front: facing negative Z)
        pumpCuboids.add(Cuboid.uniform(9.4f, 3.6f, 1.2f, 12.2f, 6.4f, 2.4f, UV_SUCTION_FLANGE));

        // Discharge Outlet Neck & Flange (Top: pointing positive Y)
        pumpCuboids.add(Cuboid.uniform(9.4f, 8.5f, 5.8f, 12.2f, 12.5f, 8.6f, UV_DISCHARGE_PIPE)); // Vertical neck
        pumpCuboids.add(Cuboid.uniform(9.0f, 12.5f, 5.4f, 12.6f, 14.2f, 9.0f, UV_DISCHARGE_PIPE)); // Discharge top flange
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            boolean active,
            float animTicks,
            int light,
            int overlay
    ) {
        Matrix4f matrix = pose.pose();

        // 1. Static Frame
        for (Cuboid c : frameCuboids) {
            renderCuboid(pose, matrix, consumer, c, 0.0f, 0.0f, 0.0f, light, overlay);
        }

        // 2. Engine and Pump (apply subtle engine vibration rumble when active)
        float vibX = 0.0f;
        float vibY = 0.0f;
        if (active) {
            vibX = (float) Math.sin(animTicks * 24.0f) * 0.0025f;
            vibY = (float) Math.abs(Math.cos(animTicks * 30.0f)) * 0.0035f;
        }

        for (Cuboid c : engineCuboids) {
            renderCuboid(pose, matrix, consumer, c, vibX, vibY, 0.0f, light, overlay);
        }
        for (Cuboid c : pumpCuboids) {
            renderCuboid(pose, matrix, consumer, c, vibX, vibY, 0.0f, light, overlay);
        }
    }

    private static void renderCuboid(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            float ox, float oy, float oz,
            int light,
            int overlay
    ) {
        float x0 = (c.x0() / 16.0f) + ox;
        float y0 = (c.y0() / 16.0f) + oy;
        float z0 = (c.z0() / 16.0f) + oz;
        float x1 = (c.x1() / 16.0f) + ox;
        float y1 = (c.y1() / 16.0f) + oy;
        float z1 = (c.z1() / 16.0f) + oz;

        // NORTH Face (-Z)
        emitQuad(pose, matrix, consumer,
                x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0,
                c.north(), 0.0f, 0.0f, -1.0f, light, overlay);

        // SOUTH Face (+Z)
        emitQuad(pose, matrix, consumer,
                x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, light, overlay);

        // WEST Face (-X)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, light, overlay);

        // EAST Face (+X)
        emitQuad(pose, matrix, consumer,
                x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, light, overlay);

        // UP Face (+Y)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, light, overlay);

        // DOWN Face (-Y)
        emitQuad(pose, matrix, consumer,
                x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1,
                c.down(), 0.0f, -1.0f, 0.0f, light, overlay);
    }

    public static void emitQuad(
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
