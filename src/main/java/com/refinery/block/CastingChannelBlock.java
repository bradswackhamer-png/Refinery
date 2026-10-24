package com.refinery.block;

import com.refinery.block.entity.CastingChannelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CastingChannelBlock extends Block implements EntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    // Base trough shape
    private static final VoxelShape BASE = Block.box(4.0, 4.0, 4.0, 12.0, 8.0, 12.0);
    private static final VoxelShape N_SHAPE = Block.box(4.0, 4.0, 0.0, 12.0, 8.0, 4.0);
    private static final VoxelShape S_SHAPE = Block.box(4.0, 4.0, 12.0, 12.0, 8.0, 16.0);
    private static final VoxelShape W_SHAPE = Block.box(0.0, 4.0, 4.0, 4.0, 8.0, 12.0);
    private static final VoxelShape E_SHAPE = Block.box(12.0, 4.0, 4.0, 16.0, 8.0, 12.0);
    private static final VoxelShape D_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0);

    public CastingChannelBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = BASE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, N_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, S_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, W_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, E_SHAPE);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, D_SHAPE);
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, canConnectTo(level, pos.north(), Direction.NORTH))
                .setValue(SOUTH, canConnectTo(level, pos.south(), Direction.SOUTH))
                .setValue(WEST, canConnectTo(level, pos.west(), Direction.WEST))
                .setValue(EAST, canConnectTo(level, pos.east(), Direction.EAST))
                .setValue(DOWN, canConnectTo(level, pos.below(), Direction.DOWN));
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        boolean canConnect = canConnectTo(level, neighborPos, direction);
        return switch (direction) {
            case NORTH -> state.setValue(NORTH, canConnect);
            case SOUTH -> state.setValue(SOUTH, canConnect);
            case WEST -> state.setValue(WEST, canConnect);
            case EAST -> state.setValue(EAST, canConnect);
            case DOWN -> state.setValue(DOWN, canConnect);
            default -> state;
        };
    }

    private boolean canConnectTo(net.minecraft.world.level.LevelReader level, BlockPos targetPos, Direction dir) {
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() instanceof CastingChannelBlock) return true;
        if (targetState.getBlock() instanceof MelterPortBlock) return true;
        if (targetState.getBlock() instanceof CastingPortBlock) return true;
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CastingChannelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof CastingChannelBlockEntity channelBE) {
                channelBE.serverTick();
            }
        };
    }
}
