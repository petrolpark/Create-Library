package com.petrolpark.common.mobeffect.shader;

import com.petrolpark.PetrolparkPostUniforms;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class ClientEffectHandler {
    public static void updateUniforms(float value) {
        PetrolparkPostUniforms.EFFECT_FACTOR.update(uniform -> uniform.set(value));
    }
}
