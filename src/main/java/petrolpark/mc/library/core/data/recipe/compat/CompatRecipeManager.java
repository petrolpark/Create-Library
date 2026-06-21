package petrolpark.mc.library.core.data.recipe.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import petrolpark.mc.library.Petrolpark;

import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.conditions.ICondition;

public class CompatRecipeManager {

    protected static final String SERIALIZER_KEY = "type";
    
    protected final Map<ResourceLocation, List<CompatRecipeDeserializer<?>>> compatRecipeDeserializers = new HashMap<>();
    protected boolean registrationDone = false;

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
     * Register a new {@link CompatRecipeDeserializer}.
     * @param <D> Type of the {@link CompatRecipeDeserializer}
     * @param deserializer
     * @see Petrolpark#COMPAT_RECIPES The instance on which to call this method
     */
    public final <D extends CompatRecipeDeserializer<?>> D register(D deserializer) {
        if (registrationDone) throw new IllegalStateException("New Compat Recipe Deserializers must be registered during initiation");
        final List<CompatRecipeDeserializer<?>> list;
        if (compatRecipeDeserializers.get(deserializer.serializerId()) == null) {
            list = new ArrayList<>();
            compatRecipeDeserializers.put(deserializer.serializerId(), list);
        } else list = compatRecipeDeserializers.get(deserializer.serializerId());
        list.add(deserializer);
        return deserializer;
    };

    /**
     * 
     * @param byTypeRecipeMapBuilder
     * @param byNameRecipeMapBuilder
     * @param jsonElement The associated Recipe should have already passed its {@link ICondition}s
     */
    public void addCompatRecipes(ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byTypeRecipeMapBuilder, ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byNameRecipeMapBuilder, RegistryOps<JsonElement> registryOps, ResourceLocation recipeId, JsonElement jsonElement) {
        deserializersDecoder.parse(registryOps, jsonElement).resultOrPartial(JsonParseException::new).stream().flatMap(List::stream)
            .forEach(deserializer -> {
                if (!deserializer.shouldDeserialize(jsonElement, recipeId)) return;
                final var result = deserializer.decoder().parse(registryOps, jsonElement);
                result.result().flatMap(Function.identity()).ifPresent(recipe -> {
                    final ResourceLocation id = deserializer.createId(recipeId);
                    final RecipeHolder<?> recipeHolder = new RecipeHolder<Recipe<?>>(id, recipe);
                    byTypeRecipeMapBuilder.put(recipe.getType(), recipeHolder);
                    byNameRecipeMapBuilder.put(id, recipeHolder);
                });
                result.ifError(error -> Petrolpark.LOGGER.warn("Skipping loading Compat Recipe {} for recipe \"{}\": {}", deserializer.toString(), recipeId, error.message()));
            });
    };
};
