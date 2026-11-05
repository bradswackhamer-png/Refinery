package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.RefineryPortBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RefineryPortBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int[] BYPRODUCT_SLOTS = new int[]{2, 3, 4, 5, 1};
    public static final int[] CRUDE_INPUT_SLOTS = new int[]{0};
    private static final int[] EMPTY_SLOTS = new int[0];

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
        if (this.level != null) {
            for (BlockPos p : BlockPos.betweenClosed(this.worldPosition.offset(-3, -9, -3), this.worldPosition.offset(3, 2, 3))) {
                if (this.level.getBlockEntity(p) instanceof RefineryControllerBlockEntity c && c.isAssembled()) {
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
        input.getLong("controllerPos").ifPresent(val -> this.controllerPos = BlockPos.of(val));
    }

    /**
     * Periodically checks adjacent blocks below and beside the port to auto-eject solid byproducts (bitumen, etc.)
     * onto conveyors or into item pipes/containers.
     */
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        if (this.portType != RefineryPortBlock.PortType.BYPRODUCT_OUT) {
            return;
        }
        if ((level.getGameTime() + Math.abs(pos.hashCode())) % 4 != 0) {
            return;
        }
        tryExportByproducts(level, pos, state);
    }

    private void tryExportByproducts(ServerLevel level, BlockPos pos, BlockState state) {
        RefineryControllerBlockEntity controller = getController();
        if (controller == null || !controller.isAssembled()) {
            return;
        }

        // Fast-path: Check if there are any exportable items in byproduct slots
        boolean hasItems = false;
        for (int slot : BYPRODUCT_SLOTS) {
            ItemStack stack = controller.getItem(slot);
            if (!stack.isEmpty()) {
                if (slot == 1 && stack.is(Items.BUCKET)) continue;
                hasItems = true;
                break;
            }
        }
        if (!hasItems) return;

        Direction facing = state.hasProperty(RefineryPortBlock.FACING)
                ? state.getValue(RefineryPortBlock.FACING)
                : Direction.NORTH;

        List<BlockPos> targetPositions = new ArrayList<>();
        // 1. Directly below the port
        targetPositions.add(pos.below());

        // 2. In front of port (facing direction) and front-below
        targetPositions.add(pos.relative(facing));
        targetPositions.add(pos.relative(facing).below());

        // 3. Other horizontal sides and sides-below
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir != facing) {
                targetPositions.add(pos.relative(dir));
                targetPositions.add(pos.relative(dir).below());
            }
        }

        for (BlockPos targetPos : targetPositions) {
            if (controller.isMultiblockPart(targetPos)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            BlockEntity targetBE = level.getBlockEntity(targetPos);

            if (targetBE instanceof RefineryPortBlockEntity
                    || targetBE instanceof RefineryControllerBlockEntity
                    || targetBE instanceof IRefineryFurnace) {
                continue;
            }

            boolean isConveyor = ConveyorBeltBlock.isAnyConveyorBlock(targetState)
                    || targetBE instanceof ConveyorBeltBlockEntity
                    || targetBE instanceof RightAngleConveyorBeltBlockEntity
                    || targetBE instanceof SlopedConveyorBeltBlockEntity;

            if (isConveyor) {
                Direction conveyorForward = null;
                if (targetState.getBlock() instanceof ConveyorBeltBlock) {
                    conveyorForward = targetState.getValue(ConveyorBeltBlock.FACING);
                } else if (targetState.getBlock() instanceof RightAngleConveyorBeltBlock) {
                    conveyorForward = RightAngleConveyorBeltBlock.getExitDirection(targetState);
                } else if (targetState.getBlock() instanceof SlopedConveyorBeltBlock) {
                    conveyorForward = targetState.getValue(SlopedConveyorBeltBlock.FACING);
                } else if (targetState.hasProperty(HorizontalDirectionalBlock.FACING)) {
                    conveyorForward = targetState.getValue(HorizontalDirectionalBlock.FACING);
                }

                // If conveyor moves directly towards this port block, don't eject into it
                if (conveyorForward != null && targetPos.relative(conveyorForward).equals(pos)) {
                    continue;
                }

                // Check if the conveyor belt surface is already carrying an item
                AABB beltBox = new AABB(
                        targetPos.getX() + 0.1, targetPos.getY() + 0.10, targetPos.getZ() + 0.1,
                        targetPos.getX() + 0.9, targetPos.getY() + 0.70, targetPos.getZ() + 0.9
                );
                List<ItemEntity> itemsOnBelt = level.getEntitiesOfClass(ItemEntity.class, beltBox);
                if (!itemsOnBelt.isEmpty()) {
                    continue;
                }

                for (int slot : BYPRODUCT_SLOTS) {
                    ItemStack sourceStack = controller.getItem(slot);
                    if (sourceStack.isEmpty()) continue;
                    if (slot == 1 && sourceStack.is(Items.BUCKET)) continue;

                    ItemStack single = sourceStack.copyWithCount(1);
                    if (targetBE instanceof Container container) {
                        container.setItem(0, single);
                    } else {
                        Direction forwardDir = conveyorForward != null ? conveyorForward : facing;
                        double spawnX = targetPos.getX() + 0.5;
                        double spawnY = targetPos.getY() + 0.35;
                        double spawnZ = targetPos.getZ() + 0.5;
                        ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, single);
                        itemEntity.setDeltaMovement(forwardDir.getStepX() * 0.05, 0.0, forwardDir.getStepZ() * 0.05);
                        itemEntity.setPickUpDelay(10);
                        level.addFreshEntity(itemEntity);
                    }

                    sourceStack.shrink(1);
                    if (sourceStack.isEmpty()) {
                        controller.setItem(slot, ItemStack.EMPTY);
                    } else {
                        controller.setItem(slot, sourceStack);
                    }
                    controller.setChanged();
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.25f, 0.8f);
                    return;
                }
                continue;
            }

            // Direct pipe insertion
            if (targetBE instanceof IndustrialPipeBlockEntity pipeBE && pipeBE.getPipeType().handlesItems()) {
                for (int slot : BYPRODUCT_SLOTS) {
                    ItemStack sourceStack = controller.getItem(slot);
                    if (sourceStack.isEmpty()) continue;
                    if (slot == 1 && sourceStack.is(Items.BUCKET)) continue;

                    ItemStack single = sourceStack.copyWithCount(1);
                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(pipeBE, single);
                    if (remainder.isEmpty()) {
                        sourceStack.shrink(1);
                        if (sourceStack.isEmpty()) {
                            controller.setItem(slot, ItemStack.EMPTY);
                        } else {
                            controller.setItem(slot, sourceStack);
                        }
                        controller.setChanged();
                        pipeBE.setChanged();
                        return;
                    }
                }
                continue;
            }

            // Standard Container (chests, barrels, hoppers, crates, machines)
            if (targetBE instanceof Container targetContainer) {
                for (int slot : BYPRODUCT_SLOTS) {
                    ItemStack sourceStack = controller.getItem(slot);
                    if (sourceStack.isEmpty()) continue;
                    if (slot == 1 && sourceStack.is(Items.BUCKET)) continue;

                    ItemStack single = sourceStack.copyWithCount(1);
                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(targetContainer, single);
                    if (remainder.isEmpty()) {
                        sourceStack.shrink(1);
                        if (sourceStack.isEmpty()) {
                            controller.setItem(slot, ItemStack.EMPTY);
                        } else {
                            controller.setItem(slot, sourceStack);
                        }
                        controller.setChanged();
                        targetContainer.setChanged();
                        return;
                    }
                }
            } else {
                Direction insertSide = Direction.UP;
                var handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, insertSide);
                if (handler != null) {
                    for (int slot : BYPRODUCT_SLOTS) {
                        ItemStack sourceStack = controller.getItem(slot);
                        if (sourceStack.isEmpty()) continue;
                        if (slot == 1 && sourceStack.is(Items.BUCKET)) continue;

                        ItemStack single = sourceStack.copyWithCount(1);
                        try (Transaction tx = Transaction.openRoot()) {
                            int inserted = handler.insert(ItemResource.of(single), 1, tx);
                            if (inserted > 0) {
                                tx.commit();
                                sourceStack.shrink(inserted);
                                if (sourceStack.isEmpty()) {
                                    controller.setItem(slot, ItemStack.EMPTY);
                                } else {
                                    controller.setItem(slot, sourceStack);
                                }
                                controller.setChanged();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    // === Container & WorldlyContainer Delegation ===

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (this.portType == RefineryPortBlock.PortType.BYPRODUCT_OUT || this.portType == RefineryPortBlock.PortType.TAR_OUT) {
            return BYPRODUCT_SLOTS;
        } else if (this.portType == RefineryPortBlock.PortType.CRUDE_OIL_IN) {
            return CRUDE_INPUT_SLOTS;
        }
        return EMPTY_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (this.portType != RefineryPortBlock.PortType.CRUDE_OIL_IN) return false;
        RefineryControllerBlockEntity controller = getController();
        return controller != null && controller.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (this.portType != RefineryPortBlock.PortType.BYPRODUCT_OUT && this.portType != RefineryPortBlock.PortType.TAR_OUT) {
            return false;
        }
        return slot >= 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (this.portType != RefineryPortBlock.PortType.CRUDE_OIL_IN) return false;
        RefineryControllerBlockEntity controller = getController();
        return controller != null && controller.canPlaceItem(slot, stack);
    }

    @Override
    public int getContainerSize() {
        RefineryControllerBlockEntity controller = getController();
        return controller != null ? controller.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        RefineryControllerBlockEntity controller = getController();
        return controller == null || controller.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        RefineryControllerBlockEntity controller = getController();
        return controller != null ? controller.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        RefineryControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        RefineryControllerBlockEntity controller = getController();
        return controller != null ? controller.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        RefineryControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        RefineryControllerBlockEntity controller = getController();
        return controller != null && controller.stillValid(player);
    }

    @Override
    public void clearContent() {
        RefineryControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.clearContent();
        }
    }
}
