package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.FromArrayIngredientRandomizer;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.FromItemSetIngredientRandomizer;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkIngredientRandomizerTypes {
  
    public static final RegistryEntry<IngredientRandomizerType, IngredientRandomizerType>
    
    FROM_ARRAY = REGISTRATE.ingredientRandomizerType("from_array", FromArrayIngredientRandomizer.CODEC),
    FROM_ITEM_SET = REGISTRATE.ingredientRandomizerType("from_item_set", FromItemSetIngredientRandomizer.CODEC);

    public static final void register() {};
};
