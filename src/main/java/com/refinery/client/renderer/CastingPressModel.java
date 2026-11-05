package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import org.joml.Matrix4f;

/**
 * High-detail 3x3x3 monolithic model for the Heavy Hydraulic Continuous Casting & Quenching Press.
 *
 * Coordinate space matches the 3x3x3 multiblock relative to the controller at (0, 0, 0):
 *   X: -16 .. 32  (Left = -16..0, Center = 0..16, Right = 16..32; Center X = 8.0)
 *   Y:   0 .. 48  (Bottom = 0..16, Middle = 16..32, Top = 32..48)
 *   Z:   0 .. 48  (Front = 0..16, Middle = 16..32, Back = 32..48; Center Z = 24.0)
 *
 * Key functional ports:
 *   - Controller:          (0, 0, 0) -> X:  0..16, Y:  0..16, Z:  0..16
 *   - Discharge Output:    (1, 0, 0) -> X: 16..32, Y:  0..16, Z:  0..16 (Run-out roller table)
 *   - Casting Mold Basin:  (0, 0, 1) -> X:  0..16, Y:  0..16, Z: 16..32
 *   - Safety Grille Window:(0, 1, 0) -> X:  0..16, Y: 16..32, Z:  0..16
 *   - Molten Inflow Port:  (0, 1, 2) -> X:  0..16, Y: 16..32, Z: 32..48 (plus Y=0 launder receivers)
 */
public class CastingPressModel {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/entity/casting_press.png");
    private static final int FULL_BRIGHT = 15728880;

    // 256x256 Atlas UV Regions: {u0, v0, u1, v1} in 0..256 pixel space
    private static final float[] UV_BODY        = {  0,   0,  64,  64};
    private static final float[] UV_FRAME       = { 64,   0, 128,  64};
    private static final float[] UV_COPPER_MOLD = {128,   0, 192,  64};
    private static final float[] UV_CHROME      = {192,   0, 256,  64};

    private static final float[] UV_HYDRAULIC   = {  0,  64,  64, 128};
    private static final float[] UV_REFRACTORY  = { 64,  64, 128, 128};
    private static final float[] UV_GRILLE      = {128,  64, 192, 128};
    private static final float[] UV_HAZARD      = {192,  64, 256, 128};

    private static final float[] UV_CONSOLE     = {  0, 128,  64, 192};
    private static final float[] UV_MOLTEN      = { 64, 128, 128, 192};
    private static final float[] UV_COOLING     = {128, 128, 192, 192};
    private static final float[] UV_ROLLERS     = {192, 128, 256, 192};

    private static final float[] UV_BASE        = {  0, 192,  64, 256};
    private static final float[] UV_LOUVERS     = { 64, 192, 128, 256};
    private static final float[] UV_STATUS_ON   = {128, 192, 160, 224};
    private static final float[] UV_STATUS_OFF  = {160, 192, 192, 224};
    private static final float[] UV_BILLET_HOT  = {192, 192, 256, 256};

    private static class BoxBuilder {
        final VertexConsumer vc;
        final Matrix4f mat;
        final int light;
        final int overlay;

        BoxBuilder(VertexConsumer vc, Matrix4f mat, int light, int overlay) {
            this.vc = vc;
            this.mat = mat;
            this.light = light;
            this.overlay = overlay;
        }

        void box(float x0, float y0, float z0, float x1, float y1, float z1, float[] uv) {
            boxColored(x0, y0, z0, x1, y1, z1, uv, 1f, 1f, 1f, 1f, this.light);
        }

        void boxEmissive(float x0, float y0, float z0, float x1, float y1, float z1, float[] uv,
                         float r, float g, float b, float a) {
            boxColored(x0, y0, z0, x1, y1, z1, uv, r, g, b, a, FULL_BRIGHT);
        }

