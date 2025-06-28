package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientModifierTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public record CompoundIngredientModifier<STACK>(List<IIngredientModifier<? super STACK>> modifiers, int required) implements ITypelessIngredientModifier<STACK>, IForcingItemIngredientModifier {

    public static final <STACK> MapCodec<CompoundIngredientModifier<STACK>> codec(Codec<IIngredientModifier<? super STACK>> typeCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            typeCodec.listOf().fieldOf("modifiers").forGetter(CompoundIngredientModifier::modifiers),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("proportion", 1).forGetter(CompoundIngredientModifier::required)
        ).apply(instance, CompoundIngredientModifier::new))
        //.validate(CompoundIngredientModifier::validate)
        ;
    };

    public static final <STACK> StreamCodec<RegistryFriendlyByteBuf, CompoundIngredientModifier<STACK>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super STACK>> typeStreamCodec) {
        return StreamCodec.composite(
            typeStreamCodec.apply(ByteBufCodecs.list()), CompoundIngredientModifier::modifiers,
            ByteBufCodecs.INT, CompoundIngredientModifier::required,
            CompoundIngredientModifier::new
        );
    };

    protected static final <STACK> CompoundIngredientModifier<STACK> typelessAnd(List<IIngredientModifier<? super STACK>> modifiers) {
        return new CompoundIngredientModifier<>(modifiers, modifiers.size());
    };

    protected static final <STACK> CompoundIngredientModifier<STACK> typelessOr(List<IIngredientModifier<? super STACK>> modifiers) {
        return new CompoundIngredientModifier<>(modifiers, 1);
    };

    public static final IIngredientModifier<ItemStack> and(List<IIngredientModifier<? super ItemStack>> modifiers) {
        return PetrolparkIngredientModifierTypes.ITEM_COMPOUND.get().create(typelessAnd(modifiers));
    };

    public static final IIngredientModifier<ItemStack> or(List<IIngredientModifier<? super ItemStack>> modifiers) {
        return PetrolparkIngredientModifierTypes.ITEM_COMPOUND.get().create(typelessOr(modifiers));
    };

    @Override
    public boolean test(STACK stack) {
        if (isImpossible()) return false;
        int fulfilled = 0;
        int unfulfilled = 0;
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            if (modifier.test(stack)) fulfilled++; else unfulfilled++;
            if (unfulfilled >= modifiers().size() - required()) return false;
            if (fulfilled >= required) return true;
        };
        return false;
    };

    @Override
    public Stream<? extends STACK> streamExamples() {
        if (isImpossible()) return Stream.empty();
        Stream<STACK> stream = modifiers().stream()
            .flatMap(IIngredientModifier::streamExamples)
            .map(this::checkedCast);
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            stream = modifyExamples(stream, modifier);
        };
        return stream.filter(this::test);
    };

    @Override
    public Stream<? extends STACK> streamCounterExamples() {
        if (isImpossible()) return Stream.empty();
        Stream<STACK> stream = modifiers().stream()
            .flatMap(IIngredientModifier::streamCounterExamples)
            .map(this::checkedCast);
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            stream = modifyCounterExamples(stream, modifier);
        };
        return stream.filter(Predicate.not(this::test));
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) { // Not quite perfect but these are only examples
        if (isImpossible()) return Stream.empty();
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            exampleStacks = modifyExamples(exampleStacks, modifier);
        }; 
        return exampleStacks;
    };

    protected <STACK_PARENT> Stream<STACK> modifyExamples(Stream<STACK> exampleStacks, IIngredientModifier<STACK_PARENT> modifier) {
        return modifier.modifyExamples(exampleStacks.map(modifier::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        if (isImpossible()) return counterExampleStacks;
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            counterExampleStacks = modifyCounterExamples(counterExampleStacks, modifier);
        }; 
        return counterExampleStacks;
    };

    protected <STACK_PARENT> Stream<STACK> modifyCounterExamples(Stream<STACK> exampleStacks, IIngredientModifier<STACK_PARENT> modifier) {
        return modifier.modifyCounterExamples(exampleStacks.map(modifier::checkedCast)).map(this::checkedCast);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        if (isOr()) description.add(translate("any"));
        else if (isAnd()) description.add(translate("all"));
        else description.add(translate("at_least", required()));
        description.indent();
        modifiers().forEach(modifier -> modifier.addToDescription(description));
        description.unindent();
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        if (isOr()) description.add(translate("none"));
        else description.add(translate("at_most", required() - 1));
        description.indent();
        modifiers().forEach(modifier -> modifier.addToDescription(description));
        description.unindent();
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        boolean forced = false;
        ItemStack forcedStack = stack;
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            if (modifier instanceof IForcingItemIngredientModifier functionForcingModifier) {
                Optional<ItemStack> forcedStackOp = functionForcingModifier.forceLootItemFunction(function, context, forcedStack);
                if (forcedStackOp.isPresent()) {
                    forced = true;
                    forcedStack = forcedStackOp.get();
                };
            };
        };
        return forced ? Optional.of(forcedStack) : Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        boolean forbidden = false;
        ItemStack forbiddenStack = stack;
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            if (modifier instanceof IForcingItemIngredientModifier functionForcingModifier) {
                Optional<ItemStack> forbiddenStackOp = functionForcingModifier.forbidLootItemFunction(function, context, forbiddenStack);
                if (forbiddenStackOp.isPresent()) {
                    forbidden = true;
                    forbiddenStack = forbiddenStackOp.get();
                };
            };
        };
        return forbidden ? Optional.of(forbiddenStack) : Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            if (modifier instanceof IForcingItemIngredientModifier functionForcingModifier) {
                Optional<MerchantOffer> forcedOffer = functionForcingModifier.forceTradeListing(tradeListing, trader, random);
                if (forcedOffer != null) return forcedOffer;
            };
        };
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        for (IIngredientModifier<? super STACK> modifier : modifiers()) {
            if (modifier instanceof IForcingItemIngredientModifier functionForcingModifier) {
                Optional<MerchantOffer> forbiddenOffer = functionForcingModifier.forbidTradeListing(tradeListing, trader, random);
                if (forbiddenOffer != null) return forbiddenOffer;
            };
        };
        return null;
    };

    @Override
    public ITypelessIngredientModifier<? super STACK> simplify() {
        if (modifiers().size() == 1) return modifiers().get(0).simplify();
        modifiers().replaceAll(IIngredientModifier::simplify);
        if (isAnd() || isOr()) {
            Iterator<IIngredientModifier<? super STACK>> iterator = modifiers().iterator();
            while (iterator.hasNext()) {
                cast(iterator.next()).ifPresent(compoundModifier -> {
                    if (compoundModifier.isAnd() == isAnd() || compoundModifier.isOr() == isOr()) {
                        modifiers().addAll(compoundModifier.modifiers());
                        iterator.remove();
                    };
                });
            };
        };
        return this;
    };

    public boolean isImpossible() {
        return required() > modifiers().size();
    };

    public boolean isOr() {
        return required() == 1;
    };

    public boolean isAnd() {
        return required() == modifiers().size();
    };

    @SuppressWarnings("unchecked")
    protected Optional<CompoundIngredientModifier<? super STACK>> cast(IIngredientModifier<? super STACK> modifier) {
        if (modifier instanceof TypeAttachedIngredientModifier typedModifier && typedModifier.untypedModifier() instanceof CompoundIngredientModifier compoundModifier) return Optional.of((CompoundIngredientModifier<? super STACK>)compoundModifier);
        return Optional.empty();
    };

    protected Component translate(String keyPostfix, Object... args) {
        return Component.translatable("petrolpark.ingredient_modifier.compound."+keyPostfix, args);
    };

    // Doesn't compile for some reason
    // public DataResult<CompoundIngredientModifier<STACK>> validate() {
    //     if (required() > modifiers().size()) return DataResult.error(() -> "Number of required modifiers cannot be greater than number of modifiers");
    //     return DataResult.success(this);
    // };
    
};
