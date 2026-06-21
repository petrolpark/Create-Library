package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.BogglePatternGeneratorType;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.EasyBogglePatternGenerator;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.FixedBogglePatternGenerator;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.HardBogglePatternGenerator;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkBogglePatternGeneratorTypes {
    
    public static final RegistryEntry<BogglePatternGeneratorType, BogglePatternGeneratorType>

    FIXED = REGISTRATE.bogglePatternGeneratorType("fixed", FixedBogglePatternGenerator.CODEC, FixedBogglePatternGenerator.DIRECT_CODEC),
    EASY = REGISTRATE.bogglePatternGeneratorType("easy", EasyBogglePatternGenerator::new),
    HARD = REGISTRATE.bogglePatternGeneratorType("hard", HardBogglePatternGenerator::new);

    public static final void register() {};
};
