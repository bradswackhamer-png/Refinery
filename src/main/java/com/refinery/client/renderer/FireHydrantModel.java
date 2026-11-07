package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

/**
 * High-detail 3D model for the AWWA Safety-Yellow Fire Hydrant (`refinery:fire_hydrant`).
 * Uses a single 64x64 texture atlas (`textures/entity/fire_hydrant.png`) with 0.5px UV inset margins
 * and strictly disjoint Y/X/Z box bounds so there is zero coplanar Z-fighting.
 */
public class FireHydrantModel {

    // 64x64 Atlas UV Regions: {u0, v0, u1, v1}
    private static final float[] UV_YELLOW_BARREL = {0, 0, 16, 16};
    private static final float[] UV_YELLOW_SMOOTH = {16, 0, 32, 16};
    private static final float[] UV_FRONT_AWWA    = {32, 0, 48, 16};
    private static final float[] UV_PUMPER_CAP    = {48, 0, 64, 16};
    private static final float[] UV_BLACK_IRON    = {0, 16, 16, 32};
    private static final float[] UV_SILVER_BOLT   = {16, 16, 32, 32};
    private static final float[] UV_BRASS_NUT     = {32, 16, 48, 32};
    private static final float[] UV_SIDE_CAP      = {48, 16, 64, 32};
    private static final float[] UV_BONNET_DOME   = {0, 32, 16, 48};
    private static final float[] UV_BOTTOM_INLET  = {16, 32, 32, 48};

    // 8-bolt circle coordinates around base & bonnet flanges (center X, center Z in 0..16 block space)
    private static final float[][] FLANGE_BOLT_COORDS = {
            {6.15f, 3.85f}, {9.85f, 3.85f},   // North pair
            {6.15f, 12.15f}, {9.85f, 12.15f}, // South pair
            {3.85f, 6.15f}, {3.85f, 9.85f},   // West pair
            {12.15f, 6.15f}, {12.15f, 9.85f}  // East pair
    };

