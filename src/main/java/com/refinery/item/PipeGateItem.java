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
            // Check if there is already a gate entity on this face of this block
            List<PipeGateEntity> existing = level.getEntitiesOfClass(PipeGateEntity.class, new AABB(clickedPos));
            for (PipeGateEntity e : existing) {
                if (e.getAttachedFace() == clickedFace) {
                    return InteractionResult.PASS;
                }
            }

            if (!level.isClientSide()) {
                PipeGateEntity gate = new PipeGateEntity(RefineryMod.PIPE_GATE_ENTITY.get(), level, clickedPos, clickedFace, this.tier, this.autarchic);
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
