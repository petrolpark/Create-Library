package com.petrolpark.core.scratch.symbol.expression;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class SimpleExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    EXPRESSION extends SimpleExpressionType<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, ?>
> extends ScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, EXPRESSION> implements IScratchExpression.Type<EXPRESSION> {

    private final MapCodec<EXPRESSION> codec = MapCodec.unit(self());
    private final StreamCodec<ByteBuf, EXPRESSION> streamCodec = StreamCodec.unit(self());

    protected SimpleExpressionType(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };

    protected abstract EXPRESSION self();

    @Override
    public final MapCodec<EXPRESSION> codec() {
        return codec;
    };

    @Override
    public final StreamCodec<ByteBuf, EXPRESSION> streamCodec() {
        return streamCodec;
    };

    @Override
    public final IScratchExpression.Type<EXPRESSION> getExpressionType() {
        return this;
    };
    
    
};
