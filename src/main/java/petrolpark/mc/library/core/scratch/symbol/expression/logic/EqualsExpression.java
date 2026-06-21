package petrolpark.mc.library.core.scratch.symbol.expression.logic;

import static petrolpark.mc.library.core.scratch.ScratchParameters.parameters;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.BinaryGenericExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.IScratchExpression;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchExpressionTypes;

public final class EqualsExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends BinaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT,
    Boolean,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> EqualsExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> scratchClass) {
        return new EqualsExpression<>(scratchClass);
    };

    protected EqualsExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("value2"))
            .after(genericClass.createDefaultParameter("value1"))
            .build()
        );
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, TYPE argument1, TYPE argument2) {
        return argument1.equals(argument2);
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public IScratchExpression.Type<EqualsExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.EQUALS.get();
    };
    
};