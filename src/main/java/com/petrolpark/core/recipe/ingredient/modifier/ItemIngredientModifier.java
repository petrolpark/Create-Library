package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

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

public interface ItemIngredientModifier extends IIngredientModifier<ItemStack>, IForcingItemIngredientModifier {

    /**
     * Use {@link ItemIngredientModifier#CODEC instead}.
     */
    static final Codec<IIngredientModifier<? super ItemStack>> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_MODIFIER_TYPES
        .byNameCodec()
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::codec);

    public static final Codec<IIngredientModifier<? super ItemStack>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassIngredientModifier.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super ItemStack>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE)
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::streamCodec);

    @Override
    public boolean test(ItemStack stack);

    default Component translate(String postfix, Object... translationArgs) {
        return Component.translatable(getType().translationKey() + "." + postfix, translationArgs);
    };

    default Component translateSimple(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return translate("inverse", translationArgs);
    };

    @Override
    public default @Nonnull Optional<ItemStack> forceLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        return Optional.empty();
    };

    @Override
    public default @Nonnull Optional<ItemStack> forbidLootItemFunction(LootItemFunction function, LootContext context, ItemStack stack) {
        return Optional.empty();
    };

    @Override
    public default @Nullable Optional<MerchantOffer> forceTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        return null;
    };

    @Override
    public default @Nullable Optional<MerchantOffer> forbidTradeListing(ItemListing tradeListing, Entity trader, RandomSource random) {
        return null;
    };

    @Override
    public INamedIngredientModifierType<ItemStack> getType();
};
