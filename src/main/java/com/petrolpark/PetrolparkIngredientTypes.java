package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.BlockHolderSetIngredient;
import com.petrolpark.core.recipe.ingredient.ModifiedFluidIngredient;
import com.petrolpark.core.recipe.ingredient.ModifiedIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class PetrolparkIngredientTypes {

    public static final RegistryEntry<IngredientType<?>, IngredientType<ModifiedIngredient>> MODIFIED = REGISTRATE.ingredientType("modified", ModifiedIngredient.CODEC, ModifiedIngredient.STREAM_CODEC);
    public static final RegistryEntry<IngredientType<?>, IngredientType<BlockHolderSetIngredient>> BLOCK_SET = REGISTRATE.ingredientType("block_set", BlockHolderSetIngredient.CODEC, BlockHolderSetIngredient.STREAM_CODEC);

    public static final RegistryEntry<FluidIngredientType<?>, FluidIngredientType<ModifiedFluidIngredient>> FLUID_MODIFIED = REGISTRATE.fluidIngredientType("modified", ModifiedFluidIngredient.CODEC, ModifiedFluidIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
