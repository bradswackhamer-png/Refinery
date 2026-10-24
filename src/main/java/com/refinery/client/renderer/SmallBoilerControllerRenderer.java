package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.SmallBoilerControllerBlock;
import com.refinery.block.entity.SmallBoilerControllerBlockEntity;
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
public class SmallBoilerControllerRenderer implements BlockEntityRenderer<SmallBoilerControllerBlockEntity, SmallBoilerControllerRenderState> {

    public SmallBoilerControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public SmallBoilerControllerRenderState createRenderState() {
        return new SmallBoilerControllerRenderState();
    }

    @Override
    public void extractRenderState(SmallBoilerControllerBlockEntity be, SmallBoilerControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(SmallBoilerControllerBlock.BLUEPRINT) && blockState.getValue(SmallBoilerControllerBlock.BLUEPRINT);
        state.facing = blockState.getValue(SmallBoilerControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = req.expectedBlock() != null && targetState.is(req.expectedBlock());

                if (!matches) {
                    state.missingComponents.add(new SmallBoilerControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(SmallBoilerControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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
}
