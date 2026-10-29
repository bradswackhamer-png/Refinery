package com.refinery.client.gui;

import com.refinery.block.entity.FluidPumpBlockEntity;
import com.refinery.menu.FluidPumpMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FluidPumpScreen extends AbstractContainerScreen<FluidPumpMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/fluid_pump.png");

    public FluidPumpScreen(FluidPumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = 72;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // Header Title
        graphics.text(this.font, Component.literal("FLUID PUMP").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD), 10, 6, 0xD97706, false);

        // Central Status Display (x: 30..144, y: 17..33)
        FluidPumpBlockEntity.PumpStatus status = this.menu.getStatus();
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
        graphics.text(this.font, statusText, 87 - statusW / 2, 21, 0xFFFFFF, false);

        // Left Power Display Text (inside the red-circled panel at x: 8..24, y: 17..71)
        // No progress bar needed — shows direct values!
        FluidPumpBlockEntity.PowerMode pwrMode = this.menu.getPowerMode();
        switch (pwrMode) {
            case RF -> {
                int energy = this.menu.getEnergy();
                String eStr = energy >= 1000 ? (energy / 1000) + "k" : String.valueOf(energy);
                graphics.text(this.font, Component.literal("RF").withStyle(ChatFormatting.BOLD), 12, 24, 0xEF4444, false);
                int ew = this.font.width(eStr);
                graphics.text(this.font, Component.literal(eStr), 16 - ew / 2, 40, 0xFFFFFF, false);
                graphics.text(this.font, Component.literal("FE"), 12, 54, 0x9CA3AF, false);
            }
            case SOLID_FUEL -> {
                int burn = this.menu.getBurnTime();
                String bStr = burn > 0 ? String.format("%.1fs", burn / 20.0f) : "3s";
                graphics.text(this.font, Component.literal("COAL").withStyle(ChatFormatting.BOLD), 9, 24, 0xF97316, false);
                int bw = this.font.width(bStr);
                graphics.text(this.font, Component.literal(bStr), 16 - bw / 2, 40, burn > 0 ? 0x22C55E : 0xFFFFFF, false);
                graphics.text(this.font, Component.literal("rate"), 10, 54, 0x9CA3AF, false);
            }
            case DIESEL -> {
                int diesel = this.menu.getDieselAmount();
                String dStr = diesel >= 1000 ? String.format("%.1fB", diesel / 1000.0f) : String.valueOf(diesel);
                graphics.text(this.font, Component.literal("DSL").withStyle(ChatFormatting.BOLD), 10, 24, 0xF59E0B, false);
                int dw = this.font.width(dStr);
                graphics.text(this.font, Component.literal(dStr), 16 - dw / 2, 40, 0xFFFFFF, false);
                graphics.text(this.font, Component.literal("mB"), 12, 54, 0x9CA3AF, false);
            }
        }

        // Power Mode Button Label (centered in button at x: 28, y: 37, w: 48, h: 16)
        String powerStr = switch (pwrMode) {
            case RF -> "[ RF ]";
            case SOLID_FUEL -> "[ COAL ]";
            case DIESEL -> "[ DIESEL ]";
        };
        int powerColor = switch (pwrMode) {
            case RF -> 0xEF4444;
            case SOLID_FUEL -> 0xF97316;
            case DIESEL -> 0xF59E0B;
        };
        int pwW = this.font.width(powerStr);
        graphics.text(this.font, Component.literal(powerStr).withStyle(ChatFormatting.BOLD), 52 - pwW / 2, 41, powerColor, false);

        // Infinite Water Mode Button Label (centered in button at x: 125, y: 37, w: 22, h: 16)
        boolean inf = this.menu.isInfiniteWater();
        String infStr = inf ? "INF" : "DRY";
        int infW = this.font.width(infStr);
        graphics.text(this.font, Component.literal(infStr).withStyle(ChatFormatting.BOLD), 136 - infW / 2, 41, inf ? 0x38BDF8 : 0xF97316, false);

        // Target Intake Pos Readout (x: 32, y: 61)
        BlockPos target = this.menu.getTargetPos();
        String targetStr = (target != null)
                ? String.format("Pos: %d, %d, %d", target.getX(), target.getY(), target.getZ())
                : "Intake: [Not Linked]";
        int targetColor = (target != null) ? 0x22C55E : 0x777777;
        graphics.text(this.font, Component.literal(targetStr), 33, 61, targetColor, false);

        // Redstone Mode Button Label (x: 30, y: 73)
        String rsStr = switch (this.menu.getRedstoneMode()) {
            case IGNORED -> "RS: Ignored";
            case REQUIRE_HIGH -> "RS: High";
            case REQUIRE_LOW -> "RS: Low";
        };
        graphics.text(this.font, Component.literal(rsStr), 30, 73, 0xAAAAAA, false);

        // Unlink Button (x: 104, y: 73)
        if (target != null) {
            graphics.text(this.font, Component.literal("[Unlink]"), 108, 73, 0xEF4444, false);
        }

        // Fluid Tank Label Under Blue Bar (x: 146..170, y: 73)
        int fluidAmt = this.menu.getFluidAmount();
        String fluidBadge;
        int badgeColor;
        if (fluidAmt > 0) {
            fluidBadge = (fluidAmt >= 1000) ? String.format("%.1fB", fluidAmt / 1000.0f) : fluidAmt + "mB";
            badgeColor = 0xFFFFFF;
        } else if (status == FluidPumpBlockEntity.PumpStatus.PUMPING) {
            fluidBadge = "FLOW";
            badgeColor = 0x38BDF8;
        } else {
            fluidBadge = "0/10B";
            badgeColor = 0x777777;
        }
        int fbW = this.font.width(fluidBadge);
        graphics.text(this.font, Component.literal(fluidBadge), 159 - fbW / 2, 73, badgeColor, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Left Power Panel Display (xo + 8, yo + 17, w: 18, h: 54)
        // Red-circled area: No progress bar! Clean dark digital panel.
        graphics.fill(RenderPipelines.GUI, xo + 8, yo + 17, xo + 26, yo + 71, 0xFF1E2124);
        graphics.fill(RenderPipelines.GUI, xo + 9, yo + 18, xo + 25, yo + 70, 0xFF111418);
        graphics.fill(RenderPipelines.GUI, xo + 9, yo + 35, xo + 25, yo + 36, 0xFF2D323B);
        graphics.fill(RenderPipelines.GUI, xo + 9, yo + 51, xo + 25, yo + 52, 0xFF2D323B);

        // 2. Output Fluid Tank Gauge (Blue-circled area: xo + 150, yo + 17, w: 18, h: 54)
        // Clear glass reservoir with graduations & active flow animation!
        graphics.fill(RenderPipelines.GUI, xo + 150, yo + 17, xo + 168, yo + 71, 0xFF1E2124);
        graphics.fill(RenderPipelines.GUI, xo + 151, yo + 18, xo + 167, yo + 70, 0xFF111827);

        int fluid = this.menu.getFluidAmount();
        int fluidColor = getFluidColor(this.menu.getFluidTypeIndex());

        if (fluid > 0) {
            int fluidHeight = Math.min(52, (int) ((fluid / 10000.0f) * 52));
            int topY = yo + 18 + (52 - fluidHeight);
            graphics.fill(RenderPipelines.GUI, xo + 151, topY, xo + 167, yo + 70, fluidColor);
            // Meniscus surface highlight
            graphics.fill(RenderPipelines.GUI, xo + 151, topY, xo + 167, topY + 1, 0x88FFFFFF);
        } else if (this.menu.getStatus() == FluidPumpBlockEntity.PumpStatus.PUMPING) {
            // Actively pumping fluid into pipes: show active flow level animation!
            int pulse = (int) ((System.currentTimeMillis() / 200) % 4);
            int flowH = 10 + pulse * 2;
            graphics.fill(RenderPipelines.GUI, xo + 151, yo + 70 - flowH, xo + 167, yo + 70, fluidColor);
            graphics.fill(RenderPipelines.GUI, xo + 151, yo + 70 - flowH, xo + 167, yo + 70 - flowH + 1, 0xAAFFFFFF);
        } else {
            // Subtle translucent glass reservoir tint so it is never a dead void
            graphics.fill(RenderPipelines.GUI, xo + 151, yo + 18, xo + 167, yo + 70, 0x1A38BDF8);
        }

        // Reservoir tick marks (25%, 50%, 75%)
        graphics.fill(RenderPipelines.GUI, xo + 152, yo + 31, xo + 157, yo + 32, 0x55FFFFFF);
        graphics.fill(RenderPipelines.GUI, xo + 152, yo + 44, xo + 157, yo + 45, 0x55FFFFFF);
        graphics.fill(RenderPipelines.GUI, xo + 152, yo + 57, xo + 157, yo + 58, 0x55FFFFFF);

        // 3. Power Mode Button (Yellow-circled area: xo + 28, yo + 37, w: 48, h: 16)
        // Authentic beveled Minecraft button!
        boolean pwrHover = mouseX >= xo + 28 && mouseX <= xo + 76 && mouseY >= yo + 37 && mouseY <= yo + 53;
        drawButton(graphics, xo + 28, yo + 37, 48, 16, pwrHover);

        // 4. Infinite Water Mode Button (Drain toggle: xo + 125, yo + 37, w: 22, h: 16)
        // Authentic beveled Minecraft button!
        boolean infHover = mouseX >= xo + 125 && mouseX <= xo + 147 && mouseY >= yo + 37 && mouseY <= yo + 53;
        drawButton(graphics, xo + 125, yo + 37, 22, 16, infHover);

        // 5. Solid Fuel Burn Flame (draws beside the fuel slot when burning)
        if (this.menu.getPowerMode() == FluidPumpBlockEntity.PowerMode.SOLID_FUEL) {
            int burn = this.menu.getBurnTime();
            int maxBurn = this.menu.getMaxBurnTime();
            if (burn > 0 && maxBurn > 0) {
                int flameH = Math.min(14, (int) ((burn / (float) maxBurn) * 14));
                graphics.fill(RenderPipelines.GUI, xo + 99, yo + 40 + (14 - flameH), xo + 104, yo + 54, 0xFFEA580C);
                graphics.fill(RenderPipelines.GUI, xo + 100, yo + 42 + (14 - flameH), xo + 103, yo + 54, 0xFFFBBF24);
            }
        }
    }

    private void drawButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean hovered) {
        int border = 0xFF1E2124;
        int topHighlight = hovered ? 0xFF9CA3AF : 0xFF6B7280;
        int bottomShadow = hovered ? 0xFF111827 : 0xFF1F2937;
        int bg = hovered ? 0xFF4B5563 : 0xFF374151;

        // Outer border
        graphics.fill(RenderPipelines.GUI, x, y, x + w, y + h, border);
        // Bevel top and left highlight
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + w - 1, y + 2, topHighlight);
        graphics.fill(RenderPipelines.GUI, x + 1, y + 1, x + 2, y + h - 1, topHighlight);
        // Bevel bottom and right shadow
        graphics.fill(RenderPipelines.GUI, x + 1, y + h - 2, x + w - 1, y + h - 1, bottomShadow);
        graphics.fill(RenderPipelines.GUI, x + w - 2, y + 1, x + w - 1, y + h - 1, bottomShadow);
        // Button interior
        graphics.fill(RenderPipelines.GUI, x + 2, y + 2, x + w - 2, y + h - 2, bg);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == 0) { // Left-click
            int xo = this.leftPos;
            int yo = this.topPos;

            // Power Mode Button clicked (xo + 28..76, yo + 37..53)
            if (mouseX >= xo + 28 && mouseX <= xo + 76 && mouseY >= yo + 37 && mouseY <= yo + 53) {
                this.menu.cyclePowerModeClient();
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Infinite Water / Drain Mode Button clicked (xo + 125..147, yo + 37..53)
            if (mouseX >= xo + 125 && mouseX <= xo + 147 && mouseY >= yo + 37 && mouseY <= yo + 53) {
                this.menu.toggleInfiniteWaterClient();
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Redstone Mode clicked (xo + 26..95, yo + 68..84)
            if (mouseX >= xo + 26 && mouseX <= xo + 95 && mouseY >= yo + 68 && mouseY <= yo + 84) {
                this.menu.cycleRedstoneModeClient();
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Unlink clicked (xo + 102..150, yo + 68..84)
            if (mouseX >= xo + 102 && mouseX <= xo + 150 && mouseY >= yo + 68 && mouseY <= yo + 84) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 3);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2f));
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

        // Left Power Display Panel Hover (xo + 8..26, yo + 17..71)
        if (mouseX >= xo + 8 && mouseX <= xo + 26 && mouseY >= yo + 17 && mouseY <= yo + 71) {
            FluidPumpBlockEntity.PowerMode pwr = this.menu.getPowerMode();
            switch (pwr) {
                case RF -> graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("Power Buffer: RF / FE").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        Component.literal(String.format("%,d / 20,000 FE", this.menu.getEnergy())).withStyle(ChatFormatting.WHITE),
                        Component.literal("Usage: 400 FE per bucket pumped (20 FE/t)").withStyle(ChatFormatting.GRAY)
                ), mouseX, mouseY);
                case SOLID_FUEL -> {
                    int burn = this.menu.getBurnTime();
                    graphics.setComponentTooltipForNextFrame(this.font, List.of(
                            Component.literal("Solid Fuel Combustion (Coal Mode)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                            Component.literal("Rate: 1 Coal = 3 seconds pumping (60t)").withStyle(ChatFormatting.WHITE),
                            Component.literal(burn > 0 ? String.format("Active Burn Time: %.1f seconds", burn / 20.0f) : "Burn Status: Ready for fuel").withStyle(burn > 0 ? ChatFormatting.GREEN : ChatFormatting.GRAY),
                            Component.literal("Accepts: Coal, Charcoal, Blaze Rod, Tar, Bitumen").withStyle(ChatFormatting.DARK_GRAY)
                    ), mouseX, mouseY);
                }
                case DIESEL -> graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("Liquid Fuel Buffer (Diesel)").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD),
                        Component.literal(String.format("%,d / 4,000 mB", this.menu.getDieselAmount())).withStyle(ChatFormatting.WHITE),
                        Component.literal("Usage: 10 mB per bucket pumped (100 buckets/tank)").withStyle(ChatFormatting.GRAY),
                        Component.literal("Insert Diesel/Gasoline/Biofuel buckets into fuel slot").withStyle(ChatFormatting.DARK_GRAY)
                ), mouseX, mouseY);
            }
            return;
        }

        // Fluid Tank Hover (xo + 150..168, yo + 17..71)
        if (mouseX >= xo + 150 && mouseX <= xo + 168 && mouseY >= yo + 17 && mouseY <= yo + 71) {
            String fluidName = getFluidName(this.menu.getFluidTypeIndex());
            int amt = this.menu.getFluidAmount();
            boolean pumping = this.menu.getStatus() == FluidPumpBlockEntity.PumpStatus.PUMPING;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Output Fluid Tank").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(String.format("Fluid: %s", fluidName)).withStyle(ChatFormatting.WHITE),
                    Component.literal(String.format("Buffer: %,d / 10,000 mB", amt)).withStyle(ChatFormatting.WHITE),
                    Component.literal(pumping ? "Status: Actively transferring to pipes" : "Status: Auto-outputs to adjacent pipes").withStyle(pumping ? ChatFormatting.GREEN : ChatFormatting.GRAY),
                    Component.literal("Pumping Rate: 1,000 mB per second").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Power Mode Button Hover (xo + 28..76, yo + 37..53)
        if (mouseX >= xo + 28 && mouseX <= xo + 76 && mouseY >= yo + 37 && mouseY <= yo + 53) {
            FluidPumpBlockEntity.PowerMode current = this.menu.getPowerMode();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Power Source Mode (Click to Toggle)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Current: " + current.name()).withStyle(ChatFormatting.YELLOW),
                    Component.literal("Click to switch: [RF] ➔ [COAL] ➔ [DIESEL]").withStyle(ChatFormatting.WHITE),
                    Component.literal("Can toggle anytime; no external source required").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Infinite Water Mode Hover (xo + 125..147, yo + 37..53)
        if (mouseX >= xo + 125 && mouseX <= xo + 147 && mouseY >= yo + 37 && mouseY <= yo + 53) {
            boolean inf = this.menu.isInfiniteWater();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Water Extraction Mode (Click to Toggle)").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal(inf ? "Mode: [INF] INFINITE WATER" : "Mode: [DRY] DRAIN POOL").withStyle(inf ? ChatFormatting.AQUA : ChatFormatting.GOLD),
                    Component.literal(inf ? "Pumps continuous water without destroying source blocks" : "Pumps all contiguous water source blocks dry").withStyle(ChatFormatting.WHITE),
                    Component.literal("Applies only to water. Lava and Oil always drain.").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }

        // Target Readout Box Hover (xo + 30..144, yo + 58..70)
        if (mouseX >= xo + 30 && mouseX <= xo + 144 && mouseY >= yo + 58 && mouseY <= yo + 70) {
            BlockPos target = this.menu.getTargetPos();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Suction Intake Point").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD),
                    Component.literal(target != null ? target.toShortString() : "No intake connected").withStyle(ChatFormatting.WHITE),
                    Component.literal("Sneak-click pump with wrench, then click any block/fluid to link!").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }
    }

    private static int getFluidColor(int fluidIndex) {
        return switch (fluidIndex) {
            case 1 -> 0xFF2563EB; // Water blue
            case 2 -> 0xFFEA580C; // Lava orange
            case 3 -> 0xFF1C1E22; // Crude Oil black
            case 4 -> 0xFFF5CF47; // Diesel amber
            case 5 -> 0xFFE0E7FF; // Gas light
            case 6 -> 0xFFA7F3D0; // Ethanol emerald
            case 7 -> 0xFFBAE6FD; // Steam cyan
            default -> 0xFF38BDF8;
        };
    }

    private static String getFluidName(int fluidIndex) {
        return switch (fluidIndex) {
            case 1 -> "Water";
            case 2 -> "Lava";
            case 3 -> "Crude Oil";
            case 4 -> "Diesel";
            case 5 -> "Gasoline";
            case 6 -> "Ethanol";
            case 7 -> "Steam";
            default -> "Empty";
        };
    }
}
