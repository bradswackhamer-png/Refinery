package com.refinery.mixin;

import com.refinery.entity.PipeGateEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void refinery$getGateSignal(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (PipeGateEntity.isEmittingRedstone(level, pos, direction)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "getDirectSignal", at = @At("HEAD"), cancellable = true)
    private void refinery$getGateDirectSignal(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (PipeGateEntity.isEmittingRedstone(level, pos, direction)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "isSignalSource", at = @At("HEAD"), cancellable = true)
    private void refinery$isSignalSource(CallbackInfoReturnable<Boolean> cir) {
        if (PipeGateEntity.hasAnyPoweredGates()) {
            cir.setReturnValue(true);
        }
    }
}
