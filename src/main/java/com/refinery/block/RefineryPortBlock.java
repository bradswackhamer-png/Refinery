package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import com.refinery.block.entity.RefineryPortBlockEntity;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class RefineryPortBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ASSEMBLED = RefineryControllerBlock.ASSEMBLED;

    public enum PortType implements StringRepresentable {
        CRUDE_OIL_IN("crude_oil_in"),
        TAR_OUT("tar_out"),
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
                .noOcclusion());
        this.portType = portType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ASSEMBLED, false));
    }

    public PortType getPortType() {
        return this.portType;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return state.getValue(ASSEMBLED) ? Shapes.empty() : Shapes.block();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(ASSEMBLED) ? 1.0f : super.getShadeBrightness(state, level, pos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(ASSEMBLED);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(ASSEMBLED) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ASSEMBLED);
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (lvl, pos, st, be) -> {
            if (be instanceof RefineryPortBlockEntity portBE) {
                portBE.serverTick((ServerLevel) lvl, pos, st);
            }
        };
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RefineryPortBlockEntity portBE) {
            RefineryControllerBlockEntity controller = portBE.getController();
            if (controller == null && state.getValue(ASSEMBLED)) {
                controller = ReinforcedCasingBlock.findNearbyController(level, pos);
            }

            if (stack.is(RefineryMod.REFINERY_WRENCH.get()) && controller != null) {
                if (!level.isClientSide()) {
                    controller.toggleBlueprintProjection(player);
                }
                return InteractionResult.SUCCESS;
            }

            // Prevent filled fuel buckets from accidentally placing liquid source blocks when clicking output ports
            if (portType == PortType.DIESEL_OUT || portType == PortType.GAS_OUT || portType == PortType.TAR_OUT || portType == PortType.BYPRODUCT_OUT) {
                if (stack.is(RefineryFluids.DIESEL_BUCKET.get())
                        || stack.is(RefineryFluids.GASOLINE_BUCKET.get())
                        || stack.is(RefineryFluids.LIQUID_TAR_BUCKET.get())) {
                    if (!level.isClientSide()) {
                        player.sendSystemMessage(Component.literal("§eThis port is for output only. Liquid cannot be inserted here."));
                    }
                    return InteractionResult.SUCCESS;
                }
            }

            // Empty Bucket on Output Ports (Fill bucket with Liquid Tar, Diesel, or Gasoline)
            if (stack.is(Items.BUCKET)) {
                if (portType == PortType.DIESEL_OUT || portType == PortType.GAS_OUT || portType == PortType.TAR_OUT || portType == PortType.BYPRODUCT_OUT) {
                    if (level.isClientSide()) {
                        return InteractionResult.SUCCESS;
                    }
                    if (controller != null && controller.isAssembled()) {
                        if (portType == PortType.DIESEL_OUT) {
                            if (controller.getDiesel() >= 1000) {
                                controller.drainDiesel(1000);
                                ItemStack filled = new ItemStack(RefineryFluids.DIESEL_BUCKET.get());
                                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
                                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                                return InteractionResult.SUCCESS;
                            } else {
                                player.sendSystemMessage(Component.literal("§cNot enough Diesel in refinery (needs 1,000 mB, has " + controller.getDiesel() + " mB)."));
                                return InteractionResult.SUCCESS;
                            }
                        } else if (portType == PortType.GAS_OUT) {
                            if (controller.getGasoline() >= 1000) {
                                controller.drainGasoline(1000);
                                ItemStack filled = new ItemStack(RefineryFluids.GASOLINE_BUCKET.get());
                                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
                                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                                return InteractionResult.SUCCESS;
                            } else {
                                player.sendSystemMessage(Component.literal("§cNot enough Gasoline in refinery (needs 1,000 mB, has " + controller.getGasoline() + " mB)."));
                                return InteractionResult.SUCCESS;
                            }
                        } else if (portType == PortType.TAR_OUT || portType == PortType.BYPRODUCT_OUT) {
                            if (controller.getLiquidTar() >= 1000) {
                                controller.drainLiquidTar(1000);
                                ItemStack filled = new ItemStack(RefineryFluids.LIQUID_TAR_BUCKET.get());
                                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
                                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                                return InteractionResult.SUCCESS;
                            } else {
                                player.sendSystemMessage(Component.literal("§cNot enough Liquid Tar in refinery (needs 1,000 mB, has " + controller.getLiquidTar() + " mB)."));
                                return InteractionResult.SUCCESS;
                            }
                        }
                    } else {
                        player.sendSystemMessage(Component.literal("§cRefinery is unformed or controller missing."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            // Crude Oil Bucket on Input Port (Empty crude oil bucket into refinery)
            if (portType == PortType.CRUDE_OIL_IN) {
                if (stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get())) {
                    if (level.isClientSide()) {
                        return InteractionResult.SUCCESS;
                    }
                    if (controller != null && controller.isAssembled()) {
                        if (controller.getCrudeOil() + 1000 <= RefineryControllerBlockEntity.TANK_CAPACITY) {
                            controller.fillCrudeOil(1000);
                            if (!player.getAbilities().instabuild) {
                                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                            }
                            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                            return InteractionResult.SUCCESS;
                        } else {
                            player.sendSystemMessage(Component.literal("§cCrude oil tank is full (16,000 mB)."));
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        player.sendSystemMessage(Component.literal("§cRefinery is unformed or controller missing."));
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (state.getValue(ASSEMBLED) && controller != null) {
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    controller.syncAssembly();
                    player.openMenu(controller);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            RefineryControllerBlockEntity controller = ReinforcedCasingBlock.findNearbyController(level, pos);
            if (controller != null && !level.isClientSide()) {
                controller.syncAssembly();
                player.openMenu(controller);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(ASSEMBLED)) {
            RefineryControllerBlockEntity controller = ReinforcedCasingBlock.findNearbyController(level, pos);
            if (controller != null) {
                controller.disassemble();
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
