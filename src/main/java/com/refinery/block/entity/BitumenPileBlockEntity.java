package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.BitumenPileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class BitumenPileBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 54; // Double chest capacity (6 rows x 9 columns)
    private static final int[] SLOTS = IntStream.range(0, INVENTORY_SIZE).toArray();

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    private int totalCount = 0;
    private int visibleStage = 0; // 0 to 8

    public BitumenPileBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.BITUMEN_PILE_BE.get(), pos, state);
    }

    public boolean isAcceptableItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(RefineryMod.BITUMEN.get())) return true;

        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase();
        if (path.contains("bitumen") || path.contains("asphalt") || path.contains("petroleum_coke")) {
            return true;
        }

        return stack.getItem().builtInRegistryHolder().tags().anyMatch(t -> {
            String tagPath = t.location().getPath().toLowerCase();
            return tagPath.contains("bitumen") || tagPath.contains("asphalt") || tagPath.contains("petroleum_coke");
        });
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getVisibleStage() {
        return this.visibleStage;
    }

    /**
     * Inserts bitumen directly when clicking on the pile or when absorbed from world entities / hoppers.
     */
    public ItemStack insertItem(ItemStack stack) {
        if (!isAcceptableItem(stack)) return stack;

        ItemStack toInsert = stack.copy();

        // 1. Stack onto existing matching slots
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack current = this.items.get(i);
            if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, toInsert)) {
                int space = current.getMaxStackSize() - current.getCount();
                if (space > 0) {
                    int add = Math.min(space, toInsert.getCount());
                    current.grow(add);
                    toInsert.shrink(add);
                    if (toInsert.isEmpty()) break;
                }
            }
        }

        // 2. Put remainder into empty slots
        if (!toInsert.isEmpty()) {
            for (int i = 0; i < INVENTORY_SIZE; i++) {
                if (this.items.get(i).isEmpty()) {
                    this.items.set(i, toInsert.copy());
                    toInsert.setCount(0);
                    break;
                }
            }
        }

        if (toInsert.getCount() != stack.getCount()) {
            setChanged();
            updateCounts();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        return toInsert;
    }

    public void updateCounts() {
        int total = 0;

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack s = this.items.get(i);
            if (!s.isEmpty() && isAcceptableItem(s)) {
                total += s.getCount();
            }
        }

        this.totalCount = total;

        // Calculate visual growth stage (0 to 8)
        // 54 slots * 64 max = 3456 items capacity
        int newStage;
        if (total == 0) {
            newStage = 0;
        } else if (total <= 32) {
            newStage = 1;
        } else if (total <= 128) {
            newStage = 2;
        } else if (total <= 384) {
            newStage = 3;
        } else if (total <= 896) {
            newStage = 4; // Fills lower block (halfway up the 2x2x2)
        } else if (total <= 1600) {
            newStage = 5; // Enters top block
        } else if (total <= 2400) {
            newStage = 6;
        } else if (total <= 3200) {
            newStage = 7;
        } else {
            newStage = 8; // Fully heaped 2x2x2 pyramid summit
        }

        this.visibleStage = newStage;

        // Update STAGE property on all 8 blocks in the multiblock
        if (this.level != null && !this.level.isClientSide()) {
            BlockState masterState = getBlockState();
            if (masterState.getBlock() instanceof BitumenPileBlock) {
                Direction facing = masterState.hasProperty(BitumenPileBlock.FACING)
                        ? masterState.getValue(BitumenPileBlock.FACING)
                        : Direction.NORTH;
                BlockPos[] allPositions = BitumenPileBlock.getAllPositions(this.worldPosition, facing);

                for (BlockPos p : allPositions) {
                    BlockState s = this.level.getBlockState(p);
                    if (s.getBlock() instanceof BitumenPileBlock && s.hasProperty(BitumenPileBlock.STAGE)) {
                        if (s.getValue(BitumenPileBlock.STAGE) != newStage) {
                            this.level.setBlock(p, s.setValue(BitumenPileBlock.STAGE, newStage), 3);
                        }
                    }
                }
            }
        }
    }

    /**
     * Server tick: absorbs bitumen item entities landing on or dropped into the pile.
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        Direction facing = state.hasProperty(BitumenPileBlock.FACING)
                ? state.getValue(BitumenPileBlock.FACING)
                : Direction.NORTH;
        Direction backDir = facing.getOpposite();
        Direction rightDir = facing.getClockWise();

        BlockPos cornerA = pos;
        BlockPos cornerB = pos.relative(rightDir, 2).relative(backDir, 2);

        double minX = Math.min(cornerA.getX(), cornerB.getX());
        double maxX = Math.max(cornerA.getX(), cornerB.getX()) + 1.0;
        double minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        double maxZ = Math.max(cornerA.getZ(), cornerB.getZ()) + 1.0;
        double minY = pos.getY();
        double maxY = pos.getY() + 2.6; // Slightly above top layer to capture falling drops

        AABB vacuumBox = new AABB(minX - 0.1, minY, minZ - 0.1, maxX + 0.1, maxY, maxZ + 0.1);

        List<ItemEntity> itemsInBox = level.getEntitiesOfClass(
                ItemEntity.class, vacuumBox, EntitySelector.ENTITY_STILL_ALIVE
        );

        for (ItemEntity itemEntity : itemsInBox) {
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty() && isAcceptableItem(stack)) {
                ItemStack originalStack = stack.copy();
                ItemStack remaining = insertItem(stack);
                int absorbedCount = originalStack.getCount() - remaining.getCount();

                if (absorbedCount > 0) {
                    if (remaining.isEmpty()) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(remaining);
                    }

                    // Sound feedback
                    level.playSound(
                            null,
                            itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                            SoundEvents.GRAVEL_PLACE,
                            SoundSource.BLOCKS,
                            0.75f,
                            0.8f + level.getRandom().nextFloat() * 0.2f
                    );

                    // Particle splash feedback
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                new ItemParticleOption(ParticleTypes.ITEM, originalStack.getItem()),
                                itemEntity.getX(), itemEntity.getY() + 0.1, itemEntity.getZ(),
                                8,
                                0.15, 0.1, 0.15,
                                0.05
                        );
                    }
                }
            }
        }
    }

    // --- Container Implementation ---

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
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            return this.items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
        if (!result.isEmpty()) {
            setChanged();
            updateCounts();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(this.items, slot);
        if (!result.isEmpty()) {
            setChanged();
            updateCounts();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            this.items.set(slot, stack);
            setChanged();
            updateCounts();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
        updateCounts();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isAcceptableItem(stack);
    }

    // --- WorldlyContainer (Hoppers, Pipes, Conveyors) ---

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return isAcceptableItem(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    // --- MenuProvider ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.bitumen_pile");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return ChestMenu.sixRows(containerId, playerInventory, this);
    }

    // --- Serialization & Sync ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("TotalCount", this.totalCount);
        output.putInt("VisibleStage", this.visibleStage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        this.totalCount = input.getIntOr("TotalCount", 0);
        this.visibleStage = input.getIntOr("VisibleStage", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("TotalCount", this.totalCount);
        tag.putInt("VisibleStage", this.visibleStage);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
