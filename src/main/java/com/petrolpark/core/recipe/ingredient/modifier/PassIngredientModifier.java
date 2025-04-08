package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.petrolpark.PetrolparkIngredientModifierTypes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PassIngredientModifier implements IngredientModifier {

    public static final PassIngredientModifier INSTANCE = new PassIngredientModifier();

    @Override
    public boolean test(ItemStack stack) {
        return true;
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks) {};

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks) {
        counterExampleStacks.clear();
    };

    @Override
    public void addToDescription(List<Component> description) {};

    @Override
    public void addToCounterDescription(List<Component> description) {};

    @Override
    public IngredientModifierType getType() {
        return PetrolparkIngredientModifierTypes.PASS.get();
    };
    
};
