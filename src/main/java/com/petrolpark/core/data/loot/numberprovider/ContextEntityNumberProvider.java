package com.petrolpark.core.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.data.IEntityTarget;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.registry.PetrolparkNumberProviderTypes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

/**
 * <p>{@code petrolpark:entity_property}</p>
 * 
 * Get a {@link EntityNumberProvider} value of an Entity provided in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code target} - An {@link IEntityTarget} specifying which Entity in the {@link LootContext}
 * <li> {@code value} - An {@link EntityNumberProvider} to call on that Entity 
 * </ul>
 * 
 * @author petrolpark
 */
public record ContextEntityNumberProvider(IEntityTarget target, EntityNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextEntityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityTarget.CODEC.fieldOf("target").forGetter(ContextEntityNumberProvider::target),
        EntityNumberProvider.CODEC.fieldOf("value").forGetter(ContextEntityNumberProvider::value)
    ).apply(instance, ContextEntityNumberProvider::new));

    @Override
    public float getFloat(@Nonnull LootContext context) {
        Entity entity = target.get(context);
        if (entity != null) return value.getFloat(entity, context);
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        Entity entity = target.get(context);
        if (entity != null) return value.getMaxFloat(entity, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_ENTITY.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(value.getReferencedContextParams(), Set.of(target.getReferencedParam()));
    };
    
};
