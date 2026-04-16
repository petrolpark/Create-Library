package com.petrolpark.core.scratch.procedure;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public final class RootScratchContext<ENVIRONMENT extends IScratchEnvironment> implements IScratchContext<RootScratchContext<ENVIRONMENT>> {

    public final IScratchEnvironment.Type<ENVIRONMENT> environmentType;

    public RootScratchContext(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        this.environmentType = environmentType;
    };

    public final class Provider implements IScratchContextProvider<RootScratchContext<ENVIRONMENT>> {

        @Override
        public IScratchEnvironment.Type<ENVIRONMENT> environmentType() {
            return environmentType;
        };

        @Override
        public IScratchContextProvider<?> enclosingContextProvider() {
            return this;
        };

        @Override
        public boolean isRoot() {
            return true;
        };

    };
};
