package com.petrolpark.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface IngredientModifier extends LootContextUser {

    /**
     * Use {@link IngredientModifier#CODEC instead}.
     */
    static final Codec<IngredientModifier> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_MODIFIER_TYPE
        .byNameCodec()
        .dispatch(IngredientModifier::getType, IngredientModifierType::codec);

    public static final Codec<IngredientModifier> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassIngredientModifier.INSTANCE)));

    public boolean test(ItemStack stack, Level level);

    public void modifyExamples(List<ItemStack> exampleStacks, Level level);

    public void modifyCounterExamples(List<ItemStack> counterExampleStacks, Level level);

    public void addToDescription(List<Component> description, Level level);

    public IngredientModifierType getType();
};
