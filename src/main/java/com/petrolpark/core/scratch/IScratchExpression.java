package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchExpression<CONTEXT extends IScratchContext, RETURN_TYPE, PARAMETERS extends ScratchParameters<? super CONTEXT>> extends IScratchSymbol<CONTEXT, PARAMETERS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchExpression<?, ?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_EXPRESSION_TYPES.byNameCodec().dispatch(IScratchExpression::getExpressionType, IScratchExpression.Type::codec);

    public static Codec<IScratchExpression<?, ?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);
    
    public <TARGET_RETURN_TYPE extends RETURN_TYPE> TARGET_RETURN_TYPE evaluate(CONTEXT context, PARAMETERS arguments, IScratchClass<TARGET_RETURN_TYPE> returnClass);

    public IScratchClass<? extends RETURN_TYPE> getReturnClass(CONTEXT context, PARAMETERS arguments);

    public IScratchExpression.Type<?> getExpressionType();

    public record Type<EXPRESSION extends IScratchExpression<?, ?, ?>>(MapCodec<EXPRESSION> codec, StreamCodec<? super RegistryFriendlyByteBuf, EXPRESSION> streamCodec) implements IScratchSymbol.Type<EXPRESSION> {};
};
