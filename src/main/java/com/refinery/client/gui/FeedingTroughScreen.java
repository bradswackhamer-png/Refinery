package com.refinery.client.gui;

import com.refinery.menu.FeedingTroughMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FeedingTroughScreen extends AbstractContainerScreen<FeedingTroughMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/feeding_trough.png");

    public FeedingTroughScreen(FeedingTroughMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int foodCount = this.menu.getFoodCount();
        int animals = this.menu.getAnimalsInRange();

        Component statusText;
        int statusColor;

        if (foodCount <= 0) {
            statusText = Component.literal("EMPTY").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0xFFAA2222;
        } else if (animals > 0) {
            statusText = Component.literal("ACTIVE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0xFF15803D;
        } else {
            statusText = Component.literal("STANDBY").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFB45309;
        }

        graphics.text(this.font, statusText, 120, 6, statusColor, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI Background texture (176 x 166)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Dynamic Golden Hay Level Gauge (x: 18, y: 18, w: 10, h: 54)
        int fillPercent = this.menu.getFillPercent();
        if (fillPercent > 0) {
            int hayHeight = Math.min(54, (int) Math.ceil((fillPercent / 100.0f) * 54));
            // Fill with golden straw / hay gradient
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 18 + 54 - hayHeight, xo + 18 + 10, yo + 18 + 54, 0xFFE0B838);
        }

        // Status Card on the right (x: 122..168, y: 18..54)
        int animals = this.menu.getAnimalsInRange();
        int foodCount = this.menu.getFoodCount();

        // Border and card background
        graphics.fill(RenderPipelines.GUI, xo + 120, yo + 18, xo + 168, yo + 54, 0xFF4A4E53);
        graphics.fill(RenderPipelines.GUI, xo + 121, yo + 19, xo + 167, yo + 53, 0xFF1E2124);

        // Indicator LED
        int ledColor = (foodCount > 0) ? ((animals > 0) ? 0xFF22C55E : 0xFFEAB308) : 0xFFEF4444;
        graphics.fill(RenderPipelines.GUI, xo + 124, yo + 22, xo + 128, yo + 26, ledColor);

        // Info lines inside card
        String hayText = fillPercent + "%";
        graphics.text(this.font, Component.literal("HAY: " + hayText).withStyle(ChatFormatting.GOLD), xo + 131, yo + 21, 0xFFD4AF37, false);

        String animalText = animals + " HD";
        graphics.text(this.font, Component.literal("HERD: " + animalText).withStyle(ChatFormatting.AQUA), xo + 124, yo + 32, 0xFF55FFFF, false);

        String rangeText = "4.5m RNG";
        graphics.text(this.font, Component.literal(rangeText).withStyle(ChatFormatting.GRAY), xo + 124, yo + 42, 0xFF888888, false);
    }
}
