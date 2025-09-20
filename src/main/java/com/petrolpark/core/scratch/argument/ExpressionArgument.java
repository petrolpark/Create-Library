package com.petrolpark.core.scratch.argument;

import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record ExpressionArgument<
    ENVIRONMENT extends IScratchEnvironment,
    TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> (
    IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS, ?> expression,
    ARGUMENTS arguments,
    ExpressionParameter<ENVIRONMENT, TYPE> parameter
) 
    implements IScratchArgument<ENVIRONMENT, TYPE> 
{
    public static final <ENVIRONMENT extends IScratchEnvironment, TYPE> ExpressionParameter<ENVIRONMENT, TYPE> parameter(String key, IScratchClass<TYPE> scratchClass) {
        return new ExpressionParameter<>(key, scratchClass);
    };

    @Override
    public TYPE get(ENVIRONMENT environment, IScratchContext<?> contxt) {
        return expression().evaluate(environment, contxt, arguments());
    };

    public static final class ExpressionParameter<
        ENVIRONMENT extends IScratchEnvironment,
        TYPE
    > implements IScratchParameter<ENVIRONMENT, TYPE, ExpressionArgument<ENVIRONMENT, TYPE, ?>> {

        private static final String EXPRESSION_KEY = "expression";
        private static final String ARGUMENTS_KEY = "arguments";

        private final String key;
        private final IScratchClass<TYPE> scratchClass;

        private final ContextualMapCodec<IScratchContextHolder<?>, ExpressionArgument<ENVIRONMENT, TYPE, ?>> mapCodec = new ContextualMapCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public <T> DataResult<ExpressionArgument<ENVIRONMENT, TYPE, ?>> decode(final DynamicOps<T> ops, final IScratchContextHolder<?> context, final MapLike<T> input) {
                return IScratchExpression.CODEC.parse(ops, input.get(EXPRESSION_KEY))
                    .flatMap(expression -> {
                        try {
                            if (expression.getReturnClass() != scratchClass) return DataResult.error(() -> String.format("Expression {} has wrong return type", expression.getExpressionType()));
                            return DataResult.success((IScratchExpression<ENVIRONMENT, TYPE, ?, ?>)expression);
                        } catch (ClassCastException e) {
                            return DataResult.error(() -> String.format("Expression {} has the wrong context or return type", expression.getExpressionType()));
                        }
                    }).flatMap(expression -> decodeInternal(ops, context, input, expression));
            };

            @Override
            public <T> RecordBuilder<T> encode(final ExpressionArgument<ENVIRONMENT, TYPE, ?> input, final IScratchContextHolder<?> context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                return encodeInternal(input, context, ops, prefix);
            };

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(EXPRESSION_KEY, ARGUMENTS_KEY).map(ops::createString);
            };
            
        };

        private <T, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> RecordBuilder<T> encodeInternal(final ExpressionArgument<ENVIRONMENT, TYPE, ARGUMENTS> input, final IScratchContextHolder<?> context, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
            //TODO
            return null;
        };

        private <T, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> DataResult<ExpressionArgument<ENVIRONMENT, TYPE, ?>> decodeInternal(final DynamicOps<T> ops, final IScratchContextHolder<?> context, final MapLike<T> input, final IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS, ?> expression) {
            return expression.getParameters().argumentsCodec().parse(ops, context, input.get(ARGUMENTS_KEY)).map(arguments -> new ExpressionArgument<>(expression, arguments, this));
        };

        private final ContextualCodec<IScratchContextHolder<?>, ExpressionArgument<ENVIRONMENT, TYPE, ?>> codec = mapCodec.codec();

        private final ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextHolder<?>, ExpressionArgument<ENVIRONMENT, TYPE, ?>> streamCodec = new ContextualStreamCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public ExpressionArgument<ENVIRONMENT, TYPE, ?> decode(RegistryFriendlyByteBuf buffer, final IScratchContextHolder<?> context) {
                try {
                    return decodeStreamInternal(buffer, context, (IScratchExpression<ENVIRONMENT, TYPE, ?, ?>)IScratchExpression.STREAM_CODEC.decode(buffer));
                } catch (ClassCastException e) {
                    throw new DecoderException("");
                }
            };

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, IScratchContextHolder<?> context, ExpressionArgument<ENVIRONMENT, TYPE, ?> value) {
                encodeStreamInternal(buffer, context, value);
            };
            
        };

        private <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ExpressionArgument<ENVIRONMENT, TYPE, ?> decodeStreamInternal(RegistryFriendlyByteBuf buffer, IScratchContextHolder<?> context, IScratchExpression<ENVIRONMENT, TYPE, ARGUMENTS, ?> expression) {
            return new ExpressionArgument<>(expression, expression.getParameters().argumentsStreamCodec().decode(buffer, context), this);
        };

        private <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> void encodeStreamInternal(RegistryFriendlyByteBuf buffer, IScratchContextHolder<?> context, ExpressionArgument<ENVIRONMENT, TYPE, ARGUMENTS> value) {
            IScratchExpression.STREAM_CODEC.encode(buffer, value.expression());
            value.expression().getParameters().argumentsStreamCodec().encode(buffer, context, value.arguments());
        };

        protected ExpressionParameter(String key, IScratchClass<TYPE> scratchClass) {
            this.key = key;
            this.scratchClass = scratchClass;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, ExpressionArgument<ENVIRONMENT, TYPE, ?>> codec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextHolder<?>, ExpressionArgument<ENVIRONMENT, TYPE, ?>> streamCodec() {
            return streamCodec;
        };
        
    };
    
};
