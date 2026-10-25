package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.block.MultiSmelterControllerBlock;
import com.refinery.block.entity.MultiSmelterControllerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class MultiSmelterControllerRenderer implements BlockEntityRenderer<MultiSmelterControllerBlockEntity, MultiSmelterControllerRenderState> {
    private final ItemModelResolver itemModelResolver;

    public MultiSmelterControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public MultiSmelterControllerRenderState createRenderState() {
        return new MultiSmelterControllerRenderState();
    }

    @Override
    public void extractRenderState(MultiSmelterControllerBlockEntity be, MultiSmelterControllerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);

        var blockState = be.getBlockState();
        state.isBlueprintActive = blockState.hasProperty(MultiSmelterControllerBlock.BLUEPRINT) && blockState.getValue(MultiSmelterControllerBlock.BLUEPRINT);
        state.isAssembled = be.isAssembled();
        state.isActive = blockState.hasProperty(MultiSmelterControllerBlock.ACTIVE) && blockState.getValue(MultiSmelterControllerBlock.ACTIVE);
        state.facing = blockState.getValue(MultiSmelterControllerBlock.FACING);
        state.controllerPos = be.getBlockPos();
        state.burnTime = be.getBurnTime();
        state.energy = be.getEnergy();
        state.gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        state.partialTicks = partialTicks;
        state.missingComponents.clear();

        Arrays.fill(state.hasItem, false);

        if (state.isAssembled && be.getLevel() != null) {
            for (int i = 0; i < 8; i++) {
                ItemStack item = be.getItem(i);
                state.cookProgress[i] = be.getCookProgress(i);
                if (!item.isEmpty()) {
                    this.itemModelResolver.updateForTopItem(state.itemStates[i], item, ItemDisplayContext.FIXED, be.getLevel(), null, i);
                    state.hasItem[i] = true;
                }
            }
        }

        // Blueprint hologram diagnostics
        if (state.isBlueprintActive && be.getLevel() != null) {
            for (var req : be.getRequirements()) {
                BlockPos targetPos = be.getRelativePos(req.x(), req.y(), req.z(), state.facing);
                var targetState = be.getLevel().getBlockState(targetPos);

                boolean matches;
                if (req.expectedBlock() == null) {
                    matches = targetState.isAir();
                } else if (req.x() == 0 && req.y() == 0 && req.z() == 0) {
                    matches = targetState.is(req.expectedBlock());
                } else {
                    matches = MultiSmelterControllerBlockEntity.isSmelterCasingOrPort(targetState);
                }

                if (!matches) {
                    state.missingComponents.add(new MultiSmelterControllerRenderState.MissingComponent(
                            req.x(), req.y(), req.z(),
                            req.outlineColor(),
                            req.name()
                    ));
                }
            }
        }
    }

    @Override
    public void submit(MultiSmelterControllerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

        // 2. 3D Interior Animation in the Hollow Chamber
        if (state.isAssembled) {
            int chamberDx = back.getStepX() * 1;
            int chamberDy = 1;
            int chamberDz = back.getStepZ() * 1;

            poseStack.pushPose();
            poseStack.translate(chamberDx + 0.5, chamberDy, chamberDz + 0.5);
            poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());

            // A. Ceramic Refractory Rack Support Bars (connecting the 3-wide chamber)
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-1.4f, 0.05f, 0.35f, 1.4f, 0.12f, 0.42f),
                    RenderTypes.lines(),
                    0xFF78716C,
                    2.0f,
                    false
            );
            submitNodeCollector.submitShapeOutline(
                    poseStack,
                    Shapes.box(-1.4f, 1.0f, 0.35f, 1.4f, 1.07f, 0.42f),
                    RenderTypes.lines(),
                    0xFF78716C,
                    2.0f,
                    false
            );

            // B. Heating Elements on Floor
            if (state.burnTime > 0) {
                // Burning Solid Fuel: Fiery glowing coal bed / fire grate
                float pulse = (float) Math.sin((state.gameTime + state.partialTicks) * 0.2f) * 0.15f + 0.85f;
                int fireColor = 0xFF000000 | ((int) (255 * pulse) << 16) | ((int) (90 * pulse) << 8) | 0x10;

                submitNodeCollector.submitShapeOutline(
                        poseStack,
                        Shapes.box(-1.35f, 0.02f, -0.38f, 1.35f, 0.08f, 0.38f),
                        RenderTypes.lines(),
                        fireColor,
                        3.0f,
                        false
                );
                // Ember ribs
                for (float rx = -1.1f; rx <= 1.1f; rx += 0.44f) {
                    submitNodeCollector.submitShapeOutline(
                            poseStack,
                            Shapes.box(rx - 0.08f, 0.08f, -0.35f, rx + 0.08f, 0.14f, 0.35f),
                            RenderTypes.lines(),
                            0xFFFF6600,
                            2.0f,
                            false
                    );
                }
            } else if (state.isActive || state.energy >= 30) {
                // Electric RF: Glowing induction heating coils
                float pulse = (float) Math.sin((state.gameTime + state.partialTicks) * 0.15f) * 0.1f + 0.9f;
                int coilColor = 0xFF000000 | ((int) (255 * pulse) << 16) | ((int) (140 * pulse) << 8) | 0x20;

                for (float zOffset : new float[]{-0.25f, 0.0f, 0.25f}) {
                    submitNodeCollector.submitShapeOutline(
                            poseStack,
                            Shapes.box(-1.35f, 0.05f, zOffset - 0.04f, 1.35f, 0.10f, zOffset + 0.04f),
                            RenderTypes.lines(),
                            coilColor,
                            3.5f,
                            false
                    );
                }
            }

            // C. 8 Cooking Items floating in the chamber
            float[] xOffsets = new float[]{-0.9f, -0.3f, 0.3f, 0.9f};

            for (int i = 0; i < 8; i++) {
                if (state.hasItem[i]) {
                    int col = i % 4;
                    int row = i / 4;
                    float xPos = xOffsets[col];
                    float yPos = (row == 0 ? 0.60f : 1.40f);

                    float bobbing = (float) Math.sin((state.gameTime + state.partialTicks) * 0.12f + i * 0.7f) * 0.04f;
                    float spin = (state.gameTime + state.partialTicks) * 2.0f + i * 45f;

                    poseStack.pushPose();
                    poseStack.translate(xPos, yPos + bobbing, 0.0f);
                    poseStack.rotateDegrees(Axis.YP, spin);
                    poseStack.scale(0.42f, 0.42f, 0.42f);

                    state.itemStates[i].submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);

                    poseStack.popPose();
                }
            }

            poseStack.popPose();
        }
    }
}
