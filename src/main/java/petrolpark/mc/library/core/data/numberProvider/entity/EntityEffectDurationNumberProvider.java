package petrolpark.mc.library.core.data.numberProvider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:effect_duration}</p>
 * 
 * Get the remaining number of ticks the entity has a MobEffect for, or {@code 0} if they do not have the MobEffect.
 * If the effect is infinite, return {@link Integer#MAX_VALUE}
 * 
 * Arguments:
 * <ul>
 * <li> {@code effect} - MobEffect ID
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record EntityEffectDurationNumberProvider(Holder<MobEffect> effect) implements EntityNumberProvider {

    public static final MapCodec<EntityEffectDurationNumberProvider> CODEC = CodecHelper.singleFieldMap(MobEffect.CODEC, "effect", EntityEffectDurationNumberProvider::effect, EntityEffectDurationNumberProvider::new);

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        return getInt(entity, lootContext);
    };

    @Override
    public int getInt(Entity entity, LootContext lootContext) {
        if (!(entity instanceof LivingEntity living)) return 0;
        final MobEffectInstance instance = living.getEffect(effect());
        if (instance == null) return 0;
        if (instance.isInfiniteDuration()) return Integer.MAX_VALUE;
        return instance.getDuration();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.EFFECT_DURATION.get();
    };
    
};
