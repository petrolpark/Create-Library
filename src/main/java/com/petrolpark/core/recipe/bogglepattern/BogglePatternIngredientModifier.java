package com.petrolpark.core.recipe.bogglepattern;

import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.world.item.ItemStack;

public record BogglePatternIngredientModifier(BogglePattern pattern) implements IIngredientModifier<ItemStack> {

    @Override
    public boolean test(ItemStack stack) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'test'");
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
    public IIngredientModifierType<? super ItemStack> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
