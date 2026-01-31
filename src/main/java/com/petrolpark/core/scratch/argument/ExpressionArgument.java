package com.petrolpark.core.scratch.argument;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.classes.ListScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record ExpressionArgument<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE
> (
    ExpressionAndArguments<? super ENVIRONMENT, TYPE, ?> expressionAndArguments,
    ExpressionParameter<? super ENVIRONMENT, TYPE> parameter
) 
    implements IScratchArgument<ENVIRONMENT, TYPE>, IScratchContextHolder 
{

    public static final <ENVIRONMENT extends IScratchEnvironment, TYPE> ExpressionParameter<ENVIRONMENT, TYPE> parameter(String key, IScratchClass<TYPE> scratchClass) {
        return new ExpressionParameter<>(key, scratchClass);
    };

    public static final <TYPE> ExpressionParameter<IScratchEnvironment, List<TYPE>> listParameter(String key, IScratchClass<TYPE> scratchClass) {
        return ListScratchClass.create(scratchClass).createDefaultParameter(key);
    };

    @Override
    public TYPE get(ENVIRONMENT environment) {
        return expressionAndArguments().evaluate(environment);
    };

    @Override
    public boolean canEvaluate() {
        return expressionAndArguments().arguments().canEvaluate();
    };

    @Override
    public <CONTEXT extends IScratchContext<CONTEXT>> void populateContext(IScratchContextProvider<CONTEXT> contextProvider, CONTEXT context) {
        expressionAndArguments().arguments().populateContext(contextProvider, context);
    };

    public static final class ExpressionParameter<
        ENVIRONMENT extends IScratchEnvironment,
        TYPE
    > implements IExpressionScratchParameter<ENVIRONMENT, TYPE, ExpressionArgument<ENVIRONMENT, TYPE>> {

        private static final String EXPRESSION_KEY = "expression";
        private static final String ARGUMENTS_KEY = "arguments";

        private final String key;
        private final IScratchClass<TYPE> scratchClass;

        private final ContextualMapCodec<IScratchContextProvider<?>, ExpressionArgument<ENVIRONMENT, TYPE>> mapCodec = new ContextualMapCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public <T> DataResult<ExpressionArgument<ENVIRONMENT, TYPE>> decode(final DynamicOps<T> ops, final IScratchContextProvider<?> context, final MapLike<T> input) {
                return IScratchExpression.CODEC.parse(ops, context.environmentType(), input.get(EXPRESSION_KEY))
                    .flatMap(expression -> {
                        try {
                            if (!expression.getReturnClass().equals(scratchClass)) return DataResult.error(() -> String.format("Expression {} has wrong return class", expression.getExpressionType()));
                            return DataResult.success((IScratchExpression<ENVIRONMENT, TYPE, ?>)expression);
                        } catch (ClassCastException e) {
                            return DataResult.error(() -> String.format("Expression {} has the wrong environment or return class", expression.getExpressionType()));
                        }
                    }).flatMap(expression -> decodeInternal(ops, context, input, expression));
            };

            @Override
            public <T> RecordBuilder<T> encode(final ExpressionArgument<ENVIRONMENT, TYPE> input, final IScratchContextProvider<?> context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                return encodeInternal(input.expressionAndArguments(), context, ops, prefix);
            };

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(EXPRESSION_KEY, ARGUMENTS_KEY).map(ops::createString);
            };
            
        };

        private <T, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> DataResult<ExpressionArgument<ENVIRONMENT, TYPE>> decodeInternal(final DynamicOps<T> ops, final IScratchContextProvider<?> context, final MapLike<T> input, final IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS> expression) {
            return expression.getParameters().argumentsCodec().parse(ops, context, input.get(ARGUMENTS_KEY)).map(arguments -> new ExpressionArgument<>(new ExpressionAndArguments<>(expression, arguments), this));
        };

        private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> RecordBuilder<T> encodeInternal(final ExpressionAndArguments<? super ENVIRONMENT, TYPE, ARGUMENTS> input, final IScratchContextProvider<?> context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
            prefix.add(EXPRESSION_KEY, IScratchExpression.CODEC.encodeStart(ops, context.environmentType(), input.expression()));
            prefix.add(ARGUMENTS_KEY, input.expression().getParameters().argumentsCodec().encodeStart(ops, context, input.arguments()));
            return prefix;
        };

        private final ContextualCodec<IScratchContextProvider<?>, ExpressionArgument<ENVIRONMENT, TYPE>> codec = mapCodec.codec();

        private final ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionArgument<ENVIRONMENT, TYPE>> streamCodec = new ContextualStreamCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public ExpressionArgument<ENVIRONMENT, TYPE> decode(RegistryFriendlyByteBuf buffer, final IScratchContextProvider<?> context) {
                final IScratchExpression<?, ?, ?> expression = IScratchExpression.STREAM_CODEC.decode(buffer, context.environmentType());
                try {
                    if (!expression.getReturnClass().equals(scratchClass)) throw new DecoderException(String.format("Expression {} has wrong return class", expression.getExpressionType()));
                    return decodeStreamInternal(buffer, context, (IScratchExpression<ENVIRONMENT, TYPE, ?>)expression);
                } catch (ClassCastException e) {
                    throw new DecoderException(String.format("Expression {} has the wrong Environment or return class", expression.getExpressionType()));
                }
            };

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, IScratchContextProvider<?> context, ExpressionArgument<ENVIRONMENT, TYPE> value) {
                encodeStreamInternal(buffer, context, value.expressionAndArguments());
            };
            
        };

        private <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ExpressionArgument<ENVIRONMENT, TYPE> decodeStreamInternal(RegistryFriendlyByteBuf buffer, IScratchContextProvider<?> context, IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS> expression) {
            return new ExpressionArgument<>(new ExpressionAndArguments<>(expression, expression.getParameters().argumentsStreamCodec().decode(buffer, context)), this);
        };

        private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> void encodeStreamInternal(RegistryFriendlyByteBuf buffer, IScratchContextProvider<?> context, ExpressionAndArguments<? super ENVIRONMENT, TYPE, ARGUMENTS> value) {
            IScratchExpression.STREAM_CODEC.encode(buffer, context.environmentType(), value.expression());
            value.expression().getParameters().argumentsStreamCodec().encode(buffer, context, value.arguments());
        };

        protected static <ENVIRONMENT extends IScratchEnvironment, TYPE> ExpressionParameter<ENVIRONMENT, TYPE> create(String key, IScratchClass<TYPE> scratchClass) {
            return new ExpressionParameter<>(key, scratchClass);
        };

        protected ExpressionParameter(String key, IScratchClass<TYPE> scratchClass) {
            this.key = key;
            this.scratchClass = scratchClass;
        };

        @Override
        public <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ExpressionArgument<ENVIRONMENT, TYPE> argument(ExpressionAndArguments<ENVIRONMENT, TYPE, ARGUMENTS> expressionAndArguments) {
            return new ExpressionArgument<>(expressionAndArguments, this);
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, ExpressionArgument<ENVIRONMENT, TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionArgument<ENVIRONMENT, TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

        @Override
        public String toString() {
            return "ExpressionArgument[\"" + key() + "\"]";
        };
        
    };
    
};
