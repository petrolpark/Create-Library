package com.petrolpark.core.scratch.symbol.block;

import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;

public interface IScratchBlockInstance<ENVIRONMENT extends IScratchEnvironment> {

    public boolean run(ENVIRONMENT environment);

    public static record ScratchBlockInstanceCodecContext<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>>(IScratchContext<?> contextHolder, ARGUMENTS arguments) {};
};
