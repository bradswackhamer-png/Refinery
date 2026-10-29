package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.FeedingTroughBlock;
import com.refinery.menu.FeedingTroughMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FeedingTroughBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 9;
    public static final int DATA_COUNT = 2;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int animalsInRange = 0;
    private int cachedFoodCount = 0;
    private int tickCount = 0;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            FeedingTroughBlockEntity master = getMaster();
            return switch (index) {
                case 0 -> master.getTotalFoodCount();
                case 1 -> master.animalsInRange;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            FeedingTroughBlockEntity master = getMaster();
            switch (index) {
                case 0 -> master.cachedFoodCount = value;
                case 1 -> master.animalsInRange = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public FeedingTroughBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.FEEDING_TROUGH_BE.get(), pos, blockState);
    }

    /**
     * Resolves the master (FOOT) BlockEntity holding the primary inventory.
     */
    public FeedingTroughBlockEntity getMaster() {
        if (this.level == null) return this;
        BlockState state = getBlockState();
        if (state.hasProperty(FeedingTroughBlock.PART) && state.getValue(FeedingTroughBlock.PART) == BedPart.HEAD) {
            Direction facing = state.getValue(FeedingTroughBlock.FACING);
            BlockPos footPos = this.worldPosition.relative(facing.getOpposite());
            if (this.level.isLoaded(footPos) && this.level.getBlockEntity(footPos) instanceof FeedingTroughBlockEntity master) {
                return master;
            }
        }
        return this;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FeedingTroughBlockEntity be) {
        // Only run logic on the FOOT (master) half
        if (state.hasProperty(FeedingTroughBlock.PART) && state.getValue(FeedingTroughBlock.PART) != BedPart.FOOT) {
            return;
        }

        be.tickCount++;
        // Check once per second (every 20 ticks)
        if (be.tickCount % 20 != 0) {
            return;
        }

        Direction facing = state.hasProperty(FeedingTroughBlock.FACING) ? state.getValue(FeedingTroughBlock.FACING) : Direction.NORTH;
        BlockPos headPos = pos.relative(facing);

        // Scan 4.5 blocks horizontally, 2 blocks vertically around both halves
        AABB troughBounds = new AABB(pos).minmax(new AABB(headPos));
        AABB scanBox = troughBounds.inflate(4.5, 2.0, 4.5);

        List<Animal> animals = level.getEntitiesOfClass(Animal.class, scanBox);
        be.animalsInRange = animals.size();

        if (be.getTotalFoodCount() <= 0) {
            return;
        }

        int fedThisSecond = 0;
        int maxFeedPerSecond = 2;

        for (Animal animal : animals) {
            if (fedThisSecond >= maxFeedPerSecond) {
                break;
            }

            // Only feed adults that are alive and off breeding cooldown
            if (!animal.isAlive() || animal.isBaby() || !animal.canFallInLove()) {
                continue;
            }

            // Find matching food in the 9-slot inventory
            for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
                ItemStack stack = be.items.get(slot);
                if (!stack.isEmpty() && animal.isFood(stack)) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        be.items.set(slot, ItemStack.EMPTY);
                    }

                    // Put the animal into love mode
                    animal.setInLove(null);

                    // Broadcast hearts
                    if (level instanceof ServerLevel sl) {
                        sl.broadcastEntityEvent(animal, (byte) 18);
                    }

                    // Play animal eating sound
                    level.playSound(
                            null,
                            animal.getX(), animal.getY(), animal.getZ(),
                            SoundEvents.GENERIC_EAT,
                            SoundSource.NEUTRAL,
                            1.0f,
                            1.0f + (animal.getRandom().nextFloat() - animal.getRandom().nextFloat()) * 0.2f
                    );

                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    fedThisSecond++;
                    break;
                }
            }
        }
    }

    public static boolean isFeedItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.has(DataComponents.FOOD)) return true;

        Item item = stack.getItem();
        if (item == Items.WHEAT || item == Items.HAY_BLOCK || item == Items.BAMBOO || item == Items.SEAGRASS || item == Items.KELP || item == Items.DRIED_KELP) {
            return true;
        }
        if (item == Items.WHEAT_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS || item == Items.BEETROOT_SEEDS || item == Items.TORCHFLOWER_SEEDS || item == Items.PITCHER_POD) {
            return true;
        }
        if (item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Items.POTATO || item == Items.BAKED_POTATO || item == Items.POISONOUS_POTATO || item == Items.BEETROOT) {
            return true;
        }
        if (item == Items.APPLE || item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.SWEET_BERRIES || item == Items.GLOW_BERRIES) {
            return true;
        }
        if (item == Items.DANDELION || item == Items.WARPED_FUNGUS || item == Items.CRIMSON_FUNGUS || item == Items.SLIME_BALL) {
            return true;
        }
        return item == Items.COD || item == Items.SALMON || item == Items.TROPICAL_FISH || item == Items.COOKED_COD || item == Items.COOKED_SALMON
                || item == Items.BEEF || item == Items.PORKCHOP || item == Items.MUTTON || item == Items.CHICKEN || item == Items.RABBIT || item == Items.ROTTEN_FLESH
                || item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP || item == Items.COOKED_MUTTON || item == Items.COOKED_CHICKEN || item == Items.COOKED_RABBIT
                || item == Items.SPIDER_EYE;
    }

    public int getTotalFoodCount() {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) {
            return master.getTotalFoodCount();
        }
        int count = 0;
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                count += stack.getCount();
            }
        }
        if (count == 0 && this.cachedFoodCount > 0) {
            return this.cachedFoodCount;
        }
        return count;
    }

    public float getFillFraction() {
        int total = getTotalFoodCount();
        if (total <= 0) return 0.0f;
        // 1 to 128 items maps smoothly to 0.15 .. 1.0 fill level
        return Math.clamp(0.15f + (total / 128.0f) * 0.85f, 0.0f, 1.0f);
    }

    public int getAnimalsInRange() {
        return getMaster().animalsInRange;
    }

    // === Container Implementation with Master Delegation ===

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) return master.isEmpty();
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) return master.getItem(slot);
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) return master.removeItem(slot, amount);
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
            if (this.level != null && !this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) return master.removeItemNoUpdate(slot);
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) {
            master.setItem(slot, stack);
            return;
        }
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isFeedItem(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        FeedingTroughBlockEntity master = getMaster();
        if (master != this) {
            master.clearContent();
            return;
        }
        this.items.clear();
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // === WorldlyContainer Implementation for Hopper / Pipe access ===

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return isFeedItem(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    // === MenuProvider Implementation ===

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.feeding_trough");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        FeedingTroughBlockEntity master = getMaster();
        return new FeedingTroughMenu(containerId, playerInventory, master, master.dataAccess);
    }

    // === Serialization & Client Sync ===

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("foodCount", getTotalFoodCount());
        output.putInt("animalsInRange", this.animalsInRange);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.cachedFoodCount = input.getIntOr("foodCount", 0);
        this.animalsInRange = input.getIntOr("animalsInRange", 0);
        ContainerHelper.loadAllItems(input, this.items);
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
