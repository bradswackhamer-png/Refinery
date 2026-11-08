package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BlastFurnaceCasingBlock;
import com.refinery.block.BlastFurnaceControllerBlock;
import com.refinery.block.ConveyorBeltBlock;
import com.refinery.block.FoundryGlassBlock;
import com.refinery.block.FoundryPortBlock;
import com.refinery.block.RightAngleConveyorBeltBlock;
import com.refinery.block.SlopedConveyorBeltBlock;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.menu.BlastFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlastFurnaceControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 12;
    public static final int RECIPE_TICKS = 100; // 5 seconds per steel ingot

    // Slots layout:
    // Slots 0..3: Iron Input Buffer (Raw Iron, Iron Ore, Iron Ingot)
    // Slots 4..7: Fuel Input Buffer (Coal, Charcoal)
    // Slots 8..11: Finished Steel Ingot Output Buffer
    public static final int[] IRON_SLOTS = new int[]{0, 1, 2, 3};
    public static final int[] FUEL_SLOTS = new int[]{4, 5, 6, 7};
    public static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final int[] OUTPUT_SLOTS = new int[]{8, 9, 10, 11};

    private boolean assembled = false;
    private int progress = 0;
    private int ironUnits = 0;
    private int fuelUnits = 0;
    private int blueprintProjectionTicks = 0;
    private int structureCheckCooldown = 0;
    private int exportCooldown = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BlastFurnaceControllerBlockEntity.this.progress;
                case 1 -> RECIPE_TICKS;
                case 2 -> BlastFurnaceControllerBlockEntity.this.assembled ? 1 : 0;
                case 3 -> BlastFurnaceControllerBlockEntity.this.isSmelting() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BlastFurnaceControllerBlockEntity.this.progress = value;
                case 2 -> BlastFurnaceControllerBlockEntity.this.assembled = (value != 0);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public BlastFurnaceControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.BLAST_FURNACE_CONTROLLER_BE.get(), pos, state);
    }

    public boolean isAssembled() {
        return this.assembled;
    }

    public boolean isSmelting() {
        return this.assembled && this.progress > 0;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getBlueprintProjectionTicks() {
        return this.blueprintProjectionTicks;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        drops.addAll(this.items);
        if (this.ironUnits >= 9) {
            drops.add(new ItemStack(Items.IRON_BLOCK, this.ironUnits / 9));
            int rem = this.ironUnits % 9;
            if (rem > 0) drops.add(new ItemStack(Items.IRON_INGOT, rem));
        } else if (this.ironUnits > 0) {
            drops.add(new ItemStack(Items.IRON_INGOT, this.ironUnits));
        }

        if (this.fuelUnits >= 9) {
            drops.add(new ItemStack(Items.COAL_BLOCK, this.fuelUnits / 9));
            int rem = this.fuelUnits % 9;
            if (rem > 0) drops.add(new ItemStack(Items.COAL, rem));
        } else if (this.fuelUnits > 0) {
            drops.add(new ItemStack(Items.COAL, this.fuelUnits));
        }
        return drops;
    }

    public void disassemble() {
        if (this.assembled) {
            this.assembled = false;
            if (this.level != null && !this.level.isClientSide()) {
                BlockState state = this.getBlockState();
                if (state.getBlock() instanceof BlastFurnaceControllerBlock) {
                    this.level.setBlock(this.worldPosition, state.setValue(BlastFurnaceControllerBlock.ASSEMBLED, false)
                            .setValue(BlastFurnaceControllerBlock.ACTIVE, false)
                            .setValue(BlastFurnaceControllerBlock.BLUEPRINT, false), 3);
                }
            }
            setChanged();
        }
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
                if (current.getBlock() instanceof BlastFurnaceControllerBlock && current.getValue(BlastFurnaceControllerBlock.BLUEPRINT)) {
                    level.setBlock(pos, current.setValue(BlastFurnaceControllerBlock.BLUEPRINT, false), 3);
                }
            }
        }

        if (!this.assembled) {
            BlockState current = getBlockState();
            if (current.getBlock() instanceof BlastFurnaceControllerBlock && current.getValue(BlastFurnaceControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(BlastFurnaceControllerBlock.ACTIVE, false), 3);
            }
            this.progress = 0;
            return;
        }

        // Auto-export finished Steel Ingots from Output Port to adjacent container every 10 ticks
        if (--this.exportCooldown <= 0) {
            this.exportCooldown = 10;
            tryExportOutput(serverLevel);
        }

        BlockState current = getBlockState();
        boolean wasActive = current.getBlock() instanceof BlastFurnaceControllerBlock && current.getValue(BlastFurnaceControllerBlock.ACTIVE);
        boolean isActive = false;

        boolean canSmelt = hasIngredients() && canOutputSteel();

        if (canSmelt) {
            isActive = true;
            this.progress++;

            // Visual effects inside the 3x3x3 viewing chamber (seen through 3x3 glass!)
            Direction facing = current.getValue(BlastFurnaceControllerBlock.FACING);
            if (level.getRandom().nextInt(2) == 0) {
                // Interior center is 2 blocks in, 2 blocks up
                BlockPos interiorCenter = getRelativePos(0, 2, 2, facing);
                double px = interiorCenter.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 1.8;
                double py = interiorCenter.getY() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 1.5;
                double pz = interiorCenter.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 1.8;

                serverLevel.sendParticles(ParticleTypes.FLAME, px, py, pz, 2, 0.05, 0.05, 0.05, 0.01);
                serverLevel.sendParticles(ParticleTypes.SMOKE, px, py + 0.5, pz, 1, 0.05, 0.1, 0.05, 0.02);
                if (level.getRandom().nextInt(4) == 0) {
                    serverLevel.sendParticles(ParticleTypes.LAVA, px, py, pz, 1, 0.1, 0.1, 0.1, 0.02);
                }
            }

            if (this.progress >= RECIPE_TICKS) {
                this.progress = 0;
                consumeIngredients();
                insertOutputSteel(new ItemStack(RefineryMod.STEEL_INGOT.get(), 1));

                serverLevel.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7f, 1.1f);
                serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.4f);
                setChanged();
            }
        } else {
            if (this.progress > 0) {
                this.progress = Math.max(0, this.progress - 2);
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.getBlock() instanceof BlastFurnaceControllerBlock) {
                level.setBlock(pos, latest.setValue(BlastFurnaceControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    private boolean hasIngredients() {
        boolean hasIron = this.ironUnits > 0;
        if (!hasIron) {
            for (int slot : IRON_SLOTS) {
                ItemStack stack = this.items.get(slot);
                if (!stack.isEmpty() && isIronSource(stack)) {
                    hasIron = true;
                    break;
                }
            }
        }

        boolean hasFuel = this.fuelUnits > 0;
        if (!hasFuel) {
            for (int slot : FUEL_SLOTS) {
                ItemStack stack = this.items.get(slot);
                if (!stack.isEmpty() && isFuelSource(stack)) {
                    hasFuel = true;
                    break;
                }
            }
        }

        return hasIron && hasFuel;
    }

    private void consumeIngredients() {
        // Ensure we have an active iron unit
        if (this.ironUnits <= 0) {
            for (int slot : IRON_SLOTS) {
                ItemStack stack = this.items.get(slot);
                if (!stack.isEmpty() && isIronSource(stack)) {
                    if (stack.is(Items.IRON_BLOCK)) {
                        this.ironUnits += 9;
                    } else {
                        this.ironUnits += 1;
                    }
                    stack.shrink(1);
                    break;
                }
            }
        }
        if (this.ironUnits > 0) {
            this.ironUnits--;
        }

        // Ensure we have an active fuel unit
        if (this.fuelUnits <= 0) {
            for (int slot : FUEL_SLOTS) {
                ItemStack stack = this.items.get(slot);
                if (!stack.isEmpty() && isFuelSource(stack)) {
                    if (stack.is(Items.COAL_BLOCK)) {
                        this.fuelUnits += 9;
                    } else {
                        this.fuelUnits += 1;
                    }
                    stack.shrink(1);
                    break;
                }
            }
        }
        if (this.fuelUnits > 0) {
            this.fuelUnits--;
        }
    }

    private boolean canOutputSteel() {
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (stack.isEmpty() || (stack.is(RefineryMod.STEEL_INGOT.get()) && stack.getCount() < stack.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    private void insertOutputSteel(ItemStack steel) {
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (stack.isEmpty()) {
                this.items.set(slot, steel.copy());
                return;
            } else if (stack.is(steel.getItem()) && stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(steel.getCount());
                return;
            }
        }
    }

    public static boolean isIronSource(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(Items.IRON_INGOT) || stack.is(Items.IRON_BLOCK);
    }

    public static boolean isFuelSource(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(Items.COAL) || stack.is(Items.COAL_BLOCK);
    }

    public boolean isMultiblockPart(BlockPos pos) {
        BlockState state = getBlockState();
        Direction facing = (state.getBlock() instanceof BlastFurnaceControllerBlock && state.hasProperty(BlastFurnaceControllerBlock.FACING))
                ? state.getValue(BlastFurnaceControllerBlock.FACING)
                : Direction.NORTH;
        for (BlueprintRequirement req : getRequirements()) {
            if (getRelativePos(req.x, req.y, req.z, facing).equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private void tryExportOutput(ServerLevel level) {
        Direction facing = getBlockState().getValue(BlastFurnaceControllerBlock.FACING);
        Direction right = facing.getClockWise();

        // Check if there are any finished items to export from output buffer slots
        boolean hasOutput = false;
        for (int slot : OUTPUT_SLOTS) {
            if (!this.items.get(slot).isEmpty()) {
                hasOutput = true;
                break;
            }
        }
        if (!hasOutput) return;

        Set<BlockPos> targetPositions = new LinkedHashSet<>();

        // Output Port is located at relative (1, 0, 0)
        BlockPos portPos = getRelativePos(1, 0, 0, facing);
        targetPositions.add(portPos.relative(facing));        // In front of output port
        targetPositions.add(portPos.relative(right));         // To the right of output port
        targetPositions.add(portPos.below());                 // Below output port
        targetPositions.add(portPos.relative(facing).below());// Below in front of output port

        // Controller at (0, 0, 0)
        targetPositions.add(this.worldPosition.relative(facing));
        targetPositions.add(this.worldPosition.relative(facing).below());

        // Front face of 5x5 base (Layer 0, Z = -1, X = -2..2)
        for (int x = -2; x <= 2; x++) {
            BlockPos frontPos = getRelativePos(x, 0, -1, facing);
            targetPositions.add(frontPos);
            targetPositions.add(frontPos.below());
        }

        // Perimeter blocks around 5x5 base
        for (int x = -2; x <= 2; x++) {
            for (int z = 0; z <= 4; z++) {
                if (x == -2) targetPositions.add(getRelativePos(x - 1, 0, z, facing));
                if (x == 2)  targetPositions.add(getRelativePos(x + 1, 0, z, facing));
                if (z == 0)  targetPositions.add(getRelativePos(x, 0, z - 1, facing));
                if (z == 4)  targetPositions.add(getRelativePos(x, 0, z + 1, facing));
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

                for (int slot : OUTPUT_SLOTS) {
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
                    setChanged();
                    return;
                }
                continue;
            }

            // Direct pipe insertion
            if (targetBE instanceof IndustrialPipeBlockEntity pipeBE && pipeBE.getPipeType().handlesItems()) {
                for (int slot : OUTPUT_SLOTS) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (sourceStack.isEmpty()) continue;

                    ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(pipeBE, sourceStack.copy());
                    if (remainder.getCount() != sourceStack.getCount()) {
                        this.items.set(slot, remainder);
                        setChanged();
                        return;
                    }
                }
                continue;
            }

            // Target is a standard Container (chests, barrels, hoppers, crates)
            if (targetBE instanceof Container targetContainer) {
                for (int slot : OUTPUT_SLOTS) {
                    ItemStack sourceStack = this.items.get(slot);
                    if (!sourceStack.isEmpty()) {
                        ItemStack remainder = AutoHarvesterBlockEntity.addItemToContainer(targetContainer, sourceStack.copy());
                        if (remainder.getCount() != sourceStack.getCount()) {
                            this.items.set(slot, remainder);
                            setChanged();
                            targetContainer.setChanged();
                            return;
                        }
                    }
                }
            } else {
                var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, targetPos, Direction.UP);
                if (handler != null) {
                    for (int slot : OUTPUT_SLOTS) {
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
                                setChanged();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void syncAssembly() {
        if (this.level == null || this.level.isClientSide()) return;
        boolean valid = checkStructure(false, null);
        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof BlastFurnaceControllerBlock)) return;

        boolean blockAssembled = currentState.getValue(BlastFurnaceControllerBlock.ASSEMBLED);
        if (valid != this.assembled || blockAssembled != valid) {
            this.assembled = valid;
            BlockState newState = currentState.setValue(BlastFurnaceControllerBlock.ASSEMBLED, valid);
            if (valid) {
                newState = newState.setValue(BlastFurnaceControllerBlock.BLUEPRINT, false);
                this.blueprintProjectionTicks = 0;
                linkPorts();
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.9f, 0.8f);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5,
                            5, 0.5, 0.5, 0.5, 0.05);
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
                if (current.getBlock() instanceof BlastFurnaceControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(BlastFurnaceControllerBlock.ASSEMBLED, true)
                            .setValue(BlastFurnaceControllerBlock.BLUEPRINT, false), 3);
                }
                linkPorts();
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Blast Smelter]§r 5x5x5 Foundry Structure is §2VALID & OPERATIONAL§r!"));
            }
        } else {
            this.assembled = false;
            this.blueprintProjectionTicks = 600; // 30 seconds
            if (this.level != null && !this.level.isClientSide()) {
                BlockState current = getBlockState();
                if (current.getBlock() instanceof BlastFurnaceControllerBlock) {
                    this.level.setBlock(this.worldPosition, current.setValue(BlastFurnaceControllerBlock.ASSEMBLED, false)
                            .setValue(BlastFurnaceControllerBlock.BLUEPRINT, true), 3);
                }
            }
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Blast Smelter]§r 5x5x5 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, @Nullable Block expectedBlock, boolean isAir, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(125);
        Block casing = RefineryMod.BLAST_FURNACE_CASING.get();
        Block glass = RefineryMod.FOUNDRY_GLASS.get();
        Block controller = RefineryMod.BLAST_FURNACE_CONTROLLER.get();
        Block inPort = RefineryMod.FOUNDRY_INPUT_PORT.get();
        Block outPort = RefineryMod.FOUNDRY_OUTPUT_PORT.get();

        for (int y = 0; y < 5; y++) {
            for (int z = 0; z < 5; z++) {
                for (int x = -2; x <= 2; x++) {
                    // 1. Hollow 3x3x3 interior chamber (X: -1..1, Y: 1..3, Z: 1..3)
                    if (x >= -1 && x <= 1 && y >= 1 && y <= 3 && z >= 1 && z <= 3) {
                        list.add(new BlueprintRequirement(x, y, z, null, true, "Empty Chamber", 0xFFEA580C));
                        continue;
                    }

                    // 2. Front Face (Z = 0)
                    if (z == 0) {
                        if (y == 0) {
                            if (x == 0) {
                                list.add(new BlueprintRequirement(x, y, z, controller, false, "Blast Furnace Controller", 0xFF10B981));
                            } else if (x == -1) {
                                list.add(new BlueprintRequirement(x, y, z, inPort, false, "Foundry Input Port", 0xFF3B82F6));
                            } else if (x == 1) {
                                list.add(new BlueprintRequirement(x, y, z, outPort, false, "Foundry Output Port", 0xFFF59E0B));
                            } else {
                                list.add(new BlueprintRequirement(x, y, z, casing, false, "Blast Furnace Casing", 0xFF475569));
                            }
                        } else if (y >= 1 && y <= 3 && x >= -1 && x <= 1) {
                            // 3x3 Transparent Viewing Window!
                            list.add(new BlueprintRequirement(x, y, z, glass, false, "Foundry Heat-Resistant Glass", 0xFF38BDF8));
                        } else {
                            list.add(new BlueprintRequirement(x, y, z, casing, false, "Blast Furnace Casing", 0xFF475569));
                        }
                        continue;
                    }

                    // 3. All remaining outer walls, floor, and roof
                    list.add(new BlueprintRequirement(x, y, z, casing, false, "Blast Furnace Casing", 0xFF475569));
                }
            }
        }
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
        Direction facing = getBlockState().getValue(BlastFurnaceControllerBlock.FACING);
        List<BlueprintRequirement> reqs = getRequirements();
        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (BlueprintRequirement req : reqs) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            BlockState state = this.level.getBlockState(targetPos);

            if (req.isAir) {
                if (state.isAir()) {
                    matched++;
                } else {
                    if (missing.size() < 4) {
                        missing.add("Chamber Layer " + req.y + ": Must be empty (clear " + state.getBlock().getName().getString() + ") at [" + targetPos.toShortString() + "]");
                    }
                }
            } else if (req.expectedBlock != null && state.is(req.expectedBlock)) {
                matched++;
            } else {
                if (missing.size() < 4) {
                    missing.add("Layer " + req.y + ": " + req.name + " at [" + targetPos.toShortString() + "]");
                }
            }
        }

        if (notifyPlayer && player != null) {
            player.sendSystemMessage(Component.literal("§e[Blast Smelter Diagnostic]§r Blocks matched: §a" + matched + " / " + reqs.size()));
            if (!missing.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cMissing structure parts:"));
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
        Direction facing = getBlockState().getValue(BlastFurnaceControllerBlock.FACING);
        for (BlueprintRequirement req : getRequirements()) {
            if (req.isAir) continue;
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof FoundryPortBlockEntity portBE) {
                portBE.setControllerPos(this.worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putInt("progress", this.progress);
        output.putInt("ironUnits", this.ironUnits);
        output.putInt("fuelUnits", this.fuelUnits);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.progress = input.getIntOr("progress", 0);
        this.ironUnits = input.getIntOr("ironUnits", 0);
        this.fuelUnits = input.getIntOr("fuelUnits", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === Container & WorldlyContainer Implementation ===
    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? OUTPUT_SLOTS : INPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot >= 8) return false; // Output slots
        if (slot < 4) return isIronSource(stack);
        return isFuelSource(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 8; // Only take from output slots
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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= 8) return false;
        if (slot < 4) return isIronSource(stack);
        return isFuelSource(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // === MenuProvider Implementation ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.blast_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BlastFurnaceMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
