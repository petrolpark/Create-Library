package petrolpark.mc.library.core.data.loot.numberprovider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.data.loot.numberprovider.ConditionalNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemPredicateNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * <p>{@code petrolpark:predicate}</p>
 * 
 * Choose between two {@link EntityNumberProvider}s based on whether a {@link EntityPredicate} passes.
 * 
 * Arguments:
 * <ul>
 * <li> {@code predicate} - {@link EntityPredicate} to test
 * <li> {@code pass} - {@link EntityNumberProvider} or {@link NumberProvider} to call if the predicate is met
 * <li> {@code fail} - {@link EntityNumberProvider} or {@link NumberProvider} to call if the predicate is not met
 * </ul>
 * 
 * @author petrolpark
 * 
 * @see ConditionalNumberProvider Generic equivalent
 * @see ItemPredicateNumberProvider Item Stack equivalent
 * 
 */
@ParametersAreNonnullByDefault
public record EntityPredicateNumberProvider(EntityPredicate predicate, EntityNumberProvider pass, EntityNumberProvider fail) implements EntityNumberProvider {

    public static final MapCodec<EntityPredicateNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityPredicate.CODEC.fieldOf("predicate").forGetter(EntityPredicateNumberProvider::predicate),
        EntityNumberProvider.CODEC.fieldOf("pass").forGetter(EntityPredicateNumberProvider::pass),
        EntityNumberProvider.CODEC.fieldOf("fail").forGetter(EntityPredicateNumberProvider::fail)
    ).apply(instance, EntityPredicateNumberProvider::new));

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        return (predicate().matches(lootContext.getLevel(), null, entity) ? pass : fail).getFloat(entity, lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return pass.getEstimate().or(fail.getEstimate());
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.ENTITY_PREDICATE.get();
    };
    
};
