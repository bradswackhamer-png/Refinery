package com.refinery.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.refinery.block.entity.QuarryControllerBlockEntity;
import com.refinery.menu.QuarryMenu;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class QuarryScreen extends AbstractContainerScreen<QuarryMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/gui/quarry.png");

    private int blockListPage = 0;

    public QuarryScreen(QuarryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 256, 196);
        this.titleLabelX = 10;
        this.titleLabelY = 7;
        this.inventoryLabelX = 48;
        this.inventoryLabelY = 105;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        // 1. Live Operation Status Readout
        int phase = this.menu.getPhase();
        boolean assembled = this.menu.isAssembled();
        int speed = this.menu.getSpeedPercent();

        String statusStr;
        int statusColor;
        if (!assembled) {
            statusStr = "STRUCTURE INCOMPLETE";
            statusColor = 0xFFEF4444; // Red
        } else if (speed == 0) {
            statusStr = "UNPOWERED (0 FE/t)";
            statusColor = 0xFF94A3B8; // Slate Gray
        } else {
            statusStr = switch (phase) {
                case QuarryControllerBlockEntity.PHASE_CLEARING -> "BENCH: 43x43x3";
                case QuarryControllerBlockEntity.PHASE_FENCING  -> "SAFETY RAILING";
                case QuarryControllerBlockEntity.PHASE_MINING   -> "DEEP PIT (Y: " + this.menu.getCurrentY() + ")";
                default -> "MINING COMPLETE";
            };
            statusColor = 0xFF22C55E; // Emerald Green
        }
        graphics.text(this.font, Component.literal(statusStr).withStyle(ChatFormatting.BOLD), 10, 19, statusColor, false);

        // 2. Zone Plate Labels
        graphics.text(this.font, Component.literal("ZONE"), 28, 33, 0xFF94A3B8, false);

        int r = this.menu.getRadius();
        int outerW = r * 2 + 1;
        int innerW = (r - 1) * 2 + 1;
        graphics.text(this.font, Component.literal("Box: " + outerW + "x" + outerW + "x3"), 28, 59, 0xFFE2E8F0, false);
        graphics.text(this.font, Component.literal("Pit: " + innerW + "x" + innerW), 28, 69, 0xFF94A3B8, false);

        // 3. Fence Plate Label
        graphics.text(this.font, Component.literal("FENCE"), 111, 33, 0xFF94A3B8, false);

        // 4. Manifest Monitor Header Labels
        graphics.text(this.font, Component.literal("MANIFEST").withStyle(ChatFormatting.BOLD), 149, 11, 0xFF06B6D4, false);

        int totalRem = this.menu.getTotalRemainingBlocks();
        String totalStr = totalRem > 0 ? String.format("%,d", totalRem) + " BLOCKS" : "0 BLOCKS";
        graphics.text(this.font, Component.literal(totalStr).withStyle(ChatFormatting.BOLD), 149, 21, 0xFFFBBF24, false);

        // 5. Buffer & Throw-Back Labels
        graphics.text(this.font, Component.literal("Buffer"), 10, 88, 0xFF94A3B8, false);
        graphics.text(this.font, Component.literal("REJECT").withStyle(ChatFormatting.BOLD), 216, 73, 0xFFF97316, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        // Base GUI background (256x196)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);

        // 1. Energy Storage Bar Fill (x: 10, y: 31, w: 12, h: 48)
        int energy = this.menu.getEnergy();
        if (energy > 0) {
            int energyHeight = Math.min(48, (int) ((energy / (float) QuarryControllerBlockEntity.ENERGY_CAPACITY) * 48));
            graphics.fill(RenderPipelines.GUI, xo + 10, yo + 31 + (48 - energyHeight), xo + 22, yo + 79, 0xFFEAB308);
            graphics.fill(RenderPipelines.GUI, xo + 10, yo + 31 + (48 - energyHeight), xo + 12, yo + 79, 0xFFFDE047);
        }

        // 2. Radius Controls: [-], [radius text], [+], [R]
        int r = this.menu.getRadius();

        // [-] Button (xo + 28, yo + 42, w: 16, h: 14)
        boolean minusHover = mouseX >= xo + 28 && mouseX <= xo + 44 && mouseY >= yo + 42 && mouseY <= yo + 56;
        if (minusHover) {
            graphics.fill(RenderPipelines.GUI, xo + 29, yo + 43, xo + 43, yo + 55, 0x44FFFFFF);
        }
        int minusColor = r > QuarryControllerBlockEntity.MIN_RADIUS ? 0xFFFFFFFF : 0xFF64748B;
        graphics.text(this.font, Component.literal("-").withStyle(ChatFormatting.BOLD), xo + 34, yo + 45, minusColor, false);

        // Radius Display (xo + 48..56, yo + 45)
        String radStr = String.valueOf(r);
        int radW = this.font.width(radStr);
        graphics.text(this.font, Component.literal(radStr).withStyle(ChatFormatting.BOLD), xo + 52 - radW / 2, yo + 45, 0xFFF1F5F9, false);

        // [+] Button (xo + 60, yo + 42, w: 16, h: 14)
        boolean plusHover = mouseX >= xo + 60 && mouseX <= xo + 76 && mouseY >= yo + 42 && mouseY <= yo + 56;
        if (plusHover) {
            graphics.fill(RenderPipelines.GUI, xo + 61, yo + 43, xo + 75, yo + 55, 0x44FFFFFF);
        }
        int plusColor = r < QuarryControllerBlockEntity.MAX_RADIUS ? 0xFFFFFFFF : 0xFF64748B;
        graphics.text(this.font, Component.literal("+").withStyle(ChatFormatting.BOLD), xo + 65, yo + 45, plusColor, false);

        // [R] Rescan Button (xo + 86, yo + 42, w: 16, h: 14)
        boolean resetHover = mouseX >= xo + 86 && mouseX <= xo + 102 && mouseY >= yo + 42 && mouseY <= yo + 56;
        if (resetHover) {
            graphics.fill(RenderPipelines.GUI, xo + 87, yo + 43, xo + 101, yo + 55, 0x44FFFFFF);
        }
        graphics.text(this.font, Component.literal("R").withStyle(ChatFormatting.BOLD), xo + 91, yo + 45, 0xFF38BDF8, false);

        // 3. Manifest Display: 8 Block Slots (4 columns x 2 rows)
        QuarryControllerBlockEntity be = this.menu.getBlockEntity();
        List<QuarryControllerBlockEntity.BlockCountEntry> blockList = be != null ? be.getClientBlockList() : List.of();

        int pageSize = 8;
        int totalPages = Math.max(1, (int) Math.ceil(blockList.size() / (double) pageSize));
        if (this.blockListPage >= totalPages) this.blockListPage = 0;
        if (this.blockListPage < 0) this.blockListPage = 0;

        int startIndex = this.blockListPage * pageSize;
        int endIndex = Math.min(blockList.size(), startIndex + pageSize);

        for (int i = startIndex; i < endIndex; i++) {
            int slotIdx = i - startIndex;
            int col = slotIdx % 4;
            int row = slotIdx / 4;
            int bx = xo + 150 + col * 24;
            int by = yo + 32 + row * 22;

            var entry = blockList.get(i);
            ItemStack itemStack = new ItemStack(entry.block());

            boolean isTB = be != null && be.isThrowBack(entry.block());
            if (isTB) {
                // Vibrant orange rejection border for throw-back blocks
                graphics.fill(RenderPipelines.GUI, bx - 1, by - 1, bx + 17, by, 0xFFF97316);
                graphics.fill(RenderPipelines.GUI, bx - 1, by + 16, bx + 17, by + 17, 0xFFF97316);
                graphics.fill(RenderPipelines.GUI, bx - 1, by, bx, by + 16, 0xFFF97316);
                graphics.fill(RenderPipelines.GUI, bx + 16, by, bx + 17, by + 16, 0xFFF97316);
            }

            // Render block item icon
            graphics.item(itemStack, bx, by);

            if (isTB) {
                // Render [TB] reject badge at top-left
                graphics.pose().pushMatrix();
                graphics.pose().translate(bx + 1, by + 1);
                graphics.pose().scale(0.55f, 0.55f);
                graphics.text(this.font, Component.literal("TB").withStyle(ChatFormatting.BOLD), 0, 0, 0xFFF97316, true);
                graphics.pose().popMatrix();
            }

            // Render compact count badge at bottom right
            String cntText = formatCount(entry.count());
            int txtW = this.font.width(cntText);
            graphics.pose().pushMatrix();
            graphics.pose().translate(bx + 18 - (txtW * 0.7f), by + 10);
            graphics.pose().scale(0.7f, 0.7f);
            graphics.text(this.font, Component.literal(cntText), 0, 0, 0xFFFDE047, true);
            graphics.pose().popMatrix();
        }

        // 4. Quick [SPOIL] Button (xo + 212, yo + 21, w: 36, h: 9)
        boolean spoilHover = mouseX >= xo + 212 && mouseX <= xo + 248 && mouseY >= yo + 21 && mouseY <= yo + 30;
        int spoilColor = spoilHover ? 0xFFFFFFFF : 0xFFF97316;
        graphics.text(this.font, Component.literal("[SPOIL]").withStyle(ChatFormatting.BOLD), xo + 212, yo + 21, spoilColor, false);

        // 5. Manifest Page Arrow Controls: [<] [1/X] [>] inside monitor at y: 11
        if (totalPages > 1) {
            boolean prevHover = mouseX >= xo + 215 && mouseX <= xo + 224 && mouseY >= yo + 11 && mouseY <= yo + 20;
            boolean nextHover = mouseX >= xo + 239 && mouseX <= xo + 248 && mouseY >= yo + 11 && mouseY <= yo + 20;

            int prevColor = this.blockListPage > 0 ? (prevHover ? 0xFFFFFFFF : 0xFF38BDF8) : 0xFF475569;
            int nextColor = this.blockListPage < totalPages - 1 ? (nextHover ? 0xFFFFFFFF : 0xFF38BDF8) : 0xFF475569;

            graphics.text(this.font, Component.literal("<").withStyle(ChatFormatting.BOLD), xo + 216, yo + 11, prevColor, false);
            String pageStr = (this.blockListPage + 1) + "/" + totalPages;
            int pageW = this.font.width(pageStr);
            graphics.text(this.font, Component.literal(pageStr), xo + 232 - pageW / 2, yo + 12, 0xFF94A3B8, false);
            graphics.text(this.font, Component.literal(">").withStyle(ChatFormatting.BOLD), xo + 241, yo + 11, nextColor, false);
        }
    }

    private String formatCount(int count) {
        if (count >= 1_000_000) return (count / 1_000_000) + "M";
        if (count >= 1_000) return (count / 1_000) + "k";
        return String.valueOf(count);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        // 1. Energy Bar Tooltip (xo + 9..23, yo + 30..80)
        if (mouseX >= xo + 9 && mouseX <= xo + 23 && mouseY >= yo + 30 && mouseY <= yo + 80) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Energy Storage").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal(String.format("%,d", this.menu.getEnergy()) + " / 50,000 FE").withStyle(ChatFormatting.GRAY),
                    Component.literal("Max Speed Consumption: 20 FE/t (1 block/s)").withStyle(ChatFormatting.GREEN),
                    Component.literal("Throttles speed proportionally when low power").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
            return;
        }

        // 2. Radius Controls Tooltips
        if (mouseX >= xo + 28 && mouseX <= xo + 44 && mouseY >= yo + 42 && mouseY <= yo + 56) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Decrease Radius").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Min radius: 3 (7x7 box)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }
        if (mouseX >= xo + 60 && mouseX <= xo + 76 && mouseY >= yo + 42 && mouseY <= yo + 56) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Increase Radius").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                    Component.literal("Max radius: 21 (43x43 box)").withStyle(ChatFormatting.GRAY)
            ), mouseX, mouseY);
            return;
        }
        if (mouseX >= xo + 86 && mouseX <= xo + 102 && mouseY >= yo + 42 && mouseY <= yo + 56) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Rescan & Reset").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    Component.literal("Rescans all chunks in mining footprint").withStyle(ChatFormatting.GRAY),
                    Component.literal("Resets excavation coordinates back to top").withStyle(ChatFormatting.YELLOW)
            ), mouseX, mouseY);
            return;
        }

        // 3. Fence Slot Tooltip (xo + 116..134, yo + 46..64)
        if (mouseX >= xo + 116 && mouseX <= xo + 134 && mouseY >= yo + 46 && mouseY <= yo + 64) {
            if (this.menu.slots.get(0).getItem().isEmpty()) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("Perimeter Safety Railing Supply").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                        Component.literal("Insert any fence or iron bars here.").withStyle(ChatFormatting.GRAY),
                        Component.literal("Used in Phase 1 to enclose the 41x41 pit.").withStyle(ChatFormatting.GREEN)
                ), mouseX, mouseY);
                return;
            }
        }

        // 4. Throw-Back Filter Slot Tooltip (xo + 219..237, yo + 83..101)
        if (mouseX >= xo + 219 && mouseX <= xo + 237 && mouseY >= yo + 83 && mouseY <= yo + 101) {
            if (this.menu.slots.get(10).getItem().isEmpty()) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.literal("Throw-Back / Spoil Filter").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("Insert any block to eject it back into the pit.").withStyle(ChatFormatting.GRAY),
                        Component.literal("Ejected via rear conveyor as pit overburden/spoil.").withStyle(ChatFormatting.DARK_GRAY)
                ), mouseX, mouseY);
                return;
            }
        }

        // 5. Quick [SPOIL] Button Tooltip (xo + 212..248, yo + 21..30)
        if (mouseX >= xo + 212 && mouseX <= xo + 248 && mouseY >= yo + 21 && mouseY <= yo + 30) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.literal("Toggle Common Spoil").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Component.literal("Quickly toggles Dirt, Sand, Gravel, Stone,").withStyle(ChatFormatting.GRAY),
                    Component.literal("Cobblestone, Deepslate & Tuff as Throw-Back.").withStyle(ChatFormatting.GRAY),
                    Component.literal("Ejects unwanted spoil into the rear pit void.").withStyle(ChatFormatting.DARK_GRAY)
            ), mouseX, mouseY);
            return;
        }

        // 6. Manifest Block Slots Tooltip
        QuarryControllerBlockEntity be = this.menu.getBlockEntity();
        List<QuarryControllerBlockEntity.BlockCountEntry> blockList = be != null ? be.getClientBlockList() : List.of();
        int totalRem = this.menu.getTotalRemainingBlocks();
        int pageSize = 8;
        int startIndex = this.blockListPage * pageSize;
        int endIndex = Math.min(blockList.size(), startIndex + pageSize);

        for (int i = startIndex; i < endIndex; i++) {
            int slotIdx = i - startIndex;
            int col = slotIdx % 4;
            int row = slotIdx / 4;
            int bx = xo + 150 + col * 24;
            int by = yo + 32 + row * 22;

            if (mouseX >= bx && mouseX <= bx + 18 && mouseY >= by && mouseY <= by + 18) {
                var entry = blockList.get(i);
                ItemStack stack = new ItemStack(entry.block());
                String blockName = stack.getHoverName().getString();
                double pct = totalRem > 0 ? (entry.count() * 100.0 / totalRem) : 0.0;
                boolean isTB = be != null && be.isThrowBack(entry.block());

                List<Component> tips = new ArrayList<>();
                tips.add(Component.literal(blockName).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tips.add(Component.literal(String.format("%,d", entry.count()) + " remaining").withStyle(ChatFormatting.WHITE));
                tips.add(Component.literal(String.format("%.2f%% of total mining volume", pct)).withStyle(ChatFormatting.GRAY));

                if (isTB) {
                    tips.add(Component.literal("▶ THROW BACK TO PIT").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                    tips.add(Component.literal("Ejected via rear conveyor back into pit").withStyle(ChatFormatting.YELLOW));
                    tips.add(Component.literal("Click to switch to normal conveyor export").withStyle(ChatFormatting.DARK_GRAY));
                } else {
                    tips.add(Component.literal("▶ NORMAL EXPORT").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    tips.add(Component.literal("Exported via side conveyors to factory").withStyle(ChatFormatting.GRAY));
                    tips.add(Component.literal("Click to toggle Throw-Back to pit").withStyle(ChatFormatting.DARK_GRAY));
                }

                graphics.setComponentTooltipForNextFrame(this.font, tips, mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == InputConstants.MOUSE_BUTTON_LEFT || button == 0) {
            int xo = this.leftPos;
            int yo = this.topPos;

            // [-] Radius button
            if (mouseX >= xo + 28 && mouseX <= xo + 44 && mouseY >= yo + 42 && mouseY <= yo + 56) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 0);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // [+] Radius button
            if (mouseX >= xo + 60 && mouseX <= xo + 76 && mouseY >= yo + 42 && mouseY <= yo + 56) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 1);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // [R] Rescan button
            if (mouseX >= xo + 86 && mouseX <= xo + 102 && mouseY >= yo + 42 && mouseY <= yo + 56) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 2);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // [SPOIL] Quick Spoil Toggle button
            if (mouseX >= xo + 212 && mouseX <= xo + 248 && mouseY >= yo + 21 && mouseY <= yo + 30) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.menu.clickMenuButton(this.minecraft.player, 3);
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 3);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            // Manifest Block Slots Click -> Toggle Throw-Back
            QuarryControllerBlockEntity be = this.menu.getBlockEntity();
            List<QuarryControllerBlockEntity.BlockCountEntry> blockList = be != null ? be.getClientBlockList() : List.of();
            int pageSize = 8;
            int startIndex = this.blockListPage * pageSize;
            int endIndex = Math.min(blockList.size(), startIndex + pageSize);

            for (int i = startIndex; i < endIndex; i++) {
                int slotIdx = i - startIndex;
                int col = slotIdx % 4;
                int row = slotIdx / 4;
                int bx = xo + 150 + col * 24;
                int by = yo + 32 + row * 22;

                if (mouseX >= bx && mouseX <= bx + 18 && mouseY >= by && mouseY <= by + 18) {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.menu.clickMenuButton(this.minecraft.player, 10 + i);
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 10 + i);
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2f));
                    }
                    return true;
                }
            }

            // Manifest [<] Arrow button
            int totalPages = Math.max(1, (int) Math.ceil(blockList.size() / 8.0));

            if (totalPages > 1) {
                if (mouseX >= xo + 215 && mouseX <= xo + 224 && mouseY >= yo + 11 && mouseY <= yo + 20) {
                    if (this.blockListPage > 0) {
                        this.blockListPage--;
                        if (this.minecraft != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        }
                    }
                    return true;
                }
                if (mouseX >= xo + 239 && mouseX <= xo + 248 && mouseY >= yo + 11 && mouseY <= yo + 20) {
                    if (this.blockListPage < totalPages - 1) {
                        this.blockListPage++;
                        if (this.minecraft != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        }
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int xo = this.leftPos;
        int yo = this.topPos;

        // If hovering over the Manifest monitor (xo + 143..250, yo + 6..80), allow mouse wheel scrolling
        if (mouseX >= xo + 143 && mouseX <= xo + 250 && mouseY >= yo + 6 && mouseY <= yo + 80) {
            QuarryControllerBlockEntity be = this.menu.getBlockEntity();
            List<QuarryControllerBlockEntity.BlockCountEntry> blockList = be != null ? be.getClientBlockList() : List.of();
            int totalPages = Math.max(1, (int) Math.ceil(blockList.size() / 8.0));

            if (totalPages > 1) {
                if (scrollY > 0 && this.blockListPage > 0) {
                    this.blockListPage--;
                    return true;
                } else if (scrollY < 0 && this.blockListPage < totalPages - 1) {
                    this.blockListPage++;
                    return true;
                }
            }
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
