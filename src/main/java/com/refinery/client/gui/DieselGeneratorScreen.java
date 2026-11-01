package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.menu.DieselGeneratorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DieselGeneratorScreen extends AbstractContainerScreen<DieselGeneratorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/diesel_generator.png");

    public DieselGeneratorScreen(DieselGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Center Digital Monitor Readout (inside 60x50 frame at x: 58, y: 20)
        boolean active = this.menu.isActive();
        int diesel = this.menu.getDieselAmount();
        int energy = this.menu.getEnergyStored();

        Component status;
        int statusColor;

        if (active) {
            status = Component.literal("GENERATING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFF59E0B;
        } else if (diesel <= 0) {
            status = Component.literal("NO FUEL").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xFFEF4444;
        } else if (energy >= 500_000) {
            status = Component.literal("STANDBY").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
            statusColor = 0xFF38BDF8;
        } else {
            status = Component.literal("ONLINE").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            statusColor = 0xFF22C55E;
        }

        graphics.text(this.font, status, 63, 25, statusColor, false);

        String rateText = active ? "+256 FE/t" : "0 FE/t";
        graphics.text(this.font, Component.literal(rateText).withStyle(ChatFormatting.YELLOW), 63, 37, 0xFFEAB308, false);

        int mode = this.menu.getRedstoneMode();
        String modeLabel = switch (mode) {
            case 1 -> "RS: HIGH";
            case 2 -> "RS: LOW";
            default -> "ALWAYS ON";
        };
        graphics.text(this.font, Component.literal(modeLabel).withStyle(ChatFormatting.GRAY), 63, 49, 0xFF9CA3AF, false);

        // Redstone Button Label inside (x: 147, y: 35, w: 20, h: 20)
        String btnText = switch (mode) {
            case 1 -> "R+";
            case 2 -> "R-";
            default -> "ON";
        };
        graphics.text(this.font, Component.literal(btnText).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD), 152, 41, 0xFFFFFFFF, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Diesel Fuel Level (x: 36, y: 21, w: 16, h: 48)
        int diesel = this.menu.getDieselAmount();
        if (diesel > 0) {
            int h = Math.min(48, (int) ((diesel / 8000.0f) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 36, yo + 21 + (48 - h), xo + 36 + 16, yo + 21 + 48, 0xFFD97706);
        }

        // 2. Energy Level (x: 125, y: 21, w: 16, h: 48)
        int energy = this.menu.getEnergyStored();
        if (energy > 0) {
            int h = Math.min(48, (int) ((energy / 500000.0f) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 125, yo + 21 + (48 - h), xo + 125 + 16, yo + 21 + 48, 0xFFFACC15);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        int button = event.button();
        if (button == InputConstants.MOUSE_BUTTON_LEFT || button == 0) {
            double mouseX = event.x();
            double mouseY = event.y();
            int xo = this.leftPos;
            int yo = this.topPos;

            // Check Redstone Mode Button (x: 147, y: 35, w: 20, h: 20)
            if (mouseX >= xo + 147 && mouseX < xo + 167 && mouseY >= yo + 35 && mouseY < yo + 55) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Diesel Tank Tooltip (x: 35..52, y: 20..69)
        if (mouseX >= xo + 35 && mouseX < xo + 52 && mouseY >= yo + 20 && mouseY < yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Refined Diesel Tank").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getDieselAmount() + " / 8,000 mB").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Burns: 1 mB per 4 ticks (1,000 FE/mB)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Energy Bar Tooltip (x: 124..141, y: 20..69)
        if (mouseX >= xo + 124 && mouseX < xo + 141 && mouseY >= yo + 20 && mouseY < yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Buffer").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergyStored() + " / 500,000 FE").withStyle(ChatFormatting.GOLD),
                    Component.literal("Output: up to 1,024 FE/t auto-push").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Redstone Mode Button Tooltip (x: 147..167, y: 35..55)
        if (mouseX >= xo + 147 && mouseX < xo + 167 && mouseY >= yo + 35 && mouseY < yo + 55) {
            int mode = this.menu.getRedstoneMode();
            String desc = switch (mode) {
                case 1 -> "Active with Redstone High";
                case 2 -> "Active with Redstone Low (Emergency Backup)";
                default -> "Always Running (when fuel & space available)";
            };
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Control Mode").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(desc).withStyle(ChatFormatting.GRAY),
                    Component.literal("Click to cycle mode").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
    }
}
