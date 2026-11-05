package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix4f;

/**
 * High-detail 3x3x3 monolithic model for the Industrial Electric Arc Melter (EAF).
 *
 * Coordinate space matches the 3x3x3 multiblock relative to the controller at (0, 0, 0):
 *   X: -16 .. 32  (Left = -16..0, Center = 0..16, Right = 16..32; Center X = 8.0)
 *   Y:   0 .. 48  (Bottom = 0..16, Middle = 16..32, Top = 32..48)
 *   Z:   0 .. 48  (Front = 0..16, Middle = 16..32, Back = 32..48; Center Z = 24.0)
 *
 * Key functional ports:
 *   - Controller:        (0, 0, 0) -> X:  0..16, Y:  0..16, Z:  0..16
 *   - Molten Output Port:(1, 0, 0) -> X: 16..32, Y:  0..16, Z:  0..16 (Launder spout at Y: 4..10)
 *   - Quartz Viewport:   (0, 1, 0) -> X:  0..16, Y: 16..32, Z:  0..16
 *   - Crucible Chamber:  (0, 1, 1) -> X:  0..16, Y: 16..32, Z: 16..32
 *   - Feed Input Port:   (0, 2, 1) -> X:  0..16, Y: 32..48, Z: 16..32
 */
public class ArcMelterModel {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/entity/arc_melter.png");
    private static final int FULL_BRIGHT = 15728880;

    // 256x256 Atlas UV Regions: {u0, v0, u1, v1} in 0..256 pixel space
    private static final float[] UV_SHELL        = {  0,   0,  64,  64};
    private static final float[] UV_FRAME        = { 64,   0, 128,  64};
    private static final float[] UV_REFRACTORY   = {128,   0, 192,  64};
    private static final float[] UV_COPPER_BUS   = {192,   0, 256,  64};

    private static final float[] UV_GRAPHITE     = {  0,  64,  64, 128};
    private static final float[] UV_GRAPHITE_HOT = { 64,  64, 128, 128};
    private static final float[] UV_VIEWPORT     = {128,  64, 192, 128};
    private static final float[] UV_HAZARD       = {192,  64, 256, 128};

    private static final float[] UV_CONSOLE      = {  0, 128,  64, 192};
    private static final float[] UV_MOLTEN       = { 64, 128, 128, 192};
    private static final float[] UV_ARC_PLASMA   = {128, 128, 192, 192};
    private static final float[] UV_HOPPER       = {192, 128, 256, 192};

    private static final float[] UV_BASE         = {  0, 192,  64, 256};
    private static final float[] UV_RADIATOR     = { 64, 192, 128, 256};
    private static final float[] UV_STATUS_ON    = {128, 192, 160, 224};
    private static final float[] UV_STATUS_OFF   = {160, 192, 192, 224};
    private static final float[] UV_CHROME       = {192, 192, 256, 256};

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

        // Compute RGB color tint for the current molten fluid inside the crucible & launder
        float[] fluidRgb = getMoltenFluidColor(moltenFluid, isActive);

        collector.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            Matrix4f mat = pose.pose();
            BoxBuilder b = new BoxBuilder(vc, mat, light, OverlayTexture.NO_OVERLAY);

            // =========================================================================
            // 1. HEAVY FOUNDATION SKID, TRUNNION ROCKER RAILS & TOE-KICK CURBING (Y: 0..6)
            // =========================================================================
            // Main reinforced steel baseplate spanning the 3x3 footprint
            b.box(-15, 0, 1, 31, 3, 47, UV_BASE);
            // Hazard yellow/black safety toe-kick borders at front & right tapping aisle
            b.box(-15.2f, 0, 0.8f, 16, 2.5f, 2.5f, UV_HAZARD);
            b.box(16, 0, 0.8f, 31.2f, 2.2f, 2.2f, UV_HAZARD);
            b.box(29.5f, 0, 2.2f, 31.2f, 2.2f, 47.2f, UV_HAZARD);
            // Heavy corner anchor bolting plinths
            b.box(-15, 3, 2, -9, 6, 8, UV_FRAME);
            b.box(-15, 3, 40, -9, 6, 46, UV_FRAME);
            b.box(25, 3, 24, 31, 6, 30, UV_FRAME);
            b.box(25, 3, 40, 31, 6, 46, UV_FRAME);

