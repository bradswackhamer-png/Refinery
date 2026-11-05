package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectGui {
    public static void main(String[] args) throws Exception {
        BufferedImage bf = ImageIO.read(new File("src/main/resources/assets/refinery/textures/gui/blast_furnace.png"));

        // Iron slots (around x: 19..56, y: 21..58)
        System.out.println("Scanning BF top slots:");
        for (int y = 15; y < 65; y++) {
            int c1 = bf.getRGB(21, y) & 0xFFFFFF;
            int c2 = bf.getRGB(63, y) & 0xFFFFFF;
            int c3 = bf.getRGB(127, y) & 0xFFFFFF;
            if (c1 < 0x222222) {
                System.out.println("Dark pixel at (21, " + y + "): #" + Integer.toHexString(c1));
            }
        }
    }
}
