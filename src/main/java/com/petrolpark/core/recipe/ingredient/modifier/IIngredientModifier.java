package com.petrolpark.core.recipe.ingredient.modifier;

public interface IIngredientModifier<STACK> extends ITypelessIngredientModifier<STACK> {

    public IIngredientModifierType<? super STACK> getType();
};
