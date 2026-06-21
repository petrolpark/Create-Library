package petrolpark.mc.library.core.data.loot.modifier;

import com.mojang.serialization.MapCodec;

public record LootTableModifierType(MapCodec<? extends ILootTableModifier> codec) {
    
};
