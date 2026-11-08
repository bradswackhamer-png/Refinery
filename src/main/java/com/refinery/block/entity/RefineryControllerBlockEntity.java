package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CombustionChamberBlock;
import com.refinery.block.RefineryControllerBlock;
import com.refinery.block.RefineryPortBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.RefineryMenu;
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
import java.util.Optional;

public class RefineryControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int TANK_CAPACITY = 16_000;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int OIL_PER_RECIPE = 1_000;
    public static final int RECIPE_TICKS = 100;
    public static final int OIL_SAND_TICKS = 60;

    private boolean assembled = false;
    private int crudeOil = 0;
    private int liquidTar = 0;
    private int diesel = 0;
    private int gasoline = 0;

    private boolean stage1Enabled = true;
    private boolean stage2Enabled = true;
    private boolean stage3Enabled = true;

    private int currentHeat = IRefineryFurnace.AMBIENT_TEMP;
    private int furnaceType = 0; // 0 = None, 1 = Burner Furnace, 2 = Electric Furnace
    private int progress = 0;
    private int oilSandProgress = 0;

    // Retained for backward compatibility with any external energy queries
    private int energy = 0;
    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1_000, 0);

    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(RefineryMenu.CONTAINER_SIZE, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> RefineryControllerBlockEntity.this.crudeOil;
                case 1 -> RefineryControllerBlockEntity.this.liquidTar;
                case 2 -> RefineryControllerBlockEntity.this.diesel;
                case 3 -> RefineryControllerBlockEntity.this.gasoline;
                case 4 -> RefineryControllerBlockEntity.this.progress;
                case 5 -> RefineryControllerBlockEntity.this.currentHeat;
                case 6 -> RefineryControllerBlockEntity.this.furnaceType;
                case 7 -> RefineryControllerBlockEntity.this.getStageFlags();
                case 8 -> RefineryControllerBlockEntity.this.assembled ? 1 : 0;
                case 9 -> RefineryControllerBlockEntity.this.oilSandProgress;
                case 10 -> OIL_SAND_TICKS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> RefineryControllerBlockEntity.this.crudeOil = value;
                case 1 -> RefineryControllerBlockEntity.this.liquidTar = value;
                case 2 -> RefineryControllerBlockEntity.this.diesel = value;
                case 3 -> RefineryControllerBlockEntity.this.gasoline = value;
                case 4 -> RefineryControllerBlockEntity.this.progress = value;
                case 5 -> RefineryControllerBlockEntity.this.currentHeat = value;
                case 6 -> RefineryControllerBlockEntity.this.furnaceType = value;
                case 7 -> RefineryControllerBlockEntity.this.setStageFlags(value);
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
    public int getLiquidTar() { return this.liquidTar; }
    public int getDiesel() { return this.diesel; }
    public int getGasoline() { return this.gasoline; }
    public int getCurrentHeat() { return this.currentHeat; }
    public int getFurnaceType() { return this.furnaceType; }
    public int getProgress() { return this.progress; }
    public int getStoredEnergy() { return this.energy; }

    public boolean isStage1Enabled() { return this.stage1Enabled; }
    public boolean isStage2Enabled() { return this.stage2Enabled; }
    public boolean isStage3Enabled() { return this.stage3Enabled; }

    public int getStageFlags() {
        int flags = 0;
        if (this.stage1Enabled) flags |= 1;
        if (this.stage2Enabled) flags |= 2;
        if (this.stage3Enabled) flags |= 4;
        return flags;
    }

    public void setStageFlags(int flags) {
        this.stage1Enabled = (flags & 1) != 0;
        this.stage2Enabled = (flags & 2) != 0;
        this.stage3Enabled = (flags & 4) != 0;
        setChanged();
        syncToClient();
    }

    public void toggleStage(int stage) {
        switch (stage) {
            case 1 -> this.stage1Enabled = !this.stage1Enabled;
            case 2 -> this.stage2Enabled = !this.stage2Enabled;
            case 3 -> this.stage3Enabled = !this.stage3Enabled;
        }
        this.progress = 0;
        setChanged();
        syncToClient();
    }

    public void setCrudeOil(int amt) {
        this.crudeOil = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    public void setLiquidTar(int amt) {
        this.liquidTar = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    public void setDiesel(int amt) {
        this.diesel = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    public void setGasoline(int amt) {
        this.gasoline = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
        syncToClient();
    }

    public int fillCrudeOil(int amount) {
        int space = TANK_CAPACITY - this.crudeOil;
        int filled = Math.min(space, amount);
        if (filled > 0) {
            this.crudeOil += filled;
            setChanged();
            syncToClient();
        }
        return filled;
    }

    public int drainLiquidTar(int amount) {
        int drained = Math.min(this.liquidTar, amount);
        if (drained > 0) {
            this.liquidTar -= drained;
            setChanged();
            syncToClient();
        }
        return drained;
    }

    public int drainDiesel(int amount) {
        int drained = Math.min(this.diesel, amount);
        if (drained > 0) {
            this.diesel -= drained;
            setChanged();
            syncToClient();
        }
        return drained;
    }

    public int drainGasoline(int amount) {
        int drained = Math.min(this.gasoline, amount);
        if (drained > 0) {
            this.gasoline -= drained;
            setChanged();
            syncToClient();
        }
        return drained;
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
        if (this.assembled) {
            this.assembled = false;
            updateMemberBlockStates(false);
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = this.getBlockState();
                if (state.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, state
                            .setValue(RefineryControllerBlock.ASSEMBLED, false)
                            .setValue(RefineryControllerBlock.ACTIVE, false)
                            .setValue(RefineryControllerBlock.BLUEPRINT, false), 3);
                }
            }
            setChanged();
            syncToClient();
        }
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof RefineryControllerBlock && getBlockState().hasProperty(RefineryControllerBlock.FACING))
                ? getBlockState().getValue(RefineryControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x == 0 && req.y == 0 && req.z == 0) continue;
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(RefineryControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(RefineryControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(RefineryControllerBlock.ASSEMBLED, isAssembled), 3);
                }
            }
        }
    }

    // === Stage Distillation Yield Calculations per 1,000 mB Crude Oil ===

    public int getRecipeTarOutput() {
        return getRecipeTarOutput(this.stage1Enabled, this.stage2Enabled, this.stage3Enabled);
    }

    public int getRecipeDieselOutput() {
        return getRecipeDieselOutput(this.stage1Enabled, this.stage2Enabled, this.stage3Enabled);
    }

    public int getRecipeGasolineOutput() {
        return getRecipeGasolineOutput(this.stage1Enabled, this.stage2Enabled, this.stage3Enabled);
    }

    public float getRecipeBitumenChance() {
        return getRecipeBitumenChance(this.stage1Enabled, this.stage2Enabled, this.stage3Enabled);
    }

    public static int getRecipeTarOutput(boolean s1, boolean s2, boolean s3) {
        if (!s1) return 0;
        if (!s2 && !s3) return 500;
        return 250;
    }

    public static int getRecipeDieselOutput(boolean s1, boolean s2, boolean s3) {
        if (!s2) return 0;
        if (!s1 && !s3) return 500;
        return 250;
    }

    public static int getRecipeGasolineOutput(boolean s1, boolean s2, boolean s3) {
        if (!s3) return 0;
        if (!s1 && !s2) return 500;
        return 250;
    }

    public static float getRecipeBitumenChance(boolean s1, boolean s2, boolean s3) {
        if (!s1) return 0.0f;
        if (s2 && s3) return 0.25f;
        return 1.0f;
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

        // Scan adjacent blocks around Stage 1 for a connected Refinery Furnace (Heat Source)
        scanConnectedFurnace(serverLevel, state.getValue(RefineryControllerBlock.FACING));

        if (!this.assembled) {
            if (state.getValue(RefineryControllerBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(RefineryControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        // Process Crude Oil Buckets placed into Slot 0
        processCrudeBucketSlot();

        boolean wasActive = state.getValue(RefineryControllerBlock.ACTIVE);
        boolean isActive = false;
        boolean hasHeat = this.currentHeat >= IRefineryFurnace.MIN_OPERATING_TEMP;

        // 1. Fractional Distillation Process (1,000 mB Crude Oil -> Stage Outputs)
        boolean anyStageEnabled = this.stage1Enabled || this.stage2Enabled || this.stage3Enabled;
        int tarOut = getRecipeTarOutput();
        int dieselOut = getRecipeDieselOutput();
        int gasOut = getRecipeGasolineOutput();
        float bitumenChance = getRecipeBitumenChance();

        boolean hasTankSpace = (TANK_CAPACITY - this.liquidTar) >= tarOut
                && (TANK_CAPACITY - this.diesel) >= dieselOut
                && (TANK_CAPACITY - this.gasoline) >= gasOut;
        boolean hasItemSpace = bitumenChance <= 0.0f || canInsertByproduct(new ItemStack(RefineryMod.BITUMEN.get(), 1));

        boolean canDistill = anyStageEnabled
                && this.crudeOil >= OIL_PER_RECIPE
                && hasTankSpace
                && hasItemSpace;

        if (canDistill && hasHeat) {
            isActive = true;
            this.progress++;

            // Distillation steam/vapor from top of Stage 3 column (Y = 8)
            if (level.getRandom().nextInt(4) == 0) {
                Direction facing = state.getValue(RefineryControllerBlock.FACING);
                BlockPos topCenter = getRelativePos(0, 8, 1, facing);
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        topCenter.getX() + 0.5, topCenter.getY() + 1.1, topCenter.getZ() + 0.5,
                        1, 0.05, 0.1, 0.05, 0.02);
            }

            if (this.progress >= RECIPE_TICKS) {
                this.progress = 0;
                this.crudeOil -= OIL_PER_RECIPE;
                this.liquidTar = Math.min(TANK_CAPACITY, this.liquidTar + tarOut);
                this.diesel = Math.min(TANK_CAPACITY, this.diesel + dieselOut);
                this.gasoline = Math.min(TANK_CAPACITY, this.gasoline + gasOut);

                if (bitumenChance >= 1.0f || (bitumenChance > 0.0f && level.getRandom().nextFloat() < bitumenChance)) {
                    insertByproduct(new ItemStack(RefineryMod.BITUMEN.get(), 1));
                }

                serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.2f);
                setChanged();
                syncToClient();
            }
        } else {
            if (!canDistill && this.progress > 0) {
                this.progress = 0;
                setChanged();
                syncToClient();
            }
        }

        // 2. Oil Sand Feedstock Extraction Process (Oil Sand -> 200 mB Crude Oil + 1 Sand) when heated
        ItemStack feedstock = this.items.get(0);
        boolean isOilSandFeed = isOilSand(feedstock);
        boolean hasOilSandRecipe = isOilSandFeed && (TANK_CAPACITY - this.crudeOil) >= 200 && canInsertSandSlot();

        if (hasOilSandRecipe && hasHeat) {
            isActive = true;
            this.oilSandProgress++;

            if (this.oilSandProgress >= OIL_SAND_TICKS) {
                this.oilSandProgress = 0;
                feedstock.shrink(1);
                this.crudeOil = Math.min(TANK_CAPACITY, this.crudeOil + 200);
                insertSandReturn(new ItemStack(Items.SAND, 1));
                serverLevel.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.7f, 1.0f);
                setChanged();
                syncToClient();
            }
        } else if (!hasOilSandRecipe && this.oilSandProgress > 0) {
            this.oilSandProgress = 0;
            setChanged();
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof RefineryControllerBlock) {
                level.setBlock(pos, latest.setValue(RefineryControllerBlock.ACTIVE, isActive), 3);
            }
            syncToClient();
        }
    }

    private void scanConnectedFurnace(ServerLevel level, Direction facing) {
        int bestTemp = IRefineryFurnace.AMBIENT_TEMP;
        int bestType = 0;

        // Check perimeter around Stage 1 (localX: -2..2, localY: 0..2, localZ: -1..3)
        for (int ly = 0; ly <= 1; ly++) {
            for (int lx = -2; lx <= 2; lx++) {
                for (int lz = -1; lz <= 3; lz++) {
                    // Only check outside or edge of the 3x3 tower footprint
                    if (lx >= -1 && lx <= 1 && lz >= 0 && lz <= 2) continue;
                    BlockPos checkPos = getRelativePos(lx, ly, lz, facing);
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof IRefineryFurnace furnace) {
                        int t = furnace.getTemperature();
                        int type = furnace.isElectric() ? 2 : 1;
                        if (bestType == 0 || t > bestTemp) {
                            bestTemp = t;
                            bestType = type;
                        }
                    }
                }
            }
        }

        if (this.currentHeat != bestTemp || this.furnaceType != bestType) {
            this.currentHeat = bestTemp;
            this.furnaceType = bestType;
            setChanged();
        }
    }

    private void processCrudeBucketSlot() {
        ItemStack input = this.items.get(0);
        if (input.is(RefineryFluids.CRUDE_OIL_BUCKET.get())) {
            if (this.crudeOil + 1000 <= TANK_CAPACITY) {
                ItemStack ret = this.items.get(1);
                if (ret.isEmpty() || (ret.is(Items.BUCKET) && ret.getCount() < ret.getMaxStackSize())) {
                    this.crudeOil += 1000;
                    input.shrink(1);
                    if (ret.isEmpty()) {
                        this.items.set(1, new ItemStack(Items.BUCKET));
                    } else {
                        ret.grow(1);
                    }
                    setChanged();
                    syncToClient();
                }
            }
        }
    }

    private boolean canInsertSandSlot() {
        ItemStack ret = this.items.get(1);
        return ret.isEmpty() || (ret.is(Items.SAND) && ret.getCount() < ret.getMaxStackSize());
    }

    private void insertSandReturn(ItemStack sand) {
        ItemStack ret = this.items.get(1);
        if (ret.isEmpty()) {
            this.items.set(1, sand);
        } else if (ret.is(Items.SAND) && ret.getCount() < ret.getMaxStackSize()) {
            ret.grow(sand.getCount());
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
            updateMemberBlockStates(valid);
            BlockState newState = currentState.setValue(RefineryControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(RefineryControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 0.9f);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 4.5, this.worldPosition.getZ() + 0.5, 4, 0.3, 1.5, 0.3, 0.05);
                }
            } else {
                newState = newState.setValue(RefineryControllerBlock.ACTIVE, false);
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        } else if (valid) {
            updateMemberBlockStates(true);
            linkPorts();
        }
    }

    private boolean canInsertByproduct(ItemStack byproduct) {
        for (int i = 2; i <= 5; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(stack, byproduct) && stack.getCount() + byproduct.getCount() <= stack.getMaxStackSize()) {
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
        if (stack.is(RefineryMod.OIL_SAND_ITEM.get()) || stack.is(RefineryMod.OIL_RED_SAND_ITEM.get())) return true;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            String path = id.getPath();
            if (path.contains("oil_sand") || path.contains("oilsand")) return true;
        }
        return false;
    }

    public static int getFuelBurnDuration(ItemStack stack) {
        return BurnerRefineryFurnaceBlockEntity.getFuelBurnDuration(stack);
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
            updateMemberBlockStates(true);
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(RefineryControllerBlock.ASSEMBLED, true).setValue(RefineryControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Industrial Refinery]§r 3-Stage Fractional Tower (3x9x3) is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            updateMemberBlockStates(false);
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof RefineryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(RefineryControllerBlock.ASSEMBLED, false).setValue(RefineryControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Industrial Refinery]§r 3-Stage Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    /**
     * 3-Stage Fractional Distillation Tower (3x9x3 total = three 3x3x3 vertical stages):
     * - Stage 1 (Bottom 3x3x3, Y = 0..2): Liquid Tar & Bitumen Fraction
     * - Stage 2 (Middle 3x3x3, Y = 3..5): Diesel Fuel Fraction
     * - Stage 3 (Top 3x3x3,    Y = 6..8): Gasoline / Light Gas Fraction
     */
    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(81);

        // =====================================================================
        // STAGE 1 (BOTTOM 3x3x3: Y = 0, 1, 2) - Liquid Tar & Bitumen
        // =====================================================================
        // Layer 0 (Y = 0)
        list.add(new BlueprintRequirement(-1, 0, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 0, RefineryMod.REFINERY_CONTROLLER.get(), "Refinery Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 1, RefineryMod.CRUDE_OIL_PORT.get(), "Crude Oil Input Port", 0xFF18181B));
        list.add(new BlueprintRequirement( 0, 0, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
        list.add(new BlueprintRequirement( 1, 0, 1, RefineryMod.BYPRODUCT_PORT.get(), "Byproduct Output Port", 0xFF475569));
        list.add(new BlueprintRequirement(-1, 0, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, RefineryMod.TAR_PORT.get(), "Liquid Tar Port", 0xFF78350F));
        list.add(new BlueprintRequirement( 1, 0, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));

        // Layer 1 (Y = 1)
        list.add(new BlueprintRequirement(-1, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 1, 0, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 1, RefineryMod.HEAT_EXCHANGER.get(), "Refinery Heat Exchanger", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 0, 1, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
        list.add(new BlueprintRequirement( 1, 1, 1, RefineryMod.HEAT_EXCHANGER.get(), "Refinery Heat Exchanger", 0xFF38BDF8));
        list.add(new BlueprintRequirement(-1, 1, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 1, 2, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));

        // Layer 2 (Y = 2)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 2, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else {
                    list.add(new BlueprintRequirement(x, 2, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // =====================================================================
        // STAGE 2 (MIDDLE 3x3x3: Y = 3, 4, 5) - Diesel Fuel
        // =====================================================================
        // Layer 3 (Y = 3)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 3, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else if (x == 0 && z == 2) {
                    list.add(new BlueprintRequirement(0, 3, 2, RefineryMod.DIESEL_PORT.get(), "Diesel Output Port", 0xFFEAB308));
                } else {
                    list.add(new BlueprintRequirement(x, 3, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // Layer 4 (Y = 4)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 4, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else if ((x == -1 || x == 1) && z == 1) {
                    list.add(new BlueprintRequirement(x, 4, 1, RefineryMod.HEAT_EXCHANGER.get(), "Refinery Heat Exchanger", 0xFF38BDF8));
                } else {
                    list.add(new BlueprintRequirement(x, 4, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // Layer 5 (Y = 5)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 5, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else {
                    list.add(new BlueprintRequirement(x, 5, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // =====================================================================
        // STAGE 3 (TOP 3x3x3: Y = 6, 7, 8) - Gasoline
        // =====================================================================
        // Layer 6 (Y = 6)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 6, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else if (x == 0 && z == 2) {
                    list.add(new BlueprintRequirement(0, 6, 2, RefineryMod.GAS_PORT.get(), "Gas Output Port", 0xFF06B6D4));
                } else {
                    list.add(new BlueprintRequirement(x, 6, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // Layer 7 (Y = 7)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 7, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else if ((x == -1 || x == 1) && z == 1) {
                    list.add(new BlueprintRequirement(x, 7, 1, RefineryMod.HEAT_EXCHANGER.get(), "Refinery Heat Exchanger", 0xFF38BDF8));
                } else {
                    list.add(new BlueprintRequirement(x, 7, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        // Layer 8 (Y = 8)
        for (int z = 0; z <= 2; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && z == 1) {
                    list.add(new BlueprintRequirement(0, 8, 1, RefineryMod.DISTILLATION_COLUMN.get(), "Distillation Column", 0xFFCBD5E1));
                } else {
                    list.add(new BlueprintRequirement(x, 8, z, RefineryMod.REINFORCED_CASING.get(), "Reinforced Refinery Casing", 0xFF64748B));
                }
            }
        }

        return list;
    }

    public boolean matchesBlockRequirement(BlueprintRequirement req, BlockState state) {
        if (state.is(req.expectedBlock)) return true;
        // Controller at (0, 0, 0) is strict
        if (req.x == 0 && req.y == 0 && req.z == 0) {
            return state.is(RefineryMod.REFINERY_CONTROLLER.get());
        }
        // Center column spine (0, y, 1) accepts Distillation Column or Combustion Chamber
        if (req.x == 0 && req.z == 1) {
            return state.is(RefineryMod.DISTILLATION_COLUMN.get()) || state.is(RefineryMod.COMBUSTION_CHAMBER.get());
        }
        // Outer shell positions flexibly allow Reinforced Casing, Heat Exchanger, Combustion Chamber, or any Refinery Port
        // so players can place ports at convenient pipe heights if desired!
        return state.is(RefineryMod.REINFORCED_CASING.get())
                || state.is(RefineryMod.HEAT_EXCHANGER.get())
                || state.is(RefineryMod.COMBUSTION_CHAMBER.get())
                || state.getBlock() instanceof RefineryPortBlock;
    }

    public BlockPos getRelativePos(int localX, int localY, int localZ, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, localX)
                .relative(Direction.UP, localY)
                .relative(back, localZ);
    }

    public boolean isMultiblockPart(BlockPos pos) {
        if (this.worldPosition.equals(pos)) return true;
        Direction facing = getBlockState().getValue(RefineryControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            if (getRelativePos(req.x, req.y, req.z, facing).equals(pos)) {
                return true;
            }
        }
        return false;
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
            if (matchesBlockRequirement(req, state)) {
                matched++;
            } else {
                if (missing.size() < 4) {
                    int stageNum = (req.y / 3) + 1;
                    missing.add("Stage " + stageNum + " (Layer " + req.y + "): " + req.name + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Refinery Diagnostic]§r 3-Stage Tower blocks in place: §a" + matched + " / " + reqs.size()));
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
        output.putInt("liquidTar", this.liquidTar);
        output.putInt("diesel", this.diesel);
        output.putInt("gasoline", this.gasoline);
        output.putBoolean("stage1Enabled", this.stage1Enabled);
        output.putBoolean("stage2Enabled", this.stage2Enabled);
        output.putBoolean("stage3Enabled", this.stage3Enabled);
        output.putInt("currentHeat", this.currentHeat);
        output.putInt("furnaceType", this.furnaceType);
        output.putInt("progress", this.progress);
        output.putInt("oilSandProgress", this.oilSandProgress);

        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.crudeOil = input.getIntOr("crudeOil", 0);
        this.liquidTar = input.getIntOr("liquidTar", 0);
        this.diesel = input.getIntOr("diesel", 0);
        this.gasoline = input.getIntOr("gasoline", 0);
        this.stage1Enabled = input.getBooleanOr("stage1Enabled", true);
        this.stage2Enabled = input.getBooleanOr("stage2Enabled", true);
        this.stage3Enabled = input.getBooleanOr("stage3Enabled", true);
        this.currentHeat = input.getIntOr("currentHeat", IRefineryFurnace.AMBIENT_TEMP);
        this.furnaceType = input.getIntOr("furnaceType", 0);
        this.progress = input.getIntOr("progress", 0);
        this.oilSandProgress = input.getIntOr("oilSandProgress", 0);

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
    private static final int[] SLOTS_DOWN = new int[]{1, 2, 3, 4, 5};
    private static final int[] SLOTS_INPUT = new int[]{0};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        }
        return SLOTS_INPUT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(RefineryFluids.CRUDE_OIL_BUCKET.get()) || isOilSand(stack);
        }
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
        return Container.stillValidBlockEntity(this, player, 16.0F);
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