        void boxColored(float x0, float y0, float z0, float x1, float y1, float z1, float[] uv,
                        float r, float g, float b, float a, int packedLight) {
            float X0 = x0 / 16f, Y0 = y0 / 16f, Z0 = z0 / 16f;
            float X1 = x1 / 16f, Y1 = y1 / 16f, Z1 = z1 / 16f;

            float u0 = uv[0] / 256f;
            float v0 = uv[1] / 256f;
            float u1 = uv[2] / 256f;
            float v1 = uv[3] / 256f;

            // North (-Z)
            quad(X1, Y1, Z0, X0, Y1, Z0, X0, Y0, Z0, X1, Y0, Z0, u0, v0, u1, v1, 0, 0, -1, r, g, b, a, packedLight);
            // South (+Z)
            quad(X0, Y1, Z1, X1, Y1, Z1, X1, Y0, Z1, X0, Y0, Z1, u0, v0, u1, v1, 0, 0, 1, r, g, b, a, packedLight);
            // West (-X)
            quad(X0, Y1, Z0, X0, Y1, Z1, X0, Y0, Z1, X0, Y0, Z0, u0, v0, u1, v1, -1, 0, 0, r, g, b, a, packedLight);
            // East (+X)
            quad(X1, Y1, Z1, X1, Y1, Z0, X1, Y0, Z0, X1, Y0, Z1, u0, v0, u1, v1, 1, 0, 0, r, g, b, a, packedLight);
            // Up (+Y)
            quad(X0, Y1, Z0, X1, Y1, Z0, X1, Y1, Z1, X0, Y1, Z1, u0, v0, u1, v1, 0, 1, 0, r, g, b, a, packedLight);
            // Down (-Y)
            quad(X0, Y0, Z1, X1, Y0, Z1, X1, Y0, Z0, X0, Y0, Z0, u0, v0, u1, v1, 0, -1, 0, r, g, b, a, packedLight);
        }

        void quad(float x0, float y0, float z0,
                  float x1, float y1, float z1,
                  float x2, float y2, float z2,
                  float x3, float y3, float z3,
                  float u0, float v0, float u1, float v1,
                  float nx, float ny, float nz,
                  float r, float g, float b, float a, int packedLight) {
            vc.addVertex(mat, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(packedLight).setNormal(nx, ny, nz);
            vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(packedLight).setNormal(nx, ny, nz);
            vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(packedLight).setNormal(nx, ny, nz);
            vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(packedLight).setNormal(nx, ny, nz);
        }
    }

