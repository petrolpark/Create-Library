package petrolpark.mc.library.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.effectShaders.IShaderEffect;
import petrolpark.mc.library.core.client.effectShaders.ShaderEffectReloadHandler;
import petrolpark.mc.library.core.client.effectShaders.SharedTargetsHandler;
import petrolpark.mc.library.shared.registry.SharedMobEffects;
import petrolpark.mc.library.util.mixininterfaces.IGameRendererMixin;
import petrolpark.mc.library.util.mixininterfaces.IMobEffectInstanceMixin;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

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
    private void petrolpark$numbnessCancelsBobbing(PoseStack ms, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(SharedMobEffects.NUMBNESS.getDelegate())) ci.cancel();
    };

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"
        )
    )
    public void petrolpark$updateUniforms(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        if (! ShaderEffectReloadHandler.isReady() ) return;
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

    @Inject(
            method = "resize",
            at = @At("TAIL")
    )
    public void petrolpark$resizeLoadedEffects(int width, int height, CallbackInfo ci) {
        petrolpark$loadedEffects.values().forEach((postChain) -> postChain.resize(width, height) );
    }

    @Override
    public void petrolpark$addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance effect) {
        IShaderEffect shaderEffect = (( IShaderEffect ) effect.getEffect().value());
        PostChain postChain = ShaderEffectReloadHandler.getShader(shaderEffect);

        if (postChain == null) {
            Petrolpark.LOGGER.error("Shader wasn't preloaded as intended: {}", location);
            return;
        };

        petrolpark$loadedEffects.put(effect.getEffect(), postChain);
        SharedTargetsHandler.incrementLoadedTargetsCounter(shaderEffect.getShader().toString());
    };

    @Override
    public void petrolpark$removeMobEffectInstanceShader(MobEffectInstance effect) {
        petrolpark$loadedEffects.remove(effect.getEffect());
        SharedTargetsHandler.decrementLoadedTargetsCounter((( IShaderEffect ) effect.getEffect().value()).getShader().toString());
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

    @Override
    public IdentityHashMap<Holder<MobEffect>, PostChain> petrolpark$getLoadedEffects() {
        return petrolpark$loadedEffects;
    }
};
