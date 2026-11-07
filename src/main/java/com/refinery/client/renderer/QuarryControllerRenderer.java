package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.QuarryControllerBlock;
import com.refinery.block.entity.QuarryControllerBlockEntity;
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
public class QuarryControllerRenderer implements BlockEntityRenderer<QuarryControllerBlockEntity, QuarryControllerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final QuarryModel quarryModel;

    public QuarryControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.quarryModel = new QuarryModel();
    }

    @Override
    public QuarryControllerRenderState createRenderState() {
        return new QuarryControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(QuarryControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(blockEntity.getRadius() + 6.0);
    }

    @Override
    public void extractRenderState(QuarryControllerBlockEntity be, QuarryControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(QuarryControllerBlock.BLUEPRINT) && blockState.getValue(QuarryControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(QuarryControllerBlock.ASSEMBLED) && blockState.getValue(QuarryControllerBlock.ASSEMBLED)) || be.isAssembled();
        state.isActive = blockState.hasProperty(QuarryControllerBlock.ACTIVE) && blockState.getValue(QuarryControllerBlock.ACTIVE);
        state.facing = blockState.hasProperty(QuarryControllerBlock.FACING) ? blockState.getValue(QuarryControllerBlock.FACING) : Direction.NORTH;
        state.controllerPos = be.getBlockPos();
        state.radius = be.getRadius();
        state.phase = be.getPhase();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        // Extract missing components for blueprint projection if active
        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                if (!be.isComponentValid(req, be.getLevel().getBlockState(targetPos))) {
                    state.missingComponents.add(new QuarryControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }

        // Extract an item from buffer to animate on the export conveyor chutes
        state.hasOutputItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack outputItem = ItemStack.EMPTY;
            for (int i = 0; i < 9; i++) {
                ItemStack s = be.getItem(i);
                if (!s.isEmpty()) {
                    outputItem = s;
                    break;
                }
            }
            if (!outputItem.isEmpty()) {
                this.itemModelResolver.updateForTopItem(state.outputItemState, outputItem, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
                state.hasOutputItem = true;
            } else {
                this.itemModelResolver.updateForTopItem(state.outputItemState, ItemStack.EMPTY, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            }
        } else if (be.getLevel() != null) {
            this.itemModelResolver.updateForTopItem(state.outputItemState, ItemStack.EMPTY, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(QuarryControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Blueprint Hologram Wireframe Rendering
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
                    BlueprintColors.renderBlueprintLabel(poseStack, submitNodeCollector, camera, formattedName);
                }

                poseStack.popPose();
            }
        }

        // 2. Monolithic 3D Quarry Rig Model & In-World Conveyor Item Animation
        if (state.isAssembled) {
            poseStack.pushPose();

            // Orient local coordinate space
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

            // A. Submit complete monolithic quarry geometry (with glowing floodlights & amber beacon)
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(QuarryModel.TEXTURE),
                    (pose, consumer) -> this.quarryModel.render(
                            pose,
                            consumer,
                            state.isActive,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY
                    )
            );

            // B. Animate Mined Items riding along the Side Conveyors & Built-in Rear Spoil Chute
            if (state.hasOutputItem && state.isActive) {
                float animTicks = state.gameTime + state.partialTicks;

                // 1. Left Conveyor (X = -0.1..-0.9, Z = 1.375, Y = 0.35)
                poseStack.pushPose();
                float leftCycle = (((animTicks * 0.05f) + 0.33f) % 0.8f);
                poseStack.translate(-0.1f - leftCycle, 0.35f, 1.375f);
                poseStack.rotateDegrees(Axis.XP, 90.0f);
                poseStack.scale(0.38f, 0.38f, 0.38f);
                state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();

                // 2. Right Conveyor (X = 1.1..1.9, Z = 1.375, Y = 0.35)
                poseStack.pushPose();
                float rightCycle = (((animTicks * 0.05f) + 0.66f) % 0.8f);
                poseStack.translate(1.1f + rightCycle, 0.35f, 1.375f);
                poseStack.rotateDegrees(Axis.XP, 90.0f);
                poseStack.scale(0.38f, 0.38f, 0.38f);
                state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();

                // 3. Rear Conveyor Chute (Throw-Back Spoil Rolling Off into Pit) (X = 0.5, Z = 2.1..2.9, Y = 0.35)
                poseStack.pushPose();
                float rearCycle = (((animTicks * 0.05f) + 0.5f) % 0.8f);
                poseStack.translate(0.5f, 0.35f, 2.1f + rearCycle);
                poseStack.rotateDegrees(Axis.XP, 90.0f);
                poseStack.scale(0.38f, 0.38f, 0.38f);
                state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
