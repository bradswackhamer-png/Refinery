package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Monolithic 3D Model for the 2-Block-Wide Livestock Feeding Trough:
 * - Reference: Formed weathered galvanized sheet steel feed bunk with zinc spangle flakes
 * - Sloped trapezoidal trough profile with rolled top safety rims
 * - 4 steel cross-tie divider struts across the top rim
 * - Formed end bulkhead plates with cleanout slots and skid feet
 * - Dynamic golden straw / hay feed layer that rises continuously based on inventory stock!
 */
@OnlyIn(Dist.CLIENT)
public class FeedingTroughModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/feeding_trough.png"
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

    // Texture UV Rectangles
    public static final UVRect UV_SIDEWALL       = UVRect.of(0, 0, 128, 64);
    public static final UVRect UV_ROLLED_FLANGE  = UVRect.of(0, 64, 128, 32);
    public static final UVRect UV_END_BULKHEAD   = UVRect.of(128, 0, 64, 64);
    public static final UVRect UV_DIVIDER_STRUT  = UVRect.of(128, 64, 64, 32);
    public static final UVRect UV_HAY            = UVRect.of(0, 128, 128, 128);

    private final List<Cuboid> staticFrame = new ArrayList<>();

    public FeedingTroughModel() {
        buildStaticGeometry();
    }

    private void buildStaticGeometry() {
        // Total trough length: 32.0 px (2 full blocks along Z: 0.0 to 32.0)

        // 1. Bottom Galvanized Sheet Floor
        staticFrame.add(new Cuboid(
                3.5f, 0.4f, 0.0f,
                12.5f, 1.0f, 32.0f,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL
        ));

        // 2. Sloped Galvanized Sidewalls
        // Left sloped sidewall (X: 1.5..3.5, Y: 0.8..7.0, Z: 0..32)
        staticFrame.add(new Cuboid(
                1.5f, 0.8f, 0.0f,
                2.5f, 7.0f, 32.0f,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL
        ));

        // Right sloped sidewall (X: 13.5..14.5, Y: 0.8..7.0, Z: 0..32)
        staticFrame.add(new Cuboid(
                13.5f, 0.8f, 0.0f,
                14.5f, 7.0f, 32.0f,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL,
                UV_SIDEWALL, UV_SIDEWALL
        ));

        // 3. Rolled Top Safety Rim Flanges
        // Left rim flange
        staticFrame.add(new Cuboid(
                0.6f, 6.8f, 0.0f,
                1.8f, 7.6f, 32.0f,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE
        ));

        // Right rim flange
        staticFrame.add(new Cuboid(
                14.2f, 6.8f, 0.0f,
                15.4f, 7.6f, 32.0f,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE,
                UV_ROLLED_FLANGE, UV_ROLLED_FLANGE
        ));

        // 4. Formed End Bulkhead Plates (Back Z = -0.4..0.6, Front Z = 31.4..32.4)
        // Back bulkhead
        staticFrame.add(new Cuboid(
                0.6f, 0.0f, -0.4f,
                15.4f, 7.8f, 0.6f,
                UV_END_BULKHEAD, UV_END_BULKHEAD,
                UV_END_BULKHEAD, UV_END_BULKHEAD,
                UV_END_BULKHEAD, UV_END_BULKHEAD
        ));

        // Front bulkhead
        staticFrame.add(new Cuboid(
                0.6f, 0.0f, 31.4f,
                15.4f, 7.8f, 32.4f,
                UV_END_BULKHEAD, UV_END_BULKHEAD,
                UV_END_BULKHEAD, UV_END_BULKHEAD,
                UV_END_BULKHEAD, UV_END_BULKHEAD
        ));

        // 5. Four Steel Cross-Tie Divider Struts across top rim
        float[] strutZ = {4.0f, 12.0f, 20.0f, 28.0f};
        for (float z : strutZ) {
            staticFrame.add(new Cuboid(
                    0.6f, 7.4f, z - 0.4f,
                    15.4f, 8.0f, z + 0.4f,
                    UV_DIVIDER_STRUT, UV_DIVIDER_STRUT,
                    UV_DIVIDER_STRUT, UV_DIVIDER_STRUT,
                    UV_DIVIDER_STRUT, UV_DIVIDER_STRUT
            ));
        }
    }

    public void render(
            Matrix4f matrix,
            VertexConsumer consumer,
            float fillFraction,
            int light,
            int overlay
    ) {
        // 1. Render Galvanized Trough Frame & Struts
        for (Cuboid c : staticFrame) {
            renderCuboid(matrix, consumer, c, light, overlay);
        }

        // 2. Render Dynamic Golden Hay / Feed Layer
        if (fillFraction > 0.005f) {
            renderHayMound(matrix, consumer, fillFraction, light, overlay);
        }
    }

    /**
     * Renders the dynamic golden straw / hay mound inside the trough.
     * Height rises smoothly from Y = 1.0px (almost empty) to Y = 6.4px (completely full).
     */
    private void renderHayMound(
            Matrix4f m,
            VertexConsumer c,
            float fraction,
            int light,
            int overlay
    ) {
        float clamped = Math.clamp(fraction, 0.0f, 1.0f);
        float hayYPx = 1.0f + clamped * 5.4f; // 1.0 to 6.4 px
        float y = hayYPx / 16.0f;

        // Trough trapezoid widens as it rises (from 3.2px to 1.8px inset)
        float insetX = 3.2f - clamped * 1.4f; // 3.2 down to 1.8 px
        float x0 = (insetX) / 16.0f;
        float x1 = (16.0f - insetX) / 16.0f;

        float z0 = 0.8f / 16.0f;
        float z1 = 31.2f / 16.0f;

        float u0 = UV_HAY.u0();
        float u1 = UV_HAY.u1();
        float v0 = UV_HAY.v0();
        float v1 = UV_HAY.v1();

        // Top Hay Mound Surface
        c.addVertex(m, x0, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // Center ridge mound line if more than 35% full
        if (clamped > 0.35f) {
            float ridgeY = (hayYPx + 0.6f) / 16.0f;
            float rX0 = 6.0f / 16.0f;
            float rX1 = 10.0f / 16.0f;
            c.addVertex(m, rX0, ridgeY, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0 + 0.05f, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, rX0, ridgeY, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0 + 0.05f, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, rX1, ridgeY, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1 - 0.05f, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, rX1, ridgeY, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1 - 0.05f, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        }
    }

    private static void renderCuboid(
            Matrix4f m, VertexConsumer c,
            Cuboid b,
            int light, int overlay
    ) {
        float x0 = b.x0 / 16.0f;
        float y0 = b.y0 / 16.0f;
        float z0 = b.z0 / 16.0f;
        float x1 = b.x1 / 16.0f;
        float y1 = b.y1 / 16.0f;
        float z1 = b.z1 / 16.0f;

        // Down
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u0, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u0, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u1, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u1, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);

        // Up
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u0, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u0, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u1, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u1, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // North (Z = z0)
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u0, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u0, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u1, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u1, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);

        // South (Z = z1)
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u0, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u0, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u1, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u1, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);

        // West (X = x0)
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u0, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u0, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u1, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u1, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);

        // East (X = x1)
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u0, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u0, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u1, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u1, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }
}
