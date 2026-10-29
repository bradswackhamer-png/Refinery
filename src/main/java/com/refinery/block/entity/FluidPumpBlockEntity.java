package com.refinery.block.entity;

import com.refinery.RefineryMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Legacy FluidPumpBlockEntity, defaults to Coal Fluid Pump behavior for backward compatibility.
 */
public class FluidPumpBlockEntity extends CoalFluidPumpBlockEntity {
    public FluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.FLUID_PUMP_BE.get(), pos, blockState);
    }
}
