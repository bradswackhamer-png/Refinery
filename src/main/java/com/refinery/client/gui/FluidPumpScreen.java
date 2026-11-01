package com.refinery.client.gui;

import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.menu.FluidPumpMenu;
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
public class FluidPumpScreen extends AbstractContainerScreen<FluidPumpMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/coal_fluid_pump.png");

    public FluidPumpScreen(FluidPumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Header Title
        graphics.text(this.font, Component.literal("CREATIVE FLUID PUMP").withStyle(ChatFormatting.BOLD), 10, 6, 0xFFD946EF, false);

        // Central Status Display (x: 24..142, y: 17..33)
        AbstractFluidPumpBlockEntity.PumpStatus status = this.menu.getPumpStatus();
        Component statusText = switch (status) {
            case PUMPING -> Component.literal("[PUMPING]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            case IDLE -> Component.literal("[IDLE]").withStyle(ChatFormatting.GRAY);
            case NO_POWER -> Component.literal("[CREATIVE]").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);
            case NO_FUEL -> Component.literal("[CREATIVE]").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);
            case NO_DIESEL -> Component.literal("[CREATIVE]").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);
            case TANK_FULL -> Component.literal("[TANK FULL]").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
            case NO_TARGET -> Component.literal("[AUTO INTAKE]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            case TARGET_TOO_FAR -> Component.literal("[OUT OF RANGE]").withStyle(ChatFormatting.RED);
            case POOL_DRY -> Component.literal("[POOL DRAINED]").withStyle(ChatFormatting.WHITE);
            case REDSTONE_OFF -> Component.literal("[PAUSED - RS]").withStyle(ChatFormatting.DARK_RED);
        };
        int statusW = this.font.width(statusText);
        graphics.text(this.font, statusText, 83 - statusW / 2, 21, 0xFFFFFFFF, false);

        // Subtext
        graphics.text(this.font, Component.literal("Infinite Fluid Generator").withStyle(ChatFormatting.GRAY), 30, 62, 0xFFA855F7, false);

        // Infinite Mode Button Label (x: 84, y: 41, w: 58, h: 18)
        boolean inf = this.menu.isInfiniteWater();
        Component infLabel = inf
                ? Component.literal("INFINITE").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                : Component.literal("DRAIN").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        int infW = this.font.width(infLabel);
        graphics.text(this.font, infLabel, 113 - infW / 2, 46, 0xFFFFFFFF, false);

        // Redstone Mode Button Label (x: 10, y: 41, w: 24, h: 18)
        String rsStr = switch (this.menu.getRedstoneMode()) {
            case IGNORED -> "IGN";
            case REQUIRE_HIGH -> "HIGH";
            case REQUIRE_LOW -> "LOW";
        };
        int rsW = this.font.width(rsStr);
        graphics.text(this.font, Component.literal(rsStr).withStyle(ChatFormatting.RED), 22 - rsW / 2, 46, 0xFFFFFFFF, false);
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

        // Hide coal slot area with clean panel filling
        graphics.fill(RenderPipelines.GUI, xo + 60, yo + 39, xo + 82, yo + 60, 0xFFC6C6C6);
        graphics.fill(RenderPipelines.GUI, xo + 61, yo + 40, xo + 81, yo + 59, 0xFFE2E8F0);

        // Buttons
        boolean infHover = mouseX >= xo + 84 && mouseX <= xo + 142 && mouseY >= yo + 41 && mouseY <= yo + 59;
        drawButton(graphics, xo + 84, yo + 41, 58, 18, infHover);

        boolean rsHover = mouseX >= xo + 10 && mouseX <= xo + 34 && mouseY >= yo + 41 && mouseY <= yo + 59;
        drawButton(graphics, xo + 10, yo + 41, 24, 18, rsHover);

        // Fluid Tank Indicator (x: 147..162, y: 17..68, h: 52)
        int fluidAmount = this.menu.getFluidAmount();
        int capacity = AbstractFluidPumpBlockEntity.TANK_CAPACITY;
        if (capacity > 0 && fluidAmount > 0) {
            float ratio = (float) fluidAmount / capacity;
            int fillH = Math.min(50, Math.round(ratio * 50));
            int color = switch (this.menu.getFluidTypeIndex()) {
                case 1 -> 0xFF3B82F6; // Water Blue
                case 2 -> 0xFFEA580C; // Lava Orange
                case 3 -> 0xFF1F2937; // Crude Oil
                case 4 -> 0xFFCA8A04; // Diesel
                case 5 -> 0xFF16A34A; // Gasoline
                case 6 -> 0xFF0284C7; // Ethanol
                default -> 0xFF3B82F6;
            };
            graphics.fill(RenderPipelines.GUI, xo + 148, yo + 68 - fillH, xo + 162, yo + 68, color);
        }
    }

    private void drawButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hover) {
        int bg = hover ? 0xFF374151 : 0xFF1F2937;
        int border = hover ? 0xFF9CA3AF : 0xFF4B5563;
        graphics.fill(RenderPipelines.GUI, x, y, x + w, y + h, border);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + w - 1, y + h - 1, bg);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        int xo = this.leftPos;
        int yo = this.topPos;
        double mx = event.x();
        double my = event.y();

        // 1. Infinite Water Toggle (x: 84, y: 41, w: 58, h: 18)
        if (mx >= xo + 84 && mx <= xo + 142 && my >= yo + 41 && my <= yo + 59) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        // 2. Redstone Mode Toggle (x: 10, y: 41, w: 24, h: 18)
        if (mx >= xo + 10 && mx <= xo + 34 && my >= yo + 41 && my <= yo + 59) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        return super.mouseClicked(event, bl);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Fluid Tank Tooltip
        if (mouseX >= xo + 147 && mouseX <= xo + 163 && mouseY >= yo + 17 && mouseY <= yo + 69) {
            String fluidName = switch (this.menu.getFluidTypeIndex()) {
                case 1 -> "Water";
                case 2 -> "Lava";
                case 3 -> "Crude Oil";
                case 4 -> "Diesel";
                case 5 -> "Gasoline";
                case 6 -> "Ethanol";
                default -> "Infinite Water";
            };
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal(fluidName).withStyle(ChatFormatting.AQUA),
                    Component.literal("Unlimited (Creative)").withStyle(ChatFormatting.LIGHT_PURPLE)
            ), mouseX, mouseY);
        }
    }
}
