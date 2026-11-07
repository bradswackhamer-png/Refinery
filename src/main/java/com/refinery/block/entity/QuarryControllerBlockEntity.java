package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.QuarryCasingBlock;
import com.refinery.block.QuarryControllerBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.config.RefineryConfig;
import com.refinery.menu.QuarryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class QuarryControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 11; // 0..8: buffer slots, 9: fence supply slot, 10: throw-back filter slot
    public static final int ENERGY_CAPACITY = 50_000;
    public static final int DEFAULT_ENERGY_PER_TICK = 240; // 240 FE/tick default at max speed
    public static final double DEFAULT_MAX_MINING_SPEED = 1.5; // 1.5 blocks/second default base speed
    public static final float PROGRESS_REQUIRED = 1.0f; // 1.0 = 1 full block progress

    public int getMaxEnergyPerTick() {
        return RefineryConfig.CONFIG != null ? RefineryConfig.CONFIG.getQuarryEnergyPerTick() : DEFAULT_ENERGY_PER_TICK;
    }

    public double getMaxMiningSpeed() {
        return RefineryConfig.CONFIG != null ? RefineryConfig.CONFIG.getQuarryMaxMiningSpeed() : DEFAULT_MAX_MINING_SPEED;
    }

    public static final int DEFAULT_RADIUS = 21; // 43x43 outer box, 41x41 inner pit
    public static final int MIN_RADIUS = 3;      // 7x7 outer box, 5x5 inner pit
    public static final int MAX_RADIUS = 21;

    // Quarry Phases
    public static final int PHASE_CLEARING = 0; // 43x43x3 outer box
    public static final int PHASE_FENCING  = 1; // lining 41x41 with fence
    public static final int PHASE_MINING   = 2; // 41x41 pit down to bedrock
    public static final int PHASE_DONE     = 3; // quarry finished

    private boolean assembled = false;
    private int energy = 0;
    private int radius = DEFAULT_RADIUS;
    private int phase = PHASE_CLEARING;

    private float miningProgress = 0.0f;
    private int blueprintTicks = 0;
    private int structureCheckCooldown = 0;
    private int exportRoundRobin = 0;

    // Mining position state
    private int currentX = -DEFAULT_RADIUS;
    private int currentZ = 1;
    private int currentY = 2; // relative to controller (2, 1, 0 for outer box)

    private int fenceIndex = 0;

    // Remaining block types tracking
    private int totalRemainingBlocks = 0;
    private final Map<Block, Integer> blockCounts = new LinkedHashMap<>();
    private final List<BlockCountEntry> clientBlockList = new ArrayList<>();
    private final Set<Block> throwBackBlocks = new LinkedHashSet<>();
    private boolean needsScan = true;
    private int scanIndex = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1_000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            QuarryControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            QuarryControllerBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    public record BlockCountEntry(Block block, int count) {}

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> QuarryControllerBlockEntity.this.energy & 0xFFFF;
                case 1 -> (QuarryControllerBlockEntity.this.energy >> 16) & 0xFFFF;
                case 2 -> QuarryControllerBlockEntity.this.assembled ? 1 : 0;
                case 3 -> (QuarryControllerBlockEntity.this.energy >= 1 && QuarryControllerBlockEntity.this.assembled && QuarryControllerBlockEntity.this.phase != PHASE_DONE) ? 1 : 0;
                case 4 -> QuarryControllerBlockEntity.this.radius;
                case 5 -> QuarryControllerBlockEntity.this.phase;
                case 6 -> QuarryControllerBlockEntity.this.getBlockPos().getY() + QuarryControllerBlockEntity.this.currentY;
                case 7 -> QuarryControllerBlockEntity.this.totalRemainingBlocks & 0xFFFF;
                case 8 -> (QuarryControllerBlockEntity.this.totalRemainingBlocks >> 16) & 0xFFFF;
                case 9 -> (int) (QuarryControllerBlockEntity.this.miningProgress * 100);
                case 10 -> (int) (Math.min(1.0f, QuarryControllerBlockEntity.this.energy / (float) QuarryControllerBlockEntity.this.getMaxEnergyPerTick()) * 100);
                case 11 -> QuarryControllerBlockEntity.this.worldPosition.getX();
                case 12 -> QuarryControllerBlockEntity.this.worldPosition.getY();
                case 13 -> QuarryControllerBlockEntity.this.worldPosition.getZ();
                case 14 -> QuarryControllerBlockEntity.this.getMaxEnergyPerTick();
                case 15 -> (int) (QuarryControllerBlockEntity.this.getMaxMiningSpeed() * 100);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 4 -> QuarryControllerBlockEntity.this.setRadius(value);
            }
        }

        @Override
        public int getCount() {
            return 16;
        }
    };

    public QuarryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.QUARRY_CONTROLLER_BE.get(), pos, state);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getPhase() {
        return this.phase;
    }

    public int getTotalRemainingBlocks() {
        return this.totalRemainingBlocks;
    }

    public List<BlockCountEntry> getClientBlockList() {
        return this.clientBlockList;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void setRadius(int newRadius) {
        int clamped = Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, newRadius));
        if (this.radius != clamped) {
            this.radius = clamped;
            resetExcavationState();
            setChanged();
            syncToClient();
        }
    }

    public void resetExcavationState() {
        this.phase = PHASE_CLEARING;
        this.currentX = -this.radius;
        this.currentZ = 1;
        this.currentY = 2; // Outer 43x43x3 box starts from same Y level as machine up 3 blocks (Y: 2 down to 0)
        this.fenceIndex = 0;
        this.miningProgress = 0.0f;
        this.needsScan = true;
    }

    public boolean isThrowBack(Block block) {
        if (this.throwBackBlocks.contains(block)) {
            return true;
        }
        // Equivalence rules: setting Stone throws back Cobblestone (and vice-versa); setting Deepslate throws back Cobbled Deepslate
        if (block == Blocks.COBBLESTONE && this.throwBackBlocks.contains(Blocks.STONE)) {
            return true;
        }
        if (block == Blocks.STONE && this.throwBackBlocks.contains(Blocks.COBBLESTONE)) {
            return true;
        }
        if (block == Blocks.COBBLED_DEEPSLATE && this.throwBackBlocks.contains(Blocks.DEEPSLATE)) {
            return true;
        }
        if (block == Blocks.DEEPSLATE && this.throwBackBlocks.contains(Blocks.COBBLED_DEEPSLATE)) {
            return true;
        }

        ItemStack filter = this.items.get(10);
        if (!filter.isEmpty() && filter.getItem() instanceof net.minecraft.world.item.BlockItem bi) {
            Block fb = bi.getBlock();
            if (fb == block) return true;
            if (block == Blocks.COBBLESTONE && fb == Blocks.STONE) return true;
            if (block == Blocks.STONE && fb == Blocks.COBBLESTONE) return true;
            if (block == Blocks.COBBLED_DEEPSLATE && fb == Blocks.DEEPSLATE) return true;
            if (block == Blocks.DEEPSLATE && fb == Blocks.COBBLED_DEEPSLATE) return true;
        }
        return false;
    }

    public boolean isThrowBack(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem bi) {
            return isThrowBack(bi.getBlock());
        }
        // If Gravel is a throwback, flint drops are also ejected
        if (stack.is(Items.FLINT) && isThrowBack(Blocks.GRAVEL)) {
            return true;
        }
        return false;
    }

    public void toggleThrowBack(Block block) {
        if (this.throwBackBlocks.contains(block)) {
            this.throwBackBlocks.remove(block);
            if (block == Blocks.STONE) this.throwBackBlocks.remove(Blocks.COBBLESTONE);
            if (block == Blocks.COBBLESTONE) this.throwBackBlocks.remove(Blocks.STONE);
            if (block == Blocks.DEEPSLATE) this.throwBackBlocks.remove(Blocks.COBBLED_DEEPSLATE);
            if (block == Blocks.COBBLED_DEEPSLATE) this.throwBackBlocks.remove(Blocks.DEEPSLATE);
        } else {
            this.throwBackBlocks.add(block);
            if (block == Blocks.STONE) this.throwBackBlocks.add(Blocks.COBBLESTONE);
            if (block == Blocks.COBBLESTONE) this.throwBackBlocks.add(Blocks.STONE);
            if (block == Blocks.DEEPSLATE) this.throwBackBlocks.add(Blocks.COBBLED_DEEPSLATE);
            if (block == Blocks.COBBLED_DEEPSLATE) this.throwBackBlocks.add(Blocks.DEEPSLATE);
        }
        setChanged();
        syncToClient();
    }

    public void toggleCommonSpoil() {
        boolean allActive = true;
        List<Block> spoilCandidates = new ArrayList<>();
        for (var entry : this.clientBlockList) {
            Block b = entry.block();
            BlockState st = b.defaultBlockState();
            if (st.is(BlockTags.DIRT) || st.is(BlockTags.SAND) || st.is(Blocks.GRAVEL)
                    || st.is(Blocks.STONE) || st.is(Blocks.COBBLESTONE)
                    || st.is(Blocks.DEEPSLATE) || st.is(Blocks.COBBLED_DEEPSLATE)
                    || st.is(Blocks.TUFF) || st.is(Blocks.ANDESITE)
                    || st.is(Blocks.DIORITE) || st.is(Blocks.GRANITE)) {
                spoilCandidates.add(b);
                if (!this.throwBackBlocks.contains(b)) {
                    allActive = false;
                }
            }
        }
        if (allActive) {
            for (Block b : spoilCandidates) {
                this.throwBackBlocks.remove(b);
            }
            this.throwBackBlocks.remove(Blocks.COBBLESTONE);
            this.throwBackBlocks.remove(Blocks.COBBLED_DEEPSLATE);
        } else {
            for (Block b : spoilCandidates) {
                this.throwBackBlocks.add(b);
            }
            this.throwBackBlocks.add(Blocks.COBBLESTONE);
            this.throwBackBlocks.add(Blocks.COBBLED_DEEPSLATE);
        }
        setChanged();
        syncToClient();
    }

    public Set<Block> getThrowBackBlocks() {
        return this.throwBackBlocks;
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
            if (state.getBlock() instanceof QuarryControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(QuarryControllerBlock.ASSEMBLED, false)
                        .setValue(QuarryControllerBlock.ACTIVE, false)
                        .setValue(QuarryControllerBlock.BLUEPRINT, false), 3);
            }
        }
        setChanged();
        syncToClient();
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof QuarryControllerBlock)) return;

        updateMemberBlockStates(valid);

        boolean blockAssembled = currentState.getValue(QuarryControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(QuarryControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(QuarryControllerBlock.BLUEPRINT, false);
                this.blueprintTicks = 0;
                if (this.level instanceof ServerLevel serverLevel) {
                    Direction facing = currentState.getValue(QuarryControllerBlock.FACING);
                    displaceEntitiesInsideMultiblock(serverLevel, facing);
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5,
                            8, 0.5, 0.5, 0.5, 0.05);
                }
            }
            this.level.setBlock(this.worldPosition, newState, 3);
            setChanged();
            syncToClient();
        }
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            updateMemberBlockStates(true);
            this.blueprintTicks = 0;
            if (this.level instanceof ServerLevel serverLevel) {
                BlockState current = getBlockState();
                Direction facing = current.hasProperty(QuarryControllerBlock.FACING)
                        ? current.getValue(QuarryControllerBlock.FACING)
                        : Direction.NORTH;
                displaceEntitiesInsideMultiblock(serverLevel, facing);
                if (current.getBlock() instanceof QuarryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(QuarryControllerBlock.ASSEMBLED, true)
                            .setValue(QuarryControllerBlock.BLUEPRINT, false), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Heavy Quarry]§r 3x3x3 Excavation Rig is §2ASSEMBLED & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof QuarryControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(QuarryControllerBlock.ASSEMBLED, false)
                            .setValue(QuarryControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§e[Heavy Quarry]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    private void displaceEntitiesInsideMultiblock(ServerLevel level, Direction facing) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    BlockPos p = getRelativePos(x, y, z, facing);
                    minX = Math.min(minX, p.getX());
                    minY = Math.min(minY, p.getY());
                    minZ = Math.min(minZ, p.getZ());
                    maxX = Math.max(maxX, p.getX() + 1.0);
                    maxY = Math.max(maxY, p.getY() + 1.0);
                    maxZ = Math.max(maxZ, p.getZ() + 1.0);
                }
            }
        }

        AABB rigBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        List<Player> trappedPlayers = level.getEntitiesOfClass(Player.class, rigBox);
        for (Player p : trappedPlayers) {
            BlockPos safeFront = this.worldPosition.relative(facing, 2);
            p.teleportTo(safeFront.getX() + 0.5, this.worldPosition.getY(), safeFront.getZ() + 0.5);
        }
    }

    // =========================================================================
    // TICK & QUARRY OPERATION LOGIC
    // =========================================================================
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // Periodic structure check every 20 ticks (1s)
        if (--this.structureCheckCooldown <= 0) {
            this.structureCheckCooldown = 20;
            syncAssembly();
        }

        // Blueprint tick countdown
        if (this.blueprintTicks > 0) {
            this.blueprintTicks--;
            if (this.blueprintTicks == 0) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof QuarryControllerBlock && current.getValue(QuarryControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(QuarryControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            if (state.getValue(QuarryControllerBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(QuarryControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        // Attempt to auto-export buffer items to the 3 conveyor exits
        tryExportBuffer(serverLevel, state.getValue(QuarryControllerBlock.FACING));

        if (this.needsScan) {
            performInitialScan(serverLevel, state.getValue(QuarryControllerBlock.FACING));
            this.needsScan = false;
            syncToClient();
        }

        if (this.phase == PHASE_DONE) {
            if (state.getValue(QuarryControllerBlock.ACTIVE)) {
                level.setBlock(pos, state.setValue(QuarryControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        // Energy consumption & proportional speed scaling:
        // Configurable: maxEnergyPerTick (default 240 FE/tick) and maxMiningSpeed (default 1.5 blocks/sec)
        // If energy < maxEnergyPerTick, runs progressively slower (progress scales with feToConsume / maxEnergyPerTick)
        // If 0 FE, progress is 0 (stops completely)
        boolean hasPower = this.energy > 0;
        boolean isOperating = false;

        if (hasPower) {
            BlockState targetState = getCurrentTargetBlockState(serverLevel, state.getValue(QuarryControllerBlock.FACING));
            boolean isThrowBackBlock = isThrowBack(targetState.getBlock());
            if (!isThrowBackBlock && !hasBufferSpace()) {
                // Buffer is full of kept items waiting for side conveyor/pipe export; pause mining engine
                isOperating = false;
            } else {
                int maxFe = getMaxEnergyPerTick();
                int feToConsume = Math.min(this.energy, maxFe);
                float speedMultiplier = getSpeedMultiplierForBlock(targetState);
                double baseBlocksPerTick = getMaxMiningSpeed() / 20.0;
                float progressDelta = (float) (baseBlocksPerTick * (feToConsume / (double) maxFe) * speedMultiplier);
                this.energyStorage.extract(feToConsume, null);
                this.miningProgress += progressDelta;
                isOperating = true;

                int steps = 0;
                while (this.miningProgress >= PROGRESS_REQUIRED && steps++ < 10) {
                    this.miningProgress -= PROGRESS_REQUIRED;
                    executeStep(serverLevel, state.getValue(QuarryControllerBlock.FACING));
                    if (this.phase == PHASE_DONE) break;
                }
            }
        }

        boolean currentActive = state.getValue(QuarryControllerBlock.ACTIVE);
        if (currentActive != isOperating) {
            level.setBlock(pos, state.setValue(QuarryControllerBlock.ACTIVE, isOperating), 3);
        }

        // Periodic client sync for GUI data
        if (serverLevel.getGameTime() % 20 == 0) {
            syncToClient();
        }
    }

    private void executeStep(ServerLevel level, Direction facing) {
        switch (this.phase) {
            case PHASE_CLEARING -> executeClearingStep(level, facing);
            case PHASE_FENCING  -> executeFencingStep(level, facing);
            case PHASE_MINING   -> executeMiningStep(level, facing);
        }
        setChanged();
    }

    public float getSpeedMultiplierForBlock(BlockState state) {
        if (state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(Blocks.GRAVEL)
                || state.is(Blocks.SUSPICIOUS_GRAVEL) || state.is(Blocks.TUFF)
                || state.getBlock().getDescriptionId().contains("tuff")) {
            return 2.0f; // 2x speed for soft materials (dirt, sand, gravel, tuff)
        }
        if (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.RAW_IRON_BLOCK) || state.is(Blocks.IRON_BLOCK)
                || state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                || state.is(Blocks.DIAMOND_BLOCK)
                || state.getBlock().getDescriptionId().contains("iron_ore")
                || state.getBlock().getDescriptionId().contains("diamond_ore")) {
            return 0.5f; // 1/2 speed for precious hard ores (iron, diamonds)
        }
        return 1.0f; // 1x standard base speed (1 block/sec at 20 FE/t)
    }

    public BlockPos findTopObstruction(ServerLevel level, Direction facing) {
        if (this.phase != PHASE_CLEARING && this.phase != PHASE_MINING) {
            return null;
        }

        int innerRadius = this.radius - 1;
        int innerMaxForward = this.radius * 2;
        // In PHASE_MINING, the top box (Y >= 0) has already been cleared and fenced off.
        // Stop monitoring the top box so placed perimeter fences are never disturbed or remined.
        // Only monitor the active inner pit excavation volume (Y <= -1).
        int startY = (this.phase == PHASE_MINING) ? -1 : 2;
        int currentMineY = this.currentY;

        // Top-down scan from startY down to currentMineY for any fallen or newly placed blocks
        for (int dy = startY; dy >= currentMineY; dy--) {
            int r = (dy >= 0) ? this.radius : innerRadius;
            int minZ = (dy >= 0) ? 1 : 2;
            int maxZ = (dy >= 0) ? (this.radius * 2 + 1) : innerMaxForward;

            for (int z = minZ; z <= maxZ; z++) {
                for (int x = -r; x <= r; x++) {
                    if (dy == currentMineY) {
                        // On the active layer, only inspect positions that were already cleared
                        if (z > this.currentZ || (z == this.currentZ && x >= this.currentX)) {
                            continue;
                        }
                    }
                    BlockPos pos = getPitPos(x, z, dy, facing);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof LiquidBlock) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (!state.isAir()
                            && !(state.getBlock() instanceof FenceBlock)
                            && !state.is(BlockTags.FENCES)
                            && !(state.getBlock() instanceof WallBlock)
                            && !state.is(BlockTags.WALLS)
                            && state.getDestroySpeed(level, pos) >= 0.0f
                            && !state.is(Blocks.BEDROCK)) {
                        return pos; // Found the highest obstruction!
                    }
                }
            }
        }
        return null;
    }

    public BlockState getCurrentTargetBlockState(ServerLevel level, Direction facing) {
        BlockPos obstruction = findTopObstruction(level, facing);
        if (obstruction != null) {
            return level.getBlockState(obstruction);
        }
        BlockPos targetPos = switch (this.phase) {
            case PHASE_CLEARING -> getPitPos(this.currentX, this.currentZ, this.currentY, facing);
            case PHASE_MINING   -> getPitPos(this.currentX, this.currentZ, this.currentY, facing);
            default             -> null;
        };
        if (targetPos != null) {
            return level.getBlockState(targetPos);
        }
        return Blocks.STONE.defaultBlockState();
    }

    // Phase 0: Clear outer 43x43x3 box behind the machine (from Y = 2 down to Y = 0)
    private void executeClearingStep(ServerLevel level, Direction facing) {
        // Clear any obstruction from the top down first
        BlockPos obstruction = findTopObstruction(level, facing);
        if (obstruction != null) {
            BlockState obstructionState = level.getBlockState(obstruction);
            mineBlockAt(level, obstruction, obstructionState, facing);
            return;
        }

        int maxForward = this.radius * 2 + 1;
        int attempts = 0;

        while (attempts++ < 30) {
            BlockPos targetPos = getPitPos(this.currentX, this.currentZ, this.currentY, facing);
            BlockState targetState = level.getBlockState(targetPos);

            boolean isClearable = !targetState.isAir() && !targetState.is(Blocks.BEDROCK) && !targetState.is(Blocks.BARRIER);
            if (isClearable) {
                // Kept items must wait in buffer; if buffer is completely full, pause mining until buffer drains
                if (!isThrowBack(targetState.getBlock()) && !hasBufferSpace()) {
                    return;
                }
                mineBlockAt(level, targetPos, targetState, facing);
                advanceClearingCoordinates(maxForward);
                return;
            }

            if (advanceClearingCoordinates(maxForward)) {
                // Outer box clearing complete! Transition to fencing perimeter
                this.phase = PHASE_FENCING;
                this.fenceIndex = 0;
                return;
            }
        }
    }

    private boolean advanceClearingCoordinates(int maxForward) {
        this.currentX++;
        if (this.currentX > this.radius) {
            this.currentX = -this.radius;
            this.currentZ++;
            if (this.currentZ > maxForward) {
                this.currentZ = 1;
                this.currentY--; // Next vertical depth layer (2 -> 1 -> 0 -> -1)
                if (this.currentY < 0) {
                    return true; // Finished all 3 depth layers (2, 1, 0)
                }
            }
        }
        return false;
    }

    // Phase 1: Line the outside of the 41x41 box with safety fence
    private void executeFencingStep(ServerLevel level, Direction facing) {
        List<BlockPos> fencePerimeter = getFencePerimeter(facing);
        if (this.fenceIndex >= fencePerimeter.size()) {
            // Finished fencing! Transition to deep pit mining starting at Y = -1
            this.phase = PHASE_MINING;
            int innerRadius = this.radius - 1;
            this.currentX = -innerRadius;
            this.currentZ = 2;
            this.currentY = -1; // Start mining downward below the machine level
            return;
        }

        BlockPos fencePos = fencePerimeter.get(this.fenceIndex);
        BlockState currentState = level.getBlockState(fencePos);

        if (currentState.isAir() || currentState.canBeReplaced()) {
            Block fenceBlock = Blocks.OAK_FENCE;
            ItemStack customFence = this.items.get(9);
            if (!customFence.isEmpty() && customFence.getItem() instanceof net.minecraft.world.item.BlockItem bi) {
                fenceBlock = bi.getBlock();
                customFence.shrink(1);
            }

            level.setBlock(fencePos, fenceBlock.defaultBlockState(), 3);
            level.playSound(null, fencePos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.7f, 1.0f);
        }

        this.fenceIndex++;
        if (this.fenceIndex >= fencePerimeter.size()) {
            this.phase = PHASE_MINING;
            int innerRadius = this.radius - 1;
            this.currentX = -innerRadius;
            this.currentZ = 2;
            this.currentY = -1;
        }
    }

    private List<BlockPos> getFencePerimeter(Direction facing) {
        List<BlockPos> list = new ArrayList<>();
        int R = this.radius;
        int maxZ = R * 2 + 1;
        int fenceY = 0; // Ground level of the machine and cleared bench

        // North/South rims (Z = 1 and Z = maxZ)
        for (int x = -R; x <= R; x++) {
            list.add(getPitPos(x, 1, fenceY, facing));
            list.add(getPitPos(x, maxZ, fenceY, facing));
        }
        // West/East rims (X = -R and X = R)
        for (int z = 2; z < maxZ; z++) {
            list.add(getPitPos(-R, z, fenceY, facing));
            list.add(getPitPos(R, z, fenceY, facing));
        }
        return list;
    }

    // Phase 2: Mine the 41x41 pit downward all the way to bedrock
    private void executeMiningStep(ServerLevel level, Direction facing) {
        // Clear any obstruction from the top down first
        BlockPos obstruction = findTopObstruction(level, facing);
        if (obstruction != null) {
            BlockState obstructionState = level.getBlockState(obstruction);
            mineBlockAt(level, obstruction, obstructionState, facing);
            return;
        }

        int innerRadius = this.radius - 1;
        int maxForward = this.radius * 2;
        int minY = level.getMinY();
        int attempts = 0;

        while (attempts++ < 30) {
            BlockPos targetPos = getPitPos(this.currentX, this.currentZ, this.currentY, facing);
            int absoluteY = targetPos.getY();

            if (absoluteY <= minY) {
                // Reached bedrock map floor! Quarry completed!
                this.phase = PHASE_DONE;
                return;
            }

            BlockState targetState = level.getBlockState(targetPos);
            boolean isBreakable = !targetState.isAir() && targetState.getDestroySpeed(level, targetPos) >= 0.0f && !targetState.is(Blocks.BEDROCK);

            if (isBreakable) {
                // Kept items must wait in buffer; if buffer is completely full, pause mining until buffer drains
                if (!isThrowBack(targetState.getBlock()) && !hasBufferSpace()) {
                    return;
                }
                mineBlockAt(level, targetPos, targetState, facing);
                advanceMiningCoordinates(innerRadius, maxForward, minY);
                return;
            }

            // Remove standing fluids in the pit so excavation stays dry
            if (targetState.getBlock() instanceof LiquidBlock) {
                level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
            }

            if (advanceMiningCoordinates(innerRadius, maxForward, minY)) {
                this.phase = PHASE_DONE;
                return;
            }
        }
    }

    private boolean advanceMiningCoordinates(int innerRadius, int maxForward, int minY) {
        this.currentX++;
        if (this.currentX > innerRadius) {
            this.currentX = -innerRadius;
            this.currentZ++;
            if (this.currentZ > maxForward) {
                this.currentZ = 2;
                this.currentY--; // Dig next layer down
                int absoluteY = this.worldPosition.getY() + this.currentY;
                if (absoluteY <= minY) {
                    return true;
                }
            }
        }
        return false;
    }

    private void mineBlockAt(ServerLevel level, BlockPos targetPos, BlockState targetState, Direction facing) {
        // Break particles and sound
        level.levelEvent(2001, targetPos, Block.getId(targetState));

        // Generate drops using simulated Diamond Pickaxe tool context
        LootParams.Builder lootBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetPos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
        BlockEntity targetBE = level.getBlockEntity(targetPos);
        if (targetBE != null) {
            lootBuilder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, targetBE);
        }

        List<ItemStack> drops = targetState.getDrops(lootBuilder);

        // Clear the mined block
        level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);

        // Update remaining counts
        Block block = targetState.getBlock();
        if (this.blockCounts.containsKey(block)) {
            int current = this.blockCounts.get(block);
            if (current <= 1) {
                this.blockCounts.remove(block);
            } else {
                this.blockCounts.put(block, current - 1);
            }
        }
        if (this.totalRemainingBlocks > 0) {
            this.totalRemainingBlocks--;
        }
        updateClientBlockList();
        syncToClient();

        // Route drops: throw-back waste blocks automatically roll off rear conveyor into pit; kept products wait in buffer for side conveyor/pipe
        boolean minedThrowBack = isThrowBack(targetState.getBlock());
        for (ItemStack drop : drops) {
            if (minedThrowBack || isThrowBack(drop)) {
                ejectViaRearConveyor(level, drop, facing);
            } else {
                addItemToBuffer(drop);
            }
        }
    }

    // =========================================================================
    // CONVEYOR EXPORT & THROW-BACK DISTRIBUTION
    // =========================================================================
    public boolean addItemToBuffer(ItemStack stack) {
        if (stack.isEmpty()) return true;
        for (int slot = 0; slot < 9 && !stack.isEmpty(); slot++) {
            ItemStack buf = this.items.get(slot);
            if (buf.isEmpty()) {
                this.items.set(slot, stack.copy());
                stack.setCount(0);
                setChanged();
                return true;
            } else if (ItemStack.isSameItemSameComponents(buf, stack) && buf.getCount() < buf.getMaxStackSize()) {
                int space = buf.getMaxStackSize() - buf.getCount();
                int transfer = Math.min(space, stack.getCount());
                buf.grow(transfer);
                stack.shrink(transfer);
                setChanged();
            }
        }
        return stack.isEmpty();
    }

    public boolean hasBufferSpace() {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack buf = this.items.get(slot);
            if (buf.isEmpty() || buf.getCount() < buf.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void ejectViaRearConveyor(ServerLevel level, ItemStack stack, Direction facing) {
        if (stack.isEmpty()) return;

        BlockPos rearChutePos = getRelativePos(0, 0, 3, facing);
        Direction back = facing.getOpposite();

        // Spoil automatically rolls off the built-in rear conveyor chute into the excavation pit
        double spawnX = rearChutePos.getX() + 0.5;
        double spawnY = this.worldPosition.getY() + 0.35;
        double spawnZ = rearChutePos.getZ() + 0.5;

        ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, stack.copy());
        itemEntity.setDeltaMovement(back.getStepX() * 0.22, -0.05, back.getStepZ() * 0.22);
        itemEntity.setPickUpDelay(20);
        level.addFreshEntity(itemEntity);

        level.playSound(null, rearChutePos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.4f, 0.9f);
        stack.setCount(0);
    }

    private void tryExportBuffer(ServerLevel level, Direction facing) {
        List<BlockPos> exportPorts = getSideConveyorPorts(facing);

        for (int slot = 0; slot < 9; slot++) {
            ItemStack buf = this.items.get(slot);
            if (buf.isEmpty()) continue;

            int startIdx = (this.exportRoundRobin++) % exportPorts.size();
            for (int i = 0; i < exportPorts.size(); i++) {
                BlockPos portPos = exportPorts.get((startIdx + i) % exportPorts.size());
                ItemStack single = buf.copyWithCount(1);
                if (tryPushToSideConveyorOrPipe(level, portPos, single)) {
                    buf.shrink(1);
                    if (buf.isEmpty()) {
                        this.items.set(slot, ItemStack.EMPTY);
                    }
                    setChanged();
                    return;
                }
            }
        }
    }

    public List<BlockPos> getSideConveyorPorts(Direction facing) {
        List<BlockPos> ports = new ArrayList<>(6);
        // Left side positions
        ports.add(getRelativePos(-2, 0, 1, facing)); // Center left chute
        ports.add(getRelativePos(-2, 0, 0, facing)); // Front left
        ports.add(getRelativePos(-2, 0, 2, facing)); // Rear left

        // Right side positions
        ports.add(getRelativePos(2, 0, 1, facing));  // Center right chute
        ports.add(getRelativePos(2, 0, 0, facing));  // Front right
        ports.add(getRelativePos(2, 0, 2, facing));  // Rear right
        return ports;
    }

    private boolean tryPushToSideConveyorOrPipe(ServerLevel level, BlockPos targetPos, ItemStack stack) {
        BlockPos actualPos = targetPos;
        BlockState targetState = level.getBlockState(actualPos);
        BlockEntity targetBE = level.getBlockEntity(actualPos);

        // 1. Check for normal conveyor belt at portPos or portPos.below()
        boolean isConveyor = ConveyorBeltBlock.isAnyConveyorBlock(targetState)
                || targetBE instanceof ConveyorBeltBlockEntity
                || targetBE instanceof RightAngleConveyorBeltBlockEntity
                || targetBE instanceof SlopedConveyorBeltBlockEntity;

        if (!isConveyor) {
            actualPos = targetPos.below();
            targetState = level.getBlockState(actualPos);
            targetBE = level.getBlockEntity(actualPos);
            isConveyor = ConveyorBeltBlock.isAnyConveyorBlock(targetState)
                    || targetBE instanceof ConveyorBeltBlockEntity
                    || targetBE instanceof RightAngleConveyorBeltBlockEntity
                    || targetBE instanceof SlopedConveyorBeltBlockEntity;
        }

        if (isConveyor) {
            double cx = actualPos.getX() + 0.5;
            double cz = actualPos.getZ() + 0.5;
            AABB beltBox = new AABB(
                    cx - 0.25, actualPos.getY() + 0.05, cz - 0.25,
                    cx + 0.25, actualPos.getY() + 0.65, cz + 0.25
            );
            List<ItemEntity> itemsOnBelt = level.getEntitiesOfClass(ItemEntity.class, beltBox);
            if (!itemsOnBelt.isEmpty()) {
                return false;
            }

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

            Direction forwardDir = conveyorForward != null ? conveyorForward : Direction.NORTH;
            double spawnX = actualPos.getX() + 0.5;
            double spawnY = actualPos.getY() + 0.35;
            double spawnZ = actualPos.getZ() + 0.5;
            ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, stack.copy());
            itemEntity.setDeltaMovement(forwardDir.getStepX() * 0.05, 0.0, forwardDir.getStepZ() * 0.05);
            itemEntity.setPickUpDelay(10);
            level.addFreshEntity(itemEntity);
            stack.setCount(0);
            return true;
        }

        // 2. Check for Industrial Pipe
        if (level.getBlockEntity(targetPos) instanceof IndustrialPipeBlockEntity pipeBE) {
            if (pipeBE.canPlaceItem(0, stack)) {
                pipeBE.setItem(0, stack.copy());
                stack.setCount(0);
                return true;
            }
        }

        // 3. Check for Buffer Pipe
        if (level.getBlockEntity(targetPos) instanceof BufferPipeBlockEntity bufferPipeBE) {
            ItemStack existing = bufferPipeBE.getItem(0);
            if (existing.isEmpty()) {
                bufferPipeBE.setItem(0, stack.copy());
                stack.setCount(0);
                return true;
            } else if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                int space = existing.getMaxStackSize() - existing.getCount();
                int transfer = Math.min(space, stack.getCount());
                existing.grow(transfer);
                bufferPipeBE.setItem(0, existing);
                stack.shrink(transfer);
                return stack.isEmpty();
            }
        }

        // 4. Try standard capability container (Chest, Barrel, Hopper, Storage Drawer)
        ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, null);
        if (handler != null) {
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = handler.insert(ItemResource.of(stack), stack.getCount(), tx);
                if (inserted > 0) {
                    tx.commit();
                    stack.shrink(inserted);
                    return stack.isEmpty();
                }
            }
        }

        return false;
    }

    // =========================================================================
    // INITIAL SCAN OF MINING VOLUME
    // =========================================================================
    private void performInitialScan(ServerLevel level, Direction facing) {
        this.blockCounts.clear();
        this.totalRemainingBlocks = 0;

        int R = this.radius;
        int maxForward = R * 2 + 1;
        int innerRadius = R - 1;
        int innerMaxForward = R * 2;
        int minY = level.getMinY();

        // 1. Ensure all chunks covering the quarry volume are loaded
        int minChunkX = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int minChunkZ = Integer.MAX_VALUE;
        int maxChunkZ = Integer.MIN_VALUE;

        for (int x : new int[]{-R, R}) {
            for (int z : new int[]{1, maxForward}) {
                BlockPos corner = getPitPos(x, z, 0, facing);
                int cx = corner.getX() >> 4;
                int cz = corner.getZ() >> 4;
                minChunkX = Math.min(minChunkX, cx);
                maxChunkX = Math.max(maxChunkX, cx);
                minChunkZ = Math.min(minChunkZ, cz);
                maxChunkZ = Math.max(maxChunkZ, cz);
            }
        }

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                level.getChunk(cx, cz, net.minecraft.world.level.chunk.status.ChunkStatus.FULL, true);
            }
        }

        // 2. Scan outer box (43x43x3, from Y = 2 down to Y = 0)
        for (int dy = 2; dy >= 0; dy--) {
            for (int z = 1; z <= maxForward; z++) {
                for (int x = -R; x <= R; x++) {
                    BlockPos p = getPitPos(x, z, dy, facing);
                    BlockState st = level.getBlockState(p);
                    if (!st.isAir() && !st.is(Blocks.BEDROCK) && !st.is(Blocks.BARRIER)) {
                        this.blockCounts.merge(st.getBlock(), 1, Integer::sum);
                        this.totalRemainingBlocks++;
                    }
                }
            }
        }

        // 3. Scan inner pit (41x41 from Y = -1 down to bedrock)
        int startY = -1;
        int currentLevelY = this.worldPosition.getY();
        for (int absY = currentLevelY + startY; absY > minY; absY--) {
            int dy = absY - currentLevelY;
            for (int z = 2; z <= innerMaxForward; z++) {
                for (int x = -innerRadius; x <= innerRadius; x++) {
                    BlockPos p = getPitPos(x, z, dy, facing);
                    BlockState st = level.getBlockState(p);
                    if (!st.isAir() && !st.is(Blocks.BEDROCK) && !st.is(Blocks.BARRIER)) {
                        this.blockCounts.merge(st.getBlock(), 1, Integer::sum);
                        this.totalRemainingBlocks++;
                    }
                }
            }
        }

        updateClientBlockList();
    }

    private void updateClientBlockList() {
        this.clientBlockList.clear();
        this.blockCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(e -> this.clientBlockList.add(new BlockCountEntry(e.getKey(), e.getValue())));
    }

    // =========================================================================
    // MULTIBLOCK 3x3x3 REQUIREMENTS & VALIDATION
    // =========================================================================
    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public boolean isComponentValid(BlueprintRequirement req, BlockState state) {
        if (req.x == 0 && req.y == 0 && req.z == 0) {
            return state.is(RefineryMod.QUARRY_CONTROLLER.get());
        }
        return state.is(RefineryMod.QUARRY_CASING.get());
    }

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        Block controller = RefineryMod.QUARRY_CONTROLLER.get();
        Block casing = RefineryMod.QUARRY_CASING.get();

        // Layer 0: Base Foundation Skid (Controller at front-center, 8 Casings)
        list.add(new BlueprintRequirement(-1, 0, 0, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Heavy Pit Quarry Controller",  0xFF38BDF8));
        list.add(new BlueprintRequirement( 1, 0, 0, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 0, 1, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 0, 1, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 0, 1, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 0, 2, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 0, 2, casing,     "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 0, 2, casing,     "Quarry Structural Casing", 0xFFD9821E));

        // Layer 1: Machinery Deck (9 Casings)
        list.add(new BlueprintRequirement(-1, 1, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 1, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 1, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 1, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 1, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 1, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 1, 2, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 1, 2, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 1, 2, casing, "Quarry Structural Casing", 0xFFD9821E));

        // Layer 2: A-Frame Gantry, Crown Sheaves & Floodlights (9 Casings)
        list.add(new BlueprintRequirement(-1, 2, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 2, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 2, 0, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 2, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 2, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 2, 1, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement(-1, 2, 2, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 0, 2, 2, casing, "Quarry Structural Casing", 0xFFD9821E));
        list.add(new BlueprintRequirement( 1, 2, 2, casing, "Quarry Structural Casing", 0xFFD9821E));

        return list;
    }

    public boolean checkStructure(boolean notifyPlayer, @Nullable Player player) {
        if (this.level == null) return false;
        Direction facing = getBlockState().hasProperty(QuarryControllerBlock.FACING)
                ? getBlockState().getValue(QuarryControllerBlock.FACING)
                : Direction.NORTH;

        List<BlueprintRequirement> reqs = getRequirements();
        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState state = this.level.getBlockState(targetPos);
            if (isComponentValid(req, state)) {
                matched++;
            } else if (missing.size() < 4) {
                missing.add("Layer " + req.y + ": " + req.name + " at [" + targetPos.toShortString() + "]");
            }
        }

        if (matched == reqs.size()) {
            return true;
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§c[Quarry Structure Incomplete]§r " + matched + " / " + reqs.size() + " blocks placed."));
            for (String err : missing) {
                player.sendSystemMessage(Component.literal(" §eMissing§r " + err));
            }
        }
        return false;
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof QuarryControllerBlock && getBlockState().hasProperty(QuarryControllerBlock.FACING))
                ? getBlockState().getValue(QuarryControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x == 0 && req.y == 0 && req.z == 0) continue;
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(QuarryControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(QuarryControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(QuarryControllerBlock.ASSEMBLED, isAssembled), 3);
                }
            }
        }
    }

    public BlockPos getRelativePos(int rightOffset, int upOffset, int forwardOffset, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, rightOffset)
                .relative(Direction.UP, upOffset)
                .relative(back, forwardOffset);
    }

    public BlockPos getPitPos(int lateralOffset, int forwardOffset, int depthOffset, Direction facing) {
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, lateralOffset)
                .relative(Direction.UP, depthOffset)
                .relative(back, forwardOffset + 2);
    }

    // =========================================================================
    // MENU & CONTAINER IMPLEMENTATION
    // =========================================================================
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.quarry");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuarryMenu(containerId, playerInventory, this, this.dataAccess);
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
        // Output from buffer slots 0..8
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 9 || slot == 10; // Fence supply slot and Throw-Back filter slot accept input
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 0 && slot < 9;
    }

    // =========================================================================
    // NBT PERSISTENCE & CLIENT SYNC
    // =========================================================================
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("energy", this.energy);
        output.putInt("radius", this.radius);
        output.putInt("phase", this.phase);
        output.putFloat("miningProgress", this.miningProgress);
        output.putInt("currentX", this.currentX);
        output.putInt("currentZ", this.currentZ);
        output.putInt("currentY", this.currentY);
        output.putInt("fenceIndex", this.fenceIndex);
        output.putInt("totalRemaining", this.totalRemainingBlocks);

        ContainerHelper.saveAllItems(output, this.items);

        // Compact encoded string for remaining block types: "id1:count1,id2:count2,..."
        updateClientBlockList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.clientBlockList.size(); i++) {
            if (i > 0) sb.append(',');
            var entry = this.clientBlockList.get(i);
            sb.append(BuiltInRegistries.BLOCK.getKey(entry.block()).toString())
              .append(':')
              .append(entry.count());
        }
        output.putString("remainingList", sb.toString());

        // Encoded string for throw-back block IDs: "id1,id2,..."
        StringBuilder tbSb = new StringBuilder();
        int tbIdx = 0;
        for (Block b : this.throwBackBlocks) {
            if (tbIdx++ > 0) tbSb.append(',');
            tbSb.append(BuiltInRegistries.BLOCK.getKey(b).toString());
        }
        output.putString("throwBackList", tbSb.toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.radius = input.getIntOr("radius", DEFAULT_RADIUS);
        this.phase = input.getIntOr("phase", PHASE_CLEARING);
        this.miningProgress = input.getFloatOr("miningProgress", 0.0f);
        if (this.miningProgress > 1.0f) {
            this.miningProgress = 0.0f; // Reset legacy progress from 20-scale
        }
        this.currentX = input.getIntOr("currentX", -this.radius);
        this.currentZ = input.getIntOr("currentZ", 1);
        this.currentY = input.getIntOr("currentY", 0);
        this.fenceIndex = input.getIntOr("fenceIndex", 0);
        this.totalRemainingBlocks = input.getIntOr("totalRemaining", 0);

        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);

        this.clientBlockList.clear();
        String enc = input.getStringOr("remainingList", "");
        if (!enc.isEmpty()) {
            String[] tokens = enc.split(",");
            for (String tok : tokens) {
                int colon = tok.lastIndexOf(':');
                if (colon > 0 && colon < tok.length() - 1) {
                    String idStr = tok.substring(0, colon);
                    try {
                        int cnt = Integer.parseInt(tok.substring(colon + 1));
                        Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(idStr));
                        if (block != null && block != Blocks.AIR) {
                            this.clientBlockList.add(new BlockCountEntry(block, cnt));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        this.throwBackBlocks.clear();
        String tbEnc = input.getStringOr("throwBackList", "");
        if (!tbEnc.isEmpty()) {
            for (String idStr : tbEnc.split(",")) {
                try {
                    Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(idStr));
                    if (block != null && block != Blocks.AIR) {
                        this.throwBackBlocks.add(block);
                    }
                } catch (Exception ignored) {}
            }
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
}
