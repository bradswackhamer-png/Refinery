package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.FluidPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Legacy block kept for backwards compatibility.
 */
public class FluidPumpBlock extends AbstractFluidPumpBlock {
    public FluidPumpBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPumpBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends AbstractFluidPumpBlockEntity> getExpectedBlockEntityType() {
        return RefineryMod.FLUID_PUMP_BE.get();
    }
}
