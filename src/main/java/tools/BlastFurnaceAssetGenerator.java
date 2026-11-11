package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates custom textures for the Multiblock Blast Furnace:
 * 1. 256x256 Entity Texture Atlas (assets/refinery/textures/entity/blast_furnace.png)
 *    - Heavy structural steel I-beam girders and window frame bezels
 *    - Magnesia-carbon refractory crucible hearth firebrick
 *    - Glowing incandescent molten pig iron / steel bath
 *    - Porous dark refractory slag crust with glowing ember veins
 *    - Cast copper/bronze hot blast tuyere cooling jackets
 *    - Insulated dark steel toroidal bustle pipe & goosenecks
 *    - Superheated hot air blast flame jet plasma
 *    - Inverted conical blast furnace charge hopper bell
 *    - Riveted corner gusset plates & heavy cross mullions
 *    - Heavy industrial foundry manufacturer nameplate badge
 *
 * 2. 16x16 Block Textures (assets/refinery/textures/block/):
 *    - blast_furnace_casing.png: Heavy refractory-backed steel plate with corner hex bolts and bevels
 *    - blast_furnace_controller_off.png: Recessed operator console with brass pyrometer gauge, CRT screen & switches
 *    - blast_furnace_controller_on.png: Active console with glowing 1600°C pyrometer, live CRT diagnostics & fire vents
 *    - foundry_port_input.png: Heavy pneumatic intake flange with inward blue directional feed chevrons
 *    - foundry_port_output.png: Heavy refractory taphole spout with glowing amber exit chevrons
 *    - foundry_glass.png: High-temperature quartz glass with embedded diamond steel reinforcement wire-mesh
 */
public class BlastFurnaceAssetGenerator {

    private static final String ASSETS_DIR = "src/main/resources/assets/refinery/";

    public static void main(String[] args) throws Exception {
        generateEntityAtlas();
        generateBlockTextures();
        System.out.println("Blast Furnace 3D textures generated successfully!");
    }

