package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.FermenterBlock;
import com.refinery.fluid.RefineryFluids;
import com.refinery.fluid.SingleFluidHandler;
import com.refinery.menu.FermenterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class FermenterBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 4;
    public static final int TANK_CAPACITY = 8_000;
    public static final int ENERGY_CAPACITY = 20_000;
    public static final int ENERGY_PER_TICK = 15;
    public static final int PROCESS_TIME = 60; // 3 seconds per crop

    private int energy = 0;
    private int progress = 0;
    private int ethanolAmount = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(ENERGY_CAPACITY, 500, 0) {
        @Override
        public void set(int amount) {
            super.set(amount);
            FermenterBlockEntity.this.energy = amount;
            setChanged();
        }

        @Override
        public int insert(int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            int inserted = super.insert(amount, transaction);
            FermenterBlockEntity.this.energy = this.energy;
            return inserted;
        }
    };

    public final SingleFluidHandler fluidHandler = new SingleFluidHandler(
            RefineryFluids.ETHANOL_SOURCE::get,
            () -> ethanolAmount,
            this::setEthanolAmount,
            TANK_CAPACITY,
            false,
            true,
            this::setChanged
    );

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> energy & 0xFFFF;
                case 3 -> (energy >> 16) & 0xFFFF;
                case 4 -> ethanolAmount;
                case 5 -> TANK_CAPACITY;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> energy = (energy & 0xFFFF0000) | (value & 0xFFFF);
                case 3 -> energy = (energy & 0x0000FFFF) | ((value & 0xFFFF) << 16);
                case 4 -> ethanolAmount = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public FermenterBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.FERMENTER_BE.get(), pos, state);
    }

    public int getEnergy() { return this.energy; }
    public int getEthanolAmount() { return this.ethanolAmount; }
    public void setEthanolAmount(int amt) {
        this.ethanolAmount = Math.clamp(amt, 0, TANK_CAPACITY);
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        boolean wasActive = state.getValue(FermenterBlock.ACTIVE);
        boolean isActive = false;

        // 1. Manual Bucket Filling (Slot 2 -> Slot 3)
        handleBucketFilling();

        // 2. Fermentation Processing
        ItemStack input = this.items.get(0);
        FermentRecipe recipe = getRecipe(input);

        if (recipe != null) {
            boolean canFit = (TANK_CAPACITY - this.ethanolAmount) >= recipe.ethanolYield && canInsertByproduct(recipe.byproduct);
            int requiredEnergy = (PROCESS_TIME - this.progress) * ENERGY_PER_TICK;

            if (canFit && this.energy >= requiredEnergy) {
                isActive = true;
                this.energy -= ENERGY_PER_TICK;
                this.energyStorage.set(this.energy);
                this.progress++;

                if (level.getRandom().nextInt(5) == 0) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.3 + level.getRandom().nextDouble() * 0.4,
                            pos.getY() + 0.8,
                            pos.getZ() + 0.3 + level.getRandom().nextDouble() * 0.4,
                            1, 0.02, 0.05, 0.02, 0.01);
                }

                if (this.progress >= PROCESS_TIME) {
                    this.progress = 0;
                    input.shrink(1);
                    this.ethanolAmount = Math.min(TANK_CAPACITY, this.ethanolAmount + recipe.ethanolYield);

                    // Byproduct chance
                    if (!recipe.byproduct.isEmpty() && level.getRandom().nextFloat() < recipe.byproductChance) {
                        insertByproduct(recipe.byproduct.copy());
                    }

                    serverLevel.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.6f, 1.2f);
                }
                setChanged();
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
        }

        // 3. Auto-push Ethanol to adjacent pipes/tanks
        if (this.ethanolAmount > 0) {
            pushFluid(serverLevel, pos);
        }

        if (wasActive != isActive) {
            level.setBlock(pos, state.setValue(FermenterBlock.ACTIVE, isActive), 3);
        }
    }

    private void handleBucketFilling() {
        ItemStack bucketIn = this.items.get(2);
        ItemStack bucketOut = this.items.get(3);

        if (bucketIn.is(Items.BUCKET) && this.ethanolAmount >= 1000) {
            ItemStack result = new ItemStack(RefineryFluids.ETHANOL_BUCKET.get());
            if (bucketOut.isEmpty()) {
                bucketIn.shrink(1);
                this.items.set(3, result);
                this.ethanolAmount -= 1000;
                setChanged();
            } else if (ItemStack.isSameItemSameComponents(bucketOut, result) && bucketOut.getCount() < bucketOut.getMaxStackSize()) {
                bucketIn.shrink(1);
                bucketOut.grow(1);
                this.ethanolAmount -= 1000;
                setChanged();
            }
        }
    }

    private void pushFluid(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.relative(dir);
            var neighborHandler = level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
            if (neighborHandler != null) {
                int toPush = Math.min(250, this.ethanolAmount);
                FluidResource res = FluidResource.of(RefineryFluids.ETHANOL_SOURCE.get());
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = 0;
                    for (int i = 0; i < neighborHandler.size(); i++) {
                        int ins = neighborHandler.insert(i, res, toPush - inserted, tx);
                        inserted += ins;
                        if (inserted >= toPush) break;
                    }
                    if (inserted > 0) {
                        tx.commit();
                        this.ethanolAmount -= inserted;
                        setChanged();
                        if (this.ethanolAmount <= 0) break;
                    }
                }
            }
        }
    }

    public record FermentRecipe(int ethanolYield, ItemStack byproduct, float byproductChance) {}

    public static @Nullable FermentRecipe getRecipe(ItemStack input) {
        if (input.isEmpty()) return null;
        // 1. Wheat -> 200 mB Ethanol + 20% bonus Wheat Seeds
        if (input.is(Items.WHEAT)) {
            return new FermentRecipe(200, new ItemStack(Items.WHEAT_SEEDS), 0.20f);
        }
        // 2. Potato -> 150 mB Ethanol + 10% Poisonous Potato
        if (input.is(Items.POTATO)) {
            return new FermentRecipe(150, new ItemStack(Items.POISONOUS_POTATO), 0.10f);
        }
        // 3. Sugar Cane -> 100 mB Ethanol + 15% Sugar
        if (input.is(Items.SUGAR_CANE)) {
            return new FermentRecipe(100, new ItemStack(Items.SUGAR), 0.15f);
        }
        // 4. Beetroot -> 100 mB Ethanol + 20% Beetroot Seeds
        if (input.is(Items.BEETROOT)) {
            return new FermentRecipe(100, new ItemStack(Items.BEETROOT_SEEDS), 0.20f);
        }
        // 5. Apple -> 150 mB Ethanol
        if (input.is(Items.APPLE)) {
            return new FermentRecipe(150, ItemStack.EMPTY, 0.0f);
        }
        // 6. Sweet Berries -> 100 mB Ethanol
        if (input.is(Items.SWEET_BERRIES)) {
            return new FermentRecipe(100, ItemStack.EMPTY, 0.0f);
        }
        return null;
    }

    private boolean canInsertByproduct(ItemStack byproduct) {
        if (byproduct.isEmpty()) return true;
        ItemStack current = this.items.get(1);
        return current.isEmpty() || (ItemStack.isSameItemSameComponents(current, byproduct) && current.getCount() + byproduct.getCount() <= current.getMaxStackSize());
    }

    private void insertByproduct(ItemStack byproduct) {
        ItemStack current = this.items.get(1);
        if (current.isEmpty()) {
            this.items.set(1, byproduct);
        } else if (ItemStack.isSameItemSameComponents(current, byproduct) && current.getCount() + byproduct.getCount() <= current.getMaxStackSize()) {
            current.grow(byproduct.getCount());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
        output.putInt("progress", this.progress);
        output.putInt("ethanol", this.ethanolAmount);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.energyStorage.set(this.energy);
        this.progress = input.getIntOr("progress", 0);
        this.ethanolAmount = input.getIntOr("ethanol", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_DOWN = new int[]{1, 3};
    private static final int[] SLOTS_SIDES = new int[]{0, 2};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return SLOTS_UP;
        if (side == Direction.DOWN) return SLOTS_DOWN;
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot == 0) return getRecipe(stack) != null;
        if (slot == 2) return stack.is(Items.BUCKET);
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 1 || slot == 3;
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
        ItemStack res = ContainerHelper.removeItem(this.items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
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
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.fermenter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FermenterMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
