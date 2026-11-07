package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity 3x3x3 Monolithic Model for the Assembled Heavy Pit Quarry Excavator:
 * - Heavy structural steel crawler tracks and I-beam base skid with 4 hydraulic outriggers
 * - 3 Low-profile conveyor discharge arms (Rear, Left, Right) at 5px height aligning flush with mod conveyors
 * - Heavy slewing ring turntable with diamond treadplate skid deck
 * - Main electric winch drum with braided high-tensile steel wire cable
 * - Reinforced operator control cabin with digital touchscreens, dual joysticks, and tinted safety glass
 * - Rear ballast / counterweight enclosure with yellow/black hazard chevrons
 * - Electric motor, radiator cooling fins, and hydraulic power pack
 * - High structural lattice A-frame gantry tower rising to Y=46
 * - Front cantilevered excavation boom mast with crown cable sheaves
 * - DUAL HIGH-INTENSITY HALOGEN WORK FLOODLIGHTS angled down into the excavation pit
 * - Amber rotating hazard warning strobe beacon
 */
@OnlyIn(Dist.CLIENT)
public class QuarryModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/quarry.png"
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

    // Atlas UV Regions (matching QuarryAssetGenerator 256x256 layout)
    private static final UVRect UV_ENAMEL_PLATES     = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_LATTICE_STEEL     = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_WINCH_DRUM        = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_TREADPLATE        = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_CABIN_CONSOLE     = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_CABIN_AWNING      = UVRect.of(64, 48, 48, 48);
    private static final UVRect UV_SAFETY_GLASS      = UVRect.of(112, 48, 48, 48);
    private static final UVRect UV_COUNTERWEIGHT     = UVRect.of(160, 48, 48, 48);
    private static final UVRect UV_RADIATOR_FINS     = UVRect.of(208, 48, 48, 48);
    private static final UVRect UV_FLOODLIGHT_LENS   = UVRect.of(0, 96, 48, 48);
    private static final UVRect UV_FLOODLIGHT_HOUSING= UVRect.of(48, 96, 48, 48);
    private static final UVRect UV_STROBE_ACTIVE     = UVRect.of(96, 96, 24, 24);
    private static final UVRect UV_STROBE_IDLE       = UVRect.of(120, 96, 24, 24);
    private static final UVRect UV_HAZARD_STRIPES    = UVRect.of(144, 96, 64, 48);
    private static final UVRect UV_CHEVRON_BELT      = UVRect.of(208, 96, 48, 48);
    private static final UVRect UV_CONVEYOR_SKIRT    = UVRect.of(0, 144, 64, 48);
    private static final UVRect UV_CHROME_CYLINDERS  = UVRect.of(64, 144, 48, 48);
    private static final UVRect UV_MOTOR_GEARBOX     = UVRect.of(112, 144, 64, 48);
    private static final UVRect UV_CRAWLER_TRACKS    = UVRect.of(176, 144, 80, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid strobeActive;
    private final Cuboid strobeIdle;
    private final Cuboid floodlightLeftLens;
    private final Cuboid floodlightRightLens;
    private final Cuboid floodlightRearLeftLens;
    private final Cuboid floodlightRearRightLens;

    public QuarryModel() {
        // =========================================================================
        // 1. CRAWLER TRACKS & STRUCTURAL BASE SKID (Y: 0..8)
        // =========================================================================
        // Left Track Assembly
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 2.0f, -2.0f, 7.5f, 46.0f, UV_CRAWLER_TRACKS));
        // Right Track Assembly
        staticCuboids.add(Cuboid.uniform(18.0f, 0.0f, 2.0f, 30.0f, 7.5f, 46.0f, UV_CRAWLER_TRACKS));

        // 4 Heavy Corner Hydraulic Outrigger Plinths
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 0.0f, -1.0f, 4.0f, 5.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(17.0f, 0.0f, 0.0f, 31.0f, 4.0f, 5.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 43.0f, -1.0f, 4.0f, 48.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(17.0f, 0.0f, 43.0f, 31.0f, 4.0f, 48.0f, UV_LATTICE_STEEL));

        // Transverse Heavy I-Beam Cross Members (connecting tracks)
        staticCuboids.add(Cuboid.uniform(-2.0f, 2.0f, 6.0f, 18.0f, 7.0f, 10.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(-2.0f, 2.0f, 38.0f, 18.0f, 7.0f, 42.0f, UV_LATTICE_STEEL));

        // Central Turntable Slewing Base & Treadplate Deck (Y: 7.5..10)
        staticCuboids.add(Cuboid.uniform(-4.0f, 7.5f, 4.0f, 20.0f, 10.0f, 44.0f, UV_TREADPLATE));

        // =========================================================================
        // 2. CONVEYOR DISCHARGE ARMS (FLUSH 5px BED HEIGHT)
        // =========================================================================
        // A. LEFT CONVEYOR CHUTE (X: -16..-2, Y: 0..5, Z: 18..26)
        staticCuboids.add(Cuboid.uniform(-16.0f, 0.0f, 18.0f, -2.0f, 5.0f, 26.0f, UV_CHEVRON_BELT));
        // Left Conveyor Side Skirts
        staticCuboids.add(Cuboid.uniform(-16.0f, 0.0f, 17.0f, -2.0f, 7.0f, 18.0f, UV_CONVEYOR_SKIRT));
        staticCuboids.add(Cuboid.uniform(-16.0f, 0.0f, 26.0f, -2.0f, 7.0f, 27.0f, UV_CONVEYOR_SKIRT));

        // B. RIGHT CONVEYOR CHUTE (X: 18..32, Y: 0..5, Z: 18..26)
        staticCuboids.add(Cuboid.uniform(18.0f, 0.0f, 18.0f, 32.0f, 5.0f, 26.0f, UV_CHEVRON_BELT));
        // Right Conveyor Side Skirts
        staticCuboids.add(Cuboid.uniform(18.0f, 0.0f, 17.0f, 32.0f, 7.0f, 18.0f, UV_CONVEYOR_SKIRT));
        staticCuboids.add(Cuboid.uniform(18.0f, 0.0f, 26.0f, 32.0f, 7.0f, 27.0f, UV_CONVEYOR_SKIRT));

        // C. BUILT-IN REAR CONVEYOR CHUTE (PIT SPOIL EJECTOR) (X: 2..14, Y: 0..5, Z: 32..48)
        staticCuboids.add(Cuboid.uniform(2.0f, 0.0f, 32.0f, 14.0f, 5.0f, 48.0f, UV_CHEVRON_BELT));
        // Rear Conveyor Side Skirts
        staticCuboids.add(Cuboid.uniform(1.0f, 0.0f, 32.0f, 2.0f, 7.0f, 48.0f, UV_CONVEYOR_SKIRT));
        staticCuboids.add(Cuboid.uniform(14.0f, 0.0f, 32.0f, 15.0f, 7.0f, 48.0f, UV_CONVEYOR_SKIRT));

        // =========================================================================
        // 3. OPERATOR CABIN (FRONT-LEFT, Y: 10..28)
        // =========================================================================
        staticCuboids.add(new Cuboid(
                -13.0f, 10.0f, 4.0f,
                -1.0f, 26.0f, 20.0f,
                UV_CABIN_CONSOLE, // North
                UV_ENAMEL_PLATES, // South
                UV_SAFETY_GLASS,  // West
                UV_ENAMEL_PLATES, // East
                UV_CABIN_AWNING,  // Up
                UV_TREADPLATE     // Down
        ));

        // Roof Sunshade Overhang / Visor
        staticCuboids.add(Cuboid.uniform(-14.0f, 26.0f, 2.0f, 0.0f, 27.5f, 22.0f, UV_CABIN_AWNING));

        // =========================================================================
        // 4. MAIN WINCH DRUM & MACHINERY DECK (CENTER, Y: 10..26)
        // =========================================================================
        // Winch Cable Drum Assembly
        staticCuboids.add(Cuboid.uniform(1.0f, 12.0f, 16.0f, 15.0f, 24.0f, 28.0f, UV_WINCH_DRUM));
        // Winch Heavy Pillow Block Bearings
        staticCuboids.add(Cuboid.uniform(0.0f, 10.0f, 18.0f, 2.0f, 22.0f, 26.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(14.0f, 10.0f, 18.0f, 16.0f, 22.0f, 26.0f, UV_LATTICE_STEEL));

        // Electric Drive Motor & Gearbox
        staticCuboids.add(Cuboid.uniform(15.0f, 11.0f, 18.0f, 21.0f, 21.0f, 26.0f, UV_MOTOR_GEARBOX));

        // Hydraulic Pump & Turbo Engine Housing
        staticCuboids.add(new Cuboid(
                0.0f, 10.0f, 28.0f,
                16.0f, 25.0f, 38.0f,
                UV_ENAMEL_PLATES, // North
                UV_RADIATOR_FINS, // South
                UV_RADIATOR_FINS, // West
                UV_RADIATOR_FINS, // East
                UV_ENAMEL_PLATES, // Up
                UV_TREADPLATE     // Down
        ));

        // =========================================================================
        // 5. REAR BALLAST / COUNTERWEIGHT (Y: 10..26, Z: 38..46)
        // =========================================================================
        staticCuboids.add(new Cuboid(
                -4.0f, 10.0f, 38.0f,
                20.0f, 24.0f, 46.0f,
                UV_COUNTERWEIGHT,  // North
                UV_HAZARD_STRIPES, // South
                UV_COUNTERWEIGHT,  // West
                UV_COUNTERWEIGHT,  // East
                UV_COUNTERWEIGHT,  // Up
                UV_TREADPLATE      // Down
        ));

        // =========================================================================
        // 6. STRUCTURAL LATTICE A-FRAME GANTRY TOWER (Y: 10..46)
        // =========================================================================
        staticCuboids.add(Cuboid.uniform(-3.0f, 10.0f, 8.0f, 0.0f, 42.0f, 11.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(16.0f, 10.0f, 8.0f, 19.0f, 42.0f, 11.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(-3.0f, 10.0f, 34.0f, 0.0f, 42.0f, 37.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(16.0f, 10.0f, 34.0f, 19.0f, 42.0f, 37.0f, UV_LATTICE_STEEL));

        // Upper Gantry Apex Beams & Sheave Platform (Y: 41..44)
        staticCuboids.add(Cuboid.uniform(-3.0f, 41.0f, 8.0f, 19.0f, 44.0f, 12.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(-3.0f, 41.0f, 33.0f, 19.0f, 44.0f, 37.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(-3.0f, 41.0f, 12.0f, 1.0f, 44.0f, 33.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(15.0f, 41.0f, 12.0f, 19.0f, 44.0f, 33.0f, UV_LATTICE_STEEL));

        // Front Cantilever Boom Mast & Sheaves
        staticCuboids.add(Cuboid.uniform(1.0f, 36.0f, -4.0f, 4.0f, 42.0f, 9.0f, UV_LATTICE_STEEL));
        staticCuboids.add(Cuboid.uniform(12.0f, 36.0f, -4.0f, 15.0f, 42.0f, 9.0f, UV_LATTICE_STEEL));
        // Boom Crown Pulleys
        staticCuboids.add(Cuboid.uniform(4.0f, 37.0f, -6.0f, 12.0f, 41.0f, -2.0f, UV_WINCH_DRUM));

        // Floodlight Housings (Left & Right - Front)
        staticCuboids.add(Cuboid.uniform(-6.0f, 37.0f, 4.0f, -1.0f, 43.0f, 8.0f, UV_FLOODLIGHT_HOUSING));
        staticCuboids.add(Cuboid.uniform(17.0f, 37.0f, 4.0f, 22.0f, 43.0f, 8.0f, UV_FLOODLIGHT_HOUSING));

        // Rear Floodlight Housings (Left & Right - Facing Pit)
        staticCuboids.add(Cuboid.uniform(-6.0f, 37.0f, 35.0f, -1.0f, 43.0f, 39.0f, UV_FLOODLIGHT_HOUSING));
        staticCuboids.add(Cuboid.uniform(17.0f, 37.0f, 35.0f, 22.0f, 43.0f, 39.0f, UV_FLOODLIGHT_HOUSING));

        // Strobe Beacon Mounting Stem
        staticCuboids.add(Cuboid.uniform(-8.0f, 27.5f, 8.0f, -5.0f, 30.0f, 11.0f, UV_LATTICE_STEEL));

        // Dynamic Elements:
        this.strobeActive = Cuboid.uniform(-8.5f, 30.0f, 7.5f, -4.5f, 34.0f, 11.5f, UV_STROBE_ACTIVE);
        this.strobeIdle   = Cuboid.uniform(-8.5f, 30.0f, 7.5f, -4.5f, 34.0f, 11.5f, UV_STROBE_IDLE);

        // Floodlight Lenses (Front North Faces)
        this.floodlightLeftLens  = Cuboid.uniform(-5.5f, 37.5f, 3.8f, -1.5f, 42.5f, 4.2f, UV_FLOODLIGHT_LENS);
        this.floodlightRightLens = Cuboid.uniform(17.5f, 37.5f, 3.8f, 21.5f, 42.5f, 4.2f, UV_FLOODLIGHT_LENS);

        // Rear Floodlight Lenses (Facing Pit South Faces)
        this.floodlightRearLeftLens  = Cuboid.uniform(-5.5f, 37.5f, 38.8f, -1.5f, 42.5f, 39.2f, UV_FLOODLIGHT_LENS);
        this.floodlightRearRightLens = Cuboid.uniform(17.5f, 37.5f, 38.8f, 21.5f, 42.5f, 39.2f, UV_FLOODLIGHT_LENS);
    }

    public void render(PoseStack.Pose pose, VertexConsumer consumer, boolean active, int light, int overlay) {
        Matrix4f matrix = pose.pose();

        // 1. Render all static machine body cuboids
        for (Cuboid c : this.staticCuboids) {
            emitCuboid(pose, matrix, consumer, c, light, overlay);
        }

        // 2. Render Warning Amber Strobe Beacon
        if (active) {
            emitCuboid(pose, matrix, consumer, this.strobeActive, FULL_BRIGHT, overlay);
        } else {
            emitCuboid(pose, matrix, consumer, this.strobeIdle, light, overlay);
        }

        // 3. Render High-Intensity Halogen Work Floodlight Lenses (Front & Rear)
        int floodLight = active ? FULL_BRIGHT : light;
        emitCuboid(pose, matrix, consumer, this.floodlightLeftLens, floodLight, overlay);
        emitCuboid(pose, matrix, consumer, this.floodlightRightLens, floodLight, overlay);
        emitCuboid(pose, matrix, consumer, this.floodlightRearLeftLens, floodLight, overlay);
        emitCuboid(pose, matrix, consumer, this.floodlightRearRightLens, floodLight, overlay);
    }

    private void emitCuboid(PoseStack.Pose pose, Matrix4f matrix, VertexConsumer consumer, Cuboid c, int light, int overlay) {
        float x0 = c.x0() / 16.0f;
        float y0 = c.y0() / 16.0f;
        float z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f;
        float y1 = c.y1() / 16.0f;
        float z1 = c.z1() / 16.0f;

        // North Face (Z = z0)
        emitQuad(pose, matrix, consumer, x0, y1, z0, x1, y1, z0, x1, y0, z0, x0, y0, z0, 0, 0, -1, c.north(), light, overlay);
        // South Face (Z = z1)
        emitQuad(pose, matrix, consumer, x1, y1, z1, x0, y1, z1, x0, y0, z1, x1, y0, z1, 0, 0, 1, c.south(), light, overlay);
        // West Face (X = x0)
        emitQuad(pose, matrix, consumer, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1, -1, 0, 0, c.west(), light, overlay);
        // East Face (X = x1)
        emitQuad(pose, matrix, consumer, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0, 1, 0, 0, c.east(), light, overlay);
        // Up Face (Y = y1)
        emitQuad(pose, matrix, consumer, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0, c.up(), light, overlay);
        // Down Face (Y = y0)
        emitQuad(pose, matrix, consumer, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0, c.down(), light, overlay);
    }

    private void emitQuad(PoseStack.Pose pose, Matrix4f matrix, VertexConsumer consumer,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float nx, float ny, float nz,
                          UVRect uv, int light, int overlay) {
        consumer.addVertex(matrix, x0, y0, z0)
                .setColor(0xFF, 0xFF, 0xFF, 0xFF)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(0xFF, 0xFF, 0xFF, 0xFF)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(0xFF, 0xFF, 0xFF, 0xFF)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, x3, y3, z3)
                .setColor(0xFF, 0xFF, 0xFF, 0xFF)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
