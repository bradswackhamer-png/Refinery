package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.SlopedConveyorBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class SlopedConveyorBeltBlock extends Block implements EntityBlock {

    public enum SlopeDirection implements StringRepresentable {
        UP("up"),
        DOWN("down");

        private final String name;

        SlopeDirection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<SlopeDirection> SLOPE = EnumProperty.create("slope", SlopeDirection.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    // Pre-computed 4-tier stepped collision shapes for players and mobs
    private static final Map<Direction, VoxelShape> UP_SHAPES = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> DOWN_SHAPES = new EnumMap<>(Direction.class);

    static {
        // Step heights in pixels: 5, 8, 12, 16
        // NORTH: Entry is SOUTH (z: 12..16, h: 5), exit is NORTH (z: 0..4, h: 16)
        UP_SHAPES.put(Direction.NORTH, Shapes.or(
                Block.box(0.0, 0.0, 12.0, 16.0, 5.0, 16.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 8.0, 12.0),
                Block.box(0.0, 0.0, 4.0, 16.0, 12.0, 8.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0)
        ));
        DOWN_SHAPES.put(Direction.NORTH, Shapes.or(
                Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 12.0, 12.0),
                Block.box(0.0, 0.0, 4.0, 16.0, 8.0, 8.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 4.0)
        ));

        // SOUTH: Entry is NORTH (z: 0..4, h: 5), exit is SOUTH (z: 12..16, h: 16)
        UP_SHAPES.put(Direction.SOUTH, Shapes.or(
                Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 4.0),
                Block.box(0.0, 0.0, 4.0, 16.0, 8.0, 8.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 12.0, 12.0),
                Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0)
        ));
        DOWN_SHAPES.put(Direction.SOUTH, Shapes.or(
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0),
                Block.box(0.0, 0.0, 4.0, 16.0, 12.0, 8.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 8.0, 12.0),
                Block.box(0.0, 0.0, 12.0, 16.0, 5.0, 16.0)
        ));

        // WEST: Entry is EAST (x: 12..16, h: 5), exit is WEST (x: 0..4, h: 16)
        UP_SHAPES.put(Direction.WEST, Shapes.or(
                Block.box(12.0, 0.0, 0.0, 16.0, 5.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 12.0, 8.0, 16.0),
                Block.box(4.0, 0.0, 0.0, 8.0, 12.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0)
        ));
        DOWN_SHAPES.put(Direction.WEST, Shapes.or(
                Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 12.0, 12.0, 16.0),
                Block.box(4.0, 0.0, 0.0, 8.0, 8.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 4.0, 5.0, 16.0)
        ));

        // EAST: Entry is WEST (x: 0..4, h: 5), exit is EAST (x: 12..16, h: 16)
        UP_SHAPES.put(Direction.EAST, Shapes.or(
                Block.box(0.0, 0.0, 0.0, 4.0, 5.0, 16.0),
                Block.box(4.0, 0.0, 0.0, 8.0, 8.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 12.0, 12.0, 16.0),
                Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        ));
        DOWN_SHAPES.put(Direction.EAST, Shapes.or(
                Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0),
                Block.box(4.0, 0.0, 0.0, 8.0, 12.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 12.0, 8.0, 16.0),
                Block.box(12.0, 0.0, 0.0, 16.0, 5.0, 16.0)
        ));
    }

    public SlopedConveyorBeltBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SLOPE, SlopeDirection.UP)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SLOPE, POWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        SlopeDirection slope = state.getValue(SLOPE);
        Map<Direction, VoxelShape> map = (slope == SlopeDirection.UP) ? UP_SHAPES : DOWN_SHAPES;
        return map.getOrDefault(facing, SHAPE);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Items must pass through smoothly without colliding with block walls or step ledges
        if (context instanceof EntityCollisionContext ecc && ecc.getEntity() instanceof ItemEntity) {
            return Shapes.empty();
        }
        Direction facing = state.getValue(FACING);
        SlopeDirection slope = state.getValue(SLOPE);
        Map<Direction, VoxelShape> map = (slope == SlopeDirection.UP) ? UP_SHAPES : DOWN_SHAPES;
        return map.getOrDefault(facing, SHAPE);
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
        SlopeDirection slope = context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
                ? SlopeDirection.DOWN
                : SlopeDirection.UP;

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(SLOPE, slope)
                .setValue(POWERED, powered);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get()) || stack.getItem().toString().contains("wrench")) {
            if (!level.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    // Toggle Slope UP <-> DOWN
                    SlopeDirection cur = state.getValue(SLOPE);
                    SlopeDirection next = cur == SlopeDirection.UP ? SlopeDirection.DOWN : SlopeDirection.UP;
                    level.setBlock(pos, state.setValue(SLOPE, next), 3);
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.4f);
                    player.sendSystemMessage(Component.literal("§6[Conveyor]§r Incline: §e" + next.name()));
                } else {
                    // Rotate Direction Clockwise
                    Direction currentFacing = state.getValue(FACING);
                    Direction newFacing = currentFacing.getClockWise();
                    level.setBlock(pos, state.setValue(FACING, newFacing), 3);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                    player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.getName().toUpperCase()));
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
                // Sneak + Right Click: Toggle Slope UP <-> DOWN
                SlopeDirection cur = state.getValue(SLOPE);
                SlopeDirection next = cur == SlopeDirection.UP ? SlopeDirection.DOWN : SlopeDirection.UP;
                level.setBlock(pos, state.setValue(SLOPE, next), 3);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.4f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Incline: §e" + next.name()));
            } else {
                // Normal Right Click: Rotate Direction Clockwise
                Direction currentFacing = state.getValue(FACING);
                Direction newFacing = currentFacing.getClockWise();
                level.setBlock(pos, state.setValue(FACING, newFacing), 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                player.sendSystemMessage(Component.literal("§6[Conveyor]§r Direction: §e" + newFacing.getName().toUpperCase()));
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
        return new SlopedConveyorBeltBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return (lvl, pos, st, be) -> {
            if (be instanceof SlopedConveyorBeltBlockEntity beltBe) {
                SlopedConveyorBeltBlockEntity.tick(lvl, pos, st, beltBe);
            }
        };
    }
}
