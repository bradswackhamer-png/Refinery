package com.refinery.item;

import com.refinery.block.RefineryControllerBlock;
import com.refinery.block.entity.RefineryControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class RefineryWrenchItem extends Item {

    public RefineryWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof RefineryControllerBlock) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof RefineryControllerBlockEntity controllerBE) {
                    controllerBE.toggleBlueprintProjection(player);
                    level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Rotate horizontal directional blocks
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            if (!level.isClientSide()) {
                var currentFacing = state.getValue(HorizontalDirectionalBlock.FACING);
                var newFacing = currentFacing.getClockWise();
                level.setBlock(pos, state.setValue(HorizontalDirectionalBlock.FACING, newFacing), 3);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.4f, 1.4f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_1"));
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_2"));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
