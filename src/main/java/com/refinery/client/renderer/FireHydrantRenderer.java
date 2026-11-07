package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.RefineryMod;
import com.refinery.block.FireHydrantBlock;
import com.refinery.block.entity.FireHydrantBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 3D BlockEntityRenderer for the AWWA Safety-Yellow Fire Hydrant (`refinery:fire_hydrant`).
 * Renders the multi-flanged hydrant body and animates the top brass pentagonal operating
 * nut when a player fills a bucket from the hydrant.
 */
public class FireHydrantRenderer implements BlockEntityRenderer<FireHydrantBlockEntity, FireHydrantRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/fire_hydrant.png");

    private final FireHydrantModel model = new FireHydrantModel();

    public FireHydrantRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public FireHydrantRenderState createRenderState() {
        return new FireHydrantRenderState();
    }

    @Override
    public void extractRenderState(FireHydrantBlockEntity blockEntity, FireHydrantRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(FireHydrantBlock.FACING)) {
            renderState.facing = state.getValue(FireHydrantBlock.FACING);
        } else {
            renderState.facing = Direction.NORTH;
        }
        if (state.hasProperty(FireHydrantBlock.ACTIVE)) {
            renderState.active = state.getValue(FireHydrantBlock.ACTIVE);
        }
        renderState.valveAnimTicks = blockEntity.getValveAnimTicks();
    }

    @Override
    public void submit(FireHydrantRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        // Orient hydrant around block center so the large pumper nozzle faces the block's FACING direction
        poseStack.translate(0.5f, 0.5f, 0.5f);
        float yRot = switch (renderState.facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f;
        };
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        int light = renderState.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        // Submit static hydrant barrel, flanges, perimeter silver bolts, nozzles, and domed bonnet
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, vc) -> {
            model.renderStaticBody(pose, vc, light, overlay);
        });

        // Submit top brass pentagonal operating stem nut (rotates when dispensing into a bucket)
        poseStack.pushPose();
        if (renderState.valveAnimTicks > 0) {
            poseStack.translate(0.5f, 0.0f, 0.5f);
            float nutAngle = (float) Math.sin(renderState.valveAnimTicks * 0.55f) * 36.0f;
            poseStack.rotateDegrees(Axis.YP, nutAngle);
            poseStack.translate(-0.5f, 0.0f, -0.5f);
        }
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, vc) -> {
            model.renderTopOperatingNut(pose, vc, light, overlay);
        });
        poseStack.popPose();

        poseStack.popPose();
    }
}
