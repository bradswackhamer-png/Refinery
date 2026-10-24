package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.ArcMelterControllerBlock;
import com.refinery.block.entity.ArcMelterControllerBlockEntity;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
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
public class ArcMelterControllerRenderer implements BlockEntityRenderer<ArcMelterControllerBlockEntity, ArcMelterControllerRenderState> {

    public ArcMelterControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public ArcMelterControllerRenderState createRenderState() {
        return new ArcMelterControllerRenderState();
    }

    @Override
    public void extractRenderState(ArcMelterControllerBlockEntity be, ArcMelterControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(ArcMelterControllerBlock.BLUEPRINT) && blockState.getValue(ArcMelterControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.isActive = blockState.hasProperty(ArcMelterControllerBlock.ACTIVE) && blockState.getValue(ArcMelterControllerBlock.ACTIVE);
        state.facing = blockState.getValue(ArcMelterControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.progress = be.getProgress();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.moltenAmount = be.getMoltenAmount();
        state.moltenFluid = be.getMoltenFluid();
        state.missingComponents.clear();

        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);
                boolean matches = (req.x() == 0 && req.y() == 1 && req.z() == 1 && targetState.isAir())
                        || (req.expectedBlock() != null && targetState.is(req.expectedBlock()));

                if (!matches) {
                    state.missingComponents.add(new ArcMelterControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(ArcMelterControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction right = state.facing.getClockWise();
        Direction back = state.facing.getOpposite();

        // 1. Hologram Blueprint Rendering
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

        // 2. Animated Arc Melter Interior (Electrodes & Molten Bath at local X: 0, Y: 1, Z: 1)
        if (state.isAssembled) {
            int chamberDx = back.getStepX();
            int chamberDy = 1;
            int chamberDz = back.getStepZ();

            poseStack.pushPose();
            poseStack.translate(chamberDx + 0.5, chamberDy + 0.5, chamberDz + 0.5);
            poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());

            // A. Dual Graphite Carbon Electrodes descending from roof
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-0.25f, 0.12f, -0.1f, -0.05f, 0.5f, 0.1f),
                    RenderTypes.lines(),
                    0xFF1E293B,
                    2.0f,
                    false
            );

            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(0.05f, 0.12f, -0.1f, 0.25f, 0.5f, 0.1f),
                    RenderTypes.lines(),
                    0xFF1E293B,
                    2.0f,
                    false
            );

            // B. Molten Metal / Glass Bath Pool
            if (state.moltenAmount > 0 || state.isActive) {
                float fillRatio = Math.min(1.0f, (float) state.moltenAmount / 8000.0f);
                float bathHeight = -0.48f + (fillRatio * 0.45f);

                int color = getFluidGlowColor(state);
                submitNodeCollector.submitShapeOutline(
                        poseStack,
                        Shapes.box(-0.45f, -0.49f, -0.45f, 0.45f, bathHeight, 0.45f),
                        RenderTypes.lines(),
                        color,
                        2.5f,
                        false
                );
            }

            // C. Electric Arc Flashes leaping between electrodes
            if (state.isActive) {
                float flickerX = (float) Math.sin((state.gameTime + state.partialTicks) * 6.0f) * 0.08f;
                float flickerZ = (float) Math.cos((state.gameTime + state.partialTicks) * 7.5f) * 0.08f;

                submitNodeCollector.submitShapeOutline(
                        poseStack,
                        Shapes.box(-0.15f + flickerX, 0.05f, -0.05f + flickerZ, 0.15f + flickerX, 0.14f, 0.05f + flickerZ),
                        RenderTypes.lines(),
                        0xFF67E8F9, // Brilliant electric cyan-white
                        3.0f,
                        false
                );
            }

            poseStack.popPose();
        }
    }

    private static int getFluidGlowColor(ArcMelterControllerRenderState state) {
        if (state.moltenFluid == RefineryFluids.MOLTEN_IRON_SOURCE.get()) return 0xFFEA580C;
        if (state.moltenFluid == RefineryFluids.MOLTEN_GOLD_SOURCE.get()) return 0xFFEAB308;
        if (state.moltenFluid == RefineryFluids.MOLTEN_COPPER_SOURCE.get()) return 0xFFD97706;
        if (state.moltenFluid == RefineryFluids.MOLTEN_TIN_SOURCE.get()) return 0xFFCBD5E1;
        if (state.moltenFluid == RefineryFluids.MOLTEN_BRONZE_SOURCE.get()) return 0xFFB45309;
        if (state.moltenFluid == RefineryFluids.MOLTEN_GLASS_SOURCE.get()) return 0xFFFDE047;
        return 0xFFF97316;
    }
}
