package petrolpark.mc.library.core.data.stringProvider;

import com.mojang.serialization.MapCodec;

public record StringProviderType(MapCodec<? extends StringProvider> codec) {
    
};
