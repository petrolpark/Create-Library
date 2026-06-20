package com.petrolpark.experimental.trade;

import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.petrolpark.registry.PetrolparkTradeListingReferenceTypes;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public record VillagerTradeListingReference(VillagerProfession profession, int level, int index) implements IVillagerTradeListingReference {

    public static final MapCodec<VillagerTradeListingReference> CODEC = IVillagerTradeListingReference.codec(VillagerTradeListingReference::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, VillagerTradeListingReference> STREAM_CODEC = IVillagerTradeListingReference.streamCodec(VillagerTradeListingReference::new);

    @Override
    public Map<VillagerProfession, Int2ObjectMap<ItemListing[]>> getAllListings() {
        return VillagerTrades.TRADES;
    };

    @Override
    public Type getType() {
        return PetrolparkTradeListingReferenceTypes.VILLAGER.get();
    };
    

};
