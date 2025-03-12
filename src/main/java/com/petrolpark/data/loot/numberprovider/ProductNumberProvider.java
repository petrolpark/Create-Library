package com.petrolpark.data.loot.numberprovider;

import java.util.List;
import java.util.stream.DoubleStream;

import com.petrolpark.data.loot.PetrolparkLootNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ProductNumberProvider extends FunctionNumberProvider {

    public ProductNumberProvider(List<NumberProvider> children) {
        super(children);
    };

    @Override
    public float apply(LootContext lootContext, DoubleStream children) {
        return (float)children.reduce(1d, (a, b) -> a * b);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkLootNumberProviderTypes.PRODUCT.get();
    };
    
};
