# Complete Guide: High-Fidelity 3D Monolithic Models & Texture Atlases
*A comprehensive engineering standard and step-by-step blueprint for creating realistic, zero-flicker 3D monolithic multiblock machines and procedural texture atlases in modern Minecraft (NeoForge 26.3+).*

---

## 1. Monolithic 3D Philosophy

In modern Minecraft modding, large multiblock structures (such as $3\times 3\times 3$, $5\times 5\times 5$, or custom footprints) often look repetitive when constructed from plain tiled voxel cubes. 

The **Monolithic 3D Architecture** transforms an assembled multiblock into a single, cohesive, realistic industrial installation:
- **Real-World Engineering**: Modeled after actual industrial heavy machinery (AIDA stamping presses, Scotch marine package boilers, Siemens turbine-generator skids, electric arc steelmaking furnaces, SMS continuous casting machines).
- **Physical Geometry**: Includes structural I-beam foundation skids, precision tie-rod columns, curved trunnions, hydraulic cylinders, busbars, radiator banks, observation viewports, and dedicated operator control consoles.
- **Dynamic Animations**: Reciprocating hydraulic rams, rotating shredder blades, oscillating graphite electrodes, 3D electric arc plasma discharges, rising/falling molten baths, and moving items/workpieces.
- **Zero Chunk Occlusion Bugs**: When assembled (`ASSEMBLED = true`), member blocks become invisible (`RenderShape.INVISIBLE`, `Shapes.empty()` occlusion shape, `refinery:block/invisible` blockstate), allowing the monolithic model to render smoothly without interior light clipping, void holes, or z-fighting.

---

## 2. The Zero-Flicker Geometry Architecture

Flickering (Z-fighting) and distorted textures are caused by three common mistakes:
1. **Texture Bleeding**: Textures bleeding over the borders of adjacent tiles in the atlas during mipmapping or bilinear interpolation.
2. **Coplanar Overlap**: Placing two separate boxes sharing the exact same coordinate plane (or penetrating by sub-millimeter fractions like $0.1$ units).
3. **Improper Normal Transformation**: Not transforming face normal vectors by the current `PoseStack` transformation matrix, causing lighting calculations to flip and fight as the model rotates.

### The 4 Rules of Flicker-Free Geometry:

#### Rule 1: The `0.15px` UV Padding Formula
Never map texture regions directly from integer edge to integer edge. In modern OpenGL/Vulkan pipelines, mipmapping samples neighboring pixels. Use a $0.15\text{px}$ inset margin:
```java
public record UVRect(float u0, float v0, float u1, float v1) {
    public static UVRect of(int x, int y, int w, int h) {
        return new UVRect(
                (x + 0.15f) / 256.0f,
                (y + 0.15f) / 256.0f,
                (x + w - 0.15f) / 256.0f,
                (y + h - 0.15f) / 256.0f
        );
    }
}
```

#### Rule 2: Single Multi-Faced Cuboids (No Double Boxing)
When creating an instrumented console, control cabinet, or viewport door, **do not** place a thin faceplate box on top of a cabinet box. That causes the back face of the faceplate and the front face of the cabinet to fight for depth buffer priority.

Instead, define **ONE** cuboid for the cabinet where:
- The **North face** (facing player) is mapped to `UV_CONSOLE_FRONT` (the dials, touch screen, and buttons).
- The **South, West, East, Up, and Down faces** are mapped to `UV_CONSOLE_CASING` (clean metal enclosure panel).
```java
// Operator Control Console (Single Non-Overlapping Cuboid)
staticCuboids.add(new Cuboid(
        -14.0f, 16.0f, 2.0f,
        -2.0f,  26.0f, 9.0f,
        UV_CONSOLE_FRONT,   // North (facing operator) -> screen, dials, buttons
        UV_CONSOLE_CASING,  // South (back)
        UV_CONSOLE_CASING,  // West (left side)
        UV_CONSOLE_CASING,  // East (right side)
        UV_CONSOLE_CASING,  // Up (top)
        UV_CONSOLE_CASING   // Down (bottom)
));
```

#### Rule 3: Transform Normals with PoseStack
Always pass `pose` to `consumer.setNormal(pose, nx, ny, nz)`. If you pass raw vector coordinates, the model's shading will distort and invert when placed facing South, East, or West.

#### Rule 4: Use `RenderTypes.entitySolid(...)`
Use `RenderTypes.entitySolid(TEXTURE)` for the main machine body. Solid render types perform immediate depth-buffer writes, preventing transparent alpha sorting artifacts. For animated transparent elements (e.g. glass viewports, holograms), render them in a separate transparent pass.

