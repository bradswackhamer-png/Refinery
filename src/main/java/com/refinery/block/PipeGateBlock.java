package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.PipeGateBlockEntity;
import com.refinery.gate.GateTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PipeGateBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    // Thin plates hugging the attached block (3 pixels thick, 8x8 pixels face centered on the pipe arm)
    private static final VoxelShape SHAPE_WEST  = Block.box(0, 4, 4, 3, 12, 12);
    private static final VoxelShape SHAPE_EAST  = Block.box(13, 4, 4, 16, 12, 12);
    private static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 0, 12, 12, 3);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 13, 12, 12, 16);
    private static final VoxelShape SHAPE_DOWN  = Block.box(4, 0, 4, 12, 3, 12);
    private static final VoxelShape SHAPE_UP    = Block.box(4, 13, 4, 12, 16, 12);

    private final GateTier tier;
    private final boolean autarchic;

    public PipeGateBlock(GateTier tier, boolean autarchic, Properties properties) {
        super(properties);
        this.tier = tier;
        this.autarchic = autarchic;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(ACTIVE, false));
    }

    public GateTier getTier() {
        return tier;
    }

    public boolean isAutarchic() {
        return autarchic;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // Players can walk freely without snagging on gate plates
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace().getOpposite();
        Level level = context.getLevel();
        BlockPos attachedPos = context.getClickedPos().relative(facing);
        BlockState attachedState = level.getBlockState(attachedPos);
        if (!attachedState.isAir()) {
            return defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(POWERED, false)
                    .setValue(ACTIVE, false);
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        Direction attachedDirection = state.getValue(FACING);
        if (direction == attachedDirection && neighborState.isAir()) {
            return state;
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        Direction attachedDirection = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(attachedDirection);
        if (level.getBlockState(attachedPos).isAir()) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return (state.getValue(POWERED) && side == state.getValue(FACING).getOpposite()) ? 15 : 0;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // In-world upgrade using Autarchic Chipset
        if (!autarchic && stack.is(RefineryMod.AUTARCHIC_CHIPSET.get())) {
            if (!level.isClientSide()) {
                Block newBlock = switch (tier) {
                    case BASIC -> RefineryMod.BASIC_AUTARCHIC_GATE.get();
                    case IRON -> RefineryMod.IRON_AUTARCHIC_GATE.get();
                    case GOLD -> RefineryMod.GOLD_AUTARCHIC_GATE.get();
                    case DIAMOND -> RefineryMod.DIAMOND_AUTARCHIC_GATE.get();
                };

                BlockEntity oldBe = level.getBlockEntity(pos);
                BlockState newState = newBlock.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(POWERED, state.getValue(POWERED))
                        .setValue(ACTIVE, state.getValue(ACTIVE));

                level.setBlock(pos, newState, 3);
                BlockEntity newBe = level.getBlockEntity(pos);
                if (oldBe instanceof PipeGateBlockEntity oldGate && newBe instanceof PipeGateBlockEntity newGate) {
                    newGate.copyFrom(oldGate);
                    newGate.setAutarchic(true);
                }

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
                player.sendSystemMessage(Component.literal("§aGate upgraded to " + tier.getDisplayName() + " (Autarchic)!"));
            }
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PipeGateBlockEntity gateBe) {
                player.openMenu(gateBe, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE) && random.nextFloat() < 0.35f) {
            Direction facing = state.getValue(FACING);
            double x = pos.getX() + 0.5 + facing.getStepX() * -0.4;
            double y = pos.getY() + 0.5 + facing.getStepY() * -0.4;
            double z = pos.getZ() + 0.5 + facing.getStepZ() * -0.4;

            if (autarchic) {
                level.addParticle(new DustParticleOptions(0x33DDFF, 1.0f),
                        x + (random.nextDouble() - 0.5) * 0.2,
                        y + (random.nextDouble() - 0.5) * 0.2,
                        z + (random.nextDouble() - 0.5) * 0.2, 0, 0, 0);
            } else if (state.getValue(POWERED)) {
                level.addParticle(DustParticleOptions.REDSTONE,
                        x + (random.nextDouble() - 0.5) * 0.2,
                        y + (random.nextDouble() - 0.5) * 0.2,
                        z + (random.nextDouble() - 0.5) * 0.2, 0, 0, 0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeGateBlockEntity(pos, state, tier, autarchic);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, RefineryMod.PIPE_GATE_BE.get(), PipeGateBlockEntity::serverTick);
    }
}
