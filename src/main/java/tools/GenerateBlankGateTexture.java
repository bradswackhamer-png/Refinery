package tools;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenerateBlankGateTexture {
    public static void main(String[] args) throws Exception {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Dark Cast-Iron Base Plate
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int base = 70;
                // Subtle metal grain noise
                int noise = ((x * 17 + y * 23) % 7) - 3;
                int r = Math.min(255, Math.max(0, base + noise));
                int gr = Math.min(255, Math.max(0, base + 4 + noise));
                int b = Math.min(255, Math.max(0, base + 10 + noise));
                img.setRGB(x, y, new Color(r, gr, b, 255).getRGB());
            }
        }

        // 2. Heavy Industrial Beveled Border
        for (int i = 0; i < size; i++) {
            // Top/Left highlight
            img.setRGB(i, 0, new Color(115, 122, 132, 255).getRGB());
            img.setRGB(0, i, new Color(105, 112, 122, 255).getRGB());
            // Bottom/Right deep shadow
            img.setRGB(i, size - 1, new Color(38, 42, 48, 255).getRGB());
            img.setRGB(size - 1, i, new Color(42, 46, 52, 255).getRGB());
        }

        // 3. Stamped Diagonal Cross "X" (Blank Seal)
        int[] strokeDark = new Color(35, 38, 44, 255).getRGB() == 0 ? new int[0] : null;
        int darkCol = new Color(35, 38, 44, 255).getRGB();
        int hiCol = new Color(125, 135, 148, 255).getRGB();

        for (int i = 4; i <= 11; i++) {
            // Diagonal 1
            img.setRGB(i, i, darkCol);
            if (i + 1 <= 11) img.setRGB(i + 1, i, hiCol);
            // Diagonal 2
            img.setRGB(i, 15 - i, darkCol);
            if (i - 1 >= 4) img.setRGB(i - 1, 15 - i, hiCol);
        }

        // Center hub circle
        img.setRGB(7, 7, darkCol);
        img.setRGB(8, 7, darkCol);
        img.setRGB(7, 8, darkCol);
        img.setRGB(8, 8, hiCol);

        // 4. Heavy Hex Studs / Bolts at 4 Corners
        int[][] bolts = {{2, 2}, {13, 2}, {2, 13}, {13, 13}};
        int boltHi = new Color(195, 205, 218, 255).getRGB();
        int boltSh = new Color(28, 32, 38, 255).getRGB();
        int boltMid = new Color(140, 150, 162, 255).getRGB();

        for (int[] b : bolts) {
            int bx = b[0];
            int by = b[1];
            img.setRGB(bx, by, boltMid);
            if (bx + 1 < size) img.setRGB(bx + 1, by, boltSh);
            if (by + 1 < size) img.setRGB(bx, by + 1, boltSh);
            img.setRGB(bx, by, boltHi);
        }

        g.dispose();

        // Save block and item textures
        File blockOut = new File("src/main/resources/assets/refinery/textures/block/blank_gate.png");
        File itemOut = new File("src/main/resources/assets/refinery/textures/item/blank_gate.png");
        blockOut.getParentFile().mkdirs();
        itemOut.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", blockOut);
        ImageIO.write(img, "PNG", itemOut);

        System.out.println("Generated blank_gate textures successfully!");
    }
}
