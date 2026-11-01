package com.refinery.client.gui;

import com.refinery.menu.IndustrialGrinderMenu;
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
public class IndustrialGrinderScreen extends AbstractContainerScreen<IndustrialGrinderMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/industrial_grinder.png");

    public IndustrialGrinderScreen(IndustrialGrinderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean assembled = this.menu.isAssembled();
        boolean grinding = this.menu.isGrinding();
        int energy = this.menu.getEnergy();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0xFF882222;
        } else if (energy < 30) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xFFEF4444;
        } else if (grinding) {
            statusText = Component.literal("GRINDING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0xFF228822;
        }

        graphics.text(this.font, statusText, 102, 6, statusColor, false);
        graphics.text(this.font, Component.literal("INPUT").withStyle(ChatFormatting.DARK_GRAY), 38, 14, 0xFF444444, false);
        graphics.text(this.font, Component.literal("OUTPUT").withStyle(ChatFormatting.DARK_GRAY), 116, 14, 0xFF444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (x: 14, y: 18, w: 14, h: 50)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(50, (int) ((energy / 50000.0f) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 18 + (50 - energyHeight), xo + 14 + 14, yo + 18 + 50, 0xFFEAB308);
        }

        // 2. Grinding Process Progress Bar (x: 78, y: 34, w: 32, h: 16)
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (progress > 0 && maxProgress > 0) {
            int fillWidth = Math.min(32, (int) (((float) progress / maxProgress) * 32));
            // Progress bar fill (orange/red grinding teeth effect)
            graphics.fill(RenderPipelines.GUI, xo + 78, yo + 34, xo + 78 + fillWidth, yo + 50, 0xFFD97706);
            // Highlight bar on top edge
            graphics.fill(RenderPipelines.GUI, xo + 78, yo + 34, xo + 78 + fillWidth, yo + 36, 0xFFFBBF24);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 14..28, y: 18..68)
        if (mouseX >= xo + 14 && mouseX <= xo + 28 && mouseY >= yo + 18 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergy() + " / 50,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 30 FE/t while grinding").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // Grinder Progress Hover (x: 76..112, y: 30..54)
        if (mouseX >= xo + 76 && mouseX <= xo + 112 && mouseY >= yo + 30 && mouseY <= yo + 54) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            int pct = maxProgress > 0 ? (progress * 100 / maxProgress) : 0;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Industrial Grinder & Pulverizer").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Raw Ores -> 2x Dust (Ore Doubling)").withStyle(ChatFormatting.GOLD),
                    Component.literal("Stone / Cobble -> Gravel -> Sand").withStyle(ChatFormatting.AQUA),
                    Component.literal("Oil Sand -> 3 Bitumen (+50% Tar)").withStyle(ChatFormatting.DARK_GREEN),
                    Component.literal(this.menu.isGrinding() ? ("Grinding Progress: " + pct + "%") : "Status: Idle").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
    }
}
