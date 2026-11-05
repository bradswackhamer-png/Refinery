package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ElectricRefineryFurnaceBlock;
import com.refinery.block.FurnacePart;
import com.refinery.menu.ElectricRefineryFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

public class ElectricRefineryFurnaceBlockEntity extends BlockEntity implements MenuProvider, IRefineryFurnace {
    public static final int ENERGY_CAPACITY = 500_000;
    public static final int MAX_RECEIVE = 10_000;
    public static final int ENERGY_PER_TICK = 500; // High industrial RF consumption (500 FE/t)

    private int energy = 0;
    private int temperature = AMBIENT_TEMP;
    private int redstoneMode = 0; // 0 = ALWAYS_ON, 1 = ACTIVE_HIGH, 2 = ACTIVE_LOW
    private boolean active = false;

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, MAX_RECEIVE, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            ElectricRefineryFurnaceBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            ElectricRefineryFurnaceBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ElectricRefineryFurnaceBlockEntity.this.energy & 0xFFFF;
                case 1 -> (ElectricRefineryFurnaceBlockEntity.this.energy >> 16) & 0xFFFF;
                case 2 -> ElectricRefineryFurnaceBlockEntity.this.temperature;
                case 3 -> ElectricRefineryFurnaceBlockEntity.this.active ? 1 : 0;
                case 4 -> ElectricRefineryFurnaceBlockEntity.this.redstoneMode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> ElectricRefineryFurnaceBlockEntity.this.energy = (ElectricRefineryFurnaceBlockEntity.this.energy & 0xFFFF0000) | (value & 0xFFFF);
                case 1 -> ElectricRefineryFurnaceBlockEntity.this.energy = (ElectricRefineryFurnaceBlockEntity.this.energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 2 -> ElectricRefineryFurnaceBlockEntity.this.temperature = value;
                case 3 -> ElectricRefineryFurnaceBlockEntity.this.active = (value != 0);
                case 4 -> ElectricRefineryFurnaceBlockEntity.this.redstoneMode = value;
            }
        }

        @Override
        public int getCount() {
            return ElectricRefineryFurnaceMenu.DATA_COUNT;
        }
    };

    public ElectricRefineryFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.ELECTRIC_REFINERY_FURNACE_BE.get(), pos, state);
    }

    public ElectricRefineryFurnaceBlockEntity getMaster() {
        if (this.level == null) return this;
        BlockState state = getBlockState();
        if (state.hasProperty(ElectricRefineryFurnaceBlock.PART) && state.getValue(ElectricRefineryFurnaceBlock.PART) == FurnacePart.UPPER) {
            BlockPos masterPos = this.worldPosition.below();
            if (this.level.getBlockEntity(masterPos) instanceof ElectricRefineryFurnaceBlockEntity master) {
                return master;
            }
        }
        return this;
    }

    public boolean isMaster() {
        BlockState state = getBlockState();
        return !state.hasProperty(ElectricRefineryFurnaceBlock.PART) || state.getValue(ElectricRefineryFurnaceBlock.PART) == FurnacePart.LOWER;
    }

    public EnergyHandler getEnergyStorage() {
        return getMaster().energyStorage;
    }

    @Override
    public int getTemperature() {
        return getMaster().temperature;
    }

    @Override
    public boolean isHeating() {
        return getMaster().active;
    }

    @Override
    public boolean isElectric() {
        return true;
    }

    public boolean isActive() {
        return getMaster().active;
    }

    public int getEnergyStored() {
        return getMaster().energy;
    }

    public int getRedstoneMode() {
        return getMaster().redstoneMode;
    }

    public void setRedstoneMode(int mode) {
        ElectricRefineryFurnaceBlockEntity m = getMaster();
        m.redstoneMode = mode % 3;
        m.setChanged();
        m.syncToClient();
    }

    public void syncToClient() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide() || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!isMaster()) return;

        BlockState state = getBlockState();
        BlockPos upperPos = this.worldPosition.above();

        boolean hasRedstone = serverLevel.hasNeighborSignal(this.worldPosition) || serverLevel.hasNeighborSignal(upperPos);
        boolean redstoneAllows = switch (this.redstoneMode) {
            case 1 -> hasRedstone;
            case 2 -> !hasRedstone;
            default -> true;
        };

        boolean wasActive = this.active;
        boolean dirty = false;

        if (redstoneAllows && this.energy >= ENERGY_PER_TICK) {
            this.energy -= ENERGY_PER_TICK;
            this.energyStorage.set(this.energy);
            this.active = true;
            dirty = true;

            if (this.temperature < MAX_TEMP) {
                this.temperature = Math.min(MAX_TEMP, this.temperature + 2);
            }

            if (serverLevel.getGameTime() % 20 == 0) {
                serverLevel.playSound(null, this.worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.5f);
            }
            if (serverLevel.getGameTime() % 5 == 0) {
                double sx = this.worldPosition.getX() + 0.5;
                double sy = this.worldPosition.getY() + 2.65;
                double sz = this.worldPosition.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.8, this.worldPosition.getZ() + 0.5,
                        2, 0.15, 0.2, 0.15, 0.02);
                serverLevel.sendParticles(ParticleTypes.SMOKE, sx, sy, sz, 1, 0.03, 0.08, 0.03, 0.01);
            }
        } else {
            this.active = false;
            if (this.temperature > AMBIENT_TEMP && serverLevel.getGameTime() % 2 == 0) {
                this.temperature = Math.max(AMBIENT_TEMP, this.temperature - 1);
                dirty = true;
            }
        }

        if (wasActive != this.active) {
            if (state.hasProperty(ElectricRefineryFurnaceBlock.ACTIVE)) {
                serverLevel.setBlock(this.worldPosition, state.setValue(ElectricRefineryFurnaceBlock.ACTIVE, this.active), 3);
            }
            BlockState upState = serverLevel.getBlockState(upperPos);
            if (upState.is(state.getBlock()) && upState.hasProperty(ElectricRefineryFurnaceBlock.ACTIVE)) {
                serverLevel.setBlock(upperPos, upState.setValue(ElectricRefineryFurnaceBlock.ACTIVE, this.active), 3);
            }
            dirty = true;
            syncToClient();
        } else if (dirty && serverLevel.getGameTime() % 10 == 0) {
            syncToClient();
        }

        if (dirty) {
            setChanged();
        }
    }

    public boolean stillValid(Player player) {
        if (this.level == null) return false;
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
        output.putInt("temperature", this.temperature);
        output.putInt("redstoneMode", this.redstoneMode);
        output.putBoolean("active", this.active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.temperature = input.getIntOr("temperature", AMBIENT_TEMP);
        this.redstoneMode = input.getIntOr("redstoneMode", 0);
        this.active = input.getBooleanOr("active", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.refinery.electric_refinery_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ElectricRefineryFurnaceBlockEntity m = getMaster();
        return new ElectricRefineryFurnaceMenu(containerId, playerInventory, m, m.dataAccess);
    }
}
