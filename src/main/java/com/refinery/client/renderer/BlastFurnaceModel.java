package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity 3D Multiblock Model for the Assembled 5x5x5 Blast Furnace:
 * - Structural Steel I-Beam perimeter window bezel, lintel, sill, and jambs
 * - Riveted corner gusset bracket plates and center cross-mullion truss with central rosette hub
 * - Heavy industrial foundry manufacturer nameplate badge
 * - Interior stepped magnesia-carbon refractory crucible hearth basin
 * - Dynamic undulating incandescent molten pig iron / molten steel bath (radiating at FULL_BRIGHT)
 * - Porous dark metallurgical slag crust rafts floating on the molten pool
 * - 6 Water-cooled cast copper superheated hot blast tuyere blowpipes
 * - Dynamic active hot air blast flame plasma jets injecting into the hearth during smelting
 * - Overhead insulated toroidal hot blast bustle pipe manifold and downcomers
 * - Overhead conical blast furnace charge throat and distribution bell valve
 */
@OnlyIn(Dist.CLIENT)
public class BlastFurnaceModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/blast_furnace.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;
    private static final int FULL_BRIGHT = 15728880;

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

    // Atlas UV Regions (matching BlastFurnaceAssetGenerator 256x256 layout)
    private static final UVRect UV_STEEL_BEZEL       = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_REFRACTORY_HEARTH = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_MOLTEN_BATH       = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_SLAG_CRUST        = UVRect.of(208, 0, 48, 48);

    private static final UVRect UV_TUYERE_COPPER     = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_BUSTLE_PIPE       = UVRect.of(64, 48, 64, 48);
    private static final UVRect UV_BLAST_FLAME       = UVRect.of(128, 48, 48, 48);
    private static final UVRect UV_CHARGE_BELL       = UVRect.of(176, 48, 48, 48);

    private static final UVRect UV_GUSSET_PLATE      = UVRect.of(0, 96, 48, 48);
    private static final UVRect UV_MULLION_BAR       = UVRect.of(48, 96, 48, 48);
    private static final UVRect UV_FOUNDRY_BADGE     = UVRect.of(96, 96, 48, 48);
    private static final UVRect UV_BASEPLATE_SKID    = UVRect.of(144, 96, 64, 48);

    private final List<Cuboid> staticFacadeCuboids = new ArrayList<>();
    private final List<Cuboid> staticHearthCuboids = new ArrayList<>();
    private final List<Cuboid> staticTuyereCuboids = new ArrayList<>();
    private final List<Cuboid> staticBustleCuboids = new ArrayList<>();

    public BlastFurnaceModel() {
        // =========================================================================
        // 1. FRONT 3x3 WINDOW STRUCTURAL STEEL TRUSS & BEZEL (X: -16..32, Y: 16..64)
        // =========================================================================
        // Bottom window sill I-beam girder
        staticFacadeCuboids.add(Cuboid.uniform(-16.5f, 14.5f, -0.8f, 32.5f, 17.5f, 1.5f, UV_STEEL_BEZEL));
        // Top window lintel I-beam girder
        staticFacadeCuboids.add(Cuboid.uniform(-16.5f, 62.5f, -0.8f, 32.5f, 65.5f, 1.5f, UV_STEEL_BEZEL));
        // Left vertical jamb column
        staticFacadeCuboids.add(Cuboid.uniform(-17.0f, 14.5f, -0.8f, -14.0f, 65.5f, 1.5f, UV_STEEL_BEZEL));
        // Right vertical jamb column
        staticFacadeCuboids.add(Cuboid.uniform(30.0f, 14.5f, -0.8f, 33.0f, 65.5f, 1.5f, UV_STEEL_BEZEL));

        // 4 Riveted corner gusset bracket plates
        staticFacadeCuboids.add(Cuboid.uniform(-17.5f, 14.0f, -1.2f, -11.0f, 20.5f, 1.8f, UV_GUSSET_PLATE));
        staticFacadeCuboids.add(Cuboid.uniform(27.0f, 14.0f, -1.2f, 33.5f, 20.5f, 1.8f, UV_GUSSET_PLATE));
        staticFacadeCuboids.add(Cuboid.uniform(-17.5f, 59.5f, -1.2f, -11.0f, 66.0f, 1.8f, UV_GUSSET_PLATE));
        staticFacadeCuboids.add(Cuboid.uniform(27.0f, 59.5f, -1.2f, 33.5f, 66.0f, 1.8f, UV_GUSSET_PLATE));

        // Center window mullion cross-strut
        staticFacadeCuboids.add(Cuboid.uniform(7.0f, 16.0f, -0.4f, 9.0f, 64.0f, 0.6f, UV_MULLION_BAR));
        staticFacadeCuboids.add(Cuboid.uniform(-15.0f, 39.0f, -0.4f, 31.0f, 41.0f, 0.6f, UV_MULLION_BAR));
        // Center connection rosette hub plate
        staticFacadeCuboids.add(Cuboid.uniform(6.0f, 38.0f, -0.8f, 10.0f, 42.0f, 1.0f, UV_MULLION_BAR));

        // Overhead industrial foundry badge above window
        staticFacadeCuboids.add(Cuboid.uniform(2.0f, 65.5f, -1.0f, 14.0f, 72.0f, 0.8f, UV_FOUNDRY_BADGE));

        // =========================================================================
        // 2. INTERIOR REFRACTORY CRUCIBLE HEARTH BASIN (Y: 16..22, X: -16..32, Z: 16..64)
        // =========================================================================
        // Outer refractory curb walls
        staticHearthCuboids.add(Cuboid.uniform(-15.5f, 16.0f, 16.5f, 31.5f, 21.0f, 21.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(-15.5f, 16.0f, 58.5f, 31.5f, 21.0f, 63.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(-15.5f, 16.0f, 21.5f, -10.5f, 21.0f, 58.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(26.5f, 16.0f, 21.5f, 31.5f, 21.0f, 58.5f, UV_REFRACTORY_HEARTH));

        // Inner stepped refractory ledge
        staticHearthCuboids.add(Cuboid.uniform(-10.5f, 16.0f, 21.5f, 26.5f, 18.5f, 24.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(-10.5f, 16.0f, 55.5f, 26.5f, 18.5f, 58.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(-10.5f, 16.0f, 24.5f, -7.5f, 18.5f, 55.5f, UV_REFRACTORY_HEARTH));
        staticHearthCuboids.add(Cuboid.uniform(23.5f, 16.0f, 24.5f, 26.5f, 18.5f, 55.5f, UV_REFRACTORY_HEARTH));

        // Slag and metal tapping launder notch leading to output port (X: 18..24, Z: 16..22)
        staticHearthCuboids.add(Cuboid.uniform(18.0f, 18.0f, 16.5f, 24.0f, 20.0f, 22.0f, UV_REFRACTORY_HEARTH));

        // =========================================================================
        // 3. SUPERHEATED HOT BLAST TUYERES (CAST COPPER BLOWPIPES)
        // =========================================================================
        // Tuyere 1 (Left-Front, X=-16, Z=30)
        staticTuyereCuboids.add(Cuboid.uniform(-16.0f, 25.5f, 28.0f, -13.5f, 29.5f, 32.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(-13.5f, 23.5f, 28.5f, -6.0f, 26.5f, 31.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(-6.0f, 20.5f, 29.0f, -4.0f, 23.5f, 31.0f, UV_TUYERE_COPPER));

        // Tuyere 2 (Left-Rear, X=-16, Z=50)
        staticTuyereCuboids.add(Cuboid.uniform(-16.0f, 25.5f, 48.0f, -13.5f, 29.5f, 52.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(-13.5f, 23.5f, 48.5f, -6.0f, 26.5f, 51.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(-6.0f, 20.5f, 49.0f, -4.0f, 23.5f, 51.0f, UV_TUYERE_COPPER));

        // Tuyere 3 (Right-Front, X=32, Z=30)
        staticTuyereCuboids.add(Cuboid.uniform(29.5f, 25.5f, 28.0f, 32.0f, 29.5f, 32.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(22.0f, 23.5f, 28.5f, 29.5f, 26.5f, 31.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(20.0f, 20.5f, 29.0f, 22.0f, 23.5f, 31.0f, UV_TUYERE_COPPER));

        // Tuyere 4 (Right-Rear, X=32, Z=50)
        staticTuyereCuboids.add(Cuboid.uniform(29.5f, 25.5f, 48.0f, 32.0f, 29.5f, 52.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(22.0f, 23.5f, 48.5f, 29.5f, 26.5f, 51.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(20.0f, 20.5f, 49.0f, 22.0f, 23.5f, 51.0f, UV_TUYERE_COPPER));

        // Tuyere 5 (Rear-Left, Z=64, X=2)
        staticTuyereCuboids.add(Cuboid.uniform(0.0f, 25.5f, 61.5f, 4.0f, 29.5f, 64.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(0.5f, 23.5f, 54.0f, 3.5f, 26.5f, 61.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(1.0f, 20.5f, 52.0f, 3.0f, 23.5f, 54.0f, UV_TUYERE_COPPER));

        // Tuyere 6 (Rear-Right, Z=64, X=14)
        staticTuyereCuboids.add(Cuboid.uniform(12.0f, 25.5f, 61.5f, 16.0f, 29.5f, 64.0f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(12.5f, 23.5f, 54.0f, 15.5f, 26.5f, 61.5f, UV_TUYERE_COPPER));
        staticTuyereCuboids.add(Cuboid.uniform(13.0f, 20.5f, 52.0f, 15.0f, 23.5f, 54.0f, UV_TUYERE_COPPER));

        // =========================================================================
        // 4. OVERHEAD INSULATED HOT BLAST BUSTLE PIPE & CHARGE HOPPER BELL
        // =========================================================================
        // Toroidal bustle pipe perimeter runners
        staticBustleCuboids.add(Cuboid.uniform(-11.0f, 54.0f, 21.0f, 27.0f, 59.0f, 25.0f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(-11.0f, 54.0f, 55.0f, 27.0f, 59.0f, 59.0f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(-11.0f, 54.0f, 21.0f, -7.0f, 59.0f, 59.0f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(23.0f, 54.0f, 21.0f, 27.0f, 59.0f, 59.0f, UV_BUSTLE_PIPE));

        // 4 Vertical gooseneck downcomers
        staticBustleCuboids.add(Cuboid.uniform(-10.0f, 30.0f, 28.5f, -7.5f, 54.0f, 31.5f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(-10.0f, 30.0f, 48.5f, -7.5f, 54.0f, 51.5f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(23.5f, 30.0f, 28.5f, 26.0f, 54.0f, 31.5f, UV_BUSTLE_PIPE));
        staticBustleCuboids.add(Cuboid.uniform(23.5f, 30.0f, 48.5f, 26.0f, 54.0f, 51.5f, UV_BUSTLE_PIPE));

        // Overhead conical charge hopper bell at ceiling center (X=8, Z=40)
        staticBustleCuboids.add(Cuboid.uniform(3.0f, 58.0f, 35.0f, 13.0f, 64.0f, 45.0f, UV_CHARGE_BELL));
        staticBustleCuboids.add(Cuboid.uniform(4.5f, 55.0f, 36.5f, 11.5f, 58.0f, 43.5f, UV_CHARGE_BELL));
        staticBustleCuboids.add(Cuboid.uniform(7.0f, 52.0f, 39.0f, 9.0f, 64.0f, 41.0f, UV_STEEL_BEZEL));
    }

    public void render(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light,
            boolean isActive,
            int progress,
            long gameTime,
            float partialTicks
    ) {
        float time = (gameTime + partialTicks);

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityCutout(TEXTURE),
                (pose, consumer) -> {
                    Matrix4f matrix = pose.pose();

                    // 1. Static structural facade, hearth, and tuyeres
                    for (Cuboid c : this.staticFacadeCuboids) {
                        emitCuboid(pose, matrix, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                    for (Cuboid c : this.staticHearthCuboids) {
                        emitCuboid(pose, matrix, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                    for (Cuboid c : this.staticTuyereCuboids) {
                        emitCuboid(pose, matrix, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                    for (Cuboid c : this.staticBustleCuboids) {
                        emitCuboid(pose, matrix, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }

                    // 2. Dynamic Molten Metal Bath & Floating Slag
                    float bathY = 19.5f;
                    float wave = 0.0f;
                    if (isActive) {
                        wave = Mth.sin(time * 0.16f) * 0.40f;
                        bathY += wave;
                    }

                    // Molten pig iron / molten steel pool
                    Cuboid moltenPool = Cuboid.uniform(-7.2f, 16.0f, 24.8f, 23.2f, bathY, 55.2f, UV_MOLTEN_BATH);
                    if (isActive) {
                        // Incandescent pulsing molten radiance
                        float pulse = 0.90f + 0.10f * Mth.sin(time * 0.25f);
                        emitCuboidColored(pose, matrix, consumer, moltenPool, 1.0f, pulse, pulse * 0.85f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                    } else {
                        // Solidifying dark hot metal crust
                        emitCuboidColored(pose, matrix, consumer, moltenPool, 0.70f, 0.55f, 0.45f, 1.0f, light, OverlayTexture.NO_OVERLAY);
                    }

                    // Floating dark slag crust patches bobbing on the molten metal
                    float slagY = bathY + 0.05f;
                    Cuboid slag1 = Cuboid.uniform(-3.0f, slagY, 28.0f, 5.0f, slagY + 0.35f, 36.0f, UV_SLAG_CRUST);
                    Cuboid slag2 = Cuboid.uniform(11.0f, slagY, 32.0f, 19.0f, slagY + 0.35f, 40.0f, UV_SLAG_CRUST);
                    Cuboid slag3 = Cuboid.uniform(1.0f, slagY, 44.0f, 9.0f, slagY + 0.35f, 51.0f, UV_SLAG_CRUST);
                    Cuboid slag4 = Cuboid.uniform(13.0f, slagY, 46.0f, 21.0f, slagY + 0.35f, 53.0f, UV_SLAG_CRUST);
                    emitCuboid(pose, matrix, consumer, slag1, light, OverlayTexture.NO_OVERLAY);
                    emitCuboid(pose, matrix, consumer, slag2, light, OverlayTexture.NO_OVERLAY);
                    emitCuboid(pose, matrix, consumer, slag3, light, OverlayTexture.NO_OVERLAY);
                    emitCuboid(pose, matrix, consumer, slag4, light, OverlayTexture.NO_OVERLAY);

                    // 3. Superheated Hot Blast Flame Jets injecting from Tuyeres (when active)
                    if (isActive) {
                        float flamePulse = Mth.sin(time * 0.45f) * 0.5f;

                        // Flame jets from 6 tuyere tips into hearth
                        // Left-Front Tuyere
                        Cuboid jetL1 = Cuboid.uniform(-4.0f, 19.0f, 29.2f, -1.0f + flamePulse, 22.0f, 30.8f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetL1, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                        // Left-Rear Tuyere
                        Cuboid jetL2 = Cuboid.uniform(-4.0f, 19.0f, 49.2f, -1.0f + flamePulse, 22.0f, 50.8f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetL2, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                        // Right-Front Tuyere
                        Cuboid jetR1 = Cuboid.uniform(17.0f - flamePulse, 19.0f, 29.2f, 20.0f, 22.0f, 30.8f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetR1, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                        // Right-Rear Tuyere
                        Cuboid jetR2 = Cuboid.uniform(17.0f - flamePulse, 19.0f, 49.2f, 20.0f, 22.0f, 50.8f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetR2, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                        // Rear-Left Tuyere
                        Cuboid jetB1 = Cuboid.uniform(1.2f, 19.0f, 48.0f - flamePulse, 2.8f, 22.0f, 52.0f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetB1, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

                        // Rear-Right Tuyere
                        Cuboid jetB2 = Cuboid.uniform(13.2f, 19.0f, 48.0f - flamePulse, 14.8f, 22.0f, 52.0f, UV_BLAST_FLAME);
                        emitCuboidColored(pose, matrix, consumer, jetB2, 1.0f, 1.0f, 0.9f, 1.0f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                    }
                }
        );
    }

    private static void emitCuboid(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            int light,
            int overlay
    ) {
        emitCuboidColored(pose, matrix, consumer, c, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay);
    }

    private static void emitCuboidColored(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
            float r, float g, float b, float a,
            int light,
            int overlay
    ) {
        float x0 = c.x0() / 16.0f;
        float y0 = c.y0() / 16.0f;
        float z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f;
        float y1 = c.y1() / 16.0f;
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
        emitQuad(pose, matrix, consumer,
                x0, y0, z1,
                x0, y0, z0,
                x1, y0, z0,
                x1, y0, z1,
                c.down(), 0.0f, -1.0f, 0.0f, r, g, b, a, light, overlay);
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            UVRect uv,
            float nx, float ny, float nz,
            float r, float g, float b, float a,
            int light,
            int overlay
    ) {
        consumer.addVertex(matrix, x0, y0, z0)
                .setColor(r, g, b, a)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(r, g, b, a)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(r, g, b, a)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x3, y3, z3)
                .setColor(r, g, b, a)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
