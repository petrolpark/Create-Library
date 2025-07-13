package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.bogglepattern.BogglePatternGeneratorType;
import com.petrolpark.core.recipe.bogglepattern.EasyBogglePatternGenerator;
import com.petrolpark.core.recipe.bogglepattern.FixedBogglePatternGenerator;
import com.petrolpark.core.recipe.bogglepattern.HardBogglePatternGenerator;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkBogglePatternGeneratorTypes {
    
    public static final RegistryEntry<BogglePatternGeneratorType, BogglePatternGeneratorType>

    FIXED = REGISTRATE.bogglePatternGeneratorType("fixed", FixedBogglePatternGenerator.CODEC, FixedBogglePatternGenerator.DIRECT_CODEC),
    EASY = REGISTRATE.bogglePatternGeneratorType("easy", EasyBogglePatternGenerator::new),
    HARD = REGISTRATE.bogglePatternGeneratorType("hard", HardBogglePatternGenerator::new);

    public static final void register() {};
};
