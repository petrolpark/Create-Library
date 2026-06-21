package petrolpark.mc.library.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryModifierType(MapCodec<? extends ILootPoolEntryModifier> codec) {
    
};
