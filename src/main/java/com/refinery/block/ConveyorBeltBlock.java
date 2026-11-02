package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.ConveyorBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ConveyorBeltBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty CONNECTED_FRONT = BooleanProperty.create("connected_front");
    public static final BooleanProperty CONNECTED_BACK = BooleanProperty.create("connected_back");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    // 5px low-profile industrial conveyor bounding box (matches reference extruded bed)
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);
    // 4px collision box for items so items entering from slopes/conveyors never catch on a 5px edge
    protected static final VoxelShape ITEM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

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
        if (context instanceof EntityCollisionContext ecc && ecc.getEntity() instanceof ItemEntity) {
            return ITEM_SHAPE;
        }
        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return false;
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

        boolean front = isBeltMatchingFront(level, currentPos, facing);
        boolean back = isBeltMatchingBack(level, currentPos, facing);
        boolean powered = level.hasNeighborSignal(currentPos);

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CONNECTED_FRONT, front)
                .setValue(CONNECTED_BACK, back)
                .setValue(POWERED, powered);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get()) || stack.getItem().toString().contains("wrench")) {
            if (!level.isClientSide()) {
                Direction currentFacing = state.getValue(FACING);
                Direction newFacing = player.isShiftKeyDown() ? currentFacing.getCounterClockWise() : currentFacing.getClockWise();
                BlockState newState = state.setValue(FACING, newFacing);
                newState = updateConnections(newState, level, pos);
                level.setBlock(pos, newState, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.getName().toUpperCase()));
            }
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                Direction currentFacing = state.getValue(FACING);
                Direction newFacing = currentFacing.getClockWise();
                BlockState newState = state.setValue(FACING, newFacing);
                newState = updateConnections(newState, level, pos);
                level.setBlock(pos, newState, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.getName().toUpperCase()));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
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
        boolean front = isBeltMatchingFront(levelReader, currentPos, facing);
        boolean back = isBeltMatchingBack(levelReader, currentPos, facing);
        return state.setValue(CONNECTED_FRONT, front).setValue(CONNECTED_BACK, back);
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

    public static BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        boolean front = isBeltMatchingFront(level, pos, facing);
        boolean back = isBeltMatchingBack(level, pos, facing);
        return state.setValue(CONNECTED_FRONT, front).setValue(CONNECTED_BACK, back);
    }

    private static boolean isBeltMatchingFront(LevelReader level, BlockPos pos, Direction facing) {
        if (level == null) return false;
        BlockPos frontPos = pos.relative(facing);
        BlockState neighbor = level.getBlockState(frontPos);
        if (neighbor.getBlock() instanceof ConveyorBeltBlock && neighbor.getValue(FACING) == facing) {
            return true;
        }
        if (neighbor.getBlock() instanceof RightAngleConveyorBeltBlock && neighbor.getValue(RightAngleConveyorBeltBlock.FACING) == facing) {
            return true;
        }
        if (neighbor.getBlock() instanceof SlopedConveyorBeltBlock && neighbor.getValue(SlopedConveyorBeltBlock.FACING) == facing
                && neighbor.getValue(SlopedConveyorBeltBlock.SLOPE) == SlopedConveyorBeltBlock.SlopeDirection.UP) {
            return true;
        }
        BlockState neighborBelow = level.getBlockState(frontPos.below());
        if (neighborBelow.getBlock() instanceof SlopedConveyorBeltBlock && neighborBelow.getValue(SlopedConveyorBeltBlock.FACING) == facing
                && neighborBelow.getValue(SlopedConveyorBeltBlock.SLOPE) == SlopedConveyorBeltBlock.SlopeDirection.DOWN) {
            return true;
        }
        return false;
    }

    private static boolean isBeltMatchingBack(LevelReader level, BlockPos pos, Direction facing) {
        if (level == null) return false;
        BlockPos backPos = pos.relative(facing.getOpposite());
        BlockState neighbor = level.getBlockState(backPos);
        if (neighbor.getBlock() instanceof ConveyorBeltBlock && neighbor.getValue(FACING) == facing) {
            return true;
        }
        if (neighbor.getBlock() instanceof RightAngleConveyorBeltBlock && RightAngleConveyorBeltBlock.getExitDirection(neighbor) == facing) {
            return true;
        }
        if (neighbor.getBlock() instanceof SlopedConveyorBeltBlock && neighbor.getValue(SlopedConveyorBeltBlock.FACING) == facing
                && neighbor.getValue(SlopedConveyorBeltBlock.SLOPE) == SlopedConveyorBeltBlock.SlopeDirection.DOWN) {
            return true;
        }
        BlockState neighborBelow = level.getBlockState(backPos.below());
        if (neighborBelow.getBlock() instanceof SlopedConveyorBeltBlock && neighborBelow.getValue(SlopedConveyorBeltBlock.FACING) == facing
                && neighborBelow.getValue(SlopedConveyorBeltBlock.SLOPE) == SlopedConveyorBeltBlock.SlopeDirection.UP) {
            return true;
        }
        return false;
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
