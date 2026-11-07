package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.FireHydrantBlock;
import com.refinery.block.pipe.PipeConnectionType;
import com.refinery.fluid.GenericFluidTank;
import com.refinery.fluid.RefineryFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * BlockEntity for the AWWA Yellow Fire Hydrant.
 * Buffers liquid fed from connected Industrial Fluid Pipes and allows players
 * to right-click with an empty bucket to fill it with 1,000 mB of the supplied fluid.
 */
public class FireHydrantBlockEntity extends BlockEntity {

    public static final int TANK_CAPACITY = 8000;
    public static final int BUCKET_VOLUME = 1000;

    private Fluid currentFluid = Fluids.EMPTY;
    private int fluidAmount = 0;
    private int valveAnimTicks = 0;

    private record FluidSnapshot(Fluid fluid, int amount) {}

    public class HydrantFluidHandler extends SnapshotJournal<FluidSnapshot> implements ResourceHandler<FluidResource> {
        @Override
        protected FluidSnapshot createSnapshot() {
            return new FluidSnapshot(currentFluid, fluidAmount);
        }

        @Override
        protected void revertToSnapshot(FluidSnapshot snapshot) {
            currentFluid = snapshot.fluid();
            fluidAmount = snapshot.amount();
        }

        @Override
        protected void onRootCommit(FluidSnapshot originalState) {
            if (originalState.fluid() != currentFluid || originalState.amount() != fluidAmount) {
                setChanged();
                syncToClient();
            }
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            return (currentFluid == Fluids.EMPTY || fluidAmount <= 0)
                    ? FluidResource.EMPTY
                    : FluidResource.of(currentFluid);
        }

        @Override
        public long getAmountAsLong(int index) {
            return fluidAmount;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return TANK_CAPACITY;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            if (resource.isEmpty()) return false;
            return isBucketableFluid(resource.getFluid());
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            if (resource.isEmpty() || amount <= 0 || !isValid(index, resource)) return 0;
            Fluid incoming = resource.getFluid();

            // If empty or holding less than 1 bucket of a previous stale fluid when a new fluid arrives, switch to the new fluid
            if (currentFluid == Fluids.EMPTY || fluidAmount <= 0) {
                updateSnapshots(transaction);
                currentFluid = incoming;
                int inserted = Math.min(amount, TANK_CAPACITY);
                fluidAmount = inserted;
                return inserted;
            }

            if (!currentFluid.isSame(incoming)) {
                // Allow replacing a sub-bucket residual amount (< 1000 mB) so switching pipe lines never deadlocks the hydrant
                if (fluidAmount < BUCKET_VOLUME) {
                    updateSnapshots(transaction);
                    currentFluid = incoming;
                    int inserted = Math.min(amount, TANK_CAPACITY);
                    fluidAmount = inserted;
                    return inserted;
                }
                return 0;
            }

            int space = TANK_CAPACITY - fluidAmount;
            if (space <= 0) return 0;
            int inserted = Math.min(amount, space);
            if (inserted > 0) {
                updateSnapshots(transaction);
                fluidAmount += inserted;
            }
            return inserted;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            // Hydrant is a terminal dispensing outlet; pipes should not back-siphon fluid out of it
            return 0;
        }
    }

    private final HydrantFluidHandler fluidHandler = new HydrantFluidHandler();

