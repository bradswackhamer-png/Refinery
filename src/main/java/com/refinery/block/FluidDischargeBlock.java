package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.FluidDischargeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FluidDischargeBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DischargeMode> MODE = EnumProperty.create("mode", DischargeMode.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3.0, 0.0, 3.0, 13.0, 2.5, 13.0),
            Block.box(2.0, 2.5, 2.0, 14.0, 7.0, 14.0),
            Block.box(0.5, 7.0, 0.5, 15.5, 10.0, 15.5),
            Block.box(2.0, 10.0, 2.0, 14.0, 15.2, 14.0)
    );

    public FluidDischargeBlock(Properties properties) {
        super(properties.strength(3.5f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODE, DischargeMode.VOID)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(MODE, DischargeMode.VOID)
                .setValue(ACTIVE, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
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
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof FluidDischargeBlockEntity be) {
                if (player.isShiftKeyDown()) {
                    // Shift-right click: Inspect status
                    String fluidInfo = (be.getCurrentFluid() == net.minecraft.world.level.material.Fluids.EMPTY || be.getFluidAmount() <= 0)
                            ? "Empty"
                            : String.format("%s (%d / %d mB)", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(be.getCurrentFluid()), be.getFluidAmount(), be.getTankCapacity());
                    player.sendSystemMessage(Component.literal(String.format(
                            "§6[Refinery] Fluid Discharge Status:\n §7Mode: §f%s\n §7Buffer: §b%s\n §7Active: §a%s\n §7Redstone Signal: §c%s",
                            be.getMode().getDisplayName(),
                            fluidInfo,
                            state.getValue(ACTIVE) ? "Yes" : "Idle",
                            level.hasNeighborSignal(pos) ? "Paused" : "Enabled"
                    )));
                } else {
                    // Right click: Toggle mode
                    DischargeMode nextMode = be.getMode().cycle();
                    be.setMode(nextMode);
                    level.setBlock(pos, state.setValue(MODE, nextMode), Block.UPDATE_ALL);

                    if (nextMode == DischargeMode.VOID) {
                        player.sendSystemMessage(Component.literal(
                                "§e[Refinery] Fluid Discharge set to: §c§lVOID §7(Dumps incoming pipe fluids into oblivion)"
                        ));
                        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 1.8f);
                    } else {
                        player.sendSystemMessage(Component.literal(String.format(
                                "§e[Refinery] Fluid Discharge set to: §b§lFILL §7(Fills contiguous blocks at Y=%d)",
                                pos.getY()
                        )));
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6f, 1.2f);
                    }
                    level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, 1.4f);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FluidDischargeBlockEntity be) {
            be.setRedstonePaused(level.hasNeighborSignal(pos));
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidDischargeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return blockEntityType == RefineryMod.FLUID_DISCHARGE_BE.get()
                ? (lvl, p, st, be) -> ((FluidDischargeBlockEntity) be).serverTick()
                : null;
    }
}
