package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.ArcMelterControllerBlock;
import com.refinery.block.entity.ArcMelterControllerBlockEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcMelterControllerRenderer implements BlockEntityRenderer<ArcMelterControllerBlockEntity, ArcMelterControllerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final ArcMelterModel melterModel;

    public ArcMelterControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.melterModel = new ArcMelterModel();
    }

    @Override
    public ArcMelterControllerRenderState createRenderState() {
        return new ArcMelterControllerRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(ArcMelterControllerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(4.0);
    }

    @Override
    public void extractRenderState(ArcMelterControllerBlockEntity be, ArcMelterControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(ArcMelterControllerBlock.BLUEPRINT) && blockState.getValue(ArcMelterControllerBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(ArcMelterControllerBlock.ASSEMBLED) && blockState.getValue(ArcMelterControllerBlock.ASSEMBLED)) || be.isAssembled();
        state.isActive = blockState.hasProperty(ArcMelterControllerBlock.ACTIVE) && blockState.getValue(ArcMelterControllerBlock.ACTIVE);
        state.facing = blockState.getValue(ArcMelterControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.moltenAmount = be.getMoltenAmount();
        state.moltenFluid = be.getMoltenFluid();
        state.missingComponents.clear();

        // Extract input item loaded in top charge hopper (slots 0..3)
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

        // Blueprint diagnostic hologram
        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = (req.x() == 0 && req.y() == 1 && req.z() == 1 && targetState.isAir())
                        || (req.expectedBlock() != null && targetState.is(req.expectedBlock()));

                if (!matches) {
                    state.missingComponents.add(new ArcMelterControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(ArcMelterControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        // 2. Monolithic 3D Electric Arc Furnace Model & Dynamic Animations
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

            boolean smeltingNow = state.isActive || state.progress > 0;

            this.melterModel.render(
                    poseStack,
                    submitNodeCollector,
                    state.lightCoords,
                    smeltingNow,
                    state.progress,
                    state.gameTime,
                    state.partialTicks,
                    state.moltenAmount,
                    state.moltenFluid
            );

            // Render input ore/dust/ingot inside top charge hopper (0, 2, 1) sinking into the crucible as it melts
            if (state.hasActiveItem) {
                poseStack.pushPose();
                float sinkOffset = 0.0f;
                if (smeltingNow) {
                    float p = Mth.clamp((state.progress + state.partialTicks) / (float) ArcMelterControllerBlockEntity.PROCESS_TIME, 0.0f, 1.0f);
                    sinkOffset = p * 0.38f;
                }
                poseStack.translate(0.5f, 2.72f - sinkOffset, 1.5f);
                if (smeltingNow) {
                    float animTicks = state.gameTime + state.partialTicks;
                    poseStack.rotateDegrees(Axis.YP, (animTicks * 3.0f) % 360.0f);
                }
                poseStack.scale(0.50f, 0.50f, 0.50f);
                state.activeItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
