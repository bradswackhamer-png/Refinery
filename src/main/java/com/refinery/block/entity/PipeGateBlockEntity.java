package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.PipeGateBlock;
import com.refinery.compat.LogisticsPipeCompat;
import com.refinery.gate.GateAction;
import com.refinery.gate.GateTier;
import com.refinery.gate.GateTrigger;
import com.refinery.gate.PipeWireColor;
import com.refinery.menu.GateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PipeGateBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MAX_SLOTS = 8;
    public static final int DATA_COUNT = 20;

    private GateTier tier;
    private boolean autarchic;

    private final GateTrigger[] triggers = new GateTrigger[MAX_SLOTS];
    private final GateAction[] actions = new GateAction[MAX_SLOTS];
    private final boolean[] conditionsMet = new boolean[MAX_SLOTS];
    private final boolean[] wireStates = new boolean[4]; // RED, BLUE, GREEN, YELLOW

    private int pulsarTimer = 0;
    private int evalTimer = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) return tier.ordinal();
            if (index == 1) return autarchic ? 1 : 0;
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
                if (value >= 0 && value < GateTier.values().length) tier = GateTier.values()[value];
            } else if (index == 1) {
                autarchic = (value != 0);
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

    public PipeGateBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, GateTier.BASIC, false);
    }

    public PipeGateBlockEntity(BlockPos pos, BlockState state, GateTier tier, boolean autarchic) {
        super(RefineryMod.PIPE_GATE_BE.get(), pos, state);
        this.tier = tier;
        this.autarchic = autarchic;
        for (int i = 0; i < MAX_SLOTS; i++) {
            triggers[i] = GateTrigger.NONE;
            actions[i] = GateAction.NONE;
            conditionsMet[i] = false;
        }
    }

    public GateTier getTier() {
        return tier;
    }

    public void setTier(GateTier tier) {
        this.tier = tier;
        setChanged();
    }

    public boolean isAutarchic() {
        return autarchic;
    }

    public void setAutarchic(boolean autarchic) {
        this.autarchic = autarchic;
        setChanged();
    }

    public GateTrigger getTrigger(int slot) {
        return (slot >= 0 && slot < MAX_SLOTS) ? triggers[slot] : GateTrigger.NONE;
    }

    public GateAction getAction(int slot) {
        return (slot >= 0 && slot < MAX_SLOTS) ? actions[slot] : GateAction.NONE;
    }

    public boolean isConditionMet(int slot) {
        return (slot >= 0 && slot < MAX_SLOTS) && conditionsMet[slot];
    }

    public boolean isWireActive(PipeWireColor color) {
        return wireStates[color.ordinal()];
    }

    public void cycleTrigger(int slot, boolean forward) {
        if (slot < 0 || slot >= tier.getSlots()) return;
        triggers[slot] = forward ? triggers[slot].next(tier) : triggers[slot].previous(tier);
        setChanged();
    }

    public void cycleAction(int slot, boolean forward) {
        if (slot < 0 || slot >= tier.getSlots()) return;
        actions[slot] = forward ? actions[slot].next(tier, autarchic) : actions[slot].previous(tier, autarchic);
        setChanged();
    }

    public void copyFrom(PipeGateBlockEntity other) {
        this.tier = other.tier;
        this.autarchic = other.autarchic;
        for (int i = 0; i < MAX_SLOTS; i++) {
            this.triggers[i] = other.triggers[i];
            this.actions[i] = other.actions[i];
            this.conditionsMet[i] = other.conditionsMet[i];
        }
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PipeGateBlockEntity gate) {
        Direction facing = state.getValue(PipeGateBlock.FACING);
        BlockPos attachedPos = pos.relative(facing);

        // Periodically evaluate conditions (every 4 ticks)
        gate.evalTimer++;
        if (gate.evalTimer >= 4) {
            gate.evalTimer = 0;
            gate.evaluateAllTriggers(level, pos, attachedPos, facing);
        }

        // Execute actions every tick
        boolean shouldEmitRedstone = false;
        boolean hasPulsarActive = false;
        boolean hasAnyActive = false;

        Set<PipeWireColor> wiresToBroadcast = EnumSet.noneOf(PipeWireColor.class);

        int activeSlots = gate.tier.getSlots();
        for (int i = 0; i < activeSlots; i++) {
            if (gate.conditionsMet[i]) {
                hasAnyActive = true;
                GateAction action = gate.actions[i];
                if (action == GateAction.REDSTONE_SIGNAL) {
                    shouldEmitRedstone = true;
                } else if (action == GateAction.REDSTONE_PULSE) {
                    if ((level.getGameTime() / 20) % 2 == 0) {
                        shouldEmitRedstone = true;
                    }
                } else if (action.isPulsar() && gate.autarchic) {
                    hasPulsarActive = true;
                } else if (action.isWireAction()) {
                    PipeWireColor color = action.getWireColor();
                    if (color != null) wiresToBroadcast.add(color);
                }
            }
        }

        // Handle wire broadcasts along the pipe network
        if (!wiresToBroadcast.isEmpty()) {
            gate.broadcastWires(level, attachedPos, wiresToBroadcast);
        }

        // Handle autarchic pulsing (every 20 ticks / 1 second)
        if (hasPulsarActive && gate.autarchic) {
            gate.pulsarTimer++;
            if (gate.pulsarTimer >= 20) {
                gate.pulsarTimer = 0;
                gate.executePulsar(level, pos, attachedPos, facing);
            }
        } else {
            gate.pulsarTimer = 0;
        }

        // Update block states if changed
        boolean curPowered = state.getValue(PipeGateBlock.POWERED);
        boolean curActive = state.getValue(PipeGateBlock.ACTIVE);

        if (curPowered != shouldEmitRedstone || curActive != hasAnyActive) {
            level.setBlock(pos, state.setValue(PipeGateBlock.POWERED, shouldEmitRedstone)
                    .setValue(PipeGateBlock.ACTIVE, hasAnyActive), 3);
            level.updateNeighborsAt(pos, state.getBlock());
            level.updateNeighborsAt(attachedPos, state.getBlock());
        }
    }

    private void evaluateAllTriggers(Level level, BlockPos pos, BlockPos attachedPos, Direction facing) {
        BlockState attachedState = level.getBlockState(attachedPos);
        BlockEntity attachedBe = level.getBlockEntity(attachedPos);

        boolean isPipe = LogisticsPipeCompat.isLogisticsPipe(attachedBe) ||
                attachedState.getBlock().getDescriptionId().contains("pipe");

        Container container = null;
        ResourceHandler<ItemResource> itemHandler = null;
        ResourceHandler<FluidResource> fluidHandler = null;
        Direction containerDir = null;
        BlockPos containerPos = null;

        if (isPipe) {
            // Find adjacent container/tank connected to the pipe (inspect 6 directions from pipe)
            for (Direction dir : Direction.values()) {
                if (dir == facing.getOpposite()) continue; // Skip back to the gate
                BlockPos nPos = attachedPos.relative(dir);
                BlockEntity nBe = level.getBlockEntity(nPos);
                if (container == null && nBe instanceof Container c) {
                    container = c;
                    containerPos = nPos;
                    containerDir = dir.getOpposite();
                }
                if (itemHandler == null) {
                    itemHandler = level.getCapability(Capabilities.Item.BLOCK, nPos, dir.getOpposite());
                    if (itemHandler != null && containerPos == null) {
                        containerPos = nPos;
                        containerDir = dir.getOpposite();
                    }
                }
                if (fluidHandler == null) {
                    fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, nPos, dir.getOpposite());
                    if (fluidHandler != null && containerPos == null) {
                        containerPos = nPos;
                        containerDir = dir.getOpposite();
                    }
                }
            }
        } else {
            // Attached block is itself the container / machine / tank
            if (attachedBe instanceof Container c) {
                container = c;
            }
            itemHandler = level.getCapability(Capabilities.Item.BLOCK, attachedPos, facing.getOpposite());
            fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, attachedPos, facing.getOpposite());
            containerPos = attachedPos;
            containerDir = facing.getOpposite();
        }

        int activeSlots = tier.getSlots();
        for (int i = 0; i < activeSlots; i++) {
            GateTrigger trigger = triggers[i];
            conditionsMet[i] = evaluateCondition(trigger, level, pos, attachedPos, attachedState, attachedBe,
                    isPipe, container, itemHandler, fluidHandler, containerPos, containerDir);
        }
    }

    private boolean evaluateCondition(GateTrigger trigger, Level level, BlockPos pos, BlockPos attachedPos,
                                      BlockState attachedState, BlockEntity attachedBe, boolean isPipe,
                                      @Nullable Container container,
                                      @Nullable ResourceHandler<ItemResource> itemHandler,
                                      @Nullable ResourceHandler<FluidResource> fluidHandler,
                                      @Nullable BlockPos containerPos, @Nullable Direction containerDir) {
        return switch (trigger) {
            case NONE -> false;
            case REDSTONE_ON -> level.hasNeighborSignal(pos) || level.getSignal(pos, getBlockState().getValue(PipeGateBlock.FACING)) > 0;
            case REDSTONE_OFF -> !level.hasNeighborSignal(pos) && level.getSignal(pos, getBlockState().getValue(PipeGateBlock.FACING)) == 0;
            case INV_EMPTY -> {
                if (container != null) {
                    boolean empty = true;
                    for (int s = 0; s < container.getContainerSize(); s++) {
                        if (!container.getItem(s).isEmpty()) {
                            empty = false;
                            break;
                        }
                    }
                    yield empty;
                }
                if (itemHandler != null) {
                    boolean empty = true;
                    for (int s = 0; s < itemHandler.size(); s++) {
                        if (itemHandler.getAmountAsLong(s) > 0) {
                            empty = false;
                            break;
                        }
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
                    long cap = fluidHandler.getCapacityAsLong(t, fluidHandler.getResource(t));
                    if (amt < cap) yield true;
                }
                yield false;
            }
            case TANK_FULL -> {
                if (fluidHandler == null) yield false;
                for (int t = 0; t < fluidHandler.size(); t++) {
                    long amt = fluidHandler.getAmountAsLong(t);
                    long cap = fluidHandler.getCapacityAsLong(t, fluidHandler.getResource(t));
                    if (amt < cap) yield false;
                }
                yield true;
            }
            case PIPE_EMPTY -> {
                if (isPipe && attachedBe != null) {
                    if (LogisticsPipeCompat.hasTravelingItems(attachedBe)) yield false;
                    if (LogisticsPipeCompat.getFluidMilliBuckets(attachedBe) > 0) yield false;
                }
                yield true;
            }
            case PIPE_FLOWING -> {
                if (isPipe && attachedBe != null) {
                    if (LogisticsPipeCompat.hasTravelingItems(attachedBe)) yield true;
                    if (LogisticsPipeCompat.getFluidMilliBuckets(attachedBe) > 0) yield true;
                }
                yield false;
            }
            case MACHINE_ACTIVE -> {
                if (containerPos != null) {
                    BlockState s = level.getBlockState(containerPos);
                    if (s.hasProperty(BlockStateProperties.LIT) && s.getValue(BlockStateProperties.LIT)) yield true;
                    if (s.hasProperty(PipeGateBlock.ACTIVE) && s.getValue(PipeGateBlock.ACTIVE)) yield true;
                }
                yield false;
            }
            case MACHINE_IDLE -> {
                if (containerPos != null) {
                    BlockState s = level.getBlockState(containerPos);
                    if (s.hasProperty(BlockStateProperties.LIT) && !s.getValue(BlockStateProperties.LIT)) yield true;
                    if (s.hasProperty(PipeGateBlock.ACTIVE) && !s.getValue(PipeGateBlock.ACTIVE)) yield true;
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

    private void executePulsar(Level level, BlockPos pos, BlockPos attachedPos, Direction facing) {
        BlockEntity attachedBe = level.getBlockEntity(attachedPos);

        // Find container connected to the pipe
        for (Direction dir : Direction.values()) {
            if (dir == facing.getOpposite()) continue;
            BlockPos nPos = attachedPos.relative(dir);
            BlockEntity nBe = level.getBlockEntity(nPos);

            // Item Pull Action from Container
            if (nBe instanceof Container container) {
                for (int slot = 0; slot < container.getContainerSize(); slot++) {
                    ItemStack st = container.getItem(slot);
                    if (!st.isEmpty()) {
                        ItemStack one = st.copyWithCount(1);
                        // Try Logistics pipe first
                        if (LogisticsPipeCompat.addItemToPipe(attachedBe, dir, one.copy())) {
                            container.removeItem(slot, 1);
                            spawnPulsarEffect(level, pos, facing);
                            return;
                        }
                        // Try standard pipe Transfer capability
                        ResourceHandler<ItemResource> pipeHandler = level.getCapability(Capabilities.Item.BLOCK, attachedPos, dir);
                        if (pipeHandler != null) {
                            try (Transaction tx = Transaction.openRoot()) {
                                int ins = pipeHandler.insert(ItemResource.of(one), 1, tx);
                                if (ins > 0) {
                                    tx.commit();
                                    container.removeItem(slot, 1);
                                    spawnPulsarEffect(level, pos, facing);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            // Item Pull Action from Item ResourceHandler
            ResourceHandler<ItemResource> itemHandler = level.getCapability(Capabilities.Item.BLOCK, nPos, dir.getOpposite());
            if (itemHandler != null) {
                for (int s = 0; s < itemHandler.size(); s++) {
                    ItemResource res = itemHandler.getResource(s);
                    if (!res.isEmpty() && itemHandler.getAmountAsLong(s) > 0) {
                        ItemStack one = res.toStack(1);
                        if (LogisticsPipeCompat.addItemToPipe(attachedBe, dir, one.copy())) {
                            try (Transaction tx = Transaction.openRoot()) {
                                itemHandler.extract(s, res, 1, tx);
                                tx.commit();
                            }
                            spawnPulsarEffect(level, pos, facing);
                            return;
                        }
                        ResourceHandler<ItemResource> pipeHandler = level.getCapability(Capabilities.Item.BLOCK, attachedPos, dir);
                        if (pipeHandler != null) {
                            try (Transaction tx = Transaction.openRoot()) {
                                int ins = pipeHandler.insert(res, 1, tx);
                                if (ins > 0) {
                                    itemHandler.extract(s, res, 1, tx);
                                    tx.commit();
                                    spawnPulsarEffect(level, pos, facing);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            // Fluid Pump Action
            ResourceHandler<FluidResource> sourceFluid = level.getCapability(Capabilities.Fluid.BLOCK, nPos, dir.getOpposite());
            ResourceHandler<FluidResource> pipeFluid = level.getCapability(Capabilities.Fluid.BLOCK, attachedPos, dir);
            if (sourceFluid != null && pipeFluid != null) {
                for (int t = 0; t < sourceFluid.size(); t++) {
                    FluidResource res = sourceFluid.getResource(t);
                    if (!res.isEmpty() && sourceFluid.getAmountAsLong(t) > 0) {
                        try (Transaction tx = Transaction.openRoot()) {
                            int toMove = (int) Math.min(250, sourceFluid.getAmountAsLong(t));
                            int ins = pipeFluid.insert(res, toMove, tx);
                            if (ins > 0) {
                                sourceFluid.extract(t, res, ins, tx);
                                tx.commit();
                                spawnPulsarEffect(level, pos, facing);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnPulsarEffect(Level level, BlockPos pos, Direction facing) {
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5 + facing.getStepX() * -0.4;
            double y = pos.getY() + 0.5 + facing.getStepY() * -0.4;
            double z = pos.getZ() + 0.5 + facing.getStepZ() * -0.4;
            serverLevel.sendParticles(new DustParticleOptions(0x33DDFF, 1.2f),
                    x, y, z, 6, 0.1, 0.1, 0.1, 0.05);
            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.6f, 1.5f);
        }
    }

    private void broadcastWires(Level level, BlockPos startPipePos, Set<PipeWireColor> colors) {
        // BFS along continuous pipe blocks up to 24 blocks
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPipePos);
        visited.add(startPipePos);

        while (!queue.isEmpty() && visited.size() < 24) {
            BlockPos current = queue.poll();

            // Check if any gates are attached to this pipe
            for (Direction dir : Direction.values()) {
                BlockPos gatePos = current.relative(dir);
                BlockEntity be = level.getBlockEntity(gatePos);
                if (be instanceof PipeGateBlockEntity otherGate && otherGate != this) {
                    BlockState gState = level.getBlockState(gatePos);
                    if (gState.getBlock() instanceof PipeGateBlock && gState.getValue(PipeGateBlock.FACING) == dir.getOpposite()) {
                        for (PipeWireColor c : colors) {
                            otherGate.wireStates[c.ordinal()] = true;
                        }
                    }
                }
            }

            // Continue pipe traversal
            for (Direction dir : Direction.values()) {
                BlockPos next = current.relative(dir);
                if (!visited.contains(next)) {
                    BlockEntity nextBe = level.getBlockEntity(next);
                    if (LogisticsPipeCompat.isLogisticsPipe(nextBe) ||
                        level.getBlockState(next).getBlock().getDescriptionId().contains("pipe")) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        String name = autarchic ? tier.getDisplayName() + " (Autarchic)" : tier.getDisplayName();
        return Component.literal(name);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GateMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("Tier", tier.name());
        output.putBoolean("Autarchic", autarchic);

        for (int i = 0; i < MAX_SLOTS; i++) {
            output.putString("Trigger_" + i, triggers[i].name());
            output.putString("Action_" + i, actions[i].name());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String tierStr = input.getStringOr("Tier", "BASIC");
        try {
            this.tier = GateTier.valueOf(tierStr);
        } catch (Exception e) {
            this.tier = GateTier.BASIC;
        }

        this.autarchic = input.getBooleanOr("Autarchic", false);

        for (int i = 0; i < MAX_SLOTS; i++) {
            String trigStr = input.getStringOr("Trigger_" + i, "NONE");
            try {
                triggers[i] = GateTrigger.valueOf(trigStr);
            } catch (Exception e) {
                triggers[i] = GateTrigger.NONE;
            }

            String actStr = input.getStringOr("Action_" + i, "NONE");
            try {
                actions[i] = GateAction.valueOf(actStr);
            } catch (Exception e) {
                actions[i] = GateAction.NONE;
            }
        }
    }
}
