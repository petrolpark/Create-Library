package com.petrolpark.core.trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkTradeListingReferenceTypes;
import com.petrolpark.util.CodecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;

public record ExperimentalWanderingTraderTradeListingReference(int group, int index) implements ITradeListingReference {

    public static final MapCodec<ExperimentalWanderingTraderTradeListingReference> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        CodecHelper.POS_INT.fieldOf("group").forGetter(ExperimentalWanderingTraderTradeListingReference::group),
        CodecHelper.POS_INT.fieldOf("index").forGetter(ExperimentalWanderingTraderTradeListingReference::index)
    ).apply(instance, ExperimentalWanderingTraderTradeListingReference::new));

    public static final StreamCodec<ByteBuf, ExperimentalWanderingTraderTradeListingReference> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ExperimentalWanderingTraderTradeListingReference::group,
        ByteBufCodecs.INT, ExperimentalWanderingTraderTradeListingReference::index,
        ExperimentalWanderingTraderTradeListingReference::new
    );

    @Override
    public ItemListing get() {
        if (group >= VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES.size()) return FAILURE;
        ItemListing[] listings = VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES.get(group).getLeft();
        if (index() < listings.length) return listings[index];
        return FAILURE;
    };

    @Override
    public Type getType() {
        return PetrolparkTradeListingReferenceTypes.EXPERIMENTAL_WANDERING_TRADER.get();
    };
    
};
