package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.pipe.BufferPipeBlock;
import com.refinery.block.pipe.IndustrialPipeType;
import com.refinery.block.pipe.PipeConnectionType;
import com.refinery.block.pipe.PipeFacadeHelper;
import com.refinery.menu.BufferPipeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BufferPipeBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {

    private static final int[] SLOTS = new int[]{0};
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final Map<Direction, PipeConnectionType> connections = new EnumMap<>(Direction.class);
    private final Map<Direction, Boolean> manualDisconnections = new EnumMap<>(Direction.class);
    private final Map<Direction, PipeFacadeHelper.FacadeData> facades = new EnumMap<>(Direction.class);

    public BufferPipeBlockEntity(BlockPos pos, BlockState state) {
        super(RefineryMod.BUFFER_PIPE_BE.get(), pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, PipeConnectionType.NONE);
            manualDisconnections.put(dir, false);
        }
    }

    public PipeConnectionType getConnection(Direction dir) {
        return connections.getOrDefault(dir, PipeConnectionType.NONE);
    }

    public Map<Direction, PipeConnectionType> getConnections() {
        return connections;
    }

    public Map<Direction, PipeFacadeHelper.FacadeData> getFacades() {
        return facades;
    }

    public PipeFacadeHelper.@Nullable FacadeData getFacade(Direction dir) {
        return facades.get(dir);
    }

    public void setFacade(Direction dir, PipeFacadeHelper.@Nullable FacadeData data) {
        if (data == null) {
            facades.remove(dir);
        } else {
            facades.put(dir, data);
        }
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void dropAllFacades() {
        if (level == null || level.isClientSide() || facades.isEmpty()) return;
        for (PipeFacadeHelper.FacadeData data : facades.values()) {
            if (data != null) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, data.toItemStack(1));
            }
        }
        facades.clear();
        setChanged();
    }

    public void toggleSideConnection(Direction dir) {
        boolean currentManual = manualDisconnections.getOrDefault(dir, false);
        manualDisconnections.put(dir, !currentManual);
        updateConnections();
    }

    public void updateConnections() {
        if (level == null) return;
        boolean changed = false;

        for (Direction dir : Direction.values()) {
            if (manualDisconnections.getOrDefault(dir, false)) {
                if (connections.get(dir) != PipeConnectionType.NONE) {
                    connections.put(dir, PipeConnectionType.NONE);
                    changed = true;
                }
                continue;
            }

            BlockPos targetPos = worldPosition.relative(dir);
            if (!level.isLoaded(targetPos)) continue;

            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.isAir()) {
                if (connections.get(dir) != PipeConnectionType.NONE) {
                    connections.put(dir, PipeConnectionType.NONE);
                    changed = true;
                }
                continue;
            }

            BlockEntity targetBe = level.getBlockEntity(targetPos);
            Direction opposite = dir.getOpposite();
            PipeConnectionType detected = PipeConnectionType.NONE;

            if (targetBe instanceof BufferPipeBlockEntity) {
                detected = PipeConnectionType.PIPE;
            } else if (targetBe instanceof IndustrialPipeBlockEntity pipeBe) {
                if (pipeBe.getPipeType().handlesItems() || pipeBe.getPipeType() == IndustrialPipeType.STRUCTURE || pipeBe.getPipeType() == IndustrialPipeType.VOID) {
                    detected = PipeConnectionType.PIPE;
                }
            } else {
                if (isItemTarget(level, targetPos, opposite, targetBe)) {
                    detected = PipeConnectionType.MACHINE;
                }
            }

            if (connections.get(dir) != detected) {
                connections.put(dir, detected);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private static boolean isItemTarget(Level level, BlockPos pos, Direction side, @Nullable BlockEntity be) {
        if (level.getCapability(Capabilities.Item.BLOCK, pos, side) != null) return true;
        if (level.getCapability(Capabilities.Item.BLOCK, pos, null) != null) return true;
        return be instanceof Container && !(be instanceof IndustrialPipeBlockEntity);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BufferPipeBlockEntity be) {
        if (level.isClientSide()) return;

        // Periodic connection refresh every 20 ticks
        if (level.getGameTime() % 20L == Math.floorMod(pos.asLong(), 20L)) {
            be.updateConnections();
        }
    }

    // === Container & WorldlyContainer (1 Slot) ===

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot == 0) return this.items.get(0);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0) {
            ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
            if (!stack.isEmpty()) {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = ContainerHelper.takeItem(this.items, slot);
            if (!stack.isEmpty()) {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            this.items.set(0, stack);
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    // === MenuProvider ===

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.refinery.buffer_pipe");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BufferPipeMenu(containerId, playerInventory, this);
    }

    // === Serialization ===

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        for (Direction dir : Direction.values()) {
            output.putString("Conn_" + dir.getName(), connections.getOrDefault(dir, PipeConnectionType.NONE).name());
            if (manualDisconnections.getOrDefault(dir, false)) {
                output.putBoolean("manual_" + dir.getSerializedName(), true);
            }
        }
        if (!facades.isEmpty()) {
            var facList = output.childrenList("Facades");
            for (Map.Entry<Direction, PipeFacadeHelper.FacadeData> entry : facades.entrySet()) {
                PipeFacadeHelper.FacadeData fd = entry.getValue();
                if (fd != null && fd.block() != null) {
                    var child = facList.addChild();
                    child.putInt("Side", entry.getKey().get3DDataValue());
                    child.putString("Block", fd.getBlockId().toString());
                    child.putBoolean("Hollow", fd.hollow());
                }
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        for (Direction dir : Direction.values()) {
            String cName = input.getStringOr("Conn_" + dir.getName(), PipeConnectionType.NONE.name());
            try {
                connections.put(dir, PipeConnectionType.valueOf(cName));
            } catch (Exception e) {
                connections.put(dir, PipeConnectionType.NONE);
            }
            manualDisconnections.put(dir, input.getBooleanOr("manual_" + dir.getSerializedName(), false));
        }
        this.facades.clear();
        for (ValueInput facIn : input.childrenListOrEmpty("Facades")) {
            int sideIdx = facIn.getIntOr("Side", -1);
            String blockIdStr = facIn.getStringOr("Block", "");
            boolean hollow = facIn.getBooleanOr("Hollow", false);
            if (sideIdx >= 0 && sideIdx < 6 && !blockIdStr.isEmpty()) {
                Identifier id = Identifier.tryParse(blockIdStr);
                if (id != null) {
                    net.minecraft.world.level.block.Block blk = BuiltInRegistries.BLOCK.getValue(id);
                    if (blk != null && blk != net.minecraft.world.level.block.Blocks.AIR) {
                        this.facades.put(Direction.from3DDataValue(sideIdx), new PipeFacadeHelper.FacadeData(blk, hollow));
                    }
                }
            }
        }
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
