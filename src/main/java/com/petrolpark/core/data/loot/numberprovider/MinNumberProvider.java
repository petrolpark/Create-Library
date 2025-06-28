package com.petrolpark.core.data.loot.numberprovider;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class MinNumberProvider extends FunctionNumberProvider {

    public MinNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float apply(LootContext lootContext, DoubleStream childResults) {
        return (float)childResults.min().orElse(0f);
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimates) {
        float min = 0f;
        float max = 0f;
        boolean approximate = false;
        for (NumberEstimate estimate : estimates.toList()) {
            if (estimate.unknown()) return estimate;
            min = Math.min(min, estimate.min());
            max = Math.min(max, estimate.max());
            approximate |= estimate.approximate;
        };
        return NumberEstimate.ranged(min, max, approximate);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.MIN.get();
    };
    
};
