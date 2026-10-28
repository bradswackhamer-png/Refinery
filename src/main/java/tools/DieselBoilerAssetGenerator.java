package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class DieselBoilerAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateSmallDieselAtlas();
        generateLargeDieselAtlas();
        generateSmallDieselGui();
        generateLargeDieselGui();
        generateBlockTextures();
        System.out.println("Diesel Boiler entity atlases, GUI textures, and block textures generated successfully!");
    }

    public static void generateBlockTextures() throws Exception {
        // 1. Small Diesel Boiler Controller Front (16x16)
        generateFrontTexture("small_diesel_boiler_controller_front.png", false, false);
        generateFrontTexture("small_diesel_boiler_controller_front_active.png", true, false);

        // 2. Large Diesel Boiler Controller Front (16x16)
        generateFrontTexture("large_diesel_boiler_controller_front.png", false, true);
        generateFrontTexture("large_diesel_boiler_controller_front_active.png", true, true);
    }

    private static void generateFrontTexture(String fileName, boolean active, boolean dual) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Dark steel casing background
        fillNoisyRect(g, 0, 0, 16, 16, new Color(0x35, 0x3B, 0x43), 2, 9001L);
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawRect(0, 0, 15, 15);
        g.setColor(new Color(0x18, 0x1B, 0x1E));
        g.drawRect(1, 1, 13, 13);

        // Corner rivets
        drawBolt(g, 2, 2);
        drawBolt(g, 12, 2);
        drawBolt(g, 2, 12);
        drawBolt(g, 12, 12);

        if (!dual) {
            // Single Diesel Injector Hub at center
            g.setColor(new Color(0x1A, 0x1D, 0x21));
            g.fillOval(4, 4, 8, 8);
            g.setColor(new Color(0xB4, 0x8A, 0x1A)); // Brass fitting
            g.drawOval(4, 4, 7, 7);

            if (active) {
                g.setColor(new Color(0xF5, 0x9E, 0x0B));
                g.fillOval(6, 6, 4, 4);
                g.setColor(new Color(0xFE, 0xEE, 0x87));
                g.fillRect(7, 7, 2, 2);
            } else {
                g.setColor(new Color(0x11, 0x13, 0x16));
                g.fillOval(6, 6, 4, 4);
            }
        } else {
            // Dual Diesel Injector Hubs side by side
            for (int cx : new int[]{3, 9}) {
                g.setColor(new Color(0x1A, 0x1D, 0x21));
                g.fillOval(cx, 5, 5, 6);
                g.setColor(new Color(0xB4, 0x8A, 0x1A));
                g.drawOval(cx, 5, 4, 5);

                if (active) {
                    g.setColor(new Color(0xF5, 0x9E, 0x0B));
                    g.fillOval(cx + 1, 6, 3, 3);
                    g.setColor(new Color(0xFE, 0xEE, 0x87));
                    g.fillRect(cx + 2, 7, 1, 1);
                } else {
                    g.setColor(new Color(0x11, 0x13, 0x16));
                    g.fillOval(cx + 1, 6, 3, 3);
                }
            }
        }

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/block/" + fileName);
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated block texture: " + out.getAbsolutePath());
    }

    // =========================================================================
    // 1. SMALL DIESEL BOILER ENTITY ATLAS (256x256)
    // =========================================================================
    public static void generateSmallDieselAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x28, 0x2D, 0x33), 4, 5001L);

        // 1. Shell cladding
        drawBoilerShell(g, 0, 0, 96, 48);
        // 2. Drum head
        drawDrumHead(g, 96, 0, 64, 48);
        // 3. Skid steel
        drawSkid(g, 160, 0, 48, 48);
        // 4. Smokestack
        drawSmokestack(g, 208, 0, 48, 48);
        // 5. Diesel Burner Face & Injection Hub (0, 48, 64, 64)
        drawDieselBurnerFace(g, 0, 48, 64, 64);
        // 6. Active glowing flame (64, 48, 32, 32)
        drawFlameSightGlass(g, 64, 48, 32, 32, true);
        // 7. Idle sight glass (96, 48, 32, 32)
        drawFlameSightGlass(g, 96, 48, 32, 32, false);
        // 8. Steam pipe (128, 48, 64, 32)
        drawPipe(g, 128, 48, 64, 32);
        // 9. Valve wheel (192, 48, 32, 32)
        drawWheel(g, 192, 48, 32, 32);
        // 10. Pressure gauge (224, 48, 32, 32)
        drawGauge(g, 224, 48, 32, 32);
        // 11. Control cabinet (0, 112, 64, 48)
        drawControlCabinet(g, 0, 112, 64, 48);
        // 12. Water column (64, 112, 48, 48)
        drawWaterColumn(g, 64, 112, 48, 48);
        // 13. Diesel Fuel Rail & Injection Pump (112, 112, 48, 48)
        drawDieselFuelRail(g, 112, 112, 48, 48);
        // 14. Reinforcement straps (160, 112, 96, 48)
        drawStraps(g, 160, 112, 96, 48);

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/entity/small_diesel_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated: " + out.getAbsolutePath());
    }

    // =========================================================================
    // 2. LARGE DIESEL BOILER ENTITY ATLAS (256x256)
    // =========================================================================
    public static void generateLargeDieselAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x28, 0x2D, 0x33), 4, 6001L);

        drawBoilerShell(g, 0, 0, 96, 48);
        drawDrumHead(g, 96, 0, 64, 48);
        drawSkid(g, 160, 0, 48, 48);
        drawSmokestack(g, 208, 0, 48, 48);
        drawDieselBurnerFace(g, 0, 48, 64, 64);
        drawFlameSightGlass(g, 64, 48, 32, 32, true);
        drawFlameSightGlass(g, 96, 48, 32, 32, false);
        drawPipe(g, 128, 48, 64, 32);
        drawWheel(g, 192, 48, 32, 32);
        drawGauge(g, 224, 48, 32, 32);
        drawControlCabinet(g, 0, 112, 64, 48);
        drawWaterColumn(g, 64, 112, 48, 48);
        drawDieselFuelRail(g, 112, 112, 48, 48);
        drawStraps(g, 160, 112, 96, 48);
        drawWalkway(g, 0, 160, 64, 48);

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/entity/large_diesel_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated: " + out.getAbsolutePath());
    }

    // =========================================================================
    // 3. SMALL DIESEL BOILER GUI TEXTURE (256x256)
    // 3 Tanks: Diesel (x: 18..34), Water (x: 44..60), Steam (x: 142..158)
    // Center: Fuel Burner & Overheat Warning, Bucket Slot at (98, 34)
    // =========================================================================
    public static void generateSmallDieselGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        fillNoisyRect(g, 0, 0, 176, 166, new Color(0x38, 0x3E, 0x47), 3, 7001L);

        // Outer rim
        g.setColor(new Color(0x52, 0x5B, 0x68));
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 165);
        g.setColor(new Color(0x1C, 0x20, 0x24));
        g.drawLine(175, 0, 175, 165);
        g.drawLine(0, 165, 175, 165);

        // Header plate
        drawPanelPlate(g, 6, 6, 164, 14);
        drawBolt(g, 9, 9);
        drawBolt(g, 163, 9);

        // Tank 1: Diesel Fuel Buffer Well (x: 17, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 17, 15, 18, 54, new Color(0x18, 0x16, 0x0E));
        g.setColor(new Color(0xB4, 0x8A, 0x1A));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(19, gy, 32, gy);
        }

        // Tank 2: Water Buffer Well (x: 43, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 43, 15, 18, 54, new Color(0x0E, 0x14, 0x22));
        g.setColor(new Color(0x1E, 0x3A, 0x8A));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(45, gy, 58, gy);
        }

        // Tank 3: Steam Buffer Well (x: 141, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 141, 15, 18, 54, new Color(0x0C, 0x18, 0x24));
        g.setColor(new Color(0x02, 0x84, 0xC7));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(143, gy, 156, gy);
        }

        // Center Panel Plate (x: 68, y: 20, w: 66, h: 46)
        drawPanelPlate(g, 68, 20, 66, 46);

        // Bucket Input & Empty Slot Wells at (74, 34) and (110, 34)
        drawRecessedSlotWell(g, 73, 33);
        drawRecessedSlotWell(g, 109, 33);

        // Center Burner Flame Well at (93, 37, 16, 12)
        drawRecessedWell(g, 93, 37, 16, 12, new Color(0x1A, 0x1D, 0x21));

        // Overheat Warning Track (x: 68, y: 58, w: 66, h: 6)
        drawRecessedWell(g, 68, 58, 66, 6, new Color(0x14, 0x16, 0x19));

        // Player Inventory (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // Player Hotbar (1 row of 9 at y: 142)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/gui/small_diesel_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated: " + out.getAbsolutePath());
    }

    // =========================================================================
    // 4. LARGE DIESEL BOILER GUI TEXTURE (256x256)
    // 3 Tanks: Diesel (x: 18..34), Water (x: 44..60), Steam (x: 142..158)
    // Center: Dual Injector Burners, 4 Canister Slots
    // =========================================================================
    public static void generateLargeDieselGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        fillNoisyRect(g, 0, 0, 176, 166, new Color(0x35, 0x3B, 0x43), 3, 8001L);

        // Outer rim
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 165);
        g.setColor(new Color(0x18, 0x1B, 0x1E));
        g.drawLine(175, 0, 175, 165);
        g.drawLine(0, 165, 175, 165);

        // Header plate
        drawPanelPlate(g, 6, 6, 164, 14);
        drawBolt(g, 9, 9);
        drawBolt(g, 163, 9);

        // Tank 1: Diesel Fuel Buffer Well (x: 17, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 17, 15, 18, 54, new Color(0x18, 0x16, 0x0E));
        g.setColor(new Color(0xB4, 0x8A, 0x1A));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(19, gy, 32, gy);
        }

        // Tank 2: Water Buffer Well (x: 43, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 43, 15, 18, 54, new Color(0x0E, 0x14, 0x22));
        g.setColor(new Color(0x1E, 0x3A, 0x8A));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(45, gy, 58, gy);
        }

        // Tank 3: Steam Buffer Well (x: 141, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 141, 15, 18, 54, new Color(0x0C, 0x18, 0x24));
        g.setColor(new Color(0x02, 0x84, 0xC7));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(143, gy, 156, gy);
        }

        // Center Panel Plate (x: 66, y: 18, w: 70, h: 48)
        drawPanelPlate(g, 66, 18, 70, 48);

        // 4 Fluid Canister / Bucket slot wells: 2x2 grid at (73, 24), (111, 24), (73, 44), (111, 44)
        drawRecessedSlotWell(g, 72, 23);
        drawRecessedSlotWell(g, 110, 23);
        drawRecessedSlotWell(g, 72, 43);
        drawRecessedSlotWell(g, 110, 43);

        // Dual Injector Combustion Indicator Wells at (93, 26, 14, 12) and (93, 44, 14, 12)
        drawRecessedWell(g, 93, 26, 14, 12, new Color(0x18, 0x1B, 0x1F));
        drawRecessedWell(g, 93, 44, 14, 12, new Color(0x18, 0x1B, 0x1F));

        // Overheat Warning Track (x: 66, y: 68, w: 70, h: 6)
        drawRecessedWell(g, 66, 68, 70, 6, new Color(0x14, 0x16, 0x19));

        // Player Inventory (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // Player Hotbar (1 row of 9 at y: 142)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/gui/large_diesel_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated: " + out.getAbsolutePath());
    }

    // Helper drawing methods
    private static void drawBoilerShell(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x3B, 0x43, 0x4D), 4, 101L);
        g.setColor(new Color(0x24, 0x28, 0x2E));
        g.drawLine(x, y + 16, x + w - 1, y + 16);
        g.drawLine(x, y + 32, x + w - 1, y + 32);
        g.setColor(new Color(0x55, 0x5F, 0x6D));
        g.drawLine(x, y + 17, x + w - 1, y + 17);
        g.drawLine(x, y + 33, x + w - 1, y + 33);
    }

    private static void drawDrumHead(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x33, 0x39, 0x42), 3, 102L);
        g.setColor(new Color(0x1F, 0x23, 0x27));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x4B, 0x54, 0x61));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);
    }

    private static void drawSkid(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1F, 0x22, 0x27), 3, 103L);
        g.setColor(new Color(0x10, 0x12, 0x15));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    }

    private static void drawSmokestack(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2A, 0x2E, 0x35), 4, 104L);
        g.setColor(new Color(0x17, 0x19, 0x1C));
        g.drawLine(x, y, x, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
    }

    private static void drawDieselBurnerFace(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2D, 0x33), 3, 105L);
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 3;

        // Circular machined burner housing
        g.setColor(new Color(0x16, 0x18, 0x1B));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x38, 0x3F, 0x48));
        g.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);

        // Stainless fuel injection nozzle hub with brass compression fittings
        g.setColor(new Color(0xB4, 0x8A, 0x1A));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(0xFE, 0xE0, 0x66));
        g.drawOval(cx - 10, cy - 10, 20, 20);

        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI / 4.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawBolt(g, bx - 1, by - 1);
        }
    }

    private static void drawFlameSightGlass(Graphics2D g, int x, int y, int w, int h, boolean active) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1B, 0x1E, 0x22), 2, 106L);
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;

        g.setColor(new Color(0x0C, 0x0E, 0x10));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        if (active) {
            g.setColor(new Color(0xDC, 0x26, 0x26));
            g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
            g.setColor(new Color(0xEA, 0x58, 0x0C));
            g.fillOval(cx - r + 4, cy - r + 4, (r - 4) * 2, (r - 4) * 2);
            g.setColor(new Color(0xF5, 0x9E, 0x0B));
            g.fillOval(cx - r + 6, cy - r + 6, (r - 6) * 2, (r - 6) * 2);
            g.setColor(new Color(0xFE, 0xF0, 0x8A));
            g.fillOval(cx - 3, cy - 3, 6, 6);
        } else {
            g.setColor(new Color(0x18, 0x1C, 0x20));
            g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        }
    }

    private static void drawPipe(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x3E, 0x45, 0x4E), 3, 107L);
        g.setColor(new Color(0x20, 0x23, 0x28));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    }

    private static void drawWheel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1A, 0x1D, 0x20), 2, 108L);
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.fillOval(cx - 3, cy - 3, 6, 6);
    }

    private static void drawGauge(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1C, 0x20, 0x24), 2, 109L);
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;
        g.setColor(new Color(0xD4, 0xAF, 0x37));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0xFA, 0xFA, 0xF9));
        g.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(cx, cy, cx + 4, cy - 5);
    }

    private static void drawControlCabinet(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xDF, 0xE5, 0xEC), 3, 110L);
        g.setColor(new Color(0x60, 0x6E, 0x80));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillOval(x + 28, y + 8, 4, 4);
        g.setColor(new Color(0xEF, 0x44, 0x44));
        g.fillOval(x + 36, y + 8, 4, 4);
    }

    private static void drawWaterColumn(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x26, 0x2A, 0x30), 3, 111L);
        g.setColor(new Color(0x60, 0xA5, 0xFA));
        g.fillRect(x + 20, y + 16, 8, h - 22);
    }

    private static void drawDieselFuelRail(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x26, 0x2B, 0x32), 3, 112L);
        // Golden diesel fuel delivery line with brass compression fittings
        g.setColor(new Color(0xCA, 0x8A, 0x04));
        g.fillRect(x + 6, y + h / 2 - 3, w - 12, 6);
        g.setColor(new Color(0xFE, 0xF0, 0x8A));
        g.drawRect(x + 6, y + h / 2 - 3, w - 12, 6);

        for (int fx = x + 12; fx < x + w - 10; fx += 12) {
            g.setColor(new Color(0x71, 0x3F, 0x12));
            g.fillRect(fx, y + 8, 4, h - 16);
            g.setColor(new Color(0xFE, 0xCD, 0x20));
            drawBolt(g, fx + 1, y + 10);
        }
    }

    private static void drawStraps(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x30, 0x36, 0x3E), 3, 113L);
        g.setColor(new Color(0x1B, 0x1E, 0x23));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
    }

    private static void drawWalkway(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x23, 0x27, 0x2C), 3, 114L);
        g.setColor(new Color(0x10, 0x12, 0x14));
        for (int gx = x; gx < x + w; gx += 4) {
            g.drawLine(gx, y, gx, y + h - 1);
        }
        for (int gy = y; gy < y + h; gy += 4) {
            g.drawLine(x, gy, x + w - 1, gy);
        }
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
        drawRecessedWell(g, x, y, 18, 18, new Color(0x1C, 0x20, 0x25));
    }

    private static void drawPanelPlate(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x38, 0x40), 3, 4001L);
        g.setColor(new Color(0x49, 0x51, 0x5C));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x18, 0x1B, 0x1E));
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
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
