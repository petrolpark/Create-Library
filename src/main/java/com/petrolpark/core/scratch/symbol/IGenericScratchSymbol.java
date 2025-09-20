package com.petrolpark.core.scratch.symbol;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IGenericScratchSymbol<ENVIRONMENT extends IScratchEnvironment, GENERIC_TYPE, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> {
    
    static <SYMBOL extends IGenericScratchSymbol<?, ?, ?>> P1<Mu<SYMBOL>, IScratchClass<?>> commonCodecFields(Instance<SYMBOL> instance) {
        return instance.group(IScratchClass.CODEC.fieldOf("class").forGetter(IGenericScratchSymbol::getGenericScratchClass));
    };

    IScratchClass<GENERIC_TYPE> getGenericScratchClass();
};
