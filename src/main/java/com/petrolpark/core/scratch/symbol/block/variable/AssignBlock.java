package com.petrolpark.core.scratch.symbol.block.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.PetrolparkScratchBlockTypes;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.argument.VariableArgument.VariableParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.block.BinaryGenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;

public class AssignBlock<TYPE> extends BinaryGenericInstantBlock<
    IVariableScratchEnvironment,
    TYPE,
    ScratchVariableIdentifier, VariableArgument, VariableParameter,
    TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>, IScratchParameter<IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>>
> {

    public static final <TYPE> AssignBlock<TYPE> create(IScratchClass<TYPE> scratchClass) {
        return new AssignBlock<>(scratchClass);  
    };

    protected AssignBlock(IScratchClass<TYPE> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters()
            .after(genericClass.createDefaultParameter("value"))
            .after(variable("identifier"))
            .build()
        );
    };

    @Override
    public void run(IVariableScratchEnvironment environment, ScratchVariableIdentifier identifier, TYPE value) {
        environment.getVariables(identifier.scope()).set(getGenericScratchClass(), identifier.name(), value);
    };

    @Override
    public IScratchBlock.Type<?> getBlockType() {
        return PetrolparkScratchBlockTypes.ASSIGN.get();
    };

};
