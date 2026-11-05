package tools;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PipeWireAssetGenerator {
    private static final String ITEM_TEX_DIR = "src/main/resources/assets/refinery/textures/item/";
    private static final String BLOCK_TEX_DIR = "src/main/resources/assets/refinery/textures/block/pipe/";

    public static void main(String[] args) throws IOException {
        new File(ITEM_TEX_DIR).mkdirs();
        new File(BLOCK_TEX_DIR).mkdirs();

        generateWireSpool("pipe_wire_red",
                new Color(0x600808), // dark outline
                new Color(0xE53935), // main wire
                new Color(0xFF7674), // highlight
                new Color(0xAB1717)  // shadow
        );

        generateWireSpool("pipe_wire_blue",
                new Color(0x062842), // dark outline
                new Color(0x0288D1), // main wire
                new Color(0x4FC3F7), // highlight
                new Color(0x01579B)  // shadow
        );

        generateWireSpool("pipe_wire_green",
                new Color(0x0E3812), // dark outline
                new Color(0x2E7D32), // main wire
                new Color(0x66BB6A), // highlight
                new Color(0x1B5E20)  // shadow
        );

        generateWireSpool("pipe_wire_yellow",
                new Color(0x4E3806), // dark outline
                new Color(0xFBC02D), // main wire
                new Color(0xFFF176), // highlight
                new Color(0xF57F17)  // shadow
        );

        generateValvePipeTextures();

        System.out.println("Pipe wire icons and valve textures generated successfully!");
    }

    private static void generateWireSpool(String name, Color wireOutline, Color wireMain, Color wireHi, Color wireShadow) throws IOException {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Flange palette (warm golden amber wood from reference image)
        Color woodBorder = new Color(0x2A190D);
        Color woodHi = new Color(0xFBB03B);
        Color woodMain = new Color(0xD8871E);
        Color woodShadow = new Color(0x9E560B);

        // Grid initialization (transparent)
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, 0x00000000);
            }
        }

        // --- TOP FLANGE ---
        // Y = 1: Top border
        for (int x = 2; x <= 10; x++) img.setRGB(x, 1, woodBorder.getRGB());
        // Y = 2: Upper flange face
        img.setRGB(1, 2, woodBorder.getRGB());
        img.setRGB(2, 2, woodHi.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 2, woodHi.getRGB());
        img.setRGB(10, 2, woodMain.getRGB());
        img.setRGB(11, 2, woodBorder.getRGB());
        // Y = 3: Lower flange bevel
        img.setRGB(1, 3, woodBorder.getRGB());
        img.setRGB(2, 3, woodMain.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 3, woodShadow.getRGB());
        img.setRGB(10, 3, woodBorder.getRGB());
        img.setRGB(11, 3, woodBorder.getRGB());

        // --- BOTTOM FLANGE ---
        // Y = 11: Upper bevel of bottom flange
        img.setRGB(1, 11, woodBorder.getRGB());
        img.setRGB(2, 11, woodMain.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 11, woodShadow.getRGB());
        img.setRGB(10, 11, woodMain.getRGB());
        img.setRGB(11, 11, woodBorder.getRGB());
        // Y = 12: Bottom flange face
        img.setRGB(1, 12, woodBorder.getRGB());
        img.setRGB(2, 12, woodHi.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 12, woodMain.getRGB());
        img.setRGB(10, 12, woodShadow.getRGB());
        img.setRGB(11, 12, woodBorder.getRGB());
        // Y = 13: Bottom border
        for (int x = 2; x <= 10; x++) img.setRGB(x, 13, woodBorder.getRGB());

        // --- WOUND COILS (Y = 4..10) ---
        // Side borders of bobbin
        for (int y = 4; y <= 10; y++) {
            img.setRGB(2, y, wireOutline.getRGB());
            img.setRGB(10, y, wireOutline.getRGB());
        }

        // Horizontal wire coils across X = 3..9
        // Coil 1 (Y = 4):
        for (int x = 3; x <= 9; x++) img.setRGB(x, 4, wireHi.getRGB());
        // Coil 2 (Y = 5, 6):
        for (int x = 3; x <= 9; x++) img.setRGB(x, 5, wireMain.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 6, wireShadow.getRGB());
        // Coil 3 (Y = 7, 8):
        for (int x = 3; x <= 9; x++) img.setRGB(x, 7, wireMain.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 8, wireShadow.getRGB());
        // Coil 4 (Y = 9, 10):
        for (int x = 3; x <= 9; x++) img.setRGB(x, 9, wireMain.getRGB());
        for (int x = 3; x <= 9; x++) img.setRGB(x, 10, wireShadow.getRGB());

        // Specular highlights on coils for 3D round look (X = 4..5)
        img.setRGB(4, 5, wireHi.getRGB());
        img.setRGB(5, 5, wireHi.getRGB());
        img.setRGB(4, 7, wireHi.getRGB());
        img.setRGB(5, 7, wireHi.getRGB());
        img.setRGB(4, 9, wireHi.getRGB());
        img.setRGB(5, 9, wireHi.getRGB());

        // --- EXTENDING WIRE TAIL ---
        // Tail emerges from Y = 5 at right side of spool, turns down, and curls right!
        // Outer border for horizontal segment:
        img.setRGB(11, 4, wireOutline.getRGB());
        img.setRGB(12, 4, wireOutline.getRGB());
        img.setRGB(13, 4, wireOutline.getRGB());

        // Horizontal wire fill (Y = 5):
        img.setRGB(10, 5, wireHi.getRGB()); // bridge through spool wall
        img.setRGB(11, 5, wireMain.getRGB());
        img.setRGB(12, 5, wireMain.getRGB());
        img.setRGB(13, 5, wireHi.getRGB()); // corner highlight

        // Below horizontal segment border:
        img.setRGB(11, 6, wireOutline.getRGB());

        // Vertical segment wire fill (X = 13, Y = 6..8):
        img.setRGB(13, 6, wireMain.getRGB());
        img.setRGB(13, 7, wireMain.getRGB());
        img.setRGB(13, 8, wireMain.getRGB());

        // Vertical segment borders (X = 12 on left, X = 14 on right):
        img.setRGB(12, 7, wireOutline.getRGB());
        img.setRGB(12, 8, wireOutline.getRGB());

        img.setRGB(14, 5, wireOutline.getRGB());
        img.setRGB(14, 6, wireOutline.getRGB());
        img.setRGB(14, 7, wireOutline.getRGB());
        img.setRGB(14, 8, wireOutline.getRGB());

        // Tail bottom hook / curl (Y = 9 turns right into X = 14):
        img.setRGB(12, 9, wireOutline.getRGB());
        img.setRGB(13, 9, wireMain.getRGB());
        img.setRGB(14, 9, wireMain.getRGB());
        img.setRGB(15, 9, wireOutline.getRGB());

        // Bottom border of curl:
        img.setRGB(13, 10, wireOutline.getRGB());
        img.setRGB(14, 10, wireOutline.getRGB());

        File outFile = new File(ITEM_TEX_DIR + name + ".png");
        ImageIO.write(img, "PNG", outFile);
    }

    private static void generateValvePipeTextures() throws IOException {
        // 1. Block Texture: textures/block/pipe/valve_pipe.png (16x16)
        BufferedImage blockTex = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color steelHi = new Color(0xCBD5E1);
        Color steelMid = new Color(0x94A3B8);
        Color steelDark = new Color(0x475569);
        Color steelBorder = new Color(0x1E293B);

        Color redHi = new Color(0xFF6B6B);
        Color redMid = new Color(0xEF4444);
        Color redDark = new Color(0xB91C1C);

        Color brassHi = new Color(0xFDE047);
        Color brassMid = new Color(0xEAB308);
        Color brassDark = new Color(0xA16207);

        // Fill background with steel plate pattern
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                blockTex.setRGB(x, y, steelMid.getRGB());
            }
        }
        // Top and bottom borders
        for (int x = 0; x < 16; x++) {
            blockTex.setRGB(x, 0, steelHi.getRGB());
            blockTex.setRGB(x, 15, steelBorder.getRGB());
            blockTex.setRGB(x, 1, steelDark.getRGB());
            blockTex.setRGB(x, 14, steelDark.getRGB());
        }
        // Left & right edges
        for (int y = 0; y < 16; y++) {
            blockTex.setRGB(0, y, steelBorder.getRGB());
            blockTex.setRGB(15, y, steelDark.getRGB());
        }

        // Central Redstone Valve Core / Actuator Housing (X = 4..11, Y = 3..12)
        for (int y = 3; y <= 12; y++) {
            for (int x = 4; x <= 11; x++) {
                blockTex.setRGB(x, y, redDark.getRGB());
            }
        }
        // Redstone glowing core (X = 5..10, Y = 4..11)
        for (int y = 4; y <= 11; y++) {
            for (int x = 5; x <= 10; x++) {
                blockTex.setRGB(x, y, redMid.getRGB());
            }
        }
        // Core highlight
        for (int y = 5; y <= 10; y++) {
            for (int x = 6; x <= 9; x++) {
                blockTex.setRGB(x, y, redHi.getRGB());
            }
        }
        // Brass valve collar / rotary wheel rim
        for (int i = 5; i <= 10; i++) {
            blockTex.setRGB(i, 3, brassMid.getRGB());
            blockTex.setRGB(i, 12, brassDark.getRGB());
        }
        blockTex.setRGB(4, 4, brassHi.getRGB());
        blockTex.setRGB(11, 4, brassHi.getRGB());
        blockTex.setRGB(4, 11, brassDark.getRGB());
        blockTex.setRGB(11, 11, brassDark.getRGB());

        File blockFile = new File(BLOCK_TEX_DIR + "valve_pipe.png");
        ImageIO.write(blockTex, "PNG", blockFile);

        // 2. Item Icon: textures/item/valve_pipe.png (16x16)
        BufferedImage itemTex = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                itemTex.setRGB(x, y, 0x00000000);
            }
        }

        // Draw horizontal steel pipe segment (Y = 6..10, X = 1..14)
        for (int x = 1; x <= 14; x++) {
            itemTex.setRGB(x, 6, steelBorder.getRGB());
            itemTex.setRGB(x, 7, steelHi.getRGB());
            itemTex.setRGB(x, 8, steelMid.getRGB());
            itemTex.setRGB(x, 9, steelDark.getRGB());
            itemTex.setRGB(x, 10, steelBorder.getRGB());
        }
        // Left & right pipe collar flanges
        for (int y = 5; y <= 11; y++) {
            itemTex.setRGB(1, y, steelBorder.getRGB());
            itemTex.setRGB(2, y, steelMid.getRGB());
            itemTex.setRGB(13, y, steelMid.getRGB());
            itemTex.setRGB(14, y, steelBorder.getRGB());
        }

        // Central Valve Housing Box (X = 6..9, Y = 4..12)
        for (int y = 4; y <= 12; y++) {
            for (int x = 6; x <= 9; x++) {
                itemTex.setRGB(x, y, redDark.getRGB());
            }
        }
        for (int y = 5; y <= 11; y++) {
            for (int x = 7; x <= 8; x++) {
                itemTex.setRGB(x, y, redMid.getRGB());
            }
        }
        itemTex.setRGB(7, 5, redHi.getRGB());
        itemTex.setRGB(8, 5, redHi.getRGB());

        // Top Valve Turning Wheel / Stem (Y = 2..4, X = 5..10)
        itemTex.setRGB(7, 3, brassDark.getRGB());
        itemTex.setRGB(8, 3, brassDark.getRGB());
        // Brass rotary wheel handle (Y = 2)
        for (int x = 5; x <= 10; x++) {
            itemTex.setRGB(x, 2, brassMid.getRGB());
        }
        itemTex.setRGB(5, 2, brassHi.getRGB());
        itemTex.setRGB(10, 2, brassDark.getRGB());
        itemTex.setRGB(7, 1, brassHi.getRGB());
        itemTex.setRGB(8, 1, brassHi.getRGB());

        // Outline around valve housing
        for (int y = 4; y <= 12; y++) {
            itemTex.setRGB(5, y, steelBorder.getRGB());
            itemTex.setRGB(10, y, steelBorder.getRGB());
        }
        for (int x = 6; x <= 9; x++) {
            itemTex.setRGB(x, 13, steelBorder.getRGB());
        }

        File itemFile = new File(ITEM_TEX_DIR + "valve_pipe.png");
        ImageIO.write(itemTex, "PNG", itemFile);
    }
}
