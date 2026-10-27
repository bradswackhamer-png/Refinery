package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates the 256x256 texture atlas for the 3-block-wide assembled Metal Press:
 * - Bottom: Immersive Engineering style 3-block-wide Dark Steel Conveyor Bed with bottom vents,
 *   4 brown/bronze vertical tensioner straps with silver rivets, steel end rollers, and a
 *   stitched terracotta-orange conveyor belt.
 * - Center: 4-post Dark Steel Gantry Frame with central bolster anvil platen and horizontal collar ring.
 * - Top: AIDA NC1-1500 Stamping Head with off-white industrial enamel crown, green AIDA 4-diamond
 *   emblem + text, green NC1-1500 badge, circular analog tonnage/crank dial gauge, side cooling louvers,
 *   amber hydraulic cylinder, white + safety-yellow stamping ram bar, side electrical cabinet, and
 *   operator control pendant with red mushroom E-stop button.
 */
public class AidaAssetGenerator {

    public static void main(String[] args) throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark industrial steel so no UV edge ever bleeds transparency
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x2B, 0x30, 0x36), 4, 101L);

        // =========================================================================
        // 1. CONVEYOR BED FRONT / BACK SIDE PANEL (0, 0, 96, 24)
        //    96x24 px (2x resolution for 48x12 model face)
        // =========================================================================
        drawConveyorSidePanel(g, 0, 0, 96, 24);

        // =========================================================================
        // 2. CONVEYOR BED LEFT / RIGHT END PANEL (96, 0, 56, 24)
        //    56x24 px (2x resolution for 28x12 model face)
        // =========================================================================
        drawConveyorEndPanel(g, 96, 0, 56, 24);

        // =========================================================================
        // 3. MILLED STEEL ANVIL PLATEN & ROLLER STEEL (160, 0, 48, 48)
        // =========================================================================
        drawAnvilPlaten(g, 160, 0, 48, 48);

        // =========================================================================
        // 4. STITCHED TERRACOTTA-ORANGE CONVEYOR BELT TOP (0, 24, 96, 48)
        //    96x48 px (2x resolution for 48x24 belt top)
        // =========================================================================
        drawConveyorBeltTop(g, 0, 24, 96, 48);

        // =========================================================================
        // 5. DARK STEEL GANTRY POSTS & COLLAR BEAM (96, 24, 64, 48)
        // =========================================================================
        drawDarkGantrySteel(g, 96, 24, 64, 48);

        // =========================================================================
        // 6. AIDA NC1-1500 STAMPING HEAD FRONT FACE (0, 72, 64, 48)
        //    Off-white industrial enamel, green AIDA diamond logo, NC1-1500 badge,
        //    and circular analog crank-angle / tonnage dial gauge
        // =========================================================================
        drawAidaHeadFront(g, 0, 72, 64, 48);

        // =========================================================================
        // 7. AIDA / IE STAMPING HEAD SIDE PANEL WITH 3 LOUVERS (64, 72, 64, 48)
        // =========================================================================
        drawAidaHeadSideLouvers(g, 64, 72, 64, 48);

        // =========================================================================
        // 8. STAMPING HEAD STEPPED TOP CAP WITH ORANGE/AMBER CORE (128, 72, 64, 48)
        // =========================================================================
        drawHeadTopCap(g, 128, 72, 64, 48);

        // =========================================================================
        // 9. AIDA SIDE ELECTRICAL CABINET (192, 72, 64, 64)
        // =========================================================================
        drawElectricalCabinet(g, 192, 72, 64, 64);

        // =========================================================================
        // 10. AIDA OPERATOR CONTROL PENDANT WITH RED E-STOP (0, 120, 64, 48)
        // =========================================================================
        drawControlPendant(g, 0, 120, 64, 48);

        // =========================================================================
        // 11. AMBER HYDRAULIC CYLINDER & POLISHED CHROME ROD (64, 120, 48, 48)
        // =========================================================================
        drawHydraulicCylinder(g, 64, 120, 48, 48);

        // =========================================================================
        // 12. AIDA WHITE RAM SLIDE & SAFETY-YELLOW DIE BAR (112, 120, 80, 48)
        // =========================================================================
        drawAidaRamSlide(g, 112, 120, 80, 48);

        // =========================================================================
        // 13. HARDENED DARK TOOL STEEL DIE & ROLLER TEXTURE (208, 0, 48, 48)
        // =========================================================================
        drawRollerAndToolSteel(g, 208, 0, 48, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/aida_press.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated 256x256 AIDA + Conveyor Press texture at: " + out.getAbsolutePath());
    }

    private static void drawConveyorSidePanel(Graphics2D g, int x, int y, int w, int h) {
        // Dark gunmetal steel frame
        fillNoisyRect(g, x, y, w, h, new Color(0x31, 0x36, 0x3D), 5, 201L);

        // Top & bottom machined steel trim rails
        g.setColor(new Color(0x47, 0x4E, 0x57));
        g.fillRect(x, y, w, 3);
        g.setColor(new Color(0x20, 0x24, 0x29));
        g.fillRect(x, y + 3, w, 1);
        g.fillRect(x, y + h - 3, w, 3);

        // Recessed middle channel
        fillNoisyRect(g, x + 2, y + 4, w - 4, h - 7, new Color(0x28, 0x2C, 0x32), 4, 202L);

        // 6 dark bottom air vents (2 per block across the 3 blocks)
        int[] ventXs = {8, 21, 40, 53, 72, 85};
        for (int vx : ventXs) {
            g.setColor(new Color(0x14, 0x16, 0x19));
            g.fillRect(x + vx, y + h - 9, 6, 6);
            g.setColor(new Color(0x43, 0x4A, 0x52));
            g.drawRect(x + vx, y + h - 9, 6, 6);
            // Horizontal grill bars inside vent
            g.setColor(new Color(0x2A, 0x2F, 0x35));
            g.fillRect(x + vx + 1, y + h - 7, 5, 1);
            g.fillRect(x + vx + 1, y + h - 5, 5, 1);
        }

        // 4 vertical saddle-brown / bronze tensioner straps with silver rivets (like IE Metal Press)
        int[] strapXs = {2, 30, 62, 90};
        for (int sx : strapXs) {
            // Dark drop shadow
            g.setColor(new Color(0x18, 0x14, 0x11));
            g.fillRect(x + sx - 1, y, 6, h);
            // Saddle-brown leather/bronze body
            fillNoisyRect(g, x + sx, y, 4, h, new Color(0x6E, 0x47, 0x2B), 6, 203L + sx);
            // Warm bronze highlight edge
            g.setColor(new Color(0x8C, 0x5D, 0x38));
            g.fillRect(x + sx, y, 1, h);
            // Silver/steel rivets near top, middle, bottom
            g.setColor(new Color(0xB8, 0xC0, 0xC8));
            g.fillRect(x + sx + 1, y + 2, 2, 2);
            g.fillRect(x + sx + 1, y + 10, 2, 2);
            g.fillRect(x + sx + 1, y + 18, 2, 2);
        }
    }

    private static void drawConveyorEndPanel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2E, 0x33, 0x3A), 5, 210L);
        g.setColor(new Color(0x45, 0x4C, 0x55));
        g.fillRect(x, y, w, 3);
        g.setColor(new Color(0x1E, 0x22, 0x27));
        g.fillRect(x, y + h - 3, w, 3);

        // 2 bottom air vents on the end face
        int[] ventXs = {14, 34};
        for (int vx : ventXs) {
            g.setColor(new Color(0x14, 0x16, 0x19));
            g.fillRect(x + vx, y + h - 9, 8, 6);
            g.setColor(new Color(0x43, 0x4A, 0x52));
            g.drawRect(x + vx, y + h - 9, 8, 6);
        }

        // 2 brown/bronze corner straps with silver rivets
        int[] strapXs = {3, w - 7};
        for (int sx : strapXs) {
            fillNoisyRect(g, x + sx, y, 4, h, new Color(0x6E, 0x47, 0x2B), 6, 215L + sx);
            g.setColor(new Color(0x8C, 0x5D, 0x38));
            g.fillRect(x + sx, y, 1, h);
            g.setColor(new Color(0xB8, 0xC0, 0xC8));
            g.fillRect(x + sx + 1, y + 3, 2, 2);
            g.fillRect(x + sx + 1, y + 17, 2, 2);
        }

        // Center roller bearing housing bar
        g.setColor(new Color(0x5A, 0x62, 0x6B));
        g.fillRect(x + 10, y + 5, w - 20, 4);
        g.setColor(new Color(0x90, 0x99, 0xA2));
        g.fillRect(x + 12, y + 6, w - 24, 2);
    }

    private static void drawConveyorBeltTop(Graphics2D g, int x, int y, int w, int h) {
        // Warm terracotta / rust-orange heavy-duty vulcanized fabric belt (matching IE screenshot)
        fillNoisyRect(g, x, y, w, h, new Color(0xB5, 0x5B, 0x37), 8, 301L);

        // Subtle longitudinal belt weave bands
        for (int py = 0; py < h; py += 2) {
            g.setColor((py % 4 == 0) ? new Color(0xBF, 0x63, 0x3E) : new Color(0xA6, 0x51, 0x30));
            g.drawLine(x, y + py, x + w - 1, y + py);
        }
        // Re-apply subtle grain so weave looks natural
        Random rand = new Random(302L);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                if (rand.nextInt(3) == 0) {
                    int rgb = (rand.nextBoolean()) ? 0x18FFFFFF : 0x18000000;
                    g.setColor(new Color(rgb, true));
                    g.fillRect(x + px, y + py, 1, 1);
                }
            }
        }

        // Darkened vulcanized rubber side guide edges (top & bottom 4px)
        fillNoisyRect(g, x, y, w, 4, new Color(0x7D, 0x3A, 0x20), 6, 303L);
        fillNoisyRect(g, x, y + h - 4, w, 4, new Color(0x7D, 0x3A, 0x20), 6, 304L);
        g.setColor(new Color(0x59, 0x28, 0x15));
        g.fillRect(x, y, w, 1);
        g.fillRect(x, y + h - 1, w, 1);

        // 3 dark cross-stitched transverse belt seams (at 1/6, 1/2, 5/6 across the 3 blocks)
        int[] seamXs = {16, 48, 80};
        for (int sx : seamXs) {
            // Dark seam groove
            g.setColor(new Color(0x47, 0x1F, 0x10));
            g.fillRect(x + sx, y + 2, 2, h - 4);
            // Highlight edge next to groove
            g.setColor(new Color(0xCC, 0x72, 0x4B));
            g.fillRect(x + sx + 2, y + 2, 1, h - 4);
            // Metallic/dark leather cross-stitches across the seam
            for (int sy = 5; sy < h - 5; sy += 4) {
                g.setColor(new Color(0x36, 0x18, 0x0C));
                g.fillRect(x + sx - 2, y + sy, 6, 2);
                g.setColor(new Color(0xD8, 0x8A, 0x62));
                g.fillRect(x + sx - 1, y + sy, 1, 1);
                g.fillRect(x + sx + 2, y + sy, 1, 1);
            }
        }
    }

    private static void drawAnvilPlaten(Graphics2D g, int x, int y, int w, int h) {
        // Machined medium-grey bolster anvil plate under the press head
        fillNoisyRect(g, x, y, w, h, new Color(0x6D, 0x74, 0x7D), 6, 401L);
        // Beveled outer rim
        g.setColor(new Color(0x8C, 0x95, 0x9F));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x3B, 0x40, 0x46));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        // Machined T-slots & target crosshairs on the anvil bed
        g.setColor(new Color(0x35, 0x3A, 0x40));
        for (int slotY = 8; slotY < h - 6; slotY += 8) {
            g.fillRect(x + 4, y + slotY, w - 8, 2);
        }
        g.fillRect(x + w / 2 - 1, y + 4, 2, h - 8);
    }

    private static void drawDarkGantrySteel(Graphics2D g, int x, int y, int w, int h) {
        // Heavy dark structural steel for the 4 gantry posts & middle support collar
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x42), 5, 501L);
        // Vertical brushed highlights
        g.setColor(new Color(0x48, 0x50, 0x59));
        g.fillRect(x + 2, y, 2, h);
        g.fillRect(x + w / 2, y, 2, h);
        g.setColor(new Color(0x22, 0x26, 0x2B));
        g.drawRect(x, y, w - 1, h - 1);

        // Structural rivets along edges
        g.setColor(new Color(0x7C, 0x85, 0x90));
        for (int ry = 4; ry < h - 3; ry += 8) {
            g.fillRect(x + 3, y + ry, 2, 2);
            g.fillRect(x + w - 5, y + ry, 2, 2);
        }
    }

    private static void drawAidaHeadFront(Graphics2D g, int x, int y, int w, int h) {
        // Outer dark steel structural frame (like IE + AIDA crown)
        fillNoisyRect(g, x, y, w, h, new Color(0x36, 0x3C, 0x44), 4, 601L);

        // Inner off-white / cream AIDA enamel panel (#E6EAE4)
        fillNoisyRect(g, x + 3, y + 3, w - 6, h - 6, new Color(0xE6, 0xEA, 0xE4), 4, 602L);
        // Subtle panel bevel
        g.setColor(new Color(0xFA, 0xFC, 0xF8));
        g.drawLine(x + 3, y + 3, x + w - 4, y + 3);
        g.drawLine(x + 3, y + 3, x + 3, y + h - 4);
        g.setColor(new Color(0xB8, 0xBE, 0xB6));
        g.drawLine(x + 3, y + h - 4, x + w - 4, y + h - 4);
        g.drawLine(x + w - 4, y + 3, x + w - 4, y + h - 4);

        // Top AIDA Green 4-Diamond Emblem (centered at x + 15, y + 6)
        Color aidaGreen = new Color(0x00, 0x82, 0x48);
        g.setColor(aidaGreen);
        // 4-diamond star emblem on left of AIDA text
        g.fillRect(x + 12, y + 6, 2, 2);
        g.fillRect(x + 10, y + 8, 2, 2);
        g.fillRect(x + 14, y + 8, 2, 2);
        g.fillRect(x + 12, y + 10, 2, 2);

        // Pixel-art bold "AIDA" text (x + 19 .. x + 49, y + 5 .. y + 12)
        // 'A'
        g.fillRect(x + 20, y + 5, 4, 1);
        g.fillRect(x + 19, y + 6, 2, 6);
        g.fillRect(x + 23, y + 6, 2, 6);
        g.fillRect(x + 20, y + 8, 4, 2);
        // 'I'
        g.fillRect(x + 27, y + 5, 4, 1);
        g.fillRect(x + 28, y + 6, 2, 5);
        g.fillRect(x + 27, y + 11, 4, 1);
        // 'D'
        g.fillRect(x + 33, y + 5, 5, 1);
        g.fillRect(x + 33, y + 6, 2, 5);
        g.fillRect(x + 37, y + 6, 2, 5);
        g.fillRect(x + 33, y + 11, 5, 1);
        // 'A'
        g.fillRect(x + 42, y + 5, 4, 1);
        g.fillRect(x + 41, y + 6, 2, 6);
        g.fillRect(x + 45, y + 6, 2, 6);
        g.fillRect(x + 42, y + 8, 4, 2);

        // "NC1-1500" green badge bar below AIDA
        g.setColor(new Color(0x00, 0x70, 0x3C));
        // N C 1 - 1 5 0 0 compact pixel glyphs (y + 14..17)
        int bx = x + 16;
        int by = y + 14;
        // N
        g.fillRect(bx, by, 1, 4); g.fillRect(bx + 1, by + 1, 1, 1); g.fillRect(bx + 2, by, 1, 4);
        // C
        g.fillRect(bx + 4, by, 3, 1); g.fillRect(bx + 4, by, 1, 4); g.fillRect(bx + 4, by + 3, 3, 1);
        // 1
        g.fillRect(bx + 8, by, 1, 4);
        // -
        g.fillRect(bx + 10, by + 2, 2, 1);
        // 1
        g.fillRect(bx + 13, by, 1, 4);
        // 5
        g.fillRect(bx + 15, by, 3, 1); g.fillRect(bx + 15, by + 1, 1, 1); g.fillRect(bx + 15, by + 2, 3, 1); g.fillRect(bx + 17, by + 2, 1, 2); g.fillRect(bx + 15, by + 3, 3, 1);
        // 0
        g.fillRect(bx + 19, by, 3, 4); g.setColor(new Color(0xE6, 0xEA, 0xE4)); g.fillRect(bx + 20, by + 1, 1, 2);
        // 0
        g.setColor(new Color(0x00, 0x70, 0x3C));
        g.fillRect(bx + 23, by, 3, 4); g.setColor(new Color(0xE6, 0xEA, 0xE4)); g.fillRect(bx + 24, by + 1, 1, 2);

        // Circular Analog Tonnage / Crank Angle Dial Gauge (centered at cx = x + 32, cy = y + 31, r = 10)
        int cx = x + 32;
        int cy = y + 31;
        int r = 10;
        // Outer dark bezel
        g.setColor(new Color(0x28, 0x2C, 0x30));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        // White gauge face
        g.setColor(new Color(0xFA, 0xFA, 0xF7));
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        // Dark degree tick marks around the dial perimeter
        g.setColor(new Color(0x33, 0x38, 0x3D));
        g.fillRect(cx - 1, cy - r + 2, 2, 2); // 12 o'clock
        g.fillRect(cx - 1, cy + r - 4, 2, 2); // 6 o'clock
        g.fillRect(cx - r + 2, cy - 1, 2, 2); // 9 o'clock
        g.fillRect(cx + r - 4, cy - 1, 2, 2); // 3 o'clock
        g.fillRect(cx - 6, cy - 6, 2, 2);     // 10 o'clock
        g.fillRect(cx + 5, cy - 6, 2, 2);     // 2 o'clock
        // Green safe operating zone arc on top-right
        g.setColor(aidaGreen);
        g.drawLine(cx + 3, cy - 6, cx + 6, cy - 3);
        // Pointer needle & red center cap
        g.setColor(new Color(0x1E, 0x22, 0x26));
        g.drawLine(cx, cy, cx + 5, cy - 5);
        g.setColor(new Color(0xD9, 0x2B, 0x2B));
        g.fillRect(cx - 2, cy - 2, 4, 4);

        // Bottom bolster window frame on the head crown
        g.setColor(new Color(0x2B, 0x30, 0x36));
        g.fillRect(x + 8, y + h - 5, w - 16, 3);
    }

    private static void drawAidaHeadSideLouvers(Graphics2D g, int x, int y, int w, int h) {
        // Outer dark steel frame
        fillNoisyRect(g, x, y, w, h, new Color(0x36, 0x3C, 0x44), 4, 701L);
        // Inner off-white/warm grey AIDA side panel
        fillNoisyRect(g, x + 4, y + 4, w - 8, h - 8, new Color(0xD8, 0xDD, 0xD6), 4, 702L);
        g.setColor(new Color(0x9A, 0xA0, 0x99));
        g.drawRect(x + 4, y + 4, w - 9, h - 9);

        // 3 prominent horizontal recessed cooling louvers (matching the IE screenshot & AIDA side vents)
        int[] slotYs = {11, 21, 31};
        for (int sy : slotYs) {
            // Outer dark metallic bezel
            g.setColor(new Color(0x4A, 0x51, 0x58));
            g.fillRect(x + 10, y + sy - 1, w - 20, 6);
            // Deep recessed shadow slot
            g.setColor(new Color(0x14, 0x17, 0x1A));
            g.fillRect(x + 12, y + sy, w - 24, 4);
            // Bottom highlight lip
            g.setColor(new Color(0xEE, 0xF2, 0xEC));
            g.fillRect(x + 10, y + sy + 5, w - 20, 1);
        }
    }

    private static void drawHeadTopCap(Graphics2D g, int x, int y, int w, int h) {
        // Dark steel stepped crown cap
        fillNoisyRect(g, x, y, w, h, new Color(0x36, 0x3C, 0x44), 5, 801L);
        g.setColor(new Color(0x50, 0x58, 0x62));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        // Inner raised housing ring
        fillNoisyRect(g, x + 12, y + 8, w - 24, h - 16, new Color(0x28, 0x2C, 0x32), 4, 802L);

        // Glowing copper-orange / amber power core in the center (like the IE screenshot top cap)
        fillNoisyRect(g, x + 22, y + 14, w - 44, h - 28, new Color(0xD9, 0x6B, 0x27), 8, 803L);
        g.setColor(new Color(0xF5, 0x9E, 0x42));
        g.drawRect(x + 23, y + 15, w - 47, h - 31);
    }

    private static void drawElectricalCabinet(Graphics2D g, int x, int y, int w, int h) {
        // Light industrial enamel grey cabinet body
        fillNoisyRect(g, x, y, w, h, new Color(0xCC, 0xD1, 0xCC), 4, 901L);
        // Cabinet door seam
        g.setColor(new Color(0x78, 0x7E, 0x7A));
        g.drawRect(x + 3, y + 3, w - 7, h - 7);
        g.setColor(new Color(0xEC, 0xF0, 0xEC));
        g.drawLine(x + 4, y + 4, x + w - 5, y + 4);

        // Upper cooling louver slits on cabinet door
        g.setColor(new Color(0x45, 0x4A, 0x48));
        for (int ly = 8; ly <= 18; ly += 3) {
            g.fillRect(x + 8, y + ly, w - 16, 2);
        }

        // Yellow high-voltage warning diamond/triangle
        g.setColor(new Color(0xF5, 0xB8, 0x00));
        g.fillRect(x + 12, y + 26, 8, 8);
        g.setColor(new Color(0x1E, 0x1E, 0x1E));
        g.drawRect(x + 12, y + 26, 8, 8);
        g.fillRect(x + 15, y + 28, 2, 4);

        // Black rotary disconnect handle
        g.setColor(new Color(0x22, 0x25, 0x28));
        g.fillRect(x + w - 14, y + 27, 5, 10);
        g.setColor(new Color(0xD9, 0x2B, 0x2B));
        g.fillRect(x + w - 13, y + 29, 3, 3);
    }

    private static void drawControlPendant(Graphics2D g, int x, int y, int w, int h) {
        // Off-white/grey operator box
        fillNoisyRect(g, x, y, w, h, new Color(0xD4, 0xD9, 0xD4), 4, 1001L);
        g.setColor(new Color(0x52, 0x58, 0x56));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Illuminated blue/cyan PLC status display screen
        g.setColor(new Color(0x16, 0x24, 0x30));
        g.fillRect(x + 6, y + 5, w - 12, 12);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.fillRect(x + 8, y + 7, w - 16, 8);
        g.setColor(new Color(0xBA, 0xE6, 0xFD));
        g.fillRect(x + 10, y + 9, 14, 2);
        g.fillRect(x + 10, y + 12, 22, 1);

        // Green cycle start button, yellow reset button
        g.setColor(new Color(0x16, 0xA3, 0x4A));
        g.fillRect(x + 8, y + 22, 8, 8);
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillRect(x + 20, y + 22, 8, 8);

        // Prominent Red Mushroom Emergency Stop Button (with yellow collar)
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 34, y + 20, 16, 16);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 36, y + 22, 12, 12);
        g.setColor(new Color(0xF8, 0x71, 0x71));
        g.fillRect(x + 38, y + 24, 4, 4);
    }

    private static void drawHydraulicCylinder(Graphics2D g, int x, int y, int w, int h) {
        // Amber/golden-bronze top hydraulic cylinder (seen atop the AIDA press in aidapress.jpg)
        for (int px = 0; px < w; px++) {
            double norm = Math.sin((px / (double) w) * Math.PI);
            int r = (int) (150 + 65 * norm);
            int gr = (int) (95 + 50 * norm);
            int b = (int) (25 + 25 * norm);
            g.setColor(new Color(clamp(r), clamp(gr), clamp(b)));
            g.fillRect(x + px, y, 1, h);
        }
        // Chrome seal bands
        g.setColor(new Color(0xD8, 0xE0, 0xE8));
        g.fillRect(x, y + 2, w, 4);
        g.fillRect(x, y + h - 6, w, 4);
        g.setColor(new Color(0x38, 0x3E, 0x44));
        g.fillRect(x, y + 6, w, 2);
        g.fillRect(x, y + h - 8, w, 2);
    }

    private static void drawAidaRamSlide(Graphics2D g, int x, int y, int w, int h) {
        // Upper 60%: Off-white AIDA moving ram slide block with vertical guide grooves
        int splitY = (h * 3) / 5;
        fillNoisyRect(g, x, y, w, splitY, new Color(0xDF, 0xE4, 0xDE), 4, 1101L);
        g.setColor(new Color(0x9E, 0xA5, 0x9E));
        g.drawRect(x, y, w - 1, splitY - 1);
        // Recessed vertical lubrication/gib slots
        g.setColor(new Color(0x6E, 0x75, 0x70));
        g.fillRect(x + 10, y + 3, 4, splitY - 6);
        g.fillRect(x + w - 14, y + 3, 4, splitY - 6);

        // Lower 40%: AIDA Safety-Yellow Die Holder Bar (#FACC15) with black T-slot cutouts
        fillNoisyRect(g, x, y + splitY, w, h - splitY, new Color(0xF5, 0xC4, 0x00), 5, 1102L);
        g.setColor(new Color(0xFF, 0xDF, 0x4D));
        g.fillRect(x, y + splitY, w, 2);
        g.setColor(new Color(0xB8, 0x8E, 0x00));
        g.fillRect(x, y + h - 2, w, 2);

        // Black T-slot clamping cutouts along the safety-yellow bar (just like aidapress.jpg)
        g.setColor(new Color(0x20, 0x23, 0x26));
        for (int tx = 6; tx < w - 8; tx += 12) {
            g.fillRect(x + tx, y + splitY + 4, 6, h - splitY - 8);
            g.fillRect(x + tx - 1, y + splitY + 4, 8, 3);
        }
    }

    private static void drawRollerAndToolSteel(Graphics2D g, int x, int y, int w, int h) {
        // Top half: Brushed silver-steel conveyor roller
        for (int py = 0; py < h / 2; py++) {
            double norm = Math.sin((py / (double) (h / 2)) * Math.PI);
            int c = clamp((int) (110 + 95 * norm));
            g.setColor(new Color(c, c + 4, clamp(c + 8)));
            g.fillRect(x, y + py, w, 1);
        }
        // Bottom half: Dark hardened tool steel stamping die block
        fillNoisyRect(g, x, y + h / 2, w, h / 2, new Color(0x24, 0x28, 0x2E), 5, 1201L);
        g.setColor(new Color(0x52, 0x5A, 0x64));
        g.drawRect(x + 1, y + h / 2 + 1, w - 3, h / 2 - 3);
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int variance, long seed) {
        Random rand = new Random(seed);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int delta = rand.nextInt(variance * 2 + 1) - variance;
                int r = clamp(base.getRed() + delta);
                int gr = clamp(base.getGreen() + delta);
                int b = clamp(base.getBlue() + delta);
                g.setColor(new Color(r, gr, b));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
