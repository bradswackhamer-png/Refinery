package com.refinery.block.entity;

import com.refinery.block.SmelterPortBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

/**
 * High-performance transactional Item ResourceHandler for the Industrial Multi-Smelter.
 * Supports load-balanced parallel distribution across the 8 input smelting slots,
 * strict port isolation (INPUT vs OUTPUT vs ENERGY_FUEL), and integration with
 * NeoForge item transfer pipelines (conveyor belts, item pipes, hoppers).
 */
public class SmelterItemHandler implements ResourceHandler<ItemResource> {
    public enum AccessType {
        INPUT,
        OUTPUT,
        ENERGY_FUEL,
        ALL
    }

    private final MultiSmelterControllerBlockEntity controller;
    private final AccessType accessType;
    private final ResourceHandler<ItemResource> wrapper;

    public SmelterItemHandler(MultiSmelterControllerBlockEntity controller, AccessType accessType) {
        this.controller = controller;
        this.accessType = accessType;
        this.wrapper = VanillaContainerWrapper.of(controller);
    }

    public static SmelterItemHandler forPort(MultiSmelterControllerBlockEntity controller, SmelterPortBlock.PortType portType) {
        AccessType access = switch (portType) {
            case INPUT -> AccessType.INPUT;
            case OUTPUT -> AccessType.OUTPUT;
            case ENERGY_FUEL -> AccessType.ENERGY_FUEL;
        };
        return new SmelterItemHandler(controller, access);
    }

    public static SmelterItemHandler forControllerSide(MultiSmelterControllerBlockEntity controller, @Nullable Direction side) {
        if (side == Direction.UP) {
            return new SmelterItemHandler(controller, AccessType.INPUT);
        } else if (side == Direction.DOWN) {
            return new SmelterItemHandler(controller, AccessType.OUTPUT);
        } else if (side != null) {
            return new SmelterItemHandler(controller, AccessType.ENERGY_FUEL);
        }
        return new SmelterItemHandler(controller, AccessType.ALL);
    }

    private int mapSlot(int index) {
        return switch (this.accessType) {
            case INPUT -> index; // 0..7
            case OUTPUT -> 8 + index; // 8..15
            case ENERGY_FUEL -> 16;
            case ALL -> index; // 0..16
        };
    }

    @Override
    public int size() {
        return switch (this.accessType) {
            case INPUT -> MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT; // 8
            case OUTPUT -> MultiSmelterControllerBlockEntity.OUTPUT_SLOTS_COUNT; // 8
            case ENERGY_FUEL -> 1;
            case ALL -> MultiSmelterControllerBlockEntity.INVENTORY_SIZE; // 17
        };
    }

