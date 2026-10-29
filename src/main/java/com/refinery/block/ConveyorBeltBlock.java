package com.refinery.block;

import com.refinery.block.entity.ConveyorBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ConveyorBeltBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty CONNECTED_FRONT = BooleanProperty.create("connected_front");
    public static final BooleanProperty CONNECTED_BACK = BooleanProperty.create("connected_back");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    // 5px low-profile industrial conveyor bounding box (matches reference extruded bed)
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

    public ConveyorBeltBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTED_FRONT, false)
                .setValue(CONNECTED_BACK, false)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTED_FRONT, CONNECTED_BACK, POWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos currentPos = context.getClickedPos();
        Level level = context.getLevel();

        boolean front = isBeltMatching(level, currentPos.relative(facing), facing);
        boolean back = isBeltMatching(level, currentPos.relative(facing.getOpposite()), facing);
        boolean powered = level.hasNeighborSignal(currentPos);

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CONNECTED_FRONT, front)
                .setValue(CONNECTED_BACK, back)
                .setValue(POWERED, powered);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTicks,
            BlockPos currentPos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        Direction facing = state.getValue(FACING);

        if (direction == facing) {
            boolean front = neighborState.getBlock() instanceof ConveyorBeltBlock
                    && neighborState.getValue(FACING) == facing;
            return state.setValue(CONNECTED_FRONT, front);
        }

        if (direction == facing.getOpposite()) {
            boolean back = neighborState.getBlock() instanceof ConveyorBeltBlock
                    && neighborState.getValue(FACING) == facing;
            return state.setValue(CONNECTED_BACK, back);
        }

        return super.updateShape(state, levelReader, scheduledTicks, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            @Nullable Orientation orientation,
            boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide()) {
            boolean powered = level.hasNeighborSignal(pos);
            BlockState updated = updateConnections(state, level, pos);
            if (powered != updated.getValue(POWERED) || updated != state) {
                level.setBlock(pos, updated.setValue(POWERED, powered), 3);
            }
        }
    }

    public static BlockState updateConnections(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        boolean front = isBeltMatching(level, pos.relative(facing), facing);
        boolean back = isBeltMatching(level, pos.relative(facing.getOpposite()), facing);
        return state.setValue(CONNECTED_FRONT, front).setValue(CONNECTED_BACK, back);
    }

    private static boolean isBeltMatching(Level level, BlockPos pos, Direction facing) {
        BlockState neighbor = level.getBlockState(pos);
        return neighbor.getBlock() instanceof ConveyorBeltBlock && neighbor.getValue(FACING) == facing;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorBeltBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return (lvl, pos, st, be) -> {
            if (be instanceof ConveyorBeltBlockEntity beltBe) {
                ConveyorBeltBlockEntity.tick(lvl, pos, st, beltBe);
            }
        };
    }
}
