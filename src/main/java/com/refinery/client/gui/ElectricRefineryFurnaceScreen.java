package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.block.entity.ElectricRefineryFurnaceBlockEntity;
import com.refinery.block.entity.IRefineryFurnace;
import com.refinery.menu.ElectricRefineryFurnaceMenu;
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
public class ElectricRefineryFurnaceScreen extends AbstractContainerScreen<ElectricRefineryFurnaceMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/electric_refinery_furnace.png");

    // Redstone Control Button (inside right control panel at x: 88, y: 54, w: 77, h: 14)
    private static final int RS_BTN_X = 88;
    private static final int RS_BTN_Y = 54;
    private static final int RS_BTN_W = 77;
    private static final int RS_BTN_H = 14;

    public ElectricRefineryFurnaceScreen(ElectricRefineryFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        int temp = this.menu.getTemperature();
        boolean active = this.menu.isActive();

        Component status;
        int statusColor;
        if (temp >= IRefineryFurnace.MIN_OPERATING_TEMP) {
            status = Component.literal("HOT (READY)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFF59E0B;
        } else if (active) {
            status = Component.literal("HEATING...").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
            statusColor = 0xFF38BDF8;
        } else if (this.menu.getEnergyStored() >= ElectricRefineryFurnaceBlockEntity.ENERGY_PER_TICK) {
            status = Component.literal("STANDBY").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            statusColor = 0xFFEAB308;
        } else {
            status = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xFFEF4444;
        }

        // Digital Telemetry Monitor Readout (inside 89..164, 20..51)
        graphics.text(this.font, status, 92, 22, statusColor, false);
        graphics.text(this.font, Component.literal(temp + " °C / 500 °C"), 92, 32,
                temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFEF4444 : 0xFF38BDF8, false);
        String drawTxt = active ? ("-" + ElectricRefineryFurnaceBlockEntity.ENERGY_PER_TICK + " FE/t | 350°C") : "0 FE/t | Min 350°C";
        graphics.text(this.font, Component.literal(drawTxt), 92, 42, 0xFFFACC15, false);

        // Centered Redstone Control Button Label
        int mode = this.menu.getRedstoneMode();
        String rsLabel = switch (mode) {
            case 1 -> "RS: HIGH";
            case 2 -> "RS: LOW";
            default -> "RS: ALWAYS ON";
        };
        int rsColor = switch (mode) {
            case 1 -> 0xFFEF4444;
            case 2 -> 0xFFF59E0B;
            default -> 0xFF34D399;
        };
        int textW = this.font.width(rsLabel);
        graphics.text(this.font, Component.literal(rsLabel), RS_BTN_X + (RS_BTN_W - textW) / 2, RS_BTN_Y + 3, rsColor, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // 1. Base Industrial Steel GUI Texture
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 2. High-Voltage RF Energy Buffer Bar (well at x: 12, y: 19, w: 18, h: 46 -> inner x: 13, y: 20, w: 16, h: 44)
        int energy = this.menu.getEnergyStored();
        if (energy > 0) {
            int h = Math.min(44, Math.max(1, (int) ((energy / (float) ElectricRefineryFurnaceBlockEntity.ENERGY_CAPACITY) * 44)));
            int topY = yo + 20 + (44 - h);
            graphics.fill(RenderPipelines.GUI, xo + 13, topY, xo + 29, yo + 64, 0xFFEAB308);
            graphics.fill(RenderPipelines.GUI, xo + 13, topY, xo + 29, topY + 1, 0xFFFDE047);
        }

        // 3. Induction Heating Coil Glow (well at x: 34, y: 24, w: 28, h: 36 -> inner x: 35, y: 25, w: 26, h: 34)
        if (this.menu.isActive()) {
            for (int cy = yo + 29; cy < yo + 56; cy += 5) {
                graphics.fill(RenderPipelines.GUI, xo + 38, cy, xo + 58, cy + 2, 0xFF0284C7);
                graphics.fill(RenderPipelines.GUI, xo + 40, cy, xo + 56, cy + 1, 0xFF38BDF8);
            }
            graphics.fill(RenderPipelines.GUI, xo + 46, yo + 36, xo + 50, yo + 48, 0xFFE0F2FE);
        }

        // 4. Temperature Thermometer Bar (well at x: 66, y: 19, w: 12, h: 46 -> inner x: 67, y: 20, w: 10, h: 44)
        int temp = this.menu.getTemperature();
        if (temp > IRefineryFurnace.AMBIENT_TEMP) {
            float ratio = (temp - IRefineryFurnace.AMBIENT_TEMP) / (float) (IRefineryFurnace.MAX_TEMP - IRefineryFurnace.AMBIENT_TEMP);
            int h = Math.min(44, Math.max(1, Math.round(ratio * 44.0f)));
            int topY = yo + 20 + (44 - h);
            int barColor = temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFDC2626 : 0xFF0284C7;
            int capColor = temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFF87171 : 0xFF38BDF8;
            graphics.fill(RenderPipelines.GUI, xo + 67, topY, xo + 77, yo + 64, barColor);
            graphics.fill(RenderPipelines.GUI, xo + 67, topY, xo + 77, topY + 1, capColor);
        }
        // 350°C Threshold marker line
        int thresholdY = yo + 20 + 44 - Math.round(((350.0f - 20.0f) / 480.0f) * 44.0f);
        graphics.fill(RenderPipelines.GUI, xo + 66, thresholdY, xo + 78, thresholdY + 1, 0xFF22C55E);

        // 5. Redstone Control 3D Beveled Button (x: 88, y: 54, w: 77, h: 14)
        boolean hovered = mouseX >= xo + RS_BTN_X && mouseX < xo + RS_BTN_X + RS_BTN_W
                && mouseY >= yo + RS_BTN_Y && mouseY < yo + RS_BTN_Y + RS_BTN_H;
        drawIndustrialButton(graphics, xo + RS_BTN_X, yo + RS_BTN_Y, RS_BTN_W, RS_BTN_H, hovered);
    }

    private void drawIndustrialButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int border = 0xFF14171A;
        int topHighlight = hovered ? 0xFF94A3B8 : 0xFF565F6C;
        int bottomShadow = 0xFF1A1D20;
        int bg = hovered ? 0xFF3E4652 : 0xFF2B3036;

        graphics.fill(RenderPipelines.GUI, x, y, x + w, y + h, border);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + w - 1, y + 2, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + 2, y + h - 1, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + h - 2, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + w - 2, y + 1, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + 2, y + 2, x + w - 2, y + h - 2, bg);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        int button = event.button();
        if (button == InputConstants.MOUSE_BUTTON_LEFT || button == 0) {
            double mouseX = event.x();
            double mouseY = event.y();
            int xo = this.leftPos;
            int yo = this.topPos;

            if (mouseX >= xo + RS_BTN_X && mouseX < xo + RS_BTN_X + RS_BTN_W
                    && mouseY >= yo + RS_BTN_Y && mouseY < yo + RS_BTN_Y + RS_BTN_H) {
                if (this.minecraft != null && this.minecraft.gameMode != null && this.minecraft.player != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

        // Energy Bar Tooltip (x: 12..30, y: 19..65)
        if (mouseX >= xo + 12 && mouseX <= xo + 30 && mouseY >= yo + 19 && mouseY <= yo + 65) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High-Voltage RF Buffer").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / %,d FE", this.menu.getEnergyStored(), ElectricRefineryFurnaceBlockEntity.ENERGY_CAPACITY)).withStyle(ChatFormatting.GOLD),
                    Component.literal("Draws " + ElectricRefineryFurnaceBlockEntity.ENERGY_PER_TICK + " FE/t when heating").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
        // Induction Coil Chamber Tooltip (x: 34..62, y: 24..60)
        else if (mouseX >= xo + 34 && mouseX <= xo + 62 && mouseY >= yo + 24 && mouseY <= yo + 60) {
            boolean active = this.menu.isActive();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High-Power Induction Heater").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Status: " + (active ? "Energized (500 FE/t)" : "Standby")).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY),
                    Component.literal("Rapidly heats oil vapor feed up to 500 °C").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Temperature Bar Tooltip (x: 66..78, y: 19..65)
        else if (mouseX >= xo + 66 && mouseX <= xo + 78 && mouseY >= yo + 19 && mouseY <= yo + 65) {
            int temp = this.menu.getTemperature();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Induction Coil Temperature").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(temp + " °C / 500 °C").withStyle(temp >= 350 ? ChatFormatting.RED : ChatFormatting.YELLOW),
                    Component.literal("Operating Threshold: >= 350 °C (Green Line)").withStyle(ChatFormatting.GREEN),
                    Component.literal("Place adjacent to Stage 1 of the Refinery Tower").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
        // Redstone Mode Button Tooltip (x: 88..165, y: 54..68)
        else if (mouseX >= xo + RS_BTN_X && mouseX < xo + RS_BTN_X + RS_BTN_W
                && mouseY >= yo + RS_BTN_Y && mouseY < yo + RS_BTN_Y + RS_BTN_H) {
            int mode = this.menu.getRedstoneMode();
            String desc = switch (mode) {
                case 1 -> "Active with Redstone High";
                case 2 -> "Active with Redstone Low";
                default -> "Always Running (when powered)";
            };
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Control Mode").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(desc).withStyle(ChatFormatting.GRAY),
                    Component.literal("Click to cycle mode").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
    }
}
