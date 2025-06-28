package com.petrolpark.core.data.loot.numberprovider;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SumNumberProvider extends FunctionNumberProvider {

    public SumNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float apply(LootContext lootContext, DoubleStream children) {
        return (float)children.sum();
    };

    @Override
    public NumberEstimate applyEstimate(Stream<NumberEstimate> estimates) {
        return estimates.reduce(NumberEstimate::add).orElse(NumberEstimate.UNKNOWN);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.SUM.get();
    };
    
};
