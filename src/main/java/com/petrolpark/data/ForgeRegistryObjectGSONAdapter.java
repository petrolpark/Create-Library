package com.petrolpark.data;

import java.lang.reflect.Type;
import java.util.function.Function;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory.InlineSerializer;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Copy of {@link GsonAdapterFactory} for custom Registries.
 */
public class ForgeRegistryObjectGSONAdapter<E, T extends SerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {

    public static <E, T extends SerializerType<E>> Builder<E, T> builder(ResourceKey<Registry<T>> registry, String elementName, String typeKey, Function<E, T> typeGetter) {
        return new Builder<>(registry, elementName, typeKey, typeGetter);
    };

    private final ResourceKey<Registry<T>> registryKey;
    private final String elementName;
    private final String typeKey;
    private final Function<E, T> typeGetter;
    @Nullable
    private final Supplier<T> defaultType;
    @Nullable
    private final Pair<Supplier<T>, GsonAdapterFactory.InlineSerializer<? extends E>> inlineType;

    protected ForgeRegistryObjectGSONAdapter(ResourceKey<Registry<T>> registryKey, String elementName, String typeKey, Function<E, T> typeGetter, Supplier<T> defaultType, Pair<Supplier<T>, InlineSerializer<? extends E>> inlineType) {
        this.registryKey = registryKey;
        this.elementName = elementName;
        this.typeKey = typeKey;
        this.typeGetter = typeGetter;
        this.defaultType = defaultType;
        this.inlineType = inlineType;
    };

    @Override
    @SuppressWarnings("unchecked")
    public JsonElement serialize(E src, Type typeOfSrc, JsonSerializationContext context) {
        T t = typeGetter.apply(src);
        if (inlineType != null && inlineType.getFirst().get() == t) {
            return ((InlineSerializer<E>)inlineType.getSecond()).serialize(src, context);
        } else if (t == null) {
           throw new JsonSyntaxException("Unknown type: " + src);
        } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(typeKey, getRegistry().getKey(t).toString());
            ((Serializer<E>)t.getSerializer()).serialize(jsonobject, src, context);
            return jsonobject;
        }
    };

    @Override
    public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(json, this.elementName);
            String s = GsonHelper.getAsString(jsonobject, this.typeKey, "");
            T t;
            if (s.isEmpty()) {
               t = defaultType.get();
            } else {
               ResourceLocation resourcelocation = ResourceLocation.fromNamespaceAndPath(s);
               t = getRegistry().getValue(resourcelocation);
            }

            if (t == null) {
               throw new JsonSyntaxException("Unknown type '" + s + "'");
            } else {
               return t.getSerializer().deserialize(jsonobject, context);
            }
        } else if (inlineType == null) {
            throw new UnsupportedOperationException("Object " + json + " can't be deserialized");
        } else {
            return (inlineType.getSecond().deserialize(json, context));
        }
    };

    private IForgeRegistry<T> getRegistry() {
        return PetrolparkRegistries.getRegistry(registryKey);
    };

    public static class Builder<E, T extends SerializerType<E>> {
        private final ResourceKey<Registry<T>> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<Supplier<T>, GsonAdapterFactory.InlineSerializer<? extends E>> inlineType;
        @Nullable
        private Supplier<T> defaultType;
  
        Builder(ResourceKey<Registry<T>> registry, String elementName, String typeKey, Function<E, T> typeGetter) {
           this.registry = registry;
           this.elementName = elementName;
           this.typeKey = typeKey;
           this.typeGetter = typeGetter;
        };
  
        public Builder<E, T> withInlineSerializer(Supplier<T> inlineType, GsonAdapterFactory.InlineSerializer<? extends E> pInlineSerializer) {
            this.inlineType = Pair.of(inlineType, pInlineSerializer);
            return this;
        };
  
        public Builder<E, T> withDefaultType(Supplier<T> defaultType) {
            this.defaultType = defaultType;
            return this;
        };

        public ForgeRegistryObjectGSONAdapter<E, T> build() {
            return new ForgeRegistryObjectGSONAdapter<>(registry, elementName, typeKey, typeGetter, defaultType, inlineType);
        };
    }
    
};
