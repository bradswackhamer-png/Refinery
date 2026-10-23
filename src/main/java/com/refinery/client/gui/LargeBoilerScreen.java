package com.refinery.client.gui;

import com.refinery.menu.LargeBoilerMenu;
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
public class LargeBoilerScreen extends AbstractContainerScreen<LargeBoilerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/large_boiler.png");

    public LargeBoilerScreen(LargeBoilerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
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
            statusText = Component.literal("IDLE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 102, 6, statusColor, false);
        graphics.text(this.font, Component.literal("HEAVY FUEL").withStyle(ChatFormatting.DARK_GRAY), 62, 15, 0x444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Water Tank Fill (x: 18, y: 16, w: 16, h: 52)
        int water = this.menu.getWaterAmount();
        if (water > 0) {
            int waterHeight = Math.min(52, (int) ((water / 64000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 18, yo + 16 + (52 - waterHeight), xo + 18 + 16, yo + 16 + 52, 0xFF2563EB);
        }

        // 2. Steam Tank Fill (x: 142, y: 16, w: 16, h: 52)
        int steam = this.menu.getSteamAmount();
        if (steam > 0) {
            int steamHeight = Math.min(52, (int) ((steam / 64000.0f) * 52));
            graphics.fill(RenderPipelines.GUI, xo + 142, yo + 16 + (52 - steamHeight), xo + 142 + 16, yo + 16 + 52, 0xFFBAE6FD);
        }

        // 3. Redstone Indicator (x: 38, y: 34, w: 10, h: 10)
        boolean hasRs = this.menu.hasRedstone();
        int rsColor = hasRs ? 0xFF22C55E : 0xFFEF4444;
        graphics.fill(RenderPipelines.GUI, xo + 38, yo + 34, xo + 48, yo + 44, rsColor);

        // 4. Fire/Burn Indicator (x: 128, y: 34, w: 10, h: 10)
        if (this.menu.isBurning()) {
            graphics.fill(RenderPipelines.GUI, xo + 128, yo + 34, xo + 138, yo + 44, 0xFFF97316);
        }

        // 5. Overheat / Meltdown Warning Bar (x: 38, y: 64, w: 100, h: 6)
        int overheat = this.menu.getOverheatTicks();
        if (overheat > 0) {
            int fillW = Math.min(100, (int) ((overheat / 1200.0f) * 100));
            int warnColor = (overheat > 600) ? 0xFFDC2626 : 0xFFF59E0B;
            graphics.fill(RenderPipelines.GUI, xo + 38, yo + 64, xo + 38 + fillW, yo + 70, warnColor);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Water Tank Hover (x: 18..34, y: 16..68)
        if (mouseX >= xo + 18 && mouseX <= xo + 34 && mouseY >= yo + 16 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Water Buffer").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal(this.menu.getWaterAmount() + " / 64,000 mB").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Steam Tank Hover (x: 142..158, y: 16..68)
        if (mouseX >= xo + 142 && mouseX <= xo + 158 && mouseY >= yo + 16 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High Pressure Steam").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(this.menu.getSteamAmount() + " / 64,000 mB").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Redstone Hover (x: 38..48, y: 34..44)
        if (mouseX >= xo + 38 && mouseX <= xo + 48 && mouseY >= yo + 34 && mouseY <= yo + 44) {
            boolean rs = this.menu.hasRedstone();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Signal: " + (rs ? "POWERED" : "OFF")).withStyle(rs ? ChatFormatting.GREEN : ChatFormatting.RED),
                    Component.literal("Boiler requires redstone power to burn.").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Burn Flame Hover (x: 128..138, y: 34..44)
        if (mouseX >= xo + 128 && mouseX <= xo + 138 && mouseY >= yo + 34 && mouseY <= yo + 44) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Combustion State: " + (this.menu.isBurning() ? "ACTIVE" : "INACTIVE"))
                            .withStyle(this.menu.isBurning() ? ChatFormatting.GOLD : ChatFormatting.GRAY),
                    Component.literal("Remaining Burn: " + this.menu.getBurnTime() + " ticks").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Overheat Warning Hover (x: 38..138, y: 64..70)
        if (this.menu.getOverheatTicks() > 0 && mouseX >= xo + 38 && mouseX <= xo + 138 && mouseY >= yo + 64 && mouseY <= yo + 70) {
            int ticksLeft = 1200 - this.menu.getOverheatTicks();
            int secondsLeft = Math.max(0, ticksLeft / 20);
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("DANGER: DRY BOILER OVERHEAT!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Running dry without water: " + this.menu.getOverheatTicks() + " / 1200 ticks").withStyle(ChatFormatting.GOLD),
                    Component.literal("CATASTROPHIC EXPLOSION IN: " + secondsLeft + "s!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
            ), mouseX, mouseY);
        }
    }
}
