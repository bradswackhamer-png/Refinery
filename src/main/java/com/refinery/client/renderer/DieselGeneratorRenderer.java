package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.DieselGeneratorBlock;
import com.refinery.block.GeneratorPart;
import com.refinery.block.entity.DieselGeneratorBlockEntity;
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
public class DieselGeneratorRenderer implements BlockEntityRenderer<DieselGeneratorBlockEntity, DieselGeneratorRenderState> {
    private final DieselGeneratorModel model;

    public DieselGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new DieselGeneratorModel();
    }

    @Override
    public DieselGeneratorRenderState createRenderState() {
        return new DieselGeneratorRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(DieselGeneratorBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.0);
    }

    @Override
    public void extractRenderState(
            DieselGeneratorBlockEntity be,
            DieselGeneratorRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        if (blockState.hasProperty(DieselGeneratorBlock.FACING)) {
            state.facing = blockState.getValue(DieselGeneratorBlock.FACING);
        } else {
            state.facing = Direction.NORTH;
        }

        if (blockState.hasProperty(DieselGeneratorBlock.PART)) {
            state.part = blockState.getValue(DieselGeneratorBlock.PART);
        } else {
            state.part = GeneratorPart.LEFT;
        }

        // Only the master LEFT block renders the full 2-block monolithic model
        state.isMainRenderer = (state.part == GeneratorPart.LEFT);
        state.active = be.isActive();
    }

    @Override
    public void submit(
            DieselGeneratorRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (!state.isMainRenderer) {
            return;
        }

        poseStack.pushPose();

        // Orient 2-block enclosure so local length (X: 0..32) and depth (Z: 0..16) match the world block placement
        switch (state.facing) {
            case NORTH -> {
                // Identity
            }
            case SOUTH -> {
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case EAST -> {
                poseStack.translate(1.0, 0.0, 0.0);
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
            case WEST -> {
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
        }

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(DieselGeneratorModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.active,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
