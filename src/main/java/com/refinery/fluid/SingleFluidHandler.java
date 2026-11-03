package com.refinery.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SingleFluidHandler extends SnapshotJournal<SingleFluidHandler.FluidSnapshot> implements ResourceHandler<FluidResource> {
    public record FluidSnapshot(int amount, Fluid fluid) {}

    private final Supplier<Fluid> fluidSupplier;
    private final @Nullable Consumer<Fluid> fluidSetter;
    private final IntSupplier amountGetter;
    private final IntConsumer amountSetter;
    private final IntSupplier capacitySupplier;
    private final boolean canInsert;
    private final boolean canExtract;
    private final Runnable onChanged;
    private final @Nullable Predicate<Fluid> fluidFilter;

    public SingleFluidHandler(Supplier<Fluid> fluidSupplier,
                              IntSupplier amountGetter,
                              IntConsumer amountSetter,
                              int capacity,
                              boolean canInsert,
                              boolean canExtract,
                              Runnable onChanged) {
        this(fluidSupplier, null, amountGetter, amountSetter, () -> capacity, canInsert, canExtract, onChanged, null);
    }

    public SingleFluidHandler(Supplier<Fluid> fluidSupplier,
                              IntSupplier amountGetter,
                              IntConsumer amountSetter,
                              IntSupplier capacitySupplier,
                              boolean canInsert,
                              boolean canExtract,
                              Runnable onChanged) {
        this(fluidSupplier, null, amountGetter, amountSetter, capacitySupplier, canInsert, canExtract, onChanged, null);
    }

    public SingleFluidHandler(Supplier<Fluid> fluidSupplier,
                              @Nullable Consumer<Fluid> fluidSetter,
                              IntSupplier amountGetter,
                              IntConsumer amountSetter,
                              int capacity,
                              boolean canInsert,
                              boolean canExtract,
                              Runnable onChanged,
                              @Nullable Predicate<Fluid> fluidFilter) {
        this(fluidSupplier, fluidSetter, amountGetter, amountSetter, () -> capacity, canInsert, canExtract, onChanged, fluidFilter);
    }

    public SingleFluidHandler(Supplier<Fluid> fluidSupplier,
                              @Nullable Consumer<Fluid> fluidSetter,
                              IntSupplier amountGetter,
                              IntConsumer amountSetter,
                              IntSupplier capacitySupplier,
                              boolean canInsert,
                              boolean canExtract,
                              Runnable onChanged,
                              @Nullable Predicate<Fluid> fluidFilter) {
        this.fluidSupplier = fluidSupplier;
        this.fluidSetter = fluidSetter;
        this.amountGetter = amountGetter;
        this.amountSetter = amountSetter;
        this.capacitySupplier = capacitySupplier;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
        this.onChanged = onChanged;
        this.fluidFilter = fluidFilter;
    }

    public int getCapacity() {
        return this.capacitySupplier.getAsInt();
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
        return getCapacity();
    }

    @Override
    public int getCapacityAsInt(int index, FluidResource resource) {
        return getCapacity();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if (index != 0 || !canInsert) return false;
        if (resource.isEmpty()) return true;
        Fluid current = fluidSupplier.get();
        if (current == null || current == Fluids.EMPTY) {
            return fluidSetter != null && (fluidFilter == null || fluidFilter.test(resource.getFluid()));
        }
        return resource.getFluid() == current;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !canInsert || amount <= 0 || resource.isEmpty()) return 0;
        Fluid current = fluidSupplier.get();
        if (current != null && current != Fluids.EMPTY) {
            if (resource.getFluid() != current) return 0;
        } else {
            // Tank is currently empty
            if (fluidSetter == null) return 0;
            if (fluidFilter != null && !fluidFilter.test(resource.getFluid())) return 0;
        }

        int currentAmount = amountGetter.getAsInt();
        int space = getCapacity() - currentAmount;
        int toInsert = Math.min(space, amount);
        if (toInsert <= 0) return 0;

        updateSnapshots(transaction);
        if ((current == null || current == Fluids.EMPTY) && fluidSetter != null) {
            fluidSetter.accept(resource.getFluid());
        }
        amountSetter.accept(currentAmount + toInsert);
        int after = amountGetter.getAsInt();
        int actualInserted = Math.max(0, after - currentAmount);
        if (onChanged != null && actualInserted > 0) onChanged.run();
        return actualInserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !canExtract || amount <= 0) return 0;
        Fluid fluid = fluidSupplier.get();
        if (fluid == null || fluid == Fluids.EMPTY) return 0;
        if (!resource.isEmpty() && resource.getFluid() != fluid) return 0;

        int current = amountGetter.getAsInt();
        int toExtract = Math.min(current, amount);
        if (toExtract <= 0) return 0;

        updateSnapshots(transaction);
        int newAmount = current - toExtract;
        amountSetter.accept(newAmount);
        if (newAmount <= 0 && fluidSetter != null) {
            fluidSetter.accept(Fluids.EMPTY);
        }
        int after = amountGetter.getAsInt();
        int actualExtracted = Math.max(0, current - after);
        if (onChanged != null && actualExtracted > 0) onChanged.run();
        return actualExtracted;
    }

    @Override
    protected FluidSnapshot createSnapshot() {
        return new FluidSnapshot(amountGetter.getAsInt(), fluidSupplier.get());
    }

    @Override
    protected void revertToSnapshot(FluidSnapshot snapshot) {
        if (fluidSetter != null) {
            fluidSetter.accept(snapshot.fluid());
        }
        amountSetter.accept(snapshot.amount());
        if (onChanged != null) onChanged.run();
    }
}
