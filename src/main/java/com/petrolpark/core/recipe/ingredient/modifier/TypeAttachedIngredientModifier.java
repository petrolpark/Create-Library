package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import net.minecraft.network.chat.Component;

public record TypeAttachedIngredientModifier<STACK, MODIFIER extends ITypelessIngredientModifier<? super STACK>>(MODIFIER untypedModifier, IIngredientModifierType<STACK> type) implements IIngredientModifier<STACK> {

    @Override
    public boolean test(STACK stack) {
        return untypedModifier().test(stack);
    };

    @Override
    public void modifyExamples(List<? extends STACK> exampleStacks) {
        untypedModifier().modifyExamples(exampleStacks);
    };

    @Override
    public void modifyCounterExamples(List<? extends STACK> counterExampleStacks) {
        untypedModifier().modifyCounterExamples(counterExampleStacks);
    };

    @Override
    public void addToDescription(List<Component> description) {
        untypedModifier().addToDescription(description);
    };

    @Override
    public void addToCounterDescription(List<Component> description) {
        untypedModifier().addToCounterDescription(description);
    };

    @Override
    public IIngredientModifierType<? super STACK> getType() {
        return type();
    };
    
};
