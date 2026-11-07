package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.QuarryControllerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class QuarryMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = QuarryControllerBlockEntity.INVENTORY_SIZE; // 10
    public static final int DATA_COUNT = 14;

    private final Container container;
    private final ContainerData data;
    private final Inventory playerInventory;
    private final @Nullable QuarryControllerBlockEntity blockEntity;

    public QuarryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public QuarryMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(RefineryMod.QUARRY_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        this.playerInventory = playerInventory;
        this.blockEntity = container instanceof QuarryControllerBlockEntity be ? be : null;

        // 1. Dedicated Fence Supply Slot (Slot 9: x: 117, y: 47)
        this.addSlot(new Slot(container, 9, 117, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof net.minecraft.world.item.BlockItem;
            }
        });

        // 2. Buffer Slots (Slots 0..8: row at x: 48 + c * 18, y: 85)
        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(container, c, 48 + c * 18, 85));
        }

        // 3. Dedicated Throw-Back / Pit Spoil Filter Slot (Slot 10: x: 220, y: 85)
        this.addSlot(new Slot(container, 10, 220, 85) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof net.minecraft.world.item.BlockItem;
            }
        });

        // 4. Player Inventory (3 rows of 9: x: 48 + c * 18, y: 115 + r * 18)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(playerInventory, c + r * 9 + 9, 48 + c * 18, 115 + r * 18));
            }
        }

        // 5. Hotbar (9 slots: x: 48 + c * 18, y: 173)
        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(playerInventory, c, 48 + c * 18, 173));
        }

        this.addDataSlots(data);
    }

    public @Nullable QuarryControllerBlockEntity getBlockEntity() {
        if (this.blockEntity != null) {
            return this.blockEntity;
        }
        if (this.playerInventory != null && this.playerInventory.player != null && this.playerInventory.player.level() != null) {
            int x = this.data.get(11);
            int y = this.data.get(12);
            int z = this.data.get(13);
            net.minecraft.core.BlockPos p = new net.minecraft.core.BlockPos(x, y, z);
            if (this.playerInventory.player.level().getBlockEntity(p) instanceof QuarryControllerBlockEntity be) {
                return be;
            }
        }
        return null;
    }

    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public boolean isAssembled() {
        return this.data.get(2) != 0;
    }

    public boolean isActive() {
        return this.data.get(3) != 0;
    }

    public int getRadius() {
        int r = this.data.get(4);
        return r > 0 ? r : QuarryControllerBlockEntity.DEFAULT_RADIUS;
    }

    public int getPhase() {
        return this.data.get(5);
    }

    public int getCurrentY() {
        return this.data.get(6);
    }

    public int getTotalRemainingBlocks() {
        return (this.data.get(8) << 16) | (this.data.get(7) & 0xFFFF);
    }

    public int getMiningProgress() {
        return this.data.get(9);
    }

    public int getSpeedPercent() {
        return this.data.get(10);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                // Radius - 1
                int r = getRadius();
                if (r > QuarryControllerBlockEntity.MIN_RADIUS) {
                    if (this.container instanceof QuarryControllerBlockEntity be) {
                        be.setRadius(r - 1);
                    }
                    this.data.set(4, r - 1);
                }
                return true;
            }
            case 1 -> {
                // Radius + 1
                int r = getRadius();
                if (r < QuarryControllerBlockEntity.MAX_RADIUS) {
                    if (this.container instanceof QuarryControllerBlockEntity be) {
                        be.setRadius(r + 1);
                    }
                    this.data.set(4, r + 1);
                }
                return true;
            }
            case 2 -> {
                // Reset / rescan
                if (this.container instanceof QuarryControllerBlockEntity be) {
                    be.resetExcavationState();
                }
                return true;
            }
            case 3 -> {
                // Toggle all common spoil blocks (dirt, sand, gravel, stone, deepslate, tuff, cobblestone, etc.)
                if (this.container instanceof QuarryControllerBlockEntity be) {
                    be.toggleCommonSpoil();
                }
                return true;
            }
        }

        // Button IDs >= 10: Toggle Throw-Back on clicked Geological Manifest block
        if (id >= 10) {
            int manifestIdx = id - 10;
            if (this.container instanceof QuarryControllerBlockEntity be) {
                var list = be.getClientBlockList();
                if (manifestIdx >= 0 && manifestIdx < list.size()) {
                    be.toggleThrowBack(list.get(manifestIdx).block());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            // From machine buffer / fence / throw-back slot to player inventory
            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory: if block item, try fence slot (0) or throw-back slot (10), or buffer (1..9)
                if (stackInSlot.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    // Try fence slot (slot 0 in menu) or throw-back slot (slot 10 in menu)
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false) && !this.moveItemStackTo(stackInSlot, 10, 11, false) && !this.moveItemStackTo(stackInSlot, 1, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < CONTAINER_SIZE + 27) {
                    // Into hotbar
                    if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE + 27, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // Into main inventory
                    if (!this.moveItemStackTo(stackInSlot, CONTAINER_SIZE, CONTAINER_SIZE + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
