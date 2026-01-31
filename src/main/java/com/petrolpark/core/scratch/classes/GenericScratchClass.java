package com.petrolpark.core.scratch.classes;

import java.util.function.Function;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class GenericScratchClass<TYPE, GENERIC_TYPE> implements IScratchClass<TYPE> {

    static <SCRATCH_CLASS extends GenericScratchClass<?, ?>> Products.P1<RecordCodecBuilder.Mu<SCRATCH_CLASS>, ISyncedScratchClass<?>> commonCodecFields(RecordCodecBuilder.Instance<SCRATCH_CLASS> instance) {
        return instance.group(IScratchClass.CODEC.<ISyncedScratchClass<?>>xmap(IScratchClass::asSynced, Function.identity()).fieldOf("class").forGetter(GenericScratchClass::getGenericScratchClass));
    };
    
    protected final ISyncedScratchClass<GENERIC_TYPE> genericScratchClass;

    public GenericScratchClass(ISyncedScratchClass<GENERIC_TYPE> genericScratchClass) {
        this.genericScratchClass = genericScratchClass;
    };

    public final ISyncedScratchClass<GENERIC_TYPE> getGenericScratchClass() {
        return genericScratchClass;
    };
};