    public static void render(PoseStack poseStack, SubmitNodeCollector collector, int light,
                              boolean isActive, int progress, long gameTime, float partialTicks,
                              int moltenAmount, Fluid moltenFluid) {
        RenderType renderType = RenderTypes.entitySolid(TEXTURE);
        float time = (gameTime + partialTicks);
        float[] fluidRgb = ArcMelterModel.getMoltenFluidColor(moltenFluid, isActive);

        // Compute smooth hydraulic press ram stroke (0.0 = top retracted, 1.0 = bottom stamping hold)
        // Casting Press maxProgress = 240 ticks:
        //   0..90:   Downward stamping stroke
        //   90..150: Bottom high-pressure dwell & water-quench solidification
        //   150..240:Smooth upward hydraulic retraction
        float strokeRatio = 0.0f;
        if (isActive || progress > 0) {
            float p = Mth.clamp(progress + (isActive ? partialTicks : 0f), 0f, 240f);
            if (p < 90f) {
                float t = p / 90f;
                strokeRatio = t * t * (3f - 2f * t); // smoothstep approach
            } else if (p < 150f) {
                strokeRatio = 1.0f + 0.02f * Mth.sin(time * 0.8f); // high-pressure dwell throb
            } else {
                float t = (p - 150f) / 90f;
                strokeRatio = 1.0f - (t * t * (3f - 2f * t)); // smooth retraction
            }
        }

        final float ramStroke = strokeRatio;

        collector.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            Matrix4f mat = pose.pose();
            BoxBuilder b = new BoxBuilder(vc, mat, light, OverlayTexture.NO_OVERLAY);

            // =========================================================================
            // 1. HEAVY FOUNDATION BEDPLATE, ANCHOR PADS & SAFETY CURBING (Y: 0..6)
            // =========================================================================
            b.box(-15, 0, 1, 31, 3, 47, UV_BASE);
            // Yellow/black safety toe-kick curbing along front & right discharge table
            b.box(-15.2f, 0, 0.8f, 31.2f, 2.2f, 2.4f, UV_HAZARD);
            b.box(29.6f, 0, 2.4f, 31.2f, 2.2f, 47.2f, UV_HAZARD);
            // Press anvil bolster bed under center bay
            b.box(-6, 3, 9, 22, 7, 39, UV_FRAME);

            // =========================================================================
            // 2. WATER-COOLED COPPER & REFRACTORY CRYSTALLIZER MOLD BASIN AT (0, 0, 1)
            //    (X: -2..18, Y: 7..15, Z: 14..34)
            // =========================================================================
            // Outer copper water-jacketed crystallizer housing
            b.box(-2, 7, 14, 18, 14.5f, 17, UV_COPPER_MOLD);  // North mold jacket
            b.box(-2, 7, 31, 18, 14.5f, 34, UV_COPPER_MOLD);  // South mold jacket
            b.box(-2, 7, 17, 1, 14.5f, 31, UV_COPPER_MOLD);   // West mold jacket
            b.box(15, 7, 17, 18, 14.5f, 31, UV_COPPER_MOLD);  // East mold jacket (with billet discharge gate)
            // Inner refractory mold liner & bottom anvil plate (cavity X: 1.5..14.5, Z: 17.5..30.5)
            b.box(1, 7, 17, 15, 9.2f, 31, UV_REFRACTORY);
            // Heavy steel clamping lugs around the mold basin
            b.box(-3, 7, 13, 0, 13, 16, UV_FRAME);
            b.box(16, 7, 13, 19, 13, 16, UV_FRAME);
            b.box(-3, 7, 32, 0, 13, 35, UV_FRAME);
            b.box(16, 7, 32, 19, 13, 35, UV_FRAME);

            // =========================================================================
            // 3. FOUR CHROME TIE-ROD COLUMNS, OVERHEAD CROWN PLATEN & MAIN HYDRAULIC CYLINDER
            // =========================================================================
            float[][] columns = {
                    { -4.5f, 10.5f }, // Front-Left column
                    { 17.5f, 10.5f }, // Front-Right column
                    { -4.5f, 34.5f }, // Rear-Left column
                    { 17.5f, 34.5f }  // Rear-Right column
            };
            for (float[] col : columns) {
                float cx = col[0];
                float cz = col[1];
                // Lower column nut & base boss
                b.box(cx - 0.6f, 7, cz - 0.6f, cx + 3.6f, 10, cz + 3.6f, UV_FRAME);
                // Precision-ground chrome tie-rod shaft (Y: 10..38)
                b.box(cx, 10, cz, cx + 3.0f, 38, cz + 3.0f, UV_CHROME);
                // Upper castle locknut above crown platen (Y: 44..46.5)
                b.box(cx - 0.5f, 44, cz - 0.5f, cx + 3.5f, 46.5f, cz + 3.5f, UV_FRAME);
            }

            // Overhead Cast-Steel Crown Platen (X: -7..23, Y: 38..44, Z: 9..39)
            b.box(-7, 38, 9, 23, 44, 39, UV_BODY);
            b.box(-7.5f, 38, 8.5f, 23.5f, 40, 39.5f, UV_FRAME);
            b.box(-7.5f, 42.5f, 8.5f, 23.5f, 44, 39.5f, UV_FRAME);

            // Main High-Tonnage Hydraulic Press Cylinder on top of Crown (X: 2..14, Y: 44..48, Z: 18..30)
            b.box(2, 44, 18, 14, 48, 30, UV_HYDRAULIC);
            b.box(1.5f, 47, 17.5f, 14.5f, 48, 30.5f, UV_FRAME);
            // Twin side hydraulic return booster cylinders
            b.box(-3, 44, 21, 0, 47.5f, 27, UV_HYDRAULIC);
            b.box(16, 44, 21, 19, 47.5f, 27, UV_HYDRAULIC);

            // =========================================================================
            // 4. FRONT SAFETY GRILLE WINDOW AT (0, 1, 0) (X: -1..17, Y: 14..36, Z: 6..10)
            // =========================================================================
            // Framed safety guard assembly on the front face
            b.box(-1, 14, 6, 17, 16, 9, UV_FRAME);
            b.box(-1, 34, 6, 17, 36, 9, UV_FRAME);
            b.box(-1, 16, 6, 1.5f, 34, 9, UV_FRAME);
            b.box(14.5f, 16, 6, 17, 34, 9, UV_FRAME);
            // Safety hazard stripe lintel above viewing window
            b.box(-1.2f, 34.2f, 5.8f, 17.2f, 35.8f, 6.2f, UV_HAZARD);
            // Perforated wire-mesh safety observation glazing
            b.box(1.5f, 16, 6.8f, 14.5f, 34, 7.8f, UV_GRILLE);

            // =========================================================================
            // 5. MOLTEN INFLOW TUNDISH AT (0, 1, 2) & LOWER CHANNEL RECEIVERS AT Y=0
            //    Supports both overhead/pipe feed at (0, 1, 2) AND direct Y=0 CastingChannel feed!
            // =========================================================================
            // Upper Refractory Tundish & Sloped Launder Chute from Rear Port (0, 1, 2) (Z: 32..48)
            b.box(2, 16, 33, 14, 21, 48, UV_REFRACTORY);
            b.box(1.5f, 19, 33, 3.5f, 25, 48, UV_FRAME);
            b.box(12.5f, 19, 33, 14.5f, 25, 48, UV_FRAME);
            // Rear input port flange collar at Z=46.5..48, Y=16..26
            b.box(1, 15.5f, 46.5f, 15, 26, 48, UV_HAZARD);

            // Lower Level (Y: 3..9) Refractory Channel Receiver Troughs:
            //   1) Rear bottom receiver at (0, 0, 2) (X: 3..13, Y: 3..9, Z: 33..48)
            b.box(3, 3, 33, 13, 5.2f, 48, UV_REFRACTORY);
            b.box(2.5f, 4, 33, 4.5f, 9.2f, 48, UV_FRAME);
            b.box(11.5f, 4, 33, 13.5f, 9.2f, 48, UV_FRAME);
            b.box(2, 3, 46.5f, 14, 9.8f, 48, UV_HAZARD);

            //   2) Left-side bottom receiver at (-1, 0, 1) (X: -16..-1, Y: 3..9, Z: 19..29)
            //      Allows side-by-side placement next to Arc Melter's (1, 0, 0) output via CastingChannels!
            b.box(-16, 3, 19, -1, 5.2f, 29, UV_REFRACTORY);
            b.box(-16, 4, 18.5f, -1, 9.2f, 20.5f, UV_FRAME);
            b.box(-16, 4, 27.5f, -1, 9.2f, 29.5f, UV_FRAME);
            b.box(-16, 3, 18, -14.5f, 9.8f, 30, UV_HAZARD);

            // Animated Molten Metal Inflow Streams when active or holding molten metal
            if (isActive || moltenAmount > 0) {
                float flowPulse = 0.90f + 0.10f * Mth.sin(time * 0.40f);
                float r = fluidRgb[0] * flowPulse;
                float g = fluidRgb[1] * flowPulse;
                float bl = fluidRgb[2] * flowPulse;

                // Stream in upper tundish (0, 1, 2) cascading into mold basin
                b.boxEmissive(3.5f, 21.0f, 33.5f, 12.5f, 23.2f, 47.8f, UV_MOLTEN, r, g, bl, 1.0f);
                b.boxEmissive(5.0f, 11.0f, 30.2f, 11.0f, 22.5f, 33.8f, UV_MOLTEN, r, g, bl, 1.0f);

                // Stream in lower rear & left channel receivers (Y: 5.2..7.6) matching CastingChannelBlock!
                b.boxEmissive(4.5f, 5.2f, 33.0f, 11.5f, 7.6f, 47.9f, UV_MOLTEN, r, g, bl, 1.0f);
                b.boxEmissive(-15.9f, 5.2f, 20.5f, -1.0f, 7.6f, 27.5f, UV_MOLTEN, r, g, bl, 1.0f);
            }

            // =========================================================================
            // 6. RIGHT-SIDE WATER QUENCH MANIFOLD & MOTORIZED RUN-OUT ROLLER TABLE AT (1, 0, 0)
            // =========================================================================
            // Water-cooling manifold cabinet at right-rear (X: 20..31, Y: 14..36, Z: 18..44)
            b.box(20, 14, 18, 31, 36, 44, UV_BODY);
            b.box(29.8f, 16, 20, 31.2f, 34, 42, UV_LOUVERS);
            // Blue supply & red return cooling water manifold panel facing front/side
            b.box(21, 16, 17.5f, 30, 34, 18.2f, UV_COOLING);

            // Motorized Ingot Run-Out Roller Table extending from Mold Basin through Output Port (1, 0, 0)
            // Spans X: 18..31, Z: 0..32, Y: 3..11 so cast billets roll forward to Z=0 and East to X=32!
            // Roller table side rails
            b.box(18.5f, 3, 0, 20.5f, 10.5f, 32, UV_FRAME);
            b.box(28.5f, 3, 0, 30.5f, 10.5f, 32, UV_FRAME);
            // Roller table bed & motorized steel rollers
            b.box(20.5f, 3, 0, 28.5f, 6.5f, 32, UV_BASE);
            b.box(20.5f, 6.5f, 0.5f, 28.5f, 9.2f, 31.5f, UV_ROLLERS);
            // Output discharge arch at Front-Right (1, 0, 0) (Z=0..2)
            b.box(18, 3, 0, 31, 13, 2, UV_HAZARD);

            // =========================================================================
            // 7. FRONT-LEFT PLC OPERATOR CONTROL CABINET AT (-1, 0, 0) (X: -15..-4, Y: 3..28, Z: 2..15)
            // =========================================================================
            b.box(-15, 3, 2, -4, 27, 15, UV_BODY);
            b.box(-15.4f, 25, 1.6f, -3.6f, 27.5f, 15.4f, UV_FRAME);
            // Louvered hydraulic pump bay on West side
            b.box(-15.3f, 5, 4, -14.7f, 23, 13, UV_LOUVERS);
            // Front hydraulic tonnage gauge, PLC screen & E-stop faceplate
            b.box(-14.2f, 12, 1.7f, -4.8f, 24.5f, 2.2f, UV_CONSOLE);
            // Status beacon on top of cabinet
            b.box(-10.5f, 27.5f, 7, -8.5f, 29.5f, 9, UV_CHROME);
            if (isActive) {
                b.boxEmissive(-10.8f, 29.5f, 6.7f, -8.2f, 32.3f, 9.3f, UV_STATUS_ON, 0.3f, 1.0f, 0.5f, 1.0f);
            } else {
                b.box(-10.6f, 29.5f, 6.9f, -8.4f, 32.0f, 9.1f, UV_STATUS_OFF);
            }

            // =========================================================================
            // 8. DYNAMIC MOLTEN POOL / SOLIDIFYING BILLET IN MOLD BASIN
            // =========================================================================
            float fillRatio = Mth.clamp(moltenAmount / 16000.0f, 0.0f, 1.0f);
            if (isActive && fillRatio < 0.35f) {
                fillRatio = 0.35f;
            }
            float poolTopY = 9.3f + fillRatio * 4.5f;

            if (isActive && progress >= 95) {
                // During quench & retraction (ticks 95..240), render the newly formed incandescent-to-metallic billet!
                float coolFactor = Mth.clamp((progress - 95f) / 145f, 0f, 1f);
                float r = Mth.lerp(coolFactor, fluidRgb[0], 0.90f);
                float g = Mth.lerp(coolFactor, fluidRgb[1], 0.92f);
                float bl = Mth.lerp(coolFactor, fluidRgb[2], 0.96f);

                // Solidified billet inside mold cavity
                b.boxEmissive(2.2f, 9.2f, 18.2f, 13.8f, 13.2f, 29.8f, UV_BILLET_HOT, r, g, bl, 1.0f);

                // During retraction (ticks 150..240), slide a freshly ejected billet along the right-hand roller table (1, 0, 0)!
                if (progress >= 145) {
                    float rollT = Mth.clamp((progress - 145f) / 95f, 0f, 1f);
                    float billetZ = Mth.lerp(rollT, 20.0f, 3.0f);
                    b.boxEmissive(21.5f, 9.3f, billetZ, 27.5f, 12.0f, billetZ + 7.0f, UV_BILLET_HOT, r, g, bl, 1.0f);
                }
            } else if (isActive || moltenAmount > 0) {
                // Liquid molten metal pool glowing inside the copper crystallizer mold
                float ripple = isActive ? (0.92f + 0.08f * Mth.sin(time * 0.5f)) : 0.88f;
                b.boxEmissive(1.6f, 9.2f, 17.6f, 14.4f, poolTopY, 30.4f, UV_MOLTEN,
                        fluidRgb[0] * ripple, fluidRgb[1] * ripple, fluidRgb[2] * ripple, 1.0f);
            }

            // =========================================================================
            // 9. ANIMATED RECIPROCATING HYDRAULIC RAM CROSSHEAD & UPPER STAMPING DIE
            // =========================================================================
            // At strokeRatio = 0.0 (retracted): crosshead sits at Y: 28..33, upper die at Y: 23..28
            // At strokeRatio = 1.0 (stamping):  crosshead sits at Y: 18..23, upper die at Y: 13..18 (pressing into mold!)
            float travel = ramStroke * 10.0f;
            float crossBotY = 28.0f - travel;
            float crossTopY = 33.0f - travel;
            float dieBotY   = 23.0f - travel;
            float dieTopY   = 28.0f - travel;

            // Main central chrome hydraulic piston rod extending from crown (Y=38) down to crosshead (crossTopY)
            b.box(5.0f, crossTopY, 21.0f, 11.0f, 38.5f, 27.0f, UV_CHROME);
            // Twin side return piston rods
            b.box(-2.2f, crossTopY, 22.8f, -0.8f, 38.5f, 25.2f, UV_CHROME);
            b.box(16.8f, crossTopY, 22.8f, 18.2f, 38.5f, 25.2f, UV_CHROME);

            // Moving Steel Crosshead Platen (X: -5.5..21.5, Y: crossBotY..crossTopY, Z: 9.5..38.5)
            b.box(-5.5f, crossBotY, 9.5f, 21.5f, crossTopY, 38.5f, UV_FRAME);
            // High-visibility diagonal yellow/black hazard warning band across the moving crosshead front & back
            b.box(-5.7f, crossBotY + 0.8f, 9.3f, 21.7f, crossTopY - 0.8f, 10.2f, UV_HAZARD);
            b.box(-5.7f, crossBotY + 0.8f, 37.8f, 21.7f, crossTopY - 0.8f, 38.7f, UV_HAZARD);

            // 4 Sliding Guide Bushings wrapping the 4 chrome tie-rod columns
            for (float[] col : columns) {
                float cx = col[0];
                float cz = col[1];
                b.box(cx - 0.8f, crossBotY - 1.0f, cz - 0.8f, cx + 3.8f, crossTopY + 1.0f, cz + 3.8f, UV_COPPER_MOLD);
            }

            // Upper Water-Cooled Copper & Tool-Steel Stamping Die Block under the Crosshead
            b.box(0.5f, dieBotY + 2.0f, 16.5f, 15.5f, dieTopY, 31.5f, UV_FRAME);
            b.box(1.8f, dieBotY, 17.8f, 14.2f, dieBotY + 2.0f, 30.2f, UV_COPPER_MOLD);
        });
    }
}
