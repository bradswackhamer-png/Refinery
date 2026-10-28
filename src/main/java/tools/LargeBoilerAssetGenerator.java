package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class LargeBoilerAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateGuiTexture();
        System.out.println("Large Boiler entity atlas and GUI textures generated successfully!");
    }

    // =========================================================================
    // 1. ENTITY TEXTURE ATLAS (256x256)
    // =========================================================================
    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark industrial steel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x28, 0x2D, 0x33), 4, 1001L);

        // 1. Cylindrical Boiler Shell Cladding (0, 0, 96, 48)
        drawBoilerShellCladding(g, 0, 0, 96, 48);

        // 2. Drum End Head Plates (96, 0, 64, 48)
        drawDrumEndHead(g, 96, 0, 64, 48);

        // 3. Structural Skid Base & Saddle Supports (160, 0, 48, 48)
        drawStructuralSkidSteel(g, 160, 0, 48, 48);

        // 4. Twin Smokestack Flue & Flange Pipe (208, 0, 48, 48)
        drawSmokestackSteel(g, 208, 0, 48, 48);

        // 5. Front Circular Boiler Door & Hinges (0, 48, 64, 64)
        drawFrontBoilerDoor(g, 0, 48, 64, 64);

        // 6. Burner Sight Glass - Active (Glowing Fire) (64, 48, 32, 32)
        drawBurnerSightGlass(g, 64, 48, 32, 32, true);

        // 7. Burner Sight Glass - Idle (Dark Glass) (96, 48, 32, 32)
        drawBurnerSightGlass(g, 96, 48, 32, 32, false);

        // 8. Overhead Steam Manifold Header Pipe (128, 48, 64, 32)
        drawSteamPipeSteel(g, 128, 48, 64, 32);

        // 9. Globe Valve Handwheel (Red) (192, 48, 32, 32)
        drawValveHandwheel(g, 192, 48, 32, 32);

        // 10. Brass Dial Pressure Gauge (224, 48, 32, 32)
        drawPressureGauge(g, 224, 48, 32, 32);

        // 11. Industrial Electrical Control Cabinet (0, 112, 64, 48)
        drawControlCabinet(g, 0, 112, 64, 48);

        // 12. Water Level Sight Column & Dual Feed Pumps (64, 112, 48, 48)
        drawWaterColumn(g, 64, 112, 48, 48);

        // 13. Lower Arched Cleanout / Ash Doors (112, 112, 48, 48)
        drawCleanoutDoor(g, 112, 112, 48, 48);

        // 14. Heavy Riveted Reinforcement Straps (160, 112, 96, 48)
        drawReinforcementStrap(g, 160, 112, 96, 48);

        // 15. Walkway Grating & Handrails (0, 160, 64, 48)
        drawWalkwayGrating(g, 0, 160, 64, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/large_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated Large Boiler entity texture at: " + out.getAbsolutePath());
    }

    private static void drawBoilerShellCladding(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x3E, 0x46, 0x50), 4, 2001L);

        // Horizontal steel plate seams
        g.setColor(new Color(0x28, 0x2D, 0x33));
        g.drawLine(x, y + 16, x + w - 1, y + 16);
        g.drawLine(x, y + 32, x + w - 1, y + 32);

        g.setColor(new Color(0x56, 0x60, 0x6E));
        g.drawLine(x, y + 17, x + w - 1, y + 17);
        g.drawLine(x, y + 33, x + w - 1, y + 33);

        // Vertical panel seams & rivets
        for (int vx = x + 24; vx < x + w; vx += 24) {
            g.setColor(new Color(0x28, 0x2D, 0x33));
            g.drawLine(vx, y, vx, y + h - 1);
            g.setColor(new Color(0x56, 0x60, 0x6E));
            g.drawLine(vx + 1, y, vx + 1, y + h - 1);

            for (int ry = y + 4; ry < y + h; ry += 8) {
                drawRivet(g, vx - 3, ry);
                drawRivet(g, vx + 3, ry);
            }
        }
    }

    private static void drawDrumEndHead(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3C, 0x44), 3, 2002L);

        g.setColor(new Color(0x20, 0x24, 0x29));
        g.drawRect(x, y, w - 1, h - 1);
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        g.setColor(new Color(0x4C, 0x56, 0x63));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        for (int px = x + 6; px < x + w - 4; px += 8) {
            drawRivet(g, px, y + 4);
            drawRivet(g, px, y + h - 6);
        }
    }

    private static void drawStructuralSkidSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x20, 0x24, 0x29), 3, 2003L);

        g.setColor(new Color(0x12, 0x14, 0x17));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + 3, x + w - 1, y + 3);
        g.drawLine(x, y + h - 4, x + w - 1, y + h - 4);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        g.setColor(new Color(0x38, 0x3F, 0x47));
        g.drawLine(x, y + 1, x + w - 1, y + 1);
        g.drawLine(x, y + h - 2, x + w - 1, y + h - 2);

        for (int bx = x + 8; bx < x + w - 4; bx += 12) {
            drawBolt(g, bx, y + 6);
            drawBolt(g, bx, y + h - 8);
        }
    }

    private static void drawSmokestackSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2B, 0x30, 0x37), 4, 2004L);

        g.setColor(new Color(0x1B, 0x1E, 0x22));
        g.drawLine(x, y, x, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

        g.setColor(new Color(0x4A, 0x53, 0x5F));
        g.drawLine(x + 10, y, x + 10, y + h - 1);
        g.drawLine(x + 11, y, x + 11, y + h - 1);

        g.setColor(new Color(0x18, 0x1B, 0x1E));
        g.fillRect(x, y + 6, w, 4);
        g.fillRect(x, y + h - 10, w, 4);
        g.setColor(new Color(0x4A, 0x53, 0x5F));
        g.drawLine(x, y + 6, x + w - 1, y + 6);
        g.drawLine(x, y + h - 10, x + w - 1, y + h - 1);
    }

    private static void drawFrontBoilerDoor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2C, 0x31, 0x38), 3, 2005L);

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 3;

        g.setColor(new Color(0x18, 0x1A, 0x1D));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        g.setColor(new Color(0x3B, 0x42, 0x4C));
        g.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);

        g.setColor(new Color(0x25, 0x29, 0x2E));
        g.fillOval(cx - r + 7, cy - r + 7, (r - 7) * 2, (r - 7) * 2);

        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI / 6.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 5));
            int by = (int) (cy + Math.sin(angle) * (r - 5));
            drawBolt(g, bx - 1, by - 1);
        }

        // Heavy industrial hinge brackets on left
        g.setColor(new Color(0x11, 0x13, 0x15));
        g.fillRect(x + 2, cy - 18, 10, 8);
        g.fillRect(x + 2, cy + 10, 10, 8);
        g.setColor(new Color(0x4B, 0x53, 0x5E));
        g.drawRect(x + 2, cy - 18, 10, 8);
        g.drawRect(x + 2, cy + 10, 10, 8);

        // Center burner hub recess
        g.setColor(new Color(0x14, 0x16, 0x18));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(0x45, 0x4D, 0x57));
        g.drawOval(cx - 10, cy - 10, 20, 20);
    }

    private static void drawBurnerSightGlass(Graphics2D g, int x, int y, int w, int h, boolean active) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1E, 0x22, 0x26), 2, 2006L);

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;

        g.setColor(new Color(0x0E, 0x10, 0x12));
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
            g.setColor(new Color(0x1A, 0x20, 0x26));
            g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

            g.setColor(new Color(0x28, 0x33, 0x3E));
            g.drawLine(cx - 4, cy - 4, cx + 4, cy + 4);
        }

        g.setColor(new Color(0x4F, 0x58, 0x64));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
    }

    private static void drawSteamPipeSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x40, 0x47, 0x50), 3, 2007L);

        g.setColor(new Color(0x22, 0x26, 0x2B));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        g.setColor(new Color(0x63, 0x6F, 0x7E));
        g.drawLine(x, y + 4, x + w - 1, y + 4);
        g.drawLine(x, y + 5, x + w - 1, y + 5);

        for (int fx = x + 16; fx < x + w - 8; fx += 24) {
            g.setColor(new Color(0x1E, 0x22, 0x26));
            g.fillRect(fx, y, 6, h);
            g.setColor(new Color(0x50, 0x5A, 0x67));
            g.drawRect(fx, y, 6, h);
            drawBolt(g, fx + 2, y + 3);
            drawBolt(g, fx + 2, y + h - 5);
        }
    }

    private static void drawValveHandwheel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1C, 0x20, 0x24), 2, 2008L);

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;

        g.setColor(new Color(0x7F, 0x1D, 0x1D));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.drawOval(cx - r + 1, cy - r + 1, (r - 1) * 2, (r - 1) * 2);

        g.setColor(new Color(0x1A, 0x1C, 0x1E));
        g.fillOval(cx - r + 4, cy - r + 4, (r - 4) * 2, (r - 4) * 2);

        g.setColor(new Color(0xDC, 0x26, 0x26));
        for (int i = 0; i < 4; i++) {
            double angle = i * (Math.PI / 2.0);
            int sx = (int) (cx + Math.cos(angle) * (r - 2));
            int sy = (int) (cy + Math.sin(angle) * (r - 2));
            g.drawLine(cx, cy, sx, sy);
        }

        g.setColor(new Color(0xF8, 0x71, 0x71));
        g.fillOval(cx - 3, cy - 3, 6, 6);
    }

    private static void drawPressureGauge(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1E, 0x22, 0x26), 2, 2009L);

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 2;

        g.setColor(new Color(0x9A, 0x7B, 0x2C));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0xD4, 0xAF, 0x37));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);

        g.setColor(new Color(0xFE, 0xFD, 0xF8));
        g.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);

        g.setColor(new Color(0x1F, 0x24, 0x2A));
        g.drawArc(cx - r + 5, cy - r + 5, (r - 5) * 2, (r - 5) * 2, 30, 240);

        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(cx, cy, cx + 5, cy - 6);

        g.setColor(new Color(0x1F, 0x24, 0x2A));
        g.fillOval(cx - 2, cy - 2, 4, 4);
    }

    private static void drawControlCabinet(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xE2, 0xE8, 0xF0), 3, 2010L);

        g.setColor(new Color(0x64, 0x74, 0x8B));
        g.drawRect(x, y, w - 1, h - 1);
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        g.setColor(new Color(0x1E, 0x3A, 0x8A));
        g.fillRect(x + 6, y + 6, 16, 10);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawRect(x + 6, y + 6, 16, 10);

        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillOval(x + 28, y + 8, 4, 4);
        g.setColor(new Color(0xEF, 0x44, 0x44));
        g.fillOval(x + 36, y + 8, 4, 4);
        g.setColor(new Color(0x3B, 0x82, 0xF6));
        g.fillOval(x + 44, y + 8, 4, 4);

        g.setColor(new Color(0x33, 0x41, 0x55));
        for (int gy = y + 22; gy < y + h - 6; gy += 4) {
            g.drawLine(x + 8, gy, x + w - 8, gy);
        }
    }

    private static void drawWaterColumn(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2D, 0x34), 3, 2011L);

        g.setColor(new Color(0x18, 0x1B, 0x1F));
        g.fillRect(x + 18, y + 4, 12, h - 8);

        g.setColor(new Color(0x60, 0xA5, 0xFA));
        g.fillRect(x + 20, y + 16, 8, h - 22);

        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.drawRect(x + 18, y + 4, 12, h - 8);

        for (int my = y + 8; my < y + h - 8; my += 6) {
            g.drawLine(x + 16, my, x + 18, my);
        }
    }

    private static void drawCleanoutDoor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2E, 0x33, 0x3A), 3, 2012L);

        g.setColor(new Color(0x16, 0x18, 0x1B));
        g.fillRect(x + 4, y + 4, w - 8, h - 8);

        g.setColor(new Color(0x4B, 0x54, 0x60));
        g.drawRect(x + 4, y + 4, w - 8, h - 8);

        g.setColor(new Color(0x1A, 0x1D, 0x20));
        g.fillRect(x + w / 2 - 3, y + h / 2 - 3, 6, 6);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.drawRect(x + w / 2 - 3, y + h / 2 - 3, 6, 6);
    }

    private static void drawReinforcementStrap(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x38, 0x40), 3, 2013L);

        g.setColor(new Color(0x1D, 0x21, 0x26));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        g.setColor(new Color(0x4C, 0x55, 0x61));
        g.drawLine(x, y + 1, x + w - 1, y + 1);
        g.drawLine(x, y + h - 2, x + w - 1, y + h - 2);

        for (int px = x + 4; px < x + w - 2; px += 8) {
            drawRivet(g, px, y + h / 2 - 1);
        }
    }

    private static void drawWalkwayGrating(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x25, 0x29, 0x2E), 3, 2014L);

        g.setColor(new Color(0x11, 0x13, 0x15));
        for (int gx = x; gx < x + w; gx += 4) {
            g.drawLine(gx, y, gx, y + h - 1);
        }
        for (int gy = y; gy < y + h; gy += 4) {
            g.drawLine(x, gy, x + w - 1, gy);
        }

        g.setColor(new Color(0x4B, 0x54, 0x60));
        g.drawRect(x, y, w - 1, h - 1);
    }

    // =========================================================================
    // 2. CUSTOM INDUSTRIAL GUI TEXTURE (256x256)
    // =========================================================================
    public static void generateGuiTexture() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Standard 176x166 GUI background plate
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0x3B, 0x41, 0x4A), 3, 3001L);

        // Outer beveled rim
        g.setColor(new Color(0x56, 0x5F, 0x6D));
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 165);
        g.setColor(new Color(0x1F, 0x23, 0x27));
        g.drawLine(175, 0, 175, 165);
        g.drawLine(0, 165, 175, 165);

        // Header plate with corner hex bolts
        drawPanelPlate(g, 6, 6, 164, 14);
        drawBolt(g, 9, 9);
        drawBolt(g, 163, 9);

        // 1. Water Tank Buffer Well (x: 17, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 17, 15, 18, 54, new Color(0x11, 0x18, 0x27));
        g.setColor(new Color(0x1E, 0x3A, 0x8A));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(19, gy, 32, gy);
        }

        // 2. Steam Tank Buffer Well (x: 141, y: 15, w: 18, h: 54)
        drawRecessedWell(g, 141, 15, 18, 54, new Color(0x11, 0x18, 0x27));
        g.setColor(new Color(0x02, 0x84, 0xC7));
        for (int gy = 20; gy < 68; gy += 8) {
            g.drawLine(143, gy, 156, gy);
        }

        // 3. Redstone Indicator Well (x: 37, y: 33, w: 12, h: 12)
        drawRecessedWell(g, 37, 33, 12, 12, new Color(0x18, 0x1B, 0x1E));

        // 4. Combustion Flame Indicator Well (x: 127, y: 33, w: 12, h: 12)
        drawRecessedWell(g, 127, 33, 12, 12, new Color(0x18, 0x1B, 0x1E));

        // 5. Fuel Buffer Plate (x: 50, y: 22, w: 76, h: 42)
        drawPanelPlate(g, 50, 22, 76, 42);

        // 8 Fuel slot wells: 2 rows of 4 at (53 + c*18, 26 + r*18) -> wells at (52 + c*18, 25 + r*18)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 4; c++) {
                drawRecessedSlotWell(g, 52 + c * 18, 25 + r * 18);
            }
        }

        // 6. Overheat / Meltdown Warning Track (x: 37, y: 63, w: 102, h: 8)
        drawRecessedWell(g, 37, 63, 102, 8, new Color(0x14, 0x16, 0x19));
        g.setColor(new Color(0x3B, 0x42, 0x4C));
        for (int px = 42; px < 134; px += 6) {
            g.drawLine(px, 65, px + 2, 69);
        }

        // 7. Player Inventory Wells (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // 8. Player Hotbar Wells (1 row of 9 at y: 142)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/large_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated custom Large Boiler GUI texture at: " + out.getAbsolutePath());
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
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x43), 3, 4001L);
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x1A, 0x1D, 0x20));
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
    }

    private static void drawBolt(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x12, 0x14, 0x16));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x94, 0xA1, 0xB0));
        g.fillRect(x, y, 1, 1);
    }

    private static void drawRivet(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x12, 0x14, 0x16));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x82, 0x8F, 0x9E));
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
