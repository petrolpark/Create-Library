package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public record ContaminatedIngredientModifier(Holder<Contaminant> contaminant) implements IIngredientModifier<MutableDataComponentHolder>, IForcingItemIngredientModifier {

    public static final MapCodec<ContaminatedIngredientModifier> CODEC = CodecHelper.singleFieldMap(Contaminant.CODEC, "contaminant", ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ContaminatedIngredientModifier> STREAM_CODEC = StreamCodec.composite(Contaminant.STREAM_CODEC, ContaminatedIngredientModifier::contaminant, ContaminatedIngredientModifier::new);
    public static final IIngredientModifierType<MutableDataComponentHolder> TYPE = new Type();

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        return IContamination.get(stack).map(contamination -> contamination.has(contaminant)).orElse(false);
    };

    @Override
    public Stream<MutableDataComponentHolder> modifyExamples(Stream<MutableDataComponentHolder> exampleStacks) {
        return exampleStacks.map(stack -> {
            IContamination.get(stack).ifPresent(c -> c.contaminate(contaminant));
            return stack;
        });
    };

    @Override
    public Stream<MutableDataComponentHolder> modifyCounterExamples(Stream<MutableDataComponentHolder> counterExampleStacks) {
        return counterExampleStacks.map(stack -> {
            IContamination.get(stack).ifPresent(c -> c.decontaminateOnly(contaminant));
            return stack;
        });
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(Contaminant.getNameColored(contaminant));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(Contaminant.getAbsentNameColored(contaminant));
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        // TODO Auto-generated method stub
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        // TODO Auto-generated method stub
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        // TODO Auto-generated method stub
        return null;
    };

    @Override
    public IIngredientModifierType<MutableDataComponentHolder> getType() {
        return TYPE;
    };

    public static final class Type implements IIngredientModifierType<MutableDataComponentHolder> {

        @Override
        public MapCodec<ContaminatedIngredientModifier> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ContaminatedIngredientModifier> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<ContaminatedIngredientModifier> streamApplicableModifiers(Level level, MutableDataComponentHolder stack) {
            return IContamination.get(stack).stream()
                .flatMap(IContamination::streamAllContaminants)
                .map(ContaminatedIngredientModifier::new);
        };

    };
    
};
