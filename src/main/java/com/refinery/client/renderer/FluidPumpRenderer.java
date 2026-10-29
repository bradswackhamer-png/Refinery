package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.FluidPumpBlock;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.FluidPumpBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FluidPumpRenderer implements BlockEntityRenderer<AbstractFluidPumpBlockEntity, FluidPumpRenderState> {
    private final FluidPumpModel model;

    public FluidPumpRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new FluidPumpModel();
    }

    @Override
    public FluidPumpRenderState createRenderState() {
        return new FluidPumpRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(AbstractFluidPumpBlockEntity blockEntity) {
        AABB box = new AABB(blockEntity.getBlockPos());
        BlockPos target = blockEntity.getTargetPos();
        if (target != null) {
            box = box.minmax(new AABB(target));
        }
        return box.inflate(3.0);
    }

    @Override
    public void extractRenderState(
            AbstractFluidPumpBlockEntity be,
            FluidPumpRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.facing = blockState.hasProperty(FluidPumpBlock.FACING)
                ? blockState.getValue(FluidPumpBlock.FACING)
                : Direction.NORTH;
        state.isActive = blockState.hasProperty(FluidPumpBlock.ACTIVE)
                && blockState.getValue(FluidPumpBlock.ACTIVE);
        state.pumpPos = be.getBlockPos();
        state.targetPos = be.getTargetPos();
        state.hasTarget = state.targetPos != null;
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.frameColor = be.getFrameColor();
    }

    @Override
    public void submit(
            FluidPumpRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        float animTicks = state.gameTime + state.partialTicks;

        // 1. Render Monolithic 3D Fluid Pump Model (Oriented to facing)
        poseStack.pushPose();

        switch (state.facing) {
            case NORTH -> {}
            case SOUTH -> {
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case EAST -> {
                poseStack.translate(1.0, 0.0, 0.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
            case WEST -> {
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
        }

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(FluidPumpModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose,
                        consumer,
                        state.isActive,
                        animTicks,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.frameColor
                )
        );

        poseStack.popPose();

        // 2. Render 3D In-World Suction Hose to target pump point
        if (state.hasTarget && state.targetPos != null) {
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(FluidPumpModel.TEXTURE),
                    (pose, consumer) -> renderSuctionHose(
                            pose,
                            consumer,
                            state.pumpPos,
                            state.facing,
                            state.targetPos,
                            state.isActive,
                            animTicks,
                            state.lightCoords
                    )
            );
        }
    }

    /**
     * Renders a 3D flexible, spiral-ribbed industrial suction hose connecting the pump's
     * suction intake port to the in-world pumping point:
     * - Accurate suction port origin based on block facing
     * - Smooth Cubic Bezier curve with natural gravity catenary sag
     * - Segmented 3D tube geometry with normal lighting
     * - Quick-connect camlock clamp at pump end
     * - Perforated brass suction strainer basket at target fluid point
     * - Active pulsing suction wave animation when pumping
     */
    private static void renderSuctionHose(
            PoseStack.Pose pose,
            com.mojang.blaze3d.vertex.VertexConsumer consumer,
            BlockPos pumpPos,
            Direction facing,
            BlockPos targetPos,
            boolean isActive,
            float animTicks,
            int light
    ) {
        // Calculate suction port local offset relative to pumpPos
        // In local north model: center of suction flange is (0.675, 0.3125, 0.075)
        Vector3f p0 = getSuctionPortOffset(facing);
        Vector3f forwardDir = getSuctionForwardVector(facing);

        // Target point relative to pumpPos: targetPos - pumpPos + (0.5, 0.5, 0.5)
        float tx = (targetPos.getX() - pumpPos.getX()) + 0.5f;
        float ty = (targetPos.getY() - pumpPos.getY()) + 0.5f;
        float tz = (targetPos.getZ() - pumpPos.getZ()) + 0.5f;
        Vector3f p1 = new Vector3f(tx, ty, tz);

        float dist = p0.distance(p1);
        if (dist < 0.15f) return;

        // Tangent control points
        float tLen = Math.min(2.0f, dist * 0.35f);
        Vector3f c0 = new Vector3f(p0).add(new Vector3f(forwardDir).mul(tLen));
        Vector3f c1 = new Vector3f(p1).add(0.0f, Math.min(1.5f, dist * 0.3f), 0.0f); // Enter from above

        int segments = Math.clamp((int) (dist * 3.5f), 12, 60);
        int ringSides = 8;
        float baseRadius = 0.075f; // Hose diameter ~ 2.4 pixels

        Vector3f[] prevRing = new Vector3f[ringSides];
        Vector3f[] currRing = new Vector3f[ringSides];
        Vector3f prevCenter = new Vector3f();
        Vector3f currCenter = new Vector3f();

        Matrix4f matrix = pose.pose();

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;

            // Cubic Bezier interpolation: (1-t)^3 P0 + 3(1-t)^2 t C0 + 3(1-t) t^2 C1 + t^3 P1
            float omt = 1.0f - t;
            float b0 = omt * omt * omt;
            float b1 = 3.0f * omt * omt * t;
            float b2 = 3.0f * omt * t * t;
            float b3 = t * t * t;

            currCenter.x = b0 * p0.x + b1 * c0.x + b2 * c1.x + b3 * p1.x;
            currCenter.y = b0 * p0.y + b1 * c0.y + b2 * c1.y + b3 * p1.y;
            currCenter.z = b0 * p0.z + b1 * c0.z + b2 * c1.z + b3 * p1.z;

            // Gravity catenary sag in Y
            float sag = -0.35f * (float) Math.sin(t * Math.PI) * Math.min(2.5f, dist * 0.12f);
            currCenter.y += sag;

            // Hose radius with subtle pulsing suction wave if active
            float r = baseRadius;
            if (isActive) {
                r *= (1.0f + 0.06f * (float) Math.sin(animTicks * 15.0f - t * 24.0f));
            }
            if (i == 0) r *= 1.25f; // Flange coupling at pump
            if (i == segments) r *= 1.35f; // Suction strainer nozzle

            // Compute tangent direction
            Vector3f tangent;
            if (i < segments) {
                float nextT = (float) (i + 1) / segments;
                float nomt = 1.0f - nextT;
                float nb0 = nomt * nomt * nomt;
                float nb1 = 3.0f * nomt * nomt * nextT;
                float nb2 = 3.0f * nomt * nextT * nextT;
                float nb3 = nextT * nextT * nextT;
                float nx = nb0 * p0.x + nb1 * c0.x + nb2 * c1.x + nb3 * p1.x;
                float ny = nb0 * p0.y + nb1 * c0.y + nb2 * c1.y + nb3 * p1.y + (-0.35f * (float) Math.sin(nextT * Math.PI) * Math.min(2.5f, dist * 0.12f));
                float nz = nb0 * p0.z + nb1 * c0.z + nb2 * c1.z + nb3 * p1.z;
                tangent = new Vector3f(nx - currCenter.x, ny - currCenter.y, nz - currCenter.z);
            } else {
                tangent = new Vector3f(currCenter).sub(prevCenter);
            }
            if (tangent.lengthSquared() < 1e-6f) tangent = new Vector3f(0, 1, 0);
            else tangent.normalize();

            // Find orthogonal vectors normal1 and normal2
            Vector3f arbitrary = Math.abs(tangent.y) < 0.9f ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
            Vector3f normal1 = new Vector3f(tangent).cross(arbitrary).normalize();
            Vector3f normal2 = new Vector3f(tangent).cross(normal1).normalize();

            // Build ring vertices
            for (int k = 0; k < ringSides; k++) {
                double angle = k * (2.0 * Math.PI / ringSides);
                float cosA = (float) Math.cos(angle);
                float sinA = (float) Math.sin(angle);
                Vector3f offset = new Vector3f(normal1).mul(cosA * r).add(new Vector3f(normal2).mul(sinA * r));
                currRing[k] = new Vector3f(currCenter).add(offset);
            }

            // Connect prevRing to currRing with quads
            if (i > 0) {
                FluidPumpModel.UVRect uv = (i >= segments - 1)
                        ? FluidPumpModel.UV_HOSE_STRAINER
                        : (i <= 1 ? FluidPumpModel.UV_HOSE_COUPLING : FluidPumpModel.UV_HOSE_RIBBED);

                for (int k = 0; k < ringSides; k++) {
                    int nextK = (k + 1) % ringSides;
                    Vector3f pA = prevRing[k];
                    Vector3f pB = currRing[k];
                    Vector3f pC = currRing[nextK];
                    Vector3f pD = prevRing[nextK];

                    // Surface normal
                    Vector3f v1 = new Vector3f(pB).sub(pA);
                    Vector3f v2 = new Vector3f(pD).sub(pA);
                    Vector3f n = new Vector3f(v2).cross(v1).normalize();

                    FluidPumpModel.emitQuad(
                            pose,
                            matrix,
                            consumer,
                            pA.x, pA.y, pA.z,
                            pB.x, pB.y, pB.z,
                            pC.x, pC.y, pC.z,
                            pD.x, pD.y, pD.z,
                            uv,
                            n.x, n.y, n.z,
                            light,
                            OverlayTexture.NO_OVERLAY,
                            0xFFFFFFFF
                    );
                }
            }

            // Store currRing into prevRing
            for (int k = 0; k < ringSides; k++) {
                prevRing[k] = currRing[k];
            }
            prevCenter.set(currCenter);
        }
    }

    private static Vector3f getSuctionPortOffset(Direction facing) {
        // Local North suction port center is: (0.675, 0.3125, 0.075)
        float lx = 0.675f;
        float ly = 0.3125f;
        float lz = 0.075f;

        return switch (facing) {
            case NORTH -> new Vector3f(lx, ly, lz);
            case SOUTH -> new Vector3f(1.0f - lx, ly, 1.0f - lz);
            case WEST  -> new Vector3f(lz, ly, 1.0f - lx);
            case EAST  -> new Vector3f(1.0f - lz, ly, lx);
            default    -> new Vector3f(0.5f, 0.3f, 0.5f);
        };
    }

    private static Vector3f getSuctionForwardVector(Direction facing) {
        return switch (facing) {
            case NORTH -> new Vector3f(0.0f, 0.0f, -1.0f);
            case SOUTH -> new Vector3f(0.0f, 0.0f, 1.0f);
            case WEST  -> new Vector3f(-1.0f, 0.0f, 0.0f);
            case EAST  -> new Vector3f(1.0f, 0.0f, 0.0f);
            default    -> new Vector3f(0.0f, -1.0f, 0.0f);
        };
    }
}
