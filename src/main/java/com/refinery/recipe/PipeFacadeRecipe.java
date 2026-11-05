package com.refinery.recipe;

import com.refinery.RefineryMod;
import com.refinery.block.pipe.PipeFacadeHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Dynamic 3x3 crafting recipe for Solid Pipe Facades:
 * - Center (1, 1): 1 eligible full opaque block item
 * - All 8 outer slots: Copper Item Pipe (refinery:copper_item_pipe)
 * Produces: 8 Solid Pipe Facades carrying the center block's ID.
 */
public class PipeFacadeRecipe extends CustomRecipe {

    public static final PipeFacadeRecipe INSTANCE = new PipeFacadeRecipe();

    public PipeFacadeRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }
        ItemStack center = input.getItem(1, 1);
        Block block = PipeFacadeHelper.getBlockFromItem(center);
        if (block == null || !PipeFacadeHelper.isEligibleBlock(block)) {
            return false;
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1) continue;
                ItemStack slot = input.getItem(col, row);
                if (!slot.is(RefineryMod.COPPER_ITEM_PIPE_ITEM.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack center = input.getItem(1, 1);
        Block block = PipeFacadeHelper.getBlockFromItem(center);
        if (block == null || !PipeFacadeHelper.isEligibleBlock(block)) {
            return ItemStack.EMPTY;
        }
        return PipeFacadeHelper.createFacadeStack(block, false, 8);
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return RefineryMod.PIPE_FACADE_RECIPE_SERIALIZER.get();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PipeFacadeRecipe;
    }

    @Override
    public int hashCode() {
        return PipeFacadeRecipe.class.hashCode();
    }
}
