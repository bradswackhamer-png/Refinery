package com.refinery.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class SingleFluidHandler extends SnapshotJournal<Integer> implements ResourceHandler<FluidResource> {
    private final Supplier<Fluid> fluidSupplier;
    private final IntSupplier amountGetter;
    private final IntConsumer amountSetter;
    private final int capacity;
    private final boolean canInsert;
    private final boolean canExtract;
    private final Runnable onChanged;

    public SingleFluidHandler(Supplier<Fluid> fluidSupplier,
                              IntSupplier amountGetter,
                              IntConsumer amountSetter,
                              int capacity,
                              boolean canInsert,
                              boolean canExtract,
                              Runnable onChanged) {
        this.fluidSupplier = fluidSupplier;
        this.amountGetter = amountGetter;
        this.amountSetter = amountSetter;
        this.capacity = capacity;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
        this.onChanged = onChanged;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        if (index != 0) return FluidResource.EMPTY;
        Fluid fluid = fluidSupplier.get();
        if (fluid == null || fluid == Fluids.EMPTY) return FluidResource.EMPTY;
        return FluidResource.of(fluid);
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? amountGetter.getAsInt() : 0L;
    }

    @Override
    public int getAmountAsInt(int index) {
        return index == 0 ? amountGetter.getAsInt() : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return capacity;
    }

    @Override
    public int getCapacityAsInt(int index, FluidResource resource) {
        return capacity;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if (index != 0 || !canInsert) return false;
        if (resource.isEmpty()) return true;
        Fluid fluid = fluidSupplier.get();
        return resource.getFluid() == fluid;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !canInsert || amount <= 0) return 0;
        Fluid fluid = fluidSupplier.get();
        if (!resource.isEmpty() && resource.getFluid() != fluid) return 0;

        int current = amountGetter.getAsInt();
        int space = capacity - current;
        int toInsert = Math.min(space, amount);
        if (toInsert <= 0) return 0;

        updateSnapshots(transaction);
        amountSetter.accept(current + toInsert);
        if (onChanged != null) onChanged.run();
        return toInsert;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !canExtract || amount <= 0) return 0;
        Fluid fluid = fluidSupplier.get();
        if (!resource.isEmpty() && resource.getFluid() != fluid) return 0;

        int current = amountGetter.getAsInt();
        int toExtract = Math.min(current, amount);
        if (toExtract <= 0) return 0;

        updateSnapshots(transaction);
        amountSetter.accept(current - toExtract);
        if (onChanged != null) onChanged.run();
        return toExtract;
    }

    @Override
    protected Integer createSnapshot() {
        return amountGetter.getAsInt();
    }

    @Override
    protected void revertToSnapshot(Integer snapshot) {
        amountSetter.accept(snapshot);
        if (onChanged != null) onChanged.run();
    }
}
