package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.StorageTankDrainValveBlock;
import com.refinery.block.StorageTankFillValveBlock;
import com.refinery.block.pipe.IndustrialPipeMaterial;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.block.pipe.PipeConnectionType;
import com.refinery.fluid.GenericFluidTank;
import com.refinery.menu.DiamondPipeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class IndustrialPipeBlockEntity extends BlockEntity implements Container, MenuProvider {
    private final IndustrialPipeType pipeType;
    private final IndustrialPipeMaterial material;

    private final Map<Direction, PipeConnectionType> connections = new EnumMap<>(Direction.class);
    private final Map<Direction, Boolean> manualDisconnections = new EnumMap<>(Direction.class);

    // Fluid Tank
    private final GenericFluidTank fluidTank;

    // Item Storage & Traveling Items
    public static class TravelingItem {
        public ItemStack stack;
        public Direction fromDir;
        public Direction toDir;
        public float progress; // 0.0f (entered) to 1.0f (exiting)

        public TravelingItem(ItemStack stack, Direction fromDir, Direction toDir, float progress) {
            this.stack = stack;
            this.fromDir = fromDir;
            this.toDir = toDir;
            this.progress = progress;
        }
    }

    private final List<TravelingItem> travelingItems = new ArrayList<>();
    private ItemStack internalItem = ItemStack.EMPTY;

    // Emerald Round-Robin Cycle Index
    private int roundRobinIndex = 0;

    // Diamond Pipe Sorting Filters (6 directions x 9 slots = 54 slots)
    private final SimpleContainer diamondFilters = new SimpleContainer(54) {
        @Override
        public void setChanged() {
            super.setChanged();
            IndustrialPipeBlockEntity.this.setChanged();
        }
    };

    private Direction mergeOutput = Direction.NORTH;

    public Direction getMergeOutput() {
        return mergeOutput;
    }

    public void setMergeOutput(Direction dir) {
        this.mergeOutput = dir != null ? dir : Direction.NORTH;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void cycleOrSetMergeOutput(@Nullable Direction clickedSide, @Nullable Player player) {
        Direction newOut;
        if (clickedSide != null && this.mergeOutput != clickedSide) {
            newOut = clickedSide;
        } else {
            Direction[] dirs = Direction.values();
            int curIdx = this.mergeOutput.ordinal();
            newOut = dirs[(curIdx + 1) % dirs.length];
            for (int i = 1; i <= 6; i++) {
                Direction candidate = dirs[(curIdx + i) % dirs.length];
                if (getConnection(candidate).isConnected()) {
                    newOut = candidate;
                    break;
                }
            }
        }
        this.mergeOutput = newOut;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.playSound(null, worldPosition, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.8f, 1.4f);
        }
        if (player != null) {
            player.sendSystemMessage(
                    Component.literal("Merge Pipe Output: ")
                            .withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(newOut.getName().toUpperCase()).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal(" (All other connections set as inputs)").withStyle(ChatFormatting.GRAY))
            );
        }
    }

    public boolean isExtractor() {
        return this.pipeType.isExtractor() || this.material == IndustrialPipeMaterial.WOOD;
    }

    public SimpleContainer getDiamondFilters() {
        return diamondFilters;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DiamondPipeMenu(containerId, playerInventory, this.diamondFilters);
    }

    public IndustrialPipeBlockEntity(BlockPos pos, BlockState state) {
        this(
                pos,
                state,
                state.getBlock() instanceof com.refinery.block.pipe.IndustrialPipeBlock ipb ? ipb.getPipeType() : IndustrialPipeType.FLUID,
                state.getBlock() instanceof com.refinery.block.pipe.IndustrialPipeBlock ipb ? ipb.getMaterial() : IndustrialPipeMaterial.STEEL
        );
    }

    public IndustrialPipeBlockEntity(BlockPos pos, BlockState state, IndustrialPipeType pipeType, IndustrialPipeMaterial material) {
        super(RefineryMod.INDUSTRIAL_PIPE_BE.get(), pos, state);
        this.pipeType = pipeType;
        this.material = material;
        if (pipeType == IndustrialPipeType.VOID) {
            this.fluidTank = new GenericFluidTank(
                    () -> 100_000,
                    true,
                    false,
                    null
            ) {
                @Override
                public boolean isValid(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource) {
                    return true;
                }

                @Override
                public int insert(int index, net.neoforged.neoforge.transfer.fluid.FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                    return amount;
                }
            };
        } else {
            this.fluidTank = new GenericFluidTank(
                    material::getFluidCapacity,
                    pipeType.handlesFluid(),
                    pipeType.handlesFluid(),
                    this::onTankChanged
            );
        }

        for (Direction dir : Direction.values()) {
            connections.put(dir, PipeConnectionType.NONE);
            manualDisconnections.put(dir, false);
        }
    }

    private void onTankChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public IndustrialPipeType getPipeType() {
        if (pipeType != null) return pipeType;
        if (getBlockState().getBlock() instanceof com.refinery.block.pipe.IndustrialPipeBlock ipb) {
            return ipb.getPipeType();
        }
        return IndustrialPipeType.FLUID;
    }

    public IndustrialPipeMaterial getMaterial() {
        if (material != null) return material;
        if (getBlockState().getBlock() instanceof com.refinery.block.pipe.IndustrialPipeBlock ipb) {
            return ipb.getMaterial();
        }
        return IndustrialPipeMaterial.STEEL;
    }

    public PipeConnectionType getConnection(Direction dir) {
        return connections.getOrDefault(dir, PipeConnectionType.NONE);
    }

    public boolean isManuallyDisconnected(Direction dir) {
        return manualDisconnections.getOrDefault(dir, false);
    }

    public GenericFluidTank getFluidTank() {
        return fluidTank;
    }

    public List<TravelingItem> getTravelingItems() {
        return travelingItems;
    }

    public void toggleSideConnection(Direction dir) {
        boolean currentlyDisabled = manualDisconnections.getOrDefault(dir, false);
        manualDisconnections.put(dir, !currentlyDisabled);
        updateConnections();
    }

    public boolean isSideBlockedByBlank(Direction dir) {
        if (level == null) return false;
        // Check for blank gate on this pipe's face
        List<com.refinery.entity.PipeGateEntity> gatesHere = level.getEntitiesOfClass(
                com.refinery.entity.PipeGateEntity.class, new net.minecraft.world.phys.AABB(worldPosition));
        for (com.refinery.entity.PipeGateEntity g : gatesHere) {
            if (g.getAttachedFace() == dir && g.getTier() == com.refinery.gate.GateTier.BLANK) {
                return true;
            }
        }
        // Also check if neighbor block has a blank gate on its opposing face pointing towards this pipe
        BlockPos targetPos = worldPosition.relative(dir);
        if (level.isLoaded(targetPos)) {
            List<com.refinery.entity.PipeGateEntity> gatesNeighbor = level.getEntitiesOfClass(
                    com.refinery.entity.PipeGateEntity.class, new net.minecraft.world.phys.AABB(targetPos));
            for (com.refinery.entity.PipeGateEntity g : gatesNeighbor) {
                if (g.getAttachedFace() == dir.getOpposite() && g.getTier() == com.refinery.gate.GateTier.BLANK) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isItemMachine(Level level, BlockPos pos, Direction side) {
        if (level == null || pos == null || !level.isLoaded(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IndustrialPipeBlockEntity) return false;

        if (level.getCapability(Capabilities.Item.BLOCK, pos, side) != null) return true;
        if (level.getCapability(Capabilities.Item.BLOCK, pos, null) != null) return true;
        if (be instanceof Container) return true;

        return state.getBlock() instanceof com.refinery.block.GrinderPortBlock ||
               state.getBlock() instanceof com.refinery.block.RefineryPortBlock ||
               state.getBlock() instanceof com.refinery.block.FoundryPortBlock ||
               state.getBlock() instanceof com.refinery.block.BoilerPortBlock ||
               state.getBlock() instanceof com.refinery.block.ConveyorBeltBlock ||
               state.getBlock() instanceof com.refinery.block.SlopedConveyorBeltBlock ||
               state.getBlock() instanceof com.refinery.block.RightAngleConveyorBeltBlock;
    }

    public static boolean isFluidMachine(Level level, BlockPos pos, Direction side) {
        if (level == null || pos == null || !level.isLoaded(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IndustrialPipeBlockEntity) return false;

        if (level.getCapability(Capabilities.Fluid.BLOCK, pos, side) != null) return true;
        if (level.getCapability(Capabilities.Fluid.BLOCK, pos, null) != null) return true;

        if (state.getBlock() instanceof com.refinery.block.StorageTankDrainValveBlock) {
            return state.getValue(StorageTankDrainValveBlock.ASSEMBLED);
        }
        if (state.getBlock() instanceof com.refinery.block.StorageTankFillValveBlock) {
            return state.getValue(StorageTankFillValveBlock.ASSEMBLED);
        }

        return state.getBlock() instanceof com.refinery.block.BoilerPortBlock ||
               state.getBlock() instanceof com.refinery.block.RefineryPortBlock;
    }

    public void updateConnections() {
        if (level == null) return;
        boolean changed = false;

        for (Direction dir : Direction.values()) {
            if (manualDisconnections.getOrDefault(dir, false) || isSideBlockedByBlank(dir)) {
                if (connections.get(dir) != PipeConnectionType.NONE) {
                    connections.put(dir, PipeConnectionType.NONE);
                    changed = true;
                }
                continue;
            }

            BlockPos targetPos = worldPosition.relative(dir);
            if (!level.isLoaded(targetPos)) {
                // If neighbor chunk is not loaded yet across restart/chunk-load, preserve existing connection!
                continue;
            }

            BlockState targetState = level.getBlockState(targetPos);
            // If the neighbor block is expected to have a block entity but it hasn't instantiated yet during chunk load, preserve existing connection!
            if (targetState.hasBlockEntity() && level.getBlockEntity(targetPos) == null) {
                continue;
            }

            BlockEntity targetBe = level.getBlockEntity(targetPos);
            Direction opposite = dir.getOpposite();

            PipeConnectionType detected = PipeConnectionType.NONE;

            if (targetBe instanceof IndustrialPipeBlockEntity otherPipe) {
                // Pipe to Pipe connection
                boolean typesCompatible = (this.pipeType.handlesFluid() && otherPipe.getPipeType().handlesFluid()) ||
                                          (this.pipeType.handlesItems() && otherPipe.getPipeType().handlesItems()) ||
                                          this.pipeType == IndustrialPipeType.VOID || otherPipe.getPipeType() == IndustrialPipeType.VOID;
                if (typesCompatible) {
                    detected = PipeConnectionType.PIPE;
                }
            } else {
                if (this.pipeType.handlesFluid() && isFluidMachine(level, targetPos, opposite)) {
                    detected = PipeConnectionType.MACHINE;
                }
                if (detected == PipeConnectionType.NONE && this.pipeType.handlesItems() && isItemMachine(level, targetPos, opposite)) {
                    detected = PipeConnectionType.MACHINE;
                }
            }

            if (connections.get(dir) != detected) {
                connections.put(dir, detected);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IndustrialPipeBlockEntity be) {
        if (level.isClientSide()) return;

        // Periodic connection refresh every 20 ticks (evenly distributed across all 20 game ticks)
        if (level.getGameTime() % 20L == Math.floorMod(pos.asLong(), 20L)) {
            be.updateConnections();
        }

        // 1. VOID PIPE BEHAVIOR
        if (be.pipeType == IndustrialPipeType.VOID) {
            be.fluidTank.dump();
            be.internalItem = ItemStack.EMPTY;
            be.travelingItems.clear();
            return;
        }

        // 2. FLUID TRANSFER & EXTRACTION
        if (be.pipeType.handlesFluid()) {
            be.tickFluid(level, pos);
        }

        // 3. ITEM TRANSFER & EXTRACTION
        if (be.pipeType.handlesItems()) {
            be.tickItems(level, pos);
        }
    }

    private void tickFluid(Level level, BlockPos pos) {
        // Active extraction
        if (isExtractor()) {
            int extractRate = material.getTransferRate();
            for (Map.Entry<Direction, PipeConnectionType> entry : connections.entrySet()) {
                if (entry.getValue() == PipeConnectionType.MACHINE) {
                    Direction dir = entry.getKey();
                    BlockPos targetPos = pos.relative(dir);
                    ResourceHandler<FluidResource> targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
                    if (targetHandler == null) {
                        targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, null);
                    }
                    if (targetHandler != null) {
                        for (int i = 0; i < targetHandler.size(); i++) {
                            FluidResource res = targetHandler.getResource(i);
                            if (!res.isEmpty()) {
                                try (Transaction tx = Transaction.openRoot()) {
                                    int extracted = targetHandler.extract(i, res, extractRate, tx);
                                    if (extracted > 0) {
                                        int accepted = fluidTank.insert(0, res, extracted, tx);
                                        if (accepted > 0) {
                                            tx.commit();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 0. QUARTZ / MERGE FLUID PIPE: Only pushes to the selected output direction! All other connections are inputs.
        if (this.material == IndustrialPipeMaterial.QUARTZ) {
            Direction out = this.mergeOutput;
            if (out != null && fluidTank.getAmount() > 0 && fluidTank.getStoredFluid() != Fluids.EMPTY) {
                Fluid stored = fluidTank.getStoredFluid();
                FluidResource fluidRes = FluidResource.of(stored);
                int toTransfer = Math.min(fluidTank.getAmount(), material.getTransferRate());
                BlockPos targetPos = pos.relative(out);

                if (connections.get(out) == PipeConnectionType.MACHINE) {
                    ResourceHandler<FluidResource> targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, out.getOpposite());
                    if (targetHandler == null) {
                        targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, null);
                    }
                    if (targetHandler != null) {
                        try (Transaction tx = Transaction.openRoot()) {
                            int filled = targetHandler.insert(fluidRes, toTransfer, tx);
                            if (filled > 0) {
                                fluidTank.extract(0, fluidRes, filled, tx);
                                tx.commit();
                            }
                        }
                    }
                } else if (connections.get(out) == PipeConnectionType.PIPE) {
                    BlockEntity targetBe = level.getBlockEntity(targetPos);
                    if (targetBe instanceof IndustrialPipeBlockEntity otherPipe && otherPipe.pipeType.handlesFluid()) {
                        if (otherPipe.pipeType == IndustrialPipeType.VOID) {
                            try (Transaction tx = Transaction.openRoot()) {
                                fluidTank.extract(0, fluidRes, toTransfer, tx);
                                tx.commit();
                            }
                        } else {
                            int space = otherPipe.fluidTank.getCapacity() - otherPipe.fluidTank.getAmount();
                            int pushAmt = Math.min(toTransfer, space);
                            if (pushAmt > 0) {
                                try (Transaction tx = Transaction.openRoot()) {
                                    int accepted = otherPipe.fluidTank.insert(0, fluidRes, pushAmt, tx);
                                    if (accepted > 0) {
                                        this.fluidTank.extract(0, fluidRes, accepted, tx);
                                        tx.commit();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return;
        }

        // Passive balancing and pushing fluid downstream
        if (fluidTank.getAmount() > 0 && fluidTank.getStoredFluid() != Fluids.EMPTY) {
            Fluid stored = fluidTank.getStoredFluid();
            FluidResource fluidRes = FluidResource.of(stored);
            int toTransfer = Math.min(fluidTank.getAmount(), material.getTransferRate());

            // First: Push into connected machines (tanks, boilers, etc.)
            for (Map.Entry<Direction, PipeConnectionType> entry : connections.entrySet()) {
                if (entry.getValue() == PipeConnectionType.MACHINE && !isExtractor()) {
                    Direction dir = entry.getKey();
                    BlockPos targetPos = pos.relative(dir);
                    ResourceHandler<FluidResource> targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
                    if (targetHandler == null) {
                        targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, null);
                    }
                    if (targetHandler != null) {
                        try (Transaction tx = Transaction.openRoot()) {
                            int filled = targetHandler.insert(fluidRes, toTransfer, tx);
                            if (filled > 0) {
                                fluidTank.extract(0, fluidRes, filled, tx);
                                tx.commit();
                                return;
                            }
                        }
                    }
                }
            }

            // Second: Equalize / push into connected pipes
            for (Map.Entry<Direction, PipeConnectionType> entry : connections.entrySet()) {
                if (entry.getValue() == PipeConnectionType.PIPE) {
                    Direction dir = entry.getKey();
                    BlockPos targetPos = pos.relative(dir);
                    BlockEntity targetBe = level.getBlockEntity(targetPos);
                    if (targetBe instanceof IndustrialPipeBlockEntity otherPipe && otherPipe.pipeType.handlesFluid()) {
                        if (otherPipe.pipeType == IndustrialPipeType.VOID) {
                            try (Transaction tx = Transaction.openRoot()) {
                                fluidTank.extract(0, fluidRes, toTransfer, tx);
                                tx.commit();
                            }
                            return;
                        }
                        int diff = this.fluidTank.getAmount() - otherPipe.fluidTank.getAmount();
                        if (diff > 1) {
                            int toEqualize = (diff + 1) / 2;
                            int pushAmt = Math.min(toEqualize, material.getTransferRate());
                            try (Transaction tx = Transaction.openRoot()) {
                                int accepted = otherPipe.fluidTank.insert(0, fluidRes, pushAmt, tx);
                                if (accepted > 0) {
                                    this.fluidTank.extract(0, fluidRes, accepted, tx);
                                    tx.commit();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void tickItems(Level level, BlockPos pos) {
        float speed = 0.08f * material.getSpeedMultiplier();

        // 1. Move existing traveling items
        for (int i = travelingItems.size() - 1; i >= 0; i--) {
            TravelingItem item = travelingItems.get(i);
            item.progress += speed;

            if (item.progress >= 1.0f) {
                // Item has reached the end of this pipe block!
                Direction targetDir = item.toDir;
                BlockPos nextPos = pos.relative(targetDir);
                BlockEntity nextBe = level.getBlockEntity(nextPos);

                boolean dispatched = false;
                if (nextBe instanceof IndustrialPipeBlockEntity nextPipe && nextPipe.pipeType.handlesItems()) {
                    if (nextPipe.pipeType == IndustrialPipeType.VOID) {
                        dispatched = true;
                    } else {
                        // Forward into next pipe!
                        Direction continueDir = chooseNextItemDirection(nextPipe, targetDir.getOpposite(), item.stack);
                        nextPipe.travelingItems.add(new TravelingItem(item.stack, targetDir.getOpposite(), continueDir, 0.0f));
                        nextPipe.setChanged();
                        if (!nextPipe.getLevel().isClientSide()) {
                            nextPipe.getLevel().sendBlockUpdated(nextPos, nextPipe.getBlockState(), nextPipe.getBlockState(), 3);
                        }
                        dispatched = true;
                    }
                } else {
                    // Try inserting into machine inventory via NeoForge Item capability (with side, null, and Container fallbacks)
                    ResourceHandler<ItemResource> destHandler = level.getCapability(Capabilities.Item.BLOCK, nextPos, targetDir.getOpposite());
                    if (destHandler == null) {
                        destHandler = level.getCapability(Capabilities.Item.BLOCK, nextPos, null);
                    }
                    if (destHandler == null && nextBe instanceof Container container && !(nextBe instanceof IndustrialPipeBlockEntity)) {
                        if (container instanceof WorldlyContainer worldly) {
                            destHandler = new WorldlyContainerWrapper(worldly, targetDir.getOpposite());
                        } else {
                            destHandler = VanillaContainerWrapper.of(container);
                        }
                    }
                    if (destHandler != null) {
                        ItemResource res = ItemResource.of(item.stack);
                        int count = item.stack.getCount();
                        try (Transaction tx = Transaction.openRoot()) {
                            int accepted = destHandler.insert(res, count, tx);
                            if (accepted > 0) {
                                tx.commit();
                                if (accepted >= count) {
                                    dispatched = true;
                                } else {
                                    item.stack.shrink(accepted);
                                    // Reverse motion or wait
                                    item.toDir = item.fromDir;
                                    item.progress = 0.5f;
                                }
                            }
                        }
                    }
                }

                if (dispatched) {
                    travelingItems.remove(i);
                    setChanged();
                    if (!level.isClientSide()) {
                        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
                    }
                }
            }
        }

        // 2. Active Extraction from machines
        if (isExtractor() && travelingItems.size() < 4) {
            for (Map.Entry<Direction, PipeConnectionType> entry : connections.entrySet()) {
                if (entry.getValue() == PipeConnectionType.MACHINE) {
                    Direction dir = entry.getKey();
                    BlockPos targetPos = pos.relative(dir);
                    ResourceHandler<ItemResource> sourceInv = level.getCapability(Capabilities.Item.BLOCK, targetPos, dir.getOpposite());
                    if (sourceInv == null) {
                        sourceInv = level.getCapability(Capabilities.Item.BLOCK, targetPos, null);
                    }
                    if (sourceInv == null) {
                        BlockEntity tBe = level.getBlockEntity(targetPos);
                        if (tBe instanceof Container container && !(tBe instanceof IndustrialPipeBlockEntity)) {
                            if (container instanceof WorldlyContainer worldly) {
                                sourceInv = new WorldlyContainerWrapper(worldly, dir.getOpposite());
                            } else {
                                sourceInv = VanillaContainerWrapper.of(container);
                            }
                        }
                    }
                    if (sourceInv != null) {
                        for (int s = 0; s < sourceInv.size(); s++) {
                            ItemResource res = sourceInv.getResource(s);
                            if (!res.isEmpty()) {
                                try (Transaction tx = Transaction.openRoot()) {
                                    int pulled = sourceInv.extract(s, res, 1, tx);
                                    if (pulled > 0) {
                                        tx.commit();
                                        ItemStack extracted = res.toStack(pulled);
                                        Direction exitDir = chooseNextItemDirection(this, dir, extracted);
                                        travelingItems.add(new TravelingItem(extracted, dir, exitDir, 0.05f));
                                        setChanged();
                                        if (!level.isClientSide()) {
                                            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Direction chooseNextItemDirection(IndustrialPipeBlockEntity pipe, Direction fromSide, ItemStack stack) {
        // 0. QUARTZ / MERGE PIPE: Routes all incoming items strictly into the selected output direction
        if (pipe.getMaterial() == IndustrialPipeMaterial.QUARTZ) {
            Direction out = pipe.getMergeOutput();
            if (out != null && out != fromSide) {
                return out;
            }
        }

        // 1. DIAMOND SORT PIPE: Routes according to configured per-direction filters
        if (pipe.getMaterial() == IndustrialPipeMaterial.DIAMOND) {
            Direction sorted = pipe.getDiamondFilteredDirection(fromSide, stack);
            if (sorted != null) return sorted;
        }

        // 2. GOLD INSERTION PIPE: Prioritizes inserting into attached machine/container until full
        if (pipe.getMaterial() == IndustrialPipeMaterial.GOLD) {
            Direction insert = pipe.getGoldInsertionDirection(fromSide, stack);
            if (insert != null) return insert;
        }

        // 3. EMERALD ROUND-ROBIN PIPE: Cycles evenly across all connected output branches
        if (pipe.getMaterial() == IndustrialPipeMaterial.EMERALD) {
            return pipe.getEmeraldRoundRobinDirection(fromSide);
        }

        // 4. Default: Forward priority if straight path is connected, else first available
        Direction straight = fromSide.getOpposite();
        if (pipe.getConnection(straight).isConnected()) {
            return straight;
        }

        for (Direction d : Direction.values()) {
            if (d != fromSide && pipe.getConnection(d).isConnected()) {
                return d;
            }
        }
        return fromSide.getOpposite();
    }

    public @Nullable Direction getDiamondFilteredDirection(Direction fromSide, ItemStack stack) {
        if (stack.isEmpty()) return null;

        List<Direction> matchingDirs = new ArrayList<>();
        List<Direction> unfilteredDirs = new ArrayList<>();

        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];
            if (dir == fromSide || !getConnection(dir).isConnected()) continue;

            boolean hasAnyFilter = false;
            boolean matched = false;
            for (int slot = 0; slot < 9; slot++) {
                ItemStack filter = diamondFilters.getItem(i * 9 + slot);
                if (!filter.isEmpty()) {
                    hasAnyFilter = true;
                    if (ItemStack.isSameItemSameComponents(filter, stack)) {
                        matched = true;
                        break;
                    }
                }
            }

            if (matched) {
                matchingDirs.add(dir);
            } else if (!hasAnyFilter) {
                unfilteredDirs.add(dir);
            }
        }

        if (!matchingDirs.isEmpty()) {
            Direction chosen = matchingDirs.get(Math.floorMod(roundRobinIndex++, matchingDirs.size()));
            setChanged();
            return chosen;
        }
        if (!unfilteredDirs.isEmpty()) {
            Direction chosen = unfilteredDirs.get(Math.floorMod(roundRobinIndex++, unfilteredDirs.size()));
            setChanged();
            return chosen;
        }
        return null;
    }

    public @Nullable Direction getGoldInsertionDirection(Direction fromSide, ItemStack stack) {
        if (stack.isEmpty() || level == null) return null;

        // Prioritize adjacent machine inventories until full
        for (Direction dir : Direction.values()) {
            if (dir == fromSide || !getConnection(dir).isConnected()) continue;
            if (getConnection(dir) != PipeConnectionType.MACHINE) continue;

            BlockPos targetPos = worldPosition.relative(dir);
            BlockEntity targetBe = level.getBlockEntity(targetPos);
            if (targetBe instanceof IndustrialPipeBlockEntity) continue;

            ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, dir.getOpposite());
            if (handler == null) {
                handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, null);
            }
            if (handler != null) {
                try (Transaction tx = Transaction.openRoot()) {
                    int accepted = handler.insert(ItemResource.of(stack), stack.getCount(), tx);
                    if (accepted > 0) {
                        return dir; // Priority insertion until machine is full!
                    }
                }
            } else if (targetBe instanceof Container c) {
                for (int slot = 0; slot < c.getContainerSize(); slot++) {
                    if (c.canPlaceItem(slot, stack)) {
                        ItemStack inSlot = c.getItem(slot);
                        if (inSlot.isEmpty() || (ItemStack.isSameItemSameComponents(inSlot, stack) && inSlot.getCount() < Math.min(inSlot.getMaxStackSize(), c.getMaxStackSize(stack)))) {
                            return dir;
                        }
                    }
                }
            }
        }

        // If no machine has space, fall back to passing items down the line to a connected pipe!
        Direction straight = fromSide.getOpposite();
        if (getConnection(straight) == PipeConnectionType.PIPE) {
            return straight;
        }
        for (Direction d : Direction.values()) {
            if (d != fromSide && getConnection(d) == PipeConnectionType.PIPE) {
                return d;
            }
        }
        return null;
    }

    public Direction getEmeraldRoundRobinDirection(Direction fromSide) {
        List<Direction> validDirs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d != fromSide && getConnection(d).isConnected()) {
                validDirs.add(d);
            }
        }
        if (validDirs.isEmpty()) {
            return fromSide.getOpposite();
        }
        Direction chosen = validDirs.get(Math.floorMod(roundRobinIndex++, validDirs.size()));
        setChanged();
        return chosen;
    }

    // === Container implementation for insertion from hoppers / pipes / machines ===

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return internalItem.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (pipeType == IndustrialPipeType.VOID) return ItemStack.EMPTY;
        return internalItem;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (pipeType == IndustrialPipeType.VOID || internalItem.isEmpty()) return ItemStack.EMPTY;
        ItemStack split = internalItem.split(amount);
        setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack copy = internalItem;
        internalItem = ItemStack.EMPTY;
        return copy;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (pipeType == IndustrialPipeType.VOID) {
            internalItem = ItemStack.EMPTY;
            return;
        }

        if (!stack.isEmpty() && pipeType.handlesItems()) {
            Direction from = Direction.UP;
            Direction to = chooseNextItemDirection(this, from, stack);
            travelingItems.add(new TravelingItem(stack.copy(), from, to, 0.05f));
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        internalItem = ItemStack.EMPTY;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null && !level.isClientSide() && getMaterial() == IndustrialPipeMaterial.DIAMOND) {
            Containers.dropContents(level, pos, this.diamondFilters);
        }
    }

    // === Persistence & Sync ===

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("PipeType", getPipeType().name());
        output.putString("Material", getMaterial().name());
        output.putInt("FluidAmount", fluidTank.getAmount());
        output.putInt("RoundRobinIndex", roundRobinIndex);
        output.putInt("MergeOutput", mergeOutput.get3DDataValue());

        if (getMaterial() == IndustrialPipeMaterial.DIAMOND) {
            NonNullList<ItemStack> items = this.diamondFilters.getItems();
            var filterList = output.list("DiamondFilters", ItemStackWithSlot.CODEC);
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    filterList.add(new ItemStackWithSlot(i, stack));
                }
            }
            ContainerHelper.saveAllItems(output, items);
        }

        Fluid fl = fluidTank.getStoredFluid();
        if (fl != Fluids.EMPTY) {
            Identifier id = BuiltInRegistries.FLUID.getKey(fl);
            if (id != null) {
                output.putString("Fluid", id.toString());
            }
        }

        for (Direction dir : Direction.values()) {
            output.putString("Conn_" + dir.getName(), connections.getOrDefault(dir, PipeConnectionType.NONE).name());
            output.putBoolean("Manual_" + dir.getName(), manualDisconnections.getOrDefault(dir, false));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int amt = input.getIntOr("FluidAmount", 0);
        this.roundRobinIndex = input.getIntOr("RoundRobinIndex", 0);
        this.mergeOutput = Direction.from3DDataValue(input.getIntOr("MergeOutput", Direction.NORTH.get3DDataValue()));

        if (getMaterial() == IndustrialPipeMaterial.DIAMOND) {
            NonNullList<ItemStack> items = this.diamondFilters.getItems();
            while (items.size() < 54) {
                items.add(ItemStack.EMPTY);
            }
            for (int i = 0; i < items.size(); i++) {
                items.set(i, ItemStack.EMPTY);
            }

            var filterList = input.listOrEmpty("DiamondFilters", ItemStackWithSlot.CODEC);
            boolean loaded = false;
            for (ItemStackWithSlot entry : filterList) {
                if (entry.isValidInContainer(items.size())) {
                    items.set(entry.slot(), entry.stack());
                    loaded = true;
                }
            }
            if (!loaded) {
                ContainerHelper.loadAllItems(input, items);
            }
        }

        String flId = input.getStringOr("Fluid", "");
        if (!flId.isEmpty()) {
            Fluid fl = BuiltInRegistries.FLUID.get(Identifier.tryParse(flId)).map(net.minecraft.core.Holder::value).orElse(Fluids.EMPTY);
            fluidTank.setStoredFluid(fl);
            fluidTank.setAmount(Math.min(amt, getMaterial().getFluidCapacity()));
        } else {
            fluidTank.setStoredFluid(Fluids.EMPTY);
        }

        for (Direction dir : Direction.values()) {
            String cName = input.getStringOr("Conn_" + dir.getName(), PipeConnectionType.NONE.name());
            try {
                connections.put(dir, PipeConnectionType.valueOf(cName));
            } catch (Exception e) {
                connections.put(dir, PipeConnectionType.NONE);
            }
            manualDisconnections.put(dir, input.getBooleanOr("Manual_" + dir.getName(), false));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
