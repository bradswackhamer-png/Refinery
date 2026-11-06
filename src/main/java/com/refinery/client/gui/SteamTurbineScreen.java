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
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 73;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Section Headers (inside sub-panel frames at y: 18, well clear of title at y: 6)
        graphics.text(this.font, Component.literal("STEAM"), 12, 18, 0xFF38BDF8, false);
        graphics.text(this.font, Component.literal("WATER"), 40, 18, 0xFF60A5FA, false);
        graphics.text(this.font, Component.literal("POWER"), 136, 18, 0xFFF59E0B, false);

        // Center Digital Diagnostic Monitor (inside monitor well x: 77..123, y: 21..67)
        boolean assembled = this.menu.isAssembled();
        boolean active = this.menu.isActive();

        if (active) {
            graphics.text(this.font, "ONLINE", 83, 24, 0xFF22C55E, false);
            graphics.text(this.font, "1800 RPM", 79, 36, 0xFFFBBF24, false);
            graphics.text(this.font, "+1000 FE", 80, 48, 0xFFF59E0B, false);
        } else if (!assembled) {
            graphics.text(this.font, "UNFORMED", 79, 32, 0xFFEF4444, false);
            graphics.text(this.font, "INCOMPLETE", 78, 44, 0xFF71717A, false);
        } else {
            graphics.text(this.font, "STANDBY", 81, 26, 0xFF38BDF8, false);
            graphics.text(this.font, "0 RPM", 86, 38, 0xFF71717A, false);
            graphics.text(this.font, "0 FE/t", 86, 49, 0xFF71717A, false);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // 1. Base Industrial Steel GUI background (176x166)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 2. Multiblock Assembly Status LED (x: 162, y: 6, w: 6, h: 6)
        boolean assembled = this.menu.isAssembled();
        graphics.fill(RenderPipelines.GUI, xo + 162, yo + 6, xo + 168, yo + 12, assembled ? 0xFF22C55E : 0xFF991B1B);
        graphics.fill(RenderPipelines.GUI, xo + 163, yo + 7, xo + 165, yo + 9, assembled ? 0xFF86EFAC : 0xFFDC2626);

        // 3. Steam Buffer Tank (well interior x: 14, y: 28, w: 20, h: 38)
        int steam = this.menu.getSteamAmount();
        if (steam > 0) {
            int fillH = Math.min(38, (int) ((steam / 32000.0f) * 38));
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 28 + (38 - fillH), xo + 14 + 20, yo + 28 + 38, 0xFF38BDF8);
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 28 + (38 - fillH), xo + 14 + 20, yo + 28 + (38 - fillH) + 1, 0xFFBAE6FD);
        }

        // 4. Recycled Water Tank (well interior x: 42, y: 28, w: 20, h: 38)
        int water = this.menu.getWaterAmount();
        if (water > 0) {
            int fillH = Math.min(38, (int) ((water / 16000.0f) * 38));
            graphics.fill(RenderPipelines.GUI, xo + 42, yo + 28 + (38 - fillH), xo + 42 + 20, yo + 28 + 38, 0xFF2563EB);
            graphics.fill(RenderPipelines.GUI, xo + 42, yo + 28 + (38 - fillH), xo + 42 + 20, yo + 28 + (38 - fillH) + 1, 0xFF60A5FA);
        }

        // 5. Energy Storage Battery (well interior x: 139, y: 28, w: 22, h: 38)
        int energy = this.menu.getEnergyStored();
        if (energy > 0) {
            int fillH = Math.min(38, (int) ((energy / 1000000.0f) * 38));
            graphics.fill(RenderPipelines.GUI, xo + 139, yo + 28 + (38 - fillH), xo + 139 + 22, yo + 28 + 38, 0xFFEAB308);
            graphics.fill(RenderPipelines.GUI, xo + 139, yo + 28 + (38 - fillH), xo + 139 + 22, yo + 28 + (38 - fillH) + 1, 0xFFFEF08A);
        }

        // 6. Active Rotating Turbine Blade Indicator inside Monitor (x: 82..118, y: 58..62)
        if (this.menu.isActive()) {
            long tick = System.currentTimeMillis() / 150;
            int phase = (int) (tick % 4);
            int barX = xo + 82 + phase * 9;
            graphics.fill(RenderPipelines.GUI, xo + 82, yo + 59, xo + 118, yo + 61, 0xFF1E293B);
            graphics.fill(RenderPipelines.GUI, barX, yo + 58, barX + 8, yo + 62, 0xFF38BDF8);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Multiblock Assembly LED (x: 160..170, y: 4..14)
        if (mouseX >= xo + 160 && mouseX <= xo + 170 && mouseY >= yo + 4 && mouseY <= yo + 14) {
            boolean assembled = this.menu.isAssembled();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("3x3x3 Steam Turbine Structure").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    assembled
                            ? Component.literal("Rotor & Shell: 27/27 ONLINE").withStyle(ChatFormatting.GREEN)
                            : Component.literal("Structure: INCOMPLETE (Shift-Right-Click with Wrench)").withStyle(ChatFormatting.RED),
                    Component.literal("Rankine Closed-Loop Cycle Supported").withStyle(ChatFormatting.AQUA)
            ), mouseX, mouseY);
            return;
        }

        // Steam Tank Hover (x: 13..35, y: 27..67)
        if (mouseX >= xo + 13 && mouseX <= xo + 35 && mouseY >= yo + 27 && mouseY <= yo + 67) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("High Pressure Steam Buffer").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(this.menu.getSteamAmount() + " / 32,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Inflow: Connect to Steam Port").withStyle(ChatFormatting.DARK_AQUA),
                    Component.literal("Consumes up to 100 mB/t at peak load").withStyle(ChatFormatting.WHITE)
            ), mouseX, mouseY);
            return;
        }

        // Water Tank Hover (x: 41..63, y: 27..67)
        if (mouseX >= xo + 41 && mouseX <= xo + 63 && mouseY >= yo + 27 && mouseY <= yo + 67) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Condensed Recycled Water").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal(this.menu.getWaterAmount() + " / 16,000 mB").withStyle(ChatFormatting.GRAY),
                    Component.literal("Closed-Loop Rankine Cycle:").withStyle(ChatFormatting.DARK_BLUE),
                    Component.literal("Condenses 50% of exhaust steam back into pure water").withStyle(ChatFormatting.GRAY),
                    Component.literal("Pipe directly back into Boiler Water Input!").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
            return;
        }

        // Center Monitor / Rotor Hover (x: 73..127, y: 17..71)
        if (mouseX >= xo + 73 && mouseX <= xo + 127 && mouseY >= yo + 17 && mouseY <= yo + 71) {
            boolean active = this.menu.isActive();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Steam Expansion Turbine").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                    Component.literal("Turbine Speed: " + (active ? "1,800 RPM" : "0 RPM (Idle)")).withStyle(active ? ChatFormatting.YELLOW : ChatFormatting.GRAY),
                    Component.literal("Thermodynamic Expansion: ACTIVE").withStyle(active ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY),
                    Component.literal("Converts enthalpy to rotational alternator power").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Energy Bar Hover (x: 137..162, y: 27..67)
        if (mouseX >= xo + 137 && mouseX <= xo + 162 && mouseY >= yo + 27 && mouseY <= yo + 67) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Internal Energy Battery").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(this.menu.getEnergyStored() + " / 1,000,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Generation: 1,000 FE/t").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Max Output: 10,000 FE/t").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }
    }
}
