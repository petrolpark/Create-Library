package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.modifier.ContaminatedIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.NotIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.PassIngredientModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class PetrolparkIngredientModifierTypes {


    // Items
    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, ? extends IIngredientModifierType<? super ItemStack>>

    ITEM_PASS = REGISTRATE.itemIngredientModifierType("pass", PassIngredientModifier.TYPE),
    ITEM_NOT = REGISTRATE.itemIngredientModifierType("not", NotIngredientModifier::codec, NotIngredientModifier::streamCodec),
    ITEM_CONTAMINATED = REGISTRATE.itemIngredientModifierType("contaminated", ContaminatedIngredientModifier.TYPE);


    // Fluids
    public static final RegistryEntry<IIngredientModifierType<? super FluidStack>, ? extends IIngredientModifierType<? super FluidStack>>

    FLUID_PASS = REGISTRATE.fluidIngredientModifierType("pass", PassIngredientModifier.TYPE),
    FLUID_NOT = REGISTRATE.fluidIngredientModifierType("not", NotIngredientModifier::codec, NotIngredientModifier::streamCodec),
    FLUID_CONTAMINATED = REGISTRATE.fluidIngredientModifierType("contaminated", ContaminatedIngredientModifier.TYPE);
    
    public static final void register() {};
};
