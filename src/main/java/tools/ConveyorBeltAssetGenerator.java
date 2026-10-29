package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Generates custom textures and JSON assets for the Industrial Belt Conveyor System:
 * - Reference: Low-profile extruded aluminum conveyor with matte black industrial belt
 * - Seamless snapping in a row (tail & head rollers, open continuous joints)
 * - 256x256 entity model texture atlas
 * - 16x16 block particle textures & 32x32 item icon
 * - Blockstate, model, loot table, and recipe JSON files
 */
public class ConveyorBeltAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/entity").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generateConveyorModelAtlas();
        generateBlockAndItemTextures();
        generateJsonFiles();

        System.out.println("Conveyor Belt assets generated successfully!");
    }

    /**
     * Generates the 256x256 entity texture atlas for the monolithic 3D Conveyor Belt model:
     * - Region 1: Extruded Anodized Aluminum Side Rails with T-Slot Channel (0, 0, 128, 32)
     * - Region 2: Matte Black Heavy-Duty Industrial Conveyor Belt (0, 32, 128, 64)
     * - Region 3: Precision Brushed Stainless Steel Roller Cylinder (0, 96, 64, 32)
     * - Region 4: Machined Bearing Plates & End Caps with Hex Bolts (64, 96, 64, 32)
     * - Region 5: Galvanized Steel Slider Bed / Deck (128, 0, 64, 32)
     * - Region 6: Heavy Cast Foot Pads & Floor Mounts (128, 32, 32, 32)
     */
    public static void generateConveyorModelAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with industrial dark metal tone to prevent atlas edge bleed
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x22, 0x25, 0x2A), 3, 2026L);

        // =========================================================================
        // 1. EXTRUDED ANODIZED ALUMINUM SIDE RAILS (0, 0, 128, 32)
        // =========================================================================
        fillNoisyRect(g, 0, 0, 128, 32, new Color(0xB8, 0xC0, 0xC9), 5, 101L);
        // Brushed horizontal grain
        for (int y = 0; y < 32; y++) {
            int tone = 180 + ((y * 17) % 25);
            g.setColor(new Color(tone, tone + 4, tone + 8, 40));
            g.drawLine(0, y, 127, y);
        }
        // Top highlight lip
        g.setColor(new Color(0xE8, 0xEE, 0xF5));
        g.drawLine(0, 0, 127, 0);
        g.drawLine(0, 1, 127, 1);
        // Bottom shadow bevel
        g.setColor(new Color(0x7D, 0x85, 0x90));
        g.drawLine(0, 31, 127, 31);
        g.drawLine(0, 30, 127, 30);

        // Recessed Center T-Slot Channel along Y = 10..22 (height 12 px)
        g.setColor(new Color(0x40, 0x46, 0x4F));
        g.fillRect(0, 10, 128, 12);
        // Deep T-slot interior
        g.setColor(new Color(0x22, 0x26, 0x2C));
        g.fillRect(0, 13, 128, 6);
        // Center inner slot groove
        g.setColor(new Color(0x13, 0x15, 0x18));
        g.fillRect(0, 15, 128, 2);
        // T-slot upper and lower lips highlights & shadows
        g.setColor(new Color(0x6A, 0x72, 0x7D));
        g.drawLine(0, 10, 127, 10);
        g.setColor(new Color(0x2A, 0x2E, 0x33));
        g.drawLine(0, 12, 127, 12);
        g.setColor(new Color(0x2A, 0x2E, 0x33));
        g.drawLine(0, 19, 127, 19);
        g.setColor(new Color(0x9CA4AF));
        g.drawLine(0, 22, 127, 22);

        // Periodic structural bolt fasteners along the rail (every 32 px)
        for (int bx = 16; bx < 128; bx += 32) {
            drawHexBolt(g, bx, 5, 4, true);
            drawHexBolt(g, bx, 26, 4, true);
        }

        // =========================================================================
        // 2. MATTE BLACK INDUSTRIAL CONVEYOR BELT (0, 32, 128, 64)
        // =========================================================================
        fillNoisyRect(g, 0, 32, 128, 64, new Color(0x1A, 0x1C, 0x1E), 4, 303L);
        // Subtle transverse woven canvas / micro-grip ribs
        for (int y = 32; y < 96; y += 2) {
            g.setColor(new Color(0x24, 0x27, 0x2B));
            g.drawLine(0, y, 127, y);
        }
        // Fine longitudinal guide grooves
        for (int x = 0; x < 128; x += 4) {
            g.setColor(new Color(0x12, 0x13, 0x15));
            g.drawLine(x, 32, x, 95);
        }
        // Vulcanized chevron / heavy joint seams every 32 px along Y
        for (int sy = 32; sy < 96; sy += 32) {
            g.setColor(new Color(0x0C, 0x0D, 0x0E));
            g.drawLine(0, sy, 127, sy);
            g.setColor(new Color(0x35, 0x3A, 0x40));
            g.drawLine(0, sy + 1, 127, sy + 1);
            // Stitched seam texture
            for (int sx = 2; sx < 128; sx += 6) {
                g.setColor(new Color(0x4D, 0x54, 0x5D));
                g.fillRect(sx, sy, 3, 2);
            }
        }
        // Belt side borders
        g.setColor(new Color(0x11, 0x12, 0x14));
        g.drawRect(0, 32, 127, 63);

        // =========================================================================
        // 3. PRECISION STAINLESS STEEL ROLLER (0, 96, 64, 32)
        // =========================================================================
        fillNoisyRect(g, 0, 96, 64, 32, new Color(0xBD, 0xC5, 0xCE), 5, 404L);
        // Lathe cylindrical gradient highlight band across middle
        for (int y = 96; y < 128; y++) {
            float distFromCenter = Math.abs((y - 112) / 16.0f);
            int bright = (int) (60.0f * (1.0f - distFromCenter));
            int r = Math.min(255, 189 + bright);
            int gr = Math.min(255, 197 + bright);
            int b = Math.min(255, 206 + bright);
            g.setColor(new Color(r, gr, b));
            g.drawLine(0, y, 63, y);
        }
        // Radial index notches / turning grooves across the roller
        for (int rx = 0; rx < 64; rx += 8) {
            g.setColor(new Color(0x85, 0x8E, 0x99));
            g.drawLine(rx, 96, rx, 127);
            g.setColor(new Color(0xEB, 0xF1, 0xF7));
            g.drawLine(rx + 1, 96, rx + 1, 127);
        }
        // Top and bottom roller curvature shading
        g.setColor(new Color(0x72, 0x7B, 0x86));
        g.drawLine(0, 96, 63, 96);
        g.drawLine(0, 127, 63, 127);

        // =========================================================================
        // 4. BEARING PLATES & END CAPS WITH TENSIONER (64, 96, 64, 32)
        // =========================================================================
        fillNoisyRect(g, 64, 96, 64, 32, new Color(0x9E, 0xA7, 0xB2), 4, 505L);
        // Outer chamfered border
        g.setColor(new Color(0x56, 0x5D, 0x66));
        g.drawRect(64, 96, 63, 31);
        g.setColor(new Color(0xD2, 0xD8, 0xE0));
        g.drawRect(65, 97, 61, 29);

        // Horizontal tensioner adjustment slot (X = 76..116, Y = 108..116)
        g.setColor(new Color(0x28, 0x2C, 0x32));
        g.fillRect(76, 108, 40, 8);
        g.setColor(new Color(0x15, 0x17, 0x1A));
        g.fillRect(78, 110, 36, 4);
        // Threaded chrome tensioner rod inside slot
        for (int tx = 79; tx < 114; tx += 3) {
            g.setColor(new Color(0xDF, 0xE5, 0xEC));
            g.drawLine(tx, 110, tx, 113);
            g.setColor(new Color(0x5A, 0x62, 0x6D));
            g.drawLine(tx + 1, 110, tx + 1, 113);
        }
        // Center bearing block carriage
        g.setColor(new Color(0x78, 0x81, 0x8D));
        g.fillRect(92, 105, 12, 14);
        g.setColor(new Color(0x35, 0x3B, 0x43));
        g.drawRect(92, 105, 11, 13);
        // Axle end cap nut
        drawHexBolt(g, 97, 111, 5, true);

        // Corner mounting hex screws
        drawHexBolt(g, 68, 100, 3, false);
        drawHexBolt(g, 123, 100, 3, false);
        drawHexBolt(g, 68, 123, 3, false);
        drawHexBolt(g, 123, 123, 3, false);

        // =========================================================================
        // 5. GALVANIZED STEEL SLIDER BED (128, 0, 64, 32)
        // =========================================================================
        fillNoisyRect(g, 128, 0, 64, 32, new Color(0x4C, 0x53, 0x5C), 5, 606L);
        // Sheet metal spangle crystals
        Random spangleRand = new Random(707L);
        for (int i = 0; i < 40; i++) {
            int sx = 128 + spangleRand.nextInt(56);
            int sy = spangleRand.nextInt(26);
            int sw = 4 + spangleRand.nextInt(6);
            int sh = 3 + spangleRand.nextInt(5);
            int spangleTone = 60 + spangleRand.nextInt(40);
            g.setColor(new Color(spangleTone, spangleTone + 3, spangleTone + 7, 70));
            g.fillRect(sx, sy, sw, sh);
        }
        g.setColor(new Color(0x35, 0x3A, 0x41));
        g.drawRect(128, 0, 63, 31);

        // =========================================================================
        // 6. HEAVY CAST FOOT PADS / FLOOR BRACKETS (128, 32, 32, 32)
        // =========================================================================
        fillNoisyRect(g, 128, 32, 32, 32, new Color(0x2E, 0x32, 0x38), 3, 808L);
        g.setColor(new Color(0x1B, 0x1D, 0x20));
        g.drawRect(128, 32, 31, 31);
        g.setColor(new Color(0x4E, 0x54, 0x5D));
        g.drawRect(129, 33, 29, 29);
        // Anchor bolt hole in center
        g.setColor(new Color(0x14, 0x16, 0x18));
        g.fillOval(140, 44, 8, 8);
        drawHexBolt(g, 142, 46, 4, true);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/conveyor_belt.png"));
    }

    /**
     * Generates 16x16 block textures and 32x32 item icon.
     */
    public static void generateBlockAndItemTextures() throws Exception {
        // 1. Block Top Texture (Black belt with aluminum rail edges)
        BufferedImage top = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTop = top.createGraphics();
        fillNoisyRect(gTop, 0, 0, 16, 16, new Color(0x1B, 0x1D, 0x20), 3, 10L);
        // Side aluminum rail edges (columns 0..1 and 14..15)
        for (int y = 0; y < 16; y++) {
            gTop.setColor(new Color(0xC5, 0xCB, 0xD4));
            gTop.fillRect(0, y, 2, 1);
            gTop.fillRect(14, y, 2, 1);
            // Highlight rail inner edge
            gTop.setColor(new Color(0xE8, 0xEE, 0xF5));
            gTop.fillRect(1, y, 1, 1);
            gTop.fillRect(14, y, 1, 1);
            // Black belt texture with subtle horizontal lines
            if (y % 4 == 0) {
                gTop.setColor(new Color(0x28, 0x2B, 0x30));
                gTop.drawLine(2, y, 13, y);
            }
        }
        gTop.dispose();
        ImageIO.write(top, "PNG", new File(ASSETS + "textures/block/conveyor_belt_top.png"));

        // 2. Block Side Texture (Extruded aluminum rail with center T-slot)
        BufferedImage side = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSide = side.createGraphics();
        // Aluminum background
        fillNoisyRect(gSide, 0, 0, 16, 16, new Color(0xB8, 0xC0, 0xC9), 4, 20L);
        // Top lip highlight
        gSide.setColor(new Color(0xE8, 0xEE, 0xF5));
        gSide.drawLine(0, 0, 15, 0);
        // Bottom bevel
        gSide.setColor(new Color(0x7D, 0x85, 0x90));
        gSide.drawLine(0, 15, 15, 15);
        // Recessed T-slot in center (Y = 6..10)
        gSide.setColor(new Color(0x3B, 0x41, 0x49));
        gSide.fillRect(0, 6, 16, 5);
        gSide.setColor(new Color(0x1E, 0x21, 0x25));
        gSide.fillRect(0, 7, 16, 3);
        // Hex bolts at sides
        drawHexBolt(gSide, 3, 3, 2, true);
        drawHexBolt(gSide, 12, 3, 2, true);
        gSide.dispose();
        ImageIO.write(side, "PNG", new File(ASSETS + "textures/block/conveyor_belt_side.png"));

        // 3. Block Bottom Texture (Dark steel deck & mounts)
        BufferedImage bottom = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBottom = bottom.createGraphics();
        fillNoisyRect(gBottom, 0, 0, 16, 16, new Color(0x32, 0x36, 0x3C), 3, 30L);
        gBottom.setColor(new Color(0x1E, 0x20, 0x24));
        gBottom.drawRect(0, 0, 15, 15);
        gBottom.dispose();
        ImageIO.write(bottom, "PNG", new File(ASSETS + "textures/block/conveyor_belt_bottom.png"));

        // 4. High-Resolution 32x32 Isometric Item Icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gIcon = icon.createGraphics();
        gIcon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Isometric Conveyor Belt representation
        // Base shadow
        gIcon.setColor(new Color(0, 0, 0, 70));
        gIcon.fillOval(3, 22, 26, 7);

        // Aluminum frame side (left isometric skirt)
        int[] skirtX = {4, 18, 28, 14};
        int[] skirtY = {17, 24, 20, 13};
        gIcon.setColor(new Color(0x9E, 0xA7, 0xB2));
        gIcon.fillPolygon(skirtX, skirtY, 4);

        // T-slot streak along skirt
        gIcon.setColor(new Color(0x36, 0x3C, 0x44));
        gIcon.drawLine(5, 18, 17, 24);
        gIcon.drawLine(6, 19, 17, 24);

        // Matte Black Top Belt surface
        int[] beltX = {4, 18, 28, 14};
        int[] beltY = {14, 21, 13, 6};
        gIcon.setColor(new Color(0x1F, 0x22, 0x26));
        gIcon.fillPolygon(beltX, beltY, 4);
        // Belt ridges
        gIcon.setColor(new Color(0x2F, 0x33, 0x39));
        gIcon.drawLine(7, 13, 21, 7);
        gIcon.drawLine(10, 15, 23, 9);
        gIcon.drawLine(13, 17, 25, 11);

        // Sleek extruded aluminum rails
        // Left rail
        gIcon.setColor(new Color(0xD6, 0xDC, 0xE3));
        gIcon.drawLine(3, 15, 17, 22);
        gIcon.setColor(new Color(0xB5, 0xBC, 0xC6));
        gIcon.drawLine(3, 16, 17, 23);

        // Right rail
        gIcon.setColor(new Color(0xC0, 0xC7, 0xD1));
        gIcon.drawLine(14, 6, 28, 13);
        gIcon.setColor(new Color(0x8D, 0x94, 0x9E));
        gIcon.drawLine(14, 7, 28, 14);

        // End roller front cylinder highlight
        gIcon.setColor(new Color(0xEB, 0xF1, 0xF7));
        gIcon.drawLine(16, 23, 18, 24);
        gIcon.setColor(new Color(0x94, 0x9D, 0xA8));
        drawHexBolt(gIcon, 17, 23, 2, true);

        gIcon.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/conveyor_belt.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // Blockstate JSON (Facing horizontal)
        String blockstate = """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/conveyor_belt" },
            "facing=south": { "model": "refinery:block/conveyor_belt", "y": 180 },
            "facing=west":  { "model": "refinery:block/conveyor_belt", "y": 270 },
            "facing=east":  { "model": "refinery:block/conveyor_belt", "y": 90 }
          }
        }
        """;
        writeFile(ASSETS + "blockstates/conveyor_belt.json", blockstate);

        // Block Model JSON (Particle fallback)
        String blockModel = """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/conveyor_belt_side",
            "up": "refinery:block/conveyor_belt_top",
            "down": "refinery:block/conveyor_belt_bottom",
            "side": "refinery:block/conveyor_belt_side"
          },
          "elements": [
            {
              "from": [0, 0, 0],
              "to": [16, 5, 16],
              "faces": {
                "down":  { "texture": "#down", "cullface": "down" },
                "up":    { "texture": "#up" },
                "north": { "texture": "#side" },
                "south": { "texture": "#side" },
                "west":  { "texture": "#side" },
                "east":  { "texture": "#side" }
              }
            }
          ]
        }
        """;
        writeFile(ASSETS + "models/block/conveyor_belt.json", blockModel);

        // Item Model JSON
        String itemModel = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/conveyor_belt"
          }
        }
        """;
        writeFile(ASSETS + "models/item/conveyor_belt.json", itemModel);

        // Loot Table JSON
        String lootTable = """
        {
          "type": "minecraft:block",
          "pools": [
            {
              "bonus_rolls": 0.0,
              "entries": [
                {
                  "type": "minecraft:item",
                  "name": "refinery:conveyor_belt"
                }
              ],
              "rolls": 1.0
            }
          ]
        }
        """;
        writeFile(DATA + "loot_table/blocks/conveyor_belt.json", lootTable);

        // Recipe JSON (Steel / Iron + Tar / Leather + Redstone -> 4 Conveyor Belts)
        String recipe = """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "T": "refinery:tar",
            "R": "minecraft:redstone"
          },
          "pattern": [
            "TTT",
            "IRI",
            "III"
          ],
          "result": {
            "count": 4,
            "id": "refinery:conveyor_belt"
          }
        }
        """;
        writeFile(DATA + "recipe/conveyor_belt.json", recipe);
    }

    private static void drawHexBolt(Graphics2D g, int cx, int cy, int size, boolean chrome) {
        g.setColor(chrome ? new Color(0xEB, 0xF1, 0xF7) : new Color(0xBD, 0xC4, 0xCE));
        g.fillRect(cx - size / 2, cy - size / 2, size, size);
        g.setColor(new Color(0x3F, 0x45, 0x4D));
        g.drawRect(cx - size / 2, cy - size / 2, size - 1, size - 1);
        g.setColor(chrome ? new Color(0xFF, 0xFF, 0xFF) : new Color(0xE1, 0xE7, 0xEE));
        g.fillRect(cx - size / 4, cy - size / 4, Math.max(1, size / 2), Math.max(1, size / 2));
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int variation, long seed) {
        Random rand = new Random(seed);
        for (int py = y; py < y + h; py++) {
            for (int px = x; px < x + w; px++) {
                int delta = rand.nextInt(variation * 2 + 1) - variation;
                int r = Math.clamp(base.getRed() + delta, 0, 255);
                int gr = Math.clamp(base.getGreen() + delta, 0, 255);
                int b = Math.clamp(base.getBlue() + delta, 0, 255);
                g.setColor(new Color(r, gr, b));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content.stripLeading());
        }
    }
}
