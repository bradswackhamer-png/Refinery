package com.refinery.item;

import com.refinery.RefineryMod;
import com.refinery.block.*;
import com.refinery.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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

        // Shift + Right-Click: Instant dismantle & pickup for any Refinery block
        if (player != null && player.isShiftKeyDown()) {
            var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (key != null && key.getNamespace().equals(RefineryMod.MODID)) {
                if (!level.isClientSide()) {
                    level.destroyBlock(pos, true, player);
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.3f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Multiblock Controllers: Toggle 3D Blueprint Projection
        if (state.getBlock() instanceof RefineryControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RefineryControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof BlastFurnaceControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BlastFurnaceControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof SmallBoilerControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SmallBoilerControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof LargeBoilerControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LargeBoilerControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof SteamTurbineControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SteamTurbineControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof MetalPressControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MetalPressControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof IndustrialGrinderControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IndustrialGrinderControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
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
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_3"));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
