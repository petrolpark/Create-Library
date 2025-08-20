package com.petrolpark.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.PetrolparkMobEffects;
import com.petrolpark.shadereffect.IShaderEffect;
import com.petrolpark.shadereffect.ShaderEffectReloadHandler;
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
import java.util.HashMap;
import java.util.Map;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererMixin {

    @Unique
    HashMap<IMobEffectInstanceMixin, PostChain> loadedEffects = new HashMap<>();
    
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
        for ( Map.Entry<IMobEffectInstanceMixin, PostChain> entry : loadedEffects.entrySet()) {
            IMobEffectInstanceMixin effect = entry.getKey();
            PostChain postChain = entry.getValue();

            effect.updateUniforms();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();

            postChain.process(deltaTracker.getGameTimeDeltaTicks());
        }
    }

    @Override
    public void addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        PostChain postChain = ShaderEffectReloadHandler.getShader((( IShaderEffect ) effect.getEffect().value()));

        if (postChain == null) {
            System.err.println("[Petrolpark] Shader wasn't preloaded as intended: " + location);
            return;
        }

        loadedEffects.put((( IMobEffectInstanceMixin ) effect), postChain);
    }

    @Override
    public void removeMobEffectInstanceShader(IMobEffectInstanceMixin effect) {
        loadedEffects.remove(effect);
    }

    @Override
    public void cleanShaderEffects() {
        for (IMobEffectInstanceMixin effect : new ArrayList<>(loadedEffects.keySet()) ) {
            removeMobEffectInstanceShader(effect);
        }
    }
};
