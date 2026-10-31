package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.block.entity.SlopedConveyorBeltBlockEntity;
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
public class SlopedConveyorBeltRenderer implements BlockEntityRenderer<SlopedConveyorBeltBlockEntity, SlopedConveyorBeltRenderState> {
    private final SlopedConveyorBeltModel model;

    public SlopedConveyorBeltRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SlopedConveyorBeltModel();
    }

    @Override
    public SlopedConveyorBeltRenderState createRenderState() {
        return new SlopedConveyorBeltRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(SlopedConveyorBeltBlockEntity blockEntity) {
        // Taller bounding box because it extends upward to the next block height
        return new AABB(blockEntity.getBlockPos()).inflate(0.0, 0.5, 0.0);
    }

    @Override
    public void extractRenderState(
            SlopedConveyorBeltBlockEntity be,
            SlopedConveyorBeltRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        if (blockState.hasProperty(SlopedConveyorBeltBlock.FACING)) {
            state.facing = blockState.getValue(SlopedConveyorBeltBlock.FACING);
        } else {
            state.facing = Direction.NORTH;
        }

        if (blockState.hasProperty(SlopedConveyorBeltBlock.SLOPE)) {
            state.slope = blockState.getValue(SlopedConveyorBeltBlock.SLOPE);
        } else {
            state.slope = SlopedConveyorBeltBlock.SlopeDirection.UP;
        }

        boolean powered = blockState.hasProperty(SlopedConveyorBeltBlock.POWERED) && blockState.getValue(SlopedConveyorBeltBlock.POWERED);
        state.isRunning = !powered;

        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(
            SlopedConveyorBeltRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        float animTicks = state.gameTime + state.partialTicks;

        poseStack.pushPose();

        // Orient local space so local +Z points in the facing direction
        switch (state.facing) {
            case SOUTH -> {
                // Identity
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
                RenderTypes.entitySolid(SlopedConveyorBeltModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.slope,
                        state.isRunning,
                        animTicks,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
