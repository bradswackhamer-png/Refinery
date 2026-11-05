package com.refinery.item;

import com.refinery.block.pipe.PipeFacadeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class PipeFacadeItem extends Item {
    private final boolean hollow;

    public PipeFacadeItem(boolean hollow, Properties properties) {
        super(properties);
        this.hollow = hollow;
    }

    public boolean isHollow() {
        return hollow;
    }

    @Override
    public Component getName(ItemStack stack) {
        Block block = PipeFacadeHelper.getFacadeBlock(stack);
        Component blockName = block.getName();
        String baseKey = this.hollow ? "item.refinery.pipe_facade_hole" : "item.refinery.pipe_facade";
        return Component.translatable(baseKey)
                .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                .append(blockName.copy().withStyle(ChatFormatting.WHITE))
                .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        Block block = PipeFacadeHelper.getFacadeBlock(stack);
        builder.accept(Component.literal("§6Material: §f").append(block.getName()));
        if (this.hollow) {
            builder.accept(Component.literal("§bStyle: §fPass-Through Frame §7(Center Pipe Opening)"));
            builder.accept(Component.literal("§7Covers a pipe face while allowing connected"));
            builder.accept(Component.literal("§7pipe arms to pass cleanly through the center hole."));
        } else {
            builder.accept(Component.literal("§bStyle: §fSolid Panel §7(Full Face Cover)"));
            builder.accept(Component.literal("§7Conceals pipes inside walls, floors, and ceilings."));
        }
        builder.accept(Component.literal("§8Right-Click a pipe face to attach."));
        builder.accept(Component.literal("§8Shift + Right-Click with Wrench to remove."));
        builder.accept(Component.literal("§8Craft 1 facade alone to toggle Solid <-> Hollow."));
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
    }
}
