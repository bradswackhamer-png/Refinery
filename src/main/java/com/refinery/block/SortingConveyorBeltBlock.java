package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.SortingConveyorBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.Nullable;

public class SortingConveyorBeltBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty CONNECTED_FRONT = BooleanProperty.create("connected_front");
    public static final BooleanProperty CONNECTED_BACK = BooleanProperty.create("connected_back");
    public static final BooleanProperty CONNECTED_LEFT = BooleanProperty.create("connected_left");
    public static final BooleanProperty CONNECTED_RIGHT = BooleanProperty.create("connected_right");

    // Full 16px high shape to encompass both 5px bed and overhead optical scanner bridge
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BED_COLLISION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

    public SortingConveyorBeltBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(CONNECTED_FRONT, false)
                .setValue(CONNECTED_BACK, false)
                .setValue(CONNECTED_LEFT, false)
                .setValue(CONNECTED_RIGHT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, CONNECTED_FRONT, CONNECTED_BACK, CONNECTED_LEFT, CONNECTED_RIGHT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc && ecc.getEntity() instanceof ItemEntity) {
            return Shapes.empty();
        }
        return BED_COLLISION_SHAPE;
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
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        boolean powered = level.hasNeighborSignal(pos);

        BlockState state = defaultBlockState()
                .setValue(FACING, facing)
                .setValue(POWERED, powered);
        return updateConnections(state, level, pos);
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
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.name().toUpperCase()));
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof SortingConveyorBeltBlockEntity be) {
                serverPlayer.openMenu(be, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof SortingConveyorBeltBlockEntity be) {
                serverPlayer.openMenu(be, pos);
            }
        }
        return InteractionResult.SUCCESS;
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
        return updateConnections(state, levelReader, currentPos);
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
        boolean front = isOutgoingTarget(level, pos, facing);
        boolean back = ConveyorBeltBlock.isIncomingBelt(level, pos, facing.getOpposite());
        boolean left = isOutgoingTarget(level, pos, facing.getCounterClockWise());
        boolean right = isOutgoingTarget(level, pos, facing.getClockWise());

        return state.setValue(CONNECTED_FRONT, front)
                .setValue(CONNECTED_BACK, back)
                .setValue(CONNECTED_LEFT, left)
                .setValue(CONNECTED_RIGHT, right);
    }

    public static boolean isOutgoingTarget(LevelReader level, BlockPos pos, Direction exitDir) {
        if (level == null) return false;
        BlockPos targetPos = pos.relative(exitDir);
        BlockState targetState = level.getBlockState(targetPos);
        if (ConveyorBeltBlock.isAnyConveyorBlock(targetState)) {
            if (targetState.hasProperty(FACING)) {
                return targetState.getValue(FACING) != exitDir.getOpposite();
            }
            return true;
        }
        BlockState targetBelowState = level.getBlockState(targetPos.below());
        if (ConveyorBeltBlock.isAnyConveyorBlock(targetBelowState)) {
            return true;
        }
        if (level instanceof Level lvl) {
            var cap = lvl.getCapability(Capabilities.Item.BLOCK, targetPos, exitDir.getOpposite());
            if (cap != null) return true;
        }
        return false;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SortingConveyorBeltBlockEntity sortingBe) {
                Containers.dropContents(level, pos, sortingBe.getFilters());
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level l && !l.isClientSide()) {
            BlockEntity be = l.getBlockEntity(pos);
            if (be instanceof SortingConveyorBeltBlockEntity sortingBe) {
                Containers.dropContents(l, pos, sortingBe.getFilters());
                l.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SortingConveyorBeltBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return (lvl, pos, st, be) -> {
            if (be instanceof SortingConveyorBeltBlockEntity sortingBe) {
                SortingConveyorBeltBlockEntity.tick(lvl, pos, st, sortingBe);
            }
        };
    }
}
