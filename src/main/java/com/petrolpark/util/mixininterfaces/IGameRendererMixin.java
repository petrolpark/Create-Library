package com.petrolpark.util.mixininterfaces;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public interface IGameRendererMixin {
    void petrolpark$addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance mobEffect);
    void petrolpark$removeMobEffectInstanceShader(IMobEffectInstanceMixin effect);
    void petrolpark$cleanShaderEffects();
}
