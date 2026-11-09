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
            "buffer_pipe",
            "void_pipe",
            "merge_pipe",
            "merge_item_pipe",
            "merge_fluid_pipe",
            "structure_pipe"
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

    public static class PipePalette {
        public final int outline;
        public final int darkBody;
        public final int highlight;
        public final int notch;
        public final int bounce;

        public PipePalette(int outline, int darkBody, int highlight, int notch, int bounce) {
            this.outline = outline;
            this.darkBody = darkBody;
            this.highlight = highlight;
            this.notch = notch;
            this.bounce = bounce;
        }
    }

    private static PipePalette getPipePalette(String id) {
        if (id.contains("wood")) return new PipePalette(0xFF2A1808, 0xFF824B1E, 0xFFE5AA68, 0xFF502C0E, 0xFFDCA05E);
        if (id.contains("copper")) return new PipePalette(0xFF3D1505, 0xFFB8592B, 0xFFFFA87A, 0xFF7D3414, 0xFFFFA87A);
        if (id.contains("iron") && !id.contains("cast")) return new PipePalette(0xFF25272B, 0xFF969DA6, 0xFFF5F8FC, 0xFF5E646E, 0xFFFFFFFF);
        if (id.contains("steel")) return new PipePalette(0xFF151D28, 0xFF6F829A, 0xFFDCEAF8, 0xFF455468, 0xFFDCEAF8);
        if (id.contains("gold")) return new PipePalette(0xFF3E2400, 0xFFDA9F00, 0xFFFFF24A, 0xFF8F6300, 0xFFFFF24A);
        if (id.contains("diamond")) return new PipePalette(0xFF053232, 0xFF20C5B7, 0xFFA8FFF6, 0xFF11756D, 0xFFA8FFF6);
        if (id.contains("emerald")) return new PipePalette(0xFF004800, 0xFF00AB00, 0xFFB8F818, 0xFF007800, 0xFFB8F818);
        if (id.contains("brass")) return new PipePalette(0xFF382602, 0xFFC7981E, 0xFFFFEB75, 0xFF7D5C0C, 0xFFFFEB75);
        if (id.contains("cast_iron")) return new PipePalette(0xFF0E0F12, 0xFF4E5158, 0xFFA0A5AE, 0xFF2D2F35, 0xFFA0A5AE);
        if (id.contains("void")) return new PipePalette(0xFF080210, 0xFF3E1958, 0xFFD45FFF, 0xFF220B33, 0xFFEA94FF);
        if (id.contains("quartz") || id.contains("merge")) return new PipePalette(0xFF3B3530, 0xFFC2BAAF, 0xFFFFFFFF, 0xFF7D756C, 0xFFFFFFFF);
        if (id.contains("structure")) return new PipePalette(0xFF222222, 0xFF5E5E5E, 0xFFB4B4B4, 0xFF3C3C3C, 0xFFB4B4B4);
        return new PipePalette(0xFF151D28, 0xFF6F829A, 0xFFDCEAF8, 0xFF455468, 0xFFDCEAF8);
    }

    public static void generateItemTextures() throws Exception {
        for (String id : ALL_PIPE_IDS) {
            PipePalette pal = getPipePalette(id);
            boolean isFluid = id.contains("fluid");
            boolean isExtractor = id.contains("extractor");
            String special = "none";
            if (id.equals("buffer_pipe")) special = "buffer";
            else if (id.equals("void_pipe")) special = "void";
            else if (id.contains("merge")) special = "merge";

            BufferedImage itemImg = createPipeIcon(pal, isFluid, isExtractor, special);
            ImageIO.write(itemImg, "PNG", new File(ASSETS + "textures/item/" + id + ".png"));
        }
    }

    public static BufferedImage createPipeIcon(PipePalette pal, boolean isFluid, boolean isExtractor, String special) {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        int leftX1 = 1, leftX2 = 5;
        int rightX1 = 26, rightX2 = 30;
        int shaftX1 = 6, shaftX2 = 25;

        // Slender horizontal rectangle proportions:
        // Collar height = 16 (Y=8 to 23), Shaft height = 12 (Y=10 to 21)
        int collarY1 = 8, collarY2 = 23;
        int shaftY1 = 10, shaftY2 = 21;

        // 1. Draw Shaft (Y=10 to 21)
        for (int y = shaftY1; y <= shaftY2; y++) {
            for (int x = shaftX1; x <= shaftX2; x++) {
                int col;
                if (y == shaftY1 || y == shaftY2) {
                    col = pal.outline;
                } else if (y == 11) {
                    col = pal.highlight; // top highlight stripe
                } else if (y == 12) {
                    col = pal.notch; // dark notch
                } else if (y == 13 || y == 14) {
                    col = pal.highlight; // main highlight
                } else if (y == 15 || y == 16) {
                    col = pal.darkBody; // main body shadow
                } else if (y == 17 || y == 18) {
                    // Dither band: checkerboard alternating darkBody and highlight
                    col = ((x + y) % 2 == 0) ? pal.highlight : pal.darkBody;
                } else if (y == 19 || y == 20) {
                    col = pal.bounce; // bottom bounce light
                } else {
                    col = pal.darkBody;
                }
                img.setRGB(x, y, col);
            }
        }

        // 2. Draw Left Collar
        drawCollar(img, leftX1, leftX2, collarY1, collarY2, pal, true);

        // 3. Draw Right Collar
        drawCollar(img, rightX1, rightX2, collarY1, collarY2, pal, false);

        // 4. Markings
        if (isFluid) {
            drawFluidDroplet(img, 15, 15);
        }

        if (isExtractor) {
            drawExtractorBadge(img, 15, 15, isFluid);
        }

        if ("buffer".equals(special)) {
            drawBufferBadge(img, 15, 15);
        } else if ("void".equals(special)) {
            drawVoidVortex(img, 15, 15);
        } else if ("merge".equals(special)) {
            drawMergeBadge(img, 15, 15, isFluid);
        }

        return img;
    }

    private static void drawCollar(BufferedImage img, int x1, int x2, int y1, int y2, PipePalette pal, boolean isLeft) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                int col;
                if (x == x1 || x == x2 || y == y1 || y == y2) {
                    col = pal.outline;
                } else {
                    int relY = y - y1;
                    if (relY <= 2) col = pal.highlight;
                    else if (relY == 3) col = pal.notch;
                    else if (relY <= 6) col = pal.highlight;
                    else if (relY <= 9) col = pal.darkBody;
                    else if (relY <= 11) col = ((x + y) % 2 == 0) ? pal.highlight : pal.darkBody;
                    else col = pal.bounce;

                    // Vertical highlight stripe near outer rim
                    if (isLeft && x == x1 + 1 && relY >= 1 && relY <= 13) {
                        if (relY != 3) col = pal.highlight;
                    } else if (!isLeft && x == x2 - 1 && relY >= 1 && relY <= 13) {
                        if (relY != 3) col = pal.highlight;
                    }
                }
                img.setRGB(x, y, col);
            }
        }
    }

    private static void drawFluidDroplet(BufferedImage img, int cx, int cy) {
        int[][] droplet = {
                {0, 0, 1, 0, 0},
                {0, 1, 2, 1, 0},
                {1, 3, 2, 2, 1},
                {1, 3, 2, 4, 1},
                {1, 2, 4, 4, 1},
                {0, 1, 4, 1, 0},
                {0, 0, 1, 0, 0}
        };

        int colOutline = 0xFF05162B;
        int colWater   = 0xFF1E90FF;
        int colWhite   = 0xFFE0F7FF;
        int colShadow  = 0xFF0C4D94;

        int startX = cx - 2;
        int startY = cy - 3;

        for (int dy = 0; dy < droplet.length; dy++) {
            for (int dx = 0; dx < droplet[dy].length; dx++) {
                int code = droplet[dy][dx];
                if (code == 1) img.setRGB(startX + dx, startY + dy, colOutline);
                else if (code == 2) img.setRGB(startX + dx, startY + dy, colWater);
                else if (code == 3) img.setRGB(startX + dx, startY + dy, colWhite);
                else if (code == 4) img.setRGB(startX + dx, startY + dy, colShadow);
            }
        }
    }

    private static void drawExtractorBadge(BufferedImage img, int cx, int cy, boolean isFluid) {
        int colGold = 0xFFFFD700;
        int colOutline = 0xFF4A3500;
        int colBright = 0xFFFFF59D;

        if (isFluid) {
            for (int y = cy - 2; y <= cy + 2; y++) {
                img.setRGB(cx - 4, y, colGold);
                img.setRGB(cx - 5, y, colOutline);
                img.setRGB(cx + 4, y, colGold);
                img.setRGB(cx + 5, y, colOutline);
            }
        } else {
            int[][] chevron = {
                    {0, 1, 2, 1, 0},
                    {1, 2, 3, 2, 1},
                    {2, 3, 3, 3, 2},
                    {1, 2, 3, 2, 1},
                    {0, 1, 2, 1, 0}
            };
            int sx = cx - 2;
            int sy = cy - 2;
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    int c = chevron[dy][dx];
                    if (c == 1) img.setRGB(sx + dx, sy + dy, colOutline);
                    else if (c == 2) img.setRGB(sx + dx, sy + dy, colGold);
                    else if (c == 3) img.setRGB(sx + dx, sy + dy, colBright);
                }
            }
        }
    }

    private static void drawBufferBadge(BufferedImage img, int cx, int cy) {
        int colGold = 0xFFFFC72C;
        int colDark = 0xFF4A3500;
        int colWhite = 0xFFFFF8D0;
        int sx = cx - 2;
        int sy = cy - 2;
        for (int dy = 0; dy < 5; dy++) {
            for (int dx = 0; dx < 5; dx++) {
                if (dx == 0 || dx == 4 || dy == 0 || dy == 4) img.setRGB(sx + dx, sy + dy, colDark);
                else if (dx == 1 && dy == 1) img.setRGB(sx + dx, sy + dy, colWhite);
                else img.setRGB(sx + dx, sy + dy, colGold);
            }
        }
    }

    private static void drawVoidVortex(BufferedImage img, int cx, int cy) {
        int colVoid = 0xFF05000C;
        int colRing = 0xFFDA70D6;
        int colPurple = 0xFF8A2BE2;
        img.setRGB(cx, cy, colVoid);
        img.setRGB(cx - 1, cy, colVoid);
        img.setRGB(cx + 1, cy, colVoid);
        img.setRGB(cx, cy - 1, colVoid);
        img.setRGB(cx, cy + 1, colVoid);
        img.setRGB(cx - 2, cy, colRing);
        img.setRGB(cx + 2, cy, colRing);
        img.setRGB(cx, cy - 2, colRing);
        img.setRGB(cx + 2, cy, colRing);
        img.setRGB(cx - 1, cy - 1, colPurple);
        img.setRGB(cx + 1, cy + 1, colPurple);
    }

    private static void drawMergeBadge(BufferedImage img, int cx, int cy, boolean isFluid) {
        int colOrange = 0xFFFF9900;
        int colDark = 0xFF502500;
        if (isFluid) {
            for (int y = cy - 2; y <= cy + 2; y++) {
                img.setRGB(cx - 4, y, colOrange);
                img.setRGB(cx - 5, y, colDark);
            }
        } else {
            img.setRGB(cx, cy - 2, colDark);
            img.setRGB(cx, cy - 1, colOrange);
            img.setRGB(cx, cy, colOrange);
            img.setRGB(cx, cy + 1, colOrange);
            img.setRGB(cx, cy + 2, colDark);
            img.setRGB(cx - 1, cy, colOrange);
            img.setRGB(cx + 1, cy, colOrange);
            img.setRGB(cx - 2, cy, colDark);
            img.setRGB(cx + 2, cy, colDark);
        }
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
                "    \"BGB\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"B\": \"minecraft:iron_bars\",\n" +
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
