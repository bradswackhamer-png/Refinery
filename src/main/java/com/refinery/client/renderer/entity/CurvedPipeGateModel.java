package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * 3D Curved Model for Industrial Pipe Gates.
 * Instead of rendering as a flat floating card, the gate plate dynamically
 * curves around the 12-sided cylindrical profile of the round pipe (R = 0.200),
 * forming an authentic saddle clamp that hugs the pipe contour perfectly.
 */
@OnlyIn(Dist.CLIENT)
public class CurvedPipeGateModel {

    private static final int GRID_N = 6;
    private static final float HALF_W = 0.125f;
    private static final float HALF_H = 0.125f;
    private static final float THICKNESS = 0.024f;
    private static final float PIPE_R = 0.200f;

    public void render(
            Matrix4f matrix,
            VertexConsumer consumer,
            float curveFactorX,
            float curveFactorY,
            boolean active,
            int light,
            int overlay
    ) {
        float[][] zBase = new float[GRID_N + 1][GRID_N + 1];
        float[] xCoords = new float[GRID_N + 1];
        float[] yCoords = new float[GRID_N + 1];
        float[] uCoords = new float[GRID_N + 1];
        float[] vCoords = new float[GRID_N + 1];

        // 1. Compute grid coordinates and cylindrical/dome curve depths
        for (int i = 0; i <= GRID_N; i++) {
            float fracX = (float) i / GRID_N;
            xCoords[i] = -HALF_W + fracX * (2.0f * HALF_W);
            uCoords[i] = fracX;
        }

        for (int j = 0; j <= GRID_N; j++) {
            float fracY = (float) j / GRID_N;
            yCoords[j] = -HALF_H + fracY * (2.0f * HALF_H);
            vCoords[j] = 1.0f - fracY;
        }

        for (int i = 0; i <= GRID_N; i++) {
            float x = xCoords[i];
            for (int j = 0; j <= GRID_N; j++) {
                float y = yCoords[j];
                float radSq = (PIPE_R * PIPE_R) - (curveFactorX * x * x + curveFactorY * y * y);
                if (radSq < 0.002f) radSq = 0.002f;
                // Base depth sits just 0.002 off the pipe cylinder
                zBase[i][j] = -0.202f + (float) Math.sqrt(radSq);
            }
        }

        // 2. Front Curved Face (Displays gate texture curved around pipe)
        for (int i = 0; i < GRID_N; i++) {
            for (int j = 0; j < GRID_N; j++) {
                float x0 = xCoords[i];
                float x1 = xCoords[i + 1];
                float y0 = yCoords[j];
                float y1 = yCoords[j + 1];

                float z00 = zBase[i][j] + THICKNESS;
                float z10 = zBase[i + 1][j] + THICKNESS;
                float z11 = zBase[i + 1][j + 1] + THICKNESS;
                float z01 = zBase[i][j + 1] + THICKNESS;

                float u0 = uCoords[i];
                float u1 = uCoords[i + 1];
                float v0 = vCoords[j];
                float v1 = vCoords[j + 1];

                // Approximate outward normal
                float midX = 0.5f * (x0 + x1);
                float midY = 0.5f * (y0 + y1);
                float nx = curveFactorX * (midX / PIPE_R);
                float ny = curveFactorY * (midY / PIPE_R);
                float nz = (float) Math.sqrt(Math.max(0.1f, 1.0f - (nx * nx + ny * ny)));

                int quadLight = light;
                // If gate is active, the central 2x2 cells emit full brightness
                if (active && i >= 2 && i <= 3 && j >= 2 && j <= 3) {
                    quadLight = 0x00F000F0;
                }

                consumer.addVertex(matrix, x0, y0, z00).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(overlay).setLight(quadLight).setNormal(nx, ny, nz);
                consumer.addVertex(matrix, x1, y0, z10).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(overlay).setLight(quadLight).setNormal(nx, ny, nz);
                consumer.addVertex(matrix, x1, y1, z11).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(overlay).setLight(quadLight).setNormal(nx, ny, nz);
                consumer.addVertex(matrix, x0, y1, z01).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(overlay).setLight(quadLight).setNormal(nx, ny, nz);
            }
        }

        // 3. Back Curved Face (Touches pipe surface)
        for (int i = 0; i < GRID_N; i++) {
            for (int j = 0; j < GRID_N; j++) {
                float x0 = xCoords[i];
                float x1 = xCoords[i + 1];
                float y0 = yCoords[j];
                float y1 = yCoords[j + 1];

                float z00 = zBase[i][j];
                float z10 = zBase[i + 1][j];
                float z11 = zBase[i + 1][j + 1];
                float z01 = zBase[i][j + 1];

                consumer.addVertex(matrix, x0, y0, z00).setColor(0.6f, 0.6f, 0.6f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                consumer.addVertex(matrix, x0, y1, z01).setColor(0.6f, 0.6f, 0.6f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                consumer.addVertex(matrix, x1, y1, z11).setColor(0.6f, 0.6f, 0.6f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
                consumer.addVertex(matrix, x1, y0, z10).setColor(0.6f, 0.6f, 0.6f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
            }
        }

        // 4. Sealed Side Rim Walls
        // Left Edge (i = 0)
        float xLeft = xCoords[0];
        for (int j = 0; j < GRID_N; j++) {
            float y0 = yCoords[j];
            float y1 = yCoords[j + 1];
            float zb0 = zBase[0][j];
            float zb1 = zBase[0][j + 1];
            float zf0 = zb0 + THICKNESS;
            float zf1 = zb1 + THICKNESS;

            consumer.addVertex(matrix, xLeft, y0, zb0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xLeft, y1, zb1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xLeft, y1, zf1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xLeft, y0, zf0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
        }

        // Right Edge (i = GRID_N)
        float xRight = xCoords[GRID_N];
        for (int j = 0; j < GRID_N; j++) {
            float y0 = yCoords[j];
            float y1 = yCoords[j + 1];
            float zb0 = zBase[GRID_N][j];
            float zb1 = zBase[GRID_N][j + 1];
            float zf0 = zb0 + THICKNESS;
            float zf1 = zb1 + THICKNESS;

            consumer.addVertex(matrix, xRight, y0, zf0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xRight, y1, zf1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xRight, y1, zb1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
            consumer.addVertex(matrix, xRight, y0, zb0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
        }

        // Bottom Edge (j = 0)
        float yBot = yCoords[0];
        for (int i = 0; i < GRID_N; i++) {
            float x0 = xCoords[i];
            float x1 = xCoords[i + 1];
            float zb0 = zBase[i][0];
            float zb1 = zBase[i + 1][0];
            float zf0 = zb0 + THICKNESS;
            float zf1 = zb1 + THICKNESS;

            consumer.addVertex(matrix, x0, yBot, zf0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            consumer.addVertex(matrix, x1, yBot, zf1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            consumer.addVertex(matrix, x1, yBot, zb1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
            consumer.addVertex(matrix, x0, yBot, zb0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
        }

        // Top Edge (j = GRID_N)
        float yTop = yCoords[GRID_N];
        for (int i = 0; i < GRID_N; i++) {
            float x0 = xCoords[i];
            float x1 = xCoords[i + 1];
            float zb0 = zBase[i][GRID_N];
            float zb1 = zBase[i + 1][GRID_N];
            float zf0 = zb0 + THICKNESS;
            float zf1 = zb1 + THICKNESS;

            consumer.addVertex(matrix, x0, yTop, zb0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, x1, yTop, zb1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, x1, yTop, zf1).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.2f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
            consumer.addVertex(matrix, x0, yTop, zf0).setColor(0.7f, 0.7f, 0.7f, 1.0f).setUv(0.0f, 0.2f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
        }

        // 5. 4 Corner Hex Mounting Clamp Studs
        renderBolt(matrix, consumer, xCoords[0] + 0.018f, yCoords[0] + 0.018f, zBase[0][0] + THICKNESS, light, overlay);
        renderBolt(matrix, consumer, xCoords[GRID_N] - 0.018f, yCoords[0] + 0.018f, zBase[GRID_N][0] + THICKNESS, light, overlay);
        renderBolt(matrix, consumer, xCoords[0] + 0.018f, yCoords[GRID_N] - 0.018f, zBase[0][GRID_N] + THICKNESS, light, overlay);
        renderBolt(matrix, consumer, xCoords[GRID_N] - 0.018f, yCoords[GRID_N] - 0.018f, zBase[GRID_N][GRID_N] + THICKNESS, light, overlay);
    }

    private void renderBolt(Matrix4f m, VertexConsumer c, float bx, float by, float bz, int light, int overlay) {
        float r = 0.014f;
        float h = 0.008f;
        int sides = 6;

        for (int i = 0; i < sides; i++) {
            double a0 = (2.0 * Math.PI * i) / sides;
            double a1 = (2.0 * Math.PI * (i + 1)) / sides;

            float x0 = bx + r * (float) Math.cos(a0);
            float y0 = by + r * (float) Math.sin(a0);
            float x1 = bx + r * (float) Math.cos(a1);
            float y1 = by + r * (float) Math.sin(a1);

            // Bolt side
            c.addVertex(m, x0, y0, bz).setColor(0.85f, 0.88f, 0.92f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal((float) Math.cos(a0), (float) Math.sin(a0), 0.0f);
            c.addVertex(m, x1, y1, bz).setColor(0.85f, 0.88f, 0.92f, 1.0f).setUv(0.1f, 0.0f).setOverlay(overlay).setLight(light).setNormal((float) Math.cos(a1), (float) Math.sin(a1), 0.0f);
            c.addVertex(m, x1, y1, bz + h).setColor(0.85f, 0.88f, 0.92f, 1.0f).setUv(0.1f, 0.1f).setOverlay(overlay).setLight(light).setNormal((float) Math.cos(a1), (float) Math.sin(a1), 0.0f);
            c.addVertex(m, x0, y0, bz + h).setColor(0.85f, 0.88f, 0.92f, 1.0f).setUv(0.0f, 0.1f).setOverlay(overlay).setLight(light).setNormal((float) Math.cos(a0), (float) Math.sin(a0), 0.0f);

            // Bolt cap
            c.addVertex(m, bx, by, bz + h).setColor(0.95f, 0.97f, 1.0f, 1.0f).setUv(0.05f, 0.05f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, x0, y0, bz + h).setColor(0.95f, 0.97f, 1.0f, 1.0f).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, x1, y1, bz + h).setColor(0.95f, 0.97f, 1.0f, 1.0f).setUv(0.1f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
            c.addVertex(m, bx, by, bz + h).setColor(0.95f, 0.97f, 1.0f, 1.0f).setUv(0.05f, 0.05f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
        }
    }
}
