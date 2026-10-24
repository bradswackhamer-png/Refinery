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
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndustrialGrinderControllerRenderer implements BlockEntityRenderer<IndustrialGrinderControllerBlockEntity, IndustrialGrinderControllerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final CrusherDrumModel drum1Model;
    private final CrusherDrumModel drum2Model;

    public IndustrialGrinderControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.drum1Model = new CrusherDrumModel(CrusherDrumModel.createDrumLayer(true).bakeRoot());
        this.drum2Model = new CrusherDrumModel(CrusherDrumModel.createDrumLayer(false).bakeRoot());
    }

    @Override
    public IndustrialGrinderControllerRenderState createRenderState() {
        return new IndustrialGrinderControllerRenderState();
    }

    @Override
    public void extractRenderState(IndustrialGrinderControllerBlockEntity be, IndustrialGrinderControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(IndustrialGrinderControllerBlock.BLUEPRINT) && blockState.getValue(IndustrialGrinderControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.isActive = blockState.hasProperty(IndustrialGrinderControllerBlock.ACTIVE) && blockState.getValue(IndustrialGrinderControllerBlock.ACTIVE);
        state.facing = blockState.getValue(IndustrialGrinderControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        // Extract active crushing item if machine is assembled and processing
        state.hasActiveItem = false;
        if (state.isAssembled && be.getLevel() != null) {
            ItemStack processingItem = ItemStack.EMPTY;
            for (int i = 0; i < 4; i++) {
                ItemStack s = be.getItem(i);
                if (!s.isEmpty()) {
                    processingItem = s;
                    break;
                }
            }
            if (!processingItem.isEmpty()) {
                this.itemModelResolver.updateForTopItem(state.activeItemState, processingItem, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
                state.hasActiveItem = true;
            }
        }

        // Blueprint diagnostic hologram
        if (state.isBlueprintActive && be.getLevel() != null) {
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

        // 2. Animated Heavy Crusher Drums & Crushing Block (Center chamber at local X: 0, Y: 1, Z: 1)
        if (state.isAssembled) {
            int chamberDx = back.getStepX();
            int chamberDy = 1;
            int chamberDz = back.getStepZ();

            poseStack.pushPose();
            // Move into the center of the hollow crushing chamber
            poseStack.translate(chamberDx + 0.5, chamberDy + 0.5, chamberDz + 0.5);
            // Orient along multiblock facing direction
            poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());

            // Rotation speed: rapid heavy crushing when active, slow industrial hum when idle
            float angle;
            if (state.isActive) {
                angle = (state.gameTime + state.partialTicks) * 16.0f;
            } else {
                angle = (state.gameTime + state.partialTicks) * 1.5f;
            }

            // A. Left Spiked Crusher Drum (local X = -0.23, spins clockwise inward)
            poseStack.pushPose();
            poseStack.translate(-0.23, 0.0, 0.0);
            poseStack.rotateDegrees(Axis.ZP, angle);
            submitNodeCollector.submitModel(this.drum1Model, Unit.INSTANCE, poseStack, CrusherDrumModel.TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            poseStack.popPose();

            // B. Right Spiked Crusher Drum (local X = +0.23, spins counter-clockwise inward, 22.5 deg offset)
            poseStack.pushPose();
            poseStack.translate(0.23, 0.0, 0.0);
            poseStack.rotateDegrees(Axis.ZP, -angle + 22.5f);
            submitNodeCollector.submitModel(this.drum2Model, Unit.INSTANCE, poseStack, CrusherDrumModel.TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            poseStack.popPose();

            // C. Crushed Block / Ore Kinetic Animation
            if (state.hasActiveItem && (state.isActive || state.progress > 0)) {
                poseStack.pushPose();

                float progressRatio = Math.min(1.0f, (float) state.progress / 100.0f);
                float shakeX = (float) Math.sin((state.gameTime + state.partialTicks) * 3.2f) * 0.035f;
                float shakeY = (float) Math.cos((state.gameTime + state.partialTicks) * 3.8f) * 0.025f;
                float shakeRot = (float) Math.sin((state.gameTime + state.partialTicks) * 5.5f) * 8.0f;

                // Suspended right in the nip between the spiked drums, drawn downwards as crushed
                float posY = 0.14f - (progressRatio * 0.18f) + shakeY;
                poseStack.translate(shakeX, posY, shakeX * 0.5f);
                poseStack.rotateDegrees(Axis.YP, shakeRot);
                poseStack.rotateDegrees(Axis.ZP, shakeX * 50.0f);

                // Pulverization scale down
                float scale = 0.45f * (1.0f - (progressRatio * 0.35f));
                poseStack.scale(scale, scale, scale);

                state.activeItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
