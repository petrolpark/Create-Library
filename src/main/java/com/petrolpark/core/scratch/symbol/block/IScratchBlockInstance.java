package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IScratchBlockInstance<ENVIRONMENT extends IScratchEnvironment> {

    public boolean tick(ENVIRONMENT environment);
};
