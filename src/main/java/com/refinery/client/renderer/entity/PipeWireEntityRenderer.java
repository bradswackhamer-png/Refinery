package com.refinery.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refinery.RefineryMod;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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

        for (int c = 0; c < 4; c++) {
            for (int d = 0; d < 6; d++) {
                state.connections[c][d] = false;
            }
        }

        Level level = entity.level();
        BlockPos pipePos = entity.getPipePos();
        if (level == null || pipePos == null) {
            return;
        }

        // 1. Query physical pipe connections at this block
        boolean[] pipeHasArm = new boolean[6];
        BlockEntity be = level.getBlockEntity(pipePos);
        if (be instanceof com.refinery.block.entity.IndustrialPipeBlockEntity pipeBe) {
            for (Direction d : Direction.values()) {
                pipeHasArm[d.ordinal()] = pipeBe.getConnection(d).isConnected();
            }
        } else {
            for (Direction d : Direction.values()) {
                BlockPos nPos = pipePos.relative(d);
                pipeHasArm[d.ordinal()] = PipeWireItem.isPipeOrHost(level, nPos, level.getBlockState(nPos));
            }
        }

        // Determine default pipe axis from physical arms
        boolean yRun = pipeHasArm[Direction.UP.ordinal()] || pipeHasArm[Direction.DOWN.ordinal()];
        boolean zRun = pipeHasArm[Direction.NORTH.ordinal()] || pipeHasArm[Direction.SOUTH.ordinal()];
        boolean xRun = pipeHasArm[Direction.WEST.ordinal()] || pipeHasArm[Direction.EAST.ordinal()];

        Direction.Axis pipeAxis = Direction.Axis.Y;
        if (yRun && !zRun && !xRun) pipeAxis = Direction.Axis.Y;
        else if (zRun && !yRun && !xRun) pipeAxis = Direction.Axis.Z;
        else if (xRun && !yRun && !zRun) pipeAxis = Direction.Axis.X;
        else if (yRun) pipeAxis = Direction.Axis.Y;
        else if (zRun) pipeAxis = Direction.Axis.Z;
        else if (xRun) pipeAxis = Direction.Axis.X;

        // 2. Compute directional connections and quadrant offsets for each wire color
        for (PipeWireColor color : PipeWireColor.values()) {
            if (!entity.hasWire(color)) continue;
            int cIdx = color.ordinal();

            int wireCount = 0;
            Direction firstWireDir = null;

            // Connect to neighboring pipes that share this wire color
            for (Direction d : Direction.values()) {
                BlockPos neighborPos = pipePos.relative(d);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (PipeWireItem.isPipeOrHost(level, neighborPos, neighborState)) {
                    List<PipeWireEntity> neighborWires = level.getEntitiesOfClass(PipeWireEntity.class, new AABB(neighborPos));
                    if (!neighborWires.isEmpty() && neighborWires.get(0).hasWire(color)) {
                        state.connections[cIdx][d.ordinal()] = true;
                        wireCount++;
                        if (firstWireDir == null) firstWireDir = d;
                    }
                }
            }

            // End wire or isolated wire: follow the physical pipe's direction across this block
            if (wireCount == 0) {
                // Isolated wire: follow the pipe's existing arms
                boolean added = false;
                for (Direction d : Direction.values()) {
                    if (pipeHasArm[d.ordinal()]) {
                        state.connections[cIdx][d.ordinal()] = true;
                        added = true;
                    }
                }
                if (!added) {
                    // Default to vertical pipe run if no neighbor connections yet
                    state.connections[cIdx][Direction.UP.ordinal()] = true;
                    state.connections[cIdx][Direction.DOWN.ordinal()] = true;
                }
            } else if (wireCount == 1 && firstWireDir != null) {
                // End wire: continue along the pipe's direction across this block!
                Direction opp = firstWireDir.getOpposite();
                if (pipeHasArm[opp.ordinal()]) {
                    // Straight continuation along the pipe axis
                    state.connections[cIdx][opp.ordinal()] = true;
                } else {
                    // Follow any other pipe arm (elbow / corner)
                    for (Direction d : Direction.values()) {
                        if (d != firstWireDir && pipeHasArm[d.ordinal()]) {
                            state.connections[cIdx][d.ordinal()] = true;
                            break;
                        }
                    }
                }
            }

            // Determine wire axis based on its actual connections
            boolean wY = state.connections[cIdx][Direction.UP.ordinal()] || state.connections[cIdx][Direction.DOWN.ordinal()];
            boolean wZ = state.connections[cIdx][Direction.NORTH.ordinal()] || state.connections[cIdx][Direction.SOUTH.ordinal()];
            boolean wX = state.connections[cIdx][Direction.WEST.ordinal()] || state.connections[cIdx][Direction.EAST.ordinal()];

            Direction.Axis wireAxis = pipeAxis;
            if (wY && !wZ && !wX) wireAxis = Direction.Axis.Y;
            else if (wZ && !wY && !wX) wireAxis = Direction.Axis.Z;
            else if (wX && !wY && !wZ) wireAxis = Direction.Axis.X;
            else if (wY) wireAxis = Direction.Axis.Y;
            else if (wZ) wireAxis = Direction.Axis.Z;
            else if (wX) wireAxis = Direction.Axis.X;

            // Set [cx, cy, cz] quadrant offsets for this color based on wireAxis
            float cx = 0.0f, cy = 0.0f, cz = 0.0f;
            switch (wireAxis) {
                case Y -> {
                    // Vertical pipe cylinder: cross section is X-Z
                    switch (color) {
                        case RED -> { cx = -D; cy = 0.0f; cz = -D; }
                        case BLUE -> { cx = D; cy = 0.0f; cz = -D; }
                        case GREEN -> { cx = -D; cy = 0.0f; cz = D; }
                        case YELLOW -> { cx = D; cy = 0.0f; cz = D; }
                    }
                }
                case Z -> {
                    // North-South pipe cylinder: cross section is X-Y
                    switch (color) {
                        case RED -> { cx = -D; cy = D; cz = 0.0f; }
                        case BLUE -> { cx = D; cy = D; cz = 0.0f; }
                        case GREEN -> { cx = -D; cy = -D; cz = 0.0f; }
                        case YELLOW -> { cx = D; cy = -D; cz = 0.0f; }
                    }
                }
                case X -> {
                    // East-West pipe cylinder: cross section is Y-Z
                    switch (color) {
                        case RED -> { cx = 0.0f; cy = D; cz = -D; }
                        case BLUE -> { cx = 0.0f; cy = D; cz = D; }
                        case GREEN -> { cx = 0.0f; cy = -D; cz = -D; }
                        case YELLOW -> { cx = 0.0f; cy = -D; cz = D; }
                    }
                }
            }

            state.offsets[cIdx][0] = cx;
            state.offsets[cIdx][1] = cy;
            state.offsets[cIdx][2] = cz;
        }
    }

    @Override
    public void submit(PipeWireRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(TEXTURE_WIRE),
                (pose, consumer) -> {
                    Matrix4f m = pose.pose();

                    // Render Red Wire
                    if (state.hasRed) {
                        float r = state.signalRed ? 1.0f : 0.85f;
                        float g = state.signalRed ? 0.28f : 0.12f;
                        float b = state.signalRed ? 0.28f : 0.12f;
                        int light = state.signalRed ? FULL_BRIGHT : state.lightCoords;
                        float[] off = state.offsets[PipeWireColor.RED.ordinal()];
                        renderWireRun(m, consumer, off[0], off[1], off[2], state.connections[PipeWireColor.RED.ordinal()], r, g, b, light);
                    }

                    // Render Blue Wire
                    if (state.hasBlue) {
                        float r = state.signalBlue ? 0.28f : 0.15f;
                        float g = state.signalBlue ? 0.72f : 0.38f;
                        float b = state.signalBlue ? 1.0f : 0.88f;
                        int light = state.signalBlue ? FULL_BRIGHT : state.lightCoords;
                        float[] off = state.offsets[PipeWireColor.BLUE.ordinal()];
                        renderWireRun(m, consumer, off[0], off[1], off[2], state.connections[PipeWireColor.BLUE.ordinal()], r, g, b, light);
                    }

                    // Render Green Wire
                    if (state.hasGreen) {
                        float r = state.signalGreen ? 0.28f : 0.10f;
                        float g = state.signalGreen ? 1.0f : 0.68f;
                        float b = state.signalGreen ? 0.40f : 0.20f;
                        int light = state.signalGreen ? FULL_BRIGHT : state.lightCoords;
                        float[] off = state.offsets[PipeWireColor.GREEN.ordinal()];
                        renderWireRun(m, consumer, off[0], off[1], off[2], state.connections[PipeWireColor.GREEN.ordinal()], r, g, b, light);
                    }

                    // Render Yellow Wire
                    if (state.hasYellow) {
                        float r = state.signalYellow ? 1.0f : 0.90f;
                        float g = state.signalYellow ? 0.95f : 0.72f;
                        float b = state.signalYellow ? 0.28f : 0.10f;
                        int light = state.signalYellow ? FULL_BRIGHT : state.lightCoords;
                        float[] off = state.offsets[PipeWireColor.YELLOW.ordinal()];
                        renderWireRun(m, consumer, off[0], off[1], off[2], state.connections[PipeWireColor.YELLOW.ordinal()], r, g, b, light);
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
