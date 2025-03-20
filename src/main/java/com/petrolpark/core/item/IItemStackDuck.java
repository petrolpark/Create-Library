package com.petrolpark.core.item;

import com.petrolpark.core.contamination.IContamination;

public interface IItemStackDuck {

    public IContamination<?, ?> getContamination();
    
    public void onContaminationSaved();
};
