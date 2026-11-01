package com.refinery.item;

import com.refinery.RefineryMod;
import com.refinery.compat.LogisticsPipeCompat;
import com.refinery.entity.PipeGateEntity;
import com.refinery.gate.GateTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

public class PipeGateItem extends BlockItem {
    private final GateTier tier;
    private final boolean autarchic;

    public PipeGateItem(Block block, GateTier tier, boolean autarchic, Properties properties) {
        super(block, properties);
        this.tier = tier;
        this.autarchic = autarchic;
    }

    public GateTier getTier() {
        return tier;
    }

    public boolean isAutarchic() {
        return autarchic;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        Direction clickedFace = context.getClickedFace();
        Player player = context.getPlayer();

        // Check if placed on a pipe or machine/container
        if (isPipeOrTarget(level, clickedPos, clickedState)) {
            List<PipeGateEntity> existing = level.getEntitiesOfClass(PipeGateEntity.class, new AABB(clickedPos));
            Direction targetFace = resolveBestPipeFace(level, clickedPos, clickedFace, context.getClickLocation(), player, existing);

            // Check if there is already a gate entity on this face of this block
            for (PipeGateEntity e : existing) {
                if (e.getAttachedFace() == targetFace) {
                    return InteractionResult.PASS;
                }
            }

            if (!level.isClientSide()) {
                PipeGateEntity gate = new PipeGateEntity(RefineryMod.PIPE_GATE_ENTITY.get(), level, clickedPos, targetFace, this.tier, this.autarchic);
                level.addFreshEntity(gate);
                gate.notifyPipes();
                level.playSound(null, clickedPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0f, 1.2f);
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // If player is not clicking on a pipe, allow placing as a block if possible
        return super.useOn(context);
    }

    public static Direction resolveBestPipeFace(
            Level level,
            BlockPos pos,
            Direction clickedFace,
            net.minecraft.world.phys.Vec3 hitLocation,
            @org.jetbrains.annotations.Nullable Player player,
            List<PipeGateEntity> existing
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof com.refinery.block.entity.IndustrialPipeBlockEntity pipeBe)) {
            return clickedFace;
        }

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

        Direction.Axis singleAxis = null;
        if (activeAxes == 0) {
            singleAxis = Direction.Axis.Z;
        } else if (activeAxes == 1) {
            singleAxis = verticalRun ? Direction.Axis.Y : (zRun ? Direction.Axis.Z : Direction.Axis.X);
        }

        // Check if clickedFace is already a valid radial side of the pipe and not already occupied
        boolean clickedValid = !pipeBe.getConnection(clickedFace).isConnected()
                && (singleAxis == null || clickedFace.getAxis() != singleAxis);
        boolean clickedOccupied = false;
        for (PipeGateEntity e : existing) {
            if (e.getAttachedFace() == clickedFace) {
                clickedOccupied = true;
                break;
            }
        }
        if (clickedValid && !clickedOccupied) {
            return clickedFace;
        }

        // Otherwise, snap to the nearest valid radial face facing the click point / player
        double dx = hitLocation.x - (pos.getX() + 0.5);
        double dy = hitLocation.y - (pos.getY() + 0.5);
        double dz = hitLocation.z - (pos.getZ() + 0.5);
        if (player != null) {
            net.minecraft.world.phys.Vec3 look = player.getLookAngle();
            dx -= look.x * 0.25;
            dy -= look.y * 0.25;
            dz -= look.z * 0.25;
        }

        Direction bestDir = clickedFace;
        double bestScore = -Double.MAX_VALUE;
        for (Direction candidate : Direction.values()) {
            if (pipeBe.getConnection(candidate).isConnected()) continue;
            if (singleAxis != null && candidate.getAxis() == singleAxis) continue;
            boolean occupied = false;
            for (PipeGateEntity e : existing) {
                if (e.getAttachedFace() == candidate) {
                    occupied = true;
                    break;
                }
            }
            if (occupied) continue;

            double score = candidate.getStepX() * dx + candidate.getStepY() * dy + candidate.getStepZ() * dz;
            if (score > bestScore) {
                bestScore = score;
                bestDir = candidate;
            }
        }
        return bestDir;
    }

    public static boolean isPipeOrTarget(Level level, BlockPos pos, BlockState state) {
        if (state.isAir()) return false;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && LogisticsPipeCompat.isLogisticsPipe(be)) {
            return true;
        }

        String desc = state.getBlock().getDescriptionId().toLowerCase();
        if (desc.contains("pipe") || desc.contains("tube") || desc.contains("duct")) {
            return true;
        }

        // Allow placing on any block with Item or Fluid capabilities (chests, tanks, boilers, machines)
        if (level.getCapability(Capabilities.Item.BLOCK, pos, null) != null ||
            level.getCapability(Capabilities.Fluid.BLOCK, pos, null) != null ||
            level.getCapability(Capabilities.Energy.BLOCK, pos, null) != null) {
            return true;
        }

        return false;
    }
}
