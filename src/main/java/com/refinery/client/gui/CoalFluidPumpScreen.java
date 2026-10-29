package com.refinery.client.gui;

import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.menu.CoalFluidPumpMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CoalFluidPumpScreen extends AbstractContainerScreen<CoalFluidPumpMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/coal_fluid_pump.png");

    public CoalFluidPumpScreen(CoalFluidPumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Header Title
        graphics.text(this.font, Component.literal("COAL FLUID PUMP").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GRAY), 10, 6, 0x374151, false);

        // Central Status Display (x: 24..142, y: 17..33)
        AbstractFluidPumpBlockEntity.PumpStatus status = this.menu.getPumpStatus();
        Component statusText = switch (status) {
            case PUMPING -> Component.literal("[PUMPING]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            case IDLE -> Component.literal("[IDLE]").withStyle(ChatFormatting.GRAY);
            case NO_POWER -> Component.literal("[NO POWER]").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
            case NO_FUEL -> Component.literal("[NO FUEL]").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            case NO_DIESEL -> Component.literal("[NO DIESEL]").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            case TANK_FULL -> Component.literal("[TANK FULL]").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
            case NO_TARGET -> Component.literal("[NO INTAKE]").withStyle(ChatFormatting.GRAY);
            case TARGET_TOO_FAR -> Component.literal("[OUT OF RANGE]").withStyle(ChatFormatting.RED);
            case POOL_DRY -> Component.literal("[POOL DRAINED]").withStyle(ChatFormatting.WHITE);
            case REDSTONE_OFF -> Component.literal("[PAUSED - RS]").withStyle(ChatFormatting.DARK_RED);
        };
        int statusW = this.font.width(statusText);
        graphics.text(this.font, statusText, 83 - statusW / 2, 21, 0xFFFFFF, false);

        // Fuel burn rate text below flame
        graphics.text(this.font, Component.literal("1 Coal = 3s").withStyle(ChatFormatting.GRAY), 38, 62, 0x6B7280, false);

        // Infinite Water Mode Button Label (x: 84, y: 41, w: 58, h: 18)
        boolean inf = this.menu.isInfiniteWater();
        Component infLabel = inf
                ? Component.literal("INFINITE").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                : Component.literal("DRAIN").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int infW = this.font.width(infLabel);
        graphics.text(this.font, infLabel, 113 - infW / 2, 46, 0xFFFFFF, false);

        // Redstone Mode Button Label (x: 10, y: 41, w: 24, h: 18)
        String rsStr = switch (this.menu.getRedstoneMode()) {
            case IGNORED -> "IGN";
            case REQUIRE_HIGH -> "HIGH";
            case REQUIRE_LOW -> "LOW";
        };
        int rsW = this.font.width(rsStr);
        graphics.text(this.font, Component.literal(rsStr).withStyle(ChatFormatting.RED), 22 - rsW / 2, 46, 0xFFFFFF, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Draw GUI Background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Status Background
        graphics.fill(RenderPipelines.GUI, xo + 25, yo + 18, xo + 141, yo + 32, 0xFF181B1F);

        // Buttons
        boolean infHover = mouseX >= xo + 84 && mouseX <= xo + 142 && mouseY >= yo + 41 && mouseY <= yo + 59;
        drawButton(graphics, xo + 84, yo + 41, 58, 18, infHover);

        boolean rsHover = mouseX >= xo + 10 && mouseX <= xo + 34 && mouseY >= yo + 41 && mouseY <= yo + 59;
        drawButton(graphics, xo + 10, yo + 41, 24, 18, rsHover);

        // Burn Flame
        int burn = this.menu.getBurnTime();
        int maxBurn = this.menu.getMaxBurnTime();
        if (burn > 0 && maxBurn > 0) {
            int flameH = Math.min(12, (int) ((burn / (float) maxBurn) * 12));
            graphics.fill(RenderPipelines.GUI, xo + 65, yo + 45 + (12 - flameH), xo + 77, yo + 57, 0xFFEA580C);
            graphics.fill(RenderPipelines.GUI, xo + 68, yo + 47 + (12 - flameH), xo + 74, yo + 57, 0xFFFBBF24);
        }

        // Output Fluid Tank (xo + 148, yo + 17, w: 18, h: 54)
        graphics.fill(RenderPipelines.GUI, xo + 148, yo + 17, xo + 166, yo + 71, 0xFF1E2124);
        graphics.fill(RenderPipelines.GUI, xo + 149, yo + 18, xo + 165, yo + 70, 0xFF111827);

        int fluid = this.menu.getFluidAmount();
        int fluidColor = getFluidColor(this.menu.getFluidTypeIndex());

        if (fluid > 0) {
            int fluidHeight = Math.min(52, (int) ((fluid / 10000.0f) * 52));
            int topY = yo + 18 + (52 - fluidHeight);
            graphics.fill(RenderPipelines.GUI, xo + 149, topY, xo + 165, yo + 70, fluidColor);
            graphics.fill(RenderPipelines.GUI, xo + 149, topY, xo + 165, topY + 1, 0x88FFFFFF);
        } else if (this.menu.getPumpStatus() == AbstractFluidPumpBlockEntity.PumpStatus.PUMPING) {
            int pulse = (int) ((System.currentTimeMillis() / 200) % 4);
            int flowH = 10 + pulse * 2;
            graphics.fill(RenderPipelines.GUI, xo + 149, yo + 70 - flowH, xo + 165, yo + 70, fluidColor);
            graphics.fill(RenderPipelines.GUI, xo + 149, yo + 70 - flowH, xo + 165, yo + 70 - flowH + 1, 0xAAFFFFFF);
        } else {
            graphics.fill(RenderPipelines.GUI, xo + 149, yo + 18, xo + 165, yo + 70, 0x1A38BDF8);
        }

        // Reservoir tick marks
        graphics.fill(RenderPipelines.GUI, xo + 150, yo + 31, xo + 155, yo + 32, 0x55FFFFFF);
        graphics.fill(RenderPipelines.GUI, xo + 150, yo + 44, xo + 155, yo + 45, 0x55FFFFFF);
        graphics.fill(RenderPipelines.GUI, xo + 150, yo + 57, xo + 155, yo + 58, 0x55FFFFFF);
    }

    private void drawButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int border = 0xFF1E2124;
        int topHighlight = hovered ? 0xFF9CA3AF : 0xFF6B7280;
        int bottomShadow = hovered ? 0xFF111827 : 0xFF1F2937;
        int bg = hovered ? 0xFF4B5563 : 0xFF374151;

        graphics.fill(RenderPipelines.GUI, x, y, x + w, y + h, border);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + w - 1, y + 2, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + 2, y + h - 1, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + h - 2, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + w - 2, y + 1, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + 2, y + 2, x + w - 2, y + h - 2, bg);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == 0) {
            int xo = this.leftPos;
            int yo = this.topPos;

            // Infinite Water / Drain Mode Button (xo + 84..142, yo + 41..59)
            if (mouseX >= xo + 84 && mouseX <= xo + 142 && mouseY >= yo + 41 && mouseY <= yo + 59) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Redstone Mode Button (xo + 10..34, yo + 41..59)
            if (mouseX >= xo + 10 && mouseX <= xo + 34 && mouseY >= yo + 41 && mouseY <= yo + 59) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int xo = this.leftPos;
        int yo = this.topPos;

        // Output Fluid Tank Tooltip
        if (mouseX >= xo + 148 && mouseX <= xo + 166 && mouseY >= yo + 17 && mouseY <= yo + 71) {
            int fluid = this.menu.getFluidAmount();
            String fName = getFluidName(this.menu.getFluidTypeIndex());
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Internal Reservoir").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(fName + ": " + fluid + " / 10,000 mB").withStyle(ChatFormatting.AQUA),
                    Component.literal("Automatically outputs to connected pipes").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

        // Mode Button Tooltip
        if (mouseX >= xo + 84 && mouseX <= xo + 142 && mouseY >= yo + 41 && mouseY <= yo + 59) {
            boolean inf = this.menu.isInfiniteWater();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Pumping Mode: " + (inf ? "Infinite Water" : "Drain Pool")).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    inf ? Component.literal("Draws infinite water without removing source blocks.").withStyle(ChatFormatting.GRAY)
                        : Component.literal("Drains all connected fluid source blocks in pool dry.").withStyle(ChatFormatting.GRAY),
                    Component.literal("Click to Toggle").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Redstone Button Tooltip
        if (mouseX >= xo + 10 && mouseX <= xo + 34 && mouseY >= yo + 41 && mouseY <= yo + 59) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Control: " + this.menu.getRedstoneMode().name()).withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Click to Cycle (Ignored / Require High / Require Low)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }

    private int getFluidColor(int fluidIdx) {
        return switch (fluidIdx) {
            case 1 -> 0xFF38BDF8; // Water
            case 2 -> 0xFFF97316; // Lava
            case 3 -> 0xFF1C1917; // Crude Oil
            case 4 -> 0xFFEAB308; // Diesel
            case 5 -> 0xFFFDE047; // Gasoline
            case 6 -> 0xFF10B981; // Ethanol
            case 7 -> 0xFFEF4444; // Sulfuric Acid
            default -> 0xFF38BDF8;
        };
    }

    private String getFluidName(int fluidIdx) {
        return switch (fluidIdx) {
            case 1 -> "Water";
            case 2 -> "Lava";
            case 3 -> "Crude Oil";
            case 4 -> "Diesel";
            case 5 -> "Gasoline";
            case 6 -> "Ethanol";
            case 7 -> "Sulfuric Acid";
            default -> "Empty";
        };
    }
}
