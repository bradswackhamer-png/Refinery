package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Procedural Asset Generator for the Heavy Industrial Pit Quarry:
 * Generates:
 * 1. 256x256 Entity Model Texture Atlas (CAT Yellow enamel, lattice steel, winch cable, floodlights, beacon, chevron belts)
 * 2. 256x256 Industrial GUI texture (Energy bar, radius controls, remaining blocks display, inventory slots)
 * 3. 16x16 Block and Item textures (controller, casing, frame)
 */
public class QuarryAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateGuiTexture();
        generateBlockAndItemTextures();
        System.out.println("Quarry entity atlas, GUI, and block/item textures generated successfully!");
    }

    // =========================================================================
    // 1. ENTITY TEXTURE ATLAS (256x256)
    // =========================================================================
    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Dark industrial background
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x23, 0x27, 0x30), 3, 1001L);

        // 1. Heavy Equipment CAT Yellow Enamel Plates (0, 0, 96, 48)
        drawEnamelPlates(g, 0, 0, 96, 48, new Color(0xD9, 0x82, 0x1E));

        // 2. Structural Steel Lattice & Gantry Beams (96, 0, 64, 48)
        drawLatticeSteel(g, 96, 0, 64, 48);

        // 3. Winch Drum & Braided Steel Wire Cable (160, 0, 48, 48)
        drawWinchDrum(g, 160, 0, 48, 48);

        // 4. Diamond Treadplate Skid Base (208, 0, 48, 48)
        drawTreadplate(g, 208, 0, 48, 48);

        // 5. Operator Cabin Front Console & Joysticks (0, 48, 64, 48)
        drawCabinConsole(g, 0, 48, 64, 48);

        // 6. Operator Cabin Enclosure & Roof Awning (64, 48, 48, 48)
        drawEnamelPlates(g, 64, 48, 48, 48, new Color(0xC7, 0x76, 0x16));

        // 7. Tinted Safety Glass Window (112, 48, 48, 48)
        drawSafetyGlass(g, 112, 48, 48, 48);

        // 8. Heavy Ballast / Counterweight Box (160, 48, 48, 48)
        drawCounterweight(g, 160, 48, 48, 48);

        // 9. Radiator Cooling Fins & Turbo Louvers (208, 48, 48, 48)
        drawRadiatorFins(g, 208, 48, 48, 48);

        // 10. High-Intensity Halogen Work Floodlight Lens (0, 96, 48, 48)
        drawFloodlightLens(g, 0, 96, 48, 48);

        // 11. Floodlight Lamp Housing Steel (48, 96, 48, 48)
        drawFloodlightHousing(g, 48, 96, 48, 48);

        // 12. Warning Amber Strobe Beacon (ON & OFF) (96, 96, 48, 48)
        drawStrobeBeacons(g, 96, 96, 48, 48);

        // 13. Yellow & Black 45-Degree Hazard Safety Stripes (144, 96, 64, 48)
        drawHazardStripes(g, 144, 96, 64, 48);

        // 14. Industrial Chevron Rubber Conveyor Belt (208, 96, 48, 48)
        drawChevronBelt(g, 208, 96, 48, 48);

        // 15. Conveyor Steel Guide Skirt & Support Rollers (0, 144, 64, 48)
        drawConveyorSkirt(g, 0, 144, 64, 48);

        // 16. Chrome Hydraulic Rams & Cylinder Rods (64, 144, 48, 48)
        drawChromeCylinders(g, 64, 144, 48, 48);

        // 17. Electric Motor & Gearbox Enclosure (112, 144, 64, 48)
        drawMotorGearbox(g, 112, 144, 64, 48);

        // 18. Heavy Crawler Tracks / Base Outrigger Pads (176, 144, 80, 48)
        drawCrawlerTracks(g, 176, 144, 80, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/quarry.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated quarry entity atlas at: " + out.getAbsolutePath());
    }

    private static void drawEnamelPlates(Graphics2D g, int x, int y, int w, int h, Color baseColor) {
        fillNoisyRect(g, x, y, w, h, baseColor, 4, 201L);
        // Highlight top & left edges
        g.setColor(new Color(Math.min(255, baseColor.getRed() + 35), Math.min(255, baseColor.getGreen() + 30), Math.min(255, baseColor.getBlue() + 15)));
        g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, y + h - 2);

        // Shadow bottom & right edges
        g.setColor(new Color(Math.max(0, baseColor.getRed() - 40), Math.max(0, baseColor.getGreen() - 35), Math.max(0, baseColor.getBlue() - 15)));
        g.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
        g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);

        // Machine structural rivets
        g.setColor(new Color(0x3E, 0x22, 0x08));
        for (int bx = x + 4; bx < x + w - 4; bx += 10) {
            drawRivet(g, bx, y + 3);
            drawRivet(g, bx, y + h - 5);
        }
        for (int by = y + 8; by < y + h - 8; by += 10) {
            drawRivet(g, x + 3, by);
            drawRivet(g, x + w - 5, by);
        }
    }

    private static void drawRivet(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x2B, 0x18, 0x04));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0xFA, 0xBA, 0x5C));
        g.fillRect(x, y, 1, 1);
    }

    private static void drawLatticeSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2C, 0x36), 3, 301L);
        // Cross-lattice girders
        g.setColor(new Color(0x40, 0x47, 0x56));
        for (int i = 0; i < w; i += 16) {
            g.drawLine(x + i, y, x + i + 16, y + h);
            g.drawLine(x + i + 16, y, x + i, y + h);
            g.drawRect(x + i, y, 16, h - 1);
        }
        // Heavy steel gusset corner plates
        g.setColor(new Color(0x1B, 0x1E, 0x26));
        for (int i = 0; i < w; i += 16) {
            g.fillPolygon(new int[]{x + i, x + i + 4, x + i}, new int[]{y, y, y + 4}, 3);
            g.fillPolygon(new int[]{x + i, x + i + 4, x + i}, new int[]{y + h - 1, y + h - 1, y + h - 5}, 3);
        }
    }

    private static void drawWinchDrum(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3B, 0x47), 3, 401L);
        // Braided steel cable wrapped around drum
        for (int py = y + 2; py < y + h - 2; py += 3) {
            g.setColor(new Color(0x8C, 0x94, 0xA3));
            g.drawLine(x + 2, py, x + w - 3, py);
            g.setColor(new Color(0x56, 0x5D, 0x6B));
            g.drawLine(x + 2, py + 1, x + w - 3, py + 1);
            g.setColor(new Color(0x25, 0x29, 0x33));
            g.drawLine(x + 2, py + 2, x + w - 3, py + 2);
        }
        // Outer drum flanges
        g.setColor(new Color(0xC7, 0x76, 0x16));
        g.fillRect(x, y, 3, h);
        g.fillRect(x + w - 3, y, 3, h);
    }

    private static void drawTreadplate(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2E, 0x33, 0x3E), 3, 501L);
        g.setColor(new Color(0x48, 0x51, 0x62));
        for (int py = y + 2; py < y + h; py += 4) {
            for (int px = x + 2; px < x + w; px += 4) {
                if ((px + py) % 8 == 0) {
                    g.drawLine(px, py, px + 2, py + 2);
                } else if ((px - py) % 8 == 0) {
                    g.drawLine(px, py + 2, px + 2, py);
                }
            }
        }
    }

    private static void drawCabinConsole(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1F, 0x24, 0x2E), 3, 601L);
        // Digital GPS Pit Excavation Screen
        g.setColor(new Color(0x0C, 0x12, 0x1C));
        g.fillRect(x + 4, y + 4, 34, 20);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.drawRect(x + 4, y + 4, 34, 20);
        // Display grid & pit outline
        g.setColor(new Color(0x02, 0x84, 0xC7));
        g.drawRect(x + 8, y + 7, 26, 14);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 10, y + 9, 22, 10);
        g.setColor(new Color(0x15, 0x80, 0x3D));
        g.drawRect(x + 13, y + 11, 16, 6);

        // Analog Dials & Status LED cluster
        g.setColor(new Color(0xF1, 0xF5, 0xF9));
        g.fillOval(x + 44, y + 5, 12, 12);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(x + 50, y + 11, x + 53, y + 8);

        // Twin operator joysticks & E-Stop
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.fillRect(x + 8, y + 30, 8, 8);
        g.fillRect(x + 24, y + 30, 8, 8);
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fillOval(x + 10, y + 32, 4, 4);
        g.fillOval(x + 26, y + 32, 4, 4);

        // Big Yellow/Red E-stop button
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 46, y + 28, 10, 10);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 48, y + 30, 6, 6);
    }

    private static void drawSafetyGlass(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1A, 0x3A, 0x48), 2, 701L);
        // Bright reflection streaks
        g.setColor(new Color(0x67, 0xE8, 0xF9, 140));
        g.drawLine(x + 6, y + 2, x + 2, y + 6);
        g.drawLine(x + w - 10, y + 4, x + 4, y + h - 10);
        g.drawLine(x + w - 6, y + 4, x + 6, y + h - 8);
        // Rubber frame gasket
        g.setColor(new Color(0x11, 0x14, 0x1A));
        g.drawRect(x, y, w - 1, h - 1);
        g.drawRect(x + 1, y + 1, w - 3, h - 3);
    }

    private static void drawCounterweight(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x38, 0x3E, 0x4A), 4, 801L);
        // Cast iron ballast blocks with dividing seams
        g.setColor(new Color(0x1F, 0x23, 0x2B));
        for (int py = y; py < y + h; py += 12) {
            g.drawLine(x, py, x + w - 1, py);
        }
        for (int px = x; px < x + w; px += 16) {
            g.drawLine(px, y, px, y + h - 1);
        }
        // Heavy lifting eyelets
        g.setColor(new Color(0xD9, 0x82, 0x1E));
        g.drawOval(x + 8, y + 4, 8, 8);
        g.drawOval(x + w - 16, y + 4, 8, 8);
    }

    private static void drawRadiatorFins(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x23, 0x28, 0x33), 3, 901L);
        // Vertical radiator fins
        for (int px = x + 2; px < x + w - 2; px += 2) {
            g.setColor(new Color(0x4B, 0x53, 0x63));
            g.drawLine(px, y + 2, px, y + h - 3);
            g.setColor(new Color(0x14, 0x17, 0x1E));
            g.drawLine(px + 1, y + 2, px + 1, y + h - 3);
        }
        // Frame
        g.setColor(new Color(0xD9, 0x82, 0x1E));
        g.drawRect(x, y, w - 1, h - 1);
    }

    private static void drawFloodlightLens(Graphics2D g, int x, int y, int w, int h) {
        // High-power halogen reflector optic
        fillNoisyRect(g, x, y, w, h, new Color(0xFE, 0xFD, 0xEB), 2, 1101L);
        g.setColor(new Color(0xFF, 0xF5, 0x9D));
        g.fillOval(x + 4, y + 4, w - 8, h - 8);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillOval(x + 10, y + 10, w - 20, h - 20);

        // Halogen bulb filament
        g.setColor(new Color(0xFB, 0xBF, 0x24));
        g.fillRect(x + w / 2 - 2, y + h / 2 - 4, 4, 8);

        // Glass lens facet grid
        g.setColor(new Color(0xFD, 0xBA, 0x74, 90));
        for (int i = x + 6; i < x + w - 6; i += 6) {
            g.drawLine(i, y + 4, i, y + h - 5);
        }
        for (int j = y + 6; j < y + h - 6; j += 6) {
            g.drawLine(x + 4, j, x + w - 5, j);
        }
    }

    private static void drawFloodlightHousing(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2B, 0x30, 0x3B), 3, 1201L);
        // Outer bezel ring
        g.setColor(new Color(0xD9, 0x82, 0x1E));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);
        g.setColor(new Color(0x18, 0x1B, 0x22));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        // Rear cooling heatsink fins
        g.setColor(new Color(0x40, 0x48, 0x58));
        for (int py = y + 6; py < y + h - 6; py += 4) {
            g.drawLine(x + 6, py, x + w - 7, py);
        }
    }

    private static void drawStrobeBeacons(Graphics2D g, int x, int y, int w, int h) {
        // Beacon ACTIVE (glowing bright amber/yellow) (x, y, 24, 24)
        fillNoisyRect(g, x, y, 24, 24, new Color(0xFF, 0x99, 0x00), 2, 1301L);
        g.setColor(new Color(0xFF, 0xD6, 0x00));
        g.fillOval(x + 3, y + 3, 18, 18);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillOval(x + 7, y + 7, 10, 10);

        // Beacon STANDBY (dark amber) (x + 24, y, 24, 24)
        fillNoisyRect(g, x + 24, y, 24, 24, new Color(0x66, 0x33, 0x00), 2, 1302L);
        g.setColor(new Color(0x8B, 0x45, 0x00));
        g.fillOval(x + 27, y + 3, 18, 18);
        g.setColor(new Color(0x4A, 0x25, 0x00));
        g.fillOval(x + 31, y + 7, 10, 10);

        // Mounting base stems (below beacons)
        fillNoisyRect(g, x, y + 24, 48, 24, new Color(0x2B, 0x2F, 0x3A), 3, 1303L);
        g.setColor(new Color(0x18, 0x1A, 0x20));
        g.drawRect(x + 1, y + 25, 46, 22);
    }

    private static void drawHazardStripes(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xFA, 0xCC, 0x15), 2, 1401L);
        g.setColor(new Color(0x18, 0x18, 0x1B));
        for (int sx = x - h; sx < x + w + h; sx += 8) {
            for (int lw = 0; lw < 4; lw++) {
                int x1 = sx + lw;
                int x2 = sx + h + lw;
                if ((x1 >= x && x1 < x + w) || (x2 >= x && x2 < x + w)) {
                    g.drawLine(Math.max(x, Math.min(x + w - 1, x1)), y,
                            Math.max(x, Math.min(x + w - 1, x2)), y + h - 1);
                }
            }
        }
        g.setColor(new Color(0x71, 0x3F, 0x12));
        g.drawRect(x, y, w - 1, h - 1);
    }

    private static void drawChevronBelt(Graphics2D g, int x, int y, int w, int h) {
        // Reinforced industrial rubber conveyor belt
        fillNoisyRect(g, x, y, w, h, new Color(0x1C, 0x1F, 0x24), 2, 1501L);
        // V-shaped chevron cleats
        g.setColor(new Color(0x38, 0x3E, 0x48));
        for (int py = y - 8; py < y + h + 8; py += 10) {
            int midX = x + w / 2;
            g.drawLine(midX, py, x + 4, py + 8);
            g.drawLine(midX, py + 1, x + 4, py + 9);
            g.drawLine(midX, py, x + w - 5, py + 8);
            g.drawLine(midX, py + 1, x + w - 5, py + 9);
        }
        // Belt outer edge borders
        g.setColor(new Color(0x0E, 0x10, 0x13));
        g.fillRect(x, y, 2, h);
        g.fillRect(x + w - 2, y, 2, h);
    }

    private static void drawConveyorSkirt(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xD9, 0x82, 0x1E), 3, 1601L);
        // Steel chute side guide rails
        g.setColor(new Color(0x2B, 0x2F, 0x3A));
        g.fillRect(x, y + h - 8, w, 8);
        // Rollers
        g.setColor(new Color(0x8C, 0x94, 0xA3));
        for (int rx = x + 6; rx < x + w - 6; rx += 14) {
            g.fillOval(rx, y + h - 7, 6, 6);
            g.setColor(new Color(0x3B, 0x41, 0x4D));
            g.drawOval(rx, y + h - 7, 6, 6);
        }
    }

    private static void drawChromeCylinders(Graphics2D g, int x, int y, int w, int h) {
        // Cylindrical gradient highlight on polished chrome shaft
        for (int px = 0; px < w; px++) {
            double factor = Math.sin((px / (double) w) * Math.PI);
            int val = 90 + (int) (155 * factor);
            g.setColor(new Color(val, val, Math.min(255, val + 10)));
            g.drawLine(x + px, y, x + px, y + h - 1);
        }
    }

    private static void drawMotorGearbox(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2C, 0x33, 0x3F), 3, 1701L);
        // Ribbed motor casing
        g.setColor(new Color(0x43, 0x4D, 0x5D));
        for (int px = x + 4; px < x + w - 4; px += 4) {
            g.drawLine(px, y + 2, px, y + h - 3);
        }
        // Gearbox end cap with bolts
        g.setColor(new Color(0x1E, 0x22, 0x2B));
        g.fillRect(x + w - 10, y, 10, h);
        g.setColor(new Color(0xD9, 0x82, 0x1E));
        g.fillOval(x + w - 8, y + h / 2 - 4, 8, 8);
    }

    private static void drawCrawlerTracks(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1F, 0x23, 0x2A), 4, 1801L);
        // Heavy steel track shoes / grouser bars
        for (int py = y + 2; py < y + h - 2; py += 5) {
            g.setColor(new Color(0x4A, 0x52, 0x62));
            g.fillRect(x + 2, py, w - 4, 3);
            g.setColor(new Color(0x11, 0x13, 0x17));
            g.drawLine(x + 2, py + 3, x + w - 3, py + 3);
        }
    }

    // =========================================================================
    // 2. INDUSTRIAL GUI TEXTURE (256x256)
    // =========================================================================
    public static void generateGuiTexture() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 256;
        int h = 196;

        // 1. Base Industrial Steel Panel (matching all other machines)
        drawBaseFrame(g, w, h, 6001L);

        // 2. Left Energy Storage Well (x: 9, y: 30, w: 14, h: 50) -> inner fill (10, 31, 12, 48)
        drawRecessedWell(g, 9, 30, 14, 50, new Color(0x19, 0x1E, 0x24));
        drawGaugeTicks(g, 10, 31, 12, 48, 5);

        // 3. Center-Left Excavation & Radius Controls Plate (x: 25, y: 30, w: 82, h: 50)
        drawPanelPlate(g, 25, 30, 82, 50);

        // Recessed button wells inside Excavation plate:
        // [-] at (28, 42, w: 16, h: 14)
        drawRecessedWell(g, 28, 42, 16, 14, new Color(0x1D, 0x21, 0x26));
        // [+] at (60, 42, w: 16, h: 14)
        drawRecessedWell(g, 60, 42, 16, 14, new Color(0x1D, 0x21, 0x26));
        // [R] at (86, 42, w: 16, h: 14)
        drawRecessedWell(g, 86, 42, 16, 14, new Color(0x1D, 0x21, 0x26));

        // 4. Fence Supply Plate (x: 109, y: 30, w: 32, h: 50)
        drawPanelPlate(g, 109, 30, 32, 50);
        // Fence slot well (Slot 9) at (116, 46)
        drawRecessedSlotWell(g, 116, 46);

        // 5. Right Manifest Telemetry Monitor (x: 143, y: 6, w: 107, h: 74)
        drawPanelPlate(g, 143, 6, 107, 74);
        // Inner CRT/LCD display screen (x: 146, y: 9, w: 101, h: 68)
        drawRecessedWell(g, 146, 9, 101, 68, new Color(0x0C, 0x12, 0x18));
        // CRT scanline pattern inside monitor
        g.setColor(new Color(0x10, 0x18, 0x20));
        for (int sy = 12; sy < 76; sy += 2) {
            g.drawLine(148, sy, 245, sy);
        }
        // 8 Recessed block slot wells inside the monitor (4 cols x 2 rows):
        // Col 0: 149, Col 1: 173, Col 2: 197, Col 3: 221
        // Row 0: 31, Row 1: 53
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                drawRecessedSlotWell(g, 149 + col * 24, 31 + row * 22);
            }
        }

        // 6. Machine Buffer Slots Plate & Wells (x: 44, y: 82, w: 168, h: 22)
        drawPanelPlate(g, 44, 82, 168, 22);
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, 47 + col * 18, 84);
        }

        // 6b. Throw-Back / Pit Spoil Filter Slot Plate (x: 216, y: 82, w: 26, h: 22)
        drawPanelPlate(g, 216, 82, 26, 22);
        drawRecessedSlotWell(g, 220, 84);
        g.setColor(new Color(0xF9, 0x73, 0x16));
        g.drawRect(220, 84, 17, 17);

        // 7. Player Inventory (y: 114) and Hotbar (y: 172)
        drawPlayerInventoryWellsCustom(g, 47, 114, 172);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/gui/quarry.png");
        out.getParentFile().mkdirs();
        ImageIO.write(gui, "PNG", out);
        System.out.println("Generated custom quarry GUI texture at: " + out.getAbsolutePath());
    }

    private static void drawBaseFrame(Graphics2D g, int w, int h, long seed) {
        // Base dark industrial steel panel
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

    private static void drawPlayerInventoryWellsCustom(Graphics2D g, int startX, int invWellY, int hotbarWellY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawRecessedSlotWell(g, startX + col * 18, invWellY + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawRecessedSlotWell(g, startX + col * 18, hotbarWellY);
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

    // =========================================================================
    // 3. 16x16 BLOCK & ITEM TEXTURES
    // =========================================================================
    public static void generateBlockAndItemTextures() throws Exception {
        // Controller Block Front (Inactive)
        BufferedImage ctrlFront = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = ctrlFront.createGraphics();
        fillNoisyRect(g1, 0, 0, 16, 16, new Color(0xD9, 0x82, 0x1E), 3, 3001L);
        g1.setColor(new Color(0x2B, 0x2F, 0x38));
        g1.drawRect(1, 1, 13, 13);
        // Inactive Screen
        g1.setColor(new Color(0x10, 0x14, 0x1C));
        g1.fillRect(3, 3, 10, 6);
        g1.setColor(new Color(0x38, 0xBD, 0xF8));
        g1.drawRect(3, 3, 10, 6);
        // Inactive status LED
        g1.setColor(new Color(0xDC, 0x26, 0x26));
        g1.fillRect(4, 11, 2, 2);
        g1.setColor(new Color(0x47, 0x55, 0x69));
        g1.fillRect(8, 11, 4, 2);
        g1.dispose();
        saveImg(ctrlFront, "assets/refinery/textures/block/quarry_controller_front.png");

        // Controller Block Front (Active)
        BufferedImage ctrlFrontActive = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = ctrlFrontActive.createGraphics();
        fillNoisyRect(g2, 0, 0, 16, 16, new Color(0xD9, 0x82, 0x1E), 3, 3002L);
        g2.setColor(new Color(0x2B, 0x2F, 0x38));
        g2.drawRect(1, 1, 13, 13);
        // Glowing Active Screen with pit grid
        g2.setColor(new Color(0x02, 0x84, 0xC7));
        g2.fillRect(3, 3, 10, 6);
        g2.setColor(new Color(0x38, 0xBD, 0xF8));
        g2.drawRect(3, 3, 10, 6);
        g2.setColor(new Color(0x22, 0xC5, 0x5E));
        g2.fillRect(5, 5, 6, 2);
        // Active status LED (Green)
        g2.setColor(new Color(0x22, 0xC5, 0x5E));
        g2.fillRect(4, 11, 2, 2);
        g2.setColor(new Color(0xFA, 0xCC, 0x15));
        g2.fillRect(8, 11, 4, 2);
        g2.dispose();
        saveImg(ctrlFrontActive, "assets/refinery/textures/block/quarry_controller_front_active.png");

        // Quarry Casing Block (16x16)
        BufferedImage casing = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = casing.createGraphics();
        fillNoisyRect(g3, 0, 0, 16, 16, new Color(0xD9, 0x82, 0x1E), 3, 3003L);
        g3.setColor(new Color(0xFA, 0xBA, 0x5C));
        g3.drawLine(0, 0, 15, 0);
        g3.drawLine(0, 0, 0, 15);
        g3.setColor(new Color(0x9E, 0x56, 0x0A));
        g3.drawLine(0, 15, 15, 15);
        g3.drawLine(15, 0, 15, 15);
        // Rivets
        g3.setColor(new Color(0x2B, 0x18, 0x04));
        g3.fillRect(2, 2, 2, 2);
        g3.fillRect(12, 2, 2, 2);
        g3.fillRect(2, 12, 2, 2);
        g3.fillRect(12, 12, 2, 2);
        g3.dispose();
        saveImg(casing, "assets/refinery/textures/block/quarry_casing.png");

        // Item icons:
        saveImg(ctrlFront, "assets/refinery/textures/item/quarry_controller.png");
        saveImg(casing, "assets/refinery/textures/item/quarry_casing.png");
    }

    private static void saveImg(BufferedImage img, String relPath) throws Exception {
        File out = new File("src/main/resources/" + relPath);
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Saved texture: " + out.getAbsolutePath());
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int variance, long seed) {
        Random rand = new Random(seed);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int d = rand.nextInt(variance * 2 + 1) - variance;
                int r = Math.max(0, Math.min(255, base.getRed() + d));
                int gr = Math.max(0, Math.min(255, base.getGreen() + d));
                int b = Math.max(0, Math.min(255, base.getBlue() + d));
                g.setColor(new Color(r, gr, b));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
    }
}
