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
public class SmallDieselBoilerModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/small_diesel_boiler.png"
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

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid burnerSightGlassActive;
    private final Cuboid burnerSightGlassIdle;

    public SmallDieselBoilerModel() {
        // Base skid runners
        staticCuboids.add(Cuboid.uniform(-12.0f, 0.0f, 0.0f, -6.0f, 4.0f, 48.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(22.0f,  0.0f, 0.0f, 28.0f, 4.0f, 48.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-6.0f,  0.0f, 2.0f, 22.0f, 4.0f, 6.0f,  UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-6.0f,  0.0f, 22.0f, 22.0f, 4.0f, 26.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-6.0f,  0.0f, 42.0f, 22.0f, 4.0f, 46.0f, UV_SKID_STEEL));

        // Saddle supports
        staticCuboids.add(Cuboid.uniform(-10.0f, 4.0f, 8.0f, 26.0f, 8.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-11.5f, 8.0f, 8.0f, -7.0f, 15.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(23.0f, 8.0f, 8.0f, 27.5f, 15.0f, 14.0f, UV_SKID_STEEL));

        staticCuboids.add(Cuboid.uniform(-10.0f, 4.0f, 34.0f, 26.0f, 8.0f, 40.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-11.5f, 8.0f, 34.0f, -7.0f, 15.0f, 40.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(23.0f, 8.0f, 34.0f, 27.5f, 15.0f, 40.0f, UV_SKID_STEEL));

        // Cylindrical boiler drum
        staticCuboids.add(Cuboid.uniform(-9.0f, 7.0f, 2.0f, 25.0f, 41.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-5.0f, 41.0f, 2.0f, 21.0f, 43.5f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-5.0f, 4.5f, 2.0f, 21.0f, 7.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-11.5f, 13.0f, 2.0f, -9.0f, 35.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(25.0f, 13.0f, 2.0f, 27.5f, 35.0f, 46.0f, UV_SHELL_CLADDING));

        // Drum Heads
        staticCuboids.add(Cuboid.uniform(-10.5f, 6.0f, 0.5f, 26.5f, 42.0f, 2.0f, UV_DRUM_HEAD));
        staticCuboids.add(Cuboid.uniform(-10.5f, 6.0f, 46.0f, 26.5f, 42.0f, 47.5f, UV_DRUM_HEAD));

        // Reinforcement Straps
        staticCuboids.add(Cuboid.uniform(-12.0f, 4.0f, 11.0f, 28.0f, 44.0f, 13.5f, UV_REINFORCE_STRAP));
        staticCuboids.add(Cuboid.uniform(-12.0f, 4.0f, 23.0f, 28.0f, 44.0f, 25.5f, UV_REINFORCE_STRAP));
        staticCuboids.add(Cuboid.uniform(-12.0f, 4.0f, 35.0f, 28.0f, 44.0f, 37.5f, UV_REINFORCE_STRAP));

        // Front Diesel Burner Face & High-Pressure Fuel Injector Hub
        staticCuboids.add(Cuboid.uniform(-4.0f, 14.0f, -0.5f, 20.0f, 38.0f, 0.5f, UV_BURNER_FACE));
        staticCuboids.add(Cuboid.uniform(-2.0f, 10.0f, -1.0f, 18.0f, 14.0f, 0.5f, UV_DIESEL_RAIL)); // Diesel fuel rail

        // Glowing burner sight glass
        burnerSightGlassActive = Cuboid.uniform(4.0f, 22.0f, -0.8f, 12.0f, 30.0f, -0.5f, UV_BURNER_ACTIVE);
        burnerSightGlassIdle   = Cuboid.uniform(4.0f, 22.0f, -0.8f, 12.0f, 30.0f, -0.5f, UV_BURNER_IDLE);

        // Overhead Smokestack
        staticCuboids.add(Cuboid.uniform(3.0f, 43.5f, 36.0f, 13.0f, 64.0f, 44.0f, UV_SMOKESTACK));
        staticCuboids.add(Cuboid.uniform(2.0f, 62.0f, 35.0f, 14.0f, 64.0f, 45.0f, UV_SMOKESTACK));

        // Steam Header & Valves
        staticCuboids.add(Cuboid.uniform(5.0f, 43.5f, 14.0f, 11.0f, 50.0f, 20.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-3.0f, 50.0f, 15.0f, 19.0f, 54.0f, 19.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-8.0f, 51.0f, 14.0f, -3.0f, 57.0f, 20.0f, UV_VALVE_WHEEL));
        staticCuboids.add(Cuboid.uniform(19.0f, 51.0f, 14.0f, 24.0f, 57.0f, 20.0f, UV_VALVE_WHEEL));
        staticCuboids.add(Cuboid.uniform(5.5f, 54.0f, 15.0f, 10.5f, 59.0f, 19.0f, UV_PRESSURE_GAUGE));

        // Side Control Cabinet & Water Sight Column
        staticCuboids.add(Cuboid.uniform(-14.5f, 16.0f, 6.0f, -11.5f, 34.0f, 22.0f, UV_CONTROL_CABINET));
        staticCuboids.add(Cuboid.uniform(27.5f, 16.0f, 8.0f, 30.5f, 36.0f, 16.0f, UV_WATER_COLUMN));
    }

    public void render(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, boolean isBurning) {
        Matrix4f matrix = pose.pose();

        for (Cuboid c : staticCuboids) {
            renderCuboid(matrix, buffer, c, packedLight, packedOverlay);
        }

        if (isBurning) {
            renderCuboid(matrix, buffer, burnerSightGlassActive, 15728880, packedOverlay);
        } else {
            renderCuboid(matrix, buffer, burnerSightGlassIdle, packedLight, packedOverlay);
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
        vertex(matrix, buffer, x1, y0, z1, c.east().u1(), c.east().v1(), light, overlay, 1, 0, 0);

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
