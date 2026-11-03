package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class CastingChannelRenderer implements BlockEntityRenderer<CastingChannelBlockEntity, CastingChannelRenderState> {

    private static final Identifier TEX_IRON = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_iron_still.png");
    private static final Identifier TEX_GOLD = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_gold_still.png");
    private static final Identifier TEX_COPPER = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_copper_still.png");
    private static final Identifier TEX_TIN = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_tin_still.png");
    private static final Identifier TEX_BRONZE = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_bronze_still.png");
    private static final Identifier TEX_GLASS = Identifier.fromNamespaceAndPath("refinery", "textures/block/molten_glass_still.png");

    public CastingChannelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public CastingChannelRenderState createRenderState() {
        return new CastingChannelRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(CastingChannelBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1.5);
    }

    @Override
    public void extractRenderState(CastingChannelBlockEntity be, CastingChannelRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);
        state.amount = be.getMoltenAmount();
        state.fluid = be.getMoltenFluid();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;

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
        if (state.amount <= 0 || state.fluid == Fluids.EMPTY) return;

        float fillRatio = Math.max(0.18f, Math.min(1.0f, (float) state.amount / (float) CastingChannelBlockEntity.CAPACITY));
        float yBottom = 5.0f / 16.0f; // 0.3125f floor
        float yTop = yBottom + 0.02f + fillRatio * (2.2f / 16.0f); // 0.33f to 0.47f (recessed inside 0.50f rim)

        float cx0 = 5.0f / 16.0f;
        float cx1 = 11.0f / 16.0f;
        float cz0 = 5.0f / 16.0f;
        float cz1 = 11.0f / 16.0f;

        float x0 = state.west ? 0.0f : cx0;
        float x1 = state.east ? 1.0f : cx1;
        float z0 = state.north ? 0.0f : cz0;
        float z1 = state.south ? 1.0f : cz1;

        Identifier texture = getFluidTexture(state.fluid);
        float scroll = (state.gameTime + state.partialTicks) * 0.0018f;

        // 1. Submit Solid Glowing Molten Metal Fluid Geometry
        poseStack.pushPose();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityTranslucent(texture),
                (pose, consumer) -> {
                    Matrix4f m = pose.pose();

                    // Top Surface (liquid face)
                    emitQuad(m, consumer,
                            x0, yTop, z0,
                            x0, yTop, z1,
                            x1, yTop, z1,
                            x1, yTop, z0,
                            0.0f, 1.0f, 0.0f,
                            0.0f, 1.0f,
                            scroll, scroll + (z1 - z0) + (x1 - x0));

                    // Underside (liquid base)
                    emitQuad(m, consumer,
                            x0, yBottom, z1,
                            x0, yBottom, z0,
                            x1, yBottom, z0,
                            x1, yBottom, z1,
                            0.0f, -1.0f, 0.0f,
                            0.0f, 1.0f,
                            scroll, scroll + (z1 - z0) + (x1 - x0));

                    // North wall liquid face (if not connected north)
                    if (!state.north) {
                        emitQuad(m, consumer,
                                x1, yTop, cz0,
                                x1, yBottom, cz0,
                                x0, yBottom, cz0,
                                x0, yTop, cz0,
                                0.0f, 0.0f, -1.0f,
                                0.0f, 1.0f, 0.0f, 1.0f);
                    }

                    // South wall liquid face (if not connected south)
                    if (!state.south) {
                        emitQuad(m, consumer,
                                x0, yTop, cz1,
                                x0, yBottom, cz1,
                                x1, yBottom, cz1,
                                x1, yTop, cz1,
                                0.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, 0.0f, 1.0f);
                    }

                    // West wall liquid face (if not connected west)
                    if (!state.west) {
                        emitQuad(m, consumer,
                                cx0, yTop, z0,
                                cx0, yBottom, z0,
                                cx0, yBottom, z1,
                                cx0, yTop, z1,
                                -1.0f, 0.0f, 0.0f,
                                0.0f, 1.0f, 0.0f, 1.0f);
                    }

                    // East wall liquid face (if not connected east)
                    if (!state.east) {
                        emitQuad(m, consumer,
                                cx1, yTop, z1,
                                cx1, yBottom, z1,
                                cx1, yBottom, z0,
                                cx1, yTop, z0,
                                1.0f, 0.0f, 0.0f,
                                0.0f, 1.0f, 0.0f, 1.0f);
                    }

                    // Downward Pouring Stream (if connected down)
                    if (state.down) {
                        float dx0 = 0.375f;
                        float dx1 = 0.625f;
                        float dz0 = 0.375f;
                        float dz1 = 0.625f;
                        float vDown = (state.gameTime + state.partialTicks) * 0.08f;

                        // North face of stream
                        emitQuad(m, consumer,
                                dx1, yTop, dz0,
                                dx1, 0.0f, dz0,
                                dx0, 0.0f, dz0,
                                dx0, yTop, dz0,
                                0.0f, 0.0f, -1.0f,
                                0.0f, 1.0f, vDown, vDown + 1.0f);

                        // South face of stream
                        emitQuad(m, consumer,
                                dx0, yTop, dz1,
                                dx0, 0.0f, dz1,
                                dx1, 0.0f, dz1,
                                dx1, yTop, dz1,
                                0.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, vDown, vDown + 1.0f);

                        // West face of stream
                        emitQuad(m, consumer,
                                dx0, yTop, dz0,
                                dx0, 0.0f, dz0,
                                dx0, 0.0f, dz1,
                                dx0, yTop, dz1,
                                -1.0f, 0.0f, 0.0f,
                                0.0f, 1.0f, vDown, vDown + 1.0f);

                        // East face of stream
                        emitQuad(m, consumer,
                                dx1, yTop, dz1,
                                dx1, 0.0f, dz1,
                                dx1, 0.0f, dz0,
                                dx1, yTop, dz0,
                                1.0f, 0.0f, 0.0f,
                                0.0f, 1.0f, vDown, vDown + 1.0f);
                    }
                }
        );
        poseStack.popPose();

        // 2. Emissive Surface Outline Highlight
        int glowColor = getFluidGlowColor(state);
        submitNodeCollector.submitShapeOutline(
                poseStack,
                Shapes.box(x0, yBottom, z0, x1, yTop, z1),
                RenderTypes.lines(),
                glowColor,
                2.0f,
                false
        );

        if (state.down) {
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(0.375f, 0.0f, 0.375f, 0.625f, yBottom, 0.625f),
                    RenderTypes.lines(),
                    glowColor,
                    2.0f,
                    false
            );
        }
    }

    private static void emitQuad(
            Matrix4f m, VertexConsumer c,
            float xA, float yA, float zA,
            float xB, float yB, float zB,
            float xC, float yC, float zC,
            float xD, float yD, float zD,
            float nx, float ny, float nz,
            float u0, float u1, float v0, float v1
    ) {
        c.addVertex(m, xA, yA, zA).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(nx, ny, nz);
        c.addVertex(m, xB, yB, zB).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(nx, ny, nz);
        c.addVertex(m, xC, yC, zC).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(nx, ny, nz);
        c.addVertex(m, xD, yD, zD).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(nx, ny, nz);
    }

    private static Identifier getFluidTexture(Fluid fluid) {
        if (fluid == RefineryFluids.MOLTEN_IRON_SOURCE.get()) return TEX_IRON;
        if (fluid == RefineryFluids.MOLTEN_GOLD_SOURCE.get()) return TEX_GOLD;
        if (fluid == RefineryFluids.MOLTEN_COPPER_SOURCE.get()) return TEX_COPPER;
        if (fluid == RefineryFluids.MOLTEN_TIN_SOURCE.get()) return TEX_TIN;
        if (fluid == RefineryFluids.MOLTEN_BRONZE_SOURCE.get()) return TEX_BRONZE;
        if (fluid == RefineryFluids.MOLTEN_GLASS_SOURCE.get()) return TEX_GLASS;
        return TEX_IRON;
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
