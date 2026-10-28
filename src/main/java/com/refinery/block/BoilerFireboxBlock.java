package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.LargeBoilerControllerBlockEntity;
import com.refinery.block.entity.SmallBoilerControllerBlockEntity;
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

public class BoilerFireboxBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty ASSEMBLED = SmallBoilerControllerBlock.ASSEMBLED;

    public BoilerFireboxBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(ASSEMBLED, false));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(ASSEMBLED) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, ASSEMBLED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            BlockEntity controller = SteelBoilerCasingBlock.findNearbyController(level, pos);
            if (controller instanceof SmallBoilerControllerBlockEntity sbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) {
                        sbe.toggleBlueprintProjection(player);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    sbe.syncAssembly();
                    player.openMenu(sbe);
                }
                return InteractionResult.SUCCESS;
            } else if (controller instanceof LargeBoilerControllerBlockEntity lbe) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) {
                        lbe.toggleBlueprintProjection(player);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (!player.isShiftKeyDown() && !level.isClientSide()) {
                    lbe.syncAssembly();
                    player.openMenu(lbe);
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
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
