package com.petrolpark.core.data.loot.numberprovider.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record EntityPredicateNumberProvider(EntityPredicate predicate, Either<NumberProvider, EntityNumberProvider> pass, Either<NumberProvider, EntityNumberProvider> fail) implements EntityNumberProvider {

    public static final MapCodec<EntityPredicateNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityPredicate.CODEC.fieldOf("predicate").forGetter(EntityPredicateNumberProvider::predicate),
        Codec.either(NumberProviders.CODEC, EntityNumberProvider.CODEC).fieldOf("pass").forGetter(EntityPredicateNumberProvider::pass),
        Codec.either(NumberProviders.CODEC, EntityNumberProvider.CODEC).fieldOf("fail").forGetter(EntityPredicateNumberProvider::fail)
    ).apply(instance, EntityPredicateNumberProvider::new));

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        return (predicate().matches(lootContext.getLevel(), null, entity) ? pass : fail).map(
            np -> np.getFloat(lootContext),
            enp -> enp.getFloat(entity, lootContext)
        );
    };

    @Override
    public NumberEstimate getEstimate() {
        return pass.map(NumberEstimate::get, EntityNumberProvider::getEstimate).or(fail.map(NumberEstimate::get, EntityNumberProvider::getEstimate));
    };

    @Override
    public LootEntityNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.ENTITY_PREDICATE.get();
    };
    
};
