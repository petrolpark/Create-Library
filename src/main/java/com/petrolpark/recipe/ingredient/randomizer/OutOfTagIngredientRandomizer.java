package com.petrolpark.recipe.ingredient.randomizer;

import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;

public class OutOfTagIngredientRandomizer implements IngredientRandomizer {

    public final TagKey<Item> tag;

    public OutOfTagIngredientRandomizer(TagKey<Item> tag) {
        this.tag = tag;
    };

    @Override
    @SuppressWarnings("deprecation")
    public Ingredient generate(LootContext context) {
        Optional<Item> item = BuiltInRegistries.ITEM.getTag(tag).map(set -> set.getRandomElement(context.getRandom())).orElse(Optional.empty()).map(Holder::get);
        if (item.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(item.get());
    };

    @Override
    public IngredientRandomizerType getType() {
        return IngredientRandomizerTypes.OUT_OF_TAG.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<OutOfTagIngredientRandomizer> {

        @Override
        public void serialize(JsonObject json, OutOfTagIngredientRandomizer value, JsonSerializationContext serializationContext) {
            json.addProperty("tag", value.tag.location().toString());
        };

        @Override
        public OutOfTagIngredientRandomizer deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new OutOfTagIngredientRandomizer(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "tag"))));
        };

    };
    
};
