package com.refinery.client.gui;

import com.refinery.block.entity.MetalPressControllerBlockEntity;
import com.refinery.menu.MetalPressMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MetalPressScreen extends AbstractContainerScreen<MetalPressMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/metal_press.png");

    public MetalPressScreen(MetalPressMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(this.font, Component.literal("INPUT").withStyle(ChatFormatting.DARK_GRAY), 44, 22, 0xFF444444, false);
        graphics.text(this.font, Component.literal("OUTPUT").withStyle(ChatFormatting.DARK_GRAY), 116, 22, 0xFF444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar (well x: 13..28, interior x: 14, y: 18, w: 14, h: 50)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(50, (int) ((energy / (float) MetalPressControllerBlockEntity.ENERGY_CAPACITY) * 50));
            graphics.fill(RenderPipelines.GUI, xo + 14, yo + 18 + (50 - energyHeight), xo + 14 + 14, yo + 18 + 50, 0xFFEAB308);
        }

        // 2. Stamping Ram Progress Indicator (x: 88, y: 28, w: 18, h: 26)
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (progress > 0 && maxProgress > 0) {
            // Down and up motion representation
            float normalized = progress <= 20 ? (progress / 20.0f) : (1.0f - ((progress - 20) / 20.0f));
            int stroke = (int) (normalized * 18);
            // Stamping hammer head moving down
            graphics.fill(RenderPipelines.GUI, xo + 89, yo + 28 + stroke, xo + 105, yo + 34 + stroke, 0xFF475569);
            graphics.fill(RenderPipelines.GUI, xo + 95, yo + 28, xo + 99, yo + 28 + stroke, 0xFF94A3B8);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Energy Bar Hover (x: 13..28, y: 17..68)
        if (mouseX >= xo + 13 && mouseX <= xo + 28 && mouseY >= yo + 17 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d", this.menu.getEnergy()) + " / " + String.format("%,d", MetalPressControllerBlockEntity.ENERGY_CAPACITY) + " FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Consumption: 500 FE/t while stamping").withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }

        // Die Slot Hover (x: 88..106, y: 57..75) when empty
        if (mouseX >= xo + 88 && mouseX <= xo + 106 && mouseY >= yo + 57 && mouseY <= yo + 75) {
            ItemStack die = this.menu.getDie();
            if (die.isEmpty()) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("Stamping Die Slot").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Insert a Plate, Gear, Corrugated, or Rail Die").withStyle(ChatFormatting.GRAY),
                        Component.literal("Stamping requires an installed die!").withStyle(ChatFormatting.RED)
                ), mouseX, mouseY);
            }
        }

        // Press Ram Hover (x: 84..108, y: 26..56)
        if (mouseX >= xo + 84 && mouseX <= xo + 108 && mouseY >= yo + 26 && mouseY <= yo + 56) {
            int progress = this.menu.getProgress();
            int pct = progress * 100 / 40;
            ItemStack die = this.menu.getDie();
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Hydraulic Metal Press").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            if (die.isEmpty()) {
                lines.add(Component.literal("Requires a Stamping Die!").withStyle(ChatFormatting.RED));
            } else {
                lines.add(Component.literal("Installed Die: ").withStyle(ChatFormatting.GRAY)
                        .append(die.getHoverName().copy().withStyle(ChatFormatting.AQUA)));
            }
            lines.add(Component.literal(this.menu.isPressing() ? ("Cycle Progress: " + pct + "%") : "Status: Idle").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }
}
