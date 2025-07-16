package com.petrolpark.mixin;

import com.petrolpark.shadereffects.IShaderEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MobEffect.class )
public abstract class MobEffectMixin implements IShaderEffect{
    @Inject(
            method = "removeAttributeModifiers",
            at = @At("HEAD")
    )
    public void inRemoveAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier, CallbackInfo ci) {
        IShaderEffect.super.cleanupShader(pLivingEntity, (( MobEffect ) (Object) this));
    }
}
