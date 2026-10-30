package com.refinery.menu;

import com.refinery.RefineryMod;
import com.refinery.block.entity.StorageTankBlockEntity;
import com.refinery.block.entity.StorageTankSize;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class StorageTankMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 6;
    private final Container container;
    private final ContainerData data;
    private final StorageTankBlockEntity blockEntity;

    public StorageTankMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(DATA_COUNT));
    }

    public StorageTankMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, container, data, null);
    }

    public StorageTankMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, StorageTankBlockEntity be) {
        super(RefineryMod.STORAGE_TANK_MENU.get(), containerId);
        checkContainerSize(container, 2);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        this.blockEntity = be;

        // Container Slot 0: Bucket Input (x: 148, y: 18)
        this.addSlot(new Slot(container, 0, 148, 18));

        // Container Slot 1: Bucket Output (x: 148, y: 52)
        this.addSlot(new Slot(container, 1, 148, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3 rows of 9 at y: 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (9 slots at y: 142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getFluidAmount() {
        return this.data.get(0);
    }

    public int getCapacity() {
        int cap = this.data.get(1);
        return cap > 0 ? cap : 64_000;
    }

    public boolean isAssembled() {
        return this.data.get(2) != 0;
    }

    public StorageTankSize getTankSize() {
        int ord = this.data.get(3);
        StorageTankSize[] values = StorageTankSize.values();
        return (ord >= 0 && ord < values.length) ? values[ord] : StorageTankSize.SIZE_3X3X3;
    }

    public StorageTankSize getTargetBlueprintSize() {
        int ord = this.data.get(4);
        StorageTankSize[] values = StorageTankSize.values();
        return (ord >= 0 && ord < values.length) ? values[ord] : StorageTankSize.SIZE_3X3X3;
    }

    public Fluid getStoredFluid() {
        int id = this.data.get(5);
        Fluid fl = BuiltInRegistries.FLUID.byId(id);
        return fl != null ? fl : Fluids.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null) {
            if (id == 0) {
                // Dump / Drain all fluid
                this.blockEntity.getFluidTank().dump();
                return true;
            } else if (id == 1) {
                // Cycle target blueprint size
                this.blockEntity.cycleTargetSize(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 2) {
                // Moving from container to player inventory
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player to input bucket slot 0
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
