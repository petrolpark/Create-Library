package petrolpark.mc.library.shared.world.effect;

import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.world.effect.SimpleMobEffect;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedDamageSources;
import petrolpark.mc.library.shared.registry.SharedMobEffects;

@EventBusSubscriber
@ParametersAreNonnullByDefault
public class InebriationMobEffect extends SimpleMobEffect implements ISharedFeature {

    public InebriationMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final MobEffectInstance instance = livingEntity.getEffect(SharedMobEffects.INEBRIATION);
        if (instance == null) return true;

        if (livingEntity.hasEffect(SharedMobEffects.HANGOVER)) livingEntity.removeEffect(SharedMobEffects.HANGOVER);

        final int duration = instance.getDuration();
        if (!livingEntity.level().isClientSide()) {
            if (amplifier >= PetrolparkConfigs.server().inebriationNauseaThreshold.get()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, (amplifier - PetrolparkConfigs.server().inebriationNauseaThreshold.get()), true, true, true));
            };
            if (amplifier >= PetrolparkConfigs.server().inebriationBlindnessThreshold.get()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, (amplifier - PetrolparkConfigs.server().inebriationBlindnessThreshold.get()), true, true, true));
            };
            if (amplifier >= PetrolparkConfigs.server().inebriationDamageThreshold.get()) {
                livingEntity.hurt(SharedDamageSources.alcoholPoisoning(livingEntity.level()), 1f);
            };
        };

        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        if (amplifier >= PetrolparkConfigs.server().inebriationDamageThreshold.get()) {
            amplifier -= PetrolparkConfigs.server().inebriationDamageThreshold.get();
        } else {
            return duration % 20 == 0;
        };
        final int i = 25 >> amplifier;
        return i > 0 ? duration % i == 0 : true;
    };

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.INEBRIATION;
    };

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (!SharedFeatureFlag.INEBRIATION.enabled()) return;
        for (Player player : event.getLevel().players()) {
            if (!player.isSleeping()) continue;

            // Sleeping with a Hangover cures it
            player.removeEffect(SharedMobEffects.HANGOVER);

            // Sleeping with Inebriation gives a hangover
            final MobEffectInstance instance = player.getEffect(SharedMobEffects.INEBRIATION);
            if (instance != null) {
                player.addEffect(new MobEffectInstance(SharedMobEffects.HANGOVER, PetrolparkConfigs.server().hangoverDuration.get(), instance.getAmplifier()));
                player.removeEffect(SharedMobEffects.INEBRIATION);
            };
        };
    };
    
};
