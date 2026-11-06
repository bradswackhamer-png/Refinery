package com.refinery.block;

import com.refinery.block.entity.ElectricRefineryFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ElectricRefineryFurnaceBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<FurnacePart> PART = EnumProperty.create("part", FurnacePart.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public ElectricRefineryFurnaceBlock(Properties properties) {
        super(properties.sound(SoundType.NETHERITE_BLOCK)
                .strength(3.0f, 8.0f)
                .noOcclusion()
                .lightLevel(state -> state.getValue(ACTIVE) ? 12 : 0));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, FurnacePart.LOWER)
                .setValue(ACTIVE, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0f;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        BlockPos upperPos = pos.above();
        Level level = context.getLevel();

        if (upperPos.getY() < level.getMaxY() && level.getBlockState(upperPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(upperPos)) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(PART, FurnacePart.LOWER)
                    .setValue(ACTIVE, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockPos upperPos = pos.above();
            level.setBlock(upperPos, state.setValue(PART, FurnacePart.UPPER), 3);
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
        FurnacePart part = state.getValue(PART);
        Direction partnerDir = (part == FurnacePart.LOWER) ? Direction.UP : Direction.DOWN;

        if (direction == partnerDir) {
            if (!neighborState.is(this) || neighborState.getValue(PART) == part) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, levelReader, scheduledTicks, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            FurnacePart part = state.getValue(PART);
            BlockPos partnerPos = (part == FurnacePart.LOWER) ? pos.above() : pos.below();
            BlockState partnerState = level.getBlockState(partnerPos);

            if (partnerState.is(this) && partnerState.getValue(PART) != part) {
                level.setBlock(partnerPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, partnerPos, Block.getId(partnerState));
            }

            // If the player destroyed the non-master upper part in survival, drop the single item
            if (!player.isCreative() && part == FurnacePart.UPPER) {
                Block.popResource(level, pos, new ItemStack(this));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(PART) == FurnacePart.LOWER) {
            return List.of(new ItemStack(this));
        }
        return List.of();
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            ElectricRefineryFurnaceBlockEntity master = getMaster(level, pos, state);
            if (master != null) {
                player.openMenu(master, master.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public static ElectricRefineryFurnaceBlockEntity getMaster(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof ElectricRefineryFurnaceBlock)) return null;
        FurnacePart part = state.getValue(PART);
        BlockPos masterPos = (part == FurnacePart.LOWER) ? pos : pos.below();
        BlockEntity be = level.getBlockEntity(masterPos);
        return (be instanceof ElectricRefineryFurnaceBlockEntity furnace) ? furnace : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricRefineryFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        if (state.getValue(PART) == FurnacePart.LOWER) {
            return (lvl, pos, st, be) -> {
                if (be instanceof ElectricRefineryFurnaceBlockEntity furnace) {
                    furnace.serverTick();
                }
            };
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, ACTIVE);
    }
}
