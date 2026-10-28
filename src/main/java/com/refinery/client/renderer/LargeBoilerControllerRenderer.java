package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.LargeBoilerControllerBlock;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeBoilerControllerRenderer implements BlockEntityRenderer<LargeBoilerControllerBlockEntity, LargeBoilerControllerRenderState> {
    private final LargeBoilerModel boilerModel;

    public LargeBoilerControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.boilerModel = new LargeBoilerModel();
    }

    @Override
    public LargeBoilerControllerRenderState createRenderState() {
        return new LargeBoilerControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(LargeBoilerControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(6.0);
    }

    @Override
    public void extractRenderState(
            LargeBoilerControllerBlockEntity be,
            LargeBoilerControllerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(LargeBoilerControllerBlock.BLUEPRINT)
                && blockState.getValue(LargeBoilerControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(LargeBoilerControllerBlock.ASSEMBLED)
                && blockState.getValue(LargeBoilerControllerBlock.ASSEMBLED))
                || be.isAssembled();
        state.isActive = blockState.hasProperty(LargeBoilerControllerBlock.ACTIVE)
                && blockState.getValue(LargeBoilerControllerBlock.ACTIVE);
        state.facing = blockState.getValue(LargeBoilerControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new LargeBoilerControllerRenderState.MissingComponent(
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
            LargeBoilerControllerRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Blueprint Outline Hologram Rendering (when unformed / wrench mode)
        if (state.isBlueprintActive && !state.isAssembled && !state.missingComponents.isEmpty()) {
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

        // 2. Monolithic 3D Package Boiler Model (when assembled)
        if (state.isAssembled) {
            poseStack.pushPose();

            // Orient local space so +X is right (clockwise of facing), +Y is up, +Z is back (opposite of facing)
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

            // Submit monolithic geometry with dynamic twin burner glow
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(LargeBoilerModel.TEXTURE),
                    (pose, consumer) -> this.boilerModel.render(
                            pose,
                            consumer,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.isActive
                    )
            );

            poseStack.popPose();
        }
    }
}
