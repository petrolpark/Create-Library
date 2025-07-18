package com.petrolpark.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.petrolpark.shadereffects.IShaderEffect;
import com.petrolpark.shadereffects.ShaderEffectReloadHandler;
import com.petrolpark.util.IGameRendererMixin;
import com.petrolpark.util.IMobEffectInstanceMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin( GameRenderer.class )
public class GameRendererMixin implements IGameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    @Final
    ResourceManager resourceManager;

    @Unique
    IdentityHashMap<IMobEffectInstanceMixin, PostChain> loadedEffects = new IdentityHashMap<>();

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"
            )
    )
    public void inRender(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci) {
        for ( Map.Entry<IMobEffectInstanceMixin, PostChain> entry : loadedEffects.entrySet()) {
            IMobEffectInstanceMixin effect = entry.getKey();
            PostChain postChain = entry.getValue();

            effect.updateUniforms();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();

            postChain.process(pPartialTicks);
        }
    }

    @Override
    public void addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        PostChain postChain = ShaderEffectReloadHandler.getShader((( IShaderEffect ) effect.getEffect()));

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
}
