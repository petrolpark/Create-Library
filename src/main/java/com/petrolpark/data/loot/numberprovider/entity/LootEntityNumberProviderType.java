package com.petrolpark.data.loot.numberprovider.entity;

import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class LootEntityNumberProviderType extends SerializerType<EntityNumberProvider> {

    public LootEntityNumberProviderType(Serializer<? extends EntityNumberProvider> serializer) {
        super(serializer);
    };

    public LootEntityNumberProviderType(Supplier<? extends EntityNumberProvider> simpleSupplier) {
        super(new SimpleSerializer<>(simpleSupplier));
    };

    public static final class SimpleSerializer<NP extends EntityNumberProvider> implements Serializer<NP> {

        public final Supplier<NP> factory;

        public SimpleSerializer(Supplier<NP> factory) {
            this.factory = factory;
        };

        @Override
        public void serialize(JsonObject json, NP value, JsonSerializationContext serializationContext) {};

        @Override
        public NP deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return factory.get();
        };
    };
    
};
