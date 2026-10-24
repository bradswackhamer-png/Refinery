package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.MelterPortBlock;
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

public class MelterPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final MelterPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public MelterPortBlockEntity(BlockPos pos, BlockState blockState, MelterPortBlock.PortType portType) {
        super(RefineryMod.MELTER_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public MelterPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof MelterPortBlock portBlock
                ? portBlock.getPortType()
                : MelterPortBlock.PortType.INPUT);
    }

    public MelterPortBlock.PortType getPortType() {
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
    public ArcMelterControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof ArcMelterControllerBlockEntity controllerBE) {
                if (controllerBE.isAssembled()) {
                    return controllerBE;
                }
            }
        }
        if (this.level != null) {
            for (BlockPos p : BlockPos.betweenClosed(this.worldPosition.offset(-3, -3, -3), this.worldPosition.offset(3, 3, 3))) {
                if (this.level.getBlockEntity(p) instanceof ArcMelterControllerBlockEntity c && c.isAssembled()) {
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

    // === WorldlyContainer forwarding for INPUT port ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        var c = getController();
        if (c != null && this.portType == MelterPortBlock.PortType.INPUT) {
            return c.getSlotsForFace(side);
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        var c = getController();
        return c != null && this.portType == MelterPortBlock.PortType.INPUT && c.canPlaceItemThroughFace(slot, stack, side);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
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
