package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.RefineryPortBlock;
import com.refinery.block.ReinforcedCasingBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.fluid.RefineryFluids;
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
import net.neoforged.neoforge.transfer.fluid.FluidResource;
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
                if (!controllerBE.isAssembled()) {
                    controllerBE.syncAssembly();
                }
                if (controllerBE.isAssembled()) {
                    return controllerBE;
                }
            }
        }
        if (this.level != null) {
            RefineryControllerBlockEntity nearby = ReinforcedCasingBlock.findNearbyController(this.level, this.worldPosition);
            if (nearby != null) {
                if (!nearby.isAssembled()) {
                    nearby.syncAssembly();
                }
                if (nearby.isAssembled()) {
                    this.controllerPos = nearby.getBlockPos();
                    setChanged();
                    return nearby;
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
     * Periodically checks adjacent blocks:
     * 1. Ejects solid byproducts (bitumen, sand) to conveyors or item pipes.
     * 2. Auto-pushes liquid outputs (Liquid Tar, Diesel, Gasoline) into connected fluid pipes or tanks.
     */
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        // Auto-eject solid byproducts (bitumen, sand) to conveyors / item pipes
        if (this.portType == RefineryPortBlock.PortType.BYPRODUCT_OUT) {
            if ((level.getGameTime() + Math.abs(pos.hashCode())) % 4 == 0) {
                tryExportByproducts(level, pos, state);
            }
        }

        // Auto-push liquids into adjacent fluid pipes, tanks, or fluid handlers
        if (this.portType == RefineryPortBlock.PortType.TAR_OUT
                || this.portType == RefineryPortBlock.PortType.DIESEL_OUT
                || this.portType == RefineryPortBlock.PortType.GAS_OUT
                || this.portType == RefineryPortBlock.PortType.BYPRODUCT_OUT) {
            tryAutoPushFluids(level, pos, state);
        }
    }

    /**
     * Auto-pushes output liquids (Liquid Tar, Diesel, Gasoline) into adjacent fluid handlers or pipes!
     * When attaching a regular fluid pipe or storage tank adjacent to an output port,
     * the fluid flows directly into the pipe without requiring an external extractor servo.
     */
    private void tryAutoPushFluids(ServerLevel level, BlockPos pos, BlockState state) {
        RefineryControllerBlockEntity controller = getController();
        if (controller == null || !controller.isAssembled()) return;

        FluidResource resource;
        int available;

        switch (this.portType) {
            case DIESEL_OUT -> {
                available = controller.getDiesel();
                resource = FluidResource.of(RefineryFluids.DIESEL_SOURCE.get());
            }
            case GAS_OUT -> {
                available = controller.getGasoline();
                resource = FluidResource.of(RefineryFluids.GASOLINE_SOURCE.get());
            }
            case TAR_OUT, BYPRODUCT_OUT -> {
                available = controller.getLiquidTar();
                resource = FluidResource.of(RefineryFluids.LIQUID_TAR_SOURCE.get());
            }
            default -> {
                return;
            }
        }

        if (available <= 0) return;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockEntity targetBE = level.getBlockEntity(target);

            // Don't push into other ports of the same refinery
            if (targetBE instanceof RefineryPortBlockEntity portBE && this.controllerPos != null && this.controllerPos.equals(portBE.getControllerPos())) {
                continue;
            }
            if (targetBE instanceof RefineryControllerBlockEntity controllerBE && this.controllerPos != null && this.controllerPos.equals(controllerBE.getBlockPos())) {
                continue;
            }

            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler == null) {
                handler = level.getCapability(Capabilities.Fluid.BLOCK, target, null);
            }
            if (handler != null) {
                int toPush = Math.min(available, 1000);
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
                        switch (this.portType) {
                            case DIESEL_OUT -> controller.drainDiesel(inserted);
                            case GAS_OUT -> controller.drainGasoline(inserted);
                            case TAR_OUT, BYPRODUCT_OUT -> controller.drainLiquidTar(inserted);
                        }
                        controller.setChanged();
                        setChanged();
                        available -= inserted;
                        if (available <= 0) break;
                    }
                }
            }
        }
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
