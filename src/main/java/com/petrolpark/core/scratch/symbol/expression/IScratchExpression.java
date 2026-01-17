package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public interface IScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static ContextualCodec<IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?>> TYPED_CODEC = ContextualCodec.dispatch(PetrolparkRegistries.SCRATCH_EXPRESSION_TYPES.byNameCodec(), IScratchExpression::getExpressionType, IScratchExpression.Type::codec);

    public static ContextualCodec<IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?>> CODEC = ContextualCodec.lazyInitialized(() -> TYPED_CODEC);

    public static ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?>> STREAM_CODEC = ContextualStreamCodec.dispatch(ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_EXPRESSION_TYPE), IScratchExpression::getExpressionType, IScratchExpression.Type::streamCodec);

    public RETURN_TYPE evaluate(ENVIRONMENT environment, ARGUMENTS arguments);

    public IScratchClass<RETURN_TYPE> getReturnClass();

    public IScratchExpression.Type<?> getExpressionType();

    public interface Type<EXPRESSION extends IScratchExpression<?, ?, ?>> extends IScratchSymbol.Type<EXPRESSION> {};
};
