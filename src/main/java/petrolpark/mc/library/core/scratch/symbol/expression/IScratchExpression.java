package petrolpark.mc.library.core.scratch.symbol.expression;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.IScratchSymbol;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public interface IScratchExpression<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> {

    /**
     * Use {@link #CODEC} instead.
     */
    static ContextualCodec<IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?, ?>> TYPED_CODEC = ContextualCodec.dispatch(PetrolparkRegistries.SCRATCH_EXPRESSION_TYPES.byNameCodec(), IScratchExpression::getExpressionType, IScratchExpression.Type::codec);

    public static ContextualCodec<IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?, ?>> CODEC = ContextualCodec.lazyInitialized(() -> TYPED_CODEC);

    public static ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, IScratchExpression<?, ?, ?, ?>> STREAM_CODEC = ContextualStreamCodec.dispatch(ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_EXPRESSION_TYPE), IScratchExpression::getExpressionType, IScratchExpression.Type::streamCodec);

    public RETURN_TYPE evaluate(ENVIRONMENT environment, ARGUMENTS arguments);

    public IScratchClass<RETURN_TYPE, ?> getReturnClass();

    public IScratchExpression.Type<?> getExpressionType();

    public interface Type<EXPRESSION extends IScratchExpression<?, ?, ?, ?>> extends IScratchSymbol.Type<EXPRESSION> {};
};
