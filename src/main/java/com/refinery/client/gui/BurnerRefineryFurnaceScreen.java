package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.block.entity.IRefineryFurnace;
import com.refinery.menu.BurnerRefineryFurnaceMenu;
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
public class BurnerRefineryFurnaceScreen extends AbstractContainerScreen<BurnerRefineryFurnaceMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/burner_refinery_furnace.png");

    // Redstone Control Button (inside right control panel at x: 88, y: 54, w: 77, h: 14)
    private static final int RS_BTN_X = 88;
    private static final int RS_BTN_Y = 54;
    private static final int RS_BTN_W = 77;
    private static final int RS_BTN_H = 14;

    public BurnerRefineryFurnaceScreen(BurnerRefineryFurnaceMenu menu, Inventory inventory, Component title) {
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
            status = Component.literal("HEATING...").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            statusColor = 0xFFEAB308;
        } else {
            status = Component.literal("COLD / IDLE").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD);
            statusColor = 0xFF9CA3AF;
        }

        // Digital Telemetry Monitor Readout (inside 89..164, 20..51)
        graphics.text(this.font, status, 92, 22, statusColor, false);
        graphics.text(this.font, Component.literal(temp + " °C / 500 °C"), 92, 32,
                temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFEF4444 : 0xFF38BDF8, false);
        graphics.text(this.font, Component.literal("Min: 350°C | 4x"), 92, 42, 0xFFF97316, false);

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

        // 2. Burn Flame Progress (well at x: 11, y: 45, w: 34, h: 20 -> inner x: 12, y: 46, w: 32, h: 18)
        int burnTime = this.menu.getBurnTime();
        int maxBurnTime = this.menu.getMaxBurnTime();
        if (burnTime > 0 && maxBurnTime > 0) {
            int h = Math.max(2, Math.min(18, (int) (((float) burnTime / maxBurnTime) * 18)));
            int topY = yo + 46 + (18 - h);
            graphics.fill(RenderPipelines.GUI, xo + 12, topY, xo + 44, yo + 64, 0xFFEA580C);
            graphics.fill(RenderPipelines.GUI, xo + 16, Math.min(yo + 64, topY + 2), xo + 40, yo + 64, 0xFFF59E0B);
            graphics.fill(RenderPipelines.GUI, xo + 21, Math.min(yo + 64, topY + 4), xo + 35, yo + 64, 0xFFFDE047);
        }

        // 3. Temperature Thermometer Bar (well at x: 67, y: 19, w: 12, h: 46 -> inner x: 68, y: 20, w: 10, h: 44)
        int temp = this.menu.getTemperature();
        if (temp > IRefineryFurnace.AMBIENT_TEMP) {
            float ratio = (temp - IRefineryFurnace.AMBIENT_TEMP) / (float) (IRefineryFurnace.MAX_TEMP - IRefineryFurnace.AMBIENT_TEMP);
            int h = Math.min(44, Math.max(1, Math.round(ratio * 44.0f)));
            int topY = yo + 20 + (44 - h);
            int barColor = temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFDC2626 : 0xFFF59E0B;
            int capColor = temp >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFF87171 : 0xFFFDE047;
            graphics.fill(RenderPipelines.GUI, xo + 68, topY, xo + 78, yo + 64, barColor);
            graphics.fill(RenderPipelines.GUI, xo + 68, topY, xo + 78, topY + 1, capColor);
        }
        // 350°C Threshold marker line on temperature bar
        int thresholdY = yo + 20 + 44 - Math.round(((350.0f - 20.0f) / 480.0f) * 44.0f);
        graphics.fill(RenderPipelines.GUI, xo + 67, thresholdY, xo + 79, thresholdY + 1, 0xFF22C55E);

        // 4. Redstone Control 3D Beveled Button (x: 88, y: 54, w: 77, h: 14)
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

        // Temperature Thermometer Bar Tooltip (x: 67..79, y: 19..65)
        if (mouseX >= xo + 67 && mouseX <= xo + 79 && mouseY >= yo + 19 && mouseY <= yo + 65) {
            int temp = this.menu.getTemperature();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Furnace Temperature").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(temp + " °C / 500 °C").withStyle(temp >= 350 ? ChatFormatting.RED : ChatFormatting.YELLOW),
                    Component.literal("Operating Threshold: >= 350 °C (Green Line)").withStyle(ChatFormatting.GREEN),
                    Component.literal("Place adjacent to Stage 1 of the Refinery Tower").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
        // Burn Flame Gauge Tooltip (x: 11..45, y: 45..65)
        else if (mouseX >= xo + 11 && mouseX <= xo + 45 && mouseY >= yo + 45 && mouseY <= yo + 65) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High-Draft Combustion Firebox").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Burn Rate: 4x Rapid Combustion").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Remaining: " + (this.menu.getBurnTime() / 4) + " ticks").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
        // Redstone Mode Button Tooltip (x: 88..165, y: 54..68)
        else if (mouseX >= xo + RS_BTN_X && mouseX < xo + RS_BTN_X + RS_BTN_W
                && mouseY >= yo + RS_BTN_Y && mouseY < yo + RS_BTN_Y + RS_BTN_H) {
            int mode = this.menu.getRedstoneMode();
            String desc = switch (mode) {
                case 1 -> "Active with Redstone High";
                case 2 -> "Active with Redstone Low";
                default -> "Always Running (when fuel available)";
            };
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Control Mode").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    Component.literal(desc).withStyle(ChatFormatting.GRAY),
                    Component.literal("Click to cycle mode").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
    }
}
