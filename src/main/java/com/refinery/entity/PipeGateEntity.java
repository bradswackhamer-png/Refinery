package com.refinery.entity;

import com.refinery.RefineryMod;
import com.refinery.block.PipeGateBlock;
import com.refinery.compat.LogisticsPipeCompat;
import com.refinery.gate.*;
import com.refinery.menu.GateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PipeGateEntity extends Entity implements MenuProvider, GateHost {
    public static final int MAX_SLOTS = 8;
    public static final int DATA_COUNT = 20;

    public record LevelPos(ResourceKey<Level> dimension, BlockPos pos) {}
    private static final Set<LevelPos> POWERED_GATE_POSITIONS = ConcurrentHashMap.newKeySet();

    public static boolean hasAnyPoweredGates() {
        return !POWERED_GATE_POSITIONS.isEmpty();
    }

    public static boolean isEmittingRedstone(BlockGetter level, BlockPos pos, Direction direction) {
        if (POWERED_GATE_POSITIONS.isEmpty() || pos == null) return false;
        if (level instanceof Level lvl) {
            return POWERED_GATE_POSITIONS.contains(new LevelPos(lvl.dimension(), pos.immutable()));
        }
        for (LevelPos lp : POWERED_GATE_POSITIONS) {
            if (lp.pos().equals(pos)) return true;
        }
        return false;
    }

    public static void notifyRedstoneNeighbors(Level level, BlockPos pos) {
        if (level == null || pos == null || level.isClientSide()) return;
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        level.updateNeighborsAt(pos, block);
        for (Direction dir : Direction.values()) {
            level.updateNeighborsAt(pos.relative(dir), block);
        }
    }

    private static final EntityDataAccessor<Integer> DATA_TIER = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_AUTARCHIC = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Direction> DATA_FACING = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<BlockPos> DATA_PIPE_POS = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_ACTIVE = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_POWERED = SynchedEntityData.defineId(PipeGateEntity.class, EntityDataSerializers.BOOLEAN);

    private final GateTrigger[] triggers = new GateTrigger[MAX_SLOTS];
    private final GateAction[] actions = new GateAction[MAX_SLOTS];
    private final boolean[] conditionsMet = new boolean[MAX_SLOTS];
    private final boolean[] wireStates = new boolean[4]; // RED, BLUE, GREEN, YELLOW

    private int pulsarTimer = 0;
    private int evalTimer = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) return getTier().ordinal();
            if (index == 1) return isAutarchic() ? 1 : 0;
            if (index >= 2 && index < 10) return triggers[index - 2].ordinal();
            if (index >= 10 && index < 18) return actions[index - 10].ordinal();
            if (index == 18) {
                int bits = 0;
                for (int i = 0; i < MAX_SLOTS; i++) {
                    if (conditionsMet[i]) bits |= (1 << i);
                }
                return bits;
            }
            if (index == 19) {
                int bits = 0;
                for (int i = 0; i < 4; i++) {
                    if (wireStates[i]) bits |= (1 << i);
                }
                return bits;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                if (value >= 0 && value < GateTier.values().length) setTier(GateTier.values()[value]);
            } else if (index == 1) {
                setAutarchic(value != 0);
            } else if (index >= 2 && index < 10) {
                int slot = index - 2;
                if (value >= 0 && value < GateTrigger.values().length) triggers[slot] = GateTrigger.values()[value];
            } else if (index >= 10 && index < 18) {
                int slot = index - 10;
                if (value >= 0 && value < GateAction.values().length) actions[slot] = GateAction.values()[value];
            } else if (index == 18) {
                for (int i = 0; i < MAX_SLOTS; i++) {
                    conditionsMet[i] = (value & (1 << i)) != 0;
                }
            } else if (index == 19) {
                for (int i = 0; i < 4; i++) {
                    wireStates[i] = (value & (1 << i)) != 0;
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public PipeGateEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        for (int i = 0; i < MAX_SLOTS; i++) {
            triggers[i] = GateTrigger.NONE;
            actions[i] = GateAction.NONE;
            conditionsMet[i] = false;
        }
    }

    public PipeGateEntity(EntityType<?> entityType, Level level, BlockPos pipePos, Direction facing, GateTier tier, boolean autarchic) {
        this(entityType, level);
        setTier(tier);
        setAutarchic(autarchic);
        snapToPipe(pipePos, facing);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TIER, 0);
        builder.define(DATA_AUTARCHIC, false);
        builder.define(DATA_FACING, Direction.NORTH);
        builder.define(DATA_PIPE_POS, BlockPos.ZERO);
        builder.define(DATA_ACTIVE, false);
        builder.define(DATA_POWERED, false);
    }

    public void snapToPipe(BlockPos pipePos, Direction face) {
        this.entityData.set(DATA_PIPE_POS, pipePos);
        this.entityData.set(DATA_FACING, face);
        double dist = 0.202; // Sits flush on round pipe cylinder (R = 0.200)
        double cx = pipePos.getX() + 0.5 + face.getStepX() * dist;
        double cy = pipePos.getY() + 0.5 + face.getStepY() * dist;
        double cz = pipePos.getZ() + 0.5 + face.getStepZ() * dist;
        this.setPos(cx, cy, cz);
        recalculateBoundingBox();
    }

    public void recalculateBoundingBox() {
        Direction face = getAttachedFace();
        double x = getX(), y = getY(), z = getZ();
        double thick = 0.0625; // 1 pixel half-thickness
        double size = 0.25;    // 4 pixels half-size (8x8 gate)
        AABB bb = switch (face) {
            case NORTH, SOUTH -> new AABB(x - size, y - size, z - thick, x + size, y + size, z + thick);
            case EAST, WEST -> new AABB(x - thick, y - size, z - size, x + thick, y + size, z + size);
            case UP, DOWN -> new AABB(x - size, y - thick, z - size, x + size, y + thick, z + size);
        };
        this.setBoundingBox(bb);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public BlockPos getPipePos() {
        return this.entityData.get(DATA_PIPE_POS);
    }

    public void setPipePos(BlockPos pos) {
        this.entityData.set(DATA_PIPE_POS, pos);
    }

    public Direction getAttachedFace() {
        return this.entityData.get(DATA_FACING);
    }

    public void setAttachedFace(Direction face) {
        this.entityData.set(DATA_FACING, face);
    }

    @Override
    public GateTier getTier() {
        int ord = this.entityData.get(DATA_TIER);
        if (ord >= 0 && ord < GateTier.values().length) return GateTier.values()[ord];
        return GateTier.BASIC;
    }

    public void setTier(GateTier tier) {
        this.entityData.set(DATA_TIER, tier.ordinal());
    }

    @Override
    public boolean isAutarchic() {
        return this.entityData.get(DATA_AUTARCHIC);
    }

    public void setAutarchic(boolean autarchic) {
        this.entityData.set(DATA_AUTARCHIC, autarchic);
    }

    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    public boolean isPowered() {
        return this.entityData.get(DATA_POWERED);
    }

    @Override
    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    @Override
    public void cycleTrigger(int slot, boolean forward) {
        if (slot < 0 || slot >= getTier().getSlots()) return;
        triggers[slot] = forward ? triggers[slot].next(getTier()) : triggers[slot].previous(getTier());
    }

    @Override
    public void cycleAction(int slot, boolean forward) {
        if (slot < 0 || slot >= getTier().getSlots()) return;
        actions[slot] = forward ? actions[slot].next(getTier(), isAutarchic()) : actions[slot].previous(getTier(), isAutarchic());
    }

    public boolean isWireActive(PipeWireColor color) {
        if (wireStates[color.ordinal()]) return true;
        if (level() != null) {
            List<PipeWireEntity> wires = level().getEntitiesOfClass(PipeWireEntity.class, new AABB(getPipePos()));
            for (PipeWireEntity w : wires) {
                if (w.isSignalActive(color)) return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack held = player.getItemInHand(hand);

        // 1. Wrench: Instantly dismantle & pick up gate
        if (held.is(RefineryMod.REFINERY_WRENCH.get())) {
            if (!level().isClientSide()) {
                dropGateItem(player);
                discard();
                level().playSound(null, blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }

        // 2. Chipset Upgrade: Upgrade normal gate to Autarchic Gate
        if (held.is(RefineryMod.AUTARCHIC_CHIPSET.get()) && !isAutarchic()) {
            if (!level().isClientSide()) {
                setAutarchic(true);
                if (!player.getAbilities().instabuild) held.shrink(1);
                level().playSound(null, blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8f, 1.5f);
                if (level() instanceof ServerLevel sl) {
                    sl.sendParticles(DustParticleOptions.REDSTONE, getX(), getY(), getZ(), 10, 0.2, 0.2, 0.2, 0.05);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 3. Pipe Wire Interaction: Connect / Toggle Pipe Wires
        PipeWireColor wireColor = null;
        if (held.getItem() instanceof com.refinery.item.PipeWireItem pwi) wireColor = pwi.getColor();
        else if (held.is(RefineryMod.PIPE_WIRE_RED.get())) wireColor = PipeWireColor.RED;
        else if (held.is(RefineryMod.PIPE_WIRE_BLUE.get())) wireColor = PipeWireColor.BLUE;
        else if (held.is(RefineryMod.PIPE_WIRE_GREEN.get())) wireColor = PipeWireColor.GREEN;
        else if (held.is(RefineryMod.PIPE_WIRE_YELLOW.get())) wireColor = PipeWireColor.YELLOW;

        if (wireColor != null) {
            if (getTier().supportsWire(wireColor)) {
                if (!level().isClientSide()) {
                    wireStates[wireColor.ordinal()] = !wireStates[wireColor.ordinal()];
                    level().playSound(null, blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6f, 1.2f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // 4. Blank Gate: Connection blocker feedback (no programming menu needed)
        if (getTier() == GateTier.BLANK) {
            if (!level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("§7[§6Pipe Blank§7] §fConnection blocked on this side"), true);
                level().playSound(null, blockPosition(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.7f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }

        // 5. Default: Open programming GUI
        if (!level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer sp) {
            sp.openMenu(this, buf -> buf.writeInt(getId()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        dropGateItem(damageSource.getEntity() instanceof Player p ? p : null);
        discard();
        return true;
    }

    public void dropGateItem(@Nullable Player player) {
        if (level().isClientSide()) return;
        ItemStack stack = getGateItemStack();
        if (player == null || !player.getAbilities().instabuild) {
            spawnAtLocation((ServerLevel) level(), stack, 0.1f);
        }
    }

    public ItemStack getGateItemStack() {
        GateTier tier = getTier();
        boolean aut = isAutarchic();
        return switch (tier) {
            case BLANK -> new ItemStack(RefineryMod.BLANK_GATE_ITEM.get());
            case BASIC -> new ItemStack(aut ? RefineryMod.BASIC_AUTARCHIC_GATE_ITEM.get() : RefineryMod.BASIC_GATE_ITEM.get());
            case IRON -> new ItemStack(aut ? RefineryMod.IRON_AUTARCHIC_GATE_ITEM.get() : RefineryMod.IRON_GATE_ITEM.get());
            case GOLD -> new ItemStack(aut ? RefineryMod.GOLD_AUTARCHIC_GATE_ITEM.get() : RefineryMod.GOLD_GATE_ITEM.get());
            case DIAMOND -> new ItemStack(aut ? RefineryMod.DIAMOND_AUTARCHIC_GATE_ITEM.get() : RefineryMod.DIAMOND_GATE_ITEM.get());
        };
    }

    public void notifyPipes() {
        if (level() == null) return;
        BlockPos pos = getPipePos();
        Direction face = getAttachedFace();
        if (pos != null) {
            BlockEntity be = level().getBlockEntity(pos);
            if (be instanceof com.refinery.block.entity.IndustrialPipeBlockEntity pipeBe) {
                pipeBe.updateConnections();
            }
            if (face != null) {
                BlockEntity neighborBe = level().getBlockEntity(pos.relative(face));
                if (neighborBe instanceof com.refinery.block.entity.IndustrialPipeBlockEntity neighborPipe) {
                    neighborPipe.updateConnections();
                }
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide()) {
            notifyPipes();
            if (this.entityData.get(DATA_POWERED)) {
                BlockPos pipePos = getPipePos();
                POWERED_GATE_POSITIONS.remove(new LevelPos(level().dimension(), pipePos.immutable()));
                notifyRedstoneNeighbors(level(), pipePos);
                PipeAndTarget pat = findPipeAndInventory(level(), pipePos, getAttachedFace());
                if (pat != null && !pat.pipePos().equals(pipePos)) {
                    POWERED_GATE_POSITIONS.remove(new LevelPos(level().dimension(), pat.pipePos().immutable()));
                    notifyRedstoneNeighbors(level(), pat.pipePos());
                }
            }
        }
        super.remove(reason);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) return;

        BlockPos pipePos = getPipePos();
        BlockState pipeState = level().getBlockState(pipePos);

        // If the pipe was broken or removed, gate drops its item and vanishes
        if (pipeState.isAir()) {
            dropGateItem(null);
            discard();
            return;
        }

        Direction facing = getAttachedFace();

        // Periodically evaluate conditions (every 4 ticks)
        this.evalTimer++;
        if (this.evalTimer >= 4) {
            this.evalTimer = 0;
            evaluateAllTriggers(level(), pipePos, facing);
        }

        // Execute actions every tick
        boolean shouldEmitRedstone = false;
        boolean hasPulsarActive = false;
        boolean hasAnyActive = false;

        Set<PipeWireColor> wiresToBroadcast = EnumSet.noneOf(PipeWireColor.class);

        int activeSlots = getTier().getSlots();
        for (int i = 0; i < activeSlots; i++) {
            if (this.conditionsMet[i]) {
                hasAnyActive = true;
                GateAction action = this.actions[i];
                if (action == GateAction.REDSTONE_SIGNAL) {
                    shouldEmitRedstone = true;
                } else if (action == GateAction.REDSTONE_PULSE) {
                    if ((level().getGameTime() / 20) % 2 == 0) {
                        shouldEmitRedstone = true;
                    }
                } else if (action.isPulsar() && isAutarchic()) {
                    hasPulsarActive = true;
                } else if (action.isWireAction()) {
                    PipeWireColor color = action.getWireColor();
                    if (color != null) wiresToBroadcast.add(color);
                }
            }
        }

        // Handle wire broadcasts along the pipe network
        if (!wiresToBroadcast.isEmpty()) {
            broadcastWires(level(), pipePos, wiresToBroadcast);
        }

        // Handle autarchic pulsing (every 20 ticks / 1 second)
        if (hasPulsarActive && isAutarchic()) {
            this.pulsarTimer++;
            if (this.pulsarTimer >= 20) {
                this.pulsarTimer = 0;
                executePulsar(level(), pipePos, facing);
            }
        } else {
            this.pulsarTimer = 0;
        }

        boolean wasPowered = this.entityData.get(DATA_POWERED);
        if (!level().isClientSide() && shouldEmitRedstone != wasPowered) {
            LevelPos lp1 = new LevelPos(level().dimension(), pipePos.immutable());
            PipeAndTarget pat = findPipeAndInventory(level(), pipePos, facing);
            LevelPos lp2 = (pat != null && !pat.pipePos().equals(pipePos))
                    ? new LevelPos(level().dimension(), pat.pipePos().immutable()) : null;

            if (shouldEmitRedstone) {
                POWERED_GATE_POSITIONS.add(lp1);
                if (lp2 != null) POWERED_GATE_POSITIONS.add(lp2);
            } else {
                POWERED_GATE_POSITIONS.remove(lp1);
                if (lp2 != null) POWERED_GATE_POSITIONS.remove(lp2);
            }
            notifyRedstoneNeighbors(level(), pipePos);
            if (lp2 != null) notifyRedstoneNeighbors(level(), pat.pipePos());
        }

        this.entityData.set(DATA_POWERED, shouldEmitRedstone);
        this.entityData.set(DATA_ACTIVE, hasAnyActive);
    }

    public record PipeAndTarget(BlockPos pipePos, BlockEntity pipeBe, BlockPos invPos, Direction fromPipeToInv) {}

    public static boolean isPipeBlock(Level level, BlockPos pos, @Nullable BlockEntity be) {
        if (be != null && LogisticsPipeCompat.isLogisticsPipe(be)) return true;
        BlockState state = level.getBlockState(pos);
        String name = state.getBlock().getDescriptionId().toLowerCase();
        return name.contains("pipe") || name.contains("tube") || name.contains("duct");
    }

    public static boolean hasInventory(Level level, BlockPos pos, Direction side) {
        if (level == null || pos == null) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (isPipeBlock(level, pos, be)) return false;

        if (be instanceof Container) return true;
        if (level.getCapability(Capabilities.Item.BLOCK, pos, side) != null) return true;
        if (level.getCapability(Capabilities.Item.BLOCK, pos, null) != null) return true;
        if (level.getCapability(Capabilities.Fluid.BLOCK, pos, side) != null) return true;
        if (level.getCapability(Capabilities.Fluid.BLOCK, pos, null) != null) return true;
        return false;
    }

    public static PipeAndTarget findPipeAndInventory(Level level, BlockPos basePos, Direction facing) {
        BlockEntity baseBe = level.getBlockEntity(basePos);
        boolean baseIsPipe = isPipeBlock(level, basePos, baseBe);

        if (baseIsPipe) {
            // Gate is attached to the PIPE!
            BlockPos facingPos = basePos.relative(facing);
            if (hasInventory(level, facingPos, facing.getOpposite())) {
                return new PipeAndTarget(basePos, baseBe, facingPos, facing);
            }
            for (Direction dir : Direction.values()) {
                if (dir == facing) continue;
                BlockPos nPos = basePos.relative(dir);
                if (hasInventory(level, nPos, dir.getOpposite())) {
                    return new PipeAndTarget(basePos, baseBe, nPos, dir);
                }
            }
        } else {
            // Gate is attached to the INVENTORY (e.g. chest)!
            BlockPos facingPos = basePos.relative(facing);
            BlockEntity facingBe = level.getBlockEntity(facingPos);
            if (isPipeBlock(level, facingPos, facingBe)) {
                return new PipeAndTarget(facingPos, facingBe, basePos, facing.getOpposite());
            }
            for (Direction dir : Direction.values()) {
                if (dir == facing) continue;
                BlockPos nPos = basePos.relative(dir);
                BlockEntity nBe = level.getBlockEntity(nPos);
                if (isPipeBlock(level, nPos, nBe)) {
                    return new PipeAndTarget(nPos, nBe, basePos, dir.getOpposite());
                }
            }
        }
        return null;
    }

    private void evaluateAllTriggers(Level level, BlockPos pipePos, Direction facing) {
        Container container = null;
        ResourceHandler<ItemResource> itemHandler = null;
        ResourceHandler<FluidResource> fluidHandler = null;
        BlockPos containerPos = null;

        PipeAndTarget pat = findPipeAndInventory(level, pipePos, facing);
        if (pat != null) {
            containerPos = pat.invPos;
            BlockEntity invBe = level.getBlockEntity(pat.invPos);
            if (invBe instanceof Container c) container = c;
            itemHandler = level.getCapability(Capabilities.Item.BLOCK, pat.invPos, pat.fromPipeToInv.getOpposite());
            if (itemHandler == null) itemHandler = level.getCapability(Capabilities.Item.BLOCK, pat.invPos, null);
            fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pat.invPos, pat.fromPipeToInv.getOpposite());
            if (fluidHandler == null) fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pat.invPos, null);
        } else {
            // Fallback
            itemHandler = level.getCapability(Capabilities.Item.BLOCK, pipePos, facing);
            if (itemHandler == null) itemHandler = level.getCapability(Capabilities.Item.BLOCK, pipePos, null);
            fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pipePos, facing);
            if (fluidHandler == null) fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pipePos, null);
            BlockEntity be = level.getBlockEntity(pipePos);
            if (be instanceof Container c) container = c;
            containerPos = pipePos;
        }

        BlockEntity pipeBe = pat != null ? pat.pipeBe : level.getBlockEntity(pipePos);
        boolean isPipe = pat != null || isPipeBlock(level, pipePos, pipeBe);

        int activeSlots = getTier().getSlots();
        for (int i = 0; i < activeSlots; i++) {
            GateTrigger trigger = this.triggers[i];
            this.conditionsMet[i] = evaluateTrigger(level, pipePos, facing, trigger, pipeBe, isPipe, container, itemHandler, fluidHandler, containerPos);
        }
    }

    private boolean evaluateTrigger(Level level, BlockPos pipePos, Direction facing, GateTrigger trigger,
                                     @Nullable BlockEntity pipeBe, boolean isPipe,
                                     @Nullable Container container,
                                     @Nullable ResourceHandler<ItemResource> itemHandler,
                                     @Nullable ResourceHandler<FluidResource> fluidHandler,
                                     @Nullable BlockPos containerPos) {
        return switch (trigger) {
            case NONE -> false;
            case REDSTONE_ON -> level.hasNeighborSignal(pipePos);
            case REDSTONE_OFF -> !level.hasNeighborSignal(pipePos);
            case INV_EMPTY -> {
                if (container != null) {
                    boolean empty = true;
                    for (int s = 0; s < container.getContainerSize(); s++) {
                        if (!container.getItem(s).isEmpty()) { empty = false; break; }
                    }
                    yield empty;
                }
                if (itemHandler != null) {
                    boolean empty = true;
                    for (int s = 0; s < itemHandler.size(); s++) {
                        if (itemHandler.getAmountAsLong(s) > 0) { empty = false; break; }
                    }
                    yield empty;
                }
                yield false;
            }
            case INV_HAS_ITEMS -> {
                if (container != null) {
                    for (int s = 0; s < container.getContainerSize(); s++) {
                        if (!container.getItem(s).isEmpty()) yield true;
                    }
                    yield false;
                }
                if (itemHandler != null) {
                    for (int s = 0; s < itemHandler.size(); s++) {
                        if (itemHandler.getAmountAsLong(s) > 0) yield true;
                    }
                    yield false;
                }
                yield false;
            }
            case INV_SPACE -> {
                if (container != null) {
                    for (int s = 0; s < container.getContainerSize(); s++) {
                        ItemStack st = container.getItem(s);
                        if (st.isEmpty() || st.getCount() < st.getMaxStackSize()) yield true;
                    }
                    yield false;
                }
                if (itemHandler != null) {
                    for (int s = 0; s < itemHandler.size(); s++) {
                        long amt = itemHandler.getAmountAsLong(s);
                        long cap = itemHandler.getCapacityAsLong(s, itemHandler.getResource(s));
                        if (amt < cap) yield true;
                    }
                    yield false;
                }
                yield false;
            }
            case INV_FULL -> {
                if (container != null) {
                    for (int s = 0; s < container.getContainerSize(); s++) {
                        ItemStack st = container.getItem(s);
                        if (st.isEmpty() || st.getCount() < st.getMaxStackSize()) yield false;
                    }
                    yield true;
                }
                if (itemHandler != null) {
                    for (int s = 0; s < itemHandler.size(); s++) {
                        long amt = itemHandler.getAmountAsLong(s);
                        long cap = itemHandler.getCapacityAsLong(s, itemHandler.getResource(s));
                        if (amt < cap) yield false;
                    }
                    yield true;
                }
                yield false;
            }
            case TANK_EMPTY -> {
                if (fluidHandler == null) yield false;
                long total = 0;
                for (int t = 0; t < fluidHandler.size(); t++) {
                    total += fluidHandler.getAmountAsLong(t);
                }
                yield total == 0;
            }
            case TANK_HAS_FLUID -> {
                if (fluidHandler == null) yield false;
                for (int t = 0; t < fluidHandler.size(); t++) {
                    if (fluidHandler.getAmountAsLong(t) > 0) yield true;
                }
                yield false;
            }
            case TANK_SPACE -> {
                if (fluidHandler == null) yield false;
                for (int t = 0; t < fluidHandler.size(); t++) {
                    long amt = fluidHandler.getAmountAsLong(t);
                    if (amt == 0) yield true; // An empty tank always has space!
                    long cap = fluidHandler.getCapacityAsLong(t, fluidHandler.getResource(t));
                    if (cap <= 0) cap = fluidHandler.getCapacityAsLong(t, net.neoforged.neoforge.transfer.fluid.FluidResource.of(net.minecraft.world.level.material.Fluids.WATER));
                    if (cap > 0 && amt < cap) yield true;
                }
                yield false;
            }
            case TANK_FULL -> {
                if (fluidHandler == null) yield false;
                boolean hasAnyTank = false;
                for (int t = 0; t < fluidHandler.size(); t++) {
                    hasAnyTank = true;
                    long amt = fluidHandler.getAmountAsLong(t);
                    if (amt == 0) yield false; // An empty tank is never full!
                    long cap = fluidHandler.getCapacityAsLong(t, fluidHandler.getResource(t));
                    if (cap <= 0) cap = fluidHandler.getCapacityAsLong(t, net.neoforged.neoforge.transfer.fluid.FluidResource.of(net.minecraft.world.level.material.Fluids.WATER));
                    if (cap > 0 && amt < cap) yield false;
                }
                yield hasAnyTank;
            }
            case PIPE_EMPTY -> {
                if (isPipe && pipeBe != null) {
                    if (LogisticsPipeCompat.hasTravelingItems(pipeBe)) yield false;
                    if (LogisticsPipeCompat.getFluidMilliBuckets(pipeBe) > 0) yield false;
                }
                yield true;
            }
            case PIPE_FLOWING -> {
                if (isPipe && pipeBe != null) {
                    if (LogisticsPipeCompat.hasTravelingItems(pipeBe)) yield true;
                    if (LogisticsPipeCompat.getFluidMilliBuckets(pipeBe) > 0) yield true;
                }
                yield false;
            }
            case MACHINE_ACTIVE -> {
                if (containerPos != null) {
                    BlockState s = level.getBlockState(containerPos);
                    for (var prop : s.getProperties()) {
                        if (prop instanceof net.minecraft.world.level.block.state.properties.BooleanProperty bp) {
                            String name = bp.getName().toLowerCase();
                            if (name.equals("working") || name.equals("active") || name.equals("lit") || name.equals("running") || name.equals("powered")) {
                                if (s.getValue(bp)) yield true;
                            }
                        }
                    }
                    BlockEntity be = level.getBlockEntity(containerPos);
                    if (be instanceof com.refinery.block.entity.AutoHarvesterBlockEntity harvester && harvester.isWorking()) yield true;
                    if (be instanceof com.refinery.block.entity.AutoPlanterBlockEntity planter && planter.isWorking()) yield true;
                }
                yield false;
            }
            case MACHINE_IDLE -> {
                if (containerPos != null) {
                    BlockState s = level.getBlockState(containerPos);
                    boolean foundActiveProp = false;
                    for (var prop : s.getProperties()) {
                        if (prop instanceof net.minecraft.world.level.block.state.properties.BooleanProperty bp) {
                            String name = bp.getName().toLowerCase();
                            if (name.equals("working") || name.equals("active") || name.equals("lit") || name.equals("running") || name.equals("powered")) {
                                foundActiveProp = true;
                                if (s.getValue(bp)) yield false;
                            }
                        }
                    }
                    BlockEntity be = level.getBlockEntity(containerPos);
                    if (be instanceof com.refinery.block.entity.AutoHarvesterBlockEntity harvester) {
                        yield !harvester.isWorking();
                    }
                    if (be instanceof com.refinery.block.entity.AutoPlanterBlockEntity planter) {
                        yield !planter.isWorking();
                    }
                    if (foundActiveProp) yield true;
                }
                yield false;
            }
            case WIRE_RED_ON -> wireStates[0];
            case WIRE_RED_OFF -> !wireStates[0];
            case WIRE_BLUE_ON -> wireStates[1];
            case WIRE_BLUE_OFF -> !wireStates[1];
            case WIRE_GREEN_ON -> wireStates[2];
            case WIRE_GREEN_OFF -> !wireStates[2];
            case WIRE_YELLOW_ON -> wireStates[3];
            case WIRE_YELLOW_OFF -> !wireStates[3];
        };
    }

    private void executePulsar(Level level, BlockPos basePos, Direction facing) {
        PipeAndTarget pat = findPipeAndInventory(level, basePos, facing);
        if (pat == null) return;

        BlockEntity invBe = level.getBlockEntity(pat.invPos);

        // 1. Item Pull Action from Container
        if (invBe instanceof Container container) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack st = container.getItem(slot);
                if (!st.isEmpty()) {
                    ItemStack one = st.copyWithCount(1);
                    if (LogisticsPipeCompat.injectItemIntoPipe(pat.pipeBe, pat.fromPipeToInv, one.copy())) {
                        container.removeItem(slot, 1);
                        spawnPulsarEffect(level, pat.pipePos);
                        return;
                    }
                    ResourceHandler<ItemResource> pipeHandler = level.getCapability(Capabilities.Item.BLOCK, pat.pipePos, pat.fromPipeToInv);
                    if (pipeHandler == null) pipeHandler = level.getCapability(Capabilities.Item.BLOCK, pat.pipePos, null);
                    if (pipeHandler != null) {
                        try (Transaction tx = Transaction.openRoot()) {
                            int ins = pipeHandler.insert(ItemResource.of(one), 1, tx);
                            if (ins > 0) {
                                tx.commit();
                                container.removeItem(slot, 1);
                                spawnPulsarEffect(level, pat.pipePos);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // 2. Item Pull Action from Item ResourceHandler
        ResourceHandler<ItemResource> itemHandler = level.getCapability(Capabilities.Item.BLOCK, pat.invPos, pat.fromPipeToInv.getOpposite());
        if (itemHandler == null) itemHandler = level.getCapability(Capabilities.Item.BLOCK, pat.invPos, null);
        if (itemHandler != null) {
            for (int s = 0; s < itemHandler.size(); s++) {
                ItemResource res = itemHandler.getResource(s);
                if (!res.isEmpty() && itemHandler.getAmountAsLong(s) > 0) {
                    ItemStack one = res.toStack(1);
                    if (LogisticsPipeCompat.injectItemIntoPipe(pat.pipeBe, pat.fromPipeToInv, one.copy())) {
                        try (Transaction tx = Transaction.openRoot()) {
                            itemHandler.extract(s, res, 1, tx);
                            tx.commit();
                        }
                        spawnPulsarEffect(level, pat.pipePos);
                        return;
                    }
                    ResourceHandler<ItemResource> pipeHandler = level.getCapability(Capabilities.Item.BLOCK, pat.pipePos, pat.fromPipeToInv);
                    if (pipeHandler == null) pipeHandler = level.getCapability(Capabilities.Item.BLOCK, pat.pipePos, null);
                    if (pipeHandler != null) {
                        try (Transaction tx = Transaction.openRoot()) {
                            int ins = pipeHandler.insert(res, 1, tx);
                            if (ins > 0) {
                                itemHandler.extract(s, res, 1, tx);
                                tx.commit();
                                spawnPulsarEffect(level, pat.pipePos);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // 3. Fluid Pump Action
        ResourceHandler<FluidResource> sourceFluid = level.getCapability(Capabilities.Fluid.BLOCK, pat.invPos, pat.fromPipeToInv.getOpposite());
        if (sourceFluid == null) sourceFluid = level.getCapability(Capabilities.Fluid.BLOCK, pat.invPos, null);

        ResourceHandler<FluidResource> pipeFluid = level.getCapability(Capabilities.Fluid.BLOCK, pat.pipePos, pat.fromPipeToInv);
        if (pipeFluid == null) pipeFluid = level.getCapability(Capabilities.Fluid.BLOCK, pat.pipePos, null);

        if (sourceFluid != null && pipeFluid != null) {
            for (int t = 0; t < sourceFluid.size(); t++) {
                FluidResource res = sourceFluid.getResource(t);
                long currentAmt = sourceFluid.getAmountAsLong(t);
                if (!res.isEmpty() && currentAmt > 0) {
                    try (Transaction tx = Transaction.openRoot()) {
                        int toMove = (int) Math.min(250, currentAmt);
                        int ins = pipeFluid.insert(res, toMove, tx);
                        if (ins > 0) {
                            sourceFluid.extract(t, res, ins, tx);
                            tx.commit();
                            spawnPulsarEffect(level, pat.pipePos);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void spawnPulsarEffect(Level level, BlockPos pipePos) {
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(DustParticleOptions.REDSTONE,
                    getX(), getY(), getZ(),
                    6, 0.1, 0.1, 0.1, 0.05);
            sl.playSound(null, blockPosition(), SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.4f, 1.6f);
        }
    }

    private void broadcastWires(Level level, BlockPos pipePos, Set<PipeWireColor> colors) {
        for (PipeWireColor c : colors) {
            propagateWireSignal(level, pipePos, c, 32);
        }
    }

    public static void propagateWireSignal(Level level, BlockPos startPos, PipeWireColor color, int maxDistance) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            // Set wire signal active on any PipeWireEntity at current
            List<PipeWireEntity> wires = level.getEntitiesOfClass(PipeWireEntity.class, new AABB(current));
            for (PipeWireEntity w : wires) {
                if (w.hasWire(color)) {
                    w.setSignalActive(color, true);
                }
            }

            // Set wire signal active on any PipeGateEntity at current
            List<PipeGateEntity> gates = level.getEntitiesOfClass(PipeGateEntity.class, new AABB(current));
            for (PipeGateEntity g : gates) {
                if (g.getTier().supportsWire(color)) {
                    g.wireStates[color.ordinal()] = true;
                }
            }

            if (visited.size() >= maxDistance) continue;

            for (Direction d : Direction.values()) {
                BlockPos nPos = current.relative(d);
                if (!visited.contains(nPos)) {
                    BlockState nState = level.getBlockState(nPos);
                    if (com.refinery.item.PipeWireItem.isPipeOrHost(level, nPos, nState)) {
                        visited.add(nPos);
                        queue.add(nPos);
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.refinery.gate");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GateMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setTier(GateTier.values()[Math.clamp(input.getIntOr("Tier", 0), 0, GateTier.values().length - 1)]);
        setAutarchic(input.getBooleanOr("Autarchic", false));
        setAttachedFace(Direction.from3DDataValue(input.getIntOr("Facing", 0)));
        int px = input.getIntOr("PipeX", 0);
        int py = input.getIntOr("PipeY", 0);
        int pz = input.getIntOr("PipeZ", 0);
        setPipePos(new BlockPos(px, py, pz));

        for (int i = 0; i < MAX_SLOTS; i++) {
            triggers[i] = GateTrigger.values()[Math.clamp(input.getIntOr("Trigger_" + i, 0), 0, GateTrigger.values().length - 1)];
            actions[i] = GateAction.values()[Math.clamp(input.getIntOr("Action_" + i, 0), 0, GateAction.values().length - 1)];
        }
        for (int i = 0; i < 4; i++) {
            wireStates[i] = input.getBooleanOr("Wire_" + i, false);
        }
        recalculateBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tier", getTier().ordinal());
        output.putBoolean("Autarchic", isAutarchic());
        output.putInt("Facing", getAttachedFace().get3DDataValue());
        BlockPos pp = getPipePos();
        output.putInt("PipeX", pp.getX());
        output.putInt("PipeY", pp.getY());
        output.putInt("PipeZ", pp.getZ());

        for (int i = 0; i < MAX_SLOTS; i++) {
            output.putInt("Trigger_" + i, triggers[i].ordinal());
            output.putInt("Action_" + i, actions[i].ordinal());
        }
        for (int i = 0; i < 4; i++) {
            output.putBoolean("Wire_" + i, wireStates[i]);
        }
    }
}
