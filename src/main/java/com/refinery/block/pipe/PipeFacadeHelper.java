package com.refinery.block.pipe;

import com.refinery.RefineryMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class PipeFacadeHelper {

    private PipeFacadeHelper() {}

    public record FacadeData(Block block, boolean hollow) {
        public Identifier getBlockId() {
            return BuiltInRegistries.BLOCK.getKey(block);
        }

        public ItemStack toItemStack(int count) {
            Item item = hollow ? RefineryMod.PIPE_FACADE_HOLE.get() : RefineryMod.PIPE_FACADE.get();
            return createFacadeStack(item, block, count);
        }
    }

    /**
     * Checks whether a block is eligible to be crafted and rendered as a Pipe Facade.
     * Excludes non-full cubes, tile entities, falling blocks, liquids, glass/ice/leaves/grass,
     * and multiblock machine components.
     */
    public static boolean isEligibleBlock(@Nullable Block block) {
        if (block == null || block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR || block == Blocks.BARRIER) {
            return false;
        }
        if (block instanceof EntityBlock || block instanceof Fallable || block instanceof LiquidBlock ||
            block instanceof TransparentBlock || block instanceof LeavesBlock) {
            return false;
        }

        BlockState state = block.defaultBlockState();
        if (state.isAir() || !state.canOcclude()) {
            return false;
        }

        try {
            if (!Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))) {
                return false;
            }
            if (!Block.isShapeFullBlock(state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return false;

        String path = id.getPath().toLowerCase();
        // Exclude glass, ice, leaves, grass/tinted blocks, slime/honey, redstone, and problematic blocks
        if (path.contains("glass") || path.contains("ice") || path.contains("leaves") ||
            path.equals("grass_block") || path.equals("mycelium") || path.equals("podzol") ||
            path.contains("slime") || path.contains("honey") || path.equals("tnt") ||
            path.contains("spawner") || path.contains("portal") || path.contains("command_block") ||
            path.contains("jigsaw") || path.contains("structure_block") || path.contains("redstone_lamp") ||
            path.contains("observer") || path.contains("piston") || path.contains("Sticky_piston") ||
            path.contains("mangrove_roots") || path.contains("sculk") || path.contains("chorus") ||
            path.contains("sponge") || path.contains("cactus") || path.contains("pumpkin") ||
            path.contains("melon") || path.contains("hay_block") || path.contains("target") ||
            path.contains("Respawn_anchor") || path.contains("lodestone") || path.contains("bee_nest") ||
            path.contains("beehive") || path.contains("infested_")) {
            return false;
        }

        // For refinery blocks, only allow decorative building blocks (Corrugated Steel Panels, Ore/Metal storage blocks, Oil Shale)
        if (RefineryMod.MODID.equals(id.getNamespace())) {
            return path.contains("corrugated_steel_panel") ||
                   path.equals("tin_block") ||
                   path.equals("bronze_block") ||
                   path.equals("raw_tin_block") ||
                   path.equals("tin_ore") ||
                   path.equals("deepslate_tin_ore") ||
                   path.equals("oil_shale") ||
                   path.equals("reinforced_casing") ||
                   path.equals("blast_furnace_casing") ||
                   path.equals("steel_boiler_casing") ||
                   path.equals("turbine_casing") ||
                   path.equals("press_casing") ||
                   path.equals("grinder_casing") ||
                   path.equals("melter_casing") ||
                   path.equals("casting_casing") ||
                   path.equals("smelter_casing") ||
                   path.equals("storage_tank_casing");
        }

        // Must have a corresponding BlockItem so it exists as an item
        Item item = block.asItem();
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return false;
        }

        return true;
    }

    @Nullable
    public static Block getBlockFromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block b = blockItem.getBlock();
            if (isEligibleBlock(b)) {
                return b;
            }
        }
        return null;
    }

    public static Block getFacadeBlock(ItemStack stack) {
        if (stack.isEmpty()) return Blocks.STONE;
        String idStr = stack.get(RefineryMod.FACADE_BLOCK.get());
        if (idStr != null && !idStr.isEmpty()) {
            Identifier id = Identifier.tryParse(idStr);
            if (id != null) {
                Block block = BuiltInRegistries.BLOCK.getValue(id);
                if (block != null && block != Blocks.AIR) {
                    return block;
                }
            }
        }
        return RefineryMod.CORRUGATED_STEEL_PANEL.get();
    }

    public static ItemStack createFacadeStack(Item facadeItem, Block targetBlock, int count) {
        ItemStack stack = new ItemStack(facadeItem, count);
        Identifier id = BuiltInRegistries.BLOCK.getKey(targetBlock);
        if (id != null) {
            stack.set(RefineryMod.FACADE_BLOCK.get(), id.toString());
        }
        return stack;
    }

    public static ItemStack createFacadeStack(Block targetBlock, boolean hollow, int count) {
        Item facadeItem = hollow ? RefineryMod.PIPE_FACADE_HOLE.get() : RefineryMod.PIPE_FACADE.get();
        return createFacadeStack(facadeItem, targetBlock, count);
    }

    public static List<Block> getAllEligibleBlocks() {
        List<Block> result = new ArrayList<>();
        // Put our Corrugated Steel Panels first so they appear prominently at the top of the facade list
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (id != null && RefineryMod.MODID.equals(id.getNamespace()) && isEligibleBlock(block)) {
                result.add(block);
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (id != null && !RefineryMod.MODID.equals(id.getNamespace()) && isEligibleBlock(block)) {
                result.add(block);
            }
        }
        return result;
    }

    /**
     * Resolves which face of a pipe block a player is targeting when placing or removing a facade.
     * If the player clicked directly on an outer facade slab, hitResult.getDirection() or the vector from block center
     * identifies that face. If clicking the center pipe hub, uses hitResult.getDirection().
     */
    public static Direction resolveTargetFace(BlockPos pos, net.minecraft.world.phys.BlockHitResult hitResult) {
        double dx = hitResult.getLocation().x - (pos.getX() + 0.5);
        double dy = hitResult.getLocation().y - (pos.getY() + 0.5);
        double dz = hitResult.getLocation().z - (pos.getZ() + 0.5);

        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);

        // If the hit point is near the outer boundary of the block (> 0.35 from center), pick that outer face
        double max = Math.max(ax, Math.max(ay, az));
        if (max > 0.34) {
            if (max == ax) return dx > 0 ? Direction.EAST : Direction.WEST;
            if (max == ay) return dy > 0 ? Direction.UP : Direction.DOWN;
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        return hitResult.getDirection();
    }
}
