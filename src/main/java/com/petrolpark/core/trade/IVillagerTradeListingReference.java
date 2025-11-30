package com.petrolpark.core.trade;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.CodecHelper;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public interface IVillagerTradeListingReference extends ITradeListingReference {

    public static <REF extends IVillagerTradeListingReference> MapCodec<REF> codec(Factory<REF> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.VILLAGER_PROFESSION.byNameCodec().fieldOf("profession").forGetter(IVillagerTradeListingReference::profession),
            Codec.intRange(1, 5).fieldOf("level").forGetter(IVillagerTradeListingReference::level),
            CodecHelper.POS_INT.fieldOf("index").forGetter(IVillagerTradeListingReference::index)
        ).apply(instance, factory::create));
    };

    public static <REF extends IVillagerTradeListingReference> StreamCodec<RegistryFriendlyByteBuf, REF> streamCodec(Factory<REF> factory) {
        return StreamCodec.composite(
            ByteBufCodecs.registry(Registries.VILLAGER_PROFESSION), IVillagerTradeListingReference::profession,
            ByteBufCodecs.INT, IVillagerTradeListingReference::level,
            ByteBufCodecs.INT, IVillagerTradeListingReference::index,
            factory::create
        );
    };

    @Override
    public default ItemListing get() {
        Int2ObjectMap<ItemListing[]> professionListings = getAllListings().get(profession());
        if (professionListings == null) return ITradeListingReference.FAILURE;
        ItemListing[] listings = professionListings.get(level());
        if (index() < listings.length) return listings[index()];
        return FAILURE;
    };

    public Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> getAllListings();
    
    public VillagerProfession profession();

    public int level();

    public int index();

    @FunctionalInterface
    public static interface Factory<REF extends ITradeListingReference> {
        public REF create(VillagerProfession profession, int level, int index);
    };
};
