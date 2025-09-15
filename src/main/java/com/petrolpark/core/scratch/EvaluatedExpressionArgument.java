package com.petrolpark.core.scratch;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EvaluatedExpressionArgument<
    CONTEXT extends IScratchContext,
    TYPE,
    PARAMETERS extends ScratchParameters<CONTEXT>,
    ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>
> (
    IScratchExpression<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, ?> expression,
    ARGUMENTS arguments,
    EvaluatedExpressionArgument.Type<CONTEXT, TYPE> type
) 
    implements IScratchArgument<CONTEXT, TYPE> 
{
    public static final <CONTEXT extends IScratchContext, TYPE> EvaluatedExpressionArgument.Type<CONTEXT, TYPE> parameter(String key, IScratchClass<TYPE> scratchClass) {
        return new EvaluatedExpressionArgument.Type<>(key, scratchClass);
    };

    @Override
    public TYPE get(CONTEXT context) {
        return expression().evaluate(context, arguments(), type().scratchClass);
    };

    public static final class Type<
        CONTEXT extends IScratchContext,
        TYPE
    > implements IScratchArgument.Type<CONTEXT, TYPE, EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> {

        private static final String EXPRESSION_KEY = "expression";
        private static final String ARGUMENTS_KEY = "arguments";

        private final String key;
        private final IScratchClass<TYPE> scratchClass;

        private final MapCodec<EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> mapCodec = new MapCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public <T> DataResult<EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> decode(DynamicOps<T> ops, MapLike<T> input) {
                return IScratchExpression.CODEC.parse(ops, input.get(EXPRESSION_KEY))
                    .flatMap(expression -> {
                        try {
                            return DataResult.success((IScratchExpression<CONTEXT, TYPE, ?, ?, ?>)expression);
                        } catch (ClassCastException e) {
                            return DataResult.error(() -> String.format("Expression {} has the wrong context or return type", expression.getExpressionType()));
                        }
                    }).flatMap(expression -> decodeInternal(ops, input, expression));
            };

            @Override
            public <T> RecordBuilder<T> encode(EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return encodeInternal(input, ops, prefix);
            };

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(EXPRESSION_KEY, ARGUMENTS_KEY).map(ops::createString);
            };
            
        };

        private <T, PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>> RecordBuilder<T> encodeInternal(EvaluatedExpressionArgument<CONTEXT, TYPE, PARAMETERS, ARGUMENTS> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            //TODO
            return null;
        };

        private <T, PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>> DataResult<EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> decodeInternal(DynamicOps<T> ops, MapLike<T> input, IScratchExpression<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, ?> expression) {
            return expression.getSignature().argumentsCodec().parse(ops, input.get(ARGUMENTS_KEY)).map(arguments -> new EvaluatedExpressionArgument<>(expression, arguments, this));
        };

        private final Codec<EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> codec = mapCodec.codec();

        private final StreamCodec<RegistryFriendlyByteBuf, EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> streamCodec = new StreamCodec<>() {

            @Override
            @SuppressWarnings("unchecked")
            public EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?> decode(@Nonnull RegistryFriendlyByteBuf buffer) {
                try {
                    return decodeStreamInternal(buffer, (IScratchExpression<CONTEXT, TYPE, ?, ?, ?>)IScratchExpression.STREAM_CODEC.decode(buffer));
                } catch (ClassCastException e) {
                    throw new DecoderException("");
                }
            };

            @Override
            public void encode(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?> value) {
                encodeStreamInternal(buffer, value);
            };
            
        };

        private <PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>> EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?> decodeStreamInternal(RegistryFriendlyByteBuf buffer, IScratchExpression<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, ?> expression) {
            return new EvaluatedExpressionArgument<>(expression, expression.getSignature().argumentsStreamCodec().decode(buffer), this);
        };

        private <PARAMETERS extends ScratchParameters<CONTEXT>, ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>> void encodeStreamInternal(RegistryFriendlyByteBuf buffer, EvaluatedExpressionArgument<CONTEXT, TYPE, PARAMETERS, ARGUMENTS> value) {
            IScratchExpression.STREAM_CODEC.encode(buffer, value.expression());
            value.expression().getSignature().argumentsStreamCodec().encode(buffer, value.arguments());
        };

        protected Type(String key, IScratchClass<TYPE> scratchClass) {
            this.key = key;
            this.scratchClass = scratchClass;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public Codec<EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> codec() {
            return codec;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EvaluatedExpressionArgument<CONTEXT, TYPE, ?, ?>> streamCodec() {
            return streamCodec;
        };
        
    };
    
};
