package petrolpark.mc.library.core.world.item.recycling;

import javax.annotation.Nonnull;

import petrolpark.mc.library.registry.PetrolparkRecipeSerializers;
import petrolpark.mc.library.registry.PetrolparkRecipeTypes;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Directly {@link RecyclingManager recycle} an Item into known {@link RecyclingOutputs}.
 * <p>Items matching {@link DirectRecyclingRecipe#ingredient()} will be recycled into {@link DirectRecyclingRecipe#outputs()}.
 * This contrasts to {@link IngredientRecyclingRecipe}s, in which any <i>other</i> Recipes that include {@link IngredientRecyclingRecipe#ingredient()} will be recycled into {@link IngredientRecyclingRecipe#outputs()}.</p>
 */
public record DirectRecyclingRecipe(Ingredient ingredient, RecyclingOutputs outputs) implements IRecyclingRecipe {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        return ingredient.test(input.item());
    };

    @Override
    public RecipeSerializer<DirectRecyclingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.RECYCLING.get();
    };

    @Override
    public RecipeType<DirectRecyclingRecipe> getType() {
        return PetrolparkRecipeTypes.RECYCLING.get();
    };
    
};
