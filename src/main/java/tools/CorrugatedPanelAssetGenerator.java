package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class CorrugatedPanelAssetGenerator {

    public record ColorScheme(
            String id,
            String englishName,
            Color base,
            Color highlight,
            Color shadow,
            String dyeItem
    ) {}

    public static final ColorScheme[] SCHEMES = new ColorScheme[] {
            new ColorScheme("corrugated_steel_panel", "Corrugated Steel Panel",
                    new Color(0x4C, 0x48, 0x44), new Color(0x86, 0x82, 0x7E), new Color(0x24, 0x22, 0x20), null),
            new ColorScheme("white_corrugated_steel_panel", "White Corrugated Steel Panel",
                    new Color(0xD8, 0xD6, 0xD2), new Color(0xFA, 0xF9, 0xF7), new Color(0x9E, 0x9B, 0x96), "minecraft:white_dye"),
            new ColorScheme("orange_corrugated_steel_panel", "Orange Corrugated Steel Panel",
                    new Color(0xC2, 0x56, 0x1B), new Color(0xEA, 0x7D, 0x38), new Color(0x7A, 0x32, 0x0E), "minecraft:orange_dye"),
            new ColorScheme("magenta_corrugated_steel_panel", "Magenta Corrugated Steel Panel",
                    new Color(0x80, 0x2E, 0x62), new Color(0xAD, 0x4D, 0x8A), new Color(0x4B, 0x17, 0x38), "minecraft:magenta_dye"),
            new ColorScheme("light_blue_corrugated_steel_panel", "Light Blue Corrugated Steel Panel",
                    new Color(0x51, 0x82, 0xA6), new Color(0x7A, 0xAC, 0xD0), new Color(0x2E, 0x51, 0x6B), "minecraft:light_blue_dye"),
            new ColorScheme("yellow_corrugated_steel_panel", "Yellow Corrugated Steel Panel",
                    new Color(0xD6, 0xA2, 0x21), new Color(0xF8, 0xCA, 0x4E), new Color(0x86, 0x63, 0x11), "minecraft:yellow_dye"),
            new ColorScheme("lime_corrugated_steel_panel", "Lime Corrugated Steel Panel",
                    new Color(0x6F, 0x9D, 0x24), new Color(0x9B, 0xCA, 0x4A), new Color(0x44, 0x62, 0x13), "minecraft:lime_dye"),
            new ColorScheme("pink_corrugated_steel_panel", "Pink Corrugated Steel Panel",
                    new Color(0xB5, 0x63, 0x7A), new Color(0xDC, 0x8C, 0xA2), new Color(0x74, 0x38, 0x49), "minecraft:pink_dye"),
            new ColorScheme("gray_corrugated_steel_panel", "Gray Corrugated Steel Panel",
                    new Color(0x46, 0x4A, 0x50), new Color(0x71, 0x77, 0x82), new Color(0x27, 0x2A, 0x2F), "minecraft:gray_dye"),
            new ColorScheme("light_gray_corrugated_steel_panel", "Light Gray Corrugated Steel Panel",
                    new Color(0x98, 0x9B, 0x9F), new Color(0xC4, 0xC7, 0xCB), new Color(0x63, 0x66, 0x6B), "minecraft:light_gray_dye"),
            new ColorScheme("cyan_corrugated_steel_panel", "Cyan Corrugated Steel Panel",
                    new Color(0x2A, 0x6E, 0x78), new Color(0x4A, 0x99, 0xA4), new Color(0x16, 0x43, 0x4A), "minecraft:cyan_dye"),
            new ColorScheme("purple_corrugated_steel_panel", "Purple Corrugated Steel Panel",
                    new Color(0x56, 0x30, 0x70), new Color(0x79, 0x4D, 0x98), new Color(0x32, 0x1A, 0x44), "minecraft:purple_dye"),
            new ColorScheme("blue_corrugated_steel_panel", "Blue Corrugated Steel Panel",
                    new Color(0x27, 0x44, 0x7F), new Color(0x47, 0x68, 0xAD), new Color(0x15, 0x27, 0x4F), "minecraft:blue_dye"),
            new ColorScheme("brown_corrugated_steel_panel", "Brown Corrugated Steel Panel",
                    new Color(0x5E, 0x42, 0x32), new Color(0x8C, 0x69, 0x53), new Color(0x38, 0x26, 0x1C), "minecraft:brown_dye"),
            new ColorScheme("green_corrugated_steel_panel", "Green Corrugated Steel Panel",
                    new Color(0x34, 0x58, 0x2C), new Color(0x53, 0x7E, 0x48), new Color(0x1D, 0x34, 0x18), "minecraft:green_dye"),
            new ColorScheme("red_corrugated_steel_panel", "Red Corrugated Steel Panel",
                    new Color(0x8E, 0x2A, 0x26), new Color(0xBE, 0x4A, 0x44), new Color(0x54, 0x16, 0x14), "minecraft:red_dye"),
            new ColorScheme("black_corrugated_steel_panel", "Black Corrugated Steel Panel",
                    new Color(0x22, 0x23, 0x26), new Color(0x42, 0x44, 0x4A), new Color(0x11, 0x12, 0x14), "minecraft:black_dye")
    };

    public static void main(String[] args) throws Exception {
        File assetsDir = new File("src/main/resources/assets/refinery");
        File dataDir = new File("src/main/resources/data/refinery");

        for (ColorScheme scheme : SCHEMES) {
            generateSideTexture(scheme, assetsDir);
            generateTopTexture(scheme, assetsDir);
            generateSideNormalTexture(scheme, assetsDir);
            generateSideSpecularTexture(scheme, assetsDir);
            generateTopNormalTexture(scheme, assetsDir);
            generateTopSpecularTexture(scheme, assetsDir);
            generateBlockModels(scheme, assetsDir);
            generateBlockstate(scheme, assetsDir);
            generateItemModel(scheme, assetsDir);
            generateItemDefinition(scheme, assetsDir);
            generateLootTables(scheme, dataDir);
            generateRecipes(scheme, dataDir);
        }

        generateTags(dataDir);
        generateWashingAndStonecutting(dataDir);

        System.out.println("All corrugated steel panel assets generated successfully!");
    }

    private static void generateTags(File dataDir) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"values\": [\n");
        for (int i = 0; i < SCHEMES.length; i++) {
            sb.append("    \"refinery:").append(SCHEMES[i].id()).append("\"");
            if (i < SCHEMES.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}\n");

        File cDataDir = new File(dataDir.getParentFile(), "c");
        writeFile(new File(cDataDir, "tags/block/corrugated_steel_panels.json"), sb.toString());
        writeFile(new File(cDataDir, "tags/item/corrugated_steel_panels.json"), sb.toString());
    }

    private static void generateWashingAndStonecutting(File dataDir) throws Exception {
        // Washing any dyed panel back to undyed with a water bucket
        String washJson = "{\n" +
                "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                "  \"category\": \"building\",\n" +
                "  \"ingredients\": [\n" +
                "    \"#c:corrugated_steel_panels\",\n" +
                "    \"minecraft:water_bucket\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"count\": 1,\n" +
                "    \"id\": \"refinery:corrugated_steel_panel\"\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(dataDir, "recipe/corrugated_steel_panel_wash.json"), washJson);

        // Stonecutting: 1 iron ingot -> 2 corrugated steel panels
        String cutIngot = "{\n" +
                "  \"type\": \"minecraft:stonecutting\",\n" +
                "  \"ingredient\": \"#c:ingots/iron\",\n" +
                "  \"result\": {\n" +
                "    \"count\": 2,\n" +
                "    \"id\": \"refinery:corrugated_steel_panel\"\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(dataDir, "recipe/corrugated_steel_panel_stonecutting_iron.json"), cutIngot);

        // Re-dyeing shapeless recipes for each dyed variant: any corrugated panel + dye -> that dyed panel
        for (ColorScheme s : SCHEMES) {
            if (s.dyeItem() != null) {
                String redye = "{\n" +
                        "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                        "  \"category\": \"building\",\n" +
                        "  \"ingredients\": [\n" +
                        "    \"#c:corrugated_steel_panels\",\n" +
                        "    \"" + s.dyeItem() + "\"\n" +
                        "  ],\n" +
                        "  \"result\": {\n" +
                        "    \"count\": 1,\n" +
                        "    \"id\": \"refinery:" + s.id() + "\"\n" +
                        "  }\n" +
                        "}\n";
                writeFile(new File(dataDir, "recipe/" + s.id() + "_redye.json"), redye);
            }
        }
    }

    private static double getSideHeight(double x, double y) {
        double u = ((x % 16.0) + 16.0) % 16.0;
        double h;
        // Major standing rib:
        // Left ascending slope: u in [14.4, 16.0) and [0.0, 0.6) -> width 2.2 pixels
        // Crest plateau: u in [0.6, 2.2] -> width 1.6 pixels
        // Right descending slope: u in (2.2, 4.0) -> width 1.8 pixels
        // Flat pan: u in [4.0, 14.4] -> height 0.14
        //   Micro-rib 1 centered at 7.6 (width 1.8, peak height 0.35)
        //   Micro-rib 2 centered at 11.6 (width 1.8, peak height 0.35)

        if (u >= 0.6 && u <= 2.2) {
            h = 0.96;
        } else if (u > 2.2 && u < 4.0) {
            double t = (u - 2.2) / 1.8;
            double s = t * t * (3.0 - 2.0 * t);
            h = 0.96 * (1.0 - s) + 0.14 * s;
        } else if (u >= 14.4) {
            double t = (u - 14.4) / 2.2;
            double s = t * t * (3.0 - 2.0 * t);
            h = 0.14 * (1.0 - s) + 0.96 * s;
        } else if (u >= 0.0 && u < 0.6) {
            double t = (u + 1.6) / 2.2;
            double s = t * t * (3.0 - 2.0 * t);
            h = 0.14 * (1.0 - s) + 0.96 * s;
        } else {
            h = 0.14;
            double d1 = Math.abs(u - 7.6);
            if (d1 < 0.9) {
                double t = 1.0 - (d1 / 0.9);
                h += 0.22 * (t * t * (3.0 - 2.0 * t));
            }
            double d2 = Math.abs(u - 11.6);
            if (d2 < 0.9) {
                double t = 1.0 - (d2 / 0.9);
                h += 0.22 * (t * t * (3.0 - 2.0 * t));
            }
        }
        return h;
    }

    private static double getSideHeightWithNoise(double x, double y) {
        double base = getSideHeight(x, y);
        // Subtle rolling mill micro-texture along extrusion axis
        double microGrain = 0.003 * Math.sin(x * 1.5 + y * 0.12) + 0.002 * Math.sin(y * 0.4);
        return Math.max(0.08, Math.min(0.98, base + microGrain));
    }

    private static void generateSideTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Random rand = new Random(s.id().hashCode());
        boolean isRawSteel = s.dyeItem() == null;

        // Voronoi crystallization spangle lattice for raw galvanized steel
        int numCells = 8;
        double cellSize = (double) size / numCells;
        double[][] seedX = new double[numCells][numCells];
        double[][] seedY = new double[numCells][numCells];
        double[][] spangleTint = new double[numCells][numCells];
        for (int cy = 0; cy < numCells; cy++) {
            for (int cx = 0; cx < numCells; cx++) {
                seedX[cx][cy] = (cx + 0.15 + rand.nextDouble() * 0.70) * cellSize;
                seedY[cx][cy] = (cy + 0.15 + rand.nextDouble() * 0.70) * cellSize;
                spangleTint[cx][cy] = 0.90 + rand.nextDouble() * 0.20; // 0.90 to 1.10
            }
        }

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double h = getSideHeight(x, y);
                int px = x % 16;
                double factor;

                if (px == 0) factor = 0.68;
                else if (px == 1) factor = 0.96; // Specular crest ridge highlight
                else if (px == 2) factor = 0.88; // Crest flat
                else if (px == 3) factor = 0.35; // Steep right slope (ambient shadow)
                else if (px == 4) factor = 0.18; // Deep trough crevice shadow
                else if (px == 5) factor = 0.34;
                else if (px == 6 || px == 7) factor = 0.48; // Valley pan
                else if (px == 8) factor = 0.60; // Micro-rib 1 left edge
                else if (px == 9) factor = 0.38; // Micro-rib 1 right shadow
                else if (px == 10 || px == 11) factor = 0.48; // Valley pan
                else if (px == 12) factor = 0.60; // Micro-rib 2 left edge
                else if (px == 13) factor = 0.38; // Micro-rib 2 right shadow
                else factor = 0.50; // Valley pan right

                if (isRawSteel) {
                    // Find nearest Voronoi seed for galvanized zinc spangles
                    int cellX = (int) (x / cellSize);
                    int cellY = (int) (y / cellSize);
                    double minDist = 9999.0;
                    double bestTint = 1.0;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int nx = (cellX + dx + numCells) % numCells;
                            int ny = (cellY + dy + numCells) % numCells;
                            double sx = seedX[nx][ny];
                            double sy = seedY[nx][ny];
                            // Wrap distance
                            double distSq = (x - sx) * (x - sx) + (y - sy) * (y - sy);
                            if (distSq < minDist) {
                                minDist = distSq;
                                bestTint = spangleTint[nx][ny];
                            }
                        }
                    }
                    factor = Math.max(0.0, Math.min(1.0, factor * bestTint));
                }

                // Add subtle metallic stipple and vertical brushed roller grain
                double grainNoise = (rand.nextDouble() - 0.5) * 0.04;
                double verticalStreak = Math.sin(x * 0.8 + y * 0.05) * 0.02;
                factor = Math.max(0.0, Math.min(1.0, factor + grainNoise + verticalStreak));

                Color c = blendColor(s.shadow(), s.base(), s.highlight(), factor);
                img.setRGB(x, y, c.getRGB());
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + ".png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateSideNormalTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        double delta = 0.05;
        double scaleK = 7.5; // Controls the depth angle of the normal vectors

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double hCenter = getSideHeight(x, y);
                double hX1 = getSideHeightWithNoise(x + delta, y);
                double hX0 = getSideHeightWithNoise(x - delta, y);
                double hY1 = getSideHeightWithNoise(x, y + delta);
                double hY0 = getSideHeightWithNoise(x, y - delta);

                double dhdx = (hX1 - hX0) / (2.0 * delta);
                double dhdy = (hY1 - hY0) / (2.0 * delta);

                // Tangent-space normal vector
                double nx = -dhdx * scaleK;
                double ny = -dhdy * scaleK;
                double nz = 1.0;
                double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
                nx /= len;
                ny /= len;
                nz /= len;

                // LabPBR 1.3 Normal Map format (DirectX Y-down):
                // Red: Normal X (-1.0 to 1.0 mapped to 0..255)
                int r = (int) Math.round(Math.max(0, Math.min(255, (nx * 0.5 + 0.5) * 255.0)));
                // Green: Normal Y (DirectX Y-down: -ny mapped to 0..255)
                int g = (int) Math.round(Math.max(0, Math.min(255, (-ny * 0.5 + 0.5) * 255.0)));

                // Blue: Linear Ambient Occlusion (AO).
                // Valley floor and crevice troughs receive lower ambient light
                double slopeMag = Math.abs(dhdx);
                double ao = 0.55 + 0.44 * hCenter - 0.12 * Math.min(1.0, slopeMag);
                int b = (int) Math.round(Math.max(0, Math.min(255, ao * 255.0)));

                // Alpha: Parallax Occlusion Mapping (POM) Height Map (0 = deep, 255 = top)
                int a = (int) Math.round(Math.max(0, Math.min(255, hCenter * 255.0)));

                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgba);
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + "_n.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateSideSpecularTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Random rand = new Random(s.id().hashCode() + 42);
        boolean isRawSteel = s.dyeItem() == null;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double h = getSideHeight(x, y);

                // Red channel: Perceptual Smoothness (1.0 - Roughness).
                // Satin metallic sheet with polished folds:
                // Crests (h >= 0.90): smoothness ~0.84 (R = 214)
                // Slopes: smoothness ~0.78 (R = 199)
                // Pan valley: smoothness ~0.73 (R = 186)
                // Micro-ribs: smoothness ~0.79 (R = 201)
                double smoothness;
                if (h >= 0.90) {
                    smoothness = 0.84;
                } else if (h > 0.25) {
                    smoothness = 0.78;
                } else {
                    smoothness = 0.73;
                }
                // Add micro-spangle / paint grain variation
                smoothness += (rand.nextDouble() - 0.5) * 0.03;
                int r = (int) Math.round(Math.max(0, Math.min(255, smoothness * 255.0)));

                // Green channel: F0 / Hardcoded Metals.
                // LabPBR 1.3:
                // 230 = Hardcoded Iron/Steel for raw unpainted corrugated panel!
                // 255 = Custom Metal mode for dyed panels (tints specular reflections by painted albedo)!
                int g = isRawSteel ? 230 : 255;

                // Blue channel: Porosity.
                // 0 = Non-porous metal (rain water creates reflective wet slick without soaking in)
                int b = 0;

                // Alpha channel: Emission.
                // 255 = Emission disabled
                int a = 255;

                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgba);
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + "_s.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateTopTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Random rand = new Random(s.id().hashCode() + 99);

        // Cross-section end showing wavy profile on steel plate cap
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int px = x % 16;
                double factor;
                if (px == 1 || px == 2) {
                    factor = 0.82; // Rib crests
                } else if (px == 4) {
                    factor = 0.22; // Trough
                } else if (px == 9 || px == 13) {
                    factor = 0.62; // Micro-ribs
                } else {
                    factor = 0.46; // Base sheet thickness
                }

                // Top surface bevel highlight
                if (y == 0 || y == size - 1 || x == 0 || x == size - 1) {
                    factor = Math.min(1.0, factor + 0.15);
                }

                double noise = (rand.nextDouble() - 0.5) * 0.05;
                factor = Math.max(0.0, Math.min(1.0, factor + noise));

                Color c = blendColor(s.shadow(), s.base(), s.highlight(), factor);
                img.setRGB(x, y, c.getRGB());
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + "_top.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateTopNormalTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Beveled perimeter edges
                double nx = 0.0;
                double ny = 0.0;
                double nz = 1.0;
                double h = 0.88;

                if (x == 0) { nx = -0.5; h = 0.65; }
                else if (x == size - 1) { nx = 0.5; h = 0.65; }

                if (y == 0) { ny = 0.5; h = 0.65; }
                else if (y == size - 1) { ny = -0.5; h = 0.65; }

                double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
                nx /= len;
                ny /= len;

                int r = (int) Math.round((nx * 0.5 + 0.5) * 255.0);
                int g = (int) Math.round((-ny * 0.5 + 0.5) * 255.0);
                int b = 245; // Ambient occlusion
                int a = (int) Math.round(h * 255.0); // POM height

                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgba);
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + "_top_n.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateTopSpecularTexture(ColorScheme s, File assetsDir) throws Exception {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        boolean isRawSteel = s.dyeItem() == null;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int r = 190; // Smoothness ~0.75
                int g = isRawSteel ? 230 : 255; // Iron or Custom Metal
                int b = 0;   // Non-porous
                int a = 255; // Non-emissive

                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgba);
            }
        }

        File out = new File(assetsDir, "textures/block/" + s.id() + "_top_s.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
    }

    private static void generateBlockModels(ColorScheme s, File assetsDir) throws Exception {
        // Vertical cube_column
        String vert = "{\n" +
                "  \"parent\": \"minecraft:block/cube_column\",\n" +
                "  \"textures\": {\n" +
                "    \"end\": \"refinery:block/" + s.id() + "_top\",\n" +
                "    \"side\": \"refinery:block/" + s.id() + "\"\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(assetsDir, "models/block/" + s.id() + ".json"), vert);

        // Horizontal cube_column_horizontal
        String horiz = "{\n" +
                "  \"parent\": \"minecraft:block/cube_column_horizontal\",\n" +
                "  \"textures\": {\n" +
                "    \"end\": \"refinery:block/" + s.id() + "_top\",\n" +
                "    \"side\": \"refinery:block/" + s.id() + "\"\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(assetsDir, "models/block/" + s.id() + "_horizontal.json"), horiz);
    }

    private static void generateBlockstate(ColorScheme s, File assetsDir) throws Exception {
        String json = "{\n" +
                "  \"variants\": {\n" +
                "    \"axis=y\": {\n" +
                "      \"model\": \"refinery:block/" + s.id() + "\"\n" +
                "    },\n" +
                "    \"axis=z\": {\n" +
                "      \"model\": \"refinery:block/" + s.id() + "_horizontal\",\n" +
                "      \"x\": 90\n" +
                "    },\n" +
                "    \"axis=x\": {\n" +
                "      \"model\": \"refinery:block/" + s.id() + "_horizontal\",\n" +
                "      \"x\": 90,\n" +
                "      \"y\": 90\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(assetsDir, "blockstates/" + s.id() + ".json"), json);
    }

    private static void generateItemModel(ColorScheme s, File assetsDir) throws Exception {
        String json = "{\n" +
                "  \"parent\": \"refinery:block/" + s.id() + "\"\n" +
                "}\n";
        writeFile(new File(assetsDir, "models/item/" + s.id() + ".json"), json);
    }

    private static void generateItemDefinition(ColorScheme s, File assetsDir) throws Exception {
        String json = "{\n" +
                "  \"model\": {\n" +
                "    \"type\": \"minecraft:model\",\n" +
                "    \"model\": \"refinery:item/" + s.id() + "\"\n" +
                "  }\n" +
                "}\n";
        writeFile(new File(assetsDir, "items/" + s.id() + ".json"), json);
    }

    private static void generateLootTables(ColorScheme s, File dataDir) throws Exception {
        String json = "{\n" +
                "  \"type\": \"minecraft:block\",\n" +
                "  \"pools\": [\n" +
                "    {\n" +
                "      \"condition\": {\n" +
                "        \"type\": \"minecraft:survives_explosion\"\n" +
                "      },\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"type\": \"minecraft:item\",\n" +
                "          \"name\": \"refinery:" + s.id() + "\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"rolls\": 1\n" +
                "    }\n" +
                "  ],\n" +
                "  \"random_sequence\": \"refinery:blocks/" + s.id() + "\"\n" +
                "}\n";
        writeFile(new File(dataDir, "loot_table/blocks/" + s.id() + ".json"), json);
        writeFile(new File(dataDir, "loot_tables/blocks/" + s.id() + ".json"), json);
    }

    private static void generateRecipes(ColorScheme s, File dataDir) throws Exception {
        if (s.dyeItem() == null) {
            // Raw panel crafted from 4 Iron Ingots in 2x2 grid -> 4 panels
            String json = "{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"category\": \"building\",\n" +
                    "  \"key\": {\n" +
                    "    \"#\": \"#c:ingots/iron\"\n" +
                    "  },\n" +
                    "  \"pattern\": [\n" +
                    "    \"##\",\n" +
                    "    \"##\"\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"count\": 4,\n" +
                    "    \"id\": \"refinery:corrugated_steel_panel\"\n" +
                    "  }\n" +
                    "}\n";
            writeFile(new File(dataDir, "recipe/corrugated_steel_panel.json"), json);
        } else {
            // Dyed panel: 8 corrugated panels surrounding dye -> 8 dyed panels
            String json = "{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"category\": \"building\",\n" +
                    "  \"key\": {\n" +
                    "    \"#\": \"refinery:corrugated_steel_panel\",\n" +
                    "    \"D\": \"" + s.dyeItem() + "\"\n" +
                    "  },\n" +
                    "  \"pattern\": [\n" +
                    "    \"###\",\n" +
                    "    \"#D#\",\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"count\": 8,\n" +
                    "    \"id\": \"refinery:" + s.id() + "\"\n" +
                    "  }\n" +
                    "}\n";
            writeFile(new File(dataDir, "recipe/" + s.id() + ".json"), json);
        }
    }

    private static Color blendColor(Color shadow, Color base, Color highlight, double factor) {
        if (factor < 0.5) {
            double t = factor / 0.5;
            int r = (int) Math.round(shadow.getRed() + t * (base.getRed() - shadow.getRed()));
            int g = (int) Math.round(shadow.getGreen() + t * (base.getGreen() - shadow.getGreen()));
            int b = (int) Math.round(shadow.getBlue() + t * (base.getBlue() - shadow.getBlue()));
            return new Color(r, g, b);
        } else {
            double t = (factor - 0.5) / 0.5;
            int r = (int) Math.round(base.getRed() + t * (highlight.getRed() - base.getRed()));
            int g = (int) Math.round(base.getGreen() + t * (highlight.getGreen() - base.getGreen()));
            int b = (int) Math.round(base.getBlue() + t * (highlight.getBlue() - base.getBlue()));
            return new Color(r, g, b);
        }
    }

    private static void writeFile(File f, String content) throws Exception {
        f.getParentFile().mkdirs();
        try (FileWriter w = new FileWriter(f)) {
            w.write(content);
        }
    }
}
