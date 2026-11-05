package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.BurnerRefineryFurnaceBlock;
import com.refinery.block.ElectricRefineryFurnaceBlock;
import com.refinery.block.FurnacePart;
import com.refinery.block.entity.IRefineryFurnace;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RefineryFurnaceRenderer<T extends BlockEntity & IRefineryFurnace> implements BlockEntityRenderer<T, RefineryFurnaceRenderState> {

    public RefineryFurnaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public RefineryFurnaceRenderState createRenderState() {
        return new RefineryFurnaceRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(3.0);
    }

    @Override
    public void extractRenderState(
            T be,
            RefineryFurnaceRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        BlockState blockState = be.getBlockState();
        if (blockState.hasProperty(BurnerRefineryFurnaceBlock.PART)) {
            state.shouldRender = blockState.getValue(BurnerRefineryFurnaceBlock.PART) == FurnacePart.LOWER;
            state.facing = blockState.getValue(BurnerRefineryFurnaceBlock.FACING);
            state.isActive = blockState.getValue(BurnerRefineryFurnaceBlock.ACTIVE);
        } else if (blockState.hasProperty(ElectricRefineryFurnaceBlock.PART)) {
            state.shouldRender = blockState.getValue(ElectricRefineryFurnaceBlock.PART) == FurnacePart.LOWER;
            state.facing = blockState.getValue(ElectricRefineryFurnaceBlock.FACING);
            state.isActive = blockState.getValue(ElectricRefineryFurnaceBlock.ACTIVE);
        } else {
            state.shouldRender = false;
        }

        state.isElectric = be.isElectric();
        state.temperature = be.getTemperature();
    }

    @Override
    public void submit(
            RefineryFurnaceRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (!state.shouldRender) {
            return;
        }

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
            default -> {}
        }

        RefineryFurnaceModel.submit(
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                state.isElectric,
                state.isActive,
                state.temperature
        );

        poseStack.popPose();
    }
}
