package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.StorageTankCasingBlock;
import com.refinery.block.StorageTankDrainValveBlock;
import com.refinery.block.StorageTankFillValveBlock;
import com.refinery.fluid.GenericFluidTank;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.StorageTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StorageTankBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private boolean assembled = false;
    private StorageTankSize tankSize = StorageTankSize.SIZE_3X3X3;
    private StorageTankSize targetBlueprintSize = StorageTankSize.SIZE_3X3X3;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    private final GenericFluidTank fluidTank = new GenericFluidTank(
            this::getCapacity,
            true,
            true,
            this::onTankChanged
    );

    // 2 item slots: 0 = Input Container, 1 = Output Container
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> StorageTankBlockEntity.this.fluidTank.getAmount();
                case 1 -> StorageTankBlockEntity.this.getCapacity();
                case 2 -> StorageTankBlockEntity.this.assembled ? 1 : 0;
                case 3 -> StorageTankBlockEntity.this.tankSize.ordinal();
                case 4 -> StorageTankBlockEntity.this.targetBlueprintSize.ordinal();
                case 5 -> BuiltInRegistries.FLUID.getId(StorageTankBlockEntity.this.fluidTank.getStoredFluid());
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> StorageTankBlockEntity.this.fluidTank.setAmount(value);
                case 2 -> StorageTankBlockEntity.this.assembled = (value != 0);
                case 3 -> {
                    StorageTankSize[] szs = StorageTankSize.values();
                    if (value >= 0 && value < szs.length) {
                        StorageTankBlockEntity.this.tankSize = szs[value];
                    }
                }
                case 4 -> {
                    StorageTankSize[] szs = StorageTankSize.values();
                    if (value >= 0 && value < szs.length) {
                        StorageTankBlockEntity.this.targetBlueprintSize = szs[value];
                    }
                }
                case 5 -> {
                    Fluid fl = BuiltInRegistries.FLUID.byId(value);
                    if (fl != null) {
                        StorageTankBlockEntity.this.fluidTank.setStoredFluid(fl);
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public StorageTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.STORAGE_TANK_DRAIN_VALVE_BE.get(), pos, blockState);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public StorageTankSize getTankSize() {
        return this.tankSize;
    }

    public StorageTankSize getTargetBlueprintSize() {
        return this.targetBlueprintSize;
    }

    public int getCapacity() {
        return this.assembled ? this.tankSize.getCapacity() : this.targetBlueprintSize.getCapacity();
    }

    public GenericFluidTank getFluidTank() {
        return this.fluidTank;
    }

    private void onTankChanged() {
        setChanged();
        syncToClient();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // 1. Periodic structure check (every 1 second)
        if (--this.structureCheckCooldown <= 0) {
            this.structureCheckCooldown = 20;
            syncAssembly();
        }

        // 2. Blueprint timer
        if (this.blueprintProjectionTicks > 0) {
            this.blueprintProjectionTicks--;
            if (this.blueprintProjectionTicks == 0) {
                BlockState cur = getBlockState();
                if (cur.getBlock() instanceof StorageTankDrainValveBlock && cur.getValue(StorageTankDrainValveBlock.BLUEPRINT)) {
                    level.setBlock(pos, cur.setValue(StorageTankDrainValveBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) return;

        // 3. Process Bucket / Container slots
        processContainerSlots();

        // 4. Auto-Push into adjacent pipes & handlers (no extract pipe needed!)
        if (this.fluidTank.getAmount() > 0) {
            pushFluidToNeighbors(serverLevel, pos);
        }
    }

    private void processContainerSlots() {
        ItemStack input = this.items.get(0);
        if (input.isEmpty()) return;

        ItemStack output = this.items.get(1);

        // Fluid Bucket -> Tank (Empty bucket)
        if (input.is(Items.WATER_BUCKET) && canAcceptFluid(Fluids.WATER, 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(Fluids.WATER, 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        } else if (input.is(Items.LAVA_BUCKET) && canAcceptFluid(Fluids.LAVA, 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(Fluids.LAVA, 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        } else if (input.is(RefineryFluids.CRUDE_OIL_BUCKET.get()) && canAcceptFluid(RefineryFluids.CRUDE_OIL_SOURCE.get(), 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(RefineryFluids.CRUDE_OIL_SOURCE.get(), 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        } else if (input.is(RefineryFluids.DIESEL_BUCKET.get()) && canAcceptFluid(RefineryFluids.DIESEL_SOURCE.get(), 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(RefineryFluids.DIESEL_SOURCE.get(), 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        } else if (input.is(RefineryFluids.GASOLINE_BUCKET.get()) && canAcceptFluid(RefineryFluids.GASOLINE_SOURCE.get(), 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(RefineryFluids.GASOLINE_SOURCE.get(), 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        } else if (input.is(RefineryFluids.ETHANOL_BUCKET.get()) && canAcceptFluid(RefineryFluids.ETHANOL_SOURCE.get(), 1000)) {
            if (output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize())) {
                insertFluidInternal(RefineryFluids.ETHANOL_SOURCE.get(), 1000);
                input.shrink(1);
                addOutput(new ItemStack(Items.BUCKET));
            }
        }
        // Empty Bucket -> Fill from Tank
        else if (input.is(Items.BUCKET) && this.fluidTank.getAmount() >= 1000) {
            ItemStack filled = getBucketForFluid(this.fluidTank.getStoredFluid());
            if (!filled.isEmpty() && (output.isEmpty() || (ItemStack.isSameItemSameComponents(output, filled) && output.getCount() < output.getMaxStackSize()))) {
                this.fluidTank.setAmount(this.fluidTank.getAmount() - 1000);
                input.shrink(1);
                addOutput(filled);
            }
        }
    }

    private boolean canAcceptFluid(Fluid fluid, int amt) {
        if (this.fluidTank.getAmount() <= 0 || this.fluidTank.getStoredFluid() == Fluids.EMPTY) return true;
        return this.fluidTank.getStoredFluid() == fluid && (getCapacity() - this.fluidTank.getAmount()) >= amt;
    }

    private void insertFluidInternal(Fluid fluid, int amt) {
        if (this.fluidTank.getAmount() <= 0 || this.fluidTank.getStoredFluid() == Fluids.EMPTY) {
            this.fluidTank.setStoredFluid(fluid);
        }
        this.fluidTank.setAmount(this.fluidTank.getAmount() + amt);
    }

    private void addOutput(ItemStack stack) {
        ItemStack current = this.items.get(1);
        if (current.isEmpty()) {
            this.items.set(1, stack);
        } else if (ItemStack.isSameItemSameComponents(current, stack)) {
            current.grow(stack.getCount());
        }
        setChanged();
    }

    public static ItemStack getBucketForFluid(Fluid fluid) {
        if (fluid == Fluids.WATER) return new ItemStack(Items.WATER_BUCKET);
        if (fluid == Fluids.LAVA) return new ItemStack(Items.LAVA_BUCKET);
        if (fluid == RefineryFluids.CRUDE_OIL_SOURCE.get()) return new ItemStack(RefineryFluids.CRUDE_OIL_BUCKET.get());
        if (fluid == RefineryFluids.DIESEL_SOURCE.get()) return new ItemStack(RefineryFluids.DIESEL_BUCKET.get());
        if (fluid == RefineryFluids.GASOLINE_SOURCE.get()) return new ItemStack(RefineryFluids.GASOLINE_BUCKET.get());
        if (fluid == RefineryFluids.ETHANOL_SOURCE.get()) return new ItemStack(RefineryFluids.ETHANOL_BUCKET.get());
        return ItemStack.EMPTY;
    }

    /**
     * Auto-pushes fluid into adjacent fluid handlers or regular liquid pipes!
     * When attaching a regular fluid pipe beneath or adjacent to the drain valve,
     * fluid flows directly into the pipe without requiring an extract pipe.
     */
    protected void pushFluidToNeighbors(ServerLevel level, BlockPos pos) {
        Fluid stored = this.fluidTank.getStoredFluid();
        if (stored == Fluids.EMPTY || this.fluidTank.getAmount() <= 0) return;

        FluidResource resource = FluidResource.of(stored);

        // Prioritize pushing downward into pipes underneath the tank floor, then horizontal sides
        Direction[] dirs = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction dir : dirs) {
            BlockPos target = pos.relative(dir);
            var handler = level.getCapability(Capabilities.Fluid.BLOCK, target, dir.getOpposite());
            if (handler != null) {
                int toPush = Math.min(this.fluidTank.getAmount(), 1000); // Up to 1000 mB/t
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
                        this.fluidTank.setAmount(this.fluidTank.getAmount() - inserted);
                        if (this.fluidTank.getAmount() <= 0) break;
                    }
                }
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;

        StorageTankSize matched = detectValidStructure();
        boolean valid = (matched != null);

        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof StorageTankDrainValveBlock)) return;

        boolean blockAssembled = currentState.getValue(StorageTankDrainValveBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid || (valid && this.tankSize != matched)) {
            this.assembled = valid;
            if (valid) {
                this.tankSize = matched;
                this.targetBlueprintSize = matched;
                this.blueprintProjectionTicks = 0;
            }

            updateMemberBlockStates(valid);

            BlockState newState = currentState.setValue(StorageTankDrainValveBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(StorageTankDrainValveBlock.BLUEPRINT, false);
                linkFillValve();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.9f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5,
                            8, 0.4, 0.4, 0.4, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void disassemble() {
        if (this.assembled) {
            this.assembled = false;
            updateMemberBlockStates(false);
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof StorageTankDrainValveBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(StorageTankDrainValveBlock.ASSEMBLED, false)
                            .setValue(StorageTankDrainValveBlock.BLUEPRINT, false), 3);
                }
            }
            setChanged();
            syncToClient();
        }
    }

    public void linkFillValve() {
        if (this.level == null || !this.assembled) return;
        BlockPos fillPos = this.worldPosition.above(this.tankSize.getHeight() - 1);
        if (this.level.getBlockEntity(fillPos) instanceof StorageTankFillValveBlockEntity fillBE) {
            fillBE.setControllerPos(this.worldPosition);
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        StorageTankSize size = this.assembled ? this.tankSize : this.targetBlueprintSize;

        for (BlueprintRequirement req : getRequirements(size)) {
            if (req.isAir() || (req.x() == 0 && req.y() == 0 && req.z() == 0)) continue;
            BlockPos targetPos = this.worldPosition.offset(req.x(), req.y(), req.z());
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(StorageTankCasingBlock.ASSEMBLED)) {
                if (targetState.getValue(StorageTankCasingBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(StorageTankCasingBlock.ASSEMBLED, isAssembled), 3);
                }
            }
        }
    }

    public @Nullable StorageTankSize detectValidStructure() {
        if (this.level == null) return null;

        // Test all sizes in descending order of size / height
        StorageTankSize[] testOrder = {
                StorageTankSize.SIZE_5X5X9,
                StorageTankSize.SIZE_5X5X7,
                StorageTankSize.SIZE_5X5X5,
                StorageTankSize.SIZE_3X3X9,
                StorageTankSize.SIZE_3X3X6,
                StorageTankSize.SIZE_3X3X3
        };

        for (StorageTankSize size : testOrder) {
            if (matchesStructure(size)) {
                return size;
            }
        }
        return null;
    }

    public boolean matchesStructure(StorageTankSize size) {
        if (this.level == null) return false;
        List<BlueprintRequirement> reqs = getRequirements(size);

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = this.worldPosition.offset(req.x(), req.y(), req.z());
            BlockState state = this.level.getBlockState(targetPos);

            if (req.isAir()) {
                if (!state.isAir()) return false;
            } else if (req.expectedBlock() != null) {
                if (!state.is(req.expectedBlock())) return false;
            }
        }
        return true;
    }

    public void cycleTargetSize(@Nullable Player player) {
        this.targetBlueprintSize = this.targetBlueprintSize.next();
        this.blueprintProjectionTicks = 600;
        if (this.level != null && !this.level.isClientSide()) {
            BlockState cur = getBlockState();
            if (cur.getBlock() instanceof StorageTankDrainValveBlock) {
                this.level.setBlock(this.worldPosition, cur.setValue(StorageTankDrainValveBlock.BLUEPRINT, true), 3);
            }
        }
        if (player != null) {
            player.sendSystemMessage(Component.literal("§6[Storage Tank]§r Blueprint mode: §b" + this.targetBlueprintSize.getDisplayName() + "§r (" + String.format("%,d", this.targetBlueprintSize.getCapacity()) + " mB)"));
        }
        setChanged();
        syncToClient();
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        StorageTankSize formed = detectValidStructure();
        if (formed != null) {
            this.assembled = true;
            this.tankSize = formed;
            this.targetBlueprintSize = formed;
            this.blueprintProjectionTicks = 0;
            updateMemberBlockStates(true);
            linkFillValve();
            if (this.level != null && !this.level.isClientSide()) {
                BlockState cur = getBlockState();
                if (cur.getBlock() instanceof StorageTankDrainValveBlock) {
                    this.level.setBlock(this.worldPosition, cur.setValue(StorageTankDrainValveBlock.ASSEMBLED, true)
                            .setValue(StorageTankDrainValveBlock.BLUEPRINT, false), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Storage Tank]§r " + formed.getDisplayName() + " is §2VALID & OPERATIONAL§r! Capacity: §e" + String.format("%,d", formed.getCapacity()) + " mB§r."));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState cur = getBlockState();
                if (cur.getBlock() instanceof StorageTankDrainValveBlock) {
                    this.level.setBlock(this.worldPosition, cur.setValue(StorageTankDrainValveBlock.ASSEMBLED, false)
                            .setValue(StorageTankDrainValveBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Storage Tank]§r Projecting blueprint for: §b" + this.targetBlueprintSize.getDisplayName() + "§r."));
                diagnoseMissingBlocks(player, this.targetBlueprintSize);
            }
        }
        setChanged();
        syncToClient();
    }

    private void diagnoseMissingBlocks(Player player, StorageTankSize size) {
        if (this.level == null) return;
        List<BlueprintRequirement> reqs = getRequirements(size);
        int missing = 0;
        List<String> details = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = this.worldPosition.offset(req.x(), req.y(), req.z());
            BlockState st = this.level.getBlockState(targetPos);
            boolean ok = req.isAir() ? st.isAir() : (req.expectedBlock() != null && st.is(req.expectedBlock()));
            if (!ok) {
                missing++;
                if (details.size() < 4) {
                    details.add(req.name() + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (missing > 0) {
            player.sendSystemMessage(Component.literal("§cMissing " + missing + " components:"));
            for (String d : details) {
                player.sendSystemMessage(Component.literal(" - §f" + d));
            }
        }
    }

    public record BlueprintRequirement(int x, int y, int z, @Nullable Block expectedBlock, boolean isAir, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements(StorageTankSize size) {
        int r = size.getRadius();
        int h = size.getHeight();
        List<BlueprintRequirement> list = new ArrayList<>((2 * r + 1) * (2 * r + 1) * h);

        Block casing = RefineryMod.STORAGE_TANK_CASING.get();
        Block drainValve = RefineryMod.STORAGE_TANK_DRAIN_VALVE.get();
        Block fillValve = RefineryMod.STORAGE_TANK_FILL_VALVE.get();

        for (int y = 0; y < h; y++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dx = -r; dx <= r; dx++) {
                    // 1. Bottom Floor Layer (y = 0)
                    if (y == 0) {
                        if (dx == 0 && dz == 0) {
                            list.add(new BlueprintRequirement(dx, y, dz, drainValve, false, "Tank Drain Valve", 0xFF10B981));
                        } else {
                            list.add(new BlueprintRequirement(dx, y, dz, casing, false, "Tank Casing", 0xFF38BDF8));
                        }
                    }
                    // 2. Top Roof Layer (y = h - 1)
                    else if (y == h - 1) {
                        if (dx == 0 && dz == 0) {
                            list.add(new BlueprintRequirement(dx, y, dz, fillValve, false, "Tank Fill Valve", 0xFF3B82F6));
                        } else {
                            list.add(new BlueprintRequirement(dx, y, dz, casing, false, "Tank Casing", 0xFF38BDF8));
                        }
                    }
                    // 3. Middle Wall Layers (y in 1..h-2)
                    else {
                        boolean isEdge = Math.abs(dx) == r || Math.abs(dz) == r;
                        if (isEdge) {
                            list.add(new BlueprintRequirement(dx, y, dz, casing, false, "Tank Casing", 0xFF38BDF8));
                        } else {
                            // Hollow liquid storage chamber
                            list.add(new BlueprintRequirement(dx, y, dz, null, true, "Empty Chamber", 0xFFEA580C));
                        }
                    }
                }
            }
        }

        return list;
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

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putString("tankSize", this.tankSize.name());
        output.putString("targetBlueprintSize", this.targetBlueprintSize.name());
        output.putInt("amount", this.fluidTank.getAmount());
        Fluid fl = this.fluidTank.getStoredFluid();
        if (fl != Fluids.EMPTY) {
            Identifier id = BuiltInRegistries.FLUID.getKey(fl);
            if (id != null) {
                output.putString("fluid", id.toString());
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        String szStr = input.getStringOr("tankSize", StorageTankSize.SIZE_3X3X3.name());
        try {
            this.tankSize = StorageTankSize.valueOf(szStr);
        } catch (Exception e) {
            this.tankSize = StorageTankSize.SIZE_3X3X3;
        }

        String targetStr = input.getStringOr("targetBlueprintSize", StorageTankSize.SIZE_3X3X3.name());
        try {
            this.targetBlueprintSize = StorageTankSize.valueOf(targetStr);
        } catch (Exception e) {
            this.targetBlueprintSize = StorageTankSize.SIZE_3X3X3;
        }

        int amt = input.getIntOr("amount", 0);
        String flStr = input.getStringOr("fluid", "");
        if (!flStr.isEmpty()) {
            Identifier id = Identifier.tryParse(flStr);
            if (id != null) {
                Fluid fl = BuiltInRegistries.FLUID.getOptional(id).orElse(Fluids.EMPTY);
                this.fluidTank.setStoredFluid(fl);
            }
        }
        this.fluidTank.setAmount(amt);
    }

    // === MenuProvider Implementation ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.storage_tank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StorageTankMenu(containerId, playerInventory, this, this.dataAccess);
    }

    // === WorldlyContainer Implementation (Item Slots) ===
    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.items.get(0).isEmpty() && this.items.get(1).isEmpty();
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

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return new int[]{0}; // Insert buckets into top slot
        return new int[]{1}; // Extract empty/filled buckets from bottom/sides
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return index == 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == 1;
    }
}
