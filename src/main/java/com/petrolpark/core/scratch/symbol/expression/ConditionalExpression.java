package com.petrolpark.core.scratch.symbol.expression;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;
import static com.petrolpark.core.scratch.argument.ExpressionArgument.parameter;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchArguments.And;
import com.petrolpark.core.scratch.ScratchArguments.Just;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;;

public final class ConditionalExpression<TYPE> extends GenericExpression<
    IScratchEnvironment,
    TYPE,
    TYPE,
    And<
        IScratchEnvironment, Boolean, ExpressionArgument<IScratchEnvironment, Boolean, ?>, And<
        IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, Just<
        IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>
    >>>, ConditionalExpression<TYPE>
> {

    public static final GenericExpression.Type<ConditionalExpression<?>> TYPE = new GenericExpression.Type<>(ConditionalExpression::create);

    protected static final <TYPE> ConditionalExpression<TYPE> create(IScratchClass<TYPE> genericClass) {
        return new ConditionalExpression<>(genericClass);
    };

    protected ConditionalExpression(IScratchClass<TYPE> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("fail"))
            .after(genericClass.createDefaultParameter("pass"))
            .after(parameter("condition", PetrolparkScratchClasses.BOOLEAN.get()))
        );
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, ScratchArguments.And<IScratchEnvironment, Boolean, ExpressionArgument<IScratchEnvironment, Boolean, ?>, ScratchArguments.And<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>, ScratchArguments.Just<IScratchEnvironment, TYPE, IScratchArgument<IScratchEnvironment, TYPE>>>> arguments) {
        if (arguments.get(environment)) {
            return arguments.next().get(environment);
        } else {
            return arguments.next().next().get(environment);
        }
    };

    @Override
    public IScratchClass<TYPE> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public IScratchExpression.Type<ConditionalExpression<TYPE>> getExpressionType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExpressionType'");
    };
    
};
