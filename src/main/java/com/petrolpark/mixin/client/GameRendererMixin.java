package com.petrolpark.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
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

import java.io.IOException;
import java.util.IdentityHashMap;

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
        for ( IMobEffectInstanceMixin effect : loadedEffects.keySet()) {
            PostChain postChain = loadedEffects.get(effect);
            effect.updateUniforms();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            postChain.process(pPartialTicks);
        }
    }

    @Override
    public void addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        try {
            PostChain postChain = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), location);
            postChain.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            loadedEffects.put((( IMobEffectInstanceMixin ) effect), postChain);
        } catch ( IOException e ) {
            System.out.println("Shader ["+ location +"] failed to load O-O");
            e.printStackTrace();
        }
    }

    @Override
    public void removeMobEffectInstanceShader(IMobEffectInstanceMixin effect) {
        PostChain postChain = loadedEffects.remove(effect);
        postChain.close();
    }

    @Override
    public void cleanShaderEffects() {
        for (IMobEffectInstanceMixin mei : loadedEffects.keySet()) {
            removeMobEffectInstanceShader(mei);
        }
    }
}
