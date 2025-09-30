package com.petrolpark.core.scratch.symbol.expression;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IGenericScratchSymbol;

public abstract class GenericExpression<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE,
    RETURN_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    EXPRESSION extends GenericExpression<ENVIRONMENT, GENERIC_TYPE, RETURN_TYPE, ARGUMENTS, ?>
> extends ScratchExpression<ENVIRONMENT, RETURN_TYPE, ARGUMENTS, EXPRESSION> implements IGenericScratchSymbol<ENVIRONMENT, GENERIC_TYPE, ARGUMENTS> {

    protected final IScratchClass<GENERIC_TYPE, ?> genericClass;

    protected GenericExpression(IScratchClass<GENERIC_TYPE, ?> genericClass, ScratchParameters<ENVIRONMENT, ARGUMENTS> parameters) {
        super(parameters);
        this.genericClass = genericClass;
    };

    @Override
    public final IScratchClass<GENERIC_TYPE, ?> getGenericScratchClass() {
        return genericClass;
    };
    
};
