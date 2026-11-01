package com.refinery.client.gui;

import com.refinery.menu.RefineryMenu;
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
public class RefineryScreen extends AbstractContainerScreen<RefineryMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/refinery.png");

    public RefineryScreen(RefineryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 176);
        this.inventoryLabelY = 82;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Status Header
        boolean assembled = this.menu.isAssembled();
        Component statusText = assembled
                ? Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD)
                : Component.literal("INCOMPLETE").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
        graphics.text(this.font, statusText, 100, 6, assembled ? 0xFF228822 : 0xFF882222, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Feedstock Slot Frame (x: 43, y: 16, w: 18, h: 18)
        graphics.fill(RenderPipelines.GUI, xo + 43, yo + 16, xo + 61, yo + 34, 0xFF374151);
        graphics.fill(RenderPipelines.GUI, xo + 44, yo + 17, xo + 60, yo + 33, 0xFF18181B);

        // Oil Sand Cracking Progress Bar (x: 44, y: 35, w: 16, h: 2)
        int sandProg = this.menu.getOilSandProgress();
        int maxSandProg = this.menu.getMaxOilSandProgress();
        if (sandProg > 0 && maxSandProg > 0) {
            int fillW = Math.min(16, (int) (((float) sandProg / maxSandProg) * 16));
            graphics.fill(RenderPipelines.GUI, xo + 44, yo + 35, xo + 44 + fillW, yo + 37, 0xFFF59E0B);
        }

        // Crude Oil Tank Gauge (x: 18, y: 20, w: 16, h: 52)
        int crudeOil = this.menu.getCrudeOil();
        if (crudeOil > 0) {
            int oilHeight = Math.min(52, (int) ((crudeOil / 16000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 20 + 52 - oilHeight, xo + 18 + 16, yo + 20 + 52, 0xFF1C1917);
        }

        // Diesel Tank Gauge (x: 74, y: 20, w: 16, h: 52)
        int diesel = this.menu.getDiesel();
        if (diesel > 0) {
            int dieselHeight = Math.min(52, (int) ((diesel / 16000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 74, yo + 20 + 52 - dieselHeight, xo + 74 + 16, yo + 20 + 52, 0xFFEAB308);
        }

        // Gasoline Tank Gauge (x: 96, y: 20, w: 16, h: 52)
        int gasoline = this.menu.getGasoline();
        if (gasoline > 0) {
            int gasHeight = Math.min(52, (int) ((gasoline / 16000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 96, yo + 20 + 52 - gasHeight, xo + 96 + 16, yo + 20 + 52, 0xFF06B6D4);
        }

        // Energy Bar (x: 162, y: 20, w: 6, h: 52)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(52, (int) ((energy / 100000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 162, yo + 20 + 52 - energyHeight, xo + 162 + 6, yo + 20 + 52, 0xFFEF4444);
        }

        // Burner Flame indicator (x: 46, y: 38, w: 12, h: 12)
        if (this.menu.isBurning()) {
            graphics.fill(RenderPipelines.GUI, xo + 46, yo + 38, xo + 46 + 12, yo + 38 + 12, 0xFFF97316);
        }

        // Refining Progress Bar (x: 38, y: 24, w: 32, h: 4)
        int progress = this.menu.getProgress();
        if (progress > 0) {
            int progWidth = Math.min(32, (int) ((progress / 100.0f) * 32));
            graphics.fill(RenderPipelines.GUI, xo + 38, yo + 24, xo + 38 + progWidth, yo + 28, 0xFF10B981);
        }

        // Multiblock Status Indicator Light (x: 88, y: 6, w: 6, h: 6)
        int statusColor = this.menu.isAssembled() ? 0xFF22C55E : 0xFFEF4444;
        graphics.fill(RenderPipelines.GUI, xo + 88, yo + 6, xo + 94, yo + 12, statusColor);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int xo = this.leftPos;
        int yo = this.topPos;

        // Feedstock Slot Hover (x: 43..61, y: 16..34)
        if (mouseX >= xo + 43 && mouseX <= xo + 61 && mouseY >= yo + 16 && mouseY <= yo + 34) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Feedstock Slot (Oil Sand)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Cracks Oil Sand into:").withStyle(ChatFormatting.GRAY),
                    Component.literal(" - +200 mB Crude Oil").withStyle(ChatFormatting.YELLOW),
                    Component.literal(" - 1 Sand (100% yield)").withStyle(ChatFormatting.WHITE),
                    Component.literal(" - Tar Byproduct (40% yield)").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Crude Oil Tank Hover (x: 17..35, y: 19..73)
        else if (mouseX >= xo + 17 && mouseX <= xo + 35 && mouseY >= yo + 19 && mouseY <= yo + 73) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Crude Oil Tank").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getCrudeOil())).withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
        // Diesel Tank Hover (x: 73..91, y: 19..73)
        else if (mouseX >= xo + 73 && mouseX <= xo + 91 && mouseY >= yo + 19 && mouseY <= yo + 73) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Diesel Fuel Tank").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getDiesel())).withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
        // Gasoline Tank Hover (x: 95..113, y: 19..73)
        else if (mouseX >= xo + 95 && mouseX <= xo + 113 && mouseY >= yo + 19 && mouseY <= yo + 73) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Gasoline / Gas Tank").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getGasoline())).withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
        }
        // Energy Bar Hover (x: 161..169, y: 19..73)
        else if (mouseX >= xo + 161 && mouseX <= xo + 169 && mouseY >= yo + 19 && mouseY <= yo + 73) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Buffer (FE / RF)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 100,000 FE", this.menu.getEnergy())).withStyle(ChatFormatting.WHITE)
            ), mouseX, mouseY);
        }
        // Multiblock Status Hover (x: 86..150, y: 4..16)
        else if (mouseX >= xo + 86 && mouseX <= xo + 150 && mouseY >= yo + 4 && mouseY <= yo + 16) {
            boolean assembled = this.menu.isAssembled();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Multiblock Status").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    assembled
                            ? Component.literal("Structure: 27/27 ONLINE").withStyle(ChatFormatting.GREEN)
                            : Component.literal("Structure: INCOMPLETE").withStyle(ChatFormatting.RED)
            ), mouseX, mouseY);
        }
        // Burner Flame Hover (x: 44..60, y: 36..50)
        else if (mouseX >= xo + 44 && mouseX <= xo + 60 && mouseY >= yo + 36 && mouseY <= yo + 50) {
            boolean burning = this.menu.isBurning();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Combustion Chamber").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    burning
                            ? Component.literal(String.format("Firebox: %d ticks remaining", this.menu.getBurnTime())).withStyle(ChatFormatting.YELLOW)
                            : Component.literal("Firebox: Idle (Slot below accepts Coal/Tar/Fuel)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }
}
