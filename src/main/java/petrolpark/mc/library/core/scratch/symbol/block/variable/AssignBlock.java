package petrolpark.mc.library.core.scratch.symbol.block.variable;

import static petrolpark.mc.library.core.scratch.argument.VariableArgument.variable;

import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.argument.VariableArgument;
import petrolpark.mc.library.core.scratch.argument.VariableArgument.VariableParameter;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.IVariableScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.ScratchVariableIdentifier;
import petrolpark.mc.library.core.scratch.symbol.block.BinaryGenericInstantBlock;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlock;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchBlockTypes;

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
