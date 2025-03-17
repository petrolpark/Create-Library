package com.petrolpark.recipe.ingredient.randomizer;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;

public record FromItemSetIngredientRandomizer(HolderSet<Item> items) implements IngredientRandomizer {

    public static final MapCodec<FromItemSetIngredientRandomizer> CODEC = CodecHelper.singleFieldMap(RegistryCodecs.homogeneousList(Registries.ITEM), "items", FromItemSetIngredientRandomizer::items, FromItemSetIngredientRandomizer::new);

    @Override
    public Ingredient generate(LootContext context) {
        return Ingredient.of(items.get(context.getRandom().nextInt(items().size())).value());
    };

    @Override
    public IngredientRandomizerType getType() {
        return PetrolparkIngredientRandomizerTypes.FROM_ITEM_SET.get();
    };
    
};
