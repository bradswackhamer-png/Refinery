package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
import com.refinery.entity.PipeGateEntity;
import com.refinery.entity.PipeWireEntity;
import com.refinery.gate.PipeWireColor;
import com.refinery.item.PipeWireItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PipeWireEntityRenderer extends EntityRenderer<PipeWireEntity, PipeWireRenderState> {
    private static final int FULL_BRIGHT = 0x00F000F0;
    private static final Identifier TEXTURE_WIRE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/entity/pipe_wire.png"
    );

    // Half-width of the wire beam (~0.5 pixels thick)
    private static final float HW = 0.016f;
    // Radial offset from pipe axis to hug the 0.200f pipe cylinder outer wall
    private static final float D = 0.148f;

    public PipeWireEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PipeWireRenderState createRenderState() {
        return new PipeWireRenderState();
    }

    @Override
    public void extractRenderState(PipeWireEntity entity, PipeWireRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.hasRed = entity.hasWire(PipeWireColor.RED);
        state.hasBlue = entity.hasWire(PipeWireColor.BLUE);
        state.hasGreen = entity.hasWire(PipeWireColor.GREEN);
        state.hasYellow = entity.hasWire(PipeWireColor.YELLOW);

        state.signalRed = entity.isSignalActive(PipeWireColor.RED);
        state.signalBlue = entity.isSignalActive(PipeWireColor.BLUE);
        state.signalGreen = entity.isSignalActive(PipeWireColor.GREEN);
        state.signalYellow = entity.isSignalActive(PipeWireColor.YELLOW);

        Level level = entity.level();
        BlockPos pipePos = entity.getPipePos();

        for (int c = 0; c < 4; c++) {
            for (int d = 0; d < 6; d++) {
                state.connections[c][d] = false;
            }
        }

        if (level == null || pipePos == null) {
            return;
        }

        // 1. Attached gates on this pipe: wires branch directly to the gate's terminal face
        List<PipeGateEntity> gates = level.getEntitiesOfClass(PipeGateEntity.class, new AABB(pipePos));
        for (PipeGateEntity gate : gates) {
            Direction gd = gate.getDirection();
            if (gd != null) {
                for (PipeWireColor color : PipeWireColor.values()) {
                    if (gate.getTier().supportsWire(color) && entity.hasWire(color)) {
                        state.connections[color.ordinal()][gd.ordinal()] = true;
                    }
                }
            }
        }

        // 2. Neighbor pipes: connect continuous wires between adjacent pipes that share this wire color
        for (Direction d : Direction.values()) {
            BlockPos neighborPos = pipePos.relative(d);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (PipeWireItem.isPipeOrHost(level, neighborPos, neighborState)) {
                List<PipeWireEntity> neighborWires = level.getEntitiesOfClass(PipeWireEntity.class, new AABB(neighborPos));
                if (!neighborWires.isEmpty()) {
                    PipeWireEntity nw = neighborWires.get(0);
                    for (PipeWireColor color : PipeWireColor.values()) {
                        if (entity.hasWire(color) && nw.hasWire(color)) {
                            state.connections[color.ordinal()][d.ordinal()] = true;
                        }
                    }
                }
            }
        }

        // 3. Fallback: if a placed wire has no neighbor wire or gate yet, run along the physical pipe's arms
        for (PipeWireColor color : PipeWireColor.values()) {
            if (!entity.hasWire(color)) continue;
            int cIdx = color.ordinal();
            boolean hasAny = false;
            for (int d = 0; d < 6; d++) {
                if (state.connections[cIdx][d]) {
                    hasAny = true;
                    break;
                }
            }
            if (!hasAny) {
                for (Direction d : Direction.values()) {
                    BlockPos neighborPos = pipePos.relative(d);
                    BlockState neighborState = level.getBlockState(neighborPos);
                    if (PipeWireItem.isPipeOrHost(level, neighborPos, neighborState)) {
                        state.connections[cIdx][d.ordinal()] = true;
                    }
                }
            }
        }
    }

    @Override
    public void submit(PipeWireRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(TEXTURE_WIRE),
                (pose, consumer) -> {
                    Matrix4f m = pose.pose();

                    // Render Red Wire (Top-Left Quadrant)
                    if (state.hasRed) {
                        float r = state.signalRed ? 1.0f : 0.85f;
                        float g = state.signalRed ? 0.28f : 0.12f;
                        float b = state.signalRed ? 0.28f : 0.12f;
                        int light = state.signalRed ? FULL_BRIGHT : state.lightCoords;
                        renderWireRun(m, consumer, -D, D, -D, state.connections[PipeWireColor.RED.ordinal()], r, g, b, light);
                    }

                    // Render Blue Wire (Top-Right Quadrant)
                    if (state.hasBlue) {
                        float r = state.signalBlue ? 0.28f : 0.15f;
                        float g = state.signalBlue ? 0.72f : 0.38f;
                        float b = state.signalBlue ? 1.0f : 0.88f;
                        int light = state.signalBlue ? FULL_BRIGHT : state.lightCoords;
                        renderWireRun(m, consumer, D, D, -D, state.connections[PipeWireColor.BLUE.ordinal()], r, g, b, light);
                    }

                    // Render Green Wire (Bottom-Left Quadrant)
                    if (state.hasGreen) {
                        float r = state.signalGreen ? 0.28f : 0.10f;
                        float g = state.signalGreen ? 1.0f : 0.68f;
                        float b = state.signalGreen ? 0.40f : 0.20f;
                        int light = state.signalGreen ? FULL_BRIGHT : state.lightCoords;
                        renderWireRun(m, consumer, -D, -D, -D, state.connections[PipeWireColor.GREEN.ordinal()], r, g, b, light);
                    }

                    // Render Yellow Wire (Bottom-Right Quadrant)
                    if (state.hasYellow) {
                        float r = state.signalYellow ? 1.0f : 0.90f;
                        float g = state.signalYellow ? 0.95f : 0.72f;
                        float b = state.signalYellow ? 0.28f : 0.10f;
                        int light = state.signalYellow ? FULL_BRIGHT : state.lightCoords;
                        renderWireRun(m, consumer, D, -D, -D, state.connections[PipeWireColor.YELLOW.ordinal()], r, g, b, light);
                    }
                }
        );
    }

    /**
     * Renders a continuous 3D wire run consisting of a central hub collar and directional conduit beams
     * extending along the pipe outer cylinder to each connected direction.
     */
    private void renderWireRun(
            Matrix4f m,
            VertexConsumer c,
            float cx, float cy, float cz,
            boolean[] conns,
            float r, float g, float b,
            int light
    ) {
        // 1. Center Junction Node (small mounting collar bridging connected arms)
        renderBox(m, c,
                cx - HW * 1.35f, cy - HW * 1.35f, cz - HW * 1.35f,
                cx + HW * 1.35f, cy + HW * 1.35f, cz + HW * 1.35f,
                r, g, b, 1.0f, light);

        // 2. Directional Arms along the pipe length
        // DOWN (-Y)
        if (conns[Direction.DOWN.ordinal()]) {
            renderBox(m, c, cx - HW, -0.5f, cz - HW, cx + HW, cy, cz + HW, r, g, b, 1.0f, light);
        }
        // UP (+Y)
        if (conns[Direction.UP.ordinal()]) {
            renderBox(m, c, cx - HW, cy, cz - HW, cx + HW, 0.5f, cz + HW, r, g, b, 1.0f, light);
        }
        // NORTH (-Z)
        if (conns[Direction.NORTH.ordinal()]) {
            renderBox(m, c, cx - HW, cy - HW, -0.5f, cx + HW, cy + HW, cz, r, g, b, 1.0f, light);
        }
        // SOUTH (+Z)
        if (conns[Direction.SOUTH.ordinal()]) {
            renderBox(m, c, cx - HW, cy - HW, cz, cx + HW, cy + HW, 0.5f, r, g, b, 1.0f, light);
        }
        // WEST (-X)
        if (conns[Direction.WEST.ordinal()]) {
            renderBox(m, c, -0.5f, cy - HW, cz - HW, cx, cy + HW, cz + HW, r, g, b, 1.0f, light);
        }
        // EAST (+X)
        if (conns[Direction.EAST.ordinal()]) {
            renderBox(m, c, cx, cy - HW, cz - HW, 0.5f, cy + HW, cz + HW, r, g, b, 1.0f, light);
        }
    }

    private void renderBox(
            Matrix4f m,
            VertexConsumer c,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float r, float g, float b, float a,
            int light
    ) {
        int overlay = OverlayTexture.NO_OVERLAY;

        // Top (+Y)
        c.addVertex(m, x0, y1, z1).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
        c.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
        c.addVertex(m, x1, y1, z0).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
        c.addVertex(m, x0, y1, z0).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 1.0f, 0.0f);

        // Bottom (-Y)
        c.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
        c.addVertex(m, x1, y0, z0).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
        c.addVertex(m, x1, y0, z1).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);
        c.addVertex(m, x0, y0, z1).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, -1.0f, 0.0f);

        // North (-Z)
        c.addVertex(m, x1, y1, z0).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
        c.addVertex(m, x1, y0, z0).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
        c.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
        c.addVertex(m, x0, y1, z0).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, -1.0f);

        // South (+Z)
        c.addVertex(m, x0, y1, z1).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
        c.addVertex(m, x0, y0, z1).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
        c.addVertex(m, x1, y0, z1).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
        c.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(0.0f, 0.0f, 1.0f);

        // West (-X)
        c.addVertex(m, x0, y1, z0).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
        c.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
        c.addVertex(m, x0, y0, z1).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);
        c.addVertex(m, x0, y1, z1).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(-1.0f, 0.0f, 0.0f);

        // East (+X)
        c.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
        c.addVertex(m, x1, y0, z1).setColor(r, g, b, a).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
        c.addVertex(m, x1, y0, z0).setColor(r, g, b, a).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
        c.addVertex(m, x1, y1, z0).setColor(r, g, b, a).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(1.0f, 0.0f, 0.0f);
    }
}
