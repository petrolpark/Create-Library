package petrolpark.mc.library.core.scratch.symbol.expression.list;

import static petrolpark.mc.library.core.scratch.ScratchParameters.parameters;
import static petrolpark.mc.library.core.scratch.argument.ExpressionArgument.listParameter;

import java.util.List;

import petrolpark.mc.library.core.scratch.argument.ExpressionArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.classes.IntegerScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.GenericExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.UnaryGenericExpression;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchExpressionTypes;

public final class ListLengthExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends UnaryGenericExpression<
    IScratchEnvironment,
    TYPE, ARGUMENT, Long,
    List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>, ExpressionParameter<IScratchEnvironment, List<TYPE>>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ListLengthExpression<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> scratchClass) {
        return new ListLengthExpression<>(scratchClass);
    };

    protected ListLengthExpression(IScratchClass<TYPE, ARGUMENT> genericClass) {
        super(genericClass, parameters().after(listParameter("list", genericClass)).build());
    };

    @Override
    public IntegerScratchClass getReturnClass() {
        return PetrolparkScratchClasses.INTEGER.get();
    };

    @Override
    public GenericExpression.Type<?> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_LENGTH.get();
    };

    @Override
    public Long evaluate(IScratchEnvironment environment, List<TYPE> list) {
        return (long)list.size();
    };
    
};
