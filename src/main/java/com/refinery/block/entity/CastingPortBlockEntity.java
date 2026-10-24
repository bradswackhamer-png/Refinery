package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CastingPortBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class CastingPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final CastingPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public CastingPortBlockEntity(BlockPos pos, BlockState blockState, CastingPortBlock.PortType portType) {
        super(RefineryMod.CASTING_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public CastingPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof CastingPortBlock portBlock
                ? portBlock.getPortType()
                : CastingPortBlock.PortType.INPUT);
    }

    public CastingPortBlock.PortType getPortType() {
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
    public CastingPressControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof CastingPressControllerBlockEntity controllerBE) {
                if (controllerBE.isAssembled()) {
                    return controllerBE;
                }
            }
        }
        if (this.level != null) {
            for (BlockPos p : BlockPos.betweenClosed(this.worldPosition.offset(-3, -3, -3), this.worldPosition.offset(3, 3, 3))) {
                if (this.level.getBlockEntity(p) instanceof CastingPressControllerBlockEntity c && c.isAssembled()) {
                    this.controllerPos = p.immutable();
                    setChanged();
                    return c;
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
        long pos = input.getLongOr("controllerPos", 0L);
        if (pos != 0L) {
            this.controllerPos = BlockPos.of(pos);
        }
    }

    // === WorldlyContainer forwarding for OUTPUT port ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        var c = getController();
        if (c != null && this.portType == CastingPortBlock.PortType.OUTPUT) {
            return c.getSlotsForFace(side);
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        var c = getController();
        return c != null && this.portType == CastingPortBlock.PortType.OUTPUT && c.canTakeItemThroughFace(slot, stack, side);
    }

    @Override
    public int getContainerSize() {
        var c = getController();
        return c != null ? c.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        var c = getController();
        return c == null || c.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        var c = getController();
        return c != null ? c.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        var c = getController();
        return c != null ? c.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        var c = getController();
        return c != null ? c.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        var c = getController();
        if (c != null) {
            c.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        var c = getController();
        if (c != null) {
            c.clearContent();
        }
    }
}
