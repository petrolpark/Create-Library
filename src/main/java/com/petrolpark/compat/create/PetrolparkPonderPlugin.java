package com.petrolpark.compat.create;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return Petrolpark.MOD_ID;
    };

    @Override
    public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreatePonderScenes.register(helper);
    };
    
};
