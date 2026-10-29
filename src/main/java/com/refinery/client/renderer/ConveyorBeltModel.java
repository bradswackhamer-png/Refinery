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
 * High-Fidelity Monolithic 3D Model for the Industrial Belt Conveyor:
 * - Reference: Low-profile extruded anodized aluminum bed frame with longitudinal T-slot channel
 * - Heavy-duty matte black industrial conveyor belt with textured canvas grip ribs
 * - Precision brushed stainless steel rotating end rollers at exposed terminals
 * - Machined aluminum end bearing brackets with tensioner adjustment slots & hex bolts
 * - Seamless automatic snapping: when conveyors are placed in a row, intermediate terminals open up
 *   to form a completely uninterrupted, continuous belt run!
 */
@OnlyIn(Dist.CLIENT)
public class ConveyorBeltModel {
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

    // Atlas UV Regions
    public static final UVRect UV_ALUMINUM_RAIL    = UVRect.of(0, 0, 128, 32);
    public static final UVRect UV_BLACK_BELT        = UVRect.of(0, 32, 128, 64);
    public static final UVRect UV_STEEL_ROLLER      = UVRect.of(0, 96, 64, 32);
    public static final UVRect UV_BEARING_BRACKET   = UVRect.of(64, 96, 64, 32);
    public static final UVRect UV_SLIDER_BED       = UVRect.of(128, 0, 64, 32);
    public static final UVRect UV_FOOT_PAD         = UVRect.of(128, 32, 32, 32);

    private final List<Cuboid> staticRailParts = new ArrayList<>();
    private final List<Cuboid> staticBedParts = new ArrayList<>();
    private final List<Cuboid> staticFootParts = new ArrayList<>();

    // Terminal end caps
    private final List<Cuboid> tailEndCaps = new ArrayList<>();
    private final List<Cuboid> headEndCaps = new ArrayList<>();

    public ConveyorBeltModel() {
        buildStaticGeometry();
    }

    private void buildStaticGeometry() {
        // =====================================================================
        // 1. EXTRUDED ALUMINUM SIDE RAILS (Continuous X: 0..1.6 and 14.4..16)
        // =====================================================================
        // Left Rail: outer face has T-slot channel, top face forms guide rail
        staticRailParts.add(new Cuboid(
                0.0f, 0.5f, 0.0f,
                1.6f, 5.0f, 16.0f,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL
        ));

        // Right Rail: outer face has T-slot channel, top face forms guide rail
        staticRailParts.add(new Cuboid(
                14.4f, 0.5f, 0.0f,
                16.0f, 5.0f, 16.0f,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL,
                UV_ALUMINUM_RAIL, UV_ALUMINUM_RAIL
        ));

        // =====================================================================
        // 2. GALVANIZED STEEL SLIDER DECK / BED
        // =====================================================================
        staticBedParts.add(new Cuboid(
                1.6f, 3.2f, 0.0f,
                14.4f, 3.8f, 16.0f,
                UV_SLIDER_BED, UV_SLIDER_BED,
                UV_SLIDER_BED, UV_SLIDER_BED,
                UV_SLIDER_BED, UV_SLIDER_BED
        ));

        // Center stiffener beam underneath slider bed
        staticBedParts.add(new Cuboid(
                7.0f, 2.0f, 0.0f,
                9.0f, 3.2f, 16.0f,
                UV_SLIDER_BED, UV_SLIDER_BED,
                UV_SLIDER_BED, UV_SLIDER_BED,
                UV_SLIDER_BED, UV_SLIDER_BED
        ));

        // =====================================================================
        // 3. HEAVY CAST STEEL MOUNTING FOOT PADS (4 Corners)
        // =====================================================================
        staticFootParts.add(Cuboid.uniform(0.0f, 0.0f, 1.0f, 2.0f, 0.6f, 3.5f, UV_FOOT_PAD));
        staticFootParts.add(Cuboid.uniform(14.0f, 0.0f, 1.0f, 16.0f, 0.6f, 3.5f, UV_FOOT_PAD));
        staticFootParts.add(Cuboid.uniform(0.0f, 0.0f, 12.5f, 2.0f, 0.6f, 15.0f, UV_FOOT_PAD));
        staticFootParts.add(Cuboid.uniform(14.0f, 0.0f, 12.5f, 16.0f, 0.6f, 15.0f, UV_FOOT_PAD));

        // =====================================================================
        // 4. TERMINAL END BEARING BRACKETS (Rendered only at exposed ends)
        // =====================================================================
        // Tail (Back) brackets (Z: 0.0 .. 3.0)
        tailEndCaps.add(new Cuboid(-0.1f, 0.6f, 0.0f, 1.7f, 4.8f, 3.0f,
                UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET));
        tailEndCaps.add(new Cuboid(14.3f, 0.6f, 0.0f, 16.1f, 4.8f, 3.0f,
                UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET));

        // Head (Front) brackets (Z: 13.0 .. 16.0)
        headEndCaps.add(new Cuboid(-0.1f, 0.6f, 13.0f, 1.7f, 4.8f, 16.0f,
                UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET));
        headEndCaps.add(new Cuboid(14.3f, 0.6f, 13.0f, 16.1f, 4.8f, 16.0f,
                UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET, UV_BEARING_BRACKET));
    }

