package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.block.RightAngleConveyorBeltBlock;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * Monolithic 3D Model for the 90-Degree Right-Angle Conveyor Belt:
 * - Extruded aluminum outer curved / L-frame side guide rails.
 * - Heavy galvanized steel slider bed deck.
 * - Rotating entry and exit cylindrical rollers.
 * - Segmented smooth curved matte black industrial conveyor belt top with scrolling texture animation.
 */
@OnlyIn(Dist.CLIENT)
public class RightAngleConveyorBeltModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/conveyor_belt.png"
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

    public static final UVRect UV_ALUMINUM_RAIL    = UVRect.of(0, 0, 128, 32);
    public static final UVRect UV_BLACK_BELT        = UVRect.of(0, 32, 128, 64);
    public static final UVRect UV_STEEL_ROLLER      = UVRect.of(0, 96, 64, 32);
    public static final UVRect UV_BEARING_BRACKET   = UVRect.of(64, 96, 64, 32);
    public static final UVRect UV_SLIDER_BED       = UVRect.of(128, 0, 64, 32);
    public static final UVRect UV_FOOT_PAD         = UVRect.of(128, 32, 32, 32);

    public void render(
            Matrix4f m,
            VertexConsumer c,
            RightAngleConveyorBeltBlock.TurnDirection turn,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        boolean isRight = turn == RightAngleConveyorBeltBlock.TurnDirection.RIGHT;

        // Base bed deck: Y = 0.02 to 0.23
        emitBox(m, c, 0.05f, 0.02f, 0.05f, 0.95f, 0.23f, 0.95f, UV_SLIDER_BED, light, overlay);

        // Foot pads at corners
        emitBox(m, c, 0.0f, 0.0f, 0.0f, 0.12f, 0.04f, 0.12f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.88f, 0.0f, 0.0f, 1.0f, 0.04f, 0.12f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.0f, 0.0f, 0.88f, 0.12f, 0.04f, 1.0f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.88f, 0.0f, 0.88f, 1.0f, 0.04f, 1.0f, UV_FOOT_PAD, light, overlay);

        float railHeight = 0.31f;

        if (isRight) {
            // Turning RIGHT: Entering at Z = 0 (moving +Z), turning towards West (-X, exiting at X = 0).
            // Outside corner is East (X = 1.0) and South (Z = 1.0).
            // Outer Rails:
            emitBox(m, c, 0.90f, 0.03f, 0.0f, 1.0f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // East outer rail
            emitBox(m, c, 0.0f, 0.03f, 0.90f, 1.0f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // South outer rail

            // Inside corner is at North-West: corner post/bracket at (0.0 .. 0.10, 0.0 .. 0.10)
            emitBox(m, c, 0.0f, 0.03f, 0.0f, 0.10f, railHeight, 0.10f, UV_BEARING_BRACKET, light, overlay);

            // Rollers
            float rollAngle = isRunning ? (animTime * 0.25f) : 0.0f;
            // Entry roller across Z = 0.06 (X: 0.10 to 0.90)
            emitRollerX(m, c, 0.10f, 0.90f, 0.06f, 0.18f, 0.04f, rollAngle, light, overlay);
            // Exit roller across X = 0.06 (Z: 0.10 to 0.90)
            emitRollerZ(m, c, 0.06f, 0.10f, 0.90f, 0.18f, 0.04f, rollAngle, light, overlay);

            // Animated curved turning belt
            renderCurvedBelt(m, c, true, isRunning, animTime, light, overlay);
        } else {
            // Turning LEFT: Entering at Z = 0 (moving +Z), turning towards East (+X, exiting at X = 1.0).
            // Outside corner is West (X = 0.0) and South (Z = 1.0).
            // Outer Rails:
            emitBox(m, c, 0.0f, 0.03f, 0.0f, 0.10f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // West outer rail
            emitBox(m, c, 0.0f, 0.03f, 0.90f, 1.0f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // South outer rail

            // Inside corner is at North-East: corner post/bracket at (0.90 .. 1.0, 0.0 .. 0.10)
            emitBox(m, c, 0.90f, 0.03f, 0.0f, 1.0f, railHeight, 0.10f, UV_BEARING_BRACKET, light, overlay);

            // Rollers
            float rollAngle = isRunning ? (animTime * 0.25f) : 0.0f;
            // Entry roller across Z = 0.06 (X: 0.10 to 0.90)
            emitRollerX(m, c, 0.10f, 0.90f, 0.06f, 0.18f, 0.04f, rollAngle, light, overlay);
            // Exit roller across X = 0.94 (Z: 0.10 to 0.90)
            emitRollerZ(m, c, 0.94f, 0.10f, 0.90f, 0.18f, 0.04f, rollAngle, light, overlay);

            // Animated curved turning belt
            renderCurvedBelt(m, c, false, isRunning, animTime, light, overlay);
        }
    }

    /**
     * Renders a 90-degree curved belt top surface using 8 radial/arc segments.
     */
    private void renderCurvedBelt(
            Matrix4f m,
            VertexConsumer c,
            boolean isRight,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        float y = 0.2875f;
        int segments = 8;
        float u0 = UV_BLACK_BELT.u0();
        float u1 = UV_BLACK_BELT.u1();
        float vMin = UV_BLACK_BELT.v0();
        float vMax = UV_BLACK_BELT.v1();
        float vHeight = vMax - vMin;

        float scroll = isRunning ? ((animTime * 0.025f) % 1.0f) : 0.0f;
        if (scroll < 0.0f) scroll += 1.0f;

        // For RIGHT turn: center of curvature is at (X=0.10, Z=0.10)
        // Arc goes from angle 0 (entry pointing +Z: points at Z=0.10..0.90) to angle PI/2 (exit pointing -X)
        // For LEFT turn: center of curvature is at (X=0.90, Z=0.10)
        float rInner = 0.08f;
        float rOuter = 0.80f;

        float ox = isRight ? 0.10f : 0.90f;
        float oz = 0.10f;

        for (int i = 0; i < segments; i++) {
            float t0 = (float) i / segments;
            float t1 = (float) (i + 1) / segments;

            // Angle in radians: 0 to PI/2
            float a0 = t0 * ((float) Math.PI * 0.5f);
            float a1 = t1 * ((float) Math.PI * 0.5f);

            // Coordinates for inner and outer radii at angle a0 and a1
            float sin0 = (float) Math.sin(a0);
            float cos0 = (float) Math.cos(a0);
            float sin1 = (float) Math.sin(a1);
            float cos1 = (float) Math.cos(a1);

            float xIn0, zIn0, xOut0, zOut0;
            float xIn1, zIn1, xOut1, zOut1;

            if (isRight) {
                // At a=0: heading +Z. Offset is (r * sin(a), r * cos(a)) with arc swinging toward -X
                // Let at a=0: x = ox + r * 0, z = oz + r * 1 = oz + r
                // at a=PI/2: x = ox - r * 1 = ox - r, z = oz + r * 0 = oz (heading -X)
                // Wait: at a=0, x=ox, z=oz+r. As a increases, x decreases (to ox - r*sin(a)), z decreases (to oz + r*cos(a))
                // That curves smoothly from entry (+Z side of ox) around to exit (-X side)!
                xIn0  = ox - rInner * sin0;
                zIn0  = oz + rInner * cos0;
                xOut0 = ox - rOuter * sin0 + (1.0f - cos0) * 0.10f;
                zOut0 = oz + rOuter * cos0 + (1.0f - sin0) * 0.10f;

                xIn1  = ox - rInner * sin1;
                zIn1  = oz + rInner * cos1;
                xOut1 = ox - rOuter * sin1 + (1.0f - cos1) * 0.10f;
                zOut1 = oz + rOuter * cos1 + (1.0f - sin1) * 0.10f;
            } else {
                // At a=0: heading +Z. As a increases, x increases (toward +X), z decreases
                xIn0  = ox + rInner * sin0;
                zIn0  = oz + rInner * cos0;
                xOut0 = ox + rOuter * sin0 - (1.0f - cos0) * 0.10f;
                zOut0 = oz + rOuter * cos0 + (1.0f - sin0) * 0.10f;

                xIn1  = ox + rInner * sin1;
                zIn1  = oz + rInner * cos1;
                xOut1 = ox + rOuter * sin1 - (1.0f - cos1) * 0.10f;
                zOut1 = oz + rOuter * cos1 + (1.0f - sin1) * 0.10f;
            }

            // Texture V scrolling along the turn progress
            float v0 = vMin + ((t0 + scroll) % 1.0f) * vHeight;
            float v1 = vMin + ((t1 + scroll) % 1.0f) * vHeight;

            c.addVertex(m, xIn0, y, zIn0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, xIn1, y, zIn1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, xOut1, y, zOut1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, xOut0, y, zOut0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        }
    }

    private void emitRollerX(Matrix4f m, VertexConsumer c, float x0, float x1, float z, float y, float r, float angle, int light, int overlay) {
        emitBox(m, c, x0, y - r, z - r, x1, y + r, z + r, UV_STEEL_ROLLER, light, overlay);
    }

    private void emitRollerZ(Matrix4f m, VertexConsumer c, float x, float z0, float z1, float y, float r, float angle, int light, int overlay) {
        emitBox(m, c, x - r, y - r, z0, x + r, y + r, z1, UV_STEEL_ROLLER, light, overlay);
    }

    private void emitBox(Matrix4f m, VertexConsumer c, float x0, float y0, float z0, float x1, float y1, float z1, UVRect uv, int light, int overlay) {
        // UP
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // DOWN
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);

        // NORTH
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);

        // SOUTH
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);

        // WEST
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);

        // EAST
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }
}
