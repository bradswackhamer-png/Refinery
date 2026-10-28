package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates the 256x256 texture atlas for the Industrial Grinder entity model
 * and the 256x256 custom industrial GUI texture.
 *
 * Visual design matches the industrial rock/jaw crusher plant from user reference:
 * - 4-post heavy structural steel stand with rubber vibration-damping feet
 * - Central heavy crushing box with side flywheels, pulley belt drive, and eccentric bearing hubs
 * - Top tapered hopper funnel loaded with rock / ore chunks
 * - Left horizontal feed conveyor bringing material in
 * - Right inclined discharge conveyor with chevron belt carrying crushed product out
 */
public class GrinderAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateGuiTexture();
        System.out.println("Industrial Grinder entity atlas and GUI textures generated successfully!");
    }

    // =========================================================================
    // 1. ENTITY TEXTURE ATLAS (256x256)
    // =========================================================================
    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark industrial steel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x28, 0x2D, 0x33), 4, 3001L);

        // 1. Heavy Crushing Box Side Plates (0, 0, 96, 48)
        drawCrushingBoxPlates(g, 0, 0, 96, 48);

        // 2. 4-Post Support Legs & Vibration Pads (96, 0, 64, 48)
        drawSupportLegs(g, 96, 0, 64, 48);

        // 3. Drive Flywheel & Pulley Belt (160, 0, 48, 48)
        drawFlywheelAndBelt(g, 160, 0, 48, 48);

        // 4. Eccentric Bearing Hubs & Grease Caps (208, 0, 48, 48)
        drawBearingHubs(g, 208, 0, 48, 48);

        // 5. Top Hopper Funnel Plates (0, 48, 64, 48)
        drawHopperFunnel(g, 0, 48, 64, 48);

        // 6. Rock / Ore Chunk Mound (64, 48, 64, 48)
        drawRockChunkMound(g, 64, 48, 64, 48);

        // 7. Feed Conveyor Trough & Rollers (128, 48, 64, 32)
        drawFeedConveyor(g, 128, 48, 64, 32);

        // 8. Discharge Inclined Conveyor & Belt (192, 48, 64, 32)
        drawDischargeConveyor(g, 192, 48, 64, 32);

        // 9. Side Drive Motor & Housing (0, 96, 64, 48)
        drawDriveMotor(g, 0, 96, 64, 48);

        // 10. Corrugated Jaw Liner Plates (64, 96, 64, 48)
        drawJawPlates(g, 64, 96, 64, 48);

        // 11. Crushed Fine Aggregate (128, 96, 64, 48)
        drawCrushedAggregate(g, 128, 96, 64, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/industrial_grinder.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated grinder entity atlas at: " + out.getAbsolutePath());
    }

    private static void drawCrushingBoxPlates(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x38, 0x3E, 0x46), 4, 101L);

        // Heavy bolted border trim
        g.setColor(new Color(0x52, 0x5A, 0x66));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);
        g.setColor(new Color(0x1E, 0x22, 0x27));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Perimeter structural bolts
        for (int bx = x + 4; bx < x + w - 4; bx += 8) {
            drawBolt(g, bx, y + 3);
            drawBolt(g, bx, y + h - 5);
        }
        for (int by = y + 8; by < y + h - 8; by += 8) {
            drawBolt(g, x + 3, by);
            drawBolt(g, x + w - 5, by);
        }

        // Center machined plate recess
        g.setColor(new Color(0x2C, 0x31, 0x38));
        g.fillRect(x + 16, y + 10, w - 32, h - 20);
        g.setColor(new Color(0x48, 0x50, 0x5C));
        g.drawRect(x + 16, y + 10, w - 32, h - 20);
    }

    private static void drawSupportLegs(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x37, 0x3F), 4, 201L);

        // Structural post beveled edges
        g.setColor(new Color(0x4A, 0x52, 0x5E));
        g.drawLine(x, y, x, y + h - 1);
        g.drawLine(x + w / 2, y, x + w / 2, y + h - 1);
        g.setColor(new Color(0x1C, 0x1F, 0x24));
        g.drawLine(x + w / 2 - 1, y, x + w / 2 - 1, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

        // Rubber vibration-damping foot pads at the bottom (dark grooved rubber)
        g.setColor(new Color(0x16, 0x18, 0x1A));
        g.fillRect(x, y + h - 12, w, 12);
        g.setColor(new Color(0x2C, 0x30, 0x35));
        for (int gy = y + h - 10; gy < y + h; gy += 3) {
            g.drawLine(x, gy, x + w - 1, gy);
        }
    }

    private static void drawFlywheelAndBelt(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x24, 0x28, 0x2D), 3, 301L);

        int cx = x + w / 2;
        int cy = y + h / 2;
        int r = Math.min(w, h) / 2 - 3;

        // Cast iron outer flywheel rim
        g.setColor(new Color(0x44, 0x4B, 0x55));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x5A, 0x63, 0x70));
        g.drawOval(cx - r + 1, cy - r + 1, (r - 1) * 2, (r - 1) * 2);
        g.setColor(new Color(0x1B, 0x1E, 0x22));
        g.drawOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

        // 6-spoke cast iron wheel
        g.setColor(new Color(0x3B, 0x41, 0x4A));
        for (int i = 0; i < 6; i++) {
            double angle = i * (Math.PI / 3.0);
            int sx = (int) (cx + Math.cos(angle) * (r - 3));
            int sy = (int) (cy + Math.sin(angle) * (r - 3));
            g.drawLine(cx, cy, sx, sy);
        }

        // Center hub
        g.setColor(new Color(0x60, 0x6A, 0x77));
        g.fillOval(cx - 5, cy - 5, 10, 10);
        g.setColor(new Color(0x20, 0x24, 0x29));
        g.fillOval(cx - 2, cy - 2, 4, 4);
    }

    private static void drawBearingHubs(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2D, 0x32, 0x38), 3, 401L);

        // Heavy eccentric cylindrical housing
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.setColor(new Color(0x4F, 0x57, 0x63));
        g.fillOval(cx - 16, cy - 16, 32, 32);
        g.setColor(new Color(0x20, 0x24, 0x28));
        g.drawOval(cx - 16, cy - 16, 32, 32);

        // Grease fitting cap (brass hexagonal nipple)
        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.fillOval(cx - 4, cy - 4, 8, 8);
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        g.fillRect(cx - 2, cy - 2, 4, 4);
    }

    private static void drawHopperFunnel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x3C, 0x42, 0x4B), 4, 501L);

        // Flanged upper rim
        g.setColor(new Color(0x56, 0x5E, 0x6B));
        g.fillRect(x, y, w, 4);
        g.setColor(new Color(0x22, 0x26, 0x2C));
        g.fillRect(x, y + 4, w, 1);

        for (int bx = x + 4; bx < x + w; bx += 8) {
            drawBolt(g, bx, y + 1);
        }

        // Tapered funnel diagonal shading lines
        g.setColor(new Color(0x2E, 0x33, 0x3A));
        g.drawLine(x + 4, y + 6, x + 16, y + h - 2);
        g.drawLine(x + w - 5, y + 6, x + w - 17, y + h - 2);
    }

    private static void drawRockChunkMound(Graphics2D g, int x, int y, int w, int h) {
        // Jagged irregular stone boulders and mineral chunks
        fillNoisyRect(g, x, y, w, h, new Color(0x57, 0x5C, 0x63), 6, 601L);

        Random r = new Random(42L);
        for (int i = 0; i < 24; i++) {
            int rx = x + r.nextInt(w - 10);
            int ry = y + r.nextInt(h - 10);
            int rw = 6 + r.nextInt(8);
            int rh = 5 + r.nextInt(7);

            // Shaded rock facet
            Color rockTone = (i % 3 == 0) ? new Color(0x43, 0x48, 0x4F)
                    : (i % 3 == 1) ? new Color(0x68, 0x6E, 0x77)
                    : new Color(0x7C, 0x82, 0x8C);
            g.setColor(rockTone);
            g.fillRoundRect(rx, ry, rw, rh, 3, 3);
            g.setColor(new Color(0x2A, 0x2E, 0x33));
            g.drawRoundRect(rx, ry, rw, rh, 3, 3);
        }
    }

    private static void drawFeedConveyor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x30, 0x35, 0x3D), 3, 701L);

        // Side steel guard channels
        g.setColor(new Color(0x4A, 0x52, 0x5D));
        g.fillRect(x, y, w, 4);
        g.fillRect(x, y + h - 4, w, 4);

        // Black rubber conveyor belt with ribbed texture
        g.setColor(new Color(0x19, 0x1B, 0x1E));
        g.fillRect(x, y + 4, w, h - 8);
        g.setColor(new Color(0x28, 0x2C, 0x31));
        for (int bx = x + 3; bx < x + w; bx += 5) {
            g.drawLine(bx, y + 4, bx, y + h - 5);
        }
    }

    private static void drawDischargeConveyor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2C, 0x31, 0x38), 3, 801L);

        // Heavy steel angle-iron truss frame
        g.setColor(new Color(0x48, 0x50, 0x5B));
        g.fillRect(x, y, w, 3);
        g.fillRect(x, y + h - 3, w, 3);

        // Chevron-patterned rubber belt (grooved V-treads for climbing incline)
        g.setColor(new Color(0x18, 0x1A, 0x1D));
        g.fillRect(x, y + 3, w, h - 6);
        g.setColor(new Color(0x2A, 0x2E, 0x34));
        for (int bx = x + 4; bx < x + w; bx += 8) {
            int midY = y + h / 2;
            g.drawLine(bx, y + 4, bx + 3, midY);
            g.drawLine(bx + 3, midY, bx, y + h - 4);
        }
    }

    private static void drawDriveMotor(Graphics2D g, int x, int y, int w, int h) {
        // Heavy industrial electric motor with cooling fins
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x43), 3, 901L);

        // Cooling fins (parallel ridges)
        for (int fy = y + 4; fy < y + h - 4; fy += 3) {
            g.setColor(new Color(0x4D, 0x55, 0x62));
            g.drawLine(x + 4, fy, x + w - 5, fy);
            g.setColor(new Color(0x1C, 0x20, 0x25));
            g.drawLine(x + 4, fy + 1, x + w - 5, fy + 1);
        }

        // Terminal connection box on side
        g.setColor(new Color(0x56, 0x5E, 0x6B));
        g.fillRect(x + w - 14, y + 8, 10, 14);
        g.setColor(new Color(0x1A, 0x1D, 0x21));
        g.drawRect(x + w - 14, y + 8, 10, 14);
    }

    private static void drawJawPlates(Graphics2D g, int x, int y, int w, int h) {
        // Corrugated manganese steel wear-resistant tooth plates
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2C, 0x32), 4, 1001L);

        for (int px = x + 3; px < x + w; px += 6) {
            g.setColor(new Color(0x4C, 0x54, 0x60));
            g.fillRect(px, y, 3, h);
            g.setColor(new Color(0x14, 0x16, 0x19));
            g.fillRect(px + 3, y, 3, h);
        }
    }

    private static void drawCrushedAggregate(Graphics2D g, int x, int y, int w, int h) {
        // Fine crushed stone gravel and mineral aggregate
        fillNoisyRect(g, x, y, w, h, new Color(0x50, 0x54, 0x5C), 5, 1101L);

        Random r = new Random(77L);
        for (int i = 0; i < 40; i++) {
            int px = x + r.nextInt(w - 3);
            int py = y + r.nextInt(h - 3);
            int tone = 70 + r.nextInt(60);
            g.setColor(new Color(tone, tone + 4, tone + 8));
            g.fillRect(px, py, 2, 2);
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

        // Base Industrial Steel Panel
        fillNoisyRect(g, 0, 0, w, h, new Color(0x2B, 0x30, 0x36), 3, 2001L);

        // Beveled border
        g.setColor(new Color(0x56, 0x5F, 0x6C));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x15, 0x18, 0x1B));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);

        // Corner Rivets
        drawBolt(g, 3, 3);
        drawBolt(g, w - 5, 3);
        drawBolt(g, 3, h - 5);
        drawBolt(g, w - 5, h - 5);

        // 1. Energy Storage Well (x: 13, y: 17, w: 16, h: 52) -> inner fill at (14, 18, 14, 50)
        drawRecessedWell(g, 13, 17, 16, 52, new Color(0x19, 0x1E, 0x24));

        // 2. Input Slots Housing Plate (x: 34, y: 22, w: 42, h: 42)
        drawPanelPlate(g, 34, 22, 42, 42);
        // 4 Input slot wells: (38, 26), (56, 26), (38, 44), (56, 44) -> wells at (37, 25), (55, 25), (37, 43), (55, 43)
        drawRecessedSlotWell(g, 37, 25);
        drawRecessedSlotWell(g, 55, 25);
        drawRecessedSlotWell(g, 37, 43);
        drawRecessedSlotWell(g, 55, 43);

        // 3. Central Crushing Progress Bar Well (x: 77, y: 33, w: 34, h: 18) -> inner fill at (78, 34, 32, 16)
        drawRecessedWell(g, 77, 33, 34, 18, new Color(0x16, 0x19, 0x1E));
        // Chevron teeth guide markings inside progress well
        g.setColor(new Color(0x35, 0x3C, 0x46));
        for (int px = 82; px < 108; px += 6) {
            g.drawLine(px, 35, px + 3, 42);
            g.drawLine(px + 3, 42, px, 49);
        }

        // 4. Output Slots Housing Plate (x: 112, y: 22, w: 42, h: 42)
        drawPanelPlate(g, 112, 22, 42, 42);
        // 4 Output slot wells: (116, 26), (134, 26), (116, 44), (134, 44) -> wells at (115, 25), (133, 25), (115, 43), (133, 43)
        drawRecessedSlotWell(g, 115, 25);
        drawRecessedSlotWell(g, 133, 25);
        drawRecessedSlotWell(g, 115, 43);
        drawRecessedSlotWell(g, 133, 43);

        // 5. Player Inventory Wells (3 rows of 9 at y: 84) -> slot at (8 + col*18, 84 + row*18) -> well at (7 + col*18, 83 + row*18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // 6. Player Hotbar Wells (1 row of 9 at y: 142) -> slot at (8 + col*18, 142) -> well at (7 + col*18, 141)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/industrial_grinder.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated custom grinder GUI texture at: " + out.getAbsolutePath());
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
