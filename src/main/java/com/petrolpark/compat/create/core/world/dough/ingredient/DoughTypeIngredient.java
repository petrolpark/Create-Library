package com.petrolpark.compat.create.core.world.dough.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.world.dough.DoughData;
import com.petrolpark.compat.create.core.world.dough.IDough;
import com.petrolpark.compat.create.registry.PetrolparkDoughIngredientTypes;
import com.petrolpark.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;
import com.petrolpark.util.Neither;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DoughTypeIngredient(IDough dough) implements DoughIngredient {

    public static final MapCodec<DoughTypeIngredient> CODEC = CodecHelper.singleFieldMap(IDough.CODEC, "dough", DoughTypeIngredient::dough, DoughTypeIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DoughTypeIngredient> STREAM_CODEC = StreamCodec.composite(IDough.STREAM_CODEC, DoughTypeIngredient::dough, DoughTypeIngredient::new);

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(dough().name());
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(dough().name()));
    };

    @Override
    public boolean test(DoughData t) {
        return t.dough().equals(dough());
    };

    @Override
    public Stream<DoughData> streamExamples() {
        return Stream.of(new DoughData(dough(), 4f, (byte)1, (byte)1, Neither.neither(), false));
    };

    @Override
    public Stream<DoughData> modifyExamples(Stream<DoughData> exampleStacks) {
        return exampleStacks.map(data -> data.withDough(dough()));
    };

    @Override
    public INamedAdvancedIngredientType<DoughData> getType() {
        return PetrolparkDoughIngredientTypes.TYPE.get();
    };
    
};
