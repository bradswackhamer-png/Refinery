package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.SortingConveyorBeltBlock;
import com.refinery.menu.SortingConveyorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ItemStackWithSlot;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortingConveyorBeltBlockEntity extends BlockEntity implements MenuProvider {

    public static final int FILTER_SLOTS = 27;

    private final SimpleContainer filters = new SimpleContainer(FILTER_SLOTS) {
        @Override
        public void setChanged() {
            super.setChanged();
            SortingConveyorBeltBlockEntity.this.setChanged();
        }
    };

    // Dedicated input sink for hopper / pipe item insertion onto the belt
    private final Container inputContainer = new Container() {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!stack.isEmpty() && level != null && !level.isClientSide()) {
                Direction facing = getBlockState().hasProperty(SortingConveyorBeltBlock.FACING)
                        ? getBlockState().getValue(SortingConveyorBeltBlock.FACING)
                        : Direction.NORTH;
                double spawnX = worldPosition.getX() + 0.5;
                double spawnY = worldPosition.getY() + 0.35;
                double spawnZ = worldPosition.getZ() + 0.5;

                ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, stack.copy());
                itemEntity.setDeltaMovement(facing.getStepX() * 0.05, 0.0, facing.getStepZ() * 0.05);
                itemEntity.setPickUpDelay(10);
                level.addFreshEntity(itemEntity);
            }
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return true;
        }
    };

    private final Set<Integer> divertedEntityIds = new HashSet<>();
    private long lastDivertedTick = 0;
    private Direction lastDivertedDir = null;

    public SortingConveyorBeltBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.SORTING_CONVEYOR_BELT_BE.get(), pos, blockState);
    }

    public SimpleContainer getFilters() {
        return filters;
    }

    public Container getInputContainer() {
        return inputContainer;
    }

    public long getLastDivertedTick() {
        return lastDivertedTick;
    }

    public Direction getLastDivertedDir() {
        return lastDivertedDir;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.refinery.sorting_conveyor_belt");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SortingConveyorMenu(containerId, playerInventory, this.filters);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null && !level.isClientSide()) {
            Containers.dropContents(level, pos, this.filters);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SortingConveyorBeltBlockEntity be) {
        boolean powered = state.hasProperty(SortingConveyorBeltBlock.POWERED) && state.getValue(SortingConveyorBeltBlock.POWERED);
        if (powered) {
            return;
        }

        Direction facing = state.hasProperty(SortingConveyorBeltBlock.FACING)
                ? state.getValue(SortingConveyorBeltBlock.FACING)
                : Direction.NORTH;

        double beltSpeed = 0.05; // standard ~1 block per second
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        // Query traveling items on the belt surface
        AABB beltBounds = new AABB(
                pos.getX() - 0.15, pos.getY() + 0.05, pos.getZ() - 0.15,
                pos.getX() + 1.15, pos.getY() + 0.65, pos.getZ() + 1.15
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, beltBounds);

        // Periodically purge old entity IDs
        if (!items.isEmpty()) {
            be.divertedEntityIds.removeIf(id -> level.getEntity(id) == null);
        }

        for (ItemEntity itemEntity : items) {
            // Keep traveling items alive during factory transit
            if (!level.isClientSide() && itemEntity.getAge() > 4000) {
                itemEntity.setUnlimitedLifetime();
            }

            // Height lock onto belt surface (Y = pos.getY() + 0.3125)
            double beltSurfaceY = pos.getY() + 0.3125;
            if (itemEntity.getY() < beltSurfaceY && itemEntity.getY() >= pos.getY() - 0.15) {
                double diff = beltSurfaceY - itemEntity.getY();
                if (diff > 0.001) {
                    itemEntity.setPos(itemEntity.getX(), beltSurfaceY, itemEntity.getZ());
                }
            } else if (itemEntity.getY() > beltSurfaceY && itemEntity.getY() <= pos.getY() + 0.45) {
                double diff = itemEntity.getY() - beltSurfaceY;
                if (diff > 0.001 && diff < 0.15) {
                    itemEntity.setPos(itemEntity.getX(), beltSurfaceY, itemEntity.getZ());
                }
            }
            itemEntity.setOnGround(true);

            // Phase 1 vs Phase 2: has the item reached or passed the center sorting bridge?
            boolean pastCenter = switch (facing) {
                case NORTH -> itemEntity.getZ() <= centerZ;
                case SOUTH -> itemEntity.getZ() >= centerZ;
                case WEST  -> itemEntity.getX() <= centerX;
                case EAST  -> itemEntity.getX() >= centerX;
                default    -> false;
            };

            Direction targetDir = pastCenter
                    ? be.getTargetDirection(itemEntity.getItem(), facing)
                    : facing;

            // If target destination is an adjacent conveyor, release ownership once crossed block boundary
            if (pastCenter) {
                BlockPos targetPos = pos.relative(targetDir);
                BlockState targetState = level.getBlockState(targetPos);
                boolean targetIsConveyor = ConveyorBeltBlock.isAnyConveyorBlock(targetState) ||
                                           ConveyorBeltBlock.isAnyConveyorBlock(level.getBlockState(targetPos.below()));
                if (targetIsConveyor) {
                    boolean crossedBoundary = switch (targetDir) {
                        case NORTH -> itemEntity.getZ() <= pos.getZ();
                        case SOUTH -> itemEntity.getZ() >= pos.getZ() + 1.0;
                        case WEST  -> itemEntity.getX() <= pos.getX();
                        case EAST  -> itemEntity.getX() >= pos.getX() + 1.0;
                        default    -> false;
                    };
                    if (crossedBoundary) {
                        continue;
                    }
                }
            }

            // Physical backstop: prevent items entering from slipping backward off the tail
            if (!state.hasProperty(SortingConveyorBeltBlock.CONNECTED_BACK) || !state.getValue(SortingConveyorBeltBlock.CONNECTED_BACK)) {
                switch (facing) {
                    case SOUTH -> {
                        if (itemEntity.getZ() < pos.getZ() + 0.15) {
                            itemEntity.setPos(itemEntity.getX(), itemEntity.getY(), pos.getZ() + 0.15);
                            if (itemEntity.getDeltaMovement().z < 0) {
                                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().x, itemEntity.getDeltaMovement().y, 0.0);
                            }
                        }
                    }
                    case NORTH -> {
                        if (itemEntity.getZ() > pos.getZ() + 0.85) {
                            itemEntity.setPos(itemEntity.getX(), itemEntity.getY(), pos.getZ() + 0.85);
                            if (itemEntity.getDeltaMovement().z > 0) {
                                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().x, itemEntity.getDeltaMovement().y, 0.0);
                            }
                        }
                    }
                    case EAST -> {
                        if (itemEntity.getX() < pos.getX() + 0.15) {
                            itemEntity.setPos(pos.getX() + 0.15, itemEntity.getY(), itemEntity.getZ());
                            if (itemEntity.getDeltaMovement().x < 0) {
                                itemEntity.setDeltaMovement(0.0, itemEntity.getDeltaMovement().y, itemEntity.getDeltaMovement().z);
                            }
                        }
                    }
                    case WEST -> {
                        if (itemEntity.getX() > pos.getX() + 0.85) {
                            itemEntity.setPos(pos.getX() + 0.85, itemEntity.getY(), itemEntity.getZ());
                            if (itemEntity.getDeltaMovement().x > 0) {
                                itemEntity.setDeltaMovement(0.0, itemEntity.getDeltaMovement().y, itemEntity.getDeltaMovement().z);
                            }
                        }
                    }
                }
            }

            double moveX;
            double moveZ;

            if (!pastCenter) {
                // Phase 1: Incoming travel along `facing` toward center, centering perpendicular
                moveX = facing.getStepX() * beltSpeed;
                moveZ = facing.getStepZ() * beltSpeed;
                if (facing.getAxis() == Direction.Axis.Z) {
                    moveX += (centerX - itemEntity.getX()) * 0.15;
                } else {
                    moveZ += (centerZ - itemEntity.getZ()) * 0.15;
                }
            } else {
                // Phase 2: Vector travel along `targetDir` toward the designated exit
                BlockPos exitPos = pos.relative(targetDir);
                BlockState exitState = level.getBlockState(exitPos);
                boolean exitIsConveyor = ConveyorBeltBlock.isAnyConveyorBlock(exitState) ||
                                         ConveyorBeltBlock.isAnyConveyorBlock(level.getBlockState(exitPos.below()));

                boolean nearExit = switch (targetDir) {
                    case NORTH -> itemEntity.getZ() <= pos.getZ() + 0.18;
                    case SOUTH -> itemEntity.getZ() >= pos.getZ() + 0.82;
                    case WEST  -> itemEntity.getX() <= pos.getX() + 0.18;
                    case EAST  -> itemEntity.getX() >= pos.getX() + 0.82;
                    default    -> false;
                };

                double exitSpeed = (nearExit && exitIsConveyor) ? beltSpeed * 1.3 : beltSpeed;
                moveX = targetDir.getStepX() * exitSpeed;
                moveZ = targetDir.getStepZ() * exitSpeed;

                // Centering along axis perpendicular to targetDir
                if (targetDir.getAxis() == Direction.Axis.Z) {
                    moveX += (centerX - itemEntity.getX()) * 0.15;
                } else {
                    moveZ += (centerZ - itemEntity.getZ()) * 0.15;
                }

                // Audio cue when diverting to the side
                if (!level.isClientSide() && targetDir != facing && !be.divertedEntityIds.contains(itemEntity.getId())) {
                    be.divertedEntityIds.add(itemEntity.getId());
                    be.lastDivertedTick = level.getGameTime();
                    be.lastDivertedDir = targetDir;
                    level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.25f, 1.8f);
                }
            }

            itemEntity.setDeltaMovement(moveX, 0.0, moveZ);

            if (!level.isClientSide() && pastCenter) {
                checkInventoryInsertion(level, pos, targetDir, itemEntity);
            }
        }
    }

    public Direction getTargetDirection(ItemStack stack, Direction facing) {
        if (stack.isEmpty()) return facing;

        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();

        // 1. Check Left Filter Bank (slots 0..8)
        for (int i = 0; i < 9; i++) {
            ItemStack filter = this.filters.getItem(i);
            if (!filter.isEmpty() && ItemStack.isSameItemSameComponents(filter, stack)) {
                return leftDir;
            }
        }

        // 2. Check Right Filter Bank (slots 18..26)
        for (int i = 18; i < 27; i++) {
            ItemStack filter = this.filters.getItem(i);
            if (!filter.isEmpty() && ItemStack.isSameItemSameComponents(filter, stack)) {
                return rightDir;
            }
        }

        // 3. Check Straight Filter Bank (slots 9..17)
        for (int i = 9; i < 18; i++) {
            ItemStack filter = this.filters.getItem(i);
            if (!filter.isEmpty() && ItemStack.isSameItemSameComponents(filter, stack)) {
                return facing;
            }
        }

        // 4. Default Pass-Through / Overflow: Straight Ahead
        return facing;
    }

    private static void checkInventoryInsertion(Level level, BlockPos pos, Direction exitDir, ItemEntity itemEntity) {
        boolean nearExitEdge = switch (exitDir) {
            case NORTH -> itemEntity.getZ() <= pos.getZ() + 0.18;
            case SOUTH -> itemEntity.getZ() >= pos.getZ() + 0.82;
            case WEST  -> itemEntity.getX() <= pos.getX() + 0.18;
            case EAST  -> itemEntity.getX() >= pos.getX() + 0.82;
            default    -> false;
        };

        if (!nearExitEdge) return;

        BlockPos exitPos = pos.relative(exitDir);
        BlockState exitState = level.getBlockState(exitPos);
        if (ConveyorBeltBlock.isAnyConveyorBlock(exitState) ||
            ConveyorBeltBlock.isAnyConveyorBlock(level.getBlockState(exitPos.below()))) {
            return;
        }

        ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, exitPos, exitDir.getOpposite());
        if (targetHandler != null) {
            ItemStack original = itemEntity.getItem();
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = targetHandler.insert(ItemResource.of(original), original.getCount(), tx);
                if (inserted > 0) {
                    tx.commit();
                    if (inserted >= original.getCount()) {
                        itemEntity.discard();
                    } else {
                        original.shrink(inserted);
                        itemEntity.setItem(original);
                    }
                }
            }
        }
    }

    // === Serialization ===

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        NonNullList<ItemStack> items = this.filters.getItems();
        var filterList = output.list("Filters", ItemStackWithSlot.CODEC);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                filterList.add(new ItemStackWithSlot(i, stack));
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        NonNullList<ItemStack> items = this.filters.getItems();
        while (items.size() < FILTER_SLOTS) {
            items.add(ItemStack.EMPTY);
        }
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }

        var filterList = input.listOrEmpty("Filters", ItemStackWithSlot.CODEC);
        boolean loaded = false;
        for (ItemStackWithSlot entry : filterList) {
            if (entry.isValidInContainer(items.size())) {
                items.set(entry.slot(), entry.stack());
                loaded = true;
            }
        }
        if (!loaded) {
            ContainerHelper.loadAllItems(input, items);
        }
    }
}
