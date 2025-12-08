package com.petrolpark.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryModifierType(MapCodec<? extends ILootPoolEntryModifier> codec) {
    
};
