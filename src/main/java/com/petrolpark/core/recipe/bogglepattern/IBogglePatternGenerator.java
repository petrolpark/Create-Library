package com.petrolpark.core.recipe.bogglepattern;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.util.RandomSource;

public interface IBogglePatternGenerator {

    /**
     * Use {@link IBogglePatternGenerator#CODEC} instead.
     */
    static final Codec<IBogglePatternGenerator> TYPED_CODEC = PetrolparkRegistries.BOGGLE_PATTERN_GENERATOR_TYPES
        .byNameCodec()
        .dispatch(IBogglePatternGenerator::getType, BogglePatternGeneratorType::codec);

    public static final Codec<IBogglePatternGenerator> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC).orElse(new EasyBogglePatternGenerator());

    /**
     * Use {@link IBogglePatternGenerator#NETWORK_CODEC} instead.
     */
    static final Codec<IBogglePatternGenerator> TYPED_NETWORK_CODEC = PetrolparkRegistries.BOGGLE_PATTERN_GENERATOR_TYPES
        .byNameCodec()
        .dispatch(IBogglePatternGenerator::getType, BogglePatternGeneratorType::networkCodec);

    public static final Codec<IBogglePatternGenerator> NETWORK_CODEC = Codec.lazyInitialized(() -> TYPED_NETWORK_CODEC).orElse(new EasyBogglePatternGenerator());

    static final List<Integer> POSITIONS = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));

    public int generate(RandomSource random);
    
    public BogglePatternGeneratorType getType();
};
