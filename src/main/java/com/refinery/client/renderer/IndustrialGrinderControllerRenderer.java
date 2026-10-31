package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.IndustrialGrinderControllerBlock;
import com.refinery.block.entity.IndustrialGrinderControllerBlockEntity;
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
public class IndustrialGrinderControllerRenderer implements BlockEntityRenderer<IndustrialGrinderControllerBlockEntity, IndustrialGrinderControllerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final IndustrialGrinderModel grinderModel;

    public IndustrialGrinderControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.grinderModel = new IndustrialGrinderModel();
    }

    @Override
    public IndustrialGrinderControllerRenderState createRenderState() {
        return new IndustrialGrinderControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(IndustrialGrinderControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(4.0);
    }

    @Override
    public void extractRenderState(IndustrialGrinderControllerBlockEntity be, IndustrialGrinderControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(IndustrialGrinderControllerBlock.BLUEPRINT) && blockState.getValue(IndustrialGrinderControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(IndustrialGrinderControllerBlock.ASSEMBLED) && blockState.getValue(IndustrialGrinderControllerBlock.ASSEMBLED)) || be.isAssembled();
        state.isActive = blockState.hasProperty(IndustrialGrinderControllerBlock.ACTIVE) && blockState.getValue(IndustrialGrinderControllerBlock.ACTIVE);
        state.facing = blockState.getValue(IndustrialGrinderControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        // Extract input item loaded in top hopper
        state.hasActiveItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack inputItem = ItemStack.EMPTY;
            for (int i = 0; i < 4; i++) {
                ItemStack s = be.getItem(i);
                if (!s.isEmpty()) {
                    inputItem = s;
                    break;
                }
            }
            if (!inputItem.isEmpty()) {
                this.itemModelResolver.updateForTopItem(state.activeItemState, inputItem, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
                state.hasActiveItem = true;
            } else {
                this.itemModelResolver.updateForTopItem(state.activeItemState, ItemStack.EMPTY, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            }
        } else if (be.getLevel() != null) {
            this.itemModelResolver.updateForTopItem(state.activeItemState, ItemStack.EMPTY, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        }

        // Extract output item on right discharge conveyor
        state.hasOutputItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack outputItem = ItemStack.EMPTY;
            for (int i = 4; i < 8; i++) {
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

        // Blueprint diagnostic hologram
        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = (req.x() == 0 && req.y() == 1 && req.z() == 1 && targetState.isAir())
                        || (req.expectedBlock() != null && targetState.is(req.expectedBlock()));

                if (!matches) {
                    state.missingComponents.add(new IndustrialGrinderControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(IndustrialGrinderControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        // 2. Monolithic 3D Rock Crusher Plant Model & In-World Hopper/Conveyor Item Animation
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

            // Compute blade rotation angle (spins when actively grinding)
            float animTicks = state.gameTime + state.partialTicks;
            boolean grindingNow = state.isActive || state.progress > 0;
            float bladeAngle = grindingNow ? (animTicks * 14.0f) % 360.0f : 0.0f;

            // A. Submit complete monolithic crusher geometry (with 3D spinning blade rotors in top hopper)
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(IndustrialGrinderModel.TEXTURE),
                    (pose, consumer) -> this.grinderModel.render(
                            pose,
                            consumer,
                            grindingNow,
                            bladeAngle,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY
                    )
            );

            // B. Render Loaded Input Material inside the Top Hopper Bowl between the Spinning Blades
            if (state.hasActiveItem) {
                // Draw 3 chunks/items along the length of the hopper bowl (Z = 1.1, 1.5, 1.9) being pulled into the blades
                float[] zOffsets = {1.12f, 1.50f, 1.88f};
                for (int i = 0; i < zOffsets.length; i++) {
                    poseStack.pushPose();
                    float phase = animTicks * 5.0f + (i * 2.1f);
                    float shakeX = grindingNow ? (float) Math.sin(phase) * 0.035f : 0.0f;
                    float shakeY = grindingNow ? (float) Math.abs(Math.cos(phase * 0.8f)) * 0.05f : 0.0f;
                    float sinkY = grindingNow ? (state.progress / 60.0f) * 0.14f : 0.0f;

                    // Hopper center is X = 0.5 (8/16), Y = 2.72 (43.5/16), Z = zOffsets[i]
                    poseStack.translate(0.5f + shakeX, 2.74f - sinkY + shakeY, zOffsets[i]);
                    poseStack.rotateDegrees(Axis.YP, (i * 60.0f) + (grindingNow ? animTicks * 3.0f : 0.0f));
                    poseStack.rotateDegrees(Axis.XP, grindingNow ? (float) Math.sin(phase) * 18.0f : 15.0f);
                    poseStack.scale(0.42f, 0.42f, 0.42f);
                    state.activeItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }

            // C. Animate Actual Crushed Output Material traveling up the Right Inclined Discharge Conveyor
            if (state.hasOutputItem) {
                // Render a stream of 2 items spaced along the inclined discharge belt
                for (int i = 0; i < 2; i++) {
                    poseStack.pushPose();
                    float cycle = grindingNow
                            ? (((animTicks * 0.03f) + (i * 0.45f)) % 0.90f)
                            : (0.20f + i * 0.40f);
                    float posX = 1.40f + cycle;
                    float posY = 1.08f + (cycle * 0.72f);
                    poseStack.translate(posX, posY, 1.50f);
                    poseStack.rotateDegrees(Axis.ZP, 35.0f); // Align flat with the rising belt slope
                    poseStack.rotateDegrees(Axis.XP, 90.0f); // Lay flat on conveyor belt
                    poseStack.scale(0.38f, 0.38f, 0.38f);
                    state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }

            poseStack.popPose();
        }
    }
}
