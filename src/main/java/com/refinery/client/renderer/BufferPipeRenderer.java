package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.entity.BufferPipeBlockEntity;
import com.refinery.block.pipe.IndustrialPipeMaterial;
import com.refinery.block.pipe.PipeConnectionType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BufferPipeRenderer implements BlockEntityRenderer<BufferPipeBlockEntity, BufferPipeRenderState> {
    private final IndustrialPipeModel model;
    private final ItemModelResolver itemModelResolver;

    public BufferPipeRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new IndustrialPipeModel();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public BufferPipeRenderState createRenderState() {
        return new BufferPipeRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return false;
    }

    @Override
    public AABB getRenderBoundingBox(BufferPipeBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }

    @Override
    public void extractRenderState(
            BufferPipeBlockEntity be,
            BufferPipeRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        state.pos = be.getBlockPos();
        for (Direction dir : Direction.values()) {
            state.connections.put(dir, be.getConnection(dir));
        }

        ItemStack stack = be.getItem(0);
        state.hasItem = !stack.isEmpty();
        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemState, stack, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        } else {
            state.itemState.clear();
        }

        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(
            BufferPipeRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        Identifier texture = IndustrialPipeModel.TEXTURE_STEEL;

        // 1. Submit seamless 3D cylindrical pipe geometry
        poseStack.pushPose();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(texture),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.connections,
                        IndustrialPipeMaterial.STEEL,
                        null,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );
        poseStack.popPose();

        // 2. Render buffered item floating inside the pipe core
        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.38f, 0.38f, 0.38f);
            poseStack.rotateDegrees(Axis.YP, (state.gameTime + state.partialTicks) * 2.0f);

            state.itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }

        // 3. Submit translucent sight-glass inspection windows
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
