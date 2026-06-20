package com.petrolpark.core.client.effectShaders;

import com.petrolpark.registry.PetrolparkPostUniforms;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class ClientEffectHandler {
    public static void updateUniforms(float value) {
        PetrolparkPostUniforms.set("EffectFactor", uniform -> uniform.set(value));
    }
}
