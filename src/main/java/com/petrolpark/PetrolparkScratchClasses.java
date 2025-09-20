package com.petrolpark;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.classes.RealScratchClass;
import com.petrolpark.core.scratch.classes.StringScratchClass;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchClasses {

    public static final RegistryEntry<IScratchClass<?>, BooleanScratchClass> BOOLEAN = Petrolpark.REGISTRATE.scratchClass("boolean", BooleanScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, IntegerScratchClass> INTEGER = Petrolpark.REGISTRATE.scratchClass("boolean", IntegerScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, RealScratchClass> REAL = Petrolpark.REGISTRATE.scratchClass("boolean", RealScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, StringScratchClass> STRING = Petrolpark.REGISTRATE.scratchClass("string", StringScratchClass::new);

    public static final void register() {};
};
