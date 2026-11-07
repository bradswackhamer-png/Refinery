package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Generates custom textures and JSON assets for the Fire Hydrant (refinery:fire_hydrant):
 * - AWWA Safety Yellow Enamel Fire Hydrant matching reference image
 * - 256x256 entity model texture atlas (safety yellow body, embossed AWWA emblem, B-41 front cap,
 *   black cast base flange, polished chrome bolts/nuts, brass top operating stem nut)
 * - 16x16 block textures (top, bottom, side)
 * - 32x32 high-detail item icon texture
 * - Blockstate, item definition (26.3+), models, loot table, and recipe JSON files
 */
public class FireHydrantAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/entity").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "recipe").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();

        generateHydrantModelAtlas();
        generateBlockAndItemTextures();
        generateJsonFiles();

        System.out.println("Fire Hydrant assets generated successfully!");
    }

    /**
     * Atlas layout (256x256):
     * - (0, 0, 64, 64)    : Gloss Safety Yellow Barrel Paint (with subtle cast iron stipple)
     * - (64, 0, 64, 64)   : Front Lower Barrel with embossed 'A' circle logo and '250 AWWA' text
     * - (128, 0, 64, 64)  : Front Pumper Cap face with circular bevel & pentagonal 'B 41' nut
     * - (192, 0, 32, 32)  : Polished Silver / Chrome Hex Bolt Heads
     * - (224, 0, 32, 32)  : Threaded Steel Bolt Shanks & Nuts
     * - (0, 64, 64, 32)   : Safety Yellow Flange Rim (Lower & Bonnet Flanges)
     * - (64, 64, 64, 32)  : Matte Black Cast-Iron Base Flange
     * - (128, 64, 64, 32) : Brass / Bronze Top Pentagonal Operating Stem Nut
     * - (192, 32, 64, 32) : Dark Graphite / Black Thrust Collar & Gasket
     * - (0, 96, 64, 64)   : Safety Yellow Domed Bonnet Top & Side Nozzles
     */
    public static void generateHydrantModelAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with solid safety yellow to prevent any transparent mipmap bleed
        g.setColor(new Color(0xF0, 0xC8, 0x14));
        g.fillRect(0, 0, 256, 256);

        // 1. Gloss Safety Yellow Barrel Paint (0, 0, 64, 64)
        fillNoisyRect(g, 0, 0, 64, 64, new Color(0xF2, 0xCA, 0x16), 5, 4001L);
        // Vertical cylindrical specular highlight streak
        g.setColor(new Color(0xFF, 0xDE, 0x38));
        g.fillRect(14, 0, 10, 64);
        g.setColor(new Color(0xFF, 0xEA, 0x62));
        g.fillRect(17, 0, 4, 64);
        // Subtle side shading for cylindrical depth
        g.setColor(new Color(0xD9, 0xB0, 0x0E));
        g.fillRect(0, 0, 6, 64);
        g.fillRect(54, 0, 10, 64);
        g.setColor(new Color(0xC2, 0x9A, 0x0A));
        g.fillRect(60, 0, 4, 64);

        // 2. Front Lower Barrel with Embossed 'A' Logo & '250 AWWA' (64, 0, 64, 64)
        fillNoisyRect(g, 64, 0, 64, 64, new Color(0xF2, 0xCA, 0x16), 5, 4002L);
        g.setColor(new Color(0xFF, 0xDE, 0x38));
        g.fillRect(78, 0, 10, 64);
        // Embossed circular emblem in center (cx=96, cy=24, r=13)
        g.setColor(new Color(0xC4, 0x9C, 0x0A));
        g.drawOval(83, 11, 26, 26);
        g.setColor(new Color(0xFF, 0xE8, 0x58));
        g.drawOval(82, 10, 26, 26);
        // Stylized 'A' inside circle
        g.setColor(new Color(0xCC, 0xA2, 0x0C));
        int[] ax = {96, 88, 92, 96, 100, 104};
        int[] ay = {14, 32, 32, 22, 32, 32};
        g.fillPolygon(ax, ay, 6);
        g.setColor(new Color(0xFF, 0xE6, 0x50));
        g.drawLine(95, 14, 88, 31);
        g.fillOval(94, 24, 4, 4);
        // Embossed '250' and 'AWWA' markings below circle
        g.setColor(new Color(0xBA, 0x92, 0x08));
        // '250' row (y=43..47)
        g.fillRect(90, 43, 3, 5);
        g.fillRect(95, 43, 3, 5);
        g.fillRect(100, 43, 3, 5);
        // 'AWWA' row (y=51..56)
        for (int wx = 84; wx <= 104; wx += 6) {
            g.fillRect(wx, 51, 4, 6);
            g.setColor(new Color(0xFF, 0xE4, 0x4C));
            g.fillRect(wx, 50, 4, 1);
            g.setColor(new Color(0xBA, 0x92, 0x08));
        }

        // 3. Front Pumper Cap Face & 'B 41' Pentagonal Nut (128, 0, 64, 64)
        fillNoisyRect(g, 128, 0, 64, 64, new Color(0xF5, 0xCE, 0x1A), 4, 4003L);
        // Outer circular cap bevel
        g.setColor(new Color(0xFF, 0xE8, 0x54));
        g.drawOval(132, 4, 56, 56);
        g.setColor(new Color(0xC2, 0x9A, 0x0A));
        g.drawOval(133, 5, 54, 54);
        // Center pentagonal nut face
        g.setColor(new Color(0xE8, 0xBE, 0x12));
        g.fillRect(146, 18, 28, 28);
        g.setColor(new Color(0xFF, 0xEA, 0x60));
        g.drawRect(146, 18, 27, 27);
        // 'B' and '41' stamped on nut
        g.setColor(new Color(0xAC, 0x86, 0x06));
        g.fillRect(157, 22, 6, 7); // B
        g.fillRect(154, 33, 5, 7); // 4
        g.fillRect(162, 33, 3, 7); // 1

        // 4. Polished Silver / Chrome Hex Bolt Heads (192, 0, 32, 32)
        fillNoisyRect(g, 192, 0, 32, 32, new Color(0xDC, 0xE0, 0xE8), 4, 4004L);
        g.setColor(new Color(0xF8, 0xFA, 0xFF));
        g.fillRect(194, 2, 28, 6);
        g.drawRect(194, 2, 27, 27);
        g.setColor(new Color(0x88, 0x90, 0x9C));
        g.drawRect(192, 0, 31, 31);
        g.fillRect(192, 26, 32, 6);
        g.setColor(Color.WHITE);
        g.fillRect(198, 6, 8, 6);

        // 5. Threaded Steel Bolt Shanks & Hex Nuts (224, 0, 32, 32)
        fillNoisyRect(g, 224, 0, 32, 32, new Color(0xAC, 0xB4, 0xC0), 4, 4005L);
        for (int ty = 2; ty < 32; ty += 4) {
            g.setColor(new Color(0x70, 0x78, 0x84));
            g.fillRect(224, ty, 32, 2);
            g.setColor(new Color(0xD8, 0xE0, 0xEC));
            g.fillRect(224, ty + 2, 32, 2);
        }

        // 6. Safety Yellow Flange Rim (0, 64, 64, 32)
        fillNoisyRect(g, 0, 64, 64, 32, new Color(0xF0, 0xC6, 0x14), 4, 4006L);
        g.setColor(new Color(0xFF, 0xE6, 0x4E));
        g.fillRect(0, 64, 64, 4);
        g.setColor(new Color(0xB8, 0x90, 0x08));
        g.fillRect(0, 92, 64, 4);

        // 7. Matte Black Cast-Iron Base Flange (64, 64, 64, 32)
        fillNoisyRect(g, 64, 64, 64, 32, new Color(0x22, 0x24, 0x28), 4, 4007L);
        g.setColor(new Color(0x38, 0x3C, 0x42));
        g.fillRect(64, 64, 64, 3);
        g.setColor(new Color(0x12, 0x13, 0x16));
        g.fillRect(64, 93, 64, 3);

        // 8. Brass / Bronze Top Pentagonal Operating Stem Nut (128, 64, 64, 32)
        fillNoisyRect(g, 128, 64, 64, 32, new Color(0xBA, 0x9C, 0x42), 5, 4008L);
        // Metallic brass facets
        g.setColor(new Color(0xDC, 0xC0, 0x62));
        g.fillRect(128, 64, 24, 32);
        g.setColor(new Color(0xEE, 0xD6, 0x7A));
        g.fillRect(134, 64, 8, 32);
        g.setColor(new Color(0x8E, 0x74, 0x28));
        g.fillRect(172, 64, 20, 32);

        // 9. Dark Graphite Thrust Collar & Flange Gasket (192, 32, 64, 32)
        fillNoisyRect(g, 192, 32, 64, 32, new Color(0x28, 0x2A, 0x2E), 3, 4009L);
        g.setColor(new Color(0x40, 0x44, 0x4A));
        g.fillRect(192, 33, 64, 3);
        g.setColor(new Color(0x16, 0x17, 0x1A));
        g.fillRect(192, 60, 64, 4);

        // 10. Safety Yellow Domed Bonnet Top & Side Nozzles (0, 96, 64, 64)
        fillNoisyRect(g, 0, 96, 64, 64, new Color(0xF5, 0xCE, 0x18), 5, 4010L);
        // Gloss dome highlight
        g.setColor(new Color(0xFF, 0xE6, 0x4C));
        g.fillRect(0, 98, 64, 8);
        g.setColor(new Color(0xFF, 0xF2, 0x78));
        g.fillRect(0, 100, 64, 3);
        // Ribbed nozzle cap groove lines
        for (int rx = 6; rx < 64; rx += 10) {
            g.setColor(new Color(0xC8, 0xA0, 0x0A));
            g.fillRect(rx, 96, 2, 64);
            g.setColor(new Color(0xFF, 0xE2, 0x42));
            g.fillRect(rx + 2, 96, 2, 64);
        }
        g.setColor(new Color(0xBA, 0x92, 0x08));
        g.fillRect(0, 154, 64, 6);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/fire_hydrant.png"));
    }

    public static void generateBlockAndItemTextures() throws Exception {
        // 1. Block Top (Domed Yellow Bonnet & Brass Operating Nut) 16x16
        BufferedImage blockTop = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTop = blockTop.createGraphics();
        fillNoisyRect(gTop, 0, 0, 16, 16, new Color(0xF0, 0xC8, 0x14), 5, 410L);
        gTop.setColor(new Color(0xC4, 0x9C, 0x0A));
        gTop.drawRect(0, 0, 15, 15);
        // Dark thrust collar ring
        gTop.setColor(new Color(0x28, 0x2A, 0x2E));
        gTop.fillOval(4, 4, 8, 8);
        // Brass center operating nut
        gTop.setColor(new Color(0xD4, 0xB6, 0x54));
        gTop.fillRect(6, 6, 4, 4);
        gTop.setColor(new Color(0xF0, 0xD8, 0x78));
        gTop.fillRect(6, 6, 2, 2);
        gTop.dispose();
        ImageIO.write(blockTop, "PNG", new File(ASSETS + "textures/block/fire_hydrant_top.png"));

        // 2. Block Bottom (Black Cast-Iron Base Flange & Pipe Inlet) 16x16
        BufferedImage blockBottom = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBot = blockBottom.createGraphics();
        fillNoisyRect(gBot, 0, 0, 16, 16, new Color(0x22, 0x24, 0x28), 4, 411L);
        // Pipe inlet orifice
        gBot.setColor(new Color(0x12, 0x13, 0x16));
        gBot.fillOval(4, 4, 8, 8);
        // Silver perimeter bolts
        gBot.setColor(new Color(0xD8, 0xE0, 0xEA));
        gBot.fillRect(2, 2, 2, 2);
        gBot.fillRect(12, 2, 2, 2);
        gBot.fillRect(2, 12, 2, 2);
        gBot.fillRect(12, 12, 2, 2);
        gBot.dispose();
        ImageIO.write(blockBottom, "PNG", new File(ASSETS + "textures/block/fire_hydrant_bottom.png"));

        // 3. Block Side (Safety Yellow Hydrant Profile) 16x16
        BufferedImage blockSide = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSide = blockSide.createGraphics();
        fillNoisyRect(gSide, 0, 0, 16, 16, new Color(0xF2, 0xCA, 0x16), 5, 412L);
        // Top brass nut hint
        gSide.setColor(new Color(0xC8, 0xA8, 0x48));
        gSide.fillRect(6, 0, 4, 2);
        // Upper bonnet flange
        gSide.setColor(new Color(0xFF, 0xE4, 0x48));
        gSide.fillRect(1, 3, 14, 2);
        // Center pumper nozzle cap
        gSide.setColor(new Color(0xC2, 0x9A, 0x0A));
        gSide.drawOval(4, 6, 8, 6);
        gSide.setColor(new Color(0xFF, 0xE6, 0x52));
        gSide.fillRect(7, 8, 2, 2);
        // Bottom black flange
        gSide.setColor(new Color(0x22, 0x24, 0x28));
        gSide.fillRect(1, 14, 14, 2);
        gSide.dispose();
        ImageIO.write(blockSide, "PNG", new File(ASSETS + "textures/block/fire_hydrant_side.png"));

        // 4. 32x32 Item Icon (Detailed Pixel-Art Render of the Yellow Fire Hydrant)
        BufferedImage itemImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gItem = itemImg.createGraphics();
        gItem.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Clear background
        gItem.setComposite(AlphaComposite.Clear);
        gItem.fillRect(0, 0, 32, 32);
        gItem.setComposite(AlphaComposite.SrcOver);

        // Top Brass Operating Stem Nut (x: 14..17, y: 1..3)
        gItem.setColor(new Color(0x9E, 0x80, 0x28));
        gItem.fillRect(14, 1, 4, 3);
        gItem.setColor(new Color(0xE6, 0xCA, 0x64));
        gItem.fillRect(14, 1, 2, 3);
        // Black thrust washer (y: 4)
        gItem.setColor(new Color(0x24, 0x26, 0x2A));
        gItem.fillRect(12, 4, 8, 1);

        // Domed Yellow Bonnet (y: 5..8)
        gItem.setColor(new Color(0xE8, 0xC0, 0x12));
        gItem.fillRect(10, 5, 12, 2);
        gItem.fillRect(9, 7, 14, 2);
        gItem.setColor(new Color(0xFF, 0xE6, 0x4A));
        gItem.fillRect(11, 5, 4, 4);

        // Upper Bonnet Double Flange & Silver Bolts (y: 9..11)
        gItem.setColor(new Color(0xDC, 0xDE, 0xE6)); // Top bolt heads
        gItem.fillRect(8, 8, 2, 1);
        gItem.fillRect(15, 8, 2, 1);
        gItem.fillRect(22, 8, 2, 1);
        gItem.setColor(new Color(0xF5, 0xCE, 0x18)); // Upper flange
        gItem.fillRect(7, 9, 18, 2);
        gItem.setColor(new Color(0xB8, 0x90, 0x08)); // Flange seam
        gItem.fillRect(8, 10, 16, 1);
        gItem.setColor(new Color(0xDC, 0xDE, 0xE6)); // Bottom flange nuts
        gItem.fillRect(8, 11, 2, 1);
        gItem.fillRect(15, 11, 2, 1);
        gItem.fillRect(22, 11, 2, 1);

        // Left & Right 2.5" Side Hose Nozzles (y: 13..18)
        // Left nozzle (x: 2..8)
        gItem.setColor(new Color(0xD8, 0xB0, 0x0E));
        gItem.fillRect(4, 13, 5, 5);
        gItem.setColor(new Color(0xFF, 0xE2, 0x42));
        gItem.fillRect(4, 14, 5, 2);
        gItem.setColor(new Color(0xC4, 0x9C, 0x0A));
        gItem.fillRect(2, 14, 2, 3); // Left wrench nut
        // Right nozzle (x: 23..29)
        gItem.setColor(new Color(0xD8, 0xB0, 0x0E));
        gItem.fillRect(23, 13, 5, 5);
        gItem.setColor(new Color(0xFF, 0xE2, 0x42));
        gItem.fillRect(23, 14, 5, 2);
        gItem.setColor(new Color(0xC4, 0x9C, 0x0A));
        gItem.fillRect(28, 14, 2, 3); // Right wrench nut

        // Main Vertical Cylindrical Barrel (x: 9..22, y: 11..26)
        gItem.setColor(new Color(0xEB, 0xC2, 0x14));
        gItem.fillRect(9, 11, 14, 16);
        // Cylindrical highlight on left-center
        gItem.setColor(new Color(0xFF, 0xE4, 0x48));
        gItem.fillRect(11, 11, 4, 16);
        // Cylindrical shadow on right edge
        gItem.setColor(new Color(0xC2, 0x9A, 0x0A));
        gItem.fillRect(20, 11, 3, 16);

        // Front Large Pumper Cap & 'B 41' Center Nut (x: 11..20, y: 12..19)
        gItem.setColor(new Color(0xB8, 0x90, 0x08));
        gItem.fillOval(10, 11, 12, 9);
        gItem.setColor(new Color(0xFF, 0xDF, 0x3C));
        gItem.fillOval(11, 11, 10, 8);
        gItem.setColor(new Color(0xD4, 0xAC, 0x0C));
        gItem.fillRect(14, 13, 4, 4); // Center pentagonal nut
        gItem.setColor(new Color(0xFF, 0xF0, 0x70));
        gItem.fillRect(14, 13, 2, 2);

        // Embossed 'A' Emblem on lower barrel (y: 21..24)
        gItem.setColor(new Color(0xC4, 0x9C, 0x0A));
        gItem.drawOval(13, 20, 6, 5);
        gItem.setColor(new Color(0xFF, 0xEA, 0x58));
        gItem.fillRect(15, 21, 2, 3);

        // Bottom Yellow Flange + Black Base Flange + Chrome Bolts (y: 27..31)
        gItem.setColor(new Color(0xDC, 0xE0, 0xE8)); // Top bolt heads
        gItem.fillRect(8, 26, 2, 1);
        gItem.fillRect(12, 26, 2, 1);
        gItem.fillRect(18, 26, 2, 1);
        gItem.fillRect(22, 26, 2, 1);

        gItem.setColor(new Color(0xF5, 0xCE, 0x18)); // Yellow base flange
        gItem.fillRect(7, 27, 18, 2);
        gItem.setColor(new Color(0xFF, 0xE6, 0x4E));
        gItem.fillRect(8, 27, 8, 1);

        gItem.setColor(new Color(0x22, 0x24, 0x28)); // Black base flange
        gItem.fillRect(7, 29, 18, 2);

        gItem.setColor(new Color(0xC8, 0xD0, 0xDC)); // Protruding bottom nuts
        gItem.fillRect(8, 31, 2, 1);
        gItem.fillRect(12, 31, 2, 1);
        gItem.fillRect(18, 31, 2, 1);
        gItem.fillRect(22, 31, 2, 1);

        gItem.dispose();
        ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/item/fire_hydrant.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // 1. Mandatory 26.3+ Item Definition: assets/refinery/items/fire_hydrant.json
        String itemDef = """
        {
          "model": {
            "type": "minecraft:model",
            "model": "refinery:item/fire_hydrant"
          }
        }
        """;
        writeFile(ASSETS + "items/fire_hydrant.json", itemDef);

        // 2. Item Model: assets/refinery/models/item/fire_hydrant.json
        String itemModel = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/fire_hydrant"
          }
        }
        """;
        writeFile(ASSETS + "models/item/fire_hydrant.json", itemModel);

        // 3. Blockstate: assets/refinery/blockstates/fire_hydrant.json
        String blockstate = """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/fire_hydrant" },
            "facing=south": { "model": "refinery:block/fire_hydrant", "y": 180 },
            "facing=west":  { "model": "refinery:block/fire_hydrant", "y": 270 },
            "facing=east":  { "model": "refinery:block/fire_hydrant", "y": 90 }
          }
        }
        """;
        writeFile(ASSETS + "blockstates/fire_hydrant.json", blockstate);

        // 4. Block Model (for particles & fallback): assets/refinery/models/block/fire_hydrant.json
        String blockModel = """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/fire_hydrant_side",
            "up": "refinery:block/fire_hydrant_top",
            "down": "refinery:block/fire_hydrant_bottom",
            "side": "refinery:block/fire_hydrant_side"
          },
          "elements": [
            {
              "from": [2, 0, 2],
              "to": [14, 16, 14],
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
        writeFile(ASSETS + "models/block/fire_hydrant.json", blockModel);

        // 5. Loot Table: data/refinery/loot_table/blocks/fire_hydrant.json
        String lootTable = """
        {
          "type": "minecraft:block",
          "pools": [
            {
              "rolls": 1.0,
              "entries": [
                {
                  "type": "minecraft:item",
                  "name": "refinery:fire_hydrant"
                }
              ],
              "conditions": [
                {
                  "condition": "minecraft:survives_explosion"
                }
              ]
            }
          ],
          "random_sequence": "refinery:blocks/fire_hydrant"
        }
        """;
        writeFile(DATA + "loot_table/blocks/fire_hydrant.json", lootTable);

        // 6. Crafting Recipe: data/refinery/recipe/fire_hydrant.json
        String recipe = """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "Y": "minecraft:yellow_dye",
            "B": "minecraft:bucket",
            "P": "refinery:copper_fluid_pipe"
          },
          "pattern": [
            "IYI",
            "YBY",
            "IPI"
          ],
          "result": {
            "count": 1,
            "id": "refinery:fire_hydrant"
          }
        }
        """;
        writeFile(DATA + "recipe/fire_hydrant.json", recipe);
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
