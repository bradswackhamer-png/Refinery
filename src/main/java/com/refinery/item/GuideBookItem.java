package com.refinery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class GuideBookItem extends Item {
    public GuideBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            com.refinery.client.RefineryClientMod.openGuideBook();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        builder.accept(Component.literal("Comprehensive engineering manual for heavy industry.").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.literal("Right-click to open Table of Contents & Multiblock Blueprints.").withStyle(ChatFormatting.GOLD));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
