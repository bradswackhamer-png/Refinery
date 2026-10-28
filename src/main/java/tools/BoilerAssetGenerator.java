package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates the 256x256 texture atlas for the Small Steam Boiler entity model
 * and the 256x256 custom industrial GUI texture.
 *
 * Visual design matches the industrial packaged fire-tube Scotch marine boiler:
 * - Solid steel horizontal cladding plates with riveted seams (no cutaways on exterior)
 * - Circular front boiler door with heavy black hinges and circular burner hub
 * - Glowing burner sight-glass viewport (active fire glow vs idle dark glass)
 * - Lower arched ash/cleanout maintenance door
 * - Heavy structural steel I-beam skid base and saddle cradles
 * - Overhead steam manifold piping, red globe valve handwheels, and dial pressure gauge
 * - Tall vertical exhaust chimney smokestack with top flange
 * - Side electrical control cabinet and feed-water sight column
 */
public class BoilerAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateGuiTexture();
        System.out.println("Small Steam Boiler entity atlas and GUI textures generated successfully!");
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

        // 4. Smokestack Flue & Flange Pipe (208, 0, 48, 48)
        drawSmokestackSteel(g, 208, 0, 48, 48);

        // 5. Front Circular Boiler Door & Hinges (0, 48, 64, 64)
        drawFrontBoilerDoor(g, 0, 48, 64, 64);

        // 6. Burner Sight Glass - Active (Glowing Fire) (64, 48, 32, 32)
        drawBurnerSightGlass(g, 64, 48, 32, 32, true);

        // 7. Burner Sight Glass - Idle (Dark Soot Glass) (96, 48, 32, 32)
        drawBurnerSightGlass(g, 96, 48, 32, 32, false);

        // 8. Overhead Steam Manifold Piping & Flanges (128, 48, 64, 32)
        drawSteamPiping(g, 128, 48, 64, 32);

        // 9. Red Valve Handwheels & Stems (192, 48, 32, 32)
        drawValveHandwheel(g, 192, 48, 32, 32);

        // 10. Circular Analog Dial Pressure Gauge (224, 48, 32, 32)
        drawPressureGauge(g, 224, 48, 32, 32);

        // 11. Side Electrical Control Cabinet (0, 112, 64, 48)
        drawControlCabinet(g, 0, 112, 64, 48);

        // 12. Water Sight Glass Column & Feed Manifold (64, 112, 48, 48)
        drawWaterColumn(g, 64, 112, 48, 48);

        // 13. Lower Cleanout Door & Latch (112, 112, 48, 48)
        drawCleanoutDoor(g, 112, 112, 48, 48);

        // 14. Extra Riveted Steel Straps & Flange Rings (160, 112, 96, 48)
        drawReinforcingStraps(g, 160, 112, 96, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/small_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated entity texture atlas at: " + out.getAbsolutePath());
    }

    private static void drawBoilerShellCladding(Graphics2D g, int x, int y, int w, int h) {
        // Base curved dark gunmetal sheet steel
        fillNoisyRect(g, x, y, w, h, new Color(0x3B, 0x41, 0x4A), 4, 101L);

        // Horizontal lap-joint cladding plate seams
        for (int py = y + 12; py < y + h; py += 12) {
            g.setColor(new Color(0x22, 0x26, 0x2C));
            g.drawLine(x, py, x + w - 1, py);
            g.setColor(new Color(0x56, 0x5E, 0x6B));
            g.drawLine(x, py + 1, x + w - 1, py + 1);

            // Rivet lines along seam
            for (int rx = x + 3; rx < x + w; rx += 8) {
                g.setColor(new Color(0x1A, 0x1D, 0x22));
                g.fillRect(rx, py - 1, 2, 2);
                g.setColor(new Color(0x8D, 0x97, 0xA5));
                g.fillRect(rx, py - 1, 1, 1);
            }
        }

        // Vertical panel divider bands
        int[] bandXs = {x + 24, x + 48, x + 72};
        for (int bx : bandXs) {
            g.setColor(new Color(0x2A, 0x2F, 0x37));
            g.fillRect(bx - 2, y, 4, h);
            g.setColor(new Color(0x5A, 0x62, 0x70));
            g.drawLine(bx - 2, y, bx - 2, y + h - 1);
            g.drawLine(bx + 2, y, bx + 2, y + h - 1);
            // Band rivets
            for (int ry = y + 4; ry < y + h; ry += 8) {
                g.setColor(new Color(0x1A, 0x1E, 0x23));
                g.fillRect(bx - 1, ry, 2, 2);
                g.setColor(new Color(0x99, 0xA4, 0xB2));
                g.fillRect(bx - 1, ry, 1, 1);
            }
        }
    }

    private static void drawDrumEndHead(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x38, 0x40), 4, 201L);

        // Circular dished head shading
        g.setColor(new Color(0x44, 0x4C, 0x57));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);
        g.setColor(new Color(0x22, 0x26, 0x2C));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Perimeter flange bolt circle
        for (int bx = x + 4; bx < x + w - 4; bx += 8) {
            drawRivet(g, bx, y + 3);
            drawRivet(g, bx, y + h - 5);
        }
        for (int by = y + 8; by < y + h - 8; by += 8) {
            drawRivet(g, x + 3, by);
            drawRivet(g, x + w - 5, by);
        }
    }

    private static void drawStructuralSkidSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x20, 0x23, 0x28), 3, 301L);

        // I-beam flange machined edges
        g.setColor(new Color(0x40, 0x47, 0x52));
        g.fillRect(x, y, w, 3);
        g.fillRect(x, y + h - 3, w, 3);
        g.setColor(new Color(0x14, 0x16, 0x19));
        g.fillRect(x, y + 3, w, 1);
        g.fillRect(x, y + h - 4, w, 1);

        // Web reinforcement gussets
        for (int gx = x + 8; gx < x + w; gx += 16) {
            g.setColor(new Color(0x18, 0x1A, 0x1E));
            g.fillRect(gx, y + 4, 2, h - 8);
            drawRivet(g, gx - 1, y + 5);
            drawRivet(g, gx - 1, y + h - 7);
        }
    }

    private static void drawSmokestackSteel(Graphics2D g, int x, int y, int w, int h) {
        // Dark soot-toned steel
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2C, 0x31), 4, 401L);

        // Smokestack collar rings
        for (int cy = y + 8; cy < y + h; cy += 14) {
            g.setColor(new Color(0x4A, 0x51, 0x5B));
            g.fillRect(x, cy, w, 3);
            g.setColor(new Color(0x18, 0x1B, 0x1E));
            g.fillRect(x, cy + 3, w, 1);
            // Flange rivets
            for (int rx = x + 3; rx < x + w; rx += 7) {
                drawRivet(g, rx, cy);
            }
        }
    }

    private static void drawFrontBoilerDoor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2E, 0x34, 0x3B), 4, 501L);

        // Circular front door outer ring
        g.setColor(new Color(0x47, 0x4F, 0x5A));
        g.drawOval(x + 2, y + 2, w - 5, h - 5);
        g.drawOval(x + 3, y + 3, w - 7, h - 7);
        g.setColor(new Color(0x1C, 0x20, 0x25));
        g.drawOval(x + 1, y + 1, w - 3, h - 3);

        // Heavy door perimeter bolts
        for (int i = 0; i < 16; i++) {
            double angle = i * (Math.PI * 2.0 / 16.0);
            int bx = (int) (x + w / 2 + Math.cos(angle) * (w / 2 - 5));
            int by = (int) (y + h / 2 + Math.sin(angle) * (h / 2 - 5));
            drawRivet(g, bx - 1, by - 1);
        }

        // Left hinge brackets (black heavy cast steel)
        g.setColor(new Color(0x15, 0x18, 0x1C));
        g.fillRect(x + 2, y + 12, 10, 8);
        g.fillRect(x + 2, y + h - 20, 10, 8);
        g.setColor(new Color(0x48, 0x50, 0x5B));
        g.drawRect(x + 2, y + 12, 10, 8);
        g.drawRect(x + 2, y + h - 20, 10, 8);

        // Central circular burner mounting collar
        int cw = 24, ch = 24;
        int cx = x + (w - cw) / 2;
        int cy = y + (h - ch) / 2 - 4;
        g.setColor(new Color(0x22, 0x27, 0x2D));
        g.fillOval(cx, cy, cw, ch);
        g.setColor(new Color(0x56, 0x5E, 0x6C));
        g.drawOval(cx, cy, cw, ch);
        // Burner hub bolt circle
        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI * 2.0 / 8.0);
            int rx = (int) (cx + cw / 2 + Math.cos(angle) * (cw / 2 - 2));
            int ry = (int) (cy + ch / 2 + Math.sin(angle) * (ch / 2 - 2));
            drawRivet(g, rx, ry);
        }
    }

    private static void drawBurnerSightGlass(Graphics2D g, int x, int y, int w, int h, boolean active) {
        // Outer dark cast iron frame
        fillNoisyRect(g, x, y, w, h, new Color(0x1E, 0x22, 0x27), 3, 601L);
        g.setColor(new Color(0x3E, 0x46, 0x50));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        int cx = x + 4, cy = y + 4, sz = w - 8;
        if (active) {
            // Intense glowing flame aperture (yellow -> amber -> orange -> red glow)
            g.setColor(new Color(0x7F, 0x1D, 0x1D));
            g.fillOval(cx, cy, sz, sz);
            g.setColor(new Color(0xEA, 0x58, 0x0C));
            g.fillOval(cx + 2, cy + 2, sz - 4, sz - 4);
            g.setColor(new Color(0xFB, 0xBF, 0x24));
            g.fillOval(cx + 4, cy + 4, sz - 8, sz - 8);
            g.setColor(new Color(0xFF, 0xFD, 0xD0));
            g.fillOval(cx + 6, cy + 6, sz - 12, sz - 12);
        } else {
            // Dark cold furnace chamber & soot glass
            g.setColor(new Color(0x12, 0x14, 0x17));
            g.fillOval(cx, cy, sz, sz);
            g.setColor(new Color(0x28, 0x2D, 0x33));
            g.drawOval(cx, cy, sz, sz);
            // Glare streak
            g.setColor(new Color(0x45, 0x4E, 0x58));
            g.drawLine(cx + 3, cy + 4, cx + sz - 5, cy + sz - 4);
        }
    }

    private static void drawSteamPiping(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x4A, 0x52, 0x5E), 4, 701L);

        // Cylindrical pipe gradient shading (top highlight, bottom shadow)
        g.setColor(new Color(0x70, 0x7B, 0x8C));
        g.fillRect(x, y + 2, w, 4);
        g.setColor(new Color(0x31, 0x37, 0x40));
        g.fillRect(x, y + h - 5, w, 5);

        // Pipe connection flanges with bolts
        for (int fx = x + 8; fx < x + w; fx += 20) {
            g.setColor(new Color(0x26, 0x2A, 0x30));
            g.fillRect(fx - 2, y, 4, h);
            g.setColor(new Color(0x60, 0x6A, 0x78));
            g.drawRect(fx - 2, y, 4, h);
            drawRivet(g, fx - 1, y + 2);
            drawRivet(g, fx - 1, y + h - 4);
        }
    }

    private static void drawValveHandwheel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x25, 0x29, 0x2E), 3, 801L);

        // Industrial valve crimson enamel handwheel
        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 3;

        g.setColor(new Color(0x99, 0x1B, 0x1B));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawOval(cx - r + 1, cy - r + 1, (r - 1) * 2, (r - 1) * 2);

        // Handwheel spokes (4-spoke cross)
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(cx - r + 2, cy, cx + r - 2, cy);
        g.drawLine(cx, cy - r + 2, cx, cy + r - 2);

        // Central brass/steel hub
        g.setColor(new Color(0xE2, 0xE8, 0xF0));
        g.fillRect(cx - 2, cy - 2, 4, 4);
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.drawRect(cx - 2, cy - 2, 4, 4);
    }

    private static void drawPressureGauge(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x22, 0x26, 0x2B), 3, 901L);

        // Polished brass/chrome bezel
        int cx = x + 3, cy = y + 3, sz = w - 6;
        g.setColor(new Color(0xB4, 0x53, 0x09)); // Brass bezel
        g.drawOval(cx, cy, sz, sz);
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        g.drawOval(cx + 1, cy + 1, sz - 2, sz - 2);

        // White dial face
        g.setColor(new Color(0xF1, 0xF5, 0xF9));
        g.fillOval(cx + 2, cy + 2, sz - 4, sz - 4);

        // Graduation tick marks
        int centerDialX = cx + sz / 2;
        int centerDialY = cy + sz / 2;
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.drawLine(centerDialX - 6, centerDialY - 6, centerDialX - 4, centerDialY - 4);
        g.drawLine(centerDialX + 6, centerDialY - 6, centerDialX + 4, centerDialY - 4);
        g.drawLine(centerDialX, centerDialY - 7, centerDialX, centerDialY - 5);

        // Red pressure indicator needle
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(centerDialX, centerDialY, centerDialX + 3, centerDialY - 5);
        g.fillRect(centerDialX - 1, centerDialY - 1, 3, 3);
    }

    private static void drawControlCabinet(Graphics2D g, int x, int y, int w, int h) {
        // Off-white / light grey industrial enamel (matching Image 2)
        fillNoisyRect(g, x, y, w, h, new Color(0xD8, 0xDC, 0xE2), 3, 1001L);

        // Cabinet beveled door trim
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Door lock key latch
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.fillRect(x + w - 8, y + h / 2 - 3, 4, 6);
        g.setColor(new Color(0xE2, 0xE8, 0xF0));
        g.fillRect(x + w - 7, y + h / 2 - 1, 2, 2);

        // Caution / High Voltage Triangle
        g.setColor(new Color(0xEA, 0x58, 0x0C));
        int tx = x + 10, ty = y + 8;
        int[] triX = {tx + 6, tx, tx + 12};
        int[] triY = {ty, ty + 10, ty + 10};
        g.fillPolygon(triX, triY, 3);
        g.setColor(new Color(0x00, 0x00, 0x00));
        g.drawLine(tx + 6, ty + 3, tx + 6, ty + 7);
        g.fillRect(tx + 6, ty + 8, 1, 1);

        // Status indicator lamps (Green, Amber, Red)
        int ly = y + 24;
        g.setColor(new Color(0x22, 0xC5, 0x5E)); // Green
        g.fillRect(x + 10, ly, 4, 4);
        g.setColor(new Color(0xF5, 0x9E, 0x0B)); // Amber
        g.fillRect(x + 18, ly, 4, 4);
        g.setColor(new Color(0xEF, 0x44, 0x44)); // Red
        g.fillRect(x + 26, ly, 4, 4);

        // Ventilation louvers
        for (int l = 0; l < 4; l++) {
            g.setColor(new Color(0x64, 0x74, 0x8B));
            g.drawLine(x + 10, y + 34 + l * 2, x + 36, y + 34 + l * 2);
        }
    }

    private static void drawWaterColumn(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2C, 0x31, 0x38), 3, 1101L);

        // Brass upper & lower manifold blocks
        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.fillRect(x + 4, y + 2, w - 8, 8);
        g.fillRect(x + 4, y + h - 10, w - 8, 8);
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.drawRect(x + 4, y + 2, w - 8, 8);
        g.drawRect(x + 4, y + h - 10, w - 8, 8);

        // Vertical clear glass tube with water column
        int gx = x + w / 2 - 3;
        g.setColor(new Color(0x1E, 0x29, 0x3B));
        g.fillRect(gx, y + 10, 6, h - 20);
        // Water fill
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.fillRect(gx + 1, y + 20, 4, h - 30);
        // Glass highlight reflection
        g.setColor(new Color(0xBA, 0xE6, 0xFD));
        g.drawLine(gx + 1, y + 11, gx + 1, y + h - 12);
    }

    private static void drawCleanoutDoor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x26, 0x2B, 0x31), 4, 1201L);

        // Arched cleanout door outer frame
        g.setColor(new Color(0x18, 0x1B, 0x1F));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);
        g.setColor(new Color(0x48, 0x51, 0x5C));
        g.drawRect(x + 3, y + 3, w - 7, h - 7);

        // Heavy latch handle
        g.setColor(new Color(0x64, 0x74, 0x8B));
        g.fillRect(x + w - 10, y + h / 2 - 2, 6, 4);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.drawRect(x + w - 10, y + h / 2 - 2, 6, 4);
    }

    private static void drawReinforcingStraps(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x43), 4, 1301L);

        for (int py = y + 8; py < y + h; py += 12) {
            g.setColor(new Color(0x1F, 0x23, 0x28));
            g.fillRect(x, py, w, 4);
            g.setColor(new Color(0x5A, 0x64, 0x73));
            g.drawLine(x, py, x + w - 1, py);
            g.drawLine(x, py + 3, x + w - 1, py + 3);

            for (int rx = x + 4; rx < x + w; rx += 8) {
                drawRivet(g, rx, py + 1);
            }
        }
    }

    // =========================================================================
    // 2. CUSTOM INDUSTRIAL GUI TEXTURE (256x256)
    // =========================================================================
    public static void generateGuiTexture() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176;
        int h = 166;

        // Base Panel (Industrial Dark Steel)
        fillNoisyRect(g, 0, 0, w, h, new Color(0x2B, 0x30, 0x36), 3, 2001L);

        // Highlight & Shadow Border
        g.setColor(new Color(0x56, 0x5F, 0x6C));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x15, 0x18, 0x1B));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);

        // Corner Rivets on the GUI frame
        drawRivet(g, 3, 3);
        drawRivet(g, w - 5, 3);
        drawRivet(g, 3, h - 5);
        drawRivet(g, w - 5, h - 5);

        // 1. Water Buffer Tank Well (x: 21, y: 15, w: 18, h: 54) -> inner (x: 22, y: 16, w: 16, h: 52)
        drawRecessedWell(g, 21, 15, 18, 54, new Color(0x17, 0x20, 0x33));
        // Graduation marks on water well
        g.setColor(new Color(0x3B, 0x82, 0xF6));
        for (int y = 15 + 13; y < 15 + 52; y += 13) {
            g.drawLine(22, y, 25, y);
            g.drawLine(34, y, 37, y);
        }

        // 2. Steam Buffer Tank Well (x: 137, y: 15, w: 18, h: 54) -> inner (x: 138, y: 16, w: 16, h: 52)
        drawRecessedWell(g, 137, 15, 18, 54, new Color(0x13, 0x25, 0x2E));
        // Graduation marks on steam well
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        for (int y = 15 + 13; y < 15 + 52; y += 13) {
            g.drawLine(138, y, 141, y);
            g.drawLine(150, y, 153, y);
        }

        // 3. Central Combustion Hearth Frame (x: 44, y: 15, w: 88, h: 54)
        drawPanelPlate(g, 44, 15, 88, 48);

        // 4. Redstone Indicator Lamp (x: 49, y: 33, w: 12, h: 12) -> inner 10x10 at 50, 34
        drawRecessedWell(g, 49, 33, 12, 12, new Color(0x18, 0x1A, 0x1D));

        // 5. Firebox Combustion Viewport (x: 114, y: 33, w: 12, h: 12) -> inner 10x10 at 115, 34
        drawRecessedWell(g, 114, 33, 12, 12, new Color(0x18, 0x1A, 0x1D));

        // 6. Fuel Buffer Slots (2x2 Grid)
        // Slot 0: (71, 26) -> well at (70, 25, 18, 18)
        // Slot 1: (89, 26) -> well at (88, 25, 18, 18)
        // Slot 2: (71, 44) -> well at (70, 43, 18, 18)
        // Slot 3: (89, 44) -> well at (88, 43, 18, 18)
        drawRecessedSlotWell(g, 70, 25);
        drawRecessedSlotWell(g, 88, 25);
        drawRecessedSlotWell(g, 70, 43);
        drawRecessedSlotWell(g, 88, 43);

        // 7. Overheat / Meltdown Warning Bar Well (x: 47, y: 64, w: 82, h: 8) -> inner (x: 48, y: 65, w: 80, h: 6)
        drawRecessedWell(g, 47, 64, 82, 8, new Color(0x1A, 0x1C, 0x20));

        // 8. Player Inventory Wells (3 rows of 9 at y: 84) -> slot at (8 + col*18, 84 + row*18) -> well at (7 + col*18, 83 + row*18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // 9. Player Hotbar Wells (1 row of 9 at y: 142) -> slot at (8 + col*18, 142) -> well at (7 + col*18, 141)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/small_boiler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated custom GUI texture at: " + out.getAbsolutePath());
    }

    private static void drawRecessedWell(Graphics2D g, int x, int y, int w, int h, Color innerFill) {
        // Deep shadow on top/left
        g.setColor(new Color(0x14, 0x17, 0x1A));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        // Beveled light on bottom/right
        g.setColor(new Color(0x4D, 0x56, 0x63));
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        // Fill well
        g.setColor(innerFill);
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    private static void drawRecessedSlotWell(Graphics2D g, int x, int y) {
        drawRecessedWell(g, x, y, 18, 18, new Color(0x1D, 0x21, 0x26));
    }

    private static void drawPanelPlate(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x43), 3, 3001L);
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x1A, 0x1D, 0x20));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Corner rivets on panel plate
        drawRivet(g, x + 2, y + 2);
        drawRivet(g, x + w - 4, y + 2);
        drawRivet(g, x + 2, y + h - 4);
        drawRivet(g, x + w - 4, y + h - 4);
    }

    private static void drawRivet(Graphics2D g, int x, int y) {
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
