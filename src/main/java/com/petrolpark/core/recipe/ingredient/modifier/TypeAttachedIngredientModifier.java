package com.petrolpark.core.recipe.ingredient.modifier;

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

public record TypeAttachedIngredientModifier<STACK, MODIFIER extends ITypelessIngredientModifier<? super STACK>>(MODIFIER untypedModifier, IIngredientModifierType<STACK> type) implements IIngredientModifier<STACK>, IForcingItemIngredientModifier {

    @Override
    public boolean test(STACK stack) {
        return untypedModifier().test(stack);
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return untypedModifier().modifyExamples(exampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return untypedModifier().modifyCounterExamples(counterExampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        untypedModifier().addToDescription(description);
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        untypedModifier().addToCounterDescription(description);
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (untypedModifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forceLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (untypedModifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forbidLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (untypedModifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forceTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (untypedModifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forbidTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public IIngredientModifierType<? super STACK> getType() {
        return type();
    };

    @Override
    public IIngredientModifier<? super STACK> simplify() {
        ITypelessIngredientModifier<? super STACK> simplifiedUntypedModifier = untypedModifier().simplify();
        if (simplifiedUntypedModifier == untypedModifier()) return this;
        return new TypeAttachedIngredientModifier<STACK,ITypelessIngredientModifier<? super STACK>>(simplifiedUntypedModifier, type());
    };
    
};
