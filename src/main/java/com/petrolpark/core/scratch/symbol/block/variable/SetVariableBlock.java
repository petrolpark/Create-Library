package com.petrolpark.core.scratch.symbol.block.variable;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.VariableArgument;
import com.petrolpark.core.scratch.environment.variable.IVariableScratchEnvironment;
import com.petrolpark.core.scratch.environment.variable.ScratchVariableIdentifier;
import com.petrolpark.core.scratch.symbol.block.GenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;

public class SetVariableBlock<TYPE> extends GenericInstantBlock<
    IVariableScratchEnvironment,
    TYPE,
    ScratchArguments.And<
        IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.Just<
        IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>
    >>
> {

    public static final GenericInstantBlock.Type<SetVariableBlock<?>> TYPE = new GenericInstantBlock.Type<>(SetVariableBlock::create);

    protected static final <TYPE> SetVariableBlock<TYPE> create(IScratchClass<TYPE> scratchClass) {
        return new SetVariableBlock<>(scratchClass);  
    };

    protected SetVariableBlock(IScratchClass<TYPE> genericClass) {
        super(genericClass, ScratchParameters.<IVariableScratchEnvironment>parameters()
            .after(genericClass.createDefaultParameter("value"))
            .after(VariableArgument.variable("identifier"))
            .build()
        );
    };

    @Override
    public void run(IVariableScratchEnvironment environment, ScratchArguments.And<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument, ScratchArguments.Just<IVariableScratchEnvironment, TYPE, IScratchArgument<IVariableScratchEnvironment, TYPE>>> arguments) {
        environment.getVariables(arguments.get(environment).scope()).set(getGenericScratchClass(), arguments.get(environment).name(), arguments.next().get(environment));
    };

    @Override
    public IScratchBlock.Type<?> getBlockType() {
        return TYPE;
    };

};
