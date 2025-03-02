package com.petrolpark.recipe.ingredient.modifier;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.data.loot.SimpleSerializer;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class IngredientModifierTypes {

    public static final RegistryEntry<IngredientModifierType>

    PASS = REGISTRATE.get().ingredientModifierType("pass", new SimpleSerializer<>(PassIngredientModifier::new)),
    CONTAMINATED = REGISTRATE.get().ingredientModifierType("contaminated", new ContaminatedIngredientModifier.Serializer());
    
    public static final void register() {};
};
