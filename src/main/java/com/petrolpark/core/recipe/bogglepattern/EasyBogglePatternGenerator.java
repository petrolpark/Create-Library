package com.petrolpark.core.recipe.bogglepattern;

import java.util.Collections;

import com.petrolpark.PetrolparkBogglePatternGeneratorTypes;

import net.minecraft.util.RandomSource;

public final class EasyBogglePatternGenerator implements IBogglePatternGenerator {

    @Override
    public int generate(RandomSource random) {
        int pattern = 0;
        Collections.shuffle(POSITIONS);
        for (int i = 0; i < 3; i++) pattern |= (1 << POSITIONS.get(i));
        return pattern;
    };

    @Override
    public BogglePatternGeneratorType getType() {
        return PetrolparkBogglePatternGeneratorTypes.EASY.get();
    };
    
};
