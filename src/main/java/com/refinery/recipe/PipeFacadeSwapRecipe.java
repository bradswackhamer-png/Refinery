package com.refinery.recipe;

import com.refinery.RefineryMod;
import com.refinery.block.pipe.PipeFacadeHelper;
import com.refinery.item.PipeFacadeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Shapeless 1-to-1 conversion recipe between a Solid Pipe Facade and a Pass-Through (Hollow) Pipe Facade,
 * preserving the disguised block ID.
 */
public class PipeFacadeSwapRecipe extends CustomRecipe {

    public PipeFacadeSwapRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack found = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (!found.isEmpty()) {
                    return false; // Only 1 item allowed
                }
                if (!(stack.getItem() instanceof PipeFacadeItem)) {
                    return false;
                }
                if (PipeFacadeHelper.getFacadeBlock(stack) == null) {
                    return false;
                }
                found = stack;
            }
        }
        return !found.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PipeFacadeItem facadeItem) {
                Block block = PipeFacadeHelper.getFacadeBlock(stack);
                if (block != null) {
                    return PipeFacadeHelper.createFacadeStack(block, !facadeItem.isHollow(), 1);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return RefineryMod.PIPE_FACADE_SWAP_SERIALIZER.get();
    }
}
