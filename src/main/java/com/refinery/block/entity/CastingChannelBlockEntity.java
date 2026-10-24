package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.fluid.SingleFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class CastingChannelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 1000;

    private Fluid moltenFluid = Fluids.EMPTY;
    private int moltenAmount = 0;

    public final SingleFluidHandler fluidHandler = new SingleFluidHandler(
            () -> moltenFluid,
            () -> moltenAmount,
            this::setMoltenAmount,
            CAPACITY,
            true,
            true,
            this::setChanged
    ) {
        @Override
        public int insert(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
            if (moltenFluid == Fluids.EMPTY || moltenFluid == resource.getFluid()) {
                if (moltenFluid == Fluids.EMPTY && !resource.isEmpty()) {
                    moltenFluid = resource.getFluid();
                }
                return super.insert(index, resource, amount, transaction);
            }
            return 0;
        }
    };

    public CastingChannelBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.CASTING_CHANNEL_BE.get(), pos, blockState);
    }

    public Fluid getMoltenFluid() {
        return this.moltenFluid;
    }

    public int getMoltenAmount() {
        return this.moltenAmount;
    }

    public void setMoltenAmount(int amount) {
        this.moltenAmount = Math.max(0, Math.min(CAPACITY, amount));
        if (this.moltenAmount == 0) {
            this.moltenFluid = Fluids.EMPTY;
        }
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) return;
        if (this.moltenAmount <= 0 || this.moltenFluid == Fluids.EMPTY) return;

        // Try pushing downwards first
        boolean pushed = pushTo(Direction.DOWN);

        // Then push horizontally
        if (!pushed) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (pushTo(dir)) break;
            }
        }
    }

    private boolean pushTo(Direction dir) {
        if (this.level == null) return false;
        BlockPos targetPos = this.worldPosition.relative(dir);
        var targetHandler = this.level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
        if (targetHandler != null) {
            int toTransfer = Math.min(100, this.moltenAmount);
            FluidResource res = FluidResource.of(this.moltenFluid);
            int accepted = targetHandler.insert(0, res, toTransfer, null);
            if (accepted > 0) {
                this.moltenAmount -= accepted;
                if (this.moltenAmount <= 0) {
                    this.moltenFluid = Fluids.EMPTY;
                }
                setChanged();
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("amount", this.moltenAmount);
        if (this.moltenFluid != Fluids.EMPTY) {
            output.putString("fluid", BuiltInRegistries.FLUID.getKey(this.moltenFluid).toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.moltenAmount = input.getIntOr("amount", 0);
        String fluidId = input.getStringOr("fluid", "");
        if (!fluidId.isEmpty()) {
            Identifier id = Identifier.tryParse(fluidId);
            if (id != null) {
                this.moltenFluid = BuiltInRegistries.FLUID.get(id).map(Holder::value).orElse(Fluids.EMPTY);
            } else {
                this.moltenFluid = Fluids.EMPTY;
            }
        } else {
            this.moltenFluid = Fluids.EMPTY;
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
