package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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

@OnlyIn(Dist.CLIENT)
public class CrudePumpJackModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/crude_pump_jack.png"
    );

    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;

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
            UVRect north, UVRect south, UVRect west, UVRect east, UVRect up, UVRect down
    ) {
        public static Cuboid uniform(float x0, float y0, float z0, float x1, float y1, float z1, UVRect uv) {
            return new Cuboid(x0, y0, z0, x1, y1, z1, uv, uv, uv, uv, uv, uv);
        }
    }

    // UV Regions matching the 256x256 atlas
    public static final UVRect UV_BASE_SKID      = UVRect.of(0, 0, 64, 48);
    public static final UVRect UV_A_FRAME        = UVRect.of(64, 0, 64, 48);
    public static final UVRect UV_WALKING_BEAM   = UVRect.of(128, 0, 64, 48);
    public static final UVRect UV_HORSEHEAD      = UVRect.of(192, 0, 64, 48);
    public static final UVRect UV_MOTOR          = UVRect.of(0, 48, 64, 48);
    public static final UVRect UV_GEARBOX        = UVRect.of(64, 48, 64, 48);
    public static final UVRect UV_CRANK_WEIGHT   = UVRect.of(128, 48, 64, 48);
    public static final UVRect UV_PITMAN         = UVRect.of(192, 48, 64, 48);
    public static final UVRect UV_WELLHEAD       = UVRect.of(0, 96, 64, 48);
    public static final UVRect UV_CONTROL_PANEL  = UVRect.of(64, 96, 64, 48);
    public static final UVRect UV_HAZARD         = UVRect.of(128, 96, 64, 48);
    public static final UVRect UV_BEARING        = UVRect.of(192, 96, 64, 48);

    private final List<Cuboid> staticSkidCuboids = new ArrayList<>();
    private final List<Cuboid> staticSamsonCuboids = new ArrayList<>();
    private final List<Cuboid> walkingBeamCuboids = new ArrayList<>();
    private final List<Cuboid> horseheadCuboids = new ArrayList<>();
    private final List<Cuboid> crankCuboids = new ArrayList<>();
    private final List<Cuboid> pitmanCuboids = new ArrayList<>();
    private final List<Cuboid> polishedRodCuboids = new ArrayList<>();

    public CrudePumpJackModel() {
        buildStaticGeometry();
        buildWalkingBeamGeometry();
        buildCrankGeometry();
        buildPitmanGeometry();
        buildPolishedRodGeometry();
    }

    private void buildStaticGeometry() {
        // 1. Foundation Skid (2 blocks wide = 32px, 3 blocks long = 48px)
        // Left & right longitudinal heavy I-beams
        staticSkidCuboids.add(Cuboid.uniform(-15.0f, 0.0f, 1.0f, -11.0f, 4.0f, 47.0f, UV_BASE_SKID));
        staticSkidCuboids.add(Cuboid.uniform(11.0f, 0.0f, 1.0f, 15.0f, 4.0f, 47.0f, UV_BASE_SKID));
        // Cross foundation beams
        staticSkidCuboids.add(Cuboid.uniform(-11.0f, 0.0f, 1.0f, 11.0f, 3.5f, 5.0f, UV_BASE_SKID));   // Front
        staticSkidCuboids.add(Cuboid.uniform(-11.0f, 0.0f, 22.0f, 11.0f, 3.5f, 26.0f, UV_BASE_SKID)); // Center
        staticSkidCuboids.add(Cuboid.uniform(-11.0f, 0.0f, 43.0f, 11.0f, 3.5f, 47.0f, UV_BASE_SKID)); // Rear

        // 2. Front Wellhead (Christmas tree & discharge port)
        // Casinghead base plate
        staticSkidCuboids.add(Cuboid.uniform(-4.0f, 0.0f, 4.0f, 4.0f, 2.5f, 8.0f, UV_WELLHEAD));
        // Stuffing box cylinder
        staticSkidCuboids.add(Cuboid.uniform(-2.0f, 2.5f, 5.0f, 2.0f, 10.0f, 7.0f, UV_WELLHEAD));
        // Crude oil pipe tee fitting & discharge flanged port extending leftward
        staticSkidCuboids.add(Cuboid.uniform(-12.0f, 4.5f, 4.8f, -2.0f, 8.5f, 7.2f, UV_WELLHEAD));
        staticSkidCuboids.add(Cuboid.uniform(-14.0f, 3.5f, 3.8f, -12.0f, 9.5f, 8.2f, UV_WELLHEAD)); // Pipe flange

        // 3. Samson Posts (A-frame tower centered around Z = 24.0)
        // Four inclined structural uprights
        staticSamsonCuboids.add(Cuboid.uniform(-10.0f, 3.5f, 16.0f, -7.5f, 30.0f, 18.5f, UV_A_FRAME)); // FL
        staticSamsonCuboids.add(Cuboid.uniform(7.5f, 3.5f, 16.0f, 10.0f, 30.0f, 18.5f, UV_A_FRAME));   // FR
        staticSamsonCuboids.add(Cuboid.uniform(-10.0f, 3.5f, 29.5f, -7.5f, 30.0f, 32.0f, UV_A_FRAME)); // RL
        staticSamsonCuboids.add(Cuboid.uniform(7.5f, 3.5f, 29.5f, 10.0f, 30.0f, 32.0f, UV_A_FRAME));   // RR

        // Tower cross-braces
        staticSamsonCuboids.add(Cuboid.uniform(-8.5f, 14.0f, 17.5f, 8.5f, 16.5f, 30.5f, UV_A_FRAME));
        staticSamsonCuboids.add(Cuboid.uniform(-7.0f, 23.0f, 19.0f, 7.0f, 25.5f, 29.0f, UV_A_FRAME));

        // Saddle Bearing housing atop Samson posts
        staticSamsonCuboids.add(Cuboid.uniform(-4.0f, 30.0f, 22.0f, 4.0f, 33.5f, 26.0f, UV_BEARING));

        // 4. Rear Machinery: Prime Mover (Electric Motor)
        staticSkidCuboids.add(Cuboid.uniform(-12.0f, 3.5f, 39.0f, -4.0f, 12.0f, 46.0f, UV_MOTOR));
        // Belt guard
        staticSkidCuboids.add(Cuboid.uniform(-6.0f, 4.5f, 34.0f, -3.5f, 11.5f, 42.0f, UV_HAZARD));

        // 5. Gear Reducer (Gearbox)
        staticSkidCuboids.add(Cuboid.uniform(-4.0f, 3.5f, 34.0f, 4.0f, 15.0f, 42.0f, UV_GEARBOX));

        // 6. Operator Control Console & Power Socket
        staticSkidCuboids.add(new Cuboid(
                6.0f, 3.5f, 41.0f,
                14.0f, 18.0f, 46.0f,
                UV_CONTROL_PANEL, // North (faceplate with display, dial, power socket)
                UV_BASE_SKID,     // South
                UV_BASE_SKID,     // West
                UV_BASE_SKID,     // East
                UV_CONTROL_PANEL, // Up
                UV_BASE_SKID      // Down
        ));
    }

    private void buildWalkingBeamGeometry() {
        // Walking beam centered on pivot (0, 0, 0)
        // Length: 32px (from front Z = -18.0 to rear Z = +14.0)
        walkingBeamCuboids.add(Cuboid.uniform(-2.5f, -2.5f, -18.0f, 2.5f, 2.5f, 14.0f, UV_WALKING_BEAM));

        // Equalizer cross-bar at rear of walking beam
        walkingBeamCuboids.add(Cuboid.uniform(-6.0f, -2.0f, 13.0f, 6.0f, 2.0f, 15.5f, UV_WALKING_BEAM));

        // Horsehead attached to front tip of beam (Z = -18.0)
        // Arc-shaped curved plate
        horseheadCuboids.add(Cuboid.uniform(-2.0f, -11.0f, -22.5f, 2.0f, 5.0f, -17.5f, UV_HORSEHEAD));
        // Wireline guide curved groove plate
        horseheadCuboids.add(Cuboid.uniform(-0.8f, -12.0f, -23.5f, 0.8f, 6.0f, -21.5f, UV_HORSEHEAD));
    }

    private void buildCrankGeometry() {
        // Crank arm & heavy counterweight lobe
        // Pivots around crank shaft at (0, 0, 0)
        // Crank arm extending to radius 6.0px
        crankCuboids.add(Cuboid.uniform(-1.0f, -1.5f, -1.5f, 1.0f, 7.5f, 1.5f, UV_CRANK_WEIGHT));
        // Heavy counterweight lobe opposite the pin
        crankCuboids.add(Cuboid.uniform(-2.5f, -8.0f, -4.5f, 2.5f, 0.0f, 4.5f, UV_CRANK_WEIGHT));
        // Crank pin
        crankCuboids.add(Cuboid.uniform(-3.0f, 5.0f, -1.0f, 3.0f, 7.0f, 1.0f, UV_PITMAN));
    }

    private void buildPitmanGeometry() {
        // Pitman arm rod (connecting crank pin to walking beam equalizer)
        pitmanCuboids.add(Cuboid.uniform(-0.8f, 0.0f, -0.8f, 0.8f, 19.0f, 0.8f, UV_PITMAN));
    }

    private void buildPolishedRodGeometry() {
        // Polished rod & wireline bridle hanging from horsehead into stuffing box
        // Wireline cables (left & right)
        polishedRodCuboids.add(Cuboid.uniform(-1.5f, -2.0f, 0.0f, -1.1f, 10.0f, 0.4f, UV_WELLHEAD));
        polishedRodCuboids.add(Cuboid.uniform(1.1f, -2.0f, 0.0f, 1.5f, 10.0f, 0.4f, UV_WELLHEAD));
        // Carrier bar
        polishedRodCuboids.add(Cuboid.uniform(-2.5f, -3.0f, -0.5f, 2.5f, -2.0f, 0.9f, UV_WELLHEAD));
        // Polished steel rod entering stuffing box
        polishedRodCuboids.add(Cuboid.uniform(-0.5f, -18.0f, -0.3f, 0.5f, -3.0f, 0.7f, UV_WELLHEAD));
    }

    public void render(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            float crankAngle,
            float pumpingSpeed,
            int light
    ) {
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(TEXTURE),
                (pose, consumer) -> {
                    Matrix4f mat = pose.pose();

                    // 1. Render Static Foundation & Skid
                    for (Cuboid c : this.staticSkidCuboids) {
                        emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                    // 2. Render Samson A-Frame Tower
                    for (Cuboid c : this.staticSamsonCuboids) {
                        emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                }
        );

        // 3. Render Kinematic Walking Beam & Horsehead
        // Center Saddle Bearing at X = 0, Y = 32.0, Z = 24.0
        poseStack.pushPose();
        poseStack.translate(0.0, 32.0 / 16.0, 24.0 / 16.0);

        // Walking beam rocks around X-axis: pitch angle theta = -0.20 rad * sin(crankAngle)
        float beamPitch = (float) (-0.20f * Math.sin(crankAngle));
        poseStack.rotateDegrees(Axis.XP, (float) Math.toDegrees(beamPitch));

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(TEXTURE),
                (pose, consumer) -> {
                    Matrix4f mat = pose.pose();
                    for (Cuboid c : this.walkingBeamCuboids) {
                        emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                    for (Cuboid c : this.horseheadCuboids) {
                        emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                }
        );
        poseStack.popPose();

        // 4. Render Rotating Cranks & Counterweights (Left and Right)
        // Gearbox crank shaft at X = +/- 5.5, Y = 11.0, Z = 38.0
        float[] crankXOffsets = { -5.5f, 5.5f };
        for (float cx : crankXOffsets) {
            poseStack.pushPose();
            poseStack.translate(cx / 16.0, 11.0 / 16.0, 38.0 / 16.0);
            poseStack.rotateDegrees(Axis.XP, (float) Math.toDegrees(crankAngle));

            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(TEXTURE),
                    (pose, consumer) -> {
                        Matrix4f mat = pose.pose();
                        for (Cuboid c : this.crankCuboids) {
                            emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                        }
                    }
            );
            poseStack.popPose();
        }

        // 5. Render Pitman Connecting Arms (Left and Right)
        // Connects from crank pin (radius 6.0 at angle crankAngle) to beam equalizer
        for (float cx : crankXOffsets) {
            poseStack.pushPose();
            // Crank pin position in world
            double pinY = 11.0 + 6.0 * Math.sin(crankAngle);
            double pinZ = 38.0 + 6.0 * Math.cos(crankAngle);

            // Equalizer position in world (beam rear at Z = 24.0 + 14.0 * cos(beamPitch), Y = 32.0 + 14.0 * sin(beamPitch))
            double eqY = 32.0 + 14.0 * Math.sin(beamPitch);
            double eqZ = 24.0 + 14.0 * Math.cos(beamPitch);

            double dY = eqY - pinY;
            double dZ = eqZ - pinZ;
            float pitmanPitch = (float) Math.atan2(dZ, dY);

            poseStack.translate(cx / 16.0, pinY / 16.0, pinZ / 16.0);
            poseStack.rotateDegrees(Axis.XP, (float) Math.toDegrees(pitmanPitch));

            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(TEXTURE),
                    (pose, consumer) -> {
                        Matrix4f mat = pose.pose();
                        for (Cuboid c : this.pitmanCuboids) {
                            emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                        }
                    }
            );
            poseStack.popPose();
        }

        // 6. Render Wireline Bridle & Polished Rod
        // Moves vertically through the wellhead at X = 0, Z = 6.0
        poseStack.pushPose();
        // Front horsehead tip vertical displacement
        double rodY = 31.0 - 3.8 * Math.sin(crankAngle);
        poseStack.translate(0.0, rodY / 16.0, 6.0 / 16.0);

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(TEXTURE),
                (pose, consumer) -> {
                    Matrix4f mat = pose.pose();
                    for (Cuboid c : this.polishedRodCuboids) {
                        emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
                    }
                }
        );
        poseStack.popPose();
    }

    private static void emitCuboid(
            PoseStack.Pose pose,
            Matrix4f mat,
            VertexConsumer vc,
            Cuboid c,
            int light,
            int overlay
    ) {
        float x0 = c.x0() / 16.0f, y0 = c.y0() / 16.0f, z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f, y1 = c.y1() / 16.0f, z1 = c.z1() / 16.0f;

        emitQuad(pose, mat, vc, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0, c.north(), 0, 0, -1, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1, c.south(), 0, 0, 1, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1, c.west(), -1, 0, 0, light, overlay);
        emitQuad(pose, mat, vc, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0, c.east(), 1, 0, 0, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, c.up(), 0, 1, 0, light, overlay);
        emitQuad(pose, mat, vc, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, c.down(), 0, -1, 0, light, overlay);
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            Matrix4f mat,
            VertexConsumer vc,
            float xA, float yA, float zA,
            float xB, float yB, float zB,
            float xC, float yC, float zC,
            float xD, float yD, float zD,
            UVRect uv,
            float nx, float ny, float nz,
            int light,
            int overlay
    ) {
        vc.addVertex(mat, xA, yA, zA).setColor(255, 255, 255, 255).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xB, yB, zB).setColor(255, 255, 255, 255).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xC, yC, zC).setColor(255, 255, 255, 255).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xD, yD, zD).setColor(255, 255, 255, 255).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }
}
