package com.petrolpark.compat.create.core.dough.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkDoughIngredientTypes;
import com.petrolpark.compat.create.core.dough.DoughData;
import com.petrolpark.compat.create.core.dough.topping.IDoughTopping;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ToppingDoughIngredient(Holder<IDoughTopping> topping) implements DoughIngredient {

    public static final MapCodec<ToppingDoughIngredient> CODEC = CodecHelper.singleFieldMap(IDoughTopping.CODEC, "topping", ToppingDoughIngredient::topping, ToppingDoughIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ToppingDoughIngredient> STREAM_CODEC = StreamCodec.composite(IDoughTopping.STREAM_CODEC, ToppingDoughIngredient::topping, ToppingDoughIngredient::new);

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(translateSimple(topping().value().name()));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(topping().value().name()));
    };

    @Override
    public boolean test(DoughData t) {
        return t.decoration().right().filter(toppings -> toppings.has(topping())).isPresent();
    };

    @Override
    public Stream<DoughData> modifyExamples(Stream<DoughData> exampleStacks) {
        return exampleStacks.map(data -> data.withNewTopping(topping()));
    };

    @Override
    public Stream<DoughData> modifyCounterExamples(Stream<DoughData> counterExampleStacks) {
        return counterExampleStacks.map(data -> data.withoutTopping(topping()));
    };

    @Override
    public INamedAdvancedIngredientType<DoughData> getType() {
        return PetrolparkDoughIngredientTypes.TOPPING.get();
    };
    
};
