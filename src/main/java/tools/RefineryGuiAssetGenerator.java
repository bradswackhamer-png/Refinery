package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates dark gunmetal industrial GUI textures for:
 * - Burner Refinery Furnace (textures/gui/burner_refinery_furnace.png)
 * - Electric Refinery Furnace (textures/gui/electric_refinery_furnace.png)
 * - 3-Stage Petroleum Refinery (textures/gui/refinery.png)
 *
 * Matches the dark industrial steel plate aesthetic of Multi-Smelter, Industrial Grinder,
 * Blast Furnace, Small Boiler, and Metal Press (#2B3036 base, #353B43 riveted plates,
 * #1D2126 recessed slot wells, and #14191F gauge/monitor wells).
 */
public class RefineryGuiAssetGenerator {

    private static final String GUI_DIR = "src/main/resources/assets/refinery/textures/gui/";

    public static void main(String[] args) throws Exception {
        new File(GUI_DIR).mkdirs();
        generateBurnerFurnaceGui();
        generateElectricFurnaceGui();
        generateRefineryGui();
        System.out.println("All Refinery & Furnace GUI textures generated successfully!");
    }

    public static void generateBurnerFurnaceGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176;
        int h = 166;

        // 1. Base Industrial Steel Panel
        drawBaseFrame(g, w, h, 5001L);

        // 2. Left Fuel & Combustion Housing Plate (x: 7, y: 16, w: 75, h: 55)
        drawPanelPlate(g, 7, 16, 75, 55);

        // 3 Fuel Input Slot Wells:
        // Slot 0 at (12, 20) -> well (11, 19)
        // Slot 1 at (30, 20) -> well (29, 19)
        // Slot 2 at (48, 20) -> well (47, 19)
        drawRecessedSlotWell(g, 11, 19);
        drawRecessedSlotWell(g, 29, 19);
        drawRecessedSlotWell(g, 47, 19);

        // Burn Flame Gauge Well (x: 11, y: 45, w: 34, h: 20) -> inner (12, 46, 32, 18)
        drawRecessedWell(g, 11, 45, 34, 20, new Color(0x16, 0x13, 0x13));
        drawFlameIconMuted(g, 22, 49);

        // Downward arrow from fuel slots to bucket return slot
        g.setColor(new Color(0x74, 0x80, 0x90));
        g.drawLine(55, 39, 55, 43);
        g.drawLine(53, 42, 55, 44);
        g.drawLine(57, 42, 55, 44);

        // Empty Bucket Return Slot Well: Slot 3 at (48, 47) -> well (47, 46)
        drawRecessedSlotWell(g, 47, 46);

        // Temperature Thermometer Gauge Well (x: 67, y: 19, w: 12, h: 46) -> inner (68, 20, 10, 44)
        drawRecessedWell(g, 67, 19, 12, 46, new Color(0x12, 0x16, 0x1C));
        drawGaugeTicks(g, 68, 20, 10, 44, 4);

        // 3. Right Digital Telemetry & Control Housing Plate (x: 84, y: 16, w: 85, h: 55)
        drawPanelPlate(g, 84, 16, 85, 55);

        // Recessed Digital Telemetry Screen Well (x: 88, y: 19, w: 77, h: 33) -> inner (89, 20, 75, 31)
        drawRecessedWell(g, 88, 19, 77, 33, new Color(0x0E, 0x13, 0x18));
        // Subtle CRT/LCD scanline accents inside telemetry monitor
        g.setColor(new Color(0x13, 0x1A, 0x22));
        for (int sy = 22; sy < 50; sy += 2) {
            g.drawLine(90, sy, 162, sy);
        }

        // Redstone Control Button Recess (x: 88, y: 54, w: 77, h: 14)
        drawRecessedWell(g, 88, 54, 77, 14, new Color(0x18, 0x1C, 0x22));

        // 4. Player Inventory & Hotbar Slot Wells
        drawPlayerInventoryWells(g, 83, 141);

        g.dispose();
        ImageIO.write(gui, "PNG", new File(GUI_DIR + "burner_refinery_furnace.png"));
    }

    public static void generateElectricFurnaceGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176;
        int h = 166;

        // 1. Base Industrial Steel Panel
        drawBaseFrame(g, w, h, 5002L);

        // 2. Left High-Voltage & Thermal Housing Plate (x: 7, y: 16, w: 75, h: 55)
        drawPanelPlate(g, 7, 16, 75, 55);

        // High-Voltage RF Energy Storage Well (x: 12, y: 19, w: 18, h: 46) -> inner (13, 20, 16, 44)
        drawRecessedWell(g, 12, 19, 18, 46, new Color(0x14, 0x18, 0x1E));
        drawGaugeTicks(g, 13, 20, 16, 44, 4);

        // Induction Heating Coil Chamber Well (x: 34, y: 24, w: 28, h: 36) -> inner (35, 25, 26, 34)
        drawRecessedWell(g, 34, 24, 28, 36, new Color(0x11, 0x17, 0x1D));
        drawInductionCoilMuted(g, 38, 29, 20, 26);

        // Temperature Thermometer Gauge Well (x: 66, y: 19, w: 12, h: 46) -> inner (67, 20, 10, 44)
        drawRecessedWell(g, 66, 19, 12, 46, new Color(0x12, 0x16, 0x1C));
        drawGaugeTicks(g, 67, 20, 10, 44, 4);

        // 3. Right Digital Telemetry & Control Housing Plate (x: 84, y: 16, w: 85, h: 55)
        drawPanelPlate(g, 84, 16, 85, 55);

        // Recessed Digital Telemetry Screen Well (x: 88, y: 19, w: 77, h: 33) -> inner (89, 20, 75, 31)
        drawRecessedWell(g, 88, 19, 77, 33, new Color(0x0E, 0x13, 0x18));
        g.setColor(new Color(0x13, 0x1A, 0x22));
        for (int sy = 22; sy < 50; sy += 2) {
            g.drawLine(90, sy, 162, sy);
        }

        // Redstone Control Button Recess (x: 88, y: 54, w: 77, h: 14)
        drawRecessedWell(g, 88, 54, 77, 14, new Color(0x18, 0x1C, 0x22));

        // 4. Player Inventory & Hotbar Slot Wells
        drawPlayerInventoryWells(g, 83, 141);

        g.dispose();
        ImageIO.write(gui, "PNG", new File(GUI_DIR + "electric_refinery_furnace.png"));
    }

    public static void generateRefineryGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176;
        int h = 176;

        // 1. Base Industrial Steel Panel (176x176)
        drawBaseFrame(g, w, h, 5003L);

        // 2. Left Crude Feed & Heat Panel Plate (x: 7, y: 16, w: 44, h: 64)
        drawPanelPlate(g, 7, 16, 44, 64);

        // Crude Oil Feed Tank Well (x: 11, y: 19, w: 16, h: 32) -> inner (12, 20, 14, 30)
        drawRecessedWell(g, 11, 19, 16, 32, new Color(0x10, 0x13, 0x17));
        drawGaugeTicks(g, 12, 20, 14, 30, 3);

        // Furnace Heat Thermometer Well (x: 31, y: 19, w: 16, h: 32) -> inner (32, 20, 14, 30)
        drawRecessedWell(g, 31, 19, 16, 32, new Color(0x14, 0x12, 0x14));
        drawGaugeTicks(g, 32, 20, 14, 30, 3);

        // Oil Sand Cracking Progress Groove (x: 11, y: 53, w: 18, h: 4) -> inner (12, 54, 16, 2)
        drawRecessedWell(g, 11, 53, 18, 4, new Color(0x14, 0x17, 0x1B));

        // Slot 0 (Crude Oil Bucket / Oil Sand Input): (12, 59) -> well (11, 58)
        drawRecessedSlotWell(g, 11, 58);
        // Slot 1 (Empty Bucket / Sand Return): (30, 59) -> well (29, 58)
        drawRecessedSlotWell(g, 29, 58);

        // 3. Center Fractional Distillation & Product Tanks Panel Plate (x: 53, y: 16, w: 75, h: 64)
        drawPanelPlate(g, 53, 16, 75, 64);

        // 3 Stage Button Recessed Mounting Frames (x: 56, y: 19 / 37 / 55, w: 24, h: 16)
        drawRecessedWell(g, 56, 19, 24, 16, new Color(0x15, 0x19, 0x1E));
        drawRecessedWell(g, 56, 37, 24, 16, new Color(0x15, 0x19, 0x1E));
        drawRecessedWell(g, 56, 55, 24, 16, new Color(0x15, 0x19, 0x1E));

        // 3 Output Tank Sight Glass Wells (y: 19, w: 12, h: 52) -> inner (x+1, 20, 10, 50)
        // Stage 1: Liquid Tar (x: 83)
        drawRecessedWell(g, 83, 19, 12, 52, new Color(0x10, 0x13, 0x17));
        drawGaugeTicks(g, 84, 20, 10, 50, 4);
        // Stage 2: Diesel (x: 98)
        drawRecessedWell(g, 98, 19, 12, 52, new Color(0x10, 0x13, 0x17));
        drawGaugeTicks(g, 99, 20, 10, 50, 4);
        // Stage 3: Gasoline (x: 113)
        drawRecessedWell(g, 113, 19, 12, 52, new Color(0x10, 0x13, 0x17));
        drawGaugeTicks(g, 114, 20, 10, 50, 4);

        // Distillation Progress Groove (x: 56, y: 73, w: 69, h: 5) -> inner (57, 74, 67, 3)
        drawRecessedWell(g, 56, 73, 69, 5, new Color(0x12, 0x16, 0x1A));

        // 4. Right Bitumen Byproduct Panel Plate (x: 130, y: 16, w: 39, h: 64)
        drawPanelPlate(g, 130, 16, 39, 64);

        // Bitumen Header Readout Well (x: 132, y: 19, w: 35, h: 14) -> inner (133, 20, 33, 12)
        drawRecessedWell(g, 132, 19, 35, 14, new Color(0x0E, 0x13, 0x18));

        // 2x2 Bitumen Output Slot Wells:
        // Slot 2: (132, 37) -> well (131, 36)
        // Slot 3: (150, 37) -> well (149, 36)
        // Slot 4: (132, 55) -> well (131, 54)
        // Slot 5: (150, 55) -> well (149, 54)
        drawRecessedSlotWell(g, 131, 36);
        drawRecessedSlotWell(g, 149, 36);
        drawRecessedSlotWell(g, 131, 54);
        drawRecessedSlotWell(g, 149, 54);

        // 5. Multiblock Status LED Bezel in Top-Right Header (x: 161, y: 5, w: 8, h: 8)
        drawRecessedWell(g, 161, 5, 8, 8, new Color(0x12, 0x15, 0x19));

        // 6. Player Inventory & Hotbar Slot Wells (y: 93 and y: 151)
        drawPlayerInventoryWells(g, 93, 151);

        g.dispose();
        ImageIO.write(gui, "PNG", new File(GUI_DIR + "refinery.png"));
    }

    private static void drawBaseFrame(Graphics2D g, int w, int h, long seed) {
        fillNoisyRect(g, 0, 0, w, h, new Color(0x2B, 0x30, 0x36), 3, seed);

        // Outer 3D Beveled Border
        g.setColor(new Color(0x56, 0x5F, 0x6C));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x15, 0x18, 0x1B));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);

        // 4 Corner Rivets
        drawBolt(g, 3, 3);
        drawBolt(g, w - 5, 3);
        drawBolt(g, 3, h - 5);
        drawBolt(g, w - 5, h - 5);
    }

    private static void drawPlayerInventoryWells(Graphics2D g, int invWellY, int hotbarWellY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, invWellY + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, hotbarWellY);
        }
    }

    private static void drawGaugeTicks(Graphics2D g, int x, int y, int w, int h, int divisions) {
        g.setColor(new Color(0x32, 0x3A, 0x44));
        int step = Math.max(4, h / divisions);
        for (int gy = y + step; gy < y + h - 2; gy += step) {
            g.drawLine(x + 1, gy, x + 3, gy);
            g.drawLine(x + w - 4, gy, x + w - 2, gy);
        }
    }

    private static void drawFlameIconMuted(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x3D, 0x24, 0x1D));
        int[] xp = {x + 5, x + 7, x + 10, x + 11, x + 8, x + 9, x + 7, x + 4, x + 2, x + 1, x + 3};
        int[] yp = {y + 1, y + 3, y + 6,  y + 10, y + 11, y + 8, y + 11, y + 11, y + 8, y + 5, y + 4};
        g.fillPolygon(xp, yp, xp.length);
    }

    private static void drawInductionCoilMuted(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x1E, 0x2B, 0x36));
        for (int cy = y + 2; cy < y + h - 2; cy += 5) {
            g.fillRect(x + 2, cy, w - 4, 2);
        }
        // Center lightning silhouette
        g.setColor(new Color(0x25, 0x39, 0x49));
        int cx = x + w / 2;
        int cy = y + h / 2;
        int[] xp = {cx + 2, cx - 3, cx, cx - 2, cx + 4, cx + 1, cx + 3};
        int[] yp = {cy - 7, cy, cy, cy + 7, cy - 1, cy - 1, cy - 7};
        g.fillPolygon(xp, yp, xp.length);
    }

    private static void drawRecessedWell(Graphics2D g, int x, int y, int w, int h, Color innerFill) {
        g.setColor(new Color(0x14, 0x17, 0x1A));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x4D, 0x56, 0x63));
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.setColor(innerFill);
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    private static void drawRecessedSlotWell(Graphics2D g, int x, int y) {
        drawRecessedWell(g, x, y, 18, 18, new Color(0x1D, 0x21, 0x26));
    }

    private static void drawPanelPlate(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x43), 3, 4001L + x * 31L + y);
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x1A, 0x1D, 0x20));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        drawBolt(g, x + 2, y + 2);
        drawBolt(g, x + w - 4, y + 2);
        drawBolt(g, x + 2, y + h - 4);
        drawBolt(g, x + w - 4, y + h - 4);
    }

    private static void drawBolt(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x12, 0x14, 0x16));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x94, 0xA1, 0xB0));
        g.fillRect(x, y, 1, 1);
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int noiseRange, long seed) {
        Random r = new Random(seed);
        for (int px = x; px < x + w; px++) {
            for (int py = y; py < y + h; py++) {
                int n = r.nextInt(noiseRange * 2 + 1) - noiseRange;
                int red = clamp(base.getRed() + n);
                int green = clamp(base.getGreen() + n);
                int blue = clamp(base.getBlue() + n);
                g.setColor(new Color(red, green, blue));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
