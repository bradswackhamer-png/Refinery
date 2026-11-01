package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.gate.GateAction;
import com.refinery.gate.GateTier;
import com.refinery.gate.GateTrigger;
import com.refinery.gate.PipeWireColor;
import com.refinery.menu.GateMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GateScreen extends AbstractContainerScreen<GateMenu> {

    public GateScreen(GateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 224, 216);
        this.inventoryLabelY = 124;
        this.titleLabelY = 6;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = this.leftPos;
        int top = this.topPos;

        // Industrial slate bezel background
        graphics.fill(RenderPipelines.GUI, left, top, left + imageWidth, top + imageHeight, 0xFF1C1D21);

        // Header plate
        GateTier tier = menu.getTier();
        boolean autarchic = menu.isAutarchic();
        graphics.fill(RenderPipelines.GUI, left + 6, top + 6, left + imageWidth - 6, top + 22, 0xFF24272E);

        // Header Title
        String titleStr = autarchic ? tier.getDisplayName() + " [Autarchic]" : tier.getDisplayName();
        graphics.text(this.font, Component.literal(titleStr), left + 10, top + 10, tier.getColor(), false);

        // Wire indicator dots in header
        if (tier.getWireCount() > 0) {
            int wireX = left + imageWidth - 56;
            int wireY = top + 10;
            for (PipeWireColor c : PipeWireColor.values()) {
                if (tier.supportsWire(c)) {
                    boolean active = menu.isWireActive(c);
                    int col = active ? c.getColor() : 0xFF444444;
                    graphics.fill(RenderPipelines.GUI, wireX, wireY, wireX + 7, wireY + 7, col);
                    wireX += 11;
                }
            }
        }

        // Slot boxes
        int slots = tier.getSlots();
        boolean twoColumns = slots > 4;

        for (int i = 0; i < slots; i++) {
            int col = (twoColumns && i >= 4) ? 1 : 0;
            int row = twoColumns ? (i % 4) : i;

            int slotX = left + 8 + col * 106;
            int slotY = top + 26 + row * 23;

            // Slot bounding container
            graphics.fill(RenderPipelines.GUI, slotX, slotY, slotX + 102, slotY + 21, 0xFF21252B);

            // LED
            boolean active = menu.isConditionMet(i);
            int ledCol = active ? 0xFF00FF66 : 0xFF335533;
            graphics.fill(RenderPipelines.GUI, slotX + 2, slotY + 7, slotX + 8, slotY + 13, ledCol);

            // Trigger box
            GateTrigger trigger = menu.getTrigger(i);
            graphics.fill(RenderPipelines.GUI, slotX + 11, slotY + 2, slotX + 53, slotY + 19, 0xFF282C34);

            String trigText = font.plainSubstrByWidth(trigger.getLabel(), 38);
            graphics.text(this.font, Component.literal(trigText), slotX + 13, slotY + 6, trigger.getDisplayColor(), false);

            // Arrow
            graphics.text(this.font, Component.literal(">"), slotX + 55, slotY + 6, 0xFF888888, false);

            // Action box
            GateAction action = menu.getAction(i);
            graphics.fill(RenderPipelines.GUI, slotX + 62, slotY + 2, slotX + 100, slotY + 19, 0xFF282C34);

            String actText = font.plainSubstrByWidth(action.getLabel(), 34);
            graphics.text(this.font, Component.literal(actText), slotX + 64, slotY + 6, action.getDisplayColor(), false);
        }

        // Inventory background boxes
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                graphics.fill(RenderPipelines.GUI, left + 7 + c * 18, top + 133 + r * 18, left + 25 + c * 18, top + 151 + r * 18, 0xFF181A1F);
            }
        }
        for (int c = 0; c < 9; c++) {
            graphics.fill(RenderPipelines.GUI, left + 7 + c * 18, top + 191, left + 25 + c * 18, top + 209, 0xFF181A1F);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        int left = this.leftPos;
        int top = this.topPos;
        int slots = menu.getTier().getSlots();
        boolean twoColumns = slots > 4;

        boolean rightClick = (button == InputConstants.MOUSE_BUTTON_RIGHT);
        boolean leftClick = (button == InputConstants.MOUSE_BUTTON_LEFT || button == 0);
        if (!leftClick && !rightClick) {
            return super.mouseClicked(event, isDouble);
        }

        for (int i = 0; i < slots; i++) {
            int col = (twoColumns && i >= 4) ? 1 : 0;
            int row = twoColumns ? (i % 4) : i;

            int slotX = left + 8 + col * 106;
            int slotY = top + 26 + row * 23;

            // Trigger box clicked (left: slotX + 11 to + 53, top: slotY + 2 to + 19)
            if (mouseX >= slotX + 11 && mouseX <= slotX + 53 && mouseY >= slotY + 2 && mouseY <= slotY + 19) {
                int buttonId = i * 4 + (rightClick ? 1 : 0);
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, buttonId);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Action box clicked (left: slotX + 62 to + 100, top: slotY + 2 to + 19)
            if (mouseX >= slotX + 62 && mouseX <= slotX + 100 && mouseY >= slotY + 2 && mouseY <= slotY + 19) {
                int buttonId = i * 4 + (rightClick ? 3 : 2);
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, buttonId);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
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

        int left = this.leftPos;
        int top = this.topPos;
        int slots = menu.getTier().getSlots();
        boolean twoColumns = slots > 4;

        for (int i = 0; i < slots; i++) {
            int col = (twoColumns && i >= 4) ? 1 : 0;
            int row = twoColumns ? (i % 4) : i;

            int slotX = left + 8 + col * 106;
            int slotY = top + 26 + row * 23;

            // Trigger tooltip
            if (mouseX >= slotX + 11 && mouseX <= slotX + 53 && mouseY >= slotY + 2 && mouseY <= slotY + 19) {
                GateTrigger trig = menu.getTrigger(i);
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("§eTrigger: §f" + trig.getLabel()),
                        Component.literal("§7" + trig.getDescription()),
                        Component.literal("§8[Left-click: Next | Right-click: Prev]")
                ), mouseX, mouseY);
                return;
            }

            // Action tooltip
            if (mouseX >= slotX + 62 && mouseX <= slotX + 100 && mouseY >= slotY + 2 && mouseY <= slotY + 19) {
                GateAction act = menu.getAction(i);
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("§bAction: §f" + act.getLabel()),
                        Component.literal("§7" + act.getDescription()),
                        Component.literal("§8[Left-click: Next | Right-click: Prev]")
                ), mouseX, mouseY);
                return;
            }

            // LED tooltip
            if (mouseX >= slotX + 2 && mouseX <= slotX + 8 && mouseY >= slotY + 7 && mouseY <= slotY + 13) {
                boolean active = menu.isConditionMet(i);
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal(active ? "§aStatus: ACTIVE" : "§7Status: INACTIVE")
                ), mouseX, mouseY);
                return;
            }
        }
    }
}
