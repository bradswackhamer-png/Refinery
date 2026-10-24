package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.MetalPressControllerBlock;
import com.refinery.block.entity.MetalPressControllerBlockEntity;
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
public class MetalPressControllerRenderer implements BlockEntityRenderer<MetalPressControllerBlockEntity, MetalPressControllerRenderState> {

    public MetalPressControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public MetalPressControllerRenderState createRenderState() {
        return new MetalPressControllerRenderState();
    }

    @Override
    public void extractRenderState(MetalPressControllerBlockEntity be, MetalPressControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(MetalPressControllerBlock.BLUEPRINT) && blockState.getValue(MetalPressControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.facing = blockState.getValue(MetalPressControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new MetalPressControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(MetalPressControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        // 2. Animated Hydraulic Stamping Ram (when assembled)
        if (state.isAssembled) {
            // Calculate dynamic vertical displacement of the stamping ram
            float ramY;
            float restingY = 0.82f;
            float lowestY = 0.12f;

            if (state.progress > 0) {
                if (state.progress <= 20) {
                    // Power stroke: rapid downward acceleration
                    float t = Math.min(1.0f, (state.progress + state.partialTicks) / 20.0f);
                    ramY = restingY - (t * t) * (restingY - lowestY);
                } else {
                    // Return stroke: smooth hydraulic retraction
                    float t = Math.min(1.0f, (state.progress - 20 + state.partialTicks) / 20.0f);
                    ramY = lowestY + t * (restingY - lowestY);
                }
            } else {
                ramY = restingY;
            }

            // Stamping ram column is at local (x: 1, y: 0, z: 0)
            int bedDx = right.getStepX();
            int bedDz = right.getStepZ();

            poseStack.pushPose();
            poseStack.translate(bedDx, 0, bedDz);

            // A. Hydraulic Cylinder Shaft (from top y=1.0 down to ram)
            float shaftMinX = 0.42f;
            float shaftMaxX = 0.58f;
            float shaftMinZ = 0.42f;
            float shaftMaxZ = 0.58f;
            float shaftMinY = ramY + 0.22f;
            float shaftMaxY = 1.0f;

            // B. Heavy Stamping Die Head (rectangular steel block)
            float headMinX = 0.22f;
            float headMaxX = 0.78f;
            float headMinZ = 0.22f;
            float headMaxZ = 0.78f;
            float headMinY = ramY;
            float headMaxY = ramY + 0.24f;

            // Render Solid 3D Geometry
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugFilledBox(), (pose, vc) -> {
                // Shaft (bright chrome steel)
                drawBox(pose, vc, shaftMinX, shaftMinY, shaftMinZ, shaftMaxX, shaftMaxY, shaftMaxZ, 0xD0, 0xD7, 0xDE, 0xFF);
                // Ram Head (hardened dark industrial steel)
                drawBox(pose, vc, headMinX, headMinY, headMinZ, headMaxX, headMaxY, headMaxZ, 0x47, 0x55, 0x69, 0xFF);
            });

            // Outline bevels on the Ram Die Head for crisp mechanical definition
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(headMinX, headMinY, headMinZ, headMaxX, headMaxY, headMaxZ),
                    RenderTypes.lines(),
                    0xFF94A3B8,
                    1.5f,
                    false
            );

            poseStack.popPose();
        }
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
        vc.addVertex(pose, x1, y2, z2).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x1, y2, z1).setColor(wr, wg, wb, a);

        // East face (+X)
        vc.addVertex(pose, x2, y1, z1).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y2, z1).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y2, z2).setColor(wr, wg, wb, a);
        vc.addVertex(pose, x2, y1, z2).setColor(wr, wg, wb, a);
    }
}
