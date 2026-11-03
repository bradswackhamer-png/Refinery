package com.refinery.block.entity;

import com.refinery.RefineryMod;
import com.refinery.block.CastingChannelBlock;
import com.refinery.fluid.SingleFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class CastingChannelBlockEntity extends BlockEntity {
    public static final int CAPACITY = 1000;

    private Fluid moltenFluid = Fluids.EMPTY;
    private int moltenAmount = 0;
    private @Nullable Direction lastInputSide = null;
    private long lastInputTick = 0;
    private int activeFlowTicks = 0;

    public final SingleFluidHandler fluidHandler = createFluidHandler(null);

    public SingleFluidHandler getFluidHandler(@Nullable Direction side) {
        return createFluidHandler(side);
    }

    private SingleFluidHandler createFluidHandler(@Nullable Direction side) {
        return new SingleFluidHandler(
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
                    int inserted = super.insert(index, resource, amount, transaction);
                    if (inserted > 0 && side != null) {
                        recordInputSide(side);
                    }
                    return inserted;
                }
                return 0;
            }
        };
    }

    public CastingChannelBlockEntity(BlockPos pos, BlockState blockState) {
        super(RefineryMod.CASTING_CHANNEL_BE.get(), pos, blockState);
    }

    public void recordInputSide(@Nullable Direction side) {
        if (side != null) {
            this.lastInputSide = side;
            if (this.level != null) {
                this.lastInputTick = this.level.getGameTime();
            }
        }
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
        if (this.moltenAmount <= 0 || this.moltenFluid == Fluids.EMPTY) {
            this.activeFlowTicks = 0;
            return;
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CastingChannelBlock)) return;

        this.activeFlowTicks++;
        // Lava-like viscous flow propagation delay: wait 25 ticks (1.25s) before cascading downstream
        if (this.activeFlowTicks < 25) {
            return;
        }

        // Transfer in small rhythmic increments every 4 ticks to simulate creeping molten flow
        if (this.level.getGameTime() % 4 != 0) {
            return;
        }

        // 1. Try pushing DOWN first (Gravity discharge into lower channel, mold, basin, or tank)
        if (state.getValue(CastingChannelBlock.DOWN) && pushTo(Direction.DOWN)) {
            return;
        }

        // 2. Push to connected horizontal sides EXCLUDING the side fluid entered from (lastInputSide)
        Direction ignoreSide = null;
        if (this.lastInputSide != null && (this.level.getGameTime() - this.lastInputTick < 60)) {
            ignoreSide = this.lastInputSide;
        }

        boolean pushed = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir == ignoreSide) continue;
            if (isConnected(state, dir)) {
                if (pushTo(dir)) {
                    pushed = true;
                    if (this.moltenAmount <= 0) break;
                }
            }
        }

        // 3. Fallback: If forward is blocked/full for over 60 ticks (3 seconds), allow equalizing backwards
        if (!pushed && ignoreSide != null && isConnected(state, ignoreSide)) {
            if (this.level.getGameTime() - this.lastInputTick > 60) {
                pushTo(ignoreSide);
            }
        }
    }

    private boolean isConnected(BlockState state, Direction dir) {
        return switch (dir) {
            case NORTH -> state.getValue(CastingChannelBlock.NORTH);
            case SOUTH -> state.getValue(CastingChannelBlock.SOUTH);
            case WEST -> state.getValue(CastingChannelBlock.WEST);
            case EAST -> state.getValue(CastingChannelBlock.EAST);
            case DOWN -> state.getValue(CastingChannelBlock.DOWN);
            default -> false;
        };
    }

    private boolean pushTo(Direction dir) {
        if (this.level == null) return false;
        BlockPos targetPos = this.worldPosition.relative(dir);
        var targetHandler = this.level.getCapability(Capabilities.Fluid.BLOCK, targetPos, dir.getOpposite());
        if (targetHandler != null) {
            // While actively fed from upstream, retain a 150 mB visual channel pool so the line remains filled & glowing
            boolean activelyFed = this.lastInputSide != null && (this.level.getGameTime() - this.lastInputTick < 40);
            int available = activelyFed ? Math.max(0, this.moltenAmount - 150) : this.moltenAmount;
            if (available <= 0) return false;

            int toTransfer = Math.min(25, available);
            FluidResource res = FluidResource.of(this.moltenFluid);
            try (Transaction tx = Transaction.openRoot()) {
                int accepted = 0;
                for (int i = 0; i < targetHandler.size(); i++) {
                    int ins = targetHandler.insert(i, res, toTransfer - accepted, tx);
                    accepted += ins;
                    if (accepted >= toTransfer) break;
                }
                if (accepted > 0) {
                    tx.commit();
                    this.moltenAmount -= accepted;
                    if (this.moltenAmount <= 0) {
                        this.moltenFluid = Fluids.EMPTY;
                        this.activeFlowTicks = 0;
                    }
                    setChanged();

                    // Notify receiving channel of the input direction to keep forward cascading flow
                    if (this.level.getBlockEntity(targetPos) instanceof CastingChannelBlockEntity targetChannel) {
                        targetChannel.recordInputSide(dir.getOpposite());
                    }

                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
                    return true;
                }
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
        if (this.lastInputSide != null) {
            output.putString("lastInputSide", this.lastInputSide.getSerializedName());
        }
        output.putLong("lastInputTick", this.lastInputTick);
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
        String sideName = input.getStringOr("lastInputSide", "");
        this.lastInputSide = !sideName.isEmpty() ? Direction.byName(sideName) : null;
        this.lastInputTick = input.getLongOr("lastInputTick", 0L);
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
