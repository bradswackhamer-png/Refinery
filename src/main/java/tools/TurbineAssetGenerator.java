package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates the 256x256 high-resolution texture atlas for the 3D Monolithic Steam Turbine model:
 * Matching the reference industrial turbine-generator skid:
 * - Structural heavy steel I-beam skid base in vibrant industrial electric blue (#12569F)
 * - Large electric AC generator / alternator with stator cooling louvers, terminal connection box,
 *   end bell air intake cowl, and drive-end bearing bracket
 * - Intermediate reduction gearbox with lubrication inspection plate and shaft coupling guard
 * - High-pressure steam turbine split casing with domed shell, bolted horizontal split line,
 *   circular bearing end covers, and lifting eye lugs
 * - Front flanged steam inlet nozzle chest with bolt ring, bore, and governor valve actuator assembly
 * - Upright control console with upper blue triple-dial pressure/RPM gauge box and lower silver
 *   electrical cabinet with red mushroom E-stop, selector switches, and glowing status indicators
 * - Stainless/copper auxiliary piping runs and rear condenser water discharge duct
 */
public class TurbineAssetGenerator {

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        System.out.println("Steam Turbine entity texture atlas generated successfully!");
    }

    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill entire background with dark industrial blue-steel to prevent any UV edge bleeding
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x0C, 0x2A, 0x4D), 3, 1001L);

        // 1. Turbine Split Casing Shell (0, 0, 96, 48)
        drawTurbineCasingShell(g, 0, 0, 96, 48);

        // 2. Turbine Bearing Pedestals & Circular End Covers (96, 0, 64, 48)
        drawTurbineBearingPedestals(g, 96, 0, 64, 48);

        // 3. Structural Skid I-Beam Steel (160, 0, 48, 48)
        drawSkidSteel(g, 160, 0, 48, 48);

        // 4. Intermediate Gearbox & Shaft Coupling Guard (208, 0, 48, 48)
        drawGearboxSteel(g, 208, 0, 48, 48);

        // 5. AC Generator Stator Shell with Cooling Louvers (0, 48, 64, 48)
        drawGeneratorStatorShell(g, 0, 48, 64, 48);

        // 6. Generator End Bell & Air Intake Mesh (64, 48, 48, 48)
        drawGeneratorEndBell(g, 64, 48, 48, 48);

        // 7. Generator Terminal Connection Box (112, 48, 48, 48)
        drawGeneratorTerminalBox(g, 112, 48, 48, 48);

        // 8. Flanged Steam Inlet Chest & Nozzle (160, 48, 48, 48)
        drawSteamInletChest(g, 160, 48, 48, 48);

        // 9. Governor Actuator & Trip Valve (208, 48, 48, 48)
        drawGovernorActuator(g, 208, 48, 48, 48);

        // 10. Control Console - Silver Electrical Cabinet (0, 96, 64, 48)
        drawControlCabinet(g, 0, 96, 64, 48);

        // 11. Control Console - Blue Gauge Box Enclosure (64, 96, 48, 48)
        drawGaugeBoxEnclosure(g, 64, 96, 48, 48);

        // 12. Triple Analog Dial Gauges (112, 96, 48, 48)
        drawTripleAnalogGauges(g, 112, 96, 48, 48);

        // 13. Active Status Indicators & Glowing Digital LED Display (160, 96, 48, 48)
        drawActiveIndicators(g, 160, 96, 48, 48);

        // 14. Machined Shafts & Stainless Tubing (208, 96, 48, 48)
        drawShaftsAndTubing(g, 208, 96, 48, 48);

        // 15. Condenser Hotwell Exhaust Duct (0, 144, 96, 48)
        drawCondenserDuct(g, 0, 144, 96, 48);

        g.dispose();

        File out = new File("src/main/resources/assets/refinery/textures/entity/steam_turbine.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Generated entity texture atlas at: " + out.getAbsolutePath());
    }

    // =========================================================================
    // 1. TURBINE CASING SHELL (0, 0, 96, 48)
    // =========================================================================
    private static void drawTurbineCasingShell(Graphics2D g, int x, int y, int w, int h) {
        // Base industrial blue enamel
        fillNoisyRect(g, x, y, w, h, new Color(0x13, 0x56, 0x9E), 4, 101L);

        // Horizontal cylindrical casing highlight (top cylindrical curvature)
        g.setColor(new Color(0x27, 0x76, 0xC9));
        g.fillRect(x, y + 2, w, 6);
        g.setColor(new Color(0x42, 0x93, 0xEB));
        g.drawLine(x, y + 4, x + w - 1, y + 4);

        // Lower shell shadow
        g.setColor(new Color(0x0C, 0x3A, 0x6E));
        g.fillRect(x, y + h - 8, w, 8);

        // Heavy Bolted Horizontal Split Flange (center band)
        int fy = y + 22;
        g.setColor(new Color(0x0A, 0x2A, 0x52));
        g.fillRect(x, fy - 1, w, 8);
        g.setColor(new Color(0x1E, 0x6E, 0xBC));
        g.fillRect(x, fy, w, 6);
        g.setColor(new Color(0x56, 0xA2, 0xF5));
        g.drawLine(x, fy, x + w - 1, fy);
        g.setColor(new Color(0x09, 0x21, 0x40));
        g.drawLine(x, fy + 5, x + w - 1, fy + 5);

        // Split flange bolts
        for (int bx = x + 3; bx < x + w; bx += 6) {
            drawRivet(g, bx, fy + 2);
        }

        // Vertical casing rib bands with lifting brackets
        int[] ribXs = {x + 18, x + 44, x + 72};
        for (int rx : ribXs) {
            g.setColor(new Color(0x0C, 0x36, 0x68));
            g.fillRect(rx - 2, y, 5, h);
            g.setColor(new Color(0x2B, 0x7C, 0xD4));
            g.fillRect(rx - 1, y, 3, h);
            g.setColor(new Color(0x60, 0xAB, 0xFD));
            g.drawLine(rx - 1, y, rx - 1, y + h - 1);

            // Stiffener bolts
            drawRivet(g, rx, y + 6);
            drawRivet(g, rx, y + 14);
            drawRivet(g, rx, y + 34);
            drawRivet(g, rx, y + 42);
        }

        // Heavy cast manufacturer lettering plate ("ELLIOTT / SIEMENS" style badge)
        g.setColor(new Color(0x0B, 0x28, 0x4D));
        g.fillRect(x + 28, y + 9, 32, 9);
        g.setColor(new Color(0x1F, 0x6B, 0xB8));
        g.fillRect(x + 29, y + 10, 30, 7);
        g.setColor(new Color(0x93, 0xC5, 0xFD));
        g.drawRect(x + 29, y + 10, 30, 7);
        // Cast embossed letters
        g.setColor(new Color(0xDB, 0xEA, 0xFE));
        for (int lx = x + 33; lx < x + 56; lx += 4) {
            g.fillRect(lx, y + 12, 2, 3);
        }
    }

    // =========================================================================
    // 2. TURBINE BEARING PEDESTALS & CIRCULAR END COVERS (96, 0, 64, 48)
    // =========================================================================
    private static void drawTurbineBearingPedestals(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x11, 0x4B, 0x8C), 3, 201L);

        // Circular bolted bearing end cap (matching Image circular cover)
        int cx = x + 16, cy = y + 24, r = 18;
        g.setColor(new Color(0x0A, 0x2E, 0x58));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x1A, 0x62, 0xB0));
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        g.setColor(new Color(0x3B, 0x8B, 0xE0));
        g.drawOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

        // Circular bolt ring (12 perimeter bolts)
        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI * 2.0 / 12.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawRivet(g, bx - 1, by - 1);
        }

        // Central machined shaft hub and brass grease cup
        g.setColor(new Color(0x0E, 0x34, 0x63));
        g.fillOval(cx - 7, cy - 7, 14, 14);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillOval(cx - 5, cy - 5, 10, 10);
        // Brass oil level fitting
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.fillRect(cx - 2, cy - 2, 4, 4);
        g.setColor(new Color(0xFB, 0xBF, 0x24));
        g.fillRect(cx - 1, cy - 1, 2, 2);

        // Right side: pedestal foot mounting base & shims
        int px = x + 38;
        g.setColor(new Color(0x0B, 0x2F, 0x5B));
        g.fillRect(px, y + 4, 22, h - 8);
        g.setColor(new Color(0x1E, 0x67, 0xB5));
        g.drawRect(px, y + 4, 22, h - 8);
        for (int by = y + 8; by < y + h - 8; by += 10) {
            drawRivet(g, px + 4, by);
            drawRivet(g, px + 17, by);
        }
    }

    // =========================================================================
    // 3. STRUCTURAL SKID I-BEAM STEEL (160, 0, 48, 48)
    // =========================================================================
    private static void drawSkidSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x0F, 0x48, 0x87), 3, 301L);

        // Heavy I-beam upper and lower flanges
        g.setColor(new Color(0x1B, 0x66, 0xB8));
        g.fillRect(x, y, w, 5);
        g.fillRect(x, y + h - 5, w, 5);
        g.setColor(new Color(0x42, 0x93, 0xEB));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + h - 5, x + w - 1, y + h - 5);
        g.setColor(new Color(0x08, 0x22, 0x40));
        g.drawLine(x, y + 4, x + w - 1, y + 4);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Web reinforcement stiffeners & gussets
        for (int gx = x + 6; gx < x + w; gx += 16) {
            g.setColor(new Color(0x09, 0x26, 0x47));
            g.fillRect(gx - 1, y + 5, 4, h - 10);
            g.setColor(new Color(0x17, 0x58, 0x9F));
            g.fillRect(gx, y + 5, 2, h - 10);
            // Skid foundation mounting bolts
            drawRivet(g, gx, y + 7);
            drawRivet(g, gx, y + h - 9);
        }
    }

    // =========================================================================
    // 4. INTERMEDIATE GEARBOX & SHAFT COUPLING GUARD (208, 0, 48, 48)
    // =========================================================================
    private static void drawGearboxSteel(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x11, 0x4D, 0x90), 3, 401L);

        // Beveled top casting cover plate
        g.setColor(new Color(0x1D, 0x6E, 0xC0));
        g.fillRect(x + 2, y + 2, w - 4, 10);
        g.setColor(new Color(0x47, 0x9C, 0xF5));
        g.drawLine(x + 2, y + 2, x + w - 3, y + 2);
        g.setColor(new Color(0x09, 0x25, 0x47));
        g.drawLine(x + 2, y + 11, x + w - 3, y + 11);

        // Red Lube / Inspection Tag ("Royal Purple" plate from reference pic)
        g.setColor(new Color(0x99, 0x1B, 0x1B));
        g.fillRect(x + 6, y + 16, 22, 8);
        g.setColor(new Color(0xEF, 0x44, 0x44));
        g.drawRect(x + 6, y + 16, 22, 8);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillRect(x + 9, y + 19, 16, 2);
        drawRivet(g, x + 7, y + 19);
        drawRivet(g, x + 25, y + 19);

        // Circular oil level sight glass with amber lubricant visible
        int sx = x + 34, sy = y + 28, sr = 6;
        g.setColor(new Color(0xB4, 0x53, 0x09));
        g.fillOval(sx - sr, sy - sr, sr * 2, sr * 2);
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        g.drawOval(sx - sr, sy - sr, sr * 2, sr * 2);
        // Amber lubricant fluid fill
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.fillOval(sx - sr + 1, sy - sr + 1, (sr - 1) * 2, (sr - 1) * 2);
        g.setColor(new Color(0x78, 0x35, 0x0F));
        g.fillRect(sx - sr + 2, sy, (sr - 2) * 2, sr - 1);
        g.setColor(new Color(0xFF, 0xF7, 0xED));
        g.drawLine(sx - 2, sy - 3, sx + 2, sy - 3);

        // Shaft coupling guard ventilation grill
        for (int gy = y + 36; gy < y + h - 2; gy += 3) {
            g.setColor(new Color(0x07, 0x1C, 0x36));
            g.drawLine(x + 4, gy, x + 26, gy);
        }
    }

    // =========================================================================
    // 5. AC GENERATOR STATOR SHELL (0, 48, 64, 48)
    // =========================================================================
    private static void drawGeneratorStatorShell(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x11, 0x50, 0x94), 3, 501L);

        // Cylindrical stator highlights
        g.setColor(new Color(0x27, 0x78, 0xCC));
        g.fillRect(x, y + 2, w, 5);
        g.setColor(new Color(0x56, 0xA2, 0xF5));
        g.drawLine(x, y + 3, x + w - 1, y + 3);

        // Prominent horizontal cooling louvers / ribs (matching Image generator body)
        for (int ly = y + 10; ly < y + h - 4; ly += 4) {
            g.setColor(new Color(0x08, 0x22, 0x40));
            g.fillRect(x, ly, w, 2);
            g.setColor(new Color(0x2E, 0x82, 0xD8));
            g.drawLine(x, ly + 2, x + w - 1, ly + 2);
            g.setColor(new Color(0x50, 0x9E, 0xF3));
            g.drawLine(x, ly + 3, x + w - 1, ly + 3);
        }

        // Heavy vertical stator clamp ribs
        int[] statorRibs = {x + 14, x + 32, x + 50};
        for (int srx : statorRibs) {
            g.setColor(new Color(0x09, 0x2A, 0x50));
            g.fillRect(srx - 1, y, 4, h);
            g.setColor(new Color(0x1F, 0x6E, 0xBD));
            g.fillRect(srx, y, 2, h);
            drawRivet(g, srx, y + 6);
            drawRivet(g, srx, y + h - 8);
        }
    }

    // =========================================================================
    // 6. GENERATOR END BELL & AIR INTAKE MESH (64, 48, 48, 48)
    // =========================================================================
    private static void drawGeneratorEndBell(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x0E, 0x43, 0x7D), 3, 601L);

        // Circular rear end bell frame
        g.setColor(new Color(0x1B, 0x67, 0xB8));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);
        g.setColor(new Color(0x07, 0x1E, 0x38));
        g.drawRect(x + 3, y + 3, w - 7, h - 7);

        // Perforated black air intake mesh grille
        int mx = x + 5, my = y + 5, mw = w - 10, mh = h - 10;
        g.setColor(new Color(0x08, 0x14, 0x22));
        g.fillRect(mx, my, mw, mh);

        // Hex mesh pattern
        g.setColor(new Color(0x1E, 0x29, 0x3B));
        for (int px = mx; px < mx + mw; px += 3) {
            for (int py = my; py < my + mh; py += 3) {
                g.fillRect(px, py, 1, 1);
            }
        }

        // Center bearing boss & grease nipple
        int cx = x + w / 2, cy = y + h / 2;
        g.setColor(new Color(0x15, 0x58, 0x9E));
        g.fillOval(cx - 7, cy - 7, 14, 14);
        g.setColor(new Color(0x56, 0xA2, 0xF5));
        g.drawOval(cx - 7, cy - 7, 14, 14);
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.fillRect(cx - 2, cy - 2, 4, 4);
    }

    // =========================================================================
    // 7. GENERATOR TERMINAL CONNECTION BOX (112, 48, 48, 48)
    // =========================================================================
    private static void drawGeneratorTerminalBox(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x12, 0x56, 0x9E), 3, 701L);

        // Bolted terminal cover plate
        g.setColor(new Color(0x1E, 0x6E, 0xBC));
        g.fillRect(x + 3, y + 3, w - 6, h - 6);
        g.setColor(new Color(0x56, 0xA2, 0xF5));
        g.drawRect(x + 3, y + 3, w - 6, h - 6);
        g.setColor(new Color(0x09, 0x24, 0x44));
        g.drawRect(x + 4, y + 4, w - 8, h - 8);

        // Cover perimeter bolts
        for (int bx = x + 5; bx < x + w - 4; bx += 8) {
            drawRivet(g, bx, y + 5);
            drawRivet(g, bx, y + h - 7);
        }
        for (int by = y + 13; by < y + h - 12; by += 8) {
            drawRivet(g, x + 5, by);
            drawRivet(g, x + w - 7, by);
        }

        // High Voltage Danger Warning Badge
        int tx = x + 16, ty = y + 16;
        g.setColor(new Color(0xF5, 0x9E, 0x0B));
        int[] triX = {tx + 7, tx, tx + 14};
        int[] triY = {ty, ty + 12, ty + 12};
        g.fillPolygon(triX, triY, 3);
        g.setColor(new Color(0x00, 0x00, 0x00));
        // Lightning bolt icon
        g.drawLine(tx + 7, ty + 3, tx + 5, ty + 7);
        g.drawLine(tx + 5, ty + 7, tx + 8, ty + 7);
        g.drawLine(tx + 8, ty + 7, tx + 6, ty + 10);
    }

    // =========================================================================
    // 8. FLANGED STEAM INLET CHEST & NOZZLE (160, 48, 48, 48)
    // =========================================================================
    private static void drawSteamInletChest(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x10, 0x4C, 0x8E), 3, 801L);

        // Heavy Circular Pipe Flange (matching prominent front inlet in reference image)
        int cx = x + w / 2, cy = y + h / 2, r = 19;
        g.setColor(new Color(0x08, 0x22, 0x40));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0x1A, 0x66, 0xB6));
        g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
        g.setColor(new Color(0x4F, 0x9C, 0xF3));
        g.drawOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

        // Circular Steam Bore (Deep dark hollow inside)
        int br = 10;
        g.setColor(new Color(0x06, 0x0D, 0x17));
        g.fillOval(cx - br, cy - br, br * 2, br * 2);
        g.setColor(new Color(0x0F, 0x22, 0x38));
        g.drawOval(cx - br, cy - br, br * 2, br * 2);

        // 12 High-Pressure Stud Bolts around the flange perimeter
        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI * 2.0 / 12.0);
            int bx = (int) (cx + Math.cos(angle) * (r - 4));
            int by = (int) (cy + Math.sin(angle) * (r - 4));
            drawRivet(g, bx - 1, by - 1);
        }
    }

    // =========================================================================
    // 9. GOVERNOR ACTUATOR & TRIP VALVE (208, 48, 48, 48)
    // =========================================================================
    private static void drawGovernorActuator(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x12, 0x52, 0x96), 3, 901L);

        // Hydraulic governor actuator cylinder (machined steel)
        g.setColor(new Color(0x64, 0x74, 0x8B));
        g.fillRect(x + 6, y + 4, 14, 26);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillRect(x + 8, y + 4, 10, 26);
        g.setColor(new Color(0xF1, 0xF5, 0xF9));
        g.drawLine(x + 10, y + 4, x + 10, y + 29);

        // Brass/Gold mechanical valve linkages & pivot arms
        g.setColor(new Color(0xD9, 0x77, 0x06));
        g.fillRect(x + 24, y + 8, 16, 4);
        g.fillRect(x + 36, y + 8, 4, 20);
        g.setColor(new Color(0xFB, 0xBF, 0x24));
        g.drawRect(x + 24, y + 8, 16, 4);
        drawRivet(g, x + 25, y + 9);
        drawRivet(g, x + 37, y + 9);
        drawRivet(g, x + 37, y + 26);

        // Emergency manual trip valve handwheel (red ring)
        int hx = x + 14, hy = y + 38, hr = 7;
        g.setColor(new Color(0x99, 0x1B, 0x1B));
        g.drawOval(hx - hr, hy - hr, hr * 2, hr * 2);
        g.setColor(new Color(0xEF, 0x44, 0x44));
        g.drawOval(hx - hr + 1, hy - hr + 1, (hr - 1) * 2, (hr - 1) * 2);
        g.drawLine(hx - hr + 2, hy, hx + hr - 2, hy);
        g.drawLine(hx, hy - hr + 2, hx, hy + hr - 2);
    }

    // =========================================================================
    // 10. CONTROL CONSOLE - SILVER ELECTRICAL CABINET (0, 96, 64, 48)
    // =========================================================================
    private static void drawControlCabinet(Graphics2D g, int x, int y, int w, int h) {
        // Industrial light-grey enamel finish
        fillNoisyRect(g, x, y, w, h, new Color(0xD1, 0xD5, 0xDB), 3, 1001L);

        // Cabinet beveled door trim
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x9C, 0xA3, 0xAF));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Black gasket seal line
        g.setColor(new Color(0x4B, 0x55, 0x63));
        g.drawRect(x + 3, y + 3, w - 7, h - 7);

        // Door hinges on left
        g.setColor(new Color(0x37, 0x41, 0x51));
        g.fillRect(x + 1, y + 6, 4, 6);
        g.fillRect(x + 1, y + h - 12, 4, 6);

        // Quarter-turn chrome door latches
        g.setColor(new Color(0x1F, 0x29, 0x37));
        g.fillRect(x + w - 8, y + 12, 4, 6);
        g.fillRect(x + w - 8, y + h - 18, 4, 6);
        g.setColor(new Color(0xF3, 0xF4, 0xF6));
        g.fillRect(x + w - 7, y + 14, 2, 2);
        g.fillRect(x + w - 7, y + h - 16, 2, 2);

        // Emergency Stop Pushbutton with Yellow Collar (from reference pic)
        int esx = x + 44, esy = y + 32;
        g.setColor(new Color(0xCA, 0x8A, 0x04)); // Yellow safety bezel
        g.fillOval(esx - 5, esy - 5, 10, 10);
        g.setColor(new Color(0xDC, 0x26, 0x26)); // Red mushroom button
        g.fillOval(esx - 3, esy - 3, 6, 6);
        g.setColor(new Color(0xEF, 0x44, 0x44));
        g.fillOval(esx - 2, esy - 2, 3, 3);

        // Black Rotary Selector Switches (2-position / 3-position)
        int[] swYs = {y + 12, y + 20};
        for (int swy : swYs) {
            g.setColor(new Color(0x1F, 0x29, 0x37));
            g.fillOval(esx - 4, swy - 4, 8, 8);
            g.setColor(new Color(0x6B, 0x72, 0x80));
            g.drawOval(esx - 4, swy - 4, 8, 8);
            g.setColor(new Color(0xFA, 0xFA, 0xFA));
            g.drawLine(esx - 2, swy, esx + 2, swy);
        }

        // Pushbuttons (Black Start, White Stop)
        g.setColor(new Color(0x11, 0x18, 0x27));
        g.fillOval(x + 30, y + 12, 5, 5);
        g.setColor(new Color(0xF3, 0xF4, 0xF6));
        g.fillOval(x + 30, y + 20, 5, 5);

        // Status Indicator Lamps (Green, Amber, Red)
        g.setColor(new Color(0x15, 0x80, 0x3D)); // Green Run
        g.fillRect(x + 12, y + 12, 4, 4);
        g.setColor(new Color(0xB4, 0x53, 0x09)); // Amber Standby
        g.fillRect(x + 12, y + 20, 4, 4);
        g.setColor(new Color(0xB9, 0x1C, 0x1C)); // Red Trip / Fault
        g.fillRect(x + 12, y + 28, 4, 4);

        // Yellow lockout warning tag
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 10, y + 36, 12, 6);
        g.setColor(new Color(0x00, 0x00, 0x00));
        g.drawRect(x + 10, y + 36, 12, 6);
        g.drawLine(x + 12, y + 39, x + 20, y + 39);
    }

    // =========================================================================
    // 11. CONTROL CONSOLE - BLUE GAUGE BOX ENCLOSURE (64, 96, 48, 48)
    // =========================================================================
    private static void drawGaugeBoxEnclosure(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x12, 0x56, 0x9E), 3, 1101L);

        // Enclosure top highlight
        g.setColor(new Color(0x27, 0x76, 0xC9));
        g.fillRect(x, y, w, 4);
        g.setColor(new Color(0x56, 0xA2, 0xF5));
        g.drawLine(x, y, x + w - 1, y);

        // Instrument bezel recesses
        g.setColor(new Color(0x0A, 0x2A, 0x52));
        g.drawRect(x + 2, y + 5, w - 5, h - 10);
        g.setColor(new Color(0x1E, 0x6E, 0xBC));
        g.drawRect(x + 3, y + 6, w - 7, h - 12);

        // Bottom digital display window cutout
        g.setColor(new Color(0x05, 0x11, 0x20));
        g.fillRect(x + 12, y + 34, 24, 8);
        g.setColor(new Color(0x1E, 0x6E, 0xBC));
        g.drawRect(x + 12, y + 34, 24, 8);
    }

    // =========================================================================
    // 12. TRIPLE ANALOG DIAL GAUGES (112, 96, 48, 48)
    // =========================================================================
    private static void drawTripleAnalogGauges(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x10, 0x4E, 0x92), 3, 1201L);

        // Three circular gauges side-by-side (matching Image upper gauge box)
        int[] gaugeXs = {x + 8, x + 24, x + 40};
        String[] labels = {"PSI", "RPM", "VAC"};

        for (int i = 0; i < 3; i++) {
            int gx = gaugeXs[i];
            int gy = y + 16;
            int gr = 7;

            // Black bezel ring
            g.setColor(new Color(0x11, 0x18, 0x27));
            g.fillOval(gx - gr, gy - gr, gr * 2, gr * 2);
            g.setColor(new Color(0x37, 0x41, 0x51));
            g.drawOval(gx - gr, gy - gr, gr * 2, gr * 2);

            // White dial face
            g.setColor(new Color(0xF8, 0xFA, 0xFC));
            g.fillOval(gx - gr + 1, gy - gr + 1, (gr - 1) * 2, (gr - 1) * 2);

            // Graduation tick marks
            g.setColor(new Color(0x0F, 0x17, 0x2A));
            g.drawLine(gx - 4, gy - 3, gx - 2, gy - 2);
            g.drawLine(gx, gy - 5, gx, gy - 3);
            g.drawLine(gx + 4, gy - 3, gx + 2, gy - 2);

            // Red indicator needle pointing to operating reading
            g.setColor(new Color(0xDC, 0x26, 0x26));
            if (i == 0) {
                // Steam Pressure: ~350 PSI (pointing upper-right)
                g.drawLine(gx, gy, gx + 3, gy - 3);
            } else if (i == 1) {
                // RPM: 3600 RPM (pointing top)
                g.drawLine(gx, gy, gx, gy - 4);
            } else {
                // Vacuum: -28 inHg (pointing upper-left)
                g.drawLine(gx, gy, gx - 3, gy - 3);
            }
            g.fillRect(gx - 1, gy - 1, 2, 2);
        }

        // Digital display below gauges
        g.setColor(new Color(0x05, 0x11, 0x20));
        g.fillRect(x + 10, y + 32, 28, 10);
        g.setColor(new Color(0x22, 0xD3, 0xEE)); // Cyan digital LED readout
        g.drawString("1000", x + 13, y + 41);
    }

    // =========================================================================
    // 13. ACTIVE GLOW INDICATORS & EMISSIVE LED DISPLAY (160, 96, 48, 48)
    // =========================================================================
    private static void drawActiveIndicators(Graphics2D g, int x, int y, int w, int h) {
        // Dark enclosure
        fillNoisyRect(g, x, y, w, h, new Color(0x06, 0x18, 0x2E), 2, 1301L);

        // Glowing Green Operational Run Light
        int lx = x + 12, ly = y + 16, lr = 8;
        // Outer glow halo
        g.setColor(new Color(0x14, 0x53, 0x2D));
        g.fillOval(lx - lr, ly - lr, lr * 2, lr * 2);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillOval(lx - lr + 2, ly - lr + 2, (lr - 2) * 2, (lr - 2) * 2);
        g.setColor(new Color(0x86, 0xEF, 0xAC));
        g.fillOval(lx - lr + 4, ly - lr + 4, (lr - 4) * 2, (lr - 4) * 2);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillRect(lx - 1, ly - 1, 3, 3);

        // Glowing Cyan Digital Power Bar
        int bx = x + 28, by = y + 8, bw = 16, bh = 32;
        g.setColor(new Color(0x02, 0x2C, 0x36));
        g.fillRect(bx, by, bw, bh);
        for (int py = by + 2; py < by + bh - 2; py += 3) {
            g.setColor(new Color(0x06, 0xB6, 0xD4));
            g.fillRect(bx + 2, py, bw - 4, 2);
            g.setColor(new Color(0xA5, 0xF3, 0xFC));
            g.fillRect(bx + 4, py, 4, 1);
        }
    }

    // =========================================================================
    // 14. MACHINED SHAFTS & STAINLESS TUBING (208, 96, 48, 48)
    // =========================================================================
    private static void drawShaftsAndTubing(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x0B, 0x28, 0x4A), 2, 1401L);

        // Precision ground polished chrome/steel shaft
        g.setColor(new Color(0x64, 0x74, 0x8B));
        g.fillRect(x + 2, y + 4, 18, 40);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillRect(x + 5, y + 4, 12, 40);
        g.setColor(new Color(0xF8, 0xFA, 0xFC));
        g.drawLine(x + 8, y + 4, x + 8, y + 43);

        // Stainless hydraulic control tubing & compression fittings
        for (int tx = x + 24; tx < x + w - 4; tx += 7) {
            g.setColor(new Color(0x94, 0xA3, 0xB8));
            g.fillRect(tx, y + 2, 2, h - 4);
            g.setColor(new Color(0xF1, 0xF5, 0xF9));
            g.drawLine(tx, y + 2, tx, y + h - 3);

            // Brass union fittings
            g.setColor(new Color(0xD9, 0x77, 0x06));
            g.fillRect(tx - 1, y + 14, 4, 4);
            g.fillRect(tx - 1, y + 32, 4, 4);
        }
    }

    // =========================================================================
    // 15. CONDENSER HOTWELL EXHAUST DUCT (0, 144, 96, 48)
    // =========================================================================
    private static void drawCondenserDuct(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x10, 0x46, 0x82), 3, 1501L);

        // Large rectangular flanged exhaust connection leading to condenser
        g.setColor(new Color(0x08, 0x20, 0x3D));
        g.drawRect(x + 3, y + 3, w - 7, h - 7);
        g.setColor(new Color(0x1C, 0x69, 0xB8));
        g.drawRect(x + 4, y + 4, w - 9, h - 9);

        // Duct perimeter bolts
        for (int bx = x + 6; bx < x + w - 5; bx += 8) {
            drawRivet(g, bx, y + 5);
            drawRivet(g, bx, y + h - 7);
        }
        for (int by = y + 12; by < y + h - 10; by += 8) {
            drawRivet(g, x + 5, by);
            drawRivet(g, x + w - 7, by);
        }

        // Inner low-pressure steam / condensate drainage grid
        int gx = x + 12, gy = y + 10, gw = w - 24, gh = h - 20;
        g.setColor(new Color(0x06, 0x14, 0x25));
        g.fillRect(gx, gy, gw, gh);
        g.setColor(new Color(0x25, 0x63, 0xEB));
        for (int px = gx; px < gx + gw; px += 4) {
            g.drawLine(px, gy, px, gy + gh - 1);
        }
    }

    // =========================================================================
    // PROCEDURAL HELPERS
    // =========================================================================
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

    private static void drawRivet(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x07, 0x1C, 0x36));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x93, 0xC5, 0xFD));
        g.fillRect(x, y, 1, 1);
    }
}