    /**
     * Renders the static body of the AWWA Yellow Fire Hydrant (base flanges, bolts, main barrel,
     * embossed AWWA panel, 3-way nozzles, upper bonnet flanges, and domed bonnet cap).
     */
    public void renderStaticBody(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay) {
        // =========================================================================
        // 1. BASE DOUBLE FLANGE & PERIMETER SILVER BOLTS (Y: 0.01 .. 3.78)
        // =========================================================================
        // 1a. Protruding bottom silver hex nuts & threaded studs under the black flange (Y: 0.01 .. 0.60)
        for (float[] bc : FLANGE_BOLT_COORDS) {
            float bx = bc[0];
            float bz = bc[1];
            // Threaded stud tip (Y: 0.01 .. 0.22)
            drawBox(pose, vc, light, overlay,
                    bx - 0.28f, 0.01f, bz - 0.28f,
                    bx + 0.28f, 0.22f, bz + 0.28f,
                    UV_SILVER_BOLT);
            // Bottom silver hex nut (Y: 0.22 .. 0.60)
            drawBox(pose, vc, light, overlay,
                    bx - 0.44f, 0.22f, bz - 0.44f,
                    bx + 0.44f, 0.60f, bz + 0.44f,
                    UV_SILVER_BOLT);
        }

        // 1b. Black cast-iron bottom flange ring (Y: 0.60 .. 1.65)
        // Central core has bottom pipe inlet texture; wings complete octagonal silhouette without overlap
        drawBoxCustomFaces(pose, vc, light, overlay,
                4.30f, 0.60f, 3.30f, 11.70f, 1.65f, 12.70f,
                UV_BLACK_IRON, UV_BOTTOM_INLET, UV_BLACK_IRON, UV_BLACK_IRON, UV_BLACK_IRON, UV_BLACK_IRON);
        drawBox(pose, vc, light, overlay,
                3.30f, 0.60f, 4.30f, 4.30f, 1.65f, 11.70f,
                UV_BLACK_IRON);
        drawBox(pose, vc, light, overlay,
                11.70f, 0.60f, 4.30f, 12.70f, 1.65f, 11.70f,
                UV_BLACK_IRON);

        // 1c. Silver bolt shanks between black flange and yellow lower flange (Y: 1.65 .. 2.25)
        // Plus dark recessed pipe neck in the center (Y: 1.65 .. 2.25)
        drawBox(pose, vc, light, overlay,
                5.20f, 1.65f, 5.20f, 10.80f, 2.25f, 10.80f,
                UV_BLACK_IRON);
        for (float[] bc : FLANGE_BOLT_COORDS) {
            float bx = bc[0];
            float bz = bc[1];
            drawBox(pose, vc, light, overlay,
                    bx - 0.30f, 1.65f, bz - 0.30f,
                    bx + 0.30f, 2.25f, bz + 0.30f,
                    UV_SILVER_BOLT);
        }

        // 1d. Safety-yellow lower barrel flange ring (Y: 2.25 .. 3.25)
        drawOctagonalRing(pose, vc, light, overlay, 2.25f, 3.25f, 4.70f, 3.70f, UV_YELLOW_SMOOTH);

        // 1e. Top silver hex bolt heads on yellow lower flange (Y: 3.25 .. 3.78)
        for (float[] bc : FLANGE_BOLT_COORDS) {
            float bx = bc[0];
            float bz = bc[1];
            drawBox(pose, vc, light, overlay,
                    bx - 0.44f, 3.25f, bz - 0.44f,
                    bx + 0.44f, 3.78f, bz + 0.44f,
                    UV_SILVER_BOLT);
        }

        // 1f. Lower barrel transition fillet ring (Y: 3.25 .. 3.65, inside the bolt circle)
        drawOctagonalRing(pose, vc, light, overlay, 3.25f, 3.65f, 3.45f, 2.65f, UV_YELLOW_SMOOTH);

        // =========================================================================
        // 2. MAIN CYLINDRICAL SAFETY-YELLOW BARREL (Y: 3.65 .. 11.10)
        // =========================================================================
        // Disjoint octagonal cylinder (radius 3.30, inner corner span 2.40):
        // Center span X: 5.60..10.40, Z: 4.70..11.30
        // Left wing X: 4.70..5.60, Z: 5.60..10.40
        // Right wing X: 10.40..11.30, Z: 5.60..10.40
        drawOctagonalRing(pose, vc, light, overlay, 3.65f, 11.10f, 3.30f, 2.40f, UV_YELLOW_BARREL);

        // Embossed front lower barrel plaque ("A" in circle + "250" + "AWWA") on North (-Z) face (Y: 3.85 .. 7.25, Z: 4.58 .. 4.70)
        drawBoxCustomFaces(pose, vc, light, overlay,
                5.65f, 3.85f, 4.58f, 10.35f, 7.25f, 4.70f,
                UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_FRONT_AWWA, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH);

        // =========================================================================
        // 3. THREE-WAY NOZZLE OUTLETS (FRONT 4.5" PUMPER + LEFT/RIGHT 2.5" HOSE)
        // =========================================================================
        // 3a. Front Large Pumper / Steamer Nozzle (North / -Z)
        // Nozzle neck boss emerging from barrel (X: 5.50..10.50, Y: 7.55..10.65, Z: 3.45..4.70)
        drawBox(pose, vc, light, overlay,
                5.50f, 7.55f, 3.45f, 10.50f, 10.65f, 4.70f,
                UV_YELLOW_SMOOTH);
        // Large circular/octagonal Pumper Cap (Y: 7.05..11.15, Z: 2.20..3.45)
        // Center section with UV_PUMPER_CAP on the North (-Z) face
        drawBoxCustomFaces(pose, vc, light, overlay,
                5.55f, 7.05f, 2.20f, 10.45f, 11.15f, 3.45f,
                UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_PUMPER_CAP, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH);
        // Disjoint left and right rounded cap cheeks (Y: 7.55..10.65, Z: 2.20..3.45)
        drawBox(pose, vc, light, overlay,
                4.95f, 7.55f, 2.20f, 5.55f, 10.65f, 3.45f,
                UV_YELLOW_SMOOTH);
        drawBox(pose, vc, light, overlay,
                10.45f, 7.55f, 2.20f, 11.05f, 10.65f, 3.45f,
                UV_YELLOW_SMOOTH);
        // 3D Protruding Front Pumper Pentagonal Nut ("B 41") (X: 7.05..8.95, Y: 8.15..10.05, Z: 1.45..2.20)
        drawBox(pose, vc, light, overlay,
                7.05f, 8.15f, 1.45f, 8.95f, 10.05f, 2.20f,
                UV_YELLOW_SMOOTH);

        // 3b. Left (-X / West) 2.5" Side Hose Nozzle
        // Barrel neck boss (X: 3.45..4.70, Y: 7.90..10.30, Z: 6.80..9.20)
        drawBox(pose, vc, light, overlay,
                3.45f, 7.90f, 6.80f, 4.70f, 10.30f, 9.20f,
                UV_YELLOW_SMOOTH);
        // Dark gasket seam ring (X: 3.25..3.45, Y: 7.95..10.25, Z: 6.85..9.15)
        drawBox(pose, vc, light, overlay,
                3.25f, 7.95f, 6.85f, 3.45f, 10.25f, 9.15f,
                UV_BLACK_IRON);
        // Ribbed Yellow Side Cap (X: 2.05..3.25, Y: 7.75..10.45, Z: 6.65..9.35)
        drawBoxCustomFaces(pose, vc, light, overlay,
                2.05f, 7.75f, 6.65f, 3.25f, 10.45f, 9.35f,
                UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_SIDE_CAP, UV_YELLOW_SMOOTH);
        // Outer Square/Pentagonal Wrench Nut (X: 1.15..2.05, Y: 8.45..9.75, Z: 7.35..8.65)
        drawBox(pose, vc, light, overlay,
                1.15f, 8.45f, 7.35f, 2.05f, 9.75f, 8.65f,
                UV_YELLOW_SMOOTH);
        // Bottom chain-loop attachment lug under left nozzle (X: 3.70..4.55, Y: 7.20..7.90, Z: 7.65..8.35)
        drawBox(pose, vc, light, overlay,
                3.70f, 7.20f, 7.65f, 4.55f, 7.90f, 8.35f,
                UV_YELLOW_SMOOTH);

        // 3c. Right (+X / East) 2.5" Side Hose Nozzle
        // Barrel neck boss (X: 11.30..12.55, Y: 7.90..10.30, Z: 6.80..9.20)
        drawBox(pose, vc, light, overlay,
                11.30f, 7.90f, 6.80f, 12.55f, 10.30f, 9.20f,
                UV_YELLOW_SMOOTH);
        // Dark gasket seam ring (X: 12.55..12.75, Y: 7.95..10.25, Z: 6.85..9.15)
        drawBox(pose, vc, light, overlay,
                12.55f, 7.95f, 6.85f, 12.75f, 10.25f, 9.15f,
                UV_BLACK_IRON);
        // Ribbed Yellow Side Cap (X: 12.75..13.95, Y: 7.75..10.45, Z: 6.65..9.35)
        drawBoxCustomFaces(pose, vc, light, overlay,
                12.75f, 7.75f, 6.65f, 13.95f, 10.45f, 9.35f,
                UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_YELLOW_SMOOTH, UV_SIDE_CAP);
        // Outer Square/Pentagonal Wrench Nut (X: 13.95..14.85, Y: 8.45..9.75, Z: 7.35..8.65)
        drawBox(pose, vc, light, overlay,
                13.95f, 8.45f, 7.35f, 14.85f, 9.75f, 8.65f,
                UV_YELLOW_SMOOTH);
        // Bottom chain-loop attachment lug under right nozzle (X: 11.45..12.30, Y: 7.20..7.90, Z: 7.65..8.35)
        drawBox(pose, vc, light, overlay,
                11.45f, 7.20f, 7.65f, 12.30f, 7.90f, 8.35f,
                UV_YELLOW_SMOOTH);

        // =========================================================================
        // 4. UPPER DOUBLE BONNET FLANGE & PERIMETER SILVER BOLTS (Y: 10.55 .. 13.10)
        // =========================================================================
        // 4a. Bottom silver hex nuts & threaded stud tips below lower bonnet flange (Y: 10.55 .. 11.10)
        for (float[] bc : FLANGE_BOLT_COORDS) {
            float bx = bc[0];
            float bz = bc[1];
            // Threaded stud tip (Y: 10.55 .. 10.75)
            drawBox(pose, vc, light, overlay,
                    bx - 0.26f, 10.55f, bz - 0.26f,
                    bx + 0.26f, 10.75f, bz + 0.26f,
                    UV_SILVER_BOLT);
            // Silver bottom hex nut (Y: 10.75 .. 11.10)
            drawBox(pose, vc, light, overlay,
                    bx - 0.42f, 10.75f, bz - 0.42f,
                    bx + 0.42f, 11.10f, bz + 0.42f,
                    UV_SILVER_BOLT);
        }

        // 4b. Lower yellow bonnet flange ring (Y: 11.10 .. 11.80)
        drawOctagonalRing(pose, vc, light, overlay, 11.10f, 11.80f, 4.65f, 3.65f, UV_YELLOW_SMOOTH);

        // 4c. Dark gasket seam between lower and upper bonnet flanges (Y: 11.80 .. 11.95)
        drawOctagonalRing(pose, vc, light, overlay, 11.80f, 11.95f, 4.40f, 3.45f, UV_BLACK_IRON);

        // 4d. Upper yellow bonnet flange ring (Y: 11.95 .. 12.65)
        drawOctagonalRing(pose, vc, light, overlay, 11.95f, 12.65f, 4.65f, 3.65f, UV_YELLOW_SMOOTH);

        // 4e. Top silver hex bolt heads on upper bonnet flange (Y: 12.65 .. 13.10)
        for (float[] bc : FLANGE_BOLT_COORDS) {
            float bx = bc[0];
            float bz = bc[1];
            drawBox(pose, vc, light, overlay,
                    bx - 0.42f, 12.65f, bz - 0.42f,
                    bx + 0.42f, 13.10f, bz + 0.42f,
                    UV_SILVER_BOLT);
        }

        // =========================================================================
        // 5. DOMED YELLOW BONNET CAP & BLACK THRUST WASHER (Y: 12.65 .. 14.90)
        // =========================================================================
        // Lower bonnet dome tier (Y: 12.65 .. 13.60)
        drawOctagonalRing(pose, vc, light, overlay, 12.65f, 13.60f, 3.25f, 2.35f, UV_BONNET_DOME);
        // Middle bonnet dome tier (Y: 13.60 .. 14.25)
        drawOctagonalRing(pose, vc, light, overlay, 13.60f, 14.25f, 2.65f, 1.90f, UV_BONNET_DOME);
        // Upper bonnet dome crown (Y: 14.25 .. 14.65)
        drawOctagonalRing(pose, vc, light, overlay, 14.25f, 14.65f, 1.85f, 1.35f, UV_BONNET_DOME);

        // Black cast-iron top thrust washer / stem seal collar (Y: 14.65 .. 14.90)
        drawBox(pose, vc, light, overlay,
                6.65f, 14.65f, 6.65f, 9.35f, 14.90f, 9.35f,
                UV_BLACK_IRON);
    }

