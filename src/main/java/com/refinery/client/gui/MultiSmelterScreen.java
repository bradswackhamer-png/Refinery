package com.refinery.client.gui;

import com.refinery.block.entity.MultiSmelterControllerBlockEntity;
import com.refinery.menu.MultiSmelterMenu;
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
public class MultiSmelterScreen extends AbstractContainerScreen<MultiSmelterMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/multi_smelter.png");

    public MultiSmelterScreen(MultiSmelterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (x: 10, y: 18, w: 12, h: 50)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(50, (int) ((energy / (float) MultiSmelterControllerBlockEntity.ENERGY_CAPACITY) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 10, yo + 18 + (50 - energyHeight), xo + 22, yo + 68, 0xFFEAB308);
            graphics.fill(RenderPipelines.GUI, xo + 10, yo + 18 + (50 - energyHeight), xo + 12, yo + 68, 0xFFFDE047);
        }

        // 2. Solid Fuel Flame Burn Indicator (x: 70, y: 63, w: 14, h: 14)
        int burnTime = this.menu.getBurnTime();
        int maxBurnTime = this.menu.getMaxBurnTime();
        if (burnTime > 0 && maxBurnTime > 0) {
            int flameH = Math.min(14, (int) (((float) burnTime / maxBurnTime) * 14));
            graphics.fill(RenderPipelines.GUI, xo + 70, yo + 63 + (14 - flameH), xo + 84, yo + 77, 0xFFEF4444);
            graphics.fill(RenderPipelines.GUI, xo + 72, yo + 63 + (14 - flameH), xo + 82, yo + 77, 0xFFF97316);
            graphics.fill(RenderPipelines.GUI, xo + 74, yo + 63 + (14 - flameH), xo + 80, yo + 77, 0xFFFDE047);
        }

        // 3. Electric Induction Heating Indicator (x: 110, y: 63, w: 14, h: 14)
        if (energy >= MultiSmelterControllerBlockEntity.ENERGY_PER_TICK && this.menu.isSmelting()) {
            graphics.fill(RenderPipelines.GUI, xo + 110, yo + 63, xo + 124, yo + 77, 0xFF0284C7);
            graphics.fill(RenderPipelines.GUI, xo + 112, yo + 65, xo + 122, yo + 75, 0xFF38BDF8);
            graphics.fill(RenderPipelines.GUI, xo + 115, yo + 67, xo + 119, yo + 73, 0xFFE0F2FE);
        }

        // 4. Parallel Cook Progress Bars (for each active input slot in dedicated recessed grooves)
        for (int i = 0; i < 8; i++) {
            int progress = this.menu.getCookProgress(i);
            if (progress > 0) {
                int col = i % 4;
                int row = i / 4;
                int slotX = xo + 27 + col * 18;
                int slotY = yo + (row == 0 ? 36 : 56);
                int progressWidth = Math.min(16, (int) (((float) progress / 100.0f) * 16));
                // Cook groove heat fill
                graphics.fill(RenderPipelines.GUI, slotX, slotY, slotX + progressWidth, slotY + 2, 0xFFF97316);
                graphics.fill(RenderPipelines.GUI, slotX, slotY, slotX + progressWidth, slotY + 1, 0xFFFDE047);
            }
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 9..23, y: 17..69)
        if (mouseX >= xo + 9 && mouseX <= xo + 23 && mouseY >= yo + 17 && mouseY <= yo + 69) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d", this.menu.getEnergy()) + " / " + String.format("%,d", MultiSmelterControllerBlockEntity.ENERGY_CAPACITY) + " FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 5,000 FE/t while electric smelting").withStyle(ChatFormatting.GREEN),
                    Component.literal("Dual-power: Runs on FE or Solid Fuel!").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Combustion Flame Hover (x: 69..85, y: 62..78)
        else if (mouseX >= xo + 69 && mouseX <= xo + 85 && mouseY >= yo + 62 && mouseY <= yo + 78) {
            int burnTime = this.menu.getBurnTime();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Combustion Firebox").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Burn Time: " + burnTime + " ticks").withStyle(ChatFormatting.GRAY),
                    Component.literal(burnTime > 0 ? "Status: Burning Solid Fuel" : "Status: Awaiting Fuel").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Powers all 8 smelting slots in parallel!").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Electric Induction Coil Hover (x: 109..125, y: 62..78)
        else if (mouseX >= xo + 109 && mouseX <= xo + 125 && mouseY >= yo + 62 && mouseY <= yo + 78) {
            boolean active = this.menu.getEnergy() >= MultiSmelterControllerBlockEntity.ENERGY_PER_TICK && this.menu.isSmelting();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Induction Heating Coils").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Status: " + (active ? "Active Electric Smelting" : "Standby")).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY),
                    Component.literal("Automatically activates when FE is available").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Inputs Panel Hover (x: 24..98, y: 16..60)
        else if (mouseX >= xo + 24 && mouseX <= xo + 98 && mouseY >= yo + 16 && mouseY <= yo + 60) {
            int activeSlots = Integer.bitCount(this.menu.getActiveSlotsMask());
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Smelting Hearth (8 Parallel Slots)").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Active Smelting Slots: " + activeSlots + " / 8").withStyle(ChatFormatting.GOLD),
                    Component.literal("Smelts any furnace recipe at high speed").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Outputs Panel Hover (x: 98..172, y: 16..60)
        else if (mouseX >= xo + 98 && mouseX <= xo + 172 && mouseY >= yo + 16 && mouseY <= yo + 60) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Smelted Product Storage").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Automated extraction via Smelter Output Ports").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }
}
