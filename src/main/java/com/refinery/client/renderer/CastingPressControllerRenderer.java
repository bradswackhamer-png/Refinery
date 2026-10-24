package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.CastingPressControllerBlock;
import com.refinery.block.entity.CastingPressControllerBlockEntity;
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
public class CastingPressControllerRenderer implements BlockEntityRenderer<CastingPressControllerBlockEntity, CastingPressControllerRenderState> {

    public CastingPressControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public CastingPressControllerRenderState createRenderState() {
        return new CastingPressControllerRenderState();
    }

    @Override
    public void extractRenderState(CastingPressControllerBlockEntity be, CastingPressControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(CastingPressControllerBlock.BLUEPRINT) && blockState.getValue(CastingPressControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.isActive = blockState.hasProperty(CastingPressControllerBlock.ACTIVE) && blockState.getValue(CastingPressControllerBlock.ACTIVE);
        state.facing = blockState.getValue(CastingPressControllerBlock.FACING);
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
                    state.missingComponents.add(new CastingPressControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(CastingPressControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        // 2. Animated Hydraulic Casting Press Ram & Mold Bed (Local X: 0, Y: 1, Z: 1)
        if (state.isAssembled) {
            int bedDx = back.getStepX();
            int bedDy = 1;
            int bedDz = back.getStepZ();

            // Calculate dynamic vertical stroke of stamping ram
            float ramY;
            float restingY = 0.85f;
            float lowestY = 0.08f;

            if (state.progress > 0) {
                if (state.progress <= 20) {
                    float t = Math.min(1.0f, (state.progress + state.partialTicks) / 20.0f);
                    ramY = restingY - (t * t) * (restingY - lowestY);
                } else {
                    float t = Math.min(1.0f, (state.progress - 20 + state.partialTicks) / 20.0f);
                    ramY = lowestY + t * (restingY - lowestY);
                }
            } else {
                ramY = restingY;
            }

            poseStack.pushPose();
            poseStack.translate(bedDx + 0.5, bedDy + 0.5, bedDz + 0.5);
            poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());

            // A. Hydraulic Cylinder Shaft (from top down to die)
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-0.1f, ramY + 0.15f - 0.5f, -0.1f, 0.1f, 0.5f, 0.1f),
                    RenderTypes.lines(),
                    0xFFD0D7DE,
                    1.8f,
                    false
            );

            // B. Heavy Water-Cooled Stamping Die Head
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-0.3f, ramY - 0.5f, -0.3f, 0.3f, ramY + 0.15f - 0.5f, 0.3f),
                    RenderTypes.lines(),
                    0xFF475569,
                    2.5f,
                    false
            );

            // C. Lower Mold Bed Molten Ingot Pool
            if (state.moltenAmount > 0 || state.progress > 0) {
                int poolColor = getFluidGlowColor(state);
                // When quenched at tick 20+, transitions to solidified metallic color
                if (state.progress >= 20) {
                    poolColor = 0xFF94A3B8;
                }

                submitNodeCollector.submitShapeOutline(
                        poseStack,
                        Shapes.box(-0.35f, -0.48f, -0.35f, 0.35f, -0.32f, 0.35f),
                        RenderTypes.lines(),
                        poolColor,
                        2.5f,
                        false
                );
            }

            poseStack.popPose();
        }
    }

    private static int getFluidGlowColor(CastingPressControllerRenderState state) {
        if (state.moltenFluid == RefineryFluids.MOLTEN_IRON_SOURCE.get()) return 0xFFEA580C;
        if (state.moltenFluid == RefineryFluids.MOLTEN_GOLD_SOURCE.get()) return 0xFFEAB308;
        if (state.moltenFluid == RefineryFluids.MOLTEN_COPPER_SOURCE.get()) return 0xFFD97706;
        if (state.moltenFluid == RefineryFluids.MOLTEN_TIN_SOURCE.get()) return 0xFFCBD5E1;
        if (state.moltenFluid == RefineryFluids.MOLTEN_BRONZE_SOURCE.get()) return 0xFFB45309;
        if (state.moltenFluid == RefineryFluids.MOLTEN_GLASS_SOURCE.get()) return 0xFFFDE047;
        return 0xFFF97316;
    }
}
