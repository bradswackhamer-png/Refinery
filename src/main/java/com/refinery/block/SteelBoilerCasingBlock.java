package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.IBoilerController;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
import com.refinery.block.entity.LargeDieselBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallDieselBoilerControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SteelBoilerCasingBlock extends Block {
    public static final BooleanProperty ASSEMBLED = SmallBoilerControllerBlock.ASSEMBLED;

    public SteelBoilerCasingBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(ASSEMBLED, false));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(ASSEMBLED) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ASSEMBLED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            BlockEntity controller = findNearbyController(level, pos);
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
            BlockEntity controller = findNearbyController(level, pos);
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
            BlockEntity controller = findNearbyController(level, pos);
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

    public static BlockEntity findNearbyController(Level level, BlockPos pos) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockEntity be = level.getBlockEntity(pos.offset(dx, dy, dz));
                    if (be instanceof SmallBoilerControllerBlockEntity sbe && sbe.isAssembled()) {
                        return sbe;
                    }
                    if (be instanceof LargeBoilerControllerBlockEntity lbe && lbe.isAssembled()) {
                        return lbe;
                    }
                    if (be instanceof SmallDieselBoilerControllerBlockEntity sdbe && sdbe.isAssembled()) {
                        return sdbe;
                    }
                    if (be instanceof LargeDieselBoilerControllerBlockEntity ldbe && ldbe.isAssembled()) {
                        return ldbe;
                    }
                }
            }
        }
        return null;
    }
}
