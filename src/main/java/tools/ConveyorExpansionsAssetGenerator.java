package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class ConveyorExpansionsAssetGenerator {
    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "textures/gui").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generateSlopedConveyorIcon();
        generateRightAngleConveyorIcon();
        generateSortingConveyorIcon();
        generateSortingConveyorGui();
        generateJsonFiles();

        System.out.println("Expansion icons, GUI, and JSONs generated successfully!");
    }

    private static void generateSlopedConveyorIcon() throws Exception {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(3, 23, 26, 7);

        // Vertical support leg on high end (right side)
        g.setColor(new Color(0x70, 0x76, 0x80));
        g.fillRect(23, 10, 3, 15);
        g.setColor(new Color(0x40, 0x46, 0x4E));
        g.fillRect(25, 10, 2, 15);
        // Base plate
        g.setColor(new Color(0x30, 0x34, 0x3A));
        g.fillRect(22, 24, 6, 2);

        // Aluminum frame side (inclined)
        int[] skirtX = {4, 18, 28, 14};
        int[] skirtY = {21, 23, 13, 8};
        g.setColor(new Color(0x9E, 0xA7, 0xB2));
        g.fillPolygon(skirtX, skirtY, 4);

        // T-slot channel along inclined side
        g.setColor(new Color(0x36, 0x3C, 0x44));
        g.drawLine(6, 21, 26, 14);
        g.drawLine(6, 22, 26, 15);

        // Inclined matte black belt top
        int[] beltX = {4, 18, 28, 14};
        int[] beltY = {18, 20, 7, 3};
        g.setColor(new Color(0x1F, 0x22, 0x26));
        g.fillPolygon(skirtX, beltY, 4);

        // Belt ridges
        g.setColor(new Color(0x30, 0x35, 0x3C));
        g.drawLine(8, 16, 17, 7);
        g.drawLine(12, 17, 21, 8);
        g.drawLine(15, 18, 24, 9);

        // Top aluminum guide rails
        g.setColor(new Color(0xD6, 0xDC, 0xE3));
        g.drawLine(3, 19, 17, 21);
        g.drawLine(14, 3, 28, 7);
        g.setColor(new Color(0xB5, 0xBC, 0xC6));
        g.drawLine(4, 18, 14, 4);

        // Rollers at ends
        g.setColor(new Color(0xEB, 0xF1, 0xF7));
        g.drawLine(16, 21, 18, 21);
        g.drawLine(26, 7, 28, 8);

        // Up arrow indicator badge (neon orange)
        g.setColor(new Color(0xE6, 0x6E, 0x19));
        g.fillRect(6, 6, 3, 6);
        int[] arrX = {4, 7, 10};
        int[] arrY = {6, 3, 6};
        g.fillPolygon(arrX, arrY, 3);

        g.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/sloped_conveyor_belt.png"));
    }

    private static void generateRightAngleConveyorIcon() throws Exception {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(4, 21, 24, 8);

        // Aluminum corner frame skirt
        int[] skirtX = {4, 16, 27, 27, 18, 4};
        int[] skirtY = {18, 24, 20, 16, 17, 14};
        g.setColor(new Color(0x9E, 0xA7, 0xB2));
        g.fillPolygon(skirtX, skirtY, 6);

        // T-slot groove along skirt
        g.setColor(new Color(0x36, 0x3C, 0x44));
        g.drawLine(5, 19, 16, 24);
        g.drawLine(17, 24, 26, 20);

        // L-shaped curved matte black belt surface
        int[] beltX = {5, 16, 27, 24, 16, 12};
        int[] beltY = {15, 21, 17, 12, 14, 10};
        g.setColor(new Color(0x1F, 0x22, 0x26));
        g.fillPolygon(beltX, beltY, 6);

        // Belt curve lines
        g.setColor(new Color(0x32, 0x38, 0x40));
        g.drawArc(7, 6, 16, 14, 200, 90);
        g.drawArc(8, 7, 18, 15, 200, 90);

        // Outer corner guide rail
        g.setColor(new Color(0xD6, 0xDC, 0xE3));
        g.drawLine(4, 16, 16, 22);
        g.drawLine(16, 22, 27, 18);
        g.setColor(new Color(0xB5, 0xBC, 0xC6));
        g.drawLine(4, 17, 16, 23);
        g.drawLine(16, 23, 27, 19);

        // Inner corner corner bracket
        g.setColor(new Color(0x7D, 0x85, 0x90));
        g.fillRect(11, 10, 4, 4);

        // Corner turning arrow badge (yellow/orange curved turn arrow)
        g.setColor(new Color(0xFA, 0xA6, 0x1A));
        g.drawArc(5, 4, 12, 12, 180, 90);
        g.drawLine(11, 4, 8, 2);
        g.drawLine(11, 4, 9, 6);

        g.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/right_angle_conveyor_belt.png"));
    }

    private static void generateSortingConveyorIcon() throws Exception {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 1. Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(3, 22, 26, 8);

        // 2. Base Conveyor Bed (Isometric view: left-to-right)
        // Aluminum skirt
        int[] skirtX = {4, 16, 28, 16};
        int[] skirtY = {18, 24, 20, 14};
        g.setColor(new Color(0x8A, 0x93, 0x9E));
        g.fillPolygon(skirtX, skirtY, 4);

        // T-slot channel
        g.setColor(new Color(0x30, 0x36, 0x3E));
        g.drawLine(5, 19, 15, 24);
        g.drawLine(17, 24, 27, 20);

        // Matte black belt surface
        int[] beltX = {4, 16, 28, 16};
        int[] beltY = {16, 22, 18, 12};
        g.setColor(new Color(0x1F, 0x22, 0x26));
        g.fillPolygon(beltX, beltY, 4);

        // Belt ribs
        g.setColor(new Color(0x35, 0x3B, 0x42));
        g.drawLine(8, 15, 18, 20);
        g.drawLine(11, 14, 21, 19);
        g.drawLine(14, 13, 24, 18);

        // Rollers at front/back
        g.setColor(new Color(0xEB, 0xF1, 0xF7));
        g.drawLine(15, 23, 17, 23);

        // 3. Overhead Optical Scanner Gantry / Bridge
        // Left pillar (back-left)
        g.setColor(new Color(0x5A, 0x62, 0x6D));
        g.fillRect(8, 6, 3, 10);
        // Right pillar (front-right)
        g.setColor(new Color(0x7D, 0x86, 0x93));
        g.fillRect(21, 8, 3, 12);

        // Overhead crossbar arch
        g.setColor(new Color(0x9E, 0xA7, 0xB4));
        g.fillRect(7, 4, 18, 4);
        g.setColor(new Color(0x40, 0x46, 0x4F));
        g.drawRect(7, 4, 18, 4);

        // Sensor hood underneath arch
        g.setColor(new Color(0x28, 0x2D, 0x34));
        g.fillRect(11, 8, 10, 2);

        // Cyan scanner beam line
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.drawLine(12, 9, 19, 9);

        // 4. Tri-color status indicator lamps on crossbeam:
        // Left (Blue): x = 9
        g.setColor(new Color(0x3B, 0x82, 0xF6));
        g.fillRect(9, 5, 2, 2);
        // Center (Green): x = 15
        g.setColor(new Color(0x10, 0xB9, 0x81));
        g.fillRect(15, 5, 2, 2);
        // Right (Orange): x = 21
        g.setColor(new Color(0xF9, 0x73, 0x16));
        g.fillRect(21, 5, 2, 2);

        g.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/sorting_conveyor_belt.png"));
    }

    private static void generateSortingConveyorGui() throws Exception {
        int w = 176;
        int h = 178;
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Base Industrial Steel Frame (176 x 178)
        fillNoisyRect(g, 0, 0, w, h, new Color(0x2B, 0x30, 0x36), 3, 7777L);

        // Outer 3D Beveled Border
        g.setColor(new Color(0x56, 0x5F, 0x6C));
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 0, 0, h - 1);
        g.setColor(new Color(0x15, 0x18, 0x1B));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);

        // 4 Corner Rivets
        drawBolt(g, 3, 3);
        drawBolt(g, w - 5, 3);
        drawBolt(g, 3, h - 5);
        drawBolt(g, w - 5, h - 5);

        // =========================================================================
        // Channel Plates & Header Tabs (Y = 15, H = 69)
        // =========================================================================

        // 1. LEFT CHANNEL (Blue Accent): x = 6, y = 14, w = 55, h = 68
        drawChannelPlate(g, 6, 14, 55, 68, new Color(0x1D, 0x4E, 0xD8), new Color(0x3B, 0x82, 0xF6), "LEFT");
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                drawRecessedSlotWell(g, 7 + c * 18, 27 + r * 18, new Color(0x16, 0x22, 0x33));
            }
        }

        // 2. FORWARD CHANNEL (Green Accent): x = 60, y = 14, w = 55, h = 68
        drawChannelPlate(g, 60, 14, 55, 68, new Color(0x04, 0x78, 0x57), new Color(0x10, 0xB9, 0x81), "FORWARD");
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                drawRecessedSlotWell(g, 61 + c * 18, 27 + r * 18, new Color(0x14, 0x27, 0x20));
            }
        }

        // 3. RIGHT CHANNEL (Orange Accent): x = 114, y = 14, w = 55, h = 68
        drawChannelPlate(g, 114, 14, 55, 68, new Color(0xC2, 0x41, 0x0C), new Color(0xF9, 0x73, 0x16), "RIGHT");
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                drawRecessedSlotWell(g, 115 + c * 18, 27 + r * 18, new Color(0x2B, 0x1E, 0x14));
            }
        }

        // =========================================================================
        // Player Inventory & Hotbar Wells
        // =========================================================================
        // Inventory Label Plate Line: y = 88
        g.setColor(new Color(0x20, 0x24, 0x28));
        g.drawLine(7, 88, w - 8, 88);
        g.setColor(new Color(0x3E, 0x45, 0x4F));
        g.drawLine(7, 89, w - 8, 89);

        // Player Inventory 3x9 slots (x: 7 + col * 18, y: 95 + row * 18)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                drawRecessedSlotWell(g, 7 + c * 18, 95 + r * 18, new Color(0x1D, 0x21, 0x26));
            }
        }

        // Hotbar 1x9 slots (x: 7 + col * 18, y: 153)
        for (int c = 0; c < 9; c++) {
            drawRecessedSlotWell(g, 7 + c * 18, 153, new Color(0x1D, 0x21, 0x26));
        }

        g.dispose();
        ImageIO.write(gui, "PNG", new File(ASSETS + "textures/gui/sorting_conveyor.png"));
    }

    private static void drawChannelPlate(Graphics2D g, int x, int y, int w, int h, Color darkAccent, Color brightAccent, String label) {
        fillNoisyRect(g, x, y, w, h, new Color(0x33, 0x39, 0x41), 2, 8888L + x);

        // Plate border
        g.setColor(new Color(0x4C, 0x54, 0x61));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0x1A, 0x1D, 0x20));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

        // Header Banner (Y: y + 2, H: 9)
        g.setColor(darkAccent);
        g.fillRect(x + 2, y + 2, w - 4, 9);
        g.setColor(brightAccent);
        g.drawRect(x + 2, y + 2, w - 4, 9);

        // Decorative direction arrows / symbols on banner:
        g.setColor(Color.WHITE);
        if ("LEFT".equals(label)) {
            // Left arrow: <
            g.drawLine(x + 6, y + 6, x + 8, y + 4);
            g.drawLine(x + 6, y + 6, x + 8, y + 8);
            g.drawLine(x + 7, y + 6, x + 11, y + 6);
        } else if ("FORWARD".equals(label)) {
            // Up arrow: ^
            g.drawLine(x + w / 2, y + 4, x + w / 2 - 2, y + 7);
            g.drawLine(x + w / 2, y + 4, x + w / 2 + 2, y + 7);
            g.drawLine(x + w / 2, y + 5, x + w / 2, y + 9);
        } else if ("RIGHT".equals(label)) {
            // Right arrow: >
            g.drawLine(x + w - 7, y + 6, x + w - 9, y + 4);
            g.drawLine(x + w - 7, y + 6, x + w - 9, y + 8);
            g.drawLine(x + w - 8, y + 6, x + w - 12, y + 6);
        }
    }

    private static void drawRecessedSlotWell(Graphics2D g, int x, int y, Color innerFill) {
        g.setColor(new Color(0x14, 0x17, 0x1A));
        g.drawLine(x, y, x + 17, y);
        g.drawLine(x, y, x, y + 17);
        g.setColor(new Color(0x4D, 0x56, 0x63));
        g.drawLine(x + 17, y + 1, x + 17, y + 17);
        g.drawLine(x + 1, y + 17, x + 17, y + 17);
        g.setColor(innerFill);
        g.fillRect(x + 1, y + 1, 16, 16);
    }

    private static void drawBolt(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x12, 0x14, 0x16));
        g.fillRect(x, y, 2, 2);
        g.setColor(new Color(0x94, 0xA1, 0xB0));
        g.fillRect(x, y, 1, 1);
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int noiseRange, long seed) {
        Random r = new Random(seed);
        for (int px = x; px < x + w; px++) {
            for (int py = y; py < y + h; py++) {
                int n = r.nextInt(noiseRange * 2 + 1) - noiseRange;
                int red = Math.max(0, Math.min(255, base.getRed() + n));
                int green = Math.max(0, Math.min(255, base.getGreen() + n));
                int blue = Math.max(0, Math.min(255, base.getBlue() + n));
                g.setColor(new Color(red, green, blue));
                g.fillRect(px, py, 1, 1);
            }
        }
    }

    private static void generateJsonFiles() throws Exception {
        // 1. Modern 1.21.4 item definition: assets/refinery/items/sorting_conveyor_belt.json
        writeString(ASSETS + "items/sorting_conveyor_belt.json",
                """
                {
                  "model": {
                    "type": "minecraft:model",
                    "model": "refinery:item/sorting_conveyor_belt"
                  }
                }
                """);

        // 2. Item model: assets/refinery/models/item/sorting_conveyor_belt.json
        writeString(ASSETS + "models/item/sorting_conveyor_belt.json",
                """
                {
                  "parent": "minecraft:item/generated",
                  "textures": {
                    "layer0": "refinery:item/sorting_conveyor_belt"
                  }
                }
                """);

        // 3. Blockstate: assets/refinery/blockstates/sorting_conveyor_belt.json
        writeString(ASSETS + "blockstates/sorting_conveyor_belt.json",
                """
                {
                  "variants": {
                    "": {
                      "model": "refinery:block/sorting_conveyor_belt"
                    }
                  }
                }
                """);

        // 4. Block model: assets/refinery/models/block/sorting_conveyor_belt.json
        writeString(ASSETS + "models/block/sorting_conveyor_belt.json",
                """
                {
                  "textures": {
                    "particle": "refinery:block/conveyor_belt_side"
                  }
                }
                """);

        // 5. Loot table: data/refinery/loot_table/blocks/sorting_conveyor_belt.json
        writeString(DATA + "loot_table/blocks/sorting_conveyor_belt.json",
                """
                {
                  "type": "minecraft:block",
                  "pools": [
                    {
                      "bonus_rolls": 0.0,
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "name": "refinery:sorting_conveyor_belt"
                        }
                      ],
                      "rolls": 1.0
                    }
                  ]
                }
                """);

        // 6. Recipe: data/refinery/recipe/sorting_conveyor_belt.json
        writeString(DATA + "recipe/sorting_conveyor_belt.json",
                """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "key": {
                    "C": "refinery:conveyor_belt",
                    "P": "refinery:diamond_item_pipe",
                    "I": "minecraft:iron_ingot"
                  },
                  "pattern": [
                    " P ",
                    "ICI"
                  ],
                  "result": {
                    "count": 1,
                    "id": "refinery:sorting_conveyor_belt"
                  }
                }
                """);
    }

    private static void writeString(String path, String content) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content.trim() + "\n");
        }
    }
}
