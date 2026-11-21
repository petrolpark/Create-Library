package petrolpark.mc.library.util.mixininterfaces;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.IdentityHashMap;

public interface IGameRendererMixin {
    void petrolpark$addMobEffectInstanceShader(ResourceLocation location, MobEffectInstance mobEffect);
    void petrolpark$removeMobEffectInstanceShader(MobEffectInstance effect);
    void petrolpark$cleanShaderEffects();
    IdentityHashMap<Holder<MobEffect>, PostChain> petrolpark$getLoadedEffects();
}
