package com.petrolpark.core.data.recipe.ingredient.advanced;

public interface IAdvancedIngredient<STACK> extends ITypelessAdvancedIngredient<STACK> {

    public IAdvancedIngredientType<? super STACK> getType();

    @Override
    public default IAdvancedIngredient<? super STACK> simplify() {
        return this;
    };
};
