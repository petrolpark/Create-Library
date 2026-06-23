package petrolpark.mc.library.core.flags.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

@ParametersAreNonnullByDefault
public interface IHandleFlagsMyselfRecipe<I extends RecipeInput> extends Recipe<I> {
    
    public default boolean areFlagsHandled(I input, HolderLookup.Provider registries) {
        return true;
    };
};
