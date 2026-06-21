package petrolpark.mc.library.core.data.recipe.bogglePattern.generator;

import com.mojang.serialization.MapCodec;

public record BogglePatternGeneratorType(MapCodec<? extends IBogglePatternGenerator> codec, MapCodec<? extends IBogglePatternGenerator> networkCodec) {
    
};
