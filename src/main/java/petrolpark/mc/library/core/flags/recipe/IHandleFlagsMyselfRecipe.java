package petrolpark.mc.library.core.flags.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface IHandleFlagsMyselfRecipe<I extends RecipeInput> extends Recipe<I> {
    
    public default boolean isFlagsHandled(I input, HolderLookup.Provider registries) {
        return true;
    };
};
