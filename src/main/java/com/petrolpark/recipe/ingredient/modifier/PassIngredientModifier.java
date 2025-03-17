package com.petrolpark.recipe.ingredient.modifier;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PassIngredientModifier implements IngredientModifier {

    public static final PassIngredientModifier INSTANCE = new PassIngredientModifier();

    @Override
    public boolean test(ItemStack stack, Level level) {
        return true;
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks, Level level) {};

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks, Level level) {};

    @Override
    public void addToDescription(List<Component> description, Level level) {};

    @Override
    public IngredientModifierType getType() {
        return PetrolparkIngredientModifierTypes.PASS.get();
    };
    
};
