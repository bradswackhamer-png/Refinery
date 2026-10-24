package com.refinery.item;

import com.refinery.RefineryMod;
import com.refinery.compat.LogisticsPipeCompat;
import com.refinery.entity.PipeGateEntity;
import com.refinery.entity.PipeWireEntity;
import com.refinery.gate.PipeWireColor;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PipeWireItem extends Item {
    private final PipeWireColor color;

    public PipeWireItem(PipeWireColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public PipeWireColor getColor() {
        return this.color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack held = context.getItemInHand();

        if (!isPipeOrHost(level, pos, state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            List<PipeWireEntity> existingWires = level.getEntitiesOfClass(PipeWireEntity.class, new AABB(pos));
            PipeWireEntity wireEntity = existingWires.isEmpty() ? null : existingWires.get(0);

            if (wireEntity == null) {
                wireEntity = new PipeWireEntity(RefineryMod.PIPE_WIRE_ENTITY.get(), level, pos);
                wireEntity.setWirePresent(this.color, true);
                level.addFreshEntity(wireEntity);
                level.playSound(null, pos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 1.0f, 1.2f);
                if (player != null && !player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else {
                if (!wireEntity.hasWire(this.color)) {
                    wireEntity.setWirePresent(this.color, true);
                    level.playSound(null, pos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 1.0f, 1.2f);
                    if (player != null && !player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean isPipeOrHost(Level level, BlockPos pos, BlockState state) {
        if (state.isAir()) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && LogisticsPipeCompat.isLogisticsPipe(be)) return true;
        String desc = state.getBlock().getDescriptionId().toLowerCase();
        return desc.contains("pipe") || desc.contains("tube") || desc.contains("duct");
    }
}