            // Curved EAF rocker trunnion cradles under the furnace vessel
            b.box(-7, 3, 11, -3, 8, 37, UV_FRAME);
            b.box(19, 3, 11, 23, 8, 37, UV_FRAME);
            // Twin hydraulic tilt cylinders at front of trunnions
            b.box(-6.5f, 3, 7, -3.5f, 11, 10, UV_SHELL);
            b.box(-5.5f, 11, 7.8f, -4.5f, 15, 9.2f, UV_CHROME);
            b.box(19.5f, 3, 11, 22.5f, 11, 14, UV_SHELL);
            b.box(20.5f, 11, 11.8f, 21.5f, 15, 13.2f, UV_CHROME);

            // =========================================================================
            // 2. CYLINDRICAL WATER-COOLED RIBBED EAF SHELL & REFRACTORY CRUCIBLE (Y: 6..31)
            //    Centered at X = 8.0, Z = 24.0
            // =========================================================================
            // Dish bottom hearth plate
            b.box(-4, 5, 12, 20, 8, 36, UV_SHELL);

            // Outer cylindrical/octagonal water-cooled steel shell walls (hollow interior for molten bath)
            // West wall (-X)
            b.box(-6, 8, 12, -2, 30, 36, UV_SHELL);
            // East wall (+X) - has EBT tapping opening at lower front-right
            b.box(18, 13, 12, 22, 30, 36, UV_SHELL);
            b.box(18, 8, 22, 22, 13, 36, UV_SHELL);
            // North wall (-Z) - has slag/observation door opening in center
            b.box(-4, 8, 9, 20, 14, 13, UV_SHELL);
            b.box(-4, 25, 9, 20, 30, 13, UV_SHELL);
            b.box(-4, 14, 9, 2, 25, 13, UV_SHELL);
            b.box(14, 14, 9, 20, 25, 13, UV_SHELL);
            // South wall (+Z)
            b.box(-4, 8, 35, 20, 30, 39, UV_SHELL);

            // Diagonal corner chamfer columns for octagonal cylindrical profile
            b.box(-5, 8, 10, -2, 30, 13, UV_FRAME);
            b.box(18, 8, 10, 21, 30, 13, UV_FRAME);
            b.box(-5, 8, 35, -2, 30, 38, UV_FRAME);
            b.box(18, 8, 35, 21, 30, 38, UV_FRAME);

            // Heavy lower & upper water-cooling flange rings around shell
            b.box(-7, 8, 8, 23, 10.5f, 40, UV_FRAME);
            b.box(-7, 27.5f, 8, 23, 30.5f, 40, UV_FRAME);

            // Vertical structural buckstay stiffener ribs around shell perimeter
            b.box(-7.2f, 8, 16, -5.5f, 30, 18.5f, UV_FRAME);
            b.box(-7.2f, 8, 29.5f, -5.5f, 30, 32, UV_FRAME);
            b.box(21.5f, 8, 16, 23.2f, 30, 18.5f, UV_FRAME);
            b.box(21.5f, 8, 29.5f, 23.2f, 30, 32, UV_FRAME);
            b.box(0, 8, 38.5f, 2.5f, 30, 40.2f, UV_FRAME);
            b.box(13.5f, 8, 38.5f, 16, 30, 40.2f, UV_FRAME);

            // Cooling water header pipes wrapping the outer shell
            b.box(-6.8f, 18, 8.5f, 22.8f, 19.5f, 10f, UV_COPPER_BUS);
            b.box(-6.8f, 18, 38f, 22.8f, 19.5f, 39.5f, UV_COPPER_BUS);
            b.box(-6.8f, 18, 9f, -5.5f, 19.5f, 39f, UV_COPPER_BUS);
            b.box(21.5f, 18, 9f, 22.8f, 19.5f, 39f, UV_COPPER_BUS);

            // Interior Magnesia-Carbon Refractory Brick Crucible Lining
            // Crucible hearth floor
            b.box(-2, 8, 13, 18, 11, 35, UV_REFRACTORY);
            // Crucible inner walls (defining molten bath cavity X: -0.5..16.5, Z: 14.5..33.5)
            b.box(-2, 11, 13, 0, 29, 35, UV_REFRACTORY);     // Inner West
            b.box(16, 11, 13, 18, 29, 35, UV_REFRACTORY);    // Inner East
            b.box(0, 11, 13, 16, 29, 15, UV_REFRACTORY);     // Inner North
            b.box(0, 11, 33, 16, 29, 35, UV_REFRACTORY);     // Inner South

