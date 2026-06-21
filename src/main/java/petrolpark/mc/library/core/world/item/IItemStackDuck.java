package petrolpark.mc.library.core.world.item;

import petrolpark.mc.library.core.flags.IFlagPole;

public interface IItemStackDuck {

    public IFlagPole<?, ?> getFlags();
    
    public void onFlagsSaved();
};
