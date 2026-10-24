package com.refinery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MaterialItem extends Item {
    private final String tooltipKey;

    public MaterialItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    public MaterialItem(Properties properties) {
        this(properties, null);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (tooltipKey != null && !tooltipKey.isEmpty()) {
            builder.accept(Component.translatable(tooltipKey));
        }
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
