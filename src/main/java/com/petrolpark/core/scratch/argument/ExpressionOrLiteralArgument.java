package com.petrolpark.core.scratch.argument;

import java.util.Optional;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record ExpressionOrLiteralArgument<ENVIRONMENT extends IScratchEnvironment, TYPE> (
    TYPE value,
    Optional<ExpressionArgument<ENVIRONMENT, TYPE, ?>> expression,
    ExpressionOrLiteralParameter<ENVIRONMENT, TYPE> parameter
) implements IScratchArgument<ENVIRONMENT, TYPE> {

    public static <ENVIRONMENT extends IScratchEnvironment> ExpressionOrLiteralParameter<ENVIRONMENT, Long> integerParameter(String key) {
        return new ExpressionOrLiteralParameter<>(key, PetrolparkScratchClasses.INTEGER.get());
    };

    public static <ENVIRONMENT extends IScratchEnvironment> ExpressionOrLiteralParameter<ENVIRONMENT, Double> realParameter(String key) {
        return new ExpressionOrLiteralParameter<>(key, PetrolparkScratchClasses.REAL.get());
    };

    public static <ENVIRONMENT extends IScratchEnvironment> ExpressionOrLiteralParameter<ENVIRONMENT, String> stringParameter(String key) {
        return new ExpressionOrLiteralParameter<>(key, PetrolparkScratchClasses.STRING.get());
    };

    @Override
    public TYPE get(ENVIRONMENT environment, IScratchContext<?> context) {
        return expression().map(expression -> expression.get(environment, context)).orElse(value());
    };

    public static final class ExpressionOrLiteralParameter<ENVIRONMENT extends IScratchEnvironment, TYPE> implements IScratchParameter<ENVIRONMENT, TYPE, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> {

        private final ExpressionParameter<ENVIRONMENT, TYPE> expressionParameter;
        private final ContextualCodec<IScratchContextHolder<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> codec;
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> streamCodec;

        public ExpressionOrLiteralParameter(String key, IScratchClass<TYPE> scratchClass) {
            expressionParameter = new ExpressionParameter<>(key, scratchClass);
            codec = RecordContextualCodecBuilder.create(instance -> instance.group(
                ContextualCodec.<IScratchContextHolder<?>, TYPE>of(scratchClass.codec()).fieldOf("literal").forGetter(ExpressionOrLiteralArgument::value),
                expressionParameter.argumentCodec().optionalFieldOf("expression").forGetter(ExpressionOrLiteralArgument::expression)
            ).apply(instance, (value, expression) -> new ExpressionOrLiteralArgument<>(value, expression, this)));
            streamCodec = ContextualStreamCodec.composite(
                ContextualStreamCodec.of(scratchClass.streamCodec()), ExpressionOrLiteralArgument::value,
                ContextualStreamCodec.optional(expressionParameter.argumentStreamCodec()), ExpressionOrLiteralArgument::expression,
                (value, expression) -> new ExpressionOrLiteralArgument<>(value, expression, this)
            );
        };

        @Override
        public String key() {
            return expressionParameter.key();
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
    
};
