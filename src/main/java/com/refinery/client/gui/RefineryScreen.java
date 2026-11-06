package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.block.entity.IRefineryFurnace;
import com.refinery.menu.RefineryMenu;
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

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RefineryScreen extends AbstractContainerScreen<RefineryMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/refinery.png");

    // Stage toggle buttons (arranged top-to-bottom like the physical tower: Stage 3 top, Stage 2 middle, Stage 1 bottom)
    private static final int STAGE_BTN_X = 56;
    private static final int STAGE_BTN_W = 24;
    private static final int STAGE_BTN_H = 16;
    private static final int STAGE3_BTN_Y = 19;
    private static final int STAGE2_BTN_Y = 37;
    private static final int STAGE1_BTN_Y = 55;

    public RefineryScreen(RefineryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 176);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 82;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Draw stage labels inside the 3 stage buttons (with space on left for 3px LED dot)
        graphics.text(this.font, "S3", STAGE_BTN_X + 8, STAGE3_BTN_Y + 4, this.menu.isStage3Enabled() ? 0xFFFFFFFF : 0xFF71717A, false);
        graphics.text(this.font, "S2", STAGE_BTN_X + 8, STAGE2_BTN_Y + 4, this.menu.isStage2Enabled() ? 0xFFFFFFFF : 0xFF71717A, false);
        graphics.text(this.font, "S1", STAGE_BTN_X + 8, STAGE1_BTN_Y + 4, this.menu.isStage1Enabled() ? 0xFFFFFFFF : 0xFF71717A, false);

        // Bitumen Byproduct Header Label inside right panel monitor (x: 132, y: 19, w: 35, h: 14)
        // Scaled down by 20% (0.8f) to fit the monitor frame perfectly
        graphics.pose().pushMatrix();
        graphics.pose().translate(134.0f, 23.0f);
        graphics.pose().scale(0.8f, 0.8f);
        graphics.text(this.font, "BITUMEN", 0, 0, 0xFFF59E0B, false);
        graphics.pose().popMatrix();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // 1. Base Industrial Steel GUI background (176x176)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 2. Crude Oil Feed Tank Fill (well at x: 11, y: 19, w: 16, h: 32 -> inner x: 12, y: 20, w: 14, h: 30)
        int crudeOil = this.menu.getCrudeOil();
        if (crudeOil > 0) {
            int oilHeight = Math.min(30, Math.max(1, (int) ((crudeOil / 16000.0f) * 30)));
            int topY = yo + 20 + (30 - oilHeight);
            graphics.fill(RenderPipelines.GUI, xo + 12, topY, xo + 26, yo + 50, 0xFF1C1917);
            graphics.fill(RenderPipelines.GUI, xo + 12, topY, xo + 26, topY + 1, 0xFF44403C);
        }

        // 3. Furnace Heat Thermometer Fill (well at x: 31, y: 19, w: 16, h: 32 -> inner x: 32, y: 20, w: 14, h: 30)
        int heat = this.menu.getCurrentHeat();
        float heatFraction = Math.max(0.0f, Math.min(1.0f,
                (heat - IRefineryFurnace.AMBIENT_TEMP) / (float) (IRefineryFurnace.MAX_TEMP - IRefineryFurnace.AMBIENT_TEMP)));
        int heatFill = Math.round(heatFraction * 30.0f);
        if (heatFill > 0) {
            int topY = yo + 20 + (30 - heatFill);
            int color = heat >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFDC2626 : 0xFFF59E0B;
            int cap = heat >= IRefineryFurnace.MIN_OPERATING_TEMP ? 0xFFF87171 : 0xFFFDE047;
            graphics.fill(RenderPipelines.GUI, xo + 32, topY, xo + 46, yo + 50, color);
            graphics.fill(RenderPipelines.GUI, xo + 32, topY, xo + 46, topY + 1, cap);
        }
        // 350°C operating threshold marker line
        int thresholdY = yo + 20 + 30 - Math.round(((350.0f - 20.0f) / 480.0f) * 30.0f);
        graphics.fill(RenderPipelines.GUI, xo + 31, thresholdY, xo + 47, thresholdY + 1, 0xFF22C55E);

        // 4. Oil Sand Cracking Progress Bar (groove at x: 11, y: 53, w: 18, h: 4 -> inner x: 12, y: 54, w: 16, h: 2)
        int sandProg = this.menu.getOilSandProgress();
        int maxSandProg = this.menu.getMaxOilSandProgress();
        if (sandProg > 0 && maxSandProg > 0) {
            int fillW = Math.min(16, (int) (((float) sandProg / maxSandProg) * 16));
            graphics.fill(RenderPipelines.GUI, xo + 12, yo + 54, xo + 12 + fillW, yo + 56, 0xFFF59E0B);
        }

        // 5. Stage Toggle 3D Beveled Industrial Buttons (x: 56, y: 19 / 37 / 55, w: 24, h: 16)
        drawStageButton(graphics, xo + STAGE_BTN_X, yo + STAGE3_BTN_Y, this.menu.isStage3Enabled(), 0xFF0891B2, 0xFF22D3EE, mouseX, mouseY);
        drawStageButton(graphics, xo + STAGE_BTN_X, yo + STAGE2_BTN_Y, this.menu.isStage2Enabled(), 0xFFD97706, 0xFFFACC15, mouseX, mouseY);
        drawStageButton(graphics, xo + STAGE_BTN_X, yo + STAGE1_BTN_Y, this.menu.isStage1Enabled(), 0xFF991B1B, 0xFFF87171, mouseX, mouseY);

        // 6. Distillation Progress Bar (groove at x: 56, y: 73, w: 69, h: 5 -> inner x: 57, y: 74, w: 67, h: 3)
        int progress = this.menu.getProgress();
        if (progress > 0) {
            int progW = Math.min(67, (int) ((progress / 100.0f) * 67));
            graphics.fill(RenderPipelines.GUI, xo + 57, yo + 74, xo + 57 + progW, yo + 77, 0xFF10B981);
            graphics.fill(RenderPipelines.GUI, xo + 57, yo + 74, xo + 57 + progW, yo + 75, 0xFF34D399);
        }

        // 7. Output Tank 1: Liquid Tar (well at x: 83, y: 19, w: 12, h: 52 -> inner x: 84, y: 20, w: 10, h: 50)
        drawTankFill(graphics, xo + 84, yo + 20, 10, 50, this.menu.getLiquidTar(), 0xFF3F2314, 0xFF78350F);
        // 8. Output Tank 2: Diesel (well at x: 98, y: 19, w: 12, h: 52 -> inner x: 99, y: 20, w: 10, h: 50)
        drawTankFill(graphics, xo + 99, yo + 20, 10, 50, this.menu.getDiesel(), 0xFFCA8A04, 0xFFFDE047);
        // 9. Output Tank 3: Gasoline (well at x: 113, y: 19, w: 12, h: 52 -> inner x: 114, y: 20, w: 10, h: 50)
        drawTankFill(graphics, xo + 114, yo + 20, 10, 50, this.menu.getGasoline(), 0xFF0891B2, 0xFF67E8F9);

        // 10. Multiblock Status Indicator Light (inside bezel at x: 161, y: 5, w: 8, h: 8 -> inner x: 162, y: 6, w: 6, h: 6)
        boolean assembled = this.menu.isAssembled();
        int statusColor = assembled ? 0xFF22C55E : 0xFFEF4444;
        graphics.fill(RenderPipelines.GUI, xo + 162, yo + 6, xo + 168, yo + 12, statusColor);
        if (assembled) {
            graphics.fill(RenderPipelines.GUI, xo + 163, yo + 7, xo + 166, yo + 10, 0xFF86EFAC);
        }
    }

    private void drawStageButton(GuiGraphicsExtractor graphics, int x, int y, boolean enabled, int activeBg, int ledGlow, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + STAGE_BTN_W && mouseY >= y && mouseY < y + STAGE_BTN_H;
        int border = 0xFF14171A;
        int topHighlight = hovered ? 0xFFE2E8F0 : (enabled ? ledGlow : 0xFF4C5461);
        int bottomShadow = 0xFF111317;
        int bg = enabled ? (hovered ? brighten(activeBg) : activeBg) : (hovered ? 0xFF323842 : 0xFF22262C);

        graphics.fill(RenderPipelines.GUI, x, y, x + STAGE_BTN_W, y + STAGE_BTN_H, border);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + STAGE_BTN_W - 1, y + 2, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + 2, y + STAGE_BTN_H - 1, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + STAGE_BTN_H - 2, x + STAGE_BTN_W - 1, y + STAGE_BTN_H - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + STAGE_BTN_W - 2, y + 1, x + STAGE_BTN_W - 1, y + STAGE_BTN_H - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + 2, y + 2, x + STAGE_BTN_W - 2, y + STAGE_BTN_H - 2, bg);

        // Status LED indicator on left side of button
        int ledCol = enabled ? 0xFF22C55E : 0xFF52525B;
        graphics.fill(RenderPipelines.GUI, x + 3, y + 6, x + 6, y + 10, ledCol);
    }

    private static int brighten(int argb) {
        int r = Math.min(255, ((argb >> 16) & 0xFF) + 28);
        int g = Math.min(255, ((argb >> 8) & 0xFF) + 28);
        int b = Math.min(255, (argb & 0xFF) + 28);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private void drawTankFill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int amount, int color, int meniscusColor) {
        if (amount > 0) {
            int fillH = Math.min(h, Math.max(1, (int) ((amount / 16000.0f) * h)));
            int topY = y + h - fillH;
            graphics.fill(RenderPipelines.GUI, x, topY, x + w, y + h, color);
            graphics.fill(RenderPipelines.GUI, x, topY, x + w, topY + 1, meniscusColor);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int button = event.button();
        if ((button == InputConstants.MOUSE_BUTTON_LEFT || button == 0)
                && this.minecraft != null && this.minecraft.gameMode != null && this.minecraft.player != null) {
            int xo = this.leftPos;
            int yo = this.topPos;
            double mx = event.x();
            double my = event.y();

            if (mx >= xo + STAGE_BTN_X && mx < xo + STAGE_BTN_X + STAGE_BTN_W) {
                if (my >= yo + STAGE3_BTN_Y && my < yo + STAGE3_BTN_Y + STAGE_BTN_H) {
                    this.menu.clickMenuButton(this.minecraft.player, 2);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
                if (my >= yo + STAGE2_BTN_Y && my < yo + STAGE2_BTN_Y + STAGE_BTN_H) {
                    this.menu.clickMenuButton(this.minecraft.player, 1);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
                if (my >= yo + STAGE1_BTN_Y && my < yo + STAGE1_BTN_Y + STAGE_BTN_H) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int xo = this.leftPos;
        int yo = this.topPos;

        // Crude Oil Tank Hover (x: 11..27, y: 19..51)
        if (mouseX >= xo + 11 && mouseX <= xo + 27 && mouseY >= yo + 19 && mouseY <= yo + 51) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Crude Oil Feed Tank").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getCrudeOil())).withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumes 1,000 mB Crude Oil per batch").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Heat Thermometer Hover (x: 31..47, y: 19..51)
        else if (mouseX >= xo + 31 && mouseX <= xo + 47 && mouseY >= yo + 19 && mouseY <= yo + 51) {
            int heat = this.menu.getCurrentHeat();
            int fType = this.menu.getFurnaceType();
            String furnaceName = switch (fType) {
                case 1 -> "Burner Refinery Furnace";
                case 2 -> "Electric Refinery Furnace";
                default -> "None (Place Furnace next to Stage 1)";
            };
            boolean hotEnough = heat >= IRefineryFurnace.MIN_OPERATING_TEMP;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Tower Heat Input").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("Temperature: %d °C", heat))
                            .withStyle(hotEnough ? ChatFormatting.GREEN : ChatFormatting.RED),
                    Component.literal("Required: >= 350 °C (Green Line)").withStyle(ChatFormatting.GRAY),
                    Component.literal("Heat Source: " + furnaceName).withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }
        // Stage Buttons Hover (x: 56..80, y: 19..71)
        else if (mouseX >= xo + STAGE_BTN_X && mouseX <= xo + STAGE_BTN_X + STAGE_BTN_W
                && mouseY >= yo + STAGE3_BTN_Y && mouseY <= yo + STAGE1_BTN_Y + STAGE_BTN_H) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Fractional Distillation Stages").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            lines.add(Component.literal("Stage 3 (Top - Gasoline): " + (this.menu.isStage3Enabled() ? "ENABLED" : "DISABLED"))
                    .withStyle(this.menu.isStage3Enabled() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Stage 2 (Mid - Diesel): " + (this.menu.isStage2Enabled() ? "ENABLED" : "DISABLED"))
                    .withStyle(this.menu.isStage2Enabled() ? ChatFormatting.YELLOW : ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Stage 1 (Bot - Liquid Tar): " + (this.menu.isStage1Enabled() ? "ENABLED" : "DISABLED"))
                    .withStyle(this.menu.isStage1Enabled() ? ChatFormatting.RED : ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Active Yield (per 1,000 mB Crude):").withStyle(ChatFormatting.WHITE));
            lines.add(Component.literal("  " + getYieldSummary()).withStyle(ChatFormatting.GREEN));
            lines.add(Component.literal("Click to toggle stage ON/OFF").withStyle(ChatFormatting.DARK_GRAY));
            graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
        // Liquid Tar Tank Hover (x: 83..95, y: 19..71)
        else if (mouseX >= xo + 83 && mouseX <= xo + 95 && mouseY >= yo + 19 && mouseY <= yo + 71) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Stage 1: Liquid Tar Tank").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getLiquidTar())).withStyle(ChatFormatting.GRAY),
                    Component.literal("Extract via Tar Port / Byproduct Port").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Diesel Tank Hover (x: 98..110, y: 19..71)
        else if (mouseX >= xo + 98 && mouseX <= xo + 110 && mouseY >= yo + 19 && mouseY <= yo + 71) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Stage 2: Diesel Tank").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getDiesel())).withStyle(ChatFormatting.YELLOW),
                    Component.literal("Extract via Diesel Port").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Gasoline Tank Hover (x: 113..125, y: 19..71)
        else if (mouseX >= xo + 113 && mouseX <= xo + 125 && mouseY >= yo + 19 && mouseY <= yo + 71) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Stage 3: Gasoline Tank").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d / 16,000 mB", this.menu.getGasoline())).withStyle(ChatFormatting.AQUA),
                    Component.literal("Extract via Gas Port").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }
        // Multiblock Status Hover (x: 160..170, y: 4..14)
        else if (mouseX >= xo + 160 && mouseX <= xo + 170 && mouseY >= yo + 4 && mouseY <= yo + 14) {
            boolean assembled = this.menu.isAssembled();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("3-Stage Refinery Tower (3x9x3)").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    assembled
                            ? Component.literal("Structure: 81/81 ONLINE (3x 3x3x3 Stages)").withStyle(ChatFormatting.GREEN)
                            : Component.literal("Structure: INCOMPLETE").withStyle(ChatFormatting.RED)
            ), mouseX, mouseY);
        }
    }

    private String getYieldSummary() {
        boolean s1 = this.menu.isStage1Enabled();
        boolean s2 = this.menu.isStage2Enabled();
        boolean s3 = this.menu.isStage3Enabled();
        if (s1 && !s2 && !s3) return "500 mB Liquid Tar + 1 Bitumen";
        if (!s1 && s2 && !s3) return "500 mB Diesel";
        if (!s1 && !s2 && s3) return "500 mB Gasoline";
        if (s1 && s2 && !s3) return "250 mB Tar + 250 mB Diesel + 1 Bitumen";
        if (s1 && !s2 && s3) return "250 mB Tar + 250 mB Gasoline + 1 Bitumen";
        if (!s1 && s2 && s3) return "250 mB Diesel + 250 mB Gasoline";
        if (s1 && s2 && s3) return "250 mB Tar + 250 mB Diesel + 250 mB Gas + 25% Bitumen";
        return "All stages disabled (Idle)";
    }
}