            // =========================================================================
            // 3. FRONT ARMORED QUARTZ SLAG & VIEWPORT DOOR AT (0, 1, 0) (X: 1..15, Y: 13..26, Z: 4..9)
            // =========================================================================
            // Heavy water-cooled cast steel door frame protruding toward front face
            b.box(1, 13, 5, 15, 15, 9, UV_FRAME);
            b.box(1, 24, 5, 15, 26, 9, UV_FRAME);
            b.box(1, 15, 5, 3.5f, 24, 9, UV_FRAME);
            b.box(12.5f, 15, 5, 15, 24, 9, UV_FRAME);
            // Door lifting guide chains / rods
            b.box(2, 26, 6, 3, 31, 7, UV_CHROME);
            b.box(13, 26, 6, 14, 31, 7, UV_CHROME);
            // Armored quartz viewing window pane
            b.box(3.5f, 15, 5.5f, 12.5f, 24, 7.2f, UV_VIEWPORT);
            if (isActive) {
                // Intense pulsing arc flash visible behind the quartz viewport
                float pulse = 0.82f + 0.18f * Mth.sin(time * 0.65f);
                b.boxEmissive(3.8f, 15.3f, 7.2f, 12.2f, 23.7f, 8.8f, UV_MOLTEN,
                        fluidRgb[0] * pulse, fluidRgb[1] * pulse, fluidRgb[2] * pulse, 1.0f);
            } else if (moltenAmount > 0) {
                b.boxEmissive(3.8f, 15.3f, 7.2f, 12.2f, 19.5f, 8.8f, UV_MOLTEN,
                        fluidRgb[0] * 0.8f, fluidRgb[1] * 0.8f, fluidRgb[2] * 0.8f, 1.0f);
            }

            // =========================================================================
            // 4. REFRACTORY TAPPING LAUNDER SPOUT AT OUTPUT PORT (1, 0, 0)
            //    Connects furnace tap-hole (X=18..22, Z=14..22) into (1, 0, 0) (X=16..32, Y=4..10, Z=0..16)
            //    Aligns directly with CastingChannelBlock (Y: 4..8) on North (Z=0) and East (X=32)!
            // =========================================================================
            // Upper eccentric bottom-tapping (EBT) refractory chute exiting shell
            b.box(17, 7, 13, 27, 13, 22, UV_REFRACTORY);
            b.box(18, 9, 14.5f, 26, 13.2f, 20.5f, UV_BASE); // Inner chute recess

            // Main lower refractory launder trough in block (1, 0, 0) running from Z=20 to Front Z=0
            // Floor of launder (Y: 3..5)
            b.box(20, 3, 0, 28, 5.2f, 22, UV_REFRACTORY);
            // Left & right walls of launder (Y: 5..9)
            b.box(19.5f, 4, 0, 21.5f, 9.2f, 22, UV_FRAME);
            b.box(26.5f, 4, 0, 28.5f, 9.2f, 22, UV_FRAME);
            // Eastward branch spout (X: 26..32, Z: 4..12) so channels/pipes on East side of (1,0,0) also align!
            b.box(26, 3, 4, 32, 5.2f, 12, UV_REFRACTORY);
            b.box(26.5f, 4, 3.5f, 32, 9.2f, 5.5f, UV_FRAME);
            b.box(26.5f, 4, 10.5f, 32, 9.2f, 12.5f, UV_FRAME);
            // Heavy steel tapping port flange ring at front (Z=0..1.5) and east (X=30.5..32)
            b.box(19, 3, 0, 29, 10, 1.5f, UV_HAZARD);
            b.box(30.5f, 3, 3.5f, 32, 10, 12.5f, UV_HAZARD);

