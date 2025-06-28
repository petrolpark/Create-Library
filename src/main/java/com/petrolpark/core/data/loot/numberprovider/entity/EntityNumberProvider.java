package com.petrolpark.core.data.loot.numberprovider.entity;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface EntityNumberProvider extends LootContextUser {

    /**
     * Use {@link EntityNumberProvider#CODEC} instead.
     */
    static final Codec<EntityNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ENTITY_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(EntityNumberProvider::getType, LootEntityNumberProviderType::codec);

    public static final Codec<EntityNumberProvider> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC); //TODO add default/inline

    public float getFloat(Entity entity, LootContext lootContext);

    public NumberEstimate getEstimate();

    public default float getMaxFloat(Entity entity, LootContext context) {
        return getFloat(entity, context);
    };

    public LootEntityNumberProviderType getType();
};
