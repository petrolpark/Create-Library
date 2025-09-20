// package com.petrolpark.core.scratch.argument;

// import com.petrolpark.core.scratch.environment.IScratchEnvironment;
// import com.petrolpark.core.scratch.procedure.IScratchContext;

// public record ScopeArgument<ENVIRONMENT extends IScratchEnvironment, SCOPE> (int recursion) implements IScratchArgument<ENVIRONMENT, SCOPE> {

//     @Override
//     public SCOPE get(ENVIRONMENT environment, IScratchContext<?> context) {
//         for (int i = 0; i < recursion; i++) scope = scope.enclosingContextHolder();
//         //if (scope instanceof SCOPE rightScope) return rightScope;
//     };

//     @Override
//     public IScratchParameter<ENVIRONMENT, SCOPE, ? extends IScratchArgument<? super ENVIRONMENT, SCOPE>> parameter() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'parameter'");
//     };
    
// };
