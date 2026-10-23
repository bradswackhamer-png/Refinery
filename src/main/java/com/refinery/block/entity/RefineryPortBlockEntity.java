package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.RefineryPortBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class RefineryPortBlockEntity extends BlockEntity {
    private final RefineryPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public RefineryPortBlockEntity(BlockPos pos, BlockState blockState, RefineryPortBlock.PortType portType) {
        super(RefineryMod.REFINERY_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public RefineryPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof RefineryPortBlock portBlock
                ? portBlock.getPortType()
                : RefineryPortBlock.PortType.CRUDE_OIL_IN);
    }

    public RefineryPortBlock.PortType getPortType() {
        return this.portType;
    }

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
    }

    @Nullable
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    @Nullable
    public RefineryControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof RefineryControllerBlockEntity controllerBE) {
                if (controllerBE.isAssembled()) {
                    return controllerBE;
                }
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.putLong("controllerPos", this.controllerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getLong("controllerPos").ifPresent(val -> this.controllerPos = BlockPos.of(val));
    }
}