---

## 3. Procedural Texture Atlas Layout (256×256)

Rather than maintaining dozens of small image files, monolithic models use a single high-resolution $256\times 256$ ARGB texture atlas generated procedurally via Java2D (`Graphics2D`).

### Standard 256×256 Texture Grid:

| Row | Y Range | Dimensions | Texture Purpose |
| :--- | :--- | :--- | :--- |
| **Row 1** | $0 \dots 47$ | $96\times 48$, $64\times 48$, $48\times 48$, $48\times 48$ | Main Outer Shell, Foundation Base Skid, Refractory Hearth, Columns |
| **Row 2** | $48 \dots 95$ | $64\times 48$, $48\times 48$, $48\times 48$, $48\times 48$, $48\times 48$ | Domed Roof / Crown, Busbars / Dies, Transformer / Chutes, Viewport |
| **Row 3** | $96 \dots 143$ | $64\times 48$, $48\times 48$, $48\times 48$, $48\times 48$ | **Operator Control Console Front Faceplate**, Plasma / Billet, Hoppers |
| **Row 4** | $144 \dots 191$ | $64\times 48$, $48\times 48$, $48\times 48$, $48\times 48$, $48\times 48$ | **Console Enclosure Casing Metal**, Safety Hazard Stripes, Chrome Steel, Radiator Fins |
| **Row 5** | $192 \dots 239$ | $24\times 24$, $24\times 24$, $64\times 48$ | Status Beacon ON (Illuminated), Status Beacon OFF (Standby), Bed Plates |

---

## 4. Complete Step-by-Step Implementation Recipe

When designing a new monolithic building (e.g., Industrial Cracker, Chemical Reactor, Pelletizer, Induction Blast Smelter):

### Step 1: Create the Procedural Asset Generator (`tools/YourAssetGenerator.java`)
Create a standalone Java tool that builds the 256×256 texture atlas:
- Use `fillNoisyRect` to create realistic cast iron, brushed steel, or enamel texture with slight noise variance ($3\dots 5$).
- Use `Math.sin((px / (double) w) * Math.PI)` to create cylindrical gradient highlights for chrome shafts, tie-rods, and pipes.
- Draw digital OLED touchscreens, analog dials with red indicator needles, yellow E-stop collars with red mushroom caps, and 45-degree yellow/black hazard safety stripes.

### Step 2: Create the Model Class (`com.refinery.client.renderer.YourMachineModel.java`)
1. Define the `UVRect` record with the $0.15\text{px}$ inset margin.
2. Define the `Cuboid` record supporting per-face `UVRect` mapping.
3. Pre-build `List<Cuboid> staticCuboids` inside the constructor so cuboids are allocated **only once** on initialization rather than every frame.
4. Separate dynamic animated components (e.g. hydraulic ram, rotating shafts, fluid levels, status beacons) into dedicated fields.
5. Implement `emitCuboid` and `emitQuad` with `consumer.setNormal(pose, nx, ny, nz)`.

### Step 3: Create or Update the Block Entity Renderer (`YourMachineControllerRenderer.java`)
1. Hold a single instance of `YourMachineModel` as a member field.
2. In `submit(...)`:
   - Handle horizontal orientation rotation (`NORTH`, `SOUTH`, `EAST`, `WEST`).
   - Call `submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(YourMachineModel.TEXTURE), (pose, consumer) -> this.model.render(...));`.
   - Render in-world 3D workpieces (ingots, dusts, blocks) sinking in hoppers or traveling across conveyors using `ItemModelResolver`.

### Step 4: Configure Member Blocks & Blockstates
To ensure clean multiblock rendering:
1. Member blocks must implement the `ASSEMBLED` boolean blockstate property.
2. When `ASSEMBLED = true`:
   - Return `RenderShape.INVISIBLE` from `getRenderShape(BlockState)`.
   - Return `Shapes.empty()` from `getOcclusionShape(BlockState)` to prevent neighbor culling.
   - Forward player right-click interactions (opening GUI or toggling blueprints) to the controller block entity.
   - When broken, invoke `disassemble()` on the controller to restore member blocks.
3. Point `assembled=true` to `"refinery:block/invisible"` in blockstate JSONs.
4. Register `Capabilities.Item.BLOCK`, `Capabilities.Fluid.BLOCK`, and `Capabilities.Energy.BLOCK` across all member blocks.

