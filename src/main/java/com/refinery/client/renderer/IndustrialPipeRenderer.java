package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.entity.IndustrialPipeBlockEntity;
import com.refinery.client.gui.StorageTankScreen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndustrialPipeRenderer implements BlockEntityRenderer<IndustrialPipeBlockEntity, IndustrialPipeRenderState> {
    private final IndustrialPipeModel model;
    private final ItemModelResolver itemModelResolver;

    public IndustrialPipeRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new IndustrialPipeModel();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public IndustrialPipeRenderState createRenderState() {
        return new IndustrialPipeRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(IndustrialPipeBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }

    @Override
    public void extractRenderState(
            IndustrialPipeBlockEntity be,
            IndustrialPipeRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        state.pos = be.getBlockPos();
        state.pipeType = be.getPipeType();
        state.material = be.getMaterial();
        state.mergeOutput = be.getMergeOutput();

        for (Direction dir : Direction.values()) {
            state.connections.put(dir, be.getConnection(dir));
        }

        state.storedFluid = be.getFluidTank().getStoredFluid();
        state.fluidFillRatio = (float) be.getFluidTank().getAmount() / (float) Math.max(1, be.getFluidTank().getCapacity());

        state.travelingItems.clear();
        for (IndustrialPipeBlockEntity.TravelingItem item : be.getTravelingItems()) {
            if (item.stack == null || item.stack.isEmpty()) continue;
            IndustrialPipeRenderState.RenderTravelingItem rItem = new IndustrialPipeRenderState.RenderTravelingItem();
            this.itemModelResolver.updateForTopItem(rItem.itemState, item.stack, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            rItem.fromDir = item.fromDir;
            rItem.toDir = item.toDir;
            rItem.progress = item.progress;
            state.travelingItems.add(rItem);
        }

        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(
            IndustrialPipeRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Identifier texture = IndustrialPipeModel.getTextureForMaterial(state.material);

        // 1. Submit seamless 3D monolithic cylindrical pipe geometry
        poseStack.pushPose();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(texture),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.connections,
                        state.material,
                        state.mergeOutput,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );
        poseStack.popPose();

        // 2. Render live fluid flow inside sight-glass core if fluid is present
        if (state.pipeType.handlesFluid() && state.storedFluid != Fluids.EMPTY && state.fluidFillRatio > 0.01f) {
            int flColor = StorageTankScreen.getFluidColor(state.storedFluid);
            poseStack.pushPose();
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entitySolid(IndustrialPipeModel.TEXTURE_STEEL),
                    (pose, consumer) -> this.model.renderFluidCore(
                            pose.pose(),
                            consumer,
                            state.connections,
                            flColor,
                            state.fluidFillRatio,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY
                    )
            );
            poseStack.popPose();
        }

        // 3. Render live traveling items through the pipe axis (visible through sight-glass windows)
        if (!state.travelingItems.isEmpty()) {
            for (IndustrialPipeRenderState.RenderTravelingItem item : state.travelingItems) {
                poseStack.pushPose();

                // Compute 3D position along the pipe center axis based on progress
                double px = 0.5;
                double py = 0.5;
                double pz = 0.5;

                float prog = item.progress;
                if (prog < 0.5f) {
                    // Moving from fromDir towards center (0.5)
                    float t = prog * 2.0f; // 0.0 to 1.0
                    Direction from = item.fromDir;
                    px = (0.5 + from.getStepX() * 0.5) * (1.0 - t) + 0.5 * t;
                    py = (0.5 + from.getStepY() * 0.5) * (1.0 - t) + 0.5 * t;
                    pz = (0.5 + from.getStepZ() * 0.5) * (1.0 - t) + 0.5 * t;
                } else {
                    // Moving from center (0.5) towards toDir
                    float t = (prog - 0.5f) * 2.0f; // 0.0 to 1.0
                    Direction to = item.toDir;
                    px = 0.5 * (1.0 - t) + (0.5 + to.getStepX() * 0.5) * t;
                    py = 0.5 * (1.0 - t) + (0.5 + to.getStepY() * 0.5) * t;
                    pz = 0.5 * (1.0 - t) + (0.5 + to.getStepZ() * 0.5) * t;
                }

                poseStack.translate(px, py, pz);
                poseStack.scale(0.35f, 0.35f, 0.35f);
                poseStack.rotateDegrees(Axis.YP, (state.gameTime + state.partialTicks) * 4.0f);

                item.itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                poseStack.popPose();
            }
        }

        // 4. Submit translucent sight-glass inspection strips along pipe lengths
        poseStack.pushPose();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityTranslucent(IndustrialPipeModel.TEXTURE_WINDOW),
                (pose, consumer) -> this.model.renderSightGlassWindows(
                        pose.pose(),
                        consumer,
                        state.connections,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );
        poseStack.popPose();
    }
}
