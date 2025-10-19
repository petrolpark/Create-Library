package com.petrolpark.core.recipe.compat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.conditions.ICondition;

public class CompatRecipeManager {

    protected static final String SERIALIZER_KEY = "type";
    
    protected final Map<ResourceLocation, List<CompatRecipeDeserializer<?>>> compatRecipeDeserializers = new HashMap<>();

    protected final MapDecoder<List<CompatRecipeDeserializer<?>>> deserializersMapDecoder = new MapDecoder.Implementation<>() {

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(SERIALIZER_KEY).map(ops::createString);
        };

        @Override
        public <T> DataResult<List<CompatRecipeDeserializer<?>>> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (ops.compressMaps()) return DataResult.error(() -> "Cannot use CompatRecipeManager decoder with compressing DynamicOps");
            final T serializerIdElement = input.get(SERIALIZER_KEY);
            if (serializerIdElement == null) return DataResult.success(Collections.emptyList()); // Quiet fail
            return ResourceLocation.CODEC.parse(ops, serializerIdElement).map(rl -> {
                final List<CompatRecipeDeserializer<?>> deserializers = compatRecipeDeserializers.get(rl);
                return deserializers == null ? Collections.emptyList() : deserializers;
            });
        };

        
    };

    protected final Decoder<List<CompatRecipeDeserializer<?>>> deserializersDecoder = deserializersMapDecoder.decoder();

    /**
     * 
     * @param byTypeRecipeMapBuilder
     * @param byNameRecipeMapBuilder
     * @param jsonElement The associated Recipe should have already passed its {@link ICondition}s
     */
    public void addCompatRecipes(ImmutableMultimap.Builder<RecipeType<?>, Recipe<?>> byTypeRecipeMapBuilder, ImmutableMap.Builder<ResourceLocation, Recipe<?>> byNameRecipeMapBuilder, ResourceLocation recipeId, JsonElement jsonElement) {
        deserializersDecoder.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(JsonParseException::new).stream().flatMap(List::stream)
            .forEach(deserializer -> deserializer.decoder().parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new)
                .ifPresent(recipe -> {
                    byTypeRecipeMapBuilder.put(recipe.getType(), recipe);
                    byNameRecipeMapBuilder.put(deserializer.createId(recipeId), recipe);
                })
            );
    };
};
