package com.refinery.block.pipe;

import com.refinery.RefineryMod;
import com.refinery.block.entity.BufferPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BufferPipeBlock extends Block implements EntityBlock {

    private static final VoxelShape CORE_SHAPE = Block.box(4.8, 4.8, 4.8, 11.2, 11.2, 11.2);
    private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);

    static {
        ARM_SHAPES.put(Direction.DOWN,  Block.box(4.8, 0.0,  4.8, 11.2, 4.8,  11.2));
        ARM_SHAPES.put(Direction.UP,    Block.box(4.8, 11.2, 4.8, 11.2, 16.0, 11.2));
        ARM_SHAPES.put(Direction.NORTH, Block.box(4.8, 4.8,  0.0, 11.2, 11.2, 4.8));
        ARM_SHAPES.put(Direction.SOUTH, Block.box(4.8, 4.8,  11.2, 11.2, 11.2, 16.0));
        ARM_SHAPES.put(Direction.WEST,  Block.box(0.0, 4.8,  4.8, 4.8,  11.2, 11.2));
        ARM_SHAPES.put(Direction.EAST,  Block.box(11.2, 4.8, 4.8, 16.0, 11.2, 11.2));
    }

    public BufferPipeBlock(Properties properties) {
        super(properties
                .strength(1.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BufferPipeBlockEntity pipeBe) {
            VoxelShape shape = CORE_SHAPE;
            boolean anyConnected = false;
            for (Direction dir : Direction.values()) {
                if (pipeBe.getConnection(dir).isConnected()) {
                    shape = Shapes.or(shape, ARM_SHAPES.get(dir));
                    anyConnected = true;
                }
            }
            if (!anyConnected) {
                shape = Block.box(4.8, 4.8, 3.2, 11.2, 11.2, 12.8);
            }
            for (Direction dir : Direction.values()) {
                if (pipeBe.getFacade(dir) != null) {
                    shape = Shapes.or(shape, IndustrialPipeBlock.FACADE_SHAPES.get(dir));
                }
            }
            return shape;
        }
        return CORE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                pipeBe.updateConnections();
            }
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
        if (levelReader instanceof Level level && !level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(currentPos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                pipeBe.updateConnections();
            }
        }
        return super.updateShape(state, levelReader, scheduledTicks, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                pipeBe.updateConnections();
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof com.refinery.item.PipeGateItem || stack.getItem() instanceof com.refinery.item.PipeWireItem) {
            return InteractionResult.PASS;
        }
        if (stack.getItem() instanceof com.refinery.item.PipeFacadeItem facadeItem) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                Direction targetSide = PipeFacadeHelper.resolveTargetFace(pos, hitResult);
                Block facadeBlock = PipeFacadeHelper.getFacadeBlock(stack);
                PipeFacadeHelper.FacadeData newData = new PipeFacadeHelper.FacadeData(facadeBlock, facadeItem.isHollow());
                PipeFacadeHelper.FacadeData existing = pipeBe.getFacade(targetSide);
                if (existing != null && existing.block() == newData.block() && existing.hollow() == newData.hollow()) {
                    return InteractionResult.PASS;
                }
                if (!level.isClientSide()) {
                    if (existing != null) {
                        ItemStack oldStack = existing.toItemStack(1);
                        if (!player.getInventory().add(oldStack)) {
                            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, oldStack);
                        }
                    }
                    pipeBe.setFacade(targetSide, newData);
                    SoundType soundType = facadeBlock.defaultBlockState().getSoundType(level, pos, player);
                    level.playSound(null, pos, soundType.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                Direction targetSide = PipeFacadeHelper.resolveTargetFace(pos, hitResult);
                PipeFacadeHelper.FacadeData existingFacade = pipeBe.getFacade(targetSide);
                if (existingFacade != null && player.isShiftKeyDown()) {
                    if (!level.isClientSide()) {
                        pipeBe.setFacade(targetSide, null);
                        ItemStack drop = existingFacade.toItemStack(1);
                        if (!player.getInventory().add(drop)) {
                            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                        }
                        SoundType soundType = existingFacade.block().defaultBlockState().getSoundType(level, pos, player);
                        level.playSound(null, pos, soundType.getBreakSound(), net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (!level.isClientSide()) {
                    pipeBe.toggleSideConnection(hitResult.getDirection());
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BufferPipeBlockEntity pipeBe) {
                    player.openMenu(pipeBe, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                Direction targetSide = PipeFacadeHelper.resolveTargetFace(pos, hitResult);
                PipeFacadeHelper.FacadeData existingFacade = pipeBe.getFacade(targetSide);
                if (existingFacade != null) {
                    if (!level.isClientSide()) {
                        pipeBe.setFacade(targetSide, null);
                        ItemStack drop = existingFacade.toItemStack(1);
                        if (!player.getInventory().add(drop)) {
                            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                        }
                        SoundType soundType = existingFacade.block().defaultBlockState().getSoundType(level, pos, player);
                        level.playSound(null, pos, soundType.getBreakSound(), net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BufferPipeBlockEntity pipeBe) {
                    player.openMenu(pipeBe, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                pipeBe.dropAllFacades();
                Containers.dropContents(level, pos, pipeBe);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level l && !l.isClientSide()) {
            BlockEntity be = l.getBlockEntity(pos);
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                pipeBe.dropAllFacades();
                Containers.dropContents(l, pos, pipeBe);
                l.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.destroy(level, pos, state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BufferPipeBlockEntity pipeBe) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(pipeBe);
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BufferPipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof BufferPipeBlockEntity pipeBe) {
                BufferPipeBlockEntity.tick(lvl, pos, st, pipeBe);
            }
        };
    }
}
