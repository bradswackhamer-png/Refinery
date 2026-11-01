package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BoilerFireboxBlock;
import com.refinery.block.BoilerPortBlock;
import com.refinery.block.LargeDieselBoilerControllerBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.LargeDieselBoilerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LargeDieselBoilerControllerBlockEntity extends BlockEntity implements IBoilerController, MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 64_000;
    public static final int DIESEL_CONSUMPTION = 25;  // 25 mB diesel per cycle
    public static final int WATER_CONSUMPTION = 250;  // 250 mB water per cycle
    public static final int STEAM_PRODUCTION = 650;   // 650 mB high pressure steam per cycle
    public static final int CYCLE_TIME = 10;          // 10 ticks (0.5s) per cycle
    public static final int MAX_OVERHEAT_TICKS = 1200;

    private boolean assembled = false;
    private int dieselAmount = 0;
    private int waterAmount = 0;
    private int steamAmount = 0;
    private int cycleProgress = 0;
    private int overheatTicks = 0;
    private boolean isBurning = false;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    // 4 Slots: Slot 0, 1 = Bucket/Canister Input, Slot 2, 3 = Output Empty Buckets
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> LargeDieselBoilerControllerBlockEntity.this.dieselAmount;
                case 1 -> LargeDieselBoilerControllerBlockEntity.this.waterAmount;
                case 2 -> LargeDieselBoilerControllerBlockEntity.this.steamAmount;
                case 3 -> LargeDieselBoilerControllerBlockEntity.this.isBurning ? 1 : 0;
                case 4 -> LargeDieselBoilerControllerBlockEntity.this.overheatTicks;
                case 5 -> LargeDieselBoilerControllerBlockEntity.this.assembled ? 1 : 0;
                case 6 -> LargeDieselBoilerControllerBlockEntity.this.hasRedstone() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> LargeDieselBoilerControllerBlockEntity.this.dieselAmount = value;
                case 1 -> LargeDieselBoilerControllerBlockEntity.this.waterAmount = value;
                case 2 -> LargeDieselBoilerControllerBlockEntity.this.steamAmount = value;
                case 3 -> LargeDieselBoilerControllerBlockEntity.this.isBurning = (value != 0);
                case 4 -> LargeDieselBoilerControllerBlockEntity.this.overheatTicks = value;
                case 5 -> LargeDieselBoilerControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public LargeDieselBoilerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.LARGE_DIESEL_BOILER_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    public boolean isDieselBoiler() {
        return true;
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
    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    @Override
    public int getDieselAmount() {
        return this.dieselAmount;
    }

    @Override
    public void setDieselAmount(int amt) {
        this.dieselAmount = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    @Override
    public int fillDiesel(int amount) {
        int space = TANK_CAPACITY - this.dieselAmount;
        int toAdd = Math.min(space, amount);
        this.dieselAmount += toAdd;
        if (toAdd > 0) {
            setChanged();
            syncToClient();
        }
        return toAdd;
    }

    @Override
    public int getWaterAmount() {
        return this.waterAmount;
    }

    @Override
    public int getSteamAmount() {
        return this.steamAmount;
    }

    @Override
    public void setWaterAmount(int amt) {
        this.waterAmount = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    @Override
    public void setSteamAmount(int amt) {
        this.steamAmount = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    @Override
    public int fillWater(int amount) {
        int space = TANK_CAPACITY - this.waterAmount;
        int toAdd = Math.min(space, amount);
        this.waterAmount += toAdd;
        if (toAdd > 0) {
            setChanged();
            syncToClient();
        }
        return toAdd;
    }

    @Override
    public int drainSteam(int amount) {
        int toDrain = Math.min(this.steamAmount, amount);
        this.steamAmount -= toDrain;
        if (toDrain > 0) {
            setChanged();
            syncToClient();
        }
        return toDrain;
    }

    @Override
    public Container getFuelContainer() {
        return this;
    }

    @Override
    public boolean canInsertFuel(ItemStack stack) {
        return stack.is(RefineryFluids.DIESEL_BUCKET.get()) || stack.is(Items.WATER_BUCKET);
    }

    private final List<BlockPos> structurePositions = new ArrayList<>();

    public boolean hasRedstone() {
        if (this.level == null) return false;
        if (this.level.hasNeighborSignal(this.worldPosition) || this.level.getBestNeighborSignal(this.worldPosition) > 0) {
            return true;
        }
        if (this.assembled) {
            if (this.structurePositions.isEmpty()) {
                Direction facing = getBlockState().getValue(LargeDieselBoilerControllerBlock.FACING);
                for (BlueprintRequirement req : getRequirements()) {
                    this.structurePositions.add(getRelativePos(req.x(), req.y(), req.z(), facing));
                }
            }
            for (int i = 0; i < this.structurePositions.size(); i++) {
                BlockPos targetPos = this.structurePositions.get(i);
                if (this.level.hasNeighborSignal(targetPos) || this.level.getBestNeighborSignal(targetPos) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void syncToClient() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public void disassemble() {
        this.assembled = false;
        this.structurePositions.clear();
        updateMemberBlockStates(false);
        if (this.level instanceof ServerLevel sl) {
            updateHearthFireboxes(sl, false);
        }
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof LargeDieselBoilerControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(LargeDieselBoilerControllerBlock.ASSEMBLED, false)
                        .setValue(LargeDieselBoilerControllerBlock.ACTIVE, false)
                        .setValue(LargeDieselBoilerControllerBlock.BLUEPRINT, false), 3);
            }
        }
        setChanged();
        syncToClient();
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
                if (current.getBlock() instanceof LargeDieselBoilerControllerBlock && current.getValue(LargeDieselBoilerControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(LargeDieselBoilerControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (this.isBurning) {
                this.isBurning = false;
                setChanged();
                syncToClient();
            }
            return;
        }

        processBuckets();

        boolean wasBurning = this.isBurning;
        boolean wantsToBurn = hasRedstone();

        if (wantsToBurn && this.dieselAmount >= DIESEL_CONSUMPTION && (TANK_CAPACITY - this.steamAmount) >= STEAM_PRODUCTION) {
            if (this.waterAmount >= WATER_CONSUMPTION) {
                this.isBurning = true;
                if (this.overheatTicks > 0) {
                    this.overheatTicks = Math.max(0, this.overheatTicks - 2);
                }

                if (++this.cycleProgress >= CYCLE_TIME) {
                    this.cycleProgress = 0;
                    this.dieselAmount -= DIESEL_CONSUMPTION;
                    this.waterAmount -= WATER_CONSUMPTION;
                    this.steamAmount = Math.min(TANK_CAPACITY, this.steamAmount + STEAM_PRODUCTION);

                    // Dual diesel injection combustion sound & particles
                    Direction facing = state.getValue(LargeDieselBoilerControllerBlock.FACING);
                    BlockPos chamberLeft = getRelativePos(-1, 1, 2, facing);
                    BlockPos chamberRight = getRelativePos(1, 1, 2, facing);

                    serverLevel.playSound(null, chamberLeft, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.8f, 1.2f);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            chamberLeft.getX() + 0.5, chamberLeft.getY() + 1.5, chamberLeft.getZ() + 0.5,
                            3, 0.3, 0.4, 0.3, 0.04);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            chamberRight.getX() + 0.5, chamberRight.getY() + 1.5, chamberRight.getZ() + 0.5,
                            3, 0.3, 0.4, 0.3, 0.04);
                }
                setChanged();
            } else {
                this.isBurning = true;
                this.overheatTicks++;

                // Very loud raid siren alarm countdown
                playOverheatAlarm(serverLevel, pos);

                if (this.overheatTicks % 20 == 0) {
                    serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.2f, 0.5f);
                }
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5,
                        4, 0.4, 0.4, 0.4, 0.08);

                if (this.overheatTicks >= MAX_OVERHEAT_TICKS) {
                    // Massive 5x5 Catastrophic Explosion!
                    serverLevel.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 3.0f, 0.5f);
                    serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 2.5, 12.0f, Level.ExplosionInteraction.BLOCK);
                    disassemble();
                    return;
                }
                setChanged();
            }
        } else {
            this.isBurning = false;
            this.cycleProgress = 0;
            if (this.overheatTicks > 0) {
                this.overheatTicks = Math.max(0, this.overheatTicks - 2);
                setChanged();
            }
        }

        // Update Fireboxes LIT blockstate in the hearth
        updateHearthFireboxes(serverLevel, this.isBurning);

        // Auto-transfer steam to adjacent pipes/handlers without requiring extraction pipes
        if (this.steamAmount > 0) {
            pushSteamFromPos(serverLevel, pos);
            Direction facing = state.getValue(LargeDieselBoilerControllerBlock.FACING);
            BlockPos steamPortPos = getRelativePos(0, 0, 4, facing);
            pushSteamFromPos(serverLevel, steamPortPos);
        }

        if (wasBurning != this.isBurning) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof LargeDieselBoilerControllerBlock) {
                level.setBlock(pos, latest.setValue(LargeDieselBoilerControllerBlock.ACTIVE, this.isBurning), 3);
            }
            syncToClient();
        }
    }

    private void playOverheatAlarm(ServerLevel level, BlockPos pos) {
        // Very loud raid siren alarm during the 60-second countdown to meltdown explosion
        int interval;
        float pitch;
        if (this.overheatTicks >= 900) {
            // Critical final 15 seconds: high-pitch, rapid siren blast every 2 seconds
            interval = 40;
            pitch = 1.2f;
        } else if (this.overheatTicks >= 600) {
            // Urgent 30s-45s: siren blast every 3 seconds
            interval = 60;
            pitch = 1.0f;
        } else {
            // Initial 0s-30s warning: deep ominous siren blast every 4 seconds
            interval = 80;
            pitch = 0.85f;
        }

        if (this.overheatTicks == 1 || this.overheatTicks % interval == 0) {
            level.playSound(null, pos, SoundEvents.RAID_HORN.value(), SoundSource.BLOCKS, 20.0f, pitch);
        }
    }

    private void updateHearthFireboxes(ServerLevel level, boolean lit) {
        Direction facing = getBlockState().getValue(LargeDieselBoilerControllerBlock.FACING);
        for (int x = -1; x <= 1; x++) {
            for (int z = 1; z <= 3; z++) {
                BlockPos fireboxPos = getRelativePos(x, 0, z, facing);
                BlockState state = level.getBlockState(fireboxPos);
                if (state.getBlock() instanceof BoilerFireboxBlock && state.getValue(BoilerFireboxBlock.LIT) != lit) {
                    level.setBlock(fireboxPos, state.setValue(BoilerFireboxBlock.LIT, lit), 3);
                }
            }
        }
    }

    private void pushSteamFromPos(ServerLevel level, BlockPos origin) {
        if (this.steamAmount <= 0) return;
        FluidResource resource = FluidResource.of(RefineryFluids.STEAM_SOURCE.get());

        for (Direction dir : Direction.values()) {
            BlockPos target = origin.relative(dir);
            BlockEntity targetBE = level.getBlockEntity(target);
            if (targetBE instanceof BoilerPortBlockEntity portBE && this.worldPosition.equals(portBE.getControllerPos())) {
                continue;
            }
            if (targetBE instanceof IBoilerController boiler && this.worldPosition.equals(boiler.getPosition())) {
                continue;
            }

            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(this.steamAmount, 1000);
                int inserted = 0;
                try (Transaction tx = Transaction.openRoot()) {
                    for (int i = 0; i < handler.size(); i++) {
                        int space = toPush - inserted;
                        if (space <= 0) break;
                        int ins = handler.insert(i, resource, space, tx);
                        inserted += ins;
                    }
                    if (inserted > 0) {
                        tx.commit();
                        this.steamAmount -= inserted;
                        setChanged();
                        if (this.steamAmount <= 0) break;
                    }
                }
            }
        }
    }

    private void processBuckets() {
        for (int i = 0; i < 2; i++) {
            ItemStack input = this.items.get(i);
            ItemStack output = this.items.get(i + 2);

            if (!input.isEmpty() && (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize()))) {
                if (input.is(RefineryFluids.DIESEL_BUCKET.get()) && (TANK_CAPACITY - this.dieselAmount) >= 1000) {
                    this.dieselAmount += 1000;
                    input.shrink(1);
                    if (output.isEmpty()) {
                        this.items.set(i + 2, new ItemStack(Items.BUCKET));
                    } else {
                        output.grow(1);
                    }
                    setChanged();
                    syncToClient();
                } else if (input.is(Items.WATER_BUCKET) && (TANK_CAPACITY - this.waterAmount) >= 1000) {
                    this.waterAmount += 1000;
                    input.shrink(1);
                    if (output.isEmpty()) {
                        this.items.set(i + 2, new ItemStack(Items.BUCKET));
                    } else {
                        output.grow(1);
                    }
                    setChanged();
                    syncToClient();
                }
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof LargeDieselBoilerControllerBlock)) return;

        updateMemberBlockStates(valid);

        boolean blockAssembled = currentState.getValue(LargeDieselBoilerControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(LargeDieselBoilerControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(LargeDieselBoilerControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 0.8f);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5,
                            1, 0, 0, 0, 0.0);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof LargeDieselBoilerControllerBlock && getBlockState().hasProperty(LargeDieselBoilerControllerBlock.FACING))
                ? getBlockState().getValue(LargeDieselBoilerControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x() == 0 && req.y() == 0 && req.z() == 0) continue;
            BlockPos targetPos = getRelativePos(req.x(), req.y(), req.z(), facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(LargeDieselBoilerControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(LargeDieselBoilerControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(LargeDieselBoilerControllerBlock.ASSEMBLED, isAssembled), 3);
                }
            }
        }
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            updateMemberBlockStates(true);
            this.blueprintProjectionTicks = 0;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof LargeDieselBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(LargeDieselBoilerControllerBlock.ASSEMBLED, true)
                            .setValue(LargeDieselBoilerControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Heavy Diesel Boiler]§r 5x5x5 Boiler is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintProjectionTicks = 600;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof LargeDieselBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(LargeDieselBoilerControllerBlock.ASSEMBLED, false)
                            .setValue(LargeDieselBoilerControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Heavy Diesel Boiler]§r 5x5x5 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(125);
        Block casing = RefineryMod.STEEL_BOILER_CASING.get();
        Block pipe = RefineryMod.BOILER_PIPE.get();
        Block firebox = RefineryMod.BOILER_FIREBOX.get();
        Block controller = RefineryMod.LARGE_DIESEL_BOILER_CONTROLLER.get();
        Block waterPort = RefineryMod.BOILER_WATER_PORT.get();
        Block steamPort = RefineryMod.BOILER_STEAM_PORT.get();
        Block fuelPort = RefineryMod.BOILER_FUEL_PORT.get();

        // Layer 0: Base / Hearth (5x5)
        for (int z = 0; z < 5; z++) {
            for (int x = -2; x <= 2; x++) {
                if (x == 0 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, controller, "Large Diesel Boiler Controller", 0xFFEAB308));
                } else if (x == -1 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, waterPort, "Boiler Water Input Port", 0xFF3B82F6));
                } else if (x == 1 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, fuelPort, "Boiler Diesel Input Port", 0xFFF59E0B));
                } else if (x == 0 && z == 4) {
                    list.add(new BlueprintRequirement(x, 0, z, steamPort, "Boiler Steam Output Port", 0xFF38BDF8));
                } else if (x >= -1 && x <= 1 && z >= 1 && z <= 3) {
                    list.add(new BlueprintRequirement(x, 0, z, firebox, "Diesel Burner Firebox", 0xFFEF4444));
                } else {
                    list.add(new BlueprintRequirement(x, 0, z, casing, "Steel Boiler Casing", 0xFF64748B));
                }
            }
        }

        // Layer 1, 2, 3: Pressure Pipe core (3x3) & Outer Casing Walls
        for (int y = 1; y <= 3; y++) {
            for (int z = 0; z < 5; z++) {
                for (int x = -2; x <= 2; x++) {
                    if (x >= -1 && x <= 1 && z >= 1 && z <= 3) {
                        list.add(new BlueprintRequirement(x, y, z, pipe, "Boiler Pressure Pipe", 0xFF94A3B8));
                    } else {
                        list.add(new BlueprintRequirement(x, y, z, casing, "Steel Boiler Casing", 0xFF64748B));
                    }
                }
            }
        }

        // Layer 4: Roof (5x5)
        for (int z = 0; z < 5; z++) {
            for (int x = -2; x <= 2; x++) {
                list.add(new BlueprintRequirement(x, 4, z, casing, "Steel Boiler Casing", 0xFF64748B));
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
        Direction facing = getBlockState().getValue(LargeDieselBoilerControllerBlock.FACING);
        List<BlueprintRequirement> reqs = getRequirements();
        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = getRelativePos(req.x(), req.y(), req.z(), facing);
            BlockState state = this.level.getBlockState(targetPos);
            if (state.is(req.expectedBlock())) {
                matched++;
            } else {
                if (missing.size() < 4) {
                    missing.add("Layer " + req.y() + ": " + req.name() + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Heavy Diesel Boiler Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing components:"));
                for (String m : missing) {
                    player.sendSystemMessage(Component.literal(" - §f" + m));
                }
            }
        }

        return matched == reqs.size();
    }

    private void linkPorts() {
        if (this.level == null) return;
        Direction facing = getBlockState().getValue(LargeDieselBoilerControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x(), req.y(), req.z(), facing);
            if (this.level.getBlockEntity(targetPos) instanceof BoilerPortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("diesel", this.dieselAmount);
        output.putInt("water", this.waterAmount);
        output.putInt("steam", this.steamAmount);
        output.putInt("overheatTicks", this.overheatTicks);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.dieselAmount = input.getIntOr("diesel", 0);
        this.waterAmount = input.getIntOr("water", 0);
        this.steamAmount = input.getIntOr("steam", 0);
        this.overheatTicks = input.getIntOr("overheatTicks", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === Container Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return (slot == 0 || slot == 1) && canInsertFuel(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 2 || slot == 3;
    }

    @Override
    public int getContainerSize() {
        return 4;
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
        if (!result.isEmpty()) {
            setChanged();
            syncToClient();
        }
        return result;
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
        syncToClient();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return (slot == 0 || slot == 1) && canInsertFuel(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        syncToClient();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.large_diesel_boiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LargeDieselBoilerMenu(containerId, playerInventory, this, this.dataAccess);
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
