package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BlastFurnaceCasingBlock;
import com.refinery.block.BlastFurnaceControllerBlock;
import com.refinery.block.FoundryGlassBlock;
import com.refinery.block.FoundryPortBlock;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        return this.items;
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
        boolean hasIron = false;
        for (int slot : IRON_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && isIronSource(stack)) {
                hasIron = true;
                break;
            }
        }

        boolean hasFuel = false;
        for (int slot : FUEL_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && isFuelSource(stack)) {
                hasFuel = true;
                break;
            }
        }

        return hasIron && hasFuel;
    }

    private void consumeIngredients() {
        // Consume 1 Iron
        for (int slot : IRON_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && isIronSource(stack)) {
                stack.shrink(1);
                break;
            }
        }

        // Consume 1 Coal / Fuel
        for (int slot : FUEL_SLOTS) {
            ItemStack stack = this.items.get(slot);
            if (!stack.isEmpty() && isFuelSource(stack)) {
                stack.shrink(1);
                break;
            }
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
        return stack.is(Items.RAW_IRON) || stack.is(Items.IRON_INGOT)
                || stack.is(Items.IRON_ORE) || stack.is(Items.DEEPSLATE_IRON_ORE)
                || stack.is(Items.RAW_IRON_BLOCK);
    }

    public static boolean isFuelSource(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(ItemTags.COALS) || stack.is(Items.COAL_BLOCK)
                || stack.is(RefineryMod.TAR_ITEM.get()) || stack.is(RefineryMod.BITUMEN.get()) || stack.is(Items.CHARCOAL)) {
            return true;
        }
        net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && (id.getPath().contains("tar") || id.getPath().contains("bitumen"));
    }

    private void tryExportOutput(ServerLevel level) {
        Direction facing = getBlockState().getValue(BlastFurnaceControllerBlock.FACING);
        // Output Port is located at relative (1, 0, 0)
        BlockPos portPos = getRelativePos(1, 0, 0, facing);

        // Try exporting to container in front of port or below port
        BlockPos[] targets = new BlockPos[]{
                portPos.relative(facing.getOpposite()),
                portPos.below()
        };

        for (BlockPos targetPos : targets) {
            BlockEntity targetBE = level.getBlockEntity(targetPos);
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
                                list.add(new BlueprintRequirement(x, y, z, casing, false, "Foundry Casing", 0xFF475569));
                            }
                        } else if (y >= 1 && y <= 3 && x >= -1 && x <= 1) {
                            // 3x3 Transparent Viewing Window!
                            list.add(new BlueprintRequirement(x, y, z, glass, false, "Foundry Heat Glass", 0xFF38BDF8));
                        } else {
                            list.add(new BlueprintRequirement(x, y, z, casing, false, "Foundry Casing", 0xFF475569));
                        }
                        continue;
                    }

                    // 3. All remaining outer walls, floor, and roof
                    list.add(new BlueprintRequirement(x, y, z, casing, false, "Foundry Casing", 0xFF475569));
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
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.progress = input.getIntOr("progress", 0);
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
