package com.petrolpark.core.scratch.symbol.block.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.argument.VariableArgument.VariableParameter;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.block.BinaryGenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.registry.scratch.PetrolparkScratchBlockTypes;

public class AssignBlock<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends BinaryGenericInstantBlock<
    IVariableScratchEnvironment,
    TYPE, ARGUMENT,
    ScratchVariableIdentifier, VariableArgument, VariableParameter,
    TYPE, ARGUMENT, IScratchParameter<IVariableScratchEnvironment, TYPE, ARGUMENT>
> {

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> AssignBlock<TYPE, ARGUMENT> create(IScratchClass<TYPE, ARGUMENT> scratchClass) {
        return new AssignBlock<>(scratchClass);  
    };

    protected AssignBlock(IScratchClass<TYPE, ARGUMENT> genericClass) {
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
