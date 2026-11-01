package com.refinery.client.gui;

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
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/metal_press.png");

    public CastingPressScreen(CastingPressMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 74;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        boolean assembled = this.menu.isAssembled();
        boolean casting = this.menu.isCasting();
        int energy = this.menu.getEnergy();

        Component statusText;
        int statusColor;

        if (!assembled) {
            statusText = Component.literal("UNFORMED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
            statusColor = 0xFF882222;
        } else if (energy < 40) {
            statusText = Component.literal("NO POWER").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            statusColor = 0xFFEF4444;
        } else if (casting) {
            statusText = Component.literal("CASTING").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            statusColor = 0xFFEA580C;
        } else {
            statusText = Component.literal("ONLINE").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
            statusColor = 0xFF228822;
        }

        graphics.text(this.font, statusText, 102, 6, statusColor, false);
        graphics.text(this.font, Component.literal("MOLD BED").withStyle(ChatFormatting.DARK_GRAY), 44, 14, 0xFF444444, false);
        graphics.text(this.font, Component.literal("CAST PRODUCT").withStyle(ChatFormatting.DARK_GRAY), 108, 14, 0xFF444444, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Energy Bar (x: 12, y: 18, width: 12, height: 50)
        int energy = this.menu.getEnergy();
        int maxEnergy = 50000;
        int energyH = Math.min(50, (int) ((long) energy * 50 / maxEnergy));
        if (energyH > 0) {
            graphics.fill(RenderPipelines.GUI, xo + 12, yo + 18 + (50 - energyH), xo + 24, yo + 68, 0xFFDC2626);
        }

        // Molten Mold Inflow Tank (x: 44, y: 26, width: 34, height: 34)
        int amount = this.menu.getMoltenAmount();
        int capacity = this.menu.getTankCapacity();
        if (amount > 0 && capacity > 0) {
            int fluidH = Math.min(34, (int) ((long) amount * 34 / capacity));
            graphics.fill(RenderPipelines.GUI, xo + 44, yo + 26 + (34 - fluidH), xo + 78, yo + 60, 0xFFEA580C);
        }

        // Progress Arrow (x: 84, y: 35, width: 22, height: 16)
        if (this.menu.isCasting()) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            int arrowW = maxProgress > 0 ? (progress * 22 / maxProgress) : 0;
            if (arrowW > 0) {
                graphics.fill(RenderPipelines.GUI, xo + 82, yo + 38, xo + 82 + (arrowW * 20 / 22), yo + 44, 0xFFF59E0B);
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

        // Tooltips
        if (mouseX >= xo + 12 && mouseX <= xo + 24 && mouseY >= yo + 18 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§cEnergy Storage:"),
                    Component.literal("§7" + String.format("%,d", energy) + " / 50,000 FE")
            ), mouseX, mouseY);
        }

        if (mouseX >= xo + 44 && mouseX <= xo + 78 && mouseY >= yo + 26 && mouseY <= yo + 60) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("§6Casting Inflow Basin:"),
                    Component.literal("§e" + String.format("%,d", amount) + " / " + String.format("%,d", capacity) + " mB")
            ), mouseX, mouseY);
        }
    }
}
