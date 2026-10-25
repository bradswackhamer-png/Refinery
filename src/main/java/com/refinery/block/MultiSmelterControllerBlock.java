package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.MultiSmelterControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class MultiSmelterControllerBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty BLUEPRINT = BooleanProperty.create("blueprint");

    public MultiSmelterControllerBlock(Properties properties) {
        super(properties.strength(2.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ASSEMBLED, false)
                .setValue(ACTIVE, false)
                .setValue(BLUEPRINT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ASSEMBLED, ACTIVE, BLUEPRINT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof MultiSmelterControllerBlockEntity controllerBE) {
                    controllerBE.toggleBlueprintProjection(player);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof MultiSmelterControllerBlockEntity controllerBE) {
                if (controllerBE.isAssembled()) {
                    player.openMenu(controllerBE, pos);
                } else {
                    controllerBE.toggleBlueprintProjection(player);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MultiSmelterControllerBlockEntity controllerBE) {
            Containers.dropContents(level, pos, controllerBE);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            Direction facing = state.getValue(FACING);
            double x = pos.getX() + 0.5 + facing.getStepX() * 0.52;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + facing.getStepZ() * 0.52;

            if (random.nextDouble() < 0.25) {
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
                level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.01, 0.0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiSmelterControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof MultiSmelterControllerBlockEntity controllerBE) {
                MultiSmelterControllerBlockEntity.serverTick(lvl, pos, st, controllerBE);
            }
        };
    }
}
