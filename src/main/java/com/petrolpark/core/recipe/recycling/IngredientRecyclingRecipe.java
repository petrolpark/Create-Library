package com.petrolpark.core.recipe.recycling;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.PetrolparkRecipeTypes;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record IngredientRecyclingRecipe(Ingredient ingredient, RecyclingOutputs outputs) implements IRecyclingRecipe {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        return false;
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
