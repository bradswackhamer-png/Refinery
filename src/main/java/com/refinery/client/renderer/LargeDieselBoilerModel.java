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

@OnlyIn(Dist.CLIENT)
public class LargeDieselBoilerModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/large_diesel_boiler.png"
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

    private static final UVRect UV_SHELL_CLADDING    = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_DRUM_HEAD        = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_SKID_STEEL       = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_SMOKESTACK       = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_BURNER_FACE      = UVRect.of(0, 48, 64, 64);
    private static final UVRect UV_BURNER_ACTIVE    = UVRect.of(64, 48, 32, 32);
    private static final UVRect UV_BURNER_IDLE      = UVRect.of(96, 48, 32, 32);
    private static final UVRect UV_STEAM_PIPE       = UVRect.of(128, 48, 64, 32);
    private static final UVRect UV_VALVE_WHEEL      = UVRect.of(192, 48, 32, 32);
    private static final UVRect UV_PRESSURE_GAUGE   = UVRect.of(224, 48, 32, 32);
    private static final UVRect UV_CONTROL_CABINET  = UVRect.of(0, 112, 64, 48);
    private static final UVRect UV_WATER_COLUMN     = UVRect.of(64, 112, 48, 48);
    private static final UVRect UV_DIESEL_RAIL      = UVRect.of(112, 112, 48, 48);
    private static final UVRect UV_REINFORCE_STRAP  = UVRect.of(160, 112, 96, 48);
    private static final UVRect UV_WALKWAY          = UVRect.of(0, 160, 64, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid leftBurnerActive;
    private final Cuboid leftBurnerIdle;
    private final Cuboid rightBurnerActive;
    private final Cuboid rightBurnerIdle;

    public LargeDieselBoilerModel() {
        // 4 Skid runners
        staticCuboids.add(Cuboid.uniform(-30.0f, 0.0f, 0.0f, -24.0f, 6.0f, 80.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-8.0f,  0.0f, 0.0f, -2.0f,  6.0f, 80.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(18.0f,  0.0f, 0.0f, 24.0f,  6.0f, 80.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(40.0f,  0.0f, 0.0f, 46.0f,  6.0f, 80.0f, UV_SKID_STEEL));

        // Cross-ties
        staticCuboids.add(Cuboid.uniform(-24.0f, 0.0f, 2.0f,  40.0f, 5.0f, 8.0f,  UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-24.0f, 0.0f, 26.0f, 40.0f, 5.0f, 32.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-24.0f, 0.0f, 50.0f, 40.0f, 5.0f, 56.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-24.0f, 0.0f, 72.0f, 40.0f, 5.0f, 78.0f, UV_SKID_STEEL));

        // 3 Curved saddle supports
        for (float sz : new float[]{10.0f, 36.0f, 62.0f}) {
            staticCuboids.add(Cuboid.uniform(-26.0f, 5.0f, sz, 42.0f, 10.0f, sz + 8.0f, UV_SKID_STEEL));
            staticCuboids.add(Cuboid.uniform(-28.0f, 10.0f, sz, -22.0f, 22.0f, sz + 8.0f, UV_SKID_STEEL));
            staticCuboids.add(Cuboid.uniform(38.0f, 10.0f, sz, 44.0f, 22.0f, sz + 8.0f, UV_SKID_STEEL));
        }

        // Cylindrical Drum
        staticCuboids.add(Cuboid.uniform(-24.0f, 10.0f, 4.0f, 40.0f, 74.0f, 76.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-18.0f, 74.0f, 4.0f, 34.0f, 78.0f, 76.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-18.0f, 6.0f,  4.0f, 34.0f, 10.0f, 76.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-28.0f, 20.0f, 4.0f, -24.0f, 64.0f, 76.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(40.0f,  20.0f, 4.0f, 44.0f,  64.0f, 76.0f, UV_SHELL_CLADDING));

        // Drum Heads
        staticCuboids.add(Cuboid.uniform(-26.0f, 8.0f, 2.0f,  42.0f, 76.0f, 4.0f,  UV_DRUM_HEAD));
        staticCuboids.add(Cuboid.uniform(-26.0f, 8.0f, 76.0f, 42.0f, 76.0f, 79.0f, UV_DRUM_HEAD));

        // 4 Reinforcement Straps
        for (float rz : new float[]{14.0f, 32.0f, 50.0f, 68.0f}) {
            staticCuboids.add(Cuboid.uniform(-28.5f, 5.5f, rz, 44.5f, 78.5f, rz + 3.0f, UV_REINFORCE_STRAP));
        }

        // DUAL DIESEL BURNERS & FUEL INJECTOR MANIFOLDS (Front face Z: 0..2)
        // Left Diesel Burner
        staticCuboids.add(Cuboid.uniform(-18.0f, 24.0f, 0.5f, 6.0f, 48.0f, 2.5f, UV_BURNER_FACE));
        // Right Diesel Burner
        staticCuboids.add(Cuboid.uniform(10.0f,  24.0f, 0.5f, 34.0f, 48.0f, 2.5f, UV_BURNER_FACE));

        // Heavy Transverse Diesel Fuel Injection Rails
        staticCuboids.add(Cuboid.uniform(-20.0f, 18.0f, -0.5f, 4.0f,  22.0f, 1.5f, UV_DIESEL_RAIL));
        staticCuboids.add(Cuboid.uniform(8.0f,   18.0f, -0.5f, 32.0f, 22.0f, 1.5f, UV_DIESEL_RAIL));

        // Sight Glasses
        leftBurnerActive  = Cuboid.uniform(-10.0f, 32.0f, 0.0f, -2.0f, 40.0f, 0.5f, UV_BURNER_ACTIVE);
        leftBurnerIdle    = Cuboid.uniform(-10.0f, 32.0f, 0.0f, -2.0f, 40.0f, 0.5f, UV_BURNER_IDLE);

        rightBurnerActive = Cuboid.uniform(18.0f, 32.0f, 0.0f, 26.0f, 40.0f, 0.5f, UV_BURNER_ACTIVE);
        rightBurnerIdle   = Cuboid.uniform(18.0f, 32.0f, 0.0f, 26.0f, 40.0f, 0.5f, UV_BURNER_IDLE);

        // Center Dial Gauge
        staticCuboids.add(Cuboid.uniform(5.0f, 52.0f, 0.5f, 11.0f, 58.0f, 2.5f, UV_PRESSURE_GAUGE));
        staticCuboids.add(Cuboid.uniform(7.0f, 48.0f, 1.0f, 9.0f,  52.0f, 2.0f, UV_STEAM_PIPE));

        // Overhead Twin Smokestacks
        staticCuboids.add(Cuboid.uniform(-16.0f, 76.0f, 60.0f, 32.0f, 82.0f, 76.0f, UV_SMOKESTACK));
        staticCuboids.add(Cuboid.uniform(-12.0f, 82.0f, 62.0f, -2.0f, 106.0f, 72.0f, UV_SMOKESTACK));
        staticCuboids.add(Cuboid.uniform(-13.5f, 104.0f, 60.5f, -0.5f, 108.0f, 73.5f, UV_SMOKESTACK));

        staticCuboids.add(Cuboid.uniform(18.0f, 82.0f, 62.0f, 28.0f, 106.0f, 72.0f, UV_SMOKESTACK));
        staticCuboids.add(Cuboid.uniform(16.5f, 104.0f, 60.5f, 29.5f, 108.0f, 73.5f, UV_SMOKESTACK));

        // Central Steam Dome & Header
        staticCuboids.add(Cuboid.uniform(2.0f,   78.0f, 26.0f, 14.0f, 86.0f, 38.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-20.0f, 86.0f, 30.0f, 36.0f, 92.0f, 34.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-10.0f, 92.0f, 30.0f, -6.0f, 98.0f, 34.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(22.0f,  92.0f, 30.0f, 26.0f, 98.0f, 34.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-18.0f, 92.0f, 30.5f, -12.0f, 98.0f, 33.5f, UV_VALVE_WHEEL));
        staticCuboids.add(Cuboid.uniform(28.0f,  92.0f, 30.5f, 34.0f, 98.0f, 33.5f, UV_VALVE_WHEEL));
        staticCuboids.add(Cuboid.uniform(5.0f,   92.0f, 30.5f, 11.0f, 98.0f, 33.5f, UV_PRESSURE_GAUGE));

        // Side Control Cabinet, Water Sight Columns & Catwalk
        staticCuboids.add(Cuboid.uniform(-31.0f, 24.0f, 10.0f, -28.0f, 52.0f, 34.0f, UV_CONTROL_CABINET));
        staticCuboids.add(Cuboid.uniform(44.0f,  24.0f, 12.0f, 47.0f,  56.0f, 22.0f, UV_WATER_COLUMN));
        staticCuboids.add(Cuboid.uniform(44.0f,  24.0f, 26.0f, 47.0f,  56.0f, 36.0f, UV_WATER_COLUMN));
        staticCuboids.add(Cuboid.uniform(44.0f,  40.0f, 8.0f,  52.0f,  42.0f, 68.0f, UV_WALKWAY));
        staticCuboids.add(Cuboid.uniform(51.0f,  42.0f, 8.0f,  52.0f,  56.0f, 68.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(46.0f,  0.0f,  66.0f, 50.0f,  40.0f, 68.0f, UV_SKID_STEEL));
    }

    public void render(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, boolean isBurning) {
        Matrix4f matrix = pose.pose();

        for (Cuboid c : staticCuboids) {
            renderCuboid(matrix, buffer, c, packedLight, packedOverlay);
        }

        if (isBurning) {
            renderCuboid(matrix, buffer, leftBurnerActive, 15728880, packedOverlay);
            renderCuboid(matrix, buffer, rightBurnerActive, 15728880, packedOverlay);
        } else {
            renderCuboid(matrix, buffer, leftBurnerIdle, packedLight, packedOverlay);
            renderCuboid(matrix, buffer, rightBurnerIdle, packedLight, packedOverlay);
        }
    }

    private void renderCuboid(Matrix4f matrix, VertexConsumer buffer, Cuboid c, int light, int overlay) {
        float x0 = c.x0() / 16.0f;
        float y0 = c.y0() / 16.0f;
        float z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f;
        float y1 = c.y1() / 16.0f;
        float z1 = c.z1() / 16.0f;

        // North
        vertex(matrix, buffer, x0, y0, z0, c.north().u1(), c.north().v1(), light, overlay, 0, 0, -1);
        vertex(matrix, buffer, x0, y1, z0, c.north().u1(), c.north().v0(), light, overlay, 0, 0, -1);
        vertex(matrix, buffer, x1, y1, z0, c.north().u0(), c.north().v0(), light, overlay, 0, 0, -1);
        vertex(matrix, buffer, x1, y0, z0, c.north().u0(), c.north().v1(), light, overlay, 0, 0, -1);

        // South
        vertex(matrix, buffer, x1, y0, z1, c.south().u1(), c.south().v1(), light, overlay, 0, 0, 1);
        vertex(matrix, buffer, x1, y1, z1, c.south().u1(), c.south().v0(), light, overlay, 0, 0, 1);
        vertex(matrix, buffer, x0, y1, z1, c.south().u0(), c.south().v0(), light, overlay, 0, 0, 1);
        vertex(matrix, buffer, x0, y0, z1, c.south().u0(), c.south().v1(), light, overlay, 0, 0, 1);

        // West
        vertex(matrix, buffer, x0, y0, z1, c.west().u1(), c.west().v1(), light, overlay, -1, 0, 0);
        vertex(matrix, buffer, x0, y1, z1, c.west().u1(), c.west().v0(), light, overlay, -1, 0, 0);
        vertex(matrix, buffer, x0, y1, z0, c.west().u0(), c.west().v0(), light, overlay, -1, 0, 0);
        vertex(matrix, buffer, x0, y0, z0, c.west().u0(), c.west().v1(), light, overlay, -1, 0, 0);

        // East
        vertex(matrix, buffer, x1, y0, z0, c.east().u1(), c.east().v1(), light, overlay, 1, 0, 0);
        vertex(matrix, buffer, x1, y1, z0, c.east().u0(), c.east().v0(), light, overlay, 1, 0, 0);
        vertex(matrix, buffer, x1, y1, z1, c.east().u0(), c.east().v1(), light, overlay, 1, 0, 0);
        vertex(matrix, buffer, x1, y0, z1, c.east().u0(), c.east().v1(), light, overlay, 1, 0, 0);

        // Up
        vertex(matrix, buffer, x0, y1, z0, c.up().u0(), c.up().v0(), light, overlay, 0, 1, 0);
        vertex(matrix, buffer, x0, y1, z1, c.up().u0(), c.up().v1(), light, overlay, 0, 1, 0);
        vertex(matrix, buffer, x1, y1, z1, c.up().u1(), c.up().v1(), light, overlay, 0, 1, 0);
        vertex(matrix, buffer, x1, y1, z0, c.up().u1(), c.up().v0(), light, overlay, 0, 1, 0);

        // Down
        vertex(matrix, buffer, x0, y0, z1, c.down().u0(), c.down().v1(), light, overlay, 0, -1, 0);
        vertex(matrix, buffer, x0, y0, z0, c.down().u0(), c.down().v0(), light, overlay, 0, -1, 0);
        vertex(matrix, buffer, x1, y0, z0, c.down().u1(), c.down().v0(), light, overlay, 0, -1, 0);
        vertex(matrix, buffer, x1, y0, z1, c.down().u1(), c.down().v1(), light, overlay, 0, -1, 0);
    }

    private void vertex(
            Matrix4f matrix, VertexConsumer buffer,
            float x, float y, float z,
            float u, float v,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        buffer.addVertex(matrix, x, y, z)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}
