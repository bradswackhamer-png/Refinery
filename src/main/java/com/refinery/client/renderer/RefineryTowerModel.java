package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Zero-flicker 3D Monolithic Model for the 3-Stage Petroleum Refinery Tower (3x9x3 blocks = 48x144x48 model units).
 *
 * Centered horizontally at (0, 0, 0) -> spans X: [-24..+24], Y: [0..144], Z: [-24..+24].
 * Uses a 16-sided polygonal cylinder/frustum emitter with strictly sequential, non-overlapping vertical segments
 * and a 0.5-pixel UV inset so no two faces are ever coplanar or intersecting.
 */
public final class RefineryTowerModel {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/refinery_tower.png"
    );

    private static final float TEX_SIZE = 256.0f;
    private static final int CYLINDER_SIDES = 16;

    // 256x256 Atlas Tile Coordinates (each tile is 32x32 with inner padding)
    private static final int[] UV_DARK_STEEL       = {0,   0,  32, 32};
    private static final int[] UV_CONCRETE_BASE    = {32,  0,  32, 32};
    private static final int[] UV_STAINLESS_SILVER = {64,  0,  32, 32};
    private static final int[] UV_BRUSHED_STEEL    = {96,  0,  32, 32};
    private static final int[] UV_COPPER_BRONZE    = {128, 0,  32, 32};
    private static final int[] UV_HAZARD_YELLOW    = {160, 0,  32, 32};
    private static final int[] UV_STAGE_OFF        = {192, 0,  32, 32};
    private static final int[] UV_CONSOLE_PANEL    = {224, 0,  32, 32};

    // Temperature Gradient Tiles (Row 2: Y = 32..64)
    private static final int[] UV_STAGE1_DEEP_RED  = {0,   32, 32, 32}; // 350-500C Bottom
    private static final int[] UV_STAGE1_CRIMSON   = {32,  32, 32, 32}; // 350C Upper Stage 1
    private static final int[] UV_STAGE2_ORANGE    = {64,  32, 32, 32}; // 250-350C Lower Stage 2
    private static final int[] UV_STAGE2_AMBER     = {96,  32, 32, 32}; // 180-250C Upper Stage 2
    private static final int[] UV_STAGE3_LIME      = {128, 32, 32, 32}; // 110-180C Lower Stage 3
    private static final int[] UV_STAGE3_GREEN     = {160, 32, 32, 32}; // 40-110C Upper Stage 3
    private static final int[] UV_STAGE3_BLUE      = {192, 32, 32, 32}; // <40C Top Condenser
    private static final int[] UV_VALVE_RED        = {224, 32, 32, 32}; // Valve Wheel / Indicator

    private RefineryTowerModel() {}

    public static void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            boolean isActive,
            boolean stage1Enabled,
            boolean stage2Enabled,
            boolean stage3Enabled
    ) {
        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, vc) -> {
            renderGeometry(pose, vc, packedLight, OverlayTexture.NO_OVERLAY, isActive, stage1Enabled, stage2Enabled, stage3Enabled);
        });
    }

    public static void renderGeometry(
            PoseStack.Pose pose,
            VertexConsumer vc,
            int packedLight,
            int packedOverlay,
            boolean isActive,
            boolean stage1Enabled,
            boolean stage2Enabled,
            boolean stage3Enabled
    ) {
        Matrix4f matrix = pose.pose();
        int fullBright = 15728880;

        // =========================================================================
        // 1. BASE FOUNDATION PAD & SUPPORT SKID (Y = 0..6, strictly disjoint planes)
        // =========================================================================
        emitBox(pose, matrix, vc, -23.0f, 0.0f, -23.0f, 23.0f, 3.5f, 23.0f, UV_CONCRETE_BASE, packedLight, packedOverlay, true, true);
        emitBox(pose, matrix, vc, -20.0f, 3.5f, -20.0f, 20.0f, 6.0f, 20.0f, UV_DARK_STEEL, packedLight, packedOverlay, false, true);

        // 4 Heavy Steel Column Support Legs: Y = 6.0 .. 14.0 around the lower sump
        emitBox(pose, matrix, vc, -14.5f, 6.0f, -14.5f, -11.5f, 14.0f, -11.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, false);
        emitBox(pose, matrix, vc,  11.5f, 6.0f, -14.5f,  14.5f, 14.0f, -11.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, false);
        emitBox(pose, matrix, vc, -14.5f, 6.0f,  11.5f, -11.5f, 14.0f,  14.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, false);
        emitBox(pose, matrix, vc,  11.5f, 6.0f,  11.5f,  14.5f, 14.0f,  14.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, false);

        // Central Drain Sump Pipe: Y = 6.0 .. 8.0
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 6.0f, 8.0f, 4.5f, 4.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, false);

        // =========================================================================
        // 2. MAIN FRACTIONATING COLUMN SHELL (Y = 8..140.5, 16-sided polygonal cylinder)
        //    Strictly sequential Y intervals so zero faces ever overlap!
        // =========================================================================
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 8.0f, 10.0f, 4.5f, 11.0f, UV_STAINLESS_SILVER, packedLight, packedOverlay, false, false);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 10.0f, 12.5f, 11.0f, 15.0f, UV_STAINLESS_SILVER, packedLight, packedOverlay, false, false);

        // Bottom Main Flange Ring: Y = 12.5 .. 14.5 (r = 16.2)
        emitFlangeRing(pose, matrix, vc, 12.5f, 14.5f, 15.0f, 16.2f, UV_STAINLESS_SILVER, packedLight, packedOverlay);

        // --- STAGE 1 (BOTTOM THIRD: Y = 0..48, Shell Y = 14.5..46.0) ---
        int[] s1LowUV  = stage1Enabled ? UV_STAGE1_DEEP_RED : UV_STAGE_OFF;
        int[] s1HighUV = stage1Enabled ? UV_STAGE1_CRIMSON  : UV_STAGE_OFF;
        int s1Light    = (stage1Enabled && isActive) ? fullBright : packedLight;

        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 14.5f, 29.0f, 15.0f, 15.0f, s1LowUV, s1Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 29.0f, 31.0f, 15.0f, 15.8f, UV_STAINLESS_SILVER, packedLight, packedOverlay);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 31.0f, 46.0f, 15.0f, 15.0f, s1HighUV, s1Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 46.0f, 48.5f, 15.0f, 16.5f, UV_STAINLESS_SILVER, packedLight, packedOverlay);

        // --- STAGE 2 (MIDDLE THIRD: Y = 48..96, Shell Y = 48.5..94.0) ---
        int[] s2LowUV  = stage2Enabled ? UV_STAGE2_ORANGE : UV_STAGE_OFF;
        int[] s2HighUV = stage2Enabled ? UV_STAGE2_AMBER  : UV_STAGE_OFF;
        int s2Light    = (stage2Enabled && isActive) ? fullBright : packedLight;

        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 48.5f, 70.0f, 15.0f, 15.0f, s2LowUV, s2Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 70.0f, 72.0f, 15.0f, 15.8f, UV_STAINLESS_SILVER, packedLight, packedOverlay);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 72.0f, 94.0f, 15.0f, 15.0f, s2HighUV, s2Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 94.0f, 96.5f, 15.0f, 16.5f, UV_STAINLESS_SILVER, packedLight, packedOverlay);

        // --- STAGE 3 (TOP THIRD: Y = 96..144, Shell Y = 96.5..134.5) ---
        int[] s3LowUV  = stage3Enabled ? UV_STAGE3_LIME  : UV_STAGE_OFF;
        int[] s3MidUV  = stage3Enabled ? UV_STAGE3_GREEN : UV_STAGE_OFF;
        int[] s3TopUV  = stage3Enabled ? UV_STAGE3_BLUE  : UV_STAGE_OFF;
        int s3Light    = (stage3Enabled && isActive) ? fullBright : packedLight;

        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 96.5f, 109.0f, 15.0f, 15.0f, s3LowUV, s3Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 109.0f, 110.5f, 15.0f, 15.8f, UV_STAINLESS_SILVER, packedLight, packedOverlay);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 110.5f, 122.5f, 15.0f, 15.0f, s3MidUV, s3Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 122.5f, 124.0f, 15.0f, 15.8f, UV_STAINLESS_SILVER, packedLight, packedOverlay);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 124.0f, 133.0f, 15.0f, 15.0f, s3TopUV, s3Light, packedOverlay, false, false);
        emitFlangeRing(pose, matrix, vc, 133.0f, 134.5f, 15.0f, 16.2f, UV_STAINLESS_SILVER, packedLight, packedOverlay);

        // --- TOP SILVER HEMISPHERICAL DOME (Y = 134.5 .. 140.5) ---
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 134.5f, 137.0f, 15.0f, 12.5f, UV_STAINLESS_SILVER, packedLight, packedOverlay, false, false);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 137.0f, 139.0f, 12.5f, 8.5f,  UV_STAINLESS_SILVER, packedLight, packedOverlay, false, false);
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 139.0f, 140.5f, 8.5f,  3.5f,  UV_STAINLESS_SILVER, packedLight, packedOverlay, false, true);

        // =========================================================================
        // 3. OVERHEAD VAPOUR GOOSENECK PIPE & CONDENSER LOOP (Y = 140.5 .. 144.0)
        // =========================================================================
        emitCylinder(pose, matrix, vc, 0.0f, 0.0f, 140.5f, 143.5f, 2.2f, 2.2f, UV_BRUSHED_STEEL, packedLight, packedOverlay, false, true);
        emitBox(pose, matrix, vc, 2.2f, 141.2f, -1.2f, 19.0f, 143.2f, 1.2f, UV_BRUSHED_STEEL, packedLight, packedOverlay, true, true);
        emitBox(pose, matrix, vc, 17.0f, 128.0f, -1.2f, 19.0f, 141.2f, 1.2f, UV_BRUSHED_STEEL, packedLight, packedOverlay, true, false);
        emitBox(pose, matrix, vc, 15.1f, 127.5f, -1.4f, 17.0f, 129.5f, 1.4f, UV_STAINLESS_SILVER, packedLight, packedOverlay, true, true);

        // =========================================================================
        // 4. SOUTH (+Z) 3-STAGE PRODUCT TAKEOFF VALVES (TAR, DIESEL & GASOLINE)
        //    Rotated 90 degrees to point South (+Z) towards the Tar, Diesel & Gas ports.
        //    Positioned to line up exactly with adjacent external pipes and output ports:
        //    - Stage 1 (Tar):      Layer 0 (Y = 0) -> centerY = 8.0f  (extends from bottom sump at Z = 4.0f)
        //    - Stage 2 (Diesel):   Layer 3 (Y = 3) -> centerY = 56.0f (extends from shell at Z = 14.5f)
        //    - Stage 3 (Gasoline): Layer 6 (Y = 6) -> centerY = 104.0f (extends from shell at Z = 14.5f)
        // =========================================================================
        emitTakeoffValveAssemblySouth(pose, matrix, vc, 8.0f, 4.0f, stage1Enabled ? UV_STAGE1_CRIMSON : UV_DARK_STEEL, packedLight, packedOverlay);
        emitTakeoffValveAssemblySouth(pose, matrix, vc, 56.0f, 14.5f, stage2Enabled ? UV_STAGE2_AMBER : UV_DARK_STEEL, packedLight, packedOverlay);
        emitTakeoffValveAssemblySouth(pose, matrix, vc, 104.0f, 14.5f, stage3Enabled ? UV_STAGE3_BLUE : UV_DARK_STEEL, packedLight, packedOverlay);

        // =========================================================================
        // 5. LEFT SIDE (-X) HEATED CRUDE INLET PIPE & CAGED SAFETY LADDER (Y = 6..134)
        // =========================================================================
        emitBox(pose, matrix, vc, -23.5f, 17.0f, -1.5f, -15.1f, 20.0f, 1.5f, UV_COPPER_BRONZE, packedLight, packedOverlay, true, true);
        emitBox(pose, matrix, vc, -24.0f, 16.2f, -2.3f, -23.5f, 20.8f, 2.3f, UV_STAINLESS_SILVER, packedLight, packedOverlay, true, true);

        // Left Safety Ladder Rails (X = -19.5 .. -18.7, Z = -2.2 .. -1.4 and Z = 1.4 .. 2.2, Y = 22.0 .. 132.0)
        emitBox(pose, matrix, vc, -19.5f, 22.0f, -2.2f, -18.7f, 132.0f, -1.4f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);
        emitBox(pose, matrix, vc, -19.5f, 22.0f,  1.4f, -18.7f, 132.0f,  2.2f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);

        // Ladder Stand-off Brackets to Tower Shell (at Y = 32, 64, 96, 126)
        int[] bracketYs = {32, 64, 96, 126};
        for (int by : bracketYs) {
            emitBox(pose, matrix, vc, -18.7f, by, -2.2f, -15.2f, by + 1.2f, -1.4f, UV_DARK_STEEL, packedLight, packedOverlay, true, true);
            emitBox(pose, matrix, vc, -18.7f, by,  1.4f, -15.2f, by + 1.2f,  2.2f, UV_DARK_STEEL, packedLight, packedOverlay, true, true);
        }

        // Ladder Rungs every 6 units (Y = 24 .. 130, strictly between rails Z = -1.4 .. +1.4)
        for (int ry = 24; ry <= 130; ry += 6) {
            emitBox(pose, matrix, vc, -19.3f, ry, -1.4f, -18.9f, ry + 0.6f, 1.4f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);
        }

        // Safety Cage Hoops at Stage 1, Stage 2, and Stage 3 Platforms (Y = 44, 72, 92, 124)
        int[] cageYs = {44, 72, 92, 124};
        for (int cy : cageYs) {
            emitBox(pose, matrix, vc, -22.5f, cy, -3.2f, -19.5f, cy + 1.2f, -2.4f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);
            emitBox(pose, matrix, vc, -22.5f, cy,  2.4f, -19.5f, cy + 1.2f,  3.2f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);
            emitBox(pose, matrix, vc, -22.5f, cy, -2.4f, -21.7f, cy + 1.2f,  2.4f, UV_HAZARD_YELLOW, packedLight, packedOverlay, true, true);
        }

        // =========================================================================
        // 6. FRONT (-Z) OPERATOR CONTROL CONSOLE (Y = 6..22, Z = -22.0..-17.5)
        // =========================================================================
        emitBox(pose, matrix, vc, -7.0f, 6.0f, -21.5f, 7.0f, 18.0f, -17.5f, UV_DARK_STEEL, packedLight, packedOverlay, false, true);
        emitBox(pose, matrix, vc, -6.5f, 18.0f, -21.0f, 6.5f, 22.0f, -17.5f, UV_CONSOLE_PANEL, isActive ? fullBright : packedLight, packedOverlay, false, true);
    }

    private static void emitTakeoffValveAssemblySouth(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer vc,
            float centerY,
            float fromZ,
            int[] valveUV,
            int light,
            int overlay
    ) {
        // Horizontal pipe extending South (+Z) from cylinder outer wall (Z = fromZ to 18.5f)
        emitBox(pose, matrix, vc, -1.2f, centerY - 1.2f, fromZ, 1.2f, centerY + 1.2f, 18.5f, UV_BRUSHED_STEEL, light, overlay, true, true);
        // Flange ring / collar (Z = 18.5f to 19.5f)
        emitBox(pose, matrix, vc, -2.0f, centerY - 2.0f, 18.5f, 2.0f, centerY + 2.0f, 19.5f, UV_STAINLESS_SILVER, light, overlay, true, true);
        // Valve body / box (Z = 19.5f to 22.5f)
        emitBox(pose, matrix, vc, -1.5f, centerY - 1.5f, 19.5f, 1.5f, centerY + 1.5f, 22.5f, UV_DARK_STEEL, light, overlay, true, true);
        // Outer connection flange (Z = 22.5f to 23.5f)
        emitBox(pose, matrix, vc, -2.0f, centerY - 2.0f, 22.5f, 2.0f, centerY + 2.0f, 23.5f, UV_STAINLESS_SILVER, light, overlay, true, true);
        // Valve stem rising up (+Y) from top of valve body
        emitBox(pose, matrix, vc, -0.5f, centerY + 1.5f, 20.5f, 0.5f, centerY + 3.5f, 21.5f, UV_STAINLESS_SILVER, light, overlay, false, false);
        // Valve handwheel on top of stem
        emitBox(pose, matrix, vc, -1.8f, centerY + 3.5f, 19.2f, 1.8f, centerY + 4.3f, 22.8f, valveUV, light, overlay, true, true);
    }

    private static void emitFlangeRing(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer vc,
            float y0,
            float y1,
            float innerR,
            float outerR,
            int[] uv,
            int light,
            int overlay
    ) {
        float u0 = (uv[0] + 0.5f) / TEX_SIZE;
        float v0 = (uv[1] + 0.5f) / TEX_SIZE;
        float u1 = (uv[0] + uv[2] - 0.5f) / TEX_SIZE;
        float v1 = (uv[1] + uv[3] - 0.5f) / TEX_SIZE;

        for (int i = 0; i < CYLINDER_SIDES; i++) {
            float a0 = (float) (i * 2.0 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2.0 * Math.PI / CYLINDER_SIDES);
            float cos0 = Mth.cos(a0), sin0 = Mth.sin(a0);
            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float nx = Mth.cos((a0 + a1) * 0.5f);
            float nz = Mth.sin((a0 + a1) * 0.5f);

            float ox0 = cos0 * outerR, oz0 = sin0 * outerR;
            float ox1 = cos1 * outerR, oz1 = sin1 * outerR;
            float ix0 = cos0 * innerR, iz0 = sin0 * innerR;
            float ix1 = cos1 * innerR, iz1 = sin1 * innerR;

            // Outer cylindrical band (outward normal nx, 0, nz)
            vert(pose, matrix, vc, ox1, y1, oz1, u0, v0, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, ox1, y0, oz1, u0, v1, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, ox0, y0, oz0, u1, v1, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, ox0, y1, oz0, u1, v0, nx, 0, nz, light, overlay);

            // Top annular lip (+Y normal)
            vert(pose, matrix, vc, ix0, y1, iz0, u0, v0, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, ix1, y1, iz1, u0, v1, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, ox1, y1, oz1, u1, v1, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, ox0, y1, oz0, u1, v0, 0, 1, 0, light, overlay);

            // Bottom annular lip (-Y normal)
            vert(pose, matrix, vc, ox0, y0, oz0, u0, v0, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, ox1, y0, oz1, u0, v1, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, ix1, y0, iz1, u1, v1, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, ix0, y0, iz0, u1, v0, 0, -1, 0, light, overlay);
        }
    }

    private static void emitCylinder(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer vc,
            float cx,
            float cz,
            float y0,
            float y1,
            float r0,
            float r1,
            int[] uv,
            int light,
            int overlay,
            boolean capBottom,
            boolean capTop
    ) {
        float u0 = (uv[0] + 0.5f) / TEX_SIZE;
        float v0 = (uv[1] + 0.5f) / TEX_SIZE;
        float u1 = (uv[0] + uv[2] - 0.5f) / TEX_SIZE;
        float v1 = (uv[1] + uv[3] - 0.5f) / TEX_SIZE;
        float uMid = 0.5f * (u0 + u1);
        float vMid = 0.5f * (v0 + v1);

        for (int i = 0; i < CYLINDER_SIDES; i++) {
            float a0 = (float) (i * 2.0 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2.0 * Math.PI / CYLINDER_SIDES);
            float cos0 = Mth.cos(a0), sin0 = Mth.sin(a0);
            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float nx = Mth.cos((a0 + a1) * 0.5f);
            float nz = Mth.sin((a0 + a1) * 0.5f);

            float x00 = cx + cos0 * r0, z00 = cz + sin0 * r0;
            float x10 = cx + cos1 * r0, z10 = cz + sin1 * r0;
            float x01 = cx + cos0 * r1, z01 = cz + sin0 * r1;
            float x11 = cx + cos1 * r1, z11 = cz + sin1 * r1;

            // Side quad (CCW outward)
            vert(pose, matrix, vc, x11, y1, z11, u0, v0, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, x10, y0, z10, u0, v1, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, x00, y0, z00, u1, v1, nx, 0, nz, light, overlay);
            vert(pose, matrix, vc, x01, y1, z01, u1, v0, nx, 0, nz, light, overlay);

            if (capTop && r1 > 0.01f) {
                vert(pose, matrix, vc, cx,  y1, cz,  uMid, vMid, 0, 1, 0, light, overlay);
                vert(pose, matrix, vc, x11, y1, z11, u0,   v1,   0, 1, 0, light, overlay);
                vert(pose, matrix, vc, x01, y1, z01, u1,   v1,   0, 1, 0, light, overlay);
                vert(pose, matrix, vc, cx,  y1, cz,  uMid, vMid, 0, 1, 0, light, overlay);
            }

            if (capBottom && r0 > 0.01f) {
                vert(pose, matrix, vc, cx,  y0, cz,  uMid, vMid, 0, -1, 0, light, overlay);
                vert(pose, matrix, vc, x00, y0, z00, u0,   v1,   0, -1, 0, light, overlay);
                vert(pose, matrix, vc, x10, y0, z10, u1,   v1,   0, -1, 0, light, overlay);
                vert(pose, matrix, vc, cx,  y0, cz,  uMid, vMid, 0, -1, 0, light, overlay);
            }
        }
    }

    private static void emitBox(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer vc,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            int[] uv,
            int light,
            int overlay,
            boolean drawBottom,
            boolean drawTop
    ) {
        float u0 = (uv[0] + 0.5f) / TEX_SIZE;
        float v0 = (uv[1] + 0.5f) / TEX_SIZE;
        float u1 = (uv[0] + uv[2] - 0.5f) / TEX_SIZE;
        float v1 = (uv[1] + uv[3] - 0.5f) / TEX_SIZE;

        // North (-Z)
        vert(pose, matrix, vc, x1, y1, z0, u0, v0, 0, 0, -1, light, overlay);
        vert(pose, matrix, vc, x1, y0, z0, u0, v1, 0, 0, -1, light, overlay);
        vert(pose, matrix, vc, x0, y0, z0, u1, v1, 0, 0, -1, light, overlay);
        vert(pose, matrix, vc, x0, y1, z0, u1, v0, 0, 0, -1, light, overlay);

        // South (+Z)
        vert(pose, matrix, vc, x0, y1, z1, u0, v0, 0, 0, 1, light, overlay);
        vert(pose, matrix, vc, x0, y0, z1, u0, v1, 0, 0, 1, light, overlay);
        vert(pose, matrix, vc, x1, y0, z1, u1, v1, 0, 0, 1, light, overlay);
        vert(pose, matrix, vc, x1, y1, z1, u1, v0, 0, 0, 1, light, overlay);

        // West (-X)
        vert(pose, matrix, vc, x0, y1, z0, u0, v0, -1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x0, y0, z0, u0, v1, -1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x0, y0, z1, u1, v1, -1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x0, y1, z1, u1, v0, -1, 0, 0, light, overlay);

        // East (+X)
        vert(pose, matrix, vc, x1, y1, z1, u0, v0, 1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x1, y0, z1, u0, v1, 1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x1, y0, z0, u1, v1, 1, 0, 0, light, overlay);
        vert(pose, matrix, vc, x1, y1, z0, u1, v0, 1, 0, 0, light, overlay);

        if (drawTop) {
            vert(pose, matrix, vc, x0, y1, z0, u0, v0, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, x0, y1, z1, u0, v1, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, x1, y1, z1, u1, v1, 0, 1, 0, light, overlay);
            vert(pose, matrix, vc, x1, y1, z0, u1, v0, 0, 1, 0, light, overlay);
        }

        if (drawBottom) {
            vert(pose, matrix, vc, x0, y0, z1, u0, v0, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, x1, y0, z1, u0, v1, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, x1, y0, z0, u1, v1, 0, -1, 0, light, overlay);
            vert(pose, matrix, vc, x0, y0, z0, u1, v0, 0, -1, 0, light, overlay);
        }
    }

    private static void vert(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer vc,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz,
            int light,
            int overlay
    ) {
        vc.addVertex(matrix, x / 16.0f, y / 16.0f, z / 16.0f)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
