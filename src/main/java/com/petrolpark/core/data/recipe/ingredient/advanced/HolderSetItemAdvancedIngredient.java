package com.petrolpark.core.data.recipe.ingredient.advanced;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.registry.PetrolparkAdvancedIngredientTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public record HolderSetItemAdvancedIngredient(HolderSet<Item> set) implements ItemAdvancedIngredient {

    public static final MapCodec<HolderSetItemAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(RegistryCodecs.homogeneousList(Registries.ITEM), "set", HolderSetItemAdvancedIngredient::set, HolderSetItemAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, HolderSetItemAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.ITEM), HolderSetItemAdvancedIngredient::set, HolderSetItemAdvancedIngredient::new);

    @Override
    public boolean test(ItemStack stack) {
        return set.contains(stack.getItemHolder());
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        set.unwrap().map(
            tag -> description.add(Component.translatable("advancedIngredient.petrolpark.tag", tag)),
            items -> {
                description.add(translate("")).indent();
                items.stream().map(Holder::value).map(Item::getDescription).forEach(description::add);
                return description.unindent();
            }
        );
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        set.unwrap().map(
            tag -> description.add(Component.translatable("advancedIngredient.petrolpark.tag.inverse", tag)),
            items -> {
                description.add(translateInverse()).indent();
                items.stream().map(Holder::value).map(Item::getDescription).forEach(description::add);
                return description.unindent();
            }
        );
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
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkAdvancedIngredientTypes.ITEM_HOLDER_SET.get();
    };
    
};
