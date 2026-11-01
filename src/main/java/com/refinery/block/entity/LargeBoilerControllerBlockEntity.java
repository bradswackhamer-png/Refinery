package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BoilerFireboxBlock;
import com.refinery.block.BoilerPortBlock;
import com.refinery.block.LargeBoilerControllerBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.LargeBoilerMenu;
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

public class LargeBoilerControllerBlockEntity extends BlockEntity implements IBoilerController, MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 64_000;
    public static final int FUEL_SLOTS_COUNT = 8;
    public static final int WATER_CONSUMPTION = 250;  // 250 mB water per batch
    public static final int STEAM_PRODUCTION = 500;   // 500 mB high pressure steam
    public static final int MAX_OVERHEAT_TICKS = 1200; // 1 minute = 1200 ticks dry run before explosion

    private boolean assembled = false;
    private int waterAmount = 0;
    private int steamAmount = 0;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int overheatTicks = 0;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(FUEL_SLOTS_COUNT, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> LargeBoilerControllerBlockEntity.this.waterAmount;
                case 1 -> LargeBoilerControllerBlockEntity.this.steamAmount;
                case 2 -> LargeBoilerControllerBlockEntity.this.burnTime;
                case 3 -> LargeBoilerControllerBlockEntity.this.maxBurnTime;
                case 4 -> LargeBoilerControllerBlockEntity.this.overheatTicks;
                case 5 -> LargeBoilerControllerBlockEntity.this.assembled ? 1 : 0;
                case 6 -> LargeBoilerControllerBlockEntity.this.hasRedstone() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> LargeBoilerControllerBlockEntity.this.waterAmount = value;
                case 1 -> LargeBoilerControllerBlockEntity.this.steamAmount = value;
                case 2 -> LargeBoilerControllerBlockEntity.this.burnTime = value;
                case 4 -> LargeBoilerControllerBlockEntity.this.overheatTicks = value;
                case 5 -> LargeBoilerControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public LargeBoilerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.LARGE_BOILER_CONTROLLER_BE.get(), pos, state);
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
    }

    @Override
    public void setSteamAmount(int amt) {
        this.steamAmount = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
    }

    @Override
    public int fillWater(int amount) {
        int space = TANK_CAPACITY - this.waterAmount;
        int toAdd = Math.min(space, amount);
        this.waterAmount += toAdd;
        if (toAdd > 0) setChanged();
        return toAdd;
    }

    @Override
    public int drainSteam(int amount) {
        int toDrain = Math.min(this.steamAmount, amount);
        this.steamAmount -= toDrain;
        if (toDrain > 0) setChanged();
        return toDrain;
    }

    @Override
    public Container getFuelContainer() {
        return this;
    }

    @Override
    public boolean canInsertFuel(ItemStack stack) {
        return SmallBoilerControllerBlockEntity.getFuelDuration(stack) > 0;
    }

    private final List<BlockPos> structurePositions = new ArrayList<>();

    public boolean hasRedstone() {
        if (this.level == null) return false;
        if (this.level.hasNeighborSignal(this.worldPosition) || this.level.getBestNeighborSignal(this.worldPosition) > 0) {
            return true;
        }
        if (this.assembled) {
            if (this.structurePositions.isEmpty()) {
                Direction facing = getBlockState().getValue(LargeBoilerControllerBlock.FACING);
                for (BlueprintRequirement req : getRequirements()) {
                    this.structurePositions.add(getRelativePos(req.x, req.y, req.z, facing));
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

    public boolean isBurning() {
        return this.burnTime > 0 && hasRedstone();
    }

    public int getOverheatTicks() {
        return this.overheatTicks;
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
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof LargeBoilerControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(LargeBoilerControllerBlock.ASSEMBLED, false)
                        .setValue(LargeBoilerControllerBlock.ACTIVE, false)
                        .setValue(LargeBoilerControllerBlock.BLUEPRINT, false), 3);
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
                if (current.getBlock() instanceof LargeBoilerControllerBlock && current.getValue(LargeBoilerControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(LargeBoilerControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            BlockState current = getBlockState();
            if (current.getBlock() instanceof LargeBoilerControllerBlock && current.getValue(LargeBoilerControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(LargeBoilerControllerBlock.ACTIVE, false), 3);
            }
            this.overheatTicks = 0;
            return;
        }

        boolean redstonePowered = hasRedstone();
        boolean wasActive = state.getValue(LargeBoilerControllerBlock.ACTIVE);
        boolean isActive = false;

        // Fuel Consumption & Combustion (Only powers up if redstone signal is present)
        if (redstonePowered) {
            if (this.burnTime > 0) {
                this.burnTime--;
            } else {
                // Try consuming fuel from inventory slots
                for (int i = 0; i < FUEL_SLOTS_COUNT; i++) {
                    ItemStack fuelStack = this.items.get(i);
                    if (!fuelStack.isEmpty()) {
                        int duration = SmallBoilerControllerBlockEntity.getFuelDuration(fuelStack);
                        if (duration > 0) {
                            this.burnTime = duration;
                            this.maxBurnTime = duration;
                            fuelStack.shrink(1);
                            setChanged();
                            break;
                        }
                    }
                }
            }
        }

        // Active Boiling & Overheat Check (Only operates while redstone signal is present)
        if (redstonePowered && this.burnTime > 0) {
            isActive = true;

            if (this.waterAmount >= WATER_CONSUMPTION && (TANK_CAPACITY - this.steamAmount) >= STEAM_PRODUCTION) {
                // Healthy Steam Generation
                this.waterAmount -= WATER_CONSUMPTION;
                this.steamAmount += STEAM_PRODUCTION;

                // Safely cool down any previous dry-run heat
                if (this.overheatTicks > 0) {
                    this.overheatTicks = Math.max(0, this.overheatTicks - 3);
                }

                // Steam venting particles at top
                if (level.getRandom().nextInt(2) == 0) {
                    Direction facing = state.getValue(LargeBoilerControllerBlock.FACING);
                    BlockPos topCenter = getRelativePos(0, 4, 2, facing);
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            topCenter.getX() + 0.5, topCenter.getY() + 1.2, topCenter.getZ() + 0.5,
                            4, 0.3, 0.1, 0.3, 0.08);
                }
            } else if (this.waterAmount < WATER_CONSUMPTION) {
                // DRY RUNNING WITH NO WATER: DANGER OF MELTDOWN EXPLOSION!
                this.overheatTicks++;

                // Warning hissing sounds & black smoke
                if (this.overheatTicks % 15 == 0) {
                    serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.4f);
                }
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5,
                        4, 0.4, 0.4, 0.4, 0.08);

                if (this.overheatTicks >= MAX_OVERHEAT_TICKS) {
                    // CATASTROPHIC 5X5X5 BOILER EXPLOSION!
                    serverLevel.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 3.0f, 0.5f);
                    serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, 10.0f, Level.ExplosionInteraction.BLOCK);
                    disassemble();
                    return;
                }
            }
            setChanged();
        } else {
            // Idle cooldown
            if (this.overheatTicks > 0) {
                this.overheatTicks = Math.max(0, this.overheatTicks - 2);
                setChanged();
            }
        }

        // Update Firebox LIT blockstate in all 9 hearth fireboxes
        updateHearthFireboxes(serverLevel, isActive);

        // Auto-transfer steam to adjacent pipes/handlers without requiring extraction pipes
        if (this.steamAmount > 0) {
            pushSteamFromPos(serverLevel, pos);
            Direction facing = state.getValue(LargeBoilerControllerBlock.FACING);
            BlockPos steamPortPos = getRelativePos(0, 0, 4, facing);
            pushSteamFromPos(serverLevel, steamPortPos);
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof LargeBoilerControllerBlock) {
                level.setBlock(pos, latest.setValue(LargeBoilerControllerBlock.ACTIVE, isActive), 3);
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

    private void updateHearthFireboxes(ServerLevel level, boolean lit) {
        Direction facing = getBlockState().getValue(LargeBoilerControllerBlock.FACING);
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

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof LargeBoilerControllerBlock)) return;

        updateMemberBlockStates(valid);

        boolean blockAssembled = currentState.getValue(LargeBoilerControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(LargeBoilerControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(LargeBoilerControllerBlock.BLUEPRINT, false);
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
        Direction facing = (getBlockState().getBlock() instanceof LargeBoilerControllerBlock && getBlockState().hasProperty(LargeBoilerControllerBlock.FACING))
                ? getBlockState().getValue(LargeBoilerControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x() == 0 && req.y() == 0 && req.z() == 0) continue;
            BlockPos targetPos = getRelativePos(req.x(), req.y(), req.z(), facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(LargeBoilerControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(LargeBoilerControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(LargeBoilerControllerBlock.ASSEMBLED, isAssembled), 3);
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
                if (current.getBlock() instanceof LargeBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(LargeBoilerControllerBlock.ASSEMBLED, true)
                            .setValue(LargeBoilerControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Heavy Boiler]§r 5x5x5 Boiler is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof LargeBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(LargeBoilerControllerBlock.ASSEMBLED, false)
                            .setValue(LargeBoilerControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Heavy Boiler]§r 5x5x5 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
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
        Block controller = RefineryMod.LARGE_BOILER_CONTROLLER.get();
        Block waterPort = RefineryMod.BOILER_WATER_PORT.get();
        Block steamPort = RefineryMod.BOILER_STEAM_PORT.get();
        Block fuelPort = RefineryMod.BOILER_FUEL_PORT.get();

        // Layer 0: Base / Hearth (5x5)
        for (int z = 0; z < 5; z++) {
            for (int x = -2; x <= 2; x++) {
                if (x == 0 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, controller, "Large Boiler Controller", 0xFF10B981));
                } else if (x == -1 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, waterPort, "Boiler Water Input Port", 0xFF3B82F6));
                } else if (x == 1 && z == 0) {
                    list.add(new BlueprintRequirement(x, 0, z, fuelPort, "Boiler Fuel Input Port", 0xFFEA580C));
                } else if (x == 0 && z == 4) {
                    list.add(new BlueprintRequirement(x, 0, z, steamPort, "Boiler Steam Output Port", 0xFF38BDF8));
                } else if (x >= -1 && x <= 1 && z >= 1 && z <= 3) {
                    list.add(new BlueprintRequirement(x, 0, z, firebox, "Boiler Firebox Hearth", 0xFFEF4444));
                } else {
                    list.add(new BlueprintRequirement(x, 0, z, casing, "Steel Boiler Casing", 0xFF64748B));
                }
            }
        }

        // Layer 1, 2, 3: Core Pressure Piping (3x3 interior) and Outer Casing Walls
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
        Direction facing = getBlockState().getValue(LargeBoilerControllerBlock.FACING);
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
            player.sendSystemMessage(Component.literal("§e[Boiler Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
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
        Direction facing = getBlockState().getValue(LargeBoilerControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof BoilerPortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("water", this.waterAmount);
        output.putInt("steam", this.steamAmount);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
        output.putInt("overheatTicks", this.overheatTicks);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.waterAmount = input.getIntOr("water", 0);
        this.steamAmount = input.getIntOr("steam", 0);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
        this.overheatTicks = input.getIntOr("overheatTicks", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === Container & WorldlyContainer Implementation ===
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canInsertFuel(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return FUEL_SLOTS_COUNT;
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
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return canInsertFuel(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // === MenuProvider Implementation ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.large_boiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LargeBoilerMenu(containerId, playerInventory, this, this.dataAccess);
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
