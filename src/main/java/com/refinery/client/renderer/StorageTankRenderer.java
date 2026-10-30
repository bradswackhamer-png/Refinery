package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.StorageTankDrainValveBlock;
import com.refinery.block.entity.StorageTankBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StorageTankRenderer implements BlockEntityRenderer<StorageTankBlockEntity, StorageTankRenderState> {
    private final StorageTankModel tankModel;

    public StorageTankRenderer(BlockEntityRendererProvider.Context context) {
        this.tankModel = new StorageTankModel();
    }

    @Override
    public StorageTankRenderState createRenderState() {
        return new StorageTankRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(StorageTankBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(12.0);
    }

    @Override
    public void extractRenderState(
            StorageTankBlockEntity be,
            StorageTankRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(StorageTankDrainValveBlock.BLUEPRINT)
                && blockState.getValue(StorageTankDrainValveBlock.BLUEPRINT);
        state.isAssembled = (blockState.hasProperty(StorageTankDrainValveBlock.ASSEMBLED)
                && blockState.getValue(StorageTankDrainValveBlock.ASSEMBLED))
                || be.isAssembled();
        state.tankSize = be.getTankSize();
        state.targetBlueprintSize = be.getTargetBlueprintSize();
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && !state.isAssembled && be.getLevel() != null) {
            for (var req : be.getRequirements(state.targetBlueprintSize)) {
                BlockPos targetPos = be.getBlockPos().offset(req.x(), req.y(), req.z());
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.isAir()
                        ? targetState.isAir()
                        : (req.expectedBlock() != null && targetState.is(req.expectedBlock()));

                if (!matches) {
                    state.missingComponents.add(new StorageTankRenderState.MissingComponent(
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
            StorageTankRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        // 1. Blueprint Outline Hologram Rendering (when unformed / wrench mode)
        if (state.isBlueprintActive && !state.isAssembled && !state.missingComponents.isEmpty()) {
            for (var comp : state.missingComponents) {
                poseStack.pushPose();
                poseStack.translate(comp.relX(), comp.relY(), comp.relZ());

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
                double distSq = (comp.relX() * comp.relX()) + (comp.relY() * comp.relY()) + (comp.relZ() * comp.relZ());
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

        // 2. Monolithic 3D Enamel Storage Tank Model (when assembled)
        if (state.isAssembled) {
            poseStack.pushPose();

            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(StorageTankModel.TEXTURE),
                    (pose, consumer) -> this.tankModel.render(
                            pose,
                            consumer,
                            state.tankSize,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY
                    )
            );

            poseStack.popPose();
        }
    }
}
