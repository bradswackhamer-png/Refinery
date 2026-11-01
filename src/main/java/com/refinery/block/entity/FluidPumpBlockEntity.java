package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.menu.FluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public class FluidPumpBlockEntity extends AbstractFluidPumpBlockEntity implements MenuProvider {
    public static final int FRAME_COLOR = 0xFFD946EF; // Vibrant Creative Magenta / Fuchsia

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> 100;
                case 1 -> 100;
                case 2 -> fluidAmount & 0xFFFF;
                case 3 -> (fluidAmount >> 16) & 0xFFFF;
                case 4 -> (currentFluid != Fluids.EMPTY)
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
                default -> {}
            }
        }

        @Override
        public int getCount() {
            return FluidPumpMenu.DATA_COUNT;
        }
    };

    public FluidPumpBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.FLUID_PUMP_BE.get(), pos, blockState);
        this.currentFluid = Fluids.WATER;
        this.fluidAmount = TANK_CAPACITY;
        this.infiniteWater = true;
    }

    @Override
    public int getFrameColor() {
        return FRAME_COLOR;
    }

    @Override
    public boolean isCreativePump() {
        return true;
    }

    @Override
    public int getPushRate() {
        return 2000;
    }

    @Override
    public int getPumpCycleTicks() {
        return 1;
    }

    @Override
    public int getPumpAmount() {
        return 2000;
    }

    @Override
    public boolean hasPowerToPump() {
        return true;
    }

    @Override
    public void consumePower() {
        // Creative mode requires no power
    }

    @Override
    public void tickFuel() {
        if (this.currentFluid == Fluids.EMPTY) {
            this.currentFluid = Fluids.WATER;
        }
        this.fluidAmount = TANK_CAPACITY;
        this.status = PumpStatus.PUMPING;
        updateActiveState(true);
    }

    @Override
    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    protected PumpStatus getNoPowerStatus() {
        return PumpStatus.IDLE;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.fluid_pump");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FluidPumpMenu(containerId, playerInventory, this.dataAccess);
    }
}
