package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkIngredientModifierTypes;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ContaminatedIngredientModifier(Holder<Contaminant> contaminant) implements IngredientModifier {

    public static final MapCodec<ContaminatedIngredientModifier> CODEC = CodecHelper.singleFieldMap(Contaminant.CODEC, "contaminant", ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ContaminatedIngredientModifier> STREAM_CODEC = StreamCodec.composite(Contaminant.STREAM_CODEC, ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);

    @Override
    public boolean test(ItemStack stack) {
        return ItemContamination.get(stack).has(contaminant);
    };

    @Override
    public void modifyExamples(List<ItemStack> exampleStacks) {
        exampleStacks.stream().map(ItemContamination::get).map(c -> c.contaminate(contaminant));
    };

    @Override
    public void modifyCounterExamples(List<ItemStack> counterExampleStacks) {
        counterExampleStacks.stream().map(ItemContamination::get).forEach(co -> co.decontaminateOnly(contaminant));
    };

    @Override
    public void addToDescription(List<Component> description) {
        description.add(Contaminant.getNameColored(contaminant));
    };

    @Override
    public void addToCounterDescription(List<Component> description) {
        description.add(Contaminant.getAbsentNameColored(contaminant));
    };

    @Override
    public IngredientModifierType getType() {
        return PetrolparkIngredientModifierTypes.CONTAMINATED.get();
    };
    
};
