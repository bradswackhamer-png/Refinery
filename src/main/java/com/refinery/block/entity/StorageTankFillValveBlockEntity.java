package com.refinery.block.entity;

import com.refinery.RefineryMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

public class StorageTankFillValveBlockEntity extends BlockEntity {
    private @Nullable BlockPos controllerPos;

    private final ResourceHandler<FluidResource> inputHandler = new ResourceHandler<>() {
        @Override
        public int size() {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().size() : 0;
        }

        @Override
        public FluidResource getResource(int index) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().getResource(index) : FluidResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().getAmountAsLong(index) : 0L;
        }

        @Override
        public int getAmountAsInt(int index) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().getAmountAsInt(index) : 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().getCapacityAsLong(index, resource) : 0L;
        }

        @Override
        public int getCapacityAsInt(int index, FluidResource resource) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().getCapacityAsInt(index, resource) : 0;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null && master.getFluidTank().isValid(index, resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            StorageTankBlockEntity master = getMasterTank();
            return master != null ? master.getFluidTank().insert(index, resource, amount, transaction) : 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            // Fill valve is input-only!
            return 0;
        }
    };

    public StorageTankFillValveBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.STORAGE_TANK_FILL_VALVE_BE.get(), pos, blockState);
    }

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
    }

    public @Nullable BlockPos getControllerPos() {
        return this.controllerPos;
    }

    public @Nullable StorageTankBlockEntity getMasterTank() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof StorageTankBlockEntity master && master.isAssembled()) {
                return master;
            }
        }
        if (this.level != null) {
            // Search straight downward up to 10 blocks
            for (int dy = 1; dy <= 10; dy++) {
                BlockPos p = this.worldPosition.below(dy);
                if (this.level.getBlockEntity(p) instanceof StorageTankBlockEntity master && master.isAssembled()) {
                    this.controllerPos = p;
                    setChanged();
                    return master;
                }
            }
        }
        return null;
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.inputHandler;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.putInt("controllerX", this.controllerPos.getX());
            output.putInt("controllerY", this.controllerPos.getY());
            output.putInt("controllerZ", this.controllerPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getInt("controllerX").isPresent()) {
            this.controllerPos = new BlockPos(
                    input.getIntOr("controllerX", 0),
                    input.getIntOr("controllerY", 0),
                    input.getIntOr("controllerZ", 0)
            );
        }
    }
}