    public void render(
            Matrix4f matrix,
            VertexConsumer consumer,
            boolean connectedFront,
            boolean connectedBack,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        // 1. Render Continuous Extruded Aluminum Rails & Bed Deck
        for (Cuboid c : staticRailParts) {
            renderCuboid(matrix, consumer, c, light, overlay);
        }
        for (Cuboid c : staticBedParts) {
            renderCuboid(matrix, consumer, c, light, overlay);
        }
        for (Cuboid c : staticFootParts) {
            renderCuboid(matrix, consumer, c, light, overlay);
        }

        // 2. Render Tail Terminal (Back end: Z = 0) if NOT connected behind
        float rollerAngle = isRunning ? (animTime * 0.14f) : 0.0f;
        if (!connectedBack) {
            for (Cuboid c : tailEndCaps) {
                renderCuboid(matrix, consumer, c, light, overlay);
            }
            // Tail Roller cylinder centered at Z = 1.6f, Y = 2.6f, radius = 1.7f
            renderCylindricalRoller(matrix, consumer, 1.6f, 14.4f, 1.6f, 2.6f, 1.7f, rollerAngle, light, overlay);
        }

        // 3. Render Head Terminal (Front end: Z = 16) if NOT connected in front
        if (!connectedFront) {
            for (Cuboid c : headEndCaps) {
                renderCuboid(matrix, consumer, c, light, overlay);
            }
            // Head Roller cylinder centered at Z = 14.4f, Y = 2.6f, radius = 1.7f
            renderCylindricalRoller(matrix, consumer, 1.6f, 14.4f, 14.4f, 2.6f, 1.7f, rollerAngle, light, overlay);
        }

        // 4. Render Animated Top & Bottom Conveyor Belt Surfaces
        renderAnimatedBeltTop(matrix, consumer, connectedFront, connectedBack, isRunning, animTime, light, overlay);
        renderAnimatedBeltBottom(matrix, consumer, connectedFront, connectedBack, isRunning, animTime, light, overlay);
    }

    /**
     * Renders the animated top conveyor belt surface at Y = 4.6px (0.2875m).
     * The texture smoothly scrolls forward from z0 (tail) to z1 (head) in the exact
     * direction of item transit.
     */
    private void renderAnimatedBeltTop(
            Matrix4f m,
            VertexConsumer c,
            boolean connectedFront,
            boolean connectedBack,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        float x0 = 1.6f / 16.0f;
        float x1 = 14.4f / 16.0f;
        float y = 4.6f / 16.0f;

        float z0Px = connectedBack ? 0.0f : 1.6f;
        float z1Px = connectedFront ? 16.0f : 14.4f;

        float z0 = z0Px / 16.0f;
        float z1 = z1Px / 16.0f;

        // UV range for the black belt region in atlas
        float u0 = UV_BLACK_BELT.u0();
        float u1 = UV_BLACK_BELT.u1();
        float vMin = UV_BLACK_BELT.v0();
        float vMax = UV_BLACK_BELT.v1();
        float vHeight = vMax - vMin;

        // 1/2 speed scroll synchronized with item speed (0.05 blocks/tick)
        float progress = isRunning ? ((animTime * 0.0225f) % 1.0f) : 0.0f;
        if (progress < 0.0f) progress += 1.0f;

        // As progress advances, texture moves forward from z0 to z1
        float zSplit = z0 + (z1 - z0) * progress;

        // Segment 1: from z0 to zSplit (new belt texture entering from the tail)
        if (progress > 0.001f) {
            float vStart = vMax - progress * vHeight;
            float vEnd = vMax;
            emitUpQuad(m, c, x0, x1, y, z0, zSplit, u0, u1, vStart, vEnd, light, overlay);
        }

        // Segment 2: from zSplit to z1 (continuing belt texture advancing to head)
        if (progress < 0.999f) {
            float vStart = vMin;
            float vEnd = vMin + (1.0f - progress) * vHeight;
            emitUpQuad(m, c, x0, x1, y, zSplit, z1, u0, u1, vStart, vEnd, light, overlay);
        }
    }

