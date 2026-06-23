package petrolpark.mc.library.core.data.numberProvider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:experience_level}</p>
 * 
 * {@link Player#experienceLevel Get the Experience level of a Player}, or {@code 0} if not a player. No arguments. 
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public class ExperienceLevelNumberProvider implements EntityNumberProvider {

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof Player player) return player.experienceLevel;
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ranged(0f, 100f, true);
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.EXPERIENCE_LEVEL.get();
    };
    
};
