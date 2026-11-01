package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.RightAngleConveyorBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class RightAngleConveyorBeltBlock extends Block implements EntityBlock {

    public enum TurnDirection implements StringRepresentable {
        RIGHT("right"),
        LEFT("left");

        private final String name;

        TurnDirection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<TurnDirection> TURN = EnumProperty.create("turn", TurnDirection.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

    public RightAngleConveyorBeltBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TURN, TurnDirection.RIGHT)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TURN, POWERED);
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
        boolean powered = level.hasNeighborSignal(currentPos);
        TurnDirection turn = context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
                ? TurnDirection.LEFT
                : TurnDirection.RIGHT;

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(TURN, turn)
                .setValue(POWERED, powered);
    }

    public static Direction getExitDirection(BlockState state) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        TurnDirection turn = state.hasProperty(TURN) ? state.getValue(TURN) : TurnDirection.RIGHT;
        return turn == TurnDirection.RIGHT ? facing.getClockWise() : facing.getCounterClockWise();
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get()) || stack.getItem().toString().contains("wrench")) {
            if (!level.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    // Toggle TURN LEFT <-> RIGHT
                    TurnDirection cur = state.getValue(TURN);
                    TurnDirection next = cur == TurnDirection.RIGHT ? TurnDirection.LEFT : TurnDirection.RIGHT;
                    BlockState newState = state.setValue(TURN, next);
                    level.setBlock(pos, newState, 3);
                    Direction exit = getExitDirection(newState);
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.4f);
                    player.sendSystemMessage(Component.literal("§6[Conveyor]§r Turn: §e" + next.name() + " (Exits " + exit.name().toUpperCase() + ")"));
                } else {
                    // Rotate Direction Clockwise
                    Direction currentFacing = state.getValue(FACING);
                    Direction newFacing = currentFacing.getClockWise();
                    BlockState newState = state.setValue(FACING, newFacing);
                    level.setBlock(pos, newState, 3);
                    Direction exit = getExitDirection(newState);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                    player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.name().toUpperCase() + " -> " + exit.name().toUpperCase()));
                }
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
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                // Sneak + Right Click: Toggle Turn LEFT <-> RIGHT
                TurnDirection cur = state.getValue(TURN);
                TurnDirection next = cur == TurnDirection.RIGHT ? TurnDirection.LEFT : TurnDirection.RIGHT;
                BlockState newState = state.setValue(TURN, next);
                level.setBlock(pos, newState, 3);
                Direction exit = getExitDirection(newState);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.4f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Turn: §e" + next.name() + " (Exits " + exit.name().toUpperCase() + ")"));
            } else {
                // Normal Right Click: Rotate Direction Clockwise
                Direction currentFacing = state.getValue(FACING);
                Direction newFacing = currentFacing.getClockWise();
                BlockState newState = state.setValue(FACING, newFacing);
                level.setBlock(pos, newState, 3);
                Direction exit = getExitDirection(newState);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.name().toUpperCase() + " -> " + exit.name().toUpperCase()));
            }
        }
        return InteractionResult.SUCCESS;
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
            if (powered != state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, powered), 3);
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RightAngleConveyorBeltBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return (lvl, pos, st, be) -> {
            if (be instanceof RightAngleConveyorBeltBlockEntity beltBe) {
                RightAngleConveyorBeltBlockEntity.tick(lvl, pos, st, beltBe);
            }
        };
    }
}