            // Animated Molten Metal Stream inside Tapping Spout & Launder when active or holding molten fluid
            if (isActive || moltenAmount > 0) {
                float flowPulse = 0.90f + 0.10f * Mth.sin(time * 0.35f);
                float r = fluidRgb[0] * flowPulse;
                float g = fluidRgb[1] * flowPulse;
                float bl = fluidRgb[2] * flowPulse;

                // Upper tap stream pouring from furnace hearth into launder
                b.boxEmissive(18.5f, 8.5f, 15.5f, 25.5f, 12.5f, 19.5f, UV_MOLTEN, r, g, bl, 1.0f);
                // Vertical falling cascade into lower launder
                b.boxEmissive(22.0f, 5.2f, 15.5f, 26.0f, 12.2f, 19.0f, UV_MOLTEN, r, g, bl, 1.0f);
                // Glowing molten river filling the main launder to Z=0 and East branch to X=32 (Y=5.2..7.6)
                b.boxEmissive(21.5f, 5.2f, 0.1f, 26.5f, 7.6f, 20.5f, UV_MOLTEN, r, g, bl, 1.0f);
                b.boxEmissive(26.0f, 5.2f, 5.5f, 31.9f, 7.6f, 10.5f, UV_MOLTEN, r, g, bl, 1.0f);
            }

            // =========================================================================
            // 5. REAR-LEFT HIGH-VOLTAGE EAF TRANSFORMER VAULT & BUSBAR TUBES (X: -15..-6, Z: 16..46)
            // =========================================================================
            // Main step-down transformer housing
            b.box(-15, 3, 18, -6, 28, 45, UV_SHELL);
            // Transformer top cap & high-voltage warning stripe
            b.box(-15.5f, 27, 17.5f, -5.5f, 29.5f, 45.5f, UV_FRAME);
            b.box(-15.2f, 15, 17.8f, -6.5f, 19, 18.2f, UV_HAZARD);
            // Oil-cooling radiator fin banks on West (-X) and South (+Z) faces
            b.box(-16, 5, 20, -14.8f, 26, 27, UV_RADIATOR);
            b.box(-16, 5, 29, -14.8f, 26, 36, UV_RADIATOR);
            b.box(-16, 5, 38, -14.8f, 26, 44, UV_RADIATOR);
            b.box(-14, 5, 44.8f, -7, 26, 46, UV_RADIATOR);

            // 3 High-voltage ceramic standoff bushings + heavy copper busbars rising from transformer
            for (int i = 0; i < 3; i++) {
                float bz = 22f + i * 7.5f;
                b.box(-13, 29.5f, bz, -10, 34, bz + 3f, UV_REFRACTORY);
                b.box(-12.2f, 34, bz + 0.6f, -10.8f, 41, bz + 2.4f, UV_COPPER_BUS);
                // Horizontal copper busbar bridge over to the rear electrode mast gantry
                b.box(-12.2f, 39.5f, bz + 0.6f, 2f + i * 5f, 41.2f, bz + 2.4f, UV_COPPER_BUS);
            }

            // =========================================================================
            // 6. TWIN HYDRAULIC GANTRY COLUMNS, WATER-COOLED DOMED ROOF & TOP HOPPER (Y: 30..48)
            // =========================================================================
            // Rear structural gantry columns supporting roof swing & electrode masts
            b.box(-4, 3, 39, 1, 44, 45, UV_FRAME);
            b.box(15, 3, 39, 20, 44, 45, UV_FRAME);
            // Crossbeam bridge linking the gantry columns
            b.box(-5, 41, 38, 21, 45, 46, UV_FRAME);

            // Swinging Water-Cooled Domed Furnace Roof (Y: 30.5..35)
            b.box(-5, 30.5f, 10, 21, 33.5f, 38, UV_SHELL);
            b.box(-3, 33.5f, 12, 19, 35.5f, 36, UV_FRAME);
            // Central Refractory Delta Roof Insert (Y: 35..37)
            b.box(0, 35, 16, 16, 37, 32, UV_REFRACTORY);

            // Top Raw Ore Charge Hopper at Input Port (0, 2, 1) -> (X: 1..15, Y: 36..48, Z: 17..31)
            // Upper feed collar at Y: 45..48 (flush with Y=48 top face of block (0, 2, 1) for conveyors/pipes!)
            b.box(1, 45, 17, 15, 48, 31, UV_HAZARD);
            // Hollow hopper walls (so items inside are visible from above)
            b.box(2, 37, 18, 14, 45, 20, UV_HOPPER);   // North hopper wall
            b.box(2, 37, 28, 14, 45, 30, UV_HOPPER);   // South hopper wall
            b.box(2, 37, 20, 4, 45, 28, UV_HOPPER);    // West hopper wall
            b.box(12, 37, 20, 14, 45, 28, UV_HOPPER);  // East hopper wall

