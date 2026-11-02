package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
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

public class SlopedConveyorBeltBlockEntity extends BlockEntity implements Container {

    public SlopedConveyorBeltBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.SLOPED_CONVEYOR_BELT_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SlopedConveyorBeltBlockEntity be) {
        boolean powered = state.hasProperty(SlopedConveyorBeltBlock.POWERED) && state.getValue(SlopedConveyorBeltBlock.POWERED);
        if (powered) {
            return;
        }

        Direction facing = state.hasProperty(SlopedConveyorBeltBlock.FACING)
                ? state.getValue(SlopedConveyorBeltBlock.FACING)
                : Direction.NORTH;
        SlopedConveyorBeltBlock.SlopeDirection slope = state.hasProperty(SlopedConveyorBeltBlock.SLOPE)
                ? state.getValue(SlopedConveyorBeltBlock.SLOPE)
                : SlopedConveyorBeltBlock.SlopeDirection.UP;

        double beltSpeed = 0.05; // 1 block per second
        double stepX = facing.getStepX() * beltSpeed;
        double stepZ = facing.getStepZ() * beltSpeed;

        // Query items within the block volume (expanded bounds capture items smoothly entering and exiting slope)
        AABB beltBounds = new AABB(
                pos.getX() - 0.20, pos.getY() - 0.15, pos.getZ() - 0.20,
                pos.getX() + 1.20, pos.getY() + 1.80, pos.getZ() + 1.20
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, beltBounds);

        for (ItemEntity itemEntity : items) {
            if (!level.isClientSide() && itemEntity.getAge() > 4000) {
                itemEntity.setUnlimitedLifetime();
            }

            // Calculate progress t in [0..1] along facing direction
            double relX = itemEntity.getX() - pos.getX();
            double relZ = itemEntity.getZ() - pos.getZ();
            double rawT = switch (facing) {
                case NORTH -> 1.0 - relZ;
                case SOUTH -> relZ;
                case WEST  -> 1.0 - relX;
                case EAST  -> relX;
                default    -> 0.5;
            };
            double t = Math.max(0.0, Math.min(1.0, rawT));

            // Surface height:
            // UP: from y + 0.3125 at t=0 to y + 1.3125 at t=1
            // DOWN: from y + 1.3125 at t=0 to y + 0.3125 at t=1
            double targetY = slope == SlopedConveyorBeltBlock.SlopeDirection.UP
                    ? pos.getY() + 0.3125 + t * 1.0
                    : pos.getY() + 1.3125 - t * 1.0;

            // Direct vertical alignment onto the inclined belt surface (only while actively traversing the slope)
            if (rawT >= 0.0 && rawT <= 1.0) {
                double diffY = targetY - itemEntity.getY();
                if (Math.abs(diffY) > 0.02) {
                    itemEntity.setPos(itemEntity.getX(), targetY, itemEntity.getZ());
                }
            }

            // Centering along perpendicular axis
            double centerVelX = 0.0;
            double centerVelZ = 0.0;
            if (facing.getAxis() == Direction.Axis.Z) {
                double targetX = pos.getX() + 0.5;
                centerVelX = (targetX - itemEntity.getX()) * 0.15;
            } else {
                double targetZ = pos.getZ() + 0.5;
                centerVelZ = (targetZ - itemEntity.getZ()) * 0.15;
            }

            // Level off vertical velocity near terminals so items glide horizontally onto flat conveyors
            double verticalSpeed;
            if (slope == SlopedConveyorBeltBlock.SlopeDirection.UP) {
                verticalSpeed = (rawT >= 0.92) ? 0.0 : beltSpeed;
            } else {
                verticalSpeed = (rawT >= 0.85) ? 0.0 : -beltSpeed;
            }

            // Provide forward momentum boost near terminal exit to smoothly transition into adjacent flat conveyor
            double forwardSpeed = (rawT >= 0.88) ? beltSpeed * 1.3 : beltSpeed;
            double curStepX = facing.getStepX() * forwardSpeed;
            double curStepZ = facing.getStepZ() * forwardSpeed;

            itemEntity.setDeltaMovement(curStepX + centerVelX, verticalSpeed, curStepZ + centerVelZ);
            itemEntity.setOnGround(true);

            if (!level.isClientSide()) {
                checkInventoryInsertion(level, pos, facing, slope, itemEntity, t);
            }
        }
    }

    private static void checkInventoryInsertion(Level level, BlockPos pos, Direction facing, SlopedConveyorBeltBlock.SlopeDirection slope, ItemEntity itemEntity, double t) {
        if (t < 0.85) {
            return;
        }

        // Target position depends on slope
        BlockPos targetPos = slope == SlopedConveyorBeltBlock.SlopeDirection.UP
                ? pos.relative(facing).above()
                : pos.relative(facing).below();

        // If target is another conveyor, let it roll smoothly
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() instanceof ConveyorBeltBlock
                || targetState.getBlock() instanceof SlopedConveyorBeltBlock
                || targetState.getBlock() instanceof com.refinery.block.RightAngleConveyorBeltBlock) {
            return;
        }

        // Also check direct forward block
        BlockPos directForward = pos.relative(facing);
        BlockState directForwardState = level.getBlockState(directForward);
        if (directForwardState.getBlock() instanceof ConveyorBeltBlock
                || directForwardState.getBlock() instanceof SlopedConveyorBeltBlock
                || directForwardState.getBlock() instanceof com.refinery.block.RightAngleConveyorBeltBlock) {
            return;
        }

        // Try inserting into inventory at target
        ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, targetPos, facing.getOpposite());
        if (targetHandler == null) {
            targetHandler = level.getCapability(Capabilities.Item.BLOCK, directForward, facing.getOpposite());
        }

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
            Direction facing = getBlockState().hasProperty(SlopedConveyorBeltBlock.FACING)
                    ? getBlockState().getValue(SlopedConveyorBeltBlock.FACING)
                    : Direction.NORTH;
            SlopedConveyorBeltBlock.SlopeDirection slope = getBlockState().hasProperty(SlopedConveyorBeltBlock.SLOPE)
                    ? getBlockState().getValue(SlopedConveyorBeltBlock.SLOPE)
                    : SlopedConveyorBeltBlock.SlopeDirection.UP;

            // Spawn at entry end
            double spawnX = worldPosition.getX() + 0.5 - facing.getStepX() * 0.35;
            double spawnZ = worldPosition.getZ() + 0.5 - facing.getStepZ() * 0.35;
            double spawnY = slope == SlopedConveyorBeltBlock.SlopeDirection.UP
                    ? worldPosition.getY() + 0.35
                    : worldPosition.getY() + 1.25;

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
