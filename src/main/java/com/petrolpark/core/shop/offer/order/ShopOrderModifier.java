package com.petrolpark.core.shop.offer.order;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.PassIngredientModifier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class ShopOrderModifier implements LootContextUser {

    public static final Codec<ShopOrderModifier> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            ItemIngredientModifier.CODEC.optionalFieldOf("requirement", PassIngredientModifier.INSTANCE).forGetter(ShopOrderModifier::getIngredientModifier),
            NumberProviders.CODEC.fieldOf("success").forGetter(ShopOrderModifier::getSuccessMultiplier),
            NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(ShopOrderModifier::getFailureNumberProvider)
        ).apply(instance, ShopOrderModifier::new)
    ));
    
    public final IIngredientModifier<? super ItemStack> ingredientModifier;
    public final NumberProvider successMultiplier;
    public final NumberProvider failureMultiplier;

    public ShopOrderModifier(IIngredientModifier<? super ItemStack> ingredientModifier, NumberProvider successMultiplier, NumberProvider failureMultiplier) {
        this.ingredientModifier = ingredientModifier;
        this.successMultiplier = successMultiplier;
        this.failureMultiplier = failureMultiplier;
    };

    public IIngredientModifier<? super ItemStack> getIngredientModifier() {
        return ingredientModifier;
    };

    public NumberProvider getSuccessMultiplier() {
        return successMultiplier;
    };

    public NumberProvider getFailureNumberProvider() {
        return failureMultiplier;
    };

    public List<Component> getDescription(Level level) {
        List<Component> description = new ArrayList<>();
        ingredientModifier.addToDescription(description);
        return description;
    };

    public NumberProvider getMultiplier(ItemStack stack, Level level) {
        if (ingredientModifier.test(stack)) return successMultiplier; else return failureMultiplier;
    };

};
