package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.FeedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FeedingTroughBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    // Sloped trough collision shape (height 8.5/16)
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.5, 16.0);

    public FeedingTroughBlock(Properties properties) {
        super(properties.sound(SoundType.METAL).strength(3.0f, 6.0f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.FOOT));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        BlockPos headPos = pos.relative(facing);
        Level level = context.getLevel();

        if (level.getBlockState(headPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(headPos)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(PART, BedPart.FOOT);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            BlockPos headPos = pos.relative(facing);
            level.setBlock(headPos, state.setValue(PART, BedPart.HEAD), 3);
            state.updateNeighbourShapes(level, pos, 3);
        }
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
        BedPart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction neighborDir = (part == BedPart.FOOT) ? facing : facing.getOpposite();

        if (direction == neighborDir) {
            if (!neighborState.is(this) || neighborState.getValue(PART) == part) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, levelReader, scheduledTicks, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BedPart part = state.getValue(PART);
            Direction facing = state.getValue(FACING);
            BlockPos otherPos = (part == BedPart.FOOT) ? pos.relative(facing) : pos.relative(facing.getOpposite());
            BlockState otherState = level.getBlockState(otherPos);

            if (otherState.is(this) && otherState.getValue(PART) != part) {
                if (player.isCreative() && part == BedPart.HEAD) {
                    level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
                }
            }

            if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity be) {
                Containers.dropContents(level, pos, be.getMaster());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity be) {
            FeedingTroughBlockEntity master = be.getMaster();

            // Quick deposit food into trough with right-click
            if (!stack.isEmpty() && FeedingTroughBlockEntity.isFeedItem(stack)) {
                if (!level.isClientSide()) {
                    boolean inserted = false;
                    // First try to merge into existing matching stacks
                    for (int i = 0; i < master.getContainerSize(); i++) {
                        ItemStack slotStack = master.getItem(i);
                        if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                            int space = slotStack.getMaxStackSize() - slotStack.getCount();
                            int toAdd = Math.min(space, stack.getCount());
                            slotStack.grow(toAdd);
                            stack.shrink(toAdd);
                            master.setItem(i, slotStack);
                            inserted = true;
                            if (stack.isEmpty()) break;
                        }
                    }

                    // Then try empty slots
                    if (!stack.isEmpty()) {
                        for (int i = 0; i < master.getContainerSize(); i++) {
                            ItemStack slotStack = master.getItem(i);
                            if (slotStack.isEmpty()) {
                                master.setItem(i, stack.copy());
                                stack.setCount(0);
                                inserted = true;
                                break;
                            }
                        }
                    }

                    if (inserted) {
                        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    return InteractionResult.SUCCESS;
                }
            }

            // Open Menu
            if (!level.isClientSide()) {
                player.openMenu(master);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity be) {
                player.openMenu(be.getMaster());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FeedingTroughBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (lvl, pos, st, be) -> {
            if (be instanceof FeedingTroughBlockEntity troughBE) {
                FeedingTroughBlockEntity.serverTick(lvl, pos, st, troughBE);
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }
}
