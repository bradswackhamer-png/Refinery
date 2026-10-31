package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ConveyorExpansionsAssetGenerator {
    private static final String ASSETS = "src/main/resources/assets/refinery/";

    public static void main(String[] args) throws Exception {
        new File(ASSETS + "textures/item").mkdirs();
        generateSlopedConveyorIcon();
        generateRightAngleConveyorIcon();
        System.out.println("Expansion icons generated successfully!");
    }

    private static void generateSlopedConveyorIcon() throws Exception {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(3, 23, 26, 7);

        // Vertical support leg on high end (right side)
        g.setColor(new Color(0x70, 0x76, 0x80));
        g.fillRect(23, 10, 3, 15);
        g.setColor(new Color(0x40, 0x46, 0x4E));
        g.fillRect(25, 10, 2, 15);
        // Base plate
        g.setColor(new Color(0x30, 0x34, 0x3A));
        g.fillRect(22, 24, 6, 2);

        // Aluminum frame side (inclined)
        // Polygon from low (left) to high (right)
        int[] skirtX = {4, 18, 28, 14};
        int[] skirtY = {21, 23, 13, 8}; // 14,8 is higher up
        g.setColor(new Color(0x9E, 0xA7, 0xB2));
        g.fillPolygon(skirtX, skirtY, 4);

        // T-slot channel along inclined side
        g.setColor(new Color(0x36, 0x3C, 0x44));
        g.drawLine(6, 21, 26, 14);
        g.drawLine(6, 22, 26, 15);

        // Inclined matte black belt top
        int[] beltX = {4, 18, 28, 14};
        int[] beltY = {18, 20, 7, 3};
        g.setColor(new Color(0x1F, 0x22, 0x26));
        g.fillPolygon(beltX, beltY, 4);

        // Belt ridges
        g.setColor(new Color(0x30, 0x35, 0x3C));
        g.drawLine(8, 16, 17, 7);
        g.drawLine(12, 17, 21, 8);
        g.drawLine(15, 18, 24, 9);

        // Top aluminum guide rails
        g.setColor(new Color(0xD6, 0xDC, 0xE3));
        g.drawLine(3, 19, 17, 21); // bottom lip
        g.drawLine(14, 3, 28, 7);  // top lip
        g.setColor(new Color(0xB5, 0xBC, 0xC6));
        g.drawLine(4, 18, 14, 4);  // inclined left rail

        // Rollers at ends
        g.setColor(new Color(0xEB, 0xF1, 0xF7));
        g.drawLine(16, 21, 18, 21);
        g.drawLine(26, 7, 28, 8);

        // Up arrow indicator badge (neon orange)
        g.setColor(new Color(0xE6, 0x6E, 0x19));
        g.fillRect(6, 6, 3, 6);
        int[] arrX = {4, 7, 10};
        int[] arrY = {6, 3, 6};
        g.fillPolygon(arrX, arrY, 3);

        g.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/sloped_conveyor_belt.png"));
    }

    private static void generateRightAngleConveyorIcon() throws Exception {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(4, 21, 24, 8);

        // Aluminum corner frame skirt
        int[] skirtX = {4, 16, 27, 27, 18, 4};
        int[] skirtY = {18, 24, 20, 16, 17, 14};
        g.setColor(new Color(0x9E, 0xA7, 0xB2));
        g.fillPolygon(skirtX, skirtY, 6);

        // T-slot groove along skirt
        g.setColor(new Color(0x36, 0x3C, 0x44));
        g.drawLine(5, 19, 16, 24);
        g.drawLine(17, 24, 26, 20);

        // L-shaped curved matte black belt surface
        int[] beltX = {5, 16, 27, 24, 16, 12};
        int[] beltY = {15, 21, 17, 12, 14, 10};
        g.setColor(new Color(0x1F, 0x22, 0x26));
        g.fillPolygon(beltX, beltY, 6);

        // Belt curve lines
        g.setColor(new Color(0x32, 0x38, 0x40));
        g.drawArc(7, 6, 16, 14, 200, 90);
        g.drawArc(8, 7, 18, 15, 200, 90);

        // Outer corner guide rail
        g.setColor(new Color(0xD6, 0xDC, 0xE3));
        g.drawLine(4, 16, 16, 22);
        g.drawLine(16, 22, 27, 18);
        g.setColor(new Color(0xB5, 0xBC, 0xC6));
        g.drawLine(4, 17, 16, 23);
        g.drawLine(16, 23, 27, 19);

        // Inner corner corner bracket
        g.setColor(new Color(0x7D, 0x85, 0x90));
        g.fillRect(11, 10, 4, 4);

        // Corner turning arrow badge (yellow/orange curved turn arrow)
        g.setColor(new Color(0xFA, 0xA6, 0x1A));
        g.drawArc(5, 4, 12, 12, 180, 90);
        g.drawLine(11, 4, 8, 2);
        g.drawLine(11, 4, 9, 6);

        g.dispose();
        ImageIO.write(icon, "PNG", new File(ASSETS + "textures/item/right_angle_conveyor_belt.png"));
    }
}
