package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class RightAngleConveyorBeltBlockEntity extends BlockEntity implements Container {

    public RightAngleConveyorBeltBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.RIGHT_ANGLE_CONVEYOR_BELT_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RightAngleConveyorBeltBlockEntity be) {
        boolean powered = state.hasProperty(RightAngleConveyorBeltBlock.POWERED) && state.getValue(RightAngleConveyorBeltBlock.POWERED);
        if (powered) {
            return;
        }

        Direction facing = state.hasProperty(RightAngleConveyorBeltBlock.FACING)
                ? state.getValue(RightAngleConveyorBeltBlock.FACING)
                : Direction.NORTH;
        Direction exitDir = RightAngleConveyorBeltBlock.getExitDirection(state);

        double beltSpeed = 0.05;

        // Query items on the belt surface (slightly expanded bounds capture items smoothly across block transitions)
        AABB beltBounds = new AABB(
                pos.getX() - 0.08, pos.getY() + 0.15, pos.getZ() - 0.08,
                pos.getX() + 1.08, pos.getY() + 0.65, pos.getZ() + 1.08
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, beltBounds);

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (ItemEntity itemEntity : items) {
            if (!level.isClientSide() && itemEntity.getAge() > 4000) {
                itemEntity.setUnlimitedLifetime();
            }

            // Determine if the item has reached the turn center
            boolean pastCenter = switch (facing) {
                case NORTH -> itemEntity.getZ() <= centerZ;
                case SOUTH -> itemEntity.getZ() >= centerZ;
                case WEST  -> itemEntity.getX() <= centerX;
                case EAST  -> itemEntity.getX() >= centerX;
                default    -> false;
            };

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
                // Phase 2: Outgoing travel along `exitDir`, centering along incoming axis
                moveX = exitDir.getStepX() * beltSpeed;
                moveZ = exitDir.getStepZ() * beltSpeed;
                if (exitDir.getAxis() == Direction.Axis.Z) {
                    moveX += (centerX - itemEntity.getX()) * 0.15;
                } else {
                    moveZ += (centerZ - itemEntity.getZ()) * 0.15;
                }
            }

            itemEntity.setDeltaMovement(
                    moveX,
                    Math.max(-0.04, Math.min(0.0, itemEntity.getDeltaMovement().y)),
                    moveZ
            );

            if (!level.isClientSide() && pastCenter) {
                checkInventoryInsertion(level, pos, exitDir, itemEntity);
            }
        }
    }

    private static void checkInventoryInsertion(Level level, BlockPos pos, Direction exitDir, ItemEntity itemEntity) {
        boolean nearExitEdge = switch (exitDir) {
            case NORTH -> itemEntity.getZ() <= pos.getZ() + 0.18;
            case SOUTH -> itemEntity.getZ() >= pos.getZ() + 0.82;
            case WEST  -> itemEntity.getX() <= pos.getX() + 0.18;
            case EAST  -> itemEntity.getX() >= pos.getX() + 0.82;
            default -> false;
        };

        if (!nearExitEdge) {
            return;
        }

        BlockPos exitPos = pos.relative(exitDir);
        BlockState exitState = level.getBlockState(exitPos);
        if (exitState.getBlock() instanceof ConveyorBeltBlock
                || exitState.getBlock() instanceof RightAngleConveyorBeltBlock
                || exitState.getBlock() instanceof com.refinery.block.SlopedConveyorBeltBlock) {
            return;
        }
        BlockState exitBelowState = level.getBlockState(exitPos.below());
        if (exitBelowState.getBlock() instanceof com.refinery.block.SlopedConveyorBeltBlock) {
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

    // === Container implementation ===

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
            Direction facing = getBlockState().hasProperty(RightAngleConveyorBeltBlock.FACING)
                    ? getBlockState().getValue(RightAngleConveyorBeltBlock.FACING)
                    : Direction.NORTH;

            // Spawn at entry end
            double spawnX = worldPosition.getX() + 0.5 - facing.getStepX() * 0.35;
            double spawnZ = worldPosition.getZ() + 0.5 - facing.getStepZ() * 0.35;
            double spawnY = worldPosition.getY() + 0.35;

            ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, stack.copy());
            itemEntity.setDeltaMovement(facing.getStepX() * 0.05, 0.0, facing.getStepZ() * 0.05);
            itemEntity.setPickUpDelay(10);
            level.addFreshEntity(itemEntity);
        }
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
}
