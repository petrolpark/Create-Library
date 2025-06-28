package com.petrolpark.core.recipe.ingredient.modifier;

public interface INamedIngredientModifierType<STACK> extends IIngredientModifierType<STACK> {

    public String translationKey();
};
