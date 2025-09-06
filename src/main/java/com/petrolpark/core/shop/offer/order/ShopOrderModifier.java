package com.petrolpark.core.shop.offer.order;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.PassAdvancedIngredient;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

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
            ItemAdvancedIngredient.CODEC.optionalFieldOf("requirement", PassAdvancedIngredient.INSTANCE).forGetter(ShopOrderModifier::getAdvancedIngredient),
            NumberProviders.CODEC.fieldOf("success").forGetter(ShopOrderModifier::getSuccessMultiplier),
            NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(ShopOrderModifier::getFailureNumberProvider)
        ).apply(instance, ShopOrderModifier::new)
    ));
    
    public final IAdvancedIngredient<? super ItemStack> ingredient;
    public final NumberProvider successMultiplier;
    public final NumberProvider failureMultiplier;

    public ShopOrderModifier(IAdvancedIngredient<? super ItemStack> ingredient, NumberProvider successMultiplier, NumberProvider failureMultiplier) {
        this.ingredient = ingredient;
        this.successMultiplier = successMultiplier;
        this.failureMultiplier = failureMultiplier;
    };

    public IAdvancedIngredient<? super ItemStack> getAdvancedIngredient() {
        return ingredient;
    };

    public NumberProvider getSuccessMultiplier() {
        return successMultiplier;
    };

    public NumberProvider getFailureNumberProvider() {
        return failureMultiplier;
    };

    public List<Component> getDescription(Level level) {
        List<Component> description = new ArrayList<>();
        ingredient.addToDescription(new IndentedTooltipBuilder(description));
        return description;
    };

    public NumberProvider getMultiplier(ItemStack stack, Level level) {
        if (ingredient.test(stack)) return successMultiplier; else return failureMultiplier;
    };

};