    public FireHydrantBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.FIRE_HYDRANT_BE.get(), pos, state);
    }

    public ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        return fluidHandler;
    }

    public Fluid getCurrentFluid() {
        return currentFluid;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    public int getValveAnimTicks() {
        return valveAnimTicks;
    }

    /**
     * Resolves the filled bucket item corresponding to a fluid, or {@link Items#AIR} if not bucketable.
     */
    public static Item getFilledBucketItem(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) return Items.AIR;
        // Steam cannot be held in an open survival bucket
        if (RefineryFluids.STEAM_SOURCE.isBound() && fluid.isSame(RefineryFluids.STEAM_SOURCE.get())) {
            return Items.AIR;
        }
        Item vanillaOrModBucket = fluid.getBucket();
        if (vanillaOrModBucket != null && vanillaOrModBucket != Items.AIR && vanillaOrModBucket != Items.BUCKET) {
            return vanillaOrModBucket;
        }
        ItemStack fallback = StorageTankBlockEntity.getBucketForFluid(fluid);
        return fallback.isEmpty() ? Items.AIR : fallback.getItem();
    }

    public static boolean isBucketableFluid(Fluid fluid) {
        return getFilledBucketItem(fluid) != Items.AIR;
    }

    public static String getFluidDisplayName(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) return "Empty";
        Item bucket = getFilledBucketItem(fluid);
        if (bucket != Items.AIR) {
            String bucketName = new ItemStack(bucket).getHoverName().getString();
            if (bucketName.endsWith(" Bucket")) {
                return bucketName.substring(0, bucketName.length() - " Bucket".length());
            }
            if (bucketName.startsWith("Bucket of ")) {
                return bucketName.substring("Bucket of ".length());
            }
            return bucketName;
        }
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        return id != null ? id.getPath() : "Unknown";
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FireHydrantBlockEntity be) {
        if (level.isClientSide()) return;

        boolean dirty = false;

        // Tick valve stem nut rotation animation
        if (be.valveAnimTicks > 0) {
            be.valveAnimTicks--;
            dirty = true;
        }

        // Actively pull fluid from directly connected adjacent fluid pipes into the hydrant reservoir
        if (be.fluidAmount < TANK_CAPACITY) {
            for (Direction dir : Direction.values()) {
                BlockPos adjPos = pos.relative(dir);
                if (level.getBlockEntity(adjPos) instanceof IndustrialPipeBlockEntity pipe && pipe.getPipeType().handlesFluid()) {
                    if (pipe.isManuallyDisconnected(dir.getOpposite()) || !pipe.isValveOpen()) continue;
                    PipeConnectionType conn = pipe.getConnection(dir.getOpposite());
                    if (conn == PipeConnectionType.NONE) continue;

                    GenericFluidTank pipeTank = pipe.getFluidTank();
                    int available = pipeTank.getAmount();
                    Fluid pipeFluid = pipeTank.getStoredFluid();
                    if (available <= 0 || pipeFluid == Fluids.EMPTY) continue;
                    if (!isBucketableFluid(pipeFluid)) continue;

                    if (be.currentFluid == Fluids.EMPTY || be.fluidAmount <= 0) {
                        be.currentFluid = pipeFluid;
                    } else if (!be.currentFluid.isSame(pipeFluid)) {
                        if (be.fluidAmount < BUCKET_VOLUME && available >= be.fluidAmount) {
                            // Switch over if hydrant only has sub-bucket leftover of a different fluid
                            be.currentFluid = pipeFluid;
                            be.fluidAmount = 0;
                        } else {
                            continue;
                        }
                    }

                    int space = TANK_CAPACITY - be.fluidAmount;
                    int drained = Math.min(space, available);
                    if (drained > 0) {
                        pipeTank.setAmount(available - drained);
                        be.fluidAmount += drained;
                        pipe.setChanged();
                        dirty = true;
                        if (be.fluidAmount >= TANK_CAPACITY) break;
                    }
                }
            }
        }

        if (be.fluidAmount <= 0 && be.currentFluid != Fluids.EMPTY) {
            be.currentFluid = Fluids.EMPTY;
            be.fluidAmount = 0;
            dirty = true;
        }

        // Keep ACTIVE blockstate synced (true when pressurized with at least 1 bucket of fluid or actively dispensing)
        boolean shouldBeActive = be.fluidAmount >= BUCKET_VOLUME || be.valveAnimTicks > 0;
        if (state.hasProperty(FireHydrantBlock.ACTIVE) && state.getValue(FireHydrantBlock.ACTIVE) != shouldBeActive) {
            level.setBlock(pos, state.setValue(FireHydrantBlock.ACTIVE, shouldBeActive), 3);
        }

        if (dirty) {
            be.setChanged();
            be.syncToClient();
        }
    }

    /**
     * When a player right-clicks or inspects the hydrant, immediately draw any available matching
     * fluid from the contiguous connected Industrial Fluid Pipe network so the player doesn't have
     * to wait for slow tick-by-tick propagation if 1,000 mB is already in the attached pipeline.
     */
    public void ensureBucketVolumeAvailable() {
        if (level == null || level.isClientSide() || fluidAmount >= BUCKET_VOLUME) return;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(worldPosition);

        for (Direction dir : Direction.values()) {
            BlockPos adjPos = worldPosition.relative(dir);
            if (level.getBlockEntity(adjPos) instanceof IndustrialPipeBlockEntity pipe && pipe.getPipeType().handlesFluid()) {
                if (!pipe.isManuallyDisconnected(dir.getOpposite()) && pipe.isValveOpen()) {
                    if (visited.add(adjPos)) {
                        queue.add(adjPos);
                    }
                }
            }
        }

        boolean changed = false;
        int maxNodes = 64;

        while (!queue.isEmpty() && visited.size() <= maxNodes && fluidAmount < BUCKET_VOLUME) {
            BlockPos curr = queue.poll();
            if (!(level.getBlockEntity(curr) instanceof IndustrialPipeBlockEntity pipe) || !pipe.getPipeType().handlesFluid() || !pipe.isValveOpen()) {
                continue;
            }

            GenericFluidTank pipeTank = pipe.getFluidTank();
            int available = pipeTank.getAmount();
            Fluid pipeFluid = pipeTank.getStoredFluid();
            if (available > 0 && pipeFluid != Fluids.EMPTY && isBucketableFluid(pipeFluid)) {
                if (currentFluid == Fluids.EMPTY || fluidAmount <= 0) {
                    currentFluid = pipeFluid;
                }
                if (currentFluid.isSame(pipeFluid)) {
                    int needed = TANK_CAPACITY - fluidAmount;
                    int drained = Math.min(needed, available);
                    if (drained > 0) {
                        pipeTank.setAmount(available - drained);
                        fluidAmount += drained;
                        pipe.setChanged();
                        changed = true;
                    }
                }
            }

            for (Direction dir : Direction.values()) {
                if (pipe.getConnection(dir) == PipeConnectionType.PIPE && !pipe.isManuallyDisconnected(dir)) {
                    BlockPos next = curr.relative(dir);
                    if (visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
        }

        if (changed) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Attempts to fill the player's held empty bucket from the hydrant.
     */
    public boolean tryFillBucket(Player player, InteractionHand hand, ItemStack heldStack) {
        if (level == null || !heldStack.is(Items.BUCKET)) return false;

        if (!level.isClientSide()) {
            ensureBucketVolumeAvailable();
        }

        if (currentFluid == Fluids.EMPTY || fluidAmount < BUCKET_VOLUME) {
            if (!level.isClientSide()) {
                String fluidLabel = (currentFluid == Fluids.EMPTY || fluidAmount <= 0)
                        ? "Empty"
                        : getFluidDisplayName(currentFluid);
                player.sendOverlayMessage(Component.literal(
                        "\u00A7c[Fire Hydrant] Insufficient liquid to fill bucket (" + fluidAmount + " / 1000 mB " + fluidLabel + ")"));
                level.playSound(null, worldPosition, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.7f, 1.2f);
            }
            return false;
        }

        Item filledBucketItem = getFilledBucketItem(currentFluid);
        if (filledBucketItem == Items.AIR) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(Component.literal("\u00A7c[Fire Hydrant] This fluid cannot be collected in a bucket."));
            }
            return false;
        }

        if (!level.isClientSide()) {
            Fluid dispensedFluid = currentFluid;
            String fluidName = getFluidDisplayName(dispensedFluid);

            fluidAmount -= BUCKET_VOLUME;
            if (fluidAmount <= 0) {
                fluidAmount = 0;
                currentFluid = Fluids.EMPTY;
            }
            valveAnimTicks = 12;

            // Replace or add filled bucket to player inventory
            ItemStack filledStack = new ItemStack(filledBucketItem);
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
                if (heldStack.isEmpty()) {
                    player.setItemInHand(hand, filledStack);
                } else if (!player.getInventory().add(filledStack)) {
                    Containers.dropItemStack(level, player.getX(), player.getY() + 0.5, player.getZ(), filledStack);
                }
            } else {
                // In Creative mode, give the filled bucket if the player doesn't already have it
                if (!player.getInventory().contains(filledStack)) {
                    player.getInventory().add(filledStack);
                }
            }

            // Play appropriate bucket fill sound + valve clank
            SoundEvent fillSound = isHotFluid(dispensedFluid) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
            level.playSound(null, worldPosition, fillSound, SoundSource.BLOCKS, 1.0f, 1.0f);
            level.playSound(null, worldPosition, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.35f);

            // Spawn a burst of liquid splash particles from the front pumper nozzle
            if (level instanceof ServerLevel serverLevel) {
                BlockState state = getBlockState();
                Direction facing = state.hasProperty(FireHydrantBlock.FACING)
                        ? state.getValue(FireHydrantBlock.FACING)
                        : Direction.NORTH;
                double px = worldPosition.getX() + 0.5 + facing.getStepX() * 0.42;
                double py = worldPosition.getY() + 0.56;
                double pz = worldPosition.getZ() + 0.5 + facing.getStepZ() * 0.42;
                serverLevel.sendParticles(
                        isHotFluid(dispensedFluid) ? ParticleTypes.LAVA : ParticleTypes.SPLASH,
                        px, py, pz,
                        10,
                        0.08, 0.06, 0.08,
                        0.04
                );
            }

            player.sendOverlayMessage(Component.literal(
                    "\u00A7a[Fire Hydrant] \u00A7fFilled \u00A7e" + filledStack.getHoverName().getString()
                            + " \u00A77(" + fluidAmount + " / " + TANK_CAPACITY + " mB " + fluidName + " remaining)"));

            setChanged();
            syncToClient();
        }

        return true;
    }

    /**
     * Flushes any buffered liquid from the hydrant when the player shift-right-clicks with an empty hand.
     */
    public void flushFluid(Player player) {
        if (level == null || level.isClientSide()) return;
        if (fluidAmount <= 0 || currentFluid == Fluids.EMPTY) {
            player.sendOverlayMessage(Component.literal("\u00A77[Fire Hydrant] Buffer is already empty."));
            return;
        }
        String flushedName = getFluidDisplayName(currentFluid);
        int flushedAmt = fluidAmount;
        currentFluid = Fluids.EMPTY;
        fluidAmount = 0;
        valveAnimTicks = 10;
        setChanged();
        syncToClient();
        level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.8f, 0.9f);
        player.sendOverlayMessage(Component.literal(
                "\u00A76[Fire Hydrant] \u00A7fDrained " + flushedAmt + " mB of " + flushedName + " from hydrant barrel."));
    }

    private static boolean isHotFluid(Fluid fluid) {
        if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) return true;
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        return id != null && (id.getPath().contains("lava") || id.getPath().contains("molten") || id.getPath().contains("bitumen"));
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("FluidAmount", fluidAmount);
        output.putInt("ValveAnimTicks", valveAnimTicks);
        Identifier fluidKey = BuiltInRegistries.FLUID.getKey(currentFluid);
        output.putString("Fluid", fluidKey != null ? fluidKey.toString() : "minecraft:empty");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidAmount = input.getIntOr("FluidAmount", 0);
        valveAnimTicks = input.getIntOr("ValveAnimTicks", 0);
        String fluidName = input.getStringOr("Fluid", "minecraft:empty");
        Fluid parsed = BuiltInRegistries.FLUID.getValue(Identifier.parse(fluidName));
        currentFluid = (parsed != null && fluidAmount > 0) ? parsed : Fluids.EMPTY;
        if (currentFluid == Fluids.EMPTY) {
            fluidAmount = 0;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
