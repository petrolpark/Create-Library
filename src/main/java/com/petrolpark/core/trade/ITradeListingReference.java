package com.petrolpark.core.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public interface ITradeListingReference {

    /**
     * Use {@link ITradeListingReference#CODEC} instead.
     */
    static Codec<ITradeListingReference> TYPED_CODEC = PetrolparkRegistries.TRADE_LISTING_REFERENCE_TYPES.byNameCodec()
        .dispatch(ITradeListingReference::getType, ITradeListingReference.Type::codec);

    public static Codec<ITradeListingReference> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static ItemListing FAILURE = (e, r) -> null;
    
    public ItemListing get();

    public ITradeListingReference.Type getType();

    public static record Type(MapCodec<? extends ITradeListingReference> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITradeListingReference> streamCodec) {};
};
