package com.petrolpark.core.scratch.symbol.expression;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    EXPRESSION extends IScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, ?>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static Codec<IScratchExpression<?, ?, ?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_EXPRESSION_TYPES.byNameCodec().dispatch(IScratchExpression::getExpressionType, IScratchExpression.Type::codec);

    public static Codec<IScratchExpression<?, ?, ?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static StreamCodec<RegistryFriendlyByteBuf, IScratchExpression<?, ?, ?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_EXPRESSION_TYPE).dispatch(IScratchExpression::getExpressionType, IScratchExpression.Type::streamCodec);

    public RETURN_TYPE evaluate(ENVIRONMENT environment, ARGUMENTS arguments);

    public IScratchClass<RETURN_TYPE, ?> getReturnClass();

    public IScratchExpression.Type<EXPRESSION> getExpressionType();

    public interface Type<EXPRESSION extends IScratchExpression<?, ?, ?, ?>> extends IScratchSymbol.Type<EXPRESSION> {};
};
