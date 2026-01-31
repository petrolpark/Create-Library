package com.petrolpark.core.scratch.symbol.block.variable;

import static com.petrolpark.core.scratch.argument.VariableArgument.variable;

import com.petrolpark.PetrolparkScratchBlockTypes;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.block.GenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;

public class AssignBlock<TYPE> extends GenericInstantBlock<
    IVariableScratchEnvironment,
    TYPE,
    ScratchArguments.And<
        IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.Just<
        IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>
    >>
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
    public void run(IVariableScratchEnvironment environment, ScratchArguments.And<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.Just<IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>>> arguments) {
        environment.getVariables(arguments.get(environment).scope()).set(getGenericScratchClass(), arguments.get(environment).name(), arguments.next().get(environment));
    };

    @Override
    public IScratchBlock.Type<?> getBlockType() {
        return PetrolparkScratchBlockTypes.ASSIGN.get();
    };

};
