package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CombustionChamberBlock;
import com.refinery.block.RefineryControllerBlock;
import com.refinery.block.RefineryPortBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.RefineryMenu;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.Optional;
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

public class RefineryControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 16_000;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int ENERGY_PER_TICK = 40;
    public static final int OIL_PER_RECIPE = 200;
    public static final int DIESEL_PER_RECIPE = 120;
    public static final int GAS_PER_RECIPE = 60;
    public static final int RECIPE_TICKS = 100;
    public static final int OIL_SAND_TICKS = 60;

    private boolean assembled = false;
    private int crudeOil = 0;
    private int diesel = 0;
    private int gasoline = 0;
    private int energy = 0;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int progress = 0;
    private int oilSandProgress = 0;

    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(RefineryMenu.CONTAINER_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1_000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            RefineryControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            RefineryControllerBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> RefineryControllerBlockEntity.this.energy & 0xFFFF;
                case 1 -> (RefineryControllerBlockEntity.this.energy >> 16) & 0xFFFF;
                case 2 -> RefineryControllerBlockEntity.this.burnTime;
                case 3 -> RefineryControllerBlockEntity.this.maxBurnTime;
                case 4 -> RefineryControllerBlockEntity.this.progress;
                case 5 -> RefineryControllerBlockEntity.this.crudeOil;
                case 6 -> RefineryControllerBlockEntity.this.diesel;
                case 7 -> RefineryControllerBlockEntity.this.gasoline;
                case 8 -> RefineryControllerBlockEntity.this.assembled ? 1 : 0;
                case 9 -> RefineryControllerBlockEntity.this.oilSandProgress;
                case 10 -> OIL_SAND_TICKS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 2 -> RefineryControllerBlockEntity.this.burnTime = value;
                case 3 -> RefineryControllerBlockEntity.this.maxBurnTime = value;
                case 4 -> RefineryControllerBlockEntity.this.progress = value;
                case 5 -> RefineryControllerBlockEntity.this.crudeOil = value;
                case 6 -> RefineryControllerBlockEntity.this.diesel = value;
                case 7 -> RefineryControllerBlockEntity.this.gasoline = value;
                case 8 -> RefineryControllerBlockEntity.this.assembled = (value != 0);
                case 9 -> RefineryControllerBlockEntity.this.oilSandProgress = value;
            }
        }

        @Override
        public int getCount() {
            return RefineryMenu.DATA_COUNT;
        }
    };

    public RefineryControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.REFINERY_CONTROLLER_BE.get(), pos, blockState);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public int getCrudeOil() { return this.crudeOil; }
    public int getDiesel() { return this.diesel; }
    public int getGasoline() { return this.gasoline; }
    public int getStoredEnergy() { return this.energy; }

    public void setCrudeOil(int amt) {
        this.crudeOil = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
    }

    public void setDiesel(int amt) {
        this.diesel = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
    }

    public void setGasoline(int amt) {
        this.gasoline = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
    }

    public int fillCrudeOil(int amount) {
        int space = TANK_CAPACITY - this.crudeOil;
        int filled = Math.min(space, amount);
        if (filled > 0) {
            this.crudeOil += filled;
            setChanged();
        }
        return filled;
    }

    public int drainDiesel(int amount) {
        int drained = Math.min(this.diesel, amount);
        if (drained > 0) {
            this.diesel -= drained;
            setChanged();
        }
        return drained;
    }

    public int drainGasoline(int amount) {
        int drained = Math.min(this.gasoline, amount);
        if (drained > 0) {
            this.gasoline -= drained;
            setChanged();
        }
        return drained;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void disassemble() {
        if (this.assembled) {
            this.assembled = false;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = this.getBlockState();
                if (state.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(RefineryControllerBlock.ASSEMBLED, false).setValue(RefineryControllerBlock.ACTIVE, false).setValue(RefineryControllerBlock.BLUEPRINT, false), 3);
                }
            }
            setChanged();
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        // Periodic structure check every 20 ticks (1 second)
        if (--this.structureCheckCooldown <= 0) {
            this.structureCheckCooldown = 20;
            syncAssembly();
        }

        // Blueprint Projection Ticking
        if (this.blueprintProjectionTicks > 0) {
            this.blueprintProjectionTicks--;
            if (this.blueprintProjectionTicks == 0) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof RefineryControllerBlock && current.getValue(RefineryControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(RefineryControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (state.getValue(RefineryControllerBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(RefineryControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(RefineryControllerBlock.ACTIVE);
        boolean isActive = false;

        // Burner Chamber Logic (Dual Power: Burns solid fuel to generate internal energy)
        if (this.burnTime > 0) {
            this.burnTime--;
            if (this.energy + 50 <= ENERGY_CAPACITY) {
                this.energy += 50;
                this.energyStorage.set(this.energy);
            }
        } else {
            ItemStack fuelStack = this.items.get(0);
            if (!fuelStack.isEmpty() && this.energy < ENERGY_CAPACITY) {
                int fuelDuration = getFuelBurnDuration(fuelStack);
                if (fuelDuration > 0) {
                    this.burnTime = fuelDuration;
                    this.maxBurnTime = fuelDuration;
                    fuelStack.shrink(1);
                    setChanged();
                }
            }
        }

        // Update combustion chamber LIT state
        updateCombustionChamberState(serverLevel, this.burnTime > 0);

        // Distillation Refining Process
        // 1. Distillation Refining Process (Crude Oil -> Diesel + Gasoline + Byproducts)
        boolean hasDistillationRecipe = this.crudeOil >= OIL_PER_RECIPE
                && (TANK_CAPACITY - this.diesel) >= DIESEL_PER_RECIPE
                && (TANK_CAPACITY - this.gasoline) >= GAS_PER_RECIPE
                && canInsertByproducts();

        if (hasDistillationRecipe) {
            int requiredEnergy = (RECIPE_TICKS - this.progress) * ENERGY_PER_TICK;
            boolean hasPower = this.burnTime > 0 || this.energy >= requiredEnergy;

            if (hasPower) {
                isActive = true;
                if (this.burnTime <= 0 && this.energy >= ENERGY_PER_TICK) {
                    this.energy -= ENERGY_PER_TICK;
                    this.energyStorage.set(this.energy);
                }
                this.progress++;

                // Subtle distillation steam from top column
                if (level.getRandom().nextInt(4) == 0) {
                    Direction facing = state.getValue(RefineryControllerBlock.FACING);
                    BlockPos topCenter = getRelativePos(0, 2, 1, facing);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            topCenter.getX() + 0.5, topCenter.getY() + 1.1, topCenter.getZ() + 0.5,
                            1, 0.05, 0.1, 0.05, 0.02);
                }

                if (this.progress >= RECIPE_TICKS) {
                    this.progress = 0;
                    this.crudeOil -= OIL_PER_RECIPE;
                    this.diesel += DIESEL_PER_RECIPE;
                    this.gasoline += GAS_PER_RECIPE;

                    // Byproducts: Tar (40%) and Sulfur (30%)
                    if (level.getRandom().nextFloat() < 0.40f) {
                        insertByproduct(new ItemStack(getPreferredTarItem(), 1));
                    }
                    if (level.getRandom().nextFloat() < 0.30f) {
                        insertByproduct(new ItemStack(RefineryMod.SULFUR_DUST_ITEM.get(), 1));
                    }

                    serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.2f);
                    setChanged();
                }
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
        }

        // 2. Oil Sand Feedstock Extraction Process (Oil Sand -> 200 mB Crude Oil + 100% Sand + 40% Tar)
        ItemStack feedstock = this.items.get(1);
        boolean isOilSandFeed = isOilSand(feedstock);
        boolean hasOilSandRecipe = isOilSandFeed && (TANK_CAPACITY - this.crudeOil) >= 200 && canInsertByproducts();

        if (hasOilSandRecipe) {
            int requiredOilSandEnergy = (OIL_SAND_TICKS - this.oilSandProgress) * 20;
            boolean hasPower = this.burnTime > 0 || this.energy >= requiredOilSandEnergy;

            if (hasPower) {
                isActive = true;
                if (this.burnTime <= 0 && this.energy >= 20) {
                    this.energy -= 20;
                    this.energyStorage.set(this.energy);
                }
                this.oilSandProgress++;

                if (level.getRandom().nextInt(4) == 0) {
                    Direction facing = state.getValue(RefineryControllerBlock.FACING);
                    BlockPos portPos = getRelativePos(-1, 0, 1, facing);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            portPos.getX() + 0.5, portPos.getY() + 0.9, portPos.getZ() + 0.5,
                            1, 0.05, 0.05, 0.05, 0.01);
                }

                if (this.oilSandProgress >= OIL_SAND_TICKS) {
                    this.oilSandProgress = 0;
                    feedstock.shrink(1);
                    this.crudeOil = Math.min(TANK_CAPACITY, this.crudeOil + 200);

                    // 100% chance for 1 Sand
                    insertByproduct(new ItemStack(Items.SAND, 1));

                    // 40% chance for Tar
                    if (level.getRandom().nextFloat() < 0.40f) {
                        insertByproduct(new ItemStack(getPreferredTarItem(), 1));
                    }

                    serverLevel.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.7f, 1.0f);
                    setChanged();
                }
            }
        } else {
            if (this.oilSandProgress > 0) {
                this.oilSandProgress = 0;
                setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof RefineryControllerBlock) {
                level.setBlock(pos, latest.setValue(RefineryControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof RefineryControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(RefineryControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(RefineryControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(RefineryControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 0.9f);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
        }
    }

    private void updateCombustionChamberState(ServerLevel level, boolean lit) {
        Direction facing = getBlockState().getValue(RefineryControllerBlock.FACING);
        BlockPos chamberPos = getRelativePos(0, 0, 1, facing);
        BlockState chamberState = level.getBlockState(chamberPos);
        if (chamberState.getBlock() instanceof CombustionChamberBlock && chamberState.getValue(CombustionChamberBlock.LIT) != lit) {
            level.setBlock(chamberPos, chamberState.setValue(CombustionChamberBlock.LIT, lit), 3);
        }
    }

    private boolean canInsertByproducts() {
        for (int i = 2; i <= 5; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void insertByproduct(ItemStack byproduct) {
        for (int i = 2; i <= 5; i++) {
            ItemStack current = this.items.get(i);
            if (current.isEmpty()) {
                this.items.set(i, byproduct);
                return;
            } else if (ItemStack.isSameItemSameComponents(current, byproduct) && current.getCount() + byproduct.getCount() <= current.getMaxStackSize()) {
                current.grow(byproduct.getCount());
                return;
            }
        }
    }

    public static boolean isOilSand(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(RefineryMod.OIL_SAND_ITEM.get())) return true;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            String path = id.getPath();
            if (path.contains("oil_sand") || path.contains("oilsand")) return true;
        }
        return false;
    }

    public static int getFuelBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(ItemTags.COALS)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(RefineryMod.TAR_ITEM.get())) return 1600;
        if (stack.is(RefineryMod.BITUMEN.get())) return 1600;
        if (stack.is(RefineryFluids.ETHANOL_BUCKET.get())) return 4000;
        net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && (id.getPath().contains("tar") || id.getPath().contains("bitumen"))) return 1600;
        if (stack.has(DataComponents.COOKING_FUEL)) return 1600;
        return 0;
    }

    public static Item getPreferredTarItem() {
        Optional<Item> logTar = BuiltInRegistries.ITEM.getOptional(Identifier.fromNamespaceAndPath("logistics", "core/tar"));
        return logTar.orElseGet(RefineryMod.TAR_ITEM::get);
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            this.blueprintProjectionTicks = 0;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(RefineryControllerBlock.ASSEMBLED, true).setValue(RefineryControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Industrial Refinery]§r Multiblock is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(RefineryControllerBlock.ASSEMBLED, false).setValue(RefineryControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Industrial Refinery]§r Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        // Layer 0
        list.add(new BlueprintRequirement(-1, 0, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 0, RefineryMod.REFINERY_CONTROLLER.get(), "Refinery Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 1, RefineryMod.CRUDE_OIL_PORT.get(), "Crude Oil Port", 0xFF18181B));
        list.add(new BlueprintRequirement( 0, 0, 1, RefineryMod.COMBUSTION_CHAMBER.get(), "Combustion Chamber", 0xFFEA580C));
        list.add(new BlueprintRequirement( 1, 0, 1, RefineryMod.BYPRODUCT_PORT.get(), "Byproduct Port", 0xFF475569));
        list.add(new BlueprintRequirement(-1, 0, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 0, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));

        // Layer 1
        list.add(new BlueprintRequirement(-1, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 1, RefineryMod.HEAT_EXCHANGER.get(), "Heat Exchanger", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 0, 1, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
        list.add(new BlueprintRequirement( 1, 1, 1, RefineryMod.HEAT_EXCHANGER.get(), "Heat Exchanger", 0xFF38BDF8));
        list.add(new BlueprintRequirement(-1, 1, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 2, RefineryMod.DIESEL_PORT.get(), "Diesel Port", 0xFFEAB308));
        list.add(new BlueprintRequirement( 1, 1, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));

        // Layer 2
        list.add(new BlueprintRequirement(-1, 2, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 1, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
        list.add(new BlueprintRequirement( 1, 2, 1, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 2, RefineryMod.GAS_PORT.get(), "Gas Port", 0xFF06B6D4));
        list.add(new BlueprintRequirement( 1, 2, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Casing", 0xFF64748B));

        return list;
    }

    public BlockPos getRelativePos(int localX, int localY, int localZ, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, localX)
                .relative(Direction.UP, localY)
                .relative(back, localZ);
    }

    public boolean checkStructure(boolean notifyPlayer, @Nullable Player player) {
        if (this.level == null) return false;
        Direction facing = getBlockState().getValue(RefineryControllerBlock.FACING);
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
            player.sendSystemMessage(Component.literal("§e[Refinery Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
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
        Direction facing = getBlockState().getValue(RefineryControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof RefineryPortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("crudeOil", this.crudeOil);
        output.putInt("diesel", this.diesel);
        output.putInt("gasoline", this.gasoline);
        output.putInt("energy", this.energy);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
        output.putInt("progress", this.progress);
        output.putInt("oilSandProgress", this.oilSandProgress);

        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.crudeOil = input.getIntOr("crudeOil", 0);
        this.diesel = input.getIntOr("diesel", 0);
        this.gasoline = input.getIntOr("gasoline", 0);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
        this.progress = input.getIntOr("progress", 0);
        this.oilSandProgress = input.getIntOr("oilSandProgress", 0);

        ContainerHelper.loadAllItems(input, this.items);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_DOWN = new int[]{2, 3, 4, 5};
    private static final int[] SLOTS_UP = new int[]{1};
    private static final int[] SLOTS_SIDES = new int[]{0, 1};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        }
        if (side == Direction.UP) {
            return SLOTS_UP;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 2;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) return getFuelBurnDuration(stack) > 0;
        if (slot == 1) return isOilSand(stack);
        return false;
    }

    // === Container Implementation ===
    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) return false;
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
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RefineryMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
