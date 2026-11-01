package com.refinery.client.gui;

import com.refinery.menu.MetalPressMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MetalPressScreen extends AbstractContainerScreen<MetalPressMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/metal_press.png");

    public MetalPressScreen(MetalPressMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean assembled = this.menu.isAssembled();
        boolean pressing = this.menu.isPressing();
        int energy = this.menu.getEnergy();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0xFF882222;
        } else if (energy < 20) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xFFEF4444;
        } else if (pressing) {
            statusText = Component.literal("STAMPING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0xFF228822;
        }

        graphics.text(this.font, statusText, 102, 6, statusColor, false);
        graphics.text(this.font, Component.literal("INGOTS").withStyle(ChatFormatting.DARK_GRAY), 44, 22, 0xFF444444, false);
        graphics.text(this.font, Component.literal("PLATES").withStyle(ChatFormatting.DARK_GRAY), 116, 22, 0xFF444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (x: 18, y: 18, w: 14, h: 50)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(50, (int) ((energy / 50000.0f) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 18 + (50 - energyHeight), xo + 18 + 14, yo + 18 + 50, 0xFFEAB308);
        }

        // 2. Stamping Ram Progress Indicator (x: 88, y: 28, w: 18, h: 26)
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (progress > 0 && maxProgress > 0) {
            // Down and up motion representation
            float normalized = progress <= 20 ? (progress / 20.0f) : (1.0f - ((progress - 20) / 20.0f));
            int stroke = (int) (normalized * 18);
            // Stamping hammer head moving down
            graphics.fill(RenderPipelines.GUI, xo + 89, yo + 28 + stroke, xo + 105, yo + 34 + stroke, 0xFF475569);
            graphics.fill(RenderPipelines.GUI, xo + 95, yo + 28, xo + 99, yo + 28 + stroke, 0xFF94A3B8);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 18..32, y: 18..68)
        if (mouseX >= xo + 18 && mouseX <= xo + 32 && mouseY >= yo + 18 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergy() + " / 50,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 20 FE/t while stamping").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // Press Ram Hover (x: 84..108, y: 26..56)
        if (mouseX >= xo + 84 && mouseX <= xo + 108 && mouseY >= yo + 26 && mouseY <= yo + 56) {
            int progress = this.menu.getProgress();
            int pct = progress * 100 / 40;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Hydraulic Metal Press").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("1 Steel Ingot -> 1 Heavy Steel Plate").withStyle(ChatFormatting.AQUA),
                    Component.literal(this.menu.isPressing() ? ("Cycle Progress: " + pct + "%") : "Status: Idle").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
    }
}
