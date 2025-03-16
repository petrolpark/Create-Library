package com.petrolpark.shop.offer.order;

import java.util.List;
import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifier;
import com.petrolpark.recipe.ingredient.modifier.PassIngredientModifier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class ShopOrderModifier implements LootContextUser {

    public static final Codec<ShopOrderModifier> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            IngredientModifier.CODEC.optionalFieldOf("requirement", PassIngredientModifier.INSTANCE).forGetter(ShopOrderModifier::getIngredientModifier),
            NumberProviders.CODEC.fieldOf("success").forGetter(ShopOrderModifier::getSuccessMultiplier),
            NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(ShopOrderModifier::getFailureNumberProvider)
        ).apply(instance, ShopOrderModifier::new)
    );
    
    public final IngredientModifier ingredientModifier;
    public final NumberProvider successMultiplier;
    public final NumberProvider failureMultiplier;

    public ShopOrderModifier(IngredientModifier ingredientModifier, NumberProvider successMultiplier, NumberProvider failureMultiplier) {
        this.ingredientModifier = ingredientModifier;
        this.successMultiplier = successMultiplier;
        this.failureMultiplier = failureMultiplier;
    };

    public IngredientModifier getIngredientModifier() {
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
        ingredientModifier.addToDescription(description, level);
        return description;
    };

    public NumberProvider getMultiplier(ItemStack stack, Level level) {
        if (ingredientModifier.test(stack, level)) return successMultiplier; else return failureMultiplier;
    };

};
