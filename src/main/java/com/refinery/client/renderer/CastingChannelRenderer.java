package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinery.block.CastingChannelBlock;
import com.refinery.block.entity.CastingChannelBlockEntity;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CastingChannelRenderer implements BlockEntityRenderer<CastingChannelBlockEntity, CastingChannelRenderState> {

    public CastingChannelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public CastingChannelRenderState createRenderState() {
        return new CastingChannelRenderState();
    }

    @Override
    public void extractRenderState(CastingChannelBlockEntity be, CastingChannelRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);
        state.amount = be.getMoltenAmount();
        state.fluid = be.getMoltenFluid();

        var blockState = be.getBlockState();
        if (blockState.getBlock() instanceof CastingChannelBlock) {
            state.north = blockState.getValue(CastingChannelBlock.NORTH);
            state.south = blockState.getValue(CastingChannelBlock.SOUTH);
            state.east = blockState.getValue(CastingChannelBlock.EAST);
            state.west = blockState.getValue(CastingChannelBlock.WEST);
            state.down = blockState.getValue(CastingChannelBlock.DOWN);
        }
    }

    @Override
    public void submit(CastingChannelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.amount <= 0) return;

        float fillRatio = Math.min(1.0f, (float) state.amount / (float) CastingChannelBlockEntity.CAPACITY);
        float fluidY = 0.28f + (fillRatio * 0.18f); // 4.5/16 to 7.4/16

        int glowColor = getFluidGlowColor(state);

        // Center pool
        float minX = 0.28f;
        float maxX = 0.72f;
        float minZ = 0.28f;
        float maxZ = 0.72f;

        // Extend arms into connected directions
        if (state.north) minZ = 0.0f;
        if (state.south) maxZ = 1.0f;
        if (state.west) minX = 0.0f;
        if (state.east) maxX = 1.0f;

        submitNodeCollector.submitShapeOutline(
                poseStack,
                Shapes.box(minX, 0.26f, minZ, maxX, fluidY, maxZ),
                RenderTypes.lines(),
                glowColor,
                2.5f,
                false
        );

        // Downward vertical pour stream if connected down
        if (state.down) {
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(0.35f, 0.0f, 0.35f, 0.65f, 0.26f, 0.65f),
                    RenderTypes.lines(),
                    glowColor,
                    2.0f,
                    false
            );
        }
    }

    private static int getFluidGlowColor(CastingChannelRenderState state) {
        if (state.fluid == RefineryFluids.MOLTEN_IRON_SOURCE.get()) return 0xFFEA580C;
        if (state.fluid == RefineryFluids.MOLTEN_GOLD_SOURCE.get()) return 0xFFEAB308;
        if (state.fluid == RefineryFluids.MOLTEN_COPPER_SOURCE.get()) return 0xFFD97706;
        if (state.fluid == RefineryFluids.MOLTEN_TIN_SOURCE.get()) return 0xFFCBD5E1;
        if (state.fluid == RefineryFluids.MOLTEN_BRONZE_SOURCE.get()) return 0xFFB45309;
        if (state.fluid == RefineryFluids.MOLTEN_GLASS_SOURCE.get()) return 0xFFFDE047;
        return 0xFFF97316;
    }
}
