package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.entity.ConveyorBeltBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConveyorBeltRenderer implements BlockEntityRenderer<ConveyorBeltBlockEntity, ConveyorBeltRenderState> {
    private final ConveyorBeltModel model;

    public ConveyorBeltRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ConveyorBeltModel();
    }

    @Override
    public ConveyorBeltRenderState createRenderState() {
        return new ConveyorBeltRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(ConveyorBeltBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }

    @Override
    public void extractRenderState(
            ConveyorBeltBlockEntity be,
            ConveyorBeltRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        if (blockState.hasProperty(ConveyorBeltBlock.FACING)) {
            state.facing = blockState.getValue(ConveyorBeltBlock.FACING);
        } else {
            state.facing = Direction.NORTH;
        }

        if (blockState.hasProperty(ConveyorBeltBlock.CONNECTED_FRONT)) {
            state.connectedFront = blockState.getValue(ConveyorBeltBlock.CONNECTED_FRONT);
        } else {
            state.connectedFront = false;
        }

        if (blockState.hasProperty(ConveyorBeltBlock.CONNECTED_BACK)) {
            state.connectedBack = blockState.getValue(ConveyorBeltBlock.CONNECTED_BACK);
        } else {
            state.connectedBack = false;
        }

        boolean powered = blockState.hasProperty(ConveyorBeltBlock.POWERED) && blockState.getValue(ConveyorBeltBlock.POWERED);
        state.isRunning = !powered;

        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(
            ConveyorBeltRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        float animTicks = state.gameTime + state.partialTicks;

        poseStack.pushPose();

        // Orient local space so local +Z points in the facing direction
        switch (state.facing) {
            case SOUTH -> {
                // Identity orientation
            }
            case NORTH -> {
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case EAST -> {
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
            case WEST -> {
                poseStack.translate(1.0, 0.0, 0.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
        }

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(ConveyorBeltModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.connectedFront,
                        state.connectedBack,
                        state.isRunning,
                        animTicks,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
