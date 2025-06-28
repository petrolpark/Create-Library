package com.petrolpark.core.recipe.ingredient.editor;

import com.petrolpark.core.recipe.ingredient.modifier.CompoundIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;

public interface INeoForgeIngredientConverter<STACK, INGREDIENT> {
    
    /**
     * Attempt to convert the {@link IIngredientModifier} (possibly {@link CompoundIngredientModifier compound}) to a Minecraft/NeoForge simplified equivalent.
     * @param modifier Fully {@link IIngredientModifier#simplify() simplified} {@link IIngredientModifier}
     * @return Non-{@code null} ingredient
     * @throws IngredientConversionException If this is not possible (e.g. because the Modifier involves more complex Modifiers that have no direct NeoForge equivalent)
     */
    public INGREDIENT convertToNeoForge(IIngredientModifier<? super STACK> modifier) throws IngredientConversionException;

    public IIngredientModifier<? super STACK> convertToModifier(INGREDIENT ingredient) throws IngredientConversionException;

    public class IngredientConversionException extends Exception {

        public IngredientConversionException(String message) {
            super(message);
        };
    };
};
