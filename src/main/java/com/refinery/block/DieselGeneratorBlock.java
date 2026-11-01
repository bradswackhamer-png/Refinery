package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.DieselGeneratorBlockEntity;
import com.refinery.fluid.RefineryFluids;
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
import net.minecraft.world.item.Items;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class DieselGeneratorBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<GeneratorPart> PART = EnumProperty.create("part", GeneratorPart.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public DieselGeneratorBlock(Properties properties) {
        super(properties.sound(SoundType.METAL).strength(3.5f, 8.0f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, GeneratorPart.LEFT)
                .setValue(ACTIVE, false));
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
        Level level = context.getLevel();

        Direction rightDir = facing.getClockWise();
        BlockPos rightPos = pos.relative(rightDir);

        if (level.getBlockState(rightPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(rightPos)) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(PART, GeneratorPart.LEFT)
                    .setValue(ACTIVE, false);
        }

        // Alternative: place clicked pos as RIGHT if left pos is free
        Direction leftDir = facing.getCounterClockWise();
        BlockPos leftPos = pos.relative(leftDir);
        if (level.getBlockState(leftPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(leftPos)) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(PART, GeneratorPart.RIGHT)
                    .setValue(ACTIVE, false);
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            GeneratorPart part = state.getValue(PART);

            if (part == GeneratorPart.LEFT) {
                BlockPos rightPos = pos.relative(facing.getClockWise());
                level.setBlock(rightPos, state.setValue(PART, GeneratorPart.RIGHT), 3);
            } else {
                BlockPos leftPos = pos.relative(facing.getCounterClockWise());
                level.setBlock(leftPos, state.setValue(PART, GeneratorPart.LEFT), 3);
            }
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
        GeneratorPart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction partnerDir = (part == GeneratorPart.LEFT) ? facing.getClockWise() : facing.getCounterClockWise();

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
            GeneratorPart part = state.getValue(PART);
            Direction facing = state.getValue(FACING);
            BlockPos partnerPos = (part == GeneratorPart.LEFT) ? pos.relative(facing.getClockWise()) : pos.relative(facing.getCounterClockWise());
            BlockState partnerState = level.getBlockState(partnerPos);

            if (partnerState.is(this) && partnerState.getValue(PART) != part) {
                if (player.isCreative() && part == GeneratorPart.RIGHT) {
                    level.setBlock(partnerPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, partnerPos, Block.getId(partnerState));
                }
            }

            DieselGeneratorBlockEntity master = getMaster(level, pos, state);
            if (master != null && part == GeneratorPart.LEFT) {
                Containers.dropContents(level, pos, master.getInventory());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        DieselGeneratorBlockEntity master = getMaster(level, pos, state);
        if (master == null) {
            return InteractionResult.PASS;
        }

        // Direct Bucket Interactions (Fill Diesel)
        if (stack.is(RefineryFluids.DIESEL_BUCKET.get())) {
            if (!level.isClientSide()) {
                if (master.fillDiesel(1000, false) == 1000) {
                    master.fillDiesel(1000, true);
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            player.openMenu(master, master.getBlockPos());
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public static DieselGeneratorBlockEntity getMaster(Level level, BlockPos pos, BlockState state) {
        if (!state.is(RefineryMod.DIESEL_GENERATOR.get())) return null;
        GeneratorPart part = state.getValue(PART);
        BlockPos masterPos = (part == GeneratorPart.LEFT) ? pos : pos.relative(state.getValue(FACING).getCounterClockWise());
        BlockEntity be = level.getBlockEntity(masterPos);
        return (be instanceof DieselGeneratorBlockEntity gen) ? gen : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DieselGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        // Only tick the master (LEFT) part
        if (state.getValue(PART) == GeneratorPart.LEFT) {
            return (lvl, pos, st, be) -> {
                if (be instanceof DieselGeneratorBlockEntity generator) {
                    generator.serverTick();
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