---

## 5. Copy-Paste Code Templates

### Template A: `AssetGeneratorTemplate.java`
```java
package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class AssetGeneratorTemplate {
    public static void main(String[] args) throws Exception {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 1. Base metal background
        fillNoisyRect(g, 0, 0, 256, 256, new Color(0x22, 0x27, 0x30), 4, 1001L);

        // 2. Control Console Front Faceplate (0, 96, 64, 48)
        drawControlConsole(g, 0, 96, 64, 48);

        // 3. Console Casing (0, 144, 64, 48)
        fillNoisyRect(g, 0, 144, 64, 48, new Color(0x33, 0x3A, 0x45), 3, 1002L);

        // 4. Yellow/Black Hazard Stripes (64, 144, 48, 48)
        drawHazardStripes(g, 64, 144, 48, 48);

        g.dispose();
        File out = new File("src/main/resources/assets/refinery/textures/entity/my_machine.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Atlas generated: " + out.getAbsolutePath());
    }

    private static void drawControlConsole(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0x2D, 0x33, 0x3D), 3, 2001L);
        g.setColor(new Color(0x47, 0x55, 0x69));
        g.drawRect(x + 1, y + 1, w - 3, h - 3);

        // Digital screen
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fillRect(x + 6, y + 6, 32, 16);
        g.setColor(new Color(0x38, 0xBD, 0xF8));
        g.drawRect(x + 6, y + 6, 32, 16);
        g.setColor(new Color(0x22, 0xC5, 0x5E));
        g.fillRect(x + 9, y + 10, 18, 2);

        // Analog dial gauge
        g.setColor(new Color(0xF8, 0xFA, 0xFC));
        g.fillOval(x + 44, y + 6, 14, 14);
        g.setColor(new Color(0x1E, 0x29, 0x3B));
        g.drawOval(x + 44, y + 6, 14, 14);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.drawLine(x + 51, y + 13, x + 55, y + 9);

        // E-stop button on yellow collar
        g.setColor(new Color(0xFA, 0xCC, 0x15));
        g.fillRect(x + 45, y + 26, 12, 12);
        g.setColor(new Color(0xDC, 0x26, 0x26));
        g.fillRect(x + 47, y + 28, 8, 8);
    }

    private static void drawHazardStripes(Graphics2D g, int x, int y, int w, int h) {
        fillNoisyRect(g, x, y, w, h, new Color(0xFA, 0xCC, 0x15), 2, 3001L);
        g.setColor(new Color(0x18, 0x18, 0x1B));
        for (int sx = x - h; sx < x + w + h; sx += 8) {
            for (int lw = 0; lw < 4; lw++) {
                int x1 = sx + lw;
                int x2 = sx + h + lw;
                if ((x1 >= x && x1 < x + w) || (x2 >= x && x2 < x + w)) {
                    g.drawLine(Math.max(x, Math.min(x + w - 1, x1)), y,
                            Math.max(x, Math.min(x + w - 1, x2)), y + h - 1);
                }
            }
        }
    }

    private static void fillNoisyRect(Graphics2D g, int x, int y, int w, int h, Color base, int variance, long seed) {
        Random rand = new Random(seed);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int d = rand.nextInt(variance * 2 + 1) - variance;
                int r = Math.max(0, Math.min(255, base.getRed() + d));
                int gr = Math.max(0, Math.min(255, base.getGreen() + d));
                int b = Math.max(0, Math.min(255, base.getBlue() + d));
                g.setColor(new Color(r, gr, b));
                g.fillRect(x + px, y + py, 1, 1);
            }
        }
    }
}
```

---

