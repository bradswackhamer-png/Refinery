package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BoilerPortBlock;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class BoilerPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final BoilerPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public BoilerPortBlockEntity(BlockPos pos, BlockState blockState, BoilerPortBlock.PortType portType) {
        super(RefineryMod.BOILER_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public BoilerPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof BoilerPortBlock portBlock
                ? portBlock.getPortType()
                : BoilerPortBlock.PortType.WATER_IN);
    }

    public BoilerPortBlock.PortType getPortType() {
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
    public IBoilerController getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof IBoilerController boiler) {
                if (boiler.isAssembled()) {
                    return boiler;
                }
            }
        }
        if (this.level != null) {
            for (BlockPos p : BlockPos.betweenClosed(this.worldPosition.offset(-3, -3, -3), this.worldPosition.offset(3, 3, 3))) {
                if (this.level.getBlockEntity(p) instanceof IBoilerController b && b.isAssembled()) {
                    this.controllerPos = p.immutable();
                    setChanged();
                    return b;
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
        this.controllerPos = input.getLong("controllerPos").map(BlockPos::of).orElse(null);
    }

    /**
     * Auto-pushes steam into adjacent fluid handlers or regular liquid pipes!
     * When attaching a regular fluid pipe adjacent to the STEAM_OUT port,
     * steam flows directly into the pipe without requiring an extract pipe.
     */
    public void tick(ServerLevel level, BlockPos pos, BlockState state) {
        if (this.portType != BoilerPortBlock.PortType.STEAM_OUT) return;
        IBoilerController controller = getController();
        if (controller == null || !controller.isAssembled() || controller.getSteamAmount() <= 0) return;

        FluidResource resource = FluidResource.of(RefineryFluids.STEAM_SOURCE.get());

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockEntity targetBE = level.getBlockEntity(target);
            if (targetBE instanceof BoilerPortBlockEntity portBE && this.controllerPos != null && this.controllerPos.equals(portBE.getControllerPos())) {
                continue;
            }
            if (targetBE instanceof IBoilerController boiler && this.controllerPos != null && this.controllerPos.equals(boiler.getPosition())) {
                continue;
            }

            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(controller.getSteamAmount(), 1000);
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
                        controller.drainSteam(inserted);
                        setChanged();
                        if (controller.getSteamAmount() <= 0) break;
                    }
                }
            }
        }
    }

    // === Container & WorldlyContainer Delegation (for FUEL_IN ports) ===
    private static final int[] SLOTS = new int[]{0, 1, 2, 3};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return this.portType == BoilerPortBlock.PortType.FUEL_IN ? SLOTS : new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (this.portType != BoilerPortBlock.PortType.FUEL_IN) return false;
        IBoilerController controller = getController();
        return controller != null && controller.canInsertFuel(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public int getContainerSize() {
        IBoilerController controller = getController();
        return controller != null ? controller.getFuelContainer().getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        IBoilerController controller = getController();
        return controller == null || controller.getFuelContainer().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        IBoilerController controller = getController();
        return controller != null ? controller.getFuelContainer().getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        IBoilerController controller = getController();
        return controller != null ? controller.getFuelContainer().removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        IBoilerController controller = getController();
        return controller != null ? controller.getFuelContainer().removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        IBoilerController controller = getController();
        if (controller != null) {
            controller.getFuelContainer().setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        IBoilerController controller = getController();
        return controller != null && controller.getFuelContainer().stillValid(player);
    }

    @Override
    public void clearContent() {
        IBoilerController controller = getController();
        if (controller != null) {
            controller.getFuelContainer().clearContent();
        }
    }
}
