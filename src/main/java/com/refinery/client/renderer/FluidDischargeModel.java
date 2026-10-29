package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.block.DischargeMode;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Monolithic 3D Model of the Industrial Fire-Engine Red Foot Valve & Discharge Strainer:
 * Reference: Fire-engine red foot valve / suction & discharge strainer head.
 * Features:
 * - Base pipe mounting collar with 4 vertical cast reinforcement ribs
 * - Main glossy red cylindrical housing
 * - Dual-layer mounting flange rings with side ear bolt lugs
 * - Dark graphite seal gasket sandwiched between flanges
 * - High-tensile steel mounting studs with silver hex bolt heads & bottom nuts
 * - Slotted strainer dome with vertical arched slots exposing dark interior throat
 * - Perforated top dome crown
 * - Mode status indicator
 */
@OnlyIn(Dist.CLIENT)
public class FluidDischargeModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/fluid_discharge.png"
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

    private final List<Cuboid> staticParts = new ArrayList<>();
    private final List<Cuboid> boltParts = new ArrayList<>();
    private final List<Cuboid> interiorParts = new ArrayList<>();

    // Texture UV definitions
    private static final UVRect UV_RED_BODY    = UVRect.of(0, 0, 64, 64);
    private static final UVRect UV_BASE_RIBS   = UVRect.of(64, 0, 64, 32);
    private static final UVRect UV_FLANGE      = UVRect.of(0, 64, 64, 32);
    private static final UVRect UV_GASKET      = UVRect.of(64, 32, 64, 16);
    private static final UVRect UV_BOLT_HEAD   = UVRect.of(128, 0, 32, 32);
    private static final UVRect UV_STUD_NUT    = UVRect.of(160, 0, 32, 32);
    private static final UVRect UV_DOME_RIBS   = UVRect.of(0, 96, 64, 64);
    private static final UVRect UV_DOME_CROWN  = UVRect.of(64, 64, 48, 48);
    private static final UVRect UV_INTERIOR    = UVRect.of(128, 32, 48, 48);

    public FluidDischargeModel() {
        buildModel();
    }

    private void buildModel() {
        // === 1. Base Collar (y = 0.0 to 2.2) ===
        // Octagonal cylindrical base collar:
        // Core square: [3.5, 0.0, 3.5] to [12.5, 2.2, 12.5]
        staticParts.add(Cuboid.uniform(3.5f, 0.0f, 3.5f, 12.5f, 2.2f, 12.5f, UV_BASE_RIBS));
        // Side octagonal rounders
        staticParts.add(Cuboid.uniform(4.5f, 0.0f, 2.75f, 11.5f, 2.2f, 13.25f, UV_BASE_RIBS));
        staticParts.add(Cuboid.uniform(2.75f, 0.0f, 4.5f, 13.25f, 2.2f, 11.5f, UV_BASE_RIBS));

        // 4 Vertical Cast Reinforcement Ribs on base collar:
        // North rib:
        staticParts.add(Cuboid.uniform(7.25f, 0.0f, 2.25f, 8.75f, 2.2f, 2.95f, UV_BASE_RIBS));
        // South rib:
        staticParts.add(Cuboid.uniform(7.25f, 0.0f, 13.05f, 8.75f, 2.2f, 13.75f, UV_BASE_RIBS));
        // West rib:
        staticParts.add(Cuboid.uniform(2.25f, 0.0f, 7.25f, 2.95f, 2.2f, 8.75f, UV_BASE_RIBS));
        // East rib:
        staticParts.add(Cuboid.uniform(13.05f, 0.0f, 7.25f, 13.75f, 2.2f, 8.75f, UV_BASE_RIBS));

        // === 2. Main Cylindrical Housing (y = 2.2 to 7.0) ===
        // Central cylindrical body (diameter ~11)
        staticParts.add(Cuboid.uniform(3.0f, 2.2f, 3.0f, 13.0f, 7.0f, 13.0f, UV_RED_BODY));
        staticParts.add(Cuboid.uniform(2.25f, 2.2f, 4.0f, 13.75f, 7.0f, 12.0f, UV_RED_BODY));
        staticParts.add(Cuboid.uniform(4.0f, 2.2f, 2.25f, 12.0f, 7.0f, 13.75f, UV_RED_BODY));

        // === 3. Lower Flange Ring & Ear Lugs (y = 7.0 to 8.2) ===
        // Circular flange rim
        staticParts.add(Cuboid.uniform(2.0f, 7.0f, 2.0f, 14.0f, 8.2f, 14.0f, UV_FLANGE));
        staticParts.add(Cuboid.uniform(3.5f, 7.0f, 1.25f, 12.5f, 8.2f, 14.75f, UV_FLANGE));
        staticParts.add(Cuboid.uniform(1.25f, 7.0f, 3.5f, 14.75f, 8.2f, 12.5f, UV_FLANGE));

        // Protruding Ear Lugs for mounting bolts (West & East):
        // West Ear Lug:
        staticParts.add(Cuboid.uniform(0.25f, 7.0f, 6.25f, 2.0f, 8.2f, 9.75f, UV_FLANGE));
        // East Ear Lug:
        staticParts.add(Cuboid.uniform(14.0f, 7.0f, 6.25f, 15.75f, 8.2f, 9.75f, UV_FLANGE));

        // === 4. Graphite Gasket Seal Ring (y = 8.2 to 8.7) ===
        staticParts.add(Cuboid.uniform(2.25f, 8.2f, 2.25f, 13.75f, 8.7f, 13.75f, UV_GASKET));
        staticParts.add(Cuboid.uniform(0.5f, 8.2f, 6.5f, 2.25f, 8.7f, 9.5f, UV_GASKET));
        staticParts.add(Cuboid.uniform(13.75f, 8.2f, 6.5f, 15.5f, 8.7f, 9.5f, UV_GASKET));

        // === 5. Upper Flange Ring & Ear Lugs (y = 8.7 to 9.9) ===
        staticParts.add(Cuboid.uniform(2.0f, 8.7f, 2.0f, 14.0f, 9.9f, 14.0f, UV_FLANGE));
        staticParts.add(Cuboid.uniform(3.5f, 8.7f, 1.25f, 12.5f, 9.9f, 14.75f, UV_FLANGE));
        staticParts.add(Cuboid.uniform(1.25f, 8.7f, 3.5f, 14.75f, 9.9f, 12.5f, UV_FLANGE));

        // Upper West & East Ear Lugs:
        staticParts.add(Cuboid.uniform(0.25f, 8.7f, 6.25f, 2.0f, 9.9f, 9.75f, UV_FLANGE));
        staticParts.add(Cuboid.uniform(14.0f, 8.7f, 6.25f, 15.75f, 9.9f, 9.75f, UV_FLANGE));

        // === 6. High-Tensile Steel Bolts & Studs ===
        // West Bolt Assembly (centered at x = 1.125, z = 8.0):
        // Hex Bolt Head on top of upper ear lug:
        boltParts.add(Cuboid.uniform(0.45f, 9.9f, 7.15f, 1.8f, 11.25f, 8.85f, UV_BOLT_HEAD));
        // Steel Stud passing through lower ear lug:
        boltParts.add(Cuboid.uniform(0.85f, 4.8f, 7.55f, 1.4f, 7.0f, 8.45f, UV_STUD_NUT));
        // Hex Nut threaded on bottom of stud:
        boltParts.add(Cuboid.uniform(0.6f, 5.3f, 7.3f, 1.65f, 6.25f, 8.7f, UV_STUD_NUT));

        // East Bolt Assembly (centered at x = 14.875, z = 8.0):
        // Hex Bolt Head on top of upper ear lug:
        boltParts.add(Cuboid.uniform(14.2f, 9.9f, 7.15f, 15.55f, 11.25f, 8.85f, UV_BOLT_HEAD));
        // Steel Stud passing through lower ear lug:
        boltParts.add(Cuboid.uniform(14.6f, 4.8f, 7.55f, 15.15f, 7.0f, 8.45f, UV_STUD_NUT));
        // Hex Nut threaded on bottom of stud:
        boltParts.add(Cuboid.uniform(14.35f, 5.3f, 7.3f, 15.4f, 6.25f, 8.7f, UV_STUD_NUT));

        // === 7. Interior Valve Throat & Flapper Plate ===
        // Dark interior cavity inside the dome (visible through slots)
        interiorParts.add(Cuboid.uniform(4.0f, 9.9f, 4.0f, 12.0f, 11.5f, 12.0f, UV_INTERIOR));
        // Bronze internal flapper disk
        interiorParts.add(Cuboid.uniform(5.5f, 11.2f, 5.5f, 10.5f, 11.7f, 10.5f, UV_INTERIOR));

        // === 8. Slotted Strainer Dome ===
        // Lower Slotted Dome Cage (y = 9.9 to 13.0, diameter ~11.5)
        // Outer slotted ribs & perimeter
        staticParts.add(new Cuboid(
                2.75f, 9.9f, 2.75f, 13.25f, 13.0f, 13.25f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                3.75f, 9.9f, 2.0f, 12.25f, 13.0f, 14.0f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                2.0f, 9.9f, 3.75f, 14.0f, 13.0f, 12.25f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));

        // Mid Dome Taper (y = 13.0 to 14.4, diameter ~9.0)
        staticParts.add(new Cuboid(
                3.5f, 13.0f, 3.5f, 12.5f, 14.4f, 12.5f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                4.25f, 13.0f, 2.75f, 11.75f, 14.4f, 13.25f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                2.75f, 13.0f, 4.25f, 13.25f, 14.4f, 11.75f,
                UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_RIBS, UV_DOME_CROWN, UV_INTERIOR
        ));

        // Upper Perforated Dome Crown (y = 14.4 to 15.3, diameter ~6.5)
        staticParts.add(new Cuboid(
                4.75f, 14.4f, 4.75f, 11.25f, 15.3f, 11.25f,
                UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                5.5f, 14.4f, 4.0f, 10.5f, 15.3f, 12.0f,
                UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_INTERIOR
        ));
        staticParts.add(new Cuboid(
                4.0f, 14.4f, 5.5f, 12.0f, 15.3f, 10.5f,
                UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_DOME_CROWN, UV_INTERIOR
        ));
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            DischargeMode mode,
            boolean isActive,
            float animTicks,
            int light,
            int overlay
    ) {
        Matrix4f matrix = pose.pose();

        // 1. Render Interior Cavity
        for (Cuboid c : interiorParts) {
            renderCuboid(matrix, consumer, c, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        }

        // 2. Render Static Red Body & Flanges
        for (Cuboid c : staticParts) {
            renderCuboid(matrix, consumer, c, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        }

        // 3. Render Chrome Hardware (Bolts, Studs, Nuts)
        for (Cuboid c : boltParts) {
            renderCuboid(matrix, consumer, c, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    private static void renderCuboid(
            Matrix4f m, VertexConsumer c,
            Cuboid b,
            int light, int overlay,
            float red, float green, float blue, float alpha
    ) {
        float x0 = b.x0 / 16.0f;
        float y0 = b.y0 / 16.0f;
        float z0 = b.z0 / 16.0f;
        float x1 = b.x1 / 16.0f;
        float y1 = b.y1 / 16.0f;
        float z1 = b.z1 / 16.0f;

        // Down face (y = y0) - normal (0, -1, 0)
        c.addVertex(m, x0, y0, z1).setColor(red, green, blue, alpha).setUv(b.down.u0, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(b.down.u0, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z0).setColor(red, green, blue, alpha).setUv(b.down.u1, b.down.v0).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);
        c.addVertex(m, x1, y0, z1).setColor(red, green, blue, alpha).setUv(b.down.u1, b.down.v1).setOverlay(overlay).setLight(light).setNormal(0, -1, 0);

        // Up face (y = y1) - normal (0, 1, 0)
        c.addVertex(m, x0, y1, z0).setColor(red, green, blue, alpha).setUv(b.up.u0, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x0, y1, z1).setColor(red, green, blue, alpha).setUv(b.up.u0, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(b.up.u1, b.up.v1).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        c.addVertex(m, x1, y1, z0).setColor(red, green, blue, alpha).setUv(b.up.u1, b.up.v0).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);

        // North face (z = z0) - normal (0, 0, -1)
        c.addVertex(m, x1, y1, z0).setColor(red, green, blue, alpha).setUv(b.north.u0, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x1, y0, z0).setColor(red, green, blue, alpha).setUv(b.north.u0, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(b.north.u1, b.north.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);
        c.addVertex(m, x0, y1, z0).setColor(red, green, blue, alpha).setUv(b.north.u1, b.north.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, -1);

        // South face (z = z1) - normal (0, 0, 1)
        c.addVertex(m, x0, y1, z1).setColor(red, green, blue, alpha).setUv(b.south.u0, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x0, y0, z1).setColor(red, green, blue, alpha).setUv(b.south.u0, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y0, z1).setColor(red, green, blue, alpha).setUv(b.south.u1, b.south.v1).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(b.south.u1, b.south.v0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);

        // West face (x = x0) - normal (-1, 0, 0)
        c.addVertex(m, x0, y1, z0).setColor(red, green, blue, alpha).setUv(b.west.u0, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z0).setColor(red, green, blue, alpha).setUv(b.west.u0, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y0, z1).setColor(red, green, blue, alpha).setUv(b.west.u1, b.west.v1).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);
        c.addVertex(m, x0, y1, z1).setColor(red, green, blue, alpha).setUv(b.west.u1, b.west.v0).setOverlay(overlay).setLight(light).setNormal(-1, 0, 0);

        // East face (x = x1) - normal (1, 0, 0)
        c.addVertex(m, x1, y1, z1).setColor(red, green, blue, alpha).setUv(b.east.u0, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z1).setColor(red, green, blue, alpha).setUv(b.east.u0, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y0, z0).setColor(red, green, blue, alpha).setUv(b.east.u1, b.east.v1).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
        c.addVertex(m, x1, y1, z0).setColor(red, green, blue, alpha).setUv(b.east.u1, b.east.v0).setOverlay(overlay).setLight(light).setNormal(1, 0, 0);
    }
}
