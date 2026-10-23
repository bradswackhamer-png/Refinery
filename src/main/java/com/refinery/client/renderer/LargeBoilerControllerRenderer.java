package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.LargeBoilerControllerBlock;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
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
public class LargeBoilerControllerRenderer implements BlockEntityRenderer<LargeBoilerControllerBlockEntity, LargeBoilerControllerRenderState> {

    public LargeBoilerControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public LargeBoilerControllerRenderState createRenderState() {
        return new LargeBoilerControllerRenderState();
    }

    @Override
    public void extractRenderState(LargeBoilerControllerBlockEntity be, LargeBoilerControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(LargeBoilerControllerBlock.BLUEPRINT) && blockState.getValue(LargeBoilerControllerBlock.BLUEPRINT);
        state.facing = blockState.getValue(LargeBoilerControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new LargeBoilerControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(LargeBoilerControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.isBlueprintActive || state.missingComponents.isEmpty()) {
            return;
        }

        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        for (var comp : state.missingComponents) {
            int dx = right.getStepX() * comp.relX() + back.getStepX() * comp.relZ();
            int dy = comp.relY();
            int dz = right.getStepZ() * comp.relX() + back.getStepZ() * comp.relZ();

            poseStack.pushPose();
            poseStack.translate(dx, dy, dz);

            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.block(),
                    RenderTypes.lines(),
                    comp.color(),
                    2.5f,
                    false
            );

            // Floating Name Tag (visible up to 10 blocks)
            double distSq = (dx * dx) + (dy * dy) + (dz * dz);
            if (distSq < 100.0) {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);

                submitNodeCollector.submitNameTag(
                        poseStack,
                        Vec3.ZERO,
                        0,
                        Component.literal(comp.name()),
                        false,
                        15728880,
                        camera
                );

                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
