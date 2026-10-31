package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.block.SlopedConveyorBeltBlock;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SlopedConveyorBeltModel {
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
            SlopedConveyorBeltBlock.SlopeDirection slope,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        boolean isUp = slope == SlopedConveyorBeltBlock.SlopeDirection.UP;

        // Heights in meters (blocks)
        float yLow = 0.22f;
        float yHigh = 1.18f;

        float yEntry = isUp ? yLow : yHigh;
        float yExit  = isUp ? yHigh : yLow;

        // 1. Inclined Left Side Guide Rail (X: 0.0 .. 0.10)
        emitSlopedSideRail(m, c, 0.0f, 0.10f, yEntry, yExit, light, overlay);

        // 2. Inclined Right Side Guide Rail (X: 0.90 .. 1.0)
        emitSlopedSideRail(m, c, 0.90f, 1.0f, yEntry, yExit, light, overlay);

        // 3. Inclined Slider Bed (X: 0.10 .. 0.90)
        emitSlopedQuad(m, c, 0.10f, 0.90f, yEntry - 0.03f, yExit - 0.03f, 0.0f, 1.0f,
                UV_SLIDER_BED.u0(), UV_SLIDER_BED.u1(), UV_SLIDER_BED.v0(), UV_SLIDER_BED.v1(), light, overlay);

        // 4. Vertical Sturdy Support Legs at high end
        float legZ = isUp ? 0.92f : 0.08f;
        float legHeight = yHigh - 0.05f;
        emitBox(m, c, 0.02f, 0.0f, legZ - 0.05f, 0.09f, legHeight, legZ + 0.05f, UV_ALUMINUM_RAIL, light, overlay);
        emitBox(m, c, 0.91f, 0.0f, legZ - 0.05f, 0.98f, legHeight, legZ + 0.05f, UV_ALUMINUM_RAIL, light, overlay);
        // Cross-brace
        emitBox(m, c, 0.09f, legHeight * 0.5f - 0.02f, legZ - 0.03f, 0.91f, legHeight * 0.5f + 0.02f, legZ + 0.03f, UV_ALUMINUM_RAIL, light, overlay);

        // 5. Rollers at both ends
        float rollAngle = isRunning ? (animTime * 0.25f) : 0.0f;
        emitRoller(m, c, 0.11f, 0.89f, 0.08f, yEntry, 0.04f, rollAngle, light, overlay);
        emitRoller(m, c, 0.11f, 0.89f, 0.92f, yExit, 0.04f, rollAngle, light, overlay);

        // 6. Animated Inclined Belt Top
        renderAnimatedBelt(m, c, yEntry, yExit, isRunning, animTime, light, overlay);
    }

    private void renderAnimatedBelt(
            Matrix4f m,
            VertexConsumer c,
            float yStart,
            float yEnd,
            boolean isRunning,
            float animTime,
            int light,
            int overlay
    ) {
        float x0 = 0.11f;
        float x1 = 0.89f;
        float z0 = 0.05f;
        float z1 = 0.95f;

        float u0 = UV_BLACK_BELT.u0();
        float u1 = UV_BLACK_BELT.u1();
        float vMin = UV_BLACK_BELT.v0();
        float vMax = UV_BLACK_BELT.v1();
        float vHeight = vMax - vMin;

        float progress = isRunning ? ((animTime * 0.0225f) % 1.0f) : 0.0f;
        if (progress < 0.0f) progress += 1.0f;

        float zSplit = z0 + (z1 - z0) * progress;
        float ySplit = yStart + (yEnd - yStart) * progress;

        // Top carrying surface
        if (progress > 0.001f) {
            float vStart = vMax - progress * vHeight;
            float vEnd = vMax;
            emitSlopedQuad(m, c, x0, x1, yStart, ySplit, z0, zSplit, u0, u1, vStart, vEnd, light, overlay);
        }
        if (progress < 0.999f) {
            float vStart = vMin;
            float vEnd = vMin + (1.0f - progress) * vHeight;
            emitSlopedQuad(m, c, x0, x1, ySplit, yEnd, zSplit, z1, u0, u1, vStart, vEnd, light, overlay);
        }

        // Bottom return surface
        float yRetStart = yStart - 0.07f;
        float yRetEnd = yEnd - 0.07f;
        emitSlopedQuad(m, c, x0, x1, yRetEnd, yRetStart, z1, z0, u0, u1, vMin, vMax, light, overlay);
    }

    private static void emitSlopedQuad(
            Matrix4f m, VertexConsumer c,
            float x0, float x1, float y0, float y1, float z0, float z1,
            float u0, float u1, float v0, float v1,
            int light, int overlay
    ) {
        c.addVertex(m, x0, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
    }

    private static void emitSlopedSideRail(
            Matrix4f m, VertexConsumer c,
            float x0, float x1, float y0, float y1,
            int light, int overlay
    ) {
        float h = 0.12f; // Guide rail lip height
        // Top face of rail
        emitSlopedQuad(m, c, x0, x1, y0 + h, y1 + h, 0.0f, 1.0f,
                UV_ALUMINUM_RAIL.u0(), UV_ALUMINUM_RAIL.u1(), UV_ALUMINUM_RAIL.v0(), UV_ALUMINUM_RAIL.v1(), light, overlay);
        // Outer face
        emitSideQuad(m, c, x0, y0 - 0.02f, y0 + h, y1 - 0.02f, y1 + h, 0.0f, 1.0f, UV_ALUMINUM_RAIL, light, overlay);
        // Inner face
        emitSideQuad(m, c, x1, y0 - 0.02f, y0 + h, y1 - 0.02f, y1 + h, 0.0f, 1.0f, UV_ALUMINUM_RAIL, light, overlay);
    }

    private static void emitSideQuad(
            Matrix4f m, VertexConsumer c,
            float x, float y0Bottom, float y0Top, float y1Bottom, float y1Top,
            float z0, float z1,
            UVRect uv, int light, int overlay
    ) {
        c.addVertex(m, x, y0Bottom, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x, y1Bottom, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x, y1Top, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x, y0Top, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }

    private static void emitBox(
            Matrix4f m, VertexConsumer c,
            float x0, float y0, float z0, float x1, float y1, float z1,
            UVRect uv, int light, int overlay
    ) {
        // Front
        c.addVertex(m, x0, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y1, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y1, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);

        // Back
        c.addVertex(m, x1, y0, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);

        // Left
        c.addVertex(m, x0, y0, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);

        // Right
        c.addVertex(m, x1, y0, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z0).setColor(1f, 1f, 1f, 1f).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z1).setColor(1f, 1f, 1f, 1f).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }

    private static void emitRoller(
            Matrix4f m, VertexConsumer c,
            float x0, float x1, float zCenter, float yCenter, float r,
            float rot, int light, int overlay
    ) {
        int segs = 6;
        float step = (float) (2.0 * Math.PI / segs);
        for (int i = 0; i < segs; i++) {
            float a0 = i * step + rot;
            float a1 = (i + 1) * step + rot;
            float zA = zCenter + r * (float) Math.sin(a0);
            float yA = yCenter + r * (float) Math.cos(a0);
            float zB = zCenter + r * (float) Math.sin(a1);
            float yB = yCenter + r * (float) Math.cos(a1);

            c.addVertex(m, x0, yA, zA).setColor(1f, 1f, 1f, 1f).setUv(UV_STEEL_ROLLER.u0(), UV_STEEL_ROLLER.v0()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, x1, yA, zA).setColor(1f, 1f, 1f, 1f).setUv(UV_STEEL_ROLLER.u1(), UV_STEEL_ROLLER.v0()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, x1, yB, zB).setColor(1f, 1f, 1f, 1f).setUv(UV_STEEL_ROLLER.u1(), UV_STEEL_ROLLER.v1()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
            c.addVertex(m, x0, yB, zB).setColor(1f, 1f, 1f, 1f).setUv(UV_STEEL_ROLLER.u0(), UV_STEEL_ROLLER.v1()).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        }
    }
}
