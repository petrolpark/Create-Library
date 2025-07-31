package com.petrolpark.mixin.interfaces;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public interface IGameRendererMixin {
    void addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance mobEffect);
    void removeMobEffectInstanceShader(IMobEffectInstanceMixin effect);
    void cleanShaderEffects();
}
