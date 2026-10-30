package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.SteamTurbineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurbineRotorBlock extends Block {
    public static final BooleanProperty ASSEMBLED = SteamTurbineControllerBlock.ASSEMBLED;

    public TurbineRotorBlock(Properties properties) {
        super(properties.strength(2.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(ASSEMBLED, false));
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
        builder.add(ASSEMBLED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED)) {
            SteamTurbineControllerBlockEntity controller = TurbineCasingBlock.findNearbyController(level, pos);
            if (controller != null) {
                if (stack.is(RefineryMod.REFINERY_WRENCH.get())) {
                    if (!level.isClientSide()) {
                        controller.toggleBlueprintProjection(player);
                    }
                    return InteractionResult.SUCCESS;
                }
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
            SteamTurbineControllerBlockEntity controller = TurbineCasingBlock.findNearbyController(level, pos);
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
            SteamTurbineControllerBlockEntity controller = TurbineCasingBlock.findNearbyController(level, pos);
            if (controller != null) {
                controller.disassemble();
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
