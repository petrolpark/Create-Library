package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import net.minecraft.network.chat.Component;

public interface ITypelessIngredientModifier<STACK> {
    
    public boolean test(STACK stack);

    public void modifyExamples(List<? extends STACK> exampleStacks);

    public void modifyCounterExamples(List<? extends STACK> counterExampleStacks);

    public void addToDescription(List<Component> description);

    public void addToCounterDescription(List<Component> description);

};
