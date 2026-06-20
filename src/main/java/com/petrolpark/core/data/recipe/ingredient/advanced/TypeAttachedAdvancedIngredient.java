package com.petrolpark.core.data.recipe.ingredient.advanced;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public record TypeAttachedAdvancedIngredient<STACK, INGREDIENT extends ITypelessAdvancedIngredient<? super STACK>>(INGREDIENT untypedIngredient, IAdvancedIngredientType<STACK> type) implements IAdvancedIngredient<STACK>, IForcingItemAdvancedIngredient {

    @Override
    public boolean test(STACK stack) {
        return untypedIngredient().test(stack);
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return untypedIngredient().modifyExamples(exampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return untypedIngredient().modifyCounterExamples(counterExampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        untypedIngredient().addToDescription(description);
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        untypedIngredient().addToCounterDescription(description);
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (untypedIngredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forceLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (untypedIngredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forbidLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (untypedIngredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forceTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (untypedIngredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forbidTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public IAdvancedIngredientType<? super STACK> getType() {
        return type();
    };

    @Override
    public IAdvancedIngredient<? super STACK> simplify() {
        ITypelessAdvancedIngredient<? super STACK> simplifiedUntypedIngredient = untypedIngredient().simplify();
        if (simplifiedUntypedIngredient == untypedIngredient()) return this;
        return new TypeAttachedAdvancedIngredient<STACK,ITypelessAdvancedIngredient<? super STACK>>(simplifiedUntypedIngredient, type());
    };
    
};
