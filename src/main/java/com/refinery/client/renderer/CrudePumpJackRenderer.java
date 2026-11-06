package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.CrudePumpJackBlock;
import com.refinery.block.entity.CrudePumpJackBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrudePumpJackRenderer implements BlockEntityRenderer<CrudePumpJackBlockEntity, CrudePumpJackRenderState> {
    private final CrudePumpJackModel model;

    public CrudePumpJackRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CrudePumpJackModel();
    }

    @Override
    public CrudePumpJackRenderState createRenderState() {
        return new CrudePumpJackRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(CrudePumpJackBlockEntity blockEntity) {
        // Encompasses the 2 wide x 3 tall x 3 deep volume
        return new AABB(blockEntity.getBlockPos()).inflate(3.5, 3.5, 3.5);
    }

    @Override
    public void extractRenderState(
            CrudePumpJackBlockEntity be,
            CrudePumpJackRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.facing = blockState.hasProperty(CrudePumpJackBlock.FACING)
                ? blockState.getValue(CrudePumpJackBlock.FACING)
                : Direction.NORTH;
        state.crankAngle = be.getCrankAngle();
        state.pumpingSpeed = be.getPumpingSpeed();
        state.crudeOil = be.getCrudeOil();
        state.energy = be.getEnergyStored();
    }

    @Override
    public void submit(
            CrudePumpJackRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();

        switch (state.facing) {
            case NORTH -> {
                poseStack.translate(1.0, 0.0, 0.0);
            }
            case SOUTH -> {
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case WEST -> {
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
            case EAST -> {
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
            default -> {}
        }

        this.model.render(
                poseStack,
                submitNodeCollector,
                state.crankAngle,
                state.pumpingSpeed,
                state.lightCoords
        );

        poseStack.popPose();
    }
}
