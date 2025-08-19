package com.petrolpark.compat.create.core.dough;

public final class SimpleDough implements IDough<SimpleDough> {

    public final SimpleDoughType type;

    public SimpleDough(SimpleDoughType type) {
        this.type = type;
    };

    @Override
    public boolean canBeCut() {
        return getType().cuttable;
    };

    @Override
    public SimpleDoughType getType() {
        return type;
    };
    
};
