package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.IndustrialGrinderControllerBlock;
import com.refinery.block.entity.IndustrialGrinderControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndustrialGrinderControllerRenderer implements BlockEntityRenderer<IndustrialGrinderControllerBlockEntity, IndustrialGrinderControllerRenderState> {

    public IndustrialGrinderControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public IndustrialGrinderControllerRenderState createRenderState() {
        return new IndustrialGrinderControllerRenderState();
    }

    @Override
    public void extractRenderState(IndustrialGrinderControllerBlockEntity be, IndustrialGrinderControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

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

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

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

        // 1. Blueprint Outline Rendering (when unformed / wrench mode)
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

        // 2. Animated Counter-Rotating Grinding Rollers (inside crushing chamber at local 1, 1, 0)
        if (state.isAssembled) {
            int chamberDx = right.getStepX();
            int chamberDz = right.getStepZ();

            poseStack.pushPose();
            // Move to center of crushing chamber block (local 1, 1, 0)
            poseStack.translate(chamberDx + 0.5, 1.5, chamberDz + 0.5);
            // Rotate to match multiblock facing orientation
            poseStack.rotateDegrees(com.mojang.math.Axis.YP, -state.facing.toYRot());

            // Determine rotation angle
            float angle;
            if (state.isActive) {
                angle = (state.gameTime + state.partialTicks) * 22.0f;
            } else {
                angle = (state.gameTime + state.partialTicks) * 1.5f; // Slow idle hum
            }

            // Left Roller (at local X = -0.22, spinning clockwise inwards)
            renderRoller(poseStack, submitNodeCollector, -0.22f, angle, 0.0f);

            // Right Roller (at local X = +0.22, spinning counter-clockwise inwards, offset by 45 deg to intermesh)
            renderRoller(poseStack, submitNodeCollector, 0.22f, -angle, 45.0f);

            poseStack.popPose();
        }
    }

    private void renderRoller(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, float localX, float baseAngle, float phaseOffset) {
        poseStack.pushPose();
        poseStack.translate(localX, 0.0, 0.0);
        poseStack.rotateDegrees(com.mojang.math.Axis.ZP, baseAngle + phaseOffset);

        // 1. Central Axle Core (dark hardened carbon steel)
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugFilledBox(), (pose, vc) -> {
            drawBox(pose, vc, -0.09f, -0.09f, -0.38f, 0.09f, 0.09f, 0.38f, 0x33, 0x41, 0x55, 0xFF);
        });

        // 2. 4 sets of crushing teeth / fluted blades spaced evenly at 90 degrees
        for (int i = 0; i < 4; i++) {
            poseStack.pushPose();
            poseStack.rotateDegrees(com.mojang.math.Axis.ZP, i * 90.0f);

            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugFilledBox(), (pose, vc) -> {
                // Fluted blade tooth
                drawBox(pose, vc, -0.035f, 0.07f, -0.36f, 0.035f, 0.19f, 0.36f, 0x64, 0x74, 0x8B, 0xFF);
            });

            // Crisp mechanical wireframe outline on blade edges
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-0.035f, 0.07f, -0.36f, 0.035f, 0.19f, 0.36f),
                    RenderTypes.lines(),
                    0xFFCBD5E1,
                    1.2f,
                    false
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void drawBox(PoseStack.Pose pose, com.mojang.blaze3d.vertex.VertexConsumer vc,
                                float x1, float y1, float z1, float x2, float y2, float z2,
                                int r, int g, int b, int a) {
        // Top face (+Y)
        int tr = Math.min(255, (int)(r * 1.15f)), tg = Math.min(255, (int)(g * 1.15f)), tb = Math.min(255, (int)(b * 1.15f));
        vc.addVertex(pose, x1, y2, z1).setColor(tr, tg, tb, a);
        vc.addVertex(pose, x1, y2, z2).setColor(tr, tg, tb, a);
        vc.addVertex(pose, x2, y2, z2).setColor(tr, tg, tb, a);
        vc.addVertex(pose, x2, y2, z1).setColor(tr, tg, tb, a);

        // Bottom face (-Y)
        int br = (int)(r * 0.6f), bg = (int)(g * 0.6f), bb = (int)(b * 0.6f);
        vc.addVertex(pose, x1, y1, z1).setColor(br, bg, bb, a);
        vc.addVertex(pose, x2, y1, z1).setColor(br, bg, bb, a);
        vc.addVertex(pose, x2, y1, z2).setColor(br, bg, bb, a);
        vc.addVertex(pose, x1, y1, z2).setColor(br, bg, bb, a);

        // North face (-Z)
        int nr = (int)(r * 0.8f), ng = (int)(g * 0.8f), nb = (int)(b * 0.8f);
        vc.addVertex(pose, x1, y1, z1).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x1, y2, z1).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x2, y2, z1).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x2, y1, z1).setColor(nr, ng, nb, a);

        // South face (+Z)
        vc.addVertex(pose, x1, y1, z2).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x2, y1, z2).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x2, y2, z2).setColor(nr, ng, nb, a);
        vc.addVertex(pose, x1, y2, z2).setColor(nr, ng, nb, a);

        // West face (-X)
        int wr = (int)(r * 0.9f), wg = (int)(g * 0.9f), wb = (int)(b * 0.9f);
        vc.addVertex(pose, x1, y1, z1).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x1, y1, z2).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y1, z2).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y1, z1).setColor(wr, wg, wb, a);

        // East face (+X)
        vc.addVertex(pose, x2, y1, z1).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y2, z1).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y2, z2).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y1, z2).setColor(wr, wg, wb, a);
    }
}
