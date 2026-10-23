package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.SteamTurbineControllerBlock;
import com.refinery.menu.SteamTurbineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SteamTurbineControllerBlockEntity extends BlockEntity implements ITurbineController, MenuProvider {
    public static final int STEAM_CAPACITY = 32_000;
    public static final int WATER_CAPACITY = 16_000;
    public static final int ENERGY_CAPACITY = 1_000_000;
    public static final int STEAM_PER_TICK = 50; // Consumes up to 50 mB steam per tick
    public static final int ENERGY_PER_STEAM = 20; // 20 FE per mB of steam -> 1,000 FE/t
    public static final int WATER_PER_STEAM_RATIO = 2; // 2 mB steam -> 1 mB water (condensed)

    private boolean assembled = false;
    private int steamAmount = 0;
    private int waterAmount = 0;
    private int energy = 0;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 0, 10_000) {
        @Override
        public void set(int amount) {
            super.set(amount);
            SteamTurbineControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int extract(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int extracted = super.extract(amount, transaction);
            SteamTurbineControllerBlockEntity.this.energy = this.energy;
            return extracted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> SteamTurbineControllerBlockEntity.this.steamAmount;
                case 1 -> SteamTurbineControllerBlockEntity.this.waterAmount;
                case 2 -> SteamTurbineControllerBlockEntity.this.energy;
                case 3 -> SteamTurbineControllerBlockEntity.this.assembled ? 1 : 0;
                case 4 -> (SteamTurbineControllerBlockEntity.this.assembled && SteamTurbineControllerBlockEntity.this.steamAmount > 0) ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> SteamTurbineControllerBlockEntity.this.steamAmount = value;
                case 1 -> SteamTurbineControllerBlockEntity.this.waterAmount = value;
                case 3 -> SteamTurbineControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public SteamTurbineControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.STEAM_TURBINE_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    public boolean isAssembled() {
        return this.assembled;
    }

    @Override
    public BlockPos getPosition() {
        return this.worldPosition;
    }

    @Override
    public int getSteamAmount() {
        return this.steamAmount;
    }

    @Override
    public int getWaterAmount() {
        return this.waterAmount;
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return ENERGY_CAPACITY;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return this.energyStorage;
    }

    @Override
    public int fillSteam(int amount) {
        int space = STEAM_CAPACITY - this.steamAmount;
        int toAdd = Math.min(space, amount);
        this.steamAmount += toAdd;
        if (toAdd > 0) setChanged();
        return toAdd;
    }

    @Override
    public int drainWater(int amount) {
        int toDrain = Math.min(this.waterAmount, amount);
        this.waterAmount -= toDrain;
        if (toDrain > 0) setChanged();
        return toDrain;
    }

    public void disassemble() {
        if (this.assembled) {
            this.assembled = false;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof SteamTurbineControllerBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(SteamTurbineControllerBlock.ASSEMBLED, false)
                            .setValue(SteamTurbineControllerBlock.ACTIVE, false)
                            .setValue(SteamTurbineControllerBlock.BLUEPRINT, false), 3);
                }
            }
            setChanged();
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Periodic 1-second structure validation
        if (--this.structureCheckCooldown <= 0) {
            this.structureCheckCooldown = 20;
            syncAssembly();
        }

        // Blueprint Projection Ticking
        if (this.blueprintProjectionTicks > 0) {
            this.blueprintProjectionTicks--;
            if (this.blueprintProjectionTicks == 0) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof SteamTurbineControllerBlock && current.getValue(SteamTurbineControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(SteamTurbineControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            BlockState current = getBlockState();
            if (current.getBlock() instanceof SteamTurbineControllerBlock && current.getValue(SteamTurbineControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(SteamTurbineControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(SteamTurbineControllerBlock.ACTIVE);
        boolean isActive = false;

        // Process Steam -> Water + FE
        int energySpace = ENERGY_CAPACITY - this.energy;
        int waterSpace = WATER_CAPACITY - this.waterAmount;

        if (this.steamAmount >= 2 && energySpace >= (2 * ENERGY_PER_STEAM) && waterSpace >= 1) {
            int maxSteamByEnergy = energySpace / ENERGY_PER_STEAM;
            int maxSteamByWater = waterSpace * WATER_PER_STEAM_RATIO;
            int steamToConsume = Math.min(STEAM_PER_TICK, Math.min(this.steamAmount, Math.min(maxSteamByEnergy, maxSteamByWater)));

            // Ensure even number for water ratio
            if (steamToConsume % 2 != 0) {
                steamToConsume--;
            }

            if (steamToConsume > 0) {
                this.steamAmount -= steamToConsume;
                int feGenerated = steamToConsume * ENERGY_PER_STEAM;
                int waterCondensed = steamToConsume / WATER_PER_STEAM_RATIO;

                addEnergy(feGenerated);
                this.waterAmount += waterCondensed;
                isActive = true;

                // Turbine hum sound & spinning cloud particles
                if (level.getRandom().nextInt(10) == 0) {
                    serverLevel.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4f, 1.8f);
                }
                if (level.getRandom().nextInt(3) == 0) {
                    Direction facing = state.getValue(SteamTurbineControllerBlock.FACING);
                    BlockPos rotorPos = getRelativePos(0, 1, 1, facing);
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            rotorPos.getX() + 0.5, rotorPos.getY() + 0.5, rotorPos.getZ() + 0.5,
                            2, 0.2, 0.2, 0.2, 0.04);
                }
                setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof SteamTurbineControllerBlock) {
                level.setBlock(pos, latest.setValue(SteamTurbineControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    private void addEnergy(int amount) {
        int toAdd = Math.min(amount, ENERGY_CAPACITY - this.energy);
        if (toAdd > 0) {
            this.energyStorage.set(this.energy + toAdd);
            setChanged();
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof SteamTurbineControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(SteamTurbineControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(SteamTurbineControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(SteamTurbineControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.9f, 1.2f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5,
                            5, 0.3, 0.3, 0.3, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
        }
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            this.blueprintProjectionTicks = 0;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof SteamTurbineControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SteamTurbineControllerBlock.ASSEMBLED, true)
                            .setValue(SteamTurbineControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Steam Turbine]§r 3x3x3 Turbine is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof SteamTurbineControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SteamTurbineControllerBlock.ASSEMBLED, false)
                            .setValue(SteamTurbineControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Steam Turbine]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        Block casing = RefineryMod.TURBINE_CASING.get();
        Block rotor = RefineryMod.TURBINE_ROTOR.get();
        Block controller = RefineryMod.STEAM_TURBINE_CONTROLLER.get();
        Block steamPort = RefineryMod.TURBINE_STEAM_PORT.get();
        Block waterPort = RefineryMod.TURBINE_WATER_PORT.get();
        Block energyPort = RefineryMod.TURBINE_ENERGY_PORT.get();

        // Layer 0: Base
        list.add(new BlueprintRequirement(-1, 0, 0, steamPort, "Turbine Steam Input Port", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Steam Turbine Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, energyPort, "Turbine Energy Output Port", 0xFFF59E0B));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Turbine Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, rotor, "Turbine Rotor Shaft", 0xFFA855F7));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Turbine Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 2, casing, "Turbine Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, waterPort, "Turbine Water Output Port", 0xFF3B82F6));
        list.add(new BlueprintRequirement( 1, 0, 2, casing, "Turbine Casing", 0xFF64748B));

        // Layer 1: Core
        for (int z = 0; z < 3; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(x, 1, z, rotor, "Turbine Rotor Shaft", 0xFFA855F7));
                } else {
                    list.add(new BlueprintRequirement(x, 1, z, casing, "Turbine Casing", 0xFF64748B));
                }
            }
        }

        // Layer 2: Top
        for (int z = 0; z < 3; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(x, 2, z, rotor, "Turbine Rotor Shaft", 0xFFA855F7));
                } else {
                    list.add(new BlueprintRequirement(x, 2, z, casing, "Turbine Casing", 0xFF64748B));
                }
            }
        }

        return list;
    }

    public BlockPos getRelativePos(int localX, int localY, int localZ, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();

        return this.worldPosition
                .relative(right, localX)
                .above(localY)
                .relative(back, localZ);
    }

    public boolean checkStructure(boolean notifyPlayer, @Nullable Player player) {
        if (this.level == null) return false;
        Direction facing = getBlockState().getValue(SteamTurbineControllerBlock.FACING);
        List<BlueprintRequirement> reqs = getRequirements();
        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState state = this.level.getBlockState(targetPos);
            if (state.is(req.expectedBlock)) {
                matched++;
            } else {
                if (missing.size() < 4) {
                    missing.add("Layer " + req.y + ": " + req.name + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Turbine Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing components:"));
                for (String m : missing) {
                    player.sendSystemMessage(Component.literal(" - §f" + m));
                }
                if (reqs.size() - matched > missing.size()) {
                    player.sendSystemMessage(Component.literal(" ... and §e" + (reqs.size() - matched - missing.size()) + "§r more."));
                }
            }
        }

        return matched == reqs.size();
    }

    private void linkPorts() {
        if (this.level == null) return;
        Direction facing = getBlockState().getValue(SteamTurbineControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof TurbinePortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("steam", this.steamAmount);
        output.putInt("water", this.waterAmount);
        output.putInt("energy", this.energy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.steamAmount = input.getIntOr("steam", 0);
        this.waterAmount = input.getIntOr("water", 0);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
    }

    // === MenuProvider Implementation ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.steam_turbine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SteamTurbineMenu(containerId, playerInventory, this.dataAccess);
    }
}
