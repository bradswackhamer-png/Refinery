package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Generates custom textures and JSON assets for the Industrial Livestock Feeding Trough:
 * - Reference: 2-block-wide weathered galvanized sheet steel livestock feed bunk
 * - Formed trapezoidal trough with rolled safety lips, formed end plates, and cross-tie divider bars
 * - Dynamic golden straw / hay texture that fills the trough as food is stocked
 * - 256x256 entity model texture atlas
 * - 256x256 GUI container interface texture
 * - 16x16 block particle textures & 32x32 item icon
 * - Blockstate, model, loot table, and recipe JSON files
 */
public class FeedingTroughAssetGenerator {

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

        generateTroughModelAtlas();
        generateTroughGui();
        generateBlockAndItemTextures();
        generateJsonFiles();

        System.out.println("Feeding Trough assets generated successfully!");
    }

    /**
     * Generates 256x256 entity texture atlas for the monolithic 3D Feeding Trough:
     * - Region 1: Weathered Galvanized Steel Sidewalls with Zinc Spangle (0, 0, 128, 64)
     * - Region 2: Rolled Top Safety Rim Flange & Brackets (0, 64, 128, 32)
     * - Region 3: Formed Galvanized End Bulkhead Plates & Skid Feet (128, 0, 64, 64)
     * - Region 4: Steel Cross-Tie Divider Struts & Rivets (128, 64, 64, 32)
     * - Region 5: Golden Chopped Hay & Agricultural Straw Feed (0, 128, 128, 128)
     */
    public static void generateTroughModelAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with neutral industrial slate
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x73, 0x7B, 0x85), 3, 3001L);

        // =========================================================================
        // 1. WEATHERED GALVANIZED STEEL SIDEWALL (0, 0, 128, 64)
        // =========================================================================
        fillNoisyRect(g, 0, 0, 128, 64, new Color(0x9E, 0xA7, 0xB0), 5, 101L);
        // Zinc spangle crystalline flakes
        Random spangleRand = new Random(42L);
        for (int i = 0; i < 90; i++) {
            int sx = spangleRand.nextInt(120);
            int sy = spangleRand.nextInt(56);
            int sw = 4 + spangleRand.nextInt(8);
            int sh = 3 + spangleRand.nextInt(6);
            int spangleTone = 145 + spangleRand.nextInt(45);
            g.setColor(new Color(spangleTone, spangleTone + 4, spangleTone + 8, 85));
            g.fillRect(sx, sy, sw, sh);
        }
        // Top edge shadow and bottom fold shadow
        g.setColor(new Color(0x5A, 0x62, 0x6D));
        g.drawLine(0, 0, 127, 0);
        g.drawLine(0, 63, 127, 63);
        g.setColor(new Color(0xD8, 0xE0, 0xE8));
        g.drawLine(0, 1, 127, 1);

        // Subtle vertical sheet-metal bend lines
        for (int bx = 16; bx < 128; bx += 32) {
            g.setColor(new Color(0x84, 0x8D, 0x96));
            g.drawLine(bx, 0, bx, 63);
            g.setColor(new Color(0xB5, 0xBD, 0xC6));
            g.drawLine(bx + 1, 0, bx + 1, 63);
        }

        // =========================================================================
        // 2. ROLLED TOP SAFETY RIM FLANGE & CLAMP BARS (0, 64, 128, 32)
        // =========================================================================
        fillNoisyRect(g, 0, 64, 128, 32, new Color(0xB2, 0xBB, 0xC4), 4, 202L);
        // Cylindrical rolled flange highlight band across middle (Y = 76..80)
        g.setColor(new Color(0xE8, 0xF0, 0xF8));
        g.fillRect(0, 74, 128, 3);
        g.setColor(new Color(0x68, 0x70, 0x7B));
        g.drawLine(0, 64, 127, 64);
        g.drawLine(0, 95, 127, 95);

        // Flange bracket rivets
        for (int rx = 8; rx < 128; rx += 24) {
            drawRivet(g, rx, 84, 3);
        }

        // =========================================================================
        // 3. FORMED END BULKHEAD PLATES & SKID FEET (128, 0, 64, 64)
        // =========================================================================
        fillNoisyRect(g, 128, 0, 64, 64, new Color(0x8D, 0x95, 0x9E), 4, 303L);
        // Outer formed perimeter flange
        g.setColor(new Color(0x52, 0x59, 0x63));
        g.drawRect(128, 0, 63, 63);
        g.setColor(new Color(0xC0, 0xC8, 0xD2));
        g.drawRect(129, 1, 61, 61);

        // Bottom cleanout / drainage notch (X = 152..168, Y = 52..63)
        g.setColor(new Color(0x28, 0x2C, 0x32));
        g.fillRect(150, 50, 20, 14);
        g.setColor(new Color(0x18, 0x1A, 0x1E));
        g.fillRect(152, 53, 16, 11);
        g.setColor(new Color(0x60, 0x68, 0x72));
        g.drawRect(150, 50, 20, 14);

        // Skid feet mounting bolts
        drawRivet(g, 136, 56, 3);
        drawRivet(g, 184, 56, 3);
        drawRivet(g, 136, 12, 3);
        drawRivet(g, 184, 12, 3);

        // =========================================================================
        // 4. STEEL CROSS-TIE DIVIDER STRUTS (128, 64, 64, 32)
        // =========================================================================
        fillNoisyRect(g, 128, 64, 64, 32, new Color(0x6E, 0x76, 0x80), 4, 404L);
        // Strut strap bevels
        g.setColor(new Color(0x9CA4AE));
        g.drawLine(128, 64, 191, 64);
        g.setColor(new Color(0x42, 0x48, 0x50));
        g.drawLine(128, 95, 191, 95);
        // Strut clamp wrap-around ends
        g.setColor(new Color(0x3B, 0x41, 0x48));
        g.fillRect(128, 70, 8, 16);
        g.fillRect(184, 70, 8, 16);
        drawRivet(g, 132, 78, 3);
        drawRivet(g, 188, 78, 3);

        // =========================================================================
        // 5. GOLDEN CHOPPED HAY & AGRICULTURAL STRAW (0, 128, 128, 128)
        // =========================================================================
        // Rich warm golden-ochre straw bed base `#C9933B`
        fillNoisyRect(g, 0, 128, 128, 128, new Color(0xBA, 0x88, 0x36), 6, 505L);

        // Individual textured straw stalks and grass fibers
        Random strawRand = new Random(777L);
        for (int i = 0; i < 450; i++) {
            int hx = strawRand.nextInt(120);
            int hy = 128 + strawRand.nextInt(120);
            int len = 6 + strawRand.nextInt(14);
            int angleType = strawRand.nextInt(4);

            int tone = strawRand.nextInt(3);
            Color strawColor = switch (tone) {
                case 0 -> new Color(0xE8, 0xB7, 0x4D); // Bright golden stalk
                case 1 -> new Color(0xD4, 0x9C, 0x3A); // Medium ripe straw
                default -> new Color(0x8A, 0x62, 0x22); // Earthy dried stem shadow
            };
            g.setColor(strawColor);

            int dx = (angleType == 0 || angleType == 1) ? len : (len / 2);
            int dy = (angleType == 1 || angleType == 2) ? (len / 3) : -(len / 4);
            g.drawLine(hx, hy, Math.min(127, hx + dx), Math.min(255, Math.max(128, hy + dy)));
        }

        // Scattered cereal grain pellets / seeds (`#E5C37A` and `#5C4018`)
        for (int i = 0; i < 120; i++) {
            int gx = strawRand.nextInt(124);
            int gy = 128 + strawRand.nextInt(124);
            g.setColor(new Color(0x42, 0x2E, 0x14));
            g.fillRect(gx, gy, 3, 2);
            g.setColor(new Color(0xFA, 0xD4, 0x84));
            g.fillRect(gx + 1, gy, 2, 1);
        }

        // Hay layer surface edge highlights
        g.setColor(new Color(0xF2, 0xC9, 0x6A));
        g.drawLine(0, 128, 127, 128);
        g.setColor(new Color(0x66, 0x48, 0x1A));
        g.drawLine(0, 255, 127, 255);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/feeding_trough.png"));
    }

    /**
     * Generates a 256x256 GUI screen texture for the Feeding Trough:
     * - 9-slot feed hopper grid (accepts wheat, carrots, potatoes, seeds, etc.)
     * - Live feed capacity gauge
     * - Active animal breeding range indicator
     */
    public static void generateTroughGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Standard Minecraft 176x166 GUI frame
        int w = 176;
        int h = 166;
        fillNoisyRect(g, 0, 0, w, h, new Color(0xC6, 0xC6, 0xC6), 2, 888L);

        // Outer beveled borders
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);

        // Title and Status Banners
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(7, 14, 168, 14);

        // 3x3 Feed Slot Grid (centered at x = 62, y = 20)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                drawSlot(g, 61 + c * 18, 19 + r * 18);
            }
        }

        // Left Feed Level Indicator Panel (x = 16, y = 20, w = 24, h = 52)
        drawRecessedPanel(g, 16, 20, 24, 52);
        // Golden straw icon badge inside left panel
        g.setColor(new Color(0xD4, 0x9C, 0x3A));
        g.fillRect(19, 23, 18, 46);
        g.setColor(new Color(0xF2, 0xC9, 0x6A));
        for (int y = 25; y < 68; y += 4) {
            g.drawLine(20, y, 35, y);
        }

        // Right Breeding Radar Indicator Panel (x = 136, y = 20, w = 24, h = 52)
        drawRecessedPanel(g, 136, 20, 24, 52);
        g.setColor(new Color(0x22, 0x8B, 0x22));
        g.fillRect(139, 23, 18, 46);
        // Concentric radar heart rings
        g.setColor(new Color(0x4C, 0xAF, 0x50));
        g.drawOval(141, 35, 14, 14);
        g.drawOval(144, 38, 8, 8);
        g.setColor(new Color(0x81, 0xC7, 0x84));
        g.fillRect(147, 41, 2, 2);

        // Player Inventory Slots (3 rows of 9 at y = 84)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                drawSlot(g, 7 + c * 18, 83 + r * 18);
            }
        }

        // Player Hotbar Slots (1 row of 9 at y = 142)
        for (int c = 0; c < 9; c++) {
            drawSlot(g, 7 + c * 18, 141);
        }

        g.dispose();
        ImageIO.write(gui, "PNG", new File(ASSETS + "textures/gui/feeding_trough.png"));
    }

    /**
     * Generates 16x16 block textures and 32x32 isometric item icon.
     */
    public static void generateBlockAndItemTextures() throws Exception {
        // 1. Block Side Texture (Weathered galvanized steel with rolled rim)
        BufferedImage side = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSide = side.createGraphics();
        fillNoisyRect(gSide, 0, 0, 16, 16, new Color(0x9E, 0xA7, 0xB0), 4, 10L);
        // Rolled top rim
        gSide.setColor(new Color(0xE8, 0xF0, 0xF8));
        gSide.drawLine(0, 0, 15, 0);
        gSide.setColor(new Color(0xB2, 0xBB, 0xC4));
        gSide.drawLine(0, 1, 15, 1);
        gSide.setColor(new Color(0x60, 0x68, 0x72));
        gSide.drawLine(0, 2, 15, 2);
        // Bottom ground flange
        gSide.setColor(new Color(0x56, 0x5D, 0x66));
        gSide.drawLine(0, 15, 15, 15);
        gSide.dispose();
        ImageIO.write(side, "PNG", new File(ASSETS + "textures/block/feeding_trough_side.png"));

        // 2. Block Top Texture (Galvanized rim with golden straw interior)
        BufferedImage top = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTop = top.createGraphics();
        fillNoisyRect(gTop, 0, 0, 16, 16, new Color(0xBA, 0x88, 0x36), 5, 20L);
        // Straw flecks
        for (int y = 0; y < 16; y++) {
            if (y % 3 == 0) {
                gTop.setColor(new Color(0xE8, 0xB7, 0x4D));
                gTop.drawLine(2, y, 13, y);
            }
        }
        // Galvanized steel border rims
        gTop.setColor(new Color(0xB2, 0xBB, 0xC4));
        gTop.fillRect(0, 0, 2, 16);
        gTop.fillRect(14, 0, 2, 16);
        gTop.setColor(new Color(0xE8, 0xF0, 0xF8));
        gTop.drawLine(1, 0, 1, 15);
        gTop.drawLine(14, 0, 14, 15);
        gTop.dispose();
        ImageIO.write(top, "PNG", new File(ASSETS + "textures/block/feeding_trough_top.png"));

        // 3. Block Bottom Texture
        BufferedImage bottom = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBottom = bottom.createGraphics();
        fillNoisyRect(gBottom, 0, 0, 16, 16, new Color(0x6A, 0x72, 0x7D), 3, 30L);
        gBottom.dispose();
        ImageIO.write(bottom, "PNG", new File(ASSETS + "textures/block/feeding_trough_bottom.png"));

        // 4. 32x32 Isometric Item Icon (Galvanized Trough filled with Golden Hay)
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gIcon = icon.createGraphics();
        gIcon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Drop shadow
        gIcon.setColor(new Color(0, 0, 0, 80));
        gIcon.fillOval(2, 22, 28, 8);

        // Sloped Galvanized Trough Front Left Skirt
        int[] skirtX = {2, 18, 30, 14};
        int[] skirtY = {16, 25, 20, 11};
        gIcon.setColor(new Color(0x8D, 0x95, 0x9E));
        gIcon.fillPolygon(skirtX, skirtY, 4);

        // End Bulkhead Left Face
        int[] endX = {2, 2, 7, 7};
        int[] endY = {16, 23, 26, 19};
        gIcon.setColor(new Color(0x6E, 0x76, 0x80));
        gIcon.fillPolygon(endX, endY, 4);

        // Golden Hay Mound Interior
        int[] hayX = {3, 18, 29, 14};
        int[] hayY = {14, 23, 18, 9};
        gIcon.setColor(new Color(0xD4, 0x9C, 0x3A));
        gIcon.fillPolygon(hayX, hayY, 4);
        // Golden straw stalks across hay mound
        gIcon.setColor(new Color(0xFA, 0xD4, 0x84));
        gIcon.drawLine(6, 13, 19, 20);
        gIcon.drawLine(10, 15, 24, 18);
        gIcon.drawLine(14, 13, 27, 15);

        // Rolled Galvanized Rims
        gIcon.setColor(new Color(0xE8, 0xF0, 0xF8));
        gIcon.drawLine(2, 14, 18, 23);
        gIcon.drawLine(14, 9, 29, 18);
        gIcon.setColor(new Color(0xA8, 0xB1, 0xBA));
        gIcon.drawLine(2, 15, 18, 24);

        // Cross-Tie Division Struts across rim
        gIcon.setColor(new Color(0x4A, 0x51, 0x59));
        gIcon.drawLine(8, 12, 11, 19);
        gIcon.drawLine(16, 14, 20, 21);
        gIcon.drawLine(23, 16, 26, 22);

        gIcon.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/feeding_trough.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // Blockstate JSON (Facing horizontal + BedPart HEAD / FOOT)
        String blockstate = """
        {
          "variants": {
            "facing=north,part=foot": { "model": "refinery:block/feeding_trough" },
            "facing=south,part=foot": { "model": "refinery:block/feeding_trough", "y": 180 },
            "facing=west,part=foot":  { "model": "refinery:block/feeding_trough", "y": 270 },
            "facing=east,part=foot":  { "model": "refinery:block/feeding_trough", "y": 90 },
            "facing=north,part=head": { "model": "refinery:block/feeding_trough" },
            "facing=south,part=head": { "model": "refinery:block/feeding_trough", "y": 180 },
            "facing=west,part=head":  { "model": "refinery:block/feeding_trough", "y": 270 },
            "facing=east,part=head":  { "model": "refinery:block/feeding_trough", "y": 90 }
          }
        }
        """;
        writeFile(ASSETS + "blockstates/feeding_trough.json", blockstate);

        // Block Model JSON (Fallback particles)
        String blockModel = """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/feeding_trough_side",
            "up": "refinery:block/feeding_trough_top",
            "down": "refinery:block/feeding_trough_bottom",
            "side": "refinery:block/feeding_trough_side"
          },
          "elements": [
            {
              "from": [1, 0, 0],
              "to": [15, 8, 16],
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
        writeFile(ASSETS + "models/block/feeding_trough.json", blockModel);

        // Item Model JSON
        String itemModel = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/feeding_trough"
          }
        }
        """;
        writeFile(ASSETS + "models/item/feeding_trough.json", itemModel);

        // Modern 26.3 Item Definition JSON
        String itemDef = """
        {
          "model": {
            "type": "minecraft:model",
            "model": "refinery:item/feeding_trough"
          }
        }
        """;
        writeFile(ASSETS + "items/feeding_trough.json", itemDef);

        // Loot Table JSON (Only drops when FOOT is broken)
        String lootTable = """
        {
          "type": "minecraft:block",
          "pools": [
            {
              "bonus_rolls": 0.0,
              "conditions": [
                {
                  "condition": "minecraft:block_state_property",
                  "block": "refinery:feeding_trough",
                  "properties": {
                    "part": "foot"
                  }
                }
              ],
              "entries": [
                {
                  "type": "minecraft:item",
                  "name": "refinery:feeding_trough"
                }
              ],
              "rolls": 1.0
            }
          ]
        }
        """;
        writeFile(DATA + "loot_table/blocks/feeding_trough.json", lootTable);

        // Recipe JSON (Iron Ingots / Steel Plates + Hay Bale -> 1 Feeding Trough)
        String recipe = """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "H": "minecraft:hay_block"
          },
          "pattern": [
            "I I",
            "IHI",
            "III"
          ],
          "result": {
            "count": 1,
            "id": "refinery:feeding_trough"
          }
        }
        """;
        writeFile(DATA + "recipe/feeding_trough.json", recipe);
    }

    private static void drawSlot(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + 17, y);
        g.drawLine(x, y, x, y + 17);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x + 17, y, x + 17, y + 17);
        g.drawLine(x, y + 17, x + 17, y + 17);
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.fillRect(x + 1, y + 1, 16, 16);
    }

    private static void drawRecessedPanel(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    private static void drawRivet(Graphics2D g, int cx, int cy, int size) {
        g.setColor(new Color(0x42, 0x48, 0x50));
        g.fillRect(cx - size / 2, cy - size / 2, size, size);
        g.setColor(new Color(0xD2, 0xDA, 0xE2));
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
