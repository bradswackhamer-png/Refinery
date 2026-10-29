package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.BoilerPortBlockEntity;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
import com.refinery.block.entity.LargeDieselBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallDieselBoilerControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BoilerPortBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ASSEMBLED = SmallBoilerControllerBlock.ASSEMBLED;

    public enum PortType implements StringRepresentable {
        WATER_IN("water_in"),
        STEAM_OUT("steam_out"),
        FUEL_IN("fuel_in");

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

    public BoilerPortBlock(PortType portType, Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
        );
        this.portType = portType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ASSEMBLED, false));
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

    public PortType getPortType() {
        return this.portType;
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

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            BlockEntity controller = SteelBoilerCasingBlock.findNearbyController(level, pos);
            if (controller instanceof SmallBoilerControllerBlockEntity sbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) sbe.toggleBlueprintProjection(player);
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    sbe.syncAssembly();
                    player.openMenu(sbe);
                }
                return InteractionResult.SUCCESS;
            } else if (controller instanceof LargeBoilerControllerBlockEntity lbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) lbe.toggleBlueprintProjection(player);
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    lbe.syncAssembly();
                    player.openMenu(lbe);
                }
                return InteractionResult.SUCCESS;
            } else if (controller instanceof SmallDieselBoilerControllerBlockEntity sdbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) sdbe.toggleBlueprintProjection(player);
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    sdbe.syncAssembly();
                    player.openMenu(sdbe);
                }
                return InteractionResult.SUCCESS;
            } else if (controller instanceof LargeDieselBoilerControllerBlockEntity ldbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) ldbe.toggleBlueprintProjection(player);
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    ldbe.syncAssembly();
                    player.openMenu(ldbe);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            BlockEntity controller = SteelBoilerCasingBlock.findNearbyController(level, pos);
            if (controller instanceof SmallBoilerControllerBlockEntity sbe && !level.isClientSide()) {
                sbe.syncAssembly();
                player.openMenu(sbe);
                return InteractionResult.SUCCESS;
            } else if (controller instanceof LargeBoilerControllerBlockEntity lbe && !level.isClientSide()) {
                lbe.syncAssembly();
                player.openMenu(lbe);
                return InteractionResult.SUCCESS;
            } else if (controller instanceof SmallDieselBoilerControllerBlockEntity sdbe && !level.isClientSide()) {
                sdbe.syncAssembly();
                player.openMenu(sdbe);
                return InteractionResult.SUCCESS;
            } else if (controller instanceof LargeDieselBoilerControllerBlockEntity ldbe && !level.isClientSide()) {
                ldbe.syncAssembly();
                player.openMenu(ldbe);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(ASSEMBLED)) {
            BlockEntity controller = SteelBoilerCasingBlock.findNearbyController(level, pos);
            if (controller instanceof SmallBoilerControllerBlockEntity sbe) {
                sbe.disassemble();
            } else if (controller instanceof LargeBoilerControllerBlockEntity lbe) {
                lbe.disassemble();
            } else if (controller instanceof SmallDieselBoilerControllerBlockEntity sdbe) {
                sdbe.disassemble();
            } else if (controller instanceof LargeDieselBoilerControllerBlockEntity ldbe) {
                ldbe.disassemble();
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoilerPortBlockEntity(pos, state);
    }
}
