package petrolpark.mc.library.core.scratch.symbol.expression.logic;

import static petrolpark.mc.library.core.scratch.ScratchParameters.parameters;
import static petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.booleanParameter;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.GenericExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.TernaryGenericExpression;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchExpressionTypes;;

public final class ConditionalExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends TernaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT,
    TYPE,
    Boolean, ExpressionOrDropdownArgument<IScratchEnvironment, Boolean>, ExpressionOrDropdownParameter<IScratchEnvironment, Boolean>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>,
    TYPE, ARGUMENT, IScratchParameter<IScratchEnvironment, TYPE, ARGUMENT>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ConditionalExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> genericClass) {
        return new ConditionalExpression<>(genericClass);
    };

    protected ConditionalExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters()
            .after(genericClass.createDefaultParameter("fail"))
            .after(genericClass.createDefaultParameter("pass"))
            .after(booleanParameter("condition"))
        );
    };

    @Override
    public TYPE evaluate(IScratchEnvironment environment, Boolean argument1, TYPE argument2, TYPE argument3) {
        if (argument1) {
            return argument2;
        } else {
            return argument3;
        }
    };

    @Override
    public IScratchClass<TYPE, ARGUMENT> getReturnClass() {
        return getGenericScratchClass();
    };

    @Override
    public GenericExpression.Type<ConditionalExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.CONDITIONAL.get();
    };
    
};
