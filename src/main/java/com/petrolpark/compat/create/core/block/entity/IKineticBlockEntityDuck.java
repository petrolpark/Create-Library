package com.petrolpark.compat.create.core.block.entity;

import javax.annotation.Nullable;

public interface IKineticBlockEntityDuck {

    @Nullable
    public Integer getSourceIndex();
    
    public void setSourceIndex(@Nullable Integer sourceIndex);
};
