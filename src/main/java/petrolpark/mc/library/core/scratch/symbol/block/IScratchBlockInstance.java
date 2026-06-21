package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public interface IScratchBlockInstance<ENVIRONMENT extends IScratchEnvironment> {

    public boolean tick(ENVIRONMENT environment);
};
