package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class PumpExpansionGenerator {
    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/gui").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "recipe").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();

        generateCoalPumpGui();
        generateDieselPumpGui();
        generateElectricPumpGui();

        generatePumpItemIcons();
        generateJsons();

        System.out.println("Pump Expansion Assets generated successfully!");
    }

    public static void generateCoalPumpGui() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Background (176x166)
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0xC6, 0xC6, 0xC6), 1, 4001L);
        drawBeveledBorder(g, 0, 0, 176, 166);

        // 2. Status Box (x: 24, y: 17, w: 118, h: 16)
        drawRecessedSlot(g, 24, 17, 118, 16);

        // 3. Solid Fuel Slot (x: 40, y: 41, w: 18, h: 18)
        drawItemSlot(g, 40, 41);

        // 4. Burn Flame Outline (x: 64, y: 44, w: 14, h: 14)
        g.setColor(new Color(0x8B, 0x8B, 0x8B));
        g.drawRect(64, 44, 13, 13);
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.fillRect(65, 45, 12, 12);

        // 5. Output Fluid Tank (x: 148, y: 17, w: 18, h: 54)
        drawRecessedSlot(g, 148, 17, 18, 54);

        // 6. Player Inventory (y: 84) and Hotbar (y: 142)
        drawPlayerInventory(g);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/gui/coal_fluid_pump.png"));
    }

    public static void generateDieselPumpGui() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Background (176x166)
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0xC6, 0xC6, 0xC6), 1, 4002L);
        drawBeveledBorder(g, 0, 0, 176, 166);

        // 2. Status Box (x: 20, y: 17, w: 122, h: 16)
        drawRecessedSlot(g, 20, 17, 122, 16);

        // 3. Diesel Fuel Reservoir Slot (x: 20, y: 37, w: 18, h: 34)
        drawRecessedSlot(g, 20, 37, 18, 34);

        // 4. Fuel Bucket Input Slot (x: 44, y: 37, w: 18, h: 18)
        drawItemSlot(g, 44, 37);

        // 5. Empty Bucket Output Slot (x: 68, y: 37, w: 18, h: 18)
        drawItemSlot(g, 68, 37);
        // Small transfer arrow
        g.setColor(new Color(0x60, 0x60, 0x60));
        g.fillRect(63, 45, 4, 2);

        // 6. Output Fluid Tank (x: 148, y: 17, w: 18, h: 54)
        drawRecessedSlot(g, 148, 17, 18, 54);

        // 7. Player Inventory
        drawPlayerInventory(g);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/gui/diesel_fluid_pump.png"));
    }

    public static void generateElectricPumpGui() throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Background (176x166)
        fillNoisyRect(g, 0, 0, 176, 166, new Color(0xC6, 0xC6, 0xC6), 1, 4003L);
        drawBeveledBorder(g, 0, 0, 176, 166);

        // 2. Status Box (x: 20, y: 17, w: 122, h: 16)
        drawRecessedSlot(g, 20, 17, 122, 16);

        // 3. RF Energy Reservoir Slot (x: 20, y: 37, w: 18, h: 34)
        drawRecessedSlot(g, 20, 37, 18, 34);

        // 4. Digital Readout Panel (x: 42, y: 37, w: 98, h: 34)
        drawRecessedSlot(g, 42, 37, 100, 34);

        // 5. Output Fluid Tank (x: 148, y: 17, w: 18, h: 54)
        drawRecessedSlot(g, 148, 17, 18, 54);

        // 6. Player Inventory
        drawPlayerInventory(g);

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/gui/electric_fluid_pump.png"));
    }

    public static void generatePumpItemIcons() throws Exception {
        // Generate 32x32 high-definition icons for Coal, Diesel, and Electric pumps
        Color coalFrame = new Color(0x37, 0x41, 0x51);       // Charcoal Dark
        Color dieselFrame = new Color(0xFA, 0x98, 0x3A);     // Caterpillar Yellow-Orange
        Color electricFrame = new Color(0x25, 0x63, 0xEB);   // Electric Blue

        createPumpIcon(coalFrame, ASSETS + "textures/item/coal_fluid_pump.png");
        createPumpIcon(dieselFrame, ASSETS + "textures/item/diesel_fluid_pump.png");
        createPumpIcon(electricFrame, ASSETS + "textures/item/electric_fluid_pump.png");
        // Also keep fluid_pump.png pointing to default
        createPumpIcon(coalFrame, ASSETS + "textures/item/fluid_pump.png");
    }

    private static void createPumpIcon(Color frameColor, String path) throws Exception {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 1. Tubular Frame Outline & Corner Welds
        g.setColor(frameColor);
        // Outer Roll Cage
        g.drawRect(2, 4, 27, 24);
        g.drawRect(3, 5, 25, 22);

        // Rubber Corner Damping Feet
        g.setColor(new Color(0x18, 0x1A, 0x1D));
        g.fillRect(2, 28, 5, 2);
        g.fillRect(25, 28, 5, 2);

        // 2. White Enamel Fuel Tank / Power Cap (Top left: x: 5..16, y: 6..12)
        g.setColor(new Color(0xEE, 0xF2, 0xF6));
        g.fillRect(6, 7, 10, 6);
        g.setColor(new Color(0xD0, 0xD7, 0xE2));
        g.drawRect(6, 7, 9, 5);
        // Chrome Filler Cap
        g.setColor(new Color(0xFA, 0xFA, 0xFA));
        g.fillRect(9, 5, 4, 2);

        // 3. Engine / Starter Box (Bottom left: x: 5..16, y: 14..25)
        g.setColor(new Color(0xFA, 0xBC, 0x09));
        g.fillOval(6, 14, 10, 10);
        g.setColor(new Color(0xD9, 0x9B, 0x00));
        g.drawOval(6, 14, 9, 9);
        // Recoil pull starter center
        g.setColor(new Color(0x1F, 0x22, 0x28));
        g.fillOval(9, 17, 4, 4);
        // Pull handle
        g.setColor(new Color(0x11, 0x12, 0x14));
        g.fillRect(4, 18, 2, 3);

        // 4. Centrifugal Impeller Volute Pump Body (Right: x: 17..27, y: 10..26)
        g.setColor(new Color(0xA8, 0xB0, 0xBA));
        g.fillOval(17, 13, 10, 11);
        g.setColor(new Color(0x73, 0x7B, 0x85));
        g.drawOval(17, 13, 9, 10);

        // Front Suction Intake Flange
        g.setColor(new Color(0x60, 0x68, 0x72));
        g.fillOval(19, 15, 6, 6);
        g.setColor(new Color(0x18, 0x1C, 0x22));
        g.fillOval(21, 17, 2, 2);

        // Top Vertical Discharge Outlet Pipe & Flange
        g.setColor(new Color(0xA8, 0xB0, 0xBA));
        g.fillRect(20, 8, 5, 6);
        g.setColor(new Color(0xD0, 0xD7, 0xE2));
        g.fillRect(19, 7, 7, 2);

        g.dispose();
        ImageIO.write(img, "PNG", new File(path));
    }

    public static void generateJsons() throws Exception {
        String[] pumps = new String[]{"coal_fluid_pump", "diesel_fluid_pump", "electric_fluid_pump"};

        for (String id : pumps) {
            // Blockstate
            writeFile(ASSETS + "blockstates/" + id + ".json", """
            {
              "variants": {
                "facing=north": { "model": "refinery:block/fluid_pump" },
                "facing=south": { "model": "refinery:block/fluid_pump", "y": 180 },
                "facing=west":  { "model": "refinery:block/fluid_pump", "y": 270 },
                "facing=east":  { "model": "refinery:block/fluid_pump", "y": 90 }
              }
            }
            """);

            // Item Model
            writeFile(ASSETS + "models/item/" + id + ".json", """
            {
              "parent": "minecraft:item/generated",
              "textures": {
                "layer0": "refinery:item/%s"
              }
            }
            """.formatted(id));

            // Modern 26.3 Item Definition
            writeFile(ASSETS + "items/" + id + ".json", """
            {
              "model": {
                "type": "minecraft:model",
                "model": "refinery:item/%s"
              }
            }
            """.formatted(id));

            // Loot Table
            writeFile(DATA + "loot_table/blocks/" + id + ".json", """
            {
              "type": "minecraft:block",
              "pools": [
                {
                  "bonus_rolls": 0.0,
                  "entries": [
                    {
                      "type": "minecraft:item",
                      "name": "refinery:%s"
                    }
                  ],
                  "rolls": 1.0
                }
              ]
            }
            """.formatted(id));
        }

        // Crafting Recipes
        // 1. Coal Fluid Pump (Solid Fuel)
        writeFile(DATA + "recipe/coal_fluid_pump.json", """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "B": "minecraft:bucket",
            "P": "minecraft:piston",
            "F": "minecraft:furnace",
            "S": "minecraft:smooth_stone"
          },
          "pattern": [
            "IBI",
            "IPI",
            "SFS"
          ],
          "result": {
            "count": 1,
            "id": "refinery:coal_fluid_pump"
          }
        }
        """);

        // 2. Diesel Fluid Pump
        writeFile(DATA + "recipe/diesel_fluid_pump.json", """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "B": "minecraft:bucket",
            "P": "minecraft:piston",
            "D": "refinery:diesel_bucket",
            "S": "refinery:steel_ingot"
          },
          "pattern": [
            "IBI",
            "IPI",
            "SDS"
          ],
          "result": {
            "count": 1,
            "id": "refinery:diesel_fluid_pump"
          }
        }
        """);

        // 3. Electric Fluid Pump
        writeFile(DATA + "recipe/electric_fluid_pump.json", """
        {
          "type": "minecraft:crafting_shaped",
          "category": "misc",
          "key": {
            "I": "minecraft:iron_ingot",
            "B": "minecraft:bucket",
            "P": "minecraft:piston",
            "R": "minecraft:redstone",
            "C": "minecraft:copper_ingot"
          },
          "pattern": [
            "IBI",
            "IPI",
            "CRC"
          ],
          "result": {
            "count": 1,
            "id": "refinery:electric_fluid_pump"
          }
        }
        """);
    }

    private static void drawBeveledBorder(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0xFF, 0xFF, 0xFF));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
    }

    private static void drawPlayerInventory(Graphics2D g) {
        // Player 3 rows of 9 at y: 84
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawItemSlot(g, 7 + col * 18, 83 + row * 18);
            }
        }
        // Hotbar row of 9 at y: 142
        for (int col = 0; col < 9; col++) {
            drawItemSlot(g, 7 + col * 18, 141);
        }
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

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int var, long seed) {
        Random rng = new Random(seed);
        for (int py = y; py < y + h; py++) {
            for (int px = x; px < x + w; px++) {
                int delta = rng.nextInt(var * 2 + 1) - var;
                int r = Math.max(0, Math.min(255, base.getRed() + delta));
                int gr = Math.max(0, Math.min(255, base.getGreen() + delta));
                int b = Math.max(0, Math.min(255, base.getBlue() + delta));
                g.setColor(new Color(r, gr, b));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void writeFile(String path, String content) throws Exception {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(content.trim());
        }
    }
}
