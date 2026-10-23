package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.RefineryControllerBlock;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RefineryControllerRenderer implements BlockEntityRenderer<RefineryControllerBlockEntity, RefineryControllerRenderState> {

    public RefineryControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public RefineryControllerRenderState createRenderState() {
        return new RefineryControllerRenderState();
    }

    @Override
    public void extractRenderState(RefineryControllerBlockEntity be, RefineryControllerRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(RefineryControllerBlock.BLUEPRINT) && blockState.getValue(RefineryControllerBlock.BLUEPRINT);
        state.facing = blockState.getValue(RefineryControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                if (!targetState.is(req.expectedBlock())) {
                    state.missingComponents.add(new RefineryControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(RefineryControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

            // 1. Render 3D Wireframe Glowing Box
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.block(),
                    RenderTypes.lines(),
                    comp.color(),
                    3.0f,
                    false
            );

            // 2. Render 3D Floating Name Tag BillBoard
            submitNodeCollector.submitNameTag(
                    poseStack,
                    new Vec3(0.5, 0.5, 0.5),
                    0,
                    Component.literal(comp.name()),
                    true,
                    0xF000F0,
                    camera
            );

            poseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(RefineryControllerBlockEntity be) {
        return new AABB(be.getBlockPos()).inflate(4.0);
    }
}
