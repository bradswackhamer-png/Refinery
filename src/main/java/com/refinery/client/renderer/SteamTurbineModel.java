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

/**
 * High-Fidelity 3x3x3 Monolithic Model for the Assembled Steam Turbine:
 * Replicating the industrial turbine-generator skid:
 * - Structural heavy steel I-beam skid base with cross-members and foundation anchor pads
 * - Large electric AC generator / alternator unit with stator cooling louvers, top terminal
 *   connection box, end bell air intake cowl, and drive-end bearing bracket
 * - Intermediate reduction gearbox with lube inspection tag and shaft coupling guard
 * - High-pressure steam turbine horizontally split casing with domed shell, bolted perimeter flange,
 *   circular bearing end covers, and crane lifting eye lugs
 * - Front flanged steam inlet nozzle chest with bolt ring, dark steam bore, and governor valve actuator
 * - Upright control console with upper blue gauge box (triple analog pressure/RPM/vacuum dials)
 *   and lower silver electrical cabinet with red emergency stop mushroom button, switches, and
 *   dynamic glowing status indicators
 * - Rear condenser exhaust duct leading to the condensate water discharge port
 */
@OnlyIn(Dist.CLIENT)
public class SteamTurbineModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/steam_turbine.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;

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

    // Atlas UV Regions (matching TurbineAssetGenerator 256x256 layout)
    private static final UVRect UV_TURBINE_CASING   = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_BEARING_PEDESTAL = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_SKID_STEEL       = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_GEARBOX_STEEL    = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_GEN_STATOR       = UVRect.of(0, 48, 64, 48);
    private static final UVRect UV_GEN_END_BELL     = UVRect.of(64, 48, 48, 48);
    private static final UVRect UV_GEN_TERMINAL     = UVRect.of(112, 48, 48, 48);
    private static final UVRect UV_INLET_CHEST      = UVRect.of(160, 48, 48, 48);
    private static final UVRect UV_GOVERNOR         = UVRect.of(208, 48, 48, 48);
    private static final UVRect UV_CONTROL_CABINET  = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_GAUGE_BOX        = UVRect.of(64, 96, 48, 48);
    private static final UVRect UV_TRIPLE_GAUGES    = UVRect.of(112, 96, 48, 48);
    private static final UVRect UV_ACTIVE_INDICATOR = UVRect.of(160, 96, 48, 48);
    private static final UVRect UV_SHAFTS_TUBING    = UVRect.of(208, 96, 48, 48);
    private static final UVRect UV_CONDENSER_DUCT   = UVRect.of(0, 144, 96, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid activeRunIndicator;
    private final Cuboid idleStandbyIndicator;

    public SteamTurbineModel() {
        // =========================================================================
        // 1. STRUCTURAL BASE SKID & ANCHOR FOOTINGS (Y: 0..4)
        // =========================================================================
        // Left longitudinal I-beam runner
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 1.0f, -10.0f, 4.0f, 47.0f, UV_SKID_STEEL));
        // Right longitudinal I-beam runner
        staticCuboids.add(Cuboid.uniform(26.0f, 0.0f, 1.0f, 30.0f, 4.0f, 47.0f, UV_SKID_STEEL));
        // Center longitudinal support beam
        staticCuboids.add(Cuboid.uniform(6.0f, 0.0f, 1.0f, 10.0f, 4.0f, 47.0f, UV_SKID_STEEL));

        // Transverse heavy cross-beams
        staticCuboids.add(Cuboid.uniform(-10.0f, 0.0f, 1.0f, 26.0f, 4.0f, 5.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-10.0f, 0.0f, 14.0f, 26.0f, 4.0f, 18.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-10.0f, 0.0f, 28.0f, 26.0f, 4.0f, 32.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-10.0f, 0.0f, 43.0f, 26.0f, 4.0f, 47.0f, UV_SKID_STEEL));

        // Foundation mounting anchor footings & shims
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 0.0f, -9.0f, 1.0f, 6.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(25.0f, 0.0f, 0.0f, 31.0f, 1.0f, 6.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 42.0f, -9.0f, 1.0f, 48.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(25.0f, 0.0f, 42.0f, 31.0f, 1.0f, 48.0f, UV_SKID_STEEL));

        // =========================================================================
        // 2. ELECTRIC AC GENERATOR / ALTERNATOR (Left Side, X: -13..1, Y: 4..35)
        // =========================================================================
        // Generator foot support risers
        staticCuboids.add(Cuboid.uniform(-13.0f, 4.0f, 10.0f, 1.0f, 7.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-13.0f, 4.0f, 32.0f, 1.0f, 7.0f, 36.0f, UV_SKID_STEEL));

        // Stator lower cradle bed
        staticCuboids.add(Cuboid.uniform(-12.5f, 7.0f, 9.0f, 0.5f, 11.0f, 37.0f, UV_GEN_STATOR));

        // Main cylindrical stator body (multi-tier octagonal casing profile)
        staticCuboids.add(Cuboid.uniform(-12.0f, 10.0f, 9.0f, 0.0f, 24.0f, 37.0f, UV_GEN_STATOR));
        staticCuboids.add(Cuboid.uniform(-11.0f, 8.0f, 10.0f, -1.0f, 10.0f, 36.0f, UV_GEN_STATOR));
        staticCuboids.add(Cuboid.uniform(-11.0f, 24.0f, 10.0f, -1.0f, 26.0f, 36.0f, UV_GEN_STATOR));

        // Outer stator cooling louver rib plates
        staticCuboids.add(Cuboid.uniform(-13.0f, 12.0f, 11.0f, -12.0f, 22.0f, 35.0f, UV_GEN_STATOR));
        staticCuboids.add(Cuboid.uniform(0.0f, 12.0f, 11.0f, 1.0f, 22.0f, 35.0f, UV_GEN_STATOR));

        // Rear cooling air intake cowl & perforated mesh
        staticCuboids.add(new Cuboid(
                -12.0f, 9.0f, 37.0f,
                0.0f, 25.0f, 41.0f,
                UV_GEN_STATOR, UV_GEN_END_BELL,
                UV_GEN_END_BELL, UV_GEN_END_BELL,
                UV_GEN_STATOR, UV_GEN_STATOR
        ));

        // Top rectangular terminal connection / junction box
        staticCuboids.add(new Cuboid(
                -11.0f, 26.0f, 14.0f,
                -1.0f, 34.0f, 32.0f,
                UV_GEN_TERMINAL, UV_GEN_TERMINAL,
                UV_GEN_TERMINAL, UV_GEN_TERMINAL,
                UV_GEN_TERMINAL, UV_GEN_TERMINAL
        ));
        // Junction box lid flange
        staticCuboids.add(Cuboid.uniform(-11.5f, 34.0f, 13.5f, -0.5f, 35.5f, 32.5f, UV_GEN_TERMINAL));

        // Generator drive-end bearing pedestal
        staticCuboids.add(Cuboid.uniform(-11.0f, 5.0f, 7.0f, -1.0f, 17.0f, 9.0f, UV_BEARING_PEDESTAL));
        // Generator drive shaft
        staticCuboids.add(Cuboid.uniform(-7.0f, 14.5f, 5.0f, -5.0f, 17.5f, 7.0f, UV_SHAFTS_TUBING));

        // =========================================================================
        // 3. INTERMEDIATE GEARBOX & SHAFT COUPLING GUARDS (X: 1..8, Y: 4..21)
        // =========================================================================
        // Gearbox mounting pedestal
        staticCuboids.add(Cuboid.uniform(1.5f, 4.0f, 15.0f, 7.5f, 8.0f, 31.0f, UV_SKID_STEEL));
        // Main gear casing
        staticCuboids.add(new Cuboid(
                1.5f, 8.0f, 16.0f,
                7.5f, 19.0f, 30.0f,
                UV_GEARBOX_STEEL, UV_GEARBOX_STEEL,
                UV_GEARBOX_STEEL, UV_GEARBOX_STEEL,
                UV_GEARBOX_STEEL, UV_GEARBOX_STEEL
        ));
        // Gearbox top inspection cover plate
        staticCuboids.add(Cuboid.uniform(2.0f, 19.0f, 18.0f, 7.0f, 20.5f, 28.0f, UV_GEARBOX_STEEL));

        // Flexible coupling guard (between generator & gearbox)
        staticCuboids.add(Cuboid.uniform(-1.0f, 12.0f, 20.0f, 1.5f, 20.0f, 26.0f, UV_GEARBOX_STEEL));
        // High-speed coupling guard (between gearbox & turbine)
        staticCuboids.add(Cuboid.uniform(7.5f, 12.0f, 20.0f, 9.5f, 20.0f, 26.0f, UV_GEARBOX_STEEL));

        // Side oil sight level gauge
        staticCuboids.add(Cuboid.uniform(0.8f, 11.0f, 25.5f, 1.5f, 15.0f, 28.5f, UV_GEARBOX_STEEL));

        // =========================================================================
        // 4. HIGH-PRESSURE STEAM TURBINE (Center-Right, X: 8..23, Y: 4..28)
        // =========================================================================
        // Front & rear rotor bearing pedestals
        staticCuboids.add(Cuboid.uniform(10.0f, 4.0f, 10.0f, 20.0f, 16.0f, 14.0f, UV_BEARING_PEDESTAL));
        staticCuboids.add(Cuboid.uniform(10.0f, 4.0f, 33.0f, 20.0f, 16.0f, 37.0f, UV_BEARING_PEDESTAL));

        // Circular bearing inspection end-cap covers (matching reference image)
        staticCuboids.add(Cuboid.uniform(12.0f, 11.5f, 9.0f, 18.0f, 17.5f, 10.0f, UV_BEARING_PEDESTAL));
        staticCuboids.add(Cuboid.uniform(12.0f, 11.5f, 37.0f, 18.0f, 17.5f, 38.0f, UV_BEARING_PEDESTAL));

        // Lower turbine split-casing half
        staticCuboids.add(Cuboid.uniform(9.0f, 10.0f, 14.0f, 22.0f, 16.5f, 33.0f, UV_TURBINE_CASING));

        // Heavy bolted horizontal split flange (prominent split line around perimeter)
        staticCuboids.add(Cuboid.uniform(8.0f, 16.0f, 13.0f, 23.0f, 17.5f, 34.0f, UV_TURBINE_CASING));

        // Upper domed turbine casing half
        staticCuboids.add(Cuboid.uniform(9.5f, 17.5f, 14.5f, 21.5f, 25.0f, 32.5f, UV_TURBINE_CASING));
        // Upper crown curvature
        staticCuboids.add(Cuboid.uniform(11.0f, 25.0f, 16.0f, 20.0f, 27.5f, 31.0f, UV_TURBINE_CASING));

        // Crane lifting eye lugs on top of turbine dome
        staticCuboids.add(Cuboid.uniform(14.5f, 27.5f, 18.0f, 16.5f, 30.5f, 20.0f, UV_SHAFTS_TUBING));
        staticCuboids.add(Cuboid.uniform(14.5f, 27.5f, 27.0f, 16.5f, 30.5f, 29.0f, UV_SHAFTS_TUBING));

        // =========================================================================
        // 5. FLANGED STEAM INLET CHEST & GOVERNOR ACTUATOR (Front, X: 1..14, Z: 2..12)
        // =========================================================================
        // Steam inlet valve chest body
        staticCuboids.add(Cuboid.uniform(7.0f, 6.0f, 5.0f, 14.0f, 17.0f, 12.0f, UV_INLET_CHEST));

        // Circular flanged steam inlet nozzle facing front (prominent in photo)
        staticCuboids.add(Cuboid.uniform(8.0f, 8.0f, 3.0f, 13.0f, 13.0f, 5.0f, UV_INLET_CHEST));
        staticCuboids.add(new Cuboid(
                7.0f, 7.0f, 1.5f,
                14.0f, 14.0f, 3.0f,
                UV_INLET_CHEST, UV_INLET_CHEST,
                UV_INLET_CHEST, UV_INLET_CHEST,
                UV_INLET_CHEST, UV_INLET_CHEST
        ));
        // Inner hollow steam bore
        staticCuboids.add(Cuboid.uniform(8.5f, 8.5f, 1.0f, 12.5f, 12.5f, 1.5f, UV_INLET_CHEST));

        // Governor valve actuator assembly (on top of chest)
        staticCuboids.add(Cuboid.uniform(8.5f, 17.0f, 7.0f, 12.5f, 20.0f, 11.0f, UV_GOVERNOR));
        staticCuboids.add(Cuboid.uniform(8.0f, 20.0f, 6.5f, 13.0f, 26.0f, 11.5f, UV_GOVERNOR));
        // Actuator vertical stem & linkage arm
        staticCuboids.add(Cuboid.uniform(10.0f, 26.0f, 8.5f, 11.0f, 29.0f, 9.5f, UV_SHAFTS_TUBING));
        staticCuboids.add(Cuboid.uniform(10.0f, 28.5f, 8.5f, 16.0f, 29.5f, 9.5f, UV_GOVERNOR));

        // Emergency manual trip valve stem & red handwheel
        staticCuboids.add(Cuboid.uniform(14.0f, 9.5f, 7.5f, 16.5f, 11.5f, 9.5f, UV_SHAFTS_TUBING));
        staticCuboids.add(Cuboid.uniform(16.5f, 8.0f, 6.0f, 17.5f, 13.0f, 11.0f, UV_GOVERNOR));

        // =========================================================================
        // 6. UPRIGHT INDUSTRIAL CONTROL CONSOLE (Right Side, X: 22..30.5, Y: 4..38)
        // =========================================================================
        // Mounting post / pedestal column
        staticCuboids.add(Cuboid.uniform(25.0f, 4.0f, 7.0f, 28.0f, 14.0f, 10.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(24.0f, 4.0f, 6.0f, 29.0f, 5.5f, 11.0f, UV_SKID_STEEL));

        // Lower Silver / Light-Grey Electrical Cabinet
        staticCuboids.add(new Cuboid(
                22.0f, 14.0f, 4.0f,
                30.0f, 26.0f, 13.0f,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));
        // Cabinet front face with door trim, E-stop, selector switches
        staticCuboids.add(new Cuboid(
                22.0f, 14.0f, 3.2f,
                30.0f, 26.0f, 4.0f,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET
        ));

        // 3D protruding emergency stop button (Red mushroom with yellow collar)
        staticCuboids.add(Cuboid.uniform(27.5f, 16.0f, 2.8f, 29.5f, 18.0f, 3.2f, UV_GOVERNOR));
        staticCuboids.add(Cuboid.uniform(27.8f, 16.3f, 2.3f, 29.2f, 17.7f, 2.8f, UV_GOVERNOR));

        // Upper Blue Gauge Box Enclosure
        staticCuboids.add(Cuboid.uniform(22.5f, 26.0f, 5.0f, 29.5f, 38.0f, 12.0f, UV_GAUGE_BOX));
        // Triple analog dials front face
        staticCuboids.add(new Cuboid(
                22.5f, 26.0f, 4.5f,
                29.5f, 38.0f, 5.0f,
                UV_TRIPLE_GAUGES, UV_GAUGE_BOX,
                UV_GAUGE_BOX, UV_GAUGE_BOX,
                UV_GAUGE_BOX, UV_GAUGE_BOX
        ));

        // Triple 3D protruding analog dial gauge bezels
        staticCuboids.add(Cuboid.uniform(23.2f, 33.0f, 4.0f, 25.0f, 36.5f, 4.5f, UV_TRIPLE_GAUGES));
        staticCuboids.add(Cuboid.uniform(25.3f, 33.0f, 4.0f, 27.1f, 36.5f, 4.5f, UV_TRIPLE_GAUGES));
        staticCuboids.add(Cuboid.uniform(27.4f, 33.0f, 4.0f, 29.2f, 36.5f, 4.5f, UV_TRIPLE_GAUGES));

        // Electrical conduit running from cabinet down to skid
        staticCuboids.add(Cuboid.uniform(24.5f, 4.0f, 10.0f, 26.0f, 14.0f, 11.5f, UV_SHAFTS_TUBING));

        // =========================================================================
        // 7. AUXILIARY PIPING & REAR CONDENSER EXHAUST DUCT
        // =========================================================================
        // Instrumentation tubing run from turbine casing to gauge console
        staticCuboids.add(Cuboid.uniform(18.0f, 28.0f, 9.0f, 23.0f, 29.0f, 10.0f, UV_SHAFTS_TUBING));
        staticCuboids.add(Cuboid.uniform(22.5f, 29.0f, 9.0f, 23.5f, 34.0f, 10.0f, UV_SHAFTS_TUBING));
        staticCuboids.add(Cuboid.uniform(7.0f, 21.0f, 6.0f, 18.0f, 22.0f, 7.0f, UV_SHAFTS_TUBING));

        // Rear low-pressure steam exhaust duct leading to water condensation port (Z: 33..46)
        staticCuboids.add(Cuboid.uniform(9.0f, 6.0f, 33.0f, 21.0f, 13.0f, 45.0f, UV_CONDENSER_DUCT));
        staticCuboids.add(Cuboid.uniform(8.0f, 4.0f, 32.0f, 22.0f, 6.0f, 46.0f, UV_CONDENSER_DUCT));

        // =========================================================================
        // 8. DYNAMIC EMISSIVE STATUS INDICATOR (Active vs Idle)
        // =========================================================================
        // Green RUN lamp & digital readout (glowing when active)
        this.activeRunIndicator = new Cuboid(
                23.5f, 23.5f, 3.1f,
                25.5f, 25.5f, 3.3f,
                UV_ACTIVE_INDICATOR, UV_ACTIVE_INDICATOR,
                UV_ACTIVE_INDICATOR, UV_ACTIVE_INDICATOR,
                UV_ACTIVE_INDICATOR, UV_ACTIVE_INDICATOR
        );

        this.idleStandbyIndicator = new Cuboid(
                23.5f, 23.5f, 3.1f,
                25.5f, 25.5f, 3.3f,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET,
                UV_CONTROL_CABINET, UV_CONTROL_CABINET
        );
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            boolean isActive,
            int light,
            int overlay
    ) {
        Matrix4f poseMatrix = pose.pose();

        // 1. Emit all static cuboids
        for (Cuboid c : this.staticCuboids) {
            emitCuboid(pose, poseMatrix, consumer, c, light, overlay);
        }

        // 2. Emit status indicator (full bright emissive glow when generating FE)
        if (isActive) {
            emitCuboid(pose, poseMatrix, consumer, this.activeRunIndicator, 15728880, overlay);
        } else {
            emitCuboid(pose, poseMatrix, consumer, this.idleStandbyIndicator, light, overlay);
        }
    }

    private static void emitCuboid(
            PoseStack.Pose pose,
            Matrix4f matrix,
            VertexConsumer consumer,
            Cuboid c,
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
                c.north(), 0.0f, 0.0f, -1.0f, light, overlay);

        // SOUTH Face (z = z1, normal 0, 0, +1)
        emitQuad(pose, matrix, consumer,
                x0, y1, z1,
                x0, y0, z1,
                x1, y0, z1,
                x1, y1, z1,
                c.south(), 0.0f, 0.0f, 1.0f, light, overlay);

        // WEST Face (x = x0, normal -1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y0, z0,
                x0, y0, z1,
                x0, y1, z1,
                c.west(), -1.0f, 0.0f, 0.0f, light, overlay);

        // EAST Face (x = x1, normal +1, 0, 0)
        emitQuad(pose, matrix, consumer,
                x1, y1, z1,
                x1, y0, z1,
                x1, y0, z0,
                x1, y1, z0,
                c.east(), 1.0f, 0.0f, 0.0f, light, overlay);

        // UP Face (y = y1, normal 0, +1, 0)
        emitQuad(pose, matrix, consumer,
                x0, y1, z0,
                x0, y1, z1,
                x1, y1, z1,
                x1, y1, z0,
                c.up(), 0.0f, 1.0f, 0.0f, light, overlay);

        // DOWN Face (y = y0, normal 0, -1, 0)
        emitQuad(pose, matrix, consumer,
                x0, y0, z1,
                x0, y0, z0,
                x1, y0, z0,
                x1, y0, z1,
                c.down(), 0.0f, -1.0f, 0.0f, light, overlay);
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
            int light,
            int overlay
    ) {
        consumer.addVertex(matrix, xA, yA, zA)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u0(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xB, yB, zB)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u0(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xC, yC, zC)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u1(), uv.v1())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);

        consumer.addVertex(matrix, xD, yD, zD)
                .setColor(255, 255, 255, 255)
                .setUv(uv.u1(), uv.v0())
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
