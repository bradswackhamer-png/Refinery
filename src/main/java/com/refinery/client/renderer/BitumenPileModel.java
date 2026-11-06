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
public class BitumenPileModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/bitumen_pile.png"
    );

    private static final float TEX_W = 2048.0f;
    private static final float TEX_H = 2048.0f;

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

    public record Vertex(float x, float y, float z, float u, float v, float nx, float ny, float nz) {}

    public record Quad(Vertex v0, Vertex v1, Vertex v2, Vertex v3) {}

    // Atlas UV Layout (2048x2048 HD)
    public static final UVRect UV_BITUMEN       = UVRect.of(0, 0, 1024, 1024);
    public static final UVRect UV_STEEL_FRAME   = UVRect.of(1024, 0, 1024, 1024);
    public static final UVRect UV_BRACKET       = UVRect.of(0, 1024, 512, 512);
    public static final UVRect UV_FLOOR_BITUMEN = UVRect.of(512, 1024, 512, 512);

    private final List<Quad> bunkerFrame = new ArrayList<>();
    // 9 stages (0 to 8) of continuous organic heightfield meshes across 3x3 footprint
    @SuppressWarnings("unchecked")
    private final List<Quad>[] stageMeshes = new List[9];

    public BitumenPileModel() {
        buildBunkerFrame();
        buildHeapMeshes();
    }

    private void buildBunkerFrame() {
        // Steel containment perimeter walls (3 blocks = 48.0 wide x 48.0 deep)
        addCuboid(bunkerFrame, 1.0f, 0.0f, 1.0f, 47.0f, 3.5f, 2.8f, UV_STEEL_FRAME);   // North wall
        addCuboid(bunkerFrame, 1.0f, 0.0f, 45.2f, 47.0f, 3.5f, 47.0f, UV_STEEL_FRAME); // South wall
        addCuboid(bunkerFrame, 1.0f, 0.0f, 2.8f, 2.8f, 3.5f, 45.2f, UV_STEEL_FRAME);   // West wall
        addCuboid(bunkerFrame, 45.2f, 0.0f, 2.8f, 47.0f, 3.5f, 45.2f, UV_STEEL_FRAME); // East wall

        // 4 Heavy reinforced corner steel posts with cast brackets
        addCuboid(bunkerFrame, 0.0f, 0.0f, 0.0f, 3.5f, 4.8f, 3.5f, UV_BRACKET);     // NW
        addCuboid(bunkerFrame, 44.5f, 0.0f, 0.0f, 48.0f, 4.8f, 3.5f, UV_BRACKET);   // NE
        addCuboid(bunkerFrame, 0.0f, 0.0f, 44.5f, 3.5f, 4.8f, 48.0f, UV_BRACKET);   // SW
        addCuboid(bunkerFrame, 44.5f, 0.0f, 44.5f, 48.0f, 4.8f, 48.0f, UV_BRACKET); // SE

        // Intermediate modular steel reinforcement uprights at 16px and 32px joints
        // North wall uprights
        addCuboid(bunkerFrame, 14.8f, 0.0f, 0.6f, 17.2f, 4.2f, 3.2f, UV_BRACKET);
        addCuboid(bunkerFrame, 30.8f, 0.0f, 0.6f, 33.2f, 4.2f, 3.2f, UV_BRACKET);

        // South wall uprights
        addCuboid(bunkerFrame, 14.8f, 0.0f, 44.8f, 17.2f, 4.2f, 47.4f, UV_BRACKET);
        addCuboid(bunkerFrame, 30.8f, 0.0f, 44.8f, 33.2f, 4.2f, 47.4f, UV_BRACKET);

        // West wall uprights
        addCuboid(bunkerFrame, 0.6f, 0.0f, 14.8f, 3.2f, 4.2f, 17.2f, UV_BRACKET);
        addCuboid(bunkerFrame, 0.6f, 0.0f, 30.8f, 3.2f, 4.2f, 33.2f, UV_BRACKET);

        // East wall uprights
        addCuboid(bunkerFrame, 44.8f, 0.0f, 14.8f, 47.4f, 4.2f, 17.2f, UV_BRACKET);
        addCuboid(bunkerFrame, 44.8f, 0.0f, 30.8f, 47.4f, 4.2f, 33.2f, UV_BRACKET);
    }

    /**
     * Builds organic continuous loose bitumen material heap meshes across the 3x3 footprint.
     * Uses a 24x24 quad heightfield grid with smooth normal vectors for photorealistic lighting.
     */
    private void buildHeapMeshes() {
        int N = 24;
        float xMin = 2.6f;
        float xMax = 45.4f;
        float zMin = 2.6f;
        float zMax = 45.4f;
        float dx = (xMax - xMin) / N;
        float dz = (zMax - zMin) / N;

        // Stage parameter profiles (Peak height, Base radius, Base floor height, Exponent)
        float[] peakHeights = { 0.8f,  5.5f,  9.5f, 13.5f, 17.5f, 21.5f, 25.5f, 29.0f, 31.8f };
        float[] baseRadii   = { 1.0f, 12.0f, 16.0f, 19.5f, 21.4f, 21.4f, 21.4f, 21.4f, 21.4f };
        float[] baseFloors  = { 0.8f,  0.8f,  0.8f,  1.2f,  2.2f,  3.0f,  3.3f,  3.5f,  3.6f };
        float[] exponents   = { 1.0f,  1.35f, 1.35f, 1.40f, 1.45f, 1.50f, 1.55f, 1.55f, 1.60f };

        for (int stage = 0; stage <= 8; stage++) {
            List<Quad> quads = new ArrayList<>();
            float peak = peakHeights[stage];
            float rBase = baseRadii[stage];
            float baseFl = baseFloors[stage];
            float exp = exponents[stage];

            // 1. Compute vertex height grid ((N+1) x (N+1))
            float[][] heights = new float[N + 1][N + 1];
            for (int i = 0; i <= N; i++) {
                float x = xMin + i * dx;
                for (int j = 0; j <= N; j++) {
                    float z = zMin + j * dz;
                    heights[i][j] = computeHeapHeight(x, z, stage, peak, rBase, baseFl, exp);
                }
            }

            // 2. Compute smooth normal vectors
            float[][][] normals = new float[N + 1][N + 1][3];
            for (int i = 0; i <= N; i++) {
                for (int j = 0; j <= N; j++) {
                    float hL = heights[Math.max(0, i - 1)][j];
                    float hR = heights[Math.min(N, i + 1)][j];
                    float hD = heights[i][Math.max(0, j - 1)];
                    float hU = heights[i][Math.min(N, j + 1)];

                    float stepX = (i == 0 || i == N) ? dx : 2.0f * dx;
                    float stepZ = (j == 0 || j == N) ? dz : 2.0f * dz;

                    float dy_dx = (hR - hL) / stepX;
                    float dy_dz = (hU - hD) / stepZ;

                    float nx = -dy_dx;
                    float ny = 1.0f;
                    float nz = -dy_dz;
                    float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                    if (len < 1e-4f) len = 1.0f;

                    normals[i][j][0] = nx / len;
                    normals[i][j][1] = ny / len;
                    normals[i][j][2] = nz / len;
                }
            }

            // 3. Generate smooth surface quads with seamless tiled UVs
            float uvTiles = 3.5f; // Tiles 3.5 times across the 3x3 heap for crisp aggregate texture
            for (int i = 0; i < N; i++) {
                float x0 = xMin + i * dx;
                float x1 = xMin + (i + 1) * dx;
                float tx0 = (float) i / N;
                float tx1 = (float) (i + 1) / N;

                for (int j = 0; j < N; j++) {
                    float z0 = zMin + j * dz;
                    float z1 = zMin + (j + 1) * dz;
                    float tz0 = (float) j / N;
                    float tz1 = (float) (j + 1) / N;

                    float uA = UV_BITUMEN.u0() + (UV_BITUMEN.u1() - UV_BITUMEN.u0()) * ((tx0 * uvTiles) % 1.0f);
                    float uB = UV_BITUMEN.u0() + (UV_BITUMEN.u1() - UV_BITUMEN.u0()) * ((tx1 * uvTiles) % 1.0f);
                    float vA = UV_BITUMEN.v0() + (UV_BITUMEN.v1() - UV_BITUMEN.v0()) * ((tz0 * uvTiles) % 1.0f);
                    float vB = UV_BITUMEN.v0() + (UV_BITUMEN.v1() - UV_BITUMEN.v0()) * ((tz1 * uvTiles) % 1.0f);

                    Vertex v0 = new Vertex(x0, heights[i][j + 1], z1, uA, vB, normals[i][j + 1][0], normals[i][j + 1][1], normals[i][j + 1][2]);
                    Vertex v1 = new Vertex(x1, heights[i + 1][j + 1], z1, uB, vB, normals[i + 1][j + 1][0], normals[i + 1][j + 1][1], normals[i + 1][j + 1][2]);
                    Vertex v2 = new Vertex(x1, heights[i + 1][j], z0, uB, vA, normals[i + 1][j][0], normals[i + 1][j][1], normals[i + 1][j][2]);
                    Vertex v3 = new Vertex(x0, heights[i][j], z0, uA, vA, normals[i][j][0], normals[i][j][1], normals[i][j][2]);

                    quads.add(new Quad(v0, v1, v2, v3));
                }
            }

            stageMeshes[stage] = quads;
        }
    }

    private static float computeHeapHeight(
            float x, float z,
            int stage,
            float peak, float rBase, float baseFl, float exp
    ) {
        if (stage == 0) {
            // Flat dusting across bunker floor with micro variations
            return baseFl + (float) (Math.sin(x * 0.7) * Math.cos(z * 0.7) * 0.06);
        }

        float cx = 24.0f;
        float cz = 24.0f;
        float rx = x - cx;
        float rz = z - cz;

        float dist = (float) Math.sqrt(rx * rx + rz * rz);
        float superDist = (float) Math.pow(Math.pow(rx, 4) + Math.pow(rz, 4), 0.25);
        float rEff = 0.65f * dist + 0.35f * superDist;

        if (rEff >= rBase) {
            return baseFl;
        }

        float u = rEff / rBase; // 0 (peak) to 1 (base)
        float cosFactor = (float) Math.cos((Math.PI / 2.0) * u);
        float profile = (float) Math.pow(Math.max(0.0f, cosFactor), exp);

        // Subtle granular surface micro-ripples
        float ripple = (float) (Math.sin(x * 1.1) * Math.cos(z * 1.1) * 0.12 * Math.sin(Math.PI * u));

        return baseFl + (peak - baseFl) * profile + ripple;
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int visibleStage,
            int light,
            int overlay
    ) {
        Matrix4f matrix = pose.pose();

        // 1. Render steel retaining frame and brackets
        for (Quad q : this.bunkerFrame) {
            emitQuad(pose, matrix, consumer, q, light, overlay);
        }

        // 2. Render organic bitumen material heap for current stage
        int stage = Math.min(8, Math.max(0, visibleStage));
        List<Quad> heapQuads = this.stageMeshes[stage];
        if (heapQuads != null) {
            for (Quad q : heapQuads) {
                emitQuad(pose, matrix, consumer, q, light, overlay);
            }
        }
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Quad q,
            int light,
            int overlay
    ) {
        emitVertex(pose, matrix, consumer, q.v0(), light, overlay);
        emitVertex(pose, matrix, consumer, q.v1(), light, overlay);
        emitVertex(pose, matrix, consumer, q.v2(), light, overlay);
        emitVertex(pose, matrix, consumer, q.v3(), light, overlay);
    }

    private static void emitVertex(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Vertex v,
            int light,
            int overlay
    ) {
        // Divide by 16.0 to convert pixel coordinates into Minecraft block units
        consumer.addVertex(matrix, v.x() / 16.0f, v.y() / 16.0f, v.z() / 16.0f)
                .setColor(255, 255, 255, 255)
                .setUv(v.u(), v.v())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, v.nx(), v.ny(), v.nz());
    }

    private static void addCuboid(
            List<Quad> quads,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            UVRect uv
    ) {
        // North face (Z-)
        quads.add(new Quad(
                new Vertex(x1, y0, z0, uv.u0(), uv.v1(), 0, 0, -1),
                new Vertex(x0, y0, z0, uv.u1(), uv.v1(), 0, 0, -1),
                new Vertex(x0, y1, z0, uv.u1(), uv.v0(), 0, 0, -1),
                new Vertex(x1, y1, z0, uv.u0(), uv.v0(), 0, 0, -1)
        ));
        // South face (Z+)
        quads.add(new Quad(
                new Vertex(x0, y0, z1, uv.u0(), uv.v1(), 0, 0, 1),
                new Vertex(x1, y0, z1, uv.u1(), uv.v1(), 0, 0, 1),
                new Vertex(x1, y1, z1, uv.u1(), uv.v0(), 0, 0, 1),
                new Vertex(x0, y1, z1, uv.u0(), uv.v0(), 0, 0, 1)
        ));
        // West face (X-)
        quads.add(new Quad(
                new Vertex(x0, y0, z0, uv.u0(), uv.v1(), -1, 0, 0),
                new Vertex(x0, y0, z1, uv.u1(), uv.v1(), -1, 0, 0),
                new Vertex(x0, y1, z1, uv.u1(), uv.v0(), -1, 0, 0),
                new Vertex(x0, y1, z0, uv.u0(), uv.v0(), -1, 0, 0)
        ));
        // East face (X+)
        quads.add(new Quad(
                new Vertex(x1, y0, z1, uv.u0(), uv.v1(), 1, 0, 0),
                new Vertex(x1, y0, z0, uv.u1(), uv.v1(), 1, 0, 0),
                new Vertex(x1, y1, z0, uv.u1(), uv.v0(), 1, 0, 0),
                new Vertex(x1, y1, z1, uv.u0(), uv.v0(), 1, 0, 0)
        ));
        // Up face (Y+)
        quads.add(new Quad(
                new Vertex(x0, y1, z1, uv.u0(), uv.v1(), 0, 1, 0),
                new Vertex(x1, y1, z1, uv.u1(), uv.v1(), 0, 1, 0),
                new Vertex(x1, y1, z0, uv.u1(), uv.v0(), 0, 1, 0),
                new Vertex(x0, y1, z0, uv.u0(), uv.v0(), 0, 1, 0)
        ));
        // Down face (Y-)
        quads.add(new Quad(
                new Vertex(x0, y0, z0, uv.u0(), uv.v1(), 0, -1, 0),
                new Vertex(x1, y0, z0, uv.u1(), uv.v1(), 0, -1, 0),
                new Vertex(x1, y0, z1, uv.u1(), uv.v0(), 0, -1, 0),
                new Vertex(x0, y0, z1, uv.u0(), uv.v0(), 0, -1, 0)
        ));
    }
}
