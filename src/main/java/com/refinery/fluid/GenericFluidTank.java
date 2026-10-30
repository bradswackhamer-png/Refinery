package com.refinery.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntSupplier;

public class GenericFluidTank extends SnapshotJournal<GenericFluidTank.TankSnapshot> implements ResourceHandler<FluidResource> {
    public record TankSnapshot(Fluid fluid, int amount) {}

    private Fluid storedFluid = Fluids.EMPTY;
    private int amount = 0;
    private final IntSupplier capacitySupplier;
    private final boolean canInsert;
    private final boolean canExtract;
    private final Runnable onChanged;

    public GenericFluidTank(IntSupplier capacitySupplier, boolean canInsert, boolean canExtract, Runnable onChanged) {
        this.capacitySupplier = capacitySupplier;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
        this.onChanged = onChanged;
    }

    public Fluid getStoredFluid() {
        return this.storedFluid;
    }

    public void setStoredFluid(Fluid fluid) {
        this.storedFluid = fluid != null ? fluid : Fluids.EMPTY;
        if (this.storedFluid == Fluids.EMPTY) {
            this.amount = 0;
        }
        if (this.onChanged != null) this.onChanged.run();
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        int cap = this.capacitySupplier.getAsInt();
        this.amount = Math.clamp(amount, 0, cap);
        if (this.amount == 0) {
            this.storedFluid = Fluids.EMPTY;
        }
        if (this.onChanged != null) this.onChanged.run();
    }

    public int getCapacity() {
        return this.capacitySupplier.getAsInt();
    }

    public void dump() {
        this.amount = 0;
        this.storedFluid = Fluids.EMPTY;
        if (this.onChanged != null) this.onChanged.run();
    }

    @Override
    protected TankSnapshot createSnapshot() {
        return new TankSnapshot(this.storedFluid, this.amount);
    }

    @Override
    protected void revertToSnapshot(TankSnapshot snapshot) {
        this.storedFluid = snapshot.fluid();
        this.amount = snapshot.amount();
        if (this.onChanged != null) this.onChanged.run();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        if (index != 0 || this.storedFluid == Fluids.EMPTY || this.amount <= 0) {
            return FluidResource.EMPTY;
        }
        return FluidResource.of(this.storedFluid);
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? this.amount : 0L;
    }

    @Override
    public int getAmountAsInt(int index) {
        return index == 0 ? this.amount : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return this.capacitySupplier.getAsInt();
    }

    @Override
    public int getCapacityAsInt(int index, FluidResource resource) {
        return this.capacitySupplier.getAsInt();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if (index != 0 || !this.canInsert) return false;
        if (resource.isEmpty()) return true;
        if (this.amount <= 0 || this.storedFluid == Fluids.EMPTY) return true;
        return resource.getFluid() == this.storedFluid;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !this.canInsert || amount <= 0 || resource.isEmpty()) return 0;
        Fluid incoming = resource.getFluid();
        if (incoming == Fluids.EMPTY) return 0;

        if (this.amount > 0 && this.storedFluid != Fluids.EMPTY && this.storedFluid != incoming) {
            return 0; // Type mismatch
        }

        int cap = this.capacitySupplier.getAsInt();
        int space = cap - this.amount;
        int toInsert = Math.min(space, amount);
        if (toInsert <= 0) return 0;

        updateSnapshots(transaction);
        this.storedFluid = incoming;
        this.amount += toInsert;
        if (this.onChanged != null) this.onChanged.run();
        return toInsert;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || !this.canExtract || amount <= 0 || this.amount <= 0 || this.storedFluid == Fluids.EMPTY) return 0;
        if (!resource.isEmpty() && resource.getFluid() != this.storedFluid) return 0;

        int toExtract = Math.min(this.amount, amount);
        if (toExtract <= 0) return 0;

        updateSnapshots(transaction);
        this.amount -= toExtract;
        if (this.amount <= 0) {
            this.storedFluid = Fluids.EMPTY;
        }
        if (this.onChanged != null) this.onChanged.run();
        return toExtract;
    }
}
