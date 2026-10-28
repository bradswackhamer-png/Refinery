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
 * High-Fidelity 3x3x3 Multiblock Model for the Assembled Small Steam Boiler:
 * - Structural dark steel I-beam skid base runners and curved saddle support cradles
 * - Horizontal cylindrical Scotch marine boiler drum with solid steel cladding plates and riveted reinforcement straps
 * - Circular front boiler door with heavy black cast hinges, perimeter bolt circle, and central burner hub
 * - Glowing burner sight-glass viewport (intense fire glow when active, soot-coated glass when idle)
 * - Lower arched rectangular cleanout/ash door with latch
 * - Overhead steam manifold header piping, red globe valve handwheels, safety relief valve, and dial pressure gauge
 * - Tall vertical exhaust smokestack / chimney with reinforcement collar and top flange
 * - Side industrial electrical control cabinet with indicator lamps and warning badge (ICI Caldaie style)
 * - Side vertical water level sight column and dual feed-water pumps
 */
@OnlyIn(Dist.CLIENT)
public class SmallBoilerModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/small_boiler.png"
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

    // Atlas UV Regions (matching BoilerAssetGenerator 256x256 layout)
    private static final UVRect UV_SHELL_CLADDING    = UVRect.of(0, 0, 96, 48);
    private static final UVRect UV_DRUM_HEAD        = UVRect.of(96, 0, 64, 48);
    private static final UVRect UV_SKID_STEEL       = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_SMOKESTACK       = UVRect.of(208, 0, 48, 48);
    private static final UVRect UV_FRONT_DOOR       = UVRect.of(0, 48, 64, 64);
    private static final UVRect UV_BURNER_ACTIVE    = UVRect.of(64, 48, 32, 32);
    private static final UVRect UV_BURNER_IDLE      = UVRect.of(96, 48, 32, 32);
    private static final UVRect UV_STEAM_PIPE       = UVRect.of(128, 48, 64, 32);
    private static final UVRect UV_VALVE_WHEEL      = UVRect.of(192, 48, 32, 32);
    private static final UVRect UV_PRESSURE_GAUGE   = UVRect.of(224, 48, 32, 32);
    private static final UVRect UV_CONTROL_CABINET  = UVRect.of(0, 112, 64, 48);
    private static final UVRect UV_WATER_COLUMN     = UVRect.of(64, 112, 48, 48);
    private static final UVRect UV_CLEANOUT_DOOR    = UVRect.of(112, 112, 48, 48);
    private static final UVRect UV_REINFORCE_STRAP  = UVRect.of(160, 112, 96, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();
    private final Cuboid burnerSightGlassActive;
    private final Cuboid burnerSightGlassIdle;

    public SmallBoilerModel() {
        // =========================================================================
        // 1. STRUCTURAL BASE SKID & SADDLE CRADLES (Y: 0..15)
        // =========================================================================
        // Left longitudinal I-beam runner
        staticCuboids.add(Cuboid.uniform(-12.0f, 0.0f, 0.0f, -6.0f, 4.0f, 48.0f, UV_SKID_STEEL));
        // Right longitudinal I-beam runner
        staticCuboids.add(Cuboid.uniform(22.0f, 0.0f, 0.0f, 28.0f, 4.0f, 48.0f, UV_SKID_STEEL));
        // Front, rear, and center transverse cross-beams
        staticCuboids.add(Cuboid.uniform(-6.0f, 0.0f, 2.0f, 22.0f, 4.0f, 6.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-6.0f, 0.0f, 22.0f, 22.0f, 4.0f, 26.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-6.0f, 0.0f, 42.0f, 22.0f, 4.0f, 46.0f, UV_SKID_STEEL));

        // Front curved saddle support cradle (Z: 8..14)
        staticCuboids.add(Cuboid.uniform(-10.0f, 4.0f, 8.0f, 26.0f, 8.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-11.5f, 8.0f, 8.0f, -7.0f, 15.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(23.0f, 8.0f, 8.0f, 27.5f, 15.0f, 14.0f, UV_SKID_STEEL));

        // Rear curved saddle support cradle (Z: 34..40)
        staticCuboids.add(Cuboid.uniform(-10.0f, 4.0f, 34.0f, 26.0f, 8.0f, 40.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-11.5f, 8.0f, 34.0f, -7.0f, 15.0f, 40.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(23.0f, 8.0f, 34.0f, 27.5f, 15.0f, 40.0f, UV_SKID_STEEL));

        // =========================================================================
        // 2. HORIZONTAL CYLINDRICAL BOILER DRUM (Z: 2..46, Centered at X=8, Y=24)
        // =========================================================================
        // Core box
        staticCuboids.add(Cuboid.uniform(-8.0f, 8.0f, 2.0f, 24.0f, 40.0f, 46.0f, UV_SHELL_CLADDING));
        // Horizontal side bulge plates (diameter 36)
        staticCuboids.add(Cuboid.uniform(-10.0f, 13.0f, 2.0f, 26.0f, 35.0f, 46.0f, UV_SHELL_CLADDING));
        // Vertical top/bottom bulge plates (height 36)
        staticCuboids.add(Cuboid.uniform(-3.0f, 6.0f, 2.0f, 19.0f, 42.0f, 46.0f, UV_SHELL_CLADDING));
        // 4 diagonal stepped facets forming smooth 12-sided cylinder
        staticCuboids.add(Cuboid.uniform(-7.0f, 33.0f, 2.0f, 5.0f, 41.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(11.0f, 33.0f, 2.0f, 23.0f, 41.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(-7.0f, 7.0f, 2.0f, 5.0f, 15.0f, 46.0f, UV_SHELL_CLADDING));
        staticCuboids.add(Cuboid.uniform(11.0f, 7.0f, 2.0f, 23.0f, 15.0f, 46.0f, UV_SHELL_CLADDING));

        // Circumferential steel cladding reinforcement straps
        staticCuboids.add(Cuboid.uniform(-10.3f, 5.7f, 3.5f, 26.3f, 42.3f, 5.5f, UV_REINFORCE_STRAP));
        staticCuboids.add(Cuboid.uniform(-10.3f, 5.7f, 16.5f, 26.3f, 42.3f, 18.5f, UV_REINFORCE_STRAP));
        staticCuboids.add(Cuboid.uniform(-10.3f, 5.7f, 29.5f, 26.3f, 42.3f, 31.5f, UV_REINFORCE_STRAP));
        staticCuboids.add(Cuboid.uniform(-10.3f, 5.7f, 42.5f, 26.3f, 42.3f, 44.5f, UV_REINFORCE_STRAP));

        // Rear dished boiler end-head plate
        staticCuboids.add(Cuboid.uniform(-8.5f, 7.5f, 46.0f, 24.5f, 40.5f, 47.5f, UV_DRUM_HEAD));

        // =========================================================================
        // 3. FRONT BOILER HEAD & CIRCULAR HINGED DOOR (Z: -2..2)
        // =========================================================================
        // Front Drum Head Sheet
        staticCuboids.add(Cuboid.uniform(-8.5f, 7.5f, 1.0f, 24.5f, 40.5f, 2.2f, UV_DRUM_HEAD));

        // Massive circular boiler door (slab + bevels)
        staticCuboids.add(new Cuboid(
                -7.0f, 9.0f, -1.0f,
                23.0f, 39.0f, 1.0f,
                UV_FRONT_DOOR, UV_FRONT_DOOR,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));
        staticCuboids.add(new Cuboid(
                -5.0f, 7.0f, -1.0f,
                21.0f, 41.0f, 1.0f,
                UV_FRONT_DOOR, UV_FRONT_DOOR,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));
        // Door outer perimeter flange rim
        staticCuboids.add(Cuboid.uniform(-6.0f, 10.0f, -1.5f, 22.0f, 38.0f, -1.0f, UV_FRONT_DOOR));

        // Heavy Left Cast Steel Hinges
        staticCuboids.add(Cuboid.uniform(-12.5f, 27.0f, -2.0f, -6.5f, 33.0f, 1.5f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-12.5f, 15.0f, -2.0f, -6.5f, 21.0f, 1.5f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-11.5f, 13.0f, -1.8f, -9.5f, 35.0f, -0.2f, UV_SKID_STEEL));

        // Burner Mount Collar & Hub
        staticCuboids.add(Cuboid.uniform(3.0f, 23.0f, -2.5f, 13.0f, 33.0f, -1.5f, UV_FRONT_DOOR));
        staticCuboids.add(Cuboid.uniform(4.0f, 24.0f, -3.2f, 12.0f, 32.0f, -2.5f, UV_SKID_STEEL));

        // Sight Glass Cuboids (Dynamic switch based on active state)
        this.burnerSightGlassActive = Cuboid.uniform(5.5f, 25.5f, -3.3f, 10.5f, 30.5f, -3.1f, UV_BURNER_ACTIVE);
        this.burnerSightGlassIdle   = Cuboid.uniform(5.5f, 25.5f, -3.3f, 10.5f, 30.5f, -3.1f, UV_BURNER_IDLE);

        // Lower Cleanout / Ash Door & Latch
        staticCuboids.add(Cuboid.uniform(3.5f, 11.0f, -2.2f, 12.5f, 19.0f, -1.2f, UV_CLEANOUT_DOOR));
        staticCuboids.add(Cuboid.uniform(11.5f, 14.0f, -2.7f, 13.5f, 16.0f, -2.0f, UV_SKID_STEEL));

        // =========================================================================
        // 4. OVERHEAD EQUIPMENT: SMOKESTACK & STEAM PIPING (Y >= 41)
        // =========================================================================
        // Smokestack Flue Base Box (X: 8, Z: 38)
        staticCuboids.add(Cuboid.uniform(3.0f, 41.5f, 33.0f, 13.0f, 46.0f, 43.0f, UV_SMOKESTACK));
        // Smokestack Cylindrical Pipe Shaft
        staticCuboids.add(Cuboid.uniform(5.0f, 46.0f, 35.0f, 11.0f, 61.0f, 41.0f, UV_SMOKESTACK));
        // Smokestack Top Reinforcement Flange Rim
        staticCuboids.add(Cuboid.uniform(4.0f, 61.0f, 34.0f, 12.0f, 63.0f, 42.0f, UV_SMOKESTACK));

        // Steam Header Manifold & Takeoff Riser (X: 8, Z: 22)
        staticCuboids.add(Cuboid.uniform(5.0f, 41.5f, 19.0f, 11.0f, 48.0f, 25.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(2.0f, 48.0f, 20.0f, 14.0f, 52.0f, 24.0f, UV_STEAM_PIPE));

        // Main Globe Shutoff Valve
        staticCuboids.add(Cuboid.uniform(6.0f, 52.0f, 20.0f, 10.0f, 54.0f, 24.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(7.5f, 54.0f, 21.5f, 8.5f, 57.0f, 22.5f, UV_SKID_STEEL));
        // Top Red Handwheel
        staticCuboids.add(Cuboid.uniform(5.0f, 57.0f, 19.0f, 11.0f, 58.0f, 25.0f, UV_VALVE_WHEEL));

        // Safety Relief Valve (Right side of header)
        staticCuboids.add(Cuboid.uniform(12.0f, 48.0f, 21.0f, 15.0f, 55.0f, 23.0f, UV_STEAM_PIPE));

        // Front Piping Loop & Red Handwheel Valve (Matching Image 1)
        staticCuboids.add(Cuboid.uniform(1.0f, 48.0f, 9.0f, 3.5f, 50.5f, 20.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(1.0f, 38.0f, 7.5f, 3.5f, 48.0f, 10.0f, UV_STEAM_PIPE));
        // Front Valve Body
        staticCuboids.add(Cuboid.uniform(0.5f, 41.5f, 7.0f, 4.0f, 44.5f, 10.5f, UV_STEAM_PIPE));
        // Front Red Handwheel facing North
        staticCuboids.add(Cuboid.uniform(0.0f, 40.0f, 5.5f, 4.5f, 46.0f, 6.5f, UV_VALVE_WHEEL));

        // Dial Pressure Gauge facing North
        staticCuboids.add(Cuboid.uniform(7.5f, 46.0f, 16.0f, 8.5f, 50.0f, 17.5f, UV_SKID_STEEL));
        staticCuboids.add(new Cuboid(
                6.0f, 49.0f, 14.5f,
                10.0f, 53.0f, 16.0f,
                UV_PRESSURE_GAUGE, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));

        // =========================================================================
        // 5. SIDE EQUIPMENT (CONTROL CABINET & WATER FEED COLUMN)
        // =========================================================================
        // Right Side Industrial Electrical Control Cabinet (ICI Caldaie style)
        staticCuboids.add(new Cuboid(
                26.0f, 16.0f, 15.0f,
                30.5f, 32.0f, 27.0f,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_CONTROL_CABINET,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));
        // Cabinet Door Face
        staticCuboids.add(new Cuboid(
                30.5f, 16.5f, 15.5f,
                31.2f, 31.5f, 26.5f,
                UV_SKID_STEEL, UV_SKID_STEEL,
                UV_SKID_STEEL, UV_CONTROL_CABINET,
                UV_SKID_STEEL, UV_SKID_STEEL
        ));
        // Conduit down to base skid
        staticCuboids.add(Cuboid.uniform(27.5f, 4.0f, 20.0f, 29.0f, 16.0f, 21.5f, UV_STEAM_PIPE));

        // Left Side Water Level Sight Glass Column
        staticCuboids.add(Cuboid.uniform(-13.5f, 14.0f, 13.0f, -11.5f, 30.0f, 15.0f, UV_WATER_COLUMN));
        staticCuboids.add(Cuboid.uniform(-12.0f, 27.5f, 13.0f, -9.0f, 29.5f, 15.0f, UV_STEAM_PIPE));
        staticCuboids.add(Cuboid.uniform(-12.0f, 14.5f, 13.0f, -9.0f, 16.5f, 15.0f, UV_STEAM_PIPE));

        // Left Side Dual Feed-Water Booster Pumps on base skid (Matching Image 2)
        staticCuboids.add(Cuboid.uniform(-14.0f, 4.0f, 11.0f, -11.0f, 13.0f, 14.0f, UV_SKID_STEEL));
        staticCuboids.add(Cuboid.uniform(-14.0f, 4.0f, 15.0f, -11.0f, 13.0f, 18.0f, UV_SKID_STEEL));
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            boolean isActive,
            int light,
            int overlay
    ) {
        Matrix4f poseMatrix = pose.pose();

        // 1. Emit all static boiler cuboids
        for (Cuboid c : this.staticCuboids) {
            emitCuboid(pose, poseMatrix, consumer, c, light, overlay);
        }

        // 2. Emit burner sight-glass (full bright glow when active)
        if (isActive) {
            // Emissive full brightness for burning fire
            emitCuboid(pose, poseMatrix, consumer, this.burnerSightGlassActive, 15728880, overlay);
        } else {
            emitCuboid(pose, poseMatrix, consumer, this.burnerSightGlassIdle, light, overlay);
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
