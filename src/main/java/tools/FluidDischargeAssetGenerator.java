package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Generates custom textures and JSON assets for the Industrial Fluid Discharge:
 * - High-gloss safety/fire-engine red foot valve & slotted discharge strainer head
 * - 256x256 entity model texture atlas (red enamel, chrome bolts, studs, gasket, slotted dome)
 * - 16x16 block textures (top, bottom, side)
 * - 16x16 / 32x32 item icon texture
 * - Blockstate, model, and recipe JSON files
 */
public class FluidDischargeAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/entity").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generateDischargeModelAtlas();
        generateBlockAndItemTextures();
        generateJsonFiles();

        System.out.println("Fluid Discharge assets generated successfully!");
    }

    /**
     * Generates 256x256 entity texture atlas for the monolithic 3D Fluid Discharge model:
     * - Vivid glossy fire-engine red enamel (body, base, flange rings, ear lugs)
     * - Dark graphite rubber/composite sealing gasket ring
     * - Mirror chrome / polished silver hexagonal bolt heads and nuts
     * - Threaded high-tensile steel studs with spiraled thread grooves
     * - Slotted dome ribs & perforated crown
     * - Dark interior valve throat & flapper
     */
    public static void generateDischargeModelAtlas() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fill background with transparent
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 256, 256);
        g.setComposite(AlphaComposite.SrcOver);

        // 1. Gloss Fire-Engine Red Enamel Paint (0, 0, 64, 64)
        // Base red `#D41C1C` with glossy specular highlights and edge bevels
        fillNoisyRect(g, 0, 0, 64, 64, new Color(0xD4, 0x1C, 0x1C), 6, 2026L);
        // Gloss highlight streak across top
        g.setColor(new Color(0xF4, 0x3E, 0x3E));
        g.fillRect(0, 2, 64, 8);
        g.setColor(new Color(0xFF, 0x6E, 0x6E));
        g.fillRect(0, 4, 64, 3);
        // Deep shading at bottom
        g.setColor(new Color(0x9E, 0x10, 0x10));
        g.fillRect(0, 56, 64, 8);
        g.setColor(new Color(0x76, 0x0A, 0x0A));
        g.fillRect(0, 62, 64, 2);

        // 2. Base Collar with Cast Reinforcement Ribs (64, 0, 64, 32)
        fillNoisyRect(g, 64, 0, 64, 32, new Color(0xC0, 0x16, 0x16), 5, 2027L);
        // Vertical cast rib textures
        for (int rx = 68; rx < 128; rx += 12) {
            g.setColor(new Color(0xE8, 0x2A, 0x2A));
            g.fillRect(rx, 0, 2, 32);
            g.setColor(new Color(0x8A, 0x0E, 0x0E));
            g.fillRect(rx + 2, 0, 2, 32);
        }
        g.setColor(new Color(0x70, 0x08, 0x08));
        g.fillRect(64, 28, 64, 4);

        // 3. Lower & Upper Flange Ring and Ear Lugs (0, 64, 64, 32)
        fillNoisyRect(g, 0, 64, 64, 32, new Color(0xDC, 0x20, 0x20), 5, 2028L);
        // Rounded flange bevel highlights
        g.setColor(new Color(0xFF, 0x55, 0x55));
        g.fillRect(0, 64, 64, 3);
        g.setColor(new Color(0x88, 0x0D, 0x0D));
        g.fillRect(0, 93, 64, 3);

        // 4. Graphite Gasket Seal Ring (64, 32, 64, 16)
        fillNoisyRect(g, 64, 32, 64, 16, new Color(0x2E, 0x30, 0x34), 4, 2029L);
        g.setColor(new Color(0x42, 0x46, 0x4C));
        g.fillRect(64, 33, 64, 2);
        g.setColor(new Color(0x1A, 0x1B, 0x1E));
        g.fillRect(64, 46, 64, 2);

        // 5. Polished Silver Chrome Bolt Hex Heads (128, 0, 32, 32)
        fillNoisyRect(g, 128, 0, 32, 32, new Color(0xD2, 0xD7, 0xDE), 5, 2030L);
        // Hexagonal bolt head chamfer outline
        g.setColor(new Color(0xF6, 0xF9, 0xFF));
        g.drawRect(130, 2, 27, 27);
        g.setColor(new Color(0x8E, 0x94, 0xA0));
        g.drawRect(128, 0, 31, 31);
        // High specular shine
        g.setColor(Color.WHITE);
        g.fillRect(134, 6, 6, 6);

        // 6. Threaded Steel Studs & Hex Nuts (160, 0, 32, 32)
        fillNoisyRect(g, 160, 0, 32, 32, new Color(0x9E, 0xA4, 0xB0), 4, 2031L);
        // Spiraled thread grooves
        for (int ty = 2; ty < 32; ty += 4) {
            g.setColor(new Color(0x6A, 0x70, 0x7C));
            g.fillRect(160, ty, 32, 2);
            g.setColor(new Color(0xC8, 0xCE, 0xD8));
            g.fillRect(160, ty + 2, 32, 2);
        }

        // 7. Slotted Strainer Dome Ribs (0, 96, 64, 64)
        fillNoisyRect(g, 0, 96, 64, 64, new Color(0xD8, 0x1E, 0x1E), 6, 2032L);
        // Arched vent slots with dark depth
        for (int sx = 4; sx < 64; sx += 12) {
            // Shadow cavity behind slot
            g.setColor(new Color(0x18, 0x18, 0x1C));
            g.fillRect(sx, 100, 6, 40);
            // Highlight on left rib edge
            g.setColor(new Color(0xF4, 0x48, 0x48));
            g.fillRect(sx - 2, 98, 2, 44);
            // Shadow on right rib edge
            g.setColor(new Color(0x88, 0x0E, 0x0E));
            g.fillRect(sx + 6, 98, 2, 44);
        }

        // 8. Perforated Dome Crown / Top Cap (64, 64, 48, 48)
        fillNoisyRect(g, 64, 64, 48, 48, new Color(0xDC, 0x20, 0x20), 5, 2033L);
        // Strainer perforations / circular holes
        g.setColor(new Color(0x14, 0x14, 0x18));
        for (int cy = 70; cy < 106; cy += 10) {
            int offset = (cy % 20 == 0) ? 5 : 0;
            for (int cx = 70 + offset; cx < 106; cx += 10) {
                g.fillOval(cx, cy, 6, 6);
                g.setColor(new Color(0x88, 0x0C, 0x0C));
                g.drawOval(cx, cy, 6, 6);
                g.setColor(new Color(0x14, 0x14, 0x18));
            }
        }

        // 9. Interior Valve Throat & Flapper Cavity (128, 32, 48, 48)
        fillNoisyRect(g, 128, 32, 48, 48, new Color(0x18, 0x19, 0x1C), 3, 2034L);
        // Valve flapper plate (bronze/brass circular disk)
        g.setColor(new Color(0x82, 0x62, 0x2A));
        g.fillOval(134, 38, 36, 36);
        g.setColor(new Color(0xB8, 0x90, 0x44));
        g.drawOval(134, 38, 36, 36);
        g.setColor(new Color(0x4C, 0x38, 0x16));
        g.fillOval(148, 52, 8, 8);

        // 10. Glowing Discharge Indicator (Fill Mode / Void Mode) (192, 0, 32, 32)
        // Mode indicator light / status ring
        fillNoisyRect(g, 192, 0, 32, 16, new Color(0x20, 0xC0, 0xFF), 8, 2035L); // Fill mode cyan
        fillNoisyRect(g, 192, 16, 32, 16, new Color(0xFF, 0x40, 0x20), 8, 2036L); // Void mode orange-red

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/entity/fluid_discharge.png"));
    }

    public static void generateBlockAndItemTextures() throws Exception {
        // Block Top (Slotted Red Strainer Crown) 16x16
        BufferedImage blockTop = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTop = blockTop.createGraphics();
        fillNoisyRect(gTop, 0, 0, 16, 16, new Color(0xD0, 0x1C, 0x1C), 6, 301L);
        gTop.setColor(new Color(0x1A, 0x1A, 0x20));
        gTop.fillOval(4, 4, 8, 8);
        gTop.fillOval(2, 6, 3, 4);
        gTop.fillOval(11, 6, 3, 4);
        gTop.fillOval(6, 2, 4, 3);
        gTop.fillOval(6, 11, 4, 3);
        gTop.setColor(new Color(0x8A, 0x0E, 0x0E));
        gTop.drawRect(0, 0, 15, 15);
        gTop.dispose();
        ImageIO.write(blockTop, "PNG", new File(ASSETS + "textures/block/fluid_discharge_top.png"));

        // Block Bottom (Base Pipe Port & Flange) 16x16
        BufferedImage blockBottom = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBot = blockBottom.createGraphics();
        fillNoisyRect(gBot, 0, 0, 16, 16, new Color(0x9E, 0x12, 0x12), 5, 302L);
        // Circular pipe inlet orifice
        gBot.setColor(new Color(0x22, 0x24, 0x28));
        gBot.fillOval(3, 3, 10, 10);
        gBot.setColor(new Color(0x6E, 0x74, 0x80));
        gBot.drawOval(3, 3, 10, 10);
        gBot.dispose();
        ImageIO.write(blockBottom, "PNG", new File(ASSETS + "textures/block/fluid_discharge_bottom.png"));

        // Block Side (Gloss Red Housing, Flanges, Gasket & Bolts) 16x16
        BufferedImage blockSide = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSide = blockSide.createGraphics();
        fillNoisyRect(gSide, 0, 0, 16, 16, new Color(0xD4, 0x1E, 0x1E), 6, 303L);
        // Upper dome slats (rows 0-5)
        for (int x = 2; x < 15; x += 4) {
            gSide.setColor(new Color(0x1E, 0x1E, 0x24));
            gSide.fillRect(x, 1, 2, 5);
        }
        // Flange ring (rows 6-9)
        gSide.setColor(new Color(0xFF, 0x48, 0x48));
        gSide.fillRect(0, 6, 16, 1);
        gSide.setColor(new Color(0x2B, 0x2C, 0x30)); // Gasket
        gSide.fillRect(0, 8, 16, 1);
        gSide.setColor(new Color(0x88, 0x0E, 0x0E));
        gSide.fillRect(0, 9, 16, 1);
        // Silver hex bolt heads on sides
        gSide.setColor(new Color(0xDE, 0xE2, 0xEA));
        gSide.fillRect(1, 5, 3, 3);
        gSide.fillRect(12, 5, 3, 3);
        // Lower cylindrical body (rows 10-15)
        gSide.setColor(new Color(0x9C, 0x12, 0x12));
        gSide.fillRect(0, 14, 16, 2);
        gSide.dispose();
        ImageIO.write(blockSide, "PNG", new File(ASSETS + "textures/block/fluid_discharge_side.png"));

        // 32x32 Item Icon (High-definition render of the red foot valve)
        BufferedImage itemImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gItem = itemImg.createGraphics();
        gItem.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Lower body cylinder (x: 8-23, y: 14-25)
        gItem.setColor(new Color(0xB8, 0x14, 0x14));
        gItem.fillRoundRect(8, 14, 16, 12, 4, 4);
        gItem.setColor(new Color(0x8A, 0x0C, 0x0C));
        gItem.fillRect(9, 23, 14, 3);

        // Lower flange & ears
        gItem.setColor(new Color(0xDC, 0x1C, 0x1C));
        gItem.fillRoundRect(5, 12, 22, 3, 2, 2);
        // Gasket
        gItem.setColor(new Color(0x28, 0x2A, 0x2E));
        gItem.fillRect(6, 11, 20, 1);
        // Upper flange & ears
        gItem.setColor(new Color(0xEE, 0x2C, 0x2C));
        gItem.fillRoundRect(5, 8, 22, 3, 2, 2);

        // Silver bolts & nuts on ears
        gItem.setColor(new Color(0xE4, 0xE8, 0xF0));
        gItem.fillRect(5, 6, 3, 3); // Left bolt
        gItem.fillRect(24, 6, 3, 3); // Right bolt
        gItem.setColor(new Color(0x90, 0x96, 0xA2));
        gItem.fillRect(6, 15, 2, 4); // Left stud
        gItem.fillRect(24, 15, 2, 4); // Right stud

        // Slotted dome (y: 2-8, x: 7-24)
        gItem.setColor(new Color(0xDC, 0x1C, 0x1C));
        gItem.fillArc(7, 1, 18, 14, 0, 180);
        // Slits in dome
        gItem.setColor(new Color(0x18, 0x18, 0x1C));
        gItem.fillRect(10, 4, 2, 4);
        gItem.fillRect(13, 3, 2, 5);
        gItem.fillRect(17, 3, 2, 5);
        gItem.fillRect(20, 4, 2, 4);
        // Dome highlight
        gItem.setColor(new Color(0xFF, 0x6E, 0x6E));
        gItem.drawArc(8, 2, 16, 12, 45, 90);

        gItem.dispose();
        ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/item/fluid_discharge.png"));
    }

    public static void generateJsonFiles() throws Exception {
        // Blockstate JSON
        String blockstate = """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/fluid_discharge" },
            "facing=south": { "model": "refinery:block/fluid_discharge", "y": 180 },
            "facing=west":  { "model": "refinery:block/fluid_discharge", "y": 270 },
            "facing=east":  { "model": "refinery:block/fluid_discharge", "y": 90 }
          }
        }
        """;
        writeFile(ASSETS + "blockstates/fluid_discharge.json", blockstate);

        // Block Model JSON
        String blockModel = """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/fluid_discharge_side",
            "up": "refinery:block/fluid_discharge_top",
            "down": "refinery:block/fluid_discharge_bottom",
            "side": "refinery:block/fluid_discharge_side"
          },
          "elements": [
            {
              "from": [1, 0, 1],
              "to": [15, 15, 15],
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
        writeFile(ASSETS + "models/block/fluid_discharge.json", blockModel);

        // Item Model JSON
        String itemModel = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/fluid_discharge"
          }
        }
        """;
        writeFile(ASSETS + "models/item/fluid_discharge.json", itemModel);

        // Recipe JSON (Red Dye + Iron Ingots + Iron Bars + Bucket + Redstone)
        String recipe = """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "B": "minecraft:iron_bars",
            "U": "minecraft:bucket",
            "R": "minecraft:redstone",
            "D": "minecraft:red_dye"
          },
          "pattern": [
            "IBI",
            "DUD",
            "IRI"
          ],
          "result": {
            "count": 1,
            "id": "refinery:fluid_discharge"
          }
        }
        """;
        writeFile(DATA + "recipe/fluid_discharge.json", recipe);
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
