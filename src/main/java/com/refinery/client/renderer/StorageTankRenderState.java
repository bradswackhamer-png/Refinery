package com.refinery.client.renderer;

import com.refinery.block.entity.StorageTankSize;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class StorageTankRenderState extends BlockEntityRenderState {
    public boolean isBlueprintActive;
    public boolean isAssembled;
    public StorageTankSize tankSize = StorageTankSize.SIZE_3X3X3;
    public StorageTankSize targetBlueprintSize = StorageTankSize.SIZE_3X3X3;
    public BlockPos controllerPos = BlockPos.ZERO;

    public record MissingComponent(
            int relX, int relY, int relZ,
            int color,
            String name
    ) {}

    public final List<MissingComponent> missingComponents = new ArrayList<>();
}
