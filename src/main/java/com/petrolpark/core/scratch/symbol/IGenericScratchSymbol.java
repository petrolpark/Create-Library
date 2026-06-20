package com.petrolpark.core.scratch.symbol;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;
import com.petrolpark.util.codec.ContextualCodec;
import com.petrolpark.util.codec.RecordContextualCodecBuilder;

public interface IGenericScratchSymbol<ENVIRONMENT extends IScratchEnvironment, GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>, PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    static <SYMBOL extends IGenericScratchSymbol<?, ?, ?, ?, ?>> Products.P1<RecordCodecBuilder.Mu<SYMBOL>, IScratchClass<?, ?>> commonCodecFields(RecordCodecBuilder.Instance<SYMBOL> instance) {
        return instance.group(IScratchClass.CODEC.fieldOf("class").forGetter(IGenericScratchSymbol::getGenericScratchClass));
    };

    static <CONTEXT, SYMBOL extends IGenericScratchSymbol<?, ?, ?, ?, ?>> Products.P1<RecordContextualCodecBuilder.Mu<CONTEXT, SYMBOL>, IScratchClass<?, ?>> commonContextualCodecFields(RecordContextualCodecBuilder.Instance<CONTEXT, SYMBOL> instance) {
        return instance.group(ContextualCodec.<CONTEXT, IScratchClass<?, ?>>of(IScratchClass.CODEC).fieldOf("class").forGetter(IGenericScratchSymbol::getGenericScratchClass));
    };

    public IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> getGenericScratchClass();

    @Override
    public default boolean canEvaluate(ARGUMENTS arguments) {
        return getGenericScratchClass() != PetrolparkScratchClasses.NULL.get();
    };
};
