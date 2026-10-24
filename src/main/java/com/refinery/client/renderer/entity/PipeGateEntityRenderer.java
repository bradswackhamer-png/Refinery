package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.entity.PipeGateEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeGateEntityRenderer extends EntityRenderer<PipeGateEntity, PipeGateRenderState> {
    private final ItemModelResolver itemModelResolver;

    public PipeGateEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public PipeGateRenderState createRenderState() {
        return new PipeGateRenderState();
    }

    @Override
    public void extractRenderState(PipeGateEntity entity, PipeGateRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.direction = entity.getAttachedFace();
        state.tier = entity.getTier();
        state.autarchic = entity.isAutarchic();
        state.active = entity.isActive();
        state.powered = entity.isPowered();

        ItemStack gateStack = entity.getGateItemStack();
        this.itemModelResolver.updateForNonLiving(state.item, gateStack, ItemDisplayContext.FIXED, entity);
    }

    @Override
    public void submit(PipeGateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        Direction dir = state.direction;
        // Orient the gate plate to face outwards from the pipe
        switch (dir) {
            case NORTH -> poseStack.rotateDegrees(Axis.YP, 180f);
            case SOUTH -> {}
            case WEST -> poseStack.rotateDegrees(Axis.YP, 90f);
            case EAST -> poseStack.rotateDegrees(Axis.YP, 270f);
            case UP -> poseStack.rotateDegrees(Axis.XP, 90f);
            case DOWN -> poseStack.rotateDegrees(Axis.XP, 270f);
        }

        // Slight scale to fit neatly on standard 8x8 pipe collars
        poseStack.scale(0.65f, 0.65f, 0.65f);

        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}
