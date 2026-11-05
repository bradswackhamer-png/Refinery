package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.RefineryControllerBlock;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RefineryControllerRenderer implements BlockEntityRenderer<RefineryControllerBlockEntity, RefineryControllerRenderState> {

    public RefineryControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public RefineryControllerRenderState createRenderState() {
        return new RefineryControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RefineryControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(10.0);
    }

    @Override
    public void extractRenderState(
            RefineryControllerBlockEntity be,
            RefineryControllerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(RefineryControllerBlock.BLUEPRINT)
                && blockState.getValue(RefineryControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(RefineryControllerBlock.ASSEMBLED)
                && blockState.getValue(RefineryControllerBlock.ASSEMBLED)) || be.isAssembled();
        state.isActive = blockState.hasProperty(RefineryControllerBlock.ACTIVE)
                && blockState.getValue(RefineryControllerBlock.ACTIVE);
        state.facing = blockState.getValue(RefineryControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.stage1Enabled = be.isStage1Enabled();
        state.stage2Enabled = be.isStage2Enabled();
        state.stage3Enabled = be.isStage3Enabled();
        state.missingComponents.clear();

        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                if (!be.matchesBlockRequirement(req, targetState)) {
                    state.missingComponents.add(new RefineryControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(
            RefineryControllerRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Render Monolithic 3x9x3 Fractional Distillation Tower when Assembled
        if (state.isAssembled) {
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

            // Center horizontally in the 3x9x3 footprint:
            // Controller is at local (0, 0, 0); tower center is at (0.5, 0.0, 1.5)
            poseStack.translate(0.5, 0.0, 1.5);

            RefineryTowerModel.submit(
                    poseStack,
                    submitNodeCollector,
                    state.lightCoords,
                    state.isActive,
                    state.stage1Enabled,
                    state.stage2Enabled,
                    state.stage3Enabled
            );

            poseStack.popPose();
        }

        // 2. Render 3D Blueprint Wireframe Hologram when Incomplete
        if (!state.isBlueprintActive || state.isAssembled || state.missingComponents.isEmpty()) {
            return;
        }

        for (var comp : state.missingComponents) {
            int dx = right.getStepX() * comp.relX() + back.getStepX() * comp.relZ();
            int dy = comp.relY();
            int dz = right.getStepZ() * comp.relX() + back.getStepZ() * comp.relZ();

            poseStack.pushPose();
            poseStack.translate(dx, dy, dz);

            int outlineColor = BlueprintColors.getOutlineColor(comp.name(), comp.color());
            Component formattedName = BlueprintColors.formatName(comp.name(), comp.color());

            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.block(),
                    RenderTypes.lines(),
                    outlineColor,
                    2.5f,
                    false
            );

            double distSq = (dx * dx) + (dy * dy) + (dz * dz);
            if (distSq < 120.0) {
                BlueprintColors.renderBlueprintLabel(poseStack, submitNodeCollector, camera, formattedName);
            }

            poseStack.popPose();
        }
    }
}