    @Override
    public ItemResource getResource(int index) {
        if (index < 0 || index >= size()) return ItemResource.EMPTY;
        return wrapper.getResource(mapSlot(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        if (index < 0 || index >= size()) return 0;
        return wrapper.getAmountAsLong(mapSlot(index));
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        if (index < 0 || index >= size()) return 0;
        return wrapper.getCapacityAsLong(mapSlot(index), resource);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (index < 0 || index >= size() || resource.isEmpty()) return false;
        int targetSlot = mapSlot(index);
        ItemStack stack = resource.toStack();
        if (targetSlot >= 0 && targetSlot < MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT) {
            return accessType != AccessType.OUTPUT && controller.canSmelt(stack);
        }
        if (targetSlot == MultiSmelterControllerBlockEntity.FUEL_SLOT_INDEX) {
            return accessType != AccessType.OUTPUT && accessType != AccessType.INPUT
                    && MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0;
        }
        return false;
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index < 0 || index >= size() || amount <= 0 || !isValid(index, resource)) {
            return 0;
        }
        return wrapper.insert(mapSlot(index), resource, amount, transaction);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) return 0;

        if (this.accessType == AccessType.OUTPUT) {
            return 0; // Output ports reject insertion
        }

        if (this.accessType == AccessType.ENERGY_FUEL) {
            return insert(0, resource, amount, transaction);
        }

        ItemStack stack = resource.toStack();

        // If accessed in ALL mode and item is fuel (but not smeltable), route to fuel slot
        if (this.accessType == AccessType.ALL && !controller.canSmelt(stack)
                && MultiSmelterControllerBlockEntity.getFuelBurnDuration(stack) > 0) {
            return wrapper.insert(MultiSmelterControllerBlockEntity.FUEL_SLOT_INDEX, resource, amount, transaction);
        }

        if (!controller.canSmelt(stack)) {
            return 0;
        }

        // Automatic load-balanced parallel distribution across the 8 input smelting slots (0..7)
        int req = controller.getRequiredInputCount(stack);
        int remaining = amount;
        int totalInserted = 0;

        while (remaining > 0) {
            int bestSlot = -1;
            int bestPriority = Integer.MAX_VALUE; // 0 = incomplete batch, 1 = complete batch
            long bestKey = Long.MAX_VALUE;

            for (int i = 0; i < MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT; i++) {
                ItemResource current = wrapper.getResource(i);
                long count = wrapper.getAmountAsLong(i);
                long cap = wrapper.getCapacityAsLong(i, resource);
                if (count < cap && (current.isEmpty() || current.equals(resource))) {
                    int rem = (count > 0 && count % req != 0) ? (int)(req - (count % req)) : 0;
                    int priority = (rem > 0) ? 0 : 1;
                    long sortKey = (rem > 0) ? rem : (count / req);

                    if (priority < bestPriority || (priority == bestPriority && sortKey < bestKey)) {
                        bestPriority = priority;
                        bestKey = sortKey;
                        bestSlot = i;
                    }
                }
            }

            if (bestSlot == -1) {
                break; // All input slots full or incompatible
            }

            long currentCount = wrapper.getAmountAsLong(bestSlot);
            long cap = wrapper.getCapacityAsLong(bestSlot, resource);
            int maxSpace = (int) Math.min(cap - currentCount, Integer.MAX_VALUE);

            int targetAmount;
            if (bestPriority == 0) {
                // Incomplete batch: insert what is needed to complete this batch and start cooking
                int needed = (int)(req - (currentCount % req));
                targetAmount = Math.min(remaining, needed);
            } else {
                // Complete batch: insert 1 full recipe batch (or remaining)
                targetAmount = Math.min(remaining, req);
            }

            int toInsert = Math.max(1, Math.min(targetAmount, maxSpace));
            int inserted = wrapper.insert(bestSlot, resource, toInsert, transaction);
            if (inserted <= 0) {
                break;
            }

            remaining -= inserted;
            totalInserted += inserted;
        }

        return totalInserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (index < 0 || index >= size() || amount <= 0) return 0;
        if (this.accessType == AccessType.INPUT) {
            return 0; // Cannot extract from input ports
        }
        int targetSlot = mapSlot(index);
        if (this.accessType == AccessType.ALL && targetSlot < MultiSmelterControllerBlockEntity.INPUT_SLOTS_COUNT) {
            return 0; // In ALL mode, protect input ingredients from extraction
        }
        return wrapper.extract(targetSlot, resource, amount, transaction);
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || this.accessType == AccessType.INPUT) return 0;
        if (this.accessType == AccessType.ENERGY_FUEL) {
            return extract(0, resource, amount, transaction);
        }

        // For OUTPUT or ALL mode: extract from output slots 8..15
        int remaining = amount;
        int totalExtracted = 0;
        for (int i = 0; i < MultiSmelterControllerBlockEntity.OUTPUT_SLOTS_COUNT && remaining > 0; i++) {
            int targetSlot = 8 + i;
            int extracted = wrapper.extract(targetSlot, resource, remaining, transaction);
            if (extracted > 0) {
                remaining -= extracted;
                totalExtracted += extracted;
            }
        }
        return totalExtracted;
    }
}
