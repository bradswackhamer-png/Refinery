package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates assets for the Industrial Liquid Storage Tank:
 * - 256x256 Entity texture atlas (textures/entity/storage_tank.png) matching reference GFS bolted enamel tanks
 * - 256x256 Custom GUI texture (textures/gui/storage_tank.png)
 * - 16x16 Block textures for casing, drain valve, and fill valve
 */
public class StorageTankAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateGuiTexture();
        generateBlockTextures();
        System.out.println("Storage Tank assets generated successfully!");
    }

    // =========================================================================
    // 1. ENTITY TEXTURE ATLAS (256x256)
    // =========================================================================
    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark industrial enamel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x11, 0x16, 0x1E), 3, 1001L);

        // 1. Enamel Bolted Wall Panel (0, 0, 96, 48)
        drawEnamelWallPanel(g, 0, 0, 96, 48);

        // 2. Galvanized Wind Ring & Stiffeners (96, 0, 48, 48)
        drawWindRingSteel(g, 96, 0, 48, 48);

        // 3. Clean Galvanized Industrial Ladder Steel (144, 0, 48, 48)
        drawGalvanizedLadderSteel(g, 144, 0, 48, 48);

        // 4. Concrete Foundation Ring Wall (192, 0, 64, 48)
        drawConcreteFoundation(g, 192, 0, 64, 48);

        // 5. Roof Cone Enamel Panels (0, 48, 64, 48)
        drawRoofCone(g, 0, 48, 64, 48);

        // 6. Bottom Flanged Drain Nozzle (64, 48, 48, 48)
        drawDrainNozzle(g, 64, 48, 48, 48);

        // 7. Top Flanged Fill Nozzle (112, 48, 48, 48)
        drawFillNozzle(g, 112, 48, 48, 48);

        // 8. Circular Manway Inspection Door (160, 48, 48, 48)
        drawManwayDoor(g, 160, 48, 48, 48);

        // 9. Clean Galvanized Guardrail Steel (208, 48, 48, 48)
        drawGalvanizedHandrailSteel(g, 208, 48, 48, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/storage_tank.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated entity texture atlas at: " + out.getAbsolutePath());
    }

    private static void drawEnamelWallPanel(Graphics2D g, int x, int y, int w, int h) {
        // Deep glossy black/charcoal enamel base (matching reference image)
        fillNoisyRect(g, x, y, w, h, new Color(0x13, 0x18, 0x22), 3, 201L);

        // Specular enamel vertical reflection sheen
        g.setColor(new Color(0x23, 0x2C, 0x3D));
        g.fillRect(x + 12, y, 16, h);
        g.setColor(new Color(0x32, 0x3E, 0x54));
        g.drawLine(x + 18, y, x + 18, y + h - 1);

        // Horizontal bolted lap seams (rings of bolted panels)
        for (int py = y + 16; py < y + h; py += 16) {
            g.setColor(new Color(0x0A, 0x0E, 0x14));
            g.drawLine(x, py - 1, x + w - 1, py - 1);
            g.setColor(new Color(0x1E, 0x26, 0x36));
            g.drawLine(x, py, x + w - 1, py);
            g.setColor(new Color(0x3B, 0x48, 0x60));
            g.drawLine(x, py + 1, x + w - 1, py + 1);

            // Horizontal seam rivets / bolts
            for (int bx = x + 4; bx < x + w; bx += 8) {
                drawBolt(g, bx, py);
            }
        }

        // Vertical panel division seams
        for (int vx = x + 32; vx < x + w; vx += 32) {
            g.setColor(new Color(0x09, 0x0D, 0x12));
            g.drawLine(vx - 1, y, vx - 1, y + h - 1);
            g.setColor(new Color(0x28, 0x33, 0x47));
            g.drawLine(vx, y, vx, y + h - 1);
            for (int by = y + 4; by < y + h; by += 8) {
                drawBolt(g, vx, by);
            }
        }
    }

    private static void drawWindRingSteel(Graphics2D g, int x, int y, int w, int h) {
        // Galvanized steel stiffener ring (light grey band around tank in photo)
        fillNoisyRect(g, x, y, w, h, new Color(0x64, 0x74, 0x8B), 3, 301L);

        // Machined highlights & shadows
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillRect(x, y + 2, w, 4);
        g.setColor(new Color(0xF1, 0xF5, 0xF9));
        g.drawLine(x, y + 3, x + w - 1, y + 3);
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.fillRect(x, y + h - 6, w, 6);

        // Fastener brackets
        for (int bx = x + 6; bx < x + w; bx += 12) {
            g.setColor(new Color(0x1E, 0x29, 0x3B));
            g.fillRect(bx - 1, y + 6, 3, h - 12);
            drawBolt(g, bx, y + 8);
            drawBolt(g, bx, y + h - 8);
        }
    }

    private static void drawGalvanizedLadderSteel(Graphics2D g, int x, int y, int w, int h) {
        // High-fidelity galvanized industrial steel (clean bright zinc finish)
        fillNoisyRect(g, x, y, w, h, new Color(0xD8, 0xDF, 0xE8), 3, 401L);

        // Subtle zinc crystal spangle variations
        Random rand = new Random(402L);
        for (int i = 0; i < 48; i++) {
            int sx = x + rand.nextInt(w - 4);
            int sy = y + rand.nextInt(h - 4);
            int sw = 2 + rand.nextInt(4);
            int sh = 2 + rand.nextInt(4);
            g.setColor(rand.nextBoolean() ? new Color(0xF1, 0xF5, 0xF9) : new Color(0xC2, 0xCD, 0xDC));
            g.fillRect(sx, sy, sw, sh);
        }

        // Clean machined highlights on top and left edges
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + 1, x + w - 1, y + 1);
        g.drawLine(x, y, x, y + h - 1);

        // Subtle ambient shadow along bottom and right edges
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

        // Structural fastener rivets
        for (int bx = x + 8; bx < x + w; bx += 16) {
            drawBolt(g, bx, y + 8);
            drawBolt(g, bx, y + h - 8);
        }
    }

    private static void drawConcreteFoundation(Graphics2D g, int x, int y, int w, int h) {
        // Industrial concrete foundation ring curb
        fillNoisyRect(g, x, y, w, h, new Color(0x78, 0x71, 0x6C), 4, 501L);

        // Concrete bevel top edge
        g.setColor(new Color(0xA8, 0xA2, 0x9E));
        g.fillRect(x, y, w, 4);
        g.setColor(new Color(0xD6, 0xD3, 0xD1));
        g.drawLine(x, y, x + w - 1, y);

        // Bottom foundation shadow
        g.setColor(new Color(0x44, 0x40, 0x3C));
        g.fillRect(x, y + h - 5, w, 5);

        // Expansion joints
        for (int jx = x + 16; jx < x + w; jx += 16) {
            g.setColor(new Color(0x29, 0x25, 0x24));
            g.drawLine(jx, y + 4, jx, y + h - 6);
        }
    }

    private static void drawRoofCone(Graphics2D g, int x, int y, int w, int h) {
        // Conical roof enamel panels
        fillNoisyRect(g, x, y, w, h, new Color(0x18, 0x1E, 0x29), 3, 601L);

        // Radial roof panel seams
        for (int rx = x + 12; rx < x + w; rx += 12) {
            g.setColor(new Color(0x0C, 0x10, 0x17));
            g.drawLine(rx, y, rx, y + h - 1);
            g.setColor(new Color(0x2B, 0x36, 0x4A));
            g.drawLine(rx + 1, y, rx + 1, y + h - 1);
        }
    }

    private static void drawDrainNozzle(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x13, 0x18, 0x22), 3, 701L);

        // Circular drain nozzle flange
        int cx = x + w / 2, cy = y + h / 2, r = 18;
        g.setColor(new Color(0x0B, 0x0E, 0x13));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        g.setColor(new Color(0x64, 0x74, 0x8B));
        g.drawOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

        // Dark hollow fluid bore
        int br = 9;
        g.setColor(new Color(0x05, 0x08, 0x0D));
        g.fillOval(cx - br, cy - br, br * 2, br * 2);

        // Perimeter stud bolts
        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI * 2.0 / 8.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawBolt(g, bx - 1, by - 1);
        }
    }

    private static void drawFillNozzle(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x18, 0x1E, 0x2A), 3, 801L);

        // Circular fill intake flange
        int cx = x + w / 2, cy = y + h / 2, r = 18;
        g.setColor(new Color(0x0E, 0x13, 0x1C));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x25, 0x63, 0xEB)); // Blue accent for intake
        g.drawOval(cx - r + 1, cy - r + 1, (r - 1) * 2, (r - 1) * 2);
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.fillOval(cx - r + 3, cy - r + 3, (r - 3) * 2, (r - 3) * 2);

        // Hollow bore
        int br = 8;
        g.setColor(new Color(0x05, 0x08, 0x0D));
        g.fillOval(cx - br, cy - br, br * 2, br * 2);

        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI * 2.0 / 8.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawBolt(g, bx - 1, by - 1);
        }
    }

    private static void drawManwayDoor(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x11, 0x16, 0x20), 3, 901L);

        // Circular manway inspection door with heavy bolts
        int cx = x + w / 2, cy = y + h / 2, r = 19;
        g.setColor(new Color(0x0A, 0x0D, 0x12));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x1F, 0x29, 0x37));
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        g.setColor(new Color(0x4B, 0x55, 0x63));
        g.drawOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

        // 12 Manway door perimeter bolts
        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI * 2.0 / 12.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawBolt(g, bx - 1, by - 1);
        }

        // Heavy hinge on left side
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.fillRect(cx - r + 1, cy - 5, 5, 10);
        g.setColor(new Color(0x9C, 0xA3, 0xAF));
        g.drawRect(cx - r + 1, cy - 5, 5, 10);
    }

    private static void drawGalvanizedHandrailSteel(Graphics2D g, int x, int y, int w, int h) {
        // Matching galvanized steel for perimeter safety handrails & stanchions
        fillNoisyRect(g, x, y, w, h, new Color(0xCF, 0xD8, 0xE3), 3, 1001L);

        // Highlight stripe
        g.setColor(new Color(0xF8, 0xFA, 0xFC));
        g.fillRect(x, y + 2, w, 4);
        g.drawLine(x, y, x + w - 1, y);

        // Shadow bottom
        g.setColor(new Color(0x82, 0x90, 0xA4));
        g.fillRect(x, y + h - 5, w, 5);
    }

    // =========================================================================
    // 2. CUSTOM GUI TEXTURE (256x256)
    // =========================================================================
    public static void generateGuiTexture() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176, h = 166;

        // Container background
        fillNoisyRect(g, 0, 0, w, h, new Color(0xC6, 0xC6, 0xC6), 2, 2001L);

        // Beveled outer border
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.drawLine(1, h - 2, w - 2, h - 2);
        g.drawLine(w - 2, 1, w - 2, h - 2);

        // Vertical Sight Glass Reservoir Frame (x: 37, y: 16, w: 34, h: 56)
        drawBeveledRecess(g, 37, 16, 34, 56);

        // Fluid Information Display Box (x: 74, y: 16, w: 64, h: 32)
        drawBeveledRecess(g, 74, 16, 64, 32);

        // Container Slot 0: Bucket In (x: 147, y: 17, w: 18, h: 18)
        drawItemSlot(g, 147, 17);

        // Flow Arrow downward from Input Slot to Output Slot
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.drawLine(155, 39, 155, 47);
        g.drawLine(156, 39, 156, 47);
        g.drawLine(153, 44, 155, 47);
        g.drawLine(158, 44, 156, 47);

        // Container Slot 1: Bucket Out (x: 147, y: 51, w: 18, h: 18)
        drawItemSlot(g, 147, 51);

        // Player Inventory Grid (3 rows of 9 at x: 7, y: 83)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawItemSlot(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // Hotbar Grid (9 slots at x: 7, y: 141)
        for (int col = 0; col < 9; col++) {
            drawItemSlot(g, 7 + col * 18, 141);
        }

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/storage_tank.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated GUI texture at: " + out.getAbsolutePath());
    }

    private static void drawBeveledRecess(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    private static void drawItemSlot(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + 17, y);
        g.drawLine(x, y, x, y + 17);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y + 17, x + 17, y + 17);
        g.drawLine(x + 17, y, x + 17, y + 17);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(x + 1, y + 1, 16, 16);
    }

    // =========================================================================
    // 3. 16x16 BLOCK TEXTURES
    // =========================================================================
    public static void generateBlockTextures() throws Exception {
        // 1. Storage Tank Casing (16x16)
        BufferedImage casing = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = casing.createGraphics();
        fillNoisyRect(g, 0, 0, 16, 16, new Color(0x14, 0x1A, 0x24), 2, 3001L);
        // Beveled plate edges
        g.setColor(new Color(0x2A, 0x35, 0x47));
        g.drawRect(0, 0, 15, 15);
        g.setColor(new Color(0x0C, 0x10, 0x17));
        g.drawLine(0, 15, 15, 15);
        g.drawLine(15, 0, 15, 15);
        // Corner bolts
        drawBolt(g, 2, 2);
        drawBolt(g, 13, 2);
        drawBolt(g, 2, 13);
        drawBolt(g, 13, 13);
        // Center panel sheen
        g.setColor(new Color(0x1E, 0x26, 0x35));
        g.fillRect(4, 4, 8, 8);
        g.dispose();
        ImageIO.write(casing, "PNG", new File("src/main/resources/assets/refinery/textures/block/storage_tank_casing.png"));

        // 2. Storage Tank Drain Valve (16x16)
        BufferedImage drain = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = drain.createGraphics();
        fillNoisyRect(g, 0, 0, 16, 16, new Color(0x14, 0x1A, 0x24), 2, 3002L);
        g.setColor(new Color(0x10, 0xB9, 0x81)); // Green valve accent
        g.drawRect(1, 1, 13, 13);
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.fillOval(3, 3, 10, 10);
        g.setColor(new Color(0x06, 0x09, 0x0E));
        g.fillOval(5, 5, 6, 6);
        drawBolt(g, 2, 2);
        drawBolt(g, 13, 2);
        drawBolt(g, 2, 13);
        drawBolt(g, 13, 13);
        g.dispose();
        ImageIO.write(drain, "PNG", new File("src/main/resources/assets/refinery/textures/block/storage_tank_drain_valve.png"));

        // 3. Storage Tank Fill Valve (16x16)
        BufferedImage fill = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = fill.createGraphics();
        fillNoisyRect(g, 0, 0, 16, 16, new Color(0x14, 0x1A, 0x24), 2, 3003L);
        g.setColor(new Color(0x3B, 0x82, 0xF6)); // Blue intake accent
        g.drawRect(1, 1, 13, 13);
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.fillOval(3, 3, 10, 10);
        g.setColor(new Color(0x06, 0x09, 0x0E));
        g.fillOval(5, 5, 6, 6);
        drawBolt(g, 2, 2);
        drawBolt(g, 13, 2);
        drawBolt(g, 2, 13);
        drawBolt(g, 13, 13);
        g.dispose();
        ImageIO.write(fill, "PNG", new File("src/main/resources/assets/refinery/textures/block/storage_tank_fill_valve.png"));

        System.out.println("Block textures generated successfully!");
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int noiseAmp, long seed) {
        Random rand = new Random(seed);
        int r0 = base.getRed();
        int gr0 = base.getGreen();
        int b0 = base.getBlue();

        for (int px = x; px < x + w; px++) {
            for (int py = y; py < y + h; py++) {
                int delta = rand.nextInt(noiseAmp * 2 + 1) - noiseAmp;
                int r = Math.clamp(r0 + delta, 0, 255);
                int gr = Math.clamp(gr0 + delta, 0, 255);
                int b = Math.clamp(b0 + delta, 0, 255);
                g.setColor(new Color(r, gr, b));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void drawBolt(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x0A, 0x0E, 0x14));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillRect(x, y, 1, 1);
    }
}
