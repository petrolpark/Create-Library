package com.petrolpark.core.data.predicate.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;

public record AdvancedIngredientItemSubPredicate(IAdvancedIngredient<? super ItemStack> ingredient) implements ItemSubPredicate {

    public static final Codec<AdvancedIngredientItemSubPredicate> CODEC = CodecHelper.singleField(ItemAdvancedIngredient.CODEC, "ingredient", AdvancedIngredientItemSubPredicate::ingredient, AdvancedIngredientItemSubPredicate::new);

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        return ingredient().test(stack);
    };
    
};
