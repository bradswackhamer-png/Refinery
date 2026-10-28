package com.refinery.client.gui;

import com.refinery.menu.LargeDieselBoilerMenu;
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
public class LargeDieselBoilerScreen extends AbstractContainerScreen<LargeDieselBoilerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/large_diesel_boiler.png");

    public LargeDieselBoilerScreen(LargeDieselBoilerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

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
            statusText = Component.literal("FIRING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xEA580C;
        } else {
            statusText = Component.literal("IDLE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 106, 6, statusColor, false);
        graphics.text(this.font, Component.literal("HEAVY DIESEL").withStyle(ChatFormatting.GOLD), 14, 6, 0xD97706, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Diesel Fuel Tank (x: 18, y: 16, w: 16, h: 52)
        int diesel = this.menu.getDieselAmount();
        if (diesel > 0) {
            int dieselHeight = Math.min(52, (int) ((diesel / 64000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 16 + (52 - dieselHeight), xo + 18 + 16, yo + 16 + 52, 0xFFF5CF47);
        }

        // 2. Water Tank (x: 44, y: 16, w: 16, h: 52)
        int water = this.menu.getWaterAmount();
        if (water > 0) {
            int waterHeight = Math.min(52, (int) ((water / 64000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 44, yo + 16 + (52 - waterHeight), xo + 44 + 16, yo + 16 + 52, 0xFF2563EB);
        }

        // 3. Steam Tank (x: 142, y: 16, w: 16, h: 52)
        int steam = this.menu.getSteamAmount();
        if (steam > 0) {
            int steamHeight = Math.min(52, (int) ((steam / 64000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 142, yo + 16 + (52 - steamHeight), xo + 142 + 16, yo + 16 + 52, 0xFFBAE6FD);
        }

        // 4. Dual Burner Flames at (94, 27, 12, 10) and (94, 45, 12, 10)
        if (this.menu.isBurning()) {
            graphics.fill(RenderPipelines.GUI, xo + 94, yo + 27, xo + 106, yo + 37, 0xFFF97316);
            graphics.fill(RenderPipelines.GUI, xo + 97, yo + 30, xo + 103, yo + 35, 0xFFFDEE87);

            graphics.fill(RenderPipelines.GUI, xo + 94, yo + 45, xo + 106, yo + 55, 0xFFF97316);
            graphics.fill(RenderPipelines.GUI, xo + 97, yo + 48, xo + 103, yo + 53, 0xFFFDEE87);
        }

        // 5. Overheat Bar (x: 67, y: 69, w: 68, h: 4)
        int overheat = this.menu.getOverheatTicks();
        if (overheat > 0) {
            int fillW = Math.min(68, (int) ((overheat / 1200.0f) * 68));
            int warnColor = (overheat > 600) ? 0xFFDC2626 : 0xFFF59E0B;
            graphics.fill(RenderPipelines.GUI, xo + 67, yo + 69, xo + 67 + fillW, yo + 73, warnColor);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Diesel Tank Hover (x: 18..34, y: 16..68)
        if (mouseX >= xo + 18 && mouseX <= xo + 34 && mouseY >= yo + 16 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Heavy Diesel Tank").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getDieselAmount() + " / 64,000 mB").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Consumption: 25 mB per cycle").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Water Tank Hover (x: 44..60, y: 16..68)
        if (mouseX >= xo + 44 && mouseX <= xo + 60 && mouseY >= yo + 16 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Water Buffer").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal(this.menu.getWaterAmount() + " / 64,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 250 mB per cycle").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Steam Tank Hover (x: 142..158, y: 16..68)
        if (mouseX >= xo + 142 && mouseX <= xo + 158 && mouseY >= yo + 16 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High Pressure Steam").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(this.menu.getSteamAmount() + " / 64,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Production: 650 mB per cycle").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // Overheat Hover (x: 66..136, y: 68..74)
        if (this.menu.getOverheatTicks() > 0 && mouseX >= xo + 66 && mouseX <= xo + 136 && mouseY >= yo + 68 && mouseY <= yo + 74) {
            int ticksLeft = 1200 - this.menu.getOverheatTicks();
            int secondsLeft = Math.max(0, ticksLeft / 20);
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("DANGER: DRY BOILER OVERHEAT!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Running dry: " + this.menu.getOverheatTicks() + " / 1200 ticks").withStyle(ChatFormatting.GOLD),
                    Component.literal("CATASTROPHIC EXPLOSION IN: " + secondsLeft + "s!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
            ), mouseX, mouseY);
        }
    }
}
