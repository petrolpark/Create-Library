package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.trade.ExperimentalVillagerTradeListingReference;
import com.petrolpark.core.trade.ExperimentalWanderingTraderTradeListingReference;
import com.petrolpark.core.trade.ITradeListingReference;
import com.petrolpark.core.trade.VillagerTradeListingReference;
import com.petrolpark.core.trade.WanderingTraderTradeListingReference;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkTradeListingReferenceTypes {
    
    public static final RegistryEntry<ITradeListingReference.Type, ITradeListingReference.Type>

    VILLAGER = REGISTRATE.tradeListingReferenceType("villager", VillagerTradeListingReference.CODEC, VillagerTradeListingReference.STREAM_CODEC),
    WANDERING_TRADER = REGISTRATE.tradeListingReferenceType("wandering_trader", WanderingTraderTradeListingReference.CODEC, WanderingTraderTradeListingReference.STREAM_CODEC),
    EXPERIMENTAL_VILLAGER = REGISTRATE.tradeListingReferenceType("experimental_villager", ExperimentalVillagerTradeListingReference.CODEC, ExperimentalVillagerTradeListingReference.STREAM_CODEC),
    EXPERIMENTAL_WANDERING_TRADER = REGISTRATE.tradeListingReferenceType("experimental_wandering_trader", ExperimentalWanderingTraderTradeListingReference.CODEC, ExperimentalWanderingTraderTradeListingReference.STREAM_CODEC);

    public static final void register() {};
};