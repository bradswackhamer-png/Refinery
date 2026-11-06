package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.BitumenPileBlockEntity;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BitumenPileBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BitumenPilePart> PART = EnumProperty.create("part", BitumenPilePart.class);
    // Stage 0: empty bunker with bitumen floor dusting
    // Stages 1-4: rising bitumen mound filling bottom block
    // Stages 5-8: towering mountain into top block
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 8);

    public BitumenPileBlock(BlockBehaviour.Properties properties) {
        super(properties
                .strength(2.0f, 3.0f)
                .sound(SoundType.STONE)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BitumenPilePart.BOTTOM_FRONT_LEFT)
                .setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, STAGE);
    }

    public static BlockPos[] getAllPositions(BlockPos masterPos, Direction facing) {
        Direction backDir = facing.getOpposite();
        Direction rightDir = facing.getClockWise();
        BitumenPilePart[] allParts = BitumenPilePart.values();
        BlockPos[] positions = new BlockPos[allParts.length];

        for (int i = 0; i < allParts.length; i++) {
            BitumenPilePart part = allParts[i];
            positions[i] = masterPos
                    .relative(rightDir, part.dx())
                    .relative(backDir, part.dz())
                    .above(part.dy());
        }
        return positions;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockPos[] positions = getAllPositions(pos, facing);
        for (BlockPos p : positions) {
            if (!level.getBlockState(p).canBeReplaced(context)) {
                return null;
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, BitumenPilePart.BOTTOM_FRONT_LEFT)
                .setValue(STAGE, 0);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            Direction backDir = facing.getOpposite();
            Direction rightDir = facing.getClockWise();

            for (BitumenPilePart part : BitumenPilePart.values()) {
                if (part == BitumenPilePart.BOTTOM_FRONT_LEFT) continue;
                BlockPos p = pos.relative(rightDir, part.dx()).relative(backDir, part.dz()).above(part.dy());
                level.setBlock(p, state.setValue(PART, part), 3);
            }

            level.updateNeighborsAt(pos, this);
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
        BlockPos masterPos = getMasterPos(currentPos, state);
        if (!currentPos.equals(masterPos)) {
            BlockState masterState = levelReader.getBlockState(masterPos);
            if (!masterState.is(this) || masterState.getValue(PART) != BitumenPilePart.BOTTOM_FRONT_LEFT) {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            // Master verifies all 17 siblings
            Direction facing = state.getValue(FACING);
            BlockPos[] positions = getAllPositions(masterPos, facing);
            for (int i = 1; i < positions.length; i++) {
                if (!levelReader.getBlockState(positions[i]).is(this)) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return super.updateShape(state, levelReader, scheduledTicks, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos masterPos = getMasterPos(pos, state);
            BitumenPileBlockEntity masterBe = getMasterBe(level, pos, state);

            if (masterBe != null) {
                Containers.dropContents(level, masterPos, masterBe);
            }

            // Clean up sibling blocks without dropping duplicate items
            Direction facing = state.getValue(FACING);
            BlockPos[] positions = getAllPositions(masterPos, facing);

            for (BlockPos p : positions) {
                if (!p.equals(pos)) {
                    BlockState s = level.getBlockState(p);
                    if (s.is(this)) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 35);
                        level.levelEvent(player, 2001, p, Block.getId(s));
                    }
                }
            }

            // If the player destroyed a non-master part in survival, drop the single item
            if (!player.isCreative() && !pos.equals(masterPos)) {
                popResource(level, masterPos, new ItemStack(this));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(PART) == BitumenPilePart.BOTTOM_FRONT_LEFT) {
            return List.of(new ItemStack(this));
        }
        return List.of();
    }

    public static BlockPos getMasterPos(BlockPos pos, BlockState state) {
        if (!state.hasProperty(PART) || !state.hasProperty(FACING)) return pos;
        BitumenPilePart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction frontDir = facing;
        Direction leftDir = facing.getCounterClockWise();

        return pos.relative(leftDir, part.dx()).relative(frontDir, part.dz()).below(part.dy());
    }

    public static @Nullable BitumenPileBlockEntity getMasterBe(Level level, BlockPos pos, BlockState state) {
        BlockPos masterPos = getMasterPos(pos, state);
        BlockEntity be = level.getBlockEntity(masterPos);
        return be instanceof BitumenPileBlockEntity master ? master : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BitumenPilePart part = state.getValue(PART);
        int stage = state.hasProperty(STAGE) ? state.getValue(STAGE) : 0;

        if (part.isBottom()) {
            return switch (stage) {
                case 0 -> Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
                case 1, 2 -> Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
                case 3 -> Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
                default -> Shapes.block();
            };
        } else {
            return switch (stage) {
                case 0, 1, 2, 3, 4 -> Shapes.empty();
                case 5 -> Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);
                case 6 -> Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
                case 7 -> Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
                default -> Block.box(0.0, 0.0, 0.0, 16.0, 15.5, 16.0);
            };
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BitumenPileBlockEntity masterBe = getMasterBe(level, pos, state);
        if (masterBe == null) return InteractionResult.PASS;

        if (masterBe.isAcceptableItem(stack)) {
            if (!level.isClientSide()) {
                ItemStack remaining = masterBe.insertItem(stack);
                if (remaining.getCount() != stack.getCount()) {
                    player.setItemInHand(hand, remaining);
                    level.playSound(null, pos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 0.9f, 0.85f + level.getRandom().nextFloat() * 0.2f);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.SUCCESS;
        }

        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BitumenPileBlockEntity masterBe = getMasterBe(level, pos, state);
            if (masterBe != null) {
                player.openMenu(masterBe);
                level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.8f, 0.95f);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BitumenPileBlockEntity masterBe = getMasterBe(level, pos, state);
        return masterBe != null ? AbstractContainerMenu.getRedstoneSignalFromBlockEntity(masterBe) : 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == BitumenPilePart.BOTTOM_FRONT_LEFT) {
            return new BitumenPileBlockEntity(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide() && state.getValue(PART) == BitumenPilePart.BOTTOM_FRONT_LEFT) {
            return (lvl, p, st, be) -> {
                if (be instanceof BitumenPileBlockEntity pileBe) {
                    pileBe.tick(lvl, p, st);
                }
            };
        }
        return null;
    }
}
