package com.petrolpark.core.scratch;

import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EvaluatedExpressionArgument<
    CONTEXT extends IScratchContext,
    TYPE,
    PARAMETERS extends ScratchParameters<? super CONTEXT>,
    ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>,
    EXPRESSION extends IScratchExpression<CONTEXT, ? super TYPE, PARAMETERS, ?>
> (
    IScratchClass<TYPE> scratchClass,
    EXPRESSION expression,
    ARGUMENTS arguments,
    EvaluatedExpressionArgument.Type<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, EXPRESSION> type
) 
    implements IScratchArgument<CONTEXT, TYPE> 
{

    @Override
    public TYPE get(CONTEXT context) {
        return expression().evaluate(context, arguments().asParameters(), scratchClass());
    };

    @Override
    public EvaluatedExpressionArgument.Type<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, EXPRESSION> getType() {
        return type();
    };

    public record Type<
        CONTEXT extends IScratchContext,
        TYPE,
        PARAMETERS extends ScratchParameters<? super CONTEXT>,
        ARGUMENTS extends ScratchArguments<CONTEXT, PARAMETERS>,
        EXPRESSION extends IScratchExpression<CONTEXT, ? super TYPE, PARAMETERS, ?>
    >(
        Codec<EvaluatedExpressionArgument<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, EXPRESSION>> codec,
        StreamCodec<RegistryFriendlyByteBuf, EvaluatedExpressionArgument<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, EXPRESSION>> streamCodec
    ) implements IScratchArgument.Type<CONTEXT, TYPE, EvaluatedExpressionArgument<CONTEXT, TYPE, PARAMETERS, ARGUMENTS, EXPRESSION>> {
        
    };
    
};