    /**
     * Renders the top Brass Pentagonal Operating Stem Nut (Y: 14.90 .. 15.98).
     * Can be rotated around the block center when the hydrant valve is actuated.
     */
    public void renderTopOperatingNut(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay) {
        // Brass stem base collar (Y: 14.90 .. 15.12)
        drawBox(pose, vc, light, overlay,
                6.95f, 14.90f, 6.95f, 9.05f, 15.12f, 9.05f,
                UV_BRASS_NUT);
        // Brass pentagonal operating nut body (Y: 15.12 .. 15.98)
        drawBox(pose, vc, light, overlay,
                7.15f, 15.12f, 7.15f, 8.85f, 15.98f, 8.85f,
                UV_BRASS_NUT);
    }

    /**
     * Draws a pseudo-cylindrical octagonal ring between [y1, y2] centered at (8, 8) using
     * three strictly disjoint boxes (center Z-spanning box + left X wing + right X wing).
     * Because the 3 boxes do not overlap in X, Y, or Z, there is zero internal Z-fighting.
     */
    private void drawOctagonalRing(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay,
                                   float y1, float y2, float outerRadius, float innerSpan, float[] uv) {
        float cMinX = 8.0f - innerSpan;
        float cMaxX = 8.0f + innerSpan;
        float cMinZ = 8.0f - outerRadius;
        float cMaxZ = 8.0f + outerRadius;

        float wMinX_L = 8.0f - outerRadius;
        float wMaxX_L = cMinX;
        float wMinX_R = cMaxX;
        float wMaxX_R = 8.0f + outerRadius;
        float wMinZ = 8.0f - innerSpan;
        float wMaxZ = 8.0f + innerSpan;

        // Center box (full Z depth, inner X width)
        drawBox(pose, vc, light, overlay, cMinX, y1, cMinZ, cMaxX, y2, cMaxZ, uv);
        // Left wing (outer X left, inner Z depth)
        drawBox(pose, vc, light, overlay, wMinX_L, y1, wMinZ, wMaxX_L, y2, wMaxZ, uv);
        // Right wing (outer X right, inner Z depth)
        drawBox(pose, vc, light, overlay, wMinX_R, y1, wMinZ, wMaxX_R, y2, wMaxZ, uv);
    }

