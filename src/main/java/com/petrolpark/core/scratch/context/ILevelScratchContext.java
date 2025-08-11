package com.petrolpark.core.scratch.context;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.level.Level;

public interface ILevelScratchContext extends IRegistryAccessScratchContext {
    
    public Level level();

    @ApiStatus.NonExtendable
    @Override
    default Provider registryAccess() {
        return level().registryAccess();
    };
};
