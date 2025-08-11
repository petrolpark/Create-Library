package com.petrolpark.core.scratch;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.scratch.type.IScratchType;
import com.petrolpark.core.scratch.type.SimpleScratchType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchTypes {
  
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Boolean>> BOOLEAN = Petrolpark.REGISTRATE.scratchType("boolean", Boolean.class);
    public static final RegistryEntry<IScratchType<?>, SimpleScratchType<Integer>> INTEGER = Petrolpark.REGISTRATE.scratchType("integer", Integer.class);

    public static final void register() {};

};
