package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchExpression<
    CONTEXT extends IScratchContext,
    RETURN_TYPE,
    PARAMETERS extends ScratchParameters<CONTEXT>,
    ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>,
    EXPRESSION extends IScratchExpression<CONTEXT, RETURN_TYPE, PARAMETERS, ARGUMENTS, ?>
> extends IScratchSymbol<CONTEXT, PARAMETERS, ARGUMENTS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchExpression<?, ?, ?, ?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_EXPRESSION_TYPES.byNameCodec().dispatch(IScratchExpression::getExpressionType, IScratchExpression.Type::codec);

    public static Codec<IScratchExpression<?, ?, ?, ?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static StreamCodec<RegistryFriendlyByteBuf, IScratchExpression<?, ?, ?, ?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_EXPRESSION_TYPE).dispatch(IScratchExpression::getExpressionType, IScratchExpression.Type::streamCodec);
    
    public <TARGET_RETURN_TYPE extends RETURN_TYPE> TARGET_RETURN_TYPE evaluate(CONTEXT context, ARGUMENTS arguments, IScratchClass<TARGET_RETURN_TYPE> returnClass);

    public IScratchClass<? extends RETURN_TYPE> getReturnClass(CONTEXT context, ARGUMENTS arguments);

    public IScratchExpression.Type<EXPRESSION> getExpressionType();

    public record Type<EXPRESSION extends IScratchExpression<?, ?, ?, ?, ?>>(MapCodec<EXPRESSION> codec, StreamCodec<? super RegistryFriendlyByteBuf, EXPRESSION> streamCodec) implements IScratchSymbol.Type<EXPRESSION> {};
};
