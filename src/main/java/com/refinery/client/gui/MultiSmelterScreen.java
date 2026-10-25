package com.refinery.client.gui;

import com.refinery.menu.MultiSmelterMenu;
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
public class MultiSmelterScreen extends AbstractContainerScreen<MultiSmelterMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/multi_smelter.png");

    public MultiSmelterScreen(MultiSmelterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean assembled = this.menu.isAssembled();
        int energy = this.menu.getEnergy();
        int burnTime = this.menu.getBurnTime();
        int mask = this.menu.getActiveSlotsMask();
        int activeCount = Integer.bitCount(mask);

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0x882222;
        } else if (activeCount > 0) {
            statusText = Component.literal("SMELTING (" + activeCount + ")").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xEA580C;
        } else if (burnTime > 0) {
            statusText = Component.literal("HEATED").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            statusColor = 0xD97706;
        } else if (energy < 30 && burnTime <= 0) {
            statusText = Component.literal("IDLE").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD);
            statusColor = 0x666666;
        } else {
            statusText = Component.literal("READY").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 98, 6, statusColor, false);
        graphics.text(this.font, Component.literal("IN").withStyle(ChatFormatting.DARK_GRAY), 26, 9, 0x444444, false);
        graphics.text(this.font, Component.literal("OUT").withStyle(ChatFormatting.DARK_GRAY), 100, 9, 0x444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (x: 8, y: 18, w: 12, h: 58)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(58, (int) ((energy / 50000.0f) * 58));
            graphics.fill(RenderPipelines.GUI, xo + 8, yo + 18 + (58 - energyHeight), xo + 8 + 12, yo + 18 + 58, 0xFFEAB308);
            graphics.fill(RenderPipelines.GUI, xo + 8, yo + 18 + (58 - energyHeight), xo + 8 + 2, yo + 18 + 58, 0xFFFDE047);
        }

        // 2. Solid Fuel Flame Burn Indicator (x: 71, y: 62, w: 14, h: 14)
        int burnTime = this.menu.getBurnTime();
        int maxBurnTime = this.menu.getMaxBurnTime();
        if (burnTime > 0 && maxBurnTime > 0) {
            int flameH = Math.min(14, (int) (((float) burnTime / maxBurnTime) * 14));
            // Fiery orange/red fill
            graphics.fill(RenderPipelines.GUI, xo + 71, yo + 62 + (14 - flameH), xo + 71 + 14, yo + 62 + 14, 0xFFEF4444);
            graphics.fill(RenderPipelines.GUI, xo + 73, yo + 62 + (14 - flameH), xo + 73 + 10, yo + 62 + 14, 0xFFF97316);
            graphics.fill(RenderPipelines.GUI, xo + 75, yo + 62 + (14 - flameH), xo + 75 + 6, yo + 62 + 14, 0xFFFDE047);
        }

        // 3. Parallel Cook Progress Bars (for each active input slot, render tiny progress glow under slot)
        for (int i = 0; i < 8; i++) {
            int progress = this.menu.getCookProgress(i);
            if (progress > 0) {
                int col = i % 4;
                int row = i / 4;
                int slotX = xo + 26 + col * 18;
                int slotY = yo + 20 + row * 18;
                int progressWidth = Math.min(16, (int) (((float) progress / 100.0f) * 16));
                // Cook underline bar
                graphics.fill(RenderPipelines.GUI, slotX, slotY + 16, slotX + progressWidth, slotY + 18, 0xFFF97316);
            }
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 8..20, y: 18..76)
        if (mouseX >= xo + 8 && mouseX <= xo + 20 && mouseY >= yo + 18 && mouseY <= yo + 76) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergy() + " / 50,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Rate: 30 FE/t while electric smelting").withStyle(ChatFormatting.GREEN),
                    Component.literal("Dual-power: Runs on FE or Solid Fuel!").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Fuel Flame Hover (x: 70..86, y: 60..76)
        if (mouseX >= xo + 70 && mouseX <= xo + 86 && mouseY >= yo + 60 && mouseY <= yo + 76) {
            int burnTime = this.menu.getBurnTime();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Combustion Firebox").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Burn Time: " + burnTime + " ticks").withStyle(ChatFormatting.GRAY),
                    Component.literal("Powers all 8 smelting slots in parallel!").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
    }
}
