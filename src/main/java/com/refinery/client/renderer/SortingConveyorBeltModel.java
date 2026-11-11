package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SortingConveyorBeltModel {
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
            SortingConveyorBeltRenderState state,
            float animTime,
            int light,
            int overlay
    ) {
        boolean isRunning = state.isRunning;

        // 1. Base Bed Deck (Y: 0.02 to 0.23)
        emitBox(m, c, 0.05f, 0.02f, 0.05f, 0.95f, 0.23f, 0.95f, UV_SLIDER_BED, light, overlay);

        // 2. Heavy Cast Foot Pads at 4 corners
        emitBox(m, c, 0.0f, 0.0f, 0.0f, 0.12f, 0.04f, 0.12f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.88f, 0.0f, 0.0f, 1.0f, 0.04f, 0.12f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.0f, 0.0f, 0.88f, 0.12f, 0.04f, 1.0f, UV_FOOT_PAD, light, overlay);
        emitBox(m, c, 0.88f, 0.0f, 0.88f, 1.0f, 0.04f, 1.0f, UV_FOOT_PAD, light, overlay);

        float railHeight = 0.31f;

        // 3. Side Guide Rails (Open at the middle X=0 and X=1 so items can divert sideways)
        // Rear side rails (funnel incoming items)
        emitBox(m, c, 0.0f, 0.03f, 0.0f, 0.10f, railHeight, 0.25f, UV_ALUMINUM_RAIL, light, overlay); // Right rear
        emitBox(m, c, 0.90f, 0.03f, 0.0f, 1.0f, railHeight, 0.25f, UV_ALUMINUM_RAIL, light, overlay); // Left rear
        // Front side rails (guide straight items)
        emitBox(m, c, 0.0f, 0.03f, 0.75f, 0.10f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // Right front
        emitBox(m, c, 0.90f, 0.03f, 0.75f, 1.0f, railHeight, 1.0f, UV_ALUMINUM_RAIL, light, overlay); // Left front

        // 4. Stainless Steel Rollers
        float rollAngle = isRunning ? (animTime * 0.25f) : 0.0f;
        // Rear entry roller across Z = 0.06
        emitRollerX(m, c, 0.10f, 0.90f, 0.06f, 0.18f, 0.04f, rollAngle, light, overlay);
        // Front exit roller across Z = 0.94
        emitRollerX(m, c, 0.10f, 0.90f, 0.94f, 0.18f, 0.04f, rollAngle, light, overlay);
        // Left lateral diversion roller across X = 0.94 (Z: 0.25 to 0.75)
        emitRollerZ(m, c, 0.94f, 0.28f, 0.72f, 0.18f, 0.04f, rollAngle, light, overlay);
        // Right lateral diversion roller across X = 0.06 (Z: 0.25 to 0.75)
        emitRollerZ(m, c, 0.06f, 0.28f, 0.72f, 0.18f, 0.04f, rollAngle, light, overlay);

        // 5. Animated Belt Surfaces
        float scroll = isRunning ? ((animTime * 0.05f) % 1.0f) : 0.0f;
        renderAnimatedBeltSurfaces(m, c, scroll, light, overlay);

        // 6. Overhead Optical Scanner Gantry
        renderScannerGantry(m, c, state, light, overlay);
    }

    private void renderAnimatedBeltSurfaces(Matrix4f m, VertexConsumer c, float scroll, int light, int overlay) {
        float u0 = UV_BLACK_BELT.u0();
        float u1 = UV_BLACK_BELT.u1();
        float vMin = UV_BLACK_BELT.v0();
        float vMax = UV_BLACK_BELT.v1();
        float vHeight = vMax - vMin;

        // Main Straight Center Belt (X: 0.10 to 0.90, Y: 0.235, Z: 0.0 to 1.0)
        float v0 = vMin + (scroll % 1.0f) * vHeight;
        float v1 = vMin + ((scroll + 1.0f) % 1.0f) * vHeight;

        c.addVertex(m, 0.10f, 0.235f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.10f, 0.235f, 1.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.90f, 0.235f, 1.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.90f, 0.235f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // Left Diverter Belt Branch (X: 0.50 to 1.0, Y: 0.236, Z: 0.28 to 0.72)
        c.addVertex(m, 0.50f, 0.236f, 0.28f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.50f, 0.236f, 0.72f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 1.0f,  0.236f, 0.72f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 1.0f,  0.236f, 0.28f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // Right Diverter Belt Branch (X: 0.0 to 0.50, Y: 0.236, Z: 0.28 to 0.72)
        c.addVertex(m, 0.0f,  0.236f, 0.28f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.0f,  0.236f, 0.72f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.50f, 0.236f, 0.72f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, 0.50f, 0.236f, 0.28f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
    }

    private void renderScannerGantry(Matrix4f m, VertexConsumer c, SortingConveyorBeltRenderState state, int light, int overlay) {
        // Vertical support pillars:
        // Left pillar (East, X: 0.88 to 0.98, Y: 0.04 to 0.85, Z: 0.44 to 0.56)
        emitBox(m, c, 0.88f, 0.04f, 0.44f, 0.98f, 0.85f, 0.56f, UV_ALUMINUM_RAIL, light, overlay);
        // Right pillar (West, X: 0.02 to 0.12, Y: 0.04 to 0.85, Z: 0.44 to 0.56)
        emitBox(m, c, 0.02f, 0.04f, 0.44f, 0.12f, 0.85f, 0.56f, UV_ALUMINUM_RAIL, light, overlay);

        // Overhead horizontal crossbar arch (X: 0.02 to 0.98, Y: 0.85 to 0.96, Z: 0.42 to 0.58)
        emitBox(m, c, 0.02f, 0.85f, 0.42f, 0.98f, 0.96f, 0.58f, UV_ALUMINUM_RAIL, light, overlay);

        // Center optical sensor hood housing (X: 0.25 to 0.75, Y: 0.76 to 0.85, Z: 0.44 to 0.56)
        emitBox(m, c, 0.25f, 0.76f, 0.44f, 0.75f, 0.85f, 0.56f, UV_BEARING_BRACKET, light, overlay);

        // Glowing Blue optical scanner bar lens underneath sensor hood (Y: 0.755)
        int fullBright = 0x00F000F0;
        emitSolidColorQuadY(m, c, 0.28f, 0.755f, 0.48f, 0.72f, 0.52f, 0.2f, 0.8f, 1.0f, 1.0f, fullBright, overlay);

        // Indicator LEDs on Crossbar:
        // Left Exit (Blue), Center Forward (Green), Right Exit (Orange)
        boolean isRecent = (state.gameTime - state.lastDivertedTick) < 15;
        Direction facing = state.facing != null ? state.facing : Direction.NORTH;
        Direction lastDir = state.lastDivertedDir;

        boolean pulseLeft = isRecent && lastDir == facing.getCounterClockWise();
        boolean pulseCenter = isRecent && lastDir == facing;
        boolean pulseRight = isRecent && lastDir == facing.getClockWise();

        // Left LED (East, X: 0.72..0.82) - Blue / Cyan
        float lr = pulseLeft ? 0.3f : 0.1f;
        float lg = pulseLeft ? 0.9f : 0.4f;
        float lb = pulseLeft ? 1.0f : 0.8f;
        emitSolidColorQuadZ(m, c, 0.72f, 0.87f, 0.582f, 0.82f, 0.93f, lr, lg, lb, 1.0f, fullBright, overlay);
        emitSolidColorQuadZ(m, c, 0.72f, 0.87f, 0.418f, 0.82f, 0.93f, lr, lg, lb, 1.0f, fullBright, overlay);

        // Center LED (Straight, X: 0.45..0.55) - Green
        float cr = pulseCenter ? 0.2f : 0.05f;
        float cg = pulseCenter ? 1.0f : 0.6f;
        float cb = pulseCenter ? 0.3f : 0.1f;
        emitSolidColorQuadZ(m, c, 0.45f, 0.87f, 0.582f, 0.55f, 0.93f, cr, cg, cb, 1.0f, fullBright, overlay);
        emitSolidColorQuadZ(m, c, 0.45f, 0.87f, 0.418f, 0.55f, 0.93f, cr, cg, cb, 1.0f, fullBright, overlay);

        // Right LED (West, X: 0.18..0.28) - Orange / Amber
        float rr = pulseRight ? 1.0f : 0.8f;
        float rg = pulseRight ? 0.6f : 0.3f;
        float rb = pulseRight ? 0.1f : 0.05f;
        emitSolidColorQuadZ(m, c, 0.18f, 0.87f, 0.582f, 0.28f, 0.93f, rr, rg, rb, 1.0f, fullBright, overlay);
        emitSolidColorQuadZ(m, c, 0.18f, 0.87f, 0.418f, 0.28f, 0.93f, rr, rg, rb, 1.0f, fullBright, overlay);
    }

    private void emitSolidColorQuadY(Matrix4f m, VertexConsumer c, float x0, float y, float z0, float x1, float z1, float r, float g, float b, float a, int light, int overlay) {
        float u0 = UV_BEARING_BRACKET.u0();
        float v0 = UV_BEARING_BRACKET.v0();
        c.addVertex(m, x0, y, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
    }

    private void emitSolidColorQuadZ(Matrix4f m, VertexConsumer c, float x0, float y0, float z, float x1, float y1, float r, float g, float b, float a, int light, int overlay) {
        float u0 = UV_BEARING_BRACKET.u0();
        float v0 = UV_BEARING_BRACKET.v0();
        c.addVertex(m, x0, y1, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y0, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y1, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
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
