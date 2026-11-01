package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.menu.StorageTankMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class StorageTankScreen extends AbstractContainerScreen<StorageTankMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/storage_tank.png");

    public StorageTankScreen(StorageTankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Header Title
        graphics.text(this.font, Component.literal("STORAGE TANK").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GRAY), 8, 6, 0xFF374151, false);

        boolean assembled = this.menu.isAssembled();
        if (assembled) {
            String sizeName = this.menu.getTankSize().getDisplayName();
            graphics.text(this.font, Component.literal("[" + sizeName + "]").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD), 95, 6, 0xFF15803D, false);
        } else {
            graphics.text(this.font, Component.literal("[UNFORMED]").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), 105, 6, 0xFFDC2626, false);
        }

        // Fluid Information on the right side of the gauge (x: 74)
        Fluid fluid = this.menu.getStoredFluid();
        int amount = this.menu.getFluidAmount();
        int cap = this.menu.getCapacity();

        if (amount > 0 && fluid != Fluids.EMPTY) {
            String fluidName = getFluidDisplayName(fluid);
            graphics.text(this.font, Component.literal(fluidName).withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GRAY), 75, 19, 0xFF1F2937, false);
            graphics.text(this.font, Component.literal(String.format("%,d", amount) + " mB").withStyle(ChatFormatting.BLUE), 75, 30, 0xFF2563EB, false);
            graphics.text(this.font, Component.literal("/ " + String.format("%,d", cap) + " mB").withStyle(ChatFormatting.GRAY), 75, 40, 0xFF6B7280, false);
        } else {
            graphics.text(this.font, Component.literal("EMPTY").withStyle(ChatFormatting.GRAY), 75, 22, 0xFF9CA3AF, false);
            graphics.text(this.font, Component.literal("0 / " + String.format("%,d", cap) + " mB").withStyle(ChatFormatting.DARK_GRAY), 75, 34, 0xFF6B7280, false);
        }

        // Flush button label
        if (amount > 0) {
            graphics.text(this.font, Component.literal("FLUSH").withStyle(ChatFormatting.RED), 84, 56, 0xFFFFFFFF, false);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Draw GUI base texture
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // Sight Glass Gauge Background (xo + 38, yo + 17, w: 32, h: 54)
        graphics.fill(RenderPipelines.GUI, xo + 38, yo + 17, xo + 70, yo + 71, 0xFF1E242B);
        graphics.fill(RenderPipelines.GUI, xo + 39, yo + 18, xo + 69, yo + 70, 0xFF0F172A);

        int amount = this.menu.getFluidAmount();
        int cap = this.menu.getCapacity();
        Fluid fluid = this.menu.getStoredFluid();

        // Fluid Level Fill inside sight glass (52 pixels max fill)
        if (amount > 0 && fluid != Fluids.EMPTY) {
            float pct = Math.clamp(amount / (float) cap, 0.0f, 1.0f);
            int fillH = Math.min(52, Math.round(pct * 52));
            int fluidColor = getFluidColor(fluid);
            int topY = yo + 18 + (52 - fillH);

            graphics.fill(RenderPipelines.GUI, xo + 40, topY, xo + 68, yo + 70, fluidColor);
            // Meniscus / surface highlight line
            graphics.fill(RenderPipelines.GUI, xo + 40, topY, xo + 68, topY + 1, 0x88FFFFFF);
        }

        // Glass glare diagonal streak
        graphics.fill(RenderPipelines.GUI, xo + 42, yo + 20, xo + 44, yo + 68, 0x33FFFFFF);

        // Graduation measurement tick marks on gauge side
        graphics.fill(RenderPipelines.GUI, xo + 63, yo + 31, xo + 68, yo + 32, 0x77FFFFFF); // 75%
        graphics.fill(RenderPipelines.GUI, xo + 61, yo + 44, xo + 68, yo + 45, 0xAAFFFFFF); // 50%
        graphics.fill(RenderPipelines.GUI, xo + 63, yo + 57, xo + 68, yo + 58, 0x77FFFFFF); // 25%

        // Flush Button (xo + 76, yo + 52, w: 46, h: 16)
        if (amount > 0) {
            boolean flushHover = mouseX >= xo + 76 && mouseX <= xo + 122 && mouseY >= yo + 52 && mouseY <= yo + 68;
            drawFlushButton(graphics, xo + 76, yo + 52, 46, 16, flushHover);
        }
    }

    private void drawFlushButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int border = 0xFF450A0A;
        int topHighlight = hovered ? 0xFFDC2626 : 0xFF991B1B;
        int bottomShadow = 0xFF2B0707;
        int bg = hovered ? 0xFF7F1D1D : 0xFF581414;

        graphics.fill(RenderPipelines.GUI, x, y, x + w, y + h, border);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + w - 1, y + 2, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + 2, y + h - 1, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + h - 2, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + w - 2, y + 1, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + 2, y + 2, x + w - 2, y + h - 2, bg);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        int xo = this.leftPos;
        int yo = this.topPos;

        // Flush Button click (x: 76..122, y: 52..68)
        if ((event.button() == InputConstants.MOUSE_BUTTON_LEFT || event.button() == 0) && this.menu.getFluidAmount() > 0) {
            if (event.x() >= xo + 76 && event.x() <= xo + 122 && event.y() >= yo + 52 && event.y() <= yo + 68) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    if (this.minecraft.getSoundManager() != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_EMPTY, 1.0f));
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Sight Glass Gauge Tooltip
        if (mouseX >= xo + 38 && mouseX <= xo + 70 && mouseY >= yo + 17 && mouseY <= yo + 71) {
            Fluid fl = this.menu.getStoredFluid();
            int amt = this.menu.getFluidAmount();
            int cap = this.menu.getCapacity();
            float pct = (amt / (float) cap) * 100.0f;

            String flName = (amt > 0 && fl != Fluids.EMPTY) ? getFluidDisplayName(fl) : "Empty";
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Fluid Storage Reservoir").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Content: ").withStyle(ChatFormatting.GRAY).append(Component.literal(flName).withStyle(ChatFormatting.WHITE)),
                    Component.literal("Level: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.format("%,d", amt) + " / " + String.format("%,d", cap) + " mB (" + String.format("%.1f", pct) + "%)").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Auto-Drain: ").withStyle(ChatFormatting.GRAY).append(Component.literal("Pushes into adjacent pipes at bottom valve").withStyle(ChatFormatting.GREEN))
            ), mouseX, mouseY);
        }

        // Flush button tooltip
        if (this.menu.getFluidAmount() > 0 && mouseX >= xo + 76 && mouseX <= xo + 122 && mouseY >= yo + 52 && mouseY <= yo + 68) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Flush Reservoir").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                    Component.literal("Click to dump and clear all stored liquid.").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
        }
    }

    public static String getFluidDisplayName(Fluid fluid) {
        if (fluid == Fluids.WATER) return "Water";
        if (fluid == Fluids.LAVA) return "Lava";
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        if (id != null) {
            String path = id.getPath();
            if (path.contains("crude_oil")) return "Crude Oil";
            if (path.contains("diesel")) return "Diesel Fuel";
            if (path.contains("gasoline")) return "Gasoline";
            if (path.contains("ethanol")) return "Bio-Ethanol";
            if (path.contains("steam")) return "High Pressure Steam";
            return capitalize(path.replace("_source", "").replace("_flowing", "").replace("_", " "));
        }
        return "Unknown Fluid";
    }

    public static int getFluidColor(Fluid fluid) {
        if (fluid == Fluids.WATER) return 0xFF2563EB; // Vivid blue
        if (fluid == Fluids.LAVA) return 0xFFEA580C; // Fiery orange
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        if (id != null) {
            String p = id.getPath();
            if (p.contains("crude_oil")) return 0xFF171717; // Viscous black
            if (p.contains("diesel")) return 0xFFCA8A04; // Amber golden
            if (p.contains("gasoline")) return 0xFFEAB308; // Translucent yellow
            if (p.contains("ethanol")) return 0xFF10B981; // Green ethanol
            if (p.contains("steam")) return 0xFFBAE6FD; // Pale steam cyan
        }
        return 0xFF3B82F6;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
