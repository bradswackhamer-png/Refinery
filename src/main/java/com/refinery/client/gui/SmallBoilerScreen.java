package com.refinery.client.gui;

import com.refinery.menu.SmallBoilerMenu;
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
public class SmallBoilerScreen extends AbstractContainerScreen<SmallBoilerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/small_boiler.png");

    public SmallBoilerScreen(SmallBoilerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 73;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Status Header
        boolean assembled = this.menu.isAssembled();
        boolean burning = this.menu.isBurning();
        int overheat = this.menu.getOverheatTicks();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0x882222;
        } else if (overheat > 0) {
            statusText = Component.literal("OVERHEATING!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xEF4444;
        } else if (burning) {
            statusText = Component.literal("BURNING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 102, 6, statusColor, false);
        graphics.text(this.font, Component.literal("HEARTH").withStyle(ChatFormatting.DARK_GRAY), 72, 16, 0x444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base Industrial GUI background (176x166)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Water Tank Fill (x: 22, y: 16, w: 16, h: 52)
        int water = this.menu.getWaterAmount();
        if (water > 0) {
            int waterHeight = Math.min(52, (int) ((water / 16000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 22, yo + 16 + (52 - waterHeight), xo + 22 + 16, yo + 16 + 52, 0xFF2563EB);
            // Highlight meniscus line on top of water
            graphics.fill(RenderPipelines.GUI, xo + 22, yo + 16 + (52 - waterHeight), xo + 22 + 16, yo + 16 + (52 - waterHeight) + 1, 0xFF60A5FA);
        }

        // 2. Steam Tank Fill (x: 138, y: 16, w: 16, h: 52)
        int steam = this.menu.getSteamAmount();
        if (steam > 0) {
            int steamHeight = Math.min(52, (int) ((steam / 16000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 138, yo + 16 + (52 - steamHeight), xo + 138 + 16, yo + 16 + 52, 0xFF38BDF8);
            // Highlight meniscus line
            graphics.fill(RenderPipelines.GUI, xo + 138, yo + 16 + (52 - steamHeight), xo + 138 + 16, yo + 16 + (52 - steamHeight) + 1, 0xFFBAE6FD);
        }

        // 3. Redstone Indicator Lamp (x: 50, y: 34, w: 10, h: 10)
        boolean hasRs = this.menu.hasRedstone();
        int rsColor = hasRs ? 0xFF22C55E : 0xFF7F1D1D;
        graphics.fill(RenderPipelines.GUI, xo + 50, yo + 34, xo + 60, yo + 44, rsColor);
        if (hasRs) {
            // Bright center filament
            graphics.fill(RenderPipelines.GUI, xo + 53, yo + 37, xo + 57, yo + 41, 0xFF86EFAC);
        }

        // 4. Firebox Combustion Viewport (x: 115, y: 34, w: 10, h: 10)
        if (this.menu.isBurning()) {
            graphics.fill(RenderPipelines.GUI, xo + 115, yo + 34, xo + 125, yo + 44, 0xFFEA580C);
            // Dynamic flickering core
            graphics.fill(RenderPipelines.GUI, xo + 117, yo + 36, xo + 123, yo + 42, 0xFFFDE047);
        } else {
            graphics.fill(RenderPipelines.GUI, xo + 115, yo + 34, xo + 125, yo + 44, 0xFF1E293B);
        }

        // 5. Overheat / Meltdown Warning Bar (x: 48, y: 65, w: 80, h: 6)
        int overheat = this.menu.getOverheatTicks();
        if (overheat > 0) {
            int fillW = Math.min(80, (int) ((overheat / 1200.0f) * 80));
            int warnColor = (overheat > 600) ? 0xFFDC2626 : 0xFFF59E0B;
            graphics.fill(RenderPipelines.GUI, xo + 48, yo + 65, xo + 48 + fillW, yo + 71, warnColor);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Water Tank Hover (x: 21..39, y: 15..69)
        if (mouseX >= xo + 21 && mouseX <= xo + 39 && mouseY >= yo + 15 && mouseY <= yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Water Buffer").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal(this.menu.getWaterAmount() + " / 16,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 50 mB / operation").withStyle(ChatFormatting.DARK_AQUA)
            ), mouseX, mouseY);
        }

        // Steam Tank Hover (x: 137..155, y: 15..69)
        if (mouseX >= xo + 137 && mouseX <= xo + 155 && mouseY >= yo + 15 && mouseY <= yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High Pressure Steam").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(this.menu.getSteamAmount() + " / 16,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Production: 100 mB / operation").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // Redstone Lamp Hover (x: 49..61, y: 33..45)
        if (mouseX >= xo + 49 && mouseX <= xo + 61 && mouseY >= yo + 33 && mouseY <= yo + 45) {
            boolean rs = this.menu.hasRedstone();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Signal: " + (rs ? "POWERED" : "OFF"))
                            .withStyle(rs ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Boiler requires redstone power to fire burner.").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Firebox Flame Viewport Hover (x: 114..126, y: 33..45)
        if (mouseX >= xo + 114 && mouseX <= xo + 126 && mouseY >= yo + 33 && mouseY <= yo + 45) {
            boolean burning = this.menu.isBurning();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Combustion State: " + (burning ? "ACTIVE" : "STANDBY"))
                            .withStyle(burning ? ChatFormatting.GOLD : ChatFormatting.GRAY, ChatFormatting.BOLD),
                    Component.literal("Remaining Burn: " + this.menu.getBurnTime() + " ticks").withStyle(ChatFormatting.DARK_GRAY),
                    Component.literal("Fuel Buffer: 4 slots").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
        }

        // Overheat Warning Hover (x: 47..129, y: 64..72)
        if (this.menu.getOverheatTicks() > 0 && mouseX >= xo + 47 && mouseX <= xo + 129 && mouseY >= yo + 64 && mouseY <= yo + 72) {
            int ticksLeft = 1200 - this.menu.getOverheatTicks();
            int secondsLeft = Math.max(0, ticksLeft / 20);
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("DANGER: DRY BOILER OVERHEAT!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Running dry without water: " + this.menu.getOverheatTicks() + " / 1200 ticks").withStyle(ChatFormatting.GOLD),
                    Component.literal("EXPLOSION IN: " + secondsLeft + "s! ADD WATER IMMEDIATELY!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
            ), mouseX, mouseY);
        }
    }
}
