package com.petrolpark.core.recipe.ingredient.advanced;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkAdvancedIngredientTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.common.CommonHooks;

public record EnchantmentItemAdvancedIngredient(EnchantmentPredicate enchantments) implements ItemAdvancedIngredient {

    public static final MapCodec<EnchantmentItemAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(EnchantmentPredicate.CODEC, "enchantments", EnchantmentItemAdvancedIngredient::enchantments, EnchantmentItemAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentItemAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(CodecHelper.ENCHANTMENT_PREDICATE_STREAM_CODEC, EnchantmentItemAdvancedIngredient::enchantments, EnchantmentItemAdvancedIngredient::new);

    public static final RandomSource RANDOM = new XoroshiroRandomSource(123456789l);

    @Override
    public boolean test(ItemStack stack) {
        return enchantments().containedIn(getEnchantments(stack));
    };

    @Override
    @Nullable
    public Stream<ItemStack> modifyExamples(Stream<ItemStack> exampleStacks) {
        return enchantments().enchantments().map(enchantments ->
            exampleStacks.flatMap(stack -> enchantments.stream()
                .map(enchantment -> {
                    if (enchantments().level().matches(stack.getEnchantmentLevel(enchantment))) return null;
                    ItemStack copy = stack.copy();
                    copy.enchant(enchantment, enchantments().level().min().orElse(1));
                    return copy;
                })
            )
        ).orElse(exampleStacks);
    };

    @Override
    @Nullable
    public Stream<ItemStack> modifyCounterExamples(Stream<ItemStack> counterExampleStacks) {
        return counterExampleStacks.map(stack -> {
            ItemEnchantments.Mutable mutableItemEnchantments = new ItemEnchantments.Mutable(getEnchantments(stack));
            enchantments().enchantments().ifPresentOrElse(enchantments -> {
                enchantments.forEach(enchantment -> mutableItemEnchantments.set(enchantment, enchantments().level().min().orElse(0)));
            }, () -> {
                mutableItemEnchantments.keySet().forEach(enchantment -> mutableItemEnchantments.set(enchantment, enchantments().level().min().orElse(0)));
            });
            stack.set(DataComponents.ENCHANTMENTS, mutableItemEnchantments.toImmutable());
            return stack;
        });
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        Stream<Holder<Enchantment>> possibleEnchantments;
        Function<Holder<Enchantment>, Float> maxLevel;

        if (function instanceof EnchantRandomlyFunction enchantRandomlyFunction) {
            possibleEnchantments = enchantRandomlyFunction.options.map(HolderSet::stream)
                .orElseGet(allEnchantments(context))
                .filter(enchantment -> !enchantRandomlyFunction.onlyCompatible || stack.is(Items.BOOK) || stack.supportsEnchantment(enchantment));
            maxLevel = enchantment -> (float)enchantment.value().getMaxLevel();
        } else if (function instanceof EnchantWithLevelsFunction enchantWithLevelsFunction) {
            possibleEnchantments = enchantWithLevelsFunction.options.map(HolderSet::stream)
                .orElseGet(allEnchantments(context));
            maxLevel = enchantment -> NumberEstimate.getMax(context, enchantWithLevelsFunction.levels);
        } else return Optional.empty();

        for (Holder<Enchantment> enchantment : possibleEnchantments.filter(e -> enchantments().enchantments().map(enchantments -> enchantments.contains(e)).orElse(true)).toList()) {
            int minLevel = enchantments().level().min().orElse(1);
            if (minLevel <= maxLevel.apply(enchantment)) {
                stack.enchant(enchantment, minLevel);
                return Optional.of(stack);
            };
        };

        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        // TODO Auto-generated method stub
        return ItemAdvancedIngredient.super.forbidLootItemFunction(function, context, stack);
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return ItemAdvancedIngredient.super.forceTradeListing(tradeListing, trader, random);
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return ItemAdvancedIngredient.super.forbidTradeListing(tradeListing, trader, random);
    };
    
    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        addToDescriptionInternal(description, "");
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        addToDescriptionInternal(description, ".inverse");
    };

    protected void addToDescriptionInternal(IndentedTooltipBuilder description, String postfix) {
        boolean ranged = !enchantments().level().isAny();
        Component range = Lang.range(enchantments().level().min().map(i -> (float)i).orElse(Float.NaN), enchantments().level().min().map(i -> (float)i).orElse(Float.NaN), Lang.INT_DF);
        if (enchantments().enchantments().isPresent()) {
            HolderSet<Enchantment> enchantments = enchantments().enchantments().get();
            if (enchantments.size() == 1) {
                Component name = enchantments.get(0).value().description();
                if (ranged) description.add(translate("single.level_range" + postfix, name, range));
                else description.add(translate("single" + postfix, name));
            } else {
                if (ranged) description.add(translate("any.level_range" + postfix, range));
                else description.add(translate("any" + postfix));
                description.indent();
                enchantments.stream().map(Holder::value).map(Enchantment::description).forEach(description::add);
                description.unindent();
            };
        } else {
            if (ranged) description.add(translate("level_range" + postfix, range));
            else description.add(translate(postfix));
        };
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkAdvancedIngredientTypes.ITEM_ENCHANTMENTS.get();
    };

    protected static final Supplier<Stream<Holder<Enchantment>>> allEnchantments(LootContext context) {
        return () -> context.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).holders().map(Function.identity());
    };

    protected static final ItemEnchantments getEnchantments(ItemStack stack) {
        return stack.getAllEnchantments(CommonHooks.resolveLookup(Registries.ENCHANTMENT));
    };

    public static record Type(String translationKey) implements INamedAdvancedIngredientType<ItemStack> {

        @Override
        public MapCodec<EnchantmentItemAdvancedIngredient> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantmentItemAdvancedIngredient> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<EnchantmentItemAdvancedIngredient> streamApplicableIngredients(Level level, ItemStack stack) {
            if (stack.isEnchanted()) {
                return Stream.concat(
                    stack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).entrySet().stream()
                        .flatMap(entry -> Stream.of(
                            new EnchantmentItemAdvancedIngredient(new EnchantmentPredicate(entry.getKey(), MinMaxBounds.Ints.ANY)), // Has each Enchantment
                            new EnchantmentItemAdvancedIngredient(new EnchantmentPredicate(entry.getKey(), MinMaxBounds.Ints.atLeast(entry.getIntValue()))) // Has each Enchantment at the required level
                        )),
                    Stream.of(new EnchantmentItemAdvancedIngredient(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY))) // Has any Enchantment
                );
            } else return Stream.empty();
        };

    };
    
};
