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

        // Extract input item on feed conveyor
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
            }
        }

        // Extract output item on discharge conveyor
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
            }
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

        // 2. Monolithic 3D Rock Crusher Plant Model & In-World Conveyor Item Animation
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

            // A. Submit complete monolithic crusher geometry
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(IndustrialGrinderModel.TEXTURE),
                    (pose, consumer) -> this.grinderModel.render(
                            pose,
                            consumer,
                            state.isActive,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY
                    )
            );

            // B. Animate Input Material riding in on the Left Feed Conveyor towards the Hopper
            if (state.hasActiveItem) {
                poseStack.pushPose();
                // Material travels on feed conveyor (local X from -1.4 towards -0.4, Y: 2.05, Z: 1.5)
                float travel = state.isActive ? ((state.gameTime + state.partialTicks) * 0.04f) % 0.8f : 0.0f;
                poseStack.translate(-1.3 + travel, 2.02, 1.5);
                poseStack.rotateDegrees(Axis.YP, (state.gameTime + state.partialTicks) * 2.0f);
                poseStack.scale(0.40f, 0.40f, 0.40f);
                state.activeItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();

                // Material dropping into hopper mouth and vibrating/grinding
                if (state.isActive || state.progress > 0) {
                    poseStack.pushPose();
                    float shake = (float) Math.sin((state.gameTime + state.partialTicks) * 4.0f) * 0.04f;
                    poseStack.translate(0.5 + shake, 2.3 + shake * 0.5f, 1.5 + shake * 0.3f);
                    poseStack.scale(0.35f, 0.35f, 0.35f);
                    state.activeItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }

            // C. Animate Crushed Product traveling out on the Right Inclined Discharge Conveyor
            if (state.hasOutputItem) {
                poseStack.pushPose();
                // Crushed product climbing inclined conveyor (local X from 1.5 towards 2.2, Y from 1.1 towards 1.5, Z: 1.5)
                float travelOut = state.isActive ? ((state.gameTime + state.partialTicks) * 0.035f) % 0.7f : 0.0f;
                float posX = 1.45f + travelOut;
                float posY = 1.05f + (travelOut * 0.65f);
                poseStack.translate(posX, posY, 1.5);
                poseStack.rotateDegrees(Axis.ZP, -25.0f); // Match incline angle
                poseStack.scale(0.35f, 0.35f, 0.35f);
                state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
