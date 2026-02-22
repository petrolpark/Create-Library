package com.petrolpark.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.PetrolparkPostUniforms;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;

@Mixin( PostPass.class)
public abstract class PostPassMixin {
    
    @Shadow
    @Final
    private EffectInstance effect;

    @Inject(method = "process", at = @At(value = "HEAD"))
    public void petrolpark$applyUniforms(float partialTicks, CallbackInfo ci) {
        if (effect == null) return;
        PetrolparkPostUniforms.apply(effect);
    };
};
