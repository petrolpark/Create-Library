package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;

public abstract class SimpleExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    EXPRESSION extends SimpleExpressionType<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, EXPRESSION>
> extends ScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, EXPRESSION> implements IScratchExpression.Type<EXPRESSION> {

    private final ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec = ContextualMapCodec.unit(self());
    private final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec = ContextualStreamCodec.unit(self());

    protected SimpleExpressionType(ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
    };

    protected abstract EXPRESSION self();

    @Override
    public final ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec() {
        return codec;
    };

    @Override
    public final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec() {
        return streamCodec;
    };

    @Override
    public final IScratchExpression.Type<EXPRESSION> getExpressionType() {
        return this;
    };
    
    
};
