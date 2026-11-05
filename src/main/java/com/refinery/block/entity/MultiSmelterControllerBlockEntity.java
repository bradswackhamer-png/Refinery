package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.MultiSmelterControllerBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.block.SmelterCasingBlock;
import com.refinery.block.SmelterChamberBlock;
import com.refinery.block.SmelterPortBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.menu.MultiSmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MultiSmelterControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INPUT_SLOTS_COUNT = 8;
    public static final int OUTPUT_SLOTS_COUNT = 8;
    public static final int FUEL_SLOT_INDEX = 16;
    public static final int INVENTORY_SIZE = 17; // 0..7: in, 8..15: out, 16: fuel

    public static final int ENERGY_CAPACITY = 50_000;
    public static final int ENERGY_PER_TICK = 30;
    public static final int PROCESS_TIME = 100; // 5 seconds per item (fast parallel smelting)

    private boolean assembled = false;
    private int energy = 0;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private final int[] cookProgress = new int[INPUT_SLOTS_COUNT];

    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;
    private int exportCooldown = 0;
    private int lastExportSlot = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1_000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            MultiSmelterControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            MultiSmelterControllerBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> PROCESS_TIME;
                case 1 -> MultiSmelterControllerBlockEntity.this.energy & 0xFFFF;
                case 2 -> (MultiSmelterControllerBlockEntity.this.energy >> 16) & 0xFFFF;
                case 3 -> MultiSmelterControllerBlockEntity.this.burnTime;
                case 4 -> MultiSmelterControllerBlockEntity.this.maxBurnTime;
                case 5 -> MultiSmelterControllerBlockEntity.this.assembled ? 1 : 0;
                case 6 -> MultiSmelterControllerBlockEntity.this.getActiveSlotsMask();
                default -> {
                    if (index >= 7 && index < 7 + INPUT_SLOTS_COUNT) {
                        yield MultiSmelterControllerBlockEntity.this.cookProgress[index - 7];
                    }
                    yield 0;
                }
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 1 -> MultiSmelterControllerBlockEntity.this.energy = (MultiSmelterControllerBlockEntity.this.energy & ~0xFFFF) | (value & 0xFFFF);
                case 2 -> MultiSmelterControllerBlockEntity.this.energy = (MultiSmelterControllerBlockEntity.this.energy & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 3 -> MultiSmelterControllerBlockEntity.this.burnTime = value;
                case 4 -> MultiSmelterControllerBlockEntity.this.maxBurnTime = value;
                case 5 -> MultiSmelterControllerBlockEntity.this.assembled = (value != 0);
                default -> {
                    if (index >= 7 && index < 7 + INPUT_SLOTS_COUNT) {
                        MultiSmelterControllerBlockEntity.this.cookProgress[index - 7] = value;
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 7 + INPUT_SLOTS_COUNT; // 15
        }
    };

    public MultiSmelterControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.MULTI_SMELTER_CONTROLLER_BE.get(), pos, blockState);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public int getCookProgress(int slot) {
        if (slot >= 0 && slot < INPUT_SLOTS_COUNT) {
            return this.cookProgress[slot];
        }
        return 0;
    }

    public int getActiveSlotsMask() {
        int mask = 0;
        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            if (this.cookProgress[i] > 0) {
                mask |= (1 << i);
            }
        }
        return mask;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MultiSmelterControllerBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Periodic multiblock integrity check
        be.structureCheckCooldown++;
        if (be.structureCheckCooldown >= 40) {
            be.structureCheckCooldown = 0;
            be.syncAssembly();
        }

        // Blueprint hologram countdown
        if (be.blueprintProjectionTicks > 0) {
            be.blueprintProjectionTicks--;
            if (be.blueprintProjectionTicks == 0) {
                BlockState current = be.getBlockState();
                if (current.getBlock() instanceof MultiSmelterControllerBlock && current.getValue(MultiSmelterControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(MultiSmelterControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!be.assembled) {
            boolean hadProgress = false;
            for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
                if (be.cookProgress[i] > 0) {
                    be.cookProgress[i] = 0;
                    hadProgress = true;
                }
            }
            if (hadProgress) be.setChanged();
            BlockState current = be.getBlockState();
            if (current.getBlock() instanceof MultiSmelterControllerBlock && current.getValue(MultiSmelterControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(MultiSmelterControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        // Auto-export finished smelted items (charcoal, ingots, etc.) from Output Slots 8..15 to adjacent conveyor, pipe, or container
        if (--be.exportCooldown <= 0) {
            be.exportCooldown = 5;
            be.tryExportOutput(serverLevel);
        }

        boolean wasActive = state.getValue(MultiSmelterControllerBlock.ACTIVE);
        boolean isActive = false;

        // Check which slots have valid recipes and available output space
        boolean[] canCook = new boolean[INPUT_SLOTS_COUNT];
        ItemStack[] results = new ItemStack[INPUT_SLOTS_COUNT];
        int activeCount = 0;

        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            ItemStack input = be.items.get(i);
            if (!input.isEmpty()) {
                ItemStack res = be.getSmeltingResult(serverLevel, input);
                if (!res.isEmpty() && be.canFitOutput(i + 8, res)) {
                    canCook[i] = true;
                    results[i] = res;
                    activeCount++;
                }
            }
            if (!canCook[i] && be.cookProgress[i] > 0) {
                be.cookProgress[i] = 0;
                be.setChanged();
            }
        }

        // If there's work to do, power the smelter
        if (activeCount > 0) {
            boolean hasPower = false;

            // 1. Check solid fuel burn time
            if (be.burnTime > 0) {
                be.burnTime--;
                hasPower = true;
            } else {
                // Try consuming solid fuel
                ItemStack fuelStack = be.items.get(FUEL_SLOT_INDEX);
                int duration = getFuelBurnDuration(fuelStack);
                if (duration > 0) {
                    be.burnTime = duration;
                    be.maxBurnTime = duration;
                    fuelStack.shrink(1);
                    hasPower = true;
                    be.setChanged();
                } else {
                    int maxProgress = 0;
                    for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
                        if (canCook[i] && be.cookProgress[i] > maxProgress) {
                            maxProgress = be.cookProgress[i];
                        }
                    }
                    int requiredEnergy = (PROCESS_TIME - maxProgress) * ENERGY_PER_TICK;
                    if (be.energy >= requiredEnergy) {
                        be.energy -= ENERGY_PER_TICK;
                        be.energyStorage.set(be.energy);
                        hasPower = true;
                    }
                }
            }

            if (hasPower) {
                isActive = true;

                // Advance cooking on all active slots
                for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
                    if (canCook[i]) {
                        be.cookProgress[i]++;
                        if (be.cookProgress[i] >= PROCESS_TIME) {
                            be.cookProgress[i] = 0;
                            be.items.get(i).shrink(1);
                            be.addOutput(i + 8, results[i].copy());

                            // Clink sound on completion
                            Direction facing = state.getValue(MultiSmelterControllerBlock.FACING);
                            BlockPos chamberPos = be.getRelativePos(0, 1, 1, facing);
                            serverLevel.playSound(null, chamberPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 1.6f);
                        }
                    }
                }

                // Ambient furnace sounds and particles in chamber
                if (serverLevel.getGameTime() % 16 == 0) {
                    Direction facing = state.getValue(MultiSmelterControllerBlock.FACING);
                    BlockPos chamberPos = be.getRelativePos(0, 1, 1, facing);
                    serverLevel.playSound(null, chamberPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.7f, 1.0f);

                    serverLevel.sendParticles(ParticleTypes.FLAME,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 0.5, chamberPos.getZ() + 0.5,
                            4, 0.8, 0.3, 0.2, 0.02);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 1.2, chamberPos.getZ() + 0.5,
                            3, 0.5, 0.2, 0.2, 0.03);
                }

                be.setChanged();
            }
        } else {
            // No active cooking; if burning fuel continues running out
            if (be.burnTime > 0) {
                be.burnTime--;
                be.setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = be.getBlockState();
            if (latest.getBlock() instanceof MultiSmelterControllerBlock) {
                level.setBlock(pos, latest.setValue(MultiSmelterControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    public ItemStack getSmeltingResult(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.recipeAccess().getRecipeFor(
                RecipeType.SMELTING,
                new SingleRecipeInput(stack),
                level
        );
        return recipe.map(r -> r.value().assemble(new SingleRecipeInput(stack))).orElse(ItemStack.EMPTY);
    }

    public boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (this.level instanceof ServerLevel serverLevel) {
            return !getSmeltingResult(serverLevel, stack).isEmpty();
        }
        return true;
    }

    public static int getFuelBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(ItemTags.COALS)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(ItemTags.LOGS)) return 300;
        if (stack.is(ItemTags.PLANKS)) return 300;
        if (stack.is(ItemTags.WOODEN_SLABS)) return 150;
        if (stack.is(Items.STICK)) return 100;
        if (stack.is(RefineryMod.TAR_ITEM.get())) return 1600;
        if (stack.is(RefineryMod.BITUMEN.get())) return 1600;
        if (stack.is(RefineryFluids.ETHANOL_BUCKET.get())) return 4000;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && (id.getPath().contains("tar") || id.getPath().contains("bitumen"))) return 1600;
        if (stack.has(DataComponents.COOKING_FUEL)) return 1600;
        return 0;
    }

    private boolean canFitOutput(int slot, ItemStack output) {
        ItemStack current = this.items.get(slot);
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, output)) return false;
        return current.getCount() + output.getCount() <= current.getMaxStackSize();
    }

    private void addOutput(int slot, ItemStack output) {
        ItemStack current = this.items.get(slot);
        if (current.isEmpty()) {
            this.items.set(slot, output);
        } else if (ItemStack.isSameItemSameComponents(current, output)) {
            current.grow(output.getCount());
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof MultiSmelterControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(MultiSmelterControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(MultiSmelterControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(MultiSmelterControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 1.0,
                            8, 0.8, 0.5, 0.8, 0.05);
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
                if (current.getBlock() instanceof MultiSmelterControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MultiSmelterControllerBlock.ASSEMBLED, true)
                            .setValue(MultiSmelterControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Multi-Smelter]§r 5x4x3 Multiblock is §2VALID & ASSEMBLED§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof MultiSmelterControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(MultiSmelterControllerBlock.ASSEMBLED, false)
                            .setValue(MultiSmelterControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Multi-Smelter]§r 5x4x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, @Nullable Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(60);
        Block controller = RefineryMod.MULTI_SMELTER_CONTROLLER.get();
        Block casing = RefineryMod.SMELTER_CASING.get();
        Block chamber = RefineryMod.SMELTER_CHAMBER.get();
        Block inputPort = RefineryMod.SMELTER_INPUT_PORT.get();
        Block outputPort = RefineryMod.SMELTER_OUTPUT_PORT.get();
        Block fuelPort = RefineryMod.SMELTER_FUEL_PORT.get();

        // 5 wide (X: -2..2), 4 high (Y: 0..3), 3 deep (Z: 0..2)
        for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -2; x <= 2; x++) {
                    // Hollow interior core (X in [-1, 0, 1], Y in [1, 2], Z = 1)
                    if ((x >= -1 && x <= 1) && (y == 1 || y == 2) && z == 1) {
                        list.add(new BlueprintRequirement(x, y, z, null, "Hollow Combustion Chamber (Air)", 0xFFF59E0B));
                        continue;
                    }

                    // Front viewing windows (X in [-1, 0, 1], Y in [1, 2], Z = 0)
                    if ((x >= -1 && x <= 1) && (y == 1 || y == 2) && z == 0) {
                        list.add(new BlueprintRequirement(x, y, z, chamber, "Smelter Chamber Glass", 0xFF38BDF8));
                        continue;
                    }

                    // Controller at (0, 0, 0)
                    if (x == 0 && y == 0 && z == 0) {
                        list.add(new BlueprintRequirement(x, y, z, controller, "Multi-Smelter Controller", 0xFF10B981));
                        continue;
                    }

                    // Suggested port locations on bottom front/back:
                    if (x == -2 && y == 0 && z == 0) {
                        list.add(new BlueprintRequirement(x, y, z, inputPort, "Smelter Input Port (or Casing)", 0xFF6366F1));
                        continue;
                    }
                    if (x == 2 && y == 0 && z == 0) {
                        list.add(new BlueprintRequirement(x, y, z, outputPort, "Smelter Output Port (or Casing)", 0xFFEC4899));
                        continue;
                    }
                    if (x == 0 && y == 0 && z == 2) {
                        list.add(new BlueprintRequirement(x, y, z, fuelPort, "Smelter Fuel/Energy Port (or Casing)", 0xFFEAB308));
                        continue;
                    }

                    // Everything else is Smelter Casing
                    list.add(new BlueprintRequirement(x, y, z, casing, "Smelter Structural Casing", 0xFF94A3B8));
                }
            }
        }
        return list;
    }

    public boolean checkStructure(boolean notifyPlayer, @Nullable Player player) {
        if (this.level == null) return false;
        BlockState controllerState = getBlockState();
        if (!(controllerState.getBlock() instanceof MultiSmelterControllerBlock)) return false;

        Direction facing = controllerState.getValue(MultiSmelterControllerBlock.FACING);

        for (BlueprintRequirement req : getRequirements()) {
            BlockPos pos = getRelativePos(req.x(), req.y(), req.z(), facing);
            BlockState state = this.level.getBlockState(pos);

            // Hollow interior core must be air
            if (req.expectedBlock() == null) {
                if (!state.isAir()) {
                    if (notifyPlayer && player != null) {
                        player.sendSystemMessage(Component.literal("§c[Multi-Smelter]§r Interior Chamber must be hollow air at " + pos.toShortString()));
                    }
                    return false;
                }
                continue;
            }

            // Controller must be the controller block
            if (req.x() == 0 && req.y() == 0 && req.z() == 0) {
                if (!state.is(RefineryMod.MULTI_SMELTER_CONTROLLER.get())) {
                    return false;
                }
                continue;
            }

            // Chamber windows must be SmelterChamberBlock (or casing)
            if ((req.x() >= -1 && req.x() <= 1) && (req.y() == 1 || req.y() == 2) && req.z() == 0) {
                if (!state.is(RefineryMod.SMELTER_CHAMBER.get()) && !isSmelterCasingOrPort(state)) {
                    if (notifyPlayer && player != null) {
                        player.sendSystemMessage(Component.literal("§c[Multi-Smelter]§r Missing Smelter Chamber Glass at " + pos.toShortString()));
                    }
                    return false;
                }
                continue;
            }

            // Any other position: SmelterCasing or any SmelterPort
            if (!isSmelterCasingOrPort(state)) {
                if (notifyPlayer && player != null) {
                    player.sendSystemMessage(Component.literal("§c[Multi-Smelter]§r Missing " + req.name() + " at " + pos.toShortString()));
                }
                return false;
            }
        }

        return true;
    }

    public static boolean isSmelterCasingOrPort(BlockState state) {
        Block b = state.getBlock();
        return b instanceof SmelterCasingBlock
                || b instanceof SmelterPortBlock
                || b instanceof SmelterChamberBlock;
    }

    public void linkPorts() {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = getBlockState().getValue(MultiSmelterControllerBlock.FACING);

        for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -2; x <= 2; x++) {
                    BlockPos p = getRelativePos(x, y, z, facing);
                    if (this.level.getBlockEntity(p) instanceof SmelterPortBlockEntity portBE) {
                        portBE.setControllerPos(this.worldPosition);
                    }
                }
            }
        }
    }

    public BlockPos getRelativePos(int rightOffset, int upOffset, int backOffset, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, rightOffset)
                .relative(Direction.UP, upOffset)
                .relative(back, backOffset);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("Assembled", this.assembled);
        output.putInt("Energy", this.energy);
        output.putInt("BurnTime", this.burnTime);
        output.putInt("MaxBurnTime", this.maxBurnTime);
        output.putInt("ExportCooldown", this.exportCooldown);
        output.putInt("LastExportSlot", this.lastExportSlot);
        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            output.putInt("CookProgress_" + i, this.cookProgress[i]);
        }
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("Assembled", false);
        this.energy = input.getIntOr("Energy", 0);
        this.burnTime = input.getIntOr("BurnTime", 0);
        this.maxBurnTime = input.getIntOr("MaxBurnTime", 0);
        this.exportCooldown = input.getIntOr("ExportCooldown", 0);
        this.lastExportSlot = input.getIntOr("LastExportSlot", 0);
        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            this.cookProgress[i] = input.getIntOr("CookProgress_" + i, 0);
        }
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }

    // WorldlyContainer
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        } else if (side == Direction.DOWN) {
            return new int[]{8, 9, 10, 11, 12, 13, 14, 15};
        } else {
            return new int[]{16}; // Sides accept fuel
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        if (index >= 0 && index < 8) return canSmelt(stack);
        if (index == 16) return getFuelBurnDuration(stack) > 0;
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 8 && index <= 15;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.items) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack res = ContainerHelper.removeItem(this.items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
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
    public Component getDisplayName() {
        return Component.literal("Industrial Multi-Smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MultiSmelterMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    public void syncToClient() {
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public boolean isMultiblockPart(BlockPos pos) {
        BlockState state = getBlockState();
        Direction facing = (state.getBlock() instanceof MultiSmelterControllerBlock && state.hasProperty(MultiSmelterControllerBlock.FACING))
                ? state.getValue(MultiSmelterControllerBlock.FACING)
                : Direction.NORTH;
        for (BlueprintRequirement req : getRequirements()) {
            if (getRelativePos(req.x(), req.y(), req.z(), facing).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private void tryExportOutput(ServerLevel level) {
        Direction facing = getBlockState().getValue(MultiSmelterControllerBlock.FACING);
        Direction right = facing.getClockWise();

        // Check if there are any finished items to export from output buffer slots 8..15
        boolean hasOutput = false;
        for (int slot = 8; slot <= 15; slot++) {
            if (!this.items.get(slot).isEmpty()) {
                hasOutput = true;
                break;
            }
        }
        if (!hasOutput) return;

        // Collect all potential export target positions in priority order:
        Set<BlockPos> targetPositions = new LinkedHashSet<>();

        // 1. Output Ports: Check all 6 adjacent directions around any SMELTER_OUTPUT_PORT in the structure
        for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -2; x <= 2; x++) {
                    BlockPos p = getRelativePos(x, y, z, facing);
                    if (this.level.getBlockEntity(p) instanceof SmelterPortBlockEntity portBE) {
                        if (portBE.getPortType() == SmelterPortBlock.PortType.OUTPUT) {
                            for (Direction dir : Direction.values()) {
                                targetPositions.add(p.relative(dir));
                            }
                        }
                    }
                }
            }
        }

        // 2. Default output port (2, 0, 0) adjacent positions
        BlockPos defaultPortPos = getRelativePos(2, 0, 0, facing);
        targetPositions.add(defaultPortPos.relative(facing));        // In front of output port
        targetPositions.add(defaultPortPos.relative(right));         // To the right of output port
        targetPositions.add(defaultPortPos.below());                 // Below output port
        targetPositions.add(defaultPortPos.relative(facing).below());// Below in front of output port

        // 3. Directly in front of the Controller (0, 0, 0)
        BlockPos controllerPos = this.worldPosition;
        targetPositions.add(controllerPos.relative(facing));         // In front of controller
        targetPositions.add(controllerPos.relative(facing).below()); // Below in front of controller

        // 4. Front face of the entire 5-wide base (X: -2..2, Z = -1, Y = 0, Y = -1, and Y = 1)
        for (int x = -2; x <= 2; x++) {
            BlockPos frontPos = getRelativePos(x, 0, -1, facing);
            targetPositions.add(frontPos);
            targetPositions.add(frontPos.below());
            targetPositions.add(frontPos.above());
        }

        // 5. Perimeter blocks around the 5x3 base (Layer 0)
        for (int x = -2; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                if (x == -2) targetPositions.add(getRelativePos(x - 1, 0, z, facing));
                if (x == 2)  targetPositions.add(getRelativePos(x + 1, 0, z, facing));
                if (z == 0)  targetPositions.add(getRelativePos(x, 0, z - 1, facing));
                if (z == 2)  targetPositions.add(getRelativePos(x, 0, z + 1, facing));
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
                // Ensure conveyor doesn't lead directly back into the multi-smelter
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
                    continue; // Skip conveyor pointing back into smelter
                }

                // Check that the conveyor belt surface near spawn point isn't currently occupied by an item entity
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

                for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
                    int slot = 8 + ((this.lastExportSlot + i) % OUTPUT_SLOTS_COUNT);
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
                    this.lastExportSlot = (slot - 8 + 1) % OUTPUT_SLOTS_COUNT;
                    setChanged();
                    syncToClient();
                    return;
                }
                continue;
            }

            // Direct pipe insertion
            if (targetBE instanceof IndustrialPipeBlockEntity pipeBE && pipeBE.getPipeType().handlesItems()) {
                for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
                    int slot = 8 + ((this.lastExportSlot + i) % OUTPUT_SLOTS_COUNT);
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(pipeBE, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        this.lastExportSlot = (slot - 8 + 1) % OUTPUT_SLOTS_COUNT;
                        setChanged();
                        syncToClient();
                        return;
                    }
                }
                continue;
            }

            // Target is a standard Container (chests, barrels, hoppers, crates)
            if (targetBE instanceof Container targetContainer) {
                for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
                    int slot = 8 + ((this.lastExportSlot + i) % OUTPUT_SLOTS_COUNT);
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(targetContainer, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        this.lastExportSlot = (slot - 8 + 1) % OUTPUT_SLOTS_COUNT;
                        setChanged();
                        targetContainer.setChanged();
                        syncToClient();
                        return;
                    }
                }
            } else {
                // External NeoForge item capability handler
                var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, targetPos, Direction.UP);
                if (handler != null) {
                    for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
                        int slot = 8 + ((this.lastExportSlot + i) % OUTPUT_SLOTS_COUNT);
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
                                this.lastExportSlot = (slot - 8 + 1) % OUTPUT_SLOTS_COUNT;
                                setChanged();
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
