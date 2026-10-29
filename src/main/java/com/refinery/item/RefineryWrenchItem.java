package com.refinery.item;

import com.refinery.RefineryMod;
import com.refinery.block.*;
import com.refinery.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class RefineryWrenchItem extends Item {

    public RefineryWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void setLinkedPump(ItemStack stack, BlockPos pos) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            tag.putInt("LinkedPumpX", pos.getX());
            tag.putInt("LinkedPumpY", pos.getY());
            tag.putInt("LinkedPumpZ", pos.getZ());
        }));
    }

    public static @Nullable BlockPos getLinkedPump(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag tag = customData.copyTag();
        if (tag.contains("LinkedPumpX") && tag.contains("LinkedPumpY") && tag.contains("LinkedPumpZ")) {
            return new BlockPos(
                    tag.getIntOr("LinkedPumpX", 0),
                    tag.getIntOr("LinkedPumpY", 0),
                    tag.getIntOr("LinkedPumpZ", 0)
            );
        }
        return null;
    }

    public static void clearLinkedPump(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            tag.remove("LinkedPumpX");
            tag.remove("LinkedPumpY");
            tag.remove("LinkedPumpZ");
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos linked = getLinkedPump(stack);

        if (linked != null) {
            // Raytrace for fluids so player can click water/fluids directly
            BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos targetPos = hit.getBlockPos();
                if (!targetPos.equals(linked)) {
                    if (!level.isClientSide()) {
                        if (level.getBlockEntity(linked) instanceof FluidPumpBlockEntity pumpBe) {
                            double dist = Math.sqrt(linked.distSqr(targetPos));
                            if (dist <= FluidPumpBlockEntity.MAX_DISTANCE) {
                                pumpBe.setTargetPos(targetPos);
                                clearLinkedPump(stack);
                                player.sendSystemMessage(
                                        Component.literal(String.format("§a[Refinery] Pump intake linked to [%d, %d, %d] (Distance: %.1fm)! 3D Suction Hose deployed.",
                                                targetPos.getX(), targetPos.getY(), targetPos.getZ(), dist))
                                );
                                level.playSound(null, targetPos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
                            } else {
                                player.sendSystemMessage(
                                        Component.literal(String.format("§c[Refinery] Target is too far from Fluid Pump! Maximum distance is %.0f blocks.",
                                                FluidPumpBlockEntity.MAX_DISTANCE))
                                );
                            }
                        } else {
                            clearLinkedPump(stack);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        // 1. Shift + Right-Click on Fluid Pump: Select pump for linking (or double shift-click to dismantle)
        if (state.getBlock() instanceof FluidPumpBlock) {
            if (player != null && player.isShiftKeyDown()) {
                BlockPos alreadyLinked = getLinkedPump(stack);
                if (alreadyLinked != null && alreadyLinked.equals(pos)) {
                    // Double shift-click on same pump: Instant dismantle & pickup
                    if (!level.isClientSide()) {
                        clearLinkedPump(stack);
                        level.destroyBlock(pos, true, player);
                        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.3f);
                    }
                    return InteractionResult.SUCCESS;
                }

                // First shift-click on pump: Link mode!
                if (!level.isClientSide()) {
                    setLinkedPump(stack, pos);
                    player.sendSystemMessage(
                            Component.literal(String.format("§6[Refinery] Fluid Pump selected at [%d, %d, %d].\n§eRight-click or Sneak-click any block or fluid in the world to set pump intake point!",
                                    pos.getX(), pos.getY(), pos.getZ()))
                    );
                    level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // 2. Setting intake target if wrench is currently carrying a linked pump position
        BlockPos linkedPumpPos = getLinkedPump(stack);
        if (linkedPumpPos != null && !pos.equals(linkedPumpPos)) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(linkedPumpPos) instanceof FluidPumpBlockEntity pumpBe) {
                    double dist = Math.sqrt(linkedPumpPos.distSqr(pos));
                    if (dist <= FluidPumpBlockEntity.MAX_DISTANCE) {
                        pumpBe.setTargetPos(pos);
                        clearLinkedPump(stack);
                        if (player != null) {
                            player.sendSystemMessage(
                                    Component.literal(String.format("§a[Refinery] Pump intake linked to [%d, %d, %d] (Distance: %.1fm)! 3D Suction Hose deployed.",
                                            pos.getX(), pos.getY(), pos.getZ(), dist))
                            );
                        }
                        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    } else if (player != null) {
                        player.sendSystemMessage(
                                Component.literal(String.format("§c[Refinery] Target is too far from Fluid Pump! Maximum distance is %.0f blocks (current: %.1fm).",
                                        FluidPumpBlockEntity.MAX_DISTANCE, dist))
                        );
                    }
                } else {
                    clearLinkedPump(stack);
                    if (player != null) {
                        player.sendSystemMessage(
                                Component.literal("§c[Refinery] Linked Fluid Pump no longer exists at original position.")
                        );
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 3. Shift + Right-Click: Instant dismantle & pickup for other Refinery blocks
        if (player != null && player.isShiftKeyDown()) {
            var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (key != null && key.getNamespace().equals(RefineryMod.MODID)) {
                if (!level.isClientSide()) {
                    level.destroyBlock(pos, true, player);
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.3f);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // 4. Multiblock Controllers: Toggle 3D Blueprint Projection
        if (state.getBlock() instanceof RefineryControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof RefineryControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof BlastFurnaceControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BlastFurnaceControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof SmallBoilerControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SmallBoilerControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof LargeBoilerControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LargeBoilerControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof SteamTurbineControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SteamTurbineControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof MetalPressControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MetalPressControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof IndustrialGrinderControllerBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IndustrialGrinderControllerBlockEntity be) {
                be.toggleBlueprintProjection(player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.8f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof FluidDischargeBlock) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FluidDischargeBlockEntity be) {
                DischargeMode nextMode = be.getMode().cycle();
                be.setMode(nextMode);
                level.setBlock(pos, state.setValue(FluidDischargeBlock.MODE, nextMode), 3);
                if (player != null) {
                    if (nextMode == DischargeMode.VOID) {
                        player.sendSystemMessage(Component.literal(
                                "§e[Refinery] Fluid Discharge set to: §c§lVOID §7(Dumps incoming pipe fluids into oblivion)"
                        ));
                    } else {
                        player.sendSystemMessage(Component.literal(String.format(
                                "§e[Refinery] Fluid Discharge set to: §b§lFILL §7(Fills contiguous blocks at Y=%d)",
                                pos.getY()
                        )));
                    }
                }
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, 1.4f);
            }
            return InteractionResult.SUCCESS;
        }

        // 5. Rotate Industrial Conveyor Belt (with seamless connection recalculation & neighbor notification)
        if (state.getBlock() instanceof ConveyorBeltBlock) {
            if (!level.isClientSide()) {
                Direction currentFacing = state.getValue(ConveyorBeltBlock.FACING);
                Direction newFacing = player != null && player.isShiftKeyDown() ? currentFacing.getCounterClockWise() : currentFacing.getClockWise();
                BlockState rotated = ConveyorBeltBlock.updateConnections(state.setValue(ConveyorBeltBlock.FACING, newFacing), level, pos);
                level.setBlock(pos, rotated, 3);
                level.updateNeighborsAt(pos, state.getBlock());
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.4f, 1.4f);
            }
            return InteractionResult.SUCCESS;
        }

        // 6. Rotate horizontal directional blocks
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            if (!level.isClientSide()) {
                var currentFacing = state.getValue(HorizontalDirectionalBlock.FACING);
                var newFacing = currentFacing.getClockWise();
                level.setBlock(pos, state.setValue(HorizontalDirectionalBlock.FACING, newFacing), 3);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.4f, 1.4f);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        BlockPos linked = getLinkedPump(itemStack);
        if (linked != null) {
            builder.accept(Component.literal(String.format("§6Linked Pump: [%d, %d, %d] §7(Click block/fluid to link intake)",
                    linked.getX(), linked.getY(), linked.getZ())));
        }
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_1"));
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_2"));
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_3"));
        builder.accept(Component.translatable("tooltip.refinery.wrench.desc_4"));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
