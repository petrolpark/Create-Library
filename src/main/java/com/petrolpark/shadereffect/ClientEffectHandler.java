package com.petrolpark.shadereffect;

import com.petrolpark.PetrolparkPostUniforms;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class ClientEffectHandler {
    public static void initShaderEffect(MobEffectInstance mobEffectInstance, IShaderEffect shaderEffect) {
        IGameRendererMixin gameRenderer = ( IGameRendererMixin ) Minecraft.getInstance().gameRenderer;
        gameRenderer.addMobEffectInstanceShader(shaderEffect.getShader(), mobEffectInstance);
    }

    public static void updateUniforms(float value) {
        PetrolparkPostUniforms.EFFECT_FACTOR.update(uniform -> uniform.set(value));
    }
}
