package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.FoundryPortBlock;
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

public class FoundryPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final FoundryPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public FoundryPortBlockEntity(BlockPos pos, BlockState blockState, FoundryPortBlock.PortType portType) {
        super(RefineryMod.FOUNDRY_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public FoundryPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof FoundryPortBlock portBlock
                ? portBlock.getPortType()
                : FoundryPortBlock.PortType.INPUT);
    }

    public FoundryPortBlock.PortType getPortType() {
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
    public BlastFurnaceControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof BlastFurnaceControllerBlockEntity controllerBE) {
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
        this.controllerPos = input.getLong("controllerPos").map(BlockPos::of).orElse(null);
    }

    // === Container / WorldlyContainer Delegation to Controller ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        BlastFurnaceControllerBlockEntity controller = getController();
        if (controller == null) return new int[0];
        return this.portType == FoundryPortBlock.PortType.INPUT
                ? BlastFurnaceControllerBlockEntity.INPUT_SLOTS
                : BlastFurnaceControllerBlockEntity.OUTPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (this.portType != FoundryPortBlock.PortType.INPUT) return false;
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null && controller.canPlaceItemThroughFace(slot, stack, side);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (this.portType != FoundryPortBlock.PortType.OUTPUT) return false;
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null && controller.canTakeItemThroughFace(slot, stack, side);
    }

    @Override
    public int getContainerSize() {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null ? controller.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller == null || controller.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null ? controller.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        BlastFurnaceControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        BlastFurnaceControllerBlockEntity controller = getController();
        return controller != null && controller.stillValid(player);
    }

    @Override
    public void clearContent() {
        BlastFurnaceControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.clearContent();
        }
    }
}
