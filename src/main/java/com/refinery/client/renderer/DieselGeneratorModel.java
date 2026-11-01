package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DieselGeneratorModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/diesel_generator.png"
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

    // UV Atlas Regions
    public static final UVRect UV_FRONT_LEFT   = UVRect.of(0, 0, 64, 48);
    public static final UVRect UV_FRONT_RIGHT  = UVRect.of(64, 0, 64, 48);
    public static final UVRect UV_BACK         = UVRect.of(128, 0, 128, 48);
    public static final UVRect UV_LEFT_INTAKE  = UVRect.of(0, 48, 64, 48);
    public static final UVRect UV_RIGHT_RAD    = UVRect.of(64, 48, 64, 48);
    public static final UVRect UV_ROOF         = UVRect.of(128, 48, 128, 48);
    public static final UVRect UV_SKID_BEAM    = UVRect.of(0, 96, 128, 24);
    public static final UVRect UV_SKID_END     = UVRect.of(128, 96, 64, 24);
    public static final UVRect UV_SUB_TANK     = UVRect.of(192, 96, 64, 24);
    public static final UVRect UV_CONTROL      = UVRect.of(0, 120, 64, 48);
    public static final UVRect UV_MUFFLER      = UVRect.of(64, 120, 48, 48);
    public static final UVRect UV_JUNCTION     = UVRect.of(112, 120, 48, 48);
    public static final UVRect UV_HARDWARE     = UVRect.of(160, 120, 96, 48);

    private final List<Cuboid> staticFrame = new ArrayList<>();

    public DieselGeneratorModel() {
        buildStaticGeometry();
    }

    private void buildStaticGeometry() {
        // =========================================================================
        // 1. LOWER HEAVY C-CHANNEL BLACK STEEL BASE SKID (X: 0..32, Y: 0..4, Z: 0..16)
        // =========================================================================
        // Front C-Channel skid beam (with forklift pockets)
        staticFrame.add(new Cuboid(
                0.0f, 0.0f, 0.0f,
                32.0f, 4.0f, 2.0f,
                UV_SKID_BEAM, UV_SKID_BEAM,
                UV_SKID_END, UV_SKID_END,
                UV_SUB_TANK, UV_SUB_TANK
        ));

        // Back C-Channel skid beam
        staticFrame.add(new Cuboid(
                0.0f, 0.0f, 14.0f,
                32.0f, 4.0f, 16.0f,
                UV_SKID_BEAM, UV_SKID_BEAM,
                UV_SKID_END, UV_SKID_END,
                UV_SUB_TANK, UV_SUB_TANK
        ));

        // Left End Skid Cap
        staticFrame.add(new Cuboid(
                0.0f, 0.0f, 2.0f,
                2.0f, 4.0f, 14.0f,
                UV_SKID_END, UV_SKID_END,
                UV_SKID_END, UV_SKID_END,
                UV_SUB_TANK, UV_SUB_TANK
        ));

        // Right End Skid Cap
        staticFrame.add(new Cuboid(
                30.0f, 0.0f, 2.0f,
                32.0f, 4.0f, 14.0f,
                UV_SKID_END, UV_SKID_END,
                UV_SKID_END, UV_SKID_END,
                UV_SUB_TANK, UV_SUB_TANK
        ));

        // Sub-Base Fuel Tank & Bottom Skid Floor Plate
        staticFrame.add(Cuboid.uniform(
                1.5f, 0.5f, 1.5f,
                30.5f, 4.0f, 14.5f,
                UV_SUB_TANK
        ));

        // =========================================================================
        // 2. OFF-WHITE ACOUSTIC CANOPY ENCLOSURE (Y: 4..17, Z: 1..15)
        // =========================================================================
        // Left Canopy Half (X: 0.8..16.0) - Control & Intake side
        staticFrame.add(new Cuboid(
                0.8f, 4.0f, 1.0f,
                16.0f, 17.0f, 15.0f,
                UV_FRONT_LEFT, UV_BACK,
                UV_LEFT_INTAKE, UV_SUB_TANK,
                UV_ROOF, UV_SUB_TANK
        ));

        // Right Canopy Half (X: 16.0..31.2) - Triton Logo & Radiator side
        staticFrame.add(new Cuboid(
                16.0f, 4.0f, 1.0f,
                31.2f, 17.0f, 15.0f,
                UV_FRONT_RIGHT, UV_BACK,
                UV_SUB_TANK, UV_RIGHT_RAD,
                UV_ROOF, UV_SUB_TANK
        ));

        // =========================================================================
        // 3. OVERHANG DRIP-LIP BEVELED ROOF (Y: 17.0..18.2)
        // =========================================================================
        staticFrame.add(new Cuboid(
                0.4f, 17.0f, 0.6f,
                31.6f, 18.2f, 15.4f,
                UV_ROOF, UV_ROOF,
                UV_ROOF, UV_ROOF,
                UV_ROOF, UV_ROOF
        ));

        // 4 Roof Crane Lifting Eye Brackets
        staticFrame.add(Cuboid.uniform(3.0f, 18.2f, 2.0f, 5.0f, 19.5f, 4.0f, UV_HARDWARE));
        staticFrame.add(Cuboid.uniform(27.0f, 18.2f, 2.0f, 29.0f, 19.5f, 4.0f, UV_HARDWARE));
        staticFrame.add(Cuboid.uniform(3.0f, 18.2f, 12.0f, 5.0f, 19.5f, 14.0f, UV_HARDWARE));
        staticFrame.add(Cuboid.uniform(27.0f, 18.2f, 12.0f, 29.0f, 19.5f, 14.0f, UV_HARDWARE));

        // =========================================================================
        // 4. REAR HIGH-VOLTAGE ELECTRICAL TERMINAL BOX (X: 5..11, Y: 6..13, Z: 15..15.8)
        // =========================================================================
        staticFrame.add(new Cuboid(
                5.0f, 6.0f, 15.0f,
                11.0f, 13.0f, 15.8f,
                UV_JUNCTION, UV_JUNCTION,
                UV_JUNCTION, UV_JUNCTION,
                UV_JUNCTION, UV_JUNCTION
        ));

        // =========================================================================
        // 5. EXHAUST MUFFLER STACK (Top Rear Right at X: 22.5..25.5, Z: 10.5..13.5)
        // =========================================================================
        // Bolted Roof Mounting Collar
        staticFrame.add(Cuboid.uniform(
                22.0f, 18.2f, 10.0f,
                26.0f, 18.8f, 14.0f,
                UV_MUFFLER
        ));

        // Main Cylindrical Exhaust Pipe Stack
        staticFrame.add(Cuboid.uniform(
                22.5f, 18.8f, 10.5f,
                25.5f, 22.5f, 13.5f,
                UV_MUFFLER
        ));
    }

    public void render(
            Matrix4f poseMatrix,
            VertexConsumer consumer,
            boolean active,
            int light,
            int overlay
    ) {
        // 1. Render all static frame cuboids
        for (Cuboid c : this.staticFrame) {
            emitCuboid(poseMatrix, consumer, c, light, overlay);
        }

        // 2. Render Exhaust Rain-Cap Flapper
        // When active: tilted open by ~25 degrees
        // When idle: rested closed atop the exhaust stack
        float flapperAngleY = active ? -0.8f : 0.0f;
        Cuboid flapper = Cuboid.uniform(
                22.2f, 22.5f + flapperAngleY, 10.2f,
                25.8f, 23.0f + flapperAngleY, 13.8f,
                UV_MUFFLER
        );
        emitCuboid(poseMatrix, consumer, flapper, light, overlay);
    }

    private static void emitCuboid(
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

        // NORTH Face (z = z0, normal 0, 0, -1) - Front Face of Generator
        emitQuad(matrix, consumer,
                x1, y1, z0,
                x1, y0, z0,
                x0, y0, z0,
                x0, y1, z0,
                c.north(), 0.0f, 0.0f, -1.0f, light, overlay);

        // SOUTH Face (z = z1, normal 0, 0, +1) - Back Face
        emitQuad(matrix, consumer,
                x0, y1, z1,
                x0, y0, z1,
                x1, y0, z1,
                x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, light, overlay);

        // WEST Face (x = x0, normal -1, 0, 0) - Left Intake End
        emitQuad(matrix, consumer,
                x0, y1, z0,
                x0, y0, z0,
                x0, y0, z1,
                x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, light, overlay);

        // EAST Face (x = x1, normal +1, 0, 0) - Right Radiator End
        emitQuad(matrix, consumer,
                x1, y1, z1,
                x1, y0, z1,
                x1, y0, z0,
                x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, light, overlay);

        // UP Face (y = y1, normal 0, +1, 0) - Roof
        emitQuad(matrix, consumer,
                x0, y1, z0,
                x0, y1, z1,
                x1, y1, z1,
                x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, light, overlay);

        // DOWN Face (y = y0, normal 0, -1, 0) - Skid Base Bottom
        emitQuad(matrix, consumer,
                x0, y0, z1,
                x0, y0, z0,
                x1, y0, z0,
                x1, y0, z1,
                c.down(), 0.0f, -1.0f, 0.0f, light, overlay);
    }

    private static void emitQuad(
            Matrix4f matrix,
            VertexConsumer consumer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            UVRect uv,
            float nx, float ny, float nz,
            int light,
            int overlay
    ) {
        consumer.addVertex(matrix, x0, y0, z0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x3, y3, z3)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}
