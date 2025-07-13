package com.petrolpark.core.recipe.bogglepattern;

import com.mojang.serialization.MapCodec;

public record BogglePatternGeneratorType(MapCodec<? extends IBogglePatternGenerator> codec, MapCodec<? extends IBogglePatternGenerator> networkCodec) {
    
};
