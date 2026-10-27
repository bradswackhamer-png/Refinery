package com.refinery.client.renderer;

import com.refinery.RefineryMod;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * High-fidelity industrial 3D model of the AIDA NC1-1500 C-frame Stamping Press.
 * Replaces the 2x2x2 multiblock cubes with a unified, seamless machine when assembled.
 */
@OnlyIn(Dist.CLIENT)
public class AidaPressModel extends Model<Unit> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/entity/aida_press.png");

    private final ModelPart ram;

    public AidaPressModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.ram = root.getChild("ram");
    }

    /**
     * Displaces the stamping ram downwards towards the bolster table.
     * @param displacementPixels 0.0f at rest (top of stroke), up to 7.0f at maximum bottom dead center.
     */
    public void setRamDisplacement(float displacementPixels) {
        this.ram.y = -displacementPixels;
    }

    public static LayerDefinition createPressLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. Foundation Base & Lower Machine Pedestals (Y: 0..12)
        root.addOrReplaceChild("base_plate",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0f, 0.0f, 0.0f, 32.0f, 3.0f, 32.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("left_pedestal",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0f, 3.0f, 0.0f, 10.0f, 9.0f, 32.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_pedestal",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(22.0f, 3.0f, 0.0f, 10.0f, 9.0f, 32.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("center_bed_well",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(10.0f, 3.0f, 6.0f, 12.0f, 8.0f, 26.0f),
                PartPose.ZERO);

        // 2. Bolster Plate / Milled Steel Bed Table (Y: 11..13)
        // Projects 4 pixels forward out from the throat (z = -4 to 22)
        root.addOrReplaceChild("bolster_plate",
                CubeListBuilder.create()
                        .texOffs(0, 80)
                        .addBox(7.0f, 11.0f, -4.0f, 18.0f, 2.0f, 26.0f),
                PartPose.ZERO);

        // 3. Lower Operator Pendant & Emergency Stop Control Station
        root.addOrReplaceChild("lower_console",
                CubeListBuilder.create()
                        .texOffs(100, 108)
                        .addBox(9.0f, 6.0f, -7.0f, 14.0f, 4.0f, 3.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("lower_conduit_support",
                CubeListBuilder.create()
                        .texOffs(124, 108)
                        .addBox(15.0f, 10.0f, -6.0f, 2.0f, 1.0f, 2.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("lower_conduit_base",
                CubeListBuilder.create()
                        .texOffs(124, 108)
                        .addBox(10.0f, 2.0f, -6.0f, 2.0f, 4.0f, 2.0f),
                PartPose.ZERO);

        // 4. Heavy C-Frame Columns & Back Wall (Y: 12..24)
        root.addOrReplaceChild("left_column",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(0.0f, 12.0f, 12.0f, 10.0f, 12.0f, 20.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_column",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(22.0f, 12.0f, 12.0f, 10.0f, 12.0f, 20.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("back_wall",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(10.0f, 12.0f, 18.0f, 12.0f, 12.0f, 14.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("left_guide_rail",
                CubeListBuilder.create()
                        .texOffs(70, 80)
                        .addBox(9.0f, 13.0f, 12.0f, 2.0f, 11.0f, 6.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("right_guide_rail",
                CubeListBuilder.create()
                        .texOffs(70, 80)
                        .addBox(21.0f, 13.0f, 12.0f, 2.0f, 11.0f, 6.0f),
                PartPose.ZERO);

        // 5. Upper Crown & Drive Head (Y: 24..36) - AIDA Branding & Analog Dial
        root.addOrReplaceChild("crown_body",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(0.0f, 24.0f, 0.0f, 32.0f, 12.0f, 32.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("crown_front_bezel",
                CubeListBuilder.create()
                        .texOffs(32, 68)
                        .addBox(1.0f, 25.0f, -1.0f, 30.0f, 10.0f, 1.0f),
                PartPose.ZERO);

        // 6. Side Electrical Cabinet (Mounted on Right Column, X: 32..38)
        root.addOrReplaceChild("side_cabinet_body",
                CubeListBuilder.create()
                        .texOffs(80, 80)
                        .addBox(32.0f, 10.0f, 6.0f, 6.0f, 18.0f, 18.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("side_cabinet_vent_hood",
                CubeListBuilder.create()
                        .texOffs(80, 80)
                        .addBox(33.0f, 28.0f, 8.0f, 4.0f, 2.0f, 14.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("side_cabinet_conduit",
                CubeListBuilder.create()
                        .texOffs(124, 108)
                        .addBox(31.0f, 16.0f, 14.0f, 1.0f, 2.0f, 2.0f),
                PartPose.ZERO);

        // 7. Upper Operator Touchscreen Pendant Console
        root.addOrReplaceChild("pendant_arm",
                CubeListBuilder.create()
                        .texOffs(100, 108)
                        .addBox(22.0f, 19.0f, 2.0f, 2.0f, 2.0f, 4.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("pendant_screen_box",
                CubeListBuilder.create()
                        .texOffs(100, 108)
                        .addBox(18.0f, 17.0f, 0.0f, 6.0f, 6.0f, 3.0f),
                PartPose.ZERO);

        // 8. Animated Stamping Ram (Child part of root, displaces vertically)
        PartDefinition ramPart = root.addOrReplaceChild("ram",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // Amber / Bronze Hydraulic Cylinder Shaft
        ramPart.addOrReplaceChild("hydraulic_cylinder",
                CubeListBuilder.create()
                        .texOffs(70, 80)
                        .addBox(13.0f, 22.0f, 9.0f, 6.0f, 6.0f, 6.0f),
                PartPose.ZERO);

        // Solid White Ram Slide Body
        ramPart.addOrReplaceChild("slide_body",
                CubeListBuilder.create()
                        .texOffs(0, 108)
                        .addBox(9.0f, 16.0f, 6.0f, 14.0f, 6.0f, 12.0f),
                PartPose.ZERO);

        // Safety Yellow Die Bar
        ramPart.addOrReplaceChild("safety_yellow_bar",
                CubeListBuilder.create()
                        .texOffs(54, 108)
                        .addBox(8.0f, 14.0f, 5.0f, 16.0f, 2.0f, 14.0f),
                PartPose.ZERO);

        // Hardened Punch Tooling Die Face
        ramPart.addOrReplaceChild("punch_die",
                CubeListBuilder.create()
                        .texOffs(0, 80)
                        .addBox(11.0f, 13.0f, 8.0f, 10.0f, 1.0f, 8.0f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }
}
