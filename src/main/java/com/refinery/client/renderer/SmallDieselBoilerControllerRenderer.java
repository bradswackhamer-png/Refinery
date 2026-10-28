package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.SmallDieselBoilerControllerBlock;
import com.refinery.block.entity.SmallDieselBoilerControllerBlockEntity;
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
public class SmallDieselBoilerControllerRenderer implements BlockEntityRenderer<SmallDieselBoilerControllerBlockEntity, SmallDieselBoilerControllerRenderState> {
    private final SmallDieselBoilerModel boilerModel;

    public SmallDieselBoilerControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.boilerModel = new SmallDieselBoilerModel();
    }

    @Override
    public SmallDieselBoilerControllerRenderState createRenderState() {
        return new SmallDieselBoilerControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(SmallDieselBoilerControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(4.0);
    }

    @Override
    public void extractRenderState(
            SmallDieselBoilerControllerBlockEntity be,
            SmallDieselBoilerControllerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(SmallDieselBoilerControllerBlock.BLUEPRINT)
                && blockState.getValue(SmallDieselBoilerControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(SmallDieselBoilerControllerBlock.ASSEMBLED)
                && blockState.getValue(SmallDieselBoilerControllerBlock.ASSEMBLED))
                || be.isAssembled();
        state.isActive = blockState.hasProperty(SmallDieselBoilerControllerBlock.ACTIVE)
                && blockState.getValue(SmallDieselBoilerControllerBlock.ACTIVE);
        state.facing = blockState.getValue(SmallDieselBoilerControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new SmallDieselBoilerControllerRenderState.MissingComponent(
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
            SmallDieselBoilerControllerRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

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
            }

            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(SmallDieselBoilerModel.TEXTURE),
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
