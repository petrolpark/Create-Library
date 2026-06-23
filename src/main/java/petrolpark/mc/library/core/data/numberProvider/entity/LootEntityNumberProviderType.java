package petrolpark.mc.library.core.data.numberProvider.entity;

import com.mojang.serialization.MapCodec;

public record LootEntityNumberProviderType(MapCodec<? extends EntityNumberProvider> codec) {
    
};
