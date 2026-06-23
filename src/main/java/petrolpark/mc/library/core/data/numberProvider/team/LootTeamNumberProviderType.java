package petrolpark.mc.library.core.data.numberProvider.team;

import com.mojang.serialization.MapCodec;

public record LootTeamNumberProviderType(MapCodec<? extends TeamNumberProvider> codec) {
    
};
