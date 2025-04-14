package com.petrolpark.core.recipe.recycling;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkRecipeTypes;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record DirectRecyclingRecipe(Ingredient ingredient, RecyclingOutputs outputs) implements IRecyclingRecipe {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        return ingredient.test(input.item());
    };

    @Override
    public RecipeSerializer<DirectRecyclingRecipe> getSerializer() {
        return PetrolparkRecipeTypes.RECYCLING.getSerializer();
    };

    @Override
    public RecipeType<DirectRecyclingRecipe> getType() {
        return PetrolparkRecipeTypes.RECYCLING.getType();
    };
    
};
