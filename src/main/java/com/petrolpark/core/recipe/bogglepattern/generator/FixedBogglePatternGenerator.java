package com.petrolpark.core.recipe.bogglepattern.generator;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkBogglePatternGeneratorTypes;
import com.petrolpark.core.recipe.bogglepattern.BogglePatternHelper;
import com.petrolpark.util.CodecHelper;

import net.minecraft.util.RandomSource;

public class FixedBogglePatternGenerator implements IBogglePatternGenerator {

    public static final MapCodec<FixedBogglePatternGenerator> CODEC = CodecHelper.singleFieldMap(Codec.list(Codec.STRING), "pattern", FixedBogglePatternGenerator::toPatternString, FixedBogglePatternGenerator::new);
    public static final MapCodec<FixedBogglePatternGenerator> DIRECT_CODEC = CodecHelper.singleFieldMap(Codec.INT, "pattern", FixedBogglePatternGenerator::getPattern, FixedBogglePatternGenerator::new);

    private final int pattern;

    public FixedBogglePatternGenerator(int pattern) {
        this.pattern = pattern;
    };

    public int getPattern() {
        return pattern;
    };

    public FixedBogglePatternGenerator(List<String> patternString) {
        int pattern = 0;
        if (patternString.size() != 4) throw new IllegalArgumentException("Pattern must be 4 by 4");
        for (int y = 0; y <= 3; y++) {
            String line = patternString.get(y);
            if (line.length() != 4) throw new IllegalArgumentException("Pattern must be 4 by 4");
            for (int x = 0; x <= 3; x++) {
                if (line.charAt(x) != ' ') pattern = BogglePatternHelper.set1(pattern, x, y);
            };
        };
        this.pattern = pattern;
    };

    public List<String> toPatternString() {
        List<String> patternString = List.of("", "", "", "");
        for (int y = 0; y <= 3; y++) {
            for (int x = 0; x <= 3; x++) {
                String line = patternString.get(y);
                patternString.set(y, line + (BogglePatternHelper.is1(pattern, x, y) ? '.' : ' '));
            };
        };
        return patternString;
    };

    @Override
    public int generate(RandomSource random) {
        return pattern;
    };

    @Override
    public BogglePatternGeneratorType getType() {
        return PetrolparkBogglePatternGeneratorTypes.FIXED.get();
    };
    
};
