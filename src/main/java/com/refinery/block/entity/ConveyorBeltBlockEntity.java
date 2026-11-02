package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

public class ConveyorBeltBlockEntity extends BlockEntity implements Container {

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.CONVEYOR_BELT_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ConveyorBeltBlockEntity be) {
        boolean powered = state.hasProperty(ConveyorBeltBlock.POWERED) && state.getValue(ConveyorBeltBlock.POWERED);
        if (powered) {
            return;
        }

        Direction facing = state.hasProperty(ConveyorBeltBlock.FACING)
                ? state.getValue(ConveyorBeltBlock.FACING)
                : Direction.NORTH;

        double beltSpeed = 0.05; // 1/2 speed: ~1 block per second
        double stepX = facing.getStepX();
        double stepZ = facing.getStepZ();

        double dx = stepX * beltSpeed;
        double dz = stepZ * beltSpeed;

        // Query items on the belt surface (slightly expanded bounds capture items smoothly across block transitions)
        AABB beltBounds = new AABB(
                pos.getX() - 0.08, pos.getY() + 0.15, pos.getZ() - 0.08,
                pos.getX() + 1.08, pos.getY() + 0.65, pos.getZ() + 1.08
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, beltBounds);

        for (ItemEntity itemEntity : items) {
            // Keep traveling items from despawning during automated factory routing
            if (!level.isClientSide() && itemEntity.getAge() > 4000) {
                itemEntity.setUnlimitedLifetime();
            }

            Vec3 currentVel = itemEntity.getDeltaMovement();

            // Centering towards middle of the conveyor
            double centerVelX = 0.0;
            double centerVelZ = 0.0;

            if (facing.getAxis() == Direction.Axis.Z) {
                double targetX = pos.getX() + 0.5;
                centerVelX = (targetX - itemEntity.getX()) * 0.15;
            } else {
                double targetZ = pos.getZ() + 0.5;
                centerVelZ = (targetZ - itemEntity.getZ()) * 0.15;
            }

            // Smoothly propel item forward along belt and center it
            itemEntity.setDeltaMovement(
                    (dx != 0 ? dx : currentVel.x * 0.5) + centerVelX,
                    Math.max(-0.04, Math.min(0.0, currentVel.y)),
                    (dz != 0 ? dz : currentVel.z * 0.5) + centerVelZ
            );

            // Server-side inventory injection when reaching the front terminal
            if (!level.isClientSide()) {
                checkInventoryInsertion(level, pos, facing, itemEntity);
            }
        }
    }

    private static void checkInventoryInsertion(Level level, BlockPos pos, Direction facing, ItemEntity itemEntity) {
        // Only attempt insertion when item is near the front edge of the conveyor
        boolean nearFrontEdge = switch (facing) {
            case NORTH -> itemEntity.getZ() <= pos.getZ() + 0.18;
            case SOUTH -> itemEntity.getZ() >= pos.getZ() + 0.82;
            case WEST  -> itemEntity.getX() <= pos.getX() + 0.18;
            case EAST  -> itemEntity.getX() >= pos.getX() + 0.82;
            default -> false;
        };

        if (!nearFrontEdge) {
            return;
        }

        BlockPos frontPos = pos.relative(facing);
        BlockState frontState = level.getBlockState(frontPos);
        // Do not insert into another conveyor belt (allow smooth physical handoff)
        if (frontState.getBlock() instanceof ConveyorBeltBlock
                || frontState.getBlock() instanceof com.refinery.block.SlopedConveyorBeltBlock
                || frontState.getBlock() instanceof com.refinery.block.RightAngleConveyorBeltBlock) {
            return;
        }
        BlockState frontBelowState = level.getBlockState(frontPos.below());
        if (frontBelowState.getBlock() instanceof com.refinery.block.SlopedConveyorBeltBlock) {
            return;
        }

        ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, frontPos, facing.getOpposite());
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

    // === Container implementation for Hopper / Pipe / Machine input ===

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
            Direction facing = getBlockState().hasProperty(ConveyorBeltBlock.FACING)
                    ? getBlockState().getValue(ConveyorBeltBlock.FACING)
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
