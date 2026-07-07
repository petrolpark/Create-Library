package petrolpark.mc.library.core.data.numberProvider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;

/**
 * <p>{@code petrolpark:health}</p>
 * 
 * Get the health (or damage) of a LivingEntity, or {@code 0f} if not a LivingEntity.
 * 
 * Arguments:
 * <ul>
 * <li> {@code damage} - Whether to query the damage instead of the health. Defaults to {@code false}
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record HealthEntityNumberProvider(boolean damage) implements EntityNumberProvider {

    public static final MapCodec<HealthEntityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("damage", false).forGetter(HealthEntityNumberProvider::damage)
    ).apply(instance, HealthEntityNumberProvider::new));

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        if (!(entity instanceof LivingEntity living)) return 0f;
        return damage() ? living.getMaxHealth() - living.getHealth() : living.getHealth();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEntityNumberProviderType'");
    };
    
};
