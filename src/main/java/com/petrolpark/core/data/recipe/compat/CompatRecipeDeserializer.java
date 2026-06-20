package com.petrolpark.core.data.recipe.compat;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.brewinandchewin.BnCFermentingRecipeDeserializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Convert Recipes added by other mods to Recipes in your own mod by emulating their Codecs and reading the JSON files as they are loaded.
 * These should be {@link CompatRecipeManager#register(CompatRecipeDeserializer) registered} to {@link Petrolpark#COMPAT_RECIPES} during mod intialization.
 * @see BnCFermentingRecipeDeserializer Example
 */
public interface CompatRecipeDeserializer<R extends Recipe<?>> {

    /**
     * The ID of the other mod's {@link Recipe#getSerializer() Recipe Serializer} that this {@link CompatRecipeDeserializer} should emulate.
     */
    public ResourceLocation serializerId();
    
    /**
     * Decoder that should emulate the Codec used by the other mod's {@link Recipe#getSerializer() Recipe Serializer}.
     * This Decoder should return a {@link DataResult#error} if there is a formatting issue with the Recipe,
     * but a {@link DataResult#success} containing an empty Optional if there is nothing unexpected with the JSON but it cannot be converted
     * (for example, if the other mod's Recipe may optionally include Fluids but your Recipe cannot).
     */
    public Decoder<Optional<R>> decoder();

    public default boolean shouldDeserialize(JsonElement element, ResourceLocation id) {
        return true;
    };

    public ResourceLocation createId(ResourceLocation baseId);
};
