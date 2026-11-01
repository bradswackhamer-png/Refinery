package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

/**
 * High-Fidelity Asset Generator for Industrial Native Seamless 3D Pipes:
 * - 128x128 seamless cylindrical metal and wood pipe textures
 * - 32x32 isometric item icons with realistic 3D round pipe graphics
 * - Blockstate, model, loot table, and recipe JSON files
 */
public class PipeAssetGenerator {

    private static final String ASSETS = "src/main/resources/assets/refinery/";
    private static final String DATA = "src/main/resources/data/refinery/";

    public static final String[] ALL_PIPE_IDS = {
            "wood_item_pipe",
            "wood_fluid_pipe",
            "copper_item_pipe",
            "copper_fluid_pipe",
            "iron_item_pipe",
            "iron_fluid_pipe",
            "steel_item_pipe",
            "steel_fluid_pipe",
            "gold_item_pipe",
            "gold_fluid_pipe",
            "diamond_item_pipe",
            "diamond_fluid_pipe",
            "emerald_item_pipe",
            "cast_iron_fluid_pipe",
            "brass_item_pipe",
            "fluid_extractor_pipe",
            "item_extractor_pipe",
            "void_pipe",
            "merge_pipe",
            "merge_item_pipe",
            "merge_fluid_pipe"
    };

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/block/pipe").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(DATA + "loot_table/blocks").mkdirs();
        new File(DATA + "recipe").mkdirs();

        generatePipeTextures();
        generateItemTextures();
        generateJsonFiles();

