package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.TurbinePortBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
        if (this.level != null) {
            for (BlockPos p : BlockPos.betweenClosed(this.worldPosition.offset(-3, -3, -3), this.worldPosition.offset(3, 3, 3))) {
                if (this.level.getBlockEntity(p) instanceof ITurbineController t && t.isAssembled()) {
                    this.controllerPos = p.immutable();
                    setChanged();
                    return t;
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

    /**
     * Auto-pushes condensed water into adjacent fluid handlers or regular liquid pipes!
     * When attaching a regular fluid pipe adjacent to the WATER_OUT port,
     * water flows directly into the pipe without requiring an extract pipe.
     */
    public void tick(ServerLevel level, BlockPos pos, BlockState state) {
        if (this.portType != TurbinePortBlock.PortType.WATER_OUT) return;
        ITurbineController controller = getController();
        if (controller == null || !controller.isAssembled() || controller.getWaterAmount() <= 0) return;

        FluidResource resource = FluidResource.of(Fluids.WATER);

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockEntity targetBE = level.getBlockEntity(target);
            if (targetBE instanceof TurbinePortBlockEntity portBE && this.controllerPos != null && this.controllerPos.equals(portBE.getControllerPos())) {
                continue;
            }
            if (targetBE instanceof ITurbineController turbine && this.controllerPos != null && this.controllerPos.equals(turbine.getPosition())) {
                continue;
            }

            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(controller.getWaterAmount(), 1000);
                int inserted = 0;
                try (Transaction tx = Transaction.openRoot()) {
                    for (int i = 0; i < handler.size(); i++) {
                        int space = toPush - inserted;
                        if (space <= 0) break;
                        int ins = handler.insert(i, resource, space, tx);
                        inserted += ins;
                    }
                    if (inserted > 0) {
                        tx.commit();
                        controller.drainWater(inserted);
                        setChanged();
                        if (controller.getWaterAmount() <= 0) break;
                    }
                }
            }
        }
    }
}
