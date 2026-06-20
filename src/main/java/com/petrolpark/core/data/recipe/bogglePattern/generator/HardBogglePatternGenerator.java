package com.petrolpark.core.data.recipe.bogglePattern.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.petrolpark.registry.PetrolparkBogglePatternGeneratorTypes;

import net.minecraft.util.RandomSource;

public final class HardBogglePatternGenerator implements IBogglePatternGenerator {
  
    public static final Integer[] outer = new Integer[]{0 , 3 , 12, 15};
    public static final Integer[] inner = new Integer[]{5 , 6 , 9 , 10};
    public static final Integer[] right = new Integer[]{2 , 4 , 11, 13};
    public static final Integer[] left  = new Integer[]{1 , 7 , 8 , 14};

    public static final Integer[] countWeights = new Integer[]{1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4};

    @Override
    public int generate(RandomSource random) {
        int pattern = 0;
        for (Integer[] set : new Integer[][]{outer, inner, left, right}) {
            List<Integer> values = Arrays.asList(set);
            Collections.shuffle(values);
            for (int i = 0; i < countWeights[random.nextInt(countWeights.length)]; i++) {
                pattern |= (1 << values.get(i));
            };
        };
        return pattern;
    };

    @Override
    public BogglePatternGeneratorType getType() {
        return PetrolparkBogglePatternGeneratorTypes.HARD.get();
    };
};
