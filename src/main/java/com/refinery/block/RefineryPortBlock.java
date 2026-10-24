package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import com.refinery.block.entity.RefineryPortBlockEntity;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class RefineryPortBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public enum PortType implements StringRepresentable {
        CRUDE_OIL_IN("crude_oil_in"),
        DIESEL_OUT("diesel_out"),
        GAS_OUT("gas_out"),
        BYPRODUCT_OUT("byproduct_out");

        private final String name;

        PortType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private final PortType portType;

    public RefineryPortBlock(PortType portType, Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                );
        this.portType = portType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public PortType getPortType() {
        return this.portType;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RefineryPortBlockEntity(pos, state, this.portType);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RefineryPortBlockEntity portBE) {
            RefineryControllerBlockEntity controller = portBE.getController();
            if (controller != null && controller.isAssembled()) {
                if (portType == PortType.CRUDE_OIL_IN) {
                    if (stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get())) {
                        if (controller.getCrudeOil() + 1000 <= RefineryControllerBlockEntity.TANK_CAPACITY) {
                            if (!level.isClientSide()) {
                                controller.fillCrudeOil(1000);
                                if (!player.getAbilities().instabuild) {
                                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                                }
                                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else if (portType == PortType.DIESEL_OUT) {
                    if (stack.is(Items.BUCKET) && controller.getDiesel() >= 1000) {
                        if (!level.isClientSide()) {
                            controller.drainDiesel(1000);
                            ItemStack filled = new ItemStack(RefineryFluids.DIESEL_BUCKET.get());
                            player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(stack, player, filled));
                            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        }
                        return InteractionResult.SUCCESS;
                    }
                } else if (portType == PortType.GAS_OUT) {
                    if (stack.is(Items.BUCKET) && controller.getGasoline() >= 1000) {
                        if (!level.isClientSide()) {
                            controller.drainGasoline(1000);
                            ItemStack filled = new ItemStack(RefineryFluids.GASOLINE_BUCKET.get());
                            player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(stack, player, filled));
                            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
