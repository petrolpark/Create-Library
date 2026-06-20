package com.petrolpark.core.item;

import com.petrolpark.core.flags.IFlagPole;

public interface IItemStackDuck {

    public IFlagPole<?, ?> getFlags();
    
    public void onFlagsSaved();
};
