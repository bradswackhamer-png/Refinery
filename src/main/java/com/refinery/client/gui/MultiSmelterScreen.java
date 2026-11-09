package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.block.entity.MultiSmelterControllerBlockEntity;
import com.refinery.menu.MultiSmelterMenu;
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

        // 4. Auto-Fuel Retention Button (x: 128, y: 63, w: 42, h: 14)
        int btnX = xo + 128;
        int btnY = yo + 63;
        int btnW = 42;
        int btnH = 14;
        boolean btnHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int fuelReserve = this.menu.getFuelReserve();

        int border = fuelReserve > 0 ? (btnHovered ? 0xFFF59E0B : 0xFFD97706) : (btnHovered ? 0xFF64748B : 0xFF475569);
        int bg = fuelReserve > 0 ? (btnHovered ? 0xFF9A3412 : 0xFF7C2D12) : (btnHovered ? 0xFF334155 : 0xFF1E293B);

        graphics.fill(RenderPipelines.GUI, btnX, btnY, btnX + btnW, btnY + btnH, border);
        graphics.fill(RenderPipelines.GUI, btnX + 1, btnY + 1, btnX + btnW - 1, btnY + btnH - 1, bg);

        String btnText = fuelReserve > 0 ? "Keep:" + fuelReserve : "Fuel:OFF";
        int textColor = fuelReserve > 0 ? 0xFFFDE047 : 0xFF94A3B8;
        int textW = this.font.width(btnText);
        graphics.text(this.font, Component.literal(btnText), btnX + (btnW - textW) / 2, btnY + 3, textColor, false);

        // 5. Parallel Cook Progress Bars (for each active input slot in dedicated recessed grooves)
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        int xo = this.leftPos;
        int yo = this.topPos;
        int btnX = xo + 128;
        int btnY = yo + 63;
        int btnW = 42;
        int btnH = 14;

        if (event.x() >= btnX && event.x() <= btnX + btnW && event.y() >= btnY && event.y() <= btnY + btnH) {
            if (event.button() == InputConstants.MOUSE_BUTTON_LEFT || event.button() == 0) {
                if (this.minecraft != null && this.minecraft.gameMode != null && this.minecraft.player != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    if (this.minecraft.getSoundManager() != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    }
                }
                return true;
            } else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT || event.button() == 1) {
                if (this.minecraft != null && this.minecraft.gameMode != null && this.minecraft.player != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 1);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    if (this.minecraft.getSoundManager() != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.85f));
                    }
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

        // Auto-Fuel Retention Reserve Button Hover (x: 128..170, y: 63..77)
        else if (mouseX >= xo + 128 && mouseX <= xo + 170 && mouseY >= yo + 63 && mouseY <= yo + 77) {
            int fuelReserve = this.menu.getFuelReserve();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Auto-Fuel Retention Reserve").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(fuelReserve > 0 ? "Target Reserve: Keeping up to " + fuelReserve + " fuel" : "Status: Disabled (All fuel exported)").withStyle(fuelReserve > 0 ? ChatFormatting.GREEN : ChatFormatting.RED),
                    Component.literal("Automatically diverts produced solid fuel").withStyle(ChatFormatting.GRAY),
                    Component.literal("(e.g. Charcoal) into the combustion fuel slot.").withStyle(ChatFormatting.GRAY),
                    Component.literal("Once the reserve is reached, excess fuel is exported.").withStyle(ChatFormatting.DARK_AQUA),
                    Component.literal("Left-Click: Increase (0, 8, 16, 24, 32, 48, 64)").withStyle(ChatFormatting.DARK_GRAY),
                    Component.literal("Right-Click: Decrease").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
        }

        // Inputs Panel Hover (x: 24..98, y: 16..60)
        else if (mouseX >= xo + 24 && mouseX <= xo + 98 && mouseY >= yo + 16 && mouseY <= yo + 60) {
            int activeSlots = Integer.bitCount(this.menu.getActiveSlotsMask());
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Smelting Hearth (8 Parallel Slots)").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Active Smelting Slots: " + activeSlots + " / 8").withStyle(ChatFormatting.GOLD),
                    Component.literal("Load-Balanced: Distributes incoming items across all 8 slots").withStyle(ChatFormatting.YELLOW),
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
