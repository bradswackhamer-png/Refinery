package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity Monolithic 3x3x3 Model for the Continuous Casting Press:
 * - Reinforced cast-steel foundation bed with corner anchor pads and run-out table
 * - 4 precision-ground chrome tie-rod columns with castle locknuts
 * - Overhead cast-steel crown platen topped by a high-tonnage crimson-amber hydraulic cylinder
 * - Central water-cooled copper crystallizer mold cavity at (0, 0, 1)
 * - Front safety grille observation window at (0, 1, 0)
 * - Upper refractory tundish & chute at (0, 1, 2) plus lower channel receivers at (0, 0, 2) and (-1, 0, 1)
 * - Right-side water-cooling manifold cabinet with blue supply and red return pipes
 * - Motorized steel run-out roller table extending through (1, 0, 0)
 * - Front-left PLC operator control console at (-1, 0, 0) with digital screen, dial, and E-stop
 * - Dynamic reciprocating hydraulic ram crosshead with hazard stripes and water-cooled stamping die
 */
@OnlyIn(Dist.CLIENT)
public class CastingPressModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/casting_press.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;
    private static final int FULL_BRIGHT = 15728880;

    public record UVRect(float u0, float v0, float u1, float v1) {
        public static UVRect of(int x, int y, int w, int h) {
            return new UVRect(
                    (x + 0.5f) / TEX_W,
                    (y + 0.5f) / TEX_H,
                    (x + w - 0.5f) / TEX_W,
                    (y + h - 0.5f) / TEX_H
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

    // Atlas UV Regions (matching ArcCastingAssetGenerator 256x256 layout)
    private static final UVRect UV_CROWN_FRAME       = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_CHROME_TIE_ROD   = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_COPPER_MOLD      = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_MAIN_CYLINDER    = UVRect.of(208, 0, 48, 48);

    private static final UVRect UV_RAM_CROSSHEAD    = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_STAMPING_DIE     = UVRect.of(64, 48, 48, 48);
    private static final UVRect UV_INFLOW_LAUNDER   = UVRect.of(112, 48, 48, 48);
    private static final UVRect UV_RUNOUT_ROLLERS   = UVRect.of(160, 48, 48, 48);
    private static final UVRect UV_COOLING_MANIFOLD = UVRect.of(208, 48, 48, 48);

    private static final UVRect UV_PLC_FRONT        = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_CAST_BILLET      = UVRect.of(64, 96, 48, 48);
    private static final UVRect UV_SAFETY_GRILLE    = UVRect.of(112, 96, 48, 48);

    private static final UVRect UV_PLC_CASING       = UVRect.of(0, 144, 64, 48);
    private static final UVRect UV_HAZARD_STRIPES   = UVRect.of(64, 144, 48, 48);
    private static final UVRect UV_CHROME_SHAFTS    = UVRect.of(112, 144, 48, 48);
    private static final UVRect UV_RADIATOR_VENTS   = UVRect.of(160, 144, 48, 48);
    private static final UVRect UV_MOLTEN_INFLOW    = UVRect.of(208, 144, 48, 48);

    private static final UVRect UV_STATUS_ON        = UVRect.of(0, 192, 24, 24);
    private static final UVRect UV_STATUS_OFF       = UVRect.of(24, 192, 24, 24);
    private static final UVRect UV_FOUNDATION_BED   = UVRect.of(48, 192, 64, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid beaconActive;
    private final Cuboid beaconIdle;

    public CastingPressModel() {
        // =========================================================================
        // 1. REINFORCED FOUNDATION BED & RUNNERS (Y: 0..6)
        // =========================================================================
        // Left longitudinal base runner
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 1.0f, -9.0f, 4.0f, 47.0f, UV_CROWN_FRAME));
        // Right longitudinal base runner
        staticCuboids.add(Cuboid.uniform(25.0f, 0.0f, 1.0f, 31.0f, 4.0f, 47.0f, UV_CROWN_FRAME));
        // Center support runner
        staticCuboids.add(Cuboid.uniform(6.0f, 0.0f, 1.0f, 10.0f, 4.0f, 47.0f, UV_CROWN_FRAME));

        // Front, mid, and rear cross-beams
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 1.0f, 25.0f, 3.5f, 5.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 22.0f, 25.0f, 3.5f, 26.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 43.0f, 25.0f, 3.5f, 47.0f, UV_CROWN_FRAME));

        // Foundation steel deck bed
        staticCuboids.add(Cuboid.uniform(-9.0f, 3.5f, 5.0f, 25.0f, 4.0f, 43.0f, UV_FOUNDATION_BED));

        // Front safety hazard stripe toe-kick
        staticCuboids.add(new Cuboid(
                -15.2f, 0.0f, 0.0f,
                31.2f, 2.5f, 0.95f,
                UV_HAZARD_STRIPES, UV_CROWN_FRAME,
                UV_HAZARD_STRIPES, UV_HAZARD_STRIPES,
                UV_HAZARD_STRIPES, UV_CROWN_FRAME
        ));

        // 4 Corner heavy tie-rod anchor bolting plinths
        staticCuboids.add(Cuboid.uniform(-14.0f, 4.0f, 2.0f, -6.0f, 6.0f, 10.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(22.0f, 4.0f, 2.0f, 30.0f, 6.0f, 10.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(-14.0f, 4.0f, 38.0f, -6.0f, 6.0f, 46.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(22.0f, 4.0f, 38.0f, 30.0f, 6.0f, 46.0f, UV_CROWN_FRAME));

        // =========================================================================
        // 2. FOUR PRECISION-GROUND CHROME TIE-ROD COLUMNS (Y: 6..42)
        // =========================================================================
        staticCuboids.add(Cuboid.uniform(-11.5f, 6.0f, 4.5f, -8.5f, 42.0f, 7.5f, UV_CHROME_TIE_ROD));
        staticCuboids.add(Cuboid.uniform(24.5f, 6.0f, 4.5f, 27.5f, 42.0f, 7.5f, UV_CHROME_TIE_ROD));
        staticCuboids.add(Cuboid.uniform(-11.5f, 6.0f, 40.5f, -8.5f, 42.0f, 43.5f, UV_CHROME_TIE_ROD));
        staticCuboids.add(Cuboid.uniform(24.5f, 6.0f, 40.5f, 27.5f, 42.0f, 43.5f, UV_CHROME_TIE_ROD));

        // Heavy threaded locknuts at column tops and bases
        for (float[] p : new float[][]{{-12f, 4f}, {24f, 4f}, {-12f, 40f}, {24f, 40f}}) {
            staticCuboids.add(Cuboid.uniform(p[0] - 0.5f, 6.0f, p[1] - 0.5f, p[0] + 4.5f, 8.5f, p[1] + 4.5f, UV_CROWN_FRAME));
            staticCuboids.add(Cuboid.uniform(p[0] - 0.5f, 39.5f, p[1] - 0.5f, p[0] + 4.5f, 41.5f, p[1] + 4.5f, UV_CROWN_FRAME));
        }

        // =========================================================================
        // 3. OVERHEAD CAST-STEEL CROWN PLATEN & MAIN HYDRAULIC CYLINDERS (Y: 41..56)
        // =========================================================================
        // Massive crown platen
        staticCuboids.add(Cuboid.uniform(-14.0f, 41.5f, 2.0f, 30.0f, 45.5f, 46.0f, UV_CROWN_FRAME));
        // Main high-tonnage hydraulic cylinder barrel
        staticCuboids.add(Cuboid.uniform(3.0f, 45.5f, 19.0f, 13.0f, 56.0f, 29.0f, UV_MAIN_CYLINDER));
        // Twin hydraulic return booster cylinders
        staticCuboids.add(Cuboid.uniform(-4.0f, 45.5f, 22.0f, 0.0f, 52.0f, 26.0f, UV_MAIN_CYLINDER));
        staticCuboids.add(Cuboid.uniform(16.0f, 45.5f, 22.0f, 20.0f, 52.0f, 26.0f, UV_MAIN_CYLINDER));

        // =========================================================================
        // 4. WATER-COOLED COPPER CRYSTALLIZER MOLD BASIN AT (0, 0, 1) (Y: 4..20)
        // =========================================================================
        // Heavy steel outer mold enclosure
        staticCuboids.add(Cuboid.uniform(-1.0f, 4.0f, 15.0f, 17.0f, 18.0f, 33.0f, UV_CROWN_FRAME));
        // Inner water-cooled copper crystallizer mold cavity (X: 1..15, Z: 17..31, Y: 6..18)
        staticCuboids.add(Cuboid.uniform(1.0f, 4.5f, 17.0f, 15.0f, 6.0f, 31.0f, UV_COPPER_MOLD)); // Basin hearth
        staticCuboids.add(Cuboid.uniform(1.0f, 6.0f, 15.5f, 15.0f, 18.0f, 17.0f, UV_COPPER_MOLD)); // North inner
        staticCuboids.add(Cuboid.uniform(1.0f, 6.0f, 31.0f, 15.0f, 18.0f, 32.5f, UV_COPPER_MOLD)); // South inner
        staticCuboids.add(Cuboid.uniform(-0.5f, 6.0f, 17.0f, 1.0f, 18.0f, 31.0f, UV_COPPER_MOLD));  // West inner
        staticCuboids.add(Cuboid.uniform(15.0f, 6.0f, 17.0f, 16.5f, 18.0f, 31.0f, UV_COPPER_MOLD)); // East inner

        // =========================================================================
        // 5. FRONT SAFETY OBSERVATION GRILLE AT (0, 1, 0) (Y: 14..26)
        // =========================================================================
        staticCuboids.add(new Cuboid(
                2.0f, 14.0f, 5.0f,
                14.0f, 26.0f, 8.0f,
                UV_SAFETY_GRILLE,   // North -> Safety grille & window!
                UV_CROWN_FRAME,     // South
                UV_CROWN_FRAME,     // West
                UV_CROWN_FRAME,     // East
                UV_CROWN_FRAME,     // Up
                UV_CROWN_FRAME      // Down
        ));

        // =========================================================================
        // 6. UPPER REFRACTORY TUNDISH & LOWER CHANNEL RECEIVERS (0, 1, 2) & (0, 0, 2)
        // =========================================================================
        // Upper tundish & sloped pouring launder chute at (0, 1, 2)
        staticCuboids.add(Cuboid.uniform(1.0f, 16.0f, 32.5f, 15.0f, 26.0f, 45.0f, UV_INFLOW_LAUNDER));
        // Lower channel receiver trough at (0, 0, 2) (Y: 3.5..9.0, aligns with channels at Y=0!)
        staticCuboids.add(Cuboid.uniform(3.0f, 3.5f, 33.0f, 13.0f, 5.0f, 48.0f, UV_INFLOW_LAUNDER));
        staticCuboids.add(Cuboid.uniform(1.5f, 3.5f, 33.0f, 3.0f, 9.0f, 48.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(13.0f, 3.5f, 33.0f, 14.5f, 9.0f, 48.0f, UV_CROWN_FRAME));
        // Lower channel receiver trough at (-1, 0, 1) (West side of mold, aligns with channels from West)
        staticCuboids.add(Cuboid.uniform(-16.0f, 3.5f, 19.0f, -1.0f, 5.0f, 29.0f, UV_INFLOW_LAUNDER));
        staticCuboids.add(Cuboid.uniform(-16.0f, 3.5f, 17.5f, -1.0f, 9.0f, 19.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(-16.0f, 3.5f, 29.0f, -1.0f, 9.0f, 30.5f, UV_CROWN_FRAME));

        // =========================================================================
        // 7. RIGHT WATER-COOLING MANIFOLD CABINET (X: 25..31, Y: 4..30, Z: 14..34)
        // =========================================================================
        staticCuboids.add(new Cuboid(
                25.0f, 4.0f, 14.0f,
                31.0f, 30.0f, 34.0f,
                UV_COOLING_MANIFOLD,    // North
                UV_COOLING_MANIFOLD,    // South
                UV_COOLING_MANIFOLD,    // West
                UV_COOLING_MANIFOLD,    // East -> blue & red water lines!
                UV_CROWN_FRAME,         // Up
                UV_CROWN_FRAME          // Down
        ));

        // =========================================================================
        // 8. MOTORIZED RUN-OUT ROLLER TABLE AT (1, 0, 0) (X: 16..32, Y: 4..10, Z: 8..24)
        // =========================================================================
        // Run-out table side frame rails
        staticCuboids.add(Cuboid.uniform(16.0f, 4.0f, 8.0f, 32.0f, 9.5f, 10.0f, UV_CROWN_FRAME));
        staticCuboids.add(Cuboid.uniform(16.0f, 4.0f, 22.0f, 32.0f, 9.5f, 24.0f, UV_CROWN_FRAME));
        // Driven steel conveyor rollers
        for (float rx = 18.0f; rx <= 30.0f; rx += 2.0f) {
            staticCuboids.add(Cuboid.uniform(rx - 0.6f, 7.0f, 10.0f, rx + 0.6f, 9.2f, 22.0f, UV_RUNOUT_ROLLERS));
        }

        // =========================================================================
        // 9. OPERATOR PLC CONTROL CONSOLE AT FRONT-LEFT (-1, 0, 0)
        // =========================================================================
        // Pedestal base
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 2.0f, -2.0f, 16.0f, 10.0f, UV_PLC_CASING));
        // Upper console cabinet (front face = PLC screen, hydraulic gauge, yellow E-stop!)
        staticCuboids.add(new Cuboid(
                -14.0f, 16.0f, 2.0f,
                -2.0f, 26.0f, 9.0f,
                UV_PLC_FRONT,   // North (facing operator/player!)
                UV_PLC_CASING,  // South
                UV_PLC_CASING,  // West
                UV_PLC_CASING,  // East
                UV_PLC_CASING,  // Up
                UV_PLC_CASING   // Down
        ));
        // Beacon stem
        staticCuboids.add(Cuboid.uniform(-9.0f, 26.0f, 4.5f, -7.0f, 27.5f, 6.5f, UV_CHROME_SHAFTS));

        // Beacon lenses
        this.beaconActive = Cuboid.uniform(-9.5f, 27.5f, 4.0f, -6.5f, 30.5f, 7.0f, UV_STATUS_ON);
        this.beaconIdle   = Cuboid.uniform(-9.5f, 27.5f, 4.0f, -6.5f, 30.5f, 7.0f, UV_STATUS_OFF);
    }

    public void render(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            boolean isActive,
            int progress,
            long gameTime,
            float partialTicks,
            int moltenAmount,
            Fluid moltenFluid
    ) {
        float time = (gameTime + partialTicks);
        float[] fluidRgb = ArcMelterModel.getMoltenFluidColor(moltenFluid, isActive);

        // Smooth hydraulic press ram stroke (0.0 = top retracted, 1.0 = bottom stamping hold)
        // 0..90:   Downward stamping stroke
        // 90..150: Bottom high-pressure dwell & water-quench solidification
        // 150..240:Smooth upward hydraulic retraction
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
                strokeRatio = 1.0f - (t * t * (3f - 2f * t)); // smooth hydraulic retraction
            }
        }

        final float ramDisplacement = strokeRatio * 11.5f;

        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, consumer) -> {
            Matrix4f matrix = pose.pose();

            // 1. Render all static cuboids
            for (Cuboid c : staticCuboids) {
                emitCuboid(pose, matrix, consumer, c, 0.0f, light, OverlayTexture.NO_OVERLAY);
            }

            // 2. Render status beacon on top of PLC console
            if (isActive) {
                emitCuboid(pose, matrix, consumer, beaconActive, 0.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            } else {
                emitCuboid(pose, matrix, consumer, beaconIdle, 0.0f, light, OverlayTexture.NO_OVERLAY);
            }

            // 3. Render Reciprocating Hydraulic Ram Crosshead (slides vertically by -ramDisplacement)
            float dy = -ramDisplacement;

            // Main moving crosshead platen (X: -12..28, Z: 3..45)
            Cuboid ramPlaten = new Cuboid(
                    -12.0f, 33.0f, 3.0f,
                    28.0f, 39.0f, 45.0f,
                    UV_RAM_CROSSHEAD, UV_RAM_CROSSHEAD,
                    UV_CROWN_FRAME, UV_CROWN_FRAME,
                    UV_CROWN_FRAME, UV_CROWN_FRAME
            );
            emitCuboid(pose, matrix, consumer, ramPlaten, dy, light, OverlayTexture.NO_OVERLAY);

            // 4 Chrome guide rods extending from crown into ram crosshead
            Cuboid centerPiston = Cuboid.uniform(6.0f, 39.0f + dy, 22.0f, 10.0f, 45.5f, 26.0f, UV_CHROME_SHAFTS);
            emitCuboid(pose, matrix, consumer, centerPiston, 0.0f, light, OverlayTexture.NO_OVERLAY);

            // Water-cooled copper upper stamping die
            Cuboid stampingDie = new Cuboid(
                    1.0f, 29.0f, 17.0f,
                    15.0f, 33.0f, 31.0f,
                    UV_STAMPING_DIE, UV_STAMPING_DIE,
                    UV_STAMPING_DIE, UV_STAMPING_DIE,
                    UV_STAMPING_DIE, UV_COPPER_MOLD
            );
            emitCuboid(pose, matrix, consumer, stampingDie, dy, light, OverlayTexture.NO_OVERLAY);

            // 4. Molten Inflow and Solidified Billet inside the crystallizer mold basin
            if (isActive || progress > 0 || moltenAmount > 0) {
                boolean solidifying = (progress >= 90);
                float alpha = solidifying ? 1.0f : 0.95f;

                if (solidifying) {
                    // Quenched incandescent solidified slab/billet under stamping die
                    Cuboid billet = Cuboid.uniform(1.2f, 6.0f, 17.2f, 14.8f, 11.0f, 30.8f, UV_CAST_BILLET);
                    emitCuboidColored(pose, matrix, consumer, billet, 0.0f, 1.0f, 0.85f, 0.65f, alpha, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                } else {
                    // Glowing molten pool filling crystallizer basin
                    float wave = 0.92f + 0.08f * Mth.sin(time * 0.45f);
                    Cuboid moltenPool = Cuboid.uniform(1.2f, 6.0f, 17.2f, 14.8f, 10.5f, 30.8f, UV_MOLTEN_INFLOW);
                    emitCuboidColored(pose, matrix, consumer, moltenPool, 0.0f, fluidRgb[0] * wave, fluidRgb[1] * wave, fluidRgb[2] * wave, alpha, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                }

                // Inflow streams entering mold from rear and west troughs
                float inWave = 0.90f + 0.10f * Mth.sin(time * 0.40f);
                Cuboid rearInflow = Cuboid.uniform(3.5f, 4.8f, 33.0f, 12.5f, 6.5f, 47.9f, UV_MOLTEN_INFLOW);
                emitCuboidColored(pose, matrix, consumer, rearInflow, 0.0f, fluidRgb[0] * inWave, fluidRgb[1] * inWave, fluidRgb[2] * inWave, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                Cuboid westInflow = Cuboid.uniform(-15.9f, 4.8f, 19.5f, -0.1f, 6.5f, 28.5f, UV_MOLTEN_INFLOW);
                emitCuboidColored(pose, matrix, consumer, westInflow, 0.0f, fluidRgb[0] * inWave, fluidRgb[1] * inWave, fluidRgb[2] * inWave, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }
        });
    }

    private static void emitCuboid(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            float yOffset,
            int light,
            int overlay
    ) {
        emitCuboidColored(pose, matrix, consumer, c, yOffset, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay);
    }

    private static void emitCuboidColored(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            float yOffset,
            float r, float g, float b, float a,
            int light,
            int overlay
    ) {
        float x0 = c.x0() / 16.0f;
        float y0 = (c.y0() + yOffset) / 16.0f;
        float z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f;
        float y1 = (c.y1() + yOffset) / 16.0f;
        float z1 = c.z1() / 16.0f;

        // NORTH Face (z = z0, normal 0, 0, -1)
        emitQuad(pose, matrix, consumer,
                x1, y1, z0,
                x1, y0, z0,
                x0, y0, z0,
                x0, y1, z0,
                c.north(), 0.0f, 0.0f, -1.0f, r, g, b, a, light, overlay);

        // SOUTH Face (z = z1, normal 0, 0, +1)
        emitQuad(pose, matrix, consumer,
                x0, y1, z1,
                x0, y0, z1,
                x1, y0, z1,
                x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, r, g, b, a, light, overlay);

        // WEST Face (x = x0, normal -1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y0, z0,
                x0, y0, z1,
                x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, r, g, b, a, light, overlay);

        // EAST Face (x = x1, normal +1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x1, y1, z1,
                x1, y0, z1,
                x1, y0, z0,
                x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, r, g, b, a, light, overlay);

        // UP Face (y = y1, normal 0, +1, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y1, z1,
                x1, y1, z1,
                x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, r, g, b, a, light, overlay);

        // DOWN Face (y = y0, normal 0, -1, 0)
        if (c.y0() + yOffset > 0.01f) {
            emitQuad(pose, matrix, consumer,
                    x0, y0, z1,
                    x1, y0, z1,
                    x1, y0, z0,
                    x0, y0, z0,
                    c.down(), 0.0f, -1.0f, 0.0f, r, g, b, a, light, overlay);
        }
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            float xA, float yA, float zA,
            float xB, float yB, float zB,
            float xC, float yC, float zC,
            float xD, float yD, float zD,
            UVRect uv,
            float nx, float ny, float nz,
            float r, float g, float b, float a,
            int light,
            int overlay
    ) {
        consumer.addVertex(matrix, xA, yA, zA)
                .setColor(r, g, b, a)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xB, yB, zB)
                .setColor(r, g, b, a)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xC, yC, zC)
                .setColor(r, g, b, a)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xD, yD, zD)
                .setColor(r, g, b, a)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
