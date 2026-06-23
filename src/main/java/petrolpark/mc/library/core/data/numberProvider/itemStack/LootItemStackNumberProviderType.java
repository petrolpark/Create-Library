package petrolpark.mc.library.core.data.numberProvider.itemStack;

import com.mojang.serialization.MapCodec;

public record LootItemStackNumberProviderType(MapCodec<? extends ItemStackNumberProvider> codec) {
    
};