    // =========================================================================
    // 1. 256x256 ENTITY TEXTURE ATLAS
    // =========================================================================
    public static void generateEntityAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with dark industrial gunmetal
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x1E, 0x22, 0x28), 3, 4001L);

        // 1. Structural Steel I-Beam Bezel & Girder (0, 0, 96, 48)
        drawStructuralSteelGirder(g, 0, 0, 96, 48);

        // 2. Refractory Crucible Hearth Firebrick (96, 0, 64, 48)
        drawRefractoryHearth(g, 96, 0, 64, 48);

        // 3. Molten Pig Iron / Steel Bath (160, 0, 48, 48)
        drawMoltenMetalBath(g, 160, 0, 48, 48);

        // 4. Porous Slag Crust with Glowing Cracks (208, 0, 48, 48)
        drawSlagCrust(g, 208, 0, 48, 48);

        // 5. Cast Copper Tuyere Cooling Jacket (0, 48, 64, 48)
        drawCopperTuyere(g, 0, 48, 64, 48);

        // 6. Insulated Toroidal Bustle Pipe (64, 48, 64, 48)
        drawBustlePipe(g, 64, 48, 64, 48);

        // 7. Superheated Hot Blast Flame Jet (128, 48, 48, 48)
        drawBlastFlameJet(g, 128, 48, 48, 48);

        // 8. Inverted Conical Charge Hopper Bell (176, 48, 48, 48)
        drawChargeHopperBell(g, 176, 48, 48, 48);

        // 9. Riveted Corner Gusset Bracket Plate (0, 96, 48, 48)
        drawCornerGussetPlate(g, 0, 96, 48, 48);

        // 10. Center Window Mullion Strut (48, 96, 48, 48)
        drawMullionStrut(g, 48, 96, 48, 48);

        // 11. Heavy Foundry Nameplate & Hazard Stripes (96, 96, 48, 48)
        drawFoundryBadge(g, 96, 96, 48, 48);

        // 12. Heavy Steel Baseplate Skid (144, 96, 64, 48)
        drawBaseplateSkid(g, 144, 96, 64, 48);

        g.dispose();

        File out = new File(ASSETS_DIR + "textures/entity/blast_furnace.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Saved entity atlas: " + out.getAbsolutePath());
    }

    private static void drawStructuralSteelGirder(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x32, 0x38, 0x42), 3, 5001L);
        // Beveled edges (top highlight, bottom shadow)
        g.setColor(new Color(0x52, 0x5E, 0x6E));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y + 1, x + w - 1, y + 1);
        g.setColor(new Color(0x18, 0x1C, 0x22));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 2, x + w - 1, y + h - 2);

        // Center recessed channel web (I-beam profile)
        fillNoisyRect(g, x + 4, y + 8, w - 8, h - 16, new Color(0x25, 0x2A, 0x31), 2, 5002L);
        g.setColor(new Color(0x16, 0x19, 0x1E));
        g.drawRect(x + 4, y + 8, w - 9, h - 17);

        // Rivet pairs along top and bottom flanges
        for (int rx = x + 8; rx < x + w - 8; rx += 14) {
            drawHexBolt(g, rx, y + 4);
            drawHexBolt(g, rx, y + h - 6);
        }
    }

    private static void drawRefractoryHearth(Graphics2D g, int x, int y, int w, int h) {
        // High-alumina / magnesia-carbon refractory firebrick (dark warm terracotta/amber-charcoal)
        fillNoisyRect(g, x, y, w, h, new Color(0x4E, 0x3B, 0x32), 4, 5003L);
        // Mortar lines for industrial furnace firebrick
        g.setColor(new Color(0x2E, 0x22, 0x1C));
        int brickH = 8;
        int brickW = 16;
        for (int by = y; by < y + h; by += brickH) {
            g.drawLine(x, by, x + w - 1, by);
            int offset = ((by - y) / brickH) % 2 == 0 ? 0 : brickW / 2;
            for (int bx = x + offset; bx < x + w; bx += brickW) {
                g.drawLine(bx, by, bx, Math.min(y + h - 1, by + brickH));
            }
        }
        // Surface cinder speckles
        Random r = new Random(5004L);
        for (int i = 0; i < 40; i++) {
            int px = x + r.nextInt(w);
            int py = y + r.nextInt(h);
            g.setColor(r.nextBoolean() ? new Color(0x22, 0x18, 0x14) : new Color(0x6E, 0x48, 0x36));
            g.fillRect(px, py, 1, 1);
        }
    }

    private static void drawMoltenMetalBath(Graphics2D g, int x, int y, int w, int h) {
        // Blazing incandescent molten pig iron / molten steel bath
        // Swirling bright yellow, orange, and white-hot convection currents
        fillNoisyRect(g, x, y, w, h, new Color(0xFF, 0x98, 0x00), 10, 5005L);

        Random r = new Random(5006L);
        for (int py = y; py < y + h; py++) {
            for (int px = x; px < x + w; px++) {
                double dist = Math.sin((px - x) * 0.4) * Math.cos((py - y) * 0.4);
                if (dist > 0.4) {
                    // White-hot core
                    g.setColor(new Color(0xFF, 0xFD, 0xD0));
                } else if (dist > 0.1) {
                    // Brilliant yellow molten steel
                    g.setColor(new Color(0xFF, 0xEB, 0x3B));
                } else if (dist > -0.3) {
                    // Radiant deep orange
                    g.setColor(new Color(0xFF, 0x70, 0x10));
                } else {
                    // Deep lava red
                    g.setColor(new Color(0xDD, 0x2C, 0x00));
                }
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void drawSlagCrust(Graphics2D g, int x, int y, int w, int h) {
        // Dark glassy metallurgical blast furnace slag with glowing heat fissures
        fillNoisyRect(g, x, y, w, h, new Color(0x24, 0x22, 0x20), 3, 5007L);

        // Glowing fissure veins running through the crust
        g.setColor(new Color(0xFF, 0x6D, 0x00));
        g.drawLine(x + 4, y + 10, x + 20, y + 14);
        g.drawLine(x + 20, y + 14, x + 35, y + 8);
        g.drawLine(x + 22, y + 14, x + 28, y + 36);
        g.drawLine(x + 10, y + 25, x + 38, y + 30);

        // Core white-yellow hot vein centers
        g.setColor(new Color(0xFF, 0xE0, 0x82));
        g.drawLine(x + 12, y + 12, x + 24, y + 15);
        g.drawLine(x + 23, y + 18, x + 26, y + 28);
    }

    private static void drawCopperTuyere(Graphics2D g, int x, int y, int w, int h) {
        // Cast copper water-cooled tuyere blowpipe body
        fillNoisyRect(g, x, y, w, h, new Color(0xB8, 0x62, 0x3A), 4, 5008L);

        // Copper metallic highlight and shadow stripes (cylindrical curvature)
        g.setColor(new Color(0xDD, 0x85, 0x5C));
        g.fillRect(x, y + 4, w, 6);
        g.setColor(new Color(0xF2, 0xAA, 0x85));
        g.drawLine(x, y + 6, x + w - 1, y + 6);

        g.setColor(new Color(0x7A, 0x3A, 0x1E));
        g.fillRect(x, y + h - 10, w, 8);
        g.setColor(new Color(0x56, 0x24, 0x10));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Cooling water jacket ribs & bolted flange
        g.setColor(new Color(0x3E, 0x44, 0x4E));
        g.fillRect(x, y, 10, h);
        for (int by = y + 4; by < y + h; by += 8) {
            drawHexBolt(g, x + 5, by);
        }
    }

    private static void drawBustlePipe(Graphics2D g, int x, int y, int w, int h) {
        // Heavy insulated dark steel circular hot blast bustle pipe
        fillNoisyRect(g, x, y, w, h, new Color(0x2D, 0x31, 0x38), 3, 5009L);

        // Cylindrical highlight along top quarter
        g.setColor(new Color(0x4A, 0x52, 0x5F));
        g.fillRect(x, y + 5, w, 7);
        g.setColor(new Color(0x68, 0x74, 0x85));
        g.drawLine(x, y + 7, x + w - 1, y + 7);

        // Shadow along bottom
        g.setColor(new Color(0x17, 0x1A, 0x1F));
        g.fillRect(x, y + h - 8, w, 8);

        // Structural reinforcing cinch straps / clamp bands
        for (int bx = x + 12; bx < x + w; bx += 20) {
            g.setColor(new Color(0x58, 0x63, 0x73));
            g.fillRect(bx, y, 4, h);
            g.setColor(new Color(0x12, 0x14, 0x18));
            g.drawLine(bx + 3, y, bx + 3, y + h - 1);
            drawHexBolt(g, bx + 2, y + 4);
            drawHexBolt(g, bx + 2, y + h - 5);
        }
    }

    private static void drawBlastFlameJet(Graphics2D g, int x, int y, int w, int h) {
        // High-temperature 1200°C+ preheated oxygen-enriched hot blast flame jet
        fillNoisyRect(g, x, y, w, h, new Color(0xFF, 0x57, 0x22), 8, 5010L);

        // Conical white-hot core
        for (int py = y; py < y + h; py++) {
            float t = (float) (py - y) / h;
            int widthAtY = (int) (w * (1.0f - t * 0.6f));
            int startX = x + (w - widthAtY) / 2;

            g.setColor(new Color(0xFF, 0xD5, 0x4F));
            g.fillRect(startX, py, widthAtY, 1);

            int coreW = widthAtY / 2;
            int coreX = x + (w - coreW) / 2;
            g.setColor(new Color(0xFF, 0xFF, 0xF0));
            g.fillRect(coreX, py, coreW, 1);
        }
    }

    private static void drawChargeHopperBell(Graphics2D g, int x, int y, int w, int h) {
        // Conical cast iron blast furnace charge distribution bell
        fillNoisyRect(g, x, y, w, h, new Color(0x35, 0x3A, 0x42), 3, 5011L);

        // Circular ring contour highlights
        g.setColor(new Color(0x58, 0x62, 0x70));
        g.drawLine(x, y + 4, x + w - 1, y + 4);
        g.drawLine(x, y + 16, x + w - 1, y + 16);
        g.drawLine(x, y + 32, x + w - 1, y + 32);

        g.setColor(new Color(0x1B, 0x1E, 0x22));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Heavy perimeter lip with bolts
        g.setColor(new Color(0x28, 0x2C, 0x32));
        g.fillRect(x, y + h - 8, w, 8);
        for (int bx = x + 4; bx < x + w; bx += 8) {
            drawHexBolt(g, bx, y + h - 4);
        }
    }

    private static void drawCornerGussetPlate(Graphics2D g, int x, int y, int w, int h) {
        // Heavy riveted corner bracket gusset plate (reinforcing the 3x3 window)
        fillNoisyRect(g, x, y, w, h, new Color(0x2A, 0x30, 0x38), 3, 5012L);
        // Bevel borders
        g.setColor(new Color(0x4C, 0x57, 0x66));
        g.drawRect(x, y, w - 1, h - 1);
        g.setColor(new Color(0x14, 0x17, 0x1B));
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);

        // Triangular reinforcement web
        g.setColor(new Color(0x38, 0x40, 0x4B));
        g.fillPolygon(
                new int[]{x + 4, x + w - 4, x + 4},
                new int[]{y + 4, y + 4, y + h - 4},
                3
        );

        // Rivets
        drawHexBolt(g, x + 8, y + 8);
        drawHexBolt(g, x + w - 10, y + 8);
        drawHexBolt(g, x + 8, y + h - 10);
        drawHexBolt(g, x + w / 2, y + h / 2);
    }

    private static void drawMullionStrut(Graphics2D g, int x, int y, int w, int h) {
        // Center steel cross-mullion bar with central connection hub
        fillNoisyRect(g, x, y, w, h, new Color(0x30, 0x36, 0x40), 2, 5013L);
        g.setColor(new Color(0x50, 0x5B, 0x6B));
        g.drawLine(x, y, x + w - 1, y);
        g.setColor(new Color(0x18, 0x1B, 0x20));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Center circular rosette bracket
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.setColor(new Color(0x22, 0x27, 0x2F));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(0x56, 0x62, 0x73));
        g.drawOval(cx - 10, cy - 10, 19, 19);

        // Heavy central retaining nut
        drawHexBolt(g, cx, cy);
    }

    private static void drawFoundryBadge(Graphics2D g, int x, int y, int w, int h) {
        // Heavy brass/steel foundry nameplate and hazard warning stripes
        fillNoisyRect(g, x, y, w, h, new Color(0x1C, 0x20, 0x26), 2, 5014L);
        // Beveled gold/brass frame
        g.setColor(new Color(0xC5, 0x9B, 0x38));
        g.drawRect(x + 2, y + 2, w - 5, h - 5);

        // Diagonal hazard stripes on bottom quarter
        int stripeY = y + h - 14;
        for (int sx = x + 2; sx < x + w - 2; sx += 8) {
            g.setColor(new Color(0xFA, 0xBA, 0x10));
            g.fillRect(sx, stripeY, 4, 10);
            g.setColor(new Color(0x18, 0x18, 0x18));
            g.fillRect(sx + 4, stripeY, 4, 10);
        }

        // Emblem text simulation
        g.setColor(new Color(0xDE, 0xB8, 0x5C));
        g.drawLine(x + 8, y + 8, x + w - 8, y + 8);
        g.drawLine(x + 12, y + 14, x + w - 12, y + 14);
        g.drawLine(x + 8, y + 20, x + w - 8, y + 20);

        // 4 corner mounting screws
        drawHexBolt(g, x + 4, y + 4);
        drawHexBolt(g, x + w - 5, y + 4);
        drawHexBolt(g, x + 4, y + h - 5);
        drawHexBolt(g, x + w - 5, y + h - 5);
    }

    private static void drawBaseplateSkid(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x28, 0x2D, 0x34), 3, 5015L);
        g.setColor(new Color(0x4E, 0x58, 0x66));
        g.drawLine(x, y, x + w - 1, y);
        g.setColor(new Color(0x12, 0x15, 0x19));
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        for (int bx = x + 8; bx < x + w; bx += 16) {
            drawHexBolt(g, bx, y + h / 2);
        }
    }

    // =========================================================================
    // 2. 16x16 BLOCK TEXTURES
    // =========================================================================
    public static void generateBlockTextures() throws Exception {
        // 1. Blast Furnace Casing (16x16)
        BufferedImage casing = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gC = casing.createGraphics();
        fillNoisyRect(gC, 0, 0, 16, 16, new Color(0x2A, 0x2F, 0x36), 2, 6001L);
        // Beveled plate perimeter
        gC.setColor(new Color(0x50, 0x5A, 0x69));
        gC.drawLine(0, 0, 15, 0);
        gC.drawLine(0, 0, 0, 15);
        gC.setColor(new Color(0x14, 0x17, 0x1C));
        gC.drawLine(0, 15, 15, 15);
        gC.drawLine(15, 0, 15, 15);

        // Center embossed refractory heat shield plate (8x8)
        fillNoisyRect(gC, 3, 3, 10, 10, new Color(0x35, 0x3B, 0x44), 3, 6002L);
        gC.setColor(new Color(0x5A, 0x65, 0x76));
        gC.drawLine(3, 3, 12, 3);
        gC.drawLine(3, 3, 3, 12);
        gC.setColor(new Color(0x18, 0x1B, 0x20));
        gC.drawLine(3, 12, 12, 12);
        gC.drawLine(12, 3, 12, 12);

        // Refractory ceramic flecks in center
        gC.setColor(new Color(0x6A, 0x54, 0x46));
        gC.fillRect(5, 5, 2, 2);
        gC.fillRect(9, 8, 2, 2);

        // 4 heavy hex bolts in the corners
        drawHexBolt16(gC, 1, 1);
        drawHexBolt16(gC, 13, 1);
        drawHexBolt16(gC, 1, 13);
        drawHexBolt16(gC, 13, 13);
        gC.dispose();
        saveImg(casing, "textures/block/blast_furnace_casing.png");

        // 2. Blast Furnace Controller OFF (16x16)
        BufferedImage ctrlOff = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gOff = ctrlOff.createGraphics();
        // Background casing
        gOff.drawImage(casing, 0, 0, null);

        // Recessed control panel (x: 2, y: 2, w: 12, h: 12)
        gOff.setColor(new Color(0x16, 0x19, 0x1E));
        gOff.fillRect(2, 2, 12, 12);
        gOff.setColor(new Color(0x0E, 0x10, 0x14));
        gOff.drawLine(2, 2, 13, 2);
        gOff.drawLine(2, 2, 2, 13);
        gOff.setColor(new Color(0x3D, 0x46, 0x53));
        gOff.drawLine(2, 13, 13, 13);
        gOff.drawLine(13, 2, 13, 13);

        // Top-left: Circular High-Temp Pyrometer (x: 3, y: 3, w: 6, h: 6)
        gOff.setColor(new Color(0x8C, 0x6E, 0x32)); // Brass rim
        gOff.drawOval(3, 3, 5, 5);
        gOff.setColor(new Color(0x28, 0x22, 0x1B)); // Dial face (cold)
        gOff.fillOval(4, 4, 4, 4);
        gOff.setColor(new Color(0xDE, 0x3C, 0x2A)); // Needle pointing to 0°C (left)
        gOff.drawLine(5, 6, 6, 6);

        // Top-right: Status LED Cluster (x: 10, y: 3, w: 3, h: 6)
        gOff.setColor(new Color(0x55, 0x15, 0x15)); // Dim red power
        gOff.fillRect(10, 4, 2, 1);
        gOff.setColor(new Color(0x18, 0x3A, 0x18)); // Dim green ready
        gOff.fillRect(10, 6, 2, 1);
        gOff.setColor(new Color(0x3A, 0x2E, 0x10)); // Dim amber heat
        gOff.fillRect(10, 8, 2, 1);

        // Bottom: Dark horizontal digital CRT telemetry display (x: 3, y: 10, w: 10, h: 3)
        gOff.setColor(new Color(0x0C, 0x12, 0x15));
        gOff.fillRect(3, 10, 10, 3);
        gOff.setColor(new Color(0x1A, 0x2A, 0x2E)); // Faint scanlines
        gOff.drawLine(4, 11, 11, 11);
        gOff.dispose();
        saveImg(ctrlOff, "textures/block/blast_furnace_controller_off.png");

        // 3. Blast Furnace Controller ON (16x16)
        BufferedImage ctrlOn = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gOn = ctrlOn.createGraphics();
        // Base same as off
        gOn.drawImage(casing, 0, 0, null);

        // Recessed control panel
        gOn.setColor(new Color(0x18, 0x1B, 0x20));
        gOn.fillRect(2, 2, 12, 12);
        gOn.setColor(new Color(0x0E, 0x10, 0x14));
        gOn.drawLine(2, 2, 13, 2);
        gOn.drawLine(2, 2, 2, 13);
        gOn.setColor(new Color(0x4B, 0x55, 0x64));
        gOn.drawLine(2, 13, 13, 13);
        gOn.drawLine(13, 2, 13, 13);

        // Top-left: Blazing 1600°C Pyrometer (pegged in red/orange zone with glowing dial!)
        gOn.setColor(new Color(0xE8, 0xB6, 0x48)); // Polished glowing brass rim
        gOn.drawOval(3, 3, 5, 5);
        gOn.setColor(new Color(0xFF, 0x6F, 0x00)); // Glowing dial face
        gOn.fillOval(4, 4, 4, 4);
        gOn.setColor(new Color(0xFF, 0xEB, 0x3B)); // Incandescent core
        gOn.fillRect(5, 5, 2, 2);
        gOn.setColor(new Color(0xFF, 0x17, 0x44)); // Needle pegged high (pointing top-right!)
        gOn.drawLine(6, 6, 7, 4);

        // Top-right: Active Bright Status LEDs
        gOn.setColor(new Color(0x00, 0xE6, 0x76)); // Bright neon green (ACTIVE)
        gOn.fillRect(10, 4, 2, 1);
        gOn.setColor(new Color(0xFF, 0xD6, 0x00)); // Bright vivid yellow (BLAST AIR ON)
        gOn.fillRect(10, 6, 2, 1);
        gOn.setColor(new Color(0xFF, 0x3D, 0x00)); // Blazing red-orange (MAX HEAT)
        gOn.fillRect(10, 8, 2, 1);

        // Bottom: Illuminated Digital CRT telemetry screen with glowing cyan readout
        gOn.setColor(new Color(0x06, 0x22, 0x28));
        gOn.fillRect(3, 10, 10, 3);
        gOn.setColor(new Color(0x00, 0xE5, 0xFF)); // Glowing cyan telemetry line
        gOn.drawLine(4, 11, 6, 11);
        gOn.drawLine(8, 11, 11, 11);
        gOn.setColor(new Color(0xFF, 0x91, 0x00)); // Amber warning blinker
        gOn.fillRect(7, 11, 1, 1);
        gOn.dispose();
        saveImg(ctrlOn, "textures/block/blast_furnace_controller_on.png");

        // 4. Foundry Input Port (16x16)
        BufferedImage inPort = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gIn = inPort.createGraphics();
        gIn.drawImage(casing, 0, 0, null);

        // Recessed circular pneumatic hopper port (x: 3, y: 3, w: 10, h: 10)
        gIn.setColor(new Color(0x1B, 0x22, 0x30)); // Deep blue-tinted steel
        gIn.fillOval(3, 3, 10, 10);
        gIn.setColor(new Color(0x3B, 0x82, 0xF6)); // Blue indicator flange rim
        gIn.drawOval(3, 3, 9, 9);
        gIn.setColor(new Color(0x0F, 0x14, 0x1E)); // Deep center chute
        gIn.fillOval(5, 5, 6, 6);

        // Inward feed chevrons (pointing inward / right-down)
        gIn.setColor(new Color(0x60, 0xA5, 0xFA));
        gIn.drawLine(7, 6, 9, 8);
        gIn.drawLine(7, 10, 9, 8);
        gIn.drawLine(6, 6, 8, 8);
        gIn.drawLine(6, 10, 8, 8);
        gIn.dispose();
        saveImg(inPort, "textures/block/foundry_port_input.png");

        // 5. Foundry Output Port (16x16)
        BufferedImage outPort = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gOut = outPort.createGraphics();
        gOut.drawImage(casing, 0, 0, null);

        // Heavy refractory tapping spout opening
        gOut.setColor(new Color(0x35, 0x24, 0x1C)); // Refractory ceramic collar
        gOut.fillOval(3, 3, 10, 10);
        gOut.setColor(new Color(0xF5, 0x9E, 0x0B)); // Amber indicator flange rim
        gOut.drawOval(3, 3, 9, 9);
        gOut.setColor(new Color(0x18, 0x0E, 0x08)); // Deep taphole orifice
        gOut.fillOval(5, 5, 6, 6);

        // Outward tapping chevrons (pointing outward / left-up)
        gOut.setColor(new Color(0xFB, 0xBF, 0x24));
        gOut.drawLine(9, 6, 7, 8);
        gOut.drawLine(9, 10, 7, 8);
        gOut.drawLine(10, 6, 8, 8);
        gOut.drawLine(10, 10, 8, 8);
        gOut.dispose();
        saveImg(outPort, "textures/block/foundry_port_output.png");

        // 6. Foundry Glass (16x16)
        BufferedImage glass = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gG = glass.createGraphics();
        // Clear translucent quartz glass background with slight cyan tint (alpha ~ 35)
        gG.setColor(new Color(0x90, 0xCA, 0xF9, 32));
        gG.fillRect(0, 0, 16, 16);

        // Dark steel perimeter frame
        gG.setColor(new Color(0x28, 0x2E, 0x36, 230));
        gG.drawRect(0, 0, 15, 15);

        // Embedded diamond steel reinforcement wire-mesh (chicken-wire pattern)
        Color wire = new Color(0x3A, 0x43, 0x50, 160);
        gG.setColor(wire);
        // Diagonal wire lines (slope +1 and -1)
        for (int offset = -16; offset < 32; offset += 6) {
            gG.drawLine(offset, 0, offset + 16, 16);
            gG.drawLine(offset, 16, offset + 16, 0);
        }

        // Crisp specular diagonal glass sheen reflection
        Color glint = new Color(0xFF, 0xFF, 0xFF, 140);
        gG.setColor(glint);
        gG.drawLine(2, 2, 8, 2);
        gG.drawLine(2, 3, 4, 3);
        gG.drawLine(12, 13, 13, 13);
        gG.dispose();
        saveImg(glass, "textures/block/foundry_glass.png");
    }

    private static void saveImg(BufferedImage img, String relPath) throws Exception {
        File out = new File(ASSETS_DIR + relPath);
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Saved texture: " + out.getAbsolutePath());
    }

    private static void drawHexBolt(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(0x60, 0x6C, 0x7D));
        g.fillRect(cx - 1, cy - 2, 3, 5);
        g.fillRect(cx - 2, cy - 1, 5, 3);
        g.setColor(new Color(0x12, 0x15, 0x19));
        g.fillRect(cx, cy, 1, 1);
        g.setColor(new Color(0x8C, 0x9B, 0xAE));
        g.fillRect(cx - 1, cy - 1, 1, 1);
    }

    private static void drawHexBolt16(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x68, 0x76, 0x88));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x14, 0x17, 0x1C));
        g.fillRect(x + 1, y + 1, 1, 1);
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int variance, long seed) {
        Random rand = new Random(seed);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int d = rand.nextInt(variance * 2 + 1) - variance;
                int r = Math.max(0, Math.min(255, base.getRed() + d));
                int gr = Math.max(0, Math.min(255, base.getGreen() + d));
                int b = Math.max(0, Math.min(255, base.getBlue() + d));
                g.setColor(new Color(r, gr, b, base.getAlpha()));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
    }
}
