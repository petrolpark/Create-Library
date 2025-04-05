package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ContaminatedIngredientModifier(Holder<Contaminant> contaminant) implements IngredientModifier {

    public static final MapCodec<ContaminatedIngredientModifier> CODEC = CodecHelper.singleFieldMap(Contaminant.CODEC, "contaminant", ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);

    @Override
    public boolean test(ItemStack stack, Level level) {
        return ItemContamination.get(stack).has(contaminant);
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks, Level level) {
        exampleStacks.stream().map(ItemContamination::get).map(c -> c.contaminate(contaminant));
    };

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks, Level level) {
        counterExampleStacks.stream().map(ItemContamination::get).forEach(co -> co.decontaminateOnly(contaminant));
    };

    @Override
    public void addToDescription(List<Component> description, Level level) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToDescription'");
    };

    @Override
    public IngredientModifierType getType() {
        return PetrolparkIngredientModifierTypes.CONTAMINATED.get();
    };
    
};
