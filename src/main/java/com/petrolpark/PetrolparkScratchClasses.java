package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.classes.BlockPosScratchClass;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.classes.NullScratchClass;
import com.petrolpark.core.scratch.classes.RealScratchClass;
import com.petrolpark.core.scratch.classes.StringScratchClass;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchClasses {

    public static final RegistryEntry<IScratchClass<?>, NullScratchClass> NULL = REGISTRATE.scratchClass("null", NullScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, BooleanScratchClass> BOOLEAN = REGISTRATE.scratchClass("boolean", BooleanScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, IntegerScratchClass> INTEGER = REGISTRATE.scratchClass("boolean", IntegerScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, RealScratchClass> REAL = REGISTRATE.scratchClass("boolean", RealScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, StringScratchClass> STRING = REGISTRATE.scratchClass("string", StringScratchClass::new);
    public static final RegistryEntry<IScratchClass<?>, BlockPosScratchClass> BLOCK_POS = REGISTRATE.scratchClass("block_pos", BlockPosScratchClass::new);

    public static final void register() {};
};
