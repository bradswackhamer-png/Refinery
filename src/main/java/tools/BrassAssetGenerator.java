package tools;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class BrassAssetGenerator {
    public static void main(String[] args) throws Exception {
        File bronzeFile = new File("src/main/resources/assets/refinery/textures/item/bronze_ingot.png");
        BufferedImage bronze = ImageIO.read(bronzeFile);
        int w = bronze.getWidth();
        int h = bronze.getHeight();
        BufferedImage brass = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // Find min and max luminance of non-transparent pixels
        float minLum = 1.0f;
        float maxLum = 0.0f;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = bronze.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                if (a > 20) {
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    float lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255.0f;
                    if (lum < minLum) minLum = lum;
                    if (lum > maxLum) maxLum = lum;
                }
            }
        }

        // Color ramp for polished industrial brass
        Color outline = new Color(0x3B, 0x24, 0x06);
        Color deepShadow = new Color(0x78, 0x4B, 0x0C);
        Color shadow = new Color(0xB8, 0x7E, 0x16);
        Color mid = new Color(0xDE, 0xA9, 0x28);
        Color light = new Color(0xF5, 0xCE, 0x4E);
        Color highlight = new Color(0xFF, 0xFA, 0x94);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = bronze.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                if (a <= 20) {
                    brass.setRGB(x, y, 0);
                    continue;
                }
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                float lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255.0f;
                float t = (lum - minLum) / (maxLum - minLum);

                Color c;
                if (t < 0.15f) {
                    c = outline;
                } else if (t < 0.35f) {
                    c = deepShadow;
                } else if (t < 0.55f) {
                    c = shadow;
                } else if (t < 0.75f) {
                    c = mid;
                } else if (t < 0.90f) {
                    c = light;
                } else {
                    c = highlight;
                }
                brass.setRGB(x, y, (a << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue());
            }
        }

        File outFile = new File("src/main/resources/assets/refinery/textures/item/brass_ingot.png");
        ImageIO.write(brass, "PNG", outFile);
        System.out.println("Generated " + outFile.getAbsolutePath());
    }
}
