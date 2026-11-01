package com.refinery.client.gui;

import com.refinery.client.renderer.IndustrialPipeModel;
import com.refinery.menu.DiamondPipeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class DiamondPipeScreen extends AbstractContainerScreen<DiamondPipeMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");

    private static final String[] ROW_LABELS = {
            "DOWN",
            "UP",
            "NORTH",
            "SOUTH",
            "WEST",
            "EAST"
    };

    private static final String[] COLOR_NAMES = {
            "Green",
            "Yellow",
            "Blue",
            "Red",
            "Purple",
            "White"
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

        Direction[] dirs = Direction.values();
        for (int row = 0; row < 6; row++) {
            int y = top + 17 + row * 18;
            int color = IndustrialPipeModel.getDiamondDirectionColor(dirs[row]);
            int rowTint = (color & 0x00FFFFFF) | 0x38000000;

            // Left & Right bold color-coded bands inside the container frame
            graphics.fill(RenderPipelines.GUI, left + 3, y + 1, left + 7, y + 17, color);
            graphics.fill(RenderPipelines.GUI, left + 169, y + 1, left + 173, y + 17, color);

            // Top & Bottom color-coded row border lines across all 9 slots
            graphics.fill(RenderPipelines.GUI, left + 7, y, left + 169, y + 1, color);
            graphics.fill(RenderPipelines.GUI, left + 7, y + 17, left + 169, y + 18, color);

            // Subtle row color tint over empty slot backgrounds
            for (int col = 0; col < 9; col++) {
                int sx = left + 8 + col * 18;
                int sy = y + 1;
                graphics.fill(RenderPipelines.GUI, sx, sy, sx + 16, sy + 16, rowTint);
            }

            // Left-side color-coded direction badge tab (left - 48 to left)
            int tabX = left - 48;
            graphics.fill(RenderPipelines.GUI, tabX, y + 1, left, y + 17, 0xFF1E2124);
            graphics.fill(RenderPipelines.GUI, tabX + 1, y + 2, left, y + 16, 0xFF282C34);
            // Color band swatch inside tab
            graphics.fill(RenderPipelines.GUI, tabX + 2, y + 3, tabX + 8, y + 15, color);
            // Direction label
            graphics.text(this.font, Component.literal(ROW_LABELS[row]), tabX + 11, y + 5, color, false);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int left = this.leftPos;
        int top = this.topPos;
        Direction[] dirs = Direction.values();

        for (int row = 0; row < 6; row++) {
            int y = top + 17 + row * 18;
            boolean onLeftTab = mouseX >= left - 48 && mouseX <= left + 7 && mouseY >= y && mouseY < y + 18;
            boolean onRightBand = mouseX >= left + 169 && mouseX <= left + 173 && mouseY >= y && mouseY < y + 18;
            if (onLeftTab || onRightBand) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal(COLOR_NAMES[row] + " Band — " + ROW_LABELS[row] + " Connection").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                        Component.literal("Items matching this row's 9 filter slots route strictly to the " + ROW_LABELS[row] + " pipe end.").withStyle(ChatFormatting.GRAY),
                        Component.literal("Leave all 9 slots empty to allow unmatched items to pass through.").withStyle(ChatFormatting.DARK_GRAY)
                ), mouseX, mouseY);
                return;
            }
        }
    }
}
