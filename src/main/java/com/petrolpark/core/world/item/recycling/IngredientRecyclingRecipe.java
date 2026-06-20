package com.petrolpark.core.world.item.recycling;

import javax.annotation.Nonnull;

import com.petrolpark.registry.PetrolparkRecipeSerializers;
import com.petrolpark.registry.PetrolparkRecipeTypes;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Recycle the output of another Recipe that takes an Ingredient into known {@link RecyclingOutputs}.
 * <p>Any <i>other</i> Recipes that include {@link IngredientRecyclingRecipe#ingredient()} as an {@link Recipe#getIngredients() Ingredient} will be recycled into {@link IngredientRecyclingRecipe#outputs()}.
 * This contrasts to {@link DirectRecyclingRecipe}, in which Items matching {@link DirectRecyclingRecipe#ingredient()} will be recycled into {@link DirectRecyclingRecipe#outputs}.</p>
 * <p>In other words, this is never used as an actual {@link Recipe}, but as a signal on how to invert other {@link Recipe}s.
 */
public record IngredientRecyclingRecipe(Ingredient ingredient, RecyclingOutputs outputs) implements IRecyclingRecipe {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        return false; // Never used as an actual Recipe
    };

    @Override
    public RecipeSerializer<IngredientRecyclingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.INGREDIENT_RECYCLING.get();
    };

    @Override
    public RecipeType<IngredientRecyclingRecipe> getType() {
        return PetrolparkRecipeTypes.INGREDIENT_RECYCLING.get();
    };
    
};
