package com.petrolpark.compat.create.core.dough.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.PetrolparkDoughIngredientTypes;
import com.petrolpark.compat.create.core.dough.DoughData;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ThicknessDoughIngredient(float min, float max) implements DoughIngredient {

    public static final MapCodec<ThicknessDoughIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.floatRange(0f, 16f).optionalFieldOf("min", Float.NaN).forGetter(ThicknessDoughIngredient::min),
        Codec.floatRange(0f, 16f).optionalFieldOf("max", Float.NaN).forGetter(ThicknessDoughIngredient::max)
    ).apply(instance, ThicknessDoughIngredient::new));

    public static final StreamCodec<ByteBuf, ThicknessDoughIngredient> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, ThicknessDoughIngredient::min,
        ByteBufCodecs.FLOAT, ThicknessDoughIngredient::max,
        ThicknessDoughIngredient::new
    );

    @Override
    public INamedAdvancedIngredientType<DoughData> getType() {
        return PetrolparkDoughIngredientTypes.THICKNESS.get();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        if (!hasMin() && !hasMax()) return;
        description.add(translateSimple(Lang.rangeWorded(min(), max(), false, Lang.ONE_DP_DF)));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        if (!hasMin() && !hasMax()) return;
        description.add(translateSimple(Lang.rangeWorded(min(), max(), true, Lang.ONE_DP_DF)));
    };

    @Override
    public boolean test(DoughData t) {
        return (!hasMin() || min() <= t.thickness()) && (!hasMax() || max >= t.thickness());
    };

    @Override
    public Stream<DoughData> modifyExamples(Stream<DoughData> exampleStacks) {
        return exampleStacks.map(data -> 
            hasMin() && data.thickness() < min()
                ? data.withThickness(min())
                : hasMax() && data.thickness() > max()
                    ? data.withThickness(max())
                    : data
        );
    };

    @Override
    public Stream<DoughData> modifyCounterExamples(Stream<DoughData> counterExampleStacks) {
        return counterExampleStacks.map(data -> 
            hasMin() && data.thickness() > min()
                ? data.withThickness(min())
                : hasMax() && data.thickness() < max()
                    ? data.withThickness(max())
                    : data
        );
    };

    public boolean hasMin() {
        return !Float.isNaN(min());
    };

    public boolean hasMax() {
        return !Float.isNaN(max());
    };
    
};
