package petrolpark.mc.library.core.scratch.classes;

import petrolpark.mc.library.core.scratch.argument.ExpressionArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class ExpressionDefaultParameterSimpleScratchClass<TYPE> extends SimpleScratchClass<TYPE, ExpressionArgument<IScratchEnvironment, TYPE>> {
    
    @Override
    public ExpressionParameter<IScratchEnvironment, TYPE> createDefaultParameter(String key) {
        return ExpressionArgument.parameter(key, this);
    };
};
