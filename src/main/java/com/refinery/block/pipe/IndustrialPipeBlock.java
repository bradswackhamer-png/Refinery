package com.refinery.block.pipe;

import com.refinery.RefineryMod;
import com.refinery.block.entity.IndustrialPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

public class IndustrialPipeBlock extends Block implements EntityBlock {

    private final IndustrialPipeType pipeType;
    private final IndustrialPipeMaterial material;

    // VoxelShapes centered around [8, 8, 8] with radius 3.2 units (4.8 to 11.2)
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

    public IndustrialPipeBlock(Properties properties) {
        this(properties, IndustrialPipeType.FLUID, IndustrialPipeMaterial.STEEL);
    }

    public IndustrialPipeBlock(Properties properties, IndustrialPipeType pipeType, IndustrialPipeMaterial material) {
        super(properties
                .strength(1.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
        this.pipeType = pipeType;
        this.material = material;
    }

    public IndustrialPipeType getPipeType() {
        return pipeType;
    }

    public IndustrialPipeMaterial getMaterial() {
        return material;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        // Rendered seamlessly via IndustrialPipeRenderer
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IndustrialPipeBlockEntity pipeBe) {
            VoxelShape shape = CORE_SHAPE;
            for (Direction dir : Direction.values()) {
                if (pipeBe.getConnection(dir).isConnected()) {
                    shape = Shapes.or(shape, ARM_SHAPES.get(dir));
                }
            }
            return shape;
        }
        return CORE_SHAPE;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                pipeBe.updateConnections();
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IndustrialPipeBlockEntity pipeBe) {
            pipeBe.updateConnections();
            for (Direction dir : Direction.values()) {
                BlockEntity nBe = level.getBlockEntity(pos.relative(dir));
                if (nBe instanceof IndustrialPipeBlockEntity neighborPipe) {
                    neighborPipe.updateConnections();
                }
            }
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity nBe = level.getBlockEntity(neighborPos);
            if (nBe instanceof IndustrialPipeBlockEntity neighborPipe) {
                neighborPipe.updateConnections();
            }
        }
        level.updateNeighborsAt(pos, this);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof com.refinery.item.PipeGateItem || stack.getItem() instanceof com.refinery.item.PipeWireItem) {
            return InteractionResult.PASS;
        }
        if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                    Direction clickedSide = hitResult.getDirection();
                    if (this.material == IndustrialPipeMaterial.QUARTZ && !player.isShiftKeyDown()) {
                        pipeBe.cycleOrSetMergeOutput(clickedSide, player);
                    } else {
                        pipeBe.toggleSideConnection(clickedSide);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.material == IndustrialPipeMaterial.QUARTZ) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                    Direction clickedSide = hitResult.getDirection();
                    pipeBe.cycleOrSetMergeOutput(clickedSide, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.material == IndustrialPipeMaterial.DIAMOND && !player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                    player.openMenu(pipeBe, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.material == IndustrialPipeMaterial.QUARTZ) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                    Direction clickedSide = hitResult.getDirection();
                    pipeBe.cycleOrSetMergeOutput(clickedSide, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.material == IndustrialPipeMaterial.DIAMOND && !player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                    player.openMenu(pipeBe, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                if (this.material == IndustrialPipeMaterial.QUARTZ && placer != null) {
                    pipeBe.setMergeOutput(placer.getDirection());
                }
                pipeBe.updateConnections();
                for (Direction dir : Direction.values()) {
                    BlockPos neighborPos = pos.relative(dir);
                    BlockEntity nBe = level.getBlockEntity(neighborPos);
                    if (nBe instanceof IndustrialPipeBlockEntity neighborPipe) {
                        neighborPipe.updateConnections();
                    }
                }
            }
            level.updateNeighborsAt(pos, this);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialPipeBlockEntity(pos, state, pipeType, material);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (lvl, pos, st, be) -> {
            if (be instanceof IndustrialPipeBlockEntity pipeBe) {
                IndustrialPipeBlockEntity.tick(lvl, pos, st, pipeBe);
            }
        };
    }
}
