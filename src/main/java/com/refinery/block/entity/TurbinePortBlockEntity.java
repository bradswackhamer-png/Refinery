package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.TurbinePortBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;

public class TurbinePortBlockEntity extends BlockEntity {
    private final TurbinePortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public TurbinePortBlockEntity(BlockPos pos, BlockState blockState, TurbinePortBlock.PortType portType) {
        super(RefineryMod.TURBINE_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public TurbinePortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof TurbinePortBlock portBlock
                ? portBlock.getPortType()
                : TurbinePortBlock.PortType.STEAM_IN);
    }

    public TurbinePortBlock.PortType getPortType() {
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
    public ITurbineController getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof ITurbineController controller) {
                if (controller.isAssembled()) {
                    return controller;
                }
            }
        }
        return null;
    }

    @Nullable
    public EnergyHandler getEnergyHandler() {
        if (this.portType != TurbinePortBlock.PortType.ENERGY_OUT) return null;
        ITurbineController controller = getController();
        return controller != null ? controller.getEnergyHandler() : null;
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
        this.controllerPos = input.getLong("controllerPos").map(BlockPos::of).orElse(null);
    }
}
