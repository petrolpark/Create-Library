package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

public interface IScratchClass<TYPE> {
    
    static Codec<IScratchClass<?>> CODEC = PetrolparkRegistries.SCRATCH_CLASSES.byNameCodec();
};
