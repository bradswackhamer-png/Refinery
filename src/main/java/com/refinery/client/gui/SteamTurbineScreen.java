package com.refinery.client.gui;

import com.refinery.menu.SteamTurbineMenu;
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
public class SteamTurbineScreen extends AbstractContainerScreen<SteamTurbineMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/steam_turbine.png");

    public SteamTurbineScreen(SteamTurbineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean assembled = this.menu.isAssembled();
        boolean active = this.menu.isActive();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0x882222;
        } else if (active) {
            statusText = Component.literal("SPINNING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0x228822;
        }

        graphics.text(this.font, statusText, 110, 6, statusColor, false);
        graphics.text(this.font, Component.literal("STEAM").withStyle(ChatFormatting.DARK_GRAY), 22, 10, 0x444444, false);
        graphics.text(this.font, Component.literal("WATER").withStyle(ChatFormatting.DARK_GRAY), 66, 10, 0x444444, false);
        graphics.text(this.font, Component.literal("POWER").withStyle(ChatFormatting.DARK_GRAY), 120, 10, 0x444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Steam Tank (x: 24, y: 20, w: 16, h: 48)
        int steam = this.menu.getSteamAmount();
        if (steam > 0) {
            int steamHeight = Math.min(48, (int) ((steam / 32000.0f) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 24, yo + 20 + (48 - steamHeight), xo + 24 + 16, yo + 20 + 48, 0xFFBAE6FD);
        }

        // 2. Water Tank (x: 68, y: 20, w: 16, h: 48)
        int water = this.menu.getWaterAmount();
        if (water > 0) {
            int waterHeight = Math.min(48, (int) ((water / 16000.0f) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 68, yo + 20 + (48 - waterHeight), xo + 68 + 16, yo + 20 + 48, 0xFF2563EB);
        }

        // 3. Energy Bar (x: 120, y: 20, w: 24, h: 48)
        int energy = this.menu.getEnergyStored();
        if (energy > 0) {
            int energyHeight = Math.min(48, (int) ((energy / 1000000.0f) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 120, yo + 20 + (48 - energyHeight), xo + 120 + 24, yo + 20 + 48, 0xFFEAB308);
        }

        // 4. Center Rotor Spinning Indicator (x: 94, y: 38, w: 16, h: 16)
        if (this.menu.isActive()) {
            graphics.fill(RenderPipelines.GUI, xo + 94, yo + 38, xo + 110, yo + 54, 0xFFA855F7);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Steam Tank Hover
        if (mouseX >= xo + 24 && mouseX <= xo + 40 && mouseY >= yo + 20 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High Pressure Steam Buffer").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(this.menu.getSteamAmount() + " / 32,000 mB").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Water Tank Hover
        if (mouseX >= xo + 68 && mouseX <= xo + 84 && mouseY >= yo + 20 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Condensed Recycled Water").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal(this.menu.getWaterAmount() + " / 16,000 mB").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Energy Bar Hover
        if (mouseX >= xo + 120 && mouseX <= xo + 144 && mouseY >= yo + 20 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Internal Energy Battery").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergyStored() + " / 1,000,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Output: Up to 10,000 FE/t").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }
    }
}
