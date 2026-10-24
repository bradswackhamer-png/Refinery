package com.refinery.entity;

import com.refinery.RefineryMod;
import com.refinery.gate.PipeWireColor;
import com.refinery.item.PipeWireItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PipeWireEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_WIRES = SynchedEntityData.defineId(PipeWireEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SIGNALS = SynchedEntityData.defineId(PipeWireEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_PIPE_POS = SynchedEntityData.defineId(PipeWireEntity.class, EntityDataSerializers.BLOCK_POS);

    private int tickCount = 0;

    public PipeWireEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public PipeWireEntity(EntityType<?> entityType, Level level, BlockPos pipePos) {
        this(entityType, level);
        setPipePos(pipePos);
        setPos(pipePos.getX() + 0.5, pipePos.getY() + 0.5, pipePos.getZ() + 0.5);
        setBoundingBox(new AABB(
                pipePos.getX() + 0.25, pipePos.getY() + 0.25, pipePos.getZ() + 0.25,
                pipePos.getX() + 0.75, pipePos.getY() + 0.75, pipePos.getZ() + 0.75
        ));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_WIRES, 0);
        builder.define(DATA_SIGNALS, 0);
        builder.define(DATA_PIPE_POS, BlockPos.ZERO);
    }

    public BlockPos getPipePos() {
        return this.entityData.get(DATA_PIPE_POS);
    }

    public void setPipePos(BlockPos pos) {
        this.entityData.set(DATA_PIPE_POS, pos);
    }

    public boolean hasWire(PipeWireColor color) {
        int mask = this.entityData.get(DATA_WIRES);
        return (mask & (1 << color.ordinal())) != 0;
    }

    public void setWirePresent(PipeWireColor color, boolean present) {
        int mask = this.entityData.get(DATA_WIRES);
        if (present) {
            mask |= (1 << color.ordinal());
        } else {
            mask &= ~(1 << color.ordinal());
        }
        this.entityData.set(DATA_WIRES, mask);
    }

    public boolean isSignalActive(PipeWireColor color) {
        int mask = this.entityData.get(DATA_SIGNALS);
        return (mask & (1 << color.ordinal())) != 0;
    }

    public void setSignalActive(PipeWireColor color, boolean active) {
        int mask = this.entityData.get(DATA_SIGNALS);
        if (active) {
            mask |= (1 << color.ordinal());
        } else {
            mask &= ~(1 << color.ordinal());
        }
        this.entityData.set(DATA_SIGNALS, mask);
    }

    public void clearSignals() {
        this.entityData.set(DATA_SIGNALS, 0);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack held = player.getItemInHand(hand);

        // 1. Wrench: dismantle & drop all wires
        if (held.is(RefineryMod.REFINERY_WRENCH.get())) {
            if (!level().isClientSide()) {
                dropAllWires();
                discard();
                level().playSound(null, blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }

        // 2. Additional wire: attach new color to this pipe
        if (held.getItem() instanceof PipeWireItem wireItem) {
            PipeWireColor color = wireItem.getColor();
            if (!hasWire(color)) {
                if (!level().isClientSide()) {
                    setWirePresent(color, true);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                    level().playSound(null, blockPosition(), SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 1.0f, 1.2f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource damageSource, float amount) {
        if (!isRemoved()) {
            dropAllWires();
            discard();
            level().playSound(null, blockPosition(), SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickCount++;

        if (!level().isClientSide() && this.tickCount % 20 == 0) {
            BlockPos pipePos = getPipePos();
            BlockState state = level().getBlockState(pipePos);
            if (!PipeWireItem.isPipeOrHost(level(), pipePos, state)) {
                dropAllWires();
                discard();
                return;
            }
            // If all wires were removed, discard entity
            if (this.entityData.get(DATA_WIRES) == 0) {
                discard();
            }
        }
    }

    public void dropAllWires() {
        if (level().isClientSide()) return;
        BlockPos pos = blockPosition();
        for (PipeWireColor color : PipeWireColor.values()) {
            if (hasWire(color)) {
                ItemStack stack = switch (color) {
                    case RED -> new ItemStack(RefineryMod.PIPE_WIRE_RED.get());
                    case BLUE -> new ItemStack(RefineryMod.PIPE_WIRE_BLUE.get());
                    case GREEN -> new ItemStack(RefineryMod.PIPE_WIRE_GREEN.get());
                    case YELLOW -> new ItemStack(RefineryMod.PIPE_WIRE_YELLOW.get());
                };
                spawnAtLocation(serverLevel(), stack);
            }
        }
    }

    private net.minecraft.server.level.ServerLevel serverLevel() {
        return (net.minecraft.server.level.ServerLevel) level();
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        int wires = input.getIntOr("Wires", 0);
        int px = input.getIntOr("PipeX", 0);
        int py = input.getIntOr("PipeY", 0);
        int pz = input.getIntOr("PipeZ", 0);
        this.entityData.set(DATA_WIRES, wires);
        setPipePos(new BlockPos(px, py, pz));
        setPos(px + 0.5, py + 0.5, pz + 0.5);
        setBoundingBox(new AABB(px + 0.25, py + 0.25, pz + 0.25, px + 0.75, py + 0.75, pz + 0.75));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Wires", this.entityData.get(DATA_WIRES));
        BlockPos pp = getPipePos();
        output.putInt("PipeX", pp.getX());
        output.putInt("PipeY", pp.getY());
        output.putInt("PipeZ", pp.getZ());
    }
}
