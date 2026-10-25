package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.SmelterPortBlock;
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

public class SmelterPortBlockEntity extends BlockEntity implements WorldlyContainer {
    private final SmelterPortBlock.PortType portType;
    private @Nullable BlockPos controllerPos;

    public SmelterPortBlockEntity(BlockPos pos, BlockState blockState, SmelterPortBlock.PortType portType) {
        super(RefineryMod.SMELTER_PORT_BE.get(), pos, blockState);
        this.portType = portType;
    }

    public SmelterPortBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, blockState.getBlock() instanceof SmelterPortBlock portBlock
                ? portBlock.getPortType()
                : SmelterPortBlock.PortType.INPUT);
    }

    public SmelterPortBlock.PortType getPortType() {
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
    public MultiSmelterControllerBlockEntity getController() {
        if (this.level != null && this.controllerPos != null && this.level.isLoaded(this.controllerPos)) {
            if (this.level.getBlockEntity(this.controllerPos) instanceof MultiSmelterControllerBlockEntity controllerBE) {
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
            output.putInt("ControllerX", this.controllerPos.getX());
            output.putInt("ControllerY", this.controllerPos.getY());
            output.putInt("ControllerZ", this.controllerPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int cx = input.getIntOr("ControllerX", Integer.MIN_VALUE);
        int cy = input.getIntOr("ControllerY", Integer.MIN_VALUE);
        int cz = input.getIntOr("ControllerZ", Integer.MIN_VALUE);
        if (cx != Integer.MIN_VALUE) {
            this.controllerPos = new BlockPos(cx, cy, cz);
        }
    }

    // WorldlyContainer forwarding to controller
    @Override
    public int[] getSlotsForFace(Direction side) {
        MultiSmelterControllerBlockEntity controller = getController();
        if (controller == null) return new int[0];
        return switch (this.portType) {
            case INPUT -> new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            case OUTPUT -> new int[]{8, 9, 10, 11, 12, 13, 14, 15};
            case ENERGY_FUEL -> new int[]{16};
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        MultiSmelterControllerBlockEntity controller = getController();
        if (controller == null) return false;
        if (this.portType == SmelterPortBlock.PortType.INPUT && index >= 0 && index < 8) {
            return controller.canSmelt(stack);
        }
        if (this.portType == SmelterPortBlock.PortType.ENERGY_FUEL && index == 16) {
            return MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return this.portType == SmelterPortBlock.PortType.OUTPUT && index >= 8 && index <= 15;
    }

    @Override
    public int getContainerSize() {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller != null ? controller.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller == null || controller.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller != null ? controller.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        MultiSmelterControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        MultiSmelterControllerBlockEntity controller = getController();
        return controller != null && controller.stillValid(player);
    }

    @Override
    public void clearContent() {
        MultiSmelterControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.clearContent();
        }
    }
}