        System.out.println("Native Industrial Pipe assets and recipes generated successfully!");
    }

    public static void generatePipeTextures() throws Exception {
        generateMetalPipeTexture("wood_pipe.png", new Color(145, 95, 50), new Color(100, 60, 30), new Color(195, 145, 90), false, true);
        generateMetalPipeTexture("copper_pipe.png", new Color(210, 125, 80), new Color(150, 75, 45), new Color(255, 180, 140), false, false);
        generateMetalPipeTexture("iron_pipe.png", new Color(215, 215, 220), new Color(150, 150, 155), new Color(250, 250, 255), false, false);
        generateMetalPipeTexture("steel_pipe.png", new Color(185, 195, 205), new Color(130, 140, 150), new Color(230, 240, 250), false, false);
        generateMetalPipeTexture("gold_pipe.png", new Color(245, 195, 35), new Color(180, 140, 20), new Color(255, 235, 120), false, false);
        generateMetalPipeTexture("diamond_pipe.png", new Color(55, 235, 205), new Color(30, 165, 150), new Color(180, 255, 245), false, false);
        generateMetalPipeTexture("emerald_pipe.png", new Color(25, 220, 100), new Color(15, 150, 65), new Color(140, 255, 180), false, false);
        generateMetalPipeTexture("brass_pipe.png", new Color(225, 185, 65), new Color(165, 130, 35), new Color(255, 230, 130), false, false);
        generateMetalPipeTexture("cast_iron_pipe.png", new Color(85, 90, 95), new Color(50, 52, 56), new Color(135, 140, 145), false, false);
        generateMetalPipeTexture("void_pipe.png", new Color(35, 30, 50), new Color(15, 12, 25), new Color(160, 60, 230), true, false);
        generateMetalPipeTexture("quartz_pipe.png", new Color(240, 235, 230), new Color(195, 185, 180), new Color(255, 255, 255), false, false);
        generateWindowTexture();
    }

    private static void generateWindowTexture() throws Exception {
        int width = 32;
        int height = 32;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color darkBorder = new Color(35, 38, 44, 255);
        Color lightBorder = new Color(85, 92, 102, 255);
        Color glassBase = new Color(210, 240, 255, 35);
        Color glare = new Color(255, 255, 255, 120);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width - 1) {
                    img.setRGB(x, y, darkBorder.getRGB());
                } else if (x == 1 || x == width - 2) {
                    img.setRGB(x, y, lightBorder.getRGB());
                } else {
                    if ((x + y >= 11 && x + y <= 13) || (x + y >= 37 && x + y <= 39)) {
                        img.setRGB(x, y, glare.getRGB());
                    } else {
                        img.setRGB(x, y, glassBase.getRGB());
                    }
                }
            }
        }

        ImageIO.write(img, "PNG", new File(ASSETS + "textures/block/pipe/pipe_window.png"));
    }

    private static void generateMetalPipeTexture(String filename, Color baseColor, Color darkShade, Color highlight, boolean isVoid, boolean isWood) throws Exception {
        int width = 128;
        int height = 128;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Cylindrical lighting gradient across width (circumference)
        for (int x = 0; x < width; x++) {
            double angle = (2.0 * Math.PI * x) / width;
            double diffuse = Math.cos(angle);
            double specular = Math.pow(Math.max(0.0, Math.cos(angle - 0.5)), isWood ? 2.0 : 4.0);

            float r, gCol, b;
            if (diffuse > 0) {
                float t = (float) diffuse;
                r = baseColor.getRed() * (1 - t) + highlight.getRed() * t;
                gCol = baseColor.getGreen() * (1 - t) + highlight.getGreen() * t;
                b = baseColor.getBlue() * (1 - t) + highlight.getBlue() * t;
            } else {
                float t = (float) -diffuse;
                r = baseColor.getRed() * (1 - t) + darkShade.getRed() * t;
                gCol = baseColor.getGreen() * (1 - t) + darkShade.getGreen() * t;
                b = baseColor.getBlue() * (1 - t) + darkShade.getBlue() * t;
            }

            if (specular > 0.05 && !isWood) {
                float st = (float) Math.min(1.0, specular * 0.7);
                r = r * (1 - st) + 255 * st;
                gCol = gCol * (1 - st) + 255 * st;
                b = b * (1 - st) + 255 * st;
            }

            for (int y = 0; y < height; y++) {
                int grain;
                if (isWood) {
                    // Wood grain rings along the tube
                    grain = (int) (Math.sin(y * 0.35 + Math.sin(x * 0.1) * 2.0) * 14.0) + ((x * 19 + y * 31) % 9 - 4);
                } else {
                    grain = ((x * 37 + y * 13) % 7) - 3;
                }
                int cr = Math.min(255, Math.max(0, (int) r + grain));
                int cg = Math.min(255, Math.max(0, (int) gCol + grain));
                int cb = Math.min(255, Math.max(0, (int) b + grain));

                if (isVoid && (y % 32 >= 14 && y % 32 <= 17)) {
                    cr = Math.min(255, cr + 120);
                    cg = Math.min(255, cg + 20);
                    cb = Math.min(255, cb + 180);
                }

                img.setRGB(x, y, new Color(cr, cg, cb, 255).getRGB());
            }
        }

        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/block/pipe/" + filename));
    }

    public static void generateItemTextures() throws Exception {
        for (String id : ALL_PIPE_IDS) {
            BufferedImage itemImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = itemImg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color base = getColorForId(id, false);
            Color hi = getColorForId(id, true);

            // Pipe Body Cylinder (Diagonal across item square)
            Stroke pipeStroke = new BasicStroke(7.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g.setStroke(pipeStroke);
            g.setColor(base.darker());
            g.drawLine(5, 27, 27, 5);

            g.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(base);
            g.drawLine(5, 27, 27, 5);

            // Specular reflection streak
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(hi);
            g.drawLine(7, 23, 23, 7);

            // Flanged Collars at endpoints
            g.setColor(base.darker().darker());
            g.fillOval(2, 23, 8, 8);
            g.fillOval(22, 3, 8, 8);

            g.setColor(hi);
            g.drawOval(2, 23, 8, 8);
            g.drawOval(22, 3, 8, 8);

            // Waterproof Seal / Fluid Indicator ring for fluid pipes
            if (id.contains("fluid")) {
                g.setColor(new Color(40, 160, 240, 200));
                g.setStroke(new BasicStroke(2.0f));
                g.drawOval(13, 13, 6, 6);
            }

            // Extractors badge
            if (id.contains("extractor")) {
                g.setColor(new Color(255, 215, 0));
                g.fillPolygon(new int[]{13, 19, 16}, new int[]{19, 19, 13}, 3);
            }

            // Merge Pipe badge
            if (id.contains("merge")) {
                g.setColor(new Color(255, 200, 30));
                g.fillPolygon(new int[]{12, 19, 15}, new int[]{15, 12, 19}, 3);
            }

            g.dispose();
            ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/item/" + id + ".png"));
        }
    }

    private static Color getColorForId(String id, boolean highlight) {
        if (id.contains("wood")) {
            return highlight ? new Color(195, 145, 90) : new Color(145, 95, 50);
        } else if (id.contains("copper")) {
            return highlight ? new Color(255, 180, 140) : new Color(210, 125, 80);
        } else if (id.contains("iron") && !id.contains("cast")) {
            return highlight ? new Color(250, 250, 255) : new Color(215, 215, 220);
        } else if (id.contains("steel")) {
            return highlight ? new Color(230, 240, 250) : new Color(185, 195, 205);
        } else if (id.contains("gold")) {
            return highlight ? new Color(255, 235, 120) : new Color(245, 195, 35);
        } else if (id.contains("diamond")) {
            return highlight ? new Color(180, 255, 245) : new Color(55, 235, 205);
        } else if (id.contains("emerald")) {
            return highlight ? new Color(140, 255, 180) : new Color(25, 220, 100);
        } else if (id.contains("brass")) {
            return highlight ? new Color(255, 230, 130) : new Color(225, 185, 65);
        } else if (id.contains("cast_iron")) {
            return highlight ? new Color(130, 135, 140) : new Color(75, 80, 85);
        } else if (id.contains("void")) {
            return highlight ? new Color(180, 80, 255) : new Color(40, 30, 60);
        } else if (id.contains("quartz") || id.contains("merge")) {
            return highlight ? new Color(255, 255, 255) : new Color(235, 230, 225);
        }
        return highlight ? new Color(230, 240, 250) : new Color(185, 195, 205);
    }

    public static void generateJsonFiles() throws Exception {
        for (String id : ALL_PIPE_IDS) {
            String texturePath = "refinery:block/pipe/" + getTextureFilenameForId(id);

            // 1. Blockstate JSON
            String blockstate = "{\n" +
                    "  \"variants\": {\n" +
                    "    \"\": { \"model\": \"refinery:block/" + id + "\" }\n" +
                    "  }\n" +
                    "}";
            try (FileWriter fw = new FileWriter(ASSETS + "blockstates/" + id + ".json")) {
                fw.write(blockstate);
            }

            // 2. Block Model JSON
            String blockModel = "{\n" +
                    "  \"parent\": \"minecraft:block/block\",\n" +
                    "  \"textures\": {\n" +
                    "    \"particle\": \"" + texturePath + "\"\n" +
                    "  }\n" +
                    "}";
            try (FileWriter fw = new FileWriter(ASSETS + "models/block/" + id + ".json")) {
                fw.write(blockModel);
            }

            // 3. Item Model JSON
            String itemModel = "{\n" +
                    "  \"parent\": \"minecraft:item/generated\",\n" +
                    "  \"textures\": {\n" +
                    "    \"layer0\": \"refinery:item/" + id + "\"\n" +
                    "  }\n" +
                    "}";
            try (FileWriter fw = new FileWriter(ASSETS + "models/item/" + id + ".json")) {
                fw.write(itemModel);
            }

            // 4. Modern MC 26.3 Item Definition JSON (assets/refinery/items/<id>.json)
            String itemDef = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"refinery:item/" + id + "\"\n" +
                    "  }\n" +
                    "}";
            try (FileWriter fw = new FileWriter(ASSETS + "items/" + id + ".json")) {
                fw.write(itemDef);
            }

            // 5. Loot Table JSON
            String lootTable = "{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"bonus_rolls\": 0.0,\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"name\": \"refinery:" + id + "\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"rolls\": 1.0\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            try (FileWriter fw = new FileWriter(DATA + "loot_table/blocks/" + id + ".json")) {
                fw.write(lootTable);
            }
        }

        // Crafting Recipes
        generateRecipes();
    }

    private static String getTextureFilenameForId(String id) {
        if (id.contains("wood")) return "wood_pipe";
        if (id.contains("copper")) return "copper_pipe";
        if (id.contains("iron") && !id.contains("cast")) return "iron_pipe";
        if (id.contains("steel")) return "steel_pipe";
        if (id.contains("gold")) return "gold_pipe";
        if (id.contains("diamond")) return "diamond_pipe";
        if (id.contains("emerald")) return "emerald_pipe";
        if (id.contains("brass")) return "brass_pipe";
        if (id.contains("cast_iron")) return "cast_iron_pipe";
        if (id.contains("void")) return "void_pipe";
        if (id.contains("quartz") || id.contains("merge")) return "quartz_pipe";
        return "steel_pipe";
    }

    private static void generateRecipes() throws Exception {
        // Base Item Pipes: 10x pieces output using [Material, Glass, Material]
        writeItemPipeRecipe("wood_item_pipe", "#minecraft:planks");
        writeItemPipeRecipe("copper_item_pipe", "minecraft:copper_ingot");
        writeItemPipeRecipe("iron_item_pipe", "minecraft:iron_ingot");
        writeItemPipeRecipe("steel_item_pipe", "refinery:steel_ingot");
        writeItemPipeRecipe("gold_item_pipe", "minecraft:gold_ingot");
        writeItemPipeRecipe("diamond_item_pipe", "minecraft:diamond");
        writeItemPipeRecipe("emerald_item_pipe", "minecraft:emerald");
        writeItemPipeRecipe("merge_pipe", "minecraft:quartz");
        writeItemPipeRecipe("merge_item_pipe", "minecraft:quartz");

        // Fluid conversion recipes: [Item Pipe] + [Tar OR Slime Ball] -> Fluid Pipe
        String[] fluidMaterials = {"wood", "copper", "iron", "steel", "gold", "diamond", "merge"};
        for (String mat : fluidMaterials) {
            writeFluidConversionRecipe(mat + "_fluid_pipe_from_tar", mat + "_item_pipe", "refinery:tar", mat + "_fluid_pipe");
            writeFluidConversionRecipe(mat + "_fluid_pipe_from_slime", mat + "_item_pipe", "minecraft:slime_ball", mat + "_fluid_pipe");
        }
        writeFluidConversionRecipe("merge_fluid_pipe_from_merge_pipe_tar", "merge_pipe", "refinery:tar", "merge_fluid_pipe");
        writeFluidConversionRecipe("merge_fluid_pipe_from_merge_pipe_slime", "merge_pipe", "minecraft:slime_ball", "merge_fluid_pipe");

        // Legacy / Utility recipes
        writeRecipe("brass_item_pipe", "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"BGB\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"B\": \"minecraft:gold_ingot\",\n" +
                "    \"G\": \"minecraft:glass\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:brass_item_pipe\",\n" +
                "    \"count\": 10\n" +
                "  }\n" +
                "}");

        writeRecipe("brass_item_pipe_from_bronze", "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"BGB\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"B\": \"refinery:bronze_ingot\",\n" +
                "    \"G\": \"minecraft:glass\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:brass_item_pipe\",\n" +
                "    \"count\": 10\n" +
                "  }\n" +
                "}");

        writeRecipe("cast_iron_fluid_pipe", "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"IGI\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"I\": \"minecraft:iron_ingot\",\n" +
                "    \"G\": \"minecraft:glass\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:cast_iron_fluid_pipe\",\n" +
                "    \"count\": 10\n" +
                "  }\n" +
                "}");

        writeRecipe("fluid_extractor_pipe", "{\n" +
                "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                "  \"ingredients\": [\n" +
                "    \"refinery:steel_fluid_pipe\",\n" +
                "    \"minecraft:piston\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:fluid_extractor_pipe\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}");

        writeRecipe("item_extractor_pipe", "{\n" +
                "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                "  \"ingredients\": [\n" +
                "    \"refinery:steel_item_pipe\",\n" +
                "    \"minecraft:piston\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:item_extractor_pipe\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}");

        writeRecipe("void_pipe", "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"LGL\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"L\": \"minecraft:lava_bucket\",\n" +
                "    \"G\": \"minecraft:glass\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:void_pipe\",\n" +
                "    \"count\": 10\n" +
                "  }\n" +
                "}");
    }

    private static void writeItemPipeRecipe(String pipeId, String material) throws Exception {
        String keyEntry = material.startsWith("#")
                ? "    \"M\": \"" + material + "\",\n"
                : "    \"M\": \"" + material + "\",\n";

        String json = "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"MGM\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                keyEntry +
                "    \"G\": \"minecraft:glass\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:" + pipeId + "\"\n," +
                "    \"count\": 10\n" +
                "  }\n" +
                "}";
        writeRecipe(pipeId, json);
    }

    private static void writeFluidConversionRecipe(String recipeFile, String itemPipeId, String waterproofingItem, String fluidPipeId) throws Exception {
        String json = "{\n" +
                "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                "  \"ingredients\": [\n" +
                "    \"refinery:" + itemPipeId + "\",\n" +
                "    \"" + waterproofingItem + "\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"id\": \"refinery:" + fluidPipeId + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}";
        writeRecipe(recipeFile, json);
    }

    private static void writeRecipe(String name, String json) throws Exception {
        try (FileWriter fw = new FileWriter(DATA + "recipe/" + name + ".json")) {
            fw.write(json);
        }
    }
}
