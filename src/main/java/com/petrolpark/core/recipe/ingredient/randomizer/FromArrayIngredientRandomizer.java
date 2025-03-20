package com.petrolpark.core.recipe.ingredient.randomizer;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;

public record FromArrayIngredientRandomizer(List<Ingredient> ingredients) implements IngredientRandomizer {

    public static final MapCodec<FromArrayIngredientRandomizer> CODEC = CodecHelper.singleFieldMap(CodecHelper.listOrSingle(Ingredient.CODEC_NONEMPTY), "ingredients", FromArrayIngredientRandomizer::ingredients, FromArrayIngredientRandomizer::new);

    @Override
    public Ingredient generate(LootContext context) {
        return ingredients.get(context.getRandom().nextInt(ingredients.size()));
    };

    @Override
    public IngredientRandomizerType getType() {
        return PetrolparkIngredientRandomizerTypes.FROM_ARRAY.get();
    };
    
};
