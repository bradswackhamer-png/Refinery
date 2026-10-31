package com.refinery.client.gui;

import com.refinery.menu.AutoHarvesterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
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

        boolean enabled = this.menu.isEnabled();
        int energy = this.menu.getEnergy();
        boolean working = this.menu.isWorking();

        Component statusText;
        int statusColor;

        if (!enabled) {
            statusText = Component.literal("DISABLED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0xAA2222;
        } else if (energy < 1) {
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

        boolean enabled = this.menu.isEnabled();
        int radius = this.menu.getRadius();

        // 1. Power Switch Button (x: 122..166, y: 18..32)
        boolean pwrHover = mouseX >= xo + 122 && mouseX <= xo + 166 && mouseY >= yo + 18 && mouseY <= yo + 32;
        int pwrBg = enabled ? (pwrHover ? 0xFF254B2C : 0xFF1B3820) : (pwrHover ? 0xFF4B2525 : 0xFF381B1B);
        int pwrBorder = enabled ? 0xFF34D399 : 0xFFF87171;

        // Outer border and fill
        graphics.fill(RenderPipelines.GUI, xo + 122, yo + 18, xo + 166, yo + 32, pwrBorder);
        graphics.fill(RenderPipelines.GUI, xo + 123, yo + 19, xo + 165, yo + 31, pwrBg);

        // Power Status LED
        int ledColor;
        if (!enabled) {
            ledColor = 0xFFEF4444; // Red (Off)
        } else if (energy < 1) {
            ledColor = 0xFFEF4444; // Red (No Power)
        } else if (this.menu.isWorking()) {
            ledColor = 0xFF22C55E; // Green (Working)
        } else {
            ledColor = 0xFFEAB308; // Yellow (Scanning)
        }
        graphics.fill(RenderPipelines.GUI, xo + 126, yo + 22, xo + 130, yo + 28, ledColor);

        // Power Text
        String pwrText = enabled ? "ON" : "OFF";
        int pwrTextColor = enabled ? 0xFF4ADE80 : 0xFFF87171;
        int pwrTextX = xo + (enabled ? 142 : 138);
        graphics.text(this.font, Component.literal(pwrText), pwrTextX, yo + 21, pwrTextColor, false);

        // 2. Radius Controls (x: 122..166, y: 36..50)
        // [-] Button (x: 122..135, y: 36..50)
        boolean minusHover = mouseX >= xo + 122 && mouseX <= xo + 135 && mouseY >= yo + 36 && mouseY <= yo + 50;
        int minusBg = minusHover ? 0xFF424754 : 0xFF2B2F38;
        graphics.fill(RenderPipelines.GUI, xo + 122, yo + 36, xo + 135, yo + 50, 0xFF525766);
        graphics.fill(RenderPipelines.GUI, xo + 123, yo + 37, xo + 134, yo + 49, minusBg);
        int minusColor = radius > 1 ? 0xFFFFFFFF : 0xFF777777;
        graphics.text(this.font, Component.literal("-"), xo + 126, yo + 39, minusColor, false);

        // Radius Display Box (x: 137..151, y: 36..50)
        graphics.fill(RenderPipelines.GUI, xo + 137, yo + 36, xo + 151, yo + 50, 0xFF383C48);
        graphics.fill(RenderPipelines.GUI, xo + 138, yo + 37, xo + 150, yo + 49, 0xFF15171C);
        String radStr = String.valueOf(radius);
        int radW = this.font.width(radStr);
        graphics.text(this.font, Component.literal(radStr), xo + 144 - radW / 2, yo + 39, 0xFF38BDF8, false);

        // [+] Button (x: 153..166, y: 36..50)
        boolean plusHover = mouseX >= xo + 153 && mouseX <= xo + 166 && mouseY >= yo + 36 && mouseY <= yo + 50;
        int plusBg = plusHover ? 0xFF424754 : 0xFF2B2F38;
        graphics.fill(RenderPipelines.GUI, xo + 153, yo + 36, xo + 166, yo + 50, 0xFF525766);
        graphics.fill(RenderPipelines.GUI, xo + 154, yo + 37, xo + 165, yo + 49, plusBg);
        int plusColor = radius < 9 ? 0xFFFFFFFF : 0xFF777777;
        graphics.text(this.font, Component.literal("+"), xo + 157, yo + 39, plusColor, false);

        // 3. Area Label underneath (x: 122..166, y: 53)
        int diameter = radius * 2 + 1;
        String areaStr = diameter + "x" + diameter;
        int areaW = this.font.width(areaStr);
        graphics.text(this.font, Component.literal(areaStr), xo + 144 - areaW / 2, yo + 53, 0xFF888888, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == 0) { // Left-click
            int xo = this.leftPos;
            int yo = this.topPos;

            // Power Switch clicked (xo + 122..166, yo + 18..32)
            if (mouseX >= xo + 122 && mouseX <= xo + 166 && mouseY >= yo + 18 && mouseY <= yo + 32) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Decrease Radius clicked (xo + 122..135, yo + 36..50)
            if (mouseX >= xo + 122 && mouseX <= xo + 135 && mouseY >= yo + 36 && mouseY <= yo + 50) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 1);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Increase Radius clicked (xo + 153..166, yo + 36..50)
            if (mouseX >= xo + 153 && mouseX <= xo + 166 && mouseY >= yo + 36 && mouseY <= yo + 50) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 2);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
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
            return;
        }

        // Power Switch Hover (x: 122..166, y: 18..32)
        if (mouseX >= xo + 122 && mouseX <= xo + 166 && mouseY >= yo + 18 && mouseY <= yo + 32) {
            boolean enabled = this.menu.isEnabled();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Harvester Power Switch").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Status: " + (enabled ? "ON (Enabled)" : "OFF (Disabled)")).withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED),
                    Component.literal("Click to turn the harvester ON or OFF").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Decrease Radius Hover (x: 122..135, y: 36..50)
        if (mouseX >= xo + 122 && mouseX <= xo + 135 && mouseY >= yo + 36 && mouseY <= yo + 50) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Decrease Work Radius").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Reduces radius by 1 block (min: 1)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Increase Radius Hover (x: 153..166, y: 36..50)
        if (mouseX >= xo + 153 && mouseX <= xo + 166 && mouseY >= yo + 36 && mouseY <= yo + 50) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Increase Work Radius").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Increases radius by 1 block (max: 9)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Radius Display / Area Hover (x: 136..152, y: 36..64 or x: 122..166, y: 51..64)
        if ((mouseX >= xo + 136 && mouseX <= xo + 152 && mouseY >= yo + 36 && mouseY <= yo + 64) ||
            (mouseX >= xo + 122 && mouseX <= xo + 166 && mouseY >= yo + 51 && mouseY <= yo + 64)) {
            int r = this.menu.getRadius();
            int dim = r * 2 + 1;
            int area = dim * dim;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Work Coverage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("Radius: %d blocks in all directions", r)).withStyle(ChatFormatting.WHITE),
                    Component.literal(String.format("Total Area: %dx%d (%d blocks / m²)", dim, dim, area)).withStyle(ChatFormatting.YELLOW),
                    Component.literal("Configurable up to 9 blocks (100m+ area)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Header Status Hover (x: 100..170, y: 4..16)
        if (mouseX >= xo + 100 && mouseX <= xo + 170 && mouseY >= yo + 4 && mouseY <= yo + 16) {
            int r = this.menu.getRadius();
            int dim = r * 2 + 1;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Work Status").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("Coverage: %dx%d horizontal square (%d blocks away)", dim, dim, r)).withStyle(ChatFormatting.WHITE),
                    Component.literal("Auto-harvests mature crops, replants, and fell trees").withStyle(ChatFormatting.GRAY),
                    Component.literal("Auto-ejects buffer into chests/pipes behind or below!").withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
        }
    }
}
