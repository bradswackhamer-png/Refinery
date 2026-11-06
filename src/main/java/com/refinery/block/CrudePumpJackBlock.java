package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.CrudePumpJackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public class CrudePumpJackBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<CrudePumpJackPart> PART = EnumProperty.create("part", CrudePumpJackPart.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public CrudePumpJackBlock(BlockBehaviour.Properties properties) {
        super(properties
                .strength(3.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, CrudePumpJackPart.BOTTOM_FRONT_LEFT)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, ACTIVE);
    }

    public static BlockPos[] getAllPositions(BlockPos masterPos, Direction facing) {
        Direction backDir = facing.getOpposite();
        Direction rightDir = facing.getClockWise();
        CrudePumpJackPart[] allParts = CrudePumpJackPart.values();
        BlockPos[] positions = new BlockPos[allParts.length];

        for (int i = 0; i < allParts.length; i++) {
            CrudePumpJackPart part = allParts[i];
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
                .setValue(PART, CrudePumpJackPart.BOTTOM_FRONT_LEFT)
                .setValue(ACTIVE, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            Direction backDir = facing.getOpposite();
            Direction rightDir = facing.getClockWise();

            for (CrudePumpJackPart part : CrudePumpJackPart.values()) {
                if (part == CrudePumpJackPart.BOTTOM_FRONT_LEFT) continue;
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
            if (!masterState.is(this) || masterState.getValue(PART) != CrudePumpJackPart.BOTTOM_FRONT_LEFT) {
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
            CrudePumpJackBlockEntity masterBe = getMasterBe(level, pos, state);

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
        if (state.getValue(PART) == CrudePumpJackPart.BOTTOM_FRONT_LEFT) {
            return List.of(new ItemStack(this));
        }
        return List.of();
    }

    public static BlockPos getMasterPos(BlockPos pos, BlockState state) {
        if (!state.hasProperty(PART) || !state.hasProperty(FACING)) return pos;
        CrudePumpJackPart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        Direction frontDir = facing;
        Direction leftDir = facing.getCounterClockWise();

        return pos.relative(leftDir, part.dx()).relative(frontDir, part.dz()).below(part.dy());
    }

    public static @Nullable CrudePumpJackBlockEntity getMasterBe(Level level, BlockPos pos, BlockState state) {
        BlockPos masterPos = getMasterPos(pos, state);
        BlockEntity be = level.getBlockEntity(masterPos);
        return be instanceof CrudePumpJackBlockEntity master ? master : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        CrudePumpJackPart part = state.getValue(PART);
        if (part.dy() == 0) {
            return Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0); // Base skid
        } else if (part.dy() == 1) {
            return Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0); // Tower framework / machinery
        } else {
            return Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 15.0); // Walking beam & horsehead
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        CrudePumpJackBlockEntity masterBe = getMasterBe(level, pos, state);
        if (masterBe == null) return InteractionResult.PASS;

        // Bucket interaction: fill bucket with crude oil if player clicks with empty bucket
        if (stack.is(Items.BUCKET) && masterBe.getCrudeOil() >= 1000) {
            if (!level.isClientSide()) {
                masterBe.drainCrudeOil(1000);
                ItemStack crudeBucket = new ItemStack(com.refinery.fluid.RefineryFluids.CRUDE_OIL_BUCKET.get());
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, crudeBucket);
                } else if (!player.getInventory().add(crudeBucket)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), crudeBucket);
                }
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            CrudePumpJackBlockEntity masterBe = getMasterBe(level, pos, state);
            if (masterBe != null) {
                int fe = masterBe.getEnergyStored();
                int maxFe = masterBe.getMaxEnergyStored();
                int crude = masterBe.getCrudeOil();
                int maxCrude = masterBe.getCrudeCapacity();
                String speedStr = masterBe.getPumpingSpeedString();

                player.sendSystemMessage(
                        Component.literal("§6§lCrude Pump Jack§r: §e" + crude + "§7/§e" + maxCrude + " mB Crude Oil §8| §a" + fe + "§7/§a" + maxFe + " FE §8| §b" + speedStr)
                );
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == CrudePumpJackPart.BOTTOM_FRONT_LEFT) {
            return new CrudePumpJackBlockEntity(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide() && state.getValue(PART) == CrudePumpJackPart.BOTTOM_FRONT_LEFT) {
            return (lvl, p, st, be) -> {
                if (be instanceof CrudePumpJackBlockEntity pumpJackBe) {
                    pumpJackBe.tick(lvl, p, st);
                }
            };
        }
        return null;
    }
}
