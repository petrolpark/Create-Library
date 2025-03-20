package com.petrolpark.core.data.loot.numberprovider.entity;

import com.mojang.serialization.MapCodec;

public record LootEntityNumberProviderType(MapCodec<? extends EntityNumberProvider> codec) {
    
};
