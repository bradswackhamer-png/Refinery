package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * High-Fidelity Asset Generator for the 2-Block Industrial Diesel Generator (1x2x2):
 * - Reference: Heavy containerized sound-attenuated industrial generator skid (Triton / Caterpillar / Cummins style)
 * - Base Skid: Heavy C-channel black steel skid chassis with forklift tine pockets and yellow tie-down tags
 * - Canopy: Off-white / light cream industrial powder-coated acoustic canopy (#E4E2DA)
 * - Operator Control Panel: Recessed panel with glowing digital LCD readout, red mushroom E-Stop button, and warning decals
 * - Branding & Badging: Bold "TRITON" power logo, "Blue Tech ECO Products" green leaf emblem, "TIER 4 FINAL" ribbon
 * - Doors & Hardware: Access doors with black industrial hinges, black recessed T-handle latches, and louvers
 * - Exhaust & Cooling: Radiator discharge louvers, alternator intake louvers, and top exhaust muffler with rain-cap
 */
public class DieselGeneratorAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/entity").mkdirs();
        new File(ASSETS + "textures/gui").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generateGeneratorAtlas();
        generateGeneratorGui();
        generateItemAndBlockTextures();
        generateJsonFiles();

        System.out.println("Diesel Generator assets generated successfully!");
    }

    /**
     * Generates the 256x256 entity texture atlas for the 3D monolithic Diesel Generator model.
     */
    public static void generateGeneratorAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark steel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x1B, 0x1D, 0x20), 3, 2001L);

        // 1. CANOPY FRONT LEFT (Control & Intake Side): (0, 0, 64, 48)
        drawCanopyFrontLeft(g, 0, 0, 64, 48);

        // 2. CANOPY FRONT RIGHT (Engine & Triton Logo Side): (64, 0, 64, 48)
        drawCanopyFrontRight(g, 64, 0, 64, 48);

        // 3. CANOPY BACK PANEL: (128, 0, 128, 48)
        drawCanopyBackPanel(g, 128, 0, 128, 48);

        // 4. CANOPY SIDES: Left Intake (0, 48, 64, 48) & Right Radiator (64, 48, 64, 48)
        drawCanopyLeftIntake(g, 0, 48, 64, 48);
        drawCanopyRightRadiator(g, 64, 48, 64, 48);

        // 5. CANOPY ROOF PANELS: (128, 48, 128, 48)
        drawCanopyRoof(g, 128, 48, 128, 48);

        // 6. BASE SKID FRONT / REAR BEAM (with Forklift Pockets): (0, 96, 128, 24)
        drawBaseSkidBeam(g, 0, 96, 128, 24);

        // 7. BASE SKID END CAPS: (128, 96, 64, 24)
        drawBaseSkidEndCaps(g, 128, 96, 64, 24);

        // 8. BASE SUB-TANK & BOTTOM SKID PLATES: (192, 96, 64, 24)
        drawSubTankPlates(g, 192, 96, 64, 24);

        // 9. RECESSED CONTROL PANEL DETAIL (Full HD): (0, 120, 64, 48)
        drawControlPanelDetail(g, 0, 120, 64, 48);

        // 10. EXHAUST MUFFLER & RAIN-CAP FLAPPER: (64, 120, 48, 48)
        drawExhaustMuffler(g, 64, 120, 48, 48);

        // 11. ELECTRICAL JUNCTION BOX & 480V HAZARD DECAL: (112, 120, 48, 48)
        drawJunctionBox(g, 112, 120, 48, 48);

        // 12. HARDWARE: Black Hinges, T-Handles & Lift Lugs: (160, 120, 96, 48)
        drawHardwareAtlas(g, 160, 120, 96, 48);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/diesel_generator.png"));
    }

    private static void drawCanopyFrontLeft(Graphics2D g, int x, int y, int w, int h) {
        // Off-white / light cream acoustic powder-coat (#E4E2DA)
        Color canopyBase = new Color(0xE4, 0xE2, 0xDA);
        fillNoisyRect(g, x, y, w, h, canopyBase, 3, 2010L);

        // Outer beveled frame
        g.setColor(new Color(0xF2, 0xF0, 0xE8));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0xC2, 0xBE, 0xB4));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

        // Door 1: Left Control Door (x+3, y+4, w: 26, h: 40)
        drawDoorPanel(g, x + 3, y + 4, 26, 40, true);

        // Control Panel Window (x+6, y+7, w: 18, h: 14)
        g.setColor(new Color(0x23, 0x26, 0x2A));
        g.fillRect(x + 6, y + 7, 18, 14);
        g.setColor(new Color(0x40, 0x45, 0x4D));
        g.drawRect(x + 6, y + 7, 17, 13);
        // LCD Glow (cyan/green)
        g.setColor(new Color(0x10, 0x4E, 0x5B));
        g.fillRect(x + 8, y + 9, 10, 6);
        g.setColor(new Color(0x22, 0xD3, 0xEE));
        g.drawLine(x + 9, y + 10, x + 16, y + 10);
        g.drawLine(x + 9, y + 12, x + 14, y + 12);
        // Emergency Stop Pushbutton (Red Mushroom with yellow circle)
        g.setColor(new Color(0xCA, 0x8A, 0x04));
        g.fillOval(x + 19, y + 9, 4, 4);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillOval(x + 20, y + 10, 2, 2);

        // Lower Door 1 Louvers
        drawLouvers(g, x + 7, y + 26, 18, 10, 4);

        // Door 2: Center Left Door (x+31, y+4, w: 30, h: 40)
        drawDoorPanel(g, x + 31, y + 4, 30, 40, false);
        // Blue Tech ECO Products Badge
        g.setColor(new Color(0x25, 0x63, 0xEB));
        g.fillRect(x + 36, y + 8, 18, 6);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 37, y + 9, 4, 4); // Green leaf icon
        g.setColor(Color.WHITE);
        g.drawLine(x + 43, y + 10, x + 51, y + 10);
        g.drawLine(x + 43, y + 12, x + 49, y + 12);

        // Upper & Lower Louvers on Door 2
        drawLouvers(g, x + 35, y + 16, 20, 8, 3);
        drawLouvers(g, x + 35, y + 28, 20, 8, 3);
    }

    private static void drawCanopyFrontRight(Graphics2D g, int x, int y, int w, int h) {
        Color canopyBase = new Color(0xE4, 0xE2, 0xDA);
        fillNoisyRect(g, x, y, w, h, canopyBase, 3, 2011L);

        // Outer beveled frame
        g.setColor(new Color(0xF2, 0xF0, 0xE8));
        g.drawLine(x, y, x + w - 1, y);
        g.setColor(new Color(0xC2, 0xBE, 0xB4));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

        // Door 3: Center Right Door (x+2, y+4, w: 26, h: 40)
        drawDoorPanel(g, x + 2, y + 4, 26, 40, false);
        drawLouvers(g, x + 6, y + 16, 18, 16, 5);

        // Door 4: Engine / Alternator Right Door (x+30, y+4, w: 31, h: 40)
        drawDoorPanel(g, x + 30, y + 4, 31, 40, false);

        // TRITON Brand Logo (Bold Black with stylized trident emblem)
        g.setColor(new Color(0x18, 0x1A, 0x1D));
        // Trident Emblem
        g.drawLine(x + 36, y + 8, x + 36, y + 14);
        g.drawLine(x + 34, y + 8, x + 38, y + 8);
        g.drawLine(x + 34, y + 8, x + 34, y + 11);
        g.drawLine(x + 38, y + 8, x + 38, y + 11);
        // TRITON Letters
        g.fillRect(x + 41, y + 9, 18, 4);
        g.setColor(new Color(0xE4, 0xE2, 0xDA));
        g.drawLine(x + 44, y + 10, x + 44, y + 12);
        g.drawLine(x + 48, y + 10, x + 48, y + 12);
        g.drawLine(x + 52, y + 10, x + 52, y + 12);

        // "TIER 4 FINAL" Green/Silver Eco Ribbon Badge
        g.setColor(new Color(0x4B, 0x55, 0x63));
        g.fillRoundRect(x + 40, y + 16, 18, 6, 2, 2);
        g.setColor(new Color(0x16, 0xA3, 0x4A));
        g.fillRect(x + 42, y + 17, 14, 4);
        g.setColor(Color.WHITE);
        g.drawLine(x + 44, y + 19, x + 53, y + 19);
    }

    private static void drawCanopyBackPanel(Graphics2D g, int x, int y, int w, int h) {
        Color canopyBase = new Color(0xE0, 0xDE, 0xD6);
        fillNoisyRect(g, x, y, w, h, canopyBase, 3, 2012L);

        // Seams and maintenance panels
        g.setColor(new Color(0xB8, 0xB4, 0xAA));
        g.drawLine(x + w / 2, y + 3, x + w / 2, y + h - 3);

        // Left rear service door
        drawDoorPanel(g, x + 8, y + 5, 48, 38, false);
        // Right rear service door
        drawDoorPanel(g, x + 68, y + 5, 48, 38, false);

        // High Voltage Electrical Output Box Cutout area (left rear)
        g.setColor(new Color(0x1E, 0x20, 0x23));
        g.fillRect(x + 16, y + 14, 28, 20);
        g.setColor(new Color(0x48, 0x4E, 0x56));
        g.drawRect(x + 16, y + 14, 27, 19);

        // Yellow Warning Triangle / Flash
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillPolygon(new int[]{x + 29, x + 24, x + 34}, new int[]{y + 18, y + 26, y + 26}, 3);
        g.setColor(Color.BLACK);
        g.drawLine(x + 29, y + 21, x + 29, y + 23);
        g.fillRect(x + 29, y + 25, 1, 1);

        // Diesel Fuel Inlet Flap (right rear)
        g.setColor(new Color(0x16, 0x65, 0x34)); // Dark green diesel cap
        g.fillOval(x + 86, y + 20, 10, 10);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.drawOval(x + 86, y + 20, 9, 9);
    }

    private static void drawCanopyLeftIntake(Graphics2D g, int x, int y, int w, int h) {
        Color canopyBase = new Color(0xDC, 0xDA, 0xD2);
        fillNoisyRect(g, x, y, w, h, canopyBase, 3, 2013L);

        // Alternator end large intake louver grille
        g.setColor(new Color(0x2B, 0x2E, 0x33));
        g.fillRect(x + 6, y + 8, w - 12, h - 16);
        g.setColor(new Color(0x56, 0x5B, 0x64));
        g.drawRect(x + 6, y + 8, w - 13, h - 17);

        // Fine wire mesh / horizontal louvers
        drawLouvers(g, x + 8, y + 10, w - 16, h - 20, 10);
    }

    private static void drawCanopyRightRadiator(Graphics2D g, int x, int y, int w, int h) {
        Color canopyBase = new Color(0xDC, 0xDA, 0xD2);
        fillNoisyRect(g, x, y, w, h, canopyBase, 3, 2014L);

        // Radiator discharge cooling louvers (wide industrial slats)
        g.setColor(new Color(0x22, 0x24, 0x28));
        g.fillRect(x + 6, y + 6, w - 12, h - 12);
        g.setColor(new Color(0x4C, 0x51, 0x5A));
        g.drawRect(x + 6, y + 6, w - 13, h - 13);

        drawLouvers(g, x + 8, y + 8, w - 16, h - 16, 12);

        // Top coolant fill access hatch
        g.setColor(new Color(0xC4, 0xC0, 0xB6));
        g.fillRect(x + 22, y + 1, 18, 4);
        g.setColor(new Color(0x8A, 0x86, 0x7E));
        g.drawRect(x + 22, y + 1, 17, 3);
    }

    private static void drawCanopyRoof(Graphics2D g, int x, int y, int w, int h) {
        Color roofBase = new Color(0xEB, 0xE9, 0xE2);
        fillNoisyRect(g, x, y, w, h, roofBase, 3, 2015L);

        // Longitudinal center rain drip-edge seam
        g.setColor(new Color(0xC0, 0xBC, 0xB2));
        g.drawLine(x, y + h / 2, x + w - 1, y + h / 2);
        g.setColor(new Color(0xF6, 0xF4, 0xEE));
        g.drawLine(x, y + h / 2 - 1, x + w - 1, y + h / 2 - 1);

        // Exhaust Stack Collar Cutout (right side)
        int ex = x + 96;
        int ey = y + 14;
        g.setColor(new Color(0x1C, 0x1E, 0x22));
        g.fillOval(ex - 6, ey - 6, 16, 16);
        g.setColor(new Color(0x40, 0x44, 0x4C));
        g.drawOval(ex - 6, ey - 6, 15, 15);
        // Bolted flange
        g.setColor(new Color(0x6A, 0x70, 0x7C));
        for (int i = 0; i < 6; i++) {
            double angle = (i / 6.0) * Math.PI * 2;
            int bx = (int) (ex + 2 + Math.cos(angle) * 6);
            int by = (int) (ey + 2 + Math.sin(angle) * 6);
            g.fillRect(bx, by, 1, 1);
        }

        // 4 Roof Crane Lifting Eye Brackets
        int[] lx = {x + 12, x + 50, x + 76, x + 114};
        for (int px : lx) {
            g.setColor(new Color(0x38, 0x3C, 0x44));
            g.fillRect(px, y + 6, 8, 4);
            g.setColor(new Color(0x65, 0x6B, 0x76));
            g.drawRect(px, y + 6, 7, 3);
        }
    }

    private static void drawBaseSkidBeam(Graphics2D g, int x, int y, int w, int h) {
        // Heavy C-Channel Black Steel Chassis (#141618)
        fillNoisyRect(g, x, y, w, h, new Color(0x14, 0x16, 0x18), 3, 2016L);

        // Top flange highlight and bottom shadow
        g.setColor(new Color(0x2E, 0x32, 0x38));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + 1, x + w - 1, y + 1);
        g.setColor(new Color(0x0C, 0x0D, 0x0F));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // 2 Rectangular Forklift Tine Pockets
        int[] pockets = {x + 18, x + 86};
        for (int px : pockets) {
            // Recessed cavity
            g.setColor(new Color(0x08, 0x09, 0x0A));
            g.fillRect(px, y + 6, 22, 12);
            // Steel sleeve frame
            g.setColor(new Color(0x3A, 0x3E, 0x46));
            g.drawRect(px, y + 6, 21, 11);

            // Yellow Safety / Tie-Down Tag on side
            g.setColor(new Color(0xEA, 0xB3, 0x08));
            g.fillRect(px - 5, y + 10, 3, 5);
        }
    }

    private static void drawBaseSkidEndCaps(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x12, 0x14, 0x16), 3, 2017L);
        g.setColor(new Color(0x2C, 0x30, 0x36));
        g.drawLine(x, y, x + w - 1, y);
        // Lifting shackle holes
        g.setColor(new Color(0x0A, 0x0B, 0x0D));
        g.fillOval(x + 12, y + 6, 12, 12);
        g.fillOval(x + 40, y + 6, 12, 12);
        g.setColor(new Color(0x44, 0x4A, 0x54));
        g.drawOval(x + 12, y + 6, 11, 11);
        g.drawOval(x + 40, y + 6, 11, 11);
    }

    private static void drawSubTankPlates(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x18, 0x1A, 0x1D), 3, 2018L);
        // Diamond plate / tread grip texture
        g.setColor(new Color(0x28, 0x2C, 0x32));
        for (int py = 2; py < h - 2; py += 3) {
            for (int px = 2; px < w - 2; px += 4) {
                g.drawLine(x + px, y + py, x + px + 1, y + py + 1);
            }
        }
    }

    private static void drawControlPanelDetail(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x16, 0x18, 0x1B), 2, 2019L);
        g.setColor(new Color(0x3B, 0x40, 0x48));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Digital LCD Screen
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fillRect(x + 8, y + 6, 48, 20);
        g.setColor(new Color(0x22, 0xD3, 0xEE));
        g.drawRect(x + 8, y + 6, 47, 19);

        // Cyan Glowing Text Readouts
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        // "480 V   60 Hz"
        g.drawLine(x + 12, y + 10, x + 26, y + 10);
        g.drawLine(x + 32, y + 10, x + 46, y + 10);
        // "1800 RPM  256 kW"
        g.drawLine(x + 12, y + 14, x + 28, y + 14);
        g.drawLine(x + 34, y + 14, x + 50, y + 14);
        // Status bar graph
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 12, y + 18, 32, 4);

        // Pushbuttons (Green Start, Red Stop, Amber Auto)
        g.setColor(new Color(0x16, 0xA3, 0x4A));
        g.fillOval(x + 12, y + 32, 6, 6);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillOval(x + 22, y + 32, 6, 6);
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillOval(x + 32, y + 32, 6, 6);

        // Big Red Mushroom E-Stop Button
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillOval(x + 46, y + 30, 10, 10);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillOval(x + 48, y + 32, 6, 6);
    }

    private static void drawExhaustMuffler(Graphics2D g, int x, int y, int w, int h) {
        // High-temperature heat-resistant matte black / dark cast steel
        fillNoisyRect(g, x, y, w, h, new Color(0x18, 0x19, 0x1C), 4, 2020L);

        // Cylindrical gradient
        for (int py = 0; py < h; py++) {
            double norm = Math.sin((py / (double) h) * Math.PI);
            int c = clamp((int) (24 + 40 * norm));
            g.setColor(new Color(c, c, c + 2));
            g.fillRect(x, y + py, w, 1);
        }

        // Clamp band & rain-cap flapper hinge
        g.setColor(new Color(0x50, 0x55, 0x60));
        g.fillRect(x, y + 4, w, 3);
        g.fillRect(x, y + h - 6, w, 3);
    }

    private static void drawJunctionBox(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2C, 0x32), 3, 2021L);
        g.setColor(new Color(0x4C, 0x52, 0x5C));
        g.drawRect(x, y, w - 1, h - 1);

        // Safety-Yellow 480V High Voltage Hazard Decal
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillRect(x + 8, y + 8, w - 16, h - 16);
        g.setColor(Color.BLACK);
        g.drawRect(x + 8, y + 8, w - 17, h - 17);
        // Lightning bolt icon
        g.setColor(Color.BLACK);
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.drawLine(cx + 2, cy - 6, cx - 3, cy);
        g.drawLine(cx - 3, cy, cx + 1, cy);
        g.drawLine(cx + 1, cy, cx - 2, cy + 6);
    }

    private static void drawHardwareAtlas(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1A, 0x1C, 0x1E), 2, 2022L);

        // Black Industrial Hinges (recessed with pin)
        for (int i = 0; i < 4; i++) {
            int hx = x + 4 + i * 20;
            g.setColor(new Color(0x10, 0x11, 0x13));
            g.fillRect(hx, y + 4, 14, 18);
            g.setColor(new Color(0x35, 0x38, 0x40));
            g.drawRect(hx, y + 4, 13, 17);
            g.setColor(new Color(0x55, 0x5C, 0x66));
            g.fillRect(hx + 6, y + 2, 2, 22); // Center hinge pin
        }

        // Black Recessed T-Handles & Paddle Latches
        for (int i = 0; i < 4; i++) {
            int tx = x + 4 + i * 20;
            g.setColor(new Color(0x0C, 0x0D, 0x0E));
            g.fillRoundRect(tx, y + 26, 14, 16, 2, 2);
            g.setColor(new Color(0x40, 0x44, 0x4C));
            g.drawRoundRect(tx, y + 26, 13, 15, 2, 2);
            g.setColor(new Color(0x22, 0x24, 0x28));
            g.fillRect(tx + 4, y + 30, 6, 8);
        }
    }

    private static void drawDoorPanel(Graphics2D g, int x, int y, int w, int h, boolean hasHandle) {
        // Recessed door seam
        g.setColor(new Color(0xB4, 0xB0, 0xA6));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x9E, 0x9A, 0x90));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Black Hinges on Left Edge
        g.setColor(new Color(0x16, 0x18, 0x1A));
        g.fillRect(x - 1, y + 4, 3, 5);
        g.fillRect(x - 1, y + h - 9, 3, 5);

        // Black Recessed Handle on Right Edge
        if (hasHandle) {
            g.setColor(new Color(0x14, 0x16, 0x18));
            g.fillRect(x + w - 6, y + h / 2 - 4, 4, 8);
            g.setColor(new Color(0x36, 0x3A, 0x42));
            g.drawRect(x + w - 6, y + h / 2 - 4, 3, 7);
        }
    }

    private static void drawLouvers(Graphics2D g, int x, int y, int w, int h, int count) {
        g.setColor(new Color(0x1E, 0x20, 0x24));
        g.fillRect(x, y, w, h);

        int step = Math.max(2, h / count);
        for (int ly = y + 1; ly < y + h - 1; ly += step) {
            g.setColor(new Color(0x5A, 0x60, 0x6B));
            g.drawLine(x + 1, ly, x + w - 2, ly);
            g.setColor(new Color(0x10, 0x11, 0x13));
            g.drawLine(x + 1, ly + 1, x + w - 2, ly + 1);
        }
    }

    /**
     * Generates modern 256x256 GUI background for the Diesel Generator.
     */
    public static void generateGeneratorGui() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 176x166 Standard GUI Frame
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0xC6, 0xC6, 0xC6), 2, 2030L);
        g.setColor(Color.WHITE);
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 165);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.drawLine(0, 165, 175, 165);
        g.drawLine(175, 0, 175, 165);

        // Title Header Bar: "Diesel Generator" (#374151)
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.fillRect(8, 6, 160, 10);

        // 1. Diesel Tank Gauge Frame (x: 35, y: 20, w: 18, h: 50)
        drawSlotFrame(g, 35, 20, 18, 50);
        g.setColor(new Color(0x11, 0x18, 0x27));
        g.fillRect(36, 21, 16, 48);

        // Bucket Input Slot (x: 12, y: 24, w: 18, h: 18)
        drawSlot(g, 12, 24);
        // Bucket Output Slot (x: 12, y: 50, w: 18, h: 18)
        drawSlot(g, 12, 50);

        // Downward fuel transfer arrow between bucket slots
        g.setColor(new Color(0x6B, 0x72, 0x80));
        g.drawLine(20, 44, 20, 47);
        g.drawLine(18, 46, 20, 48);
        g.drawLine(22, 46, 20, 48);

        // 2. Central Industrial Digital Monitor Frame (x: 58, y: 20, w: 60, h: 50)
        g.setColor(new Color(0x1F, 0x29, 0x37));
        g.fillRect(58, 20, 60, 50);
        g.setColor(new Color(0x11, 0x18, 0x27));
        g.fillRect(60, 22, 56, 46);
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.drawRect(58, 20, 59, 49);

        // 3. Energy Buffer Gauge Frame (x: 124, y: 20, w: 18, h: 50)
        drawSlotFrame(g, 124, 20, 18, 50);
        g.setColor(new Color(0x1C, 0x19, 0x17));
        g.fillRect(125, 21, 16, 48);

        // Redstone Mode Button Frame (x: 147, y: 35, w: 20, h: 20)
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.fillRect(147, 35, 20, 20);
        g.setColor(new Color(0x9C, 0xA3, 0xAF));
        g.drawRect(147, 35, 19, 19);

        // Player Inventory Slots (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // Hotbar Slots (9 slots at y: 142)
        for (int col = 0; col < 9; col++) {
            drawSlot(g, 7 + col * 18, 141);
        }

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/gui/diesel_generator.png"));
    }

    private static void drawSlot(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + 17, y);
        g.drawLine(x, y, x, y + 17);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(x + 1, y + 1, 16, 16);
        g.setColor(Color.WHITE);
        g.drawLine(x + 1, y + 17, x + 17, y + 17);
        g.drawLine(x + 17, y + 1, x + 17, y + 17);
    }

    private static void drawSlotFrame(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(Color.WHITE);
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
    }

    /**
     * Generates 16x16 pixel textures for block and item representations.
     */
    public static void generateItemAndBlockTextures() throws Exception {
        BufferedImage itemImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = itemImg.createGraphics();

        // 16x16 Pixel Art of the Triton Industrial Diesel Generator:
        // Top row: Exhaust stack & rain cap
        g.setColor(new Color(0x27, 0x2A, 0x30));
        g.fillRect(11, 0, 2, 3);
        g.setColor(new Color(0x4B, 0x51, 0x5D));
        g.drawLine(10, 0, 13, 0);

        // Main Canopy (y: 3..11, x: 1..14) - Off-white / cream
        g.setColor(new Color(0xE4, 0xE2, 0xDA));
        g.fillRect(1, 3, 14, 9);
        g.setColor(new Color(0xF4, 0xF2, 0xEB));
        g.drawLine(1, 3, 14, 3);

        // Control Panel LCD (cyan) on left
        g.setColor(new Color(0x1F, 0x29, 0x37));
        g.fillRect(2, 5, 4, 3);
        g.setColor(new Color(0x22, 0xD3, 0xEE));
        g.fillRect(3, 6, 2, 1);

        // Red E-Stop dot
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(6, 6, 1, 1);

        // Door seams & louvers
        g.setColor(new Color(0x27, 0x2A, 0x30));
        g.drawLine(7, 4, 7, 11);
        g.drawLine(3, 9, 5, 9);
        g.drawLine(9, 6, 13, 6);
        g.drawLine(9, 8, 13, 8);

        // Heavy Skid Base (y: 12..15, x: 0..15) - Black steel
        g.setColor(new Color(0x16, 0x18, 0x1B));
        g.fillRect(0, 12, 16, 4);
        g.setColor(new Color(0x32, 0x37, 0x3E));
        g.drawLine(0, 12, 15, 12);

        // Forklift pockets (black hollows)
        g.setColor(Color.BLACK);
        g.fillRect(3, 13, 2, 2);
        g.fillRect(11, 13, 2, 2);
        // Yellow safety tabs
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillRect(2, 13, 1, 2);
        g.fillRect(13, 13, 1, 2);

        g.dispose();
        ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/item/diesel_generator.png"));
        ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/block/diesel_generator.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // 1. Mandatory MC 26.3 item definition: assets/refinery/items/diesel_generator.json
        write(ASSETS + "items/diesel_generator.json",
                """
                {
                  "model": {
                    "type": "minecraft:model",
                    "model": "refinery:item/diesel_generator"
                  }
                }
                """);

        // 2. Item model: assets/refinery/models/item/diesel_generator.json
        write(ASSETS + "models/item/diesel_generator.json",
                """
                {
                  "parent": "minecraft:item/generated",
                  "textures": {
                    "layer0": "refinery:item/diesel_generator"
                  }
                }
                """);

        // 3. Block model: assets/refinery/models/block/diesel_generator.json
        write(ASSETS + "models/block/diesel_generator.json",
                """
                {
                  "parent": "minecraft:block/cube_all",
                  "textures": {
                    "all": "refinery:block/diesel_generator"
                  }
                }
                """);

        // 4. Blockstate: assets/refinery/blockstates/diesel_generator.json
        write(ASSETS + "blockstates/diesel_generator.json",
                """
                {
                  "variants": {
                    "": {
                      "model": "refinery:block/diesel_generator"
                    }
                  }
                }
                """);

        // 5. Loot Table: data/refinery/loot_table/blocks/diesel_generator.json
        write(DATA + "loot_table/blocks/diesel_generator.json",
                """
                {
                  "type": "minecraft:block",
                  "pools": [
                    {
                      "bonus_rolls": 0.0,
                      "conditions": [
                        {
                          "condition": "minecraft:survives_explosion"
                        }
                      ],
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "name": "refinery:diesel_generator"
                        }
                      ],
                      "rolls": 1.0
                    }
                  ]
                }
                """);

        // 6. Crafting Recipe: data/refinery/recipe/diesel_generator.json
        write(DATA + "recipe/diesel_generator.json",
                """
                {
                  "type": "minecraft:crafting_shaped",
                  "pattern": [
                    "PSP",
                    "ECE",
                    "BMB"
                  ],
                  "key": {
                    "P": "#c:plates/steel",
                    "S": "refinery:steel_boiler_casing",
                    "E": "minecraft:piston",
                    "C": "minecraft:redstone_block",
                    "B": "refinery:reinforced_casing",
                    "M": "#c:plates/copper"
                  },
                  "result": {
                    "id": "refinery:diesel_generator",
                    "count": 1
                  }
                }
                """);
    }

    private static void write(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content.trim() + "\n");
        }
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
