package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.DischargeMode;
import com.refinery.block.FluidDischargeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class FluidDischargeBlockEntity extends BlockEntity {
    public static final int TANK_CAPACITY = 4000;
    public static final int MAX_SEARCH_RADIUS = 48;
    public static final int MAX_BFS_NODES = 2048;

    private DischargeMode mode = DischargeMode.VOID;
    private boolean redstonePaused = false;
    private int activeTimer = 0;
    private int tickCounter = 0;

    private Fluid currentFluid = Fluids.EMPTY;
    private int fluidAmount = 0;

    private final DischargeFluidHandler fluidHandler = new DischargeFluidHandler();

    public FluidDischargeBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.FLUID_DISCHARGE_BE.get(), pos, state);
        if (state.hasProperty(FluidDischargeBlock.MODE)) {
            this.mode = state.getValue(FluidDischargeBlock.MODE);
        }
    }

    public DischargeMode getMode() {
        return this.mode;
    }

    public void setMode(DischargeMode mode) {
        this.mode = mode;
        if (mode == DischargeMode.VOID) {
            this.currentFluid = Fluids.EMPTY;
            this.fluidAmount = 0;
        }
        setChanged();
    }

    public boolean isRedstonePaused() {
        return this.redstonePaused;
    }

    public void setRedstonePaused(boolean paused) {
        this.redstonePaused = paused;
        setChanged();
    }

    public ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        return this.fluidHandler;
    }

    public Fluid getCurrentFluid() {
        return this.currentFluid;
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    public void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        tickCounter++;

        // 1. Redstone Pause check
        if (this.redstonePaused || level.hasNeighborSignal(worldPosition)) {
            updateActiveState(false);
            return;
        }

        // 2. Mode Operations
        if (this.mode == DischargeMode.VOID) {
            handleVoidMode();
        } else {
            handleFillMode();
        }

        // 3. Update active timer and blockstate
        if (this.activeTimer > 0) {
            this.activeTimer--;
        }
        updateActiveState(this.activeTimer > 0);
    }

    private void handleVoidMode() {
        if (this.fluidAmount > 0) {
            // Drain / dump buffered fluid
            this.fluidAmount = 0;
            this.currentFluid = Fluids.EMPTY;
            this.activeTimer = 10;
            if (this.tickCounter % 20 == 0) {
                level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.25f, 1.8f);
            }
            setChanged();
        }
    }

    private void handleFillMode() {
        // Attempt fill every 4 ticks (~5 source blocks per second)
        if (this.tickCounter % 4 != 0) {
            return;
        }

        if (this.fluidAmount < 1000 || this.currentFluid == Fluids.EMPTY) {
            return;
        }

        BlockState sourceBlockState = this.currentFluid.defaultFluidState().createLegacyBlock();
        if (sourceBlockState.isAir()) {
            return;
        }

        // Search for nearest contiguous unfilled air/replaceable position on the same Y level
        BlockPos targetPos = findNextContiguousFillPosition(level, worldPosition, this.currentFluid);
        if (targetPos != null) {
            // Place fluid source block
            level.setBlock(targetPos, sourceBlockState, Block.UPDATE_ALL);
            this.fluidAmount -= 1000;
            if (this.fluidAmount <= 0) {
                this.fluidAmount = 0;
                this.currentFluid = Fluids.EMPTY;
            }
            this.activeTimer = 10;

            level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.7f, 1.0f);
            setChanged();
        }
    }

    /**
     * Contiguous Breadth-First Search along the horizontal plane (Y = startPos.getY()).
     *
     * Rules:
     * - Only traverses contiguous blocks on the exact same Y level.
     * - Only expands through the machine block or through blocks already containing this fluid.
     * - Stops at solid walls / boundaries, ensuring fluid cannot jump across walls into adjacent pits.
     * - Returns the nearest contiguous block that needs to be filled with a fluid source.
     */
    public static @Nullable BlockPos findNextContiguousFillPosition(Level level, BlockPos startPos, Fluid fluid) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        // Start search from direct horizontal neighbors of the discharge block
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = startPos.relative(dir);
            visited.add(neighbor);
            queue.add(neighbor);
        }

        int nodesExamined = 0;

        while (!queue.isEmpty() && nodesExamined < MAX_BFS_NODES) {
            BlockPos current = queue.poll();
            nodesExamined++;

            // If current position is a candidate to be converted to a fluid source
            if (canBeFilledWithFluid(level, current, fluid)) {
                return current;
            }

            // If current position is already fluid matching this type (either source or flowing),
            // it can conduct fluid further to its neighbors!
            if (isContiguousFluid(level, current, fluid)) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos next = current.relative(dir);
                    if (withinDistance(startPos, next, MAX_SEARCH_RADIUS) && visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
            // If it's a solid block / wall / foreign block, it does NOT conduct fluid and does not expand!
        }

        return null;
    }

    /**
     * Checks if a block is eligible to be turned into a fluid source block:
     * - Must be air or replaceable (e.g. grass, snow, tall grass)
     * - OR flowing fluid of the same type that is not yet a full source block
     */
    private static boolean canBeFilledWithFluid(Level level, BlockPos pos, Fluid fluid) {
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        // If it's already a full source block of this fluid, it doesn't need filling
        if (fluidState.isSource() && isSameFluid(fluidState.getType(), fluid)) {
            return false;
        }

        // Air block or replaceable non-solid block
        if (state.isAir() || (state.canBeReplaced() && !fluidState.isSource())) {
            return true;
        }

        // Flowing fluid of the same type (converting flowing fluid to source)
        if (!fluidState.isEmpty() && isSameFluid(fluidState.getType(), fluid) && !fluidState.isSource()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a block position contains contiguous fluid matching the discharge fluid type.
     */
    private static boolean isContiguousFluid(Level level, BlockPos pos, Fluid fluid) {
        FluidState fluidState = level.getFluidState(pos);
        if (fluidState.isEmpty()) {
            return false;
        }
        return isSameFluid(fluidState.getType(), fluid);
    }

    private static boolean isSameFluid(Fluid a, Fluid b) {
        if (a == b) return true;
        if (a instanceof FlowingFluid fa && b instanceof FlowingFluid fb) {
            return fa.isSame(fb);
        }
        return false;
    }

    private static boolean withinDistance(BlockPos start, BlockPos pos, int maxRadius) {
        return Math.abs(start.getX() - pos.getX()) <= maxRadius
                && Math.abs(start.getZ() - pos.getZ()) <= maxRadius;
    }

    private void updateActiveState(boolean active) {
        if (level == null) return;
        BlockState state = getBlockState();
        if (state.hasProperty(FluidDischargeBlock.ACTIVE) && state.getValue(FluidDischargeBlock.ACTIVE) != active) {
            level.setBlock(worldPosition, state.setValue(FluidDischargeBlock.ACTIVE, active), Block.UPDATE_ALL);
        }
    }

    // === Capabilities Fluid Handler ===

    private class DischargeFluidHandler extends SnapshotJournal<FluidSnapshot> implements ResourceHandler<FluidResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            if (index != 0 || currentFluid == Fluids.EMPTY) {
                return FluidResource.EMPTY;
            }
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
            if (index != 0 || resource.isEmpty()) return false;
            if (mode == DischargeMode.VOID) {
                return true; // Accepts any fluid in VOID mode
            }
            // In FILL mode:
            Fluid fluid = resource.getFluid();
            if (fluid == Fluids.EMPTY || fluid.defaultFluidState().createLegacyBlock().isAir()) {
                return false;
            }
            return currentFluid == Fluids.EMPTY || isSameFluid(currentFluid, fluid);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
            if (redstonePaused || (level != null && level.hasNeighborSignal(worldPosition))) {
                return 0; // Paused by redstone
            }

            Fluid fluid = resource.getFluid();

            // In VOID mode: immediately accept and dump!
            if (mode == DischargeMode.VOID) {
                updateSnapshots(transaction);
                currentFluid = fluid;
                fluidAmount = Math.min(TANK_CAPACITY, fluidAmount + amount);
                activeTimer = 10;
                setChanged();
                return amount;
            }

            // In FILL mode:
            if (fluid == Fluids.EMPTY || fluid.defaultFluidState().createLegacyBlock().isAir()) {
                return 0;
            }
            if (currentFluid != Fluids.EMPTY && !isSameFluid(currentFluid, fluid)) {
                return 0; // Different fluid in tank
            }

            int space = TANK_CAPACITY - fluidAmount;
            int toInsert = Math.min(space, amount);
            if (toInsert <= 0) return 0;

            updateSnapshots(transaction);
            currentFluid = fluid;
            fluidAmount += toInsert;
            setChanged();
            return toInsert;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            return 0; // Pure discharge/drain sink
        }

        @Override
        protected FluidSnapshot createSnapshot() {
            return new FluidSnapshot(currentFluid, fluidAmount);
        }

        @Override
        protected void revertToSnapshot(FluidSnapshot snapshot) {
            currentFluid = snapshot.fluid;
            fluidAmount = snapshot.amount;
            setChanged();
        }
    }

    private record FluidSnapshot(Fluid fluid, int amount) {}

    // === Serialization ===

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("mode", this.mode.ordinal());
        output.putInt("fluidAmount", this.fluidAmount);
        output.putString("fluidType", BuiltInRegistries.FLUID.getKey(this.currentFluid).toString());
        output.putBoolean("redstonePaused", this.redstonePaused);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int modeIdx = input.getIntOr("mode", 0);
        this.mode = DischargeMode.values()[Math.clamp(modeIdx, 0, DischargeMode.values().length - 1)];

        this.fluidAmount = input.getIntOr("fluidAmount", 0);
        String fluidKey = input.getStringOr("fluidType", "minecraft:empty");
        this.currentFluid = BuiltInRegistries.FLUID.getOptional(Identifier.parse(fluidKey)).orElse(Fluids.EMPTY);
        if (this.currentFluid == Fluids.EMPTY) {
            this.fluidAmount = 0;
        }

        this.redstonePaused = input.getBooleanOr("redstonePaused", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
