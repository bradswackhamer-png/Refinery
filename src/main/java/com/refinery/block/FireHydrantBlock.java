package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.FireHydrantBlockEntity;
import com.refinery.block.entity.IndustrialPipeBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FireHydrantBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 3.6, 14.0),   // Bottom double flange & bolts
            Block.box(4.0, 3.6, 4.0, 12.0, 11.5, 12.0),  // Main cylindrical barrel
            Block.box(1.0, 7.2, 1.2, 15.0, 13.4, 14.8),  // 3-way nozzles & upper bonnet flange
            Block.box(4.2, 13.4, 4.2, 11.8, 16.0, 11.8)  // Domed bonnet & top brass stem nut
    );

    public FireHydrantBlock(Properties properties) {
        super(properties.strength(3.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(ACTIVE, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, 1);
            notifyNeighborPipes(level, pos);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            notifyNeighborPipes(level, pos);
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        notifyNeighborPipes(level, pos);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
        notifyNeighborPipes(level, pos);
        level.updateNeighborsAt(pos, this);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            notifyNeighborPipes(level, pos);
        }
    }

    private static void notifyNeighborPipes(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockEntity nBe = level.getBlockEntity(pos.relative(dir));
            if (nBe instanceof IndustrialPipeBlockEntity neighborPipe) {
                neighborPipe.updateConnections();
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
            return InteractionResult.PASS;
        }

        if (stack.is(Items.BUCKET)) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FireHydrantBlockEntity be) {
                be.tryFillBucket(player, hand, stack);
            }
            return InteractionResult.SUCCESS;
        }

        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FireHydrantBlockEntity be) {
            if (player.isShiftKeyDown() && be.getFluidAmount() > 0) {
                be.flushFluid(player);
            } else {
                be.ensureBucketVolumeAvailable();
                String fluidInfo = (be.getCurrentFluid() == Fluids.EMPTY || be.getFluidAmount() <= 0)
                        ? "§7Empty (Connect a fluid pipe to feed liquid)"
                        : String.format("§b%s §f(%d / %d mB) %s",
                                BuiltInRegistries.FLUID.getKey(be.getCurrentFluid()),
                                be.getFluidAmount(),
                                be.getTankCapacity(),
                                be.getFluidAmount() >= 1000 ? "§a[Ready to fill bucket]" : "§e[Filling...]");
                player.sendSystemMessage(Component.literal(
                        "§e§l[Fire Hydrant]§r " + fluidInfo + " §8| §7Right-click with an Empty Bucket to fill (Shift-click to flush)"
                ));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FireHydrantBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return blockEntityType == RefineryMod.FIRE_HYDRANT_BE.get()
                ? (lvl, p, st, be) -> FireHydrantBlockEntity.serverTick(lvl, p, st, (FireHydrantBlockEntity) be)
                : null;
    }
}
