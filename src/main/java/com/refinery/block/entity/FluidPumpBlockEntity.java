package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.FluidPumpBlock;
import com.refinery.compat.LogisticsPipeCompat;
import com.refinery.fluid.MultiFluidHandler;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import com.refinery.menu.FluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class FluidPumpBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 2; // Slot 0: Fuel/Bucket In, Slot 1: Empty Bucket Out
    public static final int TANK_CAPACITY = 10_000; // 10 Buckets
    public static final int DIESEL_CAPACITY = 4_000; // 4 Buckets
    public static final int ENERGY_CAPACITY = 20_000; // 20,000 FE
    public static final int RF_PER_BUCKET = 400; // Low requirement
    public static final int DIESEL_PER_BUCKET = 10; // 10 mB per 1000 mB pumped (1 bucket = 100 pumped)
    public static final int PUMP_CYCLE_TICKS = 20; // 1 bucket per second
    public static final int SOLID_FUEL_BURN_TICKS = 60; // 1 coal burns for 3 seconds (60 ticks)
    public static final double MAX_DISTANCE = 64.0;

    public enum PowerMode {
        RF,
        SOLID_FUEL,
        DIESEL
    }

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

    private @Nullable BlockPos targetPos = null;
    private boolean infiniteWater = false;
    private PowerMode powerMode = PowerMode.RF;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    private PumpStatus status = PumpStatus.IDLE;

    private int energy = 0;
    private int dieselAmount = 0;
    private int burnTime = 0;
    private int maxBurnTime = 0;

    private Fluid currentFluid = Fluids.EMPTY;
    private Fluid lastPumpedFluid = Fluids.EMPTY;
    private int fluidAmount = 0;
    private int pumpCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 500, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            FluidPumpBlockEntity.this.energy = amount;
            setChanged();
        }
    };

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy & 0xFFFF;
                case 1 -> (energy >> 16) & 0xFFFF;
                case 2 -> fluidAmount & 0xFFFF;
                case 3 -> (fluidAmount >> 16) & 0xFFFF;
                case 4 -> (currentFluid != Fluids.EMPTY) ? getFluidTypeIndex(currentFluid) : getFluidTypeIndex(lastPumpedFluid);
                case 5 -> dieselAmount;
                case 6 -> burnTime;
                case 7 -> maxBurnTime;
                case 8 -> powerMode.ordinal();
                case 9 -> infiniteWater ? 1 : 0;
                case 10 -> redstoneMode.ordinal();
                case 11 -> status.ordinal();
                case 12 -> targetPos != null ? targetPos.getX() : 0;
                case 13 -> targetPos != null ? targetPos.getY() : 0;
                case 14 -> targetPos != null ? targetPos.getZ() : 0;
                case 15 -> targetPos != null ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 8 -> {
                    powerMode = PowerMode.values()[Math.clamp(value, 0, PowerMode.values().length - 1)];
                    setChanged();
                }
                case 9 -> {
                    infiniteWater = (value != 0);
                    setChanged();
                }
                case 10 -> {
                    redstoneMode = RedstoneMode.values()[Math.clamp(value, 0, RedstoneMode.values().length - 1)];
                    setChanged();
                }
            }
        }

        @Override
        public int getCount() {
            return 16;
        }
    };

    public FluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.FLUID_PUMP_BE.get(), pos, blockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // Keep local energy int synced with storage
        this.energy = this.energyStorage.getAmountAsInt();

        // 1. Process Fuel Input Slots
        handleFuelInput();

        // 2. Decrement burn timer for solid fuel ONLY when pump is actively operating / pumping!
        if (this.powerMode == PowerMode.SOLID_FUEL && this.burnTime > 0) {
            if (this.status == PumpStatus.PUMPING || this.pumpCooldown > 0) {
                this.burnTime--;
            }
        }

        // 3. Auto-Push Fluid into adjacent Logistics Craft fluid pipes and tanks
        if (this.fluidAmount > 0 && this.currentFluid != Fluids.EMPTY) {
            pushFluidToNeighbors(serverLevel, pos);
        }

        // 4. Redstone power check
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

        // 5. Target Intake Check
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

        // 6. Tank capacity check
        if (this.fluidAmount + 1000 > TANK_CAPACITY) {
            this.status = PumpStatus.TANK_FULL;
            updateActiveState(false);
            return;
        }

        // 7. Power Availability Check
        if (!hasPowerToPump()) {
            this.status = switch (this.powerMode) {
                case RF -> PumpStatus.NO_POWER;
                case SOLID_FUEL -> PumpStatus.NO_FUEL;
                case DIESEL -> PumpStatus.NO_DIESEL;
            };
            updateActiveState(false);
            return;
        }

        // 8. Pump Cooldown Tick
        if (this.pumpCooldown > 0) {
            this.pumpCooldown--;
            return;
        }

        // 9. Execute Pumping Operation
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

    private boolean executePump(ServerLevel level) {
        if (this.targetPos == null) return false;

        // Check fluid at target position or adjacent
        FluidState targetFluid = level.getFluidState(this.targetPos);
        BlockPos intakeFluidPos = this.targetPos;

        if (targetFluid.isEmpty()) {
            // If targetPos was already drained, check 6 cardinal neighbors for connected fluid
            for (Direction dir : Direction.values()) {
                BlockPos nPos = this.targetPos.relative(dir);
                FluidState nState = level.getFluidState(nPos);
                if (!nState.isEmpty() && nState.isSource()) {
                    intakeFluidPos = nPos;
                    targetFluid = nState;
                    break;
                }
            }
        }

        if (targetFluid.isEmpty() || !targetFluid.isSource()) {
            this.status = PumpStatus.POOL_DRY;
            return false;
        }

        Fluid fluidToPump = targetFluid.getType();

        // Cannot mix incompatible fluids in internal tank
        if (this.fluidAmount > 0 && this.currentFluid != Fluids.EMPTY && this.currentFluid != fluidToPump) {
            this.status = PumpStatus.TANK_FULL;
            return false;
        }

        // Infinite Water Mode check: Only works on Water!
        boolean isWater = (fluidToPump == Fluids.WATER || fluidToPump == Fluids.FLOWING_WATER);
        if (this.infiniteWater && isWater) {
            // Infinite water: pump without removing source block
            consumePower();
            this.currentFluid = Fluids.WATER;
            this.lastPumpedFluid = Fluids.WATER;
            this.fluidAmount = Math.min(TANK_CAPACITY, this.fluidAmount + 1000);
            level.playSound(null, this.worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1.1f);
            return true;
        }

        // Finite Source Pumping: Find contiguous connected fluid source blocks
        // "pump all connecting source blocks, one at a time from pumping point out."
        // "if source blocks aren't connected don't pump from the unconnected blocks."
        BlockPos blockToDrain = findClosestConnectedSource(level, intakeFluidPos, fluidToPump);
        if (blockToDrain == null) {
            this.status = PumpStatus.POOL_DRY;
            return false;
        }

        // Drain the contiguous source block
        level.setBlock(blockToDrain, Blocks.AIR.defaultBlockState(), 3);
        consumePower();
        this.currentFluid = fluidToPump;
        this.lastPumpedFluid = fluidToPump;
        this.fluidAmount = Math.min(TANK_CAPACITY, this.fluidAmount + 1000);
        level.playSound(null, this.worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1.1f);
        return true;
    }

    /**
     * Breadth-First-Search (BFS) starting from the intake point to find all contiguous,
     * directly connected source blocks of the given fluid type.
     * Returns the connected source block closest to targetPos ("from pumping point out").
     */
    private @Nullable BlockPos findClosestConnectedSource(ServerLevel level, BlockPos startPos, Fluid targetFluid) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        BlockPos closest = null;
        double minDistanceSq = Double.MAX_VALUE;
        int searched = 0;
        final int MAX_SEARCH = 2048;

        while (!queue.isEmpty() && searched++ < MAX_SEARCH) {
            BlockPos current = queue.poll();
            FluidState fs = level.getFluidState(current);

            if (fs.getType() == targetFluid && fs.isSource()) {
                double distSq = (this.targetPos != null) ? this.targetPos.distSqr(current) : 0;
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq;
                    closest = current;
                    // If startPos itself is a valid source, pump it first!
                    if (distSq == 0) break;
                }
            }

            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (visited.add(next)) {
                    if (this.targetPos != null && this.targetPos.distSqr(next) <= MAX_DISTANCE * MAX_DISTANCE) {
                        FluidState nextFs = level.getFluidState(next);
                        // Only traverse if the fluid matches or is connected flow path
                        if (nextFs.getType() == targetFluid || next == this.targetPos) {
                            queue.add(next);
                        }
                    }
                }
            }
        }

        return closest;
    }

    private void pushFluidToNeighbors(ServerLevel level, BlockPos pos) {
        // Output to adjacent fluid handlers, prioritizing Direction.UP (discharge outlet flange)
        Direction[] dirs = new Direction[]{
                Direction.UP,
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
                Direction.DOWN
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

    private boolean hasPowerToPump() {
        return switch (this.powerMode) {
            case RF -> this.energy >= RF_PER_BUCKET;
            case SOLID_FUEL -> this.burnTime > 0 || canBurnSolidFuel();
            case DIESEL -> this.dieselAmount >= DIESEL_PER_BUCKET;
        };
    }

    private boolean canBurnSolidFuel() {
        ItemStack fuel = this.items.get(0);
        return !fuel.isEmpty() && isSolidFuel(fuel);
    }

    public static boolean isSolidFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL) || stack.is(Items.COAL_BLOCK) || stack.is(Items.BLAZE_ROD)) return true;
        if (stack.is(RefineryMod.TAR_ITEM.get()) || stack.is(RefineryMod.BITUMEN.get()) || stack.is(RefineryMod.COAL_DUST.get())) return true;
        return MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0;
    }

    public static int getSolidFuelDuration(ItemStack stack) {
        if (stack.is(Items.COAL_BLOCK)) return SOLID_FUEL_BURN_TICKS * 9; // 9 coal per block = 27s
        return SOLID_FUEL_BURN_TICKS; // 1 coal burns for 3 seconds (60 ticks)
    }

    private void consumePower() {
        switch (this.powerMode) {
            case RF -> {
                this.energy = Math.max(0, this.energy - RF_PER_BUCKET);
                this.energyStorage.set(this.energy);
            }
            case SOLID_FUEL -> {
                if (this.burnTime <= 0) {
                    ItemStack fuel = this.items.get(0);
                    if (!fuel.isEmpty() && isSolidFuel(fuel)) {
                        int dur = getSolidFuelDuration(fuel);
                        this.burnTime = dur;
                        this.maxBurnTime = dur;
                        fuel.shrink(1);
                    }
                }
            }
            case DIESEL -> {
                this.dieselAmount = Math.max(0, this.dieselAmount - DIESEL_PER_BUCKET);
            }
        }
        setChanged();
    }

    private void handleFuelInput() {
        ItemStack in = this.items.get(0);
        if (in.isEmpty()) return;

        // In Diesel Mode: handle bucket refuel (Diesel, Gasoline, Ethanol)
        if (this.powerMode == PowerMode.DIESEL) {
            boolean isDiesel = in.is(RefineryFluids.DIESEL_BUCKET.get());
            boolean isGasoline = in.is(RefineryFluids.GASOLINE_BUCKET.get());
            boolean isEthanol = in.is(RefineryFluids.ETHANOL_BUCKET.get());

            if (isDiesel || isGasoline || isEthanol) {
                if (this.dieselAmount + 1000 <= DIESEL_CAPACITY) {
                    ItemStack out = this.items.get(1);
                    if (out.isEmpty() || (out.is(Items.BUCKET) && out.getCount() < out.getMaxStackSize())) {
                        this.dieselAmount += 1000;
                        in.shrink(1);
                        if (out.isEmpty()) {
                            this.items.set(1, new ItemStack(Items.BUCKET));
                        } else {
                            out.grow(1);
                        }
                        setChanged();
                    }
                }
            }
        }
    }

    private void updateActiveState(boolean active) {
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

    public PowerMode getPowerMode() {
        return this.powerMode;
    }

    public void setPowerMode(PowerMode mode) {
        this.powerMode = mode;
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void cyclePowerMode() {
        int next = (this.powerMode.ordinal() + 1) % PowerMode.values().length;
        setPowerMode(PowerMode.values()[next]);
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

    public void cycleRedstoneMode() {
        int next = (this.redstoneMode.ordinal() + 1) % RedstoneMode.values().length;
        setRedstoneMode(RedstoneMode.values()[next]);
    }

    public PumpStatus getStatus() {
        return this.status;
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public void setFluidAmount(int amt) {
        this.fluidAmount = amt;
    }

    public Fluid getCurrentFluid() {
        return this.currentFluid;
    }

    public void setCurrentFluid(Fluid f) {
        this.currentFluid = f;
    }

    public int getDieselAmount() {
        return this.dieselAmount;
    }

    public void setDieselAmount(int amt) {
        this.dieselAmount = amt;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
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

    public static Fluid getFluidFromIndex(int index) {
        return switch (index) {
            case 1 -> Fluids.WATER;
            case 2 -> Fluids.LAVA;
            case 3 -> RefineryFluids.CRUDE_OIL_SOURCE.get();
            case 4 -> RefineryFluids.DIESEL_SOURCE.get();
            case 5 -> RefineryFluids.GASOLINE_SOURCE.get();
            case 6 -> RefineryFluids.ETHANOL_SOURCE.get();
            case 7 -> RefineryFluids.STEAM_SOURCE.get();
            default -> Fluids.EMPTY;
        };
    }

    // === Capabilities Integration ===
    public ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        // Output tank handler (can extract pumped fluid)
        SingleFluidHandler outputHandler = new SingleFluidHandler(
                () -> this.currentFluid,
                () -> this.fluidAmount,
                amt -> {
                    this.fluidAmount = amt;
                    if (amt <= 0) this.currentFluid = Fluids.EMPTY;
                },
                TANK_CAPACITY,
                false, // External cannot insert into pumped output tank
                true,  // External can extract pumped fluid
                this::setChanged
        );

        // Diesel fuel input handler (can insert fuel in diesel mode)
        SingleFluidHandler dieselHandler = new SingleFluidHandler(
                RefineryFluids.DIESEL_SOURCE::get,
                () -> this.dieselAmount,
                amt -> this.dieselAmount = amt,
                DIESEL_CAPACITY,
                true,
                false,
                this::setChanged
        );

        return new MultiFluidHandler(outputHandler, dieselHandler);
    }

    // === WorldlyContainer Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return new int[]{1};
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(RefineryFluids.DIESEL_BUCKET.get()) ||
                    stack.is(RefineryFluids.GASOLINE_BUCKET.get()) ||
                    stack.is(RefineryFluids.ETHANOL_BUCKET.get()) ||
                    isSolidFuel(stack);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.items) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack res = ContainerHelper.removeItem(this.items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.fluid_pump");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FluidPumpMenu(containerId, playerInventory, this, this.dataAccess);
    }

    // === Persistence ===
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
        output.putInt("diesel", this.dieselAmount);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
        output.putInt("fluidAmount", this.fluidAmount);
        output.putString("fluidType", BuiltInRegistries.FLUID.getKey(this.currentFluid).toString());
        output.putString("lastPumpedFluid", BuiltInRegistries.FLUID.getKey(this.lastPumpedFluid).toString());
        output.putInt("powerMode", this.powerMode.ordinal());
        output.putBoolean("infiniteWater", this.infiniteWater);
        output.putInt("redstoneMode", this.redstoneMode.ordinal());

        output.putBoolean("hasTarget", this.targetPos != null);
        if (this.targetPos != null) {
            output.putInt("targetX", this.targetPos.getX());
            output.putInt("targetY", this.targetPos.getY());
            output.putInt("targetZ", this.targetPos.getZ());
        }

        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.dieselAmount = input.getIntOr("diesel", 0);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
        this.fluidAmount = input.getIntOr("fluidAmount", 0);

        String fluidKey = input.getStringOr("fluidType", "minecraft:empty");
        this.currentFluid = BuiltInRegistries.FLUID.getOptional(net.minecraft.resources.Identifier.parse(fluidKey)).orElse(Fluids.EMPTY);

        String lastFluidKey = input.getStringOr("lastPumpedFluid", "minecraft:empty");
        this.lastPumpedFluid = BuiltInRegistries.FLUID.getOptional(net.minecraft.resources.Identifier.parse(lastFluidKey)).orElse(Fluids.EMPTY);

        this.powerMode = PowerMode.values()[Math.clamp(input.getIntOr("powerMode", 0), 0, PowerMode.values().length - 1)];
        this.infiniteWater = input.getBooleanOr("infiniteWater", false);
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

        ContainerHelper.loadAllItems(input, this.items);
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
