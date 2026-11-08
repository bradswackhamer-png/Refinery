package com.refinery.client.gui;

import com.refinery.block.entity.CastingPressControllerBlockEntity;
import com.refinery.menu.CastingPressMenu;
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
public class CastingPressScreen extends AbstractContainerScreen<CastingPressMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/industrial_grinder.png");

    public CastingPressScreen(CastingPressMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(this.font, Component.literal("FLUID INFLOW").withStyle(ChatFormatting.DARK_GRAY), 24, 14, 0xFF444444, false);
        graphics.text(this.font, Component.literal("CAST PRODUCTS").withStyle(ChatFormatting.DARK_GRAY), 104, 14, 0xFF444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (x: 14..28, y: 18..68, width: 14, height: 50)
        int energy = this.menu.getEnergy();
        int maxEnergy = CastingPressControllerBlockEntity.ENERGY_CAPACITY;
        int energyH = Math.min(50, (int) ((long) energy * 50 / maxEnergy));
        if (energyH > 0) {
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 18 + (50 - energyH), xo + 28, yo + 68, 0xFFDC2626);
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 18 + (50 - energyH), xo + 28, yo + 18 + (50 - energyH) + 2, 0xFFEF4444);
        }

        // 2a. Molten Inflow Tank Well & Gauge (x: 38..54, y: 26..62, width: 16, height: 36)
        graphics.fill(RenderPipelines.GUI, xo + 37, yo + 25, xo + 55, yo + 63, 0xFF14171A);
        int amount = this.menu.getMoltenAmount();
        int capacity = this.menu.getTankCapacity();
        if (amount > 0 && capacity > 0) {
            int fluidH = Math.min(36, (int) ((long) amount * 36 / capacity));
            graphics.fill(RenderPipelines.GUI, xo + 38, yo + 26 + (36 - fluidH), xo + 54, yo + 62, 0xFFEA580C);
            graphics.fill(RenderPipelines.GUI, xo + 38, yo + 26 + (36 - fluidH), xo + 54, yo + 26 + (36 - fluidH) + 2, 0xFFFBBF24);
        }

        // 2b. Quenching Water Tank Well & Gauge (x: 56..72, y: 26..62, width: 16, height: 36)
        graphics.fill(RenderPipelines.GUI, xo + 55, yo + 25, xo + 73, yo + 63, 0xFF14171A);
        graphics.fill(RenderPipelines.GUI, xo + 54, yo + 25, xo + 56, yo + 63, 0xFF2B3036);
        int waterAmount = this.menu.getWaterAmount();
        int waterCapacity = this.menu.getWaterCapacity();
        if (waterAmount > 0 && waterCapacity > 0) {
            int waterH = Math.min(36, (int) ((long) waterAmount * 36 / waterCapacity));
            graphics.fill(RenderPipelines.GUI, xo + 56, yo + 26 + (36 - waterH), xo + 72, yo + 62, 0xFF2563EB);
            graphics.fill(RenderPipelines.GUI, xo + 56, yo + 26 + (36 - waterH), xo + 72, yo + 26 + (36 - waterH) + 2, 0xFF60A5FA);
        }

        // 3. Casting Ram Stamping Progress Bar (x: 78..110, y: 34..50, width: 32, height: 16)
        if (this.menu.isCasting()) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            int fillW = maxProgress > 0 ? Math.min(32, (int) (((float) progress / maxProgress) * 32)) : 0;
            if (fillW > 0) {
                graphics.fill(RenderPipelines.GUI, xo + 78, yo + 34, xo + 78 + fillW, yo + 50, 0xFF38BDF8);
                graphics.fill(RenderPipelines.GUI, xo + 78, yo + 34, xo + 78 + fillW, yo + 36, 0xFFBAE6FD);
            }
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        int energy = this.menu.getEnergy();
        int amount = this.menu.getMoltenAmount();
        int capacity = this.menu.getTankCapacity();
        int waterAmount = this.menu.getWaterAmount();
        int waterCapacity = this.menu.getWaterCapacity();

        // 1. Energy Tooltip (x: 14..28, y: 18..68)
        if (mouseX >= xo + 14 && mouseX <= xo + 28 && mouseY >= yo + 18 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§cEnergy Storage:"),
                    Component.literal("§7" + String.format("%,d", energy) + " / " + String.format("%,d", CastingPressControllerBlockEntity.ENERGY_CAPACITY) + " FE"),
                    Component.literal("Consumption: 600 FE/t while stamping").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // 2a. Molten Inflow Basin Tooltip (x: 38..54, y: 26..62)
        if (mouseX >= xo + 38 && mouseX <= xo + 54 && mouseY >= yo + 26 && mouseY <= yo + 62) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§6Molten Inflow Basin:"),
                    Component.literal("§e" + String.format("%,d", amount) + " / " + String.format("%,d", capacity) + " mB"),
                    Component.literal("§7Consumes: 1,000 mB per cycle")
            ), mouseX, mouseY);
        }

        // 2b. Quenching Water Tank Tooltip (x: 56..72, y: 26..62)
        if (mouseX >= xo + 56 && mouseX <= xo + 72 && mouseY >= yo + 26 && mouseY <= yo + 62) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§9Quenching Water Tank:"),
                    Component.literal("§b" + String.format("%,d", waterAmount) + " / " + String.format("%,d", waterCapacity) + " mB"),
                    Component.literal("§7Consumes: 1,000 mB per cycle (1:1 with molten metal)")
            ), mouseX, mouseY);
        }

        // 3. Casting Progress Tooltip (x: 78..110, y: 34..50)
        if (mouseX >= xo + 78 && mouseX <= xo + 110 && mouseY >= yo + 34 && mouseY <= yo + 50) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            int pct = maxProgress > 0 ? (progress * 100 / maxProgress) : 0;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§bHydraulic Stamping:"),
                    Component.literal("§f" + pct + "% complete")
            ), mouseX, mouseY);
        }
    }
}
