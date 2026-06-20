package com.petrolpark.core.data.recipe.ingredient.editor;

import com.petrolpark.core.data.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

public interface INeoForgeIngredientConverter<STACK, INGREDIENT> {
    
    /**
     * Attempt to convert the {@link IAdvancedIngredient} (possibly {@link CompoundAdvancedIngredient compound}) to a Minecraft/NeoForge simplified equivalent.
     * @param ingredient Fully {@link IAdvancedIngredient#simplify() simplified} {@link IAdvancedIngredient}
     * @return Non-{@code null} ingredient
     * @throws IngredientConversionException If this is not possible (e.g. because the Ingredient involves more complex Ingredients that have no direct NeoForge equivalent)
     */
    public INGREDIENT convertToNeoForgeIngredient(IAdvancedIngredient<? super STACK> ingredient) throws IngredientConversionException;

    public IAdvancedIngredient<? super STACK> convertToAdvancedIngredient(INGREDIENT ingredient) throws IngredientConversionException;

    public class IngredientConversionException extends Exception {

        public IngredientConversionException(String message) {
            super(message);
        };
    };
};
