package tools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AidaAssetGenerator {
    private static final int W = 128;
    private static final int H = 128;

    public static void main(String[] args) throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 1. Fill entire sheet with default industrial off-white base
        Color offWhite = new Color(0xE6, 0xEA, 0xE4);
        Color offWhiteLight = new Color(0xF0, 0xF4, 0xEE);
        Color offWhiteShadow = new Color(0xD8, 0xDC, 0xD6);
        g.setColor(offWhite);
        g.fillRect(0, 0, W, H);

        // Subtle machine panel texture variation
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int noise = ((x * 37 + y * 17) % 7) - 3;
                int r = Math.min(255, Math.max(0, offWhite.getRed() + noise));
                int gr = Math.min(255, Math.max(0, offWhite.getGreen() + noise));
                int b = Math.min(255, Math.max(0, offWhite.getBlue() + noise));
                img.setRGB(x, y, new Color(r, gr, b).getRGB());
            }
        }

        // 2. Base Floor Section (u: 0..128, v: 0..36)
        // Dark steel floor trim along the lower edges of base
        Color darkTrim = new Color(0x33, 0x3B, 0x46);
        g.setColor(darkTrim);
        g.fillRect(0, 32, 128, 4); // bottom rim of all 4 base sides
        // Anchor bolt plates on the base sides
        Color boltPlate = new Color(0x4B, 0x55, 0x63);
        Color bolt = new Color(0x11, 0x18, 0x27);
        for (int bx : new int[]{ 4, 24, 36, 56, 68, 88, 100, 120 }) {
            g.setColor(boltPlate);
            g.fillRect(bx, 33, 4, 3);
            g.setColor(bolt);
            g.fillRect(bx + 1, 34, 2, 1);
        }

        // 3. Crown Front Face (u: 32..64, v: 68..80) - AIDA Branding & Analog Dial
        // Background panel of crown front
        g.setColor(offWhite);
        g.fillRect(32, 68, 32, 12);
        // Beveled highlight at top of crown front
        g.setColor(offWhiteLight);
        g.drawLine(32, 68, 63, 68);
        g.setColor(offWhiteShadow);
        g.drawLine(32, 79, 63, 79);

        // A. Green AIDA Emblem & Text (Left side: x = 33..42, y = 70..75)
        Color aidaGreen = new Color(0x00, 0x78, 0x48);
        Color aidaGreenLight = new Color(0x10, 0x90, 0x58);

        // AIDA Diamond Logo Icon (4-quadrant diamond chevron)
        img.setRGB(34, 71, aidaGreenLight.getRGB());
        img.setRGB(33, 72, aidaGreen.getRGB());
        img.setRGB(35, 72, aidaGreen.getRGB());
        img.setRGB(34, 73, aidaGreen.getRGB());
        img.setRGB(36, 73, aidaGreenLight.getRGB());
        img.setRGB(35, 74, aidaGreen.getRGB());

        // "AIDA" Pixel Lettering (3px high)
        // A
        img.setRGB(37, 72, aidaGreen.getRGB());
        img.setRGB(37, 73, aidaGreen.getRGB());
        img.setRGB(37, 74, aidaGreen.getRGB());
        img.setRGB(38, 71, aidaGreen.getRGB());
        img.setRGB(38, 73, aidaGreen.getRGB());
        img.setRGB(39, 72, aidaGreen.getRGB());
        img.setRGB(39, 73, aidaGreen.getRGB());
        img.setRGB(39, 74, aidaGreen.getRGB());
        // I
        img.setRGB(40, 71, aidaGreen.getRGB());
        img.setRGB(40, 72, aidaGreen.getRGB());
        img.setRGB(40, 73, aidaGreen.getRGB());
        img.setRGB(40, 74, aidaGreen.getRGB());
        // D
        img.setRGB(41, 71, aidaGreen.getRGB());
        img.setRGB(41, 72, aidaGreen.getRGB());
        img.setRGB(41, 73, aidaGreen.getRGB());
        img.setRGB(41, 74, aidaGreen.getRGB());
        img.setRGB(42, 71, aidaGreen.getRGB());
        img.setRGB(42, 74, aidaGreen.getRGB());
        img.setRGB(43, 72, aidaGreen.getRGB());
        img.setRGB(43, 73, aidaGreen.getRGB());

        // B. Large Round Analog Dial Gauge (Center: center x=48, center y=73, radius 4)
        Color dialBezel = new Color(0x1F, 0x24, 0x2B);
        Color dialFace = new Color(0xF8, 0xFA, 0xFC);
        Color dialTicks = new Color(0x47, 0x55, 0x69);
        Color dialNeedle = new Color(0x0F, 0x17, 0x2A);
        Color dialPivot = new Color(0xDC, 0x26, 0x26); // red center pivot

        // Dial outer square mount frame
        g.setColor(offWhiteShadow);
        g.drawRect(44, 69, 7, 7);
        // Dial circular bezel
        g.setColor(dialBezel);
        g.drawOval(44, 69, 7, 7);
        // Dial face
        g.setColor(dialFace);
        g.fillOval(45, 70, 5, 5);
        // Tick marks on perimeter
        img.setRGB(47, 70, dialTicks.getRGB()); // 12 o'clock
        img.setRGB(49, 72, dialTicks.getRGB()); // 3 o'clock
        img.setRGB(47, 74, dialTicks.getRGB()); // 6 o'clock
        img.setRGB(45, 72, dialTicks.getRGB()); // 9 o'clock
        // Needle pointer (pointing straight up to 12 o'clock)
        img.setRGB(47, 71, dialNeedle.getRGB());
        img.setRGB(47, 72, dialPivot.getRGB()); // center red dot

        // C. Green "NC1-1500" Designation (Right: x = 53..62, y = 71..74)
        g.setColor(aidaGreen);
        // N
        img.setRGB(53, 72, aidaGreen.getRGB()); img.setRGB(53, 73, aidaGreen.getRGB()); img.setRGB(53, 74, aidaGreen.getRGB());
        img.setRGB(54, 73, aidaGreen.getRGB());
        img.setRGB(55, 72, aidaGreen.getRGB()); img.setRGB(55, 73, aidaGreen.getRGB()); img.setRGB(55, 74, aidaGreen.getRGB());
        // C
        img.setRGB(56, 72, aidaGreen.getRGB()); img.setRGB(56, 73, aidaGreen.getRGB()); img.setRGB(56, 74, aidaGreen.getRGB());
        img.setRGB(57, 72, aidaGreen.getRGB()); img.setRGB(57, 74, aidaGreen.getRGB());
        // 1
        img.setRGB(58, 72, aidaGreen.getRGB()); img.setRGB(58, 73, aidaGreen.getRGB()); img.setRGB(58, 74, aidaGreen.getRGB());
        // -
        img.setRGB(59, 73, aidaGreen.getRGB());
        // 1500
        img.setRGB(60, 72, aidaGreen.getRGB()); img.setRGB(60, 73, aidaGreen.getRGB()); img.setRGB(60, 74, aidaGreen.getRGB());
        img.setRGB(61, 72, aidaGreen.getRGB()); img.setRGB(61, 73, aidaGreen.getRGB()); img.setRGB(61, 74, aidaGreen.getRGB());

        // 4. Bolster Plate / Stamping Bed (u: 0..70, v: 80..108)
        // Top Face of Bolster (u: 26..44, v: 80..106) -> 18 x 26 pixels
        Color steelBed = new Color(0x64, 0x74, 0x8B);
        Color steelLight = new Color(0x94, 0xA3, 0xB8);
        Color tSlot = new Color(0x1E, 0x29, 0x3B);
        Color tSlotEdge = new Color(0x33, 0x41, 0x55);

        g.setColor(steelBed);
        g.fillRect(26, 80, 18, 26);
        // Outer beveled border
        g.setColor(steelLight);
        g.drawRect(26, 80, 17, 25);

        // Milled T-Slots (3 vertical slots running lengthwise)
        for (int tx : new int[]{ 29, 34, 40 }) {
            g.setColor(tSlot);
            g.fillRect(tx, 81, 2, 24);
            g.setColor(tSlotEdge);
            g.drawLine(tx - 1, 81, tx - 1, 104);
            g.drawLine(tx + 2, 81, tx + 2, 104);
        }
        // Cross clamping slots (2 horizontal)
        for (int ty : new int[]{ 87, 98 }) {
            g.setColor(tSlot);
            g.fillRect(27, ty, 16, 2);
        }

        // Bolster Front & Side Edges
        g.setColor(steelBed);
        g.fillRect(0, 80, 26, 2);
        g.setColor(steelLight);
        g.drawLine(0, 80, 25, 80);

        // 5. Side Electrical Cabinet (u: 80..128, v: 80..106)
        Color cabGrey = new Color(0xCF, 0xD4, 0xDA);
        Color cabShadow = new Color(0x9A, 0xA2, 0xAC);
        Color louverDark = new Color(0x37, 0x41, 0x51);
        Color louverLight = new Color(0xE5, 0xE7, 0xEB);

        g.setColor(cabGrey);
        g.fillRect(80, 80, 48, 26);

        // Cabinet door frame outline
        g.setColor(cabShadow);
        g.drawRect(82, 82, 20, 22);

        // Ventilation Louvers (2 sets of cooling slots)
        for (int louverY : new int[]{ 84, 86, 88, 96, 98, 100 }) {
            g.setColor(louverDark);
            g.drawLine(85, louverY, 98, louverY);
            g.setColor(louverLight);
            g.drawLine(85, louverY + 1, 98, louverY + 1);
        }

        // Black Door Handle / Latch
        g.setColor(new Color(0x11, 0x18, 0x27));
        g.fillRect(100, 92, 1, 4);
        g.setColor(new Color(0x4B, 0x55, 0x63));
        g.fillRect(99, 93, 3, 2);

        // Yellow Electrical Hazard Warning Triangle
        Color warnYellow = new Color(0xEA, 0xB3, 0x08);
        img.setRGB(92, 92, warnYellow.getRGB());
        img.setRGB(91, 93, warnYellow.getRGB()); img.setRGB(92, 93, Color.BLACK.getRGB()); img.setRGB(93, 93, warnYellow.getRGB());
        img.setRGB(90, 94, warnYellow.getRGB()); img.setRGB(91, 94, warnYellow.getRGB()); img.setRGB(92, 94, warnYellow.getRGB()); img.setRGB(93, 94, warnYellow.getRGB()); img.setRGB(94, 94, warnYellow.getRGB());

        // 6. Hydraulic Cylinder (u: 70..80, v: 80..100)
        // Amber / Bronze Hydraulic Piston Rod
        Color amberCylinder = new Color(0xD9, 0x77, 0x06);
        Color amberLight = new Color(0xFB, 0xBF, 0x24);
        Color amberDark = new Color(0x92, 0x40, 0x0E);
        Color chromeRod = new Color(0xE2, 0xE8, 0xF0);

        g.setColor(amberCylinder);
        g.fillRect(70, 80, 10, 16);
        // Vertical sheen on cylinder
        g.setColor(amberLight);
        g.fillRect(72, 80, 2, 16);
        g.setColor(amberDark);
        g.fillRect(77, 80, 2, 16);
        // Chrome wiper seal ring at top & bottom
        g.setColor(chromeRod);
        g.drawLine(70, 80, 79, 80);
        g.drawLine(70, 95, 79, 95);

        // 7. Stamping Ram Slide & Safety Yellow Die Bar (u: 0..100, v: 108..128)
        // A. Ram Slide Body (white machine casting)
        g.setColor(offWhite);
        g.fillRect(0, 108, 54, 20);
        g.setColor(offWhiteShadow);
        g.drawRect(0, 108, 53, 19);

        // B. Safety Yellow Die Bar (u: 54..100, v: 108..128)
        Color safetyYellow = new Color(0xFA, 0xCC, 0x15);
        Color safetyYellowDark = new Color(0xCA, 0x8A, 0x04);
        Color slotBlack = new Color(0x18, 0x18, 0x1B);

        g.setColor(safetyYellow);
        g.fillRect(54, 108, 46, 20);
        // Highlight along top edge
        g.setColor(new Color(0xFE, 0xF0, 0x8A));
        g.drawLine(54, 108, 99, 108);
        g.setColor(safetyYellowDark);
        g.drawLine(54, 127, 99, 127);

        // Punch toolholder clamping slots (black rectangular slots)
        g.setColor(slotBlack);
        for (int sx = 58; sx < 96; sx += 6) {
            g.fillRect(sx, 114, 3, 4);
        }

        // 8. Lower Operator Pendant Control Box (u: 100..128, v: 108..128)
        Color pendantGrey = new Color(0x9C, 0xA3, 0xAF);
        Color eStopRed = new Color(0xDC, 0x26, 0x26);
        Color eStopLight = new Color(0xEF, 0x44, 0x44);
        Color buttonYellow = new Color(0xF5, 0x9E, 0x0B);
        Color buttonGreen = new Color(0x10, 0xB9, 0x81);
        Color buttonBlue = new Color(0x3B, 0x82, 0xF6);

        g.setColor(pendantGrey);
        g.fillRect(100, 108, 28, 20);
        g.setColor(new Color(0x6B, 0x72, 0x80));
        g.drawRect(100, 108, 27, 19);

        // Emergency Stop Mushroom Knob (prominent red round button)
        g.setColor(eStopRed);
        g.fillOval(104, 112, 6, 6);
        g.setColor(eStopLight);
        g.fillOval(105, 113, 2, 2);

        // Yellow Cycle Start / Inch Buttons
        g.setColor(buttonYellow);
        g.fillRect(113, 113, 3, 2);
        g.setColor(buttonGreen);
        g.fillRect(117, 113, 3, 2);
        g.setColor(buttonBlue);
        g.fillRect(121, 113, 3, 2);

        // Key selector switch
        g.setColor(Color.BLACK);
        g.fillRect(113, 118, 2, 3);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(113, 119, 4, 1);

        // Flexible silver conduit texture pattern
        for (int cy = 108; cy < 128; cy += 2) {
            img.setRGB(126, cy, new Color(0x47, 0x55, 0x69).getRGB());
            img.setRGB(127, cy, new Color(0x94, 0xA3, 0xB8).getRGB());
        }

        g.dispose();

        // Save output to refinery textures
        File outFile = new File("src/main/resources/assets/refinery/textures/entity/aida_press.png");
        outFile.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", outFile);
        System.out.println("Generated AIDA NC1-1500 press texture successfully: " + outFile.getAbsolutePath());
    }
}
