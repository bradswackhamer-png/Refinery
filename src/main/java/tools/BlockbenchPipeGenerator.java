package tools;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

public class BlockbenchPipeGenerator {

    private static final String DESKTOP_DIR = "C:/Users/swack/Desktop/Pipe_Models_and_Textures/blockbench_models/";
    private static final String TEXTURE_DIR = "src/main/resources/assets/refinery/textures/block/pipe/";

    public static void main(String[] args) throws Exception {
        File outDir = new File(DESKTOP_DIR);
        outDir.mkdirs();

        generateBBModel();
        generateJsonBlockModels();

        System.out.println("Blockbench models generated successfully in: " + DESKTOP_DIR);
    }

    public static void generateBBModel() throws Exception {
        String steelB64 = getBase64Texture(TEXTURE_DIR + "steel_pipe.png");
        String copperB64 = getBase64Texture(TEXTURE_DIR + "copper_pipe.png");
        String ironB64 = getBase64Texture(TEXTURE_DIR + "iron_pipe.png");
        String goldB64 = getBase64Texture(TEXTURE_DIR + "gold_pipe.png");
        String diamondB64 = getBase64Texture(TEXTURE_DIR + "diamond_pipe.png");
        String brassB64 = getBase64Texture(TEXTURE_DIR + "brass_pipe.png");
        String woodB64 = getBase64Texture(TEXTURE_DIR + "wood_pipe.png");
        String windowB64 = getBase64Texture(TEXTURE_DIR + "pipe_window.png");

        StringBuilder bb = new StringBuilder();
        bb.append("{\n");
        bb.append("  \"meta\": {\n");
        bb.append("    \"format_version\": \"4.10\",\n");
        bb.append("    \"model_format\": \"java_block\",\n");
        bb.append("    \"box_uv\": false\n");
        bb.append("  },\n");
        bb.append("  \"name\": \"industrial_refinery_pipe\",\n");
        bb.append("  \"model_identifier\": \"industrial_refinery_pipe\",\n");
        bb.append("  \"visible_box\": [1, 1, 0],\n");
        bb.append("  \"resolution\": {\"width\": 128, \"height\": 128},\n");

        // Textures
        bb.append("  \"textures\": [\n");
        bb.append(makeTextureJson("Steel Pipe", "steel_pipe", "0", steelB64, true)).append(",\n");
        bb.append(makeTextureJson("Copper Pipe", "copper_pipe", "1", copperB64, false)).append(",\n");
        bb.append(makeTextureJson("Iron Pipe", "iron_pipe", "2", ironB64, false)).append(",\n");
        bb.append(makeTextureJson("Gold Pipe", "gold_pipe", "3", goldB64, false)).append(",\n");
        bb.append(makeTextureJson("Diamond Pipe", "diamond_pipe", "4", diamondB64, false)).append(",\n");
        bb.append(makeTextureJson("Brass Pipe", "brass_pipe", "5", brassB64, false)).append(",\n");
        bb.append(makeTextureJson("Wood Pipe", "wood_pipe", "6", woodB64, false)).append(",\n");
        bb.append(makeTextureJson("Sight Glass Window", "pipe_window", "7", windowB64, false)).append("\n");
        bb.append("  ],\n");

        // Elements
        bb.append("  \"elements\": [\n");

        String uCore = UUID.randomUUID().toString();
        String uStraightZ = UUID.randomUUID().toString();
        String uStraightX = UUID.randomUUID().toString();
        String uStraightY = UUID.randomUUID().toString();

        String uArmN = UUID.randomUUID().toString();
        String uArmS = UUID.randomUUID().toString();
        String uArmW = UUID.randomUUID().toString();
        String uArmE = UUID.randomUUID().toString();
        String uArmD = UUID.randomUUID().toString();
        String uArmU = UUID.randomUUID().toString();

        String uFlangeN = UUID.randomUUID().toString();
        String uFlangeS = UUID.randomUUID().toString();
        String uFlangeW = UUID.randomUUID().toString();
        String uFlangeE = UUID.randomUUID().toString();
        String uFlangeD = UUID.randomUUID().toString();
        String uFlangeU = UUID.randomUUID().toString();

        String uWinN = UUID.randomUUID().toString();
        String uWinS = UUID.randomUUID().toString();
        String uWinW = UUID.randomUUID().toString();
        String uWinE = UUID.randomUUID().toString();

        // 1. Center Core (6x6x6, from [5,5,5] to [11,11,11])
        bb.append(makeBbCube("Center Core", 5, 5, 5, 11, 11, 11, 0, uCore)).append(",\n");

        // 2. Straight Runs (from 0 to 16)
        bb.append(makeBbCube("Straight Run (Z)", 5, 5, 0, 11, 11, 16, 0, uStraightZ)).append(",\n");
        bb.append(makeBbCube("Straight Run (X)", 0, 5, 5, 16, 11, 11, 0, uStraightX)).append(",\n");
        bb.append(makeBbCube("Straight Run (Y)", 5, 0, 5, 11, 16, 11, 0, uStraightY)).append(",\n");

        // 3. Directional Connection Arms
        bb.append(makeBbCube("Arm North (-Z)", 5, 5, 0, 11, 11, 5, 0, uArmN)).append(",\n");
        bb.append(makeBbCube("Arm South (+Z)", 5, 5, 11, 11, 11, 16, 0, uArmS)).append(",\n");
        bb.append(makeBbCube("Arm West (-X)", 0, 5, 5, 5, 11, 11, 0, uArmW)).append(",\n");
        bb.append(makeBbCube("Arm East (+X)", 11, 5, 5, 16, 11, 11, 0, uArmE)).append(",\n");
        bb.append(makeBbCube("Arm Down (-Y)", 5, 0, 5, 11, 5, 11, 0, uArmD)).append(",\n");
        bb.append(makeBbCube("Arm Up (+Y)", 5, 11, 5, 11, 16, 11, 0, uArmU)).append(",\n");

        // 4. Machine Connection Flanges (8x8 collar, 1.5px thick)
        bb.append(makeBbCube("Flange North", 4, 4, 0, 12, 12, 1.5, 0, uFlangeN)).append(",\n");
        bb.append(makeBbCube("Flange South", 4, 4, 14.5, 12, 12, 16, 0, uFlangeS)).append(",\n");
        bb.append(makeBbCube("Flange West", 0, 4, 4, 1.5, 12, 12, 0, uFlangeW)).append(",\n");
        bb.append(makeBbCube("Flange East", 14.5, 4, 4, 16, 12, 12, 0, uFlangeE)).append(",\n");
        bb.append(makeBbCube("Flange Down", 4, 0, 4, 12, 1.5, 12, 0, uFlangeD)).append(",\n");
        bb.append(makeBbCube("Flange Up", 4, 14.5, 4, 12, 16, 12, 0, uFlangeU)).append(",\n");

        // 5. Sight Glass Inspection Windows (2px wide strip, 0.05px relief to avoid z-fighting)
        bb.append(makeBbCube("Sight Glass North", 7, 7, -0.05, 9, 9, 5, 7, uWinN)).append(",\n");
        bb.append(makeBbCube("Sight Glass South", 7, 7, 11, 9, 9, 16.05, 7, uWinS)).append(",\n");
        bb.append(makeBbCube("Sight Glass West", -0.05, 7, 7, 5, 9, 9, 7, uWinW)).append(",\n");
        bb.append(makeBbCube("Sight Glass East", 11, 7, 7, 16.05, 9, 9, 7, uWinE)).append("\n");

        bb.append("  ],\n");

        // Outliner (Organized hierarchy into folders)
        bb.append("  \"outliner\": [\n");
        bb.append("    {\n");
        bb.append("      \"name\": \"Center Hub\",\n");
        bb.append("      \"origin\": [8, 8, 8],\n");
        bb.append("      \"isOpen\": true,\n");
        bb.append("      \"uuid\": \"").append(UUID.randomUUID()).append("\",\n");
        bb.append("      \"children\": [\"").append(uCore).append("\"]\n");
        bb.append("    },\n");
        bb.append("    {\n");
        bb.append("      \"name\": \"Full Straight Pipe Runs\",\n");
        bb.append("      \"origin\": [8, 8, 8],\n");
        bb.append("      \"isOpen\": false,\n");
        bb.append("      \"visibility\": false,\n");
        bb.append("      \"uuid\": \"").append(UUID.randomUUID()).append("\",\n");
        bb.append("      \"children\": [\"").append(uStraightZ).append("\", \"").append(uStraightX).append("\", \"").append(uStraightY).append("\"]\n");
        bb.append("    },\n");
        bb.append("    {\n");
        bb.append("      \"name\": \"Connecting Arms (6 Directions)\",\n");
        bb.append("      \"origin\": [8, 8, 8],\n");
        bb.append("      \"isOpen\": true,\n");
        bb.append("      \"uuid\": \"").append(UUID.randomUUID()).append("\",\n");
        bb.append("      \"children\": [\"").append(uArmN).append("\", \"").append(uArmS).append("\", \"").append(uArmW).append("\", \"").append(uArmE).append("\", \"").append(uArmD).append("\", \"").append(uArmU).append("\"]\n");
        bb.append("    },\n");
        bb.append("    {\n");
        bb.append("      \"name\": \"Machine Flanges\",\n");
        bb.append("      \"origin\": [8, 8, 8],\n");
        bb.append("      \"isOpen\": false,\n");
        bb.append("      \"visibility\": true,\n");
        bb.append("      \"uuid\": \"").append(UUID.randomUUID()).append("\",\n");
        bb.append("      \"children\": [\"").append(uFlangeN).append("\", \"").append(uFlangeS).append("\", \"").append(uFlangeW).append("\", \"").append(uFlangeE).append("\", \"").append(uFlangeD).append("\", \"").append(uFlangeU).append("\"]\n");
        bb.append("    },\n");
        bb.append("    {\n");
        bb.append("      \"name\": \"Sight Glass Inspection Windows\",\n");
        bb.append("      \"origin\": [8, 8, 8],\n");
        bb.append("      \"isOpen\": false,\n");
        bb.append("      \"visibility\": true,\n");
        bb.append("      \"uuid\": \"").append(UUID.randomUUID()).append("\",\n");
        bb.append("      \"children\": [\"").append(uWinN).append("\", \"").append(uWinS).append("\", \"").append(uWinW).append("\", \"").append(uWinE).append("\"]\n");
        bb.append("    }\n");
        bb.append("  ]\n");
        bb.append("}\n");

        writeFile(DESKTOP_DIR + "industrial_refinery_pipe.bbmodel", bb.toString());
    }

