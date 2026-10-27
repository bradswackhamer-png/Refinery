package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.MetalPressControllerBlock;
import com.refinery.block.entity.MetalPressControllerBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalPressControllerRenderer implements BlockEntityRenderer<MetalPressControllerBlockEntity, MetalPressControllerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final AidaPressModel pressModel;

    public MetalPressControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.pressModel = new AidaPressModel();
    }

    @Override
    public MetalPressControllerRenderState createRenderState() {
        return new MetalPressControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(MetalPressControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(4.0);
    }

    @Override
    public void extractRenderState(
            MetalPressControllerBlockEntity be,
            MetalPressControllerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(MetalPressControllerBlock.BLUEPRINT)
                && blockState.getValue(MetalPressControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(MetalPressControllerBlock.ASSEMBLED)
                && blockState.getValue(MetalPressControllerBlock.ASSEMBLED))
                || be.isAssembled();
        state.isActive = blockState.hasProperty(MetalPressControllerBlock.ACTIVE)
                && blockState.getValue(MetalPressControllerBlock.ACTIVE);
        state.facing = blockState.getValue(MetalPressControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.structureOffsetX = be.getStructureOffsetX();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        // Extract active workpiece on the conveyor belt if present
        state.hasActiveItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack inputStack = be.getItem(0);
            ItemStack outputStack = be.getItem(1);
            ItemStack displayStack = !inputStack.isEmpty() ? inputStack : outputStack;
            if (!displayStack.isEmpty()) {
                this.itemModelResolver.updateForTopItem(
                        state.activeItemState,
                        displayStack,
                        ItemDisplayContext.FIXED,
                        be.getLevel(),
                        null,
                        0
                );
                state.hasActiveItem = true;
            }
        }

        if (state.isBlueprintActive && be.getLevel() != null && !state.isAssembled) {
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
    public void submit(
            MetalPressControllerRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Blueprint Outline Rendering (when unformed / wrench mode)
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

        // 2. 3-Block-Wide Conveyor Bed + AIDA NC1-1500 Stamping Press Rendering (when assembled)
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

            // Align with the 3-block-wide footprint if controller was placed on left/right end
            if (state.structureOffsetX != 0) {
                poseStack.translate(state.structureOffsetX, 0.0f, 0.0f);
            }
            if (state.structureDepth == 1) {
                poseStack.translate(0.0f, 0.0f, -0.5f);
            }

            // Calculate dynamic vertical displacement of the AIDA stamping ram
            float displacementPixels = 0.0f;
            float maxStrokePixels = 3.3f;
            float cycleProgress = 0.0f;

            if (state.progress > 0) {
                cycleProgress = state.progress + state.partialTicks;
            } else if (state.isActive) {
                cycleProgress = (state.gameTime % 40L) + state.partialTicks;
            }

            if (cycleProgress > 0.0f) {
                if (cycleProgress <= 20.0f) {
                    // Power stroke: rapid downward acceleration onto the center bolster anvil
                    float t = Math.min(1.0f, cycleProgress / 20.0f);
                    displacementPixels = (t * t) * maxStrokePixels;
                } else {
                    // Return stroke: smooth hydraulic retraction
                    float t = Math.min(1.0f, (cycleProgress - 20.0f) / 20.0f);
                    displacementPixels = (1.0f - t) * maxStrokePixels;
                }
            }

            final float finalDisplacement = displacementPixels;
            final int light = state.lightCoords;

            // Submit the complete 3-block-wide Conveyor + AIDA Press geometry
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(AidaPressModel.TEXTURE),
                    (pose, consumer) -> this.pressModel.render(
                            pose,
                            consumer,
                            light,
                            OverlayTexture.NO_OVERLAY,
                            finalDisplacement
                    )
            );

            // Render workpiece travelling across the 3-block-wide conveyor belt if present
            if (state.hasActiveItem) {
                poseStack.pushPose();
                float beltRatio = (cycleProgress > 0.0f) ? Math.min(1.0f, cycleProgress / 40.0f) : 0.5f;
                // Travels from left end of conveyor (-0.4f), pauses under center die (0.5f) during stamp, exits right (1.4f)
                float itemX;
                if (beltRatio < 0.35f) {
                    itemX = -0.4f + (beltRatio / 0.35f) * 0.9f;
                } else if (beltRatio < 0.65f) {
                    itemX = 0.5f;
                } else {
                    itemX = 0.5f + ((beltRatio - 0.65f) / 0.35f) * 0.9f;
                }
                poseStack.translate(itemX, 12.65f / 16.0f, 1.0f);
                poseStack.rotateDegrees(Axis.XP, 90.0f);
                poseStack.scale(0.55f, 0.55f, 0.55f);
                state.activeItemState.submit(poseStack, submitNodeCollector, light, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
