package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import petrolpark.mc.library.registry.PetrolparkAdvancedIngredientTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

public record NotAdvancedIngredient<STACK>(IAdvancedIngredient<STACK> ingredient) implements ITypelessAdvancedIngredient<STACK>, IForcingItemAdvancedIngredient {

    public static final <STACK> MapCodec<NotAdvancedIngredient<STACK>> codec(Codec<IAdvancedIngredient<STACK>> typeCodec) {
        return CodecHelper.singleFieldMap(typeCodec, "ingredient", NotAdvancedIngredient::ingredient, NotAdvancedIngredient::new);
    };

    public static final <STACK> StreamCodec<? super RegistryFriendlyByteBuf, NotAdvancedIngredient<STACK>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<STACK>> typeStreamCodec) {
        return StreamCodec.composite(typeStreamCodec, NotAdvancedIngredient::ingredient, NotAdvancedIngredient::new);
    };

    public static final IAdvancedIngredient<ItemStack> of(IAdvancedIngredient<ItemStack> ingredient) {
        return PetrolparkAdvancedIngredientTypes.ITEM_NOT.get().create(new NotAdvancedIngredient<>(ingredient));
    };
    
    @Override
    public boolean test(STACK stack) {
        return !ingredient().test(stack);
    };

    @Override
    public Stream<STACK> streamExamples() {
        return ingredient().streamCounterExamples().map(this::checkedCast);
    };

    @Override
    public Stream<STACK> streamCounterExamples() {
        return ingredient().streamExamples().map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyExamples(Stream<STACK> exampleStacks) {
        return ingredient().modifyCounterExamples(exampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public Stream<STACK> modifyCounterExamples(Stream<STACK> counterExampleStacks) {
        return ingredient().modifyExamples(counterExampleStacks.map(this::checkedCast)).map(this::checkedCast);
    };

    @Override
    public @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (ingredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forbidLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        if (ingredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forceLootItemFunction(function, context, stack);
        return Optional.empty();
    };

    @Override
    public @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (ingredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forceTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        if (ingredient() instanceof IForcingItemAdvancedIngredient functionForcingIngredient) return functionForcingIngredient.forbidTradeListing(tradeListing, trader, random);
        return null;
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        ingredient().addToCounterDescription(description);
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        ingredient().addToDescription(description);
    };

    @Override
    @SuppressWarnings("unchecked")
    public ITypelessAdvancedIngredient<STACK> simplify() {
        if (ingredient() instanceof TypeAttachedAdvancedIngredient ingredient
            && ingredient.untypedIngredient() instanceof NotAdvancedIngredient notIngredient
        ) return ((NotAdvancedIngredient<STACK>)notIngredient).ingredient().simplify();
        IAdvancedIngredient<STACK> simplifiedIngredient = ingredient().simplify();
        if (simplifiedIngredient != ingredient()) return new NotAdvancedIngredient<>(simplifiedIngredient);
        return this;
    };
    
};
