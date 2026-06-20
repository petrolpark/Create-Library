package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.util.codec.ContextualMapCodec;
import com.petrolpark.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;

public abstract class SimpleExpressionType<
    ENVIRONMENT extends IScratchEnvironment,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>,
    EXPRESSION extends SimpleExpressionType<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, PARAMETERS, ?>
> extends ScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, PARAMETERS> implements IScratchExpression.Type<EXPRESSION> {

    private final ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec = ContextualMapCodec.unit(self());
    private final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec = ContextualStreamCodec.unit(self());

    protected SimpleExpressionType(PARAMETERS parameters) {
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
