package com.refinery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public class SulfurDustItem extends Item {
    public SulfurDustItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip.refinery.sulfur_dust.desc"));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
