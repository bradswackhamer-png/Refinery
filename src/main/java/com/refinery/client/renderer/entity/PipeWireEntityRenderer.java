package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.RefineryMod;
import com.refinery.entity.PipeWireEntity;
import com.refinery.gate.PipeWireColor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeWireEntityRenderer extends EntityRenderer<PipeWireEntity, PipeWireRenderState> {
    private static final int FULL_BRIGHT = 0x00F000F0;
    private final ItemModelResolver itemModelResolver;

    public PipeWireEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public PipeWireRenderState createRenderState() {
        return new PipeWireRenderState();
    }

    @Override
    public void extractRenderState(PipeWireEntity entity, PipeWireRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.hasRed = entity.hasWire(PipeWireColor.RED);
        state.hasBlue = entity.hasWire(PipeWireColor.BLUE);
        state.hasGreen = entity.hasWire(PipeWireColor.GREEN);
        state.hasYellow = entity.hasWire(PipeWireColor.YELLOW);

        state.signalRed = entity.isSignalActive(PipeWireColor.RED);
        state.signalBlue = entity.isSignalActive(PipeWireColor.BLUE);
        state.signalGreen = entity.isSignalActive(PipeWireColor.GREEN);
        state.signalYellow = entity.isSignalActive(PipeWireColor.YELLOW);

        if (state.hasRed) {
            this.itemModelResolver.updateForNonLiving(state.redItem, new ItemStack(RefineryMod.PIPE_WIRE_RED.get()), ItemDisplayContext.FIXED, entity);
        }
        if (state.hasBlue) {
            this.itemModelResolver.updateForNonLiving(state.blueItem, new ItemStack(RefineryMod.PIPE_WIRE_BLUE.get()), ItemDisplayContext.FIXED, entity);
        }
        if (state.hasGreen) {
            this.itemModelResolver.updateForNonLiving(state.greenItem, new ItemStack(RefineryMod.PIPE_WIRE_GREEN.get()), ItemDisplayContext.FIXED, entity);
        }
        if (state.hasYellow) {
            this.itemModelResolver.updateForNonLiving(state.yellowItem, new ItemStack(RefineryMod.PIPE_WIRE_YELLOW.get()), ItemDisplayContext.FIXED, entity);
        }
    }

    @Override
    public void submit(PipeWireRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        // Red Wire (Top Left)
        if (state.hasRed) {
            poseStack.pushPose();
            poseStack.translate(-0.16f, 0.16f, 0.0f);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            int light = state.signalRed ? FULL_BRIGHT : state.lightCoords;
            state.redItem.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        // Blue Wire (Top Right)
        if (state.hasBlue) {
            poseStack.pushPose();
            poseStack.translate(0.16f, 0.16f, 0.0f);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            int light = state.signalBlue ? FULL_BRIGHT : state.lightCoords;
            state.blueItem.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        // Green Wire (Bottom Left)
        if (state.hasGreen) {
            poseStack.pushPose();
            poseStack.translate(-0.16f, -0.16f, 0.0f);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            int light = state.signalGreen ? FULL_BRIGHT : state.lightCoords;
            state.greenItem.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        // Yellow Wire (Bottom Right)
        if (state.hasYellow) {
            poseStack.pushPose();
            poseStack.translate(0.16f, -0.16f, 0.0f);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            int light = state.signalYellow ? FULL_BRIGHT : state.lightCoords;
            state.yellowItem.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
