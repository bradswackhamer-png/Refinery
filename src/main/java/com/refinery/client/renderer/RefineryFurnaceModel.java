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
 * Zero-flicker 2-Block-Tall (1x2x1) Monolithic 3D Model for the Burner & Electric Refinery Furnaces.
 * Modeled after the reference Fractional Distillation Furnace with:
 * - Heavy steel housing & pitched roof hood
 * - Open front viewport into the refractory firebox
 * - 5-turn serpentine crude-oil heating coil suspended inside the chamber
 * - Glowing coal ember bed & flame core (Burner) or High-Voltage RF induction coils & busbars (Electric)
 * - Cylindrical exhaust stack & side heated crude vapour outlet pipe
 *
 * All boxes and cylinder segments use strictly disjoint coordinates and 0.5px UV inset.
 */
public final class RefineryFurnaceModel {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/refinery_furnace.png"
    );

    private static final float TEX_SIZE = 256.0f;
    private static final int STACK_SIDES = 12;

    // 256x256 Atlas Tile Coordinates
    private static final int[] UV_BURNER_CASING   = {0,   0,  32, 32};
    private static final int[] UV_ELECTRIC_CASING = {32,  0,  32, 32};
    private static final int[] UV_REFRACTORY_WALL = {64,  0,  32, 32};
    private static final int[] UV_STAINLESS_STACK = {96,  0,  32, 32};
    private static final int[] UV_COPPER_COIL_COLD= {128, 0,  32, 32};
    private static final int[] UV_COPPER_COIL_HOT = {160, 0,  32, 32};
    private static final int[] UV_EMBER_BED_COLD  = {192, 0,  32, 32};
    private static final int[] UV_EMBER_BED_HOT   = {224, 0,  32, 32};

    private static final int[] UV_FLAME_CORE      = {0,   32, 32, 32};
    private static final int[] UV_RF_COIL_HOT     = {32,  32, 32, 32};
    private static final int[] UV_RF_TRANSFORMER  = {64,  32, 32, 32};
    private static final int[] UV_DARK_TRIM       = {96,  32, 32, 32};

    private RefineryFurnaceModel() {}

    public static void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            boolean isElectric,
            boolean isActive,
            int temperature
    ) {
        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, vc) -> {
            renderGeometry(pose, vc, packedLight, OverlayTexture.NO_OVERLAY, isElectric, isActive, temperature);
        });
    }

    public static void renderGeometry(
            PoseStack.Pose pose,
            VertexConsumer vc,
            int packedLight,
            int packedOverlay,
            boolean isElectric,
            boolean isActive,
            int temperature
    ) {
        Matrix4f matrix = pose.pose();
        int fullBright = 15728880;
        boolean hot = isActive || temperature >= 350;
        int coilLight = hot ? fullBright : packedLight;

        int[] casingUV = isElectric ? UV_ELECTRIC_CASING : UV_BURNER_CASING;
        int[] coilUV   = hot ? UV_COPPER_COIL_HOT : UV_COPPER_COIL_COLD;

        // =========================================================================
        // 1. SUPPORT LEGS & LOWER HEARTH BASE (Y = 0.0 .. 3.5, strictly disjoint)
        // =========================================================================
        // 4 Corner Support Legs: Y = 0.0 .. 2.0
        emitBox(pose, matrix, vc, 0.5f,  0.0f, 0.5f,  3.5f,  2.0f, 3.5f,  UV_DARK_TRIM, packedLight, packedOverlay, true, false);
        emitBox(pose, matrix, vc, 12.5f, 0.0f, 0.5f,  15.5f, 2.0f, 3.5f,  UV_DARK_TRIM, packedLight, packedOverlay, true, false);
        emitBox(pose, matrix, vc, 0.5f,  0.0f, 12.5f, 3.5f,  2.0f, 15.5f, UV_DARK_TRIM, packedLight, packedOverlay, true, false);
        emitBox(pose, matrix, vc, 12.5f, 0.0f, 12.5f, 15.5f, 2.0f, 15.5f, UV_DARK_TRIM, packedLight, packedOverlay, true, false);

        // Main Hearth Base Slab: Y = 2.0 .. 3.5
        emitBox(pose, matrix, vc, 0.5f, 2.0f, 0.5f, 15.5f, 3.5f, 15.5f, casingUV, packedLight, packedOverlay, true, true);

        // =========================================================================
        // 2. FURNACE WALLS (Y = 3.5 .. 22.0, open front viewport on -Z)
        // =========================================================================
        // Left Wall (-X): X = 1.0 .. 2.8, Y = 3.5 .. 22.0, Z = 1.0 .. 15.0
        emitBox(pose, matrix, vc, 1.0f, 3.5f, 1.0f, 2.8f, 22.0f, 15.0f, casingUV, packedLight, packedOverlay, false, false);
        // Right Wall (+X): X = 13.2 .. 15.0, Y = 3.5 .. 22.0, Z = 1.0 .. 15.0
        emitBox(pose, matrix, vc, 13.2f, 3.5f, 1.0f, 15.0f, 22.0f, 15.0f, casingUV, packedLight, packedOverlay, false, false);
        // Back Wall (+Z): X = 2.8 .. 13.2, Y = 3.5 .. 22.0, Z = 13.2 .. 15.0
        emitBox(pose, matrix, vc, 2.8f, 3.5f, 13.2f, 13.2f, 22.0f, 15.0f, UV_REFRACTORY_WALL, packedLight, packedOverlay, false, false);
        // Front Upper Arch Header (-Z): X = 2.8 .. 13.2, Y = 19.5 .. 22.0, Z = 1.0 .. 2.5
        emitBox(pose, matrix, vc, 2.8f, 19.5f, 1.0f, 13.2f, 22.0f, 2.5f, casingUV, packedLight, packedOverlay, true, false);

        // =========================================================================
        // 3. FIREBOX INTERIOR: EMBER BED / RF ELEMENT + 5-TURN SERPENTINE COIL
        // =========================================================================
        // Hearth Floor Bed: X = 2.8 .. 13.2, Y = 3.5 .. 5.2, Z = 2.5 .. 13.2
        int[] bedUV = isElectric
                ? (hot ? UV_RF_COIL_HOT : UV_RF_TRANSFORMER)
                : (hot ? UV_EMBER_BED_HOT : UV_EMBER_BED_COLD);
        emitBox(pose, matrix, vc, 2.8f, 3.5f, 2.5f, 13.2f, 5.2f, 13.2f, bedUV, coilLight, packedOverlay, false, true);

        // Active Flame Core or Electric Arc Glow: Y = 5.2 .. 8.8
        if (isActive) {
            int[] coreUV = isElectric ? UV_RF_COIL_HOT : UV_FLAME_CORE;
            emitBox(pose, matrix, vc, 4.8f, 5.2f, 5.0f, 11.2f, 8.8f, 11.0f, coreUV, fullBright, packedOverlay, false, true);
        }

        // 5-Turn Serpentine Crude-Oil Heating Coil inside Chamber (Y = 9.5 .. 18.5)
        // Each horizontal coil loop is 1.2 units tall with 0.8 unit vertical risers between loops (strictly disjoint Y!)
        float[] coilYs = {9.5f, 11.5f, 13.5f, 15.5f, 17.5f};
        for (int i = 0; i < coilYs.length; i++) {
            float cy0 = coilYs[i];
            float cy1 = cy0 + 1.2f;
            emitBox(pose, matrix, vc, 4.0f, cy0, 4.2f, 12.0f, cy1, 11.8f, coilUV, coilLight, packedOverlay, true, true);
            // Vertical riser connecting this loop to the next loop
            if (i < coilYs.length - 1) {
                float rx0 = (i % 2 == 0) ? 10.5f : 4.2f;
                emitBox(pose, matrix, vc, rx0, cy1, 7.0f, rx0 + 1.3f, coilYs[i + 1], 8.5f, coilUV, coilLight, packedOverlay, false, false);
            }
        }

        // Heated Crude Pipe exiting right side (+X) toward the Refinery Tower:
        // Riser drop from bottom coil (Y = 6.5 .. 9.5) and horizontal outlet nozzle (X = 15.0 .. 16.0, Y = 6.5 .. 9.0)
        emitBox(pose, matrix, vc, 11.8f, 6.5f, 6.8f, 13.2f, 9.5f, 9.2f, coilUV, coilLight, packedOverlay, true, false);
        emitBox(pose, matrix, vc, 15.0f, 6.5f, 6.5f, 16.0f, 9.5f, 9.5f, coilUV, coilLight, packedOverlay, true, true);

        // If Electric Furnace, add Rear High-Voltage Transformer Pack (+Z: Z = 15.0 .. 16.0, Y = 5.0 .. 18.0)
        if (isElectric) {
            emitBox(pose, matrix, vc, 3.5f, 5.0f, 15.0f, 12.5f, 18.0f, 16.0f, UV_RF_TRANSFORMER, packedLight, packedOverlay, true, true);
        }

        // =========================================================================
        // 4. STEPPED PITCHED ROOF HOOD & EXHAUST SMOKESTACK (Y = 22.0 .. 32.0)
        // =========================================================================
        // Roof Step 1: Y = 22.0 .. 23.8 (X: 0.8..15.2, Z: 0.8..15.2)
        emitBox(pose, matrix, vc, 0.8f, 22.0f, 0.8f, 15.2f, 23.8f, 15.2f, casingUV, packedLight, packedOverlay, true, true);
        // Roof Step 2: Y = 23.8 .. 25.2 (X: 2.5..13.5, Z: 2.5..13.5)
        emitBox(pose, matrix, vc, 2.5f, 23.8f, 2.5f, 13.5f, 25.2f, 13.5f, casingUV, packedLight, packedOverlay, false, true);
        // Roof Step 3 (Breech Collar): Y = 25.2 .. 26.2 (X: 4.5..11.5, Z: 4.5..11.5)
        emitBox(pose, matrix, vc, 4.5f, 25.2f, 4.5f, 11.5f, 26.2f, 11.5f, UV_DARK_TRIM, packedLight, packedOverlay, false, true);

        // Cylindrical Stainless Exhaust Smokestack: Y = 26.2 .. 31.2 (center 8, 8, radius 2.6)
        emitCylinder(pose, matrix, vc, 8.0f, 8.0f, 26.2f, 31.2f, 2.6f, 2.6f, UV_STAINLESS_STACK, packedLight, packedOverlay, false, false);
        // Smokestack Top Crown Ring: Y = 31.2 .. 32.0 (radius 3.0)
        emitCylinder(pose, matrix, vc, 8.0f, 8.0f, 31.2f, 32.0f, 3.0f, 3.0f, UV_DARK_TRIM, packedLight, packedOverlay, true, true);
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

        for (int i = 0; i < STACK_SIDES; i++) {
            float a0 = (float) (i * 2.0 * Math.PI / STACK_SIDES);
            float a1 = (float) ((i + 1) * 2.0 * Math.PI / STACK_SIDES);
            float cos0 = Mth.cos(a0), sin0 = Mth.sin(a0);
            float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);
            float nx = Mth.cos((a0 + a1) * 0.5f);
            float nz = Mth.sin((a0 + a1) * 0.5f);

            float x00 = cx + cos0 * r0, z00 = cz + sin0 * r0;
            float x10 = cx + cos1 * r0, z10 = cz + sin1 * r0;
            float x01 = cx + cos0 * r1, z01 = cz + sin0 * r1;
            float x11 = cx + cos1 * r1, z11 = cz + sin1 * r1;

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
