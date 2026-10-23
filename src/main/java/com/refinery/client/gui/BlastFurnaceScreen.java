package com.refinery.client.gui;

import com.refinery.menu.BlastFurnaceMenu;
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
public class BlastFurnaceScreen extends AbstractContainerScreen<BlastFurnaceMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 64;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Section Headers
        graphics.text(this.font, Component.literal("IRON").withStyle(ChatFormatting.DARK_GRAY), 20, 11, 0x444444, false);
        graphics.text(this.font, Component.literal("FUEL").withStyle(ChatFormatting.DARK_GRAY), 64, 11, 0x444444, false);
        graphics.text(this.font, Component.literal("STEEL").withStyle(ChatFormatting.DARK_GRAY), 128, 11, 0x444444, false);

        // Multiblock Status Header
        boolean assembled = this.menu.isAssembled();
        boolean smelting = this.menu.isSmelting();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("INCOMPLETE").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0x882222;
        } else if (smelting) {
            statusText = Component.literal("FORGING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 106, 6, statusColor, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Smelting Progress Bar (x: 102, y: 31, w: 20, h: 16)
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (progress > 0 && maxProgress > 0) {
            int progWidth = Math.min(20, (int) ((progress / (float) maxProgress) * 20));
            // Orange/gold smelting heat bar
            graphics.fill(RenderPipelines.GUI, xo + 102, yo + 34, xo + 102 + progWidth, yo + 38, 0xFFF59E0B);
        }

        // Active Flame Indicator (x: 107, y: 44, w: 10, h: 10)
        if (this.menu.isSmelting()) {
            graphics.fill(RenderPipelines.GUI, xo + 107, yo + 44, xo + 107 + 10, yo + 44 + 10, 0xFFEA580C);
        }

        // Multiblock Status Light (x: 94, y: 6, w: 6, h: 6)
        int statusColor = this.menu.isAssembled() ? 0xFF22C55E : 0xFFEF4444;
        graphics.fill(RenderPipelines.GUI, xo + 94, yo + 6, xo + 100, yo + 12, statusColor);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Progress Arrow Hover (x: 100..124, y: 30..42)
        if (mouseX >= xo + 100 && mouseX <= xo + 124 && mouseY >= yo + 30 && mouseY <= yo + 42) {
            int progress = this.menu.getProgress();
            int max = this.menu.getMaxProgress();
            int pct = max > 0 ? (progress * 100 / max) : 0;

            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Blast Smelting Progress").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("1 Raw Iron + 1 Coal -> 1 Steel Ingot").withStyle(ChatFormatting.WHITE),
                    Component.literal(this.menu.isSmelting() ? ("Forging: " + pct + "%") : "Status: Idle").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }

        // Status Header Hover (x: 90..160, y: 4..16)
        else if (mouseX >= xo + 90 && mouseX <= xo + 160 && mouseY >= yo + 4 && mouseY <= yo + 16) {
            boolean assembled = this.menu.isAssembled();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("5x5x5 Blast Smelter Structure").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    assembled
                            ? Component.literal("Chamber & Shell: 125/125 ONLINE").withStyle(ChatFormatting.GREEN)
                            : Component.literal("Structure: INCOMPLETE (Shift-Right-Click with Wrench)").withStyle(ChatFormatting.RED),
                    Component.literal("Front 3x3 Window: Foundry Heat Glass").withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
        }

        // Iron Slots Hover
        else if (mouseX >= xo + 19 && mouseX <= xo + 57 && mouseY >= yo + 21 && mouseY <= yo + 59) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Iron Input Queue Buffer").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Accepts: Raw Iron, Iron Ingots, Iron Ore").withStyle(ChatFormatting.GRAY),
                    Component.literal("Pipes connect to Foundry Input Port").withStyle(ChatFormatting.BLUE)
            ), mouseX, mouseY);
        }

        // Fuel Slots Hover
        else if (mouseX >= xo + 61 && mouseX <= xo + 99 && mouseY >= yo + 21 && mouseY <= yo + 59) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Fuel Input Queue Buffer").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Accepts: Coal, Charcoal, Petroleum Tar").withStyle(ChatFormatting.GRAY),
                    Component.literal("Pipes connect to Foundry Input Port").withStyle(ChatFormatting.BLUE)
            ), mouseX, mouseY);
        }

        // Steel Output Slots Hover
        else if (mouseX >= xo + 125 && mouseX <= xo + 163 && mouseY >= yo + 21 && mouseY <= yo + 59) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Steel Output Buffer").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Holds finished Steel Ingots").withStyle(ChatFormatting.WHITE),
                    Component.literal("Auto-ejects or pulls via Foundry Output Port").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }
    }
}