    /**
     * Renders the animated bottom return conveyor belt at Y = 1.0px (0.0625m).
     * The return path scrolls in reverse (from front z1 towards back z0).
     */
    private void renderAnimatedBeltBottom(
            Matrix4f m,
            VertexConsumer c,
            boolean connectedFront,
            boolean connectedBack,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        float x0 = 1.8f / 16.0f;
        float x1 = 14.2f / 16.0f;
        float y = 1.0f / 16.0f;

        float z0 = (connectedBack ? 0.0f : 1.6f) / 16.0f;
        float z1 = (connectedFront ? 16.0f : 14.4f) / 16.0f;

        float u0 = UV_BLACK_BELT.u0();
        float u1 = UV_BLACK_BELT.u1();
        float vMin = UV_BLACK_BELT.v0();
        float vMax = UV_BLACK_BELT.v1();
        float vHeight = vMax - vMin;

        // Reverse scrolling along the bottom return path
        float progress = isRunning ? ((1.0f - (animTime * 0.0225f) % 1.0f) % 1.0f) : 0.0f;
        if (progress < 0.0f) progress += 1.0f;

        float zSplit = z0 + (z1 - z0) * progress;

        if (progress > 0.001f) {
            float vStart = vMax - progress * vHeight;
            float vEnd = vMax;
            emitDownQuad(m, c, x0, x1, y, z0, zSplit, u0, u1, vStart, vEnd, light, overlay);
        }

        if (progress < 0.999f) {
            float vStart = vMin;
            float vEnd = vMin + (1.0f - progress) * vHeight;
            emitDownQuad(m, c, x0, x1, y, zSplit, z1, u0, u1, vStart, vEnd, light, overlay);
        }
    }

    private static void emitDownQuad(
            Matrix4f m,
            VertexConsumer c,
            float x0, float x1, float y,
            float z0, float z1,
            float u0, float u1,
            float v0, float v1,
            int light, int overlay
    ) {
        c.addVertex(m, x0, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
    }

    private static void emitUpQuad(
            Matrix4f m,
            VertexConsumer c,
            float x0, float x1, float y,
            float z0, float z1,
            float u0, float u1,
            float v0, float v1,
            int light, int overlay
    ) {
        c.addVertex(m, x0, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
    }

    /**
     * Renders an 8-sided rotating cylindrical roller across the belt width.
     */
    private void renderCylindricalRoller(
            Matrix4f m,
            VertexConsumer c,
            float x0Px, float x1Px,
            float zCenterPx, float yCenterPx,
            float radiusPx,
            float rotationAngle,
            int light,
            int overlay
    ) {
        float x0 = x0Px / 16.0f;
        float x1 = x1Px / 16.0f;
        float zCenter = zCenterPx / 16.0f;
        float yCenter = yCenterPx / 16.0f;
        float radius = radiusPx / 16.0f;

        int segments = 8;
        float angleStep = (float) (2.0 * Math.PI / segments);

        float u0 = UV_STEEL_ROLLER.u0();
        float u1 = UV_STEEL_ROLLER.u1();
        float v0 = UV_STEEL_ROLLER.v0();
        float v1 = UV_STEEL_ROLLER.v1();
        float vStep = (v1 - v0) / segments;

        for (int i = 0; i < segments; i++) {
            float a0 = i * angleStep + rotationAngle;
            float a1 = (i + 1) * angleStep + rotationAngle;

            float zA = zCenter + radius * (float) Math.sin(a0);
            float yA = yCenter + radius * (float) Math.cos(a0);

            float zB = zCenter + radius * (float) Math.sin(a1);
            float yB = yCenter + radius * (float) Math.cos(a1);

            float nZ = (float) Math.sin((a0 + a1) * 0.5f);
            float nY = (float) Math.cos((a0 + a1) * 0.5f);

            float vf0 = v0 + i * vStep;
            float vf1 = v0 + (i + 1) * vStep;

            c.addVertex(m, x0, yA, zA).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, vf0).setOverlay(overlay).setLight(light).setNormal(0, nY, nZ);
            c.addVertex(m, x0, yB, zB).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, vf1).setOverlay(overlay).setLight(light).setNormal(0, nY, nZ);
            c.addVertex(m, x1, yB, zB).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, vf1).setOverlay(overlay).setLight(light).setNormal(0, nY, nZ);
            c.addVertex(m, x1, yA, zA).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, vf0).setOverlay(overlay).setLight(light).setNormal(0, nY, nZ);
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

        // Down face (y = y0) - normal (0, -1, 0)
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u0, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u0, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u1, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.down.u1, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);

        // Up face (y = y1) - normal (0, 1, 0)
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u0, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u0, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u1, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.up.u1, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // North face (z = z0) - normal (0, 0, -1)
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u0, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u0, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u1, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.north.u1, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);

        // South face (z = z1) - normal (0, 0, 1)
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u0, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u0, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u1, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.south.u1, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);

        // West face (x = x0) - normal (-1, 0, 0)
        c.addVertex(m, x0, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u0, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u0, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u1, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.west.u1, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);

        // East face (x = x1) - normal (1, 0, 0)
        c.addVertex(m, x1, y1, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u0, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z1).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u0, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u1, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z0).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(b.east.u1, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }
}
