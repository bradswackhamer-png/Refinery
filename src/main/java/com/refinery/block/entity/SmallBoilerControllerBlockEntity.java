package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BoilerFireboxBlock;
import com.refinery.block.BoilerPortBlock;
import com.refinery.block.SmallBoilerControllerBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.SmallBoilerMenu;
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
import net.minecraft.tags.ItemTags;
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

public class SmallBoilerControllerBlockEntity extends BlockEntity implements IBoilerController, MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 16_000;
    public static final int FUEL_SLOTS_COUNT = 4;
    public static final int WATER_CONSUMPTION = 50;  // 50 mB water per batch
    public static final int STEAM_PRODUCTION = 100; // 100 mB high pressure steam
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
                case 0 -> SmallBoilerControllerBlockEntity.this.waterAmount;
                case 1 -> SmallBoilerControllerBlockEntity.this.steamAmount;
                case 2 -> SmallBoilerControllerBlockEntity.this.burnTime;
                case 3 -> SmallBoilerControllerBlockEntity.this.maxBurnTime;
                case 4 -> SmallBoilerControllerBlockEntity.this.overheatTicks;
                case 5 -> SmallBoilerControllerBlockEntity.this.assembled ? 1 : 0;
                case 6 -> SmallBoilerControllerBlockEntity.this.hasRedstone() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> SmallBoilerControllerBlockEntity.this.waterAmount = value;
                case 1 -> SmallBoilerControllerBlockEntity.this.steamAmount = value;
                case 2 -> SmallBoilerControllerBlockEntity.this.burnTime = value;
                case 4 -> SmallBoilerControllerBlockEntity.this.overheatTicks = value;
                case 5 -> SmallBoilerControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public SmallBoilerControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.SMALL_BOILER_CONTROLLER_BE.get(), pos, state);
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
        return getFuelDuration(stack) > 0;
    }

    public boolean hasRedstone() {
        if (this.level == null) return false;
        return this.level.hasNeighborSignal(this.worldPosition) || this.level.getBestNeighborSignal(this.worldPosition) > 0;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public int getOverheatTicks() {
        return this.overheatTicks;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void disassemble() {
        this.assembled = false;
        updateMemberBlockStates(false);
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof SmallBoilerControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(SmallBoilerControllerBlock.ASSEMBLED, false)
                        .setValue(SmallBoilerControllerBlock.ACTIVE, false)
                        .setValue(SmallBoilerControllerBlock.BLUEPRINT, false), 3);
            }
        }
        setChanged();
        syncToClient();
    }

    private void syncToClient() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
                if (current.getBlock() instanceof SmallBoilerControllerBlock && current.getValue(SmallBoilerControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(SmallBoilerControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            BlockState current = getBlockState();
            if (current.getBlock() instanceof SmallBoilerControllerBlock && current.getValue(SmallBoilerControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(SmallBoilerControllerBlock.ACTIVE, false), 3);
            }
            this.overheatTicks = 0;
            return;
        }

        boolean redstonePowered = hasRedstone();
        boolean wasActive = state.getValue(SmallBoilerControllerBlock.ACTIVE);
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
                        int duration = getFuelDuration(fuelStack);
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

        // Active Boiling & Overheat Check
        if (this.burnTime > 0) {
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
                if (level.getRandom().nextInt(3) == 0) {
                    Direction facing = state.getValue(SmallBoilerControllerBlock.FACING);
                    BlockPos topVents = getRelativePos(0, 2, 1, facing);
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            topVents.getX() + 0.5, topVents.getY() + 1.1, topVents.getZ() + 0.5,
                            2, 0.1, 0.1, 0.1, 0.05);
                }
            } else if (this.waterAmount < WATER_CONSUMPTION) {
                // DRY RUNNING WITH NO WATER: DANGER OF MELTDOWN EXPLOSION!
                this.overheatTicks++;

                // Warning hissing sounds & black smoke
                if (this.overheatTicks % 20 == 0) {
                    serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.6f);
                }
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        2, 0.2, 0.2, 0.2, 0.05);

                if (this.overheatTicks >= MAX_OVERHEAT_TICKS) {
                    // CATASTROPHIC BOILER EXPLOSION!
                    serverLevel.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 2.0f, 0.7f);
                    serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 7.0f, Level.ExplosionInteraction.BLOCK);
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

        // Update Firebox LIT blockstate in the center
        updateFireboxState(serverLevel, isActive);

        // Auto-transfer steam to adjacent pipes/handlers without requiring extraction pipes
        if (this.steamAmount > 0) {
            pushSteamFromPos(serverLevel, pos);
            Direction facing = state.getValue(SmallBoilerControllerBlock.FACING);
            BlockPos steamPortPos = getRelativePos(0, 0, 2, facing);
            pushSteamFromPos(serverLevel, steamPortPos);
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof SmallBoilerControllerBlock) {
                level.setBlock(pos, latest.setValue(SmallBoilerControllerBlock.ACTIVE, isActive), 3);
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

    private void updateFireboxState(ServerLevel level, boolean lit) {
        Direction facing = getBlockState().getValue(SmallBoilerControllerBlock.FACING);
        BlockPos fireboxPos = getRelativePos(0, 0, 1, facing);
        BlockState fireboxState = level.getBlockState(fireboxPos);
        if (fireboxState.getBlock() instanceof BoilerFireboxBlock && fireboxState.getValue(BoilerFireboxBlock.LIT) != lit) {
            level.setBlock(fireboxPos, fireboxState.setValue(BoilerFireboxBlock.LIT, lit), 3);
        }
    }

    public static int getFuelDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(ItemTags.COALS)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(RefineryMod.TAR_ITEM.get())) return 1600;
        if (stack.is(RefineryMod.BITUMEN.get())) return 1600;
        net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && (id.getPath().contains("tar") || id.getPath().contains("bitumen"))) return 1600;
        if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)) return 300;
        return 0;
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof SmallBoilerControllerBlock)) return;

        // Synchronize all 26 member block states with assembly state
        updateMemberBlockStates(valid);

        boolean blockAssembled = currentState.getValue(SmallBoilerControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(SmallBoilerControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(SmallBoilerControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 0.9f);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5,
                            3, 0.3, 0.3, 0.3, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof SmallBoilerControllerBlock && getBlockState().hasProperty(SmallBoilerControllerBlock.FACING))
                ? getBlockState().getValue(SmallBoilerControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x == 0 && req.y == 0 && req.z == 0) continue;
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(SmallBoilerControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(SmallBoilerControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(SmallBoilerControllerBlock.ASSEMBLED, isAssembled), 3);
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
                if (current.getBlock() instanceof SmallBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SmallBoilerControllerBlock.ASSEMBLED, true)
                            .setValue(SmallBoilerControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Steam Boiler]§r 3x3x3 Boiler is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof SmallBoilerControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(SmallBoilerControllerBlock.ASSEMBLED, false)
                            .setValue(SmallBoilerControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Steam Boiler]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
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
        Block controller = RefineryMod.SMALL_BOILER_CONTROLLER.get();
        Block waterPort = RefineryMod.BOILER_WATER_PORT.get();
        Block steamPort = RefineryMod.BOILER_STEAM_PORT.get();
        Block fuelPort = RefineryMod.BOILER_FUEL_PORT.get();

        // Layer 0: Hearth & Bottom
        list.add(new BlueprintRequirement(-1, 0, 0, waterPort, "Boiler Water Input Port", 0xFF3B82F6));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Small Boiler Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, fuelPort, "Boiler Fuel Input Port", 0xFFEA580C));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, firebox, "Boiler Firebox Hearth", 0xFFEF4444));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 2, casing, "Steel Boiler Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, steamPort, "Boiler Steam Output Port", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 1, 0, 2, casing, "Steel Boiler Casing", 0xFF64748B));

        // Layer 1: Heat Chamber & Boiler Pipes
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
        Direction facing = getBlockState().getValue(SmallBoilerControllerBlock.FACING);
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
        Direction facing = getBlockState().getValue(SmallBoilerControllerBlock.FACING);
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
    private static final int[] SLOTS = new int[]{0, 1, 2, 3};

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
        return Component.translatable("container.refinery.small_boiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SmallBoilerMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
