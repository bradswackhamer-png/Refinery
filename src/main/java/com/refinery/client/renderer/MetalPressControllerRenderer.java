package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.MetalPressControllerBlock;
import com.refinery.block.entity.MetalPressControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalPressControllerRenderer implements BlockEntityRenderer<MetalPressControllerBlockEntity, MetalPressControllerRenderState> {
    private final AidaPressModel pressModel;

    public MetalPressControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.pressModel = new AidaPressModel(AidaPressModel.createPressLayer().bakeRoot());
    }

    @Override
    public MetalPressControllerRenderState createRenderState() {
        return new MetalPressControllerRenderState();
    }

    @Override
    public void extractRenderState(MetalPressControllerBlockEntity be, MetalPressControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(MetalPressControllerBlock.BLUEPRINT) && blockState.getValue(MetalPressControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.facing = blockState.getValue(MetalPressControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new MetalPressControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(MetalPressControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Blueprint Outline Rendering (when unformed / wrench mode)
        if (state.isBlueprintActive && !state.missingComponents.isEmpty()) {
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
                if (distSq < 100.0) {
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.5, 0.5);

                    submitNodeCollector.submitNameTag(
                            poseStack,
                            Vec3.ZERO,
                            0,
                            formattedName,
                            false,
                            15728880,
                            camera
                    );

                    poseStack.popPose();
                }

                poseStack.popPose();
            }
        }

        // 2. High-Fidelity AIDA NC1-1500 Stamping Press Rendering (when assembled)
        if (state.isAssembled) {
            poseStack.pushPose();

            // Orient model with the multiblock structure based on controller facing
            switch (state.facing) {
                case NORTH -> {}
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

            // Calculate dynamic vertical displacement of the stamping ram
            float displacementPixels = 0.0f;
            float maxStrokePixels = 6.5f;

            if (state.progress > 0) {
                if (state.progress <= 20) {
                    // Power stroke: rapid downward acceleration
                    float t = Math.min(1.0f, (state.progress + state.partialTicks) / 20.0f);
                    displacementPixels = (t * t) * maxStrokePixels;
                } else {
                    // Return stroke: smooth hydraulic retraction
                    float t = Math.min(1.0f, (state.progress - 20 + state.partialTicks) / 20.0f);
                    displacementPixels = (1.0f - t) * maxStrokePixels;
                }
            }

            this.pressModel.setRamDisplacement(displacementPixels);

            // Submit the complete unified AIDA C-frame model
            submitNodeCollector.submitModel(
                    this.pressModel,
                    Unit.INSTANCE,
                    poseStack,
                    AidaPressModel.TEXTURE,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    -1
            );

            poseStack.popPose();
        }
    }
}
