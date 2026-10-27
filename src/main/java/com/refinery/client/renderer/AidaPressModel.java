package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * High-Fidelity 3-Block-Wide (3x2x2) Multiblock Model for the Assembled Metal Press:
 * - Bottom: Immersive Engineering style 3-block-wide Dark Steel Conveyor Bed with bottom air vents,
 *   4 vertical brown/bronze tensioner straps with silver rivets, steel end rollers, and a
 *   stitched terracotta-orange conveyor belt spanning X = -16..32 (3 full blocks wide).
 * - Center: 4-post Dark Steel Gantry Frame with central bolster anvil platen and horizontal support collar.
 * - Top: AIDA NC1-1500 Stamping Head Crown with off-white industrial enamel housing, green AIDA 4-diamond
 *   emblem + text, green NC1-1500 badge, circular analog tonnage/crank-angle dial gauge, side cooling louvers,
 *   amber hydraulic cylinder, side electrical cabinet, operator control pendant with red mushroom E-stop,
 *   and dynamic animated white + safety-yellow stamping ram assembly.
 */
@OnlyIn(Dist.CLIENT)
public class AidaPressModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/aida_press.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;

    public record UVRect(float u0, float v0, float u1, float v1) {
        public static UVRect of(int x, int y, int w, int h) {
            // Inset by 0.15 texels to prevent any adjacent atlas bleeding at mip/angle boundaries
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

    // Atlas UV Regions (matching AidaAssetGenerator 256x256 layout)
    private static final UVRect UV_CONVEYOR_SIDE   = UVRect.of(0, 0, 96, 24);
    private static final UVRect UV_CONVEYOR_END    = UVRect.of(96, 0, 56, 24);
    private static final UVRect UV_ANVIL_PLATEN    = UVRect.of(160, 0, 48, 48);
    private static final UVRect UV_STEEL_ROLLER    = UVRect.of(208, 0, 48, 24);
    private static final UVRect UV_TOOL_STEEL_DIE  = UVRect.of(208, 24, 48, 24);
    private static final UVRect UV_CONVEYOR_BELT   = UVRect.of(0, 24, 96, 48);
    private static final UVRect UV_DARK_STEEL      = UVRect.of(96, 24, 64, 48);
    private static final UVRect UV_DARK_STEEL_TRIM = UVRect.of(100, 28, 32, 24);
    private static final UVRect UV_BROWN_STRAP     = UVRect.of(30, 0, 4, 24);
    private static final UVRect UV_AIDA_FRONT      = UVRect.of(0, 72, 64, 48);
    private static final UVRect UV_AIDA_LOUVERS    = UVRect.of(64, 72, 64, 48);
    private static final UVRect UV_HEAD_TOP_CAP    = UVRect.of(128, 72, 64, 48);
    private static final UVRect UV_ORANGE_CORE     = UVRect.of(150, 86, 20, 20);
    private static final UVRect UV_ELEC_CABINET    = UVRect.of(192, 72, 64, 64);
    private static final UVRect UV_CABINET_SIDE    = UVRect.of(196, 76, 16, 56);
    private static final UVRect UV_CONTROL_PENDANT = UVRect.of(0, 120, 64, 48);
    private static final UVRect UV_HYDRAULIC_CYL   = UVRect.of(64, 120, 48, 48);
    private static final UVRect UV_CHROME_ROD      = UVRect.of(64, 122, 48, 4);
    private static final UVRect UV_AIDA_RAM_SLIDE  = UVRect.of(112, 120, 80, 48);
    private static final UVRect UV_AIDA_DIAL_ONLY  = UVRect.of(22, 93, 20, 20);

    private final List<Cuboid> staticCuboids = new ArrayList<>();

    public AidaPressModel() {
        buildStaticGeometry();
    }

    private void buildStaticGeometry() {
        // =====================================================================
        // 1. 3-BLOCK-WIDE CONVEYOR BED BASE (X: -16..32, Y: 0..11.2, Z: 3..29)
        // =====================================================================
        // Main heavy dark steel conveyor frame spanning all 3 blocks (46 px wide)
        staticCuboids.add(new Cuboid(
                -15.0f, 0.0f, 4.0f,
                31.0f, 10.0f, 28.0f,
                UV_CONVEYOR_SIDE, UV_CONVEYOR_SIDE,
                UV_CONVEYOR_END, UV_CONVEYOR_END,
                UV_DARK_STEEL, UV_DARK_STEEL
        ));

        // Bottom heavy mounting plinth feet at the left, center, and right blocks
        staticCuboids.add(Cuboid.uniform(-15.5f, 0.0f, 3.5f, -11.5f, 2.0f, 28.5f, UV_DARK_STEEL_TRIM));
        staticCuboids.add(Cuboid.uniform(-1.5f, 0.0f, 3.5f, 2.5f, 2.0f, 28.5f, UV_DARK_STEEL_TRIM));
        staticCuboids.add(Cuboid.uniform(13.5f, 0.0f, 3.5f, 17.5f, 2.0f, 28.5f, UV_DARK_STEEL_TRIM));
        staticCuboids.add(Cuboid.uniform(27.5f, 0.0f, 3.5f, 31.5f, 2.0f, 28.5f, UV_DARK_STEEL_TRIM));

        // 4 3D Relief Brown/Bronze Tensioner Straps with Silver Rivets along Front & Back
        float[] strapCentersX = {-13.5f, 0.5f, 15.5f, 29.5f};
        for (float sx : strapCentersX) {
            // Front strap
            staticCuboids.add(new Cuboid(
                    sx - 1.0f, 0.2f, 3.4f,
                    sx + 1.0f, 11.3f, 4.1f,
                    UV_BROWN_STRAP, UV_BROWN_STRAP,
                    UV_BROWN_STRAP, UV_BROWN_STRAP,
                    UV_BROWN_STRAP, UV_BROWN_STRAP
            ));
            // Back strap
            staticCuboids.add(new Cuboid(
                    sx - 1.0f, 0.2f, 27.9f,
                    sx + 1.0f, 11.3f, 28.6f,
                    UV_BROWN_STRAP, UV_BROWN_STRAP,
                    UV_BROWN_STRAP, UV_BROWN_STRAP,
                    UV_BROWN_STRAP, UV_BROWN_STRAP
            ));
        }

        // Left & Right End Brushed Steel Conveyor Rollers & Bearing Axles
        staticCuboids.add(Cuboid.uniform(-15.8f, 6.2f, 5.2f, -13.5f, 10.8f, 26.8f, UV_STEEL_ROLLER));
        staticCuboids.add(Cuboid.uniform(-16.0f, 7.6f, 3.5f, -14.8f, 9.4f, 28.5f, UV_CHROME_ROD));
        staticCuboids.add(Cuboid.uniform(29.5f, 6.2f, 5.2f, 31.8f, 10.8f, 26.8f, UV_STEEL_ROLLER));
        staticCuboids.add(Cuboid.uniform(30.8f, 7.6f, 3.5f, 32.0f, 9.4f, 28.5f, UV_CHROME_ROD));

        // Stitched Terracotta-Orange Conveyor Belt Top Surface (spanning X = -15.2..31.2)
        staticCuboids.add(new Cuboid(
                -15.2f, 10.0f, 5.0f,
                31.2f, 11.2f, 27.0f,
                UV_CONVEYOR_BELT, UV_CONVEYOR_BELT,
                UV_CONVEYOR_BELT, UV_CONVEYOR_BELT,
                UV_CONVEYOR_BELT, UV_DARK_STEEL
        ));

        // Front & Back Conveyor Belt Steel Guide Rails
        staticCuboids.add(Cuboid.uniform(-14.5f, 10.0f, 4.0f, 30.5f, 11.7f, 5.0f, UV_DARK_STEEL_TRIM));
        staticCuboids.add(Cuboid.uniform(-14.5f, 10.0f, 27.0f, 30.5f, 11.7f, 28.0f, UV_DARK_STEEL_TRIM));

        // Center Machined Steel Bolster Anvil Platen (under the stamping head at X = 2..14)
        staticCuboids.add(new Cuboid(
                2.0f, 11.2f, 6.0f,
                14.0f, 12.5f, 26.0f,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM,
                UV_ANVIL_PLATEN, UV_DARK_STEEL
        ));

        // =====================================================================
        // 2. CENTER 4-POST DARK STEEL GANTRY & SUPPORT COLLAR (X: 0..16, Y: 10..21)
        // =====================================================================
        // 4 Heavy Vertical Gantry Corner Columns (open along X so conveyor belt runs through)
        // Front-Left Post
        staticCuboids.add(Cuboid.uniform(0.0f, 10.0f, 3.2f, 3.0f, 21.0f, 6.2f, UV_DARK_STEEL));
        // Front-Right Post
        staticCuboids.add(Cuboid.uniform(13.0f, 10.0f, 3.2f, 16.0f, 21.0f, 6.2f, UV_DARK_STEEL));
        // Back-Left Post
        staticCuboids.add(Cuboid.uniform(0.0f, 10.0f, 25.8f, 3.0f, 21.0f, 28.8f, UV_DARK_STEEL));
        // Back-Right Post
        staticCuboids.add(Cuboid.uniform(13.0f, 10.0f, 25.8f, 16.0f, 21.0f, 28.8f, UV_DARK_STEEL));

        // Horizontal Steel Support Collar Ring at Y = 18.2..21.0 (tying the 4 posts together)
        staticCuboids.add(Cuboid.uniform(-0.5f, 18.5f, 2.8f, 16.5f, 21.0f, 6.4f, UV_DARK_STEEL));
        staticCuboids.add(Cuboid.uniform(-0.5f, 18.5f, 25.6f, 16.5f, 21.0f, 29.2f, UV_DARK_STEEL));
        staticCuboids.add(Cuboid.uniform(-0.5f, 18.5f, 6.4f, 2.8f, 21.0f, 25.6f, UV_DARK_STEEL));
        staticCuboids.add(Cuboid.uniform(13.2f, 18.5f, 6.4f, 16.5f, 21.0f, 25.6f, UV_DARK_STEEL));

        // =====================================================================
        // 3. OVERHEAD AIDA NC1-1500 STAMPING HEAD CROWN (X: -1..17, Y: 21..32, Z: 3..29)
        // =====================================================================
        // Main AIDA Crown Housing:
        // - Front (NORTH) & Back (SOUTH) display the off-white AIDA panel with green 4-diamond AIDA logo,
        //   NC1-1500 badge, and circular analog dial gauge
        // - Left (WEST) & Right (EAST) display the 3 recessed horizontal cooling louvers
        staticCuboids.add(new Cuboid(
                -1.0f, 21.0f, 3.0f,
                17.0f, 32.0f, 29.0f,
                UV_AIDA_FRONT, UV_AIDA_FRONT,
                UV_AIDA_LOUVERS, UV_AIDA_LOUVERS,
                UV_HEAD_TOP_CAP, UV_DARK_STEEL
        ));

        // 3D Protruding Circular Analog Tonnage / Crank-Angle Dial Gauge on Front Face
        staticCuboids.add(new Cuboid(
                5.6f, 22.0f, 2.4f,
                10.4f, 26.8f, 3.0f,
                UV_AIDA_DIAL_ONLY, UV_DARK_STEEL_TRIM,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM
        ));

        // Stepped Upper Crown Cap (Y: 32.0..33.8) with Glowing Copper-Orange Drive Core
        staticCuboids.add(new Cuboid(
                1.5f, 32.0f, 6.0f,
                14.5f, 33.8f, 26.0f,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM,
                UV_HEAD_TOP_CAP, UV_DARK_STEEL
        ));
        staticCuboids.add(Cuboid.uniform(5.0f, 33.8f, 11.5f, 11.0f, 34.5f, 20.5f, UV_ORANGE_CORE));

        // Top Amber/Bronze AIDA Hydraulic Cylinder & Chrome Tie Rods (Y: 34.5..38.0)
        staticCuboids.add(new Cuboid(
                4.5f, 34.5f, 11.0f,
                11.5f, 38.0f, 21.0f,
                UV_HYDRAULIC_CYL, UV_HYDRAULIC_CYL,
                UV_HYDRAULIC_CYL, UV_HYDRAULIC_CYL,
                UV_DARK_STEEL_TRIM, UV_DARK_STEEL_TRIM
        ));

        // AIDA Side Electrical Cabinet (mounted on right side of crown at X: 17.0..20.5)
        staticCuboids.add(new Cuboid(
                17.0f, 21.5f, 7.5f,
                20.5f, 30.8f, 24.5f,
                UV_CABINET_SIDE, UV_CABINET_SIDE,
                UV_CABINET_SIDE, UV_ELEC_CABINET,
                UV_CABINET_SIDE, UV_CABINET_SIDE
        ));

        // AIDA Swinging Operator Control Pendant Arm & Box with Red E-Stop (front-left at X: -5.5..-1.0)
        // Boom arm from crown
        staticCuboids.add(Cuboid.uniform(-4.0f, 24.5f, 3.8f, -1.0f, 26.0f, 5.2f, UV_DARK_STEEL_TRIM));
        staticCuboids.add(Cuboid.uniform(-4.0f, 18.5f, 3.8f, -2.8f, 24.5f, 5.0f, UV_CHROME_ROD));
        // Operator Control Pendant Box (facing NORTH towards operator)
        staticCuboids.add(new Cuboid(
                -5.8f, 13.8f, 3.0f,
                -1.2f, 18.8f, 6.2f,
                UV_CONTROL_PENDANT, UV_CABINET_SIDE,
                UV_CABINET_SIDE, UV_CABINET_SIDE,
                UV_CABINET_SIDE, UV_CABINET_SIDE
        ));
    }

    /**
     * Renders both the static 3-block-wide press structure and the dynamic animated stamping ram.
     */
    public void render(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float ramDisplacement) {
        Matrix4f poseMatrix = pose.pose();

        // 1. Render all static cuboids
        for (Cuboid c : staticCuboids) {
            emitCuboid(pose, poseMatrix, consumer, c, 0.0f, light, overlay);
        }

        // 2. Render Dynamic Stamping Ram Assembly (slides vertically by -ramDisplacement)
        float dy = -ramDisplacement;

        // Polished Chrome Hydraulic Guide Rods (extend from crown at Y=21.5 down into the moving slide)
        Cuboid centerPiston = Cuboid.uniform(
                6.0f, 20.5f + dy, 13.0f,
                10.0f, 22.0f, 19.0f,
                UV_HYDRAULIC_CYL
        );
        Cuboid leftGuideRod = Cuboid.uniform(
                3.2f, 20.5f + dy, 9.0f,
                4.8f, 22.0f, 23.0f,
                UV_CHROME_ROD
        );
        Cuboid rightGuideRod = Cuboid.uniform(
                11.2f, 20.5f + dy, 9.0f,
                12.8f, 22.0f, 23.0f,
                UV_CHROME_ROD
        );
        emitCuboid(pose, poseMatrix, consumer, centerPiston, 0.0f, light, overlay);
        emitCuboid(pose, poseMatrix, consumer, leftGuideRod, 0.0f, light, overlay);
        emitCuboid(pose, poseMatrix, consumer, rightGuideRod, 0.0f, light, overlay);

        // AIDA White Moving Ram Slide + Safety-Yellow Die Holder Bar (#FACC15 with black T-slots)
        Cuboid ramSlide = new Cuboid(
                2.2f, 17.5f, 5.8f,
                13.8f, 21.8f, 26.2f,
                UV_AIDA_RAM_SLIDE, UV_AIDA_RAM_SLIDE,
                UV_AIDA_RAM_SLIDE, UV_AIDA_RAM_SLIDE,
                UV_DARK_STEEL_TRIM, UV_TOOL_STEEL_DIE
        );
        emitCuboid(pose, poseMatrix, consumer, ramSlide, dy, light, overlay);

        // Hardened Dark Tool-Steel Upper Stamping Die Platen (presses down onto the center anvil at Y=12.5)
        Cuboid upperDie = new Cuboid(
                3.0f, 15.8f, 7.0f,
                13.0f, 17.5f, 25.0f,
                UV_TOOL_STEEL_DIE, UV_TOOL_STEEL_DIE,
                UV_TOOL_STEEL_DIE, UV_TOOL_STEEL_DIE,
                UV_TOOL_STEEL_DIE, UV_ANVIL_PLATEN
        );
        emitCuboid(pose, poseMatrix, consumer, upperDie, dy, light, overlay);
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

        // UP Face (y = y1, normal 0, +1, 0) - U along X, V along Z
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
            float ax, float ay, float az,
            float bx, float by, float bz,
            float cx, float cy, float cz,
            float dx, float dy, float dz,
            UVRect uv,
            float nx, float ny, float nz,
            int light,
            int overlay
    ) {
        Vector3f norm = pose.transformNormal(nx, ny, nz, new Vector3f());
        Vector3f pos = new Vector3f();

        matrix.transformPosition(ax, ay, az, pos);
        consumer.addVertex(pos.x(), pos.y(), pos.z(), -1, uv.u0(), uv.v0(), overlay, light, norm.x(), norm.y(), norm.z());

        matrix.transformPosition(bx, by, bz, pos);
        consumer.addVertex(pos.x(), pos.y(), pos.z(), -1, uv.u0(), uv.v1(), overlay, light, norm.x(), norm.y(), norm.z());

        matrix.transformPosition(cx, cy, cz, pos);
        consumer.addVertex(pos.x(), pos.y(), pos.z(), -1, uv.u1(), uv.v1(), overlay, light, norm.x(), norm.y(), norm.z());

        matrix.transformPosition(dx, dy, dz, pos);
        consumer.addVertex(pos.x(), pos.y(), pos.z(), -1, uv.u1(), uv.v0(), overlay, light, norm.x(), norm.y(), norm.z());
    }
}
