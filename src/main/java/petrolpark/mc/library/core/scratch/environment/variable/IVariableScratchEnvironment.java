package petrolpark.mc.library.core.scratch.environment.variable;

import java.util.stream.Stream;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public interface IVariableScratchEnvironment extends IScratchEnvironment {
    
    public IScratchVariables getVariables(IScratchScope scope);

    @Override
    public IVariableScratchEnvironment.Type<?> getType();

    public static interface Type<ENVIRONMENT extends IVariableScratchEnvironment> extends IScratchEnvironment.Type<ENVIRONMENT> {

        public Stream<IScratchScope> streamAccessibleScopes();

        public boolean canAccessScope(IScratchScope scope);
    };
};
