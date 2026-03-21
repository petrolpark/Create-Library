package com.petrolpark.compat.create;

import com.petrolpark.PetrolparkMobEffects;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.core.fluid.openpipeeffect.MobEffectOpenPipeEffectHandler;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.registry.SimpleRegistry;

public class PetrolparkOpenPipeEffectHandlers {
    
    public static final void register() {

        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(PetrolparkTags.Fluids.COOKING_OILS, new MobEffectOpenPipeEffectHandler(PetrolparkMobEffects.SLIPPING.asInstanceSupplier(21, 0))));
    };
};