            // =========================================================================
            // 7. FRONT-LEFT OPERATOR CONTROL CONSOLE AT (-1, 0, 0) (X: -14..-3, Y: 3..26, Z: 2..12)
            // =========================================================================
            // Pedestal cabinet body
            b.box(-14, 3, 3, -3, 18, 12, UV_SHELL);
            b.box(-14.5f, 18, 2.5f, -2.5f, 25, 11.5f, UV_FRAME);
            // Instrumented front faceplate (3-phase kA meters, OLED display, E-stop)
            b.box(-13.8f, 18.5f, 2.1f, -3.2f, 24.5f, 2.6f, UV_CONSOLE);
            // Status beacon light on top of console
            b.box(-9.5f, 25, 6, -7.5f, 27, 8, UV_CHROME);
            if (isActive) {
                b.boxEmissive(-9.8f, 27, 5.7f, -7.2f, 29.8f, 8.3f, UV_STATUS_ON, 1f, 0.9f, 0.3f, 1f);
            } else {
                b.box(-9.6f, 27, 5.9f, -7.4f, 29.5f, 8.1f, UV_STATUS_OFF);
            }

            // =========================================================================
            // 8. DYNAMIC MOLTEN BATH SURFACE & 3 RECIPROCATING GRAPHITE ELECTRODES + ARCS
            // =========================================================================
            // Calculate dynamic molten bath surface height inside crucible (Y: 11.5 .. 23.0)
            float fillRatio = Mth.clamp(moltenAmount / 16000.0f, 0.0f, 1.0f);
            if (isActive && fillRatio < 0.25f) {
                fillRatio = 0.25f + 0.15f * (progress / 100.0f);
            }
            float bathY = 11.5f + fillRatio * 10.5f;
            if (isActive) {
                bathY += 0.35f * Mth.sin(time * 0.25f);
            }

            // Render glowing molten metal bath inside crucible when active or containing fluid
            if (isActive || moltenAmount > 0) {
                float wave = isActive ? (0.92f + 0.08f * Mth.sin(time * 0.45f)) : 0.88f;
                b.boxEmissive(0.1f, 11.0f, 15.1f, 15.9f, bathY, 32.9f, UV_MOLTEN,
                        fluidRgb[0] * wave, fluidRgb[1] * wave, fluidRgb[2] * wave, 1.0f);
            }

            // 3 Graphite Electrodes in a delta triad around the center hopper:
            //   Electrode 0: Front-Left  (center X =  0.5, Z = 18.5)
            //   Electrode 1: Front-Right (center X = 15.5, Z = 18.5)
            //   Electrode 2: Rear-Center (center X =  8.0, Z = 32.5)
            float[][] electrodes = {
                    { -0.5f, 16.5f }, // E0: West-North crucible quadrant
                    { 13.5f, 16.5f }, // E1: East-North crucible quadrant
                    {  6.5f, 29.5f }  // E2: Center-South crucible quadrant
            };

