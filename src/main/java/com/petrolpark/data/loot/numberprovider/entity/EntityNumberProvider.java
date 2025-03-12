package com.petrolpark.data.loot.numberprovider.entity;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface EntityNumberProvider extends LootContextUser {

    public static final Codec<EntityNumberProvider> CODEC = Codec.lazyInitialized(
        () -> TypedCodec.TYPED_CODEC //TODO add default value and inline serializer
    );

    public float getFloat(Entity entity, LootContext lootContext);

    public LootEntityNumberProviderType getType();

    public static class TypedCodec {

        private static final Codec<EntityNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ENTITY_NUMBER_PROVIDER_TYPES
            .byNameCodec()
            .dispatch(EntityNumberProvider::getType, LootEntityNumberProviderType::codec);
    };
};
