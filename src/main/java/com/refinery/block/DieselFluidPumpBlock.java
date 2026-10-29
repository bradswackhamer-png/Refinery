package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.DieselFluidPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class DieselFluidPumpBlock extends AbstractFluidPumpBlock {
    public DieselFluidPumpBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DieselFluidPumpBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends AbstractFluidPumpBlockEntity> getExpectedBlockEntityType() {
        return RefineryMod.DIESEL_FLUID_PUMP_BE.get();
    }
}
