package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.ArcMelterControllerBlock;
import com.refinery.block.MelterPortBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.menu.ArcMelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArcMelterControllerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 4; // 4 input slots
    public static final int TANK_CAPACITY = 8000;
    public static final int ENERGY_CAPACITY = 50000;
    public static final int ENERGY_PER_TICK = 40;
    public static final int PROCESS_TIME = 40; // 2 seconds per batch

    private boolean assembled = false;
    private int energy = 0;
    private int progress = 0;
    private int blueprintTicks = 0;

    // Internal Fluid Storage
    private Fluid moltenFluid = Fluids.EMPTY;
    private int moltenAmount = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 1000, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            ArcMelterControllerBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int maxReceive, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(maxReceive, transaction);
            ArcMelterControllerBlockEntity.this.energy = this.energy;
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

    public ArcMelterControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.ARC_MELTER_CONTROLLER_BE.get(), pos, blockState);
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
            if (this.blueprintTicks == 0 && state.hasProperty(ArcMelterControllerBlock.BLUEPRINT) && state.getValue(ArcMelterControllerBlock.BLUEPRINT)) {
                level.setBlock(pos, state.setValue(ArcMelterControllerBlock.BLUEPRINT, false), 3);
            }
        }

        if (serverLevel.getGameTime() % 20 == 0) {
            boolean matches = checkStructure(false, null);
            if (matches != this.assembled) {
                this.assembled = matches;
                if (this.assembled) {
                    linkPorts();
                }
                BlockState current = getBlockState();
                if (current.hasProperty(ArcMelterControllerBlock.ASSEMBLED)) {
                    level.setBlock(pos, current.setValue(ArcMelterControllerBlock.ASSEMBLED, this.assembled), 3);
                }
                setChanged();
            }
        }

        if (!this.assembled) {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
            BlockState current = getBlockState();
            if (current.hasProperty(ArcMelterControllerBlock.ACTIVE) && current.getValue(ArcMelterControllerBlock.ACTIVE)) {
                level.setBlock(pos, current.setValue(ArcMelterControllerBlock.ACTIVE, false), 3);
            }
            return;
        }

        boolean wasActive = state.getValue(ArcMelterControllerBlock.ACTIVE);
        boolean isActive = false;

        // Auto-eject molten fluid to adjacent handlers or downstream channels
        if (this.moltenAmount > 0 && this.moltenFluid != Fluids.EMPTY) {
            pushFluidOut();
        }

        // Process item melting
        int inputSlot = getValidInputSlot();
        if (inputSlot != -1) {
            ItemStack input = this.items.get(inputSlot);
            Fluid resultFluid = getMeltingResult(input);

            boolean canFit = (this.moltenFluid == Fluids.EMPTY || this.moltenFluid == resultFluid)
                    && (this.moltenAmount + 1000 <= TANK_CAPACITY);

            if (resultFluid != Fluids.EMPTY && canFit && this.energy >= ENERGY_PER_TICK) {
                isActive = true;
                this.energy -= ENERGY_PER_TICK;
                this.energyStorage.set(this.energy);
                this.progress++;

                // Electric arc sizzle and flashes
                if (this.progress % 8 == 1) {
                    Direction facing = state.getValue(ArcMelterControllerBlock.FACING);
                    BlockPos chamberPos = getRelativePos(0, 1, 1, facing);
                    serverLevel.playSound(null, chamberPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.2f, 1.8f);
                    serverLevel.playSound(null, chamberPos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.8f, 1.2f);

                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 0.4, chamberPos.getZ() + 0.5,
                            6, 0.2, 0.2, 0.2, 0.05);
                    serverLevel.sendParticles(ParticleTypes.LAVA,
                            chamberPos.getX() + 0.5, chamberPos.getY() + 0.3, chamberPos.getZ() + 0.5,
                            3, 0.15, 0.1, 0.15, 0.02);
                }

                if (this.progress >= PROCESS_TIME) {
                    this.progress = 0;
                    input.shrink(1);
                    this.moltenFluid = resultFluid;
                    this.moltenAmount += 1000;

                    Direction facing = state.getValue(ArcMelterControllerBlock.FACING);
                    BlockPos chamberPos = getRelativePos(0, 1, 1, facing);
                    serverLevel.playSound(null, chamberPos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 0.7f, 1.1f);
                    setChanged();
                }
            } else {
                if (this.progress > 0) {
                    this.progress = 0;
                    setChanged();
                }
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
        }

        if (wasActive != isActive) {
            BlockState latest = getBlockState();
            if (latest.hasProperty(ArcMelterControllerBlock.ACTIVE)) {
                level.setBlock(pos, latest.setValue(ArcMelterControllerBlock.ACTIVE, isActive), 3);
            }
        }
    }

    private void pushFluidOut() {
        if (this.level == null) return;
        Direction facing = getBlockState().getValue(ArcMelterControllerBlock.FACING);
        // Find output port
        for (BlueprintRequirement req : getRequirements()) {
            BlockPos targetPos = getRelativePos(req.x, req.y, req.z, facing);
            if (this.level.getBlockEntity(targetPos) instanceof MelterPortBlockEntity portBE) {
                if (portBE.getPortType() == MelterPortBlock.PortType.OUTPUT) {
                    for (Direction dir : Direction.values()) {
                        BlockPos neighborPos = targetPos.relative(dir);
                        var neighborHandler = this.level.getCapability(Capabilities.Fluid.BLOCK, neighborPos, dir.getOpposite());
                        if (neighborHandler != null) {
                            int toPush = Math.min(250, this.moltenAmount);
                            FluidResource res = FluidResource.of(this.moltenFluid);
                            int inserted = neighborHandler.insert(0, res, toPush, null);
                            if (inserted > 0) {
                                this.moltenAmount -= inserted;
                                if (this.moltenAmount <= 0) {
                                    this.moltenFluid = Fluids.EMPTY;
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

    private int getValidInputSlot() {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && getMeltingResult(stack) != Fluids.EMPTY) {
                return i;
            }
        }
        return -1;
    }

    public static Fluid getMeltingResult(ItemStack input) {
        if (input.isEmpty()) return Fluids.EMPTY;
        Identifier id = BuiltInRegistries.ITEM.getKey(input.getItem());
        String path = id.getPath();

        // 1. Iron Dust -> Molten Iron
        if (input.is(RefineryMod.IRON_DUST.get()) || input.is(Items.RAW_IRON) || path.contains("iron_dust")) {
            return RefineryFluids.MOLTEN_IRON_SOURCE.get();
        }
        // 2. Gold Dust -> Molten Gold
        if (input.is(RefineryMod.GOLD_DUST.get()) || input.is(Items.RAW_GOLD) || path.contains("gold_dust")) {
            return RefineryFluids.MOLTEN_GOLD_SOURCE.get();
        }
        // 3. Copper Dust -> Molten Copper
        if (input.is(RefineryMod.COPPER_DUST.get()) || input.is(Items.RAW_COPPER) || path.contains("copper_dust")) {
            return RefineryFluids.MOLTEN_COPPER_SOURCE.get();
        }
        // 4. Tin Dust -> Molten Tin
        if (input.is(RefineryMod.TIN_DUST.get()) || path.contains("tin_dust")) {
            return RefineryFluids.MOLTEN_TIN_SOURCE.get();
        }
        // 5. Bronze Dust -> Molten Bronze
        if (input.is(RefineryMod.BRONZE_DUST.get()) || path.contains("bronze_dust")) {
            return RefineryFluids.MOLTEN_BRONZE_SOURCE.get();
        }
        // 6. Sand & Quartz -> Molten Glass
        if (input.is(Items.SAND) || input.is(Items.RED_SAND) || input.is(Items.QUARTZ) || path.contains("quartz")) {
            return RefineryFluids.MOLTEN_GLASS_SOURCE.get();
        }

        return Fluids.EMPTY;
    }

    public void toggleBlueprintProjection(@Nullable Player player) {
        if (this.level == null) return;
        BlockState state = getBlockState();
        boolean current = state.hasProperty(ArcMelterControllerBlock.BLUEPRINT) && state.getValue(ArcMelterControllerBlock.BLUEPRINT);
        boolean next = !current;
        this.blueprintTicks = next ? 600 : 0;
        level.setBlock(getBlockPos(), state.setValue(ArcMelterControllerBlock.BLUEPRINT, next), 3);

        if (next) {
            checkStructure(true, player);
            if (player != null) {
                player.sendSystemMessage(Component.literal("§6[Electric Arc Melter]§r 3x3x3 Blueprint Hologram §bPROJECTED§r for 30 seconds!"));
            }
        }
        setChanged();
    }

    public record BlueprintRequirement(int x, int y, int z, Block expectedBlock, String name, int outlineColor) {}

    public List<BlueprintRequirement> getRequirements() {
        List<BlueprintRequirement> list = new ArrayList<>(27);
        Block controller = RefineryMod.ARC_MELTER_CONTROLLER.get();
        Block inputPort = RefineryMod.MELTER_INPUT_PORT.get();
        Block outputPort = RefineryMod.MELTER_OUTPUT_PORT.get();
        Block chamber = RefineryMod.MELTER_CHAMBER.get();
        Block casing = RefineryMod.MELTER_CASING.get();

        // Layer 0: Foundation & Tapping Spout
        list.add(new BlueprintRequirement(-1, 0, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 0, controller, "Electric Arc Melter Controller", 0xFF10B981));
        list.add(new BlueprintRequirement( 1, 0, 0, outputPort, "Melter Molten Output Port", 0xFFF97316));
        list.add(new BlueprintRequirement(-1, 0, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 0, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 0, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 0, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 0, 2, casing, "Melter Structural Casing", 0xFF64748B));

        // Layer 1: Melting Crucible & Electrodes
        list.add(new BlueprintRequirement(-1, 1, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 0, chamber, "Melter Quartz Viewport", 0xFF38BDF8));
        list.add(new BlueprintRequirement( 1, 1, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 1, chamber, "Crucible Bath (or Hollow)", 0xFFF59E0B));
        list.add(new BlueprintRequirement( 1, 1, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 1, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 1, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 1, 2, casing, "Melter Structural Casing", 0xFF64748B));

        // Layer 2: Gantry & Feed Port
        list.add(new BlueprintRequirement(-1, 2, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 0, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 1, inputPort, "Melter Feed Input Port", 0xFF3B82F6));
        list.add(new BlueprintRequirement( 1, 2, 1, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement(-1, 2, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 0, 2, 2, casing, "Melter Structural Casing", 0xFF64748B));
        list.add(new BlueprintRequirement( 1, 2, 2, casing, "Melter Structural Casing", 0xFF64748B));

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
        Direction facing = getBlockState().getValue(ArcMelterControllerBlock.FACING);
        List<BlueprintRequirement> reqs = getRequirements();
        Block chamberBlock = RefineryMod.MELTER_CHAMBER.get();

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
            player.sendSystemMessage(Component.literal("§e[Arc Melter Diagnostic]§r Blocks in place: §a" + matched + " / " + reqs.size()));
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
        Direction facing = getBlockState().getValue(ArcMelterControllerBlock.FACING);
        Block controller = RefineryMod.ARC_MELTER_CONTROLLER.get();
        Block inputPort = RefineryMod.MELTER_INPUT_PORT.get();
        Block outputPort = RefineryMod.MELTER_OUTPUT_PORT.get();
        Block chamber = RefineryMod.MELTER_CHAMBER.get();
        Block casing = RefineryMod.MELTER_CASING.get();

        int inPorts = 0;
        int outPorts = 0;
        int chambers = 0;

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
                    else if (state.is(chamber)) chambers++;
                    else if (!state.is(casing)) return false;
                }
            }
        }

        return inPorts >= 1 && outPorts >= 1 && chambers >= 1;
    }

    private void linkPorts() {
        if (this.level == null) return;
        Direction facing = getBlockState().getValue(ArcMelterControllerBlock.FACING);
        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = -1; x <= 1; x++) {
                    BlockPos targetPos = getRelativePos(x, y, z, facing);
                    if (this.level.getBlockEntity(targetPos) instanceof MelterPortBlockEntity portBE) {
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
        return slot < 4 && getMeltingResult(stack) != Fluids.EMPTY;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
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
        return slot < 4 && getMeltingResult(stack) != Fluids.EMPTY;
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
        return Component.translatable("container.refinery.arc_melter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ArcMelterMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
