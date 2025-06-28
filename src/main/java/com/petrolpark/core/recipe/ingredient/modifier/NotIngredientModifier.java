package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkIngredientModifierTypes;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public record NotIngredientModifier<STACK>(IIngredientModifier<? super STACK> modifier) implements ITypelessIngredientModifier<STACK>, IForcingItemIngredientModifier {

    public static final <STACK> MapCodec<NotIngredientModifier<STACK>> codec(Codec<IIngredientModifier<? super STACK>> typeCodec) {
        return CodecHelper.singleFieldMap(typeCodec, "modifier", NotIngredientModifier::modifier, NotIngredientModifier::new);
    };

    public static final <STACK> StreamCodec<? super RegistryFriendlyByteBuf, NotIngredientModifier<STACK>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super STACK>> typeStreamCodec) {
        return StreamCodec.composite(typeStreamCodec, NotIngredientModifier::modifier, NotIngredientModifier::new);
    };

    public static final IIngredientModifier<ItemStack> of(IIngredientModifier<? super ItemStack> modifier) {
        return PetrolparkIngredientModifierTypes.ITEM_NOT.get().create(new NotIngredientModifier<>(modifier));
    };
    
    @Override
    public boolean test(STACK stack) {
        return !modifier().test(stack);
    };

    @Override
    public Stream<STACK> streamExamples() {
        return modifier().streamCounterExamples().map(this::checkedCast);
    };

    @Override
    public Stream<STACK> streamCounterExamples() {
        return modifier().streamExamples().map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return modifier().modifyCounterExamples(exampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return modifier().modifyExamples(counterExampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (modifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forbidLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (modifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forceLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (modifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forceTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (modifier() instanceof IForcingItemIngredientModifier functionForcingModifier) return functionForcingModifier.forbidTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        modifier().addToCounterDescription(description);
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        modifier().addToDescription(description);
    };

    @Override
    @SuppressWarnings("unchecked")
    public ITypelessIngredientModifier<? super STACK> simplify() {
        if (modifier() instanceof TypeAttachedIngredientModifier modifier
            && modifier.untypedModifier() instanceof NotIngredientModifier notModifier
        ) return ((NotIngredientModifier<STACK>)notModifier).modifier().simplify();
        IIngredientModifier<? super STACK> simplifiedModifier = modifier().simplify();
        if (simplifiedModifier != modifier()) return new NotIngredientModifier<>(simplifiedModifier);
        return this;
    };
    
};
