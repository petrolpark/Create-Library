package com.petrolpark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkMobEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    
    @Inject(
        method = "bobHurt",
        at = @At(
            value = "INVOKE",
            target = "getLastDamageSource"
        ),
        cancellable = true
    )
    private void inBobHurt(PoseStack ms, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(PetrolparkMobEffects.NUMBNESS.getDelegate())) ci.cancel();
    };
};
