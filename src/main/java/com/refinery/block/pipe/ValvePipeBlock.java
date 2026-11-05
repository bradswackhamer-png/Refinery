package com.refinery.block.pipe;

import com.refinery.RefineryMod;
import com.refinery.entity.PipeGateEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class ValvePipeBlock extends IndustrialPipeBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    public ValvePipeBlock(Properties properties) {
        super(properties, IndustrialPipeType.VALVE, IndustrialPipeMaterial.VALVE);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(OPEN, true)
                .setValue(INVERTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, INVERTED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = hasRedstoneSignal(context.getLevel(), context.getClickedPos());
        return this.defaultBlockState()
                .setValue(OPEN, !powered)
                .setValue(INVERTED, false);
    }

    public static boolean hasRedstoneSignal(Level level, BlockPos pos) {
        if (level == null || pos == null) return false;
        if (level.hasNeighborSignal(pos)) {
            return true;
        }
        if (PipeGateEntity.isEmittingRedstone(level, pos, null)) {
            return true;
        }
        for (Direction dir : Direction.values()) {
            if (PipeGateEntity.isEmittingRedstone(level, pos.relative(dir), dir.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            updateValveState(state, level, pos);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        updateValveState(state, level, pos);
    }

    public static void updateValveState(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        boolean powered = hasRedstoneSignal(level, pos);
        boolean inverted = state.getValue(INVERTED);
        boolean shouldBeOpen = inverted ? powered : !powered;

        if (state.getValue(OPEN) != shouldBeOpen) {
            level.setBlock(pos, state.setValue(OPEN, shouldBeOpen), 3);
            level.playSound(
                    null,
                    pos,
                    shouldBeOpen ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS,
                    0.8f,
                    shouldBeOpen ? 1.2f : 0.9f
            );
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get()) && !player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                boolean newInverted = !state.getValue(INVERTED);
                boolean powered = hasRedstoneSignal(level, pos);
                boolean newOpen = newInverted ? powered : !powered;

                level.setBlock(pos, state.setValue(INVERTED, newInverted).setValue(OPEN, newOpen), 3);
                level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.8f, newInverted ? 1.2f : 0.8f);

                player.sendSystemMessage(Component.literal(newInverted
                        ? "§6[Valve Pipe] §eInverted Mode §7(Opens on Redstone Signal)"
                        : "§6[Valve Pipe] §eNormal Mode §7(Closes on Redstone Signal)"));
            }
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
