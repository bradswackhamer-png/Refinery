package com.refinery.block;

import com.refinery.block.entity.CastingPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

public class CastingPortBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public enum PortType implements StringRepresentable {
        INPUT("input"),
        OUTPUT("output");

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

    public CastingPortBlock(PortType portType, Properties properties) {
        super(properties.strength(3.0f, 9.0f)
                .sound(SoundType.NETHERITE_BLOCK));
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
        return new CastingPortBlockEntity(pos, state, this.portType);
    }

    @Override
    protected net.minecraft.world.InteractionResult useItemOn(
            net.minecraft.world.item.ItemStack stack,
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof CastingPortBlockEntity portBE) {
            var controller = portBE.getController();
            if (controller != null && this.portType == PortType.INPUT && stack.getItem() instanceof net.minecraft.world.item.BucketItem bucketItem) {
                net.minecraft.world.level.material.Fluid fluid = bucketItem.content;
                if (!com.refinery.block.entity.CastingPressControllerBlockEntity.getCastingProduct(fluid).isEmpty()) {
                    if (controller.getMoltenAmount() + 1000 <= com.refinery.block.entity.CastingPressControllerBlockEntity.TANK_CAPACITY) {
                        if (controller.getMoltenFluid() == net.minecraft.world.level.material.Fluids.EMPTY || controller.getMoltenFluid() == fluid) {
                            if (!level.isClientSide()) {
                                controller.setMoltenFluid(fluid);
                                controller.setMoltenAmount(controller.getMoltenAmount() + 1000);
                                controller.setChanged();
                                if (!player.isCreative()) {
                                    player.setItemInHand(hand, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));
                                }
                                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_LAVA, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                            }
                            return net.minecraft.world.InteractionResult.SUCCESS;
                        }
                    }
                }
            }

            if (controller != null && controller.isAssembled()) {
                if (!level.isClientSide()) {
                    player.openMenu(controller, controller.getBlockPos());
                }
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
