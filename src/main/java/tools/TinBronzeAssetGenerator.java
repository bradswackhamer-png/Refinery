package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TinBronzeAssetGenerator {
    private static final String RES = "src/main/resources/";
    private static final String ASSETS = RES + "assets/refinery/";
    private static final String DATA = RES + "data/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(ASSETS + "lang").mkdirs();
        new File(DATA + "refinery/recipe").mkdirs();
        new File(DATA + "refinery/loot_table/blocks").mkdirs();
        new File(DATA + "refinery/worldgen/configured_feature").mkdirs();
        new File(DATA + "refinery/worldgen/placed_feature").mkdirs();
        new File(DATA + "refinery/neoforge/biome_modifier").mkdirs();
        new File(DATA + "c/tags/block/ores").mkdirs();
        new File(DATA + "c/tags/block/storage_blocks").mkdirs();
        new File(DATA + "c/tags/item/ores").mkdirs();
        new File(DATA + "c/tags/item/raw_materials").mkdirs();
        new File(DATA + "c/tags/item/storage_blocks").mkdirs();
        new File(DATA + "minecraft/tags/block/mineable").mkdirs();
        new File(DATA + "minecraft/tags/blocks/mineable").mkdirs();

        generateTextures();
        generateModelsAndBlockstates();
        generateItemDefinitions();
        generateLootTables();
        generateRecipes();
        generateTags();
        generateWorldgen();

        System.out.println("Tin & Bronze assets generated successfully!");
    }

    private static void generateTextures() throws Exception {
        // Try reading vanilla textures from gradle cache
        File mcJar = new File("C:/Users/swack/.gradle/caches/neoformruntime/artifacts/minecraft_26.3_client.jar");
        BufferedImage stone = null;
        BufferedImage deepslate = null;
        BufferedImage copperOre = null;
        BufferedImage rawCopper = null;

        if (mcJar.exists()) {
            try (ZipFile zip = new ZipFile(mcJar)) {
                ZipEntry eStone = zip.getEntry("assets/minecraft/textures/block/stone.png");
                if (eStone != null) try (InputStream in = zip.getInputStream(eStone)) { stone = ImageIO.read(in); }

                ZipEntry eDeep = zip.getEntry("assets/minecraft/textures/block/deepslate.png");
                if (eDeep != null) try (InputStream in = zip.getInputStream(eDeep)) { deepslate = ImageIO.read(in); }

                ZipEntry eCop = zip.getEntry("assets/minecraft/textures/block/copper_ore.png");
                if (eCop != null) try (InputStream in = zip.getInputStream(eCop)) { copperOre = ImageIO.read(in); }

                ZipEntry eRawCop = zip.getEntry("assets/minecraft/textures/item/raw_copper.png");
                if (eRawCop != null) try (InputStream in = zip.getInputStream(eRawCop)) { rawCopper = ImageIO.read(in); }
            }
        }

        // 1. Tin Ore
        BufferedImage tinOre = createOreTexture(stone, copperOre, false);
        ImageIO.write(tinOre, "PNG", new File(ASSETS + "textures/block/tin_ore.png"));

        // 2. Deepslate Tin Ore
        BufferedImage deepslateTinOre = createOreTexture(deepslate, copperOre, true);
        ImageIO.write(deepslateTinOre, "PNG", new File(ASSETS + "textures/block/deepslate_tin_ore.png"));

        // 3. Raw Tin
        BufferedImage rawTin = createRawTinTexture(rawCopper);
        ImageIO.write(rawTin, "PNG", new File(ASSETS + "textures/item/raw_tin.png"));

        // 4. Raw Tin Block
        BufferedImage rawTinBlock = createRawTinBlockTexture();
        ImageIO.write(rawTinBlock, "PNG", new File(ASSETS + "textures/block/raw_tin_block.png"));

        // 5. Tin Block
        BufferedImage tinBlock = createMetalBlockTexture(new Color(0xD8, 0xE2, 0xEC), new Color(0x9E, 0xAA, 0xB8), new Color(0x6A, 0x75, 0x82), new Color(0xF0, 0xF6, 0xFC));
        ImageIO.write(tinBlock, "PNG", new File(ASSETS + "textures/block/tin_block.png"));

        // 6. Bronze Block
        BufferedImage bronzeBlock = createMetalBlockTexture(new Color(0xD9, 0x77, 0x24), new Color(0xB4, 0x53, 0x09), new Color(0x78, 0x35, 0x0F), new Color(0xF5, 0x9E, 0x0B));
        ImageIO.write(bronzeBlock, "PNG", new File(ASSETS + "textures/block/bronze_block.png"));
    }

    private static BufferedImage createOreTexture(BufferedImage baseRock, BufferedImage refOre, boolean isDeepslate) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        if (baseRock != null) {
            g.drawImage(baseRock, 0, 0, null);
        } else {
            // Procedural rock fallback
            int baseGray = isDeepslate ? 45 : 128;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    int variance = ((x * 13 + y * 7 + (x ^ y)) % 15) - 7;
                    int c = Math.max(0, Math.min(255, baseGray + variance));
                    img.setRGB(x, y, new Color(c, c, c).getRGB());
                }
            }
        }

        // Overlay tin ore flecks (silvery-gray with soft steel-cyan luster)
        // Authentic mineral deposit layout
        int[][] flecks = {
                {4, 3, 0}, {5, 3, 1}, {5, 4, 2}, {6, 4, 1}, {6, 5, 3}, {7, 4, 2},
                {10, 2, 1}, {11, 2, 0}, {11, 3, 2}, {12, 3, 3},
                {2, 9, 2}, {3, 9, 1}, {3, 10, 0}, {4, 10, 1}, {4, 11, 2},
                {8, 8, 1}, {9, 8, 0}, {9, 9, 2}, {10, 9, 1}, {10, 10, 3},
                {12, 11, 2}, {13, 11, 1}, {13, 12, 0}, {14, 12, 2},
                {6, 13, 1}, {7, 13, 0}, {7, 14, 2}, {8, 14, 3}
        };

        Color[] palette = {
                new Color(0xF1, 0xF5, 0xF9), // 0: Specular highlight
                new Color(0xCB, 0xD5, 0xE1), // 1: Primary light tin
                new Color(0x94, 0xA3, 0xB8), // 2: Midtone tin
                new Color(0x64, 0x74, 0x8B), // 3: Shadow / edge
                new Color(0x33, 0x41, 0x55)  // 4: Cavity depth
        };

        for (int[] fleck : flecks) {
            int x = fleck[0];
            int y = fleck[1];
            int cIdx = fleck[2];
            img.setRGB(x, y, palette[cIdx].getRGB());
        }

        g.dispose();
        return img;
    }

    private static BufferedImage createRawTinTexture(BufferedImage refRaw) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Chunky raw nodule cluster shape (16x16)
        Color hi = new Color(0xF1, 0xF5, 0xF9);
        Color light = new Color(0xCB, 0xD5, 0xE1);
        Color mid = new Color(0x94, 0xA3, 0xB8);
        Color dark = new Color(0x64, 0x74, 0x8B);
        Color deep = new Color(0x33, 0x41, 0x55);
        Color out = new Color(0x1E, 0x29, 0x3B);

        int[][] pixels = {
                // y, x, color
                {3, 7, 0}, {3, 8, 1},
                {4, 6, 0}, {4, 7, 1}, {4, 8, 1}, {4, 9, 2},
                {5, 5, 0}, {5, 6, 1}, {5, 7, 1}, {5, 8, 2}, {5, 9, 3}, {5, 10, 4},
                {6, 4, 0}, {6, 5, 1}, {6, 6, 1}, {6, 7, 2}, {6, 8, 3}, {6, 9, 4}, {6, 10, 4},
                {7, 4, 1}, {7, 5, 1}, {7, 6, 2}, {7, 7, 2}, {7, 8, 3}, {7, 9, 4}, {7, 11, 2},
                {8, 4, 2}, {8, 5, 2}, {8, 6, 3}, {8, 7, 3}, {8, 8, 4}, {8, 9, 4}, {8, 10, 3}, {8, 11, 1}, {8, 12, 2},
                {9, 5, 3}, {9, 6, 3}, {9, 7, 4}, {9, 8, 4}, {9, 9, 3}, {9, 10, 2}, {9, 11, 2}, {9, 12, 3},
                {10, 6, 4}, {10, 7, 4}, {10, 8, 3}, {10, 9, 2}, {10, 10, 2}, {10, 11, 3},
                {11, 7, 3}, {11, 8, 3}, {11, 9, 3}, {11, 10, 4},
                {12, 8, 4}, {12, 9, 4}
        };

        Color[] pal = {hi, light, mid, dark, deep};
        for (int[] p : pixels) {
            img.setRGB(p[1], p[0], pal[p[2]].getRGB());
        }

        // Draw subtle outline
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (img.getRGB(x, y) != 0) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < 16 && ny >= 0 && ny < 16 && img.getRGB(nx, ny) == 0) {
                                // border shadow
                                img.setRGB(nx, ny, new Color(0x1E, 0x29, 0x3B, 180).getRGB());
                            }
                        }
                    }
                }
            }
        }

        return img;
    }

    private static BufferedImage createRawTinBlockTexture() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color[] pal = {
                new Color(0xF1, 0xF5, 0xF9), // highlight
                new Color(0xCB, 0xD5, 0xE1), // light
                new Color(0x94, 0xA3, 0xB8), // mid
                new Color(0x64, 0x74, 0x8B), // dark
                new Color(0x33, 0x41, 0x55)  // deep
        };

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int pattern = ((x * 11 + y * 17 + (x ^ y)) % 5);
                img.setRGB(x, y, pal[pattern].getRGB());
            }
        }

        // Draw cobblestone/cluster seams
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x1E, 0x29, 0x3B, 180));
        g.drawRect(0, 0, 7, 7);
        g.drawRect(8, 0, 7, 7);
        g.drawRect(0, 8, 7, 7);
        g.drawRect(8, 8, 7, 7);
        g.dispose();

        return img;
    }

    private static BufferedImage createMetalBlockTexture(Color main, Color shade, Color border, Color shine) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Base fill
        g.setColor(main);
        g.fillRect(0, 0, 16, 16);

        // Subtle industrial noise
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int noise = ((x * 7 + y * 13) % 9) - 4;
                int r = Math.max(0, Math.min(255, main.getRed() + noise));
                int gr = Math.max(0, Math.min(255, main.getGreen() + noise));
                int b = Math.max(0, Math.min(255, main.getBlue() + noise));
                img.setRGB(x, y, new Color(r, gr, b).getRGB());
            }
        }

        // Beveled frame
        g.setColor(shine);
        g.drawLine(0, 0, 15, 0);
        g.drawLine(0, 0, 0, 15);

        g.setColor(border);
        g.drawLine(0, 15, 15, 15);
        g.drawLine(15, 0, 15, 15);

        // Inner beveled plate
        g.setColor(shade);
        g.drawRect(2, 2, 11, 11);

        // Corner rivets
        g.setColor(shine);
        g.fillRect(3, 3, 1, 1);
        g.fillRect(12, 3, 1, 1);
        g.fillRect(3, 12, 1, 1);
        g.fillRect(12, 12, 1, 1);

        g.dispose();
        return img;
    }

    private static void generateModelsAndBlockstates() throws Exception {
        String[] blocks = {"tin_ore", "deepslate_tin_ore", "raw_tin_block", "tin_block", "bronze_block"};

        for (String b : blocks) {
            // Blockstate
            String bs = """
            {
              "variants": {
                "": { "model": "refinery:block/%s" }
              }
            }
            """.formatted(b);
            Files.writeString(Paths.get(ASSETS + "blockstates/" + b + ".json"), bs);

            // Block Model
            String bm = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "refinery:block/%s"
              }
            }
            """.formatted(b);
            Files.writeString(Paths.get(ASSETS + "models/block/" + b + ".json"), bm);

            // Item Model
            String im = """
            {
              "parent": "refinery:block/%s"
            }
            """.formatted(b);
            Files.writeString(Paths.get(ASSETS + "models/item/" + b + ".json"), im);
        }

        // Raw Tin item model
        String rawTinModel = """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/raw_tin"
          }
        }
        """;
        Files.writeString(Paths.get(ASSETS + "models/item/raw_tin.json"), rawTinModel);
    }

    private static void generateItemDefinitions() throws Exception {
        String[] allItems = {
                "tin_ore", "deepslate_tin_ore", "raw_tin_block", "tin_block", "bronze_block", "raw_tin"
        };

        for (String id : allItems) {
            String itemDef = """
            {
              "model": {
                "type": "minecraft:model",
                "model": "refinery:item/%s"
              }
            }
            """.formatted(id);
            Files.writeString(Paths.get(ASSETS + "items/" + id + ".json"), itemDef);
        }
    }

    private static void generateLootTables() throws Exception {
        // Simple drops self
        String[] selfDrops = {"tin_block", "bronze_block", "raw_tin_block"};
        for (String b : selfDrops) {
            String lt = """
            {
              "type": "minecraft:block",
              "pools": [
                {
                  "rolls": 1.0,
                  "bonus_rolls": 0.0,
                  "entries": [
                    {
                      "type": "minecraft:item",
                      "name": "refinery:%s"
                    }
                  ],
                  "conditions": [
                    {
                      "condition": "minecraft:survives_explosion"
                    }
                  ]
                }
              ],
              "random_sequence": "refinery:blocks/%s"
            }
            """.formatted(b, b);
            Files.writeString(Paths.get(DATA + "refinery/loot_table/blocks/" + b + ".json"), lt);
        }

        // Tin Ore with Silk Touch / Fortune
        writeOreLootTable("tin_ore");
        writeOreLootTable("deepslate_tin_ore");
    }

    private static void writeOreLootTable(String oreId) throws Exception {
        String lt = """
        {
          "type": "minecraft:block",
          "pools": [
            {
              "rolls": 1.0,
              "bonus_rolls": 0.0,
              "entries": [
                {
                  "type": "minecraft:alternatives",
                  "children": [
                    {
                      "type": "minecraft:item",
                      "name": "refinery:%s",
                      "conditions": [
                        {
                          "condition": "minecraft:match_tool",
                          "predicate": {
                            "predicates": {
                              "minecraft:enchantments": [
                                {
                                  "enchantments": "#minecraft:enchantments/silk_touch",
                                  "levels": {
                                    "min": 1
                                  }
                                }
                              ]
                            }
                          }
                        }
                      ]
                    },
                    {
                      "type": "minecraft:item",
                      "name": "refinery:raw_tin",
                      "functions": [
                        {
                          "function": "minecraft:apply_bonus",
                          "enchantment": "minecraft:fortune",
                          "formula": "minecraft:ore_drops"
                        },
                        {
                          "function": "minecraft:explosion_decay"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ],
          "random_sequence": "refinery:blocks/%s"
        }
        """.formatted(oreId, oreId);
        Files.writeString(Paths.get(DATA + "refinery/loot_table/blocks/" + oreId + ".json"), lt);
    }

    private static void generateRecipes() throws Exception {
        // 1. Bronze from 1 Copper + 1 Tin = 2 Bronze Ingots
        String bronzeIngot = """
        {
          "type": "minecraft:crafting_shapeless",
          "ingredients": [
            "#c:ingots/copper",
            "#c:ingots/tin"
          ],
          "result": {
            "id": "refinery:bronze_ingot",
            "count": 2
          }
        }
        """;
        Files.writeString(Paths.get(DATA + "refinery/recipe/bronze_ingot_from_alloying.json"), bronzeIngot);

        // 2. Bronze Dust from 1 Copper Dust + 1 Tin Dust = 2 Bronze Dust
        String bronzeDust = """
        {
          "type": "minecraft:crafting_shapeless",
          "ingredients": [
            "#c:dusts/copper",
            "#c:dusts/tin"
          ],
          "result": {
            "id": "refinery:bronze_dust",
            "count": 2
          }
        }
        """;
        Files.writeString(Paths.get(DATA + "refinery/recipe/bronze_dust_from_alloying.json"), bronzeDust);

        // 3. Storage block compressions & decompressions
        writeCompression("tin_block", "refinery:tin_ingot", "refinery:tin_block");
        writeDecompression("tin_ingot_from_tin_block", "refinery:tin_block", "refinery:tin_ingot");

        writeCompression("bronze_block", "refinery:bronze_ingot", "refinery:bronze_block");
        writeDecompression("bronze_ingot_from_bronze_block", "refinery:bronze_block", "refinery:bronze_ingot");

        writeCompression("raw_tin_block", "refinery:raw_tin", "refinery:raw_tin_block");
        writeDecompression("raw_tin_from_raw_tin_block", "refinery:raw_tin_block", "refinery:raw_tin");

        // 4. Smelting & Blasting
        writeSmeltingBlasting("tin_ingot_from_smelting_raw_tin", "tin_ingot_from_blasting_raw_tin", "refinery:raw_tin");
        writeSmeltingBlastingTag("tin_ingot_from_smelting_tin_ore", "tin_ingot_from_blasting_tin_ore", "c:ores/tin");
        writeSmeltingBlasting("tin_ingot_from_smelting_deepslate_tin_ore", "tin_ingot_from_blasting_deepslate_tin_ore", "refinery:deepslate_tin_ore");
    }

    private static void writeCompression(String name, String input, String result) throws Exception {
        String json = """
        {
          "type": "minecraft:crafting_shaped",
          "category": "building",
          "key": {
            "#": "%s"
          },
          "pattern": [
            "###",
            "###",
            "###"
          ],
          "result": {
            "count": 1,
            "id": "%s"
          }
        }
        """.formatted(input, result);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + name + ".json"), json);
    }

    private static void writeDecompression(String name, String input, String result) throws Exception {
        String json = """
        {
          "type": "minecraft:crafting_shapeless",
          "category": "misc",
          "ingredients": [
            "%s"
          ],
          "result": {
            "count": 9,
            "id": "%s"
          }
        }
        """.formatted(input, result);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + name + ".json"), json);
    }

    private static void writeSmeltingBlasting(String smeltName, String blastName, String inputItem) throws Exception {
        String smelt = """
        {
          "type": "minecraft:smelting",
          "category": "misc",
          "cookingtime": 200,
          "experience": 0.7,
          "ingredient": "%s",
          "result": {
            "id": "refinery:tin_ingot"
          }
        }
        """.formatted(inputItem);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + smeltName + ".json"), smelt);

        String blast = """
        {
          "type": "minecraft:blasting",
          "category": "misc",
          "cookingtime": 100,
          "experience": 0.7,
          "ingredient": "%s",
          "result": {
            "id": "refinery:tin_ingot"
          }
        }
        """.formatted(inputItem);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + blastName + ".json"), blast);
    }

    private static void writeSmeltingBlastingTag(String smeltName, String blastName, String inputTag) throws Exception {
        String smelt = """
        {
          "type": "minecraft:smelting",
          "category": "misc",
          "cookingtime": 200,
          "experience": 0.7,
          "ingredient": "#%s",
          "result": {
            "id": "refinery:tin_ingot"
          }
        }
        """.formatted(inputTag);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + smeltName + ".json"), smelt);

        String blast = """
        {
          "type": "minecraft:blasting",
          "category": "misc",
          "cookingtime": 100,
          "experience": 0.7,
          "ingredient": "#%s",
          "result": {
            "id": "refinery:tin_ingot"
          }
        }
        """.formatted(inputTag);
        Files.writeString(Paths.get(DATA + "refinery/recipe/" + blastName + ".json"), blast);
    }

    private static void generateTags() throws Exception {
        // Block tags: ores/tin
        Files.writeString(Paths.get(DATA + "c/tags/block/ores/tin.json"), """
        {
          "replace": false,
          "values": [
            "refinery:tin_ore",
            "refinery:deepslate_tin_ore"
          ]
        }
        """);

        // Item tags: ores/tin
        Files.writeString(Paths.get(DATA + "c/tags/item/ores/tin.json"), """
        {
          "replace": false,
          "values": [
            "refinery:tin_ore",
            "refinery:deepslate_tin_ore"
          ]
        }
        """);

        // Item tags: raw_materials/tin
        Files.writeString(Paths.get(DATA + "c/tags/item/raw_materials/tin.json"), """
        {
          "replace": false,
          "values": [
            "refinery:raw_tin"
          ]
        }
        """);

        // Block storage tags
        Files.writeString(Paths.get(DATA + "c/tags/block/storage_blocks/tin.json"), """
        {
          "replace": false,
          "values": ["refinery:tin_block"]
        }
        """);
        Files.writeString(Paths.get(DATA + "c/tags/block/storage_blocks/bronze.json"), """
        {
          "replace": false,
          "values": ["refinery:bronze_block"]
        }
        """);
        Files.writeString(Paths.get(DATA + "c/tags/block/storage_blocks/raw_tin.json"), """
        {
          "replace": false,
          "values": ["refinery:raw_tin_block"]
        }
        """);

        // Item storage tags
        Files.writeString(Paths.get(DATA + "c/tags/item/storage_blocks/tin.json"), """
        {
          "replace": false,
          "values": ["refinery:tin_block"]
        }
        """);
        Files.writeString(Paths.get(DATA + "c/tags/item/storage_blocks/bronze.json"), """
        {
          "replace": false,
          "values": ["refinery:bronze_block"]
        }
        """);
        Files.writeString(Paths.get(DATA + "c/tags/item/storage_blocks/raw_tin.json"), """
        {
          "replace": false,
          "values": ["refinery:raw_tin_block"]
        }
        """);
    }

    private static void generateWorldgen() throws Exception {
        // Configured Feature
        String cfg = """
        {
          "type": "minecraft:ore",
          "config": {
            "discard_chance_on_air_exposure": 0.0,
            "size": 10,
            "targets": [
              {
                "state": {
                  "Name": "refinery:tin_ore"
                },
                "target": {
                  "predicate_type": "minecraft:tag_match",
                  "tag": "minecraft:stone_ore_replaceables"
                }
              },
              {
                "state": {
                  "Name": "refinery:deepslate_tin_ore"
                },
                "target": {
                  "predicate_type": "minecraft:tag_match",
                  "tag": "minecraft:deepslate_ore_replaceables"
                }
              }
            ]
          }
        }
        """;
        Files.writeString(Paths.get(DATA + "refinery/worldgen/configured_feature/ore_tin.json"), cfg);

        // Placed Feature (Identical frequency and height range to Vanilla Copper: 16 count, triangle -16 to 112)
        String placed = """
        {
          "feature": "refinery:ore_tin",
          "placement": [
            {
              "type": "minecraft:count",
              "count": 16
            },
            {
              "type": "minecraft:in_square"
            },
            {
              "type": "minecraft:height_range",
              "height": {
                "type": "minecraft:trapezoid",
                "max_inclusive": {
                  "absolute": 112
                },
                "min_inclusive": {
                  "absolute": -16
                }
              }
            },
            {
              "type": "minecraft:biome"
            }
          ]
        }
        """;
        Files.writeString(Paths.get(DATA + "refinery/worldgen/placed_feature/ore_tin.json"), placed);

        // NeoForge Biome Modifier
        String bm = """
        {
          "type": "neoforge:add_features",
          "biomes": "#minecraft:is_overworld",
          "features": "refinery:ore_tin",
          "step": "underground_ores"
        }
        """;
        Files.writeString(Paths.get(DATA + "refinery/neoforge/biome_modifier/add_tin_ore.json"), bm);
    }
}
