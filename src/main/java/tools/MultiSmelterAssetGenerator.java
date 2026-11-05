package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates the 256x256 custom industrial GUI texture for the Multi-Smelter.
 * Styling matches Industrial Grinder and Metal Press:
 * - Dark industrial steel plate base with beveled borders and corner rivets
 * - Recessed energy storage well on the left
 * - Symmetrical dual panel plates for 8-slot input (left) and 8-slot output (right)
 * - Central Dual-Power console with combustion flame well, solid fuel slot well, and electric induction well
 * - Dedicated recessed progress grooves under each input slot
 * - Player inventory and hotbar wells with authentic dark metallic bevels
 */
public class MultiSmelterAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateGuiTexture();
        System.out.println("Multi-Smelter GUI texture generated successfully!");
    }

    public static void generateGuiTexture() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 176;
        int h = 166;

        // 1. Base Industrial Steel Panel
        fillNoisyRect(g, 0, 0, w, h, new Color(0x2B, 0x30, 0x36), 3, 3001L);

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

        // 2. Energy Storage Well (x: 9, y: 17, w: 14, h: 52) -> inner fill at (10, 18, 12, 50)
        drawRecessedWell(g, 9, 17, 14, 52, new Color(0x19, 0x1E, 0x24));
        // Energy gauge tick marks
        g.setColor(new Color(0x35, 0x3C, 0x46));
        for (int gy = 27; gy <= 59; gy += 10) {
            g.drawLine(11, gy, 14, gy);
            g.drawLine(18, gy, 21, gy);
        }

        // 3. Input Slots Housing Plate (x: 24, y: 16, w: 74, h: 44)
        drawPanelPlate(g, 24, 16, 74, 44);
        // 8 Input slot wells (4 cols x 2 rows):
        // Col 0: 27 (well 26), Col 1: 45 (well 44), Col 2: 63 (well 62), Col 3: 81 (well 80)
        // Row 0: 19 (well 18), Row 1: 39 (well 38)
        for (int r = 0; r < 2; r++) {
            int wy = r == 0 ? 18 : 38;
            for (int c = 0; c < 4; c++) {
                int wx = 26 + c * 18;
                drawRecessedSlotWell(g, wx, wy);
            }
        }
        // Dedicated 2px recessed progress grooves under input slots (Row 0: y=36, Row 1: y=56)
        for (int r = 0; r < 2; r++) {
            int py = r == 0 ? 36 : 56;
            for (int c = 0; c < 4; c++) {
                int px = 27 + c * 18;
                drawRecessedWell(g, px - 1, py - 1, 18, 4, new Color(0x15, 0x18, 0x1C));
            }
        }

        // 4. Output Slots Housing Plate (x: 98, y: 16, w: 74, h: 44)
        drawPanelPlate(g, 98, 16, 74, 44);
        // 8 Output slot wells:
        // Col 0: 101 (well 100), Col 1: 119 (well 118), Col 2: 137 (well 136), Col 3: 155 (well 154)
        // Row 0: 19 (well 18), Row 1: 39 (well 38)
        for (int r = 0; r < 2; r++) {
            int wy = r == 0 ? 18 : 38;
            for (int c = 0; c < 4; c++) {
                int wx = 100 + c * 18;
                drawRecessedSlotWell(g, wx, wy);
            }
        }

        // 5. Central Dual-Power Combustion & Induction Console (x: 66, y: 60, w: 64, h: 22)
        drawPanelPlate(g, 66, 60, 64, 22);

        // Flame indicator well (x: 69, y: 62, w: 16, h: 16) -> inner at (70, 63, 14, 14)
        drawRecessedWell(g, 69, 62, 16, 16, new Color(0x18, 0x15, 0x15));
        drawFlameIconMuted(g, 71, 64);

        // Fuel Slot well (x: 88, y: 61, w: 18, h: 18) -> slot at (89, 62)
        drawRecessedSlotWell(g, 88, 61);

        // Electric Induction coil well (x: 109, y: 62, w: 16, h: 16) -> inner at (110, 63, 14, 14)
        drawRecessedWell(g, 109, 62, 16, 16, new Color(0x12, 0x18, 0x1D));
        drawLightningIconMuted(g, 113, 64);

        // 6. Player Inventory Wells (3 rows of 9 at y: 84) -> slot at (8 + col*18, 84 + row*18)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // 7. Player Hotbar Wells (1 row of 9 at y: 142) -> slot at (8 + col*18, 142)
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 7 + col * 18, 141);
        }

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/multi_smelter.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated custom multi-smelter GUI texture at: " + out.getAbsolutePath());
    }

    private static void drawFlameIconMuted(Graphics2D g, int x, int y) {
        // Subtle dark orange/gray idle flame silhouette
        g.setColor(new Color(0x3D, 0x24, 0x1D));
        int[] xp = {x + 5, x + 7, x + 10, x + 11, x + 8, x + 9, x + 7, x + 4, x + 2, x + 1, x + 3};
        int[] yp = {y + 1, y + 3, y + 6,  y + 10, y + 11,y + 8, y + 11,y + 11,y + 8, y + 5, y + 4};
        g.fillPolygon(xp, yp, xp.length);
    }

    private static void drawLightningIconMuted(Graphics2D g, int x, int y) {
        // Subtle dark cyan/gray idle electric bolt silhouette
        g.setColor(new Color(0x1D, 0x2F, 0x3D));
        int[] xp = {x + 4, x + 1, x + 4, x + 3, x + 7, x + 4, x + 6};
        int[] yp = {y + 1, y + 6, y + 6, y + 11, y + 5, y + 5, y + 1};
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
