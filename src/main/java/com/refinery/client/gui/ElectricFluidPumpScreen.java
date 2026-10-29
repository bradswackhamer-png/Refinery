package com.refinery.client.gui;

import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.menu.ElectricFluidPumpMenu;
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
public class ElectricFluidPumpScreen extends AbstractContainerScreen<ElectricFluidPumpMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/electric_fluid_pump.png");

    public ElectricFluidPumpScreen(ElectricFluidPumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Header Title
        graphics.text(this.font, Component.literal("ELECTRIC FLUID PUMP").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), 10, 6, 0x2563EB, false);

        // Central Status Display (x: 20..142, y: 17..33)
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
        graphics.text(this.font, statusText, 81 - statusW / 2, 21, 0xFFFFFF, false);

        // Digital Readout Text
        int energy = this.menu.getEnergy();
        String eStr = energy >= 1000 ? (energy / 1000) + "k / 20k FE" : energy + " / 20,000 FE";
        graphics.text(this.font, Component.literal(eStr).withStyle(ChatFormatting.GREEN), 46, 40, 0x22C55E, false);

        // Mode Button Label inside digital panel (x: 44, y: 53, w: 50, h: 15)
        boolean inf = this.menu.isInfiniteWater();
        Component infLabel = inf
                ? Component.literal("INFINITE").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                : Component.literal("DRAIN").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int infW = this.font.width(infLabel);
        graphics.text(this.font, infLabel, 69 - infW / 2, 57, 0xFFFFFF, false);

        // Redstone Mode Button Label (x: 96, y: 53, w: 44, h: 15)
        String rsStr = switch (this.menu.getRedstoneMode()) {
            case IGNORED -> "RS: IGN";
            case REQUIRE_HIGH -> "RS: HIGH";
            case REQUIRE_LOW -> "RS: LOW";
        };
        int rsW = this.font.width(rsStr);
        graphics.text(this.font, Component.literal(rsStr).withStyle(ChatFormatting.RED), 118 - rsW / 2, 57, 0xFFFFFF, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Status Background
        graphics.fill(RenderPipelines.GUI, xo + 21, yo + 18, xo + 141, yo + 32, 0xFF181B1F);

        // RF Energy Reservoir (xo + 20, yo + 37, w: 18, h: 34)
        graphics.fill(RenderPipelines.GUI, xo + 20, yo + 37, xo + 38, yo + 71, 0xFF1E2124);
        graphics.fill(RenderPipelines.GUI, xo + 21, yo + 38, xo + 37, yo + 70, 0xFF111827);

        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int eHeight = Math.min(32, (int) ((energy / 20000.0f) * 32));
            int topY = yo + 38 + (32 - eHeight);
            graphics.fill(RenderPipelines.GUI, xo + 21, topY, xo + 37, yo + 70, 0xFFEF4444);
            graphics.fill(RenderPipelines.GUI, xo + 21, topY, xo + 37, topY + 1, 0xFFF87171);
        } else {
            graphics.fill(RenderPipelines.GUI, xo + 21, yo + 38, xo + 37, yo + 70, 0x1AEF4444);
        }

        graphics.fill(RenderPipelines.GUI, xo + 22, yo + 54, xo + 26, yo + 55, 0x55FFFFFF);

        // Digital Readout Screen Background (xo + 42, yo + 37, w: 100, h: 34)
        graphics.fill(RenderPipelines.GUI, xo + 43, yo + 38, xo + 141, yo + 70, 0xFF0F172A);

        // Mode Button (xo + 44, yo + 53, w: 50, h: 15)
        boolean infHover = mouseX >= xo + 44 && mouseX <= xo + 94 && mouseY >= yo + 53 && mouseY <= yo + 68;
        drawButton(graphics, xo + 44, yo + 53, 50, 15, infHover);

        // Redstone Button (xo + 96, yo + 53, w: 44, h: 15)
        boolean rsHover = mouseX >= xo + 96 && mouseX <= xo + 140 && mouseY >= yo + 53 && mouseY <= yo + 68;
        drawButton(graphics, xo + 96, yo + 53, 44, 15, rsHover);

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

            // Mode Button (xo + 44..94, yo + 53..68)
            if (mouseX >= xo + 44 && mouseX <= xo + 94 && mouseY >= yo + 53 && mouseY <= yo + 68) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Redstone Button (xo + 96..140, yo + 53..68)
            if (mouseX >= xo + 96 && mouseX <= xo + 140 && mouseY >= yo + 53 && mouseY <= yo + 68) {
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

        // Energy Reservoir Tooltip
        if (mouseX >= xo + 20 && mouseX <= xo + 38 && mouseY >= yo + 37 && mouseY <= yo + 71) {
            int energy = this.menu.getEnergy();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Buffer").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal(energy + " / 20,000 FE").withStyle(ChatFormatting.YELLOW),
                    Component.literal("Consumes 400 FE per 1,000 mB pumped (20 FE/t)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }

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
        if (mouseX >= xo + 44 && mouseX <= xo + 94 && mouseY >= yo + 53 && mouseY <= yo + 68) {
            boolean inf = this.menu.isInfiniteWater();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Pumping Mode: " + (inf ? "Infinite Water" : "Drain Pool")).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                    inf ? Component.literal("Draws infinite water without removing source blocks.").withStyle(ChatFormatting.GRAY)
                        : Component.literal("Drains all connected fluid source blocks in pool dry.").withStyle(ChatFormatting.GRAY),
                    Component.literal("Click to Toggle").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Redstone Button Tooltip
        if (mouseX >= xo + 96 && mouseX <= xo + 140 && mouseY >= yo + 53 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Redstone Control: " + this.menu.getRedstoneMode().name()).withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Click to Cycle (Ignored / Require High / Require Low)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }

    private int getFluidColor(int fluidIdx) {
        return switch (fluidIdx) {
            case 1 -> 0xFF38BDF8;
            case 2 -> 0xFFF97316;
            case 3 -> 0xFF1C1917;
            case 4 -> 0xFFEAB308;
            case 5 -> 0xFFFDE047;
            case 6 -> 0xFF10B981;
            case 7 -> 0xFFEF4444;
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
