package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.AdvancedFluidIngredient;
import com.petrolpark.core.recipe.ingredient.AdvancedItemIngredient;
import com.petrolpark.core.recipe.ingredient.BlockHolderSetIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class PetrolparkIngredientTypes {

    public static final RegistryEntry<IngredientType<?>, IngredientType<AdvancedItemIngredient>> ADVANCED = REGISTRATE.ingredientType("advanced", AdvancedItemIngredient.CODEC, AdvancedItemIngredient.STREAM_CODEC);
    public static final RegistryEntry<IngredientType<?>, IngredientType<BlockHolderSetIngredient>> BLOCK_SET = REGISTRATE.ingredientType("block_set", BlockHolderSetIngredient.CODEC, BlockHolderSetIngredient.STREAM_CODEC);

    public static final RegistryEntry<FluidIngredientType<?>, FluidIngredientType<AdvancedFluidIngredient>> FLUID_ADVANCED = REGISTRATE.fluidIngredientType("advanced", AdvancedFluidIngredient.CODEC, AdvancedFluidIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
