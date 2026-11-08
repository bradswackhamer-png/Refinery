package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.MetalPressControllerBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.menu.MetalPressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MetalPressControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 5;
    public static final int DIE_SLOT = 0;
    public static final int INPUT_SLOT_1 = 1;
    public static final int INPUT_SLOT_2 = 2;
    public static final int OUTPUT_SLOT_1 = 3;
    public static final int OUTPUT_SLOT_2 = 4;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int ENERGY_PER_TICK = 500;
    public static final int TOTAL_PROCESS_TIME = 40; // 2 seconds per press cycle

    private boolean assembled = false;
    private int structureOffsetX = 0; // 0 = centered (-1..1), +1 = right-shifted (0..2), -1 = left-shifted (-2..0)
    private int energy = 0;
    private int progress = 0;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;
    private int exportCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 20_000, 0) {
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
                case 4 -> MetalPressControllerBlockEntity.this.isAssembled() ? 1 : 0;
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
        BlockState state = getBlockState();
        if (state.hasProperty(MetalPressControllerBlock.ASSEMBLED)) {
            return state.getValue(MetalPressControllerBlock.ASSEMBLED) || this.assembled;
        }
        return this.assembled;
    }

    public int getStructureOffsetX() {
        return this.structureOffsetX;
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

    public ItemStack getDie() {
        return this.items.get(DIE_SLOT);
    }

    public ItemStack getActiveInputItem() {
        ItemStack die = getDie();
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_2; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && (!die.isEmpty() ? !getPressResult(die, stack).isEmpty() : isValidInput(stack))) {
                return stack;
            }
        }
        for (int i = OUTPUT_SLOT_1; i <= OUTPUT_SLOT_2; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void syncToClient() {
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
            if (state.getBlock() instanceof MetalPressControllerBlock) {
                BlockState newState = state.setValue(MetalPressControllerBlock.ASSEMBLED, false)
                        .setValue(MetalPressControllerBlock.ACTIVE, false)
                        .setValue(MetalPressControllerBlock.BLUEPRINT, false);
                this.level.setBlock(this.worldPosition, newState, 3);
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
                if (current.getBlock() instanceof MetalPressControllerBlock && current.getValue(MetalPressControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(MetalPressControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
                syncToClient();
            }
            BlockState current = getBlockState();
            if (current.getBlock() instanceof MetalPressControllerBlock && current.getValue(MetalPressControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(MetalPressControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        // Auto-export finished stamped items (plates, gears, panels, rails) from Output Slots 3..4 to adjacent conveyor, pipe, or container
        if (--this.exportCooldown <= 0) {
            this.exportCooldown = 5;
            tryExportOutput(serverLevel);
        }

        boolean wasActive = state.getValue(MetalPressControllerBlock.ACTIVE);
        boolean isActive = false;

        // Check if we can stamp
        int inputSlot = getValidInputSlot();
        ItemStack die = getDie();
        ItemStack resultStack = (inputSlot != -1 && !die.isEmpty()) ? getPressResult(die, this.items.get(inputSlot)) : ItemStack.EMPTY;
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
                    // Consume 1 item & produce stamped output
                    if (inputSlot != -1 && !die.isEmpty()) {
                        ItemStack inputStack = this.items.get(inputSlot);
                        ItemStack result = getPressResult(die, inputStack);
                        if (!result.isEmpty()) {
                            inputStack.shrink(1);
                            addOutput(result.copy());
                        }
                    }

                    // Heavy anvil impact sound at center of conveyor press bed
                    Direction facing = state.getValue(MetalPressControllerBlock.FACING);
                    BlockPos bedPos = getRelativePos(this.structureOffsetX, 0, 0, facing);
                    serverLevel.playSound(null, bedPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.2f, 1.1f);
                    serverLevel.playSound(null, bedPos, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 0.8f, 0.6f);

                    // Sparks & smoke particles shooting outward from stamping die
                    serverLevel.sendParticles(ParticleTypes.LAVA,
                            bedPos.getX() + 0.5, bedPos.getY() + 0.85, bedPos.getZ() + 0.5,
                            6, 0.25, 0.05, 0.25, 0.1);
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                            bedPos.getX() + 0.5, bedPos.getY() + 0.85, bedPos.getZ() + 0.5,
                            8, 0.3, 0.1, 0.3, 0.15);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            bedPos.getX() + 0.5, bedPos.getY() + 0.85, bedPos.getZ() + 0.5,
                            3, 0.1, 0.05, 0.1, 0.02);
                }

                if (this.progress >= TOTAL_PROCESS_TIME) {
                    this.progress = 0;
                }
                setChanged();
                if (this.progress == 1 || this.progress == 20 || this.progress == 0) {
                    syncToClient();
                }
            }
        } else {
            if (this.progress > 0 && this.progress <= 20) {
                this.progress = 0;
                setChanged();
                syncToClient();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof MetalPressControllerBlock) {
                level.setBlock(pos, latest.setValue(MetalPressControllerBlock.ACTIVE, isActive), 3);
            }
            syncToClient();
        }
    }

    public static final TagKey<Item> PRESS_DIES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("refinery", "press_dies"));
    public static final TagKey<Item> GEARS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "gears"));

    public static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/steel"));
    public static final TagKey<Item> INGOTS_IRON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/iron"));
    public static final TagKey<Item> INGOTS_COPPER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/copper"));
    public static final TagKey<Item> INGOTS_GOLD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/gold"));
    public static final TagKey<Item> INGOTS_TIN = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/tin"));
    public static final TagKey<Item> INGOTS_BRONZE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/bronze"));

    public static final TagKey<Item> PLATES_STEEL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/steel"));
    public static final TagKey<Item> PLATES_IRON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/iron"));
    public static final TagKey<Item> PLATES_COPPER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/copper"));
    public static final TagKey<Item> PLATES_GOLD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/gold"));
    public static final TagKey<Item> PLATES_TIN = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/tin"));
    public static final TagKey<Item> PLATES_BRONZE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "plates/bronze"));

    public static boolean isDie(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(PRESS_DIES)
                || stack.is(RefineryMod.PLATE_DIE.get())
                || stack.is(RefineryMod.GEAR_DIE.get())
                || stack.is(RefineryMod.CORRUGATED_DIE.get())
                || stack.is(RefineryMod.RAIL_DIE.get());
    }

    public static boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return !getPressResult(new ItemStack(RefineryMod.PLATE_DIE.get()), stack).isEmpty()
                || !getPressResult(new ItemStack(RefineryMod.GEAR_DIE.get()), stack).isEmpty()
                || !getPressResult(new ItemStack(RefineryMod.CORRUGATED_DIE.get()), stack).isEmpty()
                || !getPressResult(new ItemStack(RefineryMod.RAIL_DIE.get()), stack).isEmpty();
    }

    public static ItemStack getPressResult(ItemStack input) {
        return getPressResult(new ItemStack(RefineryMod.PLATE_DIE.get()), input);
    }

    public static ItemStack getPressResult(ItemStack die, ItemStack input) {
        if (die.isEmpty() || input.isEmpty()) return ItemStack.EMPTY;

        Identifier id = BuiltInRegistries.ITEM.getKey(input.getItem());
        String path = id != null ? id.getPath() : "";

        // === 1. PLATE DIE: Ingots -> Plates ===
        if (die.is(RefineryMod.PLATE_DIE.get())) {
            if (input.is(RefineryMod.STEEL_INGOT.get()) || input.is(INGOTS_STEEL) || path.equals("steel_ingot")) {
                return new ItemStack(RefineryMod.STEEL_PLATE.get(), 1);
            }
            if (input.is(Items.IRON_INGOT) || input.is(INGOTS_IRON) || path.equals("iron_ingot")) {
                return new ItemStack(RefineryMod.IRON_PLATE.get(), 1);
            }
            if (input.is(Items.COPPER_INGOT) || input.is(INGOTS_COPPER) || path.equals("copper_ingot")) {
                return new ItemStack(RefineryMod.COPPER_PLATE.get(), 1);
            }
            if (input.is(Items.GOLD_INGOT) || input.is(INGOTS_GOLD) || path.equals("gold_ingot")) {
                return new ItemStack(RefineryMod.GOLD_PLATE.get(), 1);
            }
            if (input.is(RefineryMod.TIN_INGOT.get()) || input.is(INGOTS_TIN) || path.equals("tin_ingot")) {
                return new ItemStack(RefineryMod.TIN_PLATE.get(), 1);
            }
            if (input.is(RefineryMod.BRONZE_INGOT.get()) || input.is(INGOTS_BRONZE) || path.equals("bronze_ingot")) {
                return new ItemStack(RefineryMod.BRONZE_PLATE.get(), 1);
            }
            return ItemStack.EMPTY;
        }

        // === 2. GEAR DIE: Ingots or Plates -> Gears ===
        if (die.is(RefineryMod.GEAR_DIE.get())) {
            if (input.is(RefineryMod.STEEL_INGOT.get()) || input.is(INGOTS_STEEL) || input.is(RefineryMod.STEEL_PLATE.get()) || input.is(PLATES_STEEL) || path.contains("steel")) {
                return new ItemStack(RefineryMod.STEEL_GEAR.get(), 1);
            }
            if (input.is(Items.IRON_INGOT) || input.is(INGOTS_IRON) || input.is(RefineryMod.IRON_PLATE.get()) || input.is(PLATES_IRON) || path.contains("iron")) {
                return new ItemStack(RefineryMod.IRON_GEAR.get(), 1);
            }
            if (input.is(Items.COPPER_INGOT) || input.is(INGOTS_COPPER) || input.is(RefineryMod.COPPER_PLATE.get()) || input.is(PLATES_COPPER) || path.contains("copper")) {
                return new ItemStack(RefineryMod.COPPER_GEAR.get(), 1);
            }
            if (input.is(RefineryMod.BRONZE_INGOT.get()) || input.is(INGOTS_BRONZE) || input.is(RefineryMod.BRONZE_PLATE.get()) || input.is(PLATES_BRONZE) || path.contains("bronze")) {
                return new ItemStack(RefineryMod.BRONZE_GEAR.get(), 1);
            }
            if (input.is(Items.GOLD_INGOT) || input.is(INGOTS_GOLD) || input.is(RefineryMod.GOLD_PLATE.get()) || input.is(PLATES_GOLD) || path.contains("gold")) {
                return new ItemStack(RefineryMod.GOLD_GEAR.get(), 1);
            }
            if (input.is(RefineryMod.TIN_INGOT.get()) || input.is(INGOTS_TIN) || input.is(RefineryMod.TIN_PLATE.get()) || input.is(PLATES_TIN) || path.contains("tin")) {
                return new ItemStack(RefineryMod.TIN_GEAR.get(), 1);
            }
            return ItemStack.EMPTY;
        }

        // === 3. CORRUGATED DIE: 1 Steel Ingot/Plate -> 4 Corrugated Sheets ===
        if (die.is(RefineryMod.CORRUGATED_DIE.get())) {
            if (input.is(RefineryMod.STEEL_INGOT.get()) || input.is(INGOTS_STEEL) || input.is(RefineryMod.STEEL_PLATE.get()) || input.is(PLATES_STEEL) || path.contains("steel")) {
                return new ItemStack(RefineryMod.CORRUGATED_STEEL_PANEL.get(), 4);
            }
            if (input.is(Items.IRON_INGOT) || input.is(INGOTS_IRON) || input.is(RefineryMod.IRON_PLATE.get()) || input.is(PLATES_IRON) || path.contains("iron")) {
                return new ItemStack(RefineryMod.CORRUGATED_STEEL_PANEL.get(), 2);
            }
            return ItemStack.EMPTY;
        }

        // === 4. RAIL DIE: Ingots or Plates -> Rails ===
        if (die.is(RefineryMod.RAIL_DIE.get())) {
            if (input.is(RefineryMod.STEEL_INGOT.get()) || input.is(INGOTS_STEEL) || input.is(RefineryMod.STEEL_PLATE.get()) || input.is(PLATES_STEEL) || path.contains("steel")) {
                return new ItemStack(Items.RAIL, 4);
            }
            if (input.is(Items.IRON_INGOT) || input.is(INGOTS_IRON) || input.is(RefineryMod.IRON_PLATE.get()) || input.is(PLATES_IRON) || path.contains("iron")) {
                return new ItemStack(Items.RAIL, 2);
            }
            if (input.is(Items.GOLD_INGOT) || input.is(INGOTS_GOLD) || input.is(RefineryMod.GOLD_PLATE.get()) || input.is(PLATES_GOLD) || path.contains("gold")) {
                return new ItemStack(Items.POWERED_RAIL, 2);
            }
            if (input.is(Items.COPPER_INGOT) || input.is(INGOTS_COPPER) || input.is(RefineryMod.COPPER_PLATE.get()) || input.is(PLATES_COPPER) || path.contains("copper")) {
                return new ItemStack(Items.ACTIVATOR_RAIL, 2);
            }
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    private int getValidInputSlot() {
        ItemStack die = getDie();
        if (die.isEmpty()) return -1;
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_2; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && !getPressResult(die, stack).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private boolean canFitOutput(ItemStack result) {
        if (result.isEmpty()) return false;
        for (int i = OUTPUT_SLOT_1; i <= OUTPUT_SLOT_2; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(stack, result) && stack.getCount() + result.getCount() <= stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int i = OUTPUT_SLOT_1; i <= OUTPUT_SLOT_2; i++) {
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

        // Always keep member blocks synchronized with assembly state
        updateMemberBlockStates(valid);

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
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5,
                            8, 0.5, 0.3, 0.5, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        BlockState currentState = getBlockState();
        Direction facing = (currentState.getBlock() instanceof MetalPressControllerBlock && currentState.hasProperty(MetalPressControllerBlock.FACING))
                ? currentState.getValue(MetalPressControllerBlock.FACING)
                : Direction.NORTH;

        if (isAssembled) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    for (int x = this.structureOffsetX - 1; x <= this.structureOffsetX + 1; x++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos targetPos = getRelativePos(x, y, z, facing);
                        BlockState targetState = this.level.getBlockState(targetPos);
                        if (isPressComponent(targetState) && targetState.hasProperty(MetalPressControllerBlock.ASSEMBLED)) {
                            if (!targetState.getValue(MetalPressControllerBlock.ASSEMBLED)) {
                                this.level.setBlock(targetPos, targetState.setValue(MetalPressControllerBlock.ASSEMBLED, true), 3);
                            }
                        }
                    }
                }
            }
        } else {
            // Sweep surrounding area so any previously assembled blocks are guaranteed to become visible again
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    for (int x = -2; x <= 2; x++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos targetPos = getRelativePos(x, y, z, facing);
                        BlockState targetState = this.level.getBlockState(targetPos);
                        if (isPressComponent(targetState) && targetState.hasProperty(MetalPressControllerBlock.ASSEMBLED)) {
                            if (targetState.getValue(MetalPressControllerBlock.ASSEMBLED)) {
                                this.level.setBlock(targetPos, targetState.setValue(MetalPressControllerBlock.ASSEMBLED, false), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPressComponent(BlockState state) {
        return state.is(RefineryMod.PRESS_BED.get())
                || state.is(RefineryMod.PRESS_HEAD.get())
                || state.is(RefineryMod.PRESS_CASING.get());
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            updateMemberBlockStates(true);
            this.blueprintProjectionTicks = 0;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof MetalPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MetalPressControllerBlock.ASSEMBLED, true)
                            .setValue(MetalPressControllerBlock.BLUEPRINT, false), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Metal Press]§r 3x2x2 Multiblock Press is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof MetalPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MetalPressControllerBlock.ASSEMBLED, false)
                            .setValue(MetalPressControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Metal Press]§r 3x2x2 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    /**
     * Canonical 3x2x2 (3 wide x 2 tall x 2 deep) Conveyor & AIDA Stamping Press blueprint.
     */
    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(12);
        Block controller = RefineryMod.METAL_PRESS_CONTROLLER.get();
        Block bed = RefineryMod.PRESS_BED.get();
        Block head = RefineryMod.PRESS_HEAD.get();
        Block casing = RefineryMod.PRESS_CASING.get();

        // Layer 0: 3-Wide Conveyor Bed & Foundation (x = -1, 0, +1)
        list.add(new BlueprintRequirement(-1, 0, 0, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Metal Press Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, bed, "Press Anvil Bed", 0xFFF59E0B));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Press Structural Casing", 0xFF64748B));

        // Layer 1: Gantry Frame & AIDA Stamping Head (x = -1, 0, +1)
        list.add(new BlueprintRequirement(-1, 1, 0, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 0, head, "Press Hydraulic Head", 0xFF3B82F6));
        list.add(new BlueprintRequirement( 1, 1, 0, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 1, casing, "Press Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 1, 1, casing, "Press Structural Casing", 0xFF64748B));

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

        if (matched == reqs.size()) {
            this.structureOffsetX = 0;
            return true;
        }

        // Also check flexible 3-wide layout (centered 0, right-aligned +1, or left-aligned -1)
        for (int candidateOffset : new int[]{ 0, 1, -1 }) {
            if (checkFlexible3Wide(facing, candidateOffset, 2) || checkFlexible3Wide(facing, candidateOffset, 1)) {
                this.structureOffsetX = candidateOffset;
                return true;
            }
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Press Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size() + " (3x2x2 Structure)"));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing components:"));
                for (String m : missing) {
                    player.sendSystemMessage(Component.literal(" - §f" + m));
                }
            }
        }

        return false;
    }

    private boolean checkFlexible3Wide(Direction facing, int centerOffset, int depth) {
        if (this.level == null) return false;
        Block controller = RefineryMod.METAL_PRESS_CONTROLLER.get();
        Block bed = RefineryMod.PRESS_BED.get();
        Block head = RefineryMod.PRESS_HEAD.get();
        Block casing = RefineryMod.PRESS_CASING.get();

        int beds = 0;
        int heads = 0;

        for (int y = 0; y < 2; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = centerOffset - 1; x <= centerOffset + 1; x++) {
                    BlockPos targetPos = getRelativePos(x, y, z, facing);
                    BlockState state = this.level.getBlockState(targetPos);
                    if (x == 0 && y == 0 && z == 0) {
                        if (!state.is(controller)) return false;
                        continue;
                    }
                    if (state.is(bed)) {
                        beds++;
                    } else if (state.is(head)) {
                        heads++;
                    } else if (!state.is(casing)) {
                        return false;
                    }
                }
            }
        }
        return beds >= 1 && heads >= 1;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("structureOffsetX", this.structureOffsetX);
        output.putInt("energy", this.energy);
        output.putInt("progress", this.progress);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.structureOffsetX = input.getIntOr("structureOffsetX", 0);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.progress = input.getIntOr("progress", 0);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_INPUT = new int[]{INPUT_SLOT_1, INPUT_SLOT_2};
    private static final int[] SLOTS_OUTPUT = new int[]{OUTPUT_SLOT_1, OUTPUT_SLOT_2};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_OUTPUT;
        }
        return SLOTS_INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2) {
            ItemStack die = getDie();
            return !die.isEmpty() ? !getPressResult(die, stack).isEmpty() : isValidInput(stack);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2;
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
        if (slot == DIE_SLOT) {
            return isDie(stack);
        }
        if (slot == INPUT_SLOT_1 || slot == INPUT_SLOT_2) {
            ItemStack die = getDie();
            return !die.isEmpty() ? !getPressResult(die, stack).isEmpty() : isValidInput(stack);
        }
        return false;
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

    public boolean isMultiblockPart(BlockPos pos) {
        BlockState state = getBlockState();
        Direction facing = (state.getBlock() instanceof MetalPressControllerBlock && state.hasProperty(MetalPressControllerBlock.FACING))
                ? state.getValue(MetalPressControllerBlock.FACING)
                : Direction.NORTH;
        for (BlueprintRequirement req : getRequirements()) {
            if (getRelativePos(req.x, req.y, req.z, facing).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private void tryExportOutput(ServerLevel level) {
        Direction facing = getBlockState().getValue(MetalPressControllerBlock.FACING);
        Direction right = facing.getClockWise();

        // Check if there are any finished items to export from output buffer slots 3..4
        boolean hasOutput = !this.items.get(OUTPUT_SLOT_1).isEmpty() || !this.items.get(OUTPUT_SLOT_2).isEmpty();
        if (!hasOutput) return;

        // Collect all potential export target positions in priority order:
        Set<BlockPos> targetPositions = new LinkedHashSet<>();

        // 1. Right-side discharge off the anvil bed (x = 2, y = 0..1, z = 0..1 in local coords)
        targetPositions.add(getRelativePos(2, 0, 0, facing));
        targetPositions.add(getRelativePos(2, 0, 1, facing));
        targetPositions.add(getRelativePos(2, 0, 0, facing).below());
        targetPositions.add(getRelativePos(2, 0, 1, facing).below());

        // 2. In front of the press bed and controller (z = -1, x = -1..1)
        for (int x = -1; x <= 1; x++) {
            BlockPos frontPos = getRelativePos(x, 0, -1, facing);
            targetPositions.add(frontPos);
            targetPositions.add(frontPos.below());
        }

        // 3. Perimeter blocks around the 3x2 base
        for (int x = -1; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                if (x == -1) targetPositions.add(getRelativePos(x - 1, 0, z, facing));
                if (x == 1)  targetPositions.add(getRelativePos(x + 1, 0, z, facing));
                if (z == 0)  targetPositions.add(getRelativePos(x, 0, z - 1, facing));
                if (z == 1)  targetPositions.add(getRelativePos(x, 0, z + 1, facing));
            }
        }

        for (BlockPos targetPos : targetPositions) {
            if (isMultiblockPart(targetPos)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            BlockEntity targetBE = level.getBlockEntity(targetPos);

            boolean isConveyor = ConveyorBeltBlock.isAnyConveyorBlock(targetState)
                    || targetBE instanceof ConveyorBeltBlockEntity
                    || targetBE instanceof RightAngleConveyorBeltBlockEntity
                    || targetBE instanceof SlopedConveyorBeltBlockEntity;

            if (isConveyor) {
                Direction conveyorForward = null;
                if (targetState.getBlock() instanceof ConveyorBeltBlock) {
                    conveyorForward = targetState.getValue(ConveyorBeltBlock.FACING);
                } else if (targetState.getBlock() instanceof RightAngleConveyorBeltBlock) {
                    conveyorForward = RightAngleConveyorBeltBlock.getExitDirection(targetState);
                } else if (targetState.getBlock() instanceof SlopedConveyorBeltBlock) {
                    conveyorForward = targetState.getValue(SlopedConveyorBeltBlock.FACING);
                } else if (targetState.hasProperty(HorizontalDirectionalBlock.FACING)) {
                    conveyorForward = targetState.getValue(HorizontalDirectionalBlock.FACING);
                }
                if (conveyorForward != null && isMultiblockPart(targetPos.relative(conveyorForward))) {
                    continue;
                }

                double cx = targetPos.getX() + 0.5;
                double cz = targetPos.getZ() + 0.5;
                net.minecraft.world.phys.AABB beltBox = new net.minecraft.world.phys.AABB(
                        cx - 0.25, targetPos.getY() + 0.10, cz - 0.25,
                        cx + 0.25, targetPos.getY() + 0.70, cz + 0.25
                );
                List<net.minecraft.world.entity.item.ItemEntity> itemsOnBelt = level.getEntitiesOfClass(
                        net.minecraft.world.entity.item.ItemEntity.class, beltBox);
                if (!itemsOnBelt.isEmpty()) {
                    continue;
                }

                for (int slot = OUTPUT_SLOT_1; slot <= OUTPUT_SLOT_2; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack single = sourceStack.copyWithCount(1);
                    if (targetBE instanceof Container container) {
                        container.setItem(0, single);
                    } else {
                        Direction forwardDir = conveyorForward != null ? conveyorForward : facing;
                        double spawnX = targetPos.getX() + 0.5;
                        double spawnY = targetPos.getY() + 0.35;
                        double spawnZ = targetPos.getZ() + 0.5;
                        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                                level, spawnX, spawnY, spawnZ, single);
                        itemEntity.setDeltaMovement(forwardDir.getStepX() * 0.05, 0.0, forwardDir.getStepZ() * 0.05);
                        itemEntity.setPickUpDelay(10);
                        level.addFreshEntity(itemEntity);
                    }

                    sourceStack.shrink(1);
                    if (sourceStack.isEmpty()) {
                        this.items.set(slot, ItemStack.EMPTY);
                    }
                    syncToClient();
                    return;
                }
                continue;
            }

            // Direct pipe insertion
            if (targetBE instanceof IndustrialPipeBlockEntity pipeBE && pipeBE.getPipeType().handlesItems()) {
                for (int slot = OUTPUT_SLOT_1; slot <= OUTPUT_SLOT_2; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(pipeBE, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        syncToClient();
                        return;
                    }
                }
                continue;
            }

            // Target is a standard Container (chests, barrels, hoppers, crates)
            if (targetBE instanceof Container targetContainer) {
                for (int slot = OUTPUT_SLOT_1; slot <= OUTPUT_SLOT_2; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(targetContainer, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        targetContainer.setChanged();
                        syncToClient();
                        return;
                    }
                }
            } else {
                var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, targetPos, Direction.UP);
                if (handler != null) {
                    for (int slot = OUTPUT_SLOT_1; slot <= OUTPUT_SLOT_2; slot++) {
                        ItemStack sourceStack = this.items.get(slot);
                        if (sourceStack.isEmpty()) continue;

                        try (net.neoforged.neoforge.transfer.transaction.Transaction tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
                            int inserted = handler.insert(net.neoforged.neoforge.transfer.item.ItemResource.of(sourceStack), sourceStack.getCount(), tx);
                            if (inserted > 0) {
                                tx.commit();
                                sourceStack.shrink(inserted);
                                if (sourceStack.isEmpty()) {
                                    this.items.set(slot, ItemStack.EMPTY);
                                }
                                syncToClient();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
