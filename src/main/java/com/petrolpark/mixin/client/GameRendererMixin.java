package com.petrolpark.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkMobEffects;
import com.petrolpark.common.mobeffect.shader.IShaderEffect;
import com.petrolpark.common.mobeffect.shader.ShaderEffectReloadHandler;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererMixin {

    @Unique
    IdentityHashMap<IMobEffectInstanceMixin, PostChain> petrolpark$loadedEffects = new IdentityHashMap<>();
    
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

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"
            )
    )
    public void inRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        for ( Map.Entry<IMobEffectInstanceMixin, PostChain> entry : petrolpark$loadedEffects.entrySet()) {
            IMobEffectInstanceMixin effect = entry.getKey();
            PostChain postChain = entry.getValue();

            effect.petrolpark$updateUniforms();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();

            postChain.process(deltaTracker.getGameTimeDeltaTicks());
        }
    }

    @Override
    public void petrolpark$addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        PostChain postChain = ShaderEffectReloadHandler.getShader((( IShaderEffect ) effect.getEffect().value()));

        if (postChain == null) {
            Petrolpark.LOGGER.error("Shader wasn't preloaded as intended: {}", location);
            return;
        }

        petrolpark$loadedEffects.put((( IMobEffectInstanceMixin ) effect), postChain);
    }

    @Override
    public void petrolpark$removeMobEffectInstanceShader(IMobEffectInstanceMixin effect) {
        petrolpark$loadedEffects.remove(effect);
    }

    @Override
    public void petrolpark$cleanShaderEffects() {
        for (IMobEffectInstanceMixin effect : new ArrayList<>(petrolpark$loadedEffects.keySet()) ) {
            petrolpark$removeMobEffectInstanceShader(effect);
        }
    }
};
