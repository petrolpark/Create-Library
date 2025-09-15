// package com.petrolpark.core.scratch;

// public abstract class GenericScratchExpression<CONTEXT extends IScratchContext, PARAMETERS extends ScratchParameters.More<? super CONTEXT, ?>> 
//     implements IScratchExpression<CONTEXT, Object, ScratchParameters.And<CONTEXT, IScratchClass<?>, PARAMETERS>>
// {
//     @Override
//     public final <TARGET_RETURN_TYPE> TARGET_RETURN_TYPE evaluate(CONTEXT context, ScratchParameters.And<CONTEXT, IScratchClass<?>, PARAMETERS> arguments, IScratchClass<TARGET_RETURN_TYPE> returnClass) {
//         if (returnClass != getReturnClass(context, arguments)) throw new IllegalStateException();
//         return evaluateGeneric(context, arguments.next(), returnClass);
//     };

//     public abstract <TARGET_RETURN_TYPE> TARGET_RETURN_TYPE evaluateGeneric(CONTEXT context, PARAMETERS arguments, IScratchClass<TARGET_RETURN_TYPE> returnClass);

//     @Override
//     public final IScratchClass<? extends Object> getReturnClass(CONTEXT context, ScratchParameters.And<CONTEXT, IScratchClass<?>, PARAMETERS> arguments) {
//         return arguments.get(context);
//     };
    
// };
