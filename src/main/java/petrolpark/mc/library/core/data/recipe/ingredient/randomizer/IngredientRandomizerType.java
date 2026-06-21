package petrolpark.mc.library.core.data.recipe.ingredient.randomizer;

import com.mojang.serialization.MapCodec;

public record IngredientRandomizerType(MapCodec<? extends IngredientRandomizer> codec) {
    
};
