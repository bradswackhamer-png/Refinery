package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.FeedingTroughBlock;
import com.refinery.block.entity.FeedingTroughBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FeedingTroughRenderer implements BlockEntityRenderer<FeedingTroughBlockEntity, FeedingTroughRenderState> {
    private final FeedingTroughModel model;

    public FeedingTroughRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new FeedingTroughModel();
    }

    @Override
    public FeedingTroughRenderState createRenderState() {
        return new FeedingTroughRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(FeedingTroughBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.0);
    }

    @Override
    public void extractRenderState(
            FeedingTroughBlockEntity be,
            FeedingTroughRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        if (blockState.hasProperty(FeedingTroughBlock.FACING)) {
            state.facing = blockState.getValue(FeedingTroughBlock.FACING);
        } else {
            state.facing = Direction.NORTH;
        }

        if (blockState.hasProperty(FeedingTroughBlock.PART)) {
            state.part = blockState.getValue(FeedingTroughBlock.PART);
        } else {
            state.part = BedPart.FOOT;
        }

        // Only the FOOT half renders the full 2-block trough assembly
        state.isMainRenderer = (state.part == BedPart.FOOT);

        // Fill fraction based on total food items in the trough
        state.fillFraction = be.getFillFraction();
    }

    @Override
    public void submit(
            FeedingTroughRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (!state.isMainRenderer) {
            return;
        }

        poseStack.pushPose();

        // Orient 2-block trough so local +Z extends towards FACING
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
                RenderTypes.entitySolid(FeedingTroughModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.fillFraction,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
