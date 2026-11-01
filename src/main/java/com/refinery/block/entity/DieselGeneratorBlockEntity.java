package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.DieselGeneratorBlock;
import com.refinery.block.GeneratorPart;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import com.refinery.menu.DieselGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class DieselGeneratorBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int TANK_CAPACITY = 8_000;
    public static final int ENERGY_CAPACITY = 500_000;
    public static final int GENERATION_RATE = 256; // 256 FE/t
    public static final int MAX_EXPORT_RATE = 1_024; // Up to 1,024 FE/t auto-push

    private int dieselAmount = 0;
    private int energy = 0;
    private int burnSubTick = 0;
    private int redstoneMode = 0; // 0 = ALWAYS_ON, 1 = ACTIVE_HIGH, 2 = ACTIVE_LOW
    private boolean active = false;

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 0, MAX_EXPORT_RATE) {
        @Override
        public void set(int amount) {
            super.set(amount);
            DieselGeneratorBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int extract(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int extracted = super.extract(amount, transaction);
            DieselGeneratorBlockEntity.this.energy = this.energy;
            return extracted;
        }
    };

    private final SingleFluidHandler fluidHandler = new SingleFluidHandler(
            RefineryFluids.DIESEL_SOURCE::get,
            this::getDieselAmount,
            this::setDieselAmount,
            TANK_CAPACITY,
            true, true,
            this::setChanged
    );

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> DieselGeneratorBlockEntity.this.getDieselAmount();
                case 1 -> DieselGeneratorBlockEntity.this.getEnergyStored();
                case 2 -> DieselGeneratorBlockEntity.this.active ? 1 : 0;
                case 3 -> DieselGeneratorBlockEntity.this.redstoneMode;
                case 4 -> DieselGeneratorBlockEntity.this.active ? GENERATION_RATE : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> DieselGeneratorBlockEntity.this.setDieselAmount(value);
                case 1 -> DieselGeneratorBlockEntity.this.setEnergy(value);
                case 2 -> DieselGeneratorBlockEntity.this.active = (value != 0);
                case 3 -> DieselGeneratorBlockEntity.this.redstoneMode = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public DieselGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.DIESEL_GENERATOR_BE.get(), pos, state);
    }

    public DieselGeneratorBlockEntity getMaster() {
        if (this.level == null) return this;
        BlockState state = getBlockState();
        if (state.hasProperty(DieselGeneratorBlock.PART) && state.getValue(DieselGeneratorBlock.PART) == GeneratorPart.RIGHT) {
            Direction facing = state.getValue(DieselGeneratorBlock.FACING);
            BlockPos masterPos = this.worldPosition.relative(facing.getCounterClockWise());
            BlockEntity be = this.level.getBlockEntity(masterPos);
            if (be instanceof DieselGeneratorBlockEntity gen) {
                return gen;
            }
        }
        return this;
    }

    public boolean isMaster() {
        BlockState state = getBlockState();
        return !state.hasProperty(DieselGeneratorBlock.PART) || state.getValue(DieselGeneratorBlock.PART) == GeneratorPart.LEFT;
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide() || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!isMaster()) {
            return;
        }

        // 1. Process Fuel Bucket Input Slot (Slot 0 -> Slot 1)
        processBucketSlots();

        // 2. Check Redstone Activation
        BlockState state = getBlockState();
        Direction facing = state.getValue(DieselGeneratorBlock.FACING);
        BlockPos slavePos = this.worldPosition.relative(facing.getClockWise());

        boolean hasRedstone = serverLevel.hasNeighborSignal(this.worldPosition) || serverLevel.hasNeighborSignal(slavePos);
        boolean redstoneAllows = switch (this.redstoneMode) {
            case 1 -> hasRedstone;       // ACTIVE_HIGH
            case 2 -> !hasRedstone;      // ACTIVE_LOW (Backup Generator)
            default -> true;             // ALWAYS_ON
        };

        boolean canGenerate = redstoneAllows && this.dieselAmount > 0 && (this.energy < ENERGY_CAPACITY);

        if (canGenerate) {
            // Generate 256 FE this tick
            addEnergy(GENERATION_RATE);

            // Burn 1 mB diesel every 4 ticks (1,000 FE per mB of diesel fuel)
            this.burnSubTick++;
            if (this.burnSubTick >= 4) {
                this.burnSubTick = 0;
                this.dieselAmount--;
                setChanged();
            }

            if (!this.active) {
                this.active = true;
                serverLevel.setBlock(this.worldPosition, state.setValue(DieselGeneratorBlock.ACTIVE, true), 3);
            }

            // Sound & Exhaust Smoke Particles
            if (serverLevel.getGameTime() % 16 == 0) {
                serverLevel.playSound(null, this.worldPosition, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.45f, 0.65f);
                serverLevel.playSound(null, this.worldPosition, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.35f, 0.85f);
            }

            // Exhaust stack particle emission (rising from top of the right block)
            if (serverLevel.getGameTime() % 2 == 0) {
                double ex = slavePos.getX() + 0.5;
                double ey = slavePos.getY() + 1.25;
                double ez = slavePos.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, ex, ey, ez, 1, 0.05, 0.15, 0.05, 0.02);
                serverLevel.sendParticles(ParticleTypes.SMOKE, ex, ey, ez, 1, 0.02, 0.1, 0.02, 0.01);
            }
        } else {
            if (this.active) {
                this.active = false;
                serverLevel.setBlock(this.worldPosition, state.setValue(DieselGeneratorBlock.ACTIVE, false), 3);
            }
        }

        // 3. Auto-Push Energy to Neighbors (Both Master and Slave positions)
        if (this.energy > 0) {
            pushEnergyToNeighbors(serverLevel, this.worldPosition);
            if (this.energy > 0) {
                pushEnergyToNeighbors(serverLevel, slavePos);
            }
        }
    }

    private void processBucketSlots() {
        ItemStack input = this.items.get(0);
        if (input.is(RefineryFluids.DIESEL_BUCKET.get())) {
            if (this.dieselAmount + 1000 <= TANK_CAPACITY) {
                ItemStack output = this.items.get(1);
                if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                    this.dieselAmount += 1000;
                    input.shrink(1);
                    if (output.isEmpty()) {
                        this.items.set(1, new ItemStack(Items.BUCKET));
                    } else {
                        output.grow(1);
                    }
                    setChanged();
                }
            }
        }
    }

    private void pushEnergyToNeighbors(ServerLevel level, BlockPos origin) {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = origin.relative(dir);
            EnergyHandler targetHandler = level.getCapability(Capabilities.Energy.BLOCK, targetPos, dir.getOpposite());
            if (targetHandler != null) {
                int toPush = Math.min(this.energy, MAX_EXPORT_RATE);
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = targetHandler.insert(toPush, tx);
                    if (inserted > 0) {
                        tx.commit();
                        this.energy -= inserted;
                        this.energyStorage.set(this.energy);
                        setChanged();
                        if (this.energy <= 0) break;
                    }
                }
            }
        }
    }

    public void cycleRedstoneMode() {
        this.redstoneMode = (this.redstoneMode + 1) % 3;
        setChanged();
    }

    public int getRedstoneMode() {
        return this.redstoneMode;
    }

    public void setRedstoneMode(int mode) {
        this.redstoneMode = mode % 3;
        setChanged();
    }

    public int fillDiesel(int amount, boolean doFill) {
        int space = TANK_CAPACITY - this.dieselAmount;
        int toFill = Math.min(amount, space);
        if (doFill && toFill > 0) {
            this.dieselAmount += toFill;
            setChanged();
        }
        return toFill;
    }

    public int getDieselAmount() {
        return this.dieselAmount;
    }

    public void setDieselAmount(int amount) {
        this.dieselAmount = Math.max(0, Math.min(TANK_CAPACITY, amount));
        setChanged();
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public void setEnergy(int amount) {
        this.energy = Math.max(0, Math.min(ENERGY_CAPACITY, amount));
        this.energyStorage.set(this.energy);
        setChanged();
    }

    public void addEnergy(int amount) {
        int space = ENERGY_CAPACITY - this.energy;
        int toAdd = Math.min(amount, space);
        if (toAdd > 0) {
            this.energy += toAdd;
            this.energyStorage.set(this.energy);
            setChanged();
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public SingleFluidHandler getFluidHandler() {
        return getMaster().fluidHandler;
    }

    public EnergyHandler getEnergyStorage() {
        return getMaster().energyStorage;
    }

    public NonNullList<ItemStack> getInventory() {
        return this.items;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("diesel", this.dieselAmount);
        output.putInt("energy", this.energy);
        output.putInt("redstoneMode", this.redstoneMode);
        output.putBoolean("active", this.active);
        output.putInt("burnSubTick", this.burnSubTick);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dieselAmount = input.getIntOr("diesel", 0);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.redstoneMode = input.getIntOr("redstoneMode", 0);
        this.active = input.getBooleanOr("active", false);
        this.burnSubTick = input.getIntOr("burnSubTick", 0);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.refinery.diesel_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DieselGeneratorMenu(containerId, playerInventory, getMaster(), getMaster().dataAccess);
    }

    // --- Container Implementation for Bucket Slots ---
    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }
}
