package com.refinery.client.gui;

import com.refinery.menu.AutoPlanterMenu;
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
public class AutoPlanterScreen extends AbstractContainerScreen<AutoPlanterMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/auto_planter.png");

    public AutoPlanterScreen(AutoPlanterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int energy = this.menu.getEnergy();
        boolean working = this.menu.isWorking();
        int seeds = this.menu.getSeedCount();

        Component statusText;
        int statusColor;

        if (energy < 2) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xAA2222;
        } else if (working) {
            statusText = Component.literal("PLANTING").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            statusColor = 0x22AA22;
        } else if (seeds == 0) {
            statusText = Component.literal("NO SEEDS").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xCC8800;
        } else {
            statusText = Component.literal("SCANNING").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            statusColor = 0xCCCC22;
        }

        graphics.text(this.font, statusText, 120, 6, statusColor, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI Background texture
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Energy Bar (x: 18, y: 18, w: 10, h: 54)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(54, (int) ((energy / 10000.0f) * 54));
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 18 + 54 - energyHeight, xo + 18 + 10, yo + 18 + 54, 0xFFEF4444);
        }

        // Status LED light (x: 154, y: 38, w: 10, h: 10)
        int ledColor;
        if (energy < 2) {
            ledColor = 0xFFEF4444; // Red
        } else if (this.menu.isWorking()) {
            ledColor = 0xFF22C55E; // Green
        } else if (this.menu.getSeedCount() == 0) {
            ledColor = 0xFFF59E0B; // Orange
        } else {
            ledColor = 0xFFEAB308; // Yellow
        }
        graphics.fill(RenderPipelines.GUI, xo + 144, yo + 38, xo + 144 + 8, yo + 38 + 8, ledColor);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 17..29, y: 17..73)
        if (mouseX >= xo + 17 && mouseX <= xo + 29 && mouseY >= yo + 17 && mouseY <= yo + 73) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Buffer (FE / RF)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 10,000 FE", this.menu.getEnergy())).withStyle(ChatFormatting.WHITE),
                    Component.literal("Usage: 1-2 FE/t (10 FE per till/plant)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Status LED Hover (x: 140..156, y: 34..50)
        else if (mouseX >= xo + 140 && mouseX <= xo + 156 && mouseY >= yo + 34 && mouseY <= yo + 50) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Planter Status").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Active: " + (this.menu.isWorking() ? "Tilling & Planting" : "Idle / Scanning")).withStyle(ChatFormatting.GREEN),
                    Component.literal("Seeds Available: " + this.menu.getSeedCount()).withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }

        // Header Status Hover (x: 110..170, y: 4..16)
        else if (mouseX >= xo + 110 && mouseX <= xo + 170 && mouseY >= yo + 4 && mouseY <= yo + 16) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Work Coverage").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Horizontal Radius: 5 blocks (11x11)").withStyle(ChatFormatting.WHITE),
                    Component.literal("Vertical Range: Up to 2 tiles above").withStyle(ChatFormatting.GRAY),
                    Component.literal("Auto-tills dirt and plants seeds in inventory").withStyle(ChatFormatting.DARK_GREEN)
            ), mouseX, mouseY);
        }
    }
}
