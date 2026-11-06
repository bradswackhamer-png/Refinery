package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.BitumenPileBlock;
import com.refinery.block.entity.BitumenPileBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BitumenPileRenderer implements BlockEntityRenderer<BitumenPileBlockEntity, BitumenPileRenderState> {
    private final BitumenPileModel model;

    public BitumenPileRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new BitumenPileModel();
    }

    @Override
    public BitumenPileRenderState createRenderState() {
        return new BitumenPileRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BitumenPileBlockEntity blockEntity) {
        // Encompasses the 3 wide x 2 tall x 3 deep volume
        return new AABB(blockEntity.getBlockPos()).inflate(3.5, 2.5, 3.5);
    }

    @Override
    public void extractRenderState(BitumenPileBlockEntity be, BitumenPileRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.facing = blockState.hasProperty(BitumenPileBlock.FACING) ? blockState.getValue(BitumenPileBlock.FACING) : Direction.NORTH;
        state.visibleStage = be.getVisibleStage();
        state.totalCount = be.getTotalCount();
    }

    @Override
    public void submit(BitumenPileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();

        switch (state.facing) {
            case NORTH -> {
                // Default orientation: extends +X and +Z from master block
            }
            case SOUTH -> {
                // Extends -X and -Z from master block: offset by (1, 1) and rotate 180
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case WEST -> {
                // Extends +X (depth) and -Z (width) from master block
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
            case EAST -> {
                // Extends -X (depth) and +Z (width) from master block
                poseStack.translate(1.0, 0.0, 0.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
            default -> {}
        }

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(BitumenPileModel.TEXTURE),
                (pose, consumer) -> this.model.render(
                        pose,
                        consumer,
                        state.visibleStage,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
