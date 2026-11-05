package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity Monolithic 3x3x3 Model for the Assembled Electric Arc Melter (EAF):
 * - Rocker trunnion foundation skid with structural I-beams and anchor bolt plinths
 * - Cylindrical water-cooled ribbed furnace shell with vertical buckstay stiffeners
 * - Magnesia-carbon refractory crucible hearth with open glowing molten bath cavity
 * - Front armored quartz slag observation door at (0, 1, 0)
 * - Eccentric bottom-tapping (EBT) refractory launder spout extending through (1, 0, 0)
 *   aligning seamlessly with Open Casting Channels on North (Z=0) and East (X=32)
 * - Rear-left high-voltage transformer vault with oil radiator fins and copper busbars
 * - Rear gantry columns supporting domed water-cooled roof and delta refractory insert
 * - Top flared raw ore charge feed hopper at (0, 2, 1)
 * - Front-left operator control console at (-1, 0, 0) with digital display and status beacon
 * - Dynamic reciprocating graphite electrodes, jagged 3D electric arc plasma bolts, and
 *   rising/falling glowing molten bath surface
 */
@OnlyIn(Dist.CLIENT)
public class ArcMelterModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/arc_melter.png"
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
    private static final UVRect UV_FURNACE_SHELL    = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_TRUNNION_BASE    = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_REFRACTORY       = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_GRAPHITE         = UVRect.of(208, 0, 48, 48);

    private static final UVRect UV_ROOF_DOME        = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_COPPER_BUS       = UVRect.of(64, 48, 48, 48);
    private static final UVRect UV_TRANSFORMER      = UVRect.of(112, 48, 48, 48);
    private static final UVRect UV_VIEWPORT         = UVRect.of(160, 48, 48, 48);
    private static final UVRect UV_MAST_CYLINDER    = UVRect.of(208, 48, 48, 48);

    private static final UVRect UV_CONSOLE_FRONT    = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_ARC_PLASMA       = UVRect.of(64, 96, 48, 48);
    private static final UVRect UV_CHARGE_HOPPER    = UVRect.of(112, 96, 48, 48);
    private static final UVRect UV_MOLTEN_BATH      = UVRect.of(160, 96, 48, 48);

    private static final UVRect UV_CONSOLE_CASING   = UVRect.of(0, 144, 64, 48);
    private static final UVRect UV_HAZARD_STRIPES   = UVRect.of(64, 144, 48, 48);
    private static final UVRect UV_CHROME_STEEL     = UVRect.of(112, 144, 48, 48);
    private static final UVRect UV_RADIATOR_FINS    = UVRect.of(160, 144, 48, 48);
    private static final UVRect UV_MOLTEN_LAUNDER   = UVRect.of(208, 144, 48, 48);

    private static final UVRect UV_STATUS_ON        = UVRect.of(0, 192, 24, 24);
    private static final UVRect UV_STATUS_OFF       = UVRect.of(24, 192, 24, 24);
    private static final UVRect UV_BASEPLATE        = UVRect.of(48, 192, 64, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid beaconActive;
    private final Cuboid beaconIdle;

    public ArcMelterModel() {
        // =========================================================================
        // 1. REINFORCED BASE SKID, TRUNNION ROCKER RAILS & TOE-KICK (Y: 0..8)
        // =========================================================================
        // Left longitudinal skid runner
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 1.0f, -9.0f, 3.5f, 47.0f, UV_TRUNNION_BASE));
        // Right longitudinal skid runner
        staticCuboids.add(Cuboid.uniform(25.0f, 0.0f, 1.0f, 31.0f, 3.5f, 47.0f, UV_TRUNNION_BASE));
        // Center longitudinal support runner
        staticCuboids.add(Cuboid.uniform(6.0f, 0.0f, 1.0f, 10.0f, 3.5f, 47.0f, UV_TRUNNION_BASE));

        // Front cross-beam
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 1.0f, 25.0f, 3.0f, 5.0f, UV_TRUNNION_BASE));
        // Mid cross-beam
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 22.0f, 25.0f, 3.0f, 26.0f, UV_TRUNNION_BASE));
        // Rear cross-beam
        staticCuboids.add(Cuboid.uniform(-9.0f, 0.0f, 43.0f, 25.0f, 3.0f, 47.0f, UV_TRUNNION_BASE));

        // Foundation steel deck plate
        staticCuboids.add(Cuboid.uniform(-9.0f, 3.0f, 5.0f, 25.0f, 3.5f, 43.0f, UV_BASEPLATE));

        // Front perimeter safety hazard toe-kick strip
        staticCuboids.add(new Cuboid(
                -15.2f, 0.0f, 0.0f,
                31.2f, 2.5f, 0.95f,
                UV_HAZARD_STRIPES, UV_TRUNNION_BASE,
                UV_HAZARD_STRIPES, UV_HAZARD_STRIPES,
                UV_HAZARD_STRIPES, UV_TRUNNION_BASE
        ));

        // Corner anchor bolting plinths
        staticCuboids.add(Cuboid.uniform(-15.0f, 3.5f, 1.0f, -9.0f, 5.5f, 7.0f, UV_TRUNNION_BASE));
        staticCuboids.add(Cuboid.uniform(-15.0f, 3.5f, 41.0f, -9.0f, 5.5f, 47.0f, UV_TRUNNION_BASE));
        staticCuboids.add(Cuboid.uniform(25.0f, 3.5f, 41.0f, 31.0f, 5.5f, 47.0f, UV_TRUNNION_BASE));

        // Rocker trunnion gear saddles beneath furnace shell
        staticCuboids.add(Cuboid.uniform(-7.0f, 3.5f, 11.0f, -3.0f, 8.0f, 37.0f, UV_TRUNNION_BASE));
        staticCuboids.add(Cuboid.uniform(19.0f, 3.5f, 11.0f, 23.0f, 8.0f, 37.0f, UV_TRUNNION_BASE));

        // Twin hydraulic tilt cylinders
        staticCuboids.add(Cuboid.uniform(-6.5f, 3.5f, 6.0f, -3.5f, 11.0f, 9.0f, UV_MAST_CYLINDER));
        staticCuboids.add(Cuboid.uniform(-5.5f, 11.0f, 7.0f, -4.5f, 14.5f, 8.0f, UV_CHROME_STEEL));
        staticCuboids.add(Cuboid.uniform(19.5f, 3.5f, 11.0f, 22.5f, 11.0f, 14.0f, UV_MAST_CYLINDER));
        staticCuboids.add(Cuboid.uniform(20.5f, 11.0f, 12.0f, 21.5f, 14.5f, 13.0f, UV_CHROME_STEEL));

        // =========================================================================
        // 2. CYLINDRICAL WATER-COOLED RIBBED EAF SHELL (Y: 8..30, Center X: 8, Z: 24)
        // =========================================================================
        // Dish bottom plate
        staticCuboids.add(Cuboid.uniform(-3.0f, 6.0f, 13.0f, 19.0f, 8.0f, 35.0f, UV_FURNACE_SHELL));

        // Outer shell walls (octagonal profile)
        staticCuboids.add(Cuboid.uniform(-6.0f, 8.0f, 14.0f, -2.0f, 30.0f, 34.0f, UV_FURNACE_SHELL)); // West
        staticCuboids.add(Cuboid.uniform(18.0f, 13.0f, 14.0f, 22.0f, 30.0f, 34.0f, UV_FURNACE_SHELL)); // East upper
        staticCuboids.add(Cuboid.uniform(18.0f, 8.0f, 24.0f, 22.0f, 13.0f, 34.0f, UV_FURNACE_SHELL)); // East lower rear
        staticCuboids.add(Cuboid.uniform(-2.0f, 8.0f, 9.0f, 18.0f, 13.0f, 13.0f, UV_FURNACE_SHELL));  // North bottom
        staticCuboids.add(Cuboid.uniform(-2.0f, 25.0f, 9.0f, 18.0f, 30.0f, 13.0f, UV_FURNACE_SHELL)); // North top
        staticCuboids.add(Cuboid.uniform(-2.0f, 13.0f, 9.0f, 2.0f, 25.0f, 13.0f, UV_FURNACE_SHELL));  // North left
        staticCuboids.add(Cuboid.uniform(14.0f, 13.0f, 9.0f, 18.0f, 25.0f, 13.0f, UV_FURNACE_SHELL)); // North right
        staticCuboids.add(Cuboid.uniform(-2.0f, 8.0f, 35.0f, 18.0f, 30.0f, 39.0f, UV_FURNACE_SHELL)); // South

        // Diagonal corner chamfer columns
        staticCuboids.add(Cuboid.uniform(-5.0f, 8.0f, 10.0f, -1.0f, 30.0f, 14.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(17.0f, 8.0f, 10.0f, 21.0f, 30.0f, 14.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(-5.0f, 8.0f, 34.0f, -1.0f, 30.0f, 38.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(17.0f, 8.0f, 34.0f, 21.0f, 30.0f, 38.0f, UV_FURNACE_SHELL));

        // Water-cooling girth flange rings
        staticCuboids.add(Cuboid.uniform(-6.8f, 8.05f, 8.2f, 22.8f, 9.95f, 39.8f, UV_TRUNNION_BASE));
        staticCuboids.add(Cuboid.uniform(-6.8f, 28.05f, 8.2f, 22.8f, 29.95f, 39.8f, UV_TRUNNION_BASE));

        // Cooling water header pipes wrapping outer shell
        staticCuboids.add(Cuboid.uniform(-6.5f, 18.0f, 8.5f, 22.5f, 19.5f, 9.5f, UV_COPPER_BUS));
        staticCuboids.add(Cuboid.uniform(-6.5f, 18.0f, 38.5f, 22.5f, 19.5f, 39.5f, UV_COPPER_BUS));
        staticCuboids.add(Cuboid.uniform(-6.5f, 18.0f, 9.5f, -5.5f, 19.5f, 38.5f, UV_COPPER_BUS));
        staticCuboids.add(Cuboid.uniform(21.5f, 18.0f, 9.5f, 22.5f, 19.5f, 38.5f, UV_COPPER_BUS));

        // =========================================================================
        // 3. INTERIOR MAGNESIA-CARBON REFRACTORY CRUCIBLE (Y: 8..29)
        // =========================================================================
        // Crucible hearth floor
        staticCuboids.add(Cuboid.uniform(-2.0f, 8.0f, 14.0f, 18.0f, 11.0f, 34.0f, UV_REFRACTORY));
        // Crucible inner walls defining molten bath cavity (X: 0..16, Z: 16..32)
        staticCuboids.add(Cuboid.uniform(-2.0f, 11.0f, 14.0f, 0.0f, 29.0f, 34.0f, UV_REFRACTORY));
        staticCuboids.add(Cuboid.uniform(16.0f, 11.0f, 14.0f, 18.0f, 29.0f, 34.0f, UV_REFRACTORY));
        staticCuboids.add(Cuboid.uniform(0.0f, 11.0f, 14.0f, 16.0f, 29.0f, 16.0f, UV_REFRACTORY));
        staticCuboids.add(Cuboid.uniform(0.0f, 11.0f, 32.0f, 16.0f, 29.0f, 34.0f, UV_REFRACTORY));

        // =========================================================================
        // 4. FRONT ARMORED QUARTZ SLAG & OBSERVATION DOOR AT (0, 1, 0)
        // =========================================================================
        // Slag door cast frame
        staticCuboids.add(new Cuboid(
                2.0f, 13.0f, 5.0f,
                14.0f, 25.0f, 9.0f,
                UV_VIEWPORT,        // North face -> armored quartz window & safety grille!
                UV_FURNACE_SHELL,   // South
                UV_FURNACE_SHELL,   // West
                UV_FURNACE_SHELL,   // East
                UV_FURNACE_SHELL,   // Up
                UV_FURNACE_SHELL    // Down
        ));
        // Door lifting guide rods
        staticCuboids.add(Cuboid.uniform(3.0f, 25.0f, 6.0f, 4.0f, 30.5f, 7.0f, UV_CHROME_STEEL));
        staticCuboids.add(Cuboid.uniform(12.0f, 25.0f, 6.0f, 13.0f, 30.5f, 7.0f, UV_CHROME_STEEL));

        // =========================================================================
        // 5. ECCENTRIC BOTTOM-TAPPING (EBT) LAUNDER SPOUT AT OUTPUT PORT (1, 0, 0)
        // =========================================================================
        // Upper EBT refractory chute exiting furnace shell tap-hole
        staticCuboids.add(Cuboid.uniform(17.0f, 7.0f, 14.0f, 24.0f, 12.0f, 20.0f, UV_REFRACTORY));
        // Main lower launder trough bed running forward to Front (Z=0)
        staticCuboids.add(Cuboid.uniform(20.0f, 3.5f, 0.0f, 28.0f, 5.0f, 20.0f, UV_REFRACTORY));
        // Launder left & right side walls
        staticCuboids.add(Cuboid.uniform(18.5f, 3.5f, 0.0f, 20.0f, 9.0f, 20.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(28.0f, 3.5f, 0.0f, 29.5f, 9.0f, 20.0f, UV_FURNACE_SHELL));
        // East branch launder bed (connecting to East face of (1, 0, 0) at X=32)
        staticCuboids.add(Cuboid.uniform(28.0f, 3.5f, 5.0f, 32.0f, 5.0f, 11.0f, UV_REFRACTORY));
        // East branch walls
        staticCuboids.add(Cuboid.uniform(28.0f, 3.5f, 3.5f, 32.0f, 9.0f, 5.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(28.0f, 3.5f, 11.0f, 32.0f, 9.0f, 12.5f, UV_FURNACE_SHELL));
        // Front hazard tapping flange collar at Z=0
        staticCuboids.add(new Cuboid(
                18.0f, 3.0f, 0.0f,
                30.0f, 9.5f, 1.0f,
                UV_HAZARD_STRIPES, UV_FURNACE_SHELL,
                UV_FURNACE_SHELL, UV_FURNACE_SHELL,
                UV_HAZARD_STRIPES, UV_FURNACE_SHELL
        ));
        // East hazard tapping flange collar at X=32
        staticCuboids.add(new Cuboid(
                31.0f, 3.0f, 3.0f,
                32.0f, 9.5f, 13.0f,
                UV_FURNACE_SHELL, UV_FURNACE_SHELL,
                UV_FURNACE_SHELL, UV_HAZARD_STRIPES,
                UV_HAZARD_STRIPES, UV_FURNACE_SHELL
        ));

        // =========================================================================
        // 6. REAR-LEFT STEP-DOWN TRANSFORMER VAULT & BUSBARS (X: -15..-5, Z: 18..46)
        // =========================================================================
        // Transformer vault housing
        staticCuboids.add(new Cuboid(
                -15.0f, 3.5f, 18.0f,
                -5.0f, 28.0f, 45.0f,
                UV_TRANSFORMER,     // North -> HV warning plate
                UV_RADIATOR_FINS,   // South -> Radiator fins
                UV_RADIATOR_FINS,   // West -> Radiator fins
                UV_TRANSFORMER,     // East
                UV_TRUNNION_BASE,   // Up
                UV_TRUNNION_BASE    // Down
        ));
        // 3 High-Voltage porcelain standoff insulators & copper busbars
        for (int i = 0; i < 3; i++) {
            float bz = 22.0f + i * 8.0f;
            staticCuboids.add(Cuboid.uniform(-13.0f, 28.0f, bz, -10.0f, 33.0f, bz + 3.0f, UV_REFRACTORY));
            staticCuboids.add(Cuboid.uniform(-12.0f, 33.0f, bz + 0.5f, -11.0f, 41.0f, bz + 2.5f, UV_COPPER_BUS));
            staticCuboids.add(Cuboid.uniform(-12.0f, 39.5f, bz + 0.5f, 3.0f + i * 5.0f, 41.0f, bz + 2.5f, UV_COPPER_BUS));
        }

        // =========================================================================
        // 7. GANTRY MAST COLUMNS, WATER-COOLED ROOF DOME & TOP HOPPER (Y: 30..48)
        // =========================================================================
        // Twin gantry mast columns at rear
        staticCuboids.add(Cuboid.uniform(-4.0f, 3.5f, 39.0f, 1.0f, 44.0f, 45.0f, UV_FURNACE_SHELL));
        staticCuboids.add(Cuboid.uniform(15.0f, 3.5f, 39.0f, 20.0f, 44.0f, 45.0f, UV_FURNACE_SHELL));
        // Overhead mast bridge
        staticCuboids.add(Cuboid.uniform(-5.0f, 41.0f, 38.0f, 21.0f, 45.0f, 46.0f, UV_FURNACE_SHELL));

        // Water-cooled roof dome
        staticCuboids.add(Cuboid.uniform(-5.0f, 30.0f, 10.0f, 21.0f, 33.5f, 38.0f, UV_ROOF_DOME));
        // Refractory delta insert ring
        staticCuboids.add(Cuboid.uniform(0.0f, 33.5f, 16.0f, 16.0f, 36.0f, 32.0f, UV_REFRACTORY));

        // Raw ore charge feed hopper at Input Port (0, 2, 1) (X: 2..14, Y: 36..48, Z: 18..30)
        staticCuboids.add(Cuboid.uniform(2.0f, 36.0f, 18.0f, 14.0f, 45.0f, 20.0f, UV_CHARGE_HOPPER)); // North
        staticCuboids.add(Cuboid.uniform(2.0f, 36.0f, 28.0f, 14.0f, 45.0f, 30.0f, UV_CHARGE_HOPPER)); // South
        staticCuboids.add(Cuboid.uniform(2.0f, 36.0f, 20.0f, 4.0f, 45.0f, 28.0f, UV_CHARGE_HOPPER));  // West
        staticCuboids.add(Cuboid.uniform(12.0f, 36.0f, 20.0f, 14.0f, 45.0f, 28.0f, UV_CHARGE_HOPPER)); // East
        // Top intake collar flange at Y: 45..48
        staticCuboids.add(new Cuboid(
                1.0f, 45.0f, 17.0f,
                15.0f, 48.0f, 31.0f,
                UV_HAZARD_STRIPES, UV_HAZARD_STRIPES,
                UV_HAZARD_STRIPES, UV_HAZARD_STRIPES,
                UV_HAZARD_STRIPES, UV_FURNACE_SHELL
        ));

        // =========================================================================
        // 8. OPERATOR CONTROL CONSOLE AT FRONT-LEFT (-1, 0, 0) (X: -14..-2, Z: 2..10)
        // =========================================================================
        // Pedestal base
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 2.0f, -2.0f, 16.0f, 10.0f, UV_CONSOLE_CASING));
        // Upper console cabinet (front face = OLED screen, dials, buttons, E-stop)
        staticCuboids.add(new Cuboid(
                -14.0f, 16.0f, 2.0f,
                -2.0f, 26.0f, 9.0f,
                UV_CONSOLE_FRONT,   // North (facing player/operator!)
                UV_CONSOLE_CASING,  // South
                UV_CONSOLE_CASING,  // West
                UV_CONSOLE_CASING,  // East
                UV_CONSOLE_CASING,  // Up
                UV_CONSOLE_CASING   // Down
        ));
        // Beacon stem
        staticCuboids.add(Cuboid.uniform(-9.0f, 26.0f, 4.5f, -7.0f, 27.5f, 6.5f, UV_CHROME_STEEL));

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
        float[] fluidRgb = getMoltenFluidColor(moltenFluid, isActive);

        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, consumer) -> {
            Matrix4f matrix = pose.pose();

            // 1. Render all static cuboids
            for (Cuboid c : staticCuboids) {
                emitCuboid(pose, matrix, consumer, c, 0.0f, light, OverlayTexture.NO_OVERLAY);
            }

            // 2. Render status beacon on top of operator console
            if (isActive) {
                emitCuboid(pose, matrix, consumer, beaconActive, 0.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            } else {
                emitCuboid(pose, matrix, consumer, beaconIdle, 0.0f, light, OverlayTexture.NO_OVERLAY);
            }

            // 3. Dynamic molten bath surface inside crucible hearth (X: 0.1..15.9, Z: 16.1..31.9)
            float fillRatio = Mth.clamp(moltenAmount / 16000.0f, 0.0f, 1.0f);
            if (isActive && fillRatio < 0.25f) {
                fillRatio = 0.25f + 0.15f * (progress / 100.0f);
            }
            float bathY = 11.2f + fillRatio * 10.5f;
            if (isActive) {
                bathY += 0.30f * Mth.sin(time * 0.35f);
            }

            if (isActive || moltenAmount > 0) {
                float wave = isActive ? (0.92f + 0.08f * Mth.sin(time * 0.45f)) : 0.88f;
                float r = fluidRgb[0] * wave;
                float g = fluidRgb[1] * wave;
                float b = fluidRgb[2] * wave;

                // Glowing molten metal bath surface
                Cuboid bath = Cuboid.uniform(0.1f, 11.0f, 16.1f, 15.9f, bathY, 31.9f, UV_MOLTEN_BATH);
                emitCuboidColored(pose, matrix, consumer, bath, 0.0f, r, g, b, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                // Flowing molten stream along tapping launder trough
                float flowWave = 0.90f + 0.10f * Mth.sin(time * 0.40f);
                Cuboid tapStream = Cuboid.uniform(20.5f, 4.8f, 0.1f, 27.5f, 6.8f, 19.5f, UV_MOLTEN_LAUNDER);
                emitCuboidColored(pose, matrix, consumer, tapStream, 0.0f, fluidRgb[0] * flowWave, fluidRgb[1] * flowWave, fluidRgb[2] * flowWave, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                Cuboid eastStream = Cuboid.uniform(27.5f, 4.8f, 5.2f, 31.9f, 6.8f, 10.8f, UV_MOLTEN_LAUNDER);
                emitCuboidColored(pose, matrix, consumer, eastStream, 0.0f, fluidRgb[0] * flowWave, fluidRgb[1] * flowWave, fluidRgb[2] * flowWave, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }

            // 4. Three Reciprocating Graphite Electrodes in a Delta Triad
            float[][] electrodePositions = {
                    { -0.5f, 16.5f }, // Electrode 0: Front-Left
                    { 13.5f, 16.5f }, // Electrode 1: Front-Right
                    {  6.5f, 29.5f }  // Electrode 2: Rear-Center
            };

            for (int i = 0; i < 3; i++) {
                float ex = electrodePositions[i][0];
                float ez = electrodePositions[i][1];

                float strokeOffset = 0.0f;
                if (isActive) {
                    float slowReg = Mth.sin(time * 0.12f + i * 2.094f) * 1.4f;
                    float arcJitter = Mth.sin(time * 1.35f + i * 1.7f) * 0.35f;
                    strokeOffset = -2.2f + slowReg + arcJitter;
                }

                float tipBottomY = 23.5f + strokeOffset;
                float colTopY = 44.5f + strokeOffset;

                // Copper busbar clamp arm
                float clampY = colTopY - 3.5f;
                Cuboid clampArm = Cuboid.uniform(ex - 0.4f, clampY, ez - 0.4f, ex + 3.4f, clampY + 2.5f, 40.0f, UV_COPPER_BUS);
                emitCuboid(pose, matrix, consumer, clampArm, 0.0f, light, OverlayTexture.NO_OVERLAY);

                // Chrome contact shoe
                Cuboid shoe = Cuboid.uniform(ex - 0.7f, clampY - 0.5f, ez - 0.7f, ex + 3.7f, clampY + 3.0f, ez + 3.7f, UV_CHROME_STEEL);
                emitCuboid(pose, matrix, consumer, shoe, 0.0f, light, OverlayTexture.NO_OVERLAY);

                // Upper dark machined graphite column
                Cuboid col = Cuboid.uniform(ex, tipBottomY + 5.0f, ez, ex + 3.0f, colTopY, ez + 3.0f, UV_GRAPHITE);
                emitCuboid(pose, matrix, consumer, col, 0.0f, light, OverlayTexture.NO_OVERLAY);

                // Lower electrode tip
                if (isActive) {
                    Cuboid hotTip = Cuboid.uniform(ex + 0.15f, tipBottomY, ez + 0.15f, ex + 2.85f, tipBottomY + 5.0f, ez + 2.85f, UV_GRAPHITE);
                    emitCuboidColored(pose, matrix, consumer, hotTip, 0.0f, 1.0f, 0.90f, 0.75f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                    // 3D Electric Arc Plasma Bolts leaping into bath
                    float arcTopY = tipBottomY + 0.5f;
                    float arcBotY = Math.min(arcTopY - 1.5f, bathY);
                    float midY = (arcTopY + arcBotY) * 0.5f;

                    float jx1 = Mth.sin(time * 2.4f + i * 3.1f) * 1.1f;
                    float jz1 = Mth.cos(time * 2.9f + i * 2.3f) * 1.1f;
                    float jx2 = Mth.cos(time * 3.3f + i * 1.9f) * 0.9f;
                    float jz2 = Mth.sin(time * 2.7f + i * 4.1f) * 0.9f;

                    float cx = ex + 1.5f;
                    float cz = ez + 1.5f;

                    Cuboid arcUpper = Cuboid.uniform(cx - 0.5f + jx1 * 0.4f, midY, cz - 0.5f + jz1 * 0.4f, cx + 0.5f + jx1, arcTopY, cz + 0.5f + jz1, UV_ARC_PLASMA);
                    emitCuboidColored(pose, matrix, consumer, arcUpper, 0.0f, 0.85f, 0.95f, 1.0f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                    Cuboid arcLower = Cuboid.uniform(cx - 0.6f + jx2, arcBotY, cz - 0.6f + jz2, cx + 0.6f + jx1 * 0.5f, midY + 0.3f, cz + 0.6f + jz1 * 0.5f, UV_ARC_PLASMA);
                    emitCuboidColored(pose, matrix, consumer, arcLower, 0.0f, 0.90f, 0.98f, 1.0f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                } else {
                    Cuboid coldTip = Cuboid.uniform(ex + 0.15f, tipBottomY, ez + 0.15f, ex + 2.85f, tipBottomY + 5.0f, ez + 2.85f, UV_GRAPHITE);
                    emitCuboid(pose, matrix, consumer, coldTip, 0.0f, light, OverlayTexture.NO_OVERLAY);
                }
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

    public static float[] getMoltenFluidColor(Fluid fluid, boolean isActive) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return new float[]{1.0f, 0.65f, 0.20f};
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
