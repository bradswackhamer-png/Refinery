package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.MultiSmelterControllerBlock;
import com.refinery.block.SmelterCasingBlock;
import com.refinery.block.SmelterChamberBlock;
import com.refinery.block.SmelterPortBlock;
import com.refinery.fluid.RefineryFluids;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            this.cookProgress[i] = input.getIntOr("CookProgress_" + i, 0);
        }
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
}