            for (int i = 0; i < 3; i++) {
                float ex = electrodes[i][0];
                float ez = electrodes[i][1];

                // Independent 3-phase vertical regulation oscillation + high-current arc vibration when active
                float strokeOffset = 0.0f;
                if (isActive) {
                    float slowReg = Mth.sin(time * 0.12f + i * 2.094f) * 1.4f;
                    float arcJitter = Mth.sin(time * 1.35f + i * 1.7f) * 0.35f;
                    strokeOffset = -2.2f + slowReg + arcJitter;
                }

                float tipBottomY = 23.5f + strokeOffset;
                float colTopY = 44.5f + strokeOffset;

                // Copper busbar clamp arm extending from rear gantry to electrode column
                float clampY = colTopY - 3.5f;
                b.box(ex - 0.4f, clampY, ez - 0.4f, ex + 3.4f, clampY + 2.5f, 40.0f, UV_COPPER_BUS);
                // Chrome water-cooled contact shoe around electrode
                b.box(ex - 0.7f, clampY - 0.5f, ez - 0.7f, ex + 3.7f, clampY + 3.0f, ez + 3.7f, UV_CHROME);

                // Upper dark machined graphite electrode column (passes into furnace roof)
                b.box(ex, tipBottomY + 5.0f, ez, ex + 3.0f, colTopY, ez + 3.0f, UV_GRAPHITE);

                // Lower electrode tip (glowing incandescent white/orange when active)
                if (isActive) {
                    b.boxEmissive(ex + 0.15f, tipBottomY, ez + 0.15f, ex + 2.85f, tipBottomY + 5.0f, ez + 2.85f,
                            UV_GRAPHITE_HOT, 1.0f, 0.95f, 0.85f, 1.0f);

                    // =================================================================
                    // 3D ELECTRIC ARC PLASMA BOLTS LEAPING FROM ELECTRODE TIP TO BATH!
                    // =================================================================
                    float arcTopY = tipBottomY + 0.5f;
                    float arcBotY = Math.min(arcTopY - 1.5f, bathY);
                    float midY = (arcTopY + arcBotY) * 0.5f;

                    // Fast chaotic plasma deflection per tick
                    float jx1 = Mth.sin(time * 2.4f + i * 3.1f) * 1.3f;
                    float jz1 = Mth.cos(time * 2.9f + i * 2.3f) * 1.3f;
                    float jx2 = Mth.cos(time * 3.3f + i * 1.9f) * 1.1f;
                    float jz2 = Mth.sin(time * 2.7f + i * 4.1f) * 1.1f;

                    float cx = ex + 1.5f;
                    float cz = ez + 1.5f;

                    // Upper arc bolt segment
                    b.boxEmissive(cx - 0.55f + jx1 * 0.4f, midY, cz - 0.55f + jz1 * 0.4f,
                            cx + 0.55f + jx1, arcTopY, cz + 0.55f + jz1,
                            UV_ARC_PLASMA, 0.85f, 0.95f, 1.0f, 1.0f);
                    // Lower arc bolt segment striking bath
                    b.boxEmissive(cx - 0.65f + jx2, arcBotY, cz - 0.65f + jz2,
                            cx + 0.65f + jx1 * 0.5f, midY + 0.4f, cz + 0.65f + jz1 * 0.5f,
                            UV_ARC_PLASMA, 0.90f, 0.98f, 1.0f, 1.0f);
                    // Bright ionized plasma impact crater on the molten bath surface
                    b.boxEmissive(cx - 2.0f + jx2 * 0.5f, bathY - 0.2f, cz - 2.0f + jz2 * 0.5f,
                            cx + 2.0f + jx2 * 0.5f, bathY + 0.7f, cz + 2.0f + jz2 * 0.5f,
                            UV_ARC_PLASMA, 1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    b.box(ex + 0.15f, tipBottomY, ez + 0.15f, ex + 2.85f, tipBottomY + 5.0f, ez + 2.85f, UV_GRAPHITE);
                }
            }
        });
    }

    /**
     * Returns normalized {R, G, B} color multiplier for the active molten fluid in the crucible/launder.
     */
    public static float[] getMoltenFluidColor(Fluid fluid, boolean isActive) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return new float[]{1.0f, 0.65f, 0.20f}; // Default fiery molten iron orange-gold
        }
        if (fluid.isSame(RefineryFluids.MOLTEN_GOLD_SOURCE.get())) {
            return new float[]{1.0f, 0.92f, 0.28f};
        }
        if (fluid.isSame(RefineryFluids.MOLTEN_COPPER_SOURCE.get())) {
            return new float[]{1.0f, 0.52f, 0.24f};
        }
        if (fluid.isSame(RefineryFluids.MOLTEN_TIN_SOURCE.get())) {
            return new float[]{0.85f, 0.92f, 1.0f};
        }
        if (fluid.isSame(RefineryFluids.MOLTEN_BRONZE_SOURCE.get())) {
            return new float[]{1.0f, 0.68f, 0.26f};
        }
        if (fluid.isSame(RefineryFluids.MOLTEN_GLASS_SOURCE.get())) {
            return new float[]{1.0f, 0.82f, 0.45f};
        }
        return new float[]{1.0f, 0.62f, 0.22f};
    }
}
