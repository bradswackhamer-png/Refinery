package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates high-resolution 256x256 texture atlases for the 3D Monolithic
 * Electric Arc Melter (EAF) and Continuous Casting Press models.
 */
public class ArcCastingAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateArcMelterAtlas();
        generateCastingPressAtlas();
        System.out.println("Electric Arc Melter and Continuous Casting Press 256x256 entity texture atlases generated successfully!");
    }

    // =========================================================================
    // 1. ELECTRIC ARC MELTER ENTITY ATLAS (256x256)
    // =========================================================================
    public static void generateArcMelterAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Background base: Dark refractory gunmetal steel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x1E, 0x22, 0x29), 4, 5001L);

        // Row 1 (y: 0..48)
        // 1. EAF Cylindrical Shell & Water-Cooled Rib Panels (0, 0, 96, 48)
        drawFurnaceShell(g, 0, 0, 96, 48);
        // 2. Heavy Structural Rocker Trunnion Base & Skid (96, 0, 64, 48)
        drawTrunnionBase(g, 96, 0, 64, 48);
        // 3. Refractory Alumina-Silica Brick Lining & Launder (160, 0, 48, 48)
        drawRefractoryBrick(g, 160, 0, 48, 48);
        // 4. Graphite Carbon Electrode Column (208, 0, 48, 48)
        drawGraphiteElectrode(g, 208, 0, 48, 48);

        // Row 2 (y: 48..96)
        // 5. Swinging Water-Cooled Roof Dome & Delta Ring (0, 48, 64, 48)
        drawWaterCooledDome(g, 0, 48, 64, 48);
        // 6. Copper Busbars & High-Current Clamps (64, 48, 48, 48)
        drawCopperBusbars(g, 64, 48, 48, 48);
        // 7. High-Voltage Step-Down Transformer Housing (112, 48, 48, 48)
        drawTransformerHousing(g, 112, 48, 48, 48);
        // 8. Quartz Slag/Viewing Port & Bezel (160, 48, 48, 48)
        drawQuartzViewport(g, 160, 48, 48, 48);
        // 9. Mast Gantry Column & Hydraulic Lift Cylinder (208, 48, 48, 48)
        drawMastAndCylinder(g, 208, 48, 48, 48);

        // Row 3 (y: 96..144)
        // 10. Operator Control Console Front Faceplate (0, 96, 64, 48)
        drawMelterControlConsole(g, 0, 96, 64, 48);
        // 11. Active Electric Arc Plasma & Glowing Indicators (64, 96, 48, 48)
        drawArcPlasmaAndGlow(g, 64, 96, 48, 48);
        // 12. Top Charge Feed Hopper & Flanged Collars (112, 96, 48, 48)
        drawChargeHopper(g, 112, 96, 48, 48);
        // 13. Molten Metal Bath Surface (160, 96, 48, 48)
        drawMoltenBath(g, 160, 96, 48, 48);

        // Row 4 (y: 144..192)
        // 14. Control Console Enclosure Metal Casing (0, 144, 64, 48)
        drawDarkConsoleCasing(g, 0, 144, 64, 48);
        // 15. Yellow & Black Safety Hazard Stripes (64, 144, 48, 48)
        drawSafetyHazardStripes(g, 64, 144, 48, 48);
        // 16. Polished Chrome Rods & Metal Fittings (112, 144, 48, 48)
        drawChromeSteelTexture(g, 112, 144, 48, 48);
        // 17. Transformer Radiator Cooling Fin Banks (160, 144, 48, 48)
        drawRadiatorFinBank(g, 160, 144, 48, 48);
        // 18. Flowing Launder Molten Stream (208, 144, 48, 48)
        drawMoltenLaunderStream(g, 208, 144, 48, 48);

        // Row 5 (y: 192..240)
        // 19. Status Indicator ON (0, 192, 24, 24) & OFF (24, 192, 24, 24)
        drawStatusBeaconLenses(g, 0, 192);
        // 20. Heavy Foundation Baseplate Steel (48, 192, 64, 48)
        drawFoundationBaseplate(g, 48, 192, 64, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/arc_melter.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated Arc Melter atlas at: " + out.getAbsolutePath());
    }

    private static void drawFurnaceShell(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2E, 0x37), 4, 5101L);

        // Horizontal water-cooling jacket bands
        int[] bandYs = {y + 6, y + 20, y + 34};
        for (int by : bandYs) {
            g.setColor(new Color(0x16, 0x1A, 0x20));
            g.fillRect(x, by - 1, w, 6);
            g.setColor(new Color(0x3B, 0x44, 0x52));
            g.fillRect(x, by, w, 4);
            g.setColor(new Color(0x52, 0x5E, 0x70));
            g.drawLine(x, by, x + w - 1, by);
        }

        // Vertical heavy steel buckstay ribs
        for (int rx = x + 12; rx < x + w; rx += 24) {
            g.setColor(new Color(0x18, 0x1C, 0x22));
            g.fillRect(rx - 2, y, 5, h);
            g.setColor(new Color(0x3F, 0x48, 0x56));
            g.fillRect(rx - 1, y, 3, h);
            drawRivet(g, rx, y + 3);
            drawRivet(g, rx, y + 15);
            drawRivet(g, rx, y + 29);
            drawRivet(g, rx, y + 43);
        }

        // High-temperature orange/yellow hazard stripe at top rim
        for (int sx = x; sx < x + w; sx += 8) {
            g.setColor(new Color(0xD9, 0x77, 0x06));
            g.fillRect(sx, y, 4, 3);
            g.setColor(new Color(0x1E, 0x22, 0x29));
            g.fillRect(sx + 4, y, 4, 3);
        }
    }

    private static void drawTrunnionBase(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x20, 0x25, 0x2C), 4, 5201L);

        // Heavy I-beam flanges
        g.setColor(new Color(0x36, 0x3E, 0x4A));
        g.fillRect(x, y, w, 5);
        g.fillRect(x, y + h - 5, w, 5);
        g.setColor(new Color(0x52, 0x5D, 0x6E));
        g.drawLine(x, y, x + w - 1, y);

        // Curved rocker trunnion gear teeth markings
        g.setColor(new Color(0x14, 0x17, 0x1C));
        for (int tx = x + 6; tx < x + w - 6; tx += 6) {
            g.fillRect(tx, y + 14, 3, 8);
            g.setColor(new Color(0x47, 0x52, 0x61));
            g.fillRect(tx, y + 14, 2, 2);
            g.setColor(new Color(0x14, 0x17, 0x1C));
        }

        // Anchor bolts
        for (int bx = x + 8; bx < x + w - 4; bx += 14) {
            drawRivet(g, bx, y + h - 4);
        }
    }

    private static void drawRefractoryBrick(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x4A, 0x35, 0x2B), 5, 5301L);

        // Staggered firebrick mortar pattern
        g.setColor(new Color(0x2B, 0x1E, 0x18));
        for (int by = y; by < y + h; by += 6) {
            g.drawLine(x, by, x + w - 1, by);
            int offset = ((by - y) / 6) % 2 == 0 ? 0 : 6;
            for (int bx = x + offset; bx < x + w; bx += 12) {
                g.drawLine(bx, by, bx, Math.min(y + h - 1, by + 6));
            }
        }

        // Warm heat-baked highlight on brick tops
        g.setColor(new Color(0x63, 0x47, 0x39));
        for (int by = y + 1; by < y + h; by += 6) {
            g.drawLine(x + 1, by, x + w - 2, by);
        }
    }

    private static void drawGraphiteElectrode(Graphics2D g, int x, int y, int w, int h) {
        // Cylindrical dark carbon graphite texture with vertical shading
        for (int px = 0; px < w; px++) {
            double norm = Math.sin((px / (double) w) * Math.PI);
            int c = clamp((int) (22 + 34 * norm));
            g.setColor(new Color(c, c + 2, c + 5));
            g.fillRect(x + px, y, 1, h);
        }
        // Threaded graphite nipple joint lines
        g.setColor(new Color(0x11, 0x13, 0x17));
        g.fillRect(x, y + 14, w, 2);
        g.fillRect(x, y + 30, w, 2);

        // Glowing white-hot / orange incandescent bottom tip (bottom 10 pixels)
        for (int py = 0; py < 10; py++) {
            int ry = y + h - 10 + py;
            int r = clamp(180 + py * 7);
            int gr = clamp(65 + py * 14);
            int b = clamp(15 + py * 10);
            g.setColor(new Color(r, gr, b));
            g.fillRect(x + 2, ry, w - 4, 1);
        }
    }

    private static void drawWaterCooledDome(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2D, 0x35, 0x40), 4, 5501L);

        // Concentric tubular water-cooling pipe rings
        int cx = x + w / 2;
        int cy = y + h / 2;
        for (int r = 6; r <= 20; r += 5) {
            g.setColor(new Color(0x1A, 0x20, 0x27));
            g.drawOval(cx - r, cy - r, r * 2, r * 2);
            g.setColor(new Color(0x46, 0x52, 0x63));
            g.drawOval(cx - r + 1, cy - r + 1, (r - 1) * 2, (r - 1) * 2);
        }

        // Center refractory delta insert (3 electrode ports)
        g.setColor(new Color(0x57, 0x41, 0x34));
        g.fillOval(cx - 8, cy - 8, 16, 16);
        g.setColor(new Color(0x14, 0x16, 0x19));
        g.fillOval(cx - 5, cy - 4, 4, 4);
        g.fillOval(cx + 1, cy - 4, 4, 4);
        g.fillOval(cx - 2, cy + 1, 4, 4);
    }

    private static void drawCopperBusbars(Graphics2D g, int x, int y, int w, int h) {
        // Heavy high-conductivity copper bus tubes & clamps
        for (int py = 0; py < h; py++) {
            double norm = Math.sin((py / (double) h) * Math.PI);
            int r = clamp((int) (145 + 75 * norm));
            int gr = clamp((int) (68 + 52 * norm));
            int b = clamp((int) (28 + 25 * norm));
            g.setColor(new Color(r, gr, b));
            g.fillRect(x, y + py, w, 1);
        }
        // Insulator ceramic rings & clamping bolts
        for (int bx = x + 6; bx < x + w - 4; bx += 12) {
            g.setColor(new Color(0x28, 0x2E, 0x36));
            g.fillRect(bx, y, 3, h);
            drawRivet(g, bx + 1, y + 6);
            drawRivet(g, bx + 1, y + h - 8);
        }
    }

    private static void drawTransformerHousing(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2B, 0x32, 0x3B), 3, 5701L);

        // Vertical oil-cooling radiator fins
        for (int fx = x + 4; fx < x + w - 4; fx += 4) {
            g.setColor(new Color(0x16, 0x1A, 0x20));
            g.fillRect(fx, y + 6, 2, h - 12);
            g.setColor(new Color(0x43, 0x4E, 0x5C));
            g.drawLine(fx + 2, y + 6, fx + 2, y + h - 7);
        }

        // High-Voltage Lightning Warning Placard
        int wx = x + w / 2 - 6;
        int wy = y + h / 2 - 6;
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(wx, wy, 12, 12);
        g.setColor(new Color(0x18, 0x18, 0x1B));
        g.drawRect(wx, wy, 12, 12);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(wx + 6, wy + 2, wx + 4, wy + 6);
        g.drawLine(wx + 4, wy + 6, wx + 7, wy + 6);
        g.drawLine(wx + 7, wy + 6, wx + 5, wy + 10);
    }

    private static void drawQuartzViewport(Graphics2D g, int x, int y, int w, int h) {
        // Heavy bolted slag door frame
        fillNoisyRect(g, x, y, w, h, new Color(0x24, 0x2A, 0x32), 4, 5801L);
        g.setColor(new Color(0x47, 0x52, 0x62));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        // Glowing armored quartz window looking into the molten arc bath
        int wx = x + 8, wy = y + 8, ww = w - 16, wh = h - 16;
        for (int py = 0; py < wh; py++) {
            float t = py / (float) wh;
            int r = clamp((int) (255 - t * 35));
            int gr = clamp((int) (185 - t * 95));
            int b = clamp((int) (60 - t * 45));
            g.setColor(new Color(r, gr, b));
            g.fillRect(wx, wy + py, ww, 1);
        }

        // Protective steel safety grille bars across the viewport
        g.setColor(new Color(0x1E, 0x23, 0x2A));
        g.drawRect(wx, wy, ww - 1, wh - 1);
        for (int gx = wx + 6; gx < wx + ww - 4; gx += 7) {
            g.fillRect(gx, wy, 2, wh);
        }
    }

    private static void drawMastAndCylinder(Graphics2D g, int x, int y, int w, int h) {
        // Left half: Polished chrome hydraulic mast column
        for (int px = 0; px < w / 2; px++) {
            double norm = Math.sin((px / (double) (w / 2)) * Math.PI);
            int c = clamp((int) (130 + 95 * norm));
            g.setColor(new Color(c, c + 4, clamp(c + 8)));
            g.fillRect(x + px, y, 1, h);
        }
        // Right half: Amber/Orange heavy hydraulic cylinder barrel
        fillNoisyRect(g, x + w / 2, y, w / 2, h, new Color(0xC2, 0x61, 0x1A), 5, 5901L);
        g.setColor(new Color(0xE0, 0x7A, 0x2E));
        g.fillRect(x + w / 2 + 3, y, 3, h);
        g.setColor(new Color(0x24, 0x29, 0x30));
        g.fillRect(x + w / 2, y + 4, w / 2, 3);
        g.fillRect(x + w / 2, y + h - 7, w / 2, 3);
    }

    private static void drawMelterControlConsole(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x39, 0x42), 3, 6001L);
        g.setColor(new Color(0x52, 0x5D, 0x6B));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Dark OLED Digital Arc Power & Bath Temp Screen
        g.setColor(new Color(0x0B, 0x13, 0x1E));
        g.fillRect(x + 6, y + 6, 34, 16);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.drawRect(x + 6, y + 6, 34, 16);
        // Cyan arc waveform & temp bars
        g.setColor(new Color(0x67, 0xE8, 0xF9));
        g.fillRect(x + 9, y + 9, 18, 2);
        g.setColor(new Color(0xF9, 0x73, 0x16));
        g.fillRect(x + 9, y + 13, 24, 2);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 9, y + 17, 14, 2);

        // 3 Phase Ammeter Dials (Phase A, B, C)
        for (int i = 0; i < 3; i++) {
            int dx = x + 45;
            int dy = y + 7 + i * 11;
            g.setColor(new Color(0xF8, 0xFA, 0xFC));
            g.fillOval(dx, dy, 9, 9);
            g.setColor(new Color(0x1E, 0x29, 0x3B));
            g.drawOval(dx, dy, 9, 9);
            g.setColor(new Color(0xDC, 0x26, 0x26));
            g.drawLine(dx + 4, dy + 4, dx + 7, dy + 2);
        }

        // Pushbuttons & Emergency Stop Mushroom
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 8, y + 28, 7, 7);
        g.setColor(new Color(0xEA, 0xB3, 0x08));
        g.fillRect(x + 18, y + 28, 7, 7);
        // Red E-stop on yellow collar
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 29, y + 26, 11, 11);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 31, y + 28, 7, 7);
    }

    private static void drawArcPlasmaAndGlow(Graphics2D g, int x, int y, int w, int h) {
        // Left half: Brilliant electric cyan-white plasma arc
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w / 2; px++) {
                double dist = Math.abs(px - (w / 4.0)) / (w / 4.0);
                int r = clamp((int) (255 - dist * 140));
                int gr = clamp((int) (255 - dist * 35));
                int b = 255;
                g.setColor(new Color(r, gr, b));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
        // Right half: Incandescent orange-white molten slag glow
        for (int py = 0; py < h; py++) {
            for (int px = w / 2; px < w; px++) {
                double dist = Math.abs((px - w / 2) - (w / 4.0)) / (w / 4.0);
                int r = 255;
                int gr = clamp((int) (210 - dist * 90));
                int b = clamp((int) (90 - dist * 75));
                g.setColor(new Color(r, gr, b));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
    }

    private static void drawChargeHopper(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x37, 0x40, 0x4C), 4, 6201L);
        g.setColor(new Color(0x56, 0x63, 0x75));
        g.fillRect(x, y, w, 4);
        for (int bx = x + 4; bx < x + w - 2; bx += 8) {
            drawRivet(g, bx, y + 1);
            drawRivet(g, bx, y + h - 4);
        }
    }

    private static void drawMoltenBath(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xEA, 0x58, 0x0C), 12, 6301L);
        Random r = new Random(6302L);
        for (int i = 0; i < 35; i++) {
            int px = x + r.nextInt(w - 6);
            int py = y + r.nextInt(h - 6);
            g.setColor(new Color(0xFD, 0xE0, 0x47));
            g.fillRect(px, py, 4, 2);
        }
    }

    // =========================================================================
    // 2. CONTINUOUS CASTING PRESS ENTITY ATLAS (256x256)
    // =========================================================================
    public static void generateCastingPressAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Background base: Dark industrial foundry steel
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x21, 0x26, 0x2E), 4, 7001L);

        // Row 1 (y: 0..48)
        // 1. Heavy 4-Column Tie-Rod Frame & Crown Platen (0, 0, 96, 48)
        drawPressCrownAndFrame(g, 0, 0, 96, 48);
        // 2. Precision Ground Chrome Tie Rods & Guide Columns (96, 0, 64, 48)
        drawChromeTieRods(g, 96, 0, 64, 48);
        // 3. Refractory Tundish / Water-Cooled Copper Mold Basin (160, 0, 48, 48)
        drawCopperMoldBasin(g, 160, 0, 48, 48);
        // 4. High-Tonnage Main Hydraulic Cylinder (Amber/Orange-Red) (208, 0, 48, 48)
        drawMainHydraulicCylinder(g, 208, 0, 48, 48);

        // Row 2 (y: 48..96)
        // 5. Moving Ram Crosshead with Safety-Yellow/Black Hazard Stripes (0, 48, 64, 48)
        drawMovingRamCrosshead(g, 0, 48, 64, 48);
        // 6. Water-Cooled Copper/Tool-Steel Stamping Die Block (64, 48, 48, 48)
        drawWaterCooledDie(g, 64, 48, 48, 48);
        // 7. Rear Molten Inflow Launder Box & Refractory Chute (112, 48, 48, 48)
        drawInflowLaunder(g, 112, 48, 48, 48);
        // 8. Run-Out Roller Table & Driven Steel Rollers (160, 48, 48, 48)
        drawRunoutRollers(g, 160, 48, 48, 48);
        // 9. Cooling Water Manifolds & Blue/Red Supply Pipes (208, 48, 48, 48)
        drawCoolingManifolds(g, 208, 48, 48, 48);

        // Row 3 (y: 96..144)
        // 10. Operator PLC Console Front Faceplate (0, 96, 64, 48)
        drawCastingControlConsole(g, 0, 96, 64, 48);
        // 11. Solidified Hot/Quenched Metal Billet Texture (64, 96, 48, 48)
        drawQuenchedBillet(g, 64, 96, 48, 48);
        // 12. Safety Grille Guard Mesh (112, 96, 48, 48)
        drawSafetyGrille(g, 112, 96, 48, 48);

        // Row 4 (y: 144..192)
        // 13. Light Gray PLC Console Enclosure Casing (0, 144, 64, 48)
        drawCastingConsoleCasing(g, 0, 144, 64, 48);
        // 14. Yellow & Black Safety Hazard Stripes (64, 144, 48, 48)
        drawSafetyHazardStripes(g, 64, 144, 48, 48);
        // 15. Polished Chrome Shafts & Cylinder Rods (112, 144, 48, 48)
        drawChromeSteelTexture(g, 112, 144, 48, 48);
        // 16. Cooling Radiator Fins & Vents (160, 144, 48, 48)
        drawRadiatorFinBank(g, 160, 144, 48, 48);
        // 17. Molten Metal Inflow Liquid (208, 144, 48, 48)
        drawMoltenLaunderStream(g, 208, 144, 48, 48);

        // Row 5 (y: 192..240)
        // 18. Status Beacon ON (0, 192, 24, 24) & OFF (24, 192, 24, 24)
        drawStatusBeaconLenses(g, 0, 192);
        // 19. Cast-Steel Foundation Bed Plates (48, 192, 64, 48)
        drawFoundationBaseplate(g, 48, 192, 64, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/casting_press.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated Casting Press atlas at: " + out.getAbsolutePath());
    }

    private static void drawPressCrownAndFrame(Graphics2D g, int x, int y, int w, int h) {
        // Deep industrial teal-slate / gunmetal casting frame
        fillNoisyRect(g, x, y, w, h, new Color(0x27, 0x33, 0x3F), 4, 7101L);

        // Heavy machined top & bottom platen borders
        g.setColor(new Color(0x3D, 0x4E, 0x60));
        g.fillRect(x, y, w, 4);
        g.fillRect(x, y + h - 4, w, 4);
        g.setColor(new Color(0x58, 0x6E, 0x85));
        g.drawLine(x, y, x + w - 1, y);

        // Recessed casting web pockets
        for (int px = x + 8; px < x + w - 20; px += 28) {
            g.setColor(new Color(0x1B, 0x24, 0x2D));
            g.fillRect(px, y + 8, 20, h - 16);
            g.setColor(new Color(0x38, 0x48, 0x59));
            g.drawRect(px, y + 8, 20, h - 16);
        }

        // "SMS CONCAST" style industrial badge plate
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fillRect(x + 34, y + 18, 28, 10);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.drawRect(x + 34, y + 18, 28, 10);
        g.setColor(new Color(0xE2, 0xE8, 0xF0));
        for (int lx = x + 38; lx < x + 58; lx += 4) {
            g.fillRect(lx, y + 21, 2, 4);
        }
    }

    private static void drawChromeTieRods(Graphics2D g, int x, int y, int w, int h) {
        for (int px = 0; px < w; px++) {
            double norm = Math.sin((px / (double) w) * Math.PI);
            int c = clamp((int) (120 + 115 * norm));
            g.setColor(new Color(c, clamp(c + 3), clamp(c + 8)));
            g.fillRect(x + px, y, 1, h);
        }
        // Heavy castle locknuts at top & bottom
        g.setColor(new Color(0x33, 0x3D, 0x4B));
        g.fillRect(x, y + 2, w, 6);
        g.fillRect(x, y + h - 8, w, 6);
    }

    private static void drawCopperMoldBasin(Graphics2D g, int x, int y, int w, int h) {
        // Outer heavy steel water-jacketed mold frame
        fillNoisyRect(g, x, y, w, h, new Color(0x2A, 0x31, 0x3A), 4, 7301L);

        // Inner copper-lined water-cooled crystallizer mold rim
        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.drawRect(x + 4, y + 4, w - 9, h - 9);
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.drawRect(x + 5, y + 5, w - 11, h - 11);

        // Refractory inner basin floor
        fillNoisyRect(g, x + 6, y + 6, w - 12, h - 12, new Color(0x3B, 0x2B, 0x23), 5, 7302L);
    }

    private static void drawMainHydraulicCylinder(Graphics2D g, int x, int y, int w, int h) {
        // Industrial safety crimson-amber hydraulic cylinder
        for (int px = 0; px < w; px++) {
            double norm = Math.sin((px / (double) w) * Math.PI);
            int r = clamp((int) (140 + 85 * norm));
            int gr = clamp((int) (45 + 40 * norm));
            int b = clamp((int) (20 + 20 * norm));
            g.setColor(new Color(r, gr, b));
            g.fillRect(x + px, y, 1, h);
        }
        // Heavy bolted cylinder head & gland rings
        g.setColor(new Color(0x26, 0x2E, 0x38));
        g.fillRect(x, y + 2, w, 5);
        g.fillRect(x, y + h - 7, w, 5);
        for (int bx = x + 4; bx < x + w - 2; bx += 8) {
            drawRivet(g, bx, y + 3);
            drawRivet(g, bx, y + h - 6);
        }
    }

    private static void drawMovingRamCrosshead(Graphics2D g, int x, int y, int w, int h) {
        // Upper body: Machined grey-steel crosshead with bronze guide bushings
        int split = (h * 2) / 3;
        fillNoisyRect(g, x, y, w, split, new Color(0x47, 0x53, 0x63), 4, 7501L);
        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.fillRect(x + 2, y + 2, 6, split - 4);
        g.fillRect(x + w - 8, y + 2, 6, split - 4);

        // Lower body: High-visibility safety yellow & black diagonal hazard stripes
        fillNoisyRect(g, x, y + split, w, h - split, new Color(0xFA, 0xCC, 0x15), 3, 7502L);
        g.setColor(new Color(0x18, 0x1B, 0x20));
        for (int sx = x - h; sx < x + w; sx += 10) {
            for (int wLine = 0; wLine < 5; wLine++) {
                int x1 = Math.clamp(sx + wLine, x, x + w - 1);
                int x2 = Math.clamp(sx + (h - split) + wLine, x, x + w - 1);
                g.drawLine(x1, y + split, x2, y + h - 1);
            }
        }
    }

    private static void drawWaterCooledDie(Graphics2D g, int x, int y, int w, int h) {
        // Hardened tool steel upper die with copper cooling channels
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x30, 0x3A), 4, 7601L);
        g.setColor(new Color(0x0E, 0xA5, 0xE9)); // Blue water cooling ports
        g.fillOval(x + 8, y + 10, 5, 5);
        g.fillOval(x + w - 13, y + 10, 5, 5);
        g.setColor(new Color(0xD9, 0x77, 0x06)); // Copper heat-transfer bottom plate
        g.fillRect(x + 2, y + h - 8, w - 4, 6);
    }

    private static void drawInflowLaunder(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x33, 0x3A, 0x45), 4, 7701L);
        // Refractory lined channel trough
        fillNoisyRect(g, x + 8, y + 8, w - 16, h - 16, new Color(0x4A, 0x35, 0x2B), 4, 7702L);
        g.setColor(new Color(0xEA, 0x58, 0x0C));
        g.fillRect(x + 14, y + 12, w - 28, h - 24);
    }

    private static void drawRunoutRollers(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x22, 0x28, 0x30), 3, 7801L);
        // Series of brushed steel conveyor rollers
        for (int ry = y + 4; ry < y + h - 4; ry += 8) {
            g.setColor(new Color(0x94, 0xA3, 0xB8));
            g.fillRect(x + 4, ry, w - 8, 5);
            g.setColor(new Color(0xE2, 0xE8, 0xF0));
            g.drawLine(x + 4, ry + 1, x + w - 5, ry + 1);
            g.setColor(new Color(0x33, 0x41, 0x55));
            g.drawLine(x + 4, ry + 4, x + w - 5, ry + 4);
        }
    }

    private static void drawCoolingManifolds(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x26, 0x2E, 0x38), 3, 7901L);
        // Blue cold water inlet pipe
        g.setColor(new Color(0x02, 0x84, 0xC7));
        g.fillRect(x + 6, y, 8, h);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.fillRect(x + 8, y, 2, h);

        // Red warm water return pipe
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 28, y, 8, h);
        g.setColor(new Color(0xF8, 0x71, 0x71));
        g.fillRect(x + 30, y, 2, h);

        // Brass manifold valves
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.fillRect(x + 4, y + 18, 12, 5);
        g.fillRect(x + 26, y + 18, 12, 5);
    }

    private static void drawCastingControlConsole(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xCBD5E1), 3, 8001L);
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // PLC Screen
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fillRect(x + 6, y + 6, 32, 16);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 9, y + 10, 20, 2);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.fillRect(x + 9, y + 15, 26, 2);

        // Hydraulic Tonnage Dial
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillOval(x + 44, y + 6, 14, 14);
        g.setColor(new Color(0x1E, 0x29, 0x3B));
        g.drawOval(x + 44, y + 6, 14, 14);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(x + 51, y + 13, x + 55, y + 9);

        // E-Stop & Buttons
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 45, y + 26, 12, 12);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 47, y + 28, 8, 8);
    }

    private static void drawQuenchedBillet(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x94, 0xA3, 0xB8), 5, 8101L);
        g.setColor(new Color(0xE2, 0xE8, 0xF0));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);
    }

    private static void drawSafetyGrille(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1E, 0x24, 0x2B), 2, 8201L);
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);
        for (int gx = x + 6; gx < x + w - 4; gx += 6) {
            g.drawLine(gx, y + 2, gx, y + h - 3);
        }
        for (int gy = y + 6; gy < y + h - 4; gy += 6) {
            g.drawLine(x + 2, gy, x + w - 3, gy);
        }
    }

    private static void drawDarkConsoleCasing(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x27, 0x2D, 0x36), 3, 6401L);
        g.setColor(new Color(0x3B, 0x44, 0x52));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x18, 0x1C, 0x22));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        // Clean ventilation louvers
        for (int ly = y + 8; ly < y + h - 8; ly += 5) {
            g.setColor(new Color(0x14, 0x17, 0x1C));
            g.fillRect(x + 8, ly, w - 16, 2);
            g.setColor(new Color(0x4A, 0x55, 0x66));
            g.drawLine(x + 8, ly + 2, x + w - 9, ly + 2);
        }
    }

    private static void drawCastingConsoleCasing(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x94, 0xA3, 0xB8), 3, 8301L);
        g.setColor(new Color(0xCB, 0xD5, 0xE1));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        for (int ly = y + 8; ly < y + h - 8; ly += 5) {
            g.setColor(new Color(0x33, 0x41, 0x55));
            g.fillRect(x + 8, ly, w - 16, 2);
            g.setColor(new Color(0xE2, 0xE8, 0xF0));
            g.drawLine(x + 8, ly + 2, x + w - 9, ly + 2);
        }
    }

    private static void drawSafetyHazardStripes(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xFA, 0xCC, 0x15), 3, 6501L);
        g.setColor(new Color(0x18, 0x18, 0x1B));
        for (int sx = x - h; sx < x + w + h; sx += 8) {
            for (int lineW = 0; lineW < 4; lineW++) {
                int x1 = sx + lineW;
                int x2 = sx + h + lineW;
                if ((x1 >= x && x1 < x + w) || (x2 >= x && x2 < x + w)) {
                    g.drawLine(Math.max(x, Math.min(x + w - 1, x1)), y,
                            Math.max(x, Math.min(x + w - 1, x2)), y + h - 1);
                }
            }
        }
        g.setColor(new Color(0x71, 0x3F, 0x12));
        g.drawRect(x, y, w - 1, h - 1);
    }

    private static void drawChromeSteelTexture(Graphics2D g, int x, int y, int w, int h) {
        for (int px = 0; px < w; px++) {
            double norm = Math.sin((px / (double) w) * Math.PI);
            int c = clamp((int) (140 + 105 * norm));
            g.setColor(new Color(c, clamp(c + 2), clamp(c + 6)));
            g.fillRect(x + px, y, 1, h);
        }
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.drawRect(x, y, w - 1, h - 1);
    }

    private static void drawRadiatorFinBank(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1E, 0x24, 0x2B), 3, 6601L);
        for (int fx = x + 3; fx < x + w - 3; fx += 4) {
            g.setColor(new Color(0x0F, 0x13, 0x18));
            g.fillRect(fx, y + 2, 2, h - 4);
            g.setColor(new Color(0x47, 0x55, 0x69));
            g.drawLine(fx + 2, y + 2, fx + 2, y + h - 3);
        }
    }

    private static void drawMoltenLaunderStream(Graphics2D g, int x, int y, int w, int h) {
        for (int py = 0; py < h; py++) {
            double norm = Math.sin((py / (double) h) * Math.PI);
            int r = 255;
            int gr = clamp((int) (120 + 95 * norm));
            int b = clamp((int) (30 + 50 * norm));
            g.setColor(new Color(r, gr, b));
            g.fillRect(x, y + py, w, 1);
        }
        Random rnd = new Random(6701L);
        for (int i = 0; i < 20; i++) {
            g.setColor(new Color(0xFF, 0xFB, 0xEB));
            g.fillRect(x + rnd.nextInt(w - 3), y + rnd.nextInt(h - 2), 3, 2);
        }
    }

    private static void drawStatusBeaconLenses(Graphics2D g, int x, int y) {
        // ON status: vibrant illuminated emerald/cyan
        fillNoisyRect(g, x, y, 24, 24, new Color(0x10, 0xB9, 0x81), 4, 6801L);
        g.setColor(new Color(0x6E, 0xEE, 0xC7));
        g.fillOval(x + 4, y + 4, 16, 16);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillOval(x + 7, y + 7, 10, 10);

        // OFF status: dark standby lens
        fillNoisyRect(g, x + 24, y, 24, 24, new Color(0x1C, 0x22, 0x2B), 2, 6802L);
        g.setColor(new Color(0x33, 0x41, 0x55));
        g.drawOval(x + 28, y + 4, 16, 16);
    }

    private static void drawFoundationBaseplate(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x1B, 0x20, 0x27), 4, 6901L);
        g.setColor(new Color(0x2E, 0x36, 0x42));
        g.fillRect(x, y, w, 3);
        g.fillRect(x, y + h - 3, w, 3);
        g.setColor(new Color(0x43, 0x4F, 0x60));
        g.drawLine(x, y, x + w - 1, y);
        for (int rx = x + 8; rx < x + w - 4; rx += 14) {
            drawRivet(g, rx, y + 4);
            drawRivet(g, rx, y + h - 5);
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================
    private static void drawRivet(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x11, 0x14, 0x18));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillRect(x, y, 1, 1);
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
