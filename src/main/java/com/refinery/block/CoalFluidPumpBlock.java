package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.CoalFluidPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CoalFluidPumpBlock extends AbstractFluidPumpBlock {
    public CoalFluidPumpBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoalFluidPumpBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends AbstractFluidPumpBlockEntity> getExpectedBlockEntityType() {
        return RefineryMod.COAL_FLUID_PUMP_BE.get();
    }
}
