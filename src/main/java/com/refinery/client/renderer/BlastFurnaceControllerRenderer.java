package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.BlastFurnaceControllerBlock;
import com.refinery.block.entity.BlastFurnaceControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
public class BlastFurnaceControllerRenderer implements BlockEntityRenderer<BlastFurnaceControllerBlockEntity, BlastFurnaceControllerRenderState> {

    private final BlastFurnaceModel model = new BlastFurnaceModel();

    public BlastFurnaceControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BlastFurnaceControllerRenderState createRenderState() {
        return new BlastFurnaceControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlastFurnaceControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(6.0);
    }

    @Override
    public void extractRenderState(
            BlastFurnaceControllerBlockEntity be,
            BlastFurnaceControllerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(BlastFurnaceControllerBlock.BLUEPRINT) && blockState.getValue(BlastFurnaceControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.isActive = blockState.hasProperty(BlastFurnaceControllerBlock.ACTIVE) && blockState.getValue(BlastFurnaceControllerBlock.ACTIVE);
        state.facing = blockState.getValue(BlastFurnaceControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.isAir() ? targetState.isAir() : (req.expectedBlock() != null && targetState.is(req.expectedBlock()));

                if (!matches) {
                    state.missingComponents.add(new BlastFurnaceControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(BlastFurnaceControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. High-Fidelity 3D Multiblock Model (when assembled)
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

            this.model.render(
                    poseStack,
                    submitNodeCollector,
                    state.lightCoords,
                    state.isActive,
                    state.progress,
                    state.gameTime,
                    state.partialTicks
            );

            poseStack.popPose();
        }

        // 2. 3D Blueprint Projection (when unformed)
        if (!state.isBlueprintActive || state.missingComponents.isEmpty()) {
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

            // Floating Name Tag (visible up to 10 blocks)
            double distSq = (dx * dx) + (dy * dy) + (dz * dz);
            if (distSq < 100.0) {
                BlueprintColors.renderBlueprintLabel(poseStack, submitNodeCollector, camera, formattedName);
            }

            poseStack.popPose();
        }
    }
}
