package com.petrolpark.mixin.client;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererMixin {

    @Unique
    IdentityHashMap<Holder<MobEffect>, PostChain> petrolpark$loadedEffects = new IdentityHashMap<>();
    
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        for ( Map.Entry<Holder<MobEffect>, PostChain> entry : petrolpark$loadedEffects.entrySet()) {
            MobEffectInstance instance = player.getEffect(entry.getKey());
            if (instance == null) continue;

            PostChain postChain = entry.getValue();

            ((IMobEffectInstanceMixin)instance).petrolpark$updateUniforms();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();

            postChain.process(deltaTracker.getGameTimeDeltaTicks());
        };
    };

    @Override
    public void petrolpark$addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        PostChain postChain = ShaderEffectReloadHandler.getShader((( IShaderEffect ) effect.getEffect().value()));

        if (postChain == null) {
            Petrolpark.LOGGER.error("Shader wasn't preloaded as intended: {}", location);
            return;
        };

        petrolpark$loadedEffects.put(effect.getEffect(), postChain);
    };

    @Override
    public void petrolpark$removeMobEffectInstanceShader(MobEffectInstance effect) {
        petrolpark$loadedEffects.remove(effect.getEffect());
    };

    @Override
    public void petrolpark$cleanShaderEffects() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        for (Holder<MobEffect> effect : new ArrayList<>(petrolpark$loadedEffects.keySet()) ) {
            MobEffectInstance instance = player.getEffect(effect);
            if (instance == null) continue;

            petrolpark$removeMobEffectInstanceShader(instance);
        };
    };
};
