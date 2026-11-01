package tools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class AssetGenerator {
    private static final String RES = "src/main/resources/";
    private static final String ASSETS = RES + "assets/refinery/";
    private static final String DATA = RES + "data/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/block").mkdirs();
        new File(ASSETS + "textures/item").mkdirs();
        new File(ASSETS + "textures/fluid").mkdirs();
        new File(ASSETS + "textures/gui").mkdirs();
        new File(ASSETS + "models/block").mkdirs();
        new File(ASSETS + "models/item").mkdirs();
        new File(ASSETS + "items").mkdirs();
        new File(ASSETS + "blockstates").mkdirs();
        new File(ASSETS + "lang").mkdirs();
        new File(DATA + "refinery/recipe").mkdirs();
        new File(DATA + "c/tags/fluid").mkdirs();
        new File(DATA + "c/tags/item").mkdirs();
        new File(DATA + "c/tags/item/dusts").mkdirs();

        generateTextures();
        generateGui();
        generateModelsAndBlockstates();
        generateItemDefinitions();
        generateTags();
        generateRecipes();
        generateLang();

        System.out.println("All assets, item definitions, models, textures, recipes, and lang generated successfully!");
    }

    private static void generateTextures() throws Exception {
        File refDir = new File("ref_textures/assets/logistics/textures");
        if (new File(refDir, "item/core/tar.png").exists()) {
            Files.copy(new File(refDir, "item/core/tar.png").toPath(), Paths.get(ASSETS + "textures/item/tar.png"), StandardCopyOption.REPLACE_EXISTING);
        } else {
            createTarTexture();
        }

        if (new File(refDir, "item/core/sulfur_dust.png").exists()) {
            Files.copy(new File(refDir, "item/core/sulfur_dust.png").toPath(), Paths.get(ASSETS + "textures/item/sulfur_dust.png"), StandardCopyOption.REPLACE_EXISTING);
        } else {
            createSulfurTexture();
        }

        // Casing texture (riveted dark steel plate)
        BufferedImage casing = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = casing.createGraphics();
        g.setColor(new Color(0x38, 0x3d, 0x44));
        g.fillRect(0, 0, 16, 16);
        // Beveled border
        g.setColor(new Color(0x52, 0x5a, 0x65));
        g.drawRect(0, 0, 15, 15);
        g.setColor(new Color(0x27, 0x2b, 0x30));
        g.drawRect(1, 1, 13, 13);
        // Inset center
        g.setColor(new Color(0x33, 0x37, 0x3e));
        g.fillRect(2, 2, 12, 12);
        // Rivets in corners
        int rivetColor = 0xFF717c8a;
        casing.setRGB(2, 2, rivetColor);
        casing.setRGB(13, 2, rivetColor);
        casing.setRGB(2, 13, rivetColor);
        casing.setRGB(13, 13, rivetColor);
        g.dispose();
        ImageIO.write(casing, "PNG", new File(ASSETS + "textures/block/reinforced_casing.png"));
        ImageIO.write(casing, "PNG", new File(ASSETS + "textures/block/machine_side.png"));
        ImageIO.write(casing, "PNG", new File(ASSETS + "textures/block/machine_top.png"));

        // Controller front (OFF)
        BufferedImage ctrlOff = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gc = ctrlOff.createGraphics();
        gc.drawImage(casing, 0, 0, null);
        // Screen bezel
        gc.setColor(new Color(0x1a, 0x1d, 0x20));
        gc.fillRect(3, 3, 10, 4);
        gc.setColor(new Color(0x40, 0x10, 0x10));
        gc.fillRect(4, 4, 8, 2); // Red offline LED bar
        // Dials / gauges
        gc.setColor(new Color(0xb8, 0x86, 0x0b)); // Brass rim
        gc.drawOval(3, 8, 4, 4);
        gc.drawOval(9, 8, 4, 4);
        gc.setColor(new Color(0xe2, 0xe8, 0xf0));
        gc.fillRect(4, 9, 3, 3);
        gc.fillRect(10, 9, 3, 3);
        gc.setColor(new Color(0x99, 0x1b, 0x1b)); // Needle
        gc.drawLine(5, 10, 5, 9);
        gc.drawLine(11, 10, 12, 9);
        // Valve wheel at bottom
        gc.setColor(new Color(0xd9, 0x77, 0x06));
        gc.drawRect(6, 13, 3, 2);
        gc.dispose();
        ImageIO.write(ctrlOff, "PNG", new File(ASSETS + "textures/block/controller_front_off.png"));

        // Controller front (ON)
        BufferedImage ctrlOn = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gco = ctrlOn.createGraphics();
        gco.drawImage(casing, 0, 0, null);
        gco.setColor(new Color(0x1a, 0x1d, 0x20));
        gco.fillRect(3, 3, 10, 4);
        gco.setColor(new Color(0x22, 0xc5, 0x5e));
        gco.fillRect(4, 4, 8, 2); // Green online LED bar
        gco.setColor(new Color(0xb8, 0x86, 0x0b));
        gco.drawOval(3, 8, 4, 4);
        gco.drawOval(9, 8, 4, 4);
        gco.setColor(new Color(0xe2, 0xe8, 0xf0));
        gco.fillRect(4, 9, 3, 3);
        gco.fillRect(10, 9, 3, 3);
        gco.setColor(new Color(0x22, 0xc5, 0x5e));
        gco.drawLine(5, 10, 6, 9);
        gco.drawLine(11, 10, 11, 9);
        gco.setColor(new Color(0xf5, 0x9e, 0x0b));
        gco.drawRect(6, 13, 3, 2);
        gco.dispose();
        ImageIO.write(ctrlOn, "PNG", new File(ASSETS + "textures/block/controller_front_on.png"));

        // Combustion chamber (OFF)
        BufferedImage combOff = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gcb = combOff.createGraphics();
        gcb.drawImage(casing, 0, 0, null);
        gcb.setColor(new Color(0x1f, 0x21, 0x24));
        gcb.fillRect(3, 4, 10, 9);
        gcb.setColor(new Color(0x4a, 0x4f, 0x57));
        for (int y = 5; y <= 11; y += 2) {
            gcb.drawLine(4, y, 11, y);
        }
        gcb.dispose();
        ImageIO.write(combOff, "PNG", new File(ASSETS + "textures/block/combustion_front_off.png"));

        // Combustion chamber (ON)
        BufferedImage combOn = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gcbo = combOn.createGraphics();
        gcbo.drawImage(casing, 0, 0, null);
        gcbo.setColor(new Color(0xf9, 0x73, 0x16)); // Glowing orange flame interior
        gcbo.fillRect(3, 4, 10, 9);
        gcbo.setColor(new Color(0xfd, 0xba, 0x74));
        gcbo.fillRect(5, 6, 6, 5);
        gcbo.setColor(new Color(0x33, 0x22, 0x11)); // Cast iron grate in front of fire
        for (int y = 5; y <= 11; y += 2) {
            gcbo.drawLine(4, y, 11, y);
        }
        gcbo.dispose();
        ImageIO.write(combOn, "PNG", new File(ASSETS + "textures/block/combustion_front_on.png"));

        // Distillation column
        BufferedImage col = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gcl = col.createGraphics();
        gcl.setColor(new Color(0x4b, 0x55, 0x63));
        gcl.fillRect(0, 0, 16, 16);
        // Column bands
        gcl.setColor(new Color(0x6b, 0x72, 0x80));
        gcl.fillRect(0, 0, 16, 2);
        gcl.fillRect(0, 14, 16, 2);
        gcl.fillRect(0, 7, 16, 2);
        // Center sight glass
        gcl.setColor(new Color(0x1e, 0x29, 0x3b));
        gcl.fillRect(6, 2, 4, 12);
        gcl.setColor(new Color(0x38, 0xbd, 0xf8)); // Glass shine
        gcl.drawLine(7, 3, 7, 12);
        gcl.dispose();
        ImageIO.write(col, "PNG", new File(ASSETS + "textures/block/distillation_column.png"));

        // Heat exchanger (cooling fins)
        BufferedImage rad = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grad = rad.createGraphics();
        grad.drawImage(casing, 0, 0, null);
        grad.setColor(new Color(0x18, 0x1b, 0x1e));
        grad.fillRect(2, 2, 12, 12);
        for (int y = 3; y <= 13; y += 2) {
            grad.setColor(new Color(0x64, 0x74, 0x8b));
            grad.drawLine(3, y, 12, y);
            grad.setColor(new Color(0x33, 0x41, 0x55));
            grad.drawLine(3, y + 1, 12, y + 1);
        }
        grad.dispose();
        ImageIO.write(rad, "PNG", new File(ASSETS + "textures/block/heat_exchanger.png"));

        // Ports
        createPortTexture("port_crude_oil", new Color(0x1c, 0x19, 0x17), new Color(0x44, 0x40, 0x3c));
        createPortTexture("port_diesel", new Color(0xf5, 0xcf, 0x47), new Color(0xfd, 0xee, 0x87));
        createPortTexture("port_gas", new Color(0x08, 0x91, 0xb2), new Color(0x67, 0xe8, 0xf9));
        createPortTexture("port_byproduct", new Color(0x47, 0x55, 0x69), new Color(0x94, 0xa3, 0xb8));

        // Fluids
        createFluidTextures("diesel", new Color(0xfd, 0xee, 0x87), new Color(0xf5, 0xcf, 0x47));
        createFluidTextures("gasoline", new Color(0x38, 0xbd, 0xf8), new Color(0x02, 0x84, 0xc7));

        // Items: Wrench, Diesel Bucket, Gas Bucket
        createWrenchTexture();
        createBucketTexture("diesel_bucket", new Color(0xfd, 0xee, 0x87));
        createBucketTexture("gasoline_bucket", new Color(0x38, 0xbd, 0xf8));
    }

    private static void createPortTexture(String name, Color inner, Color rim) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x38, 0x3d, 0x44));
        g.fillRect(0, 0, 16, 16);
        g.setColor(new Color(0x52, 0x5a, 0x65));
        g.drawRect(0, 0, 15, 15);
        // Circular flange socket
        g.setColor(rim);
        g.drawOval(3, 3, 9, 9);
        g.setColor(inner);
        g.fillOval(4, 4, 8, 8);
        g.setColor(new Color(0x11, 0x13, 0x15));
        g.fillOval(6, 6, 4, 4); // Pipe bore hole
        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/block/" + name + ".png"));
    }

    private static void createFluidTextures(String name, Color c1, Color c2) throws Exception {
        BufferedImage still = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = still.createGraphics();
        gs.setColor(c1);
        gs.fillRect(0, 0, 16, 16);
        gs.setColor(c2);
        for (int i = 0; i < 16; i += 4) {
            gs.drawLine(0, i, 15, i + 2);
        }
        gs.dispose();
        ImageIO.write(still, "PNG", new File(ASSETS + "textures/fluid/" + name + "_still.png"));
        ImageIO.write(still, "PNG", new File(ASSETS + "textures/fluid/" + name + "_flow.png"));

        // Create mcmeta
        String mcmeta = "{\"animation\":{\"frametime\":2,\"interpolate\":true}}";
        Files.writeString(Paths.get(ASSETS + "textures/fluid/" + name + "_flow.png.mcmeta"), mcmeta);
    }

    private static void createWrenchTexture() throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        // Diagonal wrench body
        g.setColor(new Color(0x94, 0xa3, 0xb8)); // Steel head
        g.fillRect(10, 2, 4, 4);
        g.fillRect(8, 4, 4, 4);
        g.setColor(new Color(0x00, 0x00, 0x00, 0)); // Transparent jaw cutout
        g.fillRect(11, 2, 2, 2);
        // Handle
        g.setColor(new Color(0x3b, 0x82, 0xf6)); // Blue rubber grip
        g.drawLine(3, 13, 8, 8);
        g.drawLine(2, 14, 7, 9);
        g.setColor(new Color(0x1d, 0x4e, 0xd8));
        g.drawLine(1, 15, 6, 10);
        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/refinery_wrench.png"));
    }

    private static void createTarTexture() throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x18, 0x18, 0x1b));
        g.fillOval(3, 4, 10, 9);
        g.setColor(new Color(0x27, 0x27, 0x2a));
        g.fillOval(5, 5, 6, 6);
        g.setColor(new Color(0x71, 0x71, 0x7a));
        g.fillRect(6, 6, 2, 2); // Glossy reflection
        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/tar.png"));
    }

    private static void createSulfurTexture() throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0xfacc15));
        g.fillOval(4, 5, 8, 7);
        g.setColor(new Color(0xea, 0xb3, 0x08));
        g.fillOval(3, 7, 7, 5);
        g.setColor(new Color(0xca, 0x8a, 0x04));
        g.fillOval(6, 8, 6, 5);
        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/sulfur_dust.png"));
    }

    private static void createBucketTexture(String name, Color fluidColor) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x64, 0x74, 0x8b));
        g.drawRect(4, 5, 7, 8);
        g.setColor(new Color(0x94, 0xa3, 0xb8));
        g.drawLine(3, 4, 12, 4);
        g.setColor(fluidColor);
        g.fillRect(5, 6, 6, 6);
        g.dispose();
        ImageIO.write(img, "PNG", new File(ASSETS + "textures/item/" + name + ".png"));
    }

    private static void generateGui() throws Exception {
        BufferedImage gui = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gui.createGraphics();
        g.setColor(new Color(0xc6, 0xc6, 0xc6));
        g.fillRect(0, 0, 176, 176);
        g.setColor(new Color(0xff, 0xff, 0xff));
        g.drawLine(0, 0, 175, 0);
        g.drawLine(0, 0, 0, 175);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.drawLine(175, 0, 175, 175);
        g.drawLine(0, 175, 175, 175);

        drawWell(g, 17, 19, 18, 54);
        drawWell(g, 73, 19, 18, 54);
        drawWell(g, 95, 19, 18, 54);
        drawWell(g, 161, 19, 8, 54);

        drawWell(g, 43, 52, 18, 18);
        g.setColor(new Color(0x88, 0x88, 0x88));
        g.drawRect(46, 38, 12, 12);

        drawWell(g, 125, 43, 18, 18);
        drawWell(g, 143, 43, 18, 18);
        drawWell(g, 125, 61, 18, 18);
        drawWell(g, 143, 61, 18, 18);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawWell(g, 7 + col * 18, 93 + row * 18, 18, 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawWell(g, 7 + col * 18, 151, 18, 18);
        }

        g.dispose();
        ImageIO.write(gui, "PNG", new File(ASSETS + "textures/gui/refinery.png"));
    }

    private static void drawWell(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0x37, 0x37, 0x37));
        g.drawLine(x, y, x + w - 1, y);
        g.drawLine(x, y, x, y + h - 1);
        g.setColor(new Color(0xff, 0xff, 0xff));
        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        g.setColor(new Color(0x8b, 0x8b, 0x8b));
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

    private static void generateItemDefinitions() throws Exception {
        String[] allItems = {
            "refinery_controller",
            "reinforced_casing",
            "combustion_chamber",
            "distillation_column",
            "heat_exchanger",
            "crude_oil_port",
            "diesel_port",
            "gas_port",
            "byproduct_port",
            "refinery_wrench",
            "tar",
            "sulfur_dust",
            "diesel_bucket",
            "gasoline_bucket"
        };
        for (String name : allItems) {
            writeJson(ASSETS + "items/" + name + ".json", """
            {
              "model": {
                "type": "minecraft:model",
                "model": "refinery:item/%s"
              }
            }
            """.formatted(name));
        }
    }

    private static void generateModelsAndBlockstates() throws Exception {
        // Blocks: reinforced_casing
        writeJson(ASSETS + "models/block/reinforced_casing.json", """
        {
          "parent": "minecraft:block/cube",
          "textures": {
            "particle": "refinery:block/reinforced_casing",
            "down": "refinery:block/reinforced_casing",
            "up": "refinery:block/reinforced_casing",
            "north": "refinery:block/reinforced_casing",
            "south": "refinery:block/reinforced_casing",
            "west": "refinery:block/reinforced_casing",
            "east": "refinery:block/reinforced_casing"
          }
        }
        """);
        writeJson(ASSETS + "blockstates/reinforced_casing.json", """
        {
          "variants": {
            "": { "model": "refinery:block/reinforced_casing" }
          }
        }
        """);
        writeItemModel("reinforced_casing", "block/reinforced_casing");

        // Controller with 3D Depth
        writeJson(ASSETS + "models/block/refinery_controller.json", createControllerModel("controller_front_off"));
        writeJson(ASSETS + "models/block/refinery_controller_active.json", createControllerModel("controller_front_on"));

        // Generate full 32-variant blockstate for controller (facing x assembled x active x blueprint)
        StringBuilder sb = new StringBuilder("{\n  \"variants\": {\n");
        String[] facings = { "north", "south", "west", "east" };
        int[] yRots = { 0, 180, 270, 90 };
        boolean first = true;
        for (int f = 0; f < 4; f++) {
            for (boolean assembled : new boolean[]{ false, true }) {
                for (boolean active : new boolean[]{ false, true }) {
                    for (boolean blueprint : new boolean[]{ false, true }) {
                        if (!first) sb.append(",\n");
                        first = false;
                        String model = (assembled || active) ? "refinery:block/refinery_controller_active" : "refinery:block/refinery_controller";
                        int yRot = yRots[f];
                        String rotStr = yRot == 0 ? "" : ", \"y\": " + yRot;
                        sb.append(String.format("    \"facing=%s,assembled=%b,active=%b,blueprint=%b\": { \"model\": \"%s\"%s }",
                                facings[f], assembled, active, blueprint, model, rotStr));
                    }
                }
            }
        }
        sb.append("\n  }\n}\n");
        writeJson(ASSETS + "blockstates/refinery_controller.json", sb.toString());
        writeItemModel("refinery_controller", "block/refinery_controller");

        // Distillation Column with cylindrical depth & support rods
        writeJson(ASSETS + "models/block/distillation_column.json", """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/distillation_column",
            "column": "refinery:block/distillation_column",
            "casing": "refinery:block/reinforced_casing"
          },
          "elements": [
            {
              "from": [1, 0, 1],
              "to": [15, 2, 15],
              "faces": {
                "down": { "uv": [1, 1, 15, 15], "texture": "#casing" },
                "up": { "uv": [1, 1, 15, 15], "texture": "#casing" },
                "north": { "uv": [1, 14, 15, 16], "texture": "#column" },
                "south": { "uv": [1, 14, 15, 16], "texture": "#column" },
                "west": { "uv": [1, 14, 15, 16], "texture": "#column" },
                "east": { "uv": [1, 14, 15, 16], "texture": "#column" }
              }
            },
            {
              "from": [3, 2, 3],
              "to": [13, 14, 13],
              "faces": {
                "north": { "uv": [3, 2, 13, 14], "texture": "#column" },
                "south": { "uv": [3, 2, 13, 14], "texture": "#column" },
                "west": { "uv": [3, 2, 13, 14], "texture": "#column" },
                "east": { "uv": [3, 2, 13, 14], "texture": "#column" }
              }
            },
            {
              "from": [1, 14, 1],
              "to": [15, 16, 15],
              "faces": {
                "down": { "uv": [1, 1, 15, 15], "texture": "#casing" },
                "up": { "uv": [1, 1, 15, 15], "texture": "#casing" },
                "north": { "uv": [1, 0, 15, 2], "texture": "#column" },
                "south": { "uv": [1, 0, 15, 2], "texture": "#column" },
                "west": { "uv": [1, 0, 15, 2], "texture": "#column" },
                "east": { "uv": [1, 0, 15, 2], "texture": "#column" }
              }
            },
            {
              "from": [1, 2, 1], "to": [2.5, 14, 2.5],
              "faces": { "north": { "texture": "#casing" }, "west": { "texture": "#casing" }, "east": { "texture": "#casing" }, "south": { "texture": "#casing" } }
            },
            {
              "from": [13.5, 2, 1], "to": [15, 14, 2.5],
              "faces": { "north": { "texture": "#casing" }, "west": { "texture": "#casing" }, "east": { "texture": "#casing" }, "south": { "texture": "#casing" } }
            },
            {
              "from": [1, 2, 13.5], "to": [2.5, 14, 15],
              "faces": { "north": { "texture": "#casing" }, "west": { "texture": "#casing" }, "east": { "texture": "#casing" }, "south": { "texture": "#casing" } }
            },
            {
              "from": [13.5, 2, 13.5], "to": [15, 14, 15],
              "faces": { "north": { "texture": "#casing" }, "west": { "texture": "#casing" }, "east": { "texture": "#casing" }, "south": { "texture": "#casing" } }
            }
          ]
        }
        """);
        writeJson(ASSETS + "blockstates/distillation_column.json", """
        { "variants": { "": { "model": "refinery:block/distillation_column" } } }
        """);
        writeItemModel("distillation_column", "block/distillation_column");

        // Heat Exchanger (Radiator louvers)
        writeJson(ASSETS + "models/block/heat_exchanger.json", """
        {
          "parent": "minecraft:block/cube",
          "textures": {
            "particle": "refinery:block/heat_exchanger",
            "down": "refinery:block/reinforced_casing",
            "up": "refinery:block/reinforced_casing",
            "north": "refinery:block/heat_exchanger",
            "south": "refinery:block/heat_exchanger",
            "west": "refinery:block/heat_exchanger",
            "east": "refinery:block/heat_exchanger"
          }
        }
        """);
        writeJson(ASSETS + "blockstates/heat_exchanger.json", """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/heat_exchanger" },
            "facing=south": { "model": "refinery:block/heat_exchanger", "y": 180 },
            "facing=west": { "model": "refinery:block/heat_exchanger", "y": 270 },
            "facing=east": { "model": "refinery:block/heat_exchanger", "y": 90 }
          }
        }
        """);
        writeItemModel("heat_exchanger", "block/heat_exchanger");

        // Combustion Chamber
        writeJson(ASSETS + "models/block/combustion_chamber.json", createCombustionModel("combustion_front_off"));
        writeJson(ASSETS + "models/block/combustion_chamber_lit.json", createCombustionModel("combustion_front_on"));
        writeJson(ASSETS + "blockstates/combustion_chamber.json", """
        {
          "variants": {
            "facing=north,lit=false": { "model": "refinery:block/combustion_chamber" },
            "facing=south,lit=false": { "model": "refinery:block/combustion_chamber", "y": 180 },
            "facing=west,lit=false": { "model": "refinery:block/combustion_chamber", "y": 270 },
            "facing=east,lit=false": { "model": "refinery:block/combustion_chamber", "y": 90 },
            "facing=north,lit=true": { "model": "refinery:block/combustion_chamber_lit" },
            "facing=south,lit=true": { "model": "refinery:block/combustion_chamber_lit", "y": 180 },
            "facing=west,lit=true": { "model": "refinery:block/combustion_chamber_lit", "y": 270 },
            "facing=east,lit=true": { "model": "refinery:block/combustion_chamber_lit", "y": 90 }
          }
        }
        """);
        writeItemModel("combustion_chamber", "block/combustion_chamber");

        // Ports
        writePortFiles("crude_oil_port", "port_crude_oil");
        writePortFiles("diesel_port", "port_diesel");
        writePortFiles("gas_port", "port_gas");
        writePortFiles("byproduct_port", "port_byproduct");

        // Item models (parenting models/item)
        writeGeneratedItemModel("refinery_wrench");
        writeGeneratedItemModel("tar");
        writeGeneratedItemModel("sulfur_dust");
        writeGeneratedItemModel("diesel_bucket");
        writeGeneratedItemModel("gasoline_bucket");
    }

    private static String createControllerModel(String frontTex) {
        return """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/reinforced_casing",
            "casing": "refinery:block/reinforced_casing",
            "front": "refinery:block/%s"
          },
          "elements": [
            {
              "from": [0, 0, 1.5],
              "to": [16, 16, 16],
              "faces": {
                "down": { "texture": "#casing" },
                "up": { "texture": "#casing" },
                "north": { "texture": "#front" },
                "south": { "texture": "#casing" },
                "west": { "texture": "#casing" },
                "east": { "texture": "#casing" }
              }
            },
            {
              "from": [0, 0, 0],
              "to": [2, 16, 1.5],
              "faces": {
                "down": { "texture": "#casing" },
                "up": { "texture": "#casing" },
                "north": { "texture": "#casing" },
                "east": { "texture": "#casing" },
                "west": { "texture": "#casing" }
              }
            },
            {
              "from": [14, 0, 0],
              "to": [16, 16, 1.5],
              "faces": {
                "down": { "texture": "#casing" },
                "up": { "texture": "#casing" },
                "north": { "texture": "#casing" },
                "east": { "texture": "#casing" },
                "west": { "texture": "#casing" }
              }
            },
            {
              "from": [2, 14, 0],
              "to": [14, 16, 1.5],
              "faces": {
                "up": { "texture": "#casing" },
                "north": { "texture": "#casing" },
                "down": { "texture": "#casing" }
              }
            },
            {
              "from": [2, 0, 0],
              "to": [14, 2, 1.5],
              "faces": {
                "down": { "texture": "#casing" },
                "north": { "texture": "#casing" },
                "up": { "texture": "#casing" }
              }
            }
          ]
        }
        """.formatted(frontTex);
    }

    private static String createCombustionModel(String frontTex) {
        return """
        {
          "parent": "minecraft:block/cube",
          "textures": {
            "particle": "refinery:block/reinforced_casing",
            "down": "refinery:block/reinforced_casing",
            "up": "refinery:block/reinforced_casing",
            "north": "refinery:block/%s",
            "south": "refinery:block/reinforced_casing",
            "west": "refinery:block/reinforced_casing",
            "east": "refinery:block/reinforced_casing"
          }
        }
        """.formatted(frontTex);
    }

    private static void writePortFiles(String portName, String portTex) throws Exception {
        writeJson(ASSETS + "models/block/" + portName + ".json", """
        {
          "parent": "minecraft:block/block",
          "textures": {
            "particle": "refinery:block/reinforced_casing",
            "casing": "refinery:block/reinforced_casing",
            "port": "refinery:block/%s"
          },
          "elements": [
            {
              "from": [0, 0, 0],
              "to": [16, 16, 16],
              "faces": {
                "down": { "texture": "#casing" },
                "up": { "texture": "#casing" },
                "north": { "texture": "#port" },
                "south": { "texture": "#casing" },
                "west": { "texture": "#casing" },
                "east": { "texture": "#casing" }
              }
            },
            {
              "from": [4, 4, -1],
              "to": [12, 12, 0],
              "faces": {
                "north": { "uv": [4, 4, 12, 12], "texture": "#port" },
                "up": { "texture": "#casing" },
                "down": { "texture": "#casing" },
                "west": { "texture": "#casing" },
                "east": { "texture": "#casing" }
              }
            }
          ]
        }
        """.formatted(portTex));

        writeJson(ASSETS + "blockstates/" + portName + ".json", """
        {
          "variants": {
            "facing=north": { "model": "refinery:block/%s" },
            "facing=south": { "model": "refinery:block/%s", "y": 180 },
            "facing=west": { "model": "refinery:block/%s", "y": 270 },
            "facing=east": { "model": "refinery:block/%s", "y": 90 }
          }
        }
        """.formatted(portName, portName, portName, portName));

        writeItemModel(portName, "block/" + portName);
    }

    private static void writeItemModel(String name, String parent) throws Exception {
        writeJson(ASSETS + "models/item/" + name + ".json", """
        {
          "parent": "refinery:%s"
        }
        """.formatted(parent));
    }

    private static void writeGeneratedItemModel(String name) throws Exception {
        writeJson(ASSETS + "models/item/" + name + ".json", """
        {
          "parent": "minecraft:item/generated",
          "textures": {
            "layer0": "refinery:item/%s"
          }
        }
        """.formatted(name));
    }

    private static void generateTags() throws Exception {
        writeJson(DATA + "c/tags/fluid/crude_oil.json", """
        {
          "values": [
            "#c:oil",
            { "id": "logistics:core/crude_oil", "required": false }
          ]
        }
        """);

        writeJson(DATA + "c/tags/fluid/diesel.json", """
        {
          "values": [
            "refinery:diesel",
            "refinery:flowing_diesel",
            { "id": "logistics:core/fuel_oil", "required": false }
          ]
        }
        """);

        writeJson(DATA + "c/tags/fluid/gasoline.json", """
        {
          "values": [
            "refinery:gasoline",
            "refinery:flowing_gasoline"
          ]
        }
        """);

        writeJson(DATA + "c/tags/item/tar.json", """
        {
          "values": [
            "refinery:tar",
            { "id": "logistics:core/tar", "required": false },
            { "id": "logistics:tar", "required": false }
          ]
        }
        """);

        writeJson(DATA + "c/tags/item/dusts/sulfur.json", """
        {
          "values": [
            "refinery:sulfur_dust",
            { "id": "logistics:core/sulfur_dust", "required": false },
            { "id": "logistics:sulfur_dust", "required": false }
          ]
        }
        """);
    }

    private static void generateRecipes() throws Exception {
        writeJson(DATA + "refinery/recipe/refinery_wrench.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            " I ",
            " SI",
            "S  "
          ],
          "key": {
            "I": "minecraft:iron_ingot",
            "S": "minecraft:stick"
          },
          "result": {
            "id": "refinery:refinery_wrench",
            "count": 1
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/reinforced_casing.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "IDI",
            "DCD",
            "IDI"
          ],
          "key": {
            "I": "minecraft:iron_ingot",
            "D": "minecraft:deepslate",
            "C": "minecraft:copper_ingot"
          },
          "result": {
            "id": "refinery:reinforced_casing",
            "count": 4
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/refinery_controller.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "GRG",
            "ICI",
            "III"
          ],
          "key": {
            "G": "minecraft:glass",
            "R": "minecraft:comparator",
            "I": "minecraft:iron_ingot",
            "C": "refinery:reinforced_casing"
          },
          "result": {
            "id": "refinery:refinery_controller",
            "count": 1
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/combustion_chamber.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "CBC",
            "BFB",
            "CBC"
          ],
          "key": {
            "C": "refinery:reinforced_casing",
            "B": "minecraft:iron_bars",
            "F": "minecraft:blast_furnace"
          },
          "result": {
            "id": "refinery:combustion_chamber",
            "count": 1
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/distillation_column.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "CGC",
            "IGI",
            "CGC"
          ],
          "key": {
            "C": "minecraft:copper_ingot",
            "G": "minecraft:glass",
            "I": "minecraft:iron_ingot"
          },
          "result": {
            "id": "refinery:distillation_column",
            "count": 2
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/heat_exchanger.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "BBB",
            "ICI",
            "BBB"
          ],
          "key": {
            "B": "minecraft:iron_bars",
            "I": "minecraft:copper_ingot",
            "C": "refinery:reinforced_casing"
          },
          "result": {
            "id": "refinery:heat_exchanger",
            "count": 2
          }
        }
        """);

        writePortRecipe("crude_oil_port", "minecraft:hopper", "minecraft:bucket");
        writePortRecipe("diesel_port", "minecraft:gold_ingot", "minecraft:bucket");
        writePortRecipe("gas_port", "minecraft:copper_ingot", "minecraft:glass");
        writePortRecipe("byproduct_port", "minecraft:hopper", "minecraft:chest");

        writeJson(DATA + "refinery/recipe/gunpowder_from_sulfur.json", """
        {
          "type": "minecraft:crafting_shapeless",
          "ingredients": [
            "refinery:sulfur_dust",
            "minecraft:charcoal",
            "minecraft:bone_meal"
          ],
          "result": {
            "id": "minecraft:gunpowder",
            "count": 2
          }
        }
        """);

        writeJson(DATA + "refinery/recipe/torch_from_tar.json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            "T",
            "S"
          ],
          "key": {
            "T": "refinery:tar",
            "S": "minecraft:stick"
          },
          "result": {
            "id": "minecraft:torch",
            "count": 4
          }
        }
        """);
    }

    private static void writePortRecipe(String port, String item1, String item2) throws Exception {
        writeJson(DATA + "refinery/recipe/" + port + ".json", """
        {
          "type": "minecraft:crafting_shaped",
          "pattern": [
            " I ",
            "ACA",
            " I "
          ],
          "key": {
            "C": "refinery:reinforced_casing",
            "I": "%s",
            "A": "%s"
          },
          "result": {
            "id": "refinery:%s",
            "count": 1
          }
        }
        """.formatted(item1, item2, port));
    }

    private static void generateLang() throws Exception {
        // lang/en_us.json is maintained directly in src/main/resources/assets/refinery/lang/en_us.json
    }

    private static void writeJson(String path, String content) throws Exception {
        Files.writeString(Paths.get(path), content.trim());
    }
}
