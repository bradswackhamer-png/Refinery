package com.refinery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.refinery.block.pipe.PipeFacadeHelper;
import com.refinery.item.PipeFacadeItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PipeFacadeSpecialRenderer implements SpecialModelRenderer<PipeFacadeHelper.FacadeData> {

    public record Unbaked() implements SpecialModelRenderer.Unbaked<PipeFacadeHelper.FacadeData> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<PipeFacadeHelper.FacadeData> bake(SpecialModelRenderer.BakingContext context) {
            return new PipeFacadeSpecialRenderer();
        }
    }

    @Override
    public PipeFacadeHelper.FacadeData extractArgument(ItemStack stack) {
        Block block = PipeFacadeHelper.getFacadeBlock(stack);
        boolean hollow = stack.getItem() instanceof PipeFacadeItem item && item.isHollow();
        return new PipeFacadeHelper.FacadeData(block, hollow);
    }

    @Override
    public void submit(
            PipeFacadeHelper.FacadeData data,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            boolean hasFoil,
            int seed
    ) {
        if (data == null || data.block() == null) return;
        PipeFacadeRenderer.renderFacadeItem(data.block(), data.hollow(), poseStack, collector, light, overlay);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0f, 0.0f, 0.0f));
        consumer.accept(new Vector3f(1.0f, 1.0f, 0.125f));
    }
}
