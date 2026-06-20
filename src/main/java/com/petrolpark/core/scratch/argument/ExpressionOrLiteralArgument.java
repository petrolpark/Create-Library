package com.petrolpark.core.scratch.argument;

import java.util.Optional;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;
import com.petrolpark.util.codec.ContextualCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;
import com.petrolpark.util.codec.RecordContextualCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record ExpressionOrLiteralArgument<ENVIRONMENT extends IScratchEnvironment, TYPE> (
    TYPE value,
    Optional<ExpressionArgument<ENVIRONMENT, TYPE>> expression,
    ExpressionOrLiteralParameter<ENVIRONMENT, TYPE> parameter
) implements IScratchArgument<ENVIRONMENT, TYPE>, IScratchContextHolder {

    public static <ENVIRONMENT extends IScratchEnvironment> ExpressionOrLiteralArgument<ENVIRONMENT, Long> integerArgument(Long value, ExpressionOrLiteralParameter<ENVIRONMENT, Long> parameter) {
        return new ExpressionOrLiteralArgument<>(value, Optional.empty(), parameter);
    };

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
    public TYPE get(ENVIRONMENT environment) {
        return expression().map(expression -> expression.get(environment)).orElse(value());
    };

    @Override
    public boolean canEvaluate() {
        return expression().map(ExpressionArgument::canEvaluate).orElse(true);
    };

    @Override
    public <CONTEXT extends IScratchContext<CONTEXT>> void populateContext(IScratchContextProvider<CONTEXT> contextProvider, CONTEXT context) {
        expression().ifPresent(expression -> expression.populateContext(contextProvider, context));
    };

    public static final class ExpressionOrLiteralParameter<ENVIRONMENT extends IScratchEnvironment, TYPE> implements IExpressionScratchParameter<ENVIRONMENT, TYPE, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> {

        private final IScratchClass<TYPE, ?> scratchClass;
        private final ExpressionParameter<ENVIRONMENT, TYPE> expressionParameter;
        private final ContextualCodec<IScratchContextProvider<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> codec;
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> streamCodec;

        public ExpressionOrLiteralParameter(String key, IScratchClass<TYPE, ?> scratchClass) {
            this.scratchClass = scratchClass;
            expressionParameter = new ExpressionParameter<>(key, scratchClass);

            codec = RecordContextualCodecBuilder.create(instance -> instance.group(
                ContextualCodec.<IScratchContextProvider<?>, TYPE>of(scratchClass.codec()).fieldOf("literal").forGetter(ExpressionOrLiteralArgument::value),
                expressionParameter.argumentCodec().optionalFieldOf("expression").forGetter(ExpressionOrLiteralArgument::expression)
            ).apply(instance, (value, expression) -> new ExpressionOrLiteralArgument<>(value, expression, this)));
            
            streamCodec = ContextualStreamCodec.composite(
                ContextualStreamCodec.of(scratchClass.streamCodec()), ExpressionOrLiteralArgument::value,
                ContextualStreamCodec.optional(expressionParameter.argumentStreamCodec()), ExpressionOrLiteralArgument::expression,
                (value, expression) -> new ExpressionOrLiteralArgument<>(value, expression, this)
            );
        };

        public ExpressionOrLiteralArgument<ENVIRONMENT, TYPE> argument(TYPE value) {
            return new ExpressionOrLiteralArgument<>(value, Optional.empty(), this);
        };

        @Override
        public <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ExpressionOrLiteralArgument<ENVIRONMENT, TYPE> pass(ExpressionAndArguments<ENVIRONMENT, TYPE, ARGUMENTS> expressionAndArguments) {
            return new ExpressionOrLiteralArgument<>(scratchClass.fallback(), Optional.of(new ExpressionArgument<>(expressionAndArguments, expressionParameter)), this);
        };

        @Override
        public String key() {
            return expressionParameter.key();
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionOrLiteralArgument<ENVIRONMENT, TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
    
};
