package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.GrinderPortBlock;
import com.refinery.block.IndustrialGrinderControllerBlock;
import com.refinery.menu.IndustrialGrinderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IndustrialGrinderControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 8; // 4 input slots (0..3), 4 output slots (4..7)
    public static final int ENERGY_CAPACITY = 50_000;
    public static final int ENERGY_PER_TICK = 30;
    public static final int PROCESS_TIME = 60; // 3 seconds per grind cycle

    private boolean assembled = false;
    private int energy = 0;
    private int progress = 0;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1_000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            IndustrialGrinderControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            IndustrialGrinderControllerBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> IndustrialGrinderControllerBlockEntity.this.progress;
                case 1 -> PROCESS_TIME;
                case 2 -> IndustrialGrinderControllerBlockEntity.this.energy & 0xFFFF;
                case 3 -> (IndustrialGrinderControllerBlockEntity.this.energy >> 16) & 0xFFFF;
                case 4 -> IndustrialGrinderControllerBlockEntity.this.assembled ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> IndustrialGrinderControllerBlockEntity.this.progress = value;
                case 4 -> IndustrialGrinderControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public IndustrialGrinderControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER_BE.get(), pos, state);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getEnergy() {
        return this.energy;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void disassemble() {
        if (this.assembled) {
            this.assembled = false;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof IndustrialGrinderControllerBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(IndustrialGrinderControllerBlock.ASSEMBLED, false)
                            .setValue(IndustrialGrinderControllerBlock.ACTIVE, false)
                            .setValue(IndustrialGrinderControllerBlock.BLUEPRINT, false), 3);
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
                if (current.getBlock() instanceof IndustrialGrinderControllerBlock && current.getValue(IndustrialGrinderControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(IndustrialGrinderControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
            BlockState current = getBlockState();
            if (current.getBlock() instanceof IndustrialGrinderControllerBlock && current.getValue(IndustrialGrinderControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(IndustrialGrinderControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(IndustrialGrinderControllerBlock.ACTIVE);
        boolean isActive = false;

        // Check if we have an item to grind
        int inputSlot = getFirstGrindableSlot();
        if (inputSlot != -1) {
            ItemStack input = this.items.get(inputSlot);
            ItemStack output = getGrindResult(input);

            if (!output.isEmpty() && canFitOutput(output) && this.energy >= ENERGY_PER_TICK) {
                isActive = true;
                this.energy -= ENERGY_PER_TICK;
                this.energyStorage.set(this.energy);
                this.progress++;

                // Grinding rumble & crunch sounds
                if (this.progress % 12 == 1) {
                    Direction facing = state.getValue(IndustrialGrinderControllerBlock.FACING);
                    BlockPos chamberPos = getRelativePos(1, 1, 0, facing);
                    serverLevel.playSound(null, chamberPos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.7f, 0.9f);
                    serverLevel.playSound(null, chamberPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 0.5f, 0.8f);

                    // Sparks & pulverizing dust particles in chamber
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 0.5, chamberPos.getZ() + 0.5,
                            4, 0.2, 0.2, 0.2, 0.05);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 0.5, chamberPos.getZ() + 0.5,
                            2, 0.1, 0.1, 0.1, 0.02);
                }

                if (this.progress >= PROCESS_TIME) {
                    this.progress = 0;
                    boolean isOilSand = isOilSand(input);
                    input.shrink(1);
                    addOutput(output.copy());

                    // Oil Sand has 50% chance to yield 1 Petroleum Tar
                    if (isOilSand && serverLevel.getRandom().nextFloat() < 0.50f) {
                        ItemStack tar = new ItemStack(getPreferredTarItem(), 1);
                        if (canFitOutput(tar)) {
                            addOutput(tar);
                        }
                    }

                    // Ejection sound
                    Direction facing = state.getValue(IndustrialGrinderControllerBlock.FACING);
                    BlockPos portPos = getRelativePos(1, 0, 0, facing);
                    serverLevel.playSound(null, portPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                }
                setChanged();
            } else {
                if (this.progress > 0) {
                    this.progress = 0;
                    setChanged();
                }
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof IndustrialGrinderControllerBlock) {
                level.setBlock(pos, latest.setValue(IndustrialGrinderControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    public static ItemStack getGrindResult(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;

        // 1. Raw Ores (2x Dust)
        if (input.is(Items.RAW_IRON) || input.is(Items.IRON_ORE) || input.is(Items.DEEPSLATE_IRON_ORE)) {
            return new ItemStack(RefineryMod.IRON_DUST.get(), 2);
        }
        if (input.is(Items.RAW_GOLD) || input.is(Items.GOLD_ORE) || input.is(Items.DEEPSLATE_GOLD_ORE)) {
            return new ItemStack(RefineryMod.GOLD_DUST.get(), 2);
        }
        if (input.is(Items.RAW_COPPER) || input.is(Items.COPPER_ORE) || input.is(Items.DEEPSLATE_COPPER_ORE)) {
            return new ItemStack(RefineryMod.COPPER_DUST.get(), 2);
        }

        // 2. Stone / Cobblestone variants -> Gravel
        if (input.is(Items.COBBLESTONE) || input.is(Items.STONE) ||
            input.is(Items.COBBLED_DEEPSLATE) || input.is(Items.DEEPSLATE) ||
            input.is(Items.ANDESITE) || input.is(Items.DIORITE) || input.is(Items.GRANITE)) {
            return new ItemStack(Items.GRAVEL, 1);
        }

        // 3. Gravel -> Sand
        if (input.is(Items.GRAVEL)) {
            return new ItemStack(Items.SAND, 1);
        }

        // 4. Ingots -> 1x Dust
        if (input.is(Items.IRON_INGOT)) {
            return new ItemStack(RefineryMod.IRON_DUST.get(), 1);
        }
        if (input.is(Items.GOLD_INGOT)) {
            return new ItemStack(RefineryMod.GOLD_DUST.get(), 1);
        }
        if (input.is(Items.COPPER_INGOT)) {
            return new ItemStack(RefineryMod.COPPER_DUST.get(), 1);
        }

        // 5. Oil Sand (Refinery or Logistics mod: oil_sand, oil_red_sand, oil_shale) -> 3x Bitumen (+ 50% chance for 1x Tar)
        if (isOilSand(input)) {
            return new ItemStack(getPreferredBitumenItem(), 3);
        }

        return ItemStack.EMPTY;
    }

    public static Item getPreferredTarItem() {
        Optional<Item> logTar = BuiltInRegistries.ITEM.getOptional(Identifier.fromNamespaceAndPath("logistics", "core/tar"));
        return logTar.orElseGet(RefineryMod.TAR_ITEM::get);
    }

    public static Item getPreferredBitumenItem() {
        Optional<Item> logBitumen = BuiltInRegistries.ITEM.getOptional(Identifier.fromNamespaceAndPath("logistics", "core/bitumen"));
        return logBitumen.orElseGet(RefineryMod.BITUMEN::get);
    }

    public static final TagKey<Item> OIL_SANDS_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "oil_sands"));

    public static boolean isOilSand(ItemStack input) {
        if (input.isEmpty()) return false;
        if (input.is(RefineryMod.OIL_SAND_ITEM.get())) return true;
        if (input.is(OIL_SANDS_TAG)) return true;

        // Logistics mod compatibility (logistics:core/oil_sand, logistics:core/oil_red_sand, logistics:core/oil_shale)
        Identifier id = BuiltInRegistries.ITEM.getKey(input.getItem());
        if (id != null) {
            String path = id.getPath();
            String namespace = id.getNamespace();
            if (namespace.equals("logistics")) {
                if (path.contains("oil_sand") || path.contains("oil_red_sand") || path.contains("oil_shale")) {
                    return true;
                }
            } else if (path.equals("oil_sand") || path.equals("oil_red_sand") || path.equals("oil_shale")) {
                return true;
            }
        }
        return false;
    }

    private int getFirstGrindableSlot() {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && !getGrindResult(stack).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private boolean canFitOutput(ItemStack result) {
        int countRemaining = result.getCount();
        for (int i = 4; i < 8; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(stack, result)) {
                int space = stack.getMaxStackSize() - stack.getCount();
                countRemaining -= space;
                if (countRemaining <= 0) return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int i = 4; i < 8; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                this.items.set(i, result);
                setChanged();
                return;
            } else if (ItemStack.isSameItemSameComponents(stack, result)) {
                int space = stack.getMaxStackSize() - stack.getCount();
                int toAdd = Math.min(space, result.getCount());
                stack.grow(toAdd);
                result.shrink(toAdd);
                setChanged();
                if (result.isEmpty()) return;
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof IndustrialGrinderControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(IndustrialGrinderControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(IndustrialGrinderControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(IndustrialGrinderControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 1.0,
                            6, 0.4, 0.4, 0.4, 0.05);
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
                if (current.getBlock() instanceof IndustrialGrinderControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(IndustrialGrinderControllerBlock.ASSEMBLED, true)
                            .setValue(IndustrialGrinderControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Industrial Grinder]§r 2x3x3 Machine is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof IndustrialGrinderControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(IndustrialGrinderControllerBlock.ASSEMBLED, false)
                            .setValue(IndustrialGrinderControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Industrial Grinder]§r 2x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(18);
        Block controller = RefineryMod.INDUSTRIAL_GRINDER_CONTROLLER.get();
        Block inputPort = RefineryMod.GRINDER_INPUT_PORT.get();
        Block outputPort = RefineryMod.GRINDER_OUTPUT_PORT.get();
        Block chamber = RefineryMod.GRINDER_CHAMBER.get();
        Block casing = RefineryMod.GRINDER_CASING.get();

        // Layer 0: Foundation & Discharge
        list.add(new BlueprintRequirement(0, 0, 0, controller, "Grinder Controller", 0xFF10B981));
        list.add(new BlueprintRequirement(1, 0, 0, outputPort, "Grinder Output Port", 0xFF38BDF8));
        list.add(new BlueprintRequirement(0, 0, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 0, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(0, 0, 2, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 0, 2, casing, "Grinder Structural Casing", 0xFF64748B));

        // Layer 1: Crushing Chamber
        list.add(new BlueprintRequirement(0, 1, 0, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 1, 0, chamber, "Grinder Crushing Chamber", 0xFFF59E0B));
        list.add(new BlueprintRequirement(0, 1, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 1, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(0, 1, 2, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 1, 2, casing, "Grinder Structural Casing", 0xFF64748B));

        // Layer 2: Feed Chute
        list.add(new BlueprintRequirement(0, 2, 0, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 2, 0, inputPort, "Grinder Input Port", 0xFFEA580C));
        list.add(new BlueprintRequirement(0, 2, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 2, 1, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(0, 2, 2, casing, "Grinder Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 2, 2, casing, "Grinder Structural Casing", 0xFF64748B));

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
        Direction facing = getBlockState().getValue(IndustrialGrinderControllerBlock.FACING);
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
            player.sendSystemMessage(Component.literal("§e[Grinder Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
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
        Direction facing = getBlockState().getValue(IndustrialGrinderControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof GrinderPortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("energy", this.energy);
        output.putInt("progress", this.progress);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.progress = input.getIntOr("progress", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_INPUT = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_OUTPUT = new int[]{4, 5, 6, 7};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_OUTPUT;
        }
        return SLOTS_INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot < 4 && !getGrindResult(stack).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 4;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
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
        return slot < 4 && !getGrindResult(stack).isEmpty();
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
        return Component.translatable("container.refinery.industrial_grinder");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new IndustrialGrinderMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
