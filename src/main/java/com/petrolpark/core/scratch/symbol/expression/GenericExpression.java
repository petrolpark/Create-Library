package com.petrolpark.core.scratch.symbol.expression;

import java.util.function.Function;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IGenericScratchSymbol;
import com.petrolpark.util.codec.ContextualMapCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;
import com.petrolpark.util.codec.RecordContextualCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;

public abstract class GenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends ScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, PARAMETERS> implements IGenericScratchSymbol<ENVIRONMENT, GENERIC_TYPE, GENERIC_ARGUMENT, ARGUMENTS, PARAMETERS> {

    protected final IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> genericClass;

    protected GenericExpression(IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> genericClass, PARAMETERS parameters) {
        super(parameters);
        this.genericClass = genericClass;
    };

    @Override
    public final IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> getGenericScratchClass() {
        return genericClass;
    };

    public static class Type<EXPRESSION extends GenericExpression<?, ?, ?, ?, ?, ?>> implements IScratchExpression.Type<EXPRESSION> {

        protected final Function<IScratchClass<?, ?>, EXPRESSION> factory;

        private final ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec;
        private final ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec;

        public Type(Function<IScratchClass<?, ?>, EXPRESSION> factory) {
            this.factory = factory;

            codec = RecordContextualCodecBuilder.mapCodec(instance -> IGenericScratchSymbol.commonContextualCodecFields(instance).apply(instance, factory));
            streamCodec = ContextualStreamCodec.of(IScratchClass.STREAM_CODEC.map(factory, GenericExpression::getGenericScratchClass));
        };

        @Override
        public ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec() {
            return streamCodec;
        };

    };
    
};
