package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public record ContaminatedIngredientModifier(Holder<Contaminant> contaminant) implements IIngredientModifier<MutableDataComponentHolder> {

    public static final MapCodec<ContaminatedIngredientModifier> CODEC = CodecHelper.singleFieldMap(Contaminant.CODEC, "contaminant", ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ContaminatedIngredientModifier> STREAM_CODEC = StreamCodec.composite(Contaminant.STREAM_CODEC, ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);
    public static final IIngredientModifierType<MutableDataComponentHolder> TYPE = new IngredientModifierGenericType<>("petrolpark.ingredient_modifier.contaminated", CODEC, STREAM_CODEC); 

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        return IContamination.get(stack).map(contamination -> contamination.has(contaminant)).orElse(false);
    };

    @Override
    public void modifyExamples(List<? extends MutableDataComponentHolder> exampleStacks) {
        exampleStacks.stream().map(IContamination::get).filter(Optional::isPresent).map(Optional::get).forEach(c -> c.contaminate(contaminant));
    };

    @Override
    public void modifyCounterExamples(List<? extends MutableDataComponentHolder> counterExampleStacks) {
        counterExampleStacks.stream().map(IContamination::get).filter(Optional::isPresent).map(Optional::get).forEach(co -> co.decontaminateOnly(contaminant));
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
    public IIngredientModifierType<MutableDataComponentHolder> getType() {
        return TYPE;
    };
    
};
