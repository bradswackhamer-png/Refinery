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

@OnlyIn(Dist.CLIENT)
public class CrusherDrumModel extends Model<Unit> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RefineryMod.MODID, "textures/entity/crusher_drum.png");

    public CrusherDrumModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
    }

    public static LayerDefinition createDrumLayer(boolean alternateStagger) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. Central Hardened Axle Shaft (3x3x16 pixels in model units)
        root.addOrReplaceChild("axle",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-1.5f, -1.5f, -8.0f, 3.0f, 3.0f, 16.0f),
                PartPose.ZERO);

        // 2. Main Cylindrical Crusher Drum Core (8-sided prism via two 7x7x12 boxes at 0° and 45°)
        root.addOrReplaceChild("barrel_0",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5f, -3.5f, -6.0f, 7.0f, 7.0f, 12.0f),
                PartPose.ZERO);

        root.addOrReplaceChild("barrel_45",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5f, -3.5f, -6.0f, 7.0f, 7.0f, 12.0f),
                PartPose.rotation(0.0f, 0.0f, (float) Math.toRadians(45.0)));

        // 3. Protruding Hardened Crushing Spikes / Teeth
        // 8 angular positions around the perimeter (every 45 degrees)
        float[] ringZ = alternateStagger
                ? new float[] { -3.8f, 0.0f, 3.8f }              // Drum 1 (Left): 3 rings of teeth
                : new float[] { -5.2f, -1.9f, 1.9f, 5.2f };      // Drum 2 (Right): 4 rings of interleaved teeth

        for (int i = 0; i < 8; i++) {
            float angleRad = (float) Math.toRadians(i * 45.0);
            PartDefinition spikePart = root.addOrReplaceChild("spikes_" + i + (alternateStagger ? "_a" : "_b"),
                    CubeListBuilder.create(),
                    PartPose.rotation(0.0f, 0.0f, angleRad));

            CubeListBuilder spikesBuilder = CubeListBuilder.create().texOffs(32, 0);
            for (float z : ringZ) {
                // Each tooth is a heavy tapered rectangular wedge protruding 2.4 pixels out from the drum surface:
                spikesBuilder.addBox(-1.0f, 3.2f, z - 1.0f, 2.0f, 2.4f, 2.0f);
            }
            spikePart.addOrReplaceChild("teeth_" + i, spikesBuilder, PartPose.ZERO);
        }

        return LayerDefinition.create(mesh, 64, 64);
    }
}
