package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BoilerFireboxBlock;
import com.refinery.block.BoilerPortBlock;
import com.refinery.block.SmallDieselBoilerControllerBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.SmallDieselBoilerMenu;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmallDieselBoilerControllerBlockEntity extends BlockEntity implements IBoilerController, MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 16_000;
    public static final int DIESEL_CONSUMPTION = 10; // 10 mB diesel per cycle
    public static final int WATER_CONSUMPTION = 100; // 100 mB water per cycle
    public static final int STEAM_PRODUCTION = 250;  // 250 mB high pressure steam per cycle
    public static final int CYCLE_TIME = 10;         // 10 ticks (0.5 seconds) per cycle
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

    // Slot 0: Bucket Input, Slot 1: Bucket Output
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> SmallDieselBoilerControllerBlockEntity.this.dieselAmount;
                case 1 -> SmallDieselBoilerControllerBlockEntity.this.waterAmount;
                case 2 -> SmallDieselBoilerControllerBlockEntity.this.steamAmount;
                case 3 -> SmallDieselBoilerControllerBlockEntity.this.isBurning ? 1 : 0;
                case 4 -> SmallDieselBoilerControllerBlockEntity.this.overheatTicks;
                case 5 -> SmallDieselBoilerControllerBlockEntity.this.assembled ? 1 : 0;
                case 6 -> SmallDieselBoilerControllerBlockEntity.this.hasRedstone() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> SmallDieselBoilerControllerBlockEntity.this.dieselAmount = value;
                case 1 -> SmallDieselBoilerControllerBlockEntity.this.waterAmount = value;
                case 2 -> SmallDieselBoilerControllerBlockEntity.this.steamAmount = value;
                case 3 -> SmallDieselBoilerControllerBlockEntity.this.isBurning = (value != 0);
                case 4 -> SmallDieselBoilerControllerBlockEntity.this.overheatTicks = value;
                case 5 -> SmallDieselBoilerControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public SmallDieselBoilerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER_BE.get(), pos, state);
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

    public boolean hasRedstone() {
        if (this.level == null) return false;
        return this.level.hasNeighborSignal(this.worldPosition);
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
        updateMemberBlockStates(false);
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof SmallDieselBoilerControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(SmallDieselBoilerControllerBlock.ASSEMBLED, false)
                        .setValue(SmallDieselBoilerControllerBlock.ACTIVE, false)
                        .setValue(SmallDieselBoilerControllerBlock.BLUEPRINT, false), 3);
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
                if (current.getBlock() instanceof SmallDieselBoilerControllerBlock && current.getValue(SmallDieselBoilerControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(SmallDieselBoilerControllerBlock.BLUEPRINT, false), 3);
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

        // Bucket Processing in GUI slots
        processBuckets();

        boolean wasBurning = this.isBurning;
        boolean wantsToBurn = hasRedstone();

        if (wantsToBurn && this.dieselAmount >= DIESEL_CONSUMPTION && (TANK_CAPACITY - this.steamAmount) >= STEAM_PRODUCTION) {
            if (this.waterAmount >= WATER_CONSUMPTION) {
                // Normal steam generation cycle
                this.isBurning = true;
                if (this.overheatTicks > 0) {
                    this.overheatTicks = Math.max(0, this.overheatTicks - 2);
                }

                if (++this.cycleProgress >= CYCLE_TIME) {
                    this.cycleProgress = 0;
                    this.dieselAmount -= DIESEL_CONSUMPTION;
                    this.waterAmount -= WATER_CONSUMPTION;
                    this.steamAmount = Math.min(TANK_CAPACITY, this.steamAmount + STEAM_PRODUCTION);

                    // Diesel injection burner hum & steam hiss
                    Direction facing = state.getValue(SmallDieselBoilerControllerBlock.FACING);
                    BlockPos chamberPos = getRelativePos(0, 1, 1, facing);
                    serverLevel.playSound(null, chamberPos, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.6f, 1.3f);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 1.2, chamberPos.getZ() + 0.5,
                            3, 0.2, 0.3, 0.2, 0.03);
                }
                setChanged();
            } else {
                // Running dry without water -> OVERHEAT
                this.isBurning = true;
                this.overheatTicks++;

                if (this.overheatTicks % 20 == 0) {
                    serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.6f);
                }

                if (this.overheatTicks >= MAX_OVERHEAT_TICKS) {
                    // Catastrophic Boiler Explosion!
                    serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 7.0f, Level.ExplosionInteraction.BLOCK);
                    disassemble();
                    return;
                }
                setChanged();
            }
        } else {
            this.isBurning = false;
            this.cycleProgress = 0;
            if (this.overheatTicks > 0) {
                this.overheatTicks--;
                setChanged();
            }
        }

        // Auto-transfer steam to adjacent pipes/handlers without requiring extraction pipes
        if (this.steamAmount > 0) {
            pushSteamFromPos(serverLevel, pos);
            Direction facing = state.getValue(SmallDieselBoilerControllerBlock.FACING);
            BlockPos steamPortPos = getRelativePos(0, 0, 2, facing);
            pushSteamFromPos(serverLevel, steamPortPos);
        }

        if (wasBurning != this.isBurning) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof SmallDieselBoilerControllerBlock) {
                level.setBlock(pos, latest.setValue(SmallDieselBoilerControllerBlock.ACTIVE, this.isBurning), 3);
            }
            syncToClient();
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
        ItemStack input = this.items.get(0);
        ItemStack output = this.items.get(1);

        if (!input.isEmpty() && (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize()))) {
            if (input.is(RefineryFluids.DIESEL_BUCKET.get()) && (TANK_CAPACITY - this.dieselAmount) >= 1000) {
                this.dieselAmount += 1000;
                input.shrink(1);
                if (output.isEmpty()) {
                    this.items.set(1, new ItemStack(Items.BUCKET));
                } else {
                    output.grow(1);
                }
                setChanged();
                syncToClient();
            } else if (input.is(Items.WATER_BUCKET) && (TANK_CAPACITY - this.waterAmount) >= 1000) {
                this.waterAmount += 1000;
                input.shrink(1);
                if (output.isEmpty()) {
                    this.items.set(1, new ItemStack(Items.BUCKET));
                } else {
                    output.grow(1);
                }
                setChanged();
                syncToClient();
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof SmallDieselBoilerControllerBlock)) return;

        updateMemberBlockStates(valid);

        boolean blockAssembled = currentState.getValue(SmallDieselBoilerControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(SmallDieselBoilerControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(SmallDieselBoilerControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5,
                            6, 0.4, 0.4, 0.4, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof SmallDieselBoilerControllerBlock && getBlockState().hasProperty(SmallDieselBoilerControllerBlock.FACING))
                ? getBlockState().getValue(SmallDieselBoilerControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x() == 0 && req.y() == 0 && req.z() == 0) continue;
            BlockPos targetPos = getRelativePos(req.x(), req.y(), req.z(), facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(SmallDieselBoilerControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(SmallDieselBoilerControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(SmallDieselBoilerControllerBlock.ASSEMBLED, isAssembled), 3);
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
                if (current.getBlock() instanceof SmallDieselBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SmallDieselBoilerControllerBlock.ASSEMBLED, true)
                            .setValue(SmallDieselBoilerControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Diesel Boiler]§r 3x3x3 Machine is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintProjectionTicks = 600;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof SmallDieselBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SmallDieselBoilerControllerBlock.ASSEMBLED, false)
                            .setValue(SmallDieselBoilerControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Diesel Boiler]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        Block casing = RefineryMod.STEEL_BOILER_CASING.get();
        Block pipe = RefineryMod.BOILER_PIPE.get();
        Block firebox = RefineryMod.BOILER_FIREBOX.get();
        Block controller = RefineryMod.SMALL_DIESEL_BOILER_CONTROLLER.get();
        Block waterPort = RefineryMod.BOILER_WATER_PORT.get();
        Block steamPort = RefineryMod.BOILER_STEAM_PORT.get();
        Block fuelPort = RefineryMod.BOILER_FUEL_PORT.get();

        // Layer 0: Hearth & Ports
        list.add(new BlueprintRequirement(-1, 0, 0, waterPort, "Boiler Water Input Port", 0xFF3B82F6));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Small Diesel Boiler Controller", 0xFFEAB308));
        list.add(new BlueprintRequirement( 1, 0, 0, fuelPort, "Boiler Diesel Input Port", 0xFFF59E0B));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, firebox, "Diesel Burner Firebox", 0xFFEF4444));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 2, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, steamPort, "Boiler Steam Output Port", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 1, 0, 2, casing, "Steel Boiler Casing", 0xFF64748B));

        // Layer 1: Heat Chamber & Pressure Pipes
        for (int z = 0; z < 3; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(x, 1, z, pipe, "Boiler Pressure Pipe", 0xFF94A3B8));
                } else {
                    list.add(new BlueprintRequirement(x, 1, z, casing, "Steel Boiler Casing", 0xFF64748B));
                }
            }
        }

        // Layer 2: Roof
        for (int z = 0; z < 3; z++) {
            for (int x = -1; x <= 1; x++) {
                list.add(new BlueprintRequirement(x, 2, z, casing, "Steel Boiler Casing", 0xFF64748B));
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
        Direction facing = getBlockState().getValue(SmallDieselBoilerControllerBlock.FACING);
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
            player.sendSystemMessage(Component.literal("§e[Diesel Boiler Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
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
        Direction facing = getBlockState().getValue(SmallDieselBoilerControllerBlock.FACING);
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
        return new int[]{0, 1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 0 && canInsertFuel(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1;
    }

    @Override
    public int getContainerSize() {
        return 2;
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
        return slot == 0 && canInsertFuel(stack);
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
        return Component.translatable("container.refinery.small_diesel_boiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SmallDieselBoilerMenu(containerId, playerInventory, this, this.dataAccess);
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
