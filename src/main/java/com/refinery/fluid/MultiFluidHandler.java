package com.refinery.fluid;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class MultiFluidHandler implements ResourceHandler<FluidResource> {
    private final ResourceHandler<FluidResource>[] handlers;

    @SafeVarargs
    public MultiFluidHandler(ResourceHandler<FluidResource>... handlers) {
        this.handlers = handlers;
    }

    @Override
    public int size() {
        return handlers.length;
    }

    @Override
    public FluidResource getResource(int index) {
        return (index >= 0 && index < handlers.length) ? handlers[index].getResource(0) : FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return (index >= 0 && index < handlers.length) ? handlers[index].getAmountAsLong(0) : 0L;
    }

    @Override
    public int getAmountAsInt(int index) {
        return (index >= 0 && index < handlers.length) ? handlers[index].getAmountAsInt(0) : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return (index >= 0 && index < handlers.length) ? handlers[index].getCapacityAsLong(0, resource) : 0L;
    }

    @Override
    public int getCapacityAsInt(int index, FluidResource resource) {
        return (index >= 0 && index < handlers.length) ? handlers[index].getCapacityAsInt(0, resource) : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return (index >= 0 && index < handlers.length) && handlers[index].isValid(0, resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return (index >= 0 && index < handlers.length) ? handlers[index].insert(0, resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return (index >= 0 && index < handlers.length) ? handlers[index].extract(0, resource, amount, transaction) : 0;
    }
}
