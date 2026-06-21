package petrolpark.mc.library.core.scratch.classes;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class SimpleParseableScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends SimpleScratchClass<TYPE, DEFAULT_ARGUMENT> implements IParseableScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
};
