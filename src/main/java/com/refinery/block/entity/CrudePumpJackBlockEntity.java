package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CrudePumpJackBlock;
import com.refinery.block.CrudePumpJackPart;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class CrudePumpJackBlockEntity extends BlockEntity {
    public static final int ENERGY_CAPACITY = 10_000;
    public static final int TANK_CAPACITY = 16_000; // 16,000 mB
    public static final int HORIZONTAL_RADIUS = 10;
    public static final int VERTICAL_DEPTH = 50;

    private int energy = 0;
    private int crudeOil = 0;

    // Fractional accumulation for slow mB pumping
    private float crudeAccumulator = 0.0f;
    private int totalPumpedFromCurrentSource = 0;
    private @Nullable BlockPos currentSourceTarget = null;
    private int searchCooldown = 0;

    // Kinematic animation
    private float crankAngle = 0.0f;
    private float pumpingSpeed = 0.0f; // 0.0 = idle, 0.2 = 1 FE/t, 0.5 = 2 FE/t, 1.0 = 4 FE/t

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 64, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            CrudePumpJackBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int maxReceive, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(maxReceive, transaction);
            CrudePumpJackBlockEntity.this.energy = this.energy;
            setChanged();
            return inserted;
        }
    };

    public final SingleFluidHandler fluidHandler = new SingleFluidHandler(
            RefineryFluids.CRUDE_OIL_SOURCE::get,
            this::getCrudeOil,
            this::setCrudeOil,
            TANK_CAPACITY,
            false, // cannot insert crude oil manually
            true,  // can extract crude oil via pipes
            this::setChanged
    );

    public CrudePumpJackBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.CRUDE_PUMP_JACK_BE.get(), pos, state);
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public int getMaxEnergyStored() {
        return ENERGY_CAPACITY;
    }

    public int getCrudeOil() {
        return this.crudeOil;
    }

    public void setCrudeOil(int amount) {
        this.crudeOil = Math.max(0, Math.min(TANK_CAPACITY, amount));
    }

    public int getCrudeCapacity() {
        return TANK_CAPACITY;
    }

    public float getCrankAngle() {
        return this.crankAngle;
    }

    public float getPumpingSpeed() {
        return this.pumpingSpeed;
    }

    public String getPumpingSpeedString() {
        if (this.pumpingSpeed >= 0.9f) return "1,000 mB/min (4 FE/t)";
        if (this.pumpingSpeed >= 0.4f) return "500 mB/min (2 FE/t)";
        if (this.pumpingSpeed >= 0.1f) return "100 mB/min (1 FE/t)";
        return "Idle";
    }

    public void drainCrudeOil(int amount) {
        this.crudeOil = Math.max(0, this.crudeOil - amount);
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            // Client side: smooth animation interpolation
            if (this.pumpingSpeed > 0.0f) {
                this.crankAngle = (this.crankAngle + this.pumpingSpeed * 0.08f) % ((float) Math.PI * 2.0f);
            }
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Direction facing = state.hasProperty(CrudePumpJackBlock.FACING) ? state.getValue(CrudePumpJackBlock.FACING) : Direction.NORTH;

        boolean canPump = this.crudeOil < TANK_CAPACITY;
        int feToConsume = 0;
        float mbPerTick = 0.0f;
        float speed = 0.0f;

        if (canPump) {
            if (this.energy >= 4) {
                feToConsume = 4;
                mbPerTick = 1000.0f / 1200.0f; // 1,000 mB / min
                speed = 1.0f;
            } else if (this.energy >= 2) {
                feToConsume = 2;
                mbPerTick = 500.0f / 1200.0f;  // 500 mB / min
                speed = 0.5f;
            } else if (this.energy >= 1) {
                feToConsume = 1;
                mbPerTick = 100.0f / 1200.0f;  // 100 mB / min
                speed = 0.2f;
            }
        }

        boolean isPumping = false;

        if (feToConsume > 0) {
            // Verify or find a crude oil source target
            if (this.currentSourceTarget == null || !isValidSource(serverLevel, this.currentSourceTarget)) {
                if (this.searchCooldown-- <= 0) {
                    this.searchCooldown = 20; // Search once every second
                    this.currentSourceTarget = findCrudeOilSource(serverLevel, pos, facing);
                }
            }

            if (this.currentSourceTarget != null) {
                // Consume energy
                this.energy -= feToConsume;
                this.energyStorage.set(this.energy);

                // Accumulate crude oil
                this.crudeAccumulator += mbPerTick;
                if (this.crudeAccumulator >= 1.0f) {
                    int add = (int) this.crudeAccumulator;
                    this.crudeOil = Math.min(TANK_CAPACITY, this.crudeOil + add);
                    this.crudeAccumulator -= add;
                    this.totalPumpedFromCurrentSource += add;

                    // When 1,000 mB is extracted, remove the world source block
                    if (this.totalPumpedFromCurrentSource >= 1000) {
                        serverLevel.setBlock(this.currentSourceTarget, Blocks.AIR.defaultBlockState(), 3);
                        serverLevel.playSound(null, this.currentSourceTarget, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.4f, 0.8f);
                        this.totalPumpedFromCurrentSource -= 1000;
                        this.currentSourceTarget = null;
                    }
                }

                isPumping = true;
                this.pumpingSpeed = speed;
                this.crankAngle = (this.crankAngle + speed * 0.08f) % ((float) Math.PI * 2.0f);
            } else {
                this.pumpingSpeed = 0.0f;
            }
        } else {
            this.pumpingSpeed = 0.0f;
        }

        // Auto-push crude oil to connected pipes/tanks through the front wellhead port
        if (this.crudeOil > 0) {
            pushCrudeOil(serverLevel, pos, facing);
        }

        // Update blockstate ACTIVE property
        if (state.hasProperty(CrudePumpJackBlock.ACTIVE)) {
            boolean wasActive = state.getValue(CrudePumpJackBlock.ACTIVE);
            if (wasActive != isPumping) {
                level.setBlock(pos, state.setValue(CrudePumpJackBlock.ACTIVE, isPumping), 3);
            }
        }

        // Sync with clients
        if (level.getGameTime() % 10 == 0) {
            level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private boolean isValidSource(ServerLevel level, BlockPos p) {
        FluidState fs = level.getFluidState(p);
        if (!fs.isSource()) return false;
        Fluid f = fs.getType();
        return f == RefineryFluids.CRUDE_OIL_SOURCE.get()
                || net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(f).getPath().contains("crude_oil");
    }

    /**
     * Scans 10 blocks horizontal radius and 50 blocks downwards for crude oil source blocks.
     * Prioritizes the vertical column directly beneath the wellhead first, then concentric rings.
     */
    private @Nullable BlockPos findCrudeOilSource(ServerLevel level, BlockPos masterPos, Direction facing) {
        // Front wellhead position (where the polished rod enters the ground)
        BlockPos wellheadPos = masterPos; // BOTTOM_FRONT_LEFT base

        int startY = wellheadPos.getY() - 1;
        int minY = Math.max(level.getMinY(), wellheadPos.getY() - VERTICAL_DEPTH);

        // 1. Check direct vertical column first
        for (int y = startY; y >= minY; y--) {
            BlockPos testPos = new BlockPos(wellheadPos.getX(), y, wellheadPos.getZ());
            if (isValidSource(level, testPos)) {
                return testPos;
            }
        }

        // 2. Scan concentric horizontal radii from 1 to 10
        for (int r = 1; r <= HORIZONTAL_RADIUS; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue; // Only perimeter of current radius
                    int targetX = wellheadPos.getX() + dx;
                    int targetZ = wellheadPos.getZ() + dz;

                    // Scan downwards from surface down to 50 blocks
                    for (int y = startY; y >= minY; y--) {
                        BlockPos testPos = new BlockPos(targetX, y, targetZ);
                        if (isValidSource(level, testPos)) {
                            return testPos;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Pushes crude oil into pipes or tanks connected to the front wellhead port.
     */
    private void pushCrudeOil(ServerLevel level, BlockPos masterPos, Direction facing) {
        Direction rightDir = facing.getClockWise();
        BlockPos wellheadA = masterPos;
        BlockPos wellheadB = masterPos.relative(rightDir);

        // Directions radiating outward from wellhead
        Direction[] pushDirs = { facing, rightDir.getOpposite(), rightDir, Direction.DOWN };

        for (Direction dir : pushDirs) {
            if (this.crudeOil <= 0) break;
            BlockPos targetPos = wellheadA.relative(dir);
            var handler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(this.crudeOil, 250);
                FluidResource res = FluidResource.of(RefineryFluids.CRUDE_OIL_SOURCE.get());
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = 0;
                    for (int i = 0; i < handler.size(); i++) {
                        int ins = handler.insert(i, res, toPush - inserted, tx);
                        inserted += ins;
                        if (inserted >= toPush) break;
                    }
                    if (inserted > 0) {
                        tx.commit();
                        this.crudeOil -= inserted;
                        setChanged();
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putInt("CrudeOil", this.crudeOil);
        output.putFloat("CrudeAccumulator", this.crudeAccumulator);
        output.putInt("TotalPumpedCurrentSource", this.totalPumpedFromCurrentSource);
        output.putFloat("CrankAngle", this.crankAngle);
        output.putFloat("PumpingSpeed", this.pumpingSpeed);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("Energy", 0);
        this.energyStorage.set(this.energy);
        this.crudeOil = input.getIntOr("CrudeOil", 0);
        this.crudeAccumulator = input.getFloatOr("CrudeAccumulator", 0.0f);
        this.totalPumpedFromCurrentSource = input.getIntOr("TotalPumpedCurrentSource", 0);
        this.crankAngle = input.getFloatOr("CrankAngle", 0.0f);
        this.pumpingSpeed = input.getFloatOr("PumpingSpeed", 0.0f);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Energy", this.energy);
        tag.putInt("CrudeOil", this.crudeOil);
        tag.putFloat("CrankAngle", this.crankAngle);
        tag.putFloat("PumpingSpeed", this.pumpingSpeed);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
