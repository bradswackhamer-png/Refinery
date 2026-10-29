package com.refinery.block;

import com.refinery.RefineryMod;
import com.refinery.block.entity.AbstractFluidPumpBlockEntity;
import com.refinery.block.entity.ElectricFluidPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ElectricFluidPumpBlock extends AbstractFluidPumpBlock {
    public ElectricFluidPumpBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricFluidPumpBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends AbstractFluidPumpBlockEntity> getExpectedBlockEntityType() {
        return RefineryMod.ELECTRIC_FLUID_PUMP_BE.get();
    }
}
