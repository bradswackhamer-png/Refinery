package com.refinery.item;

import com.refinery.block.pipe.BufferPipeBlock;
import com.refinery.block.pipe.IndustrialPipeBlock;
import com.refinery.block.pipe.IndustrialPipeMaterial;
import com.refinery.block.pipe.IndustrialPipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class PipeBlockItem extends BlockItem {

    public PipeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        Block block = getBlock();
        if (block instanceof BufferPipeBlock) {
            builder.accept(Component.literal("§6Type: §fInline Buffer Storage"));
            builder.accept(Component.literal("§aCapacity: §f27 Stacks §7(1 internal slot)"));
            builder.accept(Component.literal("§7Buffers items inline and smoothly auto-forwards"));
            builder.accept(Component.literal("§7them to connected pipes and machines."));
            super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
            return;
        }

        if (block instanceof IndustrialPipeBlock pipe) {
            IndustrialPipeType type = pipe.getPipeType();
            IndustrialPipeMaterial material = pipe.getMaterial();

            if (type == IndustrialPipeType.STRUCTURE || material == IndustrialPipeMaterial.STRUCTURE) {
                builder.accept(Component.literal("§6Type: §fStructural Support Pipe"));
                builder.accept(Component.literal("§7Cheap structural conduit that does not transfer"));
                builder.accept(Component.literal("§7items or fluids. Ideal for routing Pipe Wires,"));
                builder.accept(Component.literal("§7mounting Pipe Gates, or holding Pipe Facades."));
            } else if (type == IndustrialPipeType.VOID) {
                builder.accept(Component.literal("§cType: §fDisposal / Void Conduit"));
                builder.accept(Component.literal("§bRate: §fInstant Void §7(Unlimited)"));
                builder.accept(Component.literal("§7Instantly deletes and destroys all incoming items"));
                builder.accept(Component.literal("§7and fluids to prevent pipe blockages and overflow."));
            } else if (type == IndustrialPipeType.VALVE || material == IndustrialPipeMaterial.VALVE) {
                builder.accept(Component.literal("§6Type: §fRedstone Valve Pipe"));
                builder.accept(Component.literal("§bCapacity: §f500 mB §8| §eItems: §f16/t"));
                builder.accept(Component.literal("§7Controls item & fluid transit using Redstone or Pipe Gates."));
                builder.accept(Component.literal("§7Right-click with Wrench to invert mode"));
                builder.accept(Component.literal("§8(Normal: Closes on signal | Inverted: Opens on signal)"));
            } else if (material == IndustrialPipeMaterial.WOOD) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fFiltered Extraction Pipe"));
                    builder.accept(Component.literal("§aExtraction: §fPulse Gate Only §7(1, 5, or 10 / pulse)"));
                    builder.accept(Component.literal("§eSpeed: §f0.75x §7(0.06 blocks/t)"));
                    builder.accept(Component.literal("§7Right-click to configure 9-slot item filter."));
                    builder.accept(Component.literal("§7Requires an active Autarchic Pulse Gate to pull."));
                } else {
                    builder.accept(Component.literal("§6Type: §fFluid Extraction Pipe"));
                    builder.accept(Component.literal("§bExtraction Rate: §f100 mB/t §7(Capacity: 250 mB)"));
                    builder.accept(Component.literal("§7Passively pumps liquids from connected containers."));
                }
            } else if (material == IndustrialPipeMaterial.QUARTZ) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fMerge Funnel Pipe"));
                    builder.accept(Component.literal("§eSpeed: §f1.75x §7(0.14 blocks/t)"));
                    builder.accept(Component.literal("§7Funnels up to 5 incoming branches into 1 output."));
                    builder.accept(Component.literal("§7Right-click a face to select output direction."));
                } else {
                    builder.accept(Component.literal("§6Type: §fFluid Merge Funnel Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f800 mB/t §7(Capacity: 400 mB)"));
                    builder.accept(Component.literal("§7Funnels up to 5 fluid lines into 1 output."));
                    builder.accept(Component.literal("§7Right-click a face to select output direction."));
                }
            } else if (material == IndustrialPipeMaterial.DIAMOND) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fDirectional Sorting Filter"));
                    builder.accept(Component.literal("§eSpeed: §f2.0x §7(0.16 blocks/t)"));
                    builder.accept(Component.literal("§7Right-click to configure per-face color filters."));
                    builder.accept(Component.literal("§7Routes matching items to specific output directions."));
                } else {
                    builder.accept(Component.literal("§6Type: §fHigh-Capacity Fluid Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f1,000 mB/t §7(Capacity: 500 mB)"));
                    builder.accept(Component.literal("§7Diamond-reinforced pipe for high-volume liquid flow."));
                }
            } else if (material == IndustrialPipeMaterial.GOLD) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fPriority Insertion & Boost Pipe"));
                    builder.accept(Component.literal("§eSpeed: §f1.5x §7(0.12 blocks/t)"));
                    builder.accept(Component.literal("§7Prioritizes feeding adjacent containers/machines"));
                    builder.accept(Component.literal("§7until full before passing items down the line."));
                } else {
                    builder.accept(Component.literal("§6Type: §fHigh-Speed Fluid Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f800 mB/t §7(Capacity: 400 mB)"));
                    builder.accept(Component.literal("§7Low friction gold lining for rapid fluid transport."));
                }
            } else if (material == IndustrialPipeMaterial.EMERALD) {
                builder.accept(Component.literal("§6Type: §fRound-Robin Load Balancer"));
                builder.accept(Component.literal("§eSpeed: §f2.0x §7(0.16 blocks/t)"));
                builder.accept(Component.literal("§7Evenly cycles item distribution across all"));
                builder.accept(Component.literal("§7connected output branches."));
            } else if (material == IndustrialPipeMaterial.COPPER) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fBasic Item Transport Pipe"));
                    builder.accept(Component.literal("§eSpeed: §f1.0x §7(0.08 blocks/t)"));
                    builder.accept(Component.literal("§7Standard copper conduit for item logistics."));
                } else {
                    builder.accept(Component.literal("§6Type: §fBasic Fluid Transport Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f250 mB/t §7(Capacity: 300 mB)"));
                    builder.accept(Component.literal("§7Standard copper pipeline for fluids."));
                }
            } else if (material == IndustrialPipeMaterial.IRON) {
                if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fDirectional One-Way Pipe"));
                    builder.accept(Component.literal("§eSpeed: §f1.75x §7(0.14 blocks/t)"));
                    builder.accept(Component.literal("§7Prevents item backflow. Items only travel"));
                    builder.accept(Component.literal("§7forward in the configured direction."));
                } else {
                    builder.accept(Component.literal("§6Type: §fOne-Way Fluid Valve Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f500 mB/t §7(Capacity: 400 mB)"));
                    builder.accept(Component.literal("§7Check valve pipe allowing fluid flow in one direction."));
                }
            } else if (material == IndustrialPipeMaterial.STEEL) {
                if (type == IndustrialPipeType.FLUID_EXTRACTOR) {
                    builder.accept(Component.literal("§6Type: §fActive Fluid Extractor Pipe"));
                    builder.accept(Component.literal("§bPumping Rate: §f1,500 mB/t §7(Capacity: 500 mB)"));
                    builder.accept(Component.literal("§7Actively pumps fluids from connected tanks/machines."));
                } else if (type == IndustrialPipeType.ITEM_EXTRACTOR) {
                    builder.accept(Component.literal("§6Type: §fActive Item Extractor Pipe"));
                    builder.accept(Component.literal("§aExtraction Rate: §f1 Item / tick"));
                    builder.accept(Component.literal("§eSpeed: §f2.5x §7(0.20 blocks/t)"));
                    builder.accept(Component.literal("§7Actively pulls items from connected inventories."));
                } else if (type.handlesItems()) {
                    builder.accept(Component.literal("§6Type: §fReinforced High-Speed Pipe"));
                    builder.accept(Component.literal("§eSpeed: §f2.5x §7(0.20 blocks/t)"));
                    builder.accept(Component.literal("§7Heavy industrial pipe for maximum item velocity."));
                } else {
                    builder.accept(Component.literal("§6Type: §fHigh-Pressure Fluid Pipe"));
                    builder.accept(Component.literal("§bTransfer Rate: §f1,500 mB/t §7(Capacity: 500 mB)"));
                    builder.accept(Component.literal("§7Reinforced steel conduit for high-pressure transfer."));
                }
            } else if (material == IndustrialPipeMaterial.CAST_IRON) {
                builder.accept(Component.literal("§6Type: §fHeavy Fluid Conduit"));
                builder.accept(Component.literal("§bTransfer Rate: §f150 mB/t §7(Capacity: 250 mB)"));
                builder.accept(Component.literal("§7Durable cast iron conduit for heavy fluid routing."));
            } else if (material == IndustrialPipeMaterial.BRASS) {
                builder.accept(Component.literal("§6Type: §fPrecision Item Transport"));
                builder.accept(Component.literal("§eSpeed: §f1.5x §7(0.12 blocks/t)"));
                builder.accept(Component.literal("§7Precision-machined brass conduit for factory logistics."));
            }
        }

        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
