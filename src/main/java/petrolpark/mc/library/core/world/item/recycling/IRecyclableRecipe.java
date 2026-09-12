package petrolpark.mc.library.core.world.item.recycling;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

/**
 * A {@link Recipe} which can be reversed by {@link RecyclingManager recycling}.
 * It is recommended you implement this on Recipes where finding the Ingredients used to craft an Item is more complicated than 
 * the naive inversal of {@link Recipe#getIngredients()} done in {@link RecyclingManager#getInverse(net.minecraft.world.item.crafting.Ingredient)}.
 */
@ParametersAreNonnullByDefault
public interface IRecyclableRecipe {
    
    /**
     * Get (optionally) the Items that would have been used to craft the given Item Stack with this Recipe, wrapped as {@link RecyclingOutputs}.
     * This method should not consider the size of this Item Stack (which should always be 1) or replicate the effects of any {@link RecyclingOutputsModifier}s as these will be applied separately.
     * @param level
     * @param stack
     * @return Optional containing the {@link RecyclingOutputs} (possibly {@link RecyclingOutputs#isEmpty() empty} itself) for recycling the given Item Stack as the inverse of this Recipe,
     * or an empty Optional if that Item Stack could never be produced by this Recipe and the recycling should pass to the default handling
     */
    public Optional<RecyclingOutputs> getRecyclingOutputs(Level level, ItemStack stack);

    @Nullable
    public static IRecyclableRecipe cast(Recipe<?> recipe) {
        if (recipe instanceof IRecyclableRecipe recyclableRecipe) return recyclableRecipe;
        return null;
    };

    public static boolean isInstance(Recipe<?> recipe) {
        return recipe instanceof IRecyclableRecipe;
    };
};
