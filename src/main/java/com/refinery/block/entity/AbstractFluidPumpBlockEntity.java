package com.refinery.block.entity;

import com.refinery.block.FluidPumpBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public abstract class AbstractFluidPumpBlockEntity extends BlockEntity implements MenuProvider {
    public static final int TANK_CAPACITY = 10_000; // 10 Buckets
    public static final int PUMP_CYCLE_TICKS = 20;  // 1 bucket per second
    public static final double MAX_DISTANCE = 64.0;

    public enum RedstoneMode {
        IGNORED,
        REQUIRE_HIGH,
        REQUIRE_LOW
    }

    public enum PumpStatus {
        IDLE,
        PUMPING,
        NO_POWER,
        NO_FUEL,
        NO_DIESEL,
        TANK_FULL,
        NO_TARGET,
        TARGET_TOO_FAR,
        POOL_DRY,
        REDSTONE_OFF
    }

    protected @Nullable BlockPos targetPos = null;
    protected boolean infiniteWater = true;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    protected PumpStatus status = PumpStatus.IDLE;

    protected Fluid currentFluid = Fluids.EMPTY;
    protected Fluid lastPumpedFluid = Fluids.EMPTY;
    protected int fluidAmount = 0;
    protected int pumpCooldown = 0;

    public AbstractFluidPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract int getFrameColor();
    public abstract boolean hasPowerToPump();
    public abstract void consumePower();
    public abstract void tickFuel();
    public abstract ContainerData getDataAccess();

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // 1. Process machine-specific fuel/energy operations
        tickFuel();

        // 2. Auto-Push Fluid into adjacent Logistics Craft fluid pipes and tanks
        if (this.fluidAmount > 0 && this.currentFluid != Fluids.EMPTY) {
            pushFluidToNeighbors(serverLevel, pos);
        }

        // 3. Redstone power check
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (this.redstoneMode == RedstoneMode.REQUIRE_HIGH && !hasSignal) {
            this.status = PumpStatus.REDSTONE_OFF;
            updateActiveState(false);
            return;
        }
        if (this.redstoneMode == RedstoneMode.REQUIRE_LOW && hasSignal) {
            this.status = PumpStatus.REDSTONE_OFF;
            updateActiveState(false);
            return;
        }

        // 4. Target Intake Check
        if (this.targetPos == null) {
            this.status = PumpStatus.NO_TARGET;
            updateActiveState(false);
            return;
        }

        double distSq = pos.distSqr(this.targetPos);
        if (distSq > MAX_DISTANCE * MAX_DISTANCE) {
            this.status = PumpStatus.TARGET_TOO_FAR;
            updateActiveState(false);
            return;
        }

        // 5. Pump Cooldown Tick
        if (this.pumpCooldown > 0) {
            this.pumpCooldown--;
            return;
        }

        // 6. Tank capacity check
        if (this.fluidAmount + 1000 > TANK_CAPACITY) {
            this.status = PumpStatus.TANK_FULL;
            updateActiveState(false);
            return;
        }

        // 7. Power Availability Check
        if (!hasPowerToPump()) {
            this.status = getNoPowerStatus();
            updateActiveState(false);
            return;
        }

        // 8. Execute Pumping Operation
        boolean pumped = executePump(serverLevel);
        if (pumped) {
            this.status = PumpStatus.PUMPING;
            this.pumpCooldown = PUMP_CYCLE_TICKS;
            updateActiveState(true);
            setChanged();
        } else {
            updateActiveState(false);
        }
    }

    protected abstract PumpStatus getNoPowerStatus();

    protected boolean executePump(ServerLevel level) {
        if (this.targetPos == null) return false;

        // Check fluid at target position or adjacent
        FluidState targetFluid = level.getFluidState(this.targetPos);
        BlockPos intakeFluidPos = this.targetPos;

        if (targetFluid.isEmpty()) {
            for (Direction dir : Direction.values()) {
                BlockPos nPos = this.targetPos.relative(dir);
                FluidState nState = level.getFluidState(nPos);
                if (!nState.isEmpty()) {
                    intakeFluidPos = nPos;
                    targetFluid = nState;
                    break;
                }
            }
        }

        if (targetFluid.isEmpty()) {
            this.status = PumpStatus.POOL_DRY;
            return false;
        }

        Fluid fluidToPump = targetFluid.getType();
        boolean isWater = targetFluid.is(FluidTags.WATER) || fluidToPump == Fluids.WATER || fluidToPump == Fluids.FLOWING_WATER;
        if (isWater) fluidToPump = Fluids.WATER;
        else if (targetFluid.is(FluidTags.LAVA) || fluidToPump == Fluids.FLOWING_LAVA) fluidToPump = Fluids.LAVA;

        // Cannot mix incompatible fluids in internal tank
        if (this.fluidAmount > 0 && this.currentFluid != Fluids.EMPTY && this.currentFluid != fluidToPump) {
            this.status = PumpStatus.TANK_FULL;
            return false;
        }

        // Infinite Water Mode: Only works on Water!
        if (this.infiniteWater && isWater) {
            consumePower();
            this.currentFluid = Fluids.WATER;
            this.lastPumpedFluid = Fluids.WATER;
            this.fluidAmount = Math.min(TANK_CAPACITY, this.fluidAmount + 1000);
            level.playSound(null, this.worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1.1f);
            return true;
        }

        // Finite Source Pumping: Find contiguous connected fluid source blocks
        BlockPos blockToDrain = findClosestConnectedSource(level, intakeFluidPos, fluidToPump);
        if (blockToDrain == null) {
            this.status = PumpStatus.POOL_DRY;
            return false;
        }

        // Drain the contiguous source block safely (protect waterlogged blocks)
        BlockState drainState = level.getBlockState(blockToDrain);
        if (drainState.hasProperty(BlockStateProperties.WATERLOGGED) && drainState.getValue(BlockStateProperties.WATERLOGGED)) {
            level.setBlock(blockToDrain, drainState.setValue(BlockStateProperties.WATERLOGGED, false), 3);
        } else {
            level.setBlock(blockToDrain, Blocks.AIR.defaultBlockState(), 3);
        }
        consumePower();
        this.currentFluid = fluidToPump;
        this.lastPumpedFluid = fluidToPump;
        this.fluidAmount = Math.min(TANK_CAPACITY, this.fluidAmount + 1000);
        level.playSound(null, this.worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1.1f);
        return true;
    }

    /**
     * Breadth-First-Search (BFS) discovering all contiguous connected fluid source blocks.
     * Can traverse through flowing fluid of the same type so draining water sources remains connected!
     */
    protected @Nullable BlockPos findClosestConnectedSource(ServerLevel level, BlockPos startPos, Fluid targetFluid) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        BlockPos closest = null;
        double minDistanceSq = Double.MAX_VALUE;
        int searched = 0;
        final int MAX_SEARCH = 2048;

        boolean isWaterTarget = (targetFluid == Fluids.WATER || targetFluid == Fluids.FLOWING_WATER);
        boolean isLavaTarget = (targetFluid == Fluids.LAVA || targetFluid == Fluids.FLOWING_LAVA);

        while (!queue.isEmpty() && searched++ < MAX_SEARCH) {
            BlockPos current = queue.poll();

            // Safety: Never drain water from any BlockEntity (pipes, machines, tanks, etc.)
            if (level.getBlockEntity(current) != null) {
                continue;
            }

            FluidState fs = level.getFluidState(current);

            boolean isMatchingSource;
            if (isWaterTarget) {
                isMatchingSource = (fs.getType() == Fluids.WATER && fs.isSource());
            } else if (isLavaTarget) {
                isMatchingSource = (fs.getType() == Fluids.LAVA && fs.isSource());
            } else {
                isMatchingSource = (fs.getType() == targetFluid && fs.isSource());
            }

            if (isMatchingSource) {
                double distSq = (this.targetPos != null) ? this.targetPos.distSqr(current) : 0;
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq;
                    closest = current;
                    if (distSq == 0) return current;
                }
            }

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (visited.add(next)) {
                    if (this.targetPos != null && this.targetPos.distSqr(next) <= MAX_DISTANCE * MAX_DISTANCE) {
                        FluidState nextFs = level.getFluidState(next);
                        boolean canTraverse;
                        if (isWaterTarget) {
                            canTraverse = nextFs.is(FluidTags.WATER) || nextFs.getType() == Fluids.WATER || nextFs.getType() == Fluids.FLOWING_WATER;
                        } else if (isLavaTarget) {
                            canTraverse = nextFs.is(FluidTags.LAVA) || nextFs.getType() == Fluids.LAVA || nextFs.getType() == Fluids.FLOWING_LAVA;
                        } else {
                            canTraverse = (nextFs.getType() == targetFluid);
                        }

                        if (canTraverse || next.equals(this.targetPos)) {
                            queue.add(next);
                        }
                    }
                }
            }
        }

        return closest;
    }

    protected void pushFluidToNeighbors(ServerLevel level, BlockPos pos) {
        Direction[] dirs = new Direction[]{
                Direction.UP,
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST
        };

        for (Direction dir : dirs) {
            if (this.fluidAmount <= 0 || this.currentFluid == Fluids.EMPTY) break;

            BlockPos target = pos.relative(dir);
            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(this.fluidAmount, 500);
                FluidResource res = FluidResource.of(this.currentFluid);
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = 0;
                    for (int i = 0; i < handler.size(); i++) {
                        int ins = handler.insert(i, res, toPush - inserted, tx);
                        inserted += ins;
                        if (inserted >= toPush) break;
                    }
                    if (inserted > 0) {
                        tx.commit();
                        this.fluidAmount -= inserted;
                        if (this.fluidAmount <= 0) {
                            this.fluidAmount = 0;
                            this.currentFluid = Fluids.EMPTY;
                        }
                        setChanged();
                    }
                }
            }
        }
    }

    protected void updateActiveState(boolean active) {
        if (this.level != null) {
            BlockState state = getBlockState();
            if (state.hasProperty(FluidPumpBlock.ACTIVE) && state.getValue(FluidPumpBlock.ACTIVE) != active) {
                this.level.setBlock(this.worldPosition, state.setValue(FluidPumpBlock.ACTIVE, active), 3);
            }
        }
    }

    public @Nullable BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void setTargetPos(@Nullable BlockPos target) {
        this.targetPos = target;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isInfiniteWater() {
        return this.infiniteWater;
    }

    public void setInfiniteWater(boolean infinite) {
        this.infiniteWater = infinite;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void toggleInfiniteWater() {
        setInfiniteWater(!this.infiniteWater);
    }

    public RedstoneMode getRedstoneMode() {
        return this.redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public PumpStatus getStatus() {
        return this.status;
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public Fluid getCurrentFluid() {
        return this.currentFluid;
    }

    public Fluid getLastPumpedFluid() {
        return this.lastPumpedFluid;
    }

    public static int getFluidTypeIndex(Fluid fluid) {
        if (fluid == Fluids.EMPTY || fluid == null) return 0;
        if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) return 1;
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) return 2;
        if (fluid == RefineryFluids.CRUDE_OIL_SOURCE.get()) return 3;
        if (fluid == RefineryFluids.DIESEL_SOURCE.get()) return 4;
        if (fluid == RefineryFluids.GASOLINE_SOURCE.get()) return 5;
        if (fluid == RefineryFluids.ETHANOL_SOURCE.get()) return 6;
        if (fluid == RefineryFluids.STEAM_SOURCE.get()) return 7;
        return 8;
    }

    private final PumpFluidHandler fluidHandler = new PumpFluidHandler();

    public @Nullable ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) {
            return null; // Skid / base mounting face has no fluid port
        }
        return this.fluidHandler;
    }

    private record PumpFluidSnapshot(Fluid fluid, Fluid lastFluid, int amount) {}

    private class PumpFluidHandler extends SnapshotJournal<PumpFluidSnapshot> implements ResourceHandler<FluidResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            if (index != 0 || currentFluid == Fluids.EMPTY) return FluidResource.EMPTY;
            return FluidResource.of(currentFluid);
        }

        @Override
        public long getAmountAsLong(int index) {
            return index == 0 ? fluidAmount : 0L;
        }

        @Override
        public int getAmountAsInt(int index) {
            return index == 0 ? fluidAmount : 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return TANK_CAPACITY;
        }

        @Override
        public int getCapacityAsInt(int index, FluidResource resource) {
            return TANK_CAPACITY;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            // Pump output reservoir is strictly discharge/extract-only.
            // Reject fluid insertion from external pipes and tanks to prevent back-draining and fluid contamination.
            return false;
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            // Strictly extract-only: never allow external pipes to pump fluid backwards into the pump.
            return 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            if (index != 0 || amount <= 0 || fluidAmount <= 0 || currentFluid == Fluids.EMPTY) return 0;
            if (!resource.isEmpty() && resource.getFluid() != currentFluid) return 0;

            int toExtract = Math.min(fluidAmount, amount);
            if (toExtract <= 0) return 0;

            updateSnapshots(transaction);
            fluidAmount -= toExtract;
            if (fluidAmount <= 0) {
                fluidAmount = 0;
                currentFluid = Fluids.EMPTY;
            }
            setChanged();
            return toExtract;
        }

        @Override
        protected PumpFluidSnapshot createSnapshot() {
            return new PumpFluidSnapshot(currentFluid, lastPumpedFluid, fluidAmount);
        }

        @Override
        protected void revertToSnapshot(PumpFluidSnapshot snapshot) {
            currentFluid = snapshot.fluid;
            lastPumpedFluid = snapshot.lastFluid;
            fluidAmount = snapshot.amount;
            setChanged();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fluidAmount", this.fluidAmount);
        output.putString("fluidType", BuiltInRegistries.FLUID.getKey(this.currentFluid).toString());
        output.putString("lastPumpedFluid", BuiltInRegistries.FLUID.getKey(this.lastPumpedFluid).toString());
        output.putBoolean("infiniteWater", this.infiniteWater);
        output.putInt("redstoneMode", this.redstoneMode.ordinal());

        output.putBoolean("hasTarget", this.targetPos != null);
        if (this.targetPos != null) {
            output.putInt("targetX", this.targetPos.getX());
            output.putInt("targetY", this.targetPos.getY());
            output.putInt("targetZ", this.targetPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);

        String fluidKey = input.getStringOr("fluidType", "minecraft:empty");
        this.currentFluid = BuiltInRegistries.FLUID.getOptional(net.minecraft.resources.Identifier.parse(fluidKey)).orElse(Fluids.EMPTY);

        String lastFluidKey = input.getStringOr("lastPumpedFluid", "minecraft:empty");
        this.lastPumpedFluid = BuiltInRegistries.FLUID.getOptional(net.minecraft.resources.Identifier.parse(lastFluidKey)).orElse(Fluids.EMPTY);

        this.infiniteWater = input.getBooleanOr("infiniteWater", true);
        this.redstoneMode = RedstoneMode.values()[Math.clamp(input.getIntOr("redstoneMode", 0), 0, RedstoneMode.values().length - 1)];

        if (input.getBooleanOr("hasTarget", false)) {
            this.targetPos = new BlockPos(
                    input.getIntOr("targetX", 0),
                    input.getIntOr("targetY", 0),
                    input.getIntOr("targetZ", 0)
            );
        } else {
            this.targetPos = null;
        }
    }
}