    private static String makeTextureJson(String name, String idName, String texIndex, String base64, boolean particle) {
        return "    {\n" +
                "      \"name\": \"" + name + "\",\n" +
                "      \"folder\": \"block\",\n" +
                "      \"namespace\": \"refinery\",\n" +
                "      \"id\": \"" + texIndex + "\",\n" +
                "      \"particle\": " + particle + ",\n" +
                "      \"render_mode\": \"default\",\n" +
                "      \"visible\": true,\n" +
                "      \"mode\": \"bitmap\",\n" +
                "      \"saved\": true,\n" +
                "      \"uuid\": \"" + UUID.randomUUID() + "\",\n" +
                "      \"source\": \"data:image/png;base64," + base64 + "\"\n" +
                "    }";
    }

    private static String makeBbCube(String name, double x1, double y1, double z1, double x2, double y2, double z2, int textureId, String uuid) {
        double ox = (x1 + x2) / 2.0;
        double oy = (y1 + y2) / 2.0;
        double oz = (z1 + z2) / 2.0;

        return "    {\n" +
                "      \"name\": \"" + name + "\",\n" +
                "      \"box_uv\": false,\n" +
                "      \"rescale\": false,\n" +
                "      \"locked\": false,\n" +
                "      \"from\": [" + x1 + ", " + y1 + ", " + z1 + "],\n" +
                "      \"to\": [" + x2 + ", " + y2 + ", " + z2 + "],\n" +
                "      \"origin\": [" + ox + ", " + oy + ", " + oz + "],\n" +
                "      \"color\": 0,\n" +
                "      \"uuid\": \"" + uuid + "\",\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "},\n" +
                "        \"south\": {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "},\n" +
                "        \"east\":  {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "},\n" +
                "        \"west\":  {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "},\n" +
                "        \"up\":    {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "},\n" +
                "        \"down\":  {\"uv\": [0, 0, 16, 16], \"texture\": " + textureId + "}\n" +
                "      }\n" +
                "    }";
    }

    public static void generateJsonBlockModels() throws Exception {
        String[] materials = {
                "steel", "copper", "iron", "gold", "diamond", "emerald",
                "brass", "cast_iron", "wood", "void", "quartz"
        };

        for (String mat : materials) {
            String texKey = "refinery:block/pipe/" + mat + "_pipe";
            String winKey = "refinery:block/pipe/pipe_window";

            // 1. Straight Pipe (North-South Z-run)
            writeFile(DESKTOP_DIR + mat + "_pipe_straight.json", makeJsonStraight(texKey, winKey));

            // 2. 6-Way Cross Junction (Center core + 6 connection arms)
            writeFile(DESKTOP_DIR + mat + "_pipe_junction_6way.json", makeJsonCross(texKey, winKey));

            // 3. T-Junction (North, South, East)
            writeFile(DESKTOP_DIR + mat + "_pipe_t_junction.json", makeJsonTee(texKey, winKey));

            // 4. 90-Degree Corner (North to East)
            writeFile(DESKTOP_DIR + mat + "_pipe_corner_elbow.json", makeJsonCorner(texKey, winKey));

            // 5. Pipe End with Machine Connection Flange
            writeFile(DESKTOP_DIR + mat + "_pipe_with_flange.json", makeJsonFlange(texKey, winKey));
        }
    }

    private static String makeJsonStraight(String tex, String win) {
        return "{\n" +
                "  \"ambientocclusion\": true,\n" +
                "  \"textures\": {\n" +
                "    \"particle\": \"" + tex + "\",\n" +
                "    \"pipe\": \"" + tex + "\",\n" +
                "    \"window\": \"" + win + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\n" +
                "      \"name\": \"straight_pipe_z\",\n" +
                "      \"from\": [5, 5, 0],\n" +
                "      \"to\": [11, 11, 16],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"},\n" +
                "        \"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"},\n" +
                "        \"east\":  {\"uv\": [0, 5, 16, 11], \"texture\": \"#pipe\"},\n" +
                "        \"west\":  {\"uv\": [0, 5, 16, 11], \"texture\": \"#pipe\"},\n" +
                "        \"up\":    {\"uv\": [5, 0, 11, 16], \"texture\": \"#pipe\"},\n" +
                "        \"down\":  {\"uv\": [5, 0, 11, 16], \"texture\": \"#pipe\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"sight_glass_west\",\n" +
                "      \"from\": [4.95, 7, 0],\n" +
                "      \"to\": [4.95, 9, 16],\n" +
                "      \"faces\": {\n" +
                "        \"west\": {\"uv\": [0, 7, 16, 9], \"texture\": \"#window\"}\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"sight_glass_east\",\n" +
                "      \"from\": [11.05, 7, 0],\n" +
                "      \"to\": [11.05, 9, 16],\n" +
                "      \"faces\": {\n" +
                "        \"east\": {\"uv\": [0, 7, 16, 9], \"texture\": \"#window\"}\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
    }

    private static String makeJsonCross(String tex, String win) {
        return "{\n" +
                "  \"ambientocclusion\": true,\n" +
                "  \"textures\": {\n" +
                "    \"particle\": \"" + tex + "\",\n" +
                "    \"pipe\": \"" + tex + "\",\n" +
                "    \"window\": \"" + win + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\"name\": \"core\", \"from\": [5, 5, 5], \"to\": [11, 11, 11], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_north\", \"from\": [5, 5, 0], \"to\": [11, 11, 5], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_south\", \"from\": [5, 5, 11], \"to\": [11, 11, 16], \"faces\": {\"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_west\", \"from\": [0, 5, 5], \"to\": [5, 11, 11], \"faces\": {\"west\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_east\", \"from\": [11, 5, 5], \"to\": [16, 11, 11], \"faces\": {\"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_down\", \"from\": [5, 0, 5], \"to\": [11, 5, 11], \"faces\": {\"down\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_up\", \"from\": [5, 11, 5], \"to\": [11, 16, 11], \"faces\": {\"up\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}}}\n" +
                "  ]\n" +
                "}\n";
    }

    private static String makeJsonTee(String tex, String win) {
        return "{\n" +
                "  \"ambientocclusion\": true,\n" +
                "  \"textures\": {\n" +
                "    \"particle\": \"" + tex + "\",\n" +
                "    \"pipe\": \"" + tex + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\"name\": \"core\", \"from\": [5, 5, 5], \"to\": [11, 11, 11], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_north\", \"from\": [5, 5, 0], \"to\": [11, 11, 5], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_south\", \"from\": [5, 5, 11], \"to\": [11, 11, 16], \"faces\": {\"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_east\", \"from\": [11, 5, 5], \"to\": [16, 11, 11], \"faces\": {\"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}}}\n" +
                "  ]\n" +
                "}\n";
    }

    private static String makeJsonCorner(String tex, String win) {
        return "{\n" +
                "  \"ambientocclusion\": true,\n" +
                "  \"textures\": {\n" +
                "    \"particle\": \"" + tex + "\",\n" +
                "    \"pipe\": \"" + tex + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\"name\": \"core\", \"from\": [5, 5, 5], \"to\": [11, 11, 11], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_north\", \"from\": [5, 5, 0], \"to\": [11, 11, 5], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 0, 11, 5], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 11, 11, 16], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"arm_east\", \"from\": [11, 5, 5], \"to\": [16, 11, 11], \"faces\": {\"east\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"north\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [0, 5, 5, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [11, 5, 16, 11], \"texture\": \"#pipe\"}}}\n" +
                "  ]\n" +
                "}\n";
    }

    private static String makeJsonFlange(String tex, String win) {
        return "{\n" +
                "  \"ambientocclusion\": true,\n" +
                "  \"textures\": {\n" +
                "    \"particle\": \"" + tex + "\",\n" +
                "    \"pipe\": \"" + tex + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\"name\": \"arm_north\", \"from\": [5, 5, 0], \"to\": [11, 11, 8], \"faces\": {\"north\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [5, 5, 11, 11], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 5, 8, 11], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [8, 5, 16, 11], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [5, 0, 11, 8], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [5, 8, 11, 16], \"texture\": \"#pipe\"}}},\n" +
                "    {\"name\": \"flange_collar\", \"from\": [4, 4, 0], \"to\": [12, 12, 1.5], \"faces\": {\"north\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#pipe\"}, \"south\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#pipe\"}, \"east\": {\"uv\": [0, 4, 1.5, 12], \"texture\": \"#pipe\"}, \"west\": {\"uv\": [14.5, 4, 16, 12], \"texture\": \"#pipe\"}, \"up\": {\"uv\": [4, 0, 12, 1.5], \"texture\": \"#pipe\"}, \"down\": {\"uv\": [4, 14.5, 12, 16], \"texture\": \"#pipe\"}}}\n" +
                "  ]\n" +
                "}\n";
    }

    private static String getBase64Texture(String path) {
        try {
            byte[] bytes = Files.readAllBytes(new File(path).toPath());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }

    private static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        }
    }
}
