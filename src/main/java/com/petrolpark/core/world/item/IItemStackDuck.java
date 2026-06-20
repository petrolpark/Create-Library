package com.petrolpark.core.world.item;

import com.petrolpark.core.flags.IFlagPole;

public interface IItemStackDuck {

    public IFlagPole<?, ?> getFlags();
    
    public void onFlagsSaved();
};
