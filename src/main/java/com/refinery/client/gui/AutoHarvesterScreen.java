package com.refinery.client.gui;

import com.refinery.menu.AutoHarvesterMenu;
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
public class AutoHarvesterScreen extends AbstractContainerScreen<AutoHarvesterMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/auto_harvester.png");

    public AutoHarvesterScreen(AutoHarvesterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int energy = this.menu.getEnergy();
        boolean working = this.menu.isWorking();

        Component statusText;
        int statusColor;

        if (energy < 1) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xAA2222;
        } else if (working) {
            statusText = Component.literal("HARVESTING").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            statusColor = 0x22AA22;
        } else {
            statusText = Component.literal("SCANNING").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            statusColor = 0xCCCC22;
        }

        graphics.text(this.font, statusText, 110, 6, statusColor, false);
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

        // Status LED light (x: 144, y: 38, w: 8, h: 8)
        int ledColor;
        if (energy < 1) {
            ledColor = 0xFFEF4444; // Red
        } else if (this.menu.isWorking()) {
            ledColor = 0xFF22C55E; // Green
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
                    Component.literal("Usage: 1 FE/t idle, 20 FE per harvest").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Status LED Hover (x: 140..156, y: 34..50)
        else if (mouseX >= xo + 140 && mouseX <= xo + 156 && mouseY >= yo + 34 && mouseY <= yo + 50) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Harvester Status").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Mode: " + (this.menu.isWorking() ? "Active Harvesting" : "Idle / Scanning")).withStyle(ChatFormatting.GREEN),
                    Component.literal("Buffer Contents: " + this.menu.getItemCount() + " items").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }

        // Header Status Hover (x: 100..170, y: 4..16)
        else if (mouseX >= xo + 100 && mouseX <= xo + 170 && mouseY >= yo + 4 && mouseY <= yo + 16) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Work Coverage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Forward Area: 11x11 (5 Left, 5 Right, 11 Forward)").withStyle(ChatFormatting.WHITE),
                    Component.literal("Auto-harvests mature crops and replants").withStyle(ChatFormatting.GRAY),
                    Component.literal("Auto-ejects buffer into chests/pipes behind or below!").withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
        }
    }
}
