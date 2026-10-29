package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class FluidPumpAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/entity").mkdirs();
        new File(ASSETS + "textures/gui").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generatePumpModelAtlas();
        generatePumpGui();
        generateBlockAndItemTextures();
        generateJsonFiles();

        System.out.println("Fluid Pump assets generated successfully!");
    }

    /**
     * Generates 256x256 entity texture atlas for the monolithic 3D Fluid Pump model:
     * - Black tubular roll cage (tubes, corners, weld seams, rubber feet)
     * - Safety Yellow recoil pull-starter casing & fan shroud with "SUPERKIGER WP40X" badge
     * - White enamel fuel tank with chrome filler cap & warning decals
     * - Silver cast-metal engine crankcase with cooling fins & red oil plug
     * - Cast aluminum centrifugal volute pump casing & bolted front inspection cover
     * - Top discharge pipe & top flange
     * - Suction intake flange & collar
     * - Spiral-ribbed industrial suction hose & brass suction strainer basket
     * - Black air filter & exhaust heat shield
     */
    public static void generatePumpModelAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with transparent
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 256, 256);
        g.setComposite(AlphaComposite.SrcOver);

        // 1. Roll-Cage Tubular Black Steel (0, 0, 96, 48)
        fillNoisyRect(g, 0, 0, 96, 48, new Color(0x1C, 0x1E, 0x22), 3, 1001L);
        // Highlight top reflections to look like cylindrical round tubing
        for (int y = 0; y < 48; y += 12) {
            g.setColor(new Color(0x35, 0x39, 0x40));
            g.fillRect(0, y + 1, 96, 2);
            g.setColor(new Color(0x10, 0x11, 0x14));
            g.fillRect(0, y + 9, 96, 3);
        }

        // 2. Rubber Feet / Skid Mounts (96, 0, 48, 48)
        fillNoisyRect(g, 96, 0, 48, 48, new Color(0x12, 0x13, 0x15), 2, 1002L);
        g.setColor(new Color(0x25, 0x27, 0x2C));
        g.drawRect(96, 0, 47, 47);
        // Bolt holes
        g.setColor(new Color(0x40, 0x45, 0x4E));
        g.fillOval(104, 8, 6, 6);
        g.fillOval(128, 8, 6, 6);
        g.fillOval(104, 32, 6, 6);
        g.fillOval(128, 32, 6, 6);

        // 3. Engine Crankcase Silver Metal (144, 0, 64, 48)
        fillNoisyRect(g, 144, 0, 64, 48, new Color(0x9E, 0xA3, 0xAA), 4, 1003L);
        g.setColor(new Color(0x73, 0x78, 0x82));
        // Cooling fin horizontal grooves
        for (int y = 4; y < 44; y += 6) {
            g.fillRect(146, y, 60, 2);
        }
        g.setColor(new Color(0xC5, 0xCB, 0xD4));
        g.drawRect(144, 0, 63, 47);

        // 4. Red Oil Plug / Dipstick (208, 0, 48, 48)
        fillNoisyRect(g, 208, 0, 48, 48, new Color(0xD3, 0x2F, 0x2F), 3, 1004L);
        g.setColor(new Color(0xEF, 0x53, 0x50));
        g.fillRect(212, 4, 40, 8);
        g.setColor(new Color(0x8B, 0x00, 0x00));
        g.drawRect(208, 0, 47, 47);

        // 5. Safety Yellow Recoil Starter Face & Shroud (0, 48, 64, 64)
        fillNoisyRect(g, 0, 48, 64, 64, new Color(0xFA, 0xBC, 0x09), 3, 1005L);
        g.setColor(new Color(0xD9, 0x9B, 0x00));
        g.drawRect(0, 48, 63, 63);
        // Concentric circular ventilation slots
        g.setColor(new Color(0x1F, 0x1E, 0x1C));
        g.fillOval(16, 64, 32, 32);
        g.setColor(new Color(0xFA, 0xBC, 0x09));
        g.fillOval(20, 68, 24, 24);
        g.setColor(new Color(0x1F, 0x1E, 0x1C));
        g.fillOval(25, 73, 14, 14);
        g.setColor(new Color(0xE5, 0xA2, 0x00));
        g.fillOval(28, 76, 8, 8);
        // Radial grille blades
        g.setColor(new Color(0x1F, 0x1E, 0x1C));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0;
            int x1 = (int) (32 + Math.cos(angle) * 11);
            int y1 = (int) (80 + Math.sin(angle) * 11);
            int x2 = (int) (32 + Math.cos(angle) * 16);
            int y2 = (int) (80 + Math.sin(angle) * 16);
            g.drawLine(x1, y1, x2, y2);
        }
        // Black decal text plate: "SUPERKIGER"
        g.setColor(new Color(0x1C, 0x1C, 0x1C));
        g.fillRect(12, 100, 40, 9);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillRect(14, 102, 36, 1);
        g.fillRect(14, 105, 36, 1);

        // 6. Recoil Pull-Handle (T-grip) (64, 48, 32, 32)
        fillNoisyRect(g, 64, 48, 32, 32, new Color(0x15, 0x16, 0x18), 2, 1006L);
        g.setColor(new Color(0x3B, 0x40, 0x48));
        g.drawRect(64, 48, 31, 31);
        // T-grip handle
        g.fillRect(72, 54, 16, 6);
        g.fillRect(77, 60, 6, 14);

        // 7. White Fuel Tank Body (96, 48, 64, 64)
        fillNoisyRect(g, 96, 48, 64, 64, new Color(0xF2, 0xF4, 0xF6), 2, 1007L);
        g.setColor(new Color(0xD0, 0xD4, 0xDA));
        g.drawRect(96, 48, 63, 63);
        // Embossed logo plate: "SUPERKIGER WP40X"
        g.setColor(new Color(0x22, 0x25, 0x2A));
        g.fillRect(104, 66, 48, 14);
        g.setColor(new Color(0xFA, 0xBC, 0x09));
        g.fillRect(104, 80, 48, 4);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.fillRect(108, 69, 40, 2);
        g.fillRect(108, 73, 40, 2);
        // Gas tank seams
        g.setColor(new Color(0xC0, 0xC5, 0xCC));
        g.drawLine(96, 104, 159, 104);

        // 8. Chrome Gas Tank Filler Cap (160, 48, 32, 32)
        fillNoisyRect(g, 160, 48, 32, 32, new Color(0xDC, 0xE0, 0xE5), 2, 1008L);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawOval(162, 50, 27, 27);
        g.setColor(new Color(0x99, 0xA0, 0xA8));
        g.drawOval(165, 53, 21, 21);
        g.setColor(new Color(0xC0, 0x20, 0x20));
        g.fillRect(174, 60, 4, 8); // Red center arrow

        // 9. Black Air Filter / Muffler Heat Shield (192, 48, 64, 64)
        fillNoisyRect(g, 192, 48, 64, 64, new Color(0x22, 0x24, 0x28), 3, 1009L);
        g.setColor(new Color(0x3B, 0x3E, 0x44));
        g.drawRect(192, 48, 63, 63);
        // Perforated heat shield vents
        g.setColor(new Color(0x11, 0x12, 0x14));
        for (int y = 56; y < 104; y += 6) {
            for (int x = 200; x < 248; x += 8) {
                g.fillRect(x, y, 4, 3);
            }
        }

        // 10. Cast Aluminum Pump Volute Casing (0, 112, 64, 64)
        fillNoisyRect(g, 0, 112, 64, 64, new Color(0xAE, 0xB4, 0xBC), 3, 1010L);
        g.setColor(new Color(0x7D, 0x83, 0x8D));
        g.drawRect(0, 112, 63, 63);
        // Bolted circular front cover
        g.setColor(new Color(0x96, 0x9D, 0xA6));
        g.fillOval(8, 120, 48, 48);
        g.setColor(new Color(0x6A, 0x70, 0x79));
        g.drawOval(8, 120, 48, 48);
        // Bolts around rim
        g.setColor(new Color(0x45, 0x49, 0x50));
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3.0;
            int bx = (int) (32 + Math.cos(angle) * 21);
            int by = (int) (144 + Math.sin(angle) * 21);
            g.fillOval(bx - 2, by - 2, 4, 4);
        }
        // Yellow safety warning label on pump
        g.setColor(new Color(0xFA, 0xBC, 0x09));
        g.fillRect(20, 138, 24, 12);
        g.setColor(new Color(0x1F, 0x1E, 0x1C));
        g.drawRect(20, 138, 23, 11);
        g.fillRect(23, 142, 18, 1);
        g.fillRect(23, 145, 18, 1);

        // 11. Top Discharge Pipe & Flange (64, 112, 48, 48)
        fillNoisyRect(g, 64, 112, 48, 48, new Color(0xB5, 0xBB, 0xC3), 3, 1011L);
        g.setColor(new Color(0x8A, 0x90, 0x99));
        g.drawRect(64, 112, 47, 47);
        // Circular outlet ring
        g.setColor(new Color(0x35, 0x39, 0x40));
        g.fillOval(72, 120, 32, 32);
        g.setColor(new Color(0x18, 0x1A, 0x1D));
        g.fillOval(78, 126, 20, 20);

        // 12. Suction Intake Port & Flange (112, 112, 48, 48)
        fillNoisyRect(g, 112, 112, 48, 48, new Color(0xA4, 0xAA, 0xB2), 3, 1012L);
        g.setColor(new Color(0x73, 0x79, 0x82));
        g.drawRect(112, 112, 47, 47);
        // Suction inlet collar
        g.setColor(new Color(0x2F, 0x33, 0x3A));
        g.fillOval(120, 120, 32, 32);
        g.setColor(new Color(0x15, 0x17, 0x1A));
        g.fillOval(126, 126, 20, 20);

        // 13. Spiral-Ribbed Suction Hose (160, 112, 64, 64)
        fillNoisyRect(g, 160, 112, 64, 64, new Color(0x23, 0x2B, 0x2F), 2, 1013L);
        // Spiraling white/translucent reinforcing ribs
        for (int y = 112; y < 176; y += 4) {
            g.setColor(new Color(0x45, 0x58, 0x62));
            g.fillRect(160, y, 64, 1);
            g.setColor(new Color(0x18, 0x1D, 0x20));
            g.fillRect(160, y + 2, 64, 2);
        }

        // 14. Hose Metal Camlock Couplings & Brass Suction Strainer (224, 112, 32, 64)
        fillNoisyRect(g, 224, 112, 32, 32, new Color(0xBD, 0x9B, 0x3A), 3, 1014L); // Brass strainer
        g.setColor(new Color(0x82, 0x67, 0x1A));
        g.drawRect(224, 112, 31, 31);
        // Strainer mesh holes
        g.setColor(new Color(0x3B, 0x2E, 0x0A));
        for (int y = 116; y < 140; y += 4) {
            for (int x = 228; x < 252; x += 4) {
                g.fillRect(x, y, 2, 2);
            }
        }
        // Silver Camlock Coupling (224, 144, 32, 32)
        fillNoisyRect(g, 224, 144, 32, 32, new Color(0xC0, 0xC6, 0xD0), 2, 1015L);
        g.setColor(new Color(0x60, 0x65, 0x70));
        g.drawRect(224, 144, 31, 31);
        g.setColor(new Color(0x40, 0x45, 0x4F));
        g.fillRect(226, 156, 28, 6);

        // 15. Base Skid Mounting Plate (0, 176, 96, 48)
        fillNoisyRect(g, 0, 176, 96, 48, new Color(0x28, 0x2B, 0x31), 3, 1016L);
        g.setColor(new Color(0x42, 0x47, 0x52));
        g.drawRect(0, 176, 95, 47);
        // Mounting bolt holes
        drawBolt(g, 10, 186);
        drawBolt(g, 84, 186);
        drawBolt(g, 10, 212);
        drawBolt(g, 84, 212);

        // 16. Engine Flywheel & Pulley (96, 176, 48, 48)
        fillNoisyRect(g, 96, 176, 48, 48, new Color(0x30, 0x34, 0x3C), 3, 1017L);
        g.setColor(new Color(0x60, 0x68, 0x75));
        g.drawOval(100, 180, 40, 40);
        g.setColor(new Color(0x18, 0x1A, 0x1E));
        g.fillOval(112, 192, 16, 16);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/fluid_pump.png"));
    }

    /**
     * Generates 256x256 GUI Texture for Fluid Pump Menu:
     * - Standard 176x166 GUI box
     * - RF Energy bar slot (12x52 at x: 10, y: 18)
     * - Fuel / Bucket input slot (18x18 at x: 80, y: 38)
     * - Bucket output slot (18x18 at x: 106, y: 38)
     * - Fluid Output Tank (16x52 at x: 150, y: 18)
     * - Status readout box (at x: 30, y: 18, w: 112, h: 16)
     * - Target Pos readout box (at x: 30, y: 60, w: 112, h: 12)
     */
    public static void generatePumpGui() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. GUI Background Body (176x166)
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0xC6, 0xC6, 0xC6), 1, 2001L);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 165);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.drawLine(1, 165, 175, 165);
        g.drawLine(175, 1, 175, 165);

        // 2. RF Energy Meter Slot (x: 10, y: 18, w: 14, h: 54)
        drawRecessedSlot(g, 9, 17, 14, 54);

        // 3. Fluid Tank Gauge Slot (x: 151, y: 18, w: 18, h: 54)
        drawRecessedSlot(g, 150, 17, 18, 54);

        // 4. Central Status Screen Box (x: 30, y: 17, w: 114, h: 16)
        g.setColor(new Color(0x18, 0x1B, 0x20));
        g.fillRect(30, 17, 114, 16);
        g.setColor(new Color(0x35, 0x3C, 0x47));
        g.drawRect(30, 17, 113, 15);

        // 5. Fuel Slot (x: 79, y: 36, w: 18, h: 18)
        drawItemSlot(g, 79, 36);

        // 6. Bucket Output Slot (x: 105, y: 36, w: 18, h: 18)
        drawItemSlot(g, 105, 36);

        // 7. Small arrow between fuel slot and bucket output
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(99, 44, 4, 2);
        g.drawLine(102, 43, 103, 44);
        g.drawLine(102, 46, 103, 45);

        // 8. Pumping Target Screen Box (x: 30, y: 58, w: 114, h: 13)
        g.setColor(new Color(0x10, 0x13, 0x17));
        g.fillRect(30, 58, 114, 13);
        g.setColor(new Color(0x2D, 0x34, 0x3E));
        g.drawRect(30, 58, 113, 12);

        // 9. Player Inventory Slots (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawItemSlot(g, 7 + col * 18, 83 + row * 18);
            }
        }

        // 10. Player Hotbar Slots (1 row of 9 at y: 142)
        for (int col = 0; col < 9; col++) {
            drawItemSlot(g, 7 + col * 18, 141);
        }

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/gui/fluid_pump.png"));
    }

    private static void drawItemSlot(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + 17, y);
        g.drawLine(x, y, x, y + 17);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x + 1, y + 17, x + 17, y + 17);
        g.drawLine(x + 17, y + 1, x + 17, y + 17);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(x + 1, y + 1, 16, 16);
    }

    private static void drawRecessedSlot(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
        g.setColor(new Color(0x15, 0x18, 0x1D));
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    public static void generateBlockAndItemTextures() throws Exception {
        // 16x16 Fluid Pump Block Textures for inventory/fallback rendering
        // Front
        BufferedImage front = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gf = front.createGraphics();
        fillNoisyRect(gf, 0, 0, 16, 16, new Color(0x25, 0x28, 0x2E), 2, 3001L);
        // Roll-cage outline
        gf.setColor(new Color(0x11, 0x12, 0x14));
        gf.drawRect(0, 0, 15, 15);
        gf.drawRect(1, 1, 13, 13);
        // Yellow engine starter on left
        gf.setColor(new Color(0xFA, 0xBC, 0x09));
        gf.fillRect(2, 6, 5, 6);
        gf.setColor(new Color(0x1F, 0x1E, 0x1C));
        gf.fillOval(3, 7, 3, 3);
        // White fuel tank on top
        gf.setColor(new Color(0xF2, 0xF4, 0xF6));
        gf.fillRect(2, 2, 6, 3);
        gf.setColor(new Color(0x22, 0x25, 0x2A));
        gf.fillRect(3, 3, 4, 1);
        // Silver pump on right
        gf.setColor(new Color(0xAE, 0xB4, 0xBC));
        gf.fillRect(8, 5, 6, 7);
        gf.setColor(new Color(0x35, 0x39, 0x40));
        gf.fillOval(9, 7, 4, 4); // Suction flange
        gf.setColor(new Color(0xB5, 0xBB, 0xC3));
        gf.fillRect(10, 2, 3, 3); // Top discharge pipe
        gf.dispose();
        ImageIO.write(front, "PNG", new File(ASSETS + "textures/block/fluid_pump_front.png"));

        // Side
        BufferedImage side = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = side.createGraphics();
        fillNoisyRect(gs, 0, 0, 16, 16, new Color(0x25, 0x28, 0x2E), 2, 3002L);
        gs.setColor(new Color(0x11, 0x12, 0x14));
        gs.drawRect(0, 0, 15, 15);
        gs.drawRect(1, 1, 13, 13);
        // Internal machinery side view
        gs.setColor(new Color(0x9E, 0xA3, 0xAA));
        gs.fillRect(3, 6, 10, 6);
        gs.setColor(new Color(0xF2, 0xF4, 0xF6));
        gs.fillRect(4, 2, 8, 3);
        gs.dispose();
        ImageIO.write(side, "PNG", new File(ASSETS + "textures/block/fluid_pump_side.png"));

        // Top
        BufferedImage top = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gt = top.createGraphics();
        fillNoisyRect(gt, 0, 0, 16, 16, new Color(0x25, 0x28, 0x2E), 2, 3003L);
        gt.setColor(new Color(0x11, 0x12, 0x14));
        gt.drawRect(0, 0, 15, 15);
        gt.drawRect(1, 1, 13, 13);
        // White gas tank top with chrome cap
        gt.setColor(new Color(0xF2, 0xF4, 0xF6));
        gt.fillRect(2, 2, 6, 12);
        gt.setColor(new Color(0xDC, 0xE0, 0xE5));
        gt.fillOval(3, 5, 4, 4);
        // Top discharge outlet flange
        gt.setColor(new Color(0xB5, 0xBB, 0xC3));
        gt.fillOval(9, 5, 5, 5);
        gt.setColor(new Color(0x18, 0x1A, 0x1D));
        gt.fillOval(10, 6, 3, 3);
        gt.dispose();
        ImageIO.write(top, "PNG", new File(ASSETS + "textures/block/fluid_pump_top.png"));

        // Bottom
        BufferedImage bottom = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gb = bottom.createGraphics();
        fillNoisyRect(gb, 0, 0, 16, 16, new Color(0x1A, 0x1C, 0x20), 2, 3004L);
        gb.setColor(new Color(0x11, 0x12, 0x14));
        gb.drawRect(0, 0, 15, 15);
        drawBolt(gb, 2, 2);
        drawBolt(gb, 12, 2);
        drawBolt(gb, 2, 12);
        drawBolt(gb, 12, 12);
        gb.dispose();
        ImageIO.write(bottom, "PNG", new File(ASSETS + "textures/block/fluid_pump_bottom.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // 1. Blockstate
        writeJson(ASSETS + "blockstates/fluid_pump.json", """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/fluid_pump" },
            "facing=south": { "model": "refinery:block/fluid_pump", "y": 180 },
            "facing=west":  { "model": "refinery:block/fluid_pump", "y": 270 },
            "facing=east":  { "model": "refinery:block/fluid_pump", "y": 90 }
          }
        }
        """);

        // 2. Block Model
        writeJson(ASSETS + "models/block/fluid_pump.json", """
        {
          "parent": "minecraft:block/cube",
          "textures": {
            "particle": "refinery:block/fluid_pump_front",
            "up": "refinery:block/fluid_pump_top",
            "down": "refinery:block/fluid_pump_bottom",
            "north": "refinery:block/fluid_pump_front",
            "south": "refinery:block/fluid_pump_side",
            "east": "refinery:block/fluid_pump_side",
            "west": "refinery:block/fluid_pump_side"
          }
        }
        """);

        // 3. Item Model
        writeJson(ASSETS + "models/item/fluid_pump.json", """
        {
          "parent": "refinery:block/fluid_pump"
        }
        """);

        // 4. Recipe
        writeJson(DATA + "recipe/fluid_pump.json", """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "B": "minecraft:bucket",
            "P": "minecraft:piston",
            "R": "minecraft:redstone",
            "S": "minecraft:smooth_stone"
          },
          "pattern": [
            "IBI",
            "IPI",
            "SRS"
          ],
          "result": {
            "count": 1,
            "id": "refinery:fluid_pump"
          }
        }
        """);
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int noise, long seed) {
        Random rand = new Random(seed);
        for (int py = y; py < y + h; py++) {
            for (int px = x; px < x + w; px++) {
                int delta = rand.nextInt(noise * 2 + 1) - noise;
                int r = Math.clamp(base.getRed() + delta, 0, 255);
                int gr = Math.clamp(base.getGreen() + delta, 0, 255);
                int b = Math.clamp(base.getBlue() + delta, 0, 255);
                g.setColor(new Color(r, gr, b, base.getAlpha()));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void drawBolt(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x6A, 0x72, 0x80));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x20, 0x24, 0x2A));
        g.fillRect(x + 1, y + 1, 1, 1);
    }

    private static void writeJson(String path, String content) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content.trim());
        }
    }
}
