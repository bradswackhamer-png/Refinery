package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.GrinderPortBlock;
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

public class GrinderPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final GrinderPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public GrinderPortBlockEntity(BlockPos pos, BlockState blockState, GrinderPortBlock.PortType portType) {
        super(RefineryMod.GRINDER_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public GrinderPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof GrinderPortBlock portBlock
                ? portBlock.getPortType()
                : GrinderPortBlock.PortType.INPUT);
    }

    public GrinderPortBlock.PortType getPortType() {
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
    public IndustrialGrinderControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof IndustrialGrinderControllerBlockEntity controllerBE) {
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

    // === WorldlyContainer Delegation ===
    private static final int[] SLOTS_INPUT = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_OUTPUT = new int[]{4, 5, 6, 7};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return this.portType == GrinderPortBlock.PortType.INPUT ? SLOTS_INPUT : SLOTS_OUTPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (this.portType != GrinderPortBlock.PortType.INPUT) return false;
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null && controller.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return this.portType == GrinderPortBlock.PortType.OUTPUT && slot >= 4;
    }

    @Override
    public int getContainerSize() {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null ? controller.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller == null || controller.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null ? controller.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        IndustrialGrinderControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        IndustrialGrinderControllerBlockEntity controller = getController();
        return controller != null && controller.stillValid(player);
    }

    @Override
    public void clearContent() {
        IndustrialGrinderControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.clearContent();
        }
    }
}
