package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.CastingPressControllerBlock;
import com.refinery.block.entity.CastingPressControllerBlockEntity;
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
public class CastingPressControllerRenderer implements BlockEntityRenderer<CastingPressControllerBlockEntity, CastingPressControllerRenderState> {
    private final ItemModelResolver itemModelResolver;

    public CastingPressControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CastingPressControllerRenderState createRenderState() {
        return new CastingPressControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(CastingPressControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(4.0);
    }

    @Override
    public void extractRenderState(CastingPressControllerBlockEntity be, CastingPressControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(CastingPressControllerBlock.BLUEPRINT) && blockState.getValue(CastingPressControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(CastingPressControllerBlock.ASSEMBLED) && blockState.getValue(CastingPressControllerBlock.ASSEMBLED)) || be.isAssembled();
        state.isActive = blockState.hasProperty(CastingPressControllerBlock.ACTIVE) && blockState.getValue(CastingPressControllerBlock.ACTIVE);
        state.facing = blockState.getValue(CastingPressControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.moltenAmount = be.getMoltenAmount();
        state.moltenFluid = be.getMoltenFluid();
        state.missingComponents.clear();

        // Extract cast output item from slots 0..3 for run-out roller table rendering
        state.hasOutputItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack outputItem = ItemStack.EMPTY;
            for (int i = 0; i < 4; i++) {
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
                    state.missingComponents.add(new CastingPressControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(CastingPressControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Hologram Blueprint Wireframe Rendering
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

        // 2. Monolithic 3D Continuous Casting Press Model & Dynamic Animations
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

            boolean pressingNow = state.isActive || state.progress > 0;

            CastingPressModel.render(
                    poseStack,
                    submitNodeCollector,
                    state.lightCoords,
                    pressingNow,
                    state.progress,
                    state.gameTime,
                    state.partialTicks,
                    state.moltenAmount,
                    state.moltenFluid
            );

            // Render finished cast ingots/blocks traveling on the right-hand motorized run-out roller table (1, 0, 0)
            if (state.hasOutputItem) {
                float animTicks = state.gameTime + state.partialTicks;
                for (int i = 0; i < 2; i++) {
                    poseStack.pushPose();
                    float zPos = pressingNow
                            ? (1.25f - (((animTicks * 0.025f) + (i * 0.55f)) % 1.05f))
                            : (0.35f + i * 0.50f);
                    poseStack.translate(1.53f, 0.62f, zPos);
                    poseStack.rotateDegrees(Axis.XP, 90.0f);
                    poseStack.scale(0.45f, 0.45f, 0.45f);
                    state.outputItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }

            poseStack.popPose();
        }
    }
}
