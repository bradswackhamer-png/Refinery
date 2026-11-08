package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CastingPressControllerBlock;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.CastingPressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CastingPressControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 4; // 4 output slots
    public static final int TANK_CAPACITY = 4000;
    public static final int ENERGY_CAPACITY = 500_000;
    public static final int ENERGY_PER_TICK = 600;
    public static final int PROCESS_TIME = 240; // 12 seconds per press stroke (turned down by 200%)

    private boolean assembled = false;
    private int energy = 0;
    private int progress = 0;
    private int blueprintTicks = 0;
    private int exportCooldown = 0;

    private Fluid moltenFluid = Fluids.EMPTY;
    private int moltenAmount = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 20_000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            CastingPressControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int maxReceive, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(maxReceive, transaction);
            CastingPressControllerBlockEntity.this.energy = this.energy;
            setChanged();
            return inserted;
        }
    };

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> energy & 0xFFFF;
                case 3 -> (energy >> 16) & 0xFFFF;
                case 4 -> assembled ? 1 : 0;
                case 5 -> moltenAmount;
                case 6 -> TANK_CAPACITY;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> energy = (energy & 0xFFFF0000) | (value & 0xFFFF);
                case 3 -> energy = (energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 4 -> assembled = (value == 1);
                case 5 -> moltenAmount = value;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public CastingPressControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.CASTING_PRESS_CONTROLLER_BE.get(), pos, blockState);
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

    public Fluid getMoltenFluid() {
        return this.moltenFluid;
    }

    public void setMoltenFluid(Fluid fluid) {
        this.moltenFluid = fluid;
        setChanged();
        syncToClient();
    }

    public int getMoltenAmount() {
        return this.moltenAmount;
    }

    public void setMoltenAmount(int amount) {
        this.moltenAmount = Math.max(0, Math.min(TANK_CAPACITY, amount));
        if (this.moltenAmount == 0) {
            this.moltenFluid = Fluids.EMPTY;
        }
        setChanged();
        syncToClient();
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
            if (state.getBlock() instanceof CastingPressControllerBlock) {
                this.level.setBlock(this.worldPosition, state.setValue(CastingPressControllerBlock.ASSEMBLED, false)
                        .setValue(CastingPressControllerBlock.ACTIVE, false)
                        .setValue(CastingPressControllerBlock.BLUEPRINT, false), 3);
            }
        }
        setChanged();
        syncToClient();
    }

    public void updateMemberBlockStates(boolean isAssembled) {
        if (this.level == null || this.level.isClientSide()) return;
        Direction facing = (getBlockState().getBlock() instanceof CastingPressControllerBlock && getBlockState().hasProperty(CastingPressControllerBlock.FACING))
                ? getBlockState().getValue(CastingPressControllerBlock.FACING)
                : Direction.NORTH;

        for (BlueprintRequirement req : getRequirements()) {
            if (req.x == 0 && req.y == 0 && req.z == 0) continue;
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.hasProperty(CastingPressControllerBlock.ASSEMBLED)) {
                if (targetState.getValue(CastingPressControllerBlock.ASSEMBLED) != isAssembled) {
                    this.level.setBlock(targetPos, targetState.setValue(CastingPressControllerBlock.ASSEMBLED, isAssembled), 3);
                }
            }
        }
    }

    public NonNullList<ItemStack> getDrops() {
        return this.items;
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) this.level;
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();

        if (this.blueprintTicks > 0) {
            this.blueprintTicks--;
            if (this.blueprintTicks == 0 && state.hasProperty(CastingPressControllerBlock.BLUEPRINT) && state.getValue(CastingPressControllerBlock.BLUEPRINT)) {
                level.setBlock(pos, state.setValue(CastingPressControllerBlock.BLUEPRINT, false), 3);
            }
        }

        if (serverLevel.getGameTime() % 20 == 0) {
            syncAssembly();
        }

        if (!this.assembled) {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
            BlockState current = getBlockState();
            if (current.hasProperty(CastingPressControllerBlock.ACTIVE) && current.getValue(CastingPressControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(CastingPressControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(CastingPressControllerBlock.ACTIVE);
        boolean isActive = false;

        // Auto-export finished cast ingots/blocks from Output Port to adjacent conveyor, pipe, or container
        if (--this.exportCooldown <= 0) {
            this.exportCooldown = 5;
            tryExportOutput(serverLevel);
        }

        // Check if we have at least 1000 mB of molten fluid and room in output
        ItemStack resultStack = getCastingProduct(this.moltenFluid);
        boolean canCast = (this.progress > 120) || (!resultStack.isEmpty() && this.moltenAmount >= 1000 && canFitOutput(resultStack));

        if (canCast) {
            int requiredEnergy = (PROCESS_TIME - this.progress) * ENERGY_PER_TICK;
            if (this.energy >= requiredEnergy) {
                isActive = true;
                this.energy -= ENERGY_PER_TICK;
                this.energyStorage.set(this.energy);
                this.progress++;

                Direction facing = state.getValue(CastingPressControllerBlock.FACING);
                BlockPos basinPos = getRelativePos(0, 0, 1, facing);

                // Stamping impact at tick 120 (bottom of hydraulic stroke)
                if (this.progress == 120) {
                    this.moltenAmount -= 1000;
                    if (this.moltenAmount <= 0) {
                        this.moltenFluid = Fluids.EMPTY;
                    }
                    if (!resultStack.isEmpty()) {
                        addOutput(resultStack.copy());
                    }

                    // Quench sounds & steam burst
                    serverLevel.playSound(null, basinPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.8f, 0.9f);
                    serverLevel.playSound(null, basinPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.7f, 1.2f);

                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            basinPos.getX() + 0.5, basinPos.getY() + 0.5, basinPos.getZ() + 0.5,
                            8, 0.2, 0.1, 0.2, 0.05);
                    serverLevel.sendParticles(ParticleTypes.POOF,
                            basinPos.getX() + 0.5, basinPos.getY() + 0.4, basinPos.getZ() + 0.5,
                            4, 0.15, 0.1, 0.15, 0.03);
                }

                if (this.progress >= PROCESS_TIME) {
                    this.progress = 0;
                }
                setChanged();
                if (this.progress == 1 || this.progress % 6 == 0 || this.progress == 120 || this.progress == 0) {
                    syncToClient();
                }
            }
        } else {
            if (this.progress > 0 && this.progress <= 120) {
                this.progress = 0;
                setChanged();
                syncToClient();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.hasProperty(CastingPressControllerBlock.ACTIVE)) {
                level.setBlock(pos, latest.setValue(CastingPressControllerBlock.ACTIVE, isActive), 3);
            }
            syncToClient();
        }
    }

    private void tryExportOutput(ServerLevel level) {
        Direction facing = getBlockState().getValue(CastingPressControllerBlock.FACING);
        Direction right = facing.getClockWise();

        boolean hasOutput = false;
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            if (!this.items.get(slot).isEmpty()) {
                hasOutput = true;
                break;
            }
        }
        if (!hasOutput) return;

        Set<BlockPos> targetPositions = new LinkedHashSet<>();

        // 1. All 6 sides around any CASTING_OUTPUT_PORT in the structure
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos partPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState partState = level.getBlockState(partPos);
            if (partState.is(RefineryMod.CASTING_OUTPUT_PORT.get())) {
                for (Direction dir : Direction.values()) {
                    targetPositions.add(partPos.relative(dir));
                }
            }
        }

        // 2. Default output port (1, 0, 0) adjacent positions
        BlockPos portPos = getRelativePos(1, 0, 0, facing);
        targetPositions.add(portPos.relative(facing));
        targetPositions.add(portPos.relative(right));
        targetPositions.add(portPos.below());

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

                net.minecraft.world.phys.AABB beltBox = new net.minecraft.world.phys.AABB(
                        targetPos.getX(), targetPos.getY() + 0.10, targetPos.getZ(),
                        targetPos.getX() + 1.0, targetPos.getY() + 0.70, targetPos.getZ() + 1.0
                );
                if (!level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, beltBox).isEmpty()) {
                    continue;
                }

                for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack single = sourceStack.copyWithCount(1);
                    if (targetBE instanceof Container container) {
                        container.setItem(0, single);
                        sourceStack.shrink(1);
                        if (sourceStack.isEmpty()) {
                            this.items.set(slot, ItemStack.EMPTY);
                        }
                        setChanged();
                        syncToClient();
                        return;
                    }
                }
                continue;
            }

            if (targetBE instanceof IndustrialPipeBlockEntity pipeBE && pipeBE.getPipeType() == IndustrialPipeType.ITEM) {
                for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(pipeBE, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        setChanged();
                        pipeBE.setChanged();
                        syncToClient();
                        return;
                    }
                }
                continue;
            }

            if (targetBE instanceof Container targetContainer) {
                for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(targetContainer, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        setChanged();
                        targetContainer.setChanged();
                        syncToClient();
                        return;
                    }
                }
            } else {
                var handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, Direction.UP);
                if (handler != null) {
                    for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                        ItemStack sourceStack = this.items.get(slot);
                        if (sourceStack.isEmpty()) continue;

                        try (Transaction tx = Transaction.openRoot()) {
                            int inserted = handler.insert(ItemResource.of(sourceStack), sourceStack.getCount(), tx);
                            if (inserted > 0) {
                                tx.commit();
                                sourceStack.shrink(inserted);
                                if (sourceStack.isEmpty()) {
                                    this.items.set(slot, ItemStack.EMPTY);
                                }
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

    private boolean isMultiblockPart(BlockPos pos) {
        BlockState state = getBlockState();
        Direction facing = (state.getBlock() instanceof CastingPressControllerBlock && state.hasProperty(CastingPressControllerBlock.FACING))
                ? state.getValue(CastingPressControllerBlock.FACING)
                : Direction.NORTH;
        for (BlueprintRequirement req : getRequirements()) {
            if (getRelativePos(req.x, req.y, req.z, facing).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getCastingProduct(Fluid fluid) {
        if (fluid == RefineryFluids.MOLTEN_IRON_SOURCE.get()) return new ItemStack(Items.IRON_INGOT, 2);
        if (fluid == RefineryFluids.MOLTEN_GOLD_SOURCE.get()) return new ItemStack(Items.GOLD_INGOT, 2);
        if (fluid == RefineryFluids.MOLTEN_COPPER_SOURCE.get()) return new ItemStack(Items.COPPER_INGOT, 2);
        if (fluid == RefineryFluids.MOLTEN_TIN_SOURCE.get()) return new ItemStack(RefineryMod.TIN_INGOT.get(), 2);
        if (fluid == RefineryFluids.MOLTEN_BRONZE_SOURCE.get()) return new ItemStack(RefineryMod.BRONZE_INGOT.get(), 2);
        if (fluid == RefineryFluids.MOLTEN_GLASS_SOURCE.get()) return new ItemStack(Blocks.GLASS, 2);
        return ItemStack.EMPTY;
    }

    private boolean canFitOutput(ItemStack result) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack existing = this.items.get(i);
            if (existing.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(existing, result) && existing.getCount() + result.getCount() <= existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void addOutput(ItemStack result) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack existing = this.items.get(i);
            if (existing.isEmpty()) {
                this.items.set(i, result);
                return;
            }
            if (ItemStack.isSameItemSameComponents(existing, result) && existing.getCount() + result.getCount() <= existing.getMaxStackSize()) {
                existing.grow(result.getCount());
                return;
            }
        }
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        if (this.level == null) return;
        boolean formed = checkStructure(true, player);
        if (formed) {
            this.assembled = true;
            updateMemberBlockStates(true);
            this.blueprintTicks = 0;
            if (!this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof CastingPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(CastingPressControllerBlock.ASSEMBLED, true)
                            .setValue(CastingPressControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Continuous Casting Press]§r 3x3x3 Machine is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            updateMemberBlockStates(false);
            this.blueprintTicks = 600;
            if (!this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof CastingPressControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(CastingPressControllerBlock.ASSEMBLED, false)
                            .setValue(CastingPressControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Continuous Casting Press]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
        syncToClient();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        Block controller = RefineryMod.CASTING_PRESS_CONTROLLER.get();
        Block inputPort = RefineryMod.CASTING_INPUT_PORT.get();
        Block outputPort = RefineryMod.CASTING_OUTPUT_PORT.get();
        Block chamber = RefineryMod.CASTING_CHAMBER.get();
        Block basin = RefineryMod.CASTING_BASIN.get();
        Block casing = RefineryMod.CASTING_CASING.get();

        // Layer 0: Foundation & Basin Bed
        list.add(new BlueprintRequirement(-1, 0, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Casting Press Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, outputPort, "Casting Discharge Port (Output)", 0xFF38BDF8));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, basin, "Refractory Casting Basin", 0xFFF59E0B));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 2, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 0, 2, casing, "Casting Structural Casing", 0xFF64748B));

        // Layer 1: Stamping Stroke & Fluid Inflow
        list.add(new BlueprintRequirement(-1, 1, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 0, chamber, "Casting Safety Grille Window", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 1, 1, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 1, chamber, "Stamping Die Stroke (or Hollow)", 0xFFEA580C));
        list.add(new BlueprintRequirement( 1, 1, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 2, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 2, inputPort, "Casting Fluid Inflow Port (Input)", 0xFFF97316));
        list.add(new BlueprintRequirement( 1, 1, 2, casing, "Casting Structural Casing", 0xFF64748B));

        // Layer 2: Overhead Hydraulic Cylinder Housing
        list.add(new BlueprintRequirement(-1, 2, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 0, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 1, casing, "Hydraulic Cylinder Head", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 1, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 2, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 2, casing, "Casting Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 2, casing, "Casting Structural Casing", 0xFF64748B));

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
        Direction facing = getBlockState().getValue(CastingPressControllerBlock.FACING);
        List<BlueprintRequirement> reqs = getRequirements();
        Block chamberBlock = RefineryMod.CASTING_CHAMBER.get();

        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState state = this.level.getBlockState(targetPos);

            if (req.x == 0 && req.y == 1 && req.z == 1) {
                if (state.is(chamberBlock) || state.isAir()) {
                    matched++;
                } else {
                    if (missing.size() < 4) {
                        missing.add("Layer " + req.y + ": " + req.name + " at [" + targetPos.toShortString() + "]");
                    }
                }
            } else if (state.is(req.expectedBlock)) {
                matched++;
            } else {
                if (missing.size() < 4) {
                    missing.add("Layer " + req.y + ": " + req.name + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (matched == reqs.size()) {
            return true;
        }

        if (checkFlexibleStructure()) {
            return true;
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Casting Press Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing components:"));
                for (String m : missing) {
                    player.sendSystemMessage(Component.literal(" - §f" + m));
                }
            }
        }

        return false;
    }

    private boolean checkFlexibleStructure() {
        if (this.level == null) return false;
        Direction facing = getBlockState().getValue(CastingPressControllerBlock.FACING);
        Block controller = RefineryMod.CASTING_PRESS_CONTROLLER.get();
        Block inputPort = RefineryMod.CASTING_INPUT_PORT.get();
        Block outputPort = RefineryMod.CASTING_OUTPUT_PORT.get();
        Block chamber = RefineryMod.CASTING_CHAMBER.get();
        Block basin = RefineryMod.CASTING_BASIN.get();
        Block casing = RefineryMod.CASTING_CASING.get();

        int inPorts = 0;
        int outPorts = 0;
        int basins = 0;

        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -1; x <= 1; x++) {
                    BlockPos targetPos = getRelativePos(x, y, z, facing);
                    BlockState state = this.level.getBlockState(targetPos);

                    if (x == 0 && y == 0 && z == 0) {
                        if (!state.is(controller)) return false;
                        continue;
                    }
                    if (x == 0 && y == 1 && z == 1 && state.isAir()) {
                        continue;
                    }
                    if (state.is(inputPort)) inPorts++;
                    else if (state.is(outputPort)) outPorts++;
                    else if (state.is(basin)) basins++;
                    else if (state.is(chamber)) {}
                    else if (!state.is(casing)) return false;
                }
            }
        }

        return inPorts >= 1 && outPorts >= 1 && basins >= 1;
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean matches = checkStructure(false, null);
        BlockState current = getBlockState();
        if (!(current.getBlock() instanceof CastingPressControllerBlock)) return;

        updateMemberBlockStates(matches);

        boolean blockAssembled = current.getValue(CastingPressControllerBlock.ASSEMBLED);
        if (matches != this.assembled || blockAssembled != matches) {
            this.assembled = matches;
            BlockState newState = current.setValue(CastingPressControllerBlock.ASSEMBLED, matches);
            if (matches) {
                newState = newState.setValue(CastingPressControllerBlock.BLUEPRINT, false);
                this.blueprintTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.1f);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 1.0,
                            6, 0.4, 0.4, 0.4, 0.05);
                }
            }
            level.setBlock(getBlockPos(), newState, 3);
            setChanged();
            syncToClient();
        }
    }

    private void linkPorts() {
        if (this.level == null) return;
        Direction facing = getBlockState().getValue(CastingPressControllerBlock.FACING);
        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -1; x <= 1; x++) {
                    BlockPos targetPos = getRelativePos(x, y, z, facing);
                    if (this.level.getBlockEntity(targetPos) instanceof CastingPortBlockEntity portBE) {
                        portBE.setControllerPos(this.worldPosition);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("energy", this.energy);
        output.putInt("progress", this.progress);
        output.putInt("moltenAmount", this.moltenAmount);
        if (this.moltenFluid != Fluids.EMPTY) {
            output.putString("moltenFluid", BuiltInRegistries.FLUID.getKey(this.moltenFluid).toString());
        }
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.progress = input.getIntOr("progress", 0);
        this.moltenAmount = input.getIntOr("moltenAmount", 0);
        String fluidId = input.getStringOr("moltenFluid", "");
        if (!fluidId.isEmpty()) {
            Identifier id = Identifier.tryParse(fluidId);
            if (id != null) {
                this.moltenFluid = BuiltInRegistries.FLUID.get(id).map(Holder::value).orElse(Fluids.EMPTY);
            } else {
                this.moltenFluid = Fluids.EMPTY;
            }
        } else {
            this.moltenFluid = Fluids.EMPTY;
        }
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // === WorldlyContainer Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
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
        return false;
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
        return Component.translatable("container.refinery.casting_press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CastingPressMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
