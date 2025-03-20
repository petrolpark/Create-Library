package com.petrolpark.core.recipe.ingredient.randomizer;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface IngredientRandomizer extends LootContextUser {

    /**
     * Use {@link IngredientRandomizer#CODEC} instead.
     */
    static final Codec<IngredientRandomizer> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_RANDOMIZER_TYPE
        .byNameCodec()
        .dispatch(IngredientRandomizer::getType, IngredientRandomizerType::codec);

    public static final Codec<IngredientRandomizer> CODEC = null;
    
    public Ingredient generate(LootContext context);

    public IngredientRandomizerType getType();
};
