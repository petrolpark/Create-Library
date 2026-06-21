package petrolpark.mc.library.core.data.loot.numberprovider.itemstack;

import com.mojang.serialization.MapCodec;

public record LootItemStackNumberProviderType(MapCodec<? extends ItemStackNumberProvider> codec) {
    
};
