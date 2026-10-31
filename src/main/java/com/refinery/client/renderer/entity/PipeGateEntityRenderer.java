package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.refinery.RefineryMod;
import com.refinery.entity.PipeGateEntity;
import com.refinery.gate.GateTier;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeGateEntityRenderer extends EntityRenderer<PipeGateEntity, PipeGateRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final CurvedPipeGateModel model;

    public static final Identifier TEXTURE_BLANK = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/blank_gate.png");
    public static final Identifier TEXTURE_BASIC = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/basic_gate.png");
    public static final Identifier TEXTURE_BASIC_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/basic_gate_active.png");
    public static final Identifier TEXTURE_BASIC_AUT = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/basic_autarchic_gate.png");
    public static final Identifier TEXTURE_BASIC_AUT_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/basic_autarchic_gate_active.png");

    public static final Identifier TEXTURE_IRON = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/iron_gate.png");
    public static final Identifier TEXTURE_IRON_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/iron_gate_active.png");
    public static final Identifier TEXTURE_IRON_AUT = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/iron_autarchic_gate.png");
    public static final Identifier TEXTURE_IRON_AUT_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/iron_autarchic_gate_active.png");

    public static final Identifier TEXTURE_GOLD = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/gold_gate.png");
    public static final Identifier TEXTURE_GOLD_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/gold_gate_active.png");
    public static final Identifier TEXTURE_GOLD_AUT = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/gold_autarchic_gate.png");
    public static final Identifier TEXTURE_GOLD_AUT_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/gold_autarchic_gate_active.png");

    public static final Identifier TEXTURE_DIAMOND = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/diamond_gate.png");
    public static final Identifier TEXTURE_DIAMOND_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/diamond_gate_active.png");
    public static final Identifier TEXTURE_DIAMOND_AUT = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/diamond_autarchic_gate.png");
    public static final Identifier TEXTURE_DIAMOND_AUT_ACTIVE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/block/diamond_autarchic_gate_active.png");

    public PipeGateEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
        this.model = new CurvedPipeGateModel();
    }

    @Override
    public PipeGateRenderState createRenderState() {
        return new PipeGateRenderState();
    }

    public static Identifier getTexture(PipeGateRenderState state) {
        if (state.tier == GateTier.BLANK) return TEXTURE_BLANK;
        boolean act = state.active || state.powered;
        boolean aut = state.autarchic;

        return switch (state.tier) {
            case BLANK -> TEXTURE_BLANK;
            case BASIC -> aut ? (act ? TEXTURE_BASIC_AUT_ACTIVE : TEXTURE_BASIC_AUT) : (act ? TEXTURE_BASIC_ACTIVE : TEXTURE_BASIC);
            case IRON -> aut ? (act ? TEXTURE_IRON_AUT_ACTIVE : TEXTURE_IRON_AUT) : (act ? TEXTURE_IRON_ACTIVE : TEXTURE_IRON);
            case GOLD -> aut ? (act ? TEXTURE_GOLD_AUT_ACTIVE : TEXTURE_GOLD_AUT) : (act ? TEXTURE_GOLD_ACTIVE : TEXTURE_GOLD);
            case DIAMOND -> aut ? (act ? TEXTURE_DIAMOND_AUT_ACTIVE : TEXTURE_DIAMOND_AUT) : (act ? TEXTURE_DIAMOND_ACTIVE : TEXTURE_DIAMOND);
        };
    }

    @Override
    public void extractRenderState(PipeGateEntity entity, PipeGateRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.direction = entity.getAttachedFace();
        state.tier = entity.getTier();
        state.autarchic = entity.isAutarchic();
        state.active = entity.isActive();
        state.powered = entity.isPowered();

        state.curveFactorX = 0.0f;
        state.curveFactorY = 0.0f;

        if (entity.level() != null) {
            BlockEntity be = entity.level().getBlockEntity(entity.getPipePos());
            if (be instanceof com.refinery.block.entity.IndustrialPipeBlockEntity pipeBe) {
                Direction face = entity.getAttachedFace();
                boolean connUp = pipeBe.getConnection(Direction.UP).isConnected();
                boolean connDown = pipeBe.getConnection(Direction.DOWN).isConnected();
                boolean connNorth = pipeBe.getConnection(Direction.NORTH).isConnected();
                boolean connSouth = pipeBe.getConnection(Direction.SOUTH).isConnected();
                boolean connWest = pipeBe.getConnection(Direction.WEST).isConnected();
                boolean connEast = pipeBe.getConnection(Direction.EAST).isConnected();

                boolean verticalRun = connUp || connDown;
                boolean zRun = connNorth || connSouth;
                boolean xRun = connWest || connEast;

                int activeAxes = (verticalRun ? 1 : 0) + (zRun ? 1 : 0) + (xRun ? 1 : 0);

                if (activeAxes == 0) {
                    // Standalone pipe: IndustrialPipeModel renders standalone pipes along Z axis
                    if (face.getAxis() != Direction.Axis.Z) {
                        if (face.getAxis() == Direction.Axis.X) {
                            state.curveFactorX = 0.0f;
                            state.curveFactorY = 1.0f;
                        } else if (face.getAxis() == Direction.Axis.Y) {
                            state.curveFactorX = 1.0f;
                            state.curveFactorY = 0.0f;
                        }
                    }
                } else if (activeAxes == 1) {
                    Direction.Axis pipeAxis = verticalRun ? Direction.Axis.Y : (zRun ? Direction.Axis.Z : Direction.Axis.X);
                    if (face.getAxis() != pipeAxis) {
                        if (face.getAxis() == Direction.Axis.Y) {
                            if (pipeAxis == Direction.Axis.Z) {
                                state.curveFactorX = 1.0f;
                                state.curveFactorY = 0.0f;
                            } else {
                                state.curveFactorX = 0.0f;
                                state.curveFactorY = 1.0f;
                            }
                        } else if (face.getAxis() == Direction.Axis.Z) {
                            if (pipeAxis == Direction.Axis.Y) {
                                state.curveFactorX = 1.0f;
                                state.curveFactorY = 0.0f;
                            } else {
                                state.curveFactorX = 0.0f;
                                state.curveFactorY = 1.0f;
                            }
                        } else if (face.getAxis() == Direction.Axis.X) {
                            if (pipeAxis == Direction.Axis.Y) {
                                state.curveFactorX = 1.0f;
                                state.curveFactorY = 0.0f;
                            } else {
                                state.curveFactorX = 0.0f;
                                state.curveFactorY = 1.0f;
                            }
                        }
                    }
                } else {
                    // Center fitting hub for corners, tees, crosses
                    state.curveFactorX = 0.7f;
                    state.curveFactorY = 0.7f;
                }
            }
        }

        ItemStack gateStack = entity.getGateItemStack();
        this.itemModelResolver.updateForNonLiving(state.item, gateStack, ItemDisplayContext.FIXED, entity);
    }

    @Override
    public void submit(PipeGateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        Direction dir = state.direction;
        // Orient the gate plate to face outwards from the pipe
        switch (dir) {
            case NORTH -> poseStack.rotateDegrees(Axis.YP, 180f);
            case SOUTH -> {}
            case WEST -> poseStack.rotateDegrees(Axis.YP, 90f);
            case EAST -> poseStack.rotateDegrees(Axis.YP, 270f);
            case UP -> poseStack.rotateDegrees(Axis.XP, 90f);
            case DOWN -> poseStack.rotateDegrees(Axis.XP, 270f);
        }

        Identifier texture = getTexture(state);

        // Submit 3D curved saddle plate wrapping tightly around the round pipe
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(texture),
                (pose, consumer) -> this.model.render(
                        pose.pose(),
                        consumer,
                        state.curveFactorX,
                        state.curveFactorY,
                        state.active || state.powered,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );

        poseStack.popPose();
    }
}
