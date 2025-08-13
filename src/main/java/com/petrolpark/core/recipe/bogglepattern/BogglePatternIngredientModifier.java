package com.petrolpark.core.recipe.bogglepattern;

import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

public record BogglePatternIngredientModifier(BogglePattern pattern) implements IIngredientModifier<MutableDataComponentHolder> {

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        Integer pattern = stack.get(PetrolparkDataComponents.BOGGLE_PATTERN);
        if (pattern == null) return false;
        return (int)pattern == pattern().getPattern();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToDescription'");
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToCounterDescription'");
    };

    @Override
    public IIngredientModifierType<? super MutableDataComponentHolder> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