    private void drawBox(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay,
                         float x1, float y1, float z1, float x2, float y2, float z2,
                         float[] uv) {
        drawBoxCustomFaces(pose, vc, light, overlay, x1, y1, z1, x2, y2, z2, uv, uv, uv, uv, uv, uv);
    }

    private void drawBoxCustomFaces(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay,
                                    float x1, float y1, float z1, float x2, float y2, float z2,
                                    float[] uvUp, float[] uvDown, float[] uvNorth, float[] uvSouth, float[] uvWest, float[] uvEast) {
        Matrix4f mat = pose.pose();
        float fx1 = x1 / 16f, fy1 = y1 / 16f, fz1 = z1 / 16f;
        float fx2 = x2 / 16f, fy2 = y2 / 16f, fz2 = z2 / 16f;

        // UP (+Y)
        emitQuad(mat, pose, vc, light, overlay,
                fx1, fy2, fz1, fx1, fy2, fz2, fx2, fy2, fz2, fx2, fy2, fz1,
                0, 1, 0, uvUp);
        // DOWN (-Y)
        emitQuad(mat, pose, vc, light, overlay,
                fx1, fy1, fz2, fx1, fy1, fz1, fx2, fy1, fz1, fx2, fy1, fz2,
                0, -1, 0, uvDown);
        // NORTH (-Z)
        emitQuad(mat, pose, vc, light, overlay,
                fx2, fy2, fz1, fx2, fy1, fz1, fx1, fy1, fz1, fx1, fy2, fz1,
                0, 0, -1, uvNorth);
        // SOUTH (+Z)
        emitQuad(mat, pose, vc, light, overlay,
                fx1, fy2, fz2, fx1, fy1, fz2, fx2, fy1, fz2, fx2, fy2, fz2,
                0, 0, 1, uvSouth);
        // WEST (-X)
        emitQuad(mat, pose, vc, light, overlay,
                fx1, fy2, fz1, fx1, fy1, fz1, fx1, fy1, fz2, fx1, fy2, fz2,
                -1, 0, 0, uvWest);
        // EAST (+X)
        emitQuad(mat, pose, vc, light, overlay,
                fx2, fy2, fz2, fx2, fy1, fz2, fx2, fy1, fz1, fx2, fy2, fz1,
                1, 0, 0, uvEast);
    }

    private void emitQuad(Matrix4f mat, PoseStack.Pose pose, VertexConsumer vc, int light, int overlay,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float nx, float ny, float nz,
                          float[] uv) {
        // Inset UV coordinates by 0.5px inside the 64x64 atlas to prevent mipmap/bilinear bleed
        float margin = 0.5f;
        float u0 = (uv[0] + margin) / 64f;
        float v0 = (uv[1] + margin) / 64f;
        float u1 = (uv[2] - margin) / 64f;
        float v1 = (uv[3] - margin) / 64f;

        vc.addVertex(mat, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, x3, y3, z3).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, x4, y4, z4).setColor(255, 255, 255, 255).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }
}
