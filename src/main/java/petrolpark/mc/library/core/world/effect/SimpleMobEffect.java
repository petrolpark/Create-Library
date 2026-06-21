package petrolpark.mc.library.core.world.effect;

import petrolpark.mc.library.core.registrate.builder.MobEffectBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SimpleMobEffect extends MobEffect {

    public SimpleMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    public SimpleMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    };

    public static final MobEffectBuilder.Factory<SimpleMobEffect> withParticle(ParticleOptions particle) {
        return (category, color) -> new SimpleMobEffect(category, color, particle);
    };
};
