package petrolpark.mc.library.core.scratch.classes;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public interface IParseableScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends IScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
    public TYPE parse(String string);
};
