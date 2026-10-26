package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.MetalPressControllerBlock;
import com.refinery.menu.MetalPressMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MetalPressControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 4;
    public static final int ENERGY_CAPACITY = 50_000;
    public static final int ENERGY_PER_TICK = 20;
    public static final int TOTAL_PROCESS_TIME = 40; // 2 seconds per press cycle

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
            MetalPressControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            MetalPressControllerBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MetalPressControllerBlockEntity.this.progress;
                case 1 -> TOTAL_PROCESS_TIME;
                case 2 -> MetalPressControllerBlockEntity.this.energy & 0xFFFF;
                case 3 -> (MetalPressControllerBlockEntity.this.energy >> 16) & 0xFFFF;
                case 4 -> MetalPressControllerBlockEntity.this.assembled ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> MetalPressControllerBlockEntity.this.progress = value;
                case 4 -> MetalPressControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public MetalPressControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.METAL_PRESS_CONTROLLER_BE.get(), pos, state);
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
                if (state.getBlock() instanceof MetalPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(MetalPressControllerBlock.ASSEMBLED, false)
                            .setValue(MetalPressControllerBlock.ACTIVE, false)
                            .setValue(MetalPressControllerBlock.BLUEPRINT, false), 3);
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
                if (current.getBlock() instanceof MetalPressControllerBlock && current.getValue(MetalPressControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(MetalPressControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
            BlockState current = getBlockState();
            if (current.getBlock() instanceof MetalPressControllerBlock && current.getValue(MetalPressControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(MetalPressControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(MetalPressControllerBlock.ACTIVE);
        boolean isActive = false;

        // Check if we can stamp
        int inputSlot = getValidInputSlot();
        ItemStack resultStack = inputSlot != -1 ? getPressResult(this.items.get(inputSlot)) : ItemStack.EMPTY;
        boolean canStamp = (this.progress > 20) || (!resultStack.isEmpty() && canFitOutput(resultStack));

        if (canStamp) {
            int requiredEnergy = (TOTAL_PROCESS_TIME - this.progress) * ENERGY_PER_TICK;
            if (this.energy >= requiredEnergy) {
                isActive = true;
                this.energy -= ENERGY_PER_TICK;
                this.energyStorage.set(this.energy);
                this.progress++;

                // Sound of hydraulic motor whirring during stroke
                if (this.progress == 1 || this.progress == 25) {
                    serverLevel.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1.4f);
                }

                // Stamping Impact at tick 20 (bottom of ram stroke)
                if (this.progress == 20) {
                    // Consume 1 ingot & produce 1 plate
                    if (inputSlot != -1) {
                        ItemStack inputStack = this.items.get(inputSlot);
                        ItemStack result = getPressResult(inputStack);
                        if (!result.isEmpty()) {
                            inputStack.shrink(1);
                            addOutput(result.copy());
                        }
                    }

                    // Heavy anvil impact sound
                    Direction facing = state.getValue(MetalPressControllerBlock.FACING);
                    BlockPos bedPos = getRelativePos(1, 0, 0, facing);
                    serverLevel.playSound(null, bedPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.2f, 1.1f);
                    serverLevel.playSound(null, bedPos, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 0.8f, 0.6f);

                    // Sparks & smoke particles shooting outward from anvil bed
                    serverLevel.sendParticles(ParticleTypes.LAVA,
                            bedPos.getX() + 0.5, bedPos.getY() + 1.05, bedPos.getZ() + 0.5,
                            6, 0.25, 0.05, 0.25, 0.1);
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                            bedPos.getX() + 0.5, bedPos.getY() + 1.05, bedPos.getZ() + 0.5,
                            8, 0.3, 0.1, 0.3, 0.15);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            bedPos.getX() + 0.5, bedPos.getY() + 1.05, bedPos.getZ() + 0.5,
                            3, 0.1, 0.05, 0.1, 0.02);
                }

                if (this.progress >= TOTAL_PROCESS_TIME) {
                    this.progress = 0;
                }
                setChanged();
            }
        } else {
            if (this.progress > 0 && this.progress <= 20) {
                this.progress = 0;
                setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof MetalPressControllerBlock) {
                level.setBlock(pos, latest.setValue(MetalPressControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    public static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/steel"));
    public static final TagKey<Item> INGOTS_IRON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/iron"));
    public static final TagKey<Item> INGOTS_COPPER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/copper"));
    public static final TagKey<Item> INGOTS_GOLD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/gold"));
    public static final TagKey<Item> INGOTS_TIN = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/tin"));
    public static final TagKey<Item> INGOTS_BRONZE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/bronze"));

    public static ItemStack getPressResult(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;

        Identifier id = BuiltInRegistries.ITEM.getKey(input.getItem());
        String path = id != null ? id.getPath() : "";

        // Steel Ingot -> Steel Plate
        if (input.is(RefineryMod.STEEL_INGOT.get()) || input.is(INGOTS_STEEL) || path.equals("steel_ingot")) {
            return new ItemStack(RefineryMod.STEEL_PLATE.get(), 1);
        }
        // Iron Ingot -> Iron Plate
        if (input.is(Items.IRON_INGOT) || input.is(INGOTS_IRON) || path.equals("iron_ingot")) {
            return new ItemStack(RefineryMod.IRON_PLATE.get(), 1);
        }
        // Copper Ingot -> Copper Plate
        if (input.is(Items.COPPER_INGOT) || input.is(INGOTS_COPPER) || path.equals("copper_ingot")) {
            return new ItemStack(RefineryMod.COPPER_PLATE.get(), 1);
        }
        // Gold Ingot -> Gold Plate
        if (input.is(Items.GOLD_INGOT) || input.is(INGOTS_GOLD) || path.equals("gold_ingot")) {
            return new ItemStack(RefineryMod.GOLD_PLATE.get(), 1);
        }
        // Tin Ingot -> Tin Plate
        if (input.is(RefineryMod.TIN_INGOT.get()) || input.is(INGOTS_TIN) || path.equals("tin_ingot")) {
            return new ItemStack(RefineryMod.TIN_PLATE.get(), 1);
        }
        // Bronze Ingot -> Bronze Plate
        if (input.is(RefineryMod.BRONZE_INGOT.get()) || input.is(INGOTS_BRONZE) || path.equals("bronze_ingot")) {
            return new ItemStack(RefineryMod.BRONZE_PLATE.get(), 1);
        }

        return ItemStack.EMPTY;
    }

    private int getValidInputSlot() {
        for (int i = 0; i < 2; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && !getPressResult(stack).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private boolean canFitOutput(ItemStack result) {
        if (result.isEmpty()) return false;
        for (int i = 2; i < 4; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(stack, result) && stack.getCount() + result.getCount() <= stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int i = 2; i < 4; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                this.items.set(i, result);
                setChanged();
                return;
            } else if (ItemStack.isSameItemSameComponents(stack, result) && stack.getCount() + result.getCount() <= stack.getMaxStackSize()) {
                stack.grow(result.getCount());
                setChanged();
                return;
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof MetalPressControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(MetalPressControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(MetalPressControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(MetalPressControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 1.0, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 1.0,
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
                if (current.getBlock() instanceof MetalPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MetalPressControllerBlock.ASSEMBLED, true)
                            .setValue(MetalPressControllerBlock.BLUEPRINT, false), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Metal Press]§r 2x2x2 Multiblock Press is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof MetalPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MetalPressControllerBlock.ASSEMBLED, false)
                            .setValue(MetalPressControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Metal Press]§r 2x2x2 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(8);
        Block controller = RefineryMod.METAL_PRESS_CONTROLLER.get();
        Block bed = RefineryMod.PRESS_BED.get();
        Block head = RefineryMod.PRESS_HEAD.get();
        Block casing = RefineryMod.PRESS_CASING.get();

        // Layer 0: Bed & Foundation
        list.add(new BlueprintRequirement(0, 0, 0, controller, "Metal Press Controller", 0xFF10B981));
        list.add(new BlueprintRequirement(1, 0, 0, bed, "Press Anvil Bed", 0xFFF59E0B));
        list.add(new BlueprintRequirement(0, 0, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 0, 1, casing, "Press Structural Casing", 0xFF64748B));

        // Layer 1: Frame & Hydraulic Head
        list.add(new BlueprintRequirement(0, 1, 0, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 1, 0, head, "Press Hydraulic Head", 0xFF3B82F6));
        list.add(new BlueprintRequirement(0, 1, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(1, 1, 1, casing, "Press Structural Casing", 0xFF64748B));

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
        Direction facing = getBlockState().getValue(MetalPressControllerBlock.FACING);
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
            player.sendSystemMessage(Component.literal("§e[Press Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing components:"));
                for (String m : missing) {
                    player.sendSystemMessage(Component.literal(" - §f" + m));
                }
            }
        }

        return matched == reqs.size();
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
    private static final int[] SLOTS_INPUT = new int[]{0, 1};
    private static final int[] SLOTS_OUTPUT = new int[]{2, 3};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_OUTPUT;
        }
        return SLOTS_INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot < 2 && !getPressResult(stack).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 2;
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
        return slot < 2 && !getPressResult(stack).isEmpty();
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
        return Component.translatable("container.refinery.metal_press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MetalPressMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