### Template B: `MonolithicModelTemplate.java`
```java
package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MonolithicModelTemplate {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("refinery", "textures/entity/my_machine.png");
    private static final float TEX_W = 256.0f;
    private static final float TEX_H = 256.0f;

    public record UVRect(float u0, float v0, float u1, float v1) {
        public static UVRect of(int x, int y, int w, int h) {
            return new UVRect((x + 0.15f) / TEX_W, (y + 0.15f) / TEX_H, (x + w - 0.15f) / TEX_W, (y + h - 0.15f) / TEX_H);
        }
    }

    public record Cuboid(
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            UVRect north, UVRect south, UVRect west, UVRect east, UVRect up, UVRect down
    ) {
        public static Cuboid uniform(float x0, float y0, float z0, float x1, float y1, float z1, UVRect uv) {
            return new Cuboid(x0, y0, z0, x1, y1, z1, uv, uv, uv, uv, uv, uv);
        }
    }

    // UV Regions matching the 256x256 atlas
    private static final UVRect UV_CONSOLE_FRONT  = UVRect.of(0, 96, 64, 48);
    private static final UVRect UV_CONSOLE_CASING = UVRect.of(0, 144, 64, 48);
    private static final UVRect UV_HAZARD_STRIPES = UVRect.of(64, 144, 48, 48);

    private final List<Cuboid> staticCuboids = new ArrayList<>();

    public MonolithicModelTemplate() {
        // Operator Console Pedestal
        staticCuboids.add(Cuboid.uniform(-14.0f, 0.0f, 2.0f, -2.0f, 16.0f, 10.0f, UV_CONSOLE_CASING));

        // Operator Console Head (Single cuboid with front faceplate)
        staticCuboids.add(new Cuboid(
                -14.0f, 16.0f, 2.0f,
                -2.0f,  26.0f, 9.0f,
                UV_CONSOLE_FRONT,   // North -> instrument faceplate
                UV_CONSOLE_CASING,  // South
                UV_CONSOLE_CASING,  // West
                UV_CONSOLE_CASING,  // East
                UV_CONSOLE_CASING,  // Up
                UV_CONSOLE_CASING   // Down
        ));
    }

    public void render(PoseStack poseStack, SubmitNodeCollector collector, int light) {
        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, consumer) -> {
            Matrix4f mat = pose.pose();
            for (Cuboid c : staticCuboids) {
                emitCuboid(pose, mat, consumer, c, light, OverlayTexture.NO_OVERLAY);
            }
        });
    }

    private static void emitCuboid(PoseStack.Pose pose, Matrix4f mat, VertexConsumer vc, Cuboid c, int light, int overlay) {
        float x0 = c.x0() / 16.0f, y0 = c.y0() / 16.0f, z0 = c.z0() / 16.0f;
        float x1 = c.x1() / 16.0f, y1 = c.y1() / 16.0f, z1 = c.z1() / 16.0f;

        emitQuad(pose, mat, vc, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0, c.north(), 0, 0, -1, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1, c.south(), 0, 0, 1, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1, c.west(), -1, 0, 0, light, overlay);
        emitQuad(pose, mat, vc, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0, c.east(), 1, 0, 0, light, overlay);
        emitQuad(pose, mat, vc, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, c.up(), 0, 1, 0, light, overlay);
        emitQuad(pose, mat, vc, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, c.down(), 0, -1, 0, light, overlay);
    }

    private static void emitQuad(PoseStack.Pose pose, Matrix4f mat, VertexConsumer vc,
                                 float xA, float yA, float zA,
                                 float xB, float yB, float zB,
                                 float xC, float yC, float zC,
                                 float xD, float yD, float zD,
                                 UVRect uv, float nx, float ny, float nz, int light, int overlay) {
        vc.addVertex(mat, xA, yA, zA).setColor(255, 255, 255, 255).setUv(uv.u0(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xB, yB, zB).setColor(255, 255, 255, 255).setUv(uv.u0(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xC, yC, zC).setColor(255, 255, 255, 255).setUv(uv.u1(), uv.v1()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vc.addVertex(mat, xD, yD, zD).setColor(255, 255, 255, 255).setUv(uv.u1(), uv.v0()).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }
}
```

---

### Template C: `MonolithicRendererTemplate.java`
```java
package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class MonolithicRendererTemplate implements BlockEntityRenderer<MyBlockEntity, MyRenderState> {
    private final MonolithicModelTemplate model;

    public MonolithicRendererTemplate(BlockEntityRendererProvider.Context context) {
        this.model = new MonolithicModelTemplate();
    }

    @Override
    public MyRenderState createRenderState() {
        return new MyRenderState();
    }

    @Override
    public void submit(MyRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.isAssembled) return;

        poseStack.pushPose();

        // Rotate local coordinate space to match machine facing
        switch (state.facing) {
            case NORTH -> {}
            case SOUTH -> {
                poseStack.translate(1.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 180.0f);
            }
            case EAST -> {
                poseStack.translate(1.0, 0.0, 0.0);
                poseStack.rotateDegrees(Axis.YP, 270.0f);
            }
            case WEST -> {
                poseStack.translate(0.0, 0.0, 1.0);
                poseStack.rotateDegrees(Axis.YP, 90.0f);
            }
        }

        this.model.render(poseStack, submitNodeCollector, state.lightCoords);

        poseStack.popPose();
    }
}
```
