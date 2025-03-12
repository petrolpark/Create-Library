package com.petrolpark.data.loot.numberprovider;

import java.util.List;
import java.util.stream.DoubleStream;

import com.petrolpark.data.loot.PetrolparkLootNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class MaxNumberProvider extends FunctionNumberProvider {

    public MaxNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float apply(LootContext lootContext, DoubleStream childResults) {
        return (float)childResults.max().orElse(0f);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkLootNumberProviderTypes.MAX.get();
    };
    
};
