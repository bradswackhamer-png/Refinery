package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.SortingConveyorBeltBlock;
import com.refinery.block.entity.SortingConveyorBeltBlockEntity;
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
public class SortingConveyorBeltRenderer implements BlockEntityRenderer<SortingConveyorBeltBlockEntity, SortingConveyorBeltRenderState> {
    private final SortingConveyorBeltModel model;

    public SortingConveyorBeltRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SortingConveyorBeltModel();
    }

    @Override
    public SortingConveyorBeltRenderState createRenderState() {
        return new SortingConveyorBeltRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(SortingConveyorBeltBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }

    @Override
    public void extractRenderState(
            SortingConveyorBeltBlockEntity be,
            SortingConveyorBeltRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        if (blockState.hasProperty(SortingConveyorBeltBlock.FACING)) {
            state.facing = blockState.getValue(SortingConveyorBeltBlock.FACING);
        } else {
            state.facing = Direction.NORTH;
        }

        if (blockState.hasProperty(SortingConveyorBeltBlock.CONNECTED_FRONT)) {
            state.connectedFront = blockState.getValue(SortingConveyorBeltBlock.CONNECTED_FRONT);
        }
        if (blockState.hasProperty(SortingConveyorBeltBlock.CONNECTED_BACK)) {
            state.connectedBack = blockState.getValue(SortingConveyorBeltBlock.CONNECTED_BACK);
        }
        if (blockState.hasProperty(SortingConveyorBeltBlock.CONNECTED_LEFT)) {
            state.connectedLeft = blockState.getValue(SortingConveyorBeltBlock.CONNECTED_LEFT);
        }
        if (blockState.hasProperty(SortingConveyorBeltBlock.CONNECTED_RIGHT)) {
            state.connectedRight = blockState.getValue(SortingConveyorBeltBlock.CONNECTED_RIGHT);
        }

        boolean powered = blockState.hasProperty(SortingConveyorBeltBlock.POWERED) && blockState.getValue(SortingConveyorBeltBlock.POWERED);
        state.isRunning = !powered;

        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
        state.lastDivertedTick = be.getLastDivertedTick();
        state.lastDivertedDir = be.getLastDivertedDir();
    }

    @Override
    public void submit(
            SortingConveyorBeltRenderState state,
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
                RenderTypes.entitySolid(SortingConveyorBeltModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state,
                        animTicks,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
