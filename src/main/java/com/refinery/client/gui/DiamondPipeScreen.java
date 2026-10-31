package com.refinery.client.gui;

import com.refinery.menu.DiamondPipeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class DiamondPipeScreen extends AbstractContainerScreen<DiamondPipeMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");

    private static final int[] ROW_COLORS = {
            0xFF22C55E, // DOWN  - Green
            0xFFEAB308, // UP    - Yellow
            0xFF3B82F6, // NORTH - Blue
            0xFFEF4444, // SOUTH - Red
            0xFF6B7280, // WEST  - Dark Silver
            0xFFF3F4F6  // EAST  - White
    };

    public DiamondPipeScreen(DiamondPipeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 222);
        this.inventoryLabelY = 222 - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = this.leftPos;
        int top = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, left, top, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Direction indicator color stripes along the slot rows
        for (int row = 0; row < 6; row++) {
            int y = top + 18 + row * 18;
            int color = ROW_COLORS[row];
            graphics.fill(RenderPipelines.GUI, left + 6, y, left + 7, y + 16, color);
            graphics.fill(RenderPipelines.GUI, left + 169, y, left + 170, y + 16, color);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
    }
}
