package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.DischargeMode;
import com.refinery.block.FluidDischargeBlock;
import com.refinery.block.entity.FluidDischargeBlockEntity;
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
public class FluidDischargeRenderer implements BlockEntityRenderer<FluidDischargeBlockEntity, FluidDischargeRenderState> {
    private final FluidDischargeModel model;

    public FluidDischargeRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new FluidDischargeModel();
    }

    @Override
    public FluidDischargeRenderState createRenderState() {
        return new FluidDischargeRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(FluidDischargeBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(0.5);
    }

    @Override
    public void extractRenderState(
            FluidDischargeBlockEntity be,
            FluidDischargeRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.facing = blockState.hasProperty(FluidDischargeBlock.FACING)
                ? blockState.getValue(FluidDischargeBlock.FACING)
                : Direction.NORTH;
        state.mode = blockState.hasProperty(FluidDischargeBlock.MODE)
                ? blockState.getValue(FluidDischargeBlock.MODE)
                : be.getMode();
        state.isActive = blockState.hasProperty(FluidDischargeBlock.ACTIVE)
                && blockState.getValue(FluidDischargeBlock.ACTIVE);
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(
            FluidDischargeRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        float animTicks = state.gameTime + state.partialTicks;

        poseStack.pushPose();

        // Rotate around center (0.5, 0, 0.5) to match facing direction
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
                RenderTypes.entitySolid(FluidDischargeModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose,
                        consumer,
                        state.mode,
                        state.isActive,
                        animTicks,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
