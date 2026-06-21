package petrolpark.mc.library.core.scratch.environment.variable;

import petrolpark.mc.library.core.scratch.classes.IScratchClass;

public interface IScratchVariables {

    public <TYPE> void create(IScratchClass<TYPE, ?> scratchClass, String identifier, TYPE initialValue);

    public <TYPE> boolean has(IScratchClass<TYPE, ?> scratchClass, String identifier);
    
    public <TYPE> TYPE get(IScratchClass<TYPE, ?> scratchClass, String identifier);

    public <TYPE> TYPE getFallback(IScratchClass<TYPE, ?> scratchClass);

    /**
     * @param <TYPE>
     * @param scratchClass
     * @param identifier
     * @param value
     * @return Whether the variable exists and was set
     */
    public <TYPE> boolean set(IScratchClass<TYPE, ?> scratchClass, String identifier, TYPE value);
};
