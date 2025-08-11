package com.petrolpark.core.scratch.context;

import net.minecraft.core.HolderLookup;

public interface IRegistryAccessScratchContext extends IScratchContext {
    
    public HolderLookup.Provider registryAccess();
};
