package com.petrolpark.data.loot.numberprovider.team;

import com.mojang.serialization.MapCodec;

public record LootTeamNumberProviderType(MapCodec<? extends TeamNumberProvider> codec) {
    
};
