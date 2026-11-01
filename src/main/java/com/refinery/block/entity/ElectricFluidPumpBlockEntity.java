package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.menu.ElectricFluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

public class ElectricFluidPumpBlockEntity extends AbstractFluidPumpBlockEntity {
    public static final int ENERGY_CAPACITY = 20_000; // 20,000 FE
    public static final int RF_PER_BUCKET = 400; // 400 FE per bucket
    public static final int FRAME_COLOR = 0xFF2563EB; // Electric Blue

    private int energy = 0;

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            ElectricFluidPumpBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int maxReceive, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(maxReceive, transaction);
            ElectricFluidPumpBlockEntity.this.energy = this.energy;
            setChanged();
            return inserted;
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
                case 4 -> (currentFluid != net.minecraft.world.level.material.Fluids.EMPTY)
                        ? getFluidTypeIndex(currentFluid)
                        : getFluidTypeIndex(lastPumpedFluid);
                case 5 -> infiniteWater ? 1 : 0;
                case 6 -> redstoneMode.ordinal();
                case 7 -> status.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 5 -> {
                    infiniteWater = (value != 0);
                    setChanged();
                }
                case 6 -> {
                    redstoneMode = RedstoneMode.values()[Math.clamp(value, 0, RedstoneMode.values().length - 1)];
                    setChanged();
                }
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public ElectricFluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.ELECTRIC_FLUID_PUMP_BE.get(), pos, blockState);
    }

    @Override
    public int getFrameColor() {
        return FRAME_COLOR;
    }

    @Override
    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    protected PumpStatus getNoPowerStatus() {
        return PumpStatus.NO_POWER;
    }

    @Override
    public boolean hasPowerToPump() {
        return this.energy >= RF_PER_BUCKET;
    }

    @Override
    public void consumePower() {
        this.energy = Math.max(0, this.energy - RF_PER_BUCKET);
        this.energyStorage.set(this.energy);
        setChanged();
    }

    @Override
    public void tickFuel() {
        this.energy = this.energyStorage.getAmountAsInt();
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        this.energyStorage.set(energy);
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.electric_fluid_pump");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFluidPumpMenu(containerId, playerInventory, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
    }
}
