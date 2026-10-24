package com.refinery.client.gui;

import com.refinery.menu.FermenterMenu;
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
public class FermenterScreen extends AbstractContainerScreen<FermenterMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/industrial_grinder.png");

    public FermenterScreen(FermenterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean fermenting = this.menu.isFermenting();
        int energy = this.menu.getEnergy();
        int ethanol = this.menu.getEthanolAmount();
        int capacity = this.menu.getTankCapacity();

        Component statusText;
        int statusColor;

        if (energy < 15) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xEF4444;
        } else if (ethanol >= capacity) {
            statusText = Component.literal("TANK FULL").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xF59E0B;
        } else if (fermenting) {
            statusText = Component.literal("FERMENTING").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            statusColor = 0x22C55E;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 96, 6, statusColor, false);
        graphics.text(this.font, Component.literal("CROPS").withStyle(ChatFormatting.DARK_GRAY), 42, 22, 0x555555, false);
        graphics.text(this.font, Component.literal("SEEDS").withStyle(ChatFormatting.DARK_GRAY), 78, 44, 0x555555, false);
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
            int energyHeight = Math.min(50, (int) ((energy / 20000.0f) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 18 + (50 - energyHeight), xo + 28, yo + 68, 0xFF76FF03);
        }

        // 2. Custom Slots Frames:
        // Input Slot Frame (x: 43, y: 34, w: 18, h: 18)
        graphics.fill(RenderPipelines.GUI, xo + 43, yo + 34, xo + 61, yo + 52, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 44, yo + 35, xo + 60, yo + 51, 0xFF1F2937);

        // Seed Byproduct Slot Frame (x: 79, y: 54, w: 18, h: 18)
        graphics.fill(RenderPipelines.GUI, xo + 79, yo + 54, xo + 97, yo + 72, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 80, yo + 55, xo + 96, yo + 71, 0xFF1F2937);

        // Bucket In Slot Frame (x: 121, y: 19, w: 18, h: 18)
        graphics.fill(RenderPipelines.GUI, xo + 121, yo + 19, xo + 139, yo + 37, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 122, yo + 20, xo + 138, yo + 36, 0xFF1F2937);

        // Bucket Out Slot Frame (x: 121, y: 51, w: 18, h: 18)
        graphics.fill(RenderPipelines.GUI, xo + 121, yo + 51, xo + 139, yo + 69, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 122, yo + 52, xo + 138, yo + 68, 0xFF1F2937);

        // 3. Fermentation Progress Bar (x: 64, y: 38, w: 28, h: 8)
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (progress > 0 && maxProgress > 0) {
            int fillW = Math.min(28, (int) (((float) progress / maxProgress) * 28));
            graphics.fill(RenderPipelines.GUI, xo + 64, yo + 38, xo + 64 + fillW, yo + 46, 0xFFCDDC39);
        }

        // 4. Ethanol Fluid Tank (x: 146, y: 18, w: 16, h: 50)
        // Tank Background
        graphics.fill(RenderPipelines.GUI, xo + 145, yo + 17, xo + 163, yo + 69, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 146, yo + 18, xo + 162, yo + 68, 0xFF111827);

        int ethanol = this.menu.getEthanolAmount();
        int capacity = this.menu.getTankCapacity();
        if (ethanol > 0 && capacity > 0) {
            int fluidH = Math.min(50, (int) (((float) ethanol / capacity) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 146, yo + 18 + (50 - fluidH), xo + 162, yo + 68, 0xFFDCE775);
            // Highlight shine
            graphics.fill(RenderPipelines.GUI, xo + 147, yo + 18 + (50 - fluidH), xo + 149, yo + 68, 0xFFF0F4C3);
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
                    Component.literal("Energy Buffer").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 20,000 FE", this.menu.getEnergy())).withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumes: 15 FE/t while fermenting").withStyle(ChatFormatting.DARK_GREEN)
            ), mouseX, mouseY);
        }

        // Ethanol Tank Hover (x: 145..163, y: 17..69)
        if (mouseX >= xo + 145 && mouseX <= xo + 163 && mouseY >= yo + 17 && mouseY <= yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Ethanol Biofuel Tank").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / %,d mB", this.menu.getEthanolAmount(), this.menu.getTankCapacity())).withStyle(ChatFormatting.WHITE),
                    Component.literal("Pure bio-distillate fuel for boilers and engines").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Input Slot Hover (x: 43..61, y: 34..52)
        if (mouseX >= xo + 43 && mouseX <= xo + 61 && mouseY >= yo + 34 && mouseY <= yo + 52) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Crop Input Slot").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Accepts: Wheat (200 mB + 20% Seeds),").withStyle(ChatFormatting.GRAY),
                    Component.literal("Potatoes (150 mB), Sugar Cane (100 mB)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }
}
