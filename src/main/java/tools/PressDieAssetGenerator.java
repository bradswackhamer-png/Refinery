package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PressDieAssetGenerator {
    private static final String RES = "src/main/resources/";
    private static final String ASSETS = RES + "assets/refinery/";
    private static final String DATA = RES + "data/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(DATA + "refinery/recipe").mkdirs();
        new File(DATA + "refinery/tags/item").mkdirs();
        new File(DATA + "c/tags/item").mkdirs();
        new File(DATA + "c/tags/item/gears").mkdirs();
        new File(DATA + "c/tags/item/ingots").mkdirs();

        generateTextures();
        generateModels();
        generateItemDefinitions();
        generateTags();
        generateRecipes();

        System.out.println("Press Dies and Gears generated successfully!");
    }

    private static void generateTextures() throws IOException {
        // 1. DIES
        generatePlateDieTexture();
        generateGearDieTexture();
        generateCorrugatedDieTexture();
        generateRailDieTexture();

        // 2. GEARS
        generateGearTexture("steel_gear",
                new Color(0xCBD5E1), new Color(0x94A3B8), new Color(0x475569), new Color(0x1E293B));
        generateGearTexture("iron_gear",
                new Color(0xFFFFFF), new Color(0xD8D8D8), new Color(0x8E8E8E), new Color(0x404040));
        generateGearTexture("copper_gear",
                new Color(0xFBA17E), new Color(0xE0734C), new Color(0x9E4426), new Color(0x561F10));
        generateGearTexture("bronze_gear",
                new Color(0xFAD082), new Color(0xD99B3D), new Color(0x945B17), new Color(0x4E2B06));
        generateGearTexture("gold_gear",
                new Color(0xFFF38B), new Color(0xFCC526), new Color(0xC7840B), new Color(0x6A3C00));
        generateGearTexture("tin_gear",
                new Color(0xF0F6FA), new Color(0xC5D6E3), new Color(0x7D94A8), new Color(0x445564));
    }

    // Common Die Base Frame
    private static BufferedImage createDieBase() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Outer square 14x14 from (1,1) to (14,14), chamfered corners
        Color hi = new Color(0x94, 0xA3, 0xB8);
        Color light = new Color(0x64, 0x74, 0x8B);
        Color mid = new Color(0x47, 0x55, 0x69);
        Color dark = new Color(0x33, 0x41, 0x55);
        Color shadow = new Color(0x1E, 0x29, 0x3B);
        Color black = new Color(0x0F, 0x17, 0x2A);

        // Fill body
        for (int y = 1; y <= 14; y++) {
            for (int x = 1; x <= 14; x++) {
                if ((x == 1 || x == 14) && (y == 1 || y == 14)) continue; // Chamfered corners
                img.setRGB(x, y, mid.getRGB());
            }
        }

        // Top & Left Highlight border
        for (int x = 2; x <= 13; x++) img.setRGB(x, 1, hi.getRGB());
        for (int y = 2; y <= 13; y++) img.setRGB(1, y, hi.getRGB());
        img.setRGB(2, 2, light.getRGB());

        // Bottom & Right Shadow border
        for (int x = 2; x <= 13; x++) img.setRGB(x, 14, black.getRGB());
        for (int y = 2; y <= 13; y++) img.setRGB(14, y, black.getRGB());
        for (int x = 2; x <= 13; x++) img.setRGB(x, 13, shadow.getRGB());
        for (int y = 2; y <= 13; y++) img.setRGB(13, y, shadow.getRGB());

        // Corner bolt holes
        int[][] bolts = {{2, 3}, {12, 3}, {2, 12}, {12, 12}};
        for (int[] b : bolts) {
            img.setRGB(b[0], b[1], black.getRGB());
            img.setRGB(b[0] + 1, b[1], dark.getRGB());
        }

        // Inner recessed die cavity from (4,4) to (11,11)
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, dark.getRGB());
            }
        }
        // Cavity inner shadow (top/left)
        for (int x = 4; x <= 11; x++) img.setRGB(x, 4, black.getRGB());
        for (int y = 4; y <= 11; y++) img.setRGB(4, y, black.getRGB());

        // Cavity bottom/right inner highlight
        for (int x = 4; x <= 11; x++) img.setRGB(x, 11, light.getRGB());
        for (int y = 4; y <= 11; y++) img.setRGB(11, y, light.getRGB());

        g.dispose();
        return img;
    }

    private static void generatePlateDieTexture() throws IOException {
        BufferedImage img = createDieBase();
        Color plateHi = new Color(0xCBD5E1);
        Color plateMid = new Color(0x94A3B8);
        Color plateShadow = new Color(0x475569);

        // Rectangular plate stamp in center: x 5..10, y 6..9
        for (int y = 6; y <= 9; y++) {
            for (int x = 5; x <= 10; x++) {
                img.setRGB(x, y, plateMid.getRGB());
            }
        }
        // Plate highlights
        for (int x = 5; x <= 10; x++) img.setRGB(x, 6, plateHi.getRGB());
        for (int y = 6; y <= 9; y++) img.setRGB(5, y, plateHi.getRGB());
        // Plate shadow
        for (int x = 5; x <= 10; x++) img.setRGB(x, 9, plateShadow.getRGB());
        for (int y = 6; y <= 9; y++) img.setRGB(10, y, plateShadow.getRGB());

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/plate_die.png"));
    }

    private static void generateGearDieTexture() throws IOException {
        BufferedImage img = createDieBase();
        Color gearHi = new Color(0xCBD5E1);
        Color gearMid = new Color(0x94A3B8);
        Color gearDark = new Color(0x334155);
        Color hole = new Color(0x0F172A);

        // Gear body in cavity
        for (int y = 6; y <= 9; y++) {
            for (int x = 6; x <= 9; x++) {
                img.setRGB(x, y, gearMid.getRGB());
            }
        }
        // 4 teeth
        img.setRGB(7, 5, gearHi.getRGB());
        img.setRGB(8, 5, gearHi.getRGB());
        img.setRGB(7, 10, gearDark.getRGB());
        img.setRGB(8, 10, gearDark.getRGB());
        img.setRGB(5, 7, gearHi.getRGB());
        img.setRGB(5, 8, gearHi.getRGB());
        img.setRGB(10, 7, gearDark.getRGB());
        img.setRGB(10, 8, gearDark.getRGB());

        // Center axle bore
        img.setRGB(7, 7, hole.getRGB());
        img.setRGB(8, 7, hole.getRGB());
        img.setRGB(7, 8, hole.getRGB());
        img.setRGB(8, 8, gearDark.getRGB());

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/gear_die.png"));
    }

    private static void generateCorrugatedDieTexture() throws IOException {
        BufferedImage img = createDieBase();
        Color ribHi = new Color(0xCBD5E1);
        Color ribMid = new Color(0x94A3B8);
        Color ribLow = new Color(0x1E293B);

        // 3 vertical corrugated ridges in cavity
        for (int y = 5; y <= 10; y++) {
            // Ridge 1
            img.setRGB(5, y, ribHi.getRGB());
            img.setRGB(6, y, ribLow.getRGB());
            // Ridge 2
            img.setRGB(7, y, ribHi.getRGB());
            img.setRGB(8, y, ribLow.getRGB());
            // Ridge 3
            img.setRGB(9, y, ribHi.getRGB());
            img.setRGB(10, y, ribLow.getRGB());
        }

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/corrugated_die.png"));
    }

    private static void generateRailDieTexture() throws IOException {
        BufferedImage img = createDieBase();
        Color railMetal = new Color(0xE2E8F0);
        Color tieWood = new Color(0x78350F);
        Color tieDark = new Color(0x451A03);

        // Wood sleepers (cross ties) at y=6, 8, 10 across x=5..10
        for (int x = 5; x <= 10; x++) {
            img.setRGB(x, 6, tieWood.getRGB());
            img.setRGB(x, 8, tieWood.getRGB());
            img.setRGB(x, 10, tieWood.getRGB());
        }
        for (int x = 5; x <= 10; x++) {
            img.setRGB(x, 7, tieDark.getRGB());
            img.setRGB(x, 9, tieDark.getRGB());
        }

        // Twin steel rail lines at x=6 and x=9
        for (int y = 5; y <= 10; y++) {
            img.setRGB(6, y, railMetal.getRGB());
            img.setRGB(9, y, railMetal.getRGB());
        }

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/rail_die.png"));
    }

    // 16x16 Mechanical Gear Sprite Generator
    private static void generateGearTexture(String name, Color hi, Color mid, Color dark, Color shadow) throws IOException {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // 1. Central disc hub: circle radius ~5 around (7.5, 7.5)
        for (int y = 3; y <= 12; y++) {
            for (int x = 3; x <= 12; x++) {
                double dist = Math.hypot(x - 7.5, y - 7.5);
                if (dist <= 4.6) {
                    if (y < 7 || (y == 7 && x <= 7)) {
                        img.setRGB(x, y, mid.getRGB());
                    } else {
                        img.setRGB(x, y, dark.getRGB());
                    }
                }
            }
        }

        // 2. 8 Protruding Gear Teeth
        // Top tooth (x: 7..8, y: 1..2)
        img.setRGB(7, 1, hi.getRGB());
        img.setRGB(8, 1, hi.getRGB());
        img.setRGB(7, 2, mid.getRGB());
        img.setRGB(8, 2, mid.getRGB());

        // Bottom tooth (x: 7..8, y: 13..14)
        img.setRGB(7, 13, dark.getRGB());
        img.setRGB(8, 13, dark.getRGB());
        img.setRGB(7, 14, shadow.getRGB());
        img.setRGB(8, 14, shadow.getRGB());

        // Left tooth (x: 1..2, y: 7..8)
        img.setRGB(1, 7, hi.getRGB());
        img.setRGB(1, 8, mid.getRGB());
        img.setRGB(2, 7, hi.getRGB());
        img.setRGB(2, 8, mid.getRGB());

        // Right tooth (x: 13..14, y: 7..8)
        img.setRGB(13, 7, dark.getRGB());
        img.setRGB(13, 8, shadow.getRGB());
        img.setRGB(14, 7, dark.getRGB());
        img.setRGB(14, 8, shadow.getRGB());

        // Top-left diagonal tooth (x: 3..4, y: 3..4)
        img.setRGB(3, 3, hi.getRGB());
        img.setRGB(4, 3, hi.getRGB());
        img.setRGB(3, 4, hi.getRGB());
        img.setRGB(4, 4, mid.getRGB());

        // Top-right diagonal tooth (x: 11..12, y: 3..4)
        img.setRGB(11, 3, mid.getRGB());
        img.setRGB(12, 3, dark.getRGB());
        img.setRGB(11, 4, mid.getRGB());
        img.setRGB(12, 4, dark.getRGB());

        // Bottom-left diagonal tooth (x: 3..4, y: 11..12)
        img.setRGB(3, 11, mid.getRGB());
        img.setRGB(4, 11, dark.getRGB());
        img.setRGB(3, 12, dark.getRGB());
        img.setRGB(4, 12, shadow.getRGB());

        // Bottom-right diagonal tooth (x: 11..12, y: 11..12)
        img.setRGB(11, 11, dark.getRGB());
        img.setRGB(12, 11, shadow.getRGB());
        img.setRGB(11, 12, shadow.getRGB());
        img.setRGB(12, 12, shadow.getRGB());

        // 3. Highlight bevel on top/left inner rim
        img.setRGB(6, 4, hi.getRGB());
        img.setRGB(7, 4, hi.getRGB());
        img.setRGB(8, 4, mid.getRGB());
        img.setRGB(4, 6, hi.getRGB());
        img.setRGB(4, 7, hi.getRGB());
        img.setRGB(4, 8, mid.getRGB());

        // 4. Shadow bevel on bottom/right inner rim
        img.setRGB(11, 7, shadow.getRGB());
        img.setRGB(11, 8, shadow.getRGB());
        img.setRGB(7, 11, shadow.getRGB());
        img.setRGB(8, 11, shadow.getRGB());
        img.setRGB(9, 11, shadow.getRGB());
        img.setRGB(10, 10, shadow.getRGB());

        // 5. Axle center bore (hole)
        Color bore = new Color(0x11, 0x14, 0x18);
        img.setRGB(7, 7, bore.getRGB());
        img.setRGB(8, 7, bore.getRGB());
        img.setRGB(7, 8, bore.getRGB());
        img.setRGB(8, 8, bore.getRGB());
        // Rim around bore
        img.setRGB(6, 7, hi.getRGB());
        img.setRGB(7, 6, hi.getRGB());
        img.setRGB(9, 8, shadow.getRGB());
        img.setRGB(8, 9, shadow.getRGB());

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/" + name + ".png"));
    }

    private static void generateModels() throws IOException {
        String[] items = {
                "plate_die", "gear_die", "corrugated_die", "rail_die",
                "iron_gear", "steel_gear", "copper_gear", "bronze_gear", "gold_gear", "tin_gear"
        };
        for (String id : items) {
            String json = """
                    {
                      "parent": "minecraft:item/generated",
                      "textures": {
                        "layer0": "refinery:item/%s"
                      }
                    }
                    """.formatted(id);
            try (FileWriter w = new FileWriter(ASSETS + "models/item/" + id + ".json")) {
                w.write(json);
            }
        }
    }

    private static void generateItemDefinitions() throws IOException {
        String[] items = {
                "plate_die", "gear_die", "corrugated_die", "rail_die",
                "iron_gear", "steel_gear", "copper_gear", "bronze_gear", "gold_gear", "tin_gear"
        };
        for (String id : items) {
            String json = """
                    {
                      "model": {
                        "type": "minecraft:model",
                        "model": "refinery:item/%s"
                      }
                    }
                    """.formatted(id);
            try (FileWriter w = new FileWriter(ASSETS + "items/" + id + ".json")) {
                w.write(json);
            }
        }
    }

    private static void generateTags() throws IOException {
        // refinery:press_dies
        String diesJson = """
                {
                  "replace": false,
                  "values": [
                    "refinery:plate_die",
                    "refinery:gear_die",
                    "refinery:corrugated_die",
                    "refinery:rail_die"
                  ]
                }
                """;
        try (FileWriter w = new FileWriter(DATA + "refinery/tags/item/press_dies.json")) {
            w.write(diesJson);
        }

        // c:gears
        String cGears = """
                {
                  "replace": false,
                  "values": [
                    "refinery:steel_gear",
                    "refinery:iron_gear",
                    "refinery:copper_gear",
                    "refinery:bronze_gear",
                    "refinery:gold_gear",
                    "refinery:tin_gear"
                  ]
                }
                """;
        try (FileWriter w = new FileWriter(DATA + "c/tags/item/gears.json")) {
            w.write(cGears);
        }

        // c:gears/<metal>
        writeSubTag("steel", "refinery:steel_gear");
        writeSubTag("iron", "refinery:iron_gear");
        writeSubTag("copper", "refinery:copper_gear");
        writeSubTag("bronze", "refinery:bronze_gear");
        writeSubTag("gold", "refinery:gold_gear");
        writeSubTag("tin", "refinery:tin_gear");

        // c:ingots/steel
        String steelIngotTag = """
                {
                  "replace": false,
                  "values": [
                    "refinery:steel_ingot",
                    {"id": "energizedpower:steel_ingot", "required": false},
                    {"id": "logistics:core/steel_ingot", "required": false}
                  ]
                }
                """;
        try (FileWriter w = new FileWriter(DATA + "c/tags/item/ingots/steel.json")) {
            w.write(steelIngotTag);
        }
    }

    private static void writeSubTag(String metal, String itemId) throws IOException {
        String json = """
                {
                  "replace": false,
                  "values": [
                    "%s"
                  ]
                }
                """.formatted(itemId);
        try (FileWriter w = new FileWriter(DATA + "c/tags/item/gears/" + metal + ".json")) {
            w.write(json);
        }
    }

    private static void generateRecipes() throws IOException {
        // 1. DIES CRAFTING RECIPES
        // Plate Die: 4 Steel Plates in line / pattern
        writeShapedRecipe("plate_die", """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "pattern": [
                    "PPP",
                    " P "
                  ],
                  "key": {
                    "P": "#c:plates/steel"
                  },
                  "result": {
                    "count": 1,
                    "id": "refinery:plate_die"
                  }
                }
                """);

        // Gear Die: 4 Steel Plates cross
        writeShapedRecipe("gear_die", """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "pattern": [
                    " P ",
                    "P P",
                    " P "
                  ],
                  "key": {
                    "P": "#c:plates/steel"
                  },
                  "result": {
                    "count": 1,
                    "id": "refinery:gear_die"
                  }
                }
                """);

        // Corrugated Die: 4 Steel Plates zig-zag
        writeShapedRecipe("corrugated_die", """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "pattern": [
                    "P P",
                    " P ",
                    "P P"
                  ],
                  "key": {
                    "P": "#c:plates/steel"
                  },
                  "result": {
                    "count": 1,
                    "id": "refinery:corrugated_die"
                  }
                }
                """);

        // Rail Die: 2 Steel Plates + 1 Iron Ingot
        writeShapedRecipe("rail_die", """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "pattern": [
                    "P P",
                    "PPP",
                    "P P"
                  ],
                  "key": {
                    "P": "#c:plates/steel"
                  },
                  "result": {
                    "count": 1,
                    "id": "refinery:rail_die"
                  }
                }
                """);

        // 2. GEARS CRAFTING RECIPES (4 Ingots -> 1 Gear)
        writeGearRecipe("steel_gear", "#c:ingots/steel");
        writeGearRecipe("iron_gear", "#c:ingots/iron");
        writeGearRecipe("copper_gear", "#c:ingots/copper");
        writeGearRecipe("bronze_gear", "#c:ingots/bronze");
        writeGearRecipe("gold_gear", "#c:ingots/gold");
        writeGearRecipe("tin_gear", "#c:ingots/tin");
    }

    private static void writeGearRecipe(String gearId, String ingredientTag) throws IOException {
        String json = """
                {
                  "type": "minecraft:crafting_shaped",
                  "category": "misc",
                  "pattern": [
                    " # ",
                    "# #",
                    " # "
                  ],
                  "key": {
                    "#": "%s"
                  },
                  "result": {
                    "count": 1,
                    "id": "refinery:%s"
                  }
                }
                """.formatted(ingredientTag, gearId);
        writeShapedRecipe(gearId, json);
    }

    private static void writeShapedRecipe(String name, String json) throws IOException {
        try (FileWriter w = new FileWriter(DATA + "refinery/recipe/" + name + ".json")) {
            w.write(json.trim());
        }
    }
}
